package com.example.moviereviewapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.MovieListResponse
import com.example.moviereviewapp.ui.adapter.AllMovieListAdapter
import com.example.moviereviewapp.ui.fragments.HomeFragment
import com.example.moviereviewapp.ui.fragments.HomeFragment.Companion.NOW_PLAYING
import com.example.moviereviewapp.ui.fragments.HomeFragment.Companion.POPULAR
import com.example.moviereviewapp.ui.fragments.HomeFragment.Companion.SELECTED_TITLE
import com.example.moviereviewapp.ui.fragments.HomeFragment.Companion.SELECTED_TYPE
import com.example.moviereviewapp.ui.fragments.HomeFragment.Companion.TOP_RATED
import com.example.moviereviewapp.ui.fragments.HomeFragment.Companion.UPCOMING
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.utils.Constants.QUERY_PAGE_SIZE
import com.example.moviereviewapp.utils.MyScrollListener
import com.example.moviereviewapp.utils.Resource
import kotlinx.android.synthetic.main.activity_all_movies.*

class AllMoviesActivity : AppCompatActivity() {
    lateinit var movieViewModel: MovieViewModel
    lateinit var adapter: AllMovieListAdapter
    var type: Int = POPULAR
    lateinit var recyclerView: RecyclerView
    lateinit var myScrollListener: MyScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_movies)
        ViewModelProvider(this).get(MovieViewModel::class.java).also {
            movieViewModel = it
        }

        actionBar?.setDisplayHomeAsUpEnabled(true)

        type = intent.getIntExtra(SELECTED_TYPE, 1)
        val data = getType(type)

        addObserver(data)
        recyclerView = rv_all_Movies
        LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
            recyclerView.layoutManager = this
        }
        setRecyclerView()
        title = intent.getStringExtra(SELECTED_TITLE)

    }

    private fun setRecyclerView() {
        adapter = AllMovieListAdapter(this)
        recyclerView.adapter = adapter
        myScrollListener = MyScrollListener(type) {
            when (type) {
                NOW_PLAYING -> movieViewModel.getNowPlayingMovies()
                POPULAR -> movieViewModel.getPopularMovies()
                UPCOMING -> movieViewModel.getUpComingMovies()
                TOP_RATED -> movieViewModel.getTopRatedMovies()
            }
        }
        recyclerView.addOnScrollListener(myScrollListener)
    }

    private fun getType(value: Int): MutableLiveData<Resource<MovieListResponse>> {
        return when (value) {
            POPULAR -> movieViewModel.popularMovies
            TOP_RATED -> movieViewModel.topRatedMovies
            NOW_PLAYING -> movieViewModel.nowPlaying
            UPCOMING -> movieViewModel.upComingMovies
            else -> movieViewModel.popularMovies
        }
    }

    private fun hideProgressBar() {
        pagination_prograss_bar.visibility = View.INVISIBLE
        myScrollListener.isLoading = false
    }

    private fun showProgressBar() {
        pagination_prograss_bar.visibility = View.VISIBLE
        myScrollListener.isLoading = true
    }

    private fun addObserver(data: MutableLiveData<Resource<MovieListResponse>>) {
        data.observe(this) {

            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    main_progress_bar.visibility =View.GONE
                    it.data?.let { it1 ->
                        adapter.setMoviesList(it1.results.toList())
                    }
                    val totalPage = it.data!!.total_pages
                    myScrollListener.isLastPage = it.data.page == totalPage
                }
                is Resource.Error -> {
                    hideProgressBar()
                    main_progress_bar.visibility =View.GONE
                    Toast.makeText(
                        this,
                        it.error.status_message,
                        Toast.LENGTH_LONG
                    ).show()

                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                onBackPressed()
                true
            }
            else -> false
        }
    }
}
