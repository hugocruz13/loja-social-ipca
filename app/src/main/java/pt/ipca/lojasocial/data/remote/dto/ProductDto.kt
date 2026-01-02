package pt.ipca.lojasocial.data.remote.dto


/**
 * Representação do produto na base de dados (ex: Firebase).
 * O ID geralmente é o nome do documento, por isso não está aqui dentro.
 */
data class ProductDto(
    val name: String = "",
    val type: String = "", // O enum é salvo como String
    val photoUrl: String? = null,
    val observations: String? = null
)