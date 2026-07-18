# himotoki (繙き) — CLAUDE actor guide

**Active disclosure-request filer.** Tier-B ·
`did:web:etzhayyim.com:actor:himotoki` (canonical; `alsoKnownAs
did:web:himotoki.etzhayyim.com`) — **REGISTERED** in did-web
(`50-infra/etzhayyim-did-web/public/actor/himotoki/{did,profile}.json`), per
ADR-2606013800 + ADR-2606272355 ·
ADR-2605302130 · **ADR-2606272355** (self-publication seed on the kotoba mesh) ·
**🟢 R1 — live operation + social emission AUTHORIZED (founder, Council Lv7+ 1/1, 2026-07-16)**:
autonomous heartbeat → content-addressed append-only kotoba Datom log; Murakumo narration
(graceful template fallback); founder-signed `:published` posts. External AT-Proto firehose
relay still needs an operator transport credential (G7 no-server-key). This authorization
covers social_post only — the disclosure-filing cells remain R0, no cells run, no dispatch,
pending their own Council Lv6+ ≥3 per-cell ratify.

## What this actor IS

The **active-outbound** sibling of passive danjo/tadori. It files
disclosure requests and custodies the responses:

- **DSAR** (個人情報開示請求, APPI §33 / GDPR Art.15 / CCPA) to private
  controllers — **own data, consenting member only**.
- **FOIA** (行政文書開示請求, 行政機関情報公開法 / FOIA) to public organs.

Driven by a **coded target registry** (`disclosureTarget`) holding each
org's 窓口 / 住所 / email / portal / 手続き / fee / deadline. Seed at
canonical `registry/targets.seed.edn` (JSON wire projection under `wire/registry/`).

```
target_registry ─┐
request_intake ──┤→ compose → dispatch → deadline_tracker → response_intake ─┐
                 │                                                            ├→ (PII) encrypted.* DID-bound envelope
                 └────────────────────────────────────── appeal_route ←──────┘  (FOIA non-PII) → danjo/ossekai
```

## Do NOT (constitutional invariants — ADR-2605302130 §4)

- **Do not** request a **third party's** personal data, or file without a
  consenting member's explicit consent + Adherent-SBT/DID binding (G3).
  DSAR is **own-data-only**.
- **Do not** use any pretext / sockpuppet / false identity / impersonation;
  every request identifies the **true requester** (G4, §2(c)).
- **Do not** render legal advice; templates + characterization + appeal
  strategy route to **chigiri + external counsel** (G5, UPL).
- **Do not** store disclosed PII anywhere except an
  `com.etzhayyim.encrypted.*` XChaCha20-Poly1305 **DID-bound envelope**
  (G6, ADR-2605181100). **Never** plaintext PII on MST.
- **Do not** mass-file, bulk-enumerate agencies, or flood requests (G8);
  **do not** resell disclosed data or run a paid pretext service (G9).
- **Do not** access systems without authorization or circumvent access
  controls / paywalls / rate limits — **lawful official channel only**
  (G10).
- **Do not** dispatch against a target whose `verificationStatus` is
  `unverified-seed` or whose `lastVerified` is stale (G14). Verify first.
- **Do not** publish disclosed PII or re-disclose it (N13); FOIA public
  records publish only via the §1.12 / 1-SBT-1-vote path (G11).
- **Do not** invent contact data. Registry entries cite `provenance` and
  must be human-verified before live use.

## Boundary with chigiri

chigiri = **what's the form and the law** (procedure templates, UPL
routing, appeal procedure, `data_privacy` cell). himotoki = **send it,
track it, take in the response, encrypt-store it.** himotoki depends on
chigiri; it does not duplicate chigiri's templating.

## Self-publication seed (ADR-2606272355) — register → autonomize → publish, no-server-key

himotoki carries the **actor self-publication seed** (the danjo 弾正 reference
implementation, adapted to himotoki's consent-bound, own-data-only disclosure-filer
posture): the uniform, charter-clean way for the actor to be registered at etzhayyim.com,
run autonomously on the kotoba mesh, and **self-publish its own history + procedures** to
AT-proto **without any server-held key**. We plant the seed; the actor grows on the mesh
(murakumo, `orgs/com-junkawasaki/murakumo/`) and self-custodies its signing identity in
its WASM runtime.

The seed:

- **did-web registration** — `50-infra/etzhayyim-did-web/public/actor/himotoki/{did,profile}.json`
  (`verificationMethod: []` — no server-minted key, did:web trust root = TLS; the
  `#xrpc-libp2p` peer multiaddr is assigned at `bb murakumo deploy` time when `wasmCid` is set).
- **social_post membrane** — `src/himotoki/cells/social_post/state_machine.cljc`: DRAFTS a record into a
  **dry-run** post ONLY if ≥2 public statute/target-registry/primary-source citations (G5) +
  non-adjudicating mirror with the disclaimer (G4) + `server_held_key` false (no-server-key) +
  status `dry-run`. A `published` request REFUSES. Verified under `bb`: `<2 sources /
  server-key / published → refused`, valid → `drafted` with `:post/status :dry-run`,
  `:post/server-held-key false`, `:post/own-data-only true`.
- **publication projection** — `src/himotoki/methods/social.cljc`: projects himotoki's HISTORY (AGGREGATE,
  own-data-only filed-request + disclosure-received records — `draft-filing-post` /
  `draft-disclosure-post`, no requester identity, no envelope contents) + PROCEDURES (the
  DSAR/FOIA disclosure procedures: root-of-right statute, coded target registry, how a member
  self-files — `draft-procedure-post`) into `app.bsky.feed.post`-shaped dry-run posts;
  `enough-sources` raises on <2 (G5); `build-live` raises (live gate). Verified under `bb`.
- **seed trigger wiring** — `kotoba.app.edn` `himotoki-social` component (`on-tick "0 */6 * * *"`
  + `on-kse etzhayyim/actor/himotoki/publish`, `:requires #{:cap/kqe :cap/atproto}`).

**CRITICAL — own-data-only / no third-party PII**: himotoki self-publishes ONLY about its
OWN disclosure procedures and the requesting member's OWN-data results (aggregate counts).
A third party's personal data is **never** projected into a post (G3/G6/N13); disclosed PII
stays in the `com.etzhayyim.encrypted.*` DID-bound envelope, never on MST, never in a post.

**Division of labor (zero-knowledge)**: the **planter** authors the in-repo seed (holds no
key); the **operator** (founder) deploys this repository's `kotoba.app.edn`
with `MURAKUMO_OPERATOR_SEED` + Tailscale and exercises the Council gate for the first live post;
the **actor's mesh runtime** self-generates/self-custodies its `did:key`, presents a member CACAO
leash (ADR-2606111400), and signs its own posts. The server never signs. R0 = dry-run drafts
only; live broadcast is Council Lv6+ + operator + member/actor-signature gated (§1.12 / G11).

```bash
bb run_tests.clj                                            # canonical repository suite
# operator step (zero-knowledge — needs MURAKUMO_OPERATOR_SEED + Tailscale):
#   bb murakumo deploy kotoba.app.edn <node>
```

## Inference

Murakumo-only (G7, ADR-2605215000). No vendor LLM callout.
