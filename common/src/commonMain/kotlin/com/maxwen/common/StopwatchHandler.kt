package com.maxwen.common

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class StopWatchHandler {
    private val _elapsedTime = MutableStateFlow(0L)
    private val _timerState = MutableStateFlow(StopWatchState.RESET)
    val timerState = _timerState.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("mm:ss:SS")
    val stopWatchText = _elapsedTime
        .map { millis ->
            LocalTime.ofNanoOfDay(millis * 1_000_000).format(formatter)
        }
        .stateIn(
            GlobalScope,
            SharingStarted.WhileSubscribed(5000),
            "00:00:00"
        )

    init {
        _timerState
            .flatMapLatest { timerState ->
                getTimerFlow(
                    isRunning = timerState == StopWatchState.RUNNING
                )
            }
            .onEach { timeDiff ->
                _elapsedTime.update { it + timeDiff }
            }
            .launchIn(GlobalScope)
    }

    fun toggleIsRunning() {
        when (timerState.value) {
            StopWatchState.RUNNING -> _timerState.update { StopWatchState.PAUSED }
            StopWatchState.PAUSED,
            StopWatchState.RESET -> _timerState.update { StopWatchState.RUNNING }
        }
    }

    fun resetTimer() {
        _timerState.update { StopWatchState.RESET }
        _elapsedTime.update { 0L }
    }

    private fun getTimerFlow(isRunning: Boolean): Flow<Long> {
        return flow {
            var startMillis = System.currentTimeMillis()
            while (isRunning) {
                val currentMillis = System.currentTimeMillis()
                val timeDiff = if (currentMillis > startMillis) {
                    currentMillis - startMillis
                } else 0L
                emit(timeDiff)
                startMillis = System.currentTimeMillis()
                delay(50L)
            }
        }
    }
}