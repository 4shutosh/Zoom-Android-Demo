package com.poc.zoom.utils

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation

object AnimationHelper {

    fun View.enterToRight() {
        visibility = View.VISIBLE
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
        )
        animation.duration = 150
        animation.interpolator = AccelerateInterpolator()
        this.startAnimation(animation)
    }

    fun View.exitToLeft() {
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
        )
        animation.duration = 150
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        this.startAnimation(animation)
    }
}