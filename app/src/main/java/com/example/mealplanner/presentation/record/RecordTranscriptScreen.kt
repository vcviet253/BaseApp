package com.example.mealplanner.presentation.record

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RecordTranscriptScreen(viewModel: RecordViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity // Get the Activity from the context
    val uiState by viewModel.recordUiState.collectAsState()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted
                Toast.makeText(context, "Permission granted. Recording", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) == true)  {
                    // User denied the permission but didn't select "Don't ask again"
                    Toast.makeText(context, "Permission denied. Cannot export the quiz.", Toast.LENGTH_SHORT).show()
                } else {
                    // User denied the permission and selected "Don't ask again", show an explanation and navigate to settings
                    Toast.makeText(context, "Permission denied permanently. Please enable it in settings.", Toast.LENGTH_SHORT).show()
                    openAppSettings(context)
                }
            }
        }
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                viewModel.startRecording()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }) {
            Text("Start recording")
        }

        Button(onClick = {
            viewModel.stopRecording()
        }) {
            Text("Stop recording")
        }

        Text(text = uiState.isRecording.toString())

        Text(
            text = "Real-time Text:",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị phần đã nói xong (Final)
        Text(
            text = uiState.historyText,
            style = MaterialTheme.typography.bodyMedium
        )

        // Hiển thị phần đang nói (Partial)
        Text(
            text = uiState.liveText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nếu có lỗi
        uiState.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Button(onClick = { viewModel.startListening() }) {
                Text("Start Listening")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.stopListening() }) {
                Text("Stop Listening")
            }
        }
    }
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}