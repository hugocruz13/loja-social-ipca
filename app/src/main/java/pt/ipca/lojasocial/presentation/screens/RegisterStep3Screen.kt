package pt.ipca.lojasocial.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppFilePickerField
import pt.ipca.lojasocial.presentation.components.AppNoteBox
import pt.ipca.lojasocial.presentation.components.AppProgressBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStep3Screen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    // Launcher para ficheiros
    var currentDocType by remember { mutableStateOf("") }
    val filePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                when (currentDocType) {
                    "identificacao" -> viewModel.updateStep3(it, state.docFamily, state.docMorada)
                    "agregado" -> viewModel.updateStep3(
                        state.docIdentification,
                        it,
                        state.docMorada
                    )

                    "morada" -> viewModel.updateStep3(state.docIdentification, state.docFamily, it)
                }
            }
        }

    Scaffold(
        topBar = { AppTopBar(title = "Registar", onBackClick = onBack) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AppProgressBar(
                currentStep = 3,
                totalSteps = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 16.dp)
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
                    description = "a) Documento de identificação do candidato",
                    fileName = state.docIdentification?.lastPathSegment,
                    onSelectFile = {
                        currentDocType = "identificacao"; filePickerLauncher.launch("*/*")
                    },
                    // errorMessage = if (state.docIdentification == null) "Obrigatório" else null
                )

                AppFilePickerField(
                    description = "b) Documento de identificação do agregado familiar",
                    fileName = state.docFamily?.lastPathSegment,
                    onSelectFile = { currentDocType = "agregado"; filePickerLauncher.launch("*/*") }
                )

                AppFilePickerField(
                    description = "c) Comprovativo de morada",
                    fileName = state.docMorada?.lastPathSegment,
                    onSelectFile = { currentDocType = "morada"; filePickerLauncher.launch("*/*") }
                )

                AppNoteBox(
                    text = "Nota: Para candidatos FAES, apenas os documentos a) e c) são obrigatórios.",
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Exibição de erro global do Firebase/Registo
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppButton(
                        text = "Anterior",
                        onClick = onBack,
                        containerColor = Color(0XFFC7C7C7),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            color = accentGreen
                        )
                    } else {
                        AppButton(
                            text = "Submeter",
                            onClick = { viewModel.register() },
                            enabled = state.isStep3Valid,
                            containerColor = if (state.isStep3Valid) accentGreen else Color(
                                0XFFC7C7C7
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        )
                    }
                }
            }
        }
    }
}