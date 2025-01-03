package com.example.mycollections

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher

sealed interface MainEvent {
  data class AddClick(val activityResultLauncher: ManagedActivityResultLauncher<Uri, Boolean>) : MainEvent
  data class PhotoAdded(val success: Boolean) : MainEvent

  data object Start : MainEvent
  data object SnackbarConsumed : MainEvent
}
