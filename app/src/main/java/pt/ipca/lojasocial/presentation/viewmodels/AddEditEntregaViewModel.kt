package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.repository.ProductRepository
import pt.ipca.lojasocial.domain.use_cases.auth.GetCurrentUserUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class AddEditEntregaUiState(
    val deliveryId: String? = null,
    val beneficiaryQuery: String = "",
    val searchedBeneficiaries: List<Beneficiary> = emptyList(),
    val selectedBeneficiary: Beneficiary? = null,
    val date: String = "",
    val time: String = "",
    val repetition: String = "Não repetir",
    val observations: String = "",
    val availableProducts: List<Product> = emptyList(),
    val selectedProducts: Map<String, Int> = emptyMap(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class AddEditEntregaViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val productRepository: ProductRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditEntregaUiState())
    val uiState: StateFlow<AddEditEntregaUiState> = _uiState.asStateFlow()

    init {
        loadAvailableProducts()
    }

    fun loadDelivery(deliveryId: String) {
        viewModelScope.launch {
            val delivery = deliveryRepository.getDeliveryById(deliveryId)
            if (delivery != null) {
                val beneficiary = beneficiaryRepository.getBeneficiaryById(delivery.beneficiaryId)
                val calendar = Calendar.getInstance().apply { timeInMillis = delivery.scheduledDate }
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                _uiState.update {
                    it.copy(
                        deliveryId = deliveryId,
                        selectedBeneficiary = beneficiary,
                        beneficiaryQuery = beneficiary?.name ?: "",
                        date = simpleDateFormat.format(calendar.time),
                        time = timeFormat.format(calendar.time),
                        repetition = "Não repetir", // Repetition is not stored, so default to this when editing
                        observations = delivery.observations ?: "",
                        selectedProducts = delivery.items ?: emptyMap()
                    )
                }
            }
        }
    }


    private fun loadAvailableProducts() {
        viewModelScope.launch {
            // Updated to call getProducts() and filter items with quantity > 0
            val products = productRepository.getProducts().filter { it.quantity > 0 }
            _uiState.update { it.copy(availableProducts = products) }
        }
    }

    fun onBeneficiaryQueryChange(query: String) {
        _uiState.update { it.copy(beneficiaryQuery = query) }
        if (query.length > 2) {
            viewModelScope.launch {
                val beneficiaries = beneficiaryRepository.getBeneficiaries()
                    .filter { it.name.contains(query, ignoreCase = true) }
                _uiState.update { it.copy(searchedBeneficiaries = beneficiaries) }
            }
        } else {
            _uiState.update { it.copy(searchedBeneficiaries = emptyList()) }
        }
    }

    fun onBeneficiarySelected(beneficiary: Beneficiary) {
        _uiState.update {
            it.copy(
                selectedBeneficiary = beneficiary,
                beneficiaryQuery = beneficiary.name,
                searchedBeneficiaries = emptyList()
            )
        }
    }

    fun onDateChange(date: String) {
        _uiState.update { it.copy(date = date) }
    }

    fun onTimeChange(time: String) {
        _uiState.update { it.copy(time = time) }
    }

    fun onRepetitionChange(repetition: String) {
        _uiState.update { it.copy(repetition = repetition) }
    }

    fun onObservationsChange(observations: String) {
        _uiState.update { it.copy(observations = observations) }
    }

    fun onProductQuantityChange(productId: String, quantity: Int) {
        val updatedProducts = _uiState.value.selectedProducts.toMutableMap()
        if (quantity > 0) {
            updatedProducts[productId] = quantity
        } else {
            updatedProducts.remove(productId)
        }
        _uiState.update { it.copy(selectedProducts = updatedProducts) }
    }

    fun onAddProduct() {
        val currentSelectedIds = _uiState.value.selectedProducts.keys
        val productToAdd = _uiState.value.availableProducts.find { it.id !in currentSelectedIds }
        if (productToAdd != null) {
            onProductQuantityChange(productToAdd.id, 1)
        }
    }

    fun saveDelivery() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val currentState = _uiState.value
            val currentUser = getCurrentUserUseCase()

            if (currentState.selectedBeneficiary == null || currentUser == null) {
                _uiState.update { it.copy(isSaving = false) }
                return@launch
            }

            val calendar = Calendar.getInstance()
            // Here you would parse date and time from currentState.date and currentState.time
            // For simplicity, using current time for new deliveries.
            // val scheduledDate = ...

            val deliveriesToCreate = mutableListOf<Delivery>()
            val repetition = currentState.repetition

            val baseDelivery = Delivery(
                id = currentState.deliveryId ?: "",
                beneficiaryId = currentState.selectedBeneficiary.id,
                date = System.currentTimeMillis(),
                scheduledDate = calendar.timeInMillis, // Should be parsed from UI state
                status = DeliveryStatus.SCHEDULED,
                items = currentState.selectedProducts,
                observations = currentState.observations,
                createdBy = currentUser.id
            )

            if (currentState.deliveryId != null) {
                // Update existing delivery
                //TODO()
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                return@launch
            }


            deliveriesToCreate.add(baseDelivery.copy(id = UUID.randomUUID().toString(), scheduledDate = calendar.timeInMillis))

            if (repetition != "Não repetir") {
                val repeatCount = when (repetition) {
                    "Semanalmente" -> 25
                    "Mensalmente" -> 6
                    "Semestral" -> 1
                    else -> 0
                }

                for (i in 1..repeatCount) {
                    when (repetition) {
                        "Semanalmente" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                        "Mensalmente" -> calendar.add(Calendar.MONTH, 1)
                        "Semestral" -> calendar.add(Calendar.MONTH, 6)
                    }
                    deliveriesToCreate.add(
                        baseDelivery.copy(
                            id = UUID.randomUUID().toString(),
                            scheduledDate = calendar.timeInMillis
                        )
                    )
                }
            }

            try {
                deliveriesToCreate.forEach { delivery ->
                    deliveryRepository.addDelivery(delivery)
                }
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
