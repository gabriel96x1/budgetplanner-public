package com.bytesdrawer.budgetplanner.categorias

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.models.base.CategoryBase
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.navigation.NavigationScreens
import java.math.BigDecimal

/**
* Usado para representar las subcategorías dentro de la ventana de edición de categorias
* */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubCategoryGrid(
    context: Context,
    navController: NavController,
    subCategories: List<Category>?,
    category: Category?,
    viewModel: MainViewModel
) {
    val longClickedCategoryToDelete : MutableState<CategoryBase?> = remember {
        mutableStateOf(null)
    }
    val deleteCategoryDialogState = remember {
        mutableStateOf(false)
    }
    Text(
        text = stringResource(R.string.sub_categories_text),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.padding(vertical = 6.dp))
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        state = rememberLazyGridState()
    ) {
        if (subCategories != null) {
            items(subCategories) {
                val menuExpanded = remember {
                    mutableStateOf(false)
                }
                Column {
                    CategoryItem(
                        it,
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                navController.navigate(
                                    "${context.getString(NavigationScreens.EDIT_SUBCATEGORY.screen)}/${it.category_id}"
                                )},
                            onLongClick = {
                                longClickedCategoryToDelete.value = it
                                menuExpanded.value = !menuExpanded.value
                            }
                        )
                    )
                    DeleteSubCategoryDropdownMenu(menuExpanded = menuExpanded, deleteCategoryDialogState = deleteCategoryDialogState)
                }
            }
        }

        item {
            CategoryItem(
                category = Category(
                    parentCategoryId = null,
                    name = stringResource(R.string.new_category),
                    icon = context.resources.getResourceEntryName(R.drawable.add),
                    isIncome = false,
                    expenseLimit = BigDecimal.ZERO,
                    color = 0xFF0D01FF,
                    order = 999999999,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ),
                modifier = Modifier.clickable {
                    navController.navigate(
                        "${context.getString(NavigationScreens.ADD_SUBCATEGORY.screen)}/${category?.category_id}"
                    )
                }
            )
        }
    }
    if (deleteCategoryDialogState.value) {
        DeleteSubcategoryDialog(
            deleteCategoryDialogState,
            viewModel,
            longClickedCategoryToDelete,
            category
        )
    }

}

@Composable
private fun DeleteSubcategoryDialog(
    deleteCategoryDialogState: MutableState<Boolean>,
    viewModel: MainViewModel,
    longClickedCategoryToDelete: MutableState<CategoryBase?>,
    parentCategory: Category?
) {
    Dialog(onDismissRequest = { deleteCategoryDialogState.value = !deleteCategoryDialogState.value }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.delete_subcategory_title_dialog),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Text(
                    text = stringResource(R.string.delete_subcategory_message_dialog, parentCategory!!.name),
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Button(
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        onClick = {
                            viewModel.deleteSubcategory(longClickedCategoryToDelete.value!! as Category, parentCategory)
                            deleteCategoryDialogState.value = !deleteCategoryDialogState.value
                        }) {
                        Text(text = stringResource(id = R.string.delete_button), color = Color.White)
                    }
                    Button(onClick = {
                        deleteCategoryDialogState.value = !deleteCategoryDialogState.value
                    }) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteSubCategoryDropdownMenu(
    menuExpanded: MutableState<Boolean>,
    deleteCategoryDialogState: MutableState<Boolean>
) {
    DropdownMenu(
        expanded = menuExpanded.value,
        onDismissRequest = { menuExpanded.value = !menuExpanded.value },
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.delete_button))
            },
            onClick = {
                menuExpanded.value = !menuExpanded.value
                deleteCategoryDialogState.value = !deleteCategoryDialogState.value
            },
        )
    }
}