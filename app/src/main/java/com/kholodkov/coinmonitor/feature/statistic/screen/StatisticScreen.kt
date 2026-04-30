package com.kholodkov.coinmonitor.feature.statistic.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kholodkov.coinmonitor.feature.statistic.StatisticViewModel
import com.kholodkov.coinmonitor.feature.statistic.model.StatisticItem
import com.kholodkov.coinmonitor.feature.statistic.state.StatisticUiState
import kotlinx.coroutines.launch

@Composable
fun StatisticScreenRoute(
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StatisticScreen(
        uiState = uiState
    )
}

@Composable
fun StatisticScreen(
    uiState: StatisticUiState,
    initialTab: StatisticTab = StatisticTab.WEEK
) {
    val pagerState =
        rememberPagerState(initialPage = initialTab.ordinal) { StatisticTab.entries.size }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            StatisticTab.entries.forEach { tab ->
                Tab(
                    selected = pagerState.currentPage == tab.ordinal,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(page = tab.ordinal) }
                    },
                    text = { Text(tab.title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val items = when (StatisticTab.entries[page]) {
                StatisticTab.WEEK -> uiState.weekItems
                StatisticTab.MONTH -> uiState.monthItems
                StatisticTab.YEAR -> uiState.yearItems
            }
            StatisticList(items)
        }
    }
}

@Composable
private fun StatisticList(statisticItem: List<StatisticItem>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(statisticItem) { item ->
            StatisticCard(item)
        }
    }
}
