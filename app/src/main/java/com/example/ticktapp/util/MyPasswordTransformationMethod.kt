package com.example.ticktapp.util

import android.text.method.PasswordTransformationMethod
import android.view.View

class MyPasswordTransformationMethod : PasswordTransformationMethod() {
    private val BIGGER_DOT = '\u2B24'
    private val SMALLER_DOT = '\u2022'
    private val HEAVY_ASTERICS = '\u2731'
    private val MEDIUM_ASTERISKS = '\u2733'
    private val LIGHT_ASTERISKS = '\u2734'
    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        return PasswordCharSequence(source)
    }

    inner class PasswordCharSequence(source: CharSequence?) : CharSequence {
        private var mSource = source

        override val length: Int
            get() = mSource!!.length

        override fun get(index: Int): Char {
            return BIGGER_DOT
        }


        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return mSource!!.subSequence(startIndex, endIndex)
        }

    }
}