package com.bytesdrawer.budgetplanner.graficos

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.min

@Composable
fun DonutChart(
    incomeExpenseState: Boolean,
    totalTransactions: BigDecimal,
    modifier: Modifier = Modifier,
    colors: List<Color>,
    inputValues: List<Float>,
    textColor: Color = MaterialTheme.colorScheme.primary
) {
    val chartDegrees = 360f // circle shape

    var startAngle = 270f

    val proportions = inputValues.map {
        it * 100 / inputValues.sum()
    }

    val angleProgress = proportions.map { prop ->
        chartDegrees * prop / 100
    }

    // calculate each slice end point in degrees, for handling click position
    val progressSize = mutableListOf<Float>()

    LaunchedEffect(angleProgress){
        progressSize.add(angleProgress.first())
        for (x in 1 until angleProgress.size) {
            progressSize.add(angleProgress[x] + progressSize[x - 1])
        }
    }

    val density = LocalDensity.current
    val textFontSize = with(density) { 14.dp.toPx() }
    val textPaint = remember {
        Paint().apply {
            color = textColor.toArgb()
            textSize = textFontSize
            textAlign = Paint.Align.CENTER
        }
    }


    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {

        val canvasSize = min(constraints.maxWidth, constraints.maxHeight)
        val size = Size(canvasSize.toFloat(), canvasSize.toFloat())
        val canvasSizeDp = with(density) { canvasSize.toDp() }
        val sliceWidth = with(LocalDensity.current) { 16.dp.toPx() }

        Canvas(
            modifier = Modifier
                .size(canvasSizeDp)
        ) {

            angleProgress.forEachIndexed { index, angle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = angle,
                    useCenter = false,
                    size = size,
                    style = Stroke(width = sliceWidth)
                )
                startAngle += angle
            }

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    if (incomeExpenseState) "+${isMiller(totalTransactions)}" else "-${isMiller(totalTransactions)}",
                    (canvasSize / 2) + textFontSize / 8,
                    (canvasSize / 2) + textFontSize / 4,
                    textPaint
                )
            }
        }
    }
}

private fun isMiller(number: BigDecimal): String {
    val df = DecimalFormat("#,###.##")
    df.roundingMode = RoundingMode.DOWN

    return when {
        number > BigDecimal(10000000000) || number < BigDecimal(-10000000000) -> { "${df.format(number.divide(BigDecimal(1000000000)))}B" }
        number > BigDecimal(10000000) || number < BigDecimal(-10000000)  -> { "${df.format(number.divide(BigDecimal(1000000)))}M" }
        number > BigDecimal(10000) || number < BigDecimal(-10000) -> { "${df.format(number.divide(BigDecimal(1000)))}k" }
        else -> { df.format(number) }
    }
}