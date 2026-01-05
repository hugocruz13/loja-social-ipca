package pt.ipca.lojasocial.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
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
import pt.ipca.lojasocial.presentation.screens.AddEditAnoLetivoScreen
import pt.ipca.lojasocial.presentation.screens.AddEditCampanhaScreen
import pt.ipca.lojasocial.presentation.screens.AddEditEntregaScreen
import pt.ipca.lojasocial.presentation.screens.AddEditProductScreen
import pt.ipca.lojasocial.presentation.screens.AddProductTypeScreen
import pt.ipca.lojasocial.presentation.screens.AnoLetivoListScreen
import pt.ipca.lojasocial.presentation.screens.CampanhaDetailScreen
import pt.ipca.lojasocial.presentation.screens.CampanhasScreen
import pt.ipca.lojasocial.presentation.screens.DashboardScreen
import pt.ipca.lojasocial.presentation.screens.EntregaDetailScreen
import pt.ipca.lojasocial.presentation.screens.EntregasScreen
import pt.ipca.lojasocial.presentation.screens.LoginScreen
import pt.ipca.lojasocial.presentation.screens.LogsListScreen
import pt.ipca.lojasocial.presentation.screens.NotificationsScreen
import pt.ipca.lojasocial.presentation.screens.ProductDetailScreen
import pt.ipca.lojasocial.presentation.screens.ProductListScreen
import pt.ipca.lojasocial.presentation.screens.ProfileScreen
import pt.ipca.lojasocial.presentation.screens.RegisterStep1Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep2Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep3Screen
import pt.ipca.lojasocial.presentation.screens.RequerimentoDetailScreen
import pt.ipca.lojasocial.presentation.screens.RequerimentoEstadoScreen
import pt.ipca.lojasocial.presentation.screens.RequerimentosScreen
import pt.ipca.lojasocial.presentation.viewmodels.AddEditEntregaViewModel
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel
import pt.ipca.lojasocial.presentation.viewmodels.BeneficiariesViewModel
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel
import pt.ipca.lojasocial.presentation.viewmodels.EntregaDetailViewModel
import pt.ipca.lojasocial.presentation.viewmodels.EntregasViewModel

sealed class AppScreen(val route: String) {
    // ... (Mantém as tuas rotas iguais) ...
    object Login : AppScreen("login")
    object RegisterStep1 : AppScreen("register/step1")
    object RegisterStep2 : AppScreen("register/step2")
    object RegisterStep3 : AppScreen("register/step3")
    object Dashboard : AppScreen("dashboard")
    object Notification : AppScreen("notification")
    object Profile : AppScreen("profile")
    object LogsList : AppScreen("logs_list")
    object ManageStaff : AppScreen("manage_staff")
    object RequerimentoStatus : AppScreen("request_status")
    object RequerimentosList : AppScreen("requerimentoslist")
    object RequerimentoDetails : AppScreen("requerimentodetails?id={id}")
    object CampanhasList : AppScreen("campanhaslist")
    object CampanhaAddEdit : AppScreen("campanha_add_edit?id={id}")
    object CampanhaDetail : AppScreen("campanha_detail/{campanhaId}")
    object ProductList : AppScreen("product_list")
    object ProductDetail : AppScreen("product_detail/{productId}")
    object ProductAddEdit : AppScreen("product_add_edit?id={id}")
    object ProductType : AppScreen("add_product_type")
    object StockEditQuantity : AppScreen("stock_edit_quantity/{stockId}")
    object EntregasList : AppScreen("entregaslist")
    object EntregaAddEdit : AppScreen("agendar_entrega?id={id}&role={role}")
    object EntregaDetail : AppScreen("entrega_detail/{entregaId}/{userRole}")
    object AnoLetivoList : AppScreen("anoletivolist")
    object AnoLetivoAddEdit : AppScreen("anoletivoaddedit?id={id}")
}

