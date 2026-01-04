package pt.ipca.lojasocial.domain.use_cases.staff

import pt.ipca.lojasocial.domain.repository.StaffRepository
import pt.ipca.lojasocial.domain.use_cases.auth.GetCurrentUserUseCase
import pt.ipca.lojasocial.domain.use_cases.log.SaveLogUseCase
import javax.inject.Inject

class ToggleStaffStatusUseCase @Inject constructor(
    private val repository: StaffRepository,
    private val saveLogUseCase: SaveLogUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(uid: String, currentStatus: Boolean, nome: String) {
        val novoEstado = !currentStatus
        repository.updateStaffStatus(uid, novoEstado)

        val currentUser = getCurrentUserUseCase()
        val userEmail = currentUser?.email ?: "Sistema"

        val estadoTexto = if (novoEstado) "Ativado" else "Desativado"
        saveLogUseCase(
            acao = "Alteração de Acesso",
            detalhe = "$estadoTexto o colaborador: $nome",
            utilizador = userEmail
        )
    }
}
