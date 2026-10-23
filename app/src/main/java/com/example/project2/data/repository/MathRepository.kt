package com.example.project2.data.repository

import com.example.project2.data.local.MathCorpus
import com.example.project2.data.model.AiSettings
import com.example.project2.data.model.CourseLevel
import com.example.project2.data.model.ForeheadCard
import com.example.project2.data.model.ForeheadCategory
import com.example.project2.data.model.MathDifficultyLevel
import com.example.project2.data.model.MathVisualDiagram
import com.example.project2.data.remote.ChatCompletionRequest
import com.example.project2.data.remote.ChatMessage
import com.example.project2.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class MathRepository(
    private val client: RetrofitClient = RetrofitClient,
    private val imageService: com.example.project2.data.remote.InternetImageService = com.example.project2.data.remote.InternetImageService()
) {
    private val _aiSettings = MutableStateFlow(AiSettings())
    val aiSettings: StateFlow<AiSettings> = _aiSettings.asStateFlow()

    fun updateSettings(settings: AiSettings) {
        _aiSettings.value = settings
    }

    suspend fun getSpyTerm(courseLevelIndex: Int, difficultyLevelIndex: Int): MathCorpus.SpyWord = withContext(Dispatchers.IO) {
        val course = CourseLevel.fromLevel(courseLevelIndex)
        val difficulty = MathDifficultyLevel.fromLevel(difficultyLevelIndex)
        val settings = _aiSettings.value
        if (settings.useRemoteAi && settings.apiKey.isNotBlank()) {
            try {
                val prompt = "Ты ведущий веселой пати-игры «Математический Шпион». " +
                        "Сгенерируй ОДИН математический термин для раунда. " +
                        "Уровень образования: ${course.title} (${course.subtitle}). " +
                        "Сложность: ${difficulty.title} (${difficulty.subtitle}). " +
                        "КРИТИЧЕСКИ ВАЖНО: термин должен быть ШИРОКО ИЗВЕСТЕН и понятен обычным игрокам в компании! " +
                        "Категорически запрещены узкие аспирантские термины. " +
                        "Термин должен быть ярким, на слуху и удобным для обсуждения и наводящих вопросов в игре (например, для 11 класса / Легко: Калькулятор, Линейка, Таблица умножения, Ноль; для 1 курса: Вектор, Матрица; для 2 курса: Производная, Интеграл; для 3 курса: Граф, Теория вероятностей; для 4 курса: Лента Мёбиуса, Парадокс Монти Холла, Кот Шрёдингера). " +
                        "Формат строго одной строкой: ТЕРМИН | КРАТКАЯ_ОСТРОУМНАЯ_ПОДСКАЗКА"

                val endpoint = if (settings.endpointUrl.endsWith("/")) {
                    "${settings.endpointUrl}chat/completions"
                } else {
                    "${settings.endpointUrl}/chat/completions"
                }

                val response = client.openAiApi.createChatCompletion(
                    url = endpoint,
                    authorization = "Bearer ${settings.apiKey.trim()}",
                    request = ChatCompletionRequest(
                        model = settings.modelName,
                        messages = listOf(
                            ChatMessage("system", "Ты генератор математических терминов для пати-игры."),
                            ChatMessage("user", prompt)
                        ),
                        temperature = 0.9,
                        maxTokens = 60
                    )
                )

                val content = response.choices?.firstOrNull()?.message?.content?.trim()
                if (!content.isNullOrBlank()) {
                    val parts = content.split("|")
                    if (parts.size >= 2) {
                        return@withContext MathCorpus.SpyWord(
                            term = parts[0].trim().removeSurrounding("\""),
                            courseLevel = courseLevelIndex,
                            difficultyLevel = difficultyLevelIndex,
                            hint = parts[1].trim()
                        )
                    } else if (parts.size == 1) {
                        return@withContext MathCorpus.SpyWord(
                            term = parts[0].trim().removeSurrounding("\""),
                            courseLevel = courseLevelIndex,
                            difficultyLevel = difficultyLevelIndex,
                            hint = "${course.title} • ${difficulty.title}"
                        )
                    }
                }
            } catch (e: Exception) {
                // Fallback to local corpus seamlessly
            }
        }
        MathCorpus.getRandomSpyWord(courseLevelIndex, difficultyLevelIndex)
    }

    suspend fun getSpyTerm(levelIndex: Int): MathCorpus.SpyWord = getSpyTerm(1, levelIndex)

    suspend fun getSpyTerm(faculty: MathDifficultyLevel): MathCorpus.SpyWord = getSpyTerm(1, faculty.levelIndex)

    suspend fun getForeheadCards(category: ForeheadCategory, count: Int = 15): List<ForeheadCard> = withContext(Dispatchers.IO) {
        val settings = _aiSettings.value
        if (settings.useRemoteAi && settings.apiKey.isNotBlank()) {
            try {
                val prompt = "Сгенерируй $count карточек для игры «Кто я? На лбу» в категории «${category.displayName}». " +
                        "Каждая строка строго в формате: ТЕРМИН_ИЛИ_ИМЯ | КРАТКАЯ_ПОДСКАЗКА_ДЛЯ_ДРУЗЕЙ"

                val endpoint = if (settings.endpointUrl.endsWith("/")) {
                    "${settings.endpointUrl}chat/completions"
                } else {
                    "${settings.endpointUrl}/chat/completions"
                }

                val response = client.openAiApi.createChatCompletion(
                    url = endpoint,
                    authorization = "Bearer ${settings.apiKey.trim()}",
                    request = ChatCompletionRequest(
                        model = settings.modelName,
                        messages = listOf(
                            ChatMessage("system", "Ты генератор математических карточек."),
                            ChatMessage("user", prompt)
                        ),
                        temperature = 0.9,
                        maxTokens = 400
                    )
                )

                val content = response.choices?.firstOrNull()?.message?.content?.trim()
                if (!content.isNullOrBlank()) {
                    val lines = content.lines().filter { it.contains("|") }
                    val parsed = lines.mapNotNull { line ->
                        val parts = line.split("|")
                        if (parts.size >= 2) {
                            ForeheadCard(
                                term = parts[0].trim().replace(Regex("^\\d+[.)]\\s*"), "").removeSurrounding("\""),
                                clue = parts[1].trim(),
                                category = category
                            )
                        } else null
                    }
                    if (parsed.isNotEmpty()) {
                        return@withContext parsed
                    }
                }
            } catch (e: Exception) {
                // Fallback
            }
        }
        MathCorpus.getRandomForeheadCards(category, count)
    }

    suspend fun getTelephoneDiagram(): MathVisualDiagram = withContext(Dispatchers.IO) {
        val settings = _aiSettings.value

        // 1. If remote AI (LLM) is configured and active, ask it to generate a creative math drawing concept
        if (settings.useRemoteAi && settings.apiKey.isNotBlank()) {
            try {
                val prompt = "Ты ведущий математической пати-игры «Испорченный телефон» (рисуем по памяти). " +
                        "Придумай ОДИН узнаваемый математический концепт, формулу, геометрическую фигуру, фрактал или топологический парадокс, который интересно рисовать. " +
                        "Верни строго в формате одной строкой: ТЕРМИН | КРАТКОЕ_ОПИСАНИЕ_ИЛИ_ФОРМУЛА | WIKIPEDIA_QUERY_IN_ENGLISH. " +
                        "Например: Бутылка Клейна | Односторонняя 4D поверхность с самопересекающимся горлышком | Klein bottle"

                val endpoint = if (settings.endpointUrl.endsWith("/")) {
                    "${settings.endpointUrl}chat/completions"
                } else {
                    "${settings.endpointUrl}/chat/completions"
                }

                val response = client.openAiApi.createChatCompletion(
                    url = endpoint,
                    authorization = "Bearer ${settings.apiKey.trim()}",
                    request = ChatCompletionRequest(
                        model = settings.modelName,
                        messages = listOf(
                            ChatMessage("system", "Ты генератор визуальных математических заданий для игры в рисование."),
                            ChatMessage("user", prompt)
                        ),
                        temperature = 0.9,
                        maxTokens = 80
                    )
                )

                val content = response.choices?.firstOrNull()?.message?.content?.trim()
                if (!content.isNullOrBlank()) {
                    val parts = content.split("|")
                    if (parts.size >= 2) {
                        val title = parts[0].trim().removeSurrounding("\"")
                        val caption = parts[1].trim()
                        val searchQ = if (parts.size >= 3) parts[2].trim() else title

                        // Query live internet image from Wikipedia/Wikimedia
                        val liveImageUrl = imageService.searchMathImageUrl(searchQ) ?: imageService.searchMathImageUrl(title)

                        return@withContext MathVisualDiagram(
                            id = "ai_" + System.currentTimeMillis(),
                            title = title,
                            caption = caption,
                            diagramType = "AI_GENERATED",
                            imageUrl = liveImageUrl,
                            searchQuery = searchQ,
                            source = "🤖 Нейросеть + Интернет"
                        )
                    }
                }
            } catch (e: Exception) {
                // Fallback seamlessly to internet search of curated corpus
            }
        }

        // 2. Select from curated corpus and fetch image from the internet in real time!
        val baseDiagram = MathCorpus.getRandomVisualDiagram()
        val query = baseDiagram.searchQuery.ifBlank { baseDiagram.title }
        val liveImageUrl = imageService.searchMathImageUrl(query) ?: imageService.searchMathImageUrl(baseDiagram.title)

        baseDiagram.copy(
            imageUrl = liveImageUrl ?: baseDiagram.imageUrl,
            source = if (liveImageUrl != null) "🌐 Википедия / Интернет" else "📐 Локальный чертёж"
        )
    }
}
