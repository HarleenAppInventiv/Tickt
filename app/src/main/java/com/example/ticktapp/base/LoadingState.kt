package com.example.ticktapp.base

sealed class LoadingState {
    class LOADING(var type: Int = LoaderType.NORMAL, var msg: String = "") : LoadingState()
    class LOADED(var type: Int = LoaderType.NORMAL, var msg: String = "") : LoadingState()
}

object LoaderType{
    const val NORMAL = 1
    const val PROGRESS_TOP = 2
}