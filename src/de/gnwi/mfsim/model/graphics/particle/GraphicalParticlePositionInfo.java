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
package de.gnwi.mfsim.model.graphics.particle;

import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.MoleculeSelectionManager;
import de.gnwi.mfsim.model.graphics.SelectedMolecule;
import de.gnwi.mfsim.model.util.VolumeFrequency;
import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.StandardColorEnum;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.valueItem.ValueItemUpdateNotifierInterface;
import java.util.Arrays;
import java.util.HashMap;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Graphical particle positions with additional information
 *
 * @author Achim Zielesny
 */
public class GraphicalParticlePositionInfo implements ValueItemUpdateNotifierInterface{

    // <editor-fold defaultstate="collapsed" desc="Private static final class variables">
    private final static String MOLECULE_SELECTION_VALUE_ITEM_NAME = "MOLECULE_SELECTION_VALUE_ITEM";
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Utility graphics methods
     */
    private final GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * Utility string methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();
    
    /**
     * Initial graphical particle positions
     */
    private final GraphicalParticlePosition[] initialGraphicalParticlePositions;

    /**
     * Initial box size info
     */
    private final BoxSizeInfo initialBoxSizeInfo;

    /**
     * Graphical particle info
     */
    private final GraphicalParticleInfo graphicalParticleInfo;

    /**
     * Factor that converts DPD length to physical length (Angstrom since
     * particle volumes are in Angstrom^3)
     */
    private final double lengthConversionFactor;

    /**
     * True: Length conversion from DPD length to physical length in Angstrom is
     * initially performed, false: Otherwise
     */
    private final boolean isLengthConversion;
    
    /**
     * Map from selection info string to graphical particle for selected
     * graphical particle positions
     */
    private final HashMap<String, GraphicalParticle> selectionInfoStringToGraphicalParticleMap;
    
    /**
     * Minimum molecule index (-1 means NOT defined)
     */
    private final int minMoleculeIndex;
    
    /**
     * Maximum molecule index (-1 means NOT defined)
     */
    private final int maxMoleculeIndex;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Current graphical particle positions
     */
    private GraphicalParticlePositionArrayList currentGraphicalParticlePositionArrayList;

    /**
     * Current box size info
     */
    private BoxSizeInfo currentBoxSizeInfo;

    /**
     * Exclusion box size info
     */
    private BoxSizeInfo exclusionBoxSizeInfo;

    /**
     * Value item container for zoom statistics
     */
    private ValueItemContainer zoomStatisticsValueItemContainer;
    
    /**
     * Molecule selection manager
     */
    private MoleculeSelectionManager moleculeSelectionManager;

    /**
     * Map from current molecule index to corresponding minimum particle index
     */
    private HashMap<Integer, Integer> currentMoleculeIndexToMinParticleIndexMap;
    
    /**
     * Map from current molecule name to minimum molecule index representation
     */
    private HashMap<String, Integer> currentMoleculeNameToMinMoleculeIndexRepresentationMap;
    
    /**
     * Map from current molecule name to maximum molecule index representation
     */
    private HashMap<String, Integer> currentMoleculeNameToMaxMoleculeIndexRepresentationMap;
    
