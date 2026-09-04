package com.example.dcsg1_mobileassignment.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost
import com.example.dcsg1_mobileassignment.communityhelp.model.PostType
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.communityhelp.screens.navigateSingleTop
import com.example.dcsg1_mobileassignment.communityhelp.validation.LocationValidator
import com.example.dcsg1_mobileassignment.communityhelp.validation.PostValidator
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CreatePostScreen(
    navController: NavController,
    initialType: PostType,
    postId: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var postType by remember { mutableStateOf(initialType) }
    var isPosting by remember { mutableStateOf(false) }

    val existingJob = if (postId != null && postType == PostType.Job) {
        CommunityPostStore.jobs.firstOrNull { it.id == postId }
    } else null

    val existingDonation = if (postId != null && postType == PostType.Donation) {
        CommunityPostStore.donations.firstOrNull { it.id == postId }
    } else null

    var jobTitle by remember { mutableStateOf(existingJob?.title ?: "") }
    var jobCategory by remember { mutableStateOf(existingJob?.category ?: CommunityData.jobCategories.first()) }
    var jobLocation by remember { mutableStateOf(existingJob?.location ?: "") }

    val initialPayment = existingJob?.payment?.split(" ")?.firstOrNull()?.removePrefix("RM") ?: ""
    val initialUnit = existingJob?.payment?.split("/")?.lastOrNull()?.trim()?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Day"

    var jobPayment by remember { mutableStateOf(if (existingJob?.payment == "Negotiable") "" else initialPayment) }
    var jobPaymentUnit by remember { mutableStateOf(if (existingJob?.payment == "Negotiable") "Negotiable" else initialUnit) }
    var jobDescription by remember { mutableStateOf(existingJob?.description ?: "") }
    var jobIsUrgent by remember { mutableStateOf(existingJob?.isUrgent ?: false) }

    var donationName by remember { mutableStateOf(existingDonation?.title ?: "") }
    var donationCategory by remember { mutableStateOf(existingDonation?.category ?: CommunityData.donationCategories.first()) }
    var donationLocation by remember { mutableStateOf(existingDonation?.location ?: "") }
    var donationPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var donationDescription by remember { mutableStateOf(existingDonation?.description ?: "") }
    var donationQuantity by remember { mutableStateOf((existingDonation?.quantity ?: 1).toString()) }

    var leaveDialog by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }

    val isEditing = postId != null

    fun showAlert(title: String, message: String) {
        alertTitle = title
        alertMessage = message
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            donationPhotoUri = result.data?.data
        }
    }

    var cameraPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            donationPhotoUri = cameraPhotoUri
        }
    }

    fun openCamera() {
        val photoUri = context.createDonationPhotoUri()
        cameraPhotoUri = photoUri

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        if (cameraIntent.resolveActivity(context.packageManager) == null) {
            showAlert("Camera Unavailable", "This device does not have a camera app.")
            return
        }

        cameraLauncher.launch(cameraIntent)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            showAlert("Camera Permission Required", "Please allow camera permission to take an item photo.")
        }
    }

    fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        photoPicker.launch(galleryIntent)
    }

    fun requestCameraPhoto() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val hasInput = if (postType == PostType.Job) {
        jobTitle.isNotBlank() ||
                jobCategory != CommunityData.jobCategories.first() ||
                jobLocation.isNotBlank() ||
                jobPayment.isNotBlank() ||
                jobPaymentUnit != "Day" ||
                jobDescription.isNotBlank() ||
                jobIsUrgent
    } else {
        donationName.isNotBlank() ||
                donationCategory != CommunityData.donationCategories.first() ||
                donationLocation.isNotBlank() ||
                donationPhotoUri != null ||
                donationDescription.isNotBlank() ||
                donationQuantity != "1"
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
            title = if (isEditing) "Edit Post" else if (postType == PostType.Job) "Create Post" else "Create Donation",
            onBack = {
                if (hasInput && !isEditing) {
                    leaveDialog = true
                } else {
                    navController.popBackStack()
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            if (!isEditing) {
                TypeSegment(postType) { postType = it }
                Spacer(Modifier.height(18.dp))
            }

            if (postType == PostType.Job) {
                LabeledField("Title") {
                    InputField(jobTitle, { jobTitle = it }, "e.g. Cashier, Cleaner, Tutor")
                }
                CategoryDropdown("Category", jobCategory, CommunityData.jobCategories) {
                    jobCategory = it
                }
                LabeledField("Location") {
                    InputField(jobLocation, { jobLocation = it }, "e.g. 77 Lorong Lembah Permai 3, Tanjong Bungah, Pulau Pinang.")
                }
                LabeledField("Salary / Payment") {
                    OutlinedTextField(
                        value = if (jobPaymentUnit == "Negotiable") "" else jobPayment,
                        onValueChange = { jobPayment = it.filter { char -> char.isDigit() } },
                        enabled = jobPaymentUnit != "Negotiable",
                        placeholder = { Text(if (jobPaymentUnit == "Negotiable") "Negotiable" else "e.g. 50") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White
                        ),
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
                UrgentJobToggle(
                    isUrgent = jobIsUrgent,
                    onUrgentChange = { jobIsUrgent = it }
                )
                Spacer(Modifier.height(18.dp))
                PrimaryButton(
                    label = if (isPosting) "Posting..." else if (isEditing) "Save Changes" else "Post Now"
                ) {
                    if (isPosting) {
                        return@PrimaryButton
                    }
                    if (jobTitle.isBlank() || jobLocation.isBlank() || jobDescription.isBlank()) {
                        showAlert("Incomplete Form", "Please complete the job title, location, and description.")
                        return@PrimaryButton
                    }
                    if (!PostValidator.isValidLocationWithState(jobLocation)) {
                        showAlert("Invalid Location", "Location must include a full Malaysia address and state. Example: 178, Taman Mangga, 05400 Alor Setar, Kedah.")
                        return@PrimaryButton
                    }
                    if (jobPaymentUnit != "Negotiable" && !PostValidator.isValidPaymentAmount(jobPayment)) {
                        showAlert("Invalid Payment", "Please enter numbers only. Example: 50.")
                        return@PrimaryButton
                    }

                    isPosting = true
                    scope.launch {
                        if (!LocationValidator.isRealMalaysiaLocation(context, jobLocation)) {
                            isPosting = false
                            showAlert("Invalid Location", "Please enter a real Malaysia address that can be found on the map. Example: 178, Taman Mangga, 05400 Alor Setar, Kedah.")
                            return@launch
                        }

                        val job = JobPost(
                            id = existingJob?.id ?: System.currentTimeMillis().toString(),
                            title = jobTitle.trim(),
                            category = jobCategory,
                            location = jobLocation.trim(),
                            payment = PostValidator.buildPayment(jobPayment, jobPaymentUnit),
                            description = jobDescription.trim(),
                            posted = existingJob?.posted ?: "Posted just now",
                            mine = true,
                            isUrgent = jobIsUrgent
                        )

                        if (isEditing) {
                            CommunityPostStore.updateJob(job)
                            isPosting = false
                            Toast.makeText(context, "Job updated successfully", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            val result = CommunityPostStore.addJobToSupabase(
                                title = jobTitle,
                                category = jobCategory,
                                location = jobLocation,
                                payment = jobPayment,
                                paymentUnit = jobPaymentUnit,
                                description = jobDescription,
                                isUrgent = jobIsUrgent
                            )
                            isPosting = false

                            result.onSuccess {
                                Toast.makeText(context, "Job posted successfully", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }.onFailure { error ->
                                showAlert("Post Failed", error.message ?: "Unable to post job. Please try again.")
                            }
                        }
                    }
                }
            } else {
                LabeledField("Item Name") {
                    InputField(donationName, { donationName = it }, "e.g. Rice, Clothes, Shampoo")
                }
                CategoryDropdown("Item Category", donationCategory, CommunityData.donationCategories) {
                    donationCategory = it
                }
                LabeledField("Quantity") {
                    InputField(
                        donationQuantity,
                        { new -> if (new.length <= 4 && new.all { it.isDigit() }) donationQuantity = new },
                        "e.g. 10"
                    )
                }
                LabeledField("Pickup Location") {
                    InputField(donationLocation, { donationLocation = it }, "Please Enter a Full Malaysia Address.")
                }
                LabeledField("Description") {
                    InputField(donationDescription, { donationDescription = it }, "Tell more about the item...", lines = 4)
                }
                if (!isEditing) {
                    ImageUploadField(
                        photoUri = donationPhotoUri,
                        onChoosePhoto = { openGallery() },
                        onTakePhoto = { requestCameraPhoto() }
                    )
                }
                Spacer(Modifier.height(40.dp))
                PrimaryButton(
                    label = if (isPosting) "Posting..." else if (isEditing) "Save Changes" else "Post Donation"
                ) {
                    if (isPosting) {
                        return@PrimaryButton
                    }
                    if (donationName.isBlank() || donationLocation.isBlank() || donationDescription.isBlank()) {
                        showAlert("Incomplete Form", "Please complete the item name, pickup location, and description.")
                        return@PrimaryButton
                    }
                    val parsedQuantity = donationQuantity.trim().toIntOrNull()
                    if (parsedQuantity == null || parsedQuantity < 1) {
                        showAlert("Invalid Quantity", "Please enter how many of this item you're giving away (at least 1).")
                        return@PrimaryButton
                    }
                    if (!isEditing && donationPhotoUri == null) {
                        showAlert("Photo Required", "Please choose one item photo before posting the donation.")
                        return@PrimaryButton
                    }
                    if (!PostValidator.isValidLocationWithState(donationLocation)) {
                        showAlert("Invalid Location", "Location must include a full Malaysia address and state. Example: 178, Taman Mangga, 05400 Alor Setar, Kedah.")
                        return@PrimaryButton
                    }
                    val selectedPhotoUri = donationPhotoUri

                    isPosting = true
                    scope.launch {
                        if (!LocationValidator.isRealMalaysiaLocation(context, donationLocation)) {
                            isPosting = false
                            showAlert("Invalid Location", "Please enter a real Malaysia address that can be found on the map. Example: 178, Taman Mangga, 05400 Alor Setar, Kedah.")
                            return@launch
                        }

                        val donation = DonationPost(
                            id = existingDonation?.id ?: System.currentTimeMillis().toString(),
                            title = donationName.trim(),
                            category = donationCategory,
                            location = donationLocation.trim(),
                            description = donationDescription.trim(),
                            posted = existingDonation?.posted ?: "Posted just now",
                            tint = PostValidator.tintForCategory(donationCategory),
                            quantity = parsedQuantity,
                            mine = true
                        )

                        if (isEditing) {
                            CommunityPostStore.updateDonation(donation)
                            isPosting = false
                            Toast.makeText(context, "Donation updated successfully", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            if (selectedPhotoUri == null) {
                                isPosting = false
                                showAlert("Photo Required", "Please choose one item photo before posting the donation.")
                                return@launch
                            }

                            val result = CommunityPostStore.addDonationToSupabase(
                                context = context,
                                itemName = donationName,
                                itemCategory = donationCategory,
                                pickupLocation = donationLocation,
                                description = donationDescription,
                                photoUri = selectedPhotoUri,
                                quantity = parsedQuantity
                            )
                            isPosting = false

                            result.onSuccess {
                                Toast.makeText(context, "Donation posted successfully", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }.onFailure { error ->
                                showAlert("Post Failed", error.message ?: "Unable to post donation. Please try again.")
                            }
                        }
                    }
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
private fun UrgentJobToggle(
    isUrgent: Boolean,
    onUrgentChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUrgentChange(!isUrgent) },
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CommunityColors.FieldBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = "Urgent Job",
                    color = CommunityColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tick this if urgent hiring",
                    color = CommunityColors.TextMuted,
                    fontSize = 11.sp
                )
            }

            Checkbox(
                checked = isUrgent,
                onCheckedChange = onUrgentChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = CommunityColors.Green,
                    uncheckedColor = CommunityColors.TextMuted,
                    checkmarkColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun ImageUploadField(
    photoUri: Uri?,
    onChoosePhoto: () -> Unit,
    onTakePhoto: () -> Unit
) {
    Column {
        Text("Item Image", color = CommunityColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onChoosePhoto,
            modifier = Modifier
                .fillMaxWidth()
                .height(94.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
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
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onChoosePhoto,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text("Choose Photo", fontSize = 12.sp, color = CommunityColors.TextPrimary)
            }
            OutlinedButton(
                onClick = onTakePhoto,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text("Take Photo", fontSize = 12.sp, color = CommunityColors.TextPrimary)
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

private fun Context.createDonationPhotoUri(): Uri {
    val imageDirectory = File(cacheDir, "donation_images").apply {
        mkdirs()
    }
    val imageFile = File.createTempFile("donation_", ".jpg", imageDirectory)
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
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
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White
        ),
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
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(selected, color = CommunityColors.TextPrimary, modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = CommunityColors.TextMuted
                    )
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