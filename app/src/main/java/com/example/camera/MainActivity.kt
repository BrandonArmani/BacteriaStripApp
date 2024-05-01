package com.example.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightbulbCircle
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var cameraManager: CameraManager
    private var isFlashOn : Boolean = false
    private lateinit var cameraId: String
    private val showProcess = mutableStateOf(false)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        cameraManager = getSystemService(Context. CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: Exception){
            e.printStackTrace()
        }

        setContent {

            val scope = rememberCoroutineScope()
            val scaffoldState = rememberBottomSheetScaffoldState()
            val controller = remember {
                LifecycleCameraController(applicationContext).apply {
                    setEnabledUseCases(
                        CameraController.IMAGE_CAPTURE
                    )
                }
            }
            val viewModel = viewModel<MainViewModel>()
            val bitmaps by viewModel.bitmaps.collectAsState()
            val image: Painter = painterResource(id = R.drawable.projectboxnew)
            val dot: Painter= painterResource(id = R.drawable.ducky2)



            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 0.dp,
                sheetContent = {
                    ImagePanel(
                        bitmaps = bitmaps,
                        modifier = Modifier
                            .fillMaxWidth()
                    )



                }

            ) { padding ->
                if(showProcess.value) {
                    FlashLight()
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),

                ) {
                    CameraPreview(
                        controller = controller,
                        modifier = Modifier

                            .fillMaxSize()

                    )
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround){
                        Image(

                            painter = image,
                            contentDescription = null,  // contentDescription is required for accessibility
                            modifier = Modifier
                                .size(400.dp)




                        )




                    }
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround){

                        Image(
                            painter = dot,
                            contentDescription = null,
                            modifier = Modifier
                                .size(10.dp)
                                .offset(y = -5.dp)
                        )


                    }




                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround

                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                        }

                        ) {
                            Icon(imageVector = Icons.Default.Photo, contentDescription = "Open gallery")
                        }

                        IconButton(
                            onClick = {

                                takePhoto(
                                    controller = controller,
                                    onPhotoTaken = viewModel::onTakePhoto
                                )

                        }

                        ) {

                            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Takes Picture")
                        }
                        IconButton(
                            onClick = {

                                showProcess.value = true

                            }
                        ) {
                            Icon(imageVector = Icons.Default.LightbulbCircle, contentDescription = "Torch" )
                        }

                    }


                }
            }
        }

    }

    private fun hasRequiredPermissions() : Boolean {
        return CameraX_Permissions.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun takePhoto(
        controller: LifecycleCameraController,
        onPhotoTaken: (Bitmap) -> Unit
        ) {
            controller.takePicture(
                ContextCompat.getMainExecutor(applicationContext),
                object : OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)
                        val matrix = Matrix().apply {
                            postRotate(image.imageInfo.rotationDegrees.toFloat())
                        }
                        val rotatedbitmap = Bitmap.createBitmap(

                            image.toBitmap(),
                            0,
                            0,
                            image.width,
                            image.height,
                            matrix,
                            true
                        )



                        onPhotoTaken(rotatedbitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                        Log.e("Camera", "Couldn't take photo")

                    }
                }
            )

    }

    @Composable
    fun FlashLight() {
        val torchStatus = remember {
            mutableStateOf(false)
        }

        try {
            // O means back camera unit,
            // 1 means front camera unit
            // on below line we are getting camera id
            // for back camera as we will be using
            // torch for back camera
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: Exception) {
            // on below line we are handling exception.
            e.printStackTrace()
        }


    }





           companion object {
               private val CameraX_Permissions = arrayOf(
                   Manifest.permission.CAMERA
               )
           }
       }
