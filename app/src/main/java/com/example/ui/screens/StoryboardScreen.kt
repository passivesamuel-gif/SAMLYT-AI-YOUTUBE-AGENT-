package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoryboardShot
import com.example.data.model.StudioScreen
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
fun StoryboardScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  val premise by viewModel.storyboardPremise.collectAsState()
  val aspect by viewModel.storyboardAspect.collectAsState()
  val optics by viewModel.storyboardOptics.collectAsState()
  val isGenerating by viewModel.isGeneratingStoryboard.collectAsState()
  val shots by viewModel.storyboardShots.collectAsState()
  val scriptScenes by viewModel.scriptScenes.collectAsState()

  var origin by remember { mutableStateOf("From Synopsis") }
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  val origins = listOf("From Synopsis", "From Script", "Blank Canvas", "Import Stills")
  val aspects = listOf("16:9", "9:16", "2.39 Scope", "4:3")
  val opticPackages = listOf("Anamorphic Prime", "Vintage 16mm", "Clean Digital")

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "Visuals Storyboard",
        subtitle = "CREATION",
        versionBadge = "PHOTOREAL 8K",
        canGoBack = true,
        onBackClick = { onNavigate(StudioScreen.HOME) },
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.STORYBOARD,
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
              .background(SamlytGreen)
          )
          Text(
            text = "PRE-VISUALIZATION ENGINE",
            color = SamlytGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Visuals Storyboard",
          color = SamlytTextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Plan cinematic shots, aspect ratios, and visual prompts for your production.",
          color = SamlytTextSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )
      }

      // Active Bible Continuity Badge
      item {
        val bible by viewModel.productionBible.collectAsState()
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF132018))
            .border(1.dp, Color(0x6622C55E), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("OPTICAL BIBLE RULES APPLIED", color = SamlytGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
              Text("${bible.title} * ${bible.cameraLighting}", color = SamlytTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SamlytGreen.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("LOCKED OPTICS", color = SamlytGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Creation Origin Pills
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          origins.forEach { o ->
            val isSel = o == origin
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (isSel) SamlytGreen else Color(0xFF1C1C1C))
                .border(1.dp, if (isSel) SamlytGreen else SamlytCardBorder, RoundedCornerShape(999.dp))
                .clickable { origin = o }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = o,
                color = if (isSel) Color.Black else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }

      // Visual Premise Card
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
                text = "VISUAL PREMISE & STYLE DIRECTION",
                color = SamlytTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = "${premise.length} / 500",
                color = SamlytTextMuted,
                fontSize = 10.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = premise,
              onValueChange = { if (it.length <= 500) viewModel.storyboardPremise.value = it },
              placeholder = {
                Text(
                  "Describe the visual atmosphere, lighting scheme, camera package, or moodboard references...",
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
                focusedBorderColor = SamlytGreen,
                unfocusedBorderColor = Color(0xFF262626),
                focusedTextColor = SamlytTextPrimary,
                unfocusedTextColor = SamlytTextPrimary
              ),
              shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF222222))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("Render: Photorealistic 8K Cinema", color = SamlytTextSecondary, fontSize = 10.sp)
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF222222))
                  .clickable { onNavigate(StudioScreen.VOICE_DIRECTOR) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(Icons.Default.Mic, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(14.dp))
                  Text("Mic", color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }
        }
      }

      // Aspect Ratio
      item {
        Text("ASPECT RATIO", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
                .background(if (isSel) Color(0xFF132218) else SamlytCard)
                .border(1.dp, if (isSel) SamlytGreen else SamlytCardBorder, RoundedCornerShape(8.dp))
                .clickable { viewModel.storyboardAspect.value = asp }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = asp,
                color = if (isSel) SamlytGreen else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }

      // Camera & Optics Package
      item {
        Text("CAMERA & OPTICS PACKAGE", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          opticPackages.forEach { opt ->
            val isSel = opt == optics
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) Color(0xFF132218) else SamlytCard)
                .border(1.dp, if (isSel) SamlytGreen else SamlytCardBorder, RoundedCornerShape(8.dp))
                .clickable { viewModel.storyboardOptics.value = opt }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = opt,
                color = if (isSel) SamlytGreen else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      }

      // Generate Storyboard Plan Button
      item {
        Button(
          onClick = {
            if (origin == "From Script" && scriptScenes.isNotEmpty()) {
              viewModel.generateStoryboardFromCurrentScript(context)
            } else {
              viewModel.generateStoryboard()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SamlytGreen),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          enabled = !isGenerating
        ) {
          if (isGenerating) {
            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generating 8K Shot Plan with Gemini...", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            val btnText = if (origin == "From Script" && scriptScenes.isNotEmpty()) {
              "Generate Storyboard from Script (${scriptScenes.size} Beats)"
            } else {
              "Generate Storyboard Plan"
            }
            Text(btnText, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Frame Timeline
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "FRAME TIMELINE (${shots.size} SHOTS)",
            color = SamlytTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Text("Target Duration: 00:00", color = SamlytTextMuted, fontSize = 11.sp)
        }
      }

      if (shots.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SamlytCard)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              SamlytLogo(size = 48.dp)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "No storyboard frames yet",
                color = SamlytTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Set your visual style above and initialize your shot sequence, or tap below to generate an optical plan.",
                color = SamlytTextSecondary,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
              )
              Spacer(modifier = Modifier.height(14.dp))
              Button(
                onClick = { viewModel.generateStoryboard() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242424)),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Generate First 4 Shots", color = SamlytTextPrimary, fontSize = 13.sp)
              }
            }
          }
        }
      } else {
        items(shots) { shot ->
          StoryboardShotCard(shot = shot)
        }
      }

      item {
        Text(
          text = "Offline auto-save enabled * Scene timeline ready for input",
          color = SamlytTextMuted,
          fontSize = 11.sp,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
        )
      }
    }
  }
}

