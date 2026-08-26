# Procedural Bouquets

Pack-owned procedural flower bouquet generator for Forge `1.20.1`.

## Gameplay

- Arrange up to 64 upright, three-dimensional flowers on the two-pixel-high bouquet grid, then collect the finished bouquet; every flower keeps its exact rotation across tray, gathered, and potted forms. Gathered bouquets use a centered kraft-paper sleeve, while potted flowers spread through the pot's full three-dimensional opening.
- Sneak-use an empty hand on the grid to open its literal 16×16 inventory map; ordinary inventory clicks rearrange or remove flowers, while hovering one flower and pressing `R` or `Shift+R` rotates it clockwise or counterclockwise. Each flower icon turns in its slot to show the stored orientation before closing the screen to inspect the tray.
- Pot a populated bouquet by using it on an existing empty flower pot or by crafting it with a flower pot. Both routes preserve the complete arrangement.
- Sneak-use an empty hand on a filled potted bouquet to recover the bouquet while leaving the pot in place.
- Breaking a filled potted bouquet returns one flower pot and the populated bouquet.

## Common commands

```bash
./gradlew verifyFast
./gradlew verifyFull
./gradlew verifyVisualHarness
./gradlew stageRuntimeJar
```

`verifyVisualHarness` launches an isolated 1600×900 client under the normal production renderers and captures nine ignored evidence sheets in `run-visual-harness/screenshots/`. The sheets cover empty, single-flower, mixed 12-flower, and dense 64-flower forms across grid, bouquet item, potted item, and potted block views, plus the production 16×16 grid editor and four full-size, hand-composed showcase arrangements. A successful task proves the sheets were captured; release review should still inspect all nine images for composition, clipping, missing textures, and context transforms.

## Release artifact

Deploy the reobfuscated runtime jar from:

- `build/libs/procedural-bouquets-<version>.jar`

## Community and support

For modpack and mod discussion, playtest feedback, and bug reports, join the [Better Content Discord](https://discord.gg/EkRnZbzqS9).

## Identity

The canonical identity is repository/artifact `procedural-bouquets`, mod ID and resource namespace `procedural_bouquets`, and Maven group `com.bettercontent`.
