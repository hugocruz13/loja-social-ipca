package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.repository.BeneficiaryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela filtragem e obtenção do histórico de beneficiários por ano letivo.
 *
 * Permite consultar registos passados ou organizar os beneficiários atuais por ciclo letivo,
 * facilitando a análise da evolução dos apoios prestados ao longo do tempo.
 *
 * **Requisitos Funcionais:**
 * - **RF03**: Visualização do histórico de beneficiários por ano letivo.
 */
class GetBeneficiariesByYearUseCase @Inject constructor(
    private val repository: BeneficiaryRepository
) {

    /**
     * Executa a pesquisa de beneficiários inscritos no ano letivo indicado.
     *
     * @param schoolYear O ano letivo a pesquisar (formato padrão sugerido: "2023/2024" ou "2024").
     * @return Lista de [Beneficiary] que estiveram ativos ou inscritos nesse período.
     */
    suspend operator fun invoke(schoolYear: String): List<Beneficiary> {
        return repository.getBeneficiariesBySchoolYear(schoolYear)
    }
}