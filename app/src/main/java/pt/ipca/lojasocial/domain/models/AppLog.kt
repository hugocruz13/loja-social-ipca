package pt.ipca.lojasocial.domain.models

data class AppLog(
    val id: String = "",
    val acao: String = "",
    val utilizador: String = "",
    val detalhe: String = "",
    val timestamp: Long = System.currentTimeMillis()
)