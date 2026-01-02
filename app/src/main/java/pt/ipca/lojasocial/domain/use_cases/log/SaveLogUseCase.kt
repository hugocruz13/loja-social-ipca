package pt.ipca.lojasocial.domain.use_cases.log

import pt.ipca.lojasocial.domain.models.AppLog
import pt.ipca.lojasocial.domain.repository.LogRepository
import javax.inject.Inject

class SaveLogUseCase @Inject constructor(
    private val repository: LogRepository
) {
    suspend operator fun invoke(acao: String, detalhe: String, utilizador: String) {
        val log = AppLog(
            acao = acao,
            detalhe = detalhe,
            utilizador = utilizador,
            timestamp = System.currentTimeMillis()
        )
        repository.saveLog(log)
    }
}