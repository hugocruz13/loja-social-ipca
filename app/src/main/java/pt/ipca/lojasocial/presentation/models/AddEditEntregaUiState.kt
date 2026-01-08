package pt.ipca.lojasocial.presentation.models

import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.models.Stock

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
    val productStockLimits: Map<String, Int> = emptyMap(),
    val selectedProducts: Map<String, Int> = emptyMap(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isProductPickerDialogVisible: Boolean = false,
    val isDatePickerDialogVisible: Boolean = false,
    val isTimePickerDialogVisible: Boolean = false,
    val isImmediateDelivery: Boolean = false,
    val beneficiaryError: String? = null,
    val dateError: String? = null,
    val productsError: String? = null,
    val isFormValid: Boolean = false
)