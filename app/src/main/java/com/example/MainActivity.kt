package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BillsModal
import com.example.ui.components.BiometricAuthModal
import com.example.ui.components.ClayFloatingNavigationBar
import com.example.ui.components.ClayNavItemData
import com.example.ui.components.CreateCardModal
import com.example.ui.components.CreateGoalModal
import com.example.ui.components.DepositModal
import com.example.ui.components.ExportStatementModal
import com.example.ui.components.LeadTopBar
import com.example.ui.components.NotificationDrawer
import com.example.ui.components.ReceiptModal
import com.example.ui.components.TransactionDetailSheet
import com.example.ui.components.TransferModal
import com.example.ui.components.WithdrawalModal
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CardsScreen
import com.example.ui.screens.CryptoDeskScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.TransfersScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BankingViewModel
import com.example.ui.viewmodel.NavigationTab
import kotlinx.coroutines.delay

class MainActivity : FragmentActivity() {

    private val viewModel: BankingViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkMode) {
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val accounts by viewModel.accounts.collectAsStateWithLifecycle()
                val selectedAccountId by viewModel.selectedAccountId.collectAsStateWithLifecycle()
                val cards by viewModel.cards.collectAsStateWithLifecycle()
                val selectedCardIndex by viewModel.selectedCardIndex.collectAsStateWithLifecycle()
                val isCardFlipped by viewModel.isCardFlipped.collectAsStateWithLifecycle()
                val showCardDetails by viewModel.showCardDetails.collectAsStateWithLifecycle()
                val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
                val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()
                val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
                val notifications by viewModel.notifications.collectAsStateWithLifecycle()
                val bannerNotice by viewModel.bannerNotice.collectAsStateWithLifecycle()

                val cryptoHoldings by viewModel.cryptoHoldings.collectAsStateWithLifecycle()
                val cryptoOrders by viewModel.cryptoOrders.collectAsStateWithLifecycle()
                val selectedCryptoSymbol by viewModel.selectedCryptoSymbol.collectAsStateWithLifecycle()
                val supabaseSyncState by viewModel.supabaseSyncState.collectAsStateWithLifecycle()

                val isTransferModalOpen by viewModel.isTransferModalOpen.collectAsStateWithLifecycle()
                val isWithdrawalModalOpen by viewModel.isWithdrawalModalOpen.collectAsStateWithLifecycle()
                val isBillsModalOpen by viewModel.isBillsModalOpen.collectAsStateWithLifecycle()
                val isDepositModalOpen by viewModel.isDepositModalOpen.collectAsStateWithLifecycle()
                val isCreateCardModalOpen by viewModel.isCreateCardModalOpen.collectAsStateWithLifecycle()
                val isCreateGoalModalOpen by viewModel.isCreateGoalModalOpen.collectAsStateWithLifecycle()
                val isNotificationDrawerOpen by viewModel.isNotificationDrawerOpen.collectAsStateWithLifecycle()
                val isExportStatementModalOpen by viewModel.isExportStatementModalOpen.collectAsStateWithLifecycle()
                val selectedTx by viewModel.selectedTransaction.collectAsStateWithLifecycle()
                val selectedReceiptTx by viewModel.selectedReceiptTx.collectAsStateWithLifecycle()

                val biometricsActive by viewModel.biometricsActive.collectAsStateWithLifecycle()
                val fraudShieldActive by viewModel.fraudShieldActive.collectAsStateWithLifecycle()
                val selectedTierId by viewModel.selectedTierId.collectAsStateWithLifecycle()
                val userName by viewModel.userName.collectAsStateWithLifecycle()
                val userNickname by viewModel.userNickname.collectAsStateWithLifecycle()

                val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
                val isWelcomeScreenSeen by viewModel.isWelcomeScreenSeen.collectAsStateWithLifecycle()

                // Real-time Multi-Channel Alerts & Room Local Cache States
                val alertLogs by viewModel.alertLogs.collectAsStateWithLifecycle()
                val cachedTransactions by viewModel.cachedTransactions.collectAsStateWithLifecycle()
                val linkedAccountsMetadata by viewModel.linkedAccountsMetadata.collectAsStateWithLifecycle()

                // Superior Admin State
                val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsStateWithLifecycle()
                val adminOperatorEmail by viewModel.adminOperatorEmail.collectAsStateWithLifecycle()
                val adminSelectedTab by viewModel.adminSelectedTab.collectAsStateWithLifecycle()
                val adminTickets by viewModel.adminTickets.collectAsStateWithLifecycle()
                val adminAuditLogs by viewModel.adminAuditLogs.collectAsStateWithLifecycle()
                val customerEmails by viewModel.customerEmails.collectAsStateWithLifecycle()
                val systemGateways by viewModel.systemGateways.collectAsStateWithLifecycle()

                // Biometric Verification for Sensitive Banking Operations
                var pendingTransferAction by remember { mutableStateOf<(() -> Unit)?>(null) }
                var pendingTransferTitle by remember { mutableStateOf("") }
                var pendingTransferDetails by remember { mutableStateOf("") }
                var pendingTransferAmount by remember { mutableStateOf<String?>(null) }
                var isBiometricAuthOpen by remember { mutableStateOf(false) }
                val biometricSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                // BottomSheet states
                val transferSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val withdrawalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val billsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val depositSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val cardSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val goalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val notifSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val txSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val exportSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val receiptSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                // BackHandler behavior
                BackHandler(enabled = !isAuthenticated || currentTab != NavigationTab.OVERVIEW || selectedTx != null || selectedReceiptTx != null || isExportStatementModalOpen || isWithdrawalModalOpen || isBillsModalOpen || isBiometricAuthOpen) {
                    when {
                        isBiometricAuthOpen -> {
                            isBiometricAuthOpen = false
                            pendingTransferAction = null
                        }
                        selectedReceiptTx != null -> viewModel.closeReceiptModal()
                        isExportStatementModalOpen -> viewModel.closeExportStatementModal()
                        isWithdrawalModalOpen -> viewModel.closeWithdrawalModal()
                        isBillsModalOpen -> viewModel.closeBillsModal()
                        selectedTx != null -> viewModel.closeTransactionDetail()
                        !isAuthenticated && isWelcomeScreenSeen -> viewModel.backToWelcome()
                        currentTab != NavigationTab.OVERVIEW -> viewModel.setTab(NavigationTab.OVERVIEW)
                    }
                }

                // Auto dismiss notice after 3.5s
                LaunchedEffect(bannerNotice) {
                    if (bannerNotice != null) {
                        delay(3500)
                        viewModel.dismissNotice()
                    }
                }

                if (!isWelcomeScreenSeen) {
                    WelcomeScreen(
                        onNavigateToLogin = { viewModel.proceedFromWelcomeToLogin() },
                        onQuickDemoAccess = { viewModel.login() }
                    )
                } else if (!isAuthenticated) {
                    LoginScreen(
                        onLoginSuccess = { viewModel.login() },
                        onBackToWelcome = { viewModel.backToWelcome() }
                    )
                } else {
                    val activeAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()

                    Box(modifier = Modifier.fillMaxSize().background(BankingTheme.colors.background)) {
                        Scaffold(
                            topBar = {
                                LeadTopBar(
                                    accounts = accounts,
                                    selectedAccountId = selectedAccountId,
                                    unreadNotificationCount = notifications.size,
                                    supabaseSyncState = supabaseSyncState,
                                    isDarkMode = isDarkMode,
                                    onToggleTheme = { viewModel.toggleTheme() },
                                    onOpenExportStatement = { viewModel.openExportStatementModal() },
                                    onToggleNetwork = { viewModel.toggleNetworkOnline(it) },
                                    onTriggerSync = { viewModel.triggerSupabaseSync() },
                                    onAccountSelect = { viewModel.selectAccount(it) },
                                    onNotificationsClick = { viewModel.toggleNotificationDrawer() },
                                    onSecurityShieldClick = { viewModel.setTab(NavigationTab.SECURITY) },
                                    onAdminClick = { viewModel.setTab(NavigationTab.ADMIN) },
                                    onLockVault = { viewModel.logout() },
                                    modifier = Modifier.statusBarsPadding()
                                )
                            },
                        bottomBar = {
                            val navItems = listOf(
                                ClayNavItemData("Overview", Icons.Default.AccountBalance, "nav_overview"),
                                ClayNavItemData("Transfers", Icons.Default.SwapHoriz, "nav_transfers"),
                                ClayNavItemData("Cards", Icons.Default.CreditCard, "nav_cards"),
                                ClayNavItemData("Crypto", Icons.Default.CurrencyBitcoin, "nav_crypto"),
                                ClayNavItemData("Analytics", Icons.Default.TrendingUp, "nav_analytics"),
                                ClayNavItemData("Security", Icons.Default.Security, "nav_security")
                            )
                            val selectedIndex = when (currentTab) {
                                NavigationTab.OVERVIEW -> 0
                                NavigationTab.TRANSFERS -> 1
                                NavigationTab.CARDS -> 2
                                NavigationTab.CRYPTO -> 3
                                NavigationTab.ANALYTICS -> 4
                                NavigationTab.SECURITY -> 5
                                NavigationTab.ADMIN -> 5
                            }

                            ClayFloatingNavigationBar(
                                items = navItems,
                                selectedIndex = selectedIndex,
                                onSelectIndex = { idx ->
                                    when (idx) {
                                        0 -> viewModel.setTab(NavigationTab.OVERVIEW)
                                        1 -> viewModel.setTab(NavigationTab.TRANSFERS)
                                        2 -> viewModel.setTab(NavigationTab.CARDS)
                                        3 -> viewModel.setTab(NavigationTab.CRYPTO)
                                        4 -> viewModel.setTab(NavigationTab.ANALYTICS)
                                        5 -> viewModel.setTab(NavigationTab.SECURITY)
                                    }
                                },
                                modifier = Modifier.navigationBarsPadding()
                            )
                        },
                        containerColor = BankingTheme.colors.background
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                NavigationTab.OVERVIEW -> DashboardScreen(
                                    accounts = accounts,
                                    selectedAccountId = selectedAccountId,
                                    cards = cards,
                                    isCardFlipped = isCardFlipped,
                                    showCardDetails = showCardDetails,
                                    recentTransactions = transactions,
                                    searchQuery = searchQuery,
                                    selectedCategoryFilter = categoryFilter,
                                    cryptoHoldings = cryptoHoldings,
                                    userName = userName,
                                    userNickname = userNickname,
                                    onUpdateNickname = { viewModel.updateNickname(it) },
                                    onTopUp = { amount, method -> viewModel.topUpAccount(amount, method) },
                                    onCopyNotice = { viewModel.postNotice(it) },
                                    onSelectAccount = { viewModel.selectAccount(it) },
                                    onCardFlip = { viewModel.toggleCardFlip() },
                                    onToggleCardDetails = { viewModel.toggleShowCardDetails() },
                                    onToggleCardFreeze = { cardId, currentFrozen -> viewModel.toggleCardFreeze(cardId, currentFrozen) },
                                    onCopyCardNumber = { viewModel.postNotice("Card number copied to secure clipboard.") },
                                    onOpenTransfer = { viewModel.openTransferModal() },
                                    onOpenWithdrawal = { viewModel.openWithdrawalModal() },
                                    onOpenBills = { viewModel.openBillsModal() },
                                    onOpenDeposit = { viewModel.openDepositModal() },
                                    onOpenCreateCard = { viewModel.openCreateCardModal() },
                                    onOpenExportStatement = { viewModel.openExportStatementModal() },
                                    onOpenReceipt = { tx -> viewModel.openReceiptModal(tx) },
                                    onExecuteCryptoSwap = { from, to, amount, slip, accId ->
                                        viewModel.executeCryptoSwap(from, to, amount, slip, accId)
                                    },
                                    onSearchChange = { viewModel.setSearchQuery(it) },
                                    onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                                    onTransactionClick = { viewModel.openTransactionDetail(it) },
                                    onViewAllTransfers = { viewModel.setTab(NavigationTab.TRANSFERS) },
                                    onNavigateCrypto = { viewModel.setTab(NavigationTab.CRYPTO) },
                                    onUpgradeTier = { viewModel.setTab(NavigationTab.SECURITY) }
                                )

                                NavigationTab.TRANSFERS -> TransfersScreen(
                                    payees = viewModel.payees,
                                    transferTransactions = allTransactions,
                                    onOpenTransfer = { viewModel.openTransferModal() },
                                    onOpenExportStatement = { viewModel.openExportStatementModal() },
                                    onOpenReceipt = { tx -> viewModel.openReceiptModal(tx) },
                                    onTransactionClick = { viewModel.openTransactionDetail(it) }
                                )

                                NavigationTab.CARDS -> CardsScreen(
                                    cards = cards,
                                    selectedCardIndex = selectedCardIndex,
                                    isCardFlipped = isCardFlipped,
                                    showCardDetails = showCardDetails,
                                    allTransactions = allTransactions,
                                    onSelectCardIndex = { viewModel.selectCardIndex(it) },
                                    onFlipCard = { viewModel.toggleCardFlip() },
                                    onToggleCardDetails = { viewModel.toggleShowCardDetails() },
                                    onToggleCardFreeze = { id, f -> viewModel.toggleCardFreeze(id, f) },
                                    onUpdateCardLimit = { id, limit -> viewModel.updateCardLimit(id, limit) },
                                    onCopyNumber = { viewModel.postNotice("Card number copied.") },
                                    onOpenCreateCard = { viewModel.openCreateCardModal() },
                                    onOpenExportStatement = { viewModel.openExportStatementModal() },
                                    onExecuteCardTransaction = { cardId, merchant, amount, category, memo ->
                                        viewModel.executeCardTransaction(cardId, merchant, amount, category, memo)
                                    },
                                    onTransactionClick = { viewModel.openTransactionDetail(it) }
                                )

                                NavigationTab.CRYPTO -> CryptoDeskScreen(
                                    accounts = accounts,
                                    cryptoHoldings = cryptoHoldings,
                                    cryptoOrders = cryptoOrders,
                                    selectedSymbol = selectedCryptoSymbol,
                                    onSelectSymbol = { viewModel.selectCryptoSymbol(it) },
                                    onExecuteSwap = { from, to, amount, slip, accId ->
                                        viewModel.executeCryptoSwap(from, to, amount, slip, accId)
                                    },
                                    getMarketDepth = { sym, price -> viewModel.getMarketDepth(sym, price) }
                                )

                                NavigationTab.ANALYTICS -> AnalyticsScreen(
                                    savingsGoals = savingsGoals,
                                    onOpenCreateGoal = { viewModel.openCreateGoalModal() },
                                    onDepositToGoal = { id, amount -> viewModel.depositToGoal(id, amount) },
                                    onOpenExportStatement = { viewModel.openExportStatementModal() }
                                )

                                NavigationTab.SECURITY -> SecurityScreen(
                                    planTiers = viewModel.planTiers,
                                    selectedTierId = selectedTierId,
                                    biometricsActive = biometricsActive,
                                    fraudShieldActive = fraudShieldActive,
                                    isDarkMode = isDarkMode,
                                    onToggleBiometrics = { viewModel.toggleBiometrics() },
                                    onToggleFraudShield = { viewModel.toggleFraudShield() },
                                    onToggleTheme = { viewModel.toggleTheme() },
                                    onOpenExportStatement = { viewModel.openExportStatementModal() },
                                    onSelectTier = { viewModel.selectTier(it) },
                                    onOpenCreateCard = { viewModel.openCreateCardModal() },
                                    onOpenAdminConsole = { viewModel.setTab(NavigationTab.ADMIN) },
                                    onOpenAlertCenter = { viewModel.toggleNotificationDrawer() },
                                    onTriggerTestApproval = { viewModel.triggerTestTransactionApprovalAlert() },
                                    onTriggerTestSecurity = { viewModel.triggerTestSecurityAlert() },
                                    onTriggerTestDeposit = { viewModel.triggerTestDepositAlert() },
                                    onLockVault = { viewModel.logout() }
                                )

                                NavigationTab.ADMIN -> AdminDashboardScreen(
                                    accounts = accounts,
                                    cards = cards,
                                    allTransactions = allTransactions,
                                    tickets = adminTickets,
                                    immutableAuditLogs = adminAuditLogs,
                                    customerEmails = customerEmails,
                                    gateways = systemGateways,
                                    selectedTab = adminSelectedTab,
                                    isUnlocked = isAdminUnlocked,
                                    operatorEmail = adminOperatorEmail,
                                    onTabSelect = { viewModel.setAdminTab(it) },
                                    onAuthenticate = { email, pass -> viewModel.authenticateAdmin(email, pass) },
                                    onLock = { viewModel.lockAdmin() },
                                    onAdjustBalance = { accId, amt, isCr, rsn, memo ->
                                        viewModel.adminAdjustAccountBalance(accId, amt, isCr, rsn, memo)
                                    },
                                    onInjectWire = { accId, sender, bank, routing, amt, rail, memo ->
                                        viewModel.adminInjectWire(accId, sender, bank, routing, amt, rail, memo)
                                    },
                                    onResolveDispute = { txId, approve, targetAcc, note ->
                                        viewModel.adminResolveDispute(txId, approve, targetAcc, note)
                                    },
                                    onOverrideCard = { cardId, frozen, limit ->
                                        viewModel.adminOverrideCard(cardId, frozen, limit)
                                    },
                                    onDeleteTransaction = { txId ->
                                        viewModel.adminDeleteTransaction(txId)
                                    },
                                    onReplyTicket = { ticketId, reply, st ->
                                        viewModel.adminReplyTicket(ticketId, reply, st)
                                    },
                                    onCreditCourtesyCompensation = { ticketId, accId, amt, reason ->
                                        viewModel.adminCreditCourtesyCompensation(ticketId, accId, amt, reason)
                                    },
                                    onBroadcastAlert = { title, message, priority ->
                                        viewModel.adminBroadcastAlert(title, message, priority)
                                    },
                                    onDispatchCustomerEmail = { recipientEmail, recipientName, subject, templateType, headline, bodyText, amount ->
                                        viewModel.dispatchCustomerEmail(recipientEmail, recipientName, subject, templateType, headline, bodyText, amount)
                                    },
                                    onUpdateAccountConfig = { accountId, name, apy, routing ->
                                        viewModel.updateAccountConfiguration(accountId, name, apy, routing)
                                    }
                                )
                            }
                        }
                    }

                    // Floating Notification Banner
                    AnimatedVisibility(
                        visible = bannerNotice != null,
                        enter = slideInVertically { -it } + fadeIn(),
                        exit = slideOutVertically { -it } + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                    ) {
                        bannerNotice?.let { notice ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(BankingTheme.colors.surfaceCard)
                                    .border(1.2.dp, BankingTheme.colors.primaryAccent, RoundedCornerShape(14.dp))
                                    .clickable { viewModel.dismissNotice() }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = BankingTheme.colors.primaryAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = notice,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BankingTheme.colors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Modals
                    if (isTransferModalOpen) {
                        TransferModal(
                            payees = viewModel.payees,
                            availableBalance = activeAccount?.balance ?: 100000.0,
                            sheetState = transferSheetState,
                            onDismiss = { viewModel.closeTransferModal() },
                            onSendTransfer = { recipient, amount, note, rail ->
                                if (biometricsActive) {
                                    viewModel.closeTransferModal()
                                    pendingTransferAction = {
                                        viewModel.sendMoney(recipient, amount, note, rail) { }
                                    }
                                    pendingTransferTitle = "Authorize $rail Transfer"
                                    pendingTransferDetails = "Recipient: $recipient • Note: ${note.ifBlank { "Direct Settlement" }}"
                                    pendingTransferAmount = "$${"%,.2f".format(amount)}"
                                    isBiometricAuthOpen = true
                                } else {
                                    viewModel.sendMoney(recipient, amount, note, rail) { }
                                }
                            }
                        )
                    }

                    if (isWithdrawalModalOpen) {
                        WithdrawalModal(
                            accounts = accounts,
                            selectedAccountId = selectedAccountId,
                            sheetState = withdrawalSheetState,
                            onDismiss = { viewModel.closeWithdrawalModal() },
                            onWithdraw = { destination, amount, method, referenceNote ->
                                if (biometricsActive) {
                                    viewModel.closeWithdrawalModal()
                                    pendingTransferAction = {
                                        viewModel.withdrawMoney(destination, amount, method, referenceNote) { }
                                    }
                                    pendingTransferTitle = "Authorize $method Withdrawal"
                                    pendingTransferDetails = "Destination: $destination • Ref: ${referenceNote.ifBlank { "Vault Clearance" }}"
                                    pendingTransferAmount = "$${"%,.2f".format(amount)}"
                                    isBiometricAuthOpen = true
                                } else {
                                    viewModel.withdrawMoney(destination, amount, method, referenceNote) { }
                                }
                            }
                        )
                    }

                    if (isBillsModalOpen) {
                        BillsModal(
                            accounts = accounts,
                            selectedAccountId = selectedAccountId,
                            sheetState = billsSheetState,
                            onDismiss = { viewModel.closeBillsModal() },
                            onPayBill = { billerName, billCategory, amount, accountNumber, memo, onComplete ->
                                if (biometricsActive) {
                                    viewModel.closeBillsModal()
                                    pendingTransferAction = {
                                        viewModel.payBill(billerName, billCategory, amount, accountNumber, memo, onComplete)
                                    }
                                    pendingTransferTitle = "Authorize Settlement: $billerName"
                                    pendingTransferDetails = "Category: $billCategory • Acct: $accountNumber"
                                    pendingTransferAmount = "$${"%,.2f".format(amount)}"
                                    isBiometricAuthOpen = true
                                } else {
                                    viewModel.payBill(billerName, billCategory, amount, accountNumber, memo, onComplete)
                                }
                            }
                        )
                    }

                    if (isDepositModalOpen) {
                        DepositModal(
                            sheetState = depositSheetState,
                            onDismiss = { viewModel.closeDepositModal() },
                            onDeposit = { amount, src ->
                                viewModel.depositMoney(amount, src)
                            }
                        )
                    }

                    if (isCreateCardModalOpen) {
                        CreateCardModal(
                            accounts = accounts,
                            sheetState = cardSheetState,
                            onDismiss = { viewModel.closeCreateCardModal() },
                            onLinkOfficialBankCard = { bankName, productName, cardholderName, cardNumber, expiryDate, cvv, theme, network, limit, linkedAccountId ->
                                viewModel.linkOfficialBankCard(
                                    bankName = bankName,
                                    productName = productName,
                                    cardholderName = cardholderName,
                                    cardNumber = cardNumber,
                                    expiryDate = expiryDate,
                                    cvv = cvv,
                                    theme = theme,
                                    network = network,
                                    spendingLimit = limit,
                                    linkedAccountId = linkedAccountId
                                )
                            },
                            onCreateLeadVirtualCard = { theme, network, limit, linkedAccountId ->
                                viewModel.createLeadVirtualCard(theme, network, limit, linkedAccountId)
                            }
                        )
                    }

                    if (isCreateGoalModalOpen) {
                        CreateGoalModal(
                            sheetState = goalSheetState,
                            onDismiss = { viewModel.closeCreateGoalModal() },
                            onCreateGoal = { title, target, initial ->
                                viewModel.createGoal(title, target, initial)
                            }
                        )
                    }

                    if (isNotificationDrawerOpen) {
                        NotificationDrawer(
                            notifications = notifications,
                            alertLogs = alertLogs,
                            cachedTransactions = cachedTransactions,
                            linkedAccountsMetadata = linkedAccountsMetadata,
                            onTriggerTestApproval = { viewModel.triggerTestTransactionApprovalAlert() },
                            onTriggerTestSecurity = { viewModel.triggerTestSecurityAlert() },
                            onTriggerTestDeposit = { viewModel.triggerTestDepositAlert() },
                            onSyncRoomCache = { viewModel.syncRoomCacheNow() },
                            onClearAlertLogs = { viewModel.clearAlertLogs() },
                            sheetState = notifSheetState,
                            onDismiss = { viewModel.toggleNotificationDrawer() }
                        )
                    }

                    if (isExportStatementModalOpen) {
                        ExportStatementModal(
                            accounts = accounts,
                            allTransactions = allTransactions,
                            initialAccountId = selectedAccountId,
                            sheetState = exportSheetState,
                            onDismiss = { viewModel.closeExportStatementModal() },
                            onPostNotice = { viewModel.postNotice(it) }
                        )
                    }

                    selectedTx?.let { tx ->
                        TransactionDetailSheet(
                            transaction = tx,
                            sheetState = txSheetState,
                            onDismiss = { viewModel.closeTransactionDetail() },
                            onToggleDispute = { viewModel.toggleDispute(tx.id, tx.isDisputed) },
                            onOpenReceipt = { targetTx ->
                                viewModel.closeTransactionDetail()
                                viewModel.openReceiptModal(targetTx)
                            }
                        )
                    }

                    selectedReceiptTx?.let { tx ->
                        ReceiptModal(
                            transaction = tx,
                            account = activeAccount,
                            sheetState = receiptSheetState,
                            onDismiss = { viewModel.closeReceiptModal() },
                            onPostNotice = { viewModel.postNotice(it) }
                        )
                    }

                    if (isBiometricAuthOpen) {
                        BiometricAuthModal(
                            operationTitle = pendingTransferTitle,
                            operationDetails = pendingTransferDetails,
                            amountText = pendingTransferAmount,
                            sheetState = biometricSheetState,
                            onDismiss = {
                                isBiometricAuthOpen = false
                                pendingTransferAction = null
                            },
                            onConfirmed = {
                                isBiometricAuthOpen = false
                                pendingTransferAction?.invoke()
                                pendingTransferAction = null
                                viewModel.postNotice("Biometric clearance verified. Operation authorized and settled.")
                            }
                        )
                    }
                }
            }
            }
        }
    }
}
