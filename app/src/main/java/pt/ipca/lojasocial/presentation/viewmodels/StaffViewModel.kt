package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Colaborador
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _colaboradores = MutableStateFlow<List<Colaborador>>(emptyList())
    val colaboradores: StateFlow<List<Colaborador>> = _colaboradores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSaveSuccess = MutableSharedFlow<Boolean>()
    val isSaveSuccess = _isSaveSuccess.asSharedFlow()

    init {
        getStaffList()
    }

    // Listar colaboradores
    private fun getStaffList() {
        firestore.collection("colaboradores")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Colaborador::class.java)?.copy(uid = doc.id)
                } ?: emptyList()
                _colaboradores.value = list
            }
    }

    // Criar novo colaborador
    fun registarColaborador(nome: String, email: String, cargo: String, permissao: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, "123456").await()
                val userUid = authResult.user?.uid ?: ""

                val dadosColaborador = hashMapOf(
                    "ativo" to true,
                    "cargo" to cargo,
                    "email" to email,
                    "nome" to nome,
                    "permissao" to permissao
                )

                firestore.collection("colaboradores")
                    .document(userUid)
                    .set(dadosColaborador)
                    .await()

                saveLog(
                    acao = "Novo Colaborador",
                    detalhe = "Criou a conta para: $nome ($email)"
                )

                _isSaveSuccess.emit(true)
            } catch (e: Exception) {
                android.util.Log.e("STAFF_ERROR", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Ativar ou Desativar colaborador
    fun toggleStatus(uid: String, currentStatus: Boolean) {
        viewModelScope.launch {
            try {
                firestore.collection("colaboradores")
                    .document(uid)
                    .update("ativo", !currentStatus)
                    .await()

                val nomeColaborador = _colaboradores.value.find { it.uid == uid }?.nome ?: "Desconhecido"
                val novoEstado = if (!currentStatus) "Ativado" else "Desativado"

                // --- LOG: Alteração de Estado ---
                saveLog(
                    acao = "Alteração de Acesso",
                    detalhe = "$novoEstado o colaborador: $nomeColaborador"
                )
            } catch (e: Exception) {
                android.util.Log.e("STAFF_LOG_ERROR", e.message.toString())
            }
        }
    }

    private suspend fun saveLog(acao: String, detalhe: String) {
        try {
            val log = hashMapOf(
                "acao" to acao,
                "detalhe" to detalhe,
                "utilizador" to (auth.currentUser?.email ?: "Sistema"),
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("logs").add(log).await()
        } catch (e: Exception) {
            android.util.Log.e("LOG_ERROR", "Falha ao gravar log: ${e.message}")
        }
    }
}