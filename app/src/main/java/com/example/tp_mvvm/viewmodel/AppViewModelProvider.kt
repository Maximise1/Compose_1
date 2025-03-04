package com.example.tp_mvvm.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tp_mvvm.MedApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AppViewModel(
                medApplication().container.chatsRepository,
                medApplication().userPreferencesRepository
            )
        }
    }
}

fun CreationExtras.medApplication(): MedApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MedApplication)