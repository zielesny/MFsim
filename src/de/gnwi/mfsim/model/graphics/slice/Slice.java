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
package de.gnwi.mfsim.model.graphics.slice;
/**
 * Slice with a start index and an end index
 *
 * @author Stefan Neumann, Achim Zielesny
 */
public class Slice {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * The start index
     */
    private int startIndex;

    /**
     * The end index.
     */
    private int endIndex;

    /**
     * The start value of the slice in the box
     */
    private double startValue;
    
    /**
     * The end value of the slice in the box
     */
    private double endValue;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public Slice() {
        // Initialise with impossible index value -1
        this.startIndex = -1;
        this.endIndex = -1;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns if valid start and end index is defined.
     * 
     * @return True: Valid start and end index is defined, false: Otherwise.
     */
    public boolean hasValidStartAndEndIndex() {
        // Compare constructor
        return this.startIndex != -1 && this.endIndex != -1;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- StartIndex">
    /**
     * Gets the start index of the range.
     *
     * @return The start index of the range.
     */
    public int getStartIndex() {
        return this.startIndex;
    }

    /**
     * Sets the start index of the range.
     *
     * @param aStartIndex The start index of the range.
     */
    public void setStartIndex(int aStartIndex) {
        this.startIndex = aStartIndex;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- EndIndex">
    /**
     * Gets the end index of the range.
     *
     * @return The end index of the range.
     */
    public int getEndIndex() {
        return this.endIndex;
    }

    /**
     * Sets the end index of the range.
     *
     * @param anEndIndex The end index of the range.
     */
    public void setEndIndex(int anEndIndex) {
        this.endIndex = anEndIndex;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- StartValue">
    /**
     * Gets the start value of the slice in the box
     *
     * @return The start value of the slice in the box
     */
    public double getStartValue() {
        return this.startValue;
    }

    /**
     * Sets the start value of the slice in the box
     *
     * @param aValue The start value of the slice in the box
     */
    public void setStartValue(double aValue) {
        this.startValue = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- EndValue">
    /**
     * Gets the end value of the slice in the box
     *
     * @return The end value of the slice in the box
     */
    public double getEndValue() {
        return this.endValue;
    }

    /**
     * Sets the end value of the slice in the box
     *
     * @param aValue The end value of the slice in the box
     */
    public void setEndValue(double aValue) {
        this.endValue = aValue;
    }
    // </editor-fold>
    // </editor-fold>

}
