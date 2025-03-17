package com.example.planner.ui.theme.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

    // Estado para armazenar o horário selecionado pelo usuário
    var selectedTime by remember { mutableStateOf<Calendar?>(null) }

    // Estado local para controlar o estado de favorito
    var isFavorite by remember { mutableStateOf(task.isFavorite) }

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
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Descrição da Tarefa em um "quadrado" separado
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botão para abrir o TimePickerDialog
            Button(
                onClick = {
                    // Abre o TimePickerDialog
                    val calendar = Calendar.getInstance()
                    val timePickerDialog = TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            // Atualiza o horário selecionado
                            selectedTime = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hourOfDay)
                                set(Calendar.MINUTE, minute)
                            }
                            // Agendar a notificação com o horário selecionado
                            selectedTime?.let { time ->
                                scheduleNotification(context, task, time, showSnackbar = { message ->
                                    snackbarMessage = message
                                    showSnackbar = true
                                })
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )
                    timePickerDialog.show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agendar Notificação")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botões de Gerenciamento e Favoritos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botão de Favoritos
                IconButton(
                    onClick = {
                        isFavorite = !isFavorite // Atualiza o estado local
                        onAddToFavorites()
                        // Chama a função para atualizar o estado no banco de dados
                        snackbarMessage = if (isFavorite) "Adicionado aos favoritos!" else "Removido dos favoritos!"
                        showSnackbar = true
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }

                // Botão de Edição
                IconButton(
                    onClick = { navController.navigate("edit_task/${task.id}") },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Botão de Exclusão
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            TaskManager.deleteTask(context, task.id)
                            snackbarMessage = "Tarefa removida com sucesso!"
                            showSnackbar = true
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Seção de Vídeos
            VideoSection(task = task)
        }
    }
}

@Composable
private fun VideoSection(task: StudyTask) {
    if (task.videoUrls.isNotEmpty()) {
        Column {
            Text(
                text = "Vídeos Relacionados:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(task.videoUrls) { url ->
                    if (url.contains("youtube.com") || url.contains("youtu.be")) {
                        YouTubePlayer(videoUrl = url, context = LocalContext.current)
                    } else {
                        Text(
                            text = "Link de vídeo inválido ou não é do YouTube.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun YouTubePlayer(videoUrl: String, context: Context) {
    AndroidView(
        factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                enableAutomaticInitialization = false
                initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        val videoId = extractVideoId(videoUrl)
                        if (videoId != null) {
                            youTubePlayer.loadVideo(videoId, 0f)
                        } else {
                            Toast.makeText(context, "Link do YouTube inválido", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
    )
}

/**
 * Extrai o videoId de URLs do YouTube, incluindo links móveis.
 */
private fun extractVideoId(videoUrl: String): String? {
    val patterns = listOf(
        "v=([^&]+)", // Padrão para links desktop (https://www.youtube.com/watch?v=VIDEO_ID)
        "youtu.be/([^?]+)", // Padrão para links encurtados (https://youtu.be/VIDEO_ID)
        "m.youtube.com/watch\\?v=([^&]+)" // Padrão para links móveis (https://m.youtube.com/watch?v=VIDEO_ID)
    )

    for (pattern in patterns) {
        val regex = Regex(pattern)
        val matchResult = regex.find(videoUrl)
        if (matchResult != null) {
            return matchResult.groupValues[1]
        }
    }
    return null
}

private fun scheduleNotification(
    context: Context,
    task: StudyTask,
    triggerTime: Calendar,
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

    val triggerTimeMillis = triggerTime.timeInMillis

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    }

    showSnackbar("Notificação agendada para ${triggerTime.get(Calendar.HOUR_OF_DAY)}:${triggerTime.get(Calendar.MINUTE)}!")
}