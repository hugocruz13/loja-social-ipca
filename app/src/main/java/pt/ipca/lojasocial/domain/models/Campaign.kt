package pt.ipca.lojasocial.domain.models

import pt.ipca.lojasocial.domain.models.CampaignStatus.ACTIVE
import pt.ipca.lojasocial.domain.models.CampaignStatus.INACTIVE
import pt.ipca.lojasocial.domain.models.CampaignStatus.PLANNED
import pt.ipca.lojasocial.domain.models.CampaignType.EXTERNAL
import pt.ipca.lojasocial.domain.models.CampaignType.INTERNAL


/**
 * Define a origem e o âmbito de organização da campanha.
 *
 * - [INTERNAL]: Organizada internamente pela própria Loja Social/IPCA.
 * - [EXTERNAL]: Organizada por entidades parceiras ou terceiros.
 */
enum class CampaignType {
    INTERNAL,
    EXTERNAL
}

/**
 * Define o estado atual do ciclo de vida da campanha.
 *
 * - [ACTIVE]: Campanha a decorrer, aceita doações e entradas de stock.
 * - [INACTIVE]: Campanha terminada, suspensa ou arquivada.
 * - [PLANNED]: Campanha agendada para o futuro, ainda não iniciada.
 */
enum class CampaignStatus {
    ACTIVE,
    INACTIVE,
    PLANNED
}

/**
 * Representa uma campanha de recolha de bens ou angariação de fundos.
 *
 * Esta entidade é central para agrupar doações e gerir stocks sazonais (ex: Natal, Páscoa).
 *
 * **Invariantes sugeridas:**
 * - A `endDate` deve ser maior ou igual à `startDate`.
 * - O `title` não deve ser vazio.
 *
 * @property id Identificador único da campanha.
 * @property title Título público da campanha (ex: "Recolha de Natal 2024").
 * @property description Descrição detalhada dos objetivos e bens aceites.
 * @property startDate Data de início da campanha em milissegundos (timestamp).
 * @property endDate Data de fim da campanha em milissegundos (timestamp).
 * @property type Classificação da campanha (Interna ou Externa).
 * @property status Estado atual de execução.
 * @property neededProductIds Lista de IDs dos produtos prioritários nesta campanha (pode ser vazia se for genérica).
 */
data class Campaign(
    val id: String,
    val title: String,
    val description: String,
    val startDate: Long,
    val endDate: Long,
    val type: CampaignType,
    val status: CampaignStatus,
    val imageUrl: String,
    val neededProductIds: List<String> = emptyList()
)