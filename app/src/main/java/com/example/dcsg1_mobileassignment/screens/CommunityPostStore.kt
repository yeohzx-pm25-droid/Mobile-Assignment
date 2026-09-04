package com.example.dcsg1_mobileassignment.screens

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.dcsg1_mobileassignment.communityhelp.data.CommunityData
import com.example.dcsg1_mobileassignment.communityhelp.data.JOB_FILTER_ALL
import com.example.dcsg1_mobileassignment.communityhelp.model.DonationPost
import com.example.dcsg1_mobileassignment.communityhelp.model.JobPost
import com.example.dcsg1_mobileassignment.communityhelp.validation.PostValidator
import com.example.dcsg1_mobileassignment.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.Locale

object CommunityPostStore {
    val jobs = mutableStateListOf<JobPost>().apply {
        addAll(CommunityData.sampleJobs)
    }

    val donations = mutableStateListOf<DonationPost>().apply {
        addAll(CommunityData.sampleDonations)
    }

    val appliedJobIds = mutableStateListOf<String>()
    val appliedJobStatuses = mutableStateMapOf<String, String>()
    val reservedDonationIds = mutableStateListOf<String>()
    val myReservationStatuses = mutableStateMapOf<String, String>()

    private val reservedQuantities = mutableStateMapOf<String, Int>()
    private val myReservedQuantities = mutableStateMapOf<String, Int>()

    private val jobCreatedAtMillis = mutableStateMapOf<String, Long>()
    private val donationCreatedAtMillis = mutableStateMapOf<String, Long>()
    private val donationImageUrls = mutableStateMapOf<String, String>()

    var isLoadingRemotePosts by mutableStateOf(false)
        private set

    var lastRemotePostError by mutableStateOf<String?>(null)
        private set

    var jobSearchQuery by mutableStateOf("")
    var jobTypeFilter by mutableStateOf(JOB_FILTER_ALL)
    var jobStateFilter by mutableStateOf(JOB_FILTER_ALL)
    var jobSalaryUnitFilter by mutableStateOf(JOB_FILTER_ALL)

    val activeJobFilterCount: Int
        get() = listOf(jobTypeFilter, jobStateFilter, jobSalaryUnitFilter)
            .count { it != JOB_FILTER_ALL }

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

    val homeJobs: List<JobPost>
        get() = jobs.sortedWith(
            compareByDescending<JobPost> { it.isUrgent }
                .thenByDescending { createdAtMillisForJob(it.id) ?: it.id.toLongOrNull() ?: 0L }
        )

    fun resetJobFilters() {
        jobTypeFilter = JOB_FILTER_ALL
        jobStateFilter = JOB_FILTER_ALL
        jobSalaryUnitFilter = JOB_FILTER_ALL
    }

    var donationSearchQuery by mutableStateOf("")
    var donationCategoryFilter by mutableStateOf(JOB_FILTER_ALL)
    var donationStateFilter by mutableStateOf(JOB_FILTER_ALL)

    val activeDonationFilterCount: Int
        get() = listOf(donationCategoryFilter, donationStateFilter)
            .count { it != JOB_FILTER_ALL }

    val filteredDonations: List<DonationPost>
        get() = donations.filter { donation ->
            val matchesSearch = donationSearchQuery.isBlank() ||
                    donation.title.contains(donationSearchQuery, ignoreCase = true) ||
                    donation.location.contains(donationSearchQuery, ignoreCase = true) ||
                    donation.category.contains(donationSearchQuery, ignoreCase = true)

            val matchesCategory = donationCategoryFilter == JOB_FILTER_ALL ||
                    donation.category == donationCategoryFilter

            val matchesState = donationStateFilter == JOB_FILTER_ALL ||
                    donation.location.contains(donationStateFilter, ignoreCase = true)

            matchesSearch && matchesCategory && matchesState
        }

    fun resetDonationFilters() {
        donationCategoryFilter = JOB_FILTER_ALL
        donationStateFilter = JOB_FILTER_ALL
    }

    fun resetLocalPosts() {
        jobs.clear()
        jobs.addAll(CommunityData.sampleJobs)
        donations.clear()
        donations.addAll(CommunityData.sampleDonations)
        appliedJobIds.clear()
        reservedDonationIds.clear()
        reservedQuantities.clear()
        myReservedQuantities.clear()
        appliedJobStatuses.clear()
        jobCreatedAtMillis.clear()
        donationCreatedAtMillis.clear()
        donationImageUrls.clear()
        lastRemotePostError = null
    }

