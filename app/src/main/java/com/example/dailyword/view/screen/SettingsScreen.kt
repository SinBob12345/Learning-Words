package com.example.dailyword.view.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.dailyword.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    windowWidth: WindowWidthSizeClass,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val selectedLevel by settingsViewModel.selectedLevel.collectAsState()
    val showPhonetic by settingsViewModel.showPhonetic.collectAsState()
    val showDefinition by settingsViewModel.showDefinition.collectAsState()
    val showExample by settingsViewModel.showExample.collectAsState()
    val showImage by settingsViewModel.showImage.collectAsState()
    val autoPlayAudio by settingsViewModel.autoPlayAudio.collectAsState()
    val isPremium by settingsViewModel.isPremium.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() }
        )

        // Section 0: Premium Status / Upgrade Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isPremium) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isPremium) "Premium Member" else "Free Plan",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isPremium) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics { heading() }
                    )

                    if (isPremium) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = Color(0xFFFFD700), // Gold badge
                            contentColor = Color.Black
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "PRO",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }

                Text(
                    text = if (isPremium)
                        "Unlimited refreshes, full definitions, phonetics, dark mode & hard vocabulary unlocked."
                    else
                        "Basic features: Unlimited Easy refreshes, 5 Medium refreshes, 1 definition per meaning. Upgrade for full access!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isPremium) MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!isPremium) {
                    Button(
                        onClick = { settingsViewModel.upgradeToPremium() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Upgrade to Premium")
                    }
                }
            }
        }

        // Section 1: Difficulty Level
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Difficulty Level",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                ) {
                    val levels = listOf(
                        Triple("easy", "Easy (Basic Vocabulary)", true),
                        Triple("medium", "Medium (Intermediate Vocabulary)", true),
                        Triple("hard", "Hard (Advanced Vocabulary)", isPremium)
                    )

                    levels.forEach { (levelKey, label, isAvailable) ->
                        val isSelected = selectedLevel == levelKey
                        val textColor = if (isAvailable) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .clickable(enabled = isAvailable) { settingsViewModel.setLevel(levelKey) }
                                .padding(vertical = 4.dp)
                                .semantics {
                                    stateDescription = if (isSelected) "Selected" else if (!isAvailable) "Locked" else "Not selected"
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { if (isAvailable) settingsViewModel.setLevel(levelKey) },
                                enabled = isAvailable,
                                modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )
                            if (!isAvailable) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked feature",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Display & Theme Options
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Display & Theme Options",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingSwitchRow(
                    label = if (isPremium) "Show Phonetics" else "Show Phonetics (Premium)",
                    checked = showPhonetic && isPremium,
                    enabled = isPremium,
                    showLock = !isPremium,
                    onCheckedChange = { settingsViewModel.togglePhonetic() }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingSwitchRow(
                    label = "Show Definitions",
                    checked = showDefinition,
                    enabled = true,
                    onCheckedChange = { settingsViewModel.toggleDefinition() }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingSwitchRow(
                    label = "Show Examples",
                    checked = showExample,
                    enabled = true,
                    onCheckedChange = { settingsViewModel.toggleExample() }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingSwitchRow(
                    label = "Show Word Images",
                    checked = showImage,
                    enabled = true,
                    onCheckedChange = { settingsViewModel.toggleImage() }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingSwitchRow(
                    label = "Auto-play Pronunciation",
                    checked = autoPlayAudio,
                    enabled = true,
                    onCheckedChange = { settingsViewModel.toggleAutoPlayAudio() }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingSwitchRow(
                    label = if (isPremium) "Dark Mode" else "Dark Mode (Premium)",
                    checked = isDarkMode,
                    enabled = isPremium,
                    showLock = !isPremium,
                    onCheckedChange = { settingsViewModel.toggleDarkMode() }
                )
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    enabled: Boolean,
    showLock: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
            .semantics {
                stateDescription = if (checked) "Enabled" else "Disabled"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            if (showLock) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked feature",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        )
    }
}
