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
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

    var isInitializing by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                println("AUTH STATUS: $status")
                when (status) {
                    is SessionStatus.Authenticated -> {
                        println("SUPABASE USER AUTHENTICATED")
                        val supabaseUser = supabase.auth.currentUserOrNull()

                        if (PasswordRecoveryState.pending) {
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

    fun loginWithGoogle() {
        viewModelScope.launch {
            try {
                supabase.auth.signInWith(Google)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
            supabase.auth.signOut(SignOutScope.LOCAL)
            null
        } catch (e: Exception) {
            e.printStackTrace()
            println("REGISTER ERROR: ${e.message}")
            "Registration failed. Please try again."
        }
    }

    suspend fun sendPasswordResetEmail(email: String): PasswordResetResult {
        return try {
            if (!emailIsRegistered(email)) {
                return PasswordResetResult.EmailNotFound
            }

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
