/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2023  Achim Zielesny (achim.zielesny@googlemail.com)
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
package de.gnwi.mfsim.model.job;

import de.gnwi.jdpd.interfaces.IProgressMonitor;
import de.gnwi.jdpd.utilities.FileOutputStrings;
import de.gnwi.jdpd.utilities.Strings;
import de.gnwi.mfsim.model.util.DirectoryInformation;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Task for job result execution
 *
 * @author Achim Zielesny
 *
 */
public class JobResultExecutionTask implements Callable<Boolean> {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Job utility methods
     */
    private final JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();

    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();
    
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
     * True: Task is submitted to executor service, false: Otherwise
     */
    private boolean isSubmittedToExecutorService;

    /**
     * True: Task was stopped, false: Otherwise
     */
    private boolean isStopped;

    /**
     * True: Task finished, false: Otherwise
     */
    private boolean isFinished;

    /**
     * Job Result of this task
     */
    private JobResult jobResult;

    /**
     * Full path of current process directory in Jdpd workspace
     */
    private String pathOfCurrentProcessDirectory;

    /**
     * DpdSimulationTask callable
     */
    private de.gnwi.jdpd.interfaces.IDpdSimulationTask dpdSimulationTask;
    
    /**
     * Future for DpdSimulationTask
     */
    private Future<Boolean> jdpdSimulatorFuture;
    
    /**
     * Progress monitor
     */
    private de.gnwi.jdpd.interfaces.IProgressMonitor progressMonitor;
    
    /**
     * Executor service
     */
    private ExecutorService executorService;
    
    /**
     * Parallelisation info
     */
    private de.gnwi.jdpd.parameters.ParallelizationInfo parallelizationInfo;
    
