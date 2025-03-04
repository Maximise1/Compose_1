package com.example.tp_mvvm

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tp_mvvm.model.data.AppContainer
import com.example.tp_mvvm.model.data.AppDataContainer
import com.example.tp_mvvm.model.data.UserPreferencesRepository

private const val THEME_PREFERENCE = "theme_preference"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = THEME_PREFERENCE
)

class MedApplication : Application() {

    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}
