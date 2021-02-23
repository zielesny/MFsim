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
package de.gnwi.mfsim.gui.chart;

import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import java.awt.image.BufferedImage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * A bar chart
 * 
 * @author Jan-Mathis Hein
 */
public class BarChart implements ChartInterface{
    
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * The actual chart object
     */
    private JFreeChart chart;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param aValueItem Value item with the data, title and axes label for the 
     * chart
     */
    public BarChart(ValueItem aValueItem) {
        try {
            int tmpXValueColumn = aValueItem.getMatrixDiagramXValueColumn();
            int tmpYValueColumn = aValueItem.getMatrixDiagramYValueColumn();
            DefaultCategoryDataset tmpCategoryDataset = new DefaultCategoryDataset();
            for (int i = 0; i < aValueItem.getMatrixRowCount(); i++) {
                tmpCategoryDataset.addValue(aValueItem.getValueAsDouble(i, tmpYValueColumn), "BarChartData", aValueItem.getValue(i, tmpXValueColumn));
            }
            this.chart = ChartFactory.createBarChart(
                aValueItem.getDisplayName(), // Title
                aValueItem.getMatrixColumnNames()[tmpXValueColumn], // XAxisLabel
                aValueItem.getMatrixColumnNames()[tmpYValueColumn], // YAxisLabel
                tmpCategoryDataset, // Dataset
                PlotOrientation.VERTICAL, // Orientation
                false, // Legend flag
                false, // Tooltips flag
                false  // URLs flag
            );
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns the chart as an image with the given width and height
     * 
     * @param aWidth Width of the image
     * @param aHeight Height of the image
     * @return The chart as an image with the given width and height
     */
    @Override
    public BufferedImage getImage(int aWidth, int aHeight) {
        try {
            return this.chart.createBufferedImage(aWidth, aHeight);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Returns the chart as an image with the given width and height
     * 
     * @param aWidth Width of the image
     * @param aHeight Height of the image
     * @param anIndex Not implemented
     * @return The chart as an image with the given width and height
     */
    @Override
    public BufferedImage getImage(int aWidth, int aHeight, int anIndex) {
        // NOTE: Method is not properly implemented for bar charts
        return this.getImage(aWidth, aHeight);
    }
    
    /**
     * Returns a message that there are no statistics available for this chart
     * 
     * @return A message that there are no statistics available for this chart
     */
    @Override
    public String getStatisticsString() {
        return GuiMessage.get("StatisticsInformation.NoInformation");
    }
    // </editor-fold>
    
}
