package com.maxwen.common

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    val handler = StopWatchHandler()
    val timerState by handler.timerState.collectAsState()
    val timerText by handler.stopWatchText.collectAsState()
    var showStopWatch by remember { mutableStateOf(false) }
    var showCounter by remember { mutableStateOf(false) }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                text = "Hello, Desktop!"
            }) {
                Text(text)
            }

            var count by remember {
                mutableStateOf(0)
            }
            Button(onClick = { showCounter = !showCounter }) {
                Text(text = "Counter")
            }
            AnimatedVisibility(
                visible = showCounter
            ) {
                Column {
                    AnimatedCounter(
                        count = count,
                        style = MaterialTheme.typography.h1
                    )
                    Button(onClick = { count++ }) {
                        Text(text = "Increment")
                    }
                }
            }
            Button(onClick = { showStopWatch = !showStopWatch }) {
                Text(text = "Stopwatch")
            }

            AnimatedVisibility(visible = showStopWatch, content = {
                StopWatch(
                    state = timerState,
                    text = timerText,
                    onToggleRunning = handler::toggleIsRunning,
                    onReset = handler::resetTimer,
                    modifier = Modifier.fillMaxSize()
                )
            })
        }
    }
}

@Composable
private fun StopWatch(
    state: StopWatchState,
    text: String,
    onToggleRunning: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onToggleRunning) {
                Icon(
                    imageVector = if (state == StopWatchState.RUNNING) {
                        Icons.Default.ArrowBack
                    } else {
                        Icons.Default.PlayArrow
                    },
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onReset,
                enabled = state != StopWatchState.RESET,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.surface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        }
    }
}
