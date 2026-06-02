package com.kholodkov.coinmonitor.feature.main.model.ui

data class DayState(
    val date: String = "",
    val dateDescriptionRes: Int? = null,
    val isDatePickerVisible: Boolean = false
)