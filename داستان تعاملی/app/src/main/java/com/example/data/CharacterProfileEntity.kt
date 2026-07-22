package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_profile")
data class CharacterProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val gender: String, // مرد, زن, نامشخص
    val avatarIndex: Int,
    val className: String, // WARRIOR, MAGE, ARCHER
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 100,
    val hp: Int = 100,
    val maxHp: Int = 100,
    val mana: Int = 50,
    val maxMana: Int = 50,
    val strength: Int = 10,
    val agility: Int = 10,
    val magic: Int = 10,
    val currentStoryId: String = "story_castle",
    val currentChapterId: String = "ch_1",
    val completedStoriesCount: Int = 0,
    val isFirebaseSynced: Boolean = false,

    // Equipment tracking
    val equippedWeaponName: String? = null,
    val equippedArtifactName: String? = null,

    // Relationship tracking (0..100)
    val relShadowGuardian: Int = 50,
    val relForestSpirit: Int = 50,
    val relRoyalGuard: Int = 50,

    // Notification Preferences
    val notifyStoryEvents: Boolean = true,
    val notifyMilestones: Boolean = true,
    val notifyAchievements: Boolean = true
)

