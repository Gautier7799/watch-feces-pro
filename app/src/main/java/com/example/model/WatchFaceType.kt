package com.example.model

import androidx.compose.ui.graphics.Color

enum class WatchFaceType(
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String
) {
    KINETIC_SPHERE(
        titleAr = "المجال الحركي",
        titleEn = "Kinetic Sphere",
        descriptionAr = "خلفية الجسيمات الكروية ثلاثية الأبعاد مع مؤشر ثواني أخضر نيون وويدجت الطقس والتاريخ"
    ),
    EVERYDAY_ANALOG(
        titleAr = "القرص اليومي",
        titleEn = "Everyday Analog",
        descriptionAr = "قرص كلاسيكي مع أقواس دائرية للبطارية ونبض القلب والخطوات والساعة الرقمية العلوية"
    ),
    CLASSIC_CHRONOGRAPH(
        titleAr = "كرونوغراف كلاسيكي",
        titleEn = "Classic Chronograph",
        descriptionAr = "تصميم احترافي بـ 3 أقراص فرعية (ساعات، دقائق، ثواني) ومؤشرات دقيقة للأداء"
    ),
    DIVER_SPORT(
        titleAr = "غواص رياضي كلاسيكي",
        titleEn = "Sport Diver Dial",
        descriptionAr = "خلفية رياضية فخمة من ألياف الكربون مع علامات مضيئة وحلقات توقيت دائرية"
    ),
    GALAXY_NEBULA(
        titleAr = "المجرة الكونية",
        titleEn = "Cosmic Galaxy",
        descriptionAr = "سديم فضائي ساحر مع نجوم وكوكبات مضيئة وتدريج حافة دائري أنيق"
    ),
    CUSTOM_PHOTO(
        titleAr = "صورة مخصصة من هاتفك",
        titleEn = "Custom Photo Dial",
        descriptionAr = "اختر أي صورة أو تصميم من معرض هاتفك وضعه كخلفية مباشرة للساعة مع عقارب حية"
    ),
    MINIMAL_DIGITAL(
        titleAr = "رقمي عصري بسيط",
        titleEn = "Modern Digital",
        descriptionAr = "عرض رقمي بارز مع مؤشرات البطارية والطقس وتاريخ اليوم بأسلوب Pixel المعاصر"
    )
}

enum class WatchColorTheme(
    val titleAr: String,
    val titleEn: String,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val arcBg: Color,
    val dialBg: Color = Color(0xFF000000),
    val textPrimary: Color = Color.White,
    val textSecondary: Color = Color(0xFFB0B0B0)
) {
    CORAL_PEACH(
        titleAr = "مرجاني / بيتش",
        titleEn = "Coral & Peach",
        primary = Color(0xFFFF8E72),
        secondary = Color(0xFF532E25),
        accent = Color(0xFFFF8E72),
        arcBg = Color(0xFF38231E)
    ),
    LEMONGRASS_MINT(
        titleAr = "عشبة الليمون / نعناع",
        titleEn = "Lemongrass & Mint",
        primary = Color(0xFF00E676),
        secondary = Color(0xFF1E3A2F),
        accent = Color(0xFF00E676),
        arcBg = Color(0xFF162D24)
    ),
    CHALK_OBSIDIAN(
        titleAr = "طباشيري / أوبسيديان",
        titleEn = "Chalk & Obsidian",
        primary = Color(0xFFE2E8F0),
        secondary = Color(0xFF334155),
        accent = Color(0xFF94A3B8),
        arcBg = Color(0xFF1E293B)
    ),
    BAY_BLUE(
        titleAr = "أزرق خليجي",
        titleEn = "Bay Blue",
        primary = Color(0xFF38BDF8),
        secondary = Color(0xFF0C4A6E),
        accent = Color(0xFF7DD3FC),
        arcBg = Color(0xFF082F49)
    ),
    HAZEL_GOLD(
        titleAr = "بندقي / ذهبي",
        titleEn = "Hazel Gold",
        primary = Color(0xFFFBBF24),
        secondary = Color(0xFF451A03),
        accent = Color(0xFFFDE68A),
        arcBg = Color(0xFF291E0A)
    ),
    SAGE_GREEN(
        titleAr = "أخضر مريمية",
        titleEn = "Sage Green",
        primary = Color(0xFFA7F3D0),
        secondary = Color(0xFF064E3B),
        accent = Color(0xFF34D399),
        arcBg = Color(0xFF062D22)
    )
}

data class WatchComplicationData(
    val batteryLevel: Int = 84,
    val isCharging: Boolean = false,
    val heartRate: Int = 75,
    val steps: Int = 4280,
    val stepsGoal: Int = 8000,
    val temperatureCelsius: Int = 24,
    val temperatureFahrenheit: Int = 61,
    val dayOfWeek: String = "MON",
    val dayOfWeekAr: String = "الإثنين",
    val dateMonthDay: String = "08-24",
    val weatherCondition: String = "صافي",
    val weatherIconMoon: Boolean = true
)
