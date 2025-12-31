package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AddEditEntregaState(
    val isLoading: Boolean = false,
    val entregaId: String? = null,
    val beneficiaryName: String = "",
    val selectedDate: Long? = null, // Exemplo para data
    val selectedTime: String = "", // Exemplo para hora
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddEditEntregaViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // Adicione aqui os seus UseCases, por exemplo:
    // private val getEntregaUseCase: GetEntregaUseCase,
    // private val saveEntregaUseCase: SaveEntregaUseCase
): ViewModel(){

}