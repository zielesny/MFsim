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

import java.util.Arrays;
import java.io.Serializable;

/**
 * A class representing the abstract mathematical entity 
 * "n dimenssional Tuple".
 * Data format is double.
 * @author Ziv Yaniv
 */
public class Tuple implements Serializable {
    /** 
     * The tuple data is stored in an array which is accesible from outside 
     * the class, for greater speed.
     */
    public double []data;
    
    /**
     * Create a tuple of the given size, with all entries initialized to zero.
     * @param n The dimensionality of the tuple.
     */
    public Tuple(int n) {
    data = new double[n];
    }

    /**
     * Create a tuple of the given size, with all entries initialized to the
     * given value.
     * @param n The dimensionality of the tuple.
     * @param val The value all tuple entries.
     */
    public Tuple(int n, double val) {
    if(n<=0) 
        throw new IllegalArgumentException("Tuple size must be"+
                           "positive");
    data = new double[n];
    Arrays.fill(data,val);
    }

    /**
     * Create a tuple according to the given double array.
     * @param data The tuple will contain entries as specified by the given
     *             array.
     */
    public Tuple(double[] data) {
    this.data = new double[data.length];
    System.arraycopy(data,0,this.data,0,data.length);
    }

    /**
     * Copy constructor, create a tuple according to the given tuple.
     * @param t Create a copy of this tuple.
     */
    public Tuple(Tuple t) {
    data = new double[t.data.length];
    System.arraycopy(t.data,0,data,0,data.length);
    }
    
    /**
     * Get the dimensionality of this tuple.
     * @return Returns the dimensionality of the tuple.
     */
    public int getDimension() {
    return data.length;
    }

