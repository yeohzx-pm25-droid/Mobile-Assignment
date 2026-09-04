package com.example.dcsg1_mobileassignment.communityhelp.data

import com.example.dcsg1_mobileassignment.R
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost

const val JOB_FILTER_ALL = "All"

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
            posted = "Posted 1d ago"
        ),
        JobPost(
            id = "job-4",
            title = "House Cleaning Assistant",
            category = "One-Time",
            location = "Tanjung Tokong, Penang",
            payment = "RM70 / day",
            description = "One-off deep cleaning for a small apartment.",
            posted = "Posted 1d ago"
        )
    )

    val sampleDonations = listOf(
        DonationPost(
            id = "donation-1",
            title = "Food Pack",
            category = "Food",
            location = "12, Lebuh Chulia, George Town, Penang",
            description = "Mixed basic food pack for a small family.",
            posted = "Posted 3h ago",
            imageRes = R.drawable.food_pack,
            quantity = 10,
            tint = 0xFFFFF3C4
        ),
        DonationPost(
            id = "donation-2",
            title = "Shampoo and Body Wash",
            category = "Toiletries",
            location = "45, Jalan Bayan Lepas, Bayan Lepas, Penang",
            description = "Two unopened bottles, suitable for daily use.",
            posted = "Posted 5h ago",
            imageRes = R.drawable.bodywash_and_hairshampoo,
            quantity = 2,
            tint = 0xFFE1F2F1
        ),
        DonationPost(
            id = "donation-3",
            title = "Canned Food Bundle",
            category = "Food",
            location = "8, Jalan Ampang, Kuala Lumpur",
            description = "Assorted canned beans and tuna, still within the expiry date. 4 items in one bundle.",
            posted = "Posted 1h ago",
            imageRes = R.drawable.canned_food_bundle,
            quantity = 3,
            tint = 0xFFFFF3C4
        ),
        DonationPost(
            id = "donation-4",
            title = "Children's Clothes (3-6 years)",
            category = "Clothing",
            location = "22, Jalan SS2/24, Petaling Jaya, Selangor",
            description = "Gently used T-shirts and shorts, good condition, washed and folded.",
            posted = "Posted 4h ago",
            imageRes = R.drawable.children_clothes,
            quantity = 6,
            tint = 0xFFE6EEFF
        ),
        DonationPost(
            id = "donation-5",
            title = "Used 65W USB-C Laptop Charger",
            category = "Electronics",
            location = "15, Jalan Wong Ah Fook, Johor Bahru, Johor",
            description = "65W USB-C charger, tested and working, no original box.",
            posted = "Posted 6h ago",
            imageRes = R.drawable.laptop_charger,
            quantity = 1,
            tint = 0xFFEFE3FF
        ),
        DonationPost(
            id = "donation-6",
            title = "Secondary School Textbooks",
            category = "Books",
            location = "3, Jalan Hang Tuah, Melaka",
            description = "Form 3 science and math textbooks, some pencil marks inside.",
            posted = "Posted 8h ago",
            imageRes = R.drawable.form3_textbook,
            quantity = 8,
            tint = 0xFFFFE8D6,
        ),
        DonationPost(
            id = "donation-7",
            title = "Study Table and Chair Set",
            category = "Furniture",
            location = "10, Jalan Pekan Baru, Alor Setar, Kedah",
            description = "Small wooden study table with a matching chair, minor scratches.",
            posted = "Posted 12h ago",
            imageRes = R.drawable.studytable_and_chair,
            quantity = 1,
            tint = 0xFFE3ECD8
        ),
        DonationPost(
            id = "donation-8",
            title = "Toy Building Blocks Set",
            category = "Toys",
            location = "5, Jalan Sultan Idris Shah, Ipoh, Perak",
            description = "Large box of compatible building blocks, all pieces included.",
            posted = "Posted 1d ago",
            imageRes = R.drawable.toy_building_block,
            quantity = 1,
            tint = 0xFFFFE1EC
        ),
        DonationPost(
            id = "donation-9",
            title = "Toothpaste and Toothbrush Set",
            category = "Toiletries",
            location = "18, Jalan Tunku Munawir, Seremban, Negeri Sembilan",
            description = "Unopened travel packs, suitable for daily use.",
            posted = "Posted 1d ago",
            imageRes = R.drawable.toothpaste_and_toothbrush,
            quantity = 6,
            tint = 0xFFE1F2F1
        ),
        DonationPost(
            id = "donation-10",
            title = "Used Lunch Boxes",
            category = "Others",
            location = "7, Jalan Satok, Kuching, Sarawak",
            description = "Pre-owned lunch boxes in different colours and sizes. Clean and still suitable for everyday use.",
            posted = "Posted 2d ago",
            imageRes = R.drawable.lunch_box,
            quantity = 10,
            tint = 0xFFEFF5EC
        )
    )
}