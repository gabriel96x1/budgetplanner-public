package com.bytesdrawer.budgetplanner.recurrents

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.navigation.NavigationScreens
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.toMillis
import java.time.LocalDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecurrentsScreen(
    recurrentsTabState: MutableState<Int>,
    notifications: List<NotificationModel>?,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val titles = remember {
        listOf(context.getString(R.string.expenses_lowecase), context.getString(R.string.income_lowecase))
    }
    val filteredNotifications = if (recurrentsTabState.value == 1) {
        notifications?.filter { it.isIncome }?.sortedByDescending {
            dateStringToRegularFormat(it.nextDateToShow)!!.toMillis() > LocalDateTime.now().toMillis()
        }
    } else {
        notifications?.filter { !it.isIncome }?.sortedByDescending {
            dateStringToRegularFormat(it.nextDateToShow)!!.toMillis() > LocalDateTime.now().toMillis()
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 66.dp),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Fab(
                navigateToCreateNotification = {
                    navController.navigate(
                        "${context.getString(NavigationScreens.NUEVO_RECURRENTE.screen)}/${recurrentsTabState.value}"
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            TabRow(
                selectedTabIndex = recurrentsTabState.value,
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = recurrentsTabState.value == index,
                        onClick = { recurrentsTabState.value = index },
                        text = { Text(text = title) }
                    )
                }
            }
            if (filteredNotifications!!.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 6.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(id = R.drawable.twilight),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(100.dp)
                    )
                    if (recurrentsTabState.value == 0) {
                        Text(text = stringResource(R.string.no_recurrent_expenses))
                    } else if (recurrentsTabState.value == 1) {
                        Text(text = stringResource(R.string.no_recurrent_incomes))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 50.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val count = remember {
                        mutableStateOf(0)
                    }
                    filteredNotifications.forEachIndexed { index , it ->
                        count.value += 1
                        NotificationItem(it, viewModel = viewModel) {
                            navController.navigate(
                                "${context.getString(NavigationScreens.EDIT_RECURRENTE.screen)}/${it.notification_id}"
                            )
                        }

                        if (index == filteredNotifications.lastIndex && count.value >= 3) {
                            Spacer(modifier = Modifier.padding(24.dp))
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun Fab(navigateToCreateNotification: () -> Unit) {
    FloatingActionButton(onClick = {
        navigateToCreateNotification()
    }) {
        Icon(painter = painterResource(id = R.drawable.add), contentDescription = null)
    }
}