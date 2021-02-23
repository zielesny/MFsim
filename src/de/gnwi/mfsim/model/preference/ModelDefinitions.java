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
package de.gnwi.mfsim.model.preference;

import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.job.JobUpdateUtils;
import de.gnwi.mfsim.model.util.ImageStorageEnum;
import de.gnwi.mfsim.model.util.ParticleColorDisplayEnum;
import de.gnwi.mfsim.model.util.StandardColorEnum;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.io.File;
import java.util.regex.Pattern;

/**
 * Global basic definitions
 *
 * @author Achim Zielesny
 */
public interface ModelDefinitions {

    // <editor-fold defaultstate="collapsed" desc="General definitions">
    // <editor-fold defaultstate="collapsed" desc="- Application version">
    /**
     * Version of application. NOTE: MUST match version pattern below!
     */
    String APPLICATION_VERSION = "2.3.0.0";

    /**
     * Minimum job input application version. NOTE: MUST match version pattern below!
     */
    String MINIMUM_JOB_INPUT_APPLICATION_VERSION = "1.7.5.1";

    /**
     * Minimum job result application version. NOTE: MUST match version pattern below!
     */
    String MINIMUM_JOB_RESULT_APPLICATION_VERSION = "1.1.0.4";
    
    /**
     * Version pattern
     */
    Pattern VERSION_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- DPD data related and internal definitions">
    // <editor-fold defaultstate="collapsed" desc="-- Directories">
    // <editor-fold defaultstate="collapsed" desc="--- Jdpd directories">
    /**
     * Input directory for Jdpd in Job Result directory
     */
    String JDPD_INPUT_DIRECTORY = "Jdpd_In";

    /**
     * Output directory for Jdpd in Job Result directory
     */
    String JDPD_OUTPUT_DIRECTORY = "Jdpd_Out";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="--- Job Result directories">
    /**
     * Steps directory for Jdpd in Job Result directory
     */
    String JDPD_STEPS_DIRECTORY = "Jdpd_Steps";

    /**
     * Minimization steps directory for Jdpd in Job Result directory
     */
    String JDPD_MINIMIZATION_STEPS_DIRECTORY = "Jdpd_MinSteps";

    /**
     * Radius-of-gyration directory for Jdpd in Job Result directory
     */
    String JDPD_RADIUS_OF_GYRATION_DIRECTORY = "Jdpd_Rg";

    /**
     * Nearest-neighbor directory for Jdpd in Job Result directory
     */
    String JDPD_NEAREST_NEIGHBOR_DIRECTORY = "Jdpd_NN";

    /**
     * Particle-pair RDF directory in Job Result directory
     */
    String JOB_RESULT_PARTICLE_PAIR_RDF_DIRECTORY = "PP_RDF";

    /**
     * Molecule-particle RDF directory in Job Result directory
     */
    String JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_RDF_DIRECTORY = "PIMP_RDF";

    /**
     * Particle-pair distance directory in Job Result directory
     */
    String JOB_RESULT_PARTICLE_PAIR_DISTANCE_DIRECTORY = "PP_Distance";

    /**
     * Molecule-particle distance directory in Job Result directory
     */
    String JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_DISTANCE_DIRECTORY = "PIMP_Distance";

    /**
     * Job input history directory in Job Result directory
     */
    String JOB_RESULT_HISTORY_DIRECTORY = "IHistory";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="--- Internal directories">
    /**
     * Directory name for MFsim source
     */
    String MFSIM_SOURCE_DIRECTORY = "MFsim_Source";

    /**
     * Directory name for MFsim data
     */
    String MFSIM_DATA_DIRECTORY = "MFsim_Data";

    /**
     * Installation directory for particle related data
     */
    String MFSIM_SOURCE_PARTICLES_DIRECTORY = ModelDefinitions.MFSIM_SOURCE_DIRECTORY + File.separatorChar + "particles";

    /**
     * Installation directory for Windows OS utilities
     */
    String MFSIM_SOURCE_WINDOWS_UTILITIES_DIRECTORY = ModelDefinitions.MFSIM_SOURCE_DIRECTORY + File.separatorChar + "winUtils";

    /**
     * Installation directory for tutorials
     */
    String MFSIM_SOURCE_TUTORIALS_DIRECTORY = ModelDefinitions.MFSIM_SOURCE_DIRECTORY + File.separatorChar + "tutorials";

    /**
     * Info directory
     */
    String MFSIM_SOURCE_INFO_DIRECTORY = ModelDefinitions.MFSIM_SOURCE_DIRECTORY + File.separatorChar + "info";

    /**
     * Directory name for data of custom particle sets
     */
    String CUSTOM_PARTICLES_DIRECTORY = "CustomParticles";

    /**
     * Directory name for job inputs
     */
    String JOB_INPUT_DIRECTORY = "JobInputs";

    /**
     * Directory name for job results
     */
    String JOB_RESULT_DIRECTORY = "JobResults";

    /**
     * Directory name for temporary files
     */
    String TEMPORARY_DIRECTORY = "Temp";
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Directory prefixes">
    /**
     * Prefix of job input directory
     */
    String PREFIX_OF_JOB_INPUT_DIRECTORY = "I_";

    /**
     * Prefix of process directory in Jdpd workspace
     */
    String PREFIX_OF_JOB_RESULT_DIRECTORY = "R_";

    /**
     * Prefix for removed directories
     */
    String PREFIX_OF_REMOVED_DIRECTORIES = "REMOVED_";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Filenames">
    // <editor-fold defaultstate="collapsed" desc="--- Jdpd filenames">
    /**
     * File name of Jdpd input file for job input
     */
    String JDPD_INPUT_FILENAME = "Input.txt";

    /**
     * File name of Jdpd log file
     */
    String JDPD_LOG_FILENAME = "LogFile.txt";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="--- Internal filenames">
    /**
     * File name for basic preferences of MFsim
     */
    String BASIC_PREFERENCES_FILENAME = "BasicPreferences.xml";

    /**
     * File name of job input information file
     */
    String JOB_INPUT_INFORMATION_FILENAME = "JobInputInformation.txt";

    /**
     * File name of job result information file
     */
    String JOB_RESULT_INFO_FILENAME = "JobResultInfo.txt";

    /**
     * File name of compressed XML file with value item container information of
     * job input
     */
    String INTERNAL_XML_JOB_INPUT_VALUE_ITEM_CONTAINER_FILENAME = "JobInputInternal.xml";

    /**
     * Name of log file for MFsim
     */
    String LOGFILE_NAME = "MFsim_Logfile.txt";

    /**
     * Single instance file name
     */
    String SINGLE_INSTANCE_FILE_NAME = "MFsim_SingleInstance.txt";

    /**
     * Filename of FFmpeg
     */
    String FFMPEG_FILE_NAME = "FFmpeg.exe";

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Filename prefixes">
    /**
     * Prefix of job input history file
     */
    String JOB_INPUT_HISTORY_PREFIX = "JobInputHistory_";

    /**
     * Prefix of JDPD file with positions and bonds of molecular particles
     */
    String JDPD_POSITIONS_BONDS_FILE_PREFIX = "PositionsBonds";

    /**
     * Prefix of particle-pair radial distribution function (RDF) file
     */
    String JOB_RESULT_PARTICLE_PAIR_RDF_FILE_PREFIX = "ParticlePairRdf_";

    /**
     * Prefix of molecule-particle-pair radial distribution function (RDF)
     * file
     */
    String JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_RDF_FILE_PREFIX = "MoleculeParticlePairRdf_";

    /**
     * Prefix of particle-pair distance file
     */
    String JOB_RESULT_PARTICLE_PAIR_DISTANCE_FILE_PREFIX = "ParticlePairDistance_";

    /**
     * Prefix of molecule-particle-pair distance file
     */
    String JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_DISTANCE_FILE_PREFIX = "MoleculeParticlePairDistance_";

