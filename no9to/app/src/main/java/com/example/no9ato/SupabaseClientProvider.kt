package com.example.no9ato

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.storage

object SupabaseClientProvider {
    private const val SUPABASE_URL = "https://kuoyxklykjrypkxrbgeh.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt1b3l4a2x5a2pyeXBreHJiZ2VoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTc4NTYyNTAsImV4cCI6MjA3MzQzMjI1MH0.e_sKY94XktAhkAwEFE2nOXk0Ijcv-9X-9VlAtBcEC-c"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(gotrue)
            install(postgrest)
            install(storage)
            install(realtime)
        }
    }
}
