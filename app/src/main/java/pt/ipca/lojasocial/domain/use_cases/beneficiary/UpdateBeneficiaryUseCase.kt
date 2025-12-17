package pt.ipca.lojasocial.domain.use_cases.beneficiary

import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela atualização dos dados de um beneficiário já registado.
 *
 * Este componente permite manter a ficha do utente atualizada, respondendo a mudanças
 * na situação de vida do beneficiário ou corrigindo erros administrativos.
 *
 * **Cenários de Uso:**
 * - Correção de gralhas no registo inicial.
 * - Alteração de morada ou contactos.
 * - Atualização da composição do agregado familiar (que pode influenciar a quantidade de bens a receber).
 * - Alteração manual do estado (ex: suspensão temporária de apoio).
 */
class UpdateBeneficiaryUseCase @Inject constructor(
    private val repository: BeneficiaryRepository
) {

    /**
     * Executa a persistência das alterações no registo do beneficiário.
     *
     * @param beneficiary O objeto [Beneficiary] contendo os dados novos/corrigidos.
     * **Nota Técnica:** O campo `id` deve corresponder ao registo original para garantir que a atualização ocorre na entidade correta.
     */
    suspend operator fun invoke(beneficiary: Beneficiary) {
        repository.updateBeneficiary(beneficiary)
    }
}