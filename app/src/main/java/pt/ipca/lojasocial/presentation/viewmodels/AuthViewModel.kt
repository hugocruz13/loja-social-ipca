package pt.ipca.lojasocial.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.RegistrationState
import pt.ipca.lojasocial.domain.models.RequestCategory
import pt.ipca.lojasocial.domain.use_cases.auth.RegisterBeneficiaryUseCase
import javax.inject.Inject

@HiltViewModel // <--- OBRIGATÓRIO PARA O HILT
class AuthViewModel @Inject constructor(
    // Injetamos o Use Case que trata de toda a lógica complexa (Auth + Storage + Firestore)
    private val registerBeneficiaryUseCase: RegisterBeneficiaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state

    // --- VALIDAÇÕES ---

    fun isStep1Valid(): Boolean {
        val s = _state.value
        // Validações básicas (podes refinar a lógica do CC e Telemóvel)
        return s.fullName.isNotBlank() &&
                s.cc.length >= 8 && // CC tem pelo menos 8 chars
                s.birthDate.isNotBlank() &&
                s.phone.length >= 9 &&
                s.email.contains("@") &&
                s.password.length >= 6
    }

    fun isStep2Valid(): Boolean {
        val s = _state.value
        // Dependendo do nível de ensino, alguns campos podem não ser obrigatórios
        return s.requestCategory != null &&
                s.educationLevel.isNotBlank() &&
                s.school.isNotBlank() &&
                s.studentNumber.isNotBlank()
    }

    fun isStep3Valid(): Boolean {
        val s = _state.value
        // Pelo menos a Identificação e a Morada costumam ser obrigatórias
        return s.docIdentification != null && s.docMorada != null
    }


    // --- ATUALIZAÇÕES DE ESTADO (INPUTS DA UI) ---

    fun updateStep1(fullName: String, cc: String, phone: String, email: String, password: String) {
        _state.update {
            it.copy(fullName = fullName, cc = cc, phone = phone, email = email, password = password)
        }
    }

    // Função específica para o DatePicker (geralmente é um componente separado)
    fun updateBirthDate(date: String) {
        _state.update { it.copy(birthDate = date) }
    }

    fun updateStep2(
        category: RequestCategory?,
        education: String,
        dependents: Int,
        school: String,
        courseName: String,
        studentNumber: String
    ) {
        _state.update {
            it.copy(
                requestCategory = category,
                educationLevel = education,
                dependents = dependents,
                school = school,
                courseName = courseName,
                studentNumber = studentNumber
            )
        }
    }

    fun updateStep3(
        docIdentification: Uri? = _state.value.docIdentification,
        docFamily: Uri? = _state.value.docFamily,
        docMorada: Uri? = _state.value.docMorada,
        docRendimento: Uri? = _state.value.docRendimento,
        docMatricula: Uri? = _state.value.docMatricula
    ) {
        _state.update {
            it.copy(
                docIdentification = docIdentification,
                docFamily = docFamily,
                docMorada = docMorada,
                docRendimento = docRendimento,
                docMatricula = docMatricula
            )
        }
    }

    // --- AÇÃO FINAL: REGISTAR ---

    fun register() {
        viewModelScope.launch {
            // 1. Iniciar Loading e limpar erros antigos
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // 2. Chamar o UseCase (A magia acontece aqui!)
                // Nota: O UseCase espera um RegistrationStateData.
                // Se o teu UseCase importar ESTA classe RegistrationState, passa direto.
                // Caso contrário, terás de mapear aqui. Assumindo que é a mesma classe:
                registerBeneficiaryUseCase(_state.value)

                // 3. Sucesso!
                _state.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {
                // 4. Erro (Mostra msg ao user)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Ocorreu um erro desconhecido."
                    )
                }
                e.printStackTrace()
            }
        }
    }

    // Método auxiliar para resetar o estado de erro/sucesso após navegação
    fun resetState() {
        _state.update { it.copy(isSuccess = false, errorMessage = null) }
    }
}