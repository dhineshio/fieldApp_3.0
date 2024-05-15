package com.thaagam.field_app

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import com.thaagam.field_app.Camerautils.CameraUtil
import com.thaagam.field_app.Permissions.PermissionHandler

class NoQRActivity : AppCompatActivity() {

  private val permissionHandler = PermissionHandler(this)
  private val cameraUtil = CameraUtil(this)

  private val permissions = arrayOf(
    PermissionHandler.CAMERA_PERMISSION,
    PermissionHandler.WRITE_EXTERNAL_PERMISSION,
    PermissionHandler.LOCATION_PERMISSION
  )

  override fun onStart() {
    super.onStart()
    cameraUtil.startCamera(this@NoQRActivity,cameraPreview, true, false)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    permissionHandler.handlePermissionResult(requestCode, permissions, grantResults, object : PermissionHandler.PermissionCallback{
      override fun onPermissionGranted(requestCode: Int) {
        when(requestCode){
          PermissionHandler.REQUEST_ALL_PERMISSIONS ->{
            cameraUtil.startCamera(this@NoQRActivity, cameraPreview, true, false)
          }
        }
      }
    })
  }

  private lateinit var qrLottie : ImageView
  private lateinit var qrResultView : TextView
  private lateinit var cameraPreview: PreviewView
  private lateinit var flash : ToggleButton


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    enableEdgeToEdge()
    qrLottie = findViewById(R.id.qr_lottie)
    qrResultView = findViewById(R.id.qr_result_txt)
    qrLottie.visibility = View.GONE
    qrResultView.visibility = View.GONE
    settingUI()
  }
  private fun settingUI(){
    cameraPreview = findViewById(R.id.cameraPreview)
    flash = findViewById(R.id.flash_btn)
  }
}