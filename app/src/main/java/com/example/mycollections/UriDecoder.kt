package com.example.mycollections

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UriDecoder @Inject constructor(
  @ApplicationContext context: Context
) {

  private val contentResolver = context.contentResolver

  fun decodeUri(uri: Uri): Bitmap {
    return contentResolver.openInputStream(uri).use { input ->
      BitmapFactory.decodeStream(input)
    }
  }

}