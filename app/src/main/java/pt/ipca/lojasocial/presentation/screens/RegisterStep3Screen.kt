package pt.ipca.lojasocial.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import pt.ipca.lojasocial.presentation.state.AuthState
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel

@Composable
fun RegisterStep3Screen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val accentGreen = Color(0XFF00713C)
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // --- REAÇÃO AO SUCESSO DO REGISTO ---
    // Observa o estado: Se isSuccess mudar para true, navega para fora.
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
            viewModel.resetState() // Limpa o estado para a próxima vez
        }
    }

    var currentDocType by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            when (currentDocType) {
                "identificacao" -> updateStep3Fields(viewModel, state, docIdentification = it)
                "agregado" -> updateStep3Fields(viewModel, state, docFamily = it)
                "morada" -> updateStep3Fields(viewModel, state, docMorada = it)
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Registar", onBackClick = onBack) },
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
                    .padding(bottom = 16.dp)
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
                        filePickerLauncher.launch("*/*")
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // ... (Podes manter os outros campos iguais) ...
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

                // ... (restantes campos de ficheiro) ...

                AppNoteBox(
                    text = "Para candidatos FAES, apenas os documentos a) e c) são obrigatórios.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // --- ÁREA DE ERRO ---
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage ?: "Erro desconhecido",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // --- BOTÕES ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically // Alinhamento vertical
                ) {
                    AppButton(
                        text = "Anterior",
                        onClick = onBack,
                        containerColor = Color.LightGray,
                        enabled = !state.isLoading, // Desativa se estiver a carregar
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )

                    if (state.isLoading) {
                        // Mostra Spinner se estiver a carregar
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = accentGreen)
                        }
                    } else {
                        // Mostra Botão se estiver parado
                        AppButton(
                            text = "Submeter",
                            onClick = {
                                viewModel.register()
                                // Nota: Não chamamos onRegisterSuccess() aqui!
                                // O LaunchedEffect lá em cima trata disso.
                            },
                            enabled = viewModel.isStep3Valid(),
                            containerColor = if (viewModel.isStep3Valid()) accentGreen else Color(
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

// Função auxiliar mantida fora (ou podes colocar dentro do composable se preferires)
private fun updateStep3Fields(
    viewModel: AuthViewModel,
    state: AuthState,
    docIdentification: Uri? = state.docIdentification,
    docFamily: Uri? = state.docFamily,
    docMorada: Uri? = state.docMorada
) {
    viewModel.updateStep3(
        docIdentification, docFamily, docMorada
    )
}