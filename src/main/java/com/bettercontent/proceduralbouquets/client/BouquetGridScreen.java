package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.menu.BouquetGridMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class BouquetGridScreen extends AbstractContainerScreen<BouquetGridMenu> {
    private static final int BACKGROUND = 0xFF2B2924;
    private static final int GRID_BACKGROUND = 0xFF514B3D;
    private static final int SLOT_BORDER = 0xFF0D0C0A;
    private static final int SLOT_BACKGROUND = 0xFF24221D;
    private static final int SLOT_BACKGROUND_ALTERNATE = 0xFF201E1A;
    private static final int LABEL = 0xFFEFE4BD;

    private AdaptiveMenuLayout.Transform transform = new AdaptiveMenuLayout.Transform(1.0F, 0.0F, 0.0F);

    public BouquetGridScreen(BouquetGridMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = BouquetGridMenu.IMAGE_WIDTH;
        imageHeight = BouquetGridMenu.IMAGE_HEIGHT;
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = (imageWidth - 162) / 2;
        inventoryLabelY = 312;
    }

    @Override
    protected void init() {
        super.init();
        leftPos = 0;
        topPos = 0;
        updateTransform();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateTransform();
        renderBackground(graphics);

        int logicalMouseX = (int) Math.floor(transform.logicalX(mouseX));
        int logicalMouseY = (int) Math.floor(transform.logicalY(mouseY));
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(transform.left(), transform.top(), 0.0F);
        pose.scale(transform.scale(), transform.scale(), 1.0F);
        super.render(graphics, logicalMouseX, logicalMouseY, partialTick);
        pose.popPose();

        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(0, 0, imageWidth, imageHeight, BACKGROUND);
        graphics.fill(7, 17, imageWidth - 7, 307, GRID_BACKGROUND);

        for (int z = 0; z < BouquetGridMenu.GRID_ROWS; z++) {
            for (int x = 0; x < BouquetGridMenu.GRID_COLUMNS; x++) {
                int slotX = BouquetGridMenu.GRID_LEFT + (x * 18);
                int slotY = BouquetGridMenu.GRID_TOP + (z * 18);
                drawSlot(graphics, slotX, slotY, ((x + z) & 1) != 0);
            }
        }

        int playerLeft = (imageWidth - 162) / 2;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(
                    graphics,
                    playerLeft + (column * 18),
                    BouquetGridMenu.PLAYER_INVENTORY_TOP + (row * 18),
                    ((row + column) & 1) != 0
                );
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlot(graphics, playerLeft + (column * 18), BouquetGridMenu.PLAYER_HOTBAR_TOP, (column & 1) != 0);
        }
    }

    private static void drawSlot(GuiGraphics graphics, int x, int y, boolean alternate) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, SLOT_BORDER);
        graphics.fill(x, y, x + 16, y + 16, alternate ? SLOT_BACKGROUND_ALTERNATE : SLOT_BACKGROUND);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, LABEL, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        updateTransform();
        return super.mouseClicked(transform.logicalX(mouseX), transform.logicalY(mouseY), button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        updateTransform();
        return super.mouseDragged(
            transform.logicalX(mouseX),
            transform.logicalY(mouseY),
            button,
            transform.logicalDelta(dragX),
            transform.logicalDelta(dragY)
        );
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        updateTransform();
        return super.mouseReleased(transform.logicalX(mouseX), transform.logicalY(mouseY), button);
    }

    private void updateTransform() {
        transform = AdaptiveMenuLayout.fit(width, height, imageWidth, imageHeight);
    }
}
