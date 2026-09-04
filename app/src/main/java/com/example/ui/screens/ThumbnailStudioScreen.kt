package com.example.ui.screens

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudioScreen
import com.example.data.model.ThumbnailConcept
import com.example.ui.components.BottomNavBar
import com.example.ui.components.SamlytLogo
import com.example.ui.components.StudioHeader
import com.example.ui.theme.SamlytBackground
import com.example.ui.theme.SamlytCard
import com.example.ui.theme.SamlytCardBorder
import com.example.ui.theme.SamlytCyan
import com.example.ui.theme.SamlytGreen
import com.example.ui.theme.SamlytOrange
import com.example.ui.theme.SamlytTextMuted
import com.example.ui.theme.SamlytTextPrimary
import com.example.ui.theme.SamlytTextSecondary
import com.example.ui.theme.SamlytYellow
import com.example.viewmodel.StudioViewModel

@Composable
fun ThumbnailStudioScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  val premise by viewModel.thumbnailPremise.collectAsState()
  val title by viewModel.thumbnailTitle.collectAsState()
  val archetype by viewModel.thumbnailArchetype.collectAsState()
  val aspect by viewModel.thumbnailAspect.collectAsState()
  val isGenerating by viewModel.isGeneratingThumbnails.collectAsState()
  val concepts by viewModel.thumbnailConcepts.collectAsState()
  val toastMessage by viewModel.thumbnailToastMessage.collectAsState()

  var selectedMode by remember { mutableStateOf("AI Packaging") }
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  LaunchedEffect(toastMessage) {
    toastMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
      viewModel.clearThumbnailToast()
    }
  }

  val archetypes = listOf("Curiosity Gap", "Subject + Reaction", "Before vs After", "Cinematic Still")
  val aspects = listOf("16:9 YT Standard", "9:16 Shorts Cover", "1:1 Community")

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "Thumbnail Studio",
        subtitle = "PACKAGING",
        versionBadge = "CTR MATRIX",
        canGoBack = true,
        onBackClick = { onNavigate(StudioScreen.HOME) },
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.THUMBNAIL_STUDIO,
        onNavigate = onNavigate,
        onFabClick = onFabClick
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(SamlytYellow)
          )
          Text(
            text = "STUDIO ENGINE READY",
            color = SamlytYellow,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Thumbnail Studio",
          color = SamlytTextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Design high-CTR packaging, title-thumbnail pairings, and visual framing.",
          color = SamlytTextSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )
      }

      // Mode Selection Pills
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("AI Packaging", "From Script", "Blank Canvas", "Upload Asset").forEach { mode ->
            val isSel = mode == selectedMode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (isSel) SamlytYellow else Color(0xFF1C1C1C))
                .border(1.dp, if (isSel) SamlytYellow else SamlytCardBorder, RoundedCornerShape(999.dp))
                .clickable { selectedMode = mode }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = mode,
                color = if (isSel) Color.Black else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }

      // Premise Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
          colors = CardDefaults.cardColors(containerColor = SamlytCard)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "THUMBNAIL PREMISE & FOCAL SUBJECT",
                color = SamlytTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text("${premise.length} / 400", color = SamlytTextMuted, fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = premise,
              onValueChange = { if (it.length <= 400) viewModel.thumbnailPremise.value = it },
              placeholder = {
                Text(
                  "Describe the core curiosity hook, expression, focal subject, background contrast, or visual conflict...",
                  color = SamlytTextMuted,
                  fontSize = 13.sp
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF181818),
                unfocusedContainerColor = Color(0xFF181818),
                focusedBorderColor = SamlytYellow,
                unfocusedBorderColor = Color(0xFF262626),
                focusedTextColor = SamlytTextPrimary,
                unfocusedTextColor = SamlytTextPrimary
              ),
              shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF222222))
                  .clickable { viewModel.thumbnailPremise.value = "Shocked expression under neon split lighting, glowing monolith" }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("Curiosity Framework", color = SamlytTextSecondary, fontSize = 10.sp)
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF222222))
                  .clickable { onNavigate(StudioScreen.VOICE_DIRECTOR) }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Default.Mic, contentDescription = null, tint = SamlytYellow, modifier = Modifier.size(12.dp))
                  Text("Mic", color = SamlytYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      // Target Format
      item {
        Text("TARGET FORMAT / ASPECT RATIO", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          aspects.forEach { asp ->
            val isSel = asp == aspect
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) Color(0xFF232014) else SamlytCard)
                .border(1.dp, if (isSel) SamlytYellow else SamlytCardBorder, RoundedCornerShape(8.dp))
                .clickable { viewModel.thumbnailAspect.value = asp }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = asp,
                color = if (isSel) SamlytYellow else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      }

      // Composition Archetype
      item {
        Text("COMPOSITION ARCHETYPE", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          archetypes.forEach { arch ->
            val isSel = arch == archetype
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) Color(0xFF232014) else SamlytCard)
                .border(1.dp, if (isSel) SamlytYellow else SamlytCardBorder, RoundedCornerShape(8.dp))
                .clickable { viewModel.thumbnailArchetype.value = arch }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = arch,
                color = if (isSel) SamlytYellow else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      }

      // Video Title Pairing
      item {
        Text("TITLE PAIRING (OPTIONAL)", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { viewModel.thumbnailTitle.value = it },
          placeholder = { Text("Test Video Title (e.g. Why Minimalist Tech...)", color = SamlytTextMuted, fontSize = 13.sp) },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SamlytCard,
            unfocusedContainerColor = SamlytCard,
            focusedBorderColor = SamlytYellow,
            unfocusedBorderColor = SamlytCardBorder,
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary
          ),
          shape = RoundedCornerShape(8.dp),
          singleLine = true
        )
      }

      // Generate Packaging Matrix Button
      item {
        Button(
          onClick = { viewModel.generateThumbnailMatrix() },
          colors = ButtonDefaults.buttonColors(containerColor = SamlytYellow),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          enabled = !isGenerating
        ) {
          if (isGenerating) {
            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generating thumbnails...", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Packaging Matrix", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Loading State (Pulsing SAMLYT icon + "Generating thumbnails..." text)
      if (isGenerating) {
        item {
          PulsingSamlytLoader()
        }
      }

      // Generated 3 Concepts
      if (concepts.isNotEmpty()) {
        item {
          Text("GENERATED PACKAGING MATRIX (3 CONCEPTS)", color = SamlytYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }

        items(concepts) { concept ->
          ThumbnailConceptCard(
            concept = concept,
            selectedAspect = aspect,
            onSaveToVault = { c ->
              viewModel.saveThumbnailToVault(c)
              Toast.makeText(context, "Thumbnail saved to Vault", Toast.LENGTH_SHORT).show()
            },
            onDownload = { c ->
              val b64 = c.imageBase64
              if (!b64.isNullOrBlank()) {
                downloadThumbnailFile(context, c.title, b64)
              } else {
                Toast.makeText(context, "No generated image to download", Toast.LENGTH_SHORT).show()
              }
            },
            onRegenerate = { id ->
              viewModel.regenerateThumbnailVariant(id)
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
fun PulsingSamlytLoader(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
  val scale by infiniteTransition.animateFloat(
    initialValue = 0.88f,
    targetValue = 1.12f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale_anim"
  )
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0.65f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "alpha_anim"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, SamlytYellow.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF14130E))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha),
        contentAlignment = Alignment.Center
      ) {
        SamlytLogo(size = 72.dp, isLiveAnimated = true)
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Generating thumbnails...",
        color = SamlytYellow,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Calling Imagen 3 8K packaging matrix...",
        color = SamlytTextSecondary,
        fontSize = 12.sp
      )
    }
  }
}

@Composable
fun ThumbnailConceptCard(
  concept: ThumbnailConcept,
  selectedAspect: String,
  onSaveToVault: (ThumbnailConcept) -> Unit,
  onDownload: (ThumbnailConcept) -> Unit,
  onRegenerate: (String) -> Unit
) {
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current

  val aspectRatioVal = when {
    selectedAspect.contains("9:16") -> 9f / 16f
    selectedAspect.contains("1:1") -> 1f
    else -> 16f / 9f
  }

  val bitmap = remember(concept.imageBase64) {
    concept.imageBase64?.let { b64 ->
      try {
        val clean = if (b64.contains(",")) b64.substringAfter(",") else b64
        val decoded = android.util.Base64.decode(clean, android.util.Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decoded, 0, decoded.size)?.asImageBitmap()
      } catch (e: Exception) {
        null
      }
    }
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = SamlytCard)
  ) {
    Column {
      // 1. The actual generated image (full width of card)
      if (bitmap != null && !concept.generationFailed) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatioVal)
            .background(Color(0xFF141414))
        ) {
          Image(
            bitmap = bitmap,
            contentDescription = concept.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )

          if (concept.isGeneratingImage) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f)),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = SamlytYellow, modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Regenerating variant...", color = SamlytYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Card details section below
      Column(modifier = Modifier.padding(14.dp)) {
        // Concept name & CTR score badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f, fill = false)
          ) {
            Text(
              text = concept.title,
              color = SamlytTextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF222222))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(concept.archetype, color = SamlytYellow, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SamlytGreen.copy(alpha = 0.2f))
              .border(1.dp, SamlytGreen.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
              .padding(horizontal = 7.dp, vertical = 3.dp)
          ) {
            Text("CTR: ${concept.ctrScore}%", color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Suggested Title: \"${concept.suggestedTitle}\"",
          color = SamlytCyan,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Focal Subject: ${concept.focalSubject} • ${concept.lightingColor}",
          color = SamlytTextMuted,
          fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // If image generation failed or no image: show text prompt in a card with "Copy Prompt" button
        if (bitmap == null || concept.generationFailed) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF181818))
              .border(1.dp, Color(0xFF262626), RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "MIDJOURNEY / IMAGEN PROMPT",
                  color = SamlytTextMuted,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF262626))
                    .clickable {
                      clipboardManager.setText(AnnotatedString(concept.midjourneyPrompt))
                      Toast.makeText(context, "Prompt copied!", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = SamlytTextSecondary, modifier = Modifier.size(11.dp))
                    Text("Copy Prompt", color = SamlytTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                  }
                }
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(concept.midjourneyPrompt, color = SamlytTextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Retry / Regenerate button
          Button(
            onClick = { onRegenerate(concept.id) },
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222018)),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SamlytYellow.copy(alpha = 0.5f)),
            enabled = !concept.isGeneratingImage
          ) {
            if (concept.isGeneratingImage) {
              CircularProgressIndicator(color = SamlytYellow, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Generating...", color = SamlytYellow, fontSize = 12.sp)
            } else {
              Text("🔄 Retry Image Generation", color = SamlytYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        } else {
          // Two buttons: "💾 Save to Vault" and "⬇️ Download PNG"
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { onSaveToVault(concept) },
              modifier = Modifier
                .weight(1f)
                .height(42.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232014)),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, SamlytYellow.copy(alpha = 0.6f))
            ) {
              Text("💾 Save to Vault", color = SamlytYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = { onDownload(concept) },
              modifier = Modifier
                .weight(1f)
                .height(42.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SamlytYellow),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("⬇️ Download PNG", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // "🔄 Regenerate" button to redo just that variant
          Button(
            onClick = { onRegenerate(concept.id) },
            modifier = Modifier
              .fillMaxWidth()
              .height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1C1C)),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SamlytCardBorder),
            enabled = !concept.isGeneratingImage
          ) {
            if (concept.isGeneratingImage) {
              CircularProgressIndicator(color = SamlytYellow, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Regenerating variant...", color = SamlytYellow, fontSize = 12.sp)
            } else {
              Text("🔄 Regenerate", color = SamlytTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
          }
        }
      }
    }
  }
}

fun downloadThumbnailFile(context: android.content.Context, conceptName: String, base64: String) {
  try {
    val clean = if (base64.contains(",")) base64.substringAfter(",") else base64
    val decoded = android.util.Base64.decode(clean, android.util.Base64.DEFAULT)
    val sanitized = conceptName.lowercase().replace("[^a-z0-9]+".toRegex(), "-").trim('-')
    val timestamp = System.currentTimeMillis()
    val filename = "samlyt-thumbnail-$sanitized-$timestamp.png"

    val contentValues = android.content.ContentValues().apply {
      put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
      put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Samlyt")
        put(MediaStore.MediaColumns.IS_PENDING, 1)
      }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    if (uri != null) {
      resolver.openOutputStream(uri)?.use { stream ->
        stream.write(decoded)
        stream.flush()
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
      }
      Toast.makeText(context, "Downloaded $filename", Toast.LENGTH_LONG).show()
    } else {
      val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
      val file = java.io.File(picturesDir, filename)
      file.writeBytes(decoded)
      Toast.makeText(context, "Downloaded $filename", Toast.LENGTH_LONG).show()
    }
  } catch (e: Exception) {
    Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
  }
}
