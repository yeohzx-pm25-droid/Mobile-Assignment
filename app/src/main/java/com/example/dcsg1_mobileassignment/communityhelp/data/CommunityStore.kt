package com.example.dcsg1_mobileassignment.communityhelp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost

// Value used for "no filter selected" in every Job Connect filter field.
const val JOB_FILTER_ALL = "All"

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

    // ---- Job Connect: search + filter state (shared by JobListScreen and JobFilterScreen) ----
    var jobSearchQuery by mutableStateOf("")
    var jobTypeFilter by mutableStateOf(JOB_FILTER_ALL)
    var jobStateFilter by mutableStateOf(JOB_FILTER_ALL)
    var jobSalaryUnitFilter by mutableStateOf(JOB_FILTER_ALL)

    val activeJobFilterCount: Int
        get() = listOf(jobTypeFilter, jobStateFilter, jobSalaryUnitFilter)
            .count { it != JOB_FILTER_ALL }

    fun resetJobFilters() {
        jobTypeFilter = JOB_FILTER_ALL
        jobStateFilter = JOB_FILTER_ALL
        jobSalaryUnitFilter = JOB_FILTER_ALL
    }

    // Jobs after search text + all active filters are applied. Read this from
    // composables (not remember-ed) so it recomputes whenever the underlying
    // state changes.
    val filteredJobs: List<JobPost>
        get() = jobs.filter { job ->
            val matchesSearch = jobSearchQuery.isBlank() ||
                    job.title.contains(jobSearchQuery, ignoreCase = true) ||
                    job.location.contains(jobSearchQuery, ignoreCase = true) ||
                    job.category.contains(jobSearchQuery, ignoreCase = true)

            val matchesType = jobTypeFilter == JOB_FILTER_ALL || job.category == jobTypeFilter

            val matchesState = jobStateFilter == JOB_FILTER_ALL ||
                    job.location.contains(jobStateFilter, ignoreCase = true)

            val matchesSalary = jobSalaryUnitFilter == JOB_FILTER_ALL || (
                    if (jobSalaryUnitFilter == "Negotiable") {
                        job.payment.equals("Negotiable", ignoreCase = true)
                    } else {
                        job.payment.contains("/ ${jobSalaryUnitFilter.lowercase()}", ignoreCase = true)
                    }
                    )

            matchesSearch && matchesType && matchesState && matchesSalary
        }

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
