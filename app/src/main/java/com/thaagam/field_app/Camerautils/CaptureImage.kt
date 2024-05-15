package com.thaagam.field_app.Camerautils

import android.content.Context
import android.os.SystemClock
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.thaagam.field_app.Utilities.CommonUtil
import com.thaagam.field_app.Utilities.PathHandler
import java.io.File

class CaptureImage(private val context: Context) {


  //OBJECTS
  private val cameraUtil = CameraUtil(context)
  private val commonUtil = CommonUtil(context)
  private val pathHandler = PathHandler(context)
  //VARIABLES
  private var qrCodeResult: String? = null
  private var lastClickTime: Long = 0
  private var lastImageCaptureTime: Long = 0
  private var imageCapture: ImageCapture? = null

  fun captureImage() {
    if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
      return
    }
    lastClickTime = SystemClock.elapsedRealtime()
    lastImageCaptureTime = SystemClock.elapsedRealtime()
    if (qrCodeResult == null) {
      commonUtil.toast("Please Scan a QR Code")
      return
    }
    imageCapture = cameraUtil.getImageCapture1()
    imageCapture?.let { capture ->
      val fileName : String
      var imageFile : File
      val outputDirectory = pathHandler.getOutputDirectory1(qrCodeResult)
      var counter = 1
      imageFile = File(outputDirectory, "$qrCodeResult.jpg")
      if(imageFile.exists()){
        while(imageFile.exists()){
          imageFile = File(outputDirectory, "$qrCodeResult($counter).jpg")
          counter++
        }
      }else{
        counter = 0
      }
      val outputFile = imageFile
      var qrCodeResult = if(counter == 0) qrCodeResult else "$qrCodeResult$counter"
      val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

      capture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
          override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            commonUtil.toast("captured")
          }
          override fun onError(exception: ImageCaptureException) {
            commonUtil.toast("Capture failed: ${exception.message}")
          }
        }
      )
    }

  }
  fun getLastImageCaptureTime(): Long {
    return lastImageCaptureTime
  }


}