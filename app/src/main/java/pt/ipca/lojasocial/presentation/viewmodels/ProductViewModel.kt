package pt.ipca.lojasocial.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.use_cases.product.AddProductUseCase
import pt.ipca.lojasocial.domain.use_cases.product.GetProductByIdUseCase
import pt.ipca.lojasocial.domain.use_cases.product.GetProductsUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedType = MutableStateFlow("")

    val filteredProducts: StateFlow<List<Product>> = combine(
        _products,
        _searchQuery,
        _selectedType
    ) { list, query, type ->
        list.filter { product ->
            val matchesQuery =
                query.isEmpty() || product.name.contains(query, ignoreCase = true)

            val matchesType =
                type.isEmpty() || product.type.name.equals(type, ignoreCase = true)

            matchesQuery && matchesType
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _products.value = getProductsUseCase()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProduct(product: Product, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val productWithId = product.copy(
                    id = UUID.randomUUID().toString()
                )

                addProductUseCase(
                    product = productWithId,
                    imageUri = imageUri
                )

                loadProducts()
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao adicionar produto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadProductById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedProduct.value = getProductByIdUseCase(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
