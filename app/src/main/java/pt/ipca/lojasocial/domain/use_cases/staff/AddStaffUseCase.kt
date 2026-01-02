package pt.ipca.lojasocial.domain.use_cases.staff

import pt.ipca.lojasocial.domain.repository.StaffRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Colaborador
import javax.inject.Inject

class AddStaffUseCase @Inject constructor(
    private val repository: StaffRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(nome: String, email: String, cargo: String, permissao: String) {
        val novoColaborador = Colaborador(
            uid = "",
            nome = nome,
            email = email,
            cargo = cargo,
            permissao = permissao,
            ativo = true
        )

        repository.createStaffMember(novoColaborador)

        val log = hashMapOf(
            "acao" to "Novo Colaborador",
            "detalhe" to "Criou a conta para: $nome ($email)",
            "utilizador" to (auth.currentUser?.email ?: "Sistema"),
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("logs").add(log).await()
    }
}