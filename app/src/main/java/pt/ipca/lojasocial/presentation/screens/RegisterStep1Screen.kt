package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppDatePickerField
import pt.ipca.lojasocial.presentation.components.AppProgressBar
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel

@Composable
fun RegisterStep1Screen(
    viewModel: AuthViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { AppTopBar(title = "Registar", onBackClick = onBack) },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AppProgressBar(
                currentStep = 1,
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
                    text = "Identificação do Beneficiário",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AppTextField(
                    value = state.fullName,
                    onValueChange = {
                        viewModel.updateStep1(
                            it,
                            state.cc,
                            state.phone,
                            state.email,
                            state.password
                        )
                    },
                    label = "Nome Completo",
                    placeholder = "Insira o seu nome",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                AppTextField(
                    value = state.cc,
                    onValueChange = {
                        if (it.length <= 14) viewModel.updateStep1(
                            state.fullName,
                            it,
                            state.phone,
                            state.email,
                            state.password
                        )
                    },
                    label = "Cartão de Cidadão",
                    placeholder = "000000000 ZZ0",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // CORREÇÃO: Agora usa o state.birthDate e chama viewModel.updateBirthDate
                AppDatePickerField(
                    label = "Data de Nascimento",
                    selectedValue = state.birthDate,
                    onDateSelected = { newDate ->
                        viewModel.updateBirthDate(newDate)
                    },
                    placeholder = "dd/mm/yyyy",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                AppTextField(
                    value = state.phone,
                    onValueChange = {
                        if (it.length <= 9) viewModel.updateStep1(
                            state.fullName,
                            state.cc,
                            it,
                            state.email,
                            state.password
                        )
                    },
                    label = "Telemóvel",
                    placeholder = "9XX XXX XXX",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                AppTextField(
                    value = state.email,
                    onValueChange = {
                        viewModel.updateStep1(
                            state.fullName,
                            state.cc,
                            state.phone,
                            it,
                            state.password
                        )
                    },
                    label = "Email",
                    placeholder = "seumemail@email.com",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                AppTextField(
                    value = state.password,
                    onValueChange = {
                        viewModel.updateStep1(
                            state.fullName,
                            state.cc,
                            state.phone,
                            state.email,
                            it
                        )
                    },
                    label = "Password",
                    placeholder = "Introduza a sua password",
                    keyboardType = KeyboardType.Password,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                AppButton(
                    text = "Próximo",
                    onClick = onNext,
                    enabled = viewModel.isStep1Valid(),
                    containerColor = Color(0XFF00713C),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}