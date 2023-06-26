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

import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.Paint;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * A chart for displaying an array of different datas
 * 
 * @author Jan-Mathis Hein
 */
public class DataArrayChart implements ChartInterface {
    
    // <editor-fold defaultstate="collapsed" desc="Private static final class variables">
    /**
     * Utility string methods
     */
    private static final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private variables">
    /**
     * True: The last point is marked, false: It is not
     */
    private boolean hasLastPointMarked;
    
    /**
     * True: The trend line of the data will be shown, false: It will not
     */
    private boolean hasTrendLine;

    /**
     * True: The fill color of the shapes is white, false: The fill color of the 
     * shapes is black
     */
    private boolean isFillColorWhite;
    
    /**
     * True: The colors of the chart are inverted, false: They are not 
     */
    private boolean isInverted;

    /**
     * True: The outline paint of the shapes is white, false: The outline paint 
     * is black
     */
    private boolean isOutlinePaintWhite;

    /**
     * True: For each data point a shape is painted, 
     * false: No shapes are painted
     */
    private boolean isShapePaint;

    /**
     * True: Thick lines, false: Thin lines
     */
    private boolean isThickLines;
    
    /**
     * Range of the chart in the form {xMin, xMax, yMin, yMax}
     */
    private double[] chartRange;
    
    /**
     * Index of the last point data for the plot
     */
    private int lastPointDataIndex;
    
    /**
     * Index of the main chart data for the plot
     */
    private int mainChartDataIndex;
    
    /**
     * Number of images this chart has
     */
    private int numberOfSteps;

    /**
     * Number of y-values to average
     */
    private int numberToAverage;

    /**
     * Index of the trend data for the plot
     */
    private int trendDataIndex;
    
    /**
     * The actual chart
     */
    private JFreeChart chart;
    
    /**
     * X axis of the chart
     */
    private ValueAxis xAxis;
    
    /**
     * Y axis of the chart
     */
    private ValueAxis yAxis;
    
    /**
     * Renderer for the last point data
     */
    private XYLineAndShapeRenderer lastPointRenderer;
    
    /**
     * Renderer for the main chart data
     */
    private XYLineAndShapeRenderer mainChartDataRenderer;
    
    /**
     * Renderer for the trend data
     */
    private XYLineAndShapeRenderer trendRenderer;
    
    /**
     * The data of the current last point
     */
    private XYSeries lastPointData;
    
    /**
     * An array of different XYSeries with data for a chart
     */
    private XYSeries[] chartDataArray;
    
    /**
     * An array of different XYSeries with data for a trend line
     */
    private XYSeries[] trendDataArray;
    
    /**
     * An array of manipulators for the main chart data which also stores the 
     * original data
     */
    private XyChartDataManipulator[] xyChartDataManipulatorArray;
    
