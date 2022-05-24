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
 * This class represents a 2D box. The box is defined by its side lengths x an y.
 * 
 * @author Achim Zielesny
 */
public class ShapeBox implements ShapeInterface {

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
	 * Area of box
	 */
	private double boxArea;

	/**
	 * X ratio for display on panel
	 */
	private double correctedXRatio;

	/**
	 * Y ratio for display on panel
	 */
	private double correctedYRatio;

	/**
	 * X length ratio for display on panel
	 */
	private double correctedXLengthRatio;

	/**
	 * Y length ratio for display on panel
	 */
	private double correctedYLengthRatio;

	/**
	 * x-length of box
	 */
	private double xLength;

	/**
	 * y-length of box
	 */
	private double yLength;

	/**
	 * Center of the box
	 */
	private PointInPlane shapeCenter;

	// </editor-fold>

	//

	// <editor-fold defaultstate="collapsed" desc="Constructors">

	/**
	 * Constructor
	 * 
	 * @param aPoint
	 *            Center of box
	 * @param aXLength
	 *            x-length of box, not allowed to be less than 0
	 * @param aYLength
	 *            y-length of box, not allowed to be less than 0
	 * @param aBody
	 *            BodyInterface object from which the ShapeInterface object is derived
	 * @throws IllegalArgumentException
	 *             Thrown if an argument is illegal
	 */
	public ShapeBox(PointInPlane aPoint, double aXLength, double aYLength, BodyInterface aBody) throws IllegalArgumentException {

		// <editor-fold defaultstate="collapsed" desc="Checks">

		if (aBody == null) {
			throw new IllegalArgumentException("aBody is null.");
		}
		if (aXLength < 0) {
			throw new IllegalArgumentException("aXLength was less than 0.");
		}
		if (aYLength < 0) {
			throw new IllegalArgumentException("aYLength was less than 0.");
		}
		if (aPoint == null) {
			throw new IllegalArgumentException("aPoint was null");
		}

		// </editor-fold>

		this.shapeCenter = aPoint;
		this.body = aBody;
		this.xLength = aXLength;
		this.yLength = aYLength;
		this.attenuation = 0.0;
		this.correctedXRatio = -1.0;
		this.correctedYRatio = -1.0;
		this.correctedXLengthRatio = -1.0;
		this.correctedYLengthRatio = -1.0;
		this.calculateArea();
	}

	/**
	 * Constructor
	 * 
	 * @param aXCoordinate
	 *            Coordinate of the center on the horizontal axis
	 * @param aYCoordinate
	 *            Coordinate of the center on the vertical axis
	 * @param aXLength
	 *            x-length of box, not allowed to be less than 0
	 * @param aYLength
	 *            y-length of box, not allowed to be less than 0
	 * @param aBody
	 *            BodyInterface object from which the ShapeInterface object is derived
	 * @throws IllegalArgumentException
	 *             Thrown if an argument is illegal
	 */
	public ShapeBox(double aXCoordinate, double aYCoordinate, double aXLength, double aYLength, BodyInterface aBody) throws IllegalArgumentException {
		this(new PointInPlane(aXCoordinate, aYCoordinate), aXLength, aYLength, aBody);
	}

	// </editor-fold>

	//

	// <editor-fold defaultstate="collapsed" desc="Public methods">

	/**
	 * Returns if a PointInPlane is within the area of the ShapeInterface object
	 * 
	 * @param aPoint
	 *            PointInPlane which has to be tested
	 * @return true if aPoint is within the area of the ShapeInterface object, false otherwise
	 */
	public boolean isInShapeArea(PointInPlane aPoint) {

		// <editor-fold defaultstate="collapsed" desc="Checks">

		if (aPoint == null) {
			return false;
		}

		// </editor-fold>

		double tmpHalfXLength = this.xLength / 2.0;
		double tmpHalfYLength = this.yLength / 2.0;
		return this.shapeCenter.getX() - tmpHalfXLength < aPoint.getX() && this.shapeCenter.getX() + tmpHalfXLength > aPoint.getX() && this.shapeCenter.getY() - tmpHalfYLength < aPoint.getY()
				&& this.shapeCenter.getY() + tmpHalfYLength > aPoint.getY();
	}

