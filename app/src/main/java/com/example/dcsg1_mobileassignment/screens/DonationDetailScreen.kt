package com.example.dcsg1_mobileassignment.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityStore
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors

@Composable
fun DonationDetailScreen(
    navController: NavController,
    donationId: String
) {
    val donation = CommunityStore.donations.firstOrNull { it.id == donationId }

    if (donation == null) {
        navController.popBackStack()
        return
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
    ) {
        DonationDetailTopBar(onBack = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = donation.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CommunityColors.TextPrimary
            )
            Text(
                text = donation.category,
                fontSize = 14.sp,
                color = CommunityColors.Green,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider(Modifier.padding(vertical = 16.dp), color = CommunityColors.FieldBorder)

            Text("Description", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text(donation.description, fontSize = 13.sp, color = CommunityColors.TextPrimary)

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = CommunityColors.TextMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(donation.location, fontSize = 12.sp, color = CommunityColors.TextMuted)
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = CommunityColors.TextMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(donation.posted, fontSize = 12.sp, color = CommunityColors.TextMuted)
            }

            Spacer(Modifier.weight(1f))

            if (donation.mine) {
                Button(
                    onClick = {
                        CommunityStore.deleteDonation(donation.id)
                        Toast.makeText(context, "Donation deleted successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Delete Post", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        CommunityStore.reserveDonation(donation.id)
                        Toast.makeText(context, "Item reserved successfully", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green)
                ) {
                    Text("Reserve Item", color = Color.White, fontWeight = FontWeight.Bold)
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
            .padding(start = 18.dp, top = 26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "<",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onBack() }.padding(10.dp)
        )
        Text("Item Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
