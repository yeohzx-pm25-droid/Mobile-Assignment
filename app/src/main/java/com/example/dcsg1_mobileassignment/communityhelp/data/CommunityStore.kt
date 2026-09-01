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

    val appliedJobIds = mutableStateListOf<String>()
    val reservedDonationIds = mutableStateListOf<String>()

    fun addJob(job: JobPost) {
        jobs.add(0, job)
    }

    fun addDonation(donation: DonationPost) {
        donations.add(0, donation)
    }

    fun applyToJob(jobId: String) {
        if (!appliedJobIds.contains(jobId)) {
            appliedJobIds.add(jobId)
        }
    }

    fun reserveDonation(donationId: String) {
        if (!reservedDonationIds.contains(donationId)) {
            reservedDonationIds.add(donationId)
        }
    }

    fun deleteJob(jobId: String) {
        jobs.removeAll { it.id == jobId }
        appliedJobIds.remove(jobId)
    }

    fun deleteDonation(donationId: String) {
        donations.removeAll { it.id == donationId }
        reservedDonationIds.remove(donationId)
    }
}
