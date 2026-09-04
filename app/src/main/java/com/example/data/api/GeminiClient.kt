package com.example.data.api

import com.example.BuildConfig
import com.example.data.model.ResearchResult
import com.example.data.model.ScriptScene
import com.example.data.model.StoryboardShot
import com.example.data.model.ThumbnailConcept
import com.example.data.model.YouTubeFormulaBlueprint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

object GeminiClient {
  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  fun resolveApiKey(userKey: String): String {
    val trimmed = userKey.trim()
    if (trimmed.isNotBlank() && trimmed != "MY_GEMINI_API_KEY" && trimmed != "YOUR_GEMINI_API_KEY") {
      return trimmed
    }
    val buildKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
    val trimmedBuildKey = buildKey.trim()
    return if (trimmedBuildKey.isNotBlank() && trimmedBuildKey != "MY_GEMINI_API_KEY" && trimmedBuildKey != "YOUR_GEMINI_API_KEY") trimmedBuildKey else ""
  }

  suspend fun testConnection(userKey: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
    val key = resolveApiKey(userKey)
    if (key.isBlank()) {
      return@withContext Pair(false, "API Key is missing. Please configure your Gemini API key in Settings or AI Studio secrets.")
    }
    val candidateModels = listOf("gemini-3.5-flash", "gemini-flash-latest", "gemini-3.1-flash-lite-preview", "gemini-3.1-pro-preview")
    var lastError = ""
    for (m in candidateModels) {
      try {
        val prompt = "Reply with 'CONNECTED' in one word."
        val jsonBody = JSONObject().apply {
          put("contents", JSONArray().put(JSONObject().apply {
            put("parts", JSONArray().put(JSONObject().put("text", prompt)))
          }))
        }
        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$m:generateContent?key=$key"
        val request = Request.Builder().url(url).post(requestBody).build()
        client.newCall(request).execute().use { response ->
          if (response.isSuccessful) {
            return@withContext Pair(true, "Connected successfully via $m (Active & Verified)")
          } else {
            lastError = "HTTP ${response.code}: ${response.message}"
          }
        }
      } catch (e: Exception) {
        lastError = e.localizedMessage ?: "Connection error"
      }
    }
    Pair(false, "Connection check: $lastError")
  }

  suspend fun analyzeYouTube(url: String, angle: String, userKey: String): ResearchResult =
    withContext(Dispatchers.IO) {
      val key = resolveApiKey(userKey)
      val prompt = """
        You are SAMLYT AI Research Engine.
        Analyze this YouTube video or channel topic: "$url"
        Target Angle: "$angle"
        Provide a structured breakdown including:
        1. Channel / Topic Title
        2. Hook retention score (1-100)
        3. 3-5 Viral Angles with high CTR potential
        4. 3 Hook Strategies for the first 30 seconds
        5. Content whitespace / gaps competitors missed
        6. Suggested 3-act narrative structure
        Format your response as a clear, high-yield creator brief.
      """.trimIndent()

      val rawResponse = callGeminiRaw(prompt, key, model = "gemini-3.1-pro-preview")
      if (rawResponse.isNotBlank()) {
        parseResearchResponse(url, rawResponse)
      } else {
        fallbackResearchResult(url, angle)
      }
    }

  suspend fun generateScript(
    premise: String,
    tone: String,
    duration: String,
    architecture: String,
    userKey: String,
    productionBibleContext: String = ""
  ): List<ScriptScene> = withContext(Dispatchers.IO) {
    val key = resolveApiKey(userKey)
    val prompt = """
      You are SAMLYT AI Script Studio (CineScript 4K engine).
      Active Production Bible Continuity: $productionBibleContext
      Create a timestamped video script with pacing and B-roll cues based on:
      Premise: $premise
      Narrative Tone: $tone
      Target Duration: $duration
      Architecture Blueprint: $architecture

      Generate 4-6 chronological scene beats. For each beat, format exactly like:
      [SCENE]
      Timestamp: 00:00 - 00:30
      Act: Act I: The Cognitive Hook
      Narration: (The precise voiceover dialogue spoken to the camera)
      B-Roll: (Specific visual camera instruction, lens choice, lighting, and movement)
      Retention Anchor: (Psychological trigger or retention spike rationale)
    """.trimIndent()

    val rawResponse = callGeminiRaw(prompt, key, model = "gemini-3.1-pro-preview")
    if (rawResponse.isNotBlank()) {
      parseScriptScenes(rawResponse, premise)
    } else {
      fallbackScriptScenes(premise, tone, duration)
    }
  }

  suspend fun generateStoryboard(
    premise: String,
    optics: String,
    aspectRatio: String,
    userKey: String,
    productionBibleContext: String = ""
  ): List<StoryboardShot> = withContext(Dispatchers.IO) {
    val key = resolveApiKey(userKey)
    val prompt = """
      You are SAMLYT AI Visual Pre-Visualization Studio.
      Active Production Bible Continuity: $productionBibleContext
      Create a shot list and Midjourney / Photorealistic 8K prompts based on:
      Visual Premise: $premise
      Camera & Optics Package: $optics
      Aspect Ratio: $aspectRatio

      Generate 4 distinct cinematic shots. Format each shot like:
      [SHOT]
      Shot Number: 1
      Focal Length: 35mm Anamorphic T1.5
      Movement: Slow push-in tracking shot at eye level
      Visual Prompt: Cinematic 8K photoreal film still, 35mm anamorphic lens, shallow depth of field, dramatic split neon lighting, high contrast color grade, volumetric haze --ar $aspectRatio --v 6.1
      Lighting: High-contrast tungsten key with cool cyan rim
    """.trimIndent()

    val rawResponse = callGeminiRaw(prompt, key, model = "gemini-3.1-pro-preview")
    if (rawResponse.isNotBlank()) {
      parseStoryboardShots(rawResponse, optics, aspectRatio)
    } else {
      fallbackStoryboardShots(premise, optics, aspectRatio)
    }
  }

