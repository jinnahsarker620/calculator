package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KeyDarkGray
import com.example.ui.theme.KeyDarkGrayActive
import com.example.ui.theme.KeyDarkGrayLight
import com.example.ui.theme.KeyTextAccent
import com.example.ui.theme.KeyTextClear
import com.example.ui.theme.KeyTextPrimary
import com.example.ui.theme.KeyTextSecondary

enum class ButtonType {
    NUMBER,
    OPERATOR,
    EQUALS,
    CLEAR,
    SCIENTIFIC,
    ACTIVE_TOGGLE
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoundCalcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    type: ButtonType = ButtonType.NUMBER,
    buttonSize: Dp = 52.dp,
    testTag: String = "btn_$text",
    contentDesc: String = text,
    fontSize: TextUnit = 20.sp,
    fontWeight: FontWeight = FontWeight.Medium
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.90f else 1.0f, label = "button_scale")

    // All buttons in dark gray, with distinct subtle functional tones
    val backgroundColor = when (type) {
        ButtonType.NUMBER -> KeyDarkGray
        ButtonType.OPERATOR -> KeyDarkGrayLight
        ButtonType.EQUALS -> KeyDarkGrayActive
        ButtonType.CLEAR -> KeyDarkGray
        ButtonType.SCIENTIFIC -> KeyDarkGray
        ButtonType.ACTIVE_TOGGLE -> KeyDarkGrayActive
    }

    val contentColor = when (type) {
        ButtonType.NUMBER -> KeyTextPrimary
        ButtonType.OPERATOR -> KeyTextAccent
        ButtonType.EQUALS -> KeyTextAccent
        ButtonType.CLEAR -> KeyTextClear
        ButtonType.SCIENTIFIC -> KeyTextSecondary
        ButtonType.ACTIVE_TOGGLE -> KeyTextAccent
    }

    Box(
        modifier = modifier
            .size(buttonSize)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(bounded = true, color = contentColor),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
                onLongClick = onLongClick?.let {
                    {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        it()
                    }
                }
            )
            .testTag(testTag)
            .semantics { contentDescription = contentDesc },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(2.dp)
        )
    }
}
