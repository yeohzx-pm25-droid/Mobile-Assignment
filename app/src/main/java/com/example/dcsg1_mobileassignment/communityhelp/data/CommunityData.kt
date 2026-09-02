package com.example.dcsg1_mobileassignment.communityhelp.data

import com.example.dcsg1_mobileassignment.R
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost

// Fixed lists and sample records for the community module.
object CommunityData {
    val jobCategories = listOf(
        "Part-time",
        "Full-time",
        "One-Time",
        "Contract",
        "Internship",
        "Freelance",
        "Volunteer",
        "Gig Work"
    )

    val donationCategories = listOf(
        "Food",
        "Toiletries",
        "Clothing",
        "Electronics",
        "Books",
        "Furniture",
        "Toys",
        "Others"
    )

    val paymentUnits = listOf("Day", "Hour", "Month", "Negotiable")

    val malaysiaStates = listOf(
        "Johor",
        "Kedah",
        "Kelantan",
        "Melaka",
        "Malacca",
        "Negeri Sembilan",
        "Pahang",
        "Penang",
        "Pulau Pinang",
        "Perak",
        "Perlis",
        "Sabah",
        "Sarawak",
        "Selangor",
        "Terengganu",
        "Kuala Lumpur",
        "Putrajaya",
        "Labuan"
    )

    val sampleJobs = listOf(
        JobPost(
            id = "job-1",
            title = "Restaurant Helper",
            category = "Part-time",
            location = "George Town, Penang",
            payment = "RM60 / day",
            description = "Help with table service and simple kitchen preparation.",
            posted = "Posted 2h ago"
        ),
        JobPost(
            id = "job-2",
            title = "Delivery Rider",
            category = "Full-time",
            location = "Bayan Lepas, Penang",
            payment = "RM80 / day",
            description = "Deliver small parcels around Bayan Lepas.",
            posted = "Posted 5h ago"
        ),
        JobPost(
            id = "job-3",
            title = "Tuition Teacher (Math)",
            category = "Part-time",
            location = "Gelugor, Penang",
            payment = "RM50 / hour",
            description = "Teach secondary school mathematics twice a week.",
            posted = "Posted 1Day ago"
        ),
        JobPost(
            id = "job-4",
            title = "House Cleaning Assistant",
            category = "One-Time",
            location = "Tanjung Tokong, Penang",
            payment = "RM70 / day",
            description = "One-off deep cleaning for a small apartment.",
            posted = "Posted 1Day ago"
        )
    )

    val sampleDonations = listOf(
        DonationPost(
            id = "donation-1",
            title = "Food Pack",
            category = "Food",
            location = "George Town, Penang",
            description = "Mixed basic food pack for a small family.",
            posted = "Posted 3h ago",
            imageRes = R.drawable.foodpack,
            tint = 0xFFFFF3C4
        ),
        DonationPost(
            id = "donation-2",
            title = "Shampoo and Body Wash",
            category = "Toiletries",
            location = "Bayan Lepas, Penang",
            description = "Two unopened bottles, suitable for daily use.",
            posted = "Posted 5h ago",
            imageRes= R.drawable.bodywashandhairshampoo,
            tint = 0xFFE1F2F1,
        )
    )
}
