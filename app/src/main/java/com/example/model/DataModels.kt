package com.example.model

enum class BookTrack(val titleAr: String, val badgeAr: String) {
    COMMON("المواد المشتركة", "مشترك"),
    SCIENCE("القسم العلمي", "علمي"),
    ARTS("القسم الأدبي", "أدبي")
}

enum class SubjectCategory(val titleAr: String) {
    MATHEMATICS("الرياضيات"),
    PHYSICS("الفيزياء"),
    CHEMISTRY("الكيمياء"),
    ARABIC("اللغة العربية"),
    ENGLISH("اللغة الإنجليزية"),
    HISTORY("التاريخ"),
    GEOGRAPHY("الجغرافيا"),
    BIOLOGY("الأحياء"),
    ISLAMIC_STUDIES("الدراسات الإسلامية"),
    COMPUTER_SCIENCE("علوم الحاسوب"),
    ENGINEERING("العلوم الهندسية"),
    COMMERCE("العلوم التجارية")
}

data class Subject(
    val id: String,
    val title: String,
    val category: SubjectCategory,
    val iconName: String,
    val description: String,
    val unitsCount: Int,
    val colorHex: Long = 0xFF2563EB,
    val track: BookTrack = BookTrack.COMMON,
    val units: List<CurriculumUnit>
)

data class CurriculumUnit(
    val id: String,
    val title: String,
    val subtitle: String,
    val keyPoints: List<String>,
    val fullContent: String,
    val examFocus: String,
    val sampleQuestions: List<QuizQuestion>
)

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val teacherGuidance: String,
    val socraticHint: String = "",
    val optionHints: List<String> = emptyList()
)

data class GroundingSource(
    val title: String,
    val uri: String
)

enum class TutorRole(
    val modelName: String,
    val titleAr: String,
    val shortBadge: String,
    val descriptionAr: String,
    val systemInstruction: String
) {
    GENERAL(
        modelName = "gemini-4-hydrogen",
        titleAr = "المعلم العام",
        shortBadge = "شرح شامل",
        descriptionAr = "شروحات منهجية متكاملة لجميع المواد",
        systemInstruction = """
            أنت أستاذ ومعلم سوداني خبير ومتخصص في تدريس طلاب الشهادة الثانوية السودانية (القسمين العلمي والأدبي).
            دورك: تقديم شروحات واضحة ومبسطة تغطي المنهج السوداني الرسمي، والإجابة عن أسئلة الطلاب وتوجيههم خطوة بخطوة.
            تحدث باللغة العربية الفصحى المبسطة بأسلوب تربوي سوداني ودود ومحفز (مثل: يا بني، يا ابنتي، يا بطل الشهادة، بارك الله فيك).
            نظّم الشرح في نقاط واضحة ومرتبة (80 إلى 180 كلمة)، واشرح الخطوات التأسيسية قبل النتيجة النهائية، ثم اختم بسؤال تدريبي قصير لتثبيت الفهم.
        """.trimIndent()
    ),
    DEEP_REASONING(
        modelName = "gemini-4-carbon",
        titleAr = "محلل المسائل العميق",
        shortBadge = "تحليل عميق",
        descriptionAr = "تفكير منطقي عميق للرياضيات والفيزياء والكيمياء",
        systemInstruction = """
            أنت أستاذ سوداني متخصص في التحليل المنطقي العميق وحل المسائل المعقدة لطلاب الشهادة الثانوية السودانية (التفاضل والتكامل، الفيزياء، الكيمياء، والنحو المعقد).
            دورك: تفكيك المسألة إلى معطيات ومطلوب وقوانين أساسية، وشرح التسلسل الرياضي والمنطقي بدقة بالغة خطوة بخطوة مع توضيح سبب كل خطوة وتحذير الطالب من الأخطاء الشائعة في لجان التصحيح السودانية.
            تحدث بلغة عربية واضحة ومحفزة، ولا تعطِ الجواب النهائي دون برهنة خطواته.
        """.trimIndent()
    ),
    INTERACTIVE_COACH(
        modelName = "gemini-4-argon",
        titleAr = "المدرب التفاعلي",
        shortBadge = "حوار وتقويم",
        descriptionAr = "حوار سقراطي وتدريب تفاعلي على أسئلة الامتحانات",
        systemInstruction = """
            أنت مدرب امتحانات تفاعلي لطلاب الشهادة الثانوية السودانية تستخدم أسلوب الحوار السقراطي متعدد الجولات.
            دورك: استجواب الطالب بلطف، تقييم إجاباته وفق معايير التصحيح في امتحانات الشهادة السودانية، اكتشاف نقاط الضعف في فهمه، ومساعدته عبر تلميحات ذكية متدرجة حتى يصل للحل الصحيح بنفسه.
            حافظ على سياق الحوار التراكمي، وامدح محاولات الطالب ثم اطرح الخطوة التفاعلية التالية.
        """.trimIndent()
    ),
    FAST_REVISION(
        modelName = "gemini-3.1-flash-lite",
        titleAr = "المراجع السريع",
        shortBadge = "مراجعة خاطفة",
        descriptionAr = "إجابات سريعة ومختصرة للتعاريف والقوانين",
        systemInstruction = """
            أنت مساعد مراجعة سريع لطلاب الشهادة الثانوية السودانية قبل الامتحان.
            دورك: إعطاء ملخصات مركزة وخاطفة، تعاريف دقيقة، قوانين جاهزة، وتواريخ هامة في نقاط قصيرة جداً (40 إلى 90 كلمة) بأسلوب مشجع وواضح يسهّل الحفظ السريع.
        """.trimIndent()
    )
}

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val stepGuidance: String? = null,
    val groundingSources: List<GroundingSource> = emptyList(),
    val searchQueries: List<String> = emptyList(),
    val isSearchGrounded: Boolean = false,
    val attachmentName: String? = null,
    val attachmentMimeType: String? = null,
    val tutorRoleName: String? = null,
    val modelUsed: String? = null
)