    /**
     * Prefix of particle-pair average distances for specified step file
     */
    String JOB_RESULT_PARTICLE_PAIR_AVERAGE_DISTANCE_FOR_STEP_FILE_PREFIX = "ParticlePairAverageDistanceForStep_";

    /**
     * Prefix of molecule-particle-pair average distances for specified step
     * file
     */
    String JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_AVERAGE_DISTANCE_FOR_STEP_FILE_PREFIX = "MoleculeParticlePairAverageDistanceForStep_";

    /**
     * File name prefix for particle set file
     */
    String PARTICLE_SET_FILE_PREFIX = "ParticleSet";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Particle Information sections">
    /**
     * Title section tag of particle information
     */
    String TITLE_SECTION_TAG = "Title";

    /**
     * Version section tag of particle information
     */
    String VERSION_SECTION_TAG = "Version";

    /**
     * Particle description section tag of particle information
     */
    String PARTICLE_DESCRIPTION_SECTION_TAG = "Particle description";

    /**
     * Particle description section tag of particle information
     */
    String PARTICLE_INTERACTIONS_SECTION_TAG = "Particle interactions";

    /**
     * Amino acid description section tag of particle information
     */
    String AMINO_ACID_DESCRIPTION_SECTION_TAG = "Amino acids";

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Other definitions">
    /**
     * Format for date and time postfix for directories
     */
    String DIRECTORY_ENDING_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    /**
     * Job export information
     */
    String JOB_EXPORT_INFORMATION = "MFsim job file";

    /**
     * Job export identification
     */
    String JOB_EXPORT_IDENTIFICATION = "zFi6b6DG90aetddYVAFv6Q";
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Other directory definitions">
    /**
     * Image directory for movies
     */
    String IMAGE_DIRECTORY_FOR_MOVIES = "Images";

    /**
     * Movie directory for movies
     */
    String MOVIE_DIRECTORY_FOR_MOVIES = "Movies";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Other file name definitions">
    /**
     * Movie directory for movies
     */
    String MOVIE_IMAGE_DIMENSION_DATA_FILENAME = "ImageDimensionData.txt";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Frame/Dialog related definitions">
    // NOTE: MFsim should run on 1280 (width) x 1024 (height) resolution 
    /**
     * Minimum width of dialog
     */
    int MINIMUM_DIALOG_WIDTH = 1050;

    /**
     * Minimum height of dialog
     */
    int MINIMUM_DIALOG_HEIGHT = 890;

    /**
     * Minimum width of frame
     */
    int MINIMUM_FRAME_WIDTH = 820;

    /**
     * Minimum height of frame
     */
    int MINIMUM_FRAME_HEIGHT = 730;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Particle related definitions">
    /**
     * Regex string for single particle to match
     */
    String PARTICLE_REGEX_PATTERN_STRING = "^[A-Z][A-Za-z0-9]{0,9}$";

    /**
     * Regex string for single particle name to match
     */
    String PARTICLE_NAME_REGEX_PATTERN_STRING = "^[A-Z][A-Za-z0-9]{0,24}$";

    /**
     * Regex string for protein backbone probe particles to match
     */
    String PARTICLE_PROTEIN_BACKBONE_PROBE_PATTERN_STRING = "^[A-Z][A-Za-z0-9]*PD[0-9]+$";

    /**
     * Regex pattern for protein backbone probe particles to match and probe
     * part to be captured
     */
    Pattern PARTICLE_PROTEIN_BACKBONE_PROBE_CAPTURING_PATTERN = Pattern.compile("^[A-Z][A-Za-z0-9]*(PD[0-9]+)$");

    /**
     * Regex pattern for protein backbone probe particles to match and particle
     * plus probe plus number part to be captured: Group 1 = Particle part,
     * Group 2 = PD, Group 3 = Number.
     */
    Pattern PARTICLE_PROTEIN_BACKBONE_PROBE_NUMBER_CAPTURING_PATTERN = Pattern.compile("^([A-Z][A-Za-z0-9]*)(PD)([0-9]+)$");

    /**
     * Regex string for protein backbone probe particles ending to match
     */
    String PARTICLE_PROTEIN_BACKBONE_PROBE_PATTERN_ENDING_STRING = "PD[0-9]+$";

    /**
     * Start of general protein backbone probe
     */
    String GENERAL_PROTEIN_BACKBONE_PROBE_START = "*";

    /**
     * Regex string for protein backbone particles to match
     */
    String PARTICLE_PROTEIN_BACKBONE_PARTICLE_PATTERN_STRING = "^[A-Z][A-Za-z0-9]*BB$";

    /**
     * Regex string for allowed characters of particles
     */
    String PARTICLE_ALLOWED_CHARACTERS_REGEX_STRING = "[a-zA-Z0-9]";

    /**
     * Default value for minimum bond length in DPD units
     */
    double MINIMUM_BOND_LENGTH_DPD_DEFAULT = 1.0;

    /**
     * Minimum value for minimum bond length in DPD units
     */
    double MINIMUM_BOND_LENGTH_DPD_MINIMUM = 0.0;

    /**
     * Maximum value for minimum bond length in DPD units
     */
    double MINIMUM_BOND_LENGTH_DPD_MAXIMUM = 1.0;

    /**
     * Minimum number of particles for display
     */
    int MINIMUM_NUMBER_OF_PARTICLES_FOR_DISPLAY = 10;

    /**
     * Default number of particles for display
     */
    int DEFAULT_NUMBER_OF_PARTICLES_FOR_DISPLAY = 100;

    /**
     * Minimum number of steps for RDF calculation
     */
    int MINIMUM_NUMBER_OF_STEPS_FOR_RDF_CALCULATION = 1;

    /**
     * Default number of steps for RDF calculation
     */
    int DEFAULT_NUMBER_OF_STEPS_FOR_RDF_CALCULATION = 1;

    /**
     * Minimum number of trials for compartment related calculations
     */
    int MINIMUM_NUMBER_OF_TRIALS_FOR_COMPARTMENT = 1;

    /**
     * Default number of trials for compartment related calculations
     */
    int DEFAULT_NUMBER_OF_TRIALS_FOR_COMPARTMENT = 100;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Default values">
    // <editor-fold defaultstate="collapsed" desc="-- Animation speed">
    /**
     * Minimum animation speed in pictures per second
     */
    int MINIMUM_ANIMATION_SPEED = 1;

    /**
     * Default animation speed in pictures per second (20 is a film)
     */
    int DEFAULT_ANIMATION_SPEED = 20;

    /**
     * Maximum animation speed in pictures per second (10 times accelerated
     * film)
     */
    int MAXIMUM_ANIMATION_SPEED = 200;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Number of steps for Job restart">
    /**
     * Minimum number of steps for Job restart
     */
    int MINIMUM_NUMBER_OF_STEPS_FOR_JOB_RESTART = 10;

    /**
     * Default number of steps for Job restart
     */
    int DEFAULT_NUMBER_OF_STEPS_FOR_JOB_RESTART = 1000;

    /**
     * Maximum number of steps for Job restart
     */
    int MAXIMUM_NUMBER_OF_ADDITIONAL_STEPS_FOR_JOB_RESTART = 100000000;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Delay for file operations in milliseconds">
    /**
     * Minimum delay in milliseconds for directory/file delete operations: This
     * is necessary due to OS specific relations between files and directories
     * to be deleted: 10 Milliseconds works on WinXP OS. A value less than or
     * equal to zero leads means no delay.
     */
    long MINIMUM_DELAY_FOR_FILES_IN_MILLISECONDS = 0L;

    /**
     * Delay in milliseconds for directory/file delete operations: This is
     * necessary due to OS specific relations between files and directories to
     * be deleted: 10 Milliseconds works on WinXP OS. A value less than or equal
     * to zero leads means no delay.
     */
    long DEFAULT_DELAY_FOR_FILES_IN_MILLISECONDS = 1L;

