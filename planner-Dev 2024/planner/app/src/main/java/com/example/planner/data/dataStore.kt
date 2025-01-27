package com.example.planner.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore by preferencesDataStore(name = "settings")