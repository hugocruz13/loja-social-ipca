package pt.ipca.lojasocial.presentation.viewmodels

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalShipping
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.models.CampaignStatus
import pt.ipca.lojasocial.domain.models.CampaignType
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.use_cases.UploadImageUseCase
import pt.ipca.lojasocial.domain.use_cases.campaign.AddCampaignUseCase
import pt.ipca.lojasocial.domain.use_cases.campaign.GetCampaignByIdUseCase
import pt.ipca.lojasocial.domain.use_cases.campaign.GetCampaignsUseCase
import pt.ipca.lojasocial.domain.use_cases.campaign.UpdateCampaignUseCase
import pt.ipca.lojasocial.presentation.screens.CampanhaModel
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel responsável pela gestão de Campanhas com suporte a Tempo Real.
 * Atualizado para consumir Flows do Repositório.
 */
@HiltViewModel
class CampanhasViewModel @Inject constructor(
    private val getCampaignsUseCase: GetCampaignsUseCase,
    private val getCampaignByIdUseCase: GetCampaignByIdUseCase,
    private val addCampaignUseCase: AddCampaignUseCase,
    private val updateCampaignUseCase: UpdateCampaignUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _campanhas = MutableStateFlow<List<CampanhaModel>>(emptyList())

    private val _selectedCampanha = MutableStateFlow<CampanhaModel?>(null)
    val selectedCampanha = _selectedCampanha.asStateFlow()

    // Filtro reativo: combina a lista realtime com a query de pesquisa
    val filteredCampanhas = combine(_campanhas, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { item ->
                item.nome.contains(query, ignoreCase = true) ||
                        item.desc.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    init {
        loadCampanhas()
    }

    /**
     * Inicia a observação das campanhas em tempo real.
     * O 'collect' mantém-se ativo enquanto o ViewModel existir.
     */
    fun loadCampanhas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // MUDANÇA: Agora fazemos 'collect' ao Flow
                getCampaignsUseCase().collect { domainList ->

                    // Mapeamento de Domain -> UI Model
                    _campanhas.value = domainList.map { domain ->
                        CampanhaModel(
                            id = domain.id,
                            nome = domain.title,
                            desc = domain.description,
                            status = when (domain.status) {
                                CampaignStatus.ACTIVE -> StatusType.ATIVA
                                CampaignStatus.PLANNED -> StatusType.AGENDADA
                                CampaignStatus.INACTIVE -> StatusType.COMPLETA
                                // Apanha null ou outros casos
                                else -> StatusType.AGENDADA
                            },
                            icon = mapIcon(domain.title),
                            startDate = domain.startDate,
                            endDate = domain.endDate,
                            // 🚨 CORREÇÃO CRÍTICA: Mapear a imagem aqui para aparecer na lista 🚨
                            imageUrl = domain.imageUrl
                        )
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                android.util.Log.e("ViewModel", "Erro ao carregar campanhas", e)
                _isLoading.value = false
            }
        }
    }

    /**
     * Inicia a observação de uma campanha específica em tempo real.
     */
    fun loadCampanhaById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // MUDANÇA: Agora fazemos 'collect' ao Flow
                getCampaignByIdUseCase(id).collect { domain ->
                    if (domain != null) {
                        android.util.Log.d("DETALHE_DEBUG", "URL Realtime: ${domain.imageUrl}")
                        _selectedCampanha.value = CampanhaModel(
                            id = domain.id,
                            nome = domain.title,
                            desc = domain.description,
                            status = when (domain.status) {
                                CampaignStatus.ACTIVE -> StatusType.ATIVA
                                CampaignStatus.PLANNED -> StatusType.AGENDADA
                                else -> StatusType.COMPLETA
                            },
                            icon = Icons.Default.Campaign,
                            startDate = domain.startDate,
                            endDate = domain.endDate,
                            type = domain.type,
                            imageUrl = domain.imageUrl
                        )
                    } else {
                        // Se for null (apagado), podemos limpar a seleção
                        _selectedCampanha.value = null
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                android.util.Log.e("ViewModel", "Erro detalhe campanha", e)
                _isLoading.value = false
            }
        }
    }

    // --- MÉTODOS DE APOIO E ESCRITA (Mantêm-se praticamente iguais) ---

    private fun mapIcon(category: String?): androidx.compose.ui.graphics.vector.ImageVector {
        return when (category?.uppercase()) {
            "LIMPEZA" -> Icons.Filled.CleaningServices
            "ALIMENTOS" -> Icons.Filled.Fastfood
            "LOGISTICA" -> Icons.Filled.LocalShipping
            else -> Icons.Filled.Campaign
        }
    }

    private fun getFileExtension(context: android.content.Context, uri: Uri): String? {
        return android.webkit.MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context.contentResolver.getType(uri))
    }

    private val _isSaveSuccess = MutableSharedFlow<Boolean>()
    val isSaveSuccess = _isSaveSuccess.asSharedFlow()

    fun saveCampanha(
        id: String?,
        nome: String,
        descricao: String,
        dataInicioStr: String,
        dataFimStr: String,
        tipo: CampaignType,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Assumindo que a imagem antiga vem do model selecionado ou da lista
                // Procuramos na lista atual para ter a referência caso o _selectedCampanha não esteja carregado
                val imageUrlAtual = _selectedCampanha.value?.imageUrl
                    ?: _campanhas.value.find { it.id == id }?.imageUrl
                    ?: ""

                val startDateAtual = _selectedCampanha.value?.startDate ?: 0L
                val endDateAtual = _selectedCampanha.value?.endDate ?: 0L

                val startTs =
                    if (dataInicioStr.isBlank() && id != null) startDateAtual else parseDateToLong(
                        dataInicioStr
                    )
                val endTs =
                    if (dataFimStr.isBlank() && id != null) endDateAtual else parseDateToLong(
                        dataFimStr
                    )

                var downloadUrl: String? = imageUrlAtual.ifEmpty { null }

                if (imageUri != null) {
                    val fileName = "campanhas/${UUID.randomUUID()}.jpg"
                    downloadUrl = uploadImageUseCase(imageUri, fileName)
                }

                val hojeTs = clearTime(System.currentTimeMillis())
                val statusCalculado = when {
                    endTs < hojeTs -> CampaignStatus.INACTIVE
                    startTs > hojeTs -> CampaignStatus.PLANNED
                    else -> CampaignStatus.ACTIVE
                }

                val campaign = Campaign(
                    id = id ?: UUID.randomUUID().toString(),
                    title = nome,
                    description = descricao,
                    startDate = startTs,
                    endDate = endTs,
                    type = tipo,
                    status = statusCalculado,
                    imageUrl = downloadUrl ?: ""
                )

                if (id == null) {
                    addCampaignUseCase(campaign)
                } else {
                    updateCampaignUseCase(campaign)
                }

                _isSaveSuccess.emit(true)

            } catch (e: Exception) {
                android.util.Log.e("SAVE_ERROR", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseDateToLong(dateStr: String): Long {
        if (dateStr.isBlank()) return 0L
        return try {
            val sdf = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
            val date = sdf.parse(dateStr) ?: return 0L

            val cal = Calendar.getInstance()
            cal.time = date
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            cal.timeInMillis
        } catch (e: Exception) {
            0L
        }
    }

    private fun clearTime(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}