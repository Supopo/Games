package com.xxx.newgames.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xxx.newgames.games.angry.AngryUncleScreen
import com.xxx.newgames.games.drinking.DrinkingScreen
import com.xxx.newgames.games.parks.ParksScreen
import com.xxx.newgames.games.truthordare.TruthOrDareScreen
import com.xxx.newgames.games.whosis.WhoIsScreen
import com.xxx.newgames.home.HomeScreen
import com.xxx.newgames.splash.SplashScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppRoute.SPLASH.path,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(AppRoute.SPLASH.path) {
            SplashScreen(onFinished = {
                navController.navigate(AppRoute.HOME.path) {
                    popUpTo(AppRoute.SPLASH.path) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
        composable(AppRoute.HOME.path) {
            HomeScreen(onOpen = { game -> navController.openGame(game.route.path) })
        }
        composable(AppRoute.TRUTH_OR_DARE.path) {
            TruthOrDareScreen(onBack = { navController.backToHome() })
        }
        composable(AppRoute.ANGRY_UNCLE.path) {
            AngryUncleScreen(onBack = { navController.backToHome() })
        }
        composable(AppRoute.WHO_IS.path) {
            WhoIsScreen(onBack = { navController.backToHome() })
        }
        composable(AppRoute.PARKS.path) {
            ParksScreen(onBack = { navController.backToHome() })
        }
        composable(AppRoute.DRINKING_CARDS.path) {
            DrinkingScreen(onBack = { navController.backToHome() })
        }
    }
}

private fun NavHostController.openGame(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        launchSingleTop = true
    }
}

private fun NavHostController.backToHome() {
    if (!popBackStack(AppRoute.HOME.path, inclusive = false)) {
        navigate(AppRoute.HOME.path) {
            launchSingleTop = true
        }
    }
}
