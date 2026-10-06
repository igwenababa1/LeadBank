package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.ui.components.BankingBackgroundType
import com.example.ui.components.ClayBadge
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCard
import com.example.ui.components.ClayElevation
import com.example.ui.components.ClayIconButton
import com.example.ui.components.ClayInputContainer
import com.example.ui.components.ClayPill
import com.example.ui.components.ClayQuickAction
import com.example.ui.components.ClayVariant
import com.example.ui.components.claymorphic
import com.example.ui.components.clayPressEffect
import com.example.ui.components.InstantBtcSwapModal
import com.example.ui.components.OfficialBitcoinIcon
import com.example.ui.components.OfficialUsdIcon
import com.example.ui.components.REALISTIC_WEALTH_BACKGROUNDS
import com.example.ui.components.bankingBackground
import com.example.ui.theme.BankingTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AccountEntity
import com.example.data.model.CardEntity
import com.example.data.model.CryptoHoldingEntity
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionEntity
import com.example.ui.components.AdvertBannerCarousel
import com.example.ui.components.Card3DView
import com.example.ui.components.SpendingAnalyticsCard
import com.example.ui.components.TransactionRow
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    accounts: List<AccountEntity>,
    selectedAccountId: String?,
    cards: List<CardEntity>,
    isCardFlipped: Boolean,
    showCardDetails: Boolean,
    recentTransactions: List<TransactionEntity>,
    searchQuery: String,
    selectedCategoryFilter: TransactionCategory?,
    cryptoHoldings: List<CryptoHoldingEntity> = emptyList(),
    userName: String = "Alexander Sterling",
    userNickname: String = "Alex 'The Sovereign'",
    onUpdateNickname: (String) -> Unit = {},
    onTopUp: (Double, String) -> Unit = { _, _ -> },
    onCopyNotice: (String) -> Unit = {},
    onSelectAccount: (String) -> Unit,
    onCardFlip: () -> Unit,
    onToggleCardDetails: () -> Unit,
    onToggleCardFreeze: (String, Boolean) -> Unit,
    onCopyCardNumber: () -> Unit,
    onOpenTransfer: () -> Unit,
    onOpenWithdrawal: () -> Unit = {},
    onOpenBills: () -> Unit = {},
    onOpenDeposit: () -> Unit,
    onOpenCreateCard: () -> Unit,
    onOpenExportStatement: () -> Unit = {},
    onOpenReceipt: (TransactionEntity) -> Unit = {},
    onExecuteCryptoSwap: (fromSymbol: String, toSymbol: String, fromAmount: Double, slippage: Double, accountId: String) -> Unit = { _, _, _, _, _ -> },
    onSearchChange: (String) -> Unit,
    onCategoryFilterChange: (TransactionCategory?) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onViewAllTransfers: () -> Unit,
    onNavigateCrypto: () -> Unit = {},
    onUpgradeTier: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
    val totalNetWorth = accounts.sumOf { it.balance }
    val primaryCard = cards.firstOrNull()

    // 7 Realistic Luxury Banking Background Selection for Total Aggregated Wealth
    var selectedWealthBgId by remember { mutableStateOf("gold_vault") }

    // Instant USD ⇄ BTC Swap Sheet State
    var isInstantSwapModalOpen by remember { mutableStateOf(false) }
    val swapSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Real-time ticking date and time
    var liveDateTimeString by remember {
        mutableStateOf(formatLiveDateTime())
    }
    LaunchedEffect(Unit) {
        while (true) {
            liveDateTimeString = formatLiveDateTime()
            delay(1000)
        }
    }

    // Account Number mask/reveal state
    var isAccountNumberVisible by remember { mutableStateOf(false) }
    val rawAccountNumber = currentAccount?.accountNumber ?: "8839201948"
    val displayedAccountNumber = if (isAccountNumberVisible) {
        rawAccountNumber.chunked(4).joinToString(" ")
    } else {
        "•••• •••• " + rawAccountNumber.takeLast(4)
    }
    val routingNumber = currentAccount?.routingNumber ?: "121000358"

    // Live BTC calculation: Assume BTC live benchmark $66,450 if holdings empty
    val btcBenchmarkPrice = cryptoHoldings.find { it.symbol == "BTC" }?.currentPriceUsd ?: 66450.0
    val btcEquivalent = if (btcBenchmarkPrice > 0) totalNetWorth / btcBenchmarkPrice else 0.0
    val totalCryptoPortfolioUsd = cryptoHoldings.sumOf { it.balance * it.currentPriceUsd }

    // Dialog state for editing Nickname
    var isEditNicknameDialogOpen by remember { mutableStateOf(false) }
    var tempNickname by remember(userNickname) { mutableStateOf(userNickname) }

    if (isEditNicknameDialogOpen) {
        AlertDialog(
            onDismissRequest = { isEditNicknameDialogOpen = false },
            containerColor = Obsidian900,
            title = {
                Text(
                    text = "Edit Client Nickname",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Customize the alias displayed below the bank header across your Lead Private Banking terminal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = tempNickname,
                        onValueChange = { tempNickname = it },
                        label = { Text("Client Nickname") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = ChampagneGold,
                            unfocusedLabelColor = TextSecondary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_nickname_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateNickname(tempNickname)
                        isEditNicknameDialogOpen = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Obsidian950),
                    modifier = Modifier.testTag("save_nickname_button")
                ) {
                    Text("Save Nickname", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditNicknameDialogOpen = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .bankingBackground(BankingBackgroundType.DASHBOARD, BankingTheme.colors.isDark)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. CLIENT NICKNAME BAR: PLACED DIRECTLY BELOW HEADER
        item {
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .testTag("client_nickname_header_bar"),
                variant = ClayVariant.OBSIDIAN,
                elevation = ClayElevation.LOW,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .claymorphic(
                                    variant = ClayVariant.GOLD,
                                    shape = CircleShape,
                                    elevation = ClayElevation.LOW,
                                    isDark = BankingTheme.colors.isDark
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = Obsidian950,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CLIENT NICKNAME",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.2.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                ClayBadge(
                                    text = "SOVEREIGN LEAD",
                                    variant = ClayVariant.EMERALD,
                                    textColor = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = userNickname,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Edit Nickname Tactile 3D Button
                    ClayIconButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit Nickname",
                        onClick = {
                            tempNickname = userNickname
                            isEditNicknameDialogOpen = true
                        },
                        size = 36.dp,
                        iconSize = 16.dp
                    )
                }
            }
        }

        // 2. MASTER TOTAL AGGREGATED WEALTH CARD
        item {
            val selectedBg = REALISTIC_WEALTH_BACKGROUNDS.find { it.id == selectedWealthBgId } ?: REALISTIC_WEALTH_BACKGROUNDS.first()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        1.5.dp,
                        Brush.linearGradient(selectedBg.borderColors),
                        RoundedCornerShape(24.dp)
                    )
                    .testTag("total_aggregated_wealth_card")
            ) {
                // REALISTIC BANKING BACKGROUND IMAGE (Selected from 7 realistic styles)
                Image(
                    painter = painterResource(id = selectedBg.drawableRes),
                    contentDescription = selectedBg.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Glass scrim overlay for high contrast & clarity
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                if (BankingTheme.colors.isDark) {
                                    listOf(
                                        Color(0xFF090B12).copy(alpha = 0.70f),
                                        Color(0xFF0D101A).copy(alpha = 0.82f),
                                        Color(0xFF06070D).copy(alpha = 0.94f)
                                    )
                                } else {
                                    listOf(
                                        Color(0xFFFFFFFF).copy(alpha = 0.78f),
                                        Color(0xFFF8FAFC).copy(alpha = 0.88f),
                                        Color(0xFFF1F5F9).copy(alpha = 0.95f)
                                    )
                                }
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                // ROW 1: Profile Picture + Name + Verified Badge + Real-time Date and Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Profile Picture in Luxury Gold Border
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    Brush.linearGradient(
                                        listOf(ChampagneGold, Color(0xFF9E7C30))
                                    ),
                                    CircleShape
                                )
                                .testTag("user_profile_picture_container")
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.user_profile_avatar),
                                contentDescription = "User Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            // User's Name with Verified Badge beside it
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = userName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    maxLines = 1
                                )
                                // Premium Advanced Realistic Professional Verified Badge
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFF0284C7).copy(alpha = 0.2f))
                                        .padding(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "KYC Verified Client",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            // KYC Tier-1 Verified Pill
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "KYC Tier-1 Verified • Private Client Certified",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.5.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Net MTD Performance Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(EmeraldGreen.copy(alpha = 0.15f))
                            .border(1.dp, EmeraldGreen.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+4.8% MTD",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Realistic Professional Premium Advanced Dates & Time Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCard.copy(alpha = 0.6f))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Live Timestamp",
                            tint = ChampagneGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = liveDateTimeString,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            fontSize = 10.5.sp
                        )
                    }

                    Text(
                        text = "NY OPEN • FedNow Stream",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ROW 2: TOTAL AGGREGATED WEALTH TITLE & AMOUNT
                Text(
                    text = "TOTAL AGGREGATED WEALTH",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.8.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${"%,.2f".format(totalNetWorth)}",
                    style = MaterialTheme.typography.displayLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontSize = 34.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ROW 3: OFFICIAL BITCOIN & REAL PROFESSIONAL PREMIUM BUTTON TO SWAP US TO BTC
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                if (BankingTheme.colors.isDark) {
                                    listOf(
                                        Color(0xFF141724).copy(alpha = 0.92f),
                                        Color(0xFF1E1710).copy(alpha = 0.92f)
                                    )
                                } else {
                                    listOf(
                                        Color(0xFFFFFFFF).copy(alpha = 0.96f),
                                        Color(0xFFFFFBEB).copy(alpha = 0.96f)
                                    )
                                }
                            )
                        )
                        .border(
                            1.2.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFF7931A).copy(alpha = 0.7f),
                                    ChampagneGold.copy(alpha = 0.4f),
                                    Color(0xFF10B981).copy(alpha = 0.5f)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                        .testTag("btc_wealth_widget")
                ) {
                    // Top: Official Bitcoin Emblem + Balance + Spot Ticker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OfficialBitcoinIcon(size = 32.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "≈ ${"%,.4f".format(btcEquivalent)} BTC",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFF7931A),
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(EmeraldGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "+3.84%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldGreen
                                        )
                                    }
                                }
                                Text(
                                    text = "Spot: 1 BTC = $${"%,.0f".format(btcBenchmarkPrice)} USD • Institutional Desk",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Crypto Portfolio Total
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "PORTFOLIO",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.5.sp,
                                letterSpacing = 1.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${"%,.2f".format(totalCryptoPortfolioUsd)}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // REAL PROFESSIONAL PREMIUM CLAY BUTTON TO SWAP US TO BTC
                    ClayButton(
                        onClick = { isInstantSwapModalOpen = true },
                        variant = ClayVariant.GOLD,
                        elevation = ClayElevation.HIGH,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("swap_us_to_btc_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Official Dual Badges: US Dollar -> Bitcoin
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OfficialUsdIcon(size = 24.dp, showGlow = false)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "USD",
                                    color = Obsidian950,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = Obsidian950,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                OfficialBitcoinIcon(size = 24.dp, showGlow = false)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "BTC",
                                    color = Obsidian950,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "INSTANT SWAP",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Obsidian950,
                                    letterSpacing = 1.sp,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Instant Swap",
                                    tint = Obsidian950,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                    // ROW 4: SHOW ACCOUNT NUMBER IN DEBOSSED CLAY CONTAINER
                    ClayInputContainer(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ACCOUNT NUMBER (${currentAccount?.name ?: "Checking"})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.5.sp,
                                    letterSpacing = 1.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = displayedAccountNumber,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 13.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• Routing: $routingNumber",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Eye Toggle Button
                                ClayIconButton(
                                    icon = if (isAccountNumberVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isAccountNumberVisible) "Hide Account Number" else "Show Account Number",
                                    onClick = { isAccountNumberVisible = !isAccountNumberVisible },
                                    size = 32.dp,
                                    iconSize = 16.dp,
                                    tint = if (isAccountNumberVisible) ChampagneGold else TextSecondary,
                                    modifier = Modifier.testTag("toggle_account_number_visibility")
                                )

                                // Copy Account Number Button
                                ClayIconButton(
                                    icon = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Account Number",
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(rawAccountNumber))
                                        onCopyNotice("Account number $rawAccountNumber copied to secure clipboard.")
                                    },
                                    size = 32.dp,
                                    iconSize = 15.dp,
                                    tint = ChampagneGold,
                                    modifier = Modifier.testTag("copy_account_number_btn")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ROW 5: EXECUTIVE WEALTH ACTIONS: TRANSFER, WITHDRAWAL & BILLS
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Dedicated 3D Clay Transfer Button (Replaces Top-up Account)
                            ClayButton(
                                onClick = onOpenTransfer,
                                variant = ClayVariant.GOLD,
                                elevation = ClayElevation.MEDIUM,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("wealth_transfer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Transfer",
                                    tint = Obsidian950,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Transfer",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Obsidian950,
                                    fontSize = 12.sp
                                )
                            }

                            // Dedicated 3D Clay Withdrawal Button
                            ClayButton(
                                onClick = onOpenWithdrawal,
                                variant = ClayVariant.SURFACE,
                                elevation = ClayElevation.MEDIUM,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("wealth_withdrawal_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowOutward,
                                    contentDescription = "Withdrawal",
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Withdrawal",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 12.sp
                                )
                            }

                            // Dedicated 3D Clay Bills Button
                            ClayButton(
                                onClick = onOpenBills,
                                variant = ClayVariant.SURFACE,
                                elevation = ClayElevation.MEDIUM,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("wealth_bills_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "Bills",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Bills",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Real-time Liquidity & Quick Action Rails with 3D Clay Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quick Rails:",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = TextSecondary
                            )

                            ClayPill(
                                text = "Instant Wire",
                                onClick = onOpenTransfer
                            )
                            ClayPill(
                                text = "ATM Cash",
                                onClick = onOpenWithdrawal
                            )
                            ClayPill(
                                text = "Pay Bills",
                                onClick = onOpenBills
                            )
                        }
                    }
            }
        }
    }

        // Multi-Account Horizontal Selector
        item {
            Column {
                Text(
                    text = "ACCOUNTS & VAULTS",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(accounts) { acc ->
                        val isSelected = acc.id == selectedAccountId
                        ClayCard(
                            modifier = Modifier
                                .width(168.dp)
                                .testTag("account_chip_${acc.id}"),
                            variant = if (isSelected) ClayVariant.OBSIDIAN else ClayVariant.SURFACE,
                            elevation = if (isSelected) ClayElevation.HIGH else ClayElevation.MEDIUM,
                            shape = RoundedCornerShape(20.dp),
                            onClick = { onSelectAccount(acc.id) }
                        ) {
                            Column(modifier = Modifier.padding(15.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = acc.type.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) ChampagneGold else TextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldGreen)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = acc.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$${"%,.0f".format(acc.balance)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) ChampagneGold else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Send,
                    label = "Send Wire",
                    onClick = onOpenTransfer,
                    isPrimary = true,
                    modifier = Modifier.weight(1f),
                    testTag = "dashboard_action_transfer"
                )

                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Deposit",
                    onClick = onOpenDeposit,
                    isPrimary = false,
                    modifier = Modifier.weight(1f),
                    testTag = "dashboard_action_deposit"
                )

                QuickActionButton(
                    icon = Icons.Default.Description,
                    label = "Statements",
                    onClick = onOpenExportStatement,
                    isPrimary = false,
                    modifier = Modifier.weight(1f),
                    testTag = "dashboard_action_statements"
                )

                QuickActionButton(
                    icon = Icons.Default.AddCard,
                    label = "New Card",
                    onClick = onOpenCreateCard,
                    isPrimary = false,
                    modifier = Modifier.weight(1f),
                    testTag = "dashboard_action_new_card"
                )
            }
        }

        // Realistic Premium Banking Advert Banners (5.40% Yield, Institutional Crypto Desk & Private Tiers)
        item {
            AdvertBannerCarousel(
                onOpenDeposit = onOpenDeposit,
                onNavigateCrypto = onNavigateCrypto,
                onUpgradeTier = onUpgradeTier
            )
        }

        // 3D Card Interactive Preview Section
        if (primaryCard != null) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRIMARY PAYMENT INSTRUMENT",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Tap to Flip",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Card3DView(
                        card = primaryCard,
                        isFlipped = isCardFlipped,
                        showDetails = showCardDetails,
                        onFlipClick = onCardFlip,
                        onToggleDetails = onToggleCardDetails,
                        onToggleFreeze = { onToggleCardFreeze(primaryCard.id, primaryCard.isFrozen) },
                        onCopyNumber = onCopyCardNumber,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Spending Analytics Chart
        item {
            val totalSpentThisMonth = recentTransactions
                .filter { it.amount < 0 }
                .sumOf { kotlin.math.abs(it.amount) }
                .let { if (it > 0) it else 4280.0 }

            val totalIncomeThisMonth = recentTransactions
                .filter { it.amount > 0 }
                .sumOf { it.amount }
                .let { if (it > 0) it else 18500.0 }

            SpendingAnalyticsCard(
                totalSpentThisMonth = totalSpentThisMonth,
                totalIncomeThisMonth = totalIncomeThisMonth,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Transactions Feed Header & Search
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT LEDGER ACTIVITY",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.2.sp,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ChampagneGold.copy(alpha = 0.12f))
                                .clickable { onOpenExportStatement() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("dashboard_export_statement_pill")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Export Statement",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onViewAllTransfers() }
                        ) {
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontSize = 11.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                ClayInputContainer(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchChange,
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = TextPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(ChampagneGold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .testTag("dashboard_search_input"),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search transactions, merchants, wires...",
                                        color = TextSecondary,
                                        fontSize = 13.5.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterPill(
                            label = "All",
                            isSelected = selectedCategoryFilter == null,
                            onClick = { onCategoryFilterChange(null) }
                        )
                    }
                    items(TransactionCategory.values()) { cat ->
                        FilterPill(
                            label = cat.displayName,
                            isSelected = selectedCategoryFilter == cat,
                            onClick = { onCategoryFilterChange(if (selectedCategoryFilter == cat) null else cat) }
                        )
                    }
                }
            }
        }

        // Transactions List
        if (recentTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found matching your criteria",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(recentTransactions.take(8)) { tx ->
                TransactionRow(
                    transaction = tx,
                    onClick = { onTransactionClick(tx) },
                    onReceiptClick = { onOpenReceipt(tx) }
                )
            }
        }

        // Security Assurance Footer
        item {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                variant = ClayVariant.OBSIDIAN,
                elevation = ClayElevation.LOW,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .claymorphic(
                                variant = ClayVariant.GOLD,
                                shape = CircleShape,
                                elevation = ClayElevation.LOW,
                                isDark = BankingTheme.colors.isDark
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Obsidian950,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Institutional-Grade Custody",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Deposits insured up to $5,000,000 through our partner bank sweep network. 256-bit AES encryption.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Official Instant USD ⇄ BTC Swap Bottom Sheet
    if (isInstantSwapModalOpen) {
        InstantBtcSwapModal(
            accounts = accounts,
            selectedAccountId = selectedAccountId,
            cryptoHoldings = cryptoHoldings,
            sheetState = swapSheetState,
            onDismiss = { isInstantSwapModalOpen = false },
            onExecuteSwap = { fromSymbol, toSymbol, fromAmount, slippage, accountId ->
                onExecuteCryptoSwap(fromSymbol, toSymbol, fromAmount, slippage, accountId)
            }
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .clayPressEffect(onClick = onClick)
            .claymorphic(
                variant = if (isPrimary) ClayVariant.GOLD else ClayVariant.SURFACE,
                shape = RoundedCornerShape(18.dp),
                elevation = ClayElevation.MEDIUM,
                isDark = BankingTheme.colors.isDark
            )
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isPrimary) Obsidian950 else ChampagneGold,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (isPrimary) Obsidian950 else TextPrimary,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ClayPill(
        text = label,
        isSelected = isSelected,
        onClick = onClick
    )
}

private fun formatLiveDateTime(): String {
    val formatter = SimpleDateFormat("EEEE, MMM dd, yyyy • hh:mm:ss a", Locale.US)
    return formatter.format(Date())
}
