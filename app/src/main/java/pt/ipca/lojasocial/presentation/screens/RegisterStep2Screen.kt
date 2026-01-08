package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.domain.models.EducationLevels
import pt.ipca.lojasocial.domain.models.RequestType
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppDropdownField
import pt.ipca.lojasocial.presentation.components.AppProgressBar
import pt.ipca.lojasocial.presentation.components.AppRadioCardItem
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStep2Screen(
    viewModel: AuthViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    // Helper function atualizada para disparar a validação do VM
    fun updateStep2Fields(
        category: RequestType? = state.requestCategory,
        education: String = state.educationLevel,
        dependents: Int = state.dependents,
        school: String = state.school,
        courseName: String = state.courseName,
        studentNumber: String = state.studentNumber
    ) {
        viewModel.updateStep2(category, education, dependents, school, courseName, studentNumber)
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
                currentStep = 2,
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
                    text = "Tipologia do Pedido",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Tipologia (RadioCards)
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    RequestType.entries.forEach { type ->
                        AppRadioCardItem(
                            label = when (type) {
                                RequestType.FOOD -> "Produtos Alimentares"
                                RequestType.HYGIENE -> "Produtos de Higiene Pessoal"
                                RequestType.CLEANING -> "Produtos de Limpeza"
                                RequestType.ALL -> "Todos"
                                else -> type.name
                            },
                            isSelected = state.requestCategory == type,
                            onClick = { updateStep2Fields(category = type) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Text(
                    text = "Dados Académicos",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AppTextField(
                    value = state.school,
                    onValueChange = { updateStep2Fields(school = it) },
                    label = "Instituição de Ensino",
                    placeholder = "Ex: IPCA",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                AppDropdownField(
                    label = "Nível de Ensino",
                    selectedValue = state.educationLevel,
                    options = EducationLevels.entries.map { it.name },
                    onOptionSelected = { updateStep2Fields(education = it) },
                    placeholder = "Selecione o nível",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                AppTextField(
                    value = state.courseName,
                    onValueChange = { updateStep2Fields(courseName = it) },
                    label = "Nome do Curso",
                    placeholder = "Ex: Engenharia de Sistemas",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                AppTextField(
                    value = state.studentNumber,
                    onValueChange = { updateStep2Fields(studentNumber = it) },
                    label = "Número do Estudante",
                    placeholder = "Insira o seu nº de estudante",
                    // errorMessage = state.studentNumberError, // Caso tenhas definido no VM
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppButton(
                        text = "Recuar",
                        onClick = onBack,
                        containerColor = Color(0XFFC7C7C7),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )
                    AppButton(
                        text = "Próximo",
                        onClick = onNext,
                        enabled = state.isStep2Valid, // Validação em tempo real
                        containerColor = if (state.isStep2Valid) accentGreen else Color(0XFFC7C7C7),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )
                }
            }
        }
    }
}