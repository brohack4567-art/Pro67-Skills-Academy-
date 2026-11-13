# BROHACK67 AI Tutor — Render and Android setup

The academy's AI Tutor uses the Express backend in `backend/`. The private xAI API key stays on Render and is never bundled into the Android APK.

## Deploy the backend on Render

1. In Render, choose **New → Blueprint** and connect this GitHub repository. Render reads `render.yaml`.
2. In the Render service's **Environment** page, add `XAI_API_KEY` with your private xAI API key. Never commit this key to GitHub or place it in the app.
3. Keep `XAI_MODEL` set to a model enabled for your xAI account (the starter default is `grok-3-mini`).
4. Deploy the service.
5. Open `https://YOUR-RENDER-SERVICE.onrender.com/health`. It should return `{"status":"ok","configured":true}`.

## Connect the Android app

1. In GitHub, open this repository's **Settings → Secrets and variables → Actions → Variables**.
2. Add a repository variable named `BROHACK67_AI_ENDPOINT`.
3. Set its value to the full Render endpoint, for example `https://your-service.onrender.com/api/chat`.
4. Open **Actions → Build Pro67 Skills Academy APK → Run workflow**.
5. Download the new APK artifact after the workflow succeeds.

The Android build reads this public endpoint variable and writes it into the generated app configuration. The xAI API key remains on Render. If the endpoint variable is empty, courses and lessons still work, but the AI Tutor displays a setup message.

## Website CORS

The backend permits the existing GitHub Pages origin and Capacitor's Android WebView origin (`https://localhost`). For another website origin, add it to `CORS_ORIGIN` in Render.

## Notes

- The API has a lightweight in-memory rate limit. It resets when the service restarts and is not a shared rate limiter.
- Grok usage may incur xAI API charges.
- Recent chat messages are held in the lesson page during the current session; conversations are not stored on the backend.
