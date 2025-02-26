package com.example.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planner.data.TaskManager
import com.example.planner.data.dataStore
import com.example.planner.ui.theme.StudyPlannerTheme
import com.example.planner.ui.theme.screens.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.example.planner.data.StudyTask
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.ViewModelProvider
import com.example.authapp.ui.view.LoginScreen
import com.example.authapp.ui.view.RegisterScreen
import com.example.planner.ui.theme.viewmodel.AuthViewModel
import com.example.planner.ui.theme.viewmodel.AuthViewModelFactory
import com.example.planner.data.AuthRepository
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        // Inicialize o AuthRepository e AuthViewModel
        val authRepository = AuthRepository()
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepository))
            .get(AuthViewModel::class.java)

        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()

            val darkModeEnabled by context.dataStore.data
                .map { preferences ->
                    preferences[booleanPreferencesKey("dark_mode")] ?: false
                }
                .collectAsState(initial = false)

            // Verifica se o usuário já está autenticado
            LaunchedEffect(Unit) {
                if (authViewModel.isUserAuthenticated()) {
                    navController.navigate("task_list") // Redireciona para a tela principal
                }
            }

            StudyPlannerTheme(darkTheme = darkModeEnabled) {
                Scaffold(
                    bottomBar = {
                        // Barra de navegação inferior (apenas para telas autenticadas)
                        if (authViewModel.isUserAuthenticated()) {
                            NavigationBar {
                                val currentRoute = navController.currentDestination?.route
                                NavigationBarItem(
                                    icon = { Icon(ImageVector.vectorResource(R.drawable.ic_home), contentDescription = "Início") },
                                    label = { Text("Início") },
                                    selected = currentRoute == "task_list",
                                    onClick = {
                                        navController.navigate("task_list") {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(ImageVector.vectorResource(R.drawable.ic_favorite), contentDescription = "Favoritos") },
                                    label = { Text("Favoritos") },
                                    selected = currentRoute == "favorites",
                                    onClick = {
                                        navController.navigate("favorites") {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(ImageVector.vectorResource(R.drawable.ic_done), contentDescription = "Concluídas") },
                                    label = { Text("Concluídas") },
                                    selected = currentRoute == "completed_tasks",
                                    onClick = {
                                        navController.navigate("completed_tasks") {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "login", // Tela inicial
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        // Telas de autenticação
                        composable("login") {
                            LoginScreen(authViewModel, navController)
                        }
                        composable("register") {
                            RegisterScreen(authViewModel, navController)
                        }
                        composable("forgotPassword") {
                            ForgotPasswordScreen(authViewModel, navController)
                        }

                        // Telas do Planner (apenas para usuários autenticados)
                        composable("task_list") {
                            TaskListScreen(
                                context = context,
                                navController = navController,
                                coroutineScope = coroutineScope
                            )
                        }
                        composable("task_detail/{taskId}") { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId")
                            var task by remember { mutableStateOf<StudyTask?>(null) }

                            // Carrega a tarefa usando uma coroutine
                            LaunchedEffect(taskId) {
                                task = taskId?.let { TaskManager.getTaskById(context, it) }
                                println("Tarefa carregada: $task") // Log para depuração
                            }

                            if (task != null) {
                                TaskDetailScreen(
                                    task = task!!,
                                    onMarkAsCompleted = {
                                        coroutineScope.launch {
                                            TaskManager.updateTask(context, task!!.copy(completed = !task!!.completed))
                                        }
                                    },
                                    onAddToFavorites = {
                                        coroutineScope.launch {
                                            TaskManager.updateTask(context, task!!.copy(isFavorite = true))
                                        }
                                    },
                                    context = context,
                                    navController = navController,
                                    coroutineScope = coroutineScope
                                )
                            }
                        }
                        composable("edit_task/{taskId}") { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId")
                            var task by remember { mutableStateOf<StudyTask?>(null) }

                            // Carrega a tarefa usando uma coroutine
                            LaunchedEffect(taskId) {
                                task = taskId?.let { TaskManager.getTaskById(context, it) }
                            }

                            if (task != null) {
                                EditTaskScreen(
                                    task = task!!,
                                    context = context,
                                    navController = navController,
                                    coroutineScope = coroutineScope
                                )
                            }
                        }
                        composable("favorites") {
                            var favoriteTasks by remember { mutableStateOf<List<StudyTask>>(emptyList()) }

                            // Carrega as tarefas favoritas usando uma coroutine
                            LaunchedEffect(Unit) {
                                favoriteTasks = TaskManager.getFavoriteTasks(context)
                            }

                            FavoritesScreen(
                                favoriteTasks = favoriteTasks,
                                onRemoveFromFavorites = { task ->
                                    coroutineScope.launch {
                                        TaskManager.updateTask(context, task.copy(isFavorite = false))
                                    }
                                }
                            )
                        }
                        composable("completed_tasks") {
                            var completedTasks by remember { mutableStateOf<List<StudyTask>>(emptyList()) }

                            // Carrega as tarefas concluídas usando uma coroutine
                            LaunchedEffect(Unit) {
                                completedTasks = TaskManager.getCompletedTasks(context)
                            }

                            CompletedTasksScreen(
                                completedTasks = completedTasks
                            )
                        }
                        composable("help") {
                            HelpScreen()
                        }
                        composable("settings") {
                            SettingsScreen(context = context)
                        }
                        composable("add_task") {
                            AddTaskScreen(
                                context = context,
                                navController = navController,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                }
            }
        }
    }
}