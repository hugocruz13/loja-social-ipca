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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.models.ProductType
import pt.ipca.lojasocial.presentation.viewmodels.ProductFormState
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import java.util.UUID

@Composable
fun AddNewProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (Product, Uri?) -> Unit,
    formState: ProductFormState, // Recebe o estado de erro do ViewModel
    onNameChange: (String, String) -> Unit, // Gatilho de validação do nome
    onTypeChange: (String, String) -> Unit, // Gatilho de validação do tipo
    viewModel: ProductViewModel = hiltViewModel()
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

                // NOME COM VALIDAÇÃO
                AppTextField(
                    label = "Nome do Produto",
                    value = name,
                    onValueChange = {
                        name = it
                        onNameChange(it, type.name) // Notifica o ViewModel para validar
                    },
                    placeholder = "Ex: Arroz 1kg",
                    // MOSTRA ERRO APENAS SE TOCADO
                    errorMessage = if (formState.nameTouched) formState.nameError else null
                )

                // TIPO COM VALIDAÇÃO
                AppDropdownField(
                    label = "Tipo",
                    selectedValue = type.name,
                    options = ProductType.values().map { it.name },
                    onOptionSelected = {
                        val selectedType = ProductType.valueOf(it)
                        type = selectedType
                        onTypeChange(it, name) // Notifica o ViewModel para validar
                    },
                    // MOSTRA ERRO APENAS SE TOCADO
                    errorMessage = if (formState.typeTouched) formState.typeError else null
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

                // BOTÃO REATIVO AO ESTADO DE VALIDAÇÃO
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
                    // SÓ HABILITA SE O FORMULÁRIO FOR VÁLIDO NO VIEWMODEL
                    enabled = formState.isFormValid,
                    containerColor = if (formState.isFormValid) Color(0XFF00713C) else Color(
                        0XFFC7C7C7
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    }
}