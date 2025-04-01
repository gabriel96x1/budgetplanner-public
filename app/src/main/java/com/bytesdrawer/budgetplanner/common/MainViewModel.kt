package com.bytesdrawer.budgetplanner.common

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytesdrawer.budgetplanner.MainActivity
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.data.AccountRepository
import com.bytesdrawer.budgetplanner.common.data.AccountTransferRepository
import com.bytesdrawer.budgetplanner.common.data.CategoryRepository
import com.bytesdrawer.budgetplanner.common.data.MoneyMovementRepository
import com.bytesdrawer.budgetplanner.common.data.NotificationModelRepository
import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDb
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.utils.ExcelGenerator
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferencesUtil,
    private val accountRepository: AccountRepository,
    private val accountTransferRepository: AccountTransferRepository,
    private val categoryRepository: CategoryRepository,
    private val moneyMovementRepository: MoneyMovementRepository,
    private val notificationModelRepository: NotificationModelRepository

): ViewModel() {
    private val _accountsList = MutableLiveData<List<Account>>()
    val accountsList: LiveData<List<Account>> = _accountsList

    private val _transactionsList = MutableLiveData<List<MoneyMovement>>()
    val transactionsList: LiveData<List<MoneyMovement>> = _transactionsList

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    private val _notificationList = MutableLiveData<List<NotificationModel>>()
    val notificationList: LiveData<List<NotificationModel>> = _notificationList

    private val _accountTransfersList = MutableLiveData<List<AccountTransfer>>()
    val accountTransfersList: LiveData<List<AccountTransfer>> = _accountTransfersList

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> get() = _showSnackBar

    private val _fileLocation = MutableStateFlow(Uri.parse(""))
    val fileLocation: StateFlow<Uri> get() = _fileLocation

    private val _userAuth = MutableStateFlow(false)
    val userAuth: StateFlow<Boolean> get() = _userAuth

    private val _isSecurityEnabled = MutableStateFlow(false)
    val isSecurityEnabled: StateFlow<Boolean> get() = _isSecurityEnabled

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading


    init {
        _isSecurityEnabled.value = sharedPreferences.isSecurityEnabled()
    }

    fun getAccountRepository(): AccountRepository {
        return accountRepository
    }

    fun disableSecurity() {
        _isSecurityEnabled.value = false
        sharedPreferences.setSecurityEnabled(false)
    }

    fun enableSecurity() {
        _isSecurityEnabled.value = true
        sharedPreferences.setSecurityEnabled(true)
    }

    fun authUser() {
        _userAuth.value = true
    }

    fun deauthUser() {
        _userAuth.value = false
    }

    fun getAccounts() {
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.getAccounts().collect {

                _accountsList.postValue(it)
            }
        }
    }

    fun getTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            moneyMovementRepository.getTransactions().collect {
                _transactionsList.postValue(it)
            }
        }
    }

    fun getAccountTransfers() {
        viewModelScope.launch(Dispatchers.IO) {
            accountTransferRepository.getAccountTransfers().collect {
                _accountTransfersList.postValue(it)
            }
        }
    }

    fun createOrUpdateAccount(account: Account) {
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.createOrUpdateAccount(account)
        }
    }

    fun createOrUpdateTransaction(moneyMovement: MoneyMovement) {

        viewModelScope.launch(Dispatchers.IO) {
            moneyMovementRepository.createOrUpdateTransaction(moneyMovement)
        }
    }

    fun createOrUpdateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.createOrUpdateCategory(category)
        }
    }

    fun createOrUpdateAccountTransfer(accountTransfer: AccountTransfer, context: Context) {

        if (_categoryList.value?.none { it.name == "Transferencias_Especial_Plus20" } == true) {
            viewModelScope.launch(Dispatchers.IO) {
                categoryRepository.createOrUpdateCategory(
                    Category(name = "Transferencias_Especial_Plus20", icon = context.resources.getResourceEntryName(R.drawable.repeat), isIncome = false, expenseLimit = BigDecimal.ZERO, color = Color.Gray.toArgb().toLong(), order = 1000000000 ,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    )
                )
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            accountTransferRepository.createOrUpdateAccountTransfer(accountTransfer)
        }
    }

    fun deleteAccount(account: Account) {
        val transactions = _transactionsList.value?.filter { it.account_id == account.account_id }
        transactions?.forEach {
            deleteTransaction(it)
        }

        val accountTransfers = _accountTransfersList.value
            ?.filter { it.from_account_id == account.account_id || it.to_account_id == account.account_id }

        accountTransfers?.forEach {
            deleteAccountTransfer(it)
        }

        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.deleteAccount(account)
        }
    }

    fun deleteTransaction(moneyMovement: MoneyMovement) {
        viewModelScope.launch(Dispatchers.IO) {
            moneyMovementRepository.deleteTransaction(moneyMovement)
        }
    }

    fun deleteAccountTransfer(accountTransfer: AccountTransfer) {
        viewModelScope.launch(Dispatchers.IO) {
            accountTransferRepository.deleteAccountTransfer(accountTransfer)
        }
    }

    fun getCategoryItems() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.getCategories().collect {
                _categoryList.postValue(it)
            }
        }
    }

    fun generateExcel(
        activity: MainActivity,
        excelGenerator: ExcelGenerator,
        data: Uri
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            excelGenerator.generate(data, activity)
            _showSnackBar.value = true
            _fileLocation.value = data
        }
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun updateTransactionsWithNewCategory(category: Category, subCategory: Category? = null) {
        val transactionsToUpdate = if (subCategory == null) {
            _transactionsList.value?.filter {
                it.category_id == category.category_id && it.subCategory_id == null
            }
        } else {
            _transactionsList.value?.filter {
                it.category_id == category.category_id && it.subCategory_id == subCategory.category_id
            }
        }

        transactionsToUpdate?.forEach {
            createOrUpdateTransaction(
                if (subCategory == null) {
                    MoneyMovement(
                        it.movement_id,
                        it.account_id,
                        category.category_id,
                        null,
                        it.amount,
                        category.name,
                        "",
                        it.comment,
                        category.icon,
                        it.isIncome,
                        it.date,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    )
                } else {
                    MoneyMovement(
                        it.movement_id,
                        it.account_id,
                        category.category_id,
                        subCategory.category_id,
                        it.amount,
                        category.name,
                        "",
                        it.comment,
                        category.icon,
                        it.isIncome,
                        it.date,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    )
                }

            )
        }
    }

    fun deleteCategory(category: Category, subCategoryList: List<Category>? = emptyList()) {

        val otherCategoryIncome: Category = _categoryList.value!!.first {
            it.category_id == 10000L
        }

        val otherCategoryExpense: Category = _categoryList.value!!.first {
            it.category_id == 10001L
        }

        subCategoryList?.forEach {
            deleteSubcategory(
                it,
                if (category.isIncome)
                    otherCategoryIncome
                else
                    otherCategoryExpense
            )
        }

        val filteredTransactionsToDelete = _transactionsList.value!!.filter { it.category_id == category.category_id }

        val filteredNotificationsToDelete = _notificationList.value!!.filter { it.category_id == category.category_id }

        filteredTransactionsToDelete.forEach {
            createOrUpdateTransaction(
                MoneyMovement(
                    it.movement_id,
                    it.account_id,
                    if (category.isIncome)
                        otherCategoryIncome.category_id
                    else
                        otherCategoryExpense.category_id,
                    null,
                    it.amount,
                    otherCategoryIncome.name,
                    it.subCategory,
                    it.comment,
                    otherCategoryIncome.icon,
                    it.isIncome,
                    it.date,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false,
                    )
            )
        }

        filteredNotificationsToDelete.forEach {
            createOrUpdateNotification(
                NotificationModel(
                    notification_id = it.notification_id,
                    category_id = if (category.isIncome)
                        otherCategoryIncome.category_id
                    else
                        otherCategoryExpense.category_id,
                    subcategory_id = null,
                    account_id = it.account_id,
                    name = it.name,
                    customNotificationText = it.customNotificationText,
                    comment = it.comment,
                    category = it.category,
                    isIncome = it.isIncome,
                    frequency = it.frequency,
                    remainingTimes = it.remainingTimes,
                    isFiniteRepeating = it.isFiniteRepeating,
                    nextDateToShow = it.nextDateToShow,
                    amount = it.amount,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                )
            )
        }

        val categoriesToReorder = _categoryList.value?.filter {
            it.order > category.order
                    && it.parentCategoryId == null
                    && it.name != "Transferencias_Especial_Plus20"
        }

        categoriesToReorder?.forEach {
            createOrUpdateCategory(
                Category(
                    it.category_id,
                    it.parentCategoryId,
                    it.name,
                    it.icon,
                    it.isIncome,
                    it.expenseLimit,
                    it.color,
                    it.order - 1,
                    it.toDelete,
                    it.timeStamp,
                    it.isSent
                )
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.deleteCategory(category)
        }
    }

    fun deleteSubcategory(category: Category, parentCategory: Category) {

        val filteredTransactionsToDelete = _transactionsList.value!!.filter { it.subCategory_id == category.category_id }

        val filteredNotificationsToDelete = _notificationList.value!!.filter { it.subcategory_id == category.category_id }

        filteredTransactionsToDelete.forEach {
            createOrUpdateTransaction(
                MoneyMovement(
                    it.movement_id,
                    it.account_id,
                    parentCategory.category_id,
                    null,
                    it.amount,
                    parentCategory.name,
                    it.subCategory,
                    it.comment,
                    parentCategory.icon,
                    it.isIncome,
                    it.date,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false,
                )
            )
        }

        filteredNotificationsToDelete.forEach {
            createOrUpdateNotification(
                NotificationModel(
                    notification_id = it.notification_id,
                    category_id = parentCategory.category_id,
                    subcategory_id = null,
                    account_id = it.account_id,
                    name = it.name,
                    customNotificationText = it.customNotificationText,
                    comment = it.comment,
                    category = it.category,
                    isIncome = it.isIncome,
                    frequency = it.frequency,
                    remainingTimes = it.remainingTimes,
                    isFiniteRepeating = it.isFiniteRepeating,
                    nextDateToShow = it.nextDateToShow,
                    amount = it.amount,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                )
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.deleteCategory(category)
        }
    }


    fun createOrUpdateNotification(notificationModel: NotificationModel) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationModelRepository.createOrUpdateNotification(notificationModel)
        }
    }

    fun deleteNotification(notificationModel: NotificationModel) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationModelRepository.deleteNotification(notificationModel)
        }
    }

    fun getAllNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationModelRepository.getNotifications().collect {
                _notificationList.postValue(it)
            }
        }
    }

    fun deleteAllData(context: Context, onComplete: () -> Unit){
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.deleteAllAccounts()
            moneyMovementRepository.deleteAllTransactions()
            categoryRepository.deleteAllCategories()
            notificationModelRepository.deleteAllNotifications()
            accountTransferRepository.deleteAllAccountTransfers()
        }.invokeOnCompletion {
            BudgetPlannerDb.prePopulateCategories(context = context)
            viewModelScope.launch(Dispatchers.Main) {
                onComplete()
                _isLoading.value = false
            }
        }
    }
}