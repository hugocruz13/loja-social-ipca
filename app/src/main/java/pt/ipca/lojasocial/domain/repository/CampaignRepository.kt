package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.models.CampaignStatus

/**
 * Interface responsável pela gestão do ciclo de vida das Campanhas.
 *
 * Permite criar, listar e gerir campanhas (internas ou externas) que servem como
 * agrupadores para doações e gestão de stock sazonal.
 *
 * Cumpre os requisitos associados ao **RF24** (Registo e Visibilidade).
 */
interface CampaignRepository {

    /**
     * Obtém a lista de todas as campanhas registadas no sistema.
     *
     * @return Lista de [Campaign].
     */
    suspend fun getCampaigns(): List<Campaign>

    /**
     * Obtém os detalhes de uma campanha específica através do seu ID.
     *
     * @param id O identificador único da campanha.
     * @return [Campaign] se encontrada, ou `null` caso contrário.
     */
    suspend fun getCampaignById(id: String): Campaign?

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