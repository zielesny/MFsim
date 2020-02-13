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
package de.gnwi.mfsim.model.preference;
/**
 * String constants for PreferenceBasic singleton XML representation
 *
 * @author Achim Zielesny
 */
public interface PreferenceXmlName {

    /**
     * Name of basic preferences
     */
    String BASIC_PREFERENCES = "BasicPreferences";

    /**
     * Name of MFsim table-data schemata
     */
    String MFSIM_TABLE_DATA_SCHEMATA = "MFsimTableDataSchemata";

    /**
     * Index of first slice for simulation box slicer
     */
    String FIRST_SLICE_INDEX = "FirstSliceIndex";

    /**
     * Index of wait steps for simulation box slicer
     */
    String NUMBER_OF_BOX_WAIT_STEPS = "NumberOfBoxWaitSteps";

    /**
     * Number of frame points for simulation box slicer
     */
    String NUMBER_OF_FRAME_POINTS_SLICER = "NumberOfFramePointsSlicer";

    /**
     * Time step display for simulation box slicer
     */
    String TIME_STEP_DISPLAY_SLICER = "TimeStepDisplaySlicer";

    /**
     * Name of particle update for job input
     */
    String IS_PARTICLE_UPDATE_FOR_JOB_INPUT = "IsParticleUpdateForJobInput";

    /**
     * Name of Job Result archive step file inclusion flag
     */
    String IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION = "IsJobResultArchiveStepFileInclusion";

    /**
     * Name of Job Input inclusion flag
     */
    String IS_JOB_INPUT_INCLUSION = "IsJobInputInclusion";

    /**
     * Name of particle distribution inclusion flag
     */
    String IS_PARTICLE_DISTRICUTION_INCLUSION = "IsParticleDistributionInclusion";

    /**
     * Name of simulation step inclusion flag
     */
    String IS_SIMULATION_STEP_INCLUSION = "IsSimulationStepInclusion";

    /**
     * Name of nearest-neighbor evaluation inclusion flag
     */
    String IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION = "IsNearestNeighborEvaluationInclusion";

    /**
     * Name of Job Result archive process parallel in background flag
     */
    String IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND = "IsJobResultArchiveProcessParallelInBackground";

    /**
     * Name of Job Result archive file compression flag
     */
    String IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED = "IsJobResultArchiveFileUncompressed";

    /**
     * Name of deterministic random flag
     */
    String IS_DETERMINISTIC_RANDOM = "IsDeterministicRandom";

    /**
     * Name of Jdpd exceptions log level flag
     */
    String IS_JDPD_LOG_LEVEL_EXCEPTIONS = "IsJdpdLogLevelExceptions";
    
    /**
     * Name of compartment constant body flag
     */
    String IS_CONSTANT_COMPARTMENT_BODY_VOLUME = "IsConstantCompartmentBodyVolume";

    /**
     * Name of simulation box slicer flag
     */
    String IS_SIMULATION_BOX_SLICER = "IsSimulationBoxSlicer";

    /**
     * Name of single slice display flag
     */
    String IS_SINGLE_SLICE_DISPLAY = "IsSingleSliceDisplay";

    /**
     * Name of slicer graphics mode
     */
    String SLICER_GRAPHICS_MODE = "SlicerGraphicsMode";

    /**
     * Name of simulation box background color slicer
     */
    String SIMULATION_BOX_BACKGROUND_COLOR_SLICER = "SimulationBoxBackgroundColorSlicer";

    /**
     * Name of measurement color slicer
     */
    String MEASUREMENT_COLOR_SLICER = "MeasurementColorSlicer";

    /**
     * Name of molecule selection color for slicer
     */
    String MOLECULE_SELECTION_COLOR_SLICER = "MoleculeSelectionColorSlicer";

    /**
     * Name of frame color slicer
     */
    String FRAME_COLOR_SLICER = "FrameColorSlicer";

