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
package de.gnwi.mfsim.model.graphics.point;

import de.gnwi.spices.PointInSpace;

/**
 * This class represents a point in space. It is defined by its x, y and z coordinate.
 * 
 * @author Achim Zielesny
 */
public class PointInSpaceInteger {

	// <editor-fold defaultstate="collapsed" desc="Private class variables">

	/**
	 * X coordinate of the point in space
	 */
	private int xCoordinate;

	/**
	 * Y coordinate of the point in space
	 */
	private int yCoordinate;

	/**
	 * Z coordinate of the point in space
	 */
	private int zCoordinate;

	// </editor-fold>

	//

	// <editor-fold defaultstate="collapsed" desc="Constructors">

	/**
	 * Constructor
	 */
	public PointInSpaceInteger() {
		this.xCoordinate = 0;
		this.yCoordinate = 0;
		this.zCoordinate = 0;
	}

	/**
	 * Constructor, creates a copy of aSpacePoint
	 * 
	 * @param aSpacePoint
	 *            PointInSpace which delivers the coordinates of the new PointInSpace
	 * @throws IllegalArgumentException
	 *             if aSpacePoint is null
	 */
	public PointInSpaceInteger(PointInSpace aSpacePoint) throws IllegalArgumentException {

		// <editor-fold defaultstate="collapsed" desc="Checks">

		if (aSpacePoint == null) {
			throw new IllegalArgumentException("aSpacePoint was null");
		}

		// </editor-fold>

		this.xCoordinate = (int) aSpacePoint.getX();
		this.yCoordinate = (int) aSpacePoint.getY();
		this.zCoordinate = (int) aSpacePoint.getZ();
	}

	/**
	 * Constructor with x, y and z coordinate of point as parameters
	 * 
	 * @param aXCoordinate
	 *            X coordinate of point in space
	 * @param aYCoordinate
	 *            Y coordinate of point in space
	 * @param aZCoordinate
	 *            Z coordinate of point in space
	 */
	public PointInSpaceInteger(int aXCoordinate, int aYCoordinate, int aZCoordinate) {
		this.xCoordinate = aXCoordinate;
		this.yCoordinate = aYCoordinate;
		this.zCoordinate = aZCoordinate;
	}

	// </editor-fold>

	//

	// <editor-fold defaultstate="collapsed" desc="Public methods">

	/**
	 * Returns if aPoint is equal to this point
	 * 
	 * @param aPoint
	 *            Point
	 * @return True: aPoint is equal to this point, false: Otherwise
	 */
	public boolean isEqual(PointInSpace aPoint) {

		// <editor-fold defaultstate="collapsed" desc="Checks">

		if (aPoint == null) {
			return false;
		}

		// </editor-fold>

		return this.xCoordinate == aPoint.getX() && this.yCoordinate == aPoint.getY() && this.zCoordinate == aPoint.getZ();
	}

	/**
	 * Returns clone of this instance
	 * 
	 * @return Clone of this instance
	 */
	public PointInSpaceInteger getClonedPointInSpaceInteger() {
		return new PointInSpaceInteger(this.xCoordinate, this.yCoordinate, this.zCoordinate);
	}

	// </editor-fold>

	//

	// <editor-fold defaultstate="collapsed" desc="Public properties">

	/**
	 * Gets x coordinate of point in space
	 * 
	 * @return X coordinate of point in space
	 */
	public int getX() {
		return this.xCoordinate;
	}

	/**
	 * Sets x coordinate of point in space
	 * 
	 * @param aXCoordinate
	 *            X coordinate of the point in space
	 */
	public void setX(int aXCoordinate) {
		this.xCoordinate = aXCoordinate;
	}

	/**
	 * Gets y coordinate of point in space
	 * 
	 * @return Y coordinate of point in space
	 */
	public int getY() {
		return this.yCoordinate;
	}

	/**
	 * Sets y coordinate of point in space
	 * 
	 * @param aYCoordinate
	 *            Y coordinate of the point in space
	 */
	public void setY(int aYCoordinate) {
		this.yCoordinate = aYCoordinate;
	}

	/**
	 * Gets z coordinate of point in space
	 * 
	 * @return Z coordinate of point in space
	 */
	public int getZ() {
		return this.zCoordinate;
	}

	/**
	 * Sets z coordinate of point in space
	 * 
	 * @param aZCoordinate
	 *            Z coordinate of the point in space
	 */
	public void setZ(int aZCoordinate) {
		this.zCoordinate = aZCoordinate;
	}

	// </editor-fold>

}