package com.example.dcsg1_mobileassignment.communityhelp.data

const val JOB_FILTER_ALL = "All"

// Fixed lists used by the community module.
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

    // States shown in the filter dropdowns. "Malacca" and "Pulau Pinang" are left out here
    // because they are the same states as "Melaka" and "Penang" (they are still matched below).
    val filterStates = listOf(
        "Johor",
        "Kedah",
        "Kelantan",
        "Melaka",
        "Negeri Sembilan",
        "Pahang",
        "Penang",
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

    // Other names people type for the same state.
    fun stateAliases(state: String): List<String> {
        return when (state.lowercase()) {
            "penang", "pulau pinang" -> listOf("Penang", "Pulau Pinang")
            "melaka", "malacca" -> listOf("Melaka", "Malacca")
            "kuala lumpur" -> listOf("Kuala Lumpur", "KL", "Wilayah Persekutuan Kuala Lumpur")
            else -> listOf(state)
        }
    }

    // True when the typed address mentions the state (or one of its other names).
    fun locationMatchesState(location: String, state: String): Boolean {
        return stateAliases(state).any { alias ->
            Regex("(^|[^A-Za-z])${Regex.escape(alias)}([^A-Za-z]|$)", RegexOption.IGNORE_CASE)
                .containsMatchIn(location)
        }
    }
}
