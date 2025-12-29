package pt.ipca.lojasocial.domain.use_cases.auth

import android.net.Uri
import pt.ipca.lojasocial.domain.models.*
import pt.ipca.lojasocial.domain.repository.*
import pt.ipca.lojasocial.presentation.components.StatusType
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

    suspend operator fun invoke(state: RegistrationState) {

        // 1. Criar utilizador (Alterado para lidar com o Result do teu Repo)
        val result = authRepository.signUp(state.email, state.password)

        // Se der erro, getOrThrow() vai lançar a exceção que definiste no RepositoryImpl
        // e o ViewModel vai apanhá-la no bloco try-catch.
        val newUserId = result.getOrThrow()

        // 2. Upload de Documentos
        val uploadedDocUrls = mutableListOf<String>()

        suspend fun uploadIfPresent(uri: Uri?, docType: String) {
            if (uri != null) {
                val fileName = "documentos/$newUserId/${docType}_${UUID.randomUUID()}"
                val url = storageRepository.uploadFile(uri, fileName)
                uploadedDocUrls.add(url)
            }
        }

        uploadIfPresent(state.docIdentification, "identificacao")
        uploadIfPresent(state.docFamily, "agregado")
        uploadIfPresent(state.docMorada, "morada")
        uploadIfPresent(state.docRendimento, "rendimento")
        uploadIfPresent(state.docMatricula, "matricula")

        // 3. Criar Beneficiário
        val newBeneficiary = Beneficiary(
            id = newUserId,
            name = state.fullName,
            email = state.email,
            phoneNumber = state.phone.replace(" ", "").toIntOrNull() ?: 0,
            birthDate = convertDateToSeconds(state.birthDate),
            schoolYearId = "2024_2025",
            ccNumber = state.cc,
            status = BeneficiaryStatus.ANALISE
        )
        beneficiaryRepository.addBeneficiary(newBeneficiary)

        // 4. Criar Requerimento
        val newRequest = Request(
            id = UUID.randomUUID().toString(),
            beneficiaryId = newUserId,
            schoolYearId = "2024_2025",
            status = StatusType.ANALISE ,
            type = mapCategoryToType(state.requestCategory),
            documentUrls = uploadedDocUrls,
            observations = "Curso: ${state.courseName}, Escola: ${state.school}, Nº: ${state.studentNumber}"
        )
        requestRepository.addRequest(newRequest)
    }

    // --- Helpers ---

    private fun mapCategoryToType(category: RequestCategory?): RequestType {
        return when (category) {
            RequestCategory.ALIMENTARES -> RequestType.FOOD
            RequestCategory.HIGIENE -> RequestType.HYGIENE
            RequestCategory.LIMPEZA -> RequestType.CLEANING
            else -> RequestType.ALL
        }
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