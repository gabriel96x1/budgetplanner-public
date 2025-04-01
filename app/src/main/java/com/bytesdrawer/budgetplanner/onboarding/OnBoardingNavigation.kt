package com.bytesdrawer.budgetplanner.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.navigation.NavigationScreens
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import com.bytesdrawer.budgetplanner.onboarding.GreetingsScreen
import com.bytesdrawer.budgetplanner.onboarding.MainAccountCreationScreen

@Composable
fun OnBoardingNavigation(
    isOnBoardingComplete: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: MainViewModel,
    screenState: MutableState<NavigationScreens>,
    preferencesUtil: SharedPreferencesUtil
) {
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = context.getString(OnBoardingScreens.GREETINGS.screen)) {
        composable(context.getString(OnBoardingScreens.GREETINGS.screen)) {
            screenState.value = NavigationScreens.ONBOARDING
            GreetingsScreen { navController.navigate(context.getString(OnBoardingScreens.MAIN_ACCOUNT_CREATION.screen)) }
        }
        composable(context.getString(OnBoardingScreens.MAIN_ACCOUNT_CREATION.screen)) {
            screenState.value = NavigationScreens.ONBOARDING
            MainAccountCreationScreen(
                isOnBoardingComplete,
                viewModel,
                preferencesUtil,
            )
        }
    }
}