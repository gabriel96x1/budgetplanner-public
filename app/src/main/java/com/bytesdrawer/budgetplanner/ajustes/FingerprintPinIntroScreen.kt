package com.bytesdrawer.budgetplanner.ajustes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bytesdrawer.budgetplanner.MainActivity
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.utils.BiometricAuthUtil

@Composable
fun FingerprintPinIntroScreen(
    userAuth: () -> Unit,
    activity: MainActivity,
    userDeauth: () -> Unit
) {
    LaunchedEffect(true) {
        BiometricAuthUtil.requestAuth(activity, userAuth, userDeauth)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.autenticate_fingerprint_screen))
        Icon(
            painter = painterResource(id = R.drawable.fingerprint),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clickable { BiometricAuthUtil.requestAuth(activity, userAuth, userDeauth) }
        )
    }
}