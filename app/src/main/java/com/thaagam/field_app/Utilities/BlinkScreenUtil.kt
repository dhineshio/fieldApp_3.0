package com.thaagam.field_app.Utilities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup

class BlinkScreenUtil(private val activity: Activity) {
  fun blinkScreen() {
	val overlay = View(activity)
	val rootLayout: ViewGroup = activity.findViewById(android.R.id.content)
	val animatorAdapter = object : AnimatorListenerAdapter() {
	  override fun onAnimationEnd(animation: Animator) {
		overlay.setBackgroundColor(Color.alpha(0))
		rootLayout.removeView(overlay)
	  }
	}
	// Create an overlay view
	overlay.setBackgroundColor(Color.BLACK)
	overlay.alpha = 1f

	// Add the overlay view to the root layout
	rootLayout.addView(
	  overlay,
	  ViewGroup.LayoutParams.MATCH_PARENT,
	  ViewGroup.LayoutParams.MATCH_PARENT
	)

	// Create an alpha animation
	val alphaAnimation = ObjectAnimator.ofFloat(overlay, "alpha", 0f)
	alphaAnimation.duration = 600 // duration of the animation in milliseconds

	// Remove the overlay view when the animation is done
	alphaAnimation.addListener(animatorAdapter)

	// Start the animation
	alphaAnimation.start()
  }
}
