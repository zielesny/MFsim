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
package de.gnwi.mfsim.model.graphics.slice;

import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.factory.BufferedImageGraphicsFactory;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.model.graphics.factory.PixelGraphicsFactory;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.graphics.TargetCoordinatesAndSize;
import de.gnwi.mfsim.model.graphics.factory.VolatileImageGraphicsFactory;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionArrayList;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticle;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ImageStorageEnum;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.spices.IPointInSpace;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import de.gnwi.mfsim.model.graphics.factory.IGraphicsFactory;

/**
 * Creates a number of image files for displaying different plains in a
 * simulation box. The box is able to display a large number of molecular
 * particles within the box. Therefore the architecture is partially optimised
 * for speed.
 *
 * @author Stefan Neumann, Achim Zielesny
 *
 */
public class SimulationBoxViewSlicer {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Graphics utility methods
     */
    private GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * The destination directory of the image files
     */
    private String destinationDirectory;

    /**
     * Box size info
     */
    private BoxSizeInfo boxSizeInfo;

    /**
     * Box view
     */
    private SimulationBoxViewEnum boxView;

    /**
     * The width in pixel of the master panel
     */
    private int masterPanelWidthInPixel;

    /**
     * The height in pixel of the master panel
     */
    private int masterPanelHeightInPixel;

    /**
     * Stores pairs of indices of the graphicsObject array which determine the
     * range of objects belonging to a slice.
     */
    private Slice[] slices;

    /**
     * The type of images to create: bmp, jpg, png ...
     */
    private ImageFileType imageFileType;

    /**
     * Map for key to created slice image pathname
     */
    private HashMap<String, String> keyToCreatedSliceImagePathnameMap;

    /**
     * Graphical particle position array list
     */
    private GraphicalParticlePositionArrayList graphicalParticlePositionArrayList;

    /**
     * Array with slice images for in-memory slicing
     */
    private BufferedImage[] sliceImageArray;

