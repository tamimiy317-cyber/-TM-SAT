const express = require("express");
const cors = require("cors");
const fs = require("fs");
const path = require("path");
const crypto = require("crypto");

const app = express();
const PORT = Number(process.env.PORT || 8080);
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || "CHANGE-ME-NOW";
const DB_FILE =
  process.env.DB_FILE ||
  path.join(__dirname, "data", "subscriptions.json");

app.use(cors());
app.use(express.json({ limit: "1mb" }));
app.use(express.static(path.join(__dirname, "public")));

function loadDb() {
  try {
    const data = JSON.parse(fs.readFileSync(DB_FILE, "utf8"));
    if (!Array.isArray(data.subscriptions)) {
      data.subscriptions = [];
    }
    return data;
  } catch (e) {
    return { subscriptions: [] };
  }
}

function saveDb(db) {
  fs.mkdirSync(path.dirname(DB_FILE), { recursive: true });
  fs.writeFileSync(DB_FILE, JSON.stringify(db, null, 2));
}

function admin(req, res, next) {
  const token = (req.headers.authorization || "")
    .replace(/^Bearer\s+/i, "");

  if (token !== ADMIN_PASSWORD) {
    return res.status(401).json({ error: "Unauthorized" });
  }

  next();
}

function normalizeCode(value) {
  return String(value || "").trim().toUpperCase();
}

function makeCode() {
  const raw = crypto.randomBytes(6).toString("hex").toUpperCase();

  return ${raw.slice(0, 4)}-${raw.slice(4, 8)}-${raw.slice(8, 12)};
}

function publicSubscription(s) {
  return {
    id: s.id,
    code: s.code,
    label: s.label || "",
    expiresAt: s.expiresAt,
    enabled: s.enabled !== false,
    boundDevice: s.boundDevice || null,
    createdAt: s.createdAt
  };
}

// فحص السيرفر
app.get("/api/health", (req, res) => {
  res.json({
    ok: true,
    service: "TM SAT",
    mode: "CODE_ONLY"
  });
});

// تسجيل دخول الإدارة
app.post("/api/admin/login", (req, res) => {
  const password = String(req.body.password || "");

  if (password !== ADMIN_PASSWORD) {
    return res.status(401).json({
      ok: false,
      error: "Wrong password"
    });
  }

  res.json({ ok: true });
});

// عرض جميع الأكواد
app.get("/api/admin/subscriptions", admin, (req, res) => {
  const db = loadDb();

  res.json(
    db.subscriptions.map(publicSubscription)
  );
});

// إنشاء كود فقط
app.post("/api/admin/subscriptions", admin, (req, res) => {
  const db = loadDb();

  const label = String(req.body.label || "").trim();
  const expiresAt = String(req.body.expiresAt || "").trim();

  if (!expiresAt) {
    return res.status(400).json({
      error: "Expiration date is required"
    });
  }

  let code;

  do {
    code = makeCode();
  } while (
    db.subscriptions.some(
      item => normalizeCode(item.code) === code
    )
  );

  const subscription = {
    id: crypto.randomUUID(),
    code,
    label,
    expiresAt,
    enabled: true,
    boundDevice: null,
    createdAt: new Date().toISOString()
  };

  db.subscriptions.unshift(subscription);
  saveDb(db);

  res.json({
    ok: true,
    subscription: publicSubscription(subscription)
  });
});

// تشغيل / إيقاف الكود
app.patch("/api/admin/subscriptions/:id", admin, (req, res) => {
  const db = loadDb();

  const subscription = db.subscriptions.find(
    item => item.id === req.params.id
  );

  if (!subscription) {
    return res.status(404).json({
      error: "Subscription not found"
    });
  }

  if (typeof req.body.enabled === "boolean") {
    subscription.enabled = req.body.enabled;
  }

  if (req.body.expiresAt) {
    subscription.expiresAt = String(req.body.expiresAt);
  }

  if (req.body.label !== undefined) {
    subscription.label = String(req.body.label || "").trim();
  }

  saveDb(db);

  res.json({
    ok: true,
    subscription: publicSubscription(subscription)
  });
});

// فك ربط الجهاز
app.post(
  "/api/admin/subscriptions/:id/unbind",
  admin,
  (req, res) => {
    const db = loadDb();

    const subscription = db.subscriptions.find(
      item => item.id === req.params.id
    );

    if (!subscription) {
      return res.status(404).json({
        error: "Subscription not found"
      });
    }

    subscription.boundDevice = null;
    saveDb(db);

    res.json({
      ok: true,
      subscription: publicSubscription(subscription)
    });
  }
);

// حذف كود
app.delete("/api/admin/subscriptions/:id", admin, (req, res) => {
  const db = loadDb();

  const index = db.subscriptions.findIndex(
    item => item.id === req.params.id
  );

  if (index === -1) {
    return res.status(404).json({
      error: "Subscription not found"
    });
  }

  db.subscriptions.splice(index, 1);
  saveDb(db);

  res.json({ ok: true });
});

// تفعيل التطبيق بالكود
app.post("/api/activate", (req, res) => {
  const code = normalizeCode(req.body.code);
  const deviceId = String(req.body.deviceId || "").trim();

  if (!code || !deviceId) {
    return res.status(400).json({
      ok: false,
      error: "Code and deviceId are required"
    });
  }

  const db = loadDb();

  const subscription = db.subscriptions.find(
    item => normalizeCode(item.code) === code
  );

  if (!subscription) {
    return res.status(404).json({
      ok: false,
      error: "Invalid code"
    });
  }

  if (subscription.enabled === false) {
    return res.status(403).json({
      ok: false,
      error: "Code disabled"
    });
  }

  const expiration = new Date(subscription.expiresAt);

  if (
    Number.isNaN(expiration.getTime()) ||
    expiration.getTime() < Date.now()
  ) {
    return res.status(403).json({
      ok: false,
      error: "Code expired"
    });
  }

  if (
    subscription.boundDevice &&
    subscription.boundDevice !== deviceId
  ) {
    return res.status(403).json({
      ok: false,
      error: "Code already linked to another device"
    });
  }

  if (!subscription.boundDevice) {
    subscription.boundDevice = deviceId;
    saveDb(db);
  }

  res.json({
    ok: true,
    code: subscription.code,
    expiresAt: subscription.expiresAt
  });
});

app.listen(PORT, "0.0.0.0", () => {
  console.log(TM SAT server listening on port ${PORT});
});
