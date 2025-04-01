package com.bytesdrawer.budgetplanner.common.utils

import android.content.Context
import androidx.compose.runtime.MutableState
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.navigation.NavigationScreens
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.WeekFields

fun getNameFromFrequencyNumber(frequency: Int, context: Context): String {
    return when (frequency) {
        0 -> context.getString(R.string.one_time_string)
        1 -> context.getString(R.string.daily_string)
        2 -> context.getString(R.string.weekly_string)
        3 -> context.getString(R.string.monthly_string)
        4 -> context.getString(R.string.yearly_string)
        else -> context.getString(R.string.one_time_string)
    }
}

fun getIncrementDateFromFrequencyNumber(frequency: Int, date: LocalDateTime): LocalDateTime? {
    return when (frequency) {
        1 -> date.plusDays(1)
        2 -> date.plusWeeks(1)
        3 -> date.plusMonths(1)
        4 -> date.plusYears(1)
        else -> date
    }
}

fun getDecrementDateFromFrequencyNumber(frequency: Int, date: LocalDateTime): LocalDateTime? {
    return when (frequency) {
        1 -> date.minusDays(1)
        2 -> date.minusWeeks(1)
        3 -> date.minusMonths(1)
        4 -> date.minusYears(1)
        else -> date.minusYears(1)
    }
}

fun transformDateFromNotification(date: String, frequency: Int): String {
    val formattedDate = dateStringToRegularFormat(date)
    return when (frequency) {
        0 -> formattedDate.toString()
        1 -> formattedDate!!.minusDays(1).toString()
        2 -> formattedDate!!.minusMonths(1).toString()
        3 -> formattedDate!!.minusWeeks(1).toString()
        4 -> formattedDate!!.minusYears(1).toString()
        else -> formattedDate.toString()
    }
}

