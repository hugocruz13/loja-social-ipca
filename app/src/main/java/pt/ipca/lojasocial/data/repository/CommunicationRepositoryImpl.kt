package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.EmailRequest
import pt.ipca.lojasocial.domain.models.NotificationRequest
import pt.ipca.lojasocial.domain.repository.CommunicationRepository
import javax.inject.Inject

class CommunicationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommunicationRepository {

    // Coleção para a fila de emails (Cloud Function dispara aqui)
    private val mailCollection = firestore.collection("mail_queue")

    // Coleção para a fila de notificações (Cloud Function dispara aqui)
    private val notificationsCollection = firestore.collection("notifications_queue")

    // --- FUNÇÃO DE EMAIL ---
    override suspend fun sendEmail(request: EmailRequest) {
        val messageMap = hashMapOf(
            "subject" to request.subject,
            if (request.isHtml) "html" to request.body else "text" to request.body
        )

        if (request.replyTo != null) {
            messageMap["replyTo"] = request.replyTo
        }

        if (request.senderName != null) {
            messageMap["from"] = "${request.senderName} <lojasocial@gmail.com>"
        }

        val emailData = hashMapOf(
            "to" to request.to,
            "message" to messageMap,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        mailCollection.add(emailData).await()
    }

    // --- FUNÇÃO DE NOTIFICAÇÃO  ---
    override suspend fun sendNotification(request: NotificationRequest) {
        val notificationData = hashMapOf(
            "userId" to request.userId,        // O ID de quem vai receber
            "title" to request.title,
            "message" to request.message,      // A Cloud Function usa 'message' no body
            "data" to (request.data
                ?: emptyMap<String, String>()), // Dados extra (ex: screen: dashboard)
            "createdAt" to FieldValue.serverTimestamp(),
            "status" to "PENDING"              // Para sabermos se a Cloud Function já processou
        )

        // Adiciona à fila. A Cloud Function vai ler isto e enviar o Push real.
        notificationsCollection.add(notificationData).await()
    }

    // --- GUARDAR TOKEN (ATUALIZADA PARA 2 COLEÇÕES) ---
    override suspend fun saveFcmToken(userId: String, token: String) {

        try {
            // 1. Tenta atualizar na coleção de Beneficiários
            firestore.collection("beneficiarios").document(userId)
                .update("fcmToken", token)
                .await()
        } catch (e: Exception) {
            // Se falhar (ex: documento não encontrado), tenta na de Colaboradores
            try {
                firestore.collection("colaboradores").document(userId)
                    .update("fcmToken", token)
                    .await()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }
}