package com.example.planner.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.planner.data.StudyTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    context: Context,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    var taskList by remember { mutableStateOf<List<StudyTask>>(emptyList()) }
    var showMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    var reloadTrigger by remember { mutableStateOf(0) }

    // Carregar tarefas ao iniciar e recarregar quando necessário
    LaunchedEffect(currentUser, reloadTrigger) {
        if (currentUser != null) {
            taskList = getTasks(currentUser.uid)
        }
    }

    // Função para atualizar a lista local
    fun updateLocalTask(updatedTask: StudyTask) {
        taskList = taskList.map { if (it.id == updatedTask.id) updatedTask else it }
    }

    // Filtro de busca
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
                        DropdownMenuItem(
                            text = { Text("Sair") },
                            onClick = {
                                logout(context, navController)
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
                            text = if (searchQuery.isEmpty()) "Nenhuma tarefa disponível."
                            else "Nenhuma tarefa encontrada.",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 18.sp
                        )
                    }
                } else {
                    items(filteredTasks) { task ->
                        var isCompleted by remember { mutableStateOf(task.completed) }
                        var isFavorite by remember { mutableStateOf(task.isFavorite) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    navController.navigate("task_detail/${task.id}")
                                },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(task.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(task.description, fontSize = 16.sp)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = {
                                            isCompleted = !isCompleted
                                            val updatedTask = task.copy(
                                                completed = isCompleted,
                                                videoUrls = task.videoUrls // Preservar URLs
                                            )
                                            coroutineScope.launch {
                                                updateTask(currentUser?.uid, updatedTask)
                                                updateLocalTask(updatedTask)
                                                snackbarHostState.showSnackbar(
                                                    if (isCompleted) "Tarefa concluída!"
                                                    else "Tarefa pendente!"
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isCompleted) Color.Green else Color.Red
                                        )
                                    ) {
                                        Text(if (isCompleted) "Concluída" else "Pendente")
                                    }
                                    IconButton(
                                        onClick = {
                                            isFavorite = !isFavorite
                                            val updatedTask = task.copy(
                                                isFavorite = isFavorite,
                                                videoUrls = task.videoUrls // Preservar URLs
                                            )
                                            coroutineScope.launch {
                                                updateTask(currentUser?.uid, updatedTask)
                                                updateLocalTask(updatedTask)
                                                snackbarHostState.showSnackbar(
                                                    if (isFavorite) "Adicionado aos favoritos!"
                                                    else "Removido dos favoritos!"
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isFavorite) Icons.Default.Favorite
                                            else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorito",
                                            tint = if (isFavorite) Color.Red else Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Funções auxiliares
fun logout(context: Context, navController: NavController) {
    FirebaseAuth.getInstance().signOut()
    navController.navigate("login") {
        popUpTo(navController.graph.startDestinationId) { inclusive = true }
    }
}

suspend fun getTasks(uid: String): List<StudyTask> {
    return try {
        val result = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("tasks")
            .get()
            .await()
        result.documents.map { document ->
            StudyTask(
                id = document.id,
                title = document.getString("title") ?: "",
                description = document.getString("description") ?: "",
                completed = document.getBoolean("completed") ?: false,
                isFavorite = document.getBoolean("isFavorite") ?: false,
                videoUrls = document.get("videoUrls") as? List<String> ?: emptyList() // Campo corrigido
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

suspend fun updateTask(uid: String?, task: StudyTask) {
    if (uid == null) return
    val taskData = hashMapOf(
        "title" to task.title,
        "description" to task.description,
        "completed" to task.completed,
        "isFavorite" to task.isFavorite,
        "videoUrls" to task.videoUrls // Campo obrigatório
    )
    FirebaseFirestore.getInstance()
        .collection("users")
        .document(uid)
        .collection("tasks")
        .document(task.id)
        .set(taskData)
        .await()
}