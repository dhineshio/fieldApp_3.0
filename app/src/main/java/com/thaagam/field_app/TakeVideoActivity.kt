package com.thaagam.field_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import com.thaagam.field_app.Camerautils.CameraUtil
import com.thaagam.field_app.Permissions.PermissionHandler
import com.thaagam.field_app.Utilities.BlinkScreenUtil
import com.thaagam.field_app.Utilities.SoundUtil

class TakeVideoActivity : AppCompatActivity() {

  //OBJECTS
  private val permissionHandler = PermissionHandler(this)
  private val cameraUtil = CameraUtil(this)
  private val soundUtil = SoundUtil(this, cameraUtil.cameraExecutor())
  private val blinkScreenUtil = BlinkScreenUtil(this)
  //UI ELEMENTS
  private lateinit var cameraPreview : PreviewView
  private lateinit var captureBtn : ImageButton

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
    setContentView(R.layout.activity_take_video)
    initUI()

  }

  @SuppressLint("ClickableViewAccessibility")
  private fun initUI(){
    cameraPreview = findViewById(R.id.cameraPreview)
    captureBtn = findViewById(R.id.capture_btn)

    captureBtn.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        // The button is pressed
        soundUtil.soundEffect(R.raw.camera_click)
        blinkScreenUtil.blinkScreen()
      }
      false // Return false to indicate that we haven't consumed the event
    }
  }

  private fun startCamera(){
    cameraUtil.startCamera(this, cameraPreview, true, false)
  }
}