package com.thaagam.field_app.Utilities

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat

import com.thaagam.field_app.R

class AnimationUtil(private val context: Context) {
  private var scaleXAnimator: ObjectAnimator? = null
  private var scaleYAnimator: ObjectAnimator? = null

  fun startScalingAnimation(qrLottie: ImageView) {
    // Define the scaling animation
    qrLottie.visibility = View.VISIBLE
    qrLottie.backgroundTintList = ColorStateList.valueOf(
      ContextCompat.getColor(context, R.color.button_danger))
    scaleXAnimator = ObjectAnimator.ofFloat(qrLottie, View.SCALE_X, 1.0f, 1.2f, 1.0f)
    scaleXAnimator?.repeatCount = ObjectAnimator.INFINITE // Repeat indefinitely
    scaleXAnimator?.repeatMode = ObjectAnimator.RESTART // Restart from the beginning when finished
    scaleXAnimator?.duration = 1000 // Animation duration in milliseconds
    scaleXAnimator?.interpolator =
      AccelerateDecelerateInterpolator() // Smooth acceleration and deceleration

    scaleYAnimator = ObjectAnimator.ofFloat(qrLottie, View.SCALE_Y, 1.0f, 1.2f, 1.0f)
    scaleYAnimator?.repeatCount = ObjectAnimator.INFINITE
    scaleYAnimator?.repeatMode = ObjectAnimator.RESTART
    scaleYAnimator?.duration = 1000
    scaleYAnimator?.interpolator = AccelerateDecelerateInterpolator()

    // Start the animation
    scaleXAnimator?.start()
    scaleYAnimator?.start()
  }

  fun stopScalingAnimation(qrLottie: ImageView) {
    // Cancel the ongoing animations
    scaleXAnimator?.cancel()
    scaleYAnimator?.cancel()
    // Reset the scale to 1.0
    qrLottie.scaleX = 1.0f
    qrLottie.scaleY = 1.0f
    qrLottie.backgroundTintList = ColorStateList.valueOf(
      ContextCompat.getColor(context, R.color.button_success))

    Handler(Looper.getMainLooper()).postDelayed({
      qrLottie.visibility = View.INVISIBLE
    }, 1000) // 3000 milliseconds = 3 seconds
  }


}