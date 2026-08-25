package com.example.dcsg1_mobileassignment.screens

import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.utils.Validation
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                when {

                    !Validation.isEmailValid(email) ->
                        message = "Invalid email."

                    !Validation.isPasswordValid(newPassword) ->
                        message = "Password must be at least 6 characters."

                    else -> {

                        val success = authViewModel.resetPassword(
                            email,
                            newPassword
                        )

                        if (success) {
                            message = "Password updated successfully."
                        } else {
                            message = "Email not found."
                        }

                    }

                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Reset Password")

        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Back")

        }

        Spacer(modifier = Modifier.height(20.dp))

        if (message.isNotEmpty()) {

            Text(
                text = message,
                color = if (message.contains("successfully"))
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )

        }

    }

}