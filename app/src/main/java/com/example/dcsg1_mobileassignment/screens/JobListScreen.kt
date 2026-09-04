package com.example.dcsg1_mobileassignment.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.data.JOB_FILTER_ALL
import com.example.dcsg1_mobileassignment.communityhelp.model.BottomTab
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityScaffold
import com.example.dcsg1_mobileassignment.communityhelp.screens.navigateSingleTop

// Quick-access chips shown above the job list, mirroring the mockup.
private val quickJobTypes = listOf(JOB_FILTER_ALL, "Part-time", "Full-time", "One-Time")

@Composable
fun JobListScreen(navController: NavController) {
    CommunityScaffold(navController, BottomTab.Jobs) { innerPadding ->
        val jobs = CommunityPostStore.filteredJobs

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
                Spacer(Modifier.height(16.dp))

                JobSearchBar(
                    query = CommunityPostStore.jobSearchQuery,
                    onQueryChange = { CommunityPostStore.jobSearchQuery = it },
                    activeFilterCount = CommunityPostStore.activeJobFilterCount,
                    onFilterClick = { navController.navigateSingleTop("jobFilter") }
                )

                Spacer(Modifier.height(14.dp))

                JobTypeChipsRow(
                    selected = CommunityPostStore.jobTypeFilter,
                    onSelected = { CommunityPostStore.jobTypeFilter = it }
                )

                Spacer(Modifier.height(18.dp))
            }

            if (jobs.isEmpty()) {
                item { EmptyJobsMessage() }
            }

            items(
                items = jobs,
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
private fun JobSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    activeFilterCount: Int,
    onFilterClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search jobs...", fontSize = 13.sp) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = CommunityColors.TextMuted)
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = CommunityColors.Green,
                unfocusedBorderColor = CommunityColors.FieldBorder
            ),
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
        )

        Spacer(Modifier.width(10.dp))

        Box {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CommunityColors.FieldBorder),
                modifier = Modifier
                    .size(52.dp)
                    .clickable { onFilterClick() }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Filled.FilterList, contentDescription = "Filter jobs", tint = CommunityColors.TextPrimary)
                }
            }

            if (activeFilterCount > 0) {
                Surface(
                    shape = CircleShape,
                    color = CommunityColors.Green,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = activeFilterCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JobTypeChipsRow(
    selected: String,
    onSelected: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(quickJobTypes) { type ->
            val isSelected = selected == type
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) CommunityColors.Green else Color.White,
                border = BorderStroke(1.dp, if (isSelected) CommunityColors.Green else CommunityColors.FieldBorder),
                modifier = Modifier.clickable { onSelected(type) }
            ) {
                Text(
                    text = type,
                    color = if (isSelected) Color.White else CommunityColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyJobsMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No jobs match your search or filters.",
            color = CommunityColors.TextMuted,
            fontSize = 13.sp
        )
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
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = job.title,
                        color = CommunityColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (CommunityPostStore.appliedJobIds.contains(job.id)) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            color = CommunityColors.SoftSurface,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Applied",
                                color = CommunityColors.TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = job.location,
                color = CommunityColors.TextPrimary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
            Text(
                text = "${job.category} - ${JobTimeFormatter.postedLine(job)}",
                color = CommunityColors.TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
