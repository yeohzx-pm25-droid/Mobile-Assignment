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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val user = authViewModel.currentUser

    if (user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CommunityColors.Surface),
            contentAlignment = Alignment.Center
        ) {
            Text("No user found.", color = CommunityColors.TextPrimary)
        }
        return
    }

    var fullName by remember { mutableStateOf(user.fullName) }
    var phone by remember { mutableStateOf(user.phone) }
    var message by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
            .padding(horizontal = 26.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = CommunityColors.TextPrimary,
            modifier = Modifier
                .padding(top = 42.dp)
                .size(24.dp)
                .clickable { navController.popBackStack() }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edit Profile",
                color = CommunityColors.Green,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Update your account details",
                color = CommunityColors.TextMuted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(42.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    message = ""
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
                value = phone,
                onValueChange = {
                    if (it.length <= 11) {
                        phone = it.filter { char -> char.isDigit() }
                        phoneError = false
                    }
                    message = ""
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

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    fullNameError = false
                    phoneError = false

                    when {
                        !Validation.isNameValid(fullName) -> {
                            message = "Name cannot be empty."
                            fullNameError = true
                            showErrorDialog = true
                        }
                        !Validation.isPhoneValid(phone) -> {
                            message = "Invalid phone format. 011 starts must be 11 digits, others 10 digits."
                            phoneError = true
                            showErrorDialog = true
                        }
                        else -> {
                            authViewModel.updateProfile(fullName, phone)
                            message = "Profile updated successfully."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("Update Error") },
                    text = { Text(message) },
                    confirmButton = {
                        TextButton(onClick = { showErrorDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            if (message.isNotEmpty() && !showErrorDialog) {
                Spacer(Modifier.height(18.dp))
                Text(
                    text = message,
                    color = if (message.contains("successfully")) {
                        CommunityColors.Green
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontSize = 12.sp
                )
            }
        }
    }
}
