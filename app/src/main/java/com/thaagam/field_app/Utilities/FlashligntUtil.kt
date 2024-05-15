package com.thaagam.field_app.Utilities

import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfoUnavailableException
import com.google.android.datatransport.BuildConfig

class FlashlightUtil(private val context: Context) {

  //OBJECTS
  private val commonUtil = CommonUtil(context)

  //VARIABLES
  private var isFlashlightOn = false

  //LATE INIT VARIABLES
  private lateinit var cameraControl: CameraControl
  fun toggleFlashlight(cameraControl: CameraControl, isChecked : Boolean) {
	this.cameraControl = cameraControl
	try {
	  val hasFlash =
		context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
	  if (hasFlash) {
		val newTorchState = !isFlashlightOn
		cameraControl.enableTorch(newTorchState)
		isFlashlightOn = isChecked
	  } else {
		if (BuildConfig.DEBUG) {
		  commonUtil.toast("Flashlight is not available on this device")
		}
	  }
	} catch (e: CameraInfoUnavailableException) {
	  if (BuildConfig.DEBUG) {
		commonUtil.toast("Unable to access camera info")
	  }
	}
  }
}
