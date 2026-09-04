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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectType
import com.example.data.model.StudioScreen
import com.example.ui.components.BeatListReviewCheckpointDialog
import com.example.ui.components.BottomNavBar
import com.example.ui.components.CastContinuityDialog
import com.example.ui.components.DepartmentCrewSection
import com.example.ui.components.DocumentImportCard
import com.example.ui.components.DocumentImportDialog
import com.example.ui.components.ProductionBibleEditDialog
import com.example.ui.components.ProductionBibleSummaryCard
import com.example.ui.components.ProductionProjectCard
import com.example.ui.components.ProductionProposalsSection
import com.example.ui.components.StartNewProjectDialog
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
fun HomeScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  var universalPrompt by remember { mutableStateOf("") }
  val projects by viewModel.projects.collectAsState()
  val activeProject by viewModel.activeProject.collectAsState()
  val lastSaved by viewModel.lastAutoSaved.collectAsState()

  val isSpeaking by viewModel.isVoiceAgentSpeaking.collectAsState()
  val voiceName by viewModel.voiceAgentName.collectAsState()
  val apiStatus by viewModel.apiConnectionStatus.collectAsState()
  val isApiConnected by viewModel.isApiConnected.collectAsState()

  val productionBible by viewModel.productionBible.collectAsState()
  val castMembers by viewModel.castMembers.collectAsState()
  val locationItems by viewModel.locationItems.collectAsState()
  val departmentStatuses by viewModel.departmentStatuses.collectAsState()
  val productionProposals by viewModel.productionProposals.collectAsState()
  val parsedBeats by viewModel.parsedBeats.collectAsState()
  val isReviewCheckpointVisible by viewModel.isReviewCheckpointVisible.collectAsState()

  var showImportDialog by remember { mutableStateOf(false) }
  var showBibleDialog by remember { mutableStateOf(false) }
  var showCastDialog by remember { mutableStateOf(false) }
  var showNewProjectDialog by remember { mutableStateOf(false) }

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "SAMLYT AI",
        subtitle = "HOME",
        versionBadge = "V2.4",
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showNewProjectDialog = true },
        containerColor = SamlytOrange,
        contentColor = Color.Black,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .padding(bottom = 60.dp)
          .testTag("start_new_project_fab"),
        icon = {
          Icon(Icons.Default.Add, contentDescription = "Start New YouTube Project", tint = Color.Black)
        },
        text = {
          Text("New Project", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.HOME,
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
      // System Telemetry & Engine Status Bar
      item {
        Spacer(modifier = Modifier.height(2.dp))
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(10.dp)),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // API Status Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isApiConnected) SamlytGreen else SamlytOrange)
                )
                Text(
                  text = "API: $apiStatus",
                  color = SamlytTextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF222222))
                  .clickable { viewModel.testGeminiApi() }
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("Test Connection", color = SamlytOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Voice Agent Status Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                  imageVector = Icons.Default.VolumeUp,
                  contentDescription = null,
                  tint = if (isSpeaking) SamlytGreen else SamlytOrange,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = if (isSpeaking) "Female Voice Speaking..." else "Voice Agent: $voiceName",
                  color = if (isSpeaking) SamlytGreen else SamlytTextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (isSpeaking) Color(0xFF3B1E12) else Color(0xFF222222))
                  .clickable {
                    if (isSpeaking) {
                      viewModel.stopVoiceAgentSpeech()
                    } else {
                      viewModel.speakTextWithVoiceAgent("Sam Skytube Voice Agent standing by. All production departments are active.")
                    }
                  }
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (isSpeaking) "Mute Voice" else "Test Voice",
                  color = if (isSpeaking) Color(0xFFF87171) else SamlytOrange,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // Active Project Spotlight (if active project exists)
      activeProject?.let { active ->
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .border(1.5.dp, SamlytOrange, RoundedCornerShape(14.dp))
              .testTag("active_project_spotlight"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1712))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
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
                    text = "ACTIVE PRODUCTION",
                    color = SamlytGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF2C1E14))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = active.channelTarget,
                    color = SamlytOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = active.title,
                color = SamlytTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "${active.targetDuration} * ${active.tone} * Status: ${active.status}",
                color = SamlytTextSecondary,
                fontSize = 12.sp
              )

              Spacer(modifier = Modifier.height(12.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = { onNavigate(StudioScreen.SCRIPT_STUDIO) },
                  colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  Text("Script Studio", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = { onNavigate(StudioScreen.STORYBOARD) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2520)),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  Text("Storyboard", color = SamlytTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                  onClick = { onNavigate(StudioScreen.VOICE_DIRECTOR) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2520)),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  Text("Voice", color = SamlytTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }
        }
      }
      // Top Headline
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
            text = "STUDIO CANVAS",
            color = SamlytOrange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "What are we creating?",
          color = SamlytTextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Your intelligent creator suite for production-ready YouTube content.",
          color = SamlytTextSecondary,
          fontSize = 13.sp
        )
      }

      // Universal Creation Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
          colors = CardDefaults.cardColors(containerColor = SamlytCard)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            OutlinedTextField(
              value = universalPrompt,
              onValueChange = { universalPrompt = it },
              placeholder = {
                Text(
                  text = "Describe an idea, paste a video link, or ask SAMLYT...",
                  color = SamlytTextMuted,
                  fontSize = 13.sp
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF191919),
                unfocusedContainerColor = Color(0xFF191919),
                focusedBorderColor = SamlytOrange,
                unfocusedBorderColor = Color(0xFF262626),
                focusedTextColor = SamlytTextPrimary,
                unfocusedTextColor = SamlytTextPrimary
              ),
              shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Link Chip
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF222222))
                    .clickable {
                      universalPrompt = "https://youtube.com/watch?v=cinematic_lens_psychology"
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = SamlytTextSecondary, modifier = Modifier.size(14.dp))
                    Text("YouTube Link", color = SamlytTextSecondary, fontSize = 11.sp)
                  }
                }

                // Mic Chip
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF222222))
                    .clickable { onNavigate(StudioScreen.VOICE_DIRECTOR) }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = SamlytOrange, modifier = Modifier.size(14.dp))
                    Text("Mic", color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                  }
                }
              }

              // Create button
              Button(
                onClick = {
                  if (universalPrompt.contains("youtube.com") || universalPrompt.contains("youtu.be")) {
                    viewModel.researchUrl.value = universalPrompt
                    onNavigate(StudioScreen.RESEARCH)
                  } else {
                    if (universalPrompt.isNotBlank()) {
                      viewModel.scriptPremise.value = universalPrompt
                    }
                    onNavigate(StudioScreen.SCRIPT_STUDIO)
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Text("Create", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
              }
            }
          }
        }
      }

      // Active Production Bible & Continuity
      item {
        ProductionBibleSummaryCard(
          bible = productionBible,
          castCount = castMembers.size,
          locationsCount = locationItems.size,
          onOpenBible = { showBibleDialog = true },
          onOpenCastLocations = { showCastDialog = true }
        )
      }

      // 10 Specialized Production Departments
      item {
        DepartmentCrewSection(
          statuses = departmentStatuses,
          onDepartmentClick = { dept ->
            when (dept) {
              com.example.data.model.DepartmentType.SCRIPT -> onNavigate(StudioScreen.SCRIPT_STUDIO)
              com.example.data.model.DepartmentType.CINEMATOGRAPHY, com.example.data.model.DepartmentType.VFX -> onNavigate(StudioScreen.STORYBOARD)
              com.example.data.model.DepartmentType.DISTRIBUTION -> onNavigate(StudioScreen.THUMBNAIL_STUDIO)
              com.example.data.model.DepartmentType.PERFORMANCE -> onNavigate(StudioScreen.VOICE_DIRECTOR)
              else -> { /* Handled */ }
            }
          }
        )
      }

      // Document-Imported Productions
      item {
        DocumentImportCard(
          onOpenImportDialog = { showImportDialog = true }
        )
      }

      // Operating Pattern: Analyze -> Propose -> Preview -> User Approval -> Apply -> Version Recorded
      item {
        ProductionProposalsSection(
          proposals = productionProposals,
          onApprove = { viewModel.approveProposal(it) },
          onReject = { viewModel.rejectProposal(it) }
        )
      }

      // Creative Engines
      item {
        Text(
          text = "CREATIVE ENGINES",
          color = SamlytTextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      // 4 Creative Engine Cards in 2x2 grid or list
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          EngineNavCard(
            icon = Icons.Default.Search,
            title = "Research Engine",
            desc = "Explore topics & verified sources",
            accent = SamlytCyan,
            onClick = { onNavigate(StudioScreen.RESEARCH) }
          )
          EngineNavCard(
            icon = Icons.Default.Description,
            title = "Script Studio",
            desc = "Draft hooks, structure & narrative",
            accent = SamlytOrange,
            onClick = { onNavigate(StudioScreen.SCRIPT_STUDIO) }
          )
          EngineNavCard(
            icon = Icons.Default.CameraAlt,
            title = "Visuals Storyboard",
            desc = "Plan shots & visual references",
            accent = SamlytGreen,
            onClick = { onNavigate(StudioScreen.STORYBOARD) }
          )
          EngineNavCard(
            icon = Icons.Default.Image,
            title = "Thumbnail Studio",
            desc = "Design packaging & framing",
            accent = SamlytYellow,
            onClick = { onNavigate(StudioScreen.THUMBNAIL_STUDIO) }
          )
        }
      }

      // Active YouTube Productions List
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
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
              text = "ACTIVE YOUTUBE PRODUCTIONS (${projects.size})",
              color = SamlytTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }

          Text(
            text = "+ New Project",
            color = SamlytOrange,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .clickable { showNewProjectDialog = true }
              .testTag("header_new_project_button")
          )
        }
      }

      // Empty State or List
      if (projects.isEmpty()) {
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
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF1E1E1E)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Folder,
                  contentDescription = null,
                  tint = SamlytOrange,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "No active productions yet",
                color = SamlytTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Create your first YouTube video project to start the automated AI script and storyboard pipeline.",
                color = SamlytTextSecondary,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
              )
              Spacer(modifier = Modifier.height(14.dp))
              Button(
                onClick = { showNewProjectDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start New Project", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      } else {
        items(projects) { project ->
          val isActive = project.id == activeProject?.id
          ProductionProjectCard(
            project = project,
            isActive = isActive,
            onSelectActive = { viewModel.selectActiveProject(project) },
            onOpen = {
              viewModel.selectActiveProject(project)
              when (project.type) {
                ProjectType.SCRIPT -> onNavigate(StudioScreen.SCRIPT_STUDIO)
                ProjectType.STORYBOARD -> onNavigate(StudioScreen.STORYBOARD)
                ProjectType.THUMBNAIL -> onNavigate(StudioScreen.THUMBNAIL_STUDIO)
                ProjectType.VOICE -> onNavigate(StudioScreen.VOICE_DIRECTOR)
                ProjectType.RESEARCH -> onNavigate(StudioScreen.RESEARCH)
              }
            },
            onDelete = { viewModel.deleteProject(project.id) }
          )
        }
      }

      // Auto-save badge
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Check, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Auto-save active * Saved to encrypted local vault",
            color = SamlytTextMuted,
            fontSize = 11.sp
          )
        }
      }
    }

    if (showImportDialog) {
      DocumentImportDialog(
        onDismiss = { showImportDialog = false },
        onParse = { text ->
          showImportDialog = false
          viewModel.parseProductionDocument(text)
        }
      )
    }

    if (isReviewCheckpointVisible) {
      BeatListReviewCheckpointDialog(
        beats = parsedBeats,
        onApprove = {
          viewModel.approveParsedBeats()
          onNavigate(StudioScreen.STORYBOARD)
        },
        onReject = {
          viewModel.rejectParsedBeats()
        }
      )
    }

    if (showBibleDialog) {
      ProductionBibleEditDialog(
        bible = productionBible,
        onDismiss = { showBibleDialog = false },
        onSave = { updated ->
          viewModel.updateProductionBible(updated)
          showBibleDialog = false
        }
      )
    }

    if (showCastDialog) {
      CastContinuityDialog(
        cast = castMembers,
        locations = locationItems,
        onAddCast = { name, tokens, voice -> viewModel.addCastMember(name, tokens, voice) },
        onAddLocation = { name, style, continuity -> viewModel.addLocationItem(name, style, continuity) },
        onDismiss = { showCastDialog = false }
      )
    }

    if (showNewProjectDialog) {
      StartNewProjectDialog(
        onDismiss = { showNewProjectDialog = false },
        onCreate = { title, topic, duration, tone, channel ->
          viewModel.createNewProject(title, topic, duration, tone, ProjectType.SCRIPT, channel)
          showNewProjectDialog = false
          onNavigate(StudioScreen.SCRIPT_STUDIO)
        }
      )
    }
  }
}

