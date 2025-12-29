package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Composable
fun AppStatusBadge(
    status: StatusType,
    modifier: Modifier = Modifier
) {
    val contentColor = status.baseColor

    val backgroundColor = status.baseColor.copy(alpha = 0.10f)

    val textColor = contentColor

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(99.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(contentColor)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = status.label,
                color = status.textColor,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppStatusBadgePreview() {
    Column(modifier = Modifier.padding(16.dp)) {

        AppStatusBadge(status = StatusType.ATIVA)
        Spacer(modifier = Modifier.height(8.dp))

        AppStatusBadge(status = StatusType.AGENDADA)
        Spacer(modifier = Modifier.height(8.dp))

        AppStatusBadge(status = StatusType.COMPLETA)
        Spacer(modifier = Modifier.height(8.dp))

        AppStatusBadge(status = StatusType.ANALISE)
        Spacer(modifier = Modifier.height(8.dp))

        AppStatusBadge(status = StatusType.PENDENTE)
        Spacer(modifier = Modifier.height(8.dp))

        AppStatusBadge(status = StatusType.ENTREGUE)
        Spacer(modifier = Modifier.height(8.dp))

        AppStatusBadge(status = StatusType.NOT_ENTREGUE)
        Spacer(modifier = Modifier.height(8.dp))

        AppStatusBadge(status = StatusType.ATUAL)
        Spacer(modifier = Modifier.height(8.dp))

    }
}