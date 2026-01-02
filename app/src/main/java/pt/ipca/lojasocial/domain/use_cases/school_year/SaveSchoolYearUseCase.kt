package pt.ipca.lojasocial.domain.use_cases.school_year

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.SchoolYear
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import javax.inject.Inject

class SaveSchoolYearUseCase @Inject constructor(
    private val repository: SchoolYearRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(schoolYear: SchoolYear, isEdition: Boolean) {
        repository.saveSchoolYear(schoolYear)

        val acao = if (isEdition) "Edição Ano Letivo" else "Novo Ano Letivo"
        val log = hashMapOf(
            "acao" to acao,
            "detalhe" to "Configuração do período: ${schoolYear.label}",
            "utilizador" to (auth.currentUser?.email ?: "Sistema"),
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("logs").add(log).await()
    }
}