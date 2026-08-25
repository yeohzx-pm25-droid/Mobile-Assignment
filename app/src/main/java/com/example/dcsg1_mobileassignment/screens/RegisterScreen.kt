package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.utils.Validation
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    var fullName by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var message by remember {
        mutableStateOf("")
    }


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


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // Full Name
        OutlinedTextField(
            value = fullName,

            onValueChange = {
                fullName = it
            },

            label = {
                Text("Full Name")
            },

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // Email
        OutlinedTextField(
            value = email,

            onValueChange = {
                email = it
            },

            label = {
                Text("Email")
            },

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // Phone
        OutlinedTextField(
            value = phone,

            onValueChange = {
                phone = it
            },

            label = {
                Text("Phone Number")
            },

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // Password
        OutlinedTextField(
            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Password")
            },

            visualTransformation = PasswordVisualTransformation(),

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // Register button
        Button(
            onClick = {

                when {

                    !Validation.isNameValid(fullName) -> {

                        message = "Please enter your name."

                    }


                    !Validation.isEmailValid(email) -> {

                        message = "Invalid email."

                    }


                    !Validation.isPhoneValid(phone) -> {

                        message = "Invalid phone number."

                    }


                    !Validation.isPasswordValid(password) -> {

                        message =
                            "Password must be at least 6 characters."

                    }


                    else -> {

                        val success = authViewModel.register(
                            fullName,
                            email,
                            phone,
                            password
                        )

                        if (success) {

                            message = "Registration Successful!"

                            navController.popBackStack()

                        } else {

                            message = "Email already exists."

                        }

                    }

                }

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Register")

        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // Back button
        OutlinedButton(
            onClick = {

                navController.popBackStack()

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Back to Login")

        }


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        // Message
        if (message.isNotEmpty()) {

            Text(
                text = message,

                color = if (
                    message.contains("Successful")
                ) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )

        }

    }

}