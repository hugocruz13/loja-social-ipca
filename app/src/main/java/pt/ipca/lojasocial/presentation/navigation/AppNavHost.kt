package pt.ipca.lojasocial.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.screens.*
import pt.ipca.lojasocial.presentation.viewmodels.*

/**
 * Definição de todas as rotas da aplicação.
 * Organizado por áreas funcionais.
 */
sealed class AppScreen(val route: String) {

    // --- Autenticação ---
    object Login : AppScreen("login")
    object RegisterStep1 : AppScreen("register/step1")
    object RegisterStep2 : AppScreen("register/step2")
    object RegisterStep3 : AppScreen("register/step3")

    // --- Core / Geral ---
    object Dashboard : AppScreen("dashboard")
    object Notification : AppScreen("notification")
    object Profile : AppScreen("profile")
    object LogsList : AppScreen("logs_list")

    object ManageStaff : AppScreen("manage_staff")

    // --- Beneficiários & Requerimentos ---
    object RequerimentoStatus : AppScreen("request_status")
    object RequerimentosList : AppScreen("requerimentoslist")
    object RequerimentoDetails : AppScreen("requerimentodetails?id={id}")

    // --- Campanhas ---
    object CampanhasList : AppScreen("campanhaslist")
    object CampanhaAddEdit : AppScreen("campanha_add_edit?id={id}")
    object CampanhaDetail : AppScreen("campanha_detail/{campanhaId}")

    // --- Produtos & Stock ---
    object ProductList : AppScreen("product_list")
    object ProductDetail : AppScreen("product_detail/{productId}")
    object ProductAddEdit : AppScreen("product_add_edit?id={id}")
    object ProductType : AppScreen("add_product_type")
    object StockEditQuantity : AppScreen("stock_edit_quantity/{stockId}")

    // --- Entregas ---
    object EntregasList : AppScreen("entregaslist")
    object EntregaAddEdit : AppScreen("agendar_entrega?id={id}&role={role}")
    object EntregaDetail : AppScreen("entrega_detail/{entregaId}/{userRole}")

    // --- Configurações ---
    object AnoLetivoList : AppScreen("anoletivolist")
    object AnoLetivoAddEdit : AppScreen("anoletivoaddedit?id={id}")
}