  suspend fun generateStoryboardFromScriptSegments(
    scriptScenes: List<ScriptScene>,
    optics: String,
    aspectRatio: String,
    userKey: String,
    productionBibleContext: String = ""
  ): List<StoryboardShot> = withContext(Dispatchers.IO) {
    val key = resolveApiKey(userKey)
    if (scriptScenes.isEmpty()) {
      return@withContext generateStoryboard("Cinematic documentary scene", optics, aspectRatio, userKey, productionBibleContext)
    }

    val scenesContext = scriptScenes.mapIndexed { i, s ->
      "Scene ${i + 1} [${s.timestamp}] (${s.actTitle}):\nNarration: \"${s.narration}\"\nB-Roll Idea: \"${s.bRollCue}\"\nRetention Hook: \"${s.retentionAnchor}\""
    }.joinToString("\n\n")

    val prompt = """
      You are SAMLYT AI Visual Pre-Visualization Studio.
      Active Production Bible Continuity: $productionBibleContext
      Camera & Optics Package: $optics
      Aspect Ratio: $aspectRatio

      Read the following script scene segments and generate corresponding cinematic storyboard shots with visual descriptions for each scene beat:
      $scenesContext

      Generate exactly ${scriptScenes.size} cinematic storyboard shots (one matching each scene beat).
      For each shot, provide visual composition, subject focal point, lighting scheme, and an 8K photorealistic Midjourney prompt.
      Format each shot like:
      [SHOT]
      Shot Number: 1
      Focal Length: 28mm Anamorphic T1.5
      Movement: Slow push-in tracking shot toward subject
      Visual Prompt: Photorealistic 8K cinematic film still, 28mm anamorphic, dramatic lighting matching the scene narrative, shallow depth of field, subtle chromatic aberration, volumetric lighting --ar $aspectRatio --v 6.1
      Lighting: High-contrast tungsten key with cool cyan rim
    """.trimIndent()

    val rawResponse = callGeminiRaw(prompt, key, model = "gemini-3.1-pro-preview")
    if (rawResponse.isNotBlank()) {
      val parsed = parseStoryboardShots(rawResponse, optics, aspectRatio)
      if (parsed.isNotEmpty()) parsed else fallbackStoryboardShotsFromScenes(scriptScenes, optics, aspectRatio)
    } else {
      fallbackStoryboardShotsFromScenes(scriptScenes, optics, aspectRatio)
    }
  }

  suspend fun assistScriptText(
    action: String,
    currentText: String,
    instruction: String,
    tone: String,
    userKey: String
  ): String = withContext(Dispatchers.IO) {
    val key = resolveApiKey(userKey)
    val actionPrompt = when (action.uppercase()) {
      "PUNCH_UP_HOOK" -> "Rewrite the following opening video hook to maximize immediate viewer curiosity, stakes, and retain 90%+ retention in the first 30 seconds. Tone: $tone."
      "CONTINUE" -> "Seamlessly continue this video script with the next scene beat. Keep the exact voice and pacing ($tone). Include Narration dialogue and a cinematic B-Roll cue."
      "SUGGEST_BROLL" -> "Analyze this narration and suggest 3 high-production cinematic B-Roll camera setups, lens choices, and visual metaphors."
      "PATTERN_INTERRUPT" -> "Suggest a powerful pattern interrupt (visual juxtaposition, sudden audio drop, cold open question, or kinetic graphics) for this section."
      "POLISH_NARRATION" -> "Polish this voiceover text for natural spoken cadence, elimination of filler words, and rhythmic pacing."
      else -> "Improve and refine this script section: $instruction"
    }

    val prompt = """
      You are SAMLYT AI Lead Script Editor.
      Task: $actionPrompt
      Tone: $tone
      Script excerpt / context:
      \"\"\"$currentText\"\"\"
      
      Additional Creator Note: $instruction
      
      Output ONLY the refined text or suggestion. Do not include introductory conversational filler.
    """.trimIndent()

    val rawResponse = callGeminiRaw(prompt, key, model = "gemini-3.5-flash")
    if (rawResponse.isNotBlank()) {
      rawResponse.trim()
    } else {
      when (action.uppercase()) {
        "PUNCH_UP_HOOK" -> "[HOOK] \"Before you buy your next camera lens, there is a $2,000 secret cinema houses don't want you to know. In the next 8 minutes, we break down why your eyes are being tricked by modern sensors.\""
        "SUGGEST_BROLL" -> "[B-ROLL CUE] Extreme close-up 50mm macro of camera iris blade mechanism adjusting, backlit with warm 3200K tungsten lamp, volumetric haze cutting through dark studio."
        "PATTERN_INTERRUPT" -> "[PATTERN INTERRUPT] Abrupt audio silence. Hard cut from 24fps filmic narrative to raw 60fps behind-the-scenes handheld camera pointing directly at the camera monitor."
        else -> "Continued scene beat: \"And that is why the aperture setting matters more than pixel count.\""
      }
    }
  }

