# Loja Social IPCA – Sistema Integrado de Apoio Social

Este projeto consiste num sistema digital completo, composto por uma **Aplicação Móvel Android**, destinado a modernizar e otimizar a gestão da Loja Social do IPCA. O sistema substitui processos manuais ineficientes por uma solução centralizada que promove a transparência e a entreajuda na comunidade académica.

---

## Funcionalidades Principais

### Gestão de Beneficiários
* **Registo Digital:** Fluxo sequencial para recolha de dados pessoais, académicos e submissão de documentos obrigatórios (RGPD).
* **Candidaturas (Requerimentos):** Submissão e monitorização do estado do pedido de apoio (Submetido, Em Análise, Em Falta, Aprovado ou Rejeitado).
* **Perfil Autónomo:** Autonomia para o beneficiário consultar o histórico de apoios e editar dados pessoais.

### Gestão de Inventário (Bens)
* **Controlo de Stock:** Diferenciação entre catálogo de produtos (tipologia) e stock físico (quantidades e lotes).
* **Categorização:** Organização por tipos de bens: Alimentares, Higiene Pessoal e Limpeza.
* **Gestão de Validades:** Monitorização de datas de validade com sistema de notificações automáticas para evitar desperdícios.

### Gestão de Entregas
* **Agendamento Flexível:** Calendarização de levantamentos mensais com opção de repetição automática.
* **Notificações:** Alertas *push* e e-mails para relembrar os beneficiários das datas de entrega.
* **Validação Operacional:** Registo imediato do estado da entrega (Entregue/Não Entregue) por parte dos colaboradores do SAS.

### Campanhas e Comunidade
* **Monitorização de Campanhas:** Gestão do ciclo de vida de iniciativas de recolha internas e externas.

---

## Stack Tecnológica

* **Linguagem:** [Kotlin]
* **UI:** [Jetpack Compose]
* **Arquitetura:** MVVM (Model-View-ViewModel) + Clean Architecture.
* **Injeção de Dependências:** [Hilt]
* **Base de Dados & Backend:** [Firebase]
    * **Firestore:** Armazenamento de dados NoSQL.
    * **Authentication:** Autenticação de utilizadores.
    * **Storage:** Armazenamento de imagens (produtos/campanhas).
* **Navegação:** Compose Navigation.

---

## Organização do Código

O projeto segue os princípios de **Clean Architecture**, dividindo-se em:

1.  **Domain:** Lógica de negócio pura (Use Cases e Modelos de Domínio).
2.  **Data:** Repositórios e Data Sources (integração com Firebase).
3.  **Presentation:** Camada de UI organizada por ecrãs (`Screens`), componentes reutilizáveis (`Components`) e gestão de estado (`ViewModels`).

---

## Como Configurar e Executar

1.  **Clonar o Repositório:**
    ```bash
    git clone [https://github.com/GustaM11/loja-social-ipca.git](https://github.com/GustaM11/loja-social-ipca.git)
    ```
2.  **Configuração do Firebase:**
    * Cria um novo projeto na [Consola do Firebase].
    * Adiciona uma aplicação Android com o package `pt.ipca.lojasocial`.
    * Faz o download do ficheiro `google-services.json` e coloca-o na pasta `app/`.
3.  **Execução:**
    * Abre o projeto no **Android Studio**.
    * Sincroniza os ficheiros Gradle.
    * Executa num emulador ou dispositivo físico com API 24 ou superior.

---

## Equipa (Projeto Aplicado & Programação Dispositivos Móveis)
* 27962 Gustavo Daniel Loureiro Marques
* 29852 Gustavo da Costa Pereira
* 23010 Hugo Tiago Mendes Cruz 
* 27977 Igor Miguel Torres da Costa 
* 23016 Dani Carvalho da Cruz 
* **Orientação:** Prof.ª Patrícia Isabel Sousa Trindade Silva Leite & Prof.º Lourenço Miguel Araújo Gomes

---
**Escola Superior de Tecnologia – IPCA**
Licenciatura em Engenharia de Sistemas Informáticos 
