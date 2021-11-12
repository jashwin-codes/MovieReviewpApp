package com.example.moviereviewapp.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moviereviewapp.R
import com.example.moviereviewapp.extensions.hideActionBar
import com.example.moviereviewapp.ui.fragments.FavoriteFragment
import com.example.moviereviewapp.ui.fragments.HomeFragment
import com.example.moviereviewapp.ui.fragments.SearchFragment
import com.example.moviereviewapp.ui.fragments.WatchListFragment
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView




class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setNavigationListener()
        setCurrentFragment(HomeFragment())

    }


    private fun setNavigationListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    setCurrentFragment(HomeFragment())
                }
                R.id.favorite -> {
                    setCurrentFragment(FavoriteFragment())
                }
                R.id.search -> {
                    setCurrentFragment(SearchFragment())
                }
                R.id.watch_list -> {
                    setCurrentFragment(WatchListFragment())
                }
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment).commit()
        }
    }
//
//    fun setSize() {
//        // Here the index: 2 at 'getChildAt(2)' means the middle icon
//// Here the index: 2 at 'getChildAt(2)' means the middle icon
//        val navigationItemView =
//            bottomNavigationView.getChildAt(0).getChildAt(2) as BottomNavigationItemView
//
//        val displayMetrics = resources.displayMetrics
//
//        navigationItemView.setIconSize(
//            TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, 40f,
//                displayMetrics
//            ).toInt()
//        )
//    }
}