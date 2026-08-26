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

    @Test
    void pottedLayoutUsesTheSameFullSpreadAcrossWidthAndDepth() {
        assertEquals(0.21F, BouquetRenderUtil.pottedHorizontalOffset(1.0F));
        assertEquals(-0.21F, BouquetRenderUtil.pottedHorizontalOffset(-1.0F));
        assertEquals(
            0.42F,
            BouquetRenderUtil.pottedHorizontalOffset(1.0F) - BouquetRenderUtil.pottedHorizontalOffset(-1.0F)
        );
    }

    @Test
    void pottedLayoutBuildsATallCenterAndSeatsOuterFlowersInThePot() {
        float center = BouquetRenderUtil.pottedVerticalCenter(0.0F, 0.0F, 0);
        float outer = BouquetRenderUtil.pottedVerticalCenter(1.0F, 0.0F, 0);

        org.junit.jupiter.api.Assertions.assertTrue(center > outer);
        assertEquals(0.62F, center);
        assertEquals(0.50F, outer);
        assertEquals(0.58F, BouquetRenderUtil.pottedFlowerScale(1));
        assertEquals(0.38F, BouquetRenderUtil.pottedFlowerScale(64));
    }

    @Test
    void gatheredTieGrowsWithTheFlowerCountWithoutBecomingAWrapper() {
        float sparse = BouquetRenderUtil.gatheredTieScale(1);
        float mixed = BouquetRenderUtil.gatheredTieScale(12);
        float dense = BouquetRenderUtil.gatheredTieScale(64);

        assertEquals(0.48F, sparse);
        assertEquals(0.68F, dense);
        org.junit.jupiter.api.Assertions.assertTrue(sparse < mixed);
        org.junit.jupiter.api.Assertions.assertTrue(mixed < dense);
    }

    @Test
    void gatheredStemCentersStayInsideTheNarrowestTie() {
        float left = BouquetRenderUtil.gatheredStemHorizontalOffset(-1.0F);
        float right = BouquetRenderUtil.gatheredStemHorizontalOffset(1.0F);
        float narrowestTieHalfWidth = BouquetRenderUtil.gatheredTieScale(1) * (2.5F / 16.0F);

        assertEquals(-0.055F, left);
        assertEquals(0.055F, right);
        org.junit.jupiter.api.Assertions.assertTrue(Math.abs(left) < narrowestTieHalfWidth);
        org.junit.jupiter.api.Assertions.assertTrue(Math.abs(right) < narrowestTieHalfWidth);
    }

    @Test
    void onlyOuterGatheredFlowersLeanAwayFromTheCenter() {
        assertEquals(0.0F, BouquetRenderUtil.gatheredOutwardTiltDegrees(-0.55F));
        assertEquals(0.0F, BouquetRenderUtil.gatheredOutwardTiltDegrees(0.0F));
        assertEquals(0.0F, BouquetRenderUtil.gatheredOutwardTiltDegrees(0.55F));
        assertEquals(7.0F, BouquetRenderUtil.gatheredOutwardTiltDegrees(-1.0F));
        assertEquals(-7.0F, BouquetRenderUtil.gatheredOutwardTiltDegrees(1.0F));
    }
}
