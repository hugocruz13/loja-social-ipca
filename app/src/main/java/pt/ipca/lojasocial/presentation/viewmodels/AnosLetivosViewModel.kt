package pt.ipca.lojasocial.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.SchoolYear
import pt.ipca.lojasocial.domain.use_cases.school_year.GetSchoolYearByIdUseCase
import pt.ipca.lojasocial.domain.use_cases.school_year.GetSchoolYearsUseCase
import pt.ipca.lojasocial.domain.use_cases.school_year.SaveSchoolYearUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AnosLetivosViewModel @Inject constructor(
    private val getSchoolYearsUseCase: GetSchoolYearsUseCase,
    private val saveSchoolYearUseCase: SaveSchoolYearUseCase,
    private val getSchoolYearByIdUseCase: GetSchoolYearByIdUseCase
) : ViewModel() {

    // --- ESTADO DOS INPUTS ---
    var dataInicioInput by mutableStateOf("")
    var dataFimInput by mutableStateOf("")

    // --- ESTADO DE VALIDAÇÃO (UX MODERNA) ---
    var dataInicioError by mutableStateOf<String?>(null)
    var dataFimError by mutableStateOf<String?>(null)
    var dataInicioTouched by mutableStateOf(false)
    var dataFimTouched by mutableStateOf(false)

    val isFormValid: Boolean
        get() = dataInicioError == null &&
                dataFimError == null &&
                dataInicioInput.isNotBlank() &&
                dataFimInput.isNotBlank()

    // --- ESTADO DE CARREGAMENTO E SUCESSO ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaveSuccess = MutableSharedFlow<Boolean>()
    val isSaveSuccess = _isSaveSuccess.asSharedFlow()

    val anosLetivos = getSchoolYearsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- LÓGICA DE VALIDAÇÃO ---
    private fun validateDates() {
        val startTs = parseDateToLong(dataInicioInput)
        val endTs = parseDateToLong(dataFimInput)

        // Obter timestamp de hoje (00:00:00) para comparação justa
        val todayCal = Calendar.getInstance()
        todayCal.set(Calendar.HOUR_OF_DAY, 0)
        todayCal.set(Calendar.MINUTE, 0)
        todayCal.set(Calendar.SECOND, 0)
        todayCal.set(Calendar.MILLISECOND, 0)
        val todayTs = todayCal.timeInMillis

        // 1. Validação Data Início
        dataInicioError = when {
            dataInicioInput.isBlank() -> "Campo obrigatório"
            startTs < todayTs -> "A data de início não pode ser anterior a hoje"
            else -> null
        }

        // 2. Validação Data Fim
        dataFimError = when {
            dataFimInput.isBlank() -> "Campo obrigatório"
            endTs <= startTs -> "A data de fim deve ser posterior ao início"
            else -> null
        }
    }

    // --- FUNÇÕES DE INTERAÇÃO ---
    fun onDataInicioChange(date: String) {
        dataInicioInput = date
        dataInicioTouched = true
        validateDates()
    }

    fun onDataFimChange(date: String) {
        dataFimInput = date
        dataFimTouched = true
        validateDates()
    }

    // --- CARREGAMENTO E PERSISTÊNCIA ---
    fun loadAnoLetivoPorId(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val year = getSchoolYearByIdUseCase(id)
                if (year != null) {
                    dataInicioInput = formatLongToString(year.startDate)
                    dataFimInput = formatLongToString(year.endDate)
                    // Ao carregar um existente, validamos logo para habilitar o botão de edição
                    validateDates()
                }
            } catch (e: Exception) {
                android.util.Log.e("LOAD_ERROR", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveAnoLetivo(idExistente: String?) {
        // Forçar uma última validação antes de salvar
        validateDates()

        if (!isFormValid) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val startTs = parseDateToLong(dataInicioInput)
                val endTs = parseDateToLong(dataFimInput)

                // O ID será ex: 2024_2025
                val docId = idExistente ?: generateSchoolYearId(dataInicioInput, dataFimInput)

                val schoolYear = SchoolYear(
                    id = docId,
                    label = docId.replace("_", "/"),
                    startDate = startTs,
                    endDate = endTs
                )

                saveSchoolYearUseCase(schoolYear, isEdition = idExistente != null)
                _isSaveSuccess.emit(true)
            } catch (e: Exception) {
                android.util.Log.e("SAVE_ERROR", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- UTILS DE FORMATAÇÃO ---
    private fun formatLongToString(timestamp: Long): String {
        if (timestamp == 0L) return ""
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Padronizado dd/MM/yyyy
        return sdf.format(Date(timestamp))
    }

    private fun generateSchoolYearId(start: String, end: String): String {
        return try {
            val startYear = start.split("/")[2]
            val endYear = end.split("/")[2]
            "${startYear}_$endYear"
        } catch (e: Exception) {
            UUID.randomUUID().toString()
        }
    }

    private fun parseDateToLong(dateStr: String): Long {
        if (dateStr.isBlank()) return 0L
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(dateStr)
            val cal = Calendar.getInstance()
            if (date != null) {
                cal.time = date
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            } else 0L
        } catch (e: Exception) {
            0L
        }
    }
}