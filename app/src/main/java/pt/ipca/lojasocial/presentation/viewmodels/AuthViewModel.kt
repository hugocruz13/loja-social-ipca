package pt.ipca.lojasocial.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
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
    private val requestRepository: RequestRepository,
    private val storageRepository: StorageRepository,
    private val communicationRepository: CommunicationRepository
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
                // 1. App abriu e já havia login: Atualiza Token
                updateFcmToken(currentUser.id)

                _state.update { it.copy(userId = currentUser.id) }
                loadUserProfile(currentUser.id)
                _state.update { it.copy(isLoggedIn = true) }
            }
        }
    }

    // Função centralizada para guardar o token
    private fun updateFcmToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            viewModelScope.launch {
                // O repositório trata de guardar na coleção certa (Beneficiários ou Colaboradores)
                communicationRepository.saveFcmToken(userId, token)
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
                // 2. Login Manual efetuado: Atualiza Token
                updateFcmToken(user.id)

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
                val beneficiary = beneficiaryRepository.getBeneficiaryById(userId)
                val requests = requestRepository.getRequestsByBeneficiary(userId)
                val latestRequest = requests.maxByOrNull { it.submissionDate }

                _state.update {
                    it.copy(
                        userRole = "beneficiario",
                        fullName = beneficiary?.name ?: "Sem Nome",
                        studentNumber = beneficiary?.id ?: "",
                        email = beneficiary?.email ?: "",
                        beneficiaryStatus = beneficiary?.status ?: BeneficiaryStatus.ANALISE,
                        requestStatus = latestRequest?.status ?: StatusType.PENDENTE,
                        requestObservations = latestRequest?.observations ?: "",
                        requestDocuments = latestRequest?.documents ?: emptyMap()
                    )
                }
            } else {
                throw Exception("Utilizador sem perfil associado.")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _state.update { it.copy(errorMessage = "Erro ao carregar perfil: ${e.message}") }
        }
    }

    fun resubmitDocument(docKey: String, uri: Uri) {
        val userId = _state.value.userId ?: return
        val currentDocs = _state.value.requestDocuments

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val requests = requestRepository.getRequestsByBeneficiary(userId)
                val request = requests.maxByOrNull { it.submissionDate } ?: return@launch

                val fileName =
                    "requerimentos/$userId/${docKey}_reenvio_${java.util.UUID.randomUUID()}"
                val downloadUrl = storageRepository.uploadFile(uri, fileName)

                val updatedDocs = currentDocs.toMutableMap()
                updatedDocs[docKey] = downloadUrl

                val allDocumentsCompleted = updatedDocs.values.none { it.isNullOrBlank() }

                val newStatus = if (allDocumentsCompleted) {
                    StatusType.ANALISE
                } else {
                    StatusType.DOCS_INCORRETOS
                }

                requestRepository.updateRequestDocsAndStatus(
                    id = request.id,
                    documents = updatedDocs,
                    status = newStatus
                )

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

    fun updateStep3(
        docIdentification: Uri?,
        docFamily: Uri?,
        docMorada: Uri?,
        docRendimento: Uri?,
        docMatricula: Uri?
    ) {
        _state.update {
            it.copy(
                docIdentification = docIdentification,
                docFamily = docFamily,
                docMorada = docMorada,
                docRendimento = docRendimento,
                docMatricula = docMatricula
            )
        }
    }

    fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 1. Regista o utilizador no Firebase Auth, Firestore e guarda ficheiros
                registerBeneficiaryUseCase(_state.value)

                // 2. Verifica se o user foi criado
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    updateFcmToken(currentUser.id)

                    loadUserProfile(currentUser.id)
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