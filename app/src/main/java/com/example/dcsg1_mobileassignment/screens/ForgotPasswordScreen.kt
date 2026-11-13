package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.utils.Validation
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel
import com.example.dcsg1_mobileassignment.viewmodel.PasswordResetResult
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.dcsg1_mobileassignment.utils.InputLimits
import com.example.dcsg1_mobileassignment.utils.limitTo
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 26.dp)
    ) {
        // Scrollable so the form still fits in landscape / with the keyboard open.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 56.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Forgot Password",
                color = CommunityColors.Green,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Enter your email and we'll send you a link to reset your password",
                color = CommunityColors.TextMuted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(42.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it.trim().limitTo(InputLimits.EMAIL)
                    message = ""
                    sent = false
                },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = CommunityColors.TextMuted)
                },
                placeholder = { Text("Email") },
                singleLine = true,
                enabled = !sent,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    scope.launch {
                        when {
                            !Validation.isEmailValid(email) ->
                                message = "Invalid email format (e.g. name@domain.com)."
                            else -> {
                                when (authViewModel.sendPasswordResetEmail(email.trim())) {
                                    is PasswordResetResult.Success -> {
                                        sent = true
                                        message = "Reset link sent. Check your email and tap the link to set a new password."
                                    }
                                    is PasswordResetResult.EmailNotFound -> {
                                        sent = false
                                        message = "No account is registered with this email."
                                    }
                                    is PasswordResetResult.Error -> {
                                        sent = false
                                        message = "Failed to send reset link. Please try again."
                                    }
                                }
                            }
                        }
                    }
                },
                enabled = !sent,
                colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (sent) "Link Sent" else "Send Reset Link",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (message.isNotEmpty()) {
                Spacer(Modifier.height(18.dp))
                Text(
                    text = message,
                    color = if (sent) CommunityColors.Green else MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
        }

        // Drawn after the scrollable column so it stays on top and can still be tapped.
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = CommunityColors.TextPrimary,
            modifier = Modifier
                .padding(top = 16.dp)
                .size(24.dp)
                .clickable { navController.popBackStack() }
        )
    }
}
