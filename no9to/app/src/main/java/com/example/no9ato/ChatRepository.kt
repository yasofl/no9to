package com.example.no9ato

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatRepository {
    private val client = SupabaseClientProvider.client

    suspend fun fetchMessages(): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        val resp = client.postgrest["messages"].select().order("created_at", "desc").execute()
        resp.decodeList<Map<String, Any>>()
    }
}
