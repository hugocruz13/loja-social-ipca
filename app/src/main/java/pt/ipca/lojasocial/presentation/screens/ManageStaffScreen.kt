package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.Colaborador
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.AdicionarButton
import pt.ipca.lojasocial.presentation.viewmodels.StaffViewModel

@Composable
fun ManageStaffScreen(
    onBackClick: () -> Unit,
    onAddStaffClick: () -> Unit,
    viewModel: StaffViewModel = hiltViewModel()
) {
    val staffList by viewModel.colaboradores.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Gestão de Colaboradores",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            AdicionarButton(onClick = onAddStaffClick)
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(staffList) { staff ->
                StaffMemberCard(
                    staff = staff,
                    onToggleStatus = { viewModel.toggleStatus(staff.uid, staff.ativo) }
                )
            }
        }
    }
}

@Composable
fun StaffMemberCard(
    staff: Colaborador,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = staff.nome,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (staff.ativo) Color.Black else Color.Gray
                )
                Text(
                    text = "${staff.cargo} • ${staff.permissao}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = staff.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray.copy(alpha = 0.7f)
                )
            }

            // Switch para Ativar/Desativar
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (staff.ativo) "Ativo" else "Inativo",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (staff.ativo) Color(0XFF00713C) else Color.Red
                )
                Switch(
                    checked = staff.ativo,
                    onCheckedChange = { onToggleStatus() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0XFF00713C),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }
        }
    }
}