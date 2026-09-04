package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.ScriptScene
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ScriptExporter {

  /**
   * Exports the script scenes as a formatted plain text file and triggers the system share sheet.
   */
  fun exportAsPlainText(
    context: Context,
    title: String,
    channelTarget: String,
    tone: String,
    duration: String,
    scenes: List<ScriptScene>
  ): Boolean {
    return try {
      val cleanTitle = title.ifBlank { "Untitled_Script" }.replace(Regex("[^a-zA-Z0-9_-]"), "_")
      val dir = File(context.cacheDir, "scripts")
      if (!dir.exists()) dir.mkdirs()
      val file = File(dir, "${cleanTitle}_script.txt")

      val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
      val textContent = buildString {
        appendLine("================================================================")
        appendLine("SAMLYT CINESCRIPT 4K STUDIO - SCRIPT EXPORT")
        appendLine("================================================================")
        appendLine("PROJECT TITLE:   $title")
        appendLine("TARGET CHANNEL:  $channelTarget")
        appendLine("TONE & PACING:   $tone | $duration")
        appendLine("EXPORT DATE:     $dateStr")
        appendLine("TOTAL SCENES:    ${scenes.size} beats")
        appendLine("================================================================\n")

        scenes.forEachIndexed { index, scene ->
          appendLine("----------------------------------------------------------------")
          appendLine("SCENE ${index + 1}: ${scene.actTitle} [${scene.timestamp}]")
          appendLine("----------------------------------------------------------------")
          appendLine("NARRATION (VOICEOVER):")
          appendLine(scene.narration)
          appendLine()
          appendLine("B-ROLL & CAMERA DIRECTION:")
          appendLine(scene.bRollCue)
          appendLine()
          appendLine("RETENTION ANCHOR:")
          appendLine(scene.retentionAnchor)
          appendLine("\n")
        }

        appendLine("================================================================")
        appendLine("END OF PRODUCTION SCRIPT - PRODUCED VIA SAMLYT AI STUDIO")
        appendLine("================================================================")
      }

      FileOutputStream(file).use { out ->
        out.write(textContent.toByteArray(Charsets.UTF_8))
      }

      val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )

      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(Intent.EXTRA_SUBJECT, "$title - Script Export")
        putExtra(Intent.EXTRA_TEXT, "Here is the production script for '$title' ($channelTarget).")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      val chooser = Intent.createChooser(shareIntent, "Share Production Script (.txt)")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
      true
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Export failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
      false
    }
  }

  /**
   * Exports the script scenes as an official PDF document using Android's native PdfDocument.
   */
  fun exportAsPdf(
    context: Context,
    title: String,
    channelTarget: String,
    tone: String,
    duration: String,
    scenes: List<ScriptScene>
  ): Boolean {
    return try {
      val cleanTitle = title.ifBlank { "Untitled_Script" }.replace(Regex("[^a-zA-Z0-9_-]"), "_")
      val dir = File(context.cacheDir, "scripts")
      if (!dir.exists()) dir.mkdirs()
      val file = File(dir, "${cleanTitle}_script.pdf")

      val pdfDocument = PdfDocument()
      val pageWidth = 595
      val pageHeight = 842
      var pageNumber = 1

      var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
      var page = pdfDocument.startPage(pageInfo)
      var canvas = page.canvas

      val titlePaint = Paint().apply {
        color = Color.rgb(249, 115, 22) // Samlyt Orange
        textSize = 18f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }

      val headerPaint = Paint().apply {
        color = Color.rgb(30, 41, 59)
        textSize = 10f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }

      val metaPaint = Paint().apply {
        color = Color.rgb(100, 116, 139)
        textSize = 9f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      }

      val actPaint = Paint().apply {
        color = Color.rgb(15, 23, 42)
        textSize = 12f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }

      val timeBadgePaint = Paint().apply {
        color = Color.rgb(249, 115, 22)
        textSize = 9f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }

      val bodyPaint = Paint().apply {
        color = Color.rgb(51, 65, 85)
        textSize = 10f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      }

      val bRollPaint = Paint().apply {
        color = Color.rgb(22, 101, 52) // Dark Green
        textSize = 9.5f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
      }

      val linePaint = Paint().apply {
        color = Color.rgb(226, 232, 240)
        strokeWidth = 1f
      }

      var y = 40f
      val left = 40f
      val right = pageWidth - 40f
      val maxWidth = (right - left).toInt()

      // Header block
      canvas.drawText("SAMLYT CINESCRIPT 4K STUDIO", left, y, headerPaint)
      y += 24f
      canvas.drawText(title.take(45), left, y, titlePaint)
      y += 18f
      canvas.drawText("Channel: $channelTarget | Pacing: $tone ($duration) | Exported: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}", left, y, metaPaint)
      y += 14f
      canvas.drawLine(left, y, right, y, linePaint)
      y += 20f

      fun checkNewPage(neededHeight: Float) {
        if (y + neededHeight > pageHeight - 50f) {
          pdfDocument.finishPage(page)
          pageNumber++
          pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
          page = pdfDocument.startPage(pageInfo)
          canvas = page.canvas
          y = 40f
          canvas.drawText("SAMLYT CINESCRIPT 4K - Page $pageNumber", left, y, metaPaint)
          y += 10f
          canvas.drawLine(left, y, right, y, linePaint)
          y += 20f
        }
      }

      fun drawWrappedText(text: String, paint: Paint, indent: Float = 0f) {
        val words = text.split(" ")
        var currentLine = ""
        for (word in words) {
          val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
          val width = paint.measureText(testLine)
          if (width > (maxWidth - indent)) {
            checkNewPage(14f)
            canvas.drawText(currentLine, left + indent, y, paint)
            y += 14f
            currentLine = word
          } else {
            currentLine = testLine
          }
        }
        if (currentLine.isNotEmpty()) {
          checkNewPage(14f)
          canvas.drawText(currentLine, left + indent, y, paint)
          y += 14f
        }
      }

      scenes.forEachIndexed { index, scene ->
        checkNewPage(60f)
        canvas.drawLine(left, y, right, y, linePaint)
        y += 16f
        canvas.drawText("SCENE ${index + 1}: ${scene.actTitle}", left, y, actPaint)
        canvas.drawText("[${scene.timestamp}]", right - 80f, y, timeBadgePaint)
        y += 16f

        canvas.drawText("NARRATION:", left, y, headerPaint)
        y += 14f
        drawWrappedText(scene.narration, bodyPaint, indent = 10f)
        y += 6f

        canvas.drawText("B-ROLL / CAMERA VISUAL:", left, y, headerPaint)
        y += 14f
        drawWrappedText(scene.bRollCue, bRollPaint, indent = 10f)
        y += 6f

        if (scene.retentionAnchor.isNotBlank()) {
          canvas.drawText("RETENTION HOOK:", left, y, headerPaint)
          y += 14f
          drawWrappedText(scene.retentionAnchor, metaPaint, indent = 10f)
          y += 10f
        }
      }

      pdfDocument.finishPage(page)

      FileOutputStream(file).use { out ->
        pdfDocument.writeTo(out)
      }
      pdfDocument.close()

      val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )

      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(Intent.EXTRA_SUBJECT, "$title - Production Script PDF")
        putExtra(Intent.EXTRA_TEXT, "Attached is the screenplay and shot list for '$title' ($channelTarget).")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      val chooser = Intent.createChooser(shareIntent, "Share Production Script PDF")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
      true
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "PDF generation failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
      false
    }
  }

  /**
   * Direct system text share.
   */
  fun shareAsQuickText(
    context: Context,
    title: String,
    channelTarget: String,
    scenes: List<ScriptScene>
  ) {
    val fullText = buildString {
      appendLine("CINESCRIPT 4K: $title ($channelTarget)")
      appendLine("----------------------------------------\n")
      scenes.forEachIndexed { i, s ->
        appendLine("BEAT ${i + 1} [${s.timestamp}] - ${s.actTitle}")
        appendLine(s.narration)
        appendLine("B-Roll: ${s.bRollCue}")
        appendLine()
      }
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "$title Script")
      putExtra(Intent.EXTRA_TEXT, fullText)
    }
    val chooser = Intent.createChooser(intent, "Share Script Text")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
  }
}
