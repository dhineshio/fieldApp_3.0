package com.thaagam.field_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import com.thaagam.field_app.Camerautils.CaptureImage
import com.thaagam.field_app.Permissions.PermissionHandler
import com.thaagam.field_app.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

  private val captureImage: CaptureImage by lazy {
    CaptureImage(this)
  }

  private lateinit var mainBinding: ActivityMainBinding

  private val permissions = arrayOf(
    PermissionHandler.CAMERA_PERMISSION,
    PermissionHandler.WRITE_EXTERNAL_PERMISSION,
    PermissionHandler.LOCATION_PERMISSION
  )

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
              startCamera()
            }
          }
        }
      })
  }

  override fun onStart() {
    super.onStart()
    permissionHandler.checkAndRequestPermission(
      permissions,
      PermissionHandler.REQUEST_ALL_PERMISSIONS,
      object : PermissionHandler.PermissionCallback {
        override fun onPermissionGranted(requestCode: Int) {
          startCamera()
        }
      })
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(mainBinding.root)

    mainBinding.swipeRefreshLayout.setOnRefreshListener {
      cameraUtil.refreshPage(
        mainBinding.qrResultTxt,
        mainBinding.qrLottie,
        mainBinding.swipeRefreshLayout
      )
    }
    mainBinding.captureBtn.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        soundUtil.soundEffect(R.raw.camera_click)
        blinkScreenUtil.blinkScreen()
      }
      false
    }
    mainBinding.menuBtn.setOnClickListener {
      val intent = Intent(this, TakeVideoActivity::class.java)
      startActivity(intent)
    }
  }

  private fun startCamera() {
    cameraUtil.startCamera(
      this@MainActivity,
      mainBinding.cameraPreview,
      true,
      enableQrCodeScanning = true,
      mainBinding.qrResultTxt,
      mainBinding.qrLottie,
    )
  }
}