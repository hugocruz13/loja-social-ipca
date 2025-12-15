package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.R


@Composable
fun AppLogo(modifier: Modifier = Modifier) {


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_lojasocial),
            contentDescription = "Loja Social Logo",
            modifier = Modifier.size(350.dp)
        )

    }
}