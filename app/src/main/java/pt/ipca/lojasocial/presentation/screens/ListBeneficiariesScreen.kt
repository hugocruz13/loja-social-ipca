package pt.ipca.lojasocial.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus
import pt.ipca.lojasocial.presentation.components.AppFilterDropdown
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BeneficiarioListItem
import pt.ipca.lojasocial.presentation.viewmodels.BeneficiariesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBeneficiariesScreen(
    onNavigateBack: () -> Unit,
    onBeneficiaryClick: (String) -> Unit,
    onAddBeneficiaryClick: () -> Unit,
    viewModel: BeneficiariesViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val beneficiariesList by viewModel.filteredBeneficiaries.collectAsState()

    val statusOptions = listOf("Ativo", "Inativo", "Analise")

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Beneficiários",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddBeneficiaryClick,
                containerColor = Color(0xFF00713C),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, "Adicionar")
                Spacer(Modifier.width(8.dp))
                Text("Novo Registo")
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // --- HEADER DE CONTEXTO ---
            Surface(
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            tint = Color(0xFF00713C),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${beneficiariesList.size} registados",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1C1E)
                            )
                        )
                    }

                    AppSearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        placeholder = "Procurar por nome, email ou ID..."
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        AppFilterDropdown(
                            label = "Estado",
                            selectedValue = selectedStatus,
                            options = statusOptions,
                            onOptionSelected = viewModel::onStatusSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // --- LISTAGEM ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = isLoading,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "loading_transition"
                ) { targetLoading ->
                    if (targetLoading) {
                        CircularProgressIndicator(color = Color(0xFF00713C))
                    } else if (beneficiariesList.isEmpty()) {
                        EmptyStateView(isSearching = searchQuery.isNotEmpty())
                    } else {
                        // ORDENAÇÃO APLICADA AQUI:
                        val sortedList = remember(beneficiariesList) {
                            beneficiariesList.sortedBy { it.name }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp)
                        ) {
                            items(sortedList, key = { it.id }) { beneficiary ->
                                BeneficiarioListItem(
                                    fullName = beneficiary.name,
                                    status = beneficiary.status,
                                    onClick = { onBeneficiaryClick(beneficiary.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(isSearching: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Ícone visual dinâmico
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Color(0xFFF1F3F4)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (isSearching) "🔍" else "👤",
                    fontSize = 48.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = if (isSearching) "Sem correspondências" else "Lista vazia",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1A1C1E)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (isSearching)
                "Não encontrámos nenhum beneficiário com esses termos. Tente ajustar os filtros ou a pesquisa."
            else
                "Ainda não existem beneficiários registados no sistema.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListBeneficiariesPreview() {
    MaterialTheme {
        Scaffold(
            topBar = { AppTopBar(title = "Beneficiários", onBackClick = {}) }
        ) { p ->
            Box(
                Modifier
                    .padding(p)
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
            ) {
                Column(Modifier.padding(16.dp)) {
                    BeneficiarioListItem(
                        fullName = "João Silva",
                        status = BeneficiaryStatus.ATIVO,
                        onClick = {}
                    )
                    BeneficiarioListItem(
                        fullName = "Maria Antónia",
                        status = BeneficiaryStatus.ANALISE,
                        onClick = {}
                    )
                    BeneficiarioListItem(
                        fullName = "Ricardo Pereira",
                        status = BeneficiaryStatus.INATIVO,
                        onClick = {}
                    )
                }
            }
        }
    }
}