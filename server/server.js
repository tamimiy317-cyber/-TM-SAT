const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const app = express();
const PORT = Number(process.env.PORT || 8080);
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || 'CHANGE-ME-NOW';
const DB_FILE = process.env.DB_FILE || path.join(__dirname, 'data', 'subscriptions.json');

app.use(cors());
app.use(express.json({ limit: '1mb' }));
app.use(express.static(path.join(__dirname, 'public')));

function loadDb() {
  try { return JSON.parse(fs.readFileSync(DB_FILE, 'utf8')); }
  catch { return { subscriptions: [] }; }
}
function saveDb(db) {
  fs.mkdirSync(path.dirname(DB_FILE), { recursive: true });
  fs.writeFileSync(DB_FILE, JSON.stringify(db, null, 2));
}
function admin(req, res, next) {
  const token = (req.headers.authorization || '').replace(/^Bearer\s+/i, '');
  if (token !== ADMIN_PASSWORD) return res.status(401).json({ error: 'Unauthorized' });
  next();
}
function normalizeCode(v) { return String(v || '').trim().toUpperCase(); }
function makeCode() {
  const raw = crypto.randomBytes(6).toString('hex').toUpperCase();
  return `${raw.slice(0,4)}-${raw.slice(4,8)}-${raw.slice(8,12)}`;
}
function publicSub(s) {
  return {
    id: s.id, code: s.code, label: s.label || '', expiresAt: s.expiresAt,
    enabled: s.enabled !== false, allPackages: s.allPackages !== false,
    boundDevice: s.boundDevice || null, createdAt: s.createdAt
  };
}

app.get('/health', (_req, res) => res.json({ ok: true, service: 'TM SAT' }));

app.post('/api/v1/activate', (req, res) => {
  const code = normalizeCode(req.body?.code);
  const deviceCode = String(req.body?.deviceCode || '').trim();
  if (!code || !deviceCode) return res.status(400).json({ error: 'code and deviceCode required' });
  const db = loadDb();
  const sub = db.subscriptions.find(x => x.code === code);
  if (!sub || sub.enabled === false) return res.status(404).json({ error: 'CODE_NOT_FOUND' });
  if (sub.expiresAt && Date.parse(sub.expiresAt) < Date.now()) return res.status(403).json({ error: 'EXPIRED' });
  if (sub.boundDevice && sub.boundDevice !== deviceCode) return res.status(409).json({ error: 'DEVICE_MISMATCH' });
  if (!sub.boundDevice) {
    sub.boundDevice = deviceCode;
    sub.activatedAt = new Date().toISOString();
    saveDb(db);
  }
  res.json({
    active: true,
    expiresAt: sub.expiresAt,
    allPackages: sub.allPackages !== false,
    xtream: {
      server: sub.xtream.server,
      username: sub.xtream.username,
      password: sub.xtream.password
    }
  });
});

app.get('/api/v1/admin/subscriptions', admin, (_req, res) => {
  const db = loadDb();
  res.json(db.subscriptions.map(publicSub));
});

app.post('/api/v1/admin/subscriptions', admin, (req, res) => {
  const { label, expiresAt, xtream, allPackages } = req.body || {};
  if (!xtream?.server || !xtream?.username || !xtream?.password) {
    return res.status(400).json({ error: 'xtream server/username/password required' });
  }
  const db = loadDb();
  let code = normalizeCode(req.body?.code) || makeCode();
  if (db.subscriptions.some(x => x.code === code)) return res.status(409).json({ error: 'CODE_EXISTS' });
  const sub = {
    id: crypto.randomUUID(), code, label: String(label || ''),
    expiresAt: expiresAt || null, enabled: true,
    allPackages: allPackages !== false, boundDevice: null,
    xtream: {
      server: String(xtream.server).trim().replace(/\/$/, ''),
      username: String(xtream.username), password: String(xtream.password)
    },
    createdAt: new Date().toISOString()
  };
  db.subscriptions.unshift(sub); saveDb(db); res.status(201).json(publicSub(sub));
});

app.patch('/api/v1/admin/subscriptions/:id', admin, (req, res) => {
  const db = loadDb();
  const sub = db.subscriptions.find(x => x.id === req.params.id);
  if (!sub) return res.status(404).json({ error: 'NOT_FOUND' });
  for (const k of ['label','expiresAt','enabled','allPackages']) if (k in req.body) sub[k] = req.body[k];
  if (req.body.resetDevice === true) { sub.boundDevice = null; sub.activatedAt = null; }
  saveDb(db); res.json(publicSub(sub));
});

app.delete('/api/v1/admin/subscriptions/:id', admin, (req, res) => {
  const db = loadDb(); const before = db.subscriptions.length;
  db.subscriptions = db.subscriptions.filter(x => x.id !== req.params.id);
  if (db.subscriptions.length === before) return res.status(404).json({ error: 'NOT_FOUND' });
  saveDb(db); res.json({ ok: true });
});

app.listen(PORT, () => console.log(`TM SAT server listening on :${PORT}`));
