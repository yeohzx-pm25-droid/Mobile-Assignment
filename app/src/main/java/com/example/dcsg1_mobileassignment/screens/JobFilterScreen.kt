package com.example.dcsg1_mobileassignment.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedButton
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
import com.example.dcsg1_mobileassignment.communityhelp.data.JOB_FILTER_ALL
import com.example.dcsg1_mobileassignment.communityhelp.screens.CommunityColors
import com.example.dcsg1_mobileassignment.communityhelp.screens.navigateSingleTop

@Composable
fun JobFilterScreen(navController: NavController) {
    // Local draft state so changes only apply once "Apply Filter" is tapped.
    var jobType by remember { mutableStateOf(CommunityPostStore.jobTypeFilter) }
    var state by remember { mutableStateOf(CommunityPostStore.jobStateFilter) }
    var salaryUnit by remember { mutableStateOf(CommunityPostStore.jobSalaryUnitFilter) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CommunityColors.Surface)
    ) {
        FilterTopBar(
            onBack = { navController.popBackStack() },
            onReset = {
                jobType = JOB_FILTER_ALL
                state = JOB_FILTER_ALL
                salaryUnit = JOB_FILTER_ALL
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Narrow jobs based on your location, type and pay.",
                color = CommunityColors.TextMuted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(22.dp))

            FilterSection(title = "Job Type") {
                ChipGroup(
                    options = listOf(JOB_FILTER_ALL) + CommunityData.jobCategories,
                    selected = jobType,
                    onSelected = { jobType = it }
                )
            }

            Spacer(Modifier.height(22.dp))

            FilterSection(title = "Location") {
                DropdownField(
                    label = "State",
                    value = state,
                    options = listOf(JOB_FILTER_ALL) + CommunityData.malaysiaStates,
                    onSelected = { state = it }
                )
            }

            Spacer(Modifier.height(22.dp))

            FilterSection(title = "Salary") {
                DropdownField(
                    label = "Payment Unit",
                    value = salaryUnit,
                    options = listOf(JOB_FILTER_ALL) + CommunityData.paymentUnits,
                    onSelected = { salaryUnit = it }
                )
            }

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = {
                    CommunityPostStore.jobTypeFilter = jobType
                    CommunityPostStore.jobStateFilter = state
                    CommunityPostStore.jobSalaryUnitFilter = salaryUnit
                    navController.navigateSingleTop("jobs")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CommunityColors.Green)
            ) {
                Text("Apply Filter", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FilterTopBar(onBack: () -> Unit, onReset: () -> Unit) {
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
                .width(46.dp)
                .height(46.dp)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Text("<", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Filter Jobs",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Reset",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onReset() }
                .padding(horizontal = 6.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, color = CommunityColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun ChipGroup(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) CommunityColors.Green else Color.White,
                border = BorderStroke(1.dp, if (isSelected) CommunityColors.Green else CommunityColors.FieldBorder),
                modifier = Modifier.clickable { onSelected(option) }
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else CommunityColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (value == JOB_FILTER_ALL) "All ${label}s" else value,
                    color = CommunityColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text("v", color = CommunityColors.TextMuted, fontWeight = FontWeight.Bold)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(max = 260.dp)
        ) {
            options.forEach { option ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable {
                            onSelected(option)
                            expanded = false
                        },
                    color = if (option == value) CommunityColors.Green else Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (option == value) CommunityColors.Green else CommunityColors.FieldBorder)
                ) {
                    Text(
                        text = if (option == JOB_FILTER_ALL) "All ${label}s" else option,
                        color = if (option == value) Color.White else CommunityColors.TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (option == value) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
