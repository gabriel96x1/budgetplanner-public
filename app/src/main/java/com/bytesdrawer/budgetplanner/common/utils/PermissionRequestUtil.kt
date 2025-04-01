package com.bytesdrawer.budgetplanner.common.utils

import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.bytesdrawer.budgetplanner.MainActivity

object PermissionRequestUtil {

    fun requestNotificationPermissions(context: MainActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if(!permissionGranted) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }
}