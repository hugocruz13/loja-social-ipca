package pt.ipca.lojasocial.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.models.EmailRequest
import pt.ipca.lojasocial.domain.models.RequestCategory
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.repository.AuthRepository
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.CommunicationRepository
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
    private val requestRepository: RequestRepository, // O repositório que agora devolve Flow
    private val storageRepository: StorageRepository,
    private val communicationRepository: CommunicationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // Variável para controlar o "listener" do Firestore e evitar duplicados
    private var requestsJob: Job? = null

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                updateFcmToken(currentUser.id)
                _state.update { it.copy(userId = currentUser.id) }

                // Carrega perfil e inicia a escuta em tempo real
                loadUserProfile(currentUser.id)

                _state.update { it.copy(isLoggedIn = true) }
            }
        }
    }

    private fun updateFcmToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            viewModelScope.launch {
                communicationRepository.saveFcmToken(userId, token)
            }
        }
    }

    // ==========================================================
    // --- LOGIN & CARREGAMENTO DE PERFIL (ALTERADO PARA TEMPO REAL) ---
    // ==========================================================

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(email, pass)

            result.onSuccess { user ->
                updateFcmToken(user.id)
                loadUserProfile(user.id) // Inicia a escuta
                _state.update { it.copy(isLoading = false, isLoggedIn = true, userId = user.id) }
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
     * Esta função agora faz duas coisas:
     * 1. Carrega dados estáticos do Beneficiário (Nome, email, etc.)
     * 2. Inicia uma sub-coroutine (Job) para ficar à escuta das mudanças nos Requerimentos.
     */
    private suspend fun loadUserProfile(userId: String) {
        try {
            val role = authRepository.getUserRole(userId)

            if (role == "colaborador") {
                _state.update {
                    it.copy(
                        userRole = "colaborador",
                        fullName = "Colaborador",
                        beneficiaryStatus = BeneficiaryStatus.ATIVO
                    )
                }
            } else if (role == "beneficiario") {
                // 1. Carregar Dados Estáticos (Uma vez só)
                val beneficiary = beneficiaryRepository.getBeneficiaryById(userId)

                _state.update {
                    it.copy(
                        userRole = "beneficiario",
                        fullName = beneficiary?.name ?: "Sem Nome",
                        studentNumber = beneficiary?.id ?: "",
                        email = beneficiary?.email ?: "",
                        beneficiaryStatus = beneficiary?.status ?: BeneficiaryStatus.ANALISE
                    )
                }

                // 2. Iniciar Escuta em Tempo Real (Flow)
                // Cancelamos job anterior se existir para não ter duplicados
                requestsJob?.cancel()

                requestsJob = viewModelScope.launch {
                    // AQUI ESTÁ A MUDANÇA: .collect em vez de .first()
                    // Isto mantém a ligação aberta com o Firestore
                    requestRepository.getRequestsByBeneficiary(userId).collect { requests ->

                        // Sempre que houver uma mudança na BD, este código corre sozinho
                        val latestRequest = requests.maxByOrNull { it.submissionDate }

                        _state.update { currentState ->
                            currentState.copy(
                                requestStatus = latestRequest?.status ?: StatusType.PENDENTE,
                                requestObservations = latestRequest?.observations ?: "",
                                requestDocuments = latestRequest?.documents ?: emptyMap()
                            )
                        }
                    }
                }
            } else {
                throw Exception("Utilizador sem perfil associado.")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _state.update { it.copy(errorMessage = "Erro ao carregar perfil: ${e.message}") }
        }
    }

    // ==========================================================
    // --- REENVIAR DOCUMENTOS ---
    // ==========================================================

    fun resubmitDocument(docKey: String, uri: Uri) {
        viewModelScope.launch {

            _state.update { it.copy(uploadingDocKey = docKey) }

            val userId = _state.value.userId ?: authRepository.getCurrentUser()?.id

            if (userId == null) {
                _state.update { it.copy(errorMessage = "Sessão inválida. Por favor, faça login novamente.") }
                return@launch
            }

            val currentDocs = _state.value.requestDocuments

            _state.update { it.copy(isLoading = true) }

            try {
                // 1. Obter o requerimento
                val requests = requestRepository.getRequestsByBeneficiary(userId).first()

                if (requests.isEmpty()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Requerimento não encontrado."
                        )
                    }
                    return@launch
                }

                val request = requests.maxByOrNull { it.submissionDate }

                if (request == null) {
                    return@launch
                }

                // 2. Upload
                val fileName =
                    "requerimentos/$userId/${docKey}_reenvio_${java.util.UUID.randomUUID()}"
                val downloadUrl = storageRepository.uploadFile(uri, fileName)

                if (downloadUrl.isBlank()) throw Exception("URL do upload veio vazio.")

                // 3. Atualizar Dados Locais
                val updatedDocs = currentDocs.toMutableMap()
                updatedDocs[docKey] = downloadUrl

                val allDocumentsCompleted = updatedDocs.values.none { it.isNullOrBlank() }
                val newStatus =
                    if (allDocumentsCompleted) StatusType.ANALISE else StatusType.DOCS_INCORRETOS

                // 4. Atualizar BD
                requestRepository.updateRequestDocsAndStatus(
                    id = request.id,
                    documents = updatedDocs,
                    status = newStatus
                )

                // 5. Atualização Otimista da UI
                _state.update {
                    it.copy(
                        uploadingDocKey = null,
                        requestDocuments = updatedDocs,
                        requestStatus = newStatus
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        uploadingDocKey = null,
                        errorMessage = "Erro ao enviar: ${e.message}"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            requestsJob?.cancel() // Parar de escutar mudanças
            authRepository.logout()
            _state.value = AuthState()
        }
    }

    fun resetState() {
        _state.update { it.copy(isSuccess = false, errorMessage = null) }
    }

    fun isStep1Valid(): Boolean = _state.value.run {
        fullName.isNotBlank() && cc.length >= 8 && birthDate.isNotBlank() && phone.length >= 9 && email.contains(
            "@"
        ) && password.length >= 6
    }

    fun isStep2Valid(): Boolean =
        _state.value.run { requestCategory != null && educationLevel.isNotBlank() && school.isNotBlank() && studentNumber.isNotBlank() }

    fun isStep3Valid(): Boolean =
        _state.value.run { docIdentification != null && docMorada != null }

    fun updateStep1(fullName: String, cc: String, phone: String, email: String, password: String) {
        _state.update {
            it.copy(
                fullName = fullName,
                cc = cc,
                phone = phone,
                email = email,
                password = password
            )
        }
    }

    fun updateBirthDate(date: String) {
        _state.update { it.copy(birthDate = date) }
    }

    fun updateStep2(
        category: RequestCategory?,
        education: String,
        dependents: Int,
        school: String,
        courseName: String,
        studentNumber: String
    ) {
        _state.update {
            it.copy(
                requestCategory = category,
                educationLevel = education,
                dependents = dependents,
                school = school,
                courseName = courseName,
                studentNumber = studentNumber
            )
        }
    }

    fun updateStep3(docIdentification: Uri?, docFamily: Uri?, docMorada: Uri?) {
        _state.update {
            it.copy(
                docIdentification = docIdentification,
                docFamily = docFamily,
                docMorada = docMorada
            )
        }
    }

    fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                registerBeneficiaryUseCase(_state.value)
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    updateFcmToken(currentUser.id)
                    loadUserProfile(currentUser.id) // Isto ativa a escuta também para novos registos

                    communicationRepository.sendEmail(
                        EmailRequest(
                            to = _state.value.email,
                            subject = "Bem-vindo à Loja Social IPCA! \uD83D\uDC4B",
                            body = "<div>Olá ${_state.value.fullName}! O teu registo foi efetuado.</div>",
                            isHtml = true
                        )
                    )
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        isLoggedIn = true,
                        userRole = "beneficiario",
                        beneficiaryStatus = BeneficiaryStatus.ANALISE
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro desconhecido."
                    )
                }
                e.printStackTrace()
            }
        }
    }
}