package com.bettercontent.proceduralbouquets.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BouquetRenderUtilTest {

    @Test
    void densityScalePreservesSparseSizeAndCapsAtDenseSize() {
        assertEquals(0.48F, BouquetRenderUtil.densityScale(1, 0.48F, 0.30F));
        assertEquals(0.30F, BouquetRenderUtil.densityScale(64, 0.48F, 0.30F));
        assertEquals(0.30F, BouquetRenderUtil.densityScale(128, 0.48F, 0.30F));
    }

    @Test
    void densityScaleShrinksMonotonicallyAsBouquetFills() {
        float sparse = BouquetRenderUtil.densityScale(1, 0.48F, 0.30F);
        float mixed = BouquetRenderUtil.densityScale(12, 0.48F, 0.30F);
        float dense = BouquetRenderUtil.densityScale(64, 0.48F, 0.30F);

        org.junit.jupiter.api.Assertions.assertTrue(sparse > mixed);
        org.junit.jupiter.api.Assertions.assertTrue(mixed > dense);
    }
}
