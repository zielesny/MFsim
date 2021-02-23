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
package de.gnwi.mfsim.model.graphics.body;

import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentBox;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.shape.ShapeCircle;
import de.gnwi.mfsim.model.graphics.shape.ShapeInterface;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.spices.IPointInSpace;
import java.util.LinkedList;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import java.util.Random;

/**
 * This class represents a sphere. The sphere is defined by center and radius.
 *
 * @author Achim Zielesny
 */
public class BodySphere extends ChangeNotifier implements BodyInterface, ChangeReceiverInterface {

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
     * Center of the body
     */
    private PointInSpace bodyCenter;

    /**
     * True: Body is selected, false: Otherwise
     */
    private boolean isSelected;

    /**
     * Radius of the sphere
     */
    private double radius;

    /**
     * Corrected radius of the sphere
     */
    private double radiusCorrected;

    /**
     * Corrected square of radius of the sphere
     */
    private double radiusSquareCorrected;

    /**
     * Circle shape
     */
    private ShapeCircle circleShape;

    /**
     * Simulation box
     */
    private CompartmentBox compartmentBox;

    /**
     * Value item
     */
    private ValueItem valueItem;

    /**
     * Volume of the body
     */
    private double volume;

    /**
     * List with excluded spheres
     */
    private LinkedList<BodySphere> excludedSphereList;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     *
     * @param aRadius Radius of the sphere
     * @param aBodyCenter Center of the body
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public BodySphere(double aRadius, PointInSpace aBodyCenter) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aRadius <= 0) {
            throw new IllegalArgumentException("aRadius was negative or 0.");
        }
        if (aBodyCenter == null) {
            throw new IllegalArgumentException("aBodyCenter was null.");
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set geometry">
        this.radius = aRadius;
        this.calculateRadiusRelatedQuantities();
        this.bodyCenter = aBodyCenter;
        // </editor-fold>
        this.initialize();
    }

    /**
     * Constructor
     *
     * @param aRadius Radius of the sphere
     * @param aXCoordinateOfCenter X coordinate of the center of mass point
     * @param aYCoordinateOfCenter Y coordinate of the center of mass point
     * @param aZCoordinateOfCenter Z coordinate of the center of mass point
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public BodySphere(double aRadius, double aXCoordinateOfCenter, double aYCoordinateOfCenter, double aZCoordinateOfCenter) throws IllegalArgumentException {
        this(aRadius, new PointInSpace(aXCoordinateOfCenter, aYCoordinateOfCenter, aZCoordinateOfCenter));
    }

    /**
     * Constructor
     *
     * @param aSphereGeometryDataValueItem Sphere geometry data value item
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public BodySphere(ValueItem aSphereGeometryDataValueItem) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSphereGeometryDataValueItem == null) {
            throw new IllegalArgumentException("aSphereValueItem is null.");
        }
        if (!this.graphicsUtilityMethods.isSphereGeometryDataValueItem(aSphereGeometryDataValueItem)) {
            throw new IllegalArgumentException("aSphereValueItem is illegal.");
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set geometry">
        this.bodyCenter = 
            new PointInSpace(
                aSphereGeometryDataValueItem.getValueAsDouble(0, 0), 
                aSphereGeometryDataValueItem.getValueAsDouble(0, 1), 
                aSphereGeometryDataValueItem.getValueAsDouble(0, 2)
            );
        this.radius = aSphereGeometryDataValueItem.getValueAsDouble(0, 3);
        this.calculateRadiusRelatedQuantities();
        // </editor-fold>
        this.initialize();
        // <editor-fold defaultstate="collapsed" desc="Add this instance as change receiver">
        this.valueItem = aSphereGeometryDataValueItem;
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
                if (this.radius != this.valueItem.getValueAsDouble(0, 3)) {
                    this.radius = this.valueItem.getValueAsDouble(0, 3);
                    this.calculateRadiusRelatedQuantities();
                    this.calculateSphereProperties();
                    tmpIsSizeChange = true;
                }
                if (tmpIsPositionChange) {
                    super.notifyChangeReceiver(this, this.positionChangeInformation);
                }
                if (tmpIsSizeChange) {
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
     * CompareTo method: Compares position of body from "front" to "back" in specified view
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
     * NOTE: ModelDefinitions.OFFSET_FOR_COMPARISON_OF_NUMBERS is used in 
     * corrected quantities to avoid errors due to roundoff problems.
     *
     * @param aPoint Point which is tested
     * @return True if point is inside body, false if point is on surface or outside of body
     */
    @Override
    public boolean isInVolume(IPointInSpace aPoint) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint == null) {
            return false;
        }
        // </editor-fold>
        return this.graphicsUtilityMethods.getDistanceSquareInSpace(aPoint, this.bodyCenter) <= this.radiusSquareCorrected;
        // Slower alternative:
        // return this.graphicsUtilityMethods.getDistanceInSpace(aPoint, this.bodyCenter) <= this.radiusCorrected;
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
                return this.graphicsUtilityMethods.isSphereSphereOverlap(this, (BodySphere) aBody);
            case XY_LAYER:
                return this.graphicsUtilityMethods.isSphereXyLayerOverlap(this, (BodyXyLayer) aBody);
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
    public PointInSpace getRandomPointInVolume(Random aRandomNumberGenerator) {
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
    public PointInSpace[] getRandomPointsInVolume(int aNumber, Random aRandomNumberGenerator) throws IllegalArgumentException {

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
    public void fillRandomPointsInVolume(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, Random aRandomNumberGenerator) throws IllegalArgumentException {
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
        this.graphicsUtilityMethods.fillRandomPointsInSphere(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.radius, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points inside the body into buffers 1 and 2.
     *
     * @param aBuffer1 Buffer 1
     * @param aBuffer2 Buffer 2
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    @Override
    public void fillRandomPointsInVolume(IPointInSpace[] aBuffer1, IPointInSpace[] aBuffer2, int aFirstIndex, int aNumber, Random aRandomNumberGenerator) throws IllegalArgumentException {
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

        // </editor-fold>
        this.graphicsUtilityMethods.fillRandomPointsInSphere(aBuffer1, aBuffer2, aFirstIndex, aNumber, this.bodyCenter, this.radius, aRandomNumberGenerator);
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
        Random aRandomNumberGenerator
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
        this.graphicsUtilityMethods.fillRandomPointsInSphereWithExcludingSpheres(
            aBuffer, 
            aFirstIndex, 
            aNumber, 
            this.bodyCenter, 
            this.radius, 
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
        Random aRandomNumberGenerator
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
        this.graphicsUtilityMethods.fillRandomPointsInSphereWithExcludingSpheres(
            aBuffer1, 
            aBuffer2, 
            aFirstIndex, 
            aNumber, 
            this.bodyCenter, 
            this.radius, 
            this.excludedSphereList,
            aStepDistance, 
            aNumberOfTrials,
            aRandomNumberGenerator
        );
    }

    /**
     * Returns a random point on the surface of body
     *
     * @param aRandomNumberGenerator Random number generator
     * @return Point on surface of body
     */
    @Override
    public PointInSpace getRandomPointOnSurface(Random aRandomNumberGenerator) {
        return this.getRandomPointsOnSurface(1, aRandomNumberGenerator)[0];
    }

    /**
     * Returns aNumber random points on surface of the body
     *
     * @param aNumber Number of random points which have to be created, not allowed to be less than 1
     * @param aRandomNumberGenerator Random number generator
     * @return Points on surface of the body as array, array.length() = aNumber
     * @throws IllegalArgumentException if aNumber is less than 1
     */
    @Override
    public PointInSpace[] getRandomPointsOnSurface(int aNumber, Random aRandomNumberGenerator) throws IllegalArgumentException {
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
    public void fillRandomPointsOnSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, Random aRandomNumberGenerator) throws IllegalArgumentException {
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
        this.graphicsUtilityMethods.fillRandomPointsOnSphereSurface(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.radius, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points on the upper surface of the body into buffer
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillUpperRandomPointsOnSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, Random aRandomNumberGenerator) throws IllegalArgumentException {

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
        this.graphicsUtilityMethods.fillUpperRandomPointsOnSphereSurface(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.radius, aRandomNumberGenerator);
    }

    /**
     * Fills aNumber random points on the middle surface of the body into buffer
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillMiddleRandomPointsOnSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, Random aRandomNumberGenerator) throws IllegalArgumentException {

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
        this.graphicsUtilityMethods.fillMiddleRandomPointsOnSphereSurface(aBuffer, aFirstIndex, aNumber, this.bodyCenter, this.radius, aRandomNumberGenerator);
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
        Random aRandomNumberGenerator
    ) {
        LinkedList<BodySphere> tmpSphereList = 
            this.graphicsUtilityMethods.getNonOverlappingRandomSpheresInSphere(
                this.excludedSphereList, 
                this.bodyCenter, 
                this.radius, 
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
        return this.circleShape;
    }

    /**
     * Updates shape attenuation
     */
    @Override
    public void updateColorShapeAttenuation() {
        if (this.compartmentBox != null) {
            switch (this.boxView) {
                case XZ_FRONT:
                    this.circleShape.setAttenuation(((this.bodyCenter.getY() - this.radius - this.compartmentBox.getThirdDimensionValue()) / this.compartmentBox.getYLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case XZ_BACK:
                    this.circleShape.setAttenuation(((this.compartmentBox.getThirdDimensionValue() - this.bodyCenter.getY() - this.radius) / this.compartmentBox.getYLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case YZ_LEFT:
                    this.circleShape.setAttenuation(((this.bodyCenter.getX() - this.radius - this.compartmentBox.getThirdDimensionValue()) / this.compartmentBox.getXLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case YZ_RIGHT:
                    this.circleShape.setAttenuation(((this.compartmentBox.getThirdDimensionValue() - this.bodyCenter.getX() - this.radius) / this.compartmentBox.getXLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case XY_TOP:
                    this.circleShape.setAttenuation(((this.compartmentBox.getThirdDimensionValue() - this.bodyCenter.getZ() - this.radius) / this.compartmentBox.getZLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
                case XY_BOTTOM:
                    this.circleShape.setAttenuation(((this.bodyCenter.getZ() - this.radius - this.compartmentBox.getThirdDimensionValue()) / this.compartmentBox.getZLength())
                            * Preferences.getInstance().getDepthAttenuationCompartment());
                    break;
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns clone of sphere with the same radius and position
     *
     * @return Clone of sphere with the same radius and position
     */
    public BodySphere getSizeClone() {
        return new BodySphere(this.radius, this.bodyCenter.getX(), this.bodyCenter.getY(), this.bodyCenter.getZ());
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
     * Gets the geometric form of the body
     *
     * @return Type of the body
     */
    public BodyTypeEnum getBodyType() {
        return this.bodyType;
    }

    /**
     * Gets the radius of the sphere
     *
     * @return Radius of the sphere
     */
    public double getRadius() {
        return this.radius;
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
    // <editor-fold defaultstate="collapsed" desc="BodyCenter">
    /**
     * Gets the center of the body
     *
     * @return Center of the body
     */
    public PointInSpace getBodyCenter() {
        return this.bodyCenter;
    }

    /**
     * Sets body center. NOTE: No checks are performed and this object instance is again initialised.
     *
     * @param aBodyCenter Center of the body
     */
    public void setBodyCenter(PointInSpace aBodyCenter) {
        this.bodyCenter = aBodyCenter;
        this.initialize();
    }

    /**
     * Sets body center WITHOUT re-initialisation of this object instance. NOTE: No checks are performed.
     *
     * @param aBodyCenter Center of the body
     */
    public void setBodyCenterWithoutInitialisation(PointInSpace aBodyCenter) {
        this.bodyCenter = aBodyCenter;
    }
    // </editor-fold>
    //
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
    //
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
    //
    // <editor-fold defaultstate="collapsed" desc="CompartmentBox">
    /**
     * Compartment box. NOTE: No checks are performed.
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
        this.bodyType = BodyTypeEnum.SPHERE;
        // <editor-fold defaultstate="collapsed" desc="Set box view">
        this.boxView = SimulationBoxViewEnum.XZ_FRONT;
        // Set shape for defined box view
        this.circleShape = new ShapeCircle(this.bodyCenter.getX(), this.bodyCenter.getZ(), this.radius, this);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set miscellaneous">
        this.valueItem = null;
        this.isSelected = false;
        this.compartmentBox = null;
        this.excludedSphereList = new LinkedList<>();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate and initialize">
        this.calculateSphereProperties();
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
    private void calculateSphereProperties() {
        double tmpRadiusSquared = this.radius * this.radius;
        this.volume = ModelDefinitions.THREE_QUARTERS_PI * tmpRadiusSquared * this.radius;
    }

    /**
     * Updates shape
     */
    private void updateShape() {

        // <editor-fold defaultstate="collapsed" desc="Update shape position">
        switch (this.boxView) {
            case XZ_FRONT:
            case XZ_BACK:
                this.circleShape.getShapeCenter().setX(this.bodyCenter.getX());
                this.circleShape.getShapeCenter().setY(this.bodyCenter.getZ());
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                this.circleShape.getShapeCenter().setX(this.bodyCenter.getY());
                this.circleShape.getShapeCenter().setY(this.bodyCenter.getZ());
                break;
            case XY_TOP:
            case XY_BOTTOM:
                this.circleShape.getShapeCenter().setX(this.bodyCenter.getX());
                this.circleShape.getShapeCenter().setY(this.bodyCenter.getY());
                break;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update shape display position ratios">
        if (this.compartmentBox != null) {
            switch (this.boxView) {
                case XZ_FRONT:
                    this.circleShape.setCorrectedXRatio((this.bodyCenter.getX() - this.radius) / this.compartmentBox.getXLength());
                    this.circleShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getZ() + this.radius) / this.compartmentBox.getZLength());
                    break;
                case XZ_BACK:
                    this.circleShape.setCorrectedXRatio(1.0 - (this.bodyCenter.getX() + this.radius) / this.compartmentBox.getXLength());
                    this.circleShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getZ() + this.radius) / this.compartmentBox.getZLength());
                    break;
                case YZ_LEFT:
                    this.circleShape.setCorrectedXRatio(1.0 - (this.bodyCenter.getY() + this.radius) / this.compartmentBox.getYLength());
                    this.circleShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getZ() + this.radius) / this.compartmentBox.getZLength());
                    break;
                case YZ_RIGHT:
                    this.circleShape.setCorrectedXRatio((this.bodyCenter.getY() - this.radius) / this.compartmentBox.getYLength());
                    this.circleShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getZ() + this.radius) / this.compartmentBox.getZLength());
                    break;
                case XY_TOP:
                    this.circleShape.setCorrectedXRatio((this.bodyCenter.getX() - this.radius) / this.compartmentBox.getXLength());
                    this.circleShape.setCorrectedYRatio(1.0 - (this.bodyCenter.getY() + this.radius) / this.compartmentBox.getYLength());
                    break;
                case XY_BOTTOM:
                    this.circleShape.setCorrectedXRatio((this.bodyCenter.getX() - this.radius) / this.compartmentBox.getXLength());
                    this.circleShape.setCorrectedYRatio((this.bodyCenter.getY() - this.radius) / this.compartmentBox.getYLength());
                    break;
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update shape size">
        this.circleShape.setRadius(this.radius);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update shape size ratio">
        if (this.compartmentBox != null) {
            switch (this.boxView) {
                case XZ_FRONT:
                case XZ_BACK:
                case XY_TOP:
                case XY_BOTTOM:
                    this.circleShape.setCorrectedRadiusXRatio(this.radius / this.compartmentBox.getXLength());
                    break;
                case YZ_LEFT:
                case YZ_RIGHT:
                    this.circleShape.setCorrectedRadiusXRatio(this.radius / this.compartmentBox.getYLength());
                    break;
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update color shape attenuation">
        this.updateColorShapeAttenuation();
        // </editor-fold>
    }
    
    /**
     * Calculates radius related quantities
     */
    private void calculateRadiusRelatedQuantities() {
        this.radiusCorrected = this.radius * ModelDefinitions.FACTOR_FOR_GRAPHICS_NUMBER_CORRECTION;
        this.radiusSquareCorrected = this.radiusCorrected * this.radiusCorrected;
    }
    // </editor-fold>

}
