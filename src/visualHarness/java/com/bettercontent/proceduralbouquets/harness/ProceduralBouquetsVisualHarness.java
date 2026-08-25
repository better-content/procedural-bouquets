package com.bettercontent.proceduralbouquets.harness;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import com.bettercontent.proceduralbouquets.client.BouquetRenderUtil;
import com.bettercontent.proceduralbouquets.client.BouquetGridScreen;
import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.item.PottedBouquetItem;
import com.bettercontent.proceduralbouquets.menu.BouquetGridMenu;
import com.bettercontent.proceduralbouquets.registry.ModBlocks;
import com.bettercontent.proceduralbouquets.registry.ModItems;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.versions.forge.ForgeVersion;

public final class ProceduralBouquetsVisualHarness {
    private static final String[] CAPTURES = {
        "01-grid.png",
        "02-bouquet-item.png",
        "03-potted-item.png",
        "04-potted-block.png",
        "05-grid-editor.png"
    };
    private static final List<BouquetEntry> EMPTY = List.of();
    private static final List<BouquetEntry> SINGLE = entries(1);
    private static final List<BouquetEntry> MIXED = entries(12);
    private static final List<BouquetEntry> DENSE = entries(64);
    private static final List<List<BouquetEntry>> FIXTURES = List.of(EMPTY, SINGLE, MIXED, DENSE);
    private static final String[] FIXTURE_NAMES = {"Empty", "Single", "Mixed 12", "Dense 64"};

    private static boolean opened;
    private static boolean capturePending;
    private static int startupTicks;
    private static int pageTicks;
    private static int page;
    private static int finishTicks;

    private ProceduralBouquetsVisualHarness() {
    }

    public static void install() {
        MinecraftForge.EVENT_BUS.register(ProceduralBouquetsVisualHarness.class);
        System.out.println("PROCEDURAL_BOUQUETS_VISUAL_HARNESS installed");
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (!opened) {
            if (minecraft.getOverlay() == null && minecraft.screen != null && ++startupTicks >= 30) {
                minecraft.setScreen(new HarnessScreen());
                opened = true;
                System.out.println("PROCEDURAL_BOUQUETS_VISUAL_HARNESS screen-ready");
            }
            return;
        }

        if (page >= CAPTURES.length) {
            if (++finishTicks >= 20) {
                minecraft.stop();
            }
            return;
        }

        if (!capturePending && ++pageTicks >= 30) {
            capturePending = true;
            String capture = CAPTURES[page];
            Screenshot.grab(minecraft.gameDirectory, capture, minecraft.getMainRenderTarget(), message ->
                minecraft.execute(() -> finishCapture(minecraft, capture, message))
            );
        }
    }

    private static void finishCapture(Minecraft minecraft, String capture, Component message) {
        Path screenshot = minecraft.gameDirectory.toPath().resolve("screenshots").resolve(capture);
        if (!Files.isRegularFile(screenshot)) {
            throw new IllegalStateException("Visual harness screenshot failed: " + capture + " (" + message.getString() + ")");
        }
        System.out.println("PROCEDURAL_BOUQUETS_VISUAL_HARNESS captured " + capture);
        page++;
        pageTicks = 0;
        capturePending = false;
        if (page == CAPTURES.length) {
            writeCompletion(minecraft.gameDirectory.toPath());
        }
    }

