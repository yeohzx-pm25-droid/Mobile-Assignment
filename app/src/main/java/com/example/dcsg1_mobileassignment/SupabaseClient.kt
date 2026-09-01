package com.example.dcsg1_mobileassignment

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://teuanaiyzlytvnvxdzcr.supabase.co",
    supabaseKey = "sb_publishable_OuRXpnr66gdNzXO8k0xwkw_s28V3TvY"
) {
    install(Auth) {
        host = "login-callback"
        scheme = "dcsg1app"
    }

    install(Postgrest)
}
