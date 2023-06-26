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
package de.gnwi.mfsim.model.peptide.utils.configuration;

import java.io.Serializable;

/**
 * A class representing the entity "3 dimensional Point". 
 * Data format is double.
 * @author Ziv Yaniv
 */
public class Point3D extends Point implements Serializable {
    /**
     * Create a point with all entries initialized to zero.
     */
    public Point3D() {
    super(3);
    }

    /**
     * Create a point with all entries initialized to the given value.
     * @param val The value all point entries.
     */
    public Point3D(double val) {
    super(3,val);
    }

    /**
     * Create a point according to the given double array.
     * @param data The point will contain entries as specified by the given
     *             array. The array size must be three.
     */
    public Point3D(double[] data) {
    super(3);
    if(data.length != 3)
        throw new IllegalArgumentException("The data length must"+
                           " be three.");
    System.arraycopy(data,0,this.data,0,data.length);
    }

    /**
     * Copy constructor, create a point according to the given point.
     * @param p Create a copy of this point.
     */
    public Point3D(Point3D p) {
    super(p);
    }

    /**
     * Create a point according to the given coordinates.
     * @param x Point's x coordinate.
     * @param y Point's y coordinate.
     * @param z Point's z coordinate.
     */
    public Point3D(double x, double y, double z) {
    super(3);
    data[0] = x;
    data[1] = y;
    data[2] = z;
    }
    /**
     * Set the point's coordinates to the given coordinates.
     * @param x Point's new x coordinate.
     * @param y Point's new y coordinate.
     * @param z Point's new z coordinate.
     */
    public void set(double x, double y, double z) {
    data[0] = x;
    data[1] = y;
    data[2] = z;
    }
}