    /**
     * Name of Jmol simulation box background color
     */
    String JMOL_SIMULATION_BOX_BACKGROUND_COLOR = "JmolSimulationBoxBackgroundColor";

    /**
     * Name of protein viewer background color
     */
    String PROTEIN_VIEWER_BACKGROUND_COLOR = "ProteinViewerBackgroundColor";

    /**
     * Name of image storage mode
     */
    String IMAGE_STORAGE_MODE = "ImageStorageMode";

    /**
     * Name of particle color display mode
     */
    String PARTICLE_COLOR_DISPLAY_MODE = "ParticleColorDisplayMode";

    /**
     * Name of single box view flag
     */
    String BOX_VIEW_DISPLAY = "BoxViewDisplay";

    /**
     * Name of slicer frame display flag
     */
    String IS_FRAME_DISPLAY_SLICER = "IsFrameDisplaySlicer";

    /**
     * Name of specular white attenuation slicer
     */
    String SPECULAR_WHITE_ATTENUATION_SLICER = "SpecularWhiteAttenuationSlicer";

    /**
     * Name of color shape attenuation compartment
     */
    String COLOR_SHAPE_ATTENUATION_COMPARTMENT = "DepthAttenuationCompartment";

    /**
     * Name of compartment body change response factor
     */
    String COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR = "CompartmentBodyChangeResponseFactor";

    /**
     * Name of maximum number of selected molecules for slicer
     */
    String MAX_SELECTED_MOLECULE_NUMBER_SLICER = "MaxSelectedMoleculeNumberSlicer";

    /**
     * Name of depth attenuation slicer
     */
    String DEPTH_ATTENUATION_SLICER = "DepthAttenuationSlicer";

    /**
     * Name of x-shift in pixel for simulation box slicer
     */
    String X_SHIFT_IN_PIXEL_SLICER = "XShiftInPixelSlicer";

    /**
     * Name of y-shift in pixel for simulation box slicer
     */
    String Y_SHIFT_IN_PIXEL_SLICER = "YShiftInPixelSlicer";

    /**
     * Name of rotation around x-axis angle
     */
    String ROTATION_AROUND_X_AXIS_ANGLE = "RotationAroundXaxisAngle";

    /**
     * Name of rotation around y-axis angle
     */
    String ROTATION_AROUND_Y_AXIS_ANGLE = "RotationAroundYaxisAngle";

    /**
     * Name of rotation around Z-axis angle
     */
    String ROTATION_AROUND_Z_AXIS_ANGLE = "RotationAroundZaxisAngle";

    /**
     * Name of particle shift in percent along x-axis
     */
    String PARTICLE_SHIFT_X = "ParticleShiftX";

    /**
     * Name of particle shift in percent along y-axis
     */
    String PARTICLE_SHIFT_Y = "ParticleShiftY";

    /**
     * Name of particle shift in percent along z-axis
     */
    String PARTICLE_SHIFT_Z = "ParticleShiftZ";
    
    /**
     * Name of radius magnification of RadialGradientPaint
     */
    String RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION = "RadialGradientPaintRadiusMagnification";

    /**
     * Name of size of specular white reflection
     */
    String SPECULAR_WHITE_SIZE_SLICER = "SpecularWhiteSizeSlicer";

    /**
     * Name of radial gradient paint focus factor in x-direction
     */
    String RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X = "RadialGradientPaintFocusFactorX";

    /**
     * Name of radial gradient paint focus factor in y-direction
     */
    String RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y = "RadialGradientPaintFocusFactorY";

    /**
     * Name of JPEG image quality
     */
    String JPEG_IMAGE_QUALITY = "JpegImageQuality";

    /**
     * Name of color gradient attenuation compartment
     */
    String COLOR_GRADIENT_ATTENUATION_COMPARTMENT = "ColorGradientAttenuationCompartment";

    /**
     * Name of color gradient attenuation compartment
     */
    String COLOR_GRADIENT_ATTENUATION_SLICER = "ColorGradientAttenuationSlicer";

