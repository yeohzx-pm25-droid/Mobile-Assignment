package com.example.dcsg1_mobileassignment.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel
import com.example.dcsg1_mobileassignment.screens.LoginScreen
import com.example.dcsg1_mobileassignment.screens.RegisterScreen
import com.example.dcsg1_mobileassignment.screens.ForgotPasswordScreen
import com.example.dcsg1_mobileassignment.screens.ProfileScreen
import com.example.dcsg1_mobileassignment.screens.EditProfileScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController, authViewModel)
        }

        composable("register") {
            RegisterScreen(navController, authViewModel)
        }

        composable("forgotPassword") {
            ForgotPasswordScreen(navController, authViewModel)
        }

        composable("profile") {
            ProfileScreen(navController, authViewModel)
        }

        composable("editProfile") {
            EditProfileScreen(navController, authViewModel)
        }
    }
}