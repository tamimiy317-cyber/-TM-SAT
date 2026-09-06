
package com.tmsat.tv

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
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

private val Navy = Color(0xFF031E33)
private val Navy2 = Color(0xFF062A44)
private val Gold = Color(0xFFFFB400)
private val SoftWhite = Color(0xFFF6F6F6)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { TmSatApp() } }
    }
}

@Composable
fun TmSatApp() {
    var screen by remember { mutableStateOf("splash") }
    LaunchedEffect(Unit) {
        delay(1800)
        screen = "activation"
    }
    when (screen) {
        "splash" -> SplashScreen()
        "activation" -> ActivationScreen(onActivated = { screen = "home" })
        else -> HomeScreen(onLogout = { screen = "activation" })
    }
}

@Composable
private fun SplashScreen() {
    Box(Modifier.fillMaxSize().background(Navy), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.tm_sat_logo),
                contentDescription = "TM SAT",
                modifier = Modifier.size(330.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(8.dp))
            Text("More Than TV", color = Gold, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ActivationScreen(onActivated: () -> Unit) {
    val context = LocalContext.current
    val deviceCode = remember { createDeviceCode(context) }
    Box(Modifier.fillMaxSize().background(Navy).padding(38.dp)) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(0.9f), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.tm_sat_logo),
                    contentDescription = "TM SAT",
                    modifier = Modifier.size(300.dp),
                    contentScale = ContentScale.Fit
                )
                Text("TV • MOVIES • ENTERTAINMENT", color = Gold, fontSize = 16.sp)
            }
            Spacer(Modifier.width(24.dp))
            Column(
                Modifier.weight(1.1f).clip(RoundedCornerShape(24.dp)).background(Navy2)
                    .border(1.dp, Gold.copy(alpha=.55f), RoundedCornerShape(24.dp)).padding(34.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("تفعيل الجهاز", color = SoftWhite, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Text("استخدم كود الجهاز في لوحة التحكم", color = SoftWhite.copy(alpha=.72f), fontSize = 17.sp)
                Spacer(Modifier.height(22.dp))
                Text("كود الجهاز", color = Gold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(14.dp), color = Navy, border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha=.35f))) {
                    Text(deviceCode, modifier = Modifier.padding(horizontal=28.dp, vertical=18.dp), color = SoftWhite,
                        fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Spacer(Modifier.height(26.dp))
                Button(onClick = onActivated, colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    modifier = Modifier.fillMaxWidth(.72f).height(58.dp).focusable()) {
                    Text("تفعيل تجريبي", color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(14.dp))
                Text("سيتم استبدال الزر بربط API الحقيقي في النسخة النهائية", color = SoftWhite.copy(alpha=.55f), fontSize = 13.sp)
            }
        }
    }
}

data class Category(val titleAr: String, val titleEn: String, val symbol: String)

@Composable
private fun HomeScreen(onLogout: () -> Unit) {
    val categories = listOf(
        Category("القنوات", "LIVE TV", "TV"),
        Category("الأفلام", "MOVIES", "MOV"),
        Category("المسلسلات", "SERIES", "SER"),
        Category("أطفال", "KIDS", "KID"),
        Category("المفضلة", "FAVORITES", "★"),
        Category("بحث", "SEARCH", "⌕")
    )
    Column(Modifier.fillMaxSize().background(Navy).padding(horizontal=32.dp, vertical=18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.tm_sat_logo), null, Modifier.size(width=150.dp,height=82.dp), contentScale=ContentScale.Fit)
            Spacer(Modifier.weight(1f))
            Surface(shape=RoundedCornerShape(14.dp), color=Navy2, border=androidx.compose.foundation.BorderStroke(1.dp,Gold.copy(alpha=.3f))) {
                Column(Modifier.padding(horizontal=20.dp, vertical=10.dp), horizontalAlignment=Alignment.End) {
                    Text("الاشتراك مفعل", color=Gold, fontWeight=FontWeight.Bold)
                    Text("نسخة تجريبية", color=SoftWhite.copy(alpha=.7f), fontSize=12.sp)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth().height(175.dp).clip(RoundedCornerShape(22.dp)).background(Navy2)
            .border(1.dp,Gold.copy(alpha=.35f),RoundedCornerShape(22.dp)).padding(26.dp)) {
            Column(Modifier.align(Alignment.CenterStart)) {
                Text("TM SAT", color=Gold, fontSize=36.sp, fontWeight=FontWeight.ExtraBold)
                Text("تجربة مشاهدة مرتبة وسريعة على Android TV", color=SoftWhite, fontSize=22.sp, fontWeight=FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("القنوات • الأفلام • المسلسلات • الأطفال", color=SoftWhite.copy(alpha=.65f), fontSize=16.sp)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("الأقسام", color=SoftWhite, fontSize=24.sp, fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(categories) { CategoryCard(it) }
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(12.dp), verticalAlignment=Alignment.CenterVertically) {
            BottomChip("الحساب")
            BottomChip("تفعيل كود")
            BottomChip("معلومات الجهاز")
            BottomChip("اللغة")
            Spacer(Modifier.weight(1f))
            Text("خروج", Modifier.clip(RoundedCornerShape(10.dp)).clickable{onLogout()}.padding(16.dp,8.dp), color=SoftWhite.copy(alpha=.8f))
        }
    }
}

@Composable
private fun CategoryCard(item: Category) {
    Column(Modifier.width(190.dp).height(138.dp).clip(RoundedCornerShape(18.dp)).background(Navy2)
        .border(1.dp,Gold.copy(alpha=.42f),RoundedCornerShape(18.dp)).clickable{}.focusable().padding(16.dp),
        horizontalAlignment=Alignment.CenterHorizontally, verticalArrangement=Arrangement.Center) {
        Text(item.symbol, color=Gold, fontSize=27.sp, fontWeight=FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Text(item.titleAr, color=SoftWhite, fontSize=20.sp, fontWeight=FontWeight.Bold)
        Text(item.titleEn, color=Gold, fontSize=12.sp)
    }
}

@Composable
private fun BottomChip(text:String) {
    Surface(shape=RoundedCornerShape(50), color=Navy2, border=androidx.compose.foundation.BorderStroke(1.dp,Gold.copy(alpha=.3f))) {
        Text(text, color=SoftWhite, modifier=Modifier.padding(horizontal=18.dp,vertical=9.dp), fontSize=13.sp)
    }
}

private fun createDeviceCode(context: Context): String {
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "TM-SAT"
    val digest = MessageDigest.getInstance("SHA-256").digest(androidId.toByteArray())
    return digest.take(8).joinToString("") { "%02X".format(it) }.chunked(4).joinToString("-")
}
