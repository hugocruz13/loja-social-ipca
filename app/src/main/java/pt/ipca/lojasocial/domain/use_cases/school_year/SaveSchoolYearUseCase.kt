package pt.ipca.lojasocial.domain.use_cases.school_year

import com.google.firebase.auth.FirebaseAuth
import pt.ipca.lojasocial.domain.use_cases.auth.GetCurrentUserUseCase
import pt.ipca.lojasocial.domain.models.SchoolYear
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import pt.ipca.lojasocial.domain.use_cases.log.SaveLogUseCase
import javax.inject.Inject

class SaveSchoolYearUseCase @Inject constructor(
    private val repository: SchoolYearRepository,
    private val saveLogUseCase: SaveLogUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) {
    suspend operator fun invoke(schoolYear: SchoolYear, isEdition: Boolean) {
        repository.saveSchoolYear(schoolYear)

        val currentUser = getCurrentUserUseCase()

        val acao = if (isEdition) "Edição Ano Letivo" else "Novo Ano Letivo"
        val detalhe = "Configuração do período: ${schoolYear.label}"
        val utilizador = currentUser?.email ?: "Sistema"

        saveLogUseCase(
            acao = acao,
            detalhe = detalhe,
            utilizador = utilizador
        )
    }
}