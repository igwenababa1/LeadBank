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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import com.example.ui.components.BankingBackgroundType
import com.example.ui.components.ClayBadge
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCard
import com.example.ui.components.ClayElevation
import com.example.ui.components.ClayIconButton
import com.example.ui.components.ClayVariant
import com.example.ui.components.claymorphic
import com.example.ui.components.bankingBackground
import com.example.ui.theme.BankingTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionCategory
import com.example.ui.components.SpendingAnalyticsCard
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CrimsonExpense
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalyticsScreen(
    savingsGoals: List<SavingsGoalEntity>,
    onOpenCreateGoal: () -> Unit,
    onDepositToGoal: (String, Double) -> Unit,
    onOpenExportStatement: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalSaved = savingsGoals.sumOf { it.currentAmount }
    val totalProjectedAnnualInterest = savingsGoals.sumOf { it.projectedAnnualInterest }

    val categoryExpenses = listOf(
        Pair(TransactionCategory.TRAVEL, 4250.0),
        Pair(TransactionCategory.TECH, 5341.10),
        Pair(TransactionCategory.DINING, 1482.60),
        Pair(TransactionCategory.SHOPPING, 1187.35),
        Pair(TransactionCategory.ENTERTAINMENT, 860.0)
    )
    val totalSpend = categoryExpenses.sumOf { it.second }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .bankingBackground(BankingBackgroundType.VAULTS, BankingTheme.colors.isDark)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
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
                            text = "PORTFOLIO & CAPITAL EXPANSION",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.5.sp,
                            color = BankingTheme.colors.primaryAccent,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Wealth & Analytics",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = BankingTheme.colors.textPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClayButton(
                            onClick = onOpenExportStatement,
                            variant = ClayVariant.SURFACE,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("vault_export_statement_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Yield Report",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        ClayButton(
                            onClick = onOpenCreateGoal,
                            variant = ClayVariant.GOLD,
                            elevation = ClayElevation.MEDIUM,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("open_create_vault_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Obsidian950,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "New Vault",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Obsidian950
                                )
                            }
                        }
                    }
                }
            }
        }

        // APY Wealth Reserve Overview Card
        item {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.MEDIUM,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .claymorphic(variant = ClayVariant.GOLD, elevation = ClayElevation.LOW, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = Obsidian950,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "AGGREGATE RESERVE VAULTS",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.2.sp,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "$${"%,.2f".format(totalSaved)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        ClayBadge(
                            text = "5.15% APY",
                            variant = ClayVariant.EMERALD
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = ClayVariant.OBSIDIAN,
                        elevation = ClayElevation.LOW,
                        shape = RoundedCornerShape(12.dp)
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
                                    text = "Projected Annual Yield",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "+$${"%,.2f".format(totalProjectedAnnualInterest)} / yr",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Compounding Schedule",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Daily at 00:00 UTC",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Weekly Spending Chart
        item {
            SpendingAnalyticsCard(
                totalSpentThisMonth = 12450.0,
                totalIncomeThisMonth = 42620.0
            )
        }

        // Individual Wealth Goals / Vaults List
        item {
            Column {
                Text(
                    text = "TARGET ALLOCATION VAULTS",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    savingsGoals.forEach { goal ->
                        ClayCard(
                            modifier = Modifier.fillMaxWidth(),
                            variant = ClayVariant.SURFACE,
                            elevation = ClayElevation.LOW,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = goal.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Target Date: ${goal.targetDate} • ${goal.apy}% APY",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    // Quick Deposit $5k chip
                                    ClayButton(
                                        onClick = { onDepositToGoal(goal.id, 5000.0) },
                                        variant = ClayVariant.GOLD,
                                        elevation = ClayElevation.LOW,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "+ $5k Deposit",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Obsidian950,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                LinearProgressIndicator(
                                    progress = { goal.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = ChampagneGold,
                                    trackColor = SurfaceCardElevated
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$${"%,.0f".format(goal.currentAmount)} funded",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Goal: $${"%,.0f".format(goal.targetAmount)} (${(goal.progress * 100).toInt()}%)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Expenditure Breakdown
        item {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                variant = ClayVariant.SURFACE,
                elevation = ClayElevation.LOW,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "CATEGORY OUTFLOW BREAKDOWN",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.2.sp,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        categoryExpenses.forEach { (cat, amount) ->
                            val percentage = if (totalSpend > 0) (amount / totalSpend).toFloat() else 0f
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = cat.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "$${"%,.2f".format(amount)} (${(percentage * 100).toInt()}%)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { percentage },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = ChampagneGold,
                                    trackColor = SurfaceCardElevated
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
