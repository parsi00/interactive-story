package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CharacterClassType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (String, String, Int, CharacterClassType) -> Unit,
    onNavigateBack: () -> Unit
) {
    var heroName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("مرد") }
    var selectedAvatarIndex by remember { mutableIntStateOf(0) }
    var selectedClass by remember { mutableStateOf(CharacterClassType.WARRIOR) }

    val genders = listOf("مرد", "زن", "نامشخص")
    val avatarIcons = listOf(
        Icons.Default.Shield to "جنگجوی سرسخت",
        Icons.Default.AutoAwesome to "استاد جادو",
        Icons.Default.AdsClick to "تیرانداز سایه",
        Icons.Default.Person to "مسافر ناشناس"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ساخت شخصیت جدید", color = GoldPrimary, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Step 1: Hero Name Input
            item {
                Text(
                    text = "۱. نام قهرمان خود را وارد کنید",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = heroName,
                    onValueChange = { heroName = it },
                    placeholder = { Text("مثال: آرتور سایه‌شکن", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Step 2: Gender Selection
            item {
                Text(
                    text = "۲. جنسیت قهرمان",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    genders.forEach { gender ->
                        val isSelected = selectedGender == gender
                        Surface(
                            selected = isSelected,
                            onClick = { selectedGender = gender },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) GoldPrimary else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) GoldPrimary else DarkCardBorder
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = gender,
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 12.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Step 3: Avatar Selection
            item {
                Text(
                    text = "۳. انتخاب نماد و آواتار",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(avatarIcons) { index, (icon, label) ->
                        val isSelected = selectedAvatarIndex == index
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) DarkSurfaceVariant else DarkSurface)
                                .border(
                                    2.dp,
                                    if (isSelected) GoldPrimary else DarkCardBorder,
                                    CircleShape
                                )
                                .clickable { selectedAvatarIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) GoldPrimary else TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            // Step 4: Class Selection
            item {
                Text(
                    text = "۴. انتخاب کلاس قهرمانی",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CharacterClassType.values().forEach { classType ->
                        val isSelected = selectedClass == classType
                        val borderColor by animateColorAsState(
                            if (isSelected) GoldPrimary else DarkCardBorder, label = "border"
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DarkSurfaceVariant else DarkSurface
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedClass = classType }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (classType) {
                                                CharacterClassType.WARRIOR -> Icons.Default.Shield
                                                CharacterClassType.MAGE -> Icons.Default.AutoAwesome
                                                CharacterClassType.ARCHER -> Icons.Default.AdsClick
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) GoldPrimary else TextMuted,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = classType.titleFa,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }

                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedClass = classType },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                    )
                                }

                                Text(
                                    text = classType.descriptionFa,
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                                )

                                Divider(color = DarkCardBorder)

                                Spacer(modifier = Modifier.height(8.dp))

                                // Stat Bars
                                StatBarRow(label = "سلامت (HP)", value = classType.defaultHp, max = 150, color = HpRed)
                                StatBarRow(label = "مانیا (MP)", value = classType.defaultMana, max = 150, color = ManaBlue)
                                StatBarRow(label = "قدرت", value = classType.strength, max = 20, color = GoldPrimary)
                                StatBarRow(label = "چابکی", value = classType.agility, max = 20, color = EmeraldGreen)
                                StatBarRow(label = "جادو", value = classType.magic, max = 20, color = MysticalPurple)
                            }
                        }
                    }
                }
            }

            // Begin Fate Button
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        onCharacterCreated(
                            heroName.ifBlank { "قهرمان ناشناس" },
                            selectedGender,
                            selectedAvatarIndex,
                            selectedClass
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "آغاز سرنوشت و ورود به داستان",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatBarRow(
    label: String,
    value: Int,
    max: Int,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.width(80.dp)
        )
        LinearProgressIndicator(
            progress = { (value.toFloat() / max.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = DarkBackground
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
