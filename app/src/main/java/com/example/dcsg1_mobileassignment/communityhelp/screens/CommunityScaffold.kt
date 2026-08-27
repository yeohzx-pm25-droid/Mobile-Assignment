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
import androidx.compose.material3.Scaffold
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
                BottomText("Home", selected == BottomTab.Home) {
                    navController.navigateSingleTop("home")
                }
                BottomText("Jobs", selected == BottomTab.Jobs) {
                    navController.navigateSingleTop("jobs")
                }
                Spacer(Modifier.width(56.dp))
                BottomText("Donation", selected == BottomTab.Donation) {
                    navController.navigateSingleTop("donation")
                }
                BottomText("Profile", selected == BottomTab.Profile) {
                    navController.navigateSingleTop("userProfile")
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 10.dp)
                    .size(48.dp)
                    .background(CommunityColors.Green, CircleShape)
                    .clickable { navController.navigateSingleTop("createJob") },
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
private fun BottomText(
    label: String,
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
        Text(
            text = label,
            color = if (selected) CommunityColors.Green else CommunityColors.TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}
