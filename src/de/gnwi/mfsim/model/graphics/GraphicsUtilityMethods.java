/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2021  Achim Zielesny (achim.zielesny@googlemail.com)
 * 
 * Source code is available at <https://github.com/zielesny/MFsim>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.gnwi.mfsim.model.graphics;

import de.gnwi.mfsim.model.graphics.point.PointInPlane;
import de.gnwi.mfsim.model.graphics.slice.Slice;
import de.gnwi.mfsim.model.graphics.body.BodyXyLayer;
import de.gnwi.mfsim.model.graphics.body.BodyXyLayerSingleSurfaceEnum;
import de.gnwi.mfsim.model.graphics.body.BodySphere;
import de.gnwi.mfsim.model.graphics.body.BodyInterface;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticle;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionArrayList;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.util.StandardColorEnum;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.spices.PointInSpace;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import de.gnwi.jdpd.interfaces.IRandom;
import de.gnwi.spices.IPointInSpace;
import java.util.ArrayList;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Graphics utility methods to be instantiated
 *
 * @author Sebastian Fritsch (if specified), Achim Zielesny (otherwise)
 */
public class GraphicsUtilityMethods {

    // <editor-fold defaultstate="collapsed" desc="Nested private comparator classes">
    // <editor-fold defaultstate="collapsed" desc="- XAscendingComparator">
    /**
     * Comparator for x-coordinate comparison in ascending order
     */
    private class XAscendingComparator implements Comparator<GraphicalParticlePosition> {

        /**
         * Compares two GraphicalParticlePositions
         *
         * @param aGraphicalParticlePosition1 The first
 GraphicalParticlePosition
         * @param aGraphicalParticlePosition2 The second
 GraphicalParticlePosition
         *
         * @return Standard comparison result: 1, 0, -1
         */
        public int compare(GraphicalParticlePosition aGraphicalParticlePosition1, GraphicalParticlePosition aGraphicalParticlePosition2) {
            return Double.compare(aGraphicalParticlePosition1.getX(), aGraphicalParticlePosition2.getX());
        }

    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- XDescendingComparator">
    /**
     * Comparator for x-coordinate comparison in descending order
     */
    private class XDescendingComparator implements Comparator<GraphicalParticlePosition> {

        /**
         * Compares two GraphicalParticlePositions
         *
         * @param aGraphicalParticlePosition1 The first
 GraphicalParticlePosition
         * @param aGraphicalParticlePosition2 The second
 GraphicalParticlePosition
         *
         * @return Standard comparison result: 1, 0, -1
         */
        public int compare(GraphicalParticlePosition aGraphicalParticlePosition1, GraphicalParticlePosition aGraphicalParticlePosition2) {
            return Double.compare(aGraphicalParticlePosition2.getX(), aGraphicalParticlePosition1.getX());
        }

    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- YAscendingComparator">
    /**
     * Comparator for y-coordinate comparison in ascending order
     */
    private class YAscendingComparator implements Comparator<GraphicalParticlePosition> {

        /**
         * Compares two GraphicalParticlePositions
         *
         * @param aGraphicalParticlePosition1 The first
 GraphicalParticlePosition
         * @param aGraphicalParticlePosition2 The second
 GraphicalParticlePosition
         *
         * @return Standard comparison result: 1, 0, -1
         */
        public int compare(GraphicalParticlePosition aGraphicalParticlePosition1, GraphicalParticlePosition aGraphicalParticlePosition2) {
            return Double.compare(aGraphicalParticlePosition1.getY(), aGraphicalParticlePosition2.getY());
        }

    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- YDescendingComparator">
    /**
     * Comparator for y-coordinate comparison in descending order
     */
    private class YDescendingComparator implements Comparator<GraphicalParticlePosition> {

        /**
         * Compares two GraphicalParticlePositions
         *
         * @param aGraphicalParticlePosition1 The first
 GraphicalParticlePosition
         * @param aGraphicalParticlePosition2 The second
 GraphicalParticlePosition
         *
         * @return Standard comparison result: 1, 0, -1
         */
        public int compare(GraphicalParticlePosition aGraphicalParticlePosition1, GraphicalParticlePosition aGraphicalParticlePosition2) {
            return Double.compare(aGraphicalParticlePosition2.getY(), aGraphicalParticlePosition1.getY());
        }

    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- ZAscendingComparator">
    /**
     * Comparator for z-coordinate comparison in ascending order
     */
    private class ZAscendingComparator implements Comparator<GraphicalParticlePosition> {

        /**
         * Compares two GraphicalParticlePositions
         *
         * @param aGraphicalParticlePosition1 The first
 GraphicalParticlePosition
         * @param aGraphicalParticlePosition2 The second
 GraphicalParticlePosition
         *
         * @return Standard comparison result: 1, 0, -1
         */
        public int compare(GraphicalParticlePosition aGraphicalParticlePosition1, GraphicalParticlePosition aGraphicalParticlePosition2) {
            return Double.compare(aGraphicalParticlePosition1.getZ(), aGraphicalParticlePosition2.getZ());
        }

    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- ZDescendingComparator">
    /**
     * Comparator for z-coordinate comparison in descending order
     */
    private class ZDescendingComparator implements Comparator<GraphicalParticlePosition> {

        /**
         * Compares two GraphicalParticlePositions
         *
         * @param aGraphicalParticlePosition1 The first
 GraphicalParticlePosition
         * @param aGraphicalParticlePosition2 The second
 GraphicalParticlePosition
         *
         * @return Standard comparison result: 1, 0, -1
         */
        public int compare(GraphicalParticlePosition aGraphicalParticlePosition1, GraphicalParticlePosition aGraphicalParticlePosition2) {
            return Double.compare(aGraphicalParticlePosition2.getZ(), aGraphicalParticlePosition1.getZ());
        }

    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility misc methods
     */
    private MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();

    /**
     * Utility string methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Mathematical constant
     */
    private static double PI_DIVIDED_BY_180 = Math.PI / 180.0;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public GraphicsUtilityMethods() {
        // Do nothing
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns key for slice image
     *
     * @param aBoxView Box view
     * @param aSliceIndex Index of slice
     * @return Key for slice image
     */
    public String getKeyForSliceImage(SimulationBoxViewEnum aBoxView, int aSliceIndex) {
        return aBoxView.name() + ModelDefinitions.GENERAL_SEPARATOR + String.valueOf(aSliceIndex);
    }

    /**
     * Returns scaled target coordinates and size of panel with target ratio of
     * height to width within a master panel with specified dimension
     *
     * @param aDimensionOfMasterPanel Dimension of master panel
     * @param aTargetRatioOfHeightToWidth Target ratio of height to width
     * @return TargetCoordinatesAndSize instance
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public TargetCoordinatesAndSize getTargetCoordinatesAndSize(Dimension aDimensionOfMasterPanel, double aTargetRatioOfHeightToWidth) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDimensionOfMasterPanel == null || aDimensionOfMasterPanel.width <= 0 || aDimensionOfMasterPanel.height <= 0) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aTargetRatioOfHeightToWidth <= 0) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        TargetCoordinatesAndSize tmpTargetCoordinatesAndSize;
        double tmpRatioOfHeightToWidthOfPanel = (double) aDimensionOfMasterPanel.height / (double) aDimensionOfMasterPanel.width;
        if (tmpRatioOfHeightToWidthOfPanel >= aTargetRatioOfHeightToWidth) {
            // Target panel has width of this: Height must be adjusted
            int tmpHeightOfTargetPanel = (int) Math.floor((double) aDimensionOfMasterPanel.width * aTargetRatioOfHeightToWidth);
            int tmpYCoordinate = (aDimensionOfMasterPanel.height - tmpHeightOfTargetPanel) / 2;
            tmpTargetCoordinatesAndSize = new TargetCoordinatesAndSize(
                0,                             // x-coordinate of target panel within master panel
                tmpYCoordinate,                // y-coordinate of target panel within master panel
                aDimensionOfMasterPanel.width, // Width of target panel
                tmpHeightOfTargetPanel         // Height of target panel
            );
        } else {
            // Target panel has height of this: Width must be adjusted
            int tmpWidthOfTargetPanel = (int) Math.floor((double) aDimensionOfMasterPanel.height / aTargetRatioOfHeightToWidth);
            int tmpXCoordinate = (aDimensionOfMasterPanel.width - tmpWidthOfTargetPanel) / 2;
            tmpTargetCoordinatesAndSize = new TargetCoordinatesAndSize(
                tmpXCoordinate,                // x-coordinate of target panel within master panel
                0,                             // y-coordinate of target panel within master panel
                tmpWidthOfTargetPanel,         // Width of target panel
                aDimensionOfMasterPanel.height // Height of target panel
            );
        }
        return tmpTargetCoordinatesAndSize;
    }

    /**
     * Returns path name of specified slice file
     *
     * @param aDestinationDirectory The destination directory for the images
     * @param aBoxView Box view
     * @param aSliceIndex Index of slice (0 to this.numberOfSlices - 1)
     * @param anImageFileType File type of the images to be created
     * @return Path name of specified slice file
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public String getSliceFilePathName(String aDestinationDirectory, SimulationBoxViewEnum aBoxView, int aSliceIndex, ImageFileType anImageFileType) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDestinationDirectory == null || aDestinationDirectory.length() == 0) {
            throw new IllegalArgumentException("The destination directory is null or empty.");
        }
        if (!new File(aDestinationDirectory).isDirectory()) {
            throw new IllegalArgumentException("The destination directory does no exist.");
        }

        // </editor-fold>
        return aDestinationDirectory + File.separatorChar + aBoxView.name() + File.separatorChar + ModelDefinitions.PREFIX_OF_SLICE_IMAGE_FILENAME + String.valueOf(aSliceIndex) + "."
                + anImageFileType.toFileTypeEnding();
    }

    /**
     * Returns path name of specified time step slice file
     *
     * @param aDestinationDirectory The destination directory for the images
     * @param aSliceIndex Index of slice (0 to this.numberOfSlices - 1)
     * @param anImageFileType File type of the images to be created
     * @return Path name of specified slice file
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public String getTimeStepSliceFilePathName(String aDestinationDirectory, int aSliceIndex, ImageFileType anImageFileType) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDestinationDirectory == null || aDestinationDirectory.length() == 0) {
            throw new IllegalArgumentException("The destination directory is null or empty.");
        }
        if (!new File(aDestinationDirectory).isDirectory()) {
            throw new IllegalArgumentException("The destination directory does no exist.");
        }

        // </editor-fold>
        return aDestinationDirectory + File.separatorChar + ModelDefinitions.PREFIX_OF_TIME_STEP_SLICE_IMAGE_FILENAME + String.valueOf(aSliceIndex) + "." + anImageFileType.toFileTypeEnding();
    }

    /**
     * Returns path name of specified spin step slice file
     *
     * @param aDestinationDirectory The destination directory for the images
     * @param aSliceIndex Index of slice (0 to this.numberOfSlices - 1)
     * @param anImageFileType File type of the images to be created
     * @return Path name of specified slice file
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public String getSpinStepSliceFilePathName(String aDestinationDirectory, int aSliceIndex, ImageFileType anImageFileType) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDestinationDirectory == null || aDestinationDirectory.length() == 0) {
            throw new IllegalArgumentException("The destination directory is null or empty.");
        }
        if (!new File(aDestinationDirectory).isDirectory()) {
            throw new IllegalArgumentException("The destination directory does no exist.");
        }

        // </editor-fold>
        return aDestinationDirectory + File.separatorChar + ModelDefinitions.PREFIX_OF_SPIN_STEP_SLICE_IMAGE_FILENAME + String.valueOf(aSliceIndex) + "." + anImageFileType.toFileTypeEnding();
    }

    /**
     * Returns path name of specified move step slice file
     *
     * @param aDestinationDirectory The destination directory for the images
     * @param aSliceIndex Index of slice (0 to this.numberOfSlices - 1)
     * @param anImageFileType File type of the images to be created
     * @return Path name of specified slice file
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public String getMoveStepSliceFilePathName(String aDestinationDirectory, int aSliceIndex, ImageFileType anImageFileType) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDestinationDirectory == null || aDestinationDirectory.length() == 0) {
            throw new IllegalArgumentException("The destination directory is null or empty.");
        }
        if (!new File(aDestinationDirectory).isDirectory()) {
            throw new IllegalArgumentException("The destination directory does no exist.");
        }

        // </editor-fold>
        return aDestinationDirectory + File.separatorChar + ModelDefinitions.PREFIX_OF_SPIN_STEP_SLICE_IMAGE_FILENAME + String.valueOf(aSliceIndex) + "." + anImageFileType.toFileTypeEnding();
    }

    /**
     * Sets color buffer with gradient colors for compartments
     *
     * @param aColor Color
     * @param aColorsBuffer Colors buffer to be set
     */
    public void setGradientColorsForCompartments(Color aColor, Color[] aColorsBuffer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aColor == null || aColorsBuffer == null || aColorsBuffer.length != 3) {
            return;
        }

        // </editor-fold>
        aColorsBuffer[0] = this.getAttenuatedColor(Color.white, aColor, 1.0 - Preferences.getInstance().getColorGradientAttenuationCompartment());
        aColorsBuffer[1] = aColor;
        aColorsBuffer[2] = this.getAttenuatedColor(aColor, Color.black, Preferences.getInstance().getColorGradientAttenuationCompartment());
    }

    /**
     * Attenuates colors according to anAttenuation
     *
     * @param aColorsToBeAttenuated Colors that will be attenuated to
     * aTargetColor: Attenuation = 0.0: Returned colors = aColorsToBeAttenuated,
     * Attenuation = 1.0: Returned colors = aTargetColor)
     * @param aTargetColor Target color of attenuation
     * @param anAttenuation Attenuation (0.0: Minimum, 1.0: Maximum)
     * @return Attenuated colors or original colors if colors could not be
     * attenuated
     */
    public Color[] getAttenuatedColors(Color[] aColorsToBeAttenuated, Color aTargetColor, double anAttenuation) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aColorsToBeAttenuated == null || aColorsToBeAttenuated.length == 0 || aTargetColor == null) {
            return aColorsToBeAttenuated;
        }
        if (anAttenuation < 0.0 || anAttenuation > 1.0) {
            return aColorsToBeAttenuated;
        }

        // </editor-fold>
        Color[] tmpAttenuatedColors = new Color[aColorsToBeAttenuated.length];
        for (int i = 0; i < aColorsToBeAttenuated.length; i++) {
            tmpAttenuatedColors[i] = this.getAttenuatedColor(aColorsToBeAttenuated[i], aTargetColor, anAttenuation);
        }
        return tmpAttenuatedColors;
    }

    /**
     * Attenuates color towards a target color according to anAttenuation
     *
     * @param aColorToBeAttenuated Color that will be attenuated to
     * aTargetColor: Attenuation = 0.0: Returned color = aColorToBeAttenuated,
     * Attenuation = 1.0: Returned color = aTargetColor)
     * @param aTargetColor Target color of attenuation
     * @param anAttenuation Attenuation (0.0: Minimum, 1.0: Maximum)
     * @return Attenuated color or original color if color could not be
     * attenuated
     */
    public Color getAttenuatedColor(Color aColorToBeAttenuated, Color aTargetColor, double anAttenuation) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTargetColor == null || aColorToBeAttenuated == null) {
            return aColorToBeAttenuated;
        }
        if (anAttenuation < 0.0 || anAttenuation > 1.0) {
            return aColorToBeAttenuated;
        }

        // </editor-fold>
        int tmpRed = aColorToBeAttenuated.getRed() - (int) (anAttenuation * (double) (aColorToBeAttenuated.getRed() - aTargetColor.getRed()));
        if (tmpRed < 0) {
            tmpRed = 0;
        }
        int tmpGreen = aColorToBeAttenuated.getGreen() - (int) (anAttenuation * (double) (aColorToBeAttenuated.getGreen() - aTargetColor.getGreen()));
        if (tmpGreen < 0) {
            tmpGreen = 0;
        }
        int tmpBlue = aColorToBeAttenuated.getBlue() - (int) (anAttenuation * (double) (aColorToBeAttenuated.getBlue() - aTargetColor.getBlue()));
        if (tmpBlue < 0) {
            tmpBlue = 0;
        }
        // Alpha = 255 means completely opaque
        return new Color(tmpRed, tmpGreen, tmpBlue, 255);
    }

    /**
     * Extracts the ARGB colors from a pixel. Inverse method of
     * mergeARGBColors().
     *
     * @param aPixel The pixel to extract the colors from
     *
     * @return Integer array of size 4 containing the alpha (index 0), red
     * (index 1), green (index 2), blue (index 3) values.
     */
    public int[] extractARGBColors(int aPixel) {
        return new int[]{(aPixel >> 24) & 0xFF, (aPixel >> 16) & 0xFF, (aPixel >> 8) & 0xFF, aPixel & 0xFF};
    }

    /**
     * Returns integer pixel according to ARGB color. Inverse method to
     * extractARGBColors().
     *
     * @param anARGBValues anAlpha = anARGBValues[0], aRed = anARGBValues[1],
     * aGreen = anARGBValues[2], aBlue = anARGBValues[3]
     * @return Integer pixel according to ARGB color
     */
    public int mergeARGBColors(int[] anARGBValues) {
        return this.mergeARGBColors(anARGBValues[0], anARGBValues[1], anARGBValues[2], anARGBValues[3]);
    }

    /**
     * Returns integer pixel according to ARGB color
     *
     * @param anAlpha Alpha value
     * @param aRed Red value
     * @param aGreen Green value
     * @param aBlue Blue value
     * @return Integer pixel according to ARGB color
     */
    public int mergeARGBColors(int anAlpha, int aRed, int aGreen, int aBlue) {
        return ((anAlpha << 24) & 0xFF000000) | ((aRed << 16) & 0x00FF0000) | ((aGreen << 8) & 0x0000FF00) | (aBlue & 0x000000FF);
    }

    /**
     * Returns points along a straight line constructed from start point to end
     * point with steps of defined distance. The first point of returned list is
     * the point next to aStartPoint, the last point is anEndPoint. NOTE:
     * aStartPoint is NOT included in the list. NOTE: Due to roundoff errors the
     * two last points may be "infinitesimally" neighbored.
     *
     * @param aStartPoint Start point
     * @param anEndPoint End point
     * @param aStepDistance Step distance
     * @return Linked list with points along straight line or null if points
     * could not be calculated. The list may be empty because of the specified
     * distance.
     */
    public LinkedList<PointInSpace> getPointsInSpaceAlongStraightLine(IPointInSpace aStartPoint, IPointInSpace anEndPoint, double aStepDistance) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStartPoint == null) {
            return null;
        }
        if (anEndPoint == null) {
            return null;
        }
        if (aStepDistance <= 0.0) {
            return null;
        }

        // </editor-fold>
        LinkedList<PointInSpace> tmpReturnList = new LinkedList<PointInSpace>();
        double tmpDistance = this.getDistanceInSpace(aStartPoint, anEndPoint);
        if (tmpDistance > aStepDistance) {
            double tmpOffset = aStepDistance / tmpDistance;
            double tmpFactor = tmpOffset;
            while (tmpFactor < 1.0) {
                double tmpX = aStartPoint.getX() + tmpFactor * (anEndPoint.getX() - aStartPoint.getX());
                double tmpY = aStartPoint.getY() + tmpFactor * (anEndPoint.getY() - aStartPoint.getY());
                double tmpZ = aStartPoint.getZ() + tmpFactor * (anEndPoint.getZ() - aStartPoint.getZ());
                tmpReturnList.add(new PointInSpace(tmpX, tmpY, tmpZ));
                tmpFactor += tmpOffset;
            }
        }
        tmpReturnList.add(new PointInSpace(anEndPoint.getX(), anEndPoint.getY(), anEndPoint.getZ()));
        return tmpReturnList;
    }

