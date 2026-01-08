package pt.ipca.lojasocial.domain.use_cases.request

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.RequestRepository
import pt.ipca.lojasocial.presentation.models.RequestUiModel
import javax.inject.Inject

class GetRequestsWithDetailsUseCase @Inject constructor(
    private val requestRepository: RequestRepository,
    private val beneficiaryRepository: BeneficiaryRepository
) {
    operator fun invoke(schoolYear: String): Flow<List<RequestUiModel>> {
        return requestRepository.getRequestsByYear(schoolYear)
            .map { requestList ->

                val uiList = mutableListOf<RequestUiModel>()

                for (request in requestList) {
                    val beneficiary =
                        beneficiaryRepository.getBeneficiaryById(request.beneficiaryId)

                    uiList.add(
                        RequestUiModel(
                            requestId = request.id,
                            beneficiaryName = beneficiary?.name ?: "Nome Desconhecido",
                            status = request.status
                        )
                    )
                }
                uiList
            }
    }
}