    fun createdAtMillisForJob(jobId: String): Long? {
        return jobCreatedAtMillis[jobId]
    }

    fun createdAtMillisForDonation(donationId: String): Long? {
        return donationCreatedAtMillis[donationId]
    }

    fun imageUrlForDonation(donationId: String): String? {
        return donationImageUrls[donationId]
    }

    suspend fun reloadFromSupabase() {
        val currentUserId = supabase.auth.currentUserOrNull()?.id ?: return
        isLoadingRemotePosts = true

        try {
            val remoteData = withContext(Dispatchers.IO) {
                val remoteJobs = supabase.from("jobs")
                    .select {
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<SupabaseJobRow>()

                val remoteDonations = supabase.from("donations")
                    .select {
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<SupabaseDonationRow>()

                val myApplications = supabase.from("job_applications")
                    .select {
                        filter { eq("applicant_id", currentUserId) }
                    }
                    .decodeList<JobApplicationRow>()

                val remoteReservations = if (remoteDonations.isEmpty()) {
                    emptyList()
                } else {
                    supabase.from("donation_reservations")
                        .select {
                            filter { isIn("donation_id", remoteDonations.map { it.id }) }
                        }
                        .decodeList<DonationReservation>()
                }

                RemoteBundle(remoteJobs, remoteDonations, myApplications, remoteReservations)
            }

            jobCreatedAtMillis.clear()
            donationCreatedAtMillis.clear()
            donationImageUrls.clear()

            val loadedJobs = remoteData.jobs.map { row ->
                row.createdAt.toEpochMillisOrNull()?.let { jobCreatedAtMillis[row.id] = it }
                row.toJobPost(currentUserId)
            }

            val loadedDonations = remoteData.donations.map { row ->
                row.createdAt.toEpochMillisOrNull()?.let { donationCreatedAtMillis[row.id] = it }
                donationImageUrls[row.id] = row.imageUrl
                row.toDonationPost(currentUserId)
            }

            jobs.clear()
            jobs.addAll(loadedJobs)
            jobs.addAll(CommunityData.sampleJobs)

            donations.clear()
            donations.addAll(loadedDonations)
            donations.addAll(CommunityData.sampleDonations)

            appliedJobIds.clear()
            appliedJobIds.addAll(remoteData.myApplications.map { it.jobId })
            appliedJobStatuses.clear()
            remoteData.myApplications.forEach { appliedJobStatuses[it.jobId] = it.status }

            reservedQuantities.clear()
            myReservedQuantities.clear()
            reservedDonationIds.clear()
            myReservationStatuses.clear()

            remoteData.reservations
                .filter { it.status != "rejected" }
                .groupBy { it.donationId }
                .forEach { (donationId, rows) ->
                    reservedQuantities[donationId] = rows.sumOf { it.quantity }
                }

            remoteData.reservations
                .filter { it.reservedBy == currentUserId && it.status != "rejected" }
                .groupBy { it.donationId }
                .forEach { (donationId, rows) ->
                    myReservedQuantities[donationId] = rows.sumOf { it.quantity }
                    reservedDonationIds.add(donationId)
                    myReservationStatuses[donationId] = rows.maxByOrNull { it.createdAt.orEmpty() }?.status ?: "pending"
                }

            lastRemotePostError = null
        } catch (e: Exception) {
            lastRemotePostError = "Unable to load posts. Please try again."
        } finally {
            isLoadingRemotePosts = false
        }
    }

    suspend fun addJobToSupabase(
        title: String,
        category: String,
        location: String,
        payment: String,
        paymentUnit: String,
        description: String,
        isUrgent: Boolean = false
    ): Result<JobPost> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("Please login before posting a job.")
            val isNegotiable = paymentUnit == "Negotiable"

            val row = withContext(Dispatchers.IO) {
                supabase.from("jobs")
                    .insert(
                        SupabaseJobInsert(
                            userId = currentUserId,
                            title = title.trim(),
                            category = category,
                            location = location.trim(),
                            paymentAmount = if (isNegotiable) null else payment.trim().toDoubleOrNull(),
                            paymentPeriod = paymentUnit,
                            isNegotiable = isNegotiable,
                            description = description.trim(),
                            isUrgent = isUrgent
                        )
                    ) {
                        select()
                    }
                    .decodeSingle<SupabaseJobRow>()
            }

            row.createdAt.toEpochMillisOrNull()?.let { jobCreatedAtMillis[row.id] = it }
            val post = row.toJobPost(currentUserId)
            jobs.add(0, post)
            post
        }
    }

