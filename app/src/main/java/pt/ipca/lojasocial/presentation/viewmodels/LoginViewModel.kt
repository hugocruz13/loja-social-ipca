package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.User
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.domain.use_cases.auth.LoginUserUseCase
import javax.inject.Inject

//--------------------------------------------
// Snippet de código exemplo com a lógica de navegaçao para o uiState entre Staff e Beneficiary
//---------------------------------------------
//when (val state = loginState) {
//    is LoginUiState.SuccessStaff -> {
//        navController.navigate("staff_dashboard") {
//            popUpTo("login") { inclusive = true }
//        }
//    }
//    is LoginUiState.SuccessBeneficiary -> {
//        navController.navigate("beneficiary_home") {
//            popUpTo("login") { inclusive = true }
//        }
//    }
//    is LoginUiState.Error -> {
//        // Mostrar erro
//    }
//    else -> { /* ... */ }
//}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            loginUserUseCase(email, password)
                .onSuccess { user ->
                    _uiState.value = when (user.role) {
                        UserRole.STAFF -> LoginUiState.SuccessStaff(user)
                        UserRole.BENEFICIARY -> LoginUiState.SuccessBeneficiary(user)
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
    data class SuccessBeneficiary(val user: User) : LoginUiState
    data class Error(val message: String) : LoginUiState
}