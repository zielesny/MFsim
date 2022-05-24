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
package de.gnwi.mfsim.gui.chart;

import de.gnwi.mfsim.model.graphics.particle.GraphicalParticle;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.util.VolumeFrequency;
import de.gnwi.mfsim.gui.chart.XyChartDataManipulator;
import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.job.JobResult;
import de.gnwi.mfsim.model.job.JobUtilityMethods;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.spices.SpicesConstants;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.concurrent.Callable;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.mfsim.model.preference.Preferences;

/**
 * Calculates the distribution of a particle, molecule or molecule particle for 
 * a specified simulation step along a specified axis
 * 
 * @author Jan-Mathis Hein, Achim Zielesny (zoom volume exclusions)
 */
public class DistributionCalculationTask implements Callable {
    
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: Task is finished, false: It is not
     */
    private boolean isFinished;
    
    /**
     * True: Task was started, false: It was not
     */
    private boolean isStarted;
    
    /**
     * True: Task is stopped, false: It is not
     */
    private boolean isStopped;
    
    /**
     * An array in the form of {{positionInAngstrom, frequency of object}}
     */
    private double[][] frequencies;
    
    /**
     * Type of the object whose distribution is calculated
     */
    private JobResult.ParticleType particleType;
    
    /**
     * Index for indentifying the distribution calculation task
     */
    private int index;
    
    /**
     * Number of volume-slices. The frequencies are calculated for each 
     * volume-slice.
     */
    private int numberOfVolumeSlices;
    
    /**
     * Progress of the task (0 not finished, 100 finished)
     */
    private int progressValue;
    
    /**
     * Property change support to notify listeners, that the task is finished
     */
    private final PropertyChangeSupport propertyChangeSupport;
    
    /**
     * Name of the object that is being examined
     */
    private String particleTypeDescriptionString;
    
    /**
     * Path name of the file for the graphical particle position info file
     */
    private String graphicalParticlePositionInfoFilePathname;
    
    /**
     * ValueItemContainer which contains an input for a simulation job
     */
    private ValueItemContainer jobInputValueItemContainer;
    
    /**
     * VolumeFrequency object that counts the frequencies
     */
    private VolumeFrequency volumeFrequency;
    
    /**
     * Axis that is examined (X, Y or Z)
     */
    private VolumeFrequency.VolumeAxis volumeAxis;
    
    /**
     * A XyChartDataManipulator which contains the distribution data
     */
    private XyChartDataManipulator xyChartDataManipulator;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param aParticleType Type of the examined object
     * @param anVolumeAxis Axis that is examined
     * @param anIndex An index for identifying this task
     * @param aPathName Path name for a file
     * @param aParticleTypeDescriptionString Name of the examined object
     * @param aNumberOfVolumeSlices Number of volume slices
     * @param aJobInputValueItemContainer ValueItemContainer which contains an 
     * input for a simulation job
     */
    public DistributionCalculationTask(
        JobResult.ParticleType aParticleType, 
        VolumeFrequency.VolumeAxis anVolumeAxis,
        int anIndex, 
        String aPathName,
        String aParticleTypeDescriptionString,
        int aNumberOfVolumeSlices,
        ValueItemContainer aJobInputValueItemContainer
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!(anVolumeAxis == VolumeFrequency.VolumeAxis.X || anVolumeAxis == VolumeFrequency.VolumeAxis.Y || anVolumeAxis == VolumeFrequency.VolumeAxis.Z)) {
            throw new IllegalArgumentException("anAxis is illegal");
        }
        if (!(aParticleType == JobResult.ParticleType.MOLECULE || aParticleType == JobResult.ParticleType.MOLECULE_PARTICLE || aParticleType == JobResult.ParticleType.PARTICLE)) {
            throw new IllegalArgumentException("anExaminedObjectType is illegal");
        }
        if (aJobInputValueItemContainer == null) {
            throw new IllegalArgumentException("aJobInputValueItemContainer is null");
        }
        if (!(new File(aPathName)).isFile()) {
            throw new IllegalArgumentException("Illegal path name");
        }
        if (anIndex < 0) {
            throw new IllegalArgumentException("Illegal index");
        }
        if (aNumberOfVolumeSlices <= 0) {
            throw new IllegalArgumentException("Illegal number of volume slices");
        }
        if (aParticleTypeDescriptionString == null) {
            throw new IllegalArgumentException("anExaminedObjectName is null");
        }
        // </editor-fold>
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.index = anIndex;
        this.volumeAxis = anVolumeAxis;
        this.graphicalParticlePositionInfoFilePathname = aPathName;
        this.jobInputValueItemContainer = aJobInputValueItemContainer;
        this.numberOfVolumeSlices = aNumberOfVolumeSlices;
        this.particleType = aParticleType;
        this.particleTypeDescriptionString = aParticleTypeDescriptionString.replace(SpicesConstants.PARTICLE_SEPARATOR, ModelDefinitions.GENERAL_SEPARATOR);
        this.isFinished = false;
        this.isStarted = false;
        this.isStopped = false;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Property change related methods">
    /**
     * Adds a property change listener that listens for changes from this object
     * 
     * @param aListener A listener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        this.propertyChangeSupport.addPropertyChangeListener(aListener);
    }
    
    /**
     * Removes a property change listener
     * 
     * @param aListener A listener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener aListener) {
        this.propertyChangeSupport.removePropertyChangeListener(aListener);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Public properties">
    /**
     * Frequencies
     * 
     * @return Frequencies
     */
    public double[][] getFrequencies() {
        return this.frequencies;
    }
    
