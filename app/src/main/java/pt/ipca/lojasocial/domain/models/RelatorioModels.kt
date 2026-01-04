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

data class RelatorioAnualData(
    val anoLetivo: String, // ex: "2024-2025"
    val totalCampanhas: Int,
    val totalEntregasRealizadas: Int,
    val totalItensDoados: Int, // Soma de todos os produtos entregues
    val listaCampanhas: List<String>, // Nomes das campanhas
    val topProdutos: List<Pair<String, Int>> // Top 5 produtos (Nome, Quantidade) para o gráfico
)