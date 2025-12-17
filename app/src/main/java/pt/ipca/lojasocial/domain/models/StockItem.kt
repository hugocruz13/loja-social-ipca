package pt.ipca.lojasocial.domain.models

/**
 * Representa um lote específico de produtos fisicamente existentes no inventário (Stock).
 *
 * Enquanto a entidade [Product] define o "tipo" de bem no catálogo, esta entidade representa
 * a quantidade real, a origem e a validade de um lote específico desse bem.
 * Fundamental para a gestão FIFO/FEFO (First Expired, First Out).
 *
 * **Invariantes e Regras de Negócio:**
 * - A `quantity` deve ser sempre maior ou igual a zero.
 * - A `expiryDate` é obrigatória para permitir o sistema de alertas de validade.
 * - A distinção entre doação individual e campanha é feita através da nulidade do `campaignId`.
 *
 * @property id Identificador único deste lote de stock.
 * @property productId ID do [Product] a que este lote corresponde (o que é?).
 * @property campaignId ID da [Campaign] de onde provém este stock. Se for `null`, considera-se uma doação individual/espontânea.
 * @property quantity Quantidade atual de unidades disponíveis neste lote.
 * @property entryDate Data de registo da entrada na Loja Social (timestamp).
 * @property expiryDate Data de validade do produto (timestamp). Crucial para relatórios de prioridade.
 * @property observations Observações sobre o estado físico do lote (ex: "Embalagem amolgada") ou detalhes da doação.
 */
data class StockItem(
    val id: String,
    val productId: String,
    val campaignId: String? = null,
    val quantity: Int,
    val entryDate: Long,
    val expiryDate: Long,
    val observations: String? = null
)