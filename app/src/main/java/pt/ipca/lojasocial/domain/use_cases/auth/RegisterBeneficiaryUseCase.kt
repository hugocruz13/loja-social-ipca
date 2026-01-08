package pt.ipca.lojasocial.domain.use_cases.auth

import android.net.Uri
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.models.Request
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.repository.AuthRepository
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.domain.repository.StorageRepository
import pt.ipca.lojasocial.presentation.state.AuthState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class RegisterBeneficiaryUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val requestRepository: RequestRepository
) {

    suspend operator fun invoke(state: AuthState) {

        // 1. Criar utilizador
        val result = authRepository.signUp(state.email, state.password, state.fullName)
        val newUserId = result.getOrThrow()

        // 2. Upload de Documentos (AGORA É UM MAPA)
        // A chave é o tipo de documento (ex: "morada"), o valor é o URL
        val uploadedDocs = mutableMapOf<String, String?>()

        suspend fun uploadIfPresent(uri: Uri?, docKey: String) {
            if (uri != null) {
                // Cria nome único
                val fileName = "requerimentos/$newUserId/${docKey}_${UUID.randomUUID()}"

                // Faz upload e recebe o URL
                val url = storageRepository.uploadFile(uri, fileName)

                // Guarda no mapa com a chave específica
                uploadedDocs[docKey] = url
            } else {
                // Opcional: Se quiseres registar explicitamente que não foi enviado
                uploadedDocs[docKey] = null
            }
        }

        // Fazemos o upload associando as chaves que definimos no ViewModel (docLabels)
        uploadIfPresent(state.docIdentification, "identificacao")
        uploadIfPresent(state.docFamily, "agregado")
        uploadIfPresent(state.docMorada, "morada")

        // 3. Criar Beneficiário
        val newBeneficiary = Beneficiary(
            id = newUserId,
            name = state.fullName,
            email = state.email,
            phoneNumber = state.phone.replace(" ", "").toIntOrNull() ?: 0,
            birthDate = convertDateToSeconds(state.birthDate),
            schoolYearId = "2024_2025",
            ccNumber = state.cc,
            status = BeneficiaryStatus.ANALISE // Este Enum é do Beneficiário, mantém-se
        )
        beneficiaryRepository.addBeneficiary(newBeneficiary)

        // 4. Criar Requerimento
        val newRequest = Request(
            id = UUID.randomUUID().toString(),
            beneficiaryId = newUserId,
            schoolYearId = "2024_2025",
            status = StatusType.ANALISE,
            type = state.requestCategory,

            // Passamos o MAPA de documentos
            documents = uploadedDocs,

            observations = "Curso: ${state.courseName}, Escola: ${state.school}, Nº: ${state.studentNumber}"
        )

        requestRepository.addRequest(newRequest)
    }

    private fun convertDateToSeconds(dateString: String): Int {
        return try {
            if (dateString.isBlank()) return 0
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(dateString)
            (date?.time?.div(1000))?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }
}