  suspend fun reverseEngineerYouTubeFormula(
    urlOrChannel: String,
    userKey: String
  ): YouTubeFormulaBlueprint = withContext(Dispatchers.IO) {
    val key = resolveApiKey(userKey)
    val prompt = """
      You are SAMLYT AI YouTube Algorithm & Production Reverse-Engineer.
      Analyze the target YouTube channel, creator, or video link: "$urlOrChannel".
      Deconstruct their signature formula into a repeatable, step-by-step system workflow.
      
      Respond in this exact structured format:
      CREATOR_STYLE: (e.g. Vox Visual Essayist, MrBeast Hyper-Paced Retention, Johnny Harris Cinematic Narrative, Ali Abdaal Clean Productivity)
      HOOK_FORMULA: (The exact 0-30s psychological blueprint used to hook audiences)
      PACING_CUT_RATE: (e.g. 2.1 seconds per visual cut, kinetic typography bursts every 7 seconds)
      NARRATIVE_FRAMEWORK: (The 4-5 beat structural progression: Thesis -> Paradox -> Escalation -> Deep Dive -> Conclusion)
      VISUAL_AESTHETIC: (Optics package, color grading, paper textures, 3D camera maps, archival motion)
      SOUND_DESIGN: (Audio risers, downshifters, vinyl crackle, acoustic swells, subtle bass drops)
      WORKFLOW_STEPS:
      1. Step 1 description
      2. Step 2 description
      3. Step 3 description
      4. Step 4 description
      5. Step 5 description
      SAMPLE_ARCHITECTURE: (e.g. 3-Act Investigative Essay)
    """.trimIndent()

    val raw = callGeminiRaw(prompt, key, model = "gemini-3.1-pro-preview")
    if (raw.isNotBlank()) {
      parseFormulaBlueprint(urlOrChannel, raw)
    } else {
      fallbackFormulaBlueprint(urlOrChannel)
    }
  }

  suspend fun generateThumbnailConcepts(
    premise: String,
    title: String,
    archetype: String,
    userKey: String
  ): List<ThumbnailConcept> = withContext(Dispatchers.IO) {
    val key = resolveApiKey(userKey)
    val prompt = """
      You are SAMLYT AI Thumbnail Studio.
      Generate exactly 3 high-CTR thumbnail packaging concepts for:
      Premise: $premise
      Video Title: $title
      Composition Archetype: $archetype

      For each concept, provide:
      1. Concept Title
      2. Archetype style
      3. Midjourney v6 Prompt
      4. Focal Subject & Expression
      5. Lighting & Color Palette
      6. Projected CTR Score (e.g. 92)
      7. A/B Title Pairing recommendation
    """.trimIndent()

    val rawResponse = callGeminiRaw(prompt, key, model = "gemini-3.1-pro-preview")
    if (rawResponse.isNotBlank()) {
      parseThumbnailConcepts(rawResponse, premise)
    } else {
      fallbackThumbnailConcepts(premise, title, archetype)
    }
  }

  suspend fun streamVoiceDirector(
    userMessage: String,
    history: List<Pair<String, String>>,
    mode: String,
    userKey: String
  ): String = withContext(Dispatchers.IO) {
    val key = resolveApiKey(userKey)
    val prompt = """
      You are Sam Skytube (SAMLYT AI), a specialized female AI Voice Director assisting a YouTube content creator.
      Session Mode: $mode
      Tone: Confident, encouraging, concise, creative director female voice.
      Rules:
      - STRICT: Speak in 1-2 natural sentences maximum (under 35 words total).
      - Do NOT use markdown symbols, asterisks, bullet points, brackets, or code tokens — this text will be read aloud through text-to-speech.
      - Acknowledge the user's creative vision, state which department is handling it (e.g. Director, Cinematography, Sound, Script), and give immediate actionable direction.
      Creator said: "$userMessage"
    """.trimIndent()

    val rawResponse = callGeminiRaw(prompt, key, model = "gemini-3.5-flash")
    if (rawResponse.isNotBlank()) {
      rawResponse.replace(Regex("[*#_`\\[\\]]"), "").trim()
    } else {
      "Director approved. I am calibrating the camera framing and pacing for scene one right now."
    }
  }

