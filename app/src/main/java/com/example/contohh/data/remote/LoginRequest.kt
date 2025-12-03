package com.example.contohh.data.remote

import com.google.gson.annotations.SerializedName

// =============== REQUESTS ===============

data class LoginRequest(
    @SerializedName("id_token")
    val idToken: String,

    @SerializedName("device_info")
    val deviceInfo: String
)

data class RegisterRequest(
    @SerializedName("id_token")
    val idToken: String,
    val name: String,
    val loginSource: String
)

// Kalau mau pakai untuk endpoint lain
data class CompleteProfileRequest(
    val name: String
)

// =============== RESPONSES ===============

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserData
)

data class RegisterResponse(
    val message: String,
    val user: UserData
)

data class UserData(
    val id: Int,
    @SerializedName("google_uid")
    val googleUid: String,
    val name: String,
    val email: String,
    val picture: String?
)
