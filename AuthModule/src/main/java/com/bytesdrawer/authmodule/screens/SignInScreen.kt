package com.bytesdrawer.authmodule.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytesdrawer.authmodule.AuthFlowLauncher
import com.bytesdrawer.authmodule.AuthViewModel
import com.bytesdrawer.authmodule.R
import com.bytesdrawer.authmodule.models.CredentialsData

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    navigateToForgottenPass: () -> Unit,
    navigateToSignUp: () -> Unit
) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val buttonEnabled = remember { mutableStateOf(false) }
    val emailError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }

    val signInStatus = viewModel.signInStatus.collectAsState().value

    buttonEnabled.value = email.value.isNotEmpty() && password.value.isNotEmpty()
    emailError.value = email.value.isEmpty()
    passwordError.value = password.value.isEmpty()

    val showPassword = remember {
        mutableStateOf(false)
    }

    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(signInStatus) {
        if (signInStatus == true) {

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
            text = stringResource(R.string.sign_in),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = false,
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(stringResource(R.string.mail_onboarding)) },
        )
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = false,
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(stringResource(R.string.password_onboarding)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        id = if (showPassword.value) {
                            R.drawable.visibility_on
                        } else {
                            R.drawable.visibility_off
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        showPassword.value = !showPassword.value
                    }
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { navigateToForgottenPass() },
            ) {
                Text(
                    text = stringResource(R.string.forgotten_pass),
                    fontSize = 12.sp,
                )
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.signUp(CredentialsData(
                email.value,
                password.value
            )) }
        ) {
            Text(text = stringResource(R.string.init_session_button_onboarding))
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navigateToSignUp() }
        ) {
            Text(text = stringResource(R.string.register_button_onboarding))
        }
        Spacer(modifier = Modifier.height(200.dp))
        TextButton(onClick = { AuthFlowLauncher.finishAuthFlow(context) }) {
            Text(text = stringResource(R.string.continue_no_register))
        }
    }
}