package de.gnwi.mfsim.custom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Utility class for chart generation
 * 
 * @author Achim Zielesny
 */
public class ChartUtil {
    
    /**
     * Tiny threshold
     */
    private final static double TINY_THRESHOLD = 1E-6;
    /**
     * Name of data series
     */
    private final static String DATA_SERIES_NAME = "XyData";
    /**
     * Whitespace pattern
     */
    private final static Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    /**
     * Buffer size (64 kByte = 65536, 256 kByte = 262144, 512 kByte = 524288, 1
     * MByte = 1048576 Byte)
     */
    private final static int BUFFER_SIZE = 65536;
    /**
     * Full HD image size
     */
    private final static int[] FULL_HD_IMAGE_SIZE = new int[] {1920, 1080};
    
    /**
     * Constructor
     */
    public ChartUtil() {
        // Nothing to initialize
    }

    /**
     * Creates energy graphics files in Full HD resolution in folder of 
     * aEnergyDataPathName.
     * 
     * @param aEnergyDataPathName File pathname with energy data
     * @param anEnergyGraphicsPrefix Prefix of energy graphics filenames
     * @return True: Operation was successful, false: Otherwise
     */
    public boolean createEnergyGraphics(
        String aEnergyDataPathName, 
        String anEnergyGraphicsPrefix
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aEnergyDataPathName == null || aEnergyDataPathName.isEmpty()) {
            return false;
        }
        if (anEnergyGraphicsPrefix == null || anEnergyGraphicsPrefix.isEmpty()) {
            return false;
        }
        if (!(new File(aEnergyDataPathName)).isFile()) {
            return false;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Read raw energy data">
            String[][] tmpEnergyData = this.readJaggedStringArrayFromFile(aEnergyDataPathName);
            if (tmpEnergyData == null || tmpEnergyData.length < 2) {
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set diagram labels">
            if (tmpEnergyData[0].length != 6) {
                return false;
            }
            String tmpXaxisLabel = tmpEnergyData[0][0] + " " + tmpEnergyData[0][1];
            String tmpYaxisLabel1 = tmpEnergyData[0][2] + " " + tmpEnergyData[0][3];
            String tmpYaxisLabel2 = tmpEnergyData[0][4] + " " + tmpEnergyData[0][5];
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Convert energy data">
            // Convert energy data
            double[] tmpDistances = new double[tmpEnergyData.length - 1];
            double[] tmpEminValues = new double[tmpEnergyData.length - 1];
            double[] tmpEaverageValues = new double[tmpEnergyData.length - 1];
            for (int i = 1; i < tmpEnergyData.length; i++) {
                tmpDistances[i - 1] = Double.valueOf(tmpEnergyData[i][0]);
                tmpEminValues[i - 1] = Double.valueOf(tmpEnergyData[i][1]);
                tmpEaverageValues[i - 1] = Double.valueOf(tmpEnergyData[i][2]);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create and save chart 1: Distance vs. Emin">
            String tmpDiagramLabel1 = tmpYaxisLabel1 + " vs. " + tmpXaxisLabel;
            JFreeChart tmpEminVsDistanceChart = 
                this.createXyChart(
                    tmpDistances, 
                    tmpEminValues, 
                    tmpDiagramLabel1, 
                    tmpXaxisLabel, 
                    tmpYaxisLabel1, 
                    true,  // anIsThickLines
                    true,  // anIsShapePaint
                    true,  // anIsFillColorWhite
                    false  // anIsOutlinePaintWhite
                );
            if (tmpEminVsDistanceChart == null) {
                return false;
            }
            BufferedImage tmpEminVsDistanceImage = 
                this.getImage(
                    tmpEminVsDistanceChart, 
                    FULL_HD_IMAGE_SIZE[0], 
                    FULL_HD_IMAGE_SIZE[1]
                );
            if (tmpEminVsDistanceImage == null) {
                return false;
            }
            String tmpEminVsDistanceImagePathname = 
                new File(aEnergyDataPathName).getParent() + 
                File.separatorChar + 
                anEnergyGraphicsPrefix + 
                "DistanceVsEmin.jpg";
            if (!ChartUtil.writeJpegImageToFile(
                    tmpEminVsDistanceImage, 
                    new File(tmpEminVsDistanceImagePathname)
                )
            ) {
                return false;
            };
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create and save chart 1: Distance vs. Eaverage">
            String tmpDiagramLabel2 = tmpYaxisLabel2 + " vs. " + tmpXaxisLabel;
            JFreeChart tmpEaverageVsDistanceChart = 
                this.createXyChart(
                    tmpDistances, 
                    tmpEaverageValues, 
                    tmpDiagramLabel2, 
                    tmpXaxisLabel, 
                    tmpYaxisLabel2, 
                    true,  // anIsThickLines
                    true,  // anIsShapePaint
                    true,  // anIsFillColorWhite
                    false  // anIsOutlinePaintWhite
                );
            if (tmpEaverageVsDistanceChart == null) {
                return false;
            }
            BufferedImage tmpEaverageVsDistanceImage = 
                this.getImage(
                    tmpEaverageVsDistanceChart, 
                    FULL_HD_IMAGE_SIZE[0], 
                    FULL_HD_IMAGE_SIZE[1]
                );
            if (tmpEaverageVsDistanceImage == null) {
                return false;
            }
            String tmpEaverageVsDistanceImagePathname = 
                new File(aEnergyDataPathName).getParent() + 
                File.separatorChar + 
                anEnergyGraphicsPrefix + 
                "DistanceVsEaverage.jpg";
            if (!ChartUtil.writeJpegImageToFile(
                    tmpEaverageVsDistanceImage, 
                    new File(tmpEaverageVsDistanceImagePathname)
                )
            ) {
                return false;
            };
            // </editor-fold>
            return true;
        } catch (Exception anException) {
            return false;
        }
    }
    
    /**
     * Creates XY chart from x and y values.
     *
     * @param aXValues X values
     * @param aYValues Y values
     * @param aTitle Title of diagram
     * @param aXAxisLabel Label of x-axis
     * @param aYAxisLabel Label of y-axis
     * @param anIsThickLines True: Thick lines, false: Thin lines
     * @param anIsShapePaint True: Shapes are painted, false: Shapes are not
     * painted
     * @param anIsFillColorWhite If shapes are painted: True: Fill color of
     * shapes is white, false: Fill color of shapes is default color
     * @param anIsOutlinePaintWhite If shapes are painted: True: Outline of
     * shapes is white, false: Outline of shapes is default color
     * @param aNumberToAverage Number of y-values to average
     * @return Chart or null if chart could not be created
     */
    private JFreeChart createXyChart(
        double[] aXValues, 
        double[] aYValues,
        String aTitle,
        String aXAxisLabel,
        String aYAxisLabel,
        boolean anIsThickLines,
        boolean anIsShapePaint, 
        boolean anIsFillColorWhite, 
        boolean anIsOutlinePaintWhite
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aXValues == null || aYValues == null || aXValues.length != aYValues.length) {
            return null;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="- Create data series">
            XYSeries tmpXySeries = new XYSeries(DATA_SERIES_NAME);

            double tmpXmin;
            double tmpXmax;
            double tmpYmin;
            double tmpYmax;
            tmpXmin = Double.MAX_VALUE;
            tmpXmax = -Double.MAX_VALUE;
            tmpYmin = Double.MAX_VALUE;
            tmpYmax = -Double.MAX_VALUE;
            for (int i = 0; i < aXValues.length; i++) {
                // Safeguard for NaN values in value item
                try {
                    if (Double.isNaN(aXValues[i]) || Double.isNaN(aYValues[i])) {
                        continue;
                    }
                } catch (Exception anException) {
                    continue;
                }
                tmpXmin = Math.min(tmpXmin, aXValues[i]);
                tmpXmax = Math.max(tmpXmax, aXValues[i]);
                tmpYmin = Math.min(tmpYmin, aYValues[i]);
                tmpYmax = Math.max(tmpYmax, aYValues[i]);
                tmpXySeries.add(aXValues[i], aYValues[i]);
            }
            double tmpXoffset;
            if (Math.abs(tmpXmax - tmpXmin) < tmpXmax * TINY_THRESHOLD) {
                tmpXoffset = (tmpXmax + tmpXmin) * 0.5 * 0.05;
            } else {
                tmpXoffset = (tmpXmax - tmpXmin) * 0.05;
            }
            double tmpX1 = tmpXmin - tmpXoffset;
            double tmpX2 = tmpXmax + tmpXoffset;
            double tmpYoffset;
            if (Math.abs(tmpYmax - tmpYmin) < tmpYmax * TINY_THRESHOLD) {
                tmpYoffset = (tmpYmax + tmpYmin) * 0.5 * 0.05;
            } else {
                tmpYoffset = (tmpYmax - tmpYmin) * 0.05;
            }
            double tmpY1 = tmpYmin - tmpYoffset;
            double tmpY2 = tmpYmax + tmpYoffset;

            XYDataset tmpXyDataset = new XYSeriesCollection(tmpXySeries);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Create chart">
            JFreeChart tmpXyChart = 
                ChartFactory.createXYLineChart(
                    aTitle, // Title
                    aXAxisLabel, // xAxisLabel
                    aYAxisLabel, // yAxisLabel
                    tmpXyDataset, // dataset
                    PlotOrientation.VERTICAL, // orientation
                    false, // legend flag
                    false, // tooltips flag
                    false  // URLs flag
                ); 
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set axis ranges">
            XYPlot tmpXyPlot = (XYPlot) tmpXyChart.getPlot();
            ValueAxis tmpXaxis = tmpXyPlot.getDomainAxis();
            tmpXaxis.setRange(tmpX1, tmpX2);
            ValueAxis tmpYaxis = tmpXyPlot.getRangeAxis();
            tmpYaxis.setRange(tmpY1, tmpY2);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set outline, shapes and thickness">
            XYLineAndShapeRenderer tmpRenderer = (XYLineAndShapeRenderer) tmpXyPlot.getRenderer();
            tmpRenderer.setPaint(Color.BLACK);
            if (anIsThickLines) {
                tmpRenderer.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            }
            if (anIsShapePaint) {
                tmpRenderer.setShape(new Ellipse2D.Float(-5.0f, -5.0f, 10.0f, 10.0f));
                tmpRenderer.setShapesVisible(true);
                tmpRenderer.setShapesFilled(true);
                if (anIsFillColorWhite) {
                    tmpRenderer.setUseFillPaint(true);
                    tmpRenderer.setFillPaint(Color.white);
                } else if (anIsOutlinePaintWhite) {
                    tmpRenderer.setOutlinePaint(Color.white);
                    tmpRenderer.setUseOutlinePaint(true);
                }
            }
            // </editor-fold>
            return tmpXyChart;
        } catch (Exception anException) {
            return null;
        }
    }

