package pt.ipca.lojasocial.domain.use_cases.campaign

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import pt.ipca.lojasocial.domain.use_cases.log.SaveLogUseCase
import javax.inject.Inject

class UpdateCampaignUseCase @Inject constructor(
    private val repository: CampaignRepository,
    private val saveLogUseCase: SaveLogUseCase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(campaign: Campaign) {
        // 1. Atualizar no repositório
        repository.updateCampaign(campaign)

        saveLogUseCase(
            acao = "Edição Campanha",
            detalhe = "Alterou dados da campanha: ${campaign.title}",
            utilizador = auth.currentUser?.email ?: "Sistema"
        )
    }
}