package pt.ipca.lojasocial.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.screens.AddEditAnoLetivoScreen
import pt.ipca.lojasocial.presentation.screens.AddEditCampanhaScreen
import pt.ipca.lojasocial.presentation.screens.AddEditEntregaScreen
import pt.ipca.lojasocial.presentation.screens.AnoLetivoListScreen
import pt.ipca.lojasocial.presentation.screens.CampanhaDetailScreen
import pt.ipca.lojasocial.presentation.screens.CampanhasScreen
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
import pt.ipca.lojasocial.presentation.screens.RequestStatus
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.CampaignType
import pt.ipca.lojasocial.presentation.navigation.AppScreen
import pt.ipca.lojasocial.presentation.screens.AddProductTypeScreen
import pt.ipca.lojasocial.presentation.screens.AddStaffScreen
import pt.ipca.lojasocial.presentation.screens.ManageStaffScreen
import pt.ipca.lojasocial.presentation.screens.products.ProductListScreen
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel

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

    object RequerimentoStatus : AppScreen("request_status")
    object CampanhasList : AppScreen("campanhaslist")
    object CampanhaAddEdit : AppScreen("campanha_add_edit?id={id}")
    object CampanhaDetail : AppScreen("campanha_detail/{campanhaId}")
    object EntregasList : AppScreen("entregaslist")
    object ProductType : AppScreen("add_product_type")
    object ProductList : AppScreen("products_list")
    object ManageStaff : AppScreen("manage_staff")
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
        startDestination = AppScreen.ManageStaff.route
    ) {

        composable("manage_staff") {
            ManageStaffScreen(
                onBackClick = { navController.popBackStack() },
                onAddStaffClick = { navController.navigate("add_staff") },
            )
        }

        composable("add_staff") {
            AddStaffScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppScreen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(AppScreen.EntregasList.route) },
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
                onRegisterSuccess = { navController.navigate(AppScreen.RequerimentoStatus.route) {
                    popUpTo(AppScreen.Login.route) { inclusive = false }
                } },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppScreen.RequerimentoStatus.route) {

            val state by viewModel.state.collectAsState()

            RequerimentoEstadoScreen(
                status = RequestStatus.IN_ANALYSIS,

                beneficiaryName = state.fullName,
                studentNumber = state.studentNumber,

                onBackClick = {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("add_product_type") {
            AddProductTypeScreen(
                onBackClick = { navController.popBackStack() }
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
            ProfileScreen(
                viewModel = viewModel,
                onLogout = { navController.navigate(AppScreen.Login.route) },
                onBackClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.AnoLetivoList.route) {
            AnoLetivoListScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("anoletivoaddedit") },
                onYearClick = { ano -> navController.navigate("anoletivoaddedit?id=${ano.id}")},
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = "anoletivoaddedit?id={id}",
            arguments = listOf(navArgument("id") {nullable = true; defaultValue = null})
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")


            AddEditAnoLetivoScreen(
                anoLetivoId = id,
                onBackClick = { navController.popBackStack() },
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
                    // Lógica para aceitar
                    navController.popBackStack()
                },
                onReject = { justificacao ->
                    // Lógica para rejeitar com a justificativa vinda do modal
                    navController.popBackStack()
                },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.CampanhasList.route) {

            val campanhasVm: CampanhasViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                campanhasVm.loadCampanhas()
            }

            CampanhasScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("campanha_add_edit") },
                onCampanhaClick = { id -> navController.navigate("campanha_detail/$id") },
                navItems = globalNavItems,
                onNavigate = onNavigate,
                viewModel = campanhasVm // Passa o VM aqui
            )
        }

        composable(route = "campanha_add_edit?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val viewModel: CampanhasViewModel = hiltViewModel()

            AddEditCampanhaScreen(
                campanhaId = id,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { nome, desc, inicio, fim, tipo, uri ->
                    viewModel.saveCampanha(id, nome, desc, inicio, fim, tipo, uri)
                },
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


        composable("products_list") {
            ProductListScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { /* ... */ },
                onAddProductClick = {
                    // Vai para a página de registo de produto (quantidade/validade)
                    navController.navigate("registar_produto_stock")
                },
                onAddNewTypeClick = {
                    // Vai para a página de registar NOVO BEM (foto/categoria)
                    navController.navigate("add_product_type")
                },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }


    }
}