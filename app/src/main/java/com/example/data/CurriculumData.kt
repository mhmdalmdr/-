package com.example.data

import com.example.model.CurriculumUnit
import com.example.model.ExamTip
import com.example.model.PastExam
import com.example.model.QuizQuestion
import com.example.model.Subject
import com.example.model.SubjectCategory
import com.example.model.Textbook

object CurriculumData {

    val mahdistRevolutionLesson = CurriculumUnit(
        id = "history_mahdiya_causes",
        title = "أسباب الثورة المهدية (1881 - 1898م)",
        subtitle = "مقرر مادة التاريخ - الشهادة الثانوية السودانية",
        keyPoints = listOf(
            "الأسباب الدينية: الدعوة لإحياء السنة وإماتة البدع، ومحاربة الانحلال الأخلاقي، وانتشار فكرة المهدي المنتظر لملء الأرض عدلاً.",
            "الأسباب السياسية والإدارية: مساوئ الحكم التركي المصري (التركية السابقة)، استبداد الحكام الأجانب وتسلط الموظفين، وسلب حريات المواطنين.",
            "الأسباب الاقتصادية: فداحة الضرائب وتعدد مسمياتها وجبايتها بالقسوة والضرب، ومصادرة أموال الأهالي وتدهور التجارة.",
            "الأسباب الاجتماعية: امتهان كرامة السودانيين، تفكيك الزعامات القبلية، وإلغاء تجارة الرقيق بصورة مفاجئة دون بدائل اقتصادية.",
            "السبب المباشر: واقعة الجزيرة أبا (رمضان 1298هـ / أغسطس 1881م) وتصدي الأنصار لحملة أبي السعود بالأسلحة البيضاء."
        ),
        fullContent = """
مرحباً بك يا بني/ابنتي في درس التاريخ الهام جداً لامتحان الشهادة السودانية!

تعتبر الثورة المهدية (1881م) نقطة تحول كبرى في تاريخ السودان الحديث، ويمكن تلخيص أسباب قيامها في أربعة محاور رئيسية، إضافة للسبب المباشر:

1. الأسباب الدينية والروحية:
- فساد الجهاز الإداري والقضائي وابتعاده عن روح الشريعة الإسلامية.
- سخط العلماء والزهاد على مظاهر الترف والانحلال الأخلاقي.
- الرغبة الصادقة لدى الإمام محمد أحمد المهدي والشعب في تجديد الدين وإحياء السنة النبوية وإماتة البدع.
- انتشار عقيدة انتظار "المهدي المنتظر" ليملأ الأرض عدلاً بعد أن مُلئت جوراً.

2. الأسباب السياسية والإدارية:
- الاستبداد المطلق وسلب إرادة السودانيين في عهد التركية السابقة.
- إسناد المناصب الكبرى لموظفين وحكام أجانب وأوروبيين (مثل غوردون وسلاطين) مما أثار حفيظة الأهالي وزعماء العشائر.
- ضعف هيبة الإدارة الحاكمة وعجزها عن بسط العدل.

3. الأسباب الاقتصادية (المحرك الأكبر للغضب الشعبي):
- فداحة الضرائب الباهظة المفروضة على الأراضي، النخيل، السواقي، والمواشي.
- القسوة المفرطة في جباية الضرائب واستخدام السوط (الكرباج) ومصادرة الممتلكات.
- احتكار الحكومة لسلع رئيسية كالصمغ والعاج وتدهور حركة التجارة.

4. الأسباب الاجتماعية:
- المعاملة الاستعلائية من الحكام للمواطنين وامتهان كرامتهم.
- تحطيم النفوذ التقليدي لزعماء القبائل والمشايخ.
- قيام الحكومة بمكافحة تجارة الرقيق بأسلوب فجائي وبمساعدة ضباط أوروبيين دون إيجاد بدائل اقتصادية.

5. السبب المباشر (الشرارة):
- إعلان الإمام محمد أحمد المهدي دعوته في الجزيرة أبا في 29 يونيو 1881م.
- إرسال حاكم عام السودان (رؤوف باشا) حملة عسكرية بقيادة أبي السعود في أغسطس 1881م لاعتقاله، فتصدى لها الأنصار في الجزيرة أبا بالأسلحة البيضاء وهزموها، معلنين اندلاع الثورة رسمياً.
        """.trimIndent(),
        examFocus = "نصيحة أستاذك للامتحان: يكرر واضع الامتحان سؤال 'علل: تضافرت عوامل متعددة لقيام الثورة المهدية؟' احرص دائماً على تقسيم إجابتك إلى 4 نقاط واضحة (دينية، سياسية، اقتصادية، اجتماعية)، ثم اذكر حادثة الجزيرة أبا كالسبب المباشر لتنال الدرجة الكاملة.",
        sampleQuestions = listOf(
            QuizQuestion(
                id = "mahdi_q1",
                question = "ما هو السبب المباشر لاندلاع الثورة المهدية عام 1881م؟",
                options = listOf(
                    "سقوط الخرطوم ومقتل غوردون",
                    "واقعة الجزيرة أبا وهزيمة حملة أبي السعود",
                    "معركة شيكان التاريخية",
                    "معركة قدير الأولى"
                ),
                correctIndex = 1,
                explanation = "السبب المباشر هو إرسال رؤوف باشا حملة أبي السعود للجزيرة أبا لاعتقال المهدي وتصدي الأنصار لها في أغسطس 1881م.",
                teacherGuidance = "أحسنت يا بطل! لاحظ دائماً الفرق بين الأسباب التراكمية العامة وبين 'السبب المباشر' الذي أطلق شرارة الميدان."
            ),
            QuizQuestion(
                id = "mahdi_q2",
                question = "من الأسباب الاقتصادية الرئيسية التي ألهبت سخط السودانيين في العهد التركي المصري:",
                options = listOf(
                    "بناء السكك الحديدية",
                    "فداحة الضرائب وتعددها وقسوة جبايتها بالكرباج",
                    "تأسيس كلية غوردون",
                    "ازدهار الزراعة المروية"
                ),
                correctIndex = 1,
                explanation = "عانت جميع الفئات السودانية من فرض ضرائب مرهقة على النخيل والسواقي والمواشي واستخدام الضرب في تحصيلها.",
                teacherGuidance = "دائماً تذكر أن الجانب الاقتصادي ارتبط بـ (الضرائب الباهظة وأساليب الجباية العنيفة)."
            )
        )
    )

