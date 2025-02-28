package com.example.planner.ui.theme.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.app.AlarmManager
import android.app.PendingIntent
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

    // Estado para armazenar o número de minutos digitado pelo usuário
    var minutesInput by remember { mutableStateOf("") }

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

            // Campo de entrada para os minutos
            OutlinedTextField(
                value = minutesInput,
                onValueChange = { minutesInput = it },
                label = { Text("Minutos para agendar") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // Aceita apenas números
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botão de Agendamento
            IconButton(
                onClick = {
                    val minutes = minutesInput.toIntOrNull() ?: 0
                    if (minutes > 0) {
                        scheduleNotification(context, task, minutes) { message ->
                            snackbarMessage = message
                            showSnackbar = true
                        }
                    } else {
                        snackbarMessage = "Digite um número válido de minutos!"
                        showSnackbar = true
                    }
                },
                modifier = Modifier.size(40.dp) // Tamanho reduzido
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Agendar",
                    tint = MaterialTheme.colorScheme.primary
                )
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
                        onAddToFavorites()
                        snackbarMessage = if (task.isFavorite) "Removido dos favoritos!" else "Adicionado aos favoritos!"
                        showSnackbar = true
                    },
                    modifier = Modifier.size(40.dp) // Tamanho reduzido
                ) {
                    Icon(
                        imageVector = if (task.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (task.isFavorite) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }

                // Botão de Edição
                IconButton(
                    onClick = { navController.navigate("edit_task/${task.id}") },
                    modifier = Modifier.size(40.dp) // Tamanho reduzido
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
                    modifier = Modifier.size(40.dp) // Tamanho reduzido
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
                    if (url.contains("youtube.com")) {
                        YouTubePlayer(videoUrl = url, context = LocalContext.current)
                    } else {
                        Text(
                            text = "Link de vídeo inválido ou não é do YouTube.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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

private fun scheduleNotification(
    context: Context,
    task: StudyTask,
    delayMinutes: Int,  // Novo parâmetro para definir o tempo de atraso
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
        add(Calendar.MINUTE, delayMinutes)  // Usa o tempo definido pelo usuário
    }.timeInMillis
// Não funciona o sistema de alarme ainda
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

    showSnackbar("Notificação agenda para $delayMinutes minutos!")
}