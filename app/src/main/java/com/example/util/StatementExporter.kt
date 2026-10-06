package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.AccountEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object StatementExporter {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm:ss z", Locale.US)
    private val fullDateTimeFormat = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a", Locale.US)

    /**
     * Generates a multi-page, publication-grade PDF Statement with bank insignia,
     * balance summary, FDIC sweep notice, ledger table, and verification hashes.
     */
    fun generatePdfStatement(
        context: Context,
        account: AccountEntity?,
        transactions: List<TransactionEntity>,
        dateRangeLabel: String,
        clientName: String = "Alexander Sterling",
        accountNumber: String = account?.accountNumber ?: "•••• 8842"
    ): File {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 standard width (72 dpi)
        val pageHeight = 842 // A4 standard height (72 dpi)
        val margin = 36f

        val totalCredits = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        val totalDebits = transactions.filter { it.amount < 0 }.sumOf { abs(it.amount) }
        val netCashflow = totalCredits - totalDebits
        val currentBalance = account?.balance ?: 148500.0

        val statementId = "STMT-LEAD-${System.currentTimeMillis().toString().takeLast(8)}"
        val statementHash = computeHash("$statementId:$clientName:$currentBalance:${transactions.size}")

        // Calculate pages needed
        val rowsPerPage = 18
        val totalRows = transactions.size.coerceAtLeast(1)
        val totalPages = ((totalRows - 1) / rowsPerPage) + 1

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        for (pageIndex in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            var y = margin

            // Background subtle tint
            paint.color = AndroidColor.WHITE
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

            // Header Banner
            // Lead Private Bank Top Strip
            paint.color = AndroidColor.rgb(13, 17, 26) // Obsidian 900
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 64f, paint)

            // Gold accent line under header
            paint.color = AndroidColor.rgb(229, 195, 120) // Champagne Gold
            canvas.drawRect(0f, 64f, pageWidth.toFloat(), 67f, paint)

            // Header Title
            paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            paint.textSize = 18f
            paint.color = AndroidColor.rgb(229, 195, 120)
            canvas.drawText("LEAD PRIVATE BANK", margin, 38f, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 9f
            paint.color = AndroidColor.rgb(148, 163, 184)
            canvas.drawText("OFFICIAL ACCOUNT STATEMENT & SETTLEMENT AUDIT", margin, 52f, paint)

            // Top Right Header Info
            paint.textAlign = Paint.Align.RIGHT
            paint.textSize = 8f
            paint.color = AndroidColor.rgb(248, 250, 252)
            canvas.drawText("FDIC SWEEP NETWORK: UP TO $5,000,000", pageWidth - margin, 32f, paint)
            paint.color = AndroidColor.rgb(16, 185, 129) // Emerald
            canvas.drawText("FedNow 24/7 • SWIFT DIRECT CLEARING", pageWidth - margin, 46f, paint)
            paint.textAlign = Paint.Align.LEFT

            y = 86f

            if (pageIndex == 0) {
                // First Page: Account Metadata & Financial Summary

                // Client & Statement Metadata Box (Left & Right)
                paint.color = AndroidColor.rgb(248, 250, 252)
                val metaRect = RectF(margin, y, pageWidth - margin, y + 84f)
                canvas.drawRoundRect(metaRect, 8f, 8f, paint)

                paint.style = Paint.Style.STROKE
                paint.color = AndroidColor.rgb(226, 232, 240)
                paint.strokeWidth = 1f
                canvas.drawRoundRect(metaRect, 8f, 8f, paint)
                paint.style = Paint.Style.FILL

                // Left Column: Account details
                paint.textSize = 8f
                paint.color = AndroidColor.rgb(100, 116, 139)
                canvas.drawText("ACCOUNT HOLDER", margin + 14f, y + 18f, paint)
                paint.textSize = 11f
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                paint.color = AndroidColor.rgb(15, 23, 42)
                canvas.drawText(clientName, margin + 14f, y + 33f, paint)

                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                paint.textSize = 8.5f
                paint.color = AndroidColor.rgb(71, 85, 105)
                canvas.drawText("Account: ${account?.name ?: "Consolidated Private Ledger"}", margin + 14f, y + 48f, paint)
                canvas.drawText("Account #: $accountNumber   |   Routing: ${account?.routingNumber ?: "121000358"}", margin + 14f, y + 62f, paint)
                canvas.drawText("Base Currency: USD ($)", margin + 14f, y + 74f, paint)

                // Right Column: Statement Details
                val rightColX = pageWidth / 2f + 20f
                paint.textSize = 8f
                paint.color = AndroidColor.rgb(100, 116, 139)
                canvas.drawText("STATEMENT PERIOD", rightColX, y + 18f, paint)
                paint.textSize = 10f
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                paint.color = AndroidColor.rgb(15, 23, 42)
                canvas.drawText(dateRangeLabel, rightColX, y + 33f, paint)

                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                paint.textSize = 8.5f
                paint.color = AndroidColor.rgb(71, 85, 105)
                canvas.drawText("Statement ID: $statementId", rightColX, y + 48f, paint)
                canvas.drawText("Generated: ${dateFormat.format(Date())} ${timeFormat.format(Date())}", rightColX, y + 62f, paint)
                canvas.drawText("Status: OFFICIAL RECONCILED RECORD", rightColX, y + 74f, paint)

                y += 96f

                // Financial Summary Strip (4 KPI boxes)
                val boxWidth = (pageWidth - (margin * 2) - 30f) / 4f
                val summaryItems = listOf(
                    Triple("STARTING BALANCE", "$${"%,.2f".format((currentBalance - netCashflow).coerceAtLeast(0.0))}", AndroidColor.rgb(15, 23, 42)),
                    Triple("TOTAL INFLOW (+)", "+$${"%,.2f".format(totalCredits)}", AndroidColor.rgb(16, 185, 129)),
                    Triple("TOTAL OUTFLOW (-)", "-$${"%,.2f".format(totalDebits)}", AndroidColor.rgb(225, 29, 72)),
                    Triple("CLOSING BALANCE", "$${"%,.2f".format(currentBalance)}", AndroidColor.rgb(180, 131, 40))
                )

                for (i in summaryItems.indices) {
                    val bx = margin + (i * (boxWidth + 10f))
                    val bRect = RectF(bx, y, bx + boxWidth, y + 46f)

                    paint.color = AndroidColor.rgb(248, 250, 252)
                    canvas.drawRoundRect(bRect, 6f, 6f, paint)
                    paint.style = Paint.Style.STROKE
                    paint.color = AndroidColor.rgb(226, 232, 240)
                    canvas.drawRoundRect(bRect, 6f, 6f, paint)
                    paint.style = Paint.Style.FILL

                    val (lbl, valStr, color) = summaryItems[i]
                    paint.textSize = 7f
                    paint.color = AndroidColor.rgb(100, 116, 139)
                    canvas.drawText(lbl, bx + 8f, y + 16f, paint)

                    paint.textSize = 10f
                    paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    paint.color = color
                    canvas.drawText(valStr, bx + 8f, y + 34f, paint)
                    paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                }

                y += 60f
            } else {
                // Continuation page header
                paint.textSize = 9f
                paint.color = AndroidColor.rgb(100, 116, 139)
                canvas.drawText("Lead Statement $statementId  •  $clientName  •  Account $accountNumber", margin, y + 14f, paint)
                y += 24f
            }

            // Table Header Row
            paint.color = AndroidColor.rgb(15, 23, 42) // Dark Navy
            val thRect = RectF(margin, y, pageWidth - margin, y + 20f)
            canvas.drawRoundRect(thRect, 4f, 4f, paint)

            paint.color = AndroidColor.WHITE
            paint.textSize = 7.5f
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)

            val colDate = margin + 8f
            val colRef = margin + 68f
            val colDesc = margin + 140f
            val colCat = margin + 300f
            val colStatus = margin + 410f
            val colAmount = pageWidth - margin - 8f

            canvas.drawText("DATE", colDate, y + 13f, paint)
            canvas.drawText("REFERENCE", colRef, y + 13f, paint)
            canvas.drawText("COUNTERPARTY / DESCRIPTION", colDesc, y + 13f, paint)
            canvas.drawText("CATEGORY", colCat, y + 13f, paint)
            canvas.drawText("STATUS", colStatus, y + 13f, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("AMOUNT (USD)", colAmount, y + 13f, paint)
            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

            y += 24f

            // Table Rows
            val startIdx = pageIndex * rowsPerPage
            val endIdx = (startIdx + rowsPerPage).coerceAtMost(transactions.size)
            val pageTransactions = if (transactions.isEmpty()) emptyList() else transactions.subList(startIdx, endIdx)

            if (pageTransactions.isEmpty()) {
                paint.textSize = 9f
                paint.color = AndroidColor.rgb(100, 116, 139)
                canvas.drawText("No transactions recorded for the selected filter and period.", margin + 12f, y + 20f, paint)
                y += 40f
            } else {
                for (rowIndex in pageTransactions.indices) {
                    val tx = pageTransactions[rowIndex]
                    val isAlternate = rowIndex % 2 == 1
                    val rowHeight = 22f

                    if (isAlternate) {
                        paint.color = AndroidColor.rgb(248, 250, 252)
                        canvas.drawRect(margin, y, pageWidth - margin, y + rowHeight, paint)
                    }

                    // Row bottom hairline
                    paint.color = AndroidColor.rgb(241, 245, 249)
                    canvas.drawLine(margin, y + rowHeight, pageWidth - margin, y + rowHeight, paint)

                    paint.textSize = 7.5f
                    paint.color = AndroidColor.rgb(71, 85, 105)
                    canvas.drawText(dateFormat.format(Date(tx.timestamp)), colDate, y + 14f, paint)

                    paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                    paint.textSize = 7f
                    paint.color = AndroidColor.rgb(100, 116, 139)
                    canvas.drawText(tx.reference.take(12), colRef, y + 14f, paint)
                    paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

                    paint.textSize = 8f
                    paint.color = AndroidColor.rgb(15, 23, 42)
                    paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                    val merchantTruncated = if (tx.merchant.length > 28) tx.merchant.take(26) + "…" else tx.merchant
                    canvas.drawText(merchantTruncated, colDesc, y + 14f, paint)
                    paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

                    paint.textSize = 7f
                    paint.color = AndroidColor.rgb(100, 116, 139)
                    canvas.drawText(tx.category.displayName.take(18), colCat, y + 14f, paint)

                    paint.textSize = 7f
                    paint.color = when (tx.status) {
                        TransactionStatus.COMPLETED -> AndroidColor.rgb(16, 185, 129)
                        TransactionStatus.PENDING -> AndroidColor.rgb(217, 119, 6)
                        TransactionStatus.REFUNDED -> AndroidColor.rgb(6, 182, 212)
                    }
                    canvas.drawText(tx.status.name, colStatus, y + 14f, paint)

                    // Amount with color
                    paint.textAlign = Paint.Align.RIGHT
                    paint.textSize = 8.5f
                    paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    paint.color = if (tx.amount > 0) AndroidColor.rgb(16, 185, 129) else AndroidColor.rgb(15, 23, 42)
                    val prefix = if (tx.amount > 0) "+" else "-"
                    canvas.drawText("$prefix$${"%,.2f".format(abs(tx.amount))}", colAmount, y + 14f, paint)
                    paint.textAlign = Paint.Align.LEFT
                    paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

                    y += rowHeight
                }
            }

            // Footer (Anchor at bottom of every page)
            val footerY = pageHeight - margin - 22f

            // Hairline separator
            paint.color = AndroidColor.rgb(226, 232, 240)
            canvas.drawLine(margin, footerY, pageWidth - margin, footerY, paint)

            paint.textSize = 7f
            paint.color = AndroidColor.rgb(148, 163, 184)
            canvas.drawText(
                "VERIFICATION: $statementHash | LEAD PRIVATE BANK NA (FDIC SWEEP #90214)",
                margin,
                footerY + 12f,
                paint
            )
            canvas.drawText(
                "Confidential document issued exclusively for Alexander Sterling. Digital reproduction is legally binding under UCC Art. 4A.",
                margin,
                footerY + 20f,
                paint
            )

            paint.textAlign = Paint.Align.RIGHT
            paint.textSize = 7.5f
            paint.color = AndroidColor.rgb(100, 116, 139)
            canvas.drawText("Page ${pageIndex + 1} of $totalPages", pageWidth - margin, footerY + 12f, paint)
            paint.textAlign = Paint.Align.LEFT

            pdfDocument.finishPage(page)
        }

        // Save PDF to cache/statements
        val dir = File(context.cacheDir, "statements")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "Lead_Statement_${statementId}.pdf")
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return file
    }

    /**
     * Generates standard RFC 4180 CSV spreadsheet statement.
     */
    fun generateCsvStatement(
        context: Context,
        account: AccountEntity?,
        transactions: List<TransactionEntity>,
        clientName: String = "Alexander Sterling"
    ): File {
        val dir = File(context.cacheDir, "statements")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "Lead_Ledger_Export_${System.currentTimeMillis()}.csv")

        val sb = StringBuilder()
        // Headers
        sb.append("Transaction ID,Date,Time,Reference Code,Account Name,Account Number,Counterparty / Merchant,Category,Type,Status,Amount (USD),Location,Payment Method,Memo\n")

        for (tx in transactions) {
            val dateStr = dateFormat.format(Date(tx.timestamp))
            val timeStr = timeFormat.format(Date(tx.timestamp))
            val typeStr = if (tx.amount > 0) "CREDIT" else "DEBIT"
            val amtStr = String.format(Locale.US, "%.2f", tx.amount)

            sb.append(escapeCsv(tx.id)).append(",")
            sb.append(escapeCsv(dateStr)).append(",")
            sb.append(escapeCsv(timeStr)).append(",")
            sb.append(escapeCsv(tx.reference)).append(",")
            sb.append(escapeCsv(account?.name ?: "Lead Private Checking")).append(",")
            sb.append(escapeCsv(account?.accountNumber ?: "•••• 8842")).append(",")
            sb.append(escapeCsv(tx.merchant)).append(",")
            sb.append(escapeCsv(tx.category.displayName)).append(",")
            sb.append(escapeCsv(typeStr)).append(",")
            sb.append(escapeCsv(tx.status.name)).append(",")
            sb.append(amtStr).append(",")
            sb.append(escapeCsv(tx.location)).append(",")
            sb.append(escapeCsv(tx.paymentMethod)).append(",")
            sb.append(escapeCsv(tx.note)).append("\n")
        }

        file.writeText(sb.toString())
        return file
    }

    /**
     * Generates official Electronic Transaction Clearance Voucher / Payment Receipt PDF.
     */
    fun generateReceiptPdf(
        context: Context,
        transaction: TransactionEntity,
        account: AccountEntity?,
        clientName: String = "Alexander Sterling"
    ): File {
        val pdfDocument = PdfDocument()
        val pageWidth = 400
        val pageHeight = 620
        val margin = 28f

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // White card background
        paint.color = AndroidColor.WHITE
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

        // Top decorative header
        paint.color = AndroidColor.rgb(13, 17, 26) // Obsidian
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 56f, paint)

        paint.color = AndroidColor.rgb(229, 195, 120) // Gold line
        canvas.drawRect(0f, 56f, pageWidth.toFloat(), 58f, paint)

        // Header text
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 14f
        paint.color = AndroidColor.rgb(229, 195, 120)
        canvas.drawText("LEAD PRIVATE BANK", margin, 32f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 7.5f
        paint.color = AndroidColor.rgb(148, 163, 184)
        canvas.drawText("FEDNOW / WIRE SETTLEMENT RECEIPT", margin, 46f, paint)

        var y = 80f

        // Hero Amount Display Box
        val heroRect = RectF(margin, y, pageWidth - margin, y + 76f)
        paint.color = AndroidColor.rgb(248, 250, 252)
        canvas.drawRoundRect(heroRect, 8f, 8f, paint)
        paint.style = Paint.Style.STROKE
        paint.color = AndroidColor.rgb(226, 232, 240)
        canvas.drawRoundRect(heroRect, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 8.5f
        paint.color = AndroidColor.rgb(100, 116, 139)
        canvas.drawText("SETTLEMENT AMOUNT", pageWidth / 2f, y + 22f, paint)

        val isCredit = transaction.amount > 0
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        paint.textSize = 20f
        paint.color = if (isCredit) AndroidColor.rgb(16, 185, 129) else AndroidColor.rgb(15, 23, 42)
        val prefix = if (isCredit) "+" else "-"
        canvas.drawText("$prefix$${"%,.2f".format(abs(transaction.amount))} USD", pageWidth / 2f, y + 48f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 8f
        paint.color = AndroidColor.rgb(16, 185, 129)
        canvas.drawText("● CLEARED & RECONCILED", pageWidth / 2f, y + 66f, paint)
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

        y += 94f

        // Key Value Details Section
        val details = listOf(
            "Beneficiary / Merchant" to transaction.merchant,
            "Reference ID" to transaction.reference,
            "Timestamp" to fullDateTimeFormat.format(Date(transaction.timestamp)),
            "Payment Rail / Instrument" to transaction.paymentMethod,
            "Originating Account" to (account?.name ?: "Lead Private Checking •• 8842"),
            "Client Identity" to clientName,
            "Category" to transaction.category.displayName,
            "Authorized Location" to transaction.location,
            "Dispute Status" to if (transaction.isDisputed) "DISPUTE PENDING REVIEW" else "NO DISPUTES RECORDED"
        )

        for ((lbl, valStr) in details) {
            paint.textSize = 8f
            paint.color = AndroidColor.rgb(100, 116, 139)
            canvas.drawText(lbl, margin, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.textSize = 8f
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.color = AndroidColor.rgb(15, 23, 42)
            val truncatedVal = if (valStr.length > 28) valStr.take(26) + "…" else valStr
            canvas.drawText(truncatedVal, pageWidth - margin, y, paint)
            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

            // Dotted separator
            paint.color = AndroidColor.rgb(241, 245, 249)
            canvas.drawLine(margin, y + 6f, pageWidth - margin, y + 6f, paint)

            y += 24f
        }

        if (transaction.note.isNotBlank()) {
            paint.textSize = 8f
            paint.color = AndroidColor.rgb(100, 116, 139)
            canvas.drawText("Memo / Purpose", margin, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.color = AndroidColor.rgb(15, 23, 42)
            canvas.drawText(transaction.note.take(30), pageWidth - margin, y, paint)
            paint.textAlign = Paint.Align.LEFT
            y += 24f
        }

        y += 10f

        // Cryptographic Seal Box
        val hash = computeHash("${transaction.id}:${transaction.reference}:${transaction.amount}")
        paint.color = AndroidColor.rgb(248, 250, 252)
        val sealRect = RectF(margin, y, pageWidth - margin, y + 44f)
        canvas.drawRoundRect(sealRect, 6f, 6f, paint)
        paint.style = Paint.Style.STROKE
        paint.color = AndroidColor.rgb(226, 232, 240)
        canvas.drawRoundRect(sealRect, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        paint.textSize = 7f
        paint.color = AndroidColor.rgb(100, 116, 139)
        canvas.drawText("DIGITAL TRANSACTION CLEARING SEAL", margin + 10f, y + 16f, paint)

        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        paint.textSize = 6.5f
        paint.color = AndroidColor.rgb(71, 85, 105)
        canvas.drawText("SHA256: ${hash.take(36)}…", margin + 10f, y + 28f, paint)
        canvas.drawText("Fedwire Tracking: ${transaction.reference}-FW-001", margin + 10f, y + 38f, paint)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

        // Footer
        val footerY = pageHeight - margin - 8f
        paint.textSize = 6.5f
        paint.color = AndroidColor.rgb(148, 163, 184)
        canvas.drawText("Lead Private Bank N.A. Member FDIC. Equal Housing Lender. All rights reserved.", margin, footerY, paint)

        pdfDocument.finishPage(page)

        val dir = File(context.cacheDir, "receipts")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "Lead_Receipt_${transaction.reference}.pdf")
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return file
    }

    /**
     * Generates official Cashier's Check / Remittance Check facsimile record PDF.
     */
    fun generateCheckRecordPdf(
        context: Context,
        transaction: TransactionEntity,
        account: AccountEntity?,
        clientName: String = "Alexander Sterling"
    ): File {
        val pdfDocument = PdfDocument()
        val pageWidth = 540
        val pageHeight = 270 // Standard US Check landscape aspect ratio
        val margin = 20f

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Check background parchment color
        paint.color = AndroidColor.rgb(255, 253, 245) // Warm Ivory
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

        // Ornamental security guilloche border
        paint.style = Paint.Style.STROKE
        paint.color = AndroidColor.rgb(203, 213, 225)
        paint.strokeWidth = 2f
        canvas.drawRect(margin, margin, pageWidth - margin, pageHeight - margin, paint)
        paint.strokeWidth = 0.5f
        canvas.drawRect(margin + 4f, margin + 4f, pageWidth - margin - 4f, pageHeight - margin - 4f, paint)
        paint.style = Paint.Style.FILL

        // Top Left: Bank Brand & Payer
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 13f
        paint.color = AndroidColor.rgb(15, 23, 42)
        canvas.drawText("LEAD PRIVATE BANK, N.A.", margin + 14f, margin + 26f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 7.5f
        paint.color = AndroidColor.rgb(71, 85, 105)
        canvas.drawText("PRIVATE CLIENT TREASURY SERVICES", margin + 14f, margin + 38f, paint)
        canvas.drawText("100 PINE STREET, SUITE 3200, SAN FRANCISCO, CA", margin + 14f, margin + 48f, paint)
        canvas.drawText("Drawer: $clientName (Acct: ${account?.accountNumber ?: "•••• 8842"})", margin + 14f, margin + 60f, paint)

        // Top Right: Check Number & Date
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        paint.color = AndroidColor.rgb(15, 23, 42)
        val checkNum = "CHECK NO. ${transaction.reference.filter { it.isDigit() }.takeLast(6).ifEmpty { "084920" }}"
        canvas.drawText(checkNum, pageWidth - margin - 14f, margin + 26f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 8f
        paint.color = AndroidColor.rgb(71, 85, 105)
        canvas.drawText("DATE: ${dateFormat.format(Date(transaction.timestamp))}", pageWidth - margin - 14f, margin + 40f, paint)
        canvas.drawText("TRANS CODE: LEAD-CHK-${transaction.reference.take(6)}", pageWidth - margin - 14f, margin + 52f, paint)
        paint.textAlign = Paint.Align.LEFT

        // "PAY TO THE ORDER OF" Box
        val payY = margin + 94f
        paint.textSize = 8f
        paint.color = AndroidColor.rgb(100, 116, 139)
        canvas.drawText("PAY TO THE", margin + 14f, payY - 8f, paint)
        canvas.drawText("ORDER OF:", margin + 14f, payY + 2f, paint)

        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = AndroidColor.rgb(15, 23, 42)
        canvas.drawText(transaction.merchant, margin + 74f, payY, paint)

        // Amount Box (Right)
        val amtX = pageWidth - margin - 130f
        val amtRect = RectF(amtX, payY - 14f, pageWidth - margin - 14f, payY + 12f)
        paint.style = Paint.Style.STROKE
        paint.color = AndroidColor.rgb(180, 131, 40)
        paint.strokeWidth = 1.2f
        canvas.drawRoundRect(amtRect, 4f, 4f, paint)
        paint.style = Paint.Style.FILL

        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = AndroidColor.rgb(15, 23, 42)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("$${"%,.2f".format(abs(transaction.amount))}", amtRect.centerX(), payY + 4f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Amount in Words
        val wordsY = payY + 28f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        paint.textSize = 9f
        paint.color = AndroidColor.rgb(30, 41, 59)
        val amountInWords = convertAmountToWords(abs(transaction.amount))
        canvas.drawText(amountInWords, margin + 14f, wordsY, paint)
        paint.color = AndroidColor.rgb(203, 213, 225)
        canvas.drawLine(margin + 14f, wordsY + 4f, pageWidth - margin - 14f, wordsY + 4f, paint)

        // Memo & Authorized Signature line
        val signY = wordsY + 40f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 7.5f
        paint.color = AndroidColor.rgb(100, 116, 139)
        canvas.drawText("FOR: ${transaction.note.ifBlank { "Official Remittance Clearance" }}", margin + 14f, signY, paint)

        // Signature
        paint.textAlign = Paint.Align.RIGHT
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
        paint.textSize = 11f
        paint.color = AndroidColor.rgb(15, 23, 42)
        canvas.drawText("Alexander Sterling (Authorized Officer)", pageWidth - margin - 14f, signY - 4f, paint)
        paint.color = AndroidColor.rgb(100, 116, 139)
        canvas.drawLine(pageWidth - margin - 220f, signY, pageWidth - margin - 14f, signY, paint)
        paint.textSize = 6.5f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("AUTHORIZED SIGNATURE • VERIFIED BIOMETRIC TOKEN", pageWidth - margin - 14f, signY + 10f, paint)
        paint.textAlign = Paint.Align.LEFT

        // MICR Encoding Line (Bottom of check)
        val micrY = pageHeight - margin - 14f
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        paint.textSize = 10f
        paint.color = AndroidColor.rgb(15, 23, 42)
        val micrLine = "⑆121000358⑆  008492019488⑈  084920⑈"
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(micrLine, pageWidth / 2f, micrY, paint)
        paint.textAlign = Paint.Align.LEFT

        pdfDocument.finishPage(page)

        val dir = File(context.cacheDir, "receipts")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "Lead_Official_Check_${transaction.reference}.pdf")
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return file
    }

    /**
     * Shares a file via Android Sharesheet with FileProvider Uri.
     */
    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    /**
     * Opens/views a file in a compatible viewer app.
     */
    fun openFile(context: Context, file: File, mimeType: String, title: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            shareFile(context, file, mimeType, title)
        }
    }

    private fun escapeCsv(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            "\"$escaped\""
        } else {
            escaped
        }
    }

    private fun computeHash(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun convertAmountToWords(amount: Double): String {
        val dollars = amount.toLong()
        val cents = Math.round((amount - dollars) * 100)
        return "${numberToWords(dollars).uppercase()} DOLLARS AND ${"%02d".format(cents)}/100"
    }

    private fun numberToWords(num: Long): String {
        if (num == 0L) return "Zero"
        val units = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
        )
        val tens = arrayOf(
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        )

        return when {
            num < 20 -> units[num.toInt()]
            num < 100 -> tens[(num / 10).toInt()] + (if (num % 10 != 0L) " " + units[(num % 10).toInt()] else "")
            num < 1000 -> units[(num / 100).toInt()] + " Hundred" + (if (num % 100 != 0L) " " + numberToWords(num % 100) else "")
            num < 1000000 -> numberToWords(num / 1000) + " Thousand" + (if (num % 1000 != 0L) " " + numberToWords(num % 1000) else "")
            num < 1000000000 -> numberToWords(num / 1000000) + " Million" + (if (num % 1000000 != 0L) " " + numberToWords(num % 1000000) else "")
            else -> numberToWords(num / 1000000000) + " Billion" + (if (num % 1000000000 != 0L) " " + numberToWords(num % 1000000000) else "")
        }
    }
}
