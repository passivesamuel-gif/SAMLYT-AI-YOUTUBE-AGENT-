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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.model.CastMember
import com.example.data.model.DepartmentType
import com.example.data.model.GenerationType
import com.example.data.model.LocationItem
import com.example.data.model.ParsedBeat
import com.example.data.model.ProductionBible
import com.example.data.model.ProductionProposal
import com.example.data.model.ProductionStatus
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

@Composable
fun ProductionStatusBadge(status: ProductionStatus) {
  val (bgColor, textColor, borderColor) = when (status) {
    ProductionStatus.QUEUED -> Triple(Color(0xFF1E1E1E), SamlytTextMuted, Color(0xFF333333))
    ProductionStatus.ANALYZING -> Triple(Color(0xFF1A2633), SamlytCyan, SamlytCyan.copy(alpha = 0.5f))
    ProductionStatus.WORKING -> Triple(Color(0xFF262013), SamlytYellow, SamlytYellow.copy(alpha = 0.5f))
    ProductionStatus.REVIEW -> Triple(Color(0xFF261811), SamlytOrange, SamlytOrange.copy(alpha = 0.5f))
    ProductionStatus.APPROVED -> Triple(Color(0xFF132417), SamlytGreen, SamlytGreen.copy(alpha = 0.5f))
    ProductionStatus.FAILED -> Triple(Color(0xFF2B1414), Color(0xFFEF4444), Color(0xFFEF4444).copy(alpha = 0.5f))
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(999.dp))
      .background(bgColor)
      .border(1.dp, borderColor, RoundedCornerShape(999.dp))
      .padding(horizontal = 8.dp, vertical = 2.dp)
  ) {
    Text(
      text = status.label.uppercase(),
      color = textColor,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
fun DepartmentCrewSection(
  statuses: Map<DepartmentType, ProductionStatus>,
  onDepartmentClick: (DepartmentType) -> Unit = {}
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "CREW DEPARTMENTS (10 SPECIALIZED AGENTS)",
        color = SamlytTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
      Text(
        text = "Real Status Only",
        color = SamlytTextMuted,
        fontSize = 10.sp
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(DepartmentType.values()) { dept ->
        val status = statuses[dept] ?: ProductionStatus.QUEUED
        Card(
          modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(10.dp))
            .clickable { onDepartmentClick(dept) },
          colors = CardDefaults.cardColors(containerColor = SamlytCard)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = dept.displayName,
              color = SamlytTextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = dept.roleSummary,
              color = SamlytTextSecondary,
              fontSize = 9.sp,
              lineHeight = 12.sp,
              maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProductionStatusBadge(status = status)
          }
        }
      }
    }
  }
}

