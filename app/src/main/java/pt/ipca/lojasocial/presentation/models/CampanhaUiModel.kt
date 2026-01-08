package pt.ipca.lojasocial.presentation.models

import androidx.compose.ui.graphics.vector.ImageVector
import pt.ipca.lojasocial.domain.models.CampaignType
import pt.ipca.lojasocial.domain.models.StatusType

/**
 * Modelo de UI para representar uma Campanha nos ecrãs.
 * Adaptado para facilitar o mapeamento de dados do domínio e o carregamento de imagens.
 */
data class CampanhaUiModel(
    val id: String,
    val nome: String,
    val desc: String,
    val status: StatusType,
    val icon: ImageVector,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val type: CampaignType = CampaignType.INTERNAL,
    val imageUrl: String? = null
)