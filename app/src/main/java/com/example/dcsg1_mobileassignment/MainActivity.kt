package com.example.dcsg1_mobileassignment

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.dcsg1_mobileassignment.navigation.AppNavigation
import com.example.dcsg1_mobileassignment.ui.theme.DCSG1_MobileAssignmentTheme
import com.example.dcsg1_mobileassignment.viewmodel.PasswordRecoveryState
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle Auth callback
        handleAuthDeepLink(intent)

        enableEdgeToEdge()

        setContent {
            DCSG1_MobileAssignmentTheme {

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }

            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)

        // Handle OAuth callback when app is already running
        handleAuthDeepLink(intent)
    }

    // The "reset password" email link comes back as a deep link containing
    // "type=recovery". We need to flag that BEFORE handleDeeplinks()
    // establishes the session, so AuthViewModel knows to route the user to
    // the "set new password" screen instead of logging them straight in.
    private fun handleAuthDeepLink(intent: Intent) {
        val uri = intent.data
        if (uri != null && uri.toString().contains("type=recovery")) {
            PasswordRecoveryState.pending = true
        }
        supabase.handleDeeplinks(intent)
    }
}