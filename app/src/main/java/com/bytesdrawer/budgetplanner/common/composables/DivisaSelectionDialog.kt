package com.bytesdrawer.budgetplanner.common.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.utils.Divisa

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivisaSelectionDialog(
    selectedDivisa: MutableState<Divisa>,
    dialogState: MutableState<Boolean>,
    onDivisaClick: ((Divisa) -> Unit)? = null
) {
    val queryText = remember {
        mutableStateOf("")
    }
    val active = remember {
        mutableStateOf(false)
    }
    val divisaList = Divisa.values().toList()
    Dialog(onDismissRequest = { dialogState.value = !dialogState.value }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            SearchBar(
                query = queryText.value,
                onQueryChange = { queryText.value = it },
                onSearch = { active.value = false },
                active = true,
                onActiveChange = { },
                placeholder = { Text(text = stringResource(R.string.search_currency)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = null
                    )
                }
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    divisaList.forEach {
                        if (
                            it.completeNameEnglish.lowercase().contains(queryText.value.lowercase()) ||
                            it.completeName.lowercase().contains(queryText.value.lowercase()) ||
                            it.name.lowercase().contains(queryText.value.lowercase())
                        ) {
                            Text(
                                text = "${it.name}: ${
                                    if (Locale.current.language == "es" ) {
                                        it.completeName
                                    } else it.completeNameEnglish
                                }",
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedDivisa.value = it
                                        if (onDivisaClick != null) onDivisaClick(it)
                                        dialogState.value = !dialogState.value
                                    }
                            )
                        }
                    }
                }

            }
        }
    }
}