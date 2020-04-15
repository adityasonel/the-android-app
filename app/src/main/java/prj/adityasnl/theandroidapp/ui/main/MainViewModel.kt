package prj.adityasnl.theandroidapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import prj.adityasnl.theandroidapp.repository.Repository
import prj.adityasnl.theandroidapp.utils.State

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _tracks = MutableLiveData<State<JsonObject>>()
    private val _movies = MutableLiveData<State<JsonObject>>()
    private val _episodes = MutableLiveData<State<JsonObject>>()
    private val _books = MutableLiveData<State<JsonObject>>()

    val tracksData: LiveData<State<JsonObject>>
        get() = _tracks

    val moviesData: LiveData<State<JsonObject>>
        get() = _movies

    val episodesData: LiveData<State<JsonObject>>
        get() = _episodes

    val booksData: LiveData<State<JsonObject>>
        get() = _books

    fun getData() {
        viewModelScope.launch {
            repository.getTracks().collect {
                _tracks.value = it
            }
        }
        viewModelScope.launch {
            repository.getMovies().collect {
                _movies.value = it
            }
        }

        viewModelScope.launch {
            repository.getEpisodes().collect {
                _episodes.value = it
            }
        }

        viewModelScope.launch {
            repository.getBooks().collect {
                _books.value = it
            }
        }
    }
}