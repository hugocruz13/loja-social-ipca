package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel
import pt.ipca.lojasocial.presentation.viewmodels.RequestCategory
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppDropdownField
import pt.ipca.lojasocial.presentation.components.AppProgressBar
import pt.ipca.lojasocial.presentation.components.AppRadioCardItem
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.viewmodels.educationLevels

@Composable
fun RegisterStep2Screen(
    viewModel: AuthViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {

    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    fun updateStep2Fields(
        category: RequestCategory? = state.requestCategory,
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
                    .padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp) // Padding lateral para o conteúdo
            ) {

                Text(
                    text = "Tipologia do Pedido",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Column(modifier = Modifier.padding(bottom = 24.dp)) {

                    AppRadioCardItem(
                        label = "Produtos Alimentares",
                        isSelected = state.requestCategory == RequestCategory.ALIMENTARES,
                        onClick = {
                            updateStep2Fields(category = RequestCategory.ALIMENTARES)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    AppRadioCardItem(
                        label = "Produtos de Higiene Pessoal",
                        isSelected = state.requestCategory == RequestCategory.HIGIENE,
                        onClick = {
                            updateStep2Fields(category = RequestCategory.HIGIENE)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    AppRadioCardItem(
                        label = "Produtos de Limpeza",
                        isSelected = state.requestCategory == RequestCategory.LIMPEZA,
                        onClick = {
                            updateStep2Fields(category = RequestCategory.LIMPEZA)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppRadioCardItem(
                        label = "Todos",
                        isSelected = state.requestCategory == RequestCategory.TODOS,
                        onClick = {
                            updateStep2Fields(category = RequestCategory.TODOS)
                        }
                    )

                }

                Text(
                    text = "Dados Académicos",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AppTextField(
                    value = state.school,
                    onValueChange = {
                        updateStep2Fields(school = it)
                    },
                    label = "Nome da Escola / Universidade",
                    placeholder = "Nome da Escola / Universidade",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                AppDropdownField(
                    label = "Nível de Ensino",
                    selectedValue = state.educationLevel,
                    options = educationLevels,
                    onOptionSelected = { novoNivel ->
                        updateStep2Fields(education = novoNivel)
                    },
                    placeholder = "Selecione nível de ensino",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                )

                AppTextField(
                    value = state.courseName,
                    onValueChange = {
                        updateStep2Fields(courseName = it)
                    },
                    label = "Nome do Curso",
                    placeholder = "Nome do Curso",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                AppTextField(
                    value = state.studentNumber,
                    onValueChange = {
                        updateStep2Fields(studentNumber = it)
                    },
                    label = "Número do Estudante",
                    placeholder = "Insira o seu nº de estudante",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                )



                Spacer(modifier = Modifier.weight(1f)) // Empurra o botão para baixo

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    AppButton(
                        text = "Recuar",
                        onClick = onBack,
                        containerColor = Color(0XFFC7C7C7),
                        enabled = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )

                    AppButton(
                        text = "Próximo",
                        onClick = onNext,
                        enabled = viewModel.isStep2Valid(),
                        containerColor = Color(0XFF00713C),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )
                }

            }
        }
    }
}