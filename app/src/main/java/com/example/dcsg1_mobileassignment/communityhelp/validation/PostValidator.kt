package com.example.dcsg1_mobileassignment.communityhelp.validation

import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityData

// Form validation used by Post Job and Post Donation.
object PostValidator {

    fun isValidPaymentAmount(payment: String): Boolean {
        return payment.isNotBlank() && payment.all { it.isDigit() }
    }

    fun isValidLocationWithState(location: String): Boolean {
        val parts = location.split(",").map { it.trim() }.filter { it.isNotBlank() }
        if (parts.size < 2) return false

        val typedStateParts = parts.drop(1).map { it.lowercase() }
        val states = CommunityData.malaysiaStates.map { it.lowercase() }
        return typedStateParts.any { typed -> states.any { state -> typed == state } }
    }

    fun buildPayment(raw: String, unit: String): String {
        val payment = raw.trim()
        return when {
            unit == "Negotiable" -> "Negotiable"
            payment.isBlank() -> "Negotiable"
            else -> "RM$payment / ${unit.lowercase()}"
        }
    }

    fun tintForCategory(category: String): Long = when (category) {
        "Food" -> 0xFFFFF3C4
        "Toiletries" -> 0xFFE1F2F1
        "Clothing" -> 0xFFE6EEFF
        else -> 0xFFEFF5EC
    }
}
