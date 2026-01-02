package pt.ipca.lojasocial.domain.use_cases.school_year

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.SchoolYear
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import javax.inject.Inject

class GetSchoolYearsUseCase @Inject constructor(
    private val repository: SchoolYearRepository
) {
    operator fun invoke(): Flow<List<SchoolYear>> = repository.getSchoolYears()
}