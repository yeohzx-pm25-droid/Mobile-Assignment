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
    val reservedDonationIds = mutableStateListOf<String>()

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

    fun resetJobFilters() {
        jobTypeFilter = JOB_FILTER_ALL
        jobStateFilter = JOB_FILTER_ALL
        jobSalaryUnitFilter = JOB_FILTER_ALL
    }

    fun resetLocalPosts() {
        jobs.clear()
        jobs.addAll(CommunityData.sampleJobs)
        donations.clear()
        donations.addAll(CommunityData.sampleDonations)
        appliedJobIds.clear()
        reservedDonationIds.clear()
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

                remoteJobs to remoteDonations
            }

            jobCreatedAtMillis.clear()
            donationCreatedAtMillis.clear()
            donationImageUrls.clear()

            val loadedJobs = remoteData.first.map { row ->
                row.createdAt.toEpochMillisOrNull()?.let { jobCreatedAtMillis[row.id] = it }
                row.toJobPost(currentUserId)
            }

            val loadedDonations = remoteData.second.map { row ->
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

            lastRemotePostError = null
        } catch (e: Exception) {
            lastRemotePostError = e.message ?: "Unable to load posts from Supabase."
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
        description: String
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
                            description = description.trim()
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
        photoUri: Uri
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
                            imageUrl = imageUrl
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

    fun applyToJob(jobId: String) {
        if (!appliedJobIds.contains(jobId)) {
            appliedJobIds.add(jobId)
        }
    }

    fun unapplyFromJob(jobId: String) {
        appliedJobIds.remove(jobId)
    }

    fun reserveDonation(donationId: String) {
        if (!reservedDonationIds.contains(donationId)) {
            reservedDonationIds.add(donationId)
        }
    }

    fun unreserveDonation(donationId: String) {
        reservedDonationIds.remove(donationId)
    }

    fun deleteJob(jobId: String) {
        jobs.removeAll { it.id == jobId }
        appliedJobIds.remove(jobId)
        jobCreatedAtMillis.remove(jobId)
    }

    fun deleteDonation(donationId: String) {
        donations.removeAll { it.id == donationId }
        reservedDonationIds.remove(donationId)
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

@Serializable
private data class SupabaseJobInsert(
    @SerialName("user_id") val userId: String,
    val title: String,
    val category: String,
    val location: String,
    @SerialName("payment_amount") val paymentAmount: Double? = null,
    @SerialName("payment_period") val paymentPeriod: String,
    @SerialName("is_negotiable") val isNegotiable: Boolean,
    val description: String
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
    @SerialName("image_url") val imageUrl: String
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
        mine = userId == currentUserId
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
