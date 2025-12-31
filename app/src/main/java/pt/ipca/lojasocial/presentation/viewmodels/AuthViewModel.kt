package pt.ipca.lojasocial.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.models.RequestCategory
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.repository.AuthRepository
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.domain.repository.StorageRepository
import pt.ipca.lojasocial.domain.use_cases.auth.RegisterBeneficiaryUseCase
import pt.ipca.lojasocial.presentation.state.AuthState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerBeneficiaryUseCase: RegisterBeneficiaryUseCase,
    private val authRepository: AuthRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val requestRepository: RequestRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // --- INIT: Verificar se já existe user logado ao abrir a app ---
    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                // Se existe user, carregamos logo o perfil para definir a navegação
                _state.update { it.copy(userId = currentUser.id) }
                loadUserProfile(currentUser.id)
                _state.update { it.copy(isLoggedIn = true) }
            }
        }
    }

    // ==========================================================
    // --- LÓGICA DE LOGIN & CARREGAMENTO DE PERFIL ---
    // ==========================================================

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.login(email, pass)

            result.onSuccess { user ->
                // Login Auth funcionou, agora carregamos os dados do Firestore
                // O loadUserProfile vai preencher a Role, Status, Nome, etc.
                loadUserProfile(user.id)

                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userId = user.id
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Erro ao fazer login."
                    )
                }
            }
        }
    }

    /**
     * Esta é a função principal que determina QUEM é o utilizador.
     * Preenche a Role (Colaborador/Beneficiário) e os Status.
     */
    private suspend fun loadUserProfile(userId: String) {
        try {
            // 1. Verificar Role (Staff vs Beneficiário)
            // Nota: Se não tiveres o método getUserRole no repo, usa a lógica de tentar ler as coleções
            val role = authRepository.getUserRole(userId)


            if (role == "colaborador") {
                _state.update {
                    it.copy(
                        userRole = "colaborador",
                        fullName = "Colaborador",
                        beneficiaryStatus = BeneficiaryStatus.ATIVO // Colaborador é sempre ativo
                    )
                }
            } else if (role == "beneficiario") {
                // 2. É Beneficiário: Carregar dados pessoais
                val beneficiary = beneficiaryRepository.getBeneficiaryById(userId)

                // 3. Carregar Requerimentos (para saber o estado do pedido e observações)
                val requests = requestRepository.getRequestsByBeneficiary(userId)
                val latestRequest = requests.maxByOrNull { it.submissionDate }

                _state.update {
                    it.copy(
                        userRole = "beneficiario",
                        fullName = beneficiary?.name ?: "Sem Nome",
                        studentNumber = beneficiary?.id ?: "",
                        email = beneficiary?.email ?: "",

                        // Estado da conta do aluno
                        beneficiaryStatus = beneficiary?.status ?: BeneficiaryStatus.ANALISE,

                        // Estado do requerimento (Aprovado, Rejeitado, etc)
                        requestStatus = latestRequest?.status ?: StatusType.PENDENTE,
                        requestObservations = latestRequest?.observations ?: "",

                        requestDocuments = latestRequest?.documents ?: emptyMap()
                    )
                }
            } else {
                // User autenticado mas sem registo na BD
                throw Exception("Utilizador sem perfil associado.")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Opcional: Logout se os dados estiverem corrompidos
            // authRepository.signOut()
            _state.update { it.copy(errorMessage = "Erro ao carregar perfil: ${e.message}") }
        }
    }

    fun resubmitDocument(docKey: String, uri: Uri) {
        val userId = _state.value.userId ?: return
        val currentDocs = _state.value.requestDocuments

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // 1. Obter o pedido atual para ter o ID
                val requests = requestRepository.getRequestsByBeneficiary(userId)
                val request = requests.maxByOrNull { it.submissionDate } ?: return@launch

                // 2. Upload do novo ficheiro para o Storage
                val fileName = "requerimentos/$userId/${docKey}_reenvio_${java.util.UUID.randomUUID()}"
                val downloadUrl = storageRepository.uploadFile(uri, fileName)

                // 3. Atualizar o mapa localmente
                val updatedDocs = currentDocs.toMutableMap()
                updatedDocs[docKey] = downloadUrl // Agora este documento deixa de ser null

                // 4. VERIFICAÇÃO INTELIGENTE:
                // Verifica se AINDA existe algum valor null ou vazio no mapa inteiro.
                // Se 'none' (nenhum) for nulo, significa que estão todos completos.
                val allDocumentsCompleted = updatedDocs.values.none { it.isNullOrBlank() }

                // Define o novo estado com base na verificação
                val newStatus = if (allDocumentsCompleted) {
                    StatusType.ANALISE // Tudo entregue -> Vai para análise
                } else {
                    StatusType.DOCS_INCORRETOS // Ainda faltam coisas -> Mantém-se no ecrã de erro
                }

                // 5. Atualizar na BD (Firebase)
                requestRepository.updateRequestDocsAndStatus(
                    id = request.id,
                    documents = updatedDocs,
                    status = newStatus
                )

                // 6. Atualizar a UI
                _state.update {
                    it.copy(
                        isLoading = false,
                        requestDocuments = updatedDocs,
                        requestStatus = newStatus
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao enviar documento. Tente novamente."
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            // Reset total do estado para evitar lixo de memória
            _state.value = AuthState()
        }
    }

    fun resetState() {
        _state.update { it.copy(isSuccess = false, errorMessage = null) }
    }

    // ==========================================================
    // --- LÓGICA DE REGISTO (STEPS 1, 2, 3) ---
    // ==========================================================

    fun isStep1Valid(): Boolean {
        val s = _state.value
        return s.fullName.isNotBlank() && s.cc.length >= 8 && s.birthDate.isNotBlank() &&
                s.phone.length >= 9 && s.email.contains("@") && s.password.length >= 6
    }

    fun isStep2Valid(): Boolean {
        val s = _state.value
        return s.requestCategory != null && s.educationLevel.isNotBlank() &&
                s.school.isNotBlank() && s.studentNumber.isNotBlank()
    }

    fun isStep3Valid(): Boolean {
        val s = _state.value
        return s.docIdentification != null && s.docMorada != null
    }

    fun updateStep1(fullName: String, cc: String, phone: String, email: String, password: String) {
        _state.update { it.copy(fullName = fullName, cc = cc, phone = phone, email = email, password = password) }
    }

    fun updateBirthDate(date: String) {
        _state.update { it.copy(birthDate = date) }
    }

    fun updateStep2(category: RequestCategory?, education: String, dependents: Int, school: String, courseName: String, studentNumber: String) {
        _state.update { it.copy(requestCategory = category, educationLevel = education, dependents = dependents, school = school, courseName = courseName, studentNumber = studentNumber) }
    }

    fun updateStep3(docIdentification: Uri?, docFamily: Uri?, docMorada: Uri?, docRendimento: Uri?, docMatricula: Uri?) {
        _state.update { it.copy(docIdentification = docIdentification, docFamily = docFamily, docMorada = docMorada, docRendimento = docRendimento, docMatricula = docMatricula) }
    }

    fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // UseCase regista Auth + Firestore + Storage
                registerBeneficiaryUseCase(_state.value)

                // Após registo, carregamos o perfil para o utilizador entrar logo
                // O ID será o que definimos no registerBeneficiaryUseCase (normalmente authRepository.getCurrentUser()?.uid)
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    loadUserProfile(currentUser.id)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        isLoggedIn = true,
                        userRole = "beneficiario",
                        beneficiaryStatus = BeneficiaryStatus.ANALISE // Default pós-registo
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Erro desconhecido.") }
                e.printStackTrace()
            }
        }
    }
}