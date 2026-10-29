# Learning Words - Educational Utility App

**Course**: CP3406 / CP5307 Mobile Computing Assessment 1  
**App Name**: Learning Words  
**Package Name**: `com.example.dailyword`  
**Architecture**: MVVM + Repository Pattern + Koin DI + Retrofit 2.9.0 + Coil 2.6.0 + Jetpack Compose + Material Design 3  

---

## 📱 Product Overview

**Learning Words** is an **at-a-glance educational utility Android application** designed to help users expand their English vocabulary during quick breaks. Opening the app immediately presents a curated English word, complete with IPA phonetics, part of speech, definitions, example sentences, visual illustration images, and Text-To-Speech (TTS) audio pronunciation.

### Key Features
1. **Utility Screen (Home)**:
   - **Word Card**: Displays word, IPA phonetic transcription, image illustration, and difficulty level tag.
   - **Visual Illustration**: Displays curated high-definition images loaded via Coil (`SubcomposeAsyncImage`), featuring progress loading indicators and automatic fallback UI.
   - **Sequential Word Refresh**: Top bar action button cycles through vocabulary sequentially in a loop within each difficulty level (`apple` → `happy` → `water` → `music` → `smile`...), rather than random selection.
   - **Audio Pronunciation (TTS)**: Tap the speech button to hear native American English pronunciation powered by Android TextToSpeech.
   - **Clipboard Copy**: One-tap copy to copy words and phonetic transcriptions to the system clipboard.
   - **Meanings & Examples**: Organized definition cards listing parts of speech and real-world example sentences.
   - **Responsive & Adaptive**: Supports portrait & landscape orientations with smooth vertical scrolling, and multi-pane landscape layout on tablets (`WindowWidthSizeClass`).

2. **Settings Screen**:
   - **Difficulty Selection**: Easy (Basic), Medium (Intermediate), Hard (Advanced).
   - **Tier Rules & Refresh Limits**:
     - **Easy Level**: Unlimited refreshes for all users (both Free and Premium).
     - **Medium Level**: 5 free refreshes per session for Free Plan users, unlimited for Premium members.
     - **Hard Level**: Exclusive to Premium members.
   - **Display Options**: Toggles for showing/hiding phonetics, definitions, example sentences, and word illustration images.
   - **Audio Behavior Option**: Toggle for auto-playing word pronunciation when a new word is loaded.
   - **Theme Option**: Dark Mode toggle exclusively for Premium members.
   - *Note*: Preferences are managed via `SettingsViewModel` (`StateFlow`), fully complying with assessment guidelines.

---

## 🏗 Architecture & Tech Stack

Following modern Android development best practices and CP3406 Lecture 1–5 topics:

```
com.example.dailyword
├── data
│   ├── model        # Word, Meaning, Definition data classes
│   ├── api          # Retrofit DictionaryApi interface (api.dictionaryapi.dev)
│   └── repository   # WordRepository interface & WordRepositoryImpl (Sequential lookup + fallback)
├── viewmodel        # MainViewModel (UiState StateFlow) & SettingsViewModel
├── view
│   └── screen       # UtilityScreen & SettingsScreen composables
├── di               # Koin AppModules (Retrofit, Repository, ViewModels)
├── util             # TtsHelper (TextToSpeech manager)
└── ui/theme         # Material 3 Color, Type, and DailyWordTheme
```

| Week | Covered Concept | Implementation in Learning Words |
|------|-----------------|----------------------------------|
| **Week 1** | Kotlin & AS Setup | Null safety, `data class`, lambdas, `Application` class, `ComponentActivity` |
| **Week 2** | Jetpack Compose | `Column`, `Row`, `Card`, `LazyColumn`, `Modifier`, state management (`StateFlow`, `collectAsState`) |
| **Week 3** | Material Design 3 | `Scaffold`, `TopAppBar`, `NavigationBar`, accessibility (≥48dp touch targets, semantics, `stateDescription`), `WindowSizeClass` adaptive layouts |
| **Week 4** | App Architecture | Standard MVVM, Repository pattern, Koin 3.4.3 Dependency Injection (`module`, `single`, `viewModel`) |
| **Week 5** | Network & Async Data | Retrofit 2.9.0 with Gson converter, Coil 2.6.0 image loading (`SubcomposeAsyncImage`), Coroutines, asynchronous state management (`UiState.Loading`, `Success`, `Error`) |

---

## 💡 Technical Highlights & Resilience

1. **Sequential Loop Refreshing & Offline Resilience**:
   - The app cycles through vocabulary in a thread-safe, sequential loop using `AtomicInteger` indices per difficulty tier.
   - `WordRepositoryImpl` includes robust dictionary definitions and curated image mappings. If network calls encounter HTTP 404 or connection failures, the app seamlessly serves complete local fallback data.
2. **State Retention on Orientation Changes**:
   - `MainViewModel` is bound to the Activity ViewModelStore using Koin's `viewModel` DSL.
   - Rotating the screen retains the currently viewed word and refresh count without triggering redundant network requests.
