package com.example.mycollections

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import javax.inject.Inject

class LocalRepository @Inject constructor(
  @ApplicationContext
  private val context: Context
) {

  fun getAllImages(): List<File> {
    val imagesDir = getOrCreateImagesDir()
    if (!imagesDir.exists()) return emptyList()

    return imagesDir.listFiles()?.toList() ?: emptyList()
  }

  fun createFileAndGetUri(): Uri {
    val imagesDir = getOrCreateImagesDir()
    val photoFile = createPhotoFile(imagesDir)
    val authority = "${context.packageName}.fileProvider"

    return FileProvider.getUriForFile(context, authority, photoFile)
  }

  private fun getOrCreateImagesDir(): File {
    val dir = File(context.filesDir, IMAGES_DIR_NAME)
    if (!dir.exists()) {
      dir.mkdirs()
    }
    return dir
  }

  private fun createPhotoFile(imagesDir: File): File {
    val filename = "collectionName_${System.currentTimeMillis()}.ext"
    val file = File(imagesDir, filename)
    file.createNewFile()
    return file
  }

  companion object {
    private const val IMAGES_DIR_NAME = "images"
  }

}