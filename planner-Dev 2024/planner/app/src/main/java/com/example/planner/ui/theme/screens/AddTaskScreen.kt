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
fun AddTaskScreen(context: Context, navController: NavController, coroutineScope: CoroutineScope) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var videoUrls by remember { mutableStateOf<List<String>>(emptyList()) } // Lista de URLs de vídeos
    var newVideoUrl by remember { mutableStateOf("") } // Campo para adicionar novo link de vídeo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campo para o título da tarefa
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título da Tarefa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo para a descrição da tarefa
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição da Tarefa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo para adicionar novo link de vídeo
        TextField(
            value = newVideoUrl,
            onValueChange = { newVideoUrl = it },
            label = { Text("Link do Vídeo (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cole o link do YouTube aqui") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Botão para adicionar o link de vídeo à lista
        Button(
            onClick = {
                if (newVideoUrl.isNotEmpty()) {
                    videoUrls = videoUrls + newVideoUrl // Adiciona o novo link à lista
                    newVideoUrl = "" // Limpa o campo de entrada
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar Vídeo")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Exibir a lista de vídeos adicionados
        if (videoUrls.isNotEmpty()) {
            Text(
                text = "Vídeos Adicionados:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(videoUrls) { url ->
                    Text(
                        text = url,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botão para adicionar a tarefa
        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    val newTask = StudyTask(
                        id = System.currentTimeMillis().toString(), // Gera um ID único
                        title = title,
                        description = description,
                        completed = false,
                        isFavorite = false,
                        videoUrls = videoUrls // Salva a lista de URLs de vídeos
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