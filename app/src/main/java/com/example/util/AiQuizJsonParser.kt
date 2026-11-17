package com.example.util

import com.example.model.QuizQuestion
import org.json.JSONObject
import java.util.UUID

data class ParsedAiQuiz(
    val quizTitle: String,
    val questions: List<QuizQuestion>
)

object AiQuizJsonParser {

    fun parse(rawJson: String, fallbackTitle: String): ParsedAiQuiz {
        val root = JSONObject(rawJson)
        val quizTitle = root.optString("quizTitle").trim().ifBlank { fallbackTitle }
        val questionsJson = root.getJSONArray("questions")
        val parsedQuestions = mutableListOf<QuizQuestion>()

        for (i in 0 until questionsJson.length()) {
            val item = questionsJson.getJSONObject(i)
            val questionText = item.optString("question").trim()
            val optionsArray = item.optJSONArray("options") ?: continue
            val options = mutableListOf<String>()
            for (optIdx in 0 until optionsArray.length()) {
                val opt = optionsArray.optString(optIdx).trim()
                if (opt.isNotEmpty()) {
                    options.add(opt)
                }
            }
            if (questionText.isBlank() || options.size < 2) continue

            val rawCorrectIndex = item.optInt("correctIndex", 0)
            val safeCorrectIndex = rawCorrectIndex.coerceIn(0, options.lastIndex)

            val hintsArray = item.optJSONArray("optionHints")
            val optionHints = if (hintsArray != null) {
                (0 until options.size).map { idx ->
                    hintsArray.optString(idx).trim()
                }
            } else {
                emptyList()
            }

            val socraticHint = item.optString("socraticHint").trim()
            val explanation = item.optString("explanation").trim()
            val teacherGuidance = item.optString("teacherGuidance").trim()

            parsedQuestions.add(
                QuizQuestion(
                    id = "ai_${UUID.randomUUID()}",
                    question = questionText,
                    options = options,
                    correctIndex = safeCorrectIndex,
                    explanation = explanation,
                    teacherGuidance = teacherGuidance.ifBlank {
                        "تأكد دائماً من فهم المبدأ الأساسي للسؤال قبل اختيار البديل النهائي."
                    },
                    socraticHint = socraticHint,
                    optionHints = optionHints
                )
            )
        }

        require(parsedQuestions.isNotEmpty()) {
            "لم يتمكن النموذج من توليد أسئلة صالحة لهذا الموضوع. يرجى تعديل صياغة الموضوع والمحاولة مرة أخرى."
        }

        return ParsedAiQuiz(
            quizTitle = quizTitle,
            questions = parsedQuestions
        )
    }
}
