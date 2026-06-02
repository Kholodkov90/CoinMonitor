package com.kholodkov.coinmonitor.feature.statistic.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
private fun StatisticScreen(
    uiState: StatisticUiState,
    initialTab: StatisticTab = StatisticTab.WEEK
) {
    val pagerState = rememberPagerState(initialPage = initialTab.ordinal) {
        StatisticTab.entries.size
    }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            StatisticTab.entries.forEach { tab ->
                Tab(
                    selected = pagerState.currentPage == tab.ordinal,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(page = tab.ordinal) }
                    },
                    text = { Text(stringResource(tab.titleRes)) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val tab = StatisticTab.entries[page]
            val items = when (tab) {
                StatisticTab.WEEK -> uiState.weekItems
                StatisticTab.MONTH -> uiState.monthItems
                StatisticTab.YEAR -> uiState.yearItems
            }
            StatisticList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                statisticItems = items
            )
        }
    }
}

@Composable
private fun StatisticList(
    modifier: Modifier = Modifier,
    statisticItems: List<StatisticItem>
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(statisticItems) { item ->
            StatisticCard(item)
        }
    }
}