    /**
     * Name of color alphaValue transparency compartment
     */
    String COLOR_TRANSPARENCY_COMPARTMENT = "ColorTransparencyCompartment";

    /**
     * Name of dialog slicer show height
     */
    String DIALOG_SLICER_SHOW_HEIGHT = "DialogSlicerShowHeight";

    /**
     * Name of dialog slicer show width
     */
    String DIALOG_SLICER_SHOW_WIDTH = "DialogSlicerShowWidth";

    /**
     * Name of custom dialog height
     */
    String CUSTOM_DIALOG_HEIGHT = "CustomDialogHeight";

    /**
     * Name of custom dialog width
     */
    String CUSTOM_DIALOG_WIDTH = "CustomDialogWidth";

    /**
     * Name of dialog single slicer show height
     */
    String DIALOG_SINGLE_SLICER_SHOW_HEIGHT = "DialogSingleSlicerShowHeight";

    /**
     * Name of dialog single slicer show width
     */
    String DIALOG_SINGLE_SLICER_SHOW_WIDTH = "DialogSingleSlicerShowWidth";

    /**
     * Name of dialog movie slicer show height
     */
    String DIALOG_SIMULATION_MOVIE_SLICER_SHOW_HEIGHT = "DialogSimulationMovieSlicerShowHeight";

    /**
     * Name of dialog movie slicer show width
     */
    String DIALOG_SIMULATION_MOVIE_SLICER_SHOW_WIDTH = "DialogSimulationMovieSlicerShowWidth";

    /**
     * Name of dialog value item edit height
     */
    String DIALOG_VALUE_ITEM_EDIT_HEIGHT = "DialogValueItemEditHeight";

    /**
     * Name of dialog value item edit width
     */
    String DIALOG_VALUE_ITEM_EDIT_WIDTH = "DialogValueItemEditWidth";

    /**
     * Name of dialog text edit height
     */
    String DIALOG_TEXT_EDIT_HEIGHT = "DialogTextEditHeight";

    /**
     * Name of dialog text edit width
     */
    String DIALOG_TEXT_EDIT_WIDTH = "DialogTextEditWidth";

    /**
     * Name of dialog value item show height
     */
    String DIALOG_VALUE_ITEM_SHOW_HEIGHT = "DialogValueItemShowHeight";

    /**
     * Name of dialog value item show width
     */
    String DIALOG_VALUE_ITEM_SHOW_WIDTH = "DialogValueItemShowWidth";

    /**
     * Name of dialog table-data schemata manage height
     */
    String DIALOG_TABLE_DATA_SCHEMATA_MANAGE_HEIGHT = "DialogTableDataSchemataManageHeight";

    /**
     * Name of dialog table-data schemata manage width
     */
    String DIALOG_TABLE_DATA_SCHEMATA_MANAGE_WIDTH = "DialogTableDataSchemataManageWidth";

    /**
     * Name of dialog value item matrix diagram height
     */
    String DIALOG_VALUE_ITEM_MATRIX_DIAGRAM_HEIGHT = "DialogValueItemMatrixDiagramHeight";

    /**
     * Name of dialog value item matrix diagram width
     */
    String DIALOG_VALUE_ITEM_MATRIX_DIAGRAM_WIDTH = "DialogValueItemMatrixDiagramWidth";

    /**
     * Name of dialog structure edit height
     */
    String DIALOG_STRUCTURE_EDIT_HEIGHT = "DialogStructureEditHeight";

    /**
     * Name of dialog structure edit width
     */
    String DIALOG_STRUCTURE_EDIT_WIDTH = "DialogStructureEditWidth";

    /**
     * Name of dialog peptide edit height
     */
    String DIALOG_PEPTIDE_EDIT_HEIGHT = "DialogPeptideEditHeight";

    /**
     * Name of dialog peptide edit width
     */
    String DIALOG_PEPTIDE_EDIT_WIDTH = "DialogPeptideEditWidth";

