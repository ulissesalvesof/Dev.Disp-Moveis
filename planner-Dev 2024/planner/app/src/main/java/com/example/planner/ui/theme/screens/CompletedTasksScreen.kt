package com.example.planner.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planner.data.StudyTask

@Composable
fun CompletedTasksScreen(
    completedTasks: List<StudyTask>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (completedTasks.isEmpty()) {
            item {
                Text("Nenhuma tarefa concluída.", modifier = Modifier.fillMaxWidth())
            }
        } else {
            items(completedTasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(task.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(task.description, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}