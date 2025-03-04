package com.example.tp_mvvm

import android.icu.util.Calendar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp_mvvm.model.data.ChatsDao
import com.example.tp_mvvm.model.data.ChatsRepository
import com.example.tp_mvvm.model.data.LinesDao
import com.example.tp_mvvm.model.data.OfflineChatsRepository
import com.example.tp_mvvm.viewmodel.AppViewModel
import com.example.tp_mvvm.viewmodel.AppViewModelProvider
import io.mockk.mockk
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat

class ViewModelTest {
    val repository: ChatsRepository = mockk<ChatsRepository>(relaxed = true)
    val viewModel: AppViewModel = AppViewModel(repository)

    @Test
    fun testTimeParsing() {
        val testTime = System.currentTimeMillis() / 1000 - 20
        val parsedTimeString = viewModel.parseDateTime(testTime)
        assertEquals("20 seconds ago", parsedTimeString)
    }

    @Test
    fun testUiState() {
        val appBarState = viewModel.uiState.value.isTopBarHidden
        viewModel.toggleTopBar()
        assertEquals(!appBarState, viewModel.uiState.value.isTopBarHidden)
    }
}