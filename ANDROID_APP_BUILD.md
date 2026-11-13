# Pro67 Skills Academy — Android APK

This repository builds the existing BROHACK67 academy website as an installable Android app using Capacitor. The original course pages and lesson HTML are copied into the Android WebView; course content is not rewritten.

## Included in the Android app

- Existing home and course catalogue pages
- Existing six detailed lesson pages
- Existing embedded YouTube learning videos and external resources
- Existing browser-based lesson progress and checklist storage
- Shared AI Tutor client for all six lesson pages
- Native Android APK build using GitHub Actions

## Build the APK

1. Open the repository's **Actions** tab.
2. Select **Build Pro67 Skills Academy APK**.
3. Tap **Run workflow** on the main branch.
4. Wait for the run to finish successfully.
5. Open the completed run and download the artifact named **Pro67-Skills-Academy-Android-APK**.
6. Extract the downloaded artifact ZIP and install app-debug.apk on Android.

The workflow also runs automatically when relevant source files are pushed to main.

## Enable the AI Tutor

The AI API key must never be stored in the Android app or GitHub source.

1. Deploy the existing render.yaml Blueprint on Render.
2. In the Render service's Environment settings, add XAI_API_KEY with your private xAI API key. Keep it private.
3. Confirm the service's /health endpoint returns status ok and configured true.
4. In GitHub, open **Settings → Secrets and variables → Actions → Variables**.
5. Create a repository variable named BROHACK67_AI_ENDPOINT with the full public endpoint, for example https://your-service.onrender.com/api/chat.
6. Run **Build Pro67 Skills Academy APK** again to bake the public endpoint into the app configuration.

The backend allows the Capacitor Android WebView origin (https://localhost) and the existing GitHub Pages origin. The xAI key remains on Render.

If BROHACK67_AI_ENDPOINT is not configured, the academy and lessons still work, but the AI Tutor will display a setup message instead of making a request.

## Local build

Requires Node.js 20+, Java 21, Android SDK, and Android Studio/Gradle tooling.

```bash
npm install
npm run build
npx cap add android
npx cap sync android
cd android
./gradlew assembleDebug
```

APK output: android/app/build/outputs/apk/debug/app-debug.apk.
