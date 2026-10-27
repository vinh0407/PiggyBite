package com.money.app.util

import android.animation.ValueAnimator
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils as AndroidAnimUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.money.app.R

/**
 * Utility helper providing smooth animations, spring scale touch feedback,
 * count-up balance animations, and fade-slide entrance transitions.
 */
object AnimationUtils {

    /**
     * Applies a spring scale bounce effect when a view is touched/clicked.
     */
    fun applyBounceEffect(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .scaleX(0.93f)
                        .scaleY(0.93f)
                        .setDuration(120)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(150)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }
            }
            false
        }
    }

    /**
     * Animates counting a number up or down on a TextView.
     */
    fun animateNumberCount(
        textView: TextView,
        targetValue: Double,
        context: Context,
        durationMs: Long = 700
    ) {
        val animator = ValueAnimator.ofFloat(0f, targetValue.toFloat())
        animator.duration = durationMs
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { anim ->
            val value = (anim.animatedValue as Float).toDouble()
            textView.text = AppUtils.formatCurrency(value, context)
        }
        animator.start()
    }

    /**
     * Performs a smooth fade-in and slide-up transition on a View.
     */
    fun fadeInSlideUp(view: View, delayMs: Long = 0) {
        view.alpha = 0f
        view.translationY = 40f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delayMs)
            .setDuration(400)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
}
