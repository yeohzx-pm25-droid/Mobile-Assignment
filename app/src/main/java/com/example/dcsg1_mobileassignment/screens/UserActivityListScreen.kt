package com.example.dcsg1_mobileassignment.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors

@Composable
fun UserActivityListScreen(
    navController: NavController,
    activityType: String
) {
    val title = when (activityType) {
        "appliedJobs" -> "My Job Applications"
        "myJobs" -> "My Posted Jobs"
        "myDonations" -> "My Donated Items"
        "reservedDonations" -> "My Reserved Items"
        else -> "My Activity"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(CommunityColors.Green)
                .padding(start = 18.dp, top = 26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
            Spacer(Modifier.size(16.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            when (activityType) {
                "appliedJobs" -> {
                    val appliedJobs = CommunityPostStore.jobs.filter { CommunityPostStore.appliedJobIds.contains(it.id) }
                    if (appliedJobs.isEmpty()) {
                        item { EmptyState("No job applications yet.") }
                    } else {
                        items(appliedJobs) { job ->
                            JobListCard(job) {
                                navController.navigate("jobDetail/${job.id}")
                            }
                        }
                    }
                }
                "myJobs" -> {
                    val myJobs = CommunityPostStore.jobs.filter { it.mine }
                    if (myJobs.isEmpty()) {
                        item { EmptyState("You haven't posted any jobs.") }
                    } else {
                        items(myJobs) { job ->
                            JobListCard(job) {
                                navController.navigate("jobDetail/${job.id}")
                            }
                        }
                    }
                }
                "myDonations" -> {
                    val myDonations = CommunityPostStore.donations.filter { it.mine }
                    if (myDonations.isEmpty()) {
                        item { EmptyState("You haven't donated any items.") }
                    } else {
                        items(myDonations) { donation ->
                            DonationItemCard(donation) {
                                navController.navigate("donationDetail/${donation.id}")
                            }
                        }
                    }
                }
                "reservedDonations" -> {
                    val reservedDonations = CommunityPostStore.donations.filter { CommunityPostStore.reservedDonationIds.contains(it.id) }
                    if (reservedDonations.isEmpty()) {
                        item { EmptyState("No reserved items yet.") }
                    } else {
                        items(reservedDonations) { donation ->
                            DonationItemCard(donation) {
                                navController.navigate("donationDetail/${donation.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = CommunityColors.TextMuted, fontSize = 14.sp)
    }
}