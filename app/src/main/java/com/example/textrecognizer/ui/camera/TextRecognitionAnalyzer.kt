package com.example.textrecognizer.ui.camera

import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextRecognitionAnalyzer (private val onDetectedTextUpdated :(String)->Unit)
    : ImageAnalysis.Analyzer {

    companion object {
        const val THROTTLE_TIMEOUT_MS = 2_000L
    }
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO+ SupervisorJob())
    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @OptIn(ExperimentalGetImage::class) override fun analyze(imageProxy: ImageProxy) {
        scope.launch {
            val mediaImage:Image = imageProxy.image?: run { imageProxy.close(); return@launch }
            val inputImage: InputImage = InputImage.fromMediaImage(mediaImage,imageProxy.imageInfo.rotationDegrees)

            suspendCoroutine {continuation ->  
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { visionText : Text ->
                        val detectText :String = visionText.text
                        if(detectText.isNotBlank()) {
                            onDetectedTextUpdated(detectText)
                        }
                    }.addOnCompleteListener{
                     continuation.resume(Unit)
                    }
            }
            delay(THROTTLE_TIMEOUT_MS)
        }.invokeOnCompletion {exception->
            exception?.printStackTrace()
            imageProxy.close()
        }
    }

}