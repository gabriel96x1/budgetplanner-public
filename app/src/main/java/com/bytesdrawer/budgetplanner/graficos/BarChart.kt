package com.bytesdrawer.budgetplanner.graficos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BarChart(
    values: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 1.dp,
    maxHeight: Dp = 200.dp,
) {
    val density = LocalDensity.current
    val stroke = with(density) { strokeWidth.toPx() }

    Row(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .height(maxHeight)
                .drawBehind {
                    drawLine(
                        color = color,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = stroke
                    )
                }
        ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        values.forEach {
            Bar(value = it, color = color, maxHeight = maxHeight)
        }
    }
}

@Composable
private fun RowScope.Bar(
    value: Float,
    color: Color,
    maxHeight: Dp
) {
    val itemHeight = remember(value) { value * maxHeight.value / 100 }

    Spacer(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(itemHeight.dp)
            .weight(1f)
            .drawBehind {
                drawRoundRect(
                    color = color,
                    cornerRadius = CornerRadius( 15f,15f)
                )
            }
    )
}

@Preview
@Composable
private fun PreviewBarChart() {
    BarChart(
        values = (0..10).map { (1..100).random().toFloat() },
        color = MaterialTheme.colorScheme.primary
    )
}