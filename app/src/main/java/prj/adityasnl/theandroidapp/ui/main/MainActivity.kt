package prj.adityasnl.theandroidapp.ui.main

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import prj.adityasnl.theandroidapp.R
import prj.adityasnl.theandroidapp.adapter.ListAdapter
import prj.adityasnl.theandroidapp.utils.MyArrayList
import prj.adityasnl.theandroidapp.utils.State
import prj.adityasnl.theandroidapp.utils.RecyclerViewMargin


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainActivity : AppCompatActivity(), KoinComponent {

    private val viewModel: MainViewModel by viewModel()

    private var trackItems: MyArrayList = MyArrayList()
    private val tracksAdapter: ListAdapter by inject {
        parametersOf(trackItems)
    }

    private var moviesItems: MyArrayList = MyArrayList()
    private val moviesAdapter: ListAdapter by inject {
        parametersOf(moviesItems)
    }

    private var episodesItems: MyArrayList = MyArrayList()
    private val episodesAdapter: ListAdapter by inject {
        parametersOf(episodesItems)
    }

    private var booksItems: MyArrayList = MyArrayList()
    private val booksAdapter: ListAdapter by inject {
        parametersOf(booksItems)
    }

    private val refresh_handler_timeout = 3200L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAdapter()
        initData()

        swipe_refresh_layout.setOnRefreshListener {
            loadData()
            Handler().postDelayed({
                swipe_refresh_layout.isRefreshing = false
            }, refresh_handler_timeout)
        }
    }

    private fun initAdapter() {
        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_tracks.layoutManager = layoutManager1
        rv_tracks.adapter = tracksAdapter
        rv_tracks.addItemDecoration(RecyclerViewMargin(resources.getDimension(R.dimen.default_padding).toInt()))

        val layoutManager2 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_movies.layoutManager = layoutManager2
        rv_movies.adapter = moviesAdapter
        rv_movies.addItemDecoration(RecyclerViewMargin(resources.getDimension(R.dimen.default_padding).toInt()))

        val layoutManager3 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_episodes.layoutManager = layoutManager3
        rv_episodes.adapter = episodesAdapter
        rv_episodes.addItemDecoration(RecyclerViewMargin(resources.getDimension(R.dimen.default_padding).toInt()))

        val layoutManager4 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_books.layoutManager = layoutManager4
        rv_books.adapter = booksAdapter
        rv_books.addItemDecoration(RecyclerViewMargin(resources.getDimension(R.dimen.default_padding).toInt()))
    }

    private fun initData() {
        viewModel.tracksData.observe(this, Observer { appState ->
            when (appState) {
                is State.Loading -> {}
                is State.Error -> {
                    Log.d(TAG, appState.message)
                }
                is State.Success -> {
                    trackItems.clear()
                    val title = appState.data.getAsJsonObject("feed")
                    title_tracks.text = title.get("title").asString

                    val results = appState.data.getAsJsonObject("feed").getAsJsonArray("results")
                    for (i in results) {
                        trackItems.add(i as JsonObject)
                    }
                    tracksAdapter.notifyDataSetChanged()
                }
            }
        })

        viewModel.moviesData.observe(this, Observer { appState ->
            when (appState) {
                is State.Loading -> {}
                is State.Error -> {
                    Log.d(TAG, appState.message)
                }
                is State.Success -> {
                    moviesItems.clear()
                    val title = appState.data.getAsJsonObject("feed")
                    title_movies.text = title.get("title").asString

                    val results = appState.data.getAsJsonObject("feed").getAsJsonArray("results")
                    for (i in results) {
                        moviesItems.add(i as JsonObject)
                    }
                    moviesAdapter.notifyDataSetChanged()
                }
            }
        })

        viewModel.episodesData.observe(this, Observer { appState ->
            when (appState) {
                is State.Loading -> {}
                is State.Error -> {
                    Log.d(TAG, appState.message)
                }
                is State.Success -> {
                    episodesItems.clear()
                    val title = appState.data.getAsJsonObject("feed")
                    title_episodes.text = title.get("title").asString

                    val results = appState.data.getAsJsonObject("feed").getAsJsonArray("results")
                    for (i in results) {
                        episodesItems.add(i as JsonObject)
                    }
                    episodesAdapter.notifyDataSetChanged()
                }
            }
        })

        viewModel.booksData.observe(this, Observer { appState ->
            when (appState) {
                is State.Loading -> {}
                is State.Error -> {
                    Log.d(TAG, appState.message)
                }
                is State.Success -> {
                    booksItems.clear()
                    val title = appState.data.getAsJsonObject("feed")
                    title_books.text = title.get("title").asString

                    val results = appState.data.getAsJsonObject("feed").getAsJsonArray("results")
                    for (i in results) {
                        booksItems.add(i as JsonObject)
                    }
                    booksAdapter.notifyDataSetChanged()
                }
            }
        })

        loadData()
    }

    private fun loadData() {
        viewModel.getData()
    }

    companion object {
        private val TAG = "xoxo"
    }
}
