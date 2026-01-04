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
import pt.ipca.lojasocial.presentation.screens.RegisterStep1Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep2Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep3Screen
import pt.ipca.lojasocial.presentation.screens.RequerimentoDetailScreen
import pt.ipca.lojasocial.presentation.screens.RequerimentoEstadoScreen
import pt.ipca.lojasocial.presentation.screens.RequerimentosScreen
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel
import pt.ipca.lojasocial.presentation.screens.*
import pt.ipca.lojasocial.presentation.viewmodels.AddEditEntregaViewModel
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel
import pt.ipca.lojasocial.presentation.screens.products.ProductListScreen
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel

sealed class AppScreen(val route: String) {
    object Dashboard : AppScreen("dashboard")
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
    object ProductList : AppScreen("product_list")
    object ProductDetail : AppScreen("product_detail/{productId}")
    object StockEditQuantity : AppScreen("stock_edit_quantity/{stockId}")
    object ProductAddEdit : AppScreen("product_add_edit?id={id}")
    object ProductType : AppScreen("add_product_type")
    object ManageStaff : AppScreen("manage_staff")
    object LogsList : AppScreen("logs_list")
    object EntregaAddEdit : AppScreen("agendar_entrega?id={id}&role={role}")
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
                        "profile" -> navController.navigate(AppScreen.Profile.route)
                        "notification" -> navController.navigate(AppScreen.Notification.route)
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
                onRegisterSuccess = { navController.navigate(AppScreen.RequerimentoStatus.route) {
                    popUpTo(AppScreen.Login.route) { inclusive = false }
                } },
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

        composable(AppScreen.Profile.route) {
            val authState by viewModel.state.collectAsState()
            // Injetar o ViewModel correto para o Perfil
            val beneficiariesViewModel: pt.ipca.lojasocial.presentation.viewmodels.BeneficiariesViewModel = hiltViewModel()

            // Converter Role
            val userRoleEnum = if (authState.userRole == "colaborador") {
                pt.ipca.lojasocial.domain.models.UserRole.STAFF
            } else {
                pt.ipca.lojasocial.domain.models.UserRole.BENEFICIARY
            }

            // Construir objeto Beneficiary temporário a partir do estado de Auth
            val currentUser = pt.ipca.lojasocial.domain.models.Beneficiary(
                id = authState.userId ?: "",
                name = authState.fullName,
                email = authState.email,
                birthDate = 0, // Dado não disponível no AuthState
                schoolYearId = "", // Dado não disponível
                phoneNumber = 0, // Dado não disponível
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

        composable("add_product_type") {
            AddProductTypeScreen(
                onBackClick = { navController.popBackStack() }
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
                anoLetivoId = id as String?,
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
            val entregasViewModel: EntregasViewModel = hiltViewModel()
            val authState by viewModel.state.collectAsState()

            // Recarrega as entregas sempre que este ecrã for exibido
            LaunchedEffect(Unit) {
                entregasViewModel.loadDeliveries()
            }

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
            route = "entrega_detail/{entregaId}/{userRole}",
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
                onStatusUpdate = { entregue -> navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(AppScreen.ProductList.route) {
            ProductListScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->  navController.navigate("product_detail/$productId") },
                navItems = globalNavItems,
                onNavigate = onNavigate,
                onDownloadReportClick = { /* Implementar download */ }
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
                onEditClick = { stockId -> navController.navigate("stock_edit_quantity/$stockId")},
                //onStatusUpdate = { /* Implement status update callback if needed at navigation level */ },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

        composable(
            route = AppScreen.StockEditQuantity.route,
            arguments = listOf(
                navArgument("stockId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val stockId = backStackEntry.arguments!!.getString("stockId")!!

            AddEditProductScreen(
                stockId = stockId,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() },
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
                entregaId = id, // Pass the extracted ID here
                viewModel = viewModel,
                isCollaborator = role == "colaborador",
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() },
                navItems = globalNavItems,
                onNavigate = onNavigate
            )
        }

    }
}