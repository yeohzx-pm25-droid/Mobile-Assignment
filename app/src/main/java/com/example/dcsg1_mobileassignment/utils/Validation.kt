package com.example.dcsg1_mobileassignment.utils

import android.util.Patterns

object Validation {

    fun isEmailValid(email: String): Boolean {

        return Patterns.EMAIL_ADDRESS
            .matcher(email)
            .matches()

    }


    fun isPasswordValid(password: String): Boolean {

        return password.length >= 6

    }


    fun isPhoneValid(phone: String): Boolean {

        return phone.length >= 10 &&
                phone.all { it.isDigit() }

    }


    fun isNameValid(name: String): Boolean {

        return name.trim().isNotEmpty()

    }


    fun isNotEmpty(text: String): Boolean {

        return text.trim().isNotEmpty()

    }

}