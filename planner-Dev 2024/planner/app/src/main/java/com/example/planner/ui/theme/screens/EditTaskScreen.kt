package com.example.planner.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    var newVideoUrl by remember { mutableStateOf("") }
    var editingIndex by remember { mutableStateOf<Int?>(null) } // Índice do link sendo editado

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

        // Campo para adicionar ou editar um link de vídeo
        TextField(
            value = newVideoUrl,
            onValueChange = { newVideoUrl = it },
            label = { Text("Link do Vídeo (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cole o link do YouTube aqui") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Botão para adicionar ou atualizar o link de vídeo
        Button(
            onClick = {
                if (newVideoUrl.isNotEmpty()) {
                    if (editingIndex != null) {
                        // Atualiza o link existente
                        videoUrls = videoUrls.toMutableList().apply {
                            this[editingIndex!!] = newVideoUrl
                        }
                        editingIndex = null // Reseta o índice de edição
                    } else {
                        // Adiciona um novo link
                        videoUrls = videoUrls + newVideoUrl
                    }
                    newVideoUrl = "" // Limpa o campo de entrada
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingIndex != null) "Atualizar Vídeo" else "Adicionar Vídeo")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Exibir lista de vídeos adicionados com opções de editar e remover
        if (videoUrls.isNotEmpty()) {
            Text(
                text = "Vídeos Adicionados:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(videoUrls.size) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = videoUrls[index],
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                // Inicia a edição do link
                                newVideoUrl = videoUrls[index]
                                editingIndex = index
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                // Remove o link da lista
                                videoUrls = videoUrls.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remover",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botão para salvar as alterações
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