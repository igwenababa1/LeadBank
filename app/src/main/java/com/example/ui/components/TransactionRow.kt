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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import com.example.ui.theme.BankingTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun TransactionRow(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    onReceiptClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isCredit = transaction.amount > 0
    val icon = getCategoryIcon(transaction.category)
    val formattedDate = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(transaction.timestamp))

    ClayCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tx_item_${transaction.id}"),
        variant = if (BankingTheme.colors.isDark) ClayVariant.OBSIDIAN else ClayVariant.SURFACE,
        elevation = ClayElevation.LOW,
        shape = RoundedCornerShape(18.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val isBtc = transaction.category == TransactionCategory.CRYPTO_TRADE ||
                        transaction.title.contains("BTC", ignoreCase = true) ||
                        transaction.title.contains("Bitcoin", ignoreCase = true)
                val isUsdCash = transaction.title.contains("Deposit", ignoreCase = true) ||
                        transaction.title.contains("USD", ignoreCase = true)

                // Category Icon Clay Sphere
                if (isBtc) {
                    OfficialBitcoinIcon(size = 40.dp)
                } else if (isUsdCash) {
                    OfficialUsdIcon(size = 40.dp)
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .claymorphic(
                                variant = if (isCredit) ClayVariant.EMERALD else ClayVariant.SURFACE,
                                shape = RoundedCornerShape(14.dp),
                                elevation = ClayElevation.LOW,
                                isDark = BankingTheme.colors.isDark
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = transaction.category.displayName,
                            tint = if (isCredit) Color.Black else BankingTheme.colors.primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = transaction.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = BankingTheme.colors.textPrimary,
                            fontSize = 13.5.sp,
                            maxLines = 1
                        )

                        if (transaction.status == TransactionStatus.PENDING) {
                            Spacer(modifier = Modifier.width(6.dp))
                            ClayBadge(
                                text = "PENDING",
                                variant = ClayVariant.GOLD,
                                textColor = Color.Black
                            )
                        }

                        if (transaction.isDisputed) {
                            Spacer(modifier = Modifier.width(6.dp))
                            ClayBadge(
                                text = "DISPUTED",
                                variant = ClayVariant.CRIMSON,
                                textColor = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${transaction.merchant} • $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = BankingTheme.colors.textSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Amount & optional receipt quick trigger
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    val prefix = if (isCredit) "+" else "-"
                    val absAmount = abs(transaction.amount)

                    Text(
                        text = "$prefix$${"%,.2f".format(absAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isCredit) BankingTheme.colors.success else BankingTheme.colors.textPrimary,
                        fontSize = 13.5.sp
                    )

                    Text(
                        text = transaction.paymentMethod.replace("Lead ", ""),
                        style = MaterialTheme.typography.labelSmall,
                        color = BankingTheme.colors.textSecondary,
                        fontSize = 10.sp
                    )
                }

                if (onReceiptClick != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    ClayIconButton(
                        icon = Icons.Default.ReceiptLong,
                        contentDescription = "View Receipt",
                        onClick = onReceiptClick,
                        size = 30.dp,
                        iconSize = 14.dp
                    )
                }
            }
        }
    }
}

private fun getCategoryIcon(cat: TransactionCategory): ImageVector {
    return when (cat) {
        TransactionCategory.SALARY -> Icons.Default.TrendingUp
        TransactionCategory.INVESTMENT -> Icons.Default.Business
        TransactionCategory.TRANSFER -> Icons.Default.SwapHoriz
        TransactionCategory.CRYPTO_TRADE -> Icons.Default.CurrencyBitcoin
        TransactionCategory.DINING -> Icons.Default.Restaurant
        TransactionCategory.SHOPPING -> Icons.Default.ShoppingCart
        TransactionCategory.TECH -> Icons.Default.Memory
        TransactionCategory.TRAVEL -> Icons.Default.Flight
        TransactionCategory.UTILITIES -> Icons.Default.CreditCard
        TransactionCategory.ENTERTAINMENT -> Icons.Default.Security
    }
}
