package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.dcsg1_mobileassignment.R
import androidx.compose.foundation.background
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.dcsg1_mobileassignment.utils.InputLimits
import com.example.dcsg1_mobileassignment.utils.limitTo
import com.example.dcsg1_mobileassignment.ui.isLandscape
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.fingrow_fulllogo),
            contentDescription = "FinGrow Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(if (isLandscape()) 90.dp else 140.dp)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Login to continue to FinGrow",
            color = CommunityColors.TextMuted,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(if (isLandscape()) 20.dp else 42.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it.trim().limitTo(InputLimits.EMAIL)
                message = ""
            },
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = null, tint = CommunityColors.TextMuted)
            },
            placeholder = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it.limitTo(InputLimits.PASSWORD)
                message = ""
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { navController.navigate("forgotPassword") }) {
                Text("Forgot Password?", color = CommunityColors.Green, fontSize = 11.sp)
            }
        }

        Button(
            enabled = !isLoading,
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    message = "Please enter your email and password."
                    return@Button
                }
                isLoading = true
                scope.launch {
                    val error = authViewModel.login(email.trim(), password)
                    isLoading = false
                    if (error == null) {
                        navController.navigate("profile") {
                            popUpTo("login") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    } else {
                        message = error
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(if (isLoading) "Logging in..." else "Login", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                message = ""
                authViewModel.loginWithGoogle()
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Login with Google", color = CommunityColors.TextPrimary)
        }

        Spacer(Modifier.height(22.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Don't have an account? ",
                color = CommunityColors.TextMuted,
                fontSize = 12.sp
            )
            Text(
                text = "Register",
                color = CommunityColors.Green,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate("register") }
            )
        }

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
}
