package com.example.mycollections

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen() {
  val viewModel = hiltViewModel<MainViewModel>()

  val photosState = viewModel.photosState.collectAsState()
  val snackbarState = viewModel.snackbarState.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(Unit) {
    viewModel.handleEvent(MainEvent.Start)
  }

  SnackbarEventEffect(
    state = snackbarState.value,
    onConsumed = { viewModel.handleEvent(MainEvent.SnackbarConsumed) },
    action = { snackbarHostState.showSnackbar(it) }
  )

  val activityResultLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture(),
    onResult = { viewModel.handleEvent(MainEvent.PhotoAdded(it)) }
  )

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = {
      FabAddPhoto {
        viewModel.handleEvent(MainEvent.AddClick(activityResultLauncher))
      }
    }
  ) { padding ->
    Box(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(36.dp),
        contentPadding = PaddingValues(16.dp)
      ) {
        items(photosState.value.photos) { bitmap ->
          Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "somePhoto"
          )
        }
      }
    }
  }

}

@Composable
private fun FabAddPhoto(onAddClick: () -> Unit) {
  FloatingActionButton(
    onClick = { onAddClick() }
  ) {
    Icon(
      imageVector = Icons.Default.Add,
      contentDescription = "addIcon"
    )
  }
}
