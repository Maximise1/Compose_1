package com.example.tp_mvvm.view

import android.icu.util.Calendar
import android.view.View.OnLongClickListener
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tp_mvvm.model.data.Chat
import com.example.tp_mvvm.model.data.Line
import com.example.tp_mvvm.view.theme.BackgroundColor
import com.example.tp_mvvm.view.theme.BackgroundColorLight
import com.example.tp_mvvm.view.theme.BarsBackgroundColor
import com.example.tp_mvvm.view.theme.BarsBackgroundColorLight
import com.example.tp_mvvm.view.theme.ButtonDisabledColor
import com.example.tp_mvvm.view.theme.ItemBackgroundColor
import com.example.tp_mvvm.view.theme.SmallTextColor
import com.example.tp_mvvm.view.theme.SmallTextColorLight
import com.example.tp_mvvm.view.theme.TitlesAndIconsColor
import com.example.tp_mvvm.view.theme.openSansFont
import com.example.tp_mvvm.viewmodel.AppViewModel

@Composable
fun MainMenuScreen(
    contentPadding: PaddingValues,
    viewModel: AppViewModel,
    onChatClicked: (Long) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentChats by uiState.currentChats.collectAsState(emptyList())
    val currentLastLines by uiState.lastLines.collectAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = contentPadding
        ) {
            items(currentChats, key = { chat -> chat.id }) { chat ->
                ChatItem(
                    chat = chat,
                    onChatClicked = onChatClicked,
                    viewModel = viewModel,
                    isDeleteState = uiState.isDeleteState,
                    selectedForDeletion = chat.id in uiState.chatsToDelete,
                    lastLines = currentLastLines,
                    isDarkTheme = isDarkTheme
                )
            }
        }
        if (!uiState.isDeleteState) {
            IconButton(
                onClick = { onChatClicked(-1L) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-16).dp, y = ((-16).dp))
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkTheme) BarsBackgroundColor else BarsBackgroundColorLight
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = "Start new chat button",
                    modifier = Modifier
                        .size(36.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatItem(
    chat: Chat,
    onChatClicked: (Long) -> Unit,
    viewModel: AppViewModel,
    lastLines: List<Line>,
    isDeleteState: Boolean,
    selectedForDeletion: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val line = if (lastLines.isNotEmpty()) {
        lastLines.firstOrNull { it.chatId == chat.id }
    } else {
        Line(
            chatId = chat.id,
            lastModified = Calendar.getInstance().timeInMillis / 1000,
            sender = 0,
            image = "",
            line = ""
        )
    }
    val backgroundColor = if (selectedForDeletion && isDarkTheme) BarsBackgroundColor
    else if (!selectedForDeletion && isDarkTheme) BackgroundColor
    else if (selectedForDeletion && !isDarkTheme) BarsBackgroundColorLight
    else BackgroundColorLight

    Row {
        Column(
            modifier = Modifier
                .combinedClickable (
                    onClick = {
                        if (isDeleteState) {
                            if (!selectedForDeletion) {
                                viewModel.addChatToDeletion(chat.id)
                            } else {
                                viewModel.removeChatFromDeletion(chat.id)
                            }
                        } else {
                            onChatClicked(chat.id)
                        }
                    },
                    onLongClick = {
                        if (!isDeleteState) {
                            viewModel.toggleDeleteState()
                            viewModel.addChatToDeletion(chat.id)
                        }
                    },
                )
                .background(backgroundColor)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = viewModel.parseDateTime(chat.lastModified),
                    fontFamily = openSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Right,
                    color = if (isDarkTheme) SmallTextColor else SmallTextColorLight,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.weight(1f))
            }
            Text(
                text = line!!.line,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                fontFamily = openSansFont,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                fontSize = 18.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            HorizontalDivider(thickness = 1.dp)
        }
    }
}