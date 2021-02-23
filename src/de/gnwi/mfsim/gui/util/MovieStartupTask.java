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
package de.gnwi.mfsim.gui.util;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import java.awt.image.BufferedImage;
import de.gnwi.mfsim.model.util.ProgressTaskInterface;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.mfsim.model.graphics.IImageProvider;

/**
 * Task for support of movie creation
 *
 * @author Achim Zielesny
 *
 */
public class MovieStartupTask implements ProgressTaskInterface {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Miscellaneous utility methods
     */
    private final MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();

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
     * CustomPanelSlicerController instance
     */
    private IImageProvider imageProvider;

    /**
     * True: Images are additionally supplied in backward order, false:
     * Otherwise (forward only)
     */
    private boolean isBackwards;
    
    /**
     * Progress value
     */
    private int progressValue;
    
    /**
     * Image directory path for movies
     */
    private String imageDirectoryPathForMovies;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates a new instance
     *
     * @param AnImageProvider IImageProvider instance
     * @param anIsBackwards True: Images are additionally supplied in backward
     * @param anImageDirectoryPathForMovies Image directory path for movies
     * order, false: Otherwise (forward only)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public MovieStartupTask(
        IImageProvider AnImageProvider, 
        boolean anIsBackwards,
        String anImageDirectoryPathForMovies
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (AnImageProvider == null) {
            throw new IllegalArgumentException("Argument is illegal.");
        }
        if (anImageDirectoryPathForMovies == null || anImageDirectoryPathForMovies.isEmpty()) {
            throw new IllegalArgumentException("Argument is illegal.");
        }
        // </editor-fold>
        this.isStarted = false;
        this.isStopped = false;
        this.isFinished = false;
        this.imageProvider = AnImageProvider;
        this.isBackwards = anIsBackwards;
        this.imageDirectoryPathForMovies = anImageDirectoryPathForMovies;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.progressValue = -1;
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
     * Stops execution of task
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
    // <editor-fold defaultstate="collapsed" desc="Public (overriden) methods">
    // <editor-fold defaultstate="collapsed" desc="- call">
    /**
     * This method will be called when the task is executed. The methods
     * calculates the graphical particle positions.
     *
     * @return True if the graphical particle positions have been calculated
     * successfully, otherwise false.
     * @throws Exception Thrown when an error occurred
     */
    @Override
    public Boolean call() throws Exception {
        try {
            // <editor-fold defaultstate="collapsed" desc="Task starts. Set progress in percent to 0.">
            this.isStarted = true;
            this.setProgressValue(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
            if (this.isStopped) {
                return this.returnCancelled();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Copy slice images">
            int tmpIndex = 1;
            int tmpCurrentMaximumImageNumber = this.fileUtilityMethods.getMaximumImageNumberOfImageDirectoryForMovies(this.imageDirectoryPathForMovies);
            if (tmpCurrentMaximumImageNumber < 0) {
                tmpCurrentMaximumImageNumber = 0;
            }
            int tmpMaximumIndex = 0;
            if (this.isBackwards) {
                tmpMaximumIndex = this.imageProvider.getNumberOfImages() * 2;
            } else {
                tmpMaximumIndex = this.imageProvider.getNumberOfImages();
            }
            // Forward
            for (int i = 0; i < this.imageProvider.getNumberOfImages(); i++) {
                BufferedImage tmpImage = this.imageProvider.getImage(i);
                if (tmpImage == null) {
                    // Fire property change to notify property change listeners about cancellation due to internal error
                    this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                    return this.returnCancelled();
                } else {
                    if (!GraphicsUtils.writeImageToInitialZerosNumberFile(
                            tmpImage, 
                            this.imageProvider.getImageFileType(), 
                            this.imageDirectoryPathForMovies, tmpIndex + tmpCurrentMaximumImageNumber)
                    ) {
                        // Fire property change to notify property change listeners about cancellation due to internal error
                        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                        return this.returnCancelled();
                    }
                    tmpIndex++;
                }
                // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                if (this.isStopped) {
                    return returnCancelled();
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set progress">
                this.setProgressValue(miscUtilityMethods.getPercentWithMax99(tmpIndex, tmpMaximumIndex));

                // </editor-fold>
            }
            if (this.isBackwards) {
                // Backward
                for (int i = this.imageProvider.getNumberOfImages() - 2; i > 0; i--) {
                    BufferedImage tmpImage = this.imageProvider.getImage(i);
                    if (tmpImage == null) {
                        // Fire property change to notify property change listeners about cancellation due to internal error
                        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                        return this.returnCancelled();
                    } else {
                        if (!GraphicsUtils.writeImageToInitialZerosNumberFile(
                                tmpImage, 
                                this.imageProvider.getImageFileType(), 
                                this.imageDirectoryPathForMovies, 
                                tmpIndex + tmpCurrentMaximumImageNumber)
                        ) {
                            // Fire property change to notify property change listeners about cancellation due to internal error
                            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                            return this.returnCancelled();
                        }
                        tmpIndex++;
                    }
                    // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                    if (this.isStopped) {
                        return returnCancelled();
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Set progress">
                    this.setProgressValue(miscUtilityMethods.getPercentWithMax99(tmpIndex, tmpMaximumIndex));

                    // </editor-fold>
                }
            }

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
            // Fire property change to notify property change listeners about cancellation due to internal error
            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
            return this.returnCancelled();
        } finally {
            this.releaseMemory();
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Sets internal variables according to internal cancellation and returns
     * false
     *
     * @return False
     */
    private boolean returnCancelled() {
        this.releaseMemory();
        this.isFinished = true;
        return false;
    }
    
    /**
     * Release memory
     */
    private void releaseMemory() {
        this.imageProvider = null;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Property change support related methods">
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
    // </editor-fold>

}