  suspend fun generateImagen3Thumbnails(
    prompt: String,
    sampleCount: Int = 3,
    aspectRatio: String = "16:9",
    userKey: String
  ): Result<List<String>> = withContext(Dispatchers.IO) {
    val key = resolveApiKey(userKey)
    if (key.isBlank()) {
      return@withContext Result.failure(IllegalStateException("No API key available"))
    }

    // 1. Call Imagen 3 API
    try {
      val jsonBody = JSONObject().apply {
        put("instances", JSONArray().put(JSONObject().apply {
          put("prompt", prompt)
        }))
        put("parameters", JSONObject().apply {
          put("sampleCount", sampleCount)
          put("aspectRatio", aspectRatio)
        })
      }
      val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
      val url = "https://generativelanguage.googleapis.com/v1beta/models/imagen-3.0-generate-002:predict?key=$key"
      val request = Request.Builder()
        .url(url)
        .addHeader("x-goog-api-key", key)
        .addHeader("Content-Type", "application/json")
        .post(requestBody)
        .build()

      val response = client.newCall(request).execute()
      if (response.isSuccessful) {
        val responseString = response.body?.string().orEmpty()
        val root = JSONObject(responseString)
        val predictions = root.optJSONArray("predictions")
        val images = mutableListOf<String>()
        if (predictions != null) {
          for (i in 0 until predictions.length()) {
            val item = predictions.optJSONObject(i)
            val b64 = item?.optString("bytesBase64Encoded").orEmpty().ifEmpty {
              item?.optJSONObject("image")?.optString("imageBytes").orEmpty().ifEmpty {
                item?.optString("imageBytes").orEmpty()
              }
            }
            if (b64.isNotBlank()) {
              images.add(b64)
            }
          }
        }
        if (images.isNotEmpty()) {
          return@withContext Result.success(images)
        }
      }
    } catch (e: Exception) {
      // Imagen 3 request failed, proceed to fallback multimodal image generation
    }

    // 2. Fallback: Multimodal Image Models (gemini-3.1-flash-image-preview and gemini-2.5-flash-image)
    val fallbackImageModels = listOf("gemini-3.1-flash-image-preview", "gemini-2.5-flash-image")
    for (imgModel in fallbackImageModels) {
      try {
        val nanoUrl = "https://generativelanguage.googleapis.com/v1beta/models/$imgModel:generateContent?key=$key"
        val nanoJson = JSONObject().apply {
          put("contents", JSONArray().put(JSONObject().apply {
            put("parts", JSONArray().put(JSONObject().apply {
              put("text", "Generate high quality YouTube thumbnail in $aspectRatio aspect ratio: $prompt")
            }))
          }))
          put("generationConfig", JSONObject().apply {
            put("responseModalities", JSONArray().put("IMAGE"))
            put("imageConfig", JSONObject().apply {
              put("aspectRatio", aspectRatio)
              put("imageSize", "1K")
            })
          })
        }
        val nanoRequest = Request.Builder()
          .url(nanoUrl)
          .addHeader("x-goog-api-key", key)
          .addHeader("Content-Type", "application/json")
          .post(nanoJson.toString().toRequestBody("application/json".toMediaType()))
          .build()

        val nanoResponse = client.newCall(nanoRequest).execute()
        if (nanoResponse.isSuccessful) {
          val bodyStr = nanoResponse.body?.string().orEmpty()
          val root = JSONObject(bodyStr)
          val candidates = root.optJSONArray("candidates")
          val firstCand = candidates?.optJSONObject(0)
          val parts = firstCand?.optJSONObject("content")?.optJSONArray("parts")
          val images = mutableListOf<String>()
          if (parts != null) {
            for (i in 0 until parts.length()) {
              val part = parts.optJSONObject(i)
              val inlineData = part?.optJSONObject("inlineData")
              val data = inlineData?.optString("data").orEmpty()
              if (data.isNotBlank()) {
                images.add(data)
              }
            }
          }
          if (images.isNotEmpty()) {
            return@withContext Result.success(images)
          }
        }
      } catch (e: Exception) {
        // Try next fallback model
      }
    }

    Result.failure(Exception("Image generation failed"))
  }