    /**
     * The plot of the chart
     */
    private XYPlot plot;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param aXyChartDataManipulatorArray An array with XyChartDataManipulators
     * for the chart
     * @param aTitle Title for the chart
     * @param aXAxisLabel X axis label for the chart
     * @param aYAxisLabel Y axis label for the chart
     * @param aNumberToAverage Number of y-values to average
     * @param aChartRange Chart range (may be null then optimum chart range is evaluated)
     */
    public DataArrayChart(
        XyChartDataManipulator[] aXyChartDataManipulatorArray, 
        String aTitle, 
        String aXAxisLabel, 
        String aYAxisLabel, 
        int aNumberToAverage,
        double[] aChartRange
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXyChartDataManipulatorArray == null || aXyChartDataManipulatorArray.length <= 0) {
            throw new IllegalArgumentException("Illegal data array");
        }
        if (aTitle == null || aTitle.isEmpty()) {
            throw new IllegalArgumentException("Illegal title");
        }
        if (aXAxisLabel == null || aXAxisLabel.isEmpty() || aYAxisLabel == null || aYAxisLabel.isEmpty()) {
            throw new IllegalArgumentException("Illegal axes labels");
        }
        if (aChartRange != null && aChartRange.length != 4) {
            throw new IllegalArgumentException("Illegal chart range");
        }
        if (aNumberToAverage <= 0) {
            aNumberToAverage = 1;
        }
        // </editor-fold>
        try {
            this.numberOfSteps = aXyChartDataManipulatorArray.length;
            this.xyChartDataManipulatorArray = aXyChartDataManipulatorArray;
            this.numberToAverage = aNumberToAverage;
            this.chartDataArray = new XYSeries[this.numberOfSteps];
            this.trendDataArray = new XYSeries[this.numberOfSteps];
            for (int i = 0; i < this.numberOfSteps; i++) {
                this.chartDataArray[i] = this.xyChartDataManipulatorArray[i].getUpdatedXySeries(this.numberToAverage);
            }
            this.chart = ChartFactory.createXYLineChart(
                aTitle, // Title
                aXAxisLabel, // XAxisLabel
                aYAxisLabel, // YAxisLabel
                new XYSeriesCollection(), // Dataset
                PlotOrientation.VERTICAL, // Orientation
                false, // Legend flag
                false, // Tooltips flag
                false  // URLs flag
            );
            this.plot = this.chart.getXYPlot();
            this.mainChartDataRenderer = (XYLineAndShapeRenderer) this.plot.getRenderer();
            this.mainChartDataRenderer.setShape(new Ellipse2D.Float(-5.0f, -5.0f, 10.0f, 10.0f));
            this.mainChartDataRenderer.setPaint(Color.BLACK);
            this.mainChartDataRenderer.setOutlinePaint(Color.WHITE);
            this.mainChartDataRenderer.setFillPaint(Color.WHITE);
            this.mainChartDataRenderer.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            // Order of datasets is changed so that they are drawn in the correct order
            this.lastPointDataIndex = 0;
            this.trendDataIndex = 1;
            this.mainChartDataIndex = 2;
            this.plot.setRenderer(this.mainChartDataIndex, this.mainChartDataRenderer);
            this.plot.setDataset(this.mainChartDataIndex, new XYSeriesCollection(this.chartDataArray[0]));
            // The renderer draws lines but no shapes
            this.trendRenderer = new XYLineAndShapeRenderer(true, false);
            this.trendRenderer.setPaint(Color.GRAY);
            this.trendRenderer.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            this.plot.setRenderer(this.trendDataIndex, this.trendRenderer);
            this.plot.setDataset(this.trendDataIndex, new XYSeriesCollection());
            // The renderer draws no lines but shapes
            this.lastPointRenderer = new XYLineAndShapeRenderer(false, true);
            this.lastPointRenderer.setShape(new Ellipse2D.Float(-5.0f, -5.0f, 10.0f, 10.0f));
            this.lastPointRenderer.setShapesVisible(true);
            this.lastPointRenderer.setPaint(Color.WHITE);
            this.lastPointRenderer.setOutlinePaint(Color.BLACK);
            this.lastPointRenderer.setUseOutlinePaint(true);
            this.lastPointRenderer.setFillPaint(Color.BLACK);
            this.plot.setRenderer(this.lastPointDataIndex, this.lastPointRenderer);
            this.plot.setDataset(this.lastPointDataIndex, new XYSeriesCollection());
            this.xAxis = this.plot.getDomainAxis();
            this.yAxis = this.plot.getRangeAxis();
            this.xAxis.setAutoRange(false);
            this.yAxis.setAutoRange(false);
            this.setOptimumChartRange(aChartRange);
            this.hasLastPointMarked = false;
            this.hasTrendLine = false;
            this.isFillColorWhite = false;
            this.isInverted = false;
            this.isOutlinePaintWhite = false;
            this.isShapePaint = false;
            this.isThickLines = false;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns an image of the chart which displays the first data of the chart 
     * data array
     * 
     * @param aWidth Width of the image
     * @param aHeight Height of the image
     * @return An image of the chart which displays the first data of the chart 
     * data array
     */
    @Override
    public BufferedImage getImage(int aWidth, int aHeight) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aWidth <= 0 || aHeight <= 0) {
            throw new IllegalArgumentException("Negative dimension for image");
        }
        // </editor-fold>
        try {
            return this.chart.createBufferedImage(aWidth, aHeight);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Returns the chart as an image where the data from the ith element of the 
     * chart data array is displayed
     * 
     * @param aWidth Width of the image
     * @param aHeight Height of the image
     * @param anIndex Index for chart data array
     * @return The chart as an image where the data from the ith element of the 
     * chart data array is displayed
     */
    @Override
    public BufferedImage getImage(int aWidth, int aHeight, int anIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anIndex < 0 || anIndex >= this.chartDataArray.length) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        if (aWidth <= 0 || aHeight <= 0) {
            throw new IllegalArgumentException("Negative dimension for image");
        }
        // </editor-fold>
        try {
            if (this.hasTrendLine) {
                this.plot.setDataset(this.trendDataIndex, new XYSeriesCollection(this.trendDataArray[anIndex]));
            }
            if (this.hasLastPointMarked) {
                XYSeries tmpXySeries = new XYSeries("XyData");
                tmpXySeries.add(
                    this.chartDataArray[anIndex].getX(this.chartDataArray[anIndex].getItemCount() - 1), 
                    this.chartDataArray[anIndex].getY(this.chartDataArray[anIndex].getItemCount() - 1)
                );
                this.plot.setDataset(this.lastPointDataIndex, new XYSeriesCollection(tmpXySeries));
            }
            this.plot.setDataset(this.mainChartDataIndex, new XYSeriesCollection(this.chartDataArray[anIndex]));
            BufferedImage tmpImage = this.chart.createBufferedImage(aWidth, aHeight);
            // Reset the chart to the original state
            this.plot.setDataset(this.mainChartDataIndex, new XYSeriesCollection(this.chartDataArray[0]));
            if (this.hasTrendLine) {
                this.plot.setDataset(this.trendDataIndex, new XYSeriesCollection(this.trendDataArray[0]));
            }
            if (this.hasLastPointMarked) {
                this.plot.setDataset(this.lastPointDataIndex, new XYSeriesCollection(this.lastPointData));
            }
            return tmpImage;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Returns the number of steps that are stored in the chart data array
     * 
     * @return The number of steps that are stored in the chart data array
     */
    public int getNumberOfSteps() {
        return this.numberOfSteps;
    }
    
    /**
     * Returns the statistics of this chart for the first step
     * 
     * @return The statistics of this chart for the first step
     */
    @Override
    public String getStatisticsString() {
        try {
            double[] tmpStatistics = this.xyChartDataManipulatorArray[0].getStatistics();
            if (tmpStatistics == null) {
                return GuiMessage.get("StatisticsInformation.NoInformation");
            }
            return String.format(GuiMessage.get("StatisticsInformation.Format"),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[0], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[1], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[2], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[3], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[4], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[5], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_SLOPE)
            );
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Returns the statistics of this chart for the ith step where i is anIndex
     * 
     * @param anIndex Index of the step for which the statistics are calculated
     * @return The statistics of this chart for the ith step where i is anIndex
     */
    public String getStatisticsString(int anIndex) {
        try {
            double[] tmpStatistics = this.xyChartDataManipulatorArray[anIndex].getStatistics();
            if (tmpStatistics == null) {
                return GuiMessage.get("StatisticsInformation.NoInformation");
            }
            return String.format(GuiMessage.get("StatisticsInformation.Format"),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[0], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[1], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[2], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[3], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[4], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                DataArrayChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[5], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_SLOPE)
            );
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Sets whether the last point of the chart is marked or not and updates the
     * chart accordingly
     * 
     * @param aHasLastPointMarked True: The chart's last point is marked, 
     * false: It is not
     */
    public void setHasLastPointMarked(boolean aHasLastPointMarked) {
        try {
            this.hasLastPointMarked = aHasLastPointMarked;
            if (this.hasLastPointMarked) {
                this.lastPointData = new XYSeries("XyData");
                this.lastPointData.add(this.chartDataArray[0].getX(this.chartDataArray[0].getItemCount() - 1), this.chartDataArray[0].getY(this.chartDataArray[0].getItemCount() - 1));
                this.plot.setDataset(this.lastPointDataIndex, new XYSeriesCollection(this.lastPointData));
            }
            else {
                this.plot.setDataset(this.lastPointDataIndex, new XYSeriesCollection());
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    
    /**
     * Sets whether the chart has a trend line or not and updates the chart 
     * accordingly
     * 
     * @param aHasTrendLine True: This chart has a trend line, false: It has not
     */
    public void setHasTrendLine(boolean aHasTrendLine) {
        this.hasTrendLine = aHasTrendLine;
        if (this.hasTrendLine) {
            for (int i = 0; i < this.numberOfSteps; i++) {
                this.trendDataArray[i] = this.xyChartDataManipulatorArray[i].getTrend();
            }
            this.plot.setDataset(this.trendDataIndex, new XYSeriesCollection(this.trendDataArray[0]));
        }
        else {
            this.plot.setDataset(this.trendDataIndex, new XYSeriesCollection());
        }
    }
    
    /**
     * Sets whether the fill color of the shapes is white or black
     * 
     * @param anIsFillColorWhite True: The fill color is white, false: The fill 
     * color is black. If the last point is marked its fill color is the 
     * opposite
     */
    public void setIsFillColorWhite(boolean anIsFillColorWhite) {
        this.isFillColorWhite = anIsFillColorWhite;
        if (this.isFillColorWhite) {
                this.mainChartDataRenderer.setUseFillPaint(true);
                // If the fill color is white, the outline color cannot be white
                this.mainChartDataRenderer.setUseOutlinePaint(false);
                this.lastPointRenderer.setUseFillPaint(true);
                this.lastPointRenderer.setUseOutlinePaint(!this.isOutlinePaintWhite);
        }
        else {
            this.mainChartDataRenderer.setUseFillPaint(false);
            this.mainChartDataRenderer.setUseOutlinePaint(this.isOutlinePaintWhite);
            this.lastPointRenderer.setUseFillPaint(false);
            // If the fill color of the last point is white, the outline color of the last point has to be black
            this.lastPointRenderer.setUseOutlinePaint(true);
        }
    }
    
    /**
     * Sets whether the chart's colors are inverted
     * 
     * @param anIsInverted True: The colors of the chart are inverted, false:
     * They are not
     */
    public void setIsInverted(boolean anIsInverted) {
        boolean tmpOldIsInverted = this.isInverted;
        this.isInverted = anIsInverted;
        // Execute the next block if isInverted changed from false to true
        if (this.isInverted && !tmpOldIsInverted) {
            //NOTE: Only implemented elements are getting inverted
            this.chart.getTitle().setPaint(this.getInvertedColor((Color) this.chart.getTitle().getPaint()));
            this.chart.setBackgroundPaint(Color.BLACK);
            this.chart.setBorderPaint(this.getInvertedColor((Color) this.chart.getBorderPaint()));
            
            this.plot.setDrawingSupplier(this.getInvertedDrawingSupplier());
            this.plot.getDomainAxis().setAxisLinePaint(this.getInvertedColor((Color) this.plot.getDomainAxis().getAxisLinePaint()));
            this.plot.getDomainAxis().setLabelPaint(this.getInvertedColor((Color) this.plot.getDomainAxis().getLabelPaint()));
            this.plot.getDomainAxis().setTickLabelPaint(this.getInvertedColor((Color) this.plot.getDomainAxis().getTickLabelPaint()));
            this.plot.getDomainAxis().setTickMarkPaint(this.getInvertedColor((Color) this.plot.getDomainAxis().getTickMarkPaint()));
            this.plot.setDomainCrosshairPaint(this.getInvertedColor((Color) this.plot.getDomainCrosshairPaint()));
            //DomainGridlinePaint gets manually altered for better contrast
            this.plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            this.plot.setDomainZeroBaselinePaint(this.getInvertedColor((Color) this.plot.getDomainZeroBaselinePaint()));
            this.plot.getRangeAxis().setAxisLinePaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getAxisLinePaint()));
            this.plot.getRangeAxis().setLabelPaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getLabelPaint()));
            this.plot.getRangeAxis().setTickLabelPaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getTickLabelPaint()));
            this.plot.getRangeAxis().setTickMarkPaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getTickMarkPaint()));
            this.plot.setRangeCrosshairPaint(this.getInvertedColor((Color) this.plot.getRangeCrosshairPaint()));
            //RangeGridlinePaint gets manually altered for better contrast
            this.plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            this.plot.setRangeZeroBaselinePaint(this.getInvertedColor((Color) this.plot.getRangeZeroBaselinePaint()));
            this.plot.setBackgroundPaint(this.getInvertedColor((Color) this.plot.getBackgroundPaint()));
            this.plot.setNoDataMessagePaint(this.getInvertedColor((Color) this.plot.getNoDataMessagePaint()));
            this.plot.setOutlinePaint(this.getInvertedColor((Color) this.plot.getOutlinePaint()));
            
            this.mainChartDataRenderer.setBaseFillPaint(this.getInvertedColor((Color) this.mainChartDataRenderer.getBaseFillPaint()));
            this.mainChartDataRenderer.setBaseItemLabelPaint(this.getInvertedColor((Color) this.mainChartDataRenderer.getBaseItemLabelPaint()));
            this.mainChartDataRenderer.setBaseOutlinePaint(this.getInvertedColor((Color) this.mainChartDataRenderer.getBaseOutlinePaint()));
            this.mainChartDataRenderer.setBasePaint(this.getInvertedColor((Color) this.mainChartDataRenderer.getBasePaint()));
            this.mainChartDataRenderer.setFillPaint(this.getInvertedColor((Color) Color.WHITE));
            this.mainChartDataRenderer.setOutlinePaint(this.getInvertedColor((Color) Color.WHITE));
            this.mainChartDataRenderer.setPaint(this.getInvertedColor((Color) Color.BLACK));
            
            this.trendRenderer.setBaseFillPaint(this.getInvertedColor((Color) this.trendRenderer.getBaseFillPaint()));
            this.trendRenderer.setBaseItemLabelPaint(this.getInvertedColor((Color) this.trendRenderer.getBaseItemLabelPaint()));
            this.trendRenderer.setBaseOutlinePaint(this.getInvertedColor((Color) this.trendRenderer.getBaseOutlinePaint()));
            this.trendRenderer.setBasePaint(this.getInvertedColor((Color) this.trendRenderer.getBasePaint()));
            this.trendRenderer.setPaint(this.getInvertedColor((Color) Color.GRAY));
            
            this.lastPointRenderer.setPaint(Color.BLACK);
            this.lastPointRenderer.setOutlinePaint(Color.WHITE);
            this.lastPointRenderer.setFillPaint(Color.WHITE);
        }
        // Execute the next block if isInverted changed from true to false
        else if (!this.isInverted && tmpOldIsInverted) {
            //NOTE: Only implemented elements are getting inverted
            this.chart.getTitle().setPaint(this.getInvertedColor((Color) this.chart.getTitle().getPaint()));
            this.chart.setBackgroundPaint(new Color(214, 217, 223));
            this.chart.setBorderPaint(this.getInvertedColor((Color) this.chart.getBorderPaint()));
            
            this.plot.setDrawingSupplier(new DefaultDrawingSupplier());
            this.plot.getDomainAxis().setAxisLinePaint(this.getInvertedColor((Color) this.plot.getDomainAxis().getAxisLinePaint()));
            this.plot.getDomainAxis().setLabelPaint(this.getInvertedColor((Color) this.plot.getDomainAxis().getLabelPaint()));
            this.plot.getDomainAxis().setTickLabelPaint(this.getInvertedColor((Color) this.plot.getDomainAxis().getTickLabelPaint()));
            this.plot.getDomainAxis().setTickMarkPaint(this.getInvertedColor((Color) this.plot.getDomainAxis().getTickMarkPaint()));
            this.plot.setDomainCrosshairPaint(this.getInvertedColor((Color) this.plot.getDomainCrosshairPaint()));
            //DomainGridlinePaint gets manually altered for better contrast
            this.plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            this.plot.setDomainZeroBaselinePaint(this.getInvertedColor((Color) this.plot.getDomainZeroBaselinePaint()));
            this.plot.getRangeAxis().setAxisLinePaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getAxisLinePaint()));
            this.plot.getRangeAxis().setLabelPaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getLabelPaint()));
            this.plot.getRangeAxis().setTickLabelPaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getTickLabelPaint()));
            this.plot.getRangeAxis().setTickMarkPaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getTickMarkPaint()));
            this.plot.setRangeCrosshairPaint(this.getInvertedColor((Color) this.plot.getRangeCrosshairPaint()));
            //RangeGridlinePaint gets manually altered for better contrast
            this.plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            this.plot.setRangeZeroBaselinePaint(this.getInvertedColor((Color) this.plot.getRangeZeroBaselinePaint()));
            this.plot.setBackgroundPaint(this.getInvertedColor((Color) this.plot.getBackgroundPaint()));
            this.plot.setNoDataMessagePaint(this.getInvertedColor((Color) this.plot.getNoDataMessagePaint()));
            this.plot.setOutlinePaint(this.getInvertedColor((Color) this.plot.getOutlinePaint()));
            
            this.mainChartDataRenderer.setBaseFillPaint(this.getInvertedColor((Color) this.mainChartDataRenderer.getBaseFillPaint()));
            this.mainChartDataRenderer.setBaseItemLabelPaint(this.getInvertedColor((Color) this.mainChartDataRenderer.getBaseItemLabelPaint()));
            this.mainChartDataRenderer.setBaseOutlinePaint(this.getInvertedColor((Color) this.mainChartDataRenderer.getBaseOutlinePaint()));
            this.mainChartDataRenderer.setBasePaint(this.getInvertedColor((Color) this.mainChartDataRenderer.getBasePaint()));
            this.mainChartDataRenderer.setFillPaint(Color.WHITE);
            this.mainChartDataRenderer.setOutlinePaint(Color.WHITE);
            this.mainChartDataRenderer.setPaint(Color.BLACK);
            
            this.trendRenderer.setBaseFillPaint(this.getInvertedColor((Color) this.trendRenderer.getBaseFillPaint()));
            this.trendRenderer.setBaseItemLabelPaint(this.getInvertedColor((Color) this.trendRenderer.getBaseItemLabelPaint()));
            this.trendRenderer.setBaseOutlinePaint(this.getInvertedColor((Color) this.trendRenderer.getBaseOutlinePaint()));
            this.trendRenderer.setBasePaint(this.getInvertedColor((Color) this.trendRenderer.getBasePaint()));
            this.trendRenderer.setPaint(Color.GRAY);
            
            this.lastPointRenderer.setPaint(Color.WHITE);
            this.lastPointRenderer.setOutlinePaint(Color.BLACK);
            this.lastPointRenderer.setFillPaint(Color.BLACK);
        }
    }
    
    /**
     * Sets whether the outline paint of all shapes is white or black
     * 
     * @param anIsOutlinePaintWhite True: The outline paint is white, false: 
     * The outline paint is black
     */
    public void setIsOutlinePaintWhite(boolean anIsOutlinePaintWhite) {
        this.isOutlinePaintWhite = anIsOutlinePaintWhite;
        if (this.isOutlinePaintWhite && !this.isFillColorWhite) {
            this.mainChartDataRenderer.setUseOutlinePaint(true);
        }
        else {
            this.mainChartDataRenderer.setUseOutlinePaint(false);
        }
        if (this.isFillColorWhite && this.isOutlinePaintWhite) {
            // Do not use the outline paint black -> outline paint is white
            this.lastPointRenderer.setUseOutlinePaint(false);
        }
        else {
            // In any other case the outline color is black
            this.lastPointRenderer.setUseOutlinePaint(true);
        }
    }
    
    /**
     * Sets whether data points in the chart are painted as shapes or not 
     * 
     * @param anIsShapePaint True: The data points are painted as shapes, 
     * false: The data points are not painted as shapes
     */
    public void setIsShapePaint(boolean anIsShapePaint) {
        this.isShapePaint = anIsShapePaint;
        if (this.isShapePaint) {
            this.mainChartDataRenderer.setShapesVisible(true);
        }
        else {
            this.mainChartDataRenderer.setShapesVisible(false);
        }
    }
    
    /**
     * Sets whether the chart has thick or thin lines
     * 
     * @param anIsThickLines True: The chart has thick lines, false: It has thin
     * lines
     */
    public void setIsThickLines(boolean anIsThickLines) {
        this.isThickLines = anIsThickLines;
        if (this.isThickLines) {
            this.mainChartDataRenderer.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        }
        else {
            this.mainChartDataRenderer.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        }
    }
    
    /**
     * Returns chart range
     * 
     * @return Chart range
     */
    public double[] getChartRange() {
        return new double[]
            {
                this.xAxis.getRange().getLowerBound(),
                this.xAxis.getRange().getUpperBound(),
                this.yAxis.getRange().getLowerBound(),
                this.yAxis.getRange().getUpperBound()
            };
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Returns a DrawingSupplier that has inverted colors in respect to the 
     * DefaultDrawingSupplier of the JFreeChart library. If the creation of the
     * inverted DrawingSupplier fails, the DefaultDrawingSupplier is returned
     * 
     * @return A DrawingSupplier that has inverted colors in respect to the 
     * DefaultDrawingSupplier of the JFreeChart library. If the creation of the 
     * inverted DrawingSupplier fails, the DefaultDrawingSupplier is returned
     */
    private DrawingSupplier getInvertedDrawingSupplier() {
        try {
            DrawingSupplier tmpDrawingSupplier;
            // Ligh gray is the default color for the outline paint
            Paint[] tmpOutlinePaintSequence = {this.getInvertedColor(Color.lightGray)};
            Paint[] tmpPaintSequence = new Paint[DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length];
            for (int i = 0; i < tmpPaintSequence.length; i++) {
                tmpPaintSequence[i] = this.getInvertedColor((Color) DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE[i]);
            }
            tmpDrawingSupplier = new DefaultDrawingSupplier(
                tmpPaintSequence, 
                tmpOutlinePaintSequence, 
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, 
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, 
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE
            );
            return tmpDrawingSupplier;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return new DefaultDrawingSupplier();
        }
    }
    
    /**
     * Inverts a color
     * 
     * @param aColor A color that will be inverted
     * @return Inverted color as a Paint object
     */
    private Paint getInvertedColor(Color aColor) {
        try {
            int tmpRed;
            int tmpGreen;
            int tmpBlue;
            tmpRed = aColor.getRed();
            tmpGreen = aColor.getGreen();
            tmpBlue = aColor.getBlue();
            return (Paint) new Color(255 - tmpRed, 255 - tmpGreen, 255 - tmpBlue);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * Determines and sets the optimal values for the chart range if not provided
     * 
     * @param aChartRange Chart range (may be null then optimum chart range is evaluated)
     */
    private void setOptimumChartRange(double[] aChartRange) {
        if (aChartRange == null) {
            double xMin = Double.MAX_VALUE;
            double xMax = -Double.MAX_VALUE;
            double yMin = Double.MAX_VALUE;
            double yMax = -Double.MAX_VALUE;
            double[] tmpChartRange;
            for (XyChartDataManipulator tmpXyChartDataManipulator : this.xyChartDataManipulatorArray) {
                tmpChartRange = tmpXyChartDataManipulator.getChartRange();
                xMin = Math.min(tmpChartRange[0], xMin);
                xMax = Math.max(tmpChartRange[1], xMax);
                yMin = Math.min(tmpChartRange[2], yMin);
                yMax = Math.max(tmpChartRange[3], yMax);
            }
            this.chartRange = new double[]{xMin, xMax, yMin, yMax};
        } else {
            this.chartRange = aChartRange;
        }
        this.xAxis.setRange(this.chartRange[0], this.chartRange[1]);
        this.yAxis.setRange(this.chartRange[2], this.chartRange[3]);
    }
    // </editor-fold>
    
}
