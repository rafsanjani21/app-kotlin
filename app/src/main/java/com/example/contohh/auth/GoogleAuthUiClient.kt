package com.example.contohh.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()

    // Login Firebase menggunakan ID Token dari Credential Manager
    suspend fun signInWithToken(idToken: String) =
        auth.signInWithCredential(
            com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        ).await()

    fun signOut() {
        auth.signOut()
    }

    fun currentUser() = auth.currentUser
}
