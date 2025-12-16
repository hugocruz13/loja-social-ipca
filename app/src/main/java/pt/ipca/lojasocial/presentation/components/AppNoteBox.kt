package pt.ipca.lojasocial.presentation.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun AppNoteBox(
    text: String,
    modifier: Modifier = Modifier
) {
    // Cor de destaque (Verde) e cor de fundo clara
    val accentGreen = Color(0XFF00713C)
    val lightGreenBackground = accentGreen.copy(alpha = 0.1f)

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightGreenBackground,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, accentGreen),
        modifier = modifier
    ) {
        Text(
            text = buildAnnotatedString {
                // Aplica negrito apenas à palavra "Nota:"
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Nota: ")
                }
                append(text)
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}