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
    assertEquals("SudX", appName)
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
}



