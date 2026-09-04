package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.StudioScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.ResearchScreen
import com.example.ui.screens.ScriptStudioScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StoryboardScreen
import com.example.ui.screens.ThumbnailStudioScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.screens.VoiceDirectorScreen
import com.example.ui.theme.MyApplicationTheme
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        SamlytStudioApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamlytStudioApp(
  viewModel: StudioViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  var showQuickCreateSheet by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  // Handle Android hardware back button
  BackHandler(enabled = currentScreen != StudioScreen.HOME && currentScreen != StudioScreen.LANDING) {
    viewModel.navigateTo(StudioScreen.HOME)
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = SamlytBackground
  ) {
    AnimatedContent(
      targetState = currentScreen,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      label = "screen_transition"
    ) { screen ->
      when (screen) {
        StudioScreen.LANDING -> LandingScreen(
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.AUTH -> AuthScreen(
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.ONBOARDING -> OnboardingScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.HOME -> HomeScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.RESEARCH -> ResearchScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.SCRIPT_STUDIO -> ScriptStudioScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.STORYBOARD -> StoryboardScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.THUMBNAIL_STUDIO -> ThumbnailStudioScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.VOICE_DIRECTOR -> VoiceDirectorScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.PROJECTS -> ProjectsScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.VAULT -> VaultScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
        StudioScreen.SETTINGS -> SettingsScreen(
          viewModel = viewModel,
          onNavigate = { viewModel.navigateTo(it) },
          onFabClick = { showQuickCreateSheet = true }
        )
      }
    }

    // Modal Quick-Create Sheet for the Center Orange FAB
    if (showQuickCreateSheet) {
      ModalBottomSheet(
        onDismissRequest = { showQuickCreateSheet = false },
        sheetState = sheetState,
        containerColor = SamlytCard,
        dragHandle = null
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SamlytOrange))
                Text("STUDIO QUICK CREATE", color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text("Launch Production Engine", color = SamlytTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(
              onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion { showQuickCreateSheet = false }
              }
            ) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = SamlytTextSecondary)
            }
          }

          QuickActionItem(
            icon = Icons.Default.Description,
            title = "New Script Beat",
            desc = "CineScript 4K timestamped screenplay draft",
            accent = SamlytOrange
          ) {
            showQuickCreateSheet = false
            viewModel.navigateTo(StudioScreen.SCRIPT_STUDIO)
          }

          QuickActionItem(
            icon = Icons.Default.CameraAlt,
            title = "Visual Storyboard Shot",
            desc = "Plan 8K photoreal angles & Midjourney prompts",
            accent = SamlytGreen
          ) {
            showQuickCreateSheet = false
            viewModel.navigateTo(StudioScreen.STORYBOARD)
          }

          QuickActionItem(
            icon = Icons.Default.Image,
            title = "Thumbnail Packaging",
            desc = "A/B CTR matrices, titles & curiosity hooks",
            accent = SamlytYellow
          ) {
            showQuickCreateSheet = false
            viewModel.navigateTo(StudioScreen.THUMBNAIL_STUDIO)
          }

          QuickActionItem(
            icon = Icons.Default.Mic,
            title = "Voice Director Session",
            desc = "Live 34ms audio directing with Gemini Flash",
            accent = Color(0xFFA855F7)
          ) {
            showQuickCreateSheet = false
            viewModel.navigateTo(StudioScreen.VOICE_DIRECTOR)
          }

          QuickActionItem(
            icon = Icons.Default.Search,
            title = "YouTube Video Analysis",
            desc = "Deconstruct viral competitors & whitespace",
            accent = SamlytCyan
          ) {
            showQuickCreateSheet = false
            viewModel.navigateTo(StudioScreen.RESEARCH)
          }

          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

@Composable
fun QuickActionItem(
  icon: ImageVector,
  title: String,
  desc: String,
  accent: Color,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(Color(0xFF1B1B1B))
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(10.dp))
      .clickable { onClick() }
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(accent.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(title, color = SamlytTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
      Text(desc, color = SamlytTextSecondary, fontSize = 11.sp)
    }
  }
}
