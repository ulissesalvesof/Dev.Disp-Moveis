package com.example.planner.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.planner.data.dataStore

private val Context.dataStore by preferencesDataStore(name = "settings")

@Composable
fun SettingsScreen(context: Context) {
    val scope = rememberCoroutineScope()
    val darkModeEnabled = remember { mutableStateOf(false) }
    val notificationsEnabled = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Modo Escuro")
            Switch(
                checked = darkModeEnabled.value,
                onCheckedChange = {
                    darkModeEnabled.value = it
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[booleanPreferencesKey("dark_mode")] = it
                        }
                    }
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Notificações")
            Switch(
                checked = notificationsEnabled.value,
                onCheckedChange = {
                    notificationsEnabled.value = it
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[booleanPreferencesKey("notifications")] = it
                        }
                    }
                }
            )
        }
    }
}