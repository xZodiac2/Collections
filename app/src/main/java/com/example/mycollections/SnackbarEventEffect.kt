package com.example.mycollections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable

sealed interface SnackbarState {
  data object Consumed : SnackbarState
  data class Triggered(val text: String) : SnackbarState
}

@Composable
@NonRestartableComposable
fun SnackbarEventEffect(state: SnackbarState, onConsumed: () -> Unit, action: suspend (String) -> Unit) {
  if (state is SnackbarState.Triggered) {
    LaunchedEffect(Unit) {
      action(state.text)
      onConsumed()
    }
  }
}