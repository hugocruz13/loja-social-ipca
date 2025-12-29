package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.components.RequerimentoListItem
import pt.ipca.lojasocial.presentation.viewmodels.RequerimentosViewModel

@Composable
fun RequerimentosScreen(
    onBackClick: () -> Unit,
    onRequerimentoClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: RequerimentosViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val requestsList by viewModel.filteredRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Requerimentos",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "requerimentoslist",
                onItemSelected = { item -> onNavigate(item.route) }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Procurar por nome...",
                modifier = Modifier.padding(16.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF00713C)
                    )
                } else if (requestsList.isEmpty()) {
                    Text(
                        text = "Não existem requerimentos.",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(requestsList) { req ->
                            RequerimentoListItem(
                                applicantName = req.beneficiaryName,
                                avatarUrl = null,
                                status = req.status,
                                onClick = { onRequerimentoClick(req.requestId) }
                            )
                        }
                    }
                }
            }
        }
    }
}