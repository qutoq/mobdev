package io.github.mobdev.api

import io.github.mobdev.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.*

interface ChatApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<String>

    @GET("channels")
    suspend fun getChannels(): List<String>

    @GET("channel/{name}")
    suspend fun getMessages(
        @Path("name") channelName: String,
        @Query("limit") limit: Int = 20,
        @Query("lastKnownId") lastKnownId: String? = null,
        @Query("reverse") reverse: Boolean = false
    ): List<Message>

    @POST("messages")
    suspend fun sendMessage(@Body message: Message): String

    companion object {
        fun create(sessionManager: SessionManager): ChatApi {
            val authInterceptor = Interceptor { chain ->
                val token = runBlocking { sessionManager.token.first() }
                val request = chain.request().newBuilder().apply {
                    if (token.isNotEmpty()) addHeader("X-Auth-Token", token)
                }.build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
            val json = Json { ignoreUnknownKeys = true; isLenient = true }

            return Retrofit.Builder()
                .baseUrl("https://faerytea.name/")
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create()) // Важно: ПЕРВЫМ
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(ChatApi::class.java)
        }
    }
}