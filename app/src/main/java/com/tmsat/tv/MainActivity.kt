
package com.tmsat.tv

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

private val Navy = Color(0xFF001321)
private val Panel = Color(0xE6032035)
private val Panel2 = Color(0xE60A3655)
private val Gold = Color(0xFFFFC531)
private val White = Color(0xFFF7F8FA)
private val Muted = Color(0xFFB8C5CE)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                TmSatApp(this)
            }
        }
    }
}

@Composable
private fun TmSatApp(context: Context) {

    val prefs = remember {
        context.getSharedPreferences(
            "tm_sat_prefs",
            Context.MODE_PRIVATE
        )
    }

    var screen by remember {
        mutableStateOf("splash")
    }

    var expiresAt by remember {
        mutableStateOf(
            prefs.getString("expires_at", "") ?: ""
        )
    }

    var activationCode by remember {
        mutableStateOf(
            prefs.getString("activation_code", "") ?: ""
        )
    }

    val alreadyActivated = remember {
        prefs.getBoolean("activated", false)
    }

    LaunchedEffect(Unit) {
        delay(1600)

        screen =
            if (alreadyActivated) {
                "home"
            } else {
                "activation"
            }
    }

    when (screen) {

        "splash" -> {
            SplashScreen()
        }

        "activation" -> {
            ActivationScreen(
                context = context,
                onActivated = { code, expiry ->

                    activationCode = code
                    expiresAt = expiry

                    prefs.edit()
                        .putBoolean("activated", true)
                        .putString("activation_code", code)
                        .putString("expires_at", expiry)
                        .apply()

                    screen = "home"
                }
            )
        }

        else -> {
            HomeScreen(
                activationCode = activationCode,
                expiresAt = expiresAt,
                onLogout = {

                    prefs.edit()
                        .clear()
                        .apply()

                    activationCode = ""
                    expiresAt = ""

                    screen = "activation"
                }
            )
        }
    }
}

