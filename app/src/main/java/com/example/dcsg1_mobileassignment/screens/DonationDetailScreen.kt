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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.utils.openLocationInMaps

@Composable
fun DonationDetailScreen(
    navController: NavController,
    donationId: String
) {
    val donation = CommunityPostStore.donations.firstOrNull { it.id == donationId }

    if (donation == null) {
        MissingDonationScreen(navController)
        return
    }

    val context = LocalContext.current
    val reserved = CommunityPostStore.reservedDonationIds.contains(donation.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
    ) {
        DonationDetailTopBar(onBack = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            RemoteDonationImage(
                imageUrl = CommunityPostStore.imageUrlForDonation(donation.id),
                fallbackTint = donation.tint,
                imageRes = donation.imageRes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                cornerRadius = 12.dp
            )

            Spacer(Modifier.height(20.dp))

            DonationHeader(donation, reserved)

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

            Spacer(Modifier.weight(1f))

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
                            CommunityPostStore.deleteDonation(donation.id)
                            Toast.makeText(context, "Donation deleted", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Text(
                            text = "Delete Post",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        if (reserved) {
                            CommunityPostStore.unreserveDonation(donation.id)
                            Toast.makeText(context, "Reservation cancelled", Toast.LENGTH_SHORT).show()
                        } else {
                            CommunityPostStore.reserveDonation(donation.id)
                            Toast.makeText(context, "Item reserved", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (reserved) Color(0xFFE53935) else CommunityColors.Green
                    )
                ) {
                    Text(
                        text = if (reserved) "Unreserve Item" else "Reserve This Item",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
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
private fun DonationHeader(donation: DonationPost, reserved: Boolean) {
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
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
            text = if (reserved) "Reserved" else "Free",
            color = if (reserved) CommunityColors.TextMuted else CommunityColors.Green,
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