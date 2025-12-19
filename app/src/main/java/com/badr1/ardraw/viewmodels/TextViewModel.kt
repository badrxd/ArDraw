package com.badr1.ardraw.viewmodels

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.badr1.ardraw.R
import androidx.lifecycle.ViewModel
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.core.graphics.createBitmap
import androidx.core.content.res.ResourcesCompat
import dagger.hilt.android.internal.Contexts


class TextViewModel() : ViewModel() {
    val fontStyles = listOf(
        R.font.font_1,
        R.font.font_2,
        R.font.font_3,
        R.font.font_4,
        R.font.font_5,
        R.font.font_6,
        R.font.font_7,
        R.font.font_8,
        R.font.font_9,
        R.font.font_10,
        R.font.font_11,
        R.font.font_12,
        R.font.font_13,
        R.font.font_14,
    )

    private val _selectedFont = mutableIntStateOf(fontStyles[0])
    val selectedFont: MutableState<Int> = _selectedFont

    private val _text = mutableStateOf("Ar Draw")
    val text: MutableState<String> = _text

    fun onChangeText(text: String) {
        _text.value = text
    }

    fun selectFont(font: Int) {
        _selectedFont.intValue = font
    }

    fun fontToBitmap(typeface: Typeface): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.textSize = 80f
            this.typeface = typeface
        }
        val bounds = Rect()
        paint.getTextBounds(_text.value, 0, _text.value.length, bounds)
        val bitmap = createBitmap(bounds.width().coerceAtLeast(1), bounds.height().coerceAtLeast(1))
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawText(
            _text.value,
            -bounds.left.toFloat(),
            -bounds.top.toFloat(),
            paint
        )

        return bitmap

    }
}
