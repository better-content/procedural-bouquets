package com.bettercontent.proceduralbouquets.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AdaptiveMenuLayoutTest {
    @Test
    void keepsNativeScaleWhenTheCompleteMenuFits() {
        AdaptiveMenuLayout.Transform transform = AdaptiveMenuLayout.fit(800, 450, 304, 406);

        assertEquals(1.0F, transform.scale());
        assertEquals(248.0F, transform.left());
        assertEquals(22.0F, transform.top());
    }

    @Test
    void uniformlyFitsTheCompleteMenuInsideConstrainedScreens() {
        AdaptiveMenuLayout.Transform transform = AdaptiveMenuLayout.fit(320, 240, 304, 406);

        assertTrue((304 * transform.scale()) <= 312.001F);
        assertTrue((406 * transform.scale()) <= 232.001F);
        assertTrue(transform.left() >= 3.999F);
        assertTrue(transform.top() >= 3.999F);
    }

    @Test
    void inverseCoordinatesMatchScaledRendering() {
        AdaptiveMenuLayout.Transform transform = AdaptiveMenuLayout.fit(320, 240, 304, 406);
        double logicalX = 127.5D;
        double logicalY = 301.25D;
        double screenX = transform.left() + (logicalX * transform.scale());
        double screenY = transform.top() + (logicalY * transform.scale());

        assertEquals(logicalX, transform.logicalX(screenX), 0.0001D);
        assertEquals(logicalY, transform.logicalY(screenY), 0.0001D);
        assertEquals(18.0D, transform.logicalDelta(18.0D * transform.scale()), 0.0001D);
    }
}
