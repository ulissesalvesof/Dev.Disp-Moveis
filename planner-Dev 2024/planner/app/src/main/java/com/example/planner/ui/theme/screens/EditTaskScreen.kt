package com.example.planner.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.planner.data.StudyTask
import com.example.planner.data.TaskManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun EditTaskScreen(
    task: StudyTask,
    context: Context,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var videoUrls by remember { mutableStateOf(task.videoUrls) }
    var newVideoUrl by remember { mutableStateOf("")}

        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título da Tarefa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição da Tarefa") },
            modifier = Modifier.fillMaxWidth()
        )// Campo para adicionar novo link de vídeo
            TextField(
                value = newVideoUrl,
                onValueChange = { newVideoUrl = it },
                label = { Text("Link do Vídeo (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cole o link do YouTube aqui") }
            )
            Button(
                onClick = {
                    if (newVideoUrl.isNotEmpty()) {
                        videoUrls = videoUrls + newVideoUrl
                        newVideoUrl = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adicionar Vídeo")
            }

            // Exibir lista de vídeos adicionados
            LazyColumn {
                items(videoUrls) { url ->
                    Text(text = url, modifier = Modifier.padding(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        val updatedTask = task.copy(
                            title = title,
                            description = description,
                            videoUrls = videoUrls // Atualiza a lista de URLs de vídeos
                        )
                        coroutineScope.launch {
                            TaskManager.updateTask(context, updatedTask)
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Alterações")
            }
        }
}