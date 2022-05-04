package com.example.ticktapp.paging

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EndlessRecyclerViewScrollListenerImplementation(
    layoutManager: LinearLayoutManager?,
    private val listener: OnScrollPageChangeListener
) :
    EndlessRecyclerViewScrollListener(layoutManager!!) {
    override fun onLoadMore(
        page: Int,
        totalItemsCount: Int,
        view: RecyclerView?
    ) {
        listener.onLoadMore(page, totalItemsCount, view)
    }

    interface OnScrollPageChangeListener {
        fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
    }

}