    /**
     * Maximum delay in milliseconds for directory/file delete operations: This
     * is necessary due to OS specific relations between files and directories
     * to be deleted: 10 Milliseconds works on WinXP OS. A value less than or
     * equal to zero leads means no delay.
     */
    long MAXIMUM_DELAY_FOR_FILES_IN_MILLISECONDS = 100L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Delay for job start in milliseconds">
    /**
     * Minimum delay for job start in milliseconds: A value less than or equal
     * to zero means no delay.
     */
    long MINIMUM_DELAY_FOR_JOB_START_IN_MILLISECONDS = 0L;

    /**
     * Default delay for job start in milliseconds: A value less than or equal
     * to zero means no delay.
     */
    long DEFAULT_DELAY_FOR_JOB_START_IN_MILLISECONDS = 1000L;

    /**
     * Maximum delay for job start in milliseconds: A value less than or equal
     * to zero means no delay.
     */
    long MAXIMUM_DELAY_FOR_JOB_START_IN_MILLISECONDS = 60000L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Timer interval in milliseconds">
    /**
     * Minimum timer interval in milliseconds
     */
    int MINIMUM_TIMER_INTERVAL_IN_MILLISECONDS = 100;

    /**
     * Timer interval in milliseconds
     */
    int DEFAULT_TIMER_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * Maximum timer interval in milliseconds
     */
    int MAXIMUM_TIMER_INTERVAL_IN_MILLISECONDS = 10000;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Simulation box slicer">
    /**
     * Default background color of simulation box for slicer
     */
    StandardColorEnum DEFAULT_SIMULATION_BOX_BACKGROUND_COLOR_SLICER = StandardColorEnum.BLACK;

    /**
     * Default measurement color for slicer
     */
    StandardColorEnum DEFAULT_MEASUREMENT_COLOR_SLICER = StandardColorEnum.WHITE;

    /**
     * Default molecule selection color for slicer
     */
    StandardColorEnum DEFAULT_MOLECULE_SELECTION_COLOR_SLICER = StandardColorEnum.YELLOW;

    /**
     * Default frame color for slicer
     */
    StandardColorEnum DEFAULT_FRAME_COLOR_SLICER = StandardColorEnum.BEIGE;

    /**
     * Default background color of simulation box for Jmol viewer
     */
    StandardColorEnum DEFAULT_JMOL_SIMULATION_BOX_BACKGROUND_COLOR = StandardColorEnum.BLACK;

    /**
     * Default background color of protein viewer
     */
    StandardColorEnum DEFAULT_PROTEIN_VIEWER_BACKGROUND_COLOR = StandardColorEnum.BLACK;

    /**
     * Default display: True: A slice only displays its corresponding particles,
     * false: A slice also displays the slices behind it.
     */
    boolean DEFAULT_SINGLE_SLICE_DISPLAY = false;

    /**
     * Default display: True: Time step image is displayed for preview during
     * slice creation, false: Otherwise
     */
    boolean DEFAULT_TIME_STEP_IMAGE_PREVIEW_SLICER = true;

    /**
     * Default image storage mode
     */
    ImageStorageEnum DEFAULT_IMAGE_STORAGE_MODE = ImageStorageEnum.MEMORY_UNCOMPRESSED;

    /**
     * Default particle color display mode
     */
    ParticleColorDisplayEnum DEFAULT_PARTICLE_COLOR_DISPLAY_MODE = ParticleColorDisplayEnum.MOLECULE_COLOR_MODE;

    /**
     * Default display: True: Only XZ front simulation box view is displayed,
     * false: All simulation box views are displayed.
     */
    SimulationBoxViewEnum DEFAULT_BOX_VIEW_DISPLAY = SimulationBoxViewEnum.XZ_FRONT;

    /**
     * Default frame display of slicer: True: Simulation box frame is displayed,
     * false: Otherwise
     */
    boolean DEFAULT_FRAME_DISPLAY_SLICER = true;

    /**
     * Default number of slices for simulation box slicer per view
     */
    int DEFAULT_NUMBER_OF_SLICES = 100;

    /**
     * Default index of first slice for simulation box slicer
     */
    int DEFAULT_FIRST_SLICE_INDEX = 0;

    /**
     * Default number of simulation box slicer wait steps
     */
    int DEFAULT_NUMBER_OF_BOX_WAIT_STEPS = 0;

    /**
     * Minimum number of simulation box slicer wait steps
     */
    int MINIMUM_NUMBER_OF_BOX_WAIT_STEPS = 0;

    /**
     * Default number of frame points for simulation box slicer
     */
    int DEFAULT_NUMBER_OF_FRAME_POINTS_SLICER = 1000;

    /**
     * Minimum number of frame points for simulation box slicer
     */
    int MINIMUM_NUMBER_OF_FRAME_POINTS_SLICER = 2;

    /**
     * Default time step display for simulation box slicer
     */
    int DEFAULT_TIME_STEP_DISPLAY_SLICER = 1;

    /**
     * Minimum time step display for simulation box slicer: This MUST be 1
     * (since a number of methods assume this implicitly)
     */
    int MINIMUM_TIME_STEP_DISPLAY_SLICER = 1;

    /**
     * Maximum time step display for simulation box slicer
     */
    int MAXIMUM_TIME_STEP_DISPLAY_SLICER = 100;

    /**
     * Minimum number of slices for simulation box slicer per view
     */
    int MINIMUM_NUMBER_OF_SLICES = 10;

    /**
     * Maximum number of slices for simulation box slicer per view. NOTE: This
     * value must correspond to NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES (see
     * explanation there).
     */
    int MAXIMUM_NUMBER_OF_SLICES = 1000;

    /**
     * Default simulation box magnification percentage
     */
    int DEFAULT_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE = -3;

    /**
     * Minimum simulation box magnification percentage
     */
    int MINIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE = -500;

    /**
     * Maximum simulation box magnification percentage. NOTE: MUST be smaller
     * than 100 (see method getEnlargedBoxSizeInfo() in class BoxSizeInfo), a
     * value greater than 95 leads to extremely long rendering periods.
     */
    int MAXIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE = 95;

    /**
     * Default number of spin steps
     */
    int DEFAULT_NUMBER_OF_SPIN_STEPS = 180;

    /**
     * Absolute minimum number of spin steps
     */
    int ABSOLUTE_MINIMUM_NUMBER_OF_SPIN_STEPS = 20;

    /**
     * Minimum number of spin steps
     */
    int MINIMUM_NUMBER_OF_SPIN_STEPS = -36000;

    /**
     * Maximum number of spin steps
     */
    int MAXIMUM_NUMBER_OF_SPIN_STEPS = 36000;

    /**
     * Minimum number of time steps
     */
    int MINIMUM_NUMBER_OF_TIME_STEPS = 5;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- JMol simulation box viewer">
    /**
     * Default shade power (integer values 1, 2, 3) for Jmol viewer
     */
    int DEFAULT_JMOL_SHADE_POWER = 2;

    /**
     * Minimum shade power (integer values 1, 2, 3) for Jmol viewer
     */
    int MINIMUM_JMOL_SHADE_POWER = 1;

    /**
     * Maximum shade power (integer values 1, 2, 3) for Jmol viewer
     */
    int MAXIMUM_JMOL_SHADE_POWER = 3;

    /**
     * Default ambient light percentage (range 0 - 100) for Jmol viewer
     */
    int DEFAULT_JMOL_AMBIENT_LIGHT_PERCENTAGE = 50;

    /**
     * Minimum ambient light percentage (range 0 - 100) for Jmol viewer
     */
    int MINIMUM_JMOL_AMBIENT_LIGHT_PERCENTAGE = 0;

    /**
     * Maximum ambient light percentage (range 0 - 100) for Jmol viewer
     */
    int MAXIMUM_JMOL_AMBIENT_LIGHT_PERCENTAGE = 100;

    /**
     * Default diffuse light percentage (range 0 - 100) for Jmol viewer
     */
    int DEFAULT_JMOL_DIFFUSE_LIGHT_PERCENTAGE = 80;

