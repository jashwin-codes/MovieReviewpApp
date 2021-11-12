package com.example.moviereviewapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviereviewapp.R
import com.example.moviereviewapp.extensions.loadImage
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.model.MovieFullDetail
import com.example.moviereviewapp.ui.adapter.CastAdapter
import com.example.moviereviewapp.ui.adapter.MovieListAdapter
import com.example.moviereviewapp.ui.adapter.ProductionCompanyAdapter
import com.example.moviereviewapp.ui.fragments.HomeFragment.Companion.MOVIE_ID
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.utils.Constants
import com.example.moviereviewapp.utils.Resource
import com.example.moviereviewapp.utils.SessionManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.cast_layout.*
import kotlinx.android.synthetic.main.production_company_layout.*
import kotlinx.android.synthetic.main.similar_movies_layout.*


class MovieDetailActivity : AppCompatActivity(), MovieListAdapter.MovieOnClickListener {
    lateinit var movieViewModel: MovieViewModel
    lateinit var movie: MovieFullDetail


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
//orientaion chnges
        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        setObserver()
        val id = intent.getIntExtra(MOVIE_ID, 0)

        val sessionId = SessionManager(this).fetchAuthToken()
        movieViewModel.getMovieDetail(id, sessionId!!)
    }

    private fun setObserver() {
        movieViewModel.movieDetails.observe(this) {
            when (it) {
                is Resource.Success -> {
                    progress_circular_movie_detail.visibility = View.GONE
                    movie = it.data!!
                    title = movie.title
                    setUpActivity()
                }
                is Resource.Loading -> {
                    progress_circular_movie_detail.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    progress_circular_movie_detail.visibility = View.GONE
                    Toast.makeText(
                        this,
                        it.error.status_message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setUpActivity() {
        loadPoster()
        setGenreGroup()
        initializeTrailer()
        setCastRv()
        setSimilarMoviesRv()
        setProductionCompanyRv()
    }

    private fun setProductionCompanyRv() {
        LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false).apply {
            rv_production_company.layoutManager = this
        }
        rv_production_company.adapter = ProductionCompanyAdapter(this,movie.production_companies)
    }

    private fun setSimilarMoviesRv() {
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false).apply {
            rv_similar_movies.layoutManager = this
        }
        rv_similar_movies.adapter = MovieListAdapter(context = this, clickListener = this).apply {
            setMoviesList(movie.recommendations.results)
        }

    }

    private fun setCastRv() {
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false).apply {
            rv_cast_layout.layoutManager = this
        }
        rv_cast_layout.adapter = CastAdapter(this, movie.credits.cast)
        var director = "-"
        var writer = "-"

        movie.credits.crew.forEach {
            if (it.job == "Director")
                director = it.name
            if (it.department == "Writing")
                writer = it.name
            if (!(director == "-" || writer == "-"))
                return@forEach
        }
        tv_director.text = director
        tv_writer.text = writer
    }


    private fun setGenreGroup() {
        val generes = movie.genres
        for (i in generes)
            Chip(this).apply {
                id = i.id
                tag = i.name
                text = i.name
                isCheckable = true
                isCheckedIconVisible = false
                cg_geners.addView(this)
            }
        cg_geners.invalidate()

        cg_geners.setOnCheckedChangeListener { group: ChipGroup, id: Int ->
            val chip = group.findViewById<Chip>(id)

        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadPoster() {
        val url = Constants.IMAGE_BASE_URL + movie.poster_path
        loadImage(url, iv_poster, R.drawable.ic_default_profile)

        tv_rating.text = "${movie.vote_average} / 10"
        tv_overview.text = movie.overview
        tv_duration_year.text = "${movie.runtime} min \t\t\t\t ${movie.release_date.slice(0..3)}"
    }

    private fun initializeTrailer() {
        val videos = movie.videos.results
        if (videos.isNotEmpty()) {
            val result = videos.filter {
                it.site == "YouTube" && it.type == "Trailer"
            }
            if (result.isNotEmpty()) {
                this.lifecycle.addObserver(yt_trailer as LifecycleObserver)
                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videos[0].key, 0F)
                    }
                }
                yt_trailer.addYouTubePlayerListener(listener)
                return
            }

        }
        yt_trailer.visibility = View.GONE
        iv_backdrop.visibility = View.VISIBLE
        val backdropUrl = Constants.IMAGE_BASE_URL + movie.backdrop_path
        loadImage(backdropUrl, iv_backdrop, R.drawable.ic_default_profile)

    }

    override fun onClick(movie: Movie) {
        startActivity(Intent(this, MovieDetailActivity::class.java).apply {
            putExtra(MOVIE_ID, movie.id)
        })
    }
}