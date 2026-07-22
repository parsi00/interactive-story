package com.example.domain

enum class CharacterClassType(
    val titleFa: String,
    val descriptionFa: String,
    val defaultHp: Int,
    val defaultMana: Int,
    val strength: Int,
    val agility: Int,
    val magic: Int,
    val iconName: String,
    val abilityNameFa: String,
    val abilityDescriptionFa: String,
    val manaCost: Int
) {
    WARRIOR(
        titleFa = "جنگجو",
        descriptionFa = "استاد نبرد تن به تن با زره سنگین و قدرتی خدشه‌ناپذیر. سلامت بالا و آسیب فیزیکی مهلک.",
        defaultHp = 130,
        defaultMana = 40,
        strength = 16,
        agility = 10,
        magic = 6,
        iconName = "shield",
        abilityNameFa = "ضربه‌ خشمگین اژدها",
        abilityDescriptionFa = "شکستن موانع سنگی و نبرد با تمام نیرو (کاهش ۱۵ مانا و عبور از خطرات نبرد)",
        manaCost = 15
    ),
    MAGE(
        titleFa = "جادوگر",
        descriptionFa = "دانشور نیروهای تاریک و باستانی. گنجینه جادو و مانای بیکران برای نابودی دشمنان با وردهای آتشین.",
        defaultHp = 90,
        defaultMana = 120,
        strength = 6,
        agility = 10,
        magic = 18,
        iconName = "auto_awesome",
        abilityNameFa = "سپر مانایی و رازگشایی آرکین",
        abilityDescriptionFa = "رمزگشایی طلسم‌های باستانی و ترمیم ۵۰ واحد مانا و خنثی‌سازی تله‌ها",
        manaCost = 20
    ),
    ARCHER(
        titleFa = "تیرانداز",
        descriptionFa = "شکارچی چابک سایه‌ها. فرز و تیزبین با شلیک‌های مرگبار از فاصله دور و مهارت در تله‌گذاری.",
        defaultHp = 105,
        defaultMana = 70,
        strength = 11,
        agility = 17,
        magic = 8,
        iconName = "ads_click",
        abilityNameFa = "تیر سایه و تمرکز شبانه",
        abilityDescriptionFa = "شلیک نفوذی بی‌صدا از چابکی سایه و افزایش شانس دریافت گنجینه (کاهش ۱۵ مانا)",
        manaCost = 15
    )
}

