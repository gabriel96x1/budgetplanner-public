package com.bytesdrawer.authmodule.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytesdrawer.authmodule.AuthViewModel
import com.bytesdrawer.authmodule.R
import com.bytesdrawer.authmodule.models.ConfirmationData

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MailVerificationScreen(email: String?, viewModel: AuthViewModel) {
    val codeToVerify = remember { mutableStateOf("") }

    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val signUpCodeConfirmationStatus = viewModel.signUpCodeConfirmationStatus.collectAsState().value

    LaunchedEffect(signUpCodeConfirmationStatus) {
        if (signUpCodeConfirmationStatus == true) {

        } else {

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.verification_code_message),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = false,
            value = codeToVerify.value,
            onValueChange = { codeToVerify.value = it },
            label = { Text(stringResource(R.string.verification_code_entry)) }
        )
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        Button(onClick = { viewModel.signUpCodeConfirmation(
            ConfirmationData(
                email.toString(),
                codeToVerify.value
            )
        ) }) {
            Text(text = stringResource(R.string.verification_code_button))
        }
    }
}