package pt.ipca.lojasocial.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.RelatorioAnualData
import pt.ipca.lojasocial.domain.models.SchoolYear
import pt.ipca.lojasocial.presentation.components.AdicionarButton
import pt.ipca.lojasocial.presentation.components.AnoLetivoListItem
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.AnosLetivosViewModel
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import pt.ipca.lojasocial.utils.PdfAnualService

// -------------------------------------------------------------------------
// 1. ECRÃ STATEFUL (Lógica e Dados)
// -------------------------------------------------------------------------
@Composable
fun AnoLetivoListScreen(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onYearClick: (SchoolYear) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    // Injeção dos ViewModels necessários para o relatório
    anosLetivosViewModel: AnosLetivosViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel(),
    // campaignViewModel: CampaignViewModel = hiltViewModel(), // Descomenta quando tiveres
    // deliveryViewModel: DeliveryViewModel = hiltViewModel()  // Descomenta quando tiveres
) {
    val anosLetivos by anosLetivosViewModel.anosLetivos.collectAsState()
    val isLoading by anosLetivosViewModel.isLoading.collectAsState()

    // Recolha de dados para o relatório (Assumindo que os ViewModels expõem estas listas)
    val products by productViewModel.filteredProducts.collectAsState() // Usa a lista completa se possível
    // val campaigns by campaignViewModel.campaigns.collectAsState()
    // val deliveries by deliveryViewModel.deliveries.collectAsState()

    // Contexto e Serviço de PDF
    val context = LocalContext.current
    val pdfService = remember { PdfAnualService(context) }

    // Callback para processar o download
    val onDownloadReport: (SchoolYear) -> Unit = { anoSelecionado ->
        Toast.makeText(
            context,
            "A processar dados de ${anoSelecionado.label}...",
            Toast.LENGTH_SHORT
        ).show()

        // --- LÓGICA DE FILTRAGEM (Simulação) ---
        // Nota: Substitui as listas vazias abaixo 'emptyList()' pelas variáveis reais 'campaigns' e 'deliveries'

        val entregasNoAno =
            emptyList<pt.ipca.lojasocial.data.remote.dto.DeliveryDto>() // deliveries.filter { it.dataEntrega >= anoSelecionado.dataInicio && it.dataEntrega <= anoSelecionado.dataFim }
        val campanhasNoAno =
            emptyList<pt.ipca.lojasocial.data.remote.dto.CampaignDto>() // campaigns.filter { it.dataInicio >= anoSelecionado.dataInicio }

        // Calcular Totais
        var totalItens = 0
        val mapaContagemProdutos = mutableMapOf<String, Int>()

        entregasNoAno.forEach { entrega ->
            entrega.produtosEntregues.forEach { (prodId, qtd) ->
                totalItens += qtd
                mapaContagemProdutos[prodId] = (mapaContagemProdutos[prodId] ?: 0) + qtd
            }
        }

        // Top Produtos (Converter ID -> Nome)
        val topProdutos = mapaContagemProdutos.entries
            .map { entry ->
                val nome = products.find { it.id == entry.key }?.name ?: "Produto Excluído"
                Pair(nome, entry.value)
            }
            .sortedByDescending { it.second }
            .take(5)

        // Criar Objeto de Dados
        val dadosRelatorio = RelatorioAnualData(
            anoLetivo = anoSelecionado.label,
            totalCampanhas = campanhasNoAno.size,
            totalEntregasRealizadas = entregasNoAno.size,
            totalItensDoados = totalItens,
            listaCampanhas = campanhasNoAno.map { it.nome },
            topProdutos = topProdutos
        )

        // Gerar PDF
        pdfService.gerarRelatorioAnual(dadosRelatorio)
    }

    // Chama o Ecrã Visual
    AnoLetivoListContent(
        anosLetivos = anosLetivos,
        isLoading = isLoading,
        onBackClick = onBackClick,
        onAddClick = onAddClick,
        onYearClick = onYearClick,
        onDownloadClick = onDownloadReport,
        navItems = navItems,
        onNavigate = onNavigate
    )
}

// -------------------------------------------------------------------------
// 2. ECRÃ STATELESS (Visual Puro - Permite Preview)
// -------------------------------------------------------------------------
@Composable
fun AnoLetivoListContent(
    anosLetivos: List<SchoolYear>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onYearClick: (SchoolYear) -> Unit,
    onDownloadClick: (SchoolYear) -> Unit, // Novo parâmetro
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Anos Letivos",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "settings",
                onItemSelected = { item -> onNavigate(item.route) }
            )
        },
        floatingActionButton = {
            AdicionarButton(onClick = onAddClick)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && anosLetivos.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0XFF00713C)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Histórico de Anos Letivos",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(anosLetivos) { ano ->
                        AnoLetivoListItem(
                            yearLabel = ano.label,
                            isCurrentYear = ano.isCurrent(), // Assumindo que tens este método no modelo
                            onClick = { onYearClick(ano) },
                            onDownloadClick = { onDownloadClick(ano) } // Passa o ano clicado
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 3. PREVIEW (Agora funciona porque usa o Content Stateless)
// -------------------------------------------------------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AnoLetivoListScreenPreview() {

    val dummyYears = listOf(
        SchoolYear(id = "1", label = "2024-2025", startDate = 0L, endDate = 0L),
        SchoolYear(id = "2", label = "2023-2024", startDate = 0L, endDate = 0L)
    )

    MaterialTheme {
        AnoLetivoListContent(
            anosLetivos = dummyYears,
            isLoading = false,
            onBackClick = { },
            onAddClick = { },
            onYearClick = { },
            onDownloadClick = { }, // Ação vazia no preview
            navItems = emptyList(),
            onNavigate = { }
        )
    }
}