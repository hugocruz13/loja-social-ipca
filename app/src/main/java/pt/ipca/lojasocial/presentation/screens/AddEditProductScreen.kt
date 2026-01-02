package pt.ipca.lojasocial.presentation.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.*

@Composable
fun AddEditProductScreen(
    productId: String? = null,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)
    val context = LocalContext.current

    LaunchedEffect(productId) {
        if (productId != null) {
            name = "Arroz $productId"
            type = "Food Staples"
            quantity = "32"
            expiryDate = "31/12/2025"
            fileName = "arroz.jpg"
        } else {
            name = ""
            type = ""
            quantity = ""
            expiryDate = ""
            fileName = null
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fileName = context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: "Ficheiro selecionado"
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (productId == null) "Criar Produto" else "Editar Produto",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { onNavigate(it.route) }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AppTextField(
                label = "Nome do Produto",
                value = name,
                onValueChange = { name = it },
                placeholder = "ex: Arroz Agulha 1kg"
            )

            AppTextField(
                label = "Tipo",
                value = type,
                onValueChange = { type = it },
                placeholder = "ex: Food Staples"
            )

            AppTextField(
                label = "Quantidade",
                value = quantity,
                onValueChange = { value ->
                    if (value.all { it.isDigit() }) {
                        quantity = value
                    }
                },
                placeholder = "ex: 32",
                modifier = Modifier.fillMaxWidth()
            )

            AppDatePickerField(
                label = "Validade",
                selectedValue = expiryDate,
                onDateSelected = { expiryDate = it },
                placeholder = "dd/mm/aaaa",
                modifier = Modifier.fillMaxWidth()
            )

            AppFilePickerField(
                description = "Selecionar Foto do Produto",
                fileName = fileName,
                onSelectFile = { filePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppButton(
                text = if (productId == null) "Criar Produto" else "Guardar Alterações",
                onClick = onSaveClick,
                containerColor = accentGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}
