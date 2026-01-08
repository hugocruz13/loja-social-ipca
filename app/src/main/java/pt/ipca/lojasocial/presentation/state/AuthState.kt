package pt.ipca.lojasocial.presentation.state

import android.net.Uri
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.models.RequestType
import pt.ipca.lojasocial.domain.models.StatusType

data class AuthState(
    // ==========================================================
    // ESTADO DO SISTEMA (Loading, Erros, Autenticação)
    // ==========================================================
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false, // Usado para indicar sucesso no registo
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val userId: String? = null,

    // --- CONTROLO DE FOCO / TOQUE (STEP 1) ---

    val fullNameTouched: Boolean = false,
    val ccTouched: Boolean = false,
    val birthDateTouched: Boolean = false,
    val phoneTouched: Boolean = false,
    val emailTouched: Boolean = false,
    val passwordTouched: Boolean = false,

    // --- CONTROLO DE FOCO / TOQUE (STEP 2) ---
    val schoolTouched: Boolean = false,
    val courseNameTouched: Boolean = false,
    val studentNumberTouched: Boolean = false,
    val educationLevelTouched: Boolean = false,
    val requestCategoryTouched: Boolean = false,

    // --- CONTROLO DE FOCO / TOQUE (STEP 3) ---
    val docIdentificationTouched: Boolean = false,
    val docFamilyTouched: Boolean = false,
    val docMoradaTouched: Boolean = false,

    // --- MENSAGENS DE ERRO ESPECÍFICAS (Para os componentes modernos) ---
    val fullNameError: String? = null,
    val ccError: String? = null,
    val birthDateError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val studentNumberError: String? = null,
    val schoolError: String? = null,
    val courseNameError: String? = null,

    // Controlo de submissão
    val isStep1Valid: Boolean = false,
    val isStep2Valid: Boolean = false,
    val isStep3Valid: Boolean = false,

    // ==========================================================
    // ROLE / PAPEL DO UTILIZADOR
    // ==========================================================
    // Define se é "colaborador" (Staff) ou "beneficiario" (Aluno)
    // Isto resolve o erro "Unresolved reference: userRole"
    val userRole: String = "",

    // ==========================================================
    // DADOS DE PERFIL (Carregados após o login)
    // ==========================================================
    val fullName: String = "",
    val studentNumber: String = "",
    val email: String = "", // Email guardado ou lido do perfil

    // Estado da conta do Beneficiário (ATIVO, INATIVO, ANALISE)
    // Usado para decidir se entra na App ou vai para o ecrã de espera
    val beneficiaryStatus: BeneficiaryStatus = BeneficiaryStatus.ANALISE,

    // Estado do último requerimento (APROVADA, REJEITADA, etc.)
    val requestStatus: StatusType = StatusType.PENDENTE,

    // Motivo de rejeição ou observações do requerimento
    val requestObservations: String = "",

    val requestDocuments: Map<String, String?> = emptyMap(),

    val uploadingDocKey: String? = null,

    // ==========================================================
    // DADOS DO FORMULÁRIO DE REGISTO (Inputs)
    // ==========================================================
    // Passo 1: Dados Pessoais
    val cc: String = "",
    val phone: String = "",
    val password: String = "",
    val birthDate: String = "",

    // Passo 2: Dados Escolares e Socioeconómicos
    val requestCategory: RequestType? = null,
    val educationLevel: String = "",
    val dependents: Int = 0,
    val school: String = "",
    val courseName: String = "",
    // studentNumber também é usado aqui

    // Passo 3: Documentos (URIs locais antes do upload)
    val docIdentification: Uri? = null,
    val docFamily: Uri? = null,
    val docMorada: Uri? = null
)
