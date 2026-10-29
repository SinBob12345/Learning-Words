package com.example.dailyword.data.repository

import com.example.dailyword.data.api.DictionaryApi
import com.example.dailyword.data.model.Definition
import com.example.dailyword.data.model.Meaning
import com.example.dailyword.data.model.Word
import java.util.concurrent.atomic.AtomicInteger

class WordRepositoryImpl(
    private val dictionaryApi: DictionaryApi
) : WordRepository {

    private val easyWords = listOf("apple", "happy", "water", "music", "smile", "house", "sun", "tree", "book", "friend")
    private val mediumWords = listOf("courage", "future", "nature", "wonder", "wisdom", "silence", "journey", "freedom", "balance", "comfort")
    private val hardWords = listOf("eloquent", "resilience", "pragmatic", "benevolent", "meticulous", "tenacity", "nostalgia", "empathy", "serendipity", "ephemeral")

    private val easyIndex = AtomicInteger(0)
    private val mediumIndex = AtomicInteger(0)
    private val hardIndex = AtomicInteger(0)

    private val wordImageMap = mapOf(
        "apple" to "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=600&q=80",
        "happy" to "https://images.unsplash.com/photo-1499209974431-9dddcece7f88?w=600&q=80",
        "water" to "https://p1.ssl.qhimgs1.com/t046ac6ab052addae97.jpg",
        "music" to "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&q=80",
        "smile" to "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=600&q=80",
        "house" to "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=600&q=80",
        "sun" to "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600&q=80",
        "tree" to "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=600&q=80",
        "book" to "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600&q=80",
        "friend" to "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=600&q=80",

        "courage" to "https://images.unsplash.com/photo-1522163182402-834f871fd851?w=600&q=80",
        "future" to "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&q=80",
        "nature" to "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=600&q=80",
        "wonder" to "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&q=80",
        "wisdom" to "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=600&q=80",
        "silence" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
        "journey" to "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=600&q=80",
        "freedom" to "https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?w=600&q=80",
        "balance" to "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=600&q=80",
        "comfort" to "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=600&q=80",

        "eloquent" to "https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=600&q=80",
        "resilience" to "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600&q=80",
        "pragmatic" to "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=600&q=80",
        "benevolent" to "https://images.unsplash.com/photo-1593113598332-cd288d649433?w=600&q=80",
        "meticulous" to "https://images.unsplash.com/photo-1581291518633-83b4ebd1d83e?w=600&q=80",
        "tenacity" to "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=600&q=80",
        "nostalgia" to "https://images.unsplash.com/photo-1516541196182-6bdb0516ed27?w=600&q=80",
        "empathy" to "https://images.unsplash.com/photo-1516585427167-9f4af9627e6c?w=600&q=80",
        "serendipity" to "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=600&q=80",
        "ephemeral" to "https://images.unsplash.com/photo-1522383225653-ed111181a951?w=600&q=80"
    )

    private val fallbackWordsData = mapOf(
        "apple" to Word("apple", "/ˈæp.əl/", listOf(Meaning("noun", listOf(Definition("A round fruit with red, yellow, or green skin and firm white flesh.", "She ate a ripe red apple for lunch."), Definition("An apple tree fruit.", "Apples grow in orchards."))))),
        "happy" to Word("happy", "/ˈhæp.i/", listOf(Meaning("adjective", listOf(Definition("Feeling or showing pleasure or contentment.", "They lived a happy life together."), Definition("Fortunate and convenient.", "By a happy coincidence, we met again."))))),
        "water" to Word("water", "/ˈwɔː.tər/", listOf(Meaning("noun", listOf(Definition("A clear, colorless liquid that forms the seas, lakes, and rain.", "Drink plenty of water every day."), Definition("A stretch or body of water.", "The boat drifted on calm water.")))), imageUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=600&q=80"),
        "music" to Word("music", "/ˈmjuː.zɪk/", listOf(Meaning("noun", listOf(Definition("Vocal or instrumental sounds combined in a harmonious way.", "She listened to her favorite music."))))),
        "smile" to Word("smile", "/smaɪl/", listOf(Meaning("verb", listOf(Definition("Form one's features into a pleased or amused expression.", "He smiled at his friend."))))),
        "house" to Word("house", "/haʊs/", listOf(Meaning("noun", listOf(Definition("A building for human habitation.", "They bought a new house near the park."))))),
        "sun" to Word("sun", "/sʌn/", listOf(Meaning("noun", listOf(Definition("The star around which the earth orbits.", "The sun shines brightly today."))))),
        "tree" to Word("tree", "/triː/", listOf(Meaning("noun", listOf(Definition("A woody perennial plant with a single trunk.", "The birds nested in the tall oak tree."))))),
        "book" to Word("book", "/bʊk/", listOf(Meaning("noun", listOf(Definition("A written or printed work consisting of pages.", "She read an inspiring book."))))),
        "friend" to Word("friend", "/frend/", listOf(Meaning("noun", listOf(Definition("A person with whom one has a bond of mutual affection.", "A loyal friend is a precious gift."))))),

        "courage" to Word("courage", "/ˈkʌr.ɪdʒ/", listOf(Meaning("noun", listOf(Definition("The ability to do something that frightens one; bravery.", "It takes courage to stand up for what is right."), Definition("Strength in the face of pain or grief.", "She faced illness with great courage."))))),
        "future" to Word("future", "/ˈfjuː.tʃər/", listOf(Meaning("noun", listOf(Definition("A period of time following the moment of speaking or writing.", "Work hard today for a better future."))))),
        "nature" to Word("nature", "/ˈneɪ.tʃər/", listOf(Meaning("noun", listOf(Definition("The physical world including plants, animals, and landscapes.", "We spent the weekend enjoying nature."))))),
        "wonder" to Word("wonder", "/ˈwʌn.dər/", listOf(Meaning("noun", listOf(Definition("A feeling of amazement and admiration, caused by something beautiful.", "The Grand Canyon is a natural wonder."))))),
        "wisdom" to Word("wisdom", "/ˈwɪz.dəm/", listOf(Meaning("noun", listOf(Definition("The quality of having experience, knowledge, and good judgment.", "He imparted words of wisdom to his pupils."))))),
        "silence" to Word("silence", "/ˈsaɪ.ləns/", listOf(Meaning("noun", listOf(Definition("Complete absence of sound.", "The room fell into a peaceful silence."))))),
        "journey" to Word("journey", "/ˈdʒɜː.ni/", listOf(Meaning("noun", listOf(Definition("An act of traveling from one place to another.", "Life is a meaningful journey."))))),
        "freedom" to Word("freedom", "/ˈfriː.dəm/", listOf(Meaning("noun", listOf(Definition("The power or right to act, speak, or think without hindrance.", "Education empowers people with freedom."))))),
        "balance" to Word("balance", "/ˈbæl.əns/", listOf(Meaning("noun", listOf(Definition("An even distribution of weight or emotional stability.", "Maintaining a good work-life balance is crucial."))))),
        "comfort" to Word("comfort", "/ˈkʌm.fət/", listOf(Meaning("noun", listOf(Definition("A state of physical ease and freedom from pain or constraint.", "Home offers warm comfort after a long day."))))),

        "eloquent" to Word("eloquent", "/ˈel.ə.kwənt/", listOf(Meaning("adjective", listOf(Definition("Fluent or persuasive in speaking or writing.", "The speaker gave an eloquent speech."), Definition("Clearly expressing or indicating something.", "His eyes expressed eloquent gratitude."))))),
        "resilience" to Word("resilience", "/rɪˈzɪl.jəns/", listOf(Meaning("noun", listOf(Definition("The capacity to withstand or recover quickly from difficulties.", "Her resilience helped her overcome every obstacle."))))),
        "pragmatic" to Word("pragmatic", "/præɡˈmæt.ɪk/", listOf(Meaning("adjective", listOf(Definition("Dealing with things sensibly and realistically in a practical way.", "We need a pragmatic approach to solve this problem."))))),
        "benevolent" to Word("benevolent", "/bəˈnev.əl.ənt/", listOf(Meaning("adjective", listOf(Definition("Well meaning and kindly; serving a charitable purpose.", "A benevolent volunteer donated books to the school."))))),
        "meticulous" to Word("meticulous", "/məˈtɪk.jə.ləs/", listOf(Meaning("adjective", listOf(Definition("Showing great attention to detail; very careful and precise.", "He paid meticulous attention to details."))))),
        "tenacity" to Word("tenacity", "/təˈnæs.ə.ti/", listOf(Meaning("noun", listOf(Definition("The quality or fact of being able to grip something firmly; determination.", "Her tenacity carried her through difficult times."))))),
        "nostalgia" to Word("nostalgia", "/nɒsˈtæl.dʒə/", listOf(Meaning("noun", listOf(Definition("A sentimental longing or affection for the past.", "Old photos filled her with warm nostalgia."))))),
        "empathy" to Word("empathy", "/ˈem.pə.θi/", listOf(Meaning("noun", listOf(Definition("The ability to understand and share the feelings of another.", "Empathy fosters deep human connection."))))),
        "serendipity" to Word("serendipity", "/ˌser.ənˈdɪp.ə.ti/", listOf(Meaning("noun", listOf(Definition("The occurrence of events by chance in a happy or beneficial way.", "Finding this vintage book was pure serendipity."))))),
        "ephemeral" to Word("ephemeral", "/ɪˈfem.ər.əl/", listOf(Meaning("adjective", listOf(Definition("Lasting for a very short time; fleeting.", "Cherry blossoms possess an ephemeral beauty."))))),
    )

    override suspend fun getRandomWord(level: String, fullDefinition: Boolean): Result<Word> {
        val levelKey = level.lowercase().trim()
        val nextWordStr = getNextWordForLevel(levelKey)
        val key = nextWordStr.lowercase().trim()

        val rawWord = try {
            val response = dictionaryApi.getWord(nextWordStr)
            val apiWord = response.firstOrNull()
            apiWord ?: getFallbackWordRaw(key)
        } catch (_: Exception) {
            getFallbackWordRaw(key)
        }

        // Attach curated image URL
        val wordWithImage = rawWord.copy(
            imageUrl = rawWord.imageUrl ?: wordImageMap[key]
        )

        // Filter word definition depth based on Premium tier status
        val finalWord = if (fullDefinition) {
            wordWithImage
        } else {
            filterWordForFreeTier(wordWithImage)
        }

        return Result.success(finalWord)
    }

    private fun getNextWordForLevel(levelKey: String): String {
        return when (levelKey) {
            "easy" -> {
                val index = Math.floorMod(easyIndex.getAndIncrement(), easyWords.size)
                easyWords[index]
            }
            "hard" -> {
                val index = Math.floorMod(hardIndex.getAndIncrement(), hardWords.size)
                hardWords[index]
            }
            else -> {
                val index = Math.floorMod(mediumIndex.getAndIncrement(), mediumWords.size)
                mediumWords[index]
            }
        }
    }

    /**
     * Filters word meanings for Free tier users:
     * Keeps only the 1st definition per part of speech and removes example sentences.
     */
    private fun filterWordForFreeTier(word: Word): Word {
        val filteredMeanings = word.meanings.map { meaning ->
            meaning.copy(
                definitions = meaning.definitions.take(1).map { def ->
                    def.copy(example = null)
                }
            )
        }
        return word.copy(meanings = filteredMeanings)
    }

    private fun getFallbackWordRaw(wordKey: String): Word {
        val key = wordKey.lowercase().trim()
        val fallback = fallbackWordsData[key]
            ?: Word(
                word = wordKey,
                phonetic = null,
                meanings = listOf(
                    Meaning(
                        partOfSpeech = "definition",
                        definitions = listOf(Definition("Definition for $wordKey.", "Example sentence for $wordKey."))
                    )
                )
            )
        return fallback.copy(imageUrl = wordImageMap[key])
    }
}
