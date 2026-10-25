# Engineering changes — 2026-10-12

- Added Starlight development study from aligned-numeral revision 10
- Kept approved effects/menu typography; added explicit state renderer seam without changing the demo path
- Added mock-only canonical telemetry adapter, host guards and teardown
- Kept development pages out of contract/OTA catalogs; no native or installed-theme edits
- Added geometry/contract/boundary/browser checks and PR-only CI
- Obsidian is handled separately and excluded from this change

Baseline has no tracked `.ai-context` directory or `.agents/skills` packages. Existing AGENTS, theme/bridge/WebView architecture and exact repository code were consulted. These handoff files establish only this task's technical record.

## 2026-10-25 — Global cluster AC focus (local review branch)

- Added bridge1.0.2 focus read/save methods over the existing global preference;
  invalid reads fall back without writes and existing scoped preferences stay unchanged.
- Default1.4.44, Minimalist1.0.4 and ApexGT1.0.16 restore before first rendering,
  persist explicit selection, and preserve focus across off/unknown telemetry.
- Late development mock rehydration is one-shot, cleaned up, and cannot replace the
  selection after any valid AC input (including UP/DOWN).
- Rebuilt all three themes through their existing build scripts; Default APK output
  and separate Minimalist/ApexGT OTA packages match their generated source outputs.
- PASS: all three npm builds, cluster-widgets npm check, ApexGT npm check,
  generated package parity, git diff --check. Android validation coordinated separately.
- No bootstrap, firewall, steering integration, UID, signing, migration or deployment changes.

- Isolated Android validation: 570 tests passed; unsigned debug assembly passed. No signing or deployment.

## 2026-10-25 — PR166 follow-up: preserve focus through HVAC snapshots

- Removed the remaining Minimalist telemetry reset of focusArea on HVAC_POWER=0.
  Power telemetry changes power state only, not the restored or explicit selection.
- Added 36 regression scenarios to the existing 10: both saved values, initial
  cache seeding and live subscription callbacks, numeric/string off/on power,
  first UP/DOWN routing, and repeated off/unknown updates after explicit selection.
- Red-before-green: the new test failed on the original code with
  `seed power=0 must preserve temp`; all 46 scenarios pass after the fix.
- Bumped Minimalist to 1.0.5 and regenerated its HTML/manifest via the normal build.
- PASS: npm ci (locked dependencies), Minimalist build, aggregate npm check,
  package byte parity, host lifecycle regression (6 Python tests; 15 inner JVM cases),
  and git diff --check. No other theme, Android production code, bridge or CI change.
- Android assemble/unit tasks were attempted but the Gradle wrapper download was
  blocked by unavailable network (SocketException: Network is unreachable). No
  Android build/unit result or new vehicle/browser validation is claimed.
