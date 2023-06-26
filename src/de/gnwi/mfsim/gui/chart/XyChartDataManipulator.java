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
package de.gnwi.mfsim.gui.chart;

import de.gnwi.mfsim.model.util.ModelUtils;
import java.util.LinkedList;
import org.jfree.data.xy.XYSeries;

/**
 * Manipulator for xy-data
 * 
 * @author Jan-Mathis Hein, Achim Zielesny (safeguard additions)
 */
public class XyChartDataManipulator {

    // <editor-fold defaultstate="collapsed" desc="Private final static class variables">
    /**
     * Tiny threshold
     */
    private static final double TINY_THRESHOLD = 1E-6;
    
    /**
     * Name for data series
     */
    private static final String DATA_SERIES_NAME = "XyData";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Updated data (zoomValues and numberOfDiscardedInitialPoints are taken in 
     * to consideration)
     */
    private LinkedList<double[]> updatedXyPairsList;
    
    /**
     * True: The zoom values have been set, false: They have not
     */
    private boolean isZoom;
    
    /**
     * Array with the data of the LinkedList updatedXyPairsList
     */
    private double[][] updatedXyPairsArray;
    
    /**
     * Data boundaries of the updated data {xMin, xMax, yMin, yMax}
     */
    private double[] updatedDataBoundaries;
    
    /**
     * Zoom values for the updated data {xMin, xMax, yMin, yMax}
     */
    private double[] zoomValues;
    
    /**
     * Original data
     */
    private final LinkedList<Double[]> originalXyPairs;
    
    /**
     * Number of points to be initially discarded for the updated data
     */
    private int numberOfDiscardedInitialPoints;
    
