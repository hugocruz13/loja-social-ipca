package pt.ipca.lojasocial.domain.use_cases.request

import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.presentation.models.RequestDetailUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetRequestByIdUseCase @Inject constructor(
    private val requestRepository: RequestRepository,
    private val beneficiaryRepository: BeneficiaryRepository
) {
    suspend operator fun invoke(requestId: String): RequestDetailUiModel? {
        // 1. Buscar o Requerimento
        val request = requestRepository.getRequestById(requestId) ?: return null

        // 2. Buscar o Beneficiário associado
        val beneficiary = beneficiaryRepository.getBeneficiaryById(request.beneficiaryId)

        // 3. Formatar Data
        val dateCheck = Date(request.submissionDate)
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateString = formatter.format(dateCheck)

        // 4. Mapear para UI Model
        return RequestDetailUiModel(
            id = request.id,
            beneficiaryName = beneficiary?.name ?: "Desconhecido",
            cc = beneficiary?.ccNumber ?: "N/A",
            email = beneficiary?.email ?: "N/A",
            phone = beneficiary?.phoneNumber?.toString() ?: "N/A",
            submissionDate = dateString,
            status = request.status,
            type = request.type,
            documents = request.documents
        )
    }
}