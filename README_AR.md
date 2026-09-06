# TM SAT — Android TV Starter

هذه نسخة أولية لتطبيق **TM SAT** على Android TV / Android Box.

## الموجود حالياً
- شاشة تفعيل بكود جهاز مولّد من Android ID.
- واجهة رئيسية مهيأة للريموت والشاشات 16:9.
- أقسام: القنوات، الأفلام، المسلسلات، الأطفال، المفضلة، البحث.
- ألوان وهوية TM SAT (كحلي + ذهبي).
- مشغل فيديو Media3 / ExoPlayer جاهز للربط بمصادر البث المرخّصة.
- نماذج أولية لعقد الـ API.

## فتح المشروع
1. فك الضغط.
2. افتح المجلد `TM_SAT_Starter` في Android Studio.
3. اترك Android Studio ينفّذ Gradle Sync.
4. شغّل المشروع على Android TV Emulator أو Android Box عبر ADB.

## إنشاء APK
من Android Studio:
`Build > Build App Bundle(s) / APK(s) > Build APK(s)`

أو من Terminal بعد توفر Gradle Wrapper:
`./gradlew assembleDebug`

الناتج عادة:
`app/build/outputs/apk/debug/app-debug.apk`

## الخطوة التالية
اربط شاشة التفعيل مع سيرفرك:
- POST `/api/v1/activate`
- حفظ Token محلياً
- جلب القنوات/الأفلام/المسلسلات من API
- تمرير `streamUrl` إلى `TmSatPlayer`

## ملاحظة
استخدم التطبيق فقط للمحتوى الذي تملكه أو لديك ترخيص لتوزيعه.


## تحديث النسخة المجهزة
- شاشة Splash بشعار TM SAT.
- أيقونة Launcher مخصصة.
- TV Banner.
- شاشة تفعيل محسنة.
- واجهة رئيسية محسنة.
- الإصدار الحالي داخل المشروع: 1.0.0.
