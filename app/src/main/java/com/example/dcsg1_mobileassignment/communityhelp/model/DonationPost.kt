package com.example.dcsg1_mobileassignment.communityhelp.model

// Data class for one donation post.
data class DonationPost(
    val id: String,
    val title: String,
    val category: String,
    val location: String,
    val description: String,
    val posted: String,
    val tint: Long = 0xFFEFF5EC,
    val imageRes: Int? = null,
    val quantity: Int = 1,
    val mine: Boolean = false,
    val state: String = ""
)