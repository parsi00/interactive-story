package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    soundEnabled: Boolean,
    notifyStoryEvents: Boolean = true,
    notifyMilestones: Boolean = true,
    notifyAchievements: Boolean = true,
    onToggleSound: () -> Unit,
    onUpdateNotificationPrefs: (Boolean, Boolean, Boolean) -> Unit = { _, _, _ -> },
    onResetGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    var localNotifyStory by remember(notifyStoryEvents) { mutableStateOf(notifyStoryEvents) }
    var localNotifyMilestones by remember(notifyMilestones) { mutableStateOf(notifyMilestones) }
    var localNotifyAchievements by remember(notifyAchievements) { mutableStateOf(notifyAchievements) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات بازی", color = GoldPrimary, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Audio & Effects
            Text(
                text = "صدا و اعلانات هوشمند",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = null,
                                tint = GoldPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("جلوه‌های صوتی و موسیقی", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("پخش افکت صوتی هنگام انتخاب‌ها", fontSize = 12.sp, color = TextSecondary)
                            }
                        }

                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = { onToggleSound() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkBackground,
                                checkedTrackColor = GoldPrimary
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = DarkCardBorder
                    )

                    Text("ترجیحات اعلان‌های پوش / درون‌برنامه‌ای:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("اعلان‌های فصل جدید و رویدادها", fontSize = 13.sp, color = TextPrimary)
                        Switch(
                            checked = localNotifyStory,
                            onCheckedChange = {
                                localNotifyStory = it
                                onUpdateNotificationPrefs(localNotifyStory, localNotifyMilestones, localNotifyAchievements)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkBackground, checkedTrackColor = GoldPrimary)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("اعلان‌های مایلستون‌ها و ارتقاء سطح", fontSize = 13.sp, color = TextPrimary)
                        Switch(
                            checked = localNotifyMilestones,
                            onCheckedChange = {
                                localNotifyMilestones = it
                                onUpdateNotificationPrefs(localNotifyStory, localNotifyMilestones, localNotifyAchievements)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkBackground, checkedTrackColor = GoldPrimary)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("اعلان‌های مدال‌ها و دستاوردها", fontSize = 13.sp, color = TextPrimary)
                        Switch(
                            checked = localNotifyAchievements,
                            onCheckedChange = {
                                localNotifyAchievements = it
                                onUpdateNotificationPrefs(localNotifyStory, localNotifyMilestones, localNotifyAchievements)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkBackground, checkedTrackColor = GoldPrimary)
                        )
                    }
                }
            }

            // Section 2: Offline Database Status
            Text(
                text = "ذخیره‌سازی محلی (Offline Room)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = EmeraldGreen
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("پایگاه داده محلی SQLite / Room", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("تمام پیشرفت‌ها، کوله‌پشتی و سکه‌ها خودکار ذخیره می‌شوند.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            // Danger Zone: Reset
            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = { showResetDialog = true },
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("بازنشانی و پاک کردن پیشرفت بازی", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("بازنشانی پیشرفت بازی", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("آیا مطمئن هستید که می‌خواهید تمام پیشرفت، شخصیت، کوله‌پشتی و سکه‌های خود را پاک کنید؟ این عمل غیرقابل بازگشت است.", color = TextPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        onResetGame()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Text("بله، پاک شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}
