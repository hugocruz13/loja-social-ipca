package pt.ipca.lojasocial.domain.use_cases.campaign

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import pt.ipca.lojasocial.domain.use_cases.log.SaveLogUseCase
import javax.inject.Inject

class AddCampaignUseCase @Inject constructor(
    private val repository: CampaignRepository,
    private val saveLogUseCase: SaveLogUseCase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(campaign: Campaign) {
        // 1. Gravar a campanha no repositório
        repository.addCampaign(campaign)

        // 2. Registar o Log de Auditoria
        saveLogUseCase(
            acao = "Nova Campanha",
            detalhe = "Criou a campanha: ${campaign.title}",
            utilizador = auth.currentUser?.email ?: "Sistema"
        )
    }
}