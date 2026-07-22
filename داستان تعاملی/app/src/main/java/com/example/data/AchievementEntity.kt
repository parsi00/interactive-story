package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String, // عنوان فارسی
    val description: String, // توضیحات فارسی
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val rewardCoins: Int = 50,
    val rewardXp: Int = 100,
    val iconName: String = "star"
)
