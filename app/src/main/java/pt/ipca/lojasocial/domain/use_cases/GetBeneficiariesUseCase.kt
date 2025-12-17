package pt.ipca.lojasocial.domain.use_cases

import jakarta.inject.Inject
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository

class GetBeneficiariesUseCase @Inject constructor(
    private val repository: BeneficiaryRepository
) {
    // O operador 'invoke' permite chamar a classe como se fosse uma função
    suspend operator fun invoke(): List<Beneficiary> {
        // Vai buscar os dados brutos
        val allBeneficiaries = repository.getBeneficiaryList()

        // Ordenação por nome
        return allBeneficiaries
            .sortedBy { it.name }
    }
}