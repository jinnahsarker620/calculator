package com.example

import com.example.engine.EvaluationResult
import com.example.engine.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ExampleUnitTest {

    @Test
    fun testBasicMath() {
        val evaluator = ExpressionEvaluator(isRadian = true)
        val res1 = evaluator.evaluate("2 + 2") as EvaluationResult.Success
        assertEquals("4", res1.formattedResult)

        val res2 = evaluator.evaluate("10 − 3") as EvaluationResult.Success
        assertEquals("7", res2.formattedResult)

        val res3 = evaluator.evaluate("5 × 6") as EvaluationResult.Success
        assertEquals("30", res3.formattedResult)

        val res4 = evaluator.evaluate("12 ÷ 4") as EvaluationResult.Success
        assertEquals("3", res4.formattedResult)
    }

    @Test
    fun testPowersAndFactorials() {
        val evaluator = ExpressionEvaluator(isRadian = true)
        val res1 = evaluator.evaluate("2^3") as EvaluationResult.Success
        assertEquals("8", res1.formattedResult)

        val res2 = evaluator.evaluate("5!") as EvaluationResult.Success
        assertEquals("120", res2.formattedResult)

        val res3 = evaluator.evaluate("0!") as EvaluationResult.Success
        assertEquals("1", res3.formattedResult)
    }

    @Test
    fun testSqrtAndConstants() {
        val evaluator = ExpressionEvaluator(isRadian = true)
        val res1 = evaluator.evaluate("√(9)") as EvaluationResult.Success
        assertEquals("3", res1.formattedResult)

        val res2 = evaluator.evaluate("2π") as EvaluationResult.Success
        assertTrue(abs(res2.value - 2 * Math.PI) < 1e-9)

        val res3 = evaluator.evaluate("e") as EvaluationResult.Success
        assertTrue(abs(res3.value - Math.E) < 1e-9)
    }

    @Test
    fun testTrigDegreeMode() {
        val evalDeg = ExpressionEvaluator(isRadian = false)
        val resSin = evalDeg.evaluate("sin(90)") as EvaluationResult.Success
        assertEquals("1", resSin.formattedResult)

        val resCos = evalDeg.evaluate("cos(0)") as EvaluationResult.Success
        assertEquals("1", resCos.formattedResult)

        val resTan = evalDeg.evaluate("tan(45)") as EvaluationResult.Success
        assertEquals("1", resTan.formattedResult)

        val resAsin = evalDeg.evaluate("asin(1)") as EvaluationResult.Success
        assertEquals("90", resAsin.formattedResult)
    }

    @Test
    fun testLogs() {
        val evaluator = ExpressionEvaluator(isRadian = true)
        val resLn = evaluator.evaluate("ln(e)") as EvaluationResult.Success
        assertEquals("1", resLn.formattedResult)

        val resLog = evaluator.evaluate("log(100)") as EvaluationResult.Success
        assertEquals("2", resLog.formattedResult)
    }

    @Test
    fun testParenthesesAndImplicitMult() {
        val evaluator = ExpressionEvaluator(isRadian = true)
        val res = evaluator.evaluate("2(3+4)") as EvaluationResult.Success
        assertEquals("14", res.formattedResult)

        val resSqrt = evaluator.evaluate("3√(4)") as EvaluationResult.Success
        assertEquals("6", resSqrt.formattedResult)
    }
}