    /**
     * Progress value
     */
    private int progressValue;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aJobResult Job result to be executed
     * @throws IllegalArgumentException Thrown if job results are invalid
     */
    public JobResultExecutionTask(JobResult aJobResult) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResult == null) {
            throw new IllegalArgumentException("JobResultExecutionTask.Constructor: aJobResult is null.");
        }
        // </editor-fold>
        this.jobResult = aJobResult;
        this.isStarted = false;
        this.isSubmittedToExecutorService = false;
        this.isStopped = false;
        this.isFinished = false;
        this.dpdSimulationTask = null;
        this.jdpdSimulatorFuture = null;
        this.progressMonitor = null;
        this.pathOfCurrentProcessDirectory = null;
        this.parallelizationInfo = null;
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
     * True: Task is submitted to executor service, false: Otherwise
     *
     * @return True: True: Task is submitted to executor service, false: Otherwise
     */
    public boolean isSubmittedToExecutorService() {
        return this.isSubmittedToExecutorService;
    }

    /**
     * Sets information that task is submitted to executor service
     */
    public void setSubmittedToExecutorService() {
        this.isSubmittedToExecutorService = true;
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
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Job result of this task
     *
     * @return Job result of this task
     */
    public JobResult getJobResult() {
        return this.jobResult;
    }

    /**
     * Full path of current process directory in Jdpd workspace
     *
     * @return Full path of current process directory in Jdpd workspace or
     * null if there is no current process directory
     */
    public String getPathOfCurrentProcessDirectory() {
        return this.pathOfCurrentProcessDirectory;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public (overriden) methods">
    /**
     * This method will be called when the task is executed.
     *
     * @return True if jobs have been executed successfully, otherwise false.
     * @throws Exception Thrown when an error occurred
     */
    @Override
    public Boolean call() throws Exception {
        try {
            // <editor-fold defaultstate="collapsed" desc="Task starts. Set progress in percent to 0">
            this.isStarted = true;
            this.setProgressValue(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set initial alive-information">
            this.setInitialAliveInformation();
            // Fire property change to notify property change listeners about "Job is alive" information
            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_JOB_IS_ALIVE, false, true);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Start job">
            if (!this.startJob()) {
                // Fire property change to notify property change listeners about cancellation due to internal error
                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                this.isFinished = true;
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Wait and check job">
            boolean tmpHasFinished = false;
            while (!tmpHasFinished) {
                // <editor-fold defaultstate="collapsed" desc="Check if task is stopped">
                if (this.isStopped) {
                    if (!this.stopJobInSimulation()) {
                        // Fire property change to notify property change listeners about cancellation due to internal error
                        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set alive information of Jdpd">
                this.setAliveInformation();
                // Fire property change to notify property change listeners about "Job is alive" information
                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_JOB_IS_ALIVE, false, true);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Perform delay">
                try {
                    Thread.sleep(Preferences.getInstance().getTimerIntervalInMilliseconds());
                } catch (InterruptedException anException) {
                    // Do NOT append exception to logfile with
                    // Utility.appendToLogfile(true, anException);
                    // since only Thread.sleep was interrupted.
                    if (!this.stopJobInSimulation()) {
                        // Fire property change to notify property change listeners about cancellation due to internal error
                        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Check job">
                JobResultProcessingStatusEnum tmpCheckStatus = this.checkJobInSimulation();
                switch (tmpCheckStatus) {
                    case JOB_FINISHED_WITH_FAILURE:
                    case JOB_FINISHED_WITH_SUCCESS:
                    case JOB_STOPPED:
                        tmpHasFinished = true;
                        break;
                    case JOB_IN_SIMULATION:
                        tmpHasFinished = false;
                        break;
                    case NO_JOB_IN_SIMULATION:
                        // This should never happen:
                        // Fire property change to notify property change listeners about cancellation due to internal error
                        this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
                        // Remove any alive-information
                        this.jobResult.removeAliveInformation();
                        this.isFinished = true;
                        return false;
                }
                // </editor-fold>
            }
            // Remove any alive-information
            this.jobResult.removeAliveInformation();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set this.isFinished to true BEFORE setting final progress in percent to 100">
            this.isFinished = true;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Task has successfully finished or was stopped. IMPORTANT: Set progress in percent to 100">
            this.setProgressValue(100);
            // </editor-fold>
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Fire property change to notify property change listeners about cancellation due to internal error">
            this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_ERROR, false, true);
            // </editor-fold>
            this.isFinished = true;
            return false;
        } finally {
            this.shutDownExecutorServiceAndReleaseMemory();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- JobResult execution related methods">
    /**
     * Starts job
     *
     * @return true: Operation was successful, false: Operation failed
     */
    private boolean startJob() {
        try {
            // <editor-fold defaultstate="collapsed" desc="1. New unique process directory">
            if (!this.createNewUniqueProcessDirectory()) {
                // This should never happen
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): this.createNewUniqueProcessDirectory() fails.");
                return false;
            }
            if (!this.initializeProcessDirectory()) {
                // This should never happen
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): this.initializeProcessDirectory() fails.");
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="2. Check if job result is to be restarted">
            if (this.jobResult.isToBeRestarted()) {
                // Set job as "restarted"
                this.jobResult.setRestarted(true);
                // Set alive-information after setting Job in restart mode
                this.setInitialAliveInformation();
                // Fire property change to notify property change listeners about "Job is alive" information
                this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_JOB_IS_ALIVE, false, true);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="3. Treat job to be restarted if necessary">
            try {
                if (this.jobResult.isRestarted()) {
                    // Copy "old" result directory to current process directory
                    if (!this.fileUtilityMethods.copyIntoDirectory(this.jobResult.getJobResultPath(), this.pathOfCurrentProcessDirectory)) {
                        ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): this.fileUtilityMethods.copyIntoDirectory() fails.");
                        return false;
                    }
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): '3. Treat job to be restarted if necessary' throws exception.");
                ModelUtils.appendToLogfile(true, anException);
                // Delete current process directory (which was created above)
                this.deleteCurrentProcessDirectory();
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="4. Set result path of current job">
            try {
                // NOTE: Complete JobInput folder of JobResult is automatically copied to process directory by job instance if result path is set
                this.jobResult.setJobResultPath(this.pathOfCurrentProcessDirectory);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): '4. Set result path of current job' throws exception.");
                ModelUtils.appendToLogfile(true, anException);
                // Delete current process directory (which was created above)
                this.deleteCurrentProcessDirectory();
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="5. Remove stop information in this.jobResult">
            try {
                this.jobResult.removeStopped();
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): '5. Remove stop information in this.jobResult' throws exception.");
                ModelUtils.appendToLogfile(true, anException);
                // Delete current process directory (which was created above)
                this.deleteCurrentProcessDirectory();
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="6. Create steps and minimization steps directory of Job Result">
            try {
                if (!ModelUtils.createDirectory(this.jobUtilityMethods.getJobResultStepsPath(this.jobResult.getJobResultPath()))) {
                    ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): Utility.createDirectory(this.jobUtilityMethods.getJobResultStepsPath(this.jobResult.getJobResultPath())) fails.");
                    // Delete current process directory (which was created above)
                    this.deleteCurrentProcessDirectory();
                    return false;
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): '6. Create steps directory of Job Result' throws exception.");
                ModelUtils.appendToLogfile(true, anException);
                // Delete current process directory (which was created above)
                this.deleteCurrentProcessDirectory();
                return false;
            }
            try {
                if (!ModelUtils.createDirectory(this.jobUtilityMethods.getJobResultMinimizationStepsPath(this.jobResult.getJobResultPath()))) {
                    ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): Utility.createDirectory(this.jobUtilityMethods.getJobResultMinimizationStepsPath(this.jobResult.getJobResultPath())) fails.");
                    // Delete current process directory (which was created above)
                    this.deleteCurrentProcessDirectory();
                    return false;
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): '6. Create steps directory of Job Result' throws exception.");
                ModelUtils.appendToLogfile(true, anException);
                // Delete current process directory (which was created above)
                this.deleteCurrentProcessDirectory();
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="7a. Create radius-of-gyration directory of Job Result">
            try {
                if (!ModelUtils.createDirectory(this.jobUtilityMethods.getJobResultRadiusOfGyrationPath(this.jobResult.getJobResultPath()))) {
                    ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): Utility.createDirectory(this.jobUtilityMethods.getJobResultRadiusOfGyrationPath(this.jobResult.getJobResultPath())) fails.");
                    // Delete current process directory (which was created above)
                    this.deleteCurrentProcessDirectory();
                    return false;
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): '7a. Create radius-of-gyration directory of Job Result' throws exception.");
                ModelUtils.appendToLogfile(true, anException);
                // Delete current process directory (which was created above)
                this.deleteCurrentProcessDirectory();
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="7b. Create nearest-neighbor directory of Job Result">
            try {
                if (!ModelUtils.createDirectory(this.jobUtilityMethods.getJobResultNearestNeighborPath(this.jobResult.getJobResultPath()))) {
                    ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): Utility.createDirectory(this.jobUtilityMethods.getJobResultNearestNeighborPath(this.jobResult.getJobResultPath())) fails.");
                    // Delete current process directory (which was created above)
                    this.deleteCurrentProcessDirectory();
                    return false;
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): '7b. Create nearest-neighbor directory of Job Result' throws exception.");
                ModelUtils.appendToLogfile(true, anException);
                // Delete current process directory (which was created above)
                this.deleteCurrentProcessDirectory();
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="8. Delete operations for restart">
            // Delete job result particle positions file after simulation if this file exists at restart
            // NOTE: No try-catch necessary since this is only a "cosmetics" operation
            this.fileUtilityMethods.deleteSingleFile(this.jobUtilityMethods.getJobResultParticlePositionsAfterSimulationFilePathname(this.jobResult.getJobResultPath()));
            // Delete RDF directories
            // NOTE: No try-catch necessary since this is only a "cosmetics" operation
            this.fileUtilityMethods.deleteDirectory(this.jobUtilityMethods.getJobResultParticlePairRdfPath(this.jobResult.getJobResultPath()));
            this.fileUtilityMethods.deleteDirectory(this.jobUtilityMethods.getJobResultMoleculeParticlePairRdfPath(this.jobResult.getJobResultPath()));
            // Delete particle-pair distance files
            // NOTE: No try-catch necessary since this is only a "cosmetics" operation
            this.fileUtilityMethods.deleteMultipleFiles(this.jobUtilityMethods.getJobResultParticlePairDistanceFilePathnames(this.jobResult.getJobResultPath()));
            this.fileUtilityMethods.deleteMultipleFiles(this.jobUtilityMethods.getJobResultMoleculeParticlePairDistanceFilePathnames(this.jobResult.getJobResultPath()));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="9. Write or replace Jdpd input file "Input.txt" and start-geometry files">
            try {
                if (!this.jobResult.isRestarted()) {
                    // <editor-fold defaultstate="collapsed" desc="Job is NOT to be restarted">
                    if (!this.jobResult.writeJdpdInputFile()) {
                        ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): this.jobResult.writeJdpdInputFile() fails.");
                        // Delete current process directory (which was created above)
                        this.deleteCurrentProcessDirectory();
                        return false;
                    }
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Job is to be restarted">
                    // Replaces old Jdpd input file ("Input.txt") with file with new text if specified. Otherwise nothing happens.
                    this.jobResult.replaceJdpdInputFile();
                    // Delete possible new Jdpd input file text
                    this.jobResult.setNewJdpdInputText(null);
                    // </editor-fold>
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): '9. Write or replace input file 'Input.txt' and start-geometry files' throws exception.");
                ModelUtils.appendToLogfile(true, anException);
                // Delete current process directory (which was created above)
                this.deleteCurrentProcessDirectory();
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="10. Append job input to history">
            this.jobResult.appendJobInputToHistory();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="11. Set job start timestamp">
            this.jobResult.setTimestampExecutionStart(ModelUtils.getTimestampInStandardFormat());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="12. Start job">
            try {
                // <editor-fold defaultstate="collapsed" desc="- Init executor service">
                this.executorService = Executors.newSingleThreadExecutor();
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Init Progress monitor">
                // NOTE: The same IProgressMonitor interface is used in Jdpd and JdpdSP
                if (Preferences.getInstance().isJdpdKernelDoublePrecision()) {
                    // Jdpd kernel with double precision
                    this.progressMonitor = new de.gnwi.jdpd.samples.ProgressMonitor();
                } else {
                    // Jdpd kernel with single precision
                    this.progressMonitor = new de.gnwi.jdpdsp.samples.ProgressMonitor();
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Init log levels">
                // NOTE: The same ILogLevel interface is used in Jdpd and JdpdSP
                int[] tmpLogLevels;
                if (Preferences.getInstance().isJdpdLogLevelException()) {
                    tmpLogLevels = new int[] { de.gnwi.jdpd.interfaces.ILogLevel.EXCEPTION };
                } else {
                    tmpLogLevels = de.gnwi.jdpd.interfaces.ILogLevel.ALL_LOGLEVELS;
                    // Debug code:
                    //    tmpLogLevels = new int[] 
                    //        { 
                    //            ILogger.EXCEPTION, 
                    //            ILogger.QUANTITY,
                    //            ILogger.OUTPUT_STEP,
                    //            ILogger.PARTICLE
                    //        };
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Init file logger">
                String tmpJdpdLogFilePathname = this.getJdpdLogFilePathname(this.jobResult.getJobResultPath());
                de.gnwi.jdpd.samples.logger.FileLogger tmpFileLogger = null;
                de.gnwi.jdpdsp.samples.logger.FileLogger tmpFileLoggerSP = null;
                if (Preferences.getInstance().isJdpdKernelDoublePrecision()) {
                    // Jdpd kernel will be used
                    tmpFileLogger = new de.gnwi.jdpd.samples.logger.FileLogger(tmpJdpdLogFilePathname, tmpLogLevels);
                } else {
                    // JdpdSP kernel will be used
                    tmpFileLoggerSP = new de.gnwi.jdpdsp.samples.logger.FileLogger(tmpJdpdLogFilePathname, tmpLogLevels);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Init file input">
                String tmpJdpdInputFilePathname = this.getJdpdInputFilePathname(this.jobResult.getJobResultPath());
                de.gnwi.jdpd.samples.FileInput tmpFileInput = null;
                de.gnwi.jdpdsp.samples.FileInput tmpFileInputSP = null;
                if (Preferences.getInstance().isJdpdKernelDoublePrecision()) {
                    // Jdpd kernel will be used
                    tmpFileInput = new de.gnwi.jdpd.samples.FileInput(tmpJdpdInputFilePathname, tmpFileLogger);
                    // Set safeguard parameter for unphysical start geometries
                    tmpFileInput.setMaximumNumberOfPositionCorrectionTrials(Preferences.getInstance().getMaximumNumberOfPositionCorrectionTrials());
                } else {
                    // JdpdSP kernel will be used
                    tmpFileInputSP = new de.gnwi.jdpdsp.samples.FileInput(tmpJdpdInputFilePathname, tmpFileLoggerSP);
                    // Set safeguard parameter for unphysical start geometries
                    tmpFileInputSP.setMaximumNumberOfPositionCorrectionTrials(Preferences.getInstance().getMaximumNumberOfPositionCorrectionTrials());
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Init file output">
                String tmpOutputDirectoryPath = this.jobResult.getJobResultPath();
                String tmpPropertiesDirectoryPath = this.jobResult.getJobResultPath();
                String tmpRadiusOfGyrationDirectoryPath = this.jobUtilityMethods.getJobResultRadiusOfGyrationPath(this.jobResult.getJobResultPath());
                String tmpNearestNeighborDirectoryPath = this.jobUtilityMethods.getJobResultNearestNeighborPath(this.jobResult.getJobResultPath());
                String tmpSimulationStepParticlePositionsDirectoryPath = this.jobUtilityMethods.getJobResultStepsPath(this.jobResult.getJobResultPath());
                String tmpMinimizationStepParticlePositionsDirectoryPath = this.jobUtilityMethods.getJobResultMinimizationStepsPath(this.jobResult.getJobResultPath());
                int tmpFileOutputParallelTaskNumber = Preferences.getInstance().getNumberOfParallelParticlePositionWriters();
                // NO restriction of number of after-decimal-separator digits for particle positions: Value "-1"
                int tmpNumberOfAfterDecimalDigitsForParticlePositions = -1;
                if (Preferences.getInstance().getNumberOfAfterDecimalDigitsForParticlePositions() < ModelDefinitions.MAXIMUM_NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS) {
                    tmpNumberOfAfterDecimalDigitsForParticlePositions = Preferences.getInstance().getNumberOfAfterDecimalDigitsForParticlePositions();
                }
                de.gnwi.jdpd.samples.FileOutput tmpFileOutput = null;
                de.gnwi.jdpdsp.samples.FileOutput tmpFileOutputSP = null;
                if (Preferences.getInstance().isJdpdKernelDoublePrecision()) {
                    // Jdpd kernel will be used
                    tmpFileOutput = 
                        new de.gnwi.jdpd.samples.FileOutput(
                            tmpOutputDirectoryPath,
                            tmpPropertiesDirectoryPath,
                            tmpRadiusOfGyrationDirectoryPath,
                            tmpNearestNeighborDirectoryPath,
                            tmpSimulationStepParticlePositionsDirectoryPath,
                            tmpMinimizationStepParticlePositionsDirectoryPath,
                            tmpFileOutputParallelTaskNumber,
                            tmpNumberOfAfterDecimalDigitsForParticlePositions
                        );
                    // IMPORTANT: Set tmpFileOutput to this.jobResult
                    this.jobResult.setJdpdFileOutput((de.gnwi.jdpd.interfaces.IOutputWriter) tmpFileOutput);
                } else {
                    // JdpdSP kernel will be used
                    tmpFileOutputSP = 
                        new de.gnwi.jdpdsp.samples.FileOutput(
                            tmpOutputDirectoryPath,
                            tmpPropertiesDirectoryPath,
                            tmpRadiusOfGyrationDirectoryPath,
                            tmpNearestNeighborDirectoryPath,
                            tmpSimulationStepParticlePositionsDirectoryPath,
                            tmpMinimizationStepParticlePositionsDirectoryPath,
                            tmpFileOutputParallelTaskNumber,
                            tmpNumberOfAfterDecimalDigitsForParticlePositions
                        );
                    // IMPORTANT: Set tmpFileOutput to this.jobResult
                    this.jobResult.setJdpdFileOutput((de.gnwi.jdpd.interfaces.IOutputWriter) tmpFileOutputSP);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Init restart info">
                de.gnwi.jdpd.parameters.RestartInfo tmpRestartInfo = null;
                de.gnwi.jdpdsp.parameters.RestartInfo tmpRestartInfoSP = null;
                if (Preferences.getInstance().isJdpdKernelDoublePrecision()) {
                    // Jdpd kernel will be used
                    if (this.jobResult.isRestarted()) {
                        int tmpAdditionalTimeStepNumber = this.jobResult.getAdditionalStepsForRestart();
                        String tmpRestartInfoFilePathname = this.getJdpdRestartInfoFilePathname(this.jobResult.getJobResultPath());
                        tmpRestartInfo = new de.gnwi.jdpd.parameters.RestartInfo(tmpAdditionalTimeStepNumber, tmpRestartInfoFilePathname);
                    }
                } else {
                    // JdpdSP kernel will be used
                    if (this.jobResult.isRestarted()) {
                        int tmpAdditionalTimeStepNumber = this.jobResult.getAdditionalStepsForRestart();
                        String tmpRestartInfoFilePathname = this.getJdpdRestartInfoFilePathname(this.jobResult.getJobResultPath());
                        tmpRestartInfoSP = new de.gnwi.jdpdsp.parameters.RestartInfo(tmpAdditionalTimeStepNumber, tmpRestartInfoFilePathname);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Init parallelization info">
                int tmpMinimumParallelTaskCellNumber = Preferences.getInstance().getNumberOfSimulationBoxCellsforParallelization();
                int tmpMinimumParallelTaskHarmonicBondNumber = Preferences.getInstance().getNumberOfBondsforParallelization();
                int tmpJdpdSimulatorParallelTaskNumber = Preferences.getInstance().getNumberOfParallelCalculators();
                // NOTE: The same ParallelizationInfo class is used in Jdpd and JdpdSP
                this.parallelizationInfo = 
                    new de.gnwi.jdpd.parameters.ParallelizationInfo(
                        tmpMinimumParallelTaskCellNumber, 
                        tmpMinimumParallelTaskHarmonicBondNumber, 
                        tmpJdpdSimulatorParallelTaskNumber
                    );
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Submit DPD simulation task">
                if (Preferences.getInstance().isJdpdKernelDoublePrecision()) {
                    // Jdpd kernel will be used
                    de.gnwi.jdpd.DpdSimulationTask tmpDpdSimulationTask =
                        new de.gnwi.jdpd.DpdSimulationTask(
                            tmpRestartInfo, 
                            tmpFileInput, 
                            tmpFileOutput, 
                            this.progressMonitor, 
                            tmpFileLogger, 
                            this.parallelizationInfo
                        );
                    this.dpdSimulationTask = tmpDpdSimulationTask;
                    this.jdpdSimulatorFuture = this.executorService.submit(tmpDpdSimulationTask);
                } else {
                    // JdpdSP kernel will be used
                    de.gnwi.jdpdsp.DpdSimulationTaskSP tmpDpdSimulationTaskSP =
                        new de.gnwi.jdpdsp.DpdSimulationTaskSP(
                            tmpRestartInfoSP, 
                            tmpFileInputSP, 
                            tmpFileOutputSP, 
                            this.progressMonitor, 
                            tmpFileLoggerSP, 
                            this.parallelizationInfo
                        );
                    this.dpdSimulationTask = tmpDpdSimulationTaskSP;
                    this.jdpdSimulatorFuture = this.executorService.submit(tmpDpdSimulationTaskSP);
                }
                // </editor-fold>
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.startJob(): '13. Start job' throws exception.");
                ModelUtils.appendToLogfile(true, anException);
                // Remove Jdpd file output from this.jobResult
                this.jobResult.removeJdpdFileOutput();
                // Move stopped job directory to result path
                this.moveJobDirectoryToResultPath(JobResultProcessingStatusEnum.JOB_STOPPED);
                return false;
            }
            // </editor-fold>
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Checks job in simulation
     *
     * @return JobResult processing status
     */
    private JobResultProcessingStatusEnum checkJobInSimulation() {
        try {
            if (this.progressMonitor.hasFinished()) {
                try {
                    // <editor-fold defaultstate="collapsed" desc="1. Remove Jdpd file output and set Job Result alive information to finishing">
                    this.jobResult.removeJdpdFileOutput();
                    this.jobResult.setAliveInformationFinishing();
                    // Fire property change to notify property change listeners about "Job is alive" information
                    this.propertyChangeSupport.firePropertyChange(ModelDefinitions.PROPERTY_CHANGE_JOB_IS_ALIVE, false, true);
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="2. Set tmpJobProcessingResult according to Jdpd status">
                    JobResultProcessingStatusEnum tmpJobProcessingResult = JobResultProcessingStatusEnum.JOB_FINISHED_WITH_FAILURE;
                    if (this.progressMonitor.wasInterrupted() && this.progressMonitor.getSimulationState() == IProgressMonitor.SimulationState.FINISHED_WITH_SUCCESS) {
                        // Stopped
                        tmpJobProcessingResult = JobResultProcessingStatusEnum.JOB_STOPPED;
                    } else if (this.progressMonitor.getSimulationState() == IProgressMonitor.SimulationState.FINISHED_WITH_SUCCESS) {
                        // Success
                        tmpJobProcessingResult = JobResultProcessingStatusEnum.JOB_FINISHED_WITH_SUCCESS;
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="3. Create RDF">
                    // Delete possible existing RDF directories
                    if (!this.fileUtilityMethods.deleteDirectory(
                            this.jobUtilityMethods.getJobResultParticlePairRdfPath(
                                this.jobResult.getJobResultPath()
                            )
                        )
                    ) {
                        return JobResultProcessingStatusEnum.JOB_FINISHED_WITH_FAILURE;
                    }
                    if (!this.fileUtilityMethods.deleteDirectory(
                            this.jobUtilityMethods.getJobResultMoleculeParticlePairRdfPath(
                                this.jobResult.getJobResultPath()
                            )
                        )
                    ) {
                        return JobResultProcessingStatusEnum.JOB_FINISHED_WITH_FAILURE;
                    }
                    if (!this.fileUtilityMethods.deleteDirectory(
                            this.jobUtilityMethods.getJobResultMoleculeCenterPairRdfPath(
                                this.jobResult.getJobResultPath()
                            )
                        )
                    ) {
                        return JobResultProcessingStatusEnum.JOB_FINISHED_WITH_FAILURE;
                    }
                    if (this.jobUtilityMethods.isParticlePairRdfCalculation(this.jobResult.getJobInput().getValueItemContainer())) {
                        // Create particle-pair RDF directory
                        ModelUtils.createDirectory(this.jobUtilityMethods.getJobResultParticlePairRdfPath(this.jobResult.getJobResultPath()));
                        this.jobUtilityMethods.createDefinedParticlePairRadialDistributionFunctionFiles(
                            this.jobUtilityMethods.getLatestJobResultParticlePositionsStepFilePathnames(
                                this.jobResult.getJobResultPath(), 
                                Preferences.getInstance().getNumberOfStepsForRdfCalculation()
                            ),
                            this.jobResult.getJobInput().getValueItemContainer(),
                            this.jobResult.getJobResultPath()
                        );
                    }
                    if (this.jobUtilityMethods.isMoleculeParticlePairRdfCalculation(this.jobResult.getJobInput().getValueItemContainer())) {
                        // Create molecule-particle RDF directory
                        ModelUtils.createDirectory(this.jobUtilityMethods.getJobResultMoleculeParticlePairRdfPath(this.jobResult.getJobResultPath()));
                        // NOTE: File JobResultParticlePositionsAfterSimulationFilePathname was created in step 3
                        this.jobUtilityMethods.createDefinedMoleculeParticlePairRadialDistributionFunctionFiles(
                            this.jobUtilityMethods.getLatestJobResultParticlePositionsStepFilePathnames(
                                this.jobResult.getJobResultPath(), 
                                Preferences.getInstance().getNumberOfStepsForRdfCalculation()
                            ),
                            this.jobResult.getJobInput().getValueItemContainer(),
                            this.jobResult.getJobResultPath()
                        );
                    }
                    if (this.jobUtilityMethods.isMoleculeCenterPairRdfCalculation(this.jobResult.getJobInput().getValueItemContainer())) {
                        // Create molecule-center RDF directory
                        ModelUtils.createDirectory(this.jobUtilityMethods.getJobResultMoleculeCenterPairRdfPath(this.jobResult.getJobResultPath()));
                        // NOTE: File JobResultParticlePositionsAfterSimulationFilePathname was created in step 3
                        this.jobUtilityMethods.createDefinedMoleculeCenterPairRadialDistributionFunctionFiles(
                            this.jobUtilityMethods.getLatestJobResultParticlePositionsStepFilePathnames(
                                this.jobResult.getJobResultPath(), 
                                Preferences.getInstance().getNumberOfStepsForRdfCalculation()
                            ),
                            this.jobResult.getJobInput().getValueItemContainer(),
                            this.jobResult.getJobResultPath()
                        );
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="4. Create particle-particle distances">
                    if (this.jobUtilityMethods.isParticlePairDistanceCalculation(this.jobResult.getJobInput().getValueItemContainer())) {
                        // Create particle-pair distance directory
                        ModelUtils.createDirectory(this.jobUtilityMethods.getJobResultParticlePairDistancePath(this.jobResult.getJobResultPath()));
                        this.jobUtilityMethods.createDefinedParticlePairAverageDistanceFiles(this.jobResult.getJobResultPath(), this.jobResult.getJobInput().getValueItemContainer());
                    }
                    if (this.jobUtilityMethods.isMoleculeParticlePairDistanceCalculation(this.jobResult.getJobInput().getValueItemContainer())) {
                        // Create molecule-particle distance directory
                        ModelUtils.createDirectory(this.jobUtilityMethods.getJobResultMoleculeParticlePairDistancePath(this.jobResult.getJobResultPath()));
                        this.jobUtilityMethods.createDefinedMoleculeParticlePairAverageDistanceFiles(this.jobResult.getJobResultPath(), this.jobResult.getJobInput().getValueItemContainer());
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="5. Move Job directory to result path">
                    this.moveJobDirectoryToResultPath(tmpJobProcessingResult);
                    // </editor-fold>
                    return tmpJobProcessingResult;
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return JobResultProcessingStatusEnum.JOB_FINISHED_WITH_FAILURE;
                }
            } else {
                return JobResultProcessingStatusEnum.JOB_IN_SIMULATION;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, "JobResultExecutionTask.checkJobInSimulation: This exception should never happen.");
            ModelUtils.appendToLogfile(true, anException);
            return JobResultProcessingStatusEnum.JOB_FINISHED_WITH_FAILURE;
        }
    }

    /**
     * Stops job in simulation
     *
     * @return true: Operation was successful, false: Operation failed
     */
    private boolean stopJobInSimulation() {
        if (this.checkJobInSimulation() == JobResultProcessingStatusEnum.JOB_IN_SIMULATION) {
            this.jobResult.setStopped();
            this.dpdSimulationTask.stopSimulation();
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobResult alive information related methods">
    /**
     * Set initial alive information in job result
     */
    private void setInitialAliveInformation() {
        this.jobResult.setAliveInformationStarting();
    }

    /**
     * Sets alive information in job result
     */
    private void setAliveInformation() {
        if (this.jobResult == null || this.jdpdSimulatorFuture == null) {
            this.jobResult.setAliveInformation(0, "?");
        } else {
            this.jobResult.setAliveInformation(this.progressMonitor.getProgressInPercent(), this.progressMonitor.getRemainingTime());
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Process directory related methods">
    /**
     * Creates new process directory
     *
     * @return true: Operation was successful, false: Operation failed
     */
    private boolean createNewUniqueProcessDirectory() {
        try {
            DirectoryInformation tmpDirectoryInformation =  
                this.fileUtilityMethods.createUniqueDirectoryWithDateTimeEnding(Preferences.getInstance().getTempPath(),
                    ModelDefinitions.PREFIX_OF_JOB_RESULT_DIRECTORY
                );
            if (tmpDirectoryInformation == null) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.createNewUniqueProcessDirectory: tmpDirectoryInformation could not be created.");
                return false;
            }
            this.pathOfCurrentProcessDirectory = tmpDirectoryInformation.getDirectoryPath();
            return true;
        } catch (Exception anExcpetion) {
            ModelUtils.appendToLogfile(true, anExcpetion);
            return false;
        }
    }

    /**
     * Deletes current process directory in Jdpd workspace
     *
     * @return true: Operation was successful, false: Operation failed
     */
    private boolean deleteCurrentProcessDirectory() {
        if (this.pathOfCurrentProcessDirectory == null) {
            return true;
        } else {
            return this.fileUtilityMethods.deleteDirectory(this.pathOfCurrentProcessDirectory);
        }
    }

    /**
     * Initialise process directory
     *
     * @return true: Operation was successful, false: Operation failed
     */
    private boolean initializeProcessDirectory() {
        // Create input and output directory for Jdpd
        if (!ModelUtils.createDirectory(this.getPathOfCurrentProcessDirectory() + File.separatorChar + ModelDefinitions.JDPD_INPUT_DIRECTORY)) {
            return false;
        }
        if (!ModelUtils.createDirectory(this.getPathOfCurrentProcessDirectory() + File.separatorChar + ModelDefinitions.JDPD_OUTPUT_DIRECTORY)) {
            return false;
        }
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Move job directory method">
    /**
     * Moves job directory to result path
     *
     * @param aJobProcessingResult Job processing result
     * @return true: Operation was successful, false: Operation failed
     */
    private boolean moveJobDirectoryToResultPath(JobResultProcessingStatusEnum aJobProcessingResult) {
        try {
            // <editor-fold defaultstate="collapsed" desc="1. Remove stopped status from this.jobResult">
            this.jobResult.removeStopped();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="2. Move to result path">
            DirectoryInformation tmpNewResultDirectoryInformation = 
                this.fileUtilityMethods.createUniqueDirectoryWithDateTimeEnding(Preferences.getInstance().getJobResultPath(),
                    ModelDefinitions.PREFIX_OF_JOB_RESULT_DIRECTORY
                );
            // IMPORTANT: 
            // COPY temporary process directory (do NOT remove since it may 
            // still be used by Progress dialog)
            // Temporary process directory is ALSO IMPORTANT for CORRECT setting 
            // of job input path in this.jobResult.setJobResultPath() below.
            if (!this.fileUtilityMethods.copyIntoDirectory(this.getPathOfCurrentProcessDirectory(), tmpNewResultDirectoryInformation.getDirectoryPath())) {
                ModelUtils.appendToLogfile(true, "JobResultExecutionTask.moveJobDirectoryToResultPath: this.fileUtilityMethods.copyIntoDirectory() could not be performed. This should never happen.");
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="3. Set new job result path and information">
            this.jobResult.setJobProcessingResult(aJobProcessingResult);
            this.jobResult.setTimestampExecutionEnd(tmpNewResultDirectoryInformation.getTimestamp());
            this.jobResult.setJobResultPath(tmpNewResultDirectoryInformation.getDirectoryPath());
            this.jobResult.setJdpdKernelInfoString(
                this.parallelizationInfo.getParallelTaskNumber(),
                this.parallelizationInfo.getMaximumUsedParallelTaskNumber(),
                this.parallelizationInfo.getMinimumParallelTaskCellNumber(),
                this.parallelizationInfo.getMinimumParallelTaskHarmonicBondNumber(),
                Preferences.getInstance().isJdpdKernelDoublePrecision()
            );
            this.jobResult.writeJobResultInformation();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="4. Delete path of current process directory if possible (MUST be LAST operation)">
            if (!this.jobResult.isResultPathLocked()) {
                // No lock (i.e. use by Progress dialog): Delete temporary process directory
                if (!this.fileUtilityMethods.deleteDirectory(this.getPathOfCurrentProcessDirectory())) {
                    // Temporary process directory could not be deleted so try to rename with prefix "REMOVED_"
                    if (!this.fileUtilityMethods.renameDirectory(this.getPathOfCurrentProcessDirectory(), 
                            ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES + (new File(this.getPathOfCurrentProcessDirectory())).getName()
                        )
                    ) {
                        ModelUtils.appendToLogfile(true, "JobResultExecutionTask.moveJobDirectoryToResultPath: deleteDirectory() AND renameDirectory() could not be performed. This should never happen.");
                        return false;
                    }
                }
            }
            // </editor-fold>
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false; 
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Jdpd file names related methods">
    /**
     * Returns Jdpd input path 
     *
     * @param aPath Path of Job Result/current process directory
     * @return Jdpd input path 
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    private String getJdpdInputPath(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return aPath + File.separatorChar + ModelDefinitions.JDPD_INPUT_DIRECTORY;
    }

    /**
     * Returns Jdpd output path
     *
     * @param aPath Path of Job Result/current process directory
     * @return Jdpd output path
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    private String getJdpdOutputPath(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return aPath + File.separatorChar + ModelDefinitions.JDPD_OUTPUT_DIRECTORY;
    }

    /**
     * Returns pathname of Jdpd input file
     *
     * @param aPath Path of Job Result/current process directory
     * @return Pathname of Jdpd input file
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    private String getJdpdInputFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return this.jobUtilityMethods.getJdpdInputFilePathname(this.getJdpdInputPath(this.jobResult.getJobResultPath()));
    }

    /**
     * Returns pathname of Jdpd logfile
     *
     * @param aPath Path of Job Result/current process directory
     * @return Pathname of Jdpd logfile
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    private String getJdpdLogFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return this.getJdpdOutputPath(aPath) + File.separatorChar + ModelDefinitions.JDPD_LOG_FILENAME;
    }

    /**
     * Returns pathname of Jdpd restart info file
     *
     * @param aPath Path of Job Result/current process directory
     * @return Pathname of Jdpd restart info file
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    private String getJdpdRestartInfoFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        String tmpFileEnding;
        tmpFileEnding = Strings.GZIP_FILE_ENDING;
        return aPath + File.separatorChar + FileOutputStrings.RESTART_INFO_FILENAME_PREFIX + tmpFileEnding;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Shutdown of executor service and memory release
     */
    private void shutDownExecutorServiceAndReleaseMemory() {
        if (this.executorService != null) {
            try {
                this.executorService.shutdown();
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
            }
        }
        this.dpdSimulationTask = null;
        this.jdpdSimulatorFuture = null;
        this.progressMonitor = null;
        this.executorService = null;
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
