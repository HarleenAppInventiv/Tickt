package com.example.ticktapp.snackbar

import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Created by kurt on 2015/06/08.
 */
object AnimationUtils {
    val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()

    @JvmField
    val FAST_OUT_SLOW_IN_INTERPOLATOR: Interpolator =
        FastOutSlowInInterpolator()
    val DECELERATE_INTERPOLATOR: Interpolator = DecelerateInterpolator()
    fun lerp(
        startValue: Float,
        endValue: Float,
        fraction: Float
    ): Float {
        return startValue + fraction * (endValue - startValue)
    }

    fun lerp(startValue: Int, endValue: Int, fraction: Float): Int {
        return startValue + Math.round(fraction * (endValue - startValue).toFloat())
    }

    class AnimationListenerAdapter : AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
    }
}