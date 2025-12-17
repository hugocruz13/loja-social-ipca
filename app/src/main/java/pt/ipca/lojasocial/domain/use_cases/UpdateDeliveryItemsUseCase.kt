package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela alteração do conteúdo (cabaz) de uma entrega.
 *
 * Permite editar a lista de produtos e respetivas quantidades associadas a uma entrega ou agendamento,
 * sem alterar os dados do beneficiário ou o estado da entrega.
 *
 * **Cenários de Uso:**
 * - Ajustes operacionais (ex: o beneficiário pediu 2 pacotes de arroz, mas só há 1 em stock).
 * - Correção de erros de lançamento no pedido inicial.
 * - Adição de produtos extra ao cabaz no momento da recolha.
 */
class UpdateDeliveryItemsUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {

    /**
     * Executa a atualização dos itens da entrega.
     *
     * Inclui uma validação de integridade para garantir que não são registadas quantidades inválidas.
     *
     * @param id O identificador da entrega a atualizar.
     * @param items Um mapa representando o novo cabaz (Chave: ID do Produto, Valor: Quantidade).
     * @throws IllegalArgumentException Se alguma das quantidades for menor ou igual a zero.
     */
    suspend operator fun invoke(id: String, items: Map<String, Int>) {
        // Validação de Integridade: Impedir quantidades negativas ou nulas.
        // Regra de Negócio: Uma entrega deve conter quantidades positivas de bens.
        if (items.values.any { it <= 0 }) {
            throw IllegalArgumentException("A quantidade dos itens deve ser positiva.")
        }
        repository.updateDeliveryItems(id, items)
    }
}