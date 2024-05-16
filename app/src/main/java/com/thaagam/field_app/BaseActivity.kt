package com.thaagam.field_app

import androidx.appcompat.app.AppCompatActivity
import com.thaagam.field_app.Camerautils.CameraUtil
import com.thaagam.field_app.Permissions.PermissionHandler
import com.thaagam.field_app.Utilities.BlinkScreenUtil
import com.thaagam.field_app.Utilities.FlashlightUtil
import com.thaagam.field_app.Utilities.SoundUtil

open class BaseActivity : AppCompatActivity(){

  protected val cameraUtil: CameraUtil by lazy {
    CameraUtil(this)
  }
  protected val permissionHandler: PermissionHandler by lazy {
    PermissionHandler(this)
  }
  protected val soundUtil: SoundUtil by lazy {
    SoundUtil(this, cameraUtil.cameraExecutor())
  }
  protected val blinkScreenUtil: BlinkScreenUtil by lazy {
    BlinkScreenUtil(this)
  }

  protected val flashlightUtil : FlashlightUtil by lazy {
    FlashlightUtil(this)
  }
}