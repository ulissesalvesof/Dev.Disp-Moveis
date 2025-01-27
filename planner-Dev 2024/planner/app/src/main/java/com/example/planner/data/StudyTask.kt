package com.example.planner.data

data class StudyTask(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val isFavorite: Boolean = false,
    val videoUrl: String = ""
)