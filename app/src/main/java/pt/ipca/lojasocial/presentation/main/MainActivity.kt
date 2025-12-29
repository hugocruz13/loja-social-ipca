package pt.ipca.lojasocial.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.ui.theme.LojaSocialIPCATheme
import pt.ipca.lojasocial.presentation.navigation.AppNavHost
import pt.ipca.lojasocial.presentation.screens.ListBeneficiariesScreen
import pt.ipca.lojasocial.presentation.screens.RequerimentosScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LojaSocialIPCATheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = Color(0XFFF5F5F5)
                ) {

                    AppNavHost()
                }

                /*ListBeneficiariesScreen(
                    onNavigateBack = { },
                    onBeneficiaryClick = { id ->
                        println("Clicou no beneficiário: $id")
                    },
                    onAddBeneficiaryClick = {
                        println("Clicou em Adicionar")
                    }
                )*/

                val dummyNavItems = listOf(
                    BottomNavItem("home", Icons.Filled.Home, "Home"),
                    BottomNavItem("notifications", Icons.Filled.Notifications, "Notificações"),
                    BottomNavItem("settings", Icons.Filled.Settings, "Configurações")
                )

                RequerimentosScreen(
                    onBackClick = {
                        println("DEBUG: Clicou em voltar")
                    },
                    onRequerimentoClick = { id ->
                        println("DEBUG: Clicou no requerimento ID: $id")
                    },
                    navItems = dummyNavItems,
                    onNavigate = { route ->
                        println("DEBUG: Navegar para $route")
                    }
                )
            }
        }
    }
}