    /**
     * Number of points in updated data set
     */
    private int numberOfPointsInUpdatedData;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public XyChartDataManipulator() {
        this.originalXyPairs = new LinkedList<>();
        this.isZoom = false;
        this.numberOfDiscardedInitialPoints = 0;
        this.zoomValues = new double[] {-Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE};
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Get">
    /**
     * Returns the zoom values {xMin, xMax, yMin, yMax}
     * 
     * @return The zoom values{xMin, xMax, yMin, yMax}
     */
    public double[] getZoomValues() {
        return this.zoomValues;
    }
    
    /**
     * Returns the number of discarded initial points
     * 
     * @return The number of discarded initial points
     */
    public int getNumberOfDiscardedInitialPoints() {
        return this.numberOfDiscardedInitialPoints;
    }
    
    /**
     * Returns the range of the updated data plus some offsets
     * {xMin, xMax, yMin, yMax}
     * 
     * @return The range of the updated data plus some offsets
     * {xMin, xMax, yMin, yMax}
     */
    public double[] getChartRange() {
        double[] tmpChartRange = new double[4];
        double tmpXoffset;
        if (Math.abs(this.updatedDataBoundaries[1] - this.updatedDataBoundaries[0]) < this.updatedDataBoundaries[1] * XyChartDataManipulator.TINY_THRESHOLD) {
            tmpXoffset = (this.updatedDataBoundaries[1] + this.updatedDataBoundaries[0]) * 0.5 * 0.05;
        } else {
            tmpXoffset = (this.updatedDataBoundaries[1] - this.updatedDataBoundaries[0]) * 0.05;
        }
        if (tmpXoffset == 0) {
            // The offset can't be zero so it is set to an arbitrary default of 0.1
            tmpXoffset = 0.1;
        }
        tmpChartRange[0] = this.updatedDataBoundaries[0] - tmpXoffset;
        tmpChartRange[1] = this.updatedDataBoundaries[1] + tmpXoffset;
        double tmpYoffset;
        if (Math.abs(this.updatedDataBoundaries[3] - this.updatedDataBoundaries[2]) < this.updatedDataBoundaries[3] * XyChartDataManipulator.TINY_THRESHOLD) {
            tmpYoffset = (this.updatedDataBoundaries[3] + this.updatedDataBoundaries[2]) * 0.5 * 0.05;
        } else {
            tmpYoffset = (this.updatedDataBoundaries[3] - this.updatedDataBoundaries[2]) * 0.05;
        }
        if (tmpYoffset == 0) {
            // The offset can't be zero so it is set to an arbitrary default of 0.1
            tmpYoffset = 0.1;
        }
        tmpChartRange[2] = this.updatedDataBoundaries[2] - tmpYoffset;
        tmpChartRange[3] = this.updatedDataBoundaries[3] + tmpYoffset;
        return tmpChartRange;
    }
    
    /**
     * Returns the updated data boundaries
     * 
     * @return The updated data boundaries
     */
    public double[] getUpdatedDataBoundaires() {
        return this.updatedDataBoundaries;
    }
    
    /**
     * Returns the statistics for the updated data {tmpYmin, tmpSampleMean, 
     * tmpSampleMeanError, tmpYmax, tmpSampleStandardDeviation, 
     * tmpLinearRegressionSlope}
     * 
     * @return The statistics for the updated data {tmpYmin, tmpSampleMean, 
     * tmpSampleMeanError, tmpYmax, tmpSampleStandardDeviation, 
     * tmpLinearRegressionSlope}
     */
    public double[] getStatistics() {
        return this.getStatistics(this.numberOfPointsInUpdatedData - 1);
    }
    
    /**
     * Returns the statistics for the updated data but only consider data up to a
     * certain point (point is included) or returns null if there is not enough 
     * data. {tmpYmin, tmpSampleMean, tmpSampleMeanError, tmpYmax, 
     * tmpSampleStandardDeviation, tmpLinearRegressionSlope}
     * 
     * @param anIndex A point up to which data is considered (point is included)
     * @return The statistics for the updated data but only consider data up to 
     * a certain point (point is included) or return null if there is not enough
     * data. {tmpYmin, tmpSampleMean, tmpSampleMeanError, tmpYmax, 
     * tmpSampleStandardDeviation, tmpLinearRegressionSlope}
     */
    public synchronized double[] getStatistics(int anIndex) {
        try {
            double tmpYmin = Double.MAX_VALUE;
            double tmpYmax = -Double.MAX_VALUE;
            double tmpSumX = 0.0;
            double tmpSumY = 0.0;
            double tmpSumXSquare = 0.0;
            double tmpSumYSquare = 0.0;
            double tmpSumXY = 0.0;
            double tmpX;
            double tmpY;
            int tmpN = 0;
            for (int i = 0; i <= anIndex; i++) {
                tmpX = this.updatedXyPairsArray[i][0];
                tmpY = this.updatedXyPairsArray[i][1];
                tmpYmin = Math.min(tmpY, tmpYmin);
                tmpYmax = Math.max(tmpY, tmpYmax);
                tmpSumX += tmpX;
                tmpSumY += tmpY;
                tmpSumXSquare += tmpX * tmpX;
                tmpSumYSquare += tmpY * tmpY;
                tmpSumXY += tmpX * tmpY;
                tmpN++;
            }
            if (tmpN < 2) {
                return null;
            }
            double tmpSampleMean = tmpSumY / tmpN;
            double tmpSxx = tmpSumXSquare - tmpSumX * tmpSumX / (double) tmpN;
            double tmpSxy = tmpSumXY - tmpSumX * tmpSumY / (double) tmpN;
            double tmpSampleStandardDeviation = Math.sqrt((tmpSumYSquare - tmpSumY * tmpSumY / (double) tmpN) / (double) (tmpN - 1));
            double tmpSampleMeanError = tmpSampleStandardDeviation / Math.sqrt(tmpN);
            double tmpLinearRegressionSlope = tmpSxy / tmpSxx;
            return new double[] {tmpYmin, tmpSampleMean, tmpSampleMeanError, tmpYmax, tmpSampleStandardDeviation, tmpLinearRegressionSlope};
        } catch(Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Returns the trend line for the updated data in the form of a start and end
     * point
     * 
     * @return The trend line for the updated data in the form of a start and 
     * end point
     */
    public XYSeries getTrend() {
        return this.getTrend(this.numberOfPointsInUpdatedData - 1);
    }
    
    /**
     * Returns the trend line for the updated data but only consider data up to a
     * certain point (point is included) or returns null if there is not enough 
     * data. The trend line is represented by a start and end point
     * 
     * @param anIndex A point up to which data is considered (point is included)
     * @return The trend line for the updated data but only consider data up to 
     * a certain point (point is included) or return null if there is not enough
     * data. The trend line is represented by a start and end point
     */
    public synchronized XYSeries getTrend(int anIndex) {
        try {
            XYSeries tmpXySeries = new XYSeries(DATA_SERIES_NAME);
            double tmpXmin = Double.MAX_VALUE;
            double tmpXmax = -Double.MAX_VALUE;
            double tmpSumX = 0.0;
            double tmpSumY = 0.0;
            double tmpSumXSquare = 0.0;
            double tmpSumXY = 0.0;
            double tmpX;
            double tmpY;
            int tmpN = 0;
            for (int i = 0; i <= anIndex; i++) {
                tmpX = this.updatedXyPairsArray[i][0];
                tmpY = this.updatedXyPairsArray[i][1];
                tmpXmin = Math.min(tmpX, tmpXmin);
                tmpXmax = Math.max(tmpX, tmpXmax);
                tmpSumX += tmpX;
                tmpSumY += tmpY;
                tmpSumXSquare += tmpX * tmpX;
                tmpSumXY += tmpX * tmpY;
                tmpN++;
            }
            if (tmpN < 2) {
                return null;
            }
            double tmpXMean = tmpSumX / tmpN;
            double tmpSampleMean = tmpSumY / tmpN;
            double tmpSxx = tmpSumXSquare - tmpSumX * tmpSumX / (double) tmpN;
            double tmpSxy = tmpSumXY - tmpSumX * tmpSumY / (double) tmpN;
            double tmpLinearRegressionSlope = tmpSxy / tmpSxx;
            double tmpLinearRegressionIntercept = tmpSampleMean - tmpXMean * tmpLinearRegressionSlope;
            tmpXySeries.add(tmpXmin, tmpLinearRegressionIntercept +  tmpLinearRegressionSlope * tmpXmin);
            tmpXySeries.add(tmpXmax, tmpLinearRegressionIntercept +  tmpLinearRegressionSlope * tmpXmax);
            return tmpXySeries;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Returns the number of points in the updated data
     * 
     * @return The number of points in the updated data
     */
    public int getNumberOfPointsInUpdatedData() {
        return this.numberOfPointsInUpdatedData;
    }
    
    /**
     * Returns the updated data as a XYSeries with averaged y-values
     * 
     * @param aNumberToAverage Number of y-values to average
     * @return The updated data as a XYSeries with averaged y-values
     */
    public synchronized XYSeries getUpdatedXySeries(int aNumberToAverage) {
        try {
            XYSeries tmpXySeries = new XYSeries(DATA_SERIES_NAME);
            if (this.updatedXyPairsArray.length > 0 && this.updatedXyPairsArray != null) {
                if (aNumberToAverage <= 1) {
                    for (double[] tmpXyPair : this.updatedXyPairsArray) {
                        tmpXySeries.add(tmpXyPair[0], tmpXyPair[1]);
                    }
                } else {
                    double tmpX = 0.0;
                    double tmpSum = 0.0;
                    double tmpNumberToAverage = (double) aNumberToAverage;
                    int tmpCounter = 1;
                    for (double[] tmpXyPair : this.updatedXyPairsArray) {
                        if (tmpCounter == 1) {
                            tmpX = tmpXyPair[0];
                            tmpSum = tmpXyPair[1];
                            tmpCounter++;
                        } else if (tmpCounter == aNumberToAverage) {
                            tmpSum += tmpXyPair[1];
                            tmpXySeries.add(tmpX, tmpSum / tmpNumberToAverage);
                            tmpCounter = 1;
                        } else {
                            tmpSum += tmpXyPair[1];
                            tmpCounter++;
                        }
                    }
                    // Average the last points that were not considered until now
                    if (tmpCounter > 1) {
                        tmpXySeries.add(tmpX, tmpSum / (tmpCounter - 1));
                    }
                }
            }
            return tmpXySeries;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Other">
    /**
     * Adds a xy-pair to the original data
     * NOTE: After adding a new number the XyChartDataManipulator is not updated
     * 
     * @param aX A x value to add
     * @param aY A y value to add
     */
    public void add(double aX, double aY) {
        this.originalXyPairs.add(new Double[] {aX, aY});
    }
    
    /**
     * Sets the zoom values for the updated data, updates updatedDataBoundaries 
     * and calls update()
     * 
     * @param aZoomValues Zoom values
     */
    public void setZoomValues(double[] aZoomValues) {
        this.zoomValues = java.util.Arrays.copyOf(aZoomValues, 4);
        this.updatedDataBoundaries = java.util.Arrays.copyOf(aZoomValues, 4);
        this.isZoom = true;
        this.update();
    }
    
    /**
     * Sets the number of initial discarded points for the updated data and 
     * calls update()
     * 
     * @param aNumberOfInitialDiscardedPoints Number of initial discarded points
     */
    public void setNumberOfDiscardedInitialPoints(int aNumberOfInitialDiscardedPoints) {
        // Not all points can be discarded and points cannot be discarded if there are valid zoom values
        if (this.originalXyPairs.size() <= aNumberOfInitialDiscardedPoints || this.isZoom) {
            return;
        }
        this.numberOfDiscardedInitialPoints = aNumberOfInitialDiscardedPoints;
        this.update();
    }
    
    /**
     * Increases the number of points to be initially discarded for the updated 
     * data by one and calls update()
     */
    public void incrementNumberOfDiscardedInitialPoints() {
        // If there is only one point it cannot be discarded and points cannot 
        // be discarded if there are valid zoom values
        if (this.updatedXyPairsList.size() <= 1 || this.isZoom) {
            return;
        }
        this.numberOfDiscardedInitialPoints++;
        this.update();
    }
    
    /**
     * Resets the zoom values and number of initially discarded points and calls
     * update()
     */
    public void reset() {
        this.zoomValues = new double[] {-Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE};
        this.isZoom = false;
        this.numberOfDiscardedInitialPoints = 0;
        this.update();
    }
    
    /**
     * Updates updatedXyPairs and updatedDataBoundaries if allowed
     */
    public synchronized void update() {
        this.updatedXyPairsList = new LinkedList<>();
        double tmpX, tmpY;
        if (!this.isZoom) {
            this.updatedDataBoundaries = new double[] {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE};
        }
        this.numberOfPointsInUpdatedData = 0;
        for(int i = this.numberOfDiscardedInitialPoints; i < this.originalXyPairs.size(); i++) {
            tmpX = this.originalXyPairs.get(i)[0];
            tmpY = this.originalXyPairs.get(i)[1];
            if (tmpX >= zoomValues[0] && tmpX <= zoomValues[1] && tmpY >= zoomValues[2] && tmpY <= zoomValues[3]) {
                this.updatedXyPairsList.add(new double[] {tmpX, tmpY});
                if (!this.isZoom) {
                    this.updatedDataBoundaries[0] = Math.min(this.updatedDataBoundaries[0], tmpX);
                    this.updatedDataBoundaries[1] = Math.max(this.updatedDataBoundaries[1], tmpX);
                    this.updatedDataBoundaries[2] = Math.min(this.updatedDataBoundaries[2], tmpY);
                    this.updatedDataBoundaries[3] = Math.max(this.updatedDataBoundaries[3], tmpY);
                }
                this.numberOfPointsInUpdatedData++;
            }
        }
        this.updateXyPairsArray();
    }
    
    /**
     * Updates this.updatedXyPairsArray with the data from 
     * this.updatedXyPairsList
     */
    public synchronized void updateXyPairsArray() {
        try {
            this.updatedXyPairsArray = new double[this.updatedXyPairsList.size()][];
            for (int i = 0; i < this.updatedXyPairsArray.length; i++) {
                this.updatedXyPairsArray[i] = this.updatedXyPairsList.get(i);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    // </editor-fold>
    // </editor-fold>
    
}
