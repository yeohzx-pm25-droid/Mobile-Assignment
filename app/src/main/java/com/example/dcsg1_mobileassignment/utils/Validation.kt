package com.example.dcsg1_mobileassignment.utils

import android.util.Patterns

object Validation {

    fun isEmailValid(email: String): Boolean {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches()

    }


    fun isPasswordValid(password: String): Boolean {

        if (password.length < 8) return false
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { "@#$%".contains(it) }

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


    fun isNotEmpty(text: String): Boolean {

        return text.trim().isNotEmpty()

    }

}