    suspend fun addDonationToSupabase(
        context: Context,
        itemName: String,
        itemCategory: String,
        pickupLocation: String,
        description: String,
        photoUri: Uri,
        quantity: Int = 1
    ): Result<DonationPost> {
        return runCatching {
            val currentUserId = supabase.auth.currentUserOrNull()?.id
                ?: error("Please login before posting a donation.")

            val row = withContext(Dispatchers.IO) {
                val imageUrl = uploadDonationImage(context, currentUserId, photoUri)

                supabase.from("donations")
                    .insert(
                        SupabaseDonationInsert(
                            userId = currentUserId,
                            itemName = itemName.trim(),
                            itemCategory = itemCategory,
                            pickupLocation = pickupLocation.trim(),
                            description = description.trim(),
                            imageUrl = imageUrl,
                            quantity = quantity.coerceAtLeast(1)
                        )
                    ) {
                        select()
                    }
                    .decodeSingle<SupabaseDonationRow>()
            }

            row.createdAt.toEpochMillisOrNull()?.let { donationCreatedAtMillis[row.id] = it }
            donationImageUrls[row.id] = row.imageUrl
            val post = row.toDonationPost(currentUserId)
            donations.add(0, post)
            post
        }
    }

    suspend fun deleteJobFromSupabase(jobId: String): Result<Unit> {
        return runCatching {
            if (!jobId.isSampleJobId()) {
                supabase.auth.currentUserOrNull()
                    ?: error("Please login before deleting a job.")

                withContext(Dispatchers.IO) {
                    supabase.from("jobs").delete {
                        filter {
                            eq("id", jobId)
                        }
                    }
                    ensureRemoteRowDeleted(
                        tableName = "jobs",
                        postId = jobId,
                        errorMessage = "Delete failed. Please try again."
                    )
                }
            }

            deleteJob(jobId)
        }
    }

    suspend fun deleteDonationFromSupabase(donationId: String): Result<Unit> {
        return runCatching {
            val imageUrl = donationImageUrls[donationId]

            if (!donationId.isSampleDonationId()) {
                supabase.auth.currentUserOrNull()
                    ?: error("Please login before deleting a donation.")

                withContext(Dispatchers.IO) {
                    supabase.from("donations").delete {
                        filter {
                            eq("id", donationId)
                        }
                    }
                    ensureRemoteRowDeleted(
                        tableName = "donations",
                        postId = donationId,
                        errorMessage = "Delete failed. Please try again."
                    )

                    storagePathFromDonationImageUrl(imageUrl)?.let { imagePath ->
                        runCatching {
                            supabase.storage.from(DONATION_IMAGE_BUCKET).delete(imagePath)
                        }
                    }
                }
            }

            deleteDonation(donationId)
        }
    }

    suspend fun applyToJob(
        jobId: String,
        applicantName: String,
        applicantPhone: String,
        applicantAge: Int,
        message: String = ""
    ) {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return
        if (appliedJobIds.contains(jobId)) return

        appliedJobIds.add(jobId)
        appliedJobStatuses[jobId] = "pending"
        try {
            withContext(Dispatchers.IO) {
                supabase.from("job_applications").insert(
                    JobApplicationInsert(
                        jobId = jobId,
                        applicantId = userId,
                        applicantName = applicantName.trim(),
                        applicantPhone = applicantPhone.trim(),
                        applicantAge = applicantAge,
                        message = message.trim(),
                        status = "pending"
                    )
                )
            }
        } catch (e: Exception) {
            appliedJobIds.remove(jobId)
            appliedJobStatuses.remove(jobId)
            throw e
        }
    }

    suspend fun unapplyFromJob(jobId: String) {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return
        appliedJobIds.remove(jobId)
        val previousStatus = appliedJobStatuses.remove(jobId)
        try {
            withContext(Dispatchers.IO) {
                supabase.from("job_applications").delete {
                    filter {
                        eq("job_id", jobId)
                        eq("applicant_id", userId)
                    }
                }
            }
        } catch (e: Exception) {
            appliedJobIds.add(jobId)
            previousStatus?.let { appliedJobStatuses[jobId] = it }
            throw e
        }
    }

