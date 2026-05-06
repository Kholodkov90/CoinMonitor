package com.kholodkov.coinmonitor.feature.statistic.screen

import androidx.annotation.StringRes
import com.kholodkov.coinmonitor.R

enum class StatisticTab(@param:StringRes val titleRes: Int) {
    WEEK(R.string.tab_week),
    MONTH(R.string.tab_month),
    YEAR(R.string.tab_year)
}