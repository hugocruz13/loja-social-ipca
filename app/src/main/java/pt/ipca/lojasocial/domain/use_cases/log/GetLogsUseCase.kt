package pt.ipca.lojasocial.domain.use_cases.log

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.AppLog
import pt.ipca.lojasocial.domain.repository.LogRepository
import javax.inject.Inject

class GetLogsUseCase @Inject constructor(
    private val repository: LogRepository
) {
    operator fun invoke(): Flow<List<AppLog>> = repository.getLogs()
}