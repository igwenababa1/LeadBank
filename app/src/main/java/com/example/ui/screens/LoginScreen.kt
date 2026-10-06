package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.auth.HolographicBiometricScanner
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCard
import com.example.ui.components.ClayElevation
import com.example.ui.components.ClayInputContainer
import com.example.ui.components.ClayVariant
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackToWelcome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var clientIdInput by remember { mutableStateOf("alexander.sterling@lead.bank") }
    var passkeyInput by remember { mutableStateOf("••••••••••") }
    var isPasskeyVisible by remember { mutableStateOf(false) }
    var isAuthenticating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isConciergeDialogOpen by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "login_ambient")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_glow"
    )

    if (isConciergeDialogOpen) {
        AlertDialog(
            onDismissRequest = { isConciergeDialogOpen = false },
            containerColor = BankingTheme.colors.surfaceCard,
            title = {
                Text(
                    text = "Sovereign Private Banker Desk",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "For emergency credential recovery or hardware passkey replacement, connect directly with your dedicated Senior Private Banker:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Private Desk: +1 (800) 532-3882\n• Sovereign Hotline: desk.vip@lead.bank\n• Encrypted Signal: @lead.private.concierge",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { isConciergeDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Obsidian950)
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BankingTheme.colors.background)
    ) {
        // Luxury Swiss Banking Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_swiss_vault_1790889776583),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Glass Scrim Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        if (BankingTheme.colors.isDark) {
                            listOf(
                                Color(0xFF07090E).copy(alpha = 0.85f),
                                Color(0xFF0D111A).copy(alpha = 0.92f),
                                Color(0xFF05070A).copy(alpha = 0.98f)
                            )
                        } else {
                            listOf(
                                Color(0xFFFFFFFF).copy(alpha = 0.88f),
                                Color(0xFFF8FAFC).copy(alpha = 0.94f),
                                Color(0xFFF1F5F9).copy(alpha = 0.98f)
                            )
                        }
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Nav Row: Back to Welcome + Security Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToWelcome,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BankingTheme.colors.surfaceCard)
                        .testTag("login_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BankingTheme.colors.surfaceCard)
                        .border(1.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "256-Bit HSM Protected",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Gold Monogram Crest with breathing halo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(ChampagneGold.copy(alpha = pulseAlpha * 0.45f), Color.Transparent)
                        )
                    )
                    .border(
                        2.dp,
                        Brush.linearGradient(listOf(ChampagneGold, Color(0xFF9E7C30))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_lead_logo),
                    contentDescription = "Lead Emblem",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SOVEREIGN CLIENT LOGIN",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.6.sp,
                fontWeight = FontWeight.Bold,
                color = ChampagneGold,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Unlock Private Vault",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Authenticate via Biometric Passkey or Master Key credentials",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // PRIMARY ATTRACTION: HOLOGRAPHIC BIOMETRIC UNLOCK CARD
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("biometric_login_card"),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.HIGH,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .border(1.5.dp, ChampagneGold.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRIMARY PASSKEY",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            letterSpacing = 1.2.sp,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Biometric Sensor Active",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Holographic Biometric Scanner
                    HolographicBiometricScanner(
                        onAuthenticated = {
                            onLoginSuccess()
                        },
                        size = 106.dp,
                        title = "Touch Fingerprint / FaceID",
                        subtitle = "Hardware-backed cryptographic unlock",
                        modifier = Modifier.testTag("login_biometric_scanner")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // OR DIVIDER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(BankingTheme.colors.border)
                )
                Text(
                    text = "  OR USE MASTER PASSKEY  ",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.5.sp,
                    letterSpacing = 1.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(BankingTheme.colors.border)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CREDENTIALS INPUT FORM CARD
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.LOW,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Client Identifier
                    Text(
                        text = "CLIENT IDENTIFIER / EMAIL",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = clientIdInput,
                        onValueChange = { clientIdInput = it },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                        },
                        placeholder = { Text("Client identifier", color = TextSecondary) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = BankingTheme.colors.border,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_client_id_input")
                    )

                    // Master Secret Passkey
                    Text(
                        text = "MASTER SECRET PASSKEY",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = passkeyInput,
                        onValueChange = { passkeyInput = it },
                        singleLine = true,
                        visualTransformation = if (isPasskeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(Icons.Default.Key, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasskeyVisible = !isPasskeyVisible }) {
                                Icon(
                                    imageVector = if (isPasskeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Visibility",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = BankingTheme.colors.border,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_passkey_input")
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFF43F5E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SUBMIT LOGIN BUTTON (3D Clay)
            ClayButton(
                onClick = {
                    if (clientIdInput.isNotBlank() && passkeyInput.isNotBlank()) {
                        isAuthenticating = true
                        onLoginSuccess()
                    } else {
                        errorMessage = "Please enter valid client credentials"
                    }
                },
                variant = ClayVariant.GOLD,
                elevation = ClayElevation.HIGH,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_login_button")
            ) {
                if (isAuthenticating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Obsidian950,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Authenticating Vault...", fontWeight = FontWeight.Bold, color = Obsidian950, fontSize = 14.sp)
                } else {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Obsidian950, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authorize Vault Entrance", fontWeight = FontWeight.ExtraBold, color = Obsidian950, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Obsidian950, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1-TAP DEMO BYPASS BUTTON (Alexander Sterling)
            Button(
                onClick = onLoginSuccess,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BankingTheme.colors.surfaceCard,
                    contentColor = ChampagneGold
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .testTag("quick_login_bypass_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quick 1-Tap Client Access (Alexander Sterling)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Concierge Direct Recovery Link
            TextButton(
                onClick = { isConciergeDialogOpen = true },
                modifier = Modifier.testTag("login_concierge_btn")
            ) {
                Text(
                    text = "Lost Passkey? Contact Sovereign Private Banker Desk",
                    color = TextSecondary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
