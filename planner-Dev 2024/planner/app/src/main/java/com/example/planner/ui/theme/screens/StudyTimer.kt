package com.example.planner.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun StudyTimer(onTimerEnd: () -> Unit) {
    var timeLeft by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        if (timeLeft == 0) {
            onTimerEnd()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tempo Restante: ${timeLeft / 60}:${timeLeft % 60}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { isRunning = !isRunning },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(if (isRunning) "Pausar" else "Iniciar")
        }
        Button(
            onClick = { timeLeft = 25 * 60 },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Reiniciar")
        }
    }
}