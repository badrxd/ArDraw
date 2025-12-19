package com.badr1.ardraw.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.badr1.ardraw.R
import androidx.lifecycle.ViewModel


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
}
