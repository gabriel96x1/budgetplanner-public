package com.bytesdrawer.authmodule.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bytesdrawer.authmodule.AuthViewModel
import com.bytesdrawer.authmodule.screens.ForgotPasswordScreen
import com.bytesdrawer.authmodule.screens.MailVerificationScreen
import com.bytesdrawer.authmodule.screens.PasswordChangeScreen
import com.bytesdrawer.authmodule.screens.SignInScreen
import com.bytesdrawer.authmodule.screens.SignUpScreen

@Composable
fun AuthNavigation(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    NavHost(navController = navController, startDestination = AuthScreens.SIGN_IN.name) {
        composable(AuthScreens.SIGN_IN.name) {
            SignInScreen(
                viewModel,
                { navController.navigate(AuthScreens.FORGOT_PASSWORD.name) }
            ) { navController.navigate(AuthScreens.SIGN_UP.name) }
        }
        composable(AuthScreens.SIGN_UP.name) {
            SignUpScreen(viewModel, navController)
        }
        composable(AuthScreens.PASSWORD_CHANGE.name) {
            PasswordChangeScreen()
        }
        composable(
            route = "${AuthScreens.MAIL_VERIFICATION.name}/{value}",
            arguments = listOf(
                navArgument("value") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            MailVerificationScreen(
                it.arguments?.getString("value"),
                viewModel
            )
        }
        composable(AuthScreens.FORGOT_PASSWORD.name) {
            ForgotPasswordScreen()
        }
    }
}