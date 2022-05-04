package com.example.ticktapp.util

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Admin on 09-Apr-19.
 */
object DateUtils {
    const val DATE_FORMATE_1 = "dd/MM/yyyy"
    // 25/12/2016

    const val DATE_FORMATE_2 = "E, MMM dd,yyyy"
    // Sat, Apr 09,2016

    const val DATE_FORMATE_3 = "HH:mm:ss"
    // 24 hour time format

    const val DATE_FORMATE_4 = "yyyy-MM-dd HH:mm:ss"
    // 24 hour time format

    const val DATE_FORMATE_5 = "EEEE dd.MM.yyyy"
    // Sunday 25.12.2016

    const val DATE_FORMATE_6 = "MM/dd/yyyy"
    // 10/13/1992

    const val DATE_FORMATE_7 = "hh:mm a"
    //06:14 PM

    const val DATE_FORMATE_8 = "yyyy-MM-dd"
    // 2016-12-25

    const val DATE_FORMATE_9 = "MMM dd"
    // June 15

    const val DATE_FORMATE_10 = "dd"
    // 12

    const val DATE_FORMATE_11 = "MMM dd, yyyy"
    // Sep 13, 2018

    const val DATE_FORMATE_12 = "E, MMM dd,yyyy hh:mm a"
    // Sat, Apr 09,2016

    const val DATE_FORMATE_13 = "MMMM yyyy"
    // April 2018

    const val DATE_FORMATE_14 = "dd MMM"
    // 15 Jun

    const val DATE_FORMATE_15 = "dd MMM, YY"
    // 15 Jun, 21

    const val DATE_FORMATE_16 = "dd MMM"
    // 15 Jun

    const val DATE_FORMATE_17 = "dd MMM YYYY"
    // 15 Jun

    const val DATE_FORMAT_18 = "yyyy-MM-dd'T'HH:mm:ss"

    const val DATE_FORMATE_19 = "dd  MMM"

    const val TIME_FORMAT_1 = "hh:mm a"
    // 11:30 PM

    const val TIME_FORMAT_2 = "HH:mm:ss" // 24 hour time format

    private const val TAG = "DateUtils"

    /**
     * By Nitesh<br></br>
     *
     *
     * e.g.
     * <br></br>Give time like 1:3 0
     * <br></br>Return time 01:03 AM in String format
     */
    fun timeFormat_HH_MM_PM(hours: Int, minutes: Int, amPm: Int): String {
        // Format Hours, Minutes and AMPM
        var strHour = if (hours >= 0 && hours < 10) "0$hours" else "" + hours
        val strMinute = if (minutes >= 0 && minutes < 10) "0$minutes" else "" + minutes
        val strAmPm = if (amPm == 0) "AM" else "PM"

        // If time is like this : 00:00 PM
        // Then convert it to   : 12:00 PM
        strHour = if (strHour == "00") "12" else "" + strHour
        return "$strHour:$strMinute $strAmPm" // e.g. 03:09 PM
    }

