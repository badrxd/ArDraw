package com.badr1.ardraw.screens.gallery

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.badr1.ardraw.R
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.screens.components.BannerAdView
import com.badr1.ardraw.screens.components.Header
import com.badr1.ardraw.screens.components.RequestPermissions
import com.badr1.ardraw.viewmodels.GalleryViewModel

private val ERROR_ID = R.drawable.outline_error_24


@Composable
fun GalleryScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: GalleryViewModel = viewModel()
) {
    val images by viewModel.images.collectAsState()
    val selectedImage by viewModel.selectedImage.collectAsState()
    val deleteIntentSender by viewModel.deleteIntentSender.collectAsState()

    var showImage by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    var hasPermissions by remember { mutableStateOf(false) }
    val context = LocalContext.current


    // ✅ Handle delete request with user confirmation
    val deleteResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // User confirmed deletion
            val image = selectedImage as? ImageSourceType.UriSourceType
            image?.let {
                viewModel.onDeleteConfirmed(it.uri)
            }
            Toast.makeText(
                context,
                "Image deleted",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // User cancelled
            Toast.makeText(
                context,
                "Delete cancelled",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.clearDeleteRequest()
        }
    }

    // ✅ Launch delete request when IntentSender is available
    LaunchedEffect(deleteIntentSender) {
        deleteIntentSender?.let { sender ->
            try {
                deleteResultLauncher.launch(
                    IntentSenderRequest.Builder(sender).build()
                )
            } catch (e: Exception) {
                Log.e("GalleryScreen", "Error launching delete intent", e)
                viewModel.clearDeleteRequest()
            }
        }
    }

    LaunchedEffect(hasPermissions) {
        if (hasPermissions) {
            viewModel.loadImagesFromFolder()
        }
    }

    if (!hasPermissions) {
        RequestPermissions(
            permissions = listOf("media"),
            onAllGranted = { hasPermissions = true },
            onPermissionDenied = { hasPermissions = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier.fillMaxSize()) {
            Header("Gallery", navController)
            Spacer(modifier.height(5.dp))

            Column(
                modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Images",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.W800
                )

                Spacer(modifier.height(8.dp))

                if (images.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No images found")
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(images) { image ->
                            GalleryImages(
                                image = ImageSourceType.UriSourceType(image),
                                onClick = { img ->
                                    viewModel.selectImage(img)
                                    showImage = true
                                },
                                onDialog = { img ->
                                    viewModel.selectImage(img)
                                    showDialog.value = true
                                }
                            )
                        }
                    }
                }
            }
        }
        BannerAdView(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }

    if (showImage) {
        ImageBoxCard(
            selectedImage as ImageSourceType.UriSourceType,
            modifier = modifier,
            onClose = { showImage = false }
        )
    }

    if (showDialog.value) {
        DeleteConfirmationScreen(
            onDismiss = {
                showDialog.value = false
            },
            onConfirm = {
                showDialog.value = false
                val image = selectedImage as? ImageSourceType.UriSourceType
                image?.let {
                    viewModel.deleteImage(it.uri) // This will trigger the delete flow
                }
            }
        )
    }

    BackHandler(enabled = showImage) {
        showImage = false
    }
}

//@Composable
//fun GalleryScreen(
//    modifier: Modifier, navController: NavController, viewModel: GalleryViewModel = viewModel()
//) {
//    val images by viewModel.images.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val selectedImage by viewModel.selectedImage.collectAsState()
//    var showImage: Boolean by remember { mutableStateOf(false) }
//    val showDialog = remember { mutableStateOf(false) }
//
//    val permissions = listOf("media")
//    val askPermissions = remember { mutableStateOf(false) }
//
////    LaunchedEffect(askPermissions) {
////    }
//
//    LaunchedEffect(askPermissions.value, images.size) {
//        viewModel.loadImagesFromFolder()
//    }
//
//    if (!askPermissions.value) {
//        RequestPermissions(permissions = permissions, onAllGranted = {
//            askPermissions.value = true
//        }, onPermissionDenied = {
//            askPermissions.value = false
//        })
//    }
//
//    Column(modifier.fillMaxSize()) {
//        Header("Gallery", navController)
//        Spacer(modifier.height(5.dp))
//        Column(
//            modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            Column(modifier.fillMaxWidth()) {
//                Text("Images", style = MaterialTheme.typography.h6, fontWeight = FontWeight.W800)
//            }
//            Spacer(modifier.height(8.dp))
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(3),
//                modifier = Modifier.fillMaxWidth(),
//                contentPadding = PaddingValues(8.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(images) { image ->
//                    GalleryImages(image = ImageSourceType.UriSourceType(image), onClick = { image ->
//                        viewModel.selectImage(image)
//                        showImage = true
//                    }, onDialog = { image ->
//                        viewModel.selectImage(image)
//                        showDialog.value = true
//                    })
//                }
//            }
//        }
//    }
//    if (showImage) {
//        ImageBoxCard(
//            selectedImage as ImageSourceType.UriSourceType, modifier = modifier, onClose = {
//                showImage = false
//            })
//    }
//
//    if (showDialog.value) {
//        DeleteConfirmationScreen(onDismiss = { showDialog.value = false }, onConfirm = {
//            val image = selectedImage as ImageSourceType.UriSourceType
//            Log.d("Deleting image: ", "deleting ${image.uri}")
//            viewModel.deleteImage(image.uri)
//        })
//    }
//
//    BackHandler(enabled = showImage) {}
//}
//
@Composable
fun DeleteConfirmationScreen(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    // 1. State to control the dialog visibility

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 2. The Trigger (Clickable Text or Button)

        // 3. The Dialog Logic
        AlertDialog(onDismissRequest = {
            // Called when user clicks outside the dialog or hits back
            onDismiss()
        }, title = {
            Text(text = "Confirm Deletion")
        }, text = {
            Text("Do you want to delete this image?")
        }, confirmButton = {
            TextButton(
                onClick = {
                    // TODO: Add your delete logic here
                    onDismiss()
                    onConfirm()
                }) {
                Text("Yes", color = Color.Red)
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }) {
                Text("No")
            }
        })
    }
}

@Composable
fun GalleryImages(
    image: ImageSourceType.UriSourceType,
    onClick: (ImageSourceType.UriSourceType) -> Unit,
    onDialog: (ImageSourceType.UriSourceType) -> Unit
) {
    AsyncImage(
        model = image.uri,
        contentDescription = "Gallery Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color.LightGray)
            .combinedClickable(
                onClick = {
                    // Standard single click
                    onClick(image)
                },
                onLongClick = {
                    // THIS IS YOUR HOLD CLICK
                    onDialog(image)
                },
            )
    )

}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ImageBoxCard(
    image: ImageSourceType.UriSourceType, modifier: Modifier = Modifier, onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Top
    ) {
        IconButton(
            onClick = onClose, modifier = Modifier
                .align(Alignment.End)
                .padding(4.dp)
//                .background(Color.White.copy(alpha = 0.8f), CircleShape) // White backing
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White // Dark icon on light background
            )
        }

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 8.dp, // Adds shadow
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(
                modifier.border(
                    width = 1.dp, color = Color.White, shape = RoundedCornerShape(16.dp)
                )
            ) {
                AsyncImage(
                    model = image.uri,
                    contentDescription = "Preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth() // Fixed height for consistency
                )

                // The Close Button (using IconButton for better touch target)
            }
        }
    }
}