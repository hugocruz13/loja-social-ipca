package pt.ipca.lojasocial.domain.use_cases.delivery

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por obter o histórico individual de entregas de um beneficiário.
 *
 * Permite consultar todas as interações passadas (entregas realizadas) e futuras (agendamentos)
 * associadas a um utente específico.
 *
 * **Contexto de Negócio:**
 * É fundamental para o perfil do beneficiário, permitindo ao Staff validar que apoios
 * já foram prestados antes de aprovar novas entregas.
 *
 * **Requisitos Funcionais:**
 * - **RF16**: Listagem do histórico de levantamentos/entregas por beneficiário.
 */
class GetDeliveriesByBeneficiaryUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {

    /**
     * Executa a pesquisa de entregas associadas ao beneficiário indicado.
     *
     * @param beneficiaryId O identificador único do beneficiário.
     * @return Lista de [Delivery] (ordenada conforme a implementação do repositório, geralmente cronologicamente).
     */
    operator fun invoke(beneficiaryId: String): Flow<List<Delivery>> {
        return repository.getDeliveriesByBeneficiary(beneficiaryId)
    }
}