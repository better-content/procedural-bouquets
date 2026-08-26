# Changelog

## Unreleased

- Normalize the project identity to `procedural-bouquets / procedural_bouquets`; this is a clean break with no legacy aliases or migrations.
- Require an existing empty flower pot for direct bouquet placement, eliminating free-pot duplication.
- Replace the static potted-bouquet recipe with an NBT-preserving custom recipe and keep both direct and crafting potting routes.
- Return exactly the original bouquet and one flower pot when a filled pot is broken.
- Rework the bouquet grid into a thin tray and add distinct gathered-bouquet and potted render compositions for block and item contexts.
- Remove unused render and invalid-drop configuration entries and cap bouquet settings at the supported 64 entries.
- Add lifecycle GameTests and a deterministic visual inspection harness.
- Add an adaptively scaled, literal 16×16 inventory editor opened by sneak-using the bouquet grid with an empty hand.
- Extend the visual harness with the production grid editor and add slot-mapping, capacity, transfer, and adaptive-layout tests.
- Add four production-rendered showcase sheets for hand-composed bouquet palettes and silhouettes.
- Stand every tray flower upright as a three-dimensional block-item model so the editor matches the collected bouquet's silhouette.
- Preserve each flower's exact quarter-turn orientation across tray, gathered, and potted renders without form-specific rotation offsets.
- Improve sparse-to-dense readability with adaptive flower sizing, wider and deeper layouts, stronger gathered tiers, and lifted potted stems while preserving exact rotations.
- Add server-authoritative per-flower rotation controls to the literal grid inventory: hover a flower and press `R` or `Shift+R` for clockwise or counterclockwise quarter-turns.
- Rotate each occupied editor-slot icon to match that flower's stored quarter-turn, providing immediate orientation feedback without adding a preview.
