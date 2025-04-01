package com.bytesdrawer.budgetplanner.ajustes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bytesdrawer.budgetplanner.MainActivity
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.composables.BuyCTADialog
import com.bytesdrawer.budgetplanner.common.composables.DevOptionsDialog
import com.bytesdrawer.budgetplanner.common.composables.DivisaSelectionDialog
import com.bytesdrawer.budgetplanner.common.utils.AppReviewLauncher
import com.bytesdrawer.budgetplanner.common.utils.BiometricAuthUtil
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.GooglePlayAccountManager
import com.bytesdrawer.budgetplanner.common.utils.PurchasesManager
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedDivisa: MutableState<Divisa>,
    preferencesUtil: SharedPreferencesUtil,
    viewModel: MainViewModel,
    activity: MainActivity,
    dynamicColors: MutableState<Boolean>,
    qaPaidUser: MutableState<Boolean>,
    appReviewLauncher: AppReviewLauncher,
    googleAccountManager: GooglePlayAccountManager,
    purchasesManger: PurchasesManager,
    navigateUp: () -> Unit
) {
    val isSecurityEnabled = viewModel.isSecurityEnabled.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }
    val securityEnrollmentDialogState = remember {
        mutableStateOf(false)
    }
    val deleteAllDataDialogState = remember {
        mutableStateOf(false)
    }
    val divisaDialogState = remember {
        mutableStateOf(false)
    }
    val devOptionsState = remember { mutableStateOf(false) }
    val buyCTADialogState = remember { mutableStateOf(false) }

    val profilePic = remember { mutableStateOf(preferencesUtil.getGooglePhotoUrl()) }
    val profileName = remember { mutableStateOf(preferencesUtil.getGoogleUserName()) }

    val signInStatus = googleAccountManager.signInStatus.collectAsState().value

    /*LaunchedEffect(signInStatus) {
        profilePic.value = preferencesUtil.getGooglePhotoUrl()
        profileName.value = preferencesUtil.getGoogleUserName()
    }

    LaunchedEffect(true) {
        if (googleAccountManager.getUser() != null) {
            googleAccountManager.tryGetUser()
        }
    }*/

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 66.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
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
                    .size(150.dp)
            )
        } else {
            Icon(
                modifier = Modifier.size(150.dp),
                painter = painterResource(id = R.drawable.profile_circle),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        if (!profileName.value.isNullOrEmpty()) {
            Text(
                text = profileName.value!!, fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        Spacer(modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .height(2.dp)
            .background(MaterialTheme.colorScheme.onBackground)
        )

        /*if (googleAccountManager.getUser() == null) {
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            googleAccountManager.signIn()
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.sign_in_settings), fontSize = 20.sp)
                Icon(
                    painter = painterResource(id = R.drawable.login),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 12.dp))
        }*/

        when (BiometricAuthUtil.hasBiometricCapability(activity)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = stringResource(R.string.enable_security), fontSize = 20.sp)
                        Text(text = stringResource(R.string.enable_security_settings_description), fontSize = 12.sp)
                    }
                    Switch(checked = isSecurityEnabled.value, onCheckedChange = { switchValue ->
                        if (switchValue) {
                            viewModel.authUser()
                            viewModel.enableSecurity()
                        } else {
                            viewModel.disableSecurity()
                        }
                    })
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = stringResource(R.string.enable_security), fontSize = 20.sp)
                        Text(text = stringResource(R.string.enable_security_settings_description_no_lockscreen), fontSize = 12.sp)
                    }
                    Switch(checked = isSecurityEnabled.value, onCheckedChange = { switchValue ->
                        if (switchValue) {
                            securityEnrollmentDialogState.value = !securityEnrollmentDialogState.value
                        }
                    })
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(0.6f)
            ) {
                Text(text = stringResource(R.string.currency_settings), fontSize = 20.sp)
                Text(text = stringResource(R.string.settings_currency_description), fontSize = 12.sp)
            }
            TextField(
                enabled = false,
                modifier = Modifier
                    .weight(.4f)
                    .clickable { divisaDialogState.value = !divisaDialogState.value },
                readOnly = true,
                value = selectedDivisa.value.name,
                onValueChange = { },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = divisaDialogState.value
                    )
                },
                colors = ExposedDropdownMenuDefaults
                    .textFieldColors(
                        disabledTextColor = if (isSystemInDarkTheme())
                            Color.White
                        else
                            Color.Black,
                        disabledTrailingIconColor = if (isSystemInDarkTheme())
                            Color.White
                        else
                            Color.Black,
                        disabledIndicatorColor = if (isSystemInDarkTheme())
                            Color.White
                        else
                            Color.Black,
                    )
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(modifier = Modifier.padding(vertical = 6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Dynamic Color", fontSize = 20.sp)
                    Text(text = stringResource(R.string.dynamic_colors_description), fontSize = 12.sp)
                }
                Switch(checked = dynamicColors.value, onCheckedChange = { switchValue ->
                    dynamicColors.value = switchValue
                    preferencesUtil.setDynamicColors(switchValue)
                })
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        appReviewLauncher.launchPlayStoreReview()
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.rate_the_app), fontSize = 20.sp)
            Icon(
                painter = painterResource(id = R.drawable.star),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        val url = "https://www.bytesdrawer.com/BPPrivacyPolicy"
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        activity.startActivity(intent)
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = stringResource(R.string.privacy_policy_text), fontSize = 20.sp)
                Text(text = stringResource(R.string.privacy_policy_detail), fontSize = 12.sp)
            }
            Icon(
                painter = painterResource(id = R.drawable.right),
                contentDescription = stringResource(R.string.privacy_policy_detail)
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        deleteAllDataDialogState.value = !deleteAllDataDialogState.value
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = stringResource(R.string.delete_data), fontSize = 20.sp)
                Text(text = stringResource(R.string.delete_data_description), fontSize = 12.sp)
            }
            Icon(
                painter = painterResource(id = R.drawable.delete),
                tint = Color.Red,
                contentDescription = stringResource(R.string.delete_data)
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        /*Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        buyCTADialogState.value = !buyCTADialogState.value
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.open_buy_CTA), fontSize = 20.sp)
            Icon(
                painter = painterResource(id = R.drawable.panorama),
                contentDescription = null
            )
        }*/

        /*Spacer(modifier = Modifier.padding(vertical = 12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    devOptionsState.value = !devOptionsState.value
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "DevOptions", fontSize = 20.sp)
            Icon(
                painter = painterResource(id = R.drawable.right),
                contentDescription = null
            )
        }
         */

        if (googleAccountManager.getUser() != null) {
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            googleAccountManager.logOut()
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.log_out_settings), fontSize = 20.sp, color = Color.Red)
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = null,
                    tint = Color.Red
                )
            }

        }
    }

    if (securityEnrollmentDialogState.value) {
        SecurityEnrollmentDialog(securityEnrollmentDialogState, activity, viewModel)
    }

    if (deleteAllDataDialogState.value) {
        DeleteAllDataDialog(deleteAllDataDialogState, viewModel, LocalContext.current, navigateUp)
    }

    if (divisaDialogState.value) {
        DivisaSelectionDialog(
            selectedDivisa,
            divisaDialogState
        ) { preferencesUtil.setGlobalDivisa(it) }
    }

    if (devOptionsState.value) {
        DevOptionsDialog(devOptionsState, qaPaidUser, preferencesUtil, viewModel)
    }

    if (buyCTADialogState.value) {
        BuyCTADialog(dialogState = buyCTADialogState, preferencesUtil = preferencesUtil, purchasesManger)
    }
}

