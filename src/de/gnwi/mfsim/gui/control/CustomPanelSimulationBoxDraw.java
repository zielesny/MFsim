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
 * Implements simulation box draw panel with fixed y:x ratio
 *
 * @author Achim Zielesny
 */
public class CustomPanelSimulationBoxDraw extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private CustomPanelSimulationBoxGraphics drawPanel;
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
    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000030L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CustomPanelSimulationBoxDraw constructor">
    /**
     * Constructor
     */
    public CustomPanelSimulationBoxDraw() {
        super();
        setLayout(null);

        // <editor-fold defaultstate="collapsed" desc="Initialize this.ratioOfHeightToWidth">
        

        this.ratioOfHeightToWidth = 1.0;

        
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="this.drawPanel">

        {
            this.drawPanel = new CustomPanelSimulationBoxGraphics();
            this.drawPanel.setBackground(Color.BLACK);
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
                CustomPanelSimulationBoxDraw.this.setBoundsOfDrawPanel();
            }
        });

        // </editor-fold>

    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public CustomPanelSimulationBoxGraphics getGraphicsPanel() {
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

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    
    /**
     * Sets bounds of draw panel
     */
    private void setBoundsOfDrawPanel() {
        // IMPORTANT: Check dimension since
        // UtilityGraphics.getTargetCoordinatesAndSize() throws error for
        // negative/zero size
        Dimension tmpDimension = this.getSize();
        if (tmpDimension.width > 0 && tmpDimension.height > 0) {
            TargetCoordinatesAndSize tmpTargetCoordinatesAndSize = this.graphicsUtilityMethods.getTargetCoordinatesAndSize(tmpDimension, this.ratioOfHeightToWidth);
            this.drawPanel.setBounds(
                //IMPORTANT: NO x- or y-shift!
tmpTargetCoordinatesAndSize.getXcoordinateWithXshift(0), 
                tmpTargetCoordinatesAndSize.getYcoordinateWithYshift(0), 
                tmpTargetCoordinatesAndSize.getWidth(), 
                tmpTargetCoordinatesAndSize.getHeight()
            );
        }
    }
    
    // </editor-fold>
}
