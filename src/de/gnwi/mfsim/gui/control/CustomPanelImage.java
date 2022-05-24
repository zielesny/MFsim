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
package de.gnwi.mfsim.gui.control;

import de.gnwi.mfsim.model.graphics.point.PointInPlane;
import de.gnwi.mfsim.model.preference.Preferences;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Panel for fast display of image
 *
 * @author Achim Zielesny
 */
public class CustomPanelImage extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    
    /**
     * Image
     */
    private BufferedImage image;

    /**
     * Centered mode flag: true: Image will be centered and not distorted
     * (default), false: Image fills panel and may be distorted
     */
    private boolean isCenteredMode;

    /**
     * True: Unscaled drawing of image, false: Otherwise
     */
    private boolean isUnscaledDrawing;

    /**
     * Scale mode of image
     */
    private int scaleMode;

    /**
     * Saved scaled image for performance purposes
     */
    private Image oldScaledImage;

    /**
     * Saved height for performance purposes
     */
    private int oldHeight;

    /**
     * Saved width for performance purposes
     */
    private int oldWidth;

    /**
     * Ratio of height to width of image
     */
    private double ratioOfHeightToWidth;

    /**
     * X-coordinate of point 1
     */
    private double x1;

    /**
     * Y-coordinate of point 1
     */
    private double y1;

    /**
     * X-coordinate of point 2
     */
    private double x2;

    /**
     * Y-coordinate of point 2
     */
    private double y2;

    /**
     * True: Line between point 1 and 2 is dashed, false: Line between point 1
     * and 2 is solid.
     */
    private boolean isLineDashed;

    /**
     * Parameter for line drawing
     */
    private float strokeWidthLine;

    /**
     * Parameter for line drawing Attenuation: float tmpAlpha = 0.5f;
     */
    private float mIterlimit;

    /**
     * Parameter for line drawing
     */
    private float[] dashPattern;

    /**
     * Parameter for line drawing
     */
    private float dashPhase;

    /**
     * Dashed stroke
     */
    BasicStroke dashedStroke;

    /**
     * Non-dashed stroke
     */
    BasicStroke nonDashedStroke;

    /**
     * Box edge point array. The following points will be connected (indices):
     * 0-1, 0-2, 0-3, 1-4, 1-6, 2-4, 2-5, 3-5, 3-6, 4-7, 5-7, 6-7
     */
    PointInPlane[] boxEdgePointArray;

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000007L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelImage() {
        super();
        // <editor-fold defaultstate="collapsed" desc="Initialisation">
        
        // No image defined
        this.image = null;
        // Default: Centered mode
        this.isCenteredMode = true;
        // No old scaled image
        this.oldScaledImage = null;
        // Smooth-scaling of image is default
        this.scaleMode = Image.SCALE_SMOOTH;
        // Scaled drawing of image
        this.isUnscaledDrawing = false;
        // Points initialisation
        this.x1 = -1.0;
        this.y1 = -1.0;
        this.x2 = -1.0;
        this.y2 = -1.0;
        // Box edge points initialisation
        this.boxEdgePointArray = null;

        this.isLineDashed = false;
        this.strokeWidthLine = 1.0f;
        this.mIterlimit = 10.0f;
        this.dashPattern = new float[]{3.0f, 3.0f};
        this.dashPhase = 0.0f;
        this.dashedStroke = new BasicStroke(this.strokeWidthLine, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, this.mIterlimit, this.dashPattern, this.dashPhase);
        this.nonDashedStroke = new BasicStroke(this.strokeWidthLine, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        
        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Protected methods">
    /**
     * Paint component
     *
     * @param g Graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.boxEdgePointArray != null) {
            // <editor-fold defaultstate="collapsed" desc="Box painting">
            Graphics2D g2D = (Graphics2D) g;

            // Simulation box background color
            g2D.setColor(Preferences.getInstance().getSimulationBoxBackgroundColorSlicer());
            // GUI color background
            // g2D.setColor(StandardColorEnum.GUI.toColor());
            g2D.fillRect(0, 0, this.getWidth(), this.getHeight());

            g2D.setPaint(Preferences.getInstance().getFrameColorSlicer());
            // g2D.setPaint(Color.BLUE);

            // Draw box lines
            // 0-1
            g2D.setStroke(this.nonDashedStroke);
            Line2D tmpLine = new Line2D.Double(this.boxEdgePointArray[0].getX(), this.boxEdgePointArray[0].getY(), this.boxEdgePointArray[1].getX(), this.boxEdgePointArray[1].getY());
            g2D.draw(tmpLine);
            // 0-2
            g2D.setStroke(this.dashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[0].getX(), this.boxEdgePointArray[0].getY(), this.boxEdgePointArray[2].getX(), this.boxEdgePointArray[2].getY());
            g2D.draw(tmpLine);
            // 0-3
            g2D.setStroke(this.nonDashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[0].getX(), this.boxEdgePointArray[0].getY(), this.boxEdgePointArray[3].getX(), this.boxEdgePointArray[3].getY());
            g2D.draw(tmpLine);
            // 1-4
            g2D.setStroke(this.dashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[1].getX(), this.boxEdgePointArray[1].getY(), this.boxEdgePointArray[4].getX(), this.boxEdgePointArray[4].getY());
            g2D.draw(tmpLine);
            // 1-6
            g2D.setStroke(this.nonDashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[1].getX(), this.boxEdgePointArray[1].getY(), this.boxEdgePointArray[6].getX(), this.boxEdgePointArray[6].getY());
            g2D.draw(tmpLine);
            // 2-4
            g2D.setStroke(this.dashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[2].getX(), this.boxEdgePointArray[2].getY(), this.boxEdgePointArray[4].getX(), this.boxEdgePointArray[4].getY());
            g2D.draw(tmpLine);
            // 2-5
            g2D.setStroke(this.dashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[2].getX(), this.boxEdgePointArray[2].getY(), this.boxEdgePointArray[5].getX(), this.boxEdgePointArray[5].getY());
            g2D.draw(tmpLine);
            // 3-5
            g2D.setStroke(this.dashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[3].getX(), this.boxEdgePointArray[3].getY(), this.boxEdgePointArray[5].getX(), this.boxEdgePointArray[5].getY());
            g2D.draw(tmpLine);
            // 3-6
            g2D.setStroke(this.nonDashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[3].getX(), this.boxEdgePointArray[3].getY(), this.boxEdgePointArray[6].getX(), this.boxEdgePointArray[6].getY());
            g2D.draw(tmpLine);
            // 4-7
            g2D.setStroke(this.dashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[4].getX(), this.boxEdgePointArray[4].getY(), this.boxEdgePointArray[7].getX(), this.boxEdgePointArray[7].getY());
            g2D.draw(tmpLine);
            // 5-7
            g2D.setStroke(this.dashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[5].getX(), this.boxEdgePointArray[5].getY(), this.boxEdgePointArray[7].getX(), this.boxEdgePointArray[7].getY());
            g2D.draw(tmpLine);
            // 6-7
            g2D.setStroke(this.dashedStroke);
            tmpLine = new Line2D.Double(this.boxEdgePointArray[6].getX(), this.boxEdgePointArray[6].getY(), this.boxEdgePointArray[7].getX(), this.boxEdgePointArray[7].getY());
            g2D.draw(tmpLine);
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Image painting">
            if (this.image != null) {
                Dimension tmpDimension = this.getSize();
                if (this.isUnscaledDrawing) {
                    // <editor-fold defaultstate="collapsed" desc="Draw unscaled image">
                    if (this.isCenteredMode) {
                        // <editor-fold defaultstate="collapsed" desc="Centered mode">
                        int tmpWidth = 0;
                        int tmpHeight = 0;
                        if (this.image.getWidth() <= tmpDimension.width && this.image.getHeight() <= tmpDimension.height) {
                            tmpWidth = (tmpDimension.width - this.image.getWidth()) / 2;
                            tmpHeight = (tmpDimension.height - this.image.getHeight()) / 2;
                        }
                        g.drawImage(this.image, tmpWidth, tmpHeight, this);
                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="Non-centered mode">
                        g.drawImage(this.image, 0, 0, this);
                        // </editor-fold>
                    }
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Draw scaled image">
                    double tmpRatioOfHeightToWidthOfPanel = (double) tmpDimension.height / (double) tmpDimension.width;
                    if (this.isCenteredMode) {
                        // <editor-fold defaultstate="collapsed" desc="Centered mode">
                        if (this.image.getWidth() == tmpDimension.width && this.image.getHeight() == tmpDimension.height) {
                            // <editor-fold defaultstate="collapsed" desc="Correct size: Scaling of image NOT necessary">
                            this.oldScaledImage = this.image;
                            this.oldWidth = tmpDimension.width;
                            this.oldHeight = tmpDimension.height;
                            g.drawImage(this.oldScaledImage, 0, 0, this);
                            // </editor-fold>
                        } else {
                            // <editor-fold defaultstate="collapsed" desc="Size differs: Scale image">
                            if (this.oldScaledImage == null || this.oldWidth != tmpDimension.width || this.oldHeight != tmpDimension.height) {
                                if (tmpRatioOfHeightToWidthOfPanel >= this.ratioOfHeightToWidth) {
                                    this.oldScaledImage = this.image.getScaledInstance(tmpDimension.width, -1, this.scaleMode);
                                } else {
                                    this.oldScaledImage = this.image.getScaledInstance(-1, tmpDimension.height, this.scaleMode);
                                }
                                this.oldWidth = tmpDimension.width;
                                this.oldHeight = tmpDimension.height;
                            }
                            // Draw centered image
                            g.drawImage(this.oldScaledImage, (tmpDimension.width - this.oldScaledImage.getWidth(null)) / 2, (tmpDimension.height - this.oldScaledImage.getHeight(null)) / 2, this);
                            // </editor-fold>
                        }
                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="Non-centered mode">
                        // Get scaled image to take image scale mode into account
                        if (this.oldScaledImage == null || this.oldWidth != tmpDimension.width || this.oldHeight != tmpDimension.height) {
                            this.oldScaledImage = this.image.getScaledInstance(tmpDimension.width, tmpDimension.height, this.scaleMode);
                            this.oldWidth = tmpDimension.width;
                            this.oldHeight = tmpDimension.height;
                        }
                        // Draw non-centered image
                        g.drawImage(this.oldScaledImage, 0, 0, this);
                        // </editor-fold>
                    }
                    // </editor-fold>
                }
                // <editor-fold defaultstate="collapsed" desc="Draw points and line in between if specified">
                if (this.x1 >= 0.0 && this.y1 >= 0.0) {
                    if (this.x2 < 0.0 && this.y2 < 0.0) {
                        Graphics2D g2D = (Graphics2D) g;
                        g2D.setPaint(Preferences.getInstance().getMeasurementColorSlicer());

                        this.drawTargetCircle(g2D, this.x1, this.y1);
                    } else {
                        Graphics2D g2D = (Graphics2D) g;
                        g2D.setPaint(Preferences.getInstance().getMeasurementColorSlicer());

                        this.drawTargetCircle(g2D, this.x1, this.y1);
                        this.drawTargetCircle(g2D, this.x2, this.y2);
                        if (this.isLineDashed) {
                            g2D.setStroke(this.dashedStroke);
                        } else {
                            g2D.setStroke(this.nonDashedStroke);
                        }
                        // Line with attenuation: g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tmpAlpha));
                        Line2D tmpLine = new Line2D.Double(this.x1, this.y1, this.x2, this.y2);
                        g2D.draw(tmpLine);
                    }
                }

                // </editor-fold>
            }

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    // <editor-fold defaultstate="collapsed" desc="CenteredMode">
    /**
     * Centered mode flag
     *
     * @return true: Image will be centered and not distorted (default), false:
     * Image fills panel and may be distorted
     */
    public boolean isCenteredMode() {
        return this.isCenteredMode;
    }

    /**
     * Centered mode flag with repaint of image
     *
     * @param aValue true: Image will be centered and not distorted (default),
     * false: Image fills panel and may be distorted
     */
    public void setCenteredMode(boolean aValue) {
        if (this.isCenteredMode != aValue) {
            this.isCenteredMode = aValue;
            this.repaint();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Image">
    /**
     * Get image
     *
     * @return Image
     */
    public BufferedImage getImage() {
        BufferedImage tmpBufferedImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        this.paint(tmpBufferedImage.getGraphics());
        return tmpBufferedImage;
    }

    /**
     * Get basic (original) image (which was set)
     *
     * @return Image
     */
    public BufferedImage getBasicImage() {
        return this.image;
    }

    /**
     * Set image
     *
     * @param anImage Image (may be null)
     */
    public void setBasicImage(BufferedImage anImage) {
        this.image = anImage;
        if (anImage != null) {
            this.ratioOfHeightToWidth = (double) this.image.getHeight(null) / (double) this.image.getWidth(null);
            // Important: Remove box edge points (otherwise image can not be displayed)
            this.removeBoxEdgePoints();
        }
        this.oldScaledImage = null;
        this.repaint();
    }

    /**
     * Returns if panel has image
     *
     * @return True: Panel has image, false: Otherwise
     */
    public boolean hasBasicImage() {
        return this.image != null;
    }

    /**
     * Removes image
     */
    public void removeBasicImage() {
        this.setBasicImage(null);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DrawUnscaled">
    /**
     * Sets unscaled drawing of image
     *
     * @param aValue True: Unscaled drawing of image, false: Otherwise
     */
    public void setDrawUnscaled(boolean aValue) {
        this.isUnscaledDrawing = aValue;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ScaleModeSmooth">
    /**
     * Sets scale mode
     *
     * @param aValue True: Smooth-scaling of image (default), false:
     * Fast-scaling of image
     */
    public void setScaleModeSmooth(boolean aValue) {
        if (aValue) {
            this.scaleMode = Image.SCALE_SMOOTH;
        } else {
            this.scaleMode = Image.SCALE_FAST;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Measurement points and line dash definition related methods">
    /**
     * Set point 1 to be drawn
     * 
     * @param aX1 X1
     * @param aY1 Y1
     */
    public void setPoint1(double aX1, double aY1) {
        this.x1 = aX1;
        this.y1 = aY1;
        this.repaint();
    }

    /**
     * Set point 2 to be drawn with line between point 1
     * 
     * @param aX2 X2
     * @param aY2 Y2
     */
    public void setPoint2(double aX2, double aY2) {
        this.x2 = aX2;
        this.y2 = aY2;
        this.repaint();
    }

    /**
     * Clear points
     */
    public void clearPoints() {
        this.x1 = -1.0;
        this.y1 = -1.0;
        this.x2 = -1.0;
        this.y2 = -1.0;
        this.isLineDashed = false;
        this.repaint();
    }

    /**
     * True: Line between point 1 and 2 is dashed, false: Line between point 1
     * and 2 is solid.
     *
     * @param aFlag True: Line between point 1 and 2 is dashed, false: Line
     * between point 1 and 2 is solid.
     */
    public void setLineDashed(boolean aFlag) {
        this.isLineDashed = aFlag;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Box edge points related methods">
    /**
     * Box edge point array. The following points will be connected (indices):
     * 0-1, 0-2, 0-3, 1-4, 1-6, 2-4, 2-5, 3-5, 3-6, 4-7, 5-7, 6-7
     *
     * @param aBoxEdgePointArray Box edge point array (see convention for
     * connection above)
     */
    public void setBoxEdgePoints(PointInPlane[] aBoxEdgePointArray) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.boxEdgePointArray != null && this.boxEdgePointArray.length != 8) {
            return;
        }

        // </editor-fold>
        this.boxEdgePointArray = aBoxEdgePointArray;
        this.repaint();
    }

    /**
     * Removes box edge points
     */
    public void removeBoxEdgePoints() {
        this.setBoxEdgePoints(null);
    }

    /**
     * Returns if box edge points are specified
     *
     * @return True: Box edge points are specified, false: Otherwise
     */
    public boolean hasBoxEdgePoints() {
        return this.boxEdgePointArray != null;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Draws target circle at specified position
     *
     * @param ag2D Graphics instance
     * @param aX1 X-coordinate
     * @param aY1 Y-coordinate
     */
    private void drawTargetCircle(Graphics2D aG2D, double aX1, double aY1) {
        double tmpCircleDiameter = 20.0;
        double tmpCircleRadius = 0.5 * tmpCircleDiameter;
        double tmpOffset = 3.0;
        float tmpAlpha = 0.25f;
        float tmpStrokeWidthCross = 1.0f;

        aG2D.setStroke(new BasicStroke(tmpStrokeWidthCross, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Line2D tmpLine = new Line2D.Double(aX1 + tmpOffset, aY1, aX1 + tmpCircleRadius, aY1);
        aG2D.draw(tmpLine);
        tmpLine = new Line2D.Double(aX1 - tmpOffset, aY1, aX1 - tmpCircleRadius, aY1);
        aG2D.draw(tmpLine);
        tmpLine = new Line2D.Double(aX1, aY1 + tmpOffset, aX1, aY1 + tmpCircleRadius);
        aG2D.draw(tmpLine);
        tmpLine = new Line2D.Double(aX1, aY1 - tmpOffset, aX1, aY1 - tmpCircleRadius);
        aG2D.draw(tmpLine);
        Ellipse2D tmpCircle = new Ellipse2D.Double(aX1 - tmpCircleRadius, aY1 - tmpCircleRadius, tmpCircleDiameter, tmpCircleDiameter);
        // aG2D.draw(tmpCircle);
        aG2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tmpAlpha));
        aG2D.fill(tmpCircle);

        if (aX1 - tmpCircleRadius - tmpOffset > 0) {
            tmpLine = new Line2D.Double(0.0, aY1, aX1 - tmpCircleRadius - tmpOffset, aY1);
            aG2D.draw(tmpLine);
        }
        if (aX1 + tmpCircleRadius + tmpOffset < this.oldWidth) {
            tmpLine = new Line2D.Double(aX1 + tmpCircleRadius + tmpOffset, aY1, this.oldWidth, aY1);
            aG2D.draw(tmpLine);
        }
        if (aY1 - tmpCircleRadius - tmpOffset > 0) {
            tmpLine = new Line2D.Double(aX1, 0.0, aX1, aY1 - tmpCircleRadius - tmpOffset);
            aG2D.draw(tmpLine);
        }
        if (aY1 + tmpCircleRadius + tmpOffset < this.oldHeight) {
            tmpLine = new Line2D.Double(aX1, aY1 + tmpCircleRadius + tmpOffset, aX1, this.oldHeight);
            aG2D.draw(tmpLine);
        }

        aG2D.setComposite(AlphaComposite.SrcOver);
    }
    // </editor-fold>

}
