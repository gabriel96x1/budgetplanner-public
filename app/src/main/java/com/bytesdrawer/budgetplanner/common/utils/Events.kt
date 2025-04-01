package com.bytesdrawer.budgetplanner.common.utils

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

class Events(context: Context) {

    private var analytics: FirebaseAnalytics? = null

    init {
        analytics = FirebaseAnalytics.getInstance(context)
    }

    fun trackEvent(event: String, ) {
        analytics!!.logEvent(event, null)
    }

    companion object {
        const val ACCOUNT_DELETED = "account_deleted"
        const val ACCOUNT_SELECTED_CHANGE_BOTTOM_SHEET = "account_selected_change_bottom_sheet"
        const val ACCOUNT_TRANSFER_TRANSFER_MONEY = "account_transfer_transfer_money"
        const val ADD_EDIT_CATEGORY_SEL_ICON = "add_edit_category_sel_icon"
        const val ADD_EDIT_CATEGORY_SEL_COLOR = "add_edit_category_sel_color"
        const val ADD_EDIT_CATEGORY_USE_EXPENSE_LIMIT = "add_edit_category_use_expense_limit"
        const val ADD_EDIT_CATEGORY_NO_EXPENSE_LIMIT = "add_edit_category_no_expense_limit"
        const val CATEGORY_EDITED = "category_edited"
        const val CATEGORY_CREATED = "category_created"
        const val CATEGORY_REORDER_START = "category_reorder_start"
        const val CATEGORY_REORDER_DONE = "category_reorder_done"
        const val CATEGORY_REORDER_CANCEL = "category_reorder_cancel"
        const val CATEGORY_DELETE = "category_delete"
        const val CREATE_TRANSACTION_CHANGE_ACCOUNT = "create_transaction_change_account"
        const val CREATE_TRANSACTION_CLOSE_CALC = "create_transaction_close_calc"
        const val CREATE_TRANSACTION_OPEN_CALC = "create_transaction_open_calc"
        const val CREATE_TRANSACTION_DATE_TODAY ="create_transaction_date_today"
        const val CREATE_TRANSACTION_DATE_YESTERDAY = "create_transaction_date_yesterday"
        const val CREATE_TRANSACTION_DATE_PERSO = "create_transaction_date_perso"
        const val CREATE_TRANSACTION_CHANGE_DATE = "create_transaction_change_date"
        const val CREATE_TRANSACTION_W_COMMENT= "create_transaction_w_comment"
        const val CREATE_TRANSACTION_NO_COMMENT= "create_transaction_no_comment"
        const val CREATE_NEW_ACCOUNT = "create_new_money_account"
        const val DISMISS_ACCOUNT_SELECTION_BOTTOM_SHEET = "dismiss_account_selection_bottom_sheet"
        const val DOWNLOAD_EXCEL = "download_excel"
        const val EDIT_TRANSACTION_CHANGE_ACCOUNT = "edit_transaction_change_account"
        const val EDIT_TRANSACTION_CLOSE_CALC = "edit_transaction_close_calc"
        const val EDIT_TRANSACTION_OPEN_CALC = "edit_transaction_open_calc"
        const val EDIT_TRANSACTION_CHANGE_DATE = "edit_transaction_change_date"
        const val EDIT_TRANSACTION_W_COMMENT= "edit_transaction_w_comment"
        const val EDIT_TRANSACTION_NO_COMMENT= "edit_transaction_no_comment"
        const val IN_APP_REVIEW_PROMPT_SHOWN = "in_app_review_prompt_shown"
        const val NAVIGATE_RECURRENT_HOME = "navigate_recurrents_home"
        const val NAVIGATE_CATEGORIES_HOME = "navigate_categories_home"
        const val NAVIGATE_PROFILE_HOME = "navigate_profile_home"
        const val NAVIGATE_ANALYTICS_HOME = "navigate_analytics_home"
        const val NAVIGATE_TO_EDIT_ACCOUNT = "navigate_to_edit_account"
        const val NAVIGATE_TO_ADD_NEW_ACCOUNT = "navigate_to_add_new_account"
        const val NAVIGATE_TO_ADD_NEW_ACCOUNT_TRANSFER = "navigate_to_add_new_account_transfer"
        const val NAVIGATE_TO_CREATE_ACCOUNT_TRANSFER = "navigate_to_create_account_transfer"
        const val NAVIGATE_TO_CREATE_NEW_MONEY_MOVEMENT = "navigate_to_create_new_money_movement"
        const val NAVIGATE_TO_CREATE_CATEGORY = "navigate_to_create_category"
        const val NAVIGATE_TO_EDIT_CATEGORY = "navigate_to_edit_category"
        const val NAVIGATE_TO_REVIEW_ON_PLAYSTORE = "navigate_to_review_on_playstore"
        const val NAVIGATE_TO_TRANSACTIONS_BY_CATEGORY = "navigate_to_transactions_by_category"
        const val OPEN_ACCOUNTS_BOTTOM_SHEET_HOME = "open_accounts_bottom_sheet_home"
        const val SAVE_ACCOUNT_CHANGES = "save_account_changes"
        const val TIMEFRAME_SELECTED_DAY = "timeframe_selected_day"
        const val TIMEFRAME_SELECTED_WEEK = "timeframe_selected_week"
        const val TIMEFRAME_SELECTED_MONTH = "timeframe_selected_month"
        const val TIMEFRAME_SELECTED_YEAR = "timeframe_selected_year"
        const val TIMEFRAME_SELECTED_PERSO = "timeframe_selected_perso"
        const val TIMEFRAME_NAVIGATION_LEFT = "timeframe_navigation_left"
        const val TIMEFRAME_NAVIGATION_RIGHT = "timeframe_navigation_right"
        const val EXPENSE_SELECTED = "expense_selected"
        const val INCOME_SELECTED = "expense_selected"
    }

}