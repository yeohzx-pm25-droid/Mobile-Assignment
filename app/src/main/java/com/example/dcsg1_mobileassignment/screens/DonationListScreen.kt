package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityData
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityStore
import com.example.dcsg1_mobileassignment.communityhelp.model.BottomTab
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityScaffold
import com.example.dcsg1_mobileassignment.communityhelp.screens.navigateSingleTop

@Composable
fun DonationListScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember { listOf("All") + CommunityData.donationCategories }

    val filtered = CommunityStore.donations.filter { donation ->
        val matchesCategory = selectedCategory == "All" || donation.category == selectedCategory
        val matchesQuery = query.isBlank() ||
                donation.title.contains(query, ignoreCase = true) ||
                donation.location.contains(query, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    CommunityScaffold(navController, BottomTab.Donation) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CommunityColors.Surface)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            item {
                Text(
                    text = "Community Share",
                    color = CommunityColors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Browse and reserve donated items nearby",
                    color = CommunityColors.TextMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search items...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = CommunityColors.TextMuted)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        CategoryChip(
                            label = category,
                            selected = category == selectedCategory
                        ) {
                            selectedCategory = category
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
            }

            if (filtered.isEmpty()) {
                item { EmptyDonationState() }
            } else {
                items(
                    items = filtered,
                    key = { it.id }
                ) { donation ->
                    DonationItemCard(donation) {
                        navController.navigateSingleTop("donationDetail/${donation.id}")
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (selected) CommunityColors.Green else Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (selected) CommunityColors.Green else CommunityColors.FieldBorder)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else CommunityColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun EmptyDonationState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No items found.", color = CommunityColors.TextMuted, fontSize = 13.sp)
    }
}

@Composable
fun DonationItemCard(
    donation: DonationPost,
    onClick: () -> Unit
) {
    val reserved = CommunityStore.reservedDonationIds.contains(donation.id)

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
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(donation.tint), RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = donation.title,
                    color = CommunityColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(donation.location, color = CommunityColors.TextPrimary, fontSize = 11.sp)
                Text(
                    text = "${donation.category} - ${donation.posted}",
                    color = CommunityColors.TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(Modifier.width(8.dp))

            StatusTag(reserved = reserved)
        }
    }
}

@Composable
private fun StatusTag(reserved: Boolean) {
    val color = if (reserved) CommunityColors.TextMuted else CommunityColors.Green
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = if (reserved) "Reserved" else "Free",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
        )
    }
}