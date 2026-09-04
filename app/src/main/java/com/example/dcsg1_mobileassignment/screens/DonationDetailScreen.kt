package com.example.dcsg1_mobileassignment.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.utils.Validation
import com.example.dcsg1_mobileassignment.utils.openLocationInMaps
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun DonationDetailScreen(
    navController: NavController,
    donationId: String,
    authViewModel: AuthViewModel
) {
    val donation = CommunityPostStore.donations.firstOrNull { it.id == donationId }

    if (donation == null) {
        MissingDonationScreen(navController)
        return
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val reserved = CommunityPostStore.reservedDonationIds.contains(donation.id)
    val myReservedQuantity = CommunityPostStore.reservedQuantityFor(donation.id)
    val remaining = CommunityPostStore.remainingQuantity(donation)
    val fullyReserved = CommunityPostStore.isFullyReserved(donation)
    var pickedQuantity by remember(donation.id) { mutableIntStateOf(1) }
    var isDeleting by remember { mutableStateOf(false) }
    var showReserveDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
    ) {
        DonationDetailTopBar(onBack = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            RemoteDonationImage(
                imageUrl = CommunityPostStore.imageUrlForDonation(donation.id),
                fallbackTint = donation.tint,
                imageRes = donation.imageRes,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 12.dp,
                contentScale = ContentScale.Fit,
                matchImageAspectRatio = true
            )

            Spacer(Modifier.height(20.dp))

            DonationHeader(donation, remaining, fullyReserved)

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
                text = donation.description,
                color = CommunityColors.TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = 34.dp, bottom = 18.dp),
                color = CommunityColors.FieldBorder
            )

            MapLocationCard(
                title = "Pickup Location",
                location = donation.location,
                onClick = { context.openLocationInMaps(donation.location) }
            )

            Spacer(Modifier.height(18.dp))

            DonationInfoRow(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = CommunityColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = donation.posted,
                underline = false
            )

            Spacer(Modifier.height(32.dp))

            if (donation.mine) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            navController.navigate("editPost/Donation/${donation.id}")
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
                                    val result = CommunityPostStore.deleteDonationFromSupabase(donation.id)
                                    isDeleting = false

                                    result
                                        .onSuccess {
                                            Toast.makeText(context, "Donation deleted successfully", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        }
                                        .onFailure { error ->
                                            Toast.makeText(context, error.message ?: "Delete failed. Please try again.", Toast.LENGTH_SHORT).show()
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

                Spacer(Modifier.height(12.dp))

                val totalReserved = CommunityPostStore.totalReservedQuantityFor(donation.id)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CommunityColors.FieldBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("donationReservers/${donation.id}") }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (totalReserved > 0) "View Reservations" else "No Reservations Yet",
                            color = CommunityColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (totalReserved > 0) {
                            Text(
                                text = "$totalReserved of ${donation.quantity} claimed",
                                color = CommunityColors.Green,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                if (reserved) {
                    Column {
                        val myStatus = CommunityPostStore.myReservationStatuses[donation.id]
                        Text(
                            text = when (myStatus) {
                                "accepted" -> "Your reservation was accepted!"
                                "rejected" -> "Your reservation was rejected."
                                else -> "You reserved $myReservedQuantity of this item. Waiting for the donor to accept."
                            },
                            color = when (myStatus) {
                                "accepted" -> CommunityColors.Green
                                "rejected" -> Color(0xFFE53935)
                                else -> CommunityColors.TextMuted
                            },
                            fontSize = 12.sp,
                            fontWeight = if (myStatus == "accepted" || myStatus == "rejected") FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    CommunityPostStore.unreserveDonationFromSupabase(donation.id)
                                        .onSuccess {
                                            Toast.makeText(context, "Reservation cancelled", Toast.LENGTH_SHORT).show()
                                        }
                                        .onFailure { error ->
                                            Toast.makeText(context, error.message ?: "Delete failed. Please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                        ) {
                            Text(
                                text = "Unreserve Item",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (fullyReserved) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CommunityColors.TextMuted,
                            disabledContainerColor = CommunityColors.TextMuted
                        )
                    ) {
                        Text(
                            text = "Fully Reserved",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Column {
                        if (donation.quantity > 1) {
                            QuantityStepper(
                                quantity = pickedQuantity,
                                maxQuantity = remaining,
                                onQuantityChange = { pickedQuantity = it }
                            )
                            Spacer(Modifier.height(14.dp))
                        }

                        Button(
                            onClick = { showReserveDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green)
                        ) {
                            Text(
                                text = if (donation.quantity > 1) "Reserve $pickedQuantity Item(s)" else "Reserve This Item",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showReserveDialog) {
        ReserveDonationDialog(
            initialName = authViewModel.currentUser?.fullName.orEmpty(),
            initialPhone = authViewModel.currentUser?.phone.orEmpty(),
            onDismiss = { showReserveDialog = false },
            onSubmit = { name, phone, age ->
                showReserveDialog = false
                val amount = if (donation.quantity > 1) pickedQuantity else 1
                coroutineScope.launch {
                    CommunityPostStore.reserveDonationToSupabase(
                        donationId = donation.id,
                        reserverName = name,
                        reserverPhone = phone,
                        reserverAge = age,
                        amount = amount
                    )
                        .onSuccess { actuallyReserved ->
                            if (actuallyReserved > 0) {
                                Toast.makeText(context, "Reserved $actuallyReserved item(s)", Toast.LENGTH_SHORT).show()
                                pickedQuantity = 1
                            } else {
                                Toast.makeText(context, "This item is fully reserved", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .onFailure { error ->
                            Toast.makeText(context, error.message ?: "Delete failed. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        )
    }
}

@Composable
private fun ReserveDonationDialog(
    initialName: String,
    initialPhone: String,
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, age: Int) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var age by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Your Details", fontWeight = FontWeight.Bold, color = CommunityColors.TextPrimary)
        },
        text = {
            Column {
                Text(
                    text = "The donor will see these details if they accept your reservation.",
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
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
                    !Validation.isAgeValid(age) -> error = "You must be 18 or older to reserve an item."
                    else -> onSubmit(name.trim(), phone.trim(), age.trim().toInt())
                }
            }) {
                Text("Submit Reservation", color = CommunityColors.Green, fontWeight = FontWeight.Bold)
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
private fun DonationDetailTopBar(onBack: () -> Unit) {
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
            text = "Item Details",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DonationHeader(donation: DonationPost, remaining: Int, fullyReserved: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(
                text = donation.title,
                color = CommunityColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = donation.category,
                color = CommunityColors.TextPrimary,
                fontSize = 12.sp
            )
        }

        Text(
            text = when {
                fullyReserved -> "Fully Reserved"
                donation.quantity <= 1 -> "Free"
                else -> "$remaining of ${donation.quantity} left"
            },
            color = if (fullyReserved) CommunityColors.TextMuted else CommunityColors.Green,
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
private fun QuantityStepper(
    quantity: Int,
    maxQuantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Quantity (max $maxQuantity)",
            color = CommunityColors.TextMuted,
            fontSize = 12.sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            StepperButton(
                icon = Icons.Filled.Remove,
                enabled = quantity > 1,
                onClick = { onQuantityChange((quantity - 1).coerceAtLeast(1)) }
            )

            Text(
                text = quantity.toString(),
                color = CommunityColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 18.dp)
            )

            StepperButton(
                icon = Icons.Filled.Add,
                enabled = quantity < maxQuantity,
                onClick = { onQuantityChange((quantity + 1).coerceAtMost(maxQuantity)) }
            )
        }
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) CommunityColors.Green else CommunityColors.FieldBorder,
        modifier = Modifier
            .size(34.dp)
            .clickable(enabled = enabled) { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun DonationInfoRow(
    icon: @Composable () -> Unit,
    text: String,
    underline: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
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
private fun MissingDonationScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Text("Item not found.", color = CommunityColors.TextPrimary, fontSize = 16.sp)
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