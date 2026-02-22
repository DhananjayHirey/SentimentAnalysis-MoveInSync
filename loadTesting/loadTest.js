const RATE_PER_MIN = 100;
const INTERVAL = 60000 / RATE_PER_MIN;

let count = 0;

console.log("Starting load test: 100 requests per minute...\n");

setInterval(async () => {
  count++;

  const payload = {
    entityType: "DRIVER",
    entityId: "D" + Math.floor(Math.random() * 10),
    rating: Math.floor(Math.random() * 5) + 1,
    comment: "JS Load test feedback " + count
  };

  try {
    const res = await fetch("http://localhost:8080/api/feedback", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      console.error("Failed request:", res.status);
    } else {
      console.log("Sent:", count);
    }

  } catch (err) {
    console.error("Error:", err.message);
  }

}, INTERVAL);