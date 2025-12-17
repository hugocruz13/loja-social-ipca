package pt.ipca.lojasocial.domain.models

data class Product(
    val id: String,
    val name: String,
    val type: ProductType, // Usa o Enum ProductType
    val photoUrl: String? = null,
    val observations: String? = null
)