package pt.ipca.lojasocial.presentation.viewmodels

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalShipping
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.models.CampaignStatus
import pt.ipca.lojasocial.domain.models.CampaignType
import pt.ipca.lojasocial.domain.use_cases.UploadImageUseCase
import pt.ipca.lojasocial.domain.use_cases.campaign.AddCampaignUseCase
import pt.ipca.lojasocial.domain.use_cases.campaign.GetCampaignsUseCase
import pt.ipca.lojasocial.domain.use_cases.campaign.GetCampaignByIdUseCase
import pt.ipca.lojasocial.domain.use_cases.campaign.UpdateCampaignUseCase
import pt.ipca.lojasocial.presentation.screens.CampanhaModel
import pt.ipca.lojasocial.domain.models.StatusType
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject



@HiltViewModel
class CampanhasViewModel @Inject constructor(
    private val getCampaignsUseCase: GetCampaignsUseCase,
    private val getCampaignByIdUseCase: GetCampaignByIdUseCase,
    private val addCampaignUseCase: AddCampaignUseCase,
    private val updateCampaignUseCase: UpdateCampaignUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _campanhas = MutableStateFlow<List<CampanhaModel>>(emptyList())

    private val _selectedCampanha = MutableStateFlow<CampanhaModel?>(null)
    val selectedCampanha = _selectedCampanha.asStateFlow()


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

    fun loadCampanhas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = getCampaignsUseCase()
                _campanhas.value = result.map { domain ->


                    CampanhaModel(
                        id = domain.id,
                        nome = domain.title,
                        desc = domain.description,
                        status = when(domain.status) {
                            CampaignStatus.ACTIVE -> StatusType.ATIVA
                            CampaignStatus.PLANNED -> StatusType.AGENDADA
                            CampaignStatus.INACTIVE -> StatusType.COMPLETA
                            else -> StatusType.AGENDADA
                        },
                        icon = mapIcon(domain.title),
                        startDate = domain.startDate,
                        endDate = domain.endDate
                    )
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCampanhaById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val domain = getCampaignByIdUseCase(id)
                if (domain != null) {

                    android.util.Log.d("DETALHE_DEBUG", "URL vindo do Domain: ${domain.imageUrl}")
                    _selectedCampanha.value = CampanhaModel(
                        id = domain.id,
                        nome = domain.title,
                        desc = domain.description,
                        status = when(domain.status) {
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
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }



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
                val campanhaAtual = if (id != null) getCampaignByIdUseCase(id) else null

                val startTs = if (dataInicioStr.isBlank() && campanhaAtual != null)
                    campanhaAtual.startDate else parseDateToLong(dataInicioStr)

                val endTs = if (dataFimStr.isBlank() && campanhaAtual != null)
                    campanhaAtual.endDate else parseDateToLong(dataFimStr)

                var downloadUrl: String? = campanhaAtual?.imageUrl

                if (imageUri != null) {
                    val fileName = "campanhas/${UUID.randomUUID()}.jpg"
                    downloadUrl = uploadImageUseCase(imageUri, fileName)
                }

                // Cálculo de estado (Regra de negócio que poderia estar num UseCase de validação)
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

                // A lógica de Log agora acontece dentro destes invokes
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

    private suspend fun saveLog(acao: String, detalhe: String) {
        try {
            val log = hashMapOf(
                "acao" to acao,
                "detalhe" to detalhe,
                "utilizador" to (auth.currentUser?.email ?: "Sistema"),
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("logs").add(log).await()
        } catch (e: Exception) {
            android.util.Log.e("LOG_ERROR", "Falha ao gravar log: ${e.message}")
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