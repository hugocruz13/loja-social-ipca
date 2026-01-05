package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppDatePickerField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.AnosLetivosViewModel

@Composable
fun AddEditAnoLetivoScreen(
    anoLetivoId: String? = null,
    onBackClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: AnosLetivosViewModel = hiltViewModel()
) {
    val realId = remember(anoLetivoId) {
        if (anoLetivoId == "{id}" || anoLetivoId.isNullOrBlank()) null else anoLetivoId
    }


    var dataInicio by remember { mutableStateOf("") }
    var dataFim by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(realId == null) }

    val accentGreen = Color(0XFF00713C)
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(realId) {
        if (realId != null) {
            viewModel.loadAnoLetivoPorId(realId)
            isEditing = false
        }
    }

    LaunchedEffect(viewModel.dataInicioInput, viewModel.dataFimInput) {
        if (realId != null) {
            dataInicio = viewModel.dataInicioInput
            dataFim = viewModel.dataFimInput
        }
    }

    LaunchedEffect(viewModel.dataInicioInput, viewModel.dataFimInput) {
        if (realId != null) {
            dataInicio = viewModel.dataInicioInput
            dataFim = viewModel.dataFimInput
        }
    }

    LaunchedEffect(viewModel.isSaveSuccess) {
        viewModel.isSaveSuccess.collect { success ->
            if (success) onBackClick()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (realId == null) "Registar Ano Letivo" else "Detalhes",
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
                .padding(horizontal = 24.dp)
        ) {
            if (realId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (isEditing) Color.Red else accentGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Cancelar" else "Editar Dados",
                            color = if (isEditing) Color.Red else accentGreen,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppDatePickerField(
                    label = "Data Ínicio",
                    selectedValue = dataInicio,
                    onDateSelected = { dataInicio = it },
                    enabled = isEditing,
                    placeholder = "Selecionar Data"
                )

                AppDatePickerField(
                    label = "Data Fim",
                    selectedValue = dataFim,
                    onDateSelected = { dataFim = it },
                    enabled = isEditing,
                    placeholder = "Selecionar Data"
                )
            }

            if (isEditing) {
                AppButton(
                    text = if (realId == null) "Registar" else "Guardar Alterações",
                    onClick = {
                        viewModel.saveAnoLetivo(realId, dataInicio, dataFim)
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
}