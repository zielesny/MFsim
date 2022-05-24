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
package de.gnwi.mfsim.model.job;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.DirectoryInformation;
import de.gnwi.mfsim.model.util.TimeUtilityMethods;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import java.io.File;
import java.io.StringReader;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Defines a job
 *
 * @author Achim Zielesny
 */
public class JobInput implements Comparable<JobInput> {

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
     * True: Job input has error, false: Otherwise
     */
    private boolean hasError;

    /**
     * True: Job input has compartments, false: Otherwise
     */
    private boolean hasCompartments;

    /**
     * True: Job input will overwrite existing information when saved, false:
     * otherwise (a new job input directory will be created when saved)
     */
    private boolean isOverwrite;

    /**
     * Full pathname of value item job container XML file
     */
    private String pathnameOfXmlValueItemContainerFile;

    /**
     * Full path of directory for this job input
     */
    private String jobInputPath;

    /**
     * Timestamp
     */
    private String timestamp;

    /**
     * Job input ID
     */
    private String jobInputId;

    /**
     * Value item job container
     */
    private ValueItemContainer valueItemContainer;

    /**
     * MFsim version information of this job input
     */
    private String mfSimVersionInformationOfJobInput;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     *
     * @param aValueItemContainer Value item job container
     * @throws IllegalArgumentException Thrown if argument is illegal or files
     * can not be created
     */
    public JobInput(ValueItemContainer aValueItemContainer) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            throw new IllegalArgumentException("aValueItemContainer is null.");
        }

        // </editor-fold>
        this.initialize();
        this.description = aValueItemContainer.getValueOfValueItem("Description");
        this.timestamp = aValueItemContainer.getValueOfValueItem("Timestamp");
        this.jobInputId = this.stringUtilityMethods.getGloballyUniqueID();
        this.valueItemContainer = aValueItemContainer;
        this.hasError = aValueItemContainer.hasError();
        this.hasCompartments = aValueItemContainer.hasValueItemWithCompartments();
        this.mfSimVersionInformationOfJobInput = ModelDefinitions.APPLICATION_VERSION;
    }

    /**
     * Constructor
     *
     * @param aValueItemContainer Value item job container
     * @param aJobInputPath Job input path (must be valid)
     * @param aTimestamp Timestamp
     * @param aJobInputId Job input ID
     * @throws IllegalArgumentException Thrown if argument is illegal or files
     * can not be created
     */
    public JobInput(ValueItemContainer aValueItemContainer, String aJobInputPath, String aTimestamp, String aJobInputId) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            throw new IllegalArgumentException("aValueItemContainer is null.");
        }
        if (aJobInputPath == null || aJobInputPath.isEmpty()) {
            throw new IllegalArgumentException("aJobInputPath is null/empty");
        }
        if (!(new File(aJobInputPath)).isDirectory()) {
            throw new IllegalArgumentException("No directory pathname: " + aJobInputPath);
        }
        if (aTimestamp == null || aTimestamp.isEmpty()) {
            throw new IllegalArgumentException("aTimestamp is null/empty.");
        }
        if (aJobInputId == null || aJobInputId.isEmpty()) {
            throw new IllegalArgumentException("aJobInputId is null/empty.");
        }
        // </editor-fold>
        this.initialize();
        // this.isOverwrite = true: Job input will overwrite existing information when saved
        this.isOverwrite = true;
        this.jobInputPath = aJobInputPath;
        this.description = aValueItemContainer.getValueOfValueItem("Description");
        this.timestamp = aTimestamp;
        this.jobInputId = aJobInputId;
        this.valueItemContainer = aValueItemContainer;
        this.hasError = aValueItemContainer.hasError();
        this.hasCompartments = aValueItemContainer.hasValueItemWithCompartments();
        this.mfSimVersionInformationOfJobInput = ModelDefinitions.APPLICATION_VERSION;
    }

    /**
     * Constructor
     *
     * @param aJobInputPath Full path of job input directory with job input
     * information
     * @param anIsOverwrite True: Job input will overwrite existing information
     * when saved, false: otherwise (then new job input directory will be
     * created when saved)
     * @throws IllegalArgumentException Thrown if argument is illegal or files
     * can not be read or do not exist
     */
    public JobInput(String aJobInputPath, boolean anIsOverwrite) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputPath == null || aJobInputPath.isEmpty()) {
            throw new IllegalArgumentException("aJobInputPath is null/empty");
        }
        if (!(new File(aJobInputPath)).isDirectory()) {
            throw new IllegalArgumentException("No directory pathname: " + aJobInputPath);
        }
        // </editor-fold>
        this.initialize();
        this.isOverwrite = anIsOverwrite;
        // <editor-fold defaultstate="collapsed" desc="Set/check job input related files">
        this.jobInputPath = aJobInputPath;
        this.pathnameOfXmlValueItemContainerFile = this.jobUtilityMethods.getInternalXmlJobInputFilePathname(this.jobInputPath);
        if (!(new File(this.pathnameOfXmlValueItemContainerFile)).isFile()) {
            throw new IllegalArgumentException("Value item job container XML file is not found: " + this.pathnameOfXmlValueItemContainerFile);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Read job input information file">
        if (!this.readJobInputInformation()) {
            throw new IllegalArgumentException("Could not read information for job input file.");
        }

        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- CompareTo method">
    /**
     * compareTo() method for Comparable interface. Compares
     * timestampExecutionEnd.
     *
     * @param anotherJob JobResult to compare
     * @return Standard compareTo-result
     */
    public int compareTo(JobInput anotherJob) {
        return this.timestamp.compareTo(anotherJob.getTimestamp());
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns total number of particles in simulation
     *
     * @return Total number of particles in simulation or -1 if total number of
     * particles can not be calculated
     */
    public int getTotalNumberOfParticlesInSimulation() {
        // NOTE: Do NOT use this.valueItemContainer because this variable may be null
        return this.jobUtilityMethods.getTotalNumberOfParticlesInSimulation(this.getValueItemContainer());
    }

    /**
     * Returns the total number of particles of the single molecule in
     * simulation which has the maximum total number of particles.
     *
     * @return Total number of particles of the single molecule in simulation
     * which has the maximum total number of particles or -1 if this quantity
     * can not be calculated
     */
    public int getMaximumNumberOfMoleculeParticles() {
        // NOTE: Do NOT use this.valueItemContainer because this variable may be null
        return this.jobUtilityMethods.getMaximumNumberOfMoleculeParticles(this.getValueItemContainer());
    }

    /**
     * Returns the number of forces of the single molecule (or protein)
     * in simulation which has the maximum number of forces.
     * NOTE: For a protein backbone and distance forces are added.
     *
     * @return Number of forces of the single molecule (or protein) in
     * simulation which has the maximum number of forces or -1 if
     * this quantity could not be calculated
     */
    public int getMaximumNumberOfForces() {
        // NOTE: Do NOT use this.valueItemContainer because this variable may be null
        return this.jobUtilityMethods.getMaximumNumberOfMoleculeAndProteinForces(this.getValueItemContainer());
    }

    /**
     * Returns maximum number of connections of a single particle in simulation
     *
     * @return Maximum number of connections of a single particle in simulation
     * or -1 if maximum number of connections of a single particle can not be
     * calculated
     */
    public int getMaximumNumberOfConnectionsOfSingleParticleInSimulation() {
        // NOTE: Do NOT use this.valueItemContainer because this variable may be null
        return this.jobUtilityMethods.getMaximumNumberOfConnectionsOfSingleParticleInSimulation(this.getValueItemContainer());
    }

    /**
     * Releases memory of value item container
     */
    public void releaseMemoryOfValueItemContainer() {
        this.valueItemContainer = null;
    }

    /**
     * Overrides toString() method and returns job description
     *
     * @return JobResult description
     */
    @Override
    public String toString() {
        String tmpParticleSetFilename = this.getParticleSetFilename();
        String tmpMFsimVersion = this.getMFsimApplicationVersion();
        if (tmpMFsimVersion == null) {
            tmpMFsimVersion = "none";
        }
        if (tmpParticleSetFilename != null) {
            if (this.hasError) {
                return String.format(ModelMessage.get("Format.JobInputDescriptionToStringWithErrorAndParticleSet"), 
                    this.description, 
                    this.timestamp, 
                    tmpParticleSetFilename, 
                    tmpMFsimVersion
                );
            } else {
                return String.format(ModelMessage.get("Format.JobInputDescriptionToStringWithParticleSet"), 
                    this.description, 
                    this.timestamp, 
                    tmpParticleSetFilename, 
                    tmpMFsimVersion
                );
            }
        } else {
            if (this.hasError) {
                return String.format(ModelMessage.get("Format.JobInputDescriptionToStringWithError"), 
                    this.description, 
                    this.timestamp
                );
            } else {
                return String.format(ModelMessage.get("Format.JobInputDescriptionToString"), 
                    this.description, 
                    this.timestamp
                );
            }
        }
    }

    /**
     * Returns particle set file name
     *
     * @return Particle set file name or null if none is available
     */
    public String getParticleSetFilename() {
        if (this.jobInputPath != null && !this.jobInputPath.isEmpty() && (new File(this.jobInputPath)).exists()) {
            String[] tmpParticleSetFilenames = this.fileUtilityMethods.getFilenamesWithPrefix(this.jobInputPath, ModelDefinitions.PARTICLE_SET_FILE_PREFIX);
            if (tmpParticleSetFilenames != null && tmpParticleSetFilenames.length == 1) {
                return tmpParticleSetFilenames[0];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * Returns particle set file pathname
     *
     * @return Particle set file pathname or null if none is available
     */
    public String getParticleSetFilePathname() {
        if (this.jobInputPath != null && !this.jobInputPath.isEmpty() && (new File(this.jobInputPath)).exists()) {
            String[] tmpParticleSetFilenames = this.fileUtilityMethods.getFilePathnamesWithPrefix(this.jobInputPath, ModelDefinitions.PARTICLE_SET_FILE_PREFIX);
            if (tmpParticleSetFilenames != null && tmpParticleSetFilenames.length == 1) {
                return tmpParticleSetFilenames[0];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Copies particle set file of this job input to aDirectoryPath.
     * NOTE: If particle set file already exists in aDirectoryPath false is returned.
     * 
     * @param aDestinationDirectoryPath Full path of directory (may be null or empty then
     * false is returned)
     * @return True: Particle set file was copied, false: Otherwise.
     */
    public boolean copyParticleSetFile(String aDestinationDirectoryPath) {
        try {
            return this.fileUtilityMethods.copySingleFileToDirectory(this.getParticleSetFilePathname(), aDestinationDirectoryPath);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Returns MFsim version this job input was designed with (e.g. "2.0.0.0")
     * 
     * @return MFsim version this job input was designed with or null if none is available
     */
    public String getMFsimApplicationVersion() {
        String[] tmpAllMFsimVersionTokens = this.stringUtilityMethods.getAllTokens(this.mfSimVersionInformationOfJobInput);
        if (tmpAllMFsimVersionTokens == null || tmpAllMFsimVersionTokens.length == 0) {
            return null;
        } else {
            return tmpAllMFsimVersionTokens[tmpAllMFsimVersionTokens.length - 1];
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Save method">
    /**
     * Saves job input to corresponding directory. NOTE: The current particle
     * set file is also copied to job input directory.
     *
     * @return True: Operation successful, false: Otherwise
     * @throws Exception Thrown if an internal error occurs
     */
    public boolean save() throws Exception {
        try {
            // <editor-fold defaultstate="collapsed" desc="Create job input path with old timestamp">
            String tmpOldJobInputPath = this.jobInputPath;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set jobInputPath and timestamp">
            if (this.jobInputPath == null) {
                if (this.isOverwrite) {
                    ModelUtils.appendToLogfile(true, "JobInput.save: this.jobInputPath is null and this.isOverwrite is true: This should never happen.");
                    return false;
                }
                // <editor-fold defaultstate="collapsed" desc="New jobInputPath">
                DirectoryInformation tmpDirectoryInformation = 
                    this.fileUtilityMethods.createUniqueDirectoryWithDateTimeEnding(Preferences.getInstance().getJobInputPath(),
                        ModelDefinitions.PREFIX_OF_JOB_INPUT_DIRECTORY
                    );
                if (tmpDirectoryInformation == null) {
                    ModelUtils.appendToLogfile(true, "JobInput.save: tmpDirectoryInformation could not be created: This should never happen.");
                    return false;
                }
                this.timestamp = tmpDirectoryInformation.getTimestamp();
                if (!this.getValueItemContainer().setValueOfValueItem("Timestamp", this.timestamp)) {
                    ModelUtils.appendToLogfile(true, "JobInput.save: setValueOfValueItem() could not be performed: This should never happen.");
                    return false;
                }
                this.jobInputPath = tmpDirectoryInformation.getDirectoryPath();
                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Clean job input directory if necessary">
            if (this.isOverwrite) {
                // Rename job input directory with old timestamp
                if (tmpOldJobInputPath != null && (new File(tmpOldJobInputPath)).isDirectory()) {
                    if (!this.fileUtilityMethods.renameDirectory(tmpOldJobInputPath, ModelDefinitions.PREFIX_OF_REMOVED_DIRECTORIES + (new File(tmpOldJobInputPath).getName()))) {
                        ModelUtils.appendToLogfile(true, "JobInput.save: Rename directory could not be performed: This should never happen.");
                        return false;
                    }
                }
                // Create job input directory
                if (!ModelUtils.createDirectory(this.jobInputPath)) {
                    ModelUtils.appendToLogfile(true, "JobInput.save: this.jobInputPath could not be created: This should never happen.");
                    return false;
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Write job input information file">
            // Update description, timestamp, hasError and hasCompartments
            this.description = this.getValueItemContainer().getValueOfValueItem("Description");
            this.hasError = this.getValueItemContainer().hasError();
            this.hasCompartments = this.getValueItemContainer().hasValueItemWithCompartments();
            if (!this.writeJobInputInformation()) {
                ModelUtils.appendToLogfile(true, "JobInput.save: this.writeJobInputInformation() could not be performed: This should never happen.");
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Write job input related files">
            if (this.pathnameOfXmlValueItemContainerFile == null) {
                this.pathnameOfXmlValueItemContainerFile = this.jobUtilityMethods.getInternalXmlJobInputFilePathname(this.jobInputPath);
            }
            // NOTE: Method export() will set this.valueItemContainer to null
            if (!this.exportJobInput(this.pathnameOfXmlValueItemContainerFile)) {
                ModelUtils.appendToLogfile(true, "JobInput.save: this.exportJobInput() could not be performed: This should never happen.");
                return false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Copy current particle set file to Job Input directory">
            // Do only overwrite particle set file if particle update for job input is activated!
            if (Preferences.getInstance().isParticleUpdateForJobInput()) {
                // First remove old particle set file ...
                if (!this.fileUtilityMethods.deleteAllFilesWithPrefix(this.jobInputPath, ModelDefinitions.PARTICLE_SET_FILE_PREFIX)) {
                    ModelUtils.appendToLogfile(true, "JobInput.save: this.fileUtilityMethods.deleteAllFilesWithPrefix() could not be performed: This should never happen.");
                    return false;
                }
                // ... then copy current one:
                if (!this.fileUtilityMethods.copySingleFileToDirectory(Preferences.getInstance().getCurrentParticleSetFilePathname(), this.jobInputPath)) {
                    ModelUtils.appendToLogfile(true, "JobInput.save: this.fileUtilityMethods.copySingleFileToDirectory() could not be performed: This should never happen.");
                    return false;
                }
            } else {
                // Check if particle set file already exists ...
                if (!this.fileUtilityMethods.hasFilenameWithPrefix(this.jobInputPath, ModelDefinitions.PARTICLE_SET_FILE_PREFIX)) {
                    // ... and copy current one if not:
                    if (!this.fileUtilityMethods.copySingleFileToDirectory(Preferences.getInstance().getCurrentParticleSetFilePathname(), this.jobInputPath)) {
                        ModelUtils.appendToLogfile(true, "JobInput.save: this.fileUtilityMethods.copySingleFileToDirectory() could not be performed: This should never happen.");
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
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    // <editor-fold defaultstate="collapsed" desc="Description (get)">
    /**
     * Description of job
     *
     * @return JobResult description
     */
    public String getDescription() {
        return this.description;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="HasError (get)">
    /**
     * Returns if job input has error
     *
     * @return True: Job input has error, false: Otherwise
     */
    public boolean hasError() {
        return this.hasError;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="HasCompartments (get)">
    /**
     * Returns if job input has compartments
     *
     * @return True: Job input has compartments, false: Otherwise
     */
    public boolean hasCompartments() {
        return this.hasCompartments;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="IsSaved (get)">
    /**
     * Returns if job input is already saved
     *
     * @return True: Job input is already saved, false: Otherwise
     */
    public boolean isSaved() {
        // If this.valueItemContainer is not null then job input is not already saved
        return this.valueItemContainer == null;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="JobInputPath (get)">
    /**
     * Full path of directory for this job input
     *
     * @return Full path of directory for this job input
     */
    public String getJobInputPath() {
        return this.jobInputPath;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Timestamp (get)">
    /**
     * Timestamp
     *
     * @return Timestamp
     */
    public String getTimestamp() {
        return this.timestamp;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="JobInputId (get)">
    /**
     * Job input ID
     *
     * @return Job input ID
     */
    public String getJobInputId() {
        return this.jobInputId;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="ValueItemContainer (get)">
    /**
     * Value item job container
     *
     * @return Value item job container or null if job container could not be
     * constructed
     */
    public ValueItemContainer getValueItemContainer() {
        try {
            if (this.valueItemContainer == null) {
                if (!this.importJobInput(this.pathnameOfXmlValueItemContainerFile)) {
                    return null;
                }
            }
            return this.valueItemContainer;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Initialize method">
    /**
     * Initialises class variables
     */
    private void initialize() {
        // this.isOverwrite = false: A new job input directory will be created when saved
        this.isOverwrite = false;
        this.description = null;
        this.pathnameOfXmlValueItemContainerFile = null;
        this.jobInputPath = null;
        this.timestamp = null;
        this.jobInputId = null;
        this.valueItemContainer = null;
        this.hasError = false;
        this.hasCompartments = false;
        this.mfSimVersionInformationOfJobInput = null;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- JobInput import/export related methods">
    /**
     * Exports this job input for import of another MFsim running
     * device.
     *
     * @param aDestinationFilePathname Full pathname of destination (may be null
     * then false is returned)
     * @return True: Operation successful, false: Otherwise
     */
    private boolean exportJobInput(String aDestinationFilePathname) {
        try {
            // <editor-fold defaultstate="collapsed" desc="Create export file">
            Element tmpRoot = new Element(JobInputXmlName.JOB_INPUT);
            // IMPORTANT: Set version of this XML definition
            tmpRoot.addContent(new Element(JobInputXmlName.VERSION).addContent("Version 1.0.0"));
            tmpRoot.addContent(new Element(JobInputXmlName.INFORMATION).addContent(ModelDefinitions.JOB_EXPORT_INFORMATION));
            tmpRoot.addContent(new Element(JobInputXmlName.IDENTIFICATION).addContent(ModelDefinitions.JOB_EXPORT_IDENTIFICATION));
            // NOTE: Value item container XML representation is compressed
            tmpRoot.addContent(new Element(JobInputXmlName.VALUE_ITEM_CONTAINER).addContent(this.stringUtilityMethods.compressIntoBase64String(this.getValueItemContainer().getAsXmlString())));
            // tmpRoot.addContent(new Element(JobInputXmlName.VALUE_ITEM_CONTAINER).addContent(this.getValueItemContainer().getAsXmlString()));
            XMLOutputter tmpOutputter = new XMLOutputter(Format.getPrettyFormat());
            Document tmpDocument = new Document();
            tmpDocument.setRootElement(tmpRoot);
            boolean tmpIsWriteSuccess = true;
            if (!this.fileUtilityMethods.writeSingleStringToTextFile(tmpOutputter.outputString(tmpDocument), aDestinationFilePathname)) {
                tmpIsWriteSuccess = false;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="IMPORTANT: Release memory of value item job container">
            this.valueItemContainer = null;
            // </editor-fold>
            return tmpIsWriteSuccess;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Imports job input from file
     *
     * @param aJobImportFilePathname File pathname for job import
     * @return True: Job input has successfully been imported, false: Otherwise
     */
    private boolean importJobInput(String aJobImportFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobImportFilePathname == null || aJobImportFilePathname.isEmpty()) {
            return false;
        }
        if (!(new File(aJobImportFilePathname)).isFile()) {
            return false;
        }

        // </editor-fold>
        String tmpXmlString = this.fileUtilityMethods.readTextFileIntoSingleString(aJobImportFilePathname);
        if (tmpXmlString == null || tmpXmlString.isEmpty()) {
            return false;
        }
        try {
            if (!this.readXmlInformation(new SAXBuilder().build(new StringReader(tmpXmlString)).getRootElement())) {
                return false;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
        return true;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- JobInput information related read/write methods">
    /**
     * Reads job input information (see code)
     *
     * @return true: Operation was successful, false: Otherwise
     */
    private boolean readJobInputInformation() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        String tmpJobInformationPathname = this.jobUtilityMethods.getJobInputInformationFilePathname(this.jobInputPath);
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
            // Line 3: this.timestamp
            // Line 4: this.jobInputId
            // Line 5: this.hasError
            // Line 6: this.hasCompartments
            if (infos[0].equals("Version 1.0.0")) {
                // <editor-fold defaultstate="collapsed" desc="Line 2 - this.description">
                if (infos.length > 1) {
                    this.description = infos[1];
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 3 - this.timestamp">
                if (infos.length > 2) {
                    this.timestamp = infos[2];
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 4 - this.jobInputId">
                if (infos.length > 3) {
                    this.jobInputId = infos[3];
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 5 - this.hasError">
                if (infos.length > 4) {
                    this.hasError = Boolean.valueOf(infos[4]);
                } else {
                    return false;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 6 - this.hasCompartments">
                if (infos.length > 5) {
                    this.hasCompartments = Boolean.valueOf(infos[5]);
                } else {
                    return false;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Line 7 - this.mfSimVersionOfJobInput">
                if (infos.length > 6) {
                    this.mfSimVersionInformationOfJobInput = infos[6];
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
     * Writes job input information
     *
     * @return true: Operation was successful, false: Otherwise
     */
    private boolean writeJobInputInformation() {
        String tmpJobInformationPathname = this.jobUtilityMethods.getJobInputInformationFilePathname(this.jobInputPath);
        // Delete job information file if necessary
        if (!this.fileUtilityMethods.deleteSingleFile(tmpJobInformationPathname)) {
            return false;
        }
        // Write to file:
        // Line 1: Version
        // Line 2: this.description
        // Line 3: this.timestamp
        // Line 4: this.jobInputId
        // Line 5: this.hasError
        // Line 6: this.hasCompartments
        // Line 7: MFsim version
        String[] infos = new String[]{
            "Version 1.0.0",
            this.description,
            this.timestamp,
            this.jobInputId,
            String.valueOf(this.hasError),
            String.valueOf(this.hasCompartments),
            String.format(ModelMessage.get("ApplicationVersionFormat"), ModelDefinitions.APPLICATION_VERSION)
        };
        return this.fileUtilityMethods.writeDefinedStringArrayToFile(infos, tmpJobInformationPathname);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- XML related methods">
    /**
     * Reads XML information for this instance
     *
     * @param anElement XML element
     * @return True: Operation successful, false: Otherwise
     */
    private boolean readXmlInformation(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return false;
        }
        if (anElement.getChild(JobInputXmlName.VERSION) == null) {
            return false;
        }

        // </editor-fold>
        String tmpVersion = anElement.getChild(JobInputXmlName.VERSION).getText();
        if (tmpVersion == null || tmpVersion.isEmpty()) {
            return false;
        }
        // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
        if (tmpVersion.equals("Version 1.0.0")) {
            return this.readXmlInformationV_1_0_0(anElement);
        }

        // </editor-fold>
        return false;
    }

    /**
     * Reads versioned XML information for this instance
     *
     * @param anElement XML element
     * @return True: Operation successful, false: Otherwise
     */
    private boolean readXmlInformationV_1_0_0(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            throw new IllegalArgumentException("XML element is null.");
        }
        if (anElement.getChild(JobInputXmlName.IDENTIFICATION) == null) {
            return false;
        }

        // </editor-fold>
        try {
            if (!anElement.getChild(JobInputXmlName.IDENTIFICATION).getText().equals(ModelDefinitions.JOB_EXPORT_IDENTIFICATION)) {
                return false;
            }
            try {
                // NOTE: Value item container XML representation is compressed
                this.valueItemContainer = new ValueItemContainer(this.stringUtilityMethods.decompressBase64String(anElement.getChild(JobInputXmlName.VALUE_ITEM_CONTAINER).getText()), ModelDefinitions.JOB_UPDATE_UTILS);
                // this.valueItemContainer = new ValueItemContainer(anElement.getChild(JobInputXmlName.VALUE_ITEM_CONTAINER).getText(), ModelDefinitions.JOB_UPDATE_UTILS);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                return false;
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
