package com.badr1.ardraw.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
//import com.example.ardraw.viewmodels.CameraPermissionViewModel
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.camera.core.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.badr1.ardraw.R
import com.badr1.ardraw.screens.components.RequestPermissions
import com.badr1.ardraw.viewmodels.utils.PermissionUtils
import com.badr1.ardraw.viewmodels.utils.createPublicImageOutputOptions
import com.badr1.ardraw.ui.theme.PurpleBoxColor
import com.badr1.ardraw.viewmodels.MultiPermissionsViewModel


@Composable
fun DrawImageScreen(
    imageSource: ImageSourceType,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: MultiPermissionsViewModel = viewModel()

    val permissions = listOf("camera")

    // FIX: Initialize state correctly so it doesn't disappear
    var showCamImage by remember {
        mutableStateOf(PermissionUtils.allPermissionsGranted(context))
    }

    var refreshTrigger by remember { mutableIntStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // This handles the logic and popup UI
    key(refreshTrigger) {
        RequestPermissions(viewModel, permissions = permissions, onAllGranted = {
            showCamImage = true
        }, onPermissionDenied = {
            showCamImage = false
        })
    }
    if (showCamImage) {
        DrawImageContent(imageSource)
    } else {
        navController.navigateUp()
    }
}


@Composable
fun CameraPreview(
    flashEnabled: Boolean,
    lensFacing: Int,
    modifier: Modifier = Modifier,
    onCaptureUseCasesReady: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(flashEnabled, lensFacing) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = Preview.Builder().build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        imageCapture.targetRotation = previewView.display.rotation
        cameraProvider.unbindAll()

        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            selector,
            preview,
            imageCapture
        )

        preview.surfaceProvider = previewView.surfaceProvider
        camera.cameraControl.enableTorch(flashEnabled)
        onCaptureUseCasesReady(imageCapture)
    }

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        factory = { previewView }
    )
}


@Composable
fun DrawImageContent(source: ImageSourceType) {

    val context = LocalContext.current

    // --- STATE MANAGEMENT ---
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var savedImagePath by remember { mutableStateOf<Uri?>(null) }


    // Camera/Gesture States (Your existing state)
    var flashEnabled by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var alpha by remember { mutableFloatStateOf(0.8f) }
    var locked by remember { mutableStateOf(false) }
    var flipX by remember { mutableFloatStateOf(1f) } // 1f = normal, -1f = flipped

    // --- Photo Capture Logic ---
    val takePhoto = {
        val capture = imageCapture // ImageCapture instance stored in state
        if (capture != null && !isCapturing) {
            isCapturing = true
            val outputOptions = createPublicImageOutputOptions(context)

            capture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),

                // --- FIX IS HERE: Implement both functions ---
                object : ImageCapture.OnImageSavedCallback {

                    // 1. SUCCESS CALLBACK
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        isCapturing = false
                        output.savedUri?.let { uri ->
                            savedImagePath = uri
                        }
                    }

                    // 2. ERROR CALLBACK (The missing function)
                    override fun onError(exc: ImageCaptureException) {
                        isCapturing = false
//                        Log.e("CAMERA_CAPTURE", "Photo capture failed: ${exc.message}", exc)

                        // IMPORTANT: Handle the error gracefully, e.g.:
                        Toast.makeText(context, "Capture failed: ${exc.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            )
        }
    }

    Box(Modifier.fillMaxSize()) {

        // 1️⃣ Camera Preview
        CameraPreview(
            flashEnabled = flashEnabled,
            lensFacing = lensFacing,
            onCaptureUseCasesReady = { capture ->
                imageCapture = capture
            }
        )

        // 2️⃣ Image Overlay (Coil AsyncImage)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (!locked) {
                        Modifier.pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, rotate ->
                                offset += pan
                                scale = (scale * zoom).coerceIn(0.2f, 5f)
                                rotation += rotate
                            }
                        }
                    } else Modifier // 🔒 gestures disabled when locked
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = when (source) {
                    is ImageSourceType.Url -> source.url
                    is ImageSourceType.UriSourceType -> source.uri
                    is ImageSourceType.BitMap -> source.bitmap

                },
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = offset.x
                        translationY = offset.y
                        scaleX = scale * flipX
                        scaleY = scale
                        rotationZ = rotation
                        this.alpha = alpha
                    }
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }

        // 3️⃣ Tools (flash, switch camera, opacity)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(4.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF4F4F4))
                    .padding(8.dp)
            ) {

                // Slider row
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.rounded_opacity_24),
                        contentDescription = "Flash Control",
                        modifier = Modifier.size(22.dp),
                        tint = Color.DarkGray
                    )

                    Slider(
                        value = alpha,
                        onValueChange = { alpha = it },
                        valueRange = 0f..1f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleBoxColor,
                            activeTrackColor = PurpleBoxColor,
                            inactiveTrackColor = Color.LightGray,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        )
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Icon bar
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FancyIcon(R.drawable.rounded_flip_24, onClick = {
                        flipX *= -1f
                    })
                    FancyIcon(R.drawable.rounded_lock_24, onClick = {
                        locked = !locked
                    }, isActive = locked)
                    FancyIcon(
                        R.drawable.rounded_flash_on_24,
                        onClick = { flashEnabled = !flashEnabled }, isActive = flashEnabled
                    )
                    FancyIcon(
                        R.drawable.rounded_photo_camera_24,
                        onClick = takePhoto
                    )


                }
            }
        }
        // 4️⃣ POP-UP DISPLAY
        savedImagePath?.let { uri ->
            ImageSavedPopup(
                imageUri = uri,
                onDismiss = { savedImagePath = null } // Hides the pop-up
            )
        }
    }
}

