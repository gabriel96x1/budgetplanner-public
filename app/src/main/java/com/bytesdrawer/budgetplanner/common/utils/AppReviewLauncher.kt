package com.bytesdrawer.budgetplanner.common.utils

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.bytesdrawer.budgetplanner.MainActivity
import com.bytesdrawer.budgetplanner.R
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode


class AppReviewLauncher(
    private val activity: MainActivity,
    private val preferencesUtil: SharedPreferencesUtil,
    private val initTimes: Int,
    private val analyticsEvents: Events
) {

    private val manager = ReviewManagerFactory.create(activity)

    fun launchIntegratedReview() {
        if (!preferencesUtil.isAppReviewed() && initTimes >= 2) {
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        analyticsEvents.trackEvent(Events.IN_APP_REVIEW_PROMPT_SHOWN)
                        preferencesUtil.setReviewedApp()
                    }
                } else {
                    try {
                        @ReviewErrorCode val reviewErrorCode = (task.exception as ReviewException).errorCode
                        Log.e("ReviewError", reviewErrorCode.toString())
                    } catch (_: Exception) {

                    }
                }
            }
        }
    }

    fun launchPlayStoreReview() {
        analyticsEvents.trackEvent(Events.NAVIGATE_TO_REVIEW_ON_PLAYSTORE)
        val packageName: String = activity.packageName
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(
            Intent.EXTRA_TEXT,
            activity.getString(R.string.playstore_rate_message)
        )
        activity.startActivity(intent)
    }
}