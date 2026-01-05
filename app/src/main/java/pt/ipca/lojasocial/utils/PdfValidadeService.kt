package pt.ipca.lojasocial.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import pt.ipca.lojasocial.domain.models.ItemRelatorioValidade
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfValidadeService(private val context: Context) {

    fun gerarRelatorio(listaItens: List<ItemRelatorioValidade>) {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        // Ordenar: Urgentes primeiro
        val listaOrdenada = listaItens.sortedBy { it.dataValidade }

        // Configurações da Página A4
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var y = 50f // Posição vertical inicial

        // --- 1. CABEÇALHO ---
        desenharCabecalho(canvas, paint)
        y += 80f

        // --- 2. TÍTULOS DA TABELA ---
        desenharTitulosTabela(canvas, paint, y)
        y += 30f

        // --- 3. LISTA DE PRODUTOS ---
        paint.textSize = 12f

        for (item in listaOrdenada) {
            // Verificar se a página acabou (A4 tem ~842 de altura)
            if (y > 780f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 50f
                desenharCabecalho(canvas, paint)
                y += 80f
                desenharTitulosTabela(canvas, paint, y)
                y += 30f
                paint.textSize = 12f
            }

            val (estado, cor) = calcularEstadoCor(item.dataValidade)

            paint.color = Color.BLACK
            // Nome (trunca se for muito grande)
            val nomeAjustado =
                if (item.nomeProduto.length > 30) item.nomeProduto.take(27) + "..." else item.nomeProduto
            canvas.drawText(nomeAjustado, 50f, y, paint)

            // Quantidade
            canvas.drawText(item.quantidade.toString(), 300f, y, paint)

            // Data Validade
            canvas.drawText(formatarData(item.dataValidade), 380f, y, paint)

            // Estado (Colorido)
            paint.color = cor
            paint.isFakeBoldText = true
            canvas.drawText(estado, 480f, y, paint)
            paint.isFakeBoldText = false

            // Linha subtil separadora
            paint.color = Color.LTGRAY
            paint.strokeWidth = 1f
            canvas.drawLine(50f, y + 10f, 550f, y + 10f, paint)

            y += 25f
        }

        pdfDocument.finishPage(page)
        salvarEAbrirArquivo(pdfDocument)
    }

    private fun desenharCabecalho(canvas: Canvas, paint: Paint) {
        paint.color = Color.BLACK
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("Loja Social - Validade de Stock", 50f, 50f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        val dataHoje = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Gerado em: $dataHoje", 50f, 80f, paint)
    }

    private fun desenharTitulosTabela(canvas: Canvas, paint: Paint, y: Float) {
        paint.color = Color.DKGRAY
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("Produto", 50f, y, paint)
        canvas.drawText("Qtd", 300f, y, paint)
        canvas.drawText("Validade", 380f, y, paint)
        canvas.drawText("Status", 480f, y, paint)

        paint.strokeWidth = 2f
        canvas.drawLine(50f, y + 10f, 550f, y + 10f, paint)
    }

    private fun calcularEstadoCor(validade: Long): Pair<String, Int> {
        val hoje = System.currentTimeMillis()
        val diff = validade - hoje
        val dias = diff / (1000 * 60 * 60 * 24)

        return when {
            dias < 30 -> Pair("URGENTE", Color.RED)
            dias < 90 -> Pair("ATENÇÃO", Color.parseColor("#F57C00"))
            else -> Pair("OK", Color.parseColor("#388E3C"))
        }
    }

    private fun formatarData(timestamp: Long): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
    }

    private fun salvarEAbrirArquivo(document: PdfDocument) {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(directory, "Relatorio_Validade.pdf")

        try {
            document.writeTo(FileOutputStream(file))
            document.close()
            abrirPdf(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao criar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun abrirPdf(file: File) {
        val authority = "${context.packageName}.provider"

        val uri = FileProvider.getUriForFile(context, authority, file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY

        val chooser = Intent.createChooser(intent, "Abrir Relatório")
        context.startActivity(chooser)
    }
}