    /**
     * Array with compressed slice images for in-memory slicing
     */
    private byte[][] sliceImageByteArray;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates a new instance. NOTE:
     *
     * @param aBoxView Box view
     * @param aDestinationDirectory The destination directory for the images
     * @param aBoxSizeInfo Box size info
     * @param aGraphicalParticlePositionArrayList Graphical particle position 
     * array list (may be null, then empty images are created). NOTE: Array 
     * sort order as well as its elements MAY BE CHANGED.
     * @param anImageFileType File type of the images to be created
     * @param anMasterPanelWidthInPixel The width in pixel of the master panel
     * @param anMasterPanelHeightInPixel The height in pixel of the master panel
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public SimulationBoxViewSlicer(
        SimulationBoxViewEnum aBoxView, 
        String aDestinationDirectory, 
        BoxSizeInfo aBoxSizeInfo, 
        GraphicalParticlePositionArrayList aGraphicalParticlePositionArrayList,
        ImageFileType anImageFileType, 
        int anMasterPanelWidthInPixel, 
        int anMasterPanelHeightInPixel
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDestinationDirectory == null || aDestinationDirectory.length() == 0) {
            throw new IllegalArgumentException("The destination directory is null/empty or missing.");
        }
        if (anMasterPanelWidthInPixel <= 0) {
            throw new IllegalArgumentException("Master panel width is less/equal zero.");
        }
        if (anMasterPanelHeightInPixel <= 0) {
            throw new IllegalArgumentException("Master panel height is less/equal zero.");
        }
        // </editor-fold>
        this.boxView = aBoxView;
        this.imageFileType = anImageFileType;
        this.destinationDirectory = aDestinationDirectory;
        this.boxSizeInfo = aBoxSizeInfo;
        this.graphicalParticlePositionArrayList = aGraphicalParticlePositionArrayList;
        this.masterPanelWidthInPixel = anMasterPanelWidthInPixel;
        this.masterPanelHeightInPixel = anMasterPanelHeightInPixel;
        this.keyToCreatedSliceImagePathnameMap = new HashMap<String, String>(Preferences.getInstance().getNumberOfSlicesPerView());
        // Set slice image arrays for in-memory slicing
        this.sliceImageArray = new BufferedImage[Preferences.getInstance().getNumberOfSlicesPerView()];
        this.sliceImageByteArray = new byte[Preferences.getInstance().getNumberOfSlicesPerView()][];
        this.slices = null;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Slice related methods">
    /**
     * Create slices
     * 
     * @return True: Slice creation was successful, false: Otherwise (Slice 
     * creation failed)
     */
    public boolean createSlices() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Local variables">
            // Utility methods for files
            FileUtilityMethods tmpUtilityFileMethods = new FileUtilityMethods();
            int tmpXCoordinate;
            int tmpYCoordinate;
            int tmpDiameter;
            IGraphicsFactory tmpGraphicsFactory;
            double tmpChangeDarkerFraction;
            String tmpSliceFilePathname;
            BufferedImage tmpImage;
            TargetCoordinatesAndSize tmpTargetCoordinatesAndSize;
            int tmpTargetImageWidth;
            int tmpTargetImageHeight;
            double tmpPixelTransformationFactor;
            double tmpOldPixelTransformationFactor;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Sort graphical particle positions according to box view">
            this.graphicsUtilityMethods.sortGraphicalParticlePositions(this.boxView, this.graphicalParticlePositionArrayList);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set tmpChangeDarkerFactor">
            tmpChangeDarkerFraction = Preferences.getInstance().getDepthAttenuationSlicer() / Preferences.getInstance().getNumberOfSlicesPerView();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize">
            // Initialize to impossible value
            tmpOldPixelTransformationFactor = -1.0;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize master image related variables">
            switch (this.boxView) {
                case XZ_FRONT:
                case XZ_BACK:
                    tmpTargetCoordinatesAndSize = this.graphicsUtilityMethods.getTargetCoordinatesAndSize(new Dimension(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel),
                            this.boxSizeInfo.getRotationDisplayFrameZLength() / this.boxSizeInfo.getRotationDisplayFrameXLength());
                    // tmpTargetCoordinatesAndSize.getWidth() : Width of target image
                    tmpPixelTransformationFactor = tmpTargetCoordinatesAndSize.getWidth() / this.boxSizeInfo.getRotationDisplayFrameXLength();
                    break;
                case XY_TOP:
                case XY_BOTTOM:
                    tmpTargetCoordinatesAndSize = this.graphicsUtilityMethods.getTargetCoordinatesAndSize(new Dimension(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel),
                            this.boxSizeInfo.getRotationDisplayFrameYLength() / this.boxSizeInfo.getRotationDisplayFrameXLength());
                    // tmpTargetCoordinatesAndSize.getWidth() : Width of target image
                    tmpPixelTransformationFactor = tmpTargetCoordinatesAndSize.getWidth() / this.boxSizeInfo.getRotationDisplayFrameXLength();
                    break;
                case YZ_LEFT:
                case YZ_RIGHT:
                    tmpTargetCoordinatesAndSize = this.graphicsUtilityMethods.getTargetCoordinatesAndSize(new Dimension(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel),
                            this.boxSizeInfo.getRotationDisplayFrameZLength() / this.boxSizeInfo.getRotationDisplayFrameYLength());
                    // tmpTargetCoordinatesAndSize.getWidth() : Width of target image
                    tmpPixelTransformationFactor = tmpTargetCoordinatesAndSize.getWidth() / this.boxSizeInfo.getRotationDisplayFrameYLength();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown box view.");
            }
            tmpTargetImageWidth = tmpTargetCoordinatesAndSize.getWidth();
            tmpTargetImageHeight = tmpTargetCoordinatesAndSize.getHeight();
            tmpGraphicsFactory = null;
            switch (Preferences.getInstance().getSlicerGraphicsMode()) {
                case PIXEL_ALL:
                    tmpGraphicsFactory = new PixelGraphicsFactory(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel, true);
                    break;
                case PIXEL_FINAL:
                    tmpGraphicsFactory = new PixelGraphicsFactory(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel, false);
                    break;
                case BUFFERED_IMAGE_ALL:
                    tmpGraphicsFactory = new BufferedImageGraphicsFactory(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel, true);
                    break;
                case BUFFERED_IMAGE_FINAL:
                    tmpGraphicsFactory = new BufferedImageGraphicsFactory(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel, false);
                    break;
                case VOLATILE_IMAGE_ALL:
                    tmpGraphicsFactory = new VolatileImageGraphicsFactory(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel, true);
                    break;
                case VOLATILE_IMAGE_FINAL:
                    tmpGraphicsFactory = new VolatileImageGraphicsFactory(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel, false);
                    break;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initial calculations">
            if (tmpOldPixelTransformationFactor != tmpPixelTransformationFactor) {
                tmpGraphicsFactory.addParticleGraphics(tmpPixelTransformationFactor, this.graphicalParticlePositionArrayList, this.boxSizeInfo);
                tmpOldPixelTransformationFactor = tmpPixelTransformationFactor;
            }
            this.slices = this.graphicsUtilityMethods.createSlices(this.boxView, this.graphicalParticlePositionArrayList, this.boxSizeInfo);
            // <editor-fold defaultstate="collapsed" desc="- Create temporary directory for slicer image files if not in-memory slicer">
            if (Preferences.getInstance().getImageStorageMode() == ImageStorageEnum.HARDDISK_COMPRESSED
                    && !tmpUtilityFileMethods.createDirectory(new File(this.destinationDirectory, this.boxView.name()))) {
                return false;
            }
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Main loop over slices">
            GraphicalParticlePosition[] tmpGraphicalParticlePositions = this.graphicalParticlePositionArrayList.getGraphicalParticlePositions();
            for (int k = this.slices.length - 1; k >= 0; k--) {
                // <editor-fold defaultstate="collapsed" desc="Make darker or clear">
                // If the single slice display is enabled, the imageManager must 
                // be cleared to clean the previous painted image. Otherwise the 
                // graphicsObjects of the current slice will be painted on the 
                // previous slice.
                if (Preferences.getInstance().isSingleSliceDisplay()) {
                    tmpGraphicsFactory.clear();
                } else {
                    tmpGraphicsFactory.attenuateToBackgroundColor(tmpChangeDarkerFraction);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Draw slices">
                if (this.slices[k].getStartIndex() > -1) {
                    // <editor-fold defaultstate="collapsed" desc="Draw slice">
                    for (int j = this.slices[k].getEndIndex(); j >= this.slices[k].getStartIndex(); j--) {
                        GraphicalParticlePosition tmpCurrentGraphicalParticlePosition = tmpGraphicalParticlePositions[j];
                        GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpCurrentGraphicalParticlePosition.getGraphicalParticle();
                        tmpDiameter = tmpGraphicalParticle.getRadiusInPixel() * 2;
                        // <editor-fold defaultstate="collapsed" desc="Set x and y coordinate">
                        switch (this.boxView) {
                            case XZ_FRONT:
                                tmpXCoordinate = tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelX() - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelZ() - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case XZ_BACK:
                                tmpXCoordinate = tmpTargetImageWidth - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelX() - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelZ() - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case XY_TOP:
                                tmpXCoordinate = tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelX() - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelY() - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case XY_BOTTOM:
                                tmpXCoordinate = tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelX() - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelY() - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case YZ_LEFT:
                                tmpXCoordinate = tmpTargetImageWidth - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelY() - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelZ() - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case YZ_RIGHT:
                                tmpXCoordinate = tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelY() - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelZ() - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            default:
                                return false;
                        }
                        tmpXCoordinate += tmpTargetCoordinatesAndSize.getXcoordinateWithXshift(Preferences.getInstance().getXshiftInPixelSlicer());
                        tmpYCoordinate += tmpTargetCoordinatesAndSize.getYcoordinateWithYshift(Preferences.getInstance().getYshiftInPixelSlicer());
                        // </editor-fold>
                        if (tmpCurrentGraphicalParticlePosition.isInFrame()) {
                            tmpGraphicsFactory.drawSinglePixel(tmpXCoordinate, tmpYCoordinate, Preferences.getInstance().getFrameColorSlicer());
                        } else {
                            if (tmpGraphicalParticle.isParticleTransparent()) {
                                tmpGraphicsFactory.drawTransparent(tmpGraphicalParticle.getGraphicsObject(), tmpXCoordinate, tmpYCoordinate, tmpDiameter);
                            } else {
                                tmpGraphicsFactory.drawOpaque(tmpGraphicalParticle.getGraphicsObject(), tmpXCoordinate, tmpYCoordinate, tmpDiameter);
                            }
                        }
                    }
                    // </editor-fold>
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Keep slice image in memory or write image file to disk">
                String tmpKey = this.graphicsUtilityMethods.getKeyForSliceImage(this.boxView, k);
                if (k > 0) {
                    tmpImage = tmpGraphicsFactory.getIntermediateImage();
                } else {
                    tmpImage = tmpGraphicsFactory.getImage();
                }
                switch (Preferences.getInstance().getImageStorageMode()) {
                    case HARDDISK_COMPRESSED:
                        // NOTE: This must correspond to tmpUtilityFile.createDirectory() method above!
                        tmpSliceFilePathname = this.graphicsUtilityMethods.getSliceFilePathName(this.destinationDirectory, this.boxView, k, this.imageFileType);
                        if (!GraphicsUtils.writeImageToFileWithoutChecks(tmpImage, this.imageFileType, new File(tmpSliceFilePathname))) {
                            return false;
                        }
                        this.keyToCreatedSliceImagePathnameMap.put(tmpKey, tmpSliceFilePathname);
                        break;
                    case MEMORY_COMPRESSED:
                        this.sliceImageByteArray[k] = GraphicsUtils.convertBufferedImageToJpegEncodedByteArray(tmpImage);
                        break;
                    case MEMORY_UNCOMPRESSED:
                        this.sliceImageArray[k] = tmpImage;
                        // Old code:
                        // IMPORTANT: Create copy of image data ...
                        // WritableRaster tmpRaster = tmpImage.copyData(null);
                        // ... then create new image
                        // this.sliceImageArray[k] = new BufferedImage(tmpImage.getColorModel(), tmpRaster, tmpImage.isAlphaPremultiplied(), null);
                        break;
                }

                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Clean up graphics factory">
            tmpGraphicsFactory.cleanUp();
            // </editor-fold>
            // NOTE: Do NOT release memory (i.e. set this.graphicalParticlePositionArrayList = null)
            //       since this.graphicalParticlePositionArrayList may still be used
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Write stack trace of exception to log file">

            // </editor-fold>
            return false;
        }
    }

    /**
     * Returns slice image
     *
     * @param aSliceIndex Index of slice (0 to
 Preferences.getInstance().getNumberOfSlicesPerView() - 1)
     * @return Slice image or null if image is not available
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public BufferedImage getSliceImage(int aSliceIndex) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSliceIndex < 0 || aSliceIndex >= Preferences.getInstance().getNumberOfSlicesPerView()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        String tmpKey = this.graphicsUtilityMethods.getKeyForSliceImage(this.boxView, aSliceIndex);
        switch (Preferences.getInstance().getImageStorageMode()) {
            case HARDDISK_COMPRESSED:
                if (this.keyToCreatedSliceImagePathnameMap.containsKey(tmpKey)) {
                    return GraphicsUtils.readImageFromFile(this.keyToCreatedSliceImagePathnameMap.get(tmpKey));
                } else {
                    return null;
                }
            case MEMORY_COMPRESSED:
                try {
                    return GraphicsUtils.convertJpegEncodedByteArrayToBufferedImage(this.sliceImageByteArray[aSliceIndex]);
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return null;
                }
            case MEMORY_UNCOMPRESSED:
                return this.sliceImageArray[aSliceIndex];
            default:
                return null;
        }
    }

    /**
     * The start value of the slice in the box
     * 
     * @param aSliceIndex Index of slice (0 to
 Preferences.getInstance().getNumberOfSlicesPerView() - 1)
     * @return Start value of the slice in the box or NaN if not available
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public double getSliceStartValue(int aSliceIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSliceIndex < 0 || aSliceIndex >= Preferences.getInstance().getNumberOfSlicesPerView()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        if (this.slices == null) {
            return Double.NaN;
        } else {
            return this.slices[aSliceIndex].getStartValue();
        }
    }

    /**
     * The end value of the slice in the box
     * 
     * @param aSliceIndex Index of slice (0 to
 Preferences.getInstance().getNumberOfSlicesPerView() - 1)
     * @return End value of the slice in the box or NaN if not available
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public double getSliceEndValue(int aSliceIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSliceIndex < 0 || aSliceIndex >= Preferences.getInstance().getNumberOfSlicesPerView()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        if (this.slices == null) {
            return Double.NaN;
        } else {
            return this.slices[aSliceIndex].getEndValue();
        }
    }
    
    /**
     * Returns index of particle which is nearest to point beginning from start
     * slice
     * 
     * @param aStartSliceIndex Index of start slice (0 to
 Preferences.getInstance().getNumberOfSlicesPerView() - 1)
     * @param aPointInSpace Point in slice
     * @return Index of particle which is nearest to point or -1 if no particle 
     * is near (i.e. aPointInSpace is outside particle radius of every particle)
     */
    public int getNearestParticleIndex(int aStartSliceIndex, IPointInSpace aPointInSpace) {
        GraphicalParticlePosition tmpNearestGraphicalParticlePosition = this.getNearestGraphicalParticlePosition(aStartSliceIndex, aPointInSpace);
        if (tmpNearestGraphicalParticlePosition == null) {
            return -1;
        } else {
            return tmpNearestGraphicalParticlePosition.getParticleIndex();
        }
    }
    
    /**
     * Returns graphical particle position which is nearest to point beginning 
     * from start slice
     * 
     * @param aStartSliceIndex Index of start slice (0 to
 Preferences.getInstance().getNumberOfSlicesPerView() - 1)
     * @param aPointInSpace Point in slice
     * @return Graphical particle position which is nearest to point or -1 if no 
     * particle is near (i.e. aPointInSpace is outside particle radius of every 
     * particle)
     */
    public GraphicalParticlePosition getNearestGraphicalParticlePosition(int aStartSliceIndex, IPointInSpace aPointInSpace) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStartSliceIndex < 0 || aStartSliceIndex >= this.slices.length) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aPointInSpace == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (this.slices == null) {
            throw new IllegalArgumentException("this.slices are null.");
        }
        // </editor-fold>
        GraphicalParticlePosition[] tmpGraphicalParticlePositions = this.graphicalParticlePositionArrayList.getGraphicalParticlePositions();
        for (int i = aStartSliceIndex; i < this.slices.length; i++) {
            // Loop from valid start to end index for correct nearest particle
            if (this.slices[i].hasValidStartAndEndIndex()) {
                for (int k = this.slices[i].getStartIndex(); k <= this.slices[i].getEndIndex(); k++) {
                    // IMPORTANT: Do NOT take particles in frame into account.
                    if (!tmpGraphicalParticlePositions[k].isInFrame()) {
                        // <editor-fold defaultstate="collapsed" desc="Local variables">
                        double tmpDeltaX;
                        double tmpDeltaY;
                        double tmpDeltaZ;
                        double tmpDistanceSquareInXZPlane;
                        double tmpDistanceSquareInYZPlane;
                        double tmpDistanceSquareInXYPlane;
                        GraphicalParticlePosition tmpGraphicalParticlePosition;
                        double tmpRadius;
                        double tmpRadiusSquare;
                        // </editor-fold>
                        tmpGraphicalParticlePosition = tmpGraphicalParticlePositions[k];
                        tmpRadius = tmpGraphicalParticlePosition.getGraphicalParticle().getParticleRadius();
                        tmpRadiusSquare = tmpRadius * tmpRadius;
                        switch(this.boxView) {
                            case XZ_FRONT:
                            case XZ_BACK:
                                tmpDeltaX = tmpGraphicalParticlePosition.getX() - aPointInSpace.getX();
                                tmpDeltaZ = tmpGraphicalParticlePosition.getZ() - aPointInSpace.getZ();
                                tmpDistanceSquareInXZPlane = tmpDeltaX * tmpDeltaX + tmpDeltaZ * tmpDeltaZ;
                                if (tmpDistanceSquareInXZPlane < tmpRadiusSquare) {
                                    return tmpGraphicalParticlePosition;
                                }
                                break;
                            case YZ_LEFT:
                            case YZ_RIGHT:
                                tmpDeltaY = tmpGraphicalParticlePosition.getY() - aPointInSpace.getY();
                                tmpDeltaZ = tmpGraphicalParticlePosition.getZ() - aPointInSpace.getZ();
                                tmpDistanceSquareInYZPlane = tmpDeltaY * tmpDeltaY + tmpDeltaZ * tmpDeltaZ;
                                if (tmpDistanceSquareInYZPlane < tmpRadiusSquare) {
                                    return tmpGraphicalParticlePosition;
                                }
                                break;
                            case XY_BOTTOM:
                            case XY_TOP:
                                tmpDeltaX = tmpGraphicalParticlePosition.getX() - aPointInSpace.getX();
                                tmpDeltaY = tmpGraphicalParticlePosition.getY() - aPointInSpace.getY();
                                tmpDistanceSquareInXYPlane = tmpDeltaX * tmpDeltaX + tmpDeltaY * tmpDeltaY;
                                if (tmpDistanceSquareInXYPlane < tmpRadiusSquare) {
                                    return tmpGraphicalParticlePosition;
                                }
                                break;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Returns linked list with one particle index of all molecules which is 
     * nearest to point beginning from start
     * slice
     * 
     * @param aStartSliceIndex Index of start slice (0 to
 Preferences.getInstance().getNumberOfSlicesPerView() - 1)
     * @param aPointInSpace Point in slice
     * @return linked list with one particle index of all molecules nearest to 
     * point or null if no particle is near (i.e. aPointInSpace is outside 
     * particle radius of every particle)
     */
    public LinkedList<Integer> getParticleIndicesOfAllMolecules(int aStartSliceIndex, IPointInSpace aPointInSpace) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStartSliceIndex < 0 || aStartSliceIndex >= this.slices.length) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aPointInSpace == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (this.slices == null) {
            throw new IllegalArgumentException("this.slices are null.");
        }
        // </editor-fold>
        GraphicalParticlePosition tmpNearestGraphicalParticlePosition = this.getNearestGraphicalParticlePosition(aStartSliceIndex, aPointInSpace);
        if (tmpNearestGraphicalParticlePosition == null) {
            return null;
        }
        int tmpNearestParticleIndex = tmpNearestGraphicalParticlePosition.getParticleIndex();
        int tmpNearestMoleculeIndex = tmpNearestGraphicalParticlePosition.getMoleculeIndex();
        GraphicalParticle tmpNearestGraphicalParticle = (GraphicalParticle) tmpNearestGraphicalParticlePosition.getGraphicalParticle();
        String tmpNearestMoleculeName = tmpNearestGraphicalParticle.getMoleculeName();
        LinkedList<Integer> tmpParticleIndexList = new LinkedList<>();
        tmpParticleIndexList.add(tmpNearestParticleIndex);
        // Assumption: 1000 Molecules may be selected
        HashMap<Integer, Integer> tmpMoleculeIndexExclusionMap = new HashMap<>(1000);
        tmpMoleculeIndexExclusionMap.put(tmpNearestMoleculeIndex, tmpNearestMoleculeIndex);
        GraphicalParticlePosition[] tmpGraphicalParticlePositions = this.graphicalParticlePositionArrayList.getGraphicalParticlePositions();
        for (int i = 0; i < this.graphicalParticlePositionArrayList.getSize(); i++) {
            if 
                (
                    !tmpGraphicalParticlePositions[i].isInFrame()
                    && ((GraphicalParticle) tmpGraphicalParticlePositions[i].getGraphicalParticle()).getMoleculeName().equals(tmpNearestMoleculeName)
                    && !tmpMoleculeIndexExclusionMap.containsKey(tmpGraphicalParticlePositions[i].getMoleculeIndex())
                ) 
            {
                int tmpParticleIndex = tmpGraphicalParticlePositions[i].getParticleIndex();
                int tmpMoleculeIndex = tmpGraphicalParticlePositions[i].getMoleculeIndex();
                tmpParticleIndexList.add(tmpParticleIndex);
                tmpMoleculeIndexExclusionMap.put(tmpMoleculeIndex, tmpMoleculeIndex);
            }
        }
        return tmpParticleIndexList;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- releaseMemory">
    /**
     * Release memory
     */
    public void releaseMemory() {
        this.graphicalParticlePositionArrayList = null;
    }
    // </editor-fold>
    // </editor-fold>

}
