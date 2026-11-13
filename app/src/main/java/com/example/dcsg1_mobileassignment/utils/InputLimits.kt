package com.example.dcsg1_mobileassignment.utils


object InputLimits {
    const val NAME = 50
    const val EMAIL = 100
    const val PASSWORD = 64
    const val PHONE = 11
    const val AGE = 3
    const val SEARCH = 50

    const val JOB_TITLE = 60
    const val ITEM_NAME = 60
    const val LOCATION = 150
    const val DESCRIPTION = 500
    const val MESSAGE = 300

    const val PAYMENT_DIGITS = 6   // up to RM999,999
    const val QUANTITY_DIGITS = 4  // up to 9,999 items
}


fun String.limitTo(max: Int): String = if (length <= max) this else take(max)


fun String.digitsOnly(max: Int): String = filter { it.isDigit() }.take(max)
