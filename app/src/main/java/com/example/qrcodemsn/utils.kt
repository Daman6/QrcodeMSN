package com.example.margins

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.qrcodemsn.BarcodeAnalyser
import com.example.qrcodemsn.R
import java.util.concurrent.Executors


@SuppressLint("RestrictedApi")
@Composable
fun PreviewViewComposable() {
    Box(modifier = Modifier.fillMaxSize()) {
    AndroidView({ context ->
        val cameraExecutor = Executors.newSingleThreadExecutor()
        val previewView = PreviewView(context).also {
//            it.scaleType = PreviewView.ScaleType.FILL_CENTER
//            it.background = ContextCompat.getDrawable(context, R.drawable.back)
            it.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyser{
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    context as ComponentActivity, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e("DEBUG", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
        previewView
    },
        modifier = Modifier
            .fillMaxSize()
    )
        DrawFourCornerLines()
        Text(
            text = "Scan       on your TV screen to log in.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 150.dp)
                .align(Alignment.BottomCenter),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W600,
            fontFamily = acumin_pro,
            fontSize = 18.sp,
            color = Color.White,
        )
        Image(painter = painterResource(id = R.drawable.baseline_close_24), contentDescription = "close button", modifier = Modifier.padding(top = 50.dp,end = 28.dp).size(25.dp).align(
            Alignment.TopEnd))
    }
}
@Composable
fun DrawFourCornerLines() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 1.dp.toPx()

        val qrCodeSize = size.minDimension / 3.5f
        val qrCodeCenter = center

        val cornerLength = qrCodeSize / 2
        val cornerWidth = strokeWidth * 2

        // Top-left corner
        drawLine(
            color = Color.White,
            start = Offset(qrCodeCenter.x - qrCodeSize, qrCodeCenter.y - qrCodeSize),
            end = Offset(qrCodeCenter.x - qrCodeSize, qrCodeCenter.y - qrCodeSize + cornerLength),
            strokeWidth = cornerWidth,
        )
        drawLine(
            color = Color.White,
            start = Offset(qrCodeCenter.x - qrCodeSize, qrCodeCenter.y - qrCodeSize),
            end = Offset(qrCodeCenter.x - qrCodeSize + cornerLength, qrCodeCenter.y - qrCodeSize),
            strokeWidth = cornerWidth,
        )

        // Top-right corner
        drawLine(
            color = Color.White,
            start = Offset(qrCodeCenter.x + qrCodeSize, qrCodeCenter.y - qrCodeSize),
            end = Offset(qrCodeCenter.x + qrCodeSize, qrCodeCenter.y - qrCodeSize + cornerLength),
            strokeWidth = cornerWidth,
        )
        drawLine(
            color = Color.White,
            start = Offset(qrCodeCenter.x + qrCodeSize, qrCodeCenter.y - qrCodeSize),
            end = Offset(qrCodeCenter.x + qrCodeSize - cornerLength, qrCodeCenter.y - qrCodeSize),
            strokeWidth = cornerWidth,
        )

        // Bottom-right corner
        drawLine(
            color = Color.White,
            start = Offset(qrCodeCenter.x + qrCodeSize, qrCodeCenter.y + qrCodeSize),
            end = Offset(qrCodeCenter.x + qrCodeSize, qrCodeCenter.y + qrCodeSize - cornerLength),
            strokeWidth = cornerWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(qrCodeCenter.x + qrCodeSize, qrCodeCenter.y + qrCodeSize),
            end = Offset(qrCodeCenter.x + qrCodeSize - cornerLength, qrCodeCenter.y + qrCodeSize),
            strokeWidth = cornerWidth,
            cap = StrokeCap.Round
        )

        // Bottom-left corner
        drawLine(
            color = Color.White,
            start = Offset(qrCodeCenter.x - qrCodeSize, qrCodeCenter.y + qrCodeSize),
            end = Offset(qrCodeCenter.x - qrCodeSize, qrCodeCenter.y + qrCodeSize - cornerLength),
            strokeWidth = cornerWidth,
        )
        drawLine(
            color = Color.White,
            start = Offset(qrCodeCenter.x - qrCodeSize, qrCodeCenter.y + qrCodeSize),
            end = Offset(qrCodeCenter.x - qrCodeSize + cornerLength, qrCodeCenter.y + qrCodeSize),
            strokeWidth = cornerWidth,
        )
    }
}