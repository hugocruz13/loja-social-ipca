package pt.ipca.lojasocial.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.net.Uri
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppFilePickerField
import pt.ipca.lojasocial.presentation.components.AppNoteBox
import pt.ipca.lojasocial.presentation.components.AppProgressBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.viewmodels.RegistrationState

@Composable
fun RegisterStep3Screen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val accentGreen = Color(0XFF00713C)
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()


    var currentDocType by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            when (currentDocType) {
                "identificacao" -> updateStep3Fields(viewModel, state, docIdentification = it)
                "agregado" -> updateStep3Fields(viewModel, state, docFamily = it)
                "morada" -> updateStep3Fields(viewModel, state, docMorada = it)
                "rendimento" -> updateStep3Fields(viewModel, state, docRendimento = it)
                "matricula" -> updateStep3Fields(viewModel, state, docMatricula = it)
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Registar", onBackClick = onBack) },
    ) { paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            AppProgressBar(
                currentStep = 3,
                totalSteps = 3,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {

                Text(
                    text = "Documentos a Entregar",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AppFilePickerField(
                    description = "a) Documento de indentificação do candidato",
                    fileName = state.docIdentification?.lastPathSegment,
                    onSelectFile = {
                        currentDocType = "identificacao"
                        filePickerLauncher.launch("*/*") // Abre qualquer tipo de ficheiro
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                AppFilePickerField(
                    description = "b) Documento de indentificação do agregado familiar",
                    fileName = state.docFamily?.lastPathSegment,
                    onSelectFile = {
                        currentDocType = "agregado"
                        filePickerLauncher.launch("*/*")
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                AppFilePickerField(
                    description = "c) Comprovativo de morada",
                    fileName = state.docMorada?.lastPathSegment,
                    onSelectFile = {
                        currentDocType = "morada"
                        filePickerLauncher.launch("*/*")
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                AppFilePickerField(
                    description = "d) Comprovativo de rendimentos",
                    fileName = state.docRendimento?.lastPathSegment,
                    onSelectFile = {
                        currentDocType = "rendimento"
                        filePickerLauncher.launch("*/*")
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                AppFilePickerField(
                    description = "e) Comprovativo de matrícula (estudantes internacionais)",
                    fileName = state.docMatricula?.lastPathSegment,
                    onSelectFile = {
                        currentDocType = "matricula"
                        filePickerLauncher.launch("*/*")
                    },
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                AppNoteBox(
                    text = "Para candidatos FAES, apenas os documentos a) e c) são obrigatórios.",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppButton(
                        text = "Anterior",
                        onClick = onBack,
                        containerColor = Color.LightGray,
                        enabled = true,
                        modifier = Modifier.weight(1f).height(56.dp)
                    )

                    AppButton(
                        text = "Submeter",
                        onClick = {
                            viewModel.register()
                            onRegisterSuccess()
                        },
                        enabled = viewModel.isStep3Valid(),
                        containerColor = if (viewModel.isStep3Valid()) accentGreen else Color(0XFFC7C7C7),
                        modifier = Modifier.weight(1f).height(56.dp)
                    )
                }
            }
        }
    }
}

private fun updateStep3Fields(
    viewModel: AuthViewModel,
    state: RegistrationState,
    docIdentification: Uri? = state.docIdentification,
    docFamily: Uri? = state.docFamily,
    docMorada: Uri? = state.docMorada,
    docRendimento: Uri? = state.docRendimento,
    docMatricula: Uri? = state.docMatricula
) {
    viewModel.updateStep3(
        docIdentification, docFamily, docMorada, docRendimento, docMatricula
    )
}