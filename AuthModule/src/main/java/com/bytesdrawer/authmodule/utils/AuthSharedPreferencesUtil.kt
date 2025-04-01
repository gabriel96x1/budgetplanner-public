package com.bytesdrawer.authmodule.utils

interface AuthSharedPreferencesUtil {
    fun isDynamicColors(): Boolean
    fun setAuthToken(token: String)

}