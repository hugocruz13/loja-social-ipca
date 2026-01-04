package pt.ipca.lojasocial.domain.use_cases.staff

import pt.ipca.lojasocial.domain.repository.StaffRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Colaborador
import pt.ipca.lojasocial.domain.use_cases.log.SaveLogUseCase
import javax.inject.Inject

class AddStaffUseCase @Inject constructor(
    private val repository: StaffRepository,
    private val saveLogUseCase: SaveLogUseCase,
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

        saveLogUseCase(
            acao = "Novo Colaborador",
            detalhe = "Criou a conta para: $nome ($email)",
            utilizador = auth.currentUser?.email ?: "Sistema"
        )
    }
}