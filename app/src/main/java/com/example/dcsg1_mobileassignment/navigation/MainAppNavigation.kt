package com.example.dcsg1_mobileassignment.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dcsg1_mobileassignment.communityhelp.model.BottomTab
import com.example.dcsg1_mobileassignment.communityhelp.model.PostType
import com.example.dcsg1_mobileassignment.communityhelp.screens.BlankCommunityScreen
import com.example.dcsg1_mobileassignment.screens.CreatePostScreen
import com.example.dcsg1_mobileassignment.screens.CommunityPostStore
import com.example.dcsg1_mobileassignment.screens.CommunityHomeScreenWithSupabase
import com.example.dcsg1_mobileassignment.screens.DonationDetailScreen
import com.example.dcsg1_mobileassignment.screens.DonationListScreen
import com.example.dcsg1_mobileassignment.screens.EditProfileScreen
import com.example.dcsg1_mobileassignment.screens.ForgotPasswordScreen
import com.example.dcsg1_mobileassignment.screens.JobDetailScreen
import com.example.dcsg1_mobileassignment.screens.JobApplicantsScreen
import com.example.dcsg1_mobileassignment.screens.JobFilterScreen
import com.example.dcsg1_mobileassignment.screens.JobListScreen
import com.example.dcsg1_mobileassignment.screens.LoginScreen
import com.example.dcsg1_mobileassignment.screens.NewPasswordScreen
import com.example.dcsg1_mobileassignment.screens.ProfileScreen
import com.example.dcsg1_mobileassignment.screens.RegisterScreen
import com.example.dcsg1_mobileassignment.screens.UserActivityListScreen
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val currentUserId = authViewModel.currentUser?.id

    LaunchedEffect(currentUserId) {
        if (currentUserId == null) {
            CommunityPostStore.resetLocalPosts()
        } else {
            CommunityPostStore.reloadFromSupabase()
        }
    }

    // Once the user taps the "reset password" email link, AuthViewModel
    // sets isPasswordRecovery = true. Jump straight to the "set new
    // password" screen regardless of where navigation currently is.
    LaunchedEffect(authViewModel.isPasswordRecovery) {
        if (authViewModel.isPasswordRecovery) {
            navController.navigate("newPassword") {
                popUpTo(0)
            }
        }
    }

    // While Supabase checks for a saved session, show a loading state
    // instead of the login screen so a previously logged-in user doesn't
    // briefly see "login" before landing on "home".
    if (authViewModel.isInitializing) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.currentUser != null) "home" else "login"
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

        composable("newPassword") {
            NewPasswordScreen(navController, authViewModel)
        }

        // LoginScreen still navigates to "profile"; this route now opens your Home.
        composable("profile") {
            CommunityHomeScreenWithSupabase(navController, authViewModel)
        }

        composable("home") {
            CommunityHomeScreenWithSupabase(navController, authViewModel)
        }

        composable("jobs") {
            JobListScreen(navController)
        }

        composable("jobFilter") {
            JobFilterScreen(navController)
        }

        composable(
            route = "jobDetail/{jobId}",
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            JobDetailScreen(
                navController = navController,
                jobId = backStackEntry.arguments?.getString("jobId").orEmpty(),
                authViewModel = authViewModel
            )
        }

        composable(
            route = "jobApplicants/{jobId}",
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            JobApplicantsScreen(
                navController = navController,
                jobId = backStackEntry.arguments?.getString("jobId").orEmpty()
            )
        }

        composable("donation") {
            DonationListScreen(navController)
        }

        composable(
            route = "donationDetail/{donationId}",
            arguments = listOf(navArgument("donationId") { type = NavType.StringType })
        ) { backStackEntry ->
            DonationDetailScreen(
                navController = navController,
                donationId = backStackEntry.arguments?.getString("donationId").orEmpty()
            )
        }

        composable("userProfile") {
            ProfileScreen(navController, authViewModel)
        }

        composable("editProfile") {
            EditProfileScreen(navController, authViewModel)
        }

        composable(
            route = "activityList/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            UserActivityListScreen(
                navController = navController,
                activityType = backStackEntry.arguments?.getString("type").orEmpty()
            )
        }

        composable("createJob") {
            CreatePostScreen(navController, PostType.Job)
        }

        composable("createDonation") {
            CreatePostScreen(navController, PostType.Donation)
        }

        composable(
            route = "editPost/{type}/{id}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "Job"
            val id = backStackEntry.arguments?.getString("id")
            CreatePostScreen(
                navController = navController,
                initialType = if (type == "Job") PostType.Job else PostType.Donation,
                postId = id
            )
        }
    }
}