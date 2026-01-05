package pt.ipca.lojasocial.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import coil.compose.AsyncImage


@Composable
fun BeneficiarioListItem(
    fullName: String,
    beneficiaryId: String,
    avatarUrl: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Avatar: se não houver URL, mostra icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0X3000713C)),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Icon",
                        tint = Color(0XFF00713C),
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Foto de perfil de $fullName",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ID: $beneficiaryId",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Ver Detalhes",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Item com Foto (Simulada)", showBackground = true)
@Composable
fun BeneficiarioListItemWithAvatarPreview() {

    Surface(modifier = Modifier.padding(16.dp)) {
        BeneficiarioListItem(
            fullName = "Joaquim Silva",
            beneficiaryId = "12345",
            avatarUrl = "https://randomuser.me/api/portraits/men/32.jpg",
            onClick = { }
        )
    }
}

@Preview(name = "Item com Fallback Icon", showBackground = true)
@Composable
fun BeneficiarioListItemFallbackPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        BeneficiarioListItem(
            fullName = "Leonardo Costa Monteiro",
            beneficiaryId = "98765",
            avatarUrl = null,
            onClick = { }
        )
    }
}