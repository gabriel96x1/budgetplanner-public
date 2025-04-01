package com.bytesdrawer.budgetplanner.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    height: List<Float> = listOf(
        0f, 50f, 80f, 100f, 75f, 200f, 40f
    ),
    highestValue: Float = 200f,
    minorValue: Float = 0f,
    graphColor: Color = MaterialTheme.colorScheme.primary,
    dotsColor: Color = MaterialTheme.colorScheme.background
) {
    val heightSpacing = 25f
    val convertedValues = height.map {
        linealInterpolation(it, minorValue, highestValue, 0f, 100f)
    }
    val invertedY = convertedValues.map { (it * 5) + heightSpacing }
    val yPoints = invertedY.map { (1f - it) + 550f }
    val spacing = 100f
    Box(
        modifier = modifier
            //.padding(all = 16.dp)
            //.background(Color.White)
    ){

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            drawRect(
                color = Color.Transparent,
                topLeft = Offset.Zero,
                size = Size(
                    width = size.width,
                    height = size.height
                ),
                style = Stroke()
            )

            val spacePerHour = (size.width - spacing) / yPoints.size

            val normX = mutableListOf<Float>()
            val normY = mutableListOf<Float>()

            val strokePath = Path().apply {

                for (i in yPoints.indices) {

                    val currentX = spacing + i * spacePerHour

                    if (i == 0) {

                        moveTo(currentX, yPoints[i])
                    } else {

                        val previousX = spacing + (i - 1) * spacePerHour

                        val conX1 = (previousX + currentX) / 2f
                        val conX2 = (previousX + currentX) / 2f

                        val conY1 = yPoints[i - 1]
                        val conY2 = yPoints[i]


                        cubicTo(
                            x1 = conX1,
                            y1 = conY1,
                            x2 = conX2,
                            y2 = conY2,
                            x3 = currentX,
                            y3 = yPoints[i]
                        )
                    }

                    // Circle dot points
                    normX.add(currentX)
                    normY.add(yPoints[i])

                }
            }


            drawPath(
                path = strokePath,
                color = graphColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            (normX.indices).forEach {
                drawCircle(
                    dotsColor,
                    radius = 3.dp.toPx(),
                    center = Offset(normX[it], normY[it])
                )
            }
        }
    }
}

private fun linealInterpolation(
    value: Float,
    originalMin: Float,
    originalMax: Float,
    targetMin: Float,
    targetMax: Float
): Float {
    return ((value - originalMin) /
            (originalMax - originalMin)) *
            (targetMax - targetMin) +
            targetMin
}

@Preview
@Composable
fun LineChartPreview() {
    LineChart()
}