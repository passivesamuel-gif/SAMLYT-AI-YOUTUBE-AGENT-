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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun LandingScreen(
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "SAMLYT AI",
        subtitle = "PRODUCTION STUDIO",
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.LANDING,
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
        Spacer(modifier = Modifier.height(8.dp))
        // Central glowing hero logo
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          SamlytLogo(size = 96.dp, isLiveAnimated = true)
        }
      }

      item {
        // Engine version badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF1F1B14))
            .border(1.dp, Color(0x55F97316), RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(SamlytOrange)
            )
            Text(
              text = "STUDIO ENGINE V2.4",
              color = SamlytOrange,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }
        }
      }

      item {
        Text(
          text = "Your AI Creative Partner.",
          color = SamlytTextPrimary,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "An integrated studio built for creators to research, write, and direct high-impact video from a single command center.",
          color = SamlytTextSecondary,
          fontSize = 14.sp,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp,
          modifier = Modifier.padding(horizontal = 12.dp)
        )
      }

      item {
        // Action buttons
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = { onNavigate(StudioScreen.ONBOARDING) },
            colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
          ) {
            Text(
              text = "Get Started",
              color = Color.Black,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(18.dp)
            )
          }

          Button(
            onClick = { onNavigate(StudioScreen.AUTH) },
            colors = ButtonDefaults.buttonColors(containerColor = SamlytCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp))
          ) {
            Text(
              text = "Sign In",
              color = SamlytTextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(12.dp))
        // Section title
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CORE CAPABILITIES",
            color = SamlytTextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
          )
          Text(
            text = "5 MODULES ACTIVE",
            color = SamlytGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      item {
        CapabilityCard(
          icon = Icons.Default.Search,
          title = "Research & Ideation",
          desc = "Deep synthesis across topic archives and creator trends.",
          tag = "RESEARCH",
          accentColor = SamlytCyan,
          onClick = { onNavigate(StudioScreen.RESEARCH) }
        )
      }

      item {
        CapabilityCard(
          icon = Icons.Default.Description,
          title = "Script Architecture",
          desc = "Precision pacing, hook construction, and scene breakdowns.",
          tag = "CINESCRIPT 4K",
          accentColor = SamlytOrange,
          onClick = { onNavigate(StudioScreen.SCRIPT_STUDIO) }
        )
      }

      item {
        CapabilityCard(
          icon = Icons.Default.CameraAlt,
          title = "Visual Pre-Visualization",
          desc = "Shot lists, visual references, and cinematic prompts.",
          tag = "PHOTOREAL 8K",
          accentColor = SamlytGreen,
          onClick = { onNavigate(StudioScreen.STORYBOARD) }
        )
      }

      item {
        CapabilityCard(
          icon = Icons.Default.Image,
          title = "Thumbnail Studio",
          desc = "Packaging concepts and visual framing analysis.",
          tag = "CTR MATRIX",
          accentColor = Color(0xFFF59E0B),
          onClick = { onNavigate(StudioScreen.THUMBNAIL_STUDIO) }
        )
      }

      item {
        CapabilityCard(
          icon = Icons.Default.Mic,
          title = "Conversational Director",
          desc = "Low-latency natural voice collaboration via microphone.",
          tag = "NEURAL VOICE",
          accentColor = Color(0xFFA855F7),
          onClick = { onNavigate(StudioScreen.VOICE_DIRECTOR) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "Designed for solo directors and professional video creators.",
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
fun CapabilityCard(
  icon: ImageVector,
  title: String,
  desc: String,
  tag: String,
  accentColor: Color,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp))
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = SamlytCard),
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = accentColor,
          modifier = Modifier.size(22.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = title,
            color = SamlytTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF222222))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = tag,
              color = SamlytTextMuted,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = desc,
          color = SamlytTextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )
      }
    }
  }
}