    /**
     * Minimum diffuse light percentage (range 0 - 100) for Jmol viewer
     */
    int MINIMUM_JMOL_DIFFUSE_LIGHT_PERCENTAGE = 0;

    /**
     * Maximum diffuse light percentage (range 0 - 100) for Jmol viewer
     */
    int MAXIMUM_JMOL_DIFFUSE_LIGHT_PERCENTAGE = 100;

    /**
     * Default specular reflection exponent (range 0 - 10) for Jmol viewer
     */
    int DEFAULT_JMOL_SPECULAR_REFLECTION_EXPONENT = 10;

    /**
     * Minimum specular reflection exponent (range 0 - 10) for Jmol viewer
     */
    int MINIMUM_JMOL_SPECULAR_REFLECTION_EXPONENT = 1;

    /**
     * Maximum specular reflection exponent (range 0 - 10) for Jmol viewer
     */
    int MAXIMUM_JMOL_SPECULAR_REFLECTION_EXPONENT = 10;

    /**
     * Default specular reflection percentage (range 0 - 100) for Jmol viewer
     */
    int DEFAULT_JMOL_SPECULAR_REFLECTION_PERCENTAGE = 100;

    /**
     * Minimum specular reflection percentage (range 0 - 100) for Jmol viewer
     */
    int MINIMUM_JMOL_SPECULAR_REFLECTION_PERCENTAGE = 0;

    /**
     * Maximum specular reflection percentage (range 0 - 100) for Jmol viewer
     */
    int MAXIMUM_JMOL_SPECULAR_REFLECTION_PERCENTAGE = 100;

    /**
     * Default specular reflection power (range 0 - 100) for Jmol viewer
     */
    int DEFAULT_JMOL_SPECULAR_REFLECTION_POWER = 50;

    /**
     * Minimum specular reflection power (range 0 - 100) for Jmol viewer
     */
    int MINIMUM_JMOL_SPECULAR_REFLECTION_POWER = 0;

    /**
     * Maximum specular reflection power (range 0 - 100) for Jmol viewer
     */
    int MAXIMUM_JMOL_SPECULAR_REFLECTION_POWER = 100;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Parallel computing">
    /**
     * Minimum number of target processor cores (without hyper-threading)
     */
    int MINIMUM_NUMBER_OF_PROCESSOR_CORES = 1;

    /**
     * Default number of target processor cores (without hyper-threading)
     */
    int DEFAULT_NUMBER_OF_PROCESSOR_CORES = 1;

    /**
     * Maximum number of target processor cores (without hyper-threading)
     */
    int MAXIMUM_NUMBER_OF_PROCESSOR_CORES = Integer.MAX_VALUE;

    /**
     * Minimum number of parallel slicers.
     */
    int MINIMUM_NUMBER_OF_PARALLEL_SLICERS = 1;

    /**
     * Default number of parallel slicers.
     */
    int DEFAULT_NUMBER_OF_PARALLEL_SLICERS = 1;

    /**
     * Maximum number of parallel slicers (includes hyper-threading).
     */
    int MAXIMUM_NUMBER_OF_PARALLEL_SLICERS = Integer.MAX_VALUE;

    /**
     * Minimum of minimum number of simulation box cells for parallelisation
     */
    int MINIMUM_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION = 10;

    /**
     * Default minimum number of simulation box cells for parallelisation
     */
    int DEFAULT_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION = 100;

    /**
     * Maximum of minimum number of simulation box cells for parallelisation
     */
    int MAXIMUM_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION = Integer.MAX_VALUE;

    /**
     * Minimum of minimum number of bonds for parallelisation
     */
    int MINIMUM_NUMBER_OF_BONDS_FOR_PARALLELIZATION = 10;

    /**
     * Default minimum number of bonds for parallelisation
     */
    int DEFAULT_NUMBER_OF_BONDS_FOR_PARALLELIZATION = 100;

    /**
     * Maximum of minimum number of bonds for parallelisation
     */
    int MAXIMUM_NUMBER_OF_BONDS_FOR_PARALLELIZATION = Integer.MAX_VALUE;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Movie quality">
    /**
     * Minimum number for movie quality (means BEST quality but largest size)
     */
    int MINIMUM_MOVIE_QUALITY = 1;

    /**
     * Default number for movie quality (means good compromise of movie size and
     * quality)
     */
    int DEFAULT_MOVIE_QUALITY = 18;

    /**
     * Maximum number for movie quality (means LOWEST quality but smallest size)
     */
    int MAXIMUM_MOVIE_QUALITY = 36;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Miscellaneous">
    /**
     * Default array size
     */
    int DEFAULT_ARRAY_SIZE = 500;

    /**
     * Default image version
     */
    int DEFAULT_IMAGE_VERSION = 1;

    /**
     * Default number of molecules
     */
    int DEFAULT_NUMBER_OF_MOLECULES = 10;

    /**
     * Default number of selection texts
     */
    int DEFAULT_NUMBER_OF_SELECTION_TEXTS = 500;

    /**
     * Default number of selection text values
     */
    int DEFAULT_NUMBER_OF_SELECTION_TEXT_VALUES = 50;

    /**
     * Default number of value items
     */
    int DEFAULT_NUMBER_OF_VALUE_ITEMS = 100;

    /**
     * Minimum number of after-decimal-separator digits for particle positions
     */
    int MINIMUM_NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS = 3;

    /**
     * Default number of after-decimal-separator digits for particle positions
     */
    int DEFAULT_NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS = 3;

    /**
     * Maximum number of after-decimal-separator digits for particle positions
     */
    int MAXIMUM_NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS = 16;

    /**
     * Minimum maximum number of position correction trials
     */
    int MINIMUM_MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS = 1;

    /**
     * Default maximum number of position correction trials
     */
    int DEFAULT_MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS = 1000;
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Timestamp format definitions">
    /**
     * Standard timestamp format
     */
    String STANDARD_TIMESTAMP_FORMAT = "yyyy/MM/dd - HH:mm:ss";

    /**
     * Standard timestamp regex pattern
     */
    Pattern STANDARD_TIMESTAMP_REGEX_PATTERN = Pattern.compile("[2][0][0-9][0-9]\\/[01][0-9]\\/[0123][0-9] - [012][0-9]\\:[012345][0-9]\\:[012345][0-9]");
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Separator related definitions">
    /**
     * General separator string
     */
    String GENERAL_SEPARATOR = "|";

    /**
     * Regex pattern for GENERAL_SEPARATOR string
     */
    Pattern GENERAL_SEPARATOR_PATTERN = Pattern.compile("\\" + ModelDefinitions.GENERAL_SEPARATOR);

    /**
     * Regex pattern for underscore
     */
    Pattern GENERAL_UNDERSCORE_PATTERN = Pattern.compile("\\_");

    /**
     * General line-separator string
     */
    String GENERAL_LINE_SEPARATOR = "~";

    /**
     * Regex pattern for GENERAL_LINE_SEPARATOR string
     */
    Pattern GENERAL_LINE_SEPARATOR_PATTERN = Pattern.compile("\\" + ModelDefinitions.GENERAL_LINE_SEPARATOR);

    /**
     * General key-value separator string
     */
    String GENERAL_KEY_VALUE_SEPARATOR = "=";

    /**
     * Regex pattern for GENERAL_KEY_VALUE_SEPARATOR string
     */
    Pattern GENERAL_KEY_VALUE_SEPARATOR_PATTERN = Pattern.compile("\\" + ModelDefinitions.GENERAL_KEY_VALUE_SEPARATOR);
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Property change related definitions">
    /**
     * String for property change that indicates an error
     */
    String PROPERTY_CHANGE_ERROR = "PropertyChangeError";

    /**
     * String for property change that indicates new "Job is alive" information
     */
    String PROPERTY_CHANGE_JOB_IS_ALIVE = "PropertyChangeJobIsAlive";

