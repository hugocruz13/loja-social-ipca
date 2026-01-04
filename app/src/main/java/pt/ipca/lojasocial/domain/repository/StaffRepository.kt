package pt.ipca.lojasocial.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Colaborador

interface StaffRepository {
    fun getStaff(): Flow<List<Colaborador>>
    suspend fun createStaffMember(colaborador: Colaborador): String
    suspend fun updateStaffStatus(uid: String, status: Boolean)
}