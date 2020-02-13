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
package de.gnwi.mfsim.model.graphics;
/**
 * Target coordinates and size
 * 
 * @author Achim Zielesny
 */
public class TargetCoordinatesAndSize {
    
    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * X-coordinate of target panel within master panel
     */
    private final int xCoordinateOfTargetPanelWithinMasterPanel;

    /**
     * Y-coordinate of target panel within master panel
     */
    private final int yCoordinateOfTargetPanelWithinMasterPanel;
    
    /**
     * Width of target panel within master panel
     */
    private final int widthOfTargetPanelWithinMasterPanel;
    
    /**
     * Height of target panel within master panel
     */
    private final int heightOfTargetPanelWithinMasterPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor
     * NOTE: NO checks are performed
     * 
     * @param aXcoordinateOfTargetPanelWithinMasterPanel X-coordinate of target panel within master panel
     * @param aYcoordinateOfTargetPanelWithinMasterPanel Y-coordinate of target panel within master panel
     * @param aWidthOfTargetPanelWithinMasterPanel Width of target panel within master panel
     * @param aHeightOfTargetPanelWithinMasterPanel Height of target panel within master panel
     */
    public TargetCoordinatesAndSize(
        int aXcoordinateOfTargetPanelWithinMasterPanel,
        int aYcoordinateOfTargetPanelWithinMasterPanel,
        int aWidthOfTargetPanelWithinMasterPanel,
        int aHeightOfTargetPanelWithinMasterPanel
    ) {
        this.xCoordinateOfTargetPanelWithinMasterPanel = aXcoordinateOfTargetPanelWithinMasterPanel;
        this.yCoordinateOfTargetPanelWithinMasterPanel = aYcoordinateOfTargetPanelWithinMasterPanel;
        this.widthOfTargetPanelWithinMasterPanel = aWidthOfTargetPanelWithinMasterPanel;
        this.heightOfTargetPanelWithinMasterPanel = aHeightOfTargetPanelWithinMasterPanel;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * X-coordinate of target panel within master panel with x-shift in pixel
     * 
     * @param aXshiftInPixel X-shift in pixel
     * @return X-coordinate with x-shift of target panel within master panel
     */
    public int getXcoordinateWithXshift(int aXshiftInPixel) {
        return this.xCoordinateOfTargetPanelWithinMasterPanel + aXshiftInPixel;
    }

    /**
     * Y-coordinate of target panel within master panel with y-shift in pixel.
     * 
     * @param aYshiftInPixel Y-shift in pixel
     * @return  Y-coordinate with y-shift of target panel within master panel
     */
    public int getYcoordinateWithYshift(int aYshiftInPixel) {
        // IMPORTANT: "-" because screen y-coordinate runs in "opposite direction"
        return this.yCoordinateOfTargetPanelWithinMasterPanel - aYshiftInPixel;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get)">
    /**
     * Width of target panel within master panel
     * 
     * @return Width of target panel within master panel
     */
    public int getWidth() {
        return this.widthOfTargetPanelWithinMasterPanel;
    }

    /**
     * Height of target panel within master panel
     * 
     * @return Height of target panel within master panel
     */
    public int getHeight() {
        return this.heightOfTargetPanelWithinMasterPanel;
    }
    // </editor-fold>
    
}
