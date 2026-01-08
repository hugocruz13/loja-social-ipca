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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.use_cases.product.AddProductUseCase
import pt.ipca.lojasocial.domain.use_cases.product.GetProductByIdUseCase
import pt.ipca.lojasocial.domain.use_cases.product.GetProductsUseCase
import java.util.UUID
import javax.inject.Inject

data class ProductFormState(
    val nameError: String? = null,
    val typeError: String? = null,
    val nameTouched: Boolean = false,
    val typeTouched: Boolean = false,
    val isFormValid: Boolean = false
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _formState = MutableStateFlow(ProductFormState())
    val formState = _formState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()

    private val _lastCreatedProduct = MutableStateFlow<Product?>(null)
    val lastCreatedProduct = _lastCreatedProduct.asStateFlow()

    // Filtros de Pesquisa (Nome e Tipo)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedType = MutableStateFlow("")

    val searchQuery = _searchQuery.asStateFlow()
    val selectedType = _selectedType.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(
        _products,
        _searchQuery,
        _selectedType
    ) { list, query, type ->
        list.filter { product ->
            val matchesQuery = query.isEmpty() || product.name.contains(query, ignoreCase = true)
            val matchesType = type.isEmpty() || product.type.name.equals(type, ignoreCase = true)
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

    // Carregar produtos
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Collect do Flow
                getProductsUseCase().collect { productsList ->
                    _products.value = productsList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    // Carregar Detalhe
    fun loadProductById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getProductByIdUseCase(id).collect { product ->
                    _selectedProduct.value = product
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    // --- ESCRITA ---

    fun addProduct(product: Product, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                val productToSend = product.copy(
                    id = product.id.ifBlank { UUID.randomUUID().toString() }
                )

                addProductUseCase(
                    product = productToSend,
                    imageUri = imageUri
                )

                _lastCreatedProduct.value = productToSend

            } catch (e: Exception) {
                _errorMessage.value = "Erro ao adicionar produto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun validateProduct(name: String, type: String) {
        val nameErr = if (name.isBlank()) "O nome do produto é obrigatório" else null
        val typeErr = if (type.isBlank()) "Selecione uma categoria" else null

        _formState.update {
            it.copy(
                nameError = nameErr,
                typeError = typeErr,
                isFormValid = nameErr == null && typeErr == null && name.isNotBlank()
            )
        }
    }

    fun onNameChange(name: String, type: String) {
        _formState.update { it.copy(nameTouched = true) }
        validateProduct(name, type)
    }

    fun onTypeSelectedForm(type: String, name: String) {
        _formState.update { it.copy(typeTouched = true) }
        validateProduct(name, type)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = type
    }

}
