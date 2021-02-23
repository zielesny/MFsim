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
package de.gnwi.mfsim.model.graphics.point;
/**
 * This class represents a point in a plane. It is defined by its x and y coordinate.
 *
 * @author Achim Zielesny
 */
public class PointInPlane {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Coordinate of the point on the horizontal axis.
     */
    private double xCoordinate;
    /**
     * Coordinate of the point on the vertical axis.
     */
    private double yCoordinate;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor with the x and y coordinate of the point as parameters
     *
     * @param aXCoordinate Coordinate of the point on the horizontal axis
     * @param aYCoordinate Coordinate of the point on the vertical axis
     */
    public PointInPlane(double aXCoordinate, double aYCoordinate) {
        this.xCoordinate = aXCoordinate;
        this.yCoordinate = aYCoordinate;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns if aPoint is equal to this point
     *
     * @param aPoint Point
     * @return True: aPoint is equal to this point, false: Otherwise
     */
    public boolean isEqual(PointInPlane aPoint) {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aPoint == null) {
            return false;
        }

        // </editor-fold>

        return this.xCoordinate == aPoint.getX() && this.yCoordinate == aPoint.getY();
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Gets coordinate of the point on the horizontal axis
     *
     * @return Coordinate of the point on the horizontal axis
     */
    public double getX() {
        return this.xCoordinate;
    }

    /**
     * Sets coordinate of the point on the horizontal axis
     *
     * @param aXCoordinate Coordinate of the point on the horizontal axis
     */
    public void setX(double aXCoordinate) {
        this.xCoordinate = aXCoordinate;
    }

    /**
     * Gets coordinate of the point on the vertical axis
     *
     * @return Coordinate of the point on the vertical axis
     */
    public double getY() {
        return this.yCoordinate;
    }

    /**
     * Sets coordinate of the point on the vertical axis
     *
     * @param aYCoordinate Coordinate of the point on the vertical axis
     */
    public void setY(double aYCoordinate) {
        this.yCoordinate = aYCoordinate;
    }
    // </editor-fold>
}