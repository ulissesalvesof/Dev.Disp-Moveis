package com.example.planner.ui.theme.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.app.AlarmManager
import android.app.PendingIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.planner.data.StudyTask
import com.example.planner.data.TaskManager
import com.example.planner.receivers.AlarmReceiver
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun TaskDetailScreen(
    task: StudyTask,
    onMarkAsCompleted: () -> Unit,
    onAddToFavorites: () -> Unit,
    context: Context,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Cabeçalho da Tarefa
            Text(text = task.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Seção de Ações Rápidas
            ActionButtons(
                task = task,
                onMarkAsCompleted = onMarkAsCompleted,
                onAddToFavorites = onAddToFavorites,
                context = context,
                showSnackbar = { message ->
                    snackbarMessage = message
                    showSnackbar = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Seção de Gerenciamento
            ManagementButtons(
                task = task,
                navController = navController,
                context = context,
                coroutineScope = coroutineScope
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Player de Vídeo (se aplicável)
            VideoSection(task = task)
        }
    }
}

@Composable
private fun ActionButtons(
    task: StudyTask,
    onMarkAsCompleted: () -> Unit,
    onAddToFavorites: () -> Unit,
    context: Context,
    showSnackbar: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Botão de Status (Concluída/Pendente)
        Button(
            onClick = {
                onMarkAsCompleted()
                showSnackbar(
                    if (task.completed) "Tarefa marcada como pendente!"
                    else "Tarefa marcada como concluída!"
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (task.completed) Color.Green else Color.Red
            )
        ) {
            Text(if (task.completed) "Concluída" else "Pendente")
        }

        // Botão de Agendamento
        Button(
            onClick = { scheduleNotification(context, task, showSnackbar) }
        ) {
            Text("Agendar Lembrete")
        }

        // Botão de Favoritos
        Button(
            onClick = {
                onAddToFavorites()
                showSnackbar("Tarefa adicionada aos favoritos!")
            }
        ) {
            Text("Adicionar aos Favoritos")
        }
    }
}

@Composable
private fun ManagementButtons(
    task: StudyTask,
    navController: NavController,
    context: Context,
    coroutineScope: CoroutineScope
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Botão de Edição
        Button(
            onClick = { navController.navigate("edit_task/${task.id}") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Editar Tarefa")
        }

        // Botão de Exclusão
        Button(
            onClick = {
                coroutineScope.launch {
                    TaskManager.deleteTask(context, task.id)
                    navController.popBackStack()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Remover Tarefa")
        }
    }
}

@Composable
private fun VideoSection(task: StudyTask) {
    if (task.videoUrl.isNotEmpty()) {
        Column {
            Text(
                text = "Vídeo Relacionado:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (task.videoUrl.contains("youtube.com")) {
                YouTubePlayer(videoUrl = task.videoUrl, context = LocalContext.current)
            } else {
                Text(
                    text = "Link de vídeo inválido ou não é do YouTube.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun scheduleNotification(
    context: Context,
    task: StudyTask,
    showSnackbar: (String) -> Unit
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("title", task.title)
        putExtra("message", task.description)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        task.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerTime = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        add(Calendar.MINUTE, 1)
    }.timeInMillis

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    showSnackbar("Notificação agendada para 1 minuto!")
}

@Composable
private fun YouTubePlayer(videoUrl: String, context: Context) {
    AndroidView(
        factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                enableAutomaticInitialization = false
                initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        val videoId = videoUrl.substringAfter("v=").substringBefore("&")
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                })
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}