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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
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
import com.example.data.model.ScriptScene
import com.example.data.model.StudioScreen
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
fun ScriptStudioScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  val premise by viewModel.scriptPremise.collectAsState()
  val tone by viewModel.scriptTone.collectAsState()
  val duration by viewModel.scriptDuration.collectAsState()
  val architecture by viewModel.scriptArchitecture.collectAsState()
  val isDrafting by viewModel.isDraftingScript.collectAsState()
  val scenes by viewModel.scriptScenes.collectAsState()
  val activeProject by viewModel.activeProject.collectAsState()
  val bible by viewModel.productionBible.collectAsState()
  val isAssisting by viewModel.isAssistingScript.collectAsState()
  val assistantFeedback by viewModel.scriptAssistantFeedback.collectAsState()
  val isGeneratingStoryboard by viewModel.isGeneratingStoryboard.collectAsState()

  var selectedMode by remember { mutableStateOf("AI Director") }
  var customInstruction by remember { mutableStateOf("") }
  var assistantDraftResult by remember { mutableStateOf<String?>(null) }
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  val durations = listOf("Short 60s Reel", "Standard 8 - 12 min", "Deep Dive 20+ min")
  val tones = listOf("Cinematic", "Storytelling", "Educational", "Commentary")
  val architectures = listOf(
    Pair("3-Act Essay", "Narrative suspense arc"),
    Pair("60s Viral Short", "0.8s hook cadence"),
    Pair("Product Review", "Spec comparisons & pacing"),
    Pair("Documentary", "Investigative chronology")
  )

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "Script Studio",
        subtitle = "CINESCRIPT 4K",
        versionBadge = "PRO",
        canGoBack = true,
        onBackClick = { onNavigate(StudioScreen.HOME) },
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.SCRIPT_STUDIO,
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
              .background(SamlytOrange)
          )
          Text(
            text = "STUDIO ENGINE READY",
            color = SamlytOrange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Script Studio",
          color = SamlytTextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Initialize a high-retention cinematic draft. Select an architecture or start freeform.",
          color = SamlytTextSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )
      }

      // Active Project Banner
      activeProject?.let { proj ->
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF141A16))
              .border(1.dp, SamlytGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("ACTIVE YOUTUBE PRODUCTION", color = SamlytGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("${proj.title} * ${proj.channelTarget}", color = SamlytTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(SamlytGreen.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(proj.status, color = SamlytGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Active Bible Continuity Badge
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1B1612))
            .border(1.dp, Color(0x66F97316), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("ACTIVE PRODUCTION BIBLE", color = SamlytOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
              Text("${bible.title} * ${bible.tone}", color = SamlytTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SamlytOrange.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("CONTINUITY ON", color = SamlytOrange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Mode Selection Pills
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("AI Director", "Blank Canvas", "From Research", "Import / Link").forEach { mode ->
            val isSel = mode == selectedMode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (isSel) SamlytOrange else Color(0xFF1C1C1C))
                .border(1.dp, if (isSel) SamlytOrange else SamlytCardBorder, RoundedCornerShape(999.dp))
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
                text = "PREMISE & HOOK ANGLE",
                color = SamlytTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = "${premise.length} / 600",
                color = SamlytTextMuted,
                fontSize = 10.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = premise,
              onValueChange = { if (it.length <= 600) viewModel.scriptPremise.value = it },
              placeholder = {
                Text(
                  "Describe your video concept, hook angle, or narrative thesis...",
                  color = SamlytTextMuted,
                  fontSize = 13.sp
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF181818),
                unfocusedContainerColor = Color(0xFF181818),
                focusedBorderColor = SamlytOrange,
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
                Text("Model: CineScript 4K (Gemini Pro)", color = SamlytTextSecondary, fontSize = 10.sp)
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
                  Icon(Icons.Default.Mic, contentDescription = null, tint = SamlytOrange, modifier = Modifier.size(14.dp))
                  Text("Dictate", color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }
        }
      }

      // Target Duration
      item {
        Text("TARGET DURATION", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          durations.forEach { dur ->
            val isSel = dur == duration
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) Color(0xFF261D15) else SamlytCard)
                .border(1.dp, if (isSel) SamlytOrange else SamlytCardBorder, RoundedCornerShape(8.dp))
                .clickable { viewModel.scriptDuration.value = dur }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = dur,
                color = if (isSel) SamlytOrange else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      }

      // Narrative Tone
      item {
        Text("NARRATIVE DELIVERY TONE", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          tones.forEach { t ->
            val isSel = t == tone
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) Color(0xFF261D15) else SamlytCard)
                .border(1.dp, if (isSel) SamlytOrange else SamlytCardBorder, RoundedCornerShape(8.dp))
                .clickable { viewModel.scriptTone.value = t }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = t,
                color = if (isSel) SamlytOrange else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      }

      // Architecture Blueprint
      item {
        Text("ARCHITECTURE BLUEPRINT", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
      }

      items(architectures) { (archName, archDesc) ->
        val isSel = archName == architecture
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, if (isSel) SamlytOrange else SamlytCardBorder, RoundedCornerShape(8.dp))
            .clickable { viewModel.scriptArchitecture.value = archName },
          colors = CardDefaults.cardColors(containerColor = if (isSel) Color(0xFF1F1710) else SamlytCard)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(archName, color = if (isSel) SamlytOrange else SamlytTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              Text(archDesc, color = SamlytTextSecondary, fontSize = 11.sp)
            }
            if (isSel) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(SamlytOrange)
              )
            }
          }
        }
      }

      // Draft Script Button
      item {
        Button(
          onClick = { viewModel.draftScript() },
          colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          enabled = !isDrafting
        ) {
          if (isDrafting) {
            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Drafting with CineScript 4K...", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Draft Script", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Generated Script Scene Timeline & Rich Editor
      if (scenes.isNotEmpty()) {
        // Export & Share Bar
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .border(1.dp, SamlytCardBorder, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = SamlytCard)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "EXPORT & SYSTEM SHARE",
                color = SamlytTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = { viewModel.exportScriptAsPdf(context) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1A1A)),
                  border = androidx.compose.foundation.BorderStroke(1.dp, SamlytOrange),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.Description, contentDescription = null, tint = SamlytOrange, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("PDF Doc", color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = { viewModel.exportScriptAsPlainText(context) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162529)),
                  border = androidx.compose.foundation.BorderStroke(1.dp, SamlytCyan),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.Description, contentDescription = null, tint = SamlytCyan, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("TXT File", color = SamlytCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = { viewModel.quickShareScriptText(context) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162719)),
                  border = androidx.compose.foundation.BorderStroke(1.dp, SamlytGreen),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.Share, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Share", color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // AI Script Assistant Copilot
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, SamlytOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1712))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SamlytOrange, modifier = Modifier.size(16.dp))
                  Text("AI SCRIPT COPILOT & ASSISTANT", color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                if (isAssisting) {
                  CircularProgressIndicator(color = SamlytOrange, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Quick Action Chips
              val assistActions = listOf("Punch Up Hook", "Pattern Interrupt", "Suggest B-Roll", "Continue Script", "Polish Cadence")
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                assistActions.take(3).forEach { act ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFF2A2018))
                      .border(1.dp, Color(0xFF4A3423), RoundedCornerShape(6.dp))
                      .clickable {
                        val sampleText = scenes.firstOrNull()?.narration ?: premise
                        viewModel.assistScript(act, sampleText, customInstruction) { res ->
                          assistantDraftResult = res
                        }
                      }
                      .padding(horizontal = 8.dp, vertical = 6.dp)
                  ) {
                    Text(act, color = SamlytOrange, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                  }
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                assistActions.drop(3).forEach { act ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFF2A2018))
                      .border(1.dp, Color(0xFF4A3423), RoundedCornerShape(6.dp))
                      .clickable {
                        val sampleText = scenes.firstOrNull()?.narration ?: premise
                        viewModel.assistScript(act, sampleText, customInstruction) { res ->
                          assistantDraftResult = res
                        }
                      }
                      .padding(horizontal = 8.dp, vertical = 6.dp)
                  ) {
                    Text(act, color = SamlytOrange, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = customInstruction,
                onValueChange = { customInstruction = it },
                placeholder = { Text("Custom AI direction (e.g. 'Make it punchier', 'Add retention cliffhanger')...", color = SamlytTextMuted, fontSize = 11.sp) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(76.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color(0xFF14100D),
                  unfocusedContainerColor = Color(0xFF14100D),
                  focusedBorderColor = SamlytOrange,
                  unfocusedBorderColor = Color(0xFF33251B),
                  focusedTextColor = SamlytTextPrimary,
                  unfocusedTextColor = SamlytTextPrimary
                ),
                shape = RoundedCornerShape(8.dp)
              )

              if (!assistantFeedback.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(assistantFeedback!!, color = SamlytOrange, fontSize = 10.sp)
              }

              // Assistant Result Preview Box
              if (!assistantDraftResult.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF261D15))
                    .border(1.dp, SamlytOrange.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
                ) {
                  Column {
                    Text("AI SUGGESTION PREVIEW", color = SamlytOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(assistantDraftResult!!, color = SamlytTextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.End,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      TextButton(onClick = { assistantDraftResult = null }) {
                        Text("Dismiss", color = SamlytTextMuted, fontSize = 11.sp)
                      }
                      Spacer(modifier = Modifier.width(6.dp))
                      Button(
                        onClick = {
                          val nextIdx = scenes.size + 1
                          val newScene = ScriptScene(
                            timestamp = "${(nextIdx - 1) * 2}:00 - ${nextIdx * 2}:00",
                            actTitle = "Act $nextIdx: AI Augmented Beat",
                            narration = assistantDraftResult!!,
                            bRollCue = "Dynamic camera push, high visual contrast",
                            retentionAnchor = "AI Optimized Retention Cue"
                          )
                          viewModel.scriptScenes.value = scenes + newScene
                          assistantDraftResult = null
                          Toast.makeText(context, "Added as new beat!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
                        shape = RoundedCornerShape(6.dp)
                      ) {
                        Text("Append as New Beat", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "SCENE BREAKDOWN & RICH EDITOR (${scenes.size} BEATS)",
              color = SamlytOrange,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF222222))
                  .clickable { viewModel.addNewScriptScene() }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Default.Add, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(12.dp))
                  Text("Add Beat", color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF222222))
                  .clickable {
                    val fullText = scenes.joinToString("\n\n") { "${it.timestamp} [${it.actTitle}]\n${it.narration}\nB-Roll: ${it.bRollCue}" }
                    clipboardManager.setText(AnnotatedString(fullText))
                    Toast.makeText(context, "Full script copied to clipboard", Toast.LENGTH_SHORT).show()
                  }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Default.ContentCopy, contentDescription = null, tint = SamlytTextSecondary, modifier = Modifier.size(12.dp))
                  Text("Copy", color = SamlytTextSecondary, fontSize = 11.sp)
                }
              }
            }
          }
        }

        items(scenes.size) { index ->
          val scene = scenes[index]
          RichScriptSceneEditorCard(
            scene = scene,
            sceneIndex = index,
            onUpdateScene = { updated -> viewModel.updateScriptScene(index, updated) },
            onDeleteScene = { viewModel.removeScriptScene(index) },
            onSpeak = { text -> viewModel.speakTextWithVoiceAgent(text) }
          )
        }

        // Bridge to Visuals Storyboard
        item {
          Button(
            onClick = {
              viewModel.storyboardPremise.value = premise
              viewModel.generateStoryboardFromCurrentScript(context)
            },
            colors = ButtonDefaults.buttonColors(containerColor = SamlytGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp),
            enabled = !isGeneratingStoryboard
          ) {
            if (isGeneratingStoryboard) {
              CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Generating Storyboard from Script...", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            } else {
              Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Generate Visual Storyboard from Script Segments", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
          }
        }
      }

      item {
        Text(
          text = "Offline Room Database v3 auto-save active. Zero simulated telemetry.",
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
fun RichScriptSceneEditorCard(
  scene: ScriptScene,
  sceneIndex: Int,
  onUpdateScene: (ScriptScene) -> Unit,
  onDeleteScene: () -> Unit,
  onSpeak: (String) -> Unit = {}
) {
  var isEditing by remember { mutableStateOf(false) }
  var editedNarration by remember(scene.narration) { mutableStateOf(scene.narration) }
  var editedBRoll by remember(scene.bRollCue) { mutableStateOf(scene.bRollCue) }
  var editedActTitle by remember(scene.actTitle) { mutableStateOf(scene.actTitle) }
  var editedTimestamp by remember(scene.timestamp) { mutableStateOf(scene.timestamp) }
  var editedAnchor by remember(scene.retentionAnchor) { mutableStateOf(scene.retentionAnchor) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, if (isEditing) SamlytOrange else SamlytCardBorder, RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = SamlytCard)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF261D15))
              .border(1.dp, Color(0x66F97316), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(scene.timestamp, color = SamlytOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
          Text(scene.actTitle, color = SamlytTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Speak with voice agent
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(Color(0xFF222222))
              .clickable { onSpeak(scene.narration) }
              .padding(5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = "Read with female voice agent",
              tint = SamlytOrange,
              modifier = Modifier.size(14.dp)
            )
          }

          // Edit toggle
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(if (isEditing) SamlytOrange else Color(0xFF222222))
              .clickable {
                if (isEditing) {
                  onUpdateScene(
                    scene.copy(
                      timestamp = editedTimestamp,
                      actTitle = editedActTitle,
                      narration = editedNarration,
                      bRollCue = editedBRoll,
                      retentionAnchor = editedAnchor
                    )
                  )
                }
                isEditing = !isEditing
              }
              .padding(5.dp)
          ) {
            Icon(
              imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
              contentDescription = "Edit scene",
              tint = if (isEditing) Color.Black else SamlytTextSecondary,
              modifier = Modifier.size(14.dp)
            )
          }

          // Delete beat
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(Color(0xFF222222))
              .clickable { onDeleteScene() }
              .padding(5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete scene",
              tint = Color(0xFFEF4444),
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (isEditing) {
        Text("ACT TITLE & TIMESTAMP", color = SamlytTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedTextField(
            value = editedTimestamp,
            onValueChange = { editedTimestamp = it },
            modifier = Modifier.weight(0.4f),
            label = { Text("Timestamp", fontSize = 9.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF161616),
              unfocusedContainerColor = Color(0xFF161616),
              focusedTextColor = SamlytTextPrimary,
              unfocusedTextColor = SamlytTextPrimary
            )
          )
          OutlinedTextField(
            value = editedActTitle,
            onValueChange = { editedActTitle = it },
            modifier = Modifier.weight(0.6f),
            label = { Text("Act Title", fontSize = 9.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF161616),
              unfocusedContainerColor = Color(0xFF161616),
              focusedTextColor = SamlytTextPrimary,
              unfocusedTextColor = SamlytTextPrimary
            )
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("VOICEOVER NARRATION", color = SamlytTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        OutlinedTextField(
          value = editedNarration,
          onValueChange = { editedNarration = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF161616),
            unfocusedContainerColor = Color(0xFF161616),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary,
            focusedBorderColor = SamlytOrange
          )
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("B-ROLL CAMERA CUE", color = SamlytCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        OutlinedTextField(
          value = editedBRoll,
          onValueChange = { editedBRoll = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF161616),
            unfocusedContainerColor = Color(0xFF161616),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary,
            focusedBorderColor = SamlytCyan
          )
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("RETENTION ANCHOR", color = SamlytGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        OutlinedTextField(
          value = editedAnchor,
          onValueChange = { editedAnchor = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF161616),
            unfocusedContainerColor = Color(0xFF161616),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary,
            focusedBorderColor = SamlytGreen
          )
        )

        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = {
            onUpdateScene(
              scene.copy(
                timestamp = editedTimestamp,
                actTitle = editedActTitle,
                narration = editedNarration,
                bRollCue = editedBRoll,
                retentionAnchor = editedAnchor
              )
            )
            isEditing = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(6.dp)
        ) {
          Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Save Beat Changes", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      } else {
        Text(
          text = scene.narration,
          color = SamlytTextPrimary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // B-Roll Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1A1A1A))
            .padding(10.dp)
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SamlytCyan, modifier = Modifier.size(12.dp))
              Text("B-ROLL CUE", color = SamlytCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(scene.bRollCue, color = SamlytTextSecondary, fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Retention Anchor
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(SamlytGreen)
          )
          Text(scene.retentionAnchor, color = SamlytGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
      }
    }
  }
}
