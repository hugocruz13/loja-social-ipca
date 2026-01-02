package pt.ipca.lojasocial.presentation.viewmodels

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
import pt.ipca.lojasocial.domain.repository.ProductRepository
import pt.ipca.lojasocial.domain.use_cases.product.AddProductUseCase
import pt.ipca.lojasocial.domain.use_cases.product.GetProductByIdUseCase
import pt.ipca.lojasocial.domain.use_cases.product.GetProductsUseCase
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    // --- ESTADOS GERAIS DA UI ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // --- ESTADOS DE DADOS (LISTA) ---
    private val _products = MutableStateFlow<List<Product>>(emptyList())

    // --- ESTADOS DE DADOS (DETALHE) ---
    // Útil para quando clicas num produto e queres ver o detalhe
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // --- FILTROS ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtro por Categoria (ProductType). String vazia = "Todos"
    private val _selectedType = MutableStateFlow("")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    /**
     * LISTA FILTRADA AUTOMÁTICA
     * Combina a lista bruta (_products) com os filtros (_searchQuery e _selectedType).
     * A UI deve observar esta variável e não a _products direta.
     */
    val filteredProducts: StateFlow<List<Product>> = combine(
        _products,
        _searchQuery,
        _selectedType
    ) { list, query, type ->
        list.filter { product ->
            // 1. Filtro de Nome
            val matchesQuery = query.isEmpty() ||
                    product.name.contains(query, ignoreCase = true)

            // 2. Filtro de Tipo/Categoria (Compara String do Enum)
            val matchesType = type.isEmpty() ||
                    product.type.name.equals(type, ignoreCase = true)

            matchesQuery && matchesType
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Inicializa a carga da lista ao criar o ViewModel
    init {
        loadProducts()
    }

    /**
     * UseCase: GetProducts
     * Carrega a lista completa da BD.
     */
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getProductsUseCase()
                _products.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar produtos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * UseCase: AddProduct
     * Adiciona produto e recarrega a lista.
     */
    fun addProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addProductUseCase(product)
                loadProducts() // Atualiza a lista para mostrar o novo item
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao adicionar produto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * UseCase: GetProductById
     * Carrega um produto específico para a variável selectedProduct.
     */
    fun loadProductById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val product = getProductByIdUseCase(id)
                _selectedProduct.value = product
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar detalhe: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- EVENTOS DA UI (Setters) ---

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = type
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Limpa o produto selecionado (ao sair do ecrã de detalhes)
    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }
}