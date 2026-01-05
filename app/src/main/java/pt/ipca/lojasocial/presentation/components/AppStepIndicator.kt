package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppProgressBar(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val progress = (currentStep.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)

    Column(modifier = modifier.padding(vertical = 16.dp)) {


        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
        ) {
            drawRect(Color(0xFFDBE0E6))
            drawRect(
                Color(0xFF00713C),
                size = Size(size.width * progress, size.height)
            )
        }
    }
}


@Preview(name = "Progresso 1/3", showBackground = true)
@Composable
fun AppProgressBarStep1Preview() {
    AppProgressBar(totalSteps = 3, currentStep = 1)
}

@Preview(name = "Progresso 2/3", showBackground = true)
@Composable
fun AppProgressBarStep2Preview() {
    AppProgressBar(totalSteps = 3, currentStep = 2)
}

@Preview(name = "Progresso 3/3", showBackground = true)
@Composable
fun AppProgressBarStep3Preview() {
    AppProgressBar(totalSteps = 3, currentStep = 3)
}