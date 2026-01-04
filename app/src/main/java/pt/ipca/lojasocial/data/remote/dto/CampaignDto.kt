package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.DocumentId

data class CampaignDto(
    @DocumentId
    val id: String = "",

    var nome: String = "",

    var descricao: String = "",

    var dataInicio: Long = 0L,

    var dataFim: Long = 0L,

    var tipo: String = "",

    var estado: String = "",

    var imagemUrl: String = ""
)