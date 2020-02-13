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
package de.gnwi.mfsim.model.graphics.slice;

import de.gnwi.mfsim.gui.util.MoveStepInfo;
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
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.concurrent.Callable;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.mfsim.model.graphics.factory.IGraphicsFactory;

/**
 * Creates a single simulation box image for a specified move step
 *
 * @author Stefan Neumann, Achim Zielesny
 *
 */
public class SimulationBoxViewSingleMoveStepSlice implements Callable<Boolean> {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Graphics utility methods
     */
    private final GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * Property change support
     */
    private final PropertyChangeSupport propertyChangeSupport;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: Task was started, false: Otherwise
     */
    private boolean isStarted;

    /**
     * True: Task was stopped, false: Otherwise
     */
    private boolean isStopped;

    /**
     * True: Task finished, false: Otherwise
     */
    private boolean isFinished;

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
     * Graphical particle position array list
     */
    private GraphicalParticlePositionArrayList graphicalParticlePositionArrayList;

    /**
     * Move step info
     */
    private MoveStepInfo moveStepInfo;

    /**
     * Box size info
     */
    private BoxSizeInfo boxSizeInfo;

    /**
     * The destination directory of the image files
     */
    private String destinationDirectory;

    /**
     * The type of images to create: bmp, jpg, png ...
     */
    private ImageFileType imageFileType;

    /**
     * Globally unique identifier of this instance
     */
    private String globallyUniqueID;
    
