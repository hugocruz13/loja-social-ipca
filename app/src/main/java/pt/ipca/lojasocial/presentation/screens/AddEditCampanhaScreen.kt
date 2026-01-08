package pt.ipca.lojasocial.presentation.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.CampaignType
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppCampanhaAssociationSelector
import pt.ipca.lojasocial.presentation.components.AppDatePickerField
import pt.ipca.lojasocial.presentation.components.AppFilePickerField
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCampanhaScreen(
    campanhaId: String? = null,
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String, String, CampaignType, Uri?) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: CampanhasViewModel = hiltViewModel()
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var dataInicio by remember { mutableStateOf("") }
    var dataFim by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(CampaignType.INTERNAL) }
    var fileName by remember { mutableStateOf<String?>(null) }

    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val selectedCampanha by viewModel.selectedCampanha.collectAsState()

    val accentGreen = Color(0XFF00713C)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val formState by viewModel.uiState.collectAsState()

    LaunchedEffect(campanhaId) {
        if (campanhaId != null) {
            viewModel.loadCampanhaById(campanhaId)
        }
    }

    LaunchedEffect(selectedCampanha) {
        selectedCampanha?.let { camp ->
            nome = camp.nome
            descricao = camp.desc
            dataInicio = formatLongToString(camp.startDate)
            dataFim = formatLongToString(camp.endDate)
            selectedType = camp.type
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            fileName = context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: "Ficheiro selecionado"
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isSaveSuccess.collect { success ->
            if (success) {
                onBackClick()
            }
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
                onItemSelected = { item -> onNavigate(item.route) }
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
                onValueChange = {
                    nome = it
                    viewModel.onNomeChange(it, descricao, dataInicio, dataFim)
                },
                errorMessage = if (formState.nomeTouched) formState.nomeError else null,
                placeholder = "ex: Campanha Mercadona"
            )

            AppTextField(
                label = "Descrição",
                value = descricao,
                onValueChange = {
                    descricao = it
                },
                errorMessage = if (formState.descTouched) formState.descError else null,
                placeholder = "Introduza uma descrição...",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppDatePickerField(
                    label = "Data Ínicio",
                    selectedValue = dataInicio,
                    onDateSelected = {
                        dataInicio = it
                        viewModel.onDataInicioChange(it, nome, descricao, dataFim)
                    },
                    errorMessage = if (formState.dataInicioTouched) formState.dataInicioError else null,
                    modifier = Modifier.weight(1f)
                )
                AppDatePickerField(
                    label = "Data Fim",
                    selectedValue = dataFim,
                    onDateSelected = {
                        dataFim = it
                        viewModel.onDataFimChange(it, nome, descricao, dataInicio)
                    },
                    errorMessage = if (formState.dataFimTouched) formState.dataFimError else null,
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
                onClick = {
                    onSaveClick(
                        nome,
                        descricao,
                        dataInicio,
                        dataFim,
                        selectedType,
                        selectedImageUri
                    )
                },
                enabled = formState.isFormValid && !viewModel.isLoading.value,
                containerColor = if (formState.isFormValid) accentGreen else Color(0xFFC7C7C7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
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