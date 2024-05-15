package com.thaagam.field_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thaagam.field_app.Camerautils.CameraUtil
import com.thaagam.field_app.Camerautils.CaptureImage
import com.thaagam.field_app.Permissions.PermissionHandler
import com.thaagam.field_app.Utilities.BlinkScreenUtil
import com.thaagam.field_app.Utilities.SoundUtil

class MainActivity : AppCompatActivity() {

  //OBJECT INITIALIZATION
  private val permissionHandler: PermissionHandler = PermissionHandler(this)
  private val cameraUtil = CameraUtil(this)
  private val soundUtil =SoundUtil(this, cameraUtil.cameraExecutor())
  private val blankScreenUtil = BlinkScreenUtil(this)
  private val captureImage: CaptureImage = CaptureImage(this)

  //UI INITIALIZATION
  private lateinit var cameraPreview: PreviewView
  private lateinit var menuBtn: ImageButton
  private lateinit var flashBtn: ToggleButton
  private lateinit var captureBtn: ImageButton
  private lateinit var qrResultView: TextView
  private lateinit var galleryBtn: ImageButton
  private lateinit var qrLottie: ImageView
  private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(R.layout.activity_main)
    initUi()
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun initUi() {
    cameraPreview = findViewById(R.id.cameraPreview)
    qrLottie = findViewById(R.id.qr_lottie)
    qrResultView = findViewById(R.id.qr_result_txt)
    menuBtn = findViewById(R.id.menu_btn)
    flashBtn = findViewById(R.id.flash_btn)
    captureBtn = findViewById(R.id.capture_btn)
    galleryBtn = findViewById(R.id.gallery_btn)
    swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)


    swipeRefreshLayout.setOnRefreshListener {
      cameraUtil.refreshPage(qrResultView, qrLottie, swipeRefreshLayout)
    }
    captureBtn.setOnClickListener {
      captureImage.captureImage()
    }
    captureBtn.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        // The button is pressed
        soundUtil.soundEffect(R.raw.camera_click)
        blankScreenUtil.blinkScreen()
      }
      false // Return false to indicate that we haven't consumed the event
    }
  }
  private fun startCamera() {
    cameraUtil.startCamera(
      this@MainActivity,
      cameraPreview,
      true,
      enableQrCodeScanning = true,
      qrResultView,
      qrLottie,
    )
  }
}