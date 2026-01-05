package pt.ipca.lojasocial.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@Composable
fun AppBottomBar(
    navItems: List<BottomNavItem>,
    currentRoute: String?,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val brandColor = Color(0XFF00713C)

    // Configurações de Dimensão
    val barHeight = 64.dp
    val popUpHeight = 16.dp
    val totalHeight = barHeight + popUpHeight

    Box(
        modifier = modifier
            .fillMaxWidth()
            // ALTERAÇÃO AQUI: Adicionei padding no fundo para subir a barra
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .height(totalHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        // CAMADA 1: Fundo Branco
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(50),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                ),
            color = Color.White,
            shape = RoundedCornerShape(50)
        ) {
            // Vazio
        }

        // CAMADA 2: Ícones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route

                // Animações
                val animSpec = tween<androidx.compose.ui.unit.Dp>(300, easing = FastOutSlowInEasing)

                val offsetY by animateDpAsState(
                    if (isSelected) -popUpHeight else 0.dp,
                    animSpec,
                    label = "y"
                )
                val bgSize by animateDpAsState(
                    if (isSelected) 50.dp else 0.dp,
                    animSpec,
                    label = "size"
                )
                val iconSize by animateDpAsState(
                    if (isSelected) 28.dp else 24.dp,
                    animSpec,
                    label = "icon"
                )

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) brandColor else Color(0xFF94A3B8),
                    animationSpec = tween(300),
                    label = "color"
                )

                // Item Individual
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemSelected(item) },
                    contentAlignment = Alignment.Center
                ) {
                    // Grupo que sobe (Bola + Ícone)
                    Box(
                        modifier = Modifier.offset(y = offsetY),
                        contentAlignment = Alignment.Center
                    ) {
                        // Bola Verde
                        Box(
                            modifier = Modifier
                                .size(bgSize)
                                .background(
                                    color = if (isSelected) brandColor.copy(alpha = 0.15f) else Color.Transparent,
                                    shape = CircleShape
                                )
                        )

                        // Ícone
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
            }
        }
    }
}