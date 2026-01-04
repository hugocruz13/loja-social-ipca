package pt.ipca.lojasocial.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.presentation.screens.AnoLetivo
import javax.inject.Inject
import com.google.firebase.firestore.ListenerRegistration

@HiltViewModel
class AnosLetivosViewModel @Inject constructor(
    private val firestore: com.google.firebase.firestore.FirebaseFirestore
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaveSuccess = MutableSharedFlow<Boolean>()
    val isSaveSuccess = _isSaveSuccess.asSharedFlow()

    private val _anosLetivos = MutableStateFlow<List<AnoLetivo>>(emptyList())
    val anosLetivos = _anosLetivos.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    var dataInicioInput by mutableStateOf("")
    var dataFimInput by mutableStateOf("")

    init {
        observeAnosLetivos()
    }

    private fun observeAnosLetivos() {
        _isLoading.value = true

        listenerRegistration = firestore.collection("anos_letivos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("FIREBASE", "Erro no listener: ${error.message}")
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val hoje = System.currentTimeMillis()
                    val list = snapshot.documents.mapNotNull { doc ->
                        val inicio = doc.getLong("dataInicio") ?: 0L
                        val fim = doc.getLong("dataFim") ?: 0L
                        val eOAtual = hoje in inicio..fim

                        AnoLetivo(
                            id = doc.id,
                            label = doc.id.replace("_", "/"),
                            isCurrent = eOAtual
                        )
                    }
                    // Atualiza o StateFlow e a UI reage automaticamente
                    _anosLetivos.value = list.sortedByDescending { it.label }
                }
                _isLoading.value = false
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun loadAnoLetivoPorId(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = firestore.collection("anos_letivos").document(id).get().await()
                if (doc.exists()) {
                    dataInicioInput = formatLongToString(doc.getLong("dataInicio") ?: 0L)
                    dataFimInput = formatLongToString(doc.getLong("dataFim") ?: 0L)
                }
            } catch (e: Exception) {
                android.util.Log.e("LOAD_ANO", "Erro: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveAnoLetivo(idExistente: String?, dataInicioStr: String, dataFimStr: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val startTs = parseDateToLong(dataInicioStr)
                val endTs = parseDateToLong(dataFimStr)
                val docId = idExistente ?: generateSchoolYearId(dataInicioStr, dataFimStr)

                val data = hashMapOf(
                    "dataInicio" to startTs,
                    "dataFim" to endTs
                )

                firestore.collection("anos_letivos")
                    .document(docId)
                    .set(data)
                    .await()

                _isSaveSuccess.emit(true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatLongToString(timestamp: Long): String {
        if (timestamp == 0L) return ""
        val sdf = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    private fun generateSchoolYearId(start: String, end: String): String {
        return try {
            val startYear = start.split("/")[2]
            val endYear = end.split("/")[2]
            "${startYear}_$endYear"
        } catch (e: Exception) {
            java.util.UUID.randomUUID().toString()
        }
    }

    private fun parseDateToLong(dateStr: String): Long {
        if (dateStr.isBlank()) return 0L
        return try {
            val sdf = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
            val date = sdf.parse(dateStr)
            val cal = java.util.Calendar.getInstance()
            if (date != null) {
                cal.time = date
                cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                cal.set(java.util.Calendar.MINUTE, 0)
                cal.set(java.util.Calendar.SECOND, 0)
                cal.set(java.util.Calendar.MILLISECOND, 0)
                cal.timeInMillis
            } else 0L
        } catch (e: Exception) { 0L }
    }
}