@Composable
fun AppNavHost(
    viewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(
            route = AppScreen.Dashboard.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            label = "Início"
        ),
        BottomNavItem(
            route = AppScreen.Notification.route,
            selectedIcon = Icons.Filled.Notifications,
            unselectedIcon = Icons.Outlined.Notifications,
            label = "Alertas"
        ),
        BottomNavItem(
            route = AppScreen.Profile.route,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            label = "Perfil"
        )
    )

    val onNavigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {

        // =====================================================================
        // AUTENTICAÇÃO (SEM BARRA DE NAVEGAÇÃO)
        // =====================================================================

        composable(AppScreen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate(AppScreen.RegisterStep1.route) },
                onLoginSuccess = {
                    val currentState = viewModel.state.value
                    if (currentState.userRole == "colaborador") {
                        navController.navigate(AppScreen.Dashboard.route) {
                            popUpTo(AppScreen.Login.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        if (currentState.beneficiaryStatus == BeneficiaryStatus.ATIVO) {
                            navController.navigate(AppScreen.Dashboard.route) {
                                popUpTo(AppScreen.Login.route) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.navigate(AppScreen.RequerimentoStatus.route) {
                                popUpTo(
                                    AppScreen.Login.route
                                ) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(AppScreen.RegisterStep1.route) {
            RegisterStep1Screen(
                viewModel = viewModel,
                onNext = { navController.navigate(AppScreen.RegisterStep2.route) },
                onBack = { navController.popBackStack() })
        }
        composable(AppScreen.RegisterStep2.route) {
            RegisterStep2Screen(
                viewModel = viewModel,
                onNext = { navController.navigate(AppScreen.RegisterStep3.route) },
                onBack = { navController.popBackStack() })
        }
        composable(AppScreen.RegisterStep3.route) {
            RegisterStep3Screen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate(AppScreen.RequerimentoStatus.route) {
                        popUpTo(
                            AppScreen.Login.route
                        ) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // =====================================================================
        // CORE / GERAL (COM BARRA DE NAVEGAÇÃO)
        // =====================================================================

        composable(AppScreen.Dashboard.route) {
            val state by viewModel.state.collectAsState()

            // Converte a Role (String -> Enum)
            val uiRole = if (state.userRole == "colaborador")
                pt.ipca.lojasocial.presentation.screens.UserRole.STAFF
            else
                pt.ipca.lojasocial.presentation.screens.UserRole.BENEFICIARY

            DashboardScreen(
                userName = state.fullName,
                role = uiRole,
                navItems = bottomNavItems,
                onNavigate = { destination ->

                    when (destination) {
                        // --- Mapeamento dos Cards da Dashboard (Chaves) ---
                        "entregas" -> navController.navigate(AppScreen.EntregasList.route)
                        "requerimentos" -> navController.navigate(AppScreen.RequerimentosList.route)
                        "campanhas" -> navController.navigate(AppScreen.CampanhasList.route)
                        "ano_letivo" -> navController.navigate(AppScreen.AnoLetivoList.route)
                        "stock" -> navController.navigate(AppScreen.ProductList.route)
                        "logs" -> navController.navigate(AppScreen.LogsList.route)
                        "profile" -> navController.navigate(AppScreen.Profile.route)
                        "notification" -> navController.navigate(AppScreen.Notification.route)

                        else -> onNavigate(destination)
                    }
                }
            )
        }

        composable(AppScreen.Notification.route) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                navItems = bottomNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.Profile.route) {
            val authState by viewModel.state.collectAsState()
            val beneficiariesViewModel: BeneficiariesViewModel = hiltViewModel()
            val userRoleEnum =
                if (authState.userRole == "colaborador") UserRole.STAFF else UserRole.BENEFICIARY

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
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onBackClick = { navController.popBackStack() },
                navItems = bottomNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.LogsList.route) {
            LogsListScreen(onBackClick = { navController.popBackStack() })
        }

        // =====================================================================
        // BENEFICIÁRIOS & REQUERIMENTOS
        // =====================================================================

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
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppScreen.RequerimentosList.route) {
            RequerimentosScreen(
                onBackClick = { navController.popBackStack() },
                onRequerimentoClick = { id -> navController.navigate("requerimentodetails?id=$id") },
                navItems = bottomNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.RequerimentoDetails.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            RequerimentoDetailScreen(
                requerimentoId = id,
                onBackClick = { navController.popBackStack() },
                navItems = emptyList(),
                onNavigate = onNavigate
            )
        }

        // =====================================================================
        // CAMPANHAS
        // =====================================================================

        composable(AppScreen.CampanhasList.route) {
            val campanhasVm: CampanhasViewModel = hiltViewModel()
            LaunchedEffect(Unit) { campanhasVm.loadCampanhas() }

            CampanhasScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("campanha_add_edit") },
                onCampanhaClick = { id -> navController.navigate("campanha_detail/$id") },
                navItems = bottomNavItems,
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
                    campanhasVm.saveCampanha(
                        id,
                        nome,
                        desc,
                        inicio,
                        fim,
                        tipo,
                        uri
                    )
                },
                navItems = emptyList(),
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
                navItems = emptyList(),
                onNavigate = onNavigate
            )
        }

        // =====================================================================
        // PRODUTOS & STOCK
        // =====================================================================

        composable(AppScreen.ProductList.route) {
            ProductListScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId -> navController.navigate("product_detail/$productId") },
                navItems = bottomNavItems,
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
                navItems = emptyList(),
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
                navItems = emptyList(),
                onNavigate = onNavigate
            )
        }

        composable("add_product_type") {
            AddProductTypeScreen(onBackClick = { navController.popBackStack() })
        }

        // =====================================================================
        // ENTREGAS
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
                navItems = bottomNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.EntregaAddEdit.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                },
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
                navItems = emptyList(),
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.EntregaDetail.route,
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
                navItems = emptyList(),
                onNavigate = onNavigate
            )
        }

        // =====================================================================
        // CONFIGURAÇÕES (ANO LETIVO)
        // =====================================================================

        composable(AppScreen.AnoLetivoList.route) {
            AnoLetivoListScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("anoletivoaddedit") },
                onYearClick = { ano -> navController.navigate("anoletivoaddedit?id=${ano.id}") },
                navItems = bottomNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = "anoletivoaddedit?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType; nullable = true; defaultValue = null
            })
        ) { backStackEntry ->
            val idString = backStackEntry.arguments?.getString("id")
            AddEditAnoLetivoScreen(
                anoLetivoId = idString,
                onBackClick = { navController.popBackStack() },
                navItems = emptyList(),
                onNavigate = onNavigate
            )
        }
    }
}