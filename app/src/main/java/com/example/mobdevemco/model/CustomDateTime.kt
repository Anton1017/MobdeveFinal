package com.example.mobdevemco.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class CustomDateTime {
    private var dateTime : LocalDateTime

    constructor() {
        this.dateTime = LocalDateTime.now()
//        date = dateTime.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
//        time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    constructor(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        this.dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute)
//        date = dateTime.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
//        time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun toStringFull(): String {
        val month = this.dateTime.format(DateTimeFormatter.ofPattern("MM")).toInt()
        return monthString[month-1] + this.dateTime.format(DateTimeFormatter.ofPattern(" dd, yyyy | HH:mm"))
    }

    fun toStringNoYear(): String {
        val month = this.dateTime.format(DateTimeFormatter.ofPattern("MM")).toInt()
        return monthString[month-1] + this.dateTime.format(DateTimeFormatter.ofPattern(" dd | HH:mm"))
    }

    companion object {
        private val monthString = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
    }
}