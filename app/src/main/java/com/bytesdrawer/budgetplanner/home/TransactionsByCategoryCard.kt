package com.bytesdrawer.budgetplanner.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.composables.CategoryIcon
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.navigation.NavigationScreens
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.common.utils.bigDecimalParsed
import com.bytesdrawer.budgetplanner.ui.theme.GreenMoney
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun TransactionsByCategoryCard(
    selectedDivisa: MutableState<Divisa>,
    totalTransactions: BigDecimal?,
    listOfTransactions: List<MoneyMovement>,
    navController: NavHostController,
    categories: List<Category>,
    isMonthPeriod: Boolean,
    analyticsEvents: Events? = null
) {
    val context = LocalContext.current
    val decimalFormat = remember { DecimalFormat("#.#") }
    decimalFormat.roundingMode = RoundingMode.DOWN
    var totalAmount = BigDecimal.ZERO
    val category = remember {
        categories.first { it.category_id == listOfTransactions.first().category_id }
    }
    val subCategories = remember {
        mutableStateOf(categories.filter { it.parentCategoryId == category.category_id })
    }
    val subCategoriesExpanded = remember {
        mutableStateOf(false)
    }
    val df = remember {
        DecimalFormat("#,###.##")
    }
    df.roundingMode = RoundingMode.DOWN

    listOfTransactions.forEach {
        totalAmount = totalAmount.add(it.amount)
    }

    val percentage = if (totalTransactions != null) {
        (totalAmount.toDouble() / totalTransactions.toDouble()).toFloat()
    } else 0f

    val percentageExpenseLimit = if (category.expenseLimit.toInt() != 0) {
        (totalAmount.toDouble() / category.expenseLimit.toDouble()).toFloat()
    } else 0f

    Card(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
            .clickable {
                analyticsEvents?.trackEvent(Events.NAVIGATE_TO_TRANSACTIONS_BY_CATEGORY)
                navController.navigate(
                    "${context.getString(NavigationScreens.TRANSACCIONS_BY_CATEGORY.screen)}/${listOfTransactions.first().category_id}"
                )
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryIcon(context, category)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(.7f)
            ) {
                Text(text = listOfTransactions.first().category)
                Spacer(modifier = Modifier.padding(vertical = 3.dp))
                Text(
                    maxLines = 1,
                    text = if (listOfTransactions.first().isIncome)
                        "+ ${bigDecimalParsed(totalAmount, df)}"
                    else
                        "- ${bigDecimalParsed(totalAmount, df)}",
                    color = if (listOfTransactions.first().isIncome)
                        if (isSystemInDarkTheme()) Color.Green else GreenMoney
                    else
                        Color.Red
                )
            }
            Column(
                modifier = Modifier.weight(.18f),
                horizontalAlignment = Alignment.End
            ) {
                if (subCategories.value.isNotEmpty()) {
                    Icon(
                        modifier = Modifier.clickable { subCategoriesExpanded.value = !subCategoriesExpanded.value },
                        painter = painterResource(
                            id = if (subCategoriesExpanded.value) {
                                R.drawable.arrow_drop_down
                            } else {
                                R.drawable.arrow_drop_up
                            }
                        ),
                        contentDescription = null
                    )
                }
                Text(
                    maxLines = 1,
                    text = "${decimalFormat.format(percentage * 100)}%"
                )
            }
        }
        if (category.expenseLimit.toInt() != 0 && isMonthPeriod) {
            Text(
                text = stringResource(
                    R.string.budget_home,
                    bigDecimalParsed(category.expenseLimit, df)
                ),
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (percentageExpenseLimit <= 1.0f) {
                    LinearProgressIndicator(
                        modifier = Modifier.height(8.dp),
                        progress = percentageExpenseLimit,
                        color = Color(category.color),
                        strokeCap = StrokeCap.Round,
                        trackColor = Color.White
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.height(8.dp),
                        progress = 1.0f,
                        color = Color(category.color),
                        strokeCap = StrokeCap.Round,
                        trackColor = Color.White
                    )
                }
                Text(
                    text = "${decimalFormat.format(percentageExpenseLimit * 100)}%",
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.padding(bottom = 6.dp))
        }
        if (subCategoriesExpanded.value) {
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .background(Color.Gray)
                    .padding(horizontal = 6.dp)
                    .fillMaxWidth()
            )
            Column(
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                subCategories.value.forEach { subCategory ->
                    val moneyOnSubcategoryTransactions = listOfTransactions.filter {
                        it.subCategory_id == subCategory.category_id
                    }.sumOf { it.amount }

                    if (moneyOnSubcategoryTransactions.toInt() > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, bottom = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            CategoryIcon(context, subCategory)
                            Text(
                                modifier = Modifier.weight(.6f),
                                text = subCategory.name,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                modifier = Modifier.weight(.3f),
                                text = "${bigDecimalParsed(moneyOnSubcategoryTransactions, df)} ${selectedDivisa.value.name}"
                            )
                        }
                    }
                }
            }
        }
    }
}