    /**
     * Progress value
     */
    private int progressValue;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates a new instance. NOTE:
     *
     * @param aMoveStepInfo Move step info
     * @param aDestinationDirectory The destination directory for the images
     * (may be null)
     * @param anImageFileType File type of the images to be created
     * @param anMasterPanelWidthInPixel The width in pixel of the master panel
     * @param anMasterPanelHeightInPixel The height in pixel of the master panel
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public SimulationBoxViewSingleMoveStepSlice(MoveStepInfo aMoveStepInfo, String aDestinationDirectory, ImageFileType anImageFileType, int anMasterPanelWidthInPixel, int anMasterPanelHeightInPixel)
            throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoveStepInfo == null) {
            throw new IllegalArgumentException("aMoveStepInfo is illegal.");
        }
        if (anMasterPanelWidthInPixel <= 0) {
            throw new IllegalArgumentException("Master panel width is less/equal zero.");
        }
        if (anMasterPanelHeightInPixel <= 0) {
            throw new IllegalArgumentException("Master panel height is less/equal zero.");
        }

        // </editor-fold>
        this.moveStepInfo = aMoveStepInfo;
        this.destinationDirectory = aDestinationDirectory;
        this.imageFileType = anImageFileType;
        this.isStarted = false;
        this.isStopped = false;
        this.isFinished = false;
        this.boxView = this.moveStepInfo.getBoxView();
        this.masterPanelWidthInPixel = anMasterPanelWidthInPixel;
        this.masterPanelHeightInPixel = anMasterPanelHeightInPixel;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.progressValue = -1;
        // Set this.globallyUniqueIdentifier
        this.globallyUniqueID = this.stringUtilityMethods.getGloballyUniqueID();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * True: Task has successfully finished operations, false: Otherwise
     *
     * @return True: Task has successfully finished operations, false:
     * Otherwise
     */
    public boolean isFinished() {
        return this.isStarted && this.isFinished;
    }

    /**
     * True: Task is executing jobs, false: Otherwise
     *
     * @return True: Task is executing jobs, false: Otherwise
     */
    public boolean isWorking() {
        return this.isStarted && !this.isFinished;
    }

    /**
     * True: Task has started operations, false: Otherwise
     *
     * @return True: Task has started operations, false: Otherwise
     */
    public boolean isStarted() {
        return this.isStarted;
    }

    /**
     * Stops execution of task, i.e. creation of images
     */
    public void stop() {
        this.isStopped = true;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public property change support methods">
    /**
     * Add property change listener
     * 
     * @param aListener Listener
     */
    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        this.propertyChangeSupport.addPropertyChangeListener(aListener);
    }

    /**
     * Remove property change listener
     * 
     * @param aListener Listener
     */
    public void removePropertyChangeListener(PropertyChangeListener aListener) {
        this.propertyChangeSupport.removePropertyChangeListener(aListener);
    }    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    /**
     * Move step info
     *
     * @param aMoveStepInfo Move step info
     */
    public void setMoveStepInfo(MoveStepInfo aMoveStepInfo) {
        this.moveStepInfo = aMoveStepInfo;
    }

    /**
     * Move step info
     *
     * @return Move step info
     */
    public MoveStepInfo getMoveStepInfo() {
        return this.moveStepInfo;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Globally unique identifier of this instance
     *
     * @return Globally unique identifier of this instance
     */
    public String getGloballyUniqueID() {
        return this.globallyUniqueID;
    }

    /**
     * Returns box size info
     *
     * @return Box size info
     */
    public BoxSizeInfo getBoxSizeInfo() {
        return this.boxSizeInfo;
    }

    /**
     * The height in pixel of the master panel
     *
     * @return The height in pixel of the master panel
     */
    public int getMasterPanelHeigthInPixel() {
        return this.masterPanelHeightInPixel;
    }

    /**
     * The width in pixel of the master panel
     *
     * @return The width in pixel of the master panel
     */
    public int getMasterPanelWidthInPixel() {
        return this.masterPanelWidthInPixel;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public (overriden) methods">
    /**
     * This method will be called when the task is executed. The methods
     * creates the slices in the destination directory.
     *
     * @return True if the slices have been created successfully, otherwise
     * false.
     * @throws Exception Thrown when an error occurred
     */
    @Override
    public Boolean call() throws Exception {
        try {
            // <editor-fold defaultstate="collapsed" desc="Local variables">
            int tmpXCoordinate;
            int tmpYCoordinate;
            int tmpDiameter;
            IGraphicsFactory tmpGraphicsFactory;
            double tmpChangeDarkerFraction;
            TargetCoordinatesAndSize tmpTargetCoordinatesAndSize;
            int tmpTargetImageWidth;
            int tmpTargetImageHeight;
            double tmpPixelTransformationFactor;
            double tmpOldPixelTransformationFactor;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Task starts. Set progress in percent to 0.">
            this.isStarted = true;
            this.setProgressValue(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set graphical particle positions">
            if (this.moveStepInfo.getGraphicalParticlePositionInfo() == null) {
                // Fire property change to notify property change listeners about cancellation due to internal error
                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                this.isFinished = true;
                return false;
            }
            this.moveStepInfo.getGraphicalParticlePositionInfo().setExclusionBoxSizeInfo(this.moveStepInfo.getExclusionBoxSizeInfo());
            this.moveStepInfo.getGraphicalParticlePositionInfo().setCurrentGraphicalParticlePositions(
                this.moveStepInfo.getRotationAroundXaxisAngle(),
                this.moveStepInfo.getRotationAroundYaxisAngle(),
                this.moveStepInfo.getRotationAroundZaxisAngle(),
                this.moveStepInfo.getParticleShiftX(),
                this.moveStepInfo.getParticleShiftY(),
                this.moveStepInfo.getParticleShiftZ()
            );
            this.graphicalParticlePositionArrayList = this.moveStepInfo.getGraphicalParticlePositionInfo().getCurrentGraphicalParticlePositionArrayList();
            // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
            this.boxSizeInfo = this.moveStepInfo.getGraphicalParticlePositionInfo().getCurrentBoxSizeInfo().getEnlargedBoxSizeInfo(-this.moveStepInfo.getSimulationBoxMagnificationPercentage());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
            if (this.isStopped) {
                this.isFinished = true;
                return false;
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Sort graphical particle positions according to box view">
            this.graphicsUtilityMethods.sortGraphicalParticlePositions(this.boxView, this.graphicalParticlePositionArrayList);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set tmpChangeDarkerFactor">
            tmpChangeDarkerFraction = this.moveStepInfo.getDepthAttenuationSlicer() / Preferences.getInstance().getNumberOfSlicesPerView();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize">
            // Initialize to impossible value
            tmpOldPixelTransformationFactor = -1.0;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
            if (this.isStopped) {
                this.isFinished = true;
                return false;
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize master image related variables">
            switch (this.boxView) {
                case XZ_FRONT:
                case XZ_BACK:
                    tmpTargetCoordinatesAndSize = this.graphicsUtilityMethods.getTargetCoordinatesAndSize(new Dimension(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel), this.boxSizeInfo.getRotationDisplayFrameZLength()
                            / this.boxSizeInfo.getRotationDisplayFrameXLength());
                    // tmpTargetCoordinatesAndSize.getWidth() : Width of target image
                    tmpPixelTransformationFactor = tmpTargetCoordinatesAndSize.getWidth() / this.boxSizeInfo.getRotationDisplayFrameXLength();
                    break;
                case XY_TOP:
                case XY_BOTTOM:
                    tmpTargetCoordinatesAndSize = this.graphicsUtilityMethods.getTargetCoordinatesAndSize(new Dimension(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel), this.boxSizeInfo.getRotationDisplayFrameYLength()
                            / this.boxSizeInfo.getRotationDisplayFrameXLength());
                    // tmpTargetCoordinatesAndSize.getWidth() : Width of target image
                    tmpPixelTransformationFactor = tmpTargetCoordinatesAndSize.getWidth() / this.boxSizeInfo.getRotationDisplayFrameXLength();
                    break;
                case YZ_LEFT:
                case YZ_RIGHT:
                    tmpTargetCoordinatesAndSize = this.graphicsUtilityMethods.getTargetCoordinatesAndSize(new Dimension(this.masterPanelWidthInPixel, this.masterPanelHeightInPixel), this.boxSizeInfo.getRotationDisplayFrameZLength()
                            / this.boxSizeInfo.getRotationDisplayFrameYLength());
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
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Main loop over slices">
            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
            if (this.isStopped) {
                this.isFinished = true;
                return false;
            }
            // </editor-fold>
            GraphicalParticlePosition[] tmpGraphicalParticlePositions = this.graphicalParticlePositionArrayList.getGraphicalParticlePositions();
            for (int k = this.slices.length - 1; k >= this.moveStepInfo.getFirstSliceIndex(); k--) {
                // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                if (this.isStopped) {
                    this.isFinished = true;
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Make darker or clear then draw">
                // If the single slice display is enabled, the imageManager must be cleared to clean the previous painted image. Otherwise the graphicsObjects of the current slice will be painted on
                // the previous slice.
                if (Preferences.getInstance().isSingleSliceDisplay() && k > this.moveStepInfo.getFirstSliceIndex()) {
                    continue;
                } else if (Preferences.getInstance().isSingleSliceDisplay() && k == this.moveStepInfo.getFirstSliceIndex()) {
                    tmpGraphicsFactory.clear();
                } else {
                    tmpGraphicsFactory.attenuateToBackgroundColor(tmpChangeDarkerFraction);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                if (this.isStopped) {
                    this.isFinished = true;
                    return false;
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
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelZ()
                                        - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case XZ_BACK:
                                tmpXCoordinate = tmpTargetImageWidth - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelX()
                                        - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelZ()
                                        - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case XY_TOP:
                                tmpXCoordinate = tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelX() - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelY()
                                        - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case XY_BOTTOM:
                                tmpXCoordinate = tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelX() - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelY() - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case YZ_LEFT:
                                tmpXCoordinate = tmpTargetImageWidth - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelY()
                                        - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelZ()
                                        - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            case YZ_RIGHT:
                                tmpXCoordinate = tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelY() - tmpGraphicalParticle.getRadiusInPixel();
                                tmpYCoordinate = tmpTargetImageHeight - tmpCurrentGraphicalParticlePosition.getMiddlePositionInPixelZ()
                                        - tmpGraphicalParticle.getRadiusInPixel();
                                break;
                            default:
                                // Fire property change to notify property change listeners about cancellation due to internal error
                                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                                this.isFinished = true;
                                return false;
                        }
                        tmpXCoordinate += tmpTargetCoordinatesAndSize.getXcoordinateWithXshift(this.moveStepInfo.getXshiftInPixel());
                        tmpYCoordinate += tmpTargetCoordinatesAndSize.getYcoordinateWithYshift(this.moveStepInfo.getYshiftInPixel());
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
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Keep slice image">
            BufferedImage tmpImage = tmpGraphicsFactory.getImage();
            switch (Preferences.getInstance().getImageStorageMode()) {
                case HARDDISK_COMPRESSED:
                    this.moveStepInfo.setStepImageFilePathname(this.graphicsUtilityMethods.getMoveStepSliceFilePathName(this.destinationDirectory, this.moveStepInfo.getBoxViewIndex(),
                            this.imageFileType));
                    if (!GraphicsUtils.writeImageToFileWithoutChecks(tmpImage, this.imageFileType, new File(this.moveStepInfo.getStepImageFilePathname()))) {
                        // Fire property change to notify property change listeners about cancellation due to internal error
                        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                        this.isFinished = true;
                        return false;
                    }
                    break;
                case MEMORY_COMPRESSED:
                    this.moveStepInfo.setStepImageByteArray(GraphicsUtils.convertBufferedImageToJpegEncodedByteArray(tmpImage));
                    break;
                case MEMORY_UNCOMPRESSED:
                    this.moveStepInfo.setStepImage(tmpImage);
                    // Old code:
                    // IMPORTANT: Create copy of image data ...
                    // WritableRaster raster = tmpImage.copyData(null);
                    // ... then create new image
                    // this.moveStepInfo.setStepImage(new BufferedImage(tmpImage.getColorModel(), raster, tmpImage.isAlphaPremultiplied(), null));
                    break;
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="IMPORTANT: Release memory for GraphicalParticlePositionInfo of this.moveStepInfo">
            this.moveStepInfo.setGraphicalParticlePositionInfo(null);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Clean up graphics factory">
            tmpGraphicsFactory.cleanUp();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set this.isFinished to true BEFORE setting final progress in percent to 100">
            this.isFinished = true;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Task has successfully finished. IMPORTANT: Set progress in percent to 100">
            this.setProgressValue(100);
            // </editor-fold>
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Fire property change">
            // Fire property change to notify property change listeners about cancellation due to internal error
            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
            // </editor-fold>
            this.isFinished = true;
            return false;
        } finally {
            this.releaseMemory();
        }
    }

    /**
     * Finalize method to end all memory claims and running threads. The method
     * will called when the object is destroyed.
     *
     * @throws Throwable This should never happen.
     *
     */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        if (!this.isFinished) {
            this.stop();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Release memory
     */
    private void releaseMemory() {
        this.graphicalParticlePositionArrayList = null;
        this.slices = null;
        this.boxView = null;
    }

    /**
     * Set progress value and fire property change
     * 
     * @param aNewValue New value
     */
    private void setProgressValue(int aNewValue) {
        int tmpOldValue = this.progressValue;
        this.progressValue = aNewValue;
        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_PROGRESS, tmpOldValue, this.progressValue);
    }
    // </editor-fold>

}
