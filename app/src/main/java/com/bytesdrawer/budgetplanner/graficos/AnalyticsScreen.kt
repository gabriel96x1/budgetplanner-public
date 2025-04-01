package com.bytesdrawer.budgetplanner.graficos

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.composables.TimeFrameSelectionComposable
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.bigDecimalParsed
import com.bytesdrawer.budgetplanner.common.utils.compareSelectedDateWithTransactionDate
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.intToCalendarName
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import com.bytesdrawer.budgetplanner.home.addTime
import com.bytesdrawer.budgetplanner.home.substractTime
import com.bytesdrawer.budgetplanner.home.textFromDateSelection
import com.bytesdrawer.budgetplanner.ui.theme.GreenMoney
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.YearMonth

@Composable
fun AnalyticsScreen(
    stringDateTime: MutableState<String>,
    timeLapseSelected: MutableState<PeriodOfTime?>,
    selectedAccount: MutableState<Account?>,
    selectedDivisa: MutableState<Divisa>,
    transactions: List<MoneyMovement>?,
    accountSelectionVisible: MutableState<Boolean>,
    accountsTotal: BigDecimal,
) {

    val context = LocalContext.current

    val dateTime = remember {
        mutableStateOf(LocalDateTime.now())
    }

    val dateSelectionDialogState = remember {
        mutableStateOf(false)
    }

    val filteredTransactionsIncome = remember {
        mutableStateOf(
            updateTransactions(
                transactions,
                true,
                selectedAccount,
                timeLapseSelected,
                dateTime,
                stringDateTime
            )
        )
    }

    val filteredTransactionsExpense = remember {
        mutableStateOf(
            updateTransactions(
                transactions,
                false,
                selectedAccount,
                timeLapseSelected,
                dateTime,
                stringDateTime
            )
        )
    }

    val filteredTransactionsByPeriodOfTimeIncome = remember {
        mutableStateOf(
            getTransactionsParsedByPeriodOfTime(
                filteredTransactionsIncome.value,
                timeLapseSelected,
                dateTime
            )
        )
    }

    val filteredTransactionsByPeriodOfTimeExpense = remember {
        mutableStateOf(
            getTransactionsParsedByPeriodOfTime(
                filteredTransactionsExpense.value,
                timeLapseSelected,
                dateTime
            )
        )
    }

    val filteredForLineChart = remember {
        mutableStateOf(
            getTransactionsForLineChart(
                filteredTransactionsByPeriodOfTimeIncome.value,
                filteredTransactionsByPeriodOfTimeExpense.value,
                accountsTotal
            )
        )
    }

    val df = remember {
        DecimalFormat("#,###.##")
    }
    df.roundingMode = RoundingMode.DOWN

    LaunchedEffect(selectedAccount.value, transactions) {

        filteredTransactionsIncome.value = updateTransactions(
            transactions,
            true,
            selectedAccount,
            timeLapseSelected,
            dateTime,
            stringDateTime
        )

        filteredTransactionsExpense.value = updateTransactions(
            transactions,
            false,
            selectedAccount,
            timeLapseSelected,
            dateTime,
            stringDateTime
        )

        filteredTransactionsByPeriodOfTimeIncome.value = getTransactionsParsedByPeriodOfTime(
            filteredTransactionsIncome.value,
            timeLapseSelected,
            dateTime
        )

        filteredTransactionsByPeriodOfTimeExpense.value = getTransactionsParsedByPeriodOfTime(
            filteredTransactionsExpense.value,
            timeLapseSelected,
            dateTime
        )

        filteredForLineChart.value = getTransactionsForLineChart(
            filteredTransactionsByPeriodOfTimeIncome.value,
            filteredTransactionsByPeriodOfTimeExpense.value,
            accountsTotal
        )

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 70.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.account_analytics_screen))
        Row(
            modifier = Modifier
                .clickable { accountSelectionVisible.value = !accountSelectionVisible.value }
                .padding()
        ) {
            Text(text = if (selectedAccount.value == null) "Total" else selectedAccount.value!!.name)
            Icon(
                painter = painterResource(id = R.drawable.arrow_drop_down),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        Card {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TimeFrameSelectionComposable(
                        dateTime,
                        timeLapseSelected,
                        dateSelectionDialogState,
                        {
                            filteredTransactionsIncome.value = updateTransactions(
                                transactions,
                                true,
                                selectedAccount,
                                timeLapseSelected,
                                dateTime,
                                stringDateTime
                            )

                            filteredTransactionsExpense.value = updateTransactions(
                                transactions,
                                false,
                                selectedAccount,
                                timeLapseSelected,
                                dateTime,
                                stringDateTime
                            )

                            filteredTransactionsByPeriodOfTimeIncome.value = getTransactionsParsedByPeriodOfTime(
                                filteredTransactionsIncome.value,
                                timeLapseSelected,
                                dateTime
                            )

                            filteredTransactionsByPeriodOfTimeExpense.value = getTransactionsParsedByPeriodOfTime(
                                filteredTransactionsExpense.value,
                                timeLapseSelected,
                                dateTime
                            )

                            filteredForLineChart.value = getTransactionsForLineChart(
                                filteredTransactionsByPeriodOfTimeIncome.value,
                                filteredTransactionsByPeriodOfTimeExpense.value,
                                accountsTotal
                            )
                        },
                        supressPerso = true
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (timeLapseSelected.value == PeriodOfTime.PERSO) Arrangement.Center else Arrangement.SpaceBetween
                ) {
                    if (timeLapseSelected.value != PeriodOfTime.PERSO) {
                        Icon(
                            modifier = Modifier.clickable {
                                substractTime(dateTime, timeLapseSelected.value)
                                filteredTransactionsIncome.value = updateTransactions(
                                    transactions,
                                    true,
                                    selectedAccount,
                                    timeLapseSelected,
                                    dateTime,
                                    stringDateTime
                                )

                                filteredTransactionsExpense.value = updateTransactions(
                                    transactions,
                                    false,
                                    selectedAccount,
                                    timeLapseSelected,
                                    dateTime,
                                    stringDateTime
                                )
                                filteredTransactionsByPeriodOfTimeIncome.value = getTransactionsParsedByPeriodOfTime(
                                    filteredTransactionsIncome.value,
                                    timeLapseSelected,
                                    dateTime
                                )

                                filteredTransactionsByPeriodOfTimeExpense.value = getTransactionsParsedByPeriodOfTime(
                                    filteredTransactionsExpense.value,
                                    timeLapseSelected,
                                    dateTime
                                )

                                filteredForLineChart.value = getTransactionsForLineChart(
                                    filteredTransactionsByPeriodOfTimeIncome.value,
                                    filteredTransactionsByPeriodOfTimeExpense.value,
                                    accountsTotal
                                )
                            },
                            painter = painterResource(id = R.drawable.left),
                            contentDescription = null
                        )
                    }
                    Text(
                        text = textFromDateSelection(dateTime.value, timeLapseSelected.value, stringDateTime.value, context),
                        fontSize = 14.sp
                    )
                    if (timeLapseSelected.value != PeriodOfTime.PERSO) {
                        Icon(
                            modifier = Modifier.clickable {
                                addTime(dateTime, timeLapseSelected.value)
                                filteredTransactionsIncome.value = updateTransactions(
                                    transactions,
                                    true,
                                    selectedAccount,
                                    timeLapseSelected,
                                    dateTime,
                                    stringDateTime
                                )

                                filteredTransactionsExpense.value = updateTransactions(
                                    transactions,
                                    false,
                                    selectedAccount,
                                    timeLapseSelected,
                                    dateTime,
                                    stringDateTime
                                )

                                filteredTransactionsByPeriodOfTimeIncome.value = getTransactionsParsedByPeriodOfTime(
                                    filteredTransactionsIncome.value,
                                    timeLapseSelected,
                                    dateTime
                                )

                                filteredTransactionsByPeriodOfTimeExpense.value = getTransactionsParsedByPeriodOfTime(
                                    filteredTransactionsExpense.value,
                                    timeLapseSelected,
                                    dateTime
                                )

                                filteredForLineChart.value = getTransactionsForLineChart(
                                    filteredTransactionsByPeriodOfTimeIncome.value,
                                    filteredTransactionsByPeriodOfTimeExpense.value,
                                    accountsTotal
                                )

                            },
                            painter = painterResource(id = R.drawable.right),
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
                if (
                    !filteredTransactionsIncome.value.isNullOrEmpty()
                    || !filteredTransactionsExpense.value.isNullOrEmpty()
                    ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = stringResource(R.string.total_income_analytics_screen)
                        )
                        Text(
                            text = "+${
                                filteredTransactionsIncome.value!!
                                    .sumOf { it.amount }
                            } ${selectedDivisa.value.name}",
                            color = if (isSystemInDarkTheme()) Color.Green else GreenMoney

                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = stringResource(R.string.total_expense_analytics_screen),
                        )
                        Text(
                            text = "-${
                                filteredTransactionsExpense.value!!
                                    .sumOf { it.amount }
                            } ${selectedDivisa.value.name}",
                            color = Color.Red
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = stringResource(R.string.final_balance_analytics_screen),
                        )
                        Text(
                            text = "${
                                filteredTransactionsIncome.value!!
                                    .sumOf { it.amount }.subtract(
                                        filteredTransactionsExpense.value!!
                                            .sumOf { it.amount }
                                    )
                            } ${selectedDivisa.value.name}",
                            color = if (
                                filteredTransactionsIncome.value!!
                                    .sumOf { it.amount }.subtract(
                                        filteredTransactionsExpense.value!!
                                            .sumOf { it.amount }
                                    ) < BigDecimal.ZERO
                            ) {
                                Color.Red
                            } else { if (isSystemInDarkTheme()) Color.Green else GreenMoney }
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_data_analytics_screen),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
            }
        }
        if (
            !filteredTransactionsIncome.value.isNullOrEmpty()
            || !filteredTransactionsExpense.value.isNullOrEmpty()
        ) {
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Card {
                Text(
                    text = stringResource(R.string.variaci_n_de_fondos),
                    modifier = Modifier
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 24.dp, top = 28.dp)
                ) {
                    Text(text = bigDecimalParsed(filteredForLineChart.value.max(), df) +
                            " ${selectedDivisa.value.name}")
                    Spacer(
                        modifier = Modifier
                            .width(10.dp)
                            .height(1.dp)
                            .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                    Row {
                        Spacer(
                            modifier = Modifier
                                .width(1.dp)
                                .height(200.dp)
                                .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                        )
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            val width = getWidthForLineChart(timeLapseSelected)
                            LineChart(
                                modifier = Modifier.width(width),
                                height = filteredForLineChart.value.map { it.toFloat() },
                                highestValue = filteredForLineChart.value.max().toFloat(),
                                minorValue = filteredForLineChart.value.min().toFloat()
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .width(10.dp)
                            .height(1.dp)
                            .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                    Text(text = bigDecimalParsed(filteredForLineChart.value.min(), df) +
                            " ${selectedDivisa.value.name}")
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Card {
                ChartHeader(timeLapseSelected)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 28.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val width = getWidthForChart(timeLapseSelected)
                    val firstColor = if (isSystemInDarkTheme()) Color.Green else GreenMoney
                    MultiBarChart(
                        values = Pair(
                            filteredTransactionsByPeriodOfTimeIncome.value,
                            filteredTransactionsByPeriodOfTimeExpense.value,
                        ).toMultiBarItems(firstColor),
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        highestValue = if (filteredTransactionsExpense.value!!
                                .sumOf { it.amount } > filteredTransactionsIncome.value!!
                                .sumOf { it.amount }) filteredTransactionsExpense.value!!
                            .sumOf { it.amount } else filteredTransactionsIncome.value!!
                            .sumOf { it.amount },
                        width = width
                    )
                    XMarkers(timeLapseSelected, dateTime, width)
                }
                ChartFooter()
            }
        }
    }
}

private fun getTransactionsForLineChart(
    filteredTransactionsByPeriodOfTimeIncome: List<List<MoneyMovement>>,
    filteredTransactionsByPeriodOfTimeExpense: List<List<MoneyMovement>>,
    balance: BigDecimal
): List<BigDecimal> {
    val incomeList = mutableListOf<BigDecimal>()
    val expenseList = mutableListOf<BigDecimal>()
    filteredTransactionsByPeriodOfTimeIncome.forEach { item ->
        incomeList.add( item.sumOf { it.amount } )
    }
    filteredTransactionsByPeriodOfTimeExpense.forEach { item ->
        expenseList.add( item.sumOf { it.amount } )
    }

    val sumList = mutableListOf<BigDecimal>()

    var accumulatedBalance = balance

    incomeList.forEachIndexed { index, element ->
        val currentOperation = (element - expenseList[index])
        sumList.add(currentOperation + accumulatedBalance)
        if (!currentOperation.equals(0)) {
            accumulatedBalance += currentOperation
        }
    }

    Log.d("LineChartList", sumList.toString())

    return sumList
}

private fun getWidthForChart(
    timeLapseSelected: MutableState<PeriodOfTime?>
): Dp {
    return when (timeLapseSelected.value) {
        PeriodOfTime.DAY -> 1000.dp
        PeriodOfTime.WEEK -> 300.dp
        PeriodOfTime.MONTH -> 1400.dp
        PeriodOfTime.YEAR -> 500.dp
        else -> 300.dp
    }
}

private fun getWidthForLineChart(
    timeLapseSelected: MutableState<PeriodOfTime?>
): Dp {
    return when (timeLapseSelected.value) {
        PeriodOfTime.DAY -> 600.dp
        PeriodOfTime.WEEK -> 400.dp
        PeriodOfTime.MONTH -> 800.dp
        PeriodOfTime.YEAR -> 400.dp
        else -> 300.dp
    }
}

@Composable
private fun ChartFooter() {
    Row(
        modifier = Modifier
            .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = if (isSystemInDarkTheme()) Color.Green else GreenMoney,
                    shape = CircleShape
                ),
        )
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = stringResource(id = R.string.income_lowecase),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.padding(horizontal = 12.dp))
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = Color.Red,
                    shape = CircleShape
                ),
        )
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = stringResource(id = R.string.expenses_lowecase),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ChartHeader(
    timeLapseSelected: MutableState<PeriodOfTime?>
) {
    when (timeLapseSelected.value) {
        PeriodOfTime.DAY -> Text(
            text = stringResource(R.string.day_hour_chart_header),
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        PeriodOfTime.WEEK -> Text(
            text = stringResource(R.string.days_of_week_chart_header),
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        PeriodOfTime.MONTH -> Text(
            text = stringResource(R.string.days_of_the_month_chart_header),
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        PeriodOfTime.YEAR -> Text(
            text = stringResource(R.string.months_chart_header),
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        else -> Text(text = stringResource(R.string.days_chart_header))
    }

}

@Composable
private fun XMarkers(
    timeLapseSelected: MutableState<PeriodOfTime?>,
    dateTime: MutableState<LocalDateTime>,
    width: Dp,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.width(width),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        when (timeLapseSelected.value) {
            PeriodOfTime.DAY -> {
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(5.dp)
                        .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                )
                for (i in 0..23) {
                    Text(text = intToCalendarName(i, PeriodOfTime.DAY, context))
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(5.dp)
                            .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                }
            }
            PeriodOfTime.WEEK -> {
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(5.dp)
                        .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                )
                for (i in 1..7) {
                    Text(text = intToCalendarName(i, PeriodOfTime.WEEK, context))
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(5.dp)
                            .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                }
            }
            PeriodOfTime.MONTH -> {
                val yearMonthObject = YearMonth.of(dateTime.value.year, dateTime.value.month)
                val daysInMonth = yearMonthObject.lengthOfMonth()
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(5.dp)
                        .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                )
                for (i in 1..daysInMonth) {
                    Text(text = intToCalendarName(i, PeriodOfTime.MONTH, context))
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(5.dp)
                            .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                }
            }
            PeriodOfTime.YEAR -> {
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(5.dp)
                        .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                )
                for (i in 1..12) {
                    Text(text = intToCalendarName(i, PeriodOfTime.YEAR, context))
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(5.dp)
                            .background(if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                }
            }
            else -> {}
        }

    }
}

