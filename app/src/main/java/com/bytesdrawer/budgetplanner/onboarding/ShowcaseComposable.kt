package com.bytesdrawer.budgetplanner.onboarding

import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import com.bytesdrawer.budgetplanner.R
import kotlin.math.roundToInt

@OptIn(ExperimentalTextApi::class)
@Composable
fun ShowCase(
    targetCords: SnapshotStateMap<String, LayoutCoordinates?>,
    showcaseShown: MutableState<Boolean>,
    showcaseStep: MutableState<ShowcaseSteps>
) {
    val step = remember { mutableStateOf(1) }
    val isDarkTheme = isSystemInDarkTheme()

    if (targetCords.size == 5) {
        when (showcaseStep.value) {
            ShowcaseSteps.ACCOUNTS -> {
                targetCords[ShowcaseSteps.ACCOUNTS.name]?.let {
                    val targetRect = it.boundsInRoot()
                    val targetRadius = targetRect.maxDimension / 2f + 20f
                    val textMeasurer = rememberTextMeasurer()
                    val textLayoutResult: TextLayoutResult =
                        textMeasurer.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_accounts_title)),
                            style = TextStyle(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        )

                    val textMeasurerSubtitle = rememberTextMeasurer()
                    val textLayoutResultSubtitle: TextLayoutResult =
                        textMeasurerSubtitle.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_accounts_subtitle)),
                            style = TextStyle(color = Color.White, fontSize = 20.sp),
                            constraints = Constraints.fixedWidth(
                                (targetRect.size.width * 2).roundToInt()
                            )
                        )

                    val bigRadius = remember { mutableStateOf(0f) }

                    val animatedRadius = animateFloatAsState(
                        targetValue = bigRadius.value / 1.3f,
                        animationSpec = tween(1000),
                        label = ""
                    )

                    val animatedText = animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(2000),
                        label = ""
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onCanvasClicked(step, showcaseShown, showcaseStep)
                            }
                            .graphicsLayer(alpha = .99f)
                    ) {
                        bigRadius.value = size.minDimension

                        drawCircle(
                            radius = animatedRadius.value,
                            color = if (isDarkTheme) Color.White else Color.Black,
                            center = targetRect.center
                        )
                        drawCircle(
                            color = Color.White,
                            radius = targetRadius,
                            center = targetRect.center,
                            blendMode = BlendMode.Clear
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                targetRect.topLeft.x - targetRect.maxDimension / 2f,
                                targetRect.topLeft.y + (targetRect.maxDimension * 1.25f) - textLayoutResultSubtitle.size.height / 2
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )
                        drawText(
                            textLayoutResult = textLayoutResultSubtitle,
                            topLeft = Offset(
                                targetRect.topLeft.x - targetRect.maxDimension / 2f,
                                targetRect.topLeft.y + (targetRect.maxDimension * 1.25f)
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )

                    }
                }
            }
            ShowcaseSteps.RECURRENTS -> {
                targetCords[ShowcaseSteps.RECURRENTS.name]?.let {
                    val targetRect = it.boundsInRoot()
                    val targetRadius = targetRect.maxDimension / 2f + 20f
                    val textMeasurer = rememberTextMeasurer()
                    val textLayoutResult: TextLayoutResult =
                        textMeasurer.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_recurrents_title)),
                            style = TextStyle(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        )

                    val textMeasurerSubtitle = rememberTextMeasurer()
                    val textLayoutResultSubtitle: TextLayoutResult =
                        textMeasurerSubtitle.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_recurrents_subtitle)),
                            style = TextStyle(color = Color.White, fontSize = 20.sp),
                            constraints = Constraints.fixedWidth(
                                (targetRect.size.width * 1.5f).roundToInt()
                            )
                        )
                    val bigRadius = remember { mutableStateOf(0f) }

                    val animatedRadius = animateFloatAsState(
                        targetValue = bigRadius.value / 1.5f,
                        animationSpec = tween(1000),
                        label = ""
                    )

                    val animatedText = animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(2000),
                        label = ""
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onCanvasClicked(step, showcaseShown, showcaseStep)
                            }
                            .graphicsLayer(alpha = .99f)
                    ) {
                        bigRadius.value = size.minDimension

                        drawCircle(
                            radius = animatedRadius.value,
                            color = if (isDarkTheme) Color.White else Color.Black,
                            center = targetRect.center
                        )
                        drawCircle(
                            color = Color.White,
                            radius = targetRadius,
                            center = targetRect.center,
                            blendMode = BlendMode.Clear
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                targetRect.topLeft.x,
                                targetRect.topLeft.y + targetRect.maxDimension - textLayoutResultSubtitle.size.height / 2
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )
                        drawText(
                            textLayoutResult = textLayoutResultSubtitle,
                            topLeft = Offset(
                                targetRect.topLeft.x,
                                targetRect.topLeft.y + targetRect.maxDimension
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )

                    }
                }

            }
            ShowcaseSteps.CATEGORIES -> {
                targetCords[ShowcaseSteps.CATEGORIES.name]?.let {
                    val targetRect = it.boundsInRoot()
                    val targetRadius = targetRect.maxDimension / 2f + 20f
                    val textMeasurer = rememberTextMeasurer()
                    val textLayoutResult: TextLayoutResult =
                        textMeasurer.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_categories_title)),
                            style = TextStyle(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        )

                    val textMeasurerSubtitle = rememberTextMeasurer()
                    val textLayoutResultSubtitle: TextLayoutResult =
                        textMeasurerSubtitle.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_categories_subtitle)),
                            style = TextStyle(color = Color.White, fontSize = 20.sp),
                            constraints = Constraints.fixedWidth(
                                (targetRect.size.width * 1.5f).roundToInt()
                            )
                        )
                    val bigRadius = remember { mutableStateOf(0f) }

                    val animatedRadius = animateFloatAsState(
                        targetValue = bigRadius.value / 1.5f,
                        animationSpec = tween(1000),
                        label = ""
                    )

                    val animatedText = animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(2000),
                        label = ""
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onCanvasClicked(step, showcaseShown, showcaseStep)
                            }
                            .graphicsLayer(alpha = .99f)
                    ) {
                        bigRadius.value = size.minDimension

                        drawCircle(
                            radius = animatedRadius.value,
                            color = if (isDarkTheme) Color.White else Color.Black,
                            center = targetRect.center
                        )
                        drawCircle(
                            color = Color.White,
                            radius = targetRadius,
                            center = targetRect.center,
                            blendMode = BlendMode.Clear
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                targetRect.topLeft.x,
                                targetRect.topLeft.y + targetRect.maxDimension - textLayoutResultSubtitle.size.height / 2
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )
                        drawText(
                            textLayoutResult = textLayoutResultSubtitle,
                            topLeft = Offset(
                                targetRect.topLeft.x,
                                targetRect.topLeft.y + targetRect.maxDimension
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )

                    }
                }

            }
            ShowcaseSteps.ANALYTICS -> {
                targetCords[ShowcaseSteps.ANALYTICS.name]?.let {
                    val targetRect = it.boundsInRoot()
                    val targetRadius = targetRect.maxDimension / 2f + 20f
                    val textMeasurer = rememberTextMeasurer()
                    val textLayoutResult: TextLayoutResult =
                        textMeasurer.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_analytics_title)),
                            style = TextStyle(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        )

                    val textMeasurerSubtitle = rememberTextMeasurer()
                    val textLayoutResultSubtitle: TextLayoutResult =
                        textMeasurerSubtitle.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_analytics_subtitle)),
                            style = TextStyle(color = Color.White, fontSize = 20.sp),
                            constraints = Constraints.fixedWidth(
                                (targetRect.size.width * 1.5f).roundToInt()
                            )
                        )
                    val bigRadius = remember { mutableStateOf(0f) }

                    val animatedRadius = animateFloatAsState(
                        targetValue = bigRadius.value / 1.5f,
                        animationSpec = tween(1000),
                        label = ""
                    )

                    val animatedText = animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(2000),
                        label = ""
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onCanvasClicked(step, showcaseShown, showcaseStep)
                            }
                            .graphicsLayer(alpha = .99f)
                    ) {
                        bigRadius.value = size.minDimension

                        drawCircle(
                            radius = animatedRadius.value,
                            color = if (isDarkTheme) Color.White else Color.Black,
                            center = targetRect.center
                        )
                        drawCircle(
                            color = Color.White,
                            radius = targetRadius,
                            center = targetRect.center,
                            blendMode = BlendMode.Clear
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                targetRect.topLeft.x - targetRect.maxDimension / 2,
                                targetRect.topLeft.y + targetRect.maxDimension - textLayoutResultSubtitle.size.height / 2
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )
                        drawText(
                            textLayoutResult = textLayoutResultSubtitle,
                            topLeft = Offset(
                                targetRect.topLeft.x - targetRect.maxDimension / 2,
                                targetRect.topLeft.y + targetRect.maxDimension
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )

                    }
                }

            }
            ShowcaseSteps.ADD_TRANSACTION_FAB -> {
                targetCords[ShowcaseSteps.ADD_TRANSACTION_FAB.name]?.let {
                    val targetRect = it.boundsInRoot()
                    val targetRadius = targetRect.maxDimension / 2f + 40f
                    val textMeasurer = rememberTextMeasurer()
                    val textLayoutResult: TextLayoutResult =
                        textMeasurer.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_fab_title)),
                            style = TextStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        )

                    val textMeasurerSubtitle = rememberTextMeasurer()
                    val textLayoutResultSubtitle: TextLayoutResult =
                        textMeasurerSubtitle.measure(
                            text = AnnotatedString(stringResource(R.string.showcase_fab_subtitle)),
                            style = TextStyle(color = Color.White, fontSize = 20.sp),
                            constraints = Constraints.fixedWidth(
                                targetRect.size.width.roundToInt() * 3
                            )
                        )
                    val bigRadius = remember { mutableStateOf(0f) }

                    val animatedRadius = animateFloatAsState(
                        targetValue = bigRadius.value / 2.0f,
                        animationSpec = tween(1000),
                        label = ""
                    )

                    val animatedText = animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(2000),
                        label = ""
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onCanvasClicked(step, showcaseShown, showcaseStep)
                            }
                            .graphicsLayer(alpha = .99f)
                    ) {
                        bigRadius.value = size.minDimension

                        drawCircle(
                            radius = animatedRadius.value,
                            color = if (isDarkTheme) Color.White else Color.Black,
                            center = targetRect.center
                        )
                        drawCircle(
                            color = Color.White,
                            radius = targetRadius,
                            center = targetRect.center,
                            blendMode = BlendMode.Clear
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                targetRect.topLeft.x - targetRect.maxDimension - 150,
                                targetRect.topLeft.y - targetRect.maxDimension - 40f - textLayoutResultSubtitle.size.height / 2
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )
                        drawText(
                            textLayoutResult = textLayoutResultSubtitle,
                            topLeft = Offset(
                                targetRect.topLeft.x - targetRect.maxDimension - 150,
                                targetRect.topLeft.y - targetRect.maxDimension - 40f
                            ),
                            alpha = animatedText.value,
                            color = if (isDarkTheme) Color.Black else Color.White,
                        )

                    }
                }

            }
        }
    }


}

private fun onCanvasClicked(
    step: MutableState<Int>,
    showcaseShown: MutableState<Boolean>,
    showcaseStep: MutableState<ShowcaseSteps>
) {
    if (step.value == ShowcaseSteps.values().size) {
        showcaseShown.value = false
    } else {
        showcaseStep.value = ShowcaseSteps.values()[step.value]
        step.value += 1
    }
}