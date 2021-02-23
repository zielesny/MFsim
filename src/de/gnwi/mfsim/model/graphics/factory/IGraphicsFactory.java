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
package de.gnwi.mfsim.model.graphics.factory;

import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionArrayList;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Interface for graphics factory
 *
 * @author Achim Zielesny
 */
public interface IGraphicsFactory {

    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- clear and cleanUp">
    /**
     * Clears the main image
     */
    public void clear();

    /**
     * Clean up
     */
    public void cleanUp();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Graphics related methods">

    /**
     * Attenuates the color of the the main image by an attenuation towards the
     * background color. NOTE: If anAttenuation is very small there may be
     * graphical effects due to round-off errors.
     *
     * @param anAttenuation Attenuation: 1.0 or bigger means background color,
     * 0.0 or smaller means remain unchanged.
     */
    public void attenuateToBackgroundColor(double anAttenuation);

    /**
     * Draws an opaque partial image at the specified x-, y-coordinate to the
     * main image.
     *
     * @param aGraphicsObject Graphics object (image) to be drawn
     * @param anUpperLeftX The x coordinate of the upper left position
     * @param anUpperLeftY The y coordinate of the upper left position
     * @param aWidthAndHeight The width and height of the image to draw on the
     * main image (width = height)
     */
    public void drawOpaque(Object aGraphicsObject, int anUpperLeftX, int anUpperLeftY, int aWidthAndHeight);

    /**
     * Draws a transparent partial image at the specified x-, y-coordinate to
     * the main image.
     *
     * @param aGraphicsObject Graphics object (image) to be drawn
     * @param anUpperLeftX The x coordinate of the upper left position
     * @param anUpperLeftY The y coordinate of the upper left position
     * @param aWidthAndHeight The width and height of the image to draw on the
     * main image (width = height)
     */
    public void drawTransparent(Object aGraphicsObject, int anUpperLeftX, int anUpperLeftY, int aWidthAndHeight);

    /**
     * Draws single pixel
     *
     * @param aXPosition x-position of pixel
     * @param aYPosition y-position of pixel
     * @param aPixelColor Color of pixel
     */
    public void drawSinglePixel(int aXPosition, int aYPosition, Color aPixelColor);

    /**
     * Gets a buffered image from the internal image
     *
     * @return A buffered image from the internal image
     */
    public BufferedImage getImage();

    /**
     * Gets an intermediate buffered image from the internal image
     *
     * @return An intermediate buffered image from the internal image
     */
    public BufferedImage getIntermediateImage();

    /**
     * Adds particle graphics to graphical particle position array list
     *
     * @param aPixelTransformationFactor Pixel transformation factor
     * @param aGraphicalParticlePositionArrayList Graphical particle position array list
     * @param aBoxSizeInfo Box size info
     */
    public void addParticleGraphics(double aPixelTransformationFactor, GraphicalParticlePositionArrayList aGraphicalParticlePositionArrayList, BoxSizeInfo aBoxSizeInfo);
    // </editor-fold>
    // </editor-fold>

}
