package com.example.no9ato

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ProfileRepository {
    private val client = SupabaseClientProvider.client

    suspend fun uploadProfileImage(bucket: String, path: String, bytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
        val resp = client.storage.from(bucket).upload(path, bytes)
        resp.isSuccessful
    }
}
