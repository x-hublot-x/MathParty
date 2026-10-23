package com.example.project2.data.model

enum class GameMode(val title: String, val subtitle: String, val icon: String) {
    SPY("Математический Шпион", "Вычисли шпиона среди гиков", "🕵️‍♂️"),
    FOREHEAD("«Кто я?» Математический Лоб", "Угадай формулу или ученого на лбу", "🧠"),
    TELEPHONE("Испорченный Телефон", "Рисуй стилусом S Pen формулы по памяти", "🎨")
}

enum class CourseLevel(
    val levelIndex: Int,
    val title: String,
    val subtitle: String
) {
    GRADE_11(0, "11-й класс", "Школьная программа, ЕГЭ, геометрия и тригонометрия"),
    COURSE_1(1, "1-й курс", "Линейная алгебра, матрицы, векторы и аналит"),
    COURSE_2(2, "2-й курс", "Матанализ, интегралы, ряды и дифуры"),
    COURSE_3(3, "3-й курс", "Дискретная математика, графы и алгоритмы"),
    COURSE_4(4, "4-й курс", "Парадоксы, топология и научпоп");

    companion object {
        fun fromLevel(level: Int): CourseLevel {
            return entries.firstOrNull { it.levelIndex == level } ?: COURSE_1
        }
    }
}

enum class MathDifficultyLevel(
    val levelIndex: Int,
    val title: String,
    val subtitle: String
) {
    EASY(
        0,
        "Легко",
        "Простая математика, быт, предметы и понятные цифры"
    ),
    MEDIUM(
        1,
        "Средне",
        "Школьная геометрия, формулы и основы алгебры"
    ),
    HARD(
        2,
        "Сложно",
        "Вышмат, матрицы, интегралы и известные парадоксы"
    );

    companion object {
        fun fromLevel(level: Int): MathDifficultyLevel {
            return entries.firstOrNull { it.levelIndex == level } ?: MEDIUM
        }
    }
}

// Backward compatibility alias
typealias MathFaculty = CourseLevel

// ----------------- SPY MODELS -----------------

enum class SpyRole {
    INNOCENT,
    SPY
}

data class Player(
    val id: Int,
    val name: String,
    val role: SpyRole = SpyRole.INNOCENT,
    val hasPeeked: Boolean = false
)

data class SpyConfig(
    val playerCount: Int = 4,
    val spyCount: Int = 1,
    val isChaosMode: Boolean = false,
    val courseLevel: Int = 1, // 0: 11 класс, 1: 1 курс, 2: 2 курс, 3: 3 курс, 4: 4 курс
    val difficultyLevel: Int = 1 // 0: Легко, 1: Средне, 2: Сложно
)

data class SpyRoundData(
    val term: String,
    val category: String,
    val description: String,
    val players: List<Player>,
    val actualSpyCount: Int,
    val courseLevel: CourseLevel = CourseLevel.COURSE_1,
    val difficultyLevel: MathDifficultyLevel = MathDifficultyLevel.MEDIUM
)

// ----------------- FOREHEAD MODELS -----------------

enum class ForeheadCategory(val displayName: String, val icon: String) {
    MATHEMATICIANS("Великие ученые", "👨‍🏫"),
    CONCEPTS("Понятия и Формулы", "∑"),
    MEMES_PARADOXES("Парадоксы и Мемы", "🐱")
}

data class ForeheadCard(
    val term: String,
    val clue: String,
    val category: ForeheadCategory
)

// ----------------- TELEPHONE MODELS -----------------

data class MathVisualDiagram(
    val id: String,
    val title: String,
    val caption: String,
    val diagramType: String,
    val imageUrl: String? = null,
    val searchQuery: String = "",
    val source: String = "Википедия / Интернет"
)

data class StrokePoint(
    val x: Float,
    val y: Float,
    val pressure: Float = 0.5f
)

data class DrawingStroke(
    val points: List<StrokePoint>,
    val colorArgb: Long,
    val baseWidth: Float = 6f,
    val isEraser: Boolean = false
)

data class PlayerIteration(
    val playerIndex: Int,
    val playerName: String,
    val strokes: List<DrawingStroke>
)

enum class TelephonePhase {
    GATE_START,         // Player 1 readiness: tap button to reveal initial diagram
    MEMORIZE_ORIGINAL,  // Diagram shown with 10s timer
    DRAWING,            // S Pen canvas
    SHIELD_PASS,        // Screen clear! Shielded: "Pass to Player N"
    GATE_NEXT_VIEW,     // Player N readiness: tap button to view previous drawing
    MEMORIZE_DRAWING,   // Previous drawing shown with 10s timer
    FINAL_GUESS,        // Last player guesses original concept
    GALLERY             // Complete mutation chain
}

// ----------------- AI SETTINGS -----------------

data class AiSettings(
    val useRemoteAi: Boolean = false,
    val apiKey: String = "",
    val endpointUrl: String = "https://api.openai.com/v1/",
    val modelName: String = "gpt-4o-mini"
)