    /**
     * Returns an image of the chart
     * 
     * @param aChart Chart
     * @param aWidth Width of the image
     * @param aHeight Height of the image
     * @return An image of the chart or null if image can not be created
     */
    private BufferedImage getImage(
        JFreeChart aChart, 
        int aWidth, 
        int aHeight
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aChart == null || aWidth <= 0 || aHeight <= 0) {
            return null;
        }
        // </editor-fold>
        try {
            return aChart.createBufferedImage(aWidth, aHeight);
        } catch (Exception anException) {
            return null;
        }
    }

    /**
     * Reads jagged string array from file. Each line is splitted after one or
     * more whitespace characters.
     *
     * @param aSourceFilePathname Full pathname of source (may be null then null
     * is returned)
     * @return Jagged string array or null if jagged string array could not be
     * read
     */
    private String[][] readJaggedStringArrayFromFile(
        String aSourceFilePathname
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSourceFilePathname == null || aSourceFilePathname.isEmpty()) {
            return null;
        }
        // </editor-fold>
        BufferedReader tmpBufferedReader = null;
        try {
            if (!(new File(aSourceFilePathname)).isFile()) {
                return null;
            }
            FileReader tmpFileReader = new FileReader(aSourceFilePathname);
            tmpBufferedReader = 
                new BufferedReader(tmpFileReader, BUFFER_SIZE);
            LinkedList<String[]> tmpLinkedList = new LinkedList<String[]>();
            String tmpLine;
            while ((tmpLine = tmpBufferedReader.readLine()) != null) {
                String[] tmpItems = this.splitAndTrim(tmpLine.trim());
                if (tmpItems != null) {
                    tmpLinkedList.add(tmpItems);
                }
            }
            return tmpLinkedList.toArray(new String[0][]);
        } catch (Exception anException) {
            return null;
        } finally {
            if (tmpBufferedReader != null) {
                try {
                    tmpBufferedReader.close();
                } catch (IOException anException) {
                    return null;
                }
            }
        }
    }
    
    /**
     * Splits a string into a string array after one or more whitespace
     * characters.
     *
     * @param aString String to split
     * @return A string array containing the trimmed split strings or null if
     * aString was null or empty
     */
    private String[] splitAndTrim(
        String aString
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return null;
        }
        // </editor-fold>
        String[] tmpItems = WHITESPACE_PATTERN.split(aString.trim());
        if (tmpItems == null || tmpItems.length == 0) {
            return null;
        }
        // NOTE: Trim-operation is not necessary since all whitespace characters 
        // are removed
        return tmpItems;
    }

    /**
     * Writes JPEG image with defined quality to file. NOTE: ImageIO is NOT
     * thread safe! (thus synchronized)
     *
     * @param anImage Image
     * @param aFile File object
     * @return True: Operation was successful, false: Otherwise
     */
    private static synchronized boolean writeJpegImageToFile(
        BufferedImage anImage, 
        File aFile
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anImage == null) {
            return false;
        }
        if (aFile == null) {
            return false;
        }
        try {
            if (aFile.isFile()) {
                if (!aFile.delete()) {
                    return false;
                }
            }
        } catch (Exception anException) {
            return false;
        }
        // </editor-fold>
        try {
            Iterator<ImageWriter> tmpIterator = ImageIO.getImageWritersByFormatName("jpg");
            if (tmpIterator.hasNext()) {
                ImageWriter tmpImageWriter = tmpIterator.next();
                ImageOutputStream tmpIoStream = ImageIO.createImageOutputStream(aFile);
                tmpImageWriter.setOutput(tmpIoStream);
                ImageWriteParam tmpParameters = tmpImageWriter.getDefaultWriteParam();
                tmpParameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                // Parameter 1.0f: High image quality
                tmpParameters.setCompressionQuality(1.0f);
                tmpImageWriter.write(null, new IIOImage(anImage, null, null), tmpParameters);
                tmpIoStream.flush();
                tmpImageWriter.dispose();
                tmpIoStream.close();
                return true;
            } else {
                return false;
            }
        } catch (Exception anException) {
            return false;
        }
    }
    
}
