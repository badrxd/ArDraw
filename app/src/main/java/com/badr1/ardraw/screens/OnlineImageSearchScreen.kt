package com.badr1.ardraw.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.imageLoader
import coil.request.ImageRequest
import com.badr1.ardraw.GOOGLE_IMAGES_URL
import com.badr1.ardraw.MOBILE_UA
import com.badr1.ardraw.navigation.Screen
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel
import kotlinx.coroutines.launch

/* --------------------------- BASE64 DECODER ------------------------------ */

fun decodeBase64ToBitmap(data: String): Bitmap? {
    return try {
        val clean = data.substringAfter(",")
        val bytes = Base64.decode(clean, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun CapturedImageDialog(
    bitmap: Bitmap,
    onClose: () -> Unit,
    onUseImage: (Bitmap) -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Captured Image",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        onUseImage(bitmap)
                        onClose()
                    }) {
                        Text("Use Image")
                    }
                }
            }
        }
    }
}

/* ---------------------------- MAIN SCREEN -------------------------------- */

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OnlineImageSearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SharedDrawImageViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    val status = remember { mutableStateOf("Long press on any image to capture") }
    val capturedBitmap = remember { mutableStateOf<Bitmap?>(null) }

    /* ------------------------- IMAGE CAPTURE HANDLER ---------------------- */

    val onCaptured: (String) -> Unit = onCaptured@{ raw ->

        // BASE64 IMAGE
        if (raw.startsWith("data:image", true)) {
            val bmp = decodeBase64ToBitmap(raw)
            if (bmp != null) {
                capturedBitmap.value = bmp  // This triggers dialog
                status.value = "Image captured successfully"
            } else {
                status.value = "Failed to decode image. Try another one."
            }
            return@onCaptured
        }

        // DIRECT IMAGE URL
        if (raw.startsWith("http", true)) {
            status.value = "Downloading image..."

            scope.launch {
                try {
                    val request = ImageRequest.Builder(context)
                        .data(raw)
                        .allowHardware(false)
                        .target { drawable ->
                            val bmp = (drawable as? BitmapDrawable)?.bitmap
                            if (bmp != null) {
                                capturedBitmap.value = bmp  // This triggers dialog
                                status.value = "Image downloaded successfully"
                            } else {
                                status.value = "Invalid image format. Try another one."
                            }
                        }
                        .build()

                    context.imageLoader.execute(request)
                } catch (e: Exception) {
                    status.value = "Download failed: ${e.message?.take(50) ?: "Unknown error"}"
                }
            }
            return@onCaptured
        }

        // NOT CAPTURABLE
        status.value = "This image is protected or not loaded. Try another one."
    }

    /* ----------------------------- WEBVIEW CLIENT ------------------------- */

    val webClient = remember {
        object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                if (
                    url.endsWith(".jpg", true) ||
                    url.endsWith(".png", true) ||
                    url.endsWith(".webp", true) ||
                    url.endsWith(".jpeg", true)
                ) {
                    onCaptured(url)
                    return true
                }
                return false
            }
        }
    }

    /* ------------------------------- BACK HANDLER -------------------------- */

    BackHandler {
        if (webViewRef.value?.canGoBack() == true) {
            webViewRef.value?.goBack()
        } else {
            navController.navigateUp()
        }
    }

    /* -------------------------------- UI ---------------------------------- */

    Column(modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.userAgentString = MOBILE_UA

                    webViewClient = webClient

                    setOnLongClickListener { v ->
                        val hit = (v as WebView).hitTestResult
                        val data = hit.extra

                        Log.d(
                            "ImageCapture",
                            "HitType=${hit.type}, Data=${data?.take(60)}"
                        )

                        if (!data.isNullOrEmpty()) {
                            onCaptured(data)
                        } else {
                            status.value = "Could not capture this image"
                        }
                        true
                    }

                    webViewRef.value = this
                    loadUrl("${GOOGLE_IMAGES_URL}easy+line+drawings")
                }
            }
        )

        // Status bar - shows errors or success messages
        Text(
            text = "Status: ${status.value}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.body2,
            color = if (status.value.contains("failed", ignoreCase = true) ||
                status.value.contains("protected", ignoreCase = true)
            ) {
                MaterialTheme.colors.error
            } else {
                MaterialTheme.colors.onSurface
            }
        )
    }

    // Dialog ONLY shows when bitmap is captured successfully
    capturedBitmap.value?.let { bitmap ->
        CapturedImageDialog(
            bitmap = bitmap,
            onClose = {
                capturedBitmap.value = null
                status.value = "Long press on any image to capture"
            },
            onUseImage = { selectedBitmap ->
                Toast.makeText(context, "Image selected!", Toast.LENGTH_SHORT).show()
                viewModel.setSelectedImage(ImageSourceType.BitMap(selectedBitmap))
                navController.navigate(Screen.DrawOptionRoute.route)
            }
        )
    }
}

