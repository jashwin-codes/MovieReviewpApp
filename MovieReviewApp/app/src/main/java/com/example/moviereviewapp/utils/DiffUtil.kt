package com.example.moviereviewapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.moviereviewapp.model.Movie

class MyDiffUtil(
    private var oldNotes : List<Movie>,
    private var newNotes : List<Movie>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return  oldNotes.size
    }

    override fun getNewListSize(): Int {
      return newNotes.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return oldNotes[oldItemPosition].id == newNotes[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNotes[oldItemPosition] === newNotes[newItemPosition]
    }
}