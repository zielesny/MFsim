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
import de.gnwi.mfsim.model.valueItem.ValueItem;
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
 * A chart with xy-data. The data and look of the chart can be manipulated.
 * 
 * @author Jan-Mathis Hein
 */
public class XyChart implements ChartInterface{
    
    // <editor-fold defaultstate="collapsed" desc="Private static final class variables">
    /**
     * Utility string methods
     */
    private static final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private variables">
    /**
     * True: There is an up to date accumulative XYSeries array of the main 
     * chart data, false: There is not
     */
    private boolean hasAccumulativeChartDataArray;
    
    /**
     * True: The statistics will be changed according to the displayed data, 
     * false: The statistics stay the same independent of the displayed data
     */
    private boolean hasReducedStatistics;
    
    /**
     * True: The last point is marked, false: It is not
     */
    private boolean hasLastPointMarked;
    
    /**
     * True: The trend line will be changed according to the displayed data, 
     * false: The trend line stays the same independent of the displayed data
     */
    private boolean hasReducedTrendLine;
    
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
     * Index of the last point data for the plot
     */
    private int lastPointDataIndex;
    
    /**
     * Index of the main chart data for the plot
     */
    private int mainChartDataIndex;

    /**
     * Number of y-values to average
     */
    private int numberToAverage;

    /**
     * Index of the trend data for the plot
     */
    private int trendDataIndex;
    
    /**
     * The actual chart object
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
     * Renderer of the trend line data
     */
    private XYLineAndShapeRenderer trendRenderer;
    
    /**
     * The data of the current last point
     */
    private XYSeries lastPointData;
    
    /**
     * The main chart data
     */
    private XYSeries mainChartData;
    
    /**
     * The data of the current trend line
     */
    private XYSeries trendData;
    
    /**
     * An accumulative XYSeries array of the chart data
     */
    private XYSeries[] accumulativeChartDataArray;
    
    /**
     * An array with data for trend lines for the corresponding XYSeries of the 
     * accumulativeChartDataArray
     */
    private XYSeries[] trendDataArray;
    
    /**
     * A manipulator for the main chart data which also stores the original data
     */
    private XyChartDataManipulator xyChartDataManipulator;
    
