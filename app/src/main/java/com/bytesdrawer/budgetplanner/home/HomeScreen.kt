package com.bytesdrawer.budgetplanner.home

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bytesdrawer.budgetplanner.MainActivity
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.composables.DateRangePickerDialog
import com.bytesdrawer.budgetplanner.common.composables.IncomeExpenseSwitchComposable
import com.bytesdrawer.budgetplanner.common.composables.LoadingDialog
import com.bytesdrawer.budgetplanner.common.composables.TimeFrameNavigatorComposable
import com.bytesdrawer.budgetplanner.common.composables.TimeFrameSelectionComposable
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.common.utils.ExcelGenerator
import com.bytesdrawer.budgetplanner.common.utils.GooglePlayAccountManager
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import com.bytesdrawer.budgetplanner.common.utils.compareSelectedDateWithTransactionDate
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.getNameOfTheMonth
import com.bytesdrawer.budgetplanner.graficos.DonutChart
import com.bytesdrawer.budgetplanner.onboarding.ShowcaseSteps
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

const val WRITE_REQUEST_CODE = 101

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedDivisa: MutableState<Divisa>,
    preferencesUtil: SharedPreferencesUtil,
    googleAccountManager: GooglePlayAccountManager?,
    showCasePosition: SnapshotStateMap<String, LayoutCoordinates?>,
    stringDateTime: MutableState<String>,
    timeLapseSelected: MutableState<PeriodOfTime?>,
    excelGenerator: ExcelGenerator,
    categories: List<Category>?,
    incomeExpenseState: MutableState<Boolean>,
    selectedAccount: MutableState<Account?>,
    transactions: List<MoneyMovement>?,
    navController: NavHostController,
    analyticsEvents: Events,
    navigateToAnalytics: () -> Unit,
    navigateToRecurrents: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToCategories: () -> Unit,
) {
    val context = LocalContext.current
    val dateTime = remember {
        mutableStateOf(LocalDateTime.now())
    }
    val dateSelectionDialogState = remember {
        mutableStateOf(false)
    }
    val filteredTransactions = remember {
        mutableStateOf(
            updateTransactions(
                transactions,
                incomeExpenseState,
                selectedAccount,
                timeLapseSelected,
                dateTime,
                stringDateTime
            )
        )
    }
    LaunchedEffect(incomeExpenseState.value) {
        filteredTransactions.value = updateTransactions(
            transactions,
            incomeExpenseState,
            selectedAccount,
            timeLapseSelected,
            dateTime,
            stringDateTime
        )
    }
    LaunchedEffect(selectedAccount.value, transactions) {
        filteredTransactions.value = updateTransactions(
            transactions,
            incomeExpenseState,
            selectedAccount,
            timeLapseSelected,
            dateTime,
            stringDateTime
        )
    }

    val profilePic = remember { mutableStateOf(preferencesUtil.getGooglePhotoUrl()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier
                    .width(160.dp)
                    .height(60.dp)
                    .onGloballyPositioned {
                        showCasePosition[ShowcaseSteps.RECURRENTS.name] = it
                    }
                    .testTag("Recurrent Button"),

                onClick = {
                    analyticsEvents.trackEvent(Events.NAVIGATE_RECURRENT_HOME)
                    navigateToRecurrents()
                })
            {
                Icon(
                    painter = painterResource(id = R.drawable.recurrent_payment),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                Text(text = stringResource(R.string.recurrents_button_home))
            }
            Button(
                modifier = Modifier
                    .width(160.dp)
                    .height(60.dp)
                    .testTag("Profile Button"),
                onClick = {
                    analyticsEvents.trackEvent(Events.NAVIGATE_PROFILE_HOME)
                    navigateToSettings()
                }
            ) {
                if (!profilePic.value.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(profilePic.value)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.profile_circle),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(28.dp)
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(id = R.drawable.profile_circle),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                Text(text = stringResource(R.string.settings_button_home))
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier
                    .width(160.dp)
                    .height(60.dp)
                    .onGloballyPositioned {
                        showCasePosition[ShowcaseSteps.CATEGORIES.name] = it
                    }
                    .testTag("Categories Button"),
                onClick = {
                    analyticsEvents.trackEvent(Events.NAVIGATE_CATEGORIES_HOME)
                    navigateToCategories()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.category),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                Text(text = stringResource(R.string.categories_button_home))
            }
            Button(
                modifier = Modifier
                    .width(160.dp)
                    .height(60.dp)
                    .onGloballyPositioned {
                        showCasePosition[ShowcaseSteps.ANALYTICS.name] = it
                    }
                    .testTag("Analytics Button"),
                onClick = {
                    analyticsEvents.trackEvent(Events.NAVIGATE_ANALYTICS_HOME)
                    navigateToAnalytics()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.analytics),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                Text(text = stringResource(R.string.analytics_button_home))
            }

        }
        IncomeExpenseSwitchComposable(incomeExpenseState = incomeExpenseState, analyticsEvents = analyticsEvents)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimeFrameSelectionComposable(
                    dateTime,
                    timeLapseSelected,
                    dateSelectionDialogState,
                    {
                        filteredTransactions.value = updateTransactions(
                            transactions,
                            incomeExpenseState,
                            selectedAccount,
                            timeLapseSelected,
                            dateTime,
                            stringDateTime
                        )
                    },
                    analyticsEvents = analyticsEvents
                )
                TimeFrameNavigatorComposable(
                    timeLapseSelected = timeLapseSelected,
                    dateTime,
                    stringDateTime,
                    context,
                    analyticsEvents = analyticsEvents,
                    onLeftClicked = {
                        substractTime(dateTime, timeLapseSelected.value)
                        filteredTransactions.value = updateTransactions(
                            transactions,
                            incomeExpenseState,
                            selectedAccount,
                            timeLapseSelected,
                            dateTime,
                            stringDateTime
                        )
                    },
                    onRightClicked = {
                        addTime(dateTime, timeLapseSelected.value)
                        filteredTransactions.value = updateTransactions(
                            transactions,
                            incomeExpenseState,
                            selectedAccount,
                            timeLapseSelected,
                            dateTime,
                            stringDateTime
                        )
                    }
                )
                if (!filteredTransactions.value.isNullOrEmpty()) {
                    val totalTransactions = filteredTransactions.value!!
                        .map { it.amount }
                        .fold(BigDecimal.ZERO) { acc, e -> acc + e }

                    DonutChart(
                        incomeExpenseState = incomeExpenseState.value,
                        totalTransactions = totalTransactions,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(12.dp),
                        colors = getColorsForDonutChart(filteredTransactions.value, categories),
                        inputValues = getAnglesForDonutChart(filteredTransactions.value, categories),
                    )
                    Button(
                        onClick = {
                            excelGenerator.account = selectedAccount.value
                            excelGenerator.periodOfTime = timeLapseSelected.value
                            excelGenerator.textFromDateSelection = textFromDateSelection(
                                dateTime.value,
                                timeLapseSelected.value,
                                stringDateTime.value,
                                context
                            )

                            excelGenerator.categories = categories

                            excelGenerator.transactions = if (selectedAccount.value == null) {
                                transactions?.filter {
                                    compareSelectedDateWithTransactionDate(
                                        timeLapseSelected,
                                        dateTime,
                                        it,
                                        stringDateTime
                                    ) && it.category != "Transferencias_Especial_Plus20"
                                }?.sortedBy { it.date }
                            } else {
                                transactions?.filter {
                                    it.account_id == selectedAccount.value?.account_id
                                            && compareSelectedDateWithTransactionDate(
                                        timeLapseSelected,
                                        dateTime,
                                        it,
                                        stringDateTime
                                    ) && it.category != "Transferencias_Especial_Plus20"
                                }?.sortedBy { it.date }
                            }

                            excelGenerator.fileName = textFromDateSelection(
                                dateTime.value,
                                timeLapseSelected.value,
                                stringDateTime.value,
                                context
                            ).replace(" ", "-") +
                                    "-${LocalDateTime.now()}"
                            analyticsEvents.trackEvent(Events.DOWNLOAD_EXCEL)
                            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                            intent.type = "text/xlsx"
                            intent.addCategory(Intent.CATEGORY_OPENABLE)
                            intent.putExtra(Intent.EXTRA_TITLE, "${excelGenerator.fileName}.xlsx")

                            startActivityForResult(context as MainActivity, intent, WRITE_REQUEST_CODE, null)
                        }
                    ) {
                        Text(text = stringResource(R.string.download_excel_button_home))
                        Icon(
                            painter = painterResource(id = R.drawable.download),
                            contentDescription = null
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.padding(vertical = 12.dp))
                    Text(text = stringResource(R.string.no_data_home))
                    Spacer(modifier = Modifier.padding(vertical = 12.dp))
                }
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 3.dp))
        if (!filteredTransactions.value.isNullOrEmpty()) {

            val totalTransactions = filteredTransactions.value!!
                .map { it.amount }
                .fold(BigDecimal.ZERO) { acc, e -> acc + e }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("Transactions")

            ) {
                val count = remember {
                    mutableStateOf(0)
                }
                categories?.sortedBy { it.order }?.forEachIndexed { index, category ->
                    val list = filteredTransactions.value!!.filter {
                        it.category_id == category.category_id
                    }

                    if (list.isNotEmpty()) {
                        count.value += 1
                        TransactionsByCategoryCard(
                            selectedDivisa,
                            totalTransactions,
                            list,
                            navController,
                            categories,
                            timeLapseSelected.value == PeriodOfTime.MONTH &&
                                    selectedAccount.value == null,
                            analyticsEvents = analyticsEvents,
                        )
                    }
                    if (index == categories.lastIndex && count.value >= 3) {
                        Spacer(modifier = Modifier.padding(24.dp))
                    }
                }
            }
        }
    }

    if (filteredTransactions.value == null && categories == null) {
        LoadingDialog()
    }
    if (dateSelectionDialogState.value) {
        DateRangePickerDialog(
            dateSelectionDialogState,
            stringDateTime,
            timeLapseSelected
        ) {
            filteredTransactions.value = updateTransactions(
                transactions,
                incomeExpenseState,
                selectedAccount,
                timeLapseSelected,
                dateTime,
                stringDateTime
            )
        }
    }
}

