package com.example.dcsg1_mobileassignment.communityhelp.model

// Data class for one job post.
data class JobPost(
    val id: String,
    val title: String,
    val category: String,
    val location: String,
    val payment: String,
    val description: String,
    val posted: String,
    val mine: Boolean = false
)
