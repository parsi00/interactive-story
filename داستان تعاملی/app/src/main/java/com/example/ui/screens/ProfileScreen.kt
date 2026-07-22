package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AchievementEntity
import com.example.data.CharacterProfileEntity
import com.example.data.InventoryItemEntity
import com.example.domain.CharacterClassType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: CharacterProfileEntity?,
    inventory: List<InventoryItemEntity>,
    achievements: List<AchievementEntity>,
    onToggleEquip: (InventoryItemEntity) -> Unit = {},
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    if (profile == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("هیچ شخصیتی ساخته نشده است.", color = TextMuted)
        }
        return
    }

    val classEnum = try {
        CharacterClassType.valueOf(profile.className)
    } catch (e: Exception) {
        CharacterClassType.WARRIOR
    }

    val reqXpNext = profile.level * 100

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پروفایل و کوله‌پشتی", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceVariant)
                                .border(2.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (profile.avatarIndex) {
                                    1 -> Icons.Default.AutoAwesome
                                    2 -> Icons.Default.AdsClick
                                    else -> Icons.Default.Shield
                                },
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = profile.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        Text(
                            text = "${classEnum.titleFa} • جنسیت: ${profile.gender}",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // XP Bar Section
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("پیشرفت سطح ${profile.level}", fontSize = 12.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                                Text("${profile.xp}/$reqXpNext XP", fontSize = 12.sp, color = XpGold, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (profile.xp.toFloat() / reqXpNext.toFloat()).coerceIn(0f, 1f) },
                                color = XpGold,
                                trackColor = DarkBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Coins and Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBadgeItem(title = "سکه طلا", value = "🪙 ${profile.coins}", color = XpGold)
                            StatBadgeItem(title = "سلامتی", value = "❤️ ${profile.hp}/${profile.maxHp}", color = HpRed)
                            StatBadgeItem(title = "مانیا", value = "🔷 ${profile.mana}/${profile.maxMana}", color = ManaBlue)
                        }
                    }
                }
            }

            // Equipped Items Section
            item {
                Text(
                    text = "تجهیزات مجهز شده",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Weapon Slot
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MysticalPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = MysticalPurple, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("سلاح اصلی", fontSize = 12.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = profile.equippedWeaponName ?: "بدون سلاح",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (profile.equippedWeaponName != null) GoldPrimary else TextMuted
                            )
                        }
                    }

                    // Artifact Slot
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("یادگار باستانی", fontSize = 12.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = profile.equippedArtifactName ?: "بدون یادگار",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (profile.equippedArtifactName != null) EmeraldGreen else TextMuted
                            )
                        }
                    }
                }
            }

            // Relationship Status with Factions
            item {
                Text(
                    text = "روابط و محبوبیت با گروه‌ها (Branching Dynamics)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RelationshipRow(title = "نگهبان سایه (Shadow Guardian)", value = profile.relShadowGuardian, color = MysticalPurple)
                        RelationshipRow(title = "روح جنگل (Forest Spirit)", value = profile.relForestSpirit, color = EmeraldGreen)
                        RelationshipRow(title = "نگهبانان سلطنتی (Royal Guard)", value = profile.relRoyalGuard, color = XpGold)
                    }
                }
            }

            // Stats Matrix Cards
            item {
                Text(
                    text = "شاخص‌های ویژگی‌های رزمی",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMatrixCard(title = "قدرت", value = profile.strength, icon = Icons.Default.Shield, color = GoldPrimary, modifier = Modifier.weight(1f))
                    StatMatrixCard(title = "چابکی", value = profile.agility, icon = Icons.Default.AdsClick, color = EmeraldGreen, modifier = Modifier.weight(1f))
                    StatMatrixCard(title = "جادو", value = profile.magic, icon = Icons.Default.AutoAwesome, color = MysticalPurple, modifier = Modifier.weight(1f))
                }
            }

            // Tab Buttons (Inventory / Achievements)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TabButton(
                        text = "کوله‌پشتی (${inventory.size})",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "دستاوردها (${achievements.count { it.isUnlocked }}/${achievements.size})",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tab Content
            if (selectedTab == 0) {
                if (inventory.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "هیچ آیتمی در کوله‌پشتی یافت نشد.",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }
                } else {
                    items(inventory) { item ->
                        InventoryItemRow(item, onToggleEquip = onToggleEquip)
                    }
                }
            } else {
                items(achievements) { ach ->
                    AchievementRow(ach)
                }
            }
        }
    }
}

@Composable
fun StatBadgeItem(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 11.sp, color = TextMuted)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun StatMatrixCard(
    title: String,
    value: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 12.sp, color = TextMuted)
            Text(text = value.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) GoldPrimary else DarkSurface,
            contentColor = if (isSelected) DarkBackground else TextPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) GoldPrimary else DarkCardBorder
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RelationshipRow(title: String, value: Int, color: Color) {
    val statusText = when {
        value >= 75 -> "متحد صمیمی"
        value >= 50 -> "بی‌طرف"
        else -> "دشمن / متخاصم"
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(text = "$value/100 ($statusText)", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (value.toFloat() / 100f).coerceIn(0f, 1f) },
            color = color,
            trackColor = DarkBackground,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}

@Composable
fun InventoryItemRow(
    item: InventoryItemEntity,
    onToggleEquip: (InventoryItemEntity) -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isEquipped) GoldPrimary else DarkCardBorder
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (item.itemType) {
                    "WEAPON" -> Icons.Default.Shield
                    "POTION" -> Icons.Default.LocalHospital
                    "SCROLL" -> Icons.Default.AutoAwesome
                    else -> Icons.Default.MonetizationOn
                },
                contentDescription = null,
                tint = if (item.isEquipped) GoldPrimary else TextMuted,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    if (item.isEquipped) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = GoldPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "مجهز شده",
                                fontSize = 10.sp,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(text = item.description, fontSize = 12.sp, color = TextSecondary)
            }

            if (item.slotType != "NONE") {
                Button(
                    onClick = { onToggleEquip(item) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isEquipped) CrimsonRed else GoldPrimary,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (item.isEquipped) "خروج" else "تجهیز",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementRow(ach: AchievementEntity) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (ach.isUnlocked) DarkSurface else DarkSurface.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (ach.isUnlocked) GoldPrimary else DarkCardBorder
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (ach.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                contentDescription = null,
                tint = if (ach.isUnlocked) GoldPrimary else TextMuted,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ach.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (ach.isUnlocked) TextPrimary else TextMuted
                )
                Text(
                    text = ach.description,
                    fontSize = 12.sp,
                    color = if (ach.isUnlocked) TextSecondary else TextMuted
                )
            }

            if (ach.isUnlocked) {
                Surface(
                    color = XpGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "+${ach.rewardCoins} سکه",
                        fontSize = 11.sp,
                        color = XpGold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
