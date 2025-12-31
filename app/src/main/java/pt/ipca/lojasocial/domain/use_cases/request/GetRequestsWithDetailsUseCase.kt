package pt.ipca.lojasocial.domain.use_cases.request

import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.presentation.models.RequestUiModel
import javax.inject.Inject

class GetRequestsWithDetailsUseCase @Inject constructor(
    private val requestRepository: RequestRepository,
    private val beneficiaryRepository: BeneficiaryRepository
) {
    suspend operator fun invoke(schoolYear: String): List<RequestUiModel> {
        // 1. Buscar requerimentos do ano
        val requests = requestRepository.getRequestsByYear(schoolYear)

        // 2. Juntar com o Nome do Beneficiário
        return requests.map { request ->
            val beneficiary = beneficiaryRepository.getBeneficiaryById(request.beneficiaryId)

            RequestUiModel(
                requestId = request.id,
                beneficiaryName = beneficiary?.name ?: "Nome Desconhecido",
                status = request.status
            )
        }
    }
}