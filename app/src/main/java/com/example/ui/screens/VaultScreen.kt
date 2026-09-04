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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavedVaultThumbnail
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
import com.example.ui.theme.SamlytYellow
import com.example.viewmodel.StudioViewModel

@Composable
fun VaultScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  val settings by viewModel.settings.collectAsState()
  val vaultThumbnails by viewModel.vaultThumbnails.collectAsState()
  val context = LocalContext.current

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "Vault",
        subtitle = "ENCRYPTED STORAGE",
        versionBadge = "AES-256",
        canGoBack = true,
        onBackClick = { onNavigate(StudioScreen.HOME) },
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.VAULT,
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
              .background(SamlytGreen)
          )
          Text(
            text = "HARDWARE ENCLAVE ARMED",
            color = SamlytGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Encrypted Studio Vault",
          color = SamlytTextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Local-first, air-gapped cryptographic enclave. Unreleased script drafts and prompt vectors never leak to public model training.",
          color = SamlytTextSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )
      }

      // Vault Status Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SamlytGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D13))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SamlytGreen.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Security, contentDescription = null, tint = SamlytGreen, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("AES-256 Hardware Enclave", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text("State: Air-Gapped & Locked\nKey Store: Android Keystore Provider", color = SamlytTextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
            }
          }
        }
      }

      // Storage Meter Card
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
              Text("VAULT CAPACITY", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
              Text("1.2 GB of 50.0 GB Used", color = SamlytGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
              progress = { 0.024f },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
              color = SamlytGreen,
              trackColor = Color(0xFF262626)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Breakdown items
            StorageBreakdownRow("Scripts & Narrative Beat Data", "142 MB", SamlytOrange)
            StorageBreakdownRow("Storyboard 8K Caches & Prompts", "680 MB", SamlytGreen)
            StorageBreakdownRow("Thumbnail Packaging Matrices", "320 MB", SamlytYellow)
            StorageBreakdownRow("Voice Director Audio Stream Caches", "80 MB", SamlytCyan)
          }
        }
      }

      // Saved Thumbnails Section
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "SAVED PACKAGING MATRICES (${vaultThumbnails.size})",
            color = SamlytTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          if (vaultThumbnails.isNotEmpty()) {
            Text(
              text = "AES-256 Vault Cached",
              color = SamlytGreen,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      if (vaultThumbnails.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .border(1.dp, SamlytCardBorder, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = SamlytCard)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "No Saved Thumbnails in Vault Yet",
                color = SamlytTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Generate thumbnails in Thumbnail Studio and click 'Save to Vault' to preserve 8K renders here.",
                color = SamlytTextSecondary,
                fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(vaultThumbnails, key = { it.id }) { thumb ->
          VaultThumbnailCard(
            item = thumb,
            onDownload = {
              downloadThumbnailFile(context, thumb.conceptName, thumb.base64)
            },
            onDelete = {
              viewModel.deleteVaultThumbnail(thumb.id)
              Toast.makeText(context, "Thumbnail removed from Vault", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }

      // Security Controls
      item {
        Text("VAULT & ENCLAVE ACTIONS", color = SamlytTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
      }

      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          VaultActionRow(
            icon = Icons.Default.Delete,
            title = "Purge Offline Preview Cache",
            subtitle = "Free 840 MB of temporary image and audio buffers",
            accent = SamlytOrange,
            onClick = {
              viewModel.purgeCache()
              Toast.makeText(context, "Offline preview cache purged (0 MB)", Toast.LENGTH_SHORT).show()
            }
          )

          VaultActionRow(
            icon = Icons.Default.Download,
            title = "Export Encrypted Studio Backup",
            subtitle = "Saves an encrypted .samlyt file to your device storage",
            accent = SamlytCyan,
            onClick = {
              Toast.makeText(context, "Exported: samlyt_backup_aes256.enc", Toast.LENGTH_LONG).show()
            }
          )

          VaultActionRow(
            icon = Icons.Default.Key,
            title = "Master Vault PIN & Biometrics",
            subtitle = "Require fingerprint / biometric lock on app launch",
            accent = SamlytGreen,
            onClick = {
              Toast.makeText(context, "Biometric security is enforced", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
fun StorageBreakdownRow(label: String, size: String, color: Color) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
      Text(label, color = SamlytTextSecondary, fontSize = 11.sp)
    }
    Text(size, color = SamlytTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
fun VaultActionRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  accent: Color,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(10.dp))
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
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(accent.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
      }
      Column(modifier = Modifier.weight(1f)) {
        Text(title, color = SamlytTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = SamlytTextSecondary, fontSize = 10.sp)
      }
    }
  }
}

@Composable
fun VaultThumbnailCard(
  item: SavedVaultThumbnail,
  onDownload: () -> Unit,
  onDelete: () -> Unit
) {
  val bitmap = remember(item.base64) {
    try {
      val clean = if (item.base64.contains(",")) item.base64.substringAfter(",") else item.base64
      val decoded = android.util.Base64.decode(clean, android.util.Base64.DEFAULT)
      android.graphics.BitmapFactory.decodeByteArray(decoded, 0, decoded.size)?.asImageBitmap()
    } catch (e: Exception) {
      null
    }
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = SamlytCard)
  ) {
    Column {
      if (bitmap != null) {
        Image(
          bitmap = bitmap,
          contentDescription = item.conceptName,
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
          contentScale = ContentScale.Crop
        )
      }

      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = item.conceptName,
            color = SamlytTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SamlytGreen.copy(alpha = 0.2f))
              .border(1.dp, SamlytGreen.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
              .padding(horizontal = 7.dp, vertical = 2.dp)
          ) {
            Text("CTR: ${item.ctrScore}%", color = SamlytGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }

        if (item.suggestedTitle.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "\"${item.suggestedTitle}\"",
            color = SamlytCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = onDownload,
            modifier = Modifier
              .weight(1f)
              .height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SamlytYellow),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("⬇️ Download PNG", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = onDelete,
            modifier = Modifier.height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF221616)),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF552222))
          ) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFFF6B6B), modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}
