package com.example.tp_mvvm

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.example.tp_mvvm.model.data.ChatsRepository
import com.example.tp_mvvm.view.ChatScreen
import com.example.tp_mvvm.view.MainMenuScreen
import com.example.tp_mvvm.view.theme.TP_MVVMTheme
import com.example.tp_mvvm.viewmodel.AppViewModel
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class ViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    val repository: ChatsRepository = mockk<ChatsRepository>(relaxed = true)
    val viewModel: AppViewModel = AppViewModel(repository)

    @Test
    fun testNewMessageWritable() {
        composeTestRule.setContent {
            TP_MVVMTheme {
                ChatScreen(
                    contentPadding = PaddingValues(),
                    viewModel = viewModel
                )
            }
        }
        composeTestRule.onNodeWithText("Can you tell me about...")
            .performTextInput("New message")
        composeTestRule.onNodeWithText("New message").assertExists(
            "No node with this text was found."
        )
    }

    @Test
    fun testSearchWritable() {
        composeTestRule.setContent {
            TP_MVVMTheme {
                MainMenuScreen(
                    contentPadding = PaddingValues(),
                    viewModel = viewModel,
                    onChatClicked = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Type here...")
            .performTextInput("Query")
        composeTestRule.onNodeWithText("Query").assertExists(
            "No node with this text was found."
        )
    }
}