package com.example.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.runtime.*
import com.example.planner.data.StudyTask

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()

            val darkModeEnabled by context.dataStore.data
                .map { preferences ->
                    preferences[booleanPreferencesKey("dark_mode")] ?: false
                }
                .collectAsState(initial = false)

            val tasks by TaskManager.getTasks(context).collectAsState(initial = emptyList())

            StudyPlannerTheme(darkTheme = darkModeEnabled) {
                NavHost(navController = navController, startDestination = "task_list") {
                    composable("task_list") {
                        TaskListScreen(
                            taskList = tasks,
                            onTaskClick = { task ->
                                navController.navigate("task_detail/${task.id}")
                            },
                            context = context,
                            navController = navController,
                            coroutineScope = coroutineScope
                        )
                    }
                    composable("task_detail/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId")
                        val task = tasks.find { it.id == taskId }
                        if (task != null) {
                            TaskDetailScreen(
                                task = task,
                                onMarkAsCompleted = {
                                    coroutineScope.launch {
                                        TaskManager.updateTask(context, task.copy(completed = !task.completed))
                                    }
                                },
                                onAddToFavorites = {
                                    coroutineScope.launch {
                                        TaskManager.updateTask(context, task.copy(isFavorite = true))
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
                        val task = tasks.find { it.id == taskId }
                        if (task != null) {
                            EditTaskScreen(
                                task = task,
                                context = context,
                                navController = navController,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            favoriteTasks = tasks.filter { it.isFavorite },
                            onRemoveFromFavorites = { task ->
                                coroutineScope.launch {
                                    TaskManager.updateTask(context, task.copy(isFavorite = false))
                                }
                            }
                        )
                    }
                    composable("completed_tasks") {
                        CompletedTasksScreen(
                            completedTasks = tasks.filter { it.completed }
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