    /**
     * XyChartDataManipulator
     * 
     * @return XyChartDataManipulator
     */
    public XyChartDataManipulator getXyChartDataManipulator() {
        return this.xyChartDataManipulator;
    }
    
    /**
     * Index
     * 
     * @return Index
     */
    public int getIndex() {
        return this.index;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Task related methods">
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
     * True: Task was started, false: Otherwise
     *
     * @return True: Task was started, false: Otherwise
     */
    public boolean isStarted() {
        return this.isStarted;
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
     * This method will be called when the task is getting executed. It will 
     * calculate the frequencies of the examined object along an specified axis.
     * Returns true if task was successfully executed or false if the execution 
     * of the task failed
     * 
     * @return True if task was successfully executed or false if the execution 
     * of the task failed
     */
    @Override
    public Boolean call() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set this.isStarted to true">
            this.isStarted = true;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Task has started. Set progress in percent to 0">
            this.setProgressValue(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize GraphicalParticlePositionInfo">
            JobUtilityMethods tmpUtilityJobMethods = new JobUtilityMethods();
            GraphicalParticlePositionInfo tmpGraphicalParticlePositionInfo = 
                tmpUtilityJobMethods.readGraphicalParticlePositionsWithRepetitions(
                    this.graphicalParticlePositionInfoFilePathname,
                    this.jobInputValueItemContainer,
                    ModelDefinitions.NUMBER_OF_GRAPHICAL_PARTICLE_POSITION_FILE_READ_REPETITIONS,
                    ModelDefinitions.GRAPHICAL_PARTICLE_POSITION_FILE_READ_DELAY
                );
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create VolumeFrequency">
            // A.Z.: Take possible exclusion box size info into account
            BoxSizeInfo tmpExclusionBoxSizeInfo = Preferences.getInstance().getSimulationMovieSlicerConfiguration().getExclusionBoxSizeInfo();
            if (tmpExclusionBoxSizeInfo == null) {
                this.volumeFrequency = 
                    new VolumeFrequency(
                        tmpGraphicalParticlePositionInfo.getInitialBoxSizeInfo(), 
                        this.numberOfVolumeSlices, 
                        tmpGraphicalParticlePositionInfo.getLengthConversionFactor()
                    );
            } else {
                this.volumeFrequency = 
                    new VolumeFrequency(
                        tmpExclusionBoxSizeInfo, 
                        this.numberOfVolumeSlices, 
                        tmpGraphicalParticlePositionInfo.getLengthConversionFactor()
                    );
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Check if canceled">
            if (this.isStopped) {
                this.isFinished = true;
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Count objects">
            switch (this.particleType) {
                case PARTICLE:
                    // <editor-fold defaultstate="collapsed" desc="Particle">
                    for (GraphicalParticlePosition tmpGraphicalParticlePosition : tmpGraphicalParticlePositionInfo.getInitialGraphicalParticlePositions()) {
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            this.isFinished = true;
                            return false;
                        }
                        // </editor-fold>
                        GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpGraphicalParticlePosition.getGraphicalParticle();
                        if (tmpGraphicalParticle.getParticle().equals(this.particleTypeDescriptionString)
                            && (tmpExclusionBoxSizeInfo == null || tmpExclusionBoxSizeInfo.isInBox(tmpGraphicalParticlePosition))
                        ) {
                            this.volumeFrequency.incrementCounters(this.volumeAxis, tmpGraphicalParticlePosition);
                        }
                    }
                    break;
                    // </editor-fold>
                case MOLECULE:
                    // <editor-fold defaultstate="collapsed" desc="Molecule">
                    for (GraphicalParticlePosition tmpGraphicalParticlePosition : tmpGraphicalParticlePositionInfo.getInitialGraphicalParticlePositions()) {
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            this.isFinished = true;
                            return false;
                        }
                        // </editor-fold>
                        GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpGraphicalParticlePosition.getGraphicalParticle();
                        if (tmpGraphicalParticle.getMoleculeName().equals(this.particleTypeDescriptionString)
                            && (tmpExclusionBoxSizeInfo == null || tmpExclusionBoxSizeInfo.isInBox(tmpGraphicalParticlePosition))
                        ) {
                            this.volumeFrequency.incrementCounters(this.volumeAxis, tmpGraphicalParticlePosition);
                        }
                    }
                    break;
                    // </editor-fold>
                case MOLECULE_PARTICLE:
                    // <editor-fold defaultstate="collapsed" desc="Molecule particle">
                    for (GraphicalParticlePosition tmpGraphicalParticlePosition : tmpGraphicalParticlePositionInfo.getInitialGraphicalParticlePositions()) {
                        // <editor-fold defaultstate="collapsed" desc="Check if canceled">
                        if (this.isStopped) {
                            this.isFinished = true;
                            return false;
                        }
                        // </editor-fold>
                        GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpGraphicalParticlePosition.getGraphicalParticle();
                        if (tmpGraphicalParticle.getMoleculeParticleString().equals(this.particleTypeDescriptionString)
                            && (tmpExclusionBoxSizeInfo == null || tmpExclusionBoxSizeInfo.isInBox(tmpGraphicalParticlePosition))
                        ) {
                            this.volumeFrequency.incrementCounters(this.volumeAxis, tmpGraphicalParticlePosition);
                        }
                    }
                    break;
                    // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set frequencies">
            this.setFrequencies();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create a xy-chart data manipulator">
            this.createXyChartDataManipulator();
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
     * Stops execution of task
     */
    public void stop() {
        this.isStopped = true;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Releases memory for this.frequencies, 
     * this.graphicalParticlePositionInfoFilePathname and this.volumeFrequency
     */
    private void releaseMemory() {
        this.frequencies = null;
        this.graphicalParticlePositionInfoFilePathname = null;
        this.volumeFrequency = null;
    }
    
    /**
     * Copies frequency data from this.volumeFrequency to this.frequencies
     */
    private void setFrequencies() {
        try {
            double[] tmpVolumeSlicePositions = this.volumeFrequency.getVolumeSlicePositionsInAngstrom(this.volumeAxis);
            this.frequencies = new double[tmpVolumeSlicePositions.length][2];
            switch (this.particleType) {
                case PARTICLE:
                    int[] tmpParticleFrequencies = this.volumeFrequency.getVolumeSliceParticleFrequencies(this.volumeAxis, this.particleTypeDescriptionString);
                    if (tmpParticleFrequencies == null) {
                        for (int i = 0; i < tmpVolumeSlicePositions.length; i++) {
                            this.frequencies[i] = new double[] {tmpVolumeSlicePositions[i] , 0.0};
                        }
                    } else {
                        for (int i = 0; i < tmpVolumeSlicePositions.length; i++) {
                            this.frequencies[i] = new double[] {tmpVolumeSlicePositions[i] ,(double) tmpParticleFrequencies[i]};
                        }
                    }
                    break;
                case MOLECULE:
                    double[] tmpMoleculeFrequencies = this.volumeFrequency.getVolumeSliceMoleculeFrequencies(this.volumeAxis, this.particleTypeDescriptionString);
                    if (tmpMoleculeFrequencies == null) {
                        for (int i = 0; i < tmpVolumeSlicePositions.length; i++) {
                            this.frequencies[i] = new double[] {tmpVolumeSlicePositions[i] , 0.0};
                        }
                    } else {
                        for (int i = 0; i < tmpVolumeSlicePositions.length; i++) {
                            this.frequencies[i] = new double[] {tmpVolumeSlicePositions[i] , tmpMoleculeFrequencies[i]};
                        }
                    }
                    break;
                case MOLECULE_PARTICLE:
                    int[] tmpMoleculeParticleFrequencies = this.volumeFrequency.getVolumeSliceMoleculeParticleFrequencies(this.volumeAxis, this.particleTypeDescriptionString.split("\\|")[0], this.particleTypeDescriptionString);
                    if (tmpMoleculeParticleFrequencies == null) {
                        for (int i = 0; i < tmpVolumeSlicePositions.length; i++) {
                            this.frequencies[i] = new double[] {tmpVolumeSlicePositions[i] , 0.0};
                        }
                    } else {
                        for (int i = 0; i < tmpVolumeSlicePositions.length; i++) {
                            this.frequencies[i] = new double[] {tmpVolumeSlicePositions[i] ,(double) tmpMoleculeParticleFrequencies[i]};
                        }
                    }
                    break;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    
    /**
     * Creates a XyChartDataManipulator from the data in this.frequencies
     */
    private void createXyChartDataManipulator() {
        try {
            this.xyChartDataManipulator = new XyChartDataManipulator();
            for (double[] tmpFrequency : this.frequencies) {
                // Safeguard for NaN values in data item
                double tmpX = 0.0;
                double tmpY = 0.0;
                try {
                    tmpX = tmpFrequency[0];
                    tmpY = tmpFrequency[1];
                    if (Double.isNaN(tmpX) || Double.isNaN(tmpY)) {
                        continue;
                    }
                }catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    continue;
                }
                this.xyChartDataManipulator.add(tmpX, tmpY);
            }
            this.xyChartDataManipulator.update();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    
    /**
     * Sets progress value and fire a property change
     * 
     * @param aNewValue A new progress value
     */
    private void setProgressValue(int aNewValue) {
        int tmpOldValue = this.progressValue;
        this.progressValue = aNewValue;
        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_PROGRESS, tmpOldValue, this.progressValue);
    }
    // </editor-fold>
    
}
