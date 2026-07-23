package com.example.movie.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TmdbSearchResponse(
    val results: List<TmdbMovieDto>? = null
)

@Serializable
data class TmdbMovieDto(
    val id: Int,
    val title: String,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
    val overview: String? = null
)

class TmdbService {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                isLenient = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 20000
            connectTimeoutMillis = 20000
        }
    }

    // Mã API Key mới (Đã kiểm tra hoạt động)
    private val apiKey = "a07e22bc18f5cb106bfe4cc1f83ad8ed"

    suspend fun searchMovies(query: String): List<TmdbMovieDto> {
        if (query.isBlank()) return emptyList()
        return try {
            val response: TmdbSearchResponse = client.get("https://api.themoviedb.org/3/search/movie") {
                // Thêm User-Agent để tránh lỗi Connection Reset trên một số mạng
                header(HttpHeaders.UserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                
                parameter("api_key", apiKey)
                parameter("query", query)
                parameter("language", "vi-VN")
            }.body()
            
            response.results ?: emptyList()
        } catch (e: Exception) {
            android.util.Log.e("TMDB_DEBUG", "Loi: ${e.message}")
            emptyList()
        }
    }
}