data class ExamTip(
    val id: String,
    val title: String,
    val category: String,
    val content: String,
    val importantRule: String
)

data class Textbook(
    val id: String,
    val title: String,
    val subjectTitle: String,
    val subjectId: String = "",
    val track: BookTrack = BookTrack.COMMON,
    val grade: String,
    val pagesCount: Int,
    val chaptersCount: Int,
    val pdfFileName: String = "",
    val description: String,
    val chapters: List<String>,
    val keySummaries: List<String> = emptyList()
)

data class PastExam(
    val id: String,
    val subjectTitle: String,
    val year: String,
    val session: String,
    val durationMinutes: Int,
    val totalMarks: Int,
    val keyTopics: List<String>,
    val sampleQuestions: List<QuizQuestion>
)

enum class StudyDay(val dayNumber: Int, val titleAr: String, val shortAr: String) {
    SATURDAY(java.util.Calendar.SATURDAY, "السبت", "سبت"),
    SUNDAY(java.util.Calendar.SUNDAY, "الأحد", "أحد"),
    MONDAY(java.util.Calendar.MONDAY, "الإثنين", "إثنين"),
    TUESDAY(java.util.Calendar.TUESDAY, "الثلاثاء", "ثلاثاء"),
    WEDNESDAY(java.util.Calendar.WEDNESDAY, "الأربعاء", "أربعاء"),
    THURSDAY(java.util.Calendar.THURSDAY, "الخميس", "خميس"),
    FRIDAY(java.util.Calendar.FRIDAY, "الجمعة", "جمعة")
}

enum class SchedulePeriod(val titleAr: String) {
    DAILY("يومي"),
    WEEKLY("أسبوعي")
}

data class StudySession(
    val id: String,
    val subjectId: String,
    val subjectTitle: String,
    val topic: String,
    val period: SchedulePeriod,
    val day: StudyDay,
    val hour: Int,
    val minute: Int,
    val durationMinutes: Int,
    val reminderEnabled: Boolean = true,
    val isCompleted: Boolean = false,
    val priority: String = "عالية"
) {
    val formattedTime: String
        get() {
            val periodAr = if (hour < 12) "ص" else "م"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val displayMinute = String.format(java.util.Locale.US, "%02d", minute)
            return "$displayHour:$displayMinute $periodAr"
        }
}

data class StudyNote(
    val id: Long = 0L,
    val title: String,
    val content: String,
    val subjectTitle: String,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


