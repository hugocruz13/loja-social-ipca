package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.use_cases.auth.GetCurrentUserUseCase
import javax.inject.Inject

@HiltViewModel
class EntregasViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _deliveries = MutableStateFlow<List<Delivery>>(emptyList())
    val deliveries: StateFlow<List<Delivery>> = _deliveries

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadDeliveries()
    }

    private fun loadDeliveries() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val currentUser = getCurrentUserUseCase()
                if (currentUser == null) {
                    _error.value = "Utilizador não autenticado."
                    _isLoading.value = false
                    return@launch
                }

                val result = when (currentUser.role) {
                    UserRole.STAFF -> deliveryRepository.getDeliveries()
                    UserRole.BENEFICIARY -> deliveryRepository.getDeliveriesByBeneficiary(currentUser.id)
                }
                _deliveries.value = result
            } catch (e: Exception) {
                _error.value = "Falha ao carregar as entregas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}