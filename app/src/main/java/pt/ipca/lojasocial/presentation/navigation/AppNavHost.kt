package pt.ipca.lojasocial.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel // Importante para o novo ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.ipca.lojasocial.domain.models.UserRole // Importante para definir o cargo
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel
import pt.ipca.lojasocial.presentation.viewmodel.BeneficiariesViewModel // Import do VM de Beneficiários
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.screens.*
import pt.ipca.lojasocial.presentation.screens.products.ProductListScreen


sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
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
                onLoginSuccess = { navController.navigate(AppScreen.ProductList.route) },
                onNavigateToRegister = { navController.navigate(AppScreen.RegisterStep1.route) }
            )
        }

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
                onRegisterSuccess = { navController.navigate(AppScreen.ConfirmationHome.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppScreen.Notification.route) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        // --- CORREÇÃO AQUI ---
        composable(AppScreen.Profile.route) {
            // 1. Criar o ViewModel correto
            val beneficiariesViewModel = hiltViewModel<BeneficiariesViewModel>()

            ProfileScreen(
                viewModel = beneficiariesViewModel, // Passa o novo VM

                // 2. Passar dados temporários para compilar e testar
                currentUser = null,
                userRole = UserRole.STAFF, // Muda para BENEFICIARY para testar as restrições

                onLogout = { navController.navigate(AppScreen.Login.route) },
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.AnoLetivoList.route) {
            AnoLetivoListScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(AppScreen.AnoLetivoAddEdit.route) },
                onYearClick = { ano -> navController.navigate("anoletivoaddedit?id=${ano.id}")},
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
            val id = idString?.toIntOrNull()

            AddEditAnoLetivoScreen(
                anoLetivoId = id,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { dataInicio, dataFim ->navController.popBackStack()},
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.RequerimentosList.route) {
            RequerimentosScreen(
                onBackClick = { navController.popBackStack() },
                onRequerimentoClick = { id -> navController.navigate("requerimentodetails?id=$id")},
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.RequerimentoDetails.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""

            RequerimentoDetailScreen(
                requerimentoId = id,
                onBackClick = { navController.popBackStack() },
                onAccept = {
                    navController.popBackStack()
                },
                onReject = { justificacao ->
                    navController.popBackStack()
                },
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

        composable(route = "campanha_detail/{campanhaId}",
            arguments = listOf(navArgument("campanhaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("campanhaId") ?: ""
            CampanhaDetailScreen(
                campanhaId = id,
                onBackClick = { navController.popBackStack() },
                onEditClick = { idToEdit ->navController.navigate("campanha_add_edit?id=$idToEdit")},
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.EntregasList.route) {
            EntregasScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("agendar_entrega?role=colaborador") },
                onEditDelivery = { id -> navController.navigate("agendar_entrega?id=$id&role=colaborador") },
                onDeliveryClick = { id ->navController.navigate("entrega_detail/$id/colaborador")},
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
            val role = backStackEntry.arguments?.getString("role") ?: "beneficiario"

            EntregaDetailScreen(
                entregaId = id,
                userRole = role,
                onBackClick = { navController.popBackStack() },
                onStatusUpdate = { entregue ->navController.popBackStack()},
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.ProductList.route) {
            ProductListScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->  navController.navigate("product_detail/$productId") },
                onAddProductClick = {},
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