    /**
     * Name of dialog compartment edit height
     */
    String DIALOG_COMPARTMENT_EDIT_HEIGHT = "DialogCompartmentEditHeight";

    /**
     * Name of dialog compartment edit width
     */
    String DIALOG_COMPARTMENT_EDIT_WIDTH = "DialogCompartmentEditWidth";

    /**
     * Name of delay for files in milliseconds
     */
    String DELAY_FOR_FILES_IN_MILLISECONDS = "DelayForFilesInMilliseconds";

    /**
     * Name of delay for job start in milliseconds
     */
    String DELAY_FOR_JOB_START_IN_MILLISECONDS = "DelayForJobStartInMilliseconds";

    /**
     * Name of timer interval in milliseconds
     */
    String TIMER_INTERVAL_IN_MILLISECONDS = "TimerIntervalInMilliseconds";

    /**
     * Name of minimum bond length in DPD units
     */
    String MINIMUM_BOND_LENGTH_DPD = "MinimumBondLengthDpd";

    /**
     * Name of maximum number of particles for graphical display
     */
    String MAXIMUM_NUMBER_OF_PARTICLES_FOR_GRAPHICAL_DISPLAY = "MaximumNumberOfParticlesForGraphicalDisplay";

    /**
     * Name of number of steps for RDF calculation
     */
    String NUMBER_OF_STEPS_FOR_RDF_CALCULATION = "NumberOfStepsForRdfCalculation";

    /**
     * Name of number of steps for volume bins
     */
    String NUMBER_OF_ZOOM_VOLUME_BINS = "NumberOfVolumeBins";
    
    /**
     * Name of number of trials for compartment related calculations
     */
    String NUMBER_OF_TRIALS_FOR_COMPARTMENT = "NumberOfTrialsForCompartment";

    /**
     * Name of animation speed
     */
    String ANIMATION_SPEED = "AnimationSpeed";

    /**
     * Name of minimum number of simulation box cells for parallelisation
     */
    String NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION = "NumberOfSimulationBoxCellsforParallelization";

    /**
     * Name of minimum number of bonds for parallelisation
     */
    String NUMBER_OF_BONDS_FOR_PARALLELIZATION = "NumberOfBondsforParallelization";
    
    /**
     * Name of numberOfStepsForJobRestart
     */
    String NUMBER_OF_STEPS_FOR_JOB_RESTART = "NumberOfAdditionalStepsForJobRestart";

    /**
     * Name of simulation box enlargement percentage
     */
    String SIMULATION_BOX_MAGNIFICATION_PERCENTAGE = "SimulationBoxMagnificationPercentage";

    /**
     * Name of number of spin steps
     */
    String NUMBER_OF_SPIN_STEPS = "NumberOfSpinSteps";

    /**
     * Name of internal MFsim job path
     */
    String INTERNAL_MFSIM_JOB_PATH = "InternalMFsimJobPath";

    /**
     * Name of internal temp path
     */
    String INTERNAL_TEMP_PATH = "InternalTempPath";

    /**
     * Name of current particle set filename
     */
    String CURRENT_PARTICLE_SET_FILENAME = "CurrentParticleSetFilename";

    /**
     * Name of simulation movie image path
     */
    String SIMULATION_MOVIE_IMAGE_PATH = "SimulationMovieImagePath";

    /**
     * Name of chart movie image path
     */
    String CHART_MOVIE_IMAGE_PATH = "ChartMovieImagePath";

    /**
     * Name of image version filter after date
     */
    String IMAGE_VERSION = "ImageVersion";

    /**
     * Name of job input filter after date
     */
    String JOB_INPUT_FILTER_AFTER_TIMESTAMP = "JobInputFilterAfterDate";

    /**
     * Name of job input filter before date
     */
    String JOB_INPUT_FILTER_BEFORE_TIMESTAMP = "JobInputFilterBeforeDate";

    /**
     * Name of job input filter contains phrase
     */
    String JOB_INPUT_FILTER_CONTAINS_PHRASE = "JobInputFilterContainsPhrase";

