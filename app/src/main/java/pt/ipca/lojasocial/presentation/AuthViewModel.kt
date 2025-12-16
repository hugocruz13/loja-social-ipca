package pt.ipca.lojasocial.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class RequestCategory {
    ALIMENTARES,
    HIGIENE,
    LIMPEZA,
    TODOS


}

val educationLevels = listOf(
    "Licenciatura",
    "Mestrado",
    "CTeSP"
)

data class RegistrationState(
    val fullName: String = "",
    val cc: String = "",
    val phone: String = "",
    val data: String = "",
    val email: String = "",
    val password: String = "",

    val requestCategory: RequestCategory? = null,
    val educationLevel: String = "",
    val dependents: Int = 0,
    val school: String = "",
    val courseName: String = "",
    val studentNumber: String = "",



    val docIdentification: Uri? = null,
    val docFamily: Uri? = null,
    val docMorada: Uri? = null,
    val docRendimento: Uri? = null,
    val docMatricula: Uri? = null
)

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state

    fun isStep1Valid(): Boolean {
        val s = _state.value
        return s.fullName.isNotBlank() && s.cc.length == 9 && s.phone.length >= 9 && s.fullName.isNotBlank() && s.email.contains("@") && s.password.length >= 6
    }
    fun isStep2Valid(): Boolean {
        val s = _state.value
        return s.requestCategory != null &&
                s.educationLevel.isNotBlank() &&
                s.school.isNotBlank()
    }
    fun isStep3Valid(): Boolean {

        return true
    }



    fun updateStep1(fullName: String, cc: String, phone: String, email: String, password: String) {
        _state.update {
            it.copy(fullName = fullName, cc = cc, phone = phone, email = email, password = password)
        }
    }
    fun updateStep2(
        category: RequestCategory?,
        education: String,
        dependents: Int,
        school: String,
        courseName: String,
        studentNumber: String
    ) {
        _state.update {
            it.copy(
                requestCategory = category,
                educationLevel = education,
                dependents = dependents,
                school = school,
                courseName = courseName,
                studentNumber = studentNumber
            )
        }
    }
    fun updateStep3(
        docIdentification: Uri? = _state.value.docIdentification,
        docFamily: Uri? = _state.value.docFamily,
        docMorada: Uri? = _state.value.docMorada,
        docRendimento: Uri? = _state.value.docRendimento,
        docMatricula: Uri? = _state.value.docMatricula
    ) {
        _state.update {
            it.copy(
                docIdentification = docIdentification,
                docFamily = docFamily,
                docMorada = docMorada,
                docRendimento = docRendimento,
                docMatricula = docMatricula
            )
        }
    }

    fun register() {
        println("A registar utilizador: ${_state.value}")
    }
}