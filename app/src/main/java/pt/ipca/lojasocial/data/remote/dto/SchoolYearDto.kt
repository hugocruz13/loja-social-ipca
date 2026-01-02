package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.DocumentId

data class SchoolYearDto(
    @DocumentId
    val id: String = "",
    val dataInicio: Long = 0L,
    val dataFim: Long = 0L
)