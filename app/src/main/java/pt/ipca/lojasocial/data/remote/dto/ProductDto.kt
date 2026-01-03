package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * Representação do produto na base de dados (ex: Firebase).
 * O ID geralmente é o nome do documento, por isso não está aqui dentro.
 */
data class ProductDto(
    @get:PropertyName("nome") @set:PropertyName("nome")
    var name: String = "",
    val type: String = "", // O enum é salvo como String
    @get:PropertyName("fotoUrl") @set:PropertyName("fotoUrl")
    var photoUrl: String? = null,
    val observations: String? = null
)