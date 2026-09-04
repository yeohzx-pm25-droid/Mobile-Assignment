package com.example.dcsg1_mobileassignment.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import kotlinx.coroutines.launch

// Person 1's (the donor's) view of everyone who reserved their donation item,
// where they choose to accept or reject each reservation.
@Composable
fun DonationReserversScreen(
    navController: NavController,
    donationId: String
) {
    val donation = CommunityPostStore.donations.firstOrNull { it.id == donationId }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var reservations by remember { mutableStateOf<List<DonationReservation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf(false) }

    LaunchedEffect(donationId) {
        isLoading = true
        loadError = false
        CommunityPostStore.loadReserversForDonation(donationId)
            .onSuccess { reservations = it }
            .onFailure { loadError = true }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
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
                    .clickable { navController.popBackStack() },
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
                text = donation?.title?.let { "Reservations - $it" } ?: "Reservations",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CommunityColors.Green)
                }
            }

            loadError -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Failed to load reservations. Please try again.", color = CommunityColors.TextMuted)
                }
            }

            reservations.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No reservations yet.", color = CommunityColors.TextMuted, fontSize = 14.sp)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reservations, key = { it.id }) { reservation ->
                        ReserverCard(
                            reservation = reservation,
                            onAccept = {
                                coroutineScope.launch {
                                    CommunityPostStore.setReservationStatus(reservation, "accepted")
                                        .onSuccess {
                                            reservations = reservations.map {
                                                if (it.id == reservation.id) it.copy(status = "accepted") else it
                                            }
                                        }
                                        .onFailure {
                                            Toast.makeText(context, "Failed to update. Please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            },
                            onReject = {
                                coroutineScope.launch {
                                    CommunityPostStore.setReservationStatus(reservation, "rejected")
                                        .onSuccess {
                                            reservations = reservations.map {
                                                if (it.id == reservation.id) it.copy(status = "rejected") else it
                                            }
                                        }
                                        .onFailure {
                                            Toast.makeText(context, "Failed to update. Please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReserverCard(
    reservation: DonationReservation,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.reserverName ?: "Unknown",
                    color = CommunityColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    QuantityBadge(reservation.quantity)
                    Spacer(Modifier.width(8.dp))
                    ReservationStatusBadge(reservation.status)
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = reservation.reserverPhone ?: "No phone provided",
                color = CommunityColors.TextMuted,
                fontSize = 13.sp
            )

            reservation.reserverAge?.let { age ->
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Age: $age",
                    color = CommunityColors.TextMuted,
                    fontSize = 13.sp
                )
            }

            if (reservation.status == "pending") {
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green)
                    ) {
                        Text("Accept", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Text("Reject", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuantityBadge(quantity: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CommunityColors.Green.copy(alpha = 0.15f)
    ) {
        Text(
            text = if (quantity == 1) "1 item" else "$quantity items",
            color = CommunityColors.Green,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ReservationStatusBadge(status: String) {
    val (label, color) = when (status) {
        "accepted" -> "Accepted" to CommunityColors.Green
        "rejected" -> "Rejected" to Color(0xFFE53935)
        else -> "Pending" to Color(0xFFF5A623)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}