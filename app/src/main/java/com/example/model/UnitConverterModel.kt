package com.example.model

enum class UnitCategory(val displayName: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature")
}

data class ConversionUnit(
    val name: String,
    val symbol: String,
    val toBaseMultiplier: Double = 1.0 // for linear units
)

object UnitDefinitions {
    val lengthUnits = listOf(
        ConversionUnit("Meter", "m", 1.0),
        ConversionUnit("Kilometer", "km", 1000.0),
        ConversionUnit("Centimeter", "cm", 0.01),
        ConversionUnit("Millimeter", "mm", 0.001),
        ConversionUnit("Mile", "mi", 1609.344),
        ConversionUnit("Yard", "yd", 0.9144),
        ConversionUnit("Foot", "ft", 0.3048),
        ConversionUnit("Inch", "in", 0.0254)
    )

    val weightUnits = listOf(
        ConversionUnit("Kilogram", "kg", 1.0),
        ConversionUnit("Gram", "g", 0.001),
        ConversionUnit("Milligram", "mg", 0.000001),
        ConversionUnit("Pound", "lb", 0.45359237),
        ConversionUnit("Ounce", "oz", 0.028349523125),
        ConversionUnit("Metric Ton", "t", 1000.0),
        ConversionUnit("Stone", "st", 6.35029318)
    )

    val tempUnits = listOf(
        ConversionUnit("Celsius", "°C"),
        ConversionUnit("Fahrenheit", "°F"),
        ConversionUnit("Kelvin", "K")
    )
}
