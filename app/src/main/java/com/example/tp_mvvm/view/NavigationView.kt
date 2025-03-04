package com.example.tp_mvvm.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tp_mvvm.R
import com.example.tp_mvvm.view.theme.DarkColorScheme
import com.example.tp_mvvm.view.theme.DimIconColor
import com.example.tp_mvvm.view.theme.ItemBackgroundColor
import com.example.tp_mvvm.view.theme.ItemBackgroundColorLight
import com.example.tp_mvvm.view.theme.LightColorScheme
import com.example.tp_mvvm.view.theme.TextColor
import com.example.tp_mvvm.view.theme.TextColorLight
import com.example.tp_mvvm.view.theme.TitlesAndIconsColor
import com.example.tp_mvvm.view.theme.TitlesAndIconsColorLight
import com.example.tp_mvvm.view.theme.TopBarDividerColor
import com.example.tp_mvvm.view.theme.TopBarDividerColorLight
import com.example.tp_mvvm.view.theme.Typography
import com.example.tp_mvvm.view.util.getBottomLineShape
import com.example.tp_mvvm.viewmodel.AppUiState
import com.example.tp_mvvm.viewmodel.AppViewModel
import com.example.tp_mvvm.viewmodel.AppViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class MedView(@StringRes val title: Int) {
    MainMenu(R.string.main_menu_screen),
    Chat(R.string.chat_screen),
    Settings(R.string.settings_screen),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedAppBar(
    canNavigateBack: Boolean,
    navController: NavHostController,
    navigateUp: () -> Unit,
    viewModel: AppViewModel,
    isSearchPressed: Boolean,
    isDeleteState: Boolean,
    isDarkTheme: Boolean,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("Routing", navController.currentDestination?.route ?: "")
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    TopAppBar(
        title = {},
        navigationIcon =  {
            if (canNavigateBack) {
                IconButton(onClick = {
                    navigateUp()
                    viewModel.toggleBottomBar()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back button",
                        tint = if (isDarkTheme) TitlesAndIconsColor
                        else TitlesAndIconsColorLight,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else if (isSearchPressed) {
                IconButton(onClick = {
                    viewModel.toggleSearchState()
                    viewModel.setAllChatsBack()
                    searchText = ""
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "End search button",
                        tint = if (isDarkTheme) TitlesAndIconsColor
                        else TitlesAndIconsColorLight,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else if (isDeleteState) {
                IconButton(onClick = {
                    viewModel.clearDeletionList()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Cancel deletion button",
                        tint = if (isDarkTheme) TitlesAndIconsColor
                        else TitlesAndIconsColorLight,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                IconButton(onClick = {
                    navigateToSettings()
                    viewModel.toggleBottomBar()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings button",
                        tint = if (isDarkTheme) TitlesAndIconsColor
                        else TitlesAndIconsColorLight,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        actions = {
            if (!canNavigateBack) {
                if (isSearchPressed) {
                    TextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            coroutineScope.launch {
                                viewModel.updateCurrentChatsOnQuery(it)
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = if (isDarkTheme) ItemBackgroundColor
                            else ItemBackgroundColorLight,
                            focusedContainerColor = if (isDarkTheme) ItemBackgroundColor
                            else ItemBackgroundColorLight,
                            focusedIndicatorColor =  Color.Transparent,
                            unfocusedIndicatorColor = if (isDarkTheme) ItemBackgroundColor
                            else ItemBackgroundColorLight,
                            cursorColor = if (isDarkTheme) TextColor else TextColorLight,
                            focusedTextColor = if (isDarkTheme) TextColor else TextColorLight,
                            unfocusedTextColor = if (isDarkTheme) TextColor else TextColorLight,
                        ),
                        placeholder = {
                            Text(
                                "Type here...",
                                color = if (isDarkTheme) DimIconColor else TextColorLight,
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Italic
                            )
                        },
                        textStyle = TextStyle.Default.copy(fontSize = 18.sp)
                    )
                } else if (isDeleteState) {
                    IconButton(onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            viewModel.deleteSelectedChats()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete selected chats button",
                            tint = if (isDarkTheme) TitlesAndIconsColor
                            else TitlesAndIconsColorLight,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    IconButton(onClick = {
                        viewModel.toggleSearchState()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search chat button",
                            tint = if (isDarkTheme) TitlesAndIconsColor
                            else TitlesAndIconsColorLight,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier
            .padding(bottom = 4.dp)
            .border(
                width = 2.dp,
                color = if (isDarkTheme) TopBarDividerColor
                else TopBarDividerColorLight,
                shape = getBottomLineShape(lineThicknessDp = 1.dp)
            ),
    )
    HorizontalDivider(thickness = 2.dp)
}

@Composable
fun MedApp(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme by uiState.isDarkTheme.collectAsState(initial = isSystemInDarkTheme())

    androidx.compose.material3.MaterialTheme(
        colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography
    ) {
        MedAppNavigation(
            navController = navController,
            viewModel = viewModel,
            uiState = uiState,
            isDarkTheme = isDarkTheme,
        )
    }
}

//@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MedAppNavigation(
    navController: NavHostController,
    viewModel: AppViewModel,
    uiState: AppUiState,
    isDarkTheme: Boolean,
) {
    //val coroutineScope = rememberCoroutineScope()
    //coroutineScope.launch { viewModel.clearDatabase() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateBack = when (navBackStackEntry?.destination?.route) {
        MedView.MainMenu.name -> false
        null -> false
        else -> true
    }

    Scaffold(
        topBar = {
            MedAppBar(
                canNavigateBack = canNavigateBack,
                navigateUp = { navController.navigateUp() },
                viewModel = viewModel,
                isSearchPressed = uiState.isSearchPressed,
                isDeleteState = uiState.isDeleteState,
                navController = navController,
                navigateToSettings = { navController.navigate(MedView.Settings.name) },
                isDarkTheme = isDarkTheme,)
        },
        bottomBar = if (!uiState.isBottomBarHidden) {
            {}
        } else { {} },
    ) { innerPadding ->
        RootNav(
            navController = navController,
            contentPadding = innerPadding,
            viewModel = viewModel,
            isDarkTheme = isDarkTheme
        )
    }
}

@Composable
fun RootNav(
    navController: NavHostController,
    contentPadding: PaddingValues,
    isDarkTheme: Boolean,
    viewModel: AppViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = MedView.MainMenu.name
    ) {
        composable(route = MedView.MainMenu.name) {
            MainMenuScreen(
                contentPadding = contentPadding,
                viewModel = viewModel,
                onChatClicked = { chatId: Long ->
                    viewModel.updateSelectedChatId(chatId)
                    viewModel.toggleBottomBar()
                    navController.navigate(
                        (MedView.Chat.name + "/${chatId}")
                    )
                },
                isDarkTheme = isDarkTheme,
            )
        }

        composable(
            route = MedView.Chat.name + "/{chatId}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.LongType }
            )
        ) {
            ChatScreen(
                contentPadding = contentPadding,
                viewModel = viewModel,
                isDarkTheme = isDarkTheme
            )
        }

        composable(route = MedView.Settings.name) {
            SettingsScreen(
                contentPadding = contentPadding,
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
            )
        }
    }
}