private fun Pair<List<List<MoneyMovement>>, List<List<MoneyMovement>>>.toMultiBarItems(firstColor: Color): List<MultiBarData> {
    val incomeList = mutableListOf<Double>()
    val expenseList = mutableListOf<Double>()
    val multiBarDataList = mutableListOf<MultiBarData>()
    first.forEach { item ->
        incomeList.add( item.sumOf { it.amount }.toDouble() )
    }
    second.forEach { item ->
        expenseList.add( item.sumOf { it.amount }.toDouble() )
    }

    val staticIncomeList = incomeList.toList()
    val staticExpenseList = expenseList.toList()

    for (i in first.indices) {
        multiBarDataList.add(
            MultiBarData(
                values = listOf(staticIncomeList[i], staticExpenseList[i]),
                colors = listOf(firstColor, Color.Red),
            )
        )
    }
    return multiBarDataList
}

private fun updateTransactions(
    transactions: List<MoneyMovement>?,
    incomeExpenseState: Boolean?,
    selectedAccount: MutableState<Account?>,
    timeLapseSelected: MutableState<PeriodOfTime?>,
    dateTime: MutableState<LocalDateTime>,
    stringDateTime: MutableState<String>
): List<MoneyMovement>? {
    return if (selectedAccount.value == null && incomeExpenseState != null) {
        transactions?.filter {
            it.isIncome == incomeExpenseState
                    && compareSelectedDateWithTransactionDate(timeLapseSelected, dateTime, it, stringDateTime)
                    && it.category != "Transferencias_Especial_Plus20"

        }
    } else if (incomeExpenseState != null && selectedAccount.value != null) {
        transactions?.filter {
            it.isIncome == incomeExpenseState
                    && it.account_id == selectedAccount.value?.account_id
                    && compareSelectedDateWithTransactionDate(
                timeLapseSelected,
                dateTime,
                it,
                stringDateTime
            ) && it.category != "Transferencias_Especial_Plus20"

        }
    } else if (incomeExpenseState == null && selectedAccount.value == null) {
        transactions?.filter {
                    it.account_id == selectedAccount.value?.account_id
                    && compareSelectedDateWithTransactionDate(
                timeLapseSelected,
                dateTime,
                it,
                stringDateTime
            ) && it.category != "Transferencias_Especial_Plus20"

        }
    } else {
        transactions?.filter {
            compareSelectedDateWithTransactionDate(
                timeLapseSelected,
                dateTime,
                it,
                stringDateTime
            ) && it.category != "Transferencias_Especial_Plus20"

        }
    }
}