    /**
     * Buffer for molecule selection info
     */
    private StringBuilder moleculeSelectionInfoBuffer;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor.
     * NOTE: NO checks performed.
     *
     * @param aGraphicalParticleInfo GraphicalParticleInfo instance
     * @param anInitialGraphicalParticlePositions Initial graphical particle
     * positions
     * @param aBoxSizeInfo Box size info
     * @param aLengthConversionFactor Factor that converts DPD length to
     * physical length (Angstrom since particle volumes are in Angstrom^3)
     * @param aMinMoleculeIndex Minimum molecule index (-1 means NOT defined)
     * @param aMaxMoleculeIndex Maximum molecule index (-1 means NOT defined)
     */
    public GraphicalParticlePositionInfo(
        GraphicalParticleInfo aGraphicalParticleInfo, 
        GraphicalParticlePosition[] anInitialGraphicalParticlePositions,
        BoxSizeInfo aBoxSizeInfo, 
        double aLengthConversionFactor,
        int aMinMoleculeIndex,
        int aMaxMoleculeIndex
    ) {
        this.graphicalParticleInfo = aGraphicalParticleInfo;
        this.initialGraphicalParticlePositions = anInitialGraphicalParticlePositions;
        this.currentGraphicalParticlePositionArrayList = new GraphicalParticlePositionArrayList(this.initialGraphicalParticlePositions.length);
        this.selectionInfoStringToGraphicalParticleMap = new HashMap<>(ModelDefinitions.DEFEAULT_NUMBER_OF_SELECTED_MOLECULE_PARTICLE_STRINGS);
        this.initialBoxSizeInfo = aBoxSizeInfo;
        this.currentBoxSizeInfo = this.initialBoxSizeInfo.getClone();
        this.exclusionBoxSizeInfo = null;
        this.lengthConversionFactor = aLengthConversionFactor;
        this.minMoleculeIndex = aMinMoleculeIndex;
        this.maxMoleculeIndex = aMaxMoleculeIndex;
        this.moleculeSelectionManager = null;
        this.zoomStatisticsValueItemContainer = null;
        this.isLengthConversion = false;
        this.currentMoleculeIndexToMinParticleIndexMap = new HashMap<>(ModelDefinitions.DEFEAULT_NUMBER_OF_MOLECULE_INDICES);
        this.currentMoleculeNameToMinMoleculeIndexRepresentationMap =  new HashMap<>(ModelDefinitions.DEFAULT_NUMBER_OF_SELECTED_MOLECULE_TYPES);
        this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap =  new HashMap<>(ModelDefinitions.DEFAULT_NUMBER_OF_SELECTED_MOLECULE_TYPES);
        this.moleculeSelectionInfoBuffer = new StringBuilder(ModelDefinitions.DEFAULT_MOLECULE_SELECTION_INFO_STRING_SIZE);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private Constructor">
    /**
     * Constructor.
     * NOTE: NO checks performed.
     *
     * @param aGraphicalParticleInfo GraphicalParticleInfo instance
     * @param anInitialGraphicalParticlePositions Initial graphical particle positions
     * @param aCurrentGraphicalParticlePositionArrayList Current graphical particle position array list
     * @param aCurrentMoleculeIndexToMinParticleIndexMap Map from current molecule index to corresponding minimum particle index
     * @param aCurrentMoleculeNameToMinMoleculeIndexRepresentationMap Map from current molecule name to minimum molecule index representation
     * @param aCurrentMoleculeNameToMaxMoleculeIndexRepresentationMap Map from current molecule name to maximum molecule index representation
     * @param anInitialBoxSizeInfo Initial box size info
     * @param aCurrentBoxSizeInfo Current box size info
     * @param anExclusionBoxSizeInfo Exclusion box size info
     * @param aMoleculeSelectionManager Molecule selection manager
     * @param anZoomStatisticsValueItemContainer Value item container for zoom/exclusion statistics
     * @param aLengthConversionFactor Factor that converts DPD length to physical length (Angstrom since particle volumes are in Angstrom^3)
     * @param anIsLengthConversion True: Length conversion from DPD length to physical length in Angstrom is initially performed, false: Otherwise
     * @param aMinMoleculeIndex Minimum molecule index (-1 means NOT defined)
     * @param aMaxMoleculeIndex Maximum molecule index (-1 means NOT defined)
     */
    private GraphicalParticlePositionInfo(
        GraphicalParticleInfo aGraphicalParticleInfo,
        GraphicalParticlePosition[] anInitialGraphicalParticlePositions,
        GraphicalParticlePositionArrayList aCurrentGraphicalParticlePositionArrayList,
        HashMap<Integer, Integer> aCurrentMoleculeIndexToMinParticleIndexMap,
        HashMap<String, Integer> aCurrentMoleculeNameToMinMoleculeIndexRepresentationMap,
        HashMap<String, Integer> aCurrentMoleculeNameToMaxMoleculeIndexRepresentationMap,
        BoxSizeInfo anInitialBoxSizeInfo,
        BoxSizeInfo aCurrentBoxSizeInfo,
        BoxSizeInfo anExclusionBoxSizeInfo,
        MoleculeSelectionManager aMoleculeSelectionManager,
        ValueItemContainer anZoomStatisticsValueItemContainer,
        double aLengthConversionFactor,
        boolean anIsLengthConversion,
        int aMinMoleculeIndex,
        int aMaxMoleculeIndex
    ) {
        this.graphicalParticleInfo = aGraphicalParticleInfo;
        this.initialGraphicalParticlePositions = anInitialGraphicalParticlePositions;
        this.currentGraphicalParticlePositionArrayList = aCurrentGraphicalParticlePositionArrayList;
        this.currentMoleculeIndexToMinParticleIndexMap = aCurrentMoleculeIndexToMinParticleIndexMap;
        this.currentMoleculeNameToMinMoleculeIndexRepresentationMap = aCurrentMoleculeNameToMinMoleculeIndexRepresentationMap;
        this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap = aCurrentMoleculeNameToMaxMoleculeIndexRepresentationMap;
        this.selectionInfoStringToGraphicalParticleMap = new HashMap<>(ModelDefinitions.DEFEAULT_NUMBER_OF_SELECTED_MOLECULE_PARTICLE_STRINGS);
        this.initialBoxSizeInfo = anInitialBoxSizeInfo;
        this.currentBoxSizeInfo = aCurrentBoxSizeInfo;
        this.exclusionBoxSizeInfo = anExclusionBoxSizeInfo;
        this.moleculeSelectionManager = aMoleculeSelectionManager;
        this.zoomStatisticsValueItemContainer = anZoomStatisticsValueItemContainer;
        this.lengthConversionFactor = aLengthConversionFactor;
        this.minMoleculeIndex = aMinMoleculeIndex;
        this.maxMoleculeIndex = aMaxMoleculeIndex;
        this.isLengthConversion = anIsLengthConversion;
        this.moleculeSelectionInfoBuffer = new StringBuilder(100);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyDependentValueItemsForUpdate() method">
    /**
     * Notify dependent value items of container for update
     *
     * @param anUpdateNotifierValueItem Value item that notifies update
     */
    @Override
    public void notifyDependentValueItemsForUpdate(ValueItem anUpdateNotifierValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anUpdateNotifierValueItem == null) {
            return;
        }
        if (anUpdateNotifierValueItem.getValueItemContainer() == null) {
            return;
        }
        // </editor-fold>
        if (anUpdateNotifierValueItem.getName().equals(MOLECULE_SELECTION_VALUE_ITEM_NAME)) {
            for (int i = 0; i < anUpdateNotifierValueItem.getMatrixRowCount(); i++) {
                String tmpMoleculeName = anUpdateNotifierValueItem.getValue(i, 0);
                int tmpCurrentMinMoleculeIndex = (int) anUpdateNotifierValueItem.getTypeFormat(i, 1).getMinimumValue();
                int tmpMinMoleculeIndex = this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.get(tmpMoleculeName);
                if (tmpCurrentMinMoleculeIndex != tmpMinMoleculeIndex) {
                    String tmpMinMoleculeIndexRepresentation = String.valueOf(tmpMinMoleculeIndex);
                    ValueItemDataTypeFormat tmpValueItemDataTypeFormat = anUpdateNotifierValueItem.getTypeFormat(i, 1);
                    tmpValueItemDataTypeFormat.setMinMaxDefaultValue(
                        tmpMinMoleculeIndex,
                        this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.get(tmpMoleculeName),
                        tmpMinMoleculeIndexRepresentation
                    );
                    anUpdateNotifierValueItem.setValue(tmpMinMoleculeIndexRepresentation, i, 1);
                }
            }
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- getClone method">
    /**
     * Returns new cloned instance of this instance (see code)
     *
     * @return New cloned instance of this instance (see code)
     */
    public GraphicalParticlePositionInfo getClone() {
        return new GraphicalParticlePositionInfo(
            this.graphicalParticleInfo,
            this.initialGraphicalParticlePositions,
            this.currentGraphicalParticlePositionArrayList.getClone(),
            this.currentMoleculeIndexToMinParticleIndexMap,
            this.currentMoleculeNameToMinMoleculeIndexRepresentationMap,
            this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap,
            this.initialBoxSizeInfo,
            this.currentBoxSizeInfo,
            this.exclusionBoxSizeInfo,
            this.moleculeSelectionManager,
            this.zoomStatisticsValueItemContainer,
            this.lengthConversionFactor,
            this.isLengthConversion,
            this.minMoleculeIndex,
            this.maxMoleculeIndex
        );
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Current graphical particle positions related methods">
    /**
     * Sets this.currentGraphicalParticlePositionArrayList from 
     * this.initialGraphicalParticlePositions: Performs exclusions and rotations 
     * according to settings. NOTE: The rotation angles of Preferences are
 used.
     * 
     * @param anIsZoomStatistics True: Zoom statistics are calculated if possible, false: Otherwise
     */
    public void setCurrentGraphicalParticlePositions(
        boolean anIsZoomStatistics
    ) {
        this.setCurrentGraphicalParticlePositions(
            Preferences.getInstance().getRotationAroundXaxisAngle(),
            Preferences.getInstance().getRotationAroundYaxisAngle(),
            Preferences.getInstance().getRotationAroundZaxisAngle(),
            Preferences.getInstance().getParticleShiftX(),
            Preferences.getInstance().getParticleShiftY(),
            Preferences.getInstance().getParticleShiftZ(),
            anIsZoomStatistics
        );
    }

    /**
     * Sets this.currentGraphicalParticlePositionArrayList from 
     * this.initialGraphicalParticlePositions: Performs exclusions and rotations 
     * according to settings. NOTE: The specified rotation angles are used.
     * NOTE: Zoom statistics are NOT calculated.
     *
     * @param aRotationAroundXaxisAngle Angle for rotation around x axis in
     * degree
     * @param aRotationAroundYaxisAngle Angle for rotation around y axis in
     * degree
     * @param aRotationAroundZaxisAngle Angle for rotation around z axis in
     * degree
     * @param aParticleShiftX Particle shift in percent of simulation box 
     * length along x-axis
     * @param aParticleShiftY Particle shift in percent of simulation box 
     * length along y-axis
     * @param aParticleShiftZ Particle shift in percent of simulation box 
     * length along z-axis
     */
    public void setCurrentGraphicalParticlePositions(
        double aRotationAroundXaxisAngle, 
        double aRotationAroundYaxisAngle, 
        double aRotationAroundZaxisAngle,
        double aParticleShiftX,
        double aParticleShiftY,
        double aParticleShiftZ
    ) {
        boolean tmpIsZoomStatistics = false;
        this.setCurrentGraphicalParticlePositions(
            aRotationAroundXaxisAngle,
            aRotationAroundYaxisAngle,
            aRotationAroundZaxisAngle,
            aParticleShiftX,
            aParticleShiftY,
            aParticleShiftZ,
            tmpIsZoomStatistics
        );
    }

    /**
     * Sets this.currentGraphicalParticlePositionArrayList from 
     * this.initialGraphicalParticlePositions: Performs exclusions and rotations 
     * according to settings. NOTE: The specified rotation angles are used.
     *
     * @param aRotationAroundXaxisAngle Angle for rotation around x axis in
     * degree
     * @param aRotationAroundYaxisAngle Angle for rotation around y axis in
     * degree
     * @param aRotationAroundZaxisAngle Angle for rotation around z axis in
     * degree
     * @param aParticleShiftX Particle shift in percent of simulation box 
     * length along x-axis
     * @param aParticleShiftY Particle shift in percent of simulation box 
     * length along y-axis
     * @param aParticleShiftZ Particle shift in percent of simulation box 
     * length along z-axis
     * @param anIsZoomStatistics True: Zoom statistics are calculated if possible, false: Otherwise
     */
    public void setCurrentGraphicalParticlePositions(
        double aRotationAroundXaxisAngle, 
        double aRotationAroundYaxisAngle, 
        double aRotationAroundZaxisAngle,
        double aParticleShiftX,
        double aParticleShiftY,
        double aParticleShiftZ,
        boolean anIsZoomStatistics
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.initialGraphicalParticlePositions == null || this.initialGraphicalParticlePositions.length == 0) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialise zoom statistics if defined">
        boolean tmpIsZoomStatisticsDefined = anIsZoomStatistics && this.exclusionBoxSizeInfo != null;
        // IMPORTANT: Set this.zoomStatisticsValueItemContainer to null!
        this.zoomStatisticsValueItemContainer = null;
        VolumeFrequency tmpVolumeFrequency = null;
        if (tmpIsZoomStatisticsDefined) {
            tmpVolumeFrequency = 
                new VolumeFrequency(
                    this.exclusionBoxSizeInfo, 
                    Preferences.getInstance().getNumberOfVolumeBins(), 
                    this.lengthConversionFactor
                );
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Particle shift, exclusions, selections and zoom statistics if defined">
        boolean tmpIsBulkExcluded = this.graphicalParticleInfo.getExcludedMoleculesTable().containsKey(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MOLECULE_DISPLAY_IN_BULK");
        boolean tmpAreCompartmentsExcluded = this.graphicalParticleInfo.getExcludedMoleculesTable().containsKey(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "MOLECULE_DISPLAY_IN_COMPARTMENTS");
        this.currentGraphicalParticlePositionArrayList.reset();
        this.clearMoleculeSelectionRelatedDataStructure();

        // Particle shift settings
        GraphicalParticlePosition tmpGraphicalParticlePositionForShiftedParticle = new GraphicalParticlePosition(0.0, 0.0, 0.0);
        double tmpAbsoluteParticleShiftX = 0.0;
        double tmpAbsoluteParticleShiftY = 0.0;
        double tmpAbsoluteParticleShiftZ = 0.0;
        boolean tmpIsParticleShift = aParticleShiftX != 0.0 || aParticleShiftY != 0.0 || aParticleShiftZ != 0.0;
        if (tmpIsParticleShift) {
            tmpAbsoluteParticleShiftX = this.initialBoxSizeInfo.getXLength() * aParticleShiftX / 100.0;
            tmpAbsoluteParticleShiftY = this.initialBoxSizeInfo.getYLength() * aParticleShiftY / 100.0;
            tmpAbsoluteParticleShiftZ = this.initialBoxSizeInfo.getZLength() * aParticleShiftZ / 100.0;
        }

        for (GraphicalParticlePosition tmpInitialGraphicalParticlePosition : this.initialGraphicalParticlePositions) {

            // Shift particle if necessary
            if (tmpIsParticleShift) {
                tmpGraphicalParticlePositionForShiftedParticle.reset(
                    tmpInitialGraphicalParticlePosition.getGraphicalParticle(),
                    tmpInitialGraphicalParticlePosition.getX(),
                    tmpInitialGraphicalParticlePosition.getY(),
                    tmpInitialGraphicalParticlePosition.getZ(),
                    tmpInitialGraphicalParticlePosition.getParticleIndex(),
                    tmpInitialGraphicalParticlePosition.getMoleculeIndex()
                );
                tmpInitialGraphicalParticlePosition = tmpGraphicalParticlePositionForShiftedParticle;
                this.graphicsUtilityMethods.shiftPointWithPeriodicBoundaries(
                    tmpInitialGraphicalParticlePosition,
                    tmpAbsoluteParticleShiftX, 
                    tmpAbsoluteParticleShiftY, 
                    tmpAbsoluteParticleShiftZ, 
                    this.initialBoxSizeInfo
                );
            }

            GraphicalParticle tmpGraphicalParticle = (GraphicalParticle) tmpInitialGraphicalParticlePosition.getGraphicalParticle();
            int tmpParticleIndex = tmpInitialGraphicalParticlePosition.getParticleIndex();
            if (
                !this.graphicalParticleInfo.getExcludedMoleculesTable().containsKey(tmpGraphicalParticle.getMoleculeName())
                && !this.graphicalParticleInfo.getExcludedParticlesTable().containsKey(tmpGraphicalParticle.getParticle())
                && !this.graphicalParticleInfo.getExcludedMoleculeParticleStringTable().containsKey(tmpGraphicalParticle.getMoleculeParticleString())
                && !(tmpIsBulkExcluded && tmpInitialGraphicalParticlePosition.isInBulk())
                && !(tmpAreCompartmentsExcluded && !tmpInitialGraphicalParticlePosition.isInBulk())
                && (this.exclusionBoxSizeInfo == null || this.exclusionBoxSizeInfo.isInBox(tmpInitialGraphicalParticlePosition))
            ) {
                if (this.moleculeSelectionManager != null && this.moleculeSelectionManager.isParticleSelected(tmpParticleIndex)) {
                    this.addSelectedGraphicalParticlePosition(
                        tmpInitialGraphicalParticlePosition,
                        tmpGraphicalParticle,
                        tmpParticleIndex,
                        tmpIsZoomStatisticsDefined,
                        tmpVolumeFrequency
                    );
                } else {
                    this.addGraphicalParticlePosition(
                        tmpInitialGraphicalParticlePosition,
                        tmpGraphicalParticle,
                        tmpIsZoomStatisticsDefined,
                        tmpVolumeFrequency
                    );
                }
            } else if (
                this.moleculeSelectionManager != null 
                && this.moleculeSelectionManager.isParticleSelected(tmpParticleIndex)
                && !this.graphicalParticleInfo.getExcludedMoleculeParticleStringTable().containsKey(tmpGraphicalParticle.getMoleculeParticleString())
                && !(tmpIsBulkExcluded && tmpInitialGraphicalParticlePosition.isInBulk())
                && !(tmpAreCompartmentsExcluded && !tmpInitialGraphicalParticlePosition.isInBulk())
                && (this.exclusionBoxSizeInfo == null || this.exclusionBoxSizeInfo.isInBox(tmpInitialGraphicalParticlePosition))
            ) {
                // Add particles of selected molecules that are otherwise excluded
                this.addSelectedGraphicalParticlePosition(
                    tmpInitialGraphicalParticlePosition,
                    tmpGraphicalParticle,
                    tmpParticleIndex,
                    tmpIsZoomStatisticsDefined,
                    tmpVolumeFrequency
                );
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set this.zoomStatisticsValueItemContainer for zoom statistics if defined">
        if (tmpIsZoomStatisticsDefined && this.currentGraphicalParticlePositionArrayList.getSize() > 0) {
            this.zoomStatisticsValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 0;
            tmpVerticalPosition = this.addVolumeStatisticsValueItem(
                this.zoomStatisticsValueItemContainer, 
                tmpVerticalPosition, 
                tmpVolumeFrequency
            );
            tmpVerticalPosition = this.addVolumeAxisStatisticsValueItems(
                VolumeFrequency.VolumeAxis.X,
                this.zoomStatisticsValueItemContainer, 
                tmpVerticalPosition, 
                tmpVolumeFrequency
            );
            tmpVerticalPosition = this.addVolumeAxisStatisticsValueItems(
                VolumeFrequency.VolumeAxis.Y,
                this.zoomStatisticsValueItemContainer, 
                tmpVerticalPosition, 
                tmpVolumeFrequency
            );
            tmpVerticalPosition = this.addVolumeAxisStatisticsValueItems(
                VolumeFrequency.VolumeAxis.Z,
                this.zoomStatisticsValueItemContainer, 
                tmpVerticalPosition, 
                tmpVolumeFrequency
            );
        }
        // </editor-fold>
        if (this.currentGraphicalParticlePositionArrayList.getSize() == 0) {
            return;
        }
        // NOTE: Method this.addGraphicalParticlePositionsOfFrame sets frame 
        // points in this.currentGraphicalParticlePositionArrayList
        this.addGraphicalParticlePositionsOfFrame();
        if (aRotationAroundXaxisAngle != 0.0 || aRotationAroundYaxisAngle != 0.0 || aRotationAroundZaxisAngle != 0.0) {
            // <editor-fold defaultstate="collapsed" desc="Rotate and translate all non-excluded graphical particle positions">
            this.addGraphicalParticlePositionsOfFrame();
            this.graphicsUtilityMethods.rotatePoints(
                this.currentGraphicalParticlePositionArrayList,
                aRotationAroundXaxisAngle,
                aRotationAroundYaxisAngle,
                aRotationAroundZaxisAngle,
                this.currentBoxSizeInfo.getBoxMidPoint()
            );
            // </editor-fold>
        }
    }

    /**
     * Rotates this.currentGraphicalParticlePositionArrayList. NOTE: The specified
     * rotation angles are used.
     *
     * @param aRotationAroundXaxisAngle Angle for rotation around x axis in
     * degree
     * @param aRotationAroundYaxisAngle Angle for rotation around y axis in
     * degree
     * @param aRotationAroundZaxisAngle Angle for rotation around z axis in
     * degree
     */
    public void rotateCurrentGraphicalParticlePositions(
        double aRotationAroundXaxisAngle, 
        double aRotationAroundYaxisAngle, 
        double aRotationAroundZaxisAngle
    ) {
        this.graphicsUtilityMethods.rotatePoints(
            this.currentGraphicalParticlePositionArrayList,
            aRotationAroundXaxisAngle,
            aRotationAroundYaxisAngle,
            aRotationAroundZaxisAngle,
            this.currentBoxSizeInfo.getBoxMidPoint()
        );
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Molecule selection related methods">
    /**
     * Selects molecule
     * 
     * @param aSelectedParticleIndex Selected particle index
     * @return True: Selection was successful, false: Otherwise (nothing changed)
     */
    public boolean selectMolecule(int aSelectedParticleIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectedParticleIndex < 0 || aSelectedParticleIndex >= this.initialGraphicalParticlePositions.length) {
            return false;
        }
        if (this.moleculeSelectionManager != null && this.moleculeSelectionManager.isParticleSelected(aSelectedParticleIndex)) {
            return false;
        }
        // </editor-fold>
        SelectedMolecule tmpSelectedMolecule = this.getSelectedMolecule(aSelectedParticleIndex);
        if (this.moleculeSelectionManager == null) {
            this.moleculeSelectionManager = new MoleculeSelectionManager();
        }
        return this.moleculeSelectionManager.addSelectedMolecule(tmpSelectedMolecule);
    }

    /**
     * Deselects molecule
     * 
     * @param aSelectedParticleIndex Selected particle index
     * @return True: Deselection was successful, false: Otherwise (nothing changed)
     */
    public boolean deselectMolecule(int aSelectedParticleIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.moleculeSelectionManager == null) {
            return false;
        }
        if (aSelectedParticleIndex < 0 || aSelectedParticleIndex >= this.initialGraphicalParticlePositions.length) {
            return false;
        }
        if (!this.moleculeSelectionManager.isParticleSelected(aSelectedParticleIndex)) {
            return false;
        }
        // </editor-fold>
        if (this.moleculeSelectionManager == null) {
            return false;
        } else {
            return this.moleculeSelectionManager.removeSelectedMolecule(aSelectedParticleIndex);
        }
    }

    /**
     * Returns if at least one molecule is selected
     * 
     * @return True: At least one molecule is selected, false: Otherwise
     */
    public boolean isMoleculeSelected() {
        return this.moleculeSelectionManager != null && this.moleculeSelectionManager.isMoleculeSelected();
    }

    /**
     * Number of selected molecules
     * 
     * @return Number of selected molecules
     */
    public int getNumberOfSelectedMolecules() {
        if (this.moleculeSelectionManager == null) {
            return 0;
        } else {
            return this.moleculeSelectionManager.getNumberOfSelectedMolecules();
        }
    }

    /**
     * Selected molecule name
     * 
     * @param aSelectedParticleIndex Selected particle index
     * @return Selected molecule name or null if none is specified
     */
    public String getSelectedMoleculeName(int aSelectedParticleIndex) {
        if (this.moleculeSelectionManager == null) {
            return null;
        } else {
            return this.moleculeSelectionManager.getSelectedMoleculeName(aSelectedParticleIndex);
        }
    }

    /**
     * Selected molecule index
     * 
     * @param aSelectedParticleIndex Selected particle index
     * @return Selected molecule index or -1 if none is specified
     */
    public int getSelectedMoleculeIndex(int aSelectedParticleIndex) {
        if (this.moleculeSelectionManager == null) {
            return -1;
        } else {
            return this.moleculeSelectionManager.getSelectedMoleculeIndex(aSelectedParticleIndex);
        }
    }
    
    /**
     * Clears selection
     */
    public void clearSelection() {
        if (this.moleculeSelectionManager != null) {
            this.moleculeSelectionManager.clear();
        }
    }
    
    /**
     * Returns value item container for selected molecules
     * 
     * @return Value item container for selected molecules or null if none 
     * could be created
     */
    public ValueItemContainer getMoleculeSelectionValueItemContainer() {
        // <editor-fold defaultstate="collapsed" desc="Determine available current molecule indices">
        if (
            this.currentMoleculeIndexToMinParticleIndexMap.isEmpty() 
            && this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.isEmpty()
            && this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.isEmpty()
        ) {
            this.setCurrentMoleculeSelectionMaps();
        }
        // </editor-fold>
        ValueItemContainer tmpValueItemContainer = new ValueItemContainer(this);
        int tmpVerticalPosition = 1;
        String[] tmpNodeNames = 
            new String[]
                {
                    ModelMessage.get("MoleculeSelection.Root")
                };
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setName(MOLECULE_SELECTION_VALUE_ITEM_NAME);
        tmpValueItem.setDisplayName(ModelMessage.get("MoleculeSelection.SelectedMolecules.DisplayName"));
        tmpValueItem.setDescription(ModelMessage.get("MoleculeSelection.SelectedMolecules.Description"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.FLEXIBLE_MATRIX);
        tmpValueItem.setMatrixColumnNames(new String[]
                {
                    ModelMessage.get("MoleculeSelection.MoleculeName"),
                    ModelMessage.get("MoleculeSelection.MoleculeIndex"),
                    ModelMessage.get("MoleculeSelection.IsColorUsage"),
                    ModelMessage.get("MoleculeSelection.Color"),
                    ModelMessage.get("MoleculeSelection.Transparency")
                }
            );
        tmpValueItem.setMatrixColumnWidths(new String[]
                {
                    ModelDefinitions.CELL_WIDTH_TEXT_200,   // MoleculeName
                    ModelDefinitions.CELL_WIDTH_TEXT_100,   // MoleculeIndex
                    ModelDefinitions.CELL_WIDTH_TEXT_100,   // IsColorUsage
                    ModelDefinitions.CELL_WIDTH_TEXT_100,   // Color
                    ModelDefinitions.CELL_WIDTH_TEXT_100    // Transparency
                }
            );
        String[] tmpMoleculeNames = this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.keySet().toArray(new String[0]);
        Arrays.sort(tmpMoleculeNames);
        // Parameter false: NO exclusive selection texts otherwise schemata may 
        // not work in desired manner
        ValueItemDataTypeFormat tmpMoleculeNameTypeFormat = 
            new ValueItemDataTypeFormat(
                tmpMoleculeNames[0],
                tmpMoleculeNames,
                false
            );                
        ValueItemDataTypeFormat tmpMoleculeIndexTypeFormat = 
            new ValueItemDataTypeFormat(
                String.valueOf(this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.get(tmpMoleculeNames[0])),
                0,
                this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.get(tmpMoleculeNames[0]),
                this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.get(tmpMoleculeNames[0])
            );                
        ValueItemDataTypeFormat tmpSelectionColorUsageTypeFormat = 
            new ValueItemDataTypeFormat(
                ModelMessage.get("MoleculeSelection.Parameter.True"),
                new String[] 
                    {
                        ModelMessage.get("MoleculeSelection.Parameter.True"),
                        ModelMessage.get("MoleculeSelection.Parameter.False")
                    }
            );
        ValueItemDataTypeFormat tmpSelectionColorTypeFormat = 
            new ValueItemDataTypeFormat(
                ModelDefinitions.DEFAULT_MOLECULE_SELECTION_COLOR_SLICER.toString(), 
                StandardColorEnum.getAllColorRepresentations()
            );
        ValueItemDataTypeFormat tmpSelectionTransparencyTypeFormat = 
            new ValueItemDataTypeFormat(
                "0.00", 
                2, 
                0.0, 
                1.0
            );
        // NOTE: Set essential to false to avoid disappearance of activity check box in GUI after activity is set to true!
        tmpValueItem.setEssential(false);
        if (this.moleculeSelectionManager != null && this.moleculeSelectionManager.isMoleculeSelected()) {
            tmpValueItem.setActivity(true);
            SelectedMolecule[] tmpSortedSelectedMolecules = this.moleculeSelectionManager.getEntryIndexSortedSelectedMolecules();
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpSortedSelectedMolecules.length][];
            for (int i = 0; i < tmpSortedSelectedMolecules.length; i++) {
                SelectedMolecule tmpSelectedMolecule = tmpSortedSelectedMolecules[i];
                // NOTE: Molecule name of tmpSelectedMolecule may NOT be contained in tmpMoleculeNames due to graphics settings
                if (!this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.containsKey(tmpSelectedMolecule.getMoleculeName())) {
                    return null;
                }
                tmpMatrix[i] = new ValueItemMatrixElement[5];
                // Molecule name
                // Parameter false: NO exclusive selection texts otherwise schemata may 
                // not work in desired manner
                ValueItemDataTypeFormat tmpSpecificMoleculeNameTypeFormat = 
                    new ValueItemDataTypeFormat(
                        tmpSelectedMolecule.getMoleculeName(),
                        tmpMoleculeNames,
                        false
                    );                
                tmpMatrix[i][0] = 
                    new ValueItemMatrixElement(
                        tmpSelectedMolecule.getMoleculeName(),
                        tmpSpecificMoleculeNameTypeFormat    
                    );
                // Molecule index
                ValueItemDataTypeFormat tmpSpecificMoleculeIndexTypeFormat = 
                    new ValueItemDataTypeFormat(
                        String.valueOf(tmpSelectedMolecule.getMoleculeIndex()),
                        0,
                        this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.get(tmpSelectedMolecule.getMoleculeName()),
                        this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.get(tmpSelectedMolecule.getMoleculeName())
                    );                
                tmpMatrix[i][1] = 
                    new ValueItemMatrixElement(
                        String.valueOf(tmpSelectedMolecule.getMoleculeIndex()),
                        tmpSpecificMoleculeIndexTypeFormat    
                    );
                // Selection color usage
                tmpMatrix[i][2] = 
                    new ValueItemMatrixElement(
                        String.valueOf(tmpSelectedMolecule.isSelectionColorUsage()),
                        tmpSelectionColorUsageTypeFormat    
                    );
                // Selection color
                tmpMatrix[i][3] = 
                    new ValueItemMatrixElement(
                        tmpSelectedMolecule.getSelectionColorAsStandardColorEnum().toString(),
                        tmpSelectionColorTypeFormat    
                    );
                // Selection transparency
                tmpMatrix[i][4] = 
                    new ValueItemMatrixElement(
                        String.valueOf(tmpSelectedMolecule.getSelectionTransparency()),
                        tmpSelectionTransparencyTypeFormat    
                    );
            }
            tmpValueItem.setMatrix(tmpMatrix);
        } else {
            tmpValueItem.setDefaultTypeFormats(
                new ValueItemDataTypeFormat[]
                    {
                        tmpMoleculeNameTypeFormat,
                        tmpMoleculeIndexTypeFormat,
                        tmpSelectionColorUsageTypeFormat,
                        tmpSelectionColorTypeFormat,
                        tmpSelectionTransparencyTypeFormat
                    }
                );
            tmpValueItem.setActivity(false);
        }
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpValueItem.setUpdateNotifier(true);
        tmpValueItemContainer.addValueItem(tmpValueItem);
        return tmpValueItemContainer;
    }
    
    /**
     * Sets molecule selections according to value item container that was created
 with method getMoleculeSelectionValueItemContainer()
     * 
     * @param aSelectedMoleculesValueItemContainer Selected molecules value item container
     */
    public void setMoleculeSelectionValueItemContainer(ValueItemContainer aSelectedMoleculesValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSelectedMoleculesValueItemContainer == null) {
            return;
        }
        // </editor-fold>
        this.clearSelection();
        ValueItem tmpSelectedMoleculesValueItem = aSelectedMoleculesValueItemContainer.getValueItem(MOLECULE_SELECTION_VALUE_ITEM_NAME);
        if (tmpSelectedMoleculesValueItem != null && tmpSelectedMoleculesValueItem.isActive()) {
            if (this.moleculeSelectionManager == null) {
                this.moleculeSelectionManager = new MoleculeSelectionManager();
            }
            for (int i = 0; i < tmpSelectedMoleculesValueItem.getMatrixRowCount(); i++) {
                int tmpMoleculeIndex = tmpSelectedMoleculesValueItem.getValueAsInt(i, 1);
                if (this.currentMoleculeIndexToMinParticleIndexMap.containsKey(tmpMoleculeIndex)) {
                    int tmpMinParticleIndex = this.currentMoleculeIndexToMinParticleIndexMap.get(tmpMoleculeIndex);
                    SelectedMolecule tmpSelectedMolecule = this.getSelectedMolecule(tmpMinParticleIndex);
                    tmpSelectedMolecule.setSelectionColorUsage(tmpSelectedMoleculesValueItem.getValueAsBoolean(i, 2));
                    tmpSelectedMolecule.setSelectionColor(StandardColorEnum.toStandardColor(tmpSelectedMoleculesValueItem.getValue(i, 3)));
                    tmpSelectedMolecule.setSelectionTransparency(tmpSelectedMoleculesValueItem.getValueAsFloat(i, 4));
                    this.moleculeSelectionManager.addSelectedMolecule(tmpSelectedMolecule);
                }
            }
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    // <editor-fold defaultstate="collapsed" desc="- Exclusion box size info">
    /**
     * Sets exclusion box size info
     *
     * @param anExclusionBoxSizeInfo Exclusion box size info (can be null, there
     * are no checks)
     */
    public void setExclusionBoxSizeInfo(BoxSizeInfo anExclusionBoxSizeInfo) {
        this.exclusionBoxSizeInfo = anExclusionBoxSizeInfo;
        // IMPORTANT: Set this.currentBoxSizeInfo
        if (this.exclusionBoxSizeInfo != null) {
            this.currentBoxSizeInfo = this.exclusionBoxSizeInfo.getClone();
        } else {
            this.currentBoxSizeInfo = this.initialBoxSizeInfo.getClone();
        }
    }

    /**
     * Sets exclusion box size info
     *
     * @param aFirstPoint First point
     * @param aSecondPoint Second point
     */
    public void setExclusionBoxSizeInfo(PointInSpace aFirstPoint, PointInSpace aSecondPoint) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFirstPoint == null || aSecondPoint == null) {
            return;
        }
        // Points must define a box and NOT a plane
        if (aFirstPoint.getX() == aSecondPoint.getX() || aFirstPoint.getY() == aSecondPoint.getY() || aFirstPoint.getZ() == aSecondPoint.getZ()) {
            return;
        }
        // </editor-fold>
        this.exclusionBoxSizeInfo = new BoxSizeInfo(aFirstPoint, aSecondPoint);
        // IMPORTANT: Set this.currentBoxSizeInfo
        this.currentBoxSizeInfo = this.exclusionBoxSizeInfo.getClone();
    }

    /**
     * Returns if exclusion box size info exists
     *
     * @return True: Exclusion box size info exists, false: Otherwise
     */
    public boolean hasExclusionBoxSizeInfo() {
        return this.exclusionBoxSizeInfo != null;
    }

    /**
     * Removes exclusion box size info
     */
    public void removeExclusionBoxSizeInfo() {
        this.exclusionBoxSizeInfo = null;
        // IMPORTANT: Set this.currentBoxSizeInfo
        this.currentBoxSizeInfo = this.initialBoxSizeInfo.getClone();
    }

    /**
     * Returns exclusion box size info (may be null)
     * 
     * @return Exclusion box size info (may be null)
     */
    public BoxSizeInfo getExclusionBoxSizeInfo() {
        return this.exclusionBoxSizeInfo;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MoleculeSelectionManager">
    /**
     * Molecule selection manager
     *
     * @return Molecule selection manager
     */
    public MoleculeSelectionManager getMoleculeSelectionManager() {
        return this.moleculeSelectionManager;
    }

    /**
     * Molecule selection manager
     *
     * @param aMoleculeSelectionManager Molecule selection manager
     */
    public void setMoleculeSelectionManager(MoleculeSelectionManager aMoleculeSelectionManager) {
        this.moleculeSelectionManager = aMoleculeSelectionManager;
    }

    /**
     * Returns if molecule selection manager is set
     *
     * @return True: Molecule selection manager is set, false: Otherwise
     */
    public boolean hasMoleculeSelectionManager() {
        return this.moleculeSelectionManager != null;
    }

    /**
     * Removes molecule selection manager
     */
    public void removeMoleculeSelectionManager() {
        this.clearSelection();
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    /**
     * Initial graphical particle positions
     * @return Initial graphical particle positions
     */
    public GraphicalParticlePosition[] getInitialGraphicalParticlePositions() {
        return this.initialGraphicalParticlePositions;
    }
    
    /**
     * Current graphical particle position array list.
     * NOTE: The graphical particle positions use the graphical particles of 
     * the particles returned by method 
     * UtilityJobMethods.getMoleculeToParticlesMap().
     *
     * @return Current graphical particle positions array list. NOTE: These 
     * graphical particle positions use the graphical particles of the particles 
     * returned by method UtilityJobMethods.getMoleculeToParticlesMap().
     */
    public GraphicalParticlePositionArrayList getCurrentGraphicalParticlePositionArrayList() {
        return this.currentGraphicalParticlePositionArrayList;
    }

    /**
     * Current box size info
     *
     * @return Current box size info
     */
    public BoxSizeInfo getCurrentBoxSizeInfo() {
        return this.currentBoxSizeInfo;
    }

    /**
     * Initial box size info
     *
     * @return Initial box size info
     */
    public BoxSizeInfo getInitialBoxSizeInfo() {
        return this.initialBoxSizeInfo;
    }

    /**
     * Graphical particle info
     *
     * @return Graphical particle info
     */
    public GraphicalParticleInfo getGraphicalParticleInfo() {
        return this.graphicalParticleInfo;
    }

    /**
     * Factor that converts DPD length to physical length (Angstrom since
     * particle volumes are in Angstrom^3)
     *
     * @return Factor that converts DPD length to physical length (Angstrom
     * since particle volumes are in Angstrom^3)
     */
    public double getLengthConversionFactor() {
        return this.lengthConversionFactor;
    }

    /**
     * Returns if length conversion from DPD length to physical length in
     * Angstrom was initially performed
     *
     * @return True: Length conversion from DPD length to physical length in
     * Angstrom is initially performed, false: Otherwise
     */
    public boolean isLengthConversion() {
        return this.isLengthConversion;
    }

    /**
     * Value item container for zoom/exclusion statistics
     *
     * @return Value item container for zoom/exclusion statistics or null if
     * none is defined
     */
    public ValueItemContainer getZoomStatisticsValueItemContainer() {
        return this.zoomStatisticsValueItemContainer;
    }

    /**
     * Value item container for zoom/exclusion statistics
     *
     * @return True: Value item container for zoom/exclusion statistics is
     * defined, false: Otherwise
     */
    public boolean hasZoomStatisticsValueItemContainer() {
        return this.zoomStatisticsValueItemContainer != null;
    }
    
    /**
     * Returns if molecule selection is supported
     * @return True: Molecule selection is supported, false: Otherwise
     */
    public boolean isMoleculeSelection() {
        return this.minMoleculeIndex >= 0;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Adds graphical particle positions of frame
     */
    private void addGraphicalParticlePositionsOfFrame() {
        if (this.currentGraphicalParticlePositionArrayList != null && this.currentGraphicalParticlePositionArrayList.getSize() > 0) {
            if (Preferences.getInstance().isFrameDisplaySlicer()) {
                this.currentBoxSizeInfo.addGraphicalParticlePositionsOfFrame(this.currentGraphicalParticlePositionArrayList);
            }
        }
    }

    /**
     * Adds zoom-volume-statistics value item
     * 
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition Vertical position
     * @param aVolumeFrequency Volume frequency instance
     * @return Incremented vertical position
     */
    private int addVolumeStatisticsValueItem(ValueItemContainer aValueItemContainer, int aVerticalPosition, VolumeFrequency aVolumeFrequency) {
        // NOTE: No checks are performed!
        ValueItem tmpValueItem = new ValueItem();
        String[] tmpNodeNames = new String[]{
            ModelMessage.get("VolumeFrequencyDistributions.Root")
        };
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setName("ZOOM_VOLUME_STATISTICS");
        tmpValueItem.setDisplayName(ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles"));
        tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles.Description"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles.MoleculeName"),
            ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles.Particle"),
            ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles.ParticleName"),
            ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles.ParticleCount"),
            ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles.MoleculeCount")});
        tmpValueItem.setMatrixColumnWidths(new String[]{
            ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecule_Name
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Particle
            ModelDefinitions.CELL_WIDTH_TEXT_150, // Particle_Name
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Particle_Count
            ModelDefinitions.CELL_WIDTH_TEXT_100}); // Molecule_Count
        // Set molecule-particle information
        String[] tmpMoleculeParticleStringArray = aVolumeFrequency.getSortedMoleculeParticleStrings();
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpMoleculeParticleStringArray.length][];
        for (int i = 0; i < tmpMoleculeParticleStringArray.length; i++) {
            String tmpMoleculeParticleString = tmpMoleculeParticleStringArray[i];
            GraphicalParticle tmpGraphicalParticle = aVolumeFrequency.getGraphicalParticle(tmpMoleculeParticleString);
            String tmpMoleculeName = tmpGraphicalParticle.getMoleculeName();
            String tmpParticle = tmpGraphicalParticle.getParticle();
            String tmpParticleName = tmpGraphicalParticle.getParticleName();
            tmpMatrix[i] = new ValueItemMatrixElement[5];
            // Set molecule name. Parameter false: Not editable
            tmpMatrix[i][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpMoleculeName, ValueItemEnumDataType.TEXT, false));
            // Set particle. Parameter false: Not editable
            tmpMatrix[i][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpParticle, ValueItemEnumDataType.TEXT, false));
            // Set particle name. Parameter false: Not editable
            tmpMatrix[i][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpParticleName, ValueItemEnumDataType.TEXT, false));
            // Set particle count. Parameter false: Not editable
            tmpMatrix[i][3] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(aVolumeFrequency.getVolumeFrequency(tmpMoleculeParticleString)), 0, false));
            // Set molecule count. Parameter false: Not editable
            double tmpMoleculeCount = (double) aVolumeFrequency.getVolumeFrequency(tmpMoleculeParticleString) / (double) tmpGraphicalParticle.getMoleculeParticleFrequency();
            tmpMatrix[i][4] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(tmpMoleculeCount), 3, false));
        }
        tmpValueItem.setMatrix(tmpMatrix);
        tmpValueItem.setVerticalPosition(aVerticalPosition++);
        aValueItemContainer.addValueItem(tmpValueItem);
        return aVerticalPosition;
    }

