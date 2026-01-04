package pt.ipca.lojasocial.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import pt.ipca.lojasocial.domain.models.ItemRelatorioLog
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfLogsService(private val context: Context) {

    // --- CONSTANTES DE LAYOUT (Ajusta aqui se quiseres mudar o espaçamento) ---
    private val MARGEM_ESQUERDA = 40f
    private val POS_X_DATA = MARGEM_ESQUERDA       // Coluna 1 começa no início da margem
    private val POS_X_ACAO = 180f                  // Coluna 2 (Dá bom espaço para a data)
    private val POS_X_UTILIZADOR = 360f            // Coluna 3 (Dá bom espaço para a ação)
    private val MARGEM_DIREITA_LINHA = 550f        // Onde a linha separadora termina
    // -------------------------------------------------------------------------

    fun gerarRelatorioLogs(listaLogs: List<ItemRelatorioLog>) {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var y = 50f

        desenharCabecalho(canvas, paint)
        y += 80f

        desenharTitulosTabela(canvas, paint, y)
        y += 35f // Um pouco mais de espaço após o título

        paint.textSize = 11f // Tamanho de fonte confortável para leitura
        paint.isAntiAlias = true

        for (log in listaLogs) {
            if (y > 780f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 50f
                desenharCabecalho(canvas, paint)
                y += 80f
                desenharTitulosTabela(canvas, paint, y)
                y += 35f
                paint.textSize = 11f
            }

            // 1. Data/Hora
            paint.color = Color.DKGRAY
            canvas.drawText(log.dataFormatada, POS_X_DATA, y, paint)

            // 2. Ação (Negrito e Preto)
            paint.color = Color.BLACK
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            // Trunca se for muito grande para não invadir a próxima coluna
            val acaoAjustada = if (log.acao.length > 22) log.acao.take(20) + "..." else log.acao
            canvas.drawText(acaoAjustada, POS_X_ACAO, y, paint)
            paint.typeface = Typeface.DEFAULT // Reset

            // 3. Utilizador (Cinzento Escuro)
            paint.color = Color.DKGRAY
            // Trunca o email se for muito longo
            val utilizadorAjustado =
                if (log.utilizador.length > 28) log.utilizador.take(26) + "..." else log.utilizador
            canvas.drawText(utilizadorAjustado, POS_X_UTILIZADOR, y, paint)

            // Linha separadora subtil
            paint.color = Color.LTGRAY
            paint.strokeWidth = 1f
            canvas.drawLine(MARGEM_ESQUERDA, y + 12f, MARGEM_DIREITA_LINHA, y + 12f, paint)

            y += 28f // Altura da linha (espaço entre registos)
        }

        pdfDocument.finishPage(page)
        salvarEAbrirArquivo(pdfDocument)
    }

    private fun desenharCabecalho(canvas: Canvas, paint: Paint) {
        // Título Principal Verde
        paint.color = Color.parseColor("#00713C")
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Relatório de Atividades do Sistema", MARGEM_ESQUERDA, 50f, paint)

        // Data de Geração
        paint.color = Color.GRAY
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f
        val dataHoje =
            SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Gerado em: $dataHoje", MARGEM_ESQUERDA, 75f, paint)
    }

    private fun desenharTitulosTabela(canvas: Canvas, paint: Paint, y: Float) {
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        // Usamos as mesmas coordenadas X para os títulos
        canvas.drawText("Data/Hora", POS_X_DATA, y, paint)
        canvas.drawText("Ação", POS_X_ACAO, y, paint)
        canvas.drawText("Utilizador", POS_X_UTILIZADOR, y, paint)

        // Linha grossa por baixo dos títulos
        paint.strokeWidth = 2f
        paint.color = Color.BLACK
        canvas.drawLine(MARGEM_ESQUERDA, y + 10f, MARGEM_DIREITA_LINHA, y + 10f, paint)
        paint.typeface = Typeface.DEFAULT // Reset
    }

    private fun salvarEAbrirArquivo(document: PdfDocument) {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(directory, "Relatorio_Atividades_${System.currentTimeMillis()}.pdf")

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