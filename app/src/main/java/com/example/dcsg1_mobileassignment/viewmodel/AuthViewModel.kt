package com.example.dcsg1_mobileassignment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dcsg1_mobileassignment.data.User
import com.example.dcsg1_mobileassignment.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Facebook
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthViewModel : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)
        private set

    // ==========================================
    // SUPABASE AUTHENTICATION LISTENER
    // ==========================================

    init {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                println("AUTH STATUS: $status")
                when (status) {
                    is SessionStatus.Authenticated -> {
                        println("SUPABASE USER AUTHENTICATED")
                        val supabaseUser = supabase.auth.currentUserOrNull()
                        if (supabaseUser != null) {
                            currentUser = User(
                                id = supabaseUser.id,
                                fullName = supabaseUser.userMetadata?.get("full_name")?.toString()?.replace("\"", "")
                                    ?: supabaseUser.userMetadata?.get("name")?.toString()?.replace("\"", "") ?: "",
                                email = supabaseUser.email ?: "",
                                phone = supabaseUser.userMetadata?.get("phone")?.toString()?.replace("\"", "") ?: "",
                                password = ""
                            )
                        }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        currentUser = null
                    }
                    else -> {}
                }
            }
        }
    }

    // ==========================================
    // NORMAL EMAIL LOGIN
    // ==========================================

    suspend fun login(email: String, password: String): String? {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: "Invalid email or password."
        }
    }

    // ==========================================
    // GOOGLE LOGIN
    // ==========================================
    fun loginWithGoogle() {
        viewModelScope.launch {
            try {
                supabase.auth.signInWith(Google)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==========================================
    // LOGOUT
    // ==========================================
    fun logout() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                currentUser = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==========================================
    // REGISTER
    // ==========================================
    // Returns null on success, or the real error message on failure so the UI
    // can show what actually went wrong instead of guessing.
    suspend fun register(fullName: String, email: String, phone: String, password: String): String? {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("full_name", fullName)
                    put("phone", phone)
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            println("REGISTER ERROR: ${e.message}")
            e.message ?: "Registration failed. Please try again."
        }
    }

    // ==========================================
    // RESET PASSWORD
    // ==========================================
    // Returns null on success, or the real error message on failure.
    suspend fun resetPassword(email: String, newPassword: String): String? {
        return try {
            supabase.auth.resetPasswordForEmail(email)
            null
        } catch (e: Exception) {
            e.printStackTrace()
            println("RESET PASSWORD ERROR: ${e.message}")
            e.message ?: "Failed to send reset link."
        }
    }

    // ==========================================
    // UPDATE PROFILE
    // ==========================================
    suspend fun updateProfile(fullName: String, phone: String): String? {
        return try {
            supabase.auth.updateUser {
                data = buildJsonObject {
                    put("full_name", fullName)
                    put("phone", phone)
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: "Failed to update profile."
        }
    }
}
