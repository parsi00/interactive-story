package com.example.domain

import com.example.data.InventoryItemEntity

data class StoryChoice(
    val id: String,
    val textFa: String,
    val nextChapterId: String,
    val requiredClass: String? = null, // "WARRIOR", "MAGE", "ARCHER"
    val requiredStat: String? = null,  // "STRENGTH", "AGILITY", "MAGIC"
    val requiredMinStatValue: Int = 0,
    val requiredRelationshipFaction: String? = null, // "SHADOW_GUARDIAN", "FOREST_SPIRIT", "ROYAL_GUARD"
    val requiredMinRelationship: Int = 0,
    val xpReward: Int = 25,
    val coinReward: Int = 15,
    val hpChange: Int = 0,
    val manaChange: Int = 0,
    val relShadowGuardianChange: Int = 0,
    val relForestSpiritChange: Int = 0,
    val relRoyalGuardChange: Int = 0,
    val itemGain: InventoryItemEntity? = null,
    val achievementToUnlock: String? = null,
    val consequenceTextFa: String? = null
)

data class StoryChapter(
    val id: String,
    val storyId: String,
    val storyTitleFa: String,
    val chapterTitleFa: String,
    val textFa: String,
    val illustrationResName: String, // "img_story_castle", "img_story_forest", "img_story_dungeon"
    val choices: List<StoryChoice>,
    val isEnding: Boolean = false,
    val endingTitleFa: String? = null
)
