/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2023  Achim Zielesny (achim.zielesny@googlemail.com)
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
import de.gnwi.mfsim.model.changeNotification.ChangeNotifierInterface;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentBox;
import de.gnwi.mfsim.model.graphics.shape.ShapeInterface;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.spices.IPointInSpace;
import de.gnwi.jdpd.interfaces.IRandom;

/**
 * Interface for bodies
 *
 * @author Achim Zielesny
 */
public interface BodyInterface extends Comparable<BodyInterface>, ChangeNotifierInterface {

    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns if a point in space is inside a body
     *
     * @param aPoint Point which is tested
     * @return True if point is inside body, false if point is on surface or
     * outside of body
     */
    boolean isInVolume(IPointInSpace aPoint);

    /**
     * Returns if a body overlaps with this instance
     *
     * @param aBody aBody
     * @return True: Body overlaps with this instance, false: Otherwise
     */
    boolean isOverlap(BodyInterface aBody);

    /**
     * Returns a random point in space inside the body
     *
     * @param aRandomNumberGenerator Random number generator
     * @return Point in space inside the body
     */
    PointInSpace getRandomPointInVolume(IRandom aRandomNumberGenerator);

    /**
     * Returns aNumber random points in space inside the body
     *
     * @param aNumber Number of random points
     * @param aRandomNumberGenerator Random number generator
     * @return Points in space inside the body
     */
    PointInSpace[] getRandomPointsInVolume(int aNumber, IRandom aRandomNumberGenerator);

    /**
     * Returns a random point on the surface of body
     *
     * @param aRandomNumberGenerator Random number generator
     * @return Point on surface of body
     */
    PointInSpace getRandomPointOnSurface(IRandom aRandomNumberGenerator);

    /**
     * Returns aNumber random points on the surface of body
     *
     * @param aNumber Number of random points
     * @param aRandomNumberGenerator Random number generator
     * @return Points on surface of body
     */
    PointInSpace[] getRandomPointsOnSurface(int aNumber, IRandom aRandomNumberGenerator);

    /**
     * Fills aNumber random points inside the body into buffer.
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    void fillRandomPointsInVolume(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator);

    /**
     * Fills aNumber random points inside the body into buffers.
     *
     * @param aBuffer1 Buffer 1
     * @param aBuffer2 Buffer 2
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points for both buffers (not allowed to
     * be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    void fillRandomPointsInVolume(IPointInSpace[] aBuffer1, IPointInSpace[] aBuffer2, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator);

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
    void fillRandomPointsInVolumeWithExcludingSpheres(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, int aNumberOfTrials, IRandom aRandomNumberGenerator);

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
    void fillRandomPointsInVolumeWithExcludingSpheres(IPointInSpace[] aBuffer1, IPointInSpace[] aBuffer2, int aFirstIndex, int aNumber, double aStepDistance, int aNumberOfTrials, IRandom aRandomNumberGenerator);

    /**
     * Fills aNumber random points on the surface of the body into buffer
     *
     * @param aBuffer Buffer
     * @param aFirstIndex First index in buffer
     * @param aNumber Number of random points (not allowed to be less than 1)
     * @param aRandomNumberGenerator Random number generator
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    void fillRandomPointsOnSurface(IPointInSpace[] aBuffer, int aFirstIndex, int aNumber, IRandom aRandomNumberGenerator);

    /**
     * Returns shape in current box view
     *
     * @return Shape in current box view
     */
    ShapeInterface getShapeInBoxView();

    /**
     * Updates shape attenuation
     */
    void updateColorShapeAttenuation();

    /**
     * Returns clone of body with the same size and position
     *
     * @return Clone of body with the same size and position
     */
    BodyInterface getSizeClone();
    
    /**
     * Clears list with excluded spheres if possible
     */
    void clearExcludedSphereList();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Gets the geometric form of the body
     *
     * @return Type of the body
     */
    BodyTypeEnum getBodyType();

    /**
     * Gets the center of the body
     *
     * @return Center of the body
     */
    PointInSpace getBodyCenter();

    /**
     * Box view
     *
     * @param aBoxView Box view
     */
    void setBoxView(SimulationBoxViewEnum aBoxView);

    /**
     * Box view
     *
     * @return Box view
     */
    SimulationBoxViewEnum getBoxView();

    /**
     * Gets the volume of the body
     *
     * @return Volume of the body
     */
    double getVolume();

    /**
     * Returns if the body is selected
     *
     * @return true if this body is selected, false if body is not selected
     */
    boolean isSelected();

    /**
     * Sets if the body is selected
     *
     * @param aValue true to select body, false to deselect body
     */
    void setSelected(boolean aValue);

    /**
     * Simulation box
     *
     * @param aSimulationBox Simulation box
     */
    void setCompartmentBox(CompartmentBox aSimulationBox);

    /**
     * Simulation box
     *
     * @return Simulation box
     */
    CompartmentBox getCompartmentBox();

    /**
     * Geometry data value item
     *
     * @return Geometry data value item
     */
    ValueItem getGeometryDataValueItem();
    // </editor-fold>

}
