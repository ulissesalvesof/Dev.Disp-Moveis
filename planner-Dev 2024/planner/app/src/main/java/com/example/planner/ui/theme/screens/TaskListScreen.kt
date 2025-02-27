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
import com.example.planner.data.StudyTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    // Carregar tarefas ao iniciar a tela
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            taskList = getTasks(currentUser.uid)
        }
    }

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
                        DropdownMenuItem(
                            text = { Text("Sair") },
                            onClick = {
                                // Implementar a lógica de logout
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
                                .clickable {
                                    // Navega para a tela de detalhes da tarefa
                                    navController.navigate("task_detail/${task.id}")
                                },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(task.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(task.description, fontSize = 16.sp)
                                if (task.videoUrls.isNotEmpty()) {
                                    Text("Vídeo: ${task.videoUrls}", fontSize = 14.sp, color = Color.Blue)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = {
                                            isCompleted = !isCompleted
                                            coroutineScope.launch {
                                                updateTask(currentUser?.uid, task.copy(completed = isCompleted))
                                                snackbarHostState.showSnackbar(
                                                    if (isCompleted) "Tarefa concluída!" else "Tarefa marcada como pendente!"
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
                                            coroutineScope.launch {
                                                toggleFavorite(currentUser?.uid, task)
                                                snackbarHostState.showSnackbar(
                                                    if (task.isFavorite) "Removido dos favoritos!" else "Adicionado aos favoritos!"
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (task.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorito",
                                            tint = if (task.isFavorite) Color.Red else Color.Gray
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
fun logout(context: Context, navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    auth.signOut() // Faz logout do usuário

    // Redireciona para a tela de login
    navController.navigate("login") {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = true // Remove todas as telas da pilha de navegação
        }
    }
}

// Função para buscar tarefas do Firestore
suspend fun getTasks(uid: String): List<StudyTask> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val result = db.collection("users")
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
                videoUrls = document.get("videoUrl") as? List<String> ?: emptyList()
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

// Função para atualizar uma tarefa no Firestore
suspend fun updateTask(uid: String?, task: StudyTask) {
    if (uid == null) return
    val db = FirebaseFirestore.getInstance()
    val taskData = hashMapOf(
        "title" to task.title,
        "description" to task.description,
        "completed" to task.completed,
        "isFavorite" to task.isFavorite,
        "videoUrl" to task.videoUrls
    )
    db.collection("users")
        .document(uid)
        .collection("tasks")
        .document(task.id) // Usando o ID da tarefa
        .set(taskData)
        .await()
}

// Função para alternar o estado de favorito de uma tarefa
suspend fun toggleFavorite(uid: String?, task: StudyTask) {
    if (uid == null) return
    val db = FirebaseFirestore.getInstance()
    val updatedTask = task.copy(isFavorite = !task.isFavorite)
    updateTask(uid, updatedTask)
}