package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.model.BottomTab
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityScaffold
import com.example.dcsg1_mobileassignment.communityhelp.screens.navigateSingleTop
import com.example.dcsg1_mobileassignment.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun CommunityHomeScreenWithSupabase(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val name = authViewModel.currentUser
        ?.fullName
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "Student"
    val homeJobs = CommunityPostStore.homeJobs.take(2)
    val scope = rememberCoroutineScope()
    val isLoading = CommunityPostStore.isLoadingRemotePosts
    val loadError = CommunityPostStore.lastRemotePostError

    CommunityScaffold(navController, BottomTab.Home) { innerPadding ->
        androidx.compose.foundation.lazy.LazyColumn(
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
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Find help, work, and useful items nearby",
                    color = CommunityColors.TextMuted,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(20.dp))
                HomeHeroCard()
                Text(
                    text = "What do you want to do?",
                    color = CommunityColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    HomeActionTile(
                        title = "Job Connect",
                        subtitle = "Post and find paid work",
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigateSingleTop("jobs")
                    }
                    HomeActionTile(
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

            if (homeJobs.isEmpty()) {
                item {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = CommunityColors.Green)
                            }
                        }

                        loadError != null -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text(loadError, color = CommunityColors.TextMuted, fontSize = 12.sp)
                                TextButton(onClick = {
                                    scope.launch { CommunityPostStore.reloadFromSupabase() }
                                }) {
                                    Text("Retry", color = CommunityColors.Green, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        else -> {
                            Text(
                                text = "No jobs posted yet. Tap + to post one.",
                                color = CommunityColors.TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            items(homeJobs.size) { index ->
                val job = homeJobs[index]
                HomeJobCard(job) {
                    navController.navigateSingleTop("jobDetail/${job.id}")
                }
            }
        }
    }
}

@Composable
private fun HomeHeroCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 90.dp),
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
private fun HomeActionTile(
    title: String,
    subtitle: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .heightIn(min = 90.dp)
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, CommunityColors.CardBorder)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, color = CommunityColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = CommunityColors.TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun HomeJobCard(
    job: JobPost,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, CommunityColors.CardBorder)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 13.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                ) {
                    Text(
                        text = job.title,
                        color = CommunityColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (job.isUrgent) {
                        Spacer(Modifier.height(5.dp))
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Urgent",
                                color = Color(0xFFE65100),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CommunityColors.Green)
                ) {
                    Text(
                        text = job.payment,
                        color = CommunityColors.Green,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = job.location,
                color = CommunityColors.TextPrimary,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${job.category} - ${JobTimeFormatter.postedLine(job)}",
                color = CommunityColors.TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
