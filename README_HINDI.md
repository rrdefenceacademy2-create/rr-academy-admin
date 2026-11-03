# RR Defence Academy — Admin + CBT Android V3

यह **Android Studio का पूरा स्रोत प्रोजेक्ट** है। यह APK नहीं है।

## क्या मिलेगा
- एक Android ऐप, दो बटन: `Admin Panel` और `CBT Control`
- Admin Portal: https://rr-academy-management.web.app/student/admin.html?v=14
- CBT Admin: https://rr-academy-management.web.app/student/cbt-admin.html?v=14
- वही Firebase लॉगिन / ऑनलाइन डेटा; अलग डेटाबेस या OTP नहीं
- Android 8+ (minSdk 26), Android 16 (targetSdk 36)
- केवल INTERNET permission, Chrome पर बाहरी लिंक; WebView SSL चेतावनियों को बायपास नहीं करता
- पुराने त्रुटिपूर्ण APK से **अलग पैकेज** `com.rrdefence.academyadmin` ताकि इंस्टॉल में टकराव न हो

## विकल्प A — कंप्यूटर पर Android Studio
1. Android Studio के नवीन संस्करण में इस फोल्डर को Open करें।
2. SDK Manager से Android API 36 इंस्टॉल करें।
3. Build → Build APK(s) चुनें।
4. APK `app/build/outputs/apk/debug/app-debug.apk` में मिलेगा।
5. पहले एक Android मोबाइल पर लॉन्च, लॉगिन और CBT टैब जाँचें।

## विकल्प B — GitHub Actions (कंप्यूटर के बिना)
1. GitHub पर नया निजी (Private) repository बनाएँ।
2. इस ZIP का **अंदर का कंटेंट**, `.github` फोल्डर सहित, repository में अपलोड/पुश करें।
3. GitHub → Actions → `Build RR Academy Admin APK` → Run workflow.
4. सफल बिल्ड के बाद उसी Run के `Artifacts` से `RR-Academy-Admin-CBT-Android-V3` डाउनलोड करें।
5. ZIP खोलकर `app-debug.apk` फोन में इंस्टॉल करें। पहले परीक्षण करें।

**ध्यान दें:** GitHub Actions का debug APK टेस्ट के लिए है। हर अलग CI रन पर debug signing key बदलने से भविष्य में अपग्रेड के लिए फिर नया भरोसेमंद release signing सेटअप आवश्यक होगा। वास्तविक वितरण के लिए निजी release keystore से स्थायी हस्ताक्षर करें।

## सुरक्षा और कार्यात्मक सीमाएँ
- बिल्ड अभी इस चैट में नहीं चला: यहाँ Android SDK/Gradle और उनका डाउनलोड उपलब्ध नहीं है।
- ऐप केवल ऑनलाइन साइट का WebView है। वेबसाइट का लॉगिन/CBT बग इस ऐप से अपने-आप नहीं सुधरेगा।
- Android पर वास्तविक लॉन्च, लॉगिन, PDF अपलोड आदि का परीक्षण आवश्यक है।
- Chrome में वेबसाइट पहले से सही चल रही हो तो तत्काल Chrome के `Add to Home screen` का इस्तेमाल कर सकते हैं।
- पुरानी फीस या Firestore डेटा को इस प्रोजेक्ट में कोई बदलाव नहीं किया गया है।