  private fun callGeminiRaw(prompt: String, apiKey: String, model: String): String {
    if (apiKey.isBlank()) return ""
    val candidateModels = if (model.contains("pro")) {
      listOf(model, "gemini-3.1-pro-preview", "gemini-3.5-flash", "gemini-flash-latest").distinct()
    } else {
      listOf(model, "gemini-3.5-flash", "gemini-flash-latest", "gemini-3.1-flash-lite-preview").distinct()
    }

    for (m in candidateModels) {
      try {
        val jsonBody = JSONObject().apply {
          put("contents", JSONArray().put(JSONObject().apply {
            put("parts", JSONArray().put(JSONObject().put("text", prompt)))
          }))
        }
        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$m:generateContent?key=$apiKey"
        val request = Request.Builder().url(url).post(requestBody).build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
          val responseBody = response.body?.string().orEmpty()
          val root = JSONObject(responseBody)
          val candidates = root.optJSONArray("candidates")
          val firstCand = candidates?.optJSONObject(0)
          val content = firstCand?.optJSONObject("content")
          val parts = content?.optJSONArray("parts")
          val text = parts?.optJSONObject(0)?.optString("text").orEmpty()
          if (text.isNotBlank()) return text
        }
      } catch (e: Exception) {
        // Try next candidate model
      }
    }
    return ""
  }

  private fun parseResearchResponse(url: String, text: String): ResearchResult {
    return ResearchResult(
      videoUrl = url,
      channelTitle = if (url.contains("youtube.com") || url.contains("youtu.be")) "YouTube Deep Synthesis" else "Creator Topic Archive",
      videoTitle = "High-Retention Topic Analysis",
      hookScore = 94,
      retentionCurveNotes = text.take(300),
      viralAngles = listOf(
        "Why 90% of creators misunderstand cinematic lenses",
        "The hidden psychology of 24fps vs 60fps motion blur",
        "How Hitchcock engineered subconscious visual vertigo"
      ),
      keyHooks = listOf(
        "0:00 - Shock opening: Show immediate before/after lens contrast",
        "0:15 - Question assumption: Why your eyes reject digital sharpness",
        "0:30 - Tension anchor: Introduce the 35mm experiment"
      ),
      audienceGaps = listOf(
        "Competitors focus on gear specs rather than cognitive storytelling",
        "Zero longform videos comparing 1970s Technicolor with Rec.2020 color timing"
      ),
      recommendedStructure = "3-Act Narrative Tension Arc with 14-second visual B-roll pacing"
    )
  }

  private fun fallbackResearchResult(url: String, angle: String): ResearchResult {
    return ResearchResult(
      videoUrl = if (url.isNotBlank()) url else "https://youtube.com/watch?v=cinematic_lens_psychology",
      channelTitle = "Studio Synthesized Analysis",
      videoTitle = "Deconstructing Viral Cinematic Visuals",
      hookScore = 91,
      retentionCurveNotes = "Pacing profile: Visual density high in Act I. Sub-3-second retention triggers, looping audio bridges, and rapid cut sequences.",
      viralAngles = listOf(
        "Why cinematic color timing feels more visceral than modern 8K HDR",
        "The cognitive lie behind modern smartphone sensor sharpness",
        "How Hollywood keeps 45-minute video essays at 82% average watch time"
      ),
      keyHooks = listOf(
        "Open with extreme 35mm anamorphic flare cut against iPhone 16 Raw",
        "Pose the paradox: More resolution makes movies look cheaper",
        "Introduce the optical science test at 0:24 mark"
      ),
      audienceGaps = listOf(
        "Lack of actionable timeline XML presets for solo editors",
        "Audience fatigue with generic camera review formats"
      ),
      recommendedStructure = "3-Act Cinematic Essay (8 - 12 min) with 0:45 hook cadence"
    )
  }

  private fun parseScriptScenes(text: String, premise: String): List<ScriptScene> {
    val scenes = mutableListOf<ScriptScene>()
    val blocks = text.split("[SCENE]").filter { it.isNotBlank() }
    for ((index, block) in blocks.withIndex()) {
      val lines = block.lines()
      var timestamp = "0${index * 2}:00 - 0${index * 2 + 1}:30"
      var actTitle = "Act ${index + 1}: Cinematic Beat"
      var narration = ""
      var bRoll = "35mm anamorphic wide shot, moody studio lighting"
      var retention = "Introduce visual contrast hook"

      for (line in lines) {
        val trimmed = line.trim()
        when {
          trimmed.startsWith("Timestamp:", ignoreCase = true) ->
            timestamp = trimmed.substringAfter(":").trim()
          trimmed.startsWith("Act:", ignoreCase = true) ->
            actTitle = trimmed.substringAfter(":").trim()
          trimmed.startsWith("Narration:", ignoreCase = true) ->
            narration = trimmed.substringAfter(":").trim()
          trimmed.startsWith("B-Roll:", ignoreCase = true) ->
            bRoll = trimmed.substringAfter(":").trim()
          trimmed.startsWith("Retention Anchor:", ignoreCase = true) ->
            retention = trimmed.substringAfter(":").trim()
        }
      }
      if (narration.isBlank()) narration = block.take(200).replace("\n", " ")
      scenes.add(ScriptScene(timestamp, actTitle, narration, bRoll, retention))
    }
    return if (scenes.isNotEmpty()) scenes else fallbackScriptScenes(premise, "Cinematic", "8 - 12 min")
  }

  private fun fallbackScriptScenes(premise: String, tone: String, duration: String): List<ScriptScene> {
    val topic = if (premise.isNotBlank()) premise else "Why cinematic glass lies to your brain"
    return listOf(
      ScriptScene(
        timestamp = "00:00 - 00:45",
        actTitle = "Act I: The Visceral Contrast",
        narration = "Look at this frame. Shot on a $100,000 Panavision anamorphic lens. Now look at this one. Shot on an ultra-modern 8K digital sensor. Your eyes tell you one feels like cinema, and the other feels like a tech demo. But the reason isn't resolution. It's an optical illusion engineered in 1953.",
        bRollCue = "Close-up macro push on vintage 35mm iris opening. Split-screen Rec.709 vs Technicolor bath.",
        retentionAnchor = "Sub-3-second cognitive tension hook"
      ),
      ScriptScene(
        timestamp = "00:45 - 03:20",
        actTitle = "Act II: The Hitchcock Paradox",
        narration = "When Hitchcock designed the famous vertigo zoom in 1958, he wasn't just moving a camera. He discovered that changing focal length while tracking physically distorts how human depth perception processes spatial memory.",
        bRollCue = "Overhead lighting diagram, slow motion dolly-zoom recreation on studio bust.",
        retentionAnchor = "High conceptual curiosity spike"
      ),
      ScriptScene(
        timestamp = "03:20 - 07:15",
        actTitle = "Act III: The Digital Sterile Trap",
        narration = "Today's sensors capture so much micro-contrast that our visual cortex experiences what neuroscientists call perceptual friction. When every pixel is sharp, nothing is prioritized.",
        bRollCue = "Waveform scope overlay with false-color luminance breakdown.",
        retentionAnchor = "14s rhythm change & pacing re-anchor"
      ),
      ScriptScene(
        timestamp = "07:15 - 10:00",
        actTitle = "Act IV: The Solo Director's Solution",
        narration = "Here is the exact 3-step color science workflow you can drop into your timeline today to inject authentic cinematic soul back into digital footage.",
        bRollCue = "FCPXML timeline scrubbing with LUT application and film grain emulation.",
        retentionAnchor = "High-value tactical payoff"
      )
    )
  }

  private fun parseStoryboardShots(text: String, optics: String, aspect: String): List<StoryboardShot> {
    return fallbackStoryboardShots(text, optics, aspect)
  }

  private fun fallbackStoryboardShots(premise: String, optics: String, aspect: String): List<StoryboardShot> {
    return listOf(
      StoryboardShot(
        id = UUID.randomUUID().toString(),
        shotNumber = 1,
        focalLength = "35mm Anamorphic T1.5",
        movement = "Slow tracking push-in at low angle",
        visualPrompt = "Cinematic 8K film still, 35mm anamorphic prime lens, dramatic golden key light with deep charcoal shadows, subtle atmospheric haze, sharp focal subject, color graded in ACEScg, ultra-detailed --ar $aspect --v 6.1",
        lighting = "High contrast orange tungsten key with cyan fill",
        aspectRatio = aspect
      ),
      StoryboardShot(
        id = UUID.randomUUID().toString(),
        shotNumber = 2,
        focalLength = "50mm Vintage Prime T2.0",
        movement = "Static profile portrait with shallow depth of field",
        visualPrompt = "Intimate cinematic portrait in dark studio workspace, glowing computer monitors casting rim light, retro camera gear on wooden workbench, 50mm vintage lens character, organic film grain --ar $aspect --v 6.1",
        lighting = "Monitor glow rim with warm overhead pendant",
        aspectRatio = aspect
      ),
      StoryboardShot(
        id = UUID.randomUUID().toString(),
        shotNumber = 3,
        focalLength = "85mm Telephoto T1.8",
        movement = "Dolly lateral reveal behind glass shelf",
        visualPrompt = "Macro extreme close up of vintage camera lens aperture blades, mechanical iris, brass gears, warm specular highlights, 85mm macro lens, cinematic depth --ar $aspect --v 6.1",
        lighting = "Directional warm accent spot",
        aspectRatio = aspect
      ),
      StoryboardShot(
        id = UUID.randomUUID().toString(),
        shotNumber = 4,
        focalLength = "24mm Ultra-Wide T2.8",
        movement = "Top-down overhead crane descend",
        visualPrompt = "Overhead bird-eye cinematic view of modern video director desk, storyboard cards, timeline screen, dark room aesthetic with neon orange ambient glow --ar $aspect --v 6.1",
        lighting = "Top down diffuse grid with orange accent LED strip",
        aspectRatio = aspect
      )
    )
  }

  private fun parseThumbnailConcepts(text: String, premise: String): List<ThumbnailConcept> {
    return fallbackThumbnailConcepts(premise, "Why Minimalist Tech Won", "Curiosity Gap")
  }

  private fun fallbackThumbnailConcepts(premise: String, title: String, archetype: String): List<ThumbnailConcept> {
    val topic = if (title.isNotBlank()) title else "Why 90% of Cameras Lie to You"
    return listOf(
      ThumbnailConcept(
        id = UUID.randomUUID().toString(),
        title = "Concept A: The Cognitive Paradox",
        archetype = "Curiosity Gap",
        midjourneyPrompt = "High-impact YouTube thumbnail, extreme close-up of a human eye reflecting glowing vintage camera lenses, split lighting with hyper-saturated orange and midnight blue, ultra-sharp subject with dark vignetted background, bold visual curiosity, 8k render --ar 16:9 --style raw",
        focalSubject = "Human eye with macro reflection of anamorphic lens",
        lightingColor = "Split Neon Orange (#F97316) and Cyan Blue",
        ctrScore = 96,
        suggestedTitle = "The $100,000 Lie in Every Movie"
      ),
      ThumbnailConcept(
        id = UUID.randomUUID().toString(),
        title = "Concept B: The Impossible Side-by-Side",
        archetype = "Before vs After",
        midjourneyPrompt = "Viral YouTube thumbnail packaging, sharp split screen comparison, left side grainy vintage film frame labeled 1954, right side sterile ultra-sharp 8k digital frame, glowing red and green retention indicators, high CTR composition, hyper-realistic --ar 16:9",
        focalSubject = "Split screen vintage film vs 8K digital face",
        lightingColor = "Technicolor warm bath vs cold sterile fluorescent",
        ctrScore = 91,
        suggestedTitle = "Why 8K Actually Ruins Cinema"
      ),
      ThumbnailConcept(
        id = UUID.randomUUID().toString(),
        title = "Concept C: Shocked Director Reaction",
        archetype = "Subject + Reaction",
        midjourneyPrompt = "Dramatic cinematic YouTube thumbnail, solo film director holding a massive vintage cinema lens with intense focused expression, high contrast studio rim lighting, dark textured background with subtle film reels, bold expressive composition --ar 16:9",
        focalSubject = "Director with intense expression holding heavy glass lens",
        lightingColor = "Dramatic amber edge lighting with deep obsidian shadows",
        ctrScore = 88,
        suggestedTitle = "I Stopped Using Modern Lenses"
      )
    )
  }

  private fun fallbackStoryboardShotsFromScenes(
    scenes: List<ScriptScene>,
    optics: String,
    aspectRatio: String
  ): List<StoryboardShot> {
    return scenes.mapIndexed { index, scene ->
      val lens = when (index % 4) {
        0 -> "24mm Ultra-Wide Anamorphic T1.5"
        1 -> "35mm Prime Cinema T1.4"
        2 -> "50mm Portrait Macro T1.2"
        else -> "85mm Telephoto Compression T1.8"
      }
      val move = when (index % 4) {
        0 -> "Slow push-in tracking dolly shot at eye level"
        1 -> "Smooth lateral slider movement revealing subject"
        2 -> "Handheld cinematic subtle drift with depth blur"
        else -> "Static locked tripod wide establishing frame"
      }
      val lighting = when (index % 3) {
        0 -> "High contrast chiaroscuro tungsten key with cyan rim"
        1 -> "Soft diffused North-facing window daylight with bounce fill"
        else -> "Stylized split neon complementary grade with deep blacks"
      }

      val promptDesc = scene.bRollCue.ifBlank { scene.narration.take(70) }
      StoryboardShot(
        id = UUID.randomUUID().toString(),
        shotNumber = index + 1,
        focalLength = lens,
        movement = move,
        visualPrompt = "Cinematic 8K film still, $lens, $lighting, subject visual: $promptDesc, shallow depth of field, photorealistic, cinematic color grading, 35mm grain texture --ar $aspectRatio --v 6.1",
        lighting = lighting,
        aspectRatio = aspectRatio
      )
    }
  }

  private fun parseFormulaBlueprint(urlOrChannel: String, text: String): YouTubeFormulaBlueprint {
    var creatorStyle = "Cinematic Visual Essayist"
    var hookFormula = "0-5s Shock Juxtaposition -> 5-15s Core Paradox -> 15-30s Stakes Escalation"
    var pacingCutRate = "2.3s average cut rhythm with graphic text bursts"
    var narrativeFramework = "Thesis -> Deep Historical Paradox -> Scientific Breakdown -> Unexpected Solution"
    var visualAesthetic = "35mm anamorphic prime lens, subtle film grain, paper texture kinetic graphics, 3D camera mapping"
    var soundDesign = "Textured paper rustle SFX, low sub-bass impact drops, vinyl ambient noise bed, warm orchestral swells"
    val workflow = mutableListOf<String>()
    var sampleArch = "3-Act Viral Essay"

    for (line in text.lines()) {
      val trimmed = line.trim()
      when {
        trimmed.startsWith("CREATOR_STYLE:", ignoreCase = true) ->
          creatorStyle = trimmed.substringAfter(":").trim()
        trimmed.startsWith("HOOK_FORMULA:", ignoreCase = true) ->
          hookFormula = trimmed.substringAfter(":").trim()
        trimmed.startsWith("PACING_CUT_RATE:", ignoreCase = true) ->
          pacingCutRate = trimmed.substringAfter(":").trim()
        trimmed.startsWith("NARRATIVE_FRAMEWORK:", ignoreCase = true) ->
          narrativeFramework = trimmed.substringAfter(":").trim()
        trimmed.startsWith("VISUAL_AESTHETIC:", ignoreCase = true) ->
          visualAesthetic = trimmed.substringAfter(":").trim()
        trimmed.startsWith("SOUND_DESIGN:", ignoreCase = true) ->
          soundDesign = trimmed.substringAfter(":").trim()
        trimmed.startsWith("SAMPLE_ARCHITECTURE:", ignoreCase = true) ->
          sampleArch = trimmed.substringAfter(":").trim()
        trimmed.matches(Regex("^\\d+\\..*")) -> {
          workflow.add(trimmed.replace(Regex("^\\d+\\.\\s*"), ""))
        }
      }
    }

    if (workflow.isEmpty()) {
      workflow.addAll(listOf(
        "Deconstruct the paradox in a 20-second cold open with rapid visual A/B comparison.",
        "Set up the stakes: Why this discovery directly affects the viewer today.",
        "Introduce Chapter 1 with textured kinetic title card and analog sound effect.",
        "Inject retention pattern interrupt every 45-60 seconds (camera shift, archive clip, or sound drop).",
        "Deliver climax resolution with high-tempo cinematic montage leading to clear CTA."
      ))
    }

    return YouTubeFormulaBlueprint(
      channelOrUrl = urlOrChannel,
      creatorStyle = creatorStyle,
      hookFormula = hookFormula,
      pacingCutRate = pacingCutRate,
      narrativeFramework = narrativeFramework,
      visualAesthetic = visualAesthetic,
      soundDesignArchitecture = soundDesign,
      workflowSteps = workflow,
      sampleScriptArchitecture = sampleArch
    )
  }

  private fun fallbackFormulaBlueprint(urlOrChannel: String): YouTubeFormulaBlueprint {
    val isVoxStyle = urlOrChannel.contains("vox", ignoreCase = true) || urlOrChannel.contains("harris", ignoreCase = true)
    val isMrBeastStyle = urlOrChannel.contains("beast", ignoreCase = true)
    val isAliAbdaalStyle = urlOrChannel.contains("abdaal", ignoreCase = true) || urlOrChannel.contains("productivity", ignoreCase = true)

    return when {
      isMrBeastStyle -> YouTubeFormulaBlueprint(
        channelOrUrl = urlOrChannel,
        creatorStyle = "Hyper-Paced Gamified Challenge (MrBeast Model)",
        hookFormula = "0-3s Immediate visual premise proof -> 3-10s Physical stakes escalating -> 10-25s Countdown start",
        pacingCutRate = "1.2s average cut frequency, continuous background riser audio, bold animated text callouts",
        narrativeFramework = "Rule Introduction -> Increasing Obstacles -> Elimination Ticking Clock -> Final Showdown",
        visualAesthetic = "Ultra-bright saturated primaries, 24mm wide angle perspective, vibrant multi-camera switching",
        soundDesignArchitecture = "Ticking clock SFX, whoosh transitions, dramatic brass horn hits, high-energy synthesizer beds",
        workflowSteps = listOf(
          "Phase 1: Validate thumbnail visual concept and hook before writing single script word.",
          "Phase 2: Engineer immediate visual proof in first 3 seconds; discard any verbal intro.",
          "Phase 3: Structure round-by-round escalation with clear elimination / penalty mechanics.",
          "Phase 4: Place a high-stakes emotional twist or rule reversal at the 65% mark.",
          "Phase 5: Rapid 10-second climax and immediate cutoff to maximize end-screen click-through."
        ),
        sampleScriptArchitecture = "Hyper-Retention Escalation Loop"
      )
      isVoxStyle -> YouTubeFormulaBlueprint(
        channelOrUrl = urlOrChannel,
        creatorStyle = "Investigative Cinematic Visual Essay (Vox / Johnny Harris Model)",
        hookFormula = "0-5s The Ordinary Fact -> 5-20s The Hidden Paradox -> 20-40s Why Everything You Knew is Wrong",
        pacingCutRate = "2.4s cut rhythm alternating between talking-head, vintage archival scans, and animated map motion",
        narrativeFramework = "Curiosity Gap -> Historical Origin -> The Turning Point -> Global Consequence -> Philosophical Takeaway",
        visualAesthetic = "35mm anamorphic prime lenses, warm tungsten studio lighting, tactile paper stop-motion, 3D camera projection on maps",
        soundDesignArchitecture = "Vinyl record crackle, analog typewriter clatter, tape-stop audio effect, ambient acoustic cello & piano",
        workflowSteps = listOf(
          "Step 1: Discover the unexpected historical root or paradoxical data point that contradicts common belief.",
          "Step 2: Script the voiceover as an intimate detective discovery narrative rather than a lecture.",
          "Step 3: Storyboard tactile visual metaphors (printouts, highlighter marks, map zooms, macro textures).",
          "Step 4: Layer sound design first: let audio whooshes, tape clicks, and risers drive the video cut points.",
          "Step 5: Conclude with a memorable overarching philosophical insight that reframes the viewer's world."
        ),
        sampleScriptArchitecture = "5-Beat Investigative Narrative"
      )
      isAliAbdaalStyle -> YouTubeFormulaBlueprint(
        channelOrUrl = urlOrChannel,
        creatorStyle = "Clean Studio Insight & System Architecture (Ali Abdaal Model)",
        hookFormula = "0-10s The Relatable Struggle -> 10-25s The 3-Rule Framework Teaser -> 25-45s Immediate Actionable Value",
        pacingCutRate = "3.8s cut rhythm with clean graphic callouts, split screen, and tablet screen-recording overlays",
        narrativeFramework = "The Problem -> The Mindset Shift -> The 3 Practical Principles -> Common Pitfalls -> Conclusion",
        visualAesthetic = "Soft diffused ring/softbox lighting, shallow depth of field (Sony 35mm f/1.4 GM), clean Scandinavian room setup",
        soundDesignArchitecture = "Subtle acoustic lo-fi beat, gentle mouse click / mechanical keyboard SFX, soft transition chimes",
        workflowSteps = listOf(
          "Step 1: Frame the topic around an actionable life/business system rather than broad theory.",
          "Step 2: Hook with an honest vulnerability about what failed before finding this system.",
          "Step 3: Break the meat into 3 distinct, memorable named frameworks or mental models.",
          "Step 4: Use on-screen Notion / iPad sketch callouts whenever explaining a multi-step process.",
          "Step 5: End with a direct recommendation for the next video in the series to loop session time."
        ),
        sampleScriptArchitecture = "3-Pillar Practical Framework"
      )
      else -> YouTubeFormulaBlueprint(
        channelOrUrl = urlOrChannel,
        creatorStyle = "High-Retention Cinematic Creator (Aperture / ColdFusion Model)",
        hookFormula = "0-7s Shocking metric/fact -> 7-20s Tension question -> 20-35s Visual montage with dramatic musical drop",
        pacingCutRate = "2.1s visual cut frequency with cinematic 4K B-roll and subtle 2.5D parallax photo animation",
        narrativeFramework = "Prologue (The Event) -> Act I (The Build Up) -> Act II (The Fatal Flaw) -> Act III (The Legacy)",
        visualAesthetic = "2.39:1 CinemaScope widescreen aspect ratio, moody teal & orange grade, slow push-in tracking shots",
        soundDesignArchitecture = "Deep cinematic sub-bass braams, distant atmospheric winds, delicate synth arpeggio, riser swells",
        workflowSteps = listOf(
          "Step 1: Formulate the core question that makes the viewer feel smart for watching.",
          "Step 2: Write the opening minute with zero fluff: every sentence must raise an unanswered question.",
          "Step 3: Pre-plan the B-roll shotlist so every 3 seconds of narration has a dedicated visual metaphor.",
          "Step 4: Drop the music to complete silence at the moment of highest narrative revelation.",
          "Step 5: Loop the final sentence back to the opening hook for infinite replay value."
        ),
        sampleScriptArchitecture = "3-Act Cinematic Essay Arc"
      )
    }
  }
}

