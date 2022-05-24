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
package de.gnwi.mfsim.model.graphics.shape;

import de.gnwi.mfsim.model.graphics.point.PointInPlane;
import de.gnwi.mfsim.model.graphics.body.BodyInterface;

/**
 * Interface for shapes
 * 
 * @author Achim Zielesny
 */
public interface ShapeInterface {

    /**
     * Returns the body from which the ShapeInterface object is derived
     * 
     * @return Body from which the ShapeInterface object is derived
     */
    BodyInterface getBody();

    /**
     * Returns the center point of the shape
     * 
     * @return Center point of the shape
     */
    PointInPlane getShapeCenter();

    /**
     * Returns the type of the shape
     * 
     * @return Type of the shape
     */
    ShapeTypeEnum getShapeType();

    /**
     * Returns the area of shape
     * 
     * @return Area of shape
     */
    double getShapeArea();

    /**
     * Returns if a PointInPlane is within the area of the ShapeInterface object
     * 
     * @param aPoint
     *            PointInPlane which has to be tested
     * @return True: aPoint is within the area of the ShapeInterface object,
     *         false: Otherwise
     */
    boolean isInShapeArea(PointInPlane aPoint);

    /**
     * Attenuation for box position
     * 
     * @return Attenuation for box position (0.0: Minimum, 1.0: Maximum)
     */
    double getAttenuation();

    /**
     * Attenuation for box position
     * 
     * @param aValue
     *            Attenuation for box position (0.0: Minimum, 1.0: Maximum)
     */
    void setAttenuation(double aValue);
}