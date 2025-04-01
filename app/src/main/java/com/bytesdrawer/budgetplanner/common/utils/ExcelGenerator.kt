package com.bytesdrawer.budgetplanner.common.utils

import android.net.Uri
import android.util.Log
import com.bytesdrawer.budgetplanner.MainActivity
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.math.BigDecimal

object ExcelGenerator {

    var transactions: List<MoneyMovement>? = emptyList()
    var categories: List<Category>? = emptyList()
    var periodOfTime: PeriodOfTime? = null
    var textFromDateSelection: String = ""
    var account: Account? = null
    var fileName: String = ""

    fun generate(data: Uri, activity: MainActivity) {
        if (!transactions.isNullOrEmpty() && periodOfTime != null && textFromDateSelection.isNotEmpty()) {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet(
                activity.getString(
                    R.string.summary_of_excel,
                    periodOfTime!!.name
                ))

            val incomeTransactions = transactions!!.filter { it.isIncome }
            val expenseTransactions = transactions!!.filter { !it.isIncome }

            var rows = 1
            val segmentRow = sheet.createRow(rows)
            createCell(segmentRow, 1,
                activity.getString(
                    R.string.account_date_excel,
                    if (account == null) "Total" else account!!.name,
                    textFromDateSelection
                ))
            sheet.addMergedRegion(CellRangeAddress(rows, rows, 1,4))
            rows += 2

            // Ingresos
            val incomeTitleRow = sheet.createRow(rows)
            createCell(incomeTitleRow, 1, activity.getString(R.string.income_lowecase))
            sheet.addMergedRegion(CellRangeAddress(rows, rows, 1,4))
            rows++

            val parametersIncomeRow = sheet.createRow(rows)
            createCell(parametersIncomeRow, 1, activity.getString(R.string.date_excel))
            createCell(parametersIncomeRow, 2, activity.getString(R.string.category_excel))
            createCell(parametersIncomeRow, 3, activity.getString(R.string.subcategory_excel))
            createCell(parametersIncomeRow, 4, activity.getString(R.string.amount_string))
            createCell(parametersIncomeRow, 5, activity.getString(R.string.comment_excel))
            rows++
            val firstIncomeRow = rows
            incomeTransactions.forEach { transaction ->
                val newRow = sheet.createRow(rows)

                createCell(newRow, 1, dateStringToRegularFormat(transaction.date).toString())
                createCell(newRow, 2, transaction.category)
                createCell(newRow, 3, categories?.firstOrNull { transaction.subCategory_id == it.category_id && it.parentCategoryId != null }?.name)
                createCell(newRow, 4, null, if (transaction.isIncome) transaction.amount.toDouble() else (transaction.amount - (transaction.amount * BigDecimal(2))).toDouble())
                createCell(newRow, 5, transaction.comment)
                rows++
            }
            val formulaRowIncome = sheet.createRow(rows)
            val incomeValueRow = rows + 1
            createCell(formulaRowIncome, 3, "Total")
            createFormulaCell(formulaRowIncome, 4, "SUM(E$firstIncomeRow:E$rows)")
            rows += 2

            // Gastos
            val expenseTitleRow = sheet.createRow(rows)
            createCell(expenseTitleRow, 1, activity.getString(R.string.expenses_lowecase))
            sheet.addMergedRegion(CellRangeAddress(rows, rows, 1,4))
            rows++

            val parametersExpenseRow = sheet.createRow(rows)
            createCell(parametersExpenseRow, 1, activity.getString(R.string.date_excel))
            createCell(parametersExpenseRow, 2, activity.getString(R.string.category_excel))
            createCell(parametersExpenseRow, 3, activity.getString(R.string.subcategory_excel))
            createCell(parametersExpenseRow, 4, activity.getString(R.string.amount_string))
            createCell(parametersExpenseRow, 5, activity.getString(R.string.comment_excel))
            rows++
            val firstExpenseRow = rows
            expenseTransactions.forEach { transaction ->
                val newRow = sheet.createRow(rows)

                createCell(newRow, 1, dateStringToRegularFormat(transaction.date).toString())
                createCell(newRow, 2, transaction.category)
                createCell(newRow, 3, categories?.firstOrNull { transaction.subCategory_id == it.category_id && it.parentCategoryId != null }?.name)
                createCell(newRow, 4, null, if (transaction.isIncome) transaction.amount.toDouble() else (transaction.amount - (transaction.amount * BigDecimal(2))).toDouble())
                createCell(newRow, 5, transaction.comment)
                rows++
            }
            val formulaRowExpense = sheet.createRow(rows)
            val expenseValueRow = rows + 1
            createCell(formulaRowExpense, 3, "Total")
            createFormulaCell(formulaRowExpense, 4, "SUM(E$firstExpenseRow:E$rows)")

            rows += 2
            val totalBalanceRow = sheet.createRow(rows)
            createCell(totalBalanceRow, 3, activity.getString(R.string.final_balance_excel))
            createFormulaCell(totalBalanceRow, 4, "SUM(E$expenseValueRow,E$incomeValueRow)")

            writeFile(activity, workbook, data)
        }
    }

    private fun writeFile(activity: MainActivity, workbook: Workbook, data: Uri) {
        Log.d("File Path:", data.host.toString())
        val fos = activity.contentResolver.openOutputStream(data)
        workbook.write(fos)
        fos?.close()
    }

    private fun createCell(sheetRow: Row, columnIndex: Int, cellValue: String?, cellNumber: Double = 0.0) {
        val ourCell = sheetRow.createCell(columnIndex)
        if (cellNumber != 0.0) {
            ourCell?.setCellValue(cellNumber)
        } else {
            ourCell?.setCellValue(cellValue)
        }
    }

    private fun createFormulaCell(sheetRow: Row, columnIndex: Int,  formula: String?) {
        val ourCell = sheetRow.createCell(columnIndex)
        ourCell.cellFormula = formula
    }

}