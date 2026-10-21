package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.ConversionUnit
import com.example.model.UnitCategory
import com.example.model.UnitDefinitions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

data class UnitConverterUiState(
    val category: UnitCategory = UnitCategory.LENGTH,
    val inputValue: String = "1",
    val fromUnit: ConversionUnit = UnitDefinitions.lengthUnits[0],
    val toUnit: ConversionUnit = UnitDefinitions.lengthUnits[1],
    val availableUnits: List<ConversionUnit> = UnitDefinitions.lengthUnits
)

class UnitConverterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UnitConverterUiState())
    val uiState: StateFlow<UnitConverterUiState> = _uiState.asStateFlow()

    fun setCategory(category: UnitCategory) {
        val units = when (category) {
            UnitCategory.LENGTH -> UnitDefinitions.lengthUnits
            UnitCategory.WEIGHT -> UnitDefinitions.weightUnits
            UnitCategory.TEMPERATURE -> UnitDefinitions.tempUnits
        }
        _uiState.update {
            it.copy(
                category = category,
                availableUnits = units,
                fromUnit = units[0],
                toUnit = if (units.size > 1) units[1] else units[0]
            )
        }
    }

    fun setFromUnit(unit: ConversionUnit) {
        _uiState.update { state ->
            if (unit.name == state.toUnit.name) {
                state.copy(fromUnit = unit, toUnit = state.fromUnit)
            } else {
                state.copy(fromUnit = unit)
            }
        }
    }

    fun setToUnit(unit: ConversionUnit) {
        _uiState.update { state ->
            if (unit.name == state.fromUnit.name) {
                state.copy(toUnit = unit, fromUnit = state.toUnit)
            } else {
                state.copy(toUnit = unit)
            }
        }
    }

    fun swapUnits() {
        _uiState.update { state ->
            state.copy(fromUnit = state.toUnit, toUnit = state.fromUnit)
        }
    }

    fun onDigit(d: String) {
        _uiState.update { state ->
            val cur = if (state.inputValue == "0") "" else state.inputValue
            if (cur.length >= 10) return@update state
            state.copy(inputValue = cur + d)
        }
    }

    fun onDot() {
        _uiState.update { state ->
            if (!state.inputValue.contains('.')) {
                val cur = if (state.inputValue.isEmpty()) "0." else state.inputValue + "."
                state.copy(inputValue = cur)
            } else state
        }
    }

    fun onBackspace() {
        _uiState.update { state ->
            if (state.inputValue.isNotEmpty()) {
                val next = state.inputValue.dropLast(1)
                state.copy(inputValue = if (next.isEmpty() || next == "-") "0" else next)
            } else {
                state.copy(inputValue = "0")
            }
        }
    }

    fun onToggleSign() {
        if (_uiState.value.category != UnitCategory.TEMPERATURE) return
        _uiState.update { state ->
            val cur = state.inputValue
            val toggled = if (cur.startsWith("-")) cur.drop(1) else "-$cur"
            state.copy(inputValue = if (toggled.isEmpty()) "0" else toggled)
        }
    }

    fun onClear() {
        _uiState.update { it.copy(inputValue = "0") }
    }

    fun calculateConversion(targetUnit: ConversionUnit = _uiState.value.toUnit): Double {
        val state = _uiState.value
        val input = state.inputValue.toDoubleOrNull() ?: 0.0

        return if (state.category == UnitCategory.TEMPERATURE) {
            convertTemperature(input, state.fromUnit.name, targetUnit.name)
        } else {
            // Linear base conversion
            val baseValue = input * state.fromUnit.toBaseMultiplier
            if (targetUnit.toBaseMultiplier > 0) baseValue / targetUnit.toBaseMultiplier else 0.0
        }
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        if (from == to) return value
        // Convert to Celsius first
        val celsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32.0) * 5.0 / 9.0
            "Kelvin" -> value - 273.15
            else -> value
        }
        // Convert Celsius to target
        return when (to) {
            "Celsius" -> celsius
            "Fahrenheit" -> (celsius * 9.0 / 5.0) + 32.0
            "Kelvin" -> celsius + 273.15
            else -> celsius
        }
    }

    fun formatNumber(value: Double): String {
        if (value == 0.0 || value == -0.0) return "0"
        val absVal = abs(value)
        val symbols = DecimalFormatSymbols(Locale.US)
        return if (absVal >= 1e8 || (absVal < 1e-4 && absVal > 0.0)) {
            val df = DecimalFormat("0.####E0", symbols)
            df.format(value).replace("E", "e")
        } else {
            val df = DecimalFormat("#,##0.######", symbols)
            df.format(value)
        }
    }
}
