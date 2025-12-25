package com.badr1.ardraw.viewmodels.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import com.badr1.ardraw.SUB_FOLDER_NAME
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Creates the OutputFileOptions for ImageCapture, using MediaStore for public storage,
 * and ensures the image is saved to a subfolder named 'ArDraw'.
 */
fun createPublicImageOutputOptions(context: Context): ImageCapture.OutputFileOptions {
    // The custom folder name
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(Date())
    val mimeType = "image/jpeg"

    // For API 29 (Android 10) and above: Use MediaStore and RELATIVE_PATH
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            // CRUCIAL CHANGE: Specify the subfolder here
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/$SUB_FOLDER_NAME"
            )
        }
        return ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()
    } else {
        // For older versions (pre-Android 10): Save directly to the File system
        // NOTE: This relies on the WRITE_EXTERNAL_STORAGE permission being granted.

        // 1. Get the public Pictures directory
        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        // 2. Create the 'ArDraw' subfolder if it doesn't exist
        val arDrawDir = File(picturesDir, SUB_FOLDER_NAME)
        if (!arDrawDir.exists()) {
            arDrawDir.mkdirs() // Creates the directory and any necessary parent directories
        }

        // 3. Create the file inside the new subfolder
        val file = File(arDrawDir, "$name.jpg")

        // 4. Return the file-based output options
        return ImageCapture.OutputFileOptions.Builder(file).build()
    }
}