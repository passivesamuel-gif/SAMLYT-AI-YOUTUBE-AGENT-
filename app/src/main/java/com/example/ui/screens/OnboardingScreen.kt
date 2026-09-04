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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.viewmodel.StudioViewModel

@Composable
fun OnboardingScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  var currentStep by remember { mutableIntStateOf(1) }
  var selectedFocus by remember { mutableStateOf("High-Retention Video Essays") }
  var selectedNle by remember { mutableStateOf("Final Cut Pro (FCPXML)") }
  var selectedFps by remember { mutableStateOf("24 fps") }
  var selectedVoice by remember { mutableStateOf("Aperture Studio (Neutral)") }

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "SAMLYT AI",
        subtitle = "CALIBRATION",
        versionBadge = "V2.4",
        canGoBack = true,
        onBackClick = {
          if (currentStep > 1) currentStep-- else onNavigate(StudioScreen.LANDING)
        },
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.ONBOARDING,
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
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(999.dp))
              .background(Color(0xFF1F1B14))
              .border(1.dp, Color(0x55F97316), RoundedCornerShape(999.dp))
              .padding(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Text(
              text = "CALIBRATION $currentStep OF 3",
              color = SamlytOrange,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }

          Text(
            text = "Skip Setup",
            color = SamlytTextMuted,
            fontSize = 12.sp,
            modifier = Modifier.clickable { onNavigate(StudioScreen.HOME) }
          )
        }
      }

      item {
        SamlytLogo(size = 64.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Configure Creative Engine",
          color = SamlytTextPrimary,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Tailor SAMLYT AI to your pacing structure, timeline export pipeline, and retention profile.",
          color = SamlytTextSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )
      }

      if (currentStep == 1) {
        // Step 1: Production Focus
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "PRIMARY PRODUCTION FOCUS",
              color = SamlytTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Text("Required", color = SamlytOrange, fontSize = 11.sp)
          }
        }

        item {
          FocusOptionCard(
            title = "High-Retention Video Essays (8-18m)",
            desc = "Visual density, rapid conceptual hooks, and dynamic B-roll timeline anchors.",
            isSelected = selectedFocus == "High-Retention Video Essays",
            onSelect = { selectedFocus = "High-Retention Video Essays" }
          )
        }

        item {
          FocusOptionCard(
            title = "Cinematic Longform (15-45m)",
            desc = "Deep narrative arcs, multi-act tension retention, and pacing scorecards.",
            isSelected = selectedFocus == "Cinematic Longform",
            onSelect = { selectedFocus = "Cinematic Longform" }
          )
        }

        item {
          FocusOptionCard(
            title = "Micro-Narratives & Shorts (9:16)",
            desc = "Sub-3-second retention triggers, looping audio bridges, and rapid cut sequences.",
            isSelected = selectedFocus == "Micro-Narratives & Shorts",
            onSelect = { selectedFocus = "Micro-Narratives & Shorts" }
          )
        }

        item {
          FocusOptionCard(
            title = "Narrative Fiction & Series (Episodic)",
            desc = "Character continuity tokens, multi-episode story bibles, and dialogue tuning.",
            isSelected = selectedFocus == "Narrative Fiction & Series",
            onSelect = { selectedFocus = "Narrative Fiction & Series" }
          )
        }
      } else if (currentStep == 2) {
        // Step 2: Pipeline Defaults
        item {
          Text(
            text = "STUDIO PIPELINE DEFAULTS",
            color = SamlytTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth()
          )
        }

        item {
          PipelineChoiceSection(
            label = "Primary NLE Target",
            options = listOf("Final Cut Pro (FCPXML)", "Premiere Pro", "DaVinci Resolve"),
            selected = selectedNle,
            onSelect = { selectedNle = it }
          )
        }

        item {
          PipelineChoiceSection(
            label = "Base Framerate",
            options = listOf("24 fps (Cinema)", "30 fps", "60 fps"),
            selected = selectedFps,
            onSelect = { selectedFps = it }
          )
        }

        item {
          PipelineChoiceSection(
            label = "Script Scratch Voice",
            options = listOf("Aperture Studio (Neutral)", "Cinematic Baritone", "Fast-Paced Narrator"),
            selected = selectedVoice,
            onSelect = { selectedVoice = it }
          )
        }
      } else {
        // Step 3: Zero-Leakage Creative Enclave
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, SamlytGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1F13))
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(SamlytGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Security,
                  contentDescription = null,
                  tint = SamlytGreen,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Zero-Leakage Creative Enclave",
                color = SamlytTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Your unreleased video ideas, scripts, and generation seeds run exclusively inside an air-gapped studio vault. No public model training. AES-256 hardware encryption.",
                color = SamlytTextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
              )
            }
          }
        }
      }

      // Next / Finish Button
      item {
        Button(
          onClick = {
            if (currentStep < 3) {
              currentStep++
            } else {
              val current = viewModel.settings.value
              viewModel.updateSettings(
                current.copy(
                  productionFocus = selectedFocus,
                  nleTarget = selectedNle,
                  baseFramerate = selectedFps,
                  scriptVoice = selectedVoice
                )
              )
              onNavigate(StudioScreen.HOME)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        ) {
          Text(
            text = if (currentStep < 3) "Next Step ->" else "Initialize Studio Workspace",
            color = Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      item {
        Text(
          text = "Preferences are non-destructive and can be re-tuned anytime in Studio Settings.",
          color = SamlytTextMuted,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(bottom = 24.dp)
        )
      }
    }
  }
}

@Composable
fun FocusOptionCard(
  title: String,
  desc: String,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  val border = if (isSelected) SamlytOrange else SamlytCardBorder
  val bg = if (isSelected) Color(0xFF1E1712) else SamlytCard

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, border, RoundedCornerShape(12.dp))
      .clickable { onSelect() },
    colors = CardDefaults.cardColors(containerColor = bg)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(CircleShape)
          .background(if (isSelected) SamlytOrange else Color(0xFF262626)),
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          color = SamlytTextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = desc,
          color = SamlytTextSecondary,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
      }
    }
  }
}

@Composable
fun PipelineChoiceSection(
  label: String,
  options: List<String>,
  selected: String,
  onSelect: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(SamlytCard)
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp))
      .padding(14.dp)
  ) {
    Text(
      text = label,
      color = SamlytTextPrimary,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(bottom = 10.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      options.forEach { opt ->
        val isSel = opt == selected
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSel) Color(0xFF261D15) else Color(0xFF1E1E1E))
            .border(
              1.dp,
              if (isSel) SamlytOrange else Color(0xFF2A2A2A),
              RoundedCornerShape(8.dp)
            )
            .clickable { onSelect(opt) }
            .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = opt,
              color = if (isSel) SamlytOrange else SamlytTextSecondary,
              fontSize = 12.sp,
              fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
            )
            if (isSel) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SamlytOrange,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}
