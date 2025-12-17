package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela eliminação de registos de entregas ou agendamentos.
 *
 * Permite remover uma entrega do sistema.
 *
 * **Nota de Integridade:**
 * A eliminação deve ser usada preferencialmente para corrigir erros de lançamento (registos duplicados ou inválidos).
 * Para agendamentos que não se concretizaram, sugere-se a alteração do estado para 'CANCELLED'
 * em vez da eliminação física, permitindo assim manter um histórico de operações auditável (**RF27**).
 */
class DeleteDeliveryUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    /**
     * Executa a eliminação da entrega indicada.
     *
     * @param id O identificador único da entrega a remover.
     */
    suspend operator fun invoke(id: String) {
        repository.deleteDelivery(id)
    }
}