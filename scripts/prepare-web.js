const fs = require("node:fs");
const path = require("node:path");

const root = path.resolve(__dirname, "..");
const output = path.join(root, "www");
const liveUrl = "https://pro67-skills-academy-a14x9l.v2.appdeploy.ai/";

fs.rmSync(output, { recursive: true, force: true });
fs.mkdirSync(output, { recursive: true });

const fallback = `<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#080d16">
  <title>Pro67 Skills Academy</title>
  <meta http-equiv="refresh" content="0;url=${liveUrl}">
</head>
<body style="margin:0;background:#080d16;color:#eff4ff;font:16px system-ui;display:grid;min-height:100vh;place-items:center">
  <main style="text-align:center;padding:24px">
    <h1>Opening Pro67 Skills Academy…</h1>
    <p><a style="color:#58d9f4" href="${liveUrl}">Tap here if the academy does not open.</a></p>
  </main>
  <script>location.replace(${JSON.stringify(liveUrl)});</script>
</body>
</html>`;

fs.writeFileSync(path.join(output, "index.html"), fallback, "utf8");
console.log("Prepared the Android web shell for " + liveUrl);
