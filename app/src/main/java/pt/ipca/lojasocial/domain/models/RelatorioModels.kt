package pt.ipca.lojasocial.domain.models

data class ItemRelatorioValidade(
    val nomeProduto: String,
    val quantidade: Int,
    val dataValidade: Long
)