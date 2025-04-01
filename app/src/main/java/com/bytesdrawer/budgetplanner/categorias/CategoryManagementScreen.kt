package com.bytesdrawer.budgetplanner.categorias

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.bytesdrawer.budgetplanner.common.utils.Events
import java.math.BigDecimal

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryManagementScreen(
    analyticsEvents: Events,
    incomeExpenseState: MutableState<Boolean>,
    navController: NavController,
    viewModel: MainViewModel,
    navigateToCreateNewCategoryScreen: () -> Unit
) {
    val context = LocalContext.current
    val categoriesIncome = viewModel.categoryList.observeAsState().value!!.filter { it.isIncome && it.parentCategoryId == null }.sortedBy { it.order }
    val categoriesExpense = viewModel.categoryList.observeAsState().value!!.filter { !it.isIncome && it.parentCategoryId == null && it.name != "Transferencias_Especial_Plus20" }.sortedBy { it.order }

    val deleteCategoryDialogState = remember {
        mutableStateOf(false)
    }
    val draggableState = remember { mutableStateOf(false) }
    val draggingItem = remember { mutableStateOf(0) }
    val reorganizationState = remember { mutableStateOf(false) }

    val copyCategoriesIncome = remember { mutableStateListOf<CategoryBase>() }
    val copyCategoriesExpense = remember { mutableStateListOf<CategoryBase>() }

    LaunchedEffect(draggableState.value, reorganizationState.value) {
        if (reorganizationState.value) {
            reorganizationState.value = !reorganizationState.value
            Log.d("onReorganization", "${reorganizationState.value}")
        } else if (copyCategoriesIncome.isEmpty() || copyCategoriesExpense.isEmpty()) {
            copyCategoriesIncome.clear()
            copyCategoriesExpense.clear()
            copyCategoriesIncome.addAll(categoriesIncome)
            copyCategoriesExpense.addAll(categoriesExpense)
        }
    }

    val positionExpenseCategoriesList = remember { mutableStateMapOf<Int,Rect>() }
    val positionIncomeCategoriesList = remember { mutableStateMapOf<Int,Rect>() }

    val longClickedCategoryToDelete : MutableState<CategoryBase?> = remember {
        mutableStateOf(null)
    }
    LazyVerticalGrid(
        modifier = Modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 66.dp)
            .fillMaxSize(),
        columns = GridCells.Fixed(5),
        state = rememberLazyGridState()
    ) {

        if (draggableState.value) {
            item(span = { GridItemSpan(5) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        modifier = Modifier.weight(.475f),
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        onClick = {
                            analyticsEvents.trackEvent(Events.CATEGORY_REORDER_CANCEL)
                            draggableState.value = false
                        },
                        contentPadding = PaddingValues(horizontal = 5.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.cancel_button),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.05f))
                    Button(
                        modifier = Modifier.weight(.475f),
                        onClick = {
                            analyticsEvents.trackEvent(Events.CATEGORY_REORDER_DONE)
                            copyCategoriesIncome.forEach {
                                viewModel.createOrUpdateCategory(it as Category)
                            }
                            copyCategoriesExpense.forEach {
                                Log.d("Category: ", it.order.toString())
                                viewModel.createOrUpdateCategory(it as Category)
                            }
                            draggableState.value = false
                            viewModel.getTransactions()
                            viewModel.getCategoryItems()
                        },
                        contentPadding = PaddingValues(horizontal = 5.dp)
                    ) {
                        Text(text = stringResource(R.string.done_draggable_categories_button))
                        Icon(
                            painter = painterResource(id = R.drawable.check_circle),
                            contentDescription = null
                        )
                    }
                }
            }
        }

        item(span = { GridItemSpan(5) }) {
            Text(
                text = stringResource(R.string.expenses_lowecase),
                fontSize = 18.sp
            )
        }

        if (!draggableState.value) {
            items(categoriesExpense) {
                if (it.name != "Transferencias_Especial_Plus20") {
                    // Usado para el la Categoria Other/Otros
                    if (it.category_id == 10000L || it.category_id == 10001L) {
                        CategoryItem(it)
                    } else {
                        // Elemento que muestra cada una de las categorias
                        val menuExpanded = remember {
                            mutableStateOf(false)
                        }
                        Column {
                            CategoryItem(
                                it,
                                modifier = Modifier.combinedClickable(
                                    onClick = {
                                        analyticsEvents.trackEvent(Events.NAVIGATE_TO_EDIT_CATEGORY)
                                        navController.navigate(
                                            "${context.getString(NavigationScreens.EDIT_CATEGORY.screen)}/${it.category_id}"
                                        )},
                                    onLongClick = {
                                        longClickedCategoryToDelete.value = it
                                        menuExpanded.value = !menuExpanded.value
                                    }
                                )
                            )
                            DeleteDropdownMenu(menuExpanded, deleteCategoryDialogState, draggableState, analyticsEvents)
                        }
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
                        analyticsEvents.trackEvent(Events.NAVIGATE_TO_CREATE_CATEGORY)
                        incomeExpenseState.value = false
                        navigateToCreateNewCategoryScreen()
                    }
                )
            }
        } else {
            items(copyCategoriesExpense.sortedBy { it.order }) {
                if (it.name != "Transferencias_Especial_Plus20") {
                    CategoryItem(
                        it,
                        draggable = draggableState,
                        listCoordinates = positionExpenseCategoriesList,
                        copiedCategoryList = copyCategoriesExpense,
                        reorganizationState = reorganizationState,
                        draggingItem = draggingItem
                    )
                }
            }

        }

        item(span = { GridItemSpan(5) }) {
            Spacer(modifier = Modifier.padding(top = 16.dp))
        }
        item(span = { GridItemSpan(5) }) {
            Text(
                text = stringResource(R.string.income_lowecase),
                fontSize = 18.sp
            )
        }
        if (!draggableState.value) {
            items(categoriesIncome) {
                if (it.category_id == 10000L || it.category_id == 10001L) {
                    CategoryItem(it)
                } else {
                    val menuExpanded = remember {
                        mutableStateOf(false)
                    }
                    Column {
                        CategoryItem(
                            it,
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    analyticsEvents.trackEvent(Events.NAVIGATE_TO_EDIT_CATEGORY)
                                    navController.navigate(
                                        "${context.getString(NavigationScreens.EDIT_CATEGORY.screen)}/${it.category_id}"
                                    )},
                                onLongClick = {
                                    longClickedCategoryToDelete.value = it
                                    menuExpanded.value = !menuExpanded.value
                                }
                            )
                        )
                        DeleteDropdownMenu(
                            menuExpanded,
                            deleteCategoryDialogState,
                            draggableState,
                            analyticsEvents
                        )
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
                    modifier = Modifier
                        .clickable {
                            analyticsEvents.trackEvent(Events.NAVIGATE_TO_CREATE_CATEGORY)
                            incomeExpenseState.value = true
                            navigateToCreateNewCategoryScreen()
                        }
                )
            }
        } else {
            items(copyCategoriesIncome.sortedBy { it.order }) {
                CategoryItem(
                    it,
                    draggable = draggableState,
                    listCoordinates = positionIncomeCategoriesList,
                    copiedCategoryList = copyCategoriesIncome,
                    reorganizationState = reorganizationState,
                    draggingItem = draggingItem
                )
            }

        }
    }
    if (deleteCategoryDialogState.value) {
        DeleteCategoryDialog(
            deleteCategoryDialogState,
            viewModel,
            longClickedCategoryToDelete,
            analyticsEvents,
            reorganizationState,
            copyCategoriesIncome,
            copyCategoriesExpense
        )
    }
}

