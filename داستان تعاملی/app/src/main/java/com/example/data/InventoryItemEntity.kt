package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: String,
    val name: String, // نام فارسی
    val description: String,
    val itemType: String, // WEAPON, POTION, SCROLL, ARTIFACT
    val slotType: String = "NONE", // WEAPON, ARTIFACT, NONE
    val isEquipped: Boolean = false,
    val quantity: Int = 1,
    val hpBonus: Int = 0,
    val manaBonus: Int = 0,
    val atkBonus: Int = 0,
    val strBonus: Int = 0,
    val agiBonus: Int = 0,
    val magBonus: Int = 0,
    val iconName: String = "sword"
)

