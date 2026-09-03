package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.model.BottomTab
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityScaffold
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val user = authViewModel.currentUser

    val jobApplicationsCount = CommunityPostStore.appliedJobIds.size
    val postedJobsCount = CommunityPostStore.jobs.count { it.mine }
    val donatedItemsCount = CommunityPostStore.donations.count { it.mine }
    val reservedItemsCount = CommunityPostStore.reservedDonationIds.size

    if (user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CommunityColors.Surface),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No user is logged in.", color = CommunityColors.TextPrimary)
                Spacer(Modifier.height(20.dp))
                Button(onClick = { navController.navigate("login") }) {
                    Text("Go to Login")
                }
            }
        }
        return
    }

    CommunityScaffold(navController, BottomTab.Profile) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CommunityColors.Surface)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(CommunityColors.Green)
                    .padding(horizontal = 30.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5EFE6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color(0xFF9BA99C),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(Modifier.size(18.dp))

                    Column {
                        Text(
                            text = user.fullName.ifBlank { "Student" },
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = user.email,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(26.dp)
                        .clickable { navController.navigate("editProfile") }
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 190.dp),
                color = CommunityColors.Surface,
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 34.dp)
                ) {
                    Text(
                        text = "My Activity",
                        color = CommunityColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(18.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CommunityColors.FieldBorder)
                    ) {
                        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            ActivityRow("My Job Applications", jobApplicationsCount.toString(), Icons.Filled.Work) {
                                navController.navigate("activityList/appliedJobs")
                            }
                            HorizontalDivider(color = CommunityColors.FieldBorder)
                            ActivityRow("My Posted Jobs", postedJobsCount.toString(), Icons.Filled.Campaign) {
                                navController.navigate("activityList/myJobs")
                            }
                            HorizontalDivider(color = CommunityColors.FieldBorder)
                            ActivityRow("My Donated Items", donatedItemsCount.toString(), Icons.Filled.VolunteerActivism) {
                                navController.navigate("activityList/myDonations")
                            }
                            HorizontalDivider(color = CommunityColors.FieldBorder)
                            ActivityRow("My Reserved Items", reservedItemsCount.toString(), Icons.Filled.Bookmark) {
                                navController.navigate("activityList/reservedDonations")
                            }
                        }
                    }

                    Spacer(Modifier.height(36.dp))

                    TextButton(
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Logout",
                            color = Color(0xFFE53935),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CommunityColors.Green, modifier = Modifier.size(21.dp))
        Spacer(Modifier.size(14.dp))
        Text(
            text = label,
            color = CommunityColors.TextPrimary,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(value, color = CommunityColors.TextMuted, fontSize = 14.sp)
        Spacer(Modifier.size(10.dp))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = CommunityColors.TextMuted)
    }
}
