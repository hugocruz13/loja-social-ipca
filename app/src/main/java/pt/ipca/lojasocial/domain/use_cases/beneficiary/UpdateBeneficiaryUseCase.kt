package pt.ipca.lojasocial.domain.use_cases.beneficiary

import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.UserRole
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
     * Atualiza o perfil aplicando regras de segurança baseadas no cargo (Role).
     */
    suspend operator fun invoke(
        role: UserRole,
        original: Beneficiary,
        modified: Beneficiary
    ): Result<Unit> {
        return try {
            // REGRA DE NEGÓCIO:
            val finalDataToSave = when (role) {
                UserRole.BENEFICIARY -> {
                    // Beneficiário: Ignora alterações de nome/ID. Só aceita telefone e email novos.
                    original.copy(
                        phoneNumber = modified.phoneNumber,
                        email = modified.email
                    )
                }
                UserRole.STAFF -> {
                    // Staff: Pode alterar todos os dados passados no objeto modified.
                    modified
                }
            }

            // Persiste na base de dados
            repository.updateBeneficiary(finalDataToSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}