@Composable
fun ProductionBibleSummaryCard(
  bible: ProductionBible,
  castCount: Int,
  locationsCount: Int,
  onOpenBible: () -> Unit,
  onOpenCastLocations: () -> Unit
) {
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
            text = "ACTIVE PRODUCTION BIBLE",
            color = SamlytOrange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF222222))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text("LOCKED CONTINUITY", color = SamlytGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = bible.title,
        color = SamlytTextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Tone: ${bible.tone}",
        color = SamlytTextSecondary,
        fontSize = 12.sp
      )
      Text(
        text = "Optics: ${bible.cameraLighting}",
        color = SamlytTextSecondary,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E1E1E))
            .clickable { onOpenCastLocations() }
            .padding(8.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.People, contentDescription = null, tint = SamlytCyan, modifier = Modifier.size(16.dp))
            Column {
              Text("$castCount Cast Characters", color = SamlytTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              Text("Tokens applied", color = SamlytTextMuted, fontSize = 9.sp)
            }
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E1E1E))
            .clickable { onOpenCastLocations() }
            .padding(8.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.Place, contentDescription = null, tint = SamlytYellow, modifier = Modifier.size(16.dp))
            Column {
              Text("$locationsCount Locations", color = SamlytTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              Text("World continuity", color = SamlytTextMuted, fontSize = 9.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(onClick = onOpenBible) {
          Icon(Icons.Default.Movie, contentDescription = null, tint = SamlytOrange, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Inspect & Edit Bible Rules", color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun DocumentImportCard(
  onOpenImportDialog: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, Color(0xFF262626), RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF171717))
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
          Icon(Icons.Default.FileUpload, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(16.dp))
          Text(
            text = "DOCUMENT-IMPORTED PRODUCTIONS",
            color = SamlytGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF222222))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text("STRICT TEMPLATE", color = SamlytTextSecondary, fontSize = 9.sp)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Import Pre-Written Scene Document",
        color = SamlytTextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = "Parses per-scene Visual prompts & explicit Animation directives (Veo video vs Still image). Matches Cast continuity and presents non-skippable beat review checkpoint.",
        color = SamlytTextSecondary,
        fontSize = 12.sp,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = onOpenImportDialog,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242424)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Description, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Import & Parse Document Checkpoint", color = SamlytTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

@Composable
fun ProductionProposalsSection(
  proposals: List<ProductionProposal>,
  onApprove: (String) -> Unit,
  onReject: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "OPERATING PATTERN & VERSION PROPOSALS",
        color = SamlytTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
      Text(
        text = "Analyze → Propose → Preview → Approve",
        color = SamlytOrange,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (proposals.isEmpty()) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .border(1.dp, SamlytCardBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = SamlytCard)
      ) {
        Text(
          text = "No pending proposals. All pipeline changes approved.",
          color = SamlytTextSecondary,
          fontSize = 12.sp,
          modifier = Modifier.padding(14.dp)
        )
      }
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        proposals.take(3).forEach { proposal ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .border(1.dp, SamlytCardBorder, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = SamlytCard)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
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
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color(0xFF261D15))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(proposal.department.displayName.uppercase(), color = SamlytOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                  }
                  Text(proposal.versionTag, color = SamlytTextMuted, fontSize = 9.sp)
                }

                ProductionStatusBadge(status = proposal.status)
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(proposal.title, color = SamlytTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(2.dp))
              Text("Analysis: ${proposal.analysis}", color = SamlytTextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text("Proposal: ${proposal.proposedChange}", color = SamlytCyan, fontSize = 11.sp, lineHeight = 15.sp)

              Spacer(modifier = Modifier.height(8.dp))

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF1A1A1A))
                  .padding(8.dp)
              ) {
                Text(proposal.previewContent, color = Color(0xFFD4D4D4), fontSize = 10.sp, lineHeight = 14.sp)
              }

              if (proposal.status == ProductionStatus.REVIEW) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  TextButton(
                    onClick = { onReject(proposal.id) }
                  ) {
                    Text("Reject", color = SamlytTextMuted, fontSize = 11.sp)
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  Button(
                    onClick = { onApprove(proposal.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = SamlytGreen),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                  ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve & Apply Version", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun DocumentImportDialog(
  onDismiss: () -> Unit,
  onParse: (String) -> Unit
) {
  val sampleDoc = """Scene 1: Introduction to the Anomaly
Visual: 35mm anamorphic wide shot of rain-slicked neon street, Investigator Alex standing in shadow under streetlamp, volumetric mist, cold cyan key light
Animation: Slow push-in tracking shot toward the silhouette

Scene 2: Entering the Archive
Visual: Close-up on weathered hands examining microfiche film in Archive 7-B, Dr. Aris Thorne holding magnifying glass, amber tungsten beam

Scene 3: The Subterranean Vault
Visual: Extreme wide shot of Brutalist subterranean vault, monolithic concrete walls, green phosphor CRT monitors glowing in darkness
Animation: 4: fast orbital camera roll around center console

Scene 4: The Revelation
Visual: Silhouette of The Cipher Subject looking over city skyline from high balcony, lens flares, anamorphic streak"""

  var text by remember { mutableStateOf(sampleDoc) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF141414),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Default.FileUpload, contentDescription = null, tint = SamlytGreen)
        Text("Import Pre-Written Document", color = SamlytTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Strict Per-Scene Template Rules:\n* Visual: <image prompt> (Every scene)\n* Animation: <directive> (ONLY for scenes to animate via Veo video; otherwise defaults to still)",
          color = SamlytTextSecondary,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = text,
          onValueChange = { text = it },
          modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF181818),
            unfocusedContainerColor = Color(0xFF181818),
            focusedBorderColor = SamlytGreen,
            unfocusedBorderColor = Color(0xFF2E2E2E),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary
          ),
          shape = RoundedCornerShape(8.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onParse(text) },
        colors = ButtonDefaults.buttonColors(containerColor = SamlytGreen),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Parse & Open Review Checkpoint", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = SamlytTextMuted)
      }
    }
  )
}

