package com.thaagam.field_app.Camerautils

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.thaagam.field_app.R
import com.thaagam.field_app.Utilities.AnimationUtil

class QrCodeAnalyzer(
  private val onQrCodeDetected: (qrCode: Barcode, imageProxy: ImageProxy) -> Unit,
  private val lastImageCaptureTime: () -> Long,
  private val qrResultView: TextView,
  private val qrLottie : ImageView,
  private val context : Context
) : ImageAnalysis.Analyzer {

  // Flag to indicate if animation is currently in progress
  private var isAnimating = false
  @SuppressLint("UnsafeOptInUsageError")
  override fun analyze(imageProxy: ImageProxy) {
    if (shouldProcessImage(imageProxy)) {
      val inputImage = getInputImage(imageProxy)
      processImage(inputImage, imageProxy)
    }
  }

  @ExperimentalGetImage
  private fun shouldProcessImage(imageProxy: ImageProxy): Boolean {
    val timeDiff = System.currentTimeMillis() - lastImageCaptureTime()
    return timeDiff >= 1800 && imageProxy.image != null
  }

  @ExperimentalGetImage
  private fun getInputImage(imageProxy: ImageProxy): InputImage {
    val mediaImage = imageProxy.image
    return InputImage.fromMediaImage(mediaImage!!, imageProxy.imageInfo.rotationDegrees)
  }

  private fun processImage(inputImage: InputImage, imageProxy: ImageProxy) {
    BarcodeScanning.getClient().process(inputImage)
      .addOnSuccessListener { barcodes ->
        if (barcodes.isNotEmpty()) {
          for (barcode in barcodes) {
            if (barcode.valueType == Barcode.TYPE_TEXT) {
              // Trigger animation if not already animating
              if (!isAnimating) {
                isAnimating = true
                animateQRResultView(qrResultView, qrLottie)
              }
              onQrCodeDetected(barcode, imageProxy)
              break
            }
          }
        }
      }
      .addOnFailureListener { e ->
        Log.e("QRCodeAnalyzer", "Barcode detection failed : ${e.message}")
      }
      .addOnCompleteListener {
          imageProxy.close()
      }
  }

  private fun animateQRResultView(qrResultView: TextView, qrLottie : ImageView) {
    qrResultView.animate().translationY(0f).setDuration(200).setListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator) {
      }

      override fun onAnimationEnd(animation: Animator) {
        isAnimating = false
      }

      override fun onAnimationCancel(animation: Animator) {
        isAnimating = false
      }

      override fun onAnimationRepeat(animation: Animator) {
        TODO("Not yet implemented")
      }
    })
  }
}