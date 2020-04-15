package prj.adityasnl.theandroidapp.repository

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.flow
import prj.adityasnl.theandroidapp.utils.State
import retrofit2.Response

abstract class NetworkBoundRepository<T> {

    fun asFlow() = flow<State<T>> {
        emit(State.loading())
        try {
            val apiResponse = fetchFromRemote()
            val data = apiResponse.body()
            if (apiResponse.isSuccessful && data != null) {
                emit(State.success(data))
            } else {
                emit(State.error(apiResponse.message()))
            }
        } catch (e: Exception) {
            emit(State.error("Network error! can't get latest data."))
        }
    }

    @MainThread
    protected abstract suspend fun fetchFromRemote(): Response<T>
}