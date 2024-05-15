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
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import com.thaagam.field_app.Camerautils.CameraUtil
import com.thaagam.field_app.Permissions.PermissionHandler
import com.thaagam.field_app.Utilities.BlinkScreenUtil
import com.thaagam.field_app.Utilities.SoundUtil

class NoQRActivity : BaseActivity(){

  //UI ELEMENTS
  private lateinit var captureBtn : ImageButton

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
  @SuppressLint("ClickableViewAccessibility")
  private fun settingUI(){
    cameraPreview = findViewById(R.id.cameraPreview)
    flash = findViewById(R.id.flash_btn)
    captureBtn = findViewById(R.id.capture_btn)

    captureBtn.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        soundUtil.soundEffect(R.raw.camera_click)
        blinkScreenUtil.blinkScreen()
      }
      false
    }
  }
}