    /**
     * String for property change that indicates progress
     */
    String PROPERTY_CHANGE_PROGRESS = "PropertyChangeProgress";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- System related definitions">
    /**
     * Java version
     */
    String JAVA_VERSION = System.getProperty("java.runtime.version");
    
    /**
     * Minimum Java version
     */
    String MINIMUM_JAVA_VERSION = "1.8";

    /**
     * Operating system
     */
    String OPERATING_SYSTEM = System.getProperty("os.name");

    /**
     * User directory for MFsim data
     */
    String USER_DIRECTORY_PATH = System.getProperty("user.dir");

    /**
     * Path to local data of user
     */
    String LOCAL_USER_DATA_DIRECTORY_PATH = System.getenv("APPDATA");

    /**
     * Line separator of OS
     */
    String LINE_SEPARATOR = System.getProperty("line.separator");
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Mathematical constants">
    /**
     * Mathematical constant
     */
    double FACTOR_3_DIV_4_PI = 0.75 / Math.PI;

    /**
     * Factor which determines the precision of the comparison of the difference
     * of two numbers used in a graphics context
     * NOTE: This factor should correspond to FACTOR_FOR_GRAPHICS_NUMBER_CORRECTION
     */
    double FACTOR_FOR_COMPARISON_OF_GRAPHICS_NUMBERS = 0.001;

    /**
     * Factor for number correction in a graphics context to avoid errors due to
     * roundoff problems
     * NOTE: This factor should correspond to FACTOR_FOR_COMPARISON_OF_GRAPHICS_NUMBERS
     */
    double FACTOR_FOR_GRAPHICS_NUMBER_CORRECTION = 1.0 + 0.001;

    /**
     * 4 * Math.PI
     */
    double FOUR_PI = 4.0 * Math.PI;

    /**
     * 0.5 * Math.PI
     */
    double HALF_PI = 0.5 * Math.PI;

    /**
     * 0.75 * Math.PI
     */
    double THREE_QUARTERS_PI = 0.75 * Math.PI;

    /**
     * 2 * Math.PI
     */
    double TWO_PI = 2.0 * Math.PI;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Physical constants">
    /**
     * Gas constant in J/(mol*K)
     */
    double GAS_CONSTANT = 8.3144621;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Table/matrix cell widths related definitions">
    /**
     * Cell width for numeric values
     */
    String CELL_WIDTH_NUMERIC_80 = "80";

    /**
     * Cell width for numeric values
     */
    String CELL_WIDTH_NUMERIC_100 = "100";

    /**
     * Cell width for numeric values
     */
    String CELL_WIDTH_NUMERIC_120 = "120";

    /**
     * Cell width for numeric values
     */
    String CELL_WIDTH_NUMERIC_140 = "140";

    /**
     * Cell width for texts
     */
    String CELL_WIDTH_TEXT_100 = "100";

    /**
     * Cell width for texts
     */
    String CELL_WIDTH_TEXT_150 = "150";

    /**
     * Cell width for texts
     */
    String CELL_WIDTH_TEXT_200 = "200";

    /**
     * Cell width for texts
     */
    String CELL_WIDTH_TEXT_250 = "250";

    /**
     * Cell width for texts
     */
    String CELL_WIDTH_TEXT_400 = "400";

    /**
     * Molecular structure length for display (should correspond to
     * CELL_WIDTH_TEXT_150)
     */
    int STRUCTURE_LENGTH_FOR_DISPLAY = 15;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Buffer related definitions">
    /**
     * Buffer size (64 kByte = 65536, 256 kByte = 262144, 512 kByte = 524288, 1
     * MByte = 1048576 Byte)
     */
    int BUFFER_SIZE = 65536;

    /**
     * Buffer size (1kByte = 1024, 64 kByte = 65536, 256 kByte = 262144, 512
     * kByte = 524288, 1 MByte = 1048576 Byte)
     */
    int BUFFER_SIZE_SMALL = 1024;

    /**
     * Buffer size (1kByte = 1024, 64 kByte = 65536, 256 kByte = 262144, 512
     * kByte = 524288, 1 MByte = 1048576 Byte)
     */
    int BUFFER_SIZE_TINY = 256;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- License">
    /**
     * Client license
     */
    String LICENSE_CLIENT = "Client";

    /**
     * Full license
     */
    String LICENSE_FULL = "Full";

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- File related definitions">
    /**
     * Pattern for file separator character on Windows (back-slash '\') and UNIX
     * (slash '/')
     */
    Pattern FILE_SEPARATOR_CHARACTER_PATTERN = Pattern.compile("\\\\+|/+");
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous definitions">
    /**
     * Default volume scaling for concentration calculation flag: 
     * True: Molecule numbers are calculated with volume scaling, false: 
     * Molecule numbers are calculated without volume scaling (i.e. scale 
     * factors are all 1.0)
     */
    boolean IS_VOLUME_SCALING_FOR_CONCENTRATION_CALCULATION_DEFAULT = true;
    
    /**
     * Default value for deterministic random seed
     */
    long DETERMINISTIC_RANDOM_SEED_DEFAULT = 1L;

    /**
     * Default deterministic random flag. True: Random number generator with
     * defined seed is used, false: Otherwise
     */
    boolean IS_DETERMINISTIC_RANDOM_DEFAULT = true;

    /**
     * Default Jdpd log level exceptions flag.
     * True: Jdpd log level EXCEPTIONS is used, false: All available Jdpd log 
     * levels are used
     */
    boolean IS_JDPD_LOG_LEVEL_EXCEPTIONS_DEFAULT = true;

    /**
     * Default compartment constant body flag
     * True: Compartment body volume is constant/preserved, false: Otherwise
     */
    boolean IS_CONSTANT_COMPARTMENT_BODY_VOLUME_DEFAULT = true;

    /**
     * Default simulation box slicer flag. True: Simulation box slicer is used
     * for simulation box display, false: Jmol viewer is used for simulation box
     * display
     */
    boolean IS_SIMULATION_BOX_SLICER_DEFAULT = true;

    /**
     * Default Job Result archive step file inclusion flag: True: Position step
     * files are included in Job Result archive files, false: Position step
     * files are omitted in Job Result archive files
     */
    boolean IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION_DEFAULT = true;

    /**
     * Default Job Result archive process parallel in background flag: True: Job
     * Result archive files are generated parallel in background, false: Job
     * Result archive files are generated in sequential manner with progress bar
     */
    boolean IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND_DEFAULT = false;

    /**
     * Default Job Result archive file compression flag: True: Job Result
     * archive file is uncompressed, false: Otherwise (archive file is
     * compressed)
     */
    boolean IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED_DEFAULT = true;

    /**
     * Default Job Input inclusion flag: 
     * True: Job inputs included in Job Result display,
     * false: Job inputs are omitted in Job Result display
     */
    boolean IS_JOB_INPUT_INCLUSION_DEFAULT = true;

    /**
     * Default particle distribution inclusion flag: 
     * True: Particle distribution analysis included in Job Result display,
     * false: Particle distribution analysis is omitted in Job Result display
     */
    boolean IS_PARTICLE_DISTRIBUTION_INCLUSION_DEFAULT = true;

    /**
     * Default simulation steps inclusion flag: 
     * True: Simulation steps are included in Job Result display,
     * false: Simulation steps are omitted in Job Result display
     */
    boolean IS_SIMULATION_STEP_INCLUSION_DEFAULT = true;

    /**
     * Default nearest-neighbour evaluation inclusion flag: 
     * True: Nearest-neighbour evaluation is included in Job Result display,
     * false: Nearest-neighbour evaluation is omitted in Job Result display
     */
    boolean IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION_DEFAULT = true;
    
    /**
     * Default start-geometry file creation flag. True: Start-geometry file is
     * created within job input save operation (if possible), false: Otherwise
     */
    boolean IS_START_GEOMETRY_FILE_CREATED_DEFAULT = false;

    /**
     * Prefix of line in text file to ignore
     */
    String LINE_PREFIX_TO_IGNORE = "#";

