package com.example.model

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String,
    val bdtRate: Double // 1 unit of this currency in BDT
)

val SupportedCurrencies = listOf(
    Currency("BDT", "Bangladeshi Taka", "৳", "🇧🇩", 1.0),
    Currency("USD", "US Dollar", "$", "🇺🇸", 121.50),
    Currency("EUR", "Euro", "€", "🇪🇺", 131.25),
    Currency("SAR", "Saudi Riyal", "﷼", "🇸🇦", 32.40),
    Currency("AED", "UAE Dirham", "د.إ", "🇦🇪", 33.08),
    Currency("INR", "Indian Rupee", "₹", "🇮🇳", 1.45),
    Currency("MYR", "Malaysian Ringgit", "RM", "🇲🇾", 27.60)
)
