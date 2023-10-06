package com.example.textrecognizer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.textrecognizer.ui.MLKitTextRecognition.MLKitTextRecognition
import com.example.textrecognizer.ui.camera.CameraScreen
import com.example.textrecognizer.ui.no_permission.NoPermissionScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
   val cameraPermissionState : PermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    MainContent(
       hasPermission = cameraPermissionState.status.isGranted,
       onRequestPermission = cameraPermissionState::launchPermissionRequest
   )
}

@Composable
private fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: ()-> Unit) {
     if(hasPermission) {
       CameraScreen()
       //  MLKitTextRecognition()
     } else {
         NoPermissionScreen(onRequestPermission)
     }
}

@Preview
@Composable
private fun Preview_MainContent() {
    MainContent(hasPermission = false,
    onRequestPermission = {})
}
