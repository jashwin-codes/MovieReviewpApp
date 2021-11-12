package com.example.moviereviewapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.ui.view.AppTextView
import com.example.moviereviewapp.utils.Constants
import com.example.moviereviewapp.utils.MyDiffUtil

class AllMovieListAdapter(val context: Context) : RecyclerView.Adapter<AllMovieListAdapter.ViewHolder>() {
    var oldMovies : List<Movie> = listOf()
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val poster: ImageView = view.findViewById<ImageView>(R.id.poster_av)
        val title: AppTextView = view.findViewById<AppTextView>(R.id.tv_title_av)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_holder_allmovies,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = oldMovies[position]
        holder.title.text = movie.title
        val url = Constants.IMAGE_BASE_URL+movie.poster_path
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_default_movie)
            .into(holder.poster)
    }

    override fun getItemCount()= oldMovies.size
    fun setMoviesList(newList : List<Movie>) {
        val myDiff = MyDiffUtil(oldMovies,newList)
        val diffResult = DiffUtil.calculateDiff(myDiff)
        oldMovies = newList
        diffResult.dispatchUpdatesTo(this)
    }
}
