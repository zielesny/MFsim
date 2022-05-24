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
package de.gnwi.mfsim.gui.chart;

import java.util.LinkedList;
import org.jfree.data.xy.XYSeries;

/**
 * Manipulator for JFreeChart xy-series
 * 
 * @author Achim Zielesny
 */
public class XySeriesManipulator {

    // <editor-fold defaultstate="collapsed" desc="Private final static class variables">
    // Name of data series
    private final static String DATA_SERIES_NAME = "XyData";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    // Linked list for xy-pairs
    private final LinkedList<Double[]> xyPairs;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public XySeriesManipulator() {
        this.xyPairs = new LinkedList<>();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Adds xy-pair
     * @param aX X value
     * @param aY Y value
     */
    public void add(double aX, double aY) {
        this.xyPairs.add(new Double[] {aX, aY});
    }

    /**
     * Returns xy-series with averaged y-values
     * 
     * @param aNumberToAverage Number of y-values to average
     * @return Xy-series with averaged y-values
     */
    public XYSeries getXySeries(int aNumberToAverage) {
        XYSeries tmpXySeries = new XYSeries(DATA_SERIES_NAME);
        if (!this.xyPairs.isEmpty()) {
            if (aNumberToAverage <= 1) {
                for (Double[] tmpXyPair : this.xyPairs) {
                    tmpXySeries.add(tmpXyPair[0], tmpXyPair[1]);
                }
            } else {
                double tmpX = 0.0;
                double tmpSum = 0.0;
                double tmpNumberToAverage = (double) aNumberToAverage;
                int tmpCounter = 1;
                for (Double[] tmpXyPair : this.xyPairs) {
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
            }
        }
        return tmpXySeries;
    }
    // </editor-fold>
    
}