    /**
     * Adds zoom-volume-axis-statistics value items
     * 
     * @param aVolumeAxis Volume axis
     * @param aValueItemContainer Value item container
     * @param aVerticalPosition Vertical position
     * @param aVolumeFrequency Volume frequency instance
     * @return Incremented vertical position
     */
    private int addVolumeAxisStatisticsValueItems(VolumeFrequency.VolumeAxis aVolumeAxis, ValueItemContainer aValueItemContainer, int aVerticalPosition, VolumeFrequency aVolumeFrequency) {
        // NOTE: No checks are performed!
        // <editor-fold defaultstate="collapsed" desc="Molecules value items">
        String[] tmpNodeNames = null;
        switch(aVolumeAxis) {
            case X:
                tmpNodeNames = new String[]{
                    ModelMessage.get("VolumeFrequencyDistributions.Root"),
                    ModelMessage.get("VolumeFrequencyDistributions.AlongX"),
                    ModelMessage.get("VolumeFrequencyDistributions.Molecules")
                };
                break;
            case Y:
                tmpNodeNames = new String[]{
                    ModelMessage.get("VolumeFrequencyDistributions.Root"),
                    ModelMessage.get("VolumeFrequencyDistributions.AlongY"),
                    ModelMessage.get("VolumeFrequencyDistributions.Molecules")
                };
                break;
            case Z:
                tmpNodeNames = new String[]{
                    ModelMessage.get("VolumeFrequencyDistributions.Root"),
                    ModelMessage.get("VolumeFrequencyDistributions.AlongZ"),
                    ModelMessage.get("VolumeFrequencyDistributions.Molecules")
                };
                break;
        }
        String[] tmpMoleculeNames = aVolumeFrequency.getSortedMoleculeNames(aVolumeAxis);
        double[] tmpVolumeSlicePositionsInAngstrom = aVolumeFrequency.getVolumeSlicePositionsInAngstrom(aVolumeAxis);
        for (String tmpMoleculeName : tmpMoleculeNames) {
            ValueItem tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            switch(aVolumeAxis) {
                case X:
                    tmpValueItem.setName("ZOOM_VOLUME_X_AXIS_STATISTICS_MOLECULE_" + tmpMoleculeName);
                    tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.AlongX.Description"));
                    break;
                case Y:
                    tmpValueItem.setName("ZOOM_VOLUME_Y_AXIS_STATISTICS_MOLECULE_" + tmpMoleculeName);
                    tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.AlongY.Description"));
                    break;
                case Z:
                    tmpValueItem.setName("ZOOM_VOLUME_Z_AXIS_STATISTICS_MOLECULE_" + tmpMoleculeName);
                    tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.AlongZ.Description"));
                    break;
            }
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(new String[]{
                ModelMessage.get("VolumeFrequencyDistributions.Position"), ModelMessage.get("VolumeFrequencyDistributions.Frequency") // Frequency
            });
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_100, // Position
                ModelDefinitions.CELL_WIDTH_TEXT_100  // Frequency
            });
            // Set molecule frequencies
            double[] tmpVolumeSliceMoleculeFrequencies = aVolumeFrequency.getVolumeSliceMoleculeFrequencies(aVolumeAxis, tmpMoleculeName);
            double tmpSymmetryIndex = this.getSymmetryIndex(tmpVolumeSliceMoleculeFrequencies);
            tmpValueItem.setDisplayName(
                String.format(
                    ModelMessage.get("VolumeFrequencyDistributions.NameAndSymmetryIndex"), 
                    tmpMoleculeName, 
                    aVolumeAxis.name().toLowerCase(),
                    this.stringUtilityMethods.formatDoubleValue(tmpSymmetryIndex, ModelDefinitions.SYMMETRY_INDEX_NUMBER_OF_DECIMALS)
                )
            );
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpVolumeSliceMoleculeFrequencies.length][];
            for (int i = 0; i < tmpVolumeSliceMoleculeFrequencies.length; i++) {
                tmpMatrix[i] = new ValueItemMatrixElement[2];
                // Set position
                tmpMatrix[i][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(tmpVolumeSlicePositionsInAngstrom[i]), 3, false));
                // Set frequency
                tmpMatrix[i][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(tmpVolumeSliceMoleculeFrequencies[i]), 3, false));
            }
            tmpValueItem.setMatrix(tmpMatrix);
            tmpValueItem.setMatrixDiagramColumns(0, 1);
            tmpValueItem.setVerticalPosition(aVerticalPosition++);
            aValueItemContainer.addValueItem(tmpValueItem);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Particles value items">
        tmpNodeNames = null;
        switch(aVolumeAxis) {
            case X:
                tmpNodeNames = new String[]{
                    ModelMessage.get("VolumeFrequencyDistributions.Root"),
                    ModelMessage.get("VolumeFrequencyDistributions.AlongX"),
                    ModelMessage.get("VolumeFrequencyDistributions.Particles")
                };
                break;
            case Y:
                tmpNodeNames = new String[]{
                    ModelMessage.get("VolumeFrequencyDistributions.Root"),
                    ModelMessage.get("VolumeFrequencyDistributions.AlongY"),
                    ModelMessage.get("VolumeFrequencyDistributions.Particles")
                };
                break;
            case Z:
                tmpNodeNames = new String[]{
                    ModelMessage.get("VolumeFrequencyDistributions.Root"),
                    ModelMessage.get("VolumeFrequencyDistributions.AlongZ"),
                    ModelMessage.get("VolumeFrequencyDistributions.Particles")
                };
                break;
        }
        String[] tmpParticles = aVolumeFrequency.getSortedParticles(aVolumeAxis);
        tmpVolumeSlicePositionsInAngstrom = aVolumeFrequency.getVolumeSlicePositionsInAngstrom(aVolumeAxis);
        for (String tmpParticle : tmpParticles) {
            ValueItem tmpValueItem = new ValueItem();
            tmpValueItem.setNodeNames(tmpNodeNames);
            switch(aVolumeAxis) {
                case X:
                    tmpValueItem.setName("ZOOM_VOLUME_X_AXIS_STATISTICS_PARTICLE_" + tmpParticle);
                    tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.AlongX.Description"));
                    break;
                case Y:
                    tmpValueItem.setName("ZOOM_VOLUME_Y_AXIS_STATISTICS_PARTICLE_" + tmpParticle);
                    tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.AlongY.Description"));
                    break;
                case Z:
                    tmpValueItem.setName("ZOOM_VOLUME_Z_AXIS_STATISTICS_PARTICLE_" + tmpParticle);
                    tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.AlongZ.Description"));
                    break;
            }
            tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpValueItem.setMatrixColumnNames(new String[]{
                ModelMessage.get("VolumeFrequencyDistributions.Position"), ModelMessage.get("VolumeFrequencyDistributions.Frequency") // Frequency
            });
            tmpValueItem.setMatrixColumnWidths(new String[]{
                ModelDefinitions.CELL_WIDTH_TEXT_100, // Position
                ModelDefinitions.CELL_WIDTH_TEXT_100  // Frequency
            });
            // Set particle frequencies
            int[] tmpVolumeSliceParticleFrequencies = aVolumeFrequency.getVolumeSliceParticleFrequencies(aVolumeAxis, tmpParticle);
            double tmpSymmetryIndex = this.getSymmetryIndex(tmpVolumeSliceParticleFrequencies);
            tmpValueItem.setDisplayName(
                String.format(
                    ModelMessage.get("VolumeFrequencyDistributions.NameAndSymmetryIndex"), 
                    tmpParticle,
                    aVolumeAxis.name().toLowerCase(),
                    this.stringUtilityMethods.formatDoubleValue(tmpSymmetryIndex, ModelDefinitions.SYMMETRY_INDEX_NUMBER_OF_DECIMALS)
                )
            );
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpVolumeSliceParticleFrequencies.length][];
            for (int i = 0; i < tmpVolumeSliceParticleFrequencies.length; i++) {
                tmpMatrix[i] = new ValueItemMatrixElement[2];
                // Set position
                tmpMatrix[i][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(tmpVolumeSlicePositionsInAngstrom[i]), 3, false));
                // Set frequency
                tmpMatrix[i][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(tmpVolumeSliceParticleFrequencies[i]), 0, false));
            }
            tmpValueItem.setMatrix(tmpMatrix);
            tmpValueItem.setMatrixDiagramColumns(0, 1);
            tmpValueItem.setVerticalPosition(aVerticalPosition++);
            aValueItemContainer.addValueItem(tmpValueItem);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Molecule-particles value items">
        tmpMoleculeNames = aVolumeFrequency.getSortedMoleculeNames(aVolumeAxis);
        tmpVolumeSlicePositionsInAngstrom = aVolumeFrequency.getVolumeSlicePositionsInAngstrom(aVolumeAxis);
        for (String tmpMoleculeName : tmpMoleculeNames) {
            tmpNodeNames = null;
            switch(aVolumeAxis) {
                case X:
                    tmpNodeNames = new String[]{
                        ModelMessage.get("VolumeFrequencyDistributions.Root"),
                        ModelMessage.get("VolumeFrequencyDistributions.AlongX"),
                        ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles"),
                        tmpMoleculeName
                    };
                    break;
                case Y:
                    tmpNodeNames = new String[]{
                        ModelMessage.get("VolumeFrequencyDistributions.Root"),
                        ModelMessage.get("VolumeFrequencyDistributions.AlongY"),
                        ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles"),
                        tmpMoleculeName
                    };
                    break;
                case Z:
                    tmpNodeNames = new String[]{
                        ModelMessage.get("VolumeFrequencyDistributions.Root"),
                        ModelMessage.get("VolumeFrequencyDistributions.AlongZ"),
                        ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticles"),
                        tmpMoleculeName
                    };
                    break;
            }
            String[] tmpMoleculeParticleStrings = aVolumeFrequency.getSortedMoleculeParticleStrings(aVolumeAxis, tmpMoleculeName);
            for (String tmpMoleculeParticleString : tmpMoleculeParticleStrings) {
                GraphicalParticle tmpGraphicalParticle = aVolumeFrequency.getGraphicalParticle(tmpMoleculeParticleString);
                String tmpParticle = tmpGraphicalParticle.getParticle();
                ValueItem tmpValueItem = new ValueItem();
                tmpValueItem.setNodeNames(tmpNodeNames);
                switch(aVolumeAxis) {
                    case X:
                        tmpValueItem.setName("ZOOM_VOLUME_X_AXIS_STATISTICS_MOLECULEPARTICLE_" + tmpMoleculeParticleString);
                        tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.AlongX.Description"));
                        break;
                    case Y:
                        tmpValueItem.setName("ZOOM_VOLUME_Y_AXIS_STATISTICS_MOLECULEPARTICLE_" + tmpMoleculeParticleString);
                        tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.AlongY.Description"));
                        break;
                    case Z:
                        tmpValueItem.setName("ZOOM_VOLUME_Z_AXIS_STATISTICS_MOLECULEPARTICLE_" + tmpMoleculeParticleString);
                        tmpValueItem.setDescription(ModelMessage.get("VolumeFrequencyDistributions.AlongZ.Description"));
                        break;
                }
                tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
                tmpValueItem.setMatrixColumnNames(new String[]{
                    ModelMessage.get("VolumeFrequencyDistributions.Position"), ModelMessage.get("VolumeFrequencyDistributions.Frequency") // Frequency
                });
                tmpValueItem.setMatrixColumnWidths(new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_100, // Position
                    ModelDefinitions.CELL_WIDTH_TEXT_100  // Frequency
                });
                // Set molecule-particle frequencies
                int[] tmpVolumeSliceMoleculeParticleFrequencies = aVolumeFrequency.getVolumeSliceMoleculeParticleFrequencies(aVolumeAxis, tmpMoleculeName, tmpMoleculeParticleString);
                double tmpSymmetryIndex = this.getSymmetryIndex(tmpVolumeSliceMoleculeParticleFrequencies);
                tmpValueItem.setDisplayName(
                    String.format(
                        ModelMessage.get("VolumeFrequencyDistributions.NameAndSymmetryIndex"), 
                        String.format(ModelMessage.get("VolumeFrequencyDistributions.MoleculeParticlesFormat"), tmpParticle, tmpMoleculeName), 
                        aVolumeAxis.name().toLowerCase(),
                        this.stringUtilityMethods.formatDoubleValue(tmpSymmetryIndex, ModelDefinitions.SYMMETRY_INDEX_NUMBER_OF_DECIMALS)
                    )
                );
                ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpVolumeSliceMoleculeParticleFrequencies.length][];
                for (int i = 0; i < tmpVolumeSliceMoleculeParticleFrequencies.length; i++) {
                    tmpMatrix[i] = new ValueItemMatrixElement[2];
                    // Set position
                    tmpMatrix[i][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(tmpVolumeSlicePositionsInAngstrom[i]), 3, false));
                    // Set frequency
                    tmpMatrix[i][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(String.valueOf(tmpVolumeSliceMoleculeParticleFrequencies[i]), 0, false));
                }
                tmpValueItem.setMatrix(tmpMatrix);
                tmpValueItem.setMatrixDiagramColumns(0, 1);
                tmpValueItem.setVerticalPosition(aVerticalPosition++);
                aValueItemContainer.addValueItem(tmpValueItem);
            }
        }
        // </editor-fold>
        return aVerticalPosition;
    }

    /**
     * Returns symmetry index for array (see code).
     * NOTE: No checks are performed.
     * 
     * @param anArray Array
     * @return Symmetry index for array
     */
    private double getSymmetryIndex(int[] anArray) {
        return getSymmetryIndex(Arrays.stream(anArray).asDoubleStream().toArray());
    }
    
    /**
     * Returns symmetry index for array (see code).
     * NOTE: No checks are performed.
     * 
     * @param anArray Array
     * @return Symmetry index for array
     */
    private double getSymmetryIndex(double[] anArray) {
        int tmpUpper;
        if (anArray.length % 2 == 0) {
            // Even
            tmpUpper = anArray.length / 2;
        } else {
            // Odd
            tmpUpper = (anArray.length - 1) / 2;
        }
        
        double tmpSum = 0.0;
        for (int i = 0; i < anArray.length; i++) {
            tmpSum += anArray[i];
        }

        int tmpLengthMinusOne = anArray.length - 1;
        double tmpDifferenceSum = 0.0;
        for (int i = 0; i < tmpUpper; i++) {
            tmpDifferenceSum += Math.abs(anArray[i] - anArray[tmpLengthMinusOne - i]);
        }
        
        return tmpDifferenceSum /= tmpSum;
    }
    
    /**
     * Returns selected molecule.
     * NOTE: No checks are performed.
     * 
     * @return Selected molecule or null if none could be created
     * @return 
     */
    private SelectedMolecule getSelectedMolecule(int aSelectedParticleIndex) {
        GraphicalParticlePosition tmpSelectedGraphicalParticlePosition = this.initialGraphicalParticlePositions[aSelectedParticleIndex];
        GraphicalParticle tmpSelectedGraphicalParticle = (GraphicalParticle) tmpSelectedGraphicalParticlePosition.getGraphicalParticle();
        String tmpSelectedMoleculeName = tmpSelectedGraphicalParticle.getMoleculeName();
        int tmpSelectedMoleculeIndex = tmpSelectedGraphicalParticlePosition.getMoleculeIndex();
        // Select other graphical particle positions of molecule
        // NOTE: Graphical particle positions of molecule are one after another 
        // located in this.initialGraphicalParticlePositions
        // Search downwards
        int tmpMinParticleIndex = aSelectedParticleIndex;
        if (aSelectedParticleIndex > 0) {
            for (int i = aSelectedParticleIndex; i >= 0; i--) {
                if (this.initialGraphicalParticlePositions[i].getMoleculeIndex() == tmpSelectedMoleculeIndex) {
                    tmpMinParticleIndex = i;
                } else {
                    break;
                }
            }
        }
        // Search upwards
        int tmpExclusiveMaxParticleIndex = aSelectedParticleIndex + 1;
        if (aSelectedParticleIndex < this.initialGraphicalParticlePositions.length - 1) {
            for (int i = aSelectedParticleIndex; i < this.initialGraphicalParticlePositions.length; i++) {
                if (this.initialGraphicalParticlePositions[i].getMoleculeIndex() == tmpSelectedMoleculeIndex) {
                    tmpExclusiveMaxParticleIndex = i + 1;
                } else {
                    break;
                }
            }
        }
        return 
            new SelectedMolecule(
                tmpSelectedMoleculeName, 
                tmpSelectedMoleculeIndex, 
                tmpMinParticleIndex, 
                tmpExclusiveMaxParticleIndex,
                true,
                Preferences.getInstance().getMoleculeSelectionColorSlicer(),
                0.0f
            );
    }

    /**
     * Sets this.currentMoleculeIndexToMinParticleIndexMap and 
     * this.currentMoleculeNameToMoleculeIndexRepresentationsMap
     */
    private void setCurrentMoleculeSelectionMaps() {
        this.currentMoleculeIndexToMinParticleIndexMap.clear();
        this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.clear();
        this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.clear();
        GraphicalParticlePosition[] tmpCurrentGraphicalParticlePositions = this.currentGraphicalParticlePositionArrayList.getGraphicalParticlePositions();
        for (int i = 0; i < this.currentGraphicalParticlePositionArrayList.getSize(); i++) {
            GraphicalParticlePosition tmpCurrentGraphicalParticlePosition = tmpCurrentGraphicalParticlePositions[i];
            GraphicalParticle tmpCurrentGraphicalParticle = (GraphicalParticle) tmpCurrentGraphicalParticlePosition.getGraphicalParticle();
            String tmpCurrentMoleculeName = tmpCurrentGraphicalParticle.getMoleculeName();
            int tmpCurrentMoleculeIndex = tmpCurrentGraphicalParticlePosition.getMoleculeIndex();
            if (!tmpCurrentGraphicalParticlePosition.isInFrame()) {
                if (!this.currentMoleculeIndexToMinParticleIndexMap.containsKey(tmpCurrentGraphicalParticlePosition.getMoleculeIndex())) {
                    this.currentMoleculeIndexToMinParticleIndexMap.put(
                        tmpCurrentGraphicalParticlePosition.getMoleculeIndex(), 
                        tmpCurrentGraphicalParticlePosition.getParticleIndex()
                    );
                }
                if (!this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.containsKey(tmpCurrentMoleculeName)) {
                    this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.put(tmpCurrentMoleculeName, tmpCurrentMoleculeIndex);
                } else {
                    if (tmpCurrentMoleculeIndex < this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.get(tmpCurrentMoleculeName)) {
                        this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.replace(tmpCurrentMoleculeName, tmpCurrentMoleculeIndex);
                    }
                }
                if (!this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.containsKey(tmpCurrentMoleculeName)) {
                    this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.put(tmpCurrentMoleculeName, tmpCurrentMoleculeIndex);
                } else {
                    if (tmpCurrentMoleculeIndex > this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.get(tmpCurrentMoleculeName)) {
                        this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.replace(tmpCurrentMoleculeName, tmpCurrentMoleculeIndex);
                    }
                }
            }
        }
    }
    
    /**
     * Adds selected graphical particle position
     * NOTE: No checks are performed
     * 
     * @param anInitialGraphicalParticlePosition See calling method
     * @param aGraphicalParticle See calling method
     * @param aParticleIndex See calling method
     * @param anIsZoomStatisticsDefined See calling method
     * @param aVolumeFrequency See calling method
     */
    private void addSelectedGraphicalParticlePosition(
        GraphicalParticlePosition anInitialGraphicalParticlePosition,
        GraphicalParticle aGraphicalParticle,
        int aParticleIndex,
        boolean anIsZoomStatisticsDefined,
        VolumeFrequency aVolumeFrequency
    ) {
        if (this.moleculeSelectionManager != null && this.moleculeSelectionManager.isMoleculeSelectionDisplayUsage(aParticleIndex)) {
            StandardColorEnum tmpSelectedMoleculeColor = this.moleculeSelectionManager.getSelectedMoleculeColor(aParticleIndex);
            float tmpSelectedMoleculeTransparency = this.moleculeSelectionManager.getSelectedMoleculeTransparency(aParticleIndex);
            this.moleculeSelectionInfoBuffer.setLength(0);
            this.moleculeSelectionInfoBuffer.append(aGraphicalParticle.getMoleculeName());
            this.moleculeSelectionInfoBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);
            this.moleculeSelectionInfoBuffer.append(aGraphicalParticle.getParticle());
            this.moleculeSelectionInfoBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);
            this.moleculeSelectionInfoBuffer.append(tmpSelectedMoleculeColor.toString());
            this.moleculeSelectionInfoBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);
            this.moleculeSelectionInfoBuffer.append(String.valueOf(tmpSelectedMoleculeTransparency));
            String tmpSelectionInfoString = this.moleculeSelectionInfoBuffer.toString();
            if (this.selectionInfoStringToGraphicalParticleMap.containsKey(tmpSelectionInfoString)) {
                aGraphicalParticle = this.selectionInfoStringToGraphicalParticleMap.get(tmpSelectionInfoString);
            } else {
                aGraphicalParticle = aGraphicalParticle.getClone();
                aGraphicalParticle.setSelectionInfoString(tmpSelectionInfoString);
                aGraphicalParticle.setSelectionColor(tmpSelectedMoleculeColor);
                aGraphicalParticle.setCurrentParticleTransparency(tmpSelectedMoleculeTransparency);
                this.selectionInfoStringToGraphicalParticleMap.put(tmpSelectionInfoString, aGraphicalParticle);
            }
        }
        this.addGraphicalParticlePosition(
            anInitialGraphicalParticlePosition,
            aGraphicalParticle,
            anIsZoomStatisticsDefined,
            aVolumeFrequency
        );
    }

    /**
     * Adds graphical particle position
     * NOTE: No checks are performed
     * 
     * @param anInitialGraphicalParticlePosition See calling method
     * @param aGraphicalParticle See calling method
     * @param anIsZoomStatisticsDefined See calling method
     * @param aVolumeFrequency See calling method
     */
    private void addGraphicalParticlePosition(
        GraphicalParticlePosition anInitialGraphicalParticlePosition,
        GraphicalParticle aGraphicalParticle,
        boolean anIsZoomStatisticsDefined,
        VolumeFrequency aVolumeFrequency
    ) {
        this.currentGraphicalParticlePositionArrayList.add(
            aGraphicalParticle,
            anInitialGraphicalParticlePosition.getX(),
            anInitialGraphicalParticlePosition.getY(),
            anInitialGraphicalParticlePosition.getZ(),
            anInitialGraphicalParticlePosition.getParticleIndex(),
            anInitialGraphicalParticlePosition.getMoleculeIndex()
        );
        // Set zoom statistics if defined
        if (anIsZoomStatisticsDefined) {
            aVolumeFrequency.incrementCounters(anInitialGraphicalParticlePosition);
        }
    }
    
    /**
     * Clears molecule selection related data structures
     */
    private void clearMoleculeSelectionRelatedDataStructure() {
        this.currentMoleculeIndexToMinParticleIndexMap.clear();
        this.currentMoleculeNameToMinMoleculeIndexRepresentationMap.clear();
        this.currentMoleculeNameToMaxMoleculeIndexRepresentationMap.clear();
        this.selectionInfoStringToGraphicalParticleMap.clear();
    }
    // </editor-fold>
    
}
