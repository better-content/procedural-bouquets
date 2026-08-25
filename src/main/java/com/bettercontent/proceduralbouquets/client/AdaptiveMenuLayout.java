package com.bettercontent.proceduralbouquets.client;

public final class AdaptiveMenuLayout {
    public static final int SCREEN_MARGIN = 8;

    private AdaptiveMenuLayout() {
    }

    public static Transform fit(int screenWidth, int screenHeight, int contentWidth, int contentHeight) {
        float usableWidth = Math.max(1, screenWidth - SCREEN_MARGIN);
        float usableHeight = Math.max(1, screenHeight - SCREEN_MARGIN);
        float scale = Math.min(1.0F, Math.min(usableWidth / contentWidth, usableHeight / contentHeight));
        float left = (screenWidth - (contentWidth * scale)) * 0.5F;
        float top = (screenHeight - (contentHeight * scale)) * 0.5F;
        return new Transform(scale, left, top);
    }

    public record Transform(float scale, float left, float top) {
        public double logicalX(double screenX) {
            return (screenX - left) / scale;
        }

        public double logicalY(double screenY) {
            return (screenY - top) / scale;
        }

        public double logicalDelta(double screenDelta) {
            return screenDelta / scale;
        }
    }
}
