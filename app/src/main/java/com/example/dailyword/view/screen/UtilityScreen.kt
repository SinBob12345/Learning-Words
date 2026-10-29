package com.example.dailyword.view.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.dailyword.data.model.Meaning
import com.example.dailyword.data.model.Word
import com.example.dailyword.util.TtsHelper
import com.example.dailyword.viewmodel.MainViewModel
import com.example.dailyword.viewmodel.SettingsViewModel
import com.example.dailyword.viewmodel.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun UtilityScreen(
    windowWidth: WindowWidthSizeClass,
    mainViewModel: MainViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val ttsHelper = remember { TtsHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    val uiState by mainViewModel.uiState.collectAsState()
    val selectedLevel by settingsViewModel.selectedLevel.collectAsState()
    val showPhonetic by settingsViewModel.showPhonetic.collectAsState()
    val showDefinition by settingsViewModel.showDefinition.collectAsState()
    val showExample by settingsViewModel.showExample.collectAsState()
    val showImage by settingsViewModel.showImage.collectAsState()
    val autoPlayAudio by settingsViewModel.autoPlayAudio.collectAsState()
    val isPremium by settingsViewModel.isPremium.collectAsState()

    LaunchedEffect(selectedLevel, isPremium) {
        mainViewModel.loadWord(selectedLevel, isPremium = isPremium, forceRefresh = false)
    }

    LaunchedEffect(uiState, autoPlayAudio) {
        if (autoPlayAudio && uiState is UiState.Success) {
            val word = (uiState as UiState.Success).word
            ttsHelper.speak(word.word)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = { mainViewModel.loadWord(selectedLevel, isPremium = isPremium, forceRefresh = true) },
                            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            is UiState.Success -> {
                WordContent(
                    word = state.word,
                    windowWidth = windowWidth,
                    showPhonetic = showPhonetic,
                    showDefinition = showDefinition,
                    showExample = showExample,
                    showImage = showImage,
                    level = selectedLevel,
                    isPremium = isPremium,
                    onSpeak = { text -> ttsHelper.speak(text) }
                )
            }
        }
    }
}

@Composable
private fun WordContent(
    word: Word,
    windowWidth: WindowWidthSizeClass,
    showPhonetic: Boolean,
    showDefinition: Boolean,
    showExample: Boolean,
    showImage: Boolean,
    level: String,
    isPremium: Boolean,
    onSpeak: (String) -> Unit
) {
    if (windowWidth == WindowWidthSizeClass.Expanded) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                WordHeaderCard(
                    word = word,
                    showPhonetic = showPhonetic,
                    showImage = showImage,
                    level = level,
                    isPremium = isPremium,
                    onSpeak = onSpeak
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                MeaningsList(
                    meanings = word.meanings,
                    showDefinition = showDefinition,
                    showExample = showExample
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                WordHeaderCard(
                    word = word,
                    showPhonetic = showPhonetic,
                    showImage = showImage,
                    level = level,
                    isPremium = isPremium,
                    onSpeak = onSpeak
                )
            }
            items(word.meanings) { meaning ->
                MeaningCard(
                    meaning = meaning,
                    showDefinition = showDefinition,
                    showExample = showExample
                )
            }
        }
    }
}

@Composable
private fun WordHeaderCard(
    word: Word,
    showPhonetic: Boolean,
    showImage: Boolean,
    level: String,
    isPremium: Boolean,
    onSpeak: (String) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word.word.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = { onSpeak(word.word) },
                        modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Pronounce word",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("LearningWords", "${word.word} ${word.phonetic ?: ""}")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Copy word",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (showPhonetic && isPremium && !word.phonetic.isNullOrBlank()) {
                Text(
                    text = word.phonetic,
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            if (showImage && !word.imageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(vertical = 4.dp)
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    SubcomposeAsyncImage(
                        model = word.imageUrl,
                        contentDescription = "Illustration for ${word.word}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "💧 ${word.word.replaceFirstChar { it.uppercase() }}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level: ${level.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )

                if (!isPremium) {
                    Text(
                        text = "Free Tier (Basic Definitions)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun MeaningsList(
    meanings: List<Meaning>,
    showDefinition: Boolean,
    showExample: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(meanings) { meaning ->
            MeaningCard(
                meaning = meaning,
                showDefinition = showDefinition,
                showExample = showExample
            )
        }
    }
}

@Composable
private fun MeaningCard(
    meaning: Meaning,
    showDefinition: Boolean,
    showExample: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = meaning.partOfSpeech.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            meaning.definitions.forEachIndexed { index, def ->
                Column(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (showDefinition) {
                        Text(
                            text = "${index + 1}. ${def.definition}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    if (showExample && !def.example.isNullOrBlank()) {
                        Text(
                            text = "Example: \"${def.example}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