@Composable
fun StoryboardShotCard(shot: StoryboardShot) {
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current
  var showConfirmDialog by remember { mutableStateOf(false) }
  var isGenerated by remember { mutableStateOf(false) }
  val isVeo = shot.movement.contains("Veo", ignoreCase = true) || shot.movement.contains("orbital", ignoreCase = true) || shot.movement.contains("zoom", ignoreCase = true)

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
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (isVeo) SamlytOrange.copy(alpha = 0.2f) else SamlytGreen.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (isVeo) "SHOT ${shot.shotNumber} (VEO VIDEO)" else "SHOT ${shot.shotNumber} (STILL)",
              color = if (isVeo) SamlytOrange else SamlytGreen,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Text(shot.focalLength, color = SamlytTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF222222))
            .clickable {
              clipboardManager.setText(AnnotatedString(shot.visualPrompt))
              Toast.makeText(context, "Prompt copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = SamlytTextSecondary, modifier = Modifier.size(11.dp))
            Text("Copy Prompt", color = SamlytTextSecondary, fontSize = 10.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text("Camera Movement: ${shot.movement}", color = if (isVeo) SamlytOrange else SamlytCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
      Text("Lighting: ${shot.lighting}", color = SamlytYellow, fontSize = 11.sp)

      Spacer(modifier = Modifier.height(8.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF181818))
          .border(1.dp, Color(0xFF262626), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Column {
          Text("GENERATIVE PROMPT (MIDJOURNEY / FLUX)", color = SamlytTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
          Spacer(modifier = Modifier.height(2.dp))
          Text(shot.visualPrompt, color = SamlytTextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isGenerated) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.Check, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(14.dp))
            Text("Generation Verified & Dispatched", color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        } else {
          Text("Explicit approval required", color = SamlytTextMuted, fontSize = 10.sp)
        }

        Button(
          onClick = { showConfirmDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = if (isVeo) SamlytOrange else SamlytGreen),
          shape = RoundedCornerShape(6.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = if (isVeo) Icons.Default.Videocam else Icons.Default.CameraAlt,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isVeo) "Review & Confirm Veo Call" else "Review & Confirm Still Call",
            color = Color.Black,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (showConfirmDialog) {
        AlertDialog(
          onDismissRequest = { showConfirmDialog = false },
          containerColor = Color(0xFF141414),
          title = {
            Text(
              text = if (isVeo) "Confirm Veo Video Generation" else "Confirm Still Plate Generation",
              color = SamlytTextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          },
          text = {
            Column {
              Text(
                text = "Before triggering a generation call, confirm the exact prompt and optical parameters:",
                color = SamlytTextSecondary,
                fontSize = 12.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF1E1E1E))
                  .padding(8.dp)
              ) {
                Text(shot.visualPrompt, color = Color(0xFFD4D4D4), fontSize = 11.sp, lineHeight = 15.sp)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("Movement: ${shot.movement}", color = if (isVeo) SamlytOrange else SamlytCyan, fontSize = 10.sp)
              Text("Lighting: ${shot.lighting}", color = SamlytYellow, fontSize = 10.sp)
            }
          },
          confirmButton = {
            Button(
              onClick = {
                showConfirmDialog = false
                isGenerated = true
                Toast.makeText(context, "Confirmed: Generation job queued for Shot ${shot.shotNumber}", Toast.LENGTH_LONG).show()
              },
              colors = ButtonDefaults.buttonColors(containerColor = if (isVeo) SamlytOrange else SamlytGreen),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text("Confirm & Queue Generation", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          },
          dismissButton = {
            TextButton(onClick = { showConfirmDialog = false }) {
              Text("Cancel", color = SamlytTextMuted)
            }
          }
        )
      }
    }
  }
}
