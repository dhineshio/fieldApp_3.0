package com.thaagam.field_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.PreviewView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thaagam.field_app.Permissions.PermissionHandler

class TakeVideoActivity : BaseActivity() {

  //UI ELEMENTS
  private lateinit var cameraPreview : PreviewView
  private lateinit var captureBtn : ImageButton
  private lateinit var qrLottie : ImageView
  private lateinit var qrResultView: TextView
  private lateinit var swipeRefreshLayout: SwipeRefreshLayout
  private lateinit var menuBtn: ImageButton

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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(R.layout.activity_take_video)

    initUI()

  }

  @SuppressLint("ClickableViewAccessibility")
  private fun initUI(){
    cameraPreview = findViewById(R.id.cameraPreview)
    captureBtn = findViewById(R.id.capture_btn)
    qrResultView = findViewById(R.id.qr_result_txt)
    qrLottie = findViewById(R.id.qr_lottie)
    swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
    menuBtn = findViewById(R.id.menu_btn)

    captureBtn.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        soundUtil.soundEffect(R.raw.camera_click)
        blinkScreenUtil.blinkScreen()
      }
      false
    }
    swipeRefreshLayout.setOnRefreshListener {
      cameraUtil.refreshPage(qrResultView, qrLottie, swipeRefreshLayout)
    }

    menuBtn.setOnClickListener{
      val intent = Intent(this, NoQRActivity::class.java)
      startActivity(intent)
    }
  }

  private fun startCamera(){
    cameraUtil.startCamera(this, cameraPreview, true, true, qrResultView, qrLottie)
  }
}