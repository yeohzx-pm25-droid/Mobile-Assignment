package com.example.dcsg1_mobileassignment.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.utils.Validation
import com.example.dcsg1_mobileassignment.utils.openLocationInMaps
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun JobDetailScreen(
    navController: NavController,
    jobId: String,
    authViewModel: AuthViewModel
) {
    val job = CommunityPostStore.jobs.firstOrNull { it.id == jobId }

    if (job == null) {
        MissingJobScreen(navController)
        return
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isDeleting by remember { mutableStateOf(false) }
    var showApplyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
    ) {
        JobDetailTopBar(
            onBack = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            JobHeader(job)

            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp, bottom = 26.dp),
                color = CommunityColors.FieldBorder
            )

            Text(
                text = "Description",
                color = CommunityColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = job.description,
                color = CommunityColors.TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = 34.dp, bottom = 18.dp),
                color = CommunityColors.FieldBorder
            )

            MapLocationCard(
                title = "Location",
                location = job.location,
                onClick = { context.openLocationInMaps(job.location) }
            )

            Spacer(Modifier.height(18.dp))

            InfoRow(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = CommunityColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = JobTimeFormatter.postedLine(job),
                underline = false
            )

            Spacer(Modifier.weight(1f))

            if (job.mine) {
                Column {
                    Button(
                        onClick = {
                            navController.navigate("jobApplicants/${job.id}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green)
                    ) {
                        Text(
                            text = "View Applicants",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("editPost/Job/${job.id}")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green)
                        ) {
                            Text(
                                text = "Edit Post",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                if (!isDeleting) {
                                    isDeleting = true
                                    coroutineScope.launch {
                                        val result = CommunityPostStore.deleteJobFromSupabase(job.id)
                                        isDeleting = false

                                        result
                                            .onSuccess {
                                                Toast.makeText(context, "Job deleted successfully", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack()
                                            }
                                            .onFailure {
                                                Toast.makeText(
                                                    context,
                                                    "Delete failed. Please try again.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                }
                            },
                            enabled = !isDeleting,
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935),
                                disabledContainerColor = Color(0xFFE57373)
                            )
                        ) {
                            Text(
                                text = if (isDeleting) "Deleting..." else "Delete Post",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                val alreadyApplied = CommunityPostStore.appliedJobIds.contains(job.id)
                val applicationStatus = CommunityPostStore.appliedJobStatuses[job.id]

                if (alreadyApplied && applicationStatus != null && applicationStatus != "pending") {
                    Text(
                        text = when (applicationStatus) {
                            "accepted" -> "Your application was accepted!"
                            "rejected" -> "Your application was rejected."
                            else -> "Application status: $applicationStatus"
                        },
                        color = when (applicationStatus) {
                            "accepted" -> CommunityColors.Green
                            "rejected" -> Color(0xFFE53935)
                            else -> CommunityColors.TextMuted
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = {
                        if (alreadyApplied) {
                            coroutineScope.launch {
                                try {
                                    CommunityPostStore.unapplyFromJob(job.id)
                                    Toast.makeText(context, "Application withdrawn", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            showApplyDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alreadyApplied) Color(0xFFE53935) else CommunityColors.Green
                    )
                ) {
                    Text(
                        text = if (alreadyApplied) "Unapply" else "Apply Now",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showApplyDialog) {
        ApplyToJobDialog(
            initialName = authViewModel.currentUser?.fullName.orEmpty(),
            initialPhone = authViewModel.currentUser?.phone.orEmpty(),
            onDismiss = { showApplyDialog = false },
            onSubmit = { name, phone, age, message ->
                showApplyDialog = false
                coroutineScope.launch {
                    try {
                        CommunityPostStore.applyToJob(job.id, name, phone, age, message)
                        Toast.makeText(context, "Applied successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

// Person 2 fills in their personal details here before applying, so Person 1
// (the job poster) can see who they are once they choose to accept.
@Composable
private fun ApplyToJobDialog(
    initialName: String,
    initialPhone: String,
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, age: Int, message: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var age by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Your Details", fontWeight = FontWeight.Bold, color = CommunityColors.TextPrimary)
        },
        text = {
            Column {
                Text(
                    text = "The job poster will see these details if they accept your application.",
                    color = CommunityColors.TextMuted,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = "" },
                    label = { Text("Full Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; error = "" },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter { ch -> ch.isDigit() }; error = "" },
                    label = { Text("Age") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message to poster (optional)") },
                    placeholder = { Text("E.g. relevant experience, availability") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = Color(0xFFE53935), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    !Validation.isNameValid(name) -> error = "Please enter your name."
                    !Validation.isPhoneValid(phone) -> error = "Please enter a valid Malaysian phone number."
                    age.trim().toIntOrNull() == null -> error = "Please enter your age."
                    !Validation.isAgeValid(age) -> error = "You must be 18 or older to apply."
                    else -> onSubmit(name.trim(), phone.trim(), age.trim().toInt(), message.trim())
                }
            }) {
                Text("Submit Application", color = CommunityColors.Green, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CommunityColors.TextMuted)
            }
        }
    )
}

@Composable
private fun JobDetailTopBar(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(CommunityColors.Green)
            .padding(start = 18.dp, top = 26.dp, end = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "Job Details",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun JobHeader(job: JobPost) {
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(
                text = job.title,
                color = CommunityColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = job.category,
                color = CommunityColors.TextPrimary,
                fontSize = 12.sp
            )
        }

        Text(
            text = job.payment,
            color = CommunityColors.Green,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MapLocationCard(
    title: String,
    location: String,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = title,
            color = CommunityColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEAF2EC))
                .clickable { onClick() }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val mainRoad = Color(0xFFD3DDD4)
                val smallRoad = Color(0xFFFFFFFF).copy(alpha = 0.9f)
                val park = Color(0xFFCFE8D1)

                drawRect(
                    color = park,
                    topLeft = Offset(size.width * 0.54f, 0f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.46f, size.height * 0.34f)
                )
                drawLine(
                    color = mainRoad,
                    start = Offset(-20f, size.height * 0.72f),
                    end = Offset(size.width + 20f, size.height * 0.54f),
                    strokeWidth = 18f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = mainRoad,
                    start = Offset(size.width * 0.16f, -20f),
                    end = Offset(size.width * 0.34f, size.height + 20f),
                    strokeWidth = 16f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = smallRoad,
                    start = Offset(-20f, size.height * 0.32f),
                    end = Offset(size.width + 20f, size.height * 0.24f),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = smallRoad,
                    start = Offset(size.width * 0.68f, -20f),
                    end = Offset(size.width * 0.78f, size.height + 20f),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }

            Surface(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(25.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = CommunityColors.Green,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = CommunityColors.TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = location,
                    color = CommunityColors.TextPrimary,
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: @Composable () -> Unit,
    text: String,
    underline: Boolean,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick == null) {
        Modifier
    } else {
        Modifier.clickable { onClick() }
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(14.dp))
        Text(
            text = text,
            color = if (underline) CommunityColors.TextPrimary else CommunityColors.TextMuted,
            fontSize = 12.sp,
            textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None
        )
    }
}

@Composable
private fun MissingJobScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Text("Job not found.", color = CommunityColors.TextPrimary, fontSize = 16.sp)
        Spacer(Modifier.height(20.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, CommunityColors.Green),
            color = Color.Transparent,
            modifier = Modifier.clickable { navController.popBackStack() }
        ) {
            Text(
                text = "Back",
                color = CommunityColors.Green,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )
        }
        Spacer(Modifier.weight(1f))
    }
}