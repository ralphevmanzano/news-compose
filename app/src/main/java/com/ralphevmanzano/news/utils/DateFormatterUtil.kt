package com.ralphevmanzano.news.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateFormatterUtil {
    private const val INPUT_FORMAT = "yyyy-MM-dd HH:mm:ss" // Updated format
    private const val OUTPUT_FORMAT = "MMMM d, yyyy"
    private const val TIMEZONE = "UTC"

    fun formatDate(inputDate: String): String {
        return try {
            val inputFormatter = SimpleDateFormat(INPUT_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone(TIMEZONE)
            }
            val outputFormatter = SimpleDateFormat(OUTPUT_FORMAT, Locale.getDefault())

            val date = inputFormatter.parse(inputDate)
            date?.let { outputFormatter.format(it) } ?: inputDate
        } catch (e: Exception) {
            inputDate
        }
    }

    fun parseDate(formattedDate: String): String {
        return try {
            val outputFormatter = SimpleDateFormat(OUTPUT_FORMAT, Locale.getDefault()).apply {
                this.timeZone = TimeZone.getDefault()
            }
            val inputFormatter = SimpleDateFormat(INPUT_FORMAT, Locale.getDefault()).apply {
                this.timeZone = TimeZone.getTimeZone(TIMEZONE)
            }

            val date = outputFormatter.parse(formattedDate)
            date?.let { inputFormatter.format(it) } ?: formattedDate
        } catch (e: Exception) {
            formattedDate
        }
    }
}