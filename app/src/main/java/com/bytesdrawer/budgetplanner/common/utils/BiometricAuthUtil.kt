package com.bytesdrawer.budgetplanner.common.utils

import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.provider.Settings.ACTION_BIOMETRIC_ENROLL
import android.provider.Settings.ACTION_SECURITY_SETTINGS
import android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.bytesdrawer.budgetplanner.MainActivity
import com.bytesdrawer.budgetplanner.R
import java.util.concurrent.Executor

object BiometricAuthUtil {

    const val BiometricEnrollmentRequestCode = 3000
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    fun hasBiometricCapability(context: Context): Int {
        return BiometricManager
            .from(context)
            .canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
    }

    fun launchSecurityEnrollment(activity: MainActivity) {
        val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
            }
        } else {
            Intent(ACTION_SECURITY_SETTINGS)
        }
        startActivityForResult(activity, enrollIntent, BiometricEnrollmentRequestCode, null)

    }

    fun requestAuth(activity: MainActivity, userAuth: () -> Unit, userDeauth: () -> Unit) {
        executor = ContextCompat.getMainExecutor(activity)
        biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(activity.applicationContext,
                        activity.getString(R.string.failed_authentication_toast), Toast.LENGTH_SHORT)
                        .show()
                    userDeauth()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    userAuth()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(activity.applicationContext, activity.getString(R.string.failed_authentication_toast),
                        Toast.LENGTH_SHORT)
                        .show()
                    userDeauth()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.sign_in_prompt))
            .setSubtitle(activity.getString(R.string.enter_credentials))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}