    /**
     * Name of job result filter after date
     */
    String JOB_RESULT_FILTER_AFTER_TIMESTAMP = "JobResultFilterAfterDate";

    /**
     * Name of job result filter before date
     */
    String JOB_RESULT_FILTER_BEFORE_TIMESTAMP = "JobResultFilterBeforeDate";

    /**
     * Name of job result filter contains phrase
     */
    String JOB_RESULT_FILTER_CONTAINS_PHRASE = "JobResultFilterContainsPhrase";

    /**
     * Name of last selected path
     */
    String LAST_SELECTED_PATH = "LastSelectedPath";

    /**
     * Name of main frame height
     */
    String MAIN_FRAME_HEIGHT = "MainFrameHeight";

    /**
     * Name of main frame width
     */
    String MAIN_FRAME_WIDTH = "MainFrameWidth";

    /**
     * Name of number of slices
     */
    String NUMBER_OF_SLICES = "NumberOfSlices";

    /**
     * Name of JMol shade power
     */
    String JMOL_SHADE_POWER = "JmolShadePower";

    /**
     * Name of JMol ambient light percentage
     */
    String JMOL_AMBIENT_LIGHT_PERCENTAGE = "JmolAmbientLightPercentage";

    /**
     * Name of JMol diffuse light percentage
     */
    String JMOL_DIFFUSE_LIGHT_PERCENTAGE = "JmolDiffuseLightPercentage";

    /**
     * Name of JMol specular reflection exponent
     */
    String JMOL_SPECULAR_REFLECTION_EXPONENT = "JmolSpecularReflectionExponent";

    /**
     * Name of JMol specular reflection percentage
     */
    String JMOL_SPECULAR_REFLECTION_PERCENTAGE = "JmolSpecularReflectionPercentage";

    /**
     * Name of JMol specular reflection power
     */
    String JMOL_SPECULAR_REFLECTION_POWER = "JmolSpecularReflectionPower";

    /**
     * Number of parallel simulations
     */
    String NUMBER_OF_PARALLEL_SIMULATIONS = "NumberOfParallelSimulations";

    /**
     * Number of parallel slicers
     */
    String NUMBER_OF_PARALLEL_SLICERS = "NumberOfParallelSlicers";

    /**
     * Number of parallel calculators
     */
    String NUMBER_OF_PARALLEL_CALCULATORS = "NumberOfParallelCalculators";

    /**
     * Number of parallel particle position writers
     */
    String NUMBER_OF_PARALLEL_PARTICLE_POSITION_WRITERS = "NumberOfParallelParticlePositionWriters";

    /**
     * Number of after-decimal-separator digits for particle positions
     */
    String NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS = "NumberOfAfterDecimalDigitsForParticlePositions";

    /**
     * Maximum number of position correction trials
     */
    String MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS = "MaximumNumberOfPositionCorrectionTrials";

    /**
     * Movie quality
     */
    String MOVIE_QUALITY = "MovieQuality";

    /**
     * Name of previous monomers
     */
    String PREVIOUS_MONOMERS = "PreviousMonomers";

    /**
     * Name of single previous monomer
     */
    String PREVIOUS_SINGLE_MONOMER = "PreviousSingleMonomer";

    /**
     * Name of previous structures
     */
    String PREVIOUS_STRUCTURES = "PreviousStructures";

    /**
     * Name of single previous structure
     */
    String PREVIOUS_SINGLE_STRUCTURE = "PreviousSingleStructure";

    /**
     * Name of previous peptides
     */
    String PREVIOUS_PEPTIDES = "PreviousPeptides";

    /**
     * Name of single previous peptide
     */
    String PREVIOUS_SINGLE_PEPTIDE = "PreviousSinglePeptide";

    /**
     * Name of version
     */
    String VERSION = "Version";

    /**
     * Name of schema value item container
     */
    String SCHEMA_VALUE_ITEM_CONTAINER = "SchemaValueItemContainer";

}
