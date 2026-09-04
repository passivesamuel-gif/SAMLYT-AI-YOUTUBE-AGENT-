package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudioScreen
import com.example.ui.theme.SamlytBackground
import com.example.ui.theme.SamlytCard
import com.example.ui.theme.SamlytCardBorder
import com.example.ui.theme.SamlytOrange
import com.example.ui.theme.SamlytTextMuted
import com.example.ui.theme.SamlytTextSecondary

@Composable
fun BottomNavBar(
  currentScreen: StudioScreen,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(SamlytBackground)
      .navigationBarsPadding()
  ) {
    // Bottom navigation bar background
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .background(SamlytCard)
        .padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Home
      NavTabItem(
        icon = Icons.Default.GridView,
        label = "Home",
        isSelected = currentScreen == StudioScreen.HOME,
        onClick = { onNavigate(StudioScreen.HOME) }
      )

      // 2. Projects
      NavTabItem(
        icon = Icons.Default.VideoLibrary,
        label = "Projects",
        isSelected = currentScreen == StudioScreen.PROJECTS,
        onClick = { onNavigate(StudioScreen.PROJECTS) }
      )

      // 3. Spacer for Center FAB
      Box(modifier = Modifier.size(52.dp))

      // 4. Voice
      NavTabItem(
        icon = Icons.Default.GraphicEq,
        label = "Voice",
        isSelected = currentScreen == StudioScreen.VOICE_DIRECTOR,
        onClick = { onNavigate(StudioScreen.VOICE_DIRECTOR) }
      )

      // 5. Vault
      NavTabItem(
        icon = Icons.Default.Folder,
        label = "Vault",
        isSelected = currentScreen == StudioScreen.VAULT,
        onClick = { onNavigate(StudioScreen.VAULT) }
      )
    }

    // Center Orange Raised FAB (+)
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = (-14).dp)
        .size(54.dp)
        .shadow(12.dp, CircleShape, spotColor = SamlytOrange)
        .clip(CircleShape)
        .background(SamlytOrange)
        .clickable(onClick = onFabClick),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "New Creation",
        tint = Color.Black,
        modifier = Modifier.size(28.dp)
      )
    }
  }
}

@Composable
private fun NavTabItem(
  icon: ImageVector,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val tint = if (isSelected) SamlytOrange else SamlytTextMuted
  val fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal

  Column(
    modifier = Modifier
      .clip(CircleShape)
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = tint,
      modifier = Modifier.size(22.dp)
    )
    Text(
      text = label,
      color = tint,
      fontSize = 11.sp,
      fontWeight = fontWeight,
      modifier = Modifier.padding(top = 2.dp)
    )
  }
}
