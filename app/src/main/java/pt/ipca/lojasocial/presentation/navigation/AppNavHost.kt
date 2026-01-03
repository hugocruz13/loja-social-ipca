package pt.ipca.lojasocial.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.screens.AddEditAnoLetivoScreen
import pt.ipca.lojasocial.presentation.screens.AddEditCampanhaScreen
import pt.ipca.lojasocial.presentation.screens.AddEditEntregaScreen
import pt.ipca.lojasocial.presentation.screens.AnoLetivoListScreen
import pt.ipca.lojasocial.presentation.screens.CampanhaDetailScreen
import pt.ipca.lojasocial.presentation.screens.CampanhasScreen
import pt.ipca.lojasocial.presentation.screens.DashboardScreen
import pt.ipca.lojasocial.presentation.screens.EntregaDetailScreen
import pt.ipca.lojasocial.presentation.screens.EntregasScreen
import pt.ipca.lojasocial.presentation.screens.LoginScreen
import pt.ipca.lojasocial.presentation.screens.NotificationsScreen
import pt.ipca.lojasocial.presentation.screens.ProfileScreen
import pt.ipca.lojasocial.presentation.screens.RegisterStep1Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep2Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep3Screen
import pt.ipca.lojasocial.presentation.screens.RequerimentoDetailScreen
import pt.ipca.lojasocial.presentation.screens.RequerimentoEstadoScreen
import pt.ipca.lojasocial.presentation.screens.RequerimentosScreen
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel
import pt.ipca.lojasocial.presentation.screens.*


sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
    object Dashboard : AppScreen("dashboard")
    object RegisterStep1 : AppScreen("register/step1")
    object RegisterStep2 : AppScreen("register/step2")
    object RegisterStep3 : AppScreen("register/step3")
    object ConfirmationHome : AppScreen("confirmation_home")
    object Notification : AppScreen("notification")
    object Profile : AppScreen("profile")
    object AnoLetivoList : AppScreen("anoletivolist")
    object AnoLetivoAddEdit : AppScreen("anoletivoaddedit?id={id}")
    object RequerimentosList : AppScreen("requerimentoslist")
    object RequerimentoDetails : AppScreen("requerimentodetails?id={id}")

    object RequerimentoStatus : AppScreen("request_status")
    object CampanhasList : AppScreen("campanhaslist")
    object CampanhaAddEdit : AppScreen("campanha_add_edit?id={id}")
    object CampanhaDetail : AppScreen("campanha_detail/{campanhaId}")
    object EntregasList : AppScreen("entregaslist")
    object ProductList : AppScreen("product_list")
    object ProductDetail : AppScreen("product_detail/{productId}")
    object ProductAddEdit : AppScreen("product_add_edit?id={id}")
}

