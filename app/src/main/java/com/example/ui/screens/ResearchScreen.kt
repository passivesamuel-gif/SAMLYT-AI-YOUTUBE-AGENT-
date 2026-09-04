package com.example.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudioScreen
import com.example.data.model.YouTubeFormulaBlueprint
import com.example.ui.components.BottomNavBar
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
import com.example.viewmodel.StudioViewModel

@Composable
fun ResearchScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  val url by viewModel.researchUrl.collectAsState()
  val selectedAngle by viewModel.researchAngle.collectAsState()
  val isAnalyzing by viewModel.isAnalyzingResearch.collectAsState()
  val result by viewModel.researchResult.collectAsState()

  val activeBlueprint by viewModel.activeFormulaBlueprint.collectAsState()
  val isReverseEngineering by viewModel.isReverseEngineeringFormula.collectAsState()

  var currentMode by remember { mutableStateOf("Reverse-Engineer Formula") }
  var channelInput by remember { mutableStateOf("https://youtube.com/@JohnnyHarris") }

  val angles = listOf(
    "Hook & Retention Breakdown",
    "Audience Demographics",
    "Competitor Gap Analysis",
    "Transcript Synthesis"
  )

  val channelPresets = listOf(
    "Johnny Harris (Visual Essay)" to "https://youtube.com/@JohnnyHarris",
    "MrBeast (Retention Machine)" to "https://youtube.com/@MrBeast",
    "Ali Abdaal (Productivity / Clean)" to "https://youtube.com/@aliabdaal",
    "ColdFusion (Tech Documentary)" to "https://youtube.com/@ColdFusion"
  )

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "Research Engine",
        subtitle = "INTELLIGENCE",
        versionBadge = "GEMINI PRO",
        canGoBack = true,
        onBackClick = { onNavigate(StudioScreen.HOME) },
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.RESEARCH,
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
              .background(SamlytCyan)
          )
          Text(
            text = "CONTENT RESEARCH ENGINE",
            color = SamlytCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (currentMode == "Reverse-Engineer Formula") "Channel Formula Reverse-Engineer" else "Analyze YouTube URLs",
          color = SamlytTextPrimary,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = if (currentMode == "Reverse-Engineer Formula")
            "Deconstruct any creator's repetitive YouTube system: hook formula, cut pacing, narrative beats, and visual blueprint."
          else
            "Dissect competitor retention curves, decode viral hooks, and synthesize whitespace topics with Gemini.",
          color = SamlytTextSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )
      }

      // Mode Switcher Tabs
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF181818))
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          listOf("Reverse-Engineer Formula", "Video Retention Analysis").forEach { mode ->
            val isSel = currentMode == mode
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) SamlytCyan else Color.Transparent)
                .clickable { currentMode = mode }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (mode == "Reverse-Engineer Formula") "Formula Deconstruct" else "Retention Breakdown",
                color = if (isSel) Color.Black else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }

      if (currentMode == "Reverse-Engineer Formula") {
        // Channel Deconstruction Input Card
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, SamlytCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SamlytCard)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Target YouTube Channel or Creator",
                color = SamlytTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 6.dp)
              )

              OutlinedTextField(
                value = channelInput,
                onValueChange = { channelInput = it },
                placeholder = { Text("Channel URL or name (e.g. Johnny Harris, Vox, Ali Abdaal)...", color = SamlytTextMuted, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                  Icon(Icons.Default.Link, contentDescription = null, tint = SamlytCyan, modifier = Modifier.size(18.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color(0xFF191919),
                  unfocusedContainerColor = Color(0xFF191919),
                  focusedBorderColor = SamlytCyan,
                  unfocusedBorderColor = Color(0xFF2A2A2A),
                  focusedTextColor = SamlytTextPrimary,
                  unfocusedTextColor = SamlytTextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
              )

              Spacer(modifier = Modifier.height(10.dp))
              Text("CREATOR BENCHMARKS", color = SamlytTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                channelPresets.take(2).forEach { (label, urlVal) ->
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFF1E262B))
                      .border(1.dp, Color(0xFF274350), RoundedCornerShape(6.dp))
                      .clickable { channelInput = urlVal }
                      .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(label, color = SamlytCyan, fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                  }
                }
              }

              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                channelPresets.drop(2).forEach { (label, urlVal) ->
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFF1E262B))
                      .border(1.dp, Color(0xFF274350), RoundedCornerShape(6.dp))
                      .clickable { channelInput = urlVal }
                      .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(label, color = SamlytCyan, fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = { viewModel.reverseEngineerYouTubeChannel(channelInput) },
                colors = ButtonDefaults.buttonColors(containerColor = SamlytCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp),
                enabled = !isReverseEngineering
              ) {
                if (isReverseEngineering) {
                  CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Deconstructing System with Gemini...", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Reverse-Engineer Creator Formula", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // Active Blueprint Display
        if (activeBlueprint != null) {
          val bp = activeBlueprint!!

          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SamlytGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1F16))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("REVERSE-ENGINEERED BLUEPRINT", color = SamlytGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(bp.channelName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                  }
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(SamlytGreen.copy(alpha = 0.2f))
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text(bp.creatorArchetype, color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("SIGNATURE HOOK FORMULA", color = SamlytTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(bp.signatureHookFormula, color = SamlytTextPrimary, fontSize = 12.sp, lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("VIDEO TEMPO & CUT FREQUENCY", color = SamlytTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(bp.videoTempoAndCutCadence, color = SamlytCyan, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("5-BEAT NARRATIVE ARC", color = SamlytTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(bp.narrativeFramework, color = SamlytTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("VISUAL AESTHETIC & OPTICS", color = SamlytTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(bp.visualStyleAndCameraPackage, color = SamlytTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("AUDIO & FOLEY ARCHITECTURE", color = SamlytTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(bp.audioAndSoundDesignFormula, color = SamlytTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
              }
            }
          }

          // Repetitive System Workflow Checklist
          item {
            Text("REPETITIVE SYSTEM WORKFLOW STEPS", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
          }

          itemsIndexed(bp.workflowSteps) { idx, step ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, SamlytCardBorder, RoundedCornerShape(8.dp)),
              colors = CardDefaults.cardColors(containerColor = SamlytCard)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(SamlytGreen.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text("${idx + 1}", color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(step, color = SamlytTextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
              }
            }
          }

          // Apply to Active Project Button
          item {
            Button(
              onClick = {
                viewModel.applyFormulaToActiveProject(bp)
                onNavigate(StudioScreen.SCRIPT_STUDIO)
              },
              colors = ButtonDefaults.buttonColors(containerColor = SamlytGreen),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Apply Formula & Open Script Studio", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
          }
        }
      } else {
        // Original Video Retention Analysis
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SamlytCard)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "YouTube URL or Video Link",
                color = SamlytTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 6.dp)
              )

              OutlinedTextField(
                value = url,
                onValueChange = { viewModel.researchUrl.value = it },
                placeholder = { Text("https://youtube.com/watch?v=...", color = SamlytTextMuted, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                  Icon(Icons.Default.Link, contentDescription = null, tint = SamlytCyan, modifier = Modifier.size(18.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color(0xFF191919),
                  unfocusedContainerColor = Color(0xFF191919),
                  focusedBorderColor = SamlytCyan,
                  unfocusedBorderColor = Color(0xFF2A2A2A),
                  focusedTextColor = SamlytTextPrimary,
                  unfocusedTextColor = SamlytTextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
              )

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "Analysis Angle",
                color = SamlytTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
              )

              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                angles.forEach { angle ->
                  val isSel = angle == selectedAngle
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSel) Color(0xFF13232C) else Color(0xFF1E1E1E))
                      .border(1.dp, if (isSel) SamlytCyan else Color(0xFF262626), RoundedCornerShape(8.dp))
                      .clickable { viewModel.researchAngle.value = angle }
                      .padding(horizontal = 12.dp, vertical = 8.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = angle,
                        color = if (isSel) SamlytCyan else SamlytTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                      )
                      if (isSel) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = SamlytCyan, modifier = Modifier.size(16.dp))
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = { viewModel.analyzeYouTube() },
                colors = ButtonDefaults.buttonColors(containerColor = SamlytCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp),
                enabled = !isAnalyzing
              ) {
                if (isAnalyzing) {
                  CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Analyzing with Gemini...", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                } else {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Analyze with Gemini", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // Analysis Results
        if (result != null) {
          val res = result!!

          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SamlytGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1E13))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text("HOOK RETENTION SCORE", color = SamlytGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                  Text("${res.hookScore} / 100", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                  Text("Top 5% of viral retention benchmarks in creative film category.", color = SamlytTextSecondary, fontSize = 11.sp)
                }
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(SamlytGreen.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(28.dp))
                }
              }
            }
          }

          item {
            Text("HIGH-CTR VIRAL ANGLES", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
          }

          itemsIndexed(res.viralAngles) { idx, angle ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, SamlytCardBorder, RoundedCornerShape(8.dp)),
              colors = CardDefaults.cardColors(containerColor = SamlytCard)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(SamlytCyan.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text("${idx + 1}", color = SamlytCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(angle, color = SamlytTextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
              }
            }
          }

          item {
            Text("FIRST 30-SECOND HOOK BLUEPRINT", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
          }

          itemsIndexed(res.keyHooks) { idx, hook ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, SamlytCardBorder, RoundedCornerShape(8.dp)),
              colors = CardDefaults.cardColors(containerColor = SamlytCard)
            ) {
              Text(
                text = hook,
                color = SamlytTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp),
                lineHeight = 16.sp
              )
            }
          }

          item {
            Button(
              onClick = {
                viewModel.scriptPremise.value = res.viralAngles.firstOrNull() ?: "Why 90% of cinematic lenses lie to your brain"
                onNavigate(StudioScreen.SCRIPT_STUDIO)
              },
              colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 4.dp)
            ) {
              Icon(Icons.Default.Description, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Export Angle to Script Studio", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

