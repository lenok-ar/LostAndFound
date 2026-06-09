package com.example.core.util

object ValidationUtils {
    fun isNotBlank(value: String): Boolean = value.isNotBlank()

    fun isValidPhone(value: String): Boolean =
        value.count(Char::isDigit) >= MIN_PHONE_DIGITS

    private const val MIN_PHONE_DIGITS = 10
}
