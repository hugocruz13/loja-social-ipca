package pt.ipca.lojasocial.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.models.Stock
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.repository.ProductRepository
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import pt.ipca.lojasocial.domain.repository.StockRepository
import pt.ipca.lojasocial.domain.use_cases.auth.GetCurrentUserUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

private const val TAG = "AddEditEntregaViewModel"

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
    val availableStockItems: List<Stock> = emptyList(),
    val productStockLimits: Map<String, Int> = emptyMap(), // ProductID -> Total Quantity
    val selectedProducts: Map<String, Int> = emptyMap(), // ProductID -> Quantity
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isProductPickerDialogVisible: Boolean = false,
    val isDatePickerDialogVisible: Boolean = false,
    val isTimePickerDialogVisible: Boolean = false,
    val isImmediateDelivery: Boolean = false
)

@HiltViewModel
class AddEditEntregaViewModel @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val schoolYearRepository: SchoolYearRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditEntregaUiState())
    val uiState: StateFlow<AddEditEntregaUiState> = _uiState.asStateFlow()

    init {
        loadAvailableProductsAndStock()
        checkUserRoleAndSetup()
    }

    private fun checkUserRoleAndSetup() {
        viewModelScope.launch {
            try {
                val currentUser = getCurrentUserUseCase()
                if (currentUser?.role == UserRole.BENEFICIARY) {
                    val beneficiary = beneficiaryRepository.getBeneficiaryById(currentUser.id)
                    if (beneficiary != null) {
                        _uiState.update {
                            it.copy(
                                selectedBeneficiary = beneficiary,
                                beneficiaryQuery = beneficiary.name // Optional: display name
                            )
                        }
                        Log.d(TAG, "Auto-selected beneficiary: ${beneficiary.name}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking user role for auto-selection", e)
            }
        }
    }

    fun loadDelivery(deliveryId: String) {
        viewModelScope.launch {
            try {
                val delivery = deliveryRepository.getDeliveryById(deliveryId)
                if (delivery != null) {
                    val beneficiary =
                        beneficiaryRepository.getBeneficiaryById(delivery.beneficiaryId)
                    val calendar =
                        Calendar.getInstance().apply { timeInMillis = delivery.scheduledDate }
                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                    val dateStr = simpleDateFormat.format(calendar.time)
                    _uiState.update {
                        it.copy(
                            deliveryId = deliveryId,
                            selectedBeneficiary = beneficiary,
                            beneficiaryQuery = beneficiary?.name ?: "",
                            date = dateStr,
                            time = timeFormat.format(calendar.time),
                            repetition = "Não repetir",
                            observations = delivery.observations ?: "",
                            selectedProducts = delivery.items, // Assuming items are ProductID -> Quantity
                            isImmediateDelivery = isDateToday(dateStr)
                        )
                    }
                } else {
                    Log.w(TAG, "loadDelivery: No delivery found with ID: $deliveryId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadDelivery: Error loading delivery", e)
            }
        }
    }


    private fun loadAvailableProductsAndStock() {
        viewModelScope.launch {
            try {
                val stockItems = stockRepository.getStockItems().filter { it.quantity > 0 }
                val allProducts = productRepository.getProducts()

                val stockLimits = stockItems
                    .groupBy { it.productId }
                    .mapValues { (_, items) -> items.sumOf { it.quantity } }

                val availableProductIds = stockItems.map { it.productId }.toSet()
                val availableProducts = allProducts.filter { it.id in availableProductIds }

                _uiState.update {
                    it.copy(
                        availableStockItems = stockItems,
                        availableProducts = availableProducts,
                        productStockLimits = stockLimits
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadAvailableProductsAndStock: Error loading data", e)
            }
        }
    }

    fun onBeneficiaryQueryChange(query: String) {
        _uiState.update { it.copy(beneficiaryQuery = query) }
        if (query.length > 2) {
            viewModelScope.launch {
                try {
                    val beneficiaries = beneficiaryRepository.getBeneficiaries()
                        .filter { it.name.contains(query, ignoreCase = true) }
                    _uiState.update { it.copy(searchedBeneficiaries = beneficiaries) }
                } catch (e: Exception) {
                    Log.e(TAG, "onBeneficiaryQueryChange: Error searching beneficiaries", e)
                }
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
        val isToday = isDateToday(date)
        _uiState.update { it.copy(date = date, isImmediateDelivery = isToday) }
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

    fun showProductPickerDialog() {
        _uiState.update { it.copy(isProductPickerDialogVisible = true) }
    }

    fun hideProductPickerDialog() {
        _uiState.update { it.copy(isProductPickerDialogVisible = false) }
    }

    fun showDatePickerDialog() {
        _uiState.update { it.copy(isDatePickerDialogVisible = true) }
    }

    fun hideDatePickerDialog() {
        _uiState.update { it.copy(isDatePickerDialogVisible = false) }
    }

    fun showTimePickerDialog() {
        _uiState.update { it.copy(isTimePickerDialogVisible = true) }
    }

    fun hideTimePickerDialog() {
        _uiState.update { it.copy(isTimePickerDialogVisible = false) }
    }

    private fun isDateToday(dateString: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val today = sdf.format(Date())
            dateString == today
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun deductStock(items: Map<String, Int>) {
        try {
            val allStock = stockRepository.getStockItems()
            items.forEach { (productId, qtyToDeduct) ->
                var remaining = qtyToDeduct
                // FEFO: Sort by expiryDate ascending
                val relevantStock = allStock
                    .filter { it.productId == productId && it.quantity > 0 }
                    .sortedBy { it.expiryDate }

                for (stock in relevantStock) {
                    if (remaining <= 0) break
                    val take = kotlin.math.min(remaining, stock.quantity)
                    stockRepository.updateStockQuantity(stock.id, stock.quantity - take)
                    remaining -= take
                }
                
                if (remaining > 0) {
                    Log.w(TAG, "Stock insufficiency for product $productId. Missing: $remaining")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deducting stock", e)
        }
    }

    fun saveDelivery() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val currentState = _uiState.value
            val currentUser = getCurrentUserUseCase()
            val isEditing = currentState.deliveryId != null

            if (currentState.selectedBeneficiary == null || currentUser == null || currentState.selectedProducts.isEmpty() || currentState.date.isBlank() || currentState.time.isBlank()) {
                Log.w(
                    TAG,
                    "Validation failed: Beneficiary: ${currentState.selectedBeneficiary}, User: $currentUser, Products: ${currentState.selectedProducts.size}, Date: ${currentState.date}, Time: ${currentState.time}"
                )
                _uiState.update { it.copy(isSaving = false) }
                return@launch
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val initialDate: Date = try {
                sdf.parse("${currentState.date} ${currentState.time}") ?: run {
                    Log.e(TAG, "Date/Time parsing returned null.")
                    _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
                    return@launch
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Could not parse date and time: ${currentState.date} ${currentState.time}",
                    e
                )
                _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
                return@launch
            }
            val calendar = Calendar.getInstance().apply { time = initialDate }

            if (isEditing) {
                // --- LÓGICA DE EDIÇÃO ---
                val id = currentState.deliveryId!!
                Log.d(TAG, "Editing delivery $id. Updating Date, Obs, Items. Ignoring Beneficiary.")
                try {
                    // Update Date
                    deliveryRepository.updateDeliveryDate(id, calendar.timeInMillis)
                    // Update Observations
                    deliveryRepository.updateDeliveryObservations(id, currentState.observations)
                    // Update Items
                    deliveryRepository.updateDeliveryItems(id, currentState.selectedProducts)

                    Log.i(TAG, "Successfully updated delivery $id")
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update delivery $id", e)
                    _uiState.update { it.copy(isSaving = false) }
                }

            } else {
                // --- LÓGICA DE CRIAÇÃO ---
                Log.d(TAG, "Creating new delivery. Repetition: ${currentState.repetition}")

                val initialStatus = if (currentUser.role == UserRole.BENEFICIARY) {
                    DeliveryStatus.UNDER_ANALYSIS
                } else {
                    DeliveryStatus.SCHEDULED
                }

                val baseDelivery = Delivery(
                    id = "",
                    beneficiaryId = currentState.selectedBeneficiary.id,
                    date = System.currentTimeMillis(),
                    scheduledDate = 0,
                    status = initialStatus, // Use determined status
                    items = currentState.selectedProducts,
                    observations = currentState.observations,
                    createdBy = currentUser.id
                )

                val deliveriesToCreate = mutableListOf<Delivery>()
                
                // Logic for Immediate Delivery (Only for Staff/Admin, assuming Beneficiary requests are always Under Analysis)
                val isStaff = currentUser.role != UserRole.BENEFICIARY
                val isImmediate = isStaff && currentState.isImmediateDelivery // Using state which is updated by date
                
                val firstDeliveryStatus = if (isImmediate) DeliveryStatus.DELIVERED else initialStatus

                deliveriesToCreate.add(
                    baseDelivery.copy(
                        id = UUID.randomUUID().toString(),
                        scheduledDate = calendar.timeInMillis,
                        status = firstDeliveryStatus
                    )
                )

                if (currentState.repetition != "Não repetir") {
                    try {
                        val allSchoolYears =
                            schoolYearRepository.getSchoolYears().firstOrNull() ?: emptyList()
                        val currentSchoolYear = allSchoolYears.find {
                            val now = System.currentTimeMillis()
                            now >= it.startDate && now <= it.endDate
                        }

                        if (currentSchoolYear != null) {
                            Log.d(
                                TAG,
                                "Repeating delivery within school year ending ${
                                    Date(currentSchoolYear.endDate)
                                }"
                            )
                            while (true) {
                                when (currentState.repetition) {
                                    "Mensalmente" -> calendar.add(Calendar.MONTH, 1)
                                    "Bimensal" -> calendar.add(Calendar.MONTH, 2)
                                    "Semestral" -> calendar.add(Calendar.MONTH, 6)
                                }

                                if (calendar.timeInMillis > currentSchoolYear.endDate) {
                                    Log.d(
                                        TAG,
                                        "Next repetition date ${calendar.time} is after school year end. Stopping."
                                    )
                                    break
                                }
                                // Repeated deliveries are always SCHEDULED (or UNDER_ANALYSIS)
                                deliveriesToCreate.add(
                                    baseDelivery.copy(
                                        id = UUID.randomUUID().toString(),
                                        scheduledDate = calendar.timeInMillis,
                                        status = initialStatus
                                    )
                                )
                            }
                        } else {
                            Log.w(TAG, "Cannot repeat delivery: No current school year found.")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during repetition logic", e)
                    }
                }

                try {
                    Log.d(TAG, "Attempting to save ${deliveriesToCreate.size} delivery/deliveries.")
                    deliveriesToCreate.forEach { delivery ->
                        deliveryRepository.addDelivery(delivery)
                    }
                    
                    // Deduct stock if immediate
                    if (isImmediate) {
                        deductStock(currentState.selectedProducts)
                    }
                    
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                    Log.i(TAG, "Successfully saved ${deliveriesToCreate.size} deliveries.")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to save deliveries to repository", e)
                    _uiState.update { it.copy(isSaving = false) }
                }
            }
        }
    }
}
