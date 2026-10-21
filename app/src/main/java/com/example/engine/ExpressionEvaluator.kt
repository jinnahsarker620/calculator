package com.example.engine

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

class ExpressionEvaluator(private val isRadian: Boolean = true) {

    fun evaluate(rawExpression: String): EvaluationResult {
        if (rawExpression.isBlank()) {
            return EvaluationResult.Success(0.0, "0")
        }

        try {
            val sanitized = sanitize(rawExpression)
            val parser = Parser(sanitized, isRadian)
            val value = parser.parseExpression()

            if (value.isNaN()) {
                return EvaluationResult.Error("Undefined")
            }
            if (value.isInfinite()) {
                return EvaluationResult.Error(if (value > 0) "Infinity" else "-Infinity")
            }

            val formatted = formatResult(value)
            return EvaluationResult.Success(value, formatted)
        } catch (e: ArithmeticException) {
            return EvaluationResult.Error(e.message ?: "Math Error")
        } catch (e: Exception) {
            return EvaluationResult.Error("Format Error")
        }
    }

    private fun sanitize(expr: String): String {
        var s = expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("√", "sqrt")
            .replace("π", "PI")
            .replace(" ", "")

        // Handle implicit multiplication
        val sb = StringBuilder()
        var i = 0
        while (i < s.length) {
            val c = s[i]
            sb.append(c)

            if (i + 1 < s.length) {
                val next = s[i + 1]
                val currentIsDigitOrConst = c.isDigit() || c == '.' || c == ')' || c == '!' || c == '%'
                val nextStartsImplicit = next == '(' ||
                        s.substring(i + 1).startsWith("PI") ||
                        (next == 'e' && (i + 2 >= s.length || !s[i + 2].isLetter())) ||
                        s.substring(i + 1).startsWith("sin") ||
                        s.substring(i + 1).startsWith("cos") ||
                        s.substring(i + 1).startsWith("tan") ||
                        s.substring(i + 1).startsWith("asin") ||
                        s.substring(i + 1).startsWith("acos") ||
                        s.substring(i + 1).startsWith("atan") ||
                        s.substring(i + 1).startsWith("ln") ||
                        s.substring(i + 1).startsWith("log") ||
                        s.substring(i + 1).startsWith("sqrt")

                if (currentIsDigitOrConst && nextStartsImplicit) {
                    sb.append("*")
                } else if ((c == ')' || c == '!' || c == '%') && (next.isDigit() || next == '.')) {
                    sb.append("*")
                } else if ((c == 'e' && (i == 0 || !s[i - 1].isLetter())) && (next.isDigit() || next == '(')) {
                    sb.append("*")
                }
            }
            i++
        }

        var result = sb.toString()

        // Auto-close open parentheses
        var openCount = 0
        for (ch in result) {
            if (ch == '(') openCount++
            if (ch == ')') openCount--
        }
        if (openCount > 0) {
            result += ")".repeat(openCount)
        }

        return result
    }

    private fun formatResult(value: Double): String {
        // Fix floating precision inaccuracies like 0.1 + 0.2 = 0.30000000000000004
        val rounded = if (abs(value - round(value)) < 1e-11) {
            round(value)
        } else {
            value
        }

        if (rounded == 0.0 || rounded == -0.0) return "0"

        val absVal = abs(rounded)
        return if (absVal >= 1e12 || (absVal < 1e-6 && absVal > 0.0)) {
            val symbols = DecimalFormatSymbols(Locale.US)
            val df = DecimalFormat("0.######E0", symbols)
            df.format(rounded).replace("E", "e")
        } else {
            val symbols = DecimalFormatSymbols(Locale.US)
            val df = DecimalFormat("#,##0.##########", symbols)
            df.format(rounded)
        }
    }

    private class Parser(private val input: String, private val isRadian: Boolean) {
        private var pos = 0

        private fun peek(): Char = if (pos < input.length) input[pos] else '\u0000'
        private fun consume(): Char = if (pos < input.length) input[pos++] else '\u0000'

        fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                when (peek()) {
                    '+' -> {
                        consume()
                        value += parseTerm()
                    }
                    '-' -> {
                        consume()
                        value -= parseTerm()
                    }
                    else -> return value
                }
            }
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (true) {
                when (peek()) {
                    '*' -> {
                        consume()
                        value *= parseFactor()
                    }
                    '/' -> {
                        consume()
                        val denominator = parseFactor()
                        if (abs(denominator) < 1e-15) {
                            throw ArithmeticException("Cannot divide by 0")
                        }
                        value /= denominator
                    }
                    else -> return value
                }
            }
        }

        private fun parseFactor(): Double {
            var value = parseUnary()
            if (peek() == '^') {
                consume()
                val exponent = parseFactor() // Right-associative
                value = value.pow(exponent)
            }

            // Postfix operators like ! and %
            while (peek() == '!' || peek() == '%') {
                when (consume()) {
                    '!' -> value = factorial(value)
                    '%' -> value /= 100.0
                }
            }

            return value
        }

        private fun parseUnary(): Double {
            if (peek() == '+') {
                consume()
                return parseUnary()
            }
            if (peek() == '-') {
                consume()
                return -parseUnary()
            }
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            val c = peek()

            if (c == '(') {
                consume()
                val value = parseExpression()
                if (peek() == ')') {
                    consume()
                }
                return value
            }

            // Numbers
            if (c.isDigit() || c == '.') {
                val start = pos
                var hasDot = false
                while (pos < input.length) {
                    val curr = input[pos]
                    if (curr.isDigit()) {
                        pos++
                    } else if (curr == '.' && !hasDot) {
                        hasDot = true
                        pos++
                    } else {
                        break
                    }
                }
                val numStr = input.substring(start, pos)
                return numStr.toDoubleOrNull() ?: 0.0
            }

            // Constants & Functions
            if (c.isLetter()) {
                val start = pos
                while (pos < input.length && input[pos].isLetter()) {
                    pos++
                }
                val name = input.substring(start, pos)
                return when (name) {
                    "PI" -> Math.PI
                    "e" -> Math.E
                    "sin" -> {
                        val arg = parsePrimary()
                        val rad = if (isRadian) arg else Math.toRadians(arg)
                        sin(rad)
                    }
                    "cos" -> {
                        val arg = parsePrimary()
                        val rad = if (isRadian) arg else Math.toRadians(arg)
                        // Near zero adjustment for 90, 270 degrees
                        val result = cos(rad)
                        if (abs(result) < 1e-15) 0.0 else result
                    }
                    "tan" -> {
                        val arg = parsePrimary()
                        val rad = if (isRadian) arg else Math.toRadians(arg)
                        val cosVal = cos(rad)
                        if (abs(cosVal) < 1e-15) throw ArithmeticException("Undefined")
                        tan(rad)
                    }
                    "asin" -> {
                        val arg = parsePrimary()
                        if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error")
                        val rad = asin(arg)
                        if (isRadian) rad else Math.toDegrees(rad)
                    }
                    "acos" -> {
                        val arg = parsePrimary()
                        if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error")
                        val rad = acos(arg)
                        if (isRadian) rad else Math.toDegrees(rad)
                    }
                    "atan" -> {
                        val arg = parsePrimary()
                        val rad = atan(arg)
                        if (isRadian) rad else Math.toDegrees(rad)
                    }
                    "ln" -> {
                        val arg = parsePrimary()
                        if (arg <= 0.0) throw ArithmeticException("Domain error")
                        ln(arg)
                    }
                    "log" -> {
                        val arg = parsePrimary()
                        if (arg <= 0.0) throw ArithmeticException("Domain error")
                        log10(arg)
                    }
                    "sqrt" -> {
                        val arg = parsePrimary()
                        if (arg < 0.0) throw ArithmeticException("Domain error")
                        sqrt(arg)
                    }
                    else -> throw IllegalArgumentException("Unknown token: $name")
                }
            }

            throw IllegalArgumentException("Unexpected character: '$c'")
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || abs(n - round(n)) > 1e-9) {
                throw ArithmeticException("Factorial requires non-negative integer")
            }
            val intN = n.toLong()
            if (intN > 170) return Double.POSITIVE_INFINITY
            var res = 1.0
            for (i in 2..intN) {
                res *= i
            }
            return res
        }
    }
}

sealed class EvaluationResult {
    data class Success(val value: Double, val formattedResult: String) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}
