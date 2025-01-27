package com.example.planner.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.map
import com.example.planner.data.dataStore
import kotlinx.coroutines.flow.Flow

object TaskManager {
    private val TASKS_KEY = stringSetPreferencesKey("tasks")

    suspend fun addTask(context: Context, task: StudyTask) {
        context.dataStore.edit { preferences ->
            val tasks = preferences[TASKS_KEY] ?: emptySet()
            preferences[TASKS_KEY] = tasks + "${task.id},${task.title},${task.description},${task.completed},${task.isFavorite},${task.videoUrl}"
        }
    }

    suspend fun updateTask(context: Context, task: StudyTask) {
        context.dataStore.edit { preferences ->
            val tasks = preferences[TASKS_KEY]?.toMutableSet() ?: mutableSetOf()
            tasks.removeIf { it.startsWith(task.id) }
            tasks.add("${task.id},${task.title},${task.description},${task.completed},${task.isFavorite},${task.videoUrl}")
            preferences[TASKS_KEY] = tasks
        }
    }

    suspend fun deleteTask(context: Context, taskId: String) {
        context.dataStore.edit { preferences ->
            val tasks = preferences[TASKS_KEY]?.toMutableSet() ?: mutableSetOf()
            tasks.removeIf { it.startsWith(taskId) }
            preferences[TASKS_KEY] = tasks
        }
    }

    fun getTasks(context: Context): Flow<List<StudyTask>> = context.dataStore.data.map { preferences ->
        val tasks = preferences[TASKS_KEY] ?: emptySet()
        tasks.map { taskString ->
            val parts = taskString.split(",")
            StudyTask(
                id = parts[0],
                title = parts[1],
                description = parts[2],
                completed = parts[3].toBoolean(),
                isFavorite = parts[4].toBoolean(),
                videoUrl = parts[5]
            )
        }
    }
}