package pt.ipca.lojasocial.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.ipca.lojasocial.presentation.AuthViewModel
import pt.ipca.lojasocial.presentation.screens.AddEditAnoLetivoScreen
import pt.ipca.lojasocial.presentation.screens.AddEditCampanhaScreen
import pt.ipca.lojasocial.presentation.screens.AnoLetivoListScreen
import pt.ipca.lojasocial.presentation.screens.CampanhaDetailScreen
import pt.ipca.lojasocial.presentation.screens.CampanhasScreen
import pt.ipca.lojasocial.presentation.screens.LoginScreen
import pt.ipca.lojasocial.presentation.screens.NotificationsScreen
import pt.ipca.lojasocial.presentation.screens.ProfileScreen
import pt.ipca.lojasocial.presentation.screens.RegisterStep1Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep2Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep3Screen
import pt.ipca.lojasocial.presentation.screens.RequerimentoDetailScreen
import pt.ipca.lojasocial.presentation.screens.RequerimentosScreen

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
}

@Composable
fun AppNavHost(
    viewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {

        composable(AppScreen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(AppScreen.CampanhasList.route) },
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
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppScreen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onLogout = { navController.navigate(AppScreen.Login.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppScreen.AnoLetivoList.route) {
            AnoLetivoListScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(AppScreen.AnoLetivoAddEdit.route) },
                onYearClick = { ano -> navController.navigate("anoletivoaddedit?id=${ano.id}")}
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
                onSaveClick = { dataInicio, dataFim ->
                    navController.popBackStack()
                }
            )
        }

        composable(AppScreen.RequerimentosList.route) {
            RequerimentosScreen(
                onBackClick = { navController.popBackStack() },
                onRequerimentoClick = { id -> navController.navigate("requerimentodetails?id=$id")}
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
                }
            )
        }

        composable(AppScreen.CampanhasList.route) {
            CampanhasScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("campanha_add_edit") },
                onCampanhaClick = { id -> navController.navigate("campanha_detail/$id") }
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
                onSaveClick = { n, d, i, f, t ->
                    /* tua lógica de save */
                    navController.popBackStack()
                }
            )
        }

        composable(route = "campanha_detail/{campanhaId}",
            arguments = listOf(navArgument("campanhaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("campanhaId") ?: ""
            CampanhaDetailScreen(
                campanhaId = id,
                onBackClick = { navController.popBackStack() },
                onEditClick = { idToEdit ->
                    // Navega para a página de AddEdit passando o ID
                    navController.navigate("campanha_add_edit?id=$idToEdit")
                }
            )
        }


    }
}