package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.User
import pt.ipca.lojasocial.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por obter o utilizador atualmente autenticado na sessão.
 *
 * Este componente é essencial para:
 * 1. Verificar se existe uma sessão ativa (funcionalidade de Auto-Login no arranque).
 * 2. Determinar as permissões de acesso (Staff vs Beneficiário) para configurar a UI.
 * 3. Obter os dados para preencher o perfil no menu lateral.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Recupera o utilizador da sessão atual.
     *
     * @return O objeto [User] com os dados do utilizador logado, ou `null` se não existir sessão ativa.
     */
    suspend operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}