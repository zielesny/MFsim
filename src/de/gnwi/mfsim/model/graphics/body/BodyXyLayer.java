/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2022  Achim Zielesny (achim.zielesny@googlemail.com)
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
package de.gnwi.mfsim.model.graphics.body;

import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentBox;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.shape.ShapeBox;
import de.gnwi.mfsim.model.graphics.shape.ShapeInterface;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import java.util.LinkedList;
import de.gnwi.spices.IPointInSpace;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.jdpd.interfaces.IRandom;

/**
 * This class represents a xy-layer. The xy-layer is defined by center and and
 * its side lengths x, y, z.
 *
 * @author Achim Zielesny
 */
public class BodyXyLayer extends ChangeNotifier implements BodyInterface, ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Center of the body
     */
    private final PointInSpace bodyCenter;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility graphics methods
     */
    private GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * Position change
     */
    private ChangeInformation positionChangeInformation;

    /**
     * Deselect body change
     */
    private ChangeInformation deselectBodyChangeInformation;

    /**
     * Select body change
     */
    private ChangeInformation selectBodyChangeInformation;

    /**
     * Position change
     */
    private ChangeInformation sizeChangeInformation;

    /**
     * Position change
     */
    private ChangeInformation boxViewChangeInformation;

    /**
     * Box view
     */
    private SimulationBoxViewEnum boxView;

    /**
     * Geometric form of the body
     */
    private BodyTypeEnum bodyType;

    /**
     * True: Body is selected, false: Otherwise
     */
    private boolean isSelected;

    /**
     * xy-layer shape
     */
    private ShapeBox xyLayerShape;

    /**
     * Simulation box
     */
    private CompartmentBox compartmentBox;

    /**
     * Value item
     */
    private ValueItem valueItem;

    /**
     * X-length of xy-layer
     */
    private double xLength;

    /**
     * Y-length of xy-layer
     */
    private double yLength;

    /**
     * Z-length of xy-layer
     */
    private double zLength;

    /**
     * Half x-length of xy-layer
     */
    private double halfXLength;

    /**
     * Half y-length of xy-layer
     */
    private double halfYLength;

    /**
     * Half z-length of xy-layer
     */
    private double halfZLength;

    /**
     * Corrected half x-length of xy-layer (to avoid errors due to roundoff
     * problems in comparisons)
     */
    private double halfXLengthCorrected;

    /**
     * Corrected half y-length of xy-layer (to avoid errors due to roundoff
     * problems in comparisons)
     */
    private double halfYLengthCorrected;

    /**
     * Corrected half z-length of xy-layer (to avoid errors due to roundoff
     * problems in comparisons)
     */
    private double halfZLengthCorrected;

    /**
     * Volume of the body
     */
    private double volume;

    /**
     * List with excluded spheres
     */
    private LinkedList<BodySphere> excludedSphereList;
    
    /**
     * Auxiliary class variables
     */
    private double bodyCenterX_Minus_halfXLengthCorrected;
    private double bodyCenterX_Plus_halfXLengthCorrected;
    private double bodyCenterY_Minus_halfYLengthCorrected;
    private double bodyCenterY_Plus_halfYLengthCorrected;
    private double bodyCenterZ_Minus_halfZLengthCorrected;
    private double bodyCenterZ_Plus_halfZLengthCorrected;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     *
     * @param aXLength X-Length of xy-layer
     * @param aYLength Y-Length of xy-layer
     * @param aZLength Z-Length of xy-layer
     * @param aBodyCenter Center of the body
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public BodyXyLayer(double aXLength, double aYLength, double aZLength, PointInSpace aBodyCenter) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXLength <= 0) {
            throw new IllegalArgumentException("aXLength was negative or 0.");
        }
        if (aYLength <= 0) {
            throw new IllegalArgumentException("aYLength was negative or 0.");
        }
        if (aZLength <= 0) {
            throw new IllegalArgumentException("aZLength was negative or 0.");
        }
        if (aBodyCenter == null) {
            throw new IllegalArgumentException("aBodyCenter was null.");
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set geometry">
        this.xLength = aXLength;
        this.yLength = aYLength;
        this.zLength = aZLength;
        this.bodyCenter = aBodyCenter;
        // calculateSizeRelatedQuantities() MUST be called after body center is set
        this.calculateSizeRelatedQuantities();
        // </editor-fold>
        this.initialize();
    }

    /**
     * Constructor
     *
     * @param aXLength X-Length of xy-layer
     * @param aYLength Y-Length of xy-layer
     * @param aZLength Z-Length of xy-layer
     * @param aXCoordinateOfCenter X coordinate of the center of mass point
     * @param aYCoordinateOfCenter Y coordinate of the center of mass point
     * @param aZCoordinateOfCenter Z coordinate of the center of mass point
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public BodyXyLayer(double aXLength, double aYLength, double aZLength, double aXCoordinateOfCenter, double aYCoordinateOfCenter, double aZCoordinateOfCenter) throws IllegalArgumentException {
        this(aXLength, aYLength, aZLength, new PointInSpace(aXCoordinateOfCenter, aYCoordinateOfCenter, aZCoordinateOfCenter));
    }

    /**
     * Constructor
     *
     * @param aXyLayerGeometryDataValueItem xy-layer geometry data value item
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public BodyXyLayer(ValueItem aXyLayerGeometryDataValueItem) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXyLayerGeometryDataValueItem == null) {
            throw new IllegalArgumentException("aXyLayerGeometryDataValueItem is null.");
        }
        if (!this.graphicsUtilityMethods.isXyLayerGeometryDataValueItem(aXyLayerGeometryDataValueItem)) {
            throw new IllegalArgumentException("aSphereValueItem is illegal.");
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set geometry">
        this.bodyCenter = new PointInSpace(aXyLayerGeometryDataValueItem.getValueAsDouble(0, 0), aXyLayerGeometryDataValueItem.getValueAsDouble(0, 1), aXyLayerGeometryDataValueItem.getValueAsDouble(
                0, 2));
        this.xLength = aXyLayerGeometryDataValueItem.getValueAsDouble(0, 3);
        this.yLength = aXyLayerGeometryDataValueItem.getValueAsDouble(0, 4);
        this.zLength = aXyLayerGeometryDataValueItem.getValueAsDouble(0, 5);
        // calculateSizeRelatedQuantities() MUST be called after body center is set
        this.calculateSizeRelatedQuantities();
        // </editor-fold>
        this.initialize();
        // <editor-fold defaultstate="collapsed" desc="Add this instance as change receiver">
        this.valueItem = aXyLayerGeometryDataValueItem;
        this.valueItem.addChangeReceiver(this);

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifiyChange() method">
    /**
     * Notifies about a change
     *
     * @param aChangeNotifier Object that notifies change
     * @param aChangeInfo Change information
     */
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        if (aChangeNotifier == this.valueItem) {
            // <editor-fold defaultstate="collapsed" desc="Position/size change">
            if (aChangeInfo.getChangeType() == ChangeTypeEnum.VALUE_ITEM_VALUE_CHANGE || aChangeInfo.getChangeType() == ChangeTypeEnum.VALUE_ITEM_MATRIX_CHANGE) {
                boolean tmpIsPositionChange = false;
                boolean tmpIsSizeChange = false;
                if (this.bodyCenter.getX() != this.valueItem.getValueAsDouble(0, 0)) {
                    this.bodyCenter.setX(this.valueItem.getValueAsDouble(0, 0));
                    tmpIsPositionChange = true;
                }
                if (this.bodyCenter.getY() != this.valueItem.getValueAsDouble(0, 1)) {
                    this.bodyCenter.setY(this.valueItem.getValueAsDouble(0, 1));
                    tmpIsPositionChange = true;
                }
                if (this.bodyCenter.getZ() != this.valueItem.getValueAsDouble(0, 2)) {
                    this.bodyCenter.setZ(this.valueItem.getValueAsDouble(0, 2));
                    tmpIsPositionChange = true;
                }
                if (this.xLength != this.valueItem.getValueAsDouble(0, 3)) {
                    this.xLength = this.valueItem.getValueAsDouble(0, 3);
                    this.calculateXyLayerProperties();
                    tmpIsSizeChange = true;
                }
                if (this.yLength != this.valueItem.getValueAsDouble(0, 4)) {
                    this.yLength = this.valueItem.getValueAsDouble(0, 4);
                    this.calculateXyLayerProperties();
                    tmpIsSizeChange = true;
                }
                if (this.zLength != this.valueItem.getValueAsDouble(0, 5)) {
                    this.zLength = this.valueItem.getValueAsDouble(0, 5);
                    this.calculateXyLayerProperties();
                    tmpIsSizeChange = true;
                }
                if (tmpIsPositionChange) {
                    super.notifyChangeReceiver(this, this.positionChangeInformation);
                }
                if (tmpIsSizeChange) {
                    // calculateSizeRelatedQuantities() MUST be called after body center is set
                    this.calculateSizeRelatedQuantities();
                    super.notifyChangeReceiver(this, this.sizeChangeInformation);
                }
                this.updateShape();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Selection change">
            if (aChangeInfo.getChangeType() == ChangeTypeEnum.VALUE_ITEM_SELECTED_CHANGE || aChangeInfo.getChangeType() == ChangeTypeEnum.VALUE_ITEM_DESELECTED_CHANGE) {
                this.isSelected = this.valueItem.isSelected();
                if (this.isSelected) {
                    super.notifyChangeReceiver(this, this.selectBodyChangeInformation);
                } else {
                    super.notifyChangeReceiver(this, this.deselectBodyChangeInformation);
                }
            }

            // </editor-fold>
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public compareTo() method">
    /**
     * CompareTo method: Compares position of body from "front" to "back" in
     * specified view
     *
     * @param aBody Body to compare
     * @return Standard compareTo result
     */
    public int compareTo(BodyInterface aBody) {
        switch (this.boxView) {
            case XZ_FRONT:
                return Double.compare(this.bodyCenter.getY(), aBody.getBodyCenter().getY());
            case XZ_BACK:
                return -Double.compare(this.bodyCenter.getY(), aBody.getBodyCenter().getY());
            case YZ_LEFT:
                return Double.compare(this.bodyCenter.getX(), aBody.getBodyCenter().getX());
            case YZ_RIGHT:
                return -Double.compare(this.bodyCenter.getX(), aBody.getBodyCenter().getX());
            case XY_TOP:
                return -Double.compare(this.bodyCenter.getZ(), aBody.getBodyCenter().getZ());
            case XY_BOTTOM:
                return Double.compare(this.bodyCenter.getZ(), aBody.getBodyCenter().getZ());
            default:
                // This should never happen: Return that bodies are equal.
                return 0;
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Geometry related methods">
    /**
     * Returns if a point in space is inside a body.
     * NOTE: ModelDefinitions.OFFSET_FOR_COMPARISON_OF_NUMBERS is used in corrected
 quantities to avoid errors due to roundoff problems.
     *
     * @param aPoint Point which is tested
     * @return True if point is inside body, false if point is on surface or
     * outside of body
     */
    @Override
    public boolean isInVolume(IPointInSpace aPoint) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint == null) {
            return false;
        }
        // </editor-fold>
        return this.bodyCenterX_Minus_halfXLengthCorrected <= aPoint.getX()
            && this.bodyCenterX_Plus_halfXLengthCorrected >= aPoint.getX()
            && this.bodyCenterY_Minus_halfYLengthCorrected <= aPoint.getY()
            && this.bodyCenterY_Plus_halfYLengthCorrected >= aPoint.getY()
            && this.bodyCenterZ_Minus_halfZLengthCorrected <= aPoint.getZ()
            && this.bodyCenterZ_Plus_halfZLengthCorrected >= aPoint.getZ();
        // Old code:
        // return this.bodyCenter.getX() - this.halfXLengthCorrected <= aPoint.getX()
        //         && this.bodyCenter.getX() + this.halfXLengthCorrected >= aPoint.getX()
        //         && this.bodyCenter.getY() - this.halfYLengthCorrected <= aPoint.getY()
        //         && this.bodyCenter.getY() + this.halfYLengthCorrected >= aPoint.getY()
        //         && this.bodyCenter.getZ() - this.halfZLengthCorrected <= aPoint.getZ()
        //         && this.bodyCenter.getZ() + this.halfZLengthCorrected >= aPoint.getZ();
    }

    /**
     * Returns if a body overlaps with this instance
     *
     * @param aBody aBody
     * @return True: Body overlaps with this instance, false: Otherwise
     */
    @Override
    public boolean isOverlap(BodyInterface aBody) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBody == null) {
            return false;
        }
        // </editor-fold>
        switch (aBody.getBodyType()) {
            case SPHERE:
                return this.graphicsUtilityMethods.isSphereXyLayerOverlap((BodySphere) aBody, this);
            case XY_LAYER:
                return this.graphicsUtilityMethods.isXyLayerXyLayerOverlap((BodyXyLayer) aBody, this);
            default:
                throw new IllegalArgumentException("Unknown body.");
        }

    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Random points related methods">
    /**
     * Returns a random point inside the body.
     *
     * @param aRandomNumberGenerator Random number generator
     * @return Point in space inside of the body
     */
    @Override
    public PointInSpace getRandomPointInVolume(IRandom aRandomNumberGenerator) {
        return this.getRandomPointsInVolume(1, aRandomNumberGenerator)[0];
    }

    /**
     * Returns aNumber random points inside the body.
     *
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @return Points inside the body volume
     * @throws IllegalArgumentException Thrown if aNumber is less than 1
     */
    @Override
    public PointInSpace[] getRandomPointsInVolume(int aNumber, IRandom aRandomNumberGenerator) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber is less than 1.");
        }

        // </editor-fold>
        PointInSpace tmpPointsInVolume[] = new PointInSpace[aNumber];
        this.fillRandomPointsInVolume(tmpPointsInVolume, 0, aNumber, aRandomNumberGenerator);
        return tmpPointsInVolume;
    }

    /**
     * Fills aNumber random points inside the body into buffer.
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    @Override
    public void fillRandomPointsInVolume(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
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

        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsInXyLayer(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.xLength, this.yLength, this.zLength, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points inside the body into buffers.
     *
     * @param aBuffer1 Buffer 1
     * @param aBuffer2 Buffer 2
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points for both buffers (not allowed to
     * @param aRandomNumberGenerator Random number generator
     * be less than 1)
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    @Override
    public void fillRandomPointsInVolume(IPointInSpace[] aBuffer1, IPointInSpace[] aBuffer2, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
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
        if ((aBuffer1 instanceof GraphicalParticlePosition[]) && !(aBuffer2 instanceof GraphicalParticlePosition[])) {
            throw new IllegalArgumentException("Both buffers must be instance of the same class.");
        }
        if ((aBuffer2 instanceof GraphicalParticlePosition[]) && !(aBuffer1 instanceof GraphicalParticlePosition[])) {
            throw new IllegalArgumentException("Both buffers must be instance of the same class.");
        }

        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsInXyLayer(aBuffer1, aBuffer2, aFirstIndex, aNumber, this.bodyCenter, this.xLength, this.yLength, this.zLength, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points inside the body into buffer. NOTE: The volume
     * of spheres within the xy-layer is excluded for the random points in
     * aBuffer. NOTE: If number of trials is NOT sufficient random points may be
     * located within the excluded volume of the spheres.
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points for both buffers (not allowed to
     * be less than 1)
     * @param aNumberOfTrials Number of trials for random point generation
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    @Override
    public void fillRandomPointsInVolumeWithExcludingSpheres(
        IPointInSpace[] aBuffer, 
        int aFirstIndex, 
        int aNumber, 
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
        if (aNumberOfTrials < 1) {
            throw new IllegalArgumentException("aNumberOfTrials is less than 1.");
        }
        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsInXyLayerWithExcludingSpheres(
            aBuffer, 
            aFirstIndex, 
            aNumber, 
            this.bodyCenter, 
            this.xLength, 
            this.yLength, 
            this.zLength, 
            this.excludedSphereList, 
            aNumberOfTrials,
            aRandomNumberGenerator
        );
    }

    /**
     * Fills aNumber random points inside the body into buffers. NOTE: The volume
     * of spheres within the xy-layer is excluded NOT just for the random points
     * in aBuffer1 and aBuffer2 but for all points along the straight line
     * between aBuffer1[i] and aBuffer2[i] with the specified step distance.
     * NOTE: If number of trials is NOT sufficient random points may be located
     * within the excluded volume of the spheres.
     *
     * @param aBuffer1 Buffer 1
     * @param aBuffer2 Buffer 2
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points for both buffers (not allowed to
     * be less than 1)
     * @param aStepDistance Step distance for points along the straight lines
     * between aBuffer1[i] and aBuffer2[i]
     * @param aNumberOfTrials Number of trials for random point generation
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    @Override
    public void fillRandomPointsInVolumeWithExcludingSpheres(
        IPointInSpace[] aBuffer1, 
        IPointInSpace[] aBuffer2, 
        int aFirstIndex, 
        int aNumber, 
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
        if (aBuffer1.length < aFirstIndex + aNumber) {
            throw new IllegalArgumentException("aBuffer1 is too small.");
        }
        if (aBuffer2 == null || aBuffer2.length == 0) {
            throw new IllegalArgumentException("aBuffer2 is null/empty.");
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
        if (aStepDistance <= 0.0) {
            throw new IllegalArgumentException("aDistance is less/equal 0.");
        }
        if (aNumberOfTrials < 1) {
            throw new IllegalArgumentException("aNumberOfTrials is less than 1.");
        }
        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsInXyLayerWithExcludingSpheres(
            aBuffer1, 
            aBuffer2, 
            aFirstIndex, 
            aNumber, 
            this.bodyCenter, 
            this.xLength, 
            this.yLength, 
            this.zLength, 
            this.excludedSphereList,
            aStepDistance, 
            aNumberOfTrials,
            aRandomNumberGenerator
        );
    }

    /**
     * Sets simple cubic lattice points inside the body into buffer.
     *
     * @param aBuffer Buffer
     * @param aTargetBondLength Target bond length
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void getSimpleCubicLatticePointsInBuffer(IPointInSpace[] aBuffer, double aTargetBondLength) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBuffer == null || aBuffer.length == 0) {
            throw new IllegalArgumentException("aBuffer is null/empty.");
        }

        // </editor-fold>
        double tmpStartHeightZ = this.bodyCenter.getZ() - this.zLength / 2.0;
        this.graphicsUtilityMethods.fillSimpleCubicLatticePositions(aBuffer, tmpStartHeightZ, aTargetBondLength, this.xLength, this.yLength, this.zLength);
    }

    /**
     * Returns a random point on the surface of body
     *
     * @param aRandomNumberGenerator Random number generator
     * @return Point on surface of body
     */
    @Override
    public PointInSpace getRandomPointOnSurface(IRandom aRandomNumberGenerator) {
        return this.getRandomPointsOnSurface(1, aRandomNumberGenerator)[0];
    }

    /**
     * Returns aNumber random points on surface of the body
     *
     * @param aNumber Number of random points which have to be created, not
     * allowed to be less than 1
     * @param aRandomNumberGenerator Random number generator
     * @return Points on surface of the body as array, array.length() = aNumber
     * @throws IllegalArgumentException if aNumber is less than 1
     */
    @Override
    public PointInSpace[] getRandomPointsOnSurface(int aNumber, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumber < 1) {
            throw new IllegalArgumentException("aNumber was less than 1.");
        }

        // </editor-fold>
        PointInSpace tmpPointsOnSurface[] = new PointInSpace[aNumber];
        this.fillRandomPointsOnSurface(tmpPointsOnSurface, 0, aNumber, aRandomNumberGenerator);
        return tmpPointsOnSurface;
    }

    /**
     * Fills aNumber random points on the surface of the body into buffer
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    @Override
    public void fillRandomPointsOnSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
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

        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsOnXyLayerTopBottomSurface(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.xLength, this.yLength, this.zLength, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points on the top and bottom surface of the body into buffer
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnTopBottomSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
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
        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsOnXyLayerTopBottomSurface(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.xLength, this.yLength, this.zLength, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points on the left and right surface of the body into buffer
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnLeftRightSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
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
        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsOnXyLayerLeftRightSurface(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.xLength, this.yLength, this.zLength, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points on the front and back surface of the body into buffer
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnFrontBackSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
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
        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsOnXyLayerFrontBackSurface(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.xLength, this.yLength, this.zLength, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points on single surface of the body into buffer
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aSingleSurface Single surface of xy-layer
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnSingleSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, BodyXyLayerSingleSurfaceEnum aSingleSurface, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
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

        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsOnSingleXyLayerSurface(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.xLength, this.yLength, this.zLength, aSingleSurface, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points on all surfaces of the body into buffer
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillRandomPointsOnAllSurfaces(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator) throws IllegalArgumentException {
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

        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsOnAllXyLayerSurfaces(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.xLength, this.yLength, this.zLength, aRandomNumberGenerator);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Random spheres related methods">
    /**
     * Returns list with specified number of non-overlapping spheres at random
     * positions that do NOT overlap with excluded spheres.
     * NOTE: Returned spheres are added to this.excludedSphereList
     * NOTE: If non-overlapping spheres could not be created with the
     * specified number of trials possibly overlapping spheres are created.
     * NOTE: The specified radius of spheres may be decreased if necessary.
     *
     * @param aNumberOfSpheres Number of spheres
     * @param aRadius Radius of spheres
     * @param aNumberOfTrials Number of trials for sphere generation
     * @param aRandomNumberGenerator Random number generator
     * @return Linked list with non-overlapping spheres or null if none could be 
     * created
     */
    public LinkedList<BodySphere> getNonOverlappingRandomSpheres(
        int aNumberOfSpheres, 
        double aRadius, 
        int aNumberOfTrials,
        IRandom aRandomNumberGenerator
    ) {
        LinkedList<BodySphere> tmpSphereList = 
            this.graphicsUtilityMethods.getNonOverlappingRandomSpheresInXyLayer(
                this.excludedSphereList, 
                this.bodyCenter, 
                this.xLength, 
                this.yLength, 
                this.zLength, 
                aNumberOfSpheres, 
                aRadius, 
                aNumberOfTrials,
                aRandomNumberGenerator
            );
        if (tmpSphereList != null && !tmpSphereList.isEmpty()) {
            for (BodySphere tmpSphere : tmpSphereList) {
                this.excludedSphereList.add(tmpSphere);
            }
        }
        return tmpSphereList;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Shape related methods">
    /**
     * Returns shape in current box view
     *
     * @return Shape in current box view
     */
    @Override
    public ShapeInterface getShapeInBoxView() {
        return this.xyLayerShape;
    }

    /**
     * Updates shape attenuation
     */
    @Override
    public void updateColorShapeAttenuation() {
        if (this.compartmentBox != null) {
            switch (this.boxView) {
                case XZ_FRONT:
                    this.xyLayerShape.setAttenuation(((this.bodyCenter.getY() - this.halfYLength - this.compartmentBox.getThirdDimensionValue()) / this.compartmentBox.getYLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case XZ_BACK:
                    this.xyLayerShape.setAttenuation(((this.compartmentBox.getThirdDimensionValue() - this.bodyCenter.getY() - this.halfYLength) / this.compartmentBox.getYLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case YZ_LEFT:
                    this.xyLayerShape.setAttenuation(((this.bodyCenter.getX() - this.halfXLength - this.compartmentBox.getThirdDimensionValue()) / this.compartmentBox.getXLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case YZ_RIGHT:
                    this.xyLayerShape.setAttenuation(((this.compartmentBox.getThirdDimensionValue() - this.bodyCenter.getX() - this.halfXLength) / this.compartmentBox.getXLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case XY_TOP:
                    this.xyLayerShape.setAttenuation(((this.compartmentBox.getThirdDimensionValue() - this.bodyCenter.getZ() - this.halfZLength) / this.compartmentBox.getZLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case XY_BOTTOM:
                    this.xyLayerShape.setAttenuation(((this.bodyCenter.getZ() - this.halfZLength - this.compartmentBox.getThirdDimensionValue()) / this.compartmentBox.getZLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns clone of xy-layer with the same size and position
     *
     * @return Clone of xy-layer with the same size and position
     */
    public BodyXyLayer getSizeClone() {
        return new BodyXyLayer(this.xLength, this.yLength, this.zLength, this.bodyCenter.getX(), this.bodyCenter.getY(), this.bodyCenter.getZ());
    }

    /**
     * Clears list with excluded spheres if possible
     */
    @Override
    public void clearExcludedSphereList() {
        this.excludedSphereList.clear();
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Gets the center of the body
     *
     * @return Center of the body
     */
    public PointInSpace getBodyCenter() {
        return this.bodyCenter;
    }

    /**
     * Gets the geometric form of the body
     *
     * @return Type of the body
     */
    public BodyTypeEnum getBodyType() {
        return this.bodyType;
    }

    /**
     * X-Length
     *
     * @return X-Length of xy-layer
     */
    public double getXLength() {
        return this.xLength;
    }

    /**
     * Y-Length
     *
     * @return Y-Length of xy-layer
     */
    public double getYLength() {
        return this.yLength;
    }

    /**
     * Z-Length
     *
     * @return Z-Length of xy-layer
     */
    public double getZLength() {
        return this.zLength;
    }

    /**
     * Value item
     *
     * @return Value item
     */
    public ValueItem getGeometryDataValueItem() {
        return this.valueItem;
    }

    /**
     * Gets the volume of the body
     *
     * @return Volume of the body
     */
    public double getVolume() {
        return this.volume;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="BoxView">
    /**
     * Box view
     *
     * @param aBoxView Box view
     */
    public void setBoxView(SimulationBoxViewEnum aBoxView) {
        if (this.boxView != aBoxView) {
            this.boxView = aBoxView;
            this.updateShape();
            super.notifyChangeReceiver(this, this.boxViewChangeInformation);
        }
    }

    /**
     * Box view
     *
     * @return Box view
     */
    public SimulationBoxViewEnum getBoxView() {
        return this.boxView;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Selected">
    /**
     * Returns if the body is selected
     *
     * @return true if this body is selected, false if body is not selected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Sets if the body is selected
     *
     * @param aValue true to select body, false to deselect body
     */
    public void setSelected(boolean aValue) {
        this.isSelected = aValue;
        if (this.isSelected) {
            super.notifyChangeReceiver(this, this.selectBodyChangeInformation);
        } else {
            super.notifyChangeReceiver(this, this.deselectBodyChangeInformation);
        }
        if (this.isSelected != this.valueItem.isSelected()) {
            this.valueItem.setSelected(this.isSelected);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CompartmentBox">
    /**
     * Compartment box
     *
     * @param aCompartmentBox Compartment box
     */
    public void setCompartmentBox(CompartmentBox aCompartmentBox) {
        this.compartmentBox = aCompartmentBox;
        this.updateShape();
    }

    /**
     * Compartment box
     *
     * @return Compartment box
     */
    public CompartmentBox getCompartmentBox() {
        return this.compartmentBox;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Initialisation method
     */
    private void initialize() {
        this.bodyType = BodyTypeEnum.XY_LAYER;
        // <editor-fold defaultstate="collapsed" desc="Set box view">
        this.boxView = SimulationBoxViewEnum.XZ_FRONT;
        // Set shape for defined box view
        this.xyLayerShape = new ShapeBox(this.bodyCenter.getX(), this.bodyCenter.getZ(), this.xLength, this.zLength, this);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set miscellaneous">
        this.valueItem = null;
        this.isSelected = false;
        this.compartmentBox = null;
        this.excludedSphereList = new LinkedList<BodySphere>();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate and initialize">
        this.calculateXyLayerProperties();
        this.positionChangeInformation = new ChangeInformation(ChangeTypeEnum.BODY_INTERFACE_POSITION_CHANGE, this);
        this.selectBodyChangeInformation = new ChangeInformation(ChangeTypeEnum.BODY_INTERFACE_SELECT_BODY_CHANGE, this);
        this.deselectBodyChangeInformation = new ChangeInformation(ChangeTypeEnum.BODY_INTERFACE_DESELECT_BODY_CHANGE, this);
        this.sizeChangeInformation = new ChangeInformation(ChangeTypeEnum.BODY_INTERFACE_SIZE_CHANGE, this);
        this.boxViewChangeInformation = new ChangeInformation(ChangeTypeEnum.BODY_INTERFACE_BOX_VIEW_CHANGE, this);
        // </editor-fold>
    }

    /**
     * Calculate volume and surface area of the sphere
     */
    private void calculateXyLayerProperties() {
        this.volume = this.xLength * this.yLength * this.zLength;
    }

    /**
     * Updates shape
     */
    private void updateShape() {
        // <editor-fold defaultstate="collapsed" desc="Update shape position">
        switch (this.boxView) {
            case XZ_FRONT:
            case XZ_BACK:
                this.xyLayerShape.getShapeCenter().setX(this.bodyCenter.getX());
                this.xyLayerShape.getShapeCenter().setY(this.bodyCenter.getZ());
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                this.xyLayerShape.getShapeCenter().setX(this.bodyCenter.getY());
                this.xyLayerShape.getShapeCenter().setY(this.bodyCenter.getZ());
                break;
            case XY_TOP:
            case XY_BOTTOM:
                this.xyLayerShape.getShapeCenter().setX(this.bodyCenter.getX());
                this.xyLayerShape.getShapeCenter().setY(this.bodyCenter.getY());
                break;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update shape display position ratios">
        if (this.compartmentBox != null) {
            switch (this.boxView) {
                case XZ_FRONT:
                    this.xyLayerShape.setCorrectedXRatio((this.bodyCenter.getX() - this.halfXLength) / this.compartmentBox.getXLength());
                    this.xyLayerShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getZ() + this.halfZLength) / this.compartmentBox.getZLength());
                    this.xyLayerShape.setXLength(this.xLength);
                    this.xyLayerShape.setYLength(this.zLength);
                    break;
                case XZ_BACK:
                    this.xyLayerShape.setCorrectedXRatio(1.0 - (this.bodyCenter.getX() + this.halfXLength) / this.compartmentBox.getXLength());
                    this.xyLayerShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getZ() + this.halfZLength) / this.compartmentBox.getZLength());
                    this.xyLayerShape.setXLength(this.xLength);
                    this.xyLayerShape.setYLength(this.zLength);
                    break;
                case YZ_LEFT:
                    this.xyLayerShape.setCorrectedXRatio(1.0 - (this.bodyCenter.getY() + this.halfYLength) / this.compartmentBox.getYLength());
                    this.xyLayerShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getZ() + this.halfZLength) / this.compartmentBox.getZLength());
                    this.xyLayerShape.setXLength(this.yLength);
                    this.xyLayerShape.setYLength(this.zLength);
                    break;
                case YZ_RIGHT:
                    this.xyLayerShape.setCorrectedXRatio((this.bodyCenter.getY() - this.halfYLength) / this.compartmentBox.getYLength());
                    this.xyLayerShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getZ() + this.halfZLength) / this.compartmentBox.getZLength());
                    this.xyLayerShape.setXLength(this.yLength);
                    this.xyLayerShape.setYLength(this.zLength);
                    break;
                case XY_TOP:
                    this.xyLayerShape.setCorrectedXRatio((this.bodyCenter.getX() - this.halfXLength) / this.compartmentBox.getXLength());
                    this.xyLayerShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getY() + this.halfYLength) / this.compartmentBox.getYLength());
                    this.xyLayerShape.setXLength(this.xLength);
                    this.xyLayerShape.setYLength(this.yLength);
                    break;
                case XY_BOTTOM:
                    this.xyLayerShape.setCorrectedXRatio((this.bodyCenter.getX() - this.halfXLength) / this.compartmentBox.getXLength());
                    this.xyLayerShape.setCorrectedYRatio((this.bodyCenter.getY() - this.halfYLength) / this.compartmentBox.getYLength());
                    this.xyLayerShape.setXLength(this.xLength);
                    this.xyLayerShape.setYLength(this.yLength);
                    break;
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update shape size ratio">
        if (this.compartmentBox != null) {
            switch (this.boxView) {
                case XZ_FRONT:
                case XZ_BACK:
                    this.xyLayerShape.setCorrectedXLengthRatio(this.xLength / this.compartmentBox.getXLength());
                    this.xyLayerShape.setCorrectedYLengthRatio(this.zLength / this.compartmentBox.getZLength());
                    break;
                case XY_TOP:
                case XY_BOTTOM:
                    this.xyLayerShape.setCorrectedXLengthRatio(this.xLength / this.compartmentBox.getXLength());
                    this.xyLayerShape.setCorrectedYLengthRatio(this.yLength / this.compartmentBox.getYLength());
                    break;
                case YZ_LEFT:
                case YZ_RIGHT:
                    this.xyLayerShape.setCorrectedXLengthRatio(this.yLength / this.compartmentBox.getYLength());
                    this.xyLayerShape.setCorrectedYLengthRatio(this.zLength / this.compartmentBox.getZLength());
                    break;
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update color shape attenuation">
        this.updateColorShapeAttenuation();
        // </editor-fold>
    }
    
    /**
     * Calculates size related quantities
     */
    private void calculateSizeRelatedQuantities() {
        this.halfXLength = this.xLength / 2.0;
        this.halfYLength = this.yLength / 2.0;
        this.halfZLength = this.zLength / 2.0;
        this.halfXLengthCorrected = this.halfXLength * ModelDefinitions.FACTOR_FOR_GRAPHICS_NUMBER_CORRECTION;
        this.halfYLengthCorrected = this.halfYLength * ModelDefinitions.FACTOR_FOR_GRAPHICS_NUMBER_CORRECTION;
        this.halfZLengthCorrected = this.halfZLength * ModelDefinitions.FACTOR_FOR_GRAPHICS_NUMBER_CORRECTION;
        
        this.bodyCenterX_Minus_halfXLengthCorrected = this.bodyCenter.getX() - this.halfXLengthCorrected;
        this.bodyCenterX_Plus_halfXLengthCorrected = this.bodyCenter.getX() + this.halfXLengthCorrected;
        this.bodyCenterY_Minus_halfYLengthCorrected = this.bodyCenter.getY() - this.halfYLengthCorrected;
        this.bodyCenterY_Plus_halfYLengthCorrected = this.bodyCenter.getY() + this.halfYLengthCorrected;
        this.bodyCenterZ_Minus_halfZLengthCorrected = this.bodyCenter.getZ() - this.halfZLengthCorrected;
        this.bodyCenterZ_Plus_halfZLengthCorrected = this.bodyCenter.getZ() + this.halfZLengthCorrected;
    }
    // </editor-fold>

}
