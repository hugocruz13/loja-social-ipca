package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.DocumentId

data class AppLogDto(
    @DocumentId val id: String = "",
    val acao: String = "",
    val detalhe: String = "",
    val utilizador: String = "",
    val timestamp: Long = 0L
)