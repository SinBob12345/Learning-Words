package com.example.dailyword

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dailyword.ui.theme.DailyWordTheme
import com.example.dailyword.view.screen.SettingsScreen
import com.example.dailyword.view.screen.UtilityScreen
import com.example.dailyword.viewmodel.MainViewModel
import com.example.dailyword.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val settingsViewModel: SettingsViewModel = koinViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            DailyWordTheme(darkTheme = isDarkMode) {
                MainApp(
                    windowWidth = windowSizeClass.widthSizeClass,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    windowWidth: WindowWidthSizeClass,
    mainViewModel: MainViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val selectedLevel by settingsViewModel.selectedLevel.collectAsState()
    val isPremium by settingsViewModel.isPremium.collectAsState()
    val refreshCount by mainViewModel.refreshCount.collectAsState()

    val canRefresh = mainViewModel.canRefresh(isPremium, selectedLevel)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = if (selectedTab == 0) "Learning Words" else "Settings")

                        // TopAppBar Status Badge (Free or Premium)
                        if (isPremium) {
                            Surface(
                                shape = MaterialTheme.shapes.extraSmall,
                                color = Color(0xFFFFD700), // Gold badge
                                contentColor = Color.Black
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Premium",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = MaterialTheme.shapes.extraSmall,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                val refreshLabel = if (selectedLevel.equals("easy", ignoreCase = true)) {
                                    "Free (Unlimited)"
                                } else {
                                    "Free (${(5 - refreshCount).coerceAtLeast(0)}/5)"
                                }
                                Text(
                                    text = refreshLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (selectedTab == 0) {
                        IconButton(
                            onClick = {
                                if (canRefresh) {
                                    mainViewModel.loadWord(selectedLevel, isPremium = isPremium, forceRefresh = true)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Daily refresh limit reached (5/5). Upgrade to Premium in Settings for unlimited refreshes!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh word",
                                tint = if (canRefresh) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> UtilityScreen(
                    windowWidth = windowWidth,
                    mainViewModel = mainViewModel,
                    settingsViewModel = settingsViewModel
                )
                1 -> SettingsScreen(
                    windowWidth = windowWidth,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
