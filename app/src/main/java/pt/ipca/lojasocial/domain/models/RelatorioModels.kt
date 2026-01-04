package pt.ipca.lojasocial.domain.models

data class ItemRelatorioValidade(
    val nomeProduto: String,
    val quantidade: Int,
    val dataValidade: Long
)

data class ItemRelatorioLog(
    val dataFormatada: String, // Já passamos a data formatada (dd/MM/yyyy HH:mm)
    val acao: String,
    val utilizador: String,
    val detalhe: String
)