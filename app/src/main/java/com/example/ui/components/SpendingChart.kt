package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BankingTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CrimsonExpense
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SpendingAnalyticsCard(
    totalSpentThisMonth: Double,
    totalIncomeThisMonth: Double,
    modifier: Modifier = Modifier
) {
    // 7-day spending data points
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dataPoints = listOf(420f, 850f, 310f, 1240f, 680f, 1890f, 540f)
    var selectedIndex by remember { mutableIntStateOf(5) } // Default to highest spend day (Sat)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
            .padding(20.dp)
            .testTag("spending_analytics_card")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CASH FLOW & LIQUIDITY",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = "Weekly Spending Curve",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            // Selected Day Callout
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardElevated)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "${days[selectedIndex]}: $${"%,.0f".format(dataPoints[selectedIndex])}",
                    style = MaterialTheme.typography.labelMedium,
                    color = ChampagneGold,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Curved Canvas Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val maxVal = dataPoints.maxOrNull() ?: 2000f
                val minVal = 0f
                val range = (maxVal - minVal).coerceAtLeast(1f)

                // Grid background lines
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    drawLine(
                        color = Color(0x14FFFFFF),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Compute point coordinates
                val points = dataPoints.mapIndexed { idx, value ->
                    val x = (idx.toFloat() / (dataPoints.size - 1)) * width
                    val normalized = (value - minVal) / range
                    val y = height - (normalized * (height - 24f)) - 12f
                    Offset(x, y)
                }

                // Smooth cubic bezier spline
                val strokePath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points[0].x, points[0].y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val cx = (p0.x + p1.x) / 2f
                            cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }
                    }
                }

                // Fill gradient underneath
                val fillPath = Path().apply {
                    addPath(strokePath)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ChampagneGold.copy(alpha = 0.25f),
                            ChampagneGold.copy(alpha = 0.02f),
                            Color.Transparent
                        )
                    )
                )

                // Stroke line
                drawPath(
                    path = strokePath,
                    brush = Brush.horizontalGradient(
                        listOf(EmeraldGreen, ChampagneGold, Color(0xFFF59E0B))
                    ),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw circles for data points
                points.forEachIndexed { idx, pt ->
                    val isSelected = idx == selectedIndex
                    drawCircle(
                        color = if (isSelected) ChampagneGold else Color(0xFF1E293B),
                        radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = if (isSelected) Color.White else ChampagneGold,
                        radius = if (isSelected) 3.5.dp.toPx() else 2.dp.toPx(),
                        center = pt
                    )
                }
            }
        }

        // Days of week selector pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, day ->
                val isSelected = index == selectedIndex
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) ChampagneGold else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { selectedIndex = index }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Inflow vs Outflow Metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Income
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardElevated)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Income Inflow",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Total Inflow",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "+$${"%,.0f".format(totalIncomeThisMonth)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Spending
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardElevated)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CrimsonExpense.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Spend Outflow",
                        tint = CrimsonExpense,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Total Outflow",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "-$${"%,.0f".format(totalSpentThisMonth)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

data class MonthlyWealthDataPoint(
    val month: String,
    val wealthAmount: Double,
    val spendingAmount: Double
)

@Composable
fun MonthlySpendingAndWealthGrowthChart(
    modifier: Modifier = Modifier
) {
    val monthsData = listOf(
        MonthlyWealthDataPoint("May", 2150000.0, 34200.0),
        MonthlyWealthDataPoint("Jun", 2280000.0, 29800.0),
        MonthlyWealthDataPoint("Jul", 2410000.0, 41200.0),
        MonthlyWealthDataPoint("Aug", 2530000.0, 31400.0),
        MonthlyWealthDataPoint("Sep", 2690000.0, 27500.0),
        MonthlyWealthDataPoint("Oct", 2845300.0, 32150.0)
    )
    var selectedMonthIndex by remember { mutableIntStateOf(5) }
    val currentMonthData = monthsData[selectedMonthIndex]

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(22.dp))
            .padding(20.dp)
            .testTag("monthly_wealth_growth_chart")
    ) {
        // Title & Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AGGREGATED ASSET TRAJECTORY",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.2.sp,
                    color = ChampagneGold,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Wealth Growth vs. Monthly Outflow",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldGreen.copy(alpha = 0.12f))
                    .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+32.3% YTD Expansion",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Month Inspector Callout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceCardElevated)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${currentMonthData.month} 2026 Aggregated Wealth",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = "$${"%,.2f".format(currentMonthData.wealthAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = ChampagneGold,
                    fontSize = 18.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Monthly Operating Outflow",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = "-$${"%,.2f".format(currentMonthData.spendingAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CrimsonExpense,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Canvas Dual-Curve Chart
        val dotUnselectedColor = SurfaceCard
        val selectedMonthTextColor = BankingTheme.colors.background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val count = monthsData.size

                val minWealth = 2000000f
                val maxWealth = 3000000f
                val wealthRange = maxWealth - minWealth

                val minSpend = 20000f
                val maxSpend = 50000f
                val spendRange = maxSpend - minSpend

                // Horizontal dashed grid
                for (i in 0..3) {
                    val y = height * (i / 3f)
                    drawLine(
                        color = Color(0x18FFFFFF),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Points for Wealth
                val wealthPoints = monthsData.mapIndexed { idx, item ->
                    val x = (idx.toFloat() / (count - 1)) * width
                    val normY = ((item.wealthAmount.toFloat() - minWealth) / wealthRange).coerceIn(0f, 1f)
                    val y = height - (normY * (height - 30f)) - 15f
                    Offset(x, y)
                }

                // Points for Spending
                val spendPoints = monthsData.mapIndexed { idx, item ->
                    val x = (idx.toFloat() / (count - 1)) * width
                    val normY = ((item.spendingAmount.toFloat() - minSpend) / spendRange).coerceIn(0f, 1f)
                    val y = height - (normY * (height * 0.45f)) - 10f
                    Offset(x, y)
                }

                // Smooth Wealth spline
                val wealthPath = Path().apply {
                    if (wealthPoints.isNotEmpty()) {
                        moveTo(wealthPoints[0].x, wealthPoints[0].y)
                        for (i in 0 until wealthPoints.size - 1) {
                            val p0 = wealthPoints[i]
                            val p1 = wealthPoints[i + 1]
                            val cx = (p0.x + p1.x) / 2f
                            cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }
                    }
                }

                // Fill under Wealth curve
                val fillWealthPath = Path().apply {
                    addPath(wealthPath)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillWealthPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ChampagneGold.copy(alpha = 0.28f),
                            ChampagneGold.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )

                // Draw Wealth stroke
                drawPath(
                    path = wealthPath,
                    brush = Brush.horizontalGradient(listOf(ChampagneGold, EmeraldGreen)),
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Smooth Spend spline
                val spendPath = Path().apply {
                    if (spendPoints.isNotEmpty()) {
                        moveTo(spendPoints[0].x, spendPoints[0].y)
                        for (i in 0 until spendPoints.size - 1) {
                            val p0 = spendPoints[i]
                            val p1 = spendPoints[i + 1]
                            val cx = (p0.x + p1.x) / 2f
                            cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }
                    }
                }

                // Draw Spend stroke
                drawPath(
                    path = spendPath,
                    color = CrimsonExpense.copy(alpha = 0.85f),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Dots for Wealth
                wealthPoints.forEachIndexed { idx, pt ->
                    val isSelected = idx == selectedMonthIndex
                    drawCircle(
                        color = if (isSelected) ChampagneGold else dotUnselectedColor,
                        radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = if (isSelected) Color.White else ChampagneGold,
                        radius = if (isSelected) 3.5.dp.toPx() else 2.dp.toPx(),
                        center = pt
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Month Selection Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            monthsData.forEachIndexed { idx, item ->
                val isSelected = idx == selectedMonthIndex
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ChampagneGold else Color.Transparent)
                        .clickable { selectedMonthIndex = idx }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.month,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) selectedMonthTextColor else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Legend row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ChampagneGold))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Aggregated Wealth Growth", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontSize = 10.sp)

            Spacer(modifier = Modifier.width(16.dp))

            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CrimsonExpense))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Monthly Operating Outflow", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
        }
    }
}