@Composable
fun AppNavHost(
    viewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    val globalNavItems = listOf(
        BottomNavItem(AppScreen.CampanhasList.route, Icons.Filled.Home, "Home"),
        BottomNavItem(AppScreen.Notification.route, Icons.Filled.Notifications, "Notificações"),
        BottomNavItem(AppScreen.Profile.route, Icons.Filled.Settings, "Configurações")
    )

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

        composable(AppScreen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate(AppScreen.RegisterStep1.route) },

                onLoginSuccess = {
                    val currentState = viewModel.state.value

                    if (currentState.userRole == "colaborador") {
                        navController.navigate(AppScreen.Dashboard.route) {
                            popUpTo(AppScreen.Login.route) { inclusive = true }
                        }
                    } else {
                        // Se estiver ATIVO -> Vai para Dashboard
                        if (currentState.beneficiaryStatus == BeneficiaryStatus.ATIVO) {
                            navController.navigate(AppScreen.Dashboard.route) {
                                popUpTo(AppScreen.Login.route) { inclusive = true }
                            }
                        }
                        // Se não estiver Ativo -> Vai para Estado do Requerimento
                        else {
                            navController.navigate(AppScreen.RequerimentoStatus.route) {
                                popUpTo(AppScreen.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // --- DASHBOARD (Home) ---
        composable(AppScreen.Dashboard.route) {
            val state by viewModel.state.collectAsState()

            // 1. Converter a Role (String) para o Enum da UI
            val uiRole = if (state.userRole == "colaborador") {
                pt.ipca.lojasocial.presentation.screens.UserRole.STAFF
            } else {
                pt.ipca.lojasocial.presentation.screens.UserRole.BENEFICIARY
            }

            // 2. Renderizar o Ecrã
            DashboardScreen(
                userName = state.fullName,
                role = uiRole,
                onNavigateTo = { destinationKey ->
                    when (destinationKey) {
                        "entregas" -> navController.navigate(AppScreen.EntregasList.route)
                        "requerimentos" -> navController.navigate(AppScreen.RequerimentosList.route)
                        "campanhas" -> navController.navigate(AppScreen.CampanhasList.route)
                        "ano_letivo" -> navController.navigate(AppScreen.AnoLetivoList.route)
                        "stock" ->  navController.navigate(AppScreen.ProductList.route)
                        "beneficiarios" -> { /* navController.navigate(AppScreen.BeneficiariosList.route) */ }
                        "reports" -> { /* navController.navigate(AppScreen.Reports.route) */ }
                        "apoio" -> { /* navController.navigate(AppScreen.Support.route) */ }

                    }
                }
            )
        }

        // --- FLUXO DE REGISTO ---
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

        // --- ECRÃ DE ESTADO (VISTA DO BENEFICIARIO) ---
        composable(AppScreen.RequerimentoStatus.route) {
            val state by viewModel.state.collectAsState()

            RequerimentoEstadoScreen(
                status = state.requestStatus,
                beneficiaryName = state.fullName,
                cc = state.cc,
                observations = state.requestObservations,
                documents = state.requestDocuments,
                onResubmitDoc = { docKey, uri ->
                    viewModel.resubmitDocument(docKey, uri)
                },

                onBackClick = {
                    viewModel.logout()
                    navController.navigate(AppScreen.Login.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // --- GESTÃO DE REQUERIMENTOS (VISTA DO STAFF) ---
        composable(AppScreen.RequerimentosList.route) {
            RequerimentosScreen(
                onBackClick = { navController.popBackStack() },
                onRequerimentoClick = { id ->
                    // Navega para o detalhe passando o ID na rota
                    navController.navigate("requerimentodetails?id=$id")
                },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.RequerimentoDetails.route, // Deve ser "requerimentodetails?id={id}"
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            // Recupera o ID passado na navegação
            val id = backStackEntry.arguments?.getString("id") ?: ""

            // O ViewModel deste ecrã (RequerimentoDetailViewModel) será criado pelo Hilt
            // e vai buscar os dados usando este ID.
            RequerimentoDetailScreen(
                requerimentoId = id,
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        // --- OUTRAS ROTAS (Notificações, Perfil, Campanhas, Entregas) ---

        composable(AppScreen.Notification.route) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.AnoLetivoList.route) {
            AnoLetivoListScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(AppScreen.AnoLetivoAddEdit.route) },
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
            val id = backStackEntry.arguments?.getString("id")

            AddEditAnoLetivoScreen(
                anoLetivoId = id,
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.CampanhasList.route) {
            CampanhasScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("campanha_add_edit") },
                onCampanhaClick = { id -> navController.navigate("campanha_detail/$id") },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = "campanha_add_edit?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")

            AddEditCampanhaScreen(
                campanhaId = id,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { n, d, i, f, t ->navController.popBackStack()},
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

        composable(AppScreen.EntregasList.route) {
            EntregasScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("agendar_entrega?role=colaborador") },
                onEditDelivery = { id -> navController.navigate("agendar_entrega?id=$id&role=colaborador") },
                onDeliveryClick = { id -> navController.navigate("entrega_detail/$id/colaborador") },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = "agendar_entrega?id={id}&role={role}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("role") { type = NavType.StringType; defaultValue = "colaborador" }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val role = backStackEntry.arguments?.getString("role")

            AddEditEntregaScreen(
                entregaId = id,
                isCollaborator = role == "colaborador",
                onBackClick = { navController.popBackStack() },
                onSaveClick = {navController.popBackStack()},
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = "entrega_detail/{id}/{role}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val userRole = backStackEntry.arguments?.getString("role") ?: "beneficiario"

            EntregaDetailScreen(
                entregaId = id,
                userRole = userRole,
                onBackClick = { navController.popBackStack() },
                onStatusUpdate = { entregue -> navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.ProductList.route) {
            ProductListScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->  navController.navigate("product_detail/$productId") },
                onAddProductClick = { navController.navigate(AppScreen.ProductAddEdit.route)},
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""

            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate("product_add_edit?id=$it") },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = "product_add_edit?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->

            val productId = backStackEntry.arguments?.getString("id")

            AddEditProductScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }
    }
}