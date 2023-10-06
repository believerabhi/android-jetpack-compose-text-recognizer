package com.example.textrecognizer.ui.camera

import android.media.Image
import android.util.Log
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

class TextRecognitionAnalyzer (private val onDetectedTextUpdated :(String)->Unit,
                               private val onDetectedTextBlockUpdated :(List<String>)->Unit)
    : ImageAnalysis.Analyzer {

    companion object {
        const val THROTTLE_TIMEOUT_MS = 5_000L
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
                        val list  = mutableListOf<String>()
                       // Log.d("Abhi","1. vision text =  $detectText")
                        for (block in visionText.textBlocks) {
                            val blockText = block.text
                            list.add(blockText)
                           // Log.d("Abhi","2. block text =  $blockText")
                            val blockCornerPoints = block.cornerPoints
                            //Log.d("Abhi","2. block cornerPoint =  $blockCornerPoints")
                            val blockFrame = block.boundingBox
                           // Log.d("Abhi","2. block boundingbox =  $blockFrame")
                            for (line in block.lines) {
                                val lineText = line.text
                                //detectText += lineText
                               // Log.d("Abhi","3. line line text =  $lineText")
                                val lineCornerPoints = line.cornerPoints
                                //Log.d("Abhi","3. line cornerPoint =  $lineCornerPoints")
                                val lineFrame = line.boundingBox
                               // Log.d("Abhi","3. line boundingbox =  $lineFrame")
                                for (element in line.elements) {
                                    val elementText = element.text
                                   // detectText += elementText
                                    Log.d("Abhi","4. element  text =  $elementText")
                                    val elementCornerPoints = element.cornerPoints
                                   // Log.d("Abhi","4. element cornerPoint =  $elementCornerPoints")
                                    val elementFrame = element.boundingBox
                                    //Log.d("Abhi","4. element boundingbox =  $elementFrame")
                                }
                            }
                        }
                        if(detectText.isNotBlank()) {
                            onDetectedTextUpdated(detectText)
                            onDetectedTextBlockUpdated(list)
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