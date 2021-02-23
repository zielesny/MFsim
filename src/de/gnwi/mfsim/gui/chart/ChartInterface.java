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

import java.awt.image.BufferedImage;

/**
 * Chart interface
 * 
 * @author Jan-Mathis Hein
 */
public interface ChartInterface {
    
    /**
     * Returns the chart as a buffered image with the given width and height
     * 
     * @param aWidth Width of the image
     * @param aHeight Height of the image 
     * @return The chart as a buffered image with the given width and height
     */
    public BufferedImage getImage(int aWidth, int aHeight);
    
    /**
     * Returns the ith element of the chart (where i is anIndex) as a buffered 
     * image with the given width and height
     * 
     * @param aWidth Width of the image
     * @param aHeight Height of the image 
     * @param anIndex Index for chart element
     * @return The ith element of the chart (where i is anIndex) as a buffered 
     * image with the given width and height
     */
    public BufferedImage getImage(int aWidth, int aHeight, int anIndex);
    
    /**
     * Returns the statistics of the chart or a message that there are no 
     * statistics available
     * 
     * @return The statistics of the chart or a message that there are no 
     * statistics available
     */
    public String getStatisticsString();
    
}
