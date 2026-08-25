package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var message by remember {
        mutableStateOf("")
    }


    // ==========================================
    // CHECK IF USER SUCCESSFULLY LOGGED IN
    // ==========================================

    val currentUser = authViewModel.currentUser

    LaunchedEffect(currentUser) {

        if (currentUser != null) {

            navController.navigate("profile") {

                popUpTo("profile") {
                    inclusive = true
                }

            }

        }

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // ==========================================
        // EMAIL
        // ==========================================

        OutlinedTextField(
            value = email,

            onValueChange = {

                email = it
                message = ""

            },

            label = {
                Text("Email")
            },

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // ==========================================
        // PASSWORD
        // ==========================================

        OutlinedTextField(
            value = password,

            onValueChange = {

                password = it
                message = ""

            },

            label = {
                Text("Password")
            },

            visualTransformation = PasswordVisualTransformation(),

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        // ==========================================
        // NORMAL LOGIN
        // ==========================================

        Button(
            onClick = {

                val success = authViewModel.login(
                    email,
                    password
                )

                if (!success) {

                    message = "Invalid email or password."

                }

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Login")

        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // ==========================================
        // GOOGLE LOGIN
        // ==========================================

        OutlinedButton(
            onClick = {

                message = ""

                authViewModel.loginWithGoogle()

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Continue with Google")

        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // ==========================================
        // FACEBOOK LOGIN
        // ==========================================

        OutlinedButton(
            onClick = {

                message = ""

                authViewModel.loginWithFacebook()

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Continue with Facebook")

        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // ==========================================
        // REGISTER
        // ==========================================

        OutlinedButton(
            onClick = {

                navController.navigate("register")

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Register")

        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // ==========================================
        // FORGOT PASSWORD
        // ==========================================

        TextButton(
            onClick = {

                navController.navigate("forgotPassword")

            }
        ) {

            Text("Forgot Password?")

        }


        // ==========================================
        // ERROR MESSAGE
        // ==========================================

        if (message.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )

        }

    }

}