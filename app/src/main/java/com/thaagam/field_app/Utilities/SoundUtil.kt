package com.thaagam.field_app.Utilities

import android.content.Context
import android.media.MediaPlayer
import java.util.concurrent.ExecutorService

class SoundUtil(private val context: Context,private val cameraExecutor: ExecutorService) {
  fun soundEffect(soundId : Int){
	cameraExecutor.submit {
	  try {
		val mediaPlayer = MediaPlayer.create(context, soundId)
		mediaPlayer.start()
		  mediaPlayer.setOnCompletionListener { mp ->
			  mp.release()
		  }
	  } catch (e : Exception){
		  e.printStackTrace()
	  }
	}
  }
}