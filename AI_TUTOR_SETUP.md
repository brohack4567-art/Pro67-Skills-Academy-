# BROHACK67 Grok AI Tutor — Render setup

This repository contains a small Express backend for the AI chat interface in `lesson.html`. The browser calls the backend; the Grok API key stays on Render and is never included in the public website code.

## Deploy on Render

1. Push this repository to GitHub (the backend is in the `backend/` directory).
2. In Render, choose **New → Blueprint** and select this repository. Render reads `render.yaml`.
   - Alternatively create a **Web Service**, set **Root Directory** to `backend`, **Build Command** to `npm install`, and **Start Command** to `npm start`.
3. In the Render service's **Environment** page, add `XAI_API_KEY` with your private xAI API key. Do not commit the key to GitHub or paste it into the public HTML.
4. Confirm `XAI_MODEL` is set to a model enabled for your xAI account. The starter default is `grok-3-mini`.
5. Deploy. Visit `/health` on the Render service. It should return JSON with `status: "ok"` and `configured: true`.

## Connect the webpage

After Render provides the service URL, open `lesson.html` and replace:

```js
const BROHACK67_AI_ENDPOINT = "https://YOUR-RENDER-SERVICE.onrender.com/api/chat";
```

with your actual Render URL ending in `/api/chat`, then commit the change.

The backend currently allows the GitHub Pages origin `https://brohack4567-art.github.io`, plus localhost development origins. If the site is hosted at another origin, update `CORS_ORIGIN` in Render.

## Notes

- The API has a small in-memory rate limit to discourage bursts. It resets when the service restarts and is not a substitute for a shared rate limiter at scale.
- Grok usage may incur xAI API charges.
- The chat UI keeps recent messages in the browser session only; it does not store learner conversations on a server.
