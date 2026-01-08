package pt.ipca.lojasocial.domain.use_cases.delivery

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por obter os detalhes completos de uma entrega ou agendamento específico.
 *
 * Este componente é utilizado na navegação para o ecrã de detalhes, permitindo visualizar
 * a lista de produtos associados, observações e o estado atual da entrega.
 *
 * **Contexto:**
 * Essencial para operações de consulta detalhada antes de realizar ações como
 * marcar como entregue, cancelar ou editar a lista de bens.
 */
class GetDeliveryByIdUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {

    /**
     * Executa a pesquisa da entrega pelo seu identificador.
     *
     * @param id O identificador único da entrega.
     * @return O objeto [Delivery] se encontrado, ou `null` caso contrário.
     */
    operator fun invoke(id: String): Flow<Delivery?> {
        return repository.getDeliveryById(id)
    }
}