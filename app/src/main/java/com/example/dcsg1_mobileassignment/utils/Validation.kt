package com.example.dcsg1_mobileassignment.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.widget.Toast

object Validation {

    fun isEmailValid(email: String): Boolean {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches()

    }


    fun isPasswordValid(password: String): Boolean {

        if (password.length < 8) return false
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasUpperCase && hasDigit && hasSpecialChar

    }


    fun isPhoneValid(phone: String): Boolean {

        if (!phone.all { it.isDigit() }) return false

        return if (phone.startsWith("011")) {
            phone.length == 11
        } else {
            phone.startsWith("01") && phone.length == 10
        }

    }


    fun isNameValid(name: String): Boolean {

        return name.trim().isNotEmpty()

    }


    fun isAgeValid(age: String): Boolean {

        val parsed = age.trim().toIntOrNull() ?: return false
        return parsed in 18..100

    }


    fun isNotEmpty(text: String): Boolean {

        return text.trim().isNotEmpty()

    }
}

fun Context.openLocationInMaps(location: String) {
    val cleanedLocation = location.replace('，', ',').trim()
    val mapQuery = if (cleanedLocation.contains("Malaysia", ignoreCase = true)) {
        cleanedLocation
    } else {
        "$cleanedLocation, Malaysia"
    }
    val query = Uri.encode(mapQuery)

    val mapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query")).apply {
        setPackage("com.google.android.apps.maps")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        startActivity(mapsIntent)
        return
    } catch (_: ActivityNotFoundException) {
    } catch (_: SecurityException) {
    }

    val webIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://www.google.com/maps/search/?api=1&query=$query")
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        startActivity(webIntent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, "Please install Google Maps or a browser to open this location.", Toast.LENGTH_SHORT).show()
    } catch (_: SecurityException) {
        Toast.makeText(this, "Unable to open map on this device.", Toast.LENGTH_SHORT).show()
    }
}
