package com.example.mobdevemco.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class CustomDateTime: Serializable {
    private var dateTime : LocalDateTime

    constructor() {
        this.dateTime = LocalDateTime.now()
    }

    constructor(str: String) {
        this.dateTime = LocalDateTime.parse(str, DateTimeFormatter.ofPattern(sqliteFormat))
    }


    constructor(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        this.dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute)
    }

    fun getLocalDateTime(): String {
        return this.dateTime.toString()
    }
    fun getYear(): Int {
        return this.dateTime.format(DateTimeFormatter.ofPattern("yyyy")).toInt()
    }

    fun getMonth(): Int {
        return this.dateTime.format(DateTimeFormatter.ofPattern("MM")).toInt()
    }

    fun getDay(): Int {
        return this.dateTime.format(DateTimeFormatter.ofPattern("dd")).toInt()
    }

    override fun toString(): String {
        return this.dateTime.format(DateTimeFormatter.ofPattern(sqliteFormat))
    }

    fun toStringFormatted(): String {
        val month = this.dateTime.format(DateTimeFormatter.ofPattern("MM")).toInt()
        return monthString[month-1] + this.dateTime.format(DateTimeFormatter.ofPattern(" dd, yyyy | HH:mm"))
    }

    fun toStringDate(): String {
        return this.dateTime.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
    }

    fun toStringTime(): String {
        return this.dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
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
        private val sqliteFormat = "yyyy-MM-dd HH:mm"
    }
}