    /**
     * Set the data in this tuple to be the same as the given tuple.
     * Tuple sizes must match.
     * @param t Set the contents of this tuple to be the same as the given
     *          tuple.
     */
    public void set(Tuple t) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    System.arraycopy(t.data,0,data,0,data.length);
    }

    /**
     * Set the data in this tuple to be the same as the given array.
     * Array and tuple sizes must match.
     * @param data Set the contents of this tuple to be the same as the given
     *             array.
     */
    public void set(double[] data) {
    if(data.length != this.data.length)
        throw new IllegalArgumentException("Array must have same "+
                           "dimenssion as tuple");
    System.arraycopy(data,0,this.data,0,data.length);
    }
    
    /**
     * Set all entries of this tuple to the given value.
     * @param val All tuple entries are set to this value.
     */
    public void set(double val) {
    Arrays.fill(data,val);
    }

    /**
     * Add the given tuple (entry by entry) to this tuple (this+t).
     * Tuple sizes must match.
     * @param t The tuple whose entries are added to this tuple's entries.
     */ 
    public void add(Tuple t) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i]+=t.data[i];
    }

    /**
     * Add the two tuples (entry by entry) and set the entries of
     * this tuple to be the result (t1+t2).
     * All tuple sizes must match.
     * @param t1 The first tuple used in the addition.
     * @param t2 The second tuple used in the addition.
     */ 
    public void add(Tuple t1, Tuple t2) {
    if(t1.data.length != data.length || 
       t2.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i]=t1.data[i] + t2.data[i];
    }

    /**
     * Subtract the given tuple (entry by entry) from this tuple (this-t).
     * Tuple sizes must match.
     * @param t The tuple whose entries are subtracted from this tuple's 
     *          entries.
     */ 
    public void sub(Tuple t) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i]-=t.data[i];
    }

    /**
     * Subtract the two tuples (entry by entry) and set the entries of
     * this tuple to be the result (t1-t2).
     * All tuple sizes must match.
     * @param t1 The first tuple used in the subtraction.
     * @param t2 The second tuple used in the subtraction.
     */ 
    public void sub(Tuple t1, Tuple t2) {
    if(t1.data.length != data.length || 
       t2.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i]=t1.data[i] - t2.data[i];
    }

    /**
     * Multiply the given tuple (entry by entry) with this tuple (this*t).
     * Tuple sizes must match.
     * @param t The tuple whose entries are multiplied with this tuple's 
     *          entries.
     */ 
    public void mul(Tuple t) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i]*=t.data[i];
    }

    /**
     * Multiply the two tuples (entry by entry) and set the entries of
     * this tuple to be the result (t1*t2).
     * All tuple sizes must match.
     * @param t1 The first tuple used in the multiplication.
     * @param t2 The second tuple used in the multiplication.
     */ 
    public void mul(Tuple t1, Tuple t2) {
    if(t1.data.length != data.length || 
       t2.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i]=t1.data[i] * t2.data[i];
    }

    /**
     * Divide this tuple by the given tuple (entry by entry) (this/t).
     * Tuple sizes must match.
     * @param t The tuple whose entries we divide by.
     */ 
    public void div(Tuple t) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i]/=t.data[i];
    }

    /**
     * Divide the two tuples (entry by entry) and set the entries of
     * this tuple to be the result (t1/t2).
     * All tuple sizes must match.
     * @param t1 The first tuple used in the division.
     * @param t2 The second tuple used in the division.
     */ 
    public void div(Tuple t1, Tuple t2) {
    if(t1.data.length != data.length || 
       t2.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i]=t1.data[i] / t2.data[i];
    }

    /**
     * Clamp the entries of this tuple to be in the interval [min,max].
     * @param min All entries in the tuple will be greater or equal to this
     *            value.
     * @param max All entries in the tuple will be less or equal to this
     *            value.
     */ 
    public void clamp(double min, double max) {
    for(int i=0; i<data.length; i++) {
        if(data[i] < min)
        data[i] = min;
        else if(data[i] > max)
        data[i] = max;
    }
    }

    /**
     * Clamp the entries of the given tuple to be in the interval [min,max]
     * and set this tuple to be the result.
     * Tuple sizes must match.
     * @param min All entries in the tuple will be greater or equal to this
     *            value.
     * @param max All entries in the tuple will be less or equal to this
     *            value.
     * @param t The tuple we clamp and set this tuple to.
     */ 
    public void clamp(double min, double max, Tuple t) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<t.data.length; i++) {
        if(t.data[i] < min)
        data[i] = min;
        else if(t.data[i] > max)
        data[i] = max;
        else
        data[i] = t.data[i];
    }
    }

    /**
     * Maximal entry in tuple.
     * @return Returns the maximal entry in this tuple.
     */
    public double max() {
    double max = data[0];
    for(int i=1; i<data.length; i++) 
        if(data[i] > max)
        max = data[i];
    return max;
    }

    /**
     * Minimal entry in tuple.
     * @return Returns the maximal entry in this tuple.
     */
    public double min() {
    double min = data[0];
    for(int i=1; i<data.length; i++) 
        if(data[i] < min)
        min = data[i];
    return min;
    }

    /**
     * Negate all entries in this tuple.
     */
    public void negate() {
    for(int i=0; i<data.length; i++)
        data[i] = -data[i];
    }

    /**
     * Set this tuple to the negative of the given tuple.
     * Tuple sizes must match.
     * 
     * @param t Tuple
     */
    public void negate(Tuple t) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i] = -t.data[i];
    }
    
    /**
     * Multiply all entries in the tuple by the given scalar.
     * @param s All tuple entries are multiplied by this value.
     */
    public void scale(double s) {
    for(int i=0; i<data.length; i++)
        data[i] *= s;
    }
    
    /**
     * Multiply all entries in the given tuple by the given scalar and
     * set this tuple to the result.
     * Tuple sizes must match.
     * @param s All the given tuple entries are multiplied by this value.
     * @param t Tuple which whose scaled value we set this tuple to.
     */
    public void scale(double s, Tuple t) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    for(int i=0; i<data.length; i++)
        data[i] = s*t.data[i];
    }

    /**
     * Set this tuple to be the linear interpolation between this tuple and
     * the given tuple ( alpha*t + (1-alpha)*this ).
     * Tuple sizes must match.
     * @param t The tuple we interpolate with.
     * @param alpha Interpolation parameter.
     */
    public void interpolate(Tuple t, double alpha) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    double oneMinusAlpha = 1-alpha;
    for(int i=0; i<data.length; i++)
        data[i] = oneMinusAlpha*data[i] + alpha*t.data[i];
    }

    /**
     * Set this tuple to be the linear interpolation between the two given
     * tuples ( alpha*t2 + (1-alpha)*t1 ).
     * All tuple sizes must match.
     * @param t1 The first tuple we interpolate with.
     * @param t2 The second tuple we interpolate with.
     * @param alpha Interpolation parameter.
     */
    public void interpolate(Tuple t1, Tuple t2, double alpha) {
    if(t1.data.length != data.length || 
       t2.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    double oneMinusAlpha = 1-alpha;
    for(int i=0; i<data.length; i++)
        data[i] = oneMinusAlpha*t1.data[i] + alpha*t2.data[i];
    }
    
    /**
     * Compare the given tuple with this tuple using the L infinity norm
     * (max(|this(i) - t(i)|) smaller epsilon ).
     * Tuple sizes must match and the epsilon value must be nonnegative. 
     * @param t The tuple we compare.
     * @param epsilon The L infinity norm of the difference between tuples is compared to this value.
     * @return True: Smaller epsilon, false: Otherwise
     */
    public boolean equals(Tuple t, double epsilon) {
    if(t.data.length != data.length)
        throw new IllegalArgumentException("Tuples must have same "+
                           "dimenssion");
    if(epsilon < 0)
        throw new IllegalArgumentException("Given epsilon value " +
                                               "must be nonnegative, got " +
                           epsilon + ".");
    for(int i=0; i<data.length; i++) {
        double val = data[i] - t.data[i];
        if(Math.abs(val) > epsilon)
        return false;
    }
    return true;
    }
    
    /**
     * The string representation of a tuple.
     * @return The string representation of the tuple, "[e1,e2,...,eN]".
     */
    public String toString() {
    int i;
    StringBuffer buff = new StringBuffer("[");
    int end = data.length - 1; 
    for(i=0; i<end; i++) 
        buff.append(data[i]+",");
    buff.append(data[i]+"]");
    return buff.toString();
    }
}
