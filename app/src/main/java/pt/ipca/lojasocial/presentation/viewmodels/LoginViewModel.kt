package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.models.User
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.domain.use_cases.auth.LoginUserUseCase
import pt.ipca.lojasocial.domain.use_cases.beneficiary.GetBeneficiaryByIdUseCase
import pt.ipca.lojasocial.domain.use_cases.beneficiary.GetBeneficiaryByUidUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val getBeneficiaryByIdUseCase: GetBeneficiaryByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            loginUserUseCase(email, password)
                .onSuccess { user ->
                    when (user.role) {
                        UserRole.STAFF -> {
                            _uiState.value = LoginUiState.SuccessStaff(user)
                        }
                        UserRole.BENEFICIARY -> {
                            // Após o login do beneficiário, vamos buscar o seu status
                            try {
                                val beneficiary = getBeneficiaryByIdUseCase(user.id)
                                _uiState.value = LoginUiState.SuccessBeneficiary(user,
                                    beneficiary?.status ?: BeneficiaryStatus.INATIVO
                                )
                            } catch (e: Exception) {
                                // Se não encontrar o perfil de beneficiário, assume PENDENTE
                                _uiState.value = LoginUiState.SuccessBeneficiary(user, BeneficiaryStatus.INATIVO)
                            }
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(
                        error.message ?: "Erro desconhecido"
                    )
                }
        }
    }
}

sealed interface LoginUiState {
    object Initial : LoginUiState
    object Loading : LoginUiState
    data class SuccessStaff(val user: User) : LoginUiState
    // Adicionamos o status ao estado de sucesso do beneficiário
    data class SuccessBeneficiary(val user: User, val status: BeneficiaryStatus) : LoginUiState
    data class Error(val message: String) : LoginUiState
}