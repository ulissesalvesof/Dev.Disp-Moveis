package com.example.planner.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreTaskManager {
    private val db = FirebaseFirestore.getInstance()
    private const val TASKS_COLLECTION = "tasks"

    // Adicionar uma nova tarefa
    suspend fun addTask(task: StudyTask) {
        val taskData = hashMapOf(
            "id" to task.id,
            "title" to task.title,
            "description" to task.description,
            "completed" to task.completed,
            "isFavorite" to task.isFavorite,
            "videoUrl" to task.videoUrl
        )
        db.collection(TASKS_COLLECTION).document(task.id).set(taskData).await()
    }

    // Atualizar uma tarefa existente
    suspend fun updateTask(task: StudyTask) {
        val taskData = hashMapOf(
            "title" to task.title,
            "description" to task.description,
            "completed" to task.completed,
            "isFavorite" to task.isFavorite,
            "videoUrl" to task.videoUrl
        )
        db.collection(TASKS_COLLECTION).document(task.id).update(taskData.toMap()).await()
    }

    // Excluir uma tarefa
    suspend fun deleteTask(taskId: String) {
        db.collection(TASKS_COLLECTION).document(taskId).delete().await()
    }

    // Obter todas as tarefas do usuário atual
    suspend fun getTasks(userId: String): List<StudyTask> {
        val snapshot = db.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId) // Filtra tarefas pelo ID do usuário
            .get()
            .await()
        return snapshot.documents.map { document ->
            StudyTask(
                id = document.getString("id") ?: "",
                title = document.getString("title") ?: "",
                description = document.getString("description") ?: "",
                completed = document.getBoolean("completed") ?: false,
                isFavorite = document.getBoolean("isFavorite") ?: false,
                videoUrl = document.getString("videoUrl") ?: ""
            )
        }
    }
}