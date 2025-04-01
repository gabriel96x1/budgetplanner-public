package com.bytesdrawer.budgetplanner.common.utils

import android.content.Context
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bytesdrawer.authmodule.AuthViewModel
import com.bytesdrawer.budgetplanner.BuildConfig
import com.bytesdrawer.budgetplanner.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class GooglePlayAccountManager(
    private val context: Context,
    private val preferencesUtil: SharedPreferencesUtil,
    private val authViewModel: AuthViewModel
) {
    private val lifecycleScope = (context as MainActivity).lifecycleScope

    private val _signInStatus = MutableStateFlow(false)
    val signInStatus: StateFlow<Boolean?> get() = _signInStatus

    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestProfile()
        .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    fun tryGetUser() {
        val account = getUser()
        setPreferences(account)
        lifecycleScope.launch {
            _signInStatus.emit(true)
        }
    }

    fun getUser(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun logOut() {
        googleSignInClient.signOut()
        removePreferences()
        lifecycleScope.launch {
            _signInStatus.emit(false)
        }
    }

    fun signIn() {
        (context as MainActivity).startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    fun handleResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            setPreferences(account)
            lifecycleScope.launch {
                _signInStatus.emit(true)
            }
        } catch (e: ApiException) {
            Log.w("GooglePlayAccountManager", "signInResult:failed code=" + e.statusCode)
            lifecycleScope.launch {
                _signInStatus.emit(false)
            }
        }
    }

    private fun removePreferences() {
        preferencesUtil.setGoogleEmail("")
        preferencesUtil.setGoogleUserName("")
        preferencesUtil.setGooglePhotoUrl("")
    }
    private fun setPreferences(account: GoogleSignInAccount?) {
        account?.let {
            preferencesUtil.setGoogleUserName(
                if (!it.displayName.isNullOrEmpty()) {
                    it.displayName!!
                } else {
                    ""
                }
            )
            preferencesUtil.setGooglePhotoUrl(
                if (it.photoUrl != null) {
                    it.photoUrl.toString()
                } else {
                    ""
                }
            )
            preferencesUtil.setGoogleEmail(
                if (!it.email.isNullOrEmpty()) {
                    it.email!!
                } else {
                    ""
                }
            )
        }
    }

    companion object {
        const val RC_SIGN_IN = 200
    }

}