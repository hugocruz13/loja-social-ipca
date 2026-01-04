package pt.ipca.lojasocial.domain.use_cases.school_year

import pt.ipca.lojasocial.domain.models.SchoolYear
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import javax.inject.Inject

class GetSchoolYearByIdUseCase @Inject constructor(
    private val repository: SchoolYearRepository
) {
    suspend operator fun invoke(id: String): SchoolYear? {
        return repository.getSchoolYearById(id)
    }
}