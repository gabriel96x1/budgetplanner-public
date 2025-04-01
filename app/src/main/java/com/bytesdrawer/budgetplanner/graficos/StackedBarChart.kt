package com.bytesdrawer.budgetplanner.graficos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.bytesdrawer.budgetplanner.common.utils.toPercent
import java.math.BigDecimal

@Composable
fun StackedBarChart(
    values: List<StackedBarData>,
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
            StackedBar(it, maxHeight = maxHeight)
        }
    }
}

@Composable
private fun RowScope.StackedBar(
    stackedBarData: StackedBarData,
    maxHeight: Dp
) {
    val percentages = stackedBarData.values.toPercent(BigDecimal(200))
    val totalHeight = stackedBarData.total
    val singleBarHeight = remember(totalHeight) { totalHeight * maxHeight.value / 100 }

    Column(
        modifier = Modifier
            .weight(1f)
            .height(singleBarHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        percentages.forEachIndexed { index, item ->
            val itemHeight = remember(item) { item * singleBarHeight / 100 }

            Spacer(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .height(itemHeight.dp)
                    .fillMaxWidth()
                    .drawBehind {
                        drawRoundRect(
                            color = stackedBarData.colors[index],
                            cornerRadius = CornerRadius(15f, 15f)
                        )
                    }
            )

        }
    }
}

data class StackedBarData(
    val total: Float,
    val values: List<Double>,
    val colors: List<Color>,
)

@Preview
@Composable
private fun PreviewBarChart() {
    val values1 = (0..2).map { (1..100).random().toDouble() }
    val values2 = (0..2).map { (1..100).random().toDouble() }
    val values3 = (0..2).map { (1..100).random().toDouble() }

    StackedBarChart(
        values = listOf(
            StackedBarData(
                100f,
                values1,
                colors = listOf(Color.Magenta, Color.Green, Color.Gray)
            ),
            StackedBarData(
                20f,
                values2,
                colors = listOf(Color.Gray, Color.Magenta, Color.Green)
            ),
            StackedBarData(
                45f,
                values3,
                colors = listOf(Color.Green, Color.Magenta, Color.Gray)
            )
        ),
        color = MaterialTheme.colorScheme.primary
    )
}