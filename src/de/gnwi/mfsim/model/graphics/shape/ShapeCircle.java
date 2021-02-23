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
package de.gnwi.mfsim.model.graphics.shape;

import de.gnwi.mfsim.model.graphics.point.PointInPlane;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.graphics.body.BodyInterface;

/**
 * This class represents a circle. The circle is defined by its center and radius.
 *
 * @author Achim Zielesny
 */
public class ShapeCircle implements ShapeInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Attenuation for box position (0.0: Minimum, 1.0: Maximum)
     */
    private double attenuation;
    /**
     * BodyInterface object from which the ShapeInterface object is derived
     */
    private BodyInterface body;
    /**
     * Area of circle
     */
    private double circleArea;
    /**
     * X ratio for display on panel
     */
    private double correctedXRatio;
    /**
     * Y ratio for display on panel
     */
    private double correctedYRatio;
    /**
     * Radius X ratio for display on panel
     */
    private double correctedRadiusXRatio;
    /**
     * Radius of the circle
     */
    private double radius;
    /**
     * Center of the circle
     */
    private PointInPlane shapeCenter;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     *
     * @param aPoint Center of circle
     * @param aRadius Radius of circle, not allowed to be less than 0
     * @param aBody BodyInterface object from which the ShapeInterface object is derived
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ShapeCircle(PointInPlane aPoint, double aRadius, BodyInterface aBody)
            throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aBody == null) {
            throw new IllegalArgumentException("aBody is null.");
        }
        if (aRadius < 0) {
            throw new IllegalArgumentException("aRadius was less than 0.");
        }
        if (aPoint == null) {
            throw new IllegalArgumentException("aPoint was null");
        }

        // </editor-fold>

        this.shapeCenter = aPoint;
        this.body = aBody;
        this.radius = aRadius;
        this.attenuation = 0.0;
        this.correctedXRatio = -1.0;
        this.correctedYRatio = -1.0;
        this.correctedRadiusXRatio = -1.0;
        this.calculateArea();
    }

    /**
     * Constructor
     *
     * @param aXCoordinate Coordinate of the center on the horizontal axis
     * @param aYCoordinate Coordinate of the center on the vertical axis
     * @param aRadius Radius of circle, not allowed to be less than 0
     * @param aBody BodyInterface object from which the ShapeInterface object is derived
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ShapeCircle(double aXCoordinate, double aYCoordinate,
            double aRadius, BodyInterface aBody)
            throws IllegalArgumentException {
        this(new PointInPlane(aXCoordinate, aYCoordinate), aRadius, aBody);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns if a PointInPlane is within the area of the ShapeInterface object
     *
     * @param aPoint PointInPlane which has to be tested
     * @return true if aPoint is within the area of the ShapeInterface object, false otherwise
     */
    public boolean isInShapeArea(PointInPlane aPoint) {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aPoint == null) {
            return false;
        }

        // </editor-fold>

        return GraphicsUtils.getDistanceInPlane(this.shapeCenter, aPoint) <= this.radius;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Properties (get only)">
    /**
     * Return the BodyInterface object from which the ShapeInterface object is derived
     *
     * @return BodyInterface object from which the ShapeInterface object is derived
     */
    public BodyInterface getBody() {
        return this.body;
    }

    /**
     * Return the type of the shape
     *
     * @return Type of the shape
     */
    public ShapeTypeEnum getShapeType() {
        return ShapeTypeEnum.CIRCLE;
    }

    /**
     * Returns area of circle
     *
     * @return Area of circle
     */
    public double getShapeArea() {
        return this.circleArea;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- Attenuation">
    /**
     * Attenuation for box position
     *
     * @return Attenuation for box position (0.0: Minimum, 1.0: Maximum)
     */
    public double getAttenuation() {
        return this.attenuation;
    }

    /**
     * Attenuation for box position. An invalid value is corrected to minimum (0.0) or maximum (1.0).
     *
     * @param aValue Attenuation for box position (0.0: Minimum, 1.0: Maximum)
     */
    public void setAttenuation(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        // NOTE: Do NOT throw an exception if aValue is invalid but correct.

        if (aValue < 0.0) {
            this.attenuation = 0.0;
            return;
        }
        if (aValue > 1.0) {
            this.attenuation = 1.0;
            return;
        }

        // </editor-fold>

        if (this.attenuation != aValue) {
            this.attenuation = aValue;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CorrectedXRatio">
    /**
     * X ratio for display on panel
     *
     * @param aValue X ratio for display on panel
     */
    public void setCorrectedXRatio(double aValue) {
        if (this.correctedXRatio != aValue) {
            this.correctedXRatio = aValue;
        }
    }

    /**
     * X ratio for display on panel
     *
     * @return X ratio for display on panel
     */
    public double getCorrectedXRatio() {
        return this.correctedXRatio;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- CorrectedYRatio">
    /**
     * Y ratio for display on panel
     *
     * @param aValue Y ratio for display on panel
     */
    public void setCorrectedYRatio(double aValue) {
        if (this.correctedYRatio != aValue) {
            this.correctedYRatio = aValue;
        }
    }

    /**
     * Y ratio for display on panel
     *
     * @return Y ratio for display on panel
     */
    public double getCorrectedYRatio() {
        return this.correctedYRatio;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- CorrectedRadiusXRatio">
    /**
     * Radius X ratio for display on panel
     *
     * @param aValue Radius X ratio for display on panel
     */
    public void setCorrectedRadiusXRatio(double aValue) {
        if (this.correctedRadiusXRatio != aValue) {
            this.correctedRadiusXRatio = aValue;
        }
    }

    /**
     * Radius X ratio for display on panel
     *
     * @return Radius X ratio for display on panel
     */
    public double getCorrectedRadiusXRatio() {
        return this.correctedRadiusXRatio;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Radius">
    /**
     * Radius of the circle
     *
     * @param aRadius Radius of the circle
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public void setRadius(double aRadius) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aRadius < 0) {
            throw new IllegalArgumentException("aRadius was less than 0.");
        }

        // </editor-fold>

        if (this.radius != aRadius) {
            this.radius = aRadius;
            this.calculateArea();
        }
    }

    /**
     * Gets the radius of the circle
     *
     * @return Radius of the circle
     */
    public double getRadius() {
        return this.radius;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- ShapeCenter">
    /**
     * Center of the circle
     *
     * @param aPoint Center of the circle
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public void setShapeCenter(PointInPlane aPoint)
            throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aPoint == null) {
            throw new IllegalArgumentException("aPoint was null");
        }

        // </editor-fold>

        if (!this.shapeCenter.isEqual(aPoint)) {
            this.shapeCenter = aPoint;
        }
    }

    /**
     * Returns the center of the circle
     *
     * @return Center of the circle
     */
    public PointInPlane getShapeCenter() {
        return this.shapeCenter;
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Calculates area
     */
    private void calculateArea() {
        this.circleArea = Math.PI * this.radius * this.radius;
    }
    // </editor-fold>
}