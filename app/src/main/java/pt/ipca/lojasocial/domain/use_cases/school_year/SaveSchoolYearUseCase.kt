package pt.ipca.lojasocial.domain.use_cases.school_year

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.lojasocial.domain.models.SchoolYear
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import pt.ipca.lojasocial.domain.use_cases.log.SaveLogUseCase
import javax.inject.Inject

class SaveSchoolYearUseCase @Inject constructor(
    private val repository: SchoolYearRepository,
    private val saveLogUseCase: SaveLogUseCase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(schoolYear: SchoolYear, isEdition: Boolean) {
        repository.saveSchoolYear(schoolYear)

        val acao = if (isEdition) "Edição Ano Letivo" else "Novo Ano Letivo"
        val detalhe = "Configuração do período: ${schoolYear.label}"
        val utilizador = auth.currentUser?.email ?: "Sistema"

        saveLogUseCase(
            acao = acao,
            detalhe = detalhe,
            utilizador = utilizador
        )
    }
}