fun textFromDateSelection(
    localDateTime: LocalDateTime,
    periodOfTime: PeriodOfTime?,
    stringDateTime: String,
    context: Context
): String {
    return when (periodOfTime) {
        PeriodOfTime.DAY -> "${localDateTime.dayOfMonth}-${getNameOfTheMonth(localDateTime.monthValue, context)}-${localDateTime.year}"
        PeriodOfTime.WEEK -> context.getString(
            R.string.semana,
            localDateTime.get(WeekFields.of(DayOfWeek.FRIDAY, 6).weekOfYear()).toString(),
            localDateTime.year.toString()
        )
        PeriodOfTime.HALF_MONTH -> if (localDateTime.dayOfMonth < 15)
                "${context.getString(R.string.first_half_of_month)} ${getNameOfTheMonth(localDateTime.monthValue, context)} ${localDateTime.year}"
            else
                "${context.getString(R.string.second_half_of_month)} ${getNameOfTheMonth(localDateTime.monthValue, context)} ${localDateTime.year}"
        PeriodOfTime.MONTH -> "${getNameOfTheMonth(localDateTime.monthValue, context)} ${localDateTime.year}"
        PeriodOfTime.YEAR -> localDateTime.year.toString()
        PeriodOfTime.PERSO -> {
            val startDate = dateStringToRegularFormat(stringDateTime.substring(0,16))
            val endDate = dateStringToRegularFormat(stringDateTime.substring(17,33))

            return "${startDate!!.dayOfMonth}/${startDate.monthValue}/${startDate.year} - " +
                    "${endDate!!.dayOfMonth}/${endDate.monthValue}/${endDate.year}"
        }
        else -> context.getString(R.string.no_data_home_error)
    }
}

