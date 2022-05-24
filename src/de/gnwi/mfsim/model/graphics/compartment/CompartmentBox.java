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
package de.gnwi.mfsim.model.graphics.compartment;

import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.body.BodyXyLayer;
import de.gnwi.mfsim.model.graphics.body.BodySphere;
import de.gnwi.mfsim.model.graphics.body.BodyInterface;
import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.point.PointInPlane;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import de.gnwi.jdpd.interfaces.IRandom;
import de.gnwi.spices.IPointInSpace;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * This class represents a simulation box that can contain BodyInterface
 * objects.
 *
 * @author Achim Zielesny
 */
public class CompartmentBox extends ChangeNotifier implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility graphics methods
     */
    private final GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * Change
     */
    private ChangeInformation changeInformation;

    /**
     * LinkedList with all BodyInterface objects of the simulation box
     */
    private ArrayList<BodyInterface> bodiesInContainer;

    /**
     * Box view
     */
    private SimulationBoxViewEnum boxView;

    /**
     * Box size info
     */
    private BoxSizeInfo boxSizeInfo;

    /**
     * Box size (x, y and z coordinate of point represent corresponding size of
     * the simulation box)
     */
    private PointInSpace boxSize;

    /**
     * Selected body
     */
    private BodyInterface selectedBody;

    /**
     * Cache for bodies
     */
    private LinkedList<BodyInterface> bodyCacheList;

    /**
     * Third dimension value for bodies
     */
    private double bodiesThirdDimensionValue;

    /**
     * Third dimension value
     */
    private double thirdDimensionValue;

    /**
     * Volume of the simulation box
     */
    private double volume;

    /**
     * Point in plane
     */
    private PointInPlane pointInPlane = new PointInPlane(0, 0);

    /**
     * Point in space
     */
    private PointInSpace pointInSpace1 = new PointInSpace(0, 0, 0);

    /**
     * Point in space
     */
    private PointInSpace pointInSpace2 = new PointInSpace(0, 0, 0);

    /**
     * Graphical particle position
     */
    private GraphicalParticlePosition graphicalParticlePosition1 = new GraphicalParticlePosition(0, 0, 0);

    /**
     * Graphical particle position
     */
    private GraphicalParticlePosition graphicalParticlePosition2 = new GraphicalParticlePosition(0, 0, 0);

    /**
     * List with excluded spheres
     */
    private LinkedList<BodySphere> excludedSphereList;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aXLength Length of the simulation box along the x axis, not
     * allowed to be less/equal 0
     * @param aYLength Length of the simulation box along the y axis, not
     * allowed to be less/equal 0
     * @param aZLength Length of the simulation box along the z axis, not
     * allowed to be less/equal 0
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CompartmentBox(double aXLength, double aYLength, double aZLength) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXLength <= 0) {
            throw new IllegalArgumentException("aXLength was less/equal 0");
        }
        if (aYLength <= 0) {
            throw new IllegalArgumentException("aYLength was less/equal 0");
        }
        if (aZLength <= 0) {
            throw new IllegalArgumentException("aZLength was less/equal 0");
        }
        // </editor-fold>
        this.boxSize = new PointInSpace(aXLength, aYLength, aZLength);
        this.boxSizeInfo = new BoxSizeInfo(0.0, aXLength, 0.0, aYLength, 0.0, aZLength);
        this.bodiesInContainer = new ArrayList<BodyInterface>(ModelDefinitions.INITIAL_NUMBER_OF_BODIES);
        this.boxView = SimulationBoxViewEnum.XZ_FRONT;
        this.thirdDimensionValue = 0.0;
        this.selectedBody = null;
        this.changeInformation = new ChangeInformation(ChangeTypeEnum.COMPARTMENT_BOX_CHANGE, null);
        this.excludedSphereList = new LinkedList<>();
        this.clearBodiesCache();
        this.calculateVolume();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyChange() method">
    /**
     * Notify method for this instance as a changeInformation receiver
     *
     * @param aChangeInfo Change information
     * @param aChangeNotifier Object that notifies changeInformation
     */
    @Override
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        if (aChangeInfo.getChangeType() == ChangeTypeEnum.BODY_INTERFACE_SELECT_BODY_CHANGE) {
            // Body was selected
            this.selectBody((BodyInterface) aChangeInfo.getInfo());
        } else if (aChangeInfo.getChangeType() == ChangeTypeEnum.BODY_INTERFACE_DESELECT_BODY_CHANGE) {
            // Body was deselected
            this.deselectBody();
        } else {
            // Something else
            this.clearBodiesCache();
        }
        super.notifyChangeReceiver(this, this.changeInformation);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Body related methods">
    /**
     * Adds an BodyInterface object to the simulation box WITHOUT move or resize
     * operations.
     *
     * @param aBody BodyInterface object which is added
     * @return True if aBody was added, false if aBody could not be added
     */
    public boolean addBody(BodyInterface aBody) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBody == null) {
            return false;
        }
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            if (tmpBody == aBody) {
                return false;
            }
        }
        // </editor-fold>
        // Set box view first ...
        aBody.setBoxView(this.boxView);
        // ... then add this instance as changeInformation receiver
        aBody.addChangeReceiver(this);
        aBody.setCompartmentBox(this);
        this.bodiesInContainer.add(aBody);
        this.clearBodiesCache();
        return true;
    }

    /**
     * Returns a BodyInterface object that corresponds to value item from the
     * simulation box.
     *
     * @param aValueItem Value item
     * @return BodyInterface object or null if none is found
     */
    public BodyInterface getBody(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return null;
        }
        // </editor-fold>
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            if (aValueItem == tmpBody.getGeometryDataValueItem()) {
                return tmpBody;
            }
        }
        return null;
    }

    /**
     * Returns whether compartment box has defined bodies
     *
     * @return True: Compartment box has defined bodies, false: Otherwise
     */
    public boolean hasBodies() {
        return this.bodiesInContainer.size() > 0;
    }

    /**
     * Removes a BodyInterface object from the simulation box.
     *
     * @param aBody Body
     * @return True: Body was removed, false: Otherwise (nothing changed)
     */
    public boolean removeBody(BodyInterface aBody) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBody == null) {
            return false;
        }

        // </editor-fold>
        for (int i = 0; i < this.bodiesInContainer.size(); i++) {
            if (aBody == this.bodiesInContainer.get(i)) {
                aBody.removeSingleChangeReceiver(this);
                if (aBody == this.selectedBody) {
                    this.selectedBody = null;
                }
                this.bodiesInContainer.remove(i);
                this.clearBodiesCache();
                return true;
            }
        }
        return false;
    }

    /**
     * Removes an BodyInterface object that corresponds to value item from the
     * simulation box.
     *
     * @param aValueItem Value item
     * @return True: Body was removed, false: Otherwise (nothing changed)
     */
    public boolean removeBody(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return false;
        }
        // </editor-fold>
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            if (aValueItem == tmpBody.getGeometryDataValueItem()) {
                return this.removeBody(tmpBody);
            }
        }
        return false;
    }

    /**
     * Checks if aBody is contained
     *
     * @param aBody Body
     * @return True: Simulation box contains aBody, false: Otherwise
     */
    public boolean containsBody(Object aBody) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBody == null) {
            return false;
        }
        if (this.bodiesInContainer.size() == 0) {
            return false;
        }

        // </editor-fold>
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            if (tmpBody == aBody) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets color shape attenuation of bodies
     */
    public void setColorShapeAttenuation() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.bodiesInContainer.size() == 0) {
            return;
        }
        // </editor-fold>
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            tmpBody.updateColorShapeAttenuation();
        }
    }

    /**
     * Magnifies body
     *
     * @param aBody BodyInterface object has to be magnified
     * @param anOffsetX Offset in X direction
     * @param anOffsetY Offset in Y direction
     * @param anOffsetZ Offset in Z direction
     * @param anOffsetRadius Offset for for radius
     * @throws IllegalArgumentException Thrown if body type is unknown
     */
    public void magnifyBody(BodyInterface aBody, double anOffsetX, double anOffsetY, double anOffsetZ, double anOffsetRadius) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        // Check of test coordinates is not necessary
        if (aBody == null) {
            throw new IllegalArgumentException("aBody was null");
        }
        // </editor-fold>
        switch (aBody.getBodyType()) {
            // <editor-fold defaultstate="collapsed" desc="SPHERE">
            case SPHERE:
                if (!Preferences.getInstance().isConstantCompartmentBodyVolume()) {
                    BodySphere tmpSphere = (BodySphere) aBody;
                    // Initialize change detection
                    aBody.getGeometryDataValueItem().initializeChangeDetection();

                    double tmpCurrentRadius = tmpSphere.getRadius();
                    double tmpOffset = anOffsetRadius;
                    double tmpMagnifiedRadius = tmpCurrentRadius + tmpOffset;
                    double tmpMinimumRadius = aBody.getGeometryDataValueItem().getMatrix()[0][3].getTypeFormat().getMinimumValue();
                    if (tmpMagnifiedRadius < tmpMinimumRadius) {
                        tmpMagnifiedRadius = tmpMinimumRadius;
                    }

                    boolean tmpIsMagnifiedRadiusValid = true;
                    if (tmpSphere.getBodyCenter().getX() < tmpMagnifiedRadius) {
                        tmpIsMagnifiedRadiusValid = false;
                    }
                    if (tmpSphere.getBodyCenter().getX() + tmpMagnifiedRadius > this.boxSize.getX()) {
                        tmpIsMagnifiedRadiusValid = false;
                    }
                    if (tmpSphere.getBodyCenter().getY() < tmpMagnifiedRadius) {
                        tmpIsMagnifiedRadiusValid = false;
                    }
                    if (tmpSphere.getBodyCenter().getY() + tmpMagnifiedRadius > this.boxSize.getY()) {
                        tmpIsMagnifiedRadiusValid = false;
                    }
                    if (tmpSphere.getBodyCenter().getZ() < tmpMagnifiedRadius) {
                        tmpIsMagnifiedRadiusValid = false;
                    }
                    if (tmpSphere.getBodyCenter().getZ() + tmpMagnifiedRadius > this.boxSize.getZ()) {
                        tmpIsMagnifiedRadiusValid = false;
                    }
                    if (tmpIsMagnifiedRadiusValid) {
                        // Set magnified radius at 4. position (with index 3) of geometry data value item
                        aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpMagnifiedRadius), 0, 3);
                    }

                    if (aBody.getGeometryDataValueItem().hasChangeDetected()) {
                        // Change occurred: Notify dependent value items
                        aBody.getGeometryDataValueItem().notifyDependentValueItemsForUpdate();
                    }
                }
                break;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="XY_LAYER">
            case XY_LAYER:
                BodyXyLayer tmpXyLayer = (BodyXyLayer) aBody;
                // Initialize change detection
                aBody.getGeometryDataValueItem().initializeChangeDetection();

                if (anOffsetX != 0) {
                    double tmpCurrentXLength = tmpXyLayer.getXLength();
                    double tmpNewXLength = tmpCurrentXLength + anOffsetX;
                    double tmpMinimumXLength = aBody.getGeometryDataValueItem().getMatrix()[0][3].getTypeFormat().getMinimumValue();
                    if (tmpNewXLength < tmpMinimumXLength) {
                        tmpNewXLength = tmpMinimumXLength;
                    }

                    double tmpNewZLength;
                    if (Preferences.getInstance().isConstantCompartmentBodyVolume()) {
                        // Change z length to preserve volume
                        tmpNewZLength = tmpXyLayer.getVolume() / (tmpNewXLength * tmpXyLayer.getYLength());
                    } else {
                        tmpNewZLength = tmpXyLayer.getZLength();
                    }

                    boolean tmpIsNewXLengthValid = true;
                    if (tmpXyLayer.getBodyCenter().getX() < tmpNewXLength / 2.0) {
                        tmpIsNewXLengthValid = false;
                    } else if (tmpXyLayer.getBodyCenter().getX() + tmpNewXLength / 2.0 > this.boxSize.getX()) {
                        tmpIsNewXLengthValid = false;
                    }
                    if (tmpXyLayer.getBodyCenter().getZ() < tmpNewZLength / 2.0) {
                        tmpIsNewXLengthValid = false;
                    } else if (tmpXyLayer.getBodyCenter().getZ() + tmpNewZLength / 2.0 > this.boxSize.getZ()) {
                        tmpIsNewXLengthValid = false;
                    }
                    if (tmpIsNewXLengthValid) {
                        // Set new x-length at 4. position (with index 3) of geometry data value item
                        aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpNewXLength), 0, 3);
                        // Set new z-length at 6. position (with index 5) of geometry data value item
                        aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpNewZLength), 0, 5);
                    }
                }

                if (anOffsetY != 0) {
                    double tmpCurrentYLength = tmpXyLayer.getYLength();
                    double tmpNewYLength = tmpCurrentYLength + anOffsetY;
                    double tmpMinimumYLength = aBody.getGeometryDataValueItem().getMatrix()[0][4].getTypeFormat().getMinimumValue();
                    if (tmpNewYLength < tmpMinimumYLength) {
                        tmpNewYLength = tmpMinimumYLength;
                    }


                    double tmpNewZLength;
                    if (Preferences.getInstance().isConstantCompartmentBodyVolume()) {
                        // Change z length to preserve volume
                        tmpNewZLength = tmpXyLayer.getVolume() / (tmpXyLayer.getXLength() * tmpNewYLength);
                    } else {
                        tmpNewZLength = tmpXyLayer.getZLength();
                    }

                    boolean tmpIsNewYLengthValid = true;
                    if (tmpXyLayer.getBodyCenter().getY() < tmpNewYLength / 2.0) {
                        tmpIsNewYLengthValid = false;
                    } else if (tmpXyLayer.getBodyCenter().getY() + tmpNewYLength / 2.0 > this.boxSize.getY()) {
                        tmpIsNewYLengthValid = false;
                    }
                    if (tmpXyLayer.getBodyCenter().getZ() < tmpNewZLength / 2.0) {
                        tmpIsNewYLengthValid = false;
                    } else if (tmpXyLayer.getBodyCenter().getZ() + tmpNewZLength / 2.0 > this.boxSize.getZ()) {
                        tmpIsNewYLengthValid = false;
                    }
                    if (tmpIsNewYLengthValid) {
                        // Set new y-length at 5. position (with index 4) of geometry data value item
                        aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpNewYLength), 0, 4);
                        // Set new z-length at 6. position (with index 5) of geometry data value item
                        aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpNewZLength), 0, 5);
                    }
                }

                if (anOffsetZ != 0) {
                    double tmpCurrentZLength = tmpXyLayer.getZLength();
                    double tmpNewZLength = tmpCurrentZLength + anOffsetZ;
                    double tmpMinimumZLength = aBody.getGeometryDataValueItem().getMatrix()[0][5].getTypeFormat().getMinimumValue();
                    if (tmpNewZLength < tmpMinimumZLength) {
                        tmpNewZLength = tmpMinimumZLength;
                    }

                    double tmpNewXLength;
                    double tmpNewYLength;
                    if (Preferences.getInstance().isConstantCompartmentBodyVolume()) {
                        // Change x and y length equally with same increment to preserve volume
                        // (x + a) * (y + a) + zNew = V ; V = x * y * z
                        // Mathematica: a = (-(x + y) * Sqrt[zNew] + Sqrt[4 * V + (x-y)^2 * zNew]) / (2*Sqrt[zNew])
                        double tmpSqrtZnew = Math.sqrt(tmpNewZLength);
                        double tmpX = tmpXyLayer.getXLength();
                        double tmpY = tmpXyLayer.getYLength();
                        double tmpFourV = 4.0 * tmpXyLayer.getVolume();
                        double tmpXplusY = tmpX + tmpY;
                        double tmpXminusYSquare = (tmpX - tmpY) * (tmpX - tmpY);
                        double tmpA = (-tmpXplusY * tmpSqrtZnew + Math.sqrt(tmpFourV + tmpXminusYSquare * tmpNewZLength)) / (2.0 * tmpSqrtZnew);
                        System.out.println(String.valueOf(tmpA));
                        tmpNewXLength = tmpXyLayer.getXLength() + tmpA;
                        tmpNewYLength = tmpXyLayer.getYLength() + tmpA;
                    } else {
                        tmpNewXLength = tmpXyLayer.getXLength();
                        tmpNewYLength = tmpXyLayer.getYLength();
                    }

                    boolean tmpIsNewZLengthValid = true;
                    if (tmpXyLayer.getBodyCenter().getX() < tmpNewXLength / 2.0) {
                        tmpIsNewZLengthValid = false;
                    } else if (tmpXyLayer.getBodyCenter().getX() + tmpNewXLength / 2.0 > this.boxSize.getX()) {
                        tmpIsNewZLengthValid = false;
                    }
                    if (tmpXyLayer.getBodyCenter().getY() < tmpNewYLength / 2.0) {
                        tmpIsNewZLengthValid = false;
                    } else if (tmpXyLayer.getBodyCenter().getY() + tmpNewYLength / 2.0 > this.boxSize.getY()) {
                        tmpIsNewZLengthValid = false;
                    }
                    if (tmpXyLayer.getBodyCenter().getZ() < tmpNewZLength / 2.0) {
                        tmpIsNewZLengthValid = false;
                    } else if (tmpXyLayer.getBodyCenter().getZ() + tmpNewZLength / 2.0 > this.boxSize.getZ()) {
                        tmpIsNewZLengthValid = false;
                    }
                    if (tmpIsNewZLengthValid) {
                        // Set new x-length at 4. position (with index 3) of geometry data value item
                        aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpNewXLength), 0, 3);
                        // Set new y-length at 5. position (with index 4) of geometry data value item
                        aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpNewYLength), 0, 4);
                        // Set new z-length at 6. position (with index 5) of geometry data value item
                        aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpNewZLength), 0, 5);
                    }
                }

                if (aBody.getGeometryDataValueItem().hasChangeDetected()) {
                    // Change occurred: Notify dependent value items
                    aBody.getGeometryDataValueItem().notifyDependentValueItemsForUpdate();
                }

                break;
            // </editor-fold>
            default:
                throw new IllegalArgumentException("Body type is unknown.");
        }
    }
    
    /**
     * Clears lists of excluded spheres of bodies
     */
    public void clearExcludedSphereListsOfBodies() {
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            tmpBody.clearExcludedSphereList();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Removes all bodies from simulation box
     */
    public void clear() {
        this.bodiesInContainer.clear();
        this.clearBodiesCache();
    }

    /**
     * Calculates the volume of the simulation box minus the volumes of all
     * bodies inside the simulation box
     *
     * @return Free volume of simulation box
     */
    public double getFreeVolume() {
        double tmpFreeVolume = this.volume;
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            tmpFreeVolume -= tmpBody.getVolume();
        }
        return tmpFreeVolume;
    }

    /**
     * Returns if the point is inside of the simulation box or on its surface
     *
     * @param aPoint Point which has to be tested
     * @return true if point is inside of the simulation box or on its surface,
     * false if point is outside of simulation box
     */
    public boolean isInContainer(IPointInSpace aPoint) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint == null) {
            return false;
        }
        // </editor-fold>
        return aPoint.getX() <= this.boxSize.getX() && aPoint.getY() <= this.boxSize.getY() && aPoint.getZ() <= this.boxSize.getZ();
    }

    /**
     * Tests if a point is outside of all of the bodies of the simulation box
     *
     * @param aPoint Point which has to be tested
     * @return true if point is outside of all bodies, false otherwise
     * @throws IllegalArgumentException if aPoint is null
     */
    public boolean isInFreeVolume(IPointInSpace aPoint) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint == null) {
            throw new IllegalArgumentException("aPoint is null.");
        }

        // </editor-fold>
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            if (tmpBody.isInVolume(aPoint)) {
                return false;
            }
        }
        for (BodySphere tmpExcludedSphere : this.excludedSphereList) {
            if (tmpExcludedSphere.isInVolume(aPoint)) {
                return false;
            }
        }
        return this.isInContainer(aPoint);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Out-of-box related methods">
    /**
     * Corrects coordinates of a body that is out of box
     *
     * @param aTestXCoordinate Test X coordinate
     * @param aTestYCoordinate Test Y coordinate
     * @param aTestZCoordinate Test Z coordinate
     * @param aBody BodyInterface object which has to be corrected
     * @throws IllegalArgumentException Thrown if body type is unknown
     */
    public void correctOutOfBox(double aTestXCoordinate, double aTestYCoordinate, double aTestZCoordinate, BodyInterface aBody) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        // Check of test coordinates is not necessary
        if (aBody == null) {
            throw new IllegalArgumentException("aBody was null");
        }

        // </editor-fold>
        // NOTE: tmpCorrectionFactor is necessary to avoid later out-of-box-errors due to roundoff-problems
        double tmpCorrectionFactor = 1.0 + ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR;
        switch (aBody.getBodyType()) {
            // <editor-fold defaultstate="collapsed" desc="SPHERE">
            case SPHERE:
                BodySphere tmpSphere = (BodySphere) aBody;
                // Initialize change detection
                aBody.getGeometryDataValueItem().initializeChangeDetection();

                if (aTestXCoordinate < tmpSphere.getRadius()) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpSphere.getRadius() * tmpCorrectionFactor), 0, 0);
                } else if (aTestXCoordinate + tmpSphere.getRadius() > this.boxSize.getX()) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(this.boxSize.getX() - tmpSphere.getRadius() * tmpCorrectionFactor), 0, 0);
                } else {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(aTestXCoordinate), 0, 0);
                }

                if (aTestYCoordinate < tmpSphere.getRadius()) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpSphere.getRadius() * tmpCorrectionFactor), 0, 1);
                } else if (aTestYCoordinate + tmpSphere.getRadius() > this.boxSize.getY()) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(this.boxSize.getY() - tmpSphere.getRadius() * tmpCorrectionFactor), 0, 1);
                } else {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(aTestYCoordinate), 0, 1);
                }

                if (aTestZCoordinate < tmpSphere.getRadius()) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpSphere.getRadius() * tmpCorrectionFactor), 0, 2);
                } else if (aTestZCoordinate + tmpSphere.getRadius() > this.boxSize.getZ()) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(this.boxSize.getZ() - tmpSphere.getRadius() * tmpCorrectionFactor), 0, 2);
                } else {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(aTestZCoordinate), 0, 2);
                }

                if (aBody.getGeometryDataValueItem().hasChangeDetected()) {
                    // Change occurred: Notify dependent value items
                    aBody.getGeometryDataValueItem().notifyDependentValueItemsForUpdate();
                }

                break;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="XY_LAYER">
            case XY_LAYER:
                BodyXyLayer tmpXyLayer = (BodyXyLayer) aBody;
                // Initialize change detection
                aBody.getGeometryDataValueItem().initializeChangeDetection();

                if (aTestXCoordinate < tmpXyLayer.getXLength() / 2.0) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpXyLayer.getXLength() / 2.0 * tmpCorrectionFactor), 0, 0);
                } else if (aTestXCoordinate + tmpXyLayer.getXLength() / 2.0 > this.boxSize.getX()) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(this.boxSize.getX() - tmpXyLayer.getXLength() / 2.0 * tmpCorrectionFactor), 0, 0);
                } else {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(aTestXCoordinate), 0, 0);
                }

                if (aTestYCoordinate < tmpXyLayer.getYLength() / 2.0) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpXyLayer.getYLength() / 2.0 * tmpCorrectionFactor), 0, 1);
                } else if (aTestYCoordinate + tmpXyLayer.getYLength() / 2.0 > this.boxSize.getY()) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(this.boxSize.getY() - tmpXyLayer.getYLength() / 2.0 * tmpCorrectionFactor), 0, 1);
                } else {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(aTestYCoordinate), 0, 1);
                }

                if (aTestZCoordinate < tmpXyLayer.getZLength() / 2.0) {
                    // Debug:
                    // System.out.println("aTestZCoordinate = " + String.valueOf(aTestZCoordinate));
                    // System.out.println("aTestZCoordinate < tmpXyLayer.getZLength() / 2.0 = " + String.valueOf(aTestZCoordinate < tmpXyLayer.getZLength() / 2.0));
                    // System.out.println("aTestZCoordinate - tmpXyLayer.getZLength() / 2.0 = " + String.valueOf(aTestZCoordinate - tmpXyLayer.getZLength() / 2.0));
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(tmpXyLayer.getZLength() / 2.0 * tmpCorrectionFactor), 0, 2);
                } else if (aTestZCoordinate + tmpXyLayer.getZLength() / 2.0 > this.boxSize.getZ()) {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(this.boxSize.getZ() - tmpXyLayer.getZLength() / 2.0 * tmpCorrectionFactor), 0, 2);
                } else {
                    aBody.getGeometryDataValueItem().setValue(String.valueOf(aTestZCoordinate), 0, 2);
                }

                if (aBody.getGeometryDataValueItem().hasChangeDetected()) {
                    // Change occurred: Notify dependent value items
                    aBody.getGeometryDataValueItem().notifyDependentValueItemsForUpdate();
                }

                break;

            // </editor-fold>
            default:
                throw new IllegalArgumentException("Body type is unknown.");
        }
    }

    /**
     * This method tests if an BodyInterface object peers out of the simulation
     * box
     *
     * @param aTestXCoordinate Test X coordinate
     * @param aTestYCoordinate Test Y coordinate
     * @param aTestZCoordinate Test Z coordinate
     * @param aBody BodyInterface object which has to be tested
     * @return true if aBody peers out of simulation box, false otherwise
     * @throws IllegalArgumentException Thrown if body type is unknown
     */
    public boolean isOutOfBox(double aTestXCoordinate, double aTestYCoordinate, double aTestZCoordinate, BodyInterface aBody) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTestXCoordinate < 0 || aTestYCoordinate < 0 || aTestZCoordinate < 0) {
            return true;
        }
        if (aBody == null) {
            throw new IllegalArgumentException("aBody was null");
        }

        // </editor-fold>
        switch (aBody.getBodyType()) {

            case SPHERE:
                BodySphere tmpSphere = (BodySphere) aBody;
                return aTestXCoordinate < tmpSphere.getRadius()
                        || aTestYCoordinate < tmpSphere.getRadius()
                        || aTestZCoordinate < tmpSphere.getRadius()
                        || aTestXCoordinate + tmpSphere.getRadius() > this.boxSize.getX()
                        || aTestYCoordinate + tmpSphere.getRadius() > this.boxSize.getY()
                        || aTestZCoordinate + tmpSphere.getRadius() > this.boxSize.getZ();

            case XY_LAYER:
                BodyXyLayer tmpXyLayer = (BodyXyLayer) aBody;

                // Debug:
                // System.out.println("isOutOfBox: aTestXCoordinate != tmpXyLayer.getBodyCenter().getX() = " + String.valueOf(aTestXCoordinate != tmpXyLayer.getBodyCenter().getX()));
                // System.out.println("isOutOfBox: aTestYCoordinate != tmpXyLayer.getBodyCenter().getY() = " + String.valueOf(aTestYCoordinate != tmpXyLayer.getBodyCenter().getY()));
                // System.out.println("aTestZCoordinate = " + String.valueOf(aTestZCoordinate));
                // System.out.println("isOutOfBox: aTestZCoordinate < tmpXyLayer.getZLength() / 2.0 = " + String.valueOf(aTestZCoordinate < tmpXyLayer.getZLength() / 2.0));
                // System.out.println("isOutOfBox: aTestZCoordinate - tmpXyLayer.getZLength() / 2.0 = " + String.valueOf(aTestZCoordinate - tmpXyLayer.getZLength() / 2.0));
                // System.out.println("isOutOfBox: aTestZCoordinate + tmpXyLayer.getZLength() / 2.0 > this.boxSize.getZ() = " + String.valueOf(aTestZCoordinate + tmpXyLayer.getZLength() / 2.0 > this.boxSize.getZ()));
                // System.out.println("isOutOfBox: aTestZCoordinate + tmpXyLayer.getZLength() / 2.0 - this.boxSize.getZ() = " + String.valueOf(aTestZCoordinate + tmpXyLayer.getZLength() / 2.0 - this.boxSize.getZ()));
                return aTestXCoordinate != tmpXyLayer.getBodyCenter().getX()
                        || aTestYCoordinate != tmpXyLayer.getBodyCenter().getY()
                        || aTestZCoordinate < tmpXyLayer.getZLength() / 2.0
                        || aTestZCoordinate + tmpXyLayer.getZLength() / 2.0 > this.boxSize.getZ();

            default:
                throw new IllegalArgumentException("Body type is unknown.");
        }
    }

    /**
     * This method tests if an BodyInterface object peers out of the simulation
     * box
     *
     * @param aBody BodyInterface object which has to be tested
     * @return true if aBody peers out of simulation box, false otherwise
     * @throws IllegalArgumentException Thrown if body type is unknown
     */
    public boolean isOutOfBox(BodyInterface aBody) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBody == null) {
            throw new IllegalArgumentException("aBody was null");
        }

        // </editor-fold>
        return this.isOutOfBox(aBody.getBodyCenter().getX(), aBody.getBodyCenter().getY(), aBody.getBodyCenter().getZ(), aBody);
    }

    /**
     * This method tests if a BodyInterface object that corresponds to
     * aValueItem peers out of the simulation box
     *
     * @param aValueItem Value item
     * @return True: A BodyInterface object that corresponds to aValueItem peers
     * out of simulation box, false: Otherwise
     */
    public boolean isOutOfBox(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return false;
        }

        // </editor-fold>
        BodyInterface tmpBody = this.getBody(aValueItem);
        if (tmpBody != null) {
            return this.isOutOfBox(tmpBody);
        } else {
            return false;
        }
    }

    /**
     * This method tests if a BodyInterface object that corresponds to
     * aValueItem is near top of the simulation box
     *
     * @param aValueItem Value item
     * @return True: A BodyInterface object that corresponds to aValueItem is
     * near top of the simulation box, false: Otherwise
     */
    public boolean isNearTopOfBox(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return false;
        }

        // </editor-fold>
        // NOTE: tmpCorrectionFactor is necessary to avoid later out-of-box-errors due to roundoff-problems
        double tmpCorrectionFactor = 1.0 + ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR;
        BodyInterface tmpBody = this.getBody(aValueItem);
        if (tmpBody != null) {
            double tmpOffset = this.boxSize.getZ() * (ModelDefinitions.FACTOR_FOR_GRAPHICS_NUMBER_CORRECTION - 1.0);
            switch (tmpBody.getBodyType()) {
                case SPHERE:
                    BodySphere tmpSphere = (BodySphere) tmpBody;
                    return tmpBody.getBodyCenter().getZ() + tmpSphere.getRadius() * tmpCorrectionFactor >= this.boxSize.getZ() - tmpOffset;

                case XY_LAYER:
                    BodyXyLayer tmpXyLayer = (BodyXyLayer) tmpBody;
                    return tmpBody.getBodyCenter().getZ() + tmpXyLayer.getZLength() / 2.0 * tmpCorrectionFactor >= this.boxSize.getZ() - tmpOffset;

                default:
                    throw new IllegalArgumentException("Body type is unknown.");
            }
        } else {
            return false;
        }
    }

    /**
     * This method tests if a BodyInterface object that corresponds to
     * aValueItem is near bottom of the simulation box
     *
     * @param aValueItem Value item
     * @return True: A BodyInterface object that corresponds to aValueItem is
     * near bottom of the simulation box, false: Otherwise
     */
    public boolean isNearBottomOfBox(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return false;
        }

        // </editor-fold>
        // NOTE: tmpCorrectionFactor is necessary to avoid later out-of-box-errors due to roundoff-problems
        double tmpCorrectionFactor = 1.0 + ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR;
        BodyInterface tmpBody = this.getBody(aValueItem);
        if (tmpBody != null) {
            double tmpOffset = this.boxSize.getZ() * (ModelDefinitions.FACTOR_FOR_GRAPHICS_NUMBER_CORRECTION - 1.0);
            switch (tmpBody.getBodyType()) {
                case SPHERE:
                    BodySphere tmpSphere = (BodySphere) tmpBody;
                    return tmpBody.getBodyCenter().getZ() - tmpSphere.getRadius() * tmpCorrectionFactor <= tmpOffset;

                case XY_LAYER:
                    BodyXyLayer tmpXyLayer = (BodyXyLayer) tmpBody;
                    return tmpBody.getBodyCenter().getZ() - tmpXyLayer.getZLength() / 2.0 * tmpCorrectionFactor <= tmpOffset;

                default:
                    throw new IllegalArgumentException("Body type is unknown.");
            }
        } else {
            return false;
        }
    }

    /**
     * This method moves a BodyInterface object that corresponds to aValueItem
     * to the top of the simulation box
     *
     * @param aValueItem Value item
     */
    public void moveToTopOfBox(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return;
        }

        // </editor-fold>
        // NOTE: tmpCorrectionFactor is necessary to avoid later out-of-box-errors due to roundoff-problems
        double tmpCorrectionFactor = 1.0 + ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR;
        BodyInterface tmpBody = this.getBody(aValueItem);
        if (tmpBody != null) {
            switch (tmpBody.getBodyType()) {
                case SPHERE:
                    BodySphere tmpSphere = (BodySphere) tmpBody;
                    tmpSphere.getGeometryDataValueItem().setValue(String.valueOf(this.boxSize.getZ() - tmpSphere.getRadius() * tmpCorrectionFactor), 0, 2);
                    break;

                case XY_LAYER:
                    BodyXyLayer tmpXyLayer = (BodyXyLayer) tmpBody;
                    tmpXyLayer.getGeometryDataValueItem().setValue(String.valueOf(this.boxSize.getZ() - tmpXyLayer.getZLength() / 2.0 * tmpCorrectionFactor), 0, 2);
                    break;

                default:
                    throw new IllegalArgumentException("Body type is unknown.");
            }
        }
    }

    /**
     * This method moves a BodyInterface object that corresponds to aValueItem
     * to the bottom of the simulation box
     *
     * @param aValueItem Value item
     */
    public void moveToBottomOfBox(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return;
        }

        // </editor-fold>
        // NOTE: tmpCorrectionFactor is necessary to avoid later out-of-box-errors due to roundoff-problems
        double tmpCorrectionFactor = 1.0 + ModelDefinitions.MINIMUM_COMPARTMENT_FACTOR;
        BodyInterface tmpBody = this.getBody(aValueItem);
        if (tmpBody != null) {
            switch (tmpBody.getBodyType()) {
                case SPHERE:
                    BodySphere tmpSphere = (BodySphere) tmpBody;
                    tmpSphere.getGeometryDataValueItem().setValue(String.valueOf(tmpSphere.getRadius() * tmpCorrectionFactor), 0, 2);
                    break;

                case XY_LAYER:
                    BodyXyLayer tmpXyLayer = (BodyXyLayer) tmpBody;
                    tmpXyLayer.getGeometryDataValueItem().setValue(String.valueOf(tmpXyLayer.getZLength() / 2.0 * tmpCorrectionFactor), 0, 2);
                    break;

                default:
                    throw new IllegalArgumentException("Body type is unknown.");
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Overlap related methods">
    /**
     * Returns if body overlaps with bodies of container
     *
     * @param aBody Body which has to be tested
     * @return True: Body overlaps with body of container, false: Otherwise
     */
    public boolean isOverlap(BodyInterface aBody) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBody == null) {
            return false;
        }
        // </editor-fold>
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            if (tmpBody != aBody && tmpBody.isOverlap(aBody)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if a BodyInterface object that corresponds to aValueItem overlaps
     * with bodies of container
     *
     * @param aValueItem Value item
     * @return True: A BodyInterface object that corresponds to aValueItem
     * overlaps with body of container, false: Otherwise
     */
    public boolean isOverlap(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return false;
        }

        // </editor-fold>
        BodyInterface tmpBody = this.getBody(aValueItem);
        if (tmpBody != null) {
            return this.isOverlap(tmpBody);
        } else {
            return false;
        }
    }

    /**
     * Returns bodies of container that overlap
     *
     * @param aBody Body which has to be tested
     * @return Bodies of container that overlap or null if none overlap
     */
    public LinkedList<BodyInterface> getBodiesWithOverlap(BodyInterface aBody) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBody == null) {
            return null;
        }

        // </editor-fold>
        LinkedList<BodyInterface> tmpOverlapBodyList = new LinkedList<BodyInterface>();
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            if (tmpBody != aBody && tmpBody.isOverlap(aBody)) {
                tmpOverlapBodyList.addLast(tmpBody);
            }
        }
        if (tmpOverlapBodyList.size() > 0) {
            return tmpOverlapBodyList;
        } else {
            return null;
        }
    }

    /**
     * Returns if bodies of container overlap
     *
     * @return True: At least two bodies of the container overlap, false:
     * Otherwise
     */
    public boolean hasOverlap() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.bodiesInContainer.isEmpty()) {
            return false;
        }
        // </editor-fold>
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            if (this.isOverlap(tmpBody)) {
                return true;
            }
        }
        return false;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Random value related methods">
    /**
     * Returns a random point in the free volume of the simulation box.
     *
     * @param aRandomNumberGenerator Random number generator
     * @return Random point in the free volume of the simulation box
     */
    public PointInSpace getRandomPointInFreeVolume(IRandom aRandomNumberGenerator) {
        this.pointInSpace1.setX(aRandomNumberGenerator.nextDouble() * this.boxSize.getX());
        this.pointInSpace1.setY(aRandomNumberGenerator.nextDouble() * this.boxSize.getY());
        this.pointInSpace1.setZ(aRandomNumberGenerator.nextDouble() * this.boxSize.getZ());
        int tmpCounter = 0;
        int tmpNumberOfTrials = Preferences.getInstance().getNumberOfTrialsForCompartment();
        while (!this.isInFreeVolume(this.pointInSpace1) && tmpCounter < tmpNumberOfTrials) {
            this.pointInSpace1.setX(aRandomNumberGenerator.nextDouble() * this.boxSize.getX());
            this.pointInSpace1.setY(aRandomNumberGenerator.nextDouble() * this.boxSize.getY());
            this.pointInSpace1.setZ(aRandomNumberGenerator.nextDouble() * this.boxSize.getZ());
            tmpCounter++;
        }
        return this.pointInSpace1.getClone();
    }

    /**
     * Returns a random graphical particle position in the free volume of the
     * simulation box.
     *
     * @param aRandomNumberGenerator Random number generator
     * @return Random graphical particle position in the free volume of the
     * simulation box
     */
    public GraphicalParticlePosition getRandomPositionInFreeVolume(IRandom aRandomNumberGenerator) {
        this.graphicalParticlePosition1.setX(aRandomNumberGenerator.nextDouble() * this.boxSize.getX());
        this.graphicalParticlePosition1.setY(aRandomNumberGenerator.nextDouble() * this.boxSize.getY());
        this.graphicalParticlePosition1.setZ(aRandomNumberGenerator.nextDouble() * this.boxSize.getZ());
        int tmpCounter = 0;
        int tmpNumberOfTrials = Preferences.getInstance().getNumberOfTrialsForCompartment();
        while (!this.isInFreeVolume(this.graphicalParticlePosition1) && tmpCounter < tmpNumberOfTrials) {
            this.graphicalParticlePosition1.setX(aRandomNumberGenerator.nextDouble() * this.boxSize.getX());
            this.graphicalParticlePosition1.setY(aRandomNumberGenerator.nextDouble() * this.boxSize.getY());
            this.graphicalParticlePosition1.setZ(aRandomNumberGenerator.nextDouble() * this.boxSize.getZ());
            tmpCounter++;
        }
        return this.graphicalParticlePosition1.getClone();
    }

    /**
     * Fills random points of free volume of the simulation box.
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aNumberOfTrials Number of trials for random point generation
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void fillFreeVolumeRandomPoints(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, int aNumberOfTrials, IRandom aRandomNumberGenerator) {
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
        int tmpIndex = aFirstIndex;
        // FIRST check if instance of GraphicalParticlePosition[]
        if (aBuffer instanceof GraphicalParticlePosition[]) {
            // <editor-fold defaultstate="collapsed" desc="aBuffer is an instance of GraphicalParticlePosition[]">
            for (int i = 0; i < aNumber; i++) {
                this.graphicalParticlePosition2.setX(aRandomNumberGenerator.nextDouble() * this.boxSize.getX());
                this.graphicalParticlePosition2.setY(aRandomNumberGenerator.nextDouble() * this.boxSize.getY());
                this.graphicalParticlePosition2.setZ(aRandomNumberGenerator.nextDouble() * this.boxSize.getZ());
                int tmpCounter = 0;
                while (!this.isInFreeVolume(this.graphicalParticlePosition2) && tmpCounter < aNumberOfTrials) {
                    this.graphicalParticlePosition2.setX(aRandomNumberGenerator.nextDouble() * this.boxSize.getX());
                    this.graphicalParticlePosition2.setY(aRandomNumberGenerator.nextDouble() * this.boxSize.getY());
                    this.graphicalParticlePosition2.setZ(aRandomNumberGenerator.nextDouble() * this.boxSize.getZ());
                    tmpCounter++;
                }
                aBuffer[tmpIndex++] = this.graphicalParticlePosition2.getClone();
            }
            // </editor-fold>
        } else if (aBuffer instanceof PointInSpace[]) {
            // <editor-fold defaultstate="collapsed" desc="aBuffer is an instance of PointInSpace[]">
            for (int i = 0; i < aNumber; i++) {
                this.pointInSpace2.setX(aRandomNumberGenerator.nextDouble() * this.boxSize.getX());
                this.pointInSpace2.setY(aRandomNumberGenerator.nextDouble() * this.boxSize.getY());
                this.pointInSpace2.setZ(aRandomNumberGenerator.nextDouble() * this.boxSize.getZ());
                int tmpCounter = 0;
                while (!this.isInFreeVolume(this.pointInSpace2) && tmpCounter < aNumberOfTrials) {
                    this.pointInSpace2.setX(aRandomNumberGenerator.nextDouble() * this.boxSize.getX());
                    this.pointInSpace2.setY(aRandomNumberGenerator.nextDouble() * this.boxSize.getY());
                    this.pointInSpace2.setZ(aRandomNumberGenerator.nextDouble() * this.boxSize.getZ());
                    tmpCounter++;
                }
                aBuffer[tmpIndex++] = this.pointInSpace2.getClone();
            }
            // </editor-fold>
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Random spheres related methods">
    /**
     * Returns list with specified number of non-overlapping spheres at random
     * positions that do NOT overlap with existing bodies in container or 
     * excluded spheres.
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
        PointInSpace tmpBodyCenter = new PointInSpace(
            this.boxSize.getX() / 2.0,
            this.boxSize.getY() / 2.0,
            this.boxSize.getZ() / 2.0
        );
        LinkedList<BodySphere> tmpSphereList = 
            this.graphicsUtilityMethods.getNonOverlappingRandomSpheresIntoCompartmentBox(
                this.bodiesInContainer,
                this.excludedSphereList, 
                tmpBodyCenter, 
                this.boxSize.getX(), 
                this.boxSize.getY(), 
                this.boxSize.getZ(), 
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
    // <editor-fold defaultstate="collapsed" desc="- Selection related methods">
    /**
     * Deselects selected body
     */
    public void deselectBody() {
        if (this.selectedBody != null) {
            // NOTE: The following sequence may not be changed since the setSelected() method makes notification calls
            // 1. Rescue selected body
            BodyInterface tmpSelectedBody = this.selectedBody;
            // 2. Remove selected body from box
            this.selectedBody = null;
            // 3. Deselect selected body
            tmpSelectedBody.setSelected(false);
        }
    }

    /**
     * Selects first body for point or deselects all bodies if there is no body
     *
     * @param aPoint Point
     */
    public void selectBody(PointInSpace aPoint) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPoint == null) {
            return;
        }

        // </editor-fold>
        this.selectBody(aPoint.getX(), aPoint.getY(), aPoint.getZ());
    }

    /**
     * Selects first body for point or deselects all bodies if there is no body
     *
     * @param aXCoordinate X coordinate
     * @param aYCoordinate Y coordinate
     * @param aZCoordinate Z coordinate
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public void selectBody(double aXCoordinate, double aYCoordinate, double aZCoordinate) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXCoordinate < 0 || aYCoordinate < 0 || aZCoordinate < 0) {
            throw new IllegalArgumentException("At least one of the coordinates was negative.");
        }
        // </editor-fold>
        double tmpThirdDimensionValue = -1.0;
        switch (this.boxView) {
            case XZ_FRONT:
            case XZ_BACK:
                tmpThirdDimensionValue = aYCoordinate;
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                tmpThirdDimensionValue = aXCoordinate;
                break;
            case XY_TOP:
            case XY_BOTTOM:
                tmpThirdDimensionValue = aZCoordinate;
                break;
            default:
                // This should never happen
                return;
        }
        LinkedList<BodyInterface> tmpBodyList = this.getBodiesForDisplay(tmpThirdDimensionValue);
        if (tmpBodyList == null) {
            this.deselectBody();
        } else {
            // NOTE: Iterate descending (since tmpBodyList is sorted from "back" to "front"
            for (Iterator<BodyInterface> e = tmpBodyList.descendingIterator(); e.hasNext();) {
                BodyInterface tmpBody = e.next();
                switch (this.boxView) {
                    case XZ_FRONT:
                    case XZ_BACK:
                        this.pointInPlane.setX(aXCoordinate);
                        this.pointInPlane.setY(aZCoordinate);
                        if (tmpBody.getShapeInBoxView().isInShapeArea(this.pointInPlane)) {
                            this.selectBody(tmpBody);
                            return;
                        }
                        break;
                    case YZ_LEFT:
                    case YZ_RIGHT:
                        this.pointInPlane.setX(aYCoordinate);
                        this.pointInPlane.setY(aZCoordinate);
                        if (tmpBody.getShapeInBoxView().isInShapeArea(this.pointInPlane)) {
                            this.selectBody(tmpBody);
                            return;
                        }
                        break;
                    case XY_TOP:
                    case XY_BOTTOM:
                        this.pointInPlane.setX(aXCoordinate);
                        this.pointInPlane.setY(aYCoordinate);
                        if (tmpBody.getShapeInBoxView().isInShapeArea(this.pointInPlane)) {
                            this.selectBody(tmpBody);
                            return;
                        }
                        break;
                    default:
                        // This should never happen
                        return;
                }
            }
            this.deselectBody();
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Bodies for display related methods">
    /**
     * Returns shapes from "back" to "front" in current box view up to
     * aThirdDimensionValue
     *
     * @param aThirdDimensionValue 3rd dimension value
     * @return Shapes from "back" to "front" in current box view up to
     * aThirdDimensionValue or null if none exist
     */
    public LinkedList<BodyInterface> getBodiesForDisplay(double aThirdDimensionValue) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.bodiesInContainer.isEmpty()) {
            return null;
        }
        if (aThirdDimensionValue == this.bodiesThirdDimensionValue && this.bodyCacheList != null) {
            return this.bodyCacheList;
        }
        // </editor-fold>
        LinkedList<BodyInterface> tmpBodyList = new LinkedList<BodyInterface>();
        for (BodyInterface tmpBody : this.bodiesInContainer) {
            switch (this.boxView) {
                case XZ_FRONT:
                    if (tmpBody.getBodyCenter().getY() >= aThirdDimensionValue) {
                        tmpBodyList.add(tmpBody);
                    }
                    break;
                case XZ_BACK:
                    if (tmpBody.getBodyCenter().getY() <= aThirdDimensionValue) {
                        tmpBodyList.add(tmpBody);
                    }
                    break;
                case YZ_LEFT:
                    if (tmpBody.getBodyCenter().getX() >= aThirdDimensionValue) {
                        tmpBodyList.add(tmpBody);
                    }
                    break;
                case YZ_RIGHT:
                    if (tmpBody.getBodyCenter().getX() <= aThirdDimensionValue) {
                        tmpBodyList.add(tmpBody);
                    }
                    break;
                case XY_TOP:
                    if (tmpBody.getBodyCenter().getZ() <= aThirdDimensionValue) {
                        tmpBodyList.add(tmpBody);
                    }
                    break;
                case XY_BOTTOM:
                    if (tmpBody.getBodyCenter().getZ() >= aThirdDimensionValue) {
                        tmpBodyList.add(tmpBody);
                    }
                    break;
            }
        }
        if (tmpBodyList.size() > 0) {
            Collections.sort(tmpBodyList);
            // NOTE: Reverse since bodies "at back" must be drawn first
            Collections.reverse(tmpBodyList);
            this.bodyCacheList = tmpBodyList;
            this.bodiesThirdDimensionValue = aThirdDimensionValue;
            return this.bodyCacheList;
        } else {
            return null;
        }
    }

    /**
     * Returns shapes from "back" to "front" in current box view. NOTE:
     * this.thirdDimensionValue must be set in correct manner.
     *
     * @return Shapes from "back" to "front" in current box view or null if none
     * exist
     */
    public LinkedList<BodyInterface> getBodiesForDisplay() {
        return this.getBodiesForDisplay(this.thirdDimensionValue);
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Returns the bodies in the simulation box
     *
     * @return Bodies in the simulation box
     */
    public ArrayList<BodyInterface> getBodies() {
        return this.bodiesInContainer;
    }

    /**
     * Box size info
     *
     * @return Box size info
     */
    public BoxSizeInfo getBoxSizeInfo() {
        return this.boxSizeInfo;
    }

    /**
     * Selected body
     *
     * @return Selected body
     */
    public BodyInterface getSelectedBody() {
        return this.selectedBody;
    }

    /**
     * Returns the volume of the simulation box
     *
     * @return Volume of the simulation box
     */
    public double getVolume() {
        return this.volume;
    }

    /**
     * Returns the length of the edge along the x axis
     *
     * @return Length of the edge along the x axis
     */
    public double getXLength() {
        return this.boxSize.getX();
    }

    /**
     * Returns the length of the edge along the y axis
     *
     * @return Length of the edge along the y axis
     */
    public double getYLength() {
        return this.boxSize.getY();
    }

    /**
     * Returns the length of the edge along the z axis
     *
     * @return Length of the edge along the z axis
     */
    public double getZLength() {
        return this.boxSize.getZ();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="Miscellaneous">
    /**
     * Box view
     *
     * @return Box view
     */
    public SimulationBoxViewEnum getBoxView() {
        return this.boxView;
    }

    /**
     * Box view
     *
     * @param aBoxView Box view
     */
    public void setBoxView(SimulationBoxViewEnum aBoxView) {
        if (this.boxView != aBoxView) {
            this.clearBodiesCache();
            this.boxView = aBoxView;
            // IMPORTANT: Set this box view in all bodies
            for (BodyInterface tmpBody : this.bodiesInContainer) {
                // NOTE: Suppress notification of each body ...
                tmpBody.setNotificationSuppressed(true);
                tmpBody.setBoxView(this.boxView);
                tmpBody.setNotificationSuppressed(false);
            }
            // ... and notify once
            super.notifyChangeReceiver(this, this.changeInformation);
        }
    }

    /**
     * Third dimension value
     *
     * @return Third dimension value
     */
    public double getThirdDimensionValue() {
        return this.thirdDimensionValue;
    }

    /**
     * Third dimension value
     *
     * @param aValue Third dimension value
     */
    public void setThirdDimensionValue(double aValue) {
        if (this.thirdDimensionValue != aValue) {
            this.thirdDimensionValue = aValue;
            // Update color shape attenuation
            this.setColorShapeAttenuation();
            super.notifyChangeReceiver(this, this.changeInformation);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ExcludedSphereList">
    /**
     * List with excluded spheres
     *
     * @param anExcludedSphereList List with excluded spheres
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public void setExcludedSphereList(LinkedList<BodySphere> anExcludedSphereList) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anExcludedSphereList == null) {
            throw new IllegalArgumentException("CompartmentBox.setExcludedSphereList: anExcludedSphereList is null.");
        }
        // </editor-fold>
        this.excludedSphereList = anExcludedSphereList;
    }

    /**
     * List with excluded spheres
     *
     * @return List with excluded spheres
     */
    public LinkedList<BodySphere> getExcludedSphereList() {
        return this.excludedSphereList;
    }

    /**
     * Clears list with excluded spheres
     */
    public void clearExcludedSphereList() {
        this.excludedSphereList.clear();
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Clears bodies cache related variables
     */
    private void clearBodiesCache() {
        if (bodyCacheList != null) {
            this.bodyCacheList = null;
            this.bodiesThirdDimensionValue = -1.0;
        }
    }

    /**
     * Calculates box volume
     */
    private void calculateVolume() {
        this.volume = this.boxSize.getX() * this.boxSize.getY() * this.boxSize.getZ();
    }

    /**
     * Selects body
     *
     * @param aBody Body
     */
    private void selectBody(BodyInterface aBody) {
        if (this.selectedBody != aBody) {
            this.deselectBody();
            if (!aBody.isSelected()) {
                aBody.setSelected(true);
            }
            this.selectedBody = aBody;
        }
    }
    // </editor-fold>

}
