package com.thaagam.field_app.Utilities

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class FileUtil {
  fun convertToFilenameSafe(text: String): String {
	val normalizedText = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFKD)
	val safeText =
	  URLEncoder.encode(normalizedText, StandardCharsets.UTF_16LE.toString()).replace('+', ' ')
	val parts = safeText.split("-").toMutableList()
	val prefix = if (parts.size >= 2) {
	  parts[0]
	} else {
	  safeText
	}
	val suffix = if (parts.size >= 5) {
	  "-" + parts[parts.size - 5] + "-" + parts[parts.size - 4] + "-" + parts[parts.size - 3] + "-" + parts[parts.size - 2] + "-" + parts[parts.size - 1]
	} else {
	  safeText
	}
	val res = if (prefix.length >= 50) {
	  prefix.substring(0, 20) + suffix
	} else {
	  safeText
	}
	return res
  }
}