package com.example.moviereviewapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.moviereviewapp.R
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.model.MovieFullDetail
import com.example.moviereviewapp.model.MovieListResponse
import com.example.moviereviewapp.ui.activity.AllMoviesActivity
import com.example.moviereviewapp.ui.activity.MovieDetailActivity
import com.example.moviereviewapp.ui.adapter.MovieListAdapter
import com.example.moviereviewapp.utils.Resource
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.movie_list_layout.view.*


class HomeFragment : Fragment(R.layout.fragment_home), MovieListAdapter.MovieOnClickListener {
    lateinit var movieViewModel: MovieViewModel
    lateinit var popularLayout: View
    lateinit var topRatedLayout: View
    lateinit var nowPlayingLayout: View
    lateinit var upComingLayout: View
    lateinit var mainShimmerLayout: ShimmerFrameLayout
    lateinit var mainListLayout: View

    lateinit var popularAdapter: MovieListAdapter
    lateinit var topRatedAdapter: MovieListAdapter
    lateinit var upComingAdapter: MovieListAdapter
    lateinit var nowPlayingAdapter: MovieListAdapter

    companion object {
        const val SELECTED_TYPE = "type"
        const val SELECTED_TITLE = "title"
        const val POPULAR = 1
        const val TOP_RATED = 2
        const val NOW_PLAYING = 3
        const val UPCOMING = 4
        const val MOVIE_ID = "id"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        popularAdapter = MovieListAdapter(requireContext(), MovieListAdapter.POPULAR, this)
        topRatedAdapter = MovieListAdapter(requireContext(), MovieListAdapter.TOP_RATED, this)
        upComingAdapter = MovieListAdapter(requireContext(), MovieListAdapter.UPCOMING, this)
        nowPlayingAdapter = MovieListAdapter(requireContext(), MovieListAdapter.NOW_PLAYING, this)

        mainListLayout = view.main_layout
        mainShimmerLayout = view.shimmer_layout_rv
        popularLayout = view.layout_popular
        topRatedLayout = view.layout_top_rated
        nowPlayingLayout = view.layout_now_playing
        upComingLayout = view.layout_upcoming

        ViewModelProvider(requireActivity()).get(MovieViewModel::class.java).also {
            movieViewModel = it
        }

        setRecyclerView()
        setOnClickListener()
        return view
    }

    private fun setOnClickListener() {
        val intent = Intent(activity, AllMoviesActivity::class.java)
        nowPlayingLayout.tv_view_all.setOnClickListener {
            intent.apply {
                putExtra(SELECTED_TYPE, NOW_PLAYING)
                putExtra(SELECTED_TITLE, getString(R.string.now_playing_movies))
                startActivity(this)
            }
        }
        topRatedLayout.tv_view_all.setOnClickListener {
            intent.apply {
                putExtra(SELECTED_TYPE, TOP_RATED)
                putExtra(SELECTED_TITLE, getString(R.string.top_rated_movies))
                startActivity(this)
            }
        }
        popularLayout.tv_view_all.setOnClickListener {
            intent.apply {
                putExtra(SELECTED_TYPE, POPULAR)
                putExtra(SELECTED_TITLE, getString(R.string.popular_movies))
                startActivity(this)
            }
        }
        upComingLayout.tv_view_all.setOnClickListener {
            intent.apply {
                putExtra(SELECTED_TYPE, UPCOMING)
                putExtra(SELECTED_TITLE, getString(R.string.upcoming_movies))
                startActivity(this)
            }
        }
    }


    private fun setRecyclerView() {
        popularLayout.tv_title.text = getString(R.string.popular_movies)
        topRatedLayout.tv_title.text = getString(R.string.top_rated_movies)
        upComingLayout.tv_title.text = getString(R.string.upcoming_movies)
        nowPlayingLayout.tv_title.text = getString(R.string.now_playing_movies)

        val popularRecyclerView = popularLayout.recycler_view
        val topRatedRecyclerView = topRatedLayout.recycler_view
        val nowPlayingRecyclerView = nowPlayingLayout.recycler_view
        val upComingRecyclerView = upComingLayout.recycler_view

        LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
            popularLayout.recycler_view.layoutManager = this
        }

        topRatedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        nowPlayingRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        upComingRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        popularRecyclerView.adapter = popularAdapter
        topRatedRecyclerView.adapter = topRatedAdapter
        nowPlayingRecyclerView.adapter = nowPlayingAdapter
        upComingRecyclerView.adapter = upComingAdapter
        addObservers()

    }

    private fun addObservers() {
        movieViewModel.popularMovies.observe(requireActivity()) {
            handleChange(it, popularAdapter)
        }
        movieViewModel.topRatedMovies.observe(requireActivity()) {
            handleChange(it, topRatedAdapter)
        }
        movieViewModel.nowPlaying.observe(requireActivity()) {
            handleChange(it, nowPlayingAdapter)
        }
        movieViewModel.upComingMovies.observe(requireActivity()) {
            handleChange(it, upComingAdapter)
        }
    }

    private fun handleChange(resource: Resource<MovieListResponse>, adapter: MovieListAdapter) {
        when (resource) {
            is Resource.Success -> {
                setProgressBarStatus(adapter.type, false)
                resource.data?.let { it1 -> adapter.setMoviesList(it1.results) }
            }
            is Resource.Error -> {
                Toast.makeText(
                    requireContext(),
                    resource.error.status_message,
                    Toast.LENGTH_LONG
                ).show()
                setProgressBarStatus(adapter.type, true)
            }
            is Resource.Loading -> {
                setProgressBarStatus(adapter.type, true)
            }
        }

    }


    private fun setProgressBarStatus(type: Int, state: Boolean) {

        when (type) {

            MovieListAdapter.NOW_PLAYING -> handleShimmer(
                state,
                nowPlayingLayout
            )
            MovieListAdapter.TOP_RATED -> handleShimmer(
                state,
                topRatedLayout
            )
            MovieListAdapter.POPULAR -> handleShimmer(
                state,
                popularLayout
            )
            MovieListAdapter.UPCOMING -> handleShimmer(
                state,
                upComingLayout
            )
        }

    }

    private fun handleShimmer(
        state: Boolean,
        recyclerView: View
    ) {
        if (state)
            mainShimmerLayout.startShimmer()
        else {
            mainShimmerLayout.stopShimmer()
            mainShimmerLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onClick(movie: Movie) {
        startActivity(Intent(activity, MovieDetailActivity::class.java).apply {
            putExtra(MOVIE_ID, movie.id)
        })
    }
}