package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectType
import com.example.ui.theme.SamlytCard
import com.example.ui.theme.SamlytCardBorder
import com.example.ui.theme.SamlytCyan
import com.example.ui.theme.SamlytGreen
import com.example.ui.theme.SamlytOrange
import com.example.ui.theme.SamlytTextMuted
import com.example.ui.theme.SamlytTextPrimary
import com.example.ui.theme.SamlytTextSecondary
import com.example.ui.theme.SamlytYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProductionProjectCard(
  project: ProjectItem,
  isActive: Boolean,
  onSelectActive: () -> Unit,
  onOpen: () -> Unit,
  onDelete: () -> Unit
) {
  val borderColor = if (isActive) SamlytOrange else SamlytCardBorder
  val dateFormatted = rememberDate(project.timestamp)

  val statusColor = when (project.status) {
    "Script Approved" -> SamlytGreen
    "Storyboarding" -> SamlytYellow
    "Ready to Render" -> SamlytCyan
    else -> SamlytOrange
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .border(if (isActive) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
      .clickable { onSelectActive() }
      .testTag("production_project_${project.id}"),
    colors = CardDefaults.cardColors(containerColor = if (isActive) Color(0xFF1E1A16) else SamlytCard)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: status pill and active badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Channel Target Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF261D1A))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = project.channelTarget,
              color = SamlytOrange,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Production Status Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(statusColor.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = project.status,
              color = statusColor,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        if (isActive) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(SamlytGreen)
            )
            Text(
              text = "ACTIVE",
              color = SamlytGreen,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }
        } else {
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SamlytTextMuted, modifier = Modifier.size(14.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Title
      Text(
        text = project.title,
        color = SamlytTextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Subtitle: format and tone
      Text(
        text = "${project.targetDuration} * ${project.tone}",
        color = SamlytTextSecondary,
        fontSize = 11.sp
      )

      if (project.content.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = project.content.take(120).replace("\n", " ") + "...",
          color = SamlytTextMuted,
          fontSize = 11.sp,
          maxLines = 2,
          lineHeight = 15.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = dateFormatted,
          color = SamlytTextMuted,
          fontSize = 10.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (!isActive) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF242424))
                .clickable { onSelectActive() }
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Text("Set Active", color = SamlytTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(SamlytOrange)
              .clickable { onOpen() }
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text("Open Studio", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
            }
          }
        }
      }
    }
  }
}

private fun rememberDate(timestamp: Long): String {
  return try {
    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
  } catch (e: Exception) {
    "Recently"
  }
}
