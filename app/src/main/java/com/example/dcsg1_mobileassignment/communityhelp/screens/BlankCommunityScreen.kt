package com.example.dcsg1_mobileassignment.communityhelp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.model.BottomTab

@Composable
fun BlankCommunityScreen(
    navController: NavController,
    selected: BottomTab
) {
    CommunityScaffold(navController, selected) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CommunityColors.Surface)
                .padding(innerPadding)
        )
    }
}
