package com.example.dcsg1_mobileassignment.communityhelp.validation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

// Form validation used by Post Job and Post Donation.
object PostValidator {

    fun isValidPaymentAmount(payment: String): Boolean {
        return payment.isNotBlank() && payment.all { it.isDigit() }
    }

    fun isValidLocationWithState(location: String): Boolean {
        val typedLocation = location.cleanedLocationInput()
        val hasAddressParts = typedLocation.split(",")
            .map { it.trim() }
            .count { it.isNotBlank() } >= 2

        if (!hasAddressParts || typedLocation.length < 8) return false

        return findTypedMalaysiaState(typedLocation) != null
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

// Uses Android Geocoder to reject fake typed places before posting.
object LocationValidator {

    suspend fun isRealMalaysiaLocation(context: Context, location: String): Boolean {
        val typedLocation = location.cleanedLocationInput()
        val typedState = findTypedMalaysiaState(typedLocation) ?: return false

        if (!PostValidator.isValidLocationWithState(typedLocation)) return false
        if (!Geocoder.isPresent()) return false

        val addresses = findAddresses(context, typedLocation.withMalaysiaForMapSearch())
        return addresses.any { address ->
            address.matchesTypedMalaysiaAddress(
                typedLocation = typedLocation,
                typedState = typedState
            )
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun findAddresses(context: Context, query: String): List<Address> {
        return withContext(Dispatchers.IO) {
            runCatching {
                Geocoder(context, Locale.getDefault())
                    .getFromLocationName(query, 5)
                    .orEmpty()
            }.getOrDefault(emptyList())
        }
    }

    private fun Address.matchesTypedMalaysiaAddress(
        typedLocation: String,
        typedState: String
    ): Boolean {
        val searchableAddress = listOfNotNull(
            getAddressLine(0),
            featureName,
            thoroughfare,
            subThoroughfare,
            locality,
            subLocality,
            subAdminArea,
            adminArea,
            postalCode,
            countryCode,
            countryName
        ).joinToString(" ").normalizedForLocation()

        val countryMatches = countryCode.equals("MY", ignoreCase = true) ||
                searchableAddress.containsLocationPhrase("Malaysia")
        val stateMatches = aliasesForState(typedState).any { alias ->
            searchableAddress.containsLocationPhrase(alias)
        }

        val typedTokens = typedLocation.locationSearchTokens(typedState)
        val matchedTokenCount = typedTokens.count { token ->
            searchableAddress.containsLocationPhrase(token)
        }
        val hasSpecificAddressMatch = when {
            typedTokens.isEmpty() -> false
            typedTokens.size == 1 -> matchedTokenCount == 1
            else -> matchedTokenCount >= 2
        }

        return countryMatches && stateMatches && hasSpecificAddressMatch
    }
}

private fun findTypedMalaysiaState(location: String): String? {
    val searchableLocation = location.normalizedForLocation()
    return CommunityData.malaysiaStates.firstOrNull { state ->
        searchableLocation.containsLocationPhrase(state)
    }
}

private fun aliasesForState(state: String): List<String> {
    return when (state.lowercase(Locale.ROOT)) {
        "penang", "pulau pinang" -> listOf("Penang", "Pulau Pinang")
        "melaka", "malacca" -> listOf("Melaka", "Malacca")
        else -> listOf(state)
    }
}

private fun String.cleanedLocationInput(): String {
    return replace('，', ',')
        .replace(Regex(" +"), " ")
        .trim()
}

private fun String.withMalaysiaForMapSearch(): String {
    return if (normalizedForLocation().containsLocationPhrase("Malaysia")) {
        this
    } else {
        "$this, Malaysia"
    }
}

private fun String.locationSearchTokens(state: String): List<String> {
    val blockedTokens = (aliasesForState(state) + "Malaysia")
        .flatMap { it.normalizedForLocation().split(" ") }
        .toSet()

    return normalizedForLocation()
        .split(" ")
        .filter { token -> token.length >= 3 && token !in blockedTokens }
        .distinct()
}

private fun String.containsLocationPhrase(phrase: String): Boolean {
    val normalizedText = normalizedForLocation()
    val normalizedPhrase = phrase.normalizedForLocation()
    if (normalizedPhrase.isBlank()) return false

    return Regex("(^| )${Regex.escape(normalizedPhrase)}( |$)")
        .containsMatchIn(normalizedText)
}

private fun String.normalizedForLocation(): String {
    return lowercase(Locale.ROOT)
        .replace(Regex("[^a-z0-9 ]"), " ")
        .replace(Regex(" +"), " ")
        .trim()
}
