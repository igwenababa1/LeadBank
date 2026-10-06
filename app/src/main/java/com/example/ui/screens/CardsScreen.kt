package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VerifiedUser
import com.example.ui.components.BankingBackgroundType
import com.example.ui.components.ClayBadge
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCard
import com.example.ui.components.ClayElevation
import com.example.ui.components.ClaySwitch
import com.example.ui.components.ClayVariant
import com.example.ui.components.bankingBackground
import com.example.ui.components.claymorphic
import com.example.ui.theme.BankingTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CardEntity
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionEntity
import com.example.ui.components.Card3DView
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

@Composable
fun CardsScreen(
    cards: List<CardEntity>,
    selectedCardIndex: Int,
    isCardFlipped: Boolean,
    showCardDetails: Boolean,
    allTransactions: List<TransactionEntity> = emptyList(),
    onSelectCardIndex: (Int) -> Unit,
    onFlipCard: () -> Unit,
    onToggleCardDetails: () -> Unit,
    onToggleCardFreeze: (String, Boolean) -> Unit,
    onUpdateCardLimit: (String, Double) -> Unit,
    onCopyNumber: () -> Unit,
    onOpenCreateCard: () -> Unit,
    onOpenExportStatement: () -> Unit = {},
    onExecuteCardTransaction: (cardId: String, merchant: String, amount: Double, category: TransactionCategory, memo: String) -> Unit = { _, _, _, _, _ -> },
    onTransactionClick: (TransactionEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeCard = cards.getOrNull(selectedCardIndex) ?: cards.firstOrNull()
    var contactlessState by remember { mutableStateOf(true) }
    var onlineState by remember { mutableStateOf(true) }
    var showChargeModal by remember { mutableStateOf(false) }

    // Filter transactions matching this card
    val cardTransactions = remember(activeCard?.id, allTransactions) {
        if (activeCard == null) emptyList()
        else {
            val last4 = activeCard.cardNumber.takeLast(4)
            allTransactions.filter { tx ->
                tx.paymentMethod.contains(last4) || 
                (tx.paymentMethod.contains(activeCard.bankName, ignoreCase = true) && !tx.paymentMethod.contains("••")) ||
                tx.title.contains(activeCard.bankName, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .bankingBackground(BankingBackgroundType.CARDS, BankingTheme.colors.isDark)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "OFFICIAL BANKS & DIGITAL VAULTS",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.5.sp,
                            color = ChampagneGold,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cards Portfolio",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Link Official Bank Card / Virtual Card CTA
                    ClayButton(
                        onClick = onOpenCreateCard,
                        variant = ClayVariant.GOLD,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("issue_new_card_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Obsidian950,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Link / Issue",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Obsidian950
                        )
                    }
                }
            }
        }

        // Horizontal Card Carousel Selector with Bank Labels
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(cards) { idx, c ->
                    val isSelected = idx == selectedCardIndex
                    ClayCard(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                        variant = if (isSelected) ClayVariant.OBSIDIAN else ClayVariant.SURFACE,
                        elevation = if (isSelected) ClayElevation.MEDIUM else ClayElevation.LOW,
                        shape = RoundedCornerShape(16.dp),
                        onClick = { onSelectCardIndex(idx) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(if (c.isFrozen) Color(0xFFF43F5E) else EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = c.bankName.take(16),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) ChampagneGold else TextPrimary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${c.cardProductName.take(14)} •• ${c.cardNumber.takeLast(4)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3D Card Interactive Display
        if (activeCard != null) {
            item {
                Card3DView(
                    card = activeCard,
                    isFlipped = isCardFlipped,
                    showDetails = showCardDetails,
                    onFlipClick = onFlipCard,
                    onToggleDetails = onToggleCardDetails,
                    onToggleFreeze = { onToggleCardFreeze(activeCard.id, activeCard.isFrozen) },
                    onCopyNumber = onCopyNumber,
                    onSimulatePurchase = { showChargeModal = true }
                )
            }

            // Real-Time Transaction Quick Action Banner
            item {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = ClayVariant.OBSIDIAN,
                    elevation = ClayElevation.MEDIUM,
                    shape = RoundedCornerShape(20.dp),
                    onClick = { showChargeModal = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
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
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Obsidian950,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Execute Real-Time Transaction",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Simulate live merchant charge on ${activeCard.bankName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        ClayButton(
                            onClick = { showChargeModal = true },
                            variant = ClayVariant.GOLD,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("test_charge_banner_btn")
                        ) {
                            Text(
                                text = "Charge",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Obsidian950
                            )
                        }
                    }
                }
            }

            // Google Wallet / Apple Pay Status Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Official Bank Token Active",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Linked to Apple Pay & Google Wallet Contactless",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Text(
                        text = "LIVE SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen,
                        fontSize = 10.sp
                    )
                }
            }

            // Monthly Limit Adjustment Slider & Real-Time Spend Progress
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(18.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MONTHLY SPENDING LIMIT",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "$${"%,.0f".format(activeCard.spendingLimit)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ChampagneGold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Live Spent",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "$${"%,.2f".format(activeCard.currentSpent)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (activeCard.currentSpent > activeCard.spendingLimit * 0.9) Color(0xFFF43F5E) else TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val progress = remember(activeCard.currentSpent, activeCard.spendingLimit) {
                        if (activeCard.spendingLimit > 0) {
                            (activeCard.currentSpent / activeCard.spendingLimit).toFloat().coerceIn(0f, 1f)
                        } else 0f
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (progress > 0.85f) Color(0xFFF43F5E) else ChampagneGold,
                        trackColor = SurfaceBorder
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    var currentLimit by remember(activeCard.id) { mutableFloatStateOf(activeCard.spendingLimit.toFloat()) }

                    Slider(
                        value = currentLimit,
                        onValueChange = { currentLimit = it },
                        onValueChangeFinished = {
                            onUpdateCardLimit(activeCard.id, currentLimit.toDouble())
                        },
                        valueRange = 1000f..100000f,
                        steps = 99,
                        colors = SliderDefaults.colors(
                            thumbColor = ChampagneGold,
                            activeTrackColor = ChampagneGold,
                            inactiveTrackColor = SurfaceBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Drag to adjust real-time velocity cap for ${activeCard.bankName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Real-Time Card Ledger
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(18.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REAL-TIME CARD LEDGER",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ChampagneGold.copy(alpha = 0.12f))
                                    .clickable { onOpenExportStatement() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("card_export_statement_pill")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Export",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ChampagneGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "${cardTransactions.size} txs",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (cardTransactions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No live charges recorded yet on this card.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showChargeModal = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SurfaceCardElevated,
                                    contentColor = ChampagneGold
                                ),
                                modifier = Modifier.border(1.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            ) {
                                Text("Run First Authorization", fontSize = 11.sp)
                            }
                        }
                    } else {
                        cardTransactions.take(5).forEach { tx ->
                            TransactionRow(
                                transaction = tx,
                                onClick = { onTransactionClick(tx) }
                            )
                        }
                    }
                }
            }

            // Card Security Switches
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(18.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "HARDWARE & SECURITY GOVERNANCE",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )

                    SettingToggleRow(
                        icon = Icons.Default.Nfc,
                        title = "Contactless POS Payments",
                        subtitle = "Enable tap-to-pay terminals globally",
                        checked = contactlessState,
                        onCheckedChange = { contactlessState = it }
                    )

                    SettingToggleRow(
                        icon = Icons.Default.ShoppingBag,
                        title = "Online E-Commerce Transactions",
                        subtitle = "Authorize web and in-app checkouts",
                        checked = onlineState,
                        onCheckedChange = { onlineState = it }
                    )

                    SettingToggleRow(
                        icon = Icons.Default.Lock,
                        title = "ATM International Withdrawals",
                        subtitle = "Allow global ATM currency dispensations",
                        checked = true,
                        onCheckedChange = {}
                    )

                    SettingToggleRow(
                        icon = Icons.Default.Security,
                        title = "Real-Time Fraud Interception",
                        subtitle = "Continuous behavioral analysis on ${activeCard.bankName}",
                        checked = true,
                        onCheckedChange = {}
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal Dialog to Execute Real-Time Card Charge
    if (showChargeModal && activeCard != null) {
        RealTimeCardChargeDialog(
            card = activeCard,
            onDismiss = { showChargeModal = false },
            onExecute = { merchant, amount, category, memo ->
                onExecuteCardTransaction(activeCard.id, merchant, amount, category, memo)
                showChargeModal = false
            }
        )
    }
}

@Composable
private fun RealTimeCardChargeDialog(
    card: CardEntity,
    onDismiss: () -> Unit,
    onExecute: (merchant: String, amount: Double, category: TransactionCategory, memo: String) -> Unit
) {
    var merchantName by remember { mutableStateOf("Apple Store") }
    var amountString by remember { mutableStateOf("249.00") }
    var memo by remember { mutableStateOf("AirPods Max Space Gray") }
    var selectedCategory by remember { mutableStateOf(TransactionCategory.TECH) }

    val merchantPresets = listOf(
        Triple("Apple Store", "1,199.00", TransactionCategory.TECH),
        Triple("Nobu Restaurant", "345.50", TransactionCategory.DINING),
        Triple("Uber Black", "58.20", TransactionCategory.TRAVEL),
        Triple("Delta Air Lines", "720.00", TransactionCategory.TRAVEL),
        Triple("Equinox Gym", "260.00", TransactionCategory.UTILITIES),
        Triple("Stripe Cloud", "149.00", TransactionCategory.TECH)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Obsidian900,
            border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
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
                    Column {
                        Text(
                            text = "LIVE PAYMENT TERMINAL",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            color = ChampagneGold,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Execute Card Charge",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Active Card Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${card.bankName} • ${card.cardProductName}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Ending in •• ${card.cardNumber.takeLast(4)} • Available: $${"%,.2f".format(card.spendingLimit - card.currentSpent)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Merchant Presets
                Text(
                    text = "QUICK MERCHANT PRESETS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 9.sp
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(merchantPresets) { preset ->
                        val isSelected = merchantName == preset.first
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ChampagneGold.copy(alpha = 0.2f) else SurfaceCard)
                                .border(1.dp, if (isSelected) ChampagneGold else SurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    merchantName = preset.first
                                    amountString = preset.second.replace(",", "")
                                    selectedCategory = preset.third
                                    memo = "${preset.first} Purchase"
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = preset.first,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) ChampagneGold else TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Merchant Name Input
                OutlinedTextField(
                    value = merchantName,
                    onValueChange = { merchantName = it },
                    label = { Text("Merchant / Store Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Amount Input
                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    label = { Text("Amount ($ USD)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Note / Memo Input
                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("Receipt Memo") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Submit Button
                val parsedAmount = amountString.toDoubleOrNull() ?: 0.0
                Button(
                    onClick = {
                        if (parsedAmount > 0 && merchantName.isNotBlank()) {
                            onExecute(merchantName, parsedAmount, selectedCategory, memo)
                        }
                    },
                    enabled = parsedAmount > 0 && merchantName.isNotBlank() && !card.isFrozen,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = Obsidian950,
                        disabledContainerColor = SurfaceCard,
                        disabledContentColor = TextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("authorize_payment_btn")
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (card.isFrozen) "Card is Locked" else "Authorize Live Payment ($${"%,.2f".format(parsedAmount)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .claymorphic(
                        variant = ClayVariant.OBSIDIAN,
                        shape = CircleShape,
                        elevation = ClayElevation.LOW,
                        isDark = BankingTheme.colors.isDark
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        ClaySwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
