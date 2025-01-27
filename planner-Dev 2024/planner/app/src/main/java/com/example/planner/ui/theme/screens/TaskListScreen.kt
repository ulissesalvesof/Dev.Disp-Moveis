package com.example.planner.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.data.StudyTask
import com.example.planner.data.TaskManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    taskList: List<StudyTask>,
    onTaskClick: (StudyTask) -> Unit,
    context: Context,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val animationsEnabled = remember { mutableStateOf(true) }

    // Filtra as tarefas com base na query de busca
    val filteredTasks = if (searchQuery.isEmpty()) {
        taskList
    } else {
        taskList.filter { task ->
            task.title.contains(searchQuery, ignoreCase = true) ||
                    task.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_task") }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Planner de Estudos") },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ver Favoritos") },
                            onClick = {
                                navController.navigate("favorites")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Tarefas Concluídas") },
                            onClick = {
                                navController.navigate("completed_tasks")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Configurações") },
                            onClick = {
                                navController.navigate("settings")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ajuda") },
                            onClick = {
                                navController.navigate("help")
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Campo de busca
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar tarefas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (filteredTasks.isEmpty()) {
                    item {
                        Text(
                            text = if (searchQuery.isEmpty()) "Nenhuma tarefa disponível." else "Nenhuma tarefa encontrada.",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 18.sp
                        )
                    }
                } else {
                    items(filteredTasks) { task ->
                        var isCompleted by remember { mutableStateOf(task.completed) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onTaskClick(task) },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(task.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(task.description, fontSize = 16.sp)
                                Button(
                                    onClick = {
                                        isCompleted = !isCompleted
                                        coroutineScope.launch {
                                            TaskManager.updateTask(context, task.copy(completed = isCompleted))
                                            snackbarHostState.showSnackbar(if (isCompleted) "Tarefa concluída!" else "Tarefa marcada como pendente!")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCompleted) Color.Green else Color.Red
                                    )
                                ) {
                                    Text(if (isCompleted) "Concluída" else "Pendente")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}