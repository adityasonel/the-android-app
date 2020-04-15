package prj.adityasnl.theandroidapp.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("apple-music/hot-tracks/all/10/explicit.json")
    suspend fun getTracks(): Response<JsonObject>

    @GET("movies/top-movies/all/10/explicit.json")
    suspend fun getMovies(): Response<JsonObject>

    @GET("tv-shows/top-tv-episodes/all/10/explicit.json")
    suspend fun getEpisodes(): Response<JsonObject>

    @GET("books/top-free/all/10/explicit.json")
    suspend fun getBooks(): Response<JsonObject>

    companion object {
        const val BASE_URL = "https://rss.itunes.apple.com/api/v1/us/"
    }
}