package com.kholodkov.coinmonitor.feature.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholodkov.coinmonitor.domain.usecase.statistic.ObserveStatisticSummaryUseCase
import com.kholodkov.coinmonitor.feature.statistic.mapper.toMonthStatisticItems
import com.kholodkov.coinmonitor.feature.statistic.mapper.toWeekStatisticItems
import com.kholodkov.coinmonitor.feature.statistic.mapper.toYearStatisticItems
import com.kholodkov.coinmonitor.feature.statistic.state.StatisticUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    observeStatisticSummaryUseCase: ObserveStatisticSummaryUseCase
) : ViewModel() {

    val uiState: StateFlow<StatisticUiState> = observeStatisticSummaryUseCase().map { summary ->
        StatisticUiState(
            weekItems = summary.weeklyStats.toWeekStatisticItems(summary.currency),
            monthItems = summary.monthlyStats.toMonthStatisticItems(summary.currency),
            yearItems = summary.yearlyStats.toYearStatisticItems(summary.currency),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticUiState()
    )
}