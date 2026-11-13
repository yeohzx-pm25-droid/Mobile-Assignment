package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.utils.Validation
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.dcsg1_mobileassignment.utils.InputLimits
import com.example.dcsg1_mobileassignment.utils.limitTo
import com.example.dcsg1_mobileassignment.utils.digitsOnly
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun RegisterScreen(
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

    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 26.dp)
    ) {
        // Scrollable so every field and the Register button can be reached in landscape.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 56.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Account",
                color = CommunityColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Join FinGrow today!",
                color = CommunityColors.TextMuted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(28.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it.limitTo(InputLimits.NAME)
                    fullNameError = false
                },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = CommunityColors.TextMuted)
                },
                placeholder = { Text("Full Name") },
                singleLine = true,
                isError = fullNameError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    errorContainerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it.trim().limitTo(InputLimits.EMAIL)
                    emailError = false
                },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = CommunityColors.TextMuted)
                },
                placeholder = { Text("Email") },
                singleLine = true,
                isError = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    errorContainerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it.limitTo(InputLimits.PASSWORD)
                    passwordError = false
                },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = CommunityColors.TextMuted)
                },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = if (passwordVisible) "Hide password" else "Show password", tint = CommunityColors.TextMuted)
                    }
                },
                placeholder = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                isError = passwordError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    errorContainerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it.digitsOnly(InputLimits.PHONE)
                    phoneError = false
                },
                leadingIcon = {
                    Icon(Icons.Filled.Phone, contentDescription = null, tint = CommunityColors.TextMuted)
                },
                placeholder = { Text("Phone Number (e.g. 01XXXXXXXX)") },
                singleLine = true,
                isError = phoneError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    errorContainerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

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
                        isLoading -> Unit
                        else -> {
                            isLoading = true
                            scope.launch {
                                val error = authViewModel.register(
                                    fullName = fullName.trim(),
                                    email = email.trim(),
                                    phone = phone,
                                    password = password
                                )
                                isLoading = false

                                if (error == null) {
                                    Toast.makeText(
                                        context,
                                        "Account created. Please check your email to verify, then log in.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate("login") {
                                        popUpTo("login") {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                } else {
                                    // Show a short friendly message instead of the backend error.
                                    message = error
                                    emailError = true
                                    showErrorDialog = true
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (isLoading) "Creating account..." else "Register", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(22.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = CommunityColors.TextMuted, fontSize = 12.sp)
                Text(
                    text = "Login",
                    color = CommunityColors.Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    // Go back to the existing Login screen instead of stacking a new one.
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
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
                Spacer(Modifier.height(14.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
        }

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
