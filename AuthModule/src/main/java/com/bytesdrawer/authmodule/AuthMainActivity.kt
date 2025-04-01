package com.bytesdrawer.authmodule

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.bytesdrawer.authmodule.navigation.AuthNavigation
import com.bytesdrawer.authmodule.ui.theme.BudgetPlannerTheme
import com.bytesdrawer.authmodule.utils.AuthSharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthMainActivity : ComponentActivity() {

    @Inject lateinit var preferencesUtil: AuthSharedPreferencesUtil
    private lateinit var viewModel: AuthViewModel

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setContent {
            val dynamicColors = remember {
                mutableStateOf(preferencesUtil.isDynamicColors())
            }
            BudgetPlannerTheme(dynamicColor = dynamicColors.value) {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.padding(24.dp)
                ) {
                    AuthNavigation(
                        navController = navController,
                        viewModel
                    )
                }
            }
        }
    }
}