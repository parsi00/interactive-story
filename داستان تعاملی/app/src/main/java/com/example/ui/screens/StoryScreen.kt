package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CharacterProfileEntity
import com.example.data.InventoryItemEntity
import com.example.domain.CharacterClassType
import com.example.domain.StoryChapter
import com.example.domain.StoryChoice
import com.example.ui.theme.*
import com.example.ui.viewmodels.StoryNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    profile: CharacterProfileEntity?,
    chapter: StoryChapter?,
    inventory: List<InventoryItemEntity>,
    notificationBanner: StoryNotification? = null,
    onDismissNotification: () -> Unit = {},
    onChoiceMade: (StoryChoice) -> Unit,
    onCustomChoiceSubmitted: (String) -> Unit = {},
    onUseItem: (InventoryItemEntity) -> Unit,
    onToggleEquip: (InventoryItemEntity) -> Unit = {},
    onUseSpecialAbility: () -> Unit = {},
    onNavigateHome: () -> Unit
) {
    var showInventorySheet by remember { mutableStateOf(false) }
    var customChoiceText by remember { mutableStateOf("") }

    if (profile == null || chapter == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "در حال بارگذاری و نگارش سرنوشت...",
                    color = GoldPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "هوش مصنوعی در حال بازسازی فصول داستان است",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        return
    }

    val classEnum = try {
        CharacterClassType.valueOf(profile.className)
    } catch (e: Exception) {
        CharacterClassType.WARRIOR
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .border(androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Top HUD Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateHome() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceVariant)
                                .border(1.dp, GoldPrimary, CircleShape),
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
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = GoldPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "سطح ${profile.level}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${classEnum.titleFa} • 🪙 ${profile.coins}",
                                fontSize = 12.sp,
                                color = XpGold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Inventory Button Trigger
                        OutlinedButton(
                            onClick = { showInventorySheet = true },
                            border = androidx.compose.foundation.BorderStroke(1.dp, MysticalPurple),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MysticalPurple),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Backpack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "کوله‌پشتی (${inventory.size})", fontSize = 12.sp)
                        }

                        IconButton(onClick = onNavigateHome) {
                            Icon(Icons.Default.Home, contentDescription = "خانه", tint = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // HP and Mana Status Bars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // HP Bar
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("سلامتی (HP)", fontSize = 10.sp, color = TextMuted)
                            Text("${profile.hp}/${profile.maxHp}", fontSize = 10.sp, color = HpRed, fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { (profile.hp.toFloat() / profile.maxHp.toFloat()).coerceIn(0f, 1f) },
                            color = HpRed,
                            trackColor = DarkBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }

                    // Mana Bar
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("مانیا (MP)", fontSize = 10.sp, color = TextMuted)
                            Text("${profile.mana}/${profile.maxMana}", fontSize = 10.sp, color = ManaBlue, fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { (profile.mana.toFloat() / profile.maxMana.toFloat()).coerceIn(0f, 1f) },
                            color = ManaBlue,
                            trackColor = DarkBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notification Banner Overlay Card
            notificationBanner?.let { notif ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (notif.iconName) {
                                    "emoji_events" -> Icons.Default.EmojiEvents
                                    "auto_awesome" -> Icons.Default.AutoAwesome
                                    else -> Icons.Default.NotificationsActive
                                },
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = notif.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                Text(text = notif.message, fontSize = 12.sp, color = TextPrimary)
                            }
                            IconButton(onClick = onDismissNotification) {
                                Icon(Icons.Default.Close, contentDescription = "بستن", tint = TextMuted)
                            }
                        }
                    }
                }
            }

            // Illustration Banner
            item {
                val imageRes = when (chapter.illustrationResName) {
                    "img_story_forest" -> R.drawable.img_story_forest
                    "img_story_dungeon" -> R.drawable.img_story_dungeon
                    else -> R.drawable.img_story_castle
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = chapter.chapterTitleFa,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Vignette
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            DarkBackground.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        // Chapter Title Badge
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                color = GoldPrimary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = chapter.storyTitleFa,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBackground,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = chapter.chapterTitleFa,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Parchment Story Text Box
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "روایت داستان",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        }

                        Text(
                            text = chapter.textFa,
                            fontSize = 16.sp,
                            color = TextPrimary,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            // Special Class Ability Card
            if (!chapter.isEnding) {
                item {
                    val canUseAbility = profile.mana >= classEnum.manaCost
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (canUseAbility) DarkSurface else DarkSurface.copy(alpha = 0.5f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (canUseAbility) MysticalPurple else DarkCardBorder
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (classEnum) {
                                    CharacterClassType.WARRIOR -> Icons.Default.Shield
                                    CharacterClassType.MAGE -> Icons.Default.AutoAwesome
                                    CharacterClassType.ARCHER -> Icons.Default.AdsClick
                                },
                                contentDescription = null,
                                tint = MysticalPurple,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "قدرت ویژه: ${classEnum.abilityNameFa}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = ManaBlue.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "${classEnum.manaCost} MP",
                                            fontSize = 10.sp,
                                            color = ManaBlue,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = classEnum.abilityDescriptionFa,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onUseSpecialAbility,
                                enabled = canUseAbility,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MysticalPurple,
                                    contentColor = TextPrimary,
                                    disabledContainerColor = DarkSurfaceVariant,
                                    disabledContentColor = TextMuted
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("اجرا", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Custom User Choice Action Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "✍️ اقدام اختصاصی خود را بنویسید (ادامه آزاد داستان)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = customChoiceText,
                                onValueChange = { customChoiceText = it },
                                placeholder = {
                                    Text(
                                        "مثلاً: با اژدها گفتگو می‌کنم و خنجر کهن را نجات می‌دهم...",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = DarkCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = DarkBackground,
                                    unfocusedContainerColor = DarkBackground
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (customChoiceText.isNotBlank()) {
                                        onCustomChoiceSubmitted(customChoiceText)
                                        customChoiceText = ""
                                    }
                                },
                                enabled = customChoiceText.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = DarkBackground,
                                    disabledContainerColor = DarkSurfaceVariant,
                                    disabledContentColor = TextMuted
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ارسال و ادامه سرنوشت", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Ending Chapter View or Branching Choices List
            if (chapter.isEnding) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = chapter.endingTitleFa ?: "پایان حماسی داستان",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "تبریک! ماجراجویی این بخش با موفقیت به پایان رسید. امتیازات، سکه‌ها و دستاوردهای شما در پروفایل ثبت شد.",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onNavigateHome,
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("بازگشت به منوی اصلی", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "تصمیم شما چیست؟",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(chapter.choices) { choice ->
                    val isClassMatch = choice.requiredClass == null || choice.requiredClass == profile.className
                    val isStatMatch = choice.requiredStat == null || when (choice.requiredStat) {
                        "STRENGTH" -> profile.strength >= choice.requiredMinStatValue
                        "AGILITY" -> profile.agility >= choice.requiredMinStatValue
                        "MAGIC" -> profile.magic >= choice.requiredMinStatValue
                        else -> true
                    }
                    val isEligible = isClassMatch && isStatMatch

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEligible) DarkSurface else DarkSurface.copy(alpha = 0.5f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isEligible) GoldPrimary.copy(alpha = 0.7f) else DarkCardBorder
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isEligible) {
                                onChoiceMade(choice)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SubdirectoryArrowLeft,
                                    contentDescription = null,
                                    tint = if (isEligible) GoldPrimary else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = choice.textFa,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEligible) TextPrimary else TextMuted,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Badges for requirements and rewards
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                choice.requiredClass?.let { reqClass ->
                                    val reqName = when (reqClass) {
                                        "WARRIOR" -> "جنگجو"
                                        "MAGE" -> "جادوگر"
                                        "ARCHER" -> "تیرانداز"
                                        else -> reqClass
                                    }
                                    Surface(
                                        color = if (isClassMatch) MysticalPurple.copy(alpha = 0.2f) else CrimsonRed.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "ویژه $reqName",
                                            fontSize = 10.sp,
                                            color = if (isClassMatch) MysticalPurple else CrimsonRed,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (choice.coinReward > 0) {
                                    Surface(
                                        color = XpGold.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "+${choice.coinReward} سکه",
                                            fontSize = 10.sp,
                                            color = XpGold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (choice.xpReward > 0) {
                                    Surface(
                                        color = EmeraldGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "+${choice.xpReward} XP",
                                            fontSize = 10.sp,
                                            color = EmeraldGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Inventory Modal Sheet
    if (showInventorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showInventorySheet = false },
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "کوله‌پشتی قهرمان (${inventory.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    IconButton(onClick = { showInventorySheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "بستن", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (inventory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "کوله‌پشتی شما هنوز خالی است. با انجام انتخاب‌ها آیتم‌ها را دریافت کنید.",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(inventory) { item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
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
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = item.description,
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    if (item.itemType == "POTION" || item.hpBonus > 0 || item.manaBonus > 0) {
                                        Button(
                                            onClick = {
                                                onUseItem(item)
                                                showInventorySheet = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = DarkBackground),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text("استفاده", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
