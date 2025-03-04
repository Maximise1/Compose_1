package com.example.tp_mvvm.view

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.tp_mvvm.model.data.Line
import com.example.tp_mvvm.view.theme.DimIconColor
import com.example.tp_mvvm.view.theme.ItemBackgroundColor
import com.example.tp_mvvm.view.theme.ItemBackgroundColorLight
import com.example.tp_mvvm.view.theme.ModelMessageBackgroundColor
import com.example.tp_mvvm.view.theme.ModelMessageBackgroundColorLight
import com.example.tp_mvvm.view.theme.TextColor
import com.example.tp_mvvm.view.theme.TextColorLight
import com.example.tp_mvvm.view.theme.TitlesAndIconsColor
import com.example.tp_mvvm.view.theme.TitlesAndIconsColorLight
import com.example.tp_mvvm.view.theme.UserMessageBackgroundColor
import com.example.tp_mvvm.view.theme.UserMessageBackgroundColorLight
import com.example.tp_mvvm.view.theme.openSansFont
import com.example.tp_mvvm.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    contentPadding: PaddingValues,
    viewModel: AppViewModel,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    var chatId = uiState.selectedChatId
    val lines by viewModel.getAllChatLines(
        chatId
    ).collectAsState(emptyList())
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current
    val contentResolver: ContentResolver = context.contentResolver

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        imageUri = uri
        if (imageUri != null) {
            contentResolver.takePersistableUriPermission(
                imageUri!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    val chatPadding = if (imageUri != null) 136.dp else 36.dp

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.Bottom,
            reverseLayout = true,
            modifier = modifier
                .fillMaxHeight()
                .padding(bottom = chatPadding)
        ) {
            items(ArrayList(lines).reversed()) { line ->
                LineItem(
                    line = line,
                    isDarkTheme = isDarkTheme,
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(
                    if (isDarkTheme) ItemBackgroundColor else ItemBackgroundColorLight
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = { launcher.launch(arrayOf("image/*")) },
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            if (isDarkTheme) ItemBackgroundColor else ItemBackgroundColorLight
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add image button",
                        tint = if (isDarkTheme) TitlesAndIconsColor else TitlesAndIconsColorLight,
                        modifier = Modifier
                            .size(44.dp)
                    )
                }
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = if (isDarkTheme) ItemBackgroundColor
                        else ItemBackgroundColorLight,
                        focusedContainerColor = if (isDarkTheme) ItemBackgroundColor
                        else ItemBackgroundColorLight,
                        focusedIndicatorColor =  Color.Transparent, //hide the indicator
                        unfocusedIndicatorColor = if (isDarkTheme) ItemBackgroundColor
                        else ItemBackgroundColorLight,
                        cursorColor = if (isDarkTheme) TextColor
                        else TextColorLight,
                        focusedTextColor = if (isDarkTheme) TextColor
                        else TextColorLight,
                        unfocusedTextColor = if (isDarkTheme) TextColor
                        else TextColorLight
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (text != "") {
                                coroutineScope.launch {
                                    if (chatId == -1L) {
                                        chatId = viewModel.createChat()
                                    } else {
                                        viewModel.updateChat(chatId)
                                    }
                                    viewModel.createLine(
                                        chatId = chatId,
                                        sender = 0,
                                        text = text,
                                        image = imageUri?.toString()
                                    )
                                    val image = if (imageUri != null) getImage(imageUri!!, contentResolver)
                                    else null
                                    viewModel.askModel(
                                        question = text,
                                        chatId = chatId,
                                        image = image
                                    )
                                    text = ""
                                    //Toast.makeText(context, "${imageUri}", Toast.LENGTH_SHORT).show()
                                    imageUri = null
                                }
                                keyboardController?.hide()
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    placeholder = {
                        androidx.compose.material.Text(
                            "Can you tell me about...",
                            color = DimIconColor,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic
                        )
                    },
                    textStyle = TextStyle.Default.copy(fontSize = 18.sp),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        if (text != "") {
                            coroutineScope.launch {
                                if (chatId == -1L) {
                                    chatId = viewModel.createChat()
                                } else {
                                    viewModel.updateChat(chatId)
                                }
                                viewModel.createLine(
                                    chatId = chatId,
                                    sender = 0,
                                    text = text,
                                    image = imageUri?.toString()
                                )
                                val image = if (imageUri != null) getImage(imageUri!!, contentResolver)
                                else null
                                viewModel.askModel(
                                    question = text,
                                    chatId = chatId,
                                    image = image
                                )
                                text = ""
                                //Toast.makeText(context, "${imageUri}", Toast.LENGTH_SHORT).show()
                                imageUri = null
                            }
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .background(if (isDarkTheme) ItemBackgroundColor
                        else ItemBackgroundColorLight)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send button",
                        tint = if (isDarkTheme) TitlesAndIconsColor
                        else TitlesAndIconsColorLight,
                        modifier = Modifier
                            .size(44.dp)
                    )
                }
            }
            if (imageUri != null) {
                Row(
                    modifier = Modifier
                        .height(100.dp)
                ) {
                    ImageItem(imageUri = imageUri)
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
fun getImage(imageUri: Uri, content: ContentResolver): Bitmap {
    val bitmap = when {
        Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
            content,
            imageUri
        ).copy(Bitmap.Config.ARGB_8888, true)
        else -> {
            val source = ImageDecoder.createSource(
                content,
                imageUri
            )
            ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
        }
    }
    return resizeImage(bitmap)
}

fun resizeImage(bitmap: Bitmap): Bitmap {
    val aspRat = bitmap.width / bitmap.height
    val w2 = 400
    val h2 = w2 * aspRat
    return Bitmap.createScaledBitmap(bitmap, w2, h2, false)
}

@Composable
fun LineItem(
    line: Line,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val arrangement = if (line.sender == 0) Arrangement.End else Arrangement.Start
    val mod = if (line.sender == 1) Modifier.padding(end = 100.dp) else Modifier
    var color = if (line.sender == 0) UserMessageBackgroundColorLight
    else ModelMessageBackgroundColorLight
    if (isDarkTheme) {
        color = if (line.sender == 0) UserMessageBackgroundColor
        else ModelMessageBackgroundColor
    }
    Row(
        horizontalArrangement = arrangement,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (line.sender == 0) Spacer(Modifier.width(100.dp))
        Box(
            modifier = mod
                .padding(4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color)
                .padding(4.dp)
        ) {
            Column {
                Text(
                    text = line.line,
                    fontFamily = openSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    fontSize = 18.sp,
                    color = if (isDarkTheme) TitlesAndIconsColor
                    else TitlesAndIconsColorLight,
                    lineHeight = 32.sp,
                    letterSpacing = 0.sp,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                )
                if (line.image != "" && line.image != null) {
                    ImageInDialogue(imageUri = line.image)
                }
            }
        }
        if (line.sender == 1) Spacer(Modifier.width(100.dp))
    }
}

@Composable
fun ImageItem(
    imageUri: Uri?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUri,
        contentDescription = "Added image",
        modifier = Modifier
            .padding(4.dp)
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun ImageInDialogue(
    imageUri: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUri,
        contentDescription = "Added image",
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop,
    )
}