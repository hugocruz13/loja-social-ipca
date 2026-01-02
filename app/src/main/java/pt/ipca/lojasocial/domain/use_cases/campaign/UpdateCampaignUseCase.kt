package pt.ipca.lojasocial.domain.use_cases.campaign

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

class UpdateCampaignUseCase @Inject constructor(
    private val repository: CampaignRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(campaign: Campaign) {
        // 1. Atualizar no repositório
        repository.updateCampaign(campaign)

        // 2. Registar o Log de Auditoria
        val log = hashMapOf(
            "acao" to "Edição Campanha",
            "detalhe" to "Alterou dados da campanha: ${campaign.title}",
            "utilizador" to (auth.currentUser?.email ?: "Sistema"),
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("logs").add(log).await()
    }
}