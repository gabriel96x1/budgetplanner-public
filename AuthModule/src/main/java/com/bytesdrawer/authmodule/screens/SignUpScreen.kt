package com.bytesdrawer.authmodule.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bytesdrawer.authmodule.AuthViewModel
import com.bytesdrawer.authmodule.R
import com.bytesdrawer.authmodule.models.CredentialsData
import com.bytesdrawer.authmodule.navigation.AuthScreens

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(viewModel: AuthViewModel, navController: NavHostController) {

    val signUpStatus = viewModel.signUpStatus.collectAsState().value

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val buttonEnabled = remember { mutableStateOf(true) }
    val emailError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }

    buttonEnabled.value = email.value.isNotEmpty() && password.value.isNotEmpty()
    emailError.value = email.value.isEmpty()
    passwordError.value = password.value.isEmpty()

    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(signUpStatus) {
        if (signUpStatus == true) {
            navController.navigate("${AuthScreens.MAIL_VERIFICATION.name}/{value}")
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
        MailAndPassword(email, password, buttonEnabled, viewModel)
    }
}

@Composable
private fun MailAndPassword(
    email: MutableState<String>,
    password: MutableState<String>,
    buttonEnabled: MutableState<Boolean>,
    viewModel: AuthViewModel
) {
    val showPassword = remember {
        mutableStateOf(false)
    }
    Text(
        text = stringResource(R.string.sign_up),
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
        label = { Text(stringResource(R.string.mail_onboarding)) }
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
    Spacer(modifier = Modifier.padding(vertical = 8.dp))
    Button(
        enabled = buttonEnabled.value,
        onClick = {
            viewModel.signUp(CredentialsData(
                email.value,
                password.value
            ))
        }
    ) {
        Text(text = stringResource(R.string.continue_button_signup))
    }
}