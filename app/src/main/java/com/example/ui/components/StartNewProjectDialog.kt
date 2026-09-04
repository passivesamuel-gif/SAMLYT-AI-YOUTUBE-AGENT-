package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SamlytBackground
import com.example.ui.theme.SamlytCard
import com.example.ui.theme.SamlytCardBorder
import com.example.ui.theme.SamlytOrange
import com.example.ui.theme.SamlytTextMuted
import com.example.ui.theme.SamlytTextPrimary
import com.example.ui.theme.SamlytTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StartNewProjectDialog(
  onDismiss: () -> Unit,
  onCreate: (title: String, topic: String, duration: String, tone: String, channel: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var topic by remember { mutableStateOf("") }
  var selectedDuration by remember { mutableStateOf("Standard 8 - 12 min") }
  var selectedTone by remember { mutableStateOf("Cinematic") }
  var channelTarget by remember { mutableStateOf("@CineProduction") }

  val suggestions = listOf(
    "The \$100K Cinema Lens Lie",
    "Why 8K Digital Ruined Cinema",
    "The Psychological Secret of Hitchcock's Vertigo",
    "How Color Grading Tricked Your Brain"
  )

  val durations = listOf("Short 60s Reel", "Standard 8 - 12 min", "Deep Dive 20+ min")
  val tones = listOf("Cinematic", "Investigative", "Fast-Paced Hook", "Educational")

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, SamlytCardBorder, RoundedCornerShape(16.dp))
        .testTag("start_new_project_dialog"),
      colors = CardDefaults.cardColors(containerColor = SamlytCard)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SamlytOrange.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = SamlytOrange, modifier = Modifier.size(18.dp))
            }
            Column {
              Text("New YouTube Project", color = SamlytTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
              Text("Create production pipeline workspace", color = SamlytTextMuted, fontSize = 11.sp)
            }
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = SamlytTextMuted)
          }
        }

        // Title Input
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Project Title", color = SamlytTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("e.g. Why 8K Digital Ruined Cinema", color = SamlytTextMuted, fontSize = 12.sp) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("project_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF161616),
              unfocusedContainerColor = Color(0xFF161616),
              focusedBorderColor = SamlytOrange,
              unfocusedBorderColor = SamlytCardBorder,
              focusedTextColor = SamlytTextPrimary,
              unfocusedTextColor = SamlytTextPrimary
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
          )
        }

        // Quick Title Suggestions
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Ideas & Angles", color = SamlytTextMuted, fontSize = 10.sp)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            suggestions.forEach { sugg ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF222222))
                  .clickable {
                    title = sugg
                    if (topic.isBlank()) topic = "A deep dive exploring $sugg with visual breakdown"
                  }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(sugg, color = SamlytTextSecondary, fontSize = 10.sp)
              }
            }
          }
        }

        // Premise / Topic Input
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Topic & Hook Premise", color = SamlytTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            placeholder = { Text("Describe the core narrative, pacing hook, and visual tone...", color = SamlytTextMuted, fontSize = 12.sp) },
            modifier = Modifier
              .fillMaxWidth()
              .height(75.dp)
              .testTag("project_premise_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF161616),
              unfocusedContainerColor = Color(0xFF161616),
              focusedBorderColor = SamlytOrange,
              unfocusedBorderColor = SamlytCardBorder,
              focusedTextColor = SamlytTextPrimary,
              unfocusedTextColor = SamlytTextPrimary
            ),
            shape = RoundedCornerShape(8.dp)
          )
        }

        // Duration selector
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Target Duration", color = SamlytTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            durations.forEach { dur ->
              val isSel = dur == selectedDuration
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSel) Color(0xFF2D1E12) else Color(0xFF181818))
                  .border(1.dp, if (isSel) SamlytOrange else Color(0xFF2B2B2B), RoundedCornerShape(6.dp))
                  .clickable { selectedDuration = dur }
                  .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = dur.substringBefore(" "),
                  color = if (isSel) SamlytOrange else SamlytTextMuted,
                  fontSize = 11.sp,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // Tone selector
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Narrative Tone", color = SamlytTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tones.forEach { tn ->
              val isSel = tn == selectedTone
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSel) Color(0xFF2D1E12) else Color(0xFF181818))
                  .border(1.dp, if (isSel) SamlytOrange else Color(0xFF2B2B2B), RoundedCornerShape(6.dp))
                  .clickable { selectedTone = tn }
                  .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = tn,
                  color = if (isSel) SamlytOrange else SamlytTextMuted,
                  fontSize = 10.sp,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // Submit Button
        Button(
          onClick = {
            val finalTitle = title.ifBlank { topic.take(40).ifBlank { "Untitled YouTube Project" } }
            val finalTopic = topic.ifBlank { finalTitle }
            onCreate(finalTitle, finalTopic, selectedDuration, selectedTone, channelTarget)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("submit_create_project_button"),
          colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Launch Production Workspace", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
