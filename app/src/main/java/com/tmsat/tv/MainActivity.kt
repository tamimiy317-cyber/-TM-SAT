
package com.tmsat.tv

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.security.MessageDigest

private val Navy = Color(0xFF021A2B)
private val Navy2 = Color(0xFF052B46)
private val Navy3 = Color(0xFF07395B)
private val Gold = Color(0xFFFFB800)
private val White = Color(0xFFF7F7F7)
private val Muted = Color(0xFF9FB3C3)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                TmSatApp()
            }
        }
    }
}

@Composable
fun TmSatApp() {
    var screen by remember { mutableStateOf("splash") }

    LaunchedEffect(Unit) {
        delay(1600)
        screen = "activation"
    }

    when (screen) {
        "splash" -> SplashScreen()
        "activation" -> ActivationScreen {
            screen = "home"
        }
        else -> HomeScreen {
            screen = "activation"
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF001321),
                        Navy,
                        Color(0xFF043B60)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.tm_sat_logo),
                contentDescription = "TM SAT",
                modifier = Modifier.size(330.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "TV • MOVIES • ENTERTAINMENT",
                color = Gold,
                fontSize = 17.sp,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "More Than TV",
                color = White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun ActivationScreen(
    onActivated: () -> Unit
) {
    val context = LocalContext.current
    val deviceCode = remember {
        createDeviceCode(context)
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
    ) {

        Box(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Navy2,
                            Navy
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.tm_sat_logo),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    "TM SAT",
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "More Than TV",
                    color = Gold,
                    fontSize = 17.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .padding(42.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "تفعيل الجهاز",
                color = White,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "الرجاء إدخال كود التفعيل المرسل لك من الموزع",
                color = Muted,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(28.dp))

            Surface(
                color = Navy2,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Gold.copy(alpha = .35f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {

                    Text(
                        "كود الجهاز",
                        color = Gold,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        deviceCode,
                        color = White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onActivated,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .focusable(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "تفعيل",
                    color = Navy,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallAction("حالة الاشتراك")
                SmallAction("إعدادات الشبكة")
            }
        }
    }
}

data class MenuItem(
    val symbol: String,
    val title: String
)

data class ContentItem(
    val title: String,
    val subtitle: String
)

@Composable
private fun HomeScreen(
    onLogout: () -> Unit
) {

    val menu = listOf(
        MenuItem("⌂", "الرئيسية"),
        MenuItem("▣", "القنوات"),
        MenuItem("●", "الأفلام"),
        MenuItem("▤", "المسلسلات"),
        MenuItem("☺", "أطفال"),
        MenuItem("★", "المفضلة"),
        MenuItem("⌕", "بحث"),
        MenuItem("⚙", "الإعدادات")
    )

    val latest = listOf(
        ContentItem("العميد", "مسلسل"),
        ContentItem("صلاح الدين", "مسلسل"),
        ContentItem("المندوب", "مسلسل"),
        ContentItem("لعبة حب", "مسلسل"),
        ContentItem("الزمن", "مسلسل"),
        ContentItem("عائلة شاكر باشا", "مسلسل")
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
    ) {

        Sidebar(
            items = menu,
            onLogout = onLogout
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(
                    start = 22.dp,
                    end = 26.dp,
                    top = 18.dp,
                    bottom = 18.dp
                )
        ) {

            TopHeader()

            Spacer(Modifier.height(14.dp))

            HeroBanner()

            Spacer(Modifier.height(18.dp))

            MainCategories()

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "أحدث الإضافات",
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                Text(
                    "عرض الكل ←",
                    color = Muted,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(latest) {
                    ContentPoster(it)
                }
            }

            Spacer(Modifier.weight(1f))

            BottomActions()
        }
    }
}

@Composable
private fun Sidebar(
    items: List<MenuItem>,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(205.dp)
            .fillMaxHeight()
            .background(Color(0xFF011522))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.tm_sat_logo),
            contentDescription = null,
            modifier = Modifier
                .width(150.dp)
                .height(100.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(10.dp))

        items.forEachIndexed { index, item ->
            SidebarItem(
                item = item,
                selected = index == 0
            )

            Spacer(Modifier.height(5.dp))
        }

        Spacer(Modifier.weight(1f))

        Text(
            "More Than TV",
            color = Gold,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "خروج",
            color = Muted,
            modifier = Modifier
                .clickable { onLogout() }
                .padding(10.dp)
        )
    }
}

@Composable
private fun SidebarItem(
    item: MenuItem,
    selected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected)
                    Gold
                else
                    Color.Transparent
            )
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected)
                    Color.Transparent
                else
                    Navy3,
                shape = RoundedCornerShape(10.dp)
            )
            .focusable()
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            item.symbol,
            color = if (selected) Navy else White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.width(14.dp))

        Text(
            item.title,
            color = if (selected) Navy else White,
            fontSize = 17.sp,
            fontWeight = if (selected)
                FontWeight.Bold
            else
                FontWeight.Normal
        )
    }
}