fun substractTime(localDateTime: MutableState<LocalDateTime>, periodOfTime: PeriodOfTime?) {
    when (periodOfTime) {
        PeriodOfTime.DAY -> localDateTime.value = localDateTime.value.minusDays(1)
        PeriodOfTime.WEEK -> localDateTime.value = localDateTime.value.minusWeeks(1)
        PeriodOfTime.HALF_MONTH -> localDateTime.value = if (localDateTime.value.dayOfMonth > 15 ) {
            localDateTime.value.minusDays(15)
        } else {
            val pastMonth = localDateTime.value.minusMonths(1)
            when (pastMonth.dayOfMonth) {
                14 -> pastMonth.with(TemporalAdjusters.lastDayOfMonth())
                15 -> pastMonth.with(TemporalAdjusters.lastDayOfMonth())
                else -> pastMonth.plusDays(15)
            }
        }
        PeriodOfTime.MONTH -> localDateTime.value = localDateTime.value.minusMonths(1)
        PeriodOfTime.YEAR -> localDateTime.value = localDateTime.value.minusYears(1)
        else -> LocalDateTime.now()
    }
}

fun addTime(localDateTime: MutableState<LocalDateTime>, periodOfTime: PeriodOfTime?) {
    when (periodOfTime) {
        PeriodOfTime.DAY -> localDateTime.value = localDateTime.value.plusDays(1)
        PeriodOfTime.WEEK -> localDateTime.value = localDateTime.value.plusWeeks(1)
        PeriodOfTime.HALF_MONTH -> localDateTime.value = if (localDateTime.value.dayOfMonth < 14 ) {
            localDateTime.value.plusDays(15)
        } else {
            val nextMonth = localDateTime.value.plusMonths(1)
            when (nextMonth.dayOfMonth) {
                14 -> nextMonth.with(TemporalAdjusters.firstDayOfMonth())
                15 -> nextMonth.with(TemporalAdjusters.firstDayOfMonth())
                else -> nextMonth.minusDays(15)
            }
        }
        PeriodOfTime.MONTH -> localDateTime.value = localDateTime.value.plusMonths(1)
        PeriodOfTime.YEAR -> localDateTime.value = localDateTime.value.plusYears(1)
        else -> LocalDateTime.now()
    }
}

