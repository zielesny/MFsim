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

import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionArrayList;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticle;
import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.preference.Preferences;
import java.awt.Color;
import de.gnwi.spices.IPointInSpace;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Info class for simulation box coordinates and size
 *
 * @author Achim Zielesny
 *
 */
public class BoxSizeInfo {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Dummy graphical particle for syntax consistency, i.e. all settings have no meaning!
     */
    private static final GraphicalParticle DUMMY_GRAPHICAL_PARTICLE = new GraphicalParticle("ParticleInFrame", "NameOfParticleInFrame", Color.BLACK, 0.0);
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility graphics methods
     */
    private GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * Minimum x-coordinate of simulation box
     */
    private double xMin;

    /**
     * Maximum x-coordinate of simulation box
     */
    private double xMax;

    /**
     * Minimum y-coordinate of simulation box
     */
    private double yMin;

    /**
     * Maximum y-coordinate of simulation box
     */
    private double yMax;

    /**
     * Minimum z-coordinate of simulation box
     */
    private double zMin;

    /**
     * Maximum z-coordinate of simulation box
     */
    private double zMax;

    /**
     * x-length of simulation box
     */
    private double xLength;

    /**
     * y-length of simulation box
     */
    private double yLength;

    /**
     * z-length of simulation box
     */
    private double zLength;

    /**
     * Half x-length of simulation box
     */
    private double halfXLength;

    /**
     * Half y-length of simulation box
     */
    private double halfYLength;

    /**
     * Half z-length of simulation box
     */
    private double halfZLength;

    /**
     * Volume of box
     */
    private double volume;

    /**
     * Space diagonal of box
     */
    private double spaceDiagonal;

    /**
     * Mid point of box
     */
    private PointInSpace boxMidPoint;

    /**
     * Minimum x-coordinate of rotation-display frame
     */
    private double rotationDisplayFrameXMin;

    /**
     * Maximum x-coordinate of rotation-display frame
     */
    private double rotationDisplayFrameXMax;

    /**
     * Minimum y-coordinate of rotation-display frame
     */
    private double rotationDisplayFrameYMin;

    /**
     * Maximum y-coordinate of rotation-display frame
     */
    private double rotationDisplayFrameYMax;

    /**
     * Minimum z-coordinate of rotation-display frame
     */
    private double rotationDisplayFrameZMin;

    /**
     * Maximum z-coordinate of rotation-display frame
     */
    private double rotationDisplayFrameZMax;

    /**
     * First point for definition of this box size info
     */
    private PointInSpace firstPoint;

