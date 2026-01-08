package pt.ipca.lojasocial.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Notification
import pt.ipca.lojasocial.domain.repository.NotificationRepository
import java.util.Date
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    private val collection = firestore.collection("notifications_queu")

    override fun getNotificationsStream(userId: String): Flow<List<Notification>> = callbackFlow {
        Log.d("NotificationRepo", "A pedir notificações para o user: $userId")

        val query = collection
            .whereEqualTo("userId", userId)
            // CUIDADO: Se não tiveres o índice composto criado no Firebase, o 'orderBy' pode fazer a query falhar.
            // Se continuar a crashar, comenta a linha do orderBy temporariamente para testar.
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("NotificationRepo", "Erro no listener: ${error.message}")
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // USAMOS mapNotNull PARA IGNORAR DOCUMENTOS COM ERRO
                val notifications = snapshot.documents.mapNotNull { doc ->
                    try {
                        // Tenta obter os campos com segurança
                        val title = doc.getString("title") ?: "Sem Título"
                        val message = doc.getString("message") ?: ""
                        val screen = doc.getString("screen")

                        // Tratamento robusto de datas
                        // createdAt pode vir como null se o documento acabou de ser criado
                        val timestamp = doc.getTimestamp("createdAt")
                        val date = timestamp?.toDate() ?: Date()

                        val readAtTimestamp = doc.getTimestamp("readAt")

                        Notification(
                            id = doc.id,
                            title = title,
                            message = message,
                            date = date,
                            screenDestination = screen,
                            readAt = readAtTimestamp?.toDate()
                        )
                    } catch (e: Exception) {
                        // Se um documento falhar, mostramos o erro no Logcat mas a app NÃO FECHA
                        Log.e("NotificationRepo", "Erro ao converter doc ${doc.id}: ${e.message}")
                        e.printStackTrace()
                        null // Retorna null para o mapNotNull ignorar este item
                    }
                }

                Log.d("NotificationRepo", "Notificações carregadas: ${notifications.size}")
                trySend(notifications)
            }
        }

        awaitClose { listener.remove() }
    }

    override suspend fun markAsRead(notificationId: String) {
        try {
            collection.document(notificationId)
                .update("readAt", Timestamp.now())
                .await()
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Erro ao marcar como lida: ${e.message}")
        }
    }
}