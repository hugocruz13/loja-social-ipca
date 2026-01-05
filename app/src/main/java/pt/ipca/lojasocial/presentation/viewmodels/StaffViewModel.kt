package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.use_cases.staff.AddStaffUseCase
import pt.ipca.lojasocial.domain.use_cases.staff.GetStaffUseCase
import pt.ipca.lojasocial.domain.use_cases.staff.ToggleStaffStatusUseCase
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val getStaffUseCase: GetStaffUseCase,
    private val addStaffUseCase: AddStaffUseCase,
    private val toggleStaffStatusUseCase: ToggleStaffStatusUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaveSuccess = MutableSharedFlow<Boolean>()
    val isSaveSuccess = _isSaveSuccess.asSharedFlow()

    val colaboradores = getStaffUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun registarColaborador(nome: String, email: String, cargo: String, permissao: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addStaffUseCase(nome, email, cargo, permissao)
                _isSaveSuccess.emit(true)
            } catch (e: Exception) {
                android.util.Log.e("STAFF_ERROR", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleStatus(uid: String, currentStatus: Boolean) {
        viewModelScope.launch {
            try {
                val nomeColaborador =
                    colaboradores.value.find { it.uid == uid }?.nome ?: "Desconhecido"
                toggleStaffStatusUseCase(uid, currentStatus, nomeColaborador)
            } catch (e: Exception) {
                android.util.Log.e("STAFF_LOG_ERROR", e.message.toString())
            }
        }
    }
}