@Composable
fun FancyIcon(
    iconId: Int,
    modifier: Modifier = Modifier,
    isActive: Boolean = false, // New parameter to "stick" the color
    onClick: () -> Unit = {},
    isCapturing: Boolean = false

) {
    val isEnabled = !isCapturing

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // The "highlighted" state is active if the user is pressing OR isActive is true
    val isHighlighted = isPressed || isActive

    // Colors respond to the combined state
    val backgroundColor = if (isHighlighted) PurpleBoxColor else Color.LightGray
    val iconTint = if (isHighlighted) Color.White else Color.DarkGray

    Box(
        modifier = modifier
            .size(40.dp)
            .background(backgroundColor, shape = CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = onClick
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = iconTint
        )
    }
}

@Composable
fun ImageSavedPopup(
    imageUri: Uri,
    onDismiss: () -> Unit
) {
    // Define the accent colors used in FancyIcon for consistency
    val successColor = Color.Black // Use black as the primary accent/active color
    val secondaryBgColor = Color(0xFFF4F4F4) // Light gray used in your controls background

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        // Use a Card for the main dialog body, setting the secondary background color
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            // Use a slight elevation for depth
            elevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .background(secondaryBgColor) // Apply the light gray background
                    .padding(16.dp)
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- 1. Title with Accent Color Icon ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Success",
                        tint = successColor, // Use the black accent color
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Image Saved!",
                        style = MaterialTheme.typography.h6.copy(color = successColor) // Black text
                    )
                }

                // --- 2. Image Preview Container (400.dp fixed height) ---
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)) // Slightly more rounded corners for the image
                        .background(Color.DarkGray), // Darker background to make the image pop
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Saved Image Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }

                // --- 3. Path Display ---
                Spacer(Modifier.height(16.dp))

                // Display Path Information in a stylized box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.85f)) // White box for contrast
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "File Path:",
                        style = MaterialTheme.typography.subtitle2.copy(color = Color.Black)
                    )
                    Text(
                        text = imageUri.toString(),
                        style = MaterialTheme.typography.caption.copy(color = Color.DarkGray),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // --- 4. Dismiss Button (Styled to match FancyIcon active state) ---
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth(0.7f) // Make the button slightly narrower
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = successColor,
                        contentColor = Color.White // White text
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("DONE")
                }
            }
        }
    }
}