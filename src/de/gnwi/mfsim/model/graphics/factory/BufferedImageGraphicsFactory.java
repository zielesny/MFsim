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
package de.gnwi.mfsim.model.graphics.factory;

import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionArrayList;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticle;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RadialGradientPaint;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Class for pixel based graphics factory.
 *
 * @author Achim Zielesny
 *
 */
public class BufferedImageGraphicsFactory implements IGraphicsFactory {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility graphics methods
     */
    GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * Master image of this factory
     */
    private BufferedImage masterImage;

    /**
     * Graphics environment of master image
     */
    private Graphics2D masterImageGraphics2D;

    /**
     * The width of the image in pixel
     */
    private int width;

    /**
     * The height of the image in pixel
     */
    private int height;

    /**
     * Image for attenuation to background color
     */
    private BufferedImage attenuationImage;

    /**
     * Attenuation corresponding to this.attenuationImage
     */
    private float attenuation;

    /**
     * True: Intermediate images are created, false: Attenuation image is
     * returned as intermediate image
     */
    private boolean isIntermediateImageCreation;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aWidth The width of the main image in pixel
     * @param aHeight The height of the main image in pixel
     * @param anIsIntermediateImageCreation True: Intermediate images are
     * created, false: Attenuation image is returned as intermediate image
     */
    public BufferedImageGraphicsFactory(int aWidth, int aHeight, boolean anIsIntermediateImageCreation) {
        this.isIntermediateImageCreation = anIsIntermediateImageCreation;
        this.initialize(aWidth, aHeight);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- clear and cleanUp">
    /**
     * Clears the main image
     */
    public void clear() {
        this.initialize(this.width, this.height);
    }

    /**
     * Clean up
     */
    public void cleanUp() {
        if (this.masterImageGraphics2D != null) {
            this.masterImageGraphics2D.dispose();
        }
    }
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
    public void attenuateToBackgroundColor(double anAttenuation) {
        float tmpCorrectedAttenuation;
        if (anAttenuation < 0.0) {
            tmpCorrectedAttenuation = 0.0f;
        } else if (anAttenuation > 1.0) {
            tmpCorrectedAttenuation = 1.0f;
        } else {
            tmpCorrectedAttenuation = (float) anAttenuation;
        }
        if (tmpCorrectedAttenuation > 0.0f) {
            if (this.attenuationImage == null || tmpCorrectedAttenuation != this.attenuation) {
                // NOTE: Set this.attenuation FIRST ...
                this.attenuation = tmpCorrectedAttenuation;
                // ... THEN set attenuation image
                this.setAttenuationImage();
            }
            this.masterImageGraphics2D.drawImage(this.attenuationImage, 0, 0, null);
        }
    }

    /**
     * Draws an opaque partial image at the specified x-, y-coordinate to the
     * main image. NOTE: try-catch is necessary due to rare unsolved
     * ArrayIndexOutOfBoundsException in this method caused by
     * SimulationBoxViewSingleMoveStepSlice
     *
     * @param aGraphicsObject Opaque buffered image
     * @param anUpperLeftX The x coordinate of the upper left position
     * @param anUpperLeftY The y coordinate of the upper left position
     * @param aWidthAndHeight The width and height of the image to draw on the
     * main image (width = height)
     */
    public void drawOpaque(Object aGraphicsObject, int anUpperLeftX, int anUpperLeftY, int aWidthAndHeight) {
        try {
            BufferedImage tmpImage = (BufferedImage) aGraphicsObject;
            if (anUpperLeftX > -1 && anUpperLeftX + aWidthAndHeight <= this.width && anUpperLeftY > -1 && anUpperLeftY + aWidthAndHeight <= this.height) {
                // <editor-fold defaultstate="collapsed" desc="Image to draw is WITHIN main image">
                this.masterImageGraphics2D.drawImage(tmpImage, anUpperLeftX, anUpperLeftY, null);
                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

        }
    }

    /**
     * Draws a transparent partial image at the specified x-, y-coordinate to
     * the main image. NOTE: try-catch is necessary due to rare unsolved
     * ArrayIndexOutOfBoundsException in this method caused by
     * SimulationBoxViewSingleMoveStepSlice
     *
     * @param aGraphicsObject Transparent buffered image
     * @param anUpperLeftX The x coordinate of the upper left position
     * @param anUpperLeftY The y coordinate of the upper left position
     * @param aWidthAndHeight The width and height of the image to draw on the
     * main image (width = height)
     */
    public void drawTransparent(Object aGraphicsObject, int anUpperLeftX, int anUpperLeftY, int aWidthAndHeight) {
        try {
            BufferedImage tmpImage = (BufferedImage) aGraphicsObject;
            if (anUpperLeftX > -1 && anUpperLeftX + aWidthAndHeight <= this.width && anUpperLeftY > -1 && anUpperLeftY + aWidthAndHeight <= this.height) {
                // <editor-fold defaultstate="collapsed" desc="Image to draw is WITHIN main image">
                this.masterImageGraphics2D.drawImage(tmpImage, anUpperLeftX, anUpperLeftY, null);
                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

        }
    }

    /**
     * Draws single pixel
     *
     * @param aXPosition x-position of pixel
     * @param aYPosition y-position of pixel
     * @param aPixelColor Color of pixel
     */
    public void drawSinglePixel(int aXPosition, int aYPosition, Color aPixelColor) {
        // NOTE: aXPosition and aYPosition are zero-based! -> aXPosition < this.width and NOT aXPosition <= this.width (the same is valid for this.height)
        if (aXPosition > -1 && aXPosition < this.width && aYPosition > -1 && aYPosition < this.height) {
            this.masterImage.setRGB(aXPosition, aYPosition, aPixelColor.getRGB());
        }
    }

    /**
     * Gets a buffered image from the internal pixel array
     *
     * @return A buffered image from the internal pixel array
     */
    public BufferedImage getImage() {
        Graphics2D tmpGraphics2D = null;
        try {
            BufferedImage tmpBufferedImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
            tmpGraphics2D = tmpBufferedImage.createGraphics();
            tmpGraphics2D.drawImage(this.masterImage, 0, 0, null);
            tmpGraphics2D.dispose();
            return tmpBufferedImage;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        } finally {
            if (tmpGraphics2D != null) {
                tmpGraphics2D.dispose();
            }
        }
    }

    /**
     * Gets an intermediate buffered image from the internal pixel array
     *
     * @return An intermediate buffered image from the internal pixel array
     */
    public BufferedImage getIntermediateImage() {
        if (this.isIntermediateImageCreation) {
            return this.getImage();
        } else {
            if (this.attenuationImage == null) {
                // NOTE: Set this.attenuation FIRST ...
                this.attenuation = 0.0f;
                // ... THEN set attenuation image
                this.setAttenuationImage();
            }
            return this.attenuationImage;
        }
    }

    /**
     * Adds particle graphics to graphical particle position array list
     *
     * @param aPixelTransformationFactor Pixel transformation factor
     * @param aGraphicalParticlePositionArrayList Graphical particle position array list
     * @param aBoxSizeInfo Box size info
     */
    @Override
    public void addParticleGraphics(
        double aPixelTransformationFactor, 
        GraphicalParticlePositionArrayList aGraphicalParticlePositionArrayList, 
        BoxSizeInfo aBoxSizeInfo
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aGraphicalParticlePositionArrayList == null || aGraphicalParticlePositionArrayList.getSize() == 0) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Local variables">
        HashMap<String, String> tmpMoleculeParticleStringMap;
        HashMap<String, String> tmpSelectionInfoStringMap;
        float[] tmpfractions;
        Color[] tmpColors;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialize">
        // Number of molecule particles is O(100): Set capacity to 1000
        tmpMoleculeParticleStringMap = new HashMap<String, String>(1000);
        tmpSelectionInfoStringMap = new HashMap<>(1000);
        tmpfractions = new float[]{0.0f, Preferences.getInstance().getSpecularWhiteSizeSlicer(), 1.0f};
        tmpColors = new Color[3];
        // </editor-fold>
        GraphicalParticlePosition[] tmpGraphicalParticlePositions = aGraphicalParticlePositionArrayList.getGraphicalParticlePositions();
        for (int i = 0; i < aGraphicalParticlePositionArrayList.getSize(); i++) {
            GraphicalParticlePosition tmpCurrentGraphicalParticlePosition = tmpGraphicalParticlePositions[i];
            GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpCurrentGraphicalParticlePosition.getGraphicalParticle();
            if (!tmpCurrentGraphicalParticlePosition.isInFrame()) {
                // <editor-fold defaultstate="collapsed" desc="Set properties of particle of molecule">
                if (tmpGraphicalParticle.hasSelectionColor()) {
                    if (!tmpSelectionInfoStringMap.containsKey(tmpGraphicalParticle.getSelectionInfoString())) {
                        this.setParticleProperties(
                            tmpGraphicalParticle,
                            tmpGraphicalParticle.getSelectionColor().toColor(),
                            aPixelTransformationFactor,
                            tmpfractions,
                            tmpColors
                        );
                        tmpSelectionInfoStringMap.put(
                            tmpGraphicalParticle.getSelectionInfoString(),
                            tmpGraphicalParticle.getSelectionInfoString()
                        );
                    }
                } else if (!tmpMoleculeParticleStringMap.containsKey(tmpGraphicalParticle.getMoleculeParticleString())) {
                    Color tmpGraphicalParticleColor = null;
                    switch (Preferences.getInstance().getParticleColorDisplayMode()) {
                        case MOLECULE_COLOR_MODE:
                            tmpGraphicalParticleColor = tmpGraphicalParticle.getMoleculeColor();
                            break;
                        case MOLECULE_PARTICLE_COLOR_MODE:
                            tmpGraphicalParticleColor = tmpGraphicalParticle.getMoleculeParticleColor();
                            break;
                        case PARTICLE_COLOR_MODE:
                            tmpGraphicalParticleColor = tmpGraphicalParticle.getParticleColor();
                            break;
                    }
                    this.setParticleProperties(
                        tmpGraphicalParticle,
                        tmpGraphicalParticleColor,
                        aPixelTransformationFactor,
                        tmpfractions,
                        tmpColors
                    );
                    tmpMoleculeParticleStringMap.put(
                        tmpGraphicalParticle.getMoleculeParticleString(),
                        tmpGraphicalParticle.getMoleculeParticleString()
                    );
                }
                // </editor-fold>
            }
            // <editor-fold defaultstate="collapsed" desc="Set relative coordinates">
            tmpCurrentGraphicalParticlePosition.setMiddlePositionInPixelX((int) ((tmpCurrentGraphicalParticlePosition.getX() - aBoxSizeInfo.getRotationDisplayFrameXMin()) * aPixelTransformationFactor));
            tmpCurrentGraphicalParticlePosition.setMiddlePositionInPixelY((int) ((tmpCurrentGraphicalParticlePosition.getY() - aBoxSizeInfo.getRotationDisplayFrameYMin()) * aPixelTransformationFactor));
            tmpCurrentGraphicalParticlePosition.setMiddlePositionInPixelZ((int) ((tmpCurrentGraphicalParticlePosition.getZ() - aBoxSizeInfo.getRotationDisplayFrameZMin()) * aPixelTransformationFactor));

            // </editor-fold>
        }
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- initialize">
    /**
     * Initialises the class
     *
     * @param aWidth The width of the image to draw on
     * @param aHeight The height of the image to draw on
     */
    private void initialize(int aWidth, int aHeight) {
        this.width = aWidth;
        this.height = aHeight;
        // NOTE: Transparency.TRANSLUCENT means that black background color of created image has integer value 0 (i.e. an alpha value of 0) when converted to integer pixel!
        this.masterImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(
                this.width, this.height, Transparency.TRANSLUCENT);
        // Alternative code:
        // this.masterImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        this.masterImage.setAccelerationPriority(1.0f);
        this.masterImageGraphics2D = this.masterImage.createGraphics();
        this.masterImageGraphics2D.setColor(Preferences.getInstance().getSimulationBoxBackgroundColorSlicer());
        this.masterImageGraphics2D.fillRect(0, 0, this.width, this.height);
        this.masterImageGraphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        this.attenuation = 0f;
        this.attenuationImage = null;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Graphics related methods">
    /**
     * Returns image for specified particle graphics. NOTE: No checks are
     * performed
     *
     * @param aRadiusInPixel Radius in pixel
     * @param aFractions Fractions for aColors
     * @param aColors Colors for RadialGradientPaint
     * @param aTransparency Transparency
     * @return Pixels for specified image
     */
    private BufferedImage getImageOfParticleGraphics(int aRadiusInPixel, float[] aFractions, Color[] aColors, float aTransparency) {
        Graphics2D tmpGraphics2D = null;
        try {
            // <editor-fold defaultstate="collapsed" desc="Create image">
            int tmpDiameterInPixel = aRadiusInPixel * 2;
            float tmpRadiusInPixelAsFloat = (float) aRadiusInPixel;
            // NOTE: Transparency.TRANSLUCENT means that black background color of created image has integer value 0 (i.e. an alpha value of 0) when converted to integer pixel!
            BufferedImage tmpImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(
                    tmpDiameterInPixel, tmpDiameterInPixel, Transparency.TRANSLUCENT);
            // Alternative code:
            // NOTE: BufferedImage.TYPE_INT_ARGB allows pixels with integer value 0 (i.e. an alpha value of 0) when converted to integer pixel
            // BufferedImage tmpImage = new BufferedImage(tmpDiameterInPixel, tmpDiameterInPixel, BufferedImage.TYPE_INT_ARGB);
            tmpGraphics2D = tmpImage.createGraphics();

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="RadialGradientPaint for 3D effect">
            RadialGradientPaint tmpRadialGradientPaint = new RadialGradientPaint(
                    tmpRadiusInPixelAsFloat * Preferences.getInstance().getRadialGradientPaintFocusFactorX(),
                    tmpRadiusInPixelAsFloat * Preferences.getInstance().getRadialGradientPaintFocusFactorY(),
                    tmpRadiusInPixelAsFloat * Preferences.getInstance().getRadialGradientPaintRadiusMagnification(),
                    aFractions,
                    aColors);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set RadialGradientPaint">
            if (aTransparency > 0.0f) {
                // Make particle pixels translucent with aTransparency: Note transparency of 0% corresponds to alpha value of 1            
                tmpGraphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - aTransparency));
            }
            tmpGraphics2D.setPaint(tmpRadialGradientPaint);
            tmpGraphics2D.fillOval(0, 0, tmpDiameterInPixel, tmpDiameterInPixel);

            // </editor-fold>
            return tmpImage;
        } finally {
            if (tmpGraphics2D != null) {
                tmpGraphics2D.dispose();
            }
        }
    }

    /**
     * Sets attenuation image
     */
    private void setAttenuationImage() {
        Graphics2D tmpGraphics2D = null;
        try {
            this.attenuationImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(
                    this.width, this.height, Transparency.TRANSLUCENT);
            // Alternative code:
            // this.attenuationImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

            this.attenuationImage.setAccelerationPriority(1.0f);
            // ImageCapabilities tmpImageCapabilities = this.attenuationImage.getCapabilities(null);
            // System.out.println("Is this.attenuationImage accelerated = " + String.valueOf(tmpImageCapabilities.isAccelerated()));

            tmpGraphics2D = this.attenuationImage.createGraphics();
            tmpGraphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.attenuation));
            tmpGraphics2D.setColor(Preferences.getInstance().getSimulationBoxBackgroundColorSlicer());
            tmpGraphics2D.fillRect(0, 0, this.width, this.height);
        } finally {
            if (tmpGraphics2D != null) {
                tmpGraphics2D.dispose();
            }
        }
    }
    
