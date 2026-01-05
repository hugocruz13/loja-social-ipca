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
import pt.ipca.lojasocial.domain.models.RelatorioAnualData
import java.io.File
import java.io.FileOutputStream

class PdfAnualService(private val context: Context) {

    fun gerarRelatorioAnual(dados: RelatorioAnualData) {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        // Página A4
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        var y = 50f

        // 1. Título e Ano
        desenharCabecalho(canvas, paint, dados.anoLetivo)
        y += 100f

        // 2. Caixas de Estatísticas (Resumo)
        desenharCaixasResumo(canvas, paint, dados, y)
        y += 120f

        // 3. Gráfico de Top Produtos
        desenharGraficoTopProdutos(canvas, paint, dados.topProdutos, y)
        y += 200f // Espaço ocupado pelo gráfico

        // 4. Lista de Campanhas
        desenharListaCampanhas(canvas, paint, dados.listaCampanhas, y)

        pdfDocument.finishPage(page)
        salvarEAbrirArquivo(pdfDocument, dados.anoLetivo)
    }

    private fun desenharCabecalho(canvas: Canvas, paint: Paint, ano: String) {
        paint.color = Color.parseColor("#00713C") // Verde
        paint.textSize = 26f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Relatório Anual de Atividades", 40f, 50f, paint)

        paint.color = Color.DKGRAY
        paint.textSize = 18f
        canvas.drawText("Ano Letivo: $ano", 40f, 80f, paint)

        paint.strokeWidth = 2f
        canvas.drawLine(40f, 95f, 550f, 95f, paint)
    }

    private fun desenharCaixasResumo(
        canvas: Canvas,
        paint: Paint,
        dados: RelatorioAnualData,
        startY: Float
    ) {
        val boxWidth = 160f
        val boxHeight = 80f
        val gap = 20f

        // Função auxiliar para desenhar uma caixa
        fun drawBox(x: Float, titulo: String, valor: String, cor: Int) {
            paint.color = cor
            paint.style = Paint.Style.FILL
            // Retângulo arredondado (simulado)
            canvas.drawRect(x, startY, x + boxWidth, startY + boxHeight, paint)

            paint.color = Color.WHITE
            paint.textSize = 28f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER

            // Valor no meio
            canvas.drawText(valor, x + (boxWidth / 2), startY + 45f, paint)

            paint.textSize = 12f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText(titulo, x + (boxWidth / 2), startY + 65f, paint)
            paint.textAlign = Paint.Align.LEFT // Reset
        }

        drawBox(
            40f,
            "Entregas Feitas",
            dados.totalEntregasRealizadas.toString(),
            Color.parseColor("#1976D2")
        ) // Azul
        drawBox(
            40f + boxWidth + gap,
            "Itens Doados",
            dados.totalItensDoados.toString(),
            Color.parseColor("#388E3C")
        ) // Verde
        drawBox(
            40f + (boxWidth + gap) * 2,
            "Campanhas",
            dados.totalCampanhas.toString(),
            Color.parseColor("#F57C00")
        ) // Laranja
    }

    private fun desenharGraficoTopProdutos(
        canvas: Canvas,
        paint: Paint,
        produtos: List<Pair<String, Int>>,
        startY: Float
    ) {
        paint.color = Color.BLACK
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Top 5 Produtos com Mais Saída", 40f, startY, paint)

        if (produtos.isEmpty()) {
            paint.textSize = 12f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Sem dados de saídas registados.", 40f, startY + 30f, paint)
            return
        }

        var currentY = startY + 30f
        val maxQtd = produtos.maxOfOrNull { it.second } ?: 1
        val maxBarWidth = 350f // Largura máxima da barra em pixels

        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT

        for (prod in produtos) {
            // Nome do produto
            paint.color = Color.BLACK
            canvas.drawText(prod.first.take(20), 40f, currentY + 15f, paint)

            // Barra
            val barWidth = (prod.second.toFloat() / maxQtd.toFloat()) * maxBarWidth
            paint.color = Color.parseColor("#00713C") // Verde Barras
            canvas.drawRect(180f, currentY, 180f + barWidth, currentY + 20f, paint)

            // Quantidade à frente da barra
            paint.color = Color.DKGRAY
            canvas.drawText(prod.second.toString(), 180f + barWidth + 10f, currentY + 15f, paint)

            currentY += 35f
        }
    }

    private fun desenharListaCampanhas(
        canvas: Canvas,
        paint: Paint,
        campanhas: List<String>,
        startY: Float
    ) {
        paint.color = Color.BLACK
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Campanhas Realizadas", 40f, startY, paint)

        var currentY = startY + 30f
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT

        if (campanhas.isEmpty()) {
            canvas.drawText("Nenhuma campanha registada neste período.", 40f, currentY, paint)
        }

        for (campanha in campanhas) {
            canvas.drawText("• $campanha", 40f, currentY, paint)
            currentY += 20f
        }
    }

    private fun salvarEAbrirArquivo(document: PdfDocument, ano: String) {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        // Sanitizar nome do ficheiro (remover barras do ano)
        val nomeFicheiro = "Relatorio_Anual_${ano.replace("/", "-")}.pdf"
        val file = File(directory, nomeFicheiro)

        try {
            document.writeTo(FileOutputStream(file))
            document.close()
            abrirPdf(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
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