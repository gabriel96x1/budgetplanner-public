package com.bytesdrawer.budgetplanner.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.common.utils.createIconList

@Composable
fun IconPickerDialog(
    iconPickerDialogState: MutableState<Boolean>,
    currentIcon: MutableState<Int>,
    analyticsEvents: Events
) {
    val selectedIcon = remember {
        mutableStateOf(currentIcon.value)
    }
    val iconList = remember {
        createIconList()
    }
    Dialog(onDismissRequest = {
        iconPickerDialogState.value = !iconPickerDialogState.value
    }) {
        Card {
            LazyVerticalGrid(
                modifier = Modifier.padding(24.dp),
                columns = GridCells.Fixed(5),
                state = rememberLazyGridState()
            ) {
                item(
                    span = { GridItemSpan(5) }
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth(),
                        text = stringResource(R.string.select_new_icon_picker),
                        textAlign = TextAlign.Center
                    )
                }

                items(iconList) {
                    Column(
                        modifier = Modifier.background(
                            if (selectedIcon.value == it) Color.Gray else MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .background(Color.DarkGray, shape = CircleShape)
                                .clickable { selectedIcon.value = it },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = it),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }

                item(span = { GridItemSpan(5) } ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            iconPickerDialogState.value = !iconPickerDialogState.value
                        }) {
                            Text(text = stringResource(id = R.string.cancel_button))
                        }
                        TextButton(onClick = {
                            analyticsEvents.trackEvent(Events.ADD_EDIT_CATEGORY_SEL_ICON)
                            currentIcon.value = selectedIcon.value
                            iconPickerDialogState.value = !iconPickerDialogState.value
                        }) {
                            Text(text = stringResource(id = R.string.save_button))
                        }
                    }
                }
            }
        }
    }
}