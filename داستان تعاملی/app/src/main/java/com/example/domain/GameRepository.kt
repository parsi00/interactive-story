package com.example.domain

import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GameRepository(private val storyDao: StoryDao) {

    val characterProfile: Flow<CharacterProfileEntity?> = storyDao.getCharacterProfile()
    val inventoryItems: Flow<List<InventoryItemEntity>> = storyDao.getInventoryItems()
    val achievements: Flow<List<AchievementEntity>> = storyDao.getAchievements()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun getCharacterDirect(): CharacterProfileEntity? = storyDao.getCharacterProfileDirect()

    suspend fun saveCharacterProfile(profile: CharacterProfileEntity) {
        storyDao.saveCharacterProfile(profile)
    }

    suspend fun seedInitialAchievementsIfEmpty() {
        val existing = storyDao.getAchievements()
        val defaultAchievements = listOf(
            AchievementEntity(
                id = "ach_first_step",
                title = "نخستین گام در تاریکی",
                description = "آغاز ماجراجویی شاهانه و خلق قهرمان خود.",
                rewardCoins = 50,
                rewardXp = 50,
                iconName = "auto_awesome"
            ),
            AchievementEntity(
                id = "ach_castle_master",
                title = "فاتح قلعه سایه‌ها",
                description = "ورود به تالار اصلی قلعه سرنوشت‌ساز و شکست اولین بیدارشدگان.",
                rewardCoins = 100,
                rewardXp = 150,
                iconName = "fort"
            ),
            AchievementEntity(
                id = "ach_wealthy",
                title = "گنجینه‌دار کهن",
                description = "جمع‌آوری بیش از ۲۰۰ سکه طلا در طول سفر.",
                rewardCoins = 150,
                rewardXp = 200,
                iconName = "monetization_on"
            ),
            AchievementEntity(
                id = "ach_dragon_slayer",
                title = "شکارچی اژدها",
                description = "یافتن یا نبرد با اژدهای خفته در سیاه‌چال‌ها.",
                rewardCoins = 250,
                rewardXp = 300,
                iconName = "shield"
            )
        )
        storyDao.insertAchievements(defaultAchievements)
    }

    suspend fun addItemToInventory(item: InventoryItemEntity) {
        storyDao.insertOrUpdateItem(item)
    }

    suspend fun removeItemFromInventory(id: Int) {
        storyDao.deleteItem(id)
    }

    suspend fun unlockAchievement(achievementId: String) {
        storyDao.unlockAchievement(achievementId)
    }

    suspend fun resetGameProgress() {
        storyDao.clearProfile()
        storyDao.clearInventory()
    }

    // --- PREBUILT BRANCHING STORIES ---
    fun getChapter(storyId: String, chapterId: String): StoryChapter {
        val chapterMap = if (storyId == "story_forest") prebuiltForestStory else prebuiltCastleStory
        return chapterMap[chapterId] ?: prebuiltCastleStory["ch_1"]!!
    }

    private val prebuiltCastleStory = mapOf(
        "ch_1" to StoryChapter(
            id = "ch_1",
            storyId = "story_castle",
            storyTitleFa = "افسانه قلعه تاریک سایه‌ها",
            chapterTitleFa = "دروازه گوهرین شاهانه",
            textFa = """
                ماه خونین بر بالای برج‌های دندانه‌دار قلعه سایه‌ها می‌درخشد. باد سردی لابلای سنگ‌های کهن زوزه می‌کشد و بوی جادوی فراموش‌شده برج‌ها را فرا گرفته است.
                
                تو در برابر دروازه عظیم آهنی با نقش و نگارهای اژدها ایستاده‌ای. مهر و موم‌های باستانی قفل دروازه در اثر پدیدار شدن جادوی تاریک شکسته شده‌اند. در دستت سلاح خود را محکم می‌فشاری و صدای نجواهای مبهم از تالار اصلی قلعه به گوش می‌رسد.
                
                مسیرهای متعددی برای ورود پیش روی توست. کدام راه را انتخاب می‌کنی؟
            """.trimIndent(),
            illustrationResName = "img_story_castle",
            choices = listOf(
                StoryChoice(
                    id = "c1_1",
                    textFa = "شکستن قفل سنگین دروازه با نیروی بازو (مخصوص جنگجو)",
                    nextChapterId = "ch_hall_combat",
                    requiredClass = "WARRIOR",
                    requiredStat = "STRENGTH",
                    requiredMinStatValue = 12,
                    xpReward = 40,
                    coinReward = 20,
                    hpChange = -5,
                    itemGain = InventoryItemEntity(
                        itemId = "sword_dragon",
                        name = "شمشیر فولاد اژدها",
                        description = "شمشیر دو لبه سنگین آبدیده با جادوی آتش. قدرت حمله +۱۵ و قدرت +۳",
                        itemType = "WEAPON",
                        slotType = "WEAPON",
                        atkBonus = 15,
                        strBonus = 3,
                        iconName = "sword"
                    ),
                    achievementToUnlock = "ach_first_step",
                    consequenceTextFa = "با ضربه‌ای سهمگین قفل سنگین دروازه در هم شکست و خرد شد!"
                ),
                StoryChoice(
                    id = "c1_2",
                    textFa = "خواند ورد کهن گشودن دروازه‌ها (مخصوص جادوگر)",
                    nextChapterId = "ch_archive",
                    requiredClass = "MAGE",
                    requiredStat = "MAGIC",
                    requiredMinStatValue = 12,
                    xpReward = 50,
                    coinReward = 25,
                    manaChange = -15,
                    itemGain = InventoryItemEntity(
                        itemId = "grimoire_shadow",
                        name = "کتاب رازهای سایه",
                        description = "طومار جادویی باستانی حاوی وردهای فراموش‌شده. مانای حداکثر +۳۰",
                        itemType = "SCROLL",
                        manaBonus = 30,
                        iconName = "auto_awesome"
                    ),
                    achievementToUnlock = "ach_first_step",
                    consequenceTextFa = "حلقه‌های جادویی بنفش حول دستت درخشیدند و قفل با صدایی آرام گشوده شد."
                ),
                StoryChoice(
                    id = "c1_3",
                    textFa = "صعود چابک از دیوارهای سنگی و ورود از پنجره برج (مخصوص تیرانداز)",
                    nextChapterId = "ch_armory",
                    requiredClass = "ARCHER",
                    requiredStat = "AGILITY",
                    requiredMinStatValue = 12,
                    xpReward = 45,
                    coinReward = 30,
                    itemGain = InventoryItemEntity(
                        itemId = "bow_silver_moon",
                        name = "کمان نقره‌ای ماه",
                        description = "کمان شاهانه ساخته شده از چوب درخشان مقدس. قدرت حمله +۱۲ و چابکی +۴",
                        itemType = "WEAPON",
                        slotType = "WEAPON",
                        atkBonus = 12,
                        agiBonus = 4,
                        iconName = "ads_click"
                    ),
                    achievementToUnlock = "ach_first_step",
                    consequenceTextFa = "مانند سایه‌ای بی‌صدا از دیوار بالا رفتی و وارد اسلحه‌خانه برج شدی."
                ),
                StoryChoice(
                    id = "c1_4",
                    textFa = "ورود از راه سیاه‌چال زیرزمینی با احتیاط فراوان",
                    nextChapterId = "ch_dungeon",
                    xpReward = 30,
                    coinReward = 40,
                    hpChange = 0,
                    itemGain = InventoryItemEntity(
                        itemId = "potion_health",
                        name = "معجون شفا بخش یاقوتی",
                        description = "نوشیدنی جادویی که ۵۰ واحد سلامتی را بلافاصله بازمی‌گرداند.",
                        itemType = "POTION",
                        hpBonus = 50,
                        iconName = "local_hospital"
                    ),
                    consequenceTextFa = "پله‌های مرطوب سیاه‌چال را پایین رفتی و یک معجون شفابخش در میان کاشی‌ها یافتی."
                )
            )
        ),
        "ch_hall_combat" to StoryChapter(
            id = "ch_hall_combat",
            storyId = "story_castle",
            storyTitleFa = "افسانه قلعه تاریک سایه‌ها",
            chapterTitleFa = "تالار سایه‌های بیدار",
            textFa = """
                وارد تالار اصلی شدی. مجسمه‌های سنگی شوالیه‌های کهن دور تا دور تالار صف کشیده‌اند. ناگهان چشمان یاقوتی مجسمه‌ها شروع به درخشش می‌کنند و سایه‌هایی به شکل شوالیه‌های تاریک از دل سنگ‌ها بیرون می‌جهند!
                
                سایه نخست با گرز سنگین به سویت می‌تازد. باید فوراً تصمیم بگیری.
            """.trimIndent(),
            illustrationResName = "img_story_castle",
            choices = listOf(
                StoryChoice(
                    id = "c2a_1",
                    textFa = "دفاع با سپر و پاتک زدن با تمام قدرت",
                    nextChapterId = "ch_throne_boss",
                    xpReward = 60,
                    coinReward = 50,
                    hpChange = -10,
                    achievementToUnlock = "ach_castle_master",
                    consequenceTextFa = "ضربه گرز را دفع کردی و با یک ضربه دقیق سایه را متلاشی ساختی!"
                ),
                StoryChoice(
                    id = "c2a_2",
                    textFa = "استفاده از معجون و رها کردن موج جادویی متراکم",
                    nextChapterId = "ch_throne_boss",
                    xpReward = 70,
                    coinReward = 40,
                    manaChange = -20,
                    consequenceTextFa = "انفجار جادویی تالار را روشن ساخت و تمام سایه‌ها خاکستر شدند."
                )
            )
        ),
        "ch_archive" to StoryChapter(
            id = "ch_archive",
            storyId = "story_castle",
            storyTitleFa = "افسانه قلعه تاریک سایه‌ها",
            chapterTitleFa = "کتابخانه طلسم‌های ممنوعه",
            textFa = """
                وارد کتابخانه‌ای عظیم شدی که هزاران طومار گرد گرفته در قفسه‌های آبنوس آن چیده شده‌اند. در مرکز سالن، طوماری طلایی روی میز سنگی معلق است.
                
                طومار حاوی رازهای پادشاه سایه‌هاست. با نزدیک شدن تو، کتاب‌های سخنگو شروع به زمزمه می‌کنند...
            """.trimIndent(),
            illustrationResName = "img_story_castle",
            choices = listOf(
                StoryChoice(
                    id = "c2b_1",
                    textFa = "رمزگشایی طومار باستانی با دانش جادویی",
                    nextChapterId = "ch_throne_boss",
                    xpReward = 80,
                    coinReward = 60,
                    manaChange = -10,
                    achievementToUnlock = "ach_castle_master",
                    consequenceTextFa = "نقطه ضعف پادشاه سایه‌ها را دریافت کردی! جادوی تو شگرف‌تر شد."
                ),
                StoryChoice(
                    id = "c2b_2",
                    textFa = "برداشتن طومار و حرکت سریع به سمت تالار پادشاهی",
                    nextChapterId = "ch_throne_boss",
                    xpReward = 50,
                    coinReward = 30,
                    consequenceTextFa = "طومار را در کیف خود گذاشتی و به سمت تالار تخت روانه شدی."
                )
            )
        ),
        "ch_armory" to StoryChapter(
            id = "ch_armory",
            storyId = "story_castle",
            storyTitleFa = "افسانه قلعه تاریک سایه‌ها",
            chapterTitleFa = "گنجینه اسلحه‌خانه کهن",
            textFa = """
                اسلحه‌خانه مملو از زره‌های زرین و شمشیرهای افسانه‌ای است. در زوایای تاریک room، یک صندوقچه سنگی با قفل طلایی قرار دارد.
                
                صدای قدم‌های پادشاه سایه‌ها از تالار مجاور شنیده می‌شود.
            """.trimIndent(),
            illustrationResName = "img_story_dungeon",
            choices = listOf(
                StoryChoice(
                    id = "c2c_1",
                    textFa = "باز کردن قفل صندوقچه با شاه‌کلید چابکی",
                    nextChapterId = "ch_throne_boss",
                    xpReward = 60,
                    coinReward = 100,
                    achievementToUnlock = "ach_wealthy",
                    itemGain = InventoryItemEntity(
                        itemId = "ring_shadow_master",
                        name = "حلقه جادویی پادشاه سایه",
                        description = "حلقه نگین‌دار باستانی. سلامتی +۲۰ و مانای +۲۰ و جادو +۴",
                        itemType = "ARTIFACT",
                        slotType = "ARTIFACT",
                        hpBonus = 20,
                        manaBonus = 20,
                        magBonus = 4,
                        iconName = "auto_awesome"
                    ),
                    consequenceTextFa = "صندوقچه گشوده شد و گنجینه‌ای فراوان همراه با حلقه پادشاه به دست آوردی!"
                ),
                StoryChoice(
                    id = "c2c_2",
                    textFa = "تجهیز سریع زره سنگی و شتاب به تالار اصلی",
                    nextChapterId = "ch_throne_boss",
                    xpReward = 50,
                    coinReward = 20,
                    consequenceTextFa = "زره را بر تن کردی و با آمادگی کامل وارد نبرد شدی."
                )
            )
        ),
        "ch_dungeon" to StoryChapter(
            id = "ch_dungeon",
            storyId = "story_castle",
            storyTitleFa = "افسانه قلعه تاریک سایه‌ها",
            chapterTitleFa = "سیاه‌چال و اژدهای خفته",
            textFa = """
                در انتهای پله‌های تاریک، غاری عظیم کشف کردی. در مرکز غار، اژدهای سیاه کهن روی خرمنی از سکه‌های طلا خفته است.
                
                شعله‌های بنفش رنگ کم‌رنگی از پوزه اژدها بیرون می‌زند. یک تاج زرین مرصع دقیقاً کنار پنگه اژدها قرار دارد.
            """.trimIndent(),
            illustrationResName = "img_story_dungeon",
            choices = listOf(
                StoryChoice(
                    id = "c2d_1",
                    textFa = "خزیدن بی‌صدا و برداشتن تاج مرصع شاهانه",
                    nextChapterId = "ch_throne_boss",
                    xpReward = 100,
                    coinReward = 150,
                    achievementToUnlock = "ach_dragon_slayer",
                    itemGain = InventoryItemEntity(
                        itemId = "crown_gold",
                        name = "تاج مرصع شاهانه",
                        description = "تاج پادشاهان کهن. سکه و اعتبار شگرف برای دارنده آن.",
                        itemType = "ARTIFACT",
                        iconName = "monetization_on"
                    ),
                    consequenceTextFa = "با چابکی خیره‌کننده تاج زرین را بدون بیدار کردن اژدها به چنگ آوردی!"
                ),
                StoryChoice(
                    id = "c2d_2",
                    textFa = "رام کردن اژدها با طلسم رام‌کننده",
                    nextChapterId = "ch_throne_boss",
                    xpReward = 90,
                    coinReward = 50,
                    manaChange = -20,
                    achievementToUnlock = "ach_dragon_slayer",
                    consequenceTextFa = "اژدها چشمان درخشانش را گشود و در برابر جادوی تو سر فرود آورد."
                )
            )
        ),
        "ch_throne_boss" to StoryChapter(
            id = "ch_throne_boss",
            storyId = "story_castle",
            storyTitleFa = "افسانه قلعه تاریک سایه‌ها",
            chapterTitleFa = "نبرد سرنوشت‌ساز در تالار پادشاه سایه‌ها",
            textFa = """
                وارد تالار عظیم تخت پادشاهی شدی. پادشاه سایه‌ها با تاج و عبای فیروزه‌ای تاریک بر تخت تکیه زده است. خنجری بنفش در دست دارد و جادوی سیاهش کل سالن را میلرزاند.
                
                او لبخند سردی می‌زند: «ای قهرمان جاویدان! سرنوشت این سرزمین اکنون در دستان توست. آیا به من می‌پیوندی یا خاکستر خواهی شد؟»
            """.trimIndent(),
            illustrationResName = "img_story_castle",
            choices = listOf(
                StoryChoice(
                    id = "cboss_1",
                    textFa = "شلیک ضربه نهایی و نابودی پادشاه سایه‌ها",
                    nextChapterId = "ch_ending_hero",
                    xpReward = 200,
                    coinReward = 300,
                    achievementToUnlock = "ach_castle_master",
                    consequenceTextFa = "با ضربه‌ای افسانه‌ای قلب پادشاه سایه‌ها شکافته شد و قلعه از نفرین رهایی یافت!"
                ),
                StoryChoice(
                    id = "cboss_2",
                    textFa = "مهر و موم کردن پادشاه سایه‌ها با جادوی ابدی",
                    nextChapterId = "ch_ending_seal",
                    xpReward = 180,
                    coinReward = 200,
                    manaChange = -30,
                    consequenceTextFa = "حلقه‌های نورانی مقدس پادشاه را محصور کرده و او را برای هزار سال به خواب فرو بردند."
                )
            )
        ),
        "ch_ending_hero" to StoryChapter(
            id = "ch_ending_hero",
            storyId = "story_castle",
            storyTitleFa = "افسانه قلعه تاریک سایه‌ها",
            chapterTitleFa = "پایان حماسی: منجی سرزمین سایه‌ها",
            textFa = """
                خورشید زرین بر فراز قلعه طلوع می‌کند و تاریکی و نفرین جای خود را به نور و آرامش می‌دهد. مردم سرزمین نام تو را به عنوان بزرگ‌ترین قهرمان تاریخ زمزمه می‌کنند.
                
                تو فرمانروای جدید و عادل این سرزمین شدی!
            """.trimIndent(),
            illustrationResName = "img_story_castle",
            choices = emptyList(),
            isEnding = true,
            endingTitleFa = "پیروزی پیروزمندانه و جاودانه"
        ),
        "ch_ending_seal" to StoryChapter(
            id = "ch_ending_seal",
            storyId = "story_castle",
            storyTitleFa = "افسانه قلعه تاریک سایه‌ها",
            chapterTitleFa = "پایان حماسی: نگهبان مهر و موم باستانی",
            textFa = """
                پادشاه سایه‌ها در گوی بلورین ابدی زندانی شد. تو وظیفه نگهبانی از این مهر و موم باستانی را به عهده گرفتی تا صلح به سرزمین بازگردد.
            """.trimIndent(),
            illustrationResName = "img_story_forest",
            choices = emptyList(),
            isEnding = true,
            endingTitleFa = "نگهبان ابدی رازهای تاریک"
        )
    )

    private val prebuiltForestStory = mapOf(
        "ch_1" to StoryChapter(
            id = "ch_1",
            storyId = "story_forest",
            storyTitleFa = "راز جنگل نفرین‌شده",
            chapterTitleFa = "ورود به دهانه جنگل زمردین",
            textFa = """
                مه غلیظی لابلای درختان کهن جنگل نفرین‌شده شناور است. قارچ‌های درخشان فیروزه‌ای مسیرهای باریک را روشن کرده‌اند.
                
                صدای آواز پریان جنگلی و زمزمه جادوگران کهن از عمق جنگل به گوش می‌رسد...
            """.trimIndent(),
            illustrationResName = "img_story_forest",
            choices = listOf(
                StoryChoice(
                    id = "fc1_1",
                    textFa = "دنبال کردن نور قارچ‌های فیروزه‌ای به سمت محراب کهن",
                    nextChapterId = "ch_altar",
                    xpReward = 40,
                    coinReward = 20,
                    itemGain = InventoryItemEntity(
                        itemId = "scroll_nature",
                        name = "طومار جادوی طبیعت",
                        description = "قدرت شفابخشی جادویی عناصر طبیعت.",
                        itemType = "SCROLL",
                        manaBonus = 20
                    )
                ),
                StoryChoice(
                    id = "fc1_2",
                    textFa = "عبور مستقیم از رودخانه خروشان غار کریستال",
                    nextChapterId = "ch_cave",
                    xpReward = 35,
                    coinReward = 30
                )
            )
        ),
        "ch_altar" to StoryChapter(
            id = "ch_altar",
            storyId = "story_forest",
            storyTitleFa = "راز جنگل نفرین‌شده",
            chapterTitleFa = "محراب جادویی روح جنگل",
            textFa = """
                به محراب سنگی در مرکز جنگل رسیدی. روح نگهبان جنگل به شکل آهویی درخشان ظاهر می‌شود و به تو نگاه می‌کند.
            """.trimIndent(),
            illustrationResName = "img_story_forest",
            choices = listOf(
                StoryChoice(
                    id = "fc2_1",
                    textFa = "ادای احترام و دریافت برکت جنگل",
                    nextChapterId = "ch_forest_ending",
                    xpReward = 100,
                    coinReward = 100,
                    achievementToUnlock = "ach_first_step"
                )
            )
        ),
        "ch_cave" to StoryChapter(
            id = "ch_cave",
            storyId = "story_forest",
            storyTitleFa = "راز جنگل نفرین‌شده",
            chapterTitleFa = "غار بلورین خفاش‌های فیروزه‌ای",
            textFa = """
                غار مملو از کریستال‌های بنفش و زلال است. چشمه‌ای از آب حیات در میان غار می‌جوشد.
            """.trimIndent(),
            illustrationResName = "img_story_dungeon",
            choices = listOf(
                StoryChoice(
                    id = "fc2_2",
                    textFa = "نوشیدن از آب چشمه حیات",
                    nextChapterId = "ch_forest_ending",
                    xpReward = 90,
                    coinReward = 80,
                    hpChange = 50,
                    manaChange = 50
                )
            )
        ),
        "ch_forest_ending" to StoryChapter(
            id = "ch_forest_ending",
            storyId = "story_forest",
            storyTitleFa = "راز جنگل نفرین‌شده",
            chapterTitleFa = "پایان: دوستدار طبیعت و قهرمان زمردین",
            textFa = """
                راز جنگل نفرین‌شده بر تو آشکار شد و صلح و شادابی به درختان کهن بازگشت.
            """.trimIndent(),
            illustrationResName = "img_story_forest",
            choices = emptyList(),
            isEnding = true,
            endingTitleFa = "حامی زمردین جنگل‌ها"
        )
    )

    suspend fun toggleEquipItem(item: InventoryItemEntity): String {
        val profile = storyDao.getCharacterProfileDirect() ?: return "شخصیتی پیدا نشد."
        
        if (item.isEquipped) {
            storyDao.setItemEquipped(item.id, false)
            val updatedProfile = when (item.slotType) {
                "WEAPON" -> profile.copy(equippedWeaponName = null)
                "ARTIFACT" -> profile.copy(equippedArtifactName = null)
                else -> profile
            }
            storyDao.saveCharacterProfile(updatedProfile)
            return "آیتم ${item.name} از تجهیزات خارج شد."
        } else {
            if (item.slotType == "NONE") {
                return "این آیتم قابلیت تجهیز ندارد."
            }
            // Unequip all items in same slot
            storyDao.unequipAllInSlot(item.slotType)
            storyDao.setItemEquipped(item.id, true)
            
            val updatedProfile = when (item.slotType) {
                "WEAPON" -> profile.copy(equippedWeaponName = item.name)
                "ARTIFACT" -> profile.copy(equippedArtifactName = item.name)
                else -> profile
            }
            storyDao.saveCharacterProfile(updatedProfile)
            return "آیتم ${item.name} با موفقیت تجهیز شد!"
        }
    }

    suspend fun useConsumableItem(item: InventoryItemEntity): String {
        val profile = storyDao.getCharacterProfileDirect() ?: return "شخصیت یافت نشد."
        val newHp = (profile.hp + item.hpBonus).coerceIn(0, profile.maxHp)
        val newMana = (profile.mana + item.manaBonus).coerceIn(0, profile.maxMana)
        
        val updated = profile.copy(hp = newHp, mana = newMana)
        storyDao.saveCharacterProfile(updated)
        storyDao.deleteItem(item.id)
        return "از ${item.name} استفاده کردید. سلامتی/مانا بازسازی شد!"
    }

    // --- GEMINI AI STORY GENERATOR (Prepared for AI-generated stories) ---
    suspend fun generateAiStoryChapter(
        promptUserIntent: String,
        heroName: String,
        heroClass: String,
        heroLevel: Int
    ): StoryChapter = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Smart offline fallback chapter if no API key configured
            return@withContext StoryChapter(
                id = "ai_ch_${System.currentTimeMillis()}",
                storyId = "story_ai",
                storyTitleFa = "ماجرای هوشمند خلق‌شده",
                chapterTitleFa = "کشف رویای جدید $heroName",
                textFa = """
                    قهرمان گرانقدر $heroName ($heroClass level $heroLevel)!
                    
                    در اثر تمرکز و جستجو در $promptUserIntent، درگاهی جادویی در برابر تو گشوده شد.
                    موجی از انرژی کیهانی دور تا دور تو می‌چرخد و سه مسیر سرنوشت‌ساز جدید پدیدار می‌شوند.
                """.trimIndent(),
                illustrationResName = "img_story_castle",
                choices = listOf(
                    StoryChoice(
                        id = "ai_c1",
                        textFa = "پیشروی در نبرد کیهانی با قدرت شگفت‌انگیز",
                        nextChapterId = "ch_throne_boss",
                        xpReward = 50,
                        coinReward = 30
                    ),
                    StoryChoice(
                        id = "ai_c2",
                        textFa = "استفاده از معجون جادویی و مراقبه در سایه‌ها",
                        nextChapterId = "ch_archive",
                        xpReward = 40,
                        coinReward = 20
                    )
                )
            )
        }

        try {
            val systemPrompt = """
                You are a master Dark Fantasy Visual Novel storyteller writing in elegant Persian (Farsi).
                Hero Name: $heroName, Class: $heroClass, Level: $heroLevel.
                Scenario / User Request: $promptUserIntent.
                Generate a single JSON object with the following fields:
                {
                  "chapterTitleFa": "Title in Persian",
                  "textFa": "Engaging Persian dark fantasy story text paragraph (approx 100 words)",
                  "choices": [
                    { "id": "c1", "textFa": "Persian choice 1", "xpReward": 40, "coinReward": 25 },
                    { "id": "c2", "textFa": "Persian choice 2", "xpReward": 35, "coinReward": 20 }
                  ]
                }
                Return ONLY raw JSON, no markdown formatting.
            """.trimIndent()

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val reqJson = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", systemPrompt)
                    }))
                }))
            }

            val request = Request.Builder()
                .url(url)
                .post(reqJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = httpClient.newCall(request).execute()
            val respBody = response.body?.string() ?: ""
            if (!response.isSuccessful || respBody.isBlank()) {
                throw IllegalStateException("Gemini API Error: HTTP ${response.code}")
            }

            val jsonRoot = JSONObject(respBody)
            val candidates = jsonRoot.optJSONArray("candidates")
            val rawText = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: ""

            if (rawText.isBlank()) {
                throw IllegalStateException("Empty response text from Gemini API")
            }

            val cleanedJson = rawText.replace("```json", "").replace("```", "").trim()
            val parsed = JSONObject(cleanedJson)

            val chTitle = parsed.optString("chapterTitleFa", "فصل جدید کیهانی")
            val storyText = parsed.optString("textFa", "سفری جدید آغاز شد...")
            val choicesArray = parsed.optJSONArray("choices")
            val choicesList = mutableListOf<StoryChoice>()

            if (choicesArray != null) {
                for (i in 0 until choicesArray.length()) {
                    val obj = choicesArray.getJSONObject(i)
                    choicesList.add(
                        StoryChoice(
                            id = obj.optString("id", "c_$i"),
                            textFa = obj.optString("textFa", "پیشروی"),
                            nextChapterId = if (i == 0) "ch_throne_boss" else "ch_archive",
                            xpReward = obj.optInt("xpReward", 30),
                            coinReward = obj.optInt("coinReward", 20)
                        )
                    )
                }
            }

            return@withContext StoryChapter(
                id = "ai_ch_${System.currentTimeMillis()}",
                storyId = "story_ai",
                storyTitleFa = "داستان هوشمند جاویدان",
                chapterTitleFa = chTitle,
                textFa = storyText,
                illustrationResName = "img_story_castle",
                choices = choicesList.ifEmpty {
                    listOf(
                        StoryChoice(id = "c1", textFa = "ادامه ماجراجویی", nextChapterId = "ch_throne_boss", xpReward = 30)
                    )
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext StoryChapter(
                id = "ai_err_${System.currentTimeMillis()}",
                storyId = "story_ai",
                storyTitleFa = "ماجرای هوشمند",
                chapterTitleFa = "پدیدار شدن سرنوشت",
                textFa = "در مواجهه با جادوی $promptUserIntent، قهرمان شجاع $heroName مسیر نوینی را تجربه می‌کند.",
                illustrationResName = "img_story_castle",
                choices = listOf(
                    StoryChoice(id = "c1", textFa = "پیشروی به تالار اصلی", nextChapterId = "ch_throne_boss", xpReward = 30)
                )
            )
        }
    }
}
