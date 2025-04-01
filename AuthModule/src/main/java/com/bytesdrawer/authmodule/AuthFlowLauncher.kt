package com.bytesdrawer.authmodule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

object AuthFlowLauncher {

    fun launch(context: Context) {
        val intent = Intent(context, AuthMainActivity::class.java)
        context.startActivity(intent)
    }

    fun finishAuthFlow(context: Context) {
        (context as AuthMainActivity).finish()
    }

}