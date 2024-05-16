package com.thaagam.field_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.PreviewView
import com.thaagam.field_app.Permissions.PermissionHandler
import com.thaagam.field_app.databinding.ActivityMainBinding

class NoQRActivity : BaseActivity() {

  private lateinit var noQrBinding : ActivityMainBinding

  private val permissions = arrayOf(
    PermissionHandler.CAMERA_PERMISSION,
    PermissionHandler.WRITE_EXTERNAL_PERMISSION,
    PermissionHandler.LOCATION_PERMISSION
  )

  override fun onStart() {
    super.onStart()
    permissionHandler.checkAndRequestPermission(permissions, PermissionHandler
      .REQUEST_ALL_PERMISSIONS, object : PermissionHandler.PermissionCallback {
      override fun onPermissionGranted(requestCode: Int) {
        cameraUtil.startCamera(this@NoQRActivity, noQrBinding.cameraPreview, true, false)
      }
    })
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray,
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    permissionHandler.handlePermissionResult(
      requestCode,
      permissions,
      grantResults,
      object : PermissionHandler.PermissionCallback {
        override fun onPermissionGranted(requestCode: Int) {
          when (requestCode) {
            PermissionHandler.REQUEST_ALL_PERMISSIONS -> {
              cameraUtil.startCamera(this@NoQRActivity, noQrBinding.cameraPreview, true, false)
            }
          }
        }
      })
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    noQrBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(noQrBinding.root)
    noQrBinding.qrLottie.visibility = View.INVISIBLE
    noQrBinding.qrResultTxt.visibility = View.INVISIBLE
    noQrBinding.swipeRefreshLayout.setOnRefreshListener {
      noQrBinding.swipeRefreshLayout.isRefreshing = false
    }


    noQrBinding.captureBtn.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        soundUtil.soundEffect(R.raw.camera_click)
        blinkScreenUtil.blinkScreen()
      }
      false
    }

  }

}