@Composable
fun BeatListReviewCheckpointDialog(
  beats: List<ParsedBeat>,
  onApprove: () -> Unit,
  onReject: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onReject,
    containerColor = Color(0xFF141414),
    title = {
      Column {
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
                .background(SamlytOrange)
            )
            Text("PARSED BEAT CHECKPOINT", color = SamlytOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF261D15))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text("${beats.size} BEATS", color = SamlytOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("Review before populating production workspaces", color = SamlytTextSecondary, fontSize = 11.sp)
      }
    },
    text = {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 380.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(beats) { beat ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .border(1.dp, Color(0xFF262626), RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "SCENE ${beat.sceneNumber}",
                  color = SamlytTextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )

                // Generation Type Badge: Veo Video vs Still Frame
                val isVeo = beat.generationType == GenerationType.VIDEO_VEO
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isVeo) Color(0xFF261811) else Color(0xFF132218))
                    .border(1.dp, if (isVeo) SamlytOrange else SamlytGreen, RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                      imageVector = if (isVeo) Icons.Default.Videocam else Icons.Default.Movie,
                      contentDescription = null,
                      tint = if (isVeo) SamlytOrange else SamlytGreen,
                      modifier = Modifier.size(10.dp)
                    )
                    Text(
                      text = if (isVeo) "VEO VIDEO (ANIMATION LINE)" else "STILL FRAME (DEFAULT)",
                      color = if (isVeo) SamlytOrange else SamlytGreen,
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text("Visual: ${beat.visualPrompt}", color = SamlytTextPrimary, fontSize = 11.sp, lineHeight = 15.sp)

              if (!beat.animationPrompt.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Animation: ${beat.animationPrompt}", color = SamlytOrange, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              }

              if (beat.matchedCastNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF222222))
                    .padding(6.dp)
                ) {
                  Column {
                    Text(
                      text = "MATCHED CAST CONTINUITY: ${beat.matchedCastNames.joinToString(", ")}",
                      color = SamlytCyan,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = beat.appliedContinuityNotes,
                      color = SamlytTextSecondary,
                      fontSize = 9.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onApprove,
        colors = ButtonDefaults.buttonColors(containerColor = SamlytGreen),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Approve & Populate Workspace", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onReject) {
        Text("Reject / Edit", color = SamlytTextMuted)
      }
    }
  )
}

