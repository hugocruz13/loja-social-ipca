package pt.ipca.lojasocial.presentation.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.domain.models.CampaignType
import pt.ipca.lojasocial.presentation.components.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddEditCampanhaScreen(
    campanhaId: String? = null,
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String, String, CampaignType, Uri?) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var dataInicio by remember { mutableStateOf("") }
    var dataFim by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(CampaignType.INTERNAL) }
    var fileName by remember { mutableStateOf<String?>(null) }

    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val accentGreen = Color(0XFF00713C)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(campanhaId) {
        if (campanhaId != null) {
            nome = "Super Alimentação"
            descricao = "Campanha simulada para testes de edição."
            dataInicio = "01/12/2025"
            dataFim = "10/01/2026"
            selectedType = CampaignType.EXTERNAL
            fileName = "imagem_exemplo.jpg"
        } else {
            nome = ""
            descricao = ""
            dataInicio = ""
            dataFim = ""
            selectedType = CampaignType.EXTERNAL
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
                title = if (campanhaId == null) "Criar Campanha" else "Editar Campanha",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { item -> onNavigate(item.route)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppTextField(
                label = "Nome Campanha",
                value = nome,
                onValueChange = { nome = it },
                placeholder = "ex: Campanha Mercadona"
            )

            AppTextField(
                label = "Descrição",
                value = descricao,
                onValueChange = { descricao = it },
                placeholder = "Introduza uma descrição...",
                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppDatePickerField(
                    label = "Data Ínicio",
                    selectedValue = dataInicio,
                    onDateSelected = { dataInicio = it },
                    modifier = Modifier.weight(1f)
                )
                AppDatePickerField(
                    label = "Data Fim",
                    selectedValue = dataFim,
                    onDateSelected = { dataFim = it },
                    modifier = Modifier.weight(1f)
                )
            }

            AppCampanhaAssociationSelector(
                selectedType = selectedType,
                onTypeSelected = { selectedType = it }
            )

            AppFilePickerField(
                description = "Selecionar Foto para Campanha",
                fileName = fileName,
                onSelectFile = { filePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppButton(
                text = if (campanhaId == null) "Criar Campanha" else "Guardar Alterações",
                onClick = {onSaveClick(nome, descricao, dataInicio, dataFim, selectedType, selectedImageUri)},
                containerColor = accentGreen,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun formatLongToString(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return sdf.format(date)
}