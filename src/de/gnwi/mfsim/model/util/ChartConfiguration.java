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
package de.gnwi.mfsim.model.util;

/**
 * Configurations for charts
 * 
 * @author Jan-Mathis Hein, Achim Zielesny
 */
public class ChartConfiguration {
    
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: The last point of the chart is marked, false: It is not
     */
    private boolean hasLastPointMarked;
    
    /**
     * True: Show the trend line for the chart data, false: Don't
     */
    private boolean hasTrendLine;

    /**
     * True: The fill color of the shapes in the chart is white, false: The fill
     * color of the shapes in the chart is black
     */
    private boolean isFillColorWhite;
    
    /**
     * True: The colors of the chart are inverted, false: They are not 
     */
    private boolean isInverted;

    /**
     * True: The outline paint of the shapes in the chart is white, false: The 
     * outline paint is not used
     */
    private boolean isOutlinePaintWhite;

    /**
     * True: Shapes are painted, false: Shapes are not painted
     */
    private boolean isShapePaint;

    /**
     * True: The chart has thick lines, false: The chart has thin lines
     */
    private boolean isThickLines;
    
    /**
     * True: There are zoom values for the chart, false: There are not
     */
    private boolean hasZoom;
    
    /**
     * The zoom values for the chart {xMin, xMax, yMin, yMax}
     */
    private double[] zoomValues;
    
    /**
     * Number of points that will be initially discarded from the chart data
     */
    private int numberOfDiscardedInitialPoints;

