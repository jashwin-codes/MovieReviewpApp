package com.example.moviereviewapp.ui.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class AppEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    init {
        applyFont()

    }

    private fun applyFont() {

        val typeFace = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")
        typeface = typeFace
    }
}