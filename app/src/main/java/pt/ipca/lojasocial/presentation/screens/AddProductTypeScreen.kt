package pt.ipca.lojasocial.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppFilePickerField
import pt.ipca.lojasocial.presentation.components.AppDropdownField
import pt.ipca.lojasocial.presentation.viewmodels.ProductTypeViewModel

@Composable
fun AddProductTypeScreen(
    onBackClick: () -> Unit,
    viewModel: ProductTypeViewModel = hiltViewModel()
) {
    // Estados do formulário
    var nome by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf("") }
    var observacoes by remember { mutableStateOf("") }

    // Estados para a Imagem
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }

    val accentGreen = Color(0XFF00713C)
    val isLoading by viewModel.isLoading.collectAsState()

    // Lista de categorias para o dropdown
    val listaCategorias = listOf("Alimentação", "Higiene Pessoal", "Limpeza", "Outros")

    // Launcher para selecionar imagem da galeria
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        fileName = if (uri != null) "imagem_produto.jpg" else null
    }

    // Navegação automática após sucesso no Firebase
    LaunchedEffect(viewModel.isSaveSuccess) {
        viewModel.isSaveSuccess.collect { success ->
            if (success) onBackClick()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Registar Novo Bem", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Informação do Bem",
                style = MaterialTheme.typography.titleLarge
            )

            // Seleção de foto
            AppFilePickerField(
                description = "Foto de Identificação",
                fileName = fileName,
                onSelectFile = { launcher.launch("image/*") }
            )

            // Nome
            AppTextField(
                label = "Nome do Produto",
                value = nome,
                onValueChange = { nome = it },
                placeholder = "Ex: Arroz, Massa, Atum..."
            )

            // Categoria (Dropdown customizado)
            AppDropdownField(
                label = "Categoria",
                selectedValue = categoriaSelecionada,
                options = listaCategorias,
                onOptionSelected = { categoriaSelecionada = it },
                placeholder = "Selecione a categoria"
            )

            // Observações
            AppTextField(
                label = "Observações",
                value = observacoes,
                onValueChange = { observacoes = it },
                placeholder = "Notas adicionais sobre este item...",
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botão de Gravação
            AppButton(
                text = "Registar Bem",
                onClick = {
                    if (nome.isNotBlank() && categoriaSelecionada.isNotBlank()) {
                        viewModel.saveProductType(
                            nome = nome,
                            tipo = categoriaSelecionada,
                            observacoes = observacoes,
                            imageUri = selectedImageUri
                        )
                    }
                },
                containerColor = accentGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(56.dp)
            )
        }
    }
}