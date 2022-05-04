package com.example.ticktapp.interfaces

import android.view.View

/*
* This class used to listing every click or any other event from the RecyclerViewAdapter class
* */
interface RecyclerCallback {
    fun onClick(position: Int, view: View?)
}