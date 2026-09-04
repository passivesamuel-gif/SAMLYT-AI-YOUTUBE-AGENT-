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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectType
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  val projects by viewModel.projects.collectAsState()
  var selectedFilter by remember { mutableStateOf("All") }

  val filters = listOf("All", "Scripts", "Storyboards", "Thumbnails", "Audio")

  val filteredProjects = when (selectedFilter) {
    "Scripts" -> projects.filter { it.type == ProjectType.SCRIPT }
    "Storyboards" -> projects.filter { it.type == ProjectType.STORYBOARD }
    "Thumbnails" -> projects.filter { it.type == ProjectType.THUMBNAIL }
    "Audio" -> projects.filter { it.type == ProjectType.VOICE }
    else -> projects
  }

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "SAMLYT AI",
        subtitle = "DASHBOARD",
        versionBadge = "V2.4",
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.PROJECTS,
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
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Projects & Library",
              color = SamlytTextPrimary,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(SamlytCyan)
              )
              Text("CLOUD SYNCED", color = SamlytCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SamlytCard)
                .border(1.dp, SamlytCardBorder, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Search, contentDescription = "Search", tint = SamlytTextSecondary, modifier = Modifier.size(18.dp))
            }
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SamlytOrange)
                .clickable { onNavigate(StudioScreen.SCRIPT_STUDIO) },
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Add, contentDescription = "New", tint = Color.Black, modifier = Modifier.size(20.dp))
            }
          }
        }
      }

      // Filter Pills
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          filters.forEach { filter ->
            val isSel = filter == selectedFilter
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (isSel) SamlytOrange else Color(0xFF1C1C1C))
                .border(1.dp, if (isSel) SamlytOrange else SamlytCardBorder, RoundedCornerShape(999.dp))
                .clickable { selectedFilter = filter }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = filter,
                color = if (isSel) Color.Black else SamlytTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }

      // Quick Creation Cards
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickCreateCard(
              modifier = Modifier.weight(1f),
              icon = Icons.Default.Description,
              title = "New Script",
              desc = "Narrative, hooks & beats",
              accent = SamlytOrange,
              onClick = { onNavigate(StudioScreen.SCRIPT_STUDIO) }
            )
            QuickCreateCard(
              modifier = Modifier.weight(1f),
              icon = Icons.Default.CameraAlt,
              title = "New Storyboard",
              desc = "Frame pacing & shots",
              accent = SamlytGreen,
              onClick = { onNavigate(StudioScreen.STORYBOARD) }
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickCreateCard(
              modifier = Modifier.weight(1f),
              icon = Icons.Default.Image,
              title = "New Thumbnail",
              desc = "A/B variants & heatmaps",
              accent = SamlytYellow,
              onClick = { onNavigate(StudioScreen.THUMBNAIL_STUDIO) }
            )
            QuickCreateCard(
              modifier = Modifier.weight(1f),
              icon = Icons.Default.Upload,
              title = "Import Asset",
              desc = "XML, Final Cut, or link",
              accent = SamlytCyan,
              onClick = { onNavigate(StudioScreen.RESEARCH) }
            )
          }
        }
      }

      // Project List Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ALL PRODUCTIONS (${filteredProjects.size})",
            color = SamlytTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
      }

      if (filteredProjects.isEmpty()) {
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
                .padding(28.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              SamlytLogo(size = 56.dp)
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "No projects created yet",
                color = SamlytTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Your production scripts, storyboard sequences, thumbnail packages, and voice director sessions will be stored here.",
                color = SamlytTextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
              )
              Spacer(modifier = Modifier.height(16.dp))
              Button(
                onClick = { onNavigate(StudioScreen.SCRIPT_STUDIO) },
                colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create First Project", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      } else {
        items(filteredProjects) { project ->
          ProjectDetailedCard(
            project = project,
            onOpen = {
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

      // Vault storage info card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
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
                .clip(CircleShape)
                .background(Color(0xFF1E1E1E)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("End-to-End Vault", color = SamlytTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              Text("Hardware-level AES-256  *  0.0 MB Used of 50 GB Free", color = SamlytTextSecondary, fontSize = 11.sp)
            }
            Text("Active", color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
fun QuickCreateCard(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  title: String,
  desc: String,
  accent: Color,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(10.dp))
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = SamlytCard)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(accent.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(title, color = SamlytTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Text(desc, color = SamlytTextSecondary, fontSize = 10.sp, lineHeight = 13.sp)
    }
  }
}

@Composable
fun ProjectDetailedCard(
  project: ProjectItem,
  onOpen: () -> Unit,
  onDelete: () -> Unit
) {
  val typeColor = when (project.type) {
    ProjectType.SCRIPT -> SamlytOrange
    ProjectType.STORYBOARD -> SamlytGreen
    ProjectType.THUMBNAIL -> SamlytYellow
    ProjectType.VOICE -> Color(0xFFA855F7)
    ProjectType.RESEARCH -> SamlytCyan
  }
  val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(project.timestamp))

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp))
      .clickable { onOpen() },
    colors = CardDefaults.cardColors(containerColor = SamlytCard)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(typeColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(project.type.name, color = typeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(dateStr, color = SamlytTextMuted, fontSize = 10.sp)
          IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SamlytTextMuted, modifier = Modifier.size(16.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(project.title, color = SamlytTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
      Text(project.description, color = SamlytTextSecondary, fontSize = 12.sp, maxLines = 2, lineHeight = 16.sp)
    }
  }
}
