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
import pt.ipca.lojasocial.domain.models.RequestType
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.repository.AuthRepository
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.CommunicationRepository
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.domain.repository.StorageRepository
import pt.ipca.lojasocial.domain.use_cases.auth.RegisterBeneficiaryUseCase
import pt.ipca.lojasocial.presentation.state.AuthState
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.UUID
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

    private var requestsJob: Job? = null

    // Expressões Regulares (Regex) para Validação
    private val ccRegex = Regex("""^\d{8}\s[A-Z0-7]{4}$""")

    // Pelo menos: 1 Maíuscula, 1 Minúscula, 1 Número, 1 Símbolo, mín. 8 caracteres
    private val passwordRegex =
        Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$""")
    private val phoneRegex = Regex("""^9\d{8}$""")

    init {
        checkAuthStatus()
    }

    // ==========================================================
    // --- LÓGICA DE VALIDAÇÃO REACTIVA (TEMPO REAL) ---
    // ==========================================================

    private fun validateStep1() {
        _state.update { s ->
            val nameErr =
                if (s.fullName.trim().split(" ").size < 2) "Introduza nome e apelido" else null
            val ccErr = if (!ccRegex.matches(s.cc)) "Formato inválido (Ex: 12345678 2ZX0)" else null
            val phoneErr = if (!phoneRegex.matches(s.phone)) "Número inválido (9 dígitos)" else null
            val emailErr = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email)
                    .matches()
            ) "Email inválido" else null
            val passErr =
                if (!passwordRegex.matches(s.password)) "Use: 8 carac., Maiúsc., Minúsc., nº e símbolo" else null
            var dateErr: String? = null
            if (s.birthDate.isBlank()) {
                dateErr = "Data obrigatória"
            } else {
                try {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val birthDate = LocalDate.parse(s.birthDate, formatter)
                    val today = LocalDate.now()
                    val age = Period.between(birthDate, today).years

                    if (age < 18) {
                        dateErr = "Apenas para maiores de 18 anos"
                    } else if (age > 120) {
                        dateErr = "Data inválida"
                    }
                } catch (e: Exception) {
                    dateErr = "Formato inválido"
                }
            }

            val isValid = nameErr == null && ccErr == null && phoneErr == null &&
                    emailErr == null && passErr == null && dateErr == null

            s.copy(
                fullNameError = nameErr,
                ccError = ccErr,
                phoneError = phoneErr,
                emailError = emailErr,
                passwordError = passErr,
                birthDateError = dateErr,
                isStep1Valid = isValid
            )
        }
    }

    private fun validateStep2() {
        _state.update { s ->
            val schoolErr = if (s.school.length < 3) "Nome da instituição demasiado curto" else null
            val courseErr = if (s.courseName.isBlank()) "O nome do curso é obrigatório" else null
            val studentNumErr =
                if (s.studentNumber.isBlank()) "Introduza o nº de estudante" else null
            val categoryErr = if (s.requestCategory == null) "Selecione uma tipologia" else null
            val educationErr =
                if (s.educationLevel.isBlank()) "Selecione o nível de ensino" else null

            val isValid = schoolErr == null && courseErr == null &&
                    studentNumErr == null && categoryErr == null && educationErr == null

            s.copy(
                studentNumberError = studentNumErr, // Exemplo de mapeamento para o campo
                isStep2Valid = isValid
            )
        }
    }

    // ==========================================================
    // --- ATUALIZAÇÃO DE ESTADO ---
    // ==========================================================

    fun updateStep1(fullName: String, cc: String, phone: String, email: String, password: String) {
        _state.update { currentState ->
            currentState.copy(
                fullName = fullName,
                cc = cc,
                phone = phone,
                email = email,
                password = password,
                // Só marca como 'touched' se o valor mudou em relação ao que estava no estado
                fullNameTouched = currentState.fullNameTouched || fullName != currentState.fullName,
                ccTouched = currentState.ccTouched || cc != currentState.cc,
                phoneTouched = currentState.phoneTouched || phone != currentState.phone,
                emailTouched = currentState.emailTouched || email != currentState.email,
                passwordTouched = currentState.passwordTouched || password != currentState.password
            )
        }
        validateStep1()
    }

    fun updateBirthDate(date: String) {
        _state.update {
            it.copy(
                birthDate = date,
                birthDateTouched = true
            )
        }
        validateStep1()
    }

    fun updateStep2(
        category: RequestType?,
        education: String,
        dependents: Int,
        school: String,
        courseName: String,
        studentNumber: String
    ) {
        _state.update { currentState ->
            currentState.copy(
                requestCategory = category,
                educationLevel = education,
                dependents = dependents,
                school = school,
                courseName = courseName,
                studentNumber = studentNumber,
                // Marcação de campos tocados no Step 2
                schoolTouched = currentState.schoolTouched || school != currentState.school,
                courseNameTouched = currentState.courseNameTouched || courseName != currentState.courseName,
                studentNumberTouched = currentState.studentNumberTouched || studentNumber != currentState.studentNumber,
                educationLevelTouched = currentState.educationLevelTouched || education != currentState.educationLevel
            )
        }
        validateStep2()
    }

    fun updateStep3(docIdentification: Uri?, docFamily: Uri?, docMorada: Uri?) {
        _state.update { currentState ->
            currentState.copy(
                docIdentification = docIdentification,
                docFamily = docFamily,
                docMorada = docMorada,
                // Se o Uri não for nulo, o utilizador interagiu com o seletor
                docIdentificationTouched = currentState.docIdentificationTouched || docIdentification != null,
                docFamilyTouched = currentState.docFamilyTouched || docFamily != null,
                docMoradaTouched = currentState.docMoradaTouched || docMorada != null
            )
        }
        // Validação do Step 3
        _state.update { it.copy(isStep3Valid = it.docIdentification != null && it.docMorada != null) }
    }

    // ==========================================================
    // --- AUTENTICAÇÃO E PERFIL ---
    // ==========================================================

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                updateFcmToken(currentUser.id)
                _state.update { it.copy(userId = currentUser.id) }
                loadUserProfile(currentUser.id)
                _state.update { it.copy(isLoggedIn = true) }
            }
        }
    }

    private fun updateFcmToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            viewModelScope.launch { communicationRepository.saveFcmToken(userId, token) }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.login(email, pass).onSuccess { user ->
                updateFcmToken(user.id)
                loadUserProfile(user.id)
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
                _state.update {
                    it.copy(
                        userRole = "beneficiario",
                        fullName = beneficiary?.name ?: "Sem Nome",
                        studentNumber = beneficiary?.id ?: "",
                        email = beneficiary?.email ?: "",
                        beneficiaryStatus = beneficiary?.status ?: BeneficiaryStatus.ANALISE
                    )
                }

                requestsJob?.cancel()
                requestsJob = viewModelScope.launch {
                    requestRepository.getRequestsByBeneficiary(userId).collect { requests ->
                        val latestRequest = requests.maxByOrNull { it.submissionDate }
                        _state.update {
                            it.copy(
                                requestStatus = latestRequest?.status ?: StatusType.PENDENTE,
                                requestObservations = latestRequest?.observations ?: "",
                                requestDocuments = latestRequest?.documents ?: emptyMap()
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = "Erro ao carregar perfil: ${e.message}") }
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
                    loadUserProfile(currentUser.id)
                    communicationRepository.sendEmail(
                        EmailRequest(
                            to = _state.value.email,
                            subject = "Bem-vindo à Loja Social IPCA! 👋",
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
            }
        }
    }

    fun resubmitDocument(docKey: String, uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(uploadingDocKey = docKey, isLoading = true) }
            val userId = _state.value.userId ?: authRepository.getCurrentUser()?.id ?: return@launch

            try {
                val requests = requestRepository.getRequestsByBeneficiary(userId).first()
                val request = requests.maxByOrNull { it.submissionDate }
                    ?: throw Exception("Requerimento não encontrado.")

                val fileName = "requerimentos/$userId/${docKey}_reenvio_${UUID.randomUUID()}"
                val downloadUrl = storageRepository.uploadFile(uri, fileName)

                val updatedDocs =
                    _state.value.requestDocuments.toMutableMap().apply { put(docKey, downloadUrl) }
                val newStatus =
                    if (updatedDocs.values.none { it.isNullOrBlank() }) StatusType.ANALISE else StatusType.DOCS_INCORRETOS

                requestRepository.updateRequestDocsAndStatus(request.id, updatedDocs, newStatus)
                _state.update {
                    it.copy(
                        uploadingDocKey = null,
                        requestDocuments = updatedDocs,
                        requestStatus = newStatus,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        uploadingDocKey = null,
                        isLoading = false,
                        errorMessage = "Erro ao enviar: ${e.message}"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            requestsJob?.cancel()
            authRepository.logout()
            _state.value = AuthState()
        }
    }

    fun resetState() {
        _state.update { it.copy(isSuccess = false, errorMessage = null) }
    }
}