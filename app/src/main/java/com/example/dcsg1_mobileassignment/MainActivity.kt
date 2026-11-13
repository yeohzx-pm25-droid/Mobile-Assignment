package com.example.dcsg1_mobileassignment

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.dcsg1_mobileassignment.navigation.AppNavigation
import com.example.dcsg1_mobileassignment.ui.theme.DCSG1_MobileAssignmentTheme
import com.example.dcsg1_mobileassignment.viewmodel.PasswordRecoveryState
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only handle the auth deep link on a fresh start, not when the activity is restored.
        if (savedInstanceState == null) {
            handleAuthDeepLink(intent)
        }

        enableEdgeToEdge()

        setContent {
            DCSG1_MobileAssignmentTheme {

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    // In landscape the camera cut-out and the 3-button navigation bar sit on the
                    // left/right side of the screen. This padding keeps content from going under them.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(
                                WindowInsets.displayCutout
                                    .union(WindowInsets.navigationBars)
                                    .only(WindowInsetsSides.Horizontal)
                            )
                    ) {
                        AppNavigation()
                    }
                }

            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)

        // Handle Auth callback when app is already running
        handleAuthDeepLink(intent)
    }

    private fun handleAuthDeepLink(intent: Intent) {
        val uri = intent.data
        if (uri != null && uri.toString().contains("type=recovery")) {
            PasswordRecoveryState.pending = true
        }
        supabase.handleDeeplinks(intent)
    }
}
