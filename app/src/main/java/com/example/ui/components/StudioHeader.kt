package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SamlytCard
import com.example.ui.theme.SamlytCardBorder
import com.example.ui.theme.SamlytOrange
import com.example.ui.theme.SamlytTextMuted
import com.example.ui.theme.SamlytTextPrimary
import com.example.ui.theme.SamlytTextSecondary

@Composable
fun StudioHeader(
  title: String = "SAMLYT AI",
  subtitle: String = "HOME",
  versionBadge: String? = "V2.4",
  canGoBack: Boolean = false,
  onBackClick: (() -> Unit)? = null,
  onProfileClick: () -> Unit = {},
  onNotificationClick: () -> Unit = {}
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (canGoBack && onBackClick != null) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SamlytCard)
            .border(1.dp, SamlytCardBorder, CircleShape)
            .clickable { onBackClick() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = SamlytTextPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      } else {
        SamlytLogo(size = 32.dp)
      }

      Column {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = title,
            color = SamlytTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          if (versionBadge != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF262626))
                .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
              Text(
                text = versionBadge,
                color = SamlytTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
        Text(
          text = subtitle,
          color = SamlytTextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 1.sp
        )
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Bell Notification
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(SamlytCard)
          .border(1.dp, SamlytCardBorder, CircleShape)
          .clickable { onNotificationClick() },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Notifications,
          contentDescription = "Notifications",
          tint = SamlytTextSecondary,
          modifier = Modifier.size(18.dp)
        )
      }

      // Creator Avatar / Settings Button
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(Color(0xFFFDBA74))
          .clickable { onProfileClick() },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "Profile & Settings",
          tint = Color(0xFF431407),
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
