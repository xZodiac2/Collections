package com.example.mycollections

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Immutable
data class Photos(
  val photos: List<Bitmap>
)

@HiltViewModel
class MainViewModel @Inject constructor(
  private val localRepository: LocalRepository,
  private val uriDecoder: UriDecoder
) : ViewModel() {

  private lateinit var currentPhotoUri: Uri

  private val _photosState = MutableStateFlow(Photos(emptyList()))
  val photosState = _photosState.asStateFlow()

  private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
  val snackbarState = _snackbarState.asStateFlow()

  fun handleEvent(event: MainEvent) {
    when (event) {
      MainEvent.Start -> onStart()
      is MainEvent.AddClick -> onAddClick(event.activityResultLauncher)
      is MainEvent.PhotoAdded -> onPhotoAdded(event.success)
      MainEvent.SnackbarConsumed -> _snackbarState.value = SnackbarState.Consumed
    }
  }

  private fun onStart() {
    val photoFiles = localRepository.getAllImages()

    val bitmaps = photoFiles.mapNotNull { file ->
      BitmapFactory.decodeFile(file.path)
    }

    _photosState.value = Photos(bitmaps)
  }

  private fun onAddClick(activityResultLauncher: ManagedActivityResultLauncher<Uri, Boolean>) {
    currentPhotoUri = localRepository.createFileAndGetUri()
    activityResultLauncher.launch(currentPhotoUri)
  }

  private fun onPhotoAdded(success: Boolean) {
    if (success) {
      onAddPhotoSuccess()
    } else {
      onAddPhotoFailure()
    }
  }

  private fun onAddPhotoSuccess() {
    val bitmap = runCatching { uriDecoder.decodeUri(currentPhotoUri)!! }
    bitmap.fold(
      onSuccess = {
        val newPhotos = _photosState.value.photos.toMutableList()
        newPhotos += it
        _photosState.value = Photos(newPhotos)
      },
      onFailure = ::onDecodePhotoFailure
    )
  }

  private fun onAddPhotoFailure() {
    _snackbarState.value = SnackbarState.Triggered("Something went wrong (´•︵•`)")
  }

  private fun onDecodePhotoFailure(error: Throwable) {
    _snackbarState.value = SnackbarState.Triggered("Some decode failure happened: $error")
  }

}
