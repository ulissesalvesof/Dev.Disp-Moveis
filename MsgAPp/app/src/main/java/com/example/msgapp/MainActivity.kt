package com.example.msgapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.msgapp.data.local.database.AppDatabase
import com.example.msgapp.repository.MessageRepository
import com.example.msgapp.ui.theme.MsgAppTheme
import com.example.msgapp.ui.view.MessageApp
import com.example.msgapp.viewmodel.MessageViewModel
import com.example.msgapp.viewmodel.MessageViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "messages-db"
        ).build() // .fallbackToDestructiveMigration().build()
        val repository = MessageRepository(db.messageDao())

        setContent {
            MsgAppTheme {
                val viewModel: MessageViewModel = viewModel(
                    factory = MessageViewModelFactory(repository)
                )
                MessageApp(viewModel)
            }
        }
    }
}