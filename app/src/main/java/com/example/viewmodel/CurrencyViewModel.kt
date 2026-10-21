package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Currency
import com.example.model.SupportedCurrencies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class CurrencyUiState(
    val amountString: String = "1000",
    val fromCurrency: Currency = SupportedCurrencies[0], // BDT
    val toCurrency: Currency = SupportedCurrencies[1],   // USD
    val rates: Map<String, Double> = SupportedCurrencies.associate { it.code to it.bdtRate },
    val includeIncentive: Boolean = false, // 2.5% BD Remittance Incentive
    val isRefreshing: Boolean = false,
    val lastUpdated: String = "Benchmark Rates",
    val message: String? = null
)

class CurrencyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val cur = if (state.amountString == "0") "" else state.amountString
            if (cur.length >= 12) return@update state
            state.copy(amountString = cur + digit)
        }
    }

    fun onDot() {
        _uiState.update { state ->
            if (!state.amountString.contains('.')) {
                val cur = if (state.amountString.isEmpty()) "0." else state.amountString + "."
                state.copy(amountString = cur)
            } else state
        }
    }

    fun onBackspace() {
        _uiState.update { state ->
            if (state.amountString.isNotEmpty()) {
                val next = state.amountString.dropLast(1)
                state.copy(amountString = if (next.isEmpty()) "0" else next)
            } else {
                state.copy(amountString = "0")
            }
        }
    }

    fun onClear() {
        _uiState.update { it.copy(amountString = "0") }
    }

    fun setAmount(newAmount: String) {
        _uiState.update { it.copy(amountString = newAmount) }
    }

    fun setFromCurrency(currency: Currency) {
        _uiState.update { state ->
            if (currency.code == state.toCurrency.code) {
                state.copy(fromCurrency = currency, toCurrency = state.fromCurrency)
            } else {
                state.copy(fromCurrency = currency)
            }
        }
    }

    fun setToCurrency(currency: Currency) {
        _uiState.update { state ->
            if (currency.code == state.fromCurrency.code) {
                state.copy(toCurrency = currency, fromCurrency = state.toCurrency)
            } else {
                state.copy(toCurrency = currency)
            }
        }
    }

    fun swapCurrencies() {
        _uiState.update { state ->
            state.copy(
                fromCurrency = state.toCurrency,
                toCurrency = state.fromCurrency
            )
        }
    }

    fun toggleIncentive() {
        _uiState.update { it.copy(includeIncentive = !it.includeIncentive) }
    }

    fun calculateConvertedAmount(): Double {
        val state = _uiState.value
        val amount = state.amountString.toDoubleOrNull() ?: 0.0
        val fromRate = state.rates[state.fromCurrency.code] ?: state.fromCurrency.bdtRate
        val toRate = state.rates[state.toCurrency.code] ?: state.toCurrency.bdtRate

        // Convert from source to BDT
        val amountInBdt = amount * fromRate

        // Apply 2.5% incentive if converting foreign currency to BDT with incentive enabled
        val finalBdt = if (state.includeIncentive && state.fromCurrency.code != "BDT" && state.toCurrency.code == "BDT") {
            amountInBdt * 1.025
        } else {
            amountInBdt
        }

        // Convert from BDT to target
        return if (toRate > 0) finalBdt / toRate else 0.0
    }

    fun formatValue(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat("#,##0.00", symbols)
        return df.format(value)
    }

    fun refreshRates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, message = null) }
            try {
                withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url("https://open.er-api.com/v6/latest/USD")
                        .build()

                    httpClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val bodyStr = response.body?.string()
                            if (bodyStr != null) {
                                val json = JSONObject(bodyStr)
                                val ratesJson = json.getJSONObject("rates")
                                val usdToBdt = ratesJson.optDouble("BDT", 121.50)

                                val updatedMap = mutableMapOf<String, Double>()
                                updatedMap["BDT"] = 1.0
                                updatedMap["USD"] = usdToBdt

                                for (curr in SupportedCurrencies) {
                                    if (curr.code != "BDT" && curr.code != "USD") {
                                        val usdToCurr = ratesJson.optDouble(curr.code, 0.0)
                                        if (usdToCurr > 0) {
                                            // 1 curr = (usdToBdt / usdToCurr) BDT
                                            updatedMap[curr.code] = usdToBdt / usdToCurr
                                        } else {
                                            updatedMap[curr.code] = curr.bdtRate
                                        }
                                    }
                                }

                                val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                _uiState.update {
                                    it.copy(
                                        rates = updatedMap,
                                        isRefreshing = false,
                                        lastUpdated = "Updated at $timeStr",
                                        message = "Rates updated successfully"
                                    )
                                }
                                return@withContext
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Offline fallback - safe and clean
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        message = "Using benchmark rates (Offline)"
                    )
                }
            }
        }
    }
}
