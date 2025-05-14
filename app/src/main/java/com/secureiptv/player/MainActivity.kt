package com.secureiptv.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.secureiptv.player.ui.screens.LoginScreen
import com.secureiptv.player.ui.screens.MainScreen
import com.secureiptv.player.ui.screens.PlayerScreen
import com.secureiptv.player.ui.theme.SecureIPTVPlayerTheme
import com.secureiptv.player.ui.viewmodels.LoginViewModel
import com.secureiptv.player.ui.viewmodels.MainViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecureIPTVPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IPTVApp()
                }
            }
        }
    }
}

@Composable
fun IPTVApp() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val mainViewModel: MainViewModel = viewModel()
    
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = loginViewModel
            )
        }
        
        composable("main") {
            MainScreen(
                onLogout = {
                    mainViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onNavigateToPlayer = { streamUrl, title ->
                    val encodedUrl = URLEncoder.encode(streamUrl, StandardCharsets.UTF_8.toString())
                    val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
                    navController.navigate("player/$encodedUrl/$encodedTitle")
                },
                viewModel = mainViewModel
            )
        }
        
        composable(
            "player/{streamUrl}/{title}",
            arguments = listOf(
                navArgument("streamUrl") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val streamUrl = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("streamUrl") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val title = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("title") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            
            PlayerScreen(
                streamUrl = streamUrl,
                title = title,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}