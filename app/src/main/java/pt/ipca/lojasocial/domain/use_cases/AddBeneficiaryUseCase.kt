package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pelo registo de novos beneficiários na Loja Social.
 *
 * Este componente encapsula a lógica de criação de fichas de beneficiários,
 * garantindo que não são criados registos "fantasma" sem identificação.
 *
 * **Requisitos Funcionais:**
 * - **RF01**: Registo de beneficiários (nome, nº de estudante, etc.).
 */
class AddBeneficiaryUseCase @Inject constructor(
    private val repository: BeneficiaryRepository
) {
    /**
     * Executa o registo de um novo beneficiário.
     *
     * Aplica regras de validação síncronas para garantir que os campos obrigatórios
     * de identificação estão preenchidos antes de contactar a camada de dados.
     *
     * @param beneficiary A entidade [Beneficiary] com os dados a inserir.
     * @throws IllegalArgumentException Se o ID (ex: Nº de Aluno) ou o Nome estiverem vazios ou em branco.
     */
    suspend operator fun invoke(beneficiary: Beneficiary) {
        // Validação de Integridade: ID e Nome são obrigatórios para identificar o beneficiário
        if (beneficiary.id.isBlank() || beneficiary.name.isBlank()) {
            throw IllegalArgumentException("O ID e o Nome são obrigatórios.")
        }

        repository.addBeneficiary(beneficiary)
    }
}