    private static void writeCompletion(Path gameDirectory) {
        try {
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("status", "complete");
            metadata.put("minecraft", SharedConstants.getCurrentVersion().getName());
            metadata.put("forge", ForgeVersion.getVersion());
            metadata.put("mod", ModList.get().getModContainerById(ProceduralBouquets.MOD_ID)
                .orElseThrow().getModInfo().getVersion().toString());
            metadata.put("resolution", List.of(1600, 900));
            metadata.put("gui_scale", 2);
            metadata.put("fixtures", List.of("empty:0", "single:1", "mixed:12", "dense:64"));
            metadata.put("captures", List.of(CAPTURES));
            Files.writeString(
                gameDirectory.resolve("visual-harness-complete.json"),
                new GsonBuilder().setPrettyPrinting().create().toJson(metadata) + "\n"
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to write visual harness completion metadata", exception);
        }
    }

    private static List<BouquetEntry> entries(int count) {
        String[] flowers = {
            "minecraft:poppy", "minecraft:dandelion", "minecraft:blue_orchid", "minecraft:allium",
            "minecraft:azure_bluet", "minecraft:red_tulip", "minecraft:orange_tulip", "minecraft:white_tulip",
            "minecraft:pink_tulip", "minecraft:oxeye_daisy", "minecraft:cornflower", "minecraft:lily_of_the_valley"
        };
        List<BouquetEntry> entries = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            int x;
            int z;
            if (count == 1) {
                x = 8;
                z = 8;
            } else if (count == 12) {
                int[] mixedX = {8, 6, 10, 4, 12, 7, 9, 5, 11, 3, 13, 8};
                int[] mixedZ = {8, 6, 6, 8, 8, 10, 10, 4, 4, 11, 11, 3};
                x = mixedX[index];
                z = mixedZ[index];
            } else {
                x = 1 + ((index % 8) * 2);
                z = 1 + (((index / 8) % 8) * 2);
            }
            float scale = 0.84F + ((index * 7) % 25) / 100.0F;
            int y = (index % 5) - 2;
            entries.add(new BouquetEntry(ResourceLocation.parse(flowers[index % flowers.length]), x, z, index, scale, y));
        }
        return List.copyOf(entries);
    }

    private static ItemStack bouquet(List<BouquetEntry> entries) {
        ItemStack stack = new ItemStack(ModItems.BOUQUET.get());
        BouquetData.write(stack, entries);
        return stack;
    }

    private static final class HarnessScreen extends Screen {
        private static final int BACKGROUND = 0xFF20252B;
        private static final int PANEL = 0xFF303841;
        private static final int LABEL = 0xFFF2E9D8;
        private BouquetGridScreen editorScreen;

        private HarnessScreen() {
            super(Component.literal("Procedural Bouquets Visual Harness"));
        }

        @Override
        protected void init() {
            Inventory inventory = new Inventory(null);
            inventory.setItem(0, new ItemStack(Items.POPPY, 16));
            inventory.setItem(1, new ItemStack(Items.CORNFLOWER, 8));
            inventory.setItem(9, new ItemStack(Items.DANDELION, 12));
            SimpleContainer grid = new SimpleContainer(BouquetGridMenu.GRID_SLOT_COUNT);
            for (BouquetEntry entry : DENSE) {
                grid.setItem(BouquetGridMenu.slotIndex(entry.x(), entry.z()), BouquetRenderUtil.stackForEntry(entry));
            }
            editorScreen = new BouquetGridScreen(
                new BouquetGridMenu(77, inventory, grid),
                inventory,
                Component.translatable("screen.procedural_bouquets.bouquet_grid")
            );
            editorScreen.init(minecraft, width, height);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            graphics.fill(0, 0, width, height, BACKGROUND);
            graphics.drawCenteredString(font, title, width / 2, 8, LABEL);
            switch (page) {
                case 0 -> renderBlockPage(graphics, true);
                case 1 -> renderItemPage(graphics, false);
                case 2 -> renderItemPage(graphics, true);
                case 3 -> renderBlockPage(graphics, false);
                case 4 -> editorScreen.render(graphics, -100, -100, partialTick);
                default -> {
                }
            }
        }

        private void renderBlockPage(GuiGraphics graphics, boolean grid) {
            String suffix = grid ? "Grid" : "Potted block";
            for (int fixture = 0; fixture < FIXTURES.size(); fixture++) {
                renderBlockPanel(graphics, fixture, 0, FIXTURE_NAMES[fixture] + " · isometric", grid, 35.0F);
                renderBlockPanel(graphics, fixture, 1, FIXTURE_NAMES[fixture] + " · high angle", grid, 75.0F);
            }
            graphics.drawString(font, suffix + " · production block model + production bouquet renderer", 12, height - 16, LABEL);
        }

