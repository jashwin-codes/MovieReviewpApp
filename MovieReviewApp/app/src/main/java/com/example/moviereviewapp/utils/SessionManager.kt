package com.example.moviereviewapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.moviereviewapp.R
import com.example.moviereviewapp.utils.Constants.USER_TOKEN

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)


    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }


    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
}