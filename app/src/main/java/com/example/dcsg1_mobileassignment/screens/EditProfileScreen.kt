package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.utils.Validation
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel

@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val user = authViewModel.currentUser

    if (user == null) {

        Text("No user found.")

        return
    }


    var fullName by remember {

        mutableStateOf(user.fullName)

    }


    var phone by remember {

        mutableStateOf(user.phone)

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
            text = "Edit Profile",

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
            modifier = Modifier.height(20.dp)
        )


        // Save
        Button(
            onClick = {

                when {

                    !Validation.isNameValid(fullName) -> {

                        message = "Name cannot be empty."

                    }


                    !Validation.isPhoneValid(phone) -> {

                        message = "Invalid phone number."

                    }


                    else -> {

                        authViewModel.updateProfile(
                            fullName,
                            phone
                        )

                        message =
                            "Profile updated successfully."

                    }

                }

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Save Changes")

        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // Back
        OutlinedButton(
            onClick = {

                navController.popBackStack()

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Back")

        }


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        // Message
        if (message.isNotEmpty()) {

            Text(
                text = message,

                color = if (
                    message.contains("successfully")
                ) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )

        }

    }

}