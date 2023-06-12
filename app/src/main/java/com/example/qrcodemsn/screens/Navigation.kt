package com.example.margins.screens

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.margins.acumin_pro
import com.example.margins.lightgreyTextColor
import com.example.margins.redButtonColor
import com.example.margins.termina
import com.example.qrcodemsn.BarcodeAnalyser
import com.example.qrcodemsn.R
import com.example.qrcodemsn.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route ){
        composable(route = Screen.MainScreen.route){
            ScanScreenUi(navController)
        }
        composable(route= Screen.QrcodeScreen.route){
            PreviewViewComposable(navController)
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreenUi(navController: NavController,cameraPermission: PermissionState = cameraPermissionState(), modifier: Modifier = Modifier) {

    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Red)) {
        Image(painter = painterResource(id  = R.drawable.background_image), contentDescription = "Background Image",modifier = modifier.fillMaxSize() , contentScale = ContentScale.FillBounds)

        Column(
            modifier= Modifier
                .padding(horizontal = 16.dp)
                .wrapContentSize()
                .align(Alignment.BottomCenter)) {
            Text(
                text = "Scan your TV Screen",
                modifier = modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W800,
                fontSize = 40.sp,
                lineHeight = 35.sp,
                fontFamily = termina,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Turn on your mobile camera to scan QR code shown on your TV screen.",
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W600,
                fontFamily = acumin_pro,
                fontSize = 14.sp,
                color = lightgreyTextColor,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (cameraPermission.status.isGranted) {
                        navController.navigate(Screen.QrcodeScreen.route)
                    } else if (!cameraPermission.status.isGranted) {
                        cameraPermission.launchPermissionRequest()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = redButtonColor),
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RectangleShape
            ) {
                Text(
                    color = Color.White,
                    text = "Turn Camera On",
                    fontSize = 14.sp,
                    fontFamily = acumin_pro,
                    fontWeight = FontWeight.W600,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                color = Color.Gray,
                text = "Do it later",
                fontSize = 14.sp,
                fontFamily = acumin_pro,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {

                    },
                textDecoration = TextDecoration.Underline
            )

            Spacer(modifier = Modifier.height(50.dp))

        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun cameraPermissionState(): PermissionState {
    return rememberPermissionState(permission = Manifest.permission.CAMERA)
}



@SuppressLint("RestrictedApi")
@Composable
fun PreviewViewComposable(navController: NavController) {
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

                val preview = androidx.camera.core.Preview.Builder()
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
        Image(painter = painterResource(id = R.drawable.baseline_close_24), contentDescription = "close button", modifier = Modifier
            .padding(top = 50.dp, end = 28.dp)
            .size(25.dp)
            .align(
                Alignment.TopEnd
            )
            .clickable {
                navController.navigate(Screen.MainScreen.route)
            }
        )
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