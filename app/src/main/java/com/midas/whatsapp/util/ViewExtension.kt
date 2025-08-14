package com.midas.whatsapp.util

import android.view.View
import androidx.transition.Visibility

object ViewExtension {

    fun View.gone(){
        this.visibility = View.GONE
    }

    fun View.show(){
        this.visibility = View.VISIBLE
    }
}