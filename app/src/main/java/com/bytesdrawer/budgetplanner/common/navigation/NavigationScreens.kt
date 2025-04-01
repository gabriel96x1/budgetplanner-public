package com.bytesdrawer.budgetplanner.common.navigation

import com.bytesdrawer.budgetplanner.R


enum class NavigationScreens(val screen: Int) {
    TRANSACCION(R.string.create_transaction_screen),
    EDIT_TRANSACTION(R.string.edit_transaction_screen),
    TRANSACCIONS_BY_CATEGORY(R.string.transactions_history_screen),
    ANALISIS(R.string.analytics_screen),
    HOME(R.string.home_screen),
    ACCOUNT(R.string.account_screen),
    ACCOUNT_TRANSFERENCE_SUMMARY(R.string.transference_summary_screen),
    ADD_ACCOUNT_TRANSFERENCE(R.string.transference_between_accounts_screen),
    PAGOS_RECURRENTES(R.string.recurrent_movements_screen),
    NUEVO_RECURRENTE(R.string.new_movement_screen),
    EDIT_RECURRENTE(R.string.edit_movement_screen),
    SETTINGS(R.string.settings_screen),
    ONBOARDING(R.string.onboarding_screen),
    ADD_ACCOUNT(R.string.add_account_screen),
    EDIT_ACCOUNT(R.string.edit_account_screen),
    CATEGORY(R.string.categories_screen),
    ADD_CATEGORY(R.string.create_category_screen),
    ADD_SUBCATEGORY(R.string.create_subcategory_screen),
    EDIT_CATEGORY(R.string.edit_category_screen),
    EDIT_SUBCATEGORY(R.string.edit_subcategory_screen);
}