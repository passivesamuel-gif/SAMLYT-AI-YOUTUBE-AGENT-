# SAMLYT AI — Cinematic YouTube Production Studio

**SAMLYT AI** is a specialized Android production studio app designed for creators, video essayists, and documentarians. Built with modern Kotlin, Jetpack Compose, Room database, and Google's Gemini API.

---

## 🎬 Core Features

- **Channel Reverse-Engineering & Research**: Deconstruct viral channels (e.g., Johnny Harris, Vox, MrBeast, ColdFusion) into actionable 7-part system blueprints (Archetype, Signature Hook, Cut Cadence, Narrative Framework, Visual Optics, Sound Design, Repetitive Workflow).
- **CineScript Studio**: Write and refine multi-act chronological video scripts with exact timestamps, B-roll framing, camera movement cues, and psychological retention anchors.
- **Storyboard Engine**: Generate photorealistic camera shot lists specifying focal length (e.g., 35mm Anamorphic), camera motion (dolly, whip pan), and optical notes.
- **Thumbnail Studio**: Generate high-CTR thumbnail prompts, title pairings, and visual concept generation via Gemini and Imagen.
- **Voice Director (Sam Skytube)**: Real-time directing loop with speech recognition (`SpeechRecognizer`) and cinematic female voice direction (`TextToSpeech`).
- **Offline-First Room Persistence**: All projects, blueprints, scripts, storyboards, and thumbnail drafts are saved locally in SQLite Room DB.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Ladybug (2024.2.1+) or newer
- **JDK 17+**
- **Android SDK Platform 36** (minSdk 24, Android 7.0+)

### Building and Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/<your-username>/samlyt-ai.git
   cd samlyt-ai
   ```

2. **Open in Android Studio:**
   - Select **Open an Existing Project** and choose the `samlyt-ai` folder.
   - Let Gradle sync automatically.

3. **Configure Gemini API Key (Optional):**
   You can configure your Gemini API key in either of two ways:
   - **Option A (In-App)**: Run the app, navigate to **Settings**, paste your Gemini API key, and tap **Save Key & Test Connection**.
   - **Option B (.env file)**: Create a `.env` file in the project root:
     ```env
     GEMINI_API_KEY=AIzaSy...your_actual_api_key_here
     ```
     The Gradle Secrets plugin will automatically inject this into `BuildConfig.GEMINI_API_KEY`.
   *(Note: If no API key is provided, the app will continue to function in intelligent offline blueprint mode with fallback responses.)*

4. **Run the App:**
   - Select an Android device or emulator running Android 7.0+ (API 24+).
   - Press **Run (Shift + F10)**.

5. **Run Unit Tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 🛠 Tech Stack

- **UI**: Jetpack Compose, Material 3, Compose Navigation
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **Persistence**: Room Database (SQLite) + Encrypted SharedPreferences
- **Networking**: Retrofit 2, OkHttp 3, Google Gemini REST API (`gemini-3.5-flash`, `gemini-3.1-pro-preview`)
- **Speech**: Android SpeechRecognizer (Speech-to-Text) + TextToSpeech (Audio Director)
- **Testing**: JUnit 4, Robolectric, Roborazzi
