package pt.ipca.lojasocial.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProductTypeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaveSuccess = MutableSharedFlow<Boolean>()
    val isSaveSuccess = _isSaveSuccess.asSharedFlow()

    fun saveProductType(nome: String, tipo: String, observacoes: String, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val documentId = "bem_${nome.lowercase().trim().replace(" ", "_")}"

                var urlFinal = "https://placeholder.co/600x400"

                imageUri?.let { uri ->
                    val ref = storage.reference.child("bens/$documentId.jpg")
                    ref.putFile(uri).await()
                    urlFinal = ref.downloadUrl.await().toString()
                }

                val dadosParaGravar = hashMapOf(
                    "nome" to nome,
                    "tipo" to tipo,
                    "observacoes" to observacoes,
                    "fotoURL" to urlFinal
                )

                firestore.collection("bens")
                    .document(documentId)
                    .set(dadosParaGravar)
                    .await()

                // 4. ADICIONAR LOG AQUI
                saveLog(
                    acao = "Registo de Bem",
                    detalhe = "Criou o novo tipo de produto: $nome ($tipo)"
                )

                _isSaveSuccess.emit(true)
            } catch (e: Exception) {
                android.util.Log.e("FIREBASE_SAVE", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun saveLog(acao: String, detalhe: String) {
        try {
            val log = hashMapOf(
                "acao" to acao,
                "detalhe" to detalhe,
                "utilizador" to (auth.currentUser?.email ?: "Desconhecido"),
                "timestamp" to System.currentTimeMillis()
            )

            // Grava na coleção que o teu LogsViewModel está a ler
            firestore.collection("logs").add(log).await()
        } catch (e: Exception) {
            android.util.Log.e("LOG_ERROR", "Falha ao gravar log: ${e.message}")
        }
    }

}