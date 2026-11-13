(() => {
  "use strict";

  const form = document.getElementById("aiChatForm") || document.getElementById("ask");
  const input = document.getElementById("aiQuestion") || document.getElementById("question");
  const log = document.getElementById("aiChatLog") || document.getElementById("chat");
  const status = document.getElementById("aiStatus") || document.getElementById("status");

  if (!form || !input || !log) return;

  const endpoint = window.PRO67_CONFIG && window.PRO67_CONFIG.aiEndpoint
    ? window.PRO67_CONFIG.aiEndpoint
    : "";
  const modern = log.id === "aiChatLog";
  const history = [];

  function addMessage(role, message) {
    const node = document.createElement("div");
    node.className = modern
      ? "ai-message " + (role === "assistant" ? "assistant" : "user")
      : "bubble " + (role === "assistant" ? "bot" : "human");
    node.textContent = message;
    log.appendChild(node);
    log.scrollTop = log.scrollHeight;
    return node;
  }

  function setStatus(message) {
    if (status) status.textContent = message;
  }

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    event.stopImmediatePropagation();

    const question = input.value.trim();
    if (!question) return;

    addMessage("user", question);
    input.value = "";

    if (!endpoint) {
      addMessage("assistant", "AI Tutor ka connection abhi configure nahi hua hai. Backend deploy karne ke baad GitHub Actions mein BROHACK67_AI_ENDPOINT variable set karke APK rebuild karein.");
      setStatus("AI backend URL not configured");
      return;
    }

    const sendButton = form.querySelector('button[type="submit"], button');
    if (sendButton) sendButton.disabled = true;
    const pending = addMessage("assistant", "Soch raha hoon…");
    setStatus("AI Tutor connected • generating answer");

    try {
      const title = document.querySelector("h1")?.textContent?.trim() || document.title;
      const response = await fetch(endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          message: question,
          course: title.slice(0, 100),
          history: history.slice(-10)
        })
      });

      const payload = await response.json().catch(() => ({}));
      if (!response.ok) throw new Error(payload.error || "AI service returned an error.");
      const answer = typeof payload.reply === "string" ? payload.reply.trim() : "";
      if (!answer) throw new Error("AI service returned an empty response.");

      pending.textContent = answer;
      history.push({ role: "user", content: question });
      history.push({ role: "assistant", content: answer });
      while (history.length > 10) history.shift();
      setStatus("AI Tutor ready");
    } catch (error) {
      pending.textContent = "AI Tutor abhi jawab nahi de paaya: " + (error.message || "Connection error");
      setStatus("AI Tutor connection issue");
    } finally {
      if (sendButton) sendButton.disabled = false;
      input.focus();
    }
  }, true);

  if (!endpoint) setStatus("AI backend URL not configured");
  else setStatus("AI Tutor ready");
})();
