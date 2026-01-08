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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val badgeCount: Int = 0
)

@Composable
fun AppBottomBar(
    navItems: List<BottomNavItem>,
    currentRoute: String?,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
    fabContent: @Composable (() -> Unit)? = null // Injeção do botão acoplado
) {
    val brandColor = Color(0XFF00713C)
    val barHeight = 64.dp
    val popUpHeight = 12.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
            .height(barHeight + popUpHeight),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- PARTE 1: A BARRA DE NAVEGAÇÃO ---
        Surface(
            modifier = Modifier
                .weight(1f) // Encolhe para dar espaço ao botão
                .height(barHeight)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(50),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                ),
            color = Color.White,
            shape = RoundedCornerShape(50)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val animSpec =
                        tween<androidx.compose.ui.unit.Dp>(300, easing = FastOutSlowInEasing)

                    val offsetY by animateDpAsState(
                        if (isSelected) -popUpHeight else 0.dp,
                        animSpec
                    )
                    val bgSize by animateDpAsState(if (isSelected) 44.dp else 0.dp, animSpec)
                    val iconColor by animateColorAsState(
                        if (isSelected) brandColor else Color(
                            0xFF94A3B8
                        ), tween(300)
                    )

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onItemSelected(item) },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.offset(y = offsetY),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(bgSize)
                                    .background(
                                        if (isSelected) brandColor.copy(alpha = 0.1f) else Color.Transparent,
                                        CircleShape
                                    )
                            )
                            Box {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    tint = iconColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                if (item.badgeCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 4.dp, y = (-2).dp)
                                            .sizeIn(minWidth = 16.dp, minHeight = 16.dp)
                                            .background(Color.Red, CircleShape)
                                            .padding(horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (item.badgeCount > 9) "+9" else item.badgeCount.toString(),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- PARTE 2: O BOTÃO ACOPLADO ---
        fabContent?.let {
            Box(
                modifier = Modifier
                    .size(barHeight) // Perfeitamente alinhado com a altura da barra
                    .shadow(10.dp, CircleShape, spotColor = brandColor.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                it()
            }
        }
    }
}