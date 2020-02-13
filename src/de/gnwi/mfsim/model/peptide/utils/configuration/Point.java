/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2020  Achim Zielesny (achim.zielesny@googlemail.com)
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
 * A class representing the abstract entity "n dimensional point". 
 * Data format is double.
 * @author Ziv Yaniv
 */
public class Point extends Tuple implements Serializable {
    /**
     * Create a point of the given size, with all entries initialized to zero.
     * @param n The dimensionality of the point.
     */
    public Point(int n) {
    super(n);
    }

    /**
     * Create a point of the given size, with all entries initialized to the
     * given value.
     * @param n The dimensionality of the point.
     * @param val The value all point entries.
     */
    public Point(int n, double val) {
    super(n,val);
    }

    /**
     * Create a point according to the given double array.
     * @param data The point will contain entries as specified by the given
     *             array.
     */
    public Point(double[] data) {
    super(data);
    }

    /**
     * Copy constructor, create a point according to the given point.
     * @param p Create a copy of this point.
     */
    public Point(Point p) {
    super(p);
    }
    
    /**
     * Create a point according to the given tuple.
     * @param t Create the point according to this tuple.
     * @see Tuple
     */
    public Point(Tuple t) {
    super(t);
    }
    
    /**
     * Compute the distance between the points using the L2 norm,
     * ( sqrt(sum((this(i) - p(i))^2)) ).
     * @param p Compute the distance to this point.
     * @return Returns the L2 norm of the distance between the two points.
     */
    public double distanceL2(Point p) {
    if(p.data.length != data.length)
        throw new IllegalArgumentException("Points must have same "+
                           "dimenssion");
    double sumSquared = 0;
    for(int i=0; i<data.length; i++) {
        double tmp = data[i] - p.data[i];
        sumSquared += (tmp*tmp); 
    }
    return Math.sqrt(sumSquared);
    } 

    /**
     * Compute the distance between the points using the L2 norm without
     * taking the square root, ( sum((this(i) - p(i))^2) ).
     * @param p Compute the distance to this point.
     * @return Returns the square of the L2 norm of the distance 
     *         between the two points.
     */
    public double distanceL2Squared(Point p) {
    if(p.data.length != data.length)
        throw new IllegalArgumentException("Points must have same "+
                           "dimenssion");
    double sumSquared = 0;
    for(int i=0; i<data.length; i++) {
        double tmp = data[i] - p.data[i];
        sumSquared += (tmp*tmp);
    }
    return sumSquared;
    } 

    /**
     * Compute the distance between the points using the L1 norm,
     * ( sum(|this(i) - p(i)|) ).
     * @param p Compute the distance to this point.
     * @return Returns the L1 norm of the distance between the two points.
     */
    public double distanceL1(Point p) {
    if(p.data.length != data.length)
        throw new IllegalArgumentException("Points must have same "+
                           "dimenssion");
    double sumAbs = 0;
    for(int i=0; i<data.length; i++)
        sumAbs += Math.abs(data[i] - p.data[i]);
    return sumAbs;
   } 

    /**
     * Compute the distance between the points using the Linfinity norm,
     * ( max(|this(i) - p(i)|) ).
     * @param p Compute the distance to this point.
     * @return Returns the Linfinity norm of the distance between 
     *         the two points.
     */
    public double distanceLinf(Point p) {
    if(p.data.length != data.length)
        throw new IllegalArgumentException("Points must have same "+
                           "dimenssion");
    double maxAbs = Math.abs(data[0] - p.data[0]);
    for(int i=1; i<data.length; i++) {
        double abs = Math.abs(data[i] - p.data[i]);
        if(abs > maxAbs)
        maxAbs = abs;
    }
    return maxAbs;
    } 
}