    /**
     * Format string for section start tag
     */
    String SECTION_START_TAG_FORMAT = "[%s]";

    /**
     * Format string for section end tag
     */
    String SECTION_END_TAG_FORMAT = "[/%s]";

    /**
     * Schema wildcard string: "*"
     */
    String SCHEMA_WILDCARD_STRING = "*";
    
    /**
     * MFsim session start
     */
    String MFSIM_SESSION_START_FORMAT = "MFsim %s session start";
    
    /**
     * MFsim session end
     */
    String MFSIM_SESSION_END = "MFsim session end";
    
    /**
     * MFsim GitHub URL
     */
    String MFSIM_GITHUB_URL = "https://github.com/zielesny/MFsim";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous regex pattern definitions">
    /**
     * Regex pattern for non-word or non-numeric characters
     */
    Pattern NON_WORDNUMERIC_CHARACTER_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    
    /**
     * Whitespace pattern
     */
    Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Job related definitions">
    // <editor-fold defaultstate="collapsed" desc="- Jdpd parameter definitions">
    /**
     * Jdpd parameter "BackboneBondNumber ". NOTE: Last space in string is IMPORTANT!
     */
    String JDPD_BACKBONE_BOND_NUMBER = "BackboneBondNumber ";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Jdpd blocks">
    /**
     * Block 0 of Jdpd input
     */
    String JDPD_INPUT_BLOCK_0 = "Block 0";

    /**
     * Block 1 of Jdpd input
     */
    String JDPD_INPUT_BLOCK_1 = "Block 1";

    /**
     * Block 2 of Jdpd input
     */
    String JDPD_INPUT_BLOCK_2 = "Block 2";

    /**
     * Block 3 of Jdpd input
     */
    String JDPD_INPUT_BLOCK_3 = "Block 3";

    /**
     * Block 4 of Jdpd input
     */
    String JDPD_INPUT_BLOCK_4 = "Block 4";

    /**
     * Block 5 of Jdpd input
     */
    String JDPD_INPUT_BLOCK_5 = "Block 5";

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Jdpd value item update">
    /**
     * Update class for JobInput related updates
     */
    JobUpdateUtils JOB_UPDATE_UTILS = new JobUpdateUtils();

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous definitions">
    /**
     * Number of decimals for box size
     */
    int BOX_SIZE_NUMBER_OF_DECIMALS = 6;

    /**
     * Number of decimals for concentration calculations
     */
    int CONCENTRATION_NUMBER_OF_DECIMALS = 6;

    /**
     * Number of decimals for interactions
     */
    int INTERACTION_NUMBER_OF_DECIMALS = 6;

    /**
     * Particle update for job input. True: Complete particle information is
     * always updated on edit of job input, false: Otherwise.
     */
    boolean IS_PARTICLE_UPDATE_FOR_JOB_INPUT_DEFAULT = false;

    /**
     * Minimum cut-off distance in Angstrom for radial distribution functions
     */
    double RDF_CUT_OFF_DISTANCE_MINIMUM = 5;

    /**
     * Default cut-off distance in Angstrom for radial distribution functions
     */
    double RDF_CUT_OFF_DISTANCE_DEFAULT = 25;

    /**
     * Maximum cut-off distance in Angstrom for radial distribution functions
     */
    double RDF_CUT_OFF_DISTANCE_MAXIMUM = 50000;

    /**
     * Basic segment length for RDF bins. IMPORTANT: All segment lengths in
     * RDF_SEGMENT_LENGTHS must be a multiple of this value!
     */
    double RDF_BASIC_SEGMENT_LENGTH = 0.05;

    /**
     * Segment lengths for RDF bins. IMPORTANT: All segment lengths must be a
     * multiple of RDF_BASIC_SEGMENT_LENGTH!
     */
    double[] RDF_SEGMENT_LENGTHS = new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

    /**
     * SINGLE start character for protein distance force index
     * NOTE: Must be different from PROTEIN_BACKBONE_FORCE_INDEX_END
     */
    String PROTEIN_BACKBONE_FORCE_INDEX_START = "<";

    /**
     * SINGLE end character for protein distance force index
     * NOTE: Must be different from PROTEIN_BACKBONE_FORCE_INDEX_START
     */
    String PROTEIN_BACKBONE_FORCE_INDEX_END = ">";
    
    /**
     * Number of repetitions for trials to read graphical particle position file
     */
    int NUMBER_OF_GRAPHICAL_PARTICLE_POSITION_FILE_READ_REPETITIONS = 60;

    /**
     * Delay in milliseconds for trials to read graphical particle position file
     */
    long GRAPHICAL_PARTICLE_POSITION_FILE_READ_DELAY = 1000L;
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Graphics related definitions">
    // <editor-fold defaultstate="collapsed" desc="- Compartments and simulation box">
    // <editor-fold defaultstate="collapsed" desc="-- Block names">
    /**
     * Compartment block: Bulk
     */
    String COMPARTMENT_BLOCK_BULK = "Block01Bulk";

    /**
     * Compartment block: Hidden
     */
    String COMPARTMENT_BLOCK_HIDDEN = "Block02Hidden";

    /**
     * Compartment block: Sphere prefix
     */
    String COMPARTMENT_BLOCK_SPHERE = "Block03Sphere";

    /**
     * Compartment block: Xy-layer prefix
     */
    String COMPARTMENT_BLOCK_XY_LAYER = "Block04XyLayer";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Info value item names">
    /**
     * Compartments: Box info value item name
     */
    String BOX_INFO_NAME = "BOX_INFO";

    /**
     * Compartments: Density info value item name
     */
    String DENSITY_INFO_NAME = "DENSITY_INFO";

    /**
     * Compartments: Molecule info value item name
     */
    String MOLECULE_INFO_NAME = "MOLECULE_INFO";

    /**
     * Compartments: Particle info value item name
     */
    String PARTICLE_INFO_NAME = "PARTICLE_INFO";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Other value item names and prefixes">
    /**
     * Compartments: Bulk value item name
     */
    String COMPARTMENT_BULK_NAME = "COMPARTMENT_BULK";
    
    /**
     * Specified name for sphere compartments
     */
    String COMPARTMENT_SPHERE_SPECIFIED_NAME = "COMPARTMENT_SPHERE_SPECIFIED_NAME";
    
    /**
     * Specified name for xy-layer compartments
     */
    String COMPARTMENT_XY_LAYER_SPECIFIED_NAME = "COMPARTMENT_XY_LAYER_SPECIFIED_NAME";

    /**
     * Compartments: Length conversion value item name
     */
    String COMPARTMENT_LENGTH_CONVERSION_NAME = "COMPARTMENT_LENGTH_CONVERSION";

    /**
     * Compartments: Geometry random seed value item name
     */
    String COMPARTMENT_GEOMETRY_RANDOM_SEED_NAME = "COMPARTMENT_GEOMETRY_RANDOM_SEED";

    /**
     * Prefix for molecule display settings value items
     */
    String MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX = "MOLECULE_DISPLAY_SETTINGS_";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Composition related value item names">
    /**
     * Compartments: Prefix for chemical composition value item name
     */
    String CHEMICAL_COMPOSITION_PREFIX_NAME = "CHEMICAL_COMPOSITION_";

    /**
     * Compartments: Prefix for sphere chemical composition value item name
     */
    String COMPARTMENT_SPHERE_CHEMICAL_COMPOSITION_PREFIX_NAME = ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME + "COMPARTMENT_SPHERE_";

    /**
     * Compartments: Prefix for yx-layer chemical composition value item name
     */
    String COMPARTMENT_XY_LAYER_CHEMICAL_COMPOSITION_PREFIX_NAME = ModelDefinitions.CHEMICAL_COMPOSITION_PREFIX_NAME + "COMPARTMENT_XY_LAYER_";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Geometry related value item names">
    /**
     * Compartments: Prefix for geometry value item name
     */
    String GEOMETRY_PREFIX_NAME = "GEOMETRY_";

