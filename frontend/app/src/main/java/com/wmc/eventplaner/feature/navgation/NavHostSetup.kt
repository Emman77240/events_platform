package com.wmc.eventplaner.feature.navgation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wmc.eventplaner.feature.ShareViewModel
import com.wmc.eventplaner.feature.auth.CreateAccountScreenRoute
import com.wmc.eventplaner.feature.event.CreateEventScreenRoute
import com.wmc.eventplaner.feature.home.HomeScreenRoute
import com.wmc.eventplaner.feature.auth.ForgotPasswordScreenRoute
import com.wmc.eventplaner.feature.auth.LoginScreenRoute
import com.wmc.eventplaner.feature.event.EventDetailsScreenRoute
import com.wmc.eventplaner.feature.event.EventUpdateScreenRoute

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { back->
            //val shareViewModel = back.sharedViewModel<ShareViewModel>(navController = navController)
            val shareViewModel = hiltViewModel<ShareViewModel>(back)
            LoginScreenRoute(
                onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                onNavigateToForgot = { navController.navigate(Screen.ForgotPassword.route) },
                onLogin = { navController.navigate(Screen.Home.route){
                    popUpTo(Screen.Login.route) { inclusive = false }
                    launchSingleTop = true
                } },
                shareViewModel = shareViewModel
            )
        }

        composable(Screen.Signup.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.Login.route)
            }
            val shareViewModel: ShareViewModel = hiltViewModel(parentEntry)
            CreateAccountScreenRoute(
                onBack = { navController.popBackStack() },
                onLogin = {
                    navController.navigate(Screen.Home.route){
                        popUpTo(Screen.Login.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                shareViewModel = shareViewModel

            )

        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreenRoute(
                onBackToLogin = { navController.popBackStack() },
            )
        }
        composable(Screen.CreateEvent.route) {backStackEntry->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Login.route)
            }
            val shareViewModel: ShareViewModel = hiltViewModel(parentEntry)
            CreateEventScreenRoute(
                onBack ={ navController.popBackStack() },
                shareViewModel = shareViewModel
            )
        }
        composable(Screen.Home.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Login.route)
            }
            val shareViewModel: ShareViewModel = hiltViewModel(parentEntry)

            // Access current activity to finish it later
            val activity = LocalContext.current as? Activity

            HomeScreenRoute(
                onAddEvent = { navController.navigate(Screen.CreateEvent.route) },
                onEventClick = { navController.navigate(Screen.EventDetails.route) },
                shareViewModel = shareViewModel,
                onEdit = { navController.navigate(Screen.UpdateEvent.route) },
                onBack = { activity?.finish() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true } // Clears the back stack completely
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.UpdateEvent.route) { backStackEntry->
          // val shareViewModel = back.sharedViewModel<ShareViewModel>(navController = navController)
            // Use the same ViewModel as the root
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Login.route)
            }
            val shareViewModel: ShareViewModel = hiltViewModel(parentEntry)
          //  val shareViewModel = hiltViewModel<ShareViewModel>(back)
             EventUpdateScreenRoute(
                 onBack ={ navController.popBackStack() },
                 shareViewModel = shareViewModel
             )
        }
        composable(Screen.EventDetails.route) { backStackEntry ->
            // val shareViewModel = back.sharedViewModel<ShareViewModel>(navController = navController)
            // Use the same ViewModel as the root
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Login.route)
            }
            val shareViewModel: ShareViewModel = hiltViewModel(parentEntry)
            //  val shareViewModel = hiltViewModel<ShareViewModel>(back)
            EventDetailsScreenRoute(
                onBack = { navController.popBackStack() },
                shareViewModel = shareViewModel
            )
        }

    }
}
