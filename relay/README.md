# Relay (Cloudflare Worker)

Zero-knowledge dumb pipe between the desktop tracker and the Android app. Stores and
forwards **end-to-end-encrypted** blobs only — it never sees the `e2eeKey` or any
plaintext. Contract: [`../docs/REMOTE.md`](../docs/REMOTE.md).

## Deploy

```bash
cd relay
npm install
npx wrangler kv namespace create KV          # paste the id into wrangler.toml
npx wrangler kv namespace create KV --preview # paste the preview_id into wrangler.toml
npx wrangler deploy                          # prints your https://<worker> URL
```

## Push (optional, enables phone notifications)

Create a Firebase project + a service account, then:

```bash
npx wrangler secret put FCM_PROJECT_ID       # e.g. my-firebase-project
npx wrangler secret put FCM_CLIENT_EMAIL     # service-account client_email
npx wrangler secret put FCM_PRIVATE_KEY      # service-account private_key (full PEM)
```

Without these, everything works except push (`POST /v1/.../push` returns `503
fcm_unconfigured`).

## Local check / dev

```bash
npm run check        # bundle + validate (no deploy)
npm run dev          # local server with simulated KV (miniflare)
```

## API

See [`../docs/REMOTE.md`](../docs/REMOTE.md). All routes require
`Authorization: Bearer <readToken>`; the first `PUT .../snapshot` pins the token hash
(trust-on-first-use). Storage is KV with a 7-day TTL, so accounts that stop syncing are
forgotten automatically.
