package com.example.chat_09_01.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object AuthApi {

    private const val BASE = "http://10.0.2.2:8081"
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    data class LoginResult(val ok: Boolean, val username: String?, val token: String?, val message: String?)

    suspend fun login(username: String, password: String): LoginResult =
        post("$BASE/login", username, password)

    suspend fun register(username: String, password: String): LoginResult =
        post("$BASE/register", username, password)

    private suspend fun post(url: String, username: String, password: String): LoginResult =
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("username", username)
                    put("password", password)
                }
                val body = json.toString().toRequestBody(JSON)

                val request = Request.Builder().url(url).post(body).build()
                client.newCall(request).execute().use { resp ->
                    val text = resp.body?.string() ?: ""
                    val obj = JSONObject(text)
                    LoginResult(
                        ok = obj.optBoolean("ok", false),
                        username = obj.optString("username").takeIf { it.isNotEmpty() },
                        token = obj.optString("token").takeIf { it.isNotEmpty() },
                        message = obj.optString("message").takeIf { it.isNotEmpty() }
                    )
                }
            } catch (e: Exception) {
                LoginResult(false, null, null, e.message ?: "网络错误")
            }
        }
}