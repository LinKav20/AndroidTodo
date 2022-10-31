package com.flinkou.todolist.data.preferences

import androidx.datastore.preferences.preferencesKey

object PreferencesKeys {
    val hide_completed = preferencesKey<Boolean>("hide_completed")
}