# Changelog

## Unreleased

- Normalize the project identity to `procedural-bouquets / procedural_bouquets`; this is a clean break with no legacy aliases or migrations.
- Require an existing empty flower pot for direct bouquet placement, eliminating free-pot duplication.
- Replace the static potted-bouquet recipe with an NBT-preserving custom recipe and keep both direct and crafting potting routes.
- Return exactly the original bouquet and one flower pot when a filled pot is broken.
- Rework the bouquet grid into a thin tray and add distinct gathered-bouquet and potted render compositions for block and item contexts.
- Remove unused render and invalid-drop configuration entries and cap bouquet settings at the supported 64 entries.
- Add lifecycle GameTests and a deterministic four-sheet visual inspection harness.