    /**
     * By Nitesh<br></br>
     *
     *
     * <br></br>Give 2 pattern
     * <br></br>Return String of second pattern
     *
     *
     * e.g.
     * <br></br>From : 2016-05-31 15:30:00
     * <br></br>To   : 31-05-2016 03:30 PM
     */
    fun changeDateFormat(fromPattern: String?, toPattern: String?, date: String?): String? {
        try {
            val d = SimpleDateFormat(fromPattern, Locale.US).parse(date)
            return SimpleDateFormat(toPattern, Locale.US).format(d)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    /**
     * By Nitesh<br></br>
     *
     *
     * <br></br>Give 1 pattern date
     * e.g.
     * <br></br>From : 2016-05-31 15:30:00
     */
    fun getCalendarFromDate(fromPattern: String?, date: String?): Calendar? {
        try {
            val d = SimpleDateFormat(fromPattern, Locale.US).parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = d
            return calendar
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    /**
     * By Nitesh<br></br>
     *
     *
     * e.g.
     * <br></br>Give date value in integer
     * <br></br>Return date in String format
     * <br></br>e.g. Sat, Apr 09,2016
     */
    fun dateFormat(day: Int, month: Int, year: Int): String {
        var strMonths = ""

        // Set month
        strMonths =
            if (month >= 0 && month < 9) "0" + (month + 1) else (month + 1).toString() + "" //
        val selected_date = "$day/$strMonths/$year"
        val sdf = SimpleDateFormat(DATE_FORMATE_1, Locale.US)
        var date: Date? = null
        try {
            date = sdf.parse(selected_date)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        val formatter = SimpleDateFormat(DATE_FORMATE_2, Locale.US)
        return formatter.format(date)
    }

    /**
     * By Nitesh<br></br>
     *
     *
     * e.g.
     * <br></br>Give time in 24 hour. For e.g. 14:30:00
     * <br></br>Return time in String format for e.g. 02:30:00
     */
    fun convertFrom24To12Hours(_24HourTime: String?): String {
        var _12HourTime = ""
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(DATE_FORMATE_3, Locale.US) // 24 hour time format
        try {
            val d = dateFormat.parse(_24HourTime) // 14:30:00
            calendar.time = d
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val hours = calendar[Calendar.HOUR] // 12 hour time
        val minutes = calendar[Calendar.MINUTE]
        val am_pm = calendar[Calendar.AM_PM]
        _12HourTime = timeFormat_HH_MM_PM(hours, minutes, am_pm) // 02:30 PM
        return _12HourTime
    }

    /**
     * By Nitesh<br></br>
     *
     *
     * Calculate time between two dates
     *
     *
     * For e.g.
     *
     *
     * Return 1 day ago, 1 minute ago, 2 years ago
     */
    fun calculateTimeBetweenTwoDates(
        startDate: String?,
        endDate: String?,
        shortDescription: Boolean
    ): String {
        /**
         * **** Easy Use ****
         * String currDate = new SimpleDateFormat(DateUtils.DATE_FORMATE_4).format(Calendar.getInstance().getTime());
         * String finalTime = DateUtils.calculateTimeBetweenTwoDates(list.get(position).getDatetime(), currDate);
         */
        var finalTimeString = ""
        var ago = "Ago"
        var returnMin = "Minute"
        var returnHour = "Hour"
        var returnDay = "Day"
        var returnMonth = "Month"
        var returnYear = "Year"
        if (shortDescription) {
            // Need short description
            ago = ""
            returnMin = "Min"
            returnHour = "Hour"
            returnDay = "Day"
            returnMonth = "Month"
            returnYear = "Year"
        }
        val myFormat = SimpleDateFormat(DATE_FORMATE_4, Locale.US) // 24 hours format
        try {
            val date1 = myFormat.parse(startDate)
            val date2 = myFormat.parse(endDate)
            val diff = date2.time - date1.time
            val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            val months = days / 30
            val year = months / 12
            finalTimeString = if (seconds < 60) {
                "Now"
            } else if (minutes < 60) {
                if (minutes == 1L) {
                    "$minutes $returnMin $ago"
                } else {
                    minutes.toString() + " " + returnMin + "s " + ago
                }
            } else if (hours < 24) {
                if (hours == 1L) {
                    "$hours $returnHour $ago"
                } else {
                    hours.toString() + " " + returnHour + "s " + ago
                }
            } else if (days < 30) {
                if (days == 1L) {
                    "$days $returnDay $ago"
                } else {
                    days.toString() + " " + returnDay + "s " + ago
                }
            } else if (months < 12) {
                if (months == 1L) {
                    "$months $returnMonth $ago"
                } else {
                    months.toString() + " " + returnMonth + "s " + ago
                }
            } else {
                if (year == 1L) {
                    "$year $returnYear $ago"
                } else {
                    year.toString() + " " + returnYear + "s " + ago
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return finalTimeString
    }

    fun printDifference(startDate: String, endDate: String): String {
        var finalTimeString = ""
        val returnDay = "day"
        val returnMonth = "month"

        //milliseconds
        val myFormat = SimpleDateFormat(DATE_FORMATE_8, Locale.US) // 24 hours format
        try {
            val date1 = myFormat.parse(startDate)
            val date2 = myFormat.parse(endDate)
            var different = date2.time - date1.time
            println("startDate : $startDate")
            println("endDate : $endDate")
            println("different : $different")
            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24
            val monthsInMilli = daysInMilli * 30
            val elapsedmonths = different / monthsInMilli
            different = different % monthsInMilli
            val elapsedDays = different / daysInMilli
            different = different % daysInMilli
            val elapsedHours = different / hoursInMilli
            different = different % hoursInMilli
            val elapsedMinutes = different / minutesInMilli
            different = different % minutesInMilli
            val elapsedSeconds = different / secondsInMilli
            System.out.printf(
                "%dmonths %d days, %d hours, %d minutes, %d seconds%n",
                elapsedmonths,
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds
            )
            finalTimeString = if (elapsedmonths >= 1) {
                if (elapsedmonths == 1L) {
                    "$elapsedmonths $returnMonth "
                } else {
                    elapsedmonths.toString() + " " + returnMonth + "s "
                }
            } else {
                /*elese of months*/
                if (elapsedDays >= 1) {
                    if (elapsedDays == 1L) {
                        "$elapsedDays $returnDay "
                    } else {
                        elapsedDays.toString() + " " + returnDay + "s "
                    }
                } else {
                    /*===== else of days ====*/
                    if (elapsedHours == 23L && elapsedMinutes > 30) {
                        "1 $returnDay "
                    } else {
                       "Today"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return finalTimeString
    }

    fun calculateTimeBetweenTwoDatesInPlusRoundOrder(
        startDate: String?,
        endDate: String?,
        shortDescription: Boolean
    ): String {
        /**
         * **** Easy Use ****
         * String currDate = new SimpleDateFormat(DateUtils.DATE_FORMATE_4).format(Calendar.getInstance().getTime());
         * String finalTime = DateUtils.calculateTimeBetweenTwoDates(list.get(position).getDatetime(), currDate);
         */
        var finalTimeString = ""
        var ago = "ago"
        var returnMin = "minute"
        var returnHour = "hour"
        var returnDay = "day"
        var returnMonth = "month"
        var returnYear = "year"
        if (shortDescription) {
            // Need short description
            ago = ""
            returnMin = "min"
            returnHour = "hour"
            returnDay = "day"
            returnMonth = "month"
            returnYear = "year"
        }
        val myFormat = SimpleDateFormat(DATE_FORMATE_4, Locale.US) // 24 hours format
        try {
            val date1 = myFormat.parse(startDate)
            val date2 = myFormat.parse(endDate)
            val diff = date2.time - date1.time
            val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            val months = days / 30
            val year = months / 12
            finalTimeString = if (seconds < 60) {
                "Now"
            } else if (minutes < 60) {
                if (minutes == 1L) {
                    "$minutes $returnMin $ago"
                } else {
                    minutes.toString() + " " + returnMin + "s " + ago
                }
            } else if (hours < 24) {
                if (hours == 1L) {
                    "$hours $returnHour $ago"
                } else {
                    hours.toString() + " " + returnHour + "s " + ago
                }
            } else if (days < 30) {
                if (days == 1L) {
                    "$days $returnDay $ago"
                } else {
                    days.toString() + " " + returnDay + "s " + ago
                }
            } else if (months < 12) {
                if (months == 1L) {
                    "$months $returnMonth $ago"
                } else {
                    months.toString() + " " + returnMonth + "s " + ago
                }
            } else {
                if (year == 1L) {
                    "$year $returnYear $ago"
                } else {
                    year.toString() + " " + returnYear + "s " + ago
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return finalTimeString
    }

    /**
     * By Nitesh<br></br>
     *
     *
     * e.g.
     * <br></br>Give date value in integer
     * <br></br>Return ArrayList of Week dates
     *
     * e.g.<br></br>
     * <t></t>day e.g. SUNDAY, MONDAY etc<br></br>
     * <t></t>date e.g. 02, 31, 25 etc<br></br>
     * <t></t>month e.g. 01, 12, 05 etc<br></br>
     * <t></t>year e.g. 2016, 2018 etc<br></br>
     * <t></t>fromToday Date is before today's date or after or equals<br></br>
     *
     *
     *
     * NOTE: This is not consider time for check date is before or after or equals
     */
    fun getWeekDates(date: Int, month: Int, year: Int): ArrayList<WeekDatesModel> {
        val todayCal = Calendar.getInstance()
        val weekCal = Calendar.getInstance()
        weekCal[Calendar.DAY_OF_MONTH] = date
        weekCal[Calendar.MONTH] = month
        weekCal[Calendar.YEAR] = year
        val weekNo = weekCal[Calendar.WEEK_OF_YEAR]
        weekCal[Calendar.WEEK_OF_YEAR] = weekNo
        weekCal.clear()
        weekCal[Calendar.WEEK_OF_YEAR] = weekNo
        weekCal[Calendar.YEAR] = year
        weekCal[Calendar.DAY_OF_WEEK] = weekCal.firstDayOfWeek
        val sdf = SimpleDateFormat(DATE_FORMATE_5, Locale.US)
        val weekList = ArrayList<WeekDatesModel>()
        for (i in 0..6) {
            var day1 = ""
            var date1 = ""
            var month1 = ""
            var year1 = ""
            var fromToday1 = ""

            // Check date is before, after or equals
            fromToday1 = if (weekCal.time.before(todayCal.time)) {
                if (todayCal[Calendar.DATE] == weekCal[Calendar.DATE] && todayCal[Calendar.MONTH] == weekCal[Calendar.MONTH] && todayCal[Calendar.YEAR] == weekCal[Calendar.YEAR]) {
                    "equal" // This is Today's Date
                } else {
                    "before" // This is Before today's Date
                }
            } else {
                "after" // This is After today's Date
            }
            day1 = weekCal[Calendar.DAY_OF_WEEK].toString()
            date1 =
                if (weekCal[Calendar.DATE] > 0 && weekCal[Calendar.DATE] < 10) "0" + weekCal[Calendar.DATE] else weekCal[Calendar.DATE].toString() + ""
            month1 =
                if (weekCal[Calendar.MONTH] >= 0 && weekCal[Calendar.MONTH] < 10) "0" + weekCal[Calendar.MONTH] else weekCal[Calendar.MONTH].toString() + ""
            year1 = weekCal[Calendar.YEAR].toString()

            //Logger.d("FULL DATE : --- " + sdf.format(weekCal.getTime()));
            weekList.add(WeekDatesModel(day1, date1, month1, year1, fromToday1))

            // Plus 1 date
            weekCal.add(Calendar.DAY_OF_WEEK, 1)
        }
        return weekList
    }

    /**
     * By Nitesh<br></br>
     *
     *
     * <br></br>Need pattern e.g. dd/MM/yyyy h:mm:ss
     * <br></br>If pattern is empty then it will return below format date
     * <br></br>E, MMM dd,yyyy hh:mm a
     *
     *
     *
     * @param pattern
     * @return Return String as per your given pattern
     */
    fun getCurrentDateTime(pattern: String?): String {
        return SimpleDateFormat(pattern, Locale.US).format(Date())
    }


    fun checkForCurrentYear(fromPattern:String,date:String?):Boolean
    {
if (date.isNullOrEmpty())
{
    return true
}
        val cal =Calendar.getInstance()
        val d = SimpleDateFormat(fromPattern, Locale.US).parse(date)
        cal.timeInMillis=d.time
        return Calendar.getInstance().get(Calendar.YEAR)==cal.get(Calendar.YEAR)
    }

    /**
     * By Nitesh<br></br>
     *
     *
     * <br></br>Need pattern e.g. dd/MM/yyyy h:mm:ss
     * <br></br>If pattern is empty then it will return below format date
     * <br></br>E, MMM dd,yyyy hh:mm a
     *
     *
     *
     * @param pattern
     * @return Return String as per your given pattern
     */
    fun getCurrentDateTime(date: Date?, pattern: String?): String {
        return SimpleDateFormat(pattern, Locale.US).format(date)
    }

    /**
     * Convert UTC time to Local time zone
     *
     * @param date
     * @return
     * @throws ParseException
     */
    fun convertUTCtoLocalTimeZone(date: String?, date_formate: String?): String {
        val simpleDateFormat = SimpleDateFormat(date_formate, Locale.US)
        //        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        var myDate: Date? = null
        try {
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            //            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            myDate = simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return SimpleDateFormat(date_formate, Locale.US).format(myDate) // Note: Use new DateFormat
    }

    fun convertLocalTimeZoneToUTC(inputPattern: String?, date: String?): String {
        val utcDateFormat = SimpleDateFormat(inputPattern, Locale.US)
        var localDate: Date? = null
        return try {
            localDate = SimpleDateFormat(
                inputPattern,
                Locale.US
            ).parse(date) // Local Date Format (By default)
            utcDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            utcDateFormat.format(localDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun getDateFromString(dateStr: String?, formatStr: String?): Date {
        var date: Date? = Date()
        val format = SimpleDateFormat(formatStr, Locale.US)
        format.isLenient = false
        try {
            date = format.parse(dateStr)
            println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date!!
    }

    /*
     *  convert string to date
     */
    /*
     * current date in string format
     */
    fun getCurrentDate(formatStr: String?): Date? {
        var return_date: Date? = null
        var datetime = ""
        val dateformat = SimpleDateFormat(formatStr, Locale.US)
        val date = Date()
        datetime = dateformat.format(date)
        println("Current Date Time : $datetime")
        return_date = getDateFromString(datetime, formatStr)
        return return_date
    }

    fun getDateFromTimeStamp(timeStamp: Long?, formatYouWant: String? = null): String? {
        var fromFormat = "dd/MM/yyyy"

        if (formatYouWant != null) {
            fromFormat = formatYouWant
        }
        if (timeStamp != null) {
            val dateInstance = Date(timeStamp)

            val formatter: DateFormat = SimpleDateFormat(fromFormat, Locale.getDefault())
            try {
                return formatter.format(dateInstance)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return ""
    }
    /*
     * compare date with current date
     */
    fun compareDateWithCurrent(date: String?): Int {
        var now: Date? = Date()
        var result = 0
        val sdf = SimpleDateFormat(DATE_FORMATE_8, Locale.US)
        now = getCurrentDate(DATE_FORMATE_8)
        var theOtherDate: Date? = null
        try {
            theOtherDate = sdf.parse(date)
            result = now!!.compareTo(theOtherDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        /* result will be an int < 0 if now Date is less than the theOtherDate Date, 0 if they are equal, and an int > 0 if this Date is greater.*/return result
    }

    /*
     * compare two dates
     * result will be an int < 0 if theFirstDate Date is less than the theSecondDate Date,
     * 0 if they are equal,
     * and an int > 0 if this Date is greater.
     */
    fun compareTwoDates(startDate: String?, endDate: String?, dateFormat: String?): Int {
        var result = 0
        val theFirstDate = getDateFromString(startDate, dateFormat)
        val theSecondDate = getDateFromString(endDate, dateFormat)
        result = theFirstDate!!.compareTo(theSecondDate)
        /* result will be an int < 0 if theFirstDate Date is less than the theSecondDate Date, 0 if they are equal, and an int > 0 if this Date is greater.*/return result
    }

    /*
     * isTimeNotExpired
     */
    fun isTimeNotExpired(date: String?): Boolean {
        val condition_var = compareDateWithCurrent(date)
        return condition_var < 0
    }

    /**
     * By Nitesh<br></br>
     *
     *
     *
     * @param date
     * @param inputPattern
     * @param field          The `Calendar` field to modify.
     * @param incrementValue
     * @return
     */
    fun addFieldIntoDate(
        date: String?,
        inputPattern: String?,
        field: Int,
        incrementValue: Int
    ): String {
        return try {
            val date1 = SimpleDateFormat(inputPattern, Locale.US).parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = date1
            calendar.add(field, incrementValue) // Add here
            SimpleDateFormat(inputPattern, Locale.US).format(calendar.time)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Week Dates list Model class
     */
    class WeekDatesModel(
        day: String,
        date: String,
        month: String,
        year: String,
        fromToday: String
    ) {
        var day = ""

        // e.g. SUNDAY, MONDAY etc
        var date = ""

        // e.g. 02, 31, 25 etc
        var month = ""

        // e.g. 01, 12, 05 etc
        var year = ""

        // e.g. 2016, 2018 etc
        var fromToday = "" // Date is before today's date or after or equals

        init {
            this.day = day
            this.date = date
            this.month = month
            this.year = year
            this.fromToday = fromToday
        }
    }

    class DatePickerFragment(
        private val date: String,
        private val fromDate: String,
        private val onDateSelectionListener: OnDateSelectionListener?
    ) : DialogFragment(), DatePickerDialog.OnDateSetListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val getfrom = date.split("-").toTypedArray()
            val year: Int
            val month: Int
            val day: Int
            day = getfrom[2].toInt()
            month = getfrom[1].toInt()
            year = getfrom[0].toInt()
            val c = Calendar.getInstance()
            c[year, month - 1] = day + 1
            val getTo = fromDate.split("-").toTypedArray()
            val year1: Int
            val month1: Int
            val day1: Int
            day1 = getTo[2].toInt()
            month1 = getTo[1].toInt()
            year1 = getTo[0].toInt()
            val c1 = Calendar.getInstance()
            c1[year1, month1 - 1] = day1
            val datePickerDialog: DatePickerDialog
            datePickerDialog = DatePickerDialog(
                requireActivity(), this, year,
                month - 1, day
            )
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.datePicker.maxDate = c1.timeInMillis
            return datePickerDialog
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            onDateSelectionListener?.onDateSelection(year.toString() + "-" + (month + 1) + "-" + day)
        }

        interface DateOfBirthPickListener {
            fun onDatePick(date: String?)
        }

        interface OnDateSelectionListener {
            fun onDateSelection(date: String?)
        }
    }

    class ToDatePickerFragment(
        private val date: String,
        private val fromDate: String,
        private val onDateSelectionListener: DatePickerFragment.OnDateSelectionListener?
    ) : DialogFragment(), DatePickerDialog.OnDateSetListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val getfrom = date.split("-").toTypedArray()
            val year: Int
            val month: Int
            val day: Int
            day = getfrom[2].toInt()
            month = getfrom[1].toInt()
            year = getfrom[0].toInt()
            val c = Calendar.getInstance()
            c[year, month - 1] = day + 1
            val getTo = fromDate.split("-").toTypedArray()
            val year1: Int
            val month1: Int
            val day1: Int
            day1 = getTo[2].toInt()
            month1 = getTo[1].toInt()
            year1 = getTo[0].toInt()
            val c1 = Calendar.getInstance()
            c1[year1, month1 - 1] = day1
            val datePickerDialog = DatePickerDialog(requireActivity(), this, year, month - 1, day)
            datePickerDialog.datePicker.minDate = c1.timeInMillis
            return datePickerDialog
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            onDateSelectionListener?.onDateSelection(year.toString() + "-" + (month + 1) + "-" + day)
        }

        interface OnDateSelectionListener {
            fun onDateSelection(date: String?)
        }
    }

    fun getDateWithUpdatedFormat(
        date: String?,
        sourceFormat: String,
        targetFormat: String
    ): String? {
        try {
            val sdf = SimpleDateFormat(sourceFormat, Locale.US)
            val strDate = sdf.parse(date)

            val sdf2 = SimpleDateFormat(targetFormat, Locale.US)
            return sdf2.format(strDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}