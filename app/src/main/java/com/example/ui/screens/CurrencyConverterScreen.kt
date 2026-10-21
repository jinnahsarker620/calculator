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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.model.SupportedCurrencies
import com.example.ui.components.ButtonType
import com.example.ui.components.RoundCalcButton
import com.example.ui.theme.CalcBackground
import com.example.ui.theme.CalcSurface
import com.example.ui.theme.CalcSurfaceVariant
import com.example.ui.theme.DisplayTextPrimary
import com.example.ui.theme.DisplayTextSecondary
import com.example.ui.theme.KeyEqualsBackground
import com.example.ui.theme.TakaGreen
import com.example.viewmodel.CurrencyViewModel

@Composable
fun CurrencyConverterScreen(
    viewModel: CurrencyViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }

    val convertedValue = viewModel.calculateConvertedAmount()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalcBackground)
            .padding(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Section: Header, Conversion Card, Incentive Toggle, Chips, and Small Multi-Currency section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header: Title and Refresh Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BDT Currency Converter",
                        color = DisplayTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = state.lastUpdated,
                        color = DisplayTextSecondary,
                        fontSize = 10.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshRates() },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_refresh_rates")
                ) {
                    if (state.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = KeyEqualsBackground,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh exchange rates",
                            tint = KeyEqualsBackground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Compact Conversion Card (From & To)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CalcSurface)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Source Currency Row
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
                                    .testTag("btn_from_currency"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = state.fromCurrency.flag, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = state.fromCurrency.code,
                                    color = DisplayTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select source currency",
                                    tint = DisplayTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showFromMenu,
                                onDismissRequest = { showFromMenu = false },
                                modifier = Modifier.background(CalcSurfaceVariant)
                            ) {
                                SupportedCurrencies.forEach { curr ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${curr.flag}  ${curr.code} - ${curr.name}",
                                                color = DisplayTextPrimary,
                                                fontSize = 13.sp
                                            )
                                        },
                                        onClick = {
                                            viewModel.setFromCurrency(curr)
                                            showFromMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Amount Display
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${state.fromCurrency.symbol} ${state.amountString.ifEmpty { "0" }}",
                                color = DisplayTextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Light,
                                maxLines = 1,
                                modifier = Modifier.testTag("text_from_amount")
                            )
                        }
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
                                .clickable { viewModel.swapCurrencies() }
                                .testTag("btn_swap_currencies"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap currencies",
                                tint = Color(0xFF003354),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF2C2F36)
                        )
                    }

                    // Target Currency Row
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
                                    .testTag("btn_to_currency"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = state.toCurrency.flag, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = state.toCurrency.code,
                                    color = DisplayTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select target currency",
                                    tint = DisplayTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showToMenu,
                                onDismissRequest = { showToMenu = false },
                                modifier = Modifier.background(CalcSurfaceVariant)
                            ) {
                                SupportedCurrencies.forEach { curr ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${curr.flag}  ${curr.code} - ${curr.name}",
                                                color = DisplayTextPrimary,
                                                fontSize = 13.sp
                                            )
                                        },
                                        onClick = {
                                            viewModel.setToCurrency(curr)
                                            showToMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Converted Amount Display
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${state.toCurrency.symbol} ${viewModel.formatValue(convertedValue)}",
                                color = KeyEqualsBackground,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                modifier = Modifier.testTag("text_to_amount")
                            )
                        }
                    }
                }
            }

            // Compact Remittance Incentive Toggle & Quick Chips in one neat row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 2.5% Incentive
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B261D))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2.5% BD Bonus",
                        color = TakaGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = state.includeIncentive,
                        onCheckedChange = { viewModel.toggleIncentive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TakaGreen
                        ),
                        modifier = Modifier
                            .size(width = 36.dp, height = 22.dp)
                            .testTag("switch_incentive")
                    )
                }

                // Quick Chips: +500, +1000, +5000
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(1000, 5000, 10000).forEach { amt ->
                        SuggestionChip(
                            onClick = { viewModel.setAmount(amt.toString()) },
                            label = {
                                Text(
                                    text = "+$amt",
                                    color = DisplayTextPrimary,
                                    fontSize = 10.sp
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = CalcSurfaceVariant
                            ),
                            border = null,
                            modifier = Modifier.height(26.dp)
                        )
                    }
                }
            }

            // SMALL BDT Multi-Currency Section: exactly 120dp height, showing 2 currencies with scroll, 50% reduced size & padding
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CalcSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "BDT Multi-Currency Live Rates",
                        color = DisplayTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(3.dp))

                    val bdtAmount = if (state.fromCurrency.code == "BDT") {
                        state.amountString.toDoubleOrNull() ?: 1.0
                    } else {
                        val input = state.amountString.toDoubleOrNull() ?: 1.0
                        val rate = state.rates[state.fromCurrency.code] ?: state.fromCurrency.bdtRate
                        input * rate
                    }

                    // Scrollable list showing 2 currencies at a time with 50% reduced text and item height
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        SupportedCurrencies.filter { it.code != "BDT" }.forEach { curr ->
                            val targetRate = state.rates[curr.code] ?: curr.bdtRate
                            val valueInCurr = if (targetRate > 0) bdtAmount / targetRate else 0.0

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = curr.flag, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${curr.code} (${curr.name})",
                                        color = DisplayTextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = "${curr.symbol} ${viewModel.formatValue(valueInCurr)}",
                                    color = DisplayTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Round Numeric Keypad for Currency Converter: 7 8 9 AC, 4 5 6 ⌫, 1 2 3 00, 0 . (fully visible at once!)
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

            // Row 3: 1, 2, 3, 00
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
                    RoundCalcButton(
                        text = "00",
                        onClick = { viewModel.onDigit("00") },
                        buttonSize = keySize,
                        fontSize = 17.sp
                    )
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
