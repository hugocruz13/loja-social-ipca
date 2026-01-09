package pt.ipca.lojasocial.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.models.ChatMessage
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor() : ViewModel() {

    // Lista de mensagens observável pela UI
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Estado de carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- CONFIGURAÇÃO DO CÉREBRO DA IA ---
    private val systemInstructions = content {
        text(
            """
            Tu és o assistente virtual da aplicação 'Loja Social IPCA', focado em ajudar beneficiários ativos.
            
            Contexto: O utilizador já é um beneficiário aprovado. Não respondas a questões sobre como se candidatar.
            O teu foco é ajudar na gestão das recolhas de bens (Entregas).
            
            Usa estas FAQs para responder:
            
            1. Como agendar uma nova entrega?
               R: Acede ao menu 'Entregas' e clica no botão '+'. Depois escolhe a data, a hora e seleciona os produtos que precisas (ex: Arroz, Massa, etc.).
            
            2. Que produtos posso pedir?
               R: Podes solicitar itens essenciais disponíveis no stock, que estarão disponíveis aquando do pedido de entrega.
            
            3. Como sei se tenho entregas por levantar?
               R: Verifica o cartão 'Entregas Pendentes' no topo do teu Ecrã Principal (Dashboard) ou consulta o filtro 'Pendente' no menu 'Entregas'.
            
            4. Onde vejo o que já recebi no passado?
               R: No menu 'Entregas', usa o filtro 'Entregue' para veres todo o teu histórico de apoios recebidos.
            
            Regras:
            - Responde sempre em Português de Portugal.
            - Sê direto e prático.
            - Se perguntarem sobre candidaturas, diz: "Como já tens acesso à app, a tua candidatura já está ativa."
            - Se perguntarem sobre stock ou gestão interna, diz que não tens acesso a essa informação."
        ""${'"'}.trimIndent())
        """.trimIndent()
        )
    }

    // Inicializa o modelo (Flash é rápido e barato)
    private val generativeModel = Firebase.vertexAI.generativeModel(
        modelName = "gemini-1.5-flash",
        systemInstruction = systemInstructions
    )

    // Inicia o chat (mantém histórico da conversa)
    private val chat = generativeModel.startChat()

    init {
        // Mensagem inicial automática
        addMessage("Olá! Sou o assistente da Loja Social. Em que posso ajudar?", isUser = false)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        // 1. Adiciona a mensagem do user à UI
        addMessage(userText, isUser = true)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // 2. Envia para o Firebase/Gemini
                val response = chat.sendMessage(userText)

                // 3. Processa a resposta
                response.text?.let { aiResponse ->
                    addMessage(aiResponse, isUser = false)
                }
            } catch (e: Exception) {
                addMessage("Erro de ligação. Verifica a tua internet.", isUser = false)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        val newMessage = ChatMessage(text = text, isFromUser = isUser)
        _messages.value = _messages.value + newMessage
    }
}