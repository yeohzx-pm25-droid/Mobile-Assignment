package com.example.dcsg1_mobileassignment.communityhelp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityStore
import com.example.dcsg1_mobileassignment.communityhelp.model.BottomTab
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel

@Composable
fun CommunityHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val name = authViewModel.currentUser
        ?.fullName
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "Student"

    CommunityScaffold(navController, BottomTab.Home) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CommunityColors.Surface)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            item {
                Text(
                    text = "Hi, $name",
                    color = CommunityColors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Find help, work, and useful items nearby",
                    color = CommunityColors.TextMuted,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(20.dp))
                HeroCard()
                Text(
                    text = "What do you want to do?",
                    color = CommunityColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ActionTile(
                        title = "Job Connect",
                        subtitle = "Post and find paid work",
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigateSingleTop("jobs")
                    }
                    ActionTile(
                        title = "Community Share",
                        subtitle = "Donate useful items",
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigateSingleTop("donation")
                    }
                }
                Text(
                    text = "Recommended Jobs",
                    color = CommunityColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 22.dp, bottom = 14.dp)
                )
            }

            items(CommunityStore.jobs.take(2).size) { index ->
                val job = CommunityStore.jobs[index]
                JobCard(job) {
                    navController.navigate("jobDetail/${job.id}")
                }
            }
        }
    }
}

@Composable
private fun HeroCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp),
        color = CommunityColors.Green,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "Share opportunities and donations with your community.",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(18.dp)
        )
    }
    Spacer(Modifier.height(18.dp))
}

@Composable
private fun ActionTile(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CommunityColors.CardBorder)
    ) {
        Column(Modifier.padding(15.dp)) {
            Text(title, color = CommunityColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(subtitle, color = CommunityColors.TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
fun JobCard(job: JobPost, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CommunityColors.CardBorder)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 13.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = job.title,
                    color = CommunityColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CommunityColors.Green)
                ) {
                    Text(
                        text = job.payment,
                        color = CommunityColors.Green,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(job.location, color = CommunityColors.TextPrimary, fontSize = 11.sp)
            Text("${job.category} - ${job.posted}", color = CommunityColors.TextMuted, fontSize = 10.sp)
        }
    }
}
