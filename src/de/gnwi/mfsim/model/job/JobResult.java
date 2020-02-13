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
package de.gnwi.mfsim.model.job;

import de.gnwi.jdpd.samples.FileOutput;
import de.gnwi.jdpd.utilities.Constants;
import de.gnwi.jdpd.utilities.Strings;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.TimeUtilityMethods;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.graphics.slice.SlicingTypeEnum;
import de.gnwi.mfsim.model.util.VolumeFrequency;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Defines a job
 *
 * @author Achim Zielesny
 */
public class JobResult implements Comparable<JobResult> {

    // <editor-fold defaultstate="collapsed" desc="Public enums">
    /**
     * Particle types
     */
    public enum ParticleType {
        MOLECULE,
        PARTICLE,
        MOLECULE_PARTICLE
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Time utility methods
     */
    private final TimeUtilityMethods timeUtilityMethods = new TimeUtilityMethods();

    /**
     * Job utility methods
     */
    private final JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();

    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Description
     */
    private String description;

    /**
     * Execution period of job, i.e. the difference between start and end
     * timestamp as a defined format string
     */
    private String executionPeriod;

    /**
     * true: JobResult is restarted, false: Otherwise
     */
    private boolean isRestarted;

    /**
     * true: JobResult is to be restarted, false: Otherwise
     */
    private boolean isToBeRestarted;

    /**
     * Full pathname of job input directory of this job result
     */
    private String jobInputPath;

    /**
     * ID for this job result instance
     */
    private String jobResultId;

    /**
     * JobResult processing result
     */
    private JobResultProcessingStatusEnum jobProcessingResult;

    /**
     * Parameter additional steps for restart
     */
    private int additionalStepsForRestart;

    /**
     * Full path of result directory for this job
     */
    private String jobResultPath;

    /**
     * Timestamp for job execution end
     */
    private String timestampExecutionEnd;

    /**
     * Timestamp for job execution start
     */
    private String timestampExecutionStart;

    /**
     * Alive-information during simulation
     */
    private String aliveInformation;

    /**
     * JobInput instance for this JobResult
     */
    private JobInput jobInput;

    /**
     * New text for Jdpd input file
     */
    private String newJdpdInputFileText;

    /**
     * True: JobResult is stopped, false: Otherwise
     */
    private boolean isStopped;

    /**
     * MFsim version of this job result
     */
    private String mfSimVersionOfJobResult;

    /**
     * Jdpd file output
     */
    private FileOutput jdpdfileOutput;

    /**
     * True: Job result path is locked, false: Otherwise
     */
    private boolean isResultPathLocked;
    
    /**
     * Maximum used parallel task number info string
     */
    private String maximumUsedParallelTaskNumberInfoString;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     *
     * @param aDescription Description of job
     * @param aJobInputPath Full pathname of job input directory of this job
     * result
     * @throws IllegalArgumentException Exception is thrown if aDescription,
     * aPathnameOfJdpdInputFile or a ResultPath is null/empty, result path
     * can not be created or Jdpd input file can not be copied to result
     * path
     * @throws FileNotFoundException Exception is thrown if job file is not
     * found
     */
    public JobResult(String aDescription, String aJobInputPath) throws IllegalArgumentException, FileNotFoundException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDescription == null || aDescription.isEmpty()) {
            throw new IllegalArgumentException("aDescription is null/empty.");
        }
        if (aJobInputPath == null || aJobInputPath.isEmpty()) {
            throw new IllegalArgumentException("aJobInputPath is null/empty.");
        }
        if (!(new File(aJobInputPath)).isDirectory()) {
            throw new FileNotFoundException("Job input directory not found: " + aJobInputPath + ".");
        }

