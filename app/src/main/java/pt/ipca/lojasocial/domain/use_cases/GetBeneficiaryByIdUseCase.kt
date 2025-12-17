package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import javax.inject.Inject

class GetBeneficiaryByIdUseCase @Inject constructor(
    private val repository: BeneficiaryRepository
) {
    /**
     * Obtém os detalhes de um beneficiário específico.
     *
     * @param id O identificador do beneficiário.
     * @return O [Beneficiary] encontrado ou null.
     */
    suspend operator fun invoke(id: String): Beneficiary? {
        return repository.getBeneficiaryById(id)
    }
}