package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.model.CurriculumUnit
import com.example.model.Textbook
import java.io.File
import java.io.FileOutputStream

private const val TAG = "PdfTextbookGenerator"
private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MARGIN_H = 42f
private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN_H * 2)

object PdfTextbookGenerator {

    private fun resolveArabicTypeface(context: Context, bold: Boolean): Typeface {
        return try {
            val resId = if (bold) R.font.cairo else R.font.tajawal
            ResourcesCompat.getFont(context, resId) ?: Typeface.create(
                Typeface.SANS_SERIF,
                if (bold) Typeface.BOLD else Typeface.NORMAL
            )
        } catch (_: Exception) {
            Typeface.create(Typeface.SANS_SERIF, if (bold) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    fun getPdfCacheDir(context: Context): File {
        val dir = File(context.filesDir, "textbooks")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun generateOrGetPdfFile(
        context: Context,
        textbook: Textbook,
        relatedUnits: List<CurriculumUnit>
    ): File {
        val safeFileName = textbook.pdfFileName
            .ifBlank { "${textbook.id}.pdf" }
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
        val outFile = File(getPdfCacheDir(context), safeFileName)
        if (outFile.exists() && outFile.length() > 512L) {
            return outFile
        }

        if (tryRenderWithAndroidPdfDocument(context, outFile, textbook, relatedUnits)) {
            return outFile
        }

        writeMinimalPdfFallback(outFile, textbook, relatedUnits)
        return outFile
    }

    private fun tryRenderWithAndroidPdfDocument(
        context: Context,
        outFile: File,
        textbook: Textbook,
        relatedUnits: List<CurriculumUnit>
    ): Boolean {
        var document: PdfDocument? = null
        return try {
            val doc = PdfDocument()
            document = doc

            val regularTypeface = resolveArabicTypeface(context, bold = false)
            val boldTypeface = resolveArabicTypeface(context, bold = true)

            val navyColor = Color.rgb(11, 19, 43)
            val royalNavyColor = Color.rgb(22, 33, 62)
            val goldColor = Color.rgb(212, 175, 55)
            val softGoldBg = Color.rgb(250, 244, 228)
            val bodyDark = Color.rgb(30, 41, 59)
            val mutedGray = Color.rgb(100, 116, 139)

            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = boldTypeface
                textSize = 20f
                color = goldColor
            }
            val headingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = boldTypeface
                textSize = 15f
                color = royalNavyColor
            }
            val subHeadingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = boldTypeface
                textSize = 13f
                color = Color.rgb(154, 114, 20)
            }
            val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = regularTypeface
                textSize = 11.5f
                color = bodyDark
            }
            val coverBodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = regularTypeface
                textSize = 12f
                color = Color.rgb(226, 232, 240)
            }
            val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = regularTypeface
                textSize = 9.5f
                color = mutedGray
            }
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }

            var pageNumber = 1
            var currentPage = doc.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            )
            var canvas = currentPage.canvas

            fun drawFooter(pageNum: Int) {
                strokePaint.color = goldColor
                canvas.drawLine(MARGIN_H, PAGE_HEIGHT - 44f, PAGE_WIDTH - MARGIN_H, PAGE_HEIGHT - 44f, strokePaint)
                val footerText = "منصة SudX للشهادة الثانوية السودانية  |  ${textbook.title}  |  صفحة $pageNum"
                val layout = buildStaticLayout(footerText, footerPaint, CONTENT_WIDTH.toInt())
                canvas.save()
                canvas.translate(MARGIN_H, PAGE_HEIGHT - 36f)
                layout.draw(canvas)
                canvas.restore()
            }

            fun startNewPage(): Float {
                drawFooter(pageNumber)
                doc.finishPage(currentPage)
                pageNumber += 1
                currentPage = doc.startPage(
                    PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                )
                canvas = currentPage.canvas
                fillPaint.color = royalNavyColor
                canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 36f, fillPaint)
                fillPaint.color = goldColor
                canvas.drawRect(0f, 36f, PAGE_WIDTH.toFloat(), 38f, fillPaint)
                return 56f
            }

            fun drawBlock(text: String, paint: TextPaint, cursorY: Float, extraSpace: Float = 8f): Float {
                if (text.isBlank()) return cursorY
                val layout = buildStaticLayout(text, paint, CONTENT_WIDTH.toInt())
                var y = cursorY
                if (y + layout.height > PAGE_HEIGHT - 58f) {
                    y = startNewPage()
                }
                canvas.save()
                canvas.translate(MARGIN_H, y)
                layout.draw(canvas)
                canvas.restore()
                return y + layout.height + extraSpace
            }

            // --- Page 1: Cover & Table of Contents ---
            fillPaint.color = navyColor
            canvas.drawRoundRect(
                RectF(MARGIN_H - 6f, 36f, PAGE_WIDTH - MARGIN_H + 6f, 246f),
                16f,
                16f,
                fillPaint
            )
            strokePaint.color = goldColor
            canvas.drawRoundRect(
                RectF(MARGIN_H - 6f, 36f, PAGE_WIDTH - MARGIN_H + 6f, 246f),
                16f,
                16f,
                strokePaint
            )

            var y = 56f
            y = drawBlock("SudX • مكتبة الشهادة الثانوية السودانية (PDF)", subHeadingPaint.apply { color = goldColor }, y, 6f)
            subHeadingPaint.color = Color.rgb(154, 114, 20)
            y = drawBlock(textbook.title, titlePaint, y, 8f)
            y = drawBlock(
                "المادة: ${textbook.subjectTitle}  •  المسار: ${textbook.track.titleAr}  •  ${textbook.grade}",
                coverBodyPaint,
                y,
                6f
            )
            y = drawBlock(
                "عدد الفصول: ${textbook.chaptersCount}  •  المرجع الوزاري (${textbook.pagesCount} صفحة)",
                coverBodyPaint,
                y,
                10f
            )
            y = drawBlock(textbook.description, coverBodyPaint, y, 22f)

            y = maxOf(y, 268f)

            // Chapters Box
            fillPaint.color = softGoldBg
            val chaptersHeaderEnd = y + 26f
            canvas.drawRoundRect(
                RectF(MARGIN_H - 4f, y - 6f, PAGE_WIDTH - MARGIN_H + 4f, chaptersHeaderEnd),
                8f,
                8f,
                fillPaint
            )
            y = drawBlock("فهرس أبواب ووحدات الكتاب:", headingPaint, y, 10f)
            textbook.chapters.forEachIndexed { index, chapter ->
                y = drawBlock("${index + 1}. $chapter", bodyPaint, y, 5f)
            }

            if (textbook.keySummaries.isNotEmpty()) {
                y += 10f
                y = drawBlock("أهم المحاور والمفاهيم المطلوبة للامتحان:", headingPaint, y, 8f)
                textbook.keySummaries.forEach { summary ->
                    y = drawBlock("• $summary", bodyPaint, y, 5f)
                }
            }

            // Detailed Curriculum Units & Sample Questions
            relatedUnits.forEach { unit ->
                y += 12f
                y = drawBlock("الوحدة الدراسية: ${unit.title}", headingPaint, y, 4f)
                y = drawBlock(unit.subtitle, subHeadingPaint, y, 8f)

                if (unit.keyPoints.isNotEmpty()) {
                    y = drawBlock("النقاط المحورية:", subHeadingPaint, y, 4f)
                    unit.keyPoints.forEachIndexed { idx, pt ->
                        y = drawBlock("${idx + 1}) $pt", bodyPaint, y, 4f)
                    }
                }

                if (unit.fullContent.isNotBlank()) {
                    y += 4f
                    y = drawBlock("الشرح المنهجي:", subHeadingPaint, y, 4f)
                    unit.fullContent.split("\n\n").forEach { paragraph ->
                        y = drawBlock(paragraph.trim(), bodyPaint, y, 6f)
                    }
                }

                if (unit.examFocus.isNotBlank()) {
                    y += 4f
                    y = drawBlock("توجيهات واضع الامتحان: ${unit.examFocus}", subHeadingPaint, y, 8f)
                }

                if (unit.sampleQuestions.isNotEmpty()) {
                    y += 4f
                    y = drawBlock("أسئلة نموذجية مع الإجابة:", headingPaint, y, 6f)
                    unit.sampleQuestions.forEachIndexed { qIdx, q ->
                        y = drawBlock("س${qIdx + 1}: ${q.question}", subHeadingPaint, y, 4f)
                        q.options.forEachIndexed { optIdx, opt ->
                            val marker = if (optIdx == q.correctIndex) "✓" else "•"
                            y = drawBlock("   $marker $opt", bodyPaint, y, 2f)
                        }
                        y = drawBlock("   التوضيح: ${q.explanation}", bodyPaint, y, 8f)
                    }
                }
            }

            drawFooter(pageNumber)
            doc.finishPage(currentPage)

            FileOutputStream(outFile).use { fos ->
                doc.writeTo(fos)
            }
            true
        } catch (t: Throwable) {
            Log.w(TAG, "PdfDocument rendering unavailable in runtime; falling back to PDF-1.4 writer", t)
            false
        } finally {
            try {
                document?.close()
            } catch (_: Throwable) {
            }
        }
    }

    private fun writeMinimalPdfFallback(
        outFile: File,
        textbook: Textbook,
        relatedUnits: List<CurriculumUnit>
    ) {
        val streamBody = buildString {
            append("BT\n")
            append("/F1 16 Tf\n")
            append("50 780 Td\n")
            append("(SudX Sudan Secondary Certificate PDF Library) Tj\n")
            append("0 -24 Td\n")
            append("/F1 12 Tf\n")
            append("(Textbook ID: ${escapePdfAscii(textbook.id)} | Track: ${escapePdfAscii(textbook.track.name)}) Tj\n")
            append("0 -18 Td\n")
            append("(File: ${escapePdfAscii(textbook.pdfFileName)} | Chapters: ${textbook.chaptersCount} | Pages: ${textbook.pagesCount}) Tj\n")
            append("0 -18 Td\n")
            append("(Units included: ${relatedUnits.size} | Key summaries: ${textbook.keySummaries.size}) Tj\n")
            append("ET\n")
        }
        val streamBytes = streamBody.toByteArray(Charsets.US_ASCII)

        val objects = listOf(
            "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n",
            "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n",
            "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n",
            "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n",
            "5 0 obj\n<< /Length ${streamBytes.size} >>\nstream\n$streamBody\nendstream\nendobj\n"
        )

        val pdfBytes = java.io.ByteArrayOutputStream()
        pdfBytes.write("%PDF-1.4\n".toByteArray(Charsets.US_ASCII))
        val offsets = mutableListOf<Int>()
        for (obj in objects) {
            offsets.add(pdfBytes.size())
            pdfBytes.write(obj.toByteArray(Charsets.US_ASCII))
        }
        val xrefStart = pdfBytes.size()
        val xref = buildString {
            append("xref\n")
            append("0 ${objects.size + 1}\n")
            append("0000000000 65535 f \n")
            for (offset in offsets) {
                append(String.format(java.util.Locale.US, "%010d 00000 n \n", offset))
            }
            append("trailer\n")
            append("<< /Size ${objects.size + 1} /Root 1 0 R >>\n")
            append("startxref\n")
            append("$xrefStart\n")
            append("%%EOF\n")
        }
        pdfBytes.write(xref.toByteArray(Charsets.US_ASCII))

        FileOutputStream(outFile).use { fos ->
            fos.write(pdfBytes.toByteArray())
        }
    }

    private fun escapePdfAscii(input: String): String {
        return input
            .filter { it.code in 32..126 }
            .replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)")
    }

    private fun buildStaticLayout(text: String, paint: TextPaint, widthPx: Int): StaticLayout {
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, widthPx.coerceAtLeast(1))
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(2f, 1.15f)
            .setIncludePad(false)
            .build()
    }

    fun copyPdfToUri(context: Context, pdfFile: File, targetUri: Uri): Boolean {
        return try {
            context.contentResolver.openOutputStream(targetUri)?.use { out ->
                pdfFile.inputStream().use { input ->
                    input.copyTo(out)
                }
            } != null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export PDF to URI", e)
            false
        }
    }

    fun getShareableUri(context: Context, pdfFile: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
    }

    fun sharePdf(context: Context, pdfFile: File, bookTitle: String): Boolean {
        return try {
            val uri = getShareableUri(context, pdfFile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, bookTitle)
                putExtra(Intent.EXTRA_TEXT, "كتاب $bookTitle بصيغة PDF من منصة SudX للشهادة الثانوية السودانية")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "مشاركة كتاب PDF"))
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share PDF", e)
            false
        }
    }

    fun openPdfExternally(context: Context, pdfFile: File): Boolean {
        return try {
            val uri = getShareableUri(context, pdfFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "فتح ملف PDF"))
            true
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "No external PDF viewer found", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open external PDF viewer", e)
            false
        }
    }
}