    /**
     * Compartments: Prefix for sphere geometry value item name
     */
    String COMPARTMENT_SPHERE_GEOMETRY_PREFIX_NAME = ModelDefinitions.GEOMETRY_PREFIX_NAME + "COMPARTMENT_SPHERE_";

    /**
     * Compartments: Prefix for sphere geometry data value item name
     */
    String COMPARTMENT_SPHERE_GEOMETRY_DATA_PREFIX_NAME = ModelDefinitions.COMPARTMENT_SPHERE_GEOMETRY_PREFIX_NAME + "DATA_";

    /**
     * Compartments: Prefix for sphere geometry display value item name
     */
    String COMPARTMENT_SPHERE_GEOMETRY_DISPLAY_PREFIX_NAME = ModelDefinitions.COMPARTMENT_SPHERE_GEOMETRY_PREFIX_NAME + "DISPLAY_";

    /**
     * Compartments: Prefix for xy-layer geometry value item name
     */
    String COMPARTMENT_XY_LAYER_GEOMETRY_PREFIX_NAME = ModelDefinitions.GEOMETRY_PREFIX_NAME + "COMPARTMENT_XY_LAYER_";

    /**
     * Compartments: Prefix for xy-layer geometry data value item name
     */
    String COMPARTMENT_XY_LAYER_GEOMETRY_DATA_PREFIX_NAME = ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_PREFIX_NAME + "DATA_";

    /**
     * Compartments: Prefix for xy-layer geometry display value item name
     */
    String COMPARTMENT_XY_LAYER_GEOMETRY_DISPLAY_PREFIX_NAME = ModelDefinitions.COMPARTMENT_XY_LAYER_GEOMETRY_PREFIX_NAME + "DISPLAY_";

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- JEPG image related attributes">
    /**
     * Quality of JPEG images (0.0F = low to 1.0F = high)
     */
    float JPEG_IMAGE_QUALITY_DEFAULT = 1.0f;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Colors and related attributes">
    /**
     * Simulation box background color
     */
    Color COLOR_SIMULATION_BOX_BACKGROUND = Color.BLACK;

    /**
     * Color for selected compartment 1 for gradient
     */
    Color COLOR_GRADIENT_SELECTED_COMPARTMENT = Color.WHITE;

    /**
     * Sphere color 1 for gradient
     */
    Color COLOR_GRADIENT_SPHERE = Color.GREEN;

    /**
     * xy-Layer color 1 for gradient
     */
    Color COLOR_GRADIENT_XY_LAYER = Color.BLUE;

    /**
     * Color attenuation for gradient default. 0.0 = Minimum (no attenuation),
     * 1.0 = Maximum (full attenuation))
     */
    double COLOR_GRADIENT_ATTENUATION_DEFAULT = 0.7;

    /**
     * Color attenuation for gradient maximum. 0.0 = Minimum (no attenuation),
     * 1.0 = Maximum (full attenuation))
     */
    double COLOR_GRADIENT_ATTENUATION_MAXIMUM = 1.0;

    /**
     * Default depth attenuation of slices for depth impression in simulation
     * box of slicer. 0.0 = Minimum (no attenuation)
     */
    double DEPTH_ATTENUATION_SLICER_DEFAULT = 2.0;

    /**
     * Minimum depth attenuation of slices for depth impression in simulation
     * box of slicer. 0.0 = Minimum (no attenuation)
     */
    double DEPTH_ATTENUATION_SLICER_MINIMUM = 0.0;

    /**
     * Maximum depth attenuation of slices for depth impression in simulation
     * box of slicer. 0.0 = Minimum (no attenuation)
     */
    double DEPTH_ATTENUATION_SLICER_MAXIMUM = 100.0;

    /**
     * Default color shape attenuation for compartment. 0.0 = Minimum (no
     * attenuation), 5.0 = strong attenuation
     */
    double COLOR_SHAPE_ATTENUATION_COMPARTMENT_DEFAULT = 1.0;

    /**
     * Color shape attenuation minimum for compartment. 0.0 = Minimum (no
     * attenuation), 5.0 = strong attenuation
     */
    double COLOR_SHAPE_ATTENUATION_COMPARTMENT_MINIMUM = 0.0;

    /**
     * Color shape attenuation maximum for compartment. 0.0 = Minimum (no
     * attenuation), 5.0 = strong attenuation
     */
    double COLOR_SHAPE_ATTENUATION_COMPARTMENT_MAXIMUM = 5.0;

    /**
     * Default transparency value between 0.0 (no transparency) and 1.0 (full
     * transparency)
     */
    float COLOR_TRANSPARENCY_DEFAULT = 0.0f;

    /**
     * Maximum transparency value between 0.0 (full transparency) and 1.0 (no
     * transparency)
     */
    float COLOR_TRANSPARENCY_MAXIMUM = 1.0f;

    /**
     * Default specular white attenuation for bodies to body color for slicer.
     * 0.0 = Minimum (no attenuation), 1.0 = Maximum (maximum attenuation)
     */
    double SPECULAR_WHITE_ATTENUATION_DEFAULT = 0.5;
    
    /**
     * Minimum compartment body change response factor
     */
    double COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR_MINIMUM = 0.000001;
    
    /**
     * Default compartment body change response factor
     */
    double COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR_DEFAULT = 0.001;
    
    /**
     * Maximum compartment body change response factor
     */
    double COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR_MAXIMUM = 1.0;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Shifts for simulation box slicer related definitions">
    /**
     * Default x-shift in pixel for simulation box slicer
     */
    int X_SHIFT_IN_PIXEL_SLICER_DEFAULT = 0;

    /**
     * Default y-shift in pixel for simulation box slicer
     */
    int Y_SHIFT_IN_PIXEL_SLICER_DEFAULT = 0;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Particle shift related definitions">
    /**
     * Default particle shift in percent of length of simulation box
     */
    int PARTICLE_SHIFT_DEFAULT = 0;

    /**
     * Minimum particle shift in percent of length of simulation box
     */
    int PARTICLE_SHIFT_MINIMUM = -100;

    /**
     * Maximum particle shift in percent of length of simulation box
     */
    int PARTICLE_SHIFT_MAXIMUM = 100;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Rotation related definitions">
    /**
     * Default angle for rotation around x-axis in degree
     */
    int ROTATION_AROUND_X_AXIS_ANGLE_DEFAULT = 0;

    /**
     * Minimum angle for rotation around x-axis in degree
     */
    int ROTATION_AROUND_X_AXIS_ANGLE_MINIMUM = 0;

    /**
     * Maximum angle for rotation around x-axis in degree
     */
    int ROTATION_AROUND_X_AXIS_ANGLE_MAXIMUM = 360;

    /**
     * Default angle for rotation around y-axis in degree
     */
    int ROTATION_AROUND_Y_AXIS_ANGLE_DEFAULT = 0;

    /**
     * Minimum angle for rotation around y-axis in degree
     */
    int ROTATION_AROUND_Y_AXIS_ANGLE_MINIMUM = 0;

    /**
     * Maximum angle for rotation around y-axis in degree
     */
    int ROTATION_AROUND_Y_AXIS_ANGLE_MAXIMUM = 360;

    /**
     * Default angle for rotation around z-axis in degree
     */
    int ROTATION_AROUND_Z_AXIS_ANGLE_DEFAULT = 0;

    /**
     * Minimum angle for rotation around z-axis in degree
     */
    int ROTATION_AROUND_Z_AXIS_ANGLE_MINIMUM = 0;

    /**
     * Maximum angle for rotation around z-axis in degree
     */
    int ROTATION_AROUND_Z_AXIS_ANGLE_MAXIMUM = 360;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- RadialGradientPaint focus factor related definitions">
    /**
     * Minimum factor for focus of RadialGradientPaint in x-direction
     */
    float RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X_MINIMUM = 0.1f;

