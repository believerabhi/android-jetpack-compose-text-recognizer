package com.example.textrecognizer.ui.camera

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.AspectRatio
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraScreen() {
    CameraContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraContent() {
    val context: Context = LocalContext.current
    val lifecycleOwner :LifecycleOwner = LocalLifecycleOwner.current
    val cameraController : LifecycleCameraController = remember { LifecycleCameraController(context) }
    var detectText :String by remember { mutableStateOf("No text detected yet") }
    //var detectTextList: List<String>  by remember { mutableStateListOf("abc") }
    var detectTextList: List<String>  by remember { mutableStateOf(listOf()) }

    fun onTextUpdate(updateText: String) {
        detectText = updateText
    }

    fun onTextUpdate(updateText: List<String>) {
        detectTextList = updateText
    }

    if (detectText != "No text detected yet") {
        showText(detectText = detectTextList)
    } else {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = { TopAppBar(title = { Text ("Text Scanner") } ) },
        ) { paddingValues : PaddingValues ->
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {

                AndroidView(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                    factory = {context->
                        PreviewView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setBackgroundColor(Color.BLACK)
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            scaleType = PreviewView.ScaleType.FILL_START
                        }.also {previewView ->
                            startTextRecognition(
                                context = context,
                                cameraController = cameraController,
                                lifecycleOwner = lifecycleOwner,
                                previewView = previewView,
                                onDetectedTextUpdated = ::onTextUpdate,
                                onDetectedTextBlockUpdated = ::onTextUpdate
                            )
                        }
                    }
                )

            Text(modifier = Modifier.fillMaxWidth().padding(16.dp).background(androidx.compose.ui.graphics.Color.White),
                text = detectText)
            }
        }
    }

}

@Composable
fun showText(
    detectText: String
) {
    Text(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(androidx.compose.ui.graphics.Color.White),
        text = detectText)
}

@Composable
fun showText(
    detectText: List<String>
) {
    LazyColumn() {
        items(detectText) { text ->
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(androidx.compose.ui.graphics.Color.White),
                text = text)
        }
    }
}

private fun startTextRecognition(context: Context,
                                 cameraController: LifecycleCameraController,
                                 lifecycleOwner: LifecycleOwner,
                                 previewView: PreviewView,
                                 onDetectedTextUpdated: (String)-> Unit,
                                 onDetectedTextBlockUpdated :(List<String>)->Unit) {

    cameraController.imageAnalysisTargetSize = CameraController.OutputSize(AspectRatio.RATIO_16_9)
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        TextRecognitionAnalyzer(onDetectedTextUpdated = onDetectedTextUpdated,
            onDetectedTextBlockUpdated = onDetectedTextBlockUpdated)
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}
