package com.thaagam.field_app.Camerautils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import android.util.Size
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.camera2.internal.Camera2CameraInfoImpl
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thaagam.field_app.R
import com.thaagam.field_app.Utilities.AnimationUtil
import com.thaagam.field_app.Utilities.CommonUtil
import com.thaagam.field_app.Utilities.FileUtil
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class CameraUtil(
  private val context: Context,
) {
  companion object {
    const val TAG = "CameraUtil"
  }

  //OBJECTS
  private val fileUtil = FileUtil()
  private val commonUtil = CommonUtil(context)
  private val animationUtil = AnimationUtil(context)

  //LATE INIT VARIABLES
  private lateinit var camera: Camera
  private lateinit var cameraControl: CameraControl

  //VARIABLES
  private var qrCodeResult: String? = null
  //LAZY VARIABLES
  private val imageCapture: ImageCapture by lazy {
    ImageCapture.Builder().setTargetResolution(getDesiredResolution(context)).build()
  }
  private val cameraExecutor by lazy {
    Executors.newSingleThreadExecutor()
  }
  private val cameraProviderFuture by lazy {
    ProcessCameraProvider.getInstance(context)
  }
  /**
   * Starts the camera preview with specified parameters.
   * @param preViewView The PreviewView where the camera preview will be displayed.
   * @param ultraWide Indicates whether to use an ultra-wide lens if available.
   * @param enableQrCodeScanning Indicates whether QR code scanning should be enabled.
   * @param tvQrCodeResult The TextView to display the result of QR code scanning.
   * @param qrCodeScannerView The ImageView used as a scanner overlay for QR code scanning.
   */
  @SuppressLint("ClickableViewAccessibility")
  fun startCamera(
    lifecycleOwner : LifecycleOwner,
    cameraPreview: PreviewView,
    ultraWide: Boolean,
    enableQrCodeScanning: Boolean,
    qrResultView: TextView? = null,
    qrLottie: ImageView? = null,
  ) {
    try {
      if (enableQrCodeScanning && qrResultView == null) {
        throw IllegalArgumentException("tvQrCodeResult cannot be null when enableQrCodeScanning is true")
      }
      cameraProviderFuture.addListener(
        {
          val cameraProvider = cameraProviderFuture.get()
          val cameraSelector = getCameraSelector(ultraWide)
          val preview = getPreview(cameraPreview)
          val imageAnalyzer = if (enableQrCodeScanning) getImageAnalyzer(
            qrResultView!!, qrLottie!!
          ) else null
          val useCases = mutableListOf<UseCase>().apply {
            add(preview)
            imageAnalyzer?.let { add(it) }
            add(imageCapture)
          }
          try {
            //UNBIND USE CASES BEFORE REBINDING
            cameraProvider.unbindAll()
            //BIND USE CASES TO CAMERA
            camera = cameraProvider.bindToLifecycle(
              lifecycleOwner, cameraSelector, *useCases.toTypedArray()
            )
            cameraControl = camera.cameraControl
            cameraControl.setZoomRatio(0.6f)
            val scaleGestureListener =
              object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                  val scaleFactor = detector.scaleFactor
                  val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 0.6f
                  val newZoomRatio = (currentZoomRatio * scaleFactor).coerceIn(
                    0.6f, camera.cameraInfo.zoomState.value?.maxZoomRatio ?: currentZoomRatio
                  )
                  cameraControl.setZoomRatio(newZoomRatio)
                  return true
                }
              }
            val scaleGestureDetector = ScaleGestureDetector(context, scaleGestureListener)
            cameraPreview.setOnTouchListener { _, event ->
              scaleGestureDetector.onTouchEvent(event)
              return@setOnTouchListener true
            }
          } catch (e: Exception) {
            Log.e("CameraUtil", "Error binding camera use cases", e)
          }
        }, ContextCompat.getMainExecutor(context)
      )
    } catch (e: Exception) {
      commonUtil.log(TAG, "Error starting camera : $e")
    }
  }

  //@return A CameraSelector instance based on the specified parameters.
  @SuppressLint("RestrictedApi")
  private fun getCameraSelector(ultraWide: Boolean): CameraSelector {
    return if (ultraWide) {
      CameraSelector.Builder().addCameraFilter { cameraInfo ->
        val backCameras = cameraInfo.filterIsInstance<Camera2CameraInfoImpl>().filter {
          it.lensFacing == CameraSelector.LENS_FACING_BACK
        }
        val wideLensCamera = backCameras.minByOrNull {
          val focalLengths =
            it.cameraCharacteristicsCompat.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
          focalLengths?.getOrNull(0) ?: Float.MAX_VALUE
        }
        if (wideLensCamera != null) {
          listOf(wideLensCamera)
        } else {
          backCameras
        }
      }.build()
    } else {
      CameraSelector.DEFAULT_BACK_CAMERA
    }
  }

  //@return A configured Preview instance.
  private fun getPreview(cameraPreview: PreviewView): Preview {
    return Preview.Builder().build().also {
      it.setSurfaceProvider(cameraPreview.surfaceProvider)
    }
  }

  //@return An ImageAnalysis instance configured for QR code scanning.
  private fun getImageAnalyzer(
    qrResultView: TextView,
    qrLottie: ImageView,
  ): ImageAnalysis {
    return ImageAnalysis.Builder().build().also {
      it.setAnalyzer(cameraExecutor, QrCodeAnalyzer({ qrCode, _ ->
        val qrNameOnly = extractNameFromQRCode(qrCode.rawValue!!)
        qrResultView.text = qrNameOnly
        qrCodeResult = fileUtil.convertToFilenameSafe(qrCode.rawValue!!)
        qrLottie.backgroundTintList = ColorStateList.valueOf(
          ContextCompat.getColor(context, R.color.button_success))
        qrLottie.animate().scaleX(.8f).scaleY(.8f).setDuration(200).start()
      }, {CaptureImage(context).getLastImageCaptureTime()}, qrResultView, qrLottie, context))
    }
  }

  //Refresh a page and reset an UI
  fun refreshPage(
    qrResultView: TextView, qrLottie: ImageView, swipeRefreshLayout: SwipeRefreshLayout,
  ) {
    qrResultView.text = ""
    qrResultView.animate().translationY(qrResultView.height.toFloat()*1.1f).setDuration(200).start()
    qrLottie.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
    qrLottie.backgroundTintList = ColorStateList.valueOf(
      ContextCompat.getColor(context, R.color.button_danger))
    qrLottie.visibility = View.VISIBLE
    swipeRefreshLayout.isRefreshing = false
  }


  //@return Extract a name from QR code.
  private fun extractNameFromQRCode(qrCode: String): String {
    val parts = qrCode.split("-").dropLast(5)
    return parts.joinToString("-")
  }

  /**
   * Calculates and returns the desired resolution for the camera preview.
   * @param context The application context.
   * @return The desired resolution as a Size object.
   */
  private fun getDesiredResolution(context: Context): Size {
    val ratio = 1.1f
    val displayMetric = context.resources.displayMetrics
    val screenWidthDp = displayMetric.widthPixels / displayMetric.density
    val screenHeightDp = displayMetric.heightPixels / displayMetric.density
    val desireWidthDp = screenWidthDp * ratio
    val desiredHeightDp = screenHeightDp * ratio
    val desiredWidthPx = (desireWidthDp * displayMetric.density).toInt()
    val desiredHeightPx = (desiredHeightDp * displayMetric.density).toInt()
    return Size(desiredWidthPx, desiredHeightPx)
  }

  fun cameraControl(): CameraControl {
    return cameraControl
  }

  fun cameraExecutor(): ExecutorService {
    return cameraExecutor
  }

  fun getQrCodeResult(): String? {
    return qrCodeResult
  }
  fun getImageCapture1(): ImageCapture {
    return imageCapture
  }

}





