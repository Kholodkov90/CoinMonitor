    package com.kholodkov.coinmonitor.app

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.kholodkov.coinmonitor.domain.usecase.auth.ObserveIsLoggedInUseCase
    import com.kholodkov.coinmonitor.domain.usecase.sync.SyncUseCase
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.Job
    import kotlinx.coroutines.flow.SharingStarted
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.stateIn
    import kotlinx.coroutines.launch
    import javax.inject.Inject

    @HiltViewModel
    class AppViewModel @Inject constructor(
        observeIsLoggedInUseCase: ObserveIsLoggedInUseCase,
        private val syncUseCase: SyncUseCase
    ) : ViewModel() {
        val isLoggedIn: StateFlow<Boolean?> = observeIsLoggedInUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

        private var syncJob: Job? = null

        init {
            viewModelScope.launch {
                isLoggedIn.collect { loggedIn ->
                    if (loggedIn == true) {
                        startSync()
                    } else if (loggedIn == false) {
                        stopSync()
                    }
                }
            }
        }

        fun onForeground() = startSync()

        fun onBackground() = stopSync()

        private fun startSync() {
            if (isLoggedIn.value != true) return
            if (syncJob?.isActive == true) return
            syncJob = viewModelScope.launch {
                syncUseCase()
            }
        }

        private fun stopSync() = syncJob?.cancel()
    }