package com.example.dcsg1_mobileassignment.communityhelp.data

import androidx.compose.runtime.mutableStateListOf
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost

// In-memory post storage while the app is open.
object CommunityStore {
    val jobs = mutableStateListOf<JobPost>().apply {
        addAll(CommunityData.sampleJobs)
    }

    val donations = mutableStateListOf<DonationPost>().apply {
        addAll(CommunityData.sampleDonations)
    }

    fun addJob(job: JobPost) {
        jobs.add(0, job)
    }

    fun addDonation(donation: DonationPost) {
        donations.add(0, donation)
    }
}
