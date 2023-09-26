package com.example.textrecognizer.ui.no_permission

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NoPermissionScreen(onRequestPermission:() -> Unit) {
    NoPermissionContent (onRequestPermission)
}

@Composable
private fun NoPermissionContent(onRequestPermission: () -> Unit) {
   Box(
       modifier = Modifier.fillMaxSize(),
       contentAlignment = Alignment.Center
   ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(textAlign = TextAlign.Center,
                text = "Please grant the permission to use the camera to use the core functionality of this app."
            )
            Button(onClick = onRequestPermission) {
                Icon(imageVector = Icons.Default.Camera, contentDescription = "camera", modifier = Modifier.padding(8.dp))
                Text(text = "Grant Permission")
            }
        }
   }
}

@Preview
@Composable
fun Preview_NoPermissionContent() {
    NoPermissionScreen (onRequestPermission = {})
}
