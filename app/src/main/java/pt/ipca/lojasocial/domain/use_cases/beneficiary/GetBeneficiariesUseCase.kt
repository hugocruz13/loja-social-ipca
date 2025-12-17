package pt.ipca.lojasocial.domain.use_cases.beneficiary

import jakarta.inject.Inject
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository

/**
 * Caso de Uso responsável pela listagem geral e ordenação dos beneficiários registados.
 *
 * Este componente serve a funcionalidade principal de consulta, garantindo que a lista
 * apresentada ao utilizador está normalizada (neste caso, ordenada alfabeticamente).
 *
 * **Requisitos Funcionais:**
 * - **RF02**: Listar beneficiários registados no sistema.
 */
class GetBeneficiariesUseCase @Inject constructor(
    private val repository: BeneficiaryRepository
) {
    /**
     * Executa a obtenção e processamento da lista de beneficiários.
     *
     * @return Lista de [Beneficiary] ordenada alfabeticamente pelo nome.
     */
    suspend operator fun invoke(): List<Beneficiary> {
        // Vai buscar os dados brutos
        val allBeneficiaries = repository.getBeneficiaries()

        // Regra de Negócio: A listagem deve ser sempre alfabética para facilitar a procura
        return allBeneficiaries
            .sortedBy { it.name }
    }
}