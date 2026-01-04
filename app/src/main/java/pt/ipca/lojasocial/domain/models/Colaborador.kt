package pt.ipca.lojasocial.domain.models

import com.google.firebase.firestore.Exclude


enum class ColaboradorStatus {
    ATIVO,
    INATIVO,
    ANALISE
}


data class Colaborador(
    @get:Exclude val uid: String = "",
    val nome: String = "",
    val email: String = "",
    val cargo: String = "",
    val permissao: String = "",
    val ativo: Boolean = true
)