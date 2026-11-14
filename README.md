# Budgeting API com Spring AI

API de orçamento que recebe **comandos de voz** sobre gastos financeiros, usa IA para entender a intenção, executa funções reais da aplicação (registrar ou consultar transações) e devolve a resposta **em áudio**.

Desafio de Projeto final da trilha de Spring Boot do bootcamp de Java com Inteligência Artificial da [DIO](https://www.dio.me/).

> **Origem do projeto**
>
> Este repositório é baseado no projeto [`05-spring-ai`](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/tree/main/05-spring-ai) do repositório [digitalinnovationone/dio-spring-boot-learning-track](https://github.com/digitalinnovationone/dio-spring-boot-learning-track), construído nas aulas com o expert Poiani.
>
> O código da aplicação (domínio, casos de uso, controller, prompts e testes) é o do projeto original. As diferenças estão descritas em [Alterações em relação ao projeto original](#alterações-em-relação-ao-projeto-original).

## Alterações em relação ao projeto original

### Versões

| Item | Original | Este projeto |
|---|---|---|
| Java (toolchain do Gradle) | 25 | **21** (LTS) |
| Spring Boot | 4.0.5 | 4.0.5 |
| Spring AI | 2.0.0-M4 | 2.0.0-M4 |
| Gradle (wrapper) | 9.4.1 | 9.4.1 |
| Plugin Lombok (`io.freefair.lombok`) | 9.2.0 | 9.2.0 |
| MySQL (Docker Compose) | 9.6 | 9.6 |

A única mudança de versão é o **Java 25 → 21**, para usar o JDK instalado no ambiente de desenvolvimento. Não foi preciso alterar código: o projeto só usa recursos disponíveis desde o Java 17 (`record`, `var`, `Stream.toList()`). As demais versões foram mantidas iguais às das aulas.

### Outras alterações

- **Testes no Windows com acento no caminho** (`build.gradle`): quando a pasta do projeto tem caracteres como `ê` (ex.: `Java com Inteligência Artificial`), o `gradlew test` falhava com `ClassNotFoundException`. O Gradle grava o classpath do processo de testes num arquivo em UTF-8, mas o `java.exe` lê esse arquivo no encoding nativo do Windows (Cp1252). A correção gera um *pathing jar* (um JAR cujo manifest lista o classpath em formato URL) numa pasta temporária sem acento. O ajuste **só é ativado no Windows com caminho não-ASCII**; em qualquer outro ambiente o build é idêntico ao original.
- **Chave da OpenAI fora do repositório**: adicionado o `.env.example` como modelo, e o `.env` (com a chave real) entrou no `.gitignore`.
- **README**: traduzido para português, com instruções de execução atualizadas e os links do README raiz da trilha apontando para o GitHub. No original eram links relativos (`../README.md`), que não funcionam fora daquele repositório.

## O que o projeto faz

Fluxo principal do endpoint de IA:

1. O cliente envia um arquivo de áudio.
2. O áudio é transcrito para texto.
3. O modelo escolhe uma ferramenta (*tool*), que corresponde a um caso de uso da aplicação.
4. O caso de uso grava ou consulta transações no banco.
5. A resposta final é convertida em áudio (MP3).

A IA é integrada **sem atravessar as fronteiras do domínio e dos casos de uso**: as mesmas classes de caso de uso atendem tanto os endpoints REST quanto o *tool calling* do modelo.

## Tecnologias

- **Java 21** e **Spring Boot 4.0.5** (Web, Data JPA, suporte a Docker Compose)
- **Spring AI 2.0.0-M4** com OpenAI:
  - `gpt-4o-mini`: chat e *tool calling*
  - `whisper-1`: transcrição de áudio (português)
  - `gpt-4o-mini-tts`: geração de voz (voz `nova`, saída MP3)
- **MySQL 9.6** via Docker Compose
- **Lombok**, **JUnit 5**, **Gradle 9.4.1**

## Estrutura do projeto

- `src/main/java/dio/budgeting/domain`: modelo de domínio e contrato do repositório.
- `src/main/java/dio/budgeting/application`: casos de uso, usados tanto pela API REST quanto pelo *tool calling*.
- `src/main/java/dio/budgeting/infrastructure`: adaptadores HTTP e JPA, e a integração entre as camadas.
- `src/main/resources/prompts/system-message.st`: prompt de sistema do assistente.
- `src/main/resources/application.properties`: configuração dos modelos de IA.

### Tópicos do módulo

- **Speech-to-text**: `TranscriptionModel` transcreve o áudio. As configurações do modelo ficam no `application.properties`.
- **Tool calling**: o `ChatClient` registra os casos de uso como ferramentas. Os métodos anotados com `@Tool` (`persist-transaction` e `list-transactions-by-category`) expõem as capacidades de negócio ao modelo.
- **Text-to-speech**: `TextToSpeechModel` gera o MP3 a partir do texto final, e o endpoint de IA devolve esse áudio.

## Como executar

### Pré-requisitos

- JDK 21
- Docker (no Windows, o Docker Desktop precisa estar aberto)
- Uma chave da API da OpenAI

### 1. Configurar a chave da OpenAI

Copie o `.env.example` para `.env` e preencha a chave:

```
OPENAI_API_KEY=sua_chave_aqui
```

Depois carregue a variável no terminal:

```bash
# Linux / macOS / Git Bash
set -a; source .env; set +a
```

```powershell
# Windows PowerShell
Get-Content .env | ForEach-Object { if ($_ -match '^\s*([A-Za-z_]\w*)\s*=\s*(.*)$') { Set-Item "env:$($Matches[1])" $Matches[2] } }
```

### 2. Subir a aplicação

```bash
./gradlew bootRun        # Linux / macOS
.\gradlew.bat bootRun    # Windows
```

O suporte a Docker Compose do Spring Boot sobe o MySQL automaticamente, usando o `compose.yml` (porta `3307`). A API fica disponível em `http://localhost:8080`.

## Como testar

### Endpoints

| Método | Rota | Corpo | Resposta |
|---|---|---|---|
| `POST` | `/transactions` | JSON | transação criada |
| `GET` | `/transactions/{categoria}` | — | transações da categoria (`GROCERIES`, `PHARMA` ou `AUTO`) |
| `POST` | `/transactions/ai` | `multipart/form-data` com o campo `file` (áudio) | áudio MP3 com a resposta |

### Fluxo com áudio

A pasta `src/test/resources/audio` tem gravações de exemplo: `recording-1.m4a` a `recording-6.m4a`, com gastos de 80, 40, 120, 90, 200 e 60 reais.

```bash
# Linux / macOS / Git Bash
curl -X POST http://localhost:8080/transactions/ai \
  -F "file=@src/test/resources/audio/recording-1.m4a" \
  -o resposta.mp3
```

```powershell
# Windows PowerShell (use curl.exe: no PowerShell 5.1, "curl" é um apelido do Invoke-WebRequest)
curl.exe -X POST "http://localhost:8080/transactions/ai" -F "file=@src/test/resources/audio/recording-1.m4a" -o resposta.mp3
```

Abra o `resposta.mp3` para ouvir a resposta. Depois, confira a transação registrada:

```bash
curl http://localhost:8080/transactions/GROCERIES
```

### Endpoints JSON

```bash
# Linux / macOS / Git Bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{"description":"Compra no mercado","category":"GROCERIES","amount":8000}'
```

```powershell
# Windows PowerShell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/transactions -ContentType 'application/json' -Body '{"description":"Compra no mercado","category":"GROCERIES","amount":8000}'
Invoke-RestMethod http://localhost:8080/transactions/GROCERIES
```

### Testes automatizados

```bash
./gradlew test        # Linux / macOS
.\gradlew.bat test    # Windows
```

- Os testes `*IT` (`OpenAiChatModelIT`, `OpenAiChatClientIT`, `ToolCallingIT`, `OpenAiTranscriptionModelIT` e `OpenAiSpeechModelIT`) chamam a API real da OpenAI. Eles só rodam com a `OPENAI_API_KEY` definida e, sem ela, são ignorados. Cada execução consome créditos da conta.
- O `BudgetingApplicationTests` falha nesta versão. Veja as limitações abaixo.

### Limitações conhecidas (herdadas do projeto original)

- **`BudgetingApplicationTests.contextLoads` falha**: o banco só é configurado pelo Docker Compose, que é `developmentOnly` e não vale para os testes. Por isso o contexto não encontra um `DataSource`.
- **Valor em centavos não é convertido na resposta**: a ferramenta recebe o valor em centavos ("80 reais" vira `8000`), mas o `TransactionOutput` não divide por 100, então a resposta mostra `8000.0`.

## Qual melhoria você implementou

> **A fazer (TODO):** descrever a melhoria implementada neste projeto.

<!--
Sugestão do que incluir:
- qual melhoria foi escolhida e por quê;
- quais arquivos foram criados ou alterados;
- como testar a melhoria (requisição de exemplo, teste automatizado);
- prints ou anotações do processo, se houver.
-->

## O que você aprendeu

> **A fazer (TODO):** descrever o que foi aprendido durante o desafio.

<!--
Sugestão do que incluir:
- conceitos novos (Spring AI, ChatClient, tool calling, transcrição, geração de voz...);
- dificuldades encontradas e como foram resolvidas;
- o que faria diferente ou evoluiria depois.
-->

## Referências

### Documentação do Spring AI

- [Spring AI Reference](https://docs.spring.io/spring-ai/reference/index.html)
- [ChatModel API](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [Tools API](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [Audio Transcriptions API](https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html)
- [Audio Speech API](https://docs.spring.io/spring-ai/reference/api/audio/speech.html)

### Arquitetura (README raiz da trilha original, em inglês)

- [DDD layers](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/README.md#ddd-layered-architecture)
- [Class vs record](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/README.md#java-class-vs-java-record-in-domain-modeling)
- [Strong typed identifiers](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/README.md#strong-typed-identifiers)
- [Repository pattern](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/README.md#repository-pattern)
- [Use cases and Clean Architecture](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/README.md#use-cases-and-clean-architecture)
- [Docker Compose support](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/README.md#docker-compose-support-in-development)
