package pt.ipca.lojasocial.domain.use_cases.staff

import pt.ipca.lojasocial.domain.repository.StaffRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ToggleStaffStatusUseCase @Inject constructor(
    private val repository: StaffRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(uid: String, currentStatus: Boolean, nome: String) {
        val novoEstado = !currentStatus
        repository.updateStaffStatus(uid, novoEstado)

        // Log de Auditoria
        val estadoTexto = if (novoEstado) "Ativado" else "Desativado"
        val log = hashMapOf(
            "acao" to "Alteração de Acesso",
            "detalhe" to "$estadoTexto o colaborador: $nome",
            "utilizador" to (auth.currentUser?.email ?: "Sistema"),
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("logs").add(log).await()
    }
}