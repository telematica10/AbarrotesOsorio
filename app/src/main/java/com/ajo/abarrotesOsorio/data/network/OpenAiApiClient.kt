package com.ajo.abarrotesOsorio.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class OpenAiApiClient {

    private val apiKey = ""

    private val client = OkHttpClient()

    /**
     * Genera una URL de imagen a partir de un prompt de texto utilizando la API de DALL-E.
     * Esta es una función 'suspend' y debe ser llamada desde una corrutina.
     * Lanza una excepción si la llamada a la API falla.
     */
    suspend fun generateImageUrl(prompt: String): String {

        return withContext(Dispatchers.IO) {
            val jsonBody = JSONObject().apply {
                put("model", "dall-e-3")
                put("prompt", prompt)
                put("n", 1)
                put("size", "1024x1024")
            }.toString()

            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string() ?: ""

                    Log.d("OpenAiApiClient", "Respuesta de la API: $responseBody")

                    if (!response.isSuccessful) {
                        throw IOException("Error al generar la imagen: ${response.code} - ${response.message}. Respuesta: $responseBody")
                    }

                    val jsonResponse = JSONObject(responseBody)

                    val imageUrl = jsonResponse.getJSONArray("data")
                        .getJSONObject(0)
                        .getString("url")

                    return@withContext imageUrl
                }
            } catch (e: Exception) {
                Log.e("OpenAiApiClient", "Error en la llamada a la API: ${e.message}", e)
                throw e
            }
        }
    }
}