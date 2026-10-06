package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.CryptoHoldingEntity
import com.example.data.model.CryptoOrderEntity
import com.example.data.model.LiveMarketDepth
import com.example.ui.components.BankingBackgroundType
import com.example.ui.components.ClayBadge
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCard
import com.example.ui.components.ClayElevation
import com.example.ui.components.ClayInputContainer
import com.example.ui.components.ClayPill
import com.example.ui.components.ClayVariant
import com.example.ui.components.OfficialCurrencyBadge
import com.example.ui.components.bankingBackground
import com.example.ui.components.claymorphic
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

enum class TradingSide {
    BUY, SELL
}

enum class OrderType(val displayName: String) {
    MARKET("Market"),
    LIMIT("Limit"),
    STOP("Stop-Loss")
}

data class OpenLimitOrder(
    val id: String,
    val symbol: String,
    val side: TradingSide,
    val price: Double,
    val amount: Double,
    val totalUsd: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun CryptoDeskScreen(
    accounts: List<AccountEntity>,
    cryptoHoldings: List<CryptoHoldingEntity>,
    cryptoOrders: List<CryptoOrderEntity>,
    selectedSymbol: String,
    onSelectSymbol: (String) -> Unit,
    onExecuteSwap: (fromSymbol: String, toSymbol: String, fromAmount: Double, slippage: Double, accountId: String) -> Unit,
    getMarketDepth: (String, Double) -> LiveMarketDepth,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val checkingAccount = accounts.find { it.id == "acc_checking" } ?: accounts.firstOrNull()
    val availableUsd = checkingAccount?.balance ?: 100000.0

    val activeHolding = cryptoHoldings.find { it.symbol == selectedSymbol } ?: cryptoHoldings.firstOrNull()
    val currentSpotPrice = activeHolding?.currentPriceUsd ?: 67420.0
    val availableCrypto = activeHolding?.balance ?: 0.0

    // Trading Panel States
    var selectedSide by remember { mutableStateOf(TradingSide.BUY) }
    var selectedOrderType by remember { mutableStateOf(OrderType.MARKET) }
    var isInputInUsd by remember { mutableStateOf(true) } // true: input is USD, false: input is Crypto

    var inputAmountText by remember { mutableStateOf("2500") }
    var limitPriceText by remember(currentSpotPrice) { mutableStateOf("%.2f".format(currentSpotPrice)) }
    var stopPriceText by remember(currentSpotPrice) { mutableStateOf("%.2f".format(currentSpotPrice * 0.98)) }
    var slippageTolerance by remember { mutableDoubleStateOf(0.1) }
    var isExecutingTrade by remember { mutableStateOf(false) }

    // Active Resting Limit Orders
    val openLimitOrders = remember {
        mutableStateListOf(
            OpenLimitOrder(
                id = "lim_8812",
                symbol = "BTC",
                side = TradingSide.BUY,
                price = 65800.00,
                amount = 0.5000,
                totalUsd = 32900.00
            ),
            OpenLimitOrder(
                id = "lim_9041",
                symbol = "ETH",
                side = TradingSide.SELL,
                price = 3750.00,
                amount = 2.0000,
                totalUsd = 7500.00
            )
        )
    }

    // Trade Fill Execution Dialog
    var lastExecutedTrade by remember { mutableStateOf<CryptoOrderEntity?>(null) }
    var isTradeReceiptOpen by remember { mutableStateOf(false) }

    // Chart Timeframe State
    var selectedTimeframe by remember { mutableStateOf("24H") }
    val timeframes = listOf("15M", "1H", "4H", "24H", "7D", "1M")

    // Active Bottom Tab
    var activeBottomTab by remember { mutableStateOf(0) } // 0: Order Book, 1: Trade Fills, 2: Open Limit Orders, 3: Asset Specs

    // Compute Input & Output Conversions
    val parsedInputAmount = inputAmountText.toDoubleOrNull() ?: 0.0
    val targetExecutionPrice = when (selectedOrderType) {
        OrderType.MARKET -> currentSpotPrice
        OrderType.LIMIT -> limitPriceText.toDoubleOrNull() ?: currentSpotPrice
        OrderType.STOP -> stopPriceText.toDoubleOrNull() ?: currentSpotPrice
    }

    // Total USD value and crypto quantity
    val (usdTotal, cryptoQuantity) = remember(parsedInputAmount, isInputInUsd, targetExecutionPrice) {
        if (targetExecutionPrice <= 0.0) Pair(0.0, 0.0)
        else if (isInputInUsd) {
            val usd = parsedInputAmount
            val crypto = usd / targetExecutionPrice
            Pair(usd, crypto)
        } else {
            val crypto = parsedInputAmount
            val usd = crypto * targetExecutionPrice
            Pair(usd, crypto)
        }
    }

    // Balance checks
    val isInsufficientBalance = when (selectedSide) {
        TradingSide.BUY -> usdTotal > availableUsd
        TradingSide.SELL -> cryptoQuantity > availableCrypto
    }

    val canSubmitTrade = parsedInputAmount > 0.0 && !isInsufficientBalance && !isExecutingTrade && targetExecutionPrice > 0.0

    val totalCryptoPortfolioUsd = remember(cryptoHoldings) {
        cryptoHoldings.sumOf { it.totalValueUsd }
    }

    // Real-time Order Book Depth
    val liveDepth = remember(selectedSymbol, currentSpotPrice) {
        getMarketDepth(selectedSymbol, currentSpotPrice)
    }

    // Trade Confirmation Dialog
    if (isTradeReceiptOpen && lastExecutedTrade != null) {
        val executed = lastExecutedTrade!!
        AlertDialog(
            onDismissRequest = { isTradeReceiptOpen = false },
            containerColor = BankingTheme.colors.surfaceCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "INSTITUTIONAL FILL CONFIRMED",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChampagneGold,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "Order Filled Successfully",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Summary Plaque
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.SURFACE,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Trading Pair", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("${executed.fromSymbol} → ${executed.toSymbol}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Execution Price", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("$${"%,.2f".format(executed.executionPrice)} USD", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = ChampagneGold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Filled Amount", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("${"%.4f".format(executed.toAmount)} ${executed.toSymbol}", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Settlement Consideration", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("$${"%,.2f".format(executed.fromAmount)} ${executed.fromSymbol}", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Institutional Commission", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("$0.00 (Zero Markup)", style = MaterialTheme.typography.bodySmall, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Blockchain TX Hash row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BankingTheme.colors.backgroundElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("MPC TX HASH", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = TextSecondary)
                            Text(executed.txHash.take(24) + "...", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextPrimary)
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(executed.txHash))
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Hash", tint = ChampagneGold, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { isTradeReceiptOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Obsidian950),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .bankingBackground(BankingBackgroundType.CRYPTO, BankingTheme.colors.isDark)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TERMINAL HEADER & TOTAL DIGITAL ASSETS VALUE
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "INSTITUTIONAL TRADING DESK",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.4.sp,
                                color = ChampagneGold,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ClayBadge(
                                text = "L2 VWAP ENGINE",
                                variant = ClayVariant.EMERALD
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Crypto Terminal",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 24.sp
                        )
                    }

                    // MPC Cold Custody Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceCard)
                            .border(1.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "MPC Vault",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Portfolio Value Card
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = ClayVariant.OBSIDIAN,
                    elevation = ClayElevation.MEDIUM,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL CRYPTO ASSETS",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp,
                                color = TextSecondary,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${"%,.2f".format(totalCryptoPortfolioUsd)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = ChampagneGold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            ClayBadge(
                                text = "+3.84% 24h",
                                variant = ClayVariant.EMERALD,
                                icon = Icons.Default.TrendingUp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "USD Balance: $${"%,.0f".format(availableUsd)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 2. SPOT ASSET SELECTOR CAROUSEL
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SELECT ASSET PAIR",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = TextSecondary,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cryptoHoldings) { holding ->
                        val isSelected = holding.symbol == selectedSymbol
                        val isPositive = holding.change24hPercent >= 0

                        ClayCard(
                            modifier = Modifier
                                .clickable { onSelectSymbol(holding.symbol) }
                                .testTag("select_crypto_chip_${holding.symbol}"),
                            variant = if (isSelected) ClayVariant.OBSIDIAN else ClayVariant.SURFACE,
                            elevation = if (isSelected) ClayElevation.HIGH else ClayElevation.LOW,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.dp,
                                        color = if (isSelected) ChampagneGold else Color.Transparent,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OfficialCurrencyBadge(symbol = holding.symbol, size = 26.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${holding.symbol}/USD",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = (if (isPositive) "+" else "") + "${holding.change24hPercent}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isPositive) EmeraldGreen else Color(0xFFF43F5E),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        )
                                    }
                                    Text(
                                        text = "$${"%,.2f".format(holding.currentPriceUsd)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = ChampagneGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. REAL PROFESSIONAL REALISTIC TRADING BUY / SELL TERMINAL
        item {
            val accentColor = if (selectedSide == TradingSide.BUY) EmeraldGreen else Color(0xFFF43F5E)

            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("crypto_trading_terminal_card"),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.HIGH,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .border(1.5.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // HEADER ROW: BUY vs SELL INSTITUTIONAL TABS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(BankingTheme.colors.backgroundElevated)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // BUY TAB
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedSide == TradingSide.BUY) EmeraldGreen else Color.Transparent)
                                .clickable { selectedSide = TradingSide.BUY }
                                .padding(vertical = 10.dp)
                                .testTag("crypto_buy_tab"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (selectedSide == TradingSide.BUY) Obsidian950 else EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BUY $selectedSymbol",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = if (selectedSide == TradingSide.BUY) Obsidian950 else EmeraldGreen,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // SELL TAB
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedSide == TradingSide.SELL) Color(0xFFF43F5E) else Color.Transparent)
                                .clickable { selectedSide = TradingSide.SELL }
                                .padding(vertical = 10.dp)
                                .testTag("crypto_sell_tab"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (selectedSide == TradingSide.SELL) Color.White else Color(0xFFF43F5E),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SELL $selectedSymbol",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = if (selectedSide == TradingSide.SELL) Color.White else Color(0xFFF43F5E),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // ORDER TYPE SELECTOR: Market | Limit | Stop
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OrderType.values().forEach { type ->
                                val isSelected = selectedOrderType == type
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ChampagneGold else SurfaceCard)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) ChampagneGold else SurfaceBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedOrderType = type }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                        .testTag("order_type_${type.name.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Obsidian950 else TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Unit input toggle: USD vs Crypto
                        ClayPill(
                            text = if (isInputInUsd) "Denom: USD ($)" else "Denom: $selectedSymbol",
                            isSelected = true,
                            onClick = { isInputInUsd = !isInputInUsd },
                            modifier = Modifier.testTag("toggle_input_currency_btn")
                        )
                    }

                    // LIMIT / STOP PRICE INPUT (Shown when Limit or Stop selected)
                    if (selectedOrderType != OrderType.MARKET) {
                        ClayInputContainer(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (selectedOrderType == OrderType.LIMIT) "LIMIT EXECUTION PRICE (USD)" else "STOP TRIGGER PRICE (USD)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 8.5.sp,
                                        letterSpacing = 1.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("$", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ChampagneGold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        OutlinedTextField(
                                            value = if (selectedOrderType == OrderType.LIMIT) limitPriceText else stopPriceText,
                                            onValueChange = {
                                                if (selectedOrderType == OrderType.LIMIT) limitPriceText = it
                                                else stopPriceText = it
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextPrimary
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color.Transparent,
                                                unfocusedBorderColor = Color.Transparent
                                            ),
                                            modifier = Modifier.testTag("trading_limit_price_input")
                                        )
                                    }
                                }

                                // Quick Price Tick Adjustment Buttons
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            val cur = limitPriceText.toDoubleOrNull() ?: currentSpotPrice
                                            val step = if (currentSpotPrice > 1000) 50.0 else 1.0
                                            limitPriceText = "%.2f".format(maxOf(0.0, cur - step))
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease Price", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val cur = limitPriceText.toDoubleOrNull() ?: currentSpotPrice
                                            val step = if (currentSpotPrice > 1000) 50.0 else 1.0
                                            limitPriceText = "%.2f".format(cur + step)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase Price", tint = ChampagneGold, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // AMOUNT INPUT CONTAINER
                    ClayInputContainer(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isInputInUsd) "ORDER AMOUNT (USD)" else "ORDER QUANTITY ($selectedSymbol)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )

                                // Available Balance Pill
                                val availableLabel = if (selectedSide == TradingSide.BUY) {
                                    "Avail: $${"%,.2f".format(availableUsd)} (Checking)"
                                } else {
                                    "Avail: ${"%.4f".format(availableCrypto)} $selectedSymbol"
                                }

                                Text(
                                    text = availableLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.5.sp,
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isInputInUsd) {
                                    Text(
                                        text = "$",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ChampagneGold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }

                                OutlinedTextField(
                                    value = inputAmountText,
                                    onValueChange = { inputAmountText = it },
                                    placeholder = { Text("0.00", color = TextSecondary) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("trading_amount_input")
                                )

                                if (!isInputInUsd) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = selectedSymbol,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ChampagneGold
                                    )
                                }
                            }

                            // Dynamic Live 2-Way Conversion Preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isInputInUsd) "≈ ${"%.4f".format(cryptoQuantity)} $selectedSymbol"
                                    else "≈ $${"%,.2f".format(usdTotal)} USD",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedSide == TradingSide.BUY) EmeraldGreen else Color(0xFFF43F5E),
                                    fontSize = 11.5.sp
                                )

                                Text(
                                    text = "Rate: $${"%,.2f".format(targetExecutionPrice)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // TACTILE PERCENT ALLOCATION PILLS: 25% | 50% | 75% | 100% (MAX)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0.25 to "25%", 0.50 to "50%", 0.75 to "75%", 1.0 to "MAX").forEach { (pct, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceCard)
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (selectedSide == TradingSide.BUY) {
                                            val maxSpend = availableUsd * pct
                                            inputAmountText = if (isInputInUsd) "%.2f".format(maxSpend)
                                            else "%.4f".format(maxSpend / targetExecutionPrice)
                                        } else {
                                            val maxSell = availableCrypto * pct
                                            inputAmountText = if (isInputInUsd) "%.2f".format(maxSell * targetExecutionPrice)
                                            else "%.4f".format(maxSell)
                                        }
                                    }
                                    .padding(vertical = 6.dp)
                                    .testTag("pct_alloc_${(pct * 100).toInt()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    if (isInsufficientBalance) {
                        Text(
                            text = if (selectedSide == TradingSide.BUY)
                                "Insufficient USD balance ($${"%,.2f".format(availableUsd)} available in checking)"
                            else "Insufficient $selectedSymbol balance (${"%.4f".format(availableCrypto)} available)",
                            color = Color(0xFFF43F5E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // INSTITUTIONAL CLEARING SPECIFICATIONS
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.OBSIDIAN,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Maker/Taker Commission", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 10.5.sp)
                                Text("0.00% (Sponsored Institutional)", style = MaterialTheme.typography.bodySmall, color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Slippage Tolerance", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 10.5.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf(0.1, 0.5, 1.0).forEach { slip ->
                                        val isSel = slippageTolerance == slip
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isSel) ChampagneGold else SurfaceCard)
                                                .clickable { slippageTolerance = slip }
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "$slip%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 8.5.sp,
                                                color = if (isSel) Obsidian950 else TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Execution Settlement", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 10.5.sp)
                                Text("< 8ms • FedNow Real-Time Gross Settlement", style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontSize = 10.5.sp)
                            }
                        }
                    }

                    // EXECUTE TRADING ORDER BUTTON (Volumetric 3D Clay Button)
                    ClayButton(
                        onClick = {
                            if (canSubmitTrade) {
                                isExecutingTrade = true
                                val accId = checkingAccount?.id ?: "acc_checking"

                                if (selectedOrderType == OrderType.LIMIT && limitPriceText.toDoubleOrNull() != null) {
                                    // Resting Limit Order placement
                                    val price = limitPriceText.toDoubleOrNull() ?: currentSpotPrice
                                    openLimitOrders.add(
                                        0,
                                        OpenLimitOrder(
                                            id = "lim_" + System.currentTimeMillis().toString().takeLast(6),
                                            symbol = selectedSymbol,
                                            side = selectedSide,
                                            price = price,
                                            amount = cryptoQuantity,
                                            totalUsd = usdTotal
                                        )
                                    )
                                    isExecutingTrade = false
                                } else {
                                    // Market / Instant execution
                                    val (fromSym, toSym, fromAmt) = if (selectedSide == TradingSide.BUY) {
                                        Triple("USD", selectedSymbol, usdTotal)
                                    } else {
                                        Triple(selectedSymbol, "USD", cryptoQuantity)
                                    }

                                    onExecuteSwap(fromSym, toSym, fromAmt, slippageTolerance, accId)

                                    // Create fill confirmation
                                    lastExecutedTrade = CryptoOrderEntity(
                                        orderId = "ord_" + System.currentTimeMillis().toString().takeLast(6),
                                        fromSymbol = fromSym,
                                        toSymbol = toSym,
                                        fromAmount = fromAmt,
                                        toAmount = if (selectedSide == TradingSide.BUY) cryptoQuantity else usdTotal,
                                        executionPrice = targetExecutionPrice,
                                        txHash = "0x" + System.currentTimeMillis().toString(16) + "e819b42cfa1",
                                        status = "CONFIRMED"
                                    )
                                    isTradeReceiptOpen = true
                                    isExecutingTrade = false
                                }
                            }
                        },
                        enabled = canSubmitTrade,
                        variant = if (selectedSide == TradingSide.BUY) ClayVariant.EMERALD else ClayVariant.CRIMSON,
                        elevation = ClayElevation.HIGH,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("execute_trading_order_btn")
                    ) {
                        if (isExecutingTrade) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Obsidian950,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Routing through Lead Engine...",
                                fontWeight = FontWeight.Bold,
                                color = Obsidian950,
                                fontSize = 13.5.sp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (selectedSide == TradingSide.BUY) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (selectedSide == TradingSide.BUY) Obsidian950 else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val ctaText = if (selectedSide == TradingSide.BUY) {
                                    if (selectedOrderType == OrderType.LIMIT) "PLACE LIMIT BUY $selectedSymbol @ $${"%,.2f".format(targetExecutionPrice)}"
                                    else "BUY $selectedSymbol ($${"%,.2f".format(usdTotal)})"
                                } else {
                                    if (selectedOrderType == OrderType.LIMIT) "PLACE LIMIT SELL $selectedSymbol @ $${"%,.2f".format(targetExecutionPrice)}"
                                    else "SELL $selectedSymbol (${"%.4f".format(cryptoQuantity)} $selectedSymbol)"
                                }

                                Text(
                                    text = ctaText,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (selectedSide == TradingSide.BUY) Obsidian950 else Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. REAL-TIME CANDLESTICK / PRICE ACTION CHART WITH TIMEFRAMES
        item {
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
                    // Header: Pair & Live Ticker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OfficialCurrencyBadge(symbol = selectedSymbol, size = 22.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "$selectedSymbol / USD SPOT",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "$${"%,.2f".format(currentSpotPrice)} USD",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold
                                )
                            }
                        }

                        // Timeframe chips
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            timeframes.forEach { tf ->
                                val isSelected = tf == selectedTimeframe
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) ChampagneGold else SurfaceCard)
                                        .clickable { selectedTimeframe = tf }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = tf,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Obsidian950 else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // 24H High, Low & Volume Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BankingTheme.colors.backgroundElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("24H HIGH", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = TextSecondary)
                            Text("$${"%,.2f".format(activeHolding?.high24h ?: (currentSpotPrice * 1.02))}", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        }
                        Column {
                            Text("24H LOW", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = TextSecondary)
                            Text("$${"%,.2f".format(activeHolding?.low24h ?: (currentSpotPrice * 0.97))}", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color(0xFFF43F5E))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("24H VOLUME", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = TextSecondary)
                            val volBillions = (activeHolding?.volume24hUsd ?: 18000000000.0) / 1000000000.0
                            Text("$${"%.1f".format(volBillions)}B USD", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    // Interactive Custom Price Canvas Chart with Breathing Gradient
                    val chartValues = remember(activeHolding?.sparklineValues, selectedTimeframe) {
                        val base = activeHolding?.sparklineValues?.ifEmpty { null } ?: listOf(64920f, 65400f, 65100f, 66200f, 65900f, 67100f, 67420f)
                        when (selectedTimeframe) {
                            "15M" -> listOf(base.last() * 0.998f, base.last() * 0.999f, base.last() * 1.001f, base.last())
                            "1H" -> listOf(base.last() * 0.992f, base.last() * 0.996f, base.last() * 1.004f, base.last())
                            else -> base
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BankingTheme.colors.backgroundElevated)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        val isPositiveTrend = (chartValues.lastOrNull() ?: 0f) >= (chartValues.firstOrNull() ?: 0f)
                        val strokeColor = if (isPositiveTrend) EmeraldGreen else Color(0xFFF43F5E)

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (chartValues.size < 2) return@Canvas

                            val minVal = chartValues.minOrNull() ?: 0f
                            val maxVal = chartValues.maxOrNull() ?: 1f
                            val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal

                            val w = size.width
                            val h = size.height
                            val stepX = w / (chartValues.size - 1)

                            val path = Path()
                            val fillPath = Path()

                            chartValues.forEachIndexed { i, v ->
                                val x = i * stepX
                                val y = h - ((v - minVal) / range) * (h * 0.8f) - (h * 0.1f)
                                if (i == 0) {
                                    path.moveTo(x, y)
                                    fillPath.moveTo(x, h)
                                    fillPath.lineTo(x, y)
                                } else {
                                    val prevX = (i - 1) * stepX
                                    val prevY = h - ((chartValues[i - 1] - minVal) / range) * (h * 0.8f) - (h * 0.1f)
                                    val cx = (prevX + x) / 2f
                                    path.cubicTo(cx, prevY, cx, y, x, y)
                                    fillPath.cubicTo(cx, prevY, cx, y, x, y)
                                }
                            }

                            fillPath.lineTo(w, h)
                            fillPath.close()

                            // Fill with subtle gradient
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    listOf(strokeColor.copy(alpha = 0.25f), Color.Transparent)
                                )
                            )

                            // Stroke line
                            drawPath(
                                path = path,
                                color = strokeColor,
                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Current point pulse
                            val lastX = w
                            val lastY = h - ((chartValues.last() - minVal) / range) * (h * 0.8f) - (h * 0.1f)
                            drawCircle(color = strokeColor, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
                            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(lastX, lastY))
                        }
                    }
                }
            }
        }

        // 5. INTERACTIVE TABBED SECTION: Order Book | Trade Fills | Open Limit Orders | Tokenomics
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Tab Selector Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BankingTheme.colors.backgroundElevated)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Live Order Book", "Recent Fills", "Open Orders (${openLimitOrders.size})", "Tokenomics").forEachIndexed { idx, tabTitle ->
                        val isSelected = activeBottomTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ChampagneGold else Color.Transparent)
                                .clickable { activeBottomTab = idx }
                                .padding(vertical = 6.dp)
                                .testTag(
                                    when (idx) {
                                        0 -> "order_book_tab"
                                        1 -> "trade_history_tab"
                                        2 -> "open_orders_tab"
                                        else -> "tokenomics_tab"
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabTitle,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Obsidian950 else TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // TAB 0: LIVE ORDER BOOK (DEPTH OF MARKET) WITH 1-TAP PRICE FILL
                if (activeBottomTab == 0) {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.SURFACE,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$selectedSymbol/USD L2 ORDER BOOK (TAP PRICE TO AUTO-FILL)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.5.sp,
                                    letterSpacing = 1.sp,
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Spread: $0.80 (0.001%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // BIDS COLUMN (BUYERS)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("BIDS (BUY)", style = MaterialTheme.typography.labelSmall, color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    liveDepth.bids.take(5).forEach { bid ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))
                                                .clickable {
                                                    limitPriceText = "%.2f".format(bid.price)
                                                    selectedOrderType = OrderType.LIMIT
                                                }
                                                .padding(vertical = 3.dp, horizontal = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "$${"%,.0f".format(bid.price)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontFamily = FontFamily.Monospace,
                                                color = EmeraldGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "%.2f".format(bid.amount),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextSecondary,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // ASKS COLUMN (SELLERS)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ASKS (SELL)", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    liveDepth.asks.take(5).forEach { ask ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))
                                                .clickable {
                                                    limitPriceText = "%.2f".format(ask.price)
                                                    selectedOrderType = OrderType.LIMIT
                                                }
                                                .padding(vertical = 3.dp, horizontal = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "$${"%,.0f".format(ask.price)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFFF43F5E),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "%.2f".format(ask.amount),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextSecondary,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 1: RECENT FILLS / TRADE HISTORY
                if (activeBottomTab == 1) {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.SURFACE,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (cryptoOrders.isEmpty()) {
                                Text("No recent fills yet. Execute your first trade above.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            } else {
                                cryptoOrders.take(6).forEach { ord ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BankingTheme.colors.backgroundElevated)
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${ord.fromSymbol} → ${ord.toSymbol}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "Rate: $${"%,.2f".format(ord.executionPrice)} • ${ord.txHash.take(12)}...",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextSecondary,
                                                fontSize = 9.sp
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "+${"%.4f".format(ord.toAmount)} ${ord.toSymbol}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldGreen
                                            )
                                            ClayBadge(text = "FILLED", variant = ClayVariant.EMERALD)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 2: ACTIVE RESTING LIMIT ORDERS
                if (activeBottomTab == 2) {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.SURFACE,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (openLimitOrders.isEmpty()) {
                                Text("No active limit orders resting in the book.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            } else {
                                openLimitOrders.forEach { lim ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BankingTheme.colors.backgroundElevated)
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                ClayBadge(
                                                    text = lim.side.name,
                                                    variant = if (lim.side == TradingSide.BUY) ClayVariant.EMERALD else ClayVariant.CRIMSON
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "${lim.symbol} Limit @ $${"%,.2f".format(lim.price)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                            }
                                            Text(
                                                text = "Qty: ${"%.4f".format(lim.amount)} ${lim.symbol} • Consideration: $${"%,.2f".format(lim.totalUsd)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary,
                                                fontSize = 9.sp
                                            )
                                        }

                                        // Cancel Order Button
                                        Button(
                                            onClick = { openLimitOrders.remove(lim) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = SurfaceCard,
                                                contentColor = Color(0xFFF43F5E)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("cancel_limit_order_btn")
                                        ) {
                                            Text("Cancel", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 3: ASSET TOKENOMICS & SPECIFICATIONS
                if (activeBottomTab == 3) {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.SURFACE,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Network / Protocol", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text(activeHolding?.networkName ?: "Native Mainnet", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Custody Mechanism", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("Lead MPC Multi-Sig Vault (Tier 4)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = ChampagneGold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Settlement Finality", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("Instant Off-Exchange Netting / On-Chain Rollup", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Institutional Lending APY", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("+4.85% Compound APY", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