@Composable
fun EngineNavCard(
  icon: ImageVector,
  title: String,
  desc: String,
  accent: Color,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp))
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = SamlytCard)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
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
        Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(20.dp))
      }
      Column(modifier = Modifier.weight(1f)) {
        Text(title, color = SamlytTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(desc, color = SamlytTextSecondary, fontSize = 11.sp)
      }
      Icon(Icons.Default.ArrowForward, contentDescription = null, tint = SamlytTextMuted, modifier = Modifier.size(16.dp))
    }
  }
}

@Composable
fun ProjectSummaryCard(
  project: ProjectItem,
  onClick: () -> Unit
) {
  val typeColor = when (project.type) {
    ProjectType.SCRIPT -> SamlytOrange
    ProjectType.STORYBOARD -> SamlytGreen
    ProjectType.THUMBNAIL -> SamlytYellow
    ProjectType.VOICE -> Color(0xFFA855F7)
    ProjectType.RESEARCH -> SamlytCyan
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp))
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = SamlytCard)
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
          .size(38.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(typeColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = project.type.name.take(2),
          color = typeColor,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = project.title,
          color = SamlytTextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1
        )
        Text(
          text = project.description,
          color = SamlytTextSecondary,
          fontSize = 11.sp,
          maxLines = 1
        )
      }

      Icon(Icons.Default.ArrowForward, contentDescription = null, tint = SamlytTextMuted, modifier = Modifier.size(16.dp))
    }
  }
}
