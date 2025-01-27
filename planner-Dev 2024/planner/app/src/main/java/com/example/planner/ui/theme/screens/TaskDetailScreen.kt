package com.example.planner.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.planner.data.StudyTask
import com.example.planner.data.TaskManager
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
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
            Text(text = task.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        onMarkAsCompleted()
                        snackbarMessage = if (task.completed) "Tarefa marcada como pendente!" else "Tarefa marcada como concluída!"
                        showSnackbar = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (task.completed) Color.Green else Color.Red
                    )
                ) {
                    Text(if (task.completed) "Concluída" else "Pendente")
                }
                Button(onClick = {
                    onAddToFavorites()
                    snackbarMessage = "Tarefa adicionada aos favoritos!"
                    showSnackbar = true
                }) {
                    Text("Adicionar aos Favoritos")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        navController.navigate("edit_task/${task.id}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue
                    )
                ) {
                    Text("Editar Tarefa")
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            TaskManager.deleteTask(context, task.id)
                            navController.popBackStack() // Volta para a tela anterior
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Remover Tarefa")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (task.videoUrl.isNotEmpty() && task.videoUrl.contains("youtube.com")) {
                Text(text = "Vídeo Relacionado:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                YouTubePlayer(task.videoUrl, context)
            } else if (task.videoUrl.isNotEmpty()) {
                Text(text = "Link de vídeo inválido ou não é do YouTube.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun YouTubePlayer(videoUrl: String, context: Context) {
    AndroidView(
        factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                enableAutomaticInitialization = false

                // Configura a inicialização com o listener correto
                initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        // Extrai o videoId do link
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