    /**
     * Default factor for focus of RadialGradientPaint in x-direction
     */
    float RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X_DEFAULT = 0.7f;

    /**
     * Maximum factor for focus of RadialGradientPaint in x-direction
     */
    float RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X_MAXIMUM = 1.9f;

    /**
     * Minimum factor for focus of RadialGradientPaint in y-direction
     */
    float RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y_MINIMUM = 0.1f;

    /**
     * Default factor for focus of RadialGradientPaint in y-direction
     */
    float RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y_DEFAULT = 0.7f;

    /**
     * Maximum factor for focus of RadialGradientPaint in y-direction
     */
    float RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y_MAXIMUM = 1.9f;

    /**
     * Minimum size of specular white reflection
     */
    float SPECULAR_WHITE_SIZE_SLICER_MINIMUM = 0.01f;

    /**
     * Default size of specular white reflection
     */
    float SPECULAR_WHITE_SIZE_SLICER_DEFAULT = 0.1f;

    /**
     * Maximum size of specular white reflection
     */
    float SPECULAR_WHITE_SIZE_SLICER_MAXIMUM = 0.9f;

    /**
     * Minimum radius magnification of RadialGradientPaint
     */
    float RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION_MINIMUM = 0.1f;

    /**
     * Default radius magnification of RadialGradientPaint
     */
    float RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION_DEFAULT = 2.0f;

    /**
     * Maximum radius magnification of RadialGradientPaint
     */
    float RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION_MAXIMUM = 100.0f;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="-- Miscellaneous definitions">
    /**
     * Initial number of bodies
     */
    int INITIAL_NUMBER_OF_BODIES = 10;

    /**
     * Maximum number of each compartment in simulation box: This number must be
     * higher than any expected value.
     */
    int MAXIMUM_NUMBER_OF_COMPARTMENTS = 1000000;

    /**
     * Number of decimals for compositions
     */
    int NUMBER_OF_DECIMALS_FOR_COMPOSITIONS = 2;

    /**
     * Number of decimals for statistics
     */
    int NUMBER_OF_DECIMALS_FOR_STATISTICS = 3;

    /**
     * Number of decimals for linear regression (LR) slope
     */
    int NUMBER_OF_DECIMALS_FOR_SLOPE = 6;

    /**
     * Number of decimals for graphics coordinates display. NOTE: This value
     * must be at least 3 which corresponds to 10^3 = 1000 different value to
     * successfully work with MAXIMUM_NUMBER_OF_SLICES = 500 slices.
     * NOTE: This value subtly influences DECREASE_FACTOR.
     */
    int NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES = 3;

    /**
     * Number of decimals for color properties
     */
    int NUMBER_OF_DECIMALS_FOR_COLOR_PROPERTIES = 2;

    /**
     * Number of decimals for parameters used by PdbToDPD.
     */
    int NUMBER_OF_DECIMALS_FOR_PDBTODPD_PARAMETERS = 6;

    /**
     * Factor for minimum compartment size
     */
    double MINIMUM_COMPARTMENT_FACTOR = 0.001;

    /**
     * Factor for decreasing a number to avoid round-off errors
     * NOTE: DECREASE_FACTOR must correspond to 
     * NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES.
     */
    double DECREASE_FACTOR = 0.999;

    /**
     * Default maximum number of selected molecules for slicer
     */
    int DEFAULT_MAX_SELECTED_MOLECULE_NUMBER_SLICER = 1000;

    /**
     * Minimum maximum number of selected molecules for slicer
     */
    int MINIMUM_MAX_SELECTED_MOLECULE_NUMBER_SLICER = 1;
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Simulation box slicer">
    /**
     * Prefix of slice image file name
     */
    String PREFIX_OF_SLICE_IMAGE_FILENAME = "Slice";

    /**
     * Prefix of time step slice image file name
     */
    String PREFIX_OF_TIME_STEP_SLICE_IMAGE_FILENAME = "TimeStepSlice";

    /**
     * Prefix of spin step slice image file name
     */
    String PREFIX_OF_SPIN_STEP_SLICE_IMAGE_FILENAME = "SpinStepSlice";

    /**
     * Prefix of move step slice image file name
     */
    String PREFIX_OF_MOVE_STEP_SLICE_IMAGE_FILENAME = "MoveStepSlice";

    /**
     * Delay in milliseconds for simulation box slicer
     */
    int SLICER_DELAY = 500;

    /**
     * Scale mode for slicer images
     */
    int SLICER_IMAGE_SCALE_MODE = Image.SCALE_SMOOTH;

    /**
     * Name of rotation change value item
     */
    String ROTATION_CHANGE_VALUE_ITEM_NAME = "ROTATION_CHANGE_VALUE_ITEM_NAME";

    /**
     * Name of particle shift change value item
     */
    String PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME = "PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME";

    /**
     * Name of shift change value item
     */
    String SHIFT_CHANGE_VALUE_ITEM_NAME = "SHIFT_CHANGE_VALUE_ITEM_NAME";

    /**
     * Minimum number of volume bins
     */
    int MINIMUM_NUMBER_OF_ZOOM_VOLUME_BINS = 2;
    
    /**
     * Default number of volume bins
     */
    int DEFAULT_NUMBER_OF_ZOOM_VOLUME_BINS = 20;

    /**
     * Number of decimals for symmetry index display
     */    
    int SYMMETRY_INDEX_NUMBER_OF_DECIMALS = 4;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Color related definitions">
    /**
     * Color model: 32 (32 bit), 0xff0000 (red mask), 0x00ff00 (green mask),
     * 0x0000ff (blue mask)
     */
    ColorModel COLOR_MODEL = new DirectColorModel(32, 0xff0000, 0x00ff00, 0x0000ff);

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Movie related definitions">
    /**
     * Prefix of DPD movie filename
     */
    String PREFIX_OF_MOVIE_FILENAME = "DpdMovie_";

    /**
     * DPD movie file ending
     */
    String MOVIE_FILE_ENDING = ".mp4";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Selection related definitions">
    /**
     * Default number of selected particles
     */
    int DEFEAULT_NUMBER_OF_SELECTED_PARTICLES = 1000;

    /**
     * Default number of selected molecules
     */
    int DEFEAULT_NUMBER_OF_SELECTED_MOLECULES = 100;

    /**
     * Default number of selected molecule types
     */
    int DEFAULT_NUMBER_OF_SELECTED_MOLECULE_TYPES = 10;

    /**
     * Default number of selected molecule particle strings
     */
    int DEFEAULT_NUMBER_OF_SELECTED_MOLECULE_PARTICLE_STRINGS = 1000;

    /**
     * Default number of molecule indices (with the assumption that water 
     * particle is not shown)
     */
    int DEFEAULT_NUMBER_OF_MOLECULE_INDICES = 10000;
    
    /**
     * Default size of molecule selection info string
     */
    int DEFAULT_MOLECULE_SELECTION_INFO_STRING_SIZE = 100;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous definitions">
    /**
     * Prefix of slice image file name
     */
    String PREFIX_OF_IMAGE_FILENAME = "MF1_Image_";

    /**
     * Number of digits for zeros number string
     */
    int NUMBER_OF_DIGITS_FOR_ZEROS_NUMBER_STRING = 9;

    /**
     * Pattern for initial zeros number string files, i.e. for "000000354ABC"
     * pattern matches "000000354"
     */
    Pattern INITIAL_ZEROS_NUMBER_STRING_FILENAME_PATTERN = Pattern.compile("^(\\d{" + String.valueOf(NUMBER_OF_DIGITS_FOR_ZEROS_NUMBER_STRING) + "}).*");
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="PreferenceBasic value items related definitions">
    // <editor-fold defaultstate="collapsed" desc="- PreferenceBasic value item update">
    /**
     * Update class for PreferenceBasic value items
     */
    PreferenceEditableValueItemUpdate PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE = new PreferenceEditableValueItemUpdate();
    // </editor-fold>
    // </editor-fold>

}
