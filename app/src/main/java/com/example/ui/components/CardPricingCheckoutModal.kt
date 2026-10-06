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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.CardNetwork
import com.example.data.model.CardTheme
import com.example.data.model.CardTier
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CardCheckoutPaymentMethod(val title: String, val subtitle: String, val badge: String) {
    ACCOUNT_BALANCE("Lead Primary Checking", "Immediate internal clearing from available cash", "Direct Debit"),
    STRIPE_SANDBOX("Stripe PCI-DSS Card Processor", "Sandbox gateway simulation with instant tokenization", "Stripe API"),
    FEDNOW_WIRE("FedNow Instant Settlement", "Federal Reserve 24/7 RTGS liquid dispatch", "FedNow RTGS")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPricingCheckoutModal(
    productName: String = "Lead Sovereign Obsidian Reserve",
    tier: CardTier = CardTier.METAL,
    accounts: List<AccountEntity>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirmPaymentAndIssue: (
        productName: String,
        theme: CardTheme,
        tier: CardTier,
        network: CardNetwork,
        spendingLimit: Double,
        linkedAccountId: String,
        paymentMethod: String,
        deliveryAddress: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedPaymentMethod by remember { mutableStateOf(CardCheckoutPaymentMethod.ACCOUNT_BALANCE) }
    var selectedTheme by remember { mutableStateOf(CardTheme.OBSIDIAN) }
    var selectedNetwork by remember { mutableStateOf(CardNetwork.VISA_INFINITE) }
    var deliveryAddress by remember { mutableStateOf("432 Park Ave, Penthouse 88, New York, NY 10022") }
    var selectedLimit by remember { mutableStateOf(50000.0) }
    val defaultAccount = accounts.find { it.id == "acc_checking" } ?: accounts.firstOrNull()

    var isProcessing by remember { mutableStateOf(false) }
    var isConfirmed by remember { mutableStateOf(false) }
    var orderRefId by remember { mutableStateOf("ORD-${(100000..999999).random()}") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BankingTheme.colors.backgroundElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BankingTheme.colors.border)
            )
        },
        modifier = modifier.testTag("card_pricing_checkout_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ISSUANCE & ACQUISITION ORDER",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.3.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold,
                        fontSize = 9.sp
                    )
                    Text(
                        text = if (tier == CardTier.METAL) "Heavy Metal Card Application" else "Virtual Card Issuance",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_checkout_modal_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Order Card Summary Plaque
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                variant = ClayVariant.OBSIDIAN,
                elevation = ClayElevation.MEDIUM,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = productName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${tier.label} • ${selectedNetwork.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ChampagneGold,
                                fontSize = 11.5.sp
                            )
                        }

                        ClayBadge(
                            text = "$299.99 UPFRONT",
                            variant = ClayVariant.GOLD
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(BankingTheme.colors.border)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Itemized Fee Breakdown (Transparency)
                    Text(
                        text = "ITEMIZED UPFRONT FEE BREAKDOWN",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val lineItems = listOf(
                        Pair(if (tier == CardTier.METAL) "18g Laser Heavy Metal Fabrication & Armored Courier" else "Instant HSM Cloud Virtual Tokenization & Enclave Registration", "$150.00"),
                        Pair("FedNow & Stripe Instant KYC Identity Verification & AML Clearance", "$99.99"),
                        Pair("1-Year $5,000,000 FDIC Multi-Bank Sweep Coverage & Dedicated Concierge", "$50.00")
                    )

                    lineItems.forEach { (desc, cost) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• $desc",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = cost,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Upfront Issuance Fee",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "$299.99",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = ChampagneGold,
                            fontSize = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Payment Method Selection
            Text(
                text = "SELECT PAYMENT SETTLEMENT METHOD",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.5.sp,
                letterSpacing = 1.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            CardCheckoutPaymentMethod.values().forEach { method ->
                val isSelected = selectedPaymentMethod == method
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) BankingTheme.colors.surfaceCardElevated else BankingTheme.colors.surfaceCard)
                        .border(
                            1.5.dp,
                            if (isSelected) ChampagneGold else BankingTheme.colors.border,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedPaymentMethod = method }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ChampagneGold.copy(alpha = 0.2f) else BankingTheme.colors.backgroundElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (method) {
                                CardCheckoutPaymentMethod.ACCOUNT_BALANCE -> Icons.Default.AccountBalance
                                CardCheckoutPaymentMethod.STRIPE_SANDBOX -> Icons.Default.CreditCard
                                CardCheckoutPaymentMethod.FEDNOW_WIRE -> Icons.Default.Payment
                            },
                            contentDescription = null,
                            tint = if (isSelected) ChampagneGold else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = method.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ClayBadge(text = method.badge, variant = if (isSelected) ClayVariant.GOLD else ClayVariant.SURFACE)
                        }
                        Text(
                            text = method.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery Destination (for Physical Cards) or Linked Account
            if (tier == CardTier.METAL) {
                Text(
                    text = "ARMORED COURIER DELIVERY DESTINATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = BankingTheme.colors.border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("checkout_delivery_address")
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Regulatory Sandbox & Consumer Disclosure
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BankingTheme.colors.surfaceCardElevated)
                    .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Regulated Bank Issuing & Stripe Integration: All cards are issued via licensed partner institutions. In prototype mode, transactions process via the simulated Stripe and FedNow clearing engine.",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Authorize & Pay Button
            Button(
                onClick = {
                    isProcessing = true
                    coroutineScope.launch {
                        delay(1200) // realistic settlement delay
                        isProcessing = false
                        isConfirmed = true
                        onConfirmPaymentAndIssue(
                            productName,
                            selectedTheme,
                            tier,
                            selectedNetwork,
                            selectedLimit,
                            defaultAccount?.id ?: "acc_checking",
                            selectedPaymentMethod.name,
                            deliveryAddress
                        )
                    }
                },
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("pay_and_issue_card_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = Obsidian950
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Obsidian950, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Clearing $299.99 Fee via ${selectedPaymentMethod.badge}...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pay $299.99 & Issue ${if (tier == CardTier.METAL) "Metal Card" else "Virtual Card"}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Order Ref: $orderRefId • 30-Day Money-Back Guarantee if unactivated",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