@Composable
private fun DeleteDropdownMenu(
    menuExpanded: MutableState<Boolean>,
    deleteCategoryDialogState: MutableState<Boolean>,
    draggableState: MutableState<Boolean>,
    analyticsEvents: Events
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
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.categories_reorder))
            },
            onClick = {
                analyticsEvents.trackEvent(Events.CATEGORY_REORDER_START)
                menuExpanded.value = !menuExpanded.value
                draggableState.value = true
            },
        )
    }
}

@Composable
private fun DeleteCategoryDialog(
    deleteCategoryDialogState: MutableState<Boolean>,
    viewModel: MainViewModel,
    longClickedCategoryToDelete: MutableState<CategoryBase?>,
    analyticsEvents: Events,
    reorganizationState: MutableState<Boolean>,
    copyCategoriesIncome: SnapshotStateList<CategoryBase>,
    copyCategoriesExpense: SnapshotStateList<CategoryBase>
) {
    Dialog(onDismissRequest = { deleteCategoryDialogState.value = !deleteCategoryDialogState.value }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.delete_category_title_dialog),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Text(
                    text = stringResource(R.string.delete_category_message_dialog),
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        onClick = {
                            analyticsEvents.trackEvent(Events.CATEGORY_DELETE)
                            val subCategories = viewModel.categoryList.value?.filter { it.parentCategoryId == longClickedCategoryToDelete.value?.category_id }
                            viewModel.deleteCategory(longClickedCategoryToDelete.value!! as Category, subCategories)
                            deleteCategoryDialogState.value = !deleteCategoryDialogState.value
                            copyCategoriesIncome.clear()
                            copyCategoriesExpense.clear()
                            reorganizationState.value = true
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