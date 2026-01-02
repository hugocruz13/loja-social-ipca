package pt.ipca.lojasocial.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Colaborador

interface StaffRepository {
    fun getStaff(): Flow<List<Colaborador>>
    suspend fun createStaffMember(email: String, dados: Map<String, Any>): String
    suspend fun updateStaffStatus(uid: String, status: Boolean)
}