@Composable
private fun SecurityEnrollmentDialog(
    securityEnrollmentDialogState: MutableState<Boolean>,
    activity: MainActivity,
    viewModel: MainViewModel,
) {
    Dialog(onDismissRequest = {
        securityEnrollmentDialogState.value = !securityEnrollmentDialogState.value
        viewModel.disableSecurity()
    }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Justify,
                    text = stringResource(R.string.activate_security_settings_dialog)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        BiometricAuthUtil.launchSecurityEnrollment(activity)
                        securityEnrollmentDialogState.value = !securityEnrollmentDialogState.value
                    }) {
                        Text(text = stringResource(R.string.continue_button))
                    }
                    TextButton(onClick = {
                        viewModel.disableSecurity()
                        securityEnrollmentDialogState.value = !securityEnrollmentDialogState.value
                    }) {
                        Text(text = stringResource(R.string.cancel_button))
                    }
                }
            }
        }
    }
}

// Se crea un dialogo para confirmar la eliminación de todos los datos es composable
@Composable
private fun DeleteAllDataDialog(
    deleteAllDataDialogState: MutableState<Boolean>,
    viewModel: MainViewModel,
    context: Context,
    navigateUp: () -> Unit
) {
    Dialog(onDismissRequest = {
        deleteAllDataDialogState.value = !deleteAllDataDialogState.value
    }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Justify,
                    text = stringResource(R.string.delete_data),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    textAlign = TextAlign.Justify,
                    text = stringResource(R.string.delete_data_dialog_description),
                )
                Text(
                    modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                    textAlign = TextAlign.Justify,
                    text = stringResource(R.string.delete_data_dialog_description_alert),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            viewModel.deleteAllData(context = context, navigateUp)
                            deleteAllDataDialogState.value = !deleteAllDataDialogState.value
                        },
                        colors = ButtonDefaults.buttonColors(Color.Red)
                    ) {
                        Text(text = stringResource(R.string.continue_button), color = Color.White)
                    }
                    Button(onClick = {
                        deleteAllDataDialogState.value = !deleteAllDataDialogState.value
                    }) {
                        Text(text = stringResource(R.string.cancel_button))
                    }
                }
            }
        }
    }
}


