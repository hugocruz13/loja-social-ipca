package pt.ipca.lojasocial.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.AppLog

interface LogRepository {
    fun getLogs(): Flow<List<AppLog>>

    suspend fun saveLog(log: AppLog)
}