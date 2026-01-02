package pt.ipca.lojasocial.domain.models

import androidx.compose.ui.graphics.Color

enum class StatusType(val label: String, val baseColor: Color, val textColor: Color) {
    ATIVA(
        "Ativa",
        Color(0xFF22C55E),
        Color(0xFF22C55E)
    ),
    APROVADA(
        "Aprovada",
        Color(0xFF22C55E),
        Color(0xFF22C55E)
    ),
    AGENDADA(
        "Agendada",
        Color(0xFFD97706),
        Color(0xFFD97706)
    ),
    COMPLETA(
        "Completa",
        Color(0xFF9CA3AF),
        Color(0xFF9CA3AF)
    ),
    ANALISE(
        "Em Análise",
        Color(0xFFD97706),
        Color(0xFFD97706)
    ),
    PENDENTE(
        "Pendente",
        Color(0xFFD97706),
        Color(0xFFD97706)
    ),
    DOCS_INCORRETOS(
        "Docs Incorretos",
        Color(0xFFD97706),
        Color(0xFFD97706)
    ),
    ENTREGUE(
        "Entregue",
        Color(0xFF2E7D32),
        Color(0xFF2E7D32)
    ),
    NOT_ENTREGUE(
        "Não Entregue",
        Color(0xFFD32F2F),
        Color(0xFFD32F2F)
    ),
    REJEITADA(
        "Rejeitado",
        Color(0xFFD32F2F),
        Color(0xFFD32F2F)
    ),
    ATUAL(
        "Atual",
        Color(0xFF2E7D32),
        Color(0xFF2E7D32)
    )

}