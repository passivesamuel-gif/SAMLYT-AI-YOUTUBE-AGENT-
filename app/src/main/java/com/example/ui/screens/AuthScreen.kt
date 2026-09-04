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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.ui.theme.SamlytGreen
import com.example.ui.theme.SamlytOrange
import com.example.ui.theme.SamlytTextMuted
import com.example.ui.theme.SamlytTextPrimary
import com.example.ui.theme.SamlytTextSecondary

@Composable
fun AuthScreen(
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  var email by remember { mutableStateOf("alex.creator@studio.samlyt.ai") }
  var otpDigits by remember { mutableStateOf(listOf("7", "4", "9", "2", "5", "8")) }
  var isCodeSent by remember { mutableStateOf(true) }

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "SAMLYT AI",
        subtitle = "AUTH NODE 01",
        versionBadge = "V2.4",
        canGoBack = true,
        onBackClick = { onNavigate(StudioScreen.LANDING) },
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.AUTH,
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
        Spacer(modifier = Modifier.height(10.dp))
        SamlytLogo(size = 76.dp, isLiveAnimated = true)
      }

      item {
        Text(
          text = "Welcome to SAMLYT",
          color = SamlytTextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Sign in to access your creative workspace and encrypted production vault.",
          color = SamlytTextSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Center
        )
      }

      // Google Sign In (NO Apple, NO iCloud)
      item {
        Button(
          onClick = { onNavigate(StudioScreen.HOME) },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFEA4335)),
              contentAlignment = Alignment.Center
            ) {
              Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "Continue with Google",
              color = Color(0xFF1F2937),
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      // Divider
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          HorizontalDivider(modifier = Modifier.weight(1f), color = SamlytCardBorder)
          Text(
            text = "OR CONTINUE WITH EMAIL",
            color = SamlytTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp),
            letterSpacing = 1.sp
          )
          HorizontalDivider(modifier = Modifier.weight(1f), color = SamlytCardBorder)
        }
      }

      // Email Input
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Email Address",
            color = SamlytTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
          )
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
              Icon(Icons.Default.Email, contentDescription = null, tint = SamlytTextMuted)
            },
            trailingIcon = {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SamlytGreen)
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = SamlytCard,
              unfocusedContainerColor = SamlytCard,
              focusedBorderColor = SamlytOrange,
              unfocusedBorderColor = SamlytCardBorder,
              focusedTextColor = SamlytTextPrimary,
              unfocusedTextColor = SamlytTextPrimary
            ),
            singleLine = true
          )
        }
      }

      // 6-digit OTP section
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SamlytCard)
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = SamlytOrange,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "Enter 6-Digit OTP Code",
                color = SamlytTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
            Text(
              text = "Secure",
              color = SamlytGreen,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 6 digit boxes
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            otpDigits.forEachIndexed { index, digit ->
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF1E1E1E))
                  .border(
                    1.dp,
                    if (index == 2) SamlytOrange else Color(0xFF333333),
                    RoundedCornerShape(8.dp)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = digit,
                  color = SamlytTextPrimary,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Resend code (0:45)",
              color = SamlytTextMuted,
              fontSize = 11.sp
            )
            Text(
              text = "Change email",
              color = SamlytOrange,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.clickable { }
            )
          }
        }
      }

      // Verify button
      item {
        Button(
          onClick = { onNavigate(StudioScreen.HOME) },
          colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        ) {
          Text(
            text = "Verify & Continue",
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
      }

      item {
        Text(
          text = "By continuing, you agree to SAMLYT Studio Terms of Service and Hardware-Enclave Privacy Protocol.",
          color = SamlytTextMuted,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          lineHeight = 15.sp,
          modifier = Modifier.padding(bottom = 24.dp)
        )
      }
    }
  }
}
