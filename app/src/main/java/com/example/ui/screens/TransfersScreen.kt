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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapVert
import com.example.ui.components.BankingBackgroundType
import com.example.ui.components.ClayBadge
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCard
import com.example.ui.components.ClayElevation
import com.example.ui.components.ClayIconButton
import com.example.ui.components.ClayInputContainer
import com.example.ui.components.ClaySwitch
import com.example.ui.components.ClayVariant
import com.example.ui.components.bankingBackground
import com.example.ui.components.claymorphic
import com.example.ui.theme.BankingTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.model.PayeeContact
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionEntity
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

data class ScheduledPayment(
    val id: String,
    val title: String,
    val beneficiary: String,
    val amount: Double,
    val frequency: String,
    val nextDate: String,
    var isEnabled: Boolean
)

@Composable
fun TransfersScreen(
    payees: List<PayeeContact>,
    transferTransactions: List<TransactionEntity>,
    onOpenTransfer: () -> Unit,
    onOpenExportStatement: () -> Unit = {},
    onOpenReceipt: (TransactionEntity) -> Unit = {},
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // FX Currency Converter State
    var fxAmountString by remember { mutableStateOf("10000") }
    var selectedFromCurrency by remember { mutableStateOf("USD") }
    var selectedToCurrency by remember { mutableStateOf("EUR") }
    var isFromMenuOpen by remember { mutableStateOf(false) }
    var isToMenuOpen by remember { mutableStateOf(false) }

    val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.918,
        "GBP" to 0.785,
        "CHF" to 0.884,
        "SGD" to 1.342,
        "JPY" to 154.20
    )

    val inputAmount = fxAmountString.toDoubleOrNull() ?: 0.0
    val fromRate = exchangeRates[selectedFromCurrency] ?: 1.0
    val toRate = exchangeRates[selectedToCurrency] ?: 1.0
    val convertedAmount = (inputAmount / fromRate) * toRate

    // Scheduled Payments State
    var scheduledList by remember {
        mutableStateOf(
            listOf(
                ScheduledPayment("sp1", "Private Aviation Retainer", "NetJets Aviation", 18500.0, "Monthly", "Oct 1, 2026", true),
                ScheduledPayment("sp2", "Zurich Office Legal Escrow", "Bär & Karrer AG", 12000.0, "Monthly", "Oct 5, 2026", true),
                ScheduledPayment("sp3", "Cloud AI Training Infrastructure", "Lambda Labs Cloud", 4500.0, "Bi-weekly", "Oct 12, 2026", true)
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .bankingBackground(BankingBackgroundType.TRANSFERS, BankingTheme.colors.isDark)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Top Hero Transfer Dispatch Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "CAPITAL SETTLEMENT DESK",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.5.sp,
                    color = BankingTheme.colors.primaryAccent,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Wires & Transfers",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = BankingTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Real-time clearing via FedNow, SEPA Instant, and international SWIFT rails.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BankingTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ClayButton(
                        onClick = onOpenTransfer,
                        variant = ClayVariant.GOLD,
                        elevation = ClayElevation.MEDIUM,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("primary_send_transfer_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Obsidian950,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dispatch Wire",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Obsidian950
                        )
                    }

                    ClayButton(
                        onClick = onOpenExportStatement,
                        variant = ClayVariant.SURFACE,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("transfer_export_register_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Wire Register",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Beneficiaries Section
        item {
            Column {
                Text(
                    text = "VERIFIED BENEFICIARIES",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(payees) { payee ->
                        ClayCard(
                            modifier = Modifier.width(128.dp),
                            variant = ClayVariant.SURFACE,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(18.dp),
                            onClick = onOpenTransfer
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .claymorphic(
                                            variant = ClayVariant.SURFACE,
                                            shape = CircleShape,
                                            elevation = ClayElevation.MEDIUM,
                                            isDark = BankingTheme.colors.isDark
                                        )
                                        .background(Color(payee.colorHex).copy(alpha = 0.85f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = payee.avatarInitials,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = payee.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = payee.bank,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // FX Currency Exchange Converter Calculator
        item {
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fx_converter_card"),
                variant = ClayVariant.OBSIDIAN,
                elevation = ClayElevation.MEDIUM,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    imageVector = Icons.Default.CurrencyExchange,
                                    contentDescription = null,
                                    tint = Obsidian950,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "GLOBAL FX CONVERTER",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.sp,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "Live Interbank Spot Rate",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        ClayBadge(
                            text = "0% FX MARKUP",
                            variant = ClayVariant.EMERALD,
                            textColor = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // From Currency Input in Debossed Clay
                    ClayInputContainer(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = fxAmountString,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() || it == '.' }) fxAmountString = input
                                },
                                textStyle = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            // Currency Dropdown
                            Box {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .claymorphic(
                                            variant = ClayVariant.SURFACE,
                                            shape = RoundedCornerShape(10.dp),
                                            elevation = ClayElevation.LOW,
                                            isDark = BankingTheme.colors.isDark
                                        )
                                        .clickable { isFromMenuOpen = true }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = selectedFromCurrency,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ChampagneGold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = isFromMenuOpen,
                                    onDismissRequest = { isFromMenuOpen = false },
                                    modifier = Modifier.background(Obsidian900)
                                ) {
                                    exchangeRates.keys.forEach { curr ->
                                        DropdownMenuItem(
                                            text = { Text(curr, color = TextPrimary) },
                                            onClick = {
                                                selectedFromCurrency = curr
                                                isFromMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Swap Icon
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        ClayIconButton(
                            icon = Icons.Default.SwapVert,
                            contentDescription = "Swap Currencies",
                            onClick = {
                                val temp = selectedFromCurrency
                                selectedFromCurrency = selectedToCurrency
                                selectedToCurrency = temp
                            },
                            size = 38.dp,
                            iconSize = 20.dp,
                            variant = ClayVariant.SURFACE
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // To Currency Output
                    ClayInputContainer(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "≈ ${"%,.2f".format(convertedAmount)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )

                            Box {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .claymorphic(
                                            variant = ClayVariant.SURFACE,
                                            shape = RoundedCornerShape(10.dp),
                                            elevation = ClayElevation.LOW,
                                            isDark = BankingTheme.colors.isDark
                                        )
                                        .clickable { isToMenuOpen = true }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = selectedToCurrency,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ChampagneGold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = isToMenuOpen,
                                    onDismissRequest = { isToMenuOpen = false },
                                    modifier = Modifier.background(Obsidian900)
                                ) {
                                    exchangeRates.keys.forEach { curr ->
                                        DropdownMenuItem(
                                            text = { Text(curr, color = TextPrimary) },
                                            onClick = {
                                                selectedToCurrency = curr
                                                isToMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Scheduled Standing Orders Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCHEDULED STANDING ORDERS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.2.sp,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    ClayBadge(
                        text = "${scheduledList.size} ACTIVE",
                        variant = ClayVariant.GOLD,
                        textColor = Obsidian950
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    scheduledList.forEachIndexed { index, item ->
                        ClayCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = ClayVariant.SURFACE,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(18.dp)
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
                                                variant = ClayVariant.OBSIDIAN,
                                                shape = CircleShape,
                                                elevation = ClayElevation.LOW,
                                                isDark = BankingTheme.colors.isDark
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = ChampagneGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${item.beneficiary} • Next: ${item.nextDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${"%,.2f".format(item.amount)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    ClaySwitch(
                                        checked = item.isEnabled,
                                        onCheckedChange = { isChecked ->
                                            val updated = scheduledList.toMutableList()
                                            updated[index] = item.copy(isEnabled = isChecked)
                                            scheduledList = updated
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Past Transfers Log
        item {
            Column {
                Text(
                    text = "SETTLEMENT AUDIT LOG",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                val wires = transferTransactions.filter { it.category == TransactionCategory.TRANSFER }
                if (wires.isEmpty()) {
                    Text(
                        text = "No wire settlements found in ledger.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        wires.forEach { tx ->
                            TransactionRow(
                                transaction = tx,
                                onClick = { onTransactionClick(tx) },
                                onReceiptClick = { onOpenReceipt(tx) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
