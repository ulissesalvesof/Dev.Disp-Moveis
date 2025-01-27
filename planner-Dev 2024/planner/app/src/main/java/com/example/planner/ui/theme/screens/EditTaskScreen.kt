package com.example.planner.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.layout.*
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
    var videoUrl by remember { mutableStateOf(task.videoUrl) }

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
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = videoUrl,
            onValueChange = { videoUrl = it },
            label = { Text("Link do Vídeo (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cole o link do YouTube aqui") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    val updatedTask = task.copy(
                        title = title,
                        description = description,
                        videoUrl = videoUrl
                    )
                    coroutineScope.launch {
                        TaskManager.updateTask(context, updatedTask)
                        navController.popBackStack() // Volta para a tela anterior
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Alterações")
        }
    }
}