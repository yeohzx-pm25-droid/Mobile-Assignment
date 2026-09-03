package com.example.dcsg1_mobileassignment.communityhelp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.model.BottomTab

object CommunityColors {
    val Green = Color(0xFF1B8E3C)
    val GreenDark = Color(0xFF136C2C)
    val Surface = Color(0xFFFCFDF8)
    val SoftSurface = Color(0xFFF6F8F3)
    val FieldBorder = Color(0xFFDFE5DC)
    val CardBorder = Color(0xFFEEF0EA)
    val TextPrimary = Color(0xFF172018)
    val TextMuted = Color(0xFF687267)
}

@Composable
fun CommunityScaffold(
    navController: NavController,
    selected: BottomTab,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = CommunityColors.Surface,
        bottomBar = {
            CommunityBottomBar(navController, selected)
        },
        content = content
    )
}

@Composable
private fun CommunityBottomBar(
    navController: NavController,
    selected: BottomTab
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomItem("Home", Icons.Filled.Home, selected == BottomTab.Home) {
                    navController.navigateSingleTop("home")
                }
                BottomItem("Jobs", Icons.Filled.Work, selected == BottomTab.Jobs) {
                    navController.navigateSingleTop("jobs")
                }
                Spacer(Modifier.width(56.dp))
                BottomItem("Donation", Icons.Filled.VolunteerActivism, selected == BottomTab.Donation) {
                    navController.navigateSingleTop("donation")
                }
                BottomItem("Profile", Icons.Filled.AccountCircle, selected == BottomTab.Profile) {
                    navController.navigateSingleTop("userProfile")
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 10.dp)
                    .size(48.dp)
                    .background(CommunityColors.Green, CircleShape)
                    .clickable {
                        val target = if (selected == BottomTab.Donation) "createDonation" else "createJob"
                        navController.navigateSingleTop(target)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 7.dp)
                    .size(width = 108.dp, height = 4.dp)
                    .background(Color(0x47111611), CircleShape)
            )
        }
    }
}

@Composable
private fun BottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(72.dp)
            .width(68.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) CommunityColors.Green else CommunityColors.TextMuted,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                color = if (selected) CommunityColors.Green else CommunityColors.TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}