package com.thaagam.field_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import com.thaagam.field_app.Permissions.PermissionHandler
import com.thaagam.field_app.databinding.ActivityTakeVideoBinding
import com.thaagam.field_app.databinding.CameraPreviewBinding

class TakeVideoActivity : BaseActivity() {

  private lateinit var takeVideoBinding: ActivityTakeVideoBinding
  private lateinit var cameraPreviewBinding: CameraPreviewBinding

  //PERMISSIONS
  private val permissions = arrayOf(
    PermissionHandler.CAMERA_PERMISSION,
    PermissionHandler.WRITE_EXTERNAL_PERMISSION,
    PermissionHandler.LOCATION_PERMISSION
  )

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    permissionHandler.handlePermissionResult(requestCode, permissions, grantResults, object  : PermissionHandler.PermissionCallback{
      override fun onPermissionGranted(requestCode: Int) {
        when(requestCode){
          PermissionHandler.REQUEST_ALL_PERMISSIONS ->{
            startCamera()
          }
        }
      }
    })
  }

  override fun onStart() {
    super.onStart()
    permissionHandler.checkAndRequestPermission(permissions, PermissionHandler.REQUEST_ALL_PERMISSIONS, object : PermissionHandler.PermissionCallback{
      override fun onPermissionGranted(requestCode: Int) {
       startCamera()
      }
    })
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    takeVideoBinding = ActivityTakeVideoBinding.inflate(layoutInflater)
    setContentView(takeVideoBinding.root)

    cameraPreviewBinding = CameraPreviewBinding.bind(takeVideoBinding.cameraPreviewInclude.root)

    takeVideoBinding.captureBtn.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        soundUtil.soundEffect(R.raw.camera_click)
        blinkScreenUtil.blinkScreen()
      }
      false
    }
    takeVideoBinding.swipeRefreshLayout.setOnRefreshListener {
      cameraUtil.refreshPage(takeVideoBinding.qrResultTxt, takeVideoBinding.qrLottie,
        takeVideoBinding.swipeRefreshLayout)
    }

    takeVideoBinding.menuBtn.setOnClickListener{
      val intent = Intent(this, NoQRActivity::class.java)
      startActivity(intent)
    }


    takeVideoBinding.flash.setOnCheckedChangeListener { _, isChecked ->
      flashlightUtil.toggleFlashlight(cameraUtil.cameraControl(), isChecked)
    }
  }

  private fun startCamera(){
    cameraUtil.startCamera(this, cameraPreviewBinding.cameraPreview, true,
      enableQrCodeScanning = true,
      qrResultView = takeVideoBinding
        .qrResultTxt,
      qrLottie = takeVideoBinding.qrLottie
    )
  }
}