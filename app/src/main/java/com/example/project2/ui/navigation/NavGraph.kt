package com.example.project2.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project2.data.repository.MathRepository
import com.example.project2.ui.screens.forehead.ForeheadScreen
import com.example.project2.ui.screens.forehead.ForeheadViewModel
import com.example.project2.ui.screens.home.HomeScreen
import com.example.project2.ui.screens.settings.SettingsScreen
import com.example.project2.ui.screens.spy.SpyScreen
import com.example.project2.ui.screens.spy.SpyViewModel
import com.example.project2.ui.screens.telephone.TelephoneScreen
import com.example.project2.ui.screens.telephone.TelephoneViewModel

object Destinations {
    const val HOME = "home"
    const val SPY = "spy"
    const val FOREHEAD = "forehead"
    const val TELEPHONE = "telephone"
    const val SETTINGS = "settings"
}

@Composable
fun MathPartyNavGraph(
    navController: NavHostController = rememberNavController(),
    repository: MathRepository = remember { MathRepository() }
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HOME,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Destinations.HOME) {
            HomeScreen(
                onNavigateToSpy = { navController.navigate(Destinations.SPY) },
                onNavigateToForehead = { navController.navigate(Destinations.FOREHEAD) },
                onNavigateToTelephone = { navController.navigate(Destinations.TELEPHONE) },
                onNavigateToSettings = { navController.navigate(Destinations.SETTINGS) }
            )
        }

        composable(Destinations.SPY) {
            val spyViewModel: SpyViewModel = viewModel { SpyViewModel(repository) }
            SpyScreen(
                viewModel = spyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.FOREHEAD) {
            val foreheadViewModel: ForeheadViewModel = viewModel { ForeheadViewModel(repository) }
            ForeheadScreen(
                viewModel = foreheadViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.TELEPHONE) {
            val telephoneViewModel: TelephoneViewModel = viewModel { TelephoneViewModel(repository) }
            TelephoneScreen(
                viewModel = telephoneViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.SETTINGS) {
            SettingsScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
