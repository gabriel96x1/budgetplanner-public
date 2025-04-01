package com.bytesdrawer.budgetplanner.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytesdrawer.budgetplanner.R

@Composable
fun GreetingsScreen(
    navigateToMainAccountCreation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = null,
            modifier = Modifier.offset(0.dp, 50.dp)
        )
        Text(
            text = stringResource(R.string.welcome_onboarding),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        Text(
            text = stringResource(R.string.welcome_message_onboarding),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        Button(onClick = {
            navigateToMainAccountCreation()
        }) {
            Text(text = stringResource(R.string.start_button_onboarding))
        }
    }

}