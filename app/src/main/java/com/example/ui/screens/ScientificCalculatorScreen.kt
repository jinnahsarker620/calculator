package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ButtonType
import com.example.ui.components.CalcDisplay
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.RoundCalcButton
import com.example.ui.theme.CalcBackground
import com.example.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScientificCalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showHistory by remember { mutableStateOf(false) }

    val btnSize: Dp = 48.dp

    if (showHistory) {
        HistoryBottomSheet(
            history = state.history,
            sheetState = sheetState,
            onDismiss = { showHistory = false },
            onSelectItem = { item -> viewModel.selectHistoryItem(item) },
            onClearHistory = { viewModel.clearHistory() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalcBackground)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Area (Expression & Live Result)
        CalcDisplay(
            expression = state.expression,
            previewResult = state.previewResult,
            isRadian = state.isRadian,
            isInverse = state.isInverse,
            historyCount = state.history.size,
            onHistoryClick = {
                showHistory = true
                coroutineScope.launch { sheetState.show() }
            },
            onToggleAngle = { viewModel.toggleAngleMode() },
            errorMessage = state.errorMessage,
            modifier = Modifier.weight(1f)
        )

        // Keypad Section: All buttons small, round, equal size in dark gray
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Scientific Functions Row 1: Rad, Inv, sin, cos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = if (state.isRadian) "RAD" else "DEG",
                        onClick = { viewModel.toggleAngleMode() },
                        type = if (state.isRadian) ButtonType.SCIENTIFIC else ButtonType.ACTIVE_TOGGLE,
                        buttonSize = btnSize,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        testTag = "btn_rad"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "INV",
                        onClick = { viewModel.toggleInverse() },
                        type = if (state.isInverse) ButtonType.ACTIVE_TOGGLE else ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        testTag = "btn_inv"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = if (state.isInverse) "sin⁻¹" else "sin",
                        onClick = { viewModel.onFunction("sin") },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 14.sp,
                        testTag = "btn_sin"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = if (state.isInverse) "cos⁻¹" else "cos",
                        onClick = { viewModel.onFunction("cos") },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 14.sp,
                        testTag = "btn_cos"
                    )
                }
            }

            // Scientific Functions Row 2: tan, ln, log, sqrt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = if (state.isInverse) "tan⁻¹" else "tan",
                        onClick = { viewModel.onFunction("tan") },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 14.sp,
                        testTag = "btn_tan"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = if (state.isInverse) "eˣ" else "ln",
                        onClick = { viewModel.onFunction("ln") },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 15.sp,
                        testTag = "btn_ln"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = if (state.isInverse) "10ˣ" else "log",
                        onClick = { viewModel.onFunction("log") },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 15.sp,
                        testTag = "btn_log"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = if (state.isInverse) "x²" else "√",
                        onClick = { viewModel.onFunction("sqrt") },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 18.sp,
                        testTag = "btn_sqrt"
                    )
                }
            }

            // Scientific Functions Row 3: pi, e, ^, factorial
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "π",
                        onClick = { viewModel.onConstant("π") },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 18.sp,
                        testTag = "btn_pi"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "e",
                        onClick = { viewModel.onConstant("e") },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 17.sp,
                        testTag = "btn_e"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "^",
                        onClick = { viewModel.onPower() },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 18.sp,
                        testTag = "btn_power"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "!",
                        onClick = { viewModel.onFactorial() },
                        type = ButtonType.SCIENTIFIC,
                        buttonSize = btnSize,
                        fontSize = 18.sp,
                        testTag = "btn_factorial"
                    )
                }
            }

            // Utilities & Divide Row: AC, (), %, divide
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "AC",
                        onClick = { viewModel.onClear() },
                        type = ButtonType.CLEAR,
                        buttonSize = btnSize,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        testTag = "btn_ac"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "( )",
                        onClick = { viewModel.onParenthesis() },
                        type = ButtonType.OPERATOR,
                        buttonSize = btnSize,
                        fontSize = 16.sp,
                        testTag = "btn_paren"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "%",
                        onClick = { viewModel.onPercent() },
                        type = ButtonType.OPERATOR,
                        buttonSize = btnSize,
                        fontSize = 18.sp,
                        testTag = "btn_percent"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "÷",
                        onClick = { viewModel.onOperator("÷") },
                        type = ButtonType.OPERATOR,
                        buttonSize = btnSize,
                        fontSize = 24.sp,
                        testTag = "btn_divide"
                    )
                }
            }

            // Numbers 7-9 & Multiply
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "7",
                        onClick = { viewModel.onDigit("7") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_7"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "8",
                        onClick = { viewModel.onDigit("8") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_8"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "9",
                        onClick = { viewModel.onDigit("9") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_9"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "×",
                        onClick = { viewModel.onOperator("×") },
                        type = ButtonType.OPERATOR,
                        buttonSize = btnSize,
                        fontSize = 24.sp,
                        testTag = "btn_multiply"
                    )
                }
            }

            // Numbers 4-6 & Minus
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "4",
                        onClick = { viewModel.onDigit("4") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_4"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "5",
                        onClick = { viewModel.onDigit("5") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_5"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "6",
                        onClick = { viewModel.onDigit("6") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_6"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "−",
                        onClick = { viewModel.onOperator("−") },
                        type = ButtonType.OPERATOR,
                        buttonSize = btnSize,
                        fontSize = 24.sp,
                        testTag = "btn_minus"
                    )
                }
            }

            // Numbers 1-3 & Plus
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "1",
                        onClick = { viewModel.onDigit("1") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_1"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "2",
                        onClick = { viewModel.onDigit("2") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_2"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "3",
                        onClick = { viewModel.onDigit("3") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_3"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "+",
                        onClick = { viewModel.onOperator("+") },
                        type = ButtonType.OPERATOR,
                        buttonSize = btnSize,
                        fontSize = 24.sp,
                        testTag = "btn_plus"
                    )
                }
            }

            // Bottom Row: Exactly 0  .  ⌫  =  (4 buttons equal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "0",
                        onClick = { viewModel.onDigit("0") },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        testTag = "btn_0"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = ".",
                        onClick = { viewModel.onDot() },
                        type = ButtonType.NUMBER,
                        buttonSize = btnSize,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        testTag = "btn_dot"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "⌫",
                        onClick = { viewModel.onBackspace() },
                        onLongClick = { viewModel.onClear() },
                        type = ButtonType.CLEAR,
                        buttonSize = btnSize,
                        fontSize = 18.sp,
                        testTag = "btn_backspace"
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    RoundCalcButton(
                        text = "=",
                        onClick = { viewModel.onEquals() },
                        type = ButtonType.EQUALS,
                        buttonSize = btnSize,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        testTag = "btn_equals"
                    )
                }
            }
        }
    }
}
