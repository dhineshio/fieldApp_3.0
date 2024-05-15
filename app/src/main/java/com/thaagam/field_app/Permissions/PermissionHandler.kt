package com.thaagam.field_app.Permissions
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class PermissionHandler(private val activity: Activity) {

  companion object {
	const val REQUEST_ALL_PERMISSIONS = 123
	const val CAMERA_PERMISSION = Manifest.permission.CAMERA
	const val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
	const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
	const val WRITE_EXTERNAL_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
  }
  interface PermissionCallback{
	fun onPermissionGranted(requestCode : Int)
  }
  // ! ************************************ CHECK AND REQUEST PERMISSION ****************************************************
  fun checkPermissions(permissions : Array<String>): Boolean{
	return permissions.all {
	  ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
	}
  }
  private fun requestPermissions(permissions: Array<out String>, requestCode: Int){
	ActivityCompat.requestPermissions(activity, permissions, requestCode)
  }
  fun checkAndRequestPermission( permissions: Array<String>, requestCode: Int, callback: PermissionCallback){
	if(checkPermissions(permissions)){
	  callback.onPermissionGranted(requestCode)
	}else{
	  val finalPermissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
		permissions.filter { it != WRITE_EXTERNAL_PERMISSION}.toTypedArray()
	  }else{
		permissions
	  }
	  requestPermissions(finalPermissions, requestCode)
	}
  }
  private fun shouldRequestPermissionRationale( permissions: Array<String>) :Boolean{
	Log.d("PermissionRationale", "Permissions: ${permissions.joinToString()}")
	return permissions.any{
	  ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
	}
  }
  fun handlePermissionResult(requestCode: Int, permissions : Array<String>, grantResults: IntArray, callback: PermissionCallback){
	Log.d("Permissions", "Permissions: ${permissions.joinToString()}")
	Log.d("Permissions", "Grant results: ${grantResults.joinToString()}")
	val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
	if(allPermissionsGranted){
	  callback.onPermissionGranted(requestCode)
	}else{
	  val deniedPermissions = permissions.filterIndexed{ index, _ ->
		grantResults[index] != PackageManager.PERMISSION_GRANTED
	  }
	  deniedPermissions.forEach{ permission ->
		showDialog(permission ,requestCode)
	  }
	}
  }
  // ! ************************************ UTILITY *******************************************************
  //GETTING PERMISSION NAME
  private fun permissionName(permission: String): String {
	val tempArray = permission.split("\\.".toRegex())
	return tempArray.last().lowercase(Locale.getDefault())
  }
  //SHOW SETTINGS DIALOG
  private fun showDialog(permission: String, requestCode: Int) {
	val builder = AlertDialog.Builder(activity)
	builder.setTitle("Permission Required")
	  .setMessage("This app requires ${permissionName(permission)} permission to function properly. Please grant the permission in the app settings.")
	  .setPositiveButton("OK") { dialog, _ ->
		dialog.dismiss()
		requestPermissions(arrayOf(permission), requestCode)
	  }
	// Check if the user has selected "Don't show again"
	if (!shouldRequestPermissionRationale(arrayOf(permission))) {
	  builder.setNeutralButton("GO TO SETTINGS") { dialog, _ ->
		dialog.dismiss()
		openSettings()
	  }
	}
	builder.setCancelable(false).show()
  }
  //SETTINGS REDIRECTOR
  private fun openSettings() {
	val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
	val uri = Uri.fromParts("package", activity.packageName, null)
	intent.data = uri
	activity.startActivity(intent)
  }
}


