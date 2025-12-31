package pt.ipca.lojasocial.domain.models

import android.net.Uri

enum class RequestCategory { ALIMENTARES, HIGIENE, LIMPEZA, TODOS }

val educationLevels = listOf("Licenciatura", "Mestrado", "CTeSP")

data class RegistrationState(
    // Step 1 - Identificação
    val fullName: String = "",
    val cc: String = "",
    val phone: String = "",
    val birthDate: String = "", // Mudei de 'data' para 'birthDate' para ser mais claro
    val email: String = "",
    val password: String = "",

    // Step 2 - Escolaridade e Pedido
    val requestCategory: RequestCategory? = null,
    val educationLevel: String = "",
    val dependents: Int = 0,
    val school: String = "",
    val courseName: String = "",
    val studentNumber: String = "",

    // Step 3 - Documentos
    val docIdentification: Uri? = null,
    val docFamily: Uri? = null,
    val docMorada: Uri? = null,
    val docRendimento: Uri? = null,
    val docMatricula: Uri? = null,

    // --- ESTADOS DE CONTROLO DA UI ---
    val isLoading: Boolean = false, // Para mostrar o Spinner
    val isSuccess: Boolean = false, // Para navegar para o Login/Home
    val errorMessage: String? = null // Para mostrar Toast/Snack com erro
)