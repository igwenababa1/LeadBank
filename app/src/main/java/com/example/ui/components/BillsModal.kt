package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class BillerOption(
    val id: String,
    val name: String,
    val category: String,
    val defaultAccount: String,
    val recommendedAmount: Double,
    val icon: ImageVector,
    val badge: String = "VERIFIED BILLER"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsModal(
    accounts: List<AccountEntity>,
    selectedAccountId: String?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onPayBill: (billerName: String, billCategory: String, amount: Double, accountNumber: String, memo: String, onComplete: (Boolean) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
    val availableBalance = currentAccount?.balance ?: 100000.0

    val billerPresets = remember {
        listOf(
            BillerOption("coned", "ConEdison NY Electric & Gas", "Utilities", "9821-4029-11", 385.50, Icons.Default.Bolt),
            BillerOption("pge", "Pacific Gas & Electric (PG&E)", "Utilities", "4410-8291-03", 420.00, Icons.Default.ElectricMeter),
            BillerOption("att", "AT&T Enterprise Fiber & Wireless", "Telecom", "8820-1948-22", 295.00, Icons.Default.Wifi),
            BillerOption("jpm_mortgage", "JPMorgan Jumbo Wealth Mortgage", "Real Estate", "5519-8841-00", 8500.00, Icons.Default.HomeWork),
            BillerOption("amex_centurion", "American Express Centurion Black", "Credit & Cards", "3712-884019-1009", 14250.00, Icons.Default.CreditCard),
            BillerOption("irs_tax", "IRS EFTPS 1040-ES Quarterly Tax", "Taxes & Municipal", "4402-9812-71", 25000.00, Icons.Default.AccountBalance)
        )
    }

    val categories = listOf("All", "Utilities", "Telecom", "Real Estate", "Credit & Cards", "Taxes & Municipal")
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedBiller by remember { mutableStateOf<BillerOption?>(billerPresets.first()) }

    var customBillerName by remember { mutableStateOf(billerPresets.first().name) }
    var accountNumberInput by remember { mutableStateOf(billerPresets.first().defaultAccount) }
    var amountString by remember { mutableStateOf(billerPresets.first().recommendedAmount.toInt().toString()) }
    var memoInput by remember { mutableStateOf("Monthly Settlement • FedNow") }
    var isExecuting by remember { mutableStateOf(false) }

    val filteredBillers = remember(selectedCategory) {
        if (selectedCategory == "All") billerPresets else billerPresets.filter { it.category == selectedCategory }
    }

    val parsedAmount = amountString.toDoubleOrNull() ?: 0.0
    val isInsufficientBalance = parsedAmount > availableBalance
    val canSubmit = parsedAmount > 0.0 && !isInsufficientBalance && customBillerName.isNotBlank() && accountNumberInput.isNotBlank() && !isExecuting

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
        modifier = modifier.testTag("bills_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 34.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .claymorphic(variant = ClayVariant.SURFACE, elevation = ClayElevation.LOW, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "EXECUTIVE BILL PAY",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.4.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChampagneGold,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Pay Utilities & Liabilities",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_bills_modal_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Source Account Plaque
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.LOW,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DEBIT ACCOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentAccount?.name ?: "Lead Private Checking",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Direct FedNow RTGS Clearing",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "AVAILABLE",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$${"%,.2f".format(availableBalance)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Pills
            Text(
                text = "SELECT BILLER CATEGORY",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.5.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSel = cat == selectedCategory
                    ClayPill(
                        text = cat,
                        isSelected = isSel,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Verified Biller Cards
            Text(
                text = "INSTITUTIONAL BILLERS",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.5.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredBillers.forEach { biller ->
                    val isSelected = selectedBiller?.id == biller.id
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = if (isSelected) ClayVariant.OBSIDIAN else ClayVariant.SURFACE,
                        elevation = if (isSelected) ClayElevation.MEDIUM else ClayElevation.LOW,
                        shape = RoundedCornerShape(14.dp),
                        onClick = {
                            selectedBiller = biller
                            customBillerName = biller.name
                            accountNumberInput = biller.defaultAccount
                            amountString = biller.recommendedAmount.toInt().toString()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .claymorphic(
                                            variant = if (isSelected) ClayVariant.GOLD else ClayVariant.SURFACE,
                                            shape = CircleShape,
                                            elevation = ClayElevation.LOW
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = biller.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Obsidian950 else ChampagneGold,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = biller.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ChampagneGold else TextPrimary
                                    )
                                    Text(
                                        text = "${biller.category} • Typical: $${"%,.0f".format(biller.recommendedAmount)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Input Card
            ClayInputContainer(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp)) {
                    Text(
                        text = "PAYMENT AMOUNT (USD)",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = ChampagneGold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = amountString,
                            onValueChange = { amountString = it },
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
                                .fillMaxWidth()
                                .testTag("bill_amount_input")
                        )
                    }
                }
            }

            // Quick Preset Amount Chips
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(150.0, 500.0, 1500.0, 5000.0).forEach { amt ->
                    ClayPill(
                        text = "$${"%,.0f".format(amt)}",
                        isSelected = parsedAmount == amt,
                        onClick = { amountString = amt.toInt().toString() }
                    )
                }
                ClayPill(
                    text = "CUSTOM",
                    isSelected = listOf(150.0, 500.0, 1500.0, 5000.0).none { it == parsedAmount },
                    onClick = { }
                )
            }

            if (isInsufficientBalance) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Insufficient available balance ($${"%,.2f".format(availableBalance)})",
                    color = Color(0xFFF43F5E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Biller Name & Account Number Inputs
            Text(
                text = "BILLER NAME",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.5.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = customBillerName,
                onValueChange = { customBillerName = it },
                placeholder = { Text("e.g. ConEdison, AT&T, Amex", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bill_biller_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = BankingTheme.colors.border,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "ACCOUNT / INVOICE NUMBER",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.5.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = accountNumberInput,
                onValueChange = { accountNumberInput = it },
                placeholder = { Text("e.g. 9821-4029-11", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bill_account_number_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = BankingTheme.colors.border,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "MEMO / REFERENCE",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.5.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = memoInput,
                onValueChange = { memoInput = it },
                placeholder = { Text("e.g. Monthly statement, Unit 42A", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bill_memo_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = BankingTheme.colors.border,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Security Guarantee Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BankingTheme.colors.surfaceCardElevated)
                    .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
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
                    text = "FedNow Direct Debit Clearing • Zero-fraud liability guarantee • Real-time alerts via Resend, Twilio & FCM",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.5.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button
            ClayButton(
                onClick = {
                    if (canSubmit) {
                        isExecuting = true
                        val cat = selectedBiller?.category ?: "Utilities"
                        onPayBill(
                            customBillerName,
                            cat,
                            parsedAmount,
                            accountNumberInput,
                            memoInput
                        ) {
                            isExecuting = false
                        }
                    }
                },
                enabled = canSubmit,
                variant = ClayVariant.GOLD,
                elevation = ClayElevation.HIGH,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_bill_pay_submit_btn")
            ) {
                if (isExecuting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Obsidian950,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Clearing FedNow Bill Pay...",
                        fontWeight = FontWeight.Bold,
                        color = Obsidian950,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = Obsidian950,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (parsedAmount > 0) "Authorize Bill Pay of $${"%,.2f".format(parsedAmount)}" else "Enter Payment Amount",
                        fontWeight = FontWeight.ExtraBold,
                        color = Obsidian950,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}
