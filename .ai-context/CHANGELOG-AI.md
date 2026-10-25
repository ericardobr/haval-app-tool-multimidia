# Engineering changes — 2026-10-12

- Added Starlight development study from aligned-numeral revision 10
- Kept approved effects/menu typography; added explicit state renderer seam without changing the demo path
- Added mock-only canonical telemetry adapter, host guards and teardown
- Kept development pages out of contract/OTA catalogs; no native or installed-theme edits
- Added geometry/contract/boundary/browser checks and PR-only CI
- Obsidian is handled separately and excluded from this change

Baseline has no tracked `.ai-context` directory or `.agents/skills` packages. Existing AGENTS, theme/bridge/WebView architecture and exact repository code were consulted. These handoff files establish only this task's technical record.

## 2026-10-25 — Native steering preservation

- Added native-selection policy and durable backup/readback adapter for both buttons.
- Limit normal capture to custom short taps; preserve stealth and existing dispatch.
- Added warning UI documenting nonexclusive native/custom double and long actions.
- Added 33 focused tests and `docs/steering-native-preservation.md`; focused tests pass.
- Android integration and on-device UI/behavior validation remain separate checks.

- Isolated Android validation: 596 tests passed; unsigned debug assembly passed. No signing or deployment.
