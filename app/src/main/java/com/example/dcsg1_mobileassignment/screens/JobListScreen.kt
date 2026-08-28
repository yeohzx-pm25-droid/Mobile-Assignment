package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityScaffold
import com.example.dcsg1_mobileassignment.communityhelp.screens.navigateSingleTop

@Composable
fun JobListScreen(navController: NavController) {
    CommunityScaffold(navController, BottomTab.Jobs) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CommunityColors.Surface)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            item {
                Text(
                    text = "Jobs",
                    color = CommunityColors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Find paid work and apply nearby",
                    color = CommunityColors.TextMuted,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(18.dp))
            }

            items(
                items = CommunityStore.jobs,
                key = { job -> job.id }
            ) { job ->
                JobListCard(job) {
                    navController.navigateSingleTop("jobDetail/${job.id}")
                }
            }
        }
    }
}

@Composable
private fun JobListCard(
    job: JobPost,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, CommunityColors.CardBorder)
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
                    border = BorderStroke(1.dp, CommunityColors.Green)
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
            Text(
                text = "${job.category} - ${JobTimeFormatter.postedLine(job)}",
                color = CommunityColors.TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
