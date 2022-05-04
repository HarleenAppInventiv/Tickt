package com.app.core.coroutines

import kotlinx.coroutines.*

/*
* this class is used to provide the coroutines threads work in background as well as main threads
* */
object CoroutinesBase {

    fun main(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }

    fun async(work: suspend (() -> Any))=CoroutineScope(Dispatchers.Main).async {
        work()
    }

    fun io(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.IO).launch {
            work()
        }

    fun default(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Default).launch {
            work()
        }

    fun unconfined(work: suspend () -> Unit) =
        CoroutineScope(Dispatchers.Unconfined).launch {

        }


}