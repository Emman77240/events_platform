package com.wmc.eventplaner.feature.navgation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object CreateEvent : Screen("create_event")
    object UpdateEvent : Screen("update_event")
    object Home : Screen("Home")
    object Splash : Screen("splash")
    object EventDetails : Screen("EventDetails")
}