    // All Science, Arts & Common Subjects for the Sudanese Secondary Certificate
    val subjects: List<Subject> = listOf(
        Subject(
            id = "math",
            title = "الرياضيات المتخصصة",
            category = SubjectCategory.MATHEMATICS,
            iconName = "calculate",
            description = "التفاضل وتطبيقاته، حساب التكامل، المصفوفات والمحددات، الإحصاء، والهندسة التحليلية.",
            unitsCount = 2,
            colorHex = 0xFF2563EB,
            track = com.example.model.BookTrack.SCIENCE,
            units = listOf(
                CurriculumUnit(
                    id = "math_diff",
                    title = "التفاضل وتطبيقاته (المشتقة الأولى والقيم القصوى)",
                    subtitle = "الرياضيات المتخصصة والعلمية",
                    keyPoints = listOf(
                        "قواعد الاشتقاق الأساسية: مشتقة القوة، حاصل ضرب دالتين، وخارج قسمة دالتين.",
                        "قاعدة السلسلة ومشتقات الدوال المثلثية.",
                        "تطبيقات المشتقة: ميل المماس، معادلة المماس والعمودي عليه.",
                        "النقاط الحرجة ونقاط الانقلاب وفترات التزايد والتناقص."
                    ),
                    fullContent = "التفاضل هو الركيزة الأساسية لورقة الرياضيات. ركز على كتابة القانون أولاً ثم التعويض الرياضي الدقيق لتضمن درجات الخطوات في امتحان الشهادة.",
                    examFocus = "نصيحة الامتحان: وزع خطوات الحل بوضوح، وضع الناتج النهائي داخل إطار مستطيل لسهولة التدقيق والتصحيح.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "math_diff_q1",
                            question = "إذا كانت ص = س³ - 6س² + 9س + 1، فما قيمة س عند النقطة الحرجة التي يكون عندها قيمة عظمى محلية؟",
                            options = listOf(
                                "س = 3",
                                "س = 1",
                                "س = 2",
                                "س = 0"
                            ),
                            correctIndex = 1,
                            explanation = "المشتقة الأولى دص/دس = 3س² - 12س + 9 = 3(س - 1)(س - 3) = 0، والمشتقة الثانية د²ص/دس² = 6س - 12. عند س = 1 تكون سالبة (-6 < 0) فهي عظمى محلية.",
                            teacherGuidance = "تذكر دائماً في امتحان الشهادة اختبار إشارة المشتقة الثانية: سالبة تعني عظمى محلية، وموجبة تعني صغرى محلية."
                        ),
                        QuizQuestion(
                            id = "math_diff_q2",
                            question = "ما ميل المماس للمنحنى ص = جا(2س) عند النقطة التي إحداثيها السيني س = 0؟",
                            options = listOf(
                                "0",
                                "1",
                                "2",
                                "-2"
                            ),
                            correctIndex = 2,
                            explanation = "دص/دس = 2 جتا(2س)، وبالتعويض س = 0 نحصل على 2 جتا(0) = 2 × 1 = 2.",
                            teacherGuidance = "لا تنسَ استخدام قاعدة السلسلة عند اشتقاق الدوال المثلثية بضرب الناتج في مشتقة الزاوية!"
                        )
                    )
                ),
                CurriculumUnit(
                    id = "math_integration",
                    title = "حساب التكامل وتطبيقات المساحات",
                    subtitle = "الرياضيات المتخصصة",
                    keyPoints = listOf(
                        "التكامل غير المحدود وقواعد التكامل الأساسية.",
                        "التكامل بالتعويض والتكامل بالتجزيء.",
                        "التكامل المحدود وحساب المساحات تحت المنحنيات."
                    ),
                    fullContent = "التكامل هو العملية العكسية للتفاضل. تأكد دائماً من إضافة ثابت التكامل (ث) في التكامل غير المحدود.",
                    examFocus = "سؤال حساب المساحة المحصورة بين منحنى ومحور السينات يتكرر سنوياً في الورقة الأولى.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "math_int_q1",
                            question = "أوجد قيمة التكامل المحدود ∫ من 0 إلى 2 للدالة (3س² + 2) دس:",
                            options = listOf(
                                "10",
                                "12",
                                "8",
                                "14"
                            ),
                            correctIndex = 1,
                            explanation = "التكامل هو [س³ + 2س] من 0 إلى 2 = (8 + 4) - (0 + 0) = 12.",
                            teacherGuidance = "ممتاز! احسب الحد العلوي أولاً ثم اطرح منه قيمة التعويض بالحد السفلي بدقة."
                        ),
                        QuizQuestion(
                            id = "math_int_q2",
                            question = "ما ناتج التكامل غير المحدود ∫ جتا(3س) دس؟",
                            options = listOf(
                                "(1/3) جا(3س) + ث",
                                "3 جا(3س) + ث",
                                "-(1/3) جا(3س) + ث",
                                "جا(3س) + ث"
                            ),
                            correctIndex = 0,
                            explanation = "تكامل جتا(أس) = (1/أ) جا(أس) + ث، ولا تنسَ ثابت التكامل (ث).",
                            teacherGuidance = "احذر من نسيان ثابت التكامل (ث) في التكامل غير المحدود؛ المصحح يخصم عليه نصف درجة."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "physics",
            title = "الفيزياء",
            category = SubjectCategory.PHYSICS,
            iconName = "speed",
            description = "الحركة الدائرية والتوافقية، الكهربية والمغناطيسية، الديناميكا الحرارية، والفيزياء الذرية.",
            unitsCount = 1,
            colorHex = 0xFF0284C7,
            track = com.example.model.BookTrack.SCIENCE,
            units = listOf(
                CurriculumUnit(
                    id = "physics_circular",
                    title = "الحركة الدائرية المنتظمة وقوانينها",
                    subtitle = "الميكانيكا الكلاسيكية للشهادة الثانوية",
                    keyPoints = listOf(
                        "السرعة الزاوية (ω) والسرعة الخطية (v): v = ω × r.",
                        "العجلة المركزية (ac): ac = v² / r = ω² × r.",
                        "القوة الجاذبة المركزية (Fc): Fc = m × v² / r.",
                        "تطبيقات عملية: حركة الأقمار الصناعية وانعطاف السيارات."
                    ),
                    fullContent = "في الحركة الدائرية المنتظمة، يكون مقدار السرعة ثابتاً ولكن اتجاهها يتغير باستمرار، مما ينشئ عجلة مركزية تتجه دائماً نحو المركز.",
                    examFocus = "ركز على استنتاج العلاقة الرياضية للعجلة المركزية وتعريف الراديان في ورقة الامتحان.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "phys_circ_q1",
                            question = "جسم يتحرك في مسار دائري نصف قطره 2 متر بسرعة خطية ثابتة 6 م/ث، فما مقدار عجلته المركزية؟",
                            options = listOf(
                                "3 م/ث²",
                                "12 م/ث²",
                                "18 م/ث²",
                                "36 م/ث²"
                            ),
                            correctIndex = 2,
                            explanation = "العجلة المركزية ت = ع² / نق = 6² / 2 = 36 / 2 = 18 م/ث².",
                            teacherGuidance = "تذكر دائماً كتابة القانون (ت = ع² / نق) ثم التعويض ثم وحدة القياس (م/ث²) لضمان الدرجة الكاملة."
                        ),
                        QuizQuestion(
                            id = "phys_circ_q2",
                            question = "ما هو اتجاه القوة الجاذبة المركزية المؤثرة على جسم يتحرك حركة دائرية منتظمة؟",
                            options = listOf(
                                "في اتجاه المماس للمسار الدائري",
                                "نحو مركز المسار الدائري عمودياً على اتجاه السرعة",
                                "للخارج بعيداً عن المركز",
                                "في عكس اتجاه الحركة المماسية"
                            ),
                            correctIndex = 1,
                            explanation = "القوة الجاذبة المركزية والعجلة المركزية تتجهان دائماً نحو مركز الدائرة وتكونان عموديتين على متجه السرعة المماسية.",
                            teacherGuidance = "لذلك لا تبذل القوة الجاذبة المركزية شغلاً ميكانيكياً لأنها عمودية على الإزاحة اللحظية."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "chemistry",
            title = "الكيمياء",
            category = SubjectCategory.CHEMISTRY,
            iconName = "science",
            description = "الكيمياء العضوية، الاتزان الكيميائي، التحليل الحجمي (المعايرة)، الأحماض والقواعد.",
            unitsCount = 1,
            colorHex = 0xFF7C3AED,
            track = com.example.model.BookTrack.SCIENCE,
            units = listOf(
                CurriculumUnit(
                    id = "chem_equilibrium",
                    title = "الاتزان الكيميائي ومبدأ لوشاتيليه",
                    subtitle = "الكيمياء الفيزيائية للشهادة الثانوية",
                    keyPoints = listOf(
                        "الاتزان الديناميكي: تساوي سرعتي التفاعل الأمامي والعكسي وثبوت التراكيز.",
                        "مبدأ لوشاتيليه: النظام ينشط في الاتجاه الذي يقلل من أثر المؤثر الخارجي.",
                        "تأثير التركيز، الضغط، ودرجة الحرارة على موضع الاتزان وقيمة ثابت الاتزان Kc."
                    ),
                    fullContent = "مبدأ لوشاتيليه سؤال مضمون في كل امتحان شهادة سودانية! تذكر أن العامل الحفاز لا يغير من موضع الاتزان أو قيمة Kc.",
                    examFocus = "دائماً حدد هل التفاعل طارد أم ماص للحرارة قبل الإجابة عن أثر تغير درجة الحرارة.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "chem_eq_q1",
                            question = "في التفاعل المتزن الطارد للحرارة: N2(g) + 3H2(g) ⇌ 2NH3(g) + حرارة، ماذا يحدث لقيمة ثابت الاتزان Kc عند رفع درجة الحرارة؟",
                            options = listOf(
                                "تزداد قيمة Kc",
                                "تقل قيمة Kc",
                                "تبقى ثابتة لا تتغير",
                                "تتضاعف قيمة Kc"
                            ),
                            correctIndex = 1,
                            explanation = "بما أن التفاعل طارد للحرارة، فإن رفع درجة الحرارة يدفع الاتزان نحو اليسار (التفاعل العكسي الماص)، مما يقلل تركيز النواتج فتقل قيمة Kc.",
                            teacherGuidance = "احفظ هذه القاعدة الذهبية: العامل الوحيد الذي يغير قيمة ثابت الاتزان Kc هو درجة الحرارة فقط!"
                        ),
                        QuizQuestion(
                            id = "chem_eq_q2",
                            question = "ما أثر إضافة عامل حفاز مناسب إلى نظام كيميائي في حالة اتزان ديناميكي؟",
                            options = listOf(
                                "يزيد كمية النواتج عند الاتزان",
                                "يزيد سرعة التفاعل الأمامي فقط",
                                "يسرّع الوصول للاتزان دون تغيير موضع الاتزان أو Kc",
                                "يدفع التفاعل نحو المتفاعلات"
                            ),
                            correctIndex = 2,
                            explanation = "العامل الحفاز يزيد سرعتي التفاعل الأمامي والعكسي بنفس المقدار، فيقلل زمن الوصول للاتزان دون تغيير التراكيز النهائية.",
                            teacherGuidance = "سؤال متكرر جداً في اختيار من متعدد؛ انتبه لعدم الخلط بين السرعة وموضع الاتزان."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "arabic",
            title = "اللغة العربية",
            category = SubjectCategory.ARABIC,
            iconName = "menu_book",
            description = "القواعد النحوية، الصرف، البلاغة (البيان والبديع)، الأدب السوداني والعربي، والنصوص.",
            unitsCount = 1,
            colorHex = 0xFF059669,
            track = com.example.model.BookTrack.COMMON,
            units = listOf(
                CurriculumUnit(
                    id = "arabic_exceptions",
                    title = "أسلوب الاستثناء (إلا، غير، سوى، خلا، عدا، حاشا)",
                    subtitle = "النحو والصرف للشهادة الثانوية",
                    keyPoints = listOf(
                        "التام المثبت: يجب نصب المستثنى (حضر الطلاب إلا خالداً).",
                        "التام المنفي: يجوز النصب على الاستثناء أو الإتباع على البدلية.",
                        "الناقص المنفي: يعرب حسب موقعه في الجملة بحذف النفي وإلا.",
                        "غير وسوى: تأخذان حكم المستثنى بعد إلا، وما بعدهما مضاف إليه."
                    ),
                    fullContent = "أسلوب الاستثناء من الثوابت في النحو. أول خطوة تحدد هل أركان الاستثناء تامة وهل سبقها نفي أم لا.",
                    examFocus = "احرص على ضبط الكلمات بالشكل التام في سؤال الإعراب والاستخراج.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "arab_exc_q1",
                            question = "ما الإعراب الصحيح لكلمة (مجتهداً) في جملة: «ما كافأ المعلمُ إلا مجتهداً»؟",
                            options = listOf(
                                "مستثنى منصوب وجوباً",
                                "مفعول به منصوب وعلامة نصبه الفتحة",
                                "بدل مرفوع",
                                "فاعل مرفوع"
                            ),
                            correctIndex = 1,
                            explanation = "الأسلوب ناقص منفي (المستثنى منه غير مذكور وسبق بنفي)، لذلك يُحذف (ما) و(إلا) وتُعرب الجملة: كافأ المعلمُ مجتهداً (مفعول به منصوب).",
                            teacherGuidance = "القاعدة العملية في الامتحان: ضع يدك على النفي و(إلا) واقرأ الجملة، فإن استقامت فهو ناقص منفي."
                        ),
                        QuizQuestion(
                            id = "arab_exc_q2",
                            question = "في جملة: «قرأتُ الكتبَ غيرَ كتابٍ»، ما إعراب كلمة (كتابٍ)؟",
                            options = listOf(
                                "مستثنى منصوب",
                                "نعت مجرور",
                                "مضاف إليه مجرور وعلامة جره الكسرة",
                                "بدل منصوب"
                            ),
                            correctIndex = 2,
                            explanation = "الاسم الواقع بعد (غير) و(سوى) يُعرب دائماً مضافاً إليه مجروراً، بينما تأخذ (غير) نفسها حكم الاسم بعد (إلا).",
                            teacherGuidance = "انتبه! في الجملة السابقة (غيرَ) هي المستثنى المنصوب، أما ما بعدها فهو مضاف إليه دائماً."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "english",
            title = "اللغة الإنجليزية",
            category = SubjectCategory.ENGLISH,
            iconName = "language",
            description = "Sudan Practical English (Spine), Grammar, Reading Comprehension, Composition & Literature.",
            unitsCount = 1,
            colorHex = 0xFFEA580C,
            track = com.example.model.BookTrack.COMMON,
            units = listOf(
                CurriculumUnit(
                    id = "english_grammar",
                    title = "Grammar Mastery: Tenses, Passive Voice & Conditionals",
                    subtitle = "Sudan Secondary Certificate English",
                    keyPoints = listOf(
                        "Passive Voice: Object + appropriate form of 'be' + Past Participle (V3).",
                        "Conditional Sentences: Zero, First, Second, and Third Conditionals.",
                        "Reported Speech: Backshift in tenses and pronoun adjustments.",
                        "Reading Comprehension strategies for the Sudanese Certificate paper."
                    ),
                    fullContent = "English paper in Sudan School Certificate focuses on grammar accuracy, reading comprehension, and clear composition writing.",
                    examFocus = "In the comprehension passage, write full grammatically correct sentences to secure full marks.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "eng_gram_q1",
                            question = "Choose the correct passive form: \"Sudanese farmers grow gum arabic in Kordofan.\"",
                            options = listOf(
                                "Gum arabic is grown in Kordofan by Sudanese farmers.",
                                "Gum arabic was grown in Kordofan by Sudanese farmers.",
                                "Gum arabic is growing in Kordofan.",
                                "Gum arabic has grown in Kordofan."
                            ),
                            correctIndex = 0,
                            explanation = "The original sentence is in the Present Simple ('grow'), so the passive uses 'is/are + past participle' -> 'is grown'.",
                            teacherGuidance = "Always check the tense of the main verb in the active sentence before choosing the form of 'to be'."
                        ),
                        QuizQuestion(
                            id = "eng_gram_q2",
                            question = "Complete the conditional sentence: \"If Ali had revised the notes, he ______ the exam easily.\"",
                            options = listOf(
                                "will pass",
                                "would pass",
                                "would have passed",
                                "passed"
                            ),
                            correctIndex = 2,
                            explanation = "This is a Third Conditional sentence (If + Past Perfect -> would have + V3), expressing an unreal past condition.",
                            teacherGuidance = "Remember: 'had + V3' in the if-clause always pairs with 'would have + V3' in the main clause."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "history",
            title = "التاريخ",
            category = SubjectCategory.HISTORY,
            iconName = "history",
            description = "تاريخ السودان الحديث والمعاصر، الثورة المهدية، الحكم الثنائي، والحركات الوطنية.",
            unitsCount = 1,
            colorHex = 0xFFB45309,
            track = com.example.model.BookTrack.ARTS,
            units = listOf(mahdistRevolutionLesson)
        ),
        Subject(
            id = "biology",
            title = "الأحياء",
            category = SubjectCategory.BIOLOGY,
            iconName = "science",
            description = "الوراثة وقوانين مندل، الحمض النووي DNA، التكاثر في النبات والحيوان، والجهاز العصبي والمناعة.",
            unitsCount = 1,
            colorHex = 0xFF047857,
            track = com.example.model.BookTrack.SCIENCE,
            units = listOf(
                CurriculumUnit(
                    id = "bio_genetics",
                    title = "الوراثة المندلية والأساس الجزيئي للوراثة (DNA)",
                    subtitle = "الأحياء للشهادة الثانوية — القسم العلمي",
                    keyPoints = listOf(
                        "قانون مندل الأول (انعزال العوامل) والثاني (التوزيع المستقل).",
                        "السيادة غير التامة وتوارث فصائل الدم في الإنسان.",
                        "تركيب الحمض النووي DNA وتضاعفه، والنسخ والترجمة لبناء البروتين."
                    ),
                    fullContent = "الوراثة من أهم أبواب الأحياء في امتحان الشهادة الثانوية. ابدأ دائماً بتحديد الطراز الجيني للآباء، ثم الأمشاج، ثم مربع بونيت للأبناء مع كتابة النسب المظهرية والجينية بوضوح.",
                    examFocus = "لا تنسَ كتابة رموز الآباء (P) والأمشاج (G) والجيل الأول (F1) عند حل مسائل الوراثة لضمان الدرجة الكاملة.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "bio_gen_q1",
                            question = "عند تزاوج نباتي بازلاء كلاهما طويل الساق هجين (Tt × Tt)، ما نسبة النباتات قصيرة الساق في الجيل الناتج؟",
                            options = listOf("0%", "25%", "50%", "75%"),
                            correctIndex = 1,
                            explanation = "التراكيب الناتجة هي TT : 2Tt : tt فتكون نسبة الصفة المتنحية قصيرة الساق (tt) ربع الأفراد أي 25%.",
                            teacherGuidance = "ارسم مربع بونيت دائماً في هامش الحل للتحقق من النسب بدقة."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "computer_science",
            title = "علوم الحاسوب",
            category = SubjectCategory.COMPUTER_SCIENCE,
            iconName = "calculate",
            description = "أنظمة العد والتحويلات، البوابات المنطقية، هياكل البيانات والخوارزميات، وقواعد البيانات.",
            unitsCount = 1,
            colorHex = 0xFF1D4ED8,
            track = com.example.model.BookTrack.SCIENCE,
            units = listOf(
                CurriculumUnit(
                    id = "cs_logic_gates",
                    title = "أنظمة العد والبوابات المنطقية الأساسية والمركبة",
                    subtitle = "علوم الحاسوب — القسم العلمي",
                    keyPoints = listOf(
                        "التحويل بين النظام الثنائي والعشري والسادس عشر.",
                        "البوابات الأساسية: AND و OR و NOT وجداول الصواب لها.",
                        "البوابات المركبة: NAND و NOR ورسم الدوائر المنطقية."
                    ),
                    fullContent = "تعتمد البوابات المنطقية على الجبر البولياني (0 و 1). درّب يدك على بناء جدول الصواب لأي دائرة منطقية خطوة بخطوة من المدخلات إلى المخرج النهائي.",
                    examFocus = "سؤال التحويل من العشري إلى الثنائي وبناء جدول الصواب للدوائر المركبة يتكرر سنوياً في ورقة الحاسوب.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "cs_q1",
                            question = "ما هو المكافئ الثنائي للعدد العشري (13)؟",
                            options = listOf("1011", "1101", "1110", "1001"),
                            correctIndex = 1,
                            explanation = "13 = 8 + 4 + 0 + 1، أي 1101 في النظام الثنائي.",
                            teacherGuidance = "استخدم أوزان الخانات (1، 2، 4، 8، 16) للتحويل السريع والتحقق من صحة القسمة المتكررة."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "math_basic",
            title = "الرياضيات الأساسية",
            category = SubjectCategory.MATHEMATICS,
            iconName = "calculate",
            description = "الدوال والمتتاليات، المصفوفات، المبادئ الأساسية للتفاضل والتكامل، والاحتمالات.",
            unitsCount = 1,
            colorHex = 0xFF3B82F6,
            track = com.example.model.BookTrack.COMMON,
            units = listOf(
                CurriculumUnit(
                    id = "math_basic_seq",
                    title = "المتوالية الحسابية والهندسية وتطبيقاتها",
                    subtitle = "الرياضيات الأساسية — المشترك والأدبي",
                    keyPoints = listOf(
                        "الحد النوني للمتوالية الحسابية: ح ن = أ + (ن - 1) د.",
                        "مجموع ن حداً في المتوالية الحسابية والهندسية.",
                        "حساب الأوساط الحسابية والهندسية."
                    ),
                    fullContent = "لحل أي مسألة في المتواليات، استخرج أولاً الحد الأول (أ) والأساس (د أو ر) ثم كوّن المعادلات من المعطيات.",
                    examFocus = "تحقق دائماً هل المتوالية حسابية (الفرق ثابت) أم هندسية (النسبة ثابتة) قبل اختيار القانون.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "mb_q1",
                            question = "في المتوالية الحسابية (3، 7، 11، ...) ما قيمة الحد العاشر؟",
                            options = listOf("35", "39", "41", "43"),
                            correctIndex = 1,
                            explanation = "أ = 3، د = 4، إذاً ح10 = 3 + 9 × 4 = 3 + 36 = 39.",
                            teacherGuidance = "اكتب قانون الحد النوني أولاً ثم عوّض بقيم (أ) و(د) و(ن)."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "islamic",
            title = "التربية الإسلامية",
            category = SubjectCategory.ISLAMIC_STUDIES,
            iconName = "menu_book",
            description = "تفسير السور المقررة، الأحاديث النبوية، العقيدة والفقه، والسيرة النبوية المشرفة.",
            unitsCount = 1,
            colorHex = 0xFF0F766E,
            track = com.example.model.BookTrack.COMMON,
            units = listOf(
                CurriculumUnit(
                    id = "islamic_tafsir",
                    title = "التفسير الموضوعي والأحكام الفقهية في القرآن والسنة",
                    subtitle = "التربية الإسلامية — لجميع طلاب الشهادة",
                    keyPoints = listOf(
                        "حفظ وفهم معاني المفردات القرآنية في الآيات المقررة.",
                        "استنباط الأحكام الشرعية والدروس التربوية من الأحاديث النبوية.",
                        "مقاصد الشريعة الإسلامية في حفظ الدين والنفس والعقل والمال."
                    ),
                    fullContent = "احرص في مادة التربية الإسلامية على حفظ النصوص القرآنية والحديثية مضبوطة، والربط بين المعنى اللغوي والحكم الشرعي المستفاد.",
                    examFocus = "في أسئلة (ما يُرشد إليه الحديث)، اكتب ثلاث فوائد عملية واضحة ومباشرة.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "isl_q1",
                            question = "أي من الآتي يُعد من الضرورات الخمس التي جاءت الشريعة الإسلامية لحفظها؟",
                            options = listOf("التجارة الدولية", "الدين والنفس والعقل والنسل والمال", "السياحة فقط", "البناء المعماري"),
                            correctIndex = 1,
                            explanation = "الكليات والضرورات الخمس في مقاصد الشريعة هي: حفظ الدين، والنفس، والعقل، والنسل (العرض)، والمال.",
                            teacherGuidance = "هذه القاعدة من ثوابت الفقه والمقاصد وتفيدك في الأسئلة المقالية."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "geography",
            title = "الجغرافيا والدراسات البيئية",
            category = SubjectCategory.GEOGRAPHY,
            iconName = "language",
            description = "جغرافية السودان الطبيعية والبشرية، الموارد الزراعية والمعدنية، الدراسات البيئية، والخرائط الكنتورية.",
            unitsCount = 1,
            colorHex = 0xFF0D9488,
            track = com.example.model.BookTrack.ARTS,
            units = listOf(
                CurriculumUnit(
                    id = "geo_resources",
                    title = "الموارد المائية والزراعية والدراسات البيئية في السودان",
                    subtitle = "الجغرافيا — القسم الأدبي",
                    keyPoints = listOf(
                        "حوض النيل وروافده الرئيسية ومشاريع الري الكبرى (الجزيرة، الرهد، حلفا).",
                        "الأقاليم المناخية والنباتية في السودان وتحديات التصحر.",
                        "قراءة الخرائط الطبوغرافية والكنتورية وحساب مقياس الرسم."
                    ),
                    fullContent = "تربط الجغرافيا الحديثة بين الموارد الطبيعية في السودان وتوزيع السكان والنشاط الاقتصادي، مع عناية خاصة بالمشروعات القومية وحماية البيئة.",
                    examFocus = "تدرب على رسم خريطة السودان الصماء وتعيين أهم المدن والأنهار والمشاريع الزراعية بدقة.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "geo_q1",
                            question = "ما هو أكبر مشروع زراعي مروي في السودان يقع بين النيلين الأزرق والأبيض؟",
                            options = listOf("مشروع حلفا الجديدة", "مشروع الجزيرة", "مشروع السوكي", "مشروع طوكر"),
                            correctIndex = 1,
                            explanation = "مشروع الجزيرة والمناقل هو أكبر مشروع مروي في السودان ويُروى انسيابياً من خزان سنار على النيل الأزرق.",
                            teacherGuidance = "اربط كل مشروع زراعي بمصدر الري الخاص به في الخريطة."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "islamic_studies",
            title = "الدراسات الإسلامية",
            category = SubjectCategory.ISLAMIC_STUDIES,
            iconName = "menu_book",
            description = "علوم القرآن والتفسير، مصطلح الحديث، أصول الفقه، وفقه المعاملات والأسرة.",
            unitsCount = 1,
            colorHex = 0xFF9A7214,
            track = com.example.model.BookTrack.ARTS,
            units = listOf(
                CurriculumUnit(
                    id = "is_usul",
                    title = "أصول الفقه ومصادر التشريع الإسلامي",
                    subtitle = "الدراسات الإسلامية — القسم الأدبي",
                    keyPoints = listOf(
                        "المصادر المتفق عليها: القرآن الكريم، السنة النبوية، الإجماع، والقياس.",
                        "أركان القياس الأربعة: الأصل، الفرع، الحكم، والعلة.",
                        "تطبيقات فقه المعاملات المالية المعاصرة."
                    ),
                    fullContent = "تُعد مادة الدراسات الإسلامية من المواد التخصصية المميزة للقسم الأدبي وتجمع بين علوم الآلة (أصول الفقه ومصطلح الحديث) والفقه التطبيقي.",
                    examFocus = "احرص على حفظ التعريفات الأصولية الدقيقة مع التمثيل لكل قاعدة بمثال شرعي.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "is_q1",
                            question = "كم عدد أركان القياس في علم أصول الفقه؟",
                            options = listOf("ركنان", "ثلاثة أركان", "أربعة أركان", "خمسة أركان"),
                            correctIndex = 2,
                            explanation = "أركان القياس أربعة: الأصل (المقيس عليه)، والفرع (المقيس)، وحكم الأصل، والعلة الجامعة.",
                            teacherGuidance = "اذكر الأركان الأربعة مع مثال تحريم النبيذ قياساً على الخمر لعلة الإسكار."
                        )
                    )
                )
            )
        ),
        Subject(
            id = "commerce",
            title = "العلوم التجارية والمحاسبة",
            category = SubjectCategory.COMMERCE,
            iconName = "calculate",
            description = "مبادئ المحاسبة المالية، القوائم الختامية والميزانية العمومية، الأعمال التجارية، والاقتصاد.",
            unitsCount = 1,
            colorHex = 0xFF6D28D9,
            track = com.example.model.BookTrack.ARTS,
            units = listOf(
                CurriculumUnit(
                    id = "comm_accounting",
                    title = "معادلة الميزانية والقوائم المالية الختامية",
                    subtitle = "العلوم التجارية — القسم الأدبي",
                    keyPoints = listOf(
                        "معادلة الميزانية الأساسية: الأصول = الخصوم + حقوق الملكية.",
                        "تسجيل القيود اليومية والترحيل لدفتر الأستاذ وميزان المراجعة.",
                        "إعداد حساب المتاجرة والأرباح والخسائر والميزانية العمومية."
                    ),
                    fullContent = "تعتمد المحاسبة في الشهادة السودانية على دقة التبويب بين الأصول الثابتة والمتداولة، والمصروفات والإيرادات في نهاية الفترة المالية.",
                    examFocus = "تأكد دائماً من توازن جانبي الميزانية العمومية في ختام السؤال العملي.",
                    sampleQuestions = listOf(
                        QuizQuestion(
                            id = "comm_q1",
                            question = "إذا بلغت أصول منشأة 150,000 جنيه وخصومها 60,000 جنيه، فكم تبلغ حقوق الملكية؟",
                            options = listOf("210,000 جنيه", "90,000 جنيه", "60,000 جنيه", "150,000 جنيه"),
                            correctIndex = 1,
                            explanation = "حقوق الملكية = الأصول - الخصوم = 150,000 - 60,000 = 90,000 جنيه.",
                            teacherGuidance = "ابدأ دائماً بكتابة معادلة الميزانية قبل التعويض."
                        )
                    )
                )
            )
        )
    )

    // Complete Library of Science, Arts & Common Textbooks in PDF format
    val textbooks: List<Textbook> = listOf(
        // --- القسم العلمي (Science Track) ---
        Textbook(
            id = "tb_math_3",
            title = "كتاب الرياضيات المتخصصة (PDF)",
            subjectTitle = "الرياضيات المتخصصة",
            subjectId = "math",
            track = com.example.model.BookTrack.SCIENCE,
            grade = "الصف الثالث الثانوي — القسم العلمي",
            pagesCount = 280,
            chaptersCount = 6,
            pdfFileName = "SudX_Specialized_Math_3.pdf",
            description = "الكتاب الوزاري المعتمد لوزارة التربية والتعليم السودانية للقسم العلمي، يشمل التفاضل والتكامل والمصفوفات والهندسة.",
            chapters = listOf(
                "الوحدة الأولى: مبدأ العد والتباديل والتوافيق ونظرية ذات الحدين",
                "الوحدة الثانية: المصفوفات والمحددات وحل نظم المعادلات",
                "الوحدة الثالثة: مبادئ التفاضل وقواعد الاشتقاق",
                "الوحدة الرابعة: تطبيقات التفاضل والقيم القصوى ورسم المنحنيات",
                "الوحدة الخامسة: حساب التكامل وتطبيقات المساحات والحجوم",
                "الوحدة السادسة: الإحصاء والاحتمالات والهندسة التحليلية (الدائرة)"
            ),
            keySummaries = listOf(
                "قوانين المشتقات الأساسية وقاعدة السلسلة واشتقاق الدوال المثلثية.",
                "التكامل غير المحدود والمحدود وحساب المساحة تحت المنحنيات.",
                "العمليات على المصفوفات وإيجاد النظير الضربي للمصفوفة."
            )
        ),
        Textbook(
            id = "tb_physics_3",
            title = "كتاب الفيزياء للشهادة الثانوية (PDF)",
            subjectTitle = "الفيزياء",
            subjectId = "physics",
            track = com.example.model.BookTrack.SCIENCE,
            grade = "الصف الثالث الثانوي — القسم العلمي",
            pagesCount = 240,
            chaptersCount = 5,
            pdfFileName = "SudX_Physics_3.pdf",
            description = "المقرر الشامل للقسم العلمي في الميكانيكا الدائرية، الجاذبية، الكهربية والمغناطيسية، الضوء، والفيزياء الذرية.",
            chapters = listOf(
                "الوحدة الأولى: الحركة الدائرية المنتظمة والحركة التوافقية البسيطة",
                "الوحدة الثانية: قانون الجذب العام وحركة الأقمار الصناعية",
                "الوحدة الثالثة: الانكسار والعدسات والمرايا الكرية",
                "الوحدة الرابعة: الكهربية الساكنة والتيارية والمكثفات والمجال المغناطيسي",
                "الوحدة الخامسة: الفيزياء الذرية والإشعاع النووي"
            ),
            keySummaries = listOf(
                "قوانين السرعة الزاوية والعجلة المركزية والقوة الجاذبة المركزية.",
                "توصيل المقاومات والمكثفات على التوالي والتوازي وقوانين كيرشوف.",
                "ظاهرة التأثير الكهروضوئي ومعادلة أينشتاين والتفاعلات النووية."
            )
        ),
        Textbook(
            id = "tb_chem_3",
            title = "كتاب الكيمياء للشهادة الثانوية (PDF)",
            subjectTitle = "الكيمياء",
            subjectId = "chemistry",
            track = com.example.model.BookTrack.SCIENCE,
            grade = "الصف الثالث الثانوي — القسم العلمي",
            pagesCount = 220,
            chaptersCount = 5,
            pdfFileName = "SudX_Chemistry_3.pdf",
            description = "الكتاب المقرر للقسم العلمي: الكيمياء العضوية، الاتزان الكيميائي، التحليل الحجمي (المعايرة)، والكيمياء الكهربية.",
            chapters = listOf(
                "الوحدة الأولى: سرعة التفاعل الكيميائي والاتزان ومبدأ لوشاتيليه",
                "الوحدة الثانية: الأحماض والقواعد والرقم الهيدروجيني (pH)",
                "الوحدة الثالثة: التحليل الكيميائي الكمي وحسابات المعايرة",
                "الوحدة الرابعة: الكيمياء الكهربية والخلايا الجلفانية والتحليل الكهربي",
                "الوحدة الخامسة: الكيمياء العضوية والهيدروكربونات ومشتقاتها"
            ),
            keySummaries = listOf(
                "تطبيقات مبدأ لوشاتيليه وأثر درجة الحرارة والضغط والتركيز على الاتزان.",
                "حسابات المولارية والعيارية في مسائل المعايرة.",
                "تسمية الألكانات والألكينات والكحولات والأحماض الكربوكسيلية حسب IUPAC."
            )
        ),
        Textbook(
            id = "tb_biology_3",
            title = "كتاب الأحياء للشهادة الثانوية (PDF)",
            subjectTitle = "الأحياء",
            subjectId = "biology",
            track = com.example.model.BookTrack.SCIENCE,
            grade = "الصف الثالث الثانوي — القسم العلمي",
            pagesCount = 235,
            chaptersCount = 5,
            pdfFileName = "SudX_Biology_3.pdf",
            description = "المقرر الرسمي للأحياء في القسم العلمي: الوراثة المندلية والجزيئية، التكاثر، التنسيق العصبي والهرموني، والبيئة.",
            chapters = listOf(
                "الوحدة الأولى: التكاثر في النباتات الزهرية والإنسان",
                "الوحدة الثانية: الوراثة المندلية والصفات المرتبطة بالجنس",
                "الوحدة الثالثة: الأساس الجزيئي للوراثة (DNA و RNA وبناء البروتين)",
                "الوحدة الرابعة: الإحساس والاستجابة والجهاز العصبي والهرمونات",
                "الوحدة الخامسة: التنوع الحيوي والمناعة والتوازن البيئي"
            ),
            keySummaries = listOf(
                "حل مسائل الوراثة بمربع بونيت وتوارث فصائل الدم والصفات المرتبطة بالجنس.",
                "مراحل الانقسام الميتوزي والميوزي ودورهما في التكاثر.",
                "تركيب الخلية العصبية وانتقال السيال العصبي ودور الغدد الصماء."
            )
        ),
        Textbook(
            id = "tb_cs_3",
            title = "كتاب علوم الحاسوب للشهادة الثانوية (PDF)",
            subjectTitle = "علوم الحاسوب",
            subjectId = "computer_science",
            track = com.example.model.BookTrack.SCIENCE,
            grade = "الصف الثالث الثانوي — القسم العلمي",
            pagesCount = 175,
            chaptersCount = 4,
            pdfFileName = "SudX_Computer_Science_3.pdf",
            description = "كتاب علوم الحاسوب للقسم العلمي: أنظمة العد، البوابات المنطقية، الخوارزميات وهياكل البيانات، وقواعد البيانات.",
            chapters = listOf(
                "الوحدة الأولى: أنظمة العد والتحويلات والتمثيل الرقمي",
                "الوحدة الثانية: البوابات المنطقية والجبر البولياني",
                "الوحدة الثالثة: الخوارزميات ومخططات الانسياب والبرمجة",
                "الوحدة الرابعة: قواعد البيانات العلائقية والشبكات"
            ),
            keySummaries = listOf(
                "التحويل الدقيق بين الأنظمة الثنائي والثماني والعشري والسادس عشر.",
                "رسم الدوائر المنطقية وبناء جداول الصواب.",
                "كتابة الخوارزميات ورسم مخططات الانسياب للمسائل الرياضية."
            )
        ),
        Textbook(
            id = "tb_eng_science_3",
            title = "كتاب العلوم الهندسية للشهادة الثانوية (PDF)",
            subjectTitle = "العلوم الهندسية",
            subjectId = "physics",
            track = com.example.model.BookTrack.SCIENCE,
            grade = "الصف الثالث الثانوي — القسم العلمي",
            pagesCount = 205,
            chaptersCount = 5,
            pdfFileName = "SudX_Engineering_Science_3.pdf",
            description = "مرجع العلوم الهندسية للقسم العلمي: الرسم الهندسي والإسقاط، الميكانيكا الهندسية، مقاومة المواد، والدوائر الهندسية.",
            chapters = listOf(
                "الوحدة الأولى: مبادئ الإسقاط الهندسي والمساقط المتعامدة",
                "الوحدة الثانية: الإستاتيكا وتحليل القوى والعزوم",
                "الوحدة الثالثة: الشغل والطاقة والآلات البسيطة",
                "الوحدة الرابعة: خواص المواد الهندسية والإجهاد والانفعال",
                "الوحدة الخامسة: مبادئ الهندسة الكهربية والتطبيقات العملية"
            ),
            keySummaries = listOf(
                "رسم المساقط الثلاثة (الأمامي، الجانبي، الأفقي) بأبعاد قياسية.",
                "تحليل القوى المتلاقية وحساب العزوم واتزان الأجسام."
            )
        ),

        // --- المواد المشتركة (العلمي والأدبي) ---
        Textbook(
            id = "tb_math_basic_3",
            title = "كتاب الرياضيات الأساسية (PDF)",
            subjectTitle = "الرياضيات الأساسية",
            subjectId = "math_basic",
            track = com.example.model.BookTrack.COMMON,
            grade = "الصف الثالث الثانوي — الأدبي والمشترك",
            pagesCount = 215,
            chaptersCount = 5,
            pdfFileName = "SudX_Basic_Math_3.pdf",
            description = "الكتاب الوزاري المعتمد للرياضيات الأساسية لطلاب القسم الأدبي والمشترك: الدوال، المتواليات، المصفوفات، ومبادئ التفاضل.",
            chapters = listOf(
                "الوحدة الأولى: الدوال الحقيقية والنهايات",
                "الوحدة الثانية: مبادئ التفاضل وقواعده الأساسية",
                "الوحدة الثالثة: المتواليات الحسابية والهندسية",
                "الوحدة الرابعة: المصفوفات وحل المعادلات الخطية",
                "الوحدة الخامسة: مبادئ الإحصاء ونظرية الاحتمالات"
            ),
            keySummaries = listOf(
                "قوانين الحد النوني ومجموع المتوالية الحسابية والهندسية.",
                "إيجاد النهايات البسيطة ومشتقات الدوال كثيرات الحدود."
            )
        ),
        Textbook(
            id = "tb_arabic_grammar",
            title = "كتاب قواعد النحو والصرف والبلاغة (PDF)",
            subjectTitle = "اللغة العربية",
            subjectId = "arabic",
            track = com.example.model.BookTrack.COMMON,
            grade = "الصف الثالث الثانوي — العلمي والأدبي",
            pagesCount = 190,
            chaptersCount = 7,
            pdfFileName = "SudX_Arabic_Grammar_3.pdf",
            description = "مرجع القواعد النحوية والبلاغية الكامل لطلاب الشهادة السودانية (العلمي والأدبي) مع تدريبات وإعراب نموذجي.",
            chapters = listOf(
                "الوحدة الأولى: أسلوب الاستثناء وأحكامه",
                "الوحدة الثانية: الممنوع من الصرف وعلل المنع",
                "الوحدة الثالثة: أسلوب التعجب والمدح والذم",
                "الوحدة الرابعة: التمييز وأحكام العدد",
                "الوحدة الخامسة: التوابع وأسلوب النداء",
                "الوحدة السادسة: المصادر والمشتقات والإعلال والإبدال",
                "الوحدة السابعة: علم البيان والبديع والمعاني"
            ),
            keySummaries = listOf(
                "التمييز بين أنواع الاستثناء التام المثبت والتام المنفي والناقص المنفي.",
                "التمييز بين الاستعارة التصريحية والمكنية والكناية والتشبيه."
            )
        ),
        Textbook(
            id = "tb_arabic_lit",
            title = "كتاب الأدب والنصوص والمطالعة (PDF)",
            subjectTitle = "اللغة العربية",
            subjectId = "arabic",
            track = com.example.model.BookTrack.COMMON,
            grade = "الصف الثالث الثانوي — العلمي والأدبي",
            pagesCount = 185,
            chaptersCount = 6,
            pdfFileName = "SudX_Arabic_Literature_3.pdf",
            description = "مقرر الأدب السوداني والعربي الحديث، النصوص الشعرية والنثرية المقررة، وتحليل المدارس الأدبية.",
            chapters = listOf(
                "الوحدة الأولى: مدرسة الإحياء والبعث والشعر الكلاسيكي",
                "الوحدة الثانية: المدارس الرومانسية (الديوان، أبولو، المهجر)",
                "الوحدة الثالثة: الشعر السوداني الحديث وأعلامه (التجاني، الفيتوري، المهدي)",
                "الوحدة الرابعة: النثر الفني والمقالة والقصة القصيرة",
                "الوحدة الخامسة: نصوص حفظ وتحليل مقررة",
                "الوحدة السادسة: تطبيقات النقد الأدبي والموازنات"
            ),
            keySummaries = listOf(
                "خصائص المدارس الشعرية وأبرز شعرائها في السودان والعالم العربي.",
                "شروح الأبيات المقررة ومعاني المفردات ومواطن الجمال."
            )
        ),
        Textbook(
            id = "tb_spine_6",
            title = "Spine Series: Sudan Practical English 6 (PDF)",
            subjectTitle = "اللغة الإنجليزية",
            subjectId = "english",
            track = com.example.model.BookTrack.COMMON,
            grade = "Secondary 3 — العلمي والأدبي",
            pagesCount = 180,
            chaptersCount = 8,
            pdfFileName = "SudX_English_Spine_6.pdf",
            description = "The official Ministry of Education English course book (Spine 6) for Science & Arts Sudanese Secondary Certificate students.",
            chapters = listOf(
                "Unit 1: Education and Future Careers",
                "Unit 2: Science and Modern Technology",
                "Unit 3: Agriculture and Resources in Sudan",
                "Unit 4: Health and Nutrition",
                "Unit 5: Cultural Heritage and History",
                "Unit 6: Environmental Conservation",
                "Unit 7: Grammar Revision & Advanced Composition",
                "Unit 8: Literature (Treasure Island / Things Fall Apart) & Practice"
            ),
            keySummaries = listOf(
                "Tenses, Passive Voice, Conditionals, Reported Speech & Relative Clauses.",
                "Reading Comprehension, Summary Writing & Guided Composition."
            )
        ),
        Textbook(
            id = "tb_islamic_3",
            title = "كتاب التربية الإسلامية والقرآن الكريم (PDF)",
            subjectTitle = "التربية الإسلامية",
            subjectId = "islamic",
            track = com.example.model.BookTrack.COMMON,
            grade = "الصف الثالث الثانوي — العلمي والأدبي",
            pagesCount = 165,
            chaptersCount = 5,
            pdfFileName = "SudX_Islamic_Education_3.pdf",
            description = "الكتاب الوزاري للتربية الإسلامية لجميع طلاب الشهادة السودانية: التفسير، الحديث الشريف، العقيدة، والفقه والسيرة.",
            chapters = listOf(
                "الوحدة الأولى: التفسير المقرر ومعاني الآيات",
                "الوحدة الثانية: أحاديث مختارة من السنة النبوية الشريفة",
                "الوحدة الثالثة: العقيدة الإسلامية ومقاصد الشريعة",
                "الوحدة الرابعة: فقه المعاملات والأسرة في الإسلام",
                "الوحدة الخامسة: صفحات مشرقة من السيرة النبوية والتاريخ الإسلامي"
            ),
            keySummaries = listOf(
                "المستفاد من الآيات والأحاديث المقررة مع حفظ النصوص.",
                "الأحكام الشرعية المرتبطة بالزواج والمعاملات المالية."
            )
        ),

        // --- القسم الأدبي (Arts Track) ---
        Textbook(
            id = "tb_history_3",
            title = "كتاب تاريخ السودان الحديث والمعاصر (PDF)",
            subjectTitle = "التاريخ",
            subjectId = "history",
            track = com.example.model.BookTrack.ARTS,
            grade = "الصف الثالث الثانوي — القسم الأدبي",
            pagesCount = 210,
            chaptersCount = 5,
            pdfFileName = "SudX_Sudan_History_3.pdf",
            description = "الكتاب الوزاري للقسم الأدبي: عهد الحكم التركي المصري، الثورة المهدية، الحكم الثنائي، والحركة الوطنية والاستقلال.",
            chapters = listOf(
                "الوحدة الأولى: السودان في عهد التركية السابقة (1821 - 1885م)",
                "الوحدة الثانية: الثورة المهدية وأسبابها ومراحلها وانتصاراتها",
                "الوحدة الثالثة: دولة المهدية في عهد الخليفة عبد الله التعايشي",
                "الوحدة الرابعة: الحكم الثنائي وتطور الإدارة الاستعمارية (1899 - 1955م)",
                "الوحدة الخامسة: الحركات الوطنية ومؤتمر الخريجين واستقلال السودان 1956م"
            ),
            keySummaries = listOf(
                "الأسباب الأربعة للثورة المهدية + السبب المباشر (واقعة الجزيرة أبا 1881م).",
                "معركة شيكان 1883م، تحرير الخرطوم 1885م، ومقاومة الاستعمار."
            )
        ),
        Textbook(
            id = "tb_geography_3",
            title = "كتاب الجغرافيا والدراسات البيئية (PDF)",
            subjectTitle = "الجغرافيا والدراسات البيئية",
            subjectId = "geography",
            track = com.example.model.BookTrack.ARTS,
            grade = "الصف الثالث الثانوي — القسم الأدبي",
            pagesCount = 225,
            chaptersCount = 5,
            pdfFileName = "SudX_Geography_Environment_3.pdf",
            description = "الكتاب الرسمي للقسم الأدبي: جغرافية السودان الطبيعية والاقتصادية، السكان والعمران، الدراسات البيئية، والخرائط.",
            chapters = listOf(
                "الوحدة الأولى: جغرافية السودان الطبيعية والمناخ والموارد المائية",
                "الوحدة الثانية: الزراعة والغابات والثروة الحيوانية والصناعة في السودان",
                "الوحدة الثالثة: جغرافية السكان والتوزيع الديموغرافي والعمران",
                "الوحدة الرابعة: الدراسات البيئية ومشكلات التصحر وتلوث البيئة",
                "الوحدة الخامسة: الخرائط الكنتورية ومقياس الرسم والإحداثيات"
            ),
            keySummaries = listOf(
                "مقارنة المشاريع المروية والمطرية ومصادر المياه في السودان.",
                "قراءة خطوط الكنتور وحساب الميل والمسافات على الخريطة."
            )
        ),
        Textbook(
            id = "tb_islamic_studies_3",
            title = "كتاب الدراسات الإسلامية المتخصصة (PDF)",
            subjectTitle = "الدراسات الإسلامية",
            subjectId = "islamic_studies",
            track = com.example.model.BookTrack.ARTS,
            grade = "الصف الثالث الثانوي — القسم الأدبي",
            pagesCount = 195,
            chaptersCount = 4,
            pdfFileName = "SudX_Islamic_Studies_3.pdf",
            description = "المرجع التخصصي لطلاب القسم الأدبي في علوم القرآن والتفسير، مصطلح الحديث، أصول الفقه، والفقه المقارن.",
            chapters = listOf(
                "الوحدة الأولى: علوم القرآن ومناهج المفسرين",
                "الوحدة الثانية: علم مصطلح الحديث وأنواع الخبر (الصحيح والحسن والضعيف)",
                "الوحدة الثالثة: أصول الفقه ومصادر التشريع (الكتاب، السنة، الإجماع، القياس)",
                "الوحدة الرابعة: فقه المعاملات المالية والميراث والأسرة"
            ),
            keySummaries = listOf(
                "شروط الحديث الصحيح والفرق بين الحديث الحسن والضعيف.",
                "أركان القياس وأمثلة الاجتهاد الفقهي المعاصر."
            )
        ),
        Textbook(
            id = "tb_commerce_3",
            title = "كتاب العلوم التجارية والمحاسبة (PDF)",
            subjectTitle = "العلوم التجارية والمحاسبة",
            subjectId = "commerce",
            track = com.example.model.BookTrack.ARTS,
            grade = "الصف الثالث الثانوي — القسم الأدبي",
            pagesCount = 200,
            chaptersCount = 5,
            pdfFileName = "SudX_Commerce_Accounting_3.pdf",
            description = "المقرر الرسمي للعلوم التجارية والمالية للقسم الأدبي: مبادئ المحاسبة، القوائم الختامية، التجارة والبنوك، والاقتصاد.",
            chapters = listOf(
                "الوحدة الأولى: المعادلة المحاسبية ونظرية القيد المزدوج",
                "الوحدة الثانية: الدفاتر المحاسبية وميزان المراجعة",
                "الوحدة الثالثة: الحسابات الختامية والميزانية العمومية",
                "الوحدة الرابعة: البنوك والأوراق التجارية والتجارة الخارجية",
                "الوحدة الخامسة: مبادئ الاقتصاد والتنمية الاقتصادية"
            ),
            keySummaries = listOf(
                "إعداد قائمة الدخل وحساب المتاجرة والأرباح والخسائر بدقة.",
                "التمييز بين الأصول الثابتة والمتداولة والخصوم."
            )
        )
    )

    // The Resources: امتحانات سابقة (Past Exams)
    val pastExams: List<PastExam> = listOf(
        PastExam(
            id = "exam_history_2022",
            subjectTitle = "التاريخ",
            year = "2022م",
            session = "الدور الأول",
            durationMinutes = 180,
            totalMarks = 100,
            keyTopics = listOf("أسباب الثورة المهدية", "معركة شيكان", "مؤتمر الخريجين 1938م", "اتفاقية 1899م"),
            sampleQuestions = listOf(
                QuizQuestion(
                    id = "pe_hist_1",
                    question = "علل لما يأتي: يعتبر فرض الضرائب الباهظة من أقوى أسباب قيام الثورة المهدية عام 1881م؟",
                    options = listOf(
                        "لأنها أدت إلى بناء مشاريع كبرى في البلاد",
                        "لأنها أرهقت الأهالي والمزارعين واستُخدم فيها الضرب بالسوط ومصادرة الأموال",
                        "لأنها ألغت التبادل التجاري مع مصر",
                        "لأنها فُرضت على السلع الأجنبية فقط"
                    ),
                    correctIndex = 1,
                    explanation = "عانت جميع الفئات من قسوة الجباة وعسفهم في تحصيل الضرائب المتعددة على النخيل والسواقي والمواشي.",
                    teacherGuidance = "في الامتحان، اكتب نقطتين: 1) فداحة المقدار وتعدد الأنواع، 2) القسوة والإهانة في أسلوب الجباية."
                )
            )
        ),
        PastExam(
            id = "exam_math_2022",
            subjectTitle = "الرياضيات المتخصصة",
            year = "2022م",
            session = "الدور الأول",
            durationMinutes = 180,
            totalMarks = 100,
            keyTopics = listOf("تطبيقات التفاضل", "المصفوفة النظيرة", "التكامل المحدود", "معادلة الدائرة"),
            sampleQuestions = listOf(
                QuizQuestion(
                    id = "pe_math_1",
                    question = "إذا كانت الدالة د(س) = س² - 4س + 5، فأوجد معادلة المماس عند النقطة (3، 2):",
                    options = listOf(
                        "ص = 2س - 4",
                        "ص = 2س + 4",
                        "ص = 4س - 10",
                        "ص = س - 1"
                    ),
                    correctIndex = 0,
                    explanation = "ميل المماس = د'(س) = 2س - 4. عند س = 3 يكون الميل = 2(3) - 4 = 2. معادلة المماس: ص - 2 = 2(س - 3) ⇐ ص = 2س - 4.",
                    teacherGuidance = "في ورقة الرياضيات المتخصصة، ابدأ دائماً بإيجاد المشتقة الأولى ثم احسب الميل عند النقطة المعطاة."
                )
            )
        ),
        PastExam(
            id = "exam_physics_2021",
            subjectTitle = "الفيزياء",
            year = "2021م",
            session = "الدور الأول",
            durationMinutes = 180,
            totalMarks = 100,
            keyTopics = listOf("العجلة المركزية", "الجاذبية الكونية", "المكثفات وتوصيلها", "التحلل الإشعاعي"),
            sampleQuestions = listOf(
                QuizQuestion(
                    id = "pe_phys_1",
                    question = "مكثفان سعتاهما 4 ميكروفاراد و 6 ميكروفاراد وصّلا على التوازي، فما السعة المكافئة لهما؟",
                    options = listOf(
                        "2.4 ميكروفاراد",
                        "10 ميكروفاراد",
                        "5 ميكروفاراد",
                        "24 ميكروفاراد"
                    ),
                    correctIndex = 1,
                    explanation = "عند توصيل المكثفات على التوازي تُجمع السعات مباشرة: س الكلية = س1 + س2 = 4 + 6 = 10 ميكروفاراد.",
                    teacherGuidance = "لا تخلط بين توصيل المكثفات وتوصيل المقاومات! في المكثفات التوازي جمع مباشر والتوالي مقلوب الجمع."
                )
            )
        ),
        PastExam(
            id = "exam_chem_2022",
            subjectTitle = "الكيمياء",
            year = "2022م",
            session = "الدور الأول",
            durationMinutes = 180,
            totalMarks = 100,
            keyTopics = listOf("قاعدة لوشاتيليه", "معايرة حمض وقاعدة", "تسمية الألكانات والألكينات", "الأكسدة والاختزال"),
            sampleQuestions = listOf(
                QuizQuestion(
                    id = "pe_chem_1",
                    question = "تعادل 25 سم³ من محلول هيدروكسيد الصوديوم مع 25 سم³ من حمض الهيدروكلوريك تركيزه 0.1 مول/لتر. ما تركيز القاعدة؟",
                    options = listOf(
                        "0.05 مول/لتر",
                        "0.1 مول/لتر",
                        "0.2 مول/لتر",
                        "0.4 مول/لتر"
                    ),
                    correctIndex = 1,
                    explanation = "نسبة التفاعل 1:1 (NaOH + HCl → NaCl + H2O)، وبما أن الحجمين متساويان (25 = 25) فإن تركيز القاعدة = تركيز الحمض = 0.1 مول/لتر.",
                    teacherGuidance = "في مسائل المعايرة، اكتب المعادلة الموزونة أولاً لتحديد عدد مولات الحمض والقاعدة (أ، ب)."
                )
            )
        ),
        PastExam(
            id = "exam_arabic_2022",
            subjectTitle = "اللغة العربية",
            year = "2022م",
            session = "الدور الأول",
            durationMinutes = 180,
            totalMarks = 100,
            keyTopics = listOf("أسلوب الاستثناء", "الممنوع من الصرف", "الاستعارة التصريحية والمكنية", "الأدب الحديث"),
            sampleQuestions = listOf(
                QuizQuestion(
                    id = "pe_arab_1",
                    question = "ما علة منع كلمة (مساجد) من الصرف؟",
                    options = listOf(
                        "علم أعجمي",
                        "صيغة منتهى الجموع",
                        "صفة على وزن أفعل",
                        "اسم منتهٍ بألف التأنيث المقصورة"
                    ),
                    correctIndex = 1,
                    explanation = "(مساجد) جمع تكسير بعد ألفه حرفان، فهي على صيغة منتهى الجموع فتُمنع من الصرف لعلة واحدة.",
                    teacherGuidance = "تذكر أن الممنوع من الصرف يُجر بالفتحة نيابة عن الكسرة إلا إذا أُضيف أو اقترن بـ (أل)."
                )
            )
        ),
        PastExam(
            id = "exam_english_2022",
            subjectTitle = "اللغة الإنجليزية",
            year = "2022م",
            session = "الدور الأول",
            durationMinutes = 180,
            totalMarks = 100,
            keyTopics = listOf("Passive Voice", "Conditionals", "Reading Comprehension", "Guided Composition"),
            sampleQuestions = listOf(
                QuizQuestion(
                    id = "pe_eng_1",
                    question = "Choose the correct preposition: \"Students must concentrate ______ their studies before the exam.\"",
                    options = listOf(
                        "in",
                        "at",
                        "on",
                        "for"
                    ),
                    correctIndex = 2,
                    explanation = "The verb 'concentrate' is followed by the preposition 'on'.",
                    teacherGuidance = "Verb-preposition collocations from the Spine series appear regularly in Section One."
                )
            )
        )
    )

    val examTips: List<ExamTip> = listOf(
        ExamTip(
            id = "tip_1",
            title = "كيف تتعامل مع ورقة الامتحان في أول 15 دقيقة؟",
            category = "استراتيجية الامتحان",
            content = "اقرأ الورقة بهدوء كامل، ضع إشارة خفيفة أمام الأسئلة التي تشعر بالثقة التامة فيها. ابدأ بحل هذه الأسئلة أولاً لبناء الثقة والاطمئنان وتوفير رصيد درجات مبكر.",
            importantRule = "لا تتوقف طويلاً عند السؤال الصعب في البداية؛ تجاوزه وعد إليه في النصف الثاني من الوقت."
        ),
        ExamTip(
            id = "tip_2",
            title = "قاعدة الدرجة الكاملة في أسئلة (علل لما يأتي):",
            category = "التاريخ والمواد الأدبية",
            content = "في امتحانات الشهادة السودانية، الإجابة المقتضبة بسطر واحد تضيع عليك نصف الدرجة. دائماً اربط السبب بالنتيجة مع ذكر مثال أو تاريخ أو واقعة مؤيدة متى ما أمكن.",
            importantRule = "استخدم الترقيم (1، 2، 3) في الإجابة لتسهيل قراءتها على الأستاذ المصحح."
        ),
        ExamTip(
            id = "tip_3",
            title = "سر كسب درجات المسائل في الفيزياء والرياضيات والكيمياء:",
            category = "المواد العلمية",
            content = "المصحح في الشهادة السودانية يوزع الدرجات على: 1) كتابة القانون الصحيح، 2) التعويض السليم بالوحدات الدولية، 3) الحساب والناتج، 4) كتابة وحدة القياس النهائية.",
            importantRule = "لا تحذف خطوات الحل في المسودات؛ اكتب الخطوات التفصيلية في ورقة الإجابة لتنال درجات الخطوات."
        ),
        ExamTip(
            id = "tip_4",
            title = "تنظيم وقت المذاكرة لطلاب الشهادة السودانية:",
            category = "تنظيم الوقت",
            content = "اعتمد طريقة الفترات المتوازنة (ساعة وربع مذاكرة مركزة ثم 15 دقيقة راحة). نوّع بين مادة علمية وأخرى أدبية في نفس اليوم لتجنب الإرهاق الذهني.",
            importantRule = "الفجر والصباح الباكر هما أفضل أوقات الاستيعاب للمواد التي تتطلب حفظاً وفهماً عميقاً كالتاريخ والنصوص."
        ),
        ExamTip(
            id = "tip_5",
            title = "التعامل مع التوتر وضغط الامتحان:",
            category = "الدعم النفسي",
            content = "تذكر يا بني أن امتحان الشهادة السودانية ليس تعجيزاً، وإنما قياس لما درسته طوال العام. توكل على الله، ونم مبكراً ليلة الامتحان، وتناول وجبة فطور خفيفة ومغذية.",
            importantRule = "الثقة بالله والهدوء الذهني يعادلان نصف الاستعداد العلمي."
        )
    )

    val quickQuestions = listOf(
        "ما هي آخر مستجدات وجدول امتحانات الشهادة الثانوية السودانية؟",
        "أسباب الثورة المهدية باختصار وكيف أحفظها للامتحان؟",
        "علل: تعتبر معركة شيكان 1883م نقطة تحول في تاريخ المهدية؟",
        "كيف أفرق بين أسلوب الاستثناء التام والناقص في اللغة العربية؟",
        "ما هي أهم تطبيقات العجلة المركزية في امتحان الفيزياء؟",
        "كيف أضمن الدرجة الكاملة في مسألة الوراثة وقوانين مندل؟",
        "قدم لي خطة مراجعة أسبوعية مركزة للشهادة السودانية"
    )

    val defaultStudySessions: List<com.example.model.StudySession> = listOf(
        com.example.model.StudySession(
            id = "sess_1",
            subjectId = "math",
            subjectTitle = "الرياضيات",
            topic = "حل مسائل التفاضل والقيم القصوى",
            period = com.example.model.SchedulePeriod.DAILY,
            day = com.example.model.StudyDay.SATURDAY,
            hour = 5,
            minute = 30,
            durationMinutes = 60,
            reminderEnabled = true,
            isCompleted = true,
            priority = "عالية"
        ),
        com.example.model.StudySession(
            id = "sess_2",
            subjectId = "history",
            subjectTitle = "التاريخ",
            topic = "مراجعة أسباب الثورة المهدية وواقعة أبا",
            period = com.example.model.SchedulePeriod.WEEKLY,
            day = com.example.model.StudyDay.SUNDAY,
            hour = 16,
            minute = 30,
            durationMinutes = 45,
            reminderEnabled = true,
            isCompleted = false,
            priority = "عالية"
        ),
        com.example.model.StudySession(
            id = "sess_3",
            subjectId = "physics",
            subjectTitle = "الفيزياء",
            topic = "قوانين العجلة المركزية والأقمار الصناعية",
            period = com.example.model.SchedulePeriod.WEEKLY,
            day = com.example.model.StudyDay.MONDAY,
            hour = 17,
            minute = 0,
            durationMinutes = 60,
            reminderEnabled = true,
            isCompleted = false,
            priority = "عالية"
        ),
        com.example.model.StudySession(
            id = "sess_4",
            subjectId = "chemistry",
            subjectTitle = "الكيمياء",
            topic = "تطبيقات مبدأ لوشاتيليه وحسابات المعايرة",
            period = com.example.model.SchedulePeriod.WEEKLY,
            day = com.example.model.StudyDay.TUESDAY,
            hour = 20,
            minute = 0,
            durationMinutes = 60,
            reminderEnabled = true,
            isCompleted = false,
            priority = "متوسطة"
        ),
        com.example.model.StudySession(
            id = "sess_5",
            subjectId = "arabic",
            subjectTitle = "اللغة العربية",
            topic = "قواعد أسلوب الاستثناء وتطبيقات الإعراب",
            period = com.example.model.SchedulePeriod.DAILY,
            day = com.example.model.StudyDay.WEDNESDAY,
            hour = 6,
            minute = 0,
            durationMinutes = 45,
            reminderEnabled = true,
            isCompleted = false,
            priority = "متوسطة"
        ),
        com.example.model.StudySession(
            id = "sess_6",
            subjectId = "english",
            subjectTitle = "اللغة الإنجليزية",
            topic = "Grammar Revision & Reading Passage Practice",
            period = com.example.model.SchedulePeriod.WEEKLY,
            day = com.example.model.StudyDay.THURSDAY,
            hour = 19,
            minute = 30,
            durationMinutes = 60,
            reminderEnabled = true,
            isCompleted = false,
            priority = "مراجعة"
        )
    )
}
