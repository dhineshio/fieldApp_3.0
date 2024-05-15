package com.thaagam.field_app.Utilities

import android.content.Context
import android.util.Log
import android.widget.Toast

class CommonUtil(private val context: Context) {
  fun toast(msg : String){
	Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
  }
  fun log(tag : String, message : String){
	Log.i(tag, message)
  }
}