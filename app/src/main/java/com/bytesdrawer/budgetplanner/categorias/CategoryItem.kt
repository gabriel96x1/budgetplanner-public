package com.bytesdrawer.budgetplanner.categorias

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bytesdrawer.budgetplanner.common.composables.SubCategorySelectorDialog
import com.bytesdrawer.budgetplanner.common.models.base.CategoryBase
import com.bytesdrawer.budgetplanner.common.models.local.Category
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun CategoryItem(
    category: CategoryBase,
    modifier: Modifier = Modifier,
    selectedCategory: Category? = null,
    draggingItem: MutableState<Int> = mutableStateOf(0),
    draggable: MutableState<Boolean> = mutableStateOf(false),
    reorganizationState: MutableState<Boolean> = mutableStateOf(false),
    listCoordinates: SnapshotStateMap<Int, Rect> = mutableStateMapOf(),
    copiedCategoryList: SnapshotStateList<CategoryBase> = emptyList<CategoryBase>().toMutableStateList(),
    // Los siguientes 4 parámetros tienen que ser includos a la vez al instanciar este Composable
    // para poder hacer uso del dialogo de selección de subcategorías
    subCategorySelected: MutableState<Category?>? = null,
    subCategorySelectorDialogState: MutableState<Boolean>? = null,
    subCategoryList: List<Category>? = listOf(),
    navigateToCreateSubCategory: (() -> Unit)? = null
) {
    val offset = remember { mutableStateOf(Offset(0f,0f)) }
    val initialRect: MutableState<Rect> = remember {
        mutableStateOf(Rect(Offset(0f,0f), Size.Zero))
    }
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (draggable.value) {
            Modifier
                .zIndex(if (category.order == draggingItem.value) 10000f else 0f)
                .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
                .background(
                    if (isSystemInDarkTheme()) {
                        Color(0x2FFFFFFF)
                    } else {
                        Color(0x2F000000)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    BorderStroke(
                        1.dp,
                        if (isSystemInDarkTheme()) {
                            Color.White
                        } else {
                            Color.Black
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .onGloballyPositioned {
                    listCoordinates[category.order] = it.boundsInRoot()
                    Log.d("Coordinates", "${category.order} ${it.boundsInRoot().center}")
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            initialRect.value = listCoordinates[category.order]!!
                            draggingItem.value = category.order
                            Log.d("OnDragStart", "done")
                        },
                        onDragEnd = {
                            Log.d("OnDragEnd", "${reorganizationState.value}")
                            val thisRect = listCoordinates[category.order]
                            Log.d("thisRectCenter", "${listCoordinates[category.order]!!.center}")

                            val thisOrderPosition = category.order
                            val lastPosition = listCoordinates.size - 1
                            val nearestCenters: Pair<Int, Int>

                            val distanceLists = mutableMapOf<Double, Int>()
                            listCoordinates.forEach { (order, rect) ->
                                if (order != category.order) {
                                    Log.d("Measuring Distances", "$order ${rect.center}")

                                    val xDifference =
                                        (rect.center.x - thisRect!!.center.x).absoluteValue
                                    val yDifference =
                                        (rect.center.y - thisRect.center.y).absoluteValue
                                    val distance =
                                        sqrt((xDifference.pow(2) + yDifference.pow(2)).toDouble())
                                    distanceLists[distance] = order
                                }
                            }
                            val orderedPositions = distanceLists
                                .toList()
                                .sortedBy { it.first }
                                .toMap().values.toList()
                            nearestCenters = if (orderedPositions.size > 1)
                                Pair(orderedPositions.first(), orderedPositions[1])
                            else
                                Pair(orderedPositions.first(), orderedPositions.first())
                            Log.d(
                                "OnDragEnd",
                                "$nearestCenters $distanceLists $orderedPositions"
                            )
                            reorderCategories(
                                nearestCenters,
                                thisOrderPosition,
                                lastPosition,
                                copiedCategoryList,
                                listCoordinates,
                                thisRect!!
                            ) {
                                offset.value = Offset(0f, 0f)
                                reorganizationState.value = !reorganizationState.value
                            }

                        },
                        onDragCancel = {
                            offset.value = Offset(0f, 0f)
                            Log.d("OnDragCancel", "done")
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offset.value += dragAmount
                        //draggingItem.value = category.order
                        //zIndex.value = if (draggingItem.value == category.order) 0f else 10f
                        //Log.d("Dragging", "${category.order} ${zIndex.value}")
                    }
                }

        } else {
            Modifier.background(
                if (selectedCategory == category)
                    MaterialTheme.colorScheme.inversePrimary
                else
                    Color.Transparent
            )
        }
    ) {
        Box(
            modifier = modifier
                .padding(4.dp)
                .fillMaxSize()
                .aspectRatio(1f)
                .background(Color(category.color), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = context.resources.getIdentifier(category.icon, "drawable", context.packageName)),
                contentDescription = null,
                tint = Color.White
            )
            if (subCategorySelected?.value != null && category.category_id == subCategorySelected.value!!.parentCategoryId) {
                // Box relacionada a la subcategoria seleccionada para la categoría
                Box(
                    modifier = modifier
                        .size(24.dp)
                        .aspectRatio(1f)
                        .background(Color(subCategorySelected.value!!.color), shape = CircleShape)
                        .shadow(elevation = 6.dp, shape = CircleShape)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        painter = painterResource(
                            id = context.resources.getIdentifier(subCategorySelected.value!!.icon, "drawable", context.packageName)
                        ),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
        Text(
            text = category.name,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
    if (subCategorySelectorDialogState?.value == true && navigateToCreateSubCategory != null && subCategorySelected != null) {
        SubCategorySelectorDialog(
            subCategorySelectorDialogState,
            subCategoryList,
            subCategorySelected,
            navigateToCreateSubCategory
        )
    }
}

private fun reorderCategories(
    nearestCenters: Pair<Int, Int>,
    thisOrderPosition: Int,
    lastPosition: Int,
    copiedCategoryList: SnapshotStateList<CategoryBase>,
    listCoordinates: SnapshotStateMap<Int, Rect>,
    thisRect: Rect,
    onFinish: () -> Unit
) {
    val rightmostItem = if (lastPosition >= 5) listCoordinates[4] else listCoordinates[lastPosition - 1]
    if (listCoordinates[0]!!.center.x > thisRect.center.x &&
        listCoordinates[0]!!.bottom > thisRect.center.y) {
        Log.d("onReorderCategories", "Primer posicion")
        val savedList: MutableList<CategoryBase> = mutableListOf()
        savedList.addAll(copiedCategoryList)
        copiedCategoryList.clear()
        savedList.forEachIndexed { index, categoryBase ->
            if (index == thisOrderPosition) {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    0,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            } else if (index > thisOrderPosition) {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    categoryBase.order,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            } else {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    categoryBase.order + 1,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            }
        }
        val orderedList = copiedCategoryList.sortedBy { it.order }.toMutableStateList()
        copiedCategoryList.clear()
        copiedCategoryList.addAll(orderedList)
        onFinish()

    } else if (listCoordinates[lastPosition]!!.center.x < thisRect.center.x &&
        listCoordinates[lastPosition]!!.top < thisRect.center.y) {
        Log.d("onReorderCategories", "Ultima posicion")
        val savedList: MutableList<CategoryBase> = mutableListOf()
        savedList.addAll(copiedCategoryList)
        copiedCategoryList.clear()
        savedList.forEachIndexed { index, categoryBase ->
            if (index == thisOrderPosition) {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    lastPosition,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            } else if (index < thisOrderPosition) {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    categoryBase.order,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            } else {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    categoryBase.order - 1,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            }
        }
        val orderedList = copiedCategoryList.sortedBy { it.order }.toMutableStateList()
        copiedCategoryList.clear()
        copiedCategoryList.addAll(orderedList)
        onFinish()

    } else if (rightmostItem!!.center.x < thisRect.center.x) {
        Log.d("onReorderCategories", "Derecha")
        val savedList: MutableList<CategoryBase> = mutableListOf()
        savedList.addAll(copiedCategoryList)
        copiedCategoryList.clear()
        savedList.forEachIndexed { index, categoryBase ->
            if (thisOrderPosition > nearestCenters.first) {
                if (index == thisOrderPosition) {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        nearestCenters.first + 1,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                } else if (index < thisOrderPosition && index > nearestCenters.first) {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        categoryBase.order + 1,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                } else {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        categoryBase.order,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                }
            } else {
                if (index == thisOrderPosition) {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        nearestCenters.first,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                } else if (index > thisOrderPosition && index < nearestCenters.first + 1) {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        categoryBase.order - 1,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                } else {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        categoryBase.order,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                }
            }
        }
        val orderedList = copiedCategoryList.sortedBy { it.order }.toMutableStateList()
        copiedCategoryList.clear()
        copiedCategoryList.addAll(orderedList)
        onFinish()

    } else if (listCoordinates[0]!!.center.x > thisRect.center.x) {
        Log.d("onReorderCategories", "Izquierda")
        val savedList: MutableList<CategoryBase> = mutableListOf()
        savedList.addAll(copiedCategoryList)
        copiedCategoryList.clear()
        savedList.forEachIndexed { index, categoryBase ->
            if (thisOrderPosition > nearestCenters.first) {
                if (index == thisOrderPosition) {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        nearestCenters.first,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                } else if (index < thisOrderPosition && index > nearestCenters.first - 1) {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        categoryBase.order + 1,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                } else {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        categoryBase.order,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                }
            } else {
                if (index == thisOrderPosition) {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        nearestCenters.first - 1,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                } else if (index > thisOrderPosition && index < nearestCenters.first) {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        categoryBase.order - 1,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                } else {
                    copiedCategoryList.add(Category(
                        categoryBase.category_id,
                        categoryBase.parentCategoryId,
                        categoryBase.name,
                        categoryBase.icon,
                        categoryBase.isIncome,
                        categoryBase.expenseLimit,
                        categoryBase.color,
                        categoryBase.order,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ))
                }
            }
        }
        val orderedList = copiedCategoryList.sortedBy { it.order }.toMutableStateList()
        copiedCategoryList.clear()
        copiedCategoryList.addAll(orderedList)
        onFinish()

    } else if (thisOrderPosition > nearestCenters.first && thisOrderPosition > nearestCenters.second) {
        Log.d("onReorderCategories", "Normal + grande")
        val savedList: MutableList<CategoryBase> = mutableListOf()
        savedList.addAll(copiedCategoryList)
        copiedCategoryList.clear()
        savedList.forEachIndexed { index, categoryBase ->
            val positionToReplace = if (nearestCenters.first < nearestCenters.second) nearestCenters.second else nearestCenters.first
            if (index == thisOrderPosition) {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    positionToReplace,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            } else if (index in positionToReplace until thisOrderPosition) {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    categoryBase.order + 1,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            } else {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    categoryBase.order,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            }
        }
        val orderedList = copiedCategoryList.sortedBy { it.order }.toMutableStateList()
        copiedCategoryList.clear()
        copiedCategoryList.addAll(orderedList)
        onFinish()

    } else if (thisOrderPosition < nearestCenters.first && thisOrderPosition < nearestCenters.second) {
        Log.d("onReorderCategories", "Normal + peque")
        val savedList: MutableList<CategoryBase> = mutableListOf()
        savedList.addAll(copiedCategoryList)
        copiedCategoryList.clear()
        savedList.forEachIndexed { index, categoryBase ->
            val positionToReplace = if (nearestCenters.first < nearestCenters.second) nearestCenters.first else nearestCenters.second
            if (index == thisOrderPosition) {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    positionToReplace,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            } else if (index in thisOrderPosition until positionToReplace + 1) {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    categoryBase.order - 1,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            } else {
                copiedCategoryList.add(Category(
                    categoryBase.category_id,
                    categoryBase.parentCategoryId,
                    categoryBase.name,
                    categoryBase.icon,
                    categoryBase.isIncome,
                    categoryBase.expenseLimit,
                    categoryBase.color,
                    categoryBase.order,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                ))
            }
        }
        val orderedList = copiedCategoryList.sortedBy { it.order }.toMutableStateList()
        copiedCategoryList.clear()
        copiedCategoryList.addAll(orderedList)
        onFinish()
    } else {
        Log.d("onReorderCategories", "Mismo Rect")
        onFinish()
    }
}