    /**
     * Second point for definition of this box size info
     */
    private PointInSpace secondPoint;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aXMin Minimum x-coordinate of simulation box
     * @param aXMax Maximum x-coordinate of simulation box
     * @param aYMin Minimum y-coordinate of simulation box
     * @param aYMax Maximum y-coordinate of simulation box
     * @param aZMin Minimum z-coordinate of simulation box
     * @param aZMax Maximum z-coordinate of simulation box
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public BoxSizeInfo(double aXMin, double aXMax, double aYMin, double aYMax, double aZMin, double aZMax) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXMin >= aXMax) {
            throw new IllegalArgumentException("BoxSizeInfo Constructor: An argument is illegal.");
        }
        if (aYMin >= aYMax) {
            throw new IllegalArgumentException("BoxSizeInfo Constructor: An argument is illegal.");
        }
        if (aZMin >= aZMax) {
            throw new IllegalArgumentException("BoxSizeInfo Constructor: An argument is illegal.");
        }

        // </editor-fold>
        this.xMin = aXMin;
        this.xMax = aXMax;
        this.yMin = aYMin;
        this.yMax = aYMax;
        this.zMin = aZMin;
        this.zMax = aZMax;

        this.firstPoint = null;
        this.secondPoint = null;

        this.calculateProperties();
    }

    /**
     * Constructor
     *
     * @param aFirstPoint First point
     * @param aSecondPoint Second point
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public BoxSizeInfo(PointInSpace aFirstPoint, PointInSpace aSecondPoint) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFirstPoint == null) {
            throw new IllegalArgumentException("BoxSizeInfo Constructor: aFirstPoint is null.");
        }
        if (aSecondPoint == null) {
            throw new IllegalArgumentException("BoxSizeInfo Constructor: aSecondPoint is null.");
        }
        // </editor-fold>
        this.xMin = Math.min(aFirstPoint.getX(), aSecondPoint.getX());
        this.xMax = Math.max(aFirstPoint.getX(), aSecondPoint.getX());
        this.yMin = Math.min(aFirstPoint.getY(), aSecondPoint.getY());
        this.yMax = Math.max(aFirstPoint.getY(), aSecondPoint.getY());
        this.zMin = Math.min(aFirstPoint.getZ(), aSecondPoint.getZ());
        this.zMax = Math.max(aFirstPoint.getZ(), aSecondPoint.getZ());

        this.firstPoint = aFirstPoint;
        this.secondPoint = aSecondPoint;

        this.calculateProperties();
    }

    /**
     * Constructor
     *
     * @param aBoxSizeInfoDefinitionString Box size info definition string (as
     * obtained with method getBoxSizeInfoDefinitionString())
     * @throws IllegalArgumentException Thrown if aBoxSizeInfoDefinitionString
     * is null/empty.
     */
    public BoxSizeInfo(String aBoxSizeInfoDefinitionString) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBoxSizeInfoDefinitionString == null || aBoxSizeInfoDefinitionString.isEmpty()) {
            throw new IllegalArgumentException("BoxSizeInfo Constructor: aBoxSizeInfoDefinitionString is null/empty.");
        }

        // </editor-fold>
        String[] tmpBoxSizeValues = ModelDefinitions.GENERAL_SEPARATOR_PATTERN.split(aBoxSizeInfoDefinitionString);

        this.xMin = Double.valueOf(tmpBoxSizeValues[0]);
        this.xMax = Double.valueOf(tmpBoxSizeValues[1]);
        this.yMin = Double.valueOf(tmpBoxSizeValues[2]);
        this.yMax = Double.valueOf(tmpBoxSizeValues[3]);
        this.zMin = Double.valueOf(tmpBoxSizeValues[4]);
        this.zMax = Double.valueOf(tmpBoxSizeValues[5]);

        this.firstPoint = null;
        this.secondPoint = null;

        this.calculateProperties();
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns clone of this instance
     * 
     * @return Clone of this instance
     */
    public BoxSizeInfo getClone() {
        return new BoxSizeInfo(this.xMin, this.xMax, this.yMin, this.yMax, this.zMin, this.zMax);
    }

    /**
     * Returns box size info which is scaled by specified factor
     *
     * @param aScaleFactor Factor to scale box size
     * @return Scaled box size info
     */
    public BoxSizeInfo getScaledBoxSizeInfo(double aScaleFactor) {
        return new BoxSizeInfo(this.xMin * aScaleFactor, this.xMax * aScaleFactor, this.yMin * aScaleFactor, this.yMax * aScaleFactor, this.zMin * aScaleFactor, this.zMax * aScaleFactor);
    }

    /**
     * Enlarges box edges symmetrically with passed percentage value and returns
     * corresponding box size info. NOTE: A percentage value of -100 or less is
     * impossible.
     *
     * @param aPercentage Percentage value (positive: Enlargement, negative:
     * Reduction). Value is NOT allowed to be less than -99 (see code).
     * @return Enlarged box size info
     * @throws IllegalArgumentException Illegal argument exception if aPercentage is smaller than -99.
     */
    public BoxSizeInfo getEnlargedBoxSizeInfo(double aPercentage) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPercentage < -99) {
            throw new IllegalArgumentException("aPercentage is smaller than -99.");
        }
        // </editor-fold>
        double tmpXOffset = this.xLength * aPercentage / 100.0;
        double tmpYOffset = this.yLength * aPercentage / 100.0;
        double tmpZOffset = this.zLength * aPercentage / 100.0;
        return 
            new BoxSizeInfo(
                this.xMin - tmpXOffset / 2.0, 
                this.xMax + tmpXOffset / 2.0, 
                this.yMin - tmpYOffset / 2.0, 
                this.yMax + tmpYOffset / 2.0, 
                this.zMin - tmpZOffset / 2.0, 
                this.zMax + tmpZOffset / 2.0
            );
    }

    /**
     * Returns whether point is in box
     *
     * @param aPoint Point to be tested
     * @return True: Point is in box, false: Otherwise
     */
    public boolean isInBox(IPointInSpace aPoint) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint == null) {
            return false;
        }
        // </editor-fold>
        if (Preferences.getInstance().isBoxVolumeShapeForZoom()) {
            // Box volume shape
            return aPoint.getX() >= this.xMin
                && aPoint.getX() <= this.xMax
                && aPoint.getY() >= this.yMin
                && aPoint.getY() <= this.yMax
                && aPoint.getZ() >= this.zMin
                && aPoint.getZ() <= this.zMax;
        } else {
            // Ellipsoid volume shape
            double tmpTermX = (aPoint.getX() - this.boxMidPoint.getX()) / this.halfXLength;
            double tmpTermY = (aPoint.getY() - this.boxMidPoint.getY()) / this.halfYLength;
            double tmpTermZ = (aPoint.getZ() - this.boxMidPoint.getZ()) / this.halfZLength;
            return tmpTermX * tmpTermX + tmpTermY * tmpTermY + tmpTermZ * tmpTermZ <= 1.0;
        }
    }

    /**
     * Adds graphical particle positions of frame
     * 
     * @param aGraphicalParticlePositionArrayList GraphicalParticlePositionArrayList instance to add
     *
     */
    public void addGraphicalParticlePositionsOfFrame(GraphicalParticlePositionArrayList aGraphicalParticlePositionArrayList) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aGraphicalParticlePositionArrayList == null) {
            return;
        }
        if (Preferences.getInstance().getNumberOfFramePointsSlicer() < 2) {
            return;
        }
        // </editor-fold>
        int tmpNumberOfPoints = Preferences.getInstance().getNumberOfFramePointsSlicer();
        // There are 12 edges with tmpNumberOfPoints each
        aGraphicalParticlePositionArrayList.setGrowthIncrement(tmpNumberOfPoints * 12);
        // Point (X1, Y1, Z1) to (X2, Y1, Z1)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMin, this.yMin, this.zMin), new PointInSpace(this.xMax, this.yMin, this.zMin), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X1, Y1, Z1) to (X1, Y1, Z2)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMin, this.yMin, this.zMin), new PointInSpace(this.xMin, this.yMin, this.zMax), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X2, Y1, Z1) to (X2, Y1, Z2)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMax, this.yMin, this.zMin), new PointInSpace(this.xMax, this.yMin, this.zMax), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X1, Y1, Z2) to (X2, Y1, Z2)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMin, this.yMin, this.zMax), new PointInSpace(this.xMax, this.yMin, this.zMax), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X1, Y2, Z1) to (X2, Y2, Z1)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMin, this.yMax, this.zMin), new PointInSpace(this.xMax, this.yMax, this.zMin), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X1, Y2, Z1) to (X1, Y2, Z2)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMin, this.yMax, this.zMin), new PointInSpace(this.xMin, this.yMax, this.zMax), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X2, Y2, Z1) to (X2, Y2, Z2)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMax, this.yMax, this.zMin), new PointInSpace(this.xMax, this.yMax, this.zMax), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X1, Y2, Z2) to (X2, Y2, Z2)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMin, this.yMax, this.zMax), new PointInSpace(this.xMax, this.yMax, this.zMax), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X1, Y1, Z1) to (X1, Y2, Z1)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMin, this.yMin, this.zMin), new PointInSpace(this.xMin, this.yMax, this.zMin), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X1, Y1, Z2) to (X1, Y2, Z2)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMin, this.yMin, this.zMax), new PointInSpace(this.xMin, this.yMax, this.zMax), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X2, Y1, Z1) to (X2, Y2, Z1)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMax, this.yMin, this.zMin), new PointInSpace(this.xMax, this.yMax, this.zMin), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
        // Point (X2, Y1, Z2) to (X2, Y2, Z2)
        this.graphicsUtilityMethods.addGraphicalParticlePositionsInSpaceAlongStraightLineOfFrameEdge(
            new PointInSpace(this.xMax, this.yMin, this.zMax), new PointInSpace(this.xMax, this.yMax, this.zMax), tmpNumberOfPoints, DUMMY_GRAPHICAL_PARTICLE, aGraphicalParticlePositionArrayList);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    // <editor-fold defaultstate="collapsed" desc="- Rotation-display frame related properties">
    /**
     * Minimum x-coordinate of rotation-display frame
     *
     * @return Minimum x-coordinate of rotation-display frame
     */
    public double getRotationDisplayFrameXMin() {
        return this.rotationDisplayFrameXMin;
    }

    /**
     * Maximum x-coordinate of rotation-display frame
     *
     * @return Maximum x-coordinate of rotation-display frame
     */
    public double getRotationDisplayFrameXMax() {
        return this.rotationDisplayFrameXMax;
    }

    /**
     * Minimum y-coordinate of rotation-display frame
     *
     * @return Minimum y-coordinate of rotation-display frame
     */
    public double getRotationDisplayFrameYMin() {
        return this.rotationDisplayFrameYMin;
    }

    /**
     * Maximum y-coordinate of rotation-display frame
     *
     * @return Maximum y-coordinate of rotation-display frame
     */
    public double getRotationDisplayFrameYMax() {
        return this.rotationDisplayFrameYMax;
    }

    /**
     * Minimum z-coordinate of rotation-display frame
     *
     * @return Minimum z-coordinate of rotation-display frame
     */
    public double getRotationDisplayFrameZMin() {
        return this.rotationDisplayFrameZMin;
    }

    /**
     * Maximum z-coordinate of rotation-display frame
     *
     * @return Maximum z-coordinate of rotation-display frame
     */
    public double getRotationDisplayFrameZMax() {
        return this.rotationDisplayFrameZMax;
    }

    /**
     * x-length of rotation-display frame
     *
     * @return x-length of rotation-display frame
     */
    public double getRotationDisplayFrameXLength() {
        return this.spaceDiagonal;
    }

    /**
     * y-length of rotation-display frame
     *
     * @return y-length of rotation-display frame
     */
    public double getRotationDisplayFrameYLength() {
        return this.spaceDiagonal;
    }

    /**
     * z-length of rotation-display frame
     *
     * @return z-length of rotation-display frame
     */
    public double getRotationDisplayFrameZLength() {
        return this.spaceDiagonal;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Other properties">
    /**
     * x-length of simulation box
     *
     * @return x-length of simulation box
     */
    public double getXLength() {
        return this.xLength;
    }

    /**
     * y-length of simulation box
     *
     * @return y-length of simulation box
     */
    public double getYLength() {
        return this.yLength;
    }

    /**
     * z-length of simulation box
     *
     * @return z-length of simulation box
     */
    public double getZLength() {
        return this.zLength;
    }

    /**
     * Returns volume of box
     *
     * @return Volume of box
     */
    public double getVolume() {
        return this.volume;
    }

    /**
     * Returns space diagonal of box
     *
     * @return Space diagonal of box
     */
    public double getSpaceDiagonal() {
        return this.spaceDiagonal;
    }

    /**
     * Returns box mid point
     *
     * @return Box mid point
     */
    public PointInSpace getBoxMidPoint() {
        return this.boxMidPoint;
    }

    /**
     * Returns array with edge points of box. The following points should be
     * connected for box display (indices): 0-1, 0-2, 0-3, 1-4, 1-6, 2-4, 2-5,
     * 3-5, 3-6, 4-7, 5-7, 6-7
     *
     * @return Array with edge points of box
     */
    public PointInSpace[] getBoxEdgePointArray() {
        PointInSpace[] tmpBoxEdgePointArray = new PointInSpace[8];
        tmpBoxEdgePointArray[0] = new PointInSpace(this.xMin, this.yMin, this.zMin);
        tmpBoxEdgePointArray[1] = new PointInSpace(this.xMax, this.yMin, this.zMin);
        tmpBoxEdgePointArray[2] = new PointInSpace(this.xMin, this.yMax, this.zMin);
        tmpBoxEdgePointArray[3] = new PointInSpace(this.xMin, this.yMin, this.zMax);
        tmpBoxEdgePointArray[4] = new PointInSpace(this.xMax, this.yMax, this.zMin);
        tmpBoxEdgePointArray[5] = new PointInSpace(this.xMin, this.yMax, this.zMax);
        tmpBoxEdgePointArray[6] = new PointInSpace(this.xMax, this.yMin, this.zMax);
        tmpBoxEdgePointArray[7] = new PointInSpace(this.xMax, this.yMax, this.zMax);
        return tmpBoxEdgePointArray;
    }

    /**
     * Box size info definition string: This string may be used to instantiate a
     * box size info with the same dimensions (see constructor).
     *
     * @return Box size info definition string
     */
    public String getBoxSizeInfoDefinitionString() {
        // Definition string just contains six double value numbers: 200 characters are sufficient!
        StringBuffer tmpBuffer = new StringBuffer(200);

        tmpBuffer.append(String.valueOf(this.xMin));
        tmpBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);
        tmpBuffer.append(String.valueOf(this.xMax));
        tmpBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);

        tmpBuffer.append(String.valueOf(this.yMin));
        tmpBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);
        tmpBuffer.append(String.valueOf(this.yMax));
        tmpBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);

        tmpBuffer.append(String.valueOf(this.zMin));
        tmpBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);
        tmpBuffer.append(String.valueOf(this.zMax));

        return tmpBuffer.toString();
    }

    /**
     * Returns first point for definition of this box size info
     *
     * @return First point for definition of this box size info or null if box
     * size info was not defined by two points
     */
    public PointInSpace getFirstPoint() {
        return this.firstPoint;
    }

    /**
     * Returns second point for definition of this box size info
     *
     * @return Second point for definition of this box size info or null if box
     * size info was not defined by two points
     */
    public PointInSpace getSecondPoint() {
        return this.secondPoint;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">

    /**
     * Minimum x-coordinate of simulation box
     *
     * @return Minimum x-coordinate of simulation box
     */
    public double getXMin() {
        return this.xMin;
    }

    /**
     * Minimum x-coordinate of simulation box
     *
     * @param aValue Value
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setXMin(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue >= this.xMax) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        this.xMin = aValue;
        this.calculateProperties();
    }

    /**
     * Maximum x-coordinate of simulation box
     *
     * @return Maximum x-coordinate of simulation box
     */
    public double getXMax() {
        return this.xMax;
    }

    /**
     * Maximum x-coordinate of simulation box
     *
     * @param aValue Value
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setXMax(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue <= this.xMin) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        this.xMax = aValue;
        this.calculateProperties();
    }

    /**
     * Minimum y-coordinate of simulation box
     *
     * @return Minimum y-coordinate of simulation box
     */
    public double getYMin() {
        return this.yMin;
    }

    /**
     * Minimum y-coordinate of simulation box
     *
     * @param aValue Value
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setYMin(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue >= this.yMax) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        this.yMin = aValue;
        this.calculateProperties();
    }

    /**
     * Maximum y-coordinate of simulation box
     *
     * @return Maximum y-coordinate of simulation box
     */
    public double getYMax() {
        return this.yMax;
    }

    /**
     * Maximum y-coordinate of simulation box
     *
     * @param aValue Value
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setYMax(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue <= this.yMin) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        this.yMax = aValue;
        this.calculateProperties();
    }

    /**
     * Minimum z-coordinate of simulation box
     *
     * @return Minimum z-coordinate of simulation box
     */
    public double getZMin() {
        return this.zMin;
    }

    /**
     * Minimum z-coordinate of simulation box
     *
     * @param aValue Value
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setZMin(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue >= this.zMax) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        this.zMin = aValue;
        this.calculateProperties();
    }

    /**
     * Maximum z-coordinate of simulation box
     *
     * @return Maximum z-coordinate of simulation box
     */
    public double getZMax() {
        return this.zMax;
    }

    /**
     * Maximum z-coordinate of simulation box
     *
     * @param aValue Value
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setZMax(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue <= this.zMin) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        this.zMax = aValue;
        this.calculateProperties();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Calculates properties: Volume, space diagonal and mid point
     */
    private void calculateProperties() {
        this.xLength = this.xMax - this.xMin;
        this.yLength = this.yMax - this.yMin;
        this.zLength = this.zMax - this.zMin;

        this.halfXLength = 0.5 * this.xLength;
        this.halfYLength = 0.5 * this.yLength;
        this.halfZLength = 0.5 * this.zLength;

        this.volume = this.xLength * this.yLength * this.zLength;
        this.spaceDiagonal = Math.sqrt(this.xLength * this.xLength + this.yLength * this.yLength + this.zLength * this.zLength);

        this.boxMidPoint = new PointInSpace(this.xMin + this.xLength / 2.0, this.yMin + this.yLength / 2.0, this.zMin + this.zLength / 2.0);

        this.rotationDisplayFrameXMin = this.boxMidPoint.getX() - this.spaceDiagonal / 2.0;
        this.rotationDisplayFrameXMax = this.boxMidPoint.getX() + this.spaceDiagonal / 2.0;
        this.rotationDisplayFrameYMin = this.boxMidPoint.getY() - this.spaceDiagonal / 2.0;
        this.rotationDisplayFrameYMax = this.boxMidPoint.getY() + this.spaceDiagonal / 2.0;
        this.rotationDisplayFrameZMin = this.boxMidPoint.getZ() - this.spaceDiagonal / 2.0;
        this.rotationDisplayFrameZMax = this.boxMidPoint.getZ() + this.spaceDiagonal / 2.0;
    }
    // </editor-fold>

}
