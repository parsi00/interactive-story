package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CharacterProfileEntity
import com.example.domain.CharacterClassType
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    profile: CharacterProfileEntity?,
    onNavigateToNewStory: () -> Unit,
    onNavigateToContinue: () -> Unit,
    onNavigateToAiStory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Decorative background image with gradient fade
        Image(
            painter = painterResource(id = R.drawable.img_splash_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentScale = ContentScale.Crop,
            alpha = 0.35f
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(285.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            DarkBackground.copy(alpha = 0.8f),
                            DarkBackground
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "داستان تعاملی",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldPrimary
                        )
                        Text(
                            text = "جهان افسانه‌ای سایه‌ها",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        color = DarkSurfaceVariant,
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                    ) {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "تنظیمات",
                                tint = GoldPrimary
                            )
                        }
                    }
                }
            }

            // Hero Profile Card Preview (if profile exists)
            item {
                if (profile != null) {
                    val classEnum = try {
                        CharacterClassType.valueOf(profile.className)
                    } catch (e: Exception) {
                        CharacterClassType.WARRIOR
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToProfile() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
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
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = MysticalPurple.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = classEnum.titleFa,
                                            fontSize = 11.sp,
                                            color = MysticalPurple,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "سطح ${profile.level}",
                                        fontSize = 13.sp,
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "🪙 ${profile.coins} سکه",
                                        fontSize = 13.sp,
                                        color = XpGold
                                    )
                                    Text(
                                        text = "❤️ ${profile.hp}/${profile.maxHp}",
                                        fontSize = 13.sp,
                                        color = HpRed
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "منوی اصلی ماجراجویی",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            }

            // Continue Story Button
            item {
                HomeMenuItemCard(
                    title = "ادامه داستان",
                    subtitle = if (profile != null) "ادامه ماجراجویی از ${profile.currentStoryId}" else "هیچ داستان ذخیره‌شده‌ای یافت نشد",
                    icon = Icons.Default.PlayArrow,
                    accentColor = GoldPrimary,
                    enabled = profile != null,
                    onClick = onNavigateToContinue
                )
            }

            // New Story Button
            item {
                HomeMenuItemCard(
                    title = "داستان جدید & خلق شخصیت",
                    subtitle = "انتخاب نام، جنسیت، آواتار و کلاس قهرمانی جدید",
                    icon = Icons.Default.Add,
                    accentColor = EmeraldGreen,
                    onClick = onNavigateToNewStory
                )
            }

            // AI Story Generator
            item {
                HomeMenuItemCard(
                    title = "مولد داستان هوشمند (AI)",
                    subtitle = "خلق ماجراها و سناریوهای جدید با هوش مصنوعی جمینای",
                    icon = Icons.Default.AutoAwesome,
                    accentColor = MysticalPurple,
                    onClick = onNavigateToAiStory
                )
            }

            // Profile & Achievements
            item {
                HomeMenuItemCard(
                    title = "پروفایل، کوله‌پشتی و دستاوردها",
                    subtitle = "مشاهده کوله‌پشتی، آمار شخصیت، مدال‌ها و سکه‌ها",
                    icon = Icons.Default.Person,
                    accentColor = CrimsonRed,
                    onClick = onNavigateToProfile
                )
            }

            // Settings
            item {
                HomeMenuItemCard(
                    title = "تنظیمات بازی",
                    subtitle = "مدیریت صدا، اعلانات و بازنشانی اطلاعات",
                    icon = Icons.Default.Settings,
                    accentColor = TextSecondary,
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
fun HomeMenuItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) DarkSurface else DarkSurface.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (enabled) accentColor.copy(alpha = 0.6f) else DarkCardBorder
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) accentColor else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) TextPrimary else TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = if (enabled) TextSecondary else TextMuted
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = if (enabled) accentColor else TextMuted
            )
        }
    }
}
