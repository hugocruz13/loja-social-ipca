package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStep1Screen(
    viewModel: AuthViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

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
                currentStep = 1,
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
                    text = "Identificação do Beneficiário",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // NOME COMPLETO
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
                    placeholder = "Ex: João Silva",
                    errorMessage = if (state.fullNameTouched) state.fullNameError else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // CARTÃO DE CIDADÃO
                AppTextField(
                    value = state.cc,
                    onValueChange = {
                        if (it.length <= 13) viewModel.updateStep1(
                            state.fullName,
                            it.uppercase(),
                            state.phone,
                            state.email,
                            state.password
                        )
                    },
                    label = "Cartão de Cidadão",
                    placeholder = "12345678 2ZX0",
                    errorMessage = if (state.ccTouched) state.ccError else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // DATA DE NASCIMENTO
                AppDatePickerField(
                    label = "Data de Nascimento",
                    selectedValue = state.birthDate,
                    onDateSelected = { newDate ->
                        viewModel.updateBirthDate(newDate)
                    },
                    placeholder = "dd/mm/yyyy",
                    enabled = true,
                    errorMessage = if (state.birthDateTouched) state.birthDateError else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // TELEMÓVEL
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
                    errorMessage = if (state.phoneTouched) state.phoneError else null,
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // EMAIL
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
                    placeholder = "exemplo@email.com",
                    errorMessage = if (state.emailTouched) state.emailError else null,
                    keyboardType = KeyboardType.Email,
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
                    placeholder = "8+ caracteres, Maíusc, Símbolo...",
                    errorMessage = if (state.passwordTouched) state.passwordError else null,
                    keyboardType = KeyboardType.Password,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppButton(
                    text = "Próximo",
                    onClick = onNext,
                    enabled = state.isStep1Valid,
                    containerColor = if (state.isStep1Valid) accentGreen else Color(0XFFC7C7C7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}