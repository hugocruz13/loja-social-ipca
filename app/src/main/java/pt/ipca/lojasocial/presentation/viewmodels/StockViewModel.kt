package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Stock
import pt.ipca.lojasocial.domain.use_cases.stock.AddStockItemUseCase
import pt.ipca.lojasocial.domain.use_cases.stock.DeleteStockItemUseCase
import pt.ipca.lojasocial.domain.use_cases.stock.GetStockByCampaignUseCase
import pt.ipca.lojasocial.domain.use_cases.stock.GetStockItemByIdUseCase
import pt.ipca.lojasocial.domain.use_cases.stock.GetStockListUseCase
import pt.ipca.lojasocial.domain.use_cases.stock.UpdateStockQuantityUseCase

@HiltViewModel
class StockViewModel @Inject constructor(
    private val addStockItemUseCase: AddStockItemUseCase,
    private val deleteStockItemUseCase: DeleteStockItemUseCase,
    private val getStockListUseCase: GetStockListUseCase,
    private val getStockItemByIdUseCase: GetStockItemByIdUseCase,
    private val updateStockQuantityUseCase: UpdateStockQuantityUseCase,
    private val getStockByCampaignUseCase: GetStockByCampaignUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _stockList = MutableStateFlow<List<Stock>>(emptyList())
    val stockList = _stockList.asStateFlow()

    private val _selectedStockItem = MutableStateFlow<Stock?>(null)
    val selectedStockItem = _selectedStockItem.asStateFlow()

    private val _operationSuccess = MutableSharedFlow<Boolean>()
    val operationSuccess = _operationSuccess.asSharedFlow()

    fun loadStock() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _stockList.value = getStockListUseCase()
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadStockByCampaign(campaignId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _stockList.value = getStockByCampaignUseCase(campaignId)
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadStockItemById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedStockItem.value = getStockItemByIdUseCase(id)
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addStockItem(item: Stock) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addStockItemUseCase(item)
                _operationSuccess.emit(true)
                loadStock()
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStockQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                updateStockQuantityUseCase(itemId, newQuantity)
                _operationSuccess.emit(true)
                loadStock()
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteStockItem(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                deleteStockItemUseCase(itemId)
                _operationSuccess.emit(true)
                loadStock()
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}