        private void renderBlockPanel(GuiGraphics graphics, int fixture, int row, String label, boolean grid, float pitch) {
            int columns = 4;
            int gap = 8;
            int panelWidth = (width - (gap * (columns + 1))) / columns;
            int panelHeight = (height - 52) / 2;
            int left = gap + fixture * (panelWidth + gap);
            int top = 24 + row * panelHeight;
            graphics.fill(left, top, left + panelWidth, top + panelHeight - gap, PANEL);
            graphics.drawCenteredString(font, label, left + panelWidth / 2, top + 8, LABEL);
            renderBlockScene(graphics, left + panelWidth / 2, top + panelHeight / 2 + 16, panelHeight * 0.52F, FIXTURES.get(fixture), grid, pitch, 35.0F);
        }

        private void renderItemPage(GuiGraphics graphics, boolean potted) {
            ItemDisplayContext[] contexts = {
                ItemDisplayContext.GUI,
                ItemDisplayContext.FIXED,
                ItemDisplayContext.GROUND,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            };
            List<List<BouquetEntry>> arrangements = potted ? FIXTURES : List.of(SINGLE, MIXED, DENSE);
            String[] names = potted ? FIXTURE_NAMES : new String[]{"Single", "Mixed 12", "Dense 64"};
            int rows = arrangements.size();
            int gap = 6;
            int panelWidth = (width - gap * 6) / 5;
            int panelHeight = (height - 42) / rows;
            for (int row = 0; row < rows; row++) {
                ItemStack stack = potted ? PottedBouquetItem.createStack(arrangements.get(row)) : bouquet(arrangements.get(row));
                for (int column = 0; column < contexts.length; column++) {
                    int left = gap + column * (panelWidth + gap);
                    int top = 24 + row * panelHeight;
                    graphics.fill(left, top, left + panelWidth, top + panelHeight - gap, PANEL);
                    graphics.drawCenteredString(font, names[row] + " · " + contextName(contexts[column]), left + panelWidth / 2, top + 6, LABEL);
                    renderItemScene(graphics, left + panelWidth / 2, top + panelHeight / 2 + 10, panelHeight * 0.36F, stack, contexts[column]);
                }
            }
            graphics.drawString(font, (potted ? "Potted bouquet" : "Bouquet") + " · production item renderer", 12, height - 16, LABEL);
        }

        private static String contextName(ItemDisplayContext context) {
            return switch (context) {
                case GUI -> "GUI";
                case FIXED -> "Fixed";
                case GROUND -> "Ground";
                case FIRST_PERSON_RIGHT_HAND -> "First person";
                case THIRD_PERSON_RIGHT_HAND -> "Third person";
                default -> context.getSerializedName();
            };
        }

        private void renderBlockScene(GuiGraphics graphics, float x, float y, float scale, List<BouquetEntry> entries, boolean grid, float pitch, float yaw) {
            PoseStack pose = graphics.pose();
            MultiBufferSource.BufferSource buffers = graphics.bufferSource();
            pose.pushPose();
            pose.translate(x, y, 200.0F);
            pose.scale(scale, -scale, scale);
            pose.mulPose(Axis.XP.rotationDegrees(pitch));
            pose.mulPose(Axis.YP.rotationDegrees(yaw));
            pose.translate(-0.5F, -0.3F, -0.5F);
            minecraft.getBlockRenderer().renderSingleBlock(
                (grid ? ModBlocks.BOUQUET_GRID : ModBlocks.POTTED_BOUQUET).get().defaultBlockState(),
                pose,
                buffers,
                LightTexture.FULL_BRIGHT,
                0
            );
            if (grid) {
                BouquetRenderUtil.renderGridBouquet(entries, pose, buffers, LightTexture.FULL_BRIGHT, 0, null, 1234L, BouquetData.MAX_ENTRIES);
            } else if (!entries.isEmpty()) {
                pose.translate(0.5F, 0.0F, 0.5F);
                BouquetRenderUtil.renderPottedBouquet(entries, pose, buffers, LightTexture.FULL_BRIGHT, 0, null, 1234L, BouquetData.MAX_ENTRIES);
            }
            graphics.flush();
            pose.popPose();
        }

        private void renderItemScene(GuiGraphics graphics, float x, float y, float scale, ItemStack stack, ItemDisplayContext context) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(x, y, 200.0F);
            pose.scale(scale, -scale, scale);
            minecraft.getItemRenderer().renderStatic(stack, context, LightTexture.FULL_BRIGHT, 0, pose, graphics.bufferSource(), null, 17);
            graphics.flush();
            pose.popPose();
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }
}
