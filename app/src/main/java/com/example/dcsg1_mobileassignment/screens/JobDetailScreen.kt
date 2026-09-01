package com.example.dcsg1_mobileassignment.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityStore
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors

@Composable
fun JobDetailScreen(
    navController: NavController,
    jobId: String
) {
    val job = CommunityStore.jobs.firstOrNull { it.id == jobId }

    if (job == null) {
        MissingJobScreen(navController)
        return
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
    ) {
        JobDetailTopBar(
            onBack = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            JobHeader(job)

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
                text = job.description,
                color = CommunityColors.TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = 34.dp, bottom = 18.dp),
                color = CommunityColors.FieldBorder
            )

            InfoRow(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = CommunityColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = job.location,
                underline = true
            )

            Spacer(Modifier.height(18.dp))

            InfoRow(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = CommunityColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = JobTimeFormatter.postedLine(job),
                underline = false
            )

            Spacer(Modifier.weight(1f))

            if (job.mine) {
                Button(
                    onClick = {
                        CommunityStore.deleteJob(job.id)
                        Toast.makeText(context, "Job deleted successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
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
            } else {
                Button(
                    onClick = {
                        CommunityStore.applyToJob(job.id)
                        Toast.makeText(context, "Applied successfully", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green)
                ) {
                    Text(
                        text = "Apply Now",
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
private fun JobDetailTopBar(
    onBack: () -> Unit
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
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Text("<", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Job Details",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun JobHeader(job: JobPost) {
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(
                text = job.title,
                color = CommunityColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = job.category,
                color = CommunityColors.TextPrimary,
                fontSize = 12.sp
            )
        }

        Text(
            text = job.payment,
            color = CommunityColors.Green,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InfoRow(
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
private fun MissingJobScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Text("Job not found.", color = CommunityColors.TextPrimary, fontSize = 16.sp)
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
