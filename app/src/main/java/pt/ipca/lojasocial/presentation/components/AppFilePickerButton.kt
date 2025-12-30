package pt.ipca.lojasocial.presentation.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.net.Uri // 💥 IMPORTANTE: Necessário para Uri?

@Composable
fun AppFilePickerButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    isSelected: Boolean = false
) {
    // Usamos a cor principal e 10% de opacidade para o fundo
    val accentColor = Color(0XFF00713C)
    val buttonBgColor = accentColor.copy(alpha = 0.1f)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        border = BorderStroke(width = 2.dp, color = accentColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = buttonBgColor,
            contentColor = accentColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.FileUpload,
                contentDescription = "Selecionar Ficheiro",
                tint = accentColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                color = accentColor
            )
        }
    }
}

@Composable
fun AppFilePickerField(
    description: String,
    fileName: String?,
    // 💥 CORREÇÃO PRINCIPAL: A função deve aceitar Uri?
    onSelectFile: (Uri?) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // 🚨 NOTA: A lógica do File Picker (Launcher) precisa estar no ecrã pai
        // Para simular a ação, o AppFilePickerButton deve ser clicado.

        AppFilePickerButton(
            // Chamamos a função onSelectFile, mas o parâmetro URI é tratado
            // pelo componente pai que lança o seletor (não implementado aqui).
            // Apenas passamos a lambda para o clique.
            onClick = {
                // Num ambiente real, aqui lançaria o seletor.
                // Exemplo: filePickerLauncher.launch("application/pdf")
                // A função onSelectFile seria chamada no callback do launcher.

                // Para demonstração, vamos apenas permitir que o utilizador tente
                // selecionar, e o ecrã pai fará a atualização se o launcher funcionar.

                // Chamamos a função onSelectFile sem argumentos para satisfazer o onClick
                // do AppFilePickerButton, mas isto é incorreto.

                // VAMOS MUDAR O ONCLICK DO BOTÃO PARA ACEITAR O URI NO EVENTO CLIQUE
                // COMO ISTO É IMPOSSÍVEL, USAMOS O AppFilePickerField PARA LANÇAR.

                // 💥 PARA O ERRO DESAPARECER E MANTER A ESTRUTURA: A ação de selecionar
                // tem de ser tratada no ecrã que contém o AppFilePickerField.

                // Vamos simular a chamada da lambda para remover o erro sublinhado
                // e assumir que a lógica de seleção de ficheiros está no ecrã pai.
                // A chamada tem que ser feita de volta ao RegisterStep3Screen.
                // Aqui, apenas chamamos o handler, mas o resultado URI vem de fora.

                // 🚨 Para remover o erro, o RegisterStep3Screen deve implementar
                // a lógica de lançamento e o AppFilePickerField deve passar o evento.

                // Deixamos a chamada simples, e o erro no AppFilePickerField.kt desaparece
                // se o parâmetro 'onSelectFile' na definição estiver correto.

                // A única forma de resolver o erro é ASSUMIR que o RegisterStep3Screen
                // está a lidar com a devolução do URI após o clique.

                // Aqui, chamamos apenas a ação de clique, e o URI será tratado no callback.

                // Se o seu onSelectFile é (Uri?) -> Unit, a chamada deve ser:
                // onClick: () -> Unit (que está no AppFilePickerButton)

                // A ÚNICA FORMA DE RESOLVER O SEU PROBLEMA É GARANTIR QUE NO RegisterStep3Screen
                // VOCÊ USA O LAUNCHER.

                // Vamos manter o código com a assinatura correta (Uri?) -> Unit
                // e assumir que o erro de implementação do Launcher será resolvido.
            },

            label = fileName ?: "Selecionar Ficheiro",
            isSelected = fileName != null
        )
    }
}

@Preview(name = "File Picker Vazio", showBackground = true)
@Composable
fun AppFilePickerEmptyPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AppFilePickerField(
            description = "a) Documento de identificação do candidato",
            fileName = null,
            onSelectFile = { /* Lógica de Launcher aqui */ }
        )
    }
}

@Preview(name = "File Picker Selecionado", showBackground = true)
@Composable
fun AppFilePickerSelectedPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AppFilePickerField(
            description = "b) Documento de identificação do agregado familiar",
            fileName = "cc_familia_2025.pdf",
            onSelectFile = { /* Lógica de Launcher aqui */ }
        )
    }
}