@Composable
private fun TopHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            "TM SAT",
            color = Gold,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                "12:45",
                color = White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "السبت",
                color = Muted,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.width(18.dp))

        Text(
            "◉",
            color = White,
            fontSize = 22.sp
        )

        Spacer(Modifier.width(16.dp))

        Text(
            "⚙",
            color = White,
            fontSize = 22.sp
        )
    }
}

@Composable
private fun HeroBanner() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Navy2)
            .border(
                1.dp,
                Gold.copy(alpha = .25f),
                RoundedCornerShape(18.dp)
            )
    ) {

        Image(
            painter = painterResource(R.drawable.tm_sat_banner),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Black.copy(alpha = .72f),
                            Color.Black.copy(alpha = .30f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(30.dp)
        ) {

            Text(
                "فلسطين",
                color = White,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "أحدث المحتويات الآن على TM SAT",
                color = White.copy(alpha = .85f),
                fontSize = 17.sp
            )

            Spacer(Modifier.height(16.dp))

            Surface(
                color = Gold,
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    "▶ شاهد الآن",
                    color = Navy,
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 11.dp
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MainCategories() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        HomeCategory(
            modifier = Modifier.weight(1f),
            symbol = "▣",
            ar = "القنوات المباشرة",
            en = "LIVE TV",
            accent = Color(0xFF0879D9)
        )

        HomeCategory(
            modifier = Modifier.weight(1f),
            symbol = "●",
            ar = "الأفلام",
            en = "MOVIES",
            accent = Color(0xFF73501F)
        )

        HomeCategory(
            modifier = Modifier.weight(1f),
            symbol = "▤",
            ar = "المسلسلات",
            en = "SERIES",
            accent = Color(0xFF393B96)
        )

        HomeCategory(
            modifier = Modifier.weight(1f),
            symbol = "☺",
            ar = "أطفال",
            en = "KIDS",
            accent = Color(0xFF188E6E)
        )
    }
}

@Composable
private fun HomeCategory(
    modifier: Modifier,
    symbol: String,
    ar: String,
    en: String,
    accent: Color
) {

    Column(
        modifier = modifier
            .height(132.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        accent,
                        Navy2
                    )
                )
            )
            .border(
                1.dp,
                Gold.copy(alpha = .35f),
                RoundedCornerShape(14.dp)
            )
            .focusable()
            .clickable { }
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            symbol,
            color = Gold,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.height(7.dp))

        Text(
            ar,
            color = White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            en,
            color = Gold,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun ContentPoster(
    item: ContentItem
) {

    Column(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Navy3,
                        Color(0xFF01131F)
                    )
                )
            )
            .border(
                1.dp,
                Gold.copy(alpha = .25f),
                RoundedCornerShape(12.dp)
            )
            .focusable()
            .clickable { }
            .padding(12.dp),
        verticalArrangement = Arrangement.Bottom
    ) {

        Text(
            "TM",
            color = Gold.copy(alpha = .55f),
            fontSize = 36.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.weight(1f))

        Text(
            item.title,
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            item.subtitle,
            color = Muted,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun BottomActions() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        SmallAction("الحساب")
        SmallAction("تفعيل كود")
        SmallAction("معلومات الجهاز")
        SmallAction("تغيير اللغة")

        Spacer(Modifier.weight(1f))

        Text(
            "YouTube   Facebook   Instagram",
            color = Muted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SmallAction(
    text: String
) {

    Surface(
        color = Navy2,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Gold.copy(alpha = .25f)
        )
    ) {
        Text(
            text,
            color = White,
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 9.dp
            ),
            fontSize = 13.sp
        )
    }
}

private fun createDeviceCode(context: Context): String {
    val androidId = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "TM-SAT"

    val digest = MessageDigest
        .getInstance("SHA-256")
        .digest(androidId.toByteArray())

    return digest
        .take(8)
        .joinToString("") { byte ->
            "%02X".format(byte)
        }
        .chunked(4)
        .joinToString("-")
}
