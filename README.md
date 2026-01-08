# Loja Social IPCA

Aplicação Android desenvolvida para a gestão eficiente de uma **Loja Social**. Este projeto permite gerir beneficiários, controlar o inventário de produtos, organizar campanhas de recolha e processar requerimentos de ajuda de forma centralizada e digital.

---

## Funcionalidades Principais

* **Gestão de Beneficiários:** Listagem completa com filtros por estado (Ativo, Inativo, Análise) e pesquisa avançada.
* **Controlo de Stock (Inventário):** Gestão de produtos categorizados por tipos (Alimentação, Higiene, Limpeza, Outros) com registo de quantidades.
* **Sistema de Campanhas:** Planeamento e monitorização de campanhas de recolha (Ativas, Agendadas e Concluídas).
* **Requerimentos de Apoio:** Fluxo de pedidos de ajuda com acompanhamento de estados em tempo real.
* **Relatórios Automáticos:** Geração de ficheiros PDF com dados de validade e inventário para facilitar a gestão administrativa.

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
**Desenvolvido para fins académicos - IPCA**