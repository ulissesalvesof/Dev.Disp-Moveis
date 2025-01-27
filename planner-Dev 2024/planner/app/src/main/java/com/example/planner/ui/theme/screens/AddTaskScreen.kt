package com.example.planner.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.planner.data.TaskManager
import com.example.planner.data.StudyTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AddTaskScreen(context: Context, navController: NavController, coroutineScope: CoroutineScope) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") } // Novo campo para o link do vídeo

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
        TextField( // Novo campo para o link do vídeo
            value = videoUrl,
            onValueChange = { videoUrl = it },
            label = { Text("Link do Vídeo (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cole o link do YouTube aqui") } // Texto de placeholder
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    val newTask = StudyTask(
                        id = System.currentTimeMillis().toString(),
                        title = title,
                        description = description,
                        completed = false,
                        videoUrl = videoUrl // Adiciona o link do vídeo à tarefa
                    )
                    coroutineScope.launch {
                        TaskManager.addTask(context, newTask)
                        navController.popBackStack() // Volta para a tela anterior
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar Tarefa")
        }
    }
}