    /**
     * The plot of the chart
     */
    private XYPlot plot;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constuctor. Creates a chart with the data of the given value item in the 
     * default state
     * 
     * @param aValueItem Value item with the data, title and axes labels for the 
     * chart
     */
    public XyChart(ValueItem aValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null || !aValueItem.hasMatrixDiagram()) {
            throw new IllegalArgumentException("No value item data");
        }
        // </editor-fold>
        try {
            String tmpTitle = aValueItem.getDisplayName();
            this.xyChartDataManipulator = new XyChartDataManipulator();
            int tmpXValueColumn = aValueItem.getMatrixDiagramXValueColumn();
            int tmpYValueColumn = aValueItem.getMatrixDiagramYValueColumn();
            for (int i = 0; i < aValueItem.getMatrixRowCount(); i++) {
                // Safeguard for NaN values in value item
                double tmpX = 0.0;
                double tmpY = 0.0;
                try {
                    tmpX = aValueItem.getValueAsDouble(i, tmpXValueColumn);
                    tmpY = aValueItem.getValueAsDouble(i, tmpYValueColumn);
                    if (Double.isNaN(tmpX) || Double.isNaN(tmpY)) {
                        continue;
                    }
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    continue;
                }
                this.xyChartDataManipulator.add(tmpX, tmpY);
            }
            // Updating the data manipulator to finish its creation
            this.xyChartDataManipulator.update();
            this.numberToAverage = 1;
            this.mainChartData = this.xyChartDataManipulator.getUpdatedXySeries(this.numberToAverage);
            this.chart = ChartFactory.createXYLineChart(
                tmpTitle, // Title
                aValueItem.getMatrixColumnNames()[tmpXValueColumn], // XAxisLabel
                aValueItem.getMatrixColumnNames()[tmpYValueColumn], // YAxisLabel
                new XYSeriesCollection(this.mainChartData), // Dataset
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
            this.plot.setDataset(this.mainChartDataIndex, new XYSeriesCollection(this.mainChartData));
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
            this.updateAxesRange();
            this.hasAccumulativeChartDataArray = false;
            this.hasReducedStatistics = false;
            this.hasLastPointMarked = false;
            this.hasReducedTrendLine = false;
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
    //</editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public Methods">
    // <editor-fold defaultstate="collapsed" desc="- Get">
    /**
     * Return the number of points that are in the chart
     * 
     * @return The number of points that are in the chart
     */
    public int getNumberOfPoints() {
        return this.mainChartData.getItemCount();
    }
    
    /**
     * Return the XyChartDataManipulator
     * 
     * @return The XyChartDataManipulator
     */
    public XyChartDataManipulator getXyChartDataManipulator() {
        return this.xyChartDataManipulator;
    }
    
    /**
     * Return the chart as an image
     * 
     * @param aWidth Width of the image
     * @param aHeight Height of the image
     * @return The chart as an image
     */
    @Override
    public BufferedImage getImage(int aWidth, int aHeight) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aWidth <= 0 || aHeight <= 0) {
            throw new IllegalArgumentException("Invalid dimension for image");
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
     * Return the chart as an image where the data from the ith element of the 
     * accumulative chart data array is used
     * 
     * @param aWidth Width of the image
     * @param aHeight Height of the image
     * @param anIndex Index for chart data array
     * @return The chart as an image where the data from the ith element of the 
     * accumulative chart data array is used
     */
    @Override
    public BufferedImage getImage(int aWidth, int aHeight, int anIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.hasAccumulativeChartDataArray || this.accumulativeChartDataArray == null || this.accumulativeChartDataArray.length < 1) {
            throw new IllegalArgumentException("No accumulative chart data array was found");
        }
        if (anIndex < 0 || anIndex >= this.accumulativeChartDataArray.length) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        if (aWidth <= 0 || aHeight <= 0) {
            throw new IllegalArgumentException("Invalid dimension for image");
        }
        // </editor-fold>
        try {
            if (this.hasTrendLine && this.hasReducedTrendLine) {
                // The index is converted to be suitable for the original data points
                int tmpIndex = (this.numberToAverage * (anIndex + 1)) - 1;
                if (tmpIndex >= this.xyChartDataManipulator.getNumberOfPointsInUpdatedData()) {
                    tmpIndex = this.xyChartDataManipulator.getNumberOfPointsInUpdatedData() - 1;
                }
                this.plot.setDataset(this.trendDataIndex, new XYSeriesCollection(this.trendDataArray[tmpIndex]));
            }
            if (this.hasLastPointMarked) {
                XYSeries tmpXySeries = new XYSeries("XyData");
                tmpXySeries.add(this.mainChartData.getX(anIndex), this.mainChartData.getY(anIndex));
                this.plot.setDataset(this.lastPointDataIndex, new XYSeriesCollection(tmpXySeries));
            }
            this.plot.setDataset(this.mainChartDataIndex, new XYSeriesCollection(this.accumulativeChartDataArray[anIndex]));
            BufferedImage tmpImage = this.chart.createBufferedImage(aWidth, aHeight);
            // Reset the chart to the original state
            this.plot.setDataset(this.mainChartDataIndex, new XYSeriesCollection(this.mainChartData));
            if (this.hasTrendLine && this.hasReducedTrendLine) {
                this.plot.setDataset(this.trendDataIndex, new XYSeriesCollection(this.trendData));
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
     * Return the statistics of this chart as a string
     * 
     * @return The statistics of this chart as a string
     */
    @Override
    public String getStatisticsString() {
        try {
            double[] tmpStatistics = this.xyChartDataManipulator.getStatistics();
            if (tmpStatistics == null) {
                return GuiMessage.get("StatisticsInformation.NoInformation");
            }
            return String.format(GuiMessage.get("StatisticsInformation.Format"),
                XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[0], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[1], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[2], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[3], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[4], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[5], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_SLOPE)
            );
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    
    /**
     * If this chart has changing statistics return the statistics of this chart
     * but only data up to a certain point (anIndex) is considered.
     * Else return the statistics for the main chart data.
     * 
     * @param anIndex Index of the point up to which data is considered
     * @return The statistics of this chart but only data up to a certain point 
     * (anIndex) is considered if the chart has changing statistics. 
     * Else return the statistics for the main chart data
     */
    public String getStatisticsString(int anIndex) {
        try {
            if (this.hasReducedStatistics && this.hasAccumulativeChartDataArray){
                // The index is converted to be suitable for the original data points
                int tmpIndex = (this.numberToAverage * (anIndex + 1)) - 1;
                if (tmpIndex >= this.xyChartDataManipulator.getNumberOfPointsInUpdatedData()) {
                    tmpIndex = this.xyChartDataManipulator.getNumberOfPointsInUpdatedData() - 1;
                }
                double[] tmpStatistics = this.xyChartDataManipulator.getStatistics(tmpIndex);
                if (tmpStatistics == null) {
                    return GuiMessage.get("StatisticsInformation.NoInformation");
                }
                return String.format(GuiMessage.get("StatisticsInformation.Format"),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[0], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[1], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[2], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[3], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[4], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[5], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_SLOPE)
                );
            }
            else {
                double[] tmpStatistics = this.xyChartDataManipulator.getStatistics();
                if (tmpStatistics == null) {
                    return GuiMessage.get("StatisticsInformation.NoInformation");
                }
                return String.format(GuiMessage.get("StatisticsInformation.Format"),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[0], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[1], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[2], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[3], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[4], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_STATISTICS),
                    XyChart.stringUtilityMethods.formatDoubleValue(tmpStatistics[5], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_SLOPE)
                );
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Set">
    /**
     * Sets whether the statistics will change according to the displayed 
     * data or not
     * 
     * @param aHasReducedStatistics True: The statistics will change, 
     * false: The statistics stay the same
     */
    public void setHasReducedStatistics(boolean aHasReducedStatistics) {
        this.hasReducedStatistics = aHasReducedStatistics;
    }
    
    /**
     * Sets whether there is an accumulative XYSeries array of the main chart 
     * data or not and creates it
     * 
     * @param aHasAccumulativeDataArrays True: This chart has an accumulative 
     * XYSeries array of the main chart data, false: It has not
     */
    public void setHasAccumulativeChartDataArray(boolean aHasAccumulativeDataArrays) {
        this.hasAccumulativeChartDataArray = aHasAccumulativeDataArrays;
        if (this.hasAccumulativeChartDataArray) {
            this.createAccumulativeChartDataArray();
            if (this.hasTrendLine) {
                this.createAccumulativeTrendDataArray();
            }
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
                if (this.mainChartData.getItemCount() > 0) {
                    this.lastPointData.add(this.mainChartData.getX(this.mainChartData.getItemCount() - 1), this.mainChartData.getY(this.mainChartData.getItemCount() - 1));
                }
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
     * Sets whether the trend line will change according to the displayed 
     * data or not
     * 
     * @param aHasReducedTrendLine True: The trend line will change, false:
     * The trend line will stay the same
     */
    public void setHasReducedTrendLine(boolean aHasReducedTrendLine) {
        this.hasReducedTrendLine = aHasReducedTrendLine;
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
            this.trendData = this.xyChartDataManipulator.getTrend();
            this.plot.setDataset(this.trendDataIndex, new XYSeriesCollection(this.trendData));
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
     * Sets wether the chart's colors are inverted or not
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
            //DomainGridlinePaint gets manually altered for a better contrast
            this.plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            this.plot.setDomainZeroBaselinePaint(this.getInvertedColor((Color) this.plot.getDomainZeroBaselinePaint()));
            this.plot.getRangeAxis().setAxisLinePaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getAxisLinePaint()));
            this.plot.getRangeAxis().setLabelPaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getLabelPaint()));
            this.plot.getRangeAxis().setTickLabelPaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getTickLabelPaint()));
            this.plot.getRangeAxis().setTickMarkPaint(this.getInvertedColor((Color) this.plot.getRangeAxis().getTickMarkPaint()));
            this.plot.setRangeCrosshairPaint(this.getInvertedColor((Color) this.plot.getRangeCrosshairPaint()));
            //RangeGridlinePaint gets manually altered for a better contrast
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
     * Sets number of discarded initial points
     * 
     * @param aNumberOfDiscardedInitialPoints Number of discarded initial points
     */
    public void setNumberOfDiscardedInitialPoints(int aNumberOfDiscardedInitialPoints) {
        this.xyChartDataManipulator.setNumberOfDiscardedInitialPoints(aNumberOfDiscardedInitialPoints);
        this.updateData();
    }
    
    /**
     * Sets the number of y-values to average
     * 
     * @param aNumberToAverage A number of y-values to average
     */
    public void setNumberToAverage(int aNumberToAverage) {
        this.numberToAverage = aNumberToAverage;
        this.updateData();
    }
    
    /**
     * Sets the zoomValues and updates the chart accordingly
     * 
     * @param aZoomValues Values for zoom
     */
    public void setZoomValues(double[] aZoomValues) {
        this.xyChartDataManipulator.setZoomValues(aZoomValues);
        this.updateData();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous">
    /**
     * Increases the number of points that are initialy discarded by one and 
     * updates the chart accordingly
     */
    public void incrementNumberOfDiscardedInitialPoints() {
        this.xyChartDataManipulator.incrementNumberOfDiscardedInitialPoints();
        this.updateData();
    }
    
    /**
     * Resets the zoom values and number of discarded initial points to their 
     * defaults and updates the chart accordingly
     */
    public void reset() {
        this.xyChartDataManipulator.reset();
        this.updateData();
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private Methods">
    /**
     * Return a DrawingSupplier that has inverted colors in respect to the 
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
     * Creates an array where the ith element contains the chart data up to the 
     * ith point of the main chart data.
     */
    private void createAccumulativeChartDataArray() {
        try {
            this.accumulativeChartDataArray = new XYSeries[this.mainChartData.getItemCount()];
            double tmpX, tmpY;
            tmpX = (double) this.mainChartData.getX(0);
            tmpY = (double) this.mainChartData.getY(0);
            this.accumulativeChartDataArray[0] = new XYSeries("XyData");
            this.accumulativeChartDataArray[0].add(tmpX, tmpY);
            for (int i = 1; i < this.mainChartData.getItemCount(); i++) {
                tmpX = (double) this.mainChartData.getX(i);
                tmpY = (double) this.mainChartData.getY(i);
                this.accumulativeChartDataArray[i] = (XYSeries) this.accumulativeChartDataArray[i - 1].clone();
                this.accumulativeChartDataArray[i].add(tmpX, tmpY);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    
    /**
     * Creates an array where the ith element contains the trend data where only
     * data up to the ith point of the updated data of the 
     * XyChartDataManipulator is considered.
     */
    private void createAccumulativeTrendDataArray() {
        try {
            this.trendDataArray = new XYSeries[this.xyChartDataManipulator.getNumberOfPointsInUpdatedData()];
            for (int i = 0; i < this.xyChartDataManipulator.getNumberOfPointsInUpdatedData(); i++) {
                this.trendDataArray[i] = this.xyChartDataManipulator.getTrend(i);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    
    /**
     * Updates the range of the axes
     */
    private void updateAxesRange() {
        try {
            double[] tmpChartRange = this.xyChartDataManipulator.getChartRange();
            this.xAxis.setRange(tmpChartRange[0], tmpChartRange[1]);
            this.yAxis.setRange(tmpChartRange[2], tmpChartRange[3]);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    
    /**
     * Update the chart data, trend data, last point data and their respective 
     * arrays if necessary and the range of the axes
     */
    private void updateData() {
        try {
            this.mainChartData = this.xyChartDataManipulator.getUpdatedXySeries(this.numberToAverage);
            this.plot.setDataset(this.mainChartDataIndex, new XYSeriesCollection(this.mainChartData));
            this.updateAxesRange();
            if (this.hasAccumulativeChartDataArray) {
                this.createAccumulativeChartDataArray();
                if (this.hasTrendLine) {
                    this.createAccumulativeTrendDataArray();
                }
            }
            if (this.hasTrendLine) {
                this.trendData = this.xyChartDataManipulator.getTrend();
                this.plot.setDataset(this.trendDataIndex, new XYSeriesCollection(this.trendData));
            }
            if (this.hasLastPointMarked) {
                this.lastPointData.clear();
                if (this.mainChartData.getItemCount() > 0) {
                    this.lastPointData.add(this.mainChartData.getX(this.mainChartData.getItemCount() - 1), this.mainChartData.getY(this.mainChartData.getItemCount() - 1));
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    // </editor-fold>
    
}
