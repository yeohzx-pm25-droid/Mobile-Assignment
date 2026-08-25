package com.example.dcsg1_mobileassignment

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://teuanaiyzlytvnvxdzcr.supabase.co",
    supabaseKey = "YOUR_SUPABASE_KEY"
) {
    install(Auth) {
        host = "login-callback"
        scheme = "dcsg1app"
    }

    install(Postgrest)
}