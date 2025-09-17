package com.example.no9ato

import io.github.jan.supabase.gotrue.signup.SignUpWithEmail
import io.github.jan.supabase.gotrue.signin.SignInWithEmail

object AuthRepository {
    private val client = SupabaseClientProvider.client

    suspend fun signUp(email: String, password: String) =
        client.auth.signUpWith(SignUpWithEmail(email = email, password = password))

    suspend fun signIn(email: String, password: String) =
        client.auth.signInWith(SignInWithEmail(email = email, password = password))

    suspend fun signOut() = client.auth.signOut()
}
