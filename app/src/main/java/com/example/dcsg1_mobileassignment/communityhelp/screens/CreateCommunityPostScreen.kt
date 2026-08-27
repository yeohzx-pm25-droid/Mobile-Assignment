package com.example.dcsg1_mobileassignment.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityData
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityStore
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost
import com.example.dcsg1_mobileassignment.communityhelp.model.PostType
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.communityhelp.screens.navigateSingleTop
import com.example.dcsg1_mobileassignment.communityhelp.validation.PostValidator

@Composable
fun CreateCommunityPostScreen(
    navController: NavController,
    initialType: PostType
) {
    val context = LocalContext.current
    var postType by remember { mutableStateOf(initialType) }

    var jobTitle by remember { mutableStateOf("") }
    var jobCategory by remember { mutableStateOf(CommunityData.jobCategories.first()) }
    var jobLocation by remember { mutableStateOf("") }
    var jobPayment by remember { mutableStateOf("") }
    var jobPaymentUnit by remember { mutableStateOf("Day") }
    var jobDescription by remember { mutableStateOf("") }

    var donationName by remember { mutableStateOf("") }
    var donationCategory by remember { mutableStateOf(CommunityData.donationCategories.first()) }
    var donationLocation by remember { mutableStateOf("") }
    var donationPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var donationDescription by remember { mutableStateOf("") }

    var leaveDialog by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        donationPhotoUri = uri
    }

    val hasInput = if (postType == PostType.Job) {
        jobTitle.isNotBlank() ||
                jobCategory != CommunityData.jobCategories.first() ||
                jobLocation.isNotBlank() ||
                jobPayment.isNotBlank() ||
                jobPaymentUnit != "Day" ||
                jobDescription.isNotBlank()
    } else {
        donationName.isNotBlank() ||
                donationCategory != CommunityData.donationCategories.first() ||
                donationLocation.isNotBlank() ||
                donationPhotoUri != null ||
                donationDescription.isNotBlank()
    }

    fun showAlert(title: String, message: String) {
        alertTitle = title
        alertMessage = message
    }

    fun goHome() {
        navController.navigateSingleTop("home")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
    ) {
        TopBar(
            title = if (postType == PostType.Job) "Create Post" else "Create Donation",
            onBack = {
                if (hasInput) {
                    leaveDialog = true
                } else {
                    goHome()
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            TypeSegment(postType) { postType = it }
            Spacer(Modifier.height(18.dp))

            if (postType == PostType.Job) {
                LabeledField("Title") {
                    InputField(jobTitle, { jobTitle = it }, "e.g. Cashier, Cleaner, Tutor")
                }
                CategoryDropdown("Category", jobCategory, CommunityData.jobCategories) {
                    jobCategory = it
                }
                LabeledField("Location") {
                    InputField(jobLocation, { jobLocation = it }, "e.g. George Town, Penang")
                }
                LabeledField("Salary / Payment") {
                    OutlinedTextField(
                        value = if (jobPaymentUnit == "Negotiable") "" else jobPayment,
                        onValueChange = { jobPayment = it.filter { char -> char.isDigit() } },
                        enabled = jobPaymentUnit != "Negotiable",
                        placeholder = { Text(if (jobPaymentUnit == "Negotiable") "Negotiable" else "e.g. 50") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                PaymentUnitRow(jobPaymentUnit) {
                    jobPaymentUnit = it
                    if (it == "Negotiable") {
                        jobPayment = ""
                    }
                }
                Spacer(Modifier.height(14.dp))
                LabeledField("Description") {
                    InputField(jobDescription, { jobDescription = it }, "Tell more about the job...", lines = 3)
                }
                Spacer(Modifier.height(54.dp))
                PrimaryButton("Post Now") {
                    if (jobTitle.isBlank() || jobLocation.isBlank() || jobDescription.isBlank()) {
                        showAlert("Incomplete Form", "Please complete the job title, location, and description.")
                        return@PrimaryButton
                    }
                    if (!PostValidator.isValidLocationWithState(jobLocation)) {
                        showAlert("Invalid Location", "Location must include city and state. Example: George Town, Penang.")
                        return@PrimaryButton
                    }
                    if (jobPaymentUnit != "Negotiable" && !PostValidator.isValidPaymentAmount(jobPayment)) {
                        showAlert("Invalid Payment", "Please enter numbers only. Example: 50.")
                        return@PrimaryButton
                    }

                    CommunityStore.addJob(
                        JobPost(
                            id = System.currentTimeMillis().toString(),
                            title = jobTitle.trim(),
                            category = jobCategory,
                            location = jobLocation.trim(),
                            payment = PostValidator.buildPayment(jobPayment, jobPaymentUnit),
                            description = jobDescription.trim(),
                            posted = "Posted just now",
                            mine = true
                        )
                    )
                    Toast.makeText(context, "Job posted successfully", Toast.LENGTH_SHORT).show()
                    navController.navigateSingleTop("jobs")
                }
            } else {
                LabeledField("Item Name") {
                    InputField(donationName, { donationName = it }, "e.g. Rice, Clothes, Shampoo")
                }
                CategoryDropdown("Item Category", donationCategory, CommunityData.donationCategories) {
                    donationCategory = it
                }
                LabeledField("Pickup Location") {
                    InputField(donationLocation, { donationLocation = it }, "e.g. George Town, Penang")
                }
                LabeledField("Description") {
                    InputField(donationDescription, { donationDescription = it }, "Tell more about the item...", lines = 4)
                }
                ImageUploadField(
                    photoUri = donationPhotoUri,
                    onChoosePhoto = { photoPicker.launch("image/*") }
                )
                Spacer(Modifier.height(40.dp))
                PrimaryButton("Post Donation") {
                    if (donationName.isBlank() || donationLocation.isBlank() || donationDescription.isBlank()) {
                        showAlert("Incomplete Form", "Please complete the item name, pickup location, and description.")
                        return@PrimaryButton
                    }
                    if (donationPhotoUri == null) {
                        showAlert("Photo Required", "Please choose one item photo before posting the donation.")
                        return@PrimaryButton
                    }
                    if (!PostValidator.isValidLocationWithState(donationLocation)) {
                        showAlert("Invalid Location", "Location must include city and state. Example: George Town, Penang.")
                        return@PrimaryButton
                    }

                    CommunityStore.addDonation(
                        DonationPost(
                            id = System.currentTimeMillis().toString(),
                            title = donationName.trim(),
                            category = donationCategory,
                            location = donationLocation.trim(),
                            description = donationDescription.trim(),
                            posted = "Posted just now",
                            tint = PostValidator.tintForCategory(donationCategory),
                            mine = true
                        )
                    )
                    Toast.makeText(context, "Donation posted successfully", Toast.LENGTH_SHORT).show()
                    navController.navigateSingleTop("donation")
                }
            }
        }
    }

    if (leaveDialog) {
        AlertDialog(
            onDismissRequest = { leaveDialog = false },
            title = { Text("Leave Form?") },
            text = { Text("You have entered information. Are you sure you want to leave without posting?") },
            confirmButton = {
                TextButton(onClick = {
                    leaveDialog = false
                    goHome()
                }) {
                    Text("Leave")
                }
            },
            dismissButton = {
                TextButton(onClick = { leaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (alertMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { alertMessage = "" },
            title = { Text(alertTitle) },
            text = { Text(alertMessage) },
            confirmButton = {
                TextButton(onClick = { alertMessage = "" }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun ImageUploadField(
    photoUri: Uri?,
    onChoosePhoto: () -> Unit
) {
    Column {
        Text("Item Image", color = CommunityColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onChoosePhoto,
            modifier = Modifier
                .fillMaxWidth()
                .height(94.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.FileUpload,
                    contentDescription = null,
                    tint = if (photoUri == null) CommunityColors.TextMuted else CommunityColors.Green
                )
                Text(
                    text = if (photoUri == null) "Upload image" else "Image selected",
                    color = if (photoUri == null) CommunityColors.TextMuted else CommunityColors.Green,
                    fontSize = 13.sp
                )
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(CommunityColors.Green)
            .padding(start = 18.dp, top = 26.dp, end = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(70.dp)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Text("<", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }
        Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TypeSegment(selected: PostType, onSelected: (PostType) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(CommunityColors.SoftSurface, RoundedCornerShape(10.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SegmentButton("Job", selected == PostType.Job, Modifier.weight(1f)) {
            onSelected(PostType.Job)
        }
        SegmentButton("Donation", selected == PostType.Donation, Modifier.weight(1f)) {
            onSelected(PostType.Donation)
        }
    }
}

@Composable
private fun SegmentButton(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (selected) CommunityColors.Green else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else CommunityColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun LabeledField(label: String, content: @Composable () -> Unit) {
    Column {
        Text(label, color = CommunityColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        content()
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun InputField(value: String, onValueChange: (String) -> Unit, hint: String, lines: Int = 1) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint) },
        singleLine = lines == 1,
        minLines = lines,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CategoryDropdown(label: String, selected: String, items: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    LabeledField(label) {
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(selected, color = CommunityColors.TextPrimary, modifier = Modifier.weight(1f))
                    Text("v", color = CommunityColors.TextMuted, fontWeight = FontWeight.Bold)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 220.dp)
            ) {

                    items.forEach { item ->
                        CategoryListItem(
                            label = item,
                            selected = item == selected
                        ) {
                            onSelected(item)
                            expanded = false
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }


@Composable
private fun CategoryListItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clickable { onClick() },
        color = if (selected) CommunityColors.Green else Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (selected) CommunityColors.Green else CommunityColors.FieldBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (selected) Color.White else CommunityColors.TextPrimary,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun PaymentUnitRow(selected: String, onSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CommunityData.paymentUnits.forEach { unit ->
            OutlinedButton(
                onClick = { onSelected(unit) },
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (unit == selected) CommunityColors.Green else Color.White,
                    contentColor = if (unit == selected) Color.White else CommunityColors.TextPrimary
                )
            ) {
                Text(unit, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun PrimaryButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green)
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
