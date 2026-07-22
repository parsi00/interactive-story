package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiStoryGeneratorScreen(
    isGenerating: Boolean,
    onGenerateRequested: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var userPrompt by remember { mutableStateOf("") }

    val presetPrompts = listOf(
        "نبرد حماسی با اژدهای بلورین در قله‌های برفی",
        "کشف معبد باستانی جادوگران در دل کویر سیاه",
        "مواجهه با ارواح سرگردان در کشتی دزدان دریایی سایه",
        "رمزگشایی از گوی زمان و سفر به آینده فانتزی"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مولد داستان هوشمند (Gemini AI)", color = GoldPrimary, fontWeight = FontWeight.Bold) },
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
            // Header Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MysticalPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MysticalPurple,
                        modifier = Modifier
                            .size(36.dp)
                            .then(if (isGenerating) Modifier.rotate(rotationAngle) else Modifier)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "داستان‌ساز نامحدود هوش مصنوعی",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "موضوع، دشمنان یا موقعیت دلخواه خود را بنویسید تا هوش مصنوعی یک فصل کامل با انتخاب‌های مؤثر بسازد.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Input Field
            Text(
                text = "ایده یا سناریوی جدید خود را وارد کنید:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )

            OutlinedTextField(
                value = userPrompt,
                onValueChange = { userPrompt = it },
                placeholder = { Text("مثال: ورود به غار اژدها و نبرد با جادوگران...", color = TextMuted) },
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MysticalPurple,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Preset Prompts Chips
            Text(
                text = "یا از پیشنهادات سریع استفاده کنید:",
                fontSize = 13.sp,
                color = TextMuted
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                presetPrompts.forEach { prompt ->
                    OutlinedButton(
                        onClick = { userPrompt = prompt },
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface, contentColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "✦ $prompt", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Button
            Button(
                onClick = {
                    if (userPrompt.isNotBlank()) {
                        onGenerateRequested(userPrompt)
                    }
                },
                enabled = userPrompt.isNotBlank() && !isGenerating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MysticalPurple,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("در حال نگارش داستان هوشمند...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Psychology, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("خلق فصل جدید با هوش مصنوعی", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