@Composable
private fun getColorsForDonutChart(
    filteredTransactions: List<MoneyMovement>?,
    categories: List<Category>?
): List<Color> {
    val listToReturn = mutableListOf<Color>()

    if (!filteredTransactions.isNullOrEmpty()) {
        categories?.forEach { category ->
            if (!filteredTransactions.none { it.category_id == category.category_id }) {
                listToReturn.add(Color(category.color))
            }
        }
    } else {
        listToReturn.add(Color.Blue)
    }
    return listToReturn
}

private fun getAnglesForDonutChart(
    filteredTransactions: List<MoneyMovement>?,
    categories: List<Category>?
): List<Float> {
    if (!filteredTransactions.isNullOrEmpty()) {
        val listToReturn = mutableListOf<Float>()
        val totalTransactions = filteredTransactions
            .map { it.amount }
            .fold(BigDecimal.ZERO) { acc, e -> acc + e }

        categories?.forEach { category ->
            if (!filteredTransactions.none { it.category_id == category.category_id }) {
                var totalAmount = BigDecimal.ZERO

                filteredTransactions.filter { it.category_id == category.category_id }.forEach {
                    totalAmount = totalAmount.add(it.amount)
                }

                val percentage = if (totalTransactions != null) {
                    totalAmount.toFloat() / totalTransactions.toFloat()
                } else 0f

                listToReturn.add((percentage*360f)/100)
            }
        }
        Log.d("Values: ", "$listToReturn | ")
        return listToReturn
    } else {
        return listOf(360f)
    }
}

private fun updateTransactions(
    transactions: List<MoneyMovement>?,
    incomeExpenseState: MutableState<Boolean>,
    selectedAccount: MutableState<Account?>,
    timeLapseSelected: MutableState<PeriodOfTime?>,
    dateTime: MutableState<LocalDateTime>,
    stringDateTime: MutableState<String>
): List<MoneyMovement>? {
    return if (selectedAccount.value == null) {
        transactions?.filter {
            it.isIncome == incomeExpenseState.value
                    && compareSelectedDateWithTransactionDate(timeLapseSelected, dateTime, it, stringDateTime)
                    && it.category != "Transferencias_Especial_Plus20"

            }
    } else {
        transactions?.filter {
            it.isIncome == incomeExpenseState.value
                    && it.account_id == selectedAccount.value?.account_id
                    && compareSelectedDateWithTransactionDate(
                timeLapseSelected,
                dateTime,
                it,
                stringDateTime
            ) && it.category != "Transferencias_Especial_Plus20"

        }
    }
}