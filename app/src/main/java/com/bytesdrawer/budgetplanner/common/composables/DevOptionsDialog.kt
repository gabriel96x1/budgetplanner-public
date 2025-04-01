package com.bytesdrawer.budgetplanner.common.composables

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.remote.AccountRemote
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.math.BigDecimal

@Composable
fun DevOptionsDialog(
    devOptionsState: MutableState<Boolean>,
    qaPaidUser: MutableState<Boolean>,
    preferencesUtil: SharedPreferencesUtil,
    viewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    Dialog(onDismissRequest = { devOptionsState.value = !devOptionsState.value }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "DevOptions", fontSize = 24.sp)
                Spacer(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "QA Paid User", fontSize = 20.sp)
                    Switch(checked = qaPaidUser.value, onCheckedChange = { switchValue ->
                        qaPaidUser.value = switchValue
                        preferencesUtil.setQaPaidUser(switchValue)
                    })
                }
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        val response = viewModel.getAccountRepository().getAccountsFromNetwork()
                        if (response.isSuccessful) {
                            Log.d("Lambda Response", response.body().toString())
                        }
                    }
                }) {
                    Text(text = "Get All Accounts")
                }
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        val response = viewModel.getAccountRepository().getAccountFromNetwork(2)
                        if (response.isSuccessful) {
                            Log.d("Lambda Response", response.body().toString())
                        }
                    }
                }) {
                    Text(text = "Account")
                }
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        val response = viewModel.getAccountRepository().createAccountOnNetwork(
                            Account(
                                account_id = 1,
                                balance = BigDecimal.TEN,
                                name = "Gabriel",
                                isSent = false,
                                timeStamp = "",
                                toDelete = false
                            )
                        )
                        if (response.isSuccessful) {
                            Log.d("Lambda Response", response.message())
                        }
                    }
                }) {
                    Text(text = "Create Account")
                }
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
            }
        }
    }
}