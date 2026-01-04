package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppDropdownField
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.viewmodels.StaffViewModel

@Composable
fun AddStaffScreen(
    onBackClick: () -> Unit,
    viewModel: StaffViewModel = hiltViewModel()
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("") }
    var permissao by remember { mutableStateOf("") }

    val listaPermissoes = listOf("Admin", "Colaborador", "Voluntário")
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(viewModel.isSaveSuccess) {
        viewModel.isSaveSuccess.collect { success ->
            if (success) onBackClick()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Novo Colaborador", onBackClick = onBackClick)
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
                text = "Dados do Colaborador",
                style = MaterialTheme.typography.titleLarge
            )

            AppTextField(
                label = "Nome Completo",
                value = nome,
                onValueChange = { nome = it },
                placeholder = "Nome do colaborador"
            )

            AppTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "exemplo@email.com"
            )

            AppTextField(
                label = "Cargo / Função",
                value = cargo,
                onValueChange = { cargo = it },
                placeholder = "Ex: Gestor de Armazém"
            )

            AppDropdownField(
                label = "Nível de Permissão",
                selectedValue = permissao,
                options = listaPermissoes,
                onOptionSelected = { permissao = it },
                placeholder = "Selecione a permissão"
            )

            Surface(
                color = Color(0xFFFFF3CD),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nota: A password inicial será '123456'. O colaborador deverá alterá-la no primeiro acesso.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF856404)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                text = "Criar Colaborador",
                onClick = {
                    if (nome.isNotBlank() && email.isNotBlank() && permissao.isNotBlank()) {
                        viewModel.registarColaborador(nome, email, cargo, permissao)
                    }
                },
                containerColor = Color(0XFF00713C),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(56.dp)
            )
        }
    }
}