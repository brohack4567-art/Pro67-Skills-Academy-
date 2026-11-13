const fs = require("node:fs");
const path = require("node:path");

const root = path.resolve(__dirname, "..");
const output = path.join(root, "www");

// Build a fully offline-capable web bundle from the GitHub Pages source.
// No hosted AppDeploy URL or remote web shell is used.
fs.rmSync(output, { recursive: true, force: true });
fs.mkdirSync(output, { recursive: true });

for (const entry of fs.readdirSync(root, { withFileTypes: true })) {
  if (entry.isFile() && (/\.html?$/i.test(entry.name) || /\.css$/i.test(entry.name) || /\.js$/i.test(entry.name))) {
    if (entry.name !== "capacitor.config.json") {
      fs.copyFileSync(path.join(root, entry.name), path.join(output, entry.name));
    }
  }
  if (entry.isDirectory() && ["assets", "images", "resources"].includes(entry.name)) {
    fs.cpSync(path.join(root, entry.name), path.join(output, entry.name), { recursive: true });
  }
}

if (!fs.existsSync(path.join(output, "index.html"))) {
  throw new Error("index.html is missing; cannot build the Zenvora app.");
}
console.log("Built Zenvora from repository files into www/");
