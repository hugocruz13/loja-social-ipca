package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val accentGreen = Color(0XFF00713C)

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp // Look mais moderno e compacto
                )
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                    contentDescription = "Voltar",
                    tint = accentGreen,
                    modifier = Modifier.size(20.dp) // Ícone ligeiramente menor para elegância
                )
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            scrolledContainerColor = Color.White.copy(alpha = 0.95f),
            titleContentColor = Color(0xFF1A1C1E),
            navigationIconContentColor = accentGreen
        )
    )
}

/**
 * Versão específica para detalhes com botão de excluir estilizado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailTopBar(
    title: String,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AppTopBar(
        title = title,
        onBackClick = onBackClick,
        actions = {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Excluir",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    MaterialTheme {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Registo de Stock",
                    onBackClick = { }
                )
            }
        ) { paddingValues ->
            Text(
                text = "Conteúdo com TopBar Moderna",
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            )
        }
    }
}