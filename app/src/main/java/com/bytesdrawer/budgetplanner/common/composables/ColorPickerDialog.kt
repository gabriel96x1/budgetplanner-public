package com.bytesdrawer.budgetplanner.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.utils.Events

@Composable
fun ColorPickerDialog(
    colorPickerDialogState: MutableState<Boolean>,
    currentColor: MutableState<Long>,
    analyticsEvents: Events
) {
    val controller = rememberColorPickerController()
    val selectedColor = remember {
        mutableStateOf(currentColor.value)
    }
    Dialog(onDismissRequest = {
        colorPickerDialogState.value = !colorPickerDialogState.value
    }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(6.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        selectedColor.value = colorEnvelope.color.toArgb().toLong()
                    }
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .height(35.dp),
                    controller = controller,
                )
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
                Text(text = stringResource(R.string.selected_color_picker))
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(selectedColor.value), shape = RectangleShape)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        colorPickerDialogState.value = !colorPickerDialogState.value
                    }) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                    TextButton(onClick = {
                        analyticsEvents.trackEvent(Events.ADD_EDIT_CATEGORY_SEL_COLOR)
                        currentColor.value = selectedColor.value
                        colorPickerDialogState.value = !colorPickerDialogState.value
                    }) {
                        Text(text = stringResource(R.string.save_button))
                    }
                }
            }
        }
    }
}