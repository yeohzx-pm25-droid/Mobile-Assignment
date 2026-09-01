package com.example.dcsg1_mobileassignment.communityhelp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.utils.Validation
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel

// Register screen used by the combined app flow.
// It keeps the same fields as the teammate register screen, then opens Home after success.
@Composable
fun RegisterToHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var fullNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                fullNameError = false
            },
            label = { Text("Full Name") },
            isError = fullNameError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = {
                if (it.length <= 11) {
                    phone = it.filter { char -> char.isDigit() }
                    phoneError = false
                }
            },
            label = { Text("Phone Number (e.g. 01XXXXXXXX)") },
            isError = phoneError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            isError = passwordError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                // Reset errors
                fullNameError = false
                emailError = false
                phoneError = false
                passwordError = false

                when {
                    !Validation.isNameValid(fullName) -> {
                        message = "Please enter your name."
                        fullNameError = true
                        showErrorDialog = true
                    }

                    !Validation.isEmailValid(email) -> {
                        message = "Invalid email format (e.g. name@domain.com)."
                        emailError = true
                        showErrorDialog = true
                    }

                    !Validation.isPhoneValid(phone) -> {
                        message = "Invalid phone format. 011 starts must be 11 digits, others 10 digits."
                        phoneError = true
                        showErrorDialog = true
                    }

                    !Validation.isPasswordValid(password) -> {
                        message = "Password must be at least 8 characters, include uppercase, number, and special character (@#$%)."
                        passwordError = true
                        showErrorDialog = true
                    }

                    else -> {
                        scope.launch {
                            val error = authViewModel.register(
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                password = password
                            )

                            if (error == null) {
                                authViewModel.login(email, password)
                                navController.navigate("home") {
                                    popUpTo("login") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            } else {
                                // Show the real reason from Supabase instead of guessing.
                                message = error
                                showErrorDialog = true
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Registration Error") },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        if (message.isNotEmpty() && !showErrorDialog) {
            Spacer(Modifier.height(15.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
