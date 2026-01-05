package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val searchShape = RoundedCornerShape(8.dp)

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },

        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = "Pesquisar")
        },

        modifier = modifier.fillMaxWidth(),
        singleLine = true,

        shape = searchShape,

        colors = OutlinedTextFieldDefaults.colors(

            // Cor das bordas (cinza claro é comum)
            unfocusedBorderColor = Color(0XFF8E8E93),
            focusedBorderColor = Color(0XFF8E8E93),

            // Cor do texto de placeholder
            unfocusedPlaceholderColor = Color(0XFF8E8E93)
        )
    )
}

@Preview(name = "Search Bar Vazia", showBackground = true)
@Composable
fun AppSearchBarEmptyPreview() {
    var searchText by remember { mutableStateOf("") }
    Surface(modifier = Modifier.padding(16.dp)) {
        AppSearchBar(
            query = searchText,
            onQueryChange = { searchText = it },
            placeholder = "Procurar por nome ou ID"
        )
    }
}

@Preview(name = "Search Bar Preenchida", showBackground = true)
@Composable
fun AppSearchBarFilledPreview() {
    var searchText by remember { mutableStateOf("John Doe") }
    Surface(modifier = Modifier.padding(16.dp)) {
        AppSearchBar(
            query = searchText,
            onQueryChange = { searchText = it },
            placeholder = "Procurar por nome ou ID"
        )
    }
}