    /**
     * Set particle properties
     * NOTE: NO checks are performed.
     * 
     * @param tmpGraphicalParticle See calling method
     * @param tmpGraphicalParticleColor See calling method
     * @param aPixelTransformationFactor See calling method
     * @param tmpfractions See calling method
     * @param tmpColors See calling method
     */
    private void setParticleProperties(
        GraphicalParticle tmpGraphicalParticle,
        Color tmpGraphicalParticleColor,
        double aPixelTransformationFactor,
        float[] tmpfractions,
        Color[] tmpColors
    ) {
        int tmpRadiusInPixel = (int) (tmpGraphicalParticle.getCurrentParticleRadius() * aPixelTransformationFactor);
        // IMPORTANT: 
        // If simulation box contains a large number of particles 
        // tmpRadiusInPixel may be rounded to zero (which is wrong and leads to 
        // an exception below)!
        // In this case tmpRadiusInPixel is set to a minimum value of 1 pixel.
        if (tmpRadiusInPixel <= 0) {
            tmpRadiusInPixel = 1;
        }
        this.graphicsUtilityMethods.setGradientColors(tmpGraphicalParticleColor, tmpColors);
        BufferedImage tmpImage = this.getImageOfParticleGraphics(tmpRadiusInPixel, tmpfractions, tmpColors, tmpGraphicalParticle.getCurrentParticleTransparency());
        tmpGraphicalParticle.setRadiusInPixel(tmpRadiusInPixel);
        tmpGraphicalParticle.setGraphicsObject(tmpImage);
    }
    // </editor-fold>
    // </editor-fold>

}