	// </editor-fold>s

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
		return ShapeTypeEnum.BOX;
	}

	/**
	 * Returns area of box
	 * 
	 * @return Area of box
	 */
	public double getShapeArea() {
		return this.boxArea;
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
	 * @param aValue
	 *            Attenuation for box position (0.0: Minimum, 1.0: Maximum)
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
	 * @param aValue
	 *            X ratio for display on panel
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

	// <editor-fold defaultstate="collapsed" desc="- CorrectedYRatio">

	/**
	 * Y ratio for display on panel
	 * 
	 * @param aValue
	 *            Y ratio for display on panel
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

	// <editor-fold defaultstate="collapsed" desc="- CorrectedXLengthRatio">

	/**
	 * X length ratio for display on panel
	 * 
	 * @param aValue
	 *            X length ratio for display on panel
	 */
	public void setCorrectedXLengthRatio(double aValue) {
		if (this.correctedXLengthRatio != aValue) {
			this.correctedXLengthRatio = aValue;
		}
	}

	/**
	 * X length ratio for display on panel
	 * 
	 * @return X length ratio for display on panel
	 */
	public double getCorrectedXLengthRatio() {
		return this.correctedXLengthRatio;
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="- CorrectedYLengthRatio">

	/**
	 * Y length ratio for display on panel
	 * 
	 * @param aValue
	 *            Y length ratio for display on panel
	 */
	public void setCorrectedYLengthRatio(double aValue) {
		if (this.correctedYLengthRatio != aValue) {
			this.correctedYLengthRatio = aValue;
		}
	}

	/**
	 * Y length ratio for display on panel
	 * 
	 * @return Y length ratio for display on panel
	 */
	public double getCorrectedYLengthRatio() {
		return this.correctedYLengthRatio;
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="- XLength">

	/**
	 * X-length
	 * 
	 * @param aValue
	 *            Value for x-length
	 * @throws IllegalArgumentException
	 *             Thrown if an argument is illegal
	 */
	public void setXLength(double aValue) throws IllegalArgumentException {

		// <editor-fold defaultstate="collapsed" desc="Checks">

		if (aValue < 0) {
			throw new IllegalArgumentException("aValue was less than 0.");
		}

		// </editor-fold>

		if (this.xLength != aValue) {
			this.xLength = aValue;
			this.calculateArea();
		}
	}

	/**
	 * X-length
	 * 
	 * @return X-length of box
	 */
	public double getXLength() {
		return this.xLength;
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="- YLength">

	/**
	 * Y-length
	 * 
	 * @param aValue
	 *            Value for y-length
	 * @throws IllegalArgumentException
	 *             Thrown if an argument is illegal
	 */
	public void setYLength(double aValue) throws IllegalArgumentException {

		// <editor-fold defaultstate="collapsed" desc="Checks">

		if (aValue < 0) {
			throw new IllegalArgumentException("aValue was less than 0.");
		}

		// </editor-fold>

		if (this.yLength != aValue) {
			this.yLength = aValue;
			this.calculateArea();
		}
	}

	/**
	 * Y-length
	 * 
	 * @return Y-length of box
	 */
	public double getYLength() {
		return this.yLength;
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="- ShapeCenter">

	/**
	 * Center of the box
	 * 
	 * @param aPoint
	 *            Center of the box
	 * @throws IllegalArgumentException
	 *             Thrown if an argument is illegal
	 */
	public void setShapeCenter(PointInPlane aPoint) throws IllegalArgumentException {

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
	 * Returns the center of the box
	 * 
	 * @return Center of the box
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
		this.boxArea = this.xLength * this.yLength;
	}

	// </editor-fold>

}
