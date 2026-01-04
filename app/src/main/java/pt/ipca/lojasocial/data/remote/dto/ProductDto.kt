package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * Objeto de Transferência de Dados (DTO) para Produtos.
 *
 * Representa a estrutura do documento armazenado no Firestore (coleção "produtos").
 * O ID do produto corresponde normalmente ao ID do documento, por isso não é incluído aqui.
 *
 * O tipo do produto ([ProductType]) é armazenado como String.
 * A conversão para enum é feita no Mapper.
 */
data class ProductDto(

    @get:PropertyName("nome") @set:PropertyName("nome")
    var name: String = "",

    // Enum ProductType guardado como String (FOOD, HYGIENE, CLEANING, OTHER)
    @get:PropertyName("tipo") @set:PropertyName("tipo")
    var type: String = "",

    @get:PropertyName("fotoUrl") @set:PropertyName("fotoUrl")
    var photoUrl: String? = null,

    @get:PropertyName("observacoes") @set:PropertyName("observacoes")
    var observations: String? = null
)
