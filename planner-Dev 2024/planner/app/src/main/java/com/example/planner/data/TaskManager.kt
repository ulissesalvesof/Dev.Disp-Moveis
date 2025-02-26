package com.example.planner.data

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth

object TaskManager {
    private val db = FirebaseFirestore.getInstance()

    suspend fun deleteTask(context: Context, taskId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid).collection("tasks").document(taskId).delete().await()
    }

    suspend fun addTask(context: Context, task: StudyTask) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val taskData = hashMapOf(
            "title" to task.title,
            "description" to task.description,
            "completed" to task.completed,
            "isFavorite" to task.isFavorite,
            "videoUrl" to task.videoUrl
        )
        db.collection("users").document(uid).collection("tasks").add(taskData).await()
    }

    // Busca uma tarefa pelo ID
    suspend fun getTaskById(context: Context, taskId: String?): StudyTask? {
        if (taskId == null) return null
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        val document = db.collection("users").document(uid).collection("tasks").document(taskId).get().await()
        return if (document.exists()) {
            StudyTask(
                id = document.id,
                title = document.getString("title") ?: "",
                description = document.getString("description") ?: "",
                completed = document.getBoolean("completed") ?: false,
                isFavorite = document.getBoolean("isFavorite") ?: false,
                videoUrl = document.getString("videoUrl") ?: ""
            )
        } else {
            null
        }
    }

    // Busca todas as tarefas do usuário
    fun getTasks(context: Context): Flow<List<StudyTask>> = flow {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@flow
        val result = db.collection("users").document(uid).collection("tasks").get().await()
        val tasks = result.documents.map { document ->
            StudyTask(
                id = document.id,
                title = document.getString("title") ?: "",
                description = document.getString("description") ?: "",
                completed = document.getBoolean("completed") ?: false,
                isFavorite = document.getBoolean("isFavorite") ?: false,
                videoUrl = document.getString("videoUrl") ?: ""
            )
        }
        emit(tasks)
    }

    // Busca tarefas favoritas
    suspend fun getFavoriteTasks(context: Context): List<StudyTask> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
        val result = db.collection("users").document(uid).collection("tasks")
            .whereEqualTo("isFavorite", true)
            .get()
            .await()
        return result.documents.map { document ->
            StudyTask(
                id = document.id,
                title = document.getString("title") ?: "",
                description = document.getString("description") ?: "",
                completed = document.getBoolean("completed") ?: false,
                isFavorite = document.getBoolean("isFavorite") ?: false,
                videoUrl = document.getString("videoUrl") ?: ""
            )
        }
    }

    // Busca tarefas concluídas
    suspend fun getCompletedTasks(context: Context): List<StudyTask> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
        val result = db.collection("users").document(uid).collection("tasks")
            .whereEqualTo("completed", true)
            .get()
            .await()
        return result.documents.map { document ->
            StudyTask(
                id = document.id,
                title = document.getString("title") ?: "",
                description = document.getString("description") ?: "",
                completed = document.getBoolean("completed") ?: false,
                isFavorite = document.getBoolean("isFavorite") ?: false,
                videoUrl = document.getString("videoUrl") ?: ""
            )
        }
    }

    // Atualiza uma tarefa
    suspend fun updateTask(context: Context, task: StudyTask) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val taskData = hashMapOf(
            "title" to task.title,
            "description" to task.description,
            "completed" to task.completed,
            "isFavorite" to task.isFavorite,
            "videoUrl" to task.videoUrl
        )
        db.collection("users").document(uid).collection("tasks").document(task.id).set(taskData).await()
    }
}