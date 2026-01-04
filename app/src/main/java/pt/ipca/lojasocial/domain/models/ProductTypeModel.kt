package pt.ipca.lojasocial.domain.models

import com.google.firebase.firestore.Exclude

data class ProductTypeModel(
    @get:Exclude val id: String = "",
    val nome: String = "",
    val tipo: String = "",
    val observacoes: String = "",
    val fotoUrl: String = ""
)