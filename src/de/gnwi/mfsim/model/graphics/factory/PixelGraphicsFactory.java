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
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Class for pixel based graphics factory.
 *
 * @author Stefan Neumann, Achim Zielesny
 *
 */
public class PixelGraphicsFactory implements IGraphicsFactory {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility graphics methods
     */
    GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * The pixels of the image
     */
    private int[] pixels;

    /**
     * The width of the image in pixel
     */
    private int width;

    /**
     * The height of the image in pixel
     */
    private int height;

    /**
     * Red value of background color of the image
     */
    private int backgroundRed;

    /**
     * Green value of background color of the image
     */
    private int backgroundGreen;

    /**
     * Blue value of background color of the image
     */
    private int backgroundBlue;

    /**
     * Integer representation (pixel) of background color
     */
    private int backgroundColorPixel;

    /**
     * Conversion array for red values
     */
    private int[] conversionRedArray;

    /**
     * Conversion array for green values
     */
    private int[] conversionGreenArray;

    /**
     * Conversion array for blue values
     */
    private int[] conversionBlueArray;

    /**
     * True: Intermediate images are created, false: Specific intermediate image
     * is returned as intermediate image
     */
    private boolean isIntermediateImageCreation;

    /**
     * Intermediate image
     */
    private BufferedImage intermediateImage;

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
    public PixelGraphicsFactory(int aWidth, int aHeight, boolean anIsIntermediateImageCreation) {
        this.isIntermediateImageCreation = anIsIntermediateImageCreation;
        this.conversionRedArray = new int[256];
        this.conversionGreenArray = new int[256];
        this.conversionBlueArray = new int[256];
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
        this.pixels = null;
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
    public void attenuateToBackgroundColor_Parallel(double anAttenuation) {
        float tmpCorrectedAttenuation;
        if (anAttenuation < 0.0) {
            tmpCorrectedAttenuation = 0.0f;
        } else if (anAttenuation > 1.0) {
            tmpCorrectedAttenuation = 1.0f;
        } else {
            tmpCorrectedAttenuation = (float) anAttenuation;
        }
        if (tmpCorrectedAttenuation > 0.0f) {
            int tmpDefaultArrayValue = -1;
            for (int i = 0; i < this.conversionRedArray.length; i++) {
                this.conversionRedArray[i] = tmpDefaultArrayValue;
                this.conversionGreenArray[i] = tmpDefaultArrayValue;
                this.conversionBlueArray[i] = tmpDefaultArrayValue;
            }

            IntStream.range(0, this.pixels.length).parallel().forEach(
                i ->
                {
                    int tmpPixel = this.pixels[i];
                    // Color black: tmpPixel is 0 (red = 0, green = 0, blue = 0)
                    if (tmpPixel != this.backgroundColorPixel) {
                        int tmpAlpha = (tmpPixel >> 24) & 0xFF; // Do NOT change!

                        int tmpRed = (tmpPixel >> 16) & 0xFF; // red: 0-255   
                        int tmpGreen = (tmpPixel >> 8) & 0xFF; // green: 0-255
                        int tmpBlue = tmpPixel & 0xFF; // blue: 0-255

                        if (this.conversionRedArray[tmpRed] == tmpDefaultArrayValue) {
                            this.conversionRedArray[tmpRed] = tmpRed - Math.round(tmpCorrectedAttenuation * (float) (tmpRed - this.backgroundRed));
                        }
                        tmpRed = this.conversionRedArray[tmpRed];
                        if (this.conversionGreenArray[tmpGreen] == tmpDefaultArrayValue) {
                            this.conversionGreenArray[tmpGreen] = tmpGreen - Math.round(tmpCorrectedAttenuation * (float) (tmpGreen - this.backgroundGreen));
                        }
                        tmpGreen = this.conversionGreenArray[tmpGreen];
                        if (this.conversionBlueArray[tmpBlue] == tmpDefaultArrayValue) {
                            this.conversionBlueArray[tmpBlue] = tmpBlue - Math.round(tmpCorrectedAttenuation * (float) (tmpBlue - this.backgroundBlue));
                        }
                        tmpBlue = this.conversionBlueArray[tmpBlue];

                        this.pixels[i] = ((tmpAlpha << 24) & 0xFF000000) | ((tmpRed << 16) & 0x00FF0000) | ((tmpGreen << 8) & 0x0000FF00) | (tmpBlue & 0x000000FF);
                    }
                }
            );
        }
    }

    /**
     * Attenuates the color of the the main image by an attenuation towards the
     * background color. NOTE: If anAttenuation is very small there may be
     * graphical effects due to round-off errors.
     *
     * @param anAttenuation Attenuation: 1.0 or bigger means background color,
     * 0.0 or smaller means remain unchanged.
     */
    @Override
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
            int tmpPixel;
            int tmpAlpha;
            int tmpRed;
            int tmpGreen;
            int tmpBlue;
            int tmpDefaultArrayValue = -1;
            for (int i = 0; i < this.conversionRedArray.length; i++) {
                this.conversionRedArray[i] = tmpDefaultArrayValue;
                this.conversionGreenArray[i] = tmpDefaultArrayValue;
                this.conversionBlueArray[i] = tmpDefaultArrayValue;
            }

            for (int i = 0; i < this.pixels.length; i++) {
                tmpPixel = this.pixels[i];
                // Color black: tmpPixel is 0 (red = 0, green = 0, blue = 0)
                if (tmpPixel != this.backgroundColorPixel) {
                    tmpAlpha = (tmpPixel >> 24) & 0xFF; // Do NOT change!

                    tmpRed = (tmpPixel >> 16) & 0xFF; // red: 0-255   
                    tmpGreen = (tmpPixel >> 8) & 0xFF; // green: 0-255
                    tmpBlue = tmpPixel & 0xFF; // blue: 0-255

                    if (this.conversionRedArray[tmpRed] == tmpDefaultArrayValue) {
                        this.conversionRedArray[tmpRed] = tmpRed - Math.round(tmpCorrectedAttenuation * (float) (tmpRed - this.backgroundRed));
                    }
                    tmpRed = this.conversionRedArray[tmpRed];
                    if (this.conversionGreenArray[tmpGreen] == tmpDefaultArrayValue) {
                        this.conversionGreenArray[tmpGreen] = tmpGreen - Math.round(tmpCorrectedAttenuation * (float) (tmpGreen - this.backgroundGreen));
                    }
                    tmpGreen = this.conversionGreenArray[tmpGreen];
                    if (this.conversionBlueArray[tmpBlue] == tmpDefaultArrayValue) {
                        this.conversionBlueArray[tmpBlue] = tmpBlue - Math.round(tmpCorrectedAttenuation * (float) (tmpBlue - this.backgroundBlue));
                    }
                    tmpBlue = this.conversionBlueArray[tmpBlue];

                    this.pixels[i] = ((tmpAlpha << 24) & 0xFF000000) | ((tmpRed << 16) & 0x00FF0000) | ((tmpGreen << 8) & 0x0000FF00) | (tmpBlue & 0x000000FF);
                }
            }
        }
    }

    /**
     * Draws an opaque partial image at the specified x-, y-coordinate to the
     * main image. NOTE: A black background pixel must have integer value 0
     * (i.e. an alpha value of 0)! NOTE: try-catch is necessary due to rare
     * unsolved ArrayIndexOutOfBoundsException in this method caused by
     * SimulationBoxViewSingleMoveStepSlice
     *
     * @param aGraphicsObject The pixels of the image to be drawn to the main
     * image. NOTE: A black background pixel must have integer value 0 (i.e. an
     * alpha value of 0)!
     * @param anUpperLeftX The x coordinate of the upper left position
     * @param anUpperLeftY The y coordinate of the upper left position
     * @param aWidthAndHeight The width and height of the image to draw on the
     * main image (width = height)
     */
    @Override
    public void drawOpaque(Object aGraphicsObject, int anUpperLeftX, int anUpperLeftY, int aWidthAndHeight) {
        try {
            int[] tmpPixels = (int[]) aGraphicsObject;
            int tmpIndexImage = 0;
            int tmpOffset1 = anUpperLeftY * this.width + anUpperLeftX;
            if (anUpperLeftX > -1 && anUpperLeftX + aWidthAndHeight <= this.width && anUpperLeftY > -1 && anUpperLeftY + aWidthAndHeight <= this.height) {
                // <editor-fold defaultstate="collapsed" desc="Image to draw is WITHIN main image">
                for (int i = 0; i < aWidthAndHeight; i++) {
                    int tmpOffset2 = tmpOffset1 + i * this.width;
                    for (int j = 0; j < aWidthAndHeight; j++) {
                        // tmpIndexMain = (anUpperLeftY + i) * this.width + anUpperLeftX + j;
                        if (tmpPixels[tmpIndexImage] != 0) {
                            this.pixels[tmpOffset2 + j] = tmpPixels[tmpIndexImage];
                        }
                        tmpIndexImage++;
                    }
                }
                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

        }
    }

    /**
     * Draws a transparent partial image at the specified x-, y-coordinate to
     * the main image. NOTE: A black background pixel must have integer value 0
     * (i.e. an alpha value of 0)! NOTE: try-catch is necessary due to rare
     * unsolved ArrayIndexOutOfBoundsException in this method caused by
     * SimulationBoxViewSingleMoveStepSlice
     *
     * @param aGraphicsObject The pixels of the image to be drawn to the main
     * image. NOTE: A black background pixel must have integer value 0 (i.e. an
     * alpha value of 0)!
     * @param anUpperLeftX The x coordinate of the upper left position
     * @param anUpperLeftY The y coordinate of the upper left position
     * @param aWidthAndHeight The width and height of the image to draw on the
     * main image (width = height)
     */
    @Override
    public void drawTransparent(Object aGraphicsObject, int anUpperLeftX, int anUpperLeftY, int aWidthAndHeight) {
        try {
            int[] tmpPixels = (int[]) aGraphicsObject;
            int tmpIndexImage = 0;
            int tmpOffset1 = anUpperLeftY * this.width + anUpperLeftX;
            if (anUpperLeftX > -1 && anUpperLeftX + aWidthAndHeight <= this.width && anUpperLeftY > -1 && anUpperLeftY + aWidthAndHeight <= this.height) {
                // <editor-fold defaultstate="collapsed" desc="Image to draw is WITHIN main image">
                for (int i = 0; i < aWidthAndHeight; i++) {
                    int tmpOffset2 = tmpOffset1 + i * this.width;
                    for (int j = 0; j < aWidthAndHeight; j++) {
                        // tmpIndexMain = (anUpperLeftY + i) * this.width + anUpperLeftX + j;
                        if (tmpPixels[tmpIndexImage] != 0) {
                            // Merge pixels according to Porter-Duff-Source-Over-Destination rule
                            int tmpPixelSource = tmpPixels[tmpIndexImage];
                            int tmpAlphaSource = (tmpPixelSource >> 24) & 0xFF; // alpha: 0-255
                            float tmpAlphaSourceFloat = (float) tmpAlphaSource / 255f;
                            int tmpRedSource = (tmpPixelSource >> 16) & 0xFF; // red: 0-255   
                            int tmpGreenSource = (tmpPixelSource >> 8) & 0xFF; // green: 0-255
                            int tmpBlueSource = tmpPixelSource & 0xFF; // blue: 0-255

                            int tmpPixelDestination = this.pixels[tmpOffset2 + j];
                            int tmpAlphaDestination = (tmpPixelDestination >> 24) & 0xFF; // alpha: 0-255
                            float tmpAlphaDestinationFloat = (float) tmpAlphaDestination / 255f;
                            int tmpRedDestination = (tmpPixelDestination >> 16) & 0xFF; // red: 0-255   
                            int tmpGreenDestination = (tmpPixelDestination >> 8) & 0xFF; // green: 0-255
                            int tmpBlueDestination = tmpPixelDestination & 0xFF; // blue: 0-255

                            float tmpFactor = tmpAlphaDestinationFloat * (1f - tmpAlphaSourceFloat);
                            int tmpAlphaSourceOver = Math.round((tmpAlphaSourceFloat + tmpFactor) * 255f);
                            int tmpRedSourceOver = Math.round((float) tmpRedSource * tmpAlphaSourceFloat + (float) tmpRedDestination * tmpFactor);
                            int tmpGreenSourceOver = Math.round((float) tmpGreenSource * tmpAlphaSourceFloat + (float) tmpGreenDestination * tmpFactor);
                            int tmpBlueSourceOver = Math.round((float) tmpBlueSource * tmpAlphaSourceFloat + (float) tmpBlueDestination * tmpFactor);
                            int tmpPixelSourceOver = ((tmpAlphaSourceOver << 24) & 0xFF000000) | ((tmpRedSourceOver << 16) & 0x00FF0000) | ((tmpGreenSourceOver << 8) & 0x0000FF00) | (tmpBlueSourceOver & 0x000000FF);

                            this.pixels[tmpOffset2 + j] = tmpPixelSourceOver;
                        }
                        tmpIndexImage++;
                    }
                }

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
            this.pixels[aYPosition * this.width + aXPosition] = aPixelColor.getRGB();
        }
    }

    /**
     * Gets a buffered image from the internal pixel array
     *
     * @return A buffered image from the internal pixel array
     */
    public BufferedImage getImage() {
        // IMPORTANT: Copy this.pixels!
        BufferedImage tmpBufferedImage = new BufferedImage(
                ModelDefinitions.COLOR_MODEL,
                WritableRaster.createWritableRaster(ModelDefinitions.COLOR_MODEL.createCompatibleSampleModel(this.width, this.height),
                        new DataBufferInt(Arrays.copyOf(this.pixels, this.pixels.length), this.pixels.length, 0),
                        new Point(0, 0)
                ),
                true,
                null);
        return tmpBufferedImage;

//        // Transform to blurred image
//        float[] tmpKernel = {
//            1 / 9f, 1 / 9f, 1 / 9f,
//            1 / 9f, 1 / 9f, 1 / 9f,
//            1 / 9f, 1 / 9f, 1 / 9f};
//        BufferedImageOp tmpBlurredImageOp = new ConvolveOp(new Kernel(3, 3, tmpKernel), ConvolveOp.EDGE_NO_OP, null);
//        return tmpBlurredImageOp.filter(tmpBufferedImage, null);
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
            if (this.intermediateImage == null) {
                int[] tmpPixels = new int[this.pixels.length];
                Arrays.fill(tmpPixels, this.backgroundColorPixel);
                this.intermediateImage = new BufferedImage(
                        ModelDefinitions.COLOR_MODEL,
                        WritableRaster.createWritableRaster(ModelDefinitions.COLOR_MODEL.createCompatibleSampleModel(this.width, this.height),
                                new DataBufferInt(tmpPixels, tmpPixels.length, 0),
                                new Point(0, 0)
                        ),
                        true,
                        null);
            }
            return this.intermediateImage;
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
        tmpMoleculeParticleStringMap = new HashMap<>(1000);
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

    // <editor-fold defaultstate="collapsed" desc="- Test only and unused methods">
    /**
     * Attenuates the color of the the main image by an attenuation towards the
     * background color. NOTE: If aFactor is very small there may be graphical
     * effects due to round-off errors.
     *
     * @param anAttenuation Attenuation: 1.0 or bigger means background color,
     * 0.0 or smaller means remain unchanged.
     */
    public void attenuateToBackgroundColor_old(double anAttenuation) {
        int tmpPixel;
        int tmpAlpha;
        int tmpRed;
        int tmpGreen;
        int tmpBlue;
        float tmpCorrectedAttenuation;

        if (anAttenuation < 0.0) {
            tmpCorrectedAttenuation = 0.0f;
        } else if (anAttenuation > 1.0) {
            tmpCorrectedAttenuation = 1.0f;
        } else {
            tmpCorrectedAttenuation = (float) anAttenuation;
        }

        for (int i = 0; i < this.pixels.length; i++) {
            tmpPixel = this.pixels[i];
            // Color black: tmpPixel is 0 (red = 0, green = 0, blue = 0)
            if (tmpPixel != this.backgroundColorPixel) {
                tmpAlpha = (tmpPixel >> 24) & 0xFF; // Do NOT change!

                tmpRed = (tmpPixel >> 16) & 0xFF; // red: 0-255   
                tmpGreen = (tmpPixel >> 8) & 0xFF; // green: 0-255
                tmpBlue = tmpPixel & 0xFF; // blue: 0-255

                // NOTE: This code would be always correct for rounding values but too slow!
                // tmpRed = tmpRed - Math.round(tmpCorrectedAttenuation * (float) (tmpRed - this.backgroundRed));
                // tmpGreen = tmpGreen - Math.round(tmpCorrectedAttenuation * (float) (tmpGreen - this.backgroundGreen));
                // tmpBlue = tmpBlue - Math.round(tmpCorrectedAttenuation * (float) (tmpBlue - this.backgroundBlue));
                // NOTE: This faster work-around does no correct rounding but works for black and white background color!
                tmpRed = tmpRed + 1 - (int) (tmpCorrectedAttenuation * (double) (tmpRed - this.backgroundRed));
                if (tmpRed > 255) {
                    tmpRed = 255;
                }
                tmpGreen = tmpGreen + 1 - (int) (tmpCorrectedAttenuation * (double) (tmpGreen - this.backgroundGreen));
                if (tmpGreen > 255) {
                    tmpGreen = 255;
                }
                tmpBlue = tmpBlue + 1 - (int) (tmpCorrectedAttenuation * (double) (tmpBlue - this.backgroundBlue));
                if (tmpBlue > 255) {
                    tmpBlue = 255;
                }

                this.pixels[i] = ((tmpAlpha << 24) & 0xFF000000) | ((tmpRed << 16) & 0x00FF0000) | ((tmpGreen << 8) & 0x0000FF00) | (tmpBlue & 0x000000FF);
            }
        }
    }

    /**
     * Draws a partial image at the specified x-, y-coordinate to the main
     * image. NOTE: A black background pixel must have integer value 0 (i.e. an
     * alpha value of 0)!
     *
     * @param aPixels The pixels of the image to be drawn to the main image.
     * NOTE: A black background pixel must have integer value 0 (i.e. an alpha
     * value of 0)!
     * @param anUpperLeftX The x coordinate of the upper left position
     * @param anUpperLeftY The y coordinate of the upper left position
     * @param aWidth The width of the image to draw on the main image
     * @param aHeight The height of the image to draw on the main image
     */
    public void drawToImage(int[] aPixels, int anUpperLeftX, int anUpperLeftY, int aWidth, int aHeight) {
        try {
            int tmpIndexImage = 0;
            int tmpOffset1 = anUpperLeftY * this.width + anUpperLeftX;
            if (anUpperLeftX > -1 && anUpperLeftX + aWidth <= this.width && anUpperLeftY > -1 && anUpperLeftY + aHeight <= this.height) {
                // <editor-fold defaultstate="collapsed" desc="Image to draw is WITHIN main image">
                for (int i = 0; i < aHeight; i++) {
                    int tmpOffset2 = tmpOffset1 + i * this.width;
                    for (int j = 0; j < aWidth; j++) {
                        // tmpIndexMain = (anUpperLeftY + i) * this.width + anUpperLeftX + j;
                        if (aPixels[tmpIndexImage] != 0) {
                            this.pixels[tmpOffset2 + j] = aPixels[tmpIndexImage];
                        }
                        tmpIndexImage++;
                    }
                }

                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Image to draw is OUTSIDE main image">
                for (int i = 0; i < aHeight; i++) {
                    int tmpOffset2 = tmpOffset1 + i * this.width;
                    for (int j = 0; j < aWidth; j++) {
                        // tmpIndexMain = (anUpperLeftY + i) * this.width + anUpperLeftX + j;
                        if (anUpperLeftX + j > -1 && anUpperLeftX + j < this.width && anUpperLeftY + i > -1 && anUpperLeftY + i < this.height) {
                            if (aPixels[tmpIndexImage] != 0) {
                                this.pixels[tmpOffset2 + j] = aPixels[tmpIndexImage];
                            }
                        }
                        tmpIndexImage++;
                    }
                }

                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

        }
    }

    /**
     * Draws a partial image at the specified x-, y-coordinate to the main
     * image. NOTE: A black background pixel must have integer value 0 (i.e. an
     * alpha value of 0)!
     *
     * @param aPixels The pixels of the image to be drawn to the main image.
     * NOTE: A black background pixel must have integer value 0 (i.e. an alpha
     * value of 0)!
     * @param anUpperLeftX The x coordinate of the upper left position
     * @param anUpperLeftY The y coordinate of the upper left position
     * @param aWidthAndHeight The width and height of the image to draw on the
     * main image (width = height)
     */
    public void drawToImage_old(int[] aPixels, int anUpperLeftX, int anUpperLeftY, int aWidthAndHeight) {
        // NOTE: try-catch is necessary due to rare unsolved ArrayIndexOutOfBoundsException in this method caused by SimulationBoxViewSingleMoveStepSlice
        try {
            int tmpIndexImage = 0;
            int tmpOffset1 = anUpperLeftY * this.width + anUpperLeftX;
            if (anUpperLeftX > -1 && anUpperLeftX + aWidthAndHeight <= this.width && anUpperLeftY > -1 && anUpperLeftY + aWidthAndHeight <= this.height) {
                // <editor-fold defaultstate="collapsed" desc="Image to draw is WITHIN main image">
                for (int i = 0; i < aWidthAndHeight; i++) {
                    int tmpOffset2 = tmpOffset1 + i * this.width;
                    for (int j = 0; j < aWidthAndHeight; j++) {
                        // tmpIndexMain = (anUpperLeftY + i) * this.width + anUpperLeftX + j;
                        if (aPixels[tmpIndexImage] != 0) {
                            this.pixels[tmpOffset2 + j] = aPixels[tmpIndexImage];
                        }
                        tmpIndexImage++;
                    }
                }

                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Image to draw is OUTSIDE main image">
                for (int i = 0; i < aWidthAndHeight; i++) {
                    int tmpOffset2 = tmpOffset1 + i * this.width;
                    for (int j = 0; j < aWidthAndHeight; j++) {
                        // tmpIndexMain = (anUpperLeftY + i) * this.width + anUpperLeftX + j;
                        if (anUpperLeftX + j > -1 && anUpperLeftX + j < this.width && anUpperLeftY + i > -1 && anUpperLeftY + i < this.height) {
                            if (aPixels[tmpIndexImage] != 0) {
                                this.pixels[tmpOffset2 + j] = aPixels[tmpIndexImage];
                            }
                        }
                        tmpIndexImage++;
                    }
                }

                // </editor-fold>
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

        }
    }

    // </editor-fold>
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- initialize">
    /**
     * Initializes the class
     *
     * @param aWidth The width of the image to draw on
     * @param aHeight The height of the image to draw on
     */
    private void initialize(int aWidth, int aHeight) {
        this.intermediateImage = null;

        this.width = aWidth;
        this.height = aHeight;

        Color tmpBackgroundColor = Preferences.getInstance().getSimulationBoxBackgroundColorSlicer();
        this.backgroundColorPixel = tmpBackgroundColor.getRGB();
        this.backgroundRed = tmpBackgroundColor.getRed();
        this.backgroundGreen = tmpBackgroundColor.getGreen();
        this.backgroundBlue = tmpBackgroundColor.getBlue();

        this.pixels = new int[aWidth * aHeight];
        Arrays.fill(this.pixels, this.backgroundColorPixel);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Graphics related methods">
    /**
     * Returns pixels for specified particle graphics. NOTE: No checks are
     * performed
     *
     * @param aRadiusInPixel Radius in pixel
     * @param aFractions Fractions for aColors
     * @param aColors Colors for RadialGradientPaint
     * @param aTransparency Transparency
     * @return Pixels for specified image
     */
    private int[] getPixelsOfParticleGraphics(int aRadiusInPixel, float[] aFractions, Color[] aColors, float aTransparency) {
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
            RadialGradientPaint tmpRadialGradientPaint = 
                new RadialGradientPaint(
                    tmpRadiusInPixelAsFloat * Preferences.getInstance().getRadialGradientPaintFocusFactorX(),
                    tmpRadiusInPixelAsFloat * Preferences.getInstance().getRadialGradientPaintFocusFactorY(),
                    tmpRadiusInPixelAsFloat * Preferences.getInstance().getRadialGradientPaintRadiusMagnification(),
                    aFractions,
                    aColors
                );
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set RadialGradientPaint">
            if (aTransparency > 0f) {
                // Make particle pixels translucent with aTransparency: Note transparency of 0% corresponds to alpha value of 1            
                tmpGraphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - aTransparency));
            }
            tmpGraphics2D.setPaint(tmpRadialGradientPaint);
            tmpGraphics2D.fillOval(0, 0, tmpDiameterInPixel, tmpDiameterInPixel);
            // </editor-fold>
            int[] tmpPixels = this.getPixelsOfImage(tmpImage);
            return tmpPixels;
        } finally {
            if (tmpGraphics2D != null) {
                tmpGraphics2D.dispose();
            }
        }
    }

    /**
     * Returns pixels of an image
     *
     * @param anImage The image to get the pixel from
     *
     * @return The pixels represented by an integer array.
     *
     */
    private int[] getPixelsOfImage(BufferedImage anImage) {
        int[] tmpPixels = new int[anImage.getWidth() * anImage.getHeight()];
        PixelGrabber tmpGrabber = new PixelGrabber(anImage, 0, 0, anImage.getWidth(), anImage.getHeight(), tmpPixels, 0, anImage.getWidth());
        try {
            tmpGrabber.grabPixels(0);
        } catch (InterruptedException anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
        return tmpPixels;
    }
    
    /**
     * Set particle properties
     * (No checks are performed)
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
        int[] tmpPixels = this.getPixelsOfParticleGraphics(tmpRadiusInPixel, tmpfractions, tmpColors, tmpGraphicalParticle.getCurrentParticleTransparency());
        tmpGraphicalParticle.setRadiusInPixel(tmpRadiusInPixel);
        tmpGraphicalParticle.setGraphicsObject(tmpPixels);
    }
    // </editor-fold>
    // </editor-fold>

}