    suspend fun loadApplicantsForJob(jobId: String): Result<List<JobApplicant>> {
        return runCatching {
            withContext(Dispatchers.IO) {
                supabase.from("job_applications")
                    .select {
                        filter { eq("job_id", jobId) }
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<JobApplicant>()
            }
        }
    }

    suspend fun setApplicationStatus(applicationId: String, newStatus: String): Result<Unit> {
        return runCatching {
            withContext(Dispatchers.IO) {
                supabase.from("job_applications")
                    .update(JobApplicationStatusUpdate(status = newStatus)) {
                        filter { eq("id", applicationId) }
                    }
            }
        }
    }

    fun remainingQuantity(donation: DonationPost): Int {
        val alreadyClaimed = reservedQuantities[donation.id] ?: 0
        return (donation.quantity - alreadyClaimed).coerceAtLeast(0)
    }

    fun isFullyReserved(donation: DonationPost): Boolean {
        return remainingQuantity(donation) <= 0
    }

    fun reservedQuantityFor(donationId: String): Int {
        return myReservedQuantities[donationId] ?: 0
    }

    fun totalReservedQuantityFor(donationId: String): Int {
        return reservedQuantities[donationId] ?: 0
    }

    suspend fun reserveDonationToSupabase(
        donationId: String,
        reserverName: String,
        reserverPhone: String,
        reserverAge: Int,
        amount: Int = 1
    ): Result<Int> {
        return runCatching {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: error("Please login before reserving an item.")

            val donation = donations.firstOrNull { it.id == donationId }
                ?: error("This item is no longer available.")

            val alreadyClaimed = reservedQuantities[donationId] ?: 0
            val claimable = (donation.quantity - alreadyClaimed).coerceAtLeast(0)
            val actualAmount = amount.coerceIn(0, claimable)
            if (actualAmount <= 0) return@runCatching 0

            withContext(Dispatchers.IO) {
                supabase.from("donation_reservations").insert(
                    DonationReservationInsert(
                        donationId = donationId,
                        reservedBy = userId,
                        reserverName = reserverName.trim(),
                        reserverPhone = reserverPhone.trim(),
                        reserverAge = reserverAge,
                        quantity = actualAmount,
                        status = "pending"
                    )
                )
            }

            reservedQuantities[donationId] = alreadyClaimed + actualAmount
            myReservedQuantities[donationId] = (myReservedQuantities[donationId] ?: 0) + actualAmount
            myReservationStatuses[donationId] = "pending"
            if (!reservedDonationIds.contains(donationId)) {
                reservedDonationIds.add(donationId)
            }
            actualAmount
        }
    }

    suspend fun unreserveDonationFromSupabase(donationId: String): Result<Unit> {
        return runCatching {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: error("Please login before updating a reservation.")

            withContext(Dispatchers.IO) {
                supabase.from("donation_reservations").delete {
                    filter {
                        eq("donation_id", donationId)
                        eq("reserved_by", userId)
                    }
                }
            }

            reservedQuantities[donationId] =
                ((reservedQuantities[donationId] ?: 0) - (myReservedQuantities[donationId] ?: 0)).coerceAtLeast(0)
            myReservedQuantities.remove(donationId)
            reservedDonationIds.remove(donationId)
            myReservationStatuses.remove(donationId)
        }
    }

    suspend fun loadReserversForDonation(donationId: String): Result<List<DonationReservation>> {
        return runCatching {
            val rows = withContext(Dispatchers.IO) {
                supabase.from("donation_reservations")
                    .select {
                        filter { eq("donation_id", donationId) }
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<DonationReservation>()
            }
            reservedQuantities[donationId] = rows.filter { it.status != "rejected" }.sumOf { it.quantity }
            rows
        }
    }

    suspend fun setReservationStatus(reservation: DonationReservation, newStatus: String): Result<Unit> {
        return runCatching {
            withContext(Dispatchers.IO) {
                supabase.from("donation_reservations")
                    .update(DonationReservationStatusUpdate(status = newStatus)) {
                        filter { eq("id", reservation.id) }
                    }
            }

            if (newStatus == "rejected") {
                reservedQuantities[reservation.donationId] =
                    ((reservedQuantities[reservation.donationId] ?: 0) - reservation.quantity).coerceAtLeast(0)
                if (reservation.reservedBy == supabase.auth.currentUserOrNull()?.id) {
                    myReservedQuantities.remove(reservation.donationId)
                    reservedDonationIds.remove(reservation.donationId)
                    myReservationStatuses[reservation.donationId] = "rejected"
                }
            } else if (reservation.reservedBy == supabase.auth.currentUserOrNull()?.id) {
                myReservationStatuses[reservation.donationId] = newStatus
            }
        }
    }

    fun deleteJob(jobId: String) {
        jobs.removeAll { it.id == jobId }
        appliedJobIds.remove(jobId)
        jobCreatedAtMillis.remove(jobId)
    }

    fun deleteDonation(donationId: String) {
        donations.removeAll { it.id == donationId }
        reservedDonationIds.remove(donationId)
        reservedQuantities.remove(donationId)
        myReservedQuantities.remove(donationId)
        donationCreatedAtMillis.remove(donationId)
        donationImageUrls.remove(donationId)
    }

    fun updateJob(updatedJob: JobPost) {
        val index = jobs.indexOfFirst { it.id == updatedJob.id }
        if (index != -1) {
            jobs[index] = updatedJob
        }
    }

    fun updateDonation(updatedDonation: DonationPost) {
        val index = donations.indexOfFirst { it.id == updatedDonation.id }
        if (index != -1) {
            donations[index] = updatedDonation
        }
    }
}

private const val DONATION_IMAGE_BUCKET = "donation-images"
private const val SUPABASE_PUBLIC_URL = "https://teuanaiyzlytvnvxdzcr.supabase.co"

private data class RemoteBundle(
    val jobs: List<SupabaseJobRow>,
    val donations: List<SupabaseDonationRow>,
    val myApplications: List<JobApplicationRow>,
    val reservations: List<DonationReservation>
)

@Serializable
private data class SupabaseJobInsert(
    @SerialName("user_id") val userId: String,
    val title: String,
    val category: String,
    val location: String,
    @SerialName("payment_amount") val paymentAmount: Double? = null,
    @SerialName("payment_period") val paymentPeriod: String,
    @SerialName("is_negotiable") val isNegotiable: Boolean,
    val description: String,
    @SerialName("is_urgent") val isUrgent: Boolean = false
)

@Serializable
private data class JobApplicationInsert(
    @SerialName("job_id") val jobId: String,
    @SerialName("applicant_id") val applicantId: String,
    @SerialName("applicant_name") val applicantName: String,
    @SerialName("applicant_phone") val applicantPhone: String,
    @SerialName("applicant_age") val applicantAge: Int,
    val message: String = "",
    val status: String = "pending"
)

@Serializable
private data class JobApplicationRow(
    @SerialName("job_id") val jobId: String,
    val status: String = "pending"
)

@Serializable
private data class JobApplicationStatusUpdate(
    val status: String
)

@Serializable
data class JobApplicant(
    val id: String,
    @SerialName("job_id") val jobId: String,
    @SerialName("applicant_id") val applicantId: String,
    @SerialName("applicant_name") val applicantName: String,
    @SerialName("applicant_phone") val applicantPhone: String,
    @SerialName("applicant_age") val applicantAge: Int? = null,
    val message: String = "",
    val status: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class DonationReservation(
    val id: String,
    @SerialName("donation_id") val donationId: String,
    @SerialName("reserved_by") val reservedBy: String,
    @SerialName("reserver_name") val reserverName: String? = null,
    @SerialName("reserver_phone") val reserverPhone: String? = null,
    @SerialName("reserver_age") val reserverAge: Int? = null,
    val quantity: Int = 1,
    val status: String = "pending",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
private data class DonationReservationInsert(
    @SerialName("donation_id") val donationId: String,
    @SerialName("reserved_by") val reservedBy: String,
    @SerialName("reserver_name") val reserverName: String,
    @SerialName("reserver_phone") val reserverPhone: String,
    @SerialName("reserver_age") val reserverAge: Int,
    val quantity: Int,
    val status: String = "pending"
)

@Serializable
private data class DonationReservationStatusUpdate(
    val status: String
)

@Serializable
private data class DeletedPostRow(
    val id: String
)

@Serializable
private data class SupabaseJobRow(
    val id: String,
    @SerialName("user_id") val userId: String,
    val title: String,
    val category: String,
    val location: String,
    @SerialName("payment_amount") val paymentAmount: Double? = null,
    @SerialName("payment_period") val paymentPeriod: String,
    @SerialName("is_negotiable") val isNegotiable: Boolean,
    @SerialName("is_urgent") val isUrgent: Boolean = false,
    val description: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
private data class SupabaseDonationInsert(
    @SerialName("user_id") val userId: String,
    @SerialName("item_name") val itemName: String,
    @SerialName("item_category") val itemCategory: String,
    @SerialName("pickup_location") val pickupLocation: String,
    val description: String,
    @SerialName("image_url") val imageUrl: String,
    val quantity: Int = 1
)

@Serializable
private data class SupabaseDonationRow(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("item_name") val itemName: String,
    @SerialName("item_category") val itemCategory: String,
    @SerialName("pickup_location") val pickupLocation: String,
    val description: String,
    @SerialName("image_url") val imageUrl: String,
    val quantity: Int = 1,
    @SerialName("created_at") val createdAt: String? = null
)

private fun SupabaseJobRow.toJobPost(currentUserId: String): JobPost {
    val paymentText = if (isNegotiable || paymentPeriod == "Negotiable") {
        "Negotiable"
    } else {
        "RM${paymentAmount.toPaymentText()} / ${paymentPeriod.lowercase(Locale.ROOT)}"
    }

    return JobPost(
        id = id,
        title = title,
        category = category,
        location = location,
        payment = paymentText,
        description = description,
        posted = createdAt.toPostedText(),
        mine = userId == currentUserId,
        isUrgent = isUrgent
    )
}

private fun SupabaseDonationRow.toDonationPost(currentUserId: String): DonationPost {
    return DonationPost(
        id = id,
        title = itemName,
        category = itemCategory,
        location = pickupLocation,
        description = description,
        posted = createdAt.toPostedText(),
        tint = PostValidator.tintForCategory(itemCategory),
        quantity = quantity,
        mine = userId == currentUserId
    )
}

private fun Double?.toPaymentText(): String {
    val amount = this ?: 0.0
    return if (amount % 1.0 == 0.0) {
        amount.toInt().toString()
    } else {
        "%.2f".format(Locale.US, amount).trimEnd('0').trimEnd('.')
    }
}

private fun String?.toPostedText(): String {
    val createdAtMillis = toEpochMillisOrNull() ?: return "Just Posted"
    val ageMillis = (System.currentTimeMillis() - createdAtMillis).coerceAtLeast(0L)
    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour

    val ageText = when {
        ageMillis < 30 * minute -> "Just Posted"
        ageMillis < hour -> "${ageMillis / minute}m"
        ageMillis < day -> "${ageMillis / hour}h"
        else -> {
            val days = ageMillis / day
            if (days == 1L) "1Day" else "${days}Days"
        }
    }

    return if (ageText == "Just Posted") ageText else "Posted $ageText ago"
}

private fun String?.toEpochMillisOrNull(): Long? {
    return this?.let { value ->
        runCatching { Instant.parse(value).toEpochMilli() }.getOrNull()
    }
}

private fun uploadFileNameFor(userId: String, mimeType: String?): String {
    val extension = MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(mimeType)
        ?: "jpg"
    return "$userId/${System.currentTimeMillis()}.$extension"
}

private fun String.isSampleJobId(): Boolean {
    return startsWith("job-")
}

private fun String.isSampleDonationId(): Boolean {
    return startsWith("donation-")
}

private fun storagePathFromDonationImageUrl(imageUrl: String?): String? {
    val publicBucketPrefix = "$SUPABASE_PUBLIC_URL/storage/v1/object/public/$DONATION_IMAGE_BUCKET/"
    return imageUrl
        ?.takeIf { it.startsWith(publicBucketPrefix) }
        ?.removePrefix(publicBucketPrefix)
        ?.takeIf { it.isNotBlank() }
}

private suspend fun ensureRemoteRowDeleted(
    tableName: String,
    postId: String,
    errorMessage: String
) {
    val remainingRows = supabase.from(tableName)
        .select {
            filter {
                eq("id", postId)
            }
        }
        .decodeList<DeletedPostRow>()

    if (remainingRows.isNotEmpty()) {
        error(errorMessage)
    }
}

private fun readBytesFromUri(context: Context, photoUri: Uri): ByteArray {
    return context.contentResolver.openInputStream(photoUri)?.use { it.readBytes() }
        ?: error("Cannot read the selected donation photo.")
}

private suspend fun uploadDonationImage(
    context: Context,
    userId: String,
    photoUri: Uri
): String {
    val mimeType = context.contentResolver.getType(photoUri) ?: "image/jpeg"
    val filePath = uploadFileNameFor(userId, mimeType)
    val fileBytes = readBytesFromUri(context, photoUri)

    supabase.storage.from(DONATION_IMAGE_BUCKET).upload(
        path = filePath,
        data = fileBytes
    ) {
        upsert = false
    }

    return "$SUPABASE_PUBLIC_URL/storage/v1/object/public/$DONATION_IMAGE_BUCKET/$filePath"
}