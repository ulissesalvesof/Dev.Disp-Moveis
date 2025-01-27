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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.planner.data.dataStore
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(context: Context) {
    val scope = rememberCoroutineScope()
    val darkModeEnabled = remember { mutableStateOf(false) }
    val notificationsEnabled = remember { mutableStateOf(false) }
    val animationsEnabled = remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Modo Escuro
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Modo Escuro", modifier = Modifier.weight(1f))
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

        // Notificações
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Notificações", modifier = Modifier.weight(1f))
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

        // Animações (Corrigido)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Animações", modifier = Modifier.weight(1f))
            Switch(
                checked = animationsEnabled.value,
                onCheckedChange = {
                    animationsEnabled.value = it
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[booleanPreferencesKey("animations")] = it
                        }
                    }
                }
            )
        }
    }
}