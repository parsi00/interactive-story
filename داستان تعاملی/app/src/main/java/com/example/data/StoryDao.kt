package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM character_profile WHERE id = 1")
    fun getCharacterProfile(): Flow<CharacterProfileEntity?>

    @Query("SELECT * FROM character_profile WHERE id = 1")
    suspend fun getCharacterProfileDirect(): CharacterProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCharacterProfile(profile: CharacterProfileEntity)

    @Query("SELECT * FROM inventory_items ORDER BY id DESC")
    fun getInventoryItems(): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItem(item: InventoryItemEntity)

    @Query("DELETE FROM inventory_items WHERE id = :id")
    suspend fun deleteItem(id: Int)

    @Query("SELECT * FROM inventory_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Int): InventoryItemEntity?

    @Query("UPDATE inventory_items SET isEquipped = 0 WHERE slotType = :slotType")
    suspend fun unequipAllInSlot(slotType: String)

    @Query("UPDATE inventory_items SET isEquipped = :isEquipped WHERE id = :id")
    suspend fun setItemEquipped(id: Int, isEquipped: Boolean)

    @Query("SELECT * FROM achievements")
    fun getAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :id")
    suspend fun unlockAchievement(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM character_profile")
    suspend fun clearProfile()

    @Query("DELETE FROM inventory_items")
    suspend fun clearInventory()
}
