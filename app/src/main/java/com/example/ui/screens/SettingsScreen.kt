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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SettingsScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  val settings by viewModel.settings.collectAsState()
  val isApiConnected by viewModel.isApiConnected.collectAsState()
  val isTestingApi by viewModel.isTestingApi.collectAsState()
  val apiTestMessage by viewModel.apiTestMessage.collectAsState()
  val apiConnectionStatus by viewModel.apiConnectionStatus.collectAsState()

  var apiKeyInput by remember(settings.geminiApiKey) { mutableStateOf(settings.geminiApiKey) }
  var zeroTelemetry by remember { mutableStateOf(true) }
  var lowLatencyVoice by remember { mutableStateOf(true) }
  val context = LocalContext.current

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "Settings",
        subtitle = "PREFERENCES & ENCLAVE",
        versionBadge = "V2.4",
        canGoBack = true,
        onBackClick = { onNavigate(StudioScreen.HOME) },
        onProfileClick = { }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.SETTINGS,
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
            text = "STUDIO CONFIGURATION",
            color = SamlytOrange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Studio Settings",
          color = SamlytTextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Configure Gemini API keys, production pipeline defaults, and local cryptographic parameters.",
          color = SamlytTextSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )
      }

      // Gemini API Key Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
          colors = CardDefaults.cardColors(containerColor = SamlytCard)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Key, contentDescription = null, tint = SamlytOrange, modifier = Modifier.size(16.dp))
                Text("GEMINI API KEY", color = SamlytTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (isTestingApi) {
                  CircularProgressIndicator(
                    color = SamlytOrange,
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 2.dp
                  )
                  Text("Testing...", color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                } else if (isApiConnected) {
                  Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SamlytGreen))
                  Text("Connected", color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                } else {
                  Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFE53935)))
                  Text("No Key / Offline", color = Color(0xFFE53935), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Powers CineScript reasoning (gemini-3.1-pro-preview), Voice Director streaming (gemini-3.5-flash), and thumbnail packaging.",
              color = SamlytTextSecondary,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = apiKeyInput,
              onValueChange = { apiKeyInput = it },
              placeholder = { Text("AIzaSy...", color = SamlytTextMuted, fontSize = 12.sp) },
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF181818),
                unfocusedContainerColor = Color(0xFF181818),
                focusedBorderColor = SamlytOrange,
                unfocusedBorderColor = Color(0xFF262626),
                focusedTextColor = SamlytTextPrimary,
                unfocusedTextColor = SamlytTextPrimary
              ),
              shape = RoundedCornerShape(8.dp),
              singleLine = true
            )

            if (!apiTestMessage.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isApiConnected) Color(0xFF0D2418) else Color(0xFF2B1313))
                  .border(
                    1.dp,
                    if (isApiConnected) SamlytGreen.copy(alpha = 0.4f) else Color(0xFFE53935).copy(alpha = 0.4f),
                    RoundedCornerShape(6.dp)
                  )
                  .padding(8.dp)
              ) {
                Text(
                  text = apiTestMessage ?: "",
                  color = if (isApiConnected) SamlytGreen else Color(0xFFFF8A80),
                  fontSize = 11.sp,
                  lineHeight = 15.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  viewModel.updateSettings(settings.copy(geminiApiKey = apiKeyInput))
                  viewModel.testGeminiApi()
                  Toast.makeText(context, "API key saved. Verifying connection...", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Key", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = {
                  viewModel.updateSettings(settings.copy(geminiApiKey = apiKeyInput))
                  viewModel.testGeminiApi()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                enabled = !isTestingApi
              ) {
                if (isTestingApi) {
                  CircularProgressIndicator(color = SamlytCyan, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                } else {
                  Icon(Icons.Default.Refresh, contentDescription = null, tint = SamlytCyan, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Test Connection", color = SamlytCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Production Pipeline Settings
      item {
        Text("PRODUCTION PIPELINE", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
      }

      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
          colors = CardDefaults.cardColors(containerColor = SamlytCard)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SettingItemRow("Production Focus", settings.productionFocus)
            SettingItemRow("Primary NLE Target", settings.nleTarget)
            SettingItemRow("Base Project Framerate", settings.baseFramerate)
            SettingItemRow("Script Scratch Voice", settings.scriptVoice)
            SettingItemRow("Auto-Save Interval", "${settings.autoSaveIntervalSeconds} seconds")
          }
        }
      }

      // Security & Privacy
      item {
        Text("ENCLAVE & HARDWARE PRIVACY", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
      }

      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
          colors = CardDefaults.cardColors(containerColor = SamlytCard)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Zero-Telemetry Air-Gap", color = SamlytTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Do not send usage telemetry or generation traces to third-party endpoints.", color = SamlytTextSecondary, fontSize = 10.sp)
              }
              Switch(
                checked = zeroTelemetry,
                onCheckedChange = { zeroTelemetry = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.Black,
                  checkedTrackColor = SamlytGreen
                )
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Neural 34ms Low Latency", color = SamlytTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Stream director dialogue directly over WebSockets for instant response.", color = SamlytTextSecondary, fontSize = 10.sp)
              }
              Switch(
                checked = lowLatencyVoice,
                onCheckedChange = { lowLatencyVoice = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.Black,
                  checkedTrackColor = SamlytGreen
                )
              )
            }
          }
        }
      }

      // About
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
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("SAMLYT AI STUDIO", color = SamlytOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("Version 2.4.0 (Production Build)", color = SamlytTextPrimary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Designed for High-Retention YouTube Content Creators", color = SamlytTextSecondary, fontSize = 11.sp)
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
fun SettingItemRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, color = SamlytTextSecondary, fontSize = 12.sp)
    Text(value, color = SamlytTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
  }
}