@Composable
fun AppNavHost(
    viewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    // Itens da barra de navegação inferior (Global)
    val globalNavItems = listOf(
        BottomNavItem(AppScreen.CampanhasList.route, Icons.Filled.Home, "Home"),
        BottomNavItem(AppScreen.Notification.route, Icons.Filled.Notifications, "Notificações"),
        BottomNavItem(AppScreen.Profile.route, Icons.Filled.Settings, "Configurações")
    )

    // Helper para navegação segura
    val onNavigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {

        // =====================================================================
        // REGIÃO: AUTENTICAÇÃO
        // =====================================================================

        composable(AppScreen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate(AppScreen.RegisterStep1.route) },
                onLoginSuccess = {
                    val currentState = viewModel.state.value

                    if (currentState.userRole == "colaborador") {
                        // Colaborador vai sempre para o Dashboard
                        navController.navigate(AppScreen.Dashboard.route) {
                            popUpTo(AppScreen.Login.route) { inclusive = true }
                        }
                    } else {
                        // Beneficiário: Depende do estado
                        if (currentState.beneficiaryStatus == BeneficiaryStatus.ATIVO) {
                            navController.navigate(AppScreen.Dashboard.route) {
                                popUpTo(AppScreen.Login.route) { inclusive = true }
                            }
                        } else {
                            // Se pendente ou inativo, vê o ecrã de estado
                            navController.navigate(AppScreen.RequerimentoStatus.route) {
                                popUpTo(AppScreen.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // --- Passos de Registo ---
        composable(AppScreen.RegisterStep1.route) {
            RegisterStep1Screen(
                viewModel = viewModel,
                onNext = { navController.navigate(AppScreen.RegisterStep2.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppScreen.RegisterStep2.route) {
            RegisterStep2Screen(
                viewModel = viewModel,
                onNext = { navController.navigate(AppScreen.RegisterStep3.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppScreen.RegisterStep3.route) {
            RegisterStep3Screen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate(AppScreen.RequerimentoStatus.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // =====================================================================
        // REGIÃO: DASHBOARD & CORE
        // =====================================================================

        composable(AppScreen.Dashboard.route) {
            val state by viewModel.state.collectAsState()

            // Converte a string de role para o Enum da UI
            val uiRole = if (state.userRole == "colaborador") {
                pt.ipca.lojasocial.presentation.screens.UserRole.STAFF
            } else {
                pt.ipca.lojasocial.presentation.screens.UserRole.BENEFICIARY
            }

            DashboardScreen(
                userName = state.fullName,
                role = uiRole,
                onNavigateTo = { destinationKey ->
                    // Mapa de navegação do Dashboard
                    when (destinationKey) {
                        "entregas" -> navController.navigate(AppScreen.EntregasList.route)
                        "requerimentos" -> navController.navigate(AppScreen.RequerimentosList.route)
                        "campanhas" -> navController.navigate(AppScreen.CampanhasList.route)
                        "ano_letivo" -> navController.navigate(AppScreen.AnoLetivoList.route)
                        "stock" -> navController.navigate(AppScreen.ProductList.route)
                        "profile" -> navController.navigate(AppScreen.Profile.route)
                        "notification" -> navController.navigate(AppScreen.Notification.route)
                        "logs" -> navController.navigate(AppScreen.LogsList.route)
                        // Adicionar outras rotas aqui conforme necessário
                    }
                }
            )
        }

        composable(AppScreen.Notification.route) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.Profile.route) {
            val authState by viewModel.state.collectAsState()
            val beneficiariesViewModel: BeneficiariesViewModel = hiltViewModel()

            val userRoleEnum = if (authState.userRole == "colaborador") UserRole.STAFF else UserRole.BENEFICIARY

            // Cria um objeto temporário para mostrar no perfil
            val currentUser = pt.ipca.lojasocial.domain.models.Beneficiary(
                id = authState.userId ?: "",
                name = authState.fullName,
                email = authState.email,
                birthDate = 0,
                schoolYearId = "",
                phoneNumber = 0,
                ccNumber = authState.cc,
                status = authState.beneficiaryStatus
            )

            ProfileScreen(
                viewModel = beneficiariesViewModel,
                currentUser = currentUser,
                userRole = userRoleEnum,
                onLogout = {
                    viewModel.logout()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.LogsList.route) {
            LogsListScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // =====================================================================
        // REGIÃO: BENEFICIÁRIOS & REQUERIMENTOS
        // =====================================================================

        // Vista do Beneficiário (Estado do seu pedido)
        composable(AppScreen.RequerimentoStatus.route) {
            val state by viewModel.state.collectAsState()

            RequerimentoEstadoScreen(
                status = state.requestStatus,
                beneficiaryName = state.fullName,
                cc = state.cc,
                observations = state.requestObservations,
                documents = state.requestDocuments,
                onResubmitDoc = { docKey, uri -> viewModel.resubmitDocument(docKey, uri) },
                onBackClick = {
                    viewModel.logout()
                    navController.navigate(AppScreen.Login.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // Vista do Staff (Lista de todos os pedidos)
        composable(AppScreen.RequerimentosList.route) {
            RequerimentosScreen(
                onBackClick = { navController.popBackStack() },
                onRequerimentoClick = { id -> navController.navigate("requerimentodetails?id=$id") },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        // Detalhe de um Requerimento
        composable(
            route = AppScreen.RequerimentoDetails.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            RequerimentoDetailScreen(
                requerimentoId = id,
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        // =====================================================================
        // REGIÃO: CAMPANHAS
        // =====================================================================

        composable(AppScreen.CampanhasList.route) {
            val campanhasVm: CampanhasViewModel = hiltViewModel()
            LaunchedEffect(Unit) { campanhasVm.loadCampanhas() }

            CampanhasScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("campanha_add_edit") },
                onCampanhaClick = { id -> navController.navigate("campanha_detail/$id") },
                navItems = globalNavItems,
                onNavigate = onNavigate,
                viewModel = campanhasVm
            )
        }

        composable(route = "campanha_add_edit?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val campanhasVm: CampanhasViewModel = hiltViewModel()

            AddEditCampanhaScreen(
                campanhaId = id,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { nome, desc, inicio, fim, tipo, uri ->
                    campanhasVm.saveCampanha(id, nome, desc, inicio, fim, tipo, uri)
                },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = "campanha_detail/{campanhaId}",
            arguments = listOf(navArgument("campanhaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("campanhaId") ?: ""
            CampanhaDetailScreen(
                campanhaId = id,
                onBackClick = { navController.popBackStack() },
                onEditClick = { idToEdit -> navController.navigate("campanha_add_edit?id=$idToEdit") },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        // =====================================================================
        // REGIÃO: PRODUTOS & STOCK
        // =====================================================================

        composable(AppScreen.ProductList.route) {
            ProductListScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId -> navController.navigate("product_detail/$productId") },
                navItems = globalNavItems,
                onNavigate = onNavigate,
                onAddProductClick = { navController.navigate("product_add_edit") },
                onAddNewTypeClick = { navController.navigate("add_product_type") }
            )
        }

        composable(
            route = AppScreen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""

            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { stockId -> navController.navigate("stock_edit_quantity/$stockId") },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.StockEditQuantity.route,
            arguments = listOf(navArgument("stockId") { type = NavType.StringType })
        ) { backStackEntry ->
            val stockId = backStackEntry.arguments?.getString("stockId") ?: ""

            AddEditProductScreen(
                stockId = stockId,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable("add_product_type") {
            AddProductTypeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // =====================================================================
        // REGIÃO: ENTREGAS
        // =====================================================================

        composable(AppScreen.EntregasList.route) {
            val entregasViewModel: EntregasViewModel = hiltViewModel()
            val authState by viewModel.state.collectAsState()

            LaunchedEffect(Unit) { entregasViewModel.loadDeliveries() }

            EntregasScreen(
                viewModel = entregasViewModel,
                isCollaborator = authState.userRole == "colaborador",
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("agendar_entrega?role=${authState.userRole}") },
                onEditDelivery = { id -> navController.navigate("agendar_entrega?id=$id&role=${authState.userRole}") },
                onDeliveryClick = { id -> navController.navigate("entrega_detail/$id/${authState.userRole}") },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.EntregaAddEdit.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("role") { type = NavType.StringType; defaultValue = "colaborador" }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val role = backStackEntry.arguments?.getString("role")
            val viewModel: AddEditEntregaViewModel = hiltViewModel()

            AddEditEntregaScreen(
                entregaId = id,
                viewModel = viewModel,
                isCollaborator = role == "colaborador",
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.EntregaDetail.route, // "entrega_detail/{entregaId}/{userRole}"
            arguments = listOf(
                navArgument("entregaId") { type = NavType.StringType },
                navArgument("userRole") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val entregaId = backStackEntry.arguments?.getString("entregaId") ?: ""
            val userRole = backStackEntry.arguments?.getString("userRole") ?: "colaborador"
            val viewModel: EntregaDetailViewModel = hiltViewModel()

            EntregaDetailScreen(
                entregaId = entregaId,
                userRole = userRole,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        // =====================================================================
        // REGIÃO: CONFIGURAÇÕES (ANO LETIVO)
        // =====================================================================

        composable(AppScreen.AnoLetivoList.route) {
            AnoLetivoListScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("anoletivoaddedit") },
                onYearClick = { ano -> navController.navigate("anoletivoaddedit?id=${ano.id}") },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = "anoletivoaddedit?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val idString = backStackEntry.arguments?.getString("id")

            AddEditAnoLetivoScreen(
                anoLetivoId = idString,
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }
    }
}