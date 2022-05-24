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
package de.gnwi.mfsim.model.preference;

import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.util.FileDeletionTask;
import de.gnwi.mfsim.model.util.GraphicsModeEnum;
import de.gnwi.mfsim.model.util.ImageStorageEnum;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.ParticleColorDisplayEnum;
import de.gnwi.mfsim.model.util.SimulationBoxChangeInfo;
import de.gnwi.mfsim.model.util.StandardColorEnum;
import de.gnwi.mfsim.model.util.MovieSlicerConfiguration;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.JOptionPane;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Singleton class for basic preferences
 *
 * @author Achim Zielesny
 */
public final class Preferences {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Basic preference instance
     */
    private static Preferences basicPreference = new Preferences();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Quality of JPEG images (0.0F = low to 1.0F = high)
     */
    private float jpegImageQuality;

    /**
     * Transparency value between 0.0 (no transparency) and 1.0 (full
     * transparency) for compartments
     */
    private float colorTransparencyCompartment;

    /**
     * Color gradient attenuation. 0.0: Minimum (no attenuation), 1.0: Maximum
     * (full attenuation) for compartments
     */
    private double colorGradientAttenuationCompartment;

    /**
     * Color gradient attenuation. 0.0: Minimum (no attenuation), 1.0: Maximum
     * (full attenuation) for slicer
     */
    private double colorGradientAttenuationSlicer;

    /**
     * Specular white attenuation for bodies to body color for slicer. 0.0 =
     * Minimum (no attenuation), 1.0 = Maximum (maximum attenuation)
     */
    private double specularWhiteAttenuationSlicer;

    /**
     * Color shape attenuation of bodies for depth impression. 0.0 = Minimum (no
     * attenuation), 1.0 = Maximum (maximum attenuation) for compartments
     */
    private double depthAttenuationCompartment;

    /**
     * Compartment body change response factor
     */
    private double compartmentBodyChangeResponseFactor;
    
    /**
     * Maximum number of selected molecules
     */
    private int maxSelectedMoleculeNumberSlicer;

    /**
     * Depth attenuation of slices for depth impression (fog) in simulation box
     * of slicer. 0.0 = Minimum (no attenuation)
     */
    private double depthAttenuationSlicer;

    /**
     * X-shift in pixel for simulation box slicer
     */
    private int xShiftInPixelSlicer;

    /**
     * Y-shift in pixel for simulation box slicer
     */
    private int yShiftInPixelSlicer;

    /**
     * Angle for rotation around x axis in degree
     */
    private int rotationAroundXaxisAngle;

    /**
     * Angle for rotation around y axis in degree
     */
    private int rotationAroundYaxisAngle;

    /**
     * Angle for rotation around z axis in degree
     */
    private int rotationAroundZaxisAngle;

    /**
     * Particle shift in percent of simulation box length along x-axis
     */
    private int particleShiftX;

    /**
     * Particle shift in percent of simulation box length along y-axis
     */
    private int particleShiftY;

    /**
     * Particle shift in percent of simulation box length along z-axis
     */
    private int particleShiftZ;
    
    /**
     * Size of specular white reflection for slicer
     */
    private float specularWhiteSizeSlicer;

    /**
     * Radius magnification of RadialGradientPaint
     */
    private float radialGradientPaintRadiusMagnification;

    /**
     * Factor for focus of RadialGradientPaint in x-direction
     */
    private float radialGradientPaintFocusFactorX;

    /**
     * Factor for focus of RadialGradientPaint in y-direction
     */
    private float radialGradientPaintFocusFactorY;

    /**
     * Delay in milliseconds for directory/file delete operations: This is
     * necessary due to OS specific relations between files and directories to
     * be deleted: 100 Milliseconds works on WinXP OS. A value less than or
     * equal to zero means no delay.
     */
    private long delayForFilesInMilliseconds;

    /**
     * Delay for job start in milliseconds: A value less than or equal to zero
     * means no delay.
     */
    private long delayForJobStartInMilliseconds;

    /**
     * Height of slicer show dialog
     */
    private int dialogSlicerShowHeight;

    /**
     * Width of slicer show dialog
     */
    private int dialogSlicerShowWidth;

    /**
     * Custom height of dialog
     */
    private int customDialogHeight;

    /**
     * Custom width of dialog
     */
    private int customDialogWidth;
    
    /**
     * Height of single slicer show dialog
     */
    private int dialogSingleSlicerShowHeight;

    /**
     * Width of single slicer show dialog
     */
    private int dialogSingleSlicerShowWidth;

    /**
     * Height of through time slicer show dialog
     */
    private int dialogSimulationMovieSlicerShowHeight;

    /**
     * Width of through time slicer show dialog
     */
    private int dialogSimulationMovieSlicerShowWidth;

    /**
     * Height of value item edit dialog
     */
    private int dialogValueItemEditHeight;

    /**
     * Width of value item edit dialog
     */
    private int dialogValueItemEditWidth;

    /**
     * Height of text edit dialog
     */
    private int dialogTextEditHeight;

    /**
     * Width of text edit dialog
     */
    private int dialogTextEditWidth;

    /**
     * Height of value item show dialog
     */
    private int dialogValueItemShowHeight;

    /**
     * Width of value item show dialog
     */
    private int dialogValueItemShowWidth;

    /**
     * Height of table-data schemata manage dialog
     */
    private int dialogTableDataSchemataManageHeight;

    /**
     * Width of table-data schemata manage dialog
     */
    private int dialogTableDataSchemataManageWidth;

    /**
     * Height of value item matrix diagram dialog
     */
    private int dialogValueItemMatrixDiagramHeight;

    /**
     * Width of value item matrix diagram dialog
     */
    private int dialogValueItemMatrixDiagramWidth;

    /**
     * Height of structure edit dialog
     */
    private int dialogStructureEditHeight;

    /**
     * Width of structure edit dialog
     */
    private int dialogStructureEditWidth;

    /**
     * Height of peptide edit dialog
     */
    private int dialogPeptideEditHeight;

    /**
     * Width of peptide edit dialog
     */
    private int dialogPeptideEditWidth;

    /**
     * Height of compartment edit dialog
     */
    private int dialogCompartmentEditHeight;

    /**
     * Width of compartment edit dialog
     */
    private int dialogCompartmentEditWidth;

    /**
     * Full path for internal DPD Job directory
     */
    private String internalMFsimJobPath;

    /**
     * Full path for internal temp directory
     */
    private String internalTempPath;

    /**
     * Filename of current particle set from installation directory (DPD_source)
     */
    private String currentParticleSetFilename;

    /**
     * Index of first slice for simulation box slicer
     */
    private int firstSliceIndex;

    /**
     * Number of simulation box slicer wait steps
     */
    private int numberOfBoxWaitSteps;

    /**
     * Number of frame points for simulation box slicer
     */
    private int numberOfFramePointsSlicer;

    /**
     * Time step display for simulation box slicer. timeStepDisplaySlicer = 1:
     * Every time step will be displayed, timeStepDisplaySlicer = 2: Every
     * second time step will be displayed etc.
     */
    private int timeStepDisplaySlicer;

    /**
     * Array with step infos. NOTE: This is NOT made persistent!
     */
    private String[] stepInfoArray;

    /**
     * First step info. NOTE: This is NOT made persistent!
     */
    private String firstStepInfo;

    /**
     * Last step info. NOTE: This is NOT made persistent!
     */
    private String lastStepInfo;

    /**
     * Full path for simulation movie images
     */
    private String simulationMovieImagePath;

    /**
     * Full path for chart movie images
     */
    private String chartMovieImagePath;
    
    /**
     * Particle update for job input. True: Complete particle information is
     * always updated on edit of job input, false: Otherwise.
     */
    private boolean isParticleUpdateForJobInput;

    /**
     * True: Position step files are included in Job Result archive files,
     * false: Position step files are omitted in Job Result archive files
     */
    private boolean isJobResultArchiveStepFileInclusion;

    /**
     * Volume scaling for concentration calculation. True: Molecule numbers
     * are calculated with volume scaling, false: Molecule numbers are 
     * calculated without volume scaling (i.e. scale factors are all 1.0)
     */
    private boolean isVolumeScalingForConcentrationCalculation;

    /**
     * True: Job inputs included in Job Result display,
     * false: Job inputs are omitted in Job Result display
     */
    private boolean isJobInputInclusion;

    /**
     * True: Particle distribution analysis included in Job Result display,
     * false: Particle distribution analysis is omitted in Job Result display
     */
    private boolean isParticleDistributionInclusion;

    /**
     * True: Simulation steps are included in Job Result display,
     * false: Simulation steps are omitted in Job Result display
     */
    private boolean isSimulationStepInclusion;

    /**
     * True: Nearest-neighbour evaluation is included in Job Result display,
     * false: Nearest-neighbour evaluation is omitted in Job Result display
     */
    private boolean isNearestNeighborEvaluationInclusion;
    
    /**
     * True: Job Result archive files are generated parallel in background,
     * false: Job Result archive files are generated in sequential manner with
     * progress bar
     */
    private boolean isJobResultArchiveProcessParallelInBackground;

    /**
     * True: Job Result archive file is uncompressed, false: Otherwise (archive
     * file is compressed)
     */
    private boolean isJobResultArchiveFileUncompressed;

    /**
     * True: Jdpd log level EXCEPTIONS is used, false: All available Jdpd log 
     * levels are used
     */
    private boolean isJdpdLogLevelExceptions;

    /**
     * True: Compartment body volume is constant/preserved, false: Otherwise
     */
    private boolean isConstantCompartmentBodyVolume;
    
    /**
     * True: Simulation box slicer is used for simulation box display, false:
     * Jmol viewer is used for simulation box display
     */
    private boolean isSimulationBoxSlicer;

    /**
     * True: A slice only displays its corresponding particles, false: A slice
     * also displays the slices behind it.
     */
    private boolean isSingleSliceDisplay;

    /**
     * Slicer graphics mode
     */
    private GraphicsModeEnum slicerGraphicsMode;

    /**
     * Image storage mode
     */
    private ImageStorageEnum imageStorageMode;

    /**
     * Particle color display mode
     */
    private ParticleColorDisplayEnum particleColorDisplayMode;

    /**
     * Box view display
     */
    private SimulationBoxViewEnum boxViewDisplay;

    /**
     * True: Simulation box frame is displayed, false: Otherwise
     */
    private boolean isFrameDisplaySlicer;

    /**
     * Job input filter: After timestamp
     */
    private String jobInputFilterAfterTimestamp;

    /**
     * Job input filter: Before timestamp
     */
    private String jobInputFilterBeforeTimestamp;

    /**
     * Job input filter: Contains phrase
     */
    private String jobInputFilterContainsPhrase;

    /**
     * Job result filter: After timestamp
     */
    private String jobResultFilterAfterTimestamp;

    /**
     * Job result filter: Before timestamp
     */
    private String jobResultFilterBeforeTimestamp;

    /**
     * Job result filter: Contains phrase
     */
    private String jobResultFilterContainsPhrase;

    /**
     * Last selected path
     */
    private String lastSelectedPath;

    /**
     * MainFrame height
     */
    private int mainFrameHeight;

    /**
     * MainFrame maximum height
     */
    private int mainFrameMaximumHeight;

    /**
     * MainFrame maximum width
     */
    private int mainFrameMaximumWidth;

    /**
     * MainFrame width
     */
    private int mainFrameWidth;

    /**
     * Full path for DPD data directory
     */
    private String dpdDataPath;

    /**
     * Number of slices for simulation box slicer per view
     */
    private int numberOfSlicesPerView;

    /**
     * Shade power (integer values 1, 2, 3) for Jmol viewer
     */
    private int jmolShadePower;

    /**
     * Ambient light percentage (range 0 - 100) for Jmol viewer
     */
    private int jmolAmbientLightPercentage;

    /**
     * Diffuse light percentage (range 0 - 100) for Jmol viewer
     */
    private int jmolDiffuseLightPercentage;

    /**
     * Specular reflection exponent (range 0 - 10) for Jmol viewer
     */
    private int jmolSpecularReflectionExponent;

    /**
     * Specular reflection percentage (range 0 - 100) for Jmol viewer
     */
    private int jmolSpecularReflectionPercentage;

    /**
     * Specular reflection power (range 0 - 100) for Jmol viewer
     */
    private int jmolSpecularReflectionPower;

    /**
     * Number of parallel simulations
     */
    private int numberOfParallelSimulations;

    /**
     * Number of parallel slicers
     */
    private int numberOfParallelSlicers;

    /**
     * Number of parallel calculators
     */
    private int numberOfParallelCalculators;

    /**
     * Number of parallel particle position writers 
     */
    private int numberOfParallelParticlePositionWriters;

    /**
     * Number of after-decimal-separator digits for particle positions
     */
    private int numberOfAfterDecimalDigitsForParticlePositions;

    /**
     * Maximum number of position correction trials
     */
    private int maximumNumberOfPositionCorrectionTrials;
    
    /**
     * Quality of movies
     */
    private int movieQuality;

    /**
     * Basic preferences persistence file pathname
     */
    private String persistenceFilePathname;

    /**
     * Previous monomers
     */
    private LinkedList<String> previousMonomers;

    /**
     * Previous structures
     */
    private LinkedList<String> previousStructures;

    /**
     * Previous peptides
     */
    private LinkedList<String> previousPeptides;

    /**
     * Timer interval in milliseconds
     */
    private int timerIntervalInMilliseconds;

    /**
     * Maximum number of particles for graphical display
     */
    private int maximumNumberOfParticlesForGraphicalDisplay;

    /**
     * Minimum bond length in DPD units
     */
    private double minimumBondLengthDpd;

    /**
     * Number of steps for RDF calculation
     */
    private int numberOfStepsForRdfCalculation;

    /**
     * Number of volume bins
     */
    private int numberOfVolumeBins;
    
    /**
     * Number of trials for compartment related calculations
     */
    private int numberOfTrialsForCompartment;

    /**
     * Simulation box magnification percentage
     */
    private int simulationBoxMagnificationPercentage;

    /**
     * Number of spin steps
     */
    private int numberOfSpinSteps;

    /**
     * Animation speed in pictures per second
     */
    private int animationSpeed;

    /**
     * Minimum number of simulation box cells for parallelisation
     */
    private int numberOfSimulationBoxCellsforParallelization;

    /**
     * Minimum number of bonds for parallelisation
     */
    private int numberOfBondsforParallelization;

    /**
     * Number of steps for Job restart
     */
    private int numberOfAdditionalStepsForJobRestart;

    /**
     * Number of working JobResultExecution tasks.
     * NOTE: This number is not made persistent!
     */
    private int numberOfWorkingJobResultExecutionTasks;
    
    /**
     * Flag for shape of zoom volume:
     * True: Box volume shape, false: Ellipsoid volume shape.
     * NOTE: This flag is not made persistent!
     */
    private boolean isBoxVolumeShapeForZoom;

    /**
     * Movie slicer configuration (NOTE: NOT to be made persistent, is ONLY
     * used at runtime!)
     */
    private MovieSlicerConfiguration simulationMovieSlicerConfiguration;

    /**
     * Time step simulation box change info (NOTE: NOT to be made persistent, is
     * ONLY used at runtime!)
     */
    private SimulationBoxChangeInfo timeStepSimulationBoxChangeInfo;

    /**
     * True: Log event occurred, false: Otherwise
     * NOTE: NOT to be made persistent, is ONLY used at runtime!
     */
    private boolean isLogEvent;
    
    /**
     * String representation of background color of simulation box for slicer
     */
    private String simulationBoxBackgroundColorSlicer;

    /**
     * String representation of measurement color for slicer
     */
    private String measurementColorSlicer;

    /**
     * String representation of molecule selection color for slicer
     */
    private String moleculeSelectionColorSlicer;

    /**
     * String representation of frame color for slicer
     */
    private String frameColorSlicer;

    /**
     * String representation of background color of simulation box for Jmol
     * viewer
     */
    private String jmolSimulationBoxBackgroundColor;

    /**
     * String representation of background color of protein viewer
     */
    private String proteinViewerBackgroundColor;

    /**
     * Value item Container for schema value items
     */
    private ValueItemContainer schemaValueItemContainer;

    /**
     * Supplementary variable for saving angle for rotation around X axis
     */
    private int savedRotationAroundXaxisAngle;

    /**
     * Supplementary variable for saving angle for rotation around Y axis
     */
    private int savedRotationAroundYaxisAngle;

    /**
     * Supplementary variable for saving angle for rotation around Z axis
     */
    private int savedRotationAroundZaxisAngle;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private singleton constructor">
    /**
     * Singleton constructor
     */
    private Preferences() {
        // NOTE: Order of operations is essential!
        // <editor-fold defaultstate="collapsed" desc="1. Set this.dpdDataPath">
        this.dpdDataPath = ModelUtils.getDpdDataPath();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="2. Create DPD data directory">
        if (!ModelUtils.createDirectory(this.dpdDataPath)) {
            // <editor-fold defaultstate="collapsed" desc="Fatal error message">
            JOptionPane.showMessageDialog(null, String.format(ModelMessage.get("Error.NoDirectoryCreation"), "DPD data", this.dpdDataPath), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.exitApplication(-1);

            // </editor-fold>
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="3. Create job input directory">
        if (!ModelUtils.createDirectory(this.getJobInputPath())) {
            // <editor-fold defaultstate="collapsed" desc="Fatal error message">
            JOptionPane.showMessageDialog(null, String.format(ModelMessage.get("Error.NoDirectoryCreation"), "job input", this.getJobInputPath()), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.exitApplication(-1);

            // </editor-fold>
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="4. Create job result directory">
        if (!ModelUtils.createDirectory(this.getJobResultPath())) {
            // <editor-fold defaultstate="collapsed" desc="Fatal error message">
            JOptionPane.showMessageDialog(null, String.format(ModelMessage.get("Error.NoDirectoryCreation"), "job result", this.getJobResultPath()), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.exitApplication(-1);

            // </editor-fold>
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="5. Create and clear temporary directory">
        if (!ModelUtils.createDirectory(this.getTempPath())) {
            // <editor-fold defaultstate="collapsed" desc="Fatal error message">
            JOptionPane.showMessageDialog(null, String.format(ModelMessage.get("Error.NoDirectoryCreation"), "temporary", this.getTempPath()), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.exitApplication(-1);

            // </editor-fold>
        }
        // NOTE: Clear-operation may take seconds up to minutes, so it is performed with separated thread
        new FileDeletionTask((new File(this.getTempPath())).listFiles()).start();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="6. Create custom particles directory">
        if (!ModelUtils.createDirectory(this.getCustomParticlesPath())) {
            // <editor-fold defaultstate="collapsed" desc="Fatal error message">
            JOptionPane.showMessageDialog(null, String.format(ModelMessage.get("Error.NoDirectoryCreation"), "custom particles", this.getCustomParticlesPath()), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Exit with error: -1">
            ModelUtils.exitApplication(-1);

            // </editor-fold>
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="7. Set this.persistenceFilePathname">
        this.persistenceFilePathname = ModelUtils.getBasicPreferencesFilePathname(this.dpdDataPath);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="8. Initialize persistent preferences">
        // FIRST set initial default values ...
        this.setInitialDefaultValues();
        // ... and THEN overwrite with persistent information (NOTE: This procedure is compatible to older versions of persistent preferences since preference items are stored flat and separated):
        this.readPersistenceXmlInformation();
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public singleton instance method">
    /**
     * Singleton initialisation and instance method
     *
     * @return Preferences instance
     */
    public static Preferences getInstance() {
        return Preferences.basicPreference;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Editable preferences related methods">
    // <editor-fold defaultstate="collapsed" desc="-- General methods">
    // <editor-fold defaultstate="collapsed" desc="--- Get all">
    /**
     * Returns ValueItemContainer with all editable preferences (may be returned
     * after edit process with method setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences
     */
    public ValueItemContainer getAllEditablePreferencesValueItemContainer() {
        ValueItem tmpValueItem;
        String[] tmpNodeNames;
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        int tmpVerticalPosition = 0;
        // <editor-fold defaultstate="collapsed" desc="Directories">
        tmpVerticalPosition = this.addDirectoriesEditablePreferencesValueItems(tmpValueItemContainer, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Job input filter">
        tmpVerticalPosition = this.addJobInputFilterEditablePreferencesValueItems(tmpValueItemContainer, tmpVerticalPosition);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Job result filter">
        tmpVerticalPosition = this.addJobResultFilterEditablePreferencesValueItems(tmpValueItemContainer, tmpVerticalPosition);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Compartment graphics settings">
        tmpVerticalPosition = this.addCompartmentGraphicsSettingsEditablePreferencesValueItems(tmpValueItemContainer, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Compartment calculation">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.CompartmentCalculation")};
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultNumberOfTrialsForCompartment()), 0,
                ModelDefinitions.MINIMUM_NUMBER_OF_TRIALS_FOR_COMPARTMENT, Integer.MAX_VALUE));
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_TRIALS_FOR_COMPARTMENT.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.CompartmentCalculation.NumberOfTrialsForCompartment.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.CompartmentCalculation.NumberOfTrialsForCompartment"));
        tmpValueItem.setValue(String.valueOf(this.numberOfTrialsForCompartment));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box display settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox")};
        tmpVerticalPosition = this.addSimulationBoxDisplayEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box particle color display">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.ParticleColorDisplayMode")};
        tmpVerticalPosition = this.addParticleColorDisplayPreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box animation settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.AnimationSettings")};
        tmpVerticalPosition = this.addAnimationEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box movie settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.MovieSettings")};
        tmpVerticalPosition = this.addSimulationMovieEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box particle shift settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.ParticleShift")};
        tmpVerticalPosition = this.addParticleShiftEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box slicer rotation settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.RotationOfSimulationBoxSlicerSettings")};
        tmpVerticalPosition = this.addSlicerRotationEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box slicer shift settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.ShiftsOfSimulationBoxSlicerSettings")};
        tmpVerticalPosition = this.addSlicerShiftEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box slicer graphics settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings")};
        tmpVerticalPosition = this.addSlicerEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box slicer time steps settings">
        tmpVerticalPosition = this.addSlicerTimeStepsEditablePrefencesValueItems(tmpValueItemContainer, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box slicer spin steps settings">
        tmpVerticalPosition = this.addSlicerSpinStepsEditablePrefencesValueItems(tmpValueItemContainer, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="JMol simulation box viewer graphics settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings")};
        tmpVerticalPosition = this.addJmolViewerEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Protein viewer graphics settings">
        tmpVerticalPosition = this.addProteinViewerEditablePreferencesValueItems(tmpValueItemContainer, tmpVerticalPosition);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="RDF calculation">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.RdfCalculation")};
        // <editor-fold defaultstate="collapsed" desc="- Number of steps for RDF calculation">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultNumberOfStepsForRdfCalculation()), 0,
                ModelDefinitions.MINIMUM_NUMBER_OF_STEPS_FOR_RDF_CALCULATION, Integer.MAX_VALUE));
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_STEPS_FOR_RDF_CALCULATION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.RdfCalculation.NumberOfStepsForRdfCalculation.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.RdfCalculation.NumberOfStepsForRdfCalculation"));
        tmpValueItem.setValue(String.valueOf(this.numberOfStepsForRdfCalculation));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Parallel computing settings">
        tmpVerticalPosition = this.addParallelComputingEditablePreferencesValueItems(tmpValueItemContainer, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Job restart settings">
        tmpVerticalPosition = this.addNumberOfAdditionalStepsForJobRestartValueItem(tmpValueItemContainer, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Job Result settings">
        tmpVerticalPosition = this.addJobResultSettingsEditablePreferencesValueItems(tmpValueItemContainer, tmpVerticalPosition);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Job Result archive settings">
        tmpVerticalPosition = this.addJobResultArchiveEditablePreferencesValueItems(tmpValueItemContainer, tmpVerticalPosition);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Miscellaneous">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.Miscellaneous")};
        // <editor-fold defaultstate="collapsed" desc="- Volume scaling for concentration calculation">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                ModelMessage.get("Preferences.Miscellaneous.IsVolumeScalingForConcentrationCalculation.True"), 
                new String[]
                {
                    ModelMessage.get("Preferences.Miscellaneous.IsVolumeScalingForConcentrationCalculation.True"),
                    ModelMessage.get("Preferences.Miscellaneous.IsVolumeScalingForConcentrationCalculation.False")
                }
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.IS_VOLUME_SCALING_FOR_CONCENTRATION_CALCULATION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.IsVolumeScalingForConcentrationCalculation.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.IsVolumeScalingForConcentrationCalculation"));
        if (this.isVolumeScalingForConcentrationCalculation) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.Miscellaneous.IsVolumeScalingForConcentrationCalculation.True"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.Miscellaneous.IsVolumeScalingForConcentrationCalculation.False"));
        }
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Minimum bond length in DPD units">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(
            new ValueItemDataTypeFormat(
                String.valueOf(this.getDefaultMinimumBondLengthDpd()), 
                3,
                ModelDefinitions.MINIMUM_BOND_LENGTH_DPD_MINIMUM, 
                ModelDefinitions.MINIMUM_BOND_LENGTH_DPD_MAXIMUM
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.MINIMUM_BOND_LENGTH_DPD.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.MinimumBondLengthDpd.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.MinimumBondLengthDpd"));
        tmpValueItem.setValue(String.valueOf(this.minimumBondLengthDpd));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Maximum number of particles for graphical display">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultMaximumNumberOfParticlesForGraphicalDisplay()), 0,
                ModelDefinitions.MINIMUM_NUMBER_OF_PARTICLES_FOR_DISPLAY, Integer.MAX_VALUE));
        tmpValueItem.setName(PreferenceEditableEnum.MAXIMUM_NUMBER_OF_PARTICLES_FOR_GRAPHICAL_DISPLAY.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.MaximumNumberOfParticlesForGraphicalDisplay.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.MaximumNumberOfParticlesForGraphicalDisplay"));
        tmpValueItem.setValue(String.valueOf(this.maximumNumberOfParticlesForGraphicalDisplay));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Timer interval in milliseconds">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultTimerIntervalInMilliseconds()), 0, ModelDefinitions.MINIMUM_TIMER_INTERVAL_IN_MILLISECONDS,
                ModelDefinitions.MAXIMUM_TIMER_INTERVAL_IN_MILLISECONDS));
        tmpValueItem.setName(PreferenceEditableEnum.TIMER_INTERVALL_IN_MILLISECONDS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.TimerInterval.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.TimerInterval"));
        tmpValueItem.setValue(String.valueOf(this.timerIntervalInMilliseconds));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Delay for file operations in milliseconds">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultDelayForFilesInMilliseconds()), 0, ModelDefinitions.MINIMUM_DELAY_FOR_JOB_START_IN_MILLISECONDS,
                ModelDefinitions.MAXIMUM_DELAY_FOR_JOB_START_IN_MILLISECONDS));
        tmpValueItem.setName(PreferenceEditableEnum.DELAY_FOR_FILES_IN_MILLISECONDS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.DelayFileOperations.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.DelayFileOperations"));
        tmpValueItem.setValue(String.valueOf(this.delayForFilesInMilliseconds));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Delay for job start in milliseconds">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultDelayForJobStartInMilliseconds()), 0, ModelDefinitions.MINIMUM_DELAY_FOR_JOB_START_IN_MILLISECONDS,
                ModelDefinitions.MAXIMUM_DELAY_FOR_JOB_START_IN_MILLISECONDS));
        tmpValueItem.setName(PreferenceEditableEnum.DELAY_FOR_JOB_START_IN_MILLISECONDS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.DelayJobStart.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.DelayJobStart"));
        tmpValueItem.setValue(String.valueOf(this.delayForJobStartInMilliseconds));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Jdpd log level exceptions">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                ModelMessage.get("Preferences.Miscellaneous.JdpdLogLevelExceptions.True"), 
                new String[] {
                    ModelMessage.get("Preferences.Miscellaneous.JdpdLogLevelExceptions.True"), 
                    ModelMessage.get("Preferences.Miscellaneous.JdpdLogLevelExceptions.False")
                }
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.IS_JDPD_LOG_LEVEL_EXCEPTIONS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.JdpdLogLevelExceptions.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.IsJdpdLogLevelExceptions"));
        if (this.isJdpdLogLevelExceptions) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.Miscellaneous.JdpdLogLevelExceptions.True"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.Miscellaneous.JdpdLogLevelExceptions.False"));
        }
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Number of after-decimal-separator digits for particle positions">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                String.valueOf(this.getDefaultNumberOfAfterDecimalDigitsForParticlePositions()), 
                ModelUtils.getNumberStringsForInterval(ModelDefinitions.MINIMUM_NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS, ModelDefinitions.MAXIMUM_NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS))
        );
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.NumberOfAfterDecimalDigitsForParticlePositions.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.NumberOfAfterDecimalDigitsForParticlePositions"));
        tmpValueItem.setValue(String.valueOf(this.numberOfAfterDecimalDigitsForParticlePositions));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Maximum number of position correction trials">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(
            new ValueItemDataTypeFormat(
                String.valueOf(this.getDefaultMaximumNumberOfPositionCorrectionTrials()),
                0,
                ModelDefinitions.MINIMUM_MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS,
                Double.POSITIVE_INFINITY
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.MaximumNumberOfPositionCorrectionTrials.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.MaximumNumberOfPositionCorrectionTrials"));
        tmpValueItem.setValue(String.valueOf(this.maximumNumberOfPositionCorrectionTrials));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Custom dialog size">
        tmpVerticalPosition = this.addCustomDialogSizePreferencesValueItem(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        // </editor-fold>
        // </editor-fold>
        return tmpValueItemContainer;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Get partial">
    /**
     * Returns ValueItemContainer with editable preferences of compartment
     * graphics settings (may be returned after edit process with method
     * setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences of compartment
     * graphics settings
     */
    public ValueItemContainer getCompartmentGraphicsSettingsEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        this.addCompartmentGraphicsSettingsEditablePreferencesValueItems(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences of directories (may
     * be returned after edit process with method setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences of directories
     */
    public ValueItemContainer getDirectoriesEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        this.addDirectoriesEditablePreferencesValueItems(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences of job input filter
     * (may be returned after edit process with method setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences of job input filter
     */
    public ValueItemContainer getJobInputFilterEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        this.addJobInputFilterEditablePreferencesValueItems(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with all editable preferences of job result
     * filter (may be returned after edit process with method
     * setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences of job result filter
     */
    public ValueItemContainer getJobResultFilterEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        this.addJobResultFilterEditablePreferencesValueItems(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences of simulation box
     * slicer (may be returned after edit process with method
     * setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences of simulation box
     * slicer
     */
    public ValueItemContainer getSlicerEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings")};
        this.addSlicerEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences of JMol simulation
     * box viewer (may be returned after edit process with method
     * setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences of JMol simulation
     * box viewer
     */
    public ValueItemContainer getJmolViewerEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(null);
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings")};
        this.addJmolViewerEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences of protein viewer
     * (may be returned after edit process with method setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences of protein viewer
     */
    public ValueItemContainer getProteinViewerEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(null);
        this.addProteinViewerEditablePreferencesValueItems(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for animation in
     * simulation box slicer (may be returned after edit process with method
     * setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences for animation in
     * simulation box slicer
     */
    public ValueItemContainer getAnimationEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.AnimationSettings")};
        this.addAnimationEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences of simulation movie 
     * (may be returned after edit process with method setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences of simulation movie
     */
    public ValueItemContainer getSimulationMovieEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.MovieSettings")};
        this.addSimulationMovieEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences of chart movie 
     * (may be returned after edit process with method setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences of chart movie
     */
    public ValueItemContainer getChartMovieEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.MovieSettings")};
        this.addChartMovieEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for shift and 
     * rotation of simulation box (may be returned after edit process with 
     * method setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences for shift and 
     * rotation of simulation box
     */
    public ValueItemContainer getRotationAndShiftEditablePreferencesValueItemContainer() {
        String[] tmpNodeNames;
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        int tmpVerticalPosition = 0;
        // <editor-fold defaultstate="collapsed" desc="Simulation box particle shift settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.ParticleShift")};
        tmpVerticalPosition = this.addParticleShiftEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box slicer rotation settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.RotationOfSimulationBoxSlicerSettings")};
        tmpVerticalPosition = this.addSlicerRotationEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simulation box slicer shift settings">
        tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.ShiftsOfSimulationBoxSlicerSettings")};
        tmpVerticalPosition = this.addSlicerShiftEditablePreferencesValueItems(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        // </editor-fold>
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with additional number of steps for Job restart
     * (may be returned after edit process with method setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences for job restart
     * settings
     */
    public ValueItemContainer getNumberOfAdditionalStepsForJobRestartValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        this.addNumberOfAdditionalStepsForJobRestartValueItem(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for Job Result
     * archive settings
     *
     * @return ValueItemContainer with editable preferences for Job Result
     * archive settings
     */
    public ValueItemContainer getJobResultArchiveEditablePrefencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        this.addJobResultArchiveEditablePreferencesValueItems(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for Job Result
     * settings
     *
     * @return ValueItemContainer with editable preferences for Job Result
     * settings
     */
    public ValueItemContainer getJobResultSettingsEditablePrefencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        this.addJobResultSettingsEditablePreferencesValueItems(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for simulation box
     * slicer time steps
     *
     * @return ValueItemContainer with editable preferences for simulation box
     * slicer time steps
     */
    public ValueItemContainer getSlicerTimeStepsEditablePrefencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        this.addSlicerTimeStepsEditablePrefencesValueItems(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for simulation box
     * slicer time steps and number of volume bins
     *
     * @return ValueItemContainer with editable preferences for simulation box
     * slicer time steps and number of volume bins
     */
    public ValueItemContainer getSlicerTimeStepsAndVolumeBinsEditablePrefencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        int tmpVerticalPosition = 0;
        tmpVerticalPosition = this.addSlicerTimeStepsEditablePrefencesValueItems(tmpValueItemContainer, tmpVerticalPosition);
        tmpVerticalPosition = this.addNumberOfVolumeBinsValueItem(tmpValueItemContainer, tmpVerticalPosition);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for simulation box
     * slicer spin steps
     *
     * @return ValueItemContainer with editable preferences for simulation box
     * slicer spin steps
     */
    public ValueItemContainer getSlicerSpinStepsEditablePrefencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        this.addSlicerSpinStepsEditablePrefencesValueItems(tmpValueItemContainer, 0);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for particle set
     * settings (may be returned after edit process with method
     * setEditablePreferences())
     *
     * @return ValueItemContainer with editable preferences for particle set
     * settings
     */
    public ValueItemContainer getParticleSetEditablePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);

        String[] tmpNodeNames = new String[]{ModelMessage.get("ParticleEdit.Root")};

        LinkedList<String> tmpAvailableParticleSetFilenamesList = new LinkedList<>();
        this.fileUtilityMethods.fillFilenamesWithPrefix(tmpAvailableParticleSetFilenamesList, this.getDpdSourceParticlesPath(), ModelDefinitions.PARTICLE_SET_FILE_PREFIX);
        this.fileUtilityMethods.fillFilenamesWithPrefix(tmpAvailableParticleSetFilenamesList, this.getCustomParticlesPath(), ModelDefinitions.PARTICLE_SET_FILE_PREFIX);

        String[] tmpAvailableParticleSetFilenames = tmpAvailableParticleSetFilenamesList.toArray(new String[0]);
        Arrays.sort(tmpAvailableParticleSetFilenames);

        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(Preferences.getInstance().getCurrentParticleSetFilename(), tmpAvailableParticleSetFilenames));
        tmpValueItem.setName(PreferenceEditableEnum.CURRENT_PARTICLE_SET_FILENAME.name());
        tmpValueItem.setDescription(ModelMessage.get("ParticleEdit.ParticleSet.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("ParticleEdit.ParticleSet"));
        tmpValueItem.setValue(Preferences.getInstance().getCurrentParticleSetFilename());
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(0);
        tmpValueItemContainer.addValueItem(tmpValueItem);

        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for zoom-volume settings
     *
     * @return ValueItemContainer with editable preferences for zoom-volume settings
     */
    public ValueItemContainer getVolumeSettingsValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        int tmpVerticalPosition = 0;
        tmpVerticalPosition = this.addNumberOfVolumeBinsValueItem(tmpValueItemContainer, tmpVerticalPosition);
        return tmpValueItemContainer;
    }

    /**
     * Returns ValueItemContainer with editable preferences for custom dialog size
     *
     * @return ValueItemContainer with editable preferences for custom dialog size
     */
    public ValueItemContainer getCustomDialogSizePreferencesValueItemContainer() {
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(ModelDefinitions.PREFERENCE_EDITABLE_VALUE_ITEM_UPDATE);
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.Miscellaneous")};
        int tmpVerticalPosition = 0;
        tmpVerticalPosition = this.addCustomDialogSizePreferencesValueItem(tmpValueItemContainer, tmpNodeNames, tmpVerticalPosition);
        return tmpValueItemContainer;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="--- Set">
    /**
     * Sets editable preferences
     *
     * @param aValueItemContainer ValueItemContainer with changed preferences
     */
    public void setEditablePreferences(ValueItemContainer aValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null || aValueItemContainer.getSize() == 0) {
            return;
        }

        // </editor-fold>
        ValueItem[] tmpValueItems = aValueItemContainer.getValueItemsOfContainer();
        // Change detection
        boolean tmpHasChanged = false;
        for (ValueItem tmpSingleValueItem : tmpValueItems) {
            PreferenceEditableEnum tmpEditablePreferenceItem = PreferenceEditableEnum.toPreferenceEditableEnum(tmpSingleValueItem.getName());
            switch (tmpEditablePreferenceItem) {
                case INTERNAL_MFSIM_JOB_PATH:
                    if (this.setInternalMFsimJobPath(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case INTERNAL_TEMP_PATH:
                    if (this.setInternalTempPath(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case SIMULATION_MOVIE_IMAGE_PATH:
                    if (this.setSimulationMovieImagePath(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case CHART_MOVIE_IMAGE_PATH:
                    if (this.setChartMovieImagePath(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case CURRENT_PARTICLE_SET_FILENAME:
                    if (this.setCurrentParticleSetFilename(tmpSingleValueItem.getValue())) {
                        StandardParticleInteractionData.getInstance().reset();
                        tmpHasChanged = true;
                    }
                    break;
                case TIMER_INTERVALL_IN_MILLISECONDS:
                    if (this.setTimerIntervalInMilliseconds(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case MINIMUM_BOND_LENGTH_DPD:
                    if (this.setMinimumBondLengthDpd(tmpSingleValueItem.getValueAsDouble())) {
                        tmpHasChanged = true;
                    }
                    break;
                case MAXIMUM_NUMBER_OF_PARTICLES_FOR_GRAPHICAL_DISPLAY:
                    if (this.setMaximumNumberOfParticlesForGraphicalDisplay(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_STEPS_FOR_RDF_CALCULATION:
                    if (this.setNumberOfStepsForRdfCalculation(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_TRIALS_FOR_COMPARTMENT:
                    if (this.setNumberOfTrialsForCompartment(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case ANIMATION_SPEED:
                    if (this.setAnimationSpeed(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION:
                    if (this.setNumberOfSimulationBoxCellsforParallelization(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_BONDS_FOR_PARALLELIZATION:
                    if (this.setNumberOfBondsforParallelization(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_STEPS_FOR_JOB_RESTART:
                    if (this.setNumberOfAdditionalStepsForJobRestart(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case SIMULATION_BOX_MAGNIFICATION_PERCENTAGE:
                    if (this.setSimulationBoxMagnificationPercentage(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_SPIN_STEPS:
                    if (this.setNumberOfSpinSteps(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case DELAY_FOR_FILES_IN_MILLISECONDS:
                    if (this.setDelayForFilesInMilliseconds(tmpSingleValueItem.getValueAsLong())) {
                        tmpHasChanged = true;
                    }
                    break;
                case DELAY_FOR_JOB_START_IN_MILLISECONDS:
                    if (this.setDelayForJobStartInMilliseconds(tmpSingleValueItem.getValueAsLong())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JOB_INPUT_FILTER_AFTER_TIMESTAMP:
                    if (this.setJobInputFilterAfterTimestamp(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JOB_INPUT_FILTER_BEFORE_TIMESTAMP:
                    if (this.setJobInputFilterBeforeTimestamp(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JOB_INPUT_FILTER_CONTAINS_PHRASE:
                    if (this.setJobInputFilterContainsPhrase(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JOB_RESULT_FILTER_AFTER_TIMESTAMP:
                    if (this.setJobResultFilterAfterTimestamp(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JOB_RESULT_FILTER_BEFORE_TIMESTAMP:
                    if (this.setJobResultFilterBeforeTimestamp(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JOB_RESULT_FILTER_CONTAINS_PHRASE:
                    if (this.setJobResultFilterContainsPhrase(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JPEG_IMAGE_QUALITY:
                    if (this.setJpegImageQuality((float) tmpSingleValueItem.getValueAsDouble())) {
                        tmpHasChanged = true;
                    }
                    break;
                case COLOR_TRANSPARENCY_COMPARTMENT:
                    if (this.setColorTransparencyCompartment((float) tmpSingleValueItem.getValueAsDouble())) {
                        tmpHasChanged = true;
                    }
                    break;
                case COLOR_GRADIENT_ATTENUATION_COMPARTMENT:
                    if (this.setColorGradientAttenuationCompartment(tmpSingleValueItem.getValueAsDouble())) {
                        tmpHasChanged = true;
                    }
                    break;
                case COLOR_GRADIENT_ATTENUATION_SLICER:
                    if (this.setColorGradientAttenuationSlicer(tmpSingleValueItem.getValueAsDouble())) {
                        tmpHasChanged = true;
                    }
                    break;
                case SPECULAR_WHITE_ATTENUATION_SLICER:
                    if (this.setSpecularWhiteAttenuationSlicer(tmpSingleValueItem.getValueAsDouble())) {
                        tmpHasChanged = true;
                    }
                    break;
                case COLOR_SHAPE_ATTENUATION_COMPARTMENT:
                    if (this.setDepthAttenuationCompartment(tmpSingleValueItem.getValueAsDouble())) {
                        tmpHasChanged = true;
                    }
                    break;
                case COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR:
                    if (this.setCompartmentBodyChangeResponseFactor(tmpSingleValueItem.getValueAsDouble())) {
                        tmpHasChanged = true;
                    }
                    break;
                case MAX_SELECTED_MOLECULE_NUMBER_SLICER:
                    if (this.setMaxSelectedMoleculeNumberSlicer(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case DEPTH_ATTENUATION_SLICER:
                    if (this.setDepthAttenuationSlicer(tmpSingleValueItem.getValueAsDouble())) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION:
                    if (this.setJobResultArchiveStepFileInclusion(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveStepFileInclusion.True")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_VOLUME_SCALING_FOR_CONCENTRATION_CALCULATION:
                    if (this.setVolumeScalingForConcentrationCalculation(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.Miscellaneous.IsVolumeScalingForConcentrationCalculation.True")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_JOB_INPUT_INCLUSION:
                    if (this.setJobInputInclusion(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.JobResultDisplay.IsJobInputInclusion.True")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_PARTICLE_DISTRICUTION_INCLUSION:
                    if (this.setParticleDistributionInclusion(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.JobResultDisplay.IsParticleDistributionInclusion.True")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_SIMULATION_STEP_INCLUSION:
                    if (this.setSimulationStepInclusion(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.JobResultDisplay.IsSimulationStepInclusion.True")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION:
                    if (this.setNearestNeighborEvaluationInclusion(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.JobResultDisplay.IsNearestNeighborEvaluationInclusion.True")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND:
                    if (this.setJobResultArchiveProcessParallelInBackground(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveProcessParallelInBackground.True")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED:
                    if (this.setJobResultArchiveFileUncompressed(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveFileCompression.False")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_JDPD_LOG_LEVEL_EXCEPTIONS:
                    if (this.setJdpdLogLevelExceptions(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.Miscellaneous.JdpdLogLevelExceptions.True")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_CONSTANT_COMPARTMENT_BODY_VOLUME:
                    if (this.setConstantCompartmentBodyVolumeFlag(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.GraphicsSettings.ConstantCompartmentBodyVolumeFlag.True")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_SIMULATION_BOX_SLICER:
                    if (this.setSimulationBoxSlicer(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.Slicer")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_SLICES:
                    if (this.setNumberOfSlicesPerView(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JMOL_SHADE_POWER:
                    if (this.setJmolShadePower(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JMOL_AMBIENT_LIGHT_PERCENTAGE:
                    if (this.setJmolAmbientLightPercentage(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JMOL_DIFFUSE_LIGHT_PERCENTAGE:
                    if (this.setJmolDiffuseLightPercentage(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JMOL_SPECULAR_REFLECTION_EXPONENT:
                    if (this.setJmolSpecularReflectionExponent(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JMOL_SPECULAR_REFLECTION_PERCENTAGE:
                    if (this.setJmolSpecularReflectionPercentage(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JMOL_SPECULAR_REFLECTION_POWER:
                    if (this.setJmolSpecularReflectionPower(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case FIRST_SLICE_INDEX:
                    if (this.setFirstSliceIndex(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_ZOOM_VOLUME_BINS:
                    if (this.setNumberOfVolumeBins(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_FRAME_POINTS_SLICER:
                    if (this.setNumberOfFramePointsSlicer(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case TIME_STEP_DISPLAY_SLICER:
                    if (this.setTimeStepDisplaySlicer(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_PARALLEL_SIMULATIONS:
                    if (this.setNumberOfParallelSimulations(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_PARALLEL_SLICERS:
                    if (this.setNumberOfParallelSlicers(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_PARALLEL_CALCULATORS:
                    if (this.setNumberOfParallelCalculators(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_PARALLEL_PARTICLE_POSITION_WRITERS:
                    if (this.setNumberOfParallelParticlePositionWriters(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS:
                    if (this.setNumberOfAfterDecimalDigitsForParticlePositions(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS:
                    if (this.setMaximumNumberOfPositionCorrectionTrials(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case MOVIE_QUALITY:
                    if (this.setMovieQuality(tmpSingleValueItem.getValueAsInt())) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_SINGLE_SLICE_DISPLAY:
                    if (this.setSingleSliceDisplay(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SingleSliceDisplay.BackgroundNo")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case SLICER_GRAPHICS_MODE:
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.PixelAll"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setSlicerGraphicsMode(GraphicsModeEnum.PIXEL_ALL);
                        } else {
                            this.setSlicerGraphicsMode(GraphicsModeEnum.PIXEL_ALL);
                        }
                    }
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.PixelFinal"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setSlicerGraphicsMode(GraphicsModeEnum.PIXEL_FINAL);
                        } else {
                            this.setSlicerGraphicsMode(GraphicsModeEnum.PIXEL_FINAL);
                        }
                    }
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.BufferedImageAll"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setSlicerGraphicsMode(GraphicsModeEnum.BUFFERED_IMAGE_ALL);
                        } else {
                            this.setSlicerGraphicsMode(GraphicsModeEnum.BUFFERED_IMAGE_ALL);
                        }
                    }
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.BufferedImageFinal"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setSlicerGraphicsMode(GraphicsModeEnum.BUFFERED_IMAGE_FINAL);
                        } else {
                            this.setSlicerGraphicsMode(GraphicsModeEnum.BUFFERED_IMAGE_FINAL);
                        }
                    }
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.VolatileImageAll"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setSlicerGraphicsMode(GraphicsModeEnum.VOLATILE_IMAGE_ALL);
                        } else {
                            this.setSlicerGraphicsMode(GraphicsModeEnum.VOLATILE_IMAGE_ALL);
                        }
                    }
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.VolatileImageFinal"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setSlicerGraphicsMode(GraphicsModeEnum.VOLATILE_IMAGE_FINAL);
                        } else {
                            this.setSlicerGraphicsMode(GraphicsModeEnum.VOLATILE_IMAGE_FINAL);
                        }
                    }
                    break;
                case SIMULATION_BOX_BACKGROUND_COLOR_SLICER:
                    if (this.setSimulationBoxBackgroundColorSlicer(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case MEASUREMENT_COLOR_SLICER:
                    if (this.setMeasurementColorSlicer(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case MOLECULE_SELECTION_COLOR_SLICER:
                    if (this.setMoleculeSelectionColorSlicer(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case FRAME_COLOR_SLICER:
                    if (this.setFrameColorSlicer(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case JMOL_SIMULATION_BOX_BACKGROUND_COLOR:
                    if (this.setJmolSimulationBoxBackgroundColor(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case PROTEIN_VIEWER_BACKGROUND_COLOR:
                    if (this.setProteinViewerBackgroundColor(tmpSingleValueItem.getValue())) {
                        tmpHasChanged = true;
                    }
                    break;
                case IMAGE_STORAGE_MODE:
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.HarddiskCompressed"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setImageStorageMode(ImageStorageEnum.HARDDISK_COMPRESSED);
                        } else {
                            this.setImageStorageMode(ImageStorageEnum.HARDDISK_COMPRESSED);
                        }
                    }
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.MemoryCompressed"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setImageStorageMode(ImageStorageEnum.MEMORY_COMPRESSED);
                        } else {
                            this.setImageStorageMode(ImageStorageEnum.MEMORY_COMPRESSED);
                        }
                    }
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.MemoryUncompressed"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setImageStorageMode(ImageStorageEnum.MEMORY_UNCOMPRESSED);
                        } else {
                            this.setImageStorageMode(ImageStorageEnum.MEMORY_UNCOMPRESSED);
                        }
                    }
                    break;
                case PARTICLE_COLOR_DISPLAY_MODE:
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.ParticleColorDisplayMode.ParticleColorMode"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setParticleColorDisplayMode(ParticleColorDisplayEnum.PARTICLE_COLOR_MODE);
                        } else {
                            this.setParticleColorDisplayMode(ParticleColorDisplayEnum.PARTICLE_COLOR_MODE);
                        }
                    }
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.ParticleColorDisplayMode.MoleculeColorMode"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setParticleColorDisplayMode(ParticleColorDisplayEnum.MOLECULE_COLOR_MODE);
                        } else {
                            this.setParticleColorDisplayMode(ParticleColorDisplayEnum.MOLECULE_COLOR_MODE);
                        }
                    }
                    if (tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.ParticleColorDisplayMode.MoleculeParticleColorMode"))) {
                        if (!tmpHasChanged) {
                            tmpHasChanged = this.setParticleColorDisplayMode(ParticleColorDisplayEnum.MOLECULE_PARTICLE_COLOR_MODE);
                        } else {
                            this.setParticleColorDisplayMode(ParticleColorDisplayEnum.MOLECULE_PARTICLE_COLOR_MODE);
                        }
                    }
                    break;
                case BOX_VIEW_DISPLAY:
                    if (this.setBoxViewDisplay(SimulationBoxViewEnum.toSimulationBoxViewEnum(tmpSingleValueItem.getValue()))) {
                        tmpHasChanged = true;
                    }
                    break;
                case IS_FRAME_DISPLAY_SLICER:
                    if (this.setFrameDisplaySlicer(tmpSingleValueItem.getValue().equals(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameDisplaySlicer.On")))) {
                        tmpHasChanged = true;
                    }
                    break;
                case SHIFTS_SLICER:
                    if (this.setXshiftInPixelSlicer(tmpSingleValueItem.getValueAsInt(0, 0))) {
                        tmpHasChanged = true;
                    }
                    if (this.setYshiftInPixelSlicer(tmpSingleValueItem.getValueAsInt(0, 1))) {
                        tmpHasChanged = true;
                    }
                    break;
                case CUSTOM_DIALOG_SIZE:
                    if (this.setCustomDialogWidth(tmpSingleValueItem.getValueAsInt(0, 0))) {
                        tmpHasChanged = true;
                    }
                    if (this.setCustomDialogHeight(tmpSingleValueItem.getValueAsInt(0, 1))) {
                        tmpHasChanged = true;
                    }
                    break;
                case ROTATION_ANGLES:
                    if (this.setRotationAroundXaxisAngle(tmpSingleValueItem.getValueAsInt(0, 0))) {
                        tmpHasChanged = true;
                    }
                    if (this.setRotationAroundYaxisAngle(tmpSingleValueItem.getValueAsInt(0, 1))) {
                        tmpHasChanged = true;
                    }
                    if (this.setRotationAroundZaxisAngle(tmpSingleValueItem.getValueAsInt(0, 2))) {
                        tmpHasChanged = true;
                    }
                    break;
                case PARTICLE_SHIFTS:
                    if (this.setParticleShiftX(tmpSingleValueItem.getValueAsInt(0, 0))) {
                        tmpHasChanged = true;
                    }
                    if (this.setParticleShiftY(tmpSingleValueItem.getValueAsInt(0, 1))) {
                        tmpHasChanged = true;
                    }
                    if (this.setParticleShiftZ(tmpSingleValueItem.getValueAsInt(0, 2))) {
                        tmpHasChanged = true;
                    }
                    break;
                case STEP_INFO_ARRAY_SLICER:
                    this.firstStepInfo = tmpSingleValueItem.getValue(0, 0);
                    this.lastStepInfo = tmpSingleValueItem.getValue(0, 1);
                    break;
                case RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION:
                    if (this.setRadialGradientPaintRadiusMagnification(tmpSingleValueItem.getValueAsFloat())) {
                        tmpHasChanged = true;
                    }
                    break;
                case SPECULAR_WHITE_SIZE_SLICER:
                    if (this.setSpecularWhiteSizeSlicer(tmpSingleValueItem.getValueAsFloat())) {
                        tmpHasChanged = true;
                    }
                    break;
                case RADIAL_GRADIENT_PAINT_FOCUS_FACTORS:
                    if (this.setRadialGradientPaintFocusFactorX(tmpSingleValueItem.getValueAsFloat(0, 0))) {
                        tmpHasChanged = true;
                    }
                    if (this.setRadialGradientPaintFocusFactorY(tmpSingleValueItem.getValueAsFloat(0, 1))) {
                        tmpHasChanged = true;
                    }
                    break;
                case UNDEFINED:
                    // Undefined: Do nothing!
                    break;
            }
        }
        // React on changes with tmpHasChanged if necessary
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Specific methods">
    /**
     * Returns value item for this.particleColorDisplayMode
     *
     * @return Value item for this.particleColorDisplayMode
     */
    public ValueItem getParticleColorDisplayModeValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("Preferences.ParticleColorDisplayMode.MoleculeColorMode"), new String[]{
            ModelMessage.get("Preferences.ParticleColorDisplayMode.MoleculeColorMode"),
            ModelMessage.get("Preferences.ParticleColorDisplayMode.ParticleColorMode"),
            ModelMessage.get("Preferences.ParticleColorDisplayMode.MoleculeParticleColorMode")}));
        tmpValueItem.setName(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLE_COLOR_DISPLAY_MODE");
        tmpValueItem.setDescription(ModelMessage.get("Preferences.ParticleColorDisplayMode.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.ParticleColorDisplayMode.ColorMode"));
        switch (this.particleColorDisplayMode) {
            case PARTICLE_COLOR_MODE:
                tmpValueItem.setValue(ModelMessage.get("Preferences.ParticleColorDisplayMode.ParticleColorMode"));
                break;
            case MOLECULE_COLOR_MODE:
                tmpValueItem.setValue(ModelMessage.get("Preferences.ParticleColorDisplayMode.MoleculeColorMode"));
                break;
            case MOLECULE_PARTICLE_COLOR_MODE:
                tmpValueItem.setValue(ModelMessage.get("Preferences.ParticleColorDisplayMode.MoleculeParticleColorMode"));
                break;
        }
        return tmpValueItem;
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Filter related methods">

    /**
     * Clears job input filter
     *
     * @return True: Filter has changed, false: Otherwise
     */
    public boolean clearJobInputFilter() {
        boolean tmpHasChanged = false;
        if (this.setJobInputFilterAfterTimestamp("")) {
            tmpHasChanged = true;
        }
        if (this.setJobInputFilterBeforeTimestamp("")) {
            tmpHasChanged = true;
        }
        if (this.setJobInputFilterContainsPhrase("")) {
            tmpHasChanged = true;
        }
        // React on changes with tmpHasChanged if necessary
        return tmpHasChanged;
    }

    /**
     * Clears job result filter
     *
     * @return True: Filter has changed, false: Otherwise
     */
    public boolean clearJobResultFilter() {
        boolean tmpHasChanged = false;
        if (this.setJobResultFilterAfterTimestamp("")) {
            tmpHasChanged = true;
        }
        if (this.setJobResultFilterBeforeTimestamp("")) {
            tmpHasChanged = true;
        }
        if (this.setJobResultFilterContainsPhrase("")) {
            tmpHasChanged = true;
        }
        // React on changes with tmpHasChanged if necessary
        return tmpHasChanged;
    }

    /**
     * Checks job input filter definition
     *
     * @return True: Job input filter definition exists, false: Otherwise
     */
    public boolean hasJobInputFilter() {
        return this.jobInputFilterAfterTimestamp.length() > 0 || this.jobInputFilterBeforeTimestamp.length() > 0 || this.jobInputFilterContainsPhrase.length() > 0;
    }

    /**
     * Checks job result filter definition
     *
     * @return True: Job result filter definition exists, false: Otherwise
     */
    public boolean hasJobResultFilter() {
        return this.jobResultFilterAfterTimestamp.length() > 0 || this.jobResultFilterBeforeTimestamp.length() > 0 || this.jobResultFilterContainsPhrase.length() > 0;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Deletes job input directory (with all subdirectories) and job result
     * directory (with all subdirectories)
     *
     * @return True: Operation successful, false: otherwise
     */
    public boolean deleteJobInputAndJobResultDirectories() {
        boolean tmpIsSuccessful = this.fileUtilityMethods.deleteDirectory(this.getJobInputPath()) && this.fileUtilityMethods.deleteDirectory(this.getJobResultPath());
        // IMPORTANT: Set this.basicPreferences to null otherwise there will be no re-instantiation with next method call
        Preferences.basicPreference = null;
        return tmpIsSuccessful;
    }

    /**
     * Sets default values for all preference items
     */
    public void setDefaultValues() {
        // <editor-fold defaultstate="collapsed" desc="Delete old basic preferences persistence file">
        if ((new File(this.persistenceFilePathname)).isFile()) {
            (new File(this.persistenceFilePathname)).delete();
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set default values">
        this.setInitialDefaultValues();
        // </editor-fold>
    }

    /**
     * Saves and removes rotation, i.e. sets all rotation angles to 0.
     */
    public void saveAndRemoveRotation() {
        this.savedRotationAroundXaxisAngle = this.getRotationAroundXaxisAngle();
        this.savedRotationAroundYaxisAngle = this.getRotationAroundYaxisAngle();
        this.savedRotationAroundZaxisAngle = this.getRotationAroundZaxisAngle();
        this.setRotationAroundXaxisAngle(0);
        this.setRotationAroundYaxisAngle(0);
        this.setRotationAroundZaxisAngle(0);
    }

    /**
     * Restores saved rotation after call of saveAndRemoveRotation()
     */
    public void restoreRotation() {
        this.setRotationAroundXaxisAngle(this.savedRotationAroundXaxisAngle);
        this.setRotationAroundYaxisAngle(this.savedRotationAroundYaxisAngle);
        this.setRotationAroundZaxisAngle(this.savedRotationAroundZaxisAngle);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Persistence XML related methods">
    /**
     * Writes persistence XML information
     *
     * @return true: Operation was successful, false: Otherwise
     */
    public boolean writePersistenceXmlInformation() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if ((new File(this.persistenceFilePathname)).isFile()) {
            if (!this.fileUtilityMethods.deleteSingleFile(this.persistenceFilePathname)) {
                return false;
            }
        }

        // </editor-fold>
        Element tmpRoot = new Element(PreferenceXmlName.BASIC_PREFERENCES);
        try {
            // <editor-fold defaultstate="collapsed" desc="Add persistent XML items">
            // Version
            tmpRoot.addContent(new Element(PreferenceXmlName.VERSION).addContent("Version 1.0.0"));
            // this.internalMFsimJobPath
            tmpRoot.addContent(new Element(PreferenceXmlName.INTERNAL_MFSIM_JOB_PATH).addContent(this.internalMFsimJobPath));
            // this.internalTempPath
            tmpRoot.addContent(new Element(PreferenceXmlName.INTERNAL_TEMP_PATH).addContent(this.internalTempPath));
            // this.currentParticleSetFilename
            tmpRoot.addContent(new Element(PreferenceXmlName.CURRENT_PARTICLE_SET_FILENAME).addContent(this.currentParticleSetFilename));
            // this.simulationMovieImagePath
            tmpRoot.addContent(new Element(PreferenceXmlName.SIMULATION_MOVIE_IMAGE_PATH).addContent(this.simulationMovieImagePath));
            // this.chartMovieImagePath
            tmpRoot.addContent(new Element(PreferenceXmlName.CHART_MOVIE_IMAGE_PATH).addContent(this.chartMovieImagePath));
            // this.delayForFilesInMilliseconds
            tmpRoot.addContent(new Element(PreferenceXmlName.DELAY_FOR_FILES_IN_MILLISECONDS).addContent(Long.toString(this.delayForFilesInMilliseconds)));
            // this.delayForJobStartInMilliseconds
            tmpRoot.addContent(new Element(PreferenceXmlName.DELAY_FOR_JOB_START_IN_MILLISECONDS).addContent(Long.toString(this.delayForJobStartInMilliseconds)));
            // this.numberOfSlicesPerView
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_SLICES).addContent(Integer.toString(this.numberOfSlicesPerView)));
            // this.jmolShadePower
            tmpRoot.addContent(new Element(PreferenceXmlName.JMOL_SHADE_POWER).addContent(Integer.toString(this.jmolShadePower)));
            // this.jmolAmbientLightPercentage
            tmpRoot.addContent(new Element(PreferenceXmlName.JMOL_AMBIENT_LIGHT_PERCENTAGE).addContent(Integer.toString(this.jmolAmbientLightPercentage)));
            // this.jmolDiffuseLightPercentage
            tmpRoot.addContent(new Element(PreferenceXmlName.JMOL_DIFFUSE_LIGHT_PERCENTAGE).addContent(Integer.toString(this.jmolDiffuseLightPercentage)));
            // this.jmolSpecularReflectionExponent
            tmpRoot.addContent(new Element(PreferenceXmlName.JMOL_SPECULAR_REFLECTION_EXPONENT).addContent(Integer.toString(this.jmolSpecularReflectionExponent)));
            // this.jmolSpecularReflectionPercentage
            tmpRoot.addContent(new Element(PreferenceXmlName.JMOL_SPECULAR_REFLECTION_PERCENTAGE).addContent(Integer.toString(this.jmolSpecularReflectionPercentage)));
            // this.jmolSpecularReflectionPower
            tmpRoot.addContent(new Element(PreferenceXmlName.JMOL_SPECULAR_REFLECTION_POWER).addContent(Integer.toString(this.jmolSpecularReflectionPower)));
            // this.firstSliceIndex
            tmpRoot.addContent(new Element(PreferenceXmlName.FIRST_SLICE_INDEX).addContent(Integer.toString(this.firstSliceIndex)));
            // this.numberOfBoxWaitSteps
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_BOX_WAIT_STEPS).addContent(Integer.toString(this.numberOfBoxWaitSteps)));
            // this.numberOfFramePointsSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_FRAME_POINTS_SLICER).addContent(Integer.toString(this.numberOfFramePointsSlicer)));
            // this.timeStepDisplaySlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.TIME_STEP_DISPLAY_SLICER).addContent(Integer.toString(this.timeStepDisplaySlicer)));
            // this.numberOfParallelSimulations
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_PARALLEL_SIMULATIONS).addContent(Integer.toString(this.numberOfParallelSimulations)));
            // this.numberOfParallelSlicers
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_PARALLEL_SLICERS).addContent(Integer.toString(this.numberOfParallelSlicers)));
            // this.numberOfParallelCalculators
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_PARALLEL_CALCULATORS).addContent(Integer.toString(this.numberOfParallelCalculators)));
            // this.numberOfParallelParticlePositionWriters
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_PARALLEL_PARTICLE_POSITION_WRITERS).addContent(Integer.toString(this.numberOfParallelParticlePositionWriters)));
            // this.numberOfAfterDecimalDigitsForParticlePositions
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS).addContent(Integer.toString(this.numberOfAfterDecimalDigitsForParticlePositions)));
            // this.maximumNumberOfPositionCorrectionTrials
            tmpRoot.addContent(new Element(PreferenceXmlName.MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS).addContent(Integer.toString(this.maximumNumberOfPositionCorrectionTrials)));
            // this.movieQuality
            tmpRoot.addContent(new Element(PreferenceXmlName.MOVIE_QUALITY).addContent(Integer.toString(this.movieQuality)));
            // this.timerIntervalInMilliseconds
            tmpRoot.addContent(new Element(PreferenceXmlName.TIMER_INTERVAL_IN_MILLISECONDS).addContent(Integer.toString(this.timerIntervalInMilliseconds)));
            // this.minimumBondLengthDpd
            tmpRoot.addContent(new Element(PreferenceXmlName.MINIMUM_BOND_LENGTH_DPD).addContent(Double.toString(this.minimumBondLengthDpd)));
            // this.maximumNumberOfParticlesForGraphicalDisplay
            tmpRoot.addContent(new Element(PreferenceXmlName.MAXIMUM_NUMBER_OF_PARTICLES_FOR_GRAPHICAL_DISPLAY).addContent(Integer.toString(this.maximumNumberOfParticlesForGraphicalDisplay)));
            // this.numberOfStepsForRdfCalculation
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_STEPS_FOR_RDF_CALCULATION).addContent(Integer.toString(this.numberOfStepsForRdfCalculation)));
            // this.numberOfVolumeBins
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_ZOOM_VOLUME_BINS).addContent(Integer.toString(this.numberOfVolumeBins)));
            // this.numberOfTrialsForCompartment
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_TRIALS_FOR_COMPARTMENT).addContent(Integer.toString(this.numberOfTrialsForCompartment)));
            // this.animationSpeed
            tmpRoot.addContent(new Element(PreferenceXmlName.ANIMATION_SPEED).addContent(Integer.toString(this.animationSpeed)));
            // this.numberOfSimulationBoxCellsforParallelization
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION).addContent(Integer.toString(this.numberOfSimulationBoxCellsforParallelization)));
            // this.numberOfBondsforParallelization
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_BONDS_FOR_PARALLELIZATION).addContent(Integer.toString(this.numberOfBondsforParallelization)));
            // this.numberOfStepsForJobRestart
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_STEPS_FOR_JOB_RESTART).addContent(Integer.toString(this.numberOfAdditionalStepsForJobRestart)));
            // this.simulationBoxMagnificationPercentage
            tmpRoot.addContent(new Element(PreferenceXmlName.SIMULATION_BOX_MAGNIFICATION_PERCENTAGE).addContent(Integer.toString(this.simulationBoxMagnificationPercentage)));
            // this.numberOfSpinSteps
            tmpRoot.addContent(new Element(PreferenceXmlName.NUMBER_OF_SPIN_STEPS).addContent(Integer.toString(this.numberOfSpinSteps)));
            // this.lastSelectedPath
            tmpRoot.addContent(new Element(PreferenceXmlName.LAST_SELECTED_PATH).addContent(this.lastSelectedPath));
            // this.mainFrameHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.MAIN_FRAME_HEIGHT).addContent(Integer.toString(this.mainFrameHeight)));
            // this.mainFrameWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.MAIN_FRAME_WIDTH).addContent(Integer.toString(this.mainFrameWidth)));
            // this.dialogSlicerShowHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_SLICER_SHOW_HEIGHT).addContent(Integer.toString(this.dialogSlicerShowHeight)));
            // this.dialogSlicerShowWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_SLICER_SHOW_WIDTH).addContent(Integer.toString(this.dialogSlicerShowWidth)));
            // this.customDialogHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.CUSTOM_DIALOG_HEIGHT).addContent(Integer.toString(this.customDialogHeight)));
            // this.customDialogWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.CUSTOM_DIALOG_WIDTH).addContent(Integer.toString(this.customDialogWidth)));
            // this.dialogSingleSlicerShowHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_SINGLE_SLICER_SHOW_HEIGHT).addContent(Integer.toString(this.dialogSingleSlicerShowHeight)));
            // this.dialogSingleSlicerShowWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_SINGLE_SLICER_SHOW_WIDTH).addContent(Integer.toString(this.dialogSingleSlicerShowWidth)));
            // this.dialogSimulationMovieSlicerShowHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_SIMULATION_MOVIE_SLICER_SHOW_HEIGHT).addContent(Integer.toString(this.dialogSimulationMovieSlicerShowHeight)));
            // this.dialogSimulationMovieSlicerShowWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_SIMULATION_MOVIE_SLICER_SHOW_WIDTH).addContent(Integer.toString(this.dialogSimulationMovieSlicerShowWidth)));
            // this.dialogValueItemEditHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_VALUE_ITEM_EDIT_HEIGHT).addContent(Integer.toString(this.dialogValueItemEditHeight)));
            // this.dialogValueItemEditWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_VALUE_ITEM_EDIT_WIDTH).addContent(Integer.toString(this.dialogValueItemEditWidth)));
            // this.dialogTextEditHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_TEXT_EDIT_HEIGHT).addContent(Integer.toString(this.dialogTextEditHeight)));
            // this.dialogTextEditWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_TEXT_EDIT_WIDTH).addContent(Integer.toString(this.dialogTextEditWidth)));
            // this.dialogValueItemShowHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_VALUE_ITEM_SHOW_HEIGHT).addContent(Integer.toString(this.dialogValueItemShowHeight)));
            // this.dialogValueItemShowWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_VALUE_ITEM_SHOW_WIDTH).addContent(Integer.toString(this.dialogValueItemShowWidth)));
            // this.dialogTableDataSchemataManageHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_TABLE_DATA_SCHEMATA_MANAGE_HEIGHT).addContent(Integer.toString(this.dialogTableDataSchemataManageHeight)));
            // this.dialogTableDataSchemataManageWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_TABLE_DATA_SCHEMATA_MANAGE_WIDTH).addContent(Integer.toString(this.dialogTableDataSchemataManageWidth)));
            // this.dialogValueItemMatrixDiagramHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_VALUE_ITEM_MATRIX_DIAGRAM_HEIGHT).addContent(Integer.toString(this.dialogValueItemMatrixDiagramHeight)));
            // this.dialogValueItemMatrixDiagramWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_VALUE_ITEM_MATRIX_DIAGRAM_WIDTH).addContent(Integer.toString(this.dialogValueItemMatrixDiagramWidth)));
            // this.dialogStructureEditHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_STRUCTURE_EDIT_HEIGHT).addContent(Integer.toString(this.dialogStructureEditHeight)));
            // this.dialogStructureEditWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_STRUCTURE_EDIT_WIDTH).addContent(Integer.toString(this.dialogStructureEditWidth)));
            // this.dialogPeptideEditHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_PEPTIDE_EDIT_HEIGHT).addContent(Integer.toString(this.dialogPeptideEditHeight)));
            // this.dialogPeptideEditWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_PEPTIDE_EDIT_WIDTH).addContent(Integer.toString(this.dialogPeptideEditWidth)));
            // this.dialogCompartmentEditHeight
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_COMPARTMENT_EDIT_HEIGHT).addContent(Integer.toString(this.dialogCompartmentEditHeight)));
            // this.dialogCompartmentEditWidth
            tmpRoot.addContent(new Element(PreferenceXmlName.DIALOG_COMPARTMENT_EDIT_WIDTH).addContent(Integer.toString(this.dialogCompartmentEditWidth)));
            // this.isParticleUpdateForJobInput
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_PARTICLE_UPDATE_FOR_JOB_INPUT).addContent(Boolean.toString(this.isParticleUpdateForJobInput)));
            // this.isJobResultArchiveStepFileInclusion
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION).addContent(Boolean.toString(this.isJobResultArchiveStepFileInclusion)));
            // this.isVolumeScalingForConcentrationCalculation
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_VOLUME_SCALING_FOR_CONCENTRATION_CALCULATION).addContent(Boolean.toString(this.isVolumeScalingForConcentrationCalculation)));
            // this.isJobInputInclusion
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_JOB_INPUT_INCLUSION).addContent(Boolean.toString(this.isJobInputInclusion)));
            // this.isParticleDistributionInclusion
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_PARTICLE_DISTRICUTION_INCLUSION).addContent(Boolean.toString(this.isParticleDistributionInclusion)));
            // this.isSimulationStepInclusion
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_SIMULATION_STEP_INCLUSION).addContent(Boolean.toString(this.isSimulationStepInclusion)));
            // this.isNearestNeighborEvaluationInclusion
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION).addContent(Boolean.toString(this.isNearestNeighborEvaluationInclusion)));
            // this.isJobResultArchiveProcessParallelInBackground
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND).addContent(Boolean.toString(this.isJobResultArchiveProcessParallelInBackground)));
            // this.isJobResultArchiveFileUncompressed
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED).addContent(Boolean.toString(this.isJobResultArchiveFileUncompressed)));
            // this.isJdpdLogLevelExceptions
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_JDPD_LOG_LEVEL_EXCEPTIONS).addContent(Boolean.toString(this.isJdpdLogLevelExceptions)));
            // this.isConstantCompartmentBodyVolume
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_CONSTANT_COMPARTMENT_BODY_VOLUME).addContent(Boolean.toString(this.isConstantCompartmentBodyVolume)));
            // this.isSimulationBoxSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_SIMULATION_BOX_SLICER).addContent(Boolean.toString(this.isSimulationBoxSlicer)));
            // this.isSingleSliceDisplay
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_SINGLE_SLICE_DISPLAY).addContent(Boolean.toString(this.isSingleSliceDisplay)));
            // this.slicerGraphicsMode
            tmpRoot.addContent(new Element(PreferenceXmlName.SLICER_GRAPHICS_MODE).addContent(this.slicerGraphicsMode.name()));
            // this.simulationBoxBackgroundColorSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.SIMULATION_BOX_BACKGROUND_COLOR_SLICER).addContent(this.simulationBoxBackgroundColorSlicer));
            // this.measurementColorSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.MEASUREMENT_COLOR_SLICER).addContent(this.measurementColorSlicer));
            // this.moleculeSelectionColorSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.MOLECULE_SELECTION_COLOR_SLICER).addContent(this.moleculeSelectionColorSlicer));
            // this.frameColorSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.FRAME_COLOR_SLICER).addContent(this.frameColorSlicer));
            // this.jmolSimulationBoxBackgroundColor
            tmpRoot.addContent(new Element(PreferenceXmlName.JMOL_SIMULATION_BOX_BACKGROUND_COLOR).addContent(this.jmolSimulationBoxBackgroundColor));
            // this.proteinViewerBackgroundColor
            tmpRoot.addContent(new Element(PreferenceXmlName.PROTEIN_VIEWER_BACKGROUND_COLOR).addContent(this.proteinViewerBackgroundColor));
            // this.imageStorageMode
            tmpRoot.addContent(new Element(PreferenceXmlName.IMAGE_STORAGE_MODE).addContent(this.imageStorageMode.name()));
            // this.particleColorDisplayMode
            tmpRoot.addContent(new Element(PreferenceXmlName.PARTICLE_COLOR_DISPLAY_MODE).addContent(this.particleColorDisplayMode.name()));
            // this.boxViewDisplay
            tmpRoot.addContent(new Element(PreferenceXmlName.BOX_VIEW_DISPLAY).addContent(this.boxViewDisplay.toRepresentation()));
            // this.isFrameDisplaySlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.IS_FRAME_DISPLAY_SLICER).addContent(Boolean.toString(this.isFrameDisplaySlicer)));
            // this.specularWhiteAttenuationSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.SPECULAR_WHITE_ATTENUATION_SLICER).addContent(Double.toString(this.specularWhiteAttenuationSlicer)));
            // this.depthAttenuationCompartment
            tmpRoot.addContent(new Element(PreferenceXmlName.COLOR_SHAPE_ATTENUATION_COMPARTMENT).addContent(Double.toString(this.depthAttenuationCompartment)));
            // this.compartmentBodyChangeResponseFactor
            tmpRoot.addContent(new Element(PreferenceXmlName.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR).addContent(Double.toString(this.compartmentBodyChangeResponseFactor)));
            // this.maxSelectedMoleculeNumberSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.MAX_SELECTED_MOLECULE_NUMBER_SLICER).addContent(Integer.toString(this.maxSelectedMoleculeNumberSlicer)));
            // this.depthAttenuationSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.DEPTH_ATTENUATION_SLICER).addContent(Double.toString(this.depthAttenuationSlicer)));
            // this.xShiftInPixelSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.X_SHIFT_IN_PIXEL_SLICER).addContent(Integer.toString(this.xShiftInPixelSlicer)));
            // this.yShiftInPixelSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.Y_SHIFT_IN_PIXEL_SLICER).addContent(Integer.toString(this.yShiftInPixelSlicer)));
            // this.rotationAroundXaxisAngle
            tmpRoot.addContent(new Element(PreferenceXmlName.ROTATION_AROUND_X_AXIS_ANGLE).addContent(Integer.toString(this.rotationAroundXaxisAngle)));
            // this.rotationAroundYaxisAngle
            tmpRoot.addContent(new Element(PreferenceXmlName.ROTATION_AROUND_Y_AXIS_ANGLE).addContent(Integer.toString(this.rotationAroundYaxisAngle)));
            // this.rotationAroundZaxisAngle
            tmpRoot.addContent(new Element(PreferenceXmlName.ROTATION_AROUND_Z_AXIS_ANGLE).addContent(Integer.toString(this.rotationAroundZaxisAngle)));
            // this.particleShiftX
            tmpRoot.addContent(new Element(PreferenceXmlName.PARTICLE_SHIFT_X).addContent(Integer.toString(this.particleShiftX)));
            // this.particleShiftY
            tmpRoot.addContent(new Element(PreferenceXmlName.PARTICLE_SHIFT_Y).addContent(Integer.toString(this.particleShiftY)));
            // this.particleShiftZ
            tmpRoot.addContent(new Element(PreferenceXmlName.PARTICLE_SHIFT_Z).addContent(Integer.toString(this.particleShiftZ)));
            // this.radialGradientPaintRadiusMagnification
            tmpRoot.addContent(new Element(PreferenceXmlName.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION).addContent(Float.toString(this.radialGradientPaintRadiusMagnification)));
            // this.specularWhiteSizeSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.SPECULAR_WHITE_SIZE_SLICER).addContent(Float.toString(this.specularWhiteSizeSlicer)));
            // this.radialGradientPaintFocusFactorX
            tmpRoot.addContent(new Element(PreferenceXmlName.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X).addContent(Float.toString(this.radialGradientPaintFocusFactorX)));
            // this.radialGradientPaintFocusFactorY
            tmpRoot.addContent(new Element(PreferenceXmlName.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y).addContent(Float.toString(this.radialGradientPaintFocusFactorY)));
            // this.colorGradientAttenuationCompartment
            tmpRoot.addContent(new Element(PreferenceXmlName.COLOR_GRADIENT_ATTENUATION_COMPARTMENT).addContent(Double.toString(this.colorGradientAttenuationCompartment)));
            // this.colorGradientAttenuationSlicer
            tmpRoot.addContent(new Element(PreferenceXmlName.COLOR_GRADIENT_ATTENUATION_SLICER).addContent(Double.toString(this.colorGradientAttenuationSlicer)));
            // this.jpegImageQuality
            tmpRoot.addContent(new Element(PreferenceXmlName.JPEG_IMAGE_QUALITY).addContent(Float.toString(this.jpegImageQuality)));
            // this.colorTransparencyCompartment
            tmpRoot.addContent(new Element(PreferenceXmlName.COLOR_TRANSPARENCY_COMPARTMENT).addContent(Float.toString(this.colorTransparencyCompartment)));
            // this.jobInputFilterAfterTimestamp
            tmpRoot.addContent(new Element(PreferenceXmlName.JOB_INPUT_FILTER_AFTER_TIMESTAMP).addContent(this.jobInputFilterAfterTimestamp));
            // this.jobInputFilterBefore
            tmpRoot.addContent(new Element(PreferenceXmlName.JOB_INPUT_FILTER_BEFORE_TIMESTAMP).addContent(this.jobInputFilterBeforeTimestamp));
            // this.jobInputFilterContainsPhrase
            tmpRoot.addContent(new Element(PreferenceXmlName.JOB_INPUT_FILTER_CONTAINS_PHRASE).addContent(this.jobInputFilterContainsPhrase));
            // this.jobResultFilterAfterTimestamp
            tmpRoot.addContent(new Element(PreferenceXmlName.JOB_RESULT_FILTER_AFTER_TIMESTAMP).addContent(this.jobResultFilterAfterTimestamp));
            // this.jobResultFilterBefore
            tmpRoot.addContent(new Element(PreferenceXmlName.JOB_RESULT_FILTER_BEFORE_TIMESTAMP).addContent(this.jobResultFilterBeforeTimestamp));
            // this.jobResultFilterContainsPhrase
            tmpRoot.addContent(new Element(PreferenceXmlName.JOB_RESULT_FILTER_CONTAINS_PHRASE).addContent(this.jobResultFilterContainsPhrase));
            // this.schemaValueItemContainer
            tmpRoot.addContent(new Element(PreferenceXmlName.SCHEMA_VALUE_ITEM_CONTAINER).addContent(this.stringUtilityMethods.compressIntoBase64String(this.schemaValueItemContainer.getAsXmlString())));
            // this.previousMonomers
            ModelUtils.addStringListToXmlElement(this.previousMonomers, tmpRoot, PreferenceXmlName.PREVIOUS_MONOMERS, PreferenceXmlName.PREVIOUS_SINGLE_MONOMER);
            // this.previousStructures
            ModelUtils.addStringListToXmlElement(this.previousStructures, tmpRoot, PreferenceXmlName.PREVIOUS_STRUCTURES, PreferenceXmlName.PREVIOUS_SINGLE_STRUCTURE);
            // this.previousPeptides
            ModelUtils.addStringListToXmlElement(this.previousPeptides, tmpRoot, PreferenceXmlName.PREVIOUS_PEPTIDES, PreferenceXmlName.PREVIOUS_SINGLE_PEPTIDE);

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Error message">
            JOptionPane.showMessageDialog(null, String.format(ModelMessage.get("Error.NoPreferenceBasicPersistence"), anException.getMessage()), ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return false;
        }
        XMLOutputter tmpOutputter = new XMLOutputter(Format.getPrettyFormat());
        Document tmpDocument = new Document();
        tmpDocument.setRootElement(tmpRoot);
        return this.fileUtilityMethods.writeSingleStringToTextFile(tmpOutputter.outputString(tmpDocument), this.persistenceFilePathname);
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    // <editor-fold defaultstate="collapsed" desc="- JobInputPath (get only)">
    /**
     * Full path for job inputs
     *
     * @return Full path for job inputs
     */
    public String getJobInputPath() {
        if (this.internalMFsimJobPath == null || this.internalMFsimJobPath.isEmpty()) {
            return this.dpdDataPath + File.separatorChar + ModelDefinitions.JOB_INPUT_DIRECTORY;
        } else {
            return this.internalMFsimJobPath + File.separatorChar + ModelDefinitions.JOB_INPUT_DIRECTORY;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobResultPath (get only)">
    /**
     * Full path for job results
     *
     * @return Full path for job results
     */
    public String getJobResultPath() {
        if (this.internalMFsimJobPath == null || this.internalMFsimJobPath.isEmpty()) {
            return this.dpdDataPath + File.separatorChar + ModelDefinitions.JOB_RESULT_DIRECTORY;
        } else {
            return this.internalMFsimJobPath + File.separatorChar + ModelDefinitions.JOB_RESULT_DIRECTORY;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DpdSourcePath (get only)">
    /**
     * Full path of DPD source
     *
     * @return Full path of DPD source
     */
    public String getDpdSourcePath() {
        return ModelDefinitions.USER_DIRECTORY_PATH + File.separatorChar + ModelDefinitions.MFSIM_SOURCE_DIRECTORY;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DpdDataPath (get only)">
    /**
     * Full path of DPD data
     *
     * @return Full path of DPD data
     */
    public String getDpdDataPath() {
        return this.dpdDataPath;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- LogfilePathname (get only)">
    /**
     * Full path and name of log file
     *
     * @return path and name of log file
     */
    public String getLogfilePathname() {
        return this.dpdDataPath + File.separatorChar + ModelDefinitions.LOGFILE_NAME;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DpdSourceTutorialsPath (get only)">
    /**
     * Full path of directory for tutorials
     *
     * @return Full path of directory for tutorials
     */
    public String getDpdSourceTutorialsPath() {
        return ModelDefinitions.USER_DIRECTORY_PATH + File.separatorChar + ModelDefinitions.MFSIM_SOURCE_TUTORIALS_DIRECTORY;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DpdSourceInfoPath (get only)">
    /**
     * Full path of info directory
     *
     * @return Full path of info directory
     */
    public String getDpdSourceInfoPath() {
        return ModelDefinitions.USER_DIRECTORY_PATH + File.separatorChar + ModelDefinitions.MFSIM_SOURCE_INFO_DIRECTORY;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DpdSourceParticlesPath (get only)">
    /**
     * Full path of installation directory for particle related data
     *
     * @return Full path of installation directory for particle related data
     */
    public String getDpdSourceParticlesPath() {
        return ModelDefinitions.USER_DIRECTORY_PATH + File.separatorChar + ModelDefinitions.MFSIM_SOURCE_PARTICLES_DIRECTORY;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DpdSourceWinUtilsPath (get only)">
    /**
     * Full path of installation directory for windows utilities
     *
     * @return Full path of installation directory for windows utilities
     */
    public String getDpdSourceWinUtilsPath() {
        return ModelDefinitions.USER_DIRECTORY_PATH + File.separatorChar + ModelDefinitions.MFSIM_SOURCE_WINDOWS_UTILITIES_DIRECTORY;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- FFmpegFilePathname (get only)">
    /**
     * File pathname of FFmpeg in directory for windows utilities
     *
     * @return File pathname of FFmpeg
     */
    public String getFFmpegFilePathnameInWinUtils() {
        return this.getDpdSourceWinUtilsPath() + File.separatorChar + ModelDefinitions.FFMPEG_FILE_NAME;
    }

    /**
     * Returns if FFmpeg.exe is available in installation directory for windows
     * utilities
     *
     * @return True: FFmpeg.exe is available in installation directory for
     * windows utilities, false: Otherwise
     */
    public boolean hasFFmpegInWinUtils() {
        return (new File(this.getFFmpegFilePathnameInWinUtils())).isFile();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DpdSourceCurrentParticleSetFilePathname (get only)">
    /**
     * Full pathname of file for current particle set
     *
     * @return Full pathname of file for current particle set or null if none is
     * found
     */
    public String getCurrentParticleSetFilePathname() {
        if (this.currentParticleSetFilename == null || this.currentParticleSetFilename.isEmpty()) {
            // Get first particle set from source particle set directory
            String[] tmpAvailableParticleSetFilenamesInDpdSourceParticles = this.fileUtilityMethods.getFilenamesWithPrefix(this.getDpdSourceParticlesPath(), ModelDefinitions.PARTICLE_SET_FILE_PREFIX);
            this.currentParticleSetFilename = tmpAvailableParticleSetFilenamesInDpdSourceParticles[0];
            return this.getDpdSourceParticlesPath() + File.separatorChar + this.currentParticleSetFilename;
        } else {
            // Get from source particle set directory
            String[] tmpAvailableParticleSetFilenamesInDpdSourceParticles = this.fileUtilityMethods.getFilenamesWithPrefix(this.getDpdSourceParticlesPath(), ModelDefinitions.PARTICLE_SET_FILE_PREFIX);
            for (String tmpParticleSetFilename : tmpAvailableParticleSetFilenamesInDpdSourceParticles) {
                if (this.currentParticleSetFilename.toLowerCase(Locale.ENGLISH).equals(tmpParticleSetFilename.toLowerCase(Locale.ENGLISH))) {
                    return this.getDpdSourceParticlesPath() + File.separatorChar + this.currentParticleSetFilename;
                }
            }
            // Get from custom particle set directory
            String[] tmpAvailableParticleSetFilenamesInCustomParticles = this.fileUtilityMethods.getFilenamesWithPrefix(this.getCustomParticlesPath(), ModelDefinitions.PARTICLE_SET_FILE_PREFIX);
            if (tmpAvailableParticleSetFilenamesInCustomParticles != null) {
                for (String tmpParticleSetFilename : tmpAvailableParticleSetFilenamesInCustomParticles) {
                    if (this.currentParticleSetFilename.toLowerCase(Locale.ENGLISH).equals(tmpParticleSetFilename.toLowerCase(Locale.ENGLISH))) {
                        return this.getCustomParticlesPath() + File.separatorChar + this.currentParticleSetFilename;
                    }
                }
            }
            // Get first particle set from source particle set directory
            this.currentParticleSetFilename = tmpAvailableParticleSetFilenamesInDpdSourceParticles[0];
            return this.getDpdSourceParticlesPath() + File.separatorChar + this.currentParticleSetFilename;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- TempPath (get only)">
    /**
     * Full path for temporary directory
     *
     * @return Full path for temporary directory
     */
    public String getTempPath() {
        if (this.internalTempPath == null || this.internalTempPath.isEmpty()) {
            return this.dpdDataPath + File.separatorChar + ModelDefinitions.TEMPORARY_DIRECTORY;
        } else {
            return this.internalTempPath;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CustomParticlesPath (get only)">
    /**
     * Full path for custom particles directory
     *
     * @return Full path for custom particles directory
     */
    public String getCustomParticlesPath() {
        return this.dpdDataPath + File.separatorChar + ModelDefinitions.CUSTOM_PARTICLES_DIRECTORY;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (set only)">
    // <editor-fold defaultstate="collapsed" desc="- NumberOfWorkingJobResultExecutionTasks (set only)">
    /**
     * Number of active JobResultExecutionTask threads
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfWorkingJobResultExecutionTasks(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.numberOfWorkingJobResultExecutionTasks != aValue) {
            this.numberOfWorkingJobResultExecutionTasks = aValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }

    /**
     * Returns if a job is working
     *
     * @return True: Job is working, false: Otherwise
     */
    private boolean isJobWorking() {
        return this.numberOfWorkingJobResultExecutionTasks > 0;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- isBoxVolumeShapeForZoom">
    /**
     * Flag for shape of zoom volume:
     * True: Box volume shape, false: Ellipsoid volume shape.
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setBoxVolumeShapeForZoom(boolean aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.isBoxVolumeShapeForZoom != aValue) {
            this.isBoxVolumeShapeForZoom = aValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }

    /**
     * Returns flag for shape of zoom volume:
     * True: Box volume shape, false: Ellipsoid volume shape.
     *
     * @return True: Box volume shape, false: Ellipsoid volume shape.
     */
    public boolean isBoxVolumeShapeForZoom() {
        return this.isBoxVolumeShapeForZoom;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MovieSlicerConfiguration">
    /**
     * Movie slicer configuration (NOTE: NOT to be made persistent, is ONLY
     * used at runtime!)
     *
     * @return Movie slicer configuration or null if none is set before
     */
    public MovieSlicerConfiguration getSimulationMovieSlicerConfiguration() {
        return this.simulationMovieSlicerConfiguration;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- TimeStepSimulationBoxChangeInfo">
    /**
     * Time step simulation box change info (NOTE: NOT to be made persistent, is
     * ONLY used at runtime!)
     *
     * @return Time step simulation box change info
     */
    public SimulationBoxChangeInfo getTimeStepSimulationBoxChangeInfo() {
        return this.timeStepSimulationBoxChangeInfo;
    }

    /**
     * Time step simulation box change info (NOTE: NOT to be made persistent, is
     * ONLY used at runtime!)
     *
     * @param aTimeStepSimulationBoxChangeInfo Time step simulation box change
     * info
     */
    public void setTimeStepSimulationBoxChangeInfo(SimulationBoxChangeInfo aTimeStepSimulationBoxChangeInfo) {
        this.timeStepSimulationBoxChangeInfo = aTimeStepSimulationBoxChangeInfo;
    }

    /**
     * Clears time step simulation box change info
     */
    public void clearTimeStepSimulationBoxChangeInfo() {
        this.timeStepSimulationBoxChangeInfo = new SimulationBoxChangeInfo();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- IsLogEvent">
    /**
     * Sets log event
     * NOTE: NOT to be made persistent, is ONLY used at runtime!
     *
     * @param aValue Value
     */
    public void setLogEvent(boolean aValue) {
        this.isLogEvent = aValue;
    }
    
    /**
     * True: Log event occurred, false: Otherwise
     * NOTE: NOT to be made persistent, is ONLY used at runtime!
     * 
     * @return True: Log event occurred, false: Otherwise
     */
    public boolean isLogEvent() {
        return this.isLogEvent;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleUpdateForJobInput">
    /**
     * Particle update for job input. True: Complete particle information is
     * always updated on edit of job input, false: Otherwise.
     *
     * @return Particle update for job input. True: Complete particle
     * information is always updated on edit of job input, false: Otherwise.
     */
    public boolean isParticleUpdateForJobInput() {
        return this.isParticleUpdateForJobInput;
    }

    /**
     * Default particle update for job input. True: Complete particle
     * information is always updated on edit of job input, false: Otherwise.
     *
     * @return Default particle update for job input. True: Complete particle
     * information is always updated on edit of job input, false: Otherwise.
     */
    public boolean getDefaultParticleUpdateForJobInput() {
        return ModelDefinitions.IS_PARTICLE_UPDATE_FOR_JOB_INPUT_DEFAULT;
    }

    /**
     * Default particle update for job input. True: Complete particle
     * information is always updated on edit of job input, false: Otherwise.
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setParticleUpdateForJobInput(boolean aValue) {
        if (this.isParticleUpdateForJobInput != aValue) {
            this.isParticleUpdateForJobInput = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SpecularWhiteAttenuationSlicer">
    /**
     * Specular white attenuation for bodies to body color for slicer. 0.0 =
     * Minimum (no attenuation), 1.0 = Maximum (maximum attenuation)
     *
     * @return Specular white attenuation for bodies to body color for slicer
     */
    public double getSpecularWhiteAttenuationSlicer() {
        return this.specularWhiteAttenuationSlicer;
    }

    /**
     * Default specular white attenuation for bodies to body color for slicer
     *
     * @return Default specular white attenuation for bodies to body color for
     * slicer
     */
    public double getDefaultSpecularWhiteAttenuationSlicer() {
        return ModelDefinitions.SPECULAR_WHITE_ATTENUATION_DEFAULT;
    }

    /**
     * Specular white attenuation for bodies to body color for slicer. 0.0 =
     * Minimum (no attenuation), 1.0 = Maximum (maximum attenuation)
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setSpecularWhiteAttenuationSlicer(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue < 0.0 || aValue > 1.0) {
            return false;
        }
        // </editor-fold>
        if (this.specularWhiteAttenuationSlicer != aValue) {
            this.specularWhiteAttenuationSlicer = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DepthAttenuationCompartment">
    /**
     * Color shape attenuation of bodies for depth impression. 0.0 = Minimum (no
     * attenuation), 1.0 = Maximum (maximum attenuation) for compartments
     *
     * @return Color shape attenuation of bodies for depth impression for
     * compartments
     */
    public double getDepthAttenuationCompartment() {
        return this.depthAttenuationCompartment;
    }

    /**
     * Default shape attenuation of bodies for depth impression for compartments
     *
     * @return Default shape attenuation of bodies for depth impression for
     * compartments
     */
    public double getDefaultDepthAttenuationCompartment() {
        return ModelDefinitions.COLOR_SHAPE_ATTENUATION_COMPARTMENT_DEFAULT;
    }

    /**
     * Color shape attenuation of bodies for depth impression. 0.0 = Minimum (no
     * attenuation), 1.0 = Maximum (maximum attenuation) for compartments
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDepthAttenuationCompartment(double aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        double tmpCorrectedValue = ModelUtils.correctDoubleValue(aValue, ModelDefinitions.COLOR_SHAPE_ATTENUATION_COMPARTMENT_MINIMUM, ModelDefinitions.COLOR_SHAPE_ATTENUATION_COMPARTMENT_MAXIMUM);
        if (this.depthAttenuationCompartment != tmpCorrectedValue) {
            this.depthAttenuationCompartment = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CompartmentBodyChangeResponseFactor">
    /**
     * Compartment body change response factor
     *
     * @return Compartment body change response factor
     */
    public double getCompartmentBodyChangeResponseFactor() {
        return this.compartmentBodyChangeResponseFactor;
    }

    /**
     * Default compartment body change response factor
     *
     * @return Default compartment body change response factor
     */
    public double getDefaultCompartmentBodyChangeResponseFactor() {
        return ModelDefinitions.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR_DEFAULT;
    }

    /**
     * Compartment body change response factor
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setCompartmentBodyChangeResponseFactor(double aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        double tmpCorrectedValue = 
            ModelUtils.correctDoubleValue(
                aValue, 
                ModelDefinitions.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR_MINIMUM, 
                ModelDefinitions.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR_MAXIMUM
            );
        if (this.compartmentBodyChangeResponseFactor != tmpCorrectedValue) {
            this.compartmentBodyChangeResponseFactor = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MaxSelectedMoleculeNumberSlicer">
    /**
     * Maximum number of selected molecules for slicer
     *
     * @return Maximum number of selected molecules for slicer
     */
    public int getMaxSelectedMoleculeNumberSlicer() {
        return this.maxSelectedMoleculeNumberSlicer;
    }

    /**
     * Default maximum number of selected molecules for slicer
     *
     * @return Default maximum number of selected molecules for slicer
     */
    public int getDefaultMaxSelectedMoleculeNumberSlicer() {
        return ModelDefinitions.DEFAULT_MAX_SELECTED_MOLECULE_NUMBER_SLICER;
    }

    /**
     * Maximum number of selected molecules for slicer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMaxSelectedMoleculeNumberSlicer(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_MAX_SELECTED_MOLECULE_NUMBER_SLICER, Integer.MAX_VALUE);
        if (this.maxSelectedMoleculeNumberSlicer != tmpCorrectedValue) {
            this.maxSelectedMoleculeNumberSlicer = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DepthAttenuationSlicer">
    /**
     * Depth attenuation of slices for depth impression (fog) in simulation box
     * of slicer. 0.0 = Minimum (no attenuation)
     *
     * @return Depth attenuation of slices for depth impression (fog) in
     * simulation box of slicer
     */
    public double getDepthAttenuationSlicer() {
        return this.depthAttenuationSlicer;
    }

    /**
     * Default depth attenuation of slices for depth impression (fog) in
     * simulation box of slicer
     *
     * @return Default depth attenuation of slices for depth impression (fog) in
     * simulation box of slicer
     */
    public double getDefaultDepthAttenuationSlicer() {
        return ModelDefinitions.DEPTH_ATTENUATION_SLICER_DEFAULT;
    }

    /**
     * Depth attenuation of slices for depth impression (fog) in simulation box
     * of slicer. 0.0 = Minimum (no attenuation)
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDepthAttenuationSlicer(double aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        double tmpCorrectedValue = ModelUtils.correctDoubleValue(aValue, ModelDefinitions.DEPTH_ATTENUATION_SLICER_MINIMUM, ModelDefinitions.DEPTH_ATTENUATION_SLICER_MAXIMUM);
        if (this.depthAttenuationSlicer != tmpCorrectedValue) {
            this.depthAttenuationSlicer = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ColorGradientAttenuationCompartment">
    /**
     * Color gradient attenuation. 0.0: Minimum (no attenuation), 1.0: Maximum
     * (full attenuation) for compartments
     *
     * @return Color gradient attenuation for compartments
     */
    public double getColorGradientAttenuationCompartment() {
        return this.colorGradientAttenuationCompartment;
    }

    /**
     * Default color gradient attenuation. 0.0: Minimum (no attenuation), 1.0:
     * Maximum (full attenuation) for compartments
     *
     * @return Default color gradient attenuation for compartments
     */
    public double getDefaultColorGradientAttenuationCompartment() {
        return ModelDefinitions.COLOR_GRADIENT_ATTENUATION_DEFAULT;
    }

    /**
     * Color gradient attenuation. 0.0: Minimum (no attenuation), 1.0: Maximum
     * (full attenuation) for compartments
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setColorGradientAttenuationCompartment(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue < 0.0 || aValue > 1.0) {
            return false;
        }

        // </editor-fold>
        if (this.colorGradientAttenuationCompartment != aValue) {
            this.colorGradientAttenuationCompartment = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ColorGradientAttenuationSlicer">
    /**
     * Color gradient attenuation. 0.0: Minimum (no attenuation), 1.0: Maximum
     * (full attenuation) for slicer
     *
     * @return Color gradient attenuation for slicer
     */
    public double getColorGradientAttenuationSlicer() {
        return this.colorGradientAttenuationSlicer;
    }

    /**
     * Default color gradient attenuation. 0.0: Minimum (no attenuation), 1.0:
     * Maximum (full attenuation) for slicer
     *
     * @return Default color gradient attenuation for slicer
     */
    public double getDefaultColorGradientAttenuationSlicer() {
        return ModelDefinitions.COLOR_GRADIENT_ATTENUATION_DEFAULT;
    }

    /**
     * Color gradient attenuation. 0.0: Minimum (no attenuation), 1.0: Maximum
     * (full attenuation) for slicer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setColorGradientAttenuationSlicer(double aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue < 0.0 || aValue > 1.0) {
            return false;
        }

        // </editor-fold>
        if (this.colorGradientAttenuationSlicer != aValue) {
            this.colorGradientAttenuationSlicer = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JpegImageQuality">
    /**
     * Quality of JPEG images (0.0F = low to 1.0F = high)
     *
     * @return Quality of JPEG images
     */
    public float getJpegImageQuality() {
        return this.jpegImageQuality;
    }

    /**
     * Default quality of JPEG images (0.0F = low to 1.0F = high)
     *
     * @return Default quality of JPEG images
     */
    public float getDefaultJpegImageQuality() {
        return ModelDefinitions.JPEG_IMAGE_QUALITY_DEFAULT;
    }

    /**
     * Quality of JPEG images (0.0F = low to 1.0F = high)
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJpegImageQuality(float aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue < 0.0f || aValue > 1.0f) {
            return false;
        }

        // </editor-fold>
        if (this.jpegImageQuality != aValue) {
            this.jpegImageQuality = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ColorTransparencyCompartment">
    /**
     * Transparency value between 0.0 (no transparency) and 1.0 (full
     * transparency) for compartments
     *
     * @return Transparency value for compartments
     */
    public float getColorTransparencyCompartment() {
        return this.colorTransparencyCompartment;
    }

    /**
     * Default transparency value between 0.0 (no transparency) and 1.0 (full
     * transparency) for compartments
     *
     * @return Default transparency value for compartments
     */
    public float getDefaultColorTransparencyCompartment() {
        return ModelDefinitions.COLOR_TRANSPARENCY_DEFAULT;
    }

    /**
     * Transparency value between 0.0 (no transparency) and 1.0 (full
     * transparency) for compartments
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setColorTransparencyCompartment(float aValue) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValue < 0.0f || aValue > 1.0f) {
            return false;
        }

        // </editor-fold>
        if (this.colorTransparencyCompartment != aValue) {
            this.colorTransparencyCompartment = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- XshiftInPixelSlicer">
    /**
     * X-shift in pixel for simulation box slicer
     *
     * @return X-shift in pixel for simulation box slicer
     */
    public int getXshiftInPixelSlicer() {
        return this.xShiftInPixelSlicer;
    }

    /**
     * Default x-shift in pixel for simulation box slicer
     *
     * @return Default x-shift in pixel for simulation box slicer
     */
    public int getDefaultXshiftInPixelSlicer() {
        return ModelDefinitions.X_SHIFT_IN_PIXEL_SLICER_DEFAULT;
    }

    /**
     * X-shift in pixel for simulation box slicer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setXshiftInPixelSlicer(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.xShiftInPixelSlicer != aValue) {
            this.xShiftInPixelSlicer = aValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- YshiftInPixelSlicer">
    /**
     * Y-shift in pixel for simulation box slicer
     *
     * @return Y-shift in pixel for simulation box slicer
     */
    public int getYshiftInPixelSlicer() {
        return this.yShiftInPixelSlicer;
    }

    /**
     * Default y-shift in pixel for simulation box slicer
     *
     * @return Default y-shift in pixel for simulation box slicer
     */
    public int getDefaultYshiftInPixelSlicer() {
        return ModelDefinitions.Y_SHIFT_IN_PIXEL_SLICER_DEFAULT;
    }

    /**
     * Y-shift in pixel for simulation box slicer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setYshiftInPixelSlicer(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.yShiftInPixelSlicer != aValue) {
            this.yShiftInPixelSlicer = aValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RotationAroundXaxisAngle">
    /**
     * Angle for rotation around x axis in degree
     *
     * @return Angle for rotation around x axis in degree
     */
    public int getRotationAroundXaxisAngle() {
        return this.rotationAroundXaxisAngle;
    }

    /**
     * Default angle for rotation around x axis in degree
     *
     * @return Default angle for rotation around x axis in degree
     */
    public int getDefaultRotationAroundXaxisAngle() {
        return ModelDefinitions.ROTATION_AROUND_X_AXIS_ANGLE_DEFAULT;
    }

    /**
     * Angle for rotation around x axis in degree
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setRotationAroundXaxisAngle(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctRotationValue(aValue);
        if (this.rotationAroundXaxisAngle != tmpCorrectedValue) {
            this.rotationAroundXaxisAngle = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RotationAroundYaxisAngle">
    /**
     * Angle for rotation around y axis in degree
     *
     * @return Angle for rotation around y axis in degree
     */
    public int getRotationAroundYaxisAngle() {
        return this.rotationAroundYaxisAngle;
    }

    /**
     * Default angle for rotation around y axis in degree
     *
     * @return Default angle for rotation around y axis in degree
     */
    public int getDefaultRotationAroundYaxisAngle() {
        return ModelDefinitions.ROTATION_AROUND_Y_AXIS_ANGLE_DEFAULT;
    }

    /**
     * Angle for rotation around y axis in degree
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setRotationAroundYaxisAngle(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctRotationValue(aValue);
        if (this.rotationAroundYaxisAngle != tmpCorrectedValue) {
            this.rotationAroundYaxisAngle = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RotationAroundZaxisAngle">
    /**
     * Angle for rotation around z axis in degree
     *
     * @return Angle for rotation around z axis in degree
     */
    public int getRotationAroundZaxisAngle() {
        return this.rotationAroundZaxisAngle;
    }

    /**
     * Default angle for rotation around z axis in degree
     *
     * @return Default angle for rotation around z axis in degree
     */
    public int getDefaultRotationAroundZaxisAngle() {
        return ModelDefinitions.ROTATION_AROUND_Z_AXIS_ANGLE_DEFAULT;
    }

    /**
     * Angle for rotation around z axis in degree
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setRotationAroundZaxisAngle(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctRotationValue(aValue);
        if (this.rotationAroundZaxisAngle != tmpCorrectedValue) {
            this.rotationAroundZaxisAngle = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleShiftX">
    /**
     * Particle shift in percent of simulation box length along x-axis
     *
     * @return Particle shift in percent of simulation box length along x-axis
     */
    public int getParticleShiftX() {
        return this.particleShiftX;
    }

    /**
     * Default particle shift in percent of simulation box length along x-axis
     *
     * @return Default particle shift in percent of simulation box length along x-axis
     */
    public int getDefaultParticleShiftX() {
        return ModelDefinitions.PARTICLE_SHIFT_DEFAULT;
    }

    /**
     * Particle shift in percent of simulation box length along x-axis
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setParticleShiftX(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.PARTICLE_SHIFT_MINIMUM, ModelDefinitions.PARTICLE_SHIFT_MAXIMUM);
        if (this.particleShiftX != tmpCorrectedValue) {
            this.particleShiftX = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleShiftY">
    /**
     * Particle shift in percent of simulation box length along y-axis
     *
     * @return Particle shift in percent of simulation box length along y-axis
     */
    public int getParticleShiftY() {
        return this.particleShiftY;
    }

    /**
     * Default particle shift in percent of simulation box length along y-axis
     *
     * @return Default particle shift in percent of simulation box length along y-axis
     */
    public int getDefaultParticleShiftY() {
        return ModelDefinitions.PARTICLE_SHIFT_DEFAULT;
    }

    /**
     * Particle shift in percent of simulation box length along y-axis
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setParticleShiftY(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.PARTICLE_SHIFT_MINIMUM, ModelDefinitions.PARTICLE_SHIFT_MAXIMUM);
        if (this.particleShiftY != tmpCorrectedValue) {
            this.particleShiftY = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleShiftZ">
    /**
     * Particle shift in percent of simulation box length along z-axis
     *
     * @return Particle shift in percent of simulation box length along z-axis
     */
    public int getParticleShiftZ() {
        return this.particleShiftZ;
    }

    /**
     * Default particle shift in percent of simulation box length along z-axis
     *
     * @return Default particle shift in percent of simulation box length along z-axis
     */
    public int getDefaultParticleShiftZ() {
        return ModelDefinitions.PARTICLE_SHIFT_DEFAULT;
    }

    /**
     * Particle shift in percent of simulation box length along z-axis
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setParticleShiftZ(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.PARTICLE_SHIFT_MINIMUM, ModelDefinitions.PARTICLE_SHIFT_MAXIMUM);
        if (this.particleShiftZ != tmpCorrectedValue) {
            this.particleShiftZ = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RadialGradientPaintRadiusMagnification">
    /**
     * Radius magnification of RadialGradientPaint
     *
     * @return Radius magnification of RadialGradientPaint
     */
    public float getRadialGradientPaintRadiusMagnification() {
        return this.radialGradientPaintRadiusMagnification;
    }

    /**
     * Default size of specular white reflection
     *
     * @return Default size of specular white reflection
     */
    public float getDefaultRadialGradientPaintRadiusMagnification() {
        return ModelDefinitions.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION_DEFAULT;
    }

    /**
     * Radius magnification of RadialGradientPaint
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setRadialGradientPaintRadiusMagnification(float aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        float tmpCorrectedValue = ModelUtils.correctFloatValue(aValue, ModelDefinitions.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION_MINIMUM,
                ModelDefinitions.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION_MAXIMUM);
        if (this.radialGradientPaintRadiusMagnification != tmpCorrectedValue) {
            this.radialGradientPaintRadiusMagnification = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SpecularWhiteSizeSlicer">
    /**
     * Size of specular white reflection for slicer
     *
     * @return Size of specular white reflection for slicer
     */
    public float getSpecularWhiteSizeSlicer() {
        return this.specularWhiteSizeSlicer;
    }

    /**
     * Default size of specular white reflection for slicer
     *
     * @return Default size of specular white reflection for slicer
     */
    public float getDefaultSpecularWhiteSizeSlicer() {
        return ModelDefinitions.SPECULAR_WHITE_SIZE_SLICER_DEFAULT;
    }

    /**
     * Size of specular white reflection for slicer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setSpecularWhiteSizeSlicer(float aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        float tmpCorrectedValue = ModelUtils.correctFloatValue(aValue, ModelDefinitions.SPECULAR_WHITE_SIZE_SLICER_MINIMUM, ModelDefinitions.SPECULAR_WHITE_SIZE_SLICER_MAXIMUM);
        if (this.specularWhiteSizeSlicer != tmpCorrectedValue) {
            this.specularWhiteSizeSlicer = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RadialGradientPaintFocusFactorX">
    /**
     * Factor for focus of RadialGradientPaint in x-direction
     *
     * @return Factor for focus of RadialGradientPaint in x-direction
     */
    public float getRadialGradientPaintFocusFactorX() {
        return this.radialGradientPaintFocusFactorX;
    }

    /**
     * Default factor for focus of RadialGradientPaint in x-direction
     *
     * @return Default factor for focus of RadialGradientPaint in x-direction
     */
    public float getDefaultRadialGradientPaintFocusFactorX() {
        return ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X_DEFAULT;
    }

    /**
     * Factor for focus of RadialGradientPaint in x-direction
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setRadialGradientPaintFocusFactorX(float aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        float tmpCorrectedValue = ModelUtils.correctFloatValue(aValue, ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X_MINIMUM, ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X_MAXIMUM);
        if (this.radialGradientPaintFocusFactorX != tmpCorrectedValue) {
            this.radialGradientPaintFocusFactorX = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- RadialGradientPaintFocusFactorY">
    /**
     * Factor for focus of RadialGradientPaint in y-direction
     *
     * @return Factor for focus of RadialGradientPaint in y-direction
     */
    public float getRadialGradientPaintFocusFactorY() {
        return this.radialGradientPaintFocusFactorY;
    }

    /**
     * Default factor for focus of RadialGradientPaint in y-direction
     *
     * @return Default factor for focus of RadialGradientPaint in y-direction
     */
    public float getDefaultRadialGradientPaintFocusFactorY() {
        return ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y_DEFAULT;
    }

    /**
     * Factor for focus of RadialGradientPaint in y-direction
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setRadialGradientPaintFocusFactorY(float aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        float tmpCorrectedValue = ModelUtils.correctFloatValue(aValue, ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y_MINIMUM, ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y_MAXIMUM);
        if (this.radialGradientPaintFocusFactorY != tmpCorrectedValue) {
            this.radialGradientPaintFocusFactorY = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DelayForFilesInMilliseconds">
    /**
     * Delay in milliseconds for directory/file delete operations: This is
     * necessary due to OS specific relations between files and directories to
     * be deleted: 100 Milliseconds works on WinXP OS. A value less than or
     * equal to zero leads means no delay.
     *
     * @return Delay in Milliseconds
     */
    public long getDelayForFilesInMilliseconds() {
        return this.delayForFilesInMilliseconds;
    }

    /**
     * Default delay in milliseconds for directory/file delete operations: This
     * is necessary due to OS specific relations between files and directories
     * to be deleted: 100 Milliseconds works on WinXP OS. A value less than or
     * equal to zero leads means no delay.
     *
     * @return Default delay in Milliseconds
     */
    public long getDefaultDelayForFilesInMilliseconds() {
        return ModelDefinitions.DEFAULT_DELAY_FOR_FILES_IN_MILLISECONDS;
    }

    /**
     * Minimum delay in milliseconds for directory/file delete operations: This
     * is necessary due to OS specific relations between files and directories
     * to be deleted: 100 Milliseconds works on WinXP OS. A value less than or
     * equal to zero leads means no delay.
     *
     * @return Minimum delay in Milliseconds
     */
    public long getMinimumDelayForFilesInMilliseconds() {
        return ModelDefinitions.MINIMUM_DELAY_FOR_FILES_IN_MILLISECONDS;
    }

    /**
     * Maximum delay in milliseconds for directory/file delete operations: This
     * is necessary due to OS specific relations between files and directories
     * to be deleted: 100 Milliseconds works on WinXP OS. A value less than or
     * equal to zero leads means no delay.
     *
     * @return Maximum delay in Milliseconds
     */
    public long getMaximumDelayForFilesInMilliseconds() {
        return ModelDefinitions.MAXIMUM_DELAY_FOR_FILES_IN_MILLISECONDS;
    }

    /**
     * Delay in milliseconds for directory/file delete operations: This is
     * necessary due to OS specific relations between files and directories to
     * be deleted: 100 Milliseconds works on WinXP OS. A value less than or
     * equal to zero leads means no delay. NOTE: Passed value is corrected.
     *
     * @param aDelayForFilesInMilliseconds Delay for files in Milliseconds
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDelayForFilesInMilliseconds(long aDelayForFilesInMilliseconds) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        long tmpCorrectedValue = ModelUtils.correctLongValue(aDelayForFilesInMilliseconds, this.getMinimumDelayForFilesInMilliseconds(), this.getMaximumDelayForFilesInMilliseconds());
        if (this.delayForFilesInMilliseconds != tmpCorrectedValue) {
            this.delayForFilesInMilliseconds = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DelayForJobStartInMilliseconds">
    /**
     * Delay for job start in milliseconds: A value less than or equal to zero
     * means no delay.
     *
     * @return Delay in Milliseconds
     */
    public long getDelayForJobStartInMilliseconds() {
        return this.delayForJobStartInMilliseconds;
    }

    /**
     * Default delay for job start in milliseconds: A value less than or equal
     * to zero means no delay.
     *
     * @return Default delay in Milliseconds
     */
    public long getDefaultDelayForJobStartInMilliseconds() {
        return ModelDefinitions.DEFAULT_DELAY_FOR_JOB_START_IN_MILLISECONDS;
    }

    /**
     * Minimum delay for job start in milliseconds: A value less than or equal
     * to zero means no delay.
     *
     * @return Minimum delay in Milliseconds
     */
    public long getMinimumDelayForJobStartInMilliseconds() {
        return ModelDefinitions.MINIMUM_DELAY_FOR_JOB_START_IN_MILLISECONDS;
    }

    /**
     * Maximum delay for job start in milliseconds: A value less than or equal
     * to zero means no delay.
     *
     * @return Maximum delay in Milliseconds
     */
    public long getMaximumDelayForJobStartInMilliseconds() {
        return ModelDefinitions.MAXIMUM_DELAY_FOR_JOB_START_IN_MILLISECONDS;
    }

    /**
     * Delay for job start in milliseconds: A value less than or equal to zero
     * means no delay.
     *
     * @param aDelayForJobStartInMilliseconds Delay for files in Milliseconds
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDelayForJobStartInMilliseconds(long aDelayForJobStartInMilliseconds) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        long tmpCorrectedValue = ModelUtils.correctLongValue(aDelayForJobStartInMilliseconds, this.getMinimumDelayForJobStartInMilliseconds(), this.getMaximumDelayForJobStartInMilliseconds());
        if (this.delayForJobStartInMilliseconds != tmpCorrectedValue) {
            this.delayForJobStartInMilliseconds = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobResultArchiveStepFileInclusion">
    /**
     * True: Position step files are included in Job Result archive files,
     * false: Position step files are omitted in Job Result archive files
     *
     * @return True: Position step files are included in Job Result archive
     * files, false: Position step files are omitted in Job Result archive files
     */
    public boolean isJobResultArchiveStepFileInclusion() {
        return this.isJobResultArchiveStepFileInclusion;
    }

    /**
     * Default Job Result archive step file inclusion flag
     *
     * @return Default Job Result archive step file inclusion flag
     */
    public boolean getDefaultJobResultArchiveStepFileInclusion() {
        return ModelDefinitions.IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION_DEFAULT;
    }

    /**
     * True: Position step files are included in Job Result archive files,
     * false: Position step files are omitted in Job Result archive files
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobResultArchiveStepFileInclusion(boolean aValue) {
        if (this.isJobResultArchiveStepFileInclusion != aValue) {
            this.isJobResultArchiveStepFileInclusion = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- VolumeScalingForConcentrationCalculation">
    /**
     * Volume scaling for concentration calculation. True: Molecule numbers
     * are calculated with volume scaling, false: Molecule numbers are 
     * calculated without volume scaling (i.e. scale factors are all 1.0)
     *
     * @return True: Molecule numbers are calculated with volume scaling, 
     * false: Molecule numbers are calculated without volume scaling (i.e. 
     * scale factors are all 1.0)
     */
    public boolean isVolumeScalingForConcentrationCalculation() {
        return this.isVolumeScalingForConcentrationCalculation;
    }

    /**
     * Default volume scaling for concentration calculation flag
     *
     * @return Default volume scaling for concentration calculation flag
     */
    public boolean getDefaultVolumeScalingForConcentrationCalculation() {
        return ModelDefinitions.IS_VOLUME_SCALING_FOR_CONCENTRATION_CALCULATION_DEFAULT;
    }

    /**
     * Volume scaling for concentration calculation. True: Molecule numbers
     * are calculated with volume scaling, false: Molecule numbers are 
     * calculated without volume scaling (i.e. scale factors are all 1.0)
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setVolumeScalingForConcentrationCalculation(boolean aValue) {
        if (this.isVolumeScalingForConcentrationCalculation != aValue) {
            this.isVolumeScalingForConcentrationCalculation = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobInputInclusion">
    /**
     * True: Job inputs included in Job Result display,
     * false: Job inputs are omitted in Job Result display
     *
     * @return True: Job inputs included in Job Result display,
     * false: Job inputs are omitted in Job Result display
     */
    public boolean isJobInputInclusion() {
        return this.isJobInputInclusion;
    }

    /**
     * Default Job Input inclusion flag
     *
     * @return Default Job Input inclusion flag
     */
    public boolean getDefaultJobInputInclusion() {
        return ModelDefinitions.IS_JOB_INPUT_INCLUSION_DEFAULT;
    }

    /**
     * True: Job inputs included in Job Result display,
     * false: Job inputs are omitted in Job Result display
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobInputInclusion(boolean aValue) {
        if (this.isJobInputInclusion != aValue) {
            this.isJobInputInclusion = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleDistributionInclusion">
    /**
     * True: Particle distribution analysis included in Job Result display,
     * false: Particle distribution analysis is omitted in Job Result display
     *
     * @return True: Particle distribution analysis included in Job Result 
     * display, false: Particle distribution analysis is omitted in Job Result 
     * display
     */
    public boolean isParticleDistributionInclusion() {
        return this.isParticleDistributionInclusion;
    }

    /**
     * Default particle distribution inclusion flag
     *
     * @return Default particle distribution inclusion flag
     */
    public boolean getDefaultParticleDistributionInclusion() {
        return ModelDefinitions.IS_PARTICLE_DISTRIBUTION_INCLUSION_DEFAULT;
    }

    /**
     * True: Particle distribution analysis included in Job Result display,
     * false: Particle distribution analysis is omitted in Job Result display
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setParticleDistributionInclusion(boolean aValue) {
        if (this.isParticleDistributionInclusion != aValue) {
            this.isParticleDistributionInclusion = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SimulationStepInclusion">
    /**
     * True: Simulation steps are included in Job Result display,
     * false: Simulation steps are omitted in Job Result display
     *
     * @return True: Simulation steps are included in Job Result display,
     * false: Simulation steps are omitted in Job Result display
     */
    public boolean isSimulationStepInclusion() {
        return this.isSimulationStepInclusion;
    }

    /**
     * Default simulation steps inclusion flag
     *
     * @return Default simulation steps inclusion flag
     */
    public boolean getDefaultSimulationStepInclusion() {
        return ModelDefinitions.IS_SIMULATION_STEP_INCLUSION_DEFAULT;
    }

    /**
     * True: Simulation steps are included in Job Result display,
     * false: Simulation steps are omitted in Job Result display
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setSimulationStepInclusion(boolean aValue) {
        if (this.isSimulationStepInclusion != aValue) {
            this.isSimulationStepInclusion = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NearestNeighborEvaluationInclusion">
    /**
     * True: Nearest-neighbour evaluation is included in Job Result display,
     * false: Nearest-neighbour evaluation is omitted in Job Result display
     *
     * @return True: Nearest-neighbour evaluation is included in Job Result 
     * display, false: Nearest-neighbour evaluation is omitted in Job Result 
     * display
     */
    public boolean isNearestNeighborEvaluationInclusion() {
        return this.isNearestNeighborEvaluationInclusion;
    }

    /**
     * Default nearest-neighbour evaluation inclusion flag
     *
     * @return Default nearest-neighbour evaluation inclusion flag
     */
    public boolean getDefaultNearestNeighborEvaluationInclusion() {
        return ModelDefinitions.IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION_DEFAULT;
    }

    /**
     * True: Nearest-neighbour evaluation is included in Job Result display,
     * false: Nearest-neighbour evaluation is omitted in Job Result display
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNearestNeighborEvaluationInclusion(boolean aValue) {
        if (this.isNearestNeighborEvaluationInclusion != aValue) {
            this.isNearestNeighborEvaluationInclusion = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobResultArchiveProcessParallelInBackground">
    /**
     * True: Job Result archive files are generated parallel in background,
     * false: Job Result archive files are generated in sequential manner with
     * progress bar
     *
     * @return True: Job Result archive files are generated parallel in
     * background, false: Job Result archive files are generated in sequential
     * manner with progress bar
     */
    public boolean isJobResultArchiveProcessParallelInBackground() {
        return this.isJobResultArchiveProcessParallelInBackground;
    }

    /**
     * Default Job Result archive process parallel in background flag
     *
     * @return Default Job Result archive process parallel in background flag
     */
    public boolean getDefaultJobResultArchiveProcessParallelInBackground() {
        return ModelDefinitions.IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND_DEFAULT;
    }

    /**
     * True: Job Result archive files are generated parallel in background,
     * false: Job Result archive files are generated in sequential manner with
     * progress bar
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobResultArchiveProcessParallelInBackground(boolean aValue) {
        if (this.isJobResultArchiveProcessParallelInBackground != aValue) {
            this.isJobResultArchiveProcessParallelInBackground = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobResultArchiveFileUncompressed">
    /**
     * True: Job Result archive file is uncompressed, false: Otherwise (archive
     * file is compressed)
     *
     * @return True: Job Result archive file is uncompressed, false: Otherwise
     * (archive file is compressed)
     */
    public boolean isJobResultArchiveFileUncompressed() {
        return this.isJobResultArchiveFileUncompressed;
    }

    /**
     * Default Job Result archive file compression flag
     *
     * @return Default Default Job Result archive file compression flag
     */
    public boolean getDefaultJobResultArchiveFileUncompressed() {
        return ModelDefinitions.IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED_DEFAULT;
    }

    /**
     * True: Job Result archive file is uncompressed, false: Otherwise (archive
     * file is compressed)
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobResultArchiveFileUncompressed(boolean aValue) {
        if (this.isJobResultArchiveFileUncompressed != aValue) {
            this.isJobResultArchiveFileUncompressed = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JdpdLogLevelExceptions">
    /**
     * True: Jdpd log level EXCEPTIONS is used, false: All available Jdpd log 
     * levels are used
     *
     * @return Jdpd log level exceptions flag
     */
    public boolean isJdpdLogLevelExceptions() {
        return this.isJdpdLogLevelExceptions;
    }

    /**
     * Default Jdpd log level exceptions flag
     *
     * @return Default Jdpd log level exceptions flag
     */
    public boolean getDefaultJdpdLogLevelExceptions() {
        return ModelDefinitions.IS_JDPD_LOG_LEVEL_EXCEPTIONS_DEFAULT;
    }

    /**
     * True: Jdpd log level EXCEPTIONS is used, false: All available Jdpd log 
     * levels are used
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJdpdLogLevelExceptions(boolean aValue) {
        if (this.isJdpdLogLevelExceptions != aValue) {
            this.isJdpdLogLevelExceptions = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ConstantCompartmentBodyVolume">
    /**
     * Compartment constant body flag
     * True: Compartment body volume is constant/preserved, false: Otherwise
     *
     * @return True: Compartment body volume is constant/preserved, 
     * false: Otherwise
     */
    public boolean isConstantCompartmentBodyVolume() {
        return this.isConstantCompartmentBodyVolume;
    }

    /**
     * Default compartment constant body flag
     *
     * @return Default compartment constant body flag
     */
    public boolean getDefaultConstantCompartmentBodyVolumeFlag() {
        return ModelDefinitions.IS_CONSTANT_COMPARTMENT_BODY_VOLUME_DEFAULT;
    }

    /**
     * Compartment constant body flag
     * True: Compartment body volume is constant/preserved, false: Otherwise
     * 
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setConstantCompartmentBodyVolumeFlag(boolean aValue) {
        if (this.isConstantCompartmentBodyVolume != aValue) {
            this.isConstantCompartmentBodyVolume = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SimulationBoxSlicer">
    /**
     * True: Simulation box slicer is used for simulation box display, false:
     * Jmol viewer is used for simulation box display
     *
     * @return True: Simulation box slicer is used for simulation box display,
     * false: Jmol viewer is used for simulation box display
     */
    public boolean isSimulationBoxSlicer() {
        return this.isSimulationBoxSlicer;
    }

    /**
     * Default simulation box slicer flag
     *
     * @return Default simulation box slicer flag
     */
    public boolean isDefaultSimulationBoxSlicer() {
        return ModelDefinitions.IS_SIMULATION_BOX_SLICER_DEFAULT;
    }

    /**
     * True: Simulation box slicer is used for simulation box display, false:
     * Jmol viewer is used for simulation box display
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setSimulationBoxSlicer(boolean aValue) {
        if (this.isSimulationBoxSlicer != aValue) {
            this.isSimulationBoxSlicer = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfSlicesPerView">
    /**
     * Number of slices for simulation box slicer per view
     *
     * @return Number of slices for simulation box slicer per view
     */
    public int getNumberOfSlicesPerView() {
        return this.numberOfSlicesPerView;
    }

    /**
     * Default number of slices for simulation box slicer per view
     *
     * @return Default number of slices for simulation box slicer per view
     */
    public int getDefaultNumberOfSlicesPerView() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_SLICES;
    }

    /**
     * Number of slices for simulation box slicer per view
     *
     * @param aValue Number of slices for simulation box slicer per view
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfSlicesPerView(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_SLICES, ModelDefinitions.MAXIMUM_NUMBER_OF_SLICES);
        if (this.numberOfSlicesPerView != tmpCorrectedValue) {
            this.numberOfSlicesPerView = tmpCorrectedValue;
            tmpHasChanged = true;
            // IMPORTANT: Correct first slice index if necessary
            if (this.firstSliceIndex > this.numberOfSlicesPerView - 1) {
                this.setFirstSliceIndex(this.getDefaultFirstSliceIndex());
            }
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JmolShadePower">
    /**
     * Shade power (integer values 1, 2, 3) for Jmol viewer
     *
     * @return Shade power (integer values 1, 2, 3) for Jmol viewer
     */
    public int getJmolShadePower() {
        return this.jmolShadePower;
    }

    /**
     * Default shade power (integer values 1, 2, 3) for Jmol viewer
     *
     * @return Default shade power (integer values 1, 2, 3) for Jmol viewer
     */
    public int getDefaultJmolShadePower() {
        return ModelDefinitions.DEFAULT_JMOL_SHADE_POWER;
    }

    /**
     * Shade power (integer values 1, 2, 3) for Jmol viewer
     *
     * @param aValue Shade power (integer values 1, 2, 3) for Jmol viewer
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJmolShadePower(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_JMOL_SHADE_POWER, ModelDefinitions.MAXIMUM_JMOL_SHADE_POWER);
        if (this.jmolShadePower != tmpCorrectedValue) {
            this.jmolShadePower = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JmolAmbientLightPercentage">
    /**
     * Ambient light percentage (range 0 - 100) for Jmol viewer
     *
     * @return Ambient light percentage (range 0 - 100) for Jmol viewer
     */
    public int getJmolAmbientLightPercentage() {
        return this.jmolAmbientLightPercentage;
    }

    /**
     * Default ambient light percentage (range 0 - 100) for Jmol viewer
     *
     * @return Default ambient light percentage (range 0 - 100) for Jmol viewer
     */
    public int getDefaultJmolAmbientLightPercentage() {
        return ModelDefinitions.DEFAULT_JMOL_AMBIENT_LIGHT_PERCENTAGE;
    }

    /**
     * Ambient light percentage (range 0 - 100) for Jmol viewer
     *
     * @param aValue Ambient light percentage (range 0 - 100) for Jmol viewer
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJmolAmbientLightPercentage(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_JMOL_AMBIENT_LIGHT_PERCENTAGE, ModelDefinitions.MAXIMUM_JMOL_AMBIENT_LIGHT_PERCENTAGE);
        if (this.jmolAmbientLightPercentage != tmpCorrectedValue) {
            this.jmolAmbientLightPercentage = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JmolDiffuseLightPercentage">
    /**
     * Diffuse light percentage (range 0 - 100) for Jmol viewer
     *
     * @return Diffuse light percentage (range 0 - 100) for Jmol viewer
     */
    public int getJmolDiffuseLightPercentage() {
        return this.jmolDiffuseLightPercentage;
    }

    /**
     * Default diffuse light percentage (range 0 - 100) for Jmol viewer
     *
     * @return Default diffuse light percentage (range 0 - 100) for Jmol viewer
     */
    public int getDefaultJmolDiffuseLightPercentage() {
        return ModelDefinitions.DEFAULT_JMOL_DIFFUSE_LIGHT_PERCENTAGE;
    }

    /**
     * Diffuse light percentage (range 0 - 100) for Jmol viewer
     *
     * @param aValue Diffuse light percentage (range 0 - 100) for Jmol viewer
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJmolDiffuseLightPercentage(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_JMOL_DIFFUSE_LIGHT_PERCENTAGE, ModelDefinitions.MAXIMUM_JMOL_DIFFUSE_LIGHT_PERCENTAGE);
        if (this.jmolDiffuseLightPercentage != tmpCorrectedValue) {
            this.jmolDiffuseLightPercentage = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JmolSpecularReflectionExponent">
    /**
     * Specular reflection exponent (range 1 - 10) for Jmol viewer
     *
     * @return Specular reflection exponent (range 1 - 10) for Jmol viewer
     */
    public int getJmolSpecularReflectionExponent() {
        return this.jmolSpecularReflectionExponent;
    }

    /**
     * Default specular reflection exponent (range 1 - 10) for Jmol viewer
     *
     * @return Default specular reflection exponent (range 1 - 10) for Jmol
     * viewer
     */
    public int getDefaultJmolSpecularReflectionExponent() {
        return ModelDefinitions.DEFAULT_JMOL_SPECULAR_REFLECTION_EXPONENT;
    }

    /**
     * Specular reflection exponent (range 1 - 10) for Jmol viewer
     *
     * @param aValue Specular reflection exponent (range 1 - 10) for Jmol viewer
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJmolSpecularReflectionExponent(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_JMOL_SPECULAR_REFLECTION_EXPONENT, ModelDefinitions.MAXIMUM_JMOL_SPECULAR_REFLECTION_EXPONENT);
        if (this.jmolSpecularReflectionExponent != tmpCorrectedValue) {
            this.jmolSpecularReflectionExponent = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JmolSpecularReflectionPercentage">
    /**
     * Specular reflection percentage (range 0 - 100) for Jmol viewer
     *
     * @return Specular reflection percentage (range 0 - 100) for Jmol viewer
     */
    public int getJmolSpecularReflectionPercentage() {
        return this.jmolSpecularReflectionPercentage;
    }

    /**
     * Default specular reflection percentage (range 0 - 100) for Jmol viewer
     *
     * @return Default specular reflection percentage (range 0 - 100) for Jmol
     * viewer
     */
    public int getDefaultJmolSpecularReflectionPercentage() {
        return ModelDefinitions.DEFAULT_JMOL_SPECULAR_REFLECTION_PERCENTAGE;
    }

    /**
     * Specular reflection percentage (range 0 - 100) for Jmol viewer
     *
     * @param aValue Specular reflection percentage (range 0 - 100) for Jmol
     * viewer
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJmolSpecularReflectionPercentage(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_JMOL_SPECULAR_REFLECTION_PERCENTAGE, ModelDefinitions.MAXIMUM_JMOL_SPECULAR_REFLECTION_PERCENTAGE);
        if (this.jmolSpecularReflectionPercentage != tmpCorrectedValue) {
            this.jmolSpecularReflectionPercentage = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JmolSpecularReflectionPower">
    /**
     * Specular reflection power (range 0 - 100) for Jmol viewer
     *
     * @return Specular reflection power (range 0 - 100) for Jmol viewer
     */
    public int getJmolSpecularReflectionPower() {
        return this.jmolSpecularReflectionPower;
    }

    /**
     * Default specular reflection power (range 0 - 100) for Jmol viewer
     *
     * @return Default specular reflection power (range 0 - 100) for Jmol viewer
     */
    public int getDefaultJmolSpecularReflectionPower() {
        return ModelDefinitions.DEFAULT_JMOL_SPECULAR_REFLECTION_POWER;
    }

    /**
     * Specular reflection power (range 0 - 100) for Jmol viewer
     *
     * @param aValue Specular reflection power (range 0 - 100) for Jmol viewer
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJmolSpecularReflectionPower(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_JMOL_SPECULAR_REFLECTION_POWER, ModelDefinitions.MAXIMUM_JMOL_SPECULAR_REFLECTION_POWER);
        if (this.jmolSpecularReflectionPower != tmpCorrectedValue) {
            this.jmolSpecularReflectionPower = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- FirstSliceIndex">
    /**
     * Index of first slice for simulation box slicer
     *
     * @return Index of first slice for simulation box slicer
     */
    public int getFirstSliceIndex() {
        return this.firstSliceIndex;
    }

    /**
     * Default index of first slice for simulation box slicer
     *
     * @return Default index of first slice for simulation box slicer
     */
    public int getDefaultFirstSliceIndex() {
        return ModelDefinitions.DEFAULT_FIRST_SLICE_INDEX;
    }

    /**
     * Index of first slice for simulation box slicer
     *
     * @param aValue Index of first slice for simulation box slicer
     * @return True: Value changed, false: Otherwise
     */
    public boolean setFirstSliceIndex(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.firstSliceIndex != aValue) {
            if (aValue > this.numberOfSlicesPerView - 1) {
                if (this.firstSliceIndex != this.getDefaultFirstSliceIndex()) {
                    this.firstSliceIndex = this.getDefaultFirstSliceIndex();
                    tmpHasChanged = true;
                }
            } else {
                this.firstSliceIndex = aValue;
                tmpHasChanged = true;
            }
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfBoxWaitSteps">
    /**
     * Number of simulation box slicer wait steps
     *
     * @return Number of simulation box slicer wait steps
     */
    public int getNumberOfBoxWaitSteps() {
        return this.numberOfBoxWaitSteps;
    }

    /**
     * Default number of simulation box slicer wait steps
     *
     * @return Default number of simulation box slicer wait steps
     */
    public int getDefaultNumberOfBoxWaitSteps() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_BOX_WAIT_STEPS;
    }

    /**
     * Number of simulation box slicer wait steps
     *
     * @param aValue Number of simulation box slicer wait steps
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfBoxWaitSteps(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_BOX_WAIT_STEPS, Integer.MAX_VALUE);
        if (this.numberOfBoxWaitSteps != tmpCorrectedValue) {
            this.numberOfBoxWaitSteps = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfFramePointsSlicer">
    /**
     * Number of frame points for simulation box slicer
     *
     * @return Number of frame points for simulation box slicer
     */
    public int getNumberOfFramePointsSlicer() {
        return this.numberOfFramePointsSlicer;
    }

    /**
     * Default number of frame points for simulation box slicer
     *
     * @return Default number of frame points for simulation box slicer
     */
    public int getDefaultNumberOfFramePointsSlicer() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_FRAME_POINTS_SLICER;
    }

    /**
     * Number of frame points for simulation box slicer
     *
     * @param aValue Number of frame points for simulation box slicer
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfFramePointsSlicer(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_FRAME_POINTS_SLICER, Integer.MAX_VALUE);
        if (this.numberOfFramePointsSlicer != tmpCorrectedValue) {
            this.numberOfFramePointsSlicer = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- TimeStepDisplaySlicer">
    /**
     * Time step display for simulation box slicer. timeStepDisplaySlicer = 1:
     * Every time step will be displayed, timeStepDisplaySlicer = 2: Every
     * second time step will be displayed etc.
     *
     * @return Time step display for simulation box slicer
     */
    public int getTimeStepDisplaySlicer() {
        return this.timeStepDisplaySlicer;
    }

    /**
     * Default time step display for simulation box slicer.
     * timeStepDisplaySlicer = 1: Every time step will be displayed,
     * timeStepDisplaySlicer = 2: Every second time step will be displayed etc.
     *
     * @return Default time step display for simulation box slicer
     */
    public int getDefaultTimeStepDisplaySlicer() {
        return ModelDefinitions.DEFAULT_TIME_STEP_DISPLAY_SLICER;
    }

    /**
     * Time step display for simulation box slicer. timeStepDisplaySlicer = 1:
     * Every time step will be displayed, timeStepDisplaySlicer = 2: Every
     * second time step will be displayed etc.
     *
     * @param aValue Time step display for simulation box slicer
     * @return True: Value changed, false: Otherwise
     */
    public boolean setTimeStepDisplaySlicer(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_TIME_STEP_DISPLAY_SLICER, ModelDefinitions.MAXIMUM_TIME_STEP_DISPLAY_SLICER);
        if (this.timeStepDisplaySlicer != tmpCorrectedValue) {
            this.timeStepDisplaySlicer = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- StepInfoArray">
    /**
     * Array with step infos. NOTE: This is NOT made persistent!
     *
     * @param anArray Array with step infos
     */
    public void setStepInfoArray(String[] anArray) {
        this.stepInfoArray = anArray;
        this.firstStepInfo = null;
        this.lastStepInfo = null;
    }

    /**
     * Returns if time step info array is set
     *
     * @return True: Time step info array is set, false: Otherwise
     */
    public boolean hasStepInfoArray() {
        return this.stepInfoArray != null;
    }

    /**
     * Removes time step info array
     */
    public void removeStepInfoArray() {
        this.stepInfoArray = null;
        this.firstStepInfo = null;
        this.lastStepInfo = null;
    }

    /**
     * Returns first step info (may be null)
     *
     * @return First step info (may be null)
     */
    public String getFirstStepInfo() {
        return this.firstStepInfo;
    }

    /**
     * Returns last step info (may be null)
     *
     * @return Last step info (may be null)
     */
    public String getLastStepInfo() {
        return this.lastStepInfo;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfParallelSimulations">
    /**
     * Number of parallel simulations
     *
     * @return Number of parallel simulations
     */
    public int getNumberOfParallelSimulations() {
        return this.numberOfParallelSimulations;
    }

    /**
     * Default number of parallel simulations
     *
     * @return Default number of parallel simulations
     */
    public int getDefaultNumberOfParallelSimulations() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_PROCESSOR_CORES;
    }

    /**
     * Number of parallel simulations
     *
     * @param aValue Number of parallel simulations
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfParallelSimulations(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_PROCESSOR_CORES, ModelDefinitions.MAXIMUM_NUMBER_OF_PROCESSOR_CORES);
        if (this.numberOfParallelSimulations != tmpCorrectedValue) {
            this.numberOfParallelSimulations = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfParallelSlicers">
    /**
     * Number of parallel slicers
     *
     * @return Number of parallel slicers
     */
    public int getNumberOfParallelSlicers() {
        return this.numberOfParallelSlicers;
    }

    /**
     * Default number of parallel slicers
     * NOTE: This is identical to this.numberOfParallelSimulations
     *
     * @return Default number of parallel slicers
     */
    public int getDefaultNumberOfParallelSlicers() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_PARALLEL_SLICERS;
    }

    /**
     * Number of parallel slicers
     *
     * @param aValue Number of parallel slicers
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfParallelSlicers(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        // NOTE: This is identical to this.numberOfParallelSimulations
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_PARALLEL_SLICERS, ModelDefinitions.MAXIMUM_NUMBER_OF_PARALLEL_SLICERS);
        if (this.numberOfParallelSlicers != tmpCorrectedValue) {
            this.numberOfParallelSlicers = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfParallelCalculators">
    /**
     * Number of parallel calculators
     *
     * @return Number of parallel calculators
     */
    public int getNumberOfParallelCalculators() {
        return this.numberOfParallelCalculators;
    }

    /**
     * Default number of parallel calculators
     * NOTE: This is identical to this.numberOfParallelSimulations
     *
     * @return Default number of parallel calculators
     */
    public int getDefaultNumberOfParallelCalculators() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_PROCESSOR_CORES;
    }

    /**
     * Number of parallel calculators
     *
     * @param aValue Number of parallel calculators
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfParallelCalculators(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        // NOTE: This is identical to this.numberOfParallelSimulations
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_PROCESSOR_CORES, ModelDefinitions.MAXIMUM_NUMBER_OF_PROCESSOR_CORES);
        if (this.numberOfParallelCalculators != tmpCorrectedValue) {
            this.numberOfParallelCalculators = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfParallelParticlePositionWriters">
    /**
     * Number of parallel particle position writers
     *
     * @return Number of parallel particle position writers
     */
    public int getNumberOfParallelParticlePositionWriters() {
        return this.numberOfParallelParticlePositionWriters;
    }

    /**
     * Default number of parallel particle position writers
     * NOTE: This is identical to this.numberOfParallelSimulations
     *
     * @return Default number of parallel particle position writers
     */
    public int getDefaultNumberOfParallelParticlePositionWriters() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_PROCESSOR_CORES;
    }

    /**
     * Number of parallel particle position writers
     *
     * @param aValue Number of parallel particle position writers
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfParallelParticlePositionWriters(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        // NOTE: This is identical to this.numberOfParallelSimulations
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_PROCESSOR_CORES, ModelDefinitions.MAXIMUM_NUMBER_OF_PROCESSOR_CORES);
        if (this.numberOfParallelParticlePositionWriters != tmpCorrectedValue) {
            this.numberOfParallelParticlePositionWriters = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfAfterDecimalDigitsForParticlePositions">
    /**
     * Number of after-decimal-separator digits for particle positions
     *
     * @return Number of after-decimal-separator digits for particle positions
     */
    public int getNumberOfAfterDecimalDigitsForParticlePositions() {
        return this.numberOfAfterDecimalDigitsForParticlePositions;
    }

    /**
     * Default number of after-decimal-separator digits for particle positions
     *
     * @return Default number of after-decimal-separator digits for particle positions
     */
    public int getDefaultNumberOfAfterDecimalDigitsForParticlePositions() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS;
    }

    /**
     * Number of after-decimal-separator digits for particle positions
     *
     * @param aValue Number of after-decimal-separator digits for particle positions
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfAfterDecimalDigitsForParticlePositions(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS, ModelDefinitions.MAXIMUM_NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS);
        if (this.numberOfAfterDecimalDigitsForParticlePositions != tmpCorrectedValue) {
            this.numberOfAfterDecimalDigitsForParticlePositions = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MaximumNumberOfPositionCorrectionTrials">
    /**
     * Maximum number of position correction trials
     *
     * @return Maximum number of position correction trials
     */
    public int getMaximumNumberOfPositionCorrectionTrials() {
        return this.maximumNumberOfPositionCorrectionTrials;
    }

    /**
     * Default maximum number of position correction trials
     *
     * @return Default maximum number of position correction trials
     */
    public int getDefaultMaximumNumberOfPositionCorrectionTrials() {
        return ModelDefinitions.DEFAULT_MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS;
    }

    /**
     * Maximum number of position correction trials
     *
     * @param aValue Maximum number of position correction trials
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMaximumNumberOfPositionCorrectionTrials(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = 
            ModelUtils.correctIntegerValue(
                aValue, 
                ModelDefinitions.MINIMUM_MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS
            );
        if (this.maximumNumberOfPositionCorrectionTrials != tmpCorrectedValue) {
            this.maximumNumberOfPositionCorrectionTrials = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MovieQuality">
    /**
     * Quality of movies
     *
     * @return Quality of movies
     */
    public int getMovieQuality() {
        return this.movieQuality;
    }

    /**
     * Default quality of movies
     *
     * @return Default quality of movies
     */
    public int getDefaultMovieQuality() {
        return ModelDefinitions.DEFAULT_MOVIE_QUALITY;
    }

    /**
     * Quality of movies
     *
     * @param aValue Quality of movies
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMovieQuality(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_MOVIE_QUALITY, ModelDefinitions.MAXIMUM_MOVIE_QUALITY);
        if (this.movieQuality != tmpCorrectedValue) {
            this.movieQuality = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- TimerIntervalInMilliseconds">
    /**
     * Timer interval in milliseconds
     *
     * @return Timer interval in milliseconds
     */
    public int getTimerIntervalInMilliseconds() {
        return this.timerIntervalInMilliseconds;
    }

    /**
     * Default timer interval in milliseconds
     *
     * @return Default timer interval in milliseconds
     */
    public int getDefaultTimerIntervalInMilliseconds() {
        return ModelDefinitions.DEFAULT_TIMER_INTERVAL_IN_MILLISECONDS;
    }

    /**
     * Timer interval in milliseconds. NOTE: Passed value is corrected.
     *
     * @param aTimerIntervalInMilliseconds Timer interval in milliseconds
     * @return True: Value changed, false: Otherwise
     */
    public boolean setTimerIntervalInMilliseconds(int aTimerIntervalInMilliseconds) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aTimerIntervalInMilliseconds, ModelDefinitions.MINIMUM_TIMER_INTERVAL_IN_MILLISECONDS,
                ModelDefinitions.MAXIMUM_TIMER_INTERVAL_IN_MILLISECONDS);
        if (this.timerIntervalInMilliseconds != tmpCorrectedValue) {
            this.timerIntervalInMilliseconds = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MinimumBondLengthDpd">
    /**
     * Minimum bond length in DPD units
     *
     * @return Minimum bond length in DPD units
     */
    public double getMinimumBondLengthDpd() {
        return this.minimumBondLengthDpd;
    }

    /**
     * Default minimum bond length in DPD units
     *
     * @return Default minimum bond length in DPD units
     */
    public double getDefaultMinimumBondLengthDpd() {
        return ModelDefinitions.MINIMUM_BOND_LENGTH_DPD_DEFAULT;
    }

    /**
     * Minimum bond length in DPD units
     *
     * @param aMinimumBondLengthDpd Minimum bond length in DPD units
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMinimumBondLengthDpd(double aMinimumBondLengthDpd) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        double tmpCorrectedValue = ModelUtils.correctDoubleValue(aMinimumBondLengthDpd, ModelDefinitions.MINIMUM_BOND_LENGTH_DPD_MINIMUM, ModelDefinitions.MINIMUM_BOND_LENGTH_DPD_MAXIMUM);
        if (this.minimumBondLengthDpd != tmpCorrectedValue) {
            this.minimumBondLengthDpd = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MaximumNumberOfParticlesForGraphicalDisplay">
    /**
     * Maximum number of particles for graphical display
     *
     * @return Maximum number of particles for graphical display
     */
    public int getMaximumNumberOfParticlesForGraphicalDisplay() {
        return this.maximumNumberOfParticlesForGraphicalDisplay;
    }

    /**
     * Default maximum number of particles for graphical display
     *
     * @return Default maximum number of particles for graphical display
     */
    public int getDefaultMaximumNumberOfParticlesForGraphicalDisplay() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_PARTICLES_FOR_DISPLAY;
    }

    /**
     * Maximum number of particles for graphical display
     *
     * @param aMaximumNumberOfParticlesForGraphicalDisplay Maximum number of
     * particles for graphical display
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMaximumNumberOfParticlesForGraphicalDisplay(int aMaximumNumberOfParticlesForGraphicalDisplay) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aMaximumNumberOfParticlesForGraphicalDisplay, ModelDefinitions.MINIMUM_NUMBER_OF_PARTICLES_FOR_DISPLAY, Integer.MAX_VALUE);
        if (this.maximumNumberOfParticlesForGraphicalDisplay != tmpCorrectedValue) {
            this.maximumNumberOfParticlesForGraphicalDisplay = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfStepsForRdfCalculation">
    /**
     * Number of steps for RDF calculation
     *
     * @return Number of steps for RDF calculation
     */
    public int getNumberOfStepsForRdfCalculation() {
        return this.numberOfStepsForRdfCalculation;
    }

    /**
     * Default number of steps for RDF calculation
     *
     * @return Default number of steps for RDF calculation
     */
    public int getDefaultNumberOfStepsForRdfCalculation() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_STEPS_FOR_RDF_CALCULATION;
    }

    /**
     * Number of steps for RDF calculation
     *
     * @param aNumberOfStepsForRdfCalculation Number of steps for RDF
     * calculation
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfStepsForRdfCalculation(int aNumberOfStepsForRdfCalculation) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aNumberOfStepsForRdfCalculation, ModelDefinitions.MINIMUM_NUMBER_OF_STEPS_FOR_RDF_CALCULATION, Integer.MAX_VALUE);
        if (this.numberOfStepsForRdfCalculation != tmpCorrectedValue) {
            this.numberOfStepsForRdfCalculation = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfVolumeBins">
    /**
     * Number of volume bins
     *
     * @return Number of volume bins
     */
    public int getNumberOfVolumeBins() {
        return this.numberOfVolumeBins;
    }

    /**
     * Default number of volume bins
     *
     * @return Default number of volume bins
     */
    public int getDefaultNumberOfVolumeBins() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_ZOOM_VOLUME_BINS;
    }

    /**
     * Number of volume bins
     *
     * @param aNumberOfVolumeBins Number of volume bins
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfVolumeBins(int aNumberOfVolumeBins) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aNumberOfVolumeBins, ModelDefinitions.MINIMUM_NUMBER_OF_ZOOM_VOLUME_BINS, Integer.MAX_VALUE);
        if (this.numberOfVolumeBins != tmpCorrectedValue) {
            this.numberOfVolumeBins = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfTrialsForCompartment">
    /**
     * Number of trials for compartment related calculations
     *
     * @return Number of trials for compartment related calculations
     */
    public int getNumberOfTrialsForCompartment() {
        return this.numberOfTrialsForCompartment;
    }

    /**
     * Default number of trials for compartment related calculations
     *
     * @return Default number of trials for compartment related calculations
     */
    public int getDefaultNumberOfTrialsForCompartment() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_TRIALS_FOR_COMPARTMENT;
    }

    /**
     * Number of trials for compartment related calculations
     *
     * @param aNumberOfTrials Number of trials for compartment related
     * calculations
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfTrialsForCompartment(int aNumberOfTrials) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aNumberOfTrials, ModelDefinitions.MINIMUM_NUMBER_OF_TRIALS_FOR_COMPARTMENT, Integer.MAX_VALUE);
        if (this.numberOfTrialsForCompartment != tmpCorrectedValue) {
            this.numberOfTrialsForCompartment = tmpCorrectedValue;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- AnimationSpeed">
    /**
     * Animation speed in pictures per second
     *
     * @return Animation speed in pictures per second
     */
    public int getAnimationSpeed() {
        return this.animationSpeed;
    }

    /**
     * Default animation speed
     *
     * @return Default animation speed
     */
    public int getDefaultAnimationSpeed() {
        return ModelDefinitions.DEFAULT_ANIMATION_SPEED;
    }

    /**
     * Animation speed in pictures per second. NOTE: Passed value is corrected.
     *
     * @param anAnimationSpeed Animation speed in pictures per second
     * @return True: Value changed, false: Otherwise
     */
    public boolean setAnimationSpeed(int anAnimationSpeed) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(anAnimationSpeed, ModelDefinitions.MINIMUM_ANIMATION_SPEED, ModelDefinitions.MAXIMUM_ANIMATION_SPEED);
        if (this.animationSpeed != tmpCorrectedValue) {
            this.animationSpeed = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfSimulationBoxCellsforParallelization">
    /**
     * Minimum number of simulation box cells for parallelisation
     *
     * @return Minimum number of simulation box cells for parallelisation
     */
    public int getNumberOfSimulationBoxCellsforParallelization() {
        return this.numberOfSimulationBoxCellsforParallelization;
    }

    /**
     * Default minimum number of simulation box cells for parallelisation
     *
     * @return Default minimum number of simulation box cells for parallelisation
     */
    public int getDefaultNumberOfSimulationBoxCellsforParallelization() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION;
    }

    /**
     * Minimum number of simulation box cells for parallelisation
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfSimulationBoxCellsforParallelization(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION, ModelDefinitions.MAXIMUM_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION);
        if (this.numberOfSimulationBoxCellsforParallelization != tmpCorrectedValue) {
            this.numberOfSimulationBoxCellsforParallelization = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfBondsforParallelization">
    /**
     * Minimum number of bonds for parallelisation
     *
     * @return Minimum number of bonds for parallelisation
     */
    public int getNumberOfBondsforParallelization() {
        return this.numberOfBondsforParallelization;
    }

    /**
     * Default minimum number of bonds for parallelisation
     *
     * @return Default number of bonds for parallelisation
     */
    public int getDefaultNumberOfBondsforParallelization() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_BONDS_FOR_PARALLELIZATION;
    }

    /**
     * Minimum number of bonds for parallelisation
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfBondsforParallelization(int aValue) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_BONDS_FOR_PARALLELIZATION, ModelDefinitions.MAXIMUM_NUMBER_OF_BONDS_FOR_PARALLELIZATION);
        if (this.numberOfBondsforParallelization != tmpCorrectedValue) {
            this.numberOfBondsforParallelization = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfAdditionalStepsForJobRestart">
    /**
     * Number of steps for Job restart
     *
     * @return Number of steps for Job restart
     */
    public int getNumberOfAdditionalStepsForJobRestart() {
        return this.numberOfAdditionalStepsForJobRestart;
    }

    /**
     * Default number of steps for Job restart
     *
     * @return Default number of steps for Job restart
     */
    public int getDefaultNumberOfAdditionalStepsForJobRestart() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_STEPS_FOR_JOB_RESTART;
    }

    /**
     * Number of steps for Job restart. NOTE: Passed value is corrected.
     *
     * @param aNumberOfAdditionalStepsForJobRestart Number of steps for Job
     * restart
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfAdditionalStepsForJobRestart(int aNumberOfAdditionalStepsForJobRestart) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aNumberOfAdditionalStepsForJobRestart, ModelDefinitions.MINIMUM_NUMBER_OF_STEPS_FOR_JOB_RESTART,
                ModelDefinitions.MAXIMUM_NUMBER_OF_ADDITIONAL_STEPS_FOR_JOB_RESTART);
        if (this.numberOfAdditionalStepsForJobRestart != tmpCorrectedValue) {
            this.numberOfAdditionalStepsForJobRestart = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SimulationBoxMagnificationPercentage">
    /**
     * Simulation box magnification percentage
     *
     * @return Simulation box magnification percentage
     */
    public int getSimulationBoxMagnificationPercentage() {
        return this.simulationBoxMagnificationPercentage;
    }

    /**
     * Default simulation box magnification percentage
     *
     * @return Default simulation box magnification percentage
     */
    public int getDefaultSimulationBoxMagnificationPercentage() {
        return ModelDefinitions.DEFAULT_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE;
    }

    /**
     * Simulation box magnification percentage
     *
     * @param aValue Simulation box magnification percentage
     * @return True: Value changed, false: Otherwise
     */
    public boolean setSimulationBoxMagnificationPercentage(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE, ModelDefinitions.MAXIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE);
        if (this.simulationBoxMagnificationPercentage != tmpCorrectedValue) {
            this.simulationBoxMagnificationPercentage = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfSpinSteps">
    /**
     * Number of spin steps
     *
     * @return Number of spin steps
     */
    public int getNumberOfSpinSteps() {
        return this.numberOfSpinSteps;
    }

    /**
     * Default number of spin steps
     *
     * @return Default number of spin steps
     */
    public int getDefaultNumberOfSpinSteps() {
        return ModelDefinitions.DEFAULT_NUMBER_OF_SPIN_STEPS;
    }

    /**
     * Number of spin steps
     *
     * @param aValue Number of spin steps
     * @return True: Value changed, false: Otherwise
     */
    public boolean setNumberOfSpinSteps(int aValue) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        int tmpCorrectedValue = ModelUtils.correctIntegerValue(aValue, ModelDefinitions.MINIMUM_NUMBER_OF_SPIN_STEPS, ModelDefinitions.MAXIMUM_NUMBER_OF_SPIN_STEPS);
        // Correct to absolute minimum number if necessary
        if (Math.abs(tmpCorrectedValue) < ModelDefinitions.ABSOLUTE_MINIMUM_NUMBER_OF_SPIN_STEPS) {
            if (tmpCorrectedValue < 0) {
                tmpCorrectedValue = -ModelDefinitions.ABSOLUTE_MINIMUM_NUMBER_OF_SPIN_STEPS;
            } else {
                tmpCorrectedValue = ModelDefinitions.ABSOLUTE_MINIMUM_NUMBER_OF_SPIN_STEPS;
            }
        }
        if (this.numberOfSpinSteps != tmpCorrectedValue) {
            this.numberOfSpinSteps = tmpCorrectedValue;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogSlicerShowHeight">
    /**
     * Height of slicer show dialog
     *
     * @return Height of slicer show dialog
     */
    public int getDialogSlicerShowHeight() {
        return this.dialogSlicerShowHeight;
    }

    /**
     * Default height of slicer show dialog
     *
     * @return Default height of slicer show dialog
     */
    public int getDefaultDialogSlicerShowHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of slicer show dialog
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogSlicerShowHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogSlicerShowHeight != aDialogHeight) {
            this.dialogSlicerShowHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogSlicerShowWidth">
    /**
     * Width of slicer show dialog
     *
     * @return Width of slicer show dialog
     */
    public int getDialogSlicerShowWidth() {
        return this.dialogSlicerShowWidth;
    }

    /**
     * Default width of slicer show dialog
     *
     * @return Default width of slicer show dialog
     */
    public int getDefaultDialogSlicerShowWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of slicer show dialog
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogSlicerShowWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogSlicerShowWidth != aDialogWidth) {
            this.dialogSlicerShowWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CustomDialogHeight">
    /**
     * Custom height of dialog
     *
     * @return Custom height of dialog
     */
    public int getCustomDialogHeight() {
        return this.customDialogHeight;
    }

    /**
     * Default custom height of dialog
     *
     * @return Default custom height of dialog
     */
    public int getDefaultCustomDialogHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Custom height of dialog
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setCustomDialogHeight(int aDialogHeight) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.customDialogHeight != aDialogHeight) {
            this.customDialogHeight = aDialogHeight;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CustomDialogWidth">
    /**
     * Custom width of dialog
     *
     * @return Custom width of dialog
     */
    public int getCustomDialogWidth() {
        return this.customDialogWidth;
    }

    /**
     * Default custom width of dialog
     *
     * @return Default custom width of dialog
     */
    public int getDefaultCustomDialogWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Custom width of dialog
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setCustomDialogWidth(int aDialogWidth) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.customDialogWidth != aDialogWidth) {
            this.customDialogWidth = aDialogWidth;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogSingleSlicerShowHeight">
    /**
     * Height of single slicer show dialog
     *
     * @return Height of single slicer show dialog
     */
    public int getDialogSingleSlicerShowHeight() {
        return this.dialogSingleSlicerShowHeight;
    }

    /**
     * Default height of single slicer show dialog
     *
     * @return Default height of single slicer show dialog
     */
    public int getDefaultDialogSingleSlicerShowHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of single slicer show dialog
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogSingleSlicerShowHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogSingleSlicerShowHeight != aDialogHeight) {
            this.dialogSingleSlicerShowHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogSingleSlicerShowWidth">
    /**
     * Width of single slicer show dialog
     *
     * @return Width of single slicer show dialog
     */
    public int getDialogSingleSlicerShowWidth() {
        return this.dialogSingleSlicerShowWidth;
    }

    /**
     * Default width of single slicer show dialog
     *
     * @return Default width of single slicer show dialog
     */
    public int getDefaultDialogSingleSlicerShowWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of single slicer show dialog
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogSingleSlicerShowWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogSingleSlicerShowWidth != aDialogWidth) {
            this.dialogSingleSlicerShowWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogSimulationMovieSlicerShowHeight">
    /**
     * Height of through time slicer show dialog
     *
     * @return Height of through time slicer show dialog
     */
    public int getDialogSimulationMovieSlicerShowHeight() {
        return this.dialogSimulationMovieSlicerShowHeight;
    }

    /**
     * Default height of through time slicer show dialog
     *
     * @return Default height of through time slicer show dialog
     */
    public int getDefaultDialogSimulationMovieSlicerShowHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of through time slicer show dialog
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogSimulationMovieSlicerShowHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogSimulationMovieSlicerShowHeight != aDialogHeight) {
            this.dialogSimulationMovieSlicerShowHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogSlicerShowWidth">
    /**
     * Width of through time slicer show dialog
     *
     * @return Width of through time slicer show dialog
     */
    public int getDialogSimulationMovieSlicerShowWidth() {
        return this.dialogSimulationMovieSlicerShowWidth;
    }

    /**
     * Default width of through time slicer show dialog
     *
     * @return Default width of through time slicer show dialog
     */
    public int getDefaultDialogSimulationMovieSlicerShowWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of through time slicer show dialog
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogSimulationMovieSlicerShowWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogSimulationMovieSlicerShowWidth != aDialogWidth) {
            this.dialogSimulationMovieSlicerShowWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogValueItemEditHeight">
    /**
     * Height of value item edit dialog
     *
     * @return Height of value item edit dialog
     */
    public int getDialogValueItemEditHeight() {
        return this.dialogValueItemEditHeight;
    }

    /**
     * Default height of value item edit dialog
     *
     * @return Default height of value item edit dialog
     */
    public int getDefaultDialogValueItemEditHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of value item edit dialog without persistence
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogValueItemEditHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogValueItemEditHeight != aDialogHeight) {
            this.dialogValueItemEditHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogValueItemEditWidth">
    /**
     * Width of value item edit dialog
     *
     * @return Width of value item edit dialog
     */
    public int getDialogValueItemEditWidth() {
        return this.dialogValueItemEditWidth;
    }

    /**
     * Default width of value item edit dialog
     *
     * @return Default width of value item edit dialog
     */
    public int getDefaultDialogValueItemEditWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of value item edit dialog without persistence
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogValueItemEditWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogValueItemEditWidth != aDialogWidth) {
            this.dialogValueItemEditWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogTextEditHeight">
    /**
     * Height of text edit dialog
     *
     * @return Height of text edit dialog
     */
    public int getDialogTextEditHeight() {
        return this.dialogTextEditHeight;
    }

    /**
     * Default height of text edit dialog
     *
     * @return Default height of text edit dialog
     */
    public int getDefaultDialogTextEditHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of text edit dialog without persistence
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogTextEditHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogTextEditHeight != aDialogHeight) {
            this.dialogTextEditHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogTextEditWidth">
    /**
     * Width of text edit dialog
     *
     * @return Width of text edit dialog
     */
    public int getDialogTextEditWidth() {
        return this.dialogTextEditWidth;
    }

    /**
     * Default width of text edit dialog
     *
     * @return Default width of text edit dialog
     */
    public int getDefaultDialogTextEditWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of text edit dialog without persistence
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogTextEditWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogTextEditWidth != aDialogWidth) {
            this.dialogTextEditWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogValueItemShowHeight">
    /**
     * Height of value item show dialog
     *
     * @return Height of value item show dialog
     */
    public int getDialogValueItemShowHeight() {
        return this.dialogValueItemShowHeight;
    }

    /**
     * Default height of value item show dialog
     *
     * @return Default height of value item show dialog
     */
    public int getDefaultDialogValueItemShowHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of value item show dialog without persistence
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogValueItemShowHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogValueItemShowHeight != aDialogHeight) {
            this.dialogValueItemShowHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogValueItemShowWidth">
    /**
     * Width of value item show dialog
     *
     * @return Width of value item show dialog
     */
    public int getDialogValueItemShowWidth() {
        return this.dialogValueItemShowWidth;
    }

    /**
     * Default width of value item show dialog
     *
     * @return Default width of value item show dialog
     */
    public int getDefaultDialogValueItemShowWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of value item show dialog without persistence
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogValueItemShowWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogValueItemShowWidth != aDialogWidth) {
            this.dialogValueItemShowWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogTableDataSchemataManageHeight">
    /**
     * Height of table-data schemata manage dialog
     *
     * @return Height of table-data schemata manage dialog
     */
    public int getDialogTableDataSchemataManageHeight() {
        return this.dialogTableDataSchemataManageHeight;
    }

    /**
     * Default height of table-data schemata manage dialog
     *
     * @return height of table-data schemata manage dialog
     */
    public int getDefaultDialogTableDataSchemataManageHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of table-data schemata manage dialog without persistence
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogTableDataSchemataManageHeight(int aDialogHeight) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogTableDataSchemataManageHeight != aDialogHeight) {
            this.dialogTableDataSchemataManageHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogTableDataSchemataManageWidth">
    /**
     * Width of table-data schemata manage dialog
     *
     * @return of table-data schemata manage dialog
     */
    public int getDialogTableDataSchemataManageWidth() {
        return this.dialogTableDataSchemataManageWidth;
    }

    /**
     * Default width of table-data schemata manage dialog
     *
     * @return Default width of table-data schemata manage dialog
     */
    public int getDefaultDialogTableDataSchemataManageWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of table-data schemata manage dialog without persistence
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogTableDataSchemataManageWidth(int aDialogWidth) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogTableDataSchemataManageWidth != aDialogWidth) {
            this.dialogTableDataSchemataManageWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogValueItemMatrixDiagramHeight">
    /**
     * Height of value item matrix diagram dialog
     *
     * @return Height of value item matrix diagram dialog
     */
    public int getDialogValueItemMatrixDiagramHeight() {
        return this.dialogValueItemMatrixDiagramHeight;
    }

    /**
     * Default height of value item matrix diagram dialog
     *
     * @return Default height of value item matrix diagram dialog
     */
    public int getDefaultDialogValueItemMatrixDiagramHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of value item matrix diagram dialog without persistence
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogValueItemMatrixDiagramHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        // NOTE: There is NO minimum height
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogValueItemMatrixDiagramHeight != aDialogHeight) {
            this.dialogValueItemMatrixDiagramHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogValueItemMatrixDiagramWidth">
    /**
     * Width of value item matrix diagram dialog
     *
     * @return Width of value item matrix diagram dialog
     */
    public int getDialogValueItemMatrixDiagramWidth() {
        return this.dialogValueItemMatrixDiagramWidth;
    }

    /**
     * Default width of value item matrix diagram dialog
     *
     * @return Default width of value item matrix diagram dialog
     */
    public int getDefaultDialogValueItemMatrixDiagramWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of value item matrix diagram dialog without persistence
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogValueItemMatrixDiagramWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        // NOTE: There is NO minimum width
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogValueItemMatrixDiagramWidth != aDialogWidth) {
            this.dialogValueItemMatrixDiagramWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogStructureEditHeight">
    /**
     * Height of structure edit dialog
     *
     * @return Height of structure edit dialog
     */
    public int getDialogStructureEditHeight() {
        return this.dialogStructureEditHeight;
    }

    /**
     * Default height of structure edit dialog
     *
     * @return Default height of structure edit dialog
     */
    public int getDefaultDialogStructureEditHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of structure edit dialog without persistence
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogStructureEditHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogStructureEditHeight != aDialogHeight) {
            this.dialogStructureEditHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogStructureEditWidth">
    /**
     * Width of structure edit dialog
     *
     * @return Width of structure edit dialog
     */
    public int getDialogStructureEditWidth() {
        return this.dialogStructureEditWidth;
    }

    /**
     * Default width of structure edit dialog
     *
     * @return Default width of structure edit dialog
     */
    public int getDefaultDialogStructureEditWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of structure edit dialog without persistence
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogStructureEditWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogStructureEditWidth != aDialogWidth) {
            this.dialogStructureEditWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogPeptideEditHeight">
    /**
     * Height of peptide edit dialog
     *
     * @return Height of peptide edit dialog
     */
    public int getDialogPeptideEditHeight() {
        return this.dialogPeptideEditHeight;
    }

    /**
     * Default height of peptide edit dialog
     *
     * @return Default height of structure edit dialog
     */
    public int getDefaultDialogPeptideEditHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of peptide edit dialog without persistence
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogPeptideEditHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogPeptideEditHeight != aDialogHeight) {
            this.dialogPeptideEditHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogPeptideEditWidth">
    /**
     * Width of peptide edit dialog
     *
     * @return Width of peptide edit dialog
     */
    public int getDialogPeptideEditWidth() {
        return this.dialogPeptideEditWidth;
    }

    /**
     * Default width of peptide edit dialog
     *
     * @return Default width of peptide edit dialog
     */
    public int getDefaultDialogPeptideEditWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of peptide edit dialog without persistence
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogPeptideEditWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogPeptideEditWidth != aDialogWidth) {
            this.dialogPeptideEditWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogCompartmentEditHeight">
    /**
     * Height of compartment edit dialog
     *
     * @return Height of compartment edit dialog
     */
    public int getDialogCompartmentEditHeight() {
        return this.dialogCompartmentEditHeight;
    }

    /**
     * Default height of compartment edit dialog
     *
     * @return Default height of compartment edit dialog
     */
    public int getDefaultDialogCompartmentEditHeight() {
        return ModelDefinitions.MINIMUM_DIALOG_HEIGHT;
    }

    /**
     * Height of compartment edit dialog without persistence
     *
     * @param aDialogHeight Dialog height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogCompartmentEditHeight(int aDialogHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogHeight < ModelDefinitions.MINIMUM_DIALOG_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogCompartmentEditHeight != aDialogHeight) {
            this.dialogCompartmentEditHeight = aDialogHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- DialogCompartmentEditWidth">
    /**
     * Width of compartment edit dialog
     *
     * @return Width of compartment edit dialog
     */
    public int getDialogCompartmentEditWidth() {
        return this.dialogCompartmentEditWidth;
    }

    /**
     * Default width of compartment edit dialog
     *
     * @return Default width of compartment edit dialog
     */
    public int getDefaultDialogCompartmentEditWidth() {
        return ModelDefinitions.MINIMUM_DIALOG_WIDTH;
    }

    /**
     * Width of compartment edit dialog without persistence
     *
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogCompartmentEditWidth(int aDialogWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDialogWidth < ModelDefinitions.MINIMUM_DIALOG_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.dialogCompartmentEditWidth != aDialogWidth) {
            this.dialogCompartmentEditWidth = aDialogWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- InternalMFsimJobPath">
    /**
     * Full path for internal DPD Job directory
     *
     * @return Full path for internal DPD Job directory or empty string if none
     * exists
     */
    public String getInternalMFsimJobPath() {
        if (!this.internalMFsimJobPath.isEmpty()) {
            if ((new File(this.internalMFsimJobPath)).isDirectory()) {
                return this.internalMFsimJobPath;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * Default full path for internal DPD Job directory
     *
     * @return Empty string (there is no default path for internal DPD Job
     * directory)
     */
    public String getDefaultInternalMFsimJobPath() {
        return "";
    }

    /**
     * Full path for internal DPD Job directory
     *
     * @param aPath Full path for internal DPD Job directory
     * @return True: Value changed, false: Otherwise
     */
    public boolean setInternalMFsimJobPath(String aPath) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null) {
            return false;
        }
        if (!aPath.isEmpty() && !(new File(aPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.internalMFsimJobPath == null ? aPath != null : !this.internalMFsimJobPath.equals(aPath)) {
            this.internalMFsimJobPath = aPath;
            tmpHasChanged = true;
            try {
                ModelUtils.createDirectory(this.getJobInputPath());
                ModelUtils.createDirectory(this.getJobResultPath());
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                this.internalMFsimJobPath = "";
            }
        }

        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- InternalTempPath">
    /**
     * Default full path for internal temp directory
     *
     * @return Empty string (there is no default path for internal temp
     * directory)
     */
    public String getDefaultInternalTempPath() {
        return "";
    }

    /**
     * Full path for internal temp directory
     *
     * @param aPath Full path for internal temp directory
     * @return True: Value changed, false: Otherwise
     */
    public boolean setInternalTempPath(String aPath) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null) {
            return false;
        }
        if (!aPath.isEmpty() && !(new File(aPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.internalTempPath != aPath) {
            this.internalTempPath = aPath;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SimulationMovieImagePath">
    /**
     * Full path for simulation movie images
     *
     * @return Full path for simulation movie images or empty string if none exists
     */
    public String getSimulationMovieImagePath() {
        if (!this.simulationMovieImagePath.isEmpty()) {
            if ((new File(this.simulationMovieImagePath)).isDirectory()) {
                return this.simulationMovieImagePath;
            }
        }
        return "";
    }

    /**
     * Full path for image directory below this.simulationMovieImagePath
     *
     * @return Full path for image directory below this.simulationMovieImagePath
     */
    public String getImageDirectoryPathForSimulationMovies() {
        if (!this.simulationMovieImagePath.isEmpty()) {
            if ((new File(this.simulationMovieImagePath)).isDirectory()) {
                String tmpImageDirectoryPathForMovies = this.simulationMovieImagePath + File.separatorChar + ModelDefinitions.IMAGE_DIRECTORY_FOR_MOVIES;
                if ((new File(tmpImageDirectoryPathForMovies)).isDirectory()) {
                    return tmpImageDirectoryPathForMovies;
                } else if (ModelUtils.createDirectory(tmpImageDirectoryPathForMovies)) {
                    return tmpImageDirectoryPathForMovies;
                }
            }
        }
        return "";
    }

    /**
     * Full path for movie directory below this.simulationMovieImagePath
     *
     * @return Full path for movie directory below this.simulationMovieImagePath
     */
    public String getMovieDirectoryPathForSimulationMovies() {
        if (!this.simulationMovieImagePath.isEmpty()) {
            if ((new File(this.simulationMovieImagePath)).isDirectory()) {
                String tmpMovieDirectoryPathForMovies = this.simulationMovieImagePath + File.separatorChar + ModelDefinitions.MOVIE_DIRECTORY_FOR_MOVIES;
                if ((new File(tmpMovieDirectoryPathForMovies)).isDirectory()) {
                    return tmpMovieDirectoryPathForMovies;
                } else if (ModelUtils.createDirectory(tmpMovieDirectoryPathForMovies)) {
                    return tmpMovieDirectoryPathForMovies;
                }
            }
        }
        return "";
    }

    /**
     * Default full path for simulation movie images
     *
     * @return Empty string (there is no default path for simulation movie images)
     */
    public String getDefaultSimulationMovieImagePath() {
        return "";
    }

    /**
     * Full path for simulation movie images
     *
     * @param aPath Full path for simulation movie images
     * @return True: Value changed, false: Otherwise
     */
    public boolean setSimulationMovieImagePath(String aPath) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null) {
            return false;
        }
        if (!aPath.isEmpty() && !(new File(aPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (!this.simulationMovieImagePath.equals(aPath)) {
            String tmpImageDirectoryPathForMovies = aPath + File.separatorChar + ModelDefinitions.IMAGE_DIRECTORY_FOR_MOVIES;
            if (!ModelUtils.createDirectory(tmpImageDirectoryPathForMovies)) {
                return false;
            }
            String tmpMovieDirectoryPathForMovies = aPath + File.separatorChar + ModelDefinitions.MOVIE_DIRECTORY_FOR_MOVIES;
            if (!ModelUtils.createDirectory(tmpMovieDirectoryPathForMovies)) {
                return false;
            }
            this.simulationMovieImagePath = aPath;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ChartMovieImagePath">
    /**
     * Full path for chart movie images
     *
     * @return Full path for chart movie images or empty string if none exists
     */
    public String getChartMovieImagePath() {
        if (!this.chartMovieImagePath.isEmpty()) {
            if ((new File(this.chartMovieImagePath)).isDirectory()) {
                return this.chartMovieImagePath;
            }
        }
        return "";
    }

    /**
     * Full path for image directory below this.chartMovieImagePath
     *
     * @return Full path for image directory below this.chartMovieImagePath
     */
    public String getImageDirectoryPathForChartMovies() {
        if (!this.chartMovieImagePath.isEmpty()) {
            if ((new File(this.chartMovieImagePath)).isDirectory()) {
                String tmpImageDirectoryPathForMovies = this.chartMovieImagePath + File.separatorChar + ModelDefinitions.IMAGE_DIRECTORY_FOR_MOVIES;
                if ((new File(tmpImageDirectoryPathForMovies)).isDirectory()) {
                    return tmpImageDirectoryPathForMovies;
                } else if (ModelUtils.createDirectory(tmpImageDirectoryPathForMovies)) {
                    return tmpImageDirectoryPathForMovies;
                }
            }
        }
        return "";
    }

    /**
     * Full path for movie directory below this.chartMovieImagePath
     *
     * @return Full path for movie directory below this.chartMovieImagePath
     */
    public String getMovieDirectoryPathForChartMovies() {
        if (!this.chartMovieImagePath.isEmpty()) {
            if ((new File(this.chartMovieImagePath)).isDirectory()) {
                String tmpMovieDirectoryPathForMovies = this.chartMovieImagePath + File.separatorChar + ModelDefinitions.MOVIE_DIRECTORY_FOR_MOVIES;
                if ((new File(tmpMovieDirectoryPathForMovies)).isDirectory()) {
                    return tmpMovieDirectoryPathForMovies;
                } else if (ModelUtils.createDirectory(tmpMovieDirectoryPathForMovies)) {
                    return tmpMovieDirectoryPathForMovies;
                }
            }
        }
        return "";
    }

    /**
     * Default full path for chart movie images
     *
     * @return Empty string (there is no default path for chart movie images)
     */
    public String getDefaultChartMovieImagePath() {
        return "";
    }

    /**
     * Full path for chart movie images
     *
     * @param aPath Full path for chart movie images
     * @return True: Value changed, false: Otherwise
     */
    public boolean setChartMovieImagePath(String aPath) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null) {
            return false;
        }
        if (!aPath.isEmpty() && !(new File(aPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (!this.chartMovieImagePath.equals(aPath)) {
            String tmpImageDirectoryPathForMovies = aPath + File.separatorChar + ModelDefinitions.IMAGE_DIRECTORY_FOR_MOVIES;
            if (!ModelUtils.createDirectory(tmpImageDirectoryPathForMovies)) {
                return false;
            }
            String tmpMovieDirectoryPathForMovies = aPath + File.separatorChar + ModelDefinitions.MOVIE_DIRECTORY_FOR_MOVIES;
            if (!ModelUtils.createDirectory(tmpMovieDirectoryPathForMovies)) {
                return false;
            }
            this.chartMovieImagePath = aPath;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Job input filter">
    // <editor-fold defaultstate="collapsed" desc="-- JobInputFilterAfterTimestamp">
    /**
     * Job input filter: After timestamp
     *
     * @return After timestamp
     */
    public String getJobInputFilterAfterTimestamp() {
        return this.jobInputFilterAfterTimestamp;
    }

    /**
     * Job input filter: Default after timestamp
     *
     * @return Default after timestamp
     */
    public String getDefaultJobInputFilterAfterTimestamp() {
        return "";
    }

    /**
     * Job input filter: After timestamp
     *
     * @param aAfterTimestamp After timestamp
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobInputFilterAfterTimestamp(String aAfterTimestamp) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        // NOTE: aAfterTimestamp may be empty!
        if (aAfterTimestamp == null) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new after timestamp if changed">
        if (this.jobInputFilterAfterTimestamp != aAfterTimestamp) {
            this.jobInputFilterAfterTimestamp = aAfterTimestamp;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- JobInputFilterBeforeTimestamp">
    /**
     * Job input filter: Before timestamp
     *
     * @return Before timestamp
     */
    public String getJobInputFilterBeforeTimestamp() {
        return this.jobInputFilterBeforeTimestamp;
    }

    /**
     * Job input filter: Default before timestamp
     *
     * @return Default before timestamp
     */
    public String getDefaultJobInputFilterBeforeTimestamp() {
        return "";
    }

    /**
     * Job input filter: Before timestamp
     *
     * @param aBeforeTimestamp Before timestamp
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobInputFilterBeforeTimestamp(String aBeforeTimestamp) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        // NOTE: aBeforeTimestamp may be empty!
        if (aBeforeTimestamp == null) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new before timestamp if changed">
        if (this.jobInputFilterBeforeTimestamp != aBeforeTimestamp) {
            this.jobInputFilterBeforeTimestamp = aBeforeTimestamp;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- JobInputFilterContainsPhrase">
    /**
     * Job input filter: Contains phrase
     *
     * @return Contains phrase
     */
    public String getJobInputFilterContainsPhrase() {
        return this.jobInputFilterContainsPhrase;
    }

    /**
     * Job input filter: Default contains phrase
     *
     * @return Default contains phrase
     */
    public String getDefaultJobInputFilterContainsPhrase() {
        return "";
    }

    /**
     * Job input filter: Contains phrase
     *
     * @param aContainsPhrase Contains phrase
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobInputFilterContainsPhrase(String aContainsPhrase) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        // NOTE: aContainsPhrase may be empty!
        if (aContainsPhrase == null) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new contains phrase if changed">
        if (this.jobInputFilterContainsPhrase != aContainsPhrase) {
            this.jobInputFilterContainsPhrase = aContainsPhrase;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Job result filter">
    // <editor-fold defaultstate="collapsed" desc="-- JobResultFilterAfterTimestamp">
    /**
     * Job result filter: After timestamp
     *
     * @return After timestamp
     */
    public String getJobResultFilterAfterTimestamp() {
        return this.jobResultFilterAfterTimestamp;
    }

    /**
     * Job result filter: Default after timestamp
     *
     * @return Default after timestamp
     */
    public String getDefaultJobResultFilterAfterTimestamp() {
        return "";
    }

    /**
     * Job result filter: After timestamp
     *
     * @param aAfterTimestamp After timestamp
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobResultFilterAfterTimestamp(String aAfterTimestamp) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        // NOTE: aAfterTimestamp may be empty!
        if (aAfterTimestamp == null) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new after timestamp if changed">
        if (this.jobResultFilterAfterTimestamp != aAfterTimestamp) {
            this.jobResultFilterAfterTimestamp = aAfterTimestamp;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- JobResultFilterBeforeTimestamp">
    /**
     * Job result filter: Before timestamp
     *
     * @return Before timestamp
     */
    public String getJobResultFilterBeforeTimestamp() {
        return this.jobResultFilterBeforeTimestamp;
    }

    /**
     * Job result filter: Default before timestamp
     *
     * @return Default before timestamp
     */
    public String getDefaultJobResultFilterBeforeTimestamp() {
        return "";
    }

    /**
     * Job result filter: Before timestamp
     *
     * @param aBeforeTimestamp Before timestamp
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobResultFilterBeforeTimestamp(String aBeforeTimestamp) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        // NOTE: aBeforeTimestamp may be empty!
        if (aBeforeTimestamp == null) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new before timestamp if changed">
        if (this.jobResultFilterBeforeTimestamp != aBeforeTimestamp) {
            this.jobResultFilterBeforeTimestamp = aBeforeTimestamp;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- JobResultFilterContainsPhrase">
    /**
     * Job result filter: Contains phrase
     *
     * @return Contains phrase
     */
    public String getJobResultFilterContainsPhrase() {
        return this.jobResultFilterContainsPhrase;
    }

    /**
     * Job result filter: Default contains phrase
     *
     * @return Default contains phrase
     */
    public String getDefaultJobResultFilterContainsPhrase() {
        return "";
    }

    /**
     * Job result filter: Contains phrase
     *
     * @param aContainsPhrase Contains phrase
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJobResultFilterContainsPhrase(String aContainsPhrase) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        // NOTE: aContainsPhrase may be empty!
        if (aContainsPhrase == null) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new contains phrase if changed">
        if (this.jobResultFilterContainsPhrase != aContainsPhrase) {
            this.jobResultFilterContainsPhrase = aContainsPhrase;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- LastSelectedPath">
    /**
     * Last selected path
     *
     * @return Last selected path
     */
    public String getLastSelectedPath() {
        if (new File(this.lastSelectedPath).isDirectory()) {
            return this.lastSelectedPath;
        } else {
            return this.getDefaultLastSelectedPath();
        }
    }

    /**
     * Default last selected path
     *
     * @return Default last selected path
     */
    public String getDefaultLastSelectedPath() {
        return this.dpdDataPath;
    }

    /**
     * Last selected path
     *
     * @param aLastSelectedPath Last selected path
     * @return True: Value changed, false: Otherwise
     */
    public boolean setLastSelectedPath(String aLastSelectedPath) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aLastSelectedPath == null || aLastSelectedPath.isEmpty() || !(new File(aLastSelectedPath)).isDirectory()) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.lastSelectedPath != aLastSelectedPath) {
            this.lastSelectedPath = aLastSelectedPath;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MainFrameHeight">
    /**
     * MainFrame height
     *
     * @return MainFrame height
     */
    public int getMainFrameHeight() {
        return this.mainFrameHeight;
    }

    /**
     * Default MainFrame height
     *
     * @return Default MainFrame height
     */
    public int getDefaultMainFrameHeight() {
        return ModelDefinitions.MINIMUM_FRAME_HEIGHT;
    }

    /**
     * MainFrame height
     *
     * @param aMainFrameHeight MainFrame height
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMainFrameHeight(int aMainFrameHeight) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMainFrameHeight < ModelDefinitions.MINIMUM_FRAME_HEIGHT) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new MainFrame height if changed">
        if (this.mainFrameHeight != aMainFrameHeight) {
            this.mainFrameHeight = aMainFrameHeight;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MainFrameWidth">
    /**
     * MainFrame width
     *
     * @return MainFrame width
     */
    public int getMainFrameWidth() {
        return this.mainFrameWidth;
    }

    /**
     * Default MainFrame width
     *
     * @return Default MainFrame width
     */
    public int getDefaultMainFrameWidth() {
        return ModelDefinitions.MINIMUM_FRAME_WIDTH;
    }

    /**
     * MainFrame width
     *
     * @param aMainFrameWidth MainFrame width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMainFrameWidth(int aMainFrameWidth) {

        boolean tmpHasChanged = false;

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMainFrameWidth < ModelDefinitions.MINIMUM_FRAME_WIDTH) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (this.mainFrameWidth != aMainFrameWidth) {
            this.mainFrameWidth = aMainFrameWidth;
            tmpHasChanged = true;
        }

        // </editor-fold>
        return tmpHasChanged;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MainFrameMaximumHeight">
    /**
     * MainFrame maximum height
     *
     * @return MainFrame maximum height
     */
    public int getMainFrameMaximumHeight() {
        return this.mainFrameMaximumHeight;
    }

    /**
     * Default MainFrame maximum height: -1 (impossible)
     *
     * @return (Impossible) default MainFrame maximum height
     */
    public int getDefaultMainFrameMaximumHeight() {
        return -1;
    }

    /**
     * MainFrame maximum height
     *
     * @param aMainFrameMaximumHeight MainFrame maximum height
     */
    public void setMainFrameMaximumHeight(int aMainFrameMaximumHeight) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMainFrameMaximumHeight < ModelDefinitions.MINIMUM_FRAME_HEIGHT) {
            return;
        }

        // </editor-fold>
        this.mainFrameMaximumHeight = aMainFrameMaximumHeight;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MainFrameMaximumWidth">
    /**
     * MainFrame maximum width
     *
     * @return MainFrame maximum width
     */
    public int getMainFrameMaximumWidth() {
        return this.mainFrameMaximumWidth;
    }

    /**
     * Default MainFrame maximum width: -1 (impossible)
     *
     * @return (Impossible) default MainFrame maximum width
     */
    public int getDefaultMainFrameMaximumWidth() {
        return -1;
    }

    /**
     * MainFrame maximum width
     *
     * @param aMainFrameMaximumWidth MainFrame maximum width
     */
    public void setMainFrameMaximumWidth(int aMainFrameMaximumWidth) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMainFrameMaximumWidth < ModelDefinitions.MINIMUM_FRAME_WIDTH) {
            return;
        }

        // </editor-fold>
        this.mainFrameMaximumWidth = aMainFrameMaximumWidth;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- PreviousMonomers">
    /**
     * Adds monomer to previous monomers. NOTE: If monomer already exists in
     * previous monomers this entry will be deleted and the monomer added at
     * first position.
     *
     * @param aMonomer Monomer
     */
    public void addPreviousMonomer(String aMonomer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomer == null || aMonomer.isEmpty()) {
            return;
        }

        // </editor-fold>
        this.previousMonomers.remove(aMonomer);
        this.previousMonomers.addFirst(aMonomer);
    }

    /**
     * Clears previous monomers
     */
    public void clearPreviousMonomers() {
        this.previousMonomers.clear();
    }

    /**
     * Previous monomers
     *
     * @return Previous monomers
     */
    public LinkedList<String> getPreviousMonomers() {
        return this.previousMonomers;
    }

    /**
     * Returns if previous monomers are defined
     *
     * @return True: Previous monomers are defined, false: Otherwise
     */
    public boolean hasPreviousMonomers() {
        return this.previousMonomers.size() > 0;
    }

    /**
     * Removes specified monomer from previous monomers if possible.
     *
     * @param aMonomer Monomer
     * @return True: Monomer was removed and previous monomers changed, false:
     * Otherwise (previous monomers are unchanged)
     */
    public boolean removePreviousMonomer(String aMonomer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomer == null || aMonomer.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.previousMonomers.remove(aMonomer);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- PreviousStructures">
    /**
     * Adds structure to previous structures. NOTE: If structure already exists
     * in previous structures this entry will be deleted and the structure added
     * at first position.
     *
     * @param aStructure Structure
     */
    public void addPreviousStructure(String aStructure) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStructure == null || aStructure.isEmpty()) {
            return;
        }
        // </editor-fold>
        boolean tmpIsInList = true;
        while (tmpIsInList) {
            tmpIsInList = this.previousStructures.remove(aStructure);
        }
        this.previousStructures.addFirst(aStructure);
    }

    /**
     * Clears previous structures
     */
    public void clearPreviousStructures() {
        this.previousStructures.clear();
    }

    /**
     * Previous structures
     *
     * @return Previous structures
     */
    public LinkedList<String> getPreviousStructures() {
        return this.previousStructures;
    }

    /**
     * Returns if previous structures are defined
     *
     * @return True: Previous structures are defined, false: Otherwise
     */
    public boolean hasPreviousStructures() {
        return this.previousStructures.size() > 0;
    }

    /**
     * Removes specified structure from previous structures if possible.
     *
     * @param aStructure Structure
     * @return True: Structure was removed and previous structures changed,
     * false: Otherwise (previous structures are unchanged)
     */
    public boolean removePreviousStructure(String aStructure) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aStructure == null || aStructure.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.previousStructures.remove(aStructure);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- PreviousPeptides">
    /**
     * Adds peptide to previous peptides. NOTE: If peptide already exists in
     * previous peptides this entry will be deleted and the peptide added at
     * first position.
     *
     * @param aPeptide Peptide
     */
    public void addPreviousPeptide(String aPeptide) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPeptide == null || aPeptide.isEmpty()) {
            return;
        }

        // </editor-fold>
        this.previousPeptides.remove(aPeptide);
        this.previousPeptides.addFirst(aPeptide);
    }

    /**
     * Clears previous peptides
     */
    public void clearPreviousPeptides() {
        this.previousPeptides.clear();
    }

    /**
     * Previous peptides
     *
     * @return Previous peptides
     */
    public LinkedList<String> getPreviousPeptides() {
        return this.previousPeptides;
    }

    /**
     * Returns if previous peptides are defined
     *
     * @return True: Previous peptides are defined, false: Otherwise
     */
    public boolean hasPreviousPeptides() {
        return this.previousPeptides.size() > 0;
    }

    /**
     * Removes specified peptide from previous peptides if possible.
     *
     * @param aPeptide Peptide
     * @return True: Peptide was removed and previous peptides changed, false:
     * Otherwise (previous peptides are unchanged)
     */
    public boolean removePreviousPeptide(String aPeptide) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPeptide == null || aPeptide.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.previousPeptides.remove(aPeptide);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SingleSliceDisplay">
    /**
     * True: A slice only displays its corresponding particles, false: A slice
     * also displays the slices behind it.
     *
     * @return True: A slice only displays its corresponding particles, false: A
     * slice also displays the slices behind it.
     */
    public boolean isSingleSliceDisplay() {
        return this.isSingleSliceDisplay;
    }

    /**
     * Default display of single slices
     *
     * @return Default display of single slices
     */
    public boolean getDefaultSingleSliceDisplay() {
        return ModelDefinitions.DEFAULT_SINGLE_SLICE_DISPLAY;
    }

    /**
     * True: A slice only displays its corresponding particles, false: A slice
     * also displays the slices behind it.
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setSingleSliceDisplay(boolean aValue) {
        if (this.isSingleSliceDisplay != aValue) {
            this.isSingleSliceDisplay = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SlicerGraphicsMode">
    /**
     * Slicer graphics mode
     *
     * @return Slicer graphics mode
     */
    public GraphicsModeEnum getSlicerGraphicsMode() {
        return this.slicerGraphicsMode;
    }

    /**
     * Default slicer graphics mode
     *
     * @return Default slicer graphics mode
     */
    public GraphicsModeEnum getDefaultSlicerGraphicsMode() {
        return GraphicsModeEnum.PIXEL_ALL;
    }

    /**
     * Slicer graphics mode
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setSlicerGraphicsMode(GraphicsModeEnum aValue) {
        if (this.slicerGraphicsMode != aValue) {
            this.slicerGraphicsMode = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SimulationBoxBackgroundColorSlicer">
    /**
     * Background color of simulation box for slicer
     *
     * @return Background color of simulation box for slicer
     */
    public Color getSimulationBoxBackgroundColorSlicer() {
        return StandardColorEnum.toStandardColor(this.simulationBoxBackgroundColorSlicer).toColor();
    }

    /**
     * Default background color of simulation box for slicer
     *
     * @return Default background color of simulation box for slicer
     */
    public Color getDefaultSimulationBoxBackgroundColorSlicer() {
        return ModelDefinitions.DEFAULT_SIMULATION_BOX_BACKGROUND_COLOR_SLICER.toColor();
    }

    /**
     * Background color of simulation box for slicer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setSimulationBoxBackgroundColorSlicer(String aValue) {
        if (!this.simulationBoxBackgroundColorSlicer.equals(aValue)) {
            this.simulationBoxBackgroundColorSlicer = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MeasurementColorSlicer">
    /**
     * Measurement color for slicer
     *
     * @return Measurement color for slicer
     */
    public Color getMeasurementColorSlicer() {
        return StandardColorEnum.toStandardColor(this.measurementColorSlicer).toColor();
    }

    /**
     * Measurement color for slicer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMeasurementColorSlicer(String aValue) {
        if (!this.measurementColorSlicer.equals(aValue)) {
            this.measurementColorSlicer = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MoleculeSelectionColorSlicer">
    /**
     * Molecule selection color for slicer
     *
     * @return Molecule selection color for slicer
     */
    public StandardColorEnum getMoleculeSelectionColorSlicer() {
        return StandardColorEnum.toStandardColor(this.moleculeSelectionColorSlicer);
    }

    /**
     * Molecule selection color for slicer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMoleculeSelectionColorSlicer(String aValue) {
        if (!this.moleculeSelectionColorSlicer.equals(aValue)) {
            this.moleculeSelectionColorSlicer = aValue;
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- FrameColorSlicer">
    /**
     * Frame color for slicer
     *
     * @return Frame color for slicer
     */
    public Color getFrameColorSlicer() {
        return StandardColorEnum.toStandardColor(this.frameColorSlicer).toColor();
    }

    /**
     * Default frame color for slicer
     *
     * @return Default frame color for slicer
     */
    public Color getDefaultFrameColorSlicer() {
        return ModelDefinitions.DEFAULT_FRAME_COLOR_SLICER.toColor();
    }

    /**
     * Frame color for slicer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setFrameColorSlicer(String aValue) {
        if (!this.frameColorSlicer.equals(aValue)) {
            this.frameColorSlicer = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JmolSimulationBoxBackgroundColor">
    /**
     * Background color of simulation box for Jmol viewer
     *
     * @return Background color of simulation box for Jmol viewer
     */
    public Color getJmolSimulationBoxBackgroundColor() {
        return StandardColorEnum.toStandardColor(this.jmolSimulationBoxBackgroundColor).toColor();
    }

    /**
     * Default background color of simulation box for Jmol viewer
     *
     * @return Default background color of simulation box for Jmol viewer
     */
    public Color getDefaultJmolSimulationBoxBackgroundColor() {
        return ModelDefinitions.DEFAULT_JMOL_SIMULATION_BOX_BACKGROUND_COLOR.toColor();
    }

    /**
     * Background color of simulation box for Jmol viewer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setJmolSimulationBoxBackgroundColor(String aValue) {
        if (!this.jmolSimulationBoxBackgroundColor.equals(aValue)) {
            this.jmolSimulationBoxBackgroundColor = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ProteinViewerBackgroundColor">
    /**
     * Background color of protein viewer
     *
     * @return Background color of protein viewer
     */
    public Color getProteinViewerBackgroundColor() {
        return StandardColorEnum.toStandardColor(this.proteinViewerBackgroundColor).toColor();
    }

    /**
     * Default background color of protein viewer
     *
     * @return Default background color of protein viewer
     */
    public Color getDefaultProteinViewerBackgroundColor() {
        return ModelDefinitions.DEFAULT_PROTEIN_VIEWER_BACKGROUND_COLOR.toColor();
    }

    /**
     * Background color of protein viewer
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setProteinViewerBackgroundColor(String aValue) {
        if (!this.proteinViewerBackgroundColor.equals(aValue)) {
            this.proteinViewerBackgroundColor = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ImageStorageMode">
    /**
     * Image storage mode
     *
     * @return Image storage mode
     */
    public ImageStorageEnum getImageStorageMode() {
        return this.imageStorageMode;
    }

    /**
     * Default image storage mode
     *
     * @return Default image storage mode
     */
    public ImageStorageEnum getDefaultImageStorageMode() {
        return ModelDefinitions.DEFAULT_IMAGE_STORAGE_MODE;
    }

    /**
     * Image storage mode
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setImageStorageMode(ImageStorageEnum aValue) {
        if (this.imageStorageMode != aValue) {
            this.imageStorageMode = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- ParticleColorDisplayMode">
    /**
     * Particle color display mode
     *
     * @return Particle color display mode
     */
    public ParticleColorDisplayEnum getParticleColorDisplayMode() {
        return this.particleColorDisplayMode;
    }

    /**
     * Default particle color display mode
     *
     * @return Default particle color display mode
     */
    public ParticleColorDisplayEnum getDefaultParticleColorDisplayMode() {
        return ModelDefinitions.DEFAULT_PARTICLE_COLOR_DISPLAY_MODE;
    }

    /**
     * Particle color display mode
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setParticleColorDisplayMode(ParticleColorDisplayEnum aValue) {
        if (this.particleColorDisplayMode != aValue) {
            this.particleColorDisplayMode = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- BoxViewDisplay">
    /**
     * Box view display
     *
     * @return Box view display
     */
    public SimulationBoxViewEnum getBoxViewDisplay() {
        return this.boxViewDisplay;
    }

    /**
     * Default box view display
     *
     * @return Default display of simulation box
     */
    public SimulationBoxViewEnum getDefaultBoxViewDisplay() {
        return ModelDefinitions.DEFAULT_BOX_VIEW_DISPLAY;
    }

    /**
     * Box view display
     *
     * @param aBoxViewDisplay Box view display
     * @return True: Value changed, false: Otherwise
     */
    public boolean setBoxViewDisplay(SimulationBoxViewEnum aBoxViewDisplay) {
        if (this.boxViewDisplay != aBoxViewDisplay) {
            this.boxViewDisplay = aBoxViewDisplay;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- FrameDisplaySlicer">
    /**
     * True: Simulation box frame is displayed, false: Otherwise
     *
     * @return True: Simulation box frame is displayed, false: Otherwise
     */
    public boolean isFrameDisplaySlicer() {
        return this.isFrameDisplaySlicer;
    }

    /**
     * Default frame display of slicer
     *
     * @return Default frame display of slicer
     */
    public boolean getDefaultFrameDisplaySlicer() {
        return ModelDefinitions.DEFAULT_FRAME_DISPLAY_SLICER;
    }

    /**
     * True: Simulation box frame is displayed, false: Otherwise
     *
     * @param aValue Value
     * @return True: Value changed, false: Otherwise
     */
    public boolean setFrameDisplaySlicer(boolean aValue) {
        if (this.isFrameDisplaySlicer != aValue) {
            this.isFrameDisplaySlicer = aValue;
            return true;
        } else {
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CurrentParticleSetFilename">
    /**
     * Filename of current particle set
     *
     * @return Filename of current particle set
     * (DPD_source)
     */
    public String getCurrentParticleSetFilename() {
        return this.currentParticleSetFilename;
    }

    /**
     * Filename of current particle set
     *
     * @param aCurrentParticleSetFilename Filename of current particle set
     * @return True: Value changed, false: Otherwise
     */
    public boolean setCurrentParticleSetFilename(String aCurrentParticleSetFilename) {
        boolean tmpHasChanged = false;
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.hasParticleSetFilename(aCurrentParticleSetFilename)) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set new value if changed">
        if (!this.currentParticleSetFilename.equals(aCurrentParticleSetFilename)) {
            this.currentParticleSetFilename = aCurrentParticleSetFilename;
            tmpHasChanged = true;
        }
        // </editor-fold>
        return tmpHasChanged;
    }

    /**
     * Returns if aParticleSetFilename is already available in DPD source or custom particles path.
     * 
     * @param aParticleSetFilename Particle set filename
     * @return True: aParticleSetFilename is already available in DPD source or custom particles path, false: Otherwise
     */
    public boolean hasParticleSetFilename(String aParticleSetFilename) {
        if ((new File(this.getDpdSourceParticlesPath() + File.separatorChar + aParticleSetFilename)).isFile()) {
            return true;
        }
        if ((new File(this.getCustomParticlesPath() + File.separatorChar + aParticleSetFilename)).isFile()) {
            return true;
        }
        return false;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- SchemaValueItemContainer (table-data schema value items)">
    /**
     * Returns all schema value items from container
     *
     * @return All schema value items or null if none are available
     */
    public ValueItem[] getSchemaValueItems() {
        return this.schemaValueItemContainer.getValueItemsOfContainer();
    }

    /**
     * Returns name sorted schema value items from container
     *
     * @return Name sorted schema value items or null if none are available
     */
    public ValueItem[] getNameSortedSchemaValueItems() {
        return this.schemaValueItemContainer.getNameSortedValueItemsOfContainer();
    }

    /**
     * Sorted schema value item names
     *
     * @return Sorted schema value item names or null if no schema is defined
     */
    public String[] getSortedSchemaValueItemNames() {
        if (this.hasSchemaValueItems()) {
            ValueItem[] tmpSchemaValueItemArray = this.getSchemaValueItems();
            String[] tmpSchemaNameArray = new String[tmpSchemaValueItemArray.length];
            for (int i = 0; i < tmpSchemaValueItemArray.length; i++) {
                tmpSchemaNameArray[i] = tmpSchemaValueItemArray[i].getName();
            }
            Arrays.sort(tmpSchemaNameArray);
            return tmpSchemaNameArray;
        } else {
            return null;
        }
    }

    /**
     * Returns schema value item
     *
     * @param aSchemaValueItemName Schema value item name
     * @return Schema value item or null if not available
     */
    public ValueItem getSchemaValueItem(String aSchemaValueItemName) {
        return this.schemaValueItemContainer.getValueItem(aSchemaValueItemName);
    }

    /**
     * Returns number of all schema value items from container
     *
     * @return Number of all schema value items
     */
    public int getNumberOfSchemaValueItems() {
        return this.schemaValueItemContainer.getSize();
    }

    /**
     * Returns if schema value item with specified name exists
     *
     * @param aSchemaValueItemName Schema value item name
     * @return True: Schema value item with specified name exists, false:
     * Otherwise
     */
    public boolean hasSchemaValueItem(String aSchemaValueItemName) {
        return this.schemaValueItemContainer.hasValueItem(aSchemaValueItemName);
    }

    /**
     * Returns if schema value item with specified display name exists
     *
     * @param aSchemaValueItemDisplayName Schema value item display name
     * @return True: Schema value item with specified display name exists, false:
     * Otherwise
     */
    public boolean hasSchemaValueItemWithDisplayName(String aSchemaValueItemDisplayName) {
        return this.schemaValueItemContainer.hasValueItemWithDisplayName(aSchemaValueItemDisplayName);
    }

    /**
     * Returns if matching schema value item already exists.
     *
     * @param aSchemaValueItem Schema value item to be checked
     * @return True: Matching schema value item already exists, false: Otherwise
     */
    public boolean hasMatchingSchemaValueItem(ValueItem aSchemaValueItem) {
        ValueItem[] tmpSchemaValueItems = this.getSchemaValueItems();
        if (tmpSchemaValueItems != null) {
            for (ValueItem tmpSingleSchemaValueItem : tmpSchemaValueItems) {
                if (tmpSingleSchemaValueItem != null && tmpSingleSchemaValueItem.matchesSchemaValueItem(aSchemaValueItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns if schema value items exist
     *
     * @return True: Schema value items exist, false: Otherwise
     */
    public boolean hasSchemaValueItems() {
        return this.schemaValueItemContainer.getSize() > 0;
    }

    /**
     * Adds schema value item to container
     *
     * @param aSchemaValueItem Schema value item
     * @return True: Schema value item was successfully added, false: Otherwise
     */
    public boolean addSchemaValueItem(ValueItem aSchemaValueItem) {
        // NOTE: Checks are not necessary at this point!
        boolean tmpHasChanged = false;
        tmpHasChanged = this.schemaValueItemContainer.addValueItem(aSchemaValueItem);
        // React on changes with tmpHasChanged if necessary
        return tmpHasChanged;
    }

    /**
     * Removes schema value item with specified name
     *
     * @param aSchemaValueItemName Schema value item name to be removed
     * @return True: Specified schema value item was removed, false: Otherwise
     */
    public boolean removeSchemaValueItem(String aSchemaValueItemName) {
        // NOTE: Checks are not necessary at this point!
        boolean tmpHasChanged = false;
        tmpHasChanged = this.schemaValueItemContainer.removeValueItem(aSchemaValueItemName);
        // React on changes with tmpHasChanged if necessary
        return tmpHasChanged;
    }

    /**
     * Removes schema value items with specified names
     *
     * @param aSchemaValueItemNameArrayForRemoval Schema value item name array
     * for removal
     * @return True: Specified schema value items were removed (if possible),
     * false: Otherwise
     */
    public boolean removeSchemaValueItems(String[] aSchemaValueItemNameArrayForRemoval) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSchemaValueItemNameArrayForRemoval == null || aSchemaValueItemNameArrayForRemoval.length == 0) {
            return false;
        }
        // </editor-fold>
        boolean tmpHasChanged = false;
        for (String tmpSchemaValueItemName : aSchemaValueItemNameArrayForRemoval) {
            if (this.schemaValueItemContainer.removeValueItem(tmpSchemaValueItemName)) {
                tmpHasChanged = true;
            }
        }
        // React on changes with tmpHasChanged if necessary
        return tmpHasChanged;
    }

    /**
     * Replaces schema value item with specified name with specified new schema
     * value item
     *
     * @param aSchemaValueItemNameToBeRemoved Name of schema value item to be
     * replaced (NOTE: Name of schema value item is IDENTICAL to its display
     * name)
     * @param aNewSchemaValueItem New schema value item for replacement
     *
     * @return True: Specified schema value item was replaced, false: Otherwise
     */
    public boolean replaceSchemaValueItem(String aSchemaValueItemNameToBeRemoved, ValueItem aNewSchemaValueItem) {
        // NOTE: Checks are not necessary at this point!
        boolean tmpHasChanged = false;
        if (aNewSchemaValueItem != null
                && this.schemaValueItemContainer.hasValueItem(aSchemaValueItemNameToBeRemoved)
                && !this.schemaValueItemContainer.hasValueItem(aNewSchemaValueItem.getName())) {
            this.schemaValueItemContainer.removeValueItem(aSchemaValueItemNameToBeRemoved);
            this.schemaValueItemContainer.addValueItem(aNewSchemaValueItem);
            tmpHasChanged = true;
        }
        // React on changes with tmpHasChanged if necessary
        return tmpHasChanged;
    }

    /**
     * Removes all schema value items
     */
    public void removeAllSchemaValueItems() {
        if (this.schemaValueItemContainer.getSize() > 0) {
            this.schemaValueItemContainer.clear();
        }
    }

    /**
     * Reads schema value item container from file which was written with
     * writeSchemaValueItemContainerToFile()
     *
     * @param aSchemaValueItemContainerFilePathname Filepathname of schema value
     * item container to load
     * @return True: Operation was successfully performed, false: Otherwise
     */
    public boolean readSchemaValueItemContainerFromFile(String aSchemaValueItemContainerFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSchemaValueItemContainerFilePathname == null || aSchemaValueItemContainerFilePathname.isEmpty()) {
            return false;
        }
        if (!(new File(aSchemaValueItemContainerFilePathname)).isFile()) {
            return false;
        }
        // </editor-fold>
        try {
            String tmpXmlString = this.fileUtilityMethods.readTextFileIntoSingleString(aSchemaValueItemContainerFilePathname);
            if (tmpXmlString == null) {
                return false;
            }
            Element tmpRoot = new SAXBuilder().build(new StringReader(tmpXmlString)).getRootElement();
            String tmpRootName = tmpRoot.getName();
            if (!tmpRootName.equals(PreferenceXmlName.MFSIM_TABLE_DATA_SCHEMATA)) {
                return false;
            }
            if (tmpRoot.getChild(PreferenceXmlName.VERSION) == null) {
                return false;
            }
            String tmpVersion = tmpRoot.getChild(PreferenceXmlName.VERSION).getText();
            if (tmpVersion.equals("Version 1.0.0")) {
                Element tmpCurrentElement = tmpRoot.getChild(PreferenceXmlName.SCHEMA_VALUE_ITEM_CONTAINER);
                if (tmpCurrentElement != null) {
                    this.schemaValueItemContainer = new ValueItemContainer(this.stringUtilityMethods.decompressBase64String(tmpCurrentElement.getText()), null);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Merges schema value item container from file which was written with
     * writeSchemaValueItemContainerToFile()
     *
     * @param aSchemaValueItemContainerFilePathname File pathname of schema
     * value item container to merge
     * @return True: Operation was successfully performed, false: Otherwise
     */
    public boolean mergeSchemaValueItemContainerFromFile(String aSchemaValueItemContainerFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSchemaValueItemContainerFilePathname == null || aSchemaValueItemContainerFilePathname.isEmpty()) {
            return false;
        }
        if (!(new File(aSchemaValueItemContainerFilePathname)).isFile()) {
            return false;
        }
        // </editor-fold>
        try {
            String tmpXmlString = this.fileUtilityMethods.readTextFileIntoSingleString(aSchemaValueItemContainerFilePathname);
            if (tmpXmlString == null) {
                return false;
            }
            Element tmpRoot = new SAXBuilder().build(new StringReader(tmpXmlString)).getRootElement();
            String tmpRootName = tmpRoot.getName();
            if (!tmpRootName.equals(PreferenceXmlName.MFSIM_TABLE_DATA_SCHEMATA)) {
                return false;
            }
            if (tmpRoot.getChild(PreferenceXmlName.VERSION) == null) {
                return false;
            }
            String tmpVersion = tmpRoot.getChild(PreferenceXmlName.VERSION).getText();
            if (tmpVersion.equals("Version 1.0.0")) {
                Element tmpCurrentElement = tmpRoot.getChild(PreferenceXmlName.SCHEMA_VALUE_ITEM_CONTAINER);
                if (tmpCurrentElement != null) {
                    ValueItemContainer tmpSchemaValueItemContainerToMerge = new ValueItemContainer(this.stringUtilityMethods.decompressBase64String(tmpCurrentElement.getText()), null);
                    // Merge schema value items with this.schemaValueItemContainer
                    ValueItem[] tmpSchemaValueItemArrayToMerge = tmpSchemaValueItemContainerToMerge.getValueItemsOfContainer();
                    ValueItem[] tmpExistingSchemaValueItemArray = this.schemaValueItemContainer.getValueItemsOfContainer();
                    for (ValueItem tmpSchemaValueItemToMerge : tmpSchemaValueItemArrayToMerge) {
                        boolean tmpIsSchemaMatch = false;
                        for (ValueItem tmpExistingSchemaValueItem : tmpExistingSchemaValueItemArray) {
                            if (tmpSchemaValueItemToMerge.matchesSchemaValueItem(tmpExistingSchemaValueItem)) {
                                tmpIsSchemaMatch = true;
                                break;
                            }
                        }
                        if (!tmpIsSchemaMatch) {
                            if (!this.hasSchemaValueItem(tmpSchemaValueItemToMerge.getName())) {
                                this.schemaValueItemContainer.addValueItem(tmpSchemaValueItemToMerge);
                            } else {
                                // Rename tmpSchemaValueItemToMerge
                                String tmpNewDisplayName = tmpSchemaValueItemToMerge.getName();
                                int tmpIndex = 0;
                                while (this.hasSchemaValueItem(tmpNewDisplayName)) {
                                    tmpIndex++;
                                    tmpNewDisplayName = tmpSchemaValueItemToMerge.getName() + " (" + String.valueOf(tmpIndex) + ")";
                                }
                                tmpSchemaValueItemToMerge.setName(tmpNewDisplayName);
                                this.schemaValueItemContainer.addValueItem(tmpSchemaValueItemToMerge);
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }

    /**
     * Writes schema value item container to file
     *
     * @param aSchemaValueItemContainerFilePathname File pathname for schema value item container
     * @return True: Operation was successfully performed, false: Otherwise
     */
    public boolean writeSchemaValueItemContainerToFile(String aSchemaValueItemContainerFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSchemaValueItemContainerFilePathname == null || aSchemaValueItemContainerFilePathname.isEmpty()) {
            return false;
        }
        if ((new File(aSchemaValueItemContainerFilePathname)).isFile()) {
            if (!this.fileUtilityMethods.deleteSingleFile(aSchemaValueItemContainerFilePathname)) {
                return false;
            }
        }
        // </editor-fold>
        try {
            Element tmpRoot = new Element(PreferenceXmlName.MFSIM_TABLE_DATA_SCHEMATA);
            // Version
            tmpRoot.addContent(new Element(PreferenceXmlName.VERSION).addContent("Version 1.0.0"));
            // this.schemaValueItemContainer
            tmpRoot.addContent(new Element(PreferenceXmlName.SCHEMA_VALUE_ITEM_CONTAINER).addContent(this.stringUtilityMethods.compressIntoBase64String(this.schemaValueItemContainer.getAsXmlString())));
            XMLOutputter tmpOutputter = new XMLOutputter(Format.getPrettyFormat());
            Document tmpDocument = new Document();
            tmpDocument.setRootElement(tmpRoot);
            return this.fileUtilityMethods.writeSingleStringToTextFile(tmpOutputter.outputString(tmpDocument), aSchemaValueItemContainerFilePathname);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
    }
    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc="- Combined properties">
    /**
     * Sets main frame height and width
     *
     * @param aMainFrameHeight Main frame height
     * @param aMainFrameWidth Main frame width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setMainFrameHeightWidth(int aMainFrameHeight, int aMainFrameWidth) {
        boolean tmpHasChanged = false;
        if (this.setMainFrameHeight(aMainFrameHeight)) {
            tmpHasChanged = true;
        }
        if (this.setMainFrameWidth(aMainFrameWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets slicer show dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogSlicerShowHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogSlicerShowHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogSlicerShowWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets custom dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setCustomDialogHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setCustomDialogHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setCustomDialogWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets single slicer show dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogSingleSlicerShowHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogSingleSlicerShowHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogSingleSlicerShowWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets through time slicer show dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogSimulationMovieSlicerShowHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogSimulationMovieSlicerShowHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogSimulationMovieSlicerShowWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets value item edit dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogValueItemEditHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogValueItemEditHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogValueItemEditWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets text edit dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogTextEditHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogTextEditHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogTextEditWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets value item show dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogValueItemShowHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogValueItemShowHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogValueItemShowWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets table-data schemata manage dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogTableDataSchemataManageHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogTableDataSchemataManageHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogTableDataSchemataManageWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets value item matrix diagram dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogValueItemMatrixDiagramHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogValueItemMatrixDiagramHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogValueItemMatrixDiagramWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets structure edit dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogStructureEditHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogStructureEditHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogStructureEditWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets peptide edit dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogPeptideEditHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogPeptideEditHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogPeptideEditWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }

    /**
     * Sets compartment edit dialog height and width
     *
     * @param aDialogHeight Dialog height
     * @param aDialogWidth Dialog width
     * @return True: Value changed, false: Otherwise
     */
    public boolean setDialogCompartmentEditHeightWidth(int aDialogHeight, int aDialogWidth) {
        boolean tmpHasChanged = false;
        if (this.setDialogCompartmentEditHeight(aDialogHeight)) {
            tmpHasChanged = true;
        }
        if (this.setDialogCompartmentEditWidth(aDialogWidth)) {
            tmpHasChanged = true;
        }
        return tmpHasChanged;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Editable preferences related methods">
    // <editor-fold defaultstate="collapsed" desc="-- Directories related value item return methods">
    /**
     * Adds value items of editable preferences of directories to value item
     * container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addDirectoriesEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.Directories")};
        // <editor-fold defaultstate="collapsed" desc="Internal DPD Job path">
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        if (this.isJobWorking()) {
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(false));
        } else {
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.DIRECTORY));
        }
        tmpValueItem.setName(PreferenceEditableEnum.INTERNAL_MFSIM_JOB_PATH.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.InternalMFsimJobPath.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.InternalMFsimJobPath"));
        tmpValueItem.setValue(this.internalMFsimJobPath);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Internal temp directory">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        if (this.isJobWorking()) {
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(false));
        } else {
            tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.DIRECTORY));
        }
        tmpValueItem.setName(PreferenceEditableEnum.INTERNAL_TEMP_PATH.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.InternalTempDirectory.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.InternalTempDirectory"));
        tmpValueItem.setValue(this.internalTempPath);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        return aVerticalPosition;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Filter related value item return methods">
    /**
     * Adds value items of editable preferences of job input filter to value
     * item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addJobInputFilterEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.JobInputFilter")};

        ValueItem tmpValueItem = this.getJobInputFilterAfterTimestampValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJobInputFilterBeforeTimestampValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJobInputFilterContainsPhraseValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Adds value items of editable preferences of job result filter to value
     * item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addJobResultFilterEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.JobResultFilter")};

        ValueItem tmpValueItem = this.getJobResultFilterAfterTimestampValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJobResultFilterBeforeTimestampValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJobResultFilterContainsPhraseValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.jobInputFilterAfterTimestamp
     *
     * @return Value item for this.jobInputFilterAfterTimestamp
     */
    private ValueItem getJobInputFilterAfterTimestampValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TIMESTAMP_EMPTY));
        tmpValueItem.setName(PreferenceEditableEnum.JOB_INPUT_FILTER_AFTER_TIMESTAMP.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobInputFilter.AfterTimestamp.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobInputFilter.AfterTimestamp"));
        tmpValueItem.setValue(this.jobInputFilterAfterTimestamp);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jobInputFilterBeforeTimestamp
     *
     * @return Value item for this.jobInputFilterBeforeTimestamp
     */
    private ValueItem getJobInputFilterBeforeTimestampValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TIMESTAMP_EMPTY));
        tmpValueItem.setName(PreferenceEditableEnum.JOB_INPUT_FILTER_BEFORE_TIMESTAMP.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobInputFilter.BeforeTimestamp.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobInputFilter.BeforeTimestamp"));
        tmpValueItem.setValue(this.jobInputFilterBeforeTimestamp);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jobInputFilterContainsPhrase
     *
     * @return Value item for this.jobInputFilterContainsPhrase
     */
    private ValueItem getJobInputFilterContainsPhraseValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        tmpValueItem.setName(PreferenceEditableEnum.JOB_INPUT_FILTER_CONTAINS_PHRASE.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobInputFilter.ContainsPhrase.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobInputFilter.ContainsPhrase"));
        tmpValueItem.setValue(this.jobInputFilterContainsPhrase);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jobResultFilterAfterTimestamp
     *
     * @return Value item for this.jobResultFilterAfterTimestamp
     */
    private ValueItem getJobResultFilterAfterTimestampValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TIMESTAMP_EMPTY));
        tmpValueItem.setName(PreferenceEditableEnum.JOB_RESULT_FILTER_AFTER_TIMESTAMP.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultFilter.AfterTimestamp.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultFilter.AfterTimestamp"));
        tmpValueItem.setValue(this.jobResultFilterAfterTimestamp);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jobResultFilterBeforeTimestamp
     *
     * @return Value item for this.jobResultFilterBeforeTimestamp
     */
    private ValueItem getJobResultFilterBeforeTimestampValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TIMESTAMP_EMPTY));
        tmpValueItem.setName(PreferenceEditableEnum.JOB_RESULT_FILTER_BEFORE_TIMESTAMP.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultFilter.BeforeTimestamp.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultFilter.BeforeTimestamp"));
        tmpValueItem.setValue(this.jobResultFilterBeforeTimestamp);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jobResultFilterContainsPhrase
     *
     * @return Value item for this.jobResultFilterContainsPhrase
     */
    private ValueItem getJobResultFilterContainsPhraseValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT));
        tmpValueItem.setName(PreferenceEditableEnum.JOB_RESULT_FILTER_CONTAINS_PHRASE.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultFilter.ContainsPhrase.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultFilter.ContainsPhrase"));
        tmpValueItem.setValue(this.jobResultFilterContainsPhrase);
        return tmpValueItem;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Compartment graphics related value item return methods">
    /**
     * Adds value items of editable preferences of compartment graphics settings
     * to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addCompartmentGraphicsSettingsEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.CompartmentGraphicsSettings")};

        ValueItem tmpValueItem = this.getDepthAttenuationCompartmentValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getColorTransparencyCompartmentValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getColorGradientAttenuationCompartmentValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getConstantCompartmentBodyVolumeFlagValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getCompartmentBodyChangeResponseFactorValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.colorTransparencyCompartment
     *
     * @return Value item for this.colorTransparencyCompartment
     */
    private ValueItem getColorTransparencyCompartmentValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultColorTransparencyCompartment()), 2, 0.00, 1.00));
        tmpValueItem.setName(PreferenceEditableEnum.COLOR_TRANSPARENCY_COMPARTMENT.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.ColorTransparency.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.ColorTransparency"));
        tmpValueItem.setValue(String.valueOf(this.colorTransparencyCompartment));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.colorGradientAttenuationCompartment
     *
     * @return Value item for this.colorGradientAttenuationCompartment
     */
    private ValueItem getColorGradientAttenuationCompartmentValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultColorGradientAttenuationCompartment()), 2, 0.00, 1.00));
        tmpValueItem.setName(PreferenceEditableEnum.COLOR_GRADIENT_ATTENUATION_COMPARTMENT.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.ColorGradientAttenuation.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.ColorGradientAttenuation"));
        tmpValueItem.setValue(String.valueOf(this.colorGradientAttenuationCompartment));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.depthAttenuationCompartment
     *
     * @return Value item for this.depthAttenuationCompartment
     */
    private ValueItem getDepthAttenuationCompartmentValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultDepthAttenuationCompartment()), 2,
                ModelDefinitions.COLOR_SHAPE_ATTENUATION_COMPARTMENT_MINIMUM, ModelDefinitions.COLOR_SHAPE_ATTENUATION_COMPARTMENT_MAXIMUM));
        tmpValueItem.setName(PreferenceEditableEnum.COLOR_SHAPE_ATTENUATION_COMPARTMENT.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.DepthAttenuation.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.DepthAttenuation"));
        tmpValueItem.setValue(String.valueOf(this.depthAttenuationCompartment));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.isConstantCompartmentBodyVolume
     *
     * @return Value item for this.isConstantCompartmentBodyVolume
     */
    private ValueItem getConstantCompartmentBodyVolumeFlagValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                ModelMessage.get("Preferences.GraphicsSettings.ConstantCompartmentBodyVolumeFlag.True"), 
                new String[] {
                    ModelMessage.get("Preferences.GraphicsSettings.ConstantCompartmentBodyVolumeFlag.True"), 
                    ModelMessage.get("Preferences.GraphicsSettings.ConstantCompartmentBodyVolumeFlag.False")
                }
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.IS_CONSTANT_COMPARTMENT_BODY_VOLUME.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.ConstantCompartmentBodyVolumeFlag.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.ConstantCompartmentBodyVolumeFlag"));
        if (this.isConstantCompartmentBodyVolume) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.GraphicsSettings.ConstantCompartmentBodyVolumeFlag.True"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.GraphicsSettings.ConstantCompartmentBodyVolumeFlag.False"));
        }
        return tmpValueItem;
    }

    /**
     * Returns value item for this.compartmentBodyChangeResponseFactor
     *
     * @return Value item for this.compartmentBodyChangeResponseFactor
     */
    private ValueItem getCompartmentBodyChangeResponseFactorValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(
            new ValueItemDataTypeFormat(
                String.valueOf(this.getDefaultCompartmentBodyChangeResponseFactor()), 
                6,
                ModelDefinitions.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR_MINIMUM, 
                ModelDefinitions.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR_MAXIMUM
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.CompartmentBodyChangeResponseFactor.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.CompartmentBodyChangeResponseFactor"));
        tmpValueItem.setValue(String.valueOf(this.compartmentBodyChangeResponseFactor));
        return tmpValueItem;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Simulation box display value item return methods">
    /**
     * Adds value items of simulation box display preferences to value item
     * container
     *
     * @param aValueItemContainer Value item container
     * @param aNodeNames Node names
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addSimulationBoxDisplayEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.Slicer"), new String[]{
            ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.Slicer"), ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.JmolViewer")}));
        tmpValueItem.setName(PreferenceEditableEnum.IS_SIMULATION_BOX_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer"));
        if (this.isSimulationBoxSlicer) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.Slicer"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.Miscellaneous.IsSimulationBoxSlicer.JmolViewer"));
        }
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);
        return aVerticalPosition;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Simulation box slicer graphics settings value item return methods">

    /**
     * Adds value items of editable preferences of simulation box slicer
     * settings to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aNodeNames Node names
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addSlicerEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {

        ValueItem tmpValueItem = this.getSlicerGraphicsModeValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getSimulationBoxBackgroundColorSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getImageStorageModeValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJpegImageQualityValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getBoxViewDisplayValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getNumberOfSlicesPerViewValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getFirstSliceIndexValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getSingleSliceDisplayValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getNumberOfVolumeBinsValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);
        
        tmpValueItem = this.getSimulationBoxMagnificationPercentageValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getMeasurementColorSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getMoleculeSelectionColorSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getMaxSelectedMoleculeNumberSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getDepthAttenuationSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getFrameDisplaySlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getFrameColorSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getNumberOfFramePointsSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getColorGradientAttenuationSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getSpecularWhiteAttenuationSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getSpecularWhiteSizeSlicerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getRadialGradienPaintFocusFactorsValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getRadialGradientPaintRadiusMagnificationValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.jpegImageQuality
     *
     * @return Value item for this.jpegImageQuality
     */
    private ValueItem getJpegImageQualityValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultJpegImageQuality()), 2, 0.00, 1.00));
        tmpValueItem.setName(PreferenceEditableEnum.JPEG_IMAGE_QUALITY.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.JpegImageQuality.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.JpegImageQuality"));
        tmpValueItem.setValue(String.valueOf(this.jpegImageQuality));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.numberOfSlicesPerView
     *
     * @return Value item for this.numberOfSlicesPerView
     */
    private ValueItem getNumberOfSlicesPerViewValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        // NOTE: tmpNumberOfSlicesArray MUST correspond to ModelDefinitions.MINIMUM_NUMBER_OF_SLICES and ModelDefinitions.MAXIMUM_NUMBER_OF_SLICES
        String[] tmpNumberOfSlicesArray = 
            new String[]
                {
                    "10", "20", "30", "40", "50", "60", "70", "80", "90", 
                    "100", "150", "200", "250", "300", "350", "400", "450", 
                    "500", "550", "600", "650", "700", "750", "800", "850", "900", "950", 
                    "1000"
                };
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultNumberOfSlicesPerView()), tmpNumberOfSlicesArray));
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_SLICES.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.NumberOfSlices.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.NumberOfSlices"));
        tmpValueItem.setValue(String.valueOf(this.numberOfSlicesPerView));
        tmpValueItem.setUpdateNotifier(true);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.firstSliceIndex
     *
     * @return Value item for this.firstSliceIndex
     */
    private ValueItem getFirstSliceIndexValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultFirstSliceIndex()), 0, 0.0, (double) (this.getNumberOfSlicesPerView() - 1)));
        tmpValueItem.setName(PreferenceEditableEnum.FIRST_SLICE_INDEX.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FirstSliceIndex.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FirstSliceIndex"));
        tmpValueItem.setValue(String.valueOf(this.firstSliceIndex));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.numberOfVolumeBins
     *
     * @return Value item for this.numberOfVolumeBins
     */
    private ValueItem getNumberOfVolumeBinsValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultNumberOfVolumeBins()), 0, ModelDefinitions.MINIMUM_NUMBER_OF_ZOOM_VOLUME_BINS, Double.POSITIVE_INFINITY));
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_ZOOM_VOLUME_BINS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.NumberOfVolumeBins.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.NumberOfVolumeBins"));
        tmpValueItem.setValue(String.valueOf(this.numberOfVolumeBins));
        return tmpValueItem;
    }

    /**
     * Add number of volume bins value item to value item container
     * 
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition Vertical position
     * @return Incremented vertical position
     */
    private int addNumberOfVolumeBinsValueItem(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = 
            new String[]
                {
                    ModelMessage.get("Preferences.Root"), 
                    ModelMessage.get("Preferences.SimulationBox"),
                    ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings")
                };
        
        ValueItem tmpValueItem = this.getNumberOfVolumeBinsValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }
    
    /**
     * Returns value item for this.numberOfFramePointsSlicer
     *
     * @return Value item for this.numberOfFramePointsSlicer
     */
    private ValueItem getNumberOfFramePointsSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                String.valueOf(ModelDefinitions.DEFAULT_NUMBER_OF_FRAME_POINTS_SLICER), 0, (double) ModelDefinitions.MINIMUM_NUMBER_OF_FRAME_POINTS_SLICER, Double.MAX_VALUE));
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_FRAME_POINTS_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.NumberOfFramePointsSlicer.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.NumberOfFramePointsSlicer"));
        tmpValueItem.setValue(String.valueOf(this.numberOfFramePointsSlicer));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.simulationBoxMagnificationPercentage
     *
     * @return Value item for this.simulationBoxMagnificationPercentage
     */
    private ValueItem getSimulationBoxMagnificationPercentageValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.DEFAULT_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE), 0,
                ModelDefinitions.MINIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE, ModelDefinitions.MAXIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE));
        tmpValueItem.setName(PreferenceEditableEnum.SIMULATION_BOX_MAGNIFICATION_PERCENTAGE.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SimulationBoxMagnificationPercentage.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SimulationBoxMagnificationPercentage"));
        tmpValueItem.setValue(String.valueOf(this.simulationBoxMagnificationPercentage));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.isSingleSliceDisplay
     *
     * @return Value item for this.isSingleSliceDisplay
     */
    private ValueItem getSingleSliceDisplayValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SingleSliceDisplay.BackgroundYes"), new String[]{
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SingleSliceDisplay.BackgroundYes"),
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SingleSliceDisplay.BackgroundNo")}));
        tmpValueItem.setName(PreferenceEditableEnum.IS_SINGLE_SLICE_DISPLAY.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SingleSliceDisplay.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SingleSliceDisplay"));
        if (this.isSingleSliceDisplay) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SingleSliceDisplay.BackgroundNo"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SingleSliceDisplay.BackgroundYes"));
        }
        return tmpValueItem;
    }

    /**
     * Returns value item for this.slicerGraphicsMode
     *
     * @return Value item for this.slicerGraphicsMode
     */
    private ValueItem getSlicerGraphicsModeValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.PixelAll"),
                new String[]{
                    ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.PixelAll"),
                    ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.PixelFinal"),
                    ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.BufferedImageAll"),
                    ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.BufferedImageFinal"),
                    ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.VolatileImageAll"),
                    ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.VolatileImageFinal")
                }));
        tmpValueItem.setName(PreferenceEditableEnum.SLICER_GRAPHICS_MODE.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode"));
        switch (this.slicerGraphicsMode) {
            case PIXEL_ALL:
                tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.PixelAll"));
                break;
            case PIXEL_FINAL:
                tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.PixelFinal"));
                break;
            case BUFFERED_IMAGE_ALL:
                tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.BufferedImageAll"));
                break;
            case BUFFERED_IMAGE_FINAL:
                tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.BufferedImageFinal"));
                break;
            case VOLATILE_IMAGE_ALL:
                tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.VolatileImageAll"));
                break;
            case VOLATILE_IMAGE_FINAL:
                tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.GraphicsMode.VolatileImageFinal"));
                break;
        }
        return tmpValueItem;
    }

    /**
     * Returns value item for this.simulationBoxBackgroundColorSlicer
     *
     * @return Value item for this.simulationBoxBackgroundColorSlicer
     */
    private ValueItem getSimulationBoxBackgroundColorSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelDefinitions.DEFAULT_SIMULATION_BOX_BACKGROUND_COLOR_SLICER.toString(), StandardColorEnum.getAllColorRepresentations()));
        tmpValueItem.setName(PreferenceEditableEnum.SIMULATION_BOX_BACKGROUND_COLOR_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SimulationBoxBackgroundColorSlicer.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.SimulationBoxBackgroundColorSlicer"));
        tmpValueItem.setValue(this.simulationBoxBackgroundColorSlicer);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.measurementColorSlicer
     *
     * @return Value item for this.measurementColorSlicer
     */
    private ValueItem getMeasurementColorSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelDefinitions.DEFAULT_MEASUREMENT_COLOR_SLICER.toString(), StandardColorEnum.getAllColorRepresentations()));
        tmpValueItem.setName(PreferenceEditableEnum.MEASUREMENT_COLOR_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.MeasurementColorSlicer.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.MeasurementColorSlicer"));
        tmpValueItem.setValue(this.measurementColorSlicer);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.moleculeSelectionColorSlicer
     *
     * @return Value item for this.moleculeSelectionColorSlicer
     */
    private ValueItem getMoleculeSelectionColorSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelDefinitions.DEFAULT_MOLECULE_SELECTION_COLOR_SLICER.toString(), StandardColorEnum.getAllColorRepresentations()));
        tmpValueItem.setName(PreferenceEditableEnum.MOLECULE_SELECTION_COLOR_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.MoleculeSelectionColorSlicer.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.MoleculeSelectionColorSlicer"));
        tmpValueItem.setValue(this.moleculeSelectionColorSlicer);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.frameColorSlicer
     *
     * @return Value item for this.frameColorSlicer
     */
    private ValueItem getFrameColorSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelDefinitions.DEFAULT_FRAME_COLOR_SLICER.toString(), StandardColorEnum.getAllColorRepresentations()));
        tmpValueItem.setName(PreferenceEditableEnum.FRAME_COLOR_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameColorSlicer.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameColorSlicer"));
        tmpValueItem.setValue(this.frameColorSlicer);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.imageStorageMode
     *
     * @return Value item for this.imageStorageMode
     */
    private ValueItem getImageStorageModeValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.HarddiskCompressed"), new String[]{
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.HarddiskCompressed"),
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.MemoryCompressed"),
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.MemoryUncompressed")}));
        tmpValueItem.setName(PreferenceEditableEnum.IMAGE_STORAGE_MODE.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode"));
        switch (this.imageStorageMode) {
            case HARDDISK_COMPRESSED:
                tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.HarddiskCompressed"));
                break;
            case MEMORY_COMPRESSED:
                tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.MemoryCompressed"));
                break;
            case MEMORY_UNCOMPRESSED:
                tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ImageStorageMode.MemoryUncompressed"));
                break;
        }
        return tmpValueItem;
    }

    /**
     * Returns value item for this.colorGradientAttenuationSlicer
     *
     * @return Value item for this.colorGradientAttenuationSlicer
     */
    private ValueItem getColorGradientAttenuationSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultColorGradientAttenuationSlicer()), 2, 0.00, 1.00));
        tmpValueItem.setName(PreferenceEditableEnum.COLOR_GRADIENT_ATTENUATION_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.ColorGradientAttenuation.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.ColorGradientAttenuation"));
        tmpValueItem.setValue(String.valueOf(this.colorGradientAttenuationSlicer));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.specularWhiteAttenuationSlicer
     *
     * @return Value item for this.specularWhiteAttenuationSlicer
     */
    private ValueItem getSpecularWhiteAttenuationSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultSpecularWhiteAttenuationSlicer()), 2, 0.00, 1.00));
        tmpValueItem.setName(PreferenceEditableEnum.SPECULAR_WHITE_ATTENUATION_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.SpecularWhiteAttenuation.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.SpecularWhiteAttenuation"));
        tmpValueItem.setValue(String.valueOf(this.specularWhiteAttenuationSlicer));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.specularWhiteSizeSlicer
     *
     * @return Value item for this.specularWhiteSizeSlicer
     */
    private ValueItem getSpecularWhiteSizeSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultSpecularWhiteSizeSlicer()), 2, ModelDefinitions.SPECULAR_WHITE_SIZE_SLICER_MINIMUM,
                ModelDefinitions.SPECULAR_WHITE_SIZE_SLICER_MAXIMUM));
        tmpValueItem.setName(PreferenceEditableEnum.SPECULAR_WHITE_SIZE_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.SpecularWhiteSize.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.SpecularWhiteSize"));
        tmpValueItem.setValue(String.valueOf(this.specularWhiteSizeSlicer));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.radialGradientPaintRadiusMagnification
     *
     * @return Value item for this.radialGradientPaintRadiusMagnification
     */
    private ValueItem getRadialGradientPaintRadiusMagnificationValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultRadialGradientPaintRadiusMagnification()), 2,
                ModelDefinitions.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION_MINIMUM, ModelDefinitions.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION_MAXIMUM));
        tmpValueItem.setName(PreferenceEditableEnum.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.RadialGradientPaintRadiusMagnification.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.RadialGradientPaintRadiusMagnification"));
        tmpValueItem.setValue(String.valueOf(this.radialGradientPaintRadiusMagnification));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.maxSelectedMoleculeNumberSlicer
     *
     * @return Value item for this.maxSelectedMoleculeNumberSlicer
     */
    private ValueItem getMaxSelectedMoleculeNumberSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(
            new ValueItemDataTypeFormat(
                String.valueOf(this.getDefaultMaxSelectedMoleculeNumberSlicer()), 
                0, 
                ModelDefinitions.MINIMUM_MAX_SELECTED_MOLECULE_NUMBER_SLICER,
                Double.POSITIVE_INFINITY
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.MAX_SELECTED_MOLECULE_NUMBER_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.MaxSelectedMoleculeNumberSlicer.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.MaxSelectedMoleculeNumberSlicer"));
        tmpValueItem.setValue(String.valueOf(this.maxSelectedMoleculeNumberSlicer));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.depthAttenuationSlicer
     *
     * @return Value item for this.depthAttenuationSlicer
     */
    private ValueItem getDepthAttenuationSlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultDepthAttenuationSlicer()), 2, ModelDefinitions.DEPTH_ATTENUATION_SLICER_MINIMUM,
                ModelDefinitions.DEPTH_ATTENUATION_SLICER_MAXIMUM));
        tmpValueItem.setName(PreferenceEditableEnum.DEPTH_ATTENUATION_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.GraphicsSettings.DepthAttenuation.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.GraphicsSettings.DepthAttenuation"));
        tmpValueItem.setValue(String.valueOf(this.depthAttenuationSlicer));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.getBoxViewDisplay
     *
     * @return Value item for this.getBoxViewDisplay
     */
    private ValueItem getBoxViewDisplayValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(
            new ValueItemDataTypeFormat(
                SimulationBoxViewEnum.XZ_FRONT.toRepresentation(), 
                new String[]
                    {
                        SimulationBoxViewEnum.XZ_FRONT.toRepresentation(),
                        SimulationBoxViewEnum.XZ_BACK.toRepresentation(),
                        SimulationBoxViewEnum.YZ_LEFT.toRepresentation(),
                        SimulationBoxViewEnum.YZ_RIGHT.toRepresentation(),
                        SimulationBoxViewEnum.XY_TOP.toRepresentation(),
                        SimulationBoxViewEnum.XY_BOTTOM.toRepresentation()
                    }
                )
        );
        tmpValueItem.setName(PreferenceEditableEnum.BOX_VIEW_DISPLAY.name());
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.BoxViewDisplay"));
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.BoxViewDisplay.Description"));
        tmpValueItem.setValue(this.boxViewDisplay.toRepresentation());
        return tmpValueItem;
    }

    /**
     * Returns value item for this.isFrameDisplaySlicer
     *
     * @return Value item for this.isFrameDisplaySlicer
     */
    private ValueItem getFrameDisplaySlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameDisplaySlicer.On"), new String[]{
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameDisplaySlicer.On"),
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameDisplaySlicer.Off")}));
        tmpValueItem.setName(PreferenceEditableEnum.IS_FRAME_DISPLAY_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameDisplaySlicer.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameDisplaySlicer"));
        if (this.isFrameDisplaySlicer) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameDisplaySlicer.On"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FrameDisplaySlicer.Off"));
        }
        return tmpValueItem;
    }

    /**
     * Returns value item for this.radialGradientPaintFocusFactorX and
     * this.radialGradientPaintFocusFactorY
     *
     * @return Value item for this.radialGradientPaintFocusFactorX and
     * this.radialGradientPaintFocusFactorY
     */
    private ValueItem getRadialGradienPaintFocusFactorsValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setName(PreferenceEditableEnum.RADIAL_GRADIENT_PAINT_FOCUS_FACTORS.name());
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FocusFactors"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpValueItem.setUpdateNotifier(true);
        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FocusFactors.X"),
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FocusFactors.Y")});
        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_80, // x-focus-factor
            ModelDefinitions.CELL_WIDTH_NUMERIC_80});
        tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
            new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X_DEFAULT), 2, ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X_MINIMUM,
            ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X_MAXIMUM), // x-focus-factor
            new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y_DEFAULT), 2, ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y_MINIMUM,
            ModelDefinitions.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y_MAXIMUM) // y-focus-factor
        });
        tmpValueItem.setValue(String.valueOf(this.getRadialGradientPaintFocusFactorX()), 0, 0);
        tmpValueItem.setValue(String.valueOf(this.getRadialGradientPaintFocusFactorY()), 0, 1);
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.FocusFactors.Description"));
        return tmpValueItem;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- JMol simulation box viewer value item return methods">
    /**
     * Adds value items of editable preferences of JMol simulation box viewer
     * settings to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aNodeNames Node names
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addJmolViewerEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {

        ValueItem tmpValueItem = this.getJmolSimulationBoxBackgroundColorValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJmolShadePowerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJmolAmbientLightPercentageValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJmolDiffuseLightPercentageValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJmolSpecularReflectionExponentValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJmolSpecularReflectionPercentageValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getJmolSpecularReflectionPowerValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.jmolSimulationBoxBackgroundColor
     *
     * @return Value item for this.jmolSimulationBoxBackgroundColor
     */
    private ValueItem getJmolSimulationBoxBackgroundColorValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelDefinitions.DEFAULT_JMOL_SIMULATION_BOX_BACKGROUND_COLOR.toString(), StandardColorEnum.getAllColorRepresentations()));
        tmpValueItem.setName(PreferenceEditableEnum.JMOL_SIMULATION_BOX_BACKGROUND_COLOR.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.JmolSimulationBoxBackgroundColor.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.JmolSimulationBoxBackgroundColor"));
        tmpValueItem.setValue(this.jmolSimulationBoxBackgroundColor);
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jmolShadePower
     *
     * @return Value item for this.jmolShadePower
     */
    private ValueItem getJmolShadePowerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        String[] tmpJmolShadePowerArray = new String[]{"1", "2", "3"};
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultJmolShadePower()), tmpJmolShadePowerArray));
        tmpValueItem.setName(PreferenceEditableEnum.JMOL_SHADE_POWER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolShadePower.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolShadePower"));
        tmpValueItem.setValue(String.valueOf(this.jmolShadePower));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jmolAmbientLightPercentage
     *
     * @return Value item for this.jmolAmbientLightPercentage
     */
    private ValueItem getJmolAmbientLightPercentageValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultJmolAmbientLightPercentage()), 0, ModelDefinitions.MINIMUM_JMOL_AMBIENT_LIGHT_PERCENTAGE,
                ModelDefinitions.MAXIMUM_JMOL_AMBIENT_LIGHT_PERCENTAGE));
        tmpValueItem.setName(PreferenceEditableEnum.JMOL_AMBIENT_LIGHT_PERCENTAGE.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolAmbientLightPercentage.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolAmbientLightPercentage"));
        tmpValueItem.setValue(String.valueOf(this.jmolAmbientLightPercentage));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jmolDiffuseLightPercentage
     *
     * @return Value item for this.jmolDiffuseLightPercentage
     */
    private ValueItem getJmolDiffuseLightPercentageValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultJmolDiffuseLightPercentage()), 0, ModelDefinitions.MINIMUM_JMOL_DIFFUSE_LIGHT_PERCENTAGE,
                ModelDefinitions.MAXIMUM_JMOL_DIFFUSE_LIGHT_PERCENTAGE));
        tmpValueItem.setName(PreferenceEditableEnum.JMOL_DIFFUSE_LIGHT_PERCENTAGE.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolDiffuseLightPercentage.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolDiffuseLightPercentage"));
        tmpValueItem.setValue(String.valueOf(this.jmolDiffuseLightPercentage));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jmolSpecularReflectionExponent
     *
     * @return Value item for this.jmolSpecularReflectionExponent
     */
    private ValueItem getJmolSpecularReflectionExponentValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultJmolSpecularReflectionExponent()), 0, ModelDefinitions.MINIMUM_JMOL_SPECULAR_REFLECTION_EXPONENT,
                ModelDefinitions.MAXIMUM_JMOL_SPECULAR_REFLECTION_EXPONENT));
        tmpValueItem.setName(PreferenceEditableEnum.JMOL_SPECULAR_REFLECTION_EXPONENT.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolSpecularReflectionExponent.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolSpecularReflectionExponent"));
        tmpValueItem.setValue(String.valueOf(this.jmolSpecularReflectionExponent));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jmolSpecularReflectionPercentage
     *
     * @return Value item for this.jmolSpecularReflectionPercentage
     */
    private ValueItem getJmolSpecularReflectionPercentageValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultJmolSpecularReflectionPercentage()), 0, ModelDefinitions.MINIMUM_JMOL_SPECULAR_REFLECTION_PERCENTAGE,
                ModelDefinitions.MAXIMUM_JMOL_SPECULAR_REFLECTION_PERCENTAGE));
        tmpValueItem.setName(PreferenceEditableEnum.JMOL_SPECULAR_REFLECTION_PERCENTAGE.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolSpecularReflectionPercentage.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolSpecularReflectionPercentage"));
        tmpValueItem.setValue(String.valueOf(this.jmolSpecularReflectionPercentage));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.jmolSpecularReflectionPower
     *
     * @return Value item for this.jmolSpecularReflectionPower
     */
    private ValueItem getJmolSpecularReflectionPowerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultJmolSpecularReflectionPower()), 0, ModelDefinitions.MINIMUM_JMOL_SPECULAR_REFLECTION_POWER,
                ModelDefinitions.MAXIMUM_JMOL_SPECULAR_REFLECTION_POWER));
        tmpValueItem.setName(PreferenceEditableEnum.JMOL_SPECULAR_REFLECTION_POWER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolSpecularReflectionPower.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JmolSimulationBoxViewerGraphicsSettings.JmolSpecularReflectionPower"));
        tmpValueItem.setValue(String.valueOf(this.jmolSpecularReflectionPower));
        return tmpValueItem;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Protein viewer value item return methods">
    /**
     * Adds value items of editable preferences of protein viewer settings to
     * value item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addProteinViewerEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition) {

        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.ProteinViewer")};

        ValueItem tmpValueItem = this.getProteinViewerBackgroundColorValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.proteinViewerBackgroundColor
     *
     * @return Value item for this.proteinViewerBackgroundColor
     */
    private ValueItem getProteinViewerBackgroundColorValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelDefinitions.DEFAULT_PROTEIN_VIEWER_BACKGROUND_COLOR.toString(), StandardColorEnum.getAllColorRepresentations()));
        tmpValueItem.setName(PreferenceEditableEnum.PROTEIN_VIEWER_BACKGROUND_COLOR.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ProteinViewerBackgroundColor.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.ProteinViewerBackgroundColor"));
        tmpValueItem.setValue(this.proteinViewerBackgroundColor);
        return tmpValueItem;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Animation related value item return methods">
    /**
     * Adds value items of editable preferences of animation settings to value
     * item container
     *
     * @param aValueItemContainer Value item container
     * @param aNodeNames Node names
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addAnimationEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {
        ValueItem tmpValueItem = this.getAnimationSpeedValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.animationSpeed
     *
     * @return Value item for this.animationSpeed
     */
    private ValueItem getAnimationSpeedValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultAnimationSpeed()), 0, ModelDefinitions.MINIMUM_ANIMATION_SPEED,
                ModelDefinitions.MAXIMUM_ANIMATION_SPEED));
        tmpValueItem.setName(PreferenceEditableEnum.ANIMATION_SPEED.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.AnimationSpeed.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.AnimationSpeed"));
        tmpValueItem.setValue(String.valueOf(this.animationSpeed));
        return tmpValueItem;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Number of steps for Job restart value item return methods">
    /**
     * Adds value item for additional number of steps for Job restart to value 
     * item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addNumberOfAdditionalStepsForJobRestartValueItem(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.JobRestartSettings")};

        ValueItem tmpValueItem = this.getNumberOfAdditionalStepsForJobRestartValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.numberOfStepsForJobRestart
     *
     * @return Value item for this.numberOfStepsForJobRestart
     */
    private ValueItem getNumberOfAdditionalStepsForJobRestartValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultNumberOfAdditionalStepsForJobRestart()), 0,
                ModelDefinitions.MINIMUM_NUMBER_OF_STEPS_FOR_JOB_RESTART, ModelDefinitions.MAXIMUM_NUMBER_OF_ADDITIONAL_STEPS_FOR_JOB_RESTART));
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_STEPS_FOR_JOB_RESTART.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobRestartSettings.NumberOfAdditionalStepsForJobRestart.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobRestartSettings.NumberOfAdditionalStepsForJobRestart"));
        tmpValueItem.setValue(String.valueOf(this.numberOfAdditionalStepsForJobRestart));
        return tmpValueItem;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Job Result archive related value item return methods">
    /**
     * Adds value items of editable preferences of Job Result archive to value
     * item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addJobResultArchiveEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.JobResultArchives")};
        // <editor-fold defaultstate="collapsed" desc="Job Result archive position step file inclusion">
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveStepFileInclusion.True"), new String[]{
            ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveStepFileInclusion.True"),
            ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveStepFileInclusion.False")}));
        tmpValueItem.setName(PreferenceEditableEnum.IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveStepFileInclusion.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveStepFileInclusion"));
        if (this.isJobResultArchiveStepFileInclusion) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveStepFileInclusion.True"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveStepFileInclusion.False"));
        }
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Job Result archive file compression">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveFileCompression.False"), new String[]{
            ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveFileCompression.False"),
            ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveFileCompression.True")}));
        tmpValueItem.setName(PreferenceEditableEnum.IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveFileCompression.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveFileCompression"));
        if (this.isJobResultArchiveFileUncompressed) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveFileCompression.False"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveFileCompression.True"));
        }
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Job Result archive process parallel in background">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveProcessParallelInBackground.True"), new String[]{
            ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveProcessParallelInBackground.True"),
            ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveProcessParallelInBackground.False")}));
        tmpValueItem.setName(PreferenceEditableEnum.IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveProcessParallelInBackground.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveProcessParallelInBackground"));
        if (this.isJobResultArchiveProcessParallelInBackground) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveProcessParallelInBackground.True"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultArchives.IsJobResultArchiveProcessParallelInBackground.False"));
        }
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // </editor-fold>
        return aVerticalPosition;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Job Result settings return methods">
    /**
     * Adds value items of editable preferences of Job Result settings
     * to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addJobResultSettingsEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.JobResultDisplay")};
        // Job Input inclusion
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                ModelMessage.get("Preferences.JobResultDisplay.IsJobInputInclusion.True"), 
                new String[]
                {
                    ModelMessage.get("Preferences.JobResultDisplay.IsJobInputInclusion.True"),
                    ModelMessage.get("Preferences.JobResultDisplay.IsJobInputInclusion.False")
                }
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.IS_JOB_INPUT_INCLUSION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultDisplay.IsJobInputInclusion.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultDisplay.IsJobInputInclusion"));
        if (this.isJobInputInclusion) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultDisplay.IsJobInputInclusion.True"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultDisplay.IsJobInputInclusion.False"));
        }
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // Particle distribution inclusion
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                ModelMessage.get("Preferences.JobResultDisplay.IsParticleDistributionInclusion.True"), 
                new String[]
                {
                    ModelMessage.get("Preferences.JobResultDisplay.IsParticleDistributionInclusion.True"),
                    ModelMessage.get("Preferences.JobResultDisplay.IsParticleDistributionInclusion.False")
                }
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.IS_PARTICLE_DISTRICUTION_INCLUSION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultDisplay.IsParticleDistributionInclusion.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultDisplay.IsParticleDistributionInclusion"));
        if (this.isParticleDistributionInclusion) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultDisplay.IsParticleDistributionInclusion.True"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultDisplay.IsParticleDistributionInclusion.False"));
        }
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // Simulation steps inclusion
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                ModelMessage.get("Preferences.JobResultDisplay.IsSimulationStepInclusion.True"), 
                new String[]
                {
                    ModelMessage.get("Preferences.JobResultDisplay.IsSimulationStepInclusion.True"),
                    ModelMessage.get("Preferences.JobResultDisplay.IsSimulationStepInclusion.False")
                }
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.IS_SIMULATION_STEP_INCLUSION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultDisplay.IsSimulationStepInclusion.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultDisplay.IsSimulationStepInclusion"));
        if (this.isSimulationStepInclusion) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultDisplay.IsSimulationStepInclusion.True"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultDisplay.IsSimulationStepInclusion.False"));
        }
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        // Nearest-neighbour evaluation inclusion
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                ModelMessage.get("Preferences.JobResultDisplay.IsNearestNeighborEvaluationInclusion.True"), 
                new String[]
                {
                    ModelMessage.get("Preferences.JobResultDisplay.IsNearestNeighborEvaluationInclusion.True"),
                    ModelMessage.get("Preferences.JobResultDisplay.IsNearestNeighborEvaluationInclusion.False")
                }
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.JobResultDisplay.IsNearestNeighborEvaluationInclusion.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.JobResultDisplay.IsNearestNeighborEvaluationInclusion"));
        if (this.isNearestNeighborEvaluationInclusion) {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultDisplay.IsNearestNeighborEvaluationInclusion.True"));
        } else {
            tmpValueItem.setValue(ModelMessage.get("Preferences.JobResultDisplay.IsNearestNeighborEvaluationInclusion.False"));
        }
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);
        
        return aVerticalPosition;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Slicer time steps related value item return methods">
    /**
     * Adds value items of editable preferences of slicer time steps to value
     * item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addSlicerTimeStepsEditablePrefencesValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.TimeSteps")};

        ValueItem tmpValueItem = this.getTimeStepDisplaySlicerValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getStepInfoArraySlicerValueItem();
        if (tmpValueItem != null) {
            tmpValueItem.setNodeNames(tmpNodeNames);
            tmpValueItem.setVerticalPosition(aVerticalPosition++);
            aValueItemContainer.addValueItem(tmpValueItem);
        }

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.timeStepDisplaySlicer
     *
     * @return Value item for this.timeStepDisplaySlicer
     */
    private ValueItem getTimeStepDisplaySlicerValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(
                String.valueOf(ModelDefinitions.DEFAULT_TIME_STEP_DISPLAY_SLICER), 0, (double) ModelDefinitions.MINIMUM_TIME_STEP_DISPLAY_SLICER, (double) ModelDefinitions.MAXIMUM_TIME_STEP_DISPLAY_SLICER));
        tmpValueItem.setName(PreferenceEditableEnum.TIME_STEP_DISPLAY_SLICER.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.TimeSteps.Display.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.TimeSteps.Display"));
        tmpValueItem.setValue(String.valueOf(this.timeStepDisplaySlicer));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.stepInfoArray if this.stepInfoArray is
     * defined
     *
     * @return Value item for this.stepInfoArray or null if this.stepInfoArray
     * is not defined
     */
    private ValueItem getStepInfoArraySlicerValueItem() {
        if (this.hasStepInfoArray()) {
            ValueItem tmpValueItem = new ValueItem();
            tmpValueItem.setName(PreferenceEditableEnum.STEP_INFO_ARRAY_SLICER.name());
            tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.TimeSteps.Range"));
            tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpValueItem.setMatrixColumnNames(new String[]{
                ModelMessage.get("Preferences.SimulationBoxSlicerSettings.TimeSteps.Range.FirstTimeStep"),
                ModelMessage.get("Preferences.SimulationBoxSlicerSettings.TimeSteps.Range.LastTimeStep")});
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_250, // First
                ModelDefinitions.CELL_WIDTH_TEXT_250}); // Last
            String tmpCurrentFirstStepInfo;
            if (this.firstStepInfo != null) {
                tmpCurrentFirstStepInfo = this.firstStepInfo;
            } else {
                tmpCurrentFirstStepInfo = this.stepInfoArray[0];
            }
            String tmpCurrentLastStepInfo;
            if (this.lastStepInfo != null) {
                tmpCurrentLastStepInfo = this.lastStepInfo;
            } else {
                tmpCurrentLastStepInfo = this.stepInfoArray[this.stepInfoArray.length - 1];
            }
            tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(tmpCurrentFirstStepInfo, this.stepInfoArray),
                new ValueItemDataTypeFormat(tmpCurrentLastStepInfo, this.stepInfoArray)
            });
            tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.TimeSteps.Range.Description"));
            return tmpValueItem;
        } else {
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Slicer spin steps related value item return methods">
    /**
     * Adds value items of editable preferences of slicer spin steps to value
     * item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addSlicerSpinStepsEditablePrefencesValueItems(ValueItemContainer aValueItemContainer, int aVerticalPosition) {
        String[] tmpNodeNames = new String[]{ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
            ModelMessage.get("Preferences.SimulationBoxSlicerSettings.SpinSteps")};

        ValueItem tmpValueItem = this.getNumberOfSpinStepsValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.numberOfSpinSteps
     *
     * @return Value item for this.numberOfSpinSteps
     */
    private ValueItem getNumberOfSpinStepsValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.DEFAULT_NUMBER_OF_SPIN_STEPS), 0,
                ModelDefinitions.MINIMUM_NUMBER_OF_SPIN_STEPS, ModelDefinitions.MAXIMUM_NUMBER_OF_SPIN_STEPS));
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_SPIN_STEPS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.NumberOfSpinSteps.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.SimulationBoxSlicerSettings.GraphicsSettings.NumberOfSpinSteps"));
        tmpValueItem.setValue(String.valueOf(this.numberOfSpinSteps));
        return tmpValueItem;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Movie related value item return methods">
    /**
     * Adds value items of editable preferences of simulation movie settings 
     * to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aNodeNames Node names
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addSimulationMovieEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {

        ValueItem tmpValueItem = this.getSimulationMovieImagePathValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getMovieQualityValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }
    
    /**
     * Adds value items of editable preferences of chart movie settings 
     * to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aNodeNames Node names
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addChartMovieEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {

        ValueItem tmpValueItem = this.getChartMovieImagePathValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getMovieQualityValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        return aVerticalPosition;
    }

    /**
     * Returns value item for this.simulationMovieImagePath
     *
     * @return Value item for this.simulationMovieImagePath
     */
    private ValueItem getSimulationMovieImagePathValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.DIRECTORY));
        tmpValueItem.setName(PreferenceEditableEnum.SIMULATION_MOVIE_IMAGE_PATH.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.MovieSettings.SimulationMovieImagePath.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.MovieSettings.SimulationMovieImagePath"));
        tmpValueItem.setValue(String.valueOf(this.simulationMovieImagePath));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.chartMovieImagePath
     *
     * @return Value item for this.chartMovieImagePath
     */
    private ValueItem getChartMovieImagePathValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(ValueItemEnumDataType.DIRECTORY));
        tmpValueItem.setName(PreferenceEditableEnum.CHART_MOVIE_IMAGE_PATH.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.MovieSettings.ChartMovieImagePath.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.MovieSettings.ChartMovieImagePath"));
        tmpValueItem.setValue(String.valueOf(this.chartMovieImagePath));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.movieQuality
     *
     * @return Value item for this.movieQuality
     */
    private ValueItem getMovieQualityValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(String.valueOf(this.getDefaultMovieQuality()), ModelUtils.getNumberStringsForInterval(
                ModelDefinitions.MINIMUM_MOVIE_QUALITY, ModelDefinitions.MAXIMUM_MOVIE_QUALITY)));
        tmpValueItem.setName(PreferenceEditableEnum.MOVIE_QUALITY.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.MovieSettings.MovieQuality.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.MovieSettings.MovieQuality"));
        tmpValueItem.setValue(String.valueOf(this.movieQuality));
        return tmpValueItem;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Slicer shifts for simulation box value item return methods">
    /**
     * Adds value items for shifts of simulation box for slicer to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aNodeNames Node names
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addSlicerShiftEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setName(PreferenceEditableEnum.SHIFTS_SLICER.name());
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.ShiftsOfSimulationBoxSlicerSettings.Shifts"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpValueItem.setUpdateNotifier(true);
        tmpValueItem.setMatrixColumnNames(new String[]
            {
                ModelMessage.get("Preferences.ShiftsOfSimulationBoxSlicerSettings.Shifts.AlongXview"),
                ModelMessage.get("Preferences.ShiftsOfSimulationBoxSlicerSettings.Shifts.AlongYview")
            }
        );
        tmpValueItem.setMatrixColumnWidths(
            new String[]
            {
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // AlongXview
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // AlongYview
            }
        );
        tmpValueItem.setDefaultTypeFormats(
            new ValueItemDataTypeFormat[]
            {
                new ValueItemDataTypeFormat(
                    String.valueOf(ModelDefinitions.X_SHIFT_IN_PIXEL_SLICER_DEFAULT), 
                    0, 
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY
                ), // AlongXview
                new ValueItemDataTypeFormat(
                    String.valueOf(ModelDefinitions.Y_SHIFT_IN_PIXEL_SLICER_DEFAULT), 
                    0, 
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY
                ) // AlongYview
            }
        );
        tmpValueItem.setValue(String.valueOf(this.getXshiftInPixelSlicer()), 0, 0);
        tmpValueItem.setValue(String.valueOf(this.getYshiftInPixelSlicer()), 0, 1);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        tmpValueItem.setDescription(ModelMessage.get("Preferences.ShiftsOfSimulationBoxSlicerSettings.Shifts.Description"));
        aValueItemContainer.addValueItem(tmpValueItem);
        return aVerticalPosition;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Slicer rotation value item return methods">
    /**
     * Adds value items for rotation of simulation box for slicer to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aNodeNames Node names
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addSlicerRotationEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setName(PreferenceEditableEnum.ROTATION_ANGLES.name());
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.RotationOfSimulationBoxSlicerSettings.RotationAngles"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpValueItem.setUpdateNotifier(true);
        tmpValueItem.setMatrixColumnNames(new String[] { 
                ModelMessage.get("Preferences.RotationOfSimulationBoxSlicerSettings.RotationAngles.AroundXaxis"),
                ModelMessage.get("Preferences.RotationOfSimulationBoxSlicerSettings.RotationAngles.AroundYaxis"),
                ModelMessage.get("Preferences.RotationOfSimulationBoxSlicerSettings.RotationAngles.AroundZaxis")
            }
        );
        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_120, // x-axis-rotation-angle
            ModelDefinitions.CELL_WIDTH_NUMERIC_120,   // y-axis-rotation-angle
            ModelDefinitions.CELL_WIDTH_NUMERIC_120}); // y-axis-rotation-angle
        tmpValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
            new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.ROTATION_AROUND_X_AXIS_ANGLE_DEFAULT), 0, ModelDefinitions.ROTATION_AROUND_X_AXIS_ANGLE_MINIMUM,
            ModelDefinitions.ROTATION_AROUND_X_AXIS_ANGLE_MAXIMUM), // x-axis-rotation-angle
            new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.ROTATION_AROUND_Y_AXIS_ANGLE_DEFAULT), 0, ModelDefinitions.ROTATION_AROUND_Y_AXIS_ANGLE_MINIMUM,
            ModelDefinitions.ROTATION_AROUND_Y_AXIS_ANGLE_MAXIMUM), // y-axis-rotation-angle
            new ValueItemDataTypeFormat(String.valueOf(ModelDefinitions.ROTATION_AROUND_Z_AXIS_ANGLE_DEFAULT), 0, ModelDefinitions.ROTATION_AROUND_Z_AXIS_ANGLE_MINIMUM,
            ModelDefinitions.ROTATION_AROUND_Z_AXIS_ANGLE_MAXIMUM) // z-axis-rotation-angle
        });
        tmpValueItem.setValue(String.valueOf(this.getRotationAroundXaxisAngle()), 0, 0);
        tmpValueItem.setValue(String.valueOf(this.getRotationAroundYaxisAngle()), 0, 1);
        tmpValueItem.setValue(String.valueOf(this.getRotationAroundZaxisAngle()), 0, 2);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        tmpValueItem.setDescription(ModelMessage.get("Preferences.RotationOfSimulationBoxSlicerSettings.RotationAngles.Description"));
        aValueItemContainer.addValueItem(tmpValueItem);
        return aVerticalPosition;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Particle shift value item return methods">
    /**
     * Adds value items for Particle shift to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aNodeNames Node names
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addParticleShiftEditablePreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setName(PreferenceEditableEnum.PARTICLE_SHIFTS.name());
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.ParticleShift.ShiftInPercent"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpValueItem.setUpdateNotifier(true);
        tmpValueItem.setMatrixColumnNames(new String[] { 
                ModelMessage.get("Preferences.ParticleShift.ShiftInPercent.AlongXaxis"),
                ModelMessage.get("Preferences.ParticleShift.ShiftInPercent.AlongYaxis"),
                ModelMessage.get("Preferences.ParticleShift.ShiftInPercent.AlongZaxis")
            }
        );
        tmpValueItem.setMatrixColumnWidths(
            new String[]{
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // shift_x
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // shift_y
                ModelDefinitions.CELL_WIDTH_NUMERIC_120  // shift_z
            }
        );
        tmpValueItem.setDefaultTypeFormats(
            new ValueItemDataTypeFormat[]{
                new ValueItemDataTypeFormat(
                    String.valueOf(ModelDefinitions.PARTICLE_SHIFT_DEFAULT), 
                    0, 
                    ModelDefinitions.PARTICLE_SHIFT_MINIMUM,
                    ModelDefinitions.PARTICLE_SHIFT_MAXIMUM
                ), // x-axis-rotation-angle
                new ValueItemDataTypeFormat(
                    String.valueOf(ModelDefinitions.PARTICLE_SHIFT_DEFAULT), 
                    0, 
                    ModelDefinitions.PARTICLE_SHIFT_MINIMUM,
                    ModelDefinitions.PARTICLE_SHIFT_MAXIMUM
                ), // y-axis-rotation-angle
                new ValueItemDataTypeFormat(
                    String.valueOf(ModelDefinitions.PARTICLE_SHIFT_DEFAULT), 
                    0, 
                    ModelDefinitions.PARTICLE_SHIFT_MINIMUM,
                    ModelDefinitions.PARTICLE_SHIFT_MAXIMUM
                ) // z-axis-rotation-angle
            }
        );
        tmpValueItem.setValue(String.valueOf(this.getParticleShiftX()), 0, 0);
        tmpValueItem.setValue(String.valueOf(this.getParticleShiftY()), 0, 1);
        tmpValueItem.setValue(String.valueOf(this.getParticleShiftZ()), 0, 2);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        tmpValueItem.setDescription(ModelMessage.get("Preferences.ParticleShift.ShiftInPercent.Description"));
        aValueItemContainer.addValueItem(tmpValueItem);
        return aVerticalPosition;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Parallel computing value item return methods">
    /**
     * Adds value items of editable preferences of parallel computing settings
     * to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     *
     * @return Next vertical position
     */
    private int addParallelComputingEditablePreferencesValueItems(
        ValueItemContainer aValueItemContainer, 
        int aVerticalPosition
    ) 
    {
        String[] tmpNodeNames = 
            new String[] { 
                ModelMessage.get("Preferences.Root"), 
                ModelMessage.get("Preferences.ParallelComputing")
            };

        ValueItem tmpValueItem = this.getNumberOfParallelSimulationsValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getNumberOfParallelCalculatorsValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getNumberOfParallelParticlePositionWritersValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getNumberOfSimulationBoxCellsforParallelizationValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getNumberOfBondsforParallelizationValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);

        tmpValueItem = this.getNumberOfParallelSlicersValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);
        
        return aVerticalPosition;
    }

    /**
     * Returns value item for this.numberOfParallelSimulations
     *
     * @return Value item for this.numberOfParallelSimulations
     */
    private ValueItem getNumberOfParallelSimulationsValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        if (!this.isJobWorking()) {
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfParallelSimulations()), 
                    0,
                    ModelDefinitions.MINIMUM_NUMBER_OF_PROCESSOR_CORES,
                    Double.POSITIVE_INFINITY
                )
            );
        } else {
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfParallelSimulations()), 
                    0,
                    ModelDefinitions.MINIMUM_NUMBER_OF_PROCESSOR_CORES,
                    Double.POSITIVE_INFINITY,
                    false,
                    false
                )
            );
        }
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_PARALLEL_SIMULATIONS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.ParallelComputing.NumberOfParallelSimulations.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.ParallelComputing.NumberOfParallelSimulations"));
        tmpValueItem.setValue(String.valueOf(this.numberOfParallelSimulations));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.numberOfParallelSlicers
     *
     * @return Value item for this.numberOfParallelSlicers
     */
    private ValueItem getNumberOfParallelSlicersValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        // NOTE: Boundaries are identical to this.numberOfParallelSimulations
        tmpValueItem.setDefaultTypeFormat(
            new ValueItemDataTypeFormat(
                String.valueOf(this.getDefaultNumberOfParallelSlicers()),
                0, 
                ModelDefinitions.MINIMUM_NUMBER_OF_PARALLEL_SLICERS,
                Double.POSITIVE_INFINITY
            )
        );
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_PARALLEL_SLICERS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.ParallelComputing.NumberOfParallelSlicers.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.ParallelComputing.NumberOfParallelSlicers"));
        tmpValueItem.setValue(String.valueOf(this.numberOfParallelSlicers));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.numberOfParallelCalculators
     *
     * @return Value item for this.numberOfParallelCalculators
     */
    private ValueItem getNumberOfParallelCalculatorsValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        if (!this.isJobWorking()) {
            // NOTE: Boundaries are identical to this.numberOfParallelSimulations
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfParallelCalculators()), 
                    0,
                    ModelDefinitions.MINIMUM_NUMBER_OF_PROCESSOR_CORES,
                    Double.POSITIVE_INFINITY
                )
            );
        } else {
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfParallelCalculators()), 
                    0,
                    ModelDefinitions.MINIMUM_NUMBER_OF_PROCESSOR_CORES,
                    Double.POSITIVE_INFINITY,
                    false,
                    false
                )
            );
        }
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_PARALLEL_CALCULATORS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.ParallelComputing.NumberOfParallelCalculators.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.ParallelComputing.NumberOfParallelCalculators"));
        tmpValueItem.setValue(String.valueOf(this.numberOfParallelCalculators));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.numberOfParallelParticlePositionWriters
     *
     * @return Value item for this.numberOfParallelParticlePositionWriters
     */
    private ValueItem getNumberOfParallelParticlePositionWritersValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        if (!this.isJobWorking()) {
            // NOTE: Boundaries are identical to this.numberOfParallelSimulations
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfParallelParticlePositionWriters()), 
                    0,
                    ModelDefinitions.MINIMUM_NUMBER_OF_PROCESSOR_CORES,    
                    Double.POSITIVE_INFINITY
                )
            );
        } else {
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfParallelParticlePositionWriters()), 
                    0,
                    ModelDefinitions.MINIMUM_NUMBER_OF_PROCESSOR_CORES,    
                    Double.POSITIVE_INFINITY,
                    false,
                    false
                )
            );
        }
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_PARALLEL_PARTICLE_POSITION_WRITERS.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.ParallelComputing.NumberOfParallelParticlePositionWriters.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.ParallelComputing.NumberOfParallelParticlePositionWriters"));
        tmpValueItem.setValue(String.valueOf(this.numberOfParallelParticlePositionWriters));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.numberOfSimulationBoxCellsforParallelization
     *
     * @return Value item for this.numberOfSimulationBoxCellsforParallelization
     */
    private ValueItem getNumberOfSimulationBoxCellsforParallelizationValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        if (!this.isJobWorking()) {
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfSimulationBoxCellsforParallelization()), 
                    0, 
                    ModelDefinitions.MINIMUM_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION,
                    ModelDefinitions.MAXIMUM_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION
                )
            );
        } else {
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfSimulationBoxCellsforParallelization()), 
                    0, 
                    ModelDefinitions.MINIMUM_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION,
                    ModelDefinitions.MAXIMUM_NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION,
                    false,
                    false
                )
            );
        }
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.ParallelComputing.NumberOfSimulationBoxCellsforParallelization.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.ParallelComputing.NumberOfSimulationBoxCellsforParallelization"));
        tmpValueItem.setValue(String.valueOf(this.numberOfSimulationBoxCellsforParallelization));
        return tmpValueItem;
    }

    /**
     * Returns value item for this.numberOfBondsforParallelization
     *
     * @return Value item for this.numberOfBondsforParallelization
     */
    private ValueItem getNumberOfBondsforParallelizationValueItem() {
        ValueItem tmpValueItem = new ValueItem();
        if (!this.isJobWorking()) {
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfBondsforParallelization()), 
                    0, 
                    ModelDefinitions.MINIMUM_NUMBER_OF_BONDS_FOR_PARALLELIZATION,
                    ModelDefinitions.MAXIMUM_NUMBER_OF_BONDS_FOR_PARALLELIZATION
                )
            );
        } else {
            tmpValueItem.setDefaultTypeFormat(
                new ValueItemDataTypeFormat(
                    String.valueOf(this.getDefaultNumberOfBondsforParallelization()), 
                    0, 
                    ModelDefinitions.MINIMUM_NUMBER_OF_BONDS_FOR_PARALLELIZATION,
                    ModelDefinitions.MAXIMUM_NUMBER_OF_BONDS_FOR_PARALLELIZATION,
                    false,
                    false
                )
            );
        }
        tmpValueItem.setName(PreferenceEditableEnum.NUMBER_OF_BONDS_FOR_PARALLELIZATION.name());
        tmpValueItem.setDescription(ModelMessage.get("Preferences.ParallelComputing.NumberOfBondsforParallelization.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.ParallelComputing.NumberOfBondsforParallelization"));
        tmpValueItem.setValue(String.valueOf(this.numberOfBondsforParallelization));
        return tmpValueItem;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Miscellaneous methods">
    /**
     * Adds value item for custom dialog size preferences to value item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     * @param aNodeNames Array with node names
     *
     * @return Next vertical position
     */
    private int addCustomDialogSizePreferencesValueItem(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setName(PreferenceEditableEnum.CUSTOM_DIALOG_SIZE.name());
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.CustomDialogSize"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
        tmpValueItem.setMatrixColumnNames(new String[]
            {
                ModelMessage.get("Preferences.CustomDialogSize.Width"),
                ModelMessage.get("Preferences.CustomDialogSize.Height")
            }
        );
        tmpValueItem.setMatrixColumnWidths(
            new String[]
            {
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // Width
                ModelDefinitions.CELL_WIDTH_NUMERIC_120, // Height
            }
        );
        tmpValueItem.setDefaultTypeFormats(
            new ValueItemDataTypeFormat[]
            {
                new ValueItemDataTypeFormat(
                    String.valueOf(ModelDefinitions.MINIMUM_DIALOG_WIDTH), 
                    0, 
                    (double) ModelDefinitions.MINIMUM_DIALOG_WIDTH,
                    Toolkit.getDefaultToolkit().getScreenSize().getWidth()
                ), // Width
                new ValueItemDataTypeFormat(
                    String.valueOf(ModelDefinitions.MINIMUM_DIALOG_HEIGHT), 
                    0, 
                    ModelDefinitions.MINIMUM_DIALOG_HEIGHT,
                    Toolkit.getDefaultToolkit().getScreenSize().getHeight()
                ) // Height
            }
        );
        tmpValueItem.setValue(String.valueOf(this.getCustomDialogWidth()), 0, 0);
        tmpValueItem.setValue(String.valueOf(this.getCustomDialogHeight()), 0, 1);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        tmpValueItem.setDescription(ModelMessage.get("Preferences.CustomDialogSize.Description"));
        aValueItemContainer.addValueItem(tmpValueItem);
        return aVerticalPosition;
    }

    /**
     * Adds value items of editable preferences of particle color display to
     * value item container
     *
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition First vertical position
     * @param aNodeNames Array with node names
     *
     * @return Next vertical position
     */
    private int addParticleColorDisplayPreferencesValueItems(ValueItemContainer aValueItemContainer, String[] aNodeNames, int aVerticalPosition) {
        ValueItem tmpValueItem = this.getParticleColorDisplayModeValueItem();
        tmpValueItem.setNodeNames(aNodeNames);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);
        return aVerticalPosition;
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Initialisation related methods">
    /**
     * Set initial default values
     */
    private void setInitialDefaultValues() {
        // NOTE: Order corresponds loosely to order in readPersistenceXmlInformation(): Do NOT change!
        // <editor-fold defaultstate="collapsed" desc="Non-persistent values">
        this.simulationMovieSlicerConfiguration = new MovieSlicerConfiguration();
        this.timeStepSimulationBoxChangeInfo = new SimulationBoxChangeInfo();
        this.stepInfoArray = null;
        this.firstStepInfo = null;
        this.lastStepInfo = null;
        this.isLogEvent = false;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.internalMFsimJobPath">
        this.internalMFsimJobPath = this.getDefaultInternalMFsimJobPath();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.internalTempPath">
        this.internalTempPath = this.getDefaultInternalTempPath();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.currentParticleSetFilename">
        this.currentParticleSetFilename = "";
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.simulationMovieImagePath">
        this.simulationMovieImagePath = "";
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.chartMovieImagePath">
        this.chartMovieImagePath = "";
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.delayForFilesInMilliseconds">
        this.delayForFilesInMilliseconds = this.getDefaultDelayForFilesInMilliseconds();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.delayForJobStartInMilliseconds">
        this.delayForJobStartInMilliseconds = this.getDefaultDelayForJobStartInMilliseconds();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfSlicesPerView">
        this.numberOfSlicesPerView = this.getDefaultNumberOfSlicesPerView();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.jmolShadePower">
        this.jmolShadePower = this.getDefaultJmolShadePower();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.jmolAmbientLightPercentage">
        this.jmolAmbientLightPercentage = this.getDefaultJmolAmbientLightPercentage();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.jmolDiffuseLightPercentage">
        this.jmolDiffuseLightPercentage = this.getDefaultJmolDiffuseLightPercentage();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.jmolSpecularReflectionExponent">
        this.jmolSpecularReflectionExponent = this.getDefaultJmolSpecularReflectionExponent();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.jmolSpecularReflectionPercentage">
        this.jmolSpecularReflectionPercentage = this.getDefaultJmolSpecularReflectionPercentage();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.jmolSpecularReflectionPower">
        this.jmolSpecularReflectionPower = this.getDefaultJmolSpecularReflectionPower();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.firstSliceIndex">
        this.firstSliceIndex = this.getDefaultFirstSliceIndex();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfBoxWaitSteps">
        this.numberOfBoxWaitSteps = this.getDefaultNumberOfBoxWaitSteps();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfFramePointsSlicer">
        this.numberOfFramePointsSlicer = this.getDefaultNumberOfFramePointsSlicer();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.timeStepDisplaySlicer">
        this.timeStepDisplaySlicer = this.getDefaultTimeStepDisplaySlicer();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfParallelSimulations">
        this.numberOfParallelSimulations = this.getDefaultNumberOfParallelSimulations();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfParallelSlicers">
        this.numberOfParallelSlicers = this.getDefaultNumberOfParallelSlicers();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfParallelCalculators">
        this.numberOfParallelCalculators = this.getDefaultNumberOfParallelCalculators();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfParallelParticlePositionWriters">
        this.numberOfParallelParticlePositionWriters = this.getDefaultNumberOfParallelParticlePositionWriters();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfAfterDecimalDigitsForParticlePositions">
        this.numberOfAfterDecimalDigitsForParticlePositions = this.getDefaultNumberOfAfterDecimalDigitsForParticlePositions();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.maximumNumberOfPositionCorrectionTrials">
        this.maximumNumberOfPositionCorrectionTrials = this.getDefaultMaximumNumberOfPositionCorrectionTrials();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.movieQuality">
        this.movieQuality = this.getDefaultMovieQuality();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.timerIntervalInMilliseconds">
        this.timerIntervalInMilliseconds = this.getDefaultTimerIntervalInMilliseconds();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.minimumBondLengthDpd">
        this.minimumBondLengthDpd = this.getDefaultMinimumBondLengthDpd();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.maximumNumberOfParticlesForGraphicalDisplay">
        this.maximumNumberOfParticlesForGraphicalDisplay = this.getDefaultMaximumNumberOfParticlesForGraphicalDisplay();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfStepsForRdfCalculation">
        this.numberOfStepsForRdfCalculation = this.getDefaultNumberOfStepsForRdfCalculation();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfVolumeBins">
        this.numberOfVolumeBins = this.getDefaultNumberOfVolumeBins();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfTrialsForCompartment">
        this.numberOfTrialsForCompartment = this.getDefaultNumberOfTrialsForCompartment();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.animationSpeed">
        this.animationSpeed = this.getDefaultAnimationSpeed();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfSimulationBoxCellsforParallelization">
        this.numberOfSimulationBoxCellsforParallelization = this.getDefaultNumberOfSimulationBoxCellsforParallelization();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfBondsforParallelization">
        this.numberOfBondsforParallelization = this.getDefaultNumberOfBondsforParallelization();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfStepsForJobRestart">
        this.numberOfAdditionalStepsForJobRestart = this.getDefaultNumberOfAdditionalStepsForJobRestart();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.simulationBoxMagnificationPercentage">
        this.simulationBoxMagnificationPercentage = this.getDefaultSimulationBoxMagnificationPercentage();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfSpinSteps">
        this.numberOfSpinSteps = this.getDefaultNumberOfSpinSteps();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.lastSelectedPath">
        this.lastSelectedPath = this.getDefaultLastSelectedPath();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.mainFrameHeight">
        this.mainFrameHeight = this.getDefaultMainFrameHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.mainFrameWidth">
        this.mainFrameWidth = this.getDefaultMainFrameWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogSlicerShowHeight">
        this.dialogSlicerShowHeight = this.getDefaultDialogSlicerShowHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogSlicerShowWidth">
        this.dialogSlicerShowWidth = this.getDefaultDialogSlicerShowWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.customDialogHeight">
        this.customDialogHeight = this.getDefaultCustomDialogHeight();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.customDialogWidth">
        this.customDialogWidth = this.getDefaultCustomDialogWidth();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogSingleSlicerShowHeight">
        this.dialogSingleSlicerShowHeight = this.getDefaultDialogSingleSlicerShowHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogSingleSlicerShowWidth">
        this.dialogSingleSlicerShowWidth = this.getDefaultDialogSingleSlicerShowWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogSimulationMovieSlicerShowHeight">
        this.dialogSimulationMovieSlicerShowHeight = this.getDefaultDialogSimulationMovieSlicerShowHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogSimulationMovieSlicerShowWidth">
        this.dialogSimulationMovieSlicerShowWidth = this.getDefaultDialogSimulationMovieSlicerShowWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemEditHeight">
        this.dialogValueItemEditHeight = this.getDefaultDialogValueItemEditHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemEditWidth">
        this.dialogValueItemEditWidth = this.getDefaultDialogValueItemEditWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogTextEditHeight">
        this.dialogTextEditHeight = this.getDefaultDialogTextEditHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogTextEditWidth">
        this.dialogTextEditWidth = this.getDefaultDialogTextEditWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemShowHeight">
        this.dialogValueItemShowHeight = this.getDefaultDialogValueItemShowHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemShowWidth">
        this.dialogValueItemShowWidth = this.getDefaultDialogValueItemShowWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogTableDataSchemataManageHeight">
        this.dialogTableDataSchemataManageHeight = this.getDefaultDialogTableDataSchemataManageHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogTableDataSchemataManageWidth">
        this.dialogTableDataSchemataManageWidth = this.getDefaultDialogTableDataSchemataManageWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemMatrixDiagramHeight">
        this.dialogValueItemMatrixDiagramHeight = this.getDefaultDialogValueItemMatrixDiagramHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemMatrixDiagramWidth">
        this.dialogValueItemMatrixDiagramWidth = this.getDefaultDialogValueItemMatrixDiagramWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogStructureEditHeight">
        this.dialogStructureEditHeight = this.getDefaultDialogStructureEditHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogStructureEditWidth">
        this.dialogStructureEditWidth = this.getDefaultDialogStructureEditWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogPeptideEditHeight">
        this.dialogPeptideEditHeight = this.getDefaultDialogPeptideEditHeight();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogPeptideEditWidth">
        this.dialogPeptideEditWidth = this.getDefaultDialogPeptideEditWidth();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogCompartmentEditHeight">
        this.dialogCompartmentEditHeight = this.getDefaultDialogCompartmentEditHeight();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.dialogCompartmentEditWidth">
        this.dialogCompartmentEditWidth = this.getDefaultDialogCompartmentEditWidth();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isParticleUpdateForJobInput">
        this.isParticleUpdateForJobInput = this.getDefaultParticleUpdateForJobInput();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="isJobResultArchiveStepFileInclusion">
        this.isJobResultArchiveStepFileInclusion = this.getDefaultJobResultArchiveStepFileInclusion();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isVolumeScalingForConcentrationCalculation">
        this.isVolumeScalingForConcentrationCalculation = this.getDefaultVolumeScalingForConcentrationCalculation();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isJobInputInclusion">
        this.isJobInputInclusion = this.getDefaultJobInputInclusion();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isParticleDistributionInclusion">
        this.isParticleDistributionInclusion = this.getDefaultParticleDistributionInclusion();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isSimulationStepInclusion">
        this.isSimulationStepInclusion = this.getDefaultSimulationStepInclusion();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isNearestNeighborEvaluationInclusion">
        this.isNearestNeighborEvaluationInclusion = this.getDefaultNearestNeighborEvaluationInclusion();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="isJobResultArchiveProcessParallelInBackground">
        this.isJobResultArchiveProcessParallelInBackground = this.getDefaultJobResultArchiveProcessParallelInBackground();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="isJobResultArchiveFileUncompressed">
        this.isJobResultArchiveFileUncompressed = this.getDefaultJobResultArchiveFileUncompressed();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isJdpdLogLevelExceptions">
        this.isJdpdLogLevelExceptions = this.getDefaultJdpdLogLevelExceptions();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isConstantCompartmentBodyVolume">
        this.isConstantCompartmentBodyVolume = this.getDefaultConstantCompartmentBodyVolumeFlag();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isSimulationBoxSlicer">
        this.isSimulationBoxSlicer = this.isDefaultSimulationBoxSlicer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isSingleSliceDisplay">
        this.isSingleSliceDisplay = this.getDefaultSingleSliceDisplay();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.slicerGraphicsMode">
        this.slicerGraphicsMode = this.getDefaultSlicerGraphicsMode();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.simulationBoxBackgroundColorSlicer">
        this.simulationBoxBackgroundColorSlicer = ModelDefinitions.DEFAULT_SIMULATION_BOX_BACKGROUND_COLOR_SLICER.toString();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.measurementColorSlicer">
        this.measurementColorSlicer = ModelDefinitions.DEFAULT_MEASUREMENT_COLOR_SLICER.toString();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.moleculeSelectionColorSlicer">
        this.moleculeSelectionColorSlicer = ModelDefinitions.DEFAULT_MOLECULE_SELECTION_COLOR_SLICER.toString();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.frameColorSlicer">
        this.frameColorSlicer = ModelDefinitions.DEFAULT_FRAME_COLOR_SLICER.toString();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.jmolSimulationBoxBackgroundColor">
        this.jmolSimulationBoxBackgroundColor = ModelDefinitions.DEFAULT_JMOL_SIMULATION_BOX_BACKGROUND_COLOR.toString();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.proteinViewerBackgroundColor">
        this.proteinViewerBackgroundColor = ModelDefinitions.DEFAULT_PROTEIN_VIEWER_BACKGROUND_COLOR.toString();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.imageStorageMode">
        this.imageStorageMode = this.getDefaultImageStorageMode();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.particleColorDisplayMode">
        this.particleColorDisplayMode = this.getDefaultParticleColorDisplayMode();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.getBoxViewDisplay">
        this.boxViewDisplay = this.getDefaultBoxViewDisplay();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isFrameDisplaySlicer">
        this.isFrameDisplaySlicer = this.getDefaultFrameDisplaySlicer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.specularWhiteAttenuationSlicer">
        this.specularWhiteAttenuationSlicer = this.getDefaultSpecularWhiteAttenuationSlicer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.depthAttenuationCompartment">
        this.depthAttenuationCompartment = this.getDefaultDepthAttenuationCompartment();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.compartmentBodyChangeResponseFactor">
        this.compartmentBodyChangeResponseFactor = this.getDefaultCompartmentBodyChangeResponseFactor();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.maxSelectedMoleculeNumberSlicer">
        this.maxSelectedMoleculeNumberSlicer = this.getDefaultMaxSelectedMoleculeNumberSlicer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.depthAttenuationSlicer">
        this.depthAttenuationSlicer = this.getDefaultDepthAttenuationSlicer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.colorGradientAttenuationCompartment">
        this.colorGradientAttenuationCompartment = this.getDefaultColorGradientAttenuationCompartment();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.colorGradientAttenuationSlicer">
        this.colorGradientAttenuationSlicer = this.getDefaultColorGradientAttenuationSlicer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.jpegImageQuality">
        this.jpegImageQuality = this.getDefaultJpegImageQuality();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.colorTransparencyCompartment">
        this.colorTransparencyCompartment = this.getDefaultColorTransparencyCompartment();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.xShiftInPixelSlicer">
        this.xShiftInPixelSlicer = this.getDefaultXshiftInPixelSlicer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.yShiftInPixelSlicer">
        this.yShiftInPixelSlicer = this.getDefaultYshiftInPixelSlicer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.rotationAroundXaxisAngle">
        this.rotationAroundXaxisAngle = this.getDefaultRotationAroundXaxisAngle();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.rotationAroundYaxisAngle">
        this.rotationAroundYaxisAngle = this.getDefaultRotationAroundYaxisAngle();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.rotationAroundZaxisAngle">
        this.rotationAroundZaxisAngle = this.getDefaultRotationAroundZaxisAngle();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.particleShiftX">
        this.particleShiftX = this.getDefaultParticleShiftX();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.particleShiftY">
        this.particleShiftY = this.getDefaultParticleShiftY();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.particleShiftZ">
        this.particleShiftZ = this.getDefaultParticleShiftZ();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.radialGradientPaintRadiusMagnification">
        this.radialGradientPaintRadiusMagnification = this.getDefaultRadialGradientPaintRadiusMagnification();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.specularWhiteSizeSlicer">
        this.specularWhiteSizeSlicer = this.getDefaultSpecularWhiteSizeSlicer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.radialGradientPaintFocusFactorX">
        this.radialGradientPaintFocusFactorX = this.getDefaultRadialGradientPaintFocusFactorX();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.radialGradientPaintFocusFactorY">
        this.radialGradientPaintFocusFactorY = this.getDefaultRadialGradientPaintFocusFactorY();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.mainFrameMaximumHeight">
        this.mainFrameMaximumHeight = this.getDefaultMainFrameMaximumHeight();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.mainFrameMaximumWidth">
        this.mainFrameMaximumWidth = this.getDefaultMainFrameMaximumWidth();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.schemaValueItemContainer">
        this.schemaValueItemContainer = new ValueItemContainer(null);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Job input filter">
        // <editor-fold defaultstate="collapsed" desc="- this.jobInputFilterAfterTimestamp">
        this.jobInputFilterAfterTimestamp = this.getDefaultJobInputFilterAfterTimestamp();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- this.jobInputFilterBeforeTimestamp">
        this.jobInputFilterBeforeTimestamp = this.getDefaultJobInputFilterBeforeTimestamp();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- this.jobInputFilterContainsPhrase">
        this.jobInputFilterContainsPhrase = this.getDefaultJobInputFilterContainsPhrase();

        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Job result filter">
        // <editor-fold defaultstate="collapsed" desc="- this.jobResultFilterAfterTimestamp">
        this.jobResultFilterAfterTimestamp = this.getDefaultJobResultFilterAfterTimestamp();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- this.jobResultFilterBeforeTimestamp">
        this.jobResultFilterBeforeTimestamp = this.getDefaultJobResultFilterBeforeTimestamp();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- this.jobResultFilterContainsPhrase">
        this.jobResultFilterContainsPhrase = this.getDefaultJobResultFilterContainsPhrase();

        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.previousMonomers">
        this.previousMonomers = new LinkedList<String>();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.previousStructures">
        this.previousStructures = new LinkedList<String>();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.previousPeptides">
        this.previousPeptides = new LinkedList<String>();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.numberOfWorkingJobResultExecutionTasks">
        this.numberOfWorkingJobResultExecutionTasks = 0;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="this.isBoxVolumeShapeForZoom">
        this.isBoxVolumeShapeForZoom = true;
        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Persistence XML related methods">
    /**
     * Reads XML information for this instance
     */
    private void readPersistenceXmlInformation() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!(new File(this.persistenceFilePathname)).isFile()) {
            return;
        }

        // </editor-fold>
        try {
            String tmpXmlString = this.fileUtilityMethods.readTextFileIntoSingleString(this.persistenceFilePathname);
            if (tmpXmlString == null) {
                return;
            }
            Element tmpRoot = new SAXBuilder().build(new StringReader(tmpXmlString)).getRootElement();
            String tmpRootName = tmpRoot.getName();
            if (!tmpRootName.equals(PreferenceXmlName.BASIC_PREFERENCES)) {
                return;
            }
            if (tmpRoot.getChild(PreferenceXmlName.VERSION) == null) {
                return;
            }
            String tmpVersion = tmpRoot.getChild(PreferenceXmlName.VERSION).getText();

            // <editor-fold defaultstate="collapsed" desc="Read version 1.0.0">
            if (tmpVersion.equals("Version 1.0.0")) {
                this.readPersistenceXmlInformationV_1_0_0(tmpRoot);
            }

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return;
        }
    }

    /**
     * Reads versioned XML information for this instance
     *
     * @param anElement XML element
     */
    private void readPersistenceXmlInformationV_1_0_0(Element anElement) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anElement == null) {
            return;
        }

        // </editor-fold>
        try {
            // NOTE: This order loosely corresponds to order in method initialize() - do NOT change!
            // <editor-fold defaultstate="collapsed" desc="Persistent XML items">
            Element tmpCurrentElement;
            // <editor-fold defaultstate="collapsed" desc="this.internalMFsimJobPath">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.INTERNAL_MFSIM_JOB_PATH);
            if (tmpCurrentElement != null) {
                this.internalMFsimJobPath = tmpCurrentElement.getText();
                if (!this.internalMFsimJobPath.isEmpty()) {
                    try {
                        // this.internalMFsimJobPath MUST contain ModelDefinitions.JOB_INPUT_DIRECTORY AND ModelDefinitions.JOB_RESULT_DIRECTORY
                        if (!new File(this.internalMFsimJobPath).isDirectory()
                                || !new File(this.internalMFsimJobPath + File.separatorChar + ModelDefinitions.JOB_INPUT_DIRECTORY).isDirectory()
                                || !new File(this.internalMFsimJobPath + File.separatorChar + ModelDefinitions.JOB_RESULT_DIRECTORY).isDirectory()) {
                            this.internalMFsimJobPath = "";
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        this.internalMFsimJobPath = "";
                    }
                }
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.internalTempPath">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.INTERNAL_TEMP_PATH);
            if (tmpCurrentElement != null) {
                this.internalTempPath = tmpCurrentElement.getText();
                if (!this.internalTempPath.isEmpty()) {
                    try {
                        if (!new File(this.internalTempPath).isDirectory()) {
                            if (!ModelUtils.createDirectory(this.internalTempPath)) {
                                // <editor-fold defaultstate="collapsed" desc="Error message">
                                JOptionPane.showMessageDialog(null, String.format(ModelMessage.get("Error.NoInternalTempDirectoryCreation"), this.internalTempPath),
                                        ModelMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                                // </editor-fold>
                                this.internalTempPath = "";
                            }
                        } else {
                            // Clear internal temporary directory
                            // NOTE: Clear-operation may take seconds up to minutes, so it is performed with separated thread
                            new FileDeletionTask((new File(this.internalTempPath)).listFiles()).start();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        this.internalTempPath = "";
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.currentParticleSetFilename">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.CURRENT_PARTICLE_SET_FILENAME);
            if (tmpCurrentElement != null) {
                this.currentParticleSetFilename = tmpCurrentElement.getText();
                // NOTE: Do NOT check if current particle set file is available at this point!
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.simulationMovieImagePath">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.SIMULATION_MOVIE_IMAGE_PATH);
            if (tmpCurrentElement != null) {
                this.simulationMovieImagePath = tmpCurrentElement.getText();
                if (!this.simulationMovieImagePath.isEmpty() && !(new File(this.simulationMovieImagePath).isDirectory())) {
                    this.simulationMovieImagePath = "";
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.chartMovieImagePath">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.CHART_MOVIE_IMAGE_PATH);
            if (tmpCurrentElement != null) {
                this.chartMovieImagePath = tmpCurrentElement.getText();
                if (!this.chartMovieImagePath.isEmpty() && !(new File(this.chartMovieImagePath).isDirectory())) {
                    this.chartMovieImagePath = "";
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.delayForFilesInMilliseconds">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DELAY_FOR_FILES_IN_MILLISECONDS);
            if (tmpCurrentElement != null) {
                this.delayForFilesInMilliseconds = Long.parseLong(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.delayForJobStartInMilliseconds">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DELAY_FOR_JOB_START_IN_MILLISECONDS);
            if (tmpCurrentElement != null) {
                this.delayForJobStartInMilliseconds = Long.parseLong(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfSlicesPerView">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_SLICES);
            if (tmpCurrentElement != null) {
                this.numberOfSlicesPerView = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.jmolShadePower">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JMOL_SHADE_POWER);
            if (tmpCurrentElement != null) {
                this.jmolShadePower = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.jmolAmbientLightPercentage">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JMOL_AMBIENT_LIGHT_PERCENTAGE);
            if (tmpCurrentElement != null) {
                this.jmolAmbientLightPercentage = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.jmolDiffuseLightPercentage">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JMOL_DIFFUSE_LIGHT_PERCENTAGE);
            if (tmpCurrentElement != null) {
                this.jmolDiffuseLightPercentage = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.jmolSpecularReflectionExponent">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JMOL_SPECULAR_REFLECTION_EXPONENT);
            if (tmpCurrentElement != null) {
                this.jmolSpecularReflectionExponent = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.jmolSpecularReflectionPercentage">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JMOL_SPECULAR_REFLECTION_PERCENTAGE);
            if (tmpCurrentElement != null) {
                this.jmolSpecularReflectionPercentage = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.jmolSpecularReflectionPower">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JMOL_SPECULAR_REFLECTION_POWER);
            if (tmpCurrentElement != null) {
                this.jmolSpecularReflectionPower = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.firstSliceIndex">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.FIRST_SLICE_INDEX);
            if (tmpCurrentElement != null) {
                this.firstSliceIndex = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfBoxWaitSteps">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_BOX_WAIT_STEPS);
            if (tmpCurrentElement != null) {
                this.numberOfBoxWaitSteps = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfFramePointsSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_FRAME_POINTS_SLICER);
            if (tmpCurrentElement != null) {
                this.numberOfFramePointsSlicer = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.timeStepDisplaySlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.TIME_STEP_DISPLAY_SLICER);
            if (tmpCurrentElement != null) {
                this.timeStepDisplaySlicer = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfParallelSimulations">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_PARALLEL_SIMULATIONS);
            if (tmpCurrentElement != null) {
                this.numberOfParallelSimulations = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfParallelSlicers">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_PARALLEL_SLICERS);
            if (tmpCurrentElement != null) {
                this.numberOfParallelSlicers = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfParallelCalculators">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_PARALLEL_CALCULATORS);
            if (tmpCurrentElement != null) {
                this.numberOfParallelCalculators = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfParallelParticlePositionWriters">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_PARALLEL_PARTICLE_POSITION_WRITERS);
            if (tmpCurrentElement != null) {
                this.numberOfParallelParticlePositionWriters = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfAfterDecimalDigitsForParticlePositions">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS);
            if (tmpCurrentElement != null) {
                this.numberOfAfterDecimalDigitsForParticlePositions = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.maximumNumberOfPositionCorrectionTrials">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS);
            if (tmpCurrentElement != null) {
                this.maximumNumberOfPositionCorrectionTrials = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.movieQuality">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.MOVIE_QUALITY);
            if (tmpCurrentElement != null) {
                this.movieQuality = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.timerIntervalInMilliseconds">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.TIMER_INTERVAL_IN_MILLISECONDS);
            if (tmpCurrentElement != null) {
                this.timerIntervalInMilliseconds = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.minimumBondLengthDpd">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.MINIMUM_BOND_LENGTH_DPD);
            if (tmpCurrentElement != null) {
                this.minimumBondLengthDpd = Double.parseDouble(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.maximumNumberOfParticlesForGraphicalDisplay">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.MAXIMUM_NUMBER_OF_PARTICLES_FOR_GRAPHICAL_DISPLAY);
            if (tmpCurrentElement != null) {
                this.maximumNumberOfParticlesForGraphicalDisplay = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfStepsForRdfCalculation">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_STEPS_FOR_RDF_CALCULATION);
            if (tmpCurrentElement != null) {
                this.numberOfStepsForRdfCalculation = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfVolumeBins">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_ZOOM_VOLUME_BINS);
            if (tmpCurrentElement != null) {
                this.numberOfVolumeBins = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfTrialsForCompartment">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_TRIALS_FOR_COMPARTMENT);
            if (tmpCurrentElement != null) {
                this.numberOfTrialsForCompartment = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.animationSpeed">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.ANIMATION_SPEED);
            if (tmpCurrentElement != null) {
                this.animationSpeed = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfSimulationBoxCellsforParallelization">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION);
            if (tmpCurrentElement != null) {
                this.numberOfSimulationBoxCellsforParallelization = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfBondsforParallelization">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_BONDS_FOR_PARALLELIZATION);
            if (tmpCurrentElement != null) {
                this.numberOfBondsforParallelization = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfStepsForJobRestart">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_STEPS_FOR_JOB_RESTART);
            if (tmpCurrentElement != null) {
                this.numberOfAdditionalStepsForJobRestart = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.simulationBoxMagnificationPercentage">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.SIMULATION_BOX_MAGNIFICATION_PERCENTAGE);
            if (tmpCurrentElement != null) {
                this.simulationBoxMagnificationPercentage = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.numberOfSpinSteps">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.NUMBER_OF_SPIN_STEPS);
            if (tmpCurrentElement != null) {
                this.numberOfSpinSteps = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.lastSelectedPath">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.LAST_SELECTED_PATH);
            if (tmpCurrentElement != null) {
                this.lastSelectedPath = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.mainFrameHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.MAIN_FRAME_HEIGHT);
            if (tmpCurrentElement != null) {
                this.mainFrameHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.mainFrameWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.MAIN_FRAME_WIDTH);
            if (tmpCurrentElement != null) {
                this.mainFrameWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogSlicerShowHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_SLICER_SHOW_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogSlicerShowHeight = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogSlicerShowWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_SLICER_SHOW_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogSlicerShowWidth = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.customDialogHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.CUSTOM_DIALOG_HEIGHT);
            if (tmpCurrentElement != null) {
                this.customDialogHeight = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.customDialogWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.CUSTOM_DIALOG_WIDTH);
            if (tmpCurrentElement != null) {
                this.customDialogWidth = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogSingleSlicerShowHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_SINGLE_SLICER_SHOW_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogSingleSlicerShowHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogSingleSlicerShowWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_SINGLE_SLICER_SHOW_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogSingleSlicerShowWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogSimulationMovieSlicerShowHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_SIMULATION_MOVIE_SLICER_SHOW_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogSimulationMovieSlicerShowHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogSimulationMovieSlicerShowWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_SIMULATION_MOVIE_SLICER_SHOW_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogSimulationMovieSlicerShowWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemEditHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_VALUE_ITEM_EDIT_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogValueItemEditHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemEditWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_VALUE_ITEM_EDIT_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogValueItemEditWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogTextEditHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_TEXT_EDIT_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogTextEditHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogTextEditWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_TEXT_EDIT_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogTextEditWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemShowHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_VALUE_ITEM_SHOW_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogValueItemShowHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemShowWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_VALUE_ITEM_SHOW_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogValueItemShowWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogTableDataSchemataManageHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_TABLE_DATA_SCHEMATA_MANAGE_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogTableDataSchemataManageHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogTableDataSchemataManageWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_TABLE_DATA_SCHEMATA_MANAGE_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogTableDataSchemataManageWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemMatrixDiagramHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_VALUE_ITEM_MATRIX_DIAGRAM_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogValueItemMatrixDiagramHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogValueItemMatrixDiagramWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_VALUE_ITEM_MATRIX_DIAGRAM_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogValueItemMatrixDiagramWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogStructureEditHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_STRUCTURE_EDIT_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogStructureEditHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogStructureEditWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_STRUCTURE_EDIT_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogStructureEditWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogPeptideEditHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_PEPTIDE_EDIT_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogPeptideEditHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogPeptideEditWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_PEPTIDE_EDIT_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogPeptideEditWidth = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogCompartmentEditHeight">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_COMPARTMENT_EDIT_HEIGHT);
            if (tmpCurrentElement != null) {
                this.dialogCompartmentEditHeight = Integer.parseInt(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.dialogCompartmentEditWidth">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DIALOG_COMPARTMENT_EDIT_WIDTH);
            if (tmpCurrentElement != null) {
                this.dialogCompartmentEditWidth = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isParticleUpdateForJobInput">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_PARTICLE_UPDATE_FOR_JOB_INPUT);
            if (tmpCurrentElement != null) {
                this.isParticleUpdateForJobInput = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isJobResultArchiveStepFileInclusion">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION);
            if (tmpCurrentElement != null) {
                this.isJobResultArchiveStepFileInclusion = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isVolumeScalingForConcentrationCalculation">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_VOLUME_SCALING_FOR_CONCENTRATION_CALCULATION);
            if (tmpCurrentElement != null) {
                this.isVolumeScalingForConcentrationCalculation = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isJobInputInclusion">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_JOB_INPUT_INCLUSION);
            if (tmpCurrentElement != null) {
                this.isJobInputInclusion = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isParticleDistributionInclusion">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_PARTICLE_DISTRICUTION_INCLUSION);
            if (tmpCurrentElement != null) {
                this.isParticleDistributionInclusion = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isSimulationStepInclusion">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_SIMULATION_STEP_INCLUSION);
            if (tmpCurrentElement != null) {
                this.isSimulationStepInclusion = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isNearestNeighborEvaluationInclusion">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION);
            if (tmpCurrentElement != null) {
                this.isNearestNeighborEvaluationInclusion = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isJobResultArchiveProcessParallelInBackground">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND);
            if (tmpCurrentElement != null) {
                this.isJobResultArchiveProcessParallelInBackground = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isJobResultArchiveFileUncompressed">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED);
            if (tmpCurrentElement != null) {
                this.isJobResultArchiveFileUncompressed = Boolean.parseBoolean(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isJdpdLogLevelExceptions">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_JDPD_LOG_LEVEL_EXCEPTIONS);
            if (tmpCurrentElement != null) {
                this.isJdpdLogLevelExceptions = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isConstantCompartmentBodyVolume">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_CONSTANT_COMPARTMENT_BODY_VOLUME);
            if (tmpCurrentElement != null) {
                this.isConstantCompartmentBodyVolume = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isSimulationBoxSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_SIMULATION_BOX_SLICER);
            if (tmpCurrentElement != null) {
                this.isSimulationBoxSlicer = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isSingleSliceDisplay">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_SINGLE_SLICE_DISPLAY);
            if (tmpCurrentElement != null) {
                this.isSingleSliceDisplay = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.slicerGraphicsMode">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.SLICER_GRAPHICS_MODE);
            if (tmpCurrentElement != null) {
                this.slicerGraphicsMode = GraphicsModeEnum.valueOf(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.simulationBoxBackgroundColorSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.SIMULATION_BOX_BACKGROUND_COLOR_SLICER);
            if (tmpCurrentElement != null) {
                this.simulationBoxBackgroundColorSlicer = tmpCurrentElement.getText();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.measurementColorSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.MEASUREMENT_COLOR_SLICER);
            if (tmpCurrentElement != null) {
                this.measurementColorSlicer = tmpCurrentElement.getText();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.moleculeSelectionColorSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.MOLECULE_SELECTION_COLOR_SLICER);
            if (tmpCurrentElement != null) {
                this.moleculeSelectionColorSlicer = tmpCurrentElement.getText();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.frameColorSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.FRAME_COLOR_SLICER);
            if (tmpCurrentElement != null) {
                this.frameColorSlicer = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.jmolSimulationBoxBackgroundColor">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JMOL_SIMULATION_BOX_BACKGROUND_COLOR);
            if (tmpCurrentElement != null) {
                this.jmolSimulationBoxBackgroundColor = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.proteinViewerBackgroundColor">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.PROTEIN_VIEWER_BACKGROUND_COLOR);
            if (tmpCurrentElement != null) {
                this.proteinViewerBackgroundColor = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.imageStorageMode">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IMAGE_STORAGE_MODE);
            if (tmpCurrentElement != null) {
                this.imageStorageMode = ImageStorageEnum.valueOf(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.particleColorDisplayMode">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.PARTICLE_COLOR_DISPLAY_MODE);
            if (tmpCurrentElement != null) {
                this.particleColorDisplayMode = ParticleColorDisplayEnum.valueOf(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.boxViewDisplay">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.BOX_VIEW_DISPLAY);
            if (tmpCurrentElement != null) {
                this.boxViewDisplay = SimulationBoxViewEnum.toSimulationBoxViewEnum(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.isFrameDisplaySlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.IS_FRAME_DISPLAY_SLICER);
            if (tmpCurrentElement != null) {
                this.isFrameDisplaySlicer = Boolean.parseBoolean(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.specularWhiteAttenuationSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.SPECULAR_WHITE_ATTENUATION_SLICER);
            if (tmpCurrentElement != null) {
                this.specularWhiteAttenuationSlicer = Double.parseDouble(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.depthAttenuationCompartment">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.COLOR_SHAPE_ATTENUATION_COMPARTMENT);
            if (tmpCurrentElement != null) {
                this.depthAttenuationCompartment = Double.parseDouble(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.compartmentBodyChangeResponseFactor">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR);
            if (tmpCurrentElement != null) {
                this.compartmentBodyChangeResponseFactor = Double.parseDouble(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.maxSelectedMoleculeNumberSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.MAX_SELECTED_MOLECULE_NUMBER_SLICER);
            if (tmpCurrentElement != null) {
                this.maxSelectedMoleculeNumberSlicer = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.depthAttenuationSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.DEPTH_ATTENUATION_SLICER);
            if (tmpCurrentElement != null) {
                this.depthAttenuationSlicer = Double.parseDouble(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.colorGradientAttenuationCompartment">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.COLOR_GRADIENT_ATTENUATION_COMPARTMENT);
            if (tmpCurrentElement != null) {
                this.colorGradientAttenuationCompartment = Double.parseDouble(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.colorGradientAttenuationSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.COLOR_GRADIENT_ATTENUATION_SLICER);
            if (tmpCurrentElement != null) {
                this.colorGradientAttenuationSlicer = Double.parseDouble(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.jpegImageQuality">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JPEG_IMAGE_QUALITY);
            if (tmpCurrentElement != null) {
                this.jpegImageQuality = Float.parseFloat(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.colorTransparencyCompartment">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.COLOR_TRANSPARENCY_COMPARTMENT);
            if (tmpCurrentElement != null) {
                this.colorTransparencyCompartment = Float.parseFloat(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.xShiftInPixelSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.X_SHIFT_IN_PIXEL_SLICER);
            if (tmpCurrentElement != null) {
                this.xShiftInPixelSlicer = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.yShiftInPixelSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.Y_SHIFT_IN_PIXEL_SLICER);
            if (tmpCurrentElement != null) {
                this.yShiftInPixelSlicer = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.rotationAroundXaxisAngle">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.ROTATION_AROUND_X_AXIS_ANGLE);
            if (tmpCurrentElement != null) {
                // NOTE: Do not set directly because of compatibility issues
                // this.rotationAroundXaxisAngle = Integer.parseInt(tmpCurrentElement.getText());
                // but use
                this.setRotationAroundXaxisAngle(Integer.parseInt(tmpCurrentElement.getText()));
                // due to automatic rotation correction
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.rotationAroundYaxisAngle">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.ROTATION_AROUND_Y_AXIS_ANGLE);
            if (tmpCurrentElement != null) {
                // NOTE: Do not set directly because of compatibility issues
                // this.rotationAroundYaxisAngle = Integer.parseInt(tmpCurrentElement.getText());
                // but use
                this.setRotationAroundYaxisAngle(Integer.parseInt(tmpCurrentElement.getText()));
                // due to automatic rotation correction
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.rotationAroundZaxisAngle">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.ROTATION_AROUND_Z_AXIS_ANGLE);
            if (tmpCurrentElement != null) {
                // NOTE: Do not set directly because of compatibility issues
                // this.rotationAroundZaxisAngle = Integer.parseInt(tmpCurrentElement.getText());
                // but use
                this.setRotationAroundZaxisAngle(Integer.parseInt(tmpCurrentElement.getText()));
                // due to automatic rotation correction
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.particleShiftX">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.PARTICLE_SHIFT_X);
            if (tmpCurrentElement != null) {
                this.particleShiftX = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.particleShiftY">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.PARTICLE_SHIFT_Y);
            if (tmpCurrentElement != null) {
                this.particleShiftY = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.particleShiftZ">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.PARTICLE_SHIFT_Z);
            if (tmpCurrentElement != null) {
                this.particleShiftZ = Integer.parseInt(tmpCurrentElement.getText());
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.radialGradientPaintRadiusMagnification">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION);
            if (tmpCurrentElement != null) {
                this.radialGradientPaintRadiusMagnification = Float.parseFloat(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.specularWhiteSizeSlicer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.SPECULAR_WHITE_SIZE_SLICER);
            if (tmpCurrentElement != null) {
                this.specularWhiteSizeSlicer = Float.parseFloat(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.radialGradientPaintFocusFactorX">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_X);
            if (tmpCurrentElement != null) {
                this.radialGradientPaintFocusFactorX = Float.parseFloat(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.radialGradientPaintFocusFactorY">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.RADIAL_GRADIENT_PAINT_FOCUS_FACTOR_Y);
            if (tmpCurrentElement != null) {
                this.radialGradientPaintFocusFactorY = Float.parseFloat(tmpCurrentElement.getText());
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.schemaValueItemContainer">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.SCHEMA_VALUE_ITEM_CONTAINER);
            if (tmpCurrentElement != null) {
                this.schemaValueItemContainer = new ValueItemContainer(this.stringUtilityMethods.decompressBase64String(tmpCurrentElement.getText()), null);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Job input filter">
            // <editor-fold defaultstate="collapsed" desc="- this.jobInputFilterAfterTimestamp">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JOB_INPUT_FILTER_AFTER_TIMESTAMP);
            if (tmpCurrentElement != null) {
                this.jobInputFilterAfterTimestamp = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- this.jobInputFilterBeforeTimestamp">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JOB_INPUT_FILTER_BEFORE_TIMESTAMP);
            if (tmpCurrentElement != null) {
                this.jobInputFilterBeforeTimestamp = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- this.jobInputFilterContainsPhrase">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JOB_INPUT_FILTER_CONTAINS_PHRASE);
            if (tmpCurrentElement != null) {
                this.jobInputFilterContainsPhrase = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Job result filter">
            // <editor-fold defaultstate="collapsed" desc="- this.jobResultFilterAfterTimestamp">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JOB_RESULT_FILTER_AFTER_TIMESTAMP);
            if (tmpCurrentElement != null) {
                this.jobResultFilterAfterTimestamp = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- this.jobResultFilterBeforeTimestamp">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JOB_RESULT_FILTER_BEFORE_TIMESTAMP);
            if (tmpCurrentElement != null) {
                this.jobResultFilterBeforeTimestamp = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- this.jobResultFilterContainsPhrase">
            tmpCurrentElement = anElement.getChild(PreferenceXmlName.JOB_RESULT_FILTER_CONTAINS_PHRASE);
            if (tmpCurrentElement != null) {
                this.jobResultFilterContainsPhrase = tmpCurrentElement.getText();
            }

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.previousMonomers">
            this.previousMonomers = ModelUtils.getStringListFromXml(anElement, PreferenceXmlName.PREVIOUS_MONOMERS);
            if (this.previousMonomers == null) {
                this.previousMonomers = new LinkedList<String>();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="this.previousStructures">
            this.previousStructures = ModelUtils.getStringListFromXml(anElement, PreferenceXmlName.PREVIOUS_STRUCTURES);
            if (this.previousStructures == null) {
                this.previousStructures = new LinkedList<String>();
            }

            // <editor-fold defaultstate="collapsed" desc="this.previousPeptides">
            this.previousPeptides = ModelUtils.getStringListFromXml(anElement, PreferenceXmlName.PREVIOUS_PEPTIDES);
            if (this.previousPeptides == null) {
                this.previousPeptides = new LinkedList<String>();
            }

            // </editor-fold>
            // </editor-fold>
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
