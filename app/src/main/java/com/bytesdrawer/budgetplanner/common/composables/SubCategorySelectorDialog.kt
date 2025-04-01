package com.bytesdrawer.budgetplanner.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.categorias.CategoryItem
import com.bytesdrawer.budgetplanner.common.models.local.Category
import java.math.BigDecimal

@Composable
fun SubCategorySelectorDialog(
    dialogState: MutableState<Boolean>,
    subCategoryList: List<Category>?,
    subCategorySelected: MutableState<Category?>,
    navigateToCreateSubCategory: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = {
        dialogState.value = !dialogState.value
        subCategorySelected.value = null
    }) {
        Card {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                state = rememberLazyGridState(),
                modifier = Modifier.padding(24.dp)
            ) {
                item(span = { GridItemSpan(5) }) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.select_subcategory),
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            onClick = {
                                navigateToCreateSubCategory()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.create_subcategory))
                            Icon(
                                painter = painterResource(id = R.drawable.add),
                                contentDescription = null
                            )
                        }
                    }
                }
                if (subCategoryList != null) {
                    item {
                        CategoryItem(
                            category = Category(
                                parentCategoryId = null,
                                name = "Ninguno",
                                icon = context.resources.getResourceEntryName(R.drawable.circle_none),
                                isIncome = false,
                                expenseLimit = BigDecimal.ZERO,
                                color = Color.Gray.toArgb().toLong(),
                                order = 999999999,
                                isSent = false,
                                timeStamp = "",
                                toDelete = false
                            ),
                            modifier = Modifier.clickable {
                                subCategorySelected.value = null
                                dialogState.value = !dialogState.value
                            }
                        )
                    }
                    items(subCategoryList) {subcategory ->
                        CategoryItem(
                            category = subcategory,
                            modifier = Modifier.clickable {
                                subCategorySelected.value = subcategory
                                dialogState.value = !dialogState.value
                            }
                        )
                    }
                }
            }
        }
    }
}