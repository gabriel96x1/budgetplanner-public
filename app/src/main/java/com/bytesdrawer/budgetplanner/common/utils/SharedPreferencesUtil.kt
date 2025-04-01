package com.bytesdrawer.budgetplanner.common.utils

import android.content.SharedPreferences
import com.bytesdrawer.authmodule.utils.AuthSharedPreferencesUtil

class SharedPreferencesUtil(private val sharedPreferences: SharedPreferences):
    AuthSharedPreferencesUtil {

    private val ONBOARDING_COMPLETED = "OnBoarding flow"
    private val INIT_TIMES = "INIT_TIMES"
    private val DYNAMIC_COLOR = "DynamicColor"
    private val ACCOUNT_CREATED = "account created"
    private val APP_SECURITY = "APP_SECURITY"
    private val QA_PAID_USER = "QA_PAID_USER"
    private val DIVISA = "divisa"
    private val REVIEWED_APP = "reviewed_app"
    private val PROMOTION_TIME = "promotion_time"
    private val AUTH_TOKEN = "auth_token"
    private val GOOGLE_USER_NAME = "google_user_name"
    private val GOOGLE_PHOTO = "google_photo"
    private val GOOGLE_EMAIL = "google_email"
    private val editor = sharedPreferences.edit()

    // Auth & profile
    fun setGoogleEmail(string: String) {
        editor.apply {
            putString(GOOGLE_EMAIL, string).apply()
        }
    }

    fun getGoogleEmail(): String? {
        return sharedPreferences.getString(GOOGLE_EMAIL, "")
    }

    fun setGoogleUserName(string: String) {
        editor.apply {
            putString(GOOGLE_USER_NAME, string).apply()
        }
    }

    fun getGoogleUserName(): String? {
        return sharedPreferences.getString(GOOGLE_USER_NAME, "")
    }

    fun setGooglePhotoUrl(string: String) {
        editor.apply {
            putString(GOOGLE_PHOTO, string).apply()
        }
    }

    fun getGooglePhotoUrl(): String? {
        return sharedPreferences.getString(GOOGLE_PHOTO, "")
    }
    override fun setAuthToken(token: String) {
        editor.apply {
            putString(AUTH_TOKEN, token).apply()
        }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN, "")
    }

    // Common
    fun isOnBoardingComplete(): Boolean {
        return sharedPreferences.getBoolean(ONBOARDING_COMPLETED, false)
    }

    override fun isDynamicColors(): Boolean {
        return sharedPreferences.getBoolean(DYNAMIC_COLOR, false)
    }

    fun isAppReviewed(): Boolean {
        return sharedPreferences.getBoolean(REVIEWED_APP, false)
    }

    fun setReviewedApp() {
        editor.apply {
            putBoolean(REVIEWED_APP, true).apply()
        }
    }

    fun getPromoTime(): Long {
        return sharedPreferences.getLong(PROMOTION_TIME, 0L)
    }

    fun setPromoTime(promoTime: Long) {
        editor.apply {
            putLong(PROMOTION_TIME, promoTime).apply()
        }
    }


    fun getInitTimes(): Int {
        return sharedPreferences.getInt(INIT_TIMES, 0)
    }

    fun setInitTimes(times: Int) {
        editor.apply {
            putInt(INIT_TIMES, times).apply()
        }
    }

    fun setDynamicColors(active: Boolean) {
        editor.apply {
            putBoolean(DYNAMIC_COLOR, active).apply()
        }
    }

    fun setOnBoardingCompleted() {
        editor.apply {
            putBoolean(ONBOARDING_COMPLETED, true).apply()
        }
    }

    fun getGlobalDivisa(): String? {
        return sharedPreferences.getString(DIVISA, Divisa.USD.name)
    }

    fun setGlobalDivisa(divisa: Divisa) {
        editor.apply {
            putString(DIVISA, divisa.name).apply()
        }
    }

    fun setAccountCreated() {
        editor.apply {
            putBoolean(ACCOUNT_CREATED, true).apply()
        }
    }

    fun setSecurityEnabled(enabled: Boolean) {
        editor.apply {
            putBoolean(APP_SECURITY, enabled).apply()
        }
    }

    fun isSecurityEnabled(): Boolean {
        return sharedPreferences.getBoolean(APP_SECURITY, false)
    }

    fun setQaPaidUser(enabled: Boolean) {
        editor.apply {
            putBoolean(QA_PAID_USER, enabled).apply()
        }
    }

    fun isQaPaidUser(): Boolean {
        return sharedPreferences.getBoolean(QA_PAID_USER, false)
    }

}