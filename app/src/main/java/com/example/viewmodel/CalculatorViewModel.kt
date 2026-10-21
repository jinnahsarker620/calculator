package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.EvaluationResult
import com.example.engine.ExpressionEvaluator
import com.example.model.CalcHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String = "",
    val isEvaluated: Boolean = false,
    val isRadian: Boolean = true,
    val isInverse: Boolean = false,
    val isScientificExpanded: Boolean = true,
    val history: List<CalcHistoryItem> = emptyList(),
    val errorMessage: String? = null
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var evaluator = ExpressionEvaluator(isRadian = true)

    private fun updateEvaluator(isRadian: Boolean) {
        evaluator = ExpressionEvaluator(isRadian = isRadian)
        recalculatePreview(_uiState.value.expression)
    }

    private fun recalculatePreview(expr: String) {
        if (expr.isBlank()) {
            _uiState.update { it.copy(previewResult = "", errorMessage = null) }
            return
        }

        // Avoid previewing incomplete trailing operators
        val trimmed = expr.trimEnd('+', '−', '×', '÷', '^', '.')
        if (trimmed.isEmpty()) {
            _uiState.update { it.copy(previewResult = "", errorMessage = null) }
            return
        }

        when (val res = evaluator.evaluate(trimmed)) {
            is EvaluationResult.Success -> {
                _uiState.update { it.copy(previewResult = res.formattedResult, errorMessage = null) }
            }
            is EvaluationResult.Error -> {
                // If it's a syntax error during typing, keep preview quiet or show previous
                _uiState.update { it.copy(previewResult = "") }
            }
        }
    }

    fun onDigit(d: String) {
        _uiState.update { state ->
            val newExpr = if (state.isEvaluated) d else state.expression + d
            state.copy(expression = newExpr, isEvaluated = false, errorMessage = null)
        }
        recalculatePreview(_uiState.value.expression)
    }

    fun onDot() {
        _uiState.update { state ->
            val expr = if (state.isEvaluated) "0." else state.expression
            val tokens = expr.split(Regex("[+−×÷^()√ ]"))
            val lastToken = tokens.lastOrNull() ?: ""
            if (!lastToken.contains('.')) {
                val updated = if (lastToken.isEmpty() || !lastToken.last().isDigit()) {
                    "$expr 0."
                } else {
                    "$expr."
                }
                state.copy(expression = updated, isEvaluated = false, errorMessage = null)
            } else {
                state
            }
        }
        recalculatePreview(_uiState.value.expression)
    }

    fun onOperator(op: String) {
        _uiState.update { state ->
            var expr = state.expression
            if (expr.isEmpty()) {
                if (op == "−" || op == "-") {
                    return@update state.copy(expression = "−", isEvaluated = false, errorMessage = null)
                }
                return@update state
            }

            // If an operator is already at the end, replace it
            val lastChar = expr.last()
            if (lastChar == '+' || lastChar == '−' || lastChar == '×' || lastChar == '÷' || lastChar == '^') {
                expr = expr.dropLast(1) + op
            } else {
                expr += op
            }

            state.copy(expression = expr, isEvaluated = false, errorMessage = null)
        }
        recalculatePreview(_uiState.value.expression)
    }

    fun onFunction(funcName: String) {
        _uiState.update { state ->
            val funcCall = when {
                state.isInverse && funcName == "sin" -> "asin("
                state.isInverse && funcName == "cos" -> "acos("
                state.isInverse && funcName == "tan" -> "atan("
                state.isInverse && funcName == "ln" -> "e^("
                state.isInverse && funcName == "log" -> "10^("
                state.isInverse && funcName == "sqrt" -> "^2"
                funcName == "sqrt" -> "√("
                else -> "$funcName("
            }

            val base = if (state.isEvaluated) "" else state.expression
            val newExpr = base + funcCall
            state.copy(expression = newExpr, isEvaluated = false, errorMessage = null)
        }
        recalculatePreview(_uiState.value.expression)
    }

    fun onConstant(c: String) {
        _uiState.update { state ->
            val base = if (state.isEvaluated) "" else state.expression
            state.copy(expression = base + c, isEvaluated = false, errorMessage = null)
        }
        recalculatePreview(_uiState.value.expression)
    }

    fun onFactorial() {
        _uiState.update { state ->
            if (state.expression.isNotEmpty()) {
                state.copy(expression = state.expression + "!", isEvaluated = false, errorMessage = null)
            } else state
        }
        recalculatePreview(_uiState.value.expression)
    }

    fun onPercent() {
        _uiState.update { state ->
            if (state.expression.isNotEmpty()) {
                state.copy(expression = state.expression + "%", isEvaluated = false, errorMessage = null)
            } else state
        }
        recalculatePreview(_uiState.value.expression)
    }

    fun onParenthesis() {
        _uiState.update { state ->
            val expr = if (state.isEvaluated) "" else state.expression
            var openCount = 0
            for (ch in expr) {
                if (ch == '(') openCount++
                if (ch == ')') openCount--
            }

            val lastChar = expr.lastOrNull()
            val newExpr = if (openCount > 0 && lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e' || lastChar == '!')) {
                "$expr)"
            } else {
                "$expr("
            }
            state.copy(expression = newExpr, isEvaluated = false, errorMessage = null)
        }
        recalculatePreview(_uiState.value.expression)
    }

    fun onPower() {
        onOperator("^")
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                previewResult = "",
                isEvaluated = false,
                errorMessage = null
            )
        }
    }

    fun onBackspace() {
        _uiState.update { state ->
            if (state.isEvaluated) {
                state.copy(expression = "", previewResult = "", isEvaluated = false, errorMessage = null)
            } else if (state.expression.isNotEmpty()) {
                var expr = state.expression
                // Check if ending with multi-char functions like sin(, cos(, tan(, asin(, acos(, atan(, ln(, log(, sqrt(
                val multiCharEndings = listOf("asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "ln(", "log(", "√(")
                var removed = false
                for (ending in multiCharEndings) {
                    if (expr.endsWith(ending)) {
                        expr = expr.removeSuffix(ending)
                        removed = true
                        break
                    }
                }
                if (!removed) {
                    expr = expr.dropLast(1)
                }
                state.copy(expression = expr, errorMessage = null)
            } else {
                state
            }
        }
        recalculatePreview(_uiState.value.expression)
    }

    fun onEquals() {
        val state = _uiState.value
        val expr = state.expression.trim()
        if (expr.isBlank()) return

        when (val res = evaluator.evaluate(expr)) {
            is EvaluationResult.Success -> {
                val item = CalcHistoryItem(expression = expr, result = res.formattedResult)
                _uiState.update {
                    it.copy(
                        expression = res.formattedResult,
                        previewResult = "",
                        isEvaluated = true,
                        history = listOf(item) + it.history.take(49),
                        errorMessage = null
                    )
                }
            }
            is EvaluationResult.Error -> {
                _uiState.update { it.copy(errorMessage = res.message) }
            }
        }
    }

    fun toggleAngleMode() {
        _uiState.update {
            val newRad = !it.isRadian
            it.copy(isRadian = newRad)
        }
        updateEvaluator(_uiState.value.isRadian)
    }

    fun toggleInverse() {
        _uiState.update { it.copy(isInverse = !it.isInverse) }
    }

    fun toggleScientificExpanded() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun selectHistoryItem(item: CalcHistoryItem) {
        _uiState.update {
            it.copy(
                expression = item.expression,
                previewResult = item.result,
                isEvaluated = false,
                errorMessage = null
            )
        }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }
}
