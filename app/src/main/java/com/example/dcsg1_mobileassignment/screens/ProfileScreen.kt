package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val user = authViewModel.currentUser


    if (user == null) {

        Column(
            modifier = Modifier.fillMaxSize(),

            verticalArrangement = Arrangement.Center,

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("No user is logged in.")


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            Button(
                onClick = {

                    navController.navigate("login")

                }
            ) {

                Text("Go to Login")

            }

        }

        return
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = "Profile",

            style = MaterialTheme.typography.headlineMedium
        )


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {


                // Full Name
                Text(
                    text = "Full Name: ${user.fullName}",

                    style = MaterialTheme.typography.bodyLarge
                )


                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                // Email
                Text(
                    text = "Email: ${user.email}",

                    style = MaterialTheme.typography.bodyLarge
                )


                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                // Phone
                Text(
                    text = "Phone: ${user.phone}",

                    style = MaterialTheme.typography.bodyLarge
                )

            }

        }


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        // Edit Profile
        Button(
            onClick = {

                navController.navigate("editProfile")

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Edit Profile")

        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // Logout
        OutlinedButton(
            onClick = {

                authViewModel.logout()

                navController.navigate("login") {

                    popUpTo("login") {

                        inclusive = true

                    }

                }

            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Logout")

        }

    }

}