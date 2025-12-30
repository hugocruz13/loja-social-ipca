package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class CampaignType { INTERNA, EXTERNA }

@Composable
fun AppCampanhaAssociationSelector(
    selectedType: CampaignType,
    onTypeSelected: (CampaignType) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {


    Column(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = "Associar a uma campanha?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SegmentButton(
                label = "Interna",
                isSelected = selectedType == CampaignType.INTERNA,
                onClick = { onTypeSelected(CampaignType.INTERNA) },
                accentColor = Color(0XFF00713C)
            )

            SegmentButton(
                label = "Externa",
                isSelected = selectedType == CampaignType.EXTERNA,
                onClick = { onTypeSelected(CampaignType.EXTERNA) },
                accentColor = Color(0XFF00713C)
            )
        }
    }
}

@Composable
private fun RowScope.SegmentButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.weight(1f),
        color = if (isSelected) Color.White else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
    ) {
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                containerColor = Color.Transparent
            )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppCampanhaAssociationSelectorInternaPreview() {
    val selected = remember { mutableStateOf(CampaignType.INTERNA) }

    Surface(modifier = Modifier.padding(16.dp)) {
        AppCampanhaAssociationSelector(
            selectedType = selected.value,
            onTypeSelected = { selected.value = it }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppCampanhaAssociationSelectorExternaPreview() {
    val selected = remember { mutableStateOf(CampaignType.EXTERNA) }

    Surface(modifier = Modifier.padding(16.dp)) {
        AppCampanhaAssociationSelector(
            selectedType = selected.value,
            onTypeSelected = { selected.value = it }
        )
    }
}