fun LocalDateTime.toMillis(): Long {
    return this.atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

enum class TransactionDateCreation {
    TODAY,
    YESTERDAY,
    PERSO
}

fun intToCalendarName(number: Int, periodOfTime: PeriodOfTime, context: Context): String {
    return when(periodOfTime) {
        PeriodOfTime.DAY -> {
            if (number < 10) "0$number" else number.toString()
        }
        PeriodOfTime.WEEK -> {
            getNameOfTheWeek(number, context).substring(0,3)
        }
        PeriodOfTime.MONTH -> {
            if (number < 10) "0$number" else number.toString()
        }
        PeriodOfTime.YEAR -> {
            getNameOfTheMonth(number, context).substring(0,3)
        }
        else -> { "" }
    }
}

fun List<Double>.toPercent(highestValue: BigDecimal): List<Double> {
    return this.map { item ->
        item * 100 / highestValue.toDouble()
    }
}

fun bigDecimalParsed(amount: BigDecimal, df: DecimalFormat): String {
    return when {
        amount > BigDecimal(9999999999) || amount < BigDecimal(-9999999999) -> { "${df.format(amount.divide(BigDecimal(1000000000)))}B" }
        amount > BigDecimal(9999999) || amount < BigDecimal(-9999999)  -> { "${df.format(amount.divide(BigDecimal(1000000)))}M" }
        amount > BigDecimal(9999) || amount < BigDecimal(-9999) -> { "${df.format(amount.divide(BigDecimal(1000)))}k" }
        else -> { df.format(amount) }
    }
}

fun compareSelectedDateWithTransactionDate(
    timeLapseSelected: MutableState<PeriodOfTime?>,
    dateTime: MutableState<LocalDateTime>,
    moneyMovement: MoneyMovement,
    stringDateTime: MutableState<String>
): Boolean {
    return when (timeLapseSelected.value) {
        PeriodOfTime.DAY -> dateStringToRegularFormat(moneyMovement.date)?.dayOfMonth == dateTime.value.dayOfMonth
                && dateStringToRegularFormat(moneyMovement.date)?.monthValue == dateTime.value.monthValue
                && dateStringToRegularFormat(moneyMovement.date)?.year == dateTime.value.year
        PeriodOfTime.WEEK -> dateStringToRegularFormat(moneyMovement.date)?.get(
            WeekFields.of(
                DayOfWeek.MONDAY, 6).weekOfYear()) == dateTime.value.get(WeekFields.of(DayOfWeek.MONDAY, 6).weekOfYear())
                && dateStringToRegularFormat(moneyMovement.date)?.year == dateTime.value.year
        PeriodOfTime.HALF_MONTH -> ((dateStringToRegularFormat(moneyMovement.date)?.dayOfMonth!! < 15 && dateTime.value.dayOfMonth < 15)
                || (dateStringToRegularFormat(moneyMovement.date)?.dayOfMonth!! >= 15 && dateTime.value.dayOfMonth >= 15))
                && dateStringToRegularFormat(moneyMovement.date)?.monthValue == dateTime.value.monthValue
                && dateStringToRegularFormat(moneyMovement.date)?.year == dateTime.value.year
        PeriodOfTime.MONTH -> dateStringToRegularFormat(moneyMovement.date)?.monthValue == dateTime.value.monthValue
                && dateStringToRegularFormat(moneyMovement.date)?.year == dateTime.value.year
        PeriodOfTime.YEAR -> dateStringToRegularFormat(moneyMovement.date)?.year == dateTime.value.year
        PeriodOfTime.PERSO -> {
            val startDate = dateStringToRegularFormat(stringDateTime.value.substring(0,16))!!
                .atZone(ZoneId.of("GMT"))
                .toInstant().toEpochMilli()

            val endDate = dateStringToRegularFormat(stringDateTime.value.substring(17,33))!!
                .atZone(ZoneId.of("GMT"))
                .toInstant().toEpochMilli()

            val localDateTransaction = dateStringToRegularFormat(moneyMovement.date)
            val transactionDate = localDateTransaction!!
                .minusHours(if (localDateTransaction.hour != 0) localDateTransaction.hour.toLong() else 0)
                .minusMinutes(if (localDateTransaction.minute != 0) localDateTransaction.minute.toLong() else 0)
                .minusSeconds(if (localDateTransaction.second != 0) localDateTransaction.second.toLong() else 0)
                .minusNanos(if (localDateTransaction.nano != 0) localDateTransaction.nano.toLong() else 0)
                .atZone(ZoneId.of("GMT"))
                .toInstant().toEpochMilli()

            return transactionDate in startDate..endDate
        }
        else -> dateStringToRegularFormat(moneyMovement.date)?.dayOfMonth == dateTime.value.dayOfMonth
    }
}

fun compareSelectedDateWithTransactionDateForAccountTransfers(
    timeLapseSelected: MutableState<PeriodOfTime?>,
    dateTime: MutableState<LocalDateTime>,
    accountTransfer: AccountTransfer,
    stringDateTime: MutableState<String>
): Boolean {
    return when (timeLapseSelected.value) {
        PeriodOfTime.DAY -> dateStringToRegularFormat(accountTransfer.date)?.dayOfMonth == dateTime.value.dayOfMonth
                && dateStringToRegularFormat(accountTransfer.date)?.monthValue == dateTime.value.monthValue
        PeriodOfTime.WEEK -> dateStringToRegularFormat(accountTransfer.date)?.get(
            WeekFields.of(
                DayOfWeek.MONDAY, 6).weekOfYear()) == dateTime.value.get(WeekFields.of(DayOfWeek.MONDAY, 6).weekOfYear())
                && dateStringToRegularFormat(accountTransfer.date)?.year == dateTime.value.year
        PeriodOfTime.HALF_MONTH -> ((dateStringToRegularFormat(accountTransfer.date)?.dayOfMonth!! < 15 && dateTime.value.dayOfMonth < 15)
                || (dateStringToRegularFormat(accountTransfer.date)?.dayOfMonth!! >= 15 && dateTime.value.dayOfMonth >= 15))
                && dateStringToRegularFormat(accountTransfer.date)?.monthValue == dateTime.value.monthValue
                && dateStringToRegularFormat(accountTransfer.date)?.year == dateTime.value.year
        PeriodOfTime.MONTH -> dateStringToRegularFormat(accountTransfer.date)?.monthValue == dateTime.value.monthValue
                && dateStringToRegularFormat(accountTransfer.date)?.year == dateTime.value.year
        PeriodOfTime.YEAR -> dateStringToRegularFormat(accountTransfer.date)?.year == dateTime.value.year
                && dateStringToRegularFormat(accountTransfer.date)?.year == dateTime.value.year
        PeriodOfTime.PERSO -> {
            val startDate = dateStringToRegularFormat(stringDateTime.value.substring(0,16))!!
                .atZone(ZoneId.of("GMT"))
                .toInstant().toEpochMilli()

            val endDate = dateStringToRegularFormat(stringDateTime.value.substring(17,33))!!
                .atZone(ZoneId.of("GMT"))
                .toInstant().toEpochMilli()

            val localDateTransaction = dateStringToRegularFormat(accountTransfer.date)
            val transactionDate = localDateTransaction!!
                .minusHours(if (localDateTransaction.hour != 0) localDateTransaction.hour.toLong() else 0)
                .minusMinutes(if (localDateTransaction.minute != 0) localDateTransaction.minute.toLong() else 0)
                .minusSeconds(if (localDateTransaction.second != 0) localDateTransaction.second.toLong() else 0)
                .minusNanos(if (localDateTransaction.nano != 0) localDateTransaction.nano.toLong() else 0)
                .atZone(ZoneId.of("GMT"))
                .toInstant().toEpochMilli()

            return transactionDate in startDate..endDate
        }
        else -> dateStringToRegularFormat(accountTransfer.date)?.dayOfMonth == dateTime.value.dayOfMonth
    }
}

fun dateStringToRegularFormat(date: String): LocalDateTime? {
    return LocalDateTime.of(
        date.substring(0, 4).toInt(),
        date.substring(5,7).toInt(),
        date.substring(8,10).toInt(),
        date.substring(11,13).toInt(),
        date.substring(14,16).toInt()
    )
}

fun getNameOfTheMonth(day: Int?, context: Context): String {
    return when (day) {
        1 -> context.getString(R.string.january_string)
        2 -> context.getString(R.string.february_string)
        3 -> context.getString(R.string.march_string)
        4 -> context.getString(R.string.april_string)
        5 -> context.getString(R.string.may_string)
        6 -> context.getString(R.string.june_string)
        7 -> context.getString(R.string.july_string)
        8 -> context.getString(R.string.august_string)
        9 -> context.getString(R.string.september_string)
        10 -> context.getString(R.string.october_string)
        11 -> context.getString(R.string.november_string)
        12 -> context.getString(R.string.december_string)
        else -> context.getString(R.string.invalid_day_string)
    }
}

fun getNameOfTheWeek(day: Int?, context: Context): String {
    return when (day) {
        1 -> context.getString(R.string.monday_string)
        2 -> context.getString(R.string.tuesday_string)
        3 -> context.getString(R.string.wednesday_string)
        4 -> context.getString(R.string.thursday_string)
        5 -> context.getString(R.string.friday_string)
        6 -> context.getString(R.string.saturday_string)
        7 -> context.getString(R.string.sunday_string)
        else -> context.getString(R.string.invalid_day_string)
    }
}

fun getNameNavigationScreen(name: String?, context: Context): NavigationScreens {
    return when (name) {
        context.getString(NavigationScreens.HOME.screen) -> {
            NavigationScreens.HOME
        }
        context.getString(NavigationScreens.ACCOUNT.screen) -> {
            NavigationScreens.ACCOUNT
        }
        context.getString(NavigationScreens.ANALISIS.screen) -> {
            NavigationScreens.ANALISIS
        }
        context.getString(NavigationScreens.SETTINGS.screen) -> {
            NavigationScreens.SETTINGS
        }
        context.getString(NavigationScreens.PAGOS_RECURRENTES.screen) -> {
            NavigationScreens.PAGOS_RECURRENTES
        }
        context.getString(NavigationScreens.TRANSACCION.screen) -> {
            NavigationScreens.TRANSACCION
        }
        context.getString(NavigationScreens.EDIT_ACCOUNT.screen) -> {
            NavigationScreens.EDIT_ACCOUNT
        }
        context.getString(NavigationScreens.EDIT_CATEGORY.screen) -> {
            NavigationScreens.EDIT_CATEGORY
        }
        context.getString(NavigationScreens.ADD_ACCOUNT.screen) -> {
            NavigationScreens.ADD_ACCOUNT
        }
        context.getString(NavigationScreens.TRANSACCIONS_BY_CATEGORY.screen) -> {
            NavigationScreens.TRANSACCIONS_BY_CATEGORY
        }
        context.getString(NavigationScreens.EDIT_TRANSACTION.screen) -> {
            NavigationScreens.EDIT_TRANSACTION
        }
        context.getString(NavigationScreens.NUEVO_RECURRENTE.screen) -> {
            NavigationScreens.NUEVO_RECURRENTE
        }
        context.getString(NavigationScreens.ADD_SUBCATEGORY.screen) -> {
            NavigationScreens.ADD_SUBCATEGORY
        }
        context.getString(NavigationScreens.EDIT_SUBCATEGORY.screen) -> {
            NavigationScreens.EDIT_SUBCATEGORY
        }
        else -> {
            NavigationScreens.HOME
        }
    }
}

fun getCalculatorButtons(): List<String> {
    return listOf(
        "C", "+/-", "%", "÷", "7", "8", "9", "x", "4", "5", "6", "-", "1", "2", "3", "+", ".", "0", "Del", "="
    )
}

fun getDivisaFromString(divisaName: String?): Divisa {
    return Divisa.valueOf(divisaName ?: Divisa.USD.name)
}

enum class Divisa(val completeName: String, val completeNameEnglish: String) {
    USD("Dólar estadounidense", "United States Dollar"),
    ARS("Peso argentino", "Argentine Peso"),
    BOB("Boliviano", "Bolivian Boliviano"),
    BRL("Real brasileño", "Brazilian Real"),
    CLP("Peso chileno", "Chilean Peso"),
    COP("Peso colombiano", "Colombian Peso"),
    CRC("Colón costarricense", "Costa Rican Colón"),
    CUP("Peso cubano", "Cuban Peso"),
    GTQ("Quetzal guatemalteco", "Guatemalan Quetzal"),
    HNL("Lempira hondureño", "Honduran Lempira"),
    MXN("Peso mexicano", "Mexican Peso"),
    NIO("Córdoba nicaragüense", "Nicaraguan Córdoba"),
    PAB("Balboa panameño", "Panamanian Balboa"),
    PYG("Guaraní paraguayo", "Paraguayan Guaraní"),
    PEN("Sol peruano", "Peruvian Sol"),
    DOP("Peso dominicano", "Dominican Peso"),
    UYU("Peso uruguayo", "Uruguayan Peso"),
    VES("Bolívar venezolano", "Venezuelan Bolívar"),
    EUR("Euro", "Euro"),
    GBP("Libra esterlina", "British Pound Sterling"),
    JPY("Yen japonés", "Japanese Yen"),
    CNY("Yuan chino", "Chinese Yuan"),
    AUD("Dólar australiano", "Australian Dollar"),
    CAD("Dólar canadiense", "Canadian Dollar"),
    CHF("Franco suizo", "Swiss Franc"),
    SEK("Corona sueca", "Swedish Krona"),
    NZD("Dólar neozelandés", "New Zealand Dollar"),
    KRW("Won surcoreano", "South Korean Won"),
    SGD("Dólar de Singapur", "Singapore Dollar"),
    INR("Rupia india", "Indian Rupee"),
    RUB("Rublo ruso", "Russian Ruble"),
    TRY("Lira turca", "Turkish Lira"),
    ZAR("Rand sudafricano", "South African Rand"),
    SAR("Riyal saudí", "Saudi Riyal"),
    AED("Dirham de los Emiratos Árabes Unidos", "United Arab Emirates Dirham"),
    HKD("Dólar de Hong Kong", "Hong Kong Dollar"),
    BGN("Lev búlgaro", "Bulgarian Lev"),
    DKK("Corona danesa", "Danish Krone"),
    HRK("Kuna croata", "Croatian Kuna"),
    CZK("Corona checa", "Czech Koruna"),
    HUF("Forint húngaro", "Hungarian Forint"),
    IDR("Rupia indonesia", "Indonesian Rupiah"),
    ILS("Shekel israelí", "Israeli Shekel"),
    KWD("Dinar kuwaití", "Kuwaiti Dinar"),
    MYR("Ringgit malasio", "Malaysian Ringgit"),
    MXV("Unidad de Inversión", "Mexican Investment Unit"),
    NOK("Corona noruega", "Norwegian Krone"),
    PKR("Rupia pakistaní", "Pakistani Rupee"),
    PHP("Peso filipino", "Philippine Peso"),
    PLN("Zloty polaco", "Polish Złoty"),
    QAR("Rial qatarí", "Qatari Riyal"),
    RON("Leu rumano", "Romanian Leu"),
    RSD("Dinar serbio", "Serbian Dinar"),
    THB("Baht tailandés", "Thai Baht"),
    TWD("Dólar taiwanés", "Taiwanese Dollar"),
    UAH("Grivna ucraniana", "Ukrainian Hryvnia"),
    VND("Dong vietnamita", "Vietnamese Dong"),
    XCD("Dólar del Caribe Oriental", "East Caribbean Dollar"),
    ZMW("Kwacha zambiano", "Zambian Kwacha")
}

fun createIconList(): List<Int> {
    return listOf(
        R.drawable.analytics,
        R.drawable.accessibility,
        R.drawable.airplane,
        R.drawable.anchor,
        R.drawable.balance,
        R.drawable.bed,
        R.drawable.blender,
        R.drawable.books,
        R.drawable.bread,
        R.drawable.brush,
        R.drawable.business,
        R.drawable.category,
        R.drawable.clip,
        R.drawable.cold,
        R.drawable.delete,
        R.drawable.family,
        R.drawable.food,
        R.drawable.free_time,
        R.drawable.health,
        R.drawable.house,
        R.drawable.interest,
        R.drawable.money,
        R.drawable.money_off,
        R.drawable.music,
        R.drawable.research,
        R.drawable.search,
        R.drawable.settings,
        R.drawable.study,
        R.drawable.transport,
        R.drawable.vacations,
        R.drawable.workout,
        R.drawable.car,
        R.drawable.camera,
        R.drawable.mail,
        R.drawable.gas,
        R.drawable.fingerprint,
        R.drawable.recycler,
        R.drawable.baby,
        R.drawable.bedtime,
        R.drawable.pet,
        R.drawable.diamond,
        R.drawable.twilight,
        R.drawable.repeat,
        R.drawable.cloud_done,
        R.drawable.no_ads,
        R.drawable.stars,
        R.drawable.panorama,
        R.drawable.castle,
        R.drawable.air,
        R.drawable.attraction,
        R.drawable.barcode_reader,
        R.drawable.bathtub,
        R.drawable.bussiness,
        R.drawable.coffe,
        R.drawable.pc,
        R.drawable.bunny,
        R.drawable.cyclone,
        R.drawable.thermostat,
        R.drawable.electric_bolt,
        R.drawable.award,
        R.drawable.fireplace
    )
}