@Composable
fun ProductionBibleEditDialog(
  bible: ProductionBible,
  onDismiss: () -> Unit,
  onSave: (ProductionBible) -> Unit
) {
  var title by remember { mutableStateOf(bible.title) }
  var visualLanguage by remember { mutableStateOf(bible.visualLanguage) }
  var cameraLighting by remember { mutableStateOf(bible.cameraLighting) }
  var editingStyle by remember { mutableStateOf(bible.editingStyle) }
  var tone by remember { mutableStateOf(bible.tone) }
  var characterStyle by remember { mutableStateOf(bible.characterStyle) }
  var environmentStyle by remember { mutableStateOf(bible.environmentStyle) }
  var colorLanguage by remember { mutableStateOf(bible.colorLanguage) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF141414),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Default.Movie, contentDescription = null, tint = SamlytOrange)
        Text("Production Bible Rules", color = SamlytTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 420.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("Title", color = SamlytTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF181818),
            unfocusedContainerColor = Color(0xFF181818),
            focusedBorderColor = SamlytOrange,
            unfocusedBorderColor = Color(0xFF2A2A2A),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Text("Tone", color = SamlytTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
          value = tone,
          onValueChange = { tone = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF181818),
            unfocusedContainerColor = Color(0xFF181818),
            focusedBorderColor = SamlytOrange,
            unfocusedBorderColor = Color(0xFF2A2A2A),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Text("Visual Language", color = SamlytTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
          value = visualLanguage,
          onValueChange = { visualLanguage = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF181818),
            unfocusedContainerColor = Color(0xFF181818),
            focusedBorderColor = SamlytOrange,
            unfocusedBorderColor = Color(0xFF2A2A2A),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Text("Camera & Lighting", color = SamlytTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
          value = cameraLighting,
          onValueChange = { cameraLighting = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF181818),
            unfocusedContainerColor = Color(0xFF181818),
            focusedBorderColor = SamlytOrange,
            unfocusedBorderColor = Color(0xFF2A2A2A),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Text("Character Style", color = SamlytTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
          value = characterStyle,
          onValueChange = { characterStyle = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF181818),
            unfocusedContainerColor = Color(0xFF181818),
            focusedBorderColor = SamlytOrange,
            unfocusedBorderColor = Color(0xFF2A2A2A),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Text("Environment Style", color = SamlytTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
          value = environmentStyle,
          onValueChange = { environmentStyle = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF181818),
            unfocusedContainerColor = Color(0xFF181818),
            focusedBorderColor = SamlytOrange,
            unfocusedBorderColor = Color(0xFF2A2A2A),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Text("Color Language", color = SamlytTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
          value = colorLanguage,
          onValueChange = { colorLanguage = it },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF181818),
            unfocusedContainerColor = Color(0xFF181818),
            focusedBorderColor = SamlytOrange,
            unfocusedBorderColor = Color(0xFF2A2A2A),
            focusedTextColor = SamlytTextPrimary,
            unfocusedTextColor = SamlytTextPrimary
          ),
          shape = RoundedCornerShape(8.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(
            bible.copy(
              title = title,
              visualLanguage = visualLanguage,
              cameraLighting = cameraLighting,
              editingStyle = editingStyle,
              tone = tone,
              characterStyle = characterStyle,
              environmentStyle = environmentStyle,
              colorLanguage = colorLanguage
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Save Bible Rules", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = SamlytTextMuted)
      }
    }
  )
}

@Composable
fun CastContinuityDialog(
  cast: List<CastMember>,
  locations: List<LocationItem>,
  onAddCast: (String, String, String) -> Unit,
  onAddLocation: (String, String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var activeTab by remember { mutableStateOf("Cast") }
  var newName by remember { mutableStateOf("") }
  var newTokens by remember { mutableStateOf("") }
  var newExtra by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF141414),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Continuity Libraries", color = SamlytTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(999.dp))
              .background(if (activeTab == "Cast") SamlytCyan else Color(0xFF222222))
              .clickable { activeTab = "Cast" }
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text("Cast (${cast.size})", color = if (activeTab == "Cast") Color.Black else SamlytTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(999.dp))
              .background(if (activeTab == "Locations") SamlytYellow else Color(0xFF222222))
              .clickable { activeTab = "Locations" }
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text("Locations (${locations.size})", color = if (activeTab == "Locations") Color.Black else SamlytTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 380.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (activeTab == "Cast") {
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            items(cast) { member ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .border(1.dp, Color(0xFF262626), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(member.name, color = SamlytCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${member.appearances} appearances", color = SamlytTextMuted, fontSize = 9.sp)
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text("Tokens: ${member.continuityTokens}", color = SamlytTextPrimary, fontSize = 10.sp)
                  Text("Voice: ${member.voiceProfile}", color = SamlytTextSecondary, fontSize = 9.sp)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text("Add New Cast Character", color = SamlytTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            placeholder = { Text("Character Name", color = SamlytTextMuted, fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF181818),
              unfocusedContainerColor = Color(0xFF181818),
              focusedTextColor = SamlytTextPrimary,
              unfocusedTextColor = SamlytTextPrimary
            )
          )
          OutlinedTextField(
            value = newTokens,
            onValueChange = { newTokens = it },
            placeholder = { Text("Continuity Tokens (e.g. trench coat, signet ring)", color = SamlytTextMuted, fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF181818),
              unfocusedContainerColor = Color(0xFF181818),
              focusedTextColor = SamlytTextPrimary,
              unfocusedTextColor = SamlytTextPrimary
            )
          )
          Button(
            onClick = {
              if (newName.isNotBlank()) {
                onAddCast(newName, newTokens, newExtra.ifBlank { "Neutral delivery" })
                newName = ""
                newTokens = ""
                newExtra = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SamlytCyan),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Character", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        } else {
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            items(locations) { loc ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .border(1.dp, Color(0xFF262626), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text(loc.name, color = SamlytYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text("Style: ${loc.environmentStyle}", color = SamlytTextPrimary, fontSize = 10.sp)
                  Text("Continuity: ${loc.continuityNotes}", color = SamlytTextSecondary, fontSize = 9.sp)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text("Add New Location", color = SamlytTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            placeholder = { Text("Location Name", color = SamlytTextMuted, fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF181818),
              unfocusedContainerColor = Color(0xFF181818),
              focusedTextColor = SamlytTextPrimary,
              unfocusedTextColor = SamlytTextPrimary
            )
          )
          OutlinedTextField(
            value = newTokens,
            onValueChange = { newTokens = it },
            placeholder = { Text("Environment Style (e.g. monolithic walls, CRT monitors)", color = SamlytTextMuted, fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF181818),
              unfocusedContainerColor = Color(0xFF181818),
              focusedTextColor = SamlytTextPrimary,
              unfocusedTextColor = SamlytTextPrimary
            )
          )
          Button(
            onClick = {
              if (newName.isNotBlank()) {
                onAddLocation(newName, newTokens, newExtra.ifBlank { "Consistent haze density" })
                newName = ""
                newTokens = ""
                newExtra = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SamlytYellow),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Location", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Done", color = SamlytTextPrimary, fontSize = 12.sp)
      }
    }
  )
}
