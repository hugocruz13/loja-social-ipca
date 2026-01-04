package pt.ipca.lojasocial.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.SchoolYear

interface SchoolYearRepository {
    fun getSchoolYears(): Flow<List<SchoolYear>>
    suspend fun getSchoolYearById(id: String): SchoolYear?
    suspend fun saveSchoolYear(schoolYear: SchoolYear)
}