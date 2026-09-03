package com.example.dcsg1_mobileassignment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dcsg1_mobileassignment.data.User
import com.example.dcsg1_mobileassignment.supabase
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Facebook
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// Result of requesting a password reset email, so the UI can tell an
// unregistered email apart from a generic failure.
sealed class PasswordResetResult {
    object Success : PasswordResetResult()
    object EmailNotFound : PasswordResetResult()
    object Error : PasswordResetResult()
}

class AuthViewModel : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)
        private set

    var isPasswordRecovery by mutableStateOf(false)
        private set

    // True until the initial session check (restoring any saved login) has
    // finished. The UI should show a loading state instead of the login
    // screen while this is true, to avoid flashing "login" before we know
    // the user is already signed in.
    var isInitializing by mutableStateOf(true)
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

                        if (PasswordRecoveryState.pending) {
                            // Came from the "reset password" email link -
                            // don't log the user in, send them to set a
                            // new password instead.
                            PasswordRecoveryState.pending = false
                            isPasswordRecovery = true
                            isInitializing = false
                            return@collect
                        }

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
                        isInitializing = false
                    }
                    is SessionStatus.NotAuthenticated -> {
                        currentUser = null
                        isInitializing = false
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
            "Login failed. Please check your email and password."
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
                supabase.auth.signOut(SignOutScope.LOCAL)
                currentUser = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==========================================
    // REGISTER
    // ==========================================
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
            // signUpWith can start an active session immediately (when email
            // confirmation is off). We want registering to always require a
            // real, deliberate login afterwards so the user proves they know
            // their password - so sign that session back out right away.
            supabase.auth.signOut(SignOutScope.LOCAL)
            null
        } catch (e: Exception) {
            e.printStackTrace()
            println("REGISTER ERROR: ${e.message}")
            "Registration failed. Please try again."
        }
    }

    // ==========================================
    // FORGOT PASSWORD - STEP 1: send the recovery email
    // ==========================================
    // Supabase never accepts a new password directly here - it can only
    // email a one-time recovery link. The actual password change happens
    // in updateNewPassword(), once the user has tapped that link and we
    // have a valid recovery session.
    suspend fun sendPasswordResetEmail(email: String): PasswordResetResult {
        return try {
            if (!emailIsRegistered(email)) {
                return PasswordResetResult.EmailNotFound
            }

            // redirectUrl must match the scheme/host declared in the
            // manifest's intent-filter, otherwise Supabase falls back to
            // the dashboard's Site URL and the link opens a blank page.
            supabase.auth.resetPasswordForEmail(
                email = email,
                redirectUrl = "dcsg1app://login-callback"
            )
            PasswordResetResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            PasswordResetResult.Error
        }
    }

    // Checks the "email_exists" Postgres function (see project SQL setup)
    // so we can tell the user their email isn't registered instead of
    // silently sending nothing. If the check itself fails (e.g. network),
    // we fail open and let resetPasswordForEmail attempt the send.
    private suspend fun emailIsRegistered(email: String): Boolean {
        return try {
            supabase.postgrest.rpc(
                "email_exists",
                buildJsonObject { put("check_email", email) }
            ).decodeAs<Boolean>()
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    // ==========================================
    // FORGOT PASSWORD - STEP 2: set the new password
    // ==========================================
    // Called from the "New Password" screen after the user arrived via the
    // recovery email link (isPasswordRecovery == true).
    suspend fun updateNewPassword(newPassword: String): Boolean {
        return try {
            supabase.auth.updateUser {
                password = newPassword
            }
            isPasswordRecovery = false
            supabase.auth.signOut(SignOutScope.LOCAL)
            currentUser = null
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
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
            "Failed to update profile. Please try again."
        }
    }
}