3. **Accessibility (A11y)**:
   - All interactive elements meet minimum 48×48dp touch target requirements.
   - Content descriptions on icons and `stateDescription` on RadioButtons/Switches.
   - Heading semantics applied to section headers.

---

## 📝 Self-Reflection (Gibbs Reflective Cycle)

### 1. Description
This assignment required developing a utility Android app called Learning Words to cover core knowledge from Weeks 1–5 of the course: Kotlin syntax, Jetpack Compose layouts, Material Design 3, MVVM architecture with the Repository pattern, Koin dependency injection, and Retrofit network requests. I built the core functions following the course’s Pets example architecture, including word display on the main screen, difficulty and display settings, responsive layout for large screens, and accessibility features. During testing, three key issues arose. First, refreshing words sometimes triggered a 404 error and crashed the page. Second, switching between portrait and landscape orientation reloaded the current word automatically and lost the content being viewed. Third, the definition list could not scroll in landscape split view, even though it worked normally in portrait mode. I then debugged each issue by reviewing course concepts and fixed all of them.

### 2. Feelings
At the beginning, building the MVVM structure went smoothly, and I gradually understood the value of layered decoupling. However, I felt confused and frustrated when the three bugs appeared one after another, especially the orientation reload issue, which made me doubt my understanding of the ViewModel lifecycle. After troubleshooting step by step and resolving all issues, I gained a strong sense of achievement. More importantly, I moved beyond memorising theoretical knowledge and developed a practical understanding of Android lifecycle and UI rendering mechanisms.

### 3. Evaluation

**Strengths**  
First, the project strictly follows the required architectural standards: a standard three-layer MVVM structure, Repository pattern for data abstraction, and Koin for dependency injection, with clear responsibilities across layers and consistent code style with course examples. Second, all required technical points are covered, including Material Design 3 components, WindowSizeClass responsive layout, and accessibility optimisation. Third, the layered architecture made troubleshooting efficient: network issues only required changes to the Repository layer, and layout issues only affected the View layer.

**Weaknesses**  
First, exception handling in the network layer was incomplete, with no targeted handling for HTTP errors or empty responses. Second, I failed to fully utilise the ViewModel’s ability to preserve state across configuration changes, causing redundant requests on orientation change. Third, I only verified visual effects of responsive layouts but ignored interactive usability in landscape mode. Fourth, boundary testing was insufficient; I did not test edge cases such as invalid data or configuration changes beforehand.

### 4. Analysis
I identified the root causes of the three issues:
- **404 error on refresh**: The public dictionary API does not cover all words and returns 404 for some entries. The Repository layer only caught general network exceptions without handling HTTP status errors or providing fallback data, so exceptions propagated directly to the UI and caused crashes.
- **Automatic reload on orientation change**: I used `LaunchedEffect(level)` to trigger word loading. Orientation change triggers activity recreation and Composable recomposition, which re-runs `LaunchedEffect` and reloads data. This happened because I did not store loaded data in the ViewModel to persist across configuration changes.
- **Non-scrolling content in landscape**: In the expanded split layout, the Box wrapping the right-side `LazyColumn` lacked proper height constraints. The `LazyColumn` content extended beyond the visible area but had no defined scrollable boundary, while portrait mode had complete `Column` constraints so scrolling worked normally.

### 5. Conclusion
Through this assignment, I not only practised core technologies such as MVVM architecture, dependency injection and Compose declarative UI, but also gained practical insights into Android development. First, the core value of ViewModel lies in both layered decoupling and state retention across configuration changes, which is essential for adapting to screen rotation and foldable devices. Second, robust network layers require complete exception handling and fallback mechanisms, rather than assuming all API calls succeed. Third, responsive design must cover both visual adaptation and consistent interaction across screen sizes. I also realised that clear layered architecture significantly reduces debugging cost, which is why app architecture is emphasised in the course.

### 6. Action Plan
For future projects, I will make the following improvements:
1. Design exception handling strategies at the early development stage. The Repository layer will centrally handle HTTP errors, network failures and empty data, and provide fallback results to prevent UI crashes.
2. Manage all business data and states in ViewModel, and let Composables only observe and render states, to avoid redundant requests during recomposition.
3. Test interaction behaviour across orientations and screen sizes when building responsive layouts, not just visual effects.
4. Include boundary testing for edge cases such as invalid data and configuration changes to identify potential issues early.

### 7. Declaration of AI-Generated Material
I declare that generative AI was used in this assignment. Tool: ChatGPT / AI Assistants. Purpose: (1) explaining concepts and troubleshooting errors; (2) correcting code that produced errors into the proper format so that it runs correctly; (3) polishing the wording of this self-reflection. I reviewed, tested and edited all AI-assisted output, and the experiences and reflections described in this document are my own. I take full responsibility for the submitted work and can provide the relevant AI chat records on request.

---

## 🛠 How to Build & Run
1. Open the project in Android Studio (Jellyfish or newer recommended).
2. Sync Gradle files (`Gradle Sync`).
3. Select an emulator or connected device (API 26+).
4. Run `:app:assembleDebug` or click **Run 'app'**.
5. Run unit tests via `:app:testDebugUnitTest`.
