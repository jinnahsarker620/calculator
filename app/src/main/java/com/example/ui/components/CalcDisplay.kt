package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DisplayTextPrimary
import com.example.ui.theme.DisplayTextSecondary
import com.example.ui.theme.KeyEqualsBackground

@Composable
fun CalcDisplay(
    expression: String,
    previewResult: String,
    isRadian: Boolean,
    isInverse: Boolean,
    historyCount: Int,
    onHistoryClick: () -> Unit,
    onToggleAngle: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Automatically scroll to the end as the user types
    LaunchedEffect(expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Top Toolbar: History button, DEG/RAD badge, INV badge, Copy button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // History Button with badge
                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier.testTag("btn_history")
                ) {
                    if (historyCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = KeyEqualsBackground) {
                                    Text(
                                        text = if (historyCount > 99) "99+" else "$historyCount",
                                        color = Color(0xFF003354),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Calculation history",
                                tint = DisplayTextSecondary
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Calculation history",
                            tint = DisplayTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // DEG / RAD Pill
                SuggestionChip(
                    onClick = onToggleAngle,
                    label = {
                        Text(
                            text = if (isRadian) "RAD" else "DEG",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KeyEqualsBackground
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFF1E2430)
                    ),
                    border = null,
                    modifier = Modifier.testTag("chip_angle_mode")
                )

                if (isInverse) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF004F6E), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "INV",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7FCFFF)
                        )
                    }
                }
            }

            // Copy to clipboard button
            if (expression.isNotEmpty() || previewResult.isNotEmpty()) {
                IconButton(
                    onClick = {
                        val textToCopy = previewResult.ifEmpty { expression }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Calculator Result", textToCopy)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied $textToCopy", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("btn_copy")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy result",
                        tint = DisplayTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Expression Text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayText = if (expression.isEmpty()) "0" else expression
            val fontSize = when {
                displayText.length > 20 -> 32.sp
                displayText.length > 12 -> 42.sp
                else -> 52.sp
            }

            Text(
                text = displayText,
                color = DisplayTextPrimary,
                fontSize = fontSize,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.testTag("text_expression")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Preview Result or Error Message
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            this@Column.AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("text_error")
                )
            }

            this@Column.AnimatedVisibility(
                visible = errorMessage == null && previewResult.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = previewResult,
                    color = DisplayTextSecondary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("text_preview_result")
                )
            }
        }
    }
}
