package com.bytesdrawer.budgetplanner.common.composables

import androidx.compose.runtime.MutableState
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

fun onCalculatorButtonClicked(
    mutableString: MutableState<String>,
    buttonClicked: String,
    clickedOperation: MutableState<CalculatorOperation>,
    accumulatedCalculatorValue: MutableState<String>,
    restoreCalculationString: MutableState<Boolean>
) {

    when (buttonClicked) {
        "C" -> {
            accumulatedCalculatorValue.value = "0"
            mutableString.value = ""
        }
        "Del" -> mutableString.value = if (mutableString.value.isNotEmpty()) mutableString.value.substring(0, mutableString.value.lastIndex) else ""
        "+/-" -> {
            if (mutableString.value.isNotEmpty())  {
                if (mutableString.value.first() != '-')
                    mutableString.value = "-" + mutableString.value
                else
                    mutableString.value = mutableString.value.substring(1, mutableString.value.length)
            }
        }
        "%" -> mutableString.value = mutableString.value.toBigDecimal().divide(BigDecimal.valueOf(100)).toString()
        "x" -> {
            if (accumulatedCalculatorValue.value.toBigDecimal() == 0.toBigDecimal()) {
                accumulatedCalculatorValue.value = mutableString.value
            } else {
                mutableString.value = accumulatedCalculatorValue.value
            }
            mutableString.value = accumulatedCalculatorValue.value
            clickedOperation.value = CalculatorOperation.MULTIPLY
            restoreCalculationString.value = true
        }
        "-" -> {
            if (accumulatedCalculatorValue.value.toBigDecimal() == 0.toBigDecimal()) {
                accumulatedCalculatorValue.value = mutableString.value
            } else {
                equalOperations(clickedOperation, mutableString, accumulatedCalculatorValue)
            }
            mutableString.value = accumulatedCalculatorValue.value
            clickedOperation.value = CalculatorOperation.SUBTRACTION
            restoreCalculationString.value = true
        }
        "+" -> {
            if (accumulatedCalculatorValue.value.toBigDecimal() == 0.toBigDecimal()) {
                accumulatedCalculatorValue.value = mutableString.value
            } else {
                equalOperations(clickedOperation, mutableString, accumulatedCalculatorValue)
            }
            mutableString.value = accumulatedCalculatorValue.value
            clickedOperation.value = CalculatorOperation.ADD
            restoreCalculationString.value = true
        }
        "÷" -> {
            if (accumulatedCalculatorValue.value.toBigDecimal() == 0.toBigDecimal()) {
                accumulatedCalculatorValue.value = mutableString.value
            } else {
                equalOperations(clickedOperation, mutableString, accumulatedCalculatorValue)
            }
            mutableString.value = accumulatedCalculatorValue.value
            clickedOperation.value = CalculatorOperation.DIV
            restoreCalculationString.value = true

        }
        "=" -> {
            if (accumulatedCalculatorValue.value.isNotEmpty()) {
                equalOperations(clickedOperation, mutableString, accumulatedCalculatorValue)
            }
            accumulatedCalculatorValue.value = "0"
            clickedOperation.value = CalculatorOperation.NOTHING
        }

        else -> {
            when (clickedOperation.value) {
                CalculatorOperation.NOTHING -> {
                    if (mutableString.value == "0" && buttonClicked == "0") {
                        mutableString.value = mutableString.value
                    } else {
                        mutableString.value = mutableString.value + buttonClicked
                    }                }
                else -> {
                    if (restoreCalculationString.value) {
                        mutableString.value = ""
                        restoreCalculationString.value = false
                    }
                    if (mutableString.value == "0" && buttonClicked == "0") {
                        mutableString.value = mutableString.value
                    } else {
                        mutableString.value = mutableString.value + buttonClicked
                    }
                }
            }
        }
    }
}

fun equalOperations(
    clickedOperation: MutableState<CalculatorOperation>,
    mutableString: MutableState<String>,
    accumulatedCalculatorValue: MutableState<String>
) {
    when (clickedOperation.value) {
        CalculatorOperation.NOTHING -> { }
        CalculatorOperation.ADD -> {
            mutableString.value =
                (accumulatedCalculatorValue.value.toBigDecimal() + if (mutableString.value.isEmpty()) 0.toBigDecimal() else mutableString.value.toBigDecimal()).toString()
        }
        CalculatorOperation.SUBTRACTION -> {
            mutableString.value =
                (accumulatedCalculatorValue.value.toBigDecimal() - if (mutableString.value.isEmpty()) 0.toBigDecimal() else mutableString.value.toBigDecimal()).toString()

        }
        CalculatorOperation.DIV -> {
            mutableString.value =
                (accumulatedCalculatorValue.value.toBigDecimal().divide(if (mutableString.value.isEmpty() || mutableString.value == "0") 1.toBigDecimal() else mutableString.value.toBigDecimal(), 2, RoundingMode.DOWN)).toString()
        }
        CalculatorOperation.MULTIPLY -> {
            mutableString.value =
                (accumulatedCalculatorValue.value.toBigDecimal() * if (mutableString.value.isEmpty()) 1.toBigDecimal() else mutableString.value.toBigDecimal()).toString()

        }
    }
    accumulatedCalculatorValue.value = mutableString.value
}

enum class CalculatorOperation {
    ADD,
    SUBTRACTION,
    DIV,
    MULTIPLY,
    NOTHING
}