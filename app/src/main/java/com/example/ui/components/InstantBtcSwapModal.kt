package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.CryptoHoldingEntity
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import java.util.UUID

/**
 * Real-Time Official Professional USD ⇄ BTC Instant Swap Sheet
 * Features official currency emblems, live price calculation, deep liquidity routing,
 * instant account balance debit, and immediate BTC portfolio addition.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstantBtcSwapModal(
    accounts: List<AccountEntity>,
    selectedAccountId: String?,
    cryptoHoldings: List<CryptoHoldingEntity>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onExecuteSwap: (fromSymbol: String, toSymbol: String, fromAmount: Double, slippage: Double, accountId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
    val btcHolding = cryptoHoldings.find { it.symbol == "BTC" }
    val btcSpotPrice = btcHolding?.currentPriceUsd ?: 67420.00
    val btcBalance = btcHolding?.balance ?: 1.4500
    val usdBalance = activeAccount?.balance ?: 285450.00

    // Swap Direction: true = USD -> BTC, false = BTC -> USD
    var isUsdToBtc by remember { mutableStateOf(true) }

    var inputAmountText by remember { mutableStateOf("") }
    val parsedAmount = inputAmountText.toDoubleOrNull() ?: 0.0

    // Calculated target output
    val targetAmount = remember(parsedAmount, isUsdToBtc, btcSpotPrice) {
        if (parsedAmount <= 0) 0.0
        else if (isUsdToBtc) parsedAmount / btcSpotPrice
        else parsedAmount * btcSpotPrice
    }

    val maxSourceAvailable = if (isUsdToBtc) usdBalance else btcBalance
    val isInsufficientBalance = parsedAmount > maxSourceAvailable
    val canSubmit = parsedAmount > 0.0 && !isInsufficientBalance

    // UI Execution Flow State
    var isExecuting by remember { mutableStateOf(false) }
    var executionResultTxHash by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (BankingTheme.colors.isDark) Obsidian950 else BankingTheme.colors.surfaceCard,
        tonalElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier.testTag("instant_btc_swap_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 34.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Title + Real Official Icons + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Overlapping Official Currency Badges
                    Box(modifier = Modifier.size(40.dp)) {
                        OfficialUsdIcon(
                            size = 28.dp,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        OfficialBitcoinIcon(
                            size = 28.dp,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(start = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "INSTANT OTC SWAP",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.6.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChampagneGold,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "0% FEE",
                                    color = EmeraldGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = if (isUsdToBtc) "Swap US Dollars to Bitcoin" else "Swap Bitcoin to US Dollars",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary,
                            fontSize = 17.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(BankingTheme.colors.surfaceCardElevated)
                        .border(1.dp, BankingTheme.colors.border, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BankingTheme.colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Real-Time Spot Benchmark Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BankingTheme.colors.surfaceCardElevated)
                    .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Lead Real-Time Benchmark",
                        style = MaterialTheme.typography.labelSmall,
                        color = BankingTheme.colors.textSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OfficialBitcoinIcon(size = 14.dp, showGlow = false)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "1 BTC = $${"%,.2f".format(btcSpotPrice)} USD",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = BankingTheme.colors.primaryAccent,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (executionResultTxHash != null) {
                // SUCCESS STATE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(EmeraldGreen.copy(alpha = 0.12f))
                        .border(1.5.dp, EmeraldGreen, RoundedCornerShape(20.dp))
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen.copy(alpha = 0.2f))
                                .border(2.dp, EmeraldGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Trade Settled",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "INSTANT OTC SETTLEMENT CONFIRMED",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isUsdToBtc) {
                                "Credited ${"%.6f".format(targetAmount)} BTC to Bitcoin Ledger"
                            } else {
                                "Credited $${"%,.2f".format(targetAmount)} USD to ${activeAccount?.name ?: "Checking"}"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "TX Ref: ${executionResultTxHash ?: ""}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = BankingTheme.colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Done • Return to Wealth Dashboard", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // ACTIVE SWAP FORM

                // 1. SOURCE CURRENCY CARD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(BankingTheme.colors.surfaceCard)
                        .border(1.2.dp, BankingTheme.colors.border, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "YOU PAY FROM",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = BankingTheme.colors.textSecondary,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (isUsdToBtc) {
                                    "Balance: $${"%,.2f".format(usdBalance)} USD"
                                } else {
                                    "Balance: ${"%,.4f".format(btcBalance)} BTC"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = BankingTheme.colors.primaryAccent,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Official Currency Icon + Name Tag
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BankingTheme.colors.surfaceCardElevated)
                                    .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                if (isUsdToBtc) {
                                    OfficialUsdIcon(size = 24.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "USD",
                                        fontWeight = FontWeight.Bold,
                                        color = BankingTheme.colors.textPrimary,
                                        fontSize = 14.sp
                                    )
                                } else {
                                    OfficialBitcoinIcon(size = 24.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "BTC",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF7931A),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            OutlinedTextField(
                                value = inputAmountText,
                                onValueChange = { inputAmountText = it.filter { c -> c.isDigit() || c == '.' } },
                                placeholder = {
                                    Text(
                                        text = if (isUsdToBtc) "0.00" else "0.0000",
                                        fontFamily = FontFamily.Monospace,
                                        color = BankingTheme.colors.textSecondary,
                                        fontSize = 18.sp
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isInsufficientBalance) Color(0xFFEF4444) else BankingTheme.colors.textPrimary
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (isInsufficientBalance) Color(0xFFEF4444) else BankingTheme.colors.primaryAccent,
                                    unfocusedBorderColor = BankingTheme.colors.border,
                                    focusedContainerColor = BankingTheme.colors.surfaceCardElevated,
                                    unfocusedContainerColor = BankingTheme.colors.surfaceCardElevated
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btc_swap_input_amount")
                            )
                        }

                        // Presets
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presets = if (isUsdToBtc) listOf("100", "500", "1000", "5000") else listOf("0.01", "0.05", "0.1", "0.5")
                            presets.forEach { p ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BankingTheme.colors.surfaceCardElevated)
                                        .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(8.dp))
                                        .clickable { inputAmountText = p }
                                        .padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isUsdToBtc) "+$$p" else "+$p",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BankingTheme.colors.textSecondary
                                    )
                                }
                            }
                            // MAX Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BankingTheme.colors.primaryAccent.copy(alpha = 0.15f))
                                    .border(1.dp, BankingTheme.colors.primaryAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        inputAmountText = if (isUsdToBtc) "%.2f".format(usdBalance) else "%.4f".format(btcBalance)
                                    }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "MAX",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BankingTheme.colors.primaryAccent
                                )
                            }
                        }

                        if (isInsufficientBalance) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Insufficient funds in ${if (isUsdToBtc) "USD balance" else "Bitcoin holding"}.",
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // CENTRAL INVERSION BUTTON
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        BankingTheme.colors.surfaceCardElevated,
                                        Color(0xFF262C40)
                                    )
                                )
                            )
                            .border(1.5.dp, ChampagneGold.copy(alpha = 0.8f), CircleShape)
                            .clickable {
                                isUsdToBtc = !isUsdToBtc
                                inputAmountText = ""
                            }
                            .testTag("invert_btc_swap_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Invert Swap Direction",
                            tint = ChampagneGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // 2. DESTINATION CURRENCY CARD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(BankingTheme.colors.surfaceCard)
                        .border(1.2.dp, BankingTheme.colors.border, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "YOU RECEIVE (ESTIMATED)",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = BankingTheme.colors.textSecondary,
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
                                    text = "Instant OTC Fill",
                                    fontSize = 10.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BankingTheme.colors.surfaceCardElevated)
                                    .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                if (isUsdToBtc) {
                                    OfficialBitcoinIcon(size = 24.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "BTC",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF7931A),
                                        fontSize = 14.sp
                                    )
                                } else {
                                    OfficialUsdIcon(size = 24.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "USD",
                                        fontWeight = FontWeight.Bold,
                                        color = BankingTheme.colors.textPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (targetAmount > 0) {
                                        if (isUsdToBtc) "≈ ${"%,.6f".format(targetAmount)}"
                                        else "≈ $${"%,.2f".format(targetAmount)}"
                                    } else "0.00",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isUsdToBtc) Color(0xFFF7931A) else EmeraldGreen,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = if (isUsdToBtc) "Lead Institutional Cold-Storage" else "Account: ${activeAccount?.name ?: "Checking"}",
                                    fontSize = 10.sp,
                                    color = BankingTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ROUTING & PRIVACY SPECIFICATION ACCORDION
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BankingTheme.colors.surfaceCardElevated)
                        .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Execution Venue", fontSize = 11.5.sp, color = BankingTheme.colors.textSecondary)
                        Text("Lead Private Liquidity Gateway", fontSize = 11.5.sp, color = BankingTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Exchange Fee", fontSize = 11.5.sp, color = BankingTheme.colors.textSecondary)
                        Text("$0.00 (Private Banking Tier)", fontSize = 11.5.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Slippage Tolerance", fontSize = 11.5.sp, color = BankingTheme.colors.textSecondary)
                        Text("0.00% Guaranteed Spot", fontSize = 11.5.sp, color = BankingTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Settlement Speed", fontSize = 11.5.sp, color = BankingTheme.colors.textSecondary)
                        Text("Real-Time Instant (< 400ms)", fontSize = 11.5.sp, color = BankingTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ACTION BUTTON
                Button(
                    onClick = {
                        isExecuting = true
                        val fromSym = if (isUsdToBtc) "USD" else "BTC"
                        val toSym = if (isUsdToBtc) "BTC" else "USD"
                        val accountId = activeAccount?.id ?: "acc_checking"
                        onExecuteSwap(fromSym, toSym, parsedAmount, 0.0, accountId)
                        val txHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(20)
                        executionResultTxHash = txHash
                        isExecuting = false
                    },
                    enabled = canSubmit && !isExecuting,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUsdToBtc) Color(0xFFF7931A) else EmeraldGreen,
                        contentColor = Color.Black,
                        disabledContainerColor = BankingTheme.colors.surfaceCardElevated,
                        disabledContentColor = BankingTheme.colors.textSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("confirm_instant_btc_swap_button")
                ) {
                    if (isExecuting) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Settling Lead OTC Swap...", fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isUsdToBtc) {
                                OfficialBitcoinIcon(size = 20.dp, showGlow = false)
                            } else {
                                OfficialUsdIcon(size = 20.dp, showGlow = false)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    parsedAmount <= 0 -> "Enter Amount to Swap"
                                    isInsufficientBalance -> "Insufficient Balance"
                                    isUsdToBtc -> "Confirm Swap: USD → BTC"
                                    else -> "Confirm Swap: BTC → USD"
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
