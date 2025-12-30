package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus

/**
 * Interface responsável pela gestão e persistência dos dados dos Beneficiários.
 *
 * Define o contrato para operações de leitura e escrita relacionadas com os alunos/candidatos
 * apoiados pela Loja Social.
 */
interface BeneficiaryRepository {

    /**
     * Obtém a lista completa de todos os beneficiários registados no sistema.
     *
     * Cumpre o requisito **RF02**.
     *
     * @return Lista de [Beneficiary]. Pode retornar uma lista vazia se não existirem registos.
     */
    suspend fun getBeneficiaries(): List<Beneficiary>

    /**
     * Obtém os detalhes de um beneficiário específico através do seu ID único.
     *
     * Essencial para ver detalhes ou editar o perfil.
     *
     * @param id O identificador único do beneficiário (geralmente o nº de aluno ou ID interno).
     * @return [Beneficiary] se encontrado, ou `null` caso contrário.
     */
    suspend fun getBeneficiaryById(id: String): Beneficiary?

    /**
     * Obtém os detalhes de um beneficiário específico através do seu UID de autenticação.
     *
     * @param uid O UID do utilizador do Firebase Auth.
     * @return [Beneficiary] se encontrado.
     * @throws Exception se não for encontrado.
     */
    suspend fun getBeneficiaryByUid(uid: String): Beneficiary

    /**
     * Regista um novo beneficiário no sistema.
     *
     * Cumpre o requisito **RF01** (Registo de nome, nº aluno, curso, etc.).
     *
     * @param beneficiary O objeto [Beneficiary] com os dados a persistir.
     * @throws Exception Se já existir um beneficiário com o mesmo ID ou email.
     */
    suspend fun addBeneficiary(beneficiary: Beneficiary)

    /**
     * Atualiza os dados de um beneficiário existente.
     *
     * Cumpre o requisito **RF04** (Edição de dados).
     *
     * @param beneficiary O objeto [Beneficiary] com os dados atualizados. O ID deve corresponder a um registo existente.
     */
    suspend fun updateBeneficiary(beneficiary: Beneficiary)

    /**
     * Filtra e lista os beneficiários inscritos num determinado ano letivo.
     *
     * Cumpre o requisito **RF03** (Histórico de beneficiários por ano letivo).
     *
     * @param schoolYear O ano letivo a pesquisar (ex: "2024-2025").
     * @return Lista de [Beneficiary] associados a esse ano letivo.
     */
    suspend fun getBeneficiariesBySchoolYear(schoolYear: String): List<Beneficiary>

    suspend fun updateStatus(id: String, status: BeneficiaryStatus)

}