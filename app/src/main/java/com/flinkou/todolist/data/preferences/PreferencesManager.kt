package com.flinkou.todolist.data.preferences

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    private val store = context.createDataStore("user_preferences")

    val preferencesFlow = store.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            val hide = preferences[PreferencesKeys.hide_completed] ?: false
            UserPreferences(hide)
        }

    suspend fun updateHideCompleted(value: Boolean) {
        store.edit {
            it[PreferencesKeys.hide_completed] = value
        }
    }
}