package com.example.project2

import com.example.project2.data.local.MathCorpus
import com.example.project2.data.model.CourseLevel
import com.example.project2.data.model.ForeheadCategory
import com.example.project2.data.model.MathDifficultyLevel
import com.example.project2.data.model.Player
import com.example.project2.data.model.SpyRole
import com.example.project2.data.repository.MathRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MathPartyLogicTest {

    @Test
    fun testCorpusContainsAllFacultiesAndCategories() {
        for (level in MathDifficultyLevel.entries) {
            val words = MathCorpus.SPY_WORDS.filter { it.levelIndex == level.levelIndex }
            assertTrue("Level ${level.title} must have words", words.isNotEmpty())
            val randomWord = MathCorpus.getRandomSpyWord(level.levelIndex)
            assertNotNull(randomWord.term)
            assertTrue(randomWord.term.isNotBlank())
            assertTrue(randomWord.hint.isNotBlank())
        }

        for (category in ForeheadCategory.values()) {
            val cards = MathCorpus.getRandomForeheadCards(category, count = 10)
            assertEquals(10, cards.size)
            cards.forEach {
                assertTrue(it.term.isNotBlank())
                assertTrue(it.clue.isNotBlank())
            }
        }

        val diagram = MathCorpus.getRandomVisualDiagram()
        assertTrue("Visual diagram title must not be blank", diagram.title.isNotBlank())
        assertTrue("Visual diagram type must not be blank", diagram.diagramType.isNotBlank())
    }

    @Test
    fun testRepositoryFallbackAndDataIntegrity() = runBlocking {
        val repository = MathRepository()

        val spyWord = repository.getSpyTerm(MathDifficultyLevel.HARD)
        assertNotNull(spyWord)
        assertTrue(spyWord.term.isNotBlank())
        assertEquals(2, spyWord.levelIndex)

        val foreheadCards = repository.getForeheadCards(ForeheadCategory.MEMES_PARADOXES, 12)
        assertTrue(foreheadCards.isNotEmpty())

        val diagram = repository.getTelephoneDiagram()
        assertTrue(diagram.title.isNotBlank())
    }

    @Test
    fun testSpyGameRegularModeRolesDistribution() {
        val playerCount = 6
        val requestedSpies = 2

        val allIndices = (0 until playerCount).shuffled()
        val spyIndices = allIndices.take(requestedSpies).toSet()

        val players = (0 until playerCount).map { idx ->
            Player(
                id = idx,
                name = "Игрок ${idx + 1}",
                role = if (spyIndices.contains(idx)) SpyRole.SPY else SpyRole.INNOCENT
            )
        }

        assertEquals(6, players.size)
        val spies = players.filter { it.role == SpyRole.SPY }
        val innocents = players.filter { it.role == SpyRole.INNOCENT }

        assertEquals(2, spies.size)
        assertEquals(4, innocents.size)
    }

    @Test
    fun testSpyGameChaosModeRandomSpies() {
        val playerCount = 8
        val maxSpies = playerCount / 2
        val observedSpyCounts = mutableSetOf<Int>()

        repeat(50) {
            val actualSpyCount = Random.nextInt(0, maxSpies + 1)
            assertTrue("Actual spies must be in 0..$maxSpies", actualSpyCount in 0..maxSpies)

            val allIndices = (0 until playerCount).shuffled()
            val spyIndices = allIndices.take(actualSpyCount).toSet()
            val players = (0 until playerCount).map { idx ->
                Player(
                    id = idx,
                    name = "Игрок ${idx + 1}",
                    role = if (spyIndices.contains(idx)) SpyRole.SPY else SpyRole.INNOCENT
                )
            }

            assertEquals(playerCount, players.size)
            assertEquals(actualSpyCount, players.count { it.role == SpyRole.SPY })
            observedSpyCounts.add(actualSpyCount)
        }

        assertTrue("Chaos mode should generate different counts across runs", observedSpyCounts.size > 1)
    }

    @Test
    fun testTelephonePlayerConstraints() {
        fun sanitizePlayerCount(input: Int): Int = input.coerceIn(3, 8)

        assertEquals(8, sanitizePlayerCount(10))
        assertEquals(3, sanitizePlayerCount(2))
        assertEquals(5, sanitizePlayerCount(5))
    }

    @Test
    fun testVisualDiagramsHaveSearchQueries() {
        for (diagram in MathCorpus.VISUAL_DIAGRAMS) {
            assertTrue("Diagram ${diagram.title} must have a non-blank search query", diagram.searchQuery.isNotBlank())
            assertTrue("Diagram ${diagram.title} must have a non-blank title", diagram.title.isNotBlank())
        }
    }

    @Test
    fun testSpyCourseAndDifficultyLevels() = runBlocking {
        val repository = MathRepository()
        for (course in CourseLevel.entries) {
            for (diff in MathDifficultyLevel.entries) {
                val word = repository.getSpyTerm(course.levelIndex, diff.levelIndex)
                assertNotNull(word)
                assertTrue(word.term.isNotBlank())
                assertTrue(word.hint.isNotBlank())
            }
        }
    }
}
