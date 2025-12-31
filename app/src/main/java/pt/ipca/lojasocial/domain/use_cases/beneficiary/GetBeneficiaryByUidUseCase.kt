package pt.ipca.lojasocial.domain.use_cases.beneficiary

import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import javax.inject.Inject

class GetBeneficiaryByUidUseCase @Inject constructor(
    private val repository: BeneficiaryRepository
) {
    suspend operator fun invoke(uid: String): Beneficiary {
        return repository.getBeneficiaryByUid(uid)
    }
}