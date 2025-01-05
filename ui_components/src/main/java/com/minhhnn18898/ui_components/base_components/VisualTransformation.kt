package com.minhhnn18898.ui_components.base_components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.minhhnn18898.core.utils.formatWithCommas

class NumberCommaTransformation : VisualTransformation {
    private val emptyHint = ""

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = AnnotatedString(
                if(text.isEmpty()) {
                    emptyHint
                } else {
                    text.text.toLongOrNull().formatWithCommas()
                }
            ),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return if(text.isEmpty()) emptyHint.length else text.text.toLongOrNull().formatWithCommas().length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return text.length
                }
            }
        )
    }
}