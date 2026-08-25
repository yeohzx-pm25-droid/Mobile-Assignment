package com.example.dcsg1_mobileassignment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dcsg1_mobileassignment.data.User
import com.example.dcsg1_mobileassignment.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Facebook
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val users = mutableStateListOf<User>()

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

                        val supabaseUser =
                            supabase.auth.currentUserOrNull()

                        if (supabaseUser != null) {

                            println(
                                "SUPABASE USER: ${supabaseUser.email}"
                            )

                            currentUser = User(

                                id = supabaseUser.id,

                                fullName = supabaseUser.userMetadata
                                    ?.get("full_name")
                                    ?.toString()
                                    ?.replace("\"", "")
                                    ?: supabaseUser.userMetadata
                                        ?.get("name")
                                        ?.toString()
                                        ?.replace("\"", "")
                                    ?: "",

                                email = supabaseUser.email ?: "",

                                phone = "",

                                password = ""
                            )

                            println(
                                "CURRENT USER SET: ${currentUser?.email}"
                            )
                        }
                    }


                    is SessionStatus.NotAuthenticated -> {

                        println("USER NOT AUTHENTICATED")

                        currentUser = null

                    }


                    else -> {

                        println("AUTH STATUS: Loading/Refreshing")

                    }

                }

            }

        }

    }


    // ==========================================
    // NORMAL EMAIL LOGIN
    // ==========================================

    fun login(
        email: String,
        password: String
    ): Boolean {

        val user = users.find {

            it.email == email &&
                    it.password == password

        }

        return if (user != null) {

            currentUser = user

            true

        } else {

            false

        }

    }


    // ==========================================
    // GOOGLE LOGIN
    // ==========================================

    fun loginWithGoogle() {

        viewModelScope.launch {

            try {

                println("STARTING GOOGLE LOGIN")

                supabase.auth.signInWith(Google)

                println("GOOGLE LOGIN FINISHED")

            } catch (e: Exception) {

                println(
                    "GOOGLE LOGIN ERROR: ${e.message}"
                )

            }

        }

    }


    // ==========================================
    // FACEBOOK LOGIN
    // ==========================================

    fun loginWithFacebook() {

        viewModelScope.launch {

            try {

                supabase.auth.signInWith(Facebook)

            } catch (e: Exception) {

                println("Facebook login error: ${e.message}")

            }

        }

    }


    // ==========================================
    // LOGOUT
    // ==========================================

    fun logout() {

        currentUser = null

        viewModelScope.launch {

            try {

                supabase.auth.signOut()

            } catch (e: Exception) {

                println(
                    "LOGOUT ERROR: ${e.message}"
                )

            }

        }

    }


    // ==========================================
    // REGISTER
    // ==========================================

    fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String
    ): Boolean {

        if (users.any { it.email == email }) {

            return false

        }

        val user = User(

            id = (users.size + 1).toString(),

            fullName = fullName,

            email = email,

            phone = phone,

            password = password
        )

        users.add(user)

        return true
    }


    // ==========================================
    // RESET PASSWORD
    // ==========================================

    fun resetPassword(
        email: String,
        newPassword: String
    ): Boolean {

        val user = users.find {

            it.email == email

        }

        return if (user != null) {

            user.password = newPassword

            true

        } else {

            false

        }

    }


    // ==========================================
    // UPDATE PROFILE
    // ==========================================

    fun updateProfile(
        fullName: String,
        phone: String
    ) {

        currentUser?.let {

            it.fullName = fullName

            it.phone = phone

            currentUser = it.copy()

        }

    }

}