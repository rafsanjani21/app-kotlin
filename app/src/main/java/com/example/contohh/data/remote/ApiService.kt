package com.example.contohh.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class GenericResponse(
    val message: String?
)

interface ApiService {

    @POST("api/login")
    suspend fun loginGoogle(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @POST("api/register")
    suspend fun registerGoogle(
        @Body body: RegisterRequest
    ): Response<GenericResponse>

    @POST("api/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<GenericResponse>
}