    /**
     * Returns specified number of points along a straight line constructed from
     * start point to end point where the first point is the start point and the
     * last point is the end point.
     *
     * @param aStartPoint Start point
     * @param anEndPoint End point
     * @param aNumberOfPoints Number of points
     * @return Linked list with points along straight line or null if points
     * could not be created
     */
    public LinkedList<PointInSpace> getPointsInSpaceAlongStraightLine(IPointInSpace aStartPoint, IPointInSpace anEndPoint, int aNumberOfPoints) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStartPoint == null) {
            return null;
        }
        if (anEndPoint == null) {
            return null;
        }
        if (aNumberOfPoints < 2) {
            return null;
        }

        // </editor-fold>
        LinkedList<PointInSpace> tmpPointList = new LinkedList<PointInSpace>();
        if (aNumberOfPoints == 2) {
            tmpPointList.add(new PointInSpace(aStartPoint.getX(), aStartPoint.getY(), aStartPoint.getZ()));
            tmpPointList.add(new PointInSpace(anEndPoint.getX(), anEndPoint.getY(), anEndPoint.getZ()));
        } else {
            tmpPointList.add(new PointInSpace(aStartPoint.getX(), aStartPoint.getY(), aStartPoint.getZ()));
            double tmpOffset = 1.0 / (double) (aNumberOfPoints - 1);
            double tmpFactor = tmpOffset;
            for (int i = 0; i < aNumberOfPoints - 2; i++) {
                double tmpX = aStartPoint.getX() + tmpFactor * (anEndPoint.getX() - aStartPoint.getX());
                double tmpY = aStartPoint.getY() + tmpFactor * (anEndPoint.getY() - aStartPoint.getY());
                double tmpZ = aStartPoint.getZ() + tmpFactor * (anEndPoint.getZ() - aStartPoint.getZ());
                tmpPointList.add(new PointInSpace(tmpX, tmpY, tmpZ));
                tmpFactor += tmpOffset;
            }
            tmpPointList.add(new PointInSpace(anEndPoint.getX(), anEndPoint.getY(), anEndPoint.getZ()));
        }
        return tmpPointList;
    }

    /**
     * Returns specified number of graphical particle positions along a straight
     * line constructed from start point to end point where the first graphical
     * particle position is the start point and the last graphical particle
     * position is the end point. All graphical particle positions contain a
     * graphical particle.
     *
     * @param aStartPoint Start point
     * @param anEndPoint End point
     * @param aNumberOfPoints Number of points
     * @param aGraphicalParticle Graphical particle
     * @return Linked list with graphical particle positions along straight line
     * or null if graphical particle positions could not be created
     */
    public LinkedList<GraphicalParticlePosition> getGraphicalParticlePositionsInSpaceAlongStraightLine(
            IPointInSpace aStartPoint, 
            IPointInSpace anEndPoint, 
            int aNumberOfPoints,
            GraphicalParticle aGraphicalParticle
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStartPoint == null) {
            return null;
        }
        if (anEndPoint == null) {
            return null;
        }
        if (aNumberOfPoints < 2) {
            return null;
        }
        if (aGraphicalParticle == null) {
            return null;
        }

        // </editor-fold>
        LinkedList<GraphicalParticlePosition> tmpGraphicalParticlePositionList = new LinkedList<GraphicalParticlePosition>();
        if (aNumberOfPoints == 2) {
            tmpGraphicalParticlePositionList.add(new GraphicalParticlePosition(aGraphicalParticle, aStartPoint.getX(), aStartPoint.getY(), aStartPoint.getZ()));
            tmpGraphicalParticlePositionList.add(new GraphicalParticlePosition(aGraphicalParticle, anEndPoint.getX(), anEndPoint.getY(), anEndPoint.getZ()));
        } else {
            tmpGraphicalParticlePositionList.add(new GraphicalParticlePosition(aGraphicalParticle, aStartPoint.getX(), aStartPoint.getY(), aStartPoint.getZ()));
            double tmpOffset = 1.0 / (double) (aNumberOfPoints - 1);
            double tmpFactor = tmpOffset;
            for (int i = 0; i < aNumberOfPoints - 2; i++) {
                double tmpX = aStartPoint.getX() + tmpFactor * (anEndPoint.getX() - aStartPoint.getX());
                double tmpY = aStartPoint.getY() + tmpFactor * (anEndPoint.getY() - aStartPoint.getY());
                double tmpZ = aStartPoint.getZ() + tmpFactor * (anEndPoint.getZ() - aStartPoint.getZ());
                tmpGraphicalParticlePositionList.add(new GraphicalParticlePosition(aGraphicalParticle, tmpX, tmpY, tmpZ));
                tmpFactor += tmpOffset;
            }
            tmpGraphicalParticlePositionList.add(new GraphicalParticlePosition(aGraphicalParticle, anEndPoint.getX(), anEndPoint.getY(), anEndPoint.getZ()));
        }
        return tmpGraphicalParticlePositionList;
    }

    /**
     * Adds graphical particle positions to GraphicalParticlePositionArrayList 
     * instance constructed along a straight line from start point to end point 
     * where the first graphical particle position is the start point and the 
     * last graphical particle position is the end point. All graphical particle 
     * positions contain the graphical particle.
     * NOTE: The is-in-frame flag is set to true.
     *
     * @param aStartPoint Start point
     * @param anEndPoint End point
     * @param aNumberOfPoints Number of points
     * @param aGraphicalParticle Graphical particle
     * @param aGraphicalParticlePositionArrayList GraphicalParticlePositionArrayList instance to be added to
     */
    public void addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
        IPointInSpace aStartPoint, 
        IPointInSpace anEndPoint, 
        int aNumberOfPoints,
        GraphicalParticle aGraphicalParticle,
        GraphicalParticlePositionArrayList aGraphicalParticlePositionArrayList
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStartPoint == null) {
            return;
        }
        if (anEndPoint == null) {
            return;
        }
        if (aNumberOfPoints < 2) {
            return;
        }
        if (aGraphicalParticle == null) {
            return;
        }
        if (aGraphicalParticlePositionArrayList == null) {
            return;
        }
        // </editor-fold>
        // Is-in-frame flag is always set to true
        if (aNumberOfPoints == 2) {
            aGraphicalParticlePositionArrayList.add(aGraphicalParticle, aStartPoint.getX(), aStartPoint.getY(), aStartPoint.getZ(), true);
            aGraphicalParticlePositionArrayList.add(aGraphicalParticle, anEndPoint.getX(), anEndPoint.getY(), anEndPoint.getZ(), true);
        } else {
            aGraphicalParticlePositionArrayList.add(aGraphicalParticle, aStartPoint.getX(), aStartPoint.getY(), aStartPoint.getZ(), true);
            double tmpOffset = 1.0 / (double) (aNumberOfPoints - 1);
            double tmpFactor = tmpOffset;
            for (int i = 0; i < aNumberOfPoints - 2; i++) {
                double tmpX = aStartPoint.getX() + tmpFactor * (anEndPoint.getX() - aStartPoint.getX());
                double tmpY = aStartPoint.getY() + tmpFactor * (anEndPoint.getY() - aStartPoint.getY());
                double tmpZ = aStartPoint.getZ() + tmpFactor * (anEndPoint.getZ() - aStartPoint.getZ());
                aGraphicalParticlePositionArrayList.add(aGraphicalParticle, tmpX, tmpY, tmpZ, true);
                tmpFactor += tmpOffset;
            }
            aGraphicalParticlePositionArrayList.add(aGraphicalParticle, anEndPoint.getX(), anEndPoint.getY(), anEndPoint.getZ(), true);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Coordinate transformations">
    /**
     * Returns mapped 2D coordinates for 3D coordinates
     *
     * @param a3dPoints 3D points
     * @param aBoxSizeInfo Box size info
     * @param aBoxView Simulation box view
     * @param aDrawPanelCoordinatesAndSize Draw panel coordinates and size
     * @return Mapped 2D coordinates for 3D coordinates or null if mapped 2D
     * coordinates could not be created
     */
    public PointInPlane[] getMapped2dPointsForGraphicsPanel(
        PointInSpace[] a3dPoints, 
        BoxSizeInfo aBoxSizeInfo, 
        SimulationBoxViewEnum aBoxView, 
        TargetCoordinatesAndSize aDrawPanelCoordinatesAndSize
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (a3dPoints == null || a3dPoints.length == 0) {
            return null;
        }
        if (aBoxSizeInfo == null) {
            return null;
        }
        if (aDrawPanelCoordinatesAndSize == null) {
            return null;
        }
        // </editor-fold>
        PointInPlane[] tmp2dPoints = new PointInPlane[a3dPoints.length];
        for (int i = 0; i < a3dPoints.length; i++) {
            double tmpX = -1.0;
            double tmpY = -1.0;
            double tmpXmin = aBoxSizeInfo.getBoxMidPoint().getX() - aBoxSizeInfo.getRotationDisplayFrameXLength() / 2.0;
            double tmpYmin = aBoxSizeInfo.getBoxMidPoint().getY() - aBoxSizeInfo.getRotationDisplayFrameYLength() / 2.0;
            double tmpZmin = aBoxSizeInfo.getBoxMidPoint().getZ() - aBoxSizeInfo.getRotationDisplayFrameZLength() / 2.0;
            switch (aBoxView) {
                case XZ_FRONT:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (a3dPoints[i].getX() - tmpXmin) / aBoxSizeInfo.getRotationDisplayFrameXLength();
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case XZ_BACK:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (1.0 - (a3dPoints[i].getX() - tmpXmin) / aBoxSizeInfo.getRotationDisplayFrameXLength());
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case YZ_LEFT:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (1.0 - (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength());
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case YZ_RIGHT:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength();
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case XY_TOP:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (a3dPoints[i].getX() - tmpXmin) / aBoxSizeInfo.getRotationDisplayFrameXLength();
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength();
                    break;
                case XY_BOTTOM:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (a3dPoints[i].getX() - tmpXmin) / aBoxSizeInfo.getRotationDisplayFrameXLength();
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength();
                    break;
            }
            tmp2dPoints[i] = 
                new PointInPlane(
                    tmpX + (double) aDrawPanelCoordinatesAndSize.getXcoordinateWithXshift(Preferences.getInstance().getXshiftInPixelSlicer()), 
                    tmpY + (double) aDrawPanelCoordinatesAndSize.getYcoordinateWithYshift(Preferences.getInstance().getYshiftInPixelSlicer())
                );
        }
        return tmp2dPoints;
    }

    /**
     * Returns if mapped 2D coordinates for 3D coordinates are outside the
     * drawing area
     *
     * @param a3dPoints 3D points
     * @param aBoxSizeInfo Box size info
     * @param aBoxView Simulation box view
     * @param aDrawPanelCoordinatesAndSize Draw panel coordinates and size
     * @return True: Mapped 2D coordinates for 3D coordinates are outside the
     * drawing area, false: Otherwise
     */
    public boolean isMapped2dPointForGraphicsPanelOutsideDrawingArea(
        PointInSpace[] a3dPoints, 
        BoxSizeInfo aBoxSizeInfo, 
        SimulationBoxViewEnum aBoxView, 
        TargetCoordinatesAndSize aDrawPanelCoordinatesAndSize
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (a3dPoints == null || a3dPoints.length == 0) {
            return false;
        }
        if (aBoxSizeInfo == null) {
            return false;
        }
        if (aDrawPanelCoordinatesAndSize == null) {
            return false;
        }
        // </editor-fold>
        for (int i = 0; i < a3dPoints.length; i++) {
            double tmpXmin = aBoxSizeInfo.getBoxMidPoint().getX() - aBoxSizeInfo.getRotationDisplayFrameXLength() / 2.0;
            double tmpYmin = aBoxSizeInfo.getBoxMidPoint().getY() - aBoxSizeInfo.getRotationDisplayFrameYLength() / 2.0;
            double tmpZmin = aBoxSizeInfo.getBoxMidPoint().getZ() - aBoxSizeInfo.getRotationDisplayFrameZLength() / 2.0;
            double tmpX = -1.0;
            double tmpY = -1.0;
            switch (aBoxView) {
                case XZ_FRONT:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (a3dPoints[i].getX() - tmpXmin) / aBoxSizeInfo.getRotationDisplayFrameXLength();
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case XZ_BACK:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (1.0 - (a3dPoints[i].getX() - tmpXmin) / aBoxSizeInfo.getRotationDisplayFrameXLength());
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case YZ_LEFT:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (1.0 - (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength());
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case YZ_RIGHT:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength();
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case XY_TOP:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (a3dPoints[i].getX() - tmpXmin) / aBoxSizeInfo.getRotationDisplayFrameXLength();
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength();
                    break;
                case XY_BOTTOM:
                    tmpX = (double) aDrawPanelCoordinatesAndSize.getWidth() * (a3dPoints[i].getX() - tmpXmin) / aBoxSizeInfo.getRotationDisplayFrameXLength();
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength();
                    break;
                default:
                    return false;
            }
            if (tmpX < 0.0 
                || tmpX > aDrawPanelCoordinatesAndSize.getWidth()
                || tmpY < 0.0 
                || tmpY > aDrawPanelCoordinatesAndSize.getHeight()) 
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if mapped 2D coordinates for 3D coordinates are above or below
     * the drawing area
     *
     * @param a3dPoints 3D points
     * @param aBoxSizeInfo Box size info
     * @param aBoxView Simulation box view
     * @param aDrawPanelCoordinatesAndSize Draw panel coordinates and size
     * @return True: Mapped 2D coordinates for 3D coordinates are outside the
     * drawing area, false: Otherwise
     */
    public boolean isMapped2dPointForGraphicsPanelAboveOrBelowDrawingArea(
        PointInSpace[] a3dPoints, 
        BoxSizeInfo aBoxSizeInfo, 
        SimulationBoxViewEnum aBoxView, 
        TargetCoordinatesAndSize aDrawPanelCoordinatesAndSize
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (a3dPoints == null || a3dPoints.length == 0) {
            return false;
        }
        if (aBoxSizeInfo == null) {
            return false;
        }
        if (aDrawPanelCoordinatesAndSize == null) {
            return false;
        }
        // </editor-fold>
        for (int i = 0; i < a3dPoints.length; i++) {
            double tmpYmin = aBoxSizeInfo.getBoxMidPoint().getY() - aBoxSizeInfo.getRotationDisplayFrameYLength() / 2.0;
            double tmpZmin = aBoxSizeInfo.getBoxMidPoint().getZ() - aBoxSizeInfo.getRotationDisplayFrameZLength() / 2.0;
            double tmpY = -1.0;
            switch (aBoxView) {
                case XZ_FRONT:
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case XZ_BACK:
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case YZ_LEFT:
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case YZ_RIGHT:
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getZ() - tmpZmin) / aBoxSizeInfo.getRotationDisplayFrameZLength();
                    break;
                case XY_TOP:
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() - (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength();
                    break;
                case XY_BOTTOM:
                    tmpY = (double) aDrawPanelCoordinatesAndSize.getHeight() * (a3dPoints[i].getY() - tmpYmin) / aBoxSizeInfo.getRotationDisplayFrameYLength();
                    break;
                default:
                    return false;
            }
            if (tmpY < 0.0 || tmpY > aDrawPanelCoordinatesAndSize.getHeight()) 
            {
                return true;
            }
        }
        return false;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Shift related methods">
    /**
     * Shifts points with periodic boundaries defined by box size info
     * 
     * @param aPointsInSpaceArray Array of points in space
     * @param aShiftX Shift in x
     * @param aShiftY Shift in y
     * @param aShiftZ Shift in z
     * @param aBoxSizeInfo Box size info for periodic boundaries
     */
    public void shiftPointsWithPeriodicBoundaries(
        IPointInSpace[] aPointsInSpaceArray, 
        double aShiftX, 
        double aShiftY, 
        double aShiftZ, 
        BoxSizeInfo aBoxSizeInfo
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPointsInSpaceArray == null || aPointsInSpaceArray.length == 0) {
            return;
        }
        if (aBoxSizeInfo == null) {
            return;
        }
        if (aShiftX == 0.0 && aShiftY == 0.0 && aShiftZ == 0.0) {
            return;
        }
        // </editor-fold>
        for (IPointInSpace tmpPointInSpace: aPointsInSpaceArray) {
            if (tmpPointInSpace != null) {
                tmpPointInSpace.setX(this.getCorrectedValue(tmpPointInSpace.getX() + aShiftX, aBoxSizeInfo.getXMin(), aBoxSizeInfo.getXMax(), aBoxSizeInfo.getXLength()));
                tmpPointInSpace.setY(this.getCorrectedValue(tmpPointInSpace.getY() + aShiftY, aBoxSizeInfo.getYMin(), aBoxSizeInfo.getYMax(), aBoxSizeInfo.getYLength()));
                tmpPointInSpace.setZ(this.getCorrectedValue(tmpPointInSpace.getZ() + aShiftZ, aBoxSizeInfo.getZMin(), aBoxSizeInfo.getZMax(), aBoxSizeInfo.getZLength()));
            }
        }
    }
    
    /**
     * Shifts point with periodic boundaries defined by box size info
     * 
     * @param aPointsInSpace Points in space
     * @param aShiftX Shift in x
     * @param aShiftY Shift in y
     * @param aShiftZ Shift in z
     * @param aBoxSizeInfo Box size info for periodic boundaries
     */
    public void shiftPointWithPeriodicBoundaries(
        IPointInSpace aPointsInSpace, 
        double aShiftX, 
        double aShiftY, 
        double aShiftZ, 
        BoxSizeInfo aBoxSizeInfo
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPointsInSpace == null) {
            return;
        }
        if (aBoxSizeInfo == null) {
            return;
        }
        // </editor-fold>
        aPointsInSpace.setX(this.getCorrectedValue(aPointsInSpace.getX() + aShiftX, aBoxSizeInfo.getXMin(), aBoxSizeInfo.getXMax(), aBoxSizeInfo.getXLength()));
        aPointsInSpace.setY(this.getCorrectedValue(aPointsInSpace.getY() + aShiftY, aBoxSizeInfo.getYMin(), aBoxSizeInfo.getYMax(), aBoxSizeInfo.getYLength()));
        aPointsInSpace.setZ(this.getCorrectedValue(aPointsInSpace.getZ() + aShiftZ, aBoxSizeInfo.getZMin(), aBoxSizeInfo.getZMax(), aBoxSizeInfo.getZLength()));
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Rotation related methods">
    /**
     * Rotates single point in space.
     * NOTE: Passed point object is changed with rotated coordinates!
     *
     * @param aPointInSpace Point in space (is changed = rotated if not null)
     * @param anAlphaAngle Angle in degree for rotation around x axis
     * @param aGammaAngle Angle in degree for rotation around y axis
     * @param aBetaAngle Angle in degree for rotation around z axis
     */
    public void rotateSinglePoint(
        PointInSpace aPointInSpace, 
        double anAlphaAngle, 
        double aGammaAngle, 
        double aBetaAngle
    ) {
        this.rotatePoints(
            new PointInSpace[]{aPointInSpace}, 
            anAlphaAngle, 
            aGammaAngle, 
            aBetaAngle, 
            null
        );
    }

    /**
     * Rotates array of points in space.
     * NOTE: Passed point objects are changed with rotated coordinates!
     *
     * @param aPointsInSpaceArray Array with point in space (points of array are
     * changed if not null)
     * @param anAlphaAngle Angle in degree for rotation around x axis
     * @param aGammaAngle Angle in degree for rotation around y axis
     * @param aBetaAngle Angle in degree for rotation around z axis
     * @param aBoxMidPoint Mid point of box so that rotation does not change
     * this point: All rotated point are translated in space to keep this mid
     * point position (see code). May be null then no translation occurs. Object
     * aBoxMidPoint is not changed.
     */
    public void rotatePoints(
        IPointInSpace[] aPointsInSpaceArray, 
        double anAlphaAngle, 
        double aGammaAngle, 
        double aBetaAngle, 
        PointInSpace aBoxMidPoint
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPointsInSpaceArray == null || aPointsInSpaceArray.length == 0) {
            return;
        }
        // </editor-fold>
        this.rotatePoints(
            aPointsInSpaceArray,
            aPointsInSpaceArray.length,
            anAlphaAngle, 
            aGammaAngle, 
            aBetaAngle, 
            aBoxMidPoint
        );        
    }
    
    /**
     * Rotates points in aGraphicalParticlePositionArrayList. 
     * NOTE: Positions are changed with rotated coordinates!
     *
     * @param aGraphicalParticlePositionArrayList GraphicalParticlePositionArrayList instance 
     * (positions are changed if not null)
     * @param anAlphaAngle Angle in degree for rotation around x axis
     * @param aGammaAngle Angle in degree for rotation around y axis
     * @param aBetaAngle Angle in degree for rotation around z axis
     * @param aBoxMidPoint Mid point of box so that rotation does not change
     * this point: All rotated point are translated in space to keep this mid
     * point position (see code). May be null then no translation occurs. Object
     * aBoxMidPoint is not changed.
     */
    public void rotatePoints(
        GraphicalParticlePositionArrayList aGraphicalParticlePositionArrayList, 
        double anAlphaAngle, 
        double aGammaAngle, 
        double aBetaAngle, 
        PointInSpace aBoxMidPoint
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aGraphicalParticlePositionArrayList == null || aGraphicalParticlePositionArrayList.getSize() == 0) {
            return;
        }
        // </editor-fold>
        this.rotatePoints(
            aGraphicalParticlePositionArrayList.getGraphicalParticlePositions(),
            aGraphicalParticlePositionArrayList.getSize(),
            anAlphaAngle, 
            aGammaAngle, 
            aBetaAngle, 
            aBoxMidPoint
        );        
    }

    /**
     * Rotates array of points in space. 
     * NOTE: Passed point objects are changed with rotated coordinates!
     *
     * @param aPointsInSpaceArray Array with points in space (points of array 
     * are changed if not null, length must be greater/equal to anArrayLength)
     * @param anArrayLength Length of aPointsInSpaceArray to be rotated
     * @param anAlphaAngle Angle in degree for rotation around x axis
     * @param aGammaAngle Angle in degree for rotation around y axis
     * @param aBetaAngle Angle in degree for rotation around z axis
     * @param aBoxMidPoint Mid point of box so that rotation does not change
     * this point: All rotated point are translated in space to keep this mid
     * point position (see code). May be null then no translation occurs. Object
     * aBoxMidPoint is not changed.
     * @exception IllegalArgumentException Thrown if anArrayLength is greater than length of aPointsInSpaceArray
     */
    public void rotatePoints(
        IPointInSpace[] aPointsInSpaceArray,
        int anArrayLength,
        double anAlphaAngle, 
        double aGammaAngle, 
        double aBetaAngle, 
        PointInSpace aBoxMidPoint
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPointsInSpaceArray == null || aPointsInSpaceArray.length == 0) {
            return;
        }
        if (aPointsInSpaceArray.length < anArrayLength) {
            throw new IllegalArgumentException("UtilityGraphicsMethods.rotatePoints: aPointsInSpaceArray.length < anArrayLength");
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Rotate">
        // <editor-fold defaultstate="collapsed" desc="- Initialisation">
        PointInSpace tmpTranslationVector = null;
        if (aBoxMidPoint != null) {
            PointInSpace tmpBoxMidPointToBeRotated = aBoxMidPoint.getClone();
            this.rotateSinglePoint(tmpBoxMidPointToBeRotated, anAlphaAngle, aGammaAngle, aBetaAngle);
            tmpTranslationVector = 
                new PointInSpace(
                    aBoxMidPoint.getX() - tmpBoxMidPointToBeRotated.getX(),
                    aBoxMidPoint.getY() - tmpBoxMidPointToBeRotated.getY(),
                    aBoxMidPoint.getZ() - tmpBoxMidPointToBeRotated.getZ()
                );
        }
        double tmpX;
        double tmpY;
        double tmpZ;
        double tmpRotatedX;
        double tmpRotatedY;
        double tmpRotatedZ;
        double tmpCosAlpha = Math.cos(anAlphaAngle * PI_DIVIDED_BY_180);
        double tmpSinAlpha = Math.sin(anAlphaAngle * PI_DIVIDED_BY_180);
        double tmpCosGamma = Math.cos(aGammaAngle * PI_DIVIDED_BY_180);
        double tmpSinGamma = Math.sin(aGammaAngle * PI_DIVIDED_BY_180);
        double tmpCosBeta = Math.cos(aBetaAngle * PI_DIVIDED_BY_180);
        double tmpSinBeta = Math.sin(aBetaAngle * PI_DIVIDED_BY_180);
        // </editor-fold>
        for (int i = 0; i < anArrayLength; i++) {
            IPointInSpace tmpSinglePointInSpace = aPointsInSpaceArray[i];
            if (tmpSinglePointInSpace != null) {
                tmpX = tmpSinglePointInSpace.getX();
                tmpY = tmpSinglePointInSpace.getY();
                tmpZ = tmpSinglePointInSpace.getZ();
                // <editor-fold defaultstate="collapsed" desc="- Rotation around x axis">
                if (anAlphaAngle != 0.0) {
                    tmpRotatedY = tmpY * tmpCosAlpha - tmpZ * tmpSinAlpha;
                    tmpRotatedZ = tmpZ * tmpCosAlpha + tmpY * tmpSinAlpha;
                    tmpY = tmpRotatedY;
                    tmpZ = tmpRotatedZ;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Rotation around y axis">
                if (aGammaAngle != 0.0) {
                    tmpRotatedX = tmpX * tmpCosGamma - tmpZ * tmpSinGamma;
                    tmpRotatedZ = tmpZ * tmpCosGamma + tmpX * tmpSinGamma;
                    tmpX = tmpRotatedX;
                    tmpZ = tmpRotatedZ;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Rotation around z axis">
                if (aBetaAngle != 0.0) {
                    tmpRotatedX = tmpX * tmpCosBeta - tmpY * tmpSinBeta;
                    tmpRotatedY = tmpY * tmpCosBeta + tmpX * tmpSinBeta;
                    tmpX = tmpRotatedX;
                    tmpY = tmpRotatedY;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Set rotated and possibly translated point">
                if (tmpTranslationVector == null) {
                    tmpSinglePointInSpace.setX(tmpX);
                    tmpSinglePointInSpace.setY(tmpY);
                    tmpSinglePointInSpace.setZ(tmpZ);
                } else {
                    tmpSinglePointInSpace.setX(tmpX + tmpTranslationVector.getX());
                    tmpSinglePointInSpace.setY(tmpY + tmpTranslationVector.getY());
                    tmpSinglePointInSpace.setZ(tmpZ + tmpTranslationVector.getZ());
                }
                // </editor-fold>
            }
        }
        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Bulk related methods">
    /**
     * Checks if protein with 3D structure in bulk is defined in specified row
     *
     * @param aBulkInfoValueItem Bulk info value item (is NOT changed)
     * @param aRow Row
     * @return True: Protein with 3D structure in bulk is defined , false:
     * Otherwise
     */
    public boolean isProtein3dStructureInBulk(ValueItem aBulkInfoValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBulkInfoValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aBulkInfoValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        return aBulkInfoValueItem.getValue(aRow, 4).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DStructure"))
            || aBulkInfoValueItem.getValue(aRow, 4).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"));
    }

    /**
     * Checks if random bulk 3D structure (spheres) geometry is defined in
     * specified row
     *
     * @param aBulkInfoValueItem Bulk info value item (is NOT changed)
     * @param aRow Row
     * @return True: Random 3D structure (spheres) in bulk is defined, false:
     * Otherwise
     */
    public boolean isRandom3dStructureGeometryInBulk(ValueItem aBulkInfoValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBulkInfoValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aBulkInfoValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        return aBulkInfoValueItem.getValue(aRow, 4).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"));
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Sphere related methods">
    /**
     * Checks if protein with 3D structure in sphere is defined in specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * sphere (is NOT changed)
     * @param aRow Row
     * @return True: Protein with 3D structure in sphere is defined , false:
     * Otherwise
     */
    public boolean isProtein3dStructureInSphere(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DStructure"))
            || aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"));
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Sphere geometry related methods">
    /**
     * Checks if sphere upper surfaces geometry is defined in specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * sphere (is NOT changed)
     * @param aRow Row
     * @return True: Sphere upper surface geometry is defined, false: Otherwise
     */
    public boolean isUpperSurfaceGeometryInSphere(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomUpperSurfaceOfSphere"));
    }

    /**
     * Checks if sphere middle surfaces geometry is defined in specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * sphere (is NOT changed)
     * @param aRow Row
     * @return True: Sphere upper surface geometry is defined, false: Otherwise
     */
    public boolean isMiddleSurfaceGeometryInSphere(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomMiddleSurfaceOfSphere"));
    }

    /**
     * Checks if random sphere 3D structure (spheres) geometry is defined in
     * specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * sphere (is NOT changed)
     * @param aRow Row
     * @return True: Random 3D structure (spheres) geometry is defined, false:
     * Otherwise
     */
    public boolean isRandom3dStructureGeometryInSphere(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"));
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- xy-layer geometry and lattice structure related methods">
    /**
     * Checks if lattice geometry is defined in xy-layer
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @return True: Lattice geometry is defined, false: Otherwise
     */
    public boolean isLatticeGeometryInXyLayer(ValueItem aChemicalCompositionValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }

        // </editor-fold>
        // Lattice geometry must be defined (NOTE: All checks have been performed before, i.e. simple cubic lattice can not be set if not possible)
        boolean tmpIsLatticeGeometryInXyLayer = false;
        for (int i = 0; i < aChemicalCompositionValueItem.getMatrixRowCount(); i++) {
            if (aChemicalCompositionValueItem.getValue(i, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.simpleCubicLattice"))) {
                tmpIsLatticeGeometryInXyLayer = true;
                break;
            }
        }
        return tmpIsLatticeGeometryInXyLayer;
    }

    /**
     * Checks if xy-layer 3D structure (spheres) geometry is defined in
     * specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: 3D structure (spheres) geometry is defined, false:
     * Otherwise
     */
    public boolean is3dStructureGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DStructure"))
            || aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"));
    }

    /**
     * Checks if random xy-layer 3D structure (spheres) geometry is defined in
     * specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: Random 3D structure (spheres) geometry is defined, false:
     * Otherwise
     */
    public boolean isRandom3dStructureGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.3DRandom"));
    }

    /**
     * Checks if xy-layer single surface geometry is defined in specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: Single surface geometry is defined, false: Otherwise
     */
    public boolean isSingleSurfaceGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopSurfaceOfXyLayer"))
            || aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyBottomSurfaceOfXyLayer"))
            || aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzLeftSurfaceOfXyLayer"))
            || aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzRightSurfaceOfXyLayer"))
            || aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzFrontSurfaceOfXyLayer"))
            || aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzBackSurfaceOfXyLayer"));
    }

    /**
     * Checks if xy-layer single surface xy top geometry is defined in specified
     * row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: Single surface xy top geometry is defined, false: Otherwise
     */
    public boolean isSingleSurfaceXyTopGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopSurfaceOfXyLayer"));
    }

    /**
     * Checks if xy-layer single surface xy bottom geometry is defined in
     * specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: Single surface xy bottom geometry is defined, false:
     * Otherwise
     */
    public boolean isSingleSurfaceXyBottomGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyBottomSurfaceOfXyLayer"));
    }

    /**
     * Checks if xy-layer single surface yz left geometry is defined in
     * specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: Single surface yz left geometry is defined, false:
     * Otherwise
     */
    public boolean isSingleSurfaceYzLeftGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzLeftSurfaceOfXyLayer"));
    }

    /**
     * Checks if xy-layer single surface yz right geometry is defined in
     * specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: Single surface yz right geometry is defined, false:
     * Otherwise
     */
    public boolean isSingleSurfaceYzRightGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzRightSurfaceOfXyLayer"));
    }

    /**
     * Checks if xy-layer single surface xz front geometry is defined in
     * specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: Single surface xz front geometry is defined, false:
     * Otherwise
     */
    public boolean isSingleSurfaceXzFrontGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzFrontSurfaceOfXyLayer"));
    }

    /**
     * Checks if xy-layer single surface xz back geometry is defined in
     * specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: Single surface xz back geometry is defined, false:
     * Otherwise
     */
    public boolean isSingleSurfaceXzBackGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzBackSurfaceOfXyLayer"));
    }

    /**
     * Checks if xy-layer xy top/bottom surface geometry is defined in specified
     * row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: xy top/bottom surface geometry is defined, false: Otherwise
     */
    public boolean isXyTopBottomGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXyTopBottomSurfaceOfXyLayer"));
    }

    /**
     * Checks if xy-layer yz left/right surface geometry is defined in specified
     * row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: yz left/right surface geometry is defined, false: Otherwise
     */
    public boolean isYzLeftRightGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomYzLeftRightSurfaceOfXyLayer"));
    }

    /**
     * Checks if xy-layer xz front/back surface geometry is defined in specified
     * row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: xz front/back surface geometry is defined, false: Otherwise
     */
    public boolean isXzFrontBackGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomXzFrontBackSurfaceOfXyLayer"));
    }
    
    /**
     * Checks if xy-layer all surfaces geometry is defined in specified row
     *
     * @param aChemicalCompositionValueItem Chemical composition value item of
     * xy-layer (is NOT changed)
     * @param aRow Row
     * @return True: All surfaces geometry is defined, false: Otherwise
     */
    public boolean isAllSurfacesGeometryInXyLayer(ValueItem aChemicalCompositionValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChemicalCompositionValueItem == null) {
            return false;
        }
        if (aRow < 0 || aRow >= aChemicalCompositionValueItem.getMatrixRowCount()) {
            return false;
        }

        // </editor-fold>
        return aChemicalCompositionValueItem.getValue(aRow, 7).equals(ModelMessage.get("CompartmentContainer.parameter.Orientation.randomAllSurfacesOfXyLayer"));
    }

    /**
     * Fills specified point array with positions of a simple cubic lattice
     * within specified cuboid and the specified target bond length.
     *
     * @param aBuffer Points to be set with positions of a simple cubic lattice
     * @param aStartHeightZ Z-height of cuboid
     * @param aTargetBondLength Target bond length
     * @param aCuboidLengthX X-length of cuboid
     * @param aCuboidLengthY Y-length of cuboid
     * @param aCuboidLengthZ Z-length of cuboid
     */
    public void fillSimpleCubicLatticePositions(IPointInSpace[] aBuffer, double aStartHeightZ, double aTargetBondLength, double aCuboidLengthX, double aCuboidLengthY, double aCuboidLengthZ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBuffer == null || aBuffer.length == 0) {
            return;
        }
        if (aStartHeightZ < 0.0) {
            return;
        }
        if (aTargetBondLength <= 0.0) {
            return;
        }
        if (aCuboidLengthX <= 0.0) {
            return;
        }
        if (aCuboidLengthY <= 0.0) {
            return;
        }
        if (aCuboidLengthY <= 0.0) {
            return;
        }
        if (aTargetBondLength > aCuboidLengthX || aTargetBondLength > aCuboidLengthY || aTargetBondLength > aCuboidLengthZ) {
            return;
        }

        // </editor-fold>
        // FIRST check if instance of GraphicalParticlePosition[]
        boolean tmpIsGraphicalParticlePositions = false;
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // A buffer is an instance of GraphicalParticlePosition[]
            tmpIsGraphicalParticlePositions = true;
        }

        // NOTE: First position is not surface of cuboid but half bond length in every dimension!
        int tmpNumberOfPoints = aBuffer.length;
        int tmpNx = (int) Math.floor((aCuboidLengthX - aTargetBondLength) / aTargetBondLength) + 1;
        int tmpNy = (int) Math.floor((aCuboidLengthY - aTargetBondLength) / aTargetBondLength) + 1;
        int tmpNz = (int) Math.floor((aCuboidLengthZ - aTargetBondLength) / aTargetBondLength) + 1;

        if (tmpNx * tmpNy * tmpNz > tmpNumberOfPoints) {

            // <editor-fold defaultstate="collapsed" desc="Shrink x and y numbers (since z number is usually much smaller)">
            int tmpChange = 1;
            while (tmpNx * tmpNy * tmpNz > tmpNumberOfPoints) {
                switch (tmpChange) {
                    case 1:
                        tmpNx--;
                        tmpChange = 2;
                        break;
                    case 2:
                        tmpNy--;
                        tmpChange = 3;
                        break;
                    case 3:
                        tmpNz--;
                        tmpChange = 1;
                        break;
                }
            }

            // </editor-fold>
        } else {

            // <editor-fold defaultstate="collapsed" desc="Enlarge x and y numbers (since z number is usually much smaller)">
            int tmpChange = 1;
            while (tmpNx * tmpNy * tmpNz < tmpNumberOfPoints) {
                switch (tmpChange) {
                    case 1:
                        tmpNx++;
                        tmpChange = 2;
                        break;
                    case 2:
                        tmpNy++;
                        tmpChange = 3;
                        break;
                    case 3:
                        tmpNz++;
                        tmpChange = 1;
                        break;
                }
            }

            // </editor-fold>
        }

        // NOTE: First position is not surface of cuboid but half bond length in every dimension:
        //       bondLength = (cuboidLength - bondLength)/ (tmpNx - 1)
        //       bondLength = cuboidLength / ((tmpNx - 1) + 1)
        double tmpBondLenghtX = aCuboidLengthX / (double) tmpNx;
        double tmpBondLenghtY = aCuboidLengthY / (double) tmpNy;
        double tmpBondLenghtZ = aCuboidLengthZ / (double) tmpNz;

        int tmpIndex = 0;
        boolean tmpBreakAll = false;
        double tmpCurrentZ = aStartHeightZ + aCuboidLengthZ + 0.5 * tmpBondLenghtZ;
        for (int i = 0; i < tmpNz; i++) {
            tmpCurrentZ -= tmpBondLenghtZ;
            double tmpCurrentY = 0.5 * tmpBondLenghtY;
            for (int j = 0; j < tmpNy; j++) {
                double tmpCurrentX = 0.5 * tmpBondLenghtX;
                for (int k = 0; k < tmpNx; k++) {
                    if (tmpIndex < tmpNumberOfPoints) {
                        if (tmpIsGraphicalParticlePositions) {
                            aBuffer[tmpIndex++] = new GraphicalParticlePosition(tmpCurrentX, tmpCurrentY, tmpCurrentZ);
                        } else {
                            aBuffer[tmpIndex++] = new PointInSpace(tmpCurrentX, tmpCurrentY, tmpCurrentZ);
                        }
                    } else {
                        tmpBreakAll = true;
                        break;
                    }
                    tmpCurrentX += tmpBondLenghtX;
                }
                if (tmpBreakAll) {
                    break;
                }
                tmpCurrentY += tmpBondLenghtY;
            }
            if (tmpBreakAll) {
                break;
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Random points related methods">
    /**
     * Fills buffer with random points in sphere
     *
     * @param aBuffer Buffer for random points in sphere
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of sphere
     * @param aRadius Radius of sphere
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsInSphere(
        IPointInSpace[] aBuffer, 
        int aFirstIndex, 
        int aNumber, 
        IPointInSpace aCenterPoint, 
        double aRadius,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aRadius <= 0) {
            throw new IllegalArgumentException("aRadius is less/equal 0.");
        }
        // </editor-fold>
        // Reduce value to avoid points outside the compartment due to round-off errors
        double tmpRadius = aRadius * ModelDefinitions.DECREASE_FACTOR;
        double tmpCubeLength = 2.0 * tmpRadius;
        double tmpRadiusSquare = tmpRadius * tmpRadius;
        double tmpDeltaX = 0;
        double tmpDeltaY = 0;
        double tmpDeltaZ = 0;
        double tmpX = 0;
        double tmpY = 0;
        double tmpZ = 0;
        int tmpIndex = aFirstIndex;
        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                do {
                    tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpDeltaX = tmpX - tmpRadius;
                    tmpDeltaY = tmpY - tmpRadius;
                    tmpDeltaZ = tmpZ - tmpRadius;
                } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpRadiusSquare);
                aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() - tmpRadius + tmpX, aCenterPoint.getY() - tmpRadius + tmpY, aCenterPoint.getZ() - tmpRadius + tmpZ);
            }
            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                do {
                    tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpDeltaX = tmpX - tmpRadius;
                    tmpDeltaY = tmpY - tmpRadius;
                    tmpDeltaZ = tmpZ - tmpRadius;
                } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpRadiusSquare);
                aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() - tmpRadius + tmpX, aCenterPoint.getY() - tmpRadius + tmpY, aCenterPoint.getZ() - tmpRadius + tmpZ);
            }
            // </editor-fold>
        }
    }

    /**
     * Fills buffers 1 and 2 with random points in sphere
     *
     * @param aBuffer1 Buffer 1 for random points in sphere
     * @param aBuffer2 Buffer 2 for random points in sphere
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of sphere
     * @param aRadius Radius of sphere
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsInSphere(
        IPointInSpace[] aBuffer1, 
        IPointInSpace[] aBuffer2, 
        int aFirstIndex, 
        int aNumber, 
        IPointInSpace aCenterPoint,
        double aRadius, 
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer1 == null || aBuffer1.length == 0) {
            throw new IllegalArgumentException("aBuffer1 is null/empty.");
        }
        if (aBuffer1.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer1 is too small.");
        }
        if (aBuffer2 == null || aBuffer2.length == 0) {
            throw new IllegalArgumentException("aBuffer2 is null/empty.");
        }
        if (aBuffer2.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer2 is too small.");
        }
        if (aBuffer1 instanceof GraphicalParticlePosition[]) {
            if (!(aBuffer1 instanceof GraphicalParticlePosition[])) {
                throw new IllegalArgumentException("aBuffer1 is instance of GraphicalParticlePosition[] but aBuffer2 is not.");
            }
        } else if (aBuffer1 instanceof PointInSpace[]) {
            if (!(aBuffer1 instanceof PointInSpace[])) {
                throw new IllegalArgumentException("aBuffer1 is instance of PointInSpace[] but aBuffer2 is not.");
            }
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aRadius <= 0) {
            throw new IllegalArgumentException("aRadius is less/equal 0.");
        }

        // </editor-fold>
        // Reduce value to avoid points outside the compartment due to round-off errors
        double tmpRadius = aRadius * ModelDefinitions.DECREASE_FACTOR;
        double tmpCubeLength = 2.0 * tmpRadius;
        double tmpRadiusSquare = tmpRadius * tmpRadius;
        double tmpDeltaX = 0;
        double tmpDeltaY = 0;
        double tmpDeltaZ = 0;
        double tmpX = 0;
        double tmpY = 0;
        double tmpZ = 0;
        int tmpIndex1 = aFirstIndex;
        int tmpIndex2 = aFirstIndex;

        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer1 instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                do {
                    tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpDeltaX = tmpX - tmpRadius;
                    tmpDeltaY = tmpY - tmpRadius;
                    tmpDeltaZ = tmpZ - tmpRadius;
                } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpRadiusSquare);
                aBuffer1[tmpIndex1++] = new GraphicalParticlePosition(aCenterPoint.getX() - tmpRadius + tmpX, aCenterPoint.getY() - tmpRadius + tmpY, aCenterPoint.getZ() - tmpRadius + tmpZ);
                do {
                    tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpDeltaX = tmpX - tmpRadius;
                    tmpDeltaY = tmpY - tmpRadius;
                    tmpDeltaZ = tmpZ - tmpRadius;
                } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpRadiusSquare);
                aBuffer2[tmpIndex2++] = new GraphicalParticlePosition(aCenterPoint.getX() - tmpRadius + tmpX, aCenterPoint.getY() - tmpRadius + tmpY, aCenterPoint.getZ() - tmpRadius + tmpZ);
            }

            // </editor-fold>
        } else if (aBuffer1 instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                do {
                    tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpDeltaX = tmpX - tmpRadius;
                    tmpDeltaY = tmpY - tmpRadius;
                    tmpDeltaZ = tmpZ - tmpRadius;
                } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpRadiusSquare);
                aBuffer1[tmpIndex1++] = new PointInSpace(aCenterPoint.getX() - tmpRadius + tmpX, aCenterPoint.getY() - tmpRadius + tmpY, aCenterPoint.getZ() - tmpRadius + tmpZ);
                do {
                    tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpDeltaX = tmpX - tmpRadius;
                    tmpDeltaY = tmpY - tmpRadius;
                    tmpDeltaZ = tmpZ - tmpRadius;
                } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpRadiusSquare);
                aBuffer2[tmpIndex2++] = new PointInSpace(aCenterPoint.getX() - tmpRadius + tmpX, aCenterPoint.getY() - tmpRadius + tmpY, aCenterPoint.getZ() - tmpRadius + tmpZ);
            }
            // </editor-fold>
        }
    }

    /**
     * Fills buffer with random points in xy-Layer
     *
     * @param aBuffer Buffer for random points in xy-layer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of xy-layer
     * @param aXLength X-Length of xy-layer
     * @param aYLength Y-Length of xy-layer
     * @param aZLength Z-Length of xy-layer
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsInXyLayer(
        IPointInSpace[] aBuffer, 
        int aFirstIndex, 
        int aNumber, 
        IPointInSpace aCenterPoint, 
        double aXLength, 
        double aYLength, 
        double aZLength,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aXLength <= 0) {
            throw new IllegalArgumentException("aXLength is less/equal 0.");
        }
        if (aYLength <= 0) {
            throw new IllegalArgumentException("aYLength is less/equal 0.");
        }
        if (aZLength <= 0) {
            throw new IllegalArgumentException("aZLength is less/equal 0.");
        }
        // </editor-fold>
        // Reduce values to avoid points outside the compartment due to round-off errors
        double tmpXLength = aXLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpYLength = aYLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpZLength = aZLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpHalfXLength = tmpXLength / 2.0;
        double tmpHalfYLength = tmpYLength / 2.0;
        double tmpHalfZLength = tmpZLength / 2.0;
        double tmpDeltaX;
        double tmpDeltaY;
        double tmpDeltaZ;
        int tmpIndex = aFirstIndex;

        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
            }
            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
            }
            // </editor-fold>
        }
    }

    /**
     * Fills buffers 1 and 2 with random points in xy-Layer
     *
     * @param aBuffer1 Buffer 1 for random points in xy-layer
     * @param aBuffer2 Buffer 2 for random points in xy-layer
     * @param aFirstIndex First index in both buffers
     * @param aNumber Number of random points in both buffers (not allowed to be
     * less than 1)
     * @param aCenterPoint Center point of xy-layer
     * @param aXLength X-Length of xy-layer
     * @param aYLength Y-Length of xy-layer
     * @param aZLength Z-Length of xy-layer
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsInXyLayer(
        IPointInSpace[] aBuffer1, 
        IPointInSpace[] aBuffer2, 
        int aFirstIndex, 
        int aNumber, 
        IPointInSpace aCenterPoint, 
        double aXLength,
        double aYLength, 
        double aZLength,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer1 == null || aBuffer1.length == 0) {
            throw new IllegalArgumentException("aBuffer1 is null/empty.");
        }
        if (aBuffer2 == null || aBuffer2.length == 0) {
            throw new IllegalArgumentException("aBuffer2 is null/empty.");
        }
        if (aBuffer1.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer1 is too small.");
        }
        if (aBuffer2.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer2 is too small.");
        }
        if ((aBuffer1 instanceof GraphicalParticlePosition[]) && !(aBuffer2 instanceof GraphicalParticlePosition[])) {
            throw new IllegalArgumentException("Both buffers must be instance of the same class.");
        }
        if ((aBuffer2 instanceof GraphicalParticlePosition[]) && !(aBuffer1 instanceof GraphicalParticlePosition[])) {
            throw new IllegalArgumentException("Both buffers must be instance of the same class.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aXLength <= 0) {
            throw new IllegalArgumentException("aXLength is less/equal 0.");
        }
        if (aYLength <= 0) {
            throw new IllegalArgumentException("aYLength is less/equal 0.");
        }
        if (aZLength <= 0) {
            throw new IllegalArgumentException("aZLength is less/equal 0.");
        }

        // </editor-fold>
        // Reduce values to avoid points outside the compartment due to round-off errors
        double tmpXLength = aXLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpYLength = aYLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpZLength = aZLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpHalfXLength = tmpXLength / 2.0;
        double tmpHalfYLength = tmpYLength / 2.0;
        double tmpHalfZLength = tmpZLength / 2.0;
        double tmpDeltaX;
        double tmpDeltaY;
        double tmpDeltaZ;
        int tmpIndex = aFirstIndex;

        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer1 instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="Buffers are an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                aBuffer1[tmpIndex] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);

                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                aBuffer2[tmpIndex] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);

                tmpIndex++;
            }

            // </editor-fold>
        } else if (aBuffer1 instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                aBuffer1[tmpIndex] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                aBuffer2[tmpIndex] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                tmpIndex++;
            }

            // </editor-fold>
        }
    }

    /**
     * Fills buffer with random points in sphere. NOTE: The volume of spheres
     * within the sphere is excluded for the random points in aBuffer. NOTE:
     * If number of trials is NOT sufficient random points may be located within
     * the excluded volume of the spheres.
     *
     * @param aBuffer Buffer for random points in sphere
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points in buffer (not allowed to be
     * less than 1)
     * @param aSphereCenterPoint Center point of sphere
     * @param aSphereRadius Radius of sphere
     * @param anExistingSphereList List with existing spheres (may be null or empty)
     * @param aNumberOfTrials Number of trials for random point generation
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsInSphereWithExcludingSpheres(
        IPointInSpace[] aBuffer, 
        int aFirstIndex, 
        int aNumber, 
        IPointInSpace aSphereCenterPoint, 
        double aSphereRadius, 
        LinkedList<BodySphere> anExistingSphereList, 
        int aNumberOfTrials,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aSphereCenterPoint == null) {
            throw new IllegalArgumentException("aSphereCenterPoint is null.");
        }
        if (aSphereRadius <= 0) {
            throw new IllegalArgumentException("aSphereRadius is less/equal 0.");
        }
        if (aNumberOfTrials < 1) {
            throw new IllegalArgumentException("aNumberOfTrials is less than 1.");
        }
        // </editor-fold>
        boolean tmpAreExistingSpheresDefined = anExistingSphereList != null && !anExistingSphereList.isEmpty();

        // Reduce value to avoid points outside the compartment due to round-off errors
        double tmpSphereRadius = aSphereRadius * ModelDefinitions.DECREASE_FACTOR;
        double tmpCubeLength = 2.0 * tmpSphereRadius;
        double tmpSphereRadiusSquare = tmpSphereRadius * tmpSphereRadius;
        double tmpDeltaX = 0;
        double tmpDeltaY = 0;
        double tmpDeltaZ = 0;
        double tmpX = 0;
        double tmpY = 0;
        double tmpZ = 0;

        PointInSpace tmpTestPoint = new PointInSpace(aSphereCenterPoint.getX(), aSphereCenterPoint.getY(), aSphereCenterPoint.getZ());

        int tmpIndex = aFirstIndex;

        for (int i = 0; i < aNumber; i++) {
            int tmpTrialCounter = 0;
            boolean isOutsideSphereExcludedVolume = true;
            do {
                do {
                    tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpDeltaX = tmpX - tmpSphereRadius;
                    tmpDeltaY = tmpY - tmpSphereRadius;
                    tmpDeltaZ = tmpZ - tmpSphereRadius;
                } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpSphereRadiusSquare);
                tmpTestPoint.setX(aSphereCenterPoint.getX() - tmpSphereRadius + tmpX);
                tmpTestPoint.setY(aSphereCenterPoint.getY() - tmpSphereRadius + tmpY);
                tmpTestPoint.setZ(aSphereCenterPoint.getZ() - tmpSphereRadius + tmpZ);
                if (tmpAreExistingSpheresDefined) {
                    for (BodySphere tmpSingleSphere : anExistingSphereList) {
                        if (tmpSingleSphere.isInVolume(tmpTestPoint)) {
                            isOutsideSphereExcludedVolume = false;
                            break;
                        }
                    }
                }
                tmpTrialCounter++;
                if (tmpTrialCounter > aNumberOfTrials) {
                    isOutsideSphereExcludedVolume = true;
                }
            } while (!isOutsideSphereExcludedVolume);

            if (aBuffer instanceof GraphicalParticlePosition[]) {
                aBuffer[tmpIndex] = new GraphicalParticlePosition(tmpTestPoint.getX(), tmpTestPoint.getY(), tmpTestPoint.getZ());
            } else if (aBuffer instanceof PointInSpace[]) {
                aBuffer[tmpIndex] = tmpTestPoint.getClone();
            }

            tmpIndex++;
        }
    }

    /**
     * Fills buffers 1 and 2 with random points in sphere. 
     * NOTE: The volume of
     * spheres within the sphere is excluded NOT just for the random points in
     * aBuffer1 and aBuffer2 but for all points along the straight line between
     * aBuffer1[i] and aBuffer2[i] with the specified step distance. 
     * NOTE: If
     * number of trials is NOT sufficient random points may be located within
     * the excluded volume of the spheres.
     *
     * @param aBuffer1 Buffer 1 for random points in sphere
     * @param aBuffer2 Buffer 2 for random points in sphere
     * @param aFirstIndex First index in both buffers
     * @param aNumber Number of random points in both buffers (not allowed to be
     * less than 1)
     * @param aSphereCenterPoint Center point of sphere
     * @param aSphereRadius Radius of sphere
     * @param anExistingSphereList List with existing spheres (may be null or empty)
     * @param aStepDistance Step distance for points along the straight lines
     * between aBuffer1[i] and aBuffer2[i]
     * @param aNumberOfTrials Number of trials for random point generation
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsInSphereWithExcludingSpheres(
        IPointInSpace[] aBuffer1, 
        IPointInSpace[] aBuffer2, 
        int aFirstIndex, 
        int aNumber, 
        IPointInSpace aSphereCenterPoint,
        double aSphereRadius, 
        LinkedList<BodySphere> anExistingSphereList, 
        double aStepDistance, 
        int aNumberOfTrials,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer1 == null || aBuffer1.length == 0) {
            throw new IllegalArgumentException("aBuffer1 is null/empty.");
        }
        if (aBuffer2 == null || aBuffer2.length == 0) {
            throw new IllegalArgumentException("aBuffer2 is null/empty.");
        }
        if (aBuffer1.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer1 is too small.");
        }
        if (aBuffer2.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer2 is too small.");
        }
        if ((aBuffer1 instanceof GraphicalParticlePosition[]) && !(aBuffer2 instanceof GraphicalParticlePosition[])) {
            throw new IllegalArgumentException("Both buffers must be instance of the same class.");
        }
        if ((aBuffer2 instanceof GraphicalParticlePosition[]) && !(aBuffer1 instanceof GraphicalParticlePosition[])) {
            throw new IllegalArgumentException("Both buffers must be instance of the same class.");
        }
        if (aSphereCenterPoint == null) {
            throw new IllegalArgumentException("aSphereCenterPoint is null.");
        }
        if (aSphereRadius <= 0) {
            throw new IllegalArgumentException("aSphereRadius is less/equal 0.");
        }
        if (aStepDistance <= 0.0) {
            throw new IllegalArgumentException("aDistance is less/equal 0.");
        }
        if (aNumberOfTrials < 1) {
            throw new IllegalArgumentException("aNumberOfTrials is less than 1.");
        }
        // </editor-fold>
        boolean tmpAreExistingSpheresDefined = anExistingSphereList != null && !anExistingSphereList.isEmpty();

        // Reduce value to avoid points outside the compartment due to round-off errors
        double tmpSphereRadius = aSphereRadius * ModelDefinitions.DECREASE_FACTOR;
        double tmpCubeLength = 2.0 * tmpSphereRadius;
        double tmpSphereRadiusSquare = tmpSphereRadius * tmpSphereRadius;
        double tmpDeltaX = 0;
        double tmpDeltaY = 0;
        double tmpDeltaZ = 0;
        double tmpX = 0;
        double tmpY = 0;
        double tmpZ = 0;

        PointInSpace tmpTestPoint1 = new PointInSpace(aSphereCenterPoint.getX(), aSphereCenterPoint.getY(), aSphereCenterPoint.getZ());
        PointInSpace tmpTestPoint2 = new PointInSpace(aSphereCenterPoint.getX(), aSphereCenterPoint.getY(), aSphereCenterPoint.getZ());

        int tmpIndex = aFirstIndex;

        for (int i = 0; i < aNumber; i++) {
            int tmpTrialCounter1 = 0;
            boolean tmpIsSecondTestPointSuccessful = false;
            do {
                int tmpTrialCounter2 = 0;
                boolean isOutsideSphereExcludedVolume = true;
                do {
                    do {
                        tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                        tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                        tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                        tmpDeltaX = tmpX - tmpSphereRadius;
                        tmpDeltaY = tmpY - tmpSphereRadius;
                        tmpDeltaZ = tmpZ - tmpSphereRadius;
                    } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpSphereRadiusSquare);
                    tmpTestPoint1.setX(aSphereCenterPoint.getX() - tmpSphereRadius + tmpX);
                    tmpTestPoint1.setY(aSphereCenterPoint.getY() - tmpSphereRadius + tmpY);
                    tmpTestPoint1.setZ(aSphereCenterPoint.getZ() - tmpSphereRadius + tmpZ);
                    if (tmpAreExistingSpheresDefined) {
                        for (BodySphere tmpSingleSphere : anExistingSphereList) {
                            if (tmpSingleSphere.isInVolume(tmpTestPoint1)) {
                                isOutsideSphereExcludedVolume = false;
                                break;
                            }
                        }
                    }
                    tmpTrialCounter2++;
                    if (tmpTrialCounter2 > aNumberOfTrials) {
                        isOutsideSphereExcludedVolume = true;
                    }
                } while (!isOutsideSphereExcludedVolume);

                tmpTrialCounter2 = 0;
                isOutsideSphereExcludedVolume = true;
                do {
                    do {
                        tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                        tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                        tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                        tmpDeltaX = tmpX - tmpSphereRadius;
                        tmpDeltaY = tmpY - tmpSphereRadius;
                        tmpDeltaZ = tmpZ - tmpSphereRadius;
                    } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpSphereRadiusSquare);
                    tmpTestPoint2.setX(aSphereCenterPoint.getX() - tmpSphereRadius + tmpX);
                    tmpTestPoint2.setY(aSphereCenterPoint.getY() - tmpSphereRadius + tmpY);
                    tmpTestPoint2.setZ(aSphereCenterPoint.getZ() - tmpSphereRadius + tmpZ);
                    if (tmpAreExistingSpheresDefined) {
                        for (BodySphere tmpSingleSphere : anExistingSphereList) {
                            if (tmpSingleSphere.isInVolume(tmpTestPoint1)) {
                                isOutsideSphereExcludedVolume = false;
                                break;
                            }
                        }
                    }
                    tmpTrialCounter2++;
                    if (tmpTrialCounter2 > aNumberOfTrials) {
                        isOutsideSphereExcludedVolume = true;
                    }
                } while (!isOutsideSphereExcludedVolume);

                LinkedList<PointInSpace> tmpTestPointList = this.getPointsInSpaceAlongStraightLine(tmpTestPoint1, tmpTestPoint2, aStepDistance);
                if (tmpTestPointList.size() > 1) {
                    for (PointInSpace tmpSinglePoint : tmpTestPointList) {
                        boolean tmpIsInSphere = false;
                        if (tmpAreExistingSpheresDefined) {
                            for (BodySphere tmpSingleSphere : anExistingSphereList) {
                                if (tmpSingleSphere.isInVolume(tmpSinglePoint)) {
                                    tmpIsInSphere = true;
                                    break;
                                }
                            }
                        }
                        if (!tmpIsInSphere) {
                            tmpTestPoint2 = tmpSinglePoint;
                            tmpIsSecondTestPointSuccessful = true;
                        }
                    }
                }

                tmpTrialCounter1++;
                if (tmpTrialCounter1 > aNumberOfTrials) {
                    tmpIsSecondTestPointSuccessful = true;
                }
            } while (!tmpIsSecondTestPointSuccessful);

            if (aBuffer1 instanceof GraphicalParticlePosition[]) {
                aBuffer1[tmpIndex] = new GraphicalParticlePosition(tmpTestPoint1.getX(), tmpTestPoint1.getY(), tmpTestPoint1.getZ());
                aBuffer2[tmpIndex] = new GraphicalParticlePosition(tmpTestPoint2.getX(), tmpTestPoint2.getY(), tmpTestPoint2.getZ());
            } else if (aBuffer1 instanceof PointInSpace[]) {
                aBuffer1[tmpIndex] = tmpTestPoint1.getClone();
                aBuffer2[tmpIndex] = tmpTestPoint2.getClone();
            }

            tmpIndex++;
        }
    }

    /**
     * Fills buffer with random points in xy-Layer.
     * NOTE: The volume of spheres within the xy-layer is excluded for the 
     * random points in aBuffer. 
     * NOTE: If number of trials is NOT sufficient random points may be located 
     * within the excluded volume of the spheres.
     *
     * @param aBuffer Buffer for random points in xy-layer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points in buffer (not allowed to be
     * less than 1)
     * @param aXyLayerCenterPoint Center point of xy-layer
     * @param aXyLayerXLength X-Length of xy-layer
     * @param aXyLayerYLength Y-Length of xy-layer
     * @param aXyLayerZLength Z-Length of xy-layer
     * @param anExistingSphereList List with existing spheres (may be null or empty)
     * @param aNumberOfTrials Number of trials for random point generation
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsInXyLayerWithExcludingSpheres(
        IPointInSpace[] aBuffer, 
        int aFirstIndex, 
        int aNumber, 
        IPointInSpace aXyLayerCenterPoint, 
        double aXyLayerXLength, 
        double aXyLayerYLength,
        double aXyLayerZLength, 
        LinkedList<BodySphere> anExistingSphereList, 
        int aNumberOfTrials,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aXyLayerCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aXyLayerXLength <= 0) {
            throw new IllegalArgumentException("aXLength is less/equal 0.");
        }
        if (aXyLayerYLength <= 0) {
            throw new IllegalArgumentException("aYLength is less/equal 0.");
        }
        if (aXyLayerZLength <= 0) {
            throw new IllegalArgumentException("aZLength is less/equal 0.");
        }
        if (aNumberOfTrials < 1) {
            throw new IllegalArgumentException("aNumberOfTrials is less than 1.");
        }
        // </editor-fold>
        boolean tmpAreExistingSpheresDefined = anExistingSphereList != null && !anExistingSphereList.isEmpty();

        // Reduce values to avoid points outside the compartment due to round-off errors
        double tmpXLength = aXyLayerXLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpYLength = aXyLayerYLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpZLength = aXyLayerZLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpHalfXLength = tmpXLength / 2.0;
        double tmpHalfYLength = tmpYLength / 2.0;
        double tmpHalfZLength = tmpZLength / 2.0;

        PointInSpace tmpTestPoint = new PointInSpace(aXyLayerCenterPoint.getX(), aXyLayerCenterPoint.getY(), aXyLayerCenterPoint.getZ());

        int tmpIndex = aFirstIndex;

        for (int i = 0; i < aNumber; i++) {
            int tmpTrialCounter = 0;
            boolean isOutsideSphereExcludedVolume = true;
            do {
                tmpTestPoint.setX(aXyLayerCenterPoint.getX() + tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength);
                tmpTestPoint.setY(aXyLayerCenterPoint.getY() + tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength);
                tmpTestPoint.setZ(aXyLayerCenterPoint.getZ() + tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength);
                if (tmpAreExistingSpheresDefined) {
                    for (BodySphere tmpSingleSphere : anExistingSphereList) {
                        if (tmpSingleSphere.isInVolume(tmpTestPoint)) {
                            isOutsideSphereExcludedVolume = false;
                            break;
                        }
                    }
                }
                tmpTrialCounter++;
                if (tmpTrialCounter > aNumberOfTrials) {
                    isOutsideSphereExcludedVolume = true;
                }
            } while (!isOutsideSphereExcludedVolume);

            if (aBuffer instanceof GraphicalParticlePosition[]) {
                aBuffer[tmpIndex] = new GraphicalParticlePosition(tmpTestPoint.getX(), tmpTestPoint.getY(), tmpTestPoint.getZ());
            } else if (aBuffer instanceof PointInSpace[]) {
                aBuffer[tmpIndex] = tmpTestPoint.getClone();
            }

            tmpIndex++;
        }
    }

    /**
     * Fills buffers 1 and 2 with random points in xy-Layer. NOTE: The volume of
     * spheres within the xy-layer is excluded NOT just for the random points in
     * aBuffer1 and aBuffer2 but for all points along the straight line between
     * aBuffer1[i] and aBuffer2[i] with the specified step distance. NOTE: If
     * number of trials is NOT sufficient random points may be located within
     * the excluded volume of the spheres.
     *
     * @param aBuffer1 Buffer 1 for random points in xy-layer
     * @param aBuffer2 Buffer 2 for random points in xy-layer
     * @param aFirstIndex First index in both buffers
     * @param aNumber Number of random points in both buffers (not allowed to be
     * less than 1)
     * @param aXyLayerCenterPoint Center point of xy-layer
     * @param aXyLayerXLength X-Length of xy-layer
     * @param aXyLayerYLength Y-Length of xy-layer
     * @param aXyLayerZLength Z-Length of xy-layer
     * @param anExistingSphereList List with existing spheres (may be null or empty)
     * @param aStepDistance Step distance for points along the straight lines
     * between aBuffer1[i] and aBuffer2[i]
     * @param aNumberOfTrials Number of trials for random point generation
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsInXyLayerWithExcludingSpheres(
        IPointInSpace[] aBuffer1, 
        IPointInSpace[] aBuffer2, 
        int aFirstIndex, 
        int aNumber, 
        IPointInSpace aXyLayerCenterPoint,
        double aXyLayerXLength, 
        double aXyLayerYLength, 
        double aXyLayerZLength, 
        LinkedList<BodySphere> anExistingSphereList, 
        double aStepDistance, 
        int aNumberOfTrials,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer1 == null || aBuffer1.length == 0) {
            throw new IllegalArgumentException("aBuffer1 is null/empty.");
        }
        if (aBuffer2 == null || aBuffer2.length == 0) {
            throw new IllegalArgumentException("aBuffer2 is null/empty.");
        }
        if (aBuffer1.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer1 is too small.");
        }
        if (aBuffer2.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer2 is too small.");
        }
        if ((aBuffer1 instanceof GraphicalParticlePosition[]) && !(aBuffer2 instanceof GraphicalParticlePosition[])) {
            throw new IllegalArgumentException("Both buffers must be instance of the same class.");
        }
        if ((aBuffer2 instanceof GraphicalParticlePosition[]) && !(aBuffer1 instanceof GraphicalParticlePosition[])) {
            throw new IllegalArgumentException("Both buffers must be instance of the same class.");
        }
        if (aXyLayerCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aXyLayerXLength <= 0) {
            throw new IllegalArgumentException("aXLength is less/equal 0.");
        }
        if (aXyLayerYLength <= 0) {
            throw new IllegalArgumentException("aYLength is less/equal 0.");
        }
        if (aXyLayerZLength <= 0) {
            throw new IllegalArgumentException("aZLength is less/equal 0.");
        }
        if (aStepDistance <= 0.0) {
            throw new IllegalArgumentException("aDistance is less/equal 0.");
        }
        if (aNumberOfTrials < 1) {
            throw new IllegalArgumentException("aNumberOfTrials is less than 1.");
        }
        // </editor-fold>
        boolean tmpAreExistingSpheresDefined = anExistingSphereList != null && !anExistingSphereList.isEmpty();

        // Reduce values to avoid points outside the compartment due to round-off errors
        double tmpXLength = aXyLayerXLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpYLength = aXyLayerYLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpZLength = aXyLayerZLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpHalfXLength = tmpXLength / 2.0;
        double tmpHalfYLength = tmpYLength / 2.0;
        double tmpHalfZLength = tmpZLength / 2.0;

        PointInSpace tmpTestPoint1 = new PointInSpace(aXyLayerCenterPoint.getX(), aXyLayerCenterPoint.getY(), aXyLayerCenterPoint.getZ());
        PointInSpace tmpTestPoint2 = new PointInSpace(aXyLayerCenterPoint.getX(), aXyLayerCenterPoint.getY(), aXyLayerCenterPoint.getZ());

        int tmpIndex = aFirstIndex;

        for (int i = 0; i < aNumber; i++) {
            int tmpTrialCounter1 = 0;
            boolean tmpIsSecondTestPointSuccessful = false;
            do {
                int tmpTrialCounter2 = 0;
                boolean isOutsideSphereExcludedVolume = true;
                do {
                    tmpTestPoint1.setX(aXyLayerCenterPoint.getX() + tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength);
                    tmpTestPoint1.setY(aXyLayerCenterPoint.getY() + tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength);
                    tmpTestPoint1.setZ(aXyLayerCenterPoint.getZ() + tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength);
                    if (tmpAreExistingSpheresDefined) {
                        for (BodySphere tmpSingleSphere : anExistingSphereList) {
                            if (tmpSingleSphere.isInVolume(tmpTestPoint1)) {
                                isOutsideSphereExcludedVolume = false;
                                break;
                            }
                        }
                    }
                    tmpTrialCounter2++;
                    if (tmpTrialCounter2 > aNumberOfTrials) {
                        isOutsideSphereExcludedVolume = true;
                    }
                } while (!isOutsideSphereExcludedVolume);

                tmpTrialCounter2 = 0;
                isOutsideSphereExcludedVolume = true;
                do {
                    tmpTestPoint2.setX(aXyLayerCenterPoint.getX() + tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength);
                    tmpTestPoint2.setY(aXyLayerCenterPoint.getY() + tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength);
                    tmpTestPoint2.setZ(aXyLayerCenterPoint.getZ() + tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength);
                    if (tmpAreExistingSpheresDefined) {
                        for (BodySphere tmpSingleSphere : anExistingSphereList) {
                            if (tmpSingleSphere.isInVolume(tmpTestPoint1)) {
                                isOutsideSphereExcludedVolume = false;
                                break;
                            }
                        }
                    }
                    tmpTrialCounter2++;
                    if (tmpTrialCounter2 > aNumberOfTrials) {
                        isOutsideSphereExcludedVolume = true;
                    }
                } while (!isOutsideSphereExcludedVolume);

                LinkedList<PointInSpace> tmpTestPointList = this.getPointsInSpaceAlongStraightLine(tmpTestPoint1, tmpTestPoint2, aStepDistance);
                if (tmpTestPointList.size() > 1) {
                    for (PointInSpace tmpSinglePoint : tmpTestPointList) {
                        boolean tmpIsInSphere = false;
                        if (tmpAreExistingSpheresDefined) {
                            for (BodySphere tmpSingleSphere : anExistingSphereList) {
                                if (tmpSingleSphere.isInVolume(tmpSinglePoint)) {
                                    tmpIsInSphere = true;
                                    break;
                                }
                            }
                        }
                        if (!tmpIsInSphere) {
                            tmpTestPoint2 = tmpSinglePoint;
                            tmpIsSecondTestPointSuccessful = true;
                        }
                    }
                }

                tmpTrialCounter1++;
                if (tmpTrialCounter1 > aNumberOfTrials) {
                    tmpIsSecondTestPointSuccessful = true;
                }
            } while (!tmpIsSecondTestPointSuccessful);

            if (aBuffer1 instanceof GraphicalParticlePosition[]) {
                aBuffer1[tmpIndex] = new GraphicalParticlePosition(tmpTestPoint1.getX(), tmpTestPoint1.getY(), tmpTestPoint1.getZ());
                aBuffer2[tmpIndex] = new GraphicalParticlePosition(tmpTestPoint2.getX(), tmpTestPoint2.getY(), tmpTestPoint2.getZ());
            } else if (aBuffer1 instanceof PointInSpace[]) {
                aBuffer1[tmpIndex] = tmpTestPoint1.getClone();
                aBuffer2[tmpIndex] = tmpTestPoint2.getClone();
            }

            tmpIndex++;
        }
    }

    /**
     * Fills buffer with random points on sphere surface. Theory: See
     * documentation document "Sphere Point Picking -- from Wolfram
     * MathWorld.pdf".
     *
     * @param aBuffer Buffer for random points on sphere surface
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of sphere
     * @param aRadius Radius of sphere
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnSphereSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, PointInSpace aCenterPoint, double aRadius, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aRadius <= 0) {
            throw new IllegalArgumentException("aRadius is less/equal 0.");
        }
        // </editor-fold>
        // Reduce value to avoid points outside the compartment due to round-off errors
        double tmpRadius = aRadius * ModelDefinitions.DECREASE_FACTOR;
        int tmpIndex = aFirstIndex;
        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                double tmpPhi = Math.acos(2.0 * aRandomNumberGenerator.nextDouble() - 1.0);
                double tmpTheta = aRandomNumberGenerator.nextDouble() * ModelDefinitions.TWO_PI;
                double tmpFactor = tmpRadius * Math.sin(tmpPhi);
                double tmpDx = tmpFactor * Math.cos(tmpTheta);
                double tmpDy = tmpFactor * Math.sin(tmpTheta);
                double tmpDz = tmpRadius * Math.cos(tmpPhi);
                aBuffer[tmpIndex++] = new GraphicalParticlePosition( // Graphical particle position:
                        aCenterPoint.getX() + tmpDx, // x
                        aCenterPoint.getY() + tmpDy, // y
                        aCenterPoint.getZ() + tmpDz // z
                );
            }

            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                double tmpPhi = Math.acos(2.0 * aRandomNumberGenerator.nextDouble() - 1.0);
                double tmpTheta = aRandomNumberGenerator.nextDouble() * ModelDefinitions.TWO_PI;
                double tmpFactor = tmpRadius * Math.sin(tmpPhi);
                double tmpDx = tmpFactor * Math.cos(tmpTheta);
                double tmpDy = tmpFactor * Math.sin(tmpTheta);
                double tmpDz = tmpRadius * Math.cos(tmpPhi);
                aBuffer[tmpIndex++] = new PointInSpace( // Point in space:
                        aCenterPoint.getX() + tmpDx, // x
                        aCenterPoint.getY() + tmpDy, // y
                        aCenterPoint.getZ() + tmpDz // z
                );
            }
            // </editor-fold>
        }
    }

    /**
     * Fills buffer with random points on sphere surface with z-height greater
     * than half of radius. Theory: See documentation document "Sphere Point
     * Picking -- from Wolfram MathWorld.pdf".
     *
     * @param aBuffer Buffer for random points on sphere surface
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of sphere
     * @param aRadius Radius of sphere
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillUpperRandomPointsOnSphereSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, PointInSpace aCenterPoint, double aRadius, IRandom aRandomNumberGenerator) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aRadius <= 0) {
            throw new IllegalArgumentException("aRadius is less/equal 0.");
        }

        // </editor-fold>
        // Reduce value to avoid points outside the compartment due to round-off errors
        double tmpRadius = aRadius * ModelDefinitions.DECREASE_FACTOR;
        int tmpIndex = aFirstIndex;
        double tmpHalfRadius = tmpRadius / 2.0;
        double tmpDx;
        double tmpDy;
        double tmpDz;

        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                do {
                    double tmpPhi = Math.acos(2.0 * aRandomNumberGenerator.nextDouble() - 1.0);
                    double tmpTheta = aRandomNumberGenerator.nextDouble() * ModelDefinitions.TWO_PI;
                    double tmpFactor = tmpRadius * Math.sin(tmpPhi);
                    tmpDx = tmpFactor * Math.cos(tmpTheta);
                    tmpDy = tmpFactor * Math.sin(tmpTheta);
                    tmpDz = tmpRadius * Math.cos(tmpPhi);
                } while (Math.abs(tmpDz) < tmpHalfRadius);
                aBuffer[tmpIndex++] = new GraphicalParticlePosition( // Graphical particle position:
                        aCenterPoint.getX() + tmpDx, // x
                        aCenterPoint.getY() + tmpDy, // y
                        aCenterPoint.getZ() + tmpDz // z
                );
            }

            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                do {
                    double tmpPhi = Math.acos(2.0 * aRandomNumberGenerator.nextDouble() - 1.0);
                    double tmpTheta = aRandomNumberGenerator.nextDouble() * ModelDefinitions.TWO_PI;
                    double tmpFactor = tmpRadius * Math.sin(tmpPhi);
                    tmpDx = tmpFactor * Math.cos(tmpTheta);
                    tmpDy = tmpFactor * Math.sin(tmpTheta);
                    tmpDz = tmpRadius * Math.cos(tmpPhi);
                } while (Math.abs(tmpDz) < tmpHalfRadius);
                aBuffer[tmpIndex++] = new PointInSpace( // Point in space:
                        aCenterPoint.getX() + tmpDx, // x
                        aCenterPoint.getY() + tmpDy, // y
                        aCenterPoint.getZ() + tmpDz // z
                );
            }

            // </editor-fold>
        }
    }

    /**
     * Fills buffer with random points on sphere surface with z-height lower
     * than half of radius. Theory: See documentation document "Sphere Point
     * Picking -- from Wolfram MathWorld.pdf".
     *
     * @param aBuffer Buffer for random points on sphere surface
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of sphere
     * @param aRadius Radius of sphere
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillMiddleRandomPointsOnSphereSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, PointInSpace aCenterPoint, double aRadius, IRandom aRandomNumberGenerator) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aRadius <= 0) {
            throw new IllegalArgumentException("aRadius is less/equal 0.");
        }

        // </editor-fold>
        // Reduce value to avoid points outside the compartment due to round-off errors
        double tmpRadius = aRadius * ModelDefinitions.DECREASE_FACTOR;
        int tmpIndex = aFirstIndex;
        double tmpHalfRadius = tmpRadius / 2.0;
        double tmpDx;
        double tmpDy;
        double tmpDz;

        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                do {
                    double tmpPhi = Math.acos(2.0 * aRandomNumberGenerator.nextDouble() - 1.0);
                    double tmpTheta = aRandomNumberGenerator.nextDouble() * ModelDefinitions.TWO_PI;
                    double tmpFactor = tmpRadius * Math.sin(tmpPhi);
                    tmpDx = tmpFactor * Math.cos(tmpTheta);
                    tmpDy = tmpFactor * Math.sin(tmpTheta);
                    tmpDz = tmpRadius * Math.cos(tmpPhi);
                } while (Math.abs(tmpDz) > tmpHalfRadius);
                aBuffer[tmpIndex++] = new GraphicalParticlePosition( // Graphical particle position:
                        aCenterPoint.getX() + tmpDx, // x
                        aCenterPoint.getY() + tmpDy, // y
                        aCenterPoint.getZ() + tmpDz // z
                );
            }

            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                do {
                    double tmpPhi = Math.acos(2.0 * aRandomNumberGenerator.nextDouble() - 1.0);
                    double tmpTheta = aRandomNumberGenerator.nextDouble() * ModelDefinitions.TWO_PI;
                    double tmpFactor = tmpRadius * Math.sin(tmpPhi);
                    tmpDx = tmpFactor * Math.cos(tmpTheta);
                    tmpDy = tmpFactor * Math.sin(tmpTheta);
                    tmpDz = tmpRadius * Math.cos(tmpPhi);
                } while (Math.abs(tmpDz) > tmpHalfRadius);
                aBuffer[tmpIndex++] = new PointInSpace( // Point in space:
                        aCenterPoint.getX() + tmpDx, // x
                        aCenterPoint.getY() + tmpDy, // y
                        aCenterPoint.getZ() + tmpDz // z
                );
            }

            // </editor-fold>
        }
    }

    /**
     * Fills buffer with random points on xy-layer top/bottom surface.
     *
     * @param aBuffer Buffer for random points on xy-layer top/bottom surface
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of xy-layer
     * @param aXLength X-Length of xy-layer
     * @param aYLength Y-Length of xy-layer
     * @param aZLength Z-Length of xy-layer
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnXyLayerTopBottomSurface(
        IPointInSpace[] aBuffer, 
        int aFirstIndex, 
        int aNumber, 
        PointInSpace aCenterPoint, 
        double aXLength, 
        double aYLength, 
        double aZLength,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aXLength <= 0) {
            throw new IllegalArgumentException("aXLength is less/equal 0.");
        }
        if (aYLength <= 0) {
            throw new IllegalArgumentException("aYLength is less/equal 0.");
        }
        if (aZLength <= 0) {
            throw new IllegalArgumentException("aZLength is less/equal 0.");
        }
        // </editor-fold>
        // Reduce values to avoid points outside the compartment due to round-off errors
        double tmpXLength = aXLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpYLength = aYLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpZLength = aZLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpHalfXLength = tmpXLength / 2.0;
        double tmpHalfYLength = tmpYLength / 2.0;
        double tmpHalfZLength = tmpZLength / 2.0;
        double tmpDeltaX = 0;
        double tmpDeltaY = 0;
        double tmpZUpper = aCenterPoint.getZ() + tmpHalfZLength;
        double tmpZLower = aCenterPoint.getZ() - tmpHalfZLength;
        int tmpIndex = aFirstIndex;
        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZUpper);
                } else {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZLower);
                }
            }
            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZUpper);
                } else {
                    aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZLower);
                }
            }
            // </editor-fold>
        }
    }

    /**
     * Fills buffer with random points on xy-layer left/right surface.
     *
     * @param aBuffer Buffer for random points on xy-layer left/right surface
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of xy-layer
     * @param aXLength X-Length of xy-layer
     * @param aYLength Y-Length of xy-layer
     * @param aZLength Z-Length of xy-layer
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnXyLayerLeftRightSurface(
        IPointInSpace[] aBuffer, 
        int aFirstIndex, 
        int aNumber, 
        PointInSpace aCenterPoint, 
        double aXLength, 
        double aYLength, 
        double aZLength,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aXLength <= 0) {
            throw new IllegalArgumentException("aXLength is less/equal 0.");
        }
        if (aYLength <= 0) {
            throw new IllegalArgumentException("aYLength is less/equal 0.");
        }
        if (aZLength <= 0) {
            throw new IllegalArgumentException("aZLength is less/equal 0.");
        }
        // </editor-fold>
        // Reduce values to avoid points outside the compartment due to round-off errors
        double tmpXLength = aXLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpYLength = aYLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpZLength = aZLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpHalfXLength = tmpXLength / 2.0;
        double tmpHalfYLength = tmpYLength / 2.0;
        double tmpHalfZLength = tmpZLength / 2.0;
        double tmpDeltaY = 0;
        double tmpDeltaZ = 0;
        double tmpXUpper = aCenterPoint.getX() + tmpHalfXLength;
        double tmpXLower = aCenterPoint.getX() - tmpHalfXLength;
        int tmpIndex = aFirstIndex;
        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(tmpXUpper, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                } else {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(tmpXLower, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                }
            }
            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new PointInSpace(tmpXUpper, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                } else {
                    aBuffer[tmpIndex++] = new PointInSpace(tmpXLower, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                }
            }
            // </editor-fold>
        }
    }

    /**
     * Fills buffer with random points on xy-layer front/back surface.
     *
     * @param aBuffer Buffer for random points on xy-layer front/back surface
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of xy-layer
     * @param aXLength X-Length of xy-layer
     * @param aYLength Y-Length of xy-layer
     * @param aZLength Z-Length of xy-layer
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnXyLayerFrontBackSurface(
        IPointInSpace[] aBuffer, 
        int aFirstIndex, 
        int aNumber, 
        PointInSpace aCenterPoint, 
        double aXLength, 
        double aYLength, 
        double aZLength,
        IRandom aRandomNumberGenerator
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aXLength <= 0) {
            throw new IllegalArgumentException("aXLength is less/equal 0.");
        }
        if (aYLength <= 0) {
            throw new IllegalArgumentException("aYLength is less/equal 0.");
        }
        if (aZLength <= 0) {
            throw new IllegalArgumentException("aZLength is less/equal 0.");
        }
        // </editor-fold>
        // Reduce values to avoid points outside the compartment due to round-off errors
        double tmpXLength = aXLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpYLength = aYLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpZLength = aZLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpHalfXLength = tmpXLength / 2.0;
        double tmpHalfYLength = tmpYLength / 2.0;
        double tmpHalfZLength = tmpZLength / 2.0;
        double tmpDeltaX = 0;
        double tmpDeltaZ = 0;
        double tmpYUpper = aCenterPoint.getY() + tmpHalfYLength;
        double tmpYLower = aCenterPoint.getY() - tmpHalfYLength;
        int tmpIndex = aFirstIndex;
        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, tmpYUpper, aCenterPoint.getZ() + tmpDeltaZ);
                } else {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, tmpYLower, aCenterPoint.getZ() + tmpDeltaZ);
                }
            }
            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, tmpYUpper, aCenterPoint.getZ() + tmpDeltaZ);
                } else {
                    aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, tmpYLower, aCenterPoint.getZ() + tmpDeltaZ);
                }
            }
            // </editor-fold>
        }
    }

    /**
     * Fills buffer with random points of single xy-layer surface.
     *
     * @param aBuffer Buffer for random points on xy-layer surface
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of xy-layer
     * @param aXLength X-Length of xy-layer
     * @param aYLength Y-Length of xy-layer
     * @param aZLength Z-Length of xy-layer
     * @param aSingleSurface Single surface of xy-layer
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnSingleXyLayerSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, PointInSpace aCenterPoint, double aXLength, double aYLength,
            double aZLength, BodyXyLayerSingleSurfaceEnum aSingleSurface, IRandom aRandomNumberGenerator)
            throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aXLength <= 0) {
            throw new IllegalArgumentException("aXLength is less/equal 0.");
        }
        if (aYLength <= 0) {
            throw new IllegalArgumentException("aYLength is less/equal 0.");
        }
        if (aZLength <= 0) {
            throw new IllegalArgumentException("aZLength is less/equal 0.");
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Definitions">
        // Reduce values to avoid points outside the compartment due to round-off errors
        double tmpXLength = aXLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpYLength = aYLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpZLength = aZLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpHalfXLength = tmpXLength / 2.0;
        double tmpHalfYLength = tmpYLength / 2.0;
        double tmpHalfZLength = tmpZLength / 2.0;
        double tmpDeltaX = 0.0;
        double tmpDeltaY = 0.0;
        double tmpDeltaZ = 0.0;
        double tmpX = 0.0;
        double tmpY = 0.0;
        double tmpZ = 0.0;
        int tmpIndex = aFirstIndex;
        // </editor-fold>
        switch (aSingleSurface) {
            case XY_TOP:
                // <editor-fold defaultstate="collapsed" desc="XY_TOP">
                tmpZ = aCenterPoint.getZ() + tmpHalfZLength;
                // FIRST check if instance of GraphicalParticlePosition[]
                if (aBuffer instanceof GraphicalParticlePosition[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                        tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                        aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZ);
                    }

                    // </editor-fold>
                } else if (aBuffer instanceof PointInSpace[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                        tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                        aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZ);
                    }
                    // </editor-fold>
                }
                // </editor-fold>
                return;
            case XY_BOTTOM:
                // <editor-fold defaultstate="collapsed" desc="XY_BOTTOM">
                tmpZ = aCenterPoint.getZ() - tmpHalfZLength;
                // FIRST check if instance of GraphicalParticlePosition[]
                if (aBuffer instanceof GraphicalParticlePosition[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                        tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                        aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZ);
                    }

                    // </editor-fold>
                } else if (aBuffer instanceof PointInSpace[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                        tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                        aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZ);
                    }

                    // </editor-fold>
                }
                // </editor-fold>
                return;
            case YZ_LEFT:
                // <editor-fold defaultstate="collapsed" desc="YZ_LEFT">
                tmpX = aCenterPoint.getX() - tmpHalfXLength;
                // FIRST check if instance of GraphicalParticlePosition[]
                if (aBuffer instanceof GraphicalParticlePosition[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                        tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                        aBuffer[tmpIndex++] = new GraphicalParticlePosition(tmpX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                    }

                    // </editor-fold>
                } else if (aBuffer instanceof PointInSpace[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                        tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                        aBuffer[tmpIndex++] = new PointInSpace(tmpX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                    }

                    // </editor-fold>
                }
                // </editor-fold>
                return;
            case YZ_RIGHT:
                // <editor-fold defaultstate="collapsed" desc="YZ_RIGHT">
                tmpX = aCenterPoint.getX() + tmpHalfXLength;
                // FIRST check if instance of GraphicalParticlePosition[]
                if (aBuffer instanceof GraphicalParticlePosition[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                        tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                        aBuffer[tmpIndex++] = new GraphicalParticlePosition(tmpX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                    }

                    // </editor-fold>
                } else if (aBuffer instanceof PointInSpace[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                        tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                        aBuffer[tmpIndex++] = new PointInSpace(tmpX, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                    }

                    // </editor-fold>
                }
                // </editor-fold>
                return;
            case XZ_FRONT:
                // <editor-fold defaultstate="collapsed" desc="XZ_FRONT">
                tmpY = aCenterPoint.getY() - tmpHalfYLength;
                // FIRST check if instance of GraphicalParticlePosition[]
                if (aBuffer instanceof GraphicalParticlePosition[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                        tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                        aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, tmpY, aCenterPoint.getZ() + tmpDeltaZ);
                    }

                    // </editor-fold>
                } else if (aBuffer instanceof PointInSpace[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                        tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                        aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, tmpY, aCenterPoint.getZ() + tmpDeltaZ);
                    }

                    // </editor-fold>
                }
                // </editor-fold>
                return;
            case XZ_BACK:
                // <editor-fold defaultstate="collapsed" desc="XZ_BACK">
                tmpY = aCenterPoint.getY() + tmpHalfYLength;
                // FIRST check if instance of GraphicalParticlePosition[]
                if (aBuffer instanceof GraphicalParticlePosition[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                        tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                        aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, tmpY, aCenterPoint.getZ() + tmpDeltaZ);
                    }

                    // </editor-fold>
                } else if (aBuffer instanceof PointInSpace[]) {
                    // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
                    for (int i = 0; i < aNumber; i++) {
                        tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                        tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                        aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, tmpY, aCenterPoint.getZ() + tmpDeltaZ);
                    }

                    // </editor-fold>
                }
                // </editor-fold>
                return;
        }
    }

    /**
     * Fills buffer with random points on all xy-layer surfaces.
     *
     * @param aBuffer Buffer for random points on xy-layer surface
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aCenterPoint Center point of xy-layer
     * @param aXLength X-Length of xy-layer
     * @param aYLength Y-Length of xy-layer
     * @param aZLength Z-Length of xy-layer
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnAllXyLayerSurfaces(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, PointInSpace aCenterPoint, double aXLength, double aYLength,
            double aZLength, IRandom aRandomNumberGenerator)
            throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }
        if (aFirstIndex < 0) {
            throw new IllegalArgumentException("aFirstIndex is less than 0.");
        }
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }
        if (aBuffer.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer is too small.");
        }
        if (aCenterPoint == null) {
            throw new IllegalArgumentException("aCenterPoint is null.");
        }
        if (aXLength <= 0) {
            throw new IllegalArgumentException("aXLength is less/equal 0.");
        }
        if (aYLength <= 0) {
            throw new IllegalArgumentException("aYLength is less/equal 0.");
        }
        if (aZLength <= 0) {
            throw new IllegalArgumentException("aZLength is less/equal 0.");
        }

        // </editor-fold>
        // Reduce values to avoid points outside the compartment due to round-off errors
        double tmpXLength = aXLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpYLength = aYLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpZLength = aZLength * ModelDefinitions.DECREASE_FACTOR;
        double tmpHalfXLength = tmpXLength / 2.0;
        double tmpHalfYLength = tmpYLength / 2.0;
        double tmpHalfZLength = tmpZLength / 2.0;
        double tmpDeltaX = 0;
        double tmpDeltaY = 0;
        double tmpDeltaZ = 0;

        double tmpXYArea = tmpXLength * tmpYLength;
        double tmpXZArea = tmpXLength * tmpZLength;
        double tmpYZArea = tmpYLength * tmpZLength;
        double tmpTotalArea = tmpXYArea + tmpXZArea + tmpYZArea;

        double tmpXYFraction = tmpXYArea / tmpTotalArea;
        double tmpXZFraction = tmpXZArea / tmpTotalArea;

        int tmpXYNumber = (int) (aNumber * tmpXYFraction);
        int tmpXZNumber = (int) (aNumber * tmpXZFraction);
        int tmpYZNumber = aNumber - tmpXYNumber - tmpXZNumber;

        double tmpXLeft = aCenterPoint.getX() - tmpHalfXLength;
        double tmpXRight = aCenterPoint.getX() + tmpHalfXLength;
        double tmpYFront = aCenterPoint.getY() - tmpHalfYLength;
        double tmpYBack = aCenterPoint.getY() + tmpHalfYLength;
        double tmpZLower = aCenterPoint.getZ() - tmpHalfZLength;
        double tmpZUpper = aCenterPoint.getZ() + tmpHalfZLength;

        int tmpIndex = aFirstIndex;

        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < tmpXYNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZUpper);
                } else {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZLower);
                }
            }
            for (int i = 0; i < tmpXZNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, tmpYFront, aCenterPoint.getZ() + tmpDeltaZ);
                } else {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(aCenterPoint.getX() + tmpDeltaX, tmpYBack, aCenterPoint.getZ() + tmpDeltaZ);
                }
            }
            for (int i = 0; i < tmpYZNumber; i++) {
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(tmpXLeft, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                } else {
                    aBuffer[tmpIndex++] = new GraphicalParticlePosition(tmpXRight, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                }
            }

            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="A buffer is an instance of PointInSpace[]">
            for (int i = 0; i < tmpXYNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZUpper);
                } else {
                    aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, aCenterPoint.getY() + tmpDeltaY, tmpZLower);
                }
            }
            for (int i = 0; i < tmpXZNumber; i++) {
                tmpDeltaX = tmpXLength * aRandomNumberGenerator.nextDouble() - tmpHalfXLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, tmpYFront, aCenterPoint.getZ() + tmpDeltaZ);
                } else {
                    aBuffer[tmpIndex++] = new PointInSpace(aCenterPoint.getX() + tmpDeltaX, tmpYBack, aCenterPoint.getZ() + tmpDeltaZ);
                }
            }
            for (int i = 0; i < tmpYZNumber; i++) {
                tmpDeltaY = tmpYLength * aRandomNumberGenerator.nextDouble() - tmpHalfYLength;
                tmpDeltaZ = tmpZLength * aRandomNumberGenerator.nextDouble() - tmpHalfZLength;
                if (aRandomNumberGenerator.nextDouble() > 0.5) {
                    aBuffer[tmpIndex++] = new PointInSpace(tmpXLeft, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                } else {
                    aBuffer[tmpIndex++] = new PointInSpace(tmpXRight, aCenterPoint.getY() + tmpDeltaY, aCenterPoint.getZ() + tmpDeltaZ);
                }
            }

            // </editor-fold>
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Random spheres in body related method">
    /**
     * Returns list with specified number of non-overlapping spheres at
     * random positions in xy-layer with specified center point and x, y,
     * z-length. 
     * NOTE: If non-overlapping spheres could not be created with the
     * specified number of trials possibly overlapping spheres are created.
     * NOTE: The specified radius of spheres may be decreased if necessary.
     *
     * @param anExistingSphereList Linked list with existing spheres (may be null or empty)
     * @param aXyLayerCenterPoint Center point of xy-layer
     * @param aXyLayerXLength X-Length of xy-layer
     * @param aXyLayerYLength Y-Length of xy-layer
     * @param aXyLayerZLength Z-Length of xy-layer
     * @param aNumberOfSpheres Number of spheres
     * @param aRadius Radius of spheres
     * @param aNumberOfTrials Number of trials for sphere generation
     * @param aRandomNumberGenerator Random number generator
     * @return List of non-overlapping spheres or null if none could be created
     */
    public LinkedList<BodySphere> getNonOverlappingRandomSpheresInXyLayer(
        LinkedList<BodySphere> anExistingSphereList, 
        PointInSpace aXyLayerCenterPoint, 
        double aXyLayerXLength, 
        double aXyLayerYLength, 
        double aXyLayerZLength,
        int aNumberOfSpheres, 
        double aRadius, 
        int aNumberOfTrials,
        IRandom aRandomNumberGenerator
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXyLayerCenterPoint == null) {
            return null;
        }
        if (aXyLayerXLength <= 0.0) {
            return null;
        }
        if (aXyLayerYLength <= 0.0) {
            return null;
        }
        if (aXyLayerZLength <= 0.0) {
            return null;
        }
        if (aNumberOfSpheres < 1) {
            return null;
        }
        if (aRadius <= 0.0) {
            return null;
        }
        if (aNumberOfTrials < 1) {
            return null;
        }
        // </editor-fold>
        LinkedList<BodySphere> tmpSphereList = new LinkedList<>();
        boolean tmpAreExistingSpheresDefined = anExistingSphereList != null && !anExistingSphereList.isEmpty();
        double tmpRadius = aRadius;
        // Correct radius if necessary
        if (tmpRadius > aXyLayerXLength / 2.0 || tmpRadius > aXyLayerYLength / 2.0 || tmpRadius > aXyLayerZLength / 2.0) {
            tmpRadius = Math.min(aXyLayerXLength, Math.min(aXyLayerYLength, aXyLayerZLength));
            tmpRadius /= 2.0;
        }

        double tmpCorrectedXyLayerXLength = aXyLayerXLength - tmpRadius - tmpRadius;
        double tmpCorrectedXyLayerYLength = aXyLayerYLength - tmpRadius - tmpRadius;
        double tmpCorrectedXyLayerZLength = aXyLayerZLength - tmpRadius - tmpRadius;
        double tmpOffsetX = tmpCorrectedXyLayerXLength / 2.0;
        double tmpOffsetY = tmpCorrectedXyLayerYLength / 2.0;
        double tmpOffsetZ = tmpCorrectedXyLayerZLength / 2.0;

        PointInSpace tmpTestPoint = new PointInSpace(aXyLayerCenterPoint.getX(), aXyLayerCenterPoint.getY(), aXyLayerCenterPoint.getZ());
        BodySphere tmpTestSphere = new BodySphere(tmpRadius, aXyLayerCenterPoint);

        int tmpTrialCounter = 0;
        int tmpSphereCounter = 0;
        while (tmpSphereCounter < aNumberOfSpheres && tmpTrialCounter < aNumberOfTrials) {
            tmpTestPoint.setX(aXyLayerCenterPoint.getX() + tmpCorrectedXyLayerXLength * aRandomNumberGenerator.nextDouble() - tmpOffsetX);
            tmpTestPoint.setY(aXyLayerCenterPoint.getY() + tmpCorrectedXyLayerYLength * aRandomNumberGenerator.nextDouble() - tmpOffsetY);
            tmpTestPoint.setZ(aXyLayerCenterPoint.getZ() + tmpCorrectedXyLayerZLength * aRandomNumberGenerator.nextDouble() - tmpOffsetZ);
            tmpTestSphere.setBodyCenterWithoutInitialisation(tmpTestPoint);
            boolean tmpIsOverlap = false;
            for (BodySphere tmpSingleSphere : tmpSphereList) {
                if (this.isSphereSphereOverlap(tmpSingleSphere, tmpTestSphere)) {
                    tmpIsOverlap = true;
                    break;
                }
            }
            if (!tmpIsOverlap && tmpAreExistingSpheresDefined) {
                for (BodySphere tmpExistingSphere : anExistingSphereList) {
                    if (tmpExistingSphere.isOverlap(tmpTestSphere)) {
                        tmpIsOverlap = true;
                        break;
                    }
                }
            }
            if (!tmpIsOverlap) {
                tmpSphereList.add(new BodySphere(tmpRadius, tmpTestPoint.getClone()));
                tmpSphereCounter++;
                tmpTrialCounter = 0;
            } else {
                tmpTrialCounter++;
            }
        }

        int numberOfRemainingSpheres = aNumberOfSpheres - tmpSphereCounter;
        if (numberOfRemainingSpheres > 0) {
            // Create possibly overlapping spheres
            for (int i = 0; i < numberOfRemainingSpheres; i++) {
                tmpTestPoint.setX(aXyLayerCenterPoint.getX() + tmpCorrectedXyLayerXLength * aRandomNumberGenerator.nextDouble() - tmpOffsetX);
                tmpTestPoint.setY(aXyLayerCenterPoint.getY() + tmpCorrectedXyLayerYLength * aRandomNumberGenerator.nextDouble() - tmpOffsetY);
                tmpTestPoint.setZ(aXyLayerCenterPoint.getZ() + tmpCorrectedXyLayerZLength * aRandomNumberGenerator.nextDouble() - tmpOffsetZ);
                tmpSphereList.add(new BodySphere(tmpRadius, tmpTestPoint.getClone()));
            }
        }
        if (tmpSphereList.isEmpty()) {
            return null;
        } else {
            return tmpSphereList;
        }
    }

    /**
     * Returns list with specified number of non-overlapping spheres at
     * random positions in sphere with specified center point and radius. 
     * NOTE: If non-overlapping spheres could not be created with the
     * specified number of trials possibly overlapping spheres are created.
     * NOTE: The specified radius of spheres may be decreased if necessary.
     *
     * @param anExistingSphereList Linked list with existing spheres (may be null or empty)
     * @param aSphereCenterPoint Center point of sphere
     * @param aSphereRadius Radius of sphere
     * @param aNumberOfSpheres Number of spheres
     * @param aRadius Radius of spheres
     * @param aNumberOfTrials Number of trials for sphere generation
     * @param aRandomNumberGenerator Random number generator
     * @return List of non-overlapping spheres or null if none could be created
     */
    public LinkedList<BodySphere> getNonOverlappingRandomSpheresInSphere(
        LinkedList<BodySphere> anExistingSphereList, 
        PointInSpace aSphereCenterPoint, 
        double aSphereRadius, 
        int aNumberOfSpheres, 
        double aRadius, 
        int aNumberOfTrials,
        IRandom aRandomNumberGenerator
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSphereCenterPoint == null) {
            return null;
        }
        if (aSphereRadius <= 0.0) {
            return null;
        }
        if (aNumberOfSpheres < 1) {
            return null;
        }
        if (aRadius <= 0.0) {
            return null;
        }
        if (aNumberOfTrials < 1) {
            return null;
        }
        // </editor-fold>
        LinkedList<BodySphere> tmpSphereList = new LinkedList<>();
        boolean tmpAreExistingSpheresDefined = anExistingSphereList != null && !anExistingSphereList.isEmpty();
        double tmpRadius = aRadius;
        // Correct radius if necessary
        if (tmpRadius > aSphereRadius) {
            tmpRadius = aSphereRadius;
        }

        PointInSpace tmpTestPoint = new PointInSpace(aSphereCenterPoint.getX(), aSphereCenterPoint.getY(), aSphereCenterPoint.getZ());
        BodySphere tmpTestSphere = new BodySphere(tmpRadius, aSphereCenterPoint);

        double tmpCorrectedSphereRadius = aSphereRadius - tmpRadius;
        double tmpCubeLength = 2.0 * tmpCorrectedSphereRadius;
        double tmpCorrectedSphereRadiusSquare = tmpCorrectedSphereRadius * tmpCorrectedSphereRadius;
        double tmpDeltaX = 0;
        double tmpDeltaY = 0;
        double tmpDeltaZ = 0;
        double tmpX = 0;
        double tmpY = 0;
        double tmpZ = 0;
        
        int tmpTrialCounter = 0;
        int tmpSphereCounter = 0;
        while (tmpSphereCounter < aNumberOfSpheres && tmpTrialCounter < aNumberOfTrials) {
            do {
                tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                tmpDeltaX = tmpX - tmpCorrectedSphereRadius;
                tmpDeltaY = tmpY - tmpCorrectedSphereRadius;
                tmpDeltaZ = tmpZ - tmpCorrectedSphereRadius;
            } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpCorrectedSphereRadiusSquare);
            tmpTestPoint.setX(aSphereCenterPoint.getX() - tmpCorrectedSphereRadius + tmpX);
            tmpTestPoint.setY(aSphereCenterPoint.getY() - tmpCorrectedSphereRadius + tmpY);
            tmpTestPoint.setZ(aSphereCenterPoint.getZ() - tmpCorrectedSphereRadius + tmpZ);
            tmpTestSphere.setBodyCenterWithoutInitialisation(tmpTestPoint);
            boolean tmpIsOverlap = false;
            for (BodySphere tmpSingleSphere : tmpSphereList) {
                if (this.isSphereSphereOverlap(tmpSingleSphere, tmpTestSphere)) {
                    tmpIsOverlap = true;
                    break;
                }
            }
            if (!tmpIsOverlap && tmpAreExistingSpheresDefined) {
                for (BodySphere tmpExistingSphere : anExistingSphereList) {
                    if (tmpExistingSphere.isOverlap(tmpTestSphere)) {
                        tmpIsOverlap = true;
                        break;
                    }
                }
            }
            if (!tmpIsOverlap) {
                tmpSphereList.add(new BodySphere(tmpRadius, tmpTestPoint.getClone()));
                tmpSphereCounter++;
                tmpTrialCounter = 0;
            } else {
                tmpTrialCounter++;
            }
        }

        int numberOfRemainingSpheres = aNumberOfSpheres - tmpSphereCounter;
        if (numberOfRemainingSpheres > 0) {
            // Create possibly overlapping spheres
            for (int i = 0; i < numberOfRemainingSpheres; i++) {
                do {
                    tmpX = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpY = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpZ = tmpCubeLength * aRandomNumberGenerator.nextDouble();
                    tmpDeltaX = tmpX - tmpCorrectedSphereRadius;
                    tmpDeltaY = tmpY - tmpCorrectedSphereRadius;
                    tmpDeltaZ = tmpZ - tmpCorrectedSphereRadius;
                } while (tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ > tmpCorrectedSphereRadiusSquare);
                tmpTestPoint.setX(aSphereCenterPoint.getX() - tmpCorrectedSphereRadius + tmpX);
                tmpTestPoint.setY(aSphereCenterPoint.getY() - tmpCorrectedSphereRadius + tmpY);
                tmpTestPoint.setZ(aSphereCenterPoint.getZ() - tmpCorrectedSphereRadius + tmpZ);
                tmpSphereList.add(new BodySphere(tmpRadius, tmpTestPoint.getClone()));
            }
        }
        if (tmpSphereList.isEmpty()) {
            return null;
        } else {
            return tmpSphereList;
        }
    }

    /**
     * Returns list with specified number of non-overlapping spheres at
     * random positions that do NOT overlap with existing bodies or excluded 
     * spheres in compartment box with specified center point and x, y, z-length. 
     * NOTE: If non-overlapping spheres could not be created with the
     * specified number of trials possibly overlapping spheres are created.
     * NOTE: The specified radius of spheres may be decreased if necessary.
     *
     * @param anExistingBodies Existing bodies (may be null or empty)
     * @param anExistingSphereList Linked list with existing spheres (may be null or empty)
     * @param aCompartmentBoxCenterPoint Center point of compartment box
     * @param aCompartmentBoxXLength X-Length of compartment box
     * @param aCompartmentBoxYLength Y-Length of compartment box
     * @param aCompartmentBoxZLength Z-Length of compartment box
     * @param aNumberOfSpheres Number of spheres
     * @param aRadius Radius of spheres
     * @param aNumberOfTrials Number of trials for sphere generation
     * @param aRandomNumberGenerator Random number generator
     * @return List of non-overlapping spheres or null if none could be created
     */
    public LinkedList<BodySphere> getNonOverlappingRandomSpheresIntoCompartmentBox(
        ArrayList<BodyInterface> anExistingBodies,
        LinkedList<BodySphere> anExistingSphereList, 
        PointInSpace aCompartmentBoxCenterPoint, 
        double aCompartmentBoxXLength, 
        double aCompartmentBoxYLength, 
        double aCompartmentBoxZLength,
        int aNumberOfSpheres, 
        double aRadius, 
        int aNumberOfTrials,
        IRandom aRandomNumberGenerator
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCompartmentBoxCenterPoint == null) {
            return null;
        }
        if (aCompartmentBoxXLength <= 0.0) {
            return null;
        }
        if (aCompartmentBoxYLength <= 0.0) {
            return null;
        }
        if (aCompartmentBoxZLength <= 0.0) {
            return null;
        }
        if (aNumberOfSpheres < 1) {
            return null;
        }
        if (aRadius <= 0.0) {
            return null;
        }
        if (aNumberOfTrials < 1) {
            return null;
        }
        // </editor-fold>
        LinkedList<BodySphere> tmpSphereList = new LinkedList<>();
        boolean tmpAreBodiesDefined = anExistingBodies != null && !anExistingBodies.isEmpty();
        boolean tmpAreExistingSpheresDefined = anExistingSphereList != null && !anExistingSphereList.isEmpty();
        double tmpRadius = aRadius;
        // Correct radius if necessary
        if (tmpRadius > aCompartmentBoxXLength / 2.0 || tmpRadius > aCompartmentBoxYLength / 2.0 || tmpRadius > aCompartmentBoxZLength / 2.0) {
            tmpRadius = Math.min(aCompartmentBoxXLength, Math.min(aCompartmentBoxYLength, aCompartmentBoxZLength));
            tmpRadius /= 2.0;
        }

        double tmpCorrectedCompartmentBoxXLength = aCompartmentBoxXLength - tmpRadius - tmpRadius;
        double tmpCorrectedCompartmentBoxYLength = aCompartmentBoxYLength - tmpRadius - tmpRadius;
        double tmpCorrectedCompartmentBoxZLength = aCompartmentBoxZLength - tmpRadius - tmpRadius;
        double tmpOffsetX = tmpCorrectedCompartmentBoxXLength / 2.0;
        double tmpOffsetY = tmpCorrectedCompartmentBoxYLength / 2.0;
        double tmpOffsetZ = tmpCorrectedCompartmentBoxZLength / 2.0;

        PointInSpace tmpTestPoint = new PointInSpace(aCompartmentBoxCenterPoint.getX(), aCompartmentBoxCenterPoint.getY(), aCompartmentBoxCenterPoint.getZ());
        LinkedList<PointInSpace> tmpUsedPointList = new LinkedList<>();
        BodySphere tmpTestSphere = new BodySphere(tmpRadius, aCompartmentBoxCenterPoint);

        int tmpTrialCounter = 0;
        int tmpSphereCounter = 0;
        while (tmpSphereCounter < aNumberOfSpheres && tmpTrialCounter < aNumberOfTrials) {
            tmpTestPoint.setX(aCompartmentBoxCenterPoint.getX() + tmpCorrectedCompartmentBoxXLength * aRandomNumberGenerator.nextDouble() - tmpOffsetX);
            tmpTestPoint.setY(aCompartmentBoxCenterPoint.getY() + tmpCorrectedCompartmentBoxYLength * aRandomNumberGenerator.nextDouble() - tmpOffsetY);
            tmpTestPoint.setZ(aCompartmentBoxCenterPoint.getZ() + tmpCorrectedCompartmentBoxZLength * aRandomNumberGenerator.nextDouble() - tmpOffsetZ);
            tmpTestSphere.setBodyCenterWithoutInitialisation(tmpTestPoint);
            boolean tmpIsOverlap = false;
            for (BodySphere tmpSingleSphere : tmpSphereList) {
                if (this.isSphereSphereOverlap(tmpSingleSphere, tmpTestSphere)) {
                    tmpIsOverlap = true;
                    break;
                }
            }
            if (!tmpIsOverlap && tmpAreBodiesDefined) {
                for (BodyInterface tmpBody : anExistingBodies) {
                    if (tmpBody.isOverlap(tmpTestSphere)) {
                        tmpIsOverlap = true;
                        break;
                    }
                }
            }
            if (!tmpIsOverlap && tmpAreExistingSpheresDefined) {
                for (BodySphere tmpExistingSphere : anExistingSphereList) {
                    if (tmpExistingSphere.isOverlap(tmpTestSphere)) {
                        tmpIsOverlap = true;
                        break;
                    }
                }
            }
            if (!tmpIsOverlap) {
                tmpSphereList.add(new BodySphere(tmpRadius, tmpTestPoint.getClone()));
                tmpUsedPointList.add(tmpTestPoint.getClone());
                tmpSphereCounter++;
                tmpTrialCounter = 0;
            } else {
                tmpTrialCounter++;
            }
        }

        int numberOfRemainingSpheres = aNumberOfSpheres - tmpSphereCounter;
        if (numberOfRemainingSpheres > 0) {
            // Re-use spheres if possible 
            // (this is NOT an optimum solution but better than placing a sphere SOMEWHERE)
            PointInSpace[] tmpUsedPoints = null;
            if (!tmpUsedPointList.isEmpty()) {
                tmpUsedPoints = tmpUsedPointList.toArray(new PointInSpace[0]);                
            }
            if (tmpUsedPoints == null) {
                // Re-use of spheres is impossible (pathological case): Place sphere SOMEWHERE
                for (int i = 0; i < numberOfRemainingSpheres; i++) {
                    tmpTestPoint.setX(aCompartmentBoxCenterPoint.getX() + tmpCorrectedCompartmentBoxXLength * aRandomNumberGenerator.nextDouble() - tmpOffsetX);
                    tmpTestPoint.setY(aCompartmentBoxCenterPoint.getY() + tmpCorrectedCompartmentBoxYLength * aRandomNumberGenerator.nextDouble() - tmpOffsetY);
                    tmpTestPoint.setZ(aCompartmentBoxCenterPoint.getZ() + tmpCorrectedCompartmentBoxZLength * aRandomNumberGenerator.nextDouble() - tmpOffsetZ);
                    tmpSphereList.add(new BodySphere(tmpRadius, tmpTestPoint.getClone()));
                }
            } else {
                // Re-use of spheres
                int tmpUsedPointsIndex = 0;
                for (int i = 0; i < numberOfRemainingSpheres; i++) {
                    tmpSphereList.add(new BodySphere(tmpRadius, tmpUsedPoints[tmpUsedPointsIndex++].getClone()));
                    // Re-Re-...-use points if necessary
                    if (tmpUsedPointsIndex == tmpUsedPoints.length) {
                        tmpUsedPointsIndex = 0;
                    }
                }
            }
        }
        if (tmpSphereList.isEmpty()) {
            return null;
        } else {
            return tmpSphereList;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Body overlap related methods">
    /**
     * Returns if two spheres overlap
     *
     * @param aSphere1 Sphere 1
     * @param aSphere2 Sphere 2
     * @return True: Spheres overlap, false: Otherwise
     */
    public boolean isSphereSphereOverlap(BodySphere aSphere1, BodySphere aSphere2) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSphere1 == null || aSphere2 == null) {
            return false;
        }
        // </editor-fold>
        return this.getDistanceInSpace(aSphere1.getBodyCenter(), aSphere2.getBodyCenter()) <= aSphere1.getRadius() + aSphere2.getRadius();
    }

    /**
     * Returns if a sphere and a xy-layer overlap
     *
     * @param aSphere Sphere
     * @param aXyLayer Xy-layer
     * @return True: Bodies overlap, false: Otherwise
     * 
     * @author Sebastian Fritsch
     */
    public boolean isSphereXyLayerOverlap(BodySphere aSphere, BodyXyLayer aXyLayer) {
        //<editor-fold defaultstate="collapsed" desc="Checks">
        if (aSphere == null || aXyLayer == null) {
            return false;
        }
        //</editor-fold>
        // Determine minimum and maximum points within the range of XyLayer
        PointInSpace tmpMinPoint = 
            new PointInSpace(
                aXyLayer.getBodyCenter().getX() - aXyLayer.getXLength() / 2, 
                aXyLayer.getBodyCenter().getY() - aXyLayer.getYLength() / 2, 
                aXyLayer.getBodyCenter().getZ() - aXyLayer.getZLength() / 2
            );
        PointInSpace tmpMaxPoint = 
            new PointInSpace(
                aXyLayer.getBodyCenter().getX() + aXyLayer.getXLength() / 2, 
                aXyLayer.getBodyCenter().getY() + aXyLayer.getYLength() / 2, 
                aXyLayer.getBodyCenter().getZ() + aXyLayer.getZLength() / 2
            );
        PointInSpace tmpSphereCenter = aSphere.getBodyCenter();
        // Calculation of the point closest to the sphere center within the XyLayer (on each axis)
        double tmpClosestX = tmpSphereCenter.getX() > tmpMaxPoint.getX() ? tmpMaxPoint.getX() : tmpSphereCenter.getX() < tmpMinPoint.getX() ? tmpMinPoint.getX() : tmpSphereCenter.getX();
        double tmpClosestY = tmpSphereCenter.getY() > tmpMaxPoint.getY() ? tmpMaxPoint.getY() : tmpSphereCenter.getY() < tmpMinPoint.getY() ? tmpMinPoint.getY() : tmpSphereCenter.getY();
        double tmpClosestZ = tmpSphereCenter.getZ() > tmpMaxPoint.getZ() ? tmpMaxPoint.getZ() : tmpSphereCenter.getZ() < tmpMinPoint.getZ() ? tmpMinPoint.getZ() : tmpSphereCenter.getZ();
        PointInSpace tmpClosestPointInRange = new PointInSpace(tmpClosestX, tmpClosestY, tmpClosestZ);
        // Measure distance between the sphere center point and the closest point
        // within the range of XyLayer and compare distance to the sphere's radius
        return this.getDistanceInSpace(tmpClosestPointInRange, tmpSphereCenter) <= aSphere.getRadius();
    }

    /**
     * Returns if two xy-layers overlap
     *
     * @param aXyLayer1 Xy-layer 1
     * @param aXyLayer2 Xy-layer 2
     * @return True: Xy-layers overlap, false: Otherwise
     * 
     * @author Sebastian Fritsch
     */
    public boolean isXyLayerXyLayerOverlap(BodyXyLayer aXyLayer1, BodyXyLayer aXyLayer2) {
        //<editor-fold defaultstate="collapsed" desc="Checks">
        if (aXyLayer1 == null || aXyLayer2 == null) {
            return false;
        }
        //</editor-fold>
        PointInSpace tmpCenter1 = aXyLayer1.getBodyCenter();
        PointInSpace tmpCenter2 = aXyLayer2.getBodyCenter();
        // Check for overlaps on each axis by comparing the distance between 
        // the center points to the layer's combined length / 2
        boolean tmpIsOverlapX = (Math.abs(tmpCenter1.getX() - tmpCenter2.getX()) <= ((aXyLayer1.getXLength() + aXyLayer2.getXLength()) / 2));
        boolean tmpIsOverlapY = (Math.abs(tmpCenter1.getY() - tmpCenter2.getY()) <= ((aXyLayer1.getYLength() + aXyLayer2.getYLength()) / 2));
        boolean tmpIsOverlapZ = (Math.abs(tmpCenter1.getZ() - tmpCenter2.getZ()) <= ((aXyLayer1.getZLength() + aXyLayer2.getZLength()) / 2));
        return tmpIsOverlapX && tmpIsOverlapY && tmpIsOverlapZ;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Distances in space">
    /**
     * Calculates the distance in x direction between two points in space
     *
     * @param aPoint1 Point in space
     * @param aPoint2 Point in space
     * @return Distance in x direction between aPoint1 and aPoint2
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public double getDistanceInXInSpace(PointInSpace aPoint1, PointInSpace aPoint2) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        return Math.abs(aPoint1.getX() - aPoint2.getX());
    }

    /**
     * Calculates the distance in y direction between two points in space
     *
     * @param aPoint1 Point in space
     * @param aPoint2 Point in space
     * @return Distance in y direction between aPoint1 and aPoint2
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public double getDistanceInYInSpace(PointInSpace aPoint1, PointInSpace aPoint2) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        return Math.abs(aPoint1.getY() - aPoint2.getY());
    }

    /**
     * Calculates the distance in z direction between two points in space
     *
     * @param aPoint1 Point in space
     * @param aPoint2 Point in space
     * @return Distance in z direction between aPoint1 and aPoint2
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public double getDistanceInZInSpace(PointInSpace aPoint1, PointInSpace aPoint2) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        return Math.abs(aPoint1.getZ() - aPoint2.getZ());
    }

    /**
     * Calculates the distance on the xy plane between two points in space
     *
     * @param aPoint1 Point in space
     * @param aPoint2 Point in space
     * @return Distance on xy plane between aPoint1 and aPoint2
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public double getDistanceOnXyPlaneInSpace(PointInSpace aPoint1, PointInSpace aPoint2) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        return Math.sqrt(Math.pow(aPoint1.getX() - aPoint2.getX(), 2) + Math.pow(aPoint1.getY() - aPoint2.getY(), 2));
    }

    /**
     * Calculates the distance on the xz plane between two points in space
     *
     * @param aPoint1 Point in space
     * @param aPoint2 Point in space
     * @return Distance on xz plane between aPoint1 and aPoint2
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public double getDistanceOnXzPlaneInSpace(PointInSpace aPoint1, PointInSpace aPoint2) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        return Math.sqrt(Math.pow(aPoint1.getX() - aPoint2.getX(), 2) + Math.pow(aPoint1.getZ() - aPoint2.getZ(), 2));
    }

    /**
     * Calculates the distance on the yz plane between two points in space
     *
     * @param aPoint1 Point in space
     * @param aPoint2 Point in space
     * @return Distance on yz plane between aPoint1 and aPoint2
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public double getDistanceOnYzPlaneInSpace(PointInSpace aPoint1, PointInSpace aPoint2) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        return Math.sqrt(Math.pow(aPoint1.getY() - aPoint2.getY(), 2) + Math.pow(aPoint1.getZ() - aPoint2.getZ(), 2));
    }

    /**
     * Calculates the distance between two points in space
     *
     * @param aPoint1 Point in space
     * @param aPoint2 Point in space
     * @return Distance in space between aPoint1 and aPoint2
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public double getDistanceInSpace(IPointInSpace aPoint1, IPointInSpace aPoint2) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 is null.");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 is null.");
        }
        // </editor-fold>
        double tmpDeltaX = aPoint1.getX() - aPoint2.getX();
        double tmpDeltaY = aPoint1.getY() - aPoint2.getY();
        double tmpDeltaZ = aPoint1.getZ() - aPoint2.getZ();
        return Math.sqrt(tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ);
        // Slower alternative:
        // return Math.sqrt(Math.pow(aPoint1.getX() - aPoint2.getX(), 2) + Math.pow(aPoint1.getY() - aPoint2.getY(), 2) + Math.pow(aPoint1.getZ() - aPoint2.getZ(), 2));
    }

    /**
     * Calculates the square of the distance between two points in space
     *
     * @param aPoint1 Point in space
     * @param aPoint2 Point in space
     * @return Square of the distance in space between aPoint1 and aPoint2
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public double getDistanceSquareInSpace(IPointInSpace aPoint1, IPointInSpace aPoint2) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 is null.");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 is null.");
        }
        // </editor-fold>
        double tmpDeltaX = aPoint1.getX() - aPoint2.getX();
        double tmpDeltaY = aPoint1.getY() - aPoint2.getY();
        double tmpDeltaZ = aPoint1.getZ() - aPoint2.getZ();
        return tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ;
        // Slower alternative:
        // return Math.sqrt(Math.pow(aPoint1.getX() - aPoint2.getX(), 2) + Math.pow(aPoint1.getY() - aPoint2.getY(), 2) + Math.pow(aPoint1.getZ() - aPoint2.getZ(), 2));
    }

    /**
     * Calculates the distance between two points in space
     *
     * @param aPoint1 Point in space
     * @param aPoint2 Point in space
     * @param aLengthConversionFactor Factor that converts DPD length to
     * physical length (Angstrom since particle volumes are in Angstrom^3)
     * @return Distance in space between aPoint1 and aPoint2
     * @throws IllegalArgumentException Thrown if aPoint1 or aPoint2 is null
     */
    public double getConvertedDistanceInSpace(IPointInSpace aPoint1, IPointInSpace aPoint2, double aLengthConversionFactor) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint1 == null) {
            throw new IllegalArgumentException("aPoint1 was null");
        }
        if (aPoint2 == null) {
            throw new IllegalArgumentException("aPoint2 was null");
        }

        // </editor-fold>
        double tmpDeltaX = (aPoint1.getX() - aPoint2.getX()) * aLengthConversionFactor;
        double tmpDeltaY = (aPoint1.getY() - aPoint2.getY()) * aLengthConversionFactor;
        double tmpDeltaZ = (aPoint1.getZ() - aPoint2.getZ()) * aLengthConversionFactor;
        return Math.sqrt(tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ);
        // Slower alternative:
        // return Math.sqrt(Math.pow((aPoint1.getX() - aPoint2.getX()) * aLengthConversionFactor, 2) + Math.pow((aPoint1.getY() - aPoint2.getY()) * aLengthConversionFactor, 2)
        //        + Math.pow((aPoint1.getZ() - aPoint2.getZ()) * aLengthConversionFactor, 2));
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Comparison of numbers">
    /**
     * Compares if two numbers are equal. The chosen precision should avoid
     * problems with deviations in the last digits due to numerical
     * calculations.
     *
     * @param aNumber1 First number for comparison
     * @param aNumber2 Second number for comparison
     * @return True if numbers are equal, false if numbers are not equal.
     */
    public boolean isEqual(double aNumber1, double aNumber2) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber1 == aNumber2) {
            return true;
        }
        // </editor-fold>
        if (aNumber1 > aNumber2) {
            return aNumber1 - aNumber2 < Math.abs(aNumber1) * ModelDefinitions.FACTOR_FOR_COMPARISON_OF_GRAPHICS_NUMBERS;
        } else {
            return aNumber2 - aNumber1 < Math.abs(aNumber2) * ModelDefinitions.FACTOR_FOR_COMPARISON_OF_GRAPHICS_NUMBERS;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Geometry value item related methods">
    /**
     * Returns if value item describes geometry of a sphere (data or display)
     *
     * @param aValueItem ValueItem
     * @return True: Value item describes geometry of a sphere (data or
     * display), false: Otherwise
     */
    public boolean isSphereGeometryValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().startsWith(ModelDefinitions.COMPARTMENT_SPHERE_GEOMETRY_PREFIX_NAME);
    }

    /**
     * Returns if value item describes geometry of a xy-layer (data or display)
     *
     * @param aValueItem ValueItem
     * @return True: Value item describes geometry of a xy-layer (data or
     * display), false: Otherwise
     */
    public boolean isXyLayerGeometryValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().startsWith(ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_PREFIX_NAME);
    }

    /**
     * Returns if value item describes geometry data of a sphere
     *
     * @param aValueItem ValueItem
     * @return True: Value item describes geometry data of a sphere, false:
     * Otherwise
     */
    public boolean isSphereGeometryDataValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().startsWith(ModelDefinitions.COMPARTMENT_SPHERE_GEOMETRY_DATA_PREFIX_NAME);
    }

    /**
     * Returns if value item describes geometry data of a xy-layer
     *
     * @param aValueItem ValueItem
     * @return True: Value item describes geometry data of a xy-layer, false:
     * Otherwise
     */
    public boolean isXyLayerGeometryDataValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().startsWith(ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_DATA_PREFIX_NAME);
    }

    /**
     * Returns if value item describes display data of a sphere
     *
     * @param aValueItem ValueItem
     * @return True: Value item describes display data of a sphere, false:
     * Otherwise
     */
    public boolean isSphereGeometryDisplayValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().startsWith(ModelDefinitions.COMPARTMENT_SPHERE_GEOMETRY_DISPLAY_PREFIX_NAME);
    }

    /**
     * Returns if value item describes display data of a xy-layer
     *
     * @param aValueItem ValueItem
     * @return True: Value item describes display data of a xy-layer, false:
     * Otherwise
     */
    public boolean isXyLayerGeometryDisplayValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().startsWith(ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_DISPLAY_PREFIX_NAME);
    }

    /**
     * Returns if value item describes geometry
     *
     * @param aValueItem ValueItem
     * @return True: Value item describes geometry, false: Otherwise
     */
    public boolean isGeometryValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().startsWith(ModelDefinitions.GEOMETRY_PREFIX_NAME);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Molecule settings value item related methods">
    /**
     * Fills value item container with value items for molecule display settings
     *
     * @param aValueItemContainer Value item container to be filled with value
     * items for molecule settings
     * @param aVerticalPosition Vertical position for first value item to be
     * added
     * @param aRootNodeNames Root node names for value items to be added
     * @param aParticleList Linked string list with particles (not allowed to be
     * null/empty)
     * @param aParticleToParticleNameMap Particle to particle name map (not
     * allowed to be null/empty, all particles of aParticleList must be in
     * aParticleToParticleNameMap)
     * @param anExcludedParticlesTable Table with excluded particles (may be
     * null/empty)
     * @param aParticleToCurrentColorMap Particle to color map (not allowed to
     * be null/empty, all particles of aParticleList must be in
     * aParticleToCurrentColorMap)
     * @param aParticleToInitialColorMap Particle to initial color map (not
     * allowed to be null/empty, all particles of aParticleList must be in
     * aParticleToInitialColorMap)
     * @param aMoleculeList Linked string list with molecules (not allowed to be
     * null/empty)
     * @param anExcludedMoleculesTable Table with excluded molecules (may be
     * null/empty)
     * @param aMoleculeToCurrentColorMap Molecule to color map (not allowed to
     * be null/empty, all molecules of aMoleculeList must be in
     * aMoleculeToCurrentColorMap)
     * @param aMoleculeToInitialColorMap Molecule to initial color map (not
     * allowed to be null/empty, all molecules of aMoleculeList must be in
     * aMoleculeToInitialColorMap)
     * @param aMoleculeParticleStringList Linked string list with
     * molecule-particle strings (not allowed to be null/empty)
     * @param aMoleculeParticleStringToGraphicalParticleMap Molecule-particle
     * string to graphical particle map (not allowed to be null/empty, all
     * molecule-particle strings of aMoleculeParticleStringList must be in
     * aMoleculeParticleStringToGraphicalParticleMap)
     * @param anExcludedMoleculeParticleStringTable Table with excluded
     * molecule-particles (may be null/empty)
     * @param aMoleculeParticleStringToCurrentColorMap Molecule-particle string
     * to color map (not allowed to be null/empty, all molecule-particle strings
     * of aMoleculeParticleStringList must be in
     * aMoleculeParticleStringToCurrentColorMap)
     * @param aMoleculeParticleStringToInitialColorMap Molecule-particle string
     * to initial color map (not allowed to be null/empty, all molecule-particle
     * strings of aMoleculeParticleStringList must be in
     * aMoleculeParticleStringToInitialColorMap)
     * @param aMoleculeParticleStringToCurrentRadiusScale Molecule-particle
     * string to radius scale map (not allowed to be null/empty, all
     * molecule-particle strings of aMoleculeParticleStringList must be in
     * aMoleculeParticleStringToCurrentRadiusScale)
     * @param aMoleculeParticleStringToCurrentTransparency Molecule-particle
     * string to transparency map (not allowed to be null/empty, all
     * molecule-particle strings of aMoleculeParticleStringList must be in
     * aMoleculeParticleStringToCurrentTransparency)
     * @param aHasCompartments True: Compartment and bulk related value items
     * are used, false: Otherwise
     * @return Vertical position for next value item or -1 if molecule display
     * settings value items could not be added
     */
    public int addMoleculeDisplaySettingsValueItems(
        ValueItemContainer aValueItemContainer,
        int aVerticalPosition,
        String[] aRootNodeNames,
        LinkedList<String> aParticleList,
        HashMap<String, String> aParticleToParticleNameMap,
        HashMap<String, String> anExcludedParticlesTable,
        HashMap<String, Color> aParticleToCurrentColorMap,
        HashMap<String, Color> aParticleToInitialColorMap,
        LinkedList<String> aMoleculeList,
        HashMap<String, String> anExcludedMoleculesTable,
        HashMap<String, Color> aMoleculeToCurrentColorMap,
        HashMap<String, Color> aMoleculeToInitialColorMap,
        LinkedList<String> aMoleculeParticleStringList,
        HashMap<String, GraphicalParticle> aMoleculeParticleStringToGraphicalParticleMap,
        HashMap<String, String> anExcludedMoleculeParticleStringTable,
        HashMap<String, Color> aMoleculeParticleStringToCurrentColorMap,
        HashMap<String, Color> aMoleculeParticleStringToInitialColorMap,
        HashMap<String, Double> aMoleculeParticleStringToCurrentRadiusScale,
        HashMap<String, Float> aMoleculeParticleStringToCurrentTransparency,
        boolean aHasCompartments
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            return -1;
        }
        if (aVerticalPosition < 0) {
            return -1;
        }
        if (aRootNodeNames == null || aRootNodeNames.length == 0) {
            return -1;
        }
        if (aParticleList == null || aParticleList.size() == 0) {
            return -1;
        }
        if (aParticleToParticleNameMap == null || aParticleToParticleNameMap.size() == 0) {
            return -1;
        }
        for (String tmpSingleParticle : aParticleList) {
            if (!aParticleToParticleNameMap.containsKey(tmpSingleParticle)) {
                return -1;
            }
        }
        if (aParticleToCurrentColorMap == null || aParticleToCurrentColorMap.size() == 0) {
            return -1;
        }
        if (aParticleToInitialColorMap == null || aParticleToInitialColorMap.size() == 0) {
            return -1;
        }
        for (String tmpSingleParticle : aParticleList) {
            if (!(aParticleToCurrentColorMap.containsKey(tmpSingleParticle) && aParticleToInitialColorMap.containsKey(tmpSingleParticle))) {
                return -1;
            }
        }
        if (aMoleculeList == null || aMoleculeList.size() == 0) {
            return -1;
        }
        if (aMoleculeToCurrentColorMap == null || aMoleculeToCurrentColorMap.size() == 0) {
            return -1;
        }
        if (aMoleculeToInitialColorMap == null || aMoleculeToInitialColorMap.size() == 0) {
            return -1;
        }
        for (String tmpSingleMolecule : aMoleculeList) {
            if (!(aMoleculeToCurrentColorMap.containsKey(tmpSingleMolecule) && aMoleculeToInitialColorMap.containsKey(tmpSingleMolecule))) {
                return -1;
            }
        }
        if (aMoleculeParticleStringList == null || aMoleculeParticleStringList.size() == 0) {
            return -1;
        }
        if (aMoleculeParticleStringToGraphicalParticleMap == null || aMoleculeParticleStringToGraphicalParticleMap.size() == 0) {
            return -1;
        }
        for (String tmpSingleMoleculeParticleString : aMoleculeParticleStringList) {
            if (!aMoleculeParticleStringToGraphicalParticleMap.containsKey(tmpSingleMoleculeParticleString)) {
                return -1;
            }
        }
        if (aMoleculeParticleStringToCurrentColorMap == null || aMoleculeParticleStringToCurrentColorMap.size() == 0) {
            return -1;
        }
        if (aMoleculeParticleStringToInitialColorMap == null || aMoleculeParticleStringToInitialColorMap.size() == 0) {
            return -1;
        }
        if (aMoleculeParticleStringToCurrentRadiusScale == null || aMoleculeParticleStringToCurrentRadiusScale.size() == 0) {
            return -1;
        }
        if (aMoleculeParticleStringToCurrentTransparency == null || aMoleculeParticleStringToCurrentTransparency.size() == 0) {
            return -1;
        }
        for (String tmpSingleMoleculeParticleString : aMoleculeParticleStringList) {
            if (!(aMoleculeParticleStringToCurrentColorMap.containsKey(tmpSingleMoleculeParticleString) && aMoleculeParticleStringToInitialColorMap.containsKey(tmpSingleMoleculeParticleString))) {
                return -1;
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialize">
        ValueItemDataTypeFormat tmpSelectionTypeFormat = new ValueItemDataTypeFormat(ModelMessage.get("MoleculeDisplaySettings.On"), new String[]{
            ModelMessage.get("MoleculeDisplaySettings.On"), ModelMessage.get("MoleculeDisplaySettings.Off")});

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set value item for color mode">
        String[] tmpNodeNames = this.stringUtilityMethods.getConcatenatedStringArrays(aRootNodeNames, new String[]{ModelMessage.get("Preferences.ParticleColorDisplayMode")});
        ValueItem tmpValueItem = Preferences.getInstance().getParticleColorDisplayModeValueItem();
        // IMPORTANT: Set default color mode = molecule-particle color
        tmpValueItem.setValue(ModelMessage.get("Preferences.ParticleColorDisplayMode.MoleculeParticleColorMode"));
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set value items for molecules display and rendering">
        tmpNodeNames = this.stringUtilityMethods.getConcatenatedStringArrays(aRootNodeNames, new String[]{ModelMessage.get("MoleculeDisplaySettings.MoleculesDisplay")});
        // <editor-fold defaultstate="collapsed" desc="- Set value item for particles">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setName(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLES");
        tmpValueItem.setDisplayName(ModelMessage.get("MoleculeDisplaySettings.Particles"));
        tmpValueItem.setDescription(ModelMessage.get("MoleculeDisplaySettings.Particles.Description"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("MoleculeDisplaySettings.Particles.Particle"),
            ModelMessage.get("MoleculeDisplaySettings.Particles.ParticleName"),
            ModelMessage.get("MoleculeDisplaySettings.Particles.Display"),
            ModelMessage.get("MoleculeDisplaySettings.Particles.RenderingColor")});
        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_100, // Particle
            ModelDefinitions.CELL_WIDTH_TEXT_150, // Particle_Name
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Display
            ModelDefinitions.CELL_WIDTH_TEXT_100});
        // Set particle information
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[aParticleList.size()][];
        int tmpIndex = 0;
        for (String tmpParticle : aParticleList) {
            tmpMatrix[tmpIndex] = new ValueItemMatrixElement[4];
            // Set particle. Parameter false: Not editable
            tmpMatrix[tmpIndex][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpParticle, ValueItemEnumDataType.TEXT, false));
            // Set particle name. Parameter false: Not editable
            tmpMatrix[tmpIndex][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(aParticleToParticleNameMap.get(tmpParticle),
                    ValueItemEnumDataType.TEXT, false));
            // Set display
            String tmpDisplayValue = null;
            if (anExcludedParticlesTable == null || !anExcludedParticlesTable.containsKey(tmpParticle)) {
                tmpDisplayValue = ModelMessage.get("MoleculeDisplaySettings.On");
            } else {
                tmpDisplayValue = ModelMessage.get("MoleculeDisplaySettings.Off");
            }
            tmpMatrix[tmpIndex][2] = new ValueItemMatrixElement(tmpDisplayValue, tmpSelectionTypeFormat);
            // Set rendering color
            String tmpColorRepresentation = StandardColorEnum.toStandardColor(aParticleToCurrentColorMap.get(tmpParticle)).toString();
            String tmpInitialColorRepresentation = StandardColorEnum.toStandardColor(aParticleToInitialColorMap.get(tmpParticle)).toString();
            tmpMatrix[tmpIndex][3] = new ValueItemMatrixElement(tmpColorRepresentation, new ValueItemDataTypeFormat(tmpInitialColorRepresentation,
                    StandardColorEnum.getAllColorRepresentations()));
            tmpIndex++;
        }
        tmpValueItem.setMatrix(tmpMatrix);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Set value item for molecules">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setName(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MoleculeTable");
        tmpValueItem.setDisplayName(ModelMessage.get("MoleculeDisplaySettings.Molecules"));
        tmpValueItem.setDescription(ModelMessage.get("MoleculeDisplaySettings.Molecules.Description"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("MoleculeDisplaySettings.Molecules.MoleculeName"),
            ModelMessage.get("MoleculeDisplaySettings.Molecules.Display"),
            ModelMessage.get("MoleculeDisplaySettings.Molecules.RenderingColor")});
        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecule_Name
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Display
            ModelDefinitions.CELL_WIDTH_TEXT_100});
        // Set molecule information
        tmpMatrix = new ValueItemMatrixElement[aMoleculeList.size()][];
        tmpIndex = 0;
        for (String tmpMolecule : aMoleculeList) {
            tmpMatrix[tmpIndex] = new ValueItemMatrixElement[3];
            // Set molecule name. Parameter false: Not editable
            tmpMatrix[tmpIndex][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpMolecule, ValueItemEnumDataType.TEXT, false));
            // Set display
            String tmpDisplayValue = null;
            if (anExcludedMoleculesTable == null || !anExcludedMoleculesTable.containsKey(tmpMolecule)) {
                tmpDisplayValue = ModelMessage.get("MoleculeDisplaySettings.On");
            } else {
                tmpDisplayValue = ModelMessage.get("MoleculeDisplaySettings.Off");
            }
            tmpMatrix[tmpIndex][1] = new ValueItemMatrixElement(tmpDisplayValue, tmpSelectionTypeFormat);
            // Set rendering color
            String tmpColorRepresentation = StandardColorEnum.toStandardColor(aMoleculeToCurrentColorMap.get(tmpMolecule)).toString();
            String tmpInitialColorRepresentation = StandardColorEnum.toStandardColor(aMoleculeToInitialColorMap.get(tmpMolecule)).toString();
            tmpMatrix[tmpIndex][2] = new ValueItemMatrixElement(tmpColorRepresentation, new ValueItemDataTypeFormat(tmpInitialColorRepresentation,
                    StandardColorEnum.getAllColorRepresentations()));
            tmpIndex++;
        }
        tmpValueItem.setMatrix(tmpMatrix);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Set value item for molecule-particles">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setName(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLES_IN_MOLECULES");
        tmpValueItem.setDisplayName(ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles"));
        tmpValueItem.setDescription(ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.Description"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpValueItem.setMatrixColumnNames(new String[]{
                ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.MoleculeName"),
                ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.Particle"),
                ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.ParticleName"),
                ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.Display"),
                ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.RenderingColor"),
                ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.RadiusScale"),
                ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.Transparency")
            }
        );
        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecule_Name
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Particle
            ModelDefinitions.CELL_WIDTH_TEXT_150, // Particle_Name
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Display
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Rendering_Color
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Radius_Scale
            ModelDefinitions.CELL_WIDTH_TEXT_100}); // Transparency
        // Set molecule-particle information
        tmpMatrix = new ValueItemMatrixElement[aMoleculeParticleStringList.size()][];
        String[] tmpMoleculeParticleStrings = new String[aMoleculeParticleStringList.size()];
        tmpIndex = 0;
        for (String tmpMoleculeParticleString : aMoleculeParticleStringList) {
            tmpMoleculeParticleStrings[tmpIndex] = tmpMoleculeParticleString;
            GraphicalParticle tmpGraphicalParticle = aMoleculeParticleStringToGraphicalParticleMap.get(tmpMoleculeParticleString);
            String tmpMolecule = tmpGraphicalParticle.getMoleculeName();
            String tmpParticle = tmpGraphicalParticle.getParticle();
            String tmpParticleName = tmpGraphicalParticle.getParticleName();
            tmpMatrix[tmpIndex] = new ValueItemMatrixElement[7];
            // Set molecule name. Parameter false: Not editable
            tmpMatrix[tmpIndex][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpMolecule, ValueItemEnumDataType.TEXT, false));
            // Set particle. Parameter false: Not editable
            tmpMatrix[tmpIndex][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpParticle, ValueItemEnumDataType.TEXT, false));
            // Set particle name. Parameter false: Not editable
            tmpMatrix[tmpIndex][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpParticleName, ValueItemEnumDataType.TEXT, false));
            // Set display
            String tmpDisplayValue;
            if (anExcludedMoleculeParticleStringTable == null || !anExcludedMoleculeParticleStringTable.containsKey(tmpMoleculeParticleString)) {
                tmpDisplayValue = ModelMessage.get("MoleculeDisplaySettings.On");
            } else {
                tmpDisplayValue = ModelMessage.get("MoleculeDisplaySettings.Off");
            }
            tmpMatrix[tmpIndex][3] = new ValueItemMatrixElement(tmpDisplayValue, tmpSelectionTypeFormat);
            // Set rendering color
            String tmpColorRepresentation = StandardColorEnum.toStandardColor(aMoleculeParticleStringToCurrentColorMap.get(tmpMoleculeParticleString)).toString();
            String tmpInitialColorRepresentation = StandardColorEnum.toStandardColor(aMoleculeParticleStringToInitialColorMap.get(tmpMoleculeParticleString)).toString();
            tmpMatrix[tmpIndex][4] = new ValueItemMatrixElement(tmpColorRepresentation, new ValueItemDataTypeFormat(tmpInitialColorRepresentation,
                    StandardColorEnum.getAllColorRepresentations()));
            // Set radius scale in %
            tmpMatrix[tmpIndex][5] = new ValueItemMatrixElement(String.valueOf(aMoleculeParticleStringToCurrentRadiusScale.get(tmpMoleculeParticleString)),
                    new ValueItemDataTypeFormat("100", 3, 0.001, 1000.0));
            // Set transparency (from 0 = opaque to 1 = transparent)
            tmpMatrix[tmpIndex][6] = new ValueItemMatrixElement(String.valueOf(aMoleculeParticleStringToCurrentTransparency.get(tmpMoleculeParticleString)),
                    new ValueItemDataTypeFormat("0.0", 2, 0.0, 1.0));
            tmpIndex++;
        }
        // IMPORTANT: Set tmpMoleculeParticleStrings as supplementary data. NOTE: tmpMoleculeParticleStrings[i] corresponds to row i of value item matrix.
        tmpValueItem.setSupplementaryData(tmpMoleculeParticleStrings);
        tmpValueItem.setMatrix(tmpMatrix);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        // </editor-fold>
        if (aHasCompartments) {
            // <editor-fold defaultstate="collapsed" desc="Set value item for bulk and compartments">
            tmpNodeNames = this.stringUtilityMethods.getConcatenatedStringArrays(aRootNodeNames, new String[]{ModelMessage.get("MoleculeDisplaySettings.BulkCompartmentsDisplay")});
            // <editor-fold defaultstate="collapsed" desc="Set value item for bulk">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setDefaultTypeFormat(tmpSelectionTypeFormat);
            tmpValueItem.setName(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MOLECULE_DISPLAY_IN_BULK");
            tmpValueItem.setDescription(ModelMessage.get("MoleculeDisplaySettings.Bulk.Description"));
            tmpValueItem.setDisplayName(ModelMessage.get("MoleculeDisplaySettings.Bulk.Display"));
            if (anExcludedMoleculesTable == null || !anExcludedMoleculesTable.containsKey(tmpValueItem.getName())) {
                tmpValueItem.setValue(ModelMessage.get("MoleculeDisplaySettings.On"));
            } else {
                tmpValueItem.setValue(ModelMessage.get("MoleculeDisplaySettings.Off"));
            }
            tmpValueItem.setVerticalPosition(aVerticalPosition++);
            aValueItemContainer.addValueItem(tmpValueItem);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set value item for compartments">
            tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setDefaultTypeFormat(tmpSelectionTypeFormat);
            tmpValueItem.setName(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MOLECULE_DISPLAY_IN_COMPARTMENTS");
            tmpValueItem.setDescription(ModelMessage.get("MoleculeDisplaySettings.Compartments.Description"));
            tmpValueItem.setDisplayName(ModelMessage.get("MoleculeDisplaySettings.Compartments.Display"));
            if (anExcludedMoleculesTable == null || !anExcludedMoleculesTable.containsKey(tmpValueItem.getName())) {
                tmpValueItem.setValue(ModelMessage.get("MoleculeDisplaySettings.On"));
            } else {
                tmpValueItem.setValue(ModelMessage.get("MoleculeDisplaySettings.Off"));
            }
            tmpValueItem.setVerticalPosition(aVerticalPosition++);
            aValueItemContainer.addValueItem(tmpValueItem);

            // </editor-fold>
            // </editor-fold>
        }
        return aVerticalPosition;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Slicer related methods">
    /**
     * Sorts graphical particle position array list according to box view.
     * NOTE: No checks are performed
     *
     * @param aBoxView Box view
     * @param aGraphicalParticlePositionArrayList Graphical particle position array list
     */
    public void sortGraphicalParticlePositions(SimulationBoxViewEnum aBoxView, GraphicalParticlePositionArrayList aGraphicalParticlePositionArrayList) {
        switch (aBoxView) {
            case XZ_FRONT:
                this.miscUtilityMethods.sortGenericArray(
                    aGraphicalParticlePositionArrayList.getGraphicalParticlePositions(), 
                    aGraphicalParticlePositionArrayList.getSize(), 
                    new YAscendingComparator()
                );
                break;
            case XZ_BACK:
                this.miscUtilityMethods.sortGenericArray(
                    aGraphicalParticlePositionArrayList.getGraphicalParticlePositions(), 
                    aGraphicalParticlePositionArrayList.getSize(), 
                    new YDescendingComparator()
                );
                break;
            case XY_BOTTOM:
                this.miscUtilityMethods.sortGenericArray(
                    aGraphicalParticlePositionArrayList.getGraphicalParticlePositions(), 
                    aGraphicalParticlePositionArrayList.getSize(), 
                    new ZAscendingComparator()
                );
                break;
            case XY_TOP:
                this.miscUtilityMethods.sortGenericArray(
                    aGraphicalParticlePositionArrayList.getGraphicalParticlePositions(), 
                    aGraphicalParticlePositionArrayList.getSize(), 
                    new ZDescendingComparator()
                );
                break;
            case YZ_LEFT:
                this.miscUtilityMethods.sortGenericArray(
                    aGraphicalParticlePositionArrayList.getGraphicalParticlePositions(), 
                    aGraphicalParticlePositionArrayList.getSize(), 
                    new XAscendingComparator()
                );
                break;
            case YZ_RIGHT:
                this.miscUtilityMethods.sortGenericArray(
                    aGraphicalParticlePositionArrayList.getGraphicalParticlePositions(), 
                    aGraphicalParticlePositionArrayList.getSize(), 
                    new XDescendingComparator()
                );
                break;
            default:
                return;
        }
    }

    /**
     * Creates subsets of particles which belong to the slices
     *
     * @param aBoxView Box view
     * @param aSortedGraphicalParticlePositionArrayList Sorted graphical particle position array list according to box view
     * @param aBoxSizeInfo Box size info
     * @return Slices
     *
     */
    public Slice[] createSlices(
        SimulationBoxViewEnum aBoxView, 
        GraphicalParticlePositionArrayList aSortedGraphicalParticlePositionArrayList, 
        BoxSizeInfo aBoxSizeInfo
    ) {
        // <editor-fold defaultstate="collapsed" desc="Local variables">
        int tmpIndex;
        double tmpSizePerSlice;
        double tmpExclusiveStartValue;
        boolean tmpIsAscending;
        int tmpSize = 0;
        GraphicalParticlePosition[] tmpSortedGraphicalParticlePositions = null;
        if (aSortedGraphicalParticlePositionArrayList != null && aSortedGraphicalParticlePositionArrayList.getSize() > 0) {
            tmpSize = aSortedGraphicalParticlePositionArrayList.getSize();
            tmpSortedGraphicalParticlePositions = aSortedGraphicalParticlePositionArrayList.getGraphicalParticlePositions();
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialize slices">
        switch (aBoxView) {
            case XZ_FRONT:
            case XZ_BACK:
                if (aSortedGraphicalParticlePositionArrayList == null || tmpSize == 0) {
                    tmpSizePerSlice = aBoxSizeInfo.getRotationDisplayFrameYLength() / (double) Preferences.getInstance().getNumberOfSlicesPerView();
                    if (aBoxView == SimulationBoxViewEnum.XZ_FRONT) {
                        tmpExclusiveStartValue = aBoxSizeInfo.getRotationDisplayFrameYMin();
                        tmpIsAscending = true;
                    } else {
                        tmpExclusiveStartValue = aBoxSizeInfo.getRotationDisplayFrameYMax();
                        tmpIsAscending = false;
                    }
                } else {
                    double tmpDelta = Math.abs(tmpSortedGraphicalParticlePositions[0].getY() - tmpSortedGraphicalParticlePositions[tmpSize - 1].getY());
                    tmpSizePerSlice = tmpDelta / (double) Preferences.getInstance().getNumberOfSlicesPerView();
                    if (aBoxView == SimulationBoxViewEnum.XZ_FRONT) {
                        tmpExclusiveStartValue = tmpSortedGraphicalParticlePositions[0].getY();
                        tmpIsAscending = true;
                    } else {
                        // XZ_BACK
                        tmpExclusiveStartValue = tmpSortedGraphicalParticlePositions[0].getY();
                        tmpIsAscending = false;
                    }
                }
                break;
            case XY_BOTTOM:
            case XY_TOP:
                if (aSortedGraphicalParticlePositionArrayList == null || tmpSize == 0) {
                    tmpSizePerSlice = aBoxSizeInfo.getRotationDisplayFrameZLength() / (double) Preferences.getInstance().getNumberOfSlicesPerView();
                    if (aBoxView == SimulationBoxViewEnum.XY_BOTTOM) {
                        tmpExclusiveStartValue = aBoxSizeInfo.getRotationDisplayFrameYMin();
                        tmpIsAscending = true;
                    } else {
                        // XY_TOP
                        tmpExclusiveStartValue = aBoxSizeInfo.getRotationDisplayFrameYMax();
                        tmpIsAscending = false;
                    }
                } else {
                    double tmpDelta = Math.abs(tmpSortedGraphicalParticlePositions[0].getZ() - tmpSortedGraphicalParticlePositions[tmpSize - 1].getZ());
                    tmpSizePerSlice = tmpDelta / (double) Preferences.getInstance().getNumberOfSlicesPerView();
                    if (aBoxView == SimulationBoxViewEnum.XY_BOTTOM) {
                        tmpExclusiveStartValue = tmpSortedGraphicalParticlePositions[0].getZ();
                        tmpIsAscending = true;
                    } else {
                        // XY_TOP
                        tmpExclusiveStartValue = tmpSortedGraphicalParticlePositions[0].getZ();
                        tmpIsAscending = false;
                    }
                }
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                if (aSortedGraphicalParticlePositionArrayList == null || tmpSize == 0) {
                    tmpSizePerSlice = aBoxSizeInfo.getRotationDisplayFrameXLength() / (double) Preferences.getInstance().getNumberOfSlicesPerView();
                    if (aBoxView == SimulationBoxViewEnum.YZ_LEFT) {
                        tmpExclusiveStartValue = aBoxSizeInfo.getRotationDisplayFrameXMin();
                        tmpIsAscending = true;
                    } else {
                        // YZ_RIGHT
                        tmpExclusiveStartValue = aBoxSizeInfo.getRotationDisplayFrameXMax();
                        tmpIsAscending = false;
                    }
                } else {
                    double tmpDelta = Math.abs(tmpSortedGraphicalParticlePositions[0].getX() - tmpSortedGraphicalParticlePositions[tmpSize - 1].getX());
                    tmpSizePerSlice = tmpDelta / (double) Preferences.getInstance().getNumberOfSlicesPerView();
                    if (aBoxView == SimulationBoxViewEnum.YZ_LEFT) {
                        tmpExclusiveStartValue = tmpSortedGraphicalParticlePositions[0].getX();
                        tmpIsAscending = true;
                    } else {
                        // YZ_RIGHT
                        tmpExclusiveStartValue = tmpSortedGraphicalParticlePositions[0].getX();
                        tmpIsAscending = false;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown simulation box view.");
        }
        // </editor-fold>
        Slice[] tmpSlices = new Slice[Preferences.getInstance().getNumberOfSlicesPerView()];
        if (tmpIsAscending) {
            // <editor-fold defaultstate="collapsed" desc="Set start and end values of slices">
            for (int i = 0; i < Preferences.getInstance().getNumberOfSlicesPerView(); i++) {
                // Initialize with impossible indices (-1)
                tmpSlices[i] = new Slice();
                tmpSlices[i].setStartValue(tmpExclusiveStartValue + (double) i * tmpSizePerSlice);
                tmpSlices[i].setEndValue(tmpExclusiveStartValue + (double) (i + 1) * tmpSizePerSlice);
            }
            // </editor-fold>
            if (aSortedGraphicalParticlePositionArrayList == null || tmpSize == 0) {
                return tmpSlices;
            }
            // <editor-fold defaultstate="collapsed" desc="Set start and end indices of slices">
            tmpIndex = 0;
            for (int i = 0; i < tmpSlices.length; i++) {
                if (i <= tmpSlices.length - 1) {
                    while (tmpIndex < tmpSize 
                            && (this.getEndValue(aBoxView, tmpSortedGraphicalParticlePositions[tmpIndex]) <= tmpSlices[i].getEndValue())) 
                    {
                        if (tmpSlices[i].getStartIndex() < 0) {
                            tmpSlices[i].setStartIndex(tmpIndex);
                        }
                        tmpSlices[i].setEndIndex(tmpIndex++);
                    }
                }
                if (tmpIndex == tmpSize) {
                    break;
                }
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Set start and end values of slices">
            for (int i = 0; i < Preferences.getInstance().getNumberOfSlicesPerView(); i++) {
                // Initialize with impossible indices (-1)
                tmpSlices[i] = new Slice();
                tmpSlices[i].setStartValue(tmpExclusiveStartValue - (double) i * tmpSizePerSlice);
                tmpSlices[i].setEndValue(tmpExclusiveStartValue - (double) (i + 1) * tmpSizePerSlice);
            }
            // </editor-fold>
            if (aSortedGraphicalParticlePositionArrayList == null || tmpSize == 0) {
                return tmpSlices;
            }
            // <editor-fold defaultstate="collapsed" desc="Set start and end indices of slices">
            tmpIndex = 0;
            for (int i = 0; i < tmpSlices.length; i++) {
                if (i <= tmpSlices.length - 1) {
                    while (tmpIndex < tmpSize 
                        && (this.getEndValue(aBoxView, tmpSortedGraphicalParticlePositions[tmpIndex]) >= tmpSlices[i].getEndValue())) 
                    {
                        if (tmpSlices[i].getStartIndex() < 0) {
                            tmpSlices[i].setStartIndex(tmpIndex);
                        }
                        tmpSlices[i].setEndIndex(tmpIndex++);
                    }
                }
                if (tmpIndex == tmpSize) {
                    break;
                }
            }
            // </editor-fold>
        }
        return tmpSlices;
    }

    /**
     * Sets colors buffer with gradient colors for slicer
     *
     * @param aColor Color
     * @param aColorsBuffer Colors buffer to be set
     */
    public void setGradientColors(Color aColor, Color[] aColorsBuffer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aColor == null || aColorsBuffer == null || aColorsBuffer.length != 3) {
            return;
        }

        // </editor-fold>
        aColorsBuffer[0] = this.getAttenuatedColor(Color.white, aColor, Preferences.getInstance().getSpecularWhiteAttenuationSlicer());
        aColorsBuffer[1] = aColor;
        aColorsBuffer[2] = this.getAttenuatedColor(aColor, Color.black, Preferences.getInstance().getColorGradientAttenuationSlicer());
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Returns end value of specified particle position according to specified
     * box view
     *
     * @param aBoxView Box view
     * @param aSortedGraphicalParticlePositions Sorted graphical particle positions according to box view
     * @return End value of specified particle position according to specified
     * box view
     */
    private double getEndValue(SimulationBoxViewEnum aBoxView, GraphicalParticlePosition aSortedGraphicalParticlePosition) {
        switch (aBoxView) {
            case XZ_FRONT:
            case XZ_BACK:
                return aSortedGraphicalParticlePosition.getY();
            case XY_BOTTOM:
            case XY_TOP:
                return aSortedGraphicalParticlePosition.getZ();
            case YZ_LEFT:
            case YZ_RIGHT:
                return aSortedGraphicalParticlePosition.getX();
            default:
                throw new IllegalArgumentException("Unknown simulation box view.");
        }
    }
    
    /**
     * Corrects value to interval [aMinValue, aMaxValue].
     * Note: This method may "run forever".
     * (No checks are performed)
     * 
     * @param aValue Value
     * @param aMinValue Minimum value
     * @param aMaxValue Maximum value
     * @param aLength Length = aMaxValue - aMinValue
     * @return Corrected value;
     */
    private double getCorrectedValue(double aValue, double aMinValue, double aMaxValue, double aLength) {
        double tmpValue = aValue;
        while (tmpValue < aMinValue) {
            tmpValue += aLength;
        }
        while (tmpValue > aMaxValue) {
            tmpValue -= aLength;
        }
        return tmpValue;
    }
    // </editor-fold>
    
}
