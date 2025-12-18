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
import pt.ipca.lojasocial.presentation.screens.AnoLetivoListScreen
import pt.ipca.lojasocial.presentation.screens.DashboardScreen
import pt.ipca.lojasocial.presentation.screens.LoginScreen
import pt.ipca.lojasocial.presentation.screens.NotificationsScreen
import pt.ipca.lojasocial.presentation.screens.ProfileScreen
import pt.ipca.lojasocial.presentation.screens.RegisterStep1Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep2Screen
import pt.ipca.lojasocial.presentation.screens.RegisterStep3Screen
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
}

@Composable
fun AppNavHost(
    viewModel: AuthViewModel = viewModel() // ViewModel para gerir o estado de autenticação/registo
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route // A primeira página a aparecer é o Login
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(AppScreen.AnoLetivoList.route) },
                onNavigateToRegister = { navController.navigate(AppScreen.RegisterStep1.route) }
            )
        }

        // --- FLUXO DE REGISTO (3 PASSOS) ---
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

        /*// --- HOME / PÁGINA DE ESPERA ---
        composable(AppScreen.ConfirmationHome.route) {
            ConfirmationHomeScreen()
        }*/

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
            route = "anoletivoaddedit?id={id}", // A tua rota definida no AppScreen
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType // Ou IntType dependendo da tua BD
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            // Extrair o ID dos argumentos
            val idString = backStackEntry.arguments?.getString("id")
            val id = idString?.toIntOrNull() // Converte para Int se necessário

            AddEditAnoLetivoScreen(
                anoLetivoId = id,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { dataInicio, dataFim ->
                    // Aqui chamarias o ViewModel para guardar os dados antes de voltar
                    navController.popBackStack()
                }
            )
        }
    }
}