@Composable
private fun SplashScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(
                R.drawable.tm_sat_banner
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = 0.55f
                    )
                )
        )

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(
                    R.drawable.tm_sat_logo
                ),
                contentDescription = "TM SAT",
                modifier = Modifier.size(310.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "TV • MOVIES • ENTERTAINMENT",
                color = Gold,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

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
    context: Context,
    onActivated: (
        code: String,
        expiry: String
    ) -> Unit
) {

    var code by remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    var isError by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val deviceCode = remember {
        createDeviceCode(context)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(
                R.drawable.tm_sat_banner
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xC900101D)
                )
        )

        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(
                            R.drawable.tm_sat_logo
                        ),
                        contentDescription = "TM SAT",
                        modifier = Modifier.size(300.dp),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text =
                            "TV • MOVIES • ENTERTAINMENT",
                        color = Gold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Panel,
                            RoundedCornerShape(22.dp)
                        )
                        .padding(30.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "تفعيل TM SAT",
                        color = Gold,
                        fontSize = 30.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "أدخل كود الاشتراك",
                        color = White,
                        fontSize = 17.sp
                    )

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    OutlinedTextField(
                        value = code,
                        onValueChange = {
                            code = it
                                .uppercase()
                                .trim()
                        },
                        singleLine = true,
                        label = {
                            Text("كود التفعيل")
                        },
                        colors =
                            OutlinedTextFieldDefaults
                                .colors(
                                    focusedTextColor =
                                        White,
                                    unfocusedTextColor =
                                        White,
                                    focusedBorderColor =
                                        Gold,
                                    unfocusedBorderColor =
                                        Muted,
                                    focusedLabelColor =
                                        Gold,
                                    unfocusedLabelColor =
                                        Muted,
                                    cursorColor = Gold
                                ),
                        modifier =
                            Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text =
                            "رقم الجهاز: $deviceCode",
                        color = Muted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    if (message.isNotBlank()) {

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        Text(
                            text = message,
                            color =
                                if (isError) {
                                    Color(0xFFFF7777)
                                } else {
                                    Color(0xFF65E59A)
                                },
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    Button(
                        onClick = {

                            if (code.isBlank()) {
                                message =
                                    "أدخل كود التفعيل"
                                isError = true
                                return@Button
                            }

                            loading = true
                            message = ""
                            isError = false

                            scope.launch {

                                try {

                                    val result =
                                        withContext(
                                            Dispatchers.IO
                                        ) {
                                            activateCode(
                                                code = code,
                                                deviceId =
                                                    deviceCode
                                            )
                                        }

                                    if (result.success) {

                                        message =
                                            "تم التفعيل بنجاح"

                                        isError = false

                                        onActivated(
                                            result.code,
                                            result.expiresAt
                                        )

                                    } else {

                                        message =
                                            result.message

                                        isError = true
                                    }

                                } catch (
                                    e: Exception
                                ) {

                                    message =
                                        e.message
                                            ?: "تعذر الاتصال بالسيرفر"

                                    isError = true

                                } finally {

                                    loading = false
                                }
                            }
                        },
                        enabled = !loading,
                        colors =
                            ButtonDefaults
                                .buttonColors(
                                    containerColor = Gold
                                ),
                        shape =
                            RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .focusable()
                    ) {

                        if (loading) {

                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = Navy
                            )

                        } else {

                            Text(
                                text = "تفعيل",
                                color =
                                    Color(0xFF151515),
                                fontSize = 19.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    activationCode: String,
    expiresAt: String,
    onLogout: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(
                R.drawable.tm_sat_banner
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xD0001424)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(
                        R.drawable.tm_sat_logo
                    ),
                    contentDescription = "TM SAT",
                    modifier = Modifier
                        .width(170.dp)
                        .height(85.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "TM SAT",
                    color = Gold,
                    fontSize = 30.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "مرحباً بك في TM SAT",
                color = White,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "تم تفعيل الجهاز بنجاح",
                color = Gold,
                fontSize = 18.sp
            )

            if (expiresAt.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text =
                        "تاريخ انتهاء الاشتراك: $expiresAt",
                    color = Muted,
                    fontSize = 14.sp
                )
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(18.dp)
            ) {

                HomeCard(
                    title = "القنوات",
                    subtitle = "LIVE TV"
                )

                HomeCard(
                    title = "الأفلام",
                    subtitle = "MOVIES"
                )

                HomeCard(
                    title = "المسلسلات",
                    subtitle = "SERIES"
                )

                HomeCard(
                    title = "الأطفال",
                    subtitle = "KIDS"
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Surface(
                color = Panel,
                shape =
                    RoundedCornerShape(20.dp),
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "الاشتراك فعّال",
                        color = Gold,
                        fontSize = 22.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "كود الاشتراك: $activationCode",
                        color = White,
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "هذا الجهاز مربوط بالكود.",
                        color = Muted,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "إلغاء التفعيل من هذا الجهاز",
                color = Muted,
                modifier = Modifier
                    .clickable {
                        onLogout()
                    }
                    .padding(12.dp)
            )
        }
    }
}

@Composable
private fun HomeCard(
    title: String,
    subtitle: String
) {

    Surface(
        color = Panel2,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .width(190.dp)
            .height(125.dp)
            .focusable()
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = title,
                color = White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = subtitle,
                color = Gold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class ActivationResponse(
    val success: Boolean,
    val code: String = "",
    val expiresAt: String = "",
    val message: String = ""
)

private fun activateCode(
    code: String,
    deviceId: String
): ActivationResponse {

    val baseUrl =
        ApiConfig.TM_SAT_API
            .trimEnd('/')

    val url =
        URL("$baseUrl/api/activate")

    val connection =
        url.openConnection()
            as HttpURLConnection

    try {

        connection.requestMethod = "POST"

        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        connection.doOutput = true

        connection.setRequestProperty(
            "Content-Type",
            "application/json"
        )

        connection.setRequestProperty(
            "Accept",
            "application/json"
        )

        val body =
            JSONObject()
                .put(
                    "code",
                    code.trim().uppercase()
                )
                .put(
                    "deviceId",
                    deviceId
                )
                .toString()

        connection.outputStream
            .bufferedWriter()
            .use {
                it.write(body)
            }

        val responseCode =
            connection.responseCode

        val stream =
            if (
                responseCode in 200..299
            ) {
                connection.inputStream
            } else {
                connection.errorStream
            }

        val text =
            stream
                ?.bufferedReader()
                ?.use {
                    it.readText()
                }
                ?: ""

        val json =
            if (text.isNotBlank()) {
                JSONObject(text)
            } else {
                JSONObject()
            }

        if (
            responseCode in 200..299 &&
            json.optBoolean(
                "ok",
                false
            )
        ) {

            return ActivationResponse(
                success = true,
                code =
                    json.optString(
                        "code",
                        code
                    ),
                expiresAt =
                    json.optString(
                        "expiresAt",
                        ""
                    )
            )
        }

        val serverError =
            json.optString(
                "error",
                ""
            )

        val message =
            when (
                serverError
                    .lowercase()
            ) {

                "invalid code" ->
                    "الكود غير صحيح"

                "code disabled" ->
                    "الكود موقوف"

                "code expired" ->
                    "انتهت صلاحية الكود"

                "code already linked to another device" ->
                    "الكود مربوط بجهاز آخر"

                else ->
                    if (
                        serverError.isNotBlank()
                    ) {
                        serverError
                    } else {
                        "فشل التفعيل"
                    }
            }

        return ActivationResponse(
            success = false,
            message = message
        )

    } finally {

        connection.disconnect()
    }
}

private fun createDeviceCode(
    context: Context
): String {

    val androidId =
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "TM-SAT"

    val digest =
        MessageDigest
            .getInstance("SHA-256")
            .digest(
                androidId.toByteArray()
            )

    return digest
        .take(8
        .joinToString("") { byte ->
            "%02X".format(byte)
        }
        .chunked(4)
        .joinToString("-")
}
