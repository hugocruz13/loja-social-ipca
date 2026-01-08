package pt.ipca.lojasocial.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.models.CampaignStatus

/**
 * Interface responsável pela gestão do ciclo de vida das Campanhas.
 *
 * Permite criar, listar e gerir campanhas (internas ou externas) que servem como
 * agrupadores para doações e gestão de stock sazonal.
 *
 * Agora suporta **atualizações em Tempo Real** através de Kotlin Flows.
 *
 * Cumpre os requisitos associados ao **RF24** (Registo e Visibilidade).
 */
interface CampaignRepository {

    /**
     * Obtém um fluxo (Flow) com a lista de todas as campanhas em tempo real.
     *
     * Ao subscrever este Flow, a UI receberá uma nova lista automaticamente sempre
     * que houver alterações (adição, remoção ou edição) na base de dados.
     *
     * @return Flow contendo a lista atualizada de [Campaign].
     */
    fun getCampaigns(): Flow<List<Campaign>>

    /**
     * Obtém um fluxo (Flow) com os detalhes de uma campanha específica em tempo real.
     *
     * Útil para manter o ecrã de detalhes sempre sincronizado (ex: se o estado mudar
     * de 'Agendada' para 'Ativa' enquanto o utilizador vê a campanha).
     *
     * @param id O identificador único da campanha.
     * @return Flow que emite [Campaign] se encontrada, ou `null` caso não exista/seja removida.
     */
    fun getCampaignById(id: String): Flow<Campaign?>

    /**
     * Regista uma nova campanha na aplicação.
     *
     * Cumpre o requisito **RF24**. A campanha criada poderá ficar visível no website/app
     * dependendo do seu estado.
     *
     * @param campaign O objeto [Campaign] com os dados a persistir.
     */
    suspend fun addCampaign(campaign: Campaign)

    /**
     * Atualiza os dados gerais de uma campanha existente (título, descrição, datas, etc.).
     *
     * @param campaign O objeto [Campaign] com os dados atualizados.
     */
    suspend fun updateCampaign(campaign: Campaign)

    /**
     * Remove permanentemente uma campanha do sistema.
     *
     * **Nota:** A implementação deve acautelar a integridade referencial se existirem
     * stocks ou entregas associadas a esta campanha.
     *
     * @param id O identificador da campanha a eliminar.
     */
    suspend fun deleteCampaign(id: String)

    /**
     * Atualiza apenas o estado de uma campanha (ex: de [CampaignStatus.PLANNED] para [CampaignStatus.ACTIVE]).
     *
     * Permite operações rápidas de ativação/desativação sem necessidade de enviar o objeto completo.
     *
     * @param id O identificador da campanha.
     * @param status O novo estado a aplicar.
     */
    suspend fun updateCampaignStatus(id: String, status: CampaignStatus)

    suspend fun getActiveCampaignsCount(): Int
}