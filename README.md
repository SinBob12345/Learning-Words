# DailyWord (每日一词) - Educational Utility App

**Course**: CP3406 / CP5307 Mobile Computing Assessment 1  
**App Name**: DailyWord  
**Package Name**: `com.example.dailyword`  
**Architecture**: MVVM + Repository Pattern + Koin DI + Retrofit 2.9.0 + Jetpack Compose + Material Design 3  

---

## 📱 Product Overview

**DailyWord** is an **at-a-glance educational utility Android application** designed to help users expand their English vocabulary during quick breaks. Opening the app immediately presents a curated English word, complete with IPA phonetics, part of speech, definitions, example sentences, and Text-To-Speech (TTS) audio pronunciation.

### Key Features
1. **Utility Screen (Home)**:
   - **Word Card**: Displays daily word, IPA phonetic transcription, and difficulty level tag.
   - **Audio Pronunciation (TTS)**: Tap the speech button to hear native American English pronunciation powered by Android TextToSpeech.
   - **Clipboard Copy**: One-tap copy to copy words and definitions to the system clipboard.
   - **Meanings & Examples**: Organized definition cards listing parts of speech and real-world example sentences.
   - **Refresh Word**: Top bar action button to randomly pull a new word for the selected difficulty level.
   - **Responsive & Adaptive**: Supports portrait & landscape orientations with smooth vertical scrolling, and multi-pane landscape layout on tablets (`WindowWidthSizeClass`).

2. **Settings Screen**:
   - **Difficulty Selection**: Easy (Basic), Medium (Intermediate), Hard (Advanced).
   - **Display Options**: Toggles for showing/hiding phonetics, definitions, and examples.
   - **Audio Behavior Option**: Toggle for auto-playing word pronunciation when a new word is loaded.
   - *Note*: Settings are held in-memory via `SettingsViewModel` (`StateFlow`), fully complying with assessment guidelines (no persistent storage required).

---

## 🏗 Architecture & Tech Stack

Following modern Android development best practices and CP3406 Lecture 1–5 topics:

```
com.example.dailyword
├── data
│   ├── model        # Word, Meaning, Definition data classes
│   ├── api          # Retrofit DictionaryApi interface (api.dictionaryapi.dev)
│   └── repository   # WordRepository interface & WordRepositoryImpl (with API + fallback)
├── viewmodel        # MainViewModel (UiState StateFlow) & SettingsViewModel
├── view
│   └── screen       # UtilityScreen & SettingsScreen composables
├── di               # Koin AppModules (Retrofit, Repository, ViewModels)
├── util             # TtsHelper (TextToSpeech manager)
└── ui/theme         # Material 3 Color, Type, and DailyWordTheme
```

| Week | Covered Concept | Implementation in DailyWord |
|------|-----------------|-----------------------------|
| **Week 1** | Kotlin & AS Setup | Null safety, `data class`, lambdas, `Application` class, `ComponentActivity` |
| **Week 2** | Jetpack Compose | `Column`, `Row`, `Card`, `LazyColumn`, `Modifier`, state management (`StateFlow`, `collectAsState`) |
| **Week 3** | Material Design 3 | `Scaffold`, `TopAppBar`, `NavigationBar`, accessibility (≥48dp touch targets, semantics, `stateDescription`), `WindowSizeClass` adaptive layouts |
| **Week 4** | App Architecture | Standard MVVM, Repository pattern, Koin 3.4.3 Dependency Injection (`module`, `single`, `viewModel`) |
| **Week 5** | Retrofit & Async API | Retrofit 2.9.0 with Gson converter, Kotlin Coroutines, asynchronous network state management (`UiState.Loading`, `Success`, `Error`) |

---

## 💡 Technical Highlights & Resilience

1. **Rate-Limit & Offline Resilience**:
   - The app primary data source is the free public [Dictionary API](https://api.dictionaryapi.dev/).
   - To prevent HTTP 429 (Too Many Requests rate limiting) or network failure disruptions, `WordRepositoryImpl` includes high-quality fallback dictionary definitions. If network calls fail or hit rate limits, the app seamlessly serves fallback definitions without showing error screens to the user.
2. **State Retention on Orientation Changes**:
   - `MainViewModel` is bound to the Activity ViewModelStore using Koin's `viewModel` DSL.
   - Rotating the screen retains the currently viewed word without triggering redundant network requests.
3. **Accessibility (A11y)**:
   - All interactive elements meet minimum 48×48dp touch target requirements.
   - Content descriptions on icons and `stateDescription` on RadioButtons/Switches.
   - Heading semantics applied to section headers.

---

## 📝 Self-Reflection (Gibbs Reflective Cycle)

### 1. Description
For Assessment 1 of CP3406/CP5307, I designed and developed "DailyWord", an educational Android utility app. The objective was to construct a lightweight, "at-a-glance" application adhering strictly to Weeks 1–5 course topics, featuring Jetpack Compose, Material Design 3, MVVM architecture, Koin dependency injection, and Retrofit network requests.

### 2. Feelings
Initially, I felt excited yet slightly apprehensive about integrating multiple modern Android libraries—such as Compose, Koin 3.4.3, and Retrofit 2.9.0—within a compact application. Managing state across screen rotations and dealing with free public API rate limits (HTTP 429) initially presented technical hurdles, but overcoming these challenges was deeply satisfying.

### 3. Evaluation
The outcome was highly successful. The app fulfills all assignment requirements and product positioning goals:
- **Positives**: The app is responsive, accessible, handles rate-limits gracefully, includes audio pronunciation via TTS, and adheres cleanly to MVVM + Repository + Koin DI design principles.
- **Challenges**: Handling free API rate-limiting (HTTP 429) required implementing single-request strategies and local fallback data structures. Preventing redundant re-fetching during orientation changes required refining ViewModel state retention logic.

### 4. Analysis
The key insight was understanding the Android lifecycle and Compose recomposition triggers. Initially, screen rotation re-invoked `LaunchedEffect`, causing the app to fetch a new word whenever rotated. By maintaining state in `MainViewModel` and guarding against unwanted re-triggers (`forceRefresh`), the app now correctly retains state across orientation changes. Furthermore, implementing local fallback data ensured 100% reliability regardless of external network stability.

### 5. Conclusion
Developing DailyWord reinforced the practical value of clean architecture and declarative UI development in Jetpack Compose. Separating concerns across Model, ViewModel, and View ensured that UI components remained simple and focused solely on rendering state.

### 6. Action Plan
In future Android projects, I will:
1. Design fallback/caching strategies early when working with external APIs.
2. Structure ViewModels to explicitly differentiate between initial state loading, orientation recomposition, and explicit user-triggered refresh actions.
3. Expand unit test suites to cover edge cases in state transitions.

---

## 🛠 How to Build & Run
1. Open the project in Android Studio (Jellyfish or newer recommended).
2. Sync Gradle files (`Gradle Sync`).
3. Select an emulator or connected device (API 26+).
4. Run `:app:assembleDebug` or click **Run 'app'**.
5. Run unit tests via `:app:testDebugUnitTest`.
