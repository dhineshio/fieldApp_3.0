package com.thaagam.field_app.Utilities
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.io.File
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class PathHandler(private val context: Context){

  fun getOutputDirectory(): File {
	val currentDate = java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
	val rootDir = ContextCompat.getExternalFilesDirs(context, null).firstOrNull()?.let {
	  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), ".Thaagam").apply {
		if(!exists()){
		  val created = mkdirs()
		  if (!created) {
			// Directory creation failed
			Log.e("OutputDirectory", "Failed to create Thaagam directory")
		  }
		}
	  }
	}
	val currentDateDir = File(rootDir, currentDate).apply {
	  if(!exists()) {
		val created = mkdirs()
		if(!created){
		  //Directory creation failed
		  Log.e("OutputDirectory", "Failed to create current date directory")
		}
	  }
	}
	// Return the directory, either the app-specific one or the internal storage directory
	return currentDateDir
  }
  fun getOutputDirectory1(qrCodeData : String?): File {
	val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
	val rootDir = ContextCompat.getExternalFilesDirs(context, null).firstOrNull()?.let {
	  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), ".Thaagam").apply{
		if(!exists()){
		  val created = mkdirs()
		  if (!created) {
			// Directory creation failed
			Log.e("OutputDirectory", "Failed to create Thaagam directory")
		  }
		}
	  }
	}
	val currentDateDir = File(rootDir, currentDate).apply {
	  if(!exists()){
		val created = mkdirs()
		if(!created){
		  //Directory creation failed
		  Log.e("OutputDirectory", "Failed to create current date directory")
		}
	  }
	}
	val parts = qrCodeData?.split("-")?.let {
		list ->
	  if(list.size >= 4){
		listOf(list[0], list[2])
	  } else {
		emptyList()
	  }
	} ?: emptyList()

	val subFolderName = if(parts.isNotEmpty()){
	  parts.joinToString("-")
	} else {
	  "default"
	}
	val subFolder = File(currentDateDir, subFolderName).apply {
	  if(!exists()){
		val created = mkdirs()
		if(!created){
		  Log.e("OutputDirectory", "Failed to create subfolder")
		}
	  }
	}
	if(!subFolder.canWrite()){
	  Log.e("OutputDirectory", "Cannot write to subfolder")
	}
	return subFolder
  }
  fun requestStoragePermissions(activity :Activity) {
	val REQUEST_WRITE_EXTERNAL_STORAGE = 1
	if (ContextCompat.checkSelfPermission(
		activity,
		Manifest.permission.WRITE_EXTERNAL_STORAGE
	  ) != PackageManager.PERMISSION_GRANTED
	) {
	  ActivityCompat.requestPermissions(
		activity,
		arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
		REQUEST_WRITE_EXTERNAL_STORAGE
	  )
	}
  }
}