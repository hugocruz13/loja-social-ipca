package pt.ipca.lojasocial.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.models.ProductType
import java.util.UUID

@Composable
fun AddNewProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (Product, Uri?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ProductType.FOOD) }
    var observations by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
        }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F9FA)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Adicionar Produto",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B)
                )

                AppTextField(
                    label = "Nome do Produto",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Ex: Arroz 1kg"
                )

                AppDropdownField(
                    label = "Tipo",
                    selectedValue = type.name,
                    options = ProductType.values().map { it.name },
                    onOptionSelected = {
                        type = ProductType.valueOf(it)
                    }
                )

                AppTextField(
                    label = "Observações",
                    value = observations,
                    onValueChange = { observations = it },
                    placeholder = "Opcional"
                )

                AppFilePickerField(
                    description = "Imagem do Produto",
                    fileName = imageUri?.lastPathSegment,
                    onSelectFile = {
                        imagePickerLauncher.launch("image/*")
                    }
                )

                AppButton(
                    text = "Guardar",
                    onClick = {
                        onConfirm(
                            Product(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                type = type,
                                observations = observations.ifBlank { null }
                            ),
                            imageUri
                        )
                    },
                    enabled = name.isNotBlank(),
                    containerColor = Color(0XFF00713C),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    }
}
