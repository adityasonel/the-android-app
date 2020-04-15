package prj.adityasnl.theandroidapp.repository

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import prj.adityasnl.theandroidapp.api.ApiService
import prj.adityasnl.theandroidapp.utils.State
import retrofit2.Response

@ExperimentalCoroutinesApi
class Repository(private val apiService: ApiService) {

    fun getTracks(): Flow<State<JsonObject>> {
        return object : NetworkBoundRepository<JsonObject>() {
            override suspend fun fetchFromRemote(): Response<JsonObject> = apiService.getTracks()
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun getMovies(): Flow<State<JsonObject>> {
        return object : NetworkBoundRepository<JsonObject>() {
            override suspend fun fetchFromRemote(): Response<JsonObject> = apiService.getMovies()
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun getEpisodes(): Flow<State<JsonObject>> {
        return object : NetworkBoundRepository<JsonObject>() {
            override suspend fun fetchFromRemote(): Response<JsonObject> = apiService.getEpisodes()
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun getBooks(): Flow<State<JsonObject>> {
        return object : NetworkBoundRepository<JsonObject>() {
            override suspend fun fetchFromRemote(): Response<JsonObject> = apiService.getBooks()
        }.asFlow().flowOn(Dispatchers.IO)
    }
}