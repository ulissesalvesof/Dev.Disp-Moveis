# Study Planner 📚

Um aplicativo de planejamento de estudos desenvolvido em Kotlin e Jetpack Compose, com funcionalidades avançadas de notificações, armazenamento local e integração multimídia. Projeto desenvolvido para atender aos requisitos dos Trabalhos Práticos 1 e 2 da disciplina de Desenvolvimento Mobile.

---

## Funcionalidades Principais 🚀

### 📋 Gerenciamento de Tarefas
- **Adicionar/Editar/Excluir tarefas** com título, descrição e link de vídeo opcional.
- **Marcar tarefas como concluídas** ou pendentes.
- **Favoritar tarefas** para acesso rápido.
- **Filtro dinâmico** por título ou descrição.

### 🎥 Multimídia Integrada
- Reprodução de **vídeos do YouTube** diretamente no aplicativo.
- Validação de URLs do YouTube.

### 🔔 Notificações Inteligentes
- **Agendar lembretes** para tarefas (disparo após 1 minuto).
- Implementação com `AlarmManager` e `BroadcastReceiver`.
- Serviço de notificação em foreground com canal dedicado.

### 🎨 Personalização
- **Modo Escuro** configurável via Jetpack DataStore.
- Configurações de **notificações e animações**.
- Interface seguindo **Material Design 3.0**.

### 🔄 Funcionalidades Avançadas
- **Jetpack DataStore** para persistência de preferências.
- **Animações** em transições e interações.
- Navegação entre telas via `NavController`.
- Menu de três pontos para acesso rápido a:
  - Favoritos ★
  - Tarefas Concluídas ✅
  - Configurações ⚙️
  - Ajuda ❓

---

## Tecnologias Utilizadas 🛠️

- **Linguagem**: Kotlin
- **UI**: Jetpack Compose
- **Persistência**: Jetpack DataStore (Preferences)
- **Multimídia**: YouTube Android Player API
- **Notificações**: `AlarmManager` + `WorkManager`
- **Arquitetura**: Componentes Android Modernos (Corrotinas, Fluxos)
- **Bibliotecas**:
  - `androidx.datastore:datastore-preferences`
  - `com.pierfrancescosoffritti.androidyoutubeplayer:core`

---

## Pré-requisitos 📋

- Android Studio Flamingo ou superior
- SDK Android 33+
- Dispositivo/Emulador com Google Play Services

---

## Como Executar ▶️

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/study-planner.git
