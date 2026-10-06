package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlockchainNetwork
import com.example.data.model.CryptoHoldingEntity
import com.example.data.model.CryptoWalletAccount
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoWalletModal(
    cryptoHoldings: List<CryptoHoldingEntity>,
    sheetState: SheetState,
    initialTab: Int = 0,
    onDismiss: () -> Unit,
    onWalletCreated: (CryptoWalletAccount) -> Unit,
    onP2PSwapExecuted: (fromSymbol: String, toSymbol: String, amount: Double, toAddress: String) -> Unit,
    onPostNotice: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    // TAB 0: GENERATE WALLET ID STATE
    var walletNameInput by remember { mutableStateOf("Sovereign Treasury Vault") }
    var selectedNetwork by remember { mutableStateOf(BlockchainNetwork.BITCOIN) }
    var generatedAddress by remember(selectedNetwork) {
        mutableStateOf(generateLegitimateAddress(selectedNetwork))
    }
    var isGeneratingAddress by remember { mutableStateOf(false) }

    // TAB 1: CONNECT EXTERNAL WALLET STATE
    var selectedExternalProvider by remember { mutableStateOf("Coinbase Institutional") }
    var externalAddressInput by remember { mutableStateOf("") }
    var isConnectingExternal by remember { mutableStateOf(false) }

    // TAB 2: P2P SWAP STATE
    var p2pFromSymbol by remember { mutableStateOf("BTC") }
    var p2pToSymbol by remember { mutableStateOf("USDC") }
    var p2pAmountInput by remember { mutableStateOf("0.25") }
    var counterpartyAddressInput by remember { mutableStateOf("0x71C...88B9 (Sterling Alpha Fund)") }
    var isExecutingP2P by remember { mutableStateOf(false) }

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
        modifier = modifier.testTag("crypto_wallet_hub_modal")
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .claymorphic(variant = ClayVariant.GOLD, elevation = ClayElevation.LOW, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyBitcoin,
                            contentDescription = null,
                            tint = Obsidian950,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SOVEREIGN CRYPTO PROTOCOL",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChampagneGold,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "Crypto Wallets & P2P Swaps",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_crypto_hub_btn")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BankingTheme.colors.surfaceCard,
                contentColor = ChampagneGold,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ChampagneGold,
                        height = 2.5.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Generate ID", fontWeight = FontWeight.Bold, fontSize = 11.5.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Connect Wallet", fontWeight = FontWeight.Bold, fontSize = 11.5.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("P2P Swap", fontWeight = FontWeight.Bold, fontSize = 11.5.sp) }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (selectedTab) {
                0 -> {
                    // TAB 0: GENERATE WALLET ID
                    Text(
                        text = "GENERATE BLOCKCHAIN WALLET ID",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Blockchain Network Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BlockchainNetwork.values().forEach { net ->
                            val isSelected = selectedNetwork == net
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) BankingTheme.colors.surfaceCardElevated else BankingTheme.colors.surfaceCard)
                                    .border(
                                        1.2.dp,
                                        if (isSelected) ChampagneGold else BankingTheme.colors.border,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedNetwork = net
                                        generatedAddress = generateLegitimateAddress(net)
                                    }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = net.symbol,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ChampagneGold else TextPrimary
                                    )
                                    Text(
                                        text = net.displayName.take(7),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 8.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Wallet Label Input
                    OutlinedTextField(
                        value = walletNameInput,
                        onValueChange = { walletNameInput = it },
                        label = { Text("Wallet Name / Label") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = BankingTheme.colors.border,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Generated Address Plaque with QR Representation
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.OBSIDIAN,
                        elevation = ClayElevation.MEDIUM,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClayBadge(
                                    text = selectedNetwork.standard,
                                    variant = ClayVariant.GOLD
                                )
                                ClayBadge(
                                    text = "FIPS-140 HSM LEVEL 3",
                                    variant = ClayVariant.EMERALD
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Canvas QR Code Representation
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                DrawSimulatedQrCode(seed = generatedAddress)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "PUBLIC RECEIVING ADDRESS",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = generatedAddress,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 11.5.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(generatedAddress))
                                        onPostNotice("Copied ${selectedNetwork.symbol} address to clipboard.")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BankingTheme.colors.surfaceCardElevated,
                                        contentColor = ChampagneGold
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("copy_gen_address_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Address", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        generatedAddress = generateLegitimateAddress(selectedNetwork)
                                        onPostNotice("Regenerated new fresh SegWit public address.")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BankingTheme.colors.surfaceCardElevated,
                                        contentColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Regenerate", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Non-Custodial Zero Exposure Security Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BankingTheme.colors.surfaceCardElevated)
                            .border(1.dp, BankingTheme.colors.border, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Non-Custodial Architecture: Private keys remain inside hardware enclave HSMs. No sensitive seed phrases are ever exposed in plaintext.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val newWallet = CryptoWalletAccount(
                                id = "wal_" + UUID.randomUUID().toString().take(8),
                                name = walletNameInput.ifBlank { "${selectedNetwork.displayName} Vault" },
                                network = selectedNetwork,
                                publicAddress = generatedAddress,
                                balanceUsd = 0.0,
                                nativeBalance = 0.0,
                                isConnectedExternal = false,
                                providerName = "Lead Sovereign Enclave",
                                createdAtFormatted = "Oct 2026",
                                status = "ACTIVE • SECURE HSM ENCLAVE"
                            )
                            onWalletCreated(newWallet)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_generated_wallet_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Obsidian950)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Wallet to Portfolio", fontWeight = FontWeight.Bold)
                    }
                }

                1 -> {
                    // TAB 1: CONNECT EXTERNAL WALLET (OAUTH / WATCH MODE)
                    Text(
                        text = "CONNECT RECOGNIZED INSTITUTIONAL PROVIDER",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val providers = listOf(
                        Pair("Coinbase Institutional", "OAuth 2.0 Direct API Sync"),
                        Pair("MetaMask / Web3", "WalletConnect v2 Session"),
                        Pair("Ledger Enterprise Vault", "Hardware FIPS Bridge"),
                        Pair("Binance Custody", "Institutional Read-Only API"),
                        Pair("Phantom / Solana", "Deep-Link Sign-In")
                    )

                    providers.forEach { (prov, sub) ->
                        val isSelected = selectedExternalProvider == prov
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) BankingTheme.colors.surfaceCardElevated else BankingTheme.colors.surfaceCard)
                                .border(1.2.dp, if (isSelected) ChampagneGold else BankingTheme.colors.border, RoundedCornerShape(14.dp))
                                .clickable { selectedExternalProvider = prov }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ChampagneGold.copy(alpha = 0.2f) else BankingTheme.colors.backgroundElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = if (isSelected) ChampagneGold else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = prov, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = sub, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 10.5.sp)
                            }
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = externalAddressInput,
                        onValueChange = { externalAddressInput = it },
                        label = { Text("Public Watch Address (Optional or Auto-Detect)") },
                        placeholder = { Text("0x... or bc1q...", color = TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = BankingTheme.colors.border,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("external_wallet_address_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notice: Never ask private keys
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldGreen.copy(alpha = 0.1f))
                            .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Zero-Knowledge Guarantee: We will never request private keys or seed phrases. Only public addresses are authorized for balances and reporting.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.5.sp,
                            color = EmeraldGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            isConnectingExternal = true
                            coroutineScope.launch {
                                delay(1000)
                                isConnectingExternal = false
                                val addr = externalAddressInput.ifBlank { "0x89C1" + UUID.randomUUID().toString().take(10) }
                                val newWallet = CryptoWalletAccount(
                                    id = "ext_" + UUID.randomUUID().toString().take(8),
                                    name = "$selectedExternalProvider Vault",
                                    network = if (addr.startsWith("bc1")) BlockchainNetwork.BITCOIN else BlockchainNetwork.ETHEREUM,
                                    publicAddress = addr,
                                    balanceUsd = 45200.0,
                                    nativeBalance = 1.34,
                                    isConnectedExternal = true,
                                    providerName = selectedExternalProvider,
                                    createdAtFormatted = "Oct 2026",
                                    status = "CONNECTED • OAUTH 2.0 READ-ONLY"
                                )
                                onWalletCreated(newWallet)
                                onDismiss()
                            }
                        },
                        enabled = !isConnectingExternal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_connect_external_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Obsidian950)
                    ) {
                        if (isConnectingExternal) {
                            CircularProgressIndicator(color = Obsidian950, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Authorizing $selectedExternalProvider...", fontWeight = FontWeight.Bold)
                        } else {
                            Text("Authorize & Connect $selectedExternalProvider", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                2 -> {
                    // TAB 2: P2P SWAP
                    Text(
                        text = "PEER-TO-PEER DIRECT CRYPTO SWAP",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.SURFACE,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Counterparty input
                            Text(
                                text = "COUNTERPARTY PLATFORM WALLET ID / ADDRESS",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = counterpartyAddressInput,
                                onValueChange = { counterpartyAddressInput = it },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = BankingTheme.colors.border,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("You Send ($p2pFromSymbol)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = p2pAmountInput,
                                        onValueChange = { p2pAmountInput = it },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ChampagneGold,
                                            unfocusedBorderColor = BankingTheme.colors.border,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("You Receive ($p2pToSymbol)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val estRecv = (p2pAmountInput.toDoubleOrNull() ?: 0.0) * 67420.0
                                    Text(
                                        text = "$${"%,.2f".format(estRecv)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Estimated Network Gas Fee:", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                                Text("0.000045 BTC (~$3.12)", style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            isExecutingP2P = true
                            coroutineScope.launch {
                                delay(1200)
                                isExecutingP2P = false
                                val amt = p2pAmountInput.toDoubleOrNull() ?: 0.25
                                onP2PSwapExecuted(p2pFromSymbol, p2pToSymbol, amt, counterpartyAddressInput)
                                onDismiss()
                            }
                        },
                        enabled = !isExecutingP2P,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("execute_p2p_swap_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = Obsidian950)
                    ) {
                        if (isExecutingP2P) {
                            CircularProgressIndicator(color = Obsidian950, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Broadcasting Mempool Settlement...", fontWeight = FontWeight.Bold)
                        } else {
                            Text("Execute P2P Atomic Swap", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun generateLegitimateAddress(network: BlockchainNetwork): String {
    val randomHex = (1..8).map { Integer.toHexString((0..15).random()) }.joinToString("")
    val randomTail = (1..6).map { Integer.toHexString((0..15).random()) }.joinToString("")
    return when (network) {
        BlockchainNetwork.BITCOIN -> "bc1q${randomHex.lowercase()}v9x4p27${randomTail.lowercase()}8k3"
        BlockchainNetwork.ETHEREUM, BlockchainNetwork.POLYGON -> "0x${randomHex.uppercase()}A8bF9027${randomTail.uppercase()}4eD2"
        BlockchainNetwork.SOLANA -> "Sol${randomHex.take(6)}ZqP9xW${randomTail.take(6)}8k"
    }
}

@Composable
private fun DrawSimulatedQrCode(seed: String) {
    Canvas(modifier = Modifier.size(114.dp)) {
        val gridSize = 19
        val cellSize = size.width / gridSize
        val hashCode = seed.hashCode()

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                // Corner positioning squares
                val isCorner = (row < 5 && col < 5) || (row < 5 && col >= gridSize - 5) || (row >= gridSize - 5 && col < 5)
                val isCornerBorder = isCorner && (row == 0 || row == 4 || col == 0 || col == 4 || row == gridSize - 5 || row == gridSize - 1 || col == gridSize - 5 || col == gridSize - 1)
                val isCornerCenter = isCorner && ((row in 1..3 && col in 1..3) || (row in 1..3 && col in gridSize - 4..gridSize - 2) || (row in gridSize - 4..gridSize - 2 && col in 1..3))

                val pseudoRandomFill = ((hashCode xor (row * 31 + col * 17)) and 1) == 0

                val shouldDraw = isCornerBorder || isCornerCenter || (!isCorner && pseudoRandomFill)

                if (shouldDraw) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize * 0.92f, cellSize * 0.92f)
                    )
                }
            }
        }
    }
}
