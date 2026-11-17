package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CurriculumData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.ChatEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("المعلم الذكي", appName)
  }

  @Test
  fun `mahdist revolution curriculum is correctly configured`() {
    val mahdiya = CurriculumData.mahdistRevolutionLesson
    assertNotNull(mahdiya)
    assertTrue(mahdiya.title.contains("الثورة المهدية"))
    assertTrue(mahdiya.keyPoints.isNotEmpty())
    assertTrue(mahdiya.sampleQuestions.isNotEmpty())
    assertEquals(1, mahdiya.sampleQuestions[0].correctIndex)
  }

  @Test
  fun `subjects list contains core curriculum`() {
    val subjects = CurriculumData.subjects
    assertTrue(subjects.any { it.id == "history" })
    assertTrue(subjects.any { it.id == "arabic" })
    assertTrue(subjects.any { it.id == "math" })
  }

  @Test
  fun `room database stores and retrieves chat messages`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.chatDao()

    try {
      val message = ChatEntity(
        id = "test_1",
        text = "ما هي شروط التنازع في النحو؟",
        isUser = true,
        timestamp = System.currentTimeMillis(),
        tutorRoleName = com.example.model.TutorRole.DEEP_REASONING.titleAr,
        modelUsed = com.example.model.TutorRole.DEEP_REASONING.modelName
      )
      dao.insertMessage(message)

      val count = dao.getMessageCount()
      assertEquals(1, count)

      val list = dao.getAllMessages().first()
      assertEquals(1, list.size)
      assertEquals("test_1", list[0].id)
      assertEquals("ما هي شروط التنازع في النحو؟", list[0].text)
      assertEquals("gemini-4-carbon", list[0].modelUsed)

      dao.deleteMessageById("test_1")
      assertEquals(0, dao.getMessageCount())
    } finally {
      db.close()
    }
  }

  @Test
  fun `tutor roles map to requested gemini models with system instructions`() {
    assertEquals("gemini-4-hydrogen", com.example.model.TutorRole.GENERAL.modelName)
    assertEquals("gemini-4-carbon", com.example.model.TutorRole.DEEP_REASONING.modelName)
    assertEquals("gemini-4-argon", com.example.model.TutorRole.INTERACTIVE_COACH.modelName)
    assertEquals("gemini-3.1-flash-lite", com.example.model.TutorRole.FAST_REVISION.modelName)
    com.example.model.TutorRole.entries.forEach { role ->
      assertTrue(role.systemInstruction.isNotBlank())
    }
  }

  @Test
  fun `tutor viewModel can be instantiated by AndroidViewModelFactory`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
      .create(com.example.viewmodel.TutorViewModel::class.java)
    assertNotNull(vm)
    assertEquals(com.example.model.TutorRole.GENERAL, vm.selectedTutorRole.value)
  }

  @Test
  fun `room database stores updates pins and deletes study notes`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.studyNoteDao()

    try {
      val now = System.currentTimeMillis()
      val noteId = dao.insertNote(
        com.example.data.local.StudyNoteEntity(
          title = "قانون العجلة المركزية",
          content = "ت = ع² / نق",
          subjectTitle = "الفيزياء",
          isPinned = false,
          createdAt = now,
          updatedAt = now
        )
      )
      assertTrue(noteId > 0L)
      assertEquals(1, dao.getNotesCount())

      val notesAfterInsert = dao.getAllNotes().first()
      assertEquals(1, notesAfterInsert.size)
      assertEquals("قانون العجلة المركزية", notesAfterInsert[0].title)
      assertEquals("الفيزياء", notesAfterInsert[0].subjectTitle)

      dao.updatePinnedStatus(noteId, true, now + 10L)
      val notesAfterPin = dao.getAllNotes().first()
      assertTrue(notesAfterPin[0].isPinned)

      dao.updateNote(notesAfterPin[0].copy(content = "ت = ع² / نق = ω² × نق", updatedAt = now + 20L))
      val notesAfterUpdate = dao.getAllNotes().first()
      assertEquals("ت = ع² / نق = ω² × نق", notesAfterUpdate[0].content)

      dao.deleteNoteById(noteId)
      assertEquals(0, dao.getNotesCount())
    } finally {
      db.close()
    }
  }

  @Test
  fun `settings repository persists theme stream and tutor preferences`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.local.SettingsRepository(context)

    repo.setThemeMode(com.example.data.local.AppThemeMode.DARK)
    repo.setAcademicStream(com.example.data.local.AcademicStream.ARTS)
    repo.setDefaultTutorRole(com.example.model.TutorRole.DEEP_REASONING)
    repo.setSearchGroundingDefault(false)
    repo.setStudyRemindersEnabled(false)

    val saved = repo.settings.first()
    assertEquals(com.example.data.local.AppThemeMode.DARK, saved.themeMode)
    assertEquals(com.example.data.local.AcademicStream.ARTS, saved.academicStream)
    assertEquals(com.example.model.TutorRole.DEEP_REASONING, saved.defaultTutorRole)
    assertEquals(false, saved.searchGroundingDefault)
    assertEquals(false, saved.studyRemindersEnabled)
  }

  @Test
  fun `tutor viewModel exposes settings and null initial student without crashing`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
      .create(com.example.viewmodel.TutorViewModel::class.java)
    assertNotNull(vm.settings.value)
    assertEquals(false, vm.isSigningIn.value)
  }

  @Test
  fun `curriculum includes all science arts and common textbooks in pdf format`() {
    val books = CurriculumData.textbooks
    assertTrue(books.any { it.track == com.example.model.BookTrack.SCIENCE && it.pdfFileName.endsWith(".pdf") })
    assertTrue(books.any { it.track == com.example.model.BookTrack.ARTS && it.pdfFileName.endsWith(".pdf") })
    assertTrue(books.any { it.track == com.example.model.BookTrack.COMMON && it.pdfFileName.endsWith(".pdf") })
    assertTrue(books.size >= 15)
  }

  @Test
  fun `pdf textbook generator creates valid pdf file starting with PDF header`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val book = CurriculumData.textbooks.first()
    val units = CurriculumData.subjects
      .filter { it.id == book.subjectId || it.title == book.subjectTitle }
      .flatMap { it.units }

    val pdfFile = com.example.util.PdfTextbookGenerator.generateOrGetPdfFile(context, book, units)
    assertTrue(pdfFile.exists())
    assertTrue(pdfFile.length() > 512L)
    val headerBytes = pdfFile.inputStream().use { stream ->
      val buf = ByteArray(5)
      stream.read(buf)
      String(buf, Charsets.US_ASCII)
    }
    assertEquals("%PDF-", headerBytes)
  }

  @Test
  fun `settings repository persists pomodoro durations and stats`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.local.SettingsRepository(context)

    repo.setPomodoroDurations(focusMinutes = 45, shortBreakMinutes = 10, longBreakMinutes = 20)
    repo.setPomodoroSoundEnabled(false)
    repo.setPomodoroAutoStartNext(true)
    repo.resetPomodoroStats()
    repo.recordPomodoroFocusCompletion(45)

    val saved = repo.settings.first()
    assertEquals(45, saved.pomodoroFocusMinutes)
    assertEquals(10, saved.pomodoroShortBreakMinutes)
    assertEquals(20, saved.pomodoroLongBreakMinutes)
    assertEquals(false, saved.pomodoroSoundEnabled)
    assertEquals(true, saved.pomodoroAutoStartNext)
    assertEquals(1, saved.pomodoroCompletedSessions)
    assertEquals(45, saved.pomodoroTotalFocusMinutes)
  }

  @Test
  fun `pomodoro viewModel supports presets phase switching skip reset and subject linking`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
      .create(com.example.viewmodel.TutorViewModel::class.java)

    vm.applyPomodoroPreset(com.example.model.PomodoroPreset.DEEP_STUDY)
    assertEquals(45, vm.pomodoroState.value.focusMinutes)
    assertEquals(45 * 60, vm.pomodoroState.value.remainingSeconds)
    assertEquals("45:00", vm.pomodoroState.value.formattedTime)

    vm.setPomodoroSubject("الفيزياء")
    assertEquals("الفيزياء", vm.pomodoroState.value.selectedSubjectTitle)

    vm.addExtraPomodoroMinutes(5)
    assertEquals(50 * 60, vm.pomodoroState.value.remainingSeconds)

    vm.skipPomodoroPhase()
    assertEquals(com.example.model.PomodoroPhase.SHORT_BREAK, vm.pomodoroState.value.phase)
    assertEquals(10 * 60, vm.pomodoroState.value.remainingSeconds)

    vm.selectPomodoroPhase(com.example.model.PomodoroPhase.LONG_BREAK)
    assertEquals(com.example.model.PomodoroPhase.LONG_BREAK, vm.pomodoroState.value.phase)
    assertEquals(20 * 60, vm.pomodoroState.value.remainingSeconds)

    vm.applyPomodoroPreset(com.example.model.PomodoroPreset.CLASSIC)
    vm.selectPomodoroPhase(com.example.model.PomodoroPhase.FOCUS)
    vm.resetPomodoro()
    assertEquals("25:00", vm.pomodoroState.value.formattedTime)
  }

  @Test
  fun `room database stores and deletes completed pomodoro sessions`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.pomodoroSessionDao()

    try {
      val now = System.currentTimeMillis()
      val id1 = dao.insertSession(
        com.example.data.local.PomodoroSessionEntity(
          subjectTitle = "الرياضيات المتخصصة",
          durationMinutes = 45,
          goal = "التكامل المحدود",
          completedAt = now
        )
      )
      val id2 = dao.insertSession(
        com.example.data.local.PomodoroSessionEntity(
          subjectTitle = "الفيزياء",
          durationMinutes = 25,
          goal = "الموجات",
          completedAt = now - 60_000L
        )
      )
      assertTrue(id1 > 0L && id2 > 0L)
      assertEquals(2, dao.getSessionsCount())

      val all = dao.getAllSessions().first()
      assertEquals("الرياضيات المتخصصة", all[0].subjectTitle)
      assertEquals(45, all[0].durationMinutes)

      dao.deleteSessionById(id2)
      assertEquals(1, dao.getSessionsCount())

      dao.clearAllSessions()
      assertEquals(0, dao.getSessionsCount())
    } finally {
      db.close()
    }
  }

  @Test
  fun `study stats aggregator computes subject hours and weekly totals accurately`() {
    val now = System.currentTimeMillis()
    val logs = listOf(
      com.example.model.PomodoroSessionLog(1L, "الفيزياء", 60, "", now),
      com.example.model.PomodoroSessionLog(2L, "الفيزياء", 30, "", now - 3_600_000L),
      com.example.model.PomodoroSessionLog(3L, "الكيمياء", 30, "", now - (2L * 24L * 3_600_000L)),
      com.example.model.PomodoroSessionLog(4L, "التاريخ", 60, "", now - (15L * 24L * 3_600_000L))
    )

    val allStats = com.example.util.StudyStatsAggregator.aggregateBySubject(logs)
    assertEquals(3, allStats.size)
    assertEquals("الفيزياء", allStats[0].subjectTitle)
    assertEquals(90, allStats[0].totalMinutes)
    assertEquals(1.5f, allStats[0].totalHours, 0.001f)
    assertEquals(2, allStats[0].sessionsCount)
    assertEquals(0.5f, allStats[0].shareFraction, 0.001f)

    val last7 = com.example.util.StudyStatsAggregator.filterByTime(
      logs,
      com.example.model.StatsTimeFilter.LAST_7_DAYS,
      nowMillis = now
    )
    assertEquals(3, last7.size)

    val weeklyTrend = com.example.util.StudyStatsAggregator.aggregateLast7Days(logs, nowMillis = now)
    assertEquals(7, weeklyTrend.size)
    assertEquals(120, weeklyTrend.sumOf { it.totalMinutes })
  }

  @Test
  fun `ai quiz json parser extracts structured questions and option hints`() {
    val sampleJson = """
      {
        "quizTitle": "الفيزياء: الحركة الدائرية",
        "questions": [
          {
            "question": "جسم يتحرك بسرعة خطية ثابتة في مسار دائري، ما اتجاه عجلته المركزية؟",
            "options": [
              "باتجاه المماس",
              "نحو مركز المسار الدائري",
              "للخارج بعيداً عن المركز",
              "عكس اتجاه السرعة"
            ],
            "correctIndex": 1,
            "socraticHint": "تذكر كيف يتغير اتجاه متجه السرعة المماسية لحظياً أثناء الدوران.",
            "optionHints": [
              "التسارع المماسي يغيّر مقدار السرعة لا اتجاهها؛ راجع نوع الحركة هنا.",
              "إجابة موفقة! التغير في اتجاه السرعة يتجه دائماً نحو المركز.",
              "لو اتجه للخارج لابتعد الجسم عن المسار الدائري.",
              "إذا كان عكس السرعة لتباطأ الجسم على خط مستقيم."
            ],
            "explanation": "العجلة المركزية تنشأ عن تغير اتجاه السرعة وتتجه دائماً نحو المركز.",
            "teacherGuidance": "ارسم متجهي السرعة عند نقطتين قريبتين لتتضح لك جهة التغير."
          }
        ]
      }
    """.trimIndent()

    val parsed = com.example.util.AiQuizJsonParser.parse(sampleJson, "اختبار الفيزياء")
    assertEquals("الفيزياء: الحركة الدائرية", parsed.quizTitle)
    assertEquals(1, parsed.questions.size)
    val q = parsed.questions.first()
    assertEquals(1, q.correctIndex)
    assertEquals(4, q.options.size)
    assertEquals(4, q.optionHints.size)
    assertTrue(q.socraticHint.isNotBlank())
  }

  @Test
  fun `guided quiz provides socratic feedback on wrong attempt before allowing retry to correct answer`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
      .create(com.example.viewmodel.TutorViewModel::class.java)

    val testQuestion = com.example.model.QuizQuestion(
      id = "q_guided_1",
      question = "ما وحدة قياس القوة في النظام الدولي؟",
      options = listOf("الجول", "النيوتن", "الواط", "الباسكال"),
      correctIndex = 1,
      explanation = "النيوتن هو وحدة قياس القوة ويعادل كجم·م/ث².",
      teacherGuidance = "تذكر قانون نيوتن الثاني: ق = ك × ت.",
      socraticHint = "فكر في الكمية التي تساوي الكتلة مضروبة في العجلة.",
      optionHints = listOf(
        "الجول وحدة قياس الشغل والطاقة وليس القوة — ما الوحدة المنسوبة لمكتشف قوانين الحركة؟",
        "أحسنت! النيوتن هو وحدة القوة.",
        "الواط يقيس القدرة (الشغل على الزمن).",
        "الباسكال يقيس الضغط (القوة على المساحة)."
      )
    )

    vm.loadSubjectQuiz(listOf(testQuestion), "الفيزياء: الوحدات")
    // First attempt: pick wrong option (index 0: الجول)
    vm.selectQuizAnswer(0)
    vm.submitQuizAnswer()

    val afterWrong = vm.quizAttemptState.value
    assertEquals(false, afterWrong.isSolvedOrRevealed)
    assertTrue(afterWrong.attemptedWrongIndices.contains(0))
    assertEquals(null, vm.selectedAnswerIndex.value)
    assertTrue(afterWrong.activeGuidanceText.orEmpty().contains("الشغل والطاقة"))

    // Cannot re-select already tried wrong option
    vm.selectQuizAnswer(0)
    assertEquals(null, vm.selectedAnswerIndex.value)

    // Second attempt: select correct option (index 1: النيوتن)
    vm.selectQuizAnswer(1)
    vm.submitQuizAnswer()

    val afterCorrect = vm.quizAttemptState.value
    assertTrue(afterCorrect.isSolvedOrRevealed)
    assertTrue(afterCorrect.isGuidanceCorrect)
    assertEquals(0, afterCorrect.firstTryCorrectCount)
    assertEquals(1, afterCorrect.guidedCorrectCount)

    // Configurator validation for blank topic
    vm.openAiQuizConfigurator("الكيمياء", "")
    assertTrue(vm.isConfiguringAiQuiz.value)
    vm.generateAiQuiz()
    assertTrue(vm.quizGeneratorState.value is com.example.model.QuizGeneratorUiState.Error)
  }

  @Test
  fun `past exams are updated with 2026 exams new dates and socratic hints`() {
    val exams = CurriculumData.pastExams
    assertTrue(exams.size >= 8)
    assertTrue(exams.all { it.year.contains("2026") && it.session.contains("2026") })
    assertTrue(exams.none { it.year.contains("2021") || it.year.contains("2022") })
    exams.forEach { exam ->
      assertTrue(exam.sampleQuestions.isNotEmpty())
      exam.sampleQuestions.forEach { q ->
        assertTrue(q.socraticHint.isNotBlank())
        assertEquals(q.options.size, q.optionHints.size)
      }
    }
  }

  @Test
  fun `forum ai supervisor parser parses post review and reply evaluation safely`() {
    val postReviewJson = """
      {
        "reviewStatus": "GUIDED",
        "keyConcept": "الحركة الدائرية المنتظمة",
        "reviewNote": "تذكر أن اتجاه السرعة يتغير باستمرار نحو المركز مما ينتج تسارعاً مركزياً."
      }
    """.trimIndent()
    val parsedPost = com.example.util.ForumAiSupervisorParser.parsePostReview(postReviewJson)
    assertEquals(com.example.model.TeacherPostReviewStatus.GUIDED, parsedPost.reviewStatus)
    assertEquals("الحركة الدائرية المنتظمة", parsedPost.keyConcept)
    assertTrue(parsedPost.reviewNote.contains("الحركة الدائرية المنتظمة"))

    val replyJsonVerified = """
      {
        "badge": "VERIFIED",
        "feedback": "إجابة دقيقة ومنهجية، أحسنت في توضيح قانون العجلة المركزية."
      }
    """.trimIndent()
    val parsedReply = com.example.util.ForumAiSupervisorParser.parseReplyEvaluation(replyJsonVerified)
    assertEquals(com.example.model.TeacherReplyBadge.VERIFIED, parsedReply.badge)
    assertTrue(parsedReply.feedback.contains("أحسنت"))

    val replyJsonUnknownBadge = """
      {
        "badge": "UNKNOWN_BADGE",
        "feedback": "تأكد من وحدة القياس في الخطوة الأخيرة."
      }
    """.trimIndent()
    val parsedFallback = com.example.util.ForumAiSupervisorParser.parseReplyEvaluation(replyJsonUnknownBadge)
    assertEquals(com.example.model.TeacherReplyBadge.NEEDS_HINT, parsedFallback.badge)
  }

  @Test
  fun `forum models handle malformed wire enums gracefully without exceptions`() {
    val post = com.example.model.ForumPost(
      id = "p1",
      status = "INVALID_STATUS",
      teacherReviewStatus = "BOGUS"
    )
    assertEquals(com.example.model.ForumPostStatus.OPEN, post.parsedStatus)
    assertEquals(com.example.model.TeacherPostReviewStatus.PENDING, post.parsedTeacherReviewStatus)

    val reply = com.example.model.ForumReply(
      id = "r1",
      teacherBadge = "WEIRD_VALUE"
    )
    assertEquals(com.example.model.TeacherReplyBadge.NONE, reply.parsedTeacherBadge)
  }

  @Test
  fun `tutor viewModel toggleNightMode switches and persists dark and light modes`() = runBlocking {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val repo = com.example.data.local.SettingsRepository(app)
    repo.setThemeMode(com.example.data.local.AppThemeMode.LIGHT)

    val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
      .create(com.example.viewmodel.TutorViewModel::class.java)

    vm.toggleNightMode(currentlyDark = false)
    kotlinx.coroutines.delay(150L)
    assertEquals(com.example.data.local.AppThemeMode.DARK, repo.settings.first().themeMode)

    vm.toggleNightMode(currentlyDark = true)
    kotlinx.coroutines.delay(150L)
    assertEquals(com.example.data.local.AppThemeMode.LIGHT, repo.settings.first().themeMode)
    assertEquals("الوضع الليلي", com.example.data.local.AppThemeMode.DARK.titleAr)
  }

  @Test
  fun `curriculum exam tips cover exam anxiety and science study strategies with action steps`() {
    val tips = CurriculumData.examTips
    assertTrue(tips.size >= 8)
    assertTrue(tips.any { it.category == com.example.model.TeacherTipCategory.EXAM_ANXIETY.titleAr })
    assertTrue(tips.any { it.category == com.example.model.TeacherTipCategory.SCIENCE_STRATEGIES.titleAr })
    assertTrue(tips.any { it.category == com.example.model.TeacherTipCategory.LITERARY_STRATEGIES.titleAr })
    tips.forEach { tip ->
      assertTrue(tip.title.isNotBlank())
      assertTrue(tip.importantRule.isNotBlank())
      assertTrue(tip.actionSteps.isNotEmpty())
    }
  }

  @Test
  fun `teacher tips json parser parses renewable ai tips safely and normalizes categories`() {
    val rawJson = """
      ```json
      {
        "tips": [
          {
            "title": "طريقة الفهم العميق لقوانين الحركة التوافقية",
            "category": "المواد العلمية",
            "content": "ارسم العلاقة بين الإزاحة والزمن واكتب وحدات القياس قبل حل أي مسألة في الفيزياء.",
            "importantRule": "التحويل للنظام الدولي خطوة إلزامية قبل التعويض.",
            "actionSteps": [
              "اكتب المعطيات في الهامش مع الوحدات.",
              "تأكد من اتجاه التسارع والإزاحة."
            ],
            "streamTag": "القسم العلمي"
          },
          {
            "title": "تهدئة التوتر قبل دخول قاعة الامتحان",
            "category": "الدعم النفسي والقلق",
            "content": "تجنب النقاشات السريعة مع الزملاء وطبّق التنفس العميق لاستعادة تركيزك.",
            "importantRule": "الهدوء الذهني يضاعف سرعة استرجاع المعلومات.",
            "actionSteps": [
              "تنفس ببطء 3 مرات."
            ]
          }
        ]
      }
      ```
    """.trimIndent()

    val parsed = com.example.util.TeacherTipsJsonParser.parse(rawJson)
    assertEquals(2, parsed.size)
    assertEquals(com.example.model.TeacherTipCategory.SCIENCE_STRATEGIES.titleAr, parsed[0].category)
    assertTrue(parsed[0].isAiGenerated)
    assertEquals(2, parsed[0].actionSteps.size)
    assertEquals(com.example.model.TeacherTipCategory.EXAM_ANXIETY.titleAr, parsed[1].category)
  }
}
