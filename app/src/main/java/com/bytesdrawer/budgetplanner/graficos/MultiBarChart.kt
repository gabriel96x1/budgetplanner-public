package com.bytesdrawer.budgetplanner.graficos

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.bytesdrawer.budgetplanner.common.utils.toPercent
import java.math.BigDecimal

@Composable
fun MultiBarChart(
    highestValue: BigDecimal,
    values: List<MultiBarData>,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 1.dp,
    maxHeight: Dp = 200.dp,
    width: Dp = 700.dp
) {
    val density = LocalDensity.current
    val stroke = with(density) { strokeWidth.toPx() }

    Row(
        modifier = modifier.then(
            Modifier
                .width(width)
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
        Log.d("MultiBar size", values.size.toString())
        values.forEach {
            MultiBar(
                value = it,
                maxHeight = maxHeight,
                highestValue,
            )
        }
    }
}

@Composable
private fun RowScope.MultiBar(
    value: MultiBarData,
    maxHeight: Dp,
    highestValue: BigDecimal
) {
    val percentages = value.values.toPercent(highestValue)

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .weight(1f)
            .padding(bottom = 1.dp, start = 5.dp, end = 5.dp)
    ) {
        percentages.forEachIndexed { index, item ->
            val itemHeight = remember(item) { item * maxHeight.value / 100 }
            val popUpState = remember {
                mutableStateOf(false)
            }
            if (popUpState.value) {
                Popup(
                    alignment = Alignment.TopCenter,
                    onDismissRequest = { popUpState.value = !popUpState.value },
                    offset = IntOffset(0, -80),
                    properties = PopupProperties()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                shape = CircleShape
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$${ value.values[index].toBigDecimal() }",
                            color = value.colors[index]
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .height(itemHeight.dp)
                    .weight(1f)
                    .drawBehind {
                        drawRoundRect(
                            color = value.colors[index],
                            cornerRadius = CornerRadius(15f, 15f)
                        )
                    }
                    .clickable { popUpState.value = !popUpState.value }
            )
        }
    }
}

data class MultiBarData(
    val values: List<Double>,
    val colors: List<Color>,
)

@Preview
@Composable
private fun PreviewBarChart() {
    val values1 = (0..2).map { (1..100).random().toDouble() }
    val values2 = (0..2).map { (1..100).random().toDouble() }
    val values3 = (0..2).map { (1..100).random().toDouble() }

    MultiBarChart(
        values = listOf(
            MultiBarData(
                values1,
                listOf(Color.Red, Color.Cyan, Color.Gray)
                ),
            MultiBarData(
                values2,
                listOf(Color.Red, Color.Cyan, Color.Gray)
            ),
            MultiBarData(
                values3,
                listOf(Color.Red, Color.Cyan, Color.Gray)
            )
        ),
        color = MaterialTheme.colorScheme.primary,
        highestValue = BigDecimal(200)
    )
}