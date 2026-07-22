package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StoryNotification(
    val title: String,
    val message: String,
    val iconName: String = "notifications",
    val id: Long = System.currentTimeMillis()
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository

    val characterProfile: StateFlow<CharacterProfileEntity?>
    val inventoryItems: StateFlow<List<InventoryItemEntity>>
    val achievements: StateFlow<List<AchievementEntity>>

    private val _currentChapter = MutableStateFlow<StoryChapter?>(null)
    val currentChapter: StateFlow<StoryChapter?> = _currentChapter.asStateFlow()

    private val _toastEvent = MutableStateFlow<String?>(null)
    val toastEvent: StateFlow<String?> = _toastEvent.asStateFlow()

    private val _notificationBanner = MutableStateFlow<StoryNotification?>(null)
    val notificationBanner: StateFlow<StoryNotification?> = _notificationBanner.asStateFlow()

    private val _isGeneratingAiStory = MutableStateFlow(false)
    val isGeneratingAiStory: StateFlow<Boolean> = _isGeneratingAiStory.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _firebaseUserEmail = MutableStateFlow<String?>(null)
    val firebaseUserEmail: StateFlow<String?> = _firebaseUserEmail.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).storyDao()
        repository = GameRepository(dao)

        characterProfile = repository.characterProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        inventoryItems = repository.inventoryItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        achievements = repository.achievements.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.seedInitialAchievementsIfEmpty()

            characterProfile.collect { profile ->
                if (profile != null && _currentChapter.value == null) {
                    _currentChapter.value = repository.getChapter(profile.currentStoryId, profile.currentChapterId)
                }
            }
        }
    }

    fun clearToast() {
        _toastEvent.value = null
    }

    fun dismissNotification() {
        _notificationBanner.value = null
    }

    private fun postNotification(title: String, message: String, icon: String = "notifications", category: String = "STORY") {
        val profile = characterProfile.value ?: return
        val enabled = when (category) {
            "STORY" -> profile.notifyStoryEvents
            "MILESTONE" -> profile.notifyMilestones
            "ACHIEVEMENT" -> profile.notifyAchievements
            else -> true
        }
        if (enabled) {
            _notificationBanner.value = StoryNotification(title, message, icon)
        }
    }

    fun updateNotificationPreferences(story: Boolean, milestones: Boolean, achievements: Boolean) {
        val profile = characterProfile.value ?: return
        viewModelScope.launch {
            val updated = profile.copy(
                notifyStoryEvents = story,
                notifyMilestones = milestones,
                notifyAchievements = achievements
            )
            repository.saveCharacterProfile(updated)
            _toastEvent.value = "تنظیمات اعلان‌ها به‌روزرسانی شد."
        }
    }

    fun createCharacter(
        name: String,
        gender: String,
        avatarIndex: Int,
        classType: CharacterClassType
    ) {
        viewModelScope.launch {
            val profile = CharacterProfileEntity(
                id = 1,
                name = name.ifBlank { "قهرمان ناشناس" },
                gender = gender,
                avatarIndex = avatarIndex,
                className = classType.name,
                level = 1,
                xp = 0,
                coins = 100,
                hp = classType.defaultHp,
                maxHp = classType.defaultHp,
                mana = classType.defaultMana,
                maxMana = classType.defaultMana,
                strength = classType.strength,
                agility = classType.agility,
                magic = classType.magic,
                currentStoryId = "story_castle",
                currentChapterId = "ch_1"
            )

            repository.saveCharacterProfile(profile)
            _currentChapter.value = repository.getChapter("story_castle", "ch_1")
            _toastEvent.value = "شخصیت ${profile.name} با موفقیت خلق شد!"
            postNotification("قهرمان جدید بیدار شد!", "خوش آمدید ای ${profile.name}! سفر حماسی شما آغاز گشت.", "auto_awesome", "STORY")

            // Unlock first step achievement
            repository.unlockAchievement("ach_first_step")
        }
    }

    fun makeChoice(choice: StoryChoice) {
        val current = characterProfile.value ?: return

        viewModelScope.launch {
            // Calculate new stats
            var newXp = current.xp + choice.xpReward
            var newLevel = current.level
            var newCoins = current.coins + choice.coinReward
            var newHp = (current.hp + choice.hpChange).coerceIn(1, current.maxHp)
            var newMana = (current.mana + choice.manaChange).coerceIn(0, current.maxMana)

            var newRelShadow = (current.relShadowGuardian + choice.relShadowGuardianChange).coerceIn(0, 100)
            var newRelForest = (current.relForestSpirit + choice.relForestSpiritChange).coerceIn(0, 100)
            var newRelGuard = (current.relRoyalGuard + choice.relRoyalGuardChange).coerceIn(0, 100)

            val reqXpForNextLevel = newLevel * 100
            var levelUpOccurred = false
            if (newXp >= reqXpForNextLevel) {
                newLevel += 1
                newXp -= reqXpForNextLevel
                newHp = current.maxHp + 20
                newMana = current.maxMana + 20
                levelUpOccurred = true
            }

            // Save new item if present
            choice.itemGain?.let { item ->
                repository.addItemToInventory(item)
                postNotification("آیتم جدید کشف شد!", "شما ${item.name} را به کوله‌پشتی افزودید.", "inventory_2", "STORY")
            }

            // Unlock achievement if present
            choice.achievementToUnlock?.let { achId ->
                repository.unlockAchievement(achId)
                postNotification("دستاورد افتخارآمیز جدید!", "دستاورد ویژه $achId آزاد شد.", "emoji_events", "ACHIEVEMENT")
            }

            val updatedProfile = current.copy(
                level = newLevel,
                xp = newXp,
                coins = newCoins,
                hp = newHp,
                mana = newMana,
                relShadowGuardian = newRelShadow,
                relForestSpirit = newRelForest,
                relRoyalGuard = newRelGuard,
                currentChapterId = choice.nextChapterId
            )

            repository.saveCharacterProfile(updatedProfile)

            // Update chapter
            val nextChap = repository.getChapter(updatedProfile.currentStoryId, choice.nextChapterId)
            _currentChapter.value = nextChap

            if (nextChap.isEnding) {
                postNotification("پایان حماسی داستان!", "فصل پایانی: ${nextChap.endingTitleFa ?: "سرنوشت نهایی"}", "flag", "MILESTONE")
            } else if (levelUpOccurred) {
                postNotification("ارتقاء سطح قهرمان!", "تبارک الله! به سطح $newLevel صعود کردید.", "military_tech", "MILESTONE")
            } else if (choice.consequenceTextFa != null) {
                _toastEvent.value = choice.consequenceTextFa
            }
        }
    }

    fun submitCustomChoice(userCustomInput: String) {
        val current = characterProfile.value ?: return
        if (userCustomInput.isBlank()) return

        viewModelScope.launch {
            _isGeneratingAiStory.value = true
            try {
                val customChapter = repository.generateAiStoryChapter(
                    promptUserIntent = "انتخاب اختصاصی کاربر: $userCustomInput در فصل فعلی ${_currentChapter.value?.chapterTitleFa}",
                    heroName = current.name,
                    heroClass = current.className,
                    heroLevel = current.level
                )
                _currentChapter.value = customChapter

                // Reward for custom choice creativity
                val updated = current.copy(
                    xp = current.xp + 50,
                    coins = current.coins + 30
                )
                repository.saveCharacterProfile(updated)

                postNotification("ادامه داستان با اقدام اختصاصی!", "انتخاب شما: \"$userCustomInput\" به سرنوشت اضافه گشت.", "edit_note", "STORY")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGeneratingAiStory.value = false
            }
        }
    }

    fun toggleEquipItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            val resultMessage = repository.toggleEquipItem(item)
            _toastEvent.value = resultMessage
            postNotification("تغییر تجهیزات!", resultMessage, "shield", "STORY")
        }
    }

    fun useConsumableItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            val msg = repository.useConsumableItem(item)
            _toastEvent.value = msg
            postNotification("استفاده از معجون!", msg, "local_hospital", "STORY")
        }
    }

    fun useClassSpecialAbility() {
        val profile = characterProfile.value ?: return
        val classType = try { CharacterClassType.valueOf(profile.className) } catch (e: Exception) { CharacterClassType.WARRIOR }

        if (profile.mana < classType.manaCost) {
            _toastEvent.value = "مانای کافی برای اجرای قدرت ویژه (${classType.abilityNameFa}) ندارید!"
            return
        }

        viewModelScope.launch {
            val newMana = profile.mana - classType.manaCost
            val newXp = profile.xp + 40
            val updated = profile.copy(mana = newMana, xp = newXp)
            repository.saveCharacterProfile(updated)

            val abilityEffectMsg = when(classType) {
                CharacterClassType.WARRIOR -> "قدرت «${classType.abilityNameFa}» فعال شد! آسیب فیزیکی مهلک وارد گردید."
                CharacterClassType.MAGE -> "طلسم «${classType.abilityNameFa}» آزاد گشت! رمز جادویی کشف و جریان مانا غنی شد."
                CharacterClassType.ARCHER -> "مهارت «${classType.abilityNameFa}» اجرا شد! تیر نفوذی در تاریکی سایه‌ها نشسته بر هدف."
            }

            _toastEvent.value = abilityEffectMsg
            postNotification("اجرای قدرت ویژه کلاس!", abilityEffectMsg, classType.iconName, "STORY")
        }
    }

    fun useItem(item: InventoryItemEntity) {
        val current = characterProfile.value ?: return
        viewModelScope.launch {
            val newHp = (current.hp + item.hpBonus).coerceIn(0, current.maxHp)
            val newMana = (current.mana + item.manaBonus).coerceIn(0, current.maxMana)

            val updatedProfile = current.copy(
                hp = newHp,
                mana = newMana
            )

            repository.saveCharacterProfile(updatedProfile)
            repository.removeItemFromInventory(item.id)
            _toastEvent.value = "استفاده شد: ${item.name}"
        }
    }

    fun startStory(storyId: String) {
        val current = characterProfile.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                currentStoryId = storyId,
                currentChapterId = "ch_1"
            )
            repository.saveCharacterProfile(updated)
            _currentChapter.value = repository.getChapter(storyId, "ch_1")
        }
    }

    fun generateAiStory(userPrompt: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isGeneratingAiStory.value = true
            var current = characterProfile.value
            if (current == null) {
                val defaultProfile = CharacterProfileEntity(
                    id = 1,
                    name = "قهرمان شجاع",
                    gender = "MALE",
                    avatarIndex = 0,
                    className = "WARRIOR",
                    level = 1,
                    xp = 0,
                    coins = 100,
                    hp = 120,
                    maxHp = 120,
                    mana = 40,
                    maxMana = 40,
                    strength = 15,
                    agility = 10,
                    magic = 8,
                    currentStoryId = "story_ai",
                    currentChapterId = "ch_1"
                )
                repository.saveCharacterProfile(defaultProfile)
                current = defaultProfile
            }

            try {
                val aiChapter = repository.generateAiStoryChapter(
                    promptUserIntent = userPrompt,
                    heroName = current.name,
                    heroClass = current.className,
                    heroLevel = current.level
                )
                _currentChapter.value = aiChapter
                _toastEvent.value = "فصل جدید هوش مصنوعی با موفقیت خلق شد!"
                postNotification("فصل جدید خلق شد!", "داستان هوشمند بر اساس ایده شما ساخته شد.", "auto_awesome", "STORY")
            } catch (e: Exception) {
                e.printStackTrace()
                _toastEvent.value = "خطا در برقراری ارتباط با AI. فصل جایگزین بارگذاری گشت."
            } finally {
                _isGeneratingAiStory.value = false
                onComplete()
            }
        }
    }

    fun toggleSound() {
        _soundEnabled.value = !_soundEnabled.value
    }

    fun resetGame() {
        viewModelScope.launch {
            repository.resetGameProgress()
            _currentChapter.value = null
            _toastEvent.value = "پیشرفت بازی با موفقیت بازنشانی شد."
        }
    }
}
