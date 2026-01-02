package pt.ipca.lojasocial.domain.use_cases.school_year

import pt.ipca.lojasocial.domain.models.SchoolYear
import pt.ipca.lojasocial.domain.repository.SchoolYearRepository
import javax.inject.Inject

class SaveSchoolYearUseCase @Inject constructor(
    private val repository: SchoolYearRepository
) {
    suspend operator fun invoke(schoolYear: SchoolYear) = repository.saveSchoolYear(schoolYear)
}