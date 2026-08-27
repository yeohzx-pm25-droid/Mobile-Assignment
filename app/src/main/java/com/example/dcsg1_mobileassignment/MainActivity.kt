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
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle OAuth callback
        supabase.handleDeeplinks(intent)

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
        supabase.handleDeeplinks(intent)
    }
}
