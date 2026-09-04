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
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityData
import com.example.dcsg1_mobileassignment.communityhelp.data.JOB_FILTER_ALL
import com.example.dcsg1_mobileassignment.communityhelp.model.BottomTab
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityScaffold
import com.example.dcsg1_mobileassignment.communityhelp.screens.navigateSingleTop

private val quickDonationCategories = listOf(JOB_FILTER_ALL) + CommunityData.donationCategories

@Composable
fun DonationListScreen(navController: NavController) {
    CommunityScaffold(navController, BottomTab.Donation) { innerPadding ->
        val donations = CommunityPostStore.filteredDonations

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

                DonationSearchBar(
                    query = CommunityPostStore.donationSearchQuery,
                    onQueryChange = { CommunityPostStore.donationSearchQuery = it },
                    activeFilterCount = CommunityPostStore.activeDonationFilterCount,
                    onFilterClick = { navController.navigateSingleTop("donationFilter") }
                )

                Spacer(Modifier.height(14.dp))

                DonationCategoryChipsRow(
                    selected = CommunityPostStore.donationCategoryFilter,
                    onSelected = { CommunityPostStore.donationCategoryFilter = it }
                )

                Spacer(Modifier.height(18.dp))
            }

            if (donations.isEmpty()) {
                item { EmptyDonationState() }
            } else {
                items(
                    items = donations,
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
private fun DonationSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    activeFilterCount: Int,
    onFilterClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search items...", fontSize = 13.sp) },
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
                    Icon(Icons.Filled.FilterList, contentDescription = "Filter donations", tint = CommunityColors.TextPrimary)
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
private fun DonationCategoryChipsRow(
    selected: String,
    onSelected: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(quickDonationCategories) { category ->
            val isSelected = selected == category
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) CommunityColors.Green else Color.White,
                border = BorderStroke(1.dp, if (isSelected) CommunityColors.Green else CommunityColors.FieldBorder),
                modifier = Modifier.clickable { onSelected(category) }
            ) {
                Text(
                    text = category,
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
private fun EmptyDonationState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No items match your search or filters.", color = CommunityColors.TextMuted, fontSize = 13.sp)
    }
}

@Composable
fun DonationItemCard(
    donation: DonationPost,
    onClick: () -> Unit
) {
    val fullyReserved = CommunityPostStore.isFullyReserved(donation)
    val remaining = CommunityPostStore.remainingQuantity(donation)

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
            RemoteDonationImage(
                imageUrl = CommunityPostStore.imageUrlForDonation(donation.id),
                fallbackTint = donation.tint,
                imageRes = donation.imageRes,
                modifier = Modifier.size(56.dp),
                cornerRadius = 10.dp
            )

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = donation.title,
                    color = CommunityColors.TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = donation.location,
                    color = CommunityColors.TextPrimary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
                Text(
                    text = "${donation.category} - ${donation.posted}",
                    color = CommunityColors.TextMuted,
                    fontSize = 10.sp
                )
                if (donation.mine) {
                    val totalReserved = CommunityPostStore.totalReservedQuantityFor(donation.id)
                    if (totalReserved > 0) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "$totalReserved reserved",
                            color = CommunityColors.Green,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            StatusTag(fullyReserved = fullyReserved, remaining = remaining, totalQuantity = donation.quantity)
        }
    }
}

@Composable
private fun StatusTag(fullyReserved: Boolean, remaining: Int, totalQuantity: Int) {
    val color = if (fullyReserved) CommunityColors.TextMuted else CommunityColors.Green
    val label = when {
        fullyReserved -> "Reserved"
        totalQuantity <= 1 -> "Free"
        else -> "$remaining left"
    }
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, color),
        modifier = Modifier.width(57.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 3.dp)) {
            Text(
                text = label,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}   