package com.example.dcsg1_mobileassignment.screens

import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost

object JobTimeFormatter {
    fun postedLine(job: JobPost): String {
        val createdAtMillis = CommunityPostStore.createdAtMillisForJob(job.id) ?: job.id.toLongOrNull()

        if (createdAtMillis == null) {
            return when {
                job.posted.equals("Posted just now", ignoreCase = true) -> "Just Posted"
                job.posted.isNotBlank() -> job.posted
                else -> "Just Posted"
            }
        }

        val age = ageText(createdAtMillis)
        return if (age == "Just Posted") age else "Posted $age ago"
    }

    private fun ageText(createdAtMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
        val ageMillis = (nowMillis - createdAtMillis).coerceAtLeast(0L)
        val minute = 60_000L
        val hour = 60 * minute
        val day = 24 * hour

        return when {
            ageMillis < 30 * minute -> "Just Posted"
            ageMillis < hour -> "${ageMillis / minute}m"
            ageMillis < day -> "${ageMillis / hour}h"
            else -> {
                val days = ageMillis / day
                if (days == 1L) "1Day" else "${days}Days"
            }
        }
    }
}
