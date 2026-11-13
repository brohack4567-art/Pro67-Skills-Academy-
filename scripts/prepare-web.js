const fs = require("node:fs");
const path = require("node:path");

const root = path.resolve(__dirname, "..");
const output = path.join(root, "www");
const pages = [
  "index.html",
  "courses.html",
  "lesson.html",
  "lesson2.html",
  "lesson3.html",
  "lesson4.html",
  "lesson5.html",
  "lesson6.html"
];

fs.rmSync(output, { recursive: true, force: true });
fs.mkdirSync(output, { recursive: true });

for (const page of pages) {
  const source = path.join(root, page);
  if (!fs.existsSync(source)) {
    throw new Error("Required academy page is missing: " + page);
  }

  let html = fs.readFileSync(source, "utf8");
  html = html.replaceAll("/Pro67-Skills-Academy-/courses.html", "courses.html");
  html = html.replaceAll("/Pro67-Skills-Academy-/lesson.html", "lesson.html");

  if (page.startsWith("lesson")) {
    const scripts = [
      '<script src="app-config.js"></script>',
      '<script src="ai-tutor.js"></script>'
    ].join("\n");
    html = html.replace(/<\/body>/i, scripts + "\n</body>");
  }

  fs.writeFileSync(path.join(output, page), html, "utf8");
}

const endpoint = (process.env.BROHACK67_AI_ENDPOINT || "").trim();
const config = "window.PRO67_CONFIG = Object.freeze({ aiEndpoint: " +
  JSON.stringify(endpoint) + " });\n";
fs.writeFileSync(path.join(output, "app-config.js"), config, "utf8");
fs.copyFileSync(path.join(root, "ai-tutor.js"), path.join(output, "ai-tutor.js"));

console.log("Prepared " + pages.length + " academy pages for the Android app.");
console.log("AI tutor endpoint: " + (endpoint ? "configured" : "not configured (set BROHACK67_AI_ENDPOINT in GitHub Actions Variables)"));
