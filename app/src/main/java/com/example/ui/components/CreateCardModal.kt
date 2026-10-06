package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.CardNetwork
import com.example.data.model.CardTheme
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class BankPreset(
    val bankName: String,
    val productName: String,
    val theme: CardTheme,
    val defaultNetwork: CardNetwork,
    val initialLimit: Double,
    val iconColor: Color,
    val binPrefix: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCardModal(
    accounts: List<AccountEntity>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onLinkOfficialBankCard: (
        bankName: String,
        productName: String,
        cardholderName: String,
        cardNumber: String,
        expiryDate: String,
        cvv: String,
        theme: CardTheme,
        network: CardNetwork,
        spendingLimit: Double,
        linkedAccountId: String
    ) -> Unit,
    onCreateLeadVirtualCard: (
        theme: CardTheme,
        network: CardNetwork,
        limit: Double,
        linkedAccountId: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Link Official Bank Card, 1 = Issue Lead Virtual Card

    val bankPresets = listOf(
        BankPreset("JPMorgan Chase", "Sapphire Reserve Metal", CardTheme.CHASE_SAPPHIRE, CardNetwork.VISA_INFINITE, 35000.0, Color(0xFF0D2D5E), "411289304910"),
        BankPreset("American Express", "Platinum Centurion", CardTheme.AMEX_PLATINUM, CardNetwork.AMEX, 100000.0, Color(0xFFCED4DA), "378282246311"),
        BankPreset("Goldman Sachs", "Apple Card Titanium", CardTheme.APPLE_TITANIUM, CardNetwork.MASTERCARD_WORLD_ELITE, 25000.0, Color(0xFFECEFF1), "541275990214"),
        BankPreset("Citigroup", "Custom Cash Elite", CardTheme.CITI_CUSTOM, CardNetwork.MASTERCARD_WORLD_ELITE, 20000.0, Color(0xFF003B70), "542418029311"),
        BankPreset("Capital One", "Venture X Rewards", CardTheme.CAPITAL_ONE_VENTURE, CardNetwork.VISA_INFINITE, 30000.0, Color(0xFF8B1E1E), "400321894012"),
        BankPreset("Barclays Wealth", "Black Card Heavy", CardTheme.BARCLAYS_BLACK, CardNetwork.MASTERCARD_WORLD_ELITE, 50000.0, Color(0xFF1E293B), "552199042183"),
        BankPreset("Lead Private Bank", "Lead Obsidian Reserve", CardTheme.OBSIDIAN, CardNetwork.VISA_INFINITE, 50000.0, ChampagneGold, "453289014418")
    )

    // Link Bank Form State
    var selectedPreset by remember { mutableStateOf(bankPresets.first()) }
    var cardholderName by remember { mutableStateOf("ALEXANDER VANCE") }
    var cardNumberInput by remember { mutableStateOf(selectedPreset.binPrefix + "4210") }
    var expiryInput by remember { mutableStateOf("05/29") }
    var cvvInput by remember { mutableStateOf("402") }
    var spendingLimit by remember { mutableFloatStateOf(selectedPreset.initialLimit.toFloat()) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "acc_checking") }

    // Instant linking loading feedback
    var isVerifyingWithBank by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPreset) {
        val last4 = (1000..9999).random().toString()
        cardNumberInput = selectedPreset.binPrefix + last4
        spendingLimit = selectedPreset.initialLimit.toFloat()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BankingTheme.colors.backgroundElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BankingTheme.colors.border)
            )
        },
        modifier = modifier.testTag("create_card_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CARDS & MULTI-BANK LIQUIDITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampagneGold,
                        letterSpacing = 1.2.sp,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Card Issuance & Link Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Tabs: Link Official Bank Card vs Issue Lead Virtual Card
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceCard,
                contentColor = ChampagneGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ChampagneGold
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Link Real Bank Card",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) ChampagneGold else TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Issue Lead Virtual Card",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) ChampagneGold else TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (selectedTab == 0) {
                // Link Official Bank Card Flow
                Text(
                    text = "Select Official Bank Institution",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(bankPresets) { preset ->
                        val isSelected = selectedPreset.bankName == preset.bankName
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SurfaceCardElevated else SurfaceCard)
                                .border(1.2.dp, if (isSelected) ChampagneGold else SurfaceBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedPreset = preset }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(preset.iconColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = preset.bankName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) ChampagneGold else TextPrimary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = preset.productName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card number and info inputs
                OutlinedTextField(
                    value = cardNumberInput,
                    onValueChange = { if (it.all { c -> c.isDigit() }) cardNumberInput = it },
                    label = { Text("Official Card Number (15-16 Digits)") },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("official_card_number_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = expiryInput,
                        onValueChange = { expiryInput = it },
                        label = { Text("Expires (MM/YY)") },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = TextPrimary),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard
                        )
                    )

                    OutlinedTextField(
                        value = cvvInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) cvvInput = it },
                        label = { Text("Security CVV") },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = TextPrimary),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real-time settlement account link
                Text(
                    text = "Settlement & Funding Account in Lead",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(accounts) { acc ->
                        val isSelected = selectedAccountId == acc.id
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SurfaceCardElevated else SurfaceCard)
                                .border(1.dp, if (isSelected) EmeraldGreen else SurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedAccountId = acc.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = if (isSelected) EmeraldGreen else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${acc.name} ($${"%,.0f".format(acc.balance)})",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bank Security Verification Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldGreen.copy(alpha = 0.12f))
                        .border(1.dp, EmeraldGreen.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encrypted Tokenization: Real-time bilateral transaction sync enabled with ${selectedPreset.bankName}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldGreen,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        isVerifyingWithBank = true
                    },
                    enabled = cardNumberInput.length >= 15 && expiryInput.isNotBlank() && cvvInput.isNotBlank() && !isVerifyingWithBank,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("link_bank_card_confirm_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = Obsidian950
                    )
                ) {
                    if (isVerifyingWithBank) {
                        CircularProgressIndicator(
                            color = Obsidian950,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Verifying with ${selectedPreset.bankName}...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Link ${selectedPreset.bankName} Card Now",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isVerifyingWithBank) {
                    LaunchedEffect(Unit) {
                        delay(1200)
                        onLinkOfficialBankCard(
                            selectedPreset.bankName,
                            selectedPreset.productName,
                            cardholderName,
                            cardNumberInput,
                            expiryInput,
                            cvvInput,
                            selectedPreset.theme,
                            selectedPreset.defaultNetwork,
                            spendingLimit.toDouble(),
                            selectedAccountId
                        )
                    }
                }
            } else {
                // Issue Lead Virtual Card Flow
                Text(
                    text = "Select Metallic Finish & Tier",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                var selectedVirtualTheme by remember { mutableStateOf(CardTheme.OBSIDIAN) }
                var selectedNetwork by remember { mutableStateOf(CardNetwork.VISA_INFINITE) }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        listOf(
                            Pair(CardTheme.OBSIDIAN, "Obsidian Black"),
                            Pair(CardTheme.CHASE_SAPPHIRE, "Sapphire Royal"),
                            Pair(CardTheme.GOLD, "Bullion Gold"),
                            Pair(CardTheme.AMEX_PLATINUM, "Platinum Heavy"),
                            Pair(CardTheme.EMERALD, "Emerald Malachite"),
                            Pair(CardTheme.APPLE_TITANIUM, "Aerospace Titanium")
                        )
                    ) { (theme, label) ->
                        val isSelected = selectedVirtualTheme == theme
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(105.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SurfaceCardElevated else SurfaceCard)
                                .border(1.5.dp, if (isSelected) ChampagneGold else SurfaceBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedVirtualTheme = theme }
                                .padding(vertical = 12.dp, horizontal = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (theme) {
                                            CardTheme.OBSIDIAN -> Color(0xFF10141D)
                                            CardTheme.CHASE_SAPPHIRE -> Color(0xFF0F2E5C)
                                            CardTheme.GOLD -> Color(0xFF8C6D27)
                                            CardTheme.AMEX_PLATINUM -> Color(0xFF94A3B8)
                                            CardTheme.EMERALD -> Color(0xFF0E4A3B)
                                            else -> Color(0xFFE2E8F0)
                                        }
                                    )
                                    .border(1.dp, Color(0x66FFFFFF), CircleShape)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) ChampagneGold else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monthly Spending Cap",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "$${"%,.0f".format(spendingLimit)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                }

                Slider(
                    value = spendingLimit,
                    onValueChange = { spendingLimit = it },
                    valueRange = 1000f..50000f,
                    steps = 49,
                    colors = SliderDefaults.colors(
                        thumbColor = ChampagneGold,
                        activeTrackColor = ChampagneGold,
                        inactiveTrackColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onCreateLeadVirtualCard(
                            selectedVirtualTheme,
                            selectedNetwork,
                            spendingLimit.toDouble(),
                            selectedAccountId
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("issue_lead_card_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = Obsidian950
                    )
                ) {
                    Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate Lead Token Card",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
