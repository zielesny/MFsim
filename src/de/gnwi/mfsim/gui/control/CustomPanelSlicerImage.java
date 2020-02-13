/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2020  Achim Zielesny (achim.zielesny@googlemail.com)
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

import de.gnwi.mfsim.model.graphics.TargetCoordinatesAndSize;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;

/**
 * Implements slicer graphics panel with fixed y:x ratio
 *
 * @author Achim Zielesny
 */
public class CustomPanelSlicerImage extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private CustomPanelImage drawPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    
    /**
     * Height to width ratio
     */
    private double ratioOfHeightToWidth;

    /**
     * Graphics utility methods
     */
    private GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * TargetCoordinatesAndSize instance for draw panel (see method 
     * setBoundsOfDrawPanel())
     */
    private TargetCoordinatesAndSize drawPanelCoordinatesAndSize;
    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000037L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelSlicerImage() {
        super();
        setLayout(null);

        // <editor-fold defaultstate="collapsed" desc="Initialize this.ratioOfHeightToWidth">
        
        this.ratioOfHeightToWidth = 1.0;
        this.drawPanelCoordinatesAndSize = null;
        
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.drawPanel">
        {
            this.drawPanel = new CustomPanelImage();
            this.drawPanel.setBackground(Color.black);
            this.drawPanel.setLayout(null);

            // <editor-fold defaultstate="collapsed" desc="Initialize bounds of draw panel">
            
            this.setBoundsOfDrawPanel();

            
            // </editor-fold>
            add(this.drawPanel);
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Add listener for component resize">
        addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                CustomPanelSlicerImage.this.setBoundsOfDrawPanel();
            }

        });

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public CustomPanelImage getDrawPanel() {
        return drawPanel;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Height to width ratio if draw panel
     *
     * @return Height to width ratio
     */
    public double getRatioOfHeightToWidth() {
        return this.ratioOfHeightToWidth;
    }

    /**
     * Height to width ratio of draw panel
     *
     * @param aRatio Height to width ratio
     */
    public void setRatioOfHeightToWidth(double aRatio) {
        if (aRatio > 0) {
            this.ratioOfHeightToWidth = aRatio;
            this.setBoundsOfDrawPanel();
        }
    }
    
    /**
     * TargetCoordinatesAndSize instance for draw panel
     * 
     * @return TargetCoordinatesAndSize instance for draw panel (may be null if
     * not calculated)
     */
    public TargetCoordinatesAndSize getDrawPanelCoordinatesAndSize() {
        return this.drawPanelCoordinatesAndSize;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Sets bounds of draw panel
     */
    private void setBoundsOfDrawPanel() {
        // IMPORTANT: Check dimension since UtilityGraphics.getTargetCoordinatesAndSize() throws error for negative/zero size
        Dimension tmpDimension = this.getSize();
        if (tmpDimension.width > 0 && tmpDimension.height > 0) {
            this.drawPanelCoordinatesAndSize = this.graphicsUtilityMethods.getTargetCoordinatesAndSize(tmpDimension, this.ratioOfHeightToWidth);
            // Old code:
            // this.drawPanel.setBounds(
            //     this.drawPanelCoordinatesAndSize.getXcoordinate(), 
            //     this.drawPanelCoordinatesAndSize.getYcoordinate(), 
            //     this.drawPanelCoordinatesAndSize.getWidth(), 
            //     this.drawPanelCoordinatesAndSize.getHeight()
            // );
            this.drawPanel.setBounds(
                0, 
                0, 
                tmpDimension.width, 
                tmpDimension.height
            );
        }
    }
    // </editor-fold>

}
