package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface

@Composable
fun AdicionarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color(0XFF00713C),
        contentColor = Color.White,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Adicionar Novo Item",
            modifier = Modifier.size(24.dp)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun AppFloatingActionButtonPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AdicionarButton(
            onClick = { println("Botão Adicionar Clicado") }
        )
    }
}