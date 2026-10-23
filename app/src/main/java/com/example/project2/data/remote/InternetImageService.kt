package com.example.project2.data.remote

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class InternetImageService(
    private val client: OkHttpClient = defaultClient
) {
    companion object {
        private const val USER_AGENT = "MathPartyApp/1.0 (Android; PartyGame; Contact: support@mathparty.app)"

        val defaultClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
        }
    }

    /**
     * Finds the best diagram / formula image URL from Wikipedia / Wikimedia Commons.
     * Tries English Wikipedia search first (widest mathematical diagram coverage),
     * then Russian Wikipedia search, then Wikimedia Commons.
     */
    suspend fun searchMathImageUrl(query: String): String? = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext null
        val cleanQuery = query.trim()

        // 1. Try English Wikipedia Search with pageimages
        fetchFromWikipediaSearch("en.wikipedia.org", cleanQuery)?.let { return@withContext it }

        // 2. Try Russian Wikipedia Search with pageimages
        fetchFromWikipediaSearch("ru.wikipedia.org", cleanQuery)?.let { return@withContext it }

        // 3. Try English Wikipedia Summary REST API (if query matches article title closely)
        fetchFromWikipediaSummary("en.wikipedia.org", cleanQuery)?.let { return@withContext it }

        // 4. Try Russian Wikipedia Summary REST API
        fetchFromWikipediaSummary("ru.wikipedia.org", cleanQuery)?.let { return@withContext it }

        // 5. Try Wikimedia Commons Search
        fetchFromWikimediaCommons(cleanQuery)?.let { return@withContext it }

        null
    }

    private fun fetchFromWikipediaSearch(domain: String, query: String): String? {
        return try {
            val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
            val url = "https://$domain/w/api.php?action=query&generator=search&gsrsearch=$encoded&gsrlimit=1&prop=pageimages&pithumbsize=1000&format=json"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null

            val body = response.body?.string() ?: return null
            val root = JsonParser.parseString(body).asJsonObject
            val pages = root.getAsJsonObject("query")?.getAsJsonObject("pages") ?: return null

            for (entry in pages.entrySet()) {
                val pageObj = entry.value.asJsonObject
                val thumb = pageObj.getAsJsonObject("thumbnail")
                val source = thumb?.get("source")?.asString
                if (!source.isNullOrBlank()) {
                    return source
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun fetchFromWikipediaSummary(domain: String, title: String): String? {
        return try {
            val encoded = URLEncoder.encode(title.replace(" ", "_"), StandardCharsets.UTF_8.toString())
            val url = "https://$domain/api/rest_v1/page/summary/$encoded"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null

            val body = response.body?.string() ?: return null
            val root = JsonParser.parseString(body).asJsonObject

            // Check originalimage first (higher res), then thumbnail
            val original = root.getAsJsonObject("originalimage")?.get("source")?.asString
            if (!original.isNullOrBlank()) return original

            val thumb = root.getAsJsonObject("thumbnail")?.get("source")?.asString
            if (!thumb.isNullOrBlank()) return thumb

            null
        } catch (e: Exception) {
            null
        }
    }

    private fun fetchFromWikimediaCommons(query: String): String? {
        return try {
            val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
            val url = "https://commons.wikimedia.org/w/api.php?action=query&generator=search&gsrsearch=$encoded&gsrnamespace=6&gsrlimit=1&prop=imageinfo&iiprop=url&format=json"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null

            val body = response.body?.string() ?: return null
            val root = JsonParser.parseString(body).asJsonObject
            val pages = root.getAsJsonObject("query")?.getAsJsonObject("pages") ?: return null

            for (entry in pages.entrySet()) {
                val pageObj = entry.value.asJsonObject
                val imageInfoArray = pageObj.getAsJsonArray("imageinfo")
                if (imageInfoArray != null && imageInfoArray.size() > 0) {
                    val directUrl = imageInfoArray.get(0).asJsonObject.get("url")?.asString
                    if (!directUrl.isNullOrBlank()) {
                        return directUrl
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
}