private fun getTransactionsParsedByPeriodOfTime(
    transactions: List<MoneyMovement>?,
    timeLapseSelected: MutableState<PeriodOfTime?>,
    dateTime: MutableState<LocalDateTime>,
): List<List<MoneyMovement>> {
    val parsedList = mutableListOf<List<MoneyMovement>>()

    when(timeLapseSelected.value) {
        PeriodOfTime.DAY -> {
            for (i in 0..23) {
                val hourList = transactions?.filter { dateStringToRegularFormat(it.date)?.hour == i }
                if (hourList.isNullOrEmpty()) {
                    parsedList.add(emptyList())
                } else {
                    parsedList.add(hourList)
                }
            }
            return parsedList
        }
        PeriodOfTime.WEEK -> {
            for (i in 1..7) {
                val daysList = transactions?.filter { dateStringToRegularFormat(it.date)?.dayOfWeek?.value == i }
                if (daysList.isNullOrEmpty()) {
                    parsedList.add(emptyList())
                } else {
                    parsedList.add(daysList)
                }
            }
            return parsedList
        }
        PeriodOfTime.MONTH -> {
            val yearMonthObject = YearMonth.of(dateTime.value.year, dateTime.value.month)
            val daysInMonth = yearMonthObject.lengthOfMonth()

            for (i in 1..daysInMonth) {
                val daysList = transactions?.filter { dateStringToRegularFormat(it.date)?.dayOfMonth == i }
                if (daysList.isNullOrEmpty()) {
                    parsedList.add(emptyList())
                } else {
                    parsedList.add(daysList)
                }
            }
            return parsedList
        }
        PeriodOfTime.YEAR -> {
            for (i in 1..12) {
                val monthsList = transactions?.filter { dateStringToRegularFormat(it.date)?.monthValue == i }
                if (monthsList.isNullOrEmpty()) {
                    parsedList.add(emptyList())
                } else {
                    parsedList.add(monthsList)
                }
            }
            return parsedList
        }
        else -> {
            for (i in 0..23) {
                val hourList = transactions?.filter { dateStringToRegularFormat(it.date)?.hour == i }
                if (hourList.isNullOrEmpty()) {
                    parsedList.add(emptyList())
                } else {
                    parsedList.add(hourList)
                }
            }
            return parsedList
        }
    }
}