        // </editor-fold>
        this.initialize();
        this.description = aDescription;
        this.jobInputPath = aJobInputPath;
        this.mfSimVersionOfJobResult = ModelDefinitions.APPLICATION_VERSION;
    }

    /**
     * Constructor
     *
     * @param aResultPath Full path of result directory with job information
     * file
     * @throws IllegalArgumentException Exception is thrown if aResultPath is
     * null/empty or information of job information file could not be read
     * @throws FileNotFoundException Exception is thrown if job information file
     * is not found
     */
    public JobResult(String aResultPath) throws IllegalArgumentException, FileNotFoundException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aResultPath == null || aResultPath.isEmpty()) {
            throw new IllegalArgumentException("aResultPath is null/empty");
        }
        if (!(new File(aResultPath)).isDirectory()) {
            throw new FileNotFoundException("Directory pathname is not found: " + aResultPath);
        }
        String tmpJobInformationPathname = this.jobUtilityMethods.getJobResultInformationFilePathname(aResultPath);
        if (!(new File(tmpJobInformationPathname)).isFile()) {
            throw new FileNotFoundException("JobResult information file is not found: " + aResultPath);
        }
        // </editor-fold>
        this.initialize();
        this.jobResultPath = aResultPath;
        // <editor-fold defaultstate="collapsed" desc="Read job information">
        if (!this.readJobResultInformation()) {
            ModelUtils.appendToLogfile(true, "JobResult.Constructor: Can not read information from job information file.");
            throw new IllegalArgumentException("JobResult.Constructor: Can not read information from job information file.");
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Jdpd input file related methods">
    /**
     * Writes Jdpd input file with all molecular start-geometry files
     *
     * @return True: Operation successful, false: Otherwise
     */
    public boolean writeJdpdInputFile() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.jobInputPath == null || this.jobInputPath.isEmpty()) {
            ModelUtils.appendToLogfile(true, "JobResult.writeJdpdInputFile(): this.jobInputPath == null || this.jobInputPath.isEmpty()");
            return false;
        }
        if (this.jobResultPath == null || this.jobResultPath.isEmpty() || !(new File(this.jobResultPath)).isDirectory()) {
            ModelUtils.appendToLogfile(true, "JobResult.writeJdpdInputFile(): this.jobResultPath == null || this.jobResultPath.isEmpty() || !(new File(this.jobResultPath)).isDirectory()");
            return false;
        }
        // </editor-fold>
        try {
            // Parameter aJdpdInputFilePathname is specified: Jdpd input file and all start-geometry files of molecules are written!
            String tmpJdpdInputText = 
                this.jobUtilityMethods.getJdpdInputText(new JobInput(this.jobInputPath, false).getValueItemContainer(),
                    this.jobUtilityMethods.getJdpdInputFilePathname(this.jobResultPath + File.separatorChar + ModelDefinitions.JDPD_INPUT_DIRECTORY)
                );
            if (tmpJdpdInputText == null) {
                ModelUtils.appendToLogfile(true, "JobResult.writeJdpdInputFile(): tmpJdpdInputText == null");
                return false;
            } else {
                return true;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Returns text of Jdpd input file ("Input.txt") if available
     *
     * @return Text of Jdpd input file ("Input.txt") or null if not
     * available
     */
    public String getJdpdInputText() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.jobResultPath == null || this.jobResultPath.isEmpty() || !(new File(this.jobResultPath)).isDirectory()) {
            return null;
        }
        // </editor-fold>
        try {
            if ((new File(this.jobUtilityMethods.getJdpdInputFilePathname(this.jobResultPath + File.separatorChar + ModelDefinitions.JDPD_INPUT_DIRECTORY)).isFile())) {
                return (new FileUtilityMethods()).readTextFileIntoSingleString(this.jobUtilityMethods.getJdpdInputFilePathname(this.jobResultPath + File.separatorChar
                        + ModelDefinitions.JDPD_INPUT_DIRECTORY));
            } else {
                return null;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Replaces old Jdpd input file ("Input.txt") with file with new text
     * if available. The old Jdpd input file is renamed with ending
     * ".old(digit)(digit)".
     */
    public void replaceJdpdInputFile() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.jobResultPath == null || this.jobResultPath.isEmpty() || !(new File(this.jobResultPath)).isDirectory()) {
            return;
        }
        // Jdpd input file ("Input.txt") MUST already exist
        if (!(new File(this.jobUtilityMethods.getJdpdInputFilePathname(this.jobResultPath + File.separatorChar + ModelDefinitions.JDPD_INPUT_DIRECTORY)).isFile())) {
            return;
        }
        // Text for new Jdpd input file ("Input.txt") MUST already be specified
        if (!this.hasNewJdpdInputFileText()) {
            return;
        }
        // </editor-fold>
        try {
            // Delete old Jdpd input file
            String tmpJdpdInputFilePathname = this.jobUtilityMethods.getJdpdInputFilePathname(this.jobResultPath + File.separatorChar + ModelDefinitions.JDPD_INPUT_DIRECTORY);
            if (!this.fileUtilityMethods.deleteSingleFile(tmpJdpdInputFilePathname)) {
                return;
            }
            // Write new Jdpd input file
            if (!this.fileUtilityMethods.writeSingleStringToTextFile(this.getNewJdpdInputFileText(), tmpJdpdInputFilePathname)) {
                return;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Job Result value items related methods">
    /**
     * Returns result value item container for this job result
     *
     * @param anIsNearestNeighbors True: Nearest-Neighbour evaluation is 
     * performed if possible, false: Nearest-Neighbour evaluation is omitted
     * @return Value item container for this job result or null if none could be
     * created
     */
    public ValueItemContainer getResultValueItemContainerForJobResult(boolean anIsNearestNeighbors) {
        ValueItem tmpValueItem;
        String[] tmpNodeNames;
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(null);
        int tmpVerticalPosition = 0;
        double tmpSingleStepLengthInNanoseconds = this.jobUtilityMethods.getSingleStepLengthInNanoseconds(this.getJobInput().getValueItemContainer());
        // IMPORTANT NOTE: The names of all value items of a Job Result are NOT 
        // allowed to already exist in any Job Input value item (compare 
        // JdpdValueItemDefinition).
        // <editor-fold defaultstate="collapsed" desc="Add value items to value item container">
        // <editor-fold defaultstate="collapsed" desc="- General information">
        tmpNodeNames = new String[]{ModelMessage.get("JobResults.Root"), ModelMessage.get("JobResults.GeneralInformation")};
        // <editor-fold defaultstate="collapsed" desc="-- Description">
        tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        tmpValueItem.setName("JOB_RESULT_DESCRIPTION");
        tmpValueItem.setDisplayName(ModelMessage.get("JobResults.GeneralInformation.Description"));
        tmpValueItem.setDescription(ModelMessage.get("JobResults.GeneralInformation.Description.Description"));
        tmpValueItem.setValue(this.getDescription());
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Simulation Success">
        tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        tmpValueItem.setName("SIMULATION_SUCCESS");
        tmpValueItem.setDisplayName(ModelMessage.get("JobResults.GeneralInformation.SimulationSuccess"));
        tmpValueItem.setDescription(ModelMessage.get("JobResults.GeneralInformation.SimulationSuccess.Description"));
        tmpValueItem.setValue(this.getJobProcessingResult().toRepresentation());
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Finished at">
        tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        tmpValueItem.setName("FINISHED_AT");
        tmpValueItem.setDisplayName(ModelMessage.get("JobResults.GeneralInformation.FinishedAt"));
        tmpValueItem.setDescription(ModelMessage.get("JobResults.GeneralInformation.FinishedAt.Description"));
        if (this.getTimestampExecutionEnd().isEmpty()) {
            tmpValueItem.setValue(ModelMessage.get("JobResults.NotDefined"));
        } else {
            tmpValueItem.setValue(this.getTimestampExecutionEnd());
        }
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Execution period">
        tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        tmpValueItem.setName("EXECUTION_PERIOD");
        tmpValueItem.setDisplayName(ModelMessage.get("JobResults.GeneralInformation.ExecutionPeriod"));
        tmpValueItem.setDescription(ModelMessage.get("JobResults.GeneralInformation.ExecutionPeriod.Description"));
        if (this.getExecutionPeriod().isEmpty()) {
            tmpValueItem.setValue(ModelMessage.get("JobResults.NotDefined"));
        } else {
            tmpValueItem.setValue(this.getExecutionPeriod());
        }
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Simulation steps">
        tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        tmpValueItem.setName("SIMULATION_STEPS");
        tmpValueItem.setDisplayName(ModelMessage.get("JobResults.GeneralInformation.SimulationSteps"));
        tmpValueItem.setDescription(ModelMessage.get("JobResults.GeneralInformation.SimulationSteps.Description"));
        int tmpMaximumSimulationStep = this.jobUtilityMethods.getMaximumSimulationStep(this.jobResultPath);
        if (tmpMaximumSimulationStep < 0) {
            tmpValueItem.setValue(ModelMessage.get("JobResults.NotDefined"));
        } else {
            tmpValueItem.setValue(String.valueOf(tmpMaximumSimulationStep).trim());
        }
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Simulation time">
        tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        tmpValueItem.setName("SIMULATION_TIME");
        tmpValueItem.setDisplayName(ModelMessage.get("JobResults.GeneralInformation.SimulationTime"));
        tmpValueItem.setDescription(ModelMessage.get("JobResults.GeneralInformation.SimulationTime.Description"));
        // tmpMaximumSimulationStep is already evaluated above
        if (tmpMaximumSimulationStep < 0) {
            tmpValueItem.setValue(ModelMessage.get("JobResults.NotDefined"));
        } else {
            double tmpSimulationInNanoseconds = tmpSingleStepLengthInNanoseconds * (double) tmpMaximumSimulationStep;
            tmpValueItem.setValue(this.stringUtilityMethods.formatDoubleValue(tmpSimulationInNanoseconds, 3));
        }
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Parallel calculators">
        tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        tmpValueItem.setName("PARALLEL_CALCULATORS");
        tmpValueItem.setDisplayName(ModelMessage.get("JobResults.GeneralInformation.ParallelCalculators"));
        tmpValueItem.setDescription(ModelMessage.get("JobResults.GeneralInformation.ParallelCalculators.Description"));
        tmpValueItem.setValue(this.maximumUsedParallelTaskNumberInfoString);
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Job Input particle set filename">
        // NOTE: Single job input directory MUST exist!
        String tmpJobInputDirectoryPathInJobResult = null;
        String[] tmpJobInputDirectoryPathInJobResultArray = this.fileUtilityMethods.getDirectoryPathsWithPrefix(this.getJobResultPath(), ModelDefinitions.PREFIX_OF_JOB_INPUT_DIRECTORY);
        if (tmpJobInputDirectoryPathInJobResultArray != null) {
            tmpJobInputDirectoryPathInJobResult = tmpJobInputDirectoryPathInJobResultArray[0];
        }
        // NOTE: Single particle set file MUST exist!
        String tmpUsedParticleSetFileName = this.fileUtilityMethods.getFilenamesWithPrefix(tmpJobInputDirectoryPathInJobResult, ModelDefinitions.PARTICLE_SET_FILE_PREFIX)[0];
        tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        // NOTE: This name is NOT allowed to match name of corresponding value item in JdpdValueItemDefinition (there "ParticleSetFilename" is used)
        tmpValueItem.setName("JOB_INPUT_PARTICLE_SET_FILENAME");
        tmpValueItem.setDisplayName(ModelMessage.get("JobResults.GeneralInformation.UsedParticleSetFileName"));
        tmpValueItem.setDescription(ModelMessage.get("JobResults.GeneralInformation.UsedParticleSetFileName.Description"));
        tmpValueItem.setValue(tmpUsedParticleSetFileName);
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="-- Job Result application version">
        tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        // NOTE: This name is NOT allowed to match name of corresponding value item in JdpdValueItemDefinition (there "ApplicationVersion" is used)
        tmpValueItem.setName("JOB_RESULT_APPLICATION_VERSION");
        tmpValueItem.setDisplayName(ModelMessage.get("JobResults.GeneralInformation.ApplicationVersion"));
        tmpValueItem.setDescription(ModelMessage.get("JobResults.GeneralInformation.ApplicationVersion.Description"));
        tmpValueItem.setValue(this.mfSimVersionOfJobResult);
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // </editor-fold>
        if (this.jobResultPath != null && !this.jobResultPath.isEmpty() && (new File(this.jobResultPath)).isDirectory()) {
            // <editor-fold defaultstate="collapsed" desc="- Simulation progress">
            tmpNodeNames = new String[]{ModelMessage.get("JobResults.Root"), ModelMessage.get("JobResults.SimulationResult.Progress")};
            ValueItemDataTypeFormat tmpDataTypeFormatText = new ValueItemDataTypeFormat(2, false, false);
            // <editor-fold defaultstate="collapsed" desc="-- Temperature progress">
            if ((new File(this.jobUtilityMethods.getJobResultTemperatureProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultTemperatureProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("TEMPERATURE_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.Temperature"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.Temperature.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                        ModelMessage.get("JobResults.SimulationResult.Progress.Temperature")});
                    tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                        ModelDefinitions.CELL_WIDTH_NUMERIC_100}); // Temperature_in_K
                    // In JobUtilityMethods.getJdpdInputText() the temperature was divided by 298.0 to achieve kT units thus multiply with 298.0 now
                    double tmpFactor = 298.0;
                    tmpValueItem.setMatrix(this.getMultipliedValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText, tmpFactor, 1, Double.POSITIVE_INFINITY));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- U(kin) progress">
            if ((new File(this.jobUtilityMethods.getJobResultUkinProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultUkinProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("UKIN_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.UkinProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.UkinProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                        ModelMessage.get("JobResults.SimulationResult.Progress.Ukin")});
                    tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                        ModelDefinitions.CELL_WIDTH_NUMERIC_100}); // Ukin
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- U(potDpd) progress">
            if ((new File(this.jobUtilityMethods.getJobResultUpotDpdProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultUpotDpdProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("UPOT_DPD_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.UpotDpdProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.UpotDpdProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                        ModelMessage.get("JobResults.SimulationResult.Progress.Upot")});
                    tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                        ModelDefinitions.CELL_WIDTH_NUMERIC_100}); // UpotDpd
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- U(potTotal) progress">
            if ((new File(this.jobUtilityMethods.getJobResultUpotBondProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultUpotBondProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("UPOT_BOND_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.UpotBondProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.UpotBondProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                        ModelMessage.get("JobResults.SimulationResult.Progress.Upot")});
                    tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                        ModelDefinitions.CELL_WIDTH_NUMERIC_100}); // UpotBond
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- U(potTotal) progress">
            if ((new File(this.jobUtilityMethods.getJobResultUpotElectrostaticsProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultUpotElectrostaticsProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("UPOT_ELECTROSTATICS_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.UpotElectrostaticsProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.UpotElectrostaticsProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                        ModelMessage.get("JobResults.SimulationResult.Progress.Upot")});
                    tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                        ModelDefinitions.CELL_WIDTH_NUMERIC_100}); // UpotElectrostatics
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- U(potTotal) progress">
            if ((new File(this.jobUtilityMethods.getJobResultUpotTotalProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultUpotTotalProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("UPOT_TOTAL_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.UpotTotalProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.UpotTotalProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                        ModelMessage.get("JobResults.SimulationResult.Progress.Upot")});
                    tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                        ModelDefinitions.CELL_WIDTH_NUMERIC_100}); // UpotTotal
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- U(total) progress">
            if ((new File(this.jobUtilityMethods.getJobResultUtotalProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultUtotalProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("UTOTAL_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.UtotalProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.UtotalProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                        ModelMessage.get("JobResults.SimulationResult.Progress.Utotal")});
                    tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                        ModelDefinitions.CELL_WIDTH_NUMERIC_100}); // Utotal
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- DPD surface tension along x progress">
            if ((new File(this.jobUtilityMethods.getJobResultDpdSurfaceTensionAlongXProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultDpdSurfaceTensionAlongXProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("DPD_SURFACE_TENSION_X_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionXProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{
                            ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                            ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionX")
                        }
                    );
                    tmpValueItem.setMatrixColumnWidths(new String[]{
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_120 // DpdSurfaceTensionX
                        }
                    );
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- DPD surface tension along y progress">
            if ((new File(this.jobUtilityMethods.getJobResultDpdSurfaceTensionAlongYProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultDpdSurfaceTensionAlongYProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("DPD_SURFACE_TENSION_Y_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionYProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{
                            ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                            ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionY")
                        }
                    );
                    tmpValueItem.setMatrixColumnWidths(new String[]{
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_120 // DpdSurfaceTensionY
                        }
                    );
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- DPD surface tension along z progress">
            if ((new File(this.jobUtilityMethods.getJobResultDpdSurfaceTensionAlongZProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultDpdSurfaceTensionAlongZProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("DPD_SURFACE_TENSION_Z_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionZProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{
                            ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                            ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionZ")
                        }
                    );
                    tmpValueItem.setMatrixColumnWidths(new String[]{
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_120 // DpdSurfaceTensionZ
                        }
                    );
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- DPD surface tension norm progress">
            if ((new File(this.jobUtilityMethods.getJobResultDpdSurfaceTensionNormProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultDpdSurfaceTensionNormProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("DPD_SURFACE_TENSION_NORM_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionNormProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionNormProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{
                            ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                            ModelMessage.get("JobResults.SimulationResult.Progress.DpdSurfaceTensionNorm")
                        }
                    );
                    tmpValueItem.setMatrixColumnWidths(new String[]{
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_TEXT_200 // DpdSurfaceTensionNorm
                        }
                    );
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Surface tension along x progress">
            if ((new File(this.jobUtilityMethods.getJobResultSurfaceTensionAlongXProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultSurfaceTensionAlongXProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("SURFACE_TENSION_X_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionXProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{
                            ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                            ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionX")
                        }
                    );
                    tmpValueItem.setMatrixColumnWidths(new String[]{
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_120 // SurfaceTensionX
                        }
                    );
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Surface tension along y progress">
            if ((new File(this.jobUtilityMethods.getJobResultSurfaceTensionAlongYProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultSurfaceTensionAlongYProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("SURFACE_TENSION_Y_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionYProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{
                            ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                            ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionY")
                        }
                    );
                    tmpValueItem.setMatrixColumnWidths(new String[]{
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_120 // SurfaceTensionY
                        }
                    );
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Surface tension along z progress">
            if ((new File(this.jobUtilityMethods.getJobResultSurfaceTensionAlongZProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultSurfaceTensionAlongZProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("SURFACE_TENSION_Z_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionZProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{
                            ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                            ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionZ")
                        }
                    );
                    tmpValueItem.setMatrixColumnWidths(new String[]{
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_120 // SurfaceTensionZ
                        }
                    );
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Surface tension norm progress">
            if ((new File(this.jobUtilityMethods.getJobResultSurfaceTensionNormProgressFilePathname(this.jobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.jobUtilityMethods.getJobResultSurfaceTensionNormProgressFilePathname(this.jobResultPath));
                if (tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("SURFACE_TENSION_NORM_PROGRESS");
                    tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionNormProgress"));
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionNormProgress.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[]{
                            ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                            ModelMessage.get("JobResults.SimulationResult.Progress.SurfaceTensionNorm")
                        }
                    );
                    tmpValueItem.setMatrixColumnWidths(new String[]{
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_TEXT_200 // SurfaceTensionNorm
                        }
                    );
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    tmpValueItemContainer.addValueItem(tmpValueItem);
                    // </editor-fold>
                }
            }
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Radial distribution functions">
            tmpDataTypeFormatText = new ValueItemDataTypeFormat(3, false, false);
            // <editor-fold defaultstate="collapsed" desc="-- Particle-pair RDF">
            String[] tmpJobResultParticlePairRdfFilePathnames = this.jobUtilityMethods.getJobResultParticlePairRdfFilePathnames(this.jobResultPath);
            if (tmpJobResultParticlePairRdfFilePathnames != null && tmpJobResultParticlePairRdfFilePathnames.length > 0) {
                Arrays.sort(tmpJobResultParticlePairRdfFilePathnames);
                for (String tmpJobResultParticlePairRdfFilePathname : tmpJobResultParticlePairRdfFilePathnames) {
                    String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(tmpJobResultParticlePairRdfFilePathname);
                    if (tmpInfoArray[0].equals("Version 1.0.0")) {

                        // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                        String tmpFirstParticle = tmpInfoArray[1];
                        String tmpSecondParticle = tmpInfoArray[2];
                        String tmpParticlePair = tmpFirstParticle + "_" + tmpSecondParticle;
                        String tmpSegmentLength = tmpInfoArray[3];
                        int tmpOffset = 4;

                        tmpNodeNames = new String[]{ModelMessage.get("JobResults.Root"), ModelMessage.get("JobResults.SimulationResult.ParticlePairRDF"),
                            String.format(ModelMessage.get("JobResults.SimulationResult.ParticlePairRDF.Pair"), tmpFirstParticle, tmpSecondParticle)};

                        tmpValueItem = new ValueItem();
                        tmpValueItem.setName("PARTICLE_PAIR_RDF_" + tmpParticlePair + "_" + tmpSegmentLength);
                        tmpValueItem.setDisplayName(String.format(ModelMessage.get("JobResults.SimulationResult.ParticlePairRDF.PairSegmentLength"), tmpFirstParticle, tmpSecondParticle,
                                tmpSegmentLength));
                        tmpValueItem.setDescription(String.format(ModelMessage.get("JobResults.SimulationResult.ParticlePairRDF.PairSegmentLength.Description"), tmpFirstParticle,
                                tmpSecondParticle, tmpSegmentLength));
                        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                        tmpValueItem.setNodeNames(tmpNodeNames);
                        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.ParticlePairRDF.Distance"),
                            ModelMessage.get("JobResults.SimulationResult.ParticlePairRDF.RDF")});
                        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Distance
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80}); // RDF
                        tmpValueItem.setMatrix(this.getAveragedValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText, tmpOffset, Double.POSITIVE_INFINITY));
                        // IMPORTANT: Set diagram columns
                        tmpValueItem.setMatrixDiagramColumns(0, 1);
                        tmpValueItemContainer.addValueItem(tmpValueItem);

                        // </editor-fold>
                    }
                }
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Molecule-particle-pair RDF">
            String[] tmpJobResultMoleculeParticlePairRdfFilePathnames = this.jobUtilityMethods.getJobResultMoleculeParticlePairRdfFilePathnames(this.jobResultPath);
            if (tmpJobResultMoleculeParticlePairRdfFilePathnames != null && tmpJobResultMoleculeParticlePairRdfFilePathnames.length > 0) {
                Arrays.sort(tmpJobResultMoleculeParticlePairRdfFilePathnames);
                for (String tmpJobResultMoleculeParticlePairRdfFilePathname : tmpJobResultMoleculeParticlePairRdfFilePathnames) {
                    String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(tmpJobResultMoleculeParticlePairRdfFilePathname);
                    if (tmpInfoArray[0].equals("Version 1.0.0")) {

                        // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                        String tmpFirstMoleculeParticle = tmpInfoArray[1];
                        String tmpSecondMoleculeParticle = tmpInfoArray[2];
                        String tmpMoleculeParticlePair = tmpFirstMoleculeParticle + "_" + tmpSecondMoleculeParticle;
                        String tmpSegmentLength = tmpInfoArray[3];
                        int tmpOffset = 4;

                        tmpNodeNames = new String[]{ModelMessage.get("JobResults.Root"), ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairRDF"),
                            String.format(ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairRDF.Pair"), tmpFirstMoleculeParticle, tmpSecondMoleculeParticle)};

                        tmpValueItem = new ValueItem();
                        tmpValueItem.setName("PARTICLE_IN_MOLECULE_PAIR_RDF_" + tmpMoleculeParticlePair + "_" + tmpSegmentLength);
                        tmpValueItem.setDisplayName(String.format(ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairRDF.PairSegmentLength"), tmpFirstMoleculeParticle,
                                tmpSecondMoleculeParticle, tmpSegmentLength));
                        tmpValueItem.setDescription(String.format(ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairRDF.PairSegmentLength.Description"),
                                tmpFirstMoleculeParticle, tmpSecondMoleculeParticle, tmpSegmentLength));
                        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                        tmpValueItem.setNodeNames(tmpNodeNames);
                        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairRDF.Distance"),
                            ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairRDF.RDF")});
                        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Distance
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80}); // RDF
                        tmpValueItem.setMatrix(this.getAveragedValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText, tmpOffset, Double.POSITIVE_INFINITY));
                        // IMPORTANT: Set diagram columns
                        tmpValueItem.setMatrixDiagramColumns(0, 1);
                        tmpValueItemContainer.addValueItem(tmpValueItem);

                        // </editor-fold>
                    }
                }
            }

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Particle-particle distances">
            // <editor-fold defaultstate="collapsed" desc="-- Particle-pair distances">
            String[] tmpJobResultParticlePairDistanceFilePathnames = this.jobUtilityMethods.getJobResultParticlePairDistanceFilePathnames(this.jobResultPath);
            if (tmpJobResultParticlePairDistanceFilePathnames != null && tmpJobResultParticlePairDistanceFilePathnames.length > 0) {
                Arrays.sort(tmpJobResultParticlePairDistanceFilePathnames);
                for (String tmpJobResultParticlePairDistanceFilePathname : tmpJobResultParticlePairDistanceFilePathnames) {
                    String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(tmpJobResultParticlePairDistanceFilePathname);
                    if (tmpInfoArray[0].equals("Version 1.0.0")) {
                        // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                        String tmpFirstParticle = tmpInfoArray[1];
                        String tmpSecondParticle = tmpInfoArray[2];
                        String tmpParticlePair = tmpFirstParticle + "_" + tmpSecondParticle;
                        int tmpOffset = 3;

                        tmpNodeNames = new String[]{ModelMessage.get("JobResults.Root"), ModelMessage.get("JobResults.SimulationResult.ParticlePairDistance")};

                        tmpValueItem = new ValueItem();
                        tmpValueItem.setName("PARTICLE_PAIR_DISTANCE_" + tmpParticlePair);
                        tmpValueItem.setDisplayName(String.format(ModelMessage.get("JobResults.SimulationResult.ParticlePairDistance.Display"), tmpFirstParticle, tmpSecondParticle));
                        tmpValueItem.setDescription(String.format(ModelMessage.get("JobResults.SimulationResult.ParticlePairDistance.Description"), tmpFirstParticle, tmpSecondParticle));
                        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                        tmpValueItem.setNodeNames(tmpNodeNames);
                        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                        tmpValueItem.setMatrixColumnNames(new String[]{
                            ModelMessage.get("JobResults.SimulationResult.ParticlePairDistance.Step"),
                            ModelMessage.get("JobResults.SimulationResult.ParticlePairDistance.Distance")});
                        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_120}); // Distance
                        tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText, tmpOffset, Double.POSITIVE_INFINITY));
                        // IMPORTANT: Set diagram columns
                        tmpValueItem.setMatrixDiagramColumns(0, 1);
                        tmpValueItemContainer.addValueItem(tmpValueItem);

                        // </editor-fold>
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Molecule-particle-pair distances">
            String[] tmpJobResultMoleculeParticlePairDistanceFilePathnames = this.jobUtilityMethods.getJobResultMoleculeParticlePairDistanceFilePathnames(this.jobResultPath);
            if (tmpJobResultMoleculeParticlePairDistanceFilePathnames != null && tmpJobResultMoleculeParticlePairDistanceFilePathnames.length > 0) {
                Arrays.sort(tmpJobResultMoleculeParticlePairDistanceFilePathnames);
                for (String tmpJobResultMoleculeParticlePairDistanceFilePathname : tmpJobResultMoleculeParticlePairDistanceFilePathnames) {
                    String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(tmpJobResultMoleculeParticlePairDistanceFilePathname);
                    if (tmpInfoArray[0].equals("Version 1.0.0")) {

                        // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                        String tmpFirstMoleculeParticle = tmpInfoArray[1];
                        String tmpSecondMoleculeParticle = tmpInfoArray[2];
                        String tmpMoleculeParticlePair = tmpFirstMoleculeParticle + "_" + tmpSecondMoleculeParticle;
                        int tmpOffset = 3;

                        tmpNodeNames = new String[]{ModelMessage.get("JobResults.Root"), ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairDistance")};

                        tmpValueItem = new ValueItem();
                        tmpValueItem.setName("PARTICLE_IN_MOLECULE_PAIR_DISTANCE_" + tmpMoleculeParticlePair);
                        tmpValueItem.setDisplayName(String.format(ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairDistance.Display"), tmpFirstMoleculeParticle,
                                tmpSecondMoleculeParticle));
                        tmpValueItem.setDescription(String.format(ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairDistance.Description"),
                                tmpFirstMoleculeParticle, tmpSecondMoleculeParticle));
                        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                        tmpValueItem.setNodeNames(tmpNodeNames);
                        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairDistance.Step"),
                            ModelMessage.get("JobResults.SimulationResult.MoleculeParticlePairDistance.Distance")});
                        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_120}); // Distance
                        tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText, tmpOffset, Double.POSITIVE_INFINITY));
                        // IMPORTANT: Set diagram columns
                        tmpValueItem.setMatrixDiagramColumns(0, 1);
                        tmpValueItemContainer.addValueItem(tmpValueItem);

                        // </editor-fold>
                    }
                }
            }

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Radius of gyration">
            String[] tmpJobResultRadiusOfGyrationFilePathnames = this.jobUtilityMethods.getJobResultRadiusOfGyrationFilePathnames(this.jobResultPath);
            if (tmpJobResultRadiusOfGyrationFilePathnames != null && tmpJobResultRadiusOfGyrationFilePathnames.length > 0) {
                for (String tmpJobResultRadiusOfGyrationFilePathname : tmpJobResultRadiusOfGyrationFilePathnames) {
                    String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(tmpJobResultRadiusOfGyrationFilePathname);
                    if (tmpInfoArray[0].equals("Version 1.0.0")) {
                        // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                        String tmpMolecule = tmpInfoArray[1];
                        int tmpOffset = 2;

                        tmpNodeNames = new String[]{ModelMessage.get("JobResults.Root"), ModelMessage.get("JobResults.SimulationResult.RadiusOfGyration")};

                        tmpValueItem = new ValueItem();
                        tmpValueItem.setName("RADIUS_OF_GYRATION_" + tmpMolecule);
                        tmpValueItem.setDisplayName(tmpMolecule);
                        tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.RadiusOfGyration.Description"));
                        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                        tmpValueItem.setNodeNames(tmpNodeNames);
                        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("JobResults.SimulationResult.RadiusOfGyration.Step"),
                            ModelMessage.get("JobResults.SimulationResult.RadiusOfGyration.Shortcut")});
                        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80}); // RadiusOfGyration
                        tmpValueItem.setMatrix(this.getValueItemMatrix(tmpInfoArray, tmpDataTypeFormatText, tmpOffset, Double.POSITIVE_INFINITY));
                        // IMPORTANT: Set diagram columns
                        tmpValueItem.setMatrixDiagramColumns(0, 1);
                        tmpValueItemContainer.addValueItem(tmpValueItem);
                        // </editor-fold>
                    }
                }
            }
            // </editor-fold>
            if (anIsNearestNeighbors && this.hasNearestNeighbors()) {
                // <editor-fold defaultstate="collapsed" desc="- Nearest neighbors">
                // <editor-fold defaultstate="collapsed" desc="-- Molecule-particle: Molecule-particle neighbors">
                String tmpBaseMoleculeParticleToNearestNeighborMoleculeParticleStepFrequencyMapFilePathname = 
                    this.jobUtilityMethods.getJobResultBaseMoleculeParticleToNearestNeighborMoleculeParticleStepFrequencyMapFilePathname(this.jobResultPath);
                if ((new File(tmpBaseMoleculeParticleToNearestNeighborMoleculeParticleStepFrequencyMapFilePathname)).isFile()) {
                    tmpNodeNames = 
                        new String[] {
                            ModelMessage.get("JobResults.Root"), 
                            ModelMessage.get("JobResults.SimulationResult.NearestNeighbors"), 
                            ModelMessage.get("JobResults.SimulationResult.MoleculeParticleToNearestNeighborMoleculeParticle")
                        };
                    String tmpValueItemDisplayNameFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeParticleToNearestNeighborMoleculeParticle.DisplayNameFormat");
                    String tmpValueItemDescriptionFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeParticleToNearestNeighborMoleculeParticle.DescriptionFormat");
                    tmpVerticalPosition = 
                        this.setNearestNeighborValueItems(
                            tmpBaseMoleculeParticleToNearestNeighborMoleculeParticleStepFrequencyMapFilePathname,
                            tmpNodeNames,
                            tmpValueItemDisplayNameFormat,
                            tmpValueItemDescriptionFormat,
                            tmpVerticalPosition,
                            tmpValueItemContainer
                        );
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="-- Molecule-particle: Particle neighbors">
                String tmpBaseMoleculeParticleToNearestNeighborParticleStepFrequencyMapFilePathname = 
                    this.jobUtilityMethods.getJobResultBaseMoleculeParticleToNearestNeighborParticleStepFrequencyMapFilePathname(this.jobResultPath);
                if ((new File(tmpBaseMoleculeParticleToNearestNeighborParticleStepFrequencyMapFilePathname)).isFile()) {
                    tmpNodeNames = 
                        new String[] {
                            ModelMessage.get("JobResults.Root"), 
                            ModelMessage.get("JobResults.SimulationResult.NearestNeighbors"), 
                            ModelMessage.get("JobResults.SimulationResult.MoleculeParticleToNearestNeighborParticle")
                        };
                    String tmpValueItemDisplayNameFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeParticleToNearestNeighborParticle.DisplayNameFormat");
                    String tmpValueItemDescriptionFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeParticleToNearestNeighborParticle.DescriptionFormat");
                    tmpVerticalPosition = this.setNearestNeighborValueItems(
                        tmpBaseMoleculeParticleToNearestNeighborParticleStepFrequencyMapFilePathname,
                        tmpNodeNames,
                        tmpValueItemDisplayNameFormat,
                        tmpValueItemDescriptionFormat,
                        tmpVerticalPosition,
                        tmpValueItemContainer
                    );
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="-- Molecule-particle: Molecule neighbors">
                String tmpBaseMoleculeParticleToNearestNeighborMoleculeStepFrequencyMapFilePathname = 
                    this.jobUtilityMethods.getJobResultBaseMoleculeParticleToNearestNeighborMoleculeStepFrequencyMapFilePathname(this.jobResultPath);
                if ((new File(tmpBaseMoleculeParticleToNearestNeighborMoleculeStepFrequencyMapFilePathname)).isFile()) {
                    tmpNodeNames = 
                        new String[] {
                            ModelMessage.get("JobResults.Root"), 
                            ModelMessage.get("JobResults.SimulationResult.NearestNeighbors"), 
                            ModelMessage.get("JobResults.SimulationResult.MoleculeParticleToNearestNeighborMolecule")
                        };
                    String tmpValueItemDisplayNameFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeParticleToNearestNeighborMolecule.DisplayNameFormat");
                    String tmpValueItemDescriptionFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeParticleToNearestNeighborMolecule.DescriptionFormat");
                    tmpVerticalPosition = this.setNearestNeighborValueItems(
                        tmpBaseMoleculeParticleToNearestNeighborMoleculeStepFrequencyMapFilePathname,
                        tmpNodeNames,
                        tmpValueItemDisplayNameFormat,
                        tmpValueItemDescriptionFormat,
                        tmpVerticalPosition,
                        tmpValueItemContainer
                    );
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="-- Molecule: Molecule neighbors">
                String tmpBaseMoleculeToNearestNeighborMoleculeStepFrequencyMapFilePathname = 
                    this.jobUtilityMethods.getJobResultBaseMoleculeToNearestNeighborMoleculeStepFrequencyMapFilePathname(this.jobResultPath);
                if ((new File(tmpBaseMoleculeToNearestNeighborMoleculeStepFrequencyMapFilePathname)).isFile()) {
                    tmpNodeNames = 
                        new String[] {
                            ModelMessage.get("JobResults.Root"), 
                            ModelMessage.get("JobResults.SimulationResult.NearestNeighbors"), 
                            ModelMessage.get("JobResults.SimulationResult.MoleculeToNearestNeighborMolecule")
                        };
                    String tmpValueItemDisplayNameFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeToNearestNeighborMolecule.DisplayNameFormat");
                    String tmpValueItemDescriptionFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeToNearestNeighborMolecule.DescriptionFormat");
                    tmpVerticalPosition = this.setNearestNeighborValueItems(
                        tmpBaseMoleculeToNearestNeighborMoleculeStepFrequencyMapFilePathname,
                        tmpNodeNames,
                        tmpValueItemDisplayNameFormat,
                        tmpValueItemDescriptionFormat,
                        tmpVerticalPosition,
                        tmpValueItemContainer
                    );
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="-- Molecule: Molecule-tuple neighbors">
                String tmpBaseMoleculeToNearestNeighborMoleculeTupleStepFrequencyMapFilePathname = 
                    this.jobUtilityMethods.getJobResultBaseMoleculeToNearestNeighborMoleculeTupleStepFrequencyMapFilePathname(this.jobResultPath);
                if ((new File(tmpBaseMoleculeToNearestNeighborMoleculeTupleStepFrequencyMapFilePathname)).isFile()) {
                    tmpNodeNames = 
                        new String[] {
                            ModelMessage.get("JobResults.Root"), 
                            ModelMessage.get("JobResults.SimulationResult.NearestNeighbors"), 
                            ModelMessage.get("JobResults.SimulationResult.MoleculeToNearestNeighborMoleculeTuple")
                        };
                    String tmpValueItemDisplayNameFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeToNearestNeighborMoleculeTuple.DisplayNameFormat");
                    String tmpValueItemDescriptionFormat = 
                        ModelMessage.get("JobResults.SimulationResult.MoleculeToNearestNeighborMoleculeTuple.DescriptionFormat");
                    tmpVerticalPosition = this.setNearestNeighborValueItems(
                        tmpBaseMoleculeToNearestNeighborMoleculeTupleStepFrequencyMapFilePathname,
                        tmpNodeNames,
                        tmpValueItemDisplayNameFormat,
                        tmpValueItemDescriptionFormat,
                        tmpVerticalPosition,
                        tmpValueItemContainer
                    );
                }
                // </editor-fold>
                // </editor-fold>
            }
            // <editor-fold defaultstate="collapsed" desc="- Initial settings for step files and time step info array">
            String[] tmpJobResultParticlePositionsStepFilePathnames = this.jobUtilityMethods.getJobResultParticlePositionsStepFilePathnames(this.jobResultPath);
            HashMap<String, String> tmpStepToFilePathnameMap = null;
            HashMap<String, String> tmpSortStringToStepMap = null;
            LinkedList<String> tmpStepSortStringList = new LinkedList<String>();
            if (tmpJobResultParticlePositionsStepFilePathnames != null && tmpJobResultParticlePositionsStepFilePathnames.length > 0) {
                int tmpMaximumStepNumber = -1;
                tmpStepToFilePathnameMap = new HashMap<String, String>(tmpJobResultParticlePositionsStepFilePathnames.length);
                for (String tmpJobResultParticlePositionsStepFilePathname : tmpJobResultParticlePositionsStepFilePathnames) {
                    String tmpStep = this.jobUtilityMethods.getStepOfJobResultParticlePositionsStepFilePathname(tmpJobResultParticlePositionsStepFilePathname);
                    int tmpStepNumber = Integer.valueOf(tmpStep);
                    if (tmpStepNumber > tmpMaximumStepNumber) {
                        tmpMaximumStepNumber = tmpStepNumber;
                    }
                    tmpStepToFilePathnameMap.put(tmpStep, tmpJobResultParticlePositionsStepFilePathname);
                }
                String[] tmpSteps = tmpStepToFilePathnameMap.keySet().toArray(new String[0]);
                tmpSortStringToStepMap = new HashMap<String, String>(tmpSteps.length);
                for (String tmpStep : tmpSteps) {
                    int tmpStepNumber = Integer.valueOf(tmpStep);
                    String tmpSortString = this.stringUtilityMethods.createSortablePositiveIntegerRepresentation(tmpStepNumber, tmpMaximumStepNumber);
                    tmpSortStringToStepMap.put(tmpSortString, tmpStep);
                    tmpStepSortStringList.add(tmpSortString);
                }
                Collections.sort(tmpStepSortStringList);
            }
            TimeStepInfo[] tmpTimeStepInfoArray = null;
            if (tmpStepSortStringList.size() > 0) {
                tmpTimeStepInfoArray = new TimeStepInfo[tmpStepSortStringList.size()];
                int tmpIndex = 0;
                for (String tmpSortString : tmpStepSortStringList) {
                    String tmpStep = tmpSortStringToStepMap.get(tmpSortString);
                    double tmpSimulationInNanoseconds = tmpSingleStepLengthInNanoseconds * Double.valueOf(tmpStep);
                    String tmpSimulationInNanosecondsString = this.stringUtilityMethods.formatDoubleValue(tmpSimulationInNanoseconds, 1);
                    String tmpStepInfo = String.format(ModelMessage.get("JobResults.SimulationResult.SimulationBox.Step"), tmpStep, tmpSimulationInNanosecondsString);
                    String tmpJobResultParticlePositionsStepFilePathname = tmpStepToFilePathnameMap.get(tmpStep);
                    tmpTimeStepInfoArray[tmpIndex] = new TimeStepInfo(tmpStepInfo, tmpJobResultParticlePositionsStepFilePathname, tmpIndex);
                    tmpIndex++;
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Distribution Movie">
            if (Preferences.getInstance().isParticleDistributionInclusion() && tmpTimeStepInfoArray != null) {
                LinkedList<String>[] tmpListArray = this.jobUtilityMethods.getAllMoleculeAndParticleLists(this.getJobInput().getValueItemContainer());
                if (tmpListArray != null) {
                    LinkedList<String> tmpMoleculeList = tmpListArray[0];
                    LinkedList<String> tmpParticleList = tmpListArray[1];
                    LinkedList<String> tmpMoleculeParticleList = tmpListArray[2];
                    VolumeFrequency.VolumeAxis[] tmpVolumeAxisArray = VolumeFrequency.VolumeAxis.getVolumeAxisArray();
                    for (VolumeFrequency.VolumeAxis tmpVolumeAxis : tmpVolumeAxisArray) {
                        String tmpVolumeAxisString = null;
                        switch(tmpVolumeAxis) {
                            case X:
                                tmpVolumeAxisString = ModelMessage.get("JobResults.xAxis");
                                break;
                            case Y:
                                tmpVolumeAxisString = ModelMessage.get("JobResults.yAxis");
                                break;
                            case Z:
                                tmpVolumeAxisString = ModelMessage.get("JobResults.zAxis");
                                break;
                        }
                        tmpNodeNames = new String[]{
                            ModelMessage.get("JobResults.Root"),
                            ModelMessage.get("JobResults.SimulationResult.DistributionMovie"),
                            tmpVolumeAxisString,
                            ModelMessage.get("JobResults.SimulationResult.DistributionMovie.Molecules")
                        };
                        for (String tmpMolecule : tmpMoleculeList) {
                            tmpVerticalPosition = 
                                this.setDistributionValueItem(tmpMolecule,
                                    tmpNodeNames,
                                    tmpVolumeAxis,
                                    ParticleType.MOLECULE,
                                    tmpTimeStepInfoArray,
                                    tmpVerticalPosition,
                                    tmpValueItemContainer
                                );
                        }
                        tmpNodeNames = new String[]{
                            ModelMessage.get("JobResults.Root"),
                            ModelMessage.get("JobResults.SimulationResult.DistributionMovie"),
                            tmpVolumeAxisString,
                            ModelMessage.get("JobResults.SimulationResult.DistributionMovie.Particles")
                        };
                        for (String tmpParticle : tmpParticleList) {
                            tmpVerticalPosition = 
                                this.setDistributionValueItem(tmpParticle,
                                    tmpNodeNames,
                                    tmpVolumeAxis,
                                    ParticleType.PARTICLE,
                                    tmpTimeStepInfoArray,
                                    tmpVerticalPosition,
                                    tmpValueItemContainer
                                );
                        }
                        tmpNodeNames = new String[]{
                            ModelMessage.get("JobResults.Root"),
                            ModelMessage.get("JobResults.SimulationResult.DistributionMovie"),
                            tmpVolumeAxisString,
                            ModelMessage.get("JobResults.SimulationResult.DistributionMovie.MoleculeParticles")
                        };
                        for (String tmpMoleculeParticle : tmpMoleculeParticleList) {
                            tmpVerticalPosition = 
                                this.setDistributionValueItem(tmpMoleculeParticle,
                                    tmpNodeNames,
                                    tmpVolumeAxis,
                                    ParticleType.MOLECULE_PARTICLE,
                                    tmpTimeStepInfoArray,
                                    tmpVerticalPosition,
                                    tmpValueItemContainer
                                );
                        }
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Simulation box">
            tmpNodeNames = 
                new String[]
                    {
                        ModelMessage.get("JobResults.Root"), 
                        ModelMessage.get("JobResults.SimulationResult.SimulationBox")
                    };
            // <editor-fold defaultstate="collapsed" desc="-- Initial settings for minimization step files">
            String[] tmpJobResultParticlePositionsMinStepFilePathnames = this.jobUtilityMethods.getJobResultParticlePositionsMinStepFilePathnames(this.jobResultPath);
            HashMap<String, String> tmpMinStepToFilePathnameMap = null;
            HashMap<String, String> tmpSortStringToMinStepMap = null;
            LinkedList<String> tmpMinStepSortStringList = new LinkedList<>();
            if (tmpJobResultParticlePositionsMinStepFilePathnames != null && tmpJobResultParticlePositionsMinStepFilePathnames.length > 0) {
                int tmpMaximumStepNumber = -1;
                tmpMinStepToFilePathnameMap = new HashMap<>(tmpJobResultParticlePositionsMinStepFilePathnames.length);
                for (String tmpJobResultParticlePositionsStepFilePathname : tmpJobResultParticlePositionsMinStepFilePathnames) {
                    String tmpStep = this.jobUtilityMethods.getStepOfJobResultParticlePositionsMinStepFilePathname(tmpJobResultParticlePositionsStepFilePathname);
                    int tmpStepNumber = Integer.valueOf(tmpStep);
                    if (tmpStepNumber > tmpMaximumStepNumber) {
                        tmpMaximumStepNumber = tmpStepNumber;
                    }
                    tmpMinStepToFilePathnameMap.put(tmpStep, tmpJobResultParticlePositionsStepFilePathname);
                }
                String[] tmpSteps = tmpMinStepToFilePathnameMap.keySet().toArray(new String[0]);
                tmpSortStringToMinStepMap = new HashMap<>(tmpSteps.length);
                for (String tmpStep : tmpSteps) {
                    int tmpStepNumber = Integer.valueOf(tmpStep);
                    String tmpSortString = this.stringUtilityMethods.createSortablePositiveIntegerRepresentation(tmpStepNumber, tmpMaximumStepNumber);
                    tmpSortStringToMinStepMap.put(tmpSortString, tmpStep);
                    tmpMinStepSortStringList.add(tmpSortString);
                }
                Collections.sort(tmpMinStepSortStringList);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Simulation box before minimization">
            if ((new File(this.jobUtilityMethods.getJobResultParticlePositionsBeforeMinimizationFilePathname(this.jobResultPath))).isFile()) {
                tmpValueItem = new ValueItem();
                tmpValueItem.setName("SIMULATION_BOX_BEFORE_MINIMIZATION");
                tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.SimulationBox.BeforeMinimization"));
                tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.SimulationBox.BeforeMinimization.Description"));
                tmpValueItem.setBasicType(ValueItemEnumBasicType.OBJECT);

                Object[] tmpObjectArray = new Object[3];
                tmpObjectArray[0] = SlicingTypeEnum.SIMULATION_BOX;
                tmpObjectArray[1] = this.jobUtilityMethods.getJobResultParticlePositionsBeforeMinimizationFilePathname(this.jobResultPath);
                tmpObjectArray[2] = this.getJobInput().getValueItemContainer();
                tmpValueItem.setObject(tmpObjectArray);

                tmpValueItem.setNodeNames(tmpNodeNames);
                tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                tmpValueItemContainer.addValueItem(tmpValueItem);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Simulation box after minimization">
            if ((new File(this.jobUtilityMethods.getJobResultParticlePositionsAfterMinimizationFilePathname(this.jobResultPath))).isFile()) {
                tmpValueItem = new ValueItem();
                tmpValueItem.setName("SIMULATION_BOX_AFTER_MINIMIZATION");
                tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.SimulationBox.AfterMinimization"));
                tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.SimulationBox.AfterMinimization.Description"));
                tmpValueItem.setBasicType(ValueItemEnumBasicType.OBJECT);

                Object[] tmpObjectArray = new Object[3];
                tmpObjectArray[0] = SlicingTypeEnum.SIMULATION_BOX;
                tmpObjectArray[1] = this.jobUtilityMethods.getJobResultParticlePositionsAfterMinimizationFilePathname(this.jobResultPath);
                tmpObjectArray[2] = this.getJobInput().getValueItemContainer();
                tmpValueItem.setObject(tmpObjectArray);

                tmpValueItem.setNodeNames(tmpNodeNames);
                tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                tmpValueItemContainer.addValueItem(tmpValueItem);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Simulation box minimization movie">
            if (tmpMinStepSortStringList.size() > 0) {
                TimeStepInfo[] tmpMinTimeStepInfoArray = new TimeStepInfo[tmpMinStepSortStringList.size()];
                int tmpIndex = 0;
                for (String tmpSortString : tmpMinStepSortStringList) {
                    String tmpMinStep = tmpSortStringToMinStepMap.get(tmpSortString);
                    String tmpMinStepInfo = String.format(ModelMessage.get("JobResults.SimulationResult.SimulationBox.MinStep"), tmpMinStep);
                    String tmpJobResultParticlePositionsMinStepFilePathname = tmpMinStepToFilePathnameMap.get(tmpMinStep);
                    tmpMinTimeStepInfoArray[tmpIndex] = new TimeStepInfo(tmpMinStepInfo, tmpJobResultParticlePositionsMinStepFilePathname, tmpIndex);
                    tmpIndex++;
                }

                tmpValueItem = new ValueItem();
                tmpValueItem.setName("SIMULATION_MOVIE_MINIMIZATION_STEPS");
                tmpValueItem.setDisplayName(String.format(ModelMessage.get("JobResults.SimulationResult.SimulationBoxMinimizationTimeStep"), tmpMinStepSortStringList.size()));
                tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.SimulationBoxMinimizationTimeStep.Description"));
                tmpValueItem.setBasicType(ValueItemEnumBasicType.OBJECT);

                Object[] tmpObjectArray = new Object[3];
                tmpObjectArray[0] = SlicingTypeEnum.SIMULATION_MOVIE;
                tmpObjectArray[1] = tmpMinTimeStepInfoArray;
                tmpObjectArray[2] = this.getJobInput().getValueItemContainer();
                tmpValueItem.setObject(tmpObjectArray);

                tmpValueItem.setNodeNames(tmpNodeNames);
                tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                tmpValueItemContainer.addValueItem(tmpValueItem);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Simulation box after simulation or latest step">
            if ((new File(this.jobUtilityMethods.getJobResultParticlePositionsAfterSimulationFilePathname(this.jobResultPath))).isFile()) {
                tmpValueItem = new ValueItem();
                tmpValueItem.setName("SIMULATION_BOX_AFTER_SIMULATION");
                tmpValueItem.setDisplayName(ModelMessage.get("JobResults.SimulationResult.SimulationBox.AfterSimulation"));
                tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.SimulationBox.AfterSimulation.Description"));
                tmpValueItem.setBasicType(ValueItemEnumBasicType.OBJECT);

                Object[] tmpObjectArray = new Object[3];
                tmpObjectArray[0] = SlicingTypeEnum.SIMULATION_BOX;
                tmpObjectArray[1] = this.jobUtilityMethods.getJobResultParticlePositionsAfterSimulationFilePathname(this.jobResultPath);
                tmpObjectArray[2] = this.getJobInput().getValueItemContainer();
                tmpValueItem.setObject(tmpObjectArray);

                tmpValueItem.setNodeNames(tmpNodeNames);
                tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                tmpValueItemContainer.addValueItem(tmpValueItem);
            } else if (!tmpStepSortStringList.isEmpty()) {
                String tmpStep = tmpSortStringToStepMap.get(tmpStepSortStringList.getLast());
                String tmpJobResultParticlePositionsStepFilePathname = tmpStepToFilePathnameMap.get(tmpStep);

                double tmpSimulationInNanoseconds = tmpSingleStepLengthInNanoseconds * Double.valueOf(tmpStep);
                String tmpSimulationInNanosecondsString = this.stringUtilityMethods.formatDoubleValue(tmpSimulationInNanoseconds, 1);

                tmpValueItem = new ValueItem();
                tmpValueItem.setName("SIMULATION_BOX_LATEST_STEP");
                tmpValueItem.setDisplayName(String.format(ModelMessage.get("JobResults.SimulationResult.SimulationBox.Step"), tmpStep, tmpSimulationInNanosecondsString));
                tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.SimulationBox.Step.Description"));
                tmpValueItem.setBasicType(ValueItemEnumBasicType.OBJECT);

                Object[] tmpObjectArray = new Object[3];
                tmpObjectArray[0] = SlicingTypeEnum.SIMULATION_BOX;
                tmpObjectArray[1] = tmpJobResultParticlePositionsStepFilePathname;
                tmpObjectArray[2] = this.getJobInput().getValueItemContainer();
                tmpValueItem.setObject(tmpObjectArray);

                tmpValueItem.setNodeNames(tmpNodeNames);
                tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                tmpValueItemContainer.addValueItem(tmpValueItem);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Simulation movie">
            if (tmpTimeStepInfoArray != null) {
                tmpValueItem = new ValueItem();
                tmpValueItem.setName("SIMULATION_MOVIE_STEPS");
                tmpValueItem.setDisplayName(String.format(ModelMessage.get("JobResults.SimulationResult.SimulationBoxTimeStep"), tmpStepSortStringList.size()));
                tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.SimulationBoxTimeStep.Description"));
                tmpValueItem.setBasicType(ValueItemEnumBasicType.OBJECT);

                Object[] tmpObjectArray = new Object[3];
                tmpObjectArray[0] = SlicingTypeEnum.SIMULATION_MOVIE;
                tmpObjectArray[1] = tmpTimeStepInfoArray;
                tmpObjectArray[2] = this.getJobInput().getValueItemContainer();
                tmpValueItem.setObject(tmpObjectArray);

                tmpValueItem.setNodeNames(tmpNodeNames);
                tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                tmpValueItemContainer.addValueItem(tmpValueItem);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Simulation box steps">
            if (Preferences.getInstance().isSimulationStepInclusion()) {
                tmpNodeNames = 
                    new String[]
                        {
                            ModelMessage.get("JobResults.Root"), 
                            ModelMessage.get("JobResults.SimulationResult.SimulationBox"),
                            ModelMessage.get("JobResults.SimulationResult.SimulationBox.Steps")
                        };
                String[] tmpCurrentNodeNames = tmpNodeNames;
                int tmpCounter = 1;
                // Each group has 100 members so subtract 2
                int tmpResultStringLength = String.valueOf(tmpStepSortStringList.size()).length() - 2;
                if (tmpResultStringLength < 1) {
                    tmpResultStringLength = 1;
                }
                for (String tmpSortString : tmpStepSortStringList) {
                    String tmpStep = tmpSortStringToStepMap.get(tmpSortString);
                    String tmpJobResultParticlePositionsStepFilePathname = tmpStepToFilePathnameMap.get(tmpStep);

                    double tmpSimulationInNanoseconds = tmpSingleStepLengthInNanoseconds * Double.valueOf(tmpStep);
                    String tmpSimulationInNanosecondsString = this.stringUtilityMethods.formatDoubleValue(tmpSimulationInNanoseconds, 1);

                    tmpValueItem = new ValueItem();
                    tmpValueItem.setName("SIMULATION_BOX_STEP_" + tmpStep);
                    tmpValueItem.setDisplayName(String.format(ModelMessage.get("JobResults.SimulationResult.SimulationBox.Step"), tmpStep, tmpSimulationInNanosecondsString));
                    if (tmpCounter == 1) {
                        tmpCurrentNodeNames = this.stringUtilityMethods.getConcatenatedStringArrays(tmpNodeNames,
                                new String[]{String.format(ModelMessage.get("JobResults.SimulationResult.SimulationBox.Group"), tmpStep, tmpSimulationInNanosecondsString)});
                    }
                    tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.SimulationBox.Step.Description"));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.OBJECT);

                    Object[] tmpObjectArray = new Object[3];
                    tmpObjectArray[0] = SlicingTypeEnum.SIMULATION_BOX;
                    tmpObjectArray[1] = tmpJobResultParticlePositionsStepFilePathname;
                    tmpObjectArray[2] = this.getJobInput().getValueItemContainer();
                    tmpValueItem.setObject(tmpObjectArray);

                    tmpValueItem.setNodeNames(tmpCurrentNodeNames);
                    tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
                    tmpValueItemContainer.addValueItem(tmpValueItem);

                    tmpCounter++;
                    if (tmpCounter > 100) {
                        tmpCounter = 1;
                    }
                }
            }
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Job input history">
            if (Preferences.getInstance().isJobInputInclusion()) {
                String[] tmpJobInputHistoryFilePathnames = this.getSortedJobInputHistoryFilePathnames();
                if (tmpJobInputHistoryFilePathnames != null) {
                    int tmpIndex = 1;
                    for (String tmpJobInputHistoryFilePathname : tmpJobInputHistoryFilePathnames) {
                        String tmpBase64String = this.fileUtilityMethods.readTextFileIntoSingleString(tmpJobInputHistoryFilePathname);
                        String tmpXmlString = null;
                        // NOTE: Base64 strings do NOT contain a '<' character
                        if (tmpBase64String.startsWith("<")) {
                            // Legacy code: Old XML text string
                            tmpXmlString = tmpBase64String;
                        } else {
                            // New compressed Base64 string
                            tmpXmlString = this.stringUtilityMethods.decompressBase64String(tmpBase64String);
                        }
                        ValueItemContainer tmpValueItemContainerOfJobInput = new ValueItemContainer(tmpXmlString, null);
                        if (tmpJobInputHistoryFilePathnames.length > 1) {
                            // IMPORTANT: Do NOT add index to value items of first job input since its value items are still needed for value item based calculatons!
                            if (tmpIndex > 1) {
                                tmpValueItemContainerOfJobInput.addIndexToValueItemNames(tmpIndex);
                            }
                            tmpValueItemContainerOfJobInput.replaceNodeNameAtFirstPosition(String.format(ModelMessage.get("JobInput.IndexFormat"), String.valueOf(tmpIndex))
                            );
                        }
                        tmpValueItemContainerOfJobInput.addNodeNameAtFirstPosition(ModelMessage.get("JobResults.Root"));
                        tmpVerticalPosition = tmpValueItemContainerOfJobInput.setSuccessiveVerticalPositions(tmpVerticalPosition);
                        tmpValueItemContainer.addValueItems(tmpValueItemContainerOfJobInput.getSortedValueItemsOfContainer(), true);
                        tmpIndex++;
                    }
                }
            }
            // </editor-fold>
        }
        // </editor-fold>
        return tmpValueItemContainer;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Job Input history related methods">
    /**
     * Writes job input to history
     */
    public void appendJobInputToHistory() {
        try {
            if (this.getJobInput() != null && !(this.jobResultPath == null || this.jobResultPath.isEmpty())) {
                ValueItemContainer tmpJobInputValueItemContainer = this.getJobInput().getValueItemContainer();
                if (tmpJobInputValueItemContainer == null) {
                    return;
                }
                String tmpHistoryPath = this.jobUtilityMethods.getJobResultHistoryPath(this.jobResultPath);
                if ((new File(tmpHistoryPath)).isDirectory()) {
                    // History directory already exists so job is restarted: Remove TimeStepNumber
                    tmpJobInputValueItemContainer.removeValueItem("TimeStepNumber");
                } else {
                    if (!ModelUtils.createDirectory(tmpHistoryPath)) {
                        return;
                    }
                }
                String tmpNewHistoryJobInputPathname = this.jobUtilityMethods.getNewHistoryJobInputPathname(tmpHistoryPath);
                this.fileUtilityMethods.writeSingleStringToTextFile(
                    this.stringUtilityMethods.compressIntoBase64String(tmpJobInputValueItemContainer.getAsXmlString()), 
                    tmpNewHistoryJobInputPathname
                );
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return;
        }
    }

    /**
     * Returns sorted (ascending) job input history file pathnames
     *
     * @return Sorted (ascending) Job input history file pathnames or null if
     * none were found
     */
    public String[] getSortedJobInputHistoryFilePathnames() {
        if (!(this.jobResultPath == null || this.jobResultPath.isEmpty())) {
            String tmpHistoryPath = this.jobUtilityMethods.getJobResultHistoryPath(this.jobResultPath);
            return this.fileUtilityMethods.getSortedFilePathnames(tmpHistoryPath);
        } else {
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * compareTo() method for Comparable interface. Compares
     * timestampExecutionEnd.
     *
     * @param anotherJob JobResult to compare
     * @return Standard compareTo-result
     */
    @Override
    public int compareTo(JobResult anotherJob) {
        return this.timestampExecutionEnd.compareTo(anotherJob.getTimestampExecutionEnd());
    }

    /**
     * Overrides toString() method and returns job description
     *
     * @return JobResult description
     */
    @Override
    public String toString() {
        if (this.isStopped) {
            // <editor-fold defaultstate="collapsed" desc="JobResult is stopped">
            return String.format(ModelMessage.get("Format.JobResultStopInformationAndDescription"), 
                this.description
            );
            // </editor-fold>
        } else {
            if (this.aliveInformation != null) {
                // <editor-fold defaultstate="collapsed" desc="Alive-information is available">
                return String.format(ModelMessage.get("Format.JobResultAliveInformationAndDescription"), 
                    this.aliveInformation, 
                    this.description
                );
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Alive-Information does not exist">
                if (this.timestampExecutionEnd == null || this.timestampExecutionEnd.isEmpty() || this.jobProcessingResult == null) {
                    // <editor-fold defaultstate="collapsed" desc="No job processing result">
                    return this.description;
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Job processing result is available">
                    if (this.isToBeRestarted) {
                        // <editor-fold defaultstate="collapsed" desc="Job is to be restarted">
                        return String.format(ModelMessage.get("Format.JobResultDescriptionToStringToBeRestarted"), 
                            this.description, this.jobProcessingResult.toRepresentation(),
                            this.timestampExecutionEnd, 
                            String.valueOf(this.additionalStepsForRestart)
                        );
                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="Job is NOT to be restarted">
                        String tmpParticleSetFilename = ModelMessage.get("JobResults.NoParticleSetInfo");
                        String tmpMFsimApplicationVersion = ModelMessage.get("JobResults.NoVersionInfo");
                        JobInput tmpJobInput = this.getJobInput();
                        if (tmpJobInput != null) {
                            tmpParticleSetFilename = tmpJobInput.getParticleSetFilename();
                            tmpMFsimApplicationVersion = tmpJobInput.getMFsimApplicationVersion();
                        }
                        return String.format(ModelMessage.get("Format.JobResultDescriptionToString"), 
                            this.description, 
                            this.jobProcessingResult.toRepresentation(), 
                            this.timestampExecutionEnd,
                            tmpParticleSetFilename,
                            tmpMFsimApplicationVersion
                        );
                        // </editor-fold>
                    }
                    // </editor-fold>
                }
                // </editor-fold>
            }
        }
    }

    /**
     * Returns clone of this job result (with new unique ID)
     *
     * @return Clone of this job result (with new unique ID) or null if job
     * result could not be cloned
     */
    public JobResult getClone() {
        if (this.jobResultPath != null && !this.jobResultPath.isEmpty() && (new File(this.jobResultPath)).isDirectory()) {
            try {
                return new JobResult(this.jobResultPath);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * True if job result has valid job input path, false: Otherwise
     *
     * @return True: Job result has valid job input path, false: Otherwise
     */
    public boolean hasValidJobInputPath() {
        return !this.jobInputPath.isEmpty() && (new File(this.jobInputPath)).isDirectory();
    }
    
    /**
     * Returns MFsim version this job result was created with (e.g. "1.0.0.0")
     * 
     * @return MFsim version this job result was created with or null if none is available
     */
    public String getMFsimApplicationVersion() {
        String[] tmpAllMFsimVersionTokens = this.stringUtilityMethods.getAllTokens(this.mfSimVersionOfJobResult);
        if (tmpAllMFsimVersionTokens == null || tmpAllMFsimVersionTokens.length == 0) {
            return null;
        } else {
            return tmpAllMFsimVersionTokens[tmpAllMFsimVersionTokens.length - 1];
        }
    }
    
    /**
     * Checks if job result has nearest-neighbour information
     * 
     * @return True: Job result has nearest-neighbour information, false: 
     * Otherwise
     */
    public boolean hasNearestNeighbors() {
            String tmpBaseMoleculeParticleToNearestNeighborMoleculeParticleStepFrequencyMapFilePathname = 
                this.jobUtilityMethods.getJobResultBaseMoleculeParticleToNearestNeighborMoleculeParticleStepFrequencyMapFilePathname(this.jobResultPath);
            if ((new File(tmpBaseMoleculeParticleToNearestNeighborMoleculeParticleStepFrequencyMapFilePathname)).isFile()) {
                return true;
            }

            String tmpBaseMoleculeParticleToNearestNeighborParticleStepFrequencyMapFilePathname = 
                this.jobUtilityMethods.getJobResultBaseMoleculeParticleToNearestNeighborParticleStepFrequencyMapFilePathname(this.jobResultPath);
            if ((new File(tmpBaseMoleculeParticleToNearestNeighborParticleStepFrequencyMapFilePathname)).isFile()) {
                return true;
            }

            String tmpBaseMoleculeParticleToNearestNeighborMoleculeStepFrequencyMapFilePathname = 
                this.jobUtilityMethods.getJobResultBaseMoleculeParticleToNearestNeighborMoleculeStepFrequencyMapFilePathname(this.jobResultPath);
            if ((new File(tmpBaseMoleculeParticleToNearestNeighborMoleculeStepFrequencyMapFilePathname)).isFile()) {
                return true;
            }

            String tmpBaseMoleculeToNearestNeighborMoleculeStepFrequencyMapFilePathname = 
                this.jobUtilityMethods.getJobResultBaseMoleculeToNearestNeighborMoleculeStepFrequencyMapFilePathname(this.jobResultPath);
            if ((new File(tmpBaseMoleculeToNearestNeighborMoleculeStepFrequencyMapFilePathname)).isFile()) {
                return true;
            }

            String tmpBaseMoleculeToNearestNeighborMoleculeTupleStepFrequencyMapFilePathname = 
                this.jobUtilityMethods.getJobResultBaseMoleculeToNearestNeighborMoleculeTupleStepFrequencyMapFilePathname(this.jobResultPath);
            if ((new File(tmpBaseMoleculeToNearestNeighborMoleculeTupleStepFrequencyMapFilePathname)).isFile()) {
                return true;
            }

            return false;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Write methods">
    /**
     * Writes job information to result directory defined in aJobInformationPath
     * if possible
     *
     * @return true: Operation was successful, false: Otherwise
     */
    public boolean writeJobResultInformation() {
        String tmpJobInformationPathname = this.jobUtilityMethods.getJobResultInformationFilePathname(this.jobResultPath);
        // Delete job information file if necessary
        if (!this.fileUtilityMethods.deleteSingleFile(tmpJobInformationPathname)) {
            return false;
        }
        // Write to file:
        // Line 1: Version
        // Line 2: this.description
        // Line 3: this.timestampExecutionStart
        // Line 4: this.timestampExecutionEnd
        // Line 5: this.executionPeriod
        // Line 6: this.jobProcessingResult.name()
        // Line 7: this.jobInputPath
        // Line 8: this.mfSimVersionOfJobResult
        // Line 9: this.maximumUsedParallelTaskNumberInfoString
        String[] infos = new String[]{
            "Version 1.0.0",
            this.description,
            this.timestampExecutionStart,
            this.timestampExecutionEnd,
            this.executionPeriod,
            this.jobProcessingResult.name(),
            this.jobInputPath,
            String.format(ModelMessage.get("ApplicationVersionFormat"), ModelDefinitions.APPLICATION_VERSION),
            this.maximumUsedParallelTaskNumberInfoString
        };
        return this.fileUtilityMethods.writeDefinedStringArrayToFile(infos, tmpJobInformationPathname);
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    // <editor-fold defaultstate="collapsed" desc="- Description (get)">
    /**
     * Description of job
     *
     * @return JobResult description
     */
    public String getDescription() {
        return this.description;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ExecutionPeriod (get)">
    /**
     * Returns execution period of job, i.e. the difference between start and
     * end timestamp as a defined format string
     *
     * @return Execution period of job, i.e. the difference between start and
     * end timestamp as a defined format string or null if timestamp information
     * is not available
     */
    public String getExecutionPeriod() {
        return this.executionPeriod;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobResultId (get)">
    /**
     * Job result ID of this instance
     *
     * @return Job result ID of this instance
     */
    public String getJobResultId() {
        return this.jobResultId;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobInput">
    /**
     * Returns JobInput instance for this JobResult. NOTE: XML-coded JobInput
     * information is taken from this.jobResultPath and NOT from
     * this.jobInputPath if necessary.
     *
     * @return JobInput instance for this JobResult
     */
    public JobInput getJobInput() {
        if (this.jobInput == null && !(this.jobResultPath == null || this.jobResultPath.isEmpty())) {
            this.jobInput = new JobInput(this.jobUtilityMethods.getJobInputPathOfJobResult(this.jobResultPath), false);
        }
        return this.jobInput;
    }

    /**
     * Removes job input instance
     */
    public void removeJobInput() {
        if (this.jobInput != null) {
            this.jobInput = null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Jdpd log file pathname">
    /**
     * Jdpd logfile pathname
     * 
     * @return Jdpd logfile pathname or null if none is available
     */
    public String getJdpdLogfilePathname() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.jobResultPath == null || this.jobResultPath.isEmpty() || !(new File(this.jobResultPath)).isDirectory()) {
            ModelUtils.appendToLogfile(true, "JobResult.getJdpdLogfilePathname(): this.jobResultPath == null || this.jobResultPath.isEmpty() || !(new File(this.jobResultPath)).isDirectory()");
            return null;
        }
        // </editor-fold>
        return this.jobResultPath + File.separatorChar + ModelDefinitions.JDPD_OUTPUT_DIRECTORY + File.separatorChar + ModelDefinitions.JDPD_LOG_FILENAME;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (set only)">
    /**
     * Maximum used parallel task number info string
     * NOTE: No checks are performed.
     * 
     * @param tmpParallelTaskNumber MParallel task number
     * @param tmpMaximumUsedParallelTaskNumber Maximum used parallel task number
     * @param tmpMinimumParallelTaskCellNumber Minimum number of box cells for parallelisation
     * @param tmpMinimumParallelTaskHarmonicBondNumber Minimum number of harmonic bonds for parallelisation
     */
    public void setMaximumUsedParallelTaskNumberInfoString(
        int tmpParallelTaskNumber,
        int tmpMaximumUsedParallelTaskNumber,
        int tmpMinimumParallelTaskCellNumber,
        int tmpMinimumParallelTaskHarmonicBondNumber
    ) {
        this.maximumUsedParallelTaskNumberInfoString = 
            String.format(ModelMessage.get("JobResults.ParallelizationInfoFormat"),
                tmpParallelTaskNumber,
                tmpMaximumUsedParallelTaskNumber,
                tmpMinimumParallelTaskCellNumber,
                tmpMinimumParallelTaskHarmonicBondNumber
            );
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- AliveInformation (get/set)">
    /**
     * Returns alive-information
     *
     * @return alive-information
     */
    public String getAliveInformation() {
        return this.aliveInformation;
    }

    /**
     * Sets alive-information
     *
     * @param aProgressPercentage Progress in percent
     * @param aTimeRemaining Time remaining
     */
    public void setAliveInformation(int aProgressPercentage, String aTimeRemaining) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTimeRemaining == null || aTimeRemaining.isEmpty()) {
            return;
        }

        // </editor-fold>
        this.aliveInformation = String.format(ModelMessage.get("Format.JobResultAliveInformation"), String.valueOf(aProgressPercentage), aTimeRemaining);
    }

    /**
     * Sets alive-information that job is running
     */
    public void setAliveInformationRunning() {
        this.aliveInformation = ModelMessage.get("Format.JobResultAliveInformation.Running");
    }

    /**
     * Sets alive-information that job is starting
     */
    public void setAliveInformationStarting() {
        this.aliveInformation = ModelMessage.get("Format.JobResultAliveInformation.Starting");
    }

    /**
     * Sets alive-information that job is finishing
     */
    public void setAliveInformationFinishing() {
        this.aliveInformation = ModelMessage.get("Format.JobResultAliveInformation.Finishing");
    }

    /**
     * Removes alive information
     */
    public void removeAliveInformation() {
        this.aliveInformation = null;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobProcessingResult (get/set)">
    /**
     * JobResult processing result
     *
     * @return JobResult processing result
     */
    public JobResultProcessingStatusEnum getJobProcessingResult() {
        return this.jobProcessingResult;
    }

    /**
     * JobResult processing result
     *
     * @param aJobProcessingResult JobResult processing result
     */
    public void setJobProcessingResult(JobResultProcessingStatusEnum aJobProcessingResult) {
        this.jobProcessingResult = aJobProcessingResult;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobResultPath (get/set)">
    /**
     * Full path of result directory for this job
     *
     * @return Full path to result directory for this job
     */
    public String getJobResultPath() {
        return this.jobResultPath;
    }

    /**
     * Full path of result directory for this job. Result directory is created
     * if necessary. JobResult input file is copied into result path if
     * necessary. JobResult information file is written to result directory.
     *
     * @param aResultPath Full path of result directory for this job
     * @throws IllegalArgumentException Exception is thrown if aResultPath is
     * null/empty, result path can not be created, Jdpd input file can not
     * be copied to result path or not deleted from result path
     */
    public void setJobResultPath(String aResultPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aResultPath == null || aResultPath.isEmpty()) {
            throw new IllegalArgumentException("JobResult.setJobResultPath(): aResultPath is null/empty");
        }
        if (!(new File(aResultPath)).isDirectory()) {
            if (!ModelUtils.createDirectory(aResultPath)) {
                throw new IllegalArgumentException("JobResult.setJobResultPath(): Can not create directory: " + aResultPath);
            }
        }
        // Create input and output directory for Jdpd if necessary
        if (!ModelUtils.createDirectory(aResultPath + File.separatorChar + ModelDefinitions.JDPD_INPUT_DIRECTORY)) {
            throw new IllegalArgumentException("JobResult.setJobResultPath(): Can not create directory: " + aResultPath + File.separatorChar + ModelDefinitions.JDPD_INPUT_DIRECTORY);
        }
        if (!ModelUtils.createDirectory(aResultPath + File.separatorChar + ModelDefinitions.JDPD_OUTPUT_DIRECTORY)) {
            throw new IllegalArgumentException("JobResult.setJobResultPath(): Can not create directory: " + aResultPath + File.separatorChar + ModelDefinitions.JDPD_OUTPUT_DIRECTORY);
        }
        // </editor-fold>
        this.jobResultPath = aResultPath;
        // <editor-fold defaultstate="collapsed" desc="Copy job input directory to job result directory and set new this.jobInputPath">
        if (this.hasValidJobInputPath()) {
            String tmpNewJobInputPath = this.jobResultPath + File.separatorChar + (new File(this.jobInputPath)).getName();
            // Copy job input if necessary
            if (!(new File(tmpNewJobInputPath)).isDirectory()) {
                if (!this.fileUtilityMethods.copyDirectory(this.jobInputPath, this.jobResultPath)) {
                    throw new IllegalArgumentException("JobResult.setJobResultPath(): Can not copy into directory: " + aResultPath);
                }
            }
            // Set new job input path in job result path
            this.jobInputPath = tmpNewJobInputPath;
        }
        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Additional steps for restart (get/set)">
    /**
     * Returns additional steps for restart
     *
     * @return Additional steps for restart
     */
    public int getAdditionalStepsForRestart() {
        return this.additionalStepsForRestart;
    }

    /**
     * Sets nsteps for restart
     *
     * @param aNStepsForRestart nsteps for restart
     */
    public void setAdditionalStepsForRestart(int aNStepsForRestart) {
        this.additionalStepsForRestart = aNStepsForRestart;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Restarted (get/set)">
    /**
     * Returns if job is restarted
     *
     * @return true: JobResult is restarted, false: Otherwise
     */
    public boolean isRestarted() {
        return this.isRestarted;
    }

    /**
     * Sets if job is restarted
     *
     * @param anIsRestarted true: JobResult is restarted, false: Otherwise
     */
    public void setRestarted(boolean anIsRestarted) {
        this.isRestarted = anIsRestarted;
        if (this.isRestarted) {
            this.setToBeRestarted(false);
            this.description = String.format(ModelMessage.get("Format.JobResultDescriptionRestart"), this.description, String.valueOf(this.additionalStepsForRestart));
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- TimestampExecutionEnd (get/set)">
    /**
     * Timestamp for job execution end
     *
     * @return Timestamp for job execution end
     */
    public String getTimestampExecutionEnd() {
        return this.timestampExecutionEnd;
    }

    /**
     * Timestamp for job execution end
     *
     * @param aTimestamp Timestamp
     */
    public void setTimestampExecutionEnd(String aTimestamp) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.timeUtilityMethods.isValidTimestampInStandardFormat(aTimestamp)) {
            return;
        }

        // </editor-fold>
        this.timestampExecutionEnd = aTimestamp;

        // <editor-fold defaultstate="collapsed" desc="Calculate execution period">
        if (this.timestampExecutionStart.length() != 0 || this.timestampExecutionEnd.length() != 0) {
            this.executionPeriod = this.timeUtilityMethods.getDateTimeDifference(this.timestampExecutionStart, this.timestampExecutionEnd);
        }

        // </editor-fold>
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- TimestampExecutionStart (get/set)">
    /**
     * Timestamp for job execution start
     *
     * @return Timestamp for job execution start
     */
    public String getTimestampExecutionStart() {
        return this.timestampExecutionStart;
    }

    /**
     * Timestamp for job execution start
     *
     * @param aTimestamp Timestamp
     */
    public void setTimestampExecutionStart(String aTimestamp) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.timeUtilityMethods.isValidTimestampInStandardFormat(aTimestamp)) {
            return;
        }

        // </editor-fold>
        this.timestampExecutionStart = aTimestamp;
        // Clear timestampExecutionEnd and executionPeriod
        this.timestampExecutionEnd = "";
        this.executionPeriod = "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ToBeRestarted (get/set)">
    /**
     * Returns if job is to be restarted
     *
     * @return true: JobResult is to be restarted, false: Otherwise
     */
    public boolean isToBeRestarted() {
        return this.isToBeRestarted;
    }

    /**
     * Sets if job is to be restarted
     *
     * @param anIsToBeRestarted true: JobResult is to be restarted, false:
     * Otherwise
     */
    public void setToBeRestarted(boolean anIsToBeRestarted) {
        this.isToBeRestarted = anIsToBeRestarted;
        if (this.isToBeRestarted) {
            this.setRestarted(false);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Stopped (get/set)">
    /**
     * Returns if JobResult is stopped
     *
     * @return true: JobResult is stopped, false: Otherwise
     */
    public boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Sets stop status of JobResult to true
     */
    public void setStopped() {
        this.isStopped = true;
        this.removeAliveInformation();
    }

    /**
     * Sets stop status of JobResult
     *
     * @param anIsStopped true: JobResult is stopped, false: Otherwise
     */
    public void setStopped(boolean anIsStopped) {
        this.isStopped = anIsStopped;
        if (this.isStopped) {
            this.removeAliveInformation();
        }
    }

    /**
     * Removes stopped status of JobResult
     */
    public void removeStopped() {
        this.isStopped = false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NewJdpdInputFileText (get/set)">
    /**
     * New text for Jdpd input file
     *
     * @param aNewText New text
     * @return True: Jdpd input text changed, false: Otherwise
     */
    public boolean setNewJdpdInputText(String aNewText) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNewText == null || aNewText.isEmpty()) {
            if (this.newJdpdInputFileText == null || this.newJdpdInputFileText.isEmpty()) {
                this.newJdpdInputFileText = null;
                return false;
            } else {
                this.newJdpdInputFileText = null;
                return true;
            }
        }

        // </editor-fold>
        String tmpOldJdpdInputText = this.getJdpdInputText();
        if (tmpOldJdpdInputText != null) {
            // NOTE: Use of trim()-Method is IMPORTANT to compare both texts
            if (tmpOldJdpdInputText.trim().equals(aNewText.trim())) {
                return false;
            } else {
                this.newJdpdInputFileText = aNewText;
                return true;
            }
        } else {
            // NOTE: Use of trim()-Method is IMPORTANT to compare both texts
            if (this.newJdpdInputFileText.trim().equals(aNewText.trim())) {
                return false;
            } else {
                this.newJdpdInputFileText = aNewText;
                return true;
            }
        }
    }

    /**
     * New text for Jdpd input file from job input value item container
     *
     * @param aJobInputValueItemContainer Value item container of job input
     * @return True: Jdpd input text changed, false: Otherwise
     */
    public boolean setNewJdpdInputTextWithJobInputValueItemContainer(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return false;
        }

        // </editor-fold>
        try {
            // Parameter aJdpdInputFilePathname is NOT specified (null): Jdpd input file and all start-geometry files of molecules are NOT written!
            return this.setNewJdpdInputText(this.jobUtilityMethods.getJdpdInputText(aJobInputValueItemContainer, null));
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * New text for Jdpd input file
     *
     * @return New text for Jdpd input file (may be null or empty)
     */
    public String getNewJdpdInputFileText() {
        return this.newJdpdInputFileText;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- FileOutput (get/set)">
    /**
     * Jdpd file output
     * 
     * @return Jdpd file output
     */
    public FileOutput getJdpdFileOutput() {
        return this.jdpdfileOutput;
    }

    /**
     * Jdpd file output
     * 
     * @param aJdpdFileOutput Jdpd file output
     */
    public void setJdpdFileOutput(FileOutput aJdpdFileOutput) {
        this.jdpdfileOutput = aJdpdFileOutput;
    }
    
    /**
     * True: Jdpd file output exists, false: Otherwise
     * 
     * @return True: Jdpd file output exists, false: Otherwise
     */
    public boolean hasJdpdFileOutput() {
        return this.jdpdfileOutput != null;
    }
    
    /**
     * Removes Jdpd file output
     */
    public void removeJdpdFileOutput() {
        this.jdpdfileOutput = null;
    }
    //
    // <editor-fold defaultstate="collapsed" desc="- PathLocked (get/set)">
    /**
     * Locks job result path
     */
    public void lockResultPath() {
        this.isResultPathLocked = true;
    }

    /**
     * Unlocks job result path
     */
    public void unlockResultPath() {
        this.isResultPathLocked = false;
    }
    
    /**
     * Returns if job result path is locked
     * 
     * @return True: Job result path is locked, false: Otherwise
     */
    public boolean isResultPathLocked() {
        return this.isResultPathLocked;
    }
    // </editor-fold>
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Initialize class variables
     */
    private void initialize() {
        this.jobResultId = this.stringUtilityMethods.getGloballyUniqueID();
        this.description = "";
        this.executionPeriod = "";
        this.isRestarted = false;
        this.isToBeRestarted = false;
        this.isStopped = false;
        this.jobProcessingResult = JobResultProcessingStatusEnum.NO_STATUS_DEFINED;
        this.additionalStepsForRestart = 0;
        this.jobInputPath = "";
        this.jobResultPath = "";
        this.timestampExecutionEnd = "";
        this.timestampExecutionStart = "";
        this.aliveInformation = null;
        this.jobInput = null;
        this.newJdpdInputFileText = null;
        this.mfSimVersionOfJobResult = null;
        this.jdpdfileOutput = null;
        this.isResultPathLocked = false;
        this.maximumUsedParallelTaskNumberInfoString = ModelMessage.get("JobResults.NoParallelizationInfo");
    }

    /**
     * Returns specific value item matrix, see code.
     *
     * @param anInfoArray Info string array
     * @param aDataTypeFormatText Value item data type format
     * @return Specific value item matrix, see code.
     */
    private ValueItemMatrixElement[][] getValueItemMatrix(String[] anInfoArray, ValueItemDataTypeFormat aDataTypeFormatText) {
        return getValueItemMatrix(anInfoArray, aDataTypeFormatText, 1, Double.POSITIVE_INFINITY);
    }

    /**
     * Returns specific value item matrix, see code.
     *
     * @param anInfoArray Info string array
     * @param aDataTypeFormatText Value item data type format
     * @param anOffset Offset for number of lines to skip
     * @param aXValueCutoff If x-value in anInfoArray is greater than
     * aXValueCutoff all further values are excluded (if
     * Double.POSITIVE_INFINITY all data are included).
     * @return Specific value item matrix, see code.
     */
    private ValueItemMatrixElement[][] getValueItemMatrix(String[] anInfoArray, ValueItemDataTypeFormat aDataTypeFormatText, int anOffset, double aXValueCutoff) {
        // <editor-fold defaultstate="collapsed" desc="Set number of data pairs">
        int tmpNumberOfDataPairs = (anInfoArray.length - anOffset) / 2;
        int tmpNumberOfDataPairsWithCutoff = 0;
        if (aXValueCutoff == Double.POSITIVE_INFINITY) {
            tmpNumberOfDataPairsWithCutoff = tmpNumberOfDataPairs;
        } else {
            int tmpIndex = anOffset;
            for (int i = 0; i < tmpNumberOfDataPairs; i++) {
                double tmpXValue = Double.valueOf(anInfoArray[tmpIndex]);
                if (tmpXValue <= aXValueCutoff) {
                    tmpNumberOfDataPairsWithCutoff++;
                    tmpIndex += 2;
                } else {
                    break;
                }
            }
        }
        // </editor-fold>
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpNumberOfDataPairsWithCutoff][];
        int tmpIndex = anOffset;
        for (int i = 0; i < tmpNumberOfDataPairsWithCutoff; i++) {
            ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
            tmpRow[0] = new ValueItemMatrixElement(anInfoArray[tmpIndex++], aDataTypeFormatText);
            tmpRow[1] = new ValueItemMatrixElement(anInfoArray[tmpIndex++], aDataTypeFormatText);
            tmpMatrix[i] = tmpRow;
        }
        return tmpMatrix;
    }

    /**
     * Returns specific value item matrix, see code.
     * NOTE: NO checks are performed.
     *
     * @param anElementList Element list
     * @param aDataTypeFormatText Value item data type format
     * @return Specific value item matrix, see code.
     */
    private ValueItemMatrixElement[][] getValueItemMatrix(LinkedList<String> anElementList, ValueItemDataTypeFormat aDataTypeFormatText) {
        int tmpNumberOfDataPairs = anElementList.size() / 2;
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpNumberOfDataPairs][];
        int tmpIndex = 0;
        boolean tmpIsRowZero = true;
        ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
        for (String anElement : anElementList) {
            if (tmpIsRowZero) {
                tmpRow[0] = new ValueItemMatrixElement(anElement, aDataTypeFormatText);
                tmpIsRowZero = false;
            } else {
                tmpRow[1] = new ValueItemMatrixElement(anElement, aDataTypeFormatText);
                tmpMatrix[tmpIndex++] = tmpRow;
                tmpIsRowZero = true;
                tmpRow = new ValueItemMatrixElement[2];
            }
        }
        return tmpMatrix;
    }

    /**
     * Returns specific multiplied value item matrix, see code.
     *
     * @param anInfoArray Info string array
     * @param aDataTypeFormatText Value item data type format
     * @param aFactor Factor for multiplication
     * @param anOffset Offset for number of lines to skip
     * @param aXValueCutoff If x-value in anInfoArray is greater than
     * aXValueCutoff all further values are excluded (if
     * Double.POSITIVE_INFINITY all data are included).
     * @return Specific value item matrix, see code.
     */
    private ValueItemMatrixElement[][] getMultipliedValueItemMatrix(String[] anInfoArray, ValueItemDataTypeFormat aDataTypeFormatText, double aFactor, int anOffset, double aXValueCutoff) {
        // <editor-fold defaultstate="collapsed" desc="Set number of data pairs">
        int tmpNumberOfDataPairs = (anInfoArray.length - anOffset) / 2;
        int tmpNumberOfDataPairsWithCutoff = 0;
        if (aXValueCutoff == Double.POSITIVE_INFINITY) {
            tmpNumberOfDataPairsWithCutoff = tmpNumberOfDataPairs;
        } else {
            int tmpIndex = anOffset;
            for (int i = 0; i < tmpNumberOfDataPairs; i++) {
                double tmpXValue = Double.valueOf(anInfoArray[tmpIndex]);
                if (tmpXValue <= aXValueCutoff) {
                    tmpNumberOfDataPairsWithCutoff++;
                    tmpIndex += 2;
                } else {
                    break;
                }
            }
        }
        // </editor-fold>
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpNumberOfDataPairsWithCutoff][];
        int tmpIndex = anOffset;
        for (int i = 0; i < tmpNumberOfDataPairsWithCutoff; i++) {
            ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
            tmpRow[0] = new ValueItemMatrixElement(anInfoArray[tmpIndex++], aDataTypeFormatText);
            double tmpMultipliedValue = Double.valueOf(anInfoArray[tmpIndex++]) * aFactor;
            tmpRow[1] = new ValueItemMatrixElement(String.valueOf(tmpMultipliedValue), aDataTypeFormatText);
            tmpMatrix[i] = tmpRow;
        }
        return tmpMatrix;
    }
    
    /**
     * Returns specific averaged value item matrix, see code.
     *
     * @param anInfoArray Info string array
     * @param aDataTypeFormatText Value item data type format
     * @param anOffset Offset for number of lines to skip
     * @param aXValueCutoff If x-value in anInfoArray is greater than
     * aXValueCutoff all further values are excluded (if
     * Double.POSITIVE_INFINITY all data are included).
     * @return Specific value item matrix, see code.
     */
    private ValueItemMatrixElement[][] getAveragedValueItemMatrix(String[] anInfoArray, ValueItemDataTypeFormat aDataTypeFormatText, int anOffset, double aXValueCutoff) {
        // <editor-fold defaultstate="collapsed" desc="Set number of data pairs">
        int tmpNumberOfDataPairs = (anInfoArray.length - anOffset) / 2;
        int tmpNumberOfDataPairsWithCutoff = 0;
        if (aXValueCutoff == Double.POSITIVE_INFINITY) {
            tmpNumberOfDataPairsWithCutoff = tmpNumberOfDataPairs;
        } else {
            int tmpIndex = anOffset;
            for (int i = 0; i < tmpNumberOfDataPairs; i++) {
                double tmpXValue = Double.valueOf(anInfoArray[tmpIndex]);
                if (tmpXValue <= aXValueCutoff) {
                    tmpNumberOfDataPairsWithCutoff++;
                    tmpIndex += 2;
                } else {
                    break;
                }
            }
        }

        // </editor-fold>
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpNumberOfDataPairsWithCutoff][];
        int tmpIndex = anOffset;
        for (int i = 0; i < tmpNumberOfDataPairsWithCutoff; i++) {
            ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
            tmpRow[0] = new ValueItemMatrixElement(anInfoArray[tmpIndex++], aDataTypeFormatText);

            String[] tmpValueRepresentations = ModelDefinitions.GENERAL_SEPARATOR_PATTERN.split(anInfoArray[tmpIndex++]);
            String tmpAveragedValueRepresentation = null;
            if (tmpValueRepresentations.length == 1) {
                tmpAveragedValueRepresentation = tmpValueRepresentations[0];
            } else {
                int tmpCounter = 0;
                double tmpSum = 0.0;
                for (String tmpValueRepresentation : tmpValueRepresentations) {
                    if (!tmpValueRepresentation.isEmpty()) {
                        double tmpValue = Double.valueOf(tmpValueRepresentation);
                        tmpSum += tmpValue;
                        tmpCounter++;
                    }
                }
                tmpAveragedValueRepresentation = String.valueOf(tmpSum / (double) tmpCounter);
            }

            tmpRow[1] = new ValueItemMatrixElement(tmpAveragedValueRepresentation, aDataTypeFormatText);
            tmpMatrix[i] = tmpRow;
        }
        return tmpMatrix;
    }

    /**
     * New text for Jdpd input file
     *
     * @return True: Job result has new input file text for Jdpd, false:
     * Otherwise.
     */
    private boolean hasNewJdpdInputFileText() {
        return this.newJdpdInputFileText != null && !this.newJdpdInputFileText.isEmpty();
    }
    
    /**
     * Reads job information from jobResultPath if possible (see code)
     *
     * @return true: Operation was successful, false: Otherwise
     */
    private boolean readJobResultInformation() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        String tmpJobInformationPathname = this.jobUtilityMethods.getJobResultInformationFilePathname(this.jobResultPath);
        if (!(new File(tmpJobInformationPathname)).isFile()) {
            return false;
        }

        // </editor-fold>
        try {
            String[] infos = this.fileUtilityMethods.readDefinedStringArrayFromFile(tmpJobInformationPathname);
            if (infos == null || infos.length == 0) {
                return false;
            }
            // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
            // Line 1: Version
            // Line 2: this.description
            // Line 3: this.timestampExecutionStart
            // Line 4: this.timestampExecutionEnd
            // Line 5: this.executionPeriod
            // Line 6: this.jobProcessingResult.name()
            // Line 7: this.jobInputPath
            // Line 8: this.mfSimVersionOfJobResult
            // Line 9: this.maximumUsedParallelTaskNumberInfoString
            if (infos[0].equals("Version 1.0.0")) {
                // <editor-fold defaultstate="collapsed" desc="Line 2 - this.description">
                if (infos.length > 1) {
                    this.description = infos[1];
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 3 - this.timestampExecutionStart">
                if (infos.length > 2) {
                    this.timestampExecutionStart = infos[2];
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 4 - this.timestampExecutionEnd">
                if (infos.length > 3) {
                    this.timestampExecutionEnd = infos[3];
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 5 - this.executionPeriod">
                if (infos.length > 4) {
                    this.executionPeriod = infos[4];
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 6 - this.jobProcessingResult.name()">
                if (infos.length > 5) {
                    this.jobProcessingResult = JobResultProcessingStatusEnum.valueOf(infos[5]);
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 7 - this.jobInputPath">
                if (infos.length > 6) {
                    this.jobInputPath = infos[6];
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 8 - this.mfSimVersionOfJobResult">
                if (infos.length > 7) {
                    this.mfSimVersionOfJobResult = infos[7];
                } else {
                    return false;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 9 - this.maximumUsedParallelTaskNumberInfoString">
                if (infos.length > 8) {
                    this.maximumUsedParallelTaskNumberInfoString = infos[8];
                } else {
                    return false;
                }
                // </editor-fold>
            }
            // </editor-fold>
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Sets nearest-neighbbor value items (see code)
     * NOTE: NO checks are performed.
     * 
     * @param aBaseToNearestNeighborStepFrequencyMapFilePathname Base to 
     * nearest-neighbor step-frequency map file pathname
     * @param aNodeNames Node names
     * @param aValueItemDisplayNameFormat Value item display name format
     * @param aValueItemDescriptionFormat Value item description format
     * @param aVerticalPosition Current vertical position
     * @param aValueItemContainer Value item container
     * @return (Possibly) Incremented vertical position
     */
    private int setNearestNeighborValueItems(
        String aBaseToNearestNeighborStepFrequencyMapFilePathname,
        String[] aNodeNames,
        String aValueItemDisplayNameFormat,
        String aValueItemDescriptionFormat,
        int aVerticalPosition,
        ValueItemContainer aValueItemContainer
    ) {
        BufferedReader tmpBufferedReader = null;
        try {
            FileReader tmpFileReader = new FileReader(aBaseToNearestNeighborStepFrequencyMapFilePathname);
            tmpBufferedReader = new BufferedReader(tmpFileReader, Constants.BUFFER_SIZE);
            // Read version
            String tmpVersion = tmpBufferedReader.readLine();
            if (!tmpVersion.equals(Strings.VERSION_1_0_0)) {
                return aVerticalPosition;
            }
            // NOTE: "0" = NO decimals
            ValueItemDataTypeFormat tmpDataTypeFormatText = new ValueItemDataTypeFormat(0, false, false);
            // Read number of bases
            int tmpNumberOfBases = Integer.valueOf(tmpBufferedReader.readLine());
            for (int tmpBaseCounter = 0; tmpBaseCounter < tmpNumberOfBases; tmpBaseCounter++) {
                // Read base
                String tmpBase = tmpBufferedReader.readLine();
                String[] tmpCurrentNodeNames = this.stringUtilityMethods.getConcatenatedStringArrays(aNodeNames, new String[]{ tmpBase });
                // Read number of nearest neighbors
                int tmpNumberOfNearestNeighbors = Integer.valueOf(tmpBufferedReader.readLine());
                for (int tmpNearestNeighborCounter = 0; tmpNearestNeighborCounter < tmpNumberOfNearestNeighbors; tmpNearestNeighborCounter++) {
                    // Read nearest neighbor
                    String tmpNearestNeighbor = tmpBufferedReader.readLine();
                    // Read number of list elements
                    int tmpNumberOfListElements = Integer.valueOf(tmpBufferedReader.readLine());
                    // NOTE: tmpStepFrequencyList does NOT have a version
                    LinkedList<String> tmpStepFrequencyList = new LinkedList<>();
                    // NOTE: FIRST line is version
                    boolean tmpIsFirstLine = true;
                    for (int tmpListElementCounter = 0; tmpListElementCounter < tmpNumberOfListElements; tmpListElementCounter++) {
                        if (tmpIsFirstLine) {
                            String tmpListVersion = tmpBufferedReader.readLine();
                            // NOTE: List version is NOT checked!
                            tmpIsFirstLine = false;
                        } else {
                            // Read list element
                            tmpStepFrequencyList.add(tmpBufferedReader.readLine());
                        }
                    }
                    ValueItem tmpValueItem = new ValueItem();
                    tmpValueItem.setName("NEAREST_NEIGHBOR_" + tmpBase + "_" + tmpNearestNeighbor + "_" + String.valueOf(aVerticalPosition));
                    tmpValueItem.setDisplayName(String.format(aValueItemDisplayNameFormat, tmpBase, tmpNearestNeighbor));
                    tmpValueItem.setDescription(String.format(aValueItemDescriptionFormat, tmpBase, tmpNearestNeighbor));
                    tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                    tmpValueItem.setNodeNames(tmpCurrentNodeNames);
                    tmpValueItem.setVerticalPosition(aVerticalPosition++);
                    tmpValueItem.setMatrixColumnNames(new String[] { 
                            ModelMessage.get("JobResults.SimulationResult.Progress.Step"),
                            ModelMessage.get("JobResults.SimulationResult.Progress.Frequency")
                        }
                    );
                    tmpValueItem.setMatrixColumnWidths(new String[] { 
                            ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Step
                            ModelDefinitions.CELL_WIDTH_NUMERIC_100 // Frequency
                        }
                    );
                    tmpValueItem.setMatrix(this.getValueItemMatrix(tmpStepFrequencyList, tmpDataTypeFormatText));
                    // IMPORTANT: Set diagram columns
                    tmpValueItem.setMatrixDiagramColumns(0, 1);
                    aValueItemContainer.addValueItem(tmpValueItem);
                }
            }
            return aVerticalPosition;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return aVerticalPosition;
        } finally {
            if (tmpBufferedReader != null) {
                try {
                    tmpBufferedReader.close();
                } catch (IOException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    return aVerticalPosition;
                }
            }
        }
    }
    
    /**
     * Sets particle distribution value item
     * 
     * @param aParticleTypeDescriptionString Particle description string
     * @param aNodeNames Node names
     * @param aVolumeAxis Simulation box axis
     * @param aParticleType Particle type
     * @param tmpTimeStepInfoArray Time step info array
     * @param aVerticalPosition Current vertical position
     * @param aValueItemContainer Value item container
     * @return Incremented vertical position
     */
    private int setDistributionValueItem(
        String aParticleTypeDescriptionString,
        String[] aNodeNames,
        VolumeFrequency.VolumeAxis aVolumeAxis,
        ParticleType aParticleType,
        TimeStepInfo[] aTimeStepInfoArray,
        int aVerticalPosition,
        ValueItemContainer aValueItemContainer
    ) {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setName("DISTRIBUTION_MOVIE_" + aVolumeAxis.name() + "_" + aParticleType.name() + "_" + String.valueOf(aVerticalPosition));
        tmpValueItem.setDisplayName(aParticleTypeDescriptionString);
        tmpValueItem.setDescription(ModelMessage.get("JobResults.SimulationResult.DistributionMovie.Description"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.OBJECT);

        Object[] tmpObjectArray = new Object[6];
        tmpObjectArray[0] = SlicingTypeEnum.DISTRIBUTION_MOVIE;
        tmpObjectArray[1] = aTimeStepInfoArray;
        tmpObjectArray[2] = this.getJobInput().getValueItemContainer();
        tmpObjectArray[3] = aVolumeAxis;
        tmpObjectArray[4] = aParticleType;
        tmpObjectArray[5] = aParticleTypeDescriptionString;
        tmpValueItem.setObject(tmpObjectArray);

        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition + 1;
    }

    // </editor-fold>

}
