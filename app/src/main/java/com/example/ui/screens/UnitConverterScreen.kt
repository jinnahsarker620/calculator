package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UnitCategory
import com.example.ui.components.ButtonType
import com.example.ui.components.RoundCalcButton
import com.example.ui.theme.CalcBackground
import com.example.ui.theme.CalcSurface
import com.example.ui.theme.CalcSurfaceVariant
import com.example.ui.theme.DisplayTextPrimary
import com.example.ui.theme.DisplayTextSecondary
import com.example.ui.theme.KeyEqualsBackground
import com.example.viewmodel.UnitConverterViewModel

@Composable
fun UnitConverterScreen(
    viewModel: UnitConverterViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }

    val convertedValue = viewModel.calculateConversion()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalcBackground)
            .padding(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Section: Category chips, compact conversion card, and overview
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Category Chips (Length, Weight, Temperature)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                UnitCategory.values().forEach { cat ->
                    val isSelected = state.category == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategory(cat) },
                        label = {
                            Text(
                                text = cat.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF003354) else DisplayTextSecondary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KeyEqualsBackground,
                            containerColor = CalcSurfaceVariant
                        ),
                        border = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .testTag("chip_cat_${cat.name.lowercase()}")
                    )
                }
            }

            // Compact Conversion Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CalcSurface)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // "From" Unit Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CalcSurfaceVariant)
                                    .clickable { showFromMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("btn_from_unit"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${state.fromUnit.name} (${state.fromUnit.symbol})",
                                    color = DisplayTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select from unit",
                                    tint = DisplayTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showFromMenu,
                                onDismissRequest = { showFromMenu = false },
                                modifier = Modifier.background(CalcSurfaceVariant)
                            ) {
                                state.availableUnits.forEach { unit ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${unit.name} (${unit.symbol})",
                                                color = DisplayTextPrimary,
                                                fontSize = 13.sp
                                            )
                                        },
                                        onClick = {
                                            viewModel.setFromUnit(unit)
                                            showFromMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Input Value
                        Text(
                            text = state.inputValue.ifEmpty { "0" },
                            color = DisplayTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Light,
                            maxLines = 1,
                            modifier = Modifier.testTag("text_input_unit_val")
                        )
                    }

                    // Swap Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF2C2F36)
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(KeyEqualsBackground)
                                .clickable { viewModel.swapUnits() }
                                .testTag("btn_swap_units"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap units",
                                tint = Color(0xFF003354),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF2C2F36)
                        )
                    }

                    // "To" Unit Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CalcSurfaceVariant)
                                    .clickable { showToMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("btn_to_unit"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${state.toUnit.name} (${state.toUnit.symbol})",
                                    color = DisplayTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select target unit",
                                    tint = DisplayTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showToMenu,
                                onDismissRequest = { showToMenu = false },
                                modifier = Modifier.background(CalcSurfaceVariant)
                            ) {
                                state.availableUnits.forEach { unit ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${unit.name} (${unit.symbol})",
                                                color = DisplayTextPrimary,
                                                fontSize = 13.sp
                                            )
                                        },
                                        onClick = {
                                            viewModel.setToUnit(unit)
                                            showToMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Converted Output
                        Text(
                            text = viewModel.formatNumber(convertedValue),
                            color = KeyEqualsBackground,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            modifier = Modifier.testTag("text_output_unit_val")
                        )
                    }
                }
            }

            // Compact Overview Card: 100dp height with scroll
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CalcSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "All ${state.category.displayName} Conversions",
                        color = DisplayTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(3.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        state.availableUnits.forEach { unit ->
                            val conv = viewModel.calculateConversion(unit)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${unit.name} (${unit.symbol})",
                                    color = DisplayTextSecondary,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "${viewModel.formatNumber(conv)} ${unit.symbol}",
                                    color = if (unit == state.toUnit) KeyEqualsBackground else DisplayTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (unit == state.toUnit) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Round Numeric Keypad for Unit Converter: all rows fully visible at once
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val keySize = 46.dp

            // Row 1: 7, 8, 9, AC
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "7", onClick = { viewModel.onDigit("7") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "8", onClick = { viewModel.onDigit("8") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "9", onClick = { viewModel.onDigit("9") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "AC",
                        onClick = { viewModel.onClear() },
                        type = ButtonType.CLEAR,
                        buttonSize = keySize,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Row 2: 4, 5, 6, ⌫
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "4", onClick = { viewModel.onDigit("4") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "5", onClick = { viewModel.onDigit("5") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "6", onClick = { viewModel.onDigit("6") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "⌫",
                        onClick = { viewModel.onBackspace() },
                        type = ButtonType.CLEAR,
                        buttonSize = keySize,
                        fontSize = 18.sp
                    )
                }
            }

            // Row 3: 1, 2, 3, +/- (or 00)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "1", onClick = { viewModel.onDigit("1") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "2", onClick = { viewModel.onDigit("2") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "3", onClick = { viewModel.onDigit("3") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (state.category == UnitCategory.TEMPERATURE) {
                        RoundCalcButton(
                            text = "±",
                            onClick = { viewModel.onToggleSign() },
                            type = ButtonType.OPERATOR,
                            buttonSize = keySize,
                            fontSize = 20.sp
                        )
                    } else {
                        RoundCalcButton(
                            text = "00",
                            onClick = { viewModel.onDigit("00") },
                            buttonSize = keySize,
                            fontSize = 17.sp
                        )
                    }
                }
            }

            // Row 4: 0, .
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(text = "0", onClick = { viewModel.onDigit("0") }, buttonSize = keySize)
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = ".",
                        onClick = { viewModel.onDot() },
                        buttonSize = keySize,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {}
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {}
            }
        }
    }
}
