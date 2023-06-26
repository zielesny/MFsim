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
package de.gnwi.mfsim.model.preference;

import java.util.HashMap;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Editable preferences
 *
 * @author Achim Zielesny
 */
public enum PreferenceEditableEnum {

    /**
     * PreferenceBasic: this.jpegImageQuality
     */
    JPEG_IMAGE_QUALITY,
    /**
     * PreferenceBasic: this.colorTransparencyCompartment
     */
    COLOR_TRANSPARENCY_COMPARTMENT,
    /**
     * PreferenceBasic: this.colorGradientAttenuationCompartment
     */
    COLOR_GRADIENT_ATTENUATION_COMPARTMENT,
    /**
     * PreferenceBasic: this.colorGradientAttenuationSlicer
     */
    COLOR_GRADIENT_ATTENUATION_SLICER,
    /**
     * PreferenceBasic: this.specularWhiteAttenuationSlicer
     */
    SPECULAR_WHITE_ATTENUATION_SLICER,
    /**
     * PreferenceBasic: this.depthAttenuationCompartment
     */
    COLOR_SHAPE_ATTENUATION_COMPARTMENT,
    /**
     * PreferenceBasic: this.compartmentBodyChangeResponseFactor
     */
    COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR,
    /**
     * PreferenceBasic: this.maxSelectedMoleculeNumberSlicer
     */
    MAX_SELECTED_MOLECULE_NUMBER_SLICER,
    /**
     * PreferenceBasic: this.depthAttenuationSlicer
     */
    DEPTH_ATTENUATION_SLICER,
    /**
     * PreferenceBasic: this.xShiftInPixelSlicer and
     * this.yShiftInPixelSlicer
     */
    SHIFTS_SLICER,
    /**
     * PreferenceBasic: this.customDialogWidth and
     * this.customDialogHeight
     */
    CUSTOM_DIALOG_SIZE,
    /**
     * PreferenceBasic: this.rotationAroundXaxisAngle,
     * this.rotationAroundYaxisAngle and this.rotationAroundZaxisAngle
     */
    ROTATION_ANGLES,
    /**
     * PreferenceBasic: this.particleShiftX, this.particleShiftY, 
     * this.particleShiftZ
     */
    PARTICLE_SHIFTS,
    /**
     * PreferenceBasic: this.stepInfoArray
     */
    STEP_INFO_ARRAY_SLICER,
    /**
     * PreferenceBasic: this.radialGradientPaintRadiusMagnification
     */
    RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION,
    /**
     * PreferenceBasic: this.specularWhiteSizeSlicer
     */
    SPECULAR_WHITE_SIZE_SLICER,
    /**
     * PreferenceBasic: this.radialGradientPaintFocusFactorX and
     * this.radialGradientPaintFocusFactorY
     */
    RADIAL_GRADIENT_PAINT_FOCUS_FACTORS,
    /**
     * PreferenceBasic: this.delayForFilesInMilliseconds
     */
    DELAY_FOR_FILES_IN_MILLISECONDS,
    /**
     * PreferenceBasic: this.delayForJobStartInMilliseconds
     */
    DELAY_FOR_JOB_START_IN_MILLISECONDS,
    /**
     * PreferenceBasic: this.internalMFsimJobPath
     */
    INTERNAL_MFSIM_JOB_PATH,
    /**
     * PreferenceBasic: this.internalTempPath
     */
    INTERNAL_TEMP_PATH,
    /**
     * PreferenceBasic: this.currentParticleSetFilename
     */
    CURRENT_PARTICLE_SET_FILENAME,
    /**
     * PreferenceBasic: this.firstSliceIndex
     */
    FIRST_SLICE_INDEX,
    /**
     * PreferenceBasic: this.numberOfVolumeBins
     */
    NUMBER_OF_ZOOM_VOLUME_BINS,
    /**
     * PreferenceBasic: this.numberOfFramePointsSlicer
     */
    NUMBER_OF_FRAME_POINTS_SLICER,
    /**
     * PreferenceBasic: this.timeStepDisplaySlicer
     */
    TIME_STEP_DISPLAY_SLICER,
    /**
     * PreferenceBasic: this.isJobResultArchiveStepFileInclusion
     */
    IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION,
    /**
     * PreferenceBasic: this.isVolumeScalingForConcentrationCalculation
     */
    IS_VOLUME_SCALING_FOR_CONCENTRATION_CALCULATION,
    /**
     * PreferenceBasic: this.isJobInputInclusion
     */
    IS_JOB_INPUT_INCLUSION,
    /**
     * PreferenceBasic: this.isParticleDistributionInclusion
     */
    IS_PARTICLE_DISTRICUTION_INCLUSION,
    /**
     * PreferenceBasic: this.isSimulationStepInclusion
     */
    IS_SIMULATION_STEP_INCLUSION,
    /**
     * PreferenceBasic: this.isNearestNeighborEvaluationInclusion
     */
    IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION,
    /**
     * PreferenceBasic: this.isJobResultArchiveProcessParallelInBackground
     */
    IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND,
    /**
     * PreferenceBasic: this.isJobResultArchiveFileUncompressed
     */
    IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED,
    /**
     * PreferenceBasic: this.isJdpdKernelDoublePrecision
     */
    IS_JDPD_KERNEL_DOUBLE_PRECISION,
    /**
     * PreferenceBasic: this.isJdpdLogLevelException
     */
    IS_JDPD_LOG_LEVEL_EXCEPTION,
    /**
     * PreferenceBasic: this.isConstantCompartmentBodyVolume
     */
    IS_CONSTANT_COMPARTMENT_BODY_VOLUME,
    /**
     * PreferenceBasic: this.isSimulationBoxSlicer
     */
    IS_SIMULATION_BOX_SLICER,
    /**
     * PreferenceBasic: this.isMoleculeDisplayWithStandardParticleSize
     */
    IS_MOLECULE_DISPLAY_WITH_STANDARD_PARTICLE_SIZE,
    /**
     * PreferenceBasic: this.isSingleSliceDisplay
     */
    IS_SINGLE_SLICE_DISPLAY,
    /**
     * PreferenceBasic: this.slicerGraphicsMode
     */
    SLICER_GRAPHICS_MODE,
    /**
     * PreferenceBasic: this.simulationBoxBackgroundColorSlicer
     */
    SIMULATION_BOX_BACKGROUND_COLOR_SLICER,
    /**
     * PreferenceBasic: this.measurementColorSlicer
     */
    MEASUREMENT_COLOR_SLICER,
    /**
     * PreferenceBasic: this.moleculeSelectionColorSlicer
     */
    MOLECULE_SELECTION_COLOR_SLICER,
    /**
     * PreferenceBasic: this.frameColorSlicer
     */
    FRAME_COLOR_SLICER,
    /**
     * PreferenceBasic: this.jmolSimulationBoxBackgroundColor
     */
    JMOL_SIMULATION_BOX_BACKGROUND_COLOR,
    /**
     * PreferenceBasic: this.proteinViewerBackgroundColor
     */
    PROTEIN_VIEWER_BACKGROUND_COLOR,
    /**
     * PreferenceBasic: this.imageStorageMode
     */
    IMAGE_STORAGE_MODE,
    /**
     * PreferenceBasic: this.particleColorDisplayMode
     */
    PARTICLE_COLOR_DISPLAY_MODE,
    /**
     * PreferenceBasic: this.boxViewDisplay
     */
    BOX_VIEW_DISPLAY,
    /**
     * PreferenceBasic: this.isFrameDisplaySlicer
     */
    IS_FRAME_DISPLAY_SLICER,
    /**
     * PreferenceBasic: this.jobInputFilterAfterTimestamp
     */
    JOB_INPUT_FILTER_AFTER_TIMESTAMP,
    /**
     * PreferenceBasic: this.jobInputFilterBeforeTimestamp
     */
    JOB_INPUT_FILTER_BEFORE_TIMESTAMP,
    /**
     * PreferenceBasic: this.jobInputFilterContainsPhrase
     */
    JOB_INPUT_FILTER_CONTAINS_PHRASE,
    /**
     * PreferenceBasic: this.jobResultFilterAfterTimestamp
     */
    JOB_RESULT_FILTER_AFTER_TIMESTAMP,
    /**
     * PreferenceBasic: this.jobResultFilterBeforeTimestamp
     */
    JOB_RESULT_FILTER_BEFORE_TIMESTAMP,
    /**
     * PreferenceBasic: this.jobResultFilterContainsPhrase
     */
    JOB_RESULT_FILTER_CONTAINS_PHRASE,
    /**
     * PreferenceBasic: this.numberOfSlicesPerView
     */
    NUMBER_OF_SLICES,
    /**
     * PreferenceBasic: this.jmolShadePower
     */
    JMOL_SHADE_POWER,
    /**
     * PreferenceBasic: this.jmolAmbientLightPercentage
     */
    JMOL_AMBIENT_LIGHT_PERCENTAGE,
    /**
     * PreferenceBasic: this.jmolDiffuseLightPercentage
     */
    JMOL_DIFFUSE_LIGHT_PERCENTAGE,
    /**
     * PreferenceBasic: this.jmolSpecularReflectionExponent
     */
    JMOL_SPECULAR_REFLECTION_EXPONENT,
    /**
     * PreferenceBasic: this.jmolSpecularReflectionPercentage
     */
    JMOL_SPECULAR_REFLECTION_PERCENTAGE,
    /**
     * PreferenceBasic: this.jmolSpecularReflectionPower
     */
    JMOL_SPECULAR_REFLECTION_POWER,
    /**
     * PreferenceBasic: this.numberOfParallelSimulations
     */
    NUMBER_OF_PARALLEL_SIMULATIONS,
    /**
     * PreferenceBasic: this.numberOfParallelSlicers
     */
    NUMBER_OF_PARALLEL_SLICERS,
    /**
     * PreferenceBasic: this.numberOfParallelCalculators
     */
    NUMBER_OF_PARALLEL_CALCULATORS,
    /**
     * PreferenceBasic: this.numberOfParallelParticlePositionWriters
     */
    NUMBER_OF_PARALLEL_PARTICLE_POSITION_WRITERS,
    /**
     * PreferenceBasic: this.numberOfAfterDecimalDigitsForParticlePositions
     */
    NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS,
    /**
     * PreferenceBasic: this.maximumNumberOfPositionCorrectionTrials
     */
    MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS,
    /**
     * PreferenceBasic: this.movieQuality
     */
    MOVIE_QUALITY,
    /**
     * PreferenceBasic: this.timerIntervalInMilliseconds
     */
    TIMER_INTERVALL_IN_MILLISECONDS,
    /**
     * PreferenceBasic: this.minimumBondLengthDpd
     */
    MINIMUM_BOND_LENGTH_DPD,
    /**
     * PreferenceBasic: this.maximumNumberOfParticlesForGraphicalDisplay
     */
    MAXIMUM_NUMBER_OF_PARTICLES_FOR_GRAPHICAL_DISPLAY,
    /**
     * PreferenceBasic: this.numberOfStepsForRdfCalculation
     */
    NUMBER_OF_STEPS_FOR_RDF_CALCULATION,
    /**
     * PreferenceBasic: this.numberOfTrialsForCompartment
     */
    NUMBER_OF_TRIALS_FOR_COMPARTMENT,
    /**
     * PreferenceBasic: this.animationSpeed
     */
    ANIMATION_SPEED,
    /**
     * PreferenceBasic: this.numberOfSimulationBoxCellsforParallelization
     */
    NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION,
    /**
     * PreferenceBasic: this.numberOfBondsforParallelization
     */
    NUMBER_OF_BONDS_FOR_PARALLELIZATION,
    /**
     * PreferenceBasic: this.numberOfStepsForJobRestart
     */
    NUMBER_OF_STEPS_FOR_JOB_RESTART,
    /**
     * PreferenceBasic: this.simulationBoxMagnificationPercentage
     */
    SIMULATION_BOX_MAGNIFICATION_PERCENTAGE,
    /**
     * PreferenceBasic: this.numberOfSpinSteps
     */
    NUMBER_OF_SPIN_STEPS,
    /**
     * PreferenceBasic: this.simulationMovieImagePath
     */
    SIMULATION_MOVIE_IMAGE_PATH,
    /**
     * PreferenceBasic: this.chartMovieImagePath
     */
    CHART_MOVIE_IMAGE_PATH,
    /**
     * Undefined
     */
    UNDEFINED;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Map of representation to enum value
     */
    private static HashMap<String, PreferenceEditableEnum> representationToPreferenceEditableEnumMap;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="toPreferenceEditableEnum()">
    /**
     * Returns PreferenceEditableEnum for name
     *
     * @param aName Name
     * @return PreferenceEditableEnum for name or
     * PreferenceEditableEnum.UNDEFINED if representation is not known
     */
    public static PreferenceEditableEnum toPreferenceEditableEnum(String aName) {
        // <editor-fold defaultstate="collapsed" desc="Initialize if necessary">
        if (PreferenceEditableEnum.representationToPreferenceEditableEnumMap == null) {
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap = new HashMap<String, PreferenceEditableEnum>(50);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JPEG_IMAGE_QUALITY.name(), PreferenceEditableEnum.JPEG_IMAGE_QUALITY);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.COLOR_TRANSPARENCY_COMPARTMENT.name(), PreferenceEditableEnum.COLOR_TRANSPARENCY_COMPARTMENT);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.COLOR_GRADIENT_ATTENUATION_COMPARTMENT.name(),
                    PreferenceEditableEnum.COLOR_GRADIENT_ATTENUATION_COMPARTMENT);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.COLOR_GRADIENT_ATTENUATION_SLICER.name(),
                    PreferenceEditableEnum.COLOR_GRADIENT_ATTENUATION_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.SPECULAR_WHITE_ATTENUATION_SLICER.name(),
                    PreferenceEditableEnum.SPECULAR_WHITE_ATTENUATION_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.COLOR_SHAPE_ATTENUATION_COMPARTMENT.name(),
                    PreferenceEditableEnum.COLOR_SHAPE_ATTENUATION_COMPARTMENT);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR.name(),
                    PreferenceEditableEnum.COMPARTMENT_BODY_CHANGE_RESPONSE_FACTOR);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.MAX_SELECTED_MOLECULE_NUMBER_SLICER.name(), PreferenceEditableEnum.MAX_SELECTED_MOLECULE_NUMBER_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.DEPTH_ATTENUATION_SLICER.name(), PreferenceEditableEnum.DEPTH_ATTENUATION_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.DELAY_FOR_FILES_IN_MILLISECONDS.name(), PreferenceEditableEnum.DELAY_FOR_FILES_IN_MILLISECONDS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.DELAY_FOR_JOB_START_IN_MILLISECONDS.name(), PreferenceEditableEnum.DELAY_FOR_JOB_START_IN_MILLISECONDS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.INTERNAL_MFSIM_JOB_PATH.name(), PreferenceEditableEnum.INTERNAL_MFSIM_JOB_PATH);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.INTERNAL_TEMP_PATH.name(), PreferenceEditableEnum.INTERNAL_TEMP_PATH);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION.name(),
                    PreferenceEditableEnum.IS_JOB_RESULT_ARCHIVE_STEP_FILE_INCLUSION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_VOLUME_SCALING_FOR_CONCENTRATION_CALCULATION.name(),PreferenceEditableEnum.IS_VOLUME_SCALING_FOR_CONCENTRATION_CALCULATION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_JOB_INPUT_INCLUSION.name(),PreferenceEditableEnum.IS_JOB_INPUT_INCLUSION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_PARTICLE_DISTRICUTION_INCLUSION.name(),PreferenceEditableEnum.IS_PARTICLE_DISTRICUTION_INCLUSION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_SIMULATION_STEP_INCLUSION.name(),PreferenceEditableEnum.IS_SIMULATION_STEP_INCLUSION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION.name(),PreferenceEditableEnum.IS_NEAREST_NEIGHBOR_EVALUATION_INCLUSION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND.name(),
                    PreferenceEditableEnum.IS_JOB_RESULT_ARCHIVE_PROCESS_PARALLEL_IN_BACKGROUND);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED.name(),
                    PreferenceEditableEnum.IS_JOB_RESULT_ARCHIVE_FILE_UNCOMPRESSED);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_JDPD_KERNEL_DOUBLE_PRECISION.name(), PreferenceEditableEnum.IS_JDPD_KERNEL_DOUBLE_PRECISION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_JDPD_LOG_LEVEL_EXCEPTION.name(), PreferenceEditableEnum.IS_JDPD_LOG_LEVEL_EXCEPTION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_CONSTANT_COMPARTMENT_BODY_VOLUME.name(), PreferenceEditableEnum.IS_CONSTANT_COMPARTMENT_BODY_VOLUME);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_SIMULATION_BOX_SLICER.name(), PreferenceEditableEnum.IS_SIMULATION_BOX_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_MOLECULE_DISPLAY_WITH_STANDARD_PARTICLE_SIZE.name(),
                    PreferenceEditableEnum.IS_MOLECULE_DISPLAY_WITH_STANDARD_PARTICLE_SIZE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_SINGLE_SLICE_DISPLAY.name(), PreferenceEditableEnum.IS_SINGLE_SLICE_DISPLAY);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.SLICER_GRAPHICS_MODE.name(), PreferenceEditableEnum.SLICER_GRAPHICS_MODE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.SIMULATION_BOX_BACKGROUND_COLOR_SLICER.name(),
                    PreferenceEditableEnum.SIMULATION_BOX_BACKGROUND_COLOR_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.MEASUREMENT_COLOR_SLICER.name(),
                    PreferenceEditableEnum.MEASUREMENT_COLOR_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.MOLECULE_SELECTION_COLOR_SLICER.name(),
                    PreferenceEditableEnum.MOLECULE_SELECTION_COLOR_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.FRAME_COLOR_SLICER.name(),
                    PreferenceEditableEnum.FRAME_COLOR_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JMOL_SIMULATION_BOX_BACKGROUND_COLOR.name(),
                    PreferenceEditableEnum.JMOL_SIMULATION_BOX_BACKGROUND_COLOR);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.PROTEIN_VIEWER_BACKGROUND_COLOR.name(),
                    PreferenceEditableEnum.PROTEIN_VIEWER_BACKGROUND_COLOR);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.BOX_VIEW_DISPLAY.name(), PreferenceEditableEnum.BOX_VIEW_DISPLAY);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IS_FRAME_DISPLAY_SLICER.name(), PreferenceEditableEnum.IS_FRAME_DISPLAY_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JOB_INPUT_FILTER_AFTER_TIMESTAMP.name(),
                    PreferenceEditableEnum.JOB_INPUT_FILTER_AFTER_TIMESTAMP);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JOB_INPUT_FILTER_BEFORE_TIMESTAMP.name(),
                    PreferenceEditableEnum.JOB_INPUT_FILTER_BEFORE_TIMESTAMP);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JOB_INPUT_FILTER_CONTAINS_PHRASE.name(),
                    PreferenceEditableEnum.JOB_INPUT_FILTER_CONTAINS_PHRASE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JOB_RESULT_FILTER_AFTER_TIMESTAMP.name(),
                    PreferenceEditableEnum.JOB_RESULT_FILTER_AFTER_TIMESTAMP);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JOB_RESULT_FILTER_BEFORE_TIMESTAMP.name(),
                    PreferenceEditableEnum.JOB_RESULT_FILTER_BEFORE_TIMESTAMP);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JOB_RESULT_FILTER_CONTAINS_PHRASE.name(),
                    PreferenceEditableEnum.JOB_RESULT_FILTER_CONTAINS_PHRASE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_SLICES.name(), PreferenceEditableEnum.NUMBER_OF_SLICES);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JMOL_SHADE_POWER.name(), PreferenceEditableEnum.JMOL_SHADE_POWER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JMOL_AMBIENT_LIGHT_PERCENTAGE.name(), PreferenceEditableEnum.JMOL_AMBIENT_LIGHT_PERCENTAGE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JMOL_DIFFUSE_LIGHT_PERCENTAGE.name(), PreferenceEditableEnum.JMOL_DIFFUSE_LIGHT_PERCENTAGE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JMOL_SPECULAR_REFLECTION_EXPONENT.name(),
                    PreferenceEditableEnum.JMOL_SPECULAR_REFLECTION_EXPONENT);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JMOL_SPECULAR_REFLECTION_PERCENTAGE.name(),
                    PreferenceEditableEnum.JMOL_SPECULAR_REFLECTION_PERCENTAGE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.JMOL_SPECULAR_REFLECTION_POWER.name(), PreferenceEditableEnum.JMOL_SPECULAR_REFLECTION_POWER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_PARALLEL_SIMULATIONS.name(), PreferenceEditableEnum.NUMBER_OF_PARALLEL_SIMULATIONS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_PARALLEL_SLICERS.name(), PreferenceEditableEnum.NUMBER_OF_PARALLEL_SLICERS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_PARALLEL_CALCULATORS.name(), PreferenceEditableEnum.NUMBER_OF_PARALLEL_CALCULATORS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_PARALLEL_PARTICLE_POSITION_WRITERS.name(), PreferenceEditableEnum.NUMBER_OF_PARALLEL_PARTICLE_POSITION_WRITERS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS.name(), PreferenceEditableEnum.NUMBER_OF_AFTER_DECIMAL_SEPARATOR_DIGITS_FOR_PARTICLE_POSITIONS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS.name(), PreferenceEditableEnum.MAXIMUM_NUMBER_OF_POSITION_CORRECTION_TRIALS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.MOVIE_QUALITY.name(), PreferenceEditableEnum.MOVIE_QUALITY);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.TIMER_INTERVALL_IN_MILLISECONDS.name(), PreferenceEditableEnum.TIMER_INTERVALL_IN_MILLISECONDS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.MINIMUM_BOND_LENGTH_DPD.name(), PreferenceEditableEnum.MINIMUM_BOND_LENGTH_DPD);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.MAXIMUM_NUMBER_OF_PARTICLES_FOR_GRAPHICAL_DISPLAY.name(), PreferenceEditableEnum.MAXIMUM_NUMBER_OF_PARTICLES_FOR_GRAPHICAL_DISPLAY);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_STEPS_FOR_RDF_CALCULATION.name(), PreferenceEditableEnum.NUMBER_OF_STEPS_FOR_RDF_CALCULATION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_TRIALS_FOR_COMPARTMENT.name(), PreferenceEditableEnum.NUMBER_OF_TRIALS_FOR_COMPARTMENT);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.SIMULATION_BOX_MAGNIFICATION_PERCENTAGE.name(),
                    PreferenceEditableEnum.SIMULATION_BOX_MAGNIFICATION_PERCENTAGE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_SPIN_STEPS.name(), PreferenceEditableEnum.NUMBER_OF_SPIN_STEPS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.SHIFTS_SLICER.name(), PreferenceEditableEnum.SHIFTS_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.CUSTOM_DIALOG_SIZE.name(), PreferenceEditableEnum.CUSTOM_DIALOG_SIZE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.ROTATION_ANGLES.name(), PreferenceEditableEnum.ROTATION_ANGLES);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.PARTICLE_SHIFTS.name(), PreferenceEditableEnum.PARTICLE_SHIFTS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.STEP_INFO_ARRAY_SLICER.name(), PreferenceEditableEnum.STEP_INFO_ARRAY_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION.name(),
                    PreferenceEditableEnum.RADIAL_GRADIENT_PAINT_RADIUS_MAGNIFICATION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.SPECULAR_WHITE_SIZE_SLICER.name(), PreferenceEditableEnum.SPECULAR_WHITE_SIZE_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.RADIAL_GRADIENT_PAINT_FOCUS_FACTORS.name(),
                    PreferenceEditableEnum.RADIAL_GRADIENT_PAINT_FOCUS_FACTORS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.IMAGE_STORAGE_MODE.name(), PreferenceEditableEnum.IMAGE_STORAGE_MODE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLE_COLOR_DISPLAY_MODE",
                    PreferenceEditableEnum.PARTICLE_COLOR_DISPLAY_MODE);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.ANIMATION_SPEED.name(), PreferenceEditableEnum.ANIMATION_SPEED);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION.name(), PreferenceEditableEnum.NUMBER_OF_SIMULATION_BOX_CELLS_FOR_PARALLELIZATION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_BONDS_FOR_PARALLELIZATION.name(), PreferenceEditableEnum.NUMBER_OF_BONDS_FOR_PARALLELIZATION);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.SIMULATION_MOVIE_IMAGE_PATH.name(), PreferenceEditableEnum.SIMULATION_MOVIE_IMAGE_PATH);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.CHART_MOVIE_IMAGE_PATH.name(), PreferenceEditableEnum.CHART_MOVIE_IMAGE_PATH);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_STEPS_FOR_JOB_RESTART.name(), PreferenceEditableEnum.NUMBER_OF_STEPS_FOR_JOB_RESTART);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.FIRST_SLICE_INDEX.name(), PreferenceEditableEnum.FIRST_SLICE_INDEX);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_ZOOM_VOLUME_BINS.name(), PreferenceEditableEnum.NUMBER_OF_ZOOM_VOLUME_BINS);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.NUMBER_OF_FRAME_POINTS_SLICER.name(), PreferenceEditableEnum.NUMBER_OF_FRAME_POINTS_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.TIME_STEP_DISPLAY_SLICER.name(), PreferenceEditableEnum.TIME_STEP_DISPLAY_SLICER);
            PreferenceEditableEnum.representationToPreferenceEditableEnumMap.put(PreferenceEditableEnum.CURRENT_PARTICLE_SET_FILENAME.name(), PreferenceEditableEnum.CURRENT_PARTICLE_SET_FILENAME);
        }
        // </editor-fold>
        if (PreferenceEditableEnum.representationToPreferenceEditableEnumMap.containsKey(aName)) {
            return PreferenceEditableEnum.representationToPreferenceEditableEnumMap.get(aName);
        } else {
            return PreferenceEditableEnum.UNDEFINED;
        }
    }
    // </editor-fold>

}