    /**
     * Number of y-values of the chart data to average
     */
    private int numberToAverage;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates a ChartConfiguration object with default configurations
     */
    public ChartConfiguration() {
        this.setToDefault();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Sets all the configuration for charts to their default state
     */
    public void setToDefault() {
        this.hasLastPointMarked = false;
        this.hasTrendLine = false;
        this.isFillColorWhite = false;
        this.isInverted = false;
        this.isOutlinePaintWhite = true;
        this.isShapePaint = true;
        this.isThickLines = false;
        this.numberOfDiscardedInitialPoints = 0;
        this.numberToAverage = 1;
        this.removeZoomValues();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- Averaging">
    /**
     * Returns number of y-values of the chart data to average
     * 
     * @return Number of y-values of the chart data to average
     */
    public int getNumberToAverage() {
        return numberToAverage;
    }
    
    /**
     * Sets number of y-values of the chart data to average
     * 
     * @param aNumberToAverage Number of y-values of the chart data to average
     */
    public void setNumberToAverage(int aNumberToAverage) {
        this.numberToAverage = aNumberToAverage;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Discarded points">
    /**
     * Returns number of points that will be initially discarded from the chart 
     * data
     * 
     * @return Number of points that will be initially discarded from the chart 
     * data
     */
    public int getNumberOfDiscardedInitialPoints() {
        return numberOfDiscardedInitialPoints;
    }
    
    /**
     * Sets number of points that will be initially discarded from the chart 
     * data
     * 
     * @param aNumberOfDiscardedInitialPoints Number of points that will be 
     * initially discarded from the chart data
     */
    public void setNumberOfDiscardedInitialPoints(int aNumberOfDiscardedInitialPoints) {
        this.numberOfDiscardedInitialPoints = aNumberOfDiscardedInitialPoints;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Look of the chart">
    /**
     * True: The last point of the chart is marked, false: It is not
     * 
     * @return True: The last point of the chart is marked, false: It is not
     */
    public boolean getHasLastPointMarked() {
        return hasLastPointMarked;
    }
    
    /**
     * True: The last point of the chart is marked, false: It is not
     * 
     * @param aHasLastPointMarked True: The last point of the chart is marked, 
     * false: It is not
     */
    public void setHasLastPointMarked(boolean aHasLastPointMarked) {
        this.hasLastPointMarked = aHasLastPointMarked;
    }

    /**
     * True: The fill color of the shapes in the chart is white, false: The fill
     * color of the shapes in the chart is black
     * 
     * @return True: The fill color of the shapes in the chart is white, 
     * false: The fill color of the shapes in the chart is black
     */
    public boolean getIsFillColorWhite() {
        return isFillColorWhite;
    }

    /**
     * True: The fill color of the shapes in the chart is white, false: The fill
     * color of the shapes in the chart is black
     * 
     * @param anIsFillColorWhite True: The fill color of the shapes in the chart
     * is white, false: The fill color of the shapes in the chart is black
     */
    public void setIsFillColorWhite(boolean anIsFillColorWhite) {
        this.isFillColorWhite = anIsFillColorWhite;
    }

    /**
     * True: The colors of the chart are inverted, false: They are not 
     * 
     * @return True: The colors of the chart are inverted, false: They are not 
     */
    public boolean getIsInverted() {
        return isInverted;
    }

    /**
     * True: The colors of the chart are inverted, false: They are not 
     * 
     * @param anIsInverted True: The colors of the chart are inverted, 
     * false: They are not 
     */
    public void setIsInverted(boolean anIsInverted) {
        this.isInverted = anIsInverted;
    }

    /**
     * True: The outline paint of the shapes in the chart is white, false: The 
     * outline paint is not used
     * 
     * @return True: The outline paint of the shapes in the chart is white, 
     * false: The outline paint is not used
     */
    public boolean getIsOutlinePaintWhite() {
        return isOutlinePaintWhite;
    }

    /**
     * True: The outline paint of the shapes in the chart is white, false: The 
     * outline paint is not used
     * 
     * @param anIsOutlinePaintWhite True: The outline paint of the shapes in the
     * chart is white, false: The outline paint is not used
     */
    public void setIsOutlinePaintWhite(boolean anIsOutlinePaintWhite) {
        this.isOutlinePaintWhite = anIsOutlinePaintWhite;
    }

    /**
     * True: Shapes are painted, false: Shapes are not painted
     * 
     * @return True: Shapes are painted, false: Shapes are not painted
     */
    public boolean getIsShapePaint() {
        return isShapePaint;
    }

    /**
     * True: Shapes are painted, false: Shapes are not painted
     * 
     * @param anIsShapePaint True: Shapes are painted, 
     * false: Shapes are not painted
     */
    public void setIsShapePaint(boolean anIsShapePaint) {
        this.isShapePaint = anIsShapePaint;
    }

    /**
     * True: The chart has thick lines, false: The chart has thin lines
     * 
     * @return True: The chart has thick lines, false: The chart has thin lines
     */
    public boolean getIsThickLines() {
        return isThickLines;
    }

    /**
     * True: The chart has thick lines, false: The chart has thin lines
     * 
     * @param anIsThickLines True: The chart has thick lines, 
     * false: The chart has thin lines
     */
    public void setIsThickLines(boolean anIsThickLines) {
        this.isThickLines = anIsThickLines;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Trend line">
    /**
     * True: Show the trend line for the chart data, false: Don't
     * 
     * @return True: Show the trend line for the chart data, false: Don't
     */
    public boolean getHasTrendLine() {
        return hasTrendLine;
    }

    /**
     * True: Show the trend line for the chart data, false: Don't
     * 
     * @param aHasTrendLine True: Show the trend line for the chart data, 
     * false: Don't
     */
    public void setHasTrendLine(boolean aHasTrendLine) {
        this.hasTrendLine = aHasTrendLine;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Zoom">
    /**
     * True: There are zoom values for the chart, false: There are not
     * 
     * @return True: There are zoom values for the chart, false: There are not
     */
    public boolean hasZoom() {
        return this.hasZoom;
    }

    /**
     * Returns the zoom values for the chart {xMin, xMax, yMin, yMax}
     * 
     * @return The zoom values for the chart {xMin, xMax, yMin, yMax}
     */
    public double[] getZoomValues() {
        return this.zoomValues;
    }

    /**
     * Sets the zoom values for the chart {xMin, xMax, yMin, yMax}
     * 
     * @param aZoomValues The zoom values for the chart {xMin, xMax, yMin, yMax}
     */
    public void setZoomValues(double[] aZoomValues) {
        this.zoomValues = aZoomValues;
        this.hasZoom = true;
    }
    
    /**
     * Removes zoom values;
     */
    public void removeZoomValues() {
        this.zoomValues = new double[] {-Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE};
        this.hasZoom = false;
    }
    // </editor-fold>
    // </editor-fold>
    
}
