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

import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumStatus;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.util.StandardColorEnum;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.TimeUtilityMethods;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.body.BodyXyLayerSingleSurfaceEnum;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticleInfo;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticle;
import de.gnwi.mfsim.model.graphics.body.BodySphere;
import de.gnwi.mfsim.model.graphics.body.BodyInterface;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.model.graphics.body.BodyXyLayer;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePosition;
import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.mfsim.model.particleStructure.SpicesPool;
import de.gnwi.spices.SpicesConstants;
import de.gnwi.spices.ParticleFrequency;
import de.gnwi.jdpd.utilities.FileOutputStrings;
import de.gnwi.jdpd.utilities.Strings;
import de.gnwi.mfsim.model.peptide.PdbToDpd;
import de.gnwi.mfsim.model.peptide.PdbToDpdPool;
import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import de.gnwi.mfsim.model.graphics.particle.IGraphicalParticle;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.jdpd.interfaces.IRandom;
import de.gnwi.mfsim.model.util.IdKeyValue;
import de.gnwi.mfsim.model.util.IdKeyValueAccumulator;

/**
 * Job utility methods to be instantiated
 *
 * @author Achim Zielesny
 */
public class JobUtilityMethods {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Miscellaneous utility methods
     */
    private MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();

    /**
     * Utility graphics methods
     */
    private GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Utility for time operations
     */
    private final TimeUtilityMethods timeUtilityMethods = new TimeUtilityMethods();

    /**
     * Pattern for single particle to match
     */
    private Pattern particlePattern = Pattern.compile(ModelDefinitions.PARTICLE_REGEX_PATTERN_STRING);

    //
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Mathematical constant
     */
    private static final double FOUR_PI_DIVIDED_BY_THREE = 4.0 * Math.PI / 3.0;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public JobUtilityMethods() {
        // Do nothing
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Conversion from DPD to physical units related methods">
    /**
     * Returns length conversion factor from DPD length to physical length in
     * Angstrom (since particle volumes are in Angstrom^3), usage: Physical
     * length in Angstrom = DPD-Length *
     * getLengthConversionFactorFromDpdToPhysicalLength()
     *
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity and Density value
     * item (value items are NOT changed)
     * @return Length conversion factor from DPD length to physical
     * length in Angstrom or -1.0 if value could not be calculated
     */
    public double getLengthConversionFactorFromDpdToPhysicalLength(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1.0;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get necessary value items">
        ValueItem tmpParticleTableValueItem = aJobInputValueItemContainer.getValueItem("ParticleTable");
        ValueItem tmpMonomerTableValueItem = aJobInputValueItemContainer.getValueItem("MonomerTable");
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        ValueItem tmpQuantityValueItem = aJobInputValueItemContainer.getValueItem("Quantity");
        ValueItem tmpDensityValueItem = aJobInputValueItemContainer.getValueItem("Density");
        if (tmpParticleTableValueItem == null || tmpMonomerTableValueItem == null || tmpMoleculeTableValueItem == null || tmpQuantityValueItem == null || tmpDensityValueItem == null) {
            return -1.0;
        }
        // </editor-fold>
        return this.getLengthConversionFactorFromDpdToPhysicalLength(
            tmpParticleTableValueItem,
            tmpMonomerTableValueItem,
            tmpMoleculeTableValueItem,
            tmpQuantityValueItem,
            tmpDensityValueItem
        );
    }

    /**
     * Returns length conversion factor from DPD length to physical length in
     * Angstrom (since particle volumes are in Angstrom^3), usage: Physical
     * length in Angstrom = DPD-Length *
     * getLengthConversionFactorFromDpdToPhysicalLength())
     *
     * @param aParticleTableValueItem ParticleTable value item (is NOT
     * changed)
     * @param aMonomerTableValueItem MonomerTable value item (is NOT changed)
     * @param aMoleculeTableValueItem MoleculeTable value item (is NOT changed)
     * @param aQuantityValueItem Quantity value item (is NOT changed)
     * @param aDensityValueItem Density value item (is NOT changed)
     * @return Length conversion factor from DPD length to physical
     * length in Angstrom or -1.0 if value could not be calculated
     */
    public double getLengthConversionFactorFromDpdToPhysicalLength(
            ValueItem aParticleTableValueItem,
            ValueItem aMonomerTableValueItem,
            ValueItem aMoleculeTableValueItem,
            ValueItem aQuantityValueItem,
            ValueItem aDensityValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return -1.0;
        }
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return -1.0;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return -1.0;
        }
        if (aQuantityValueItem == null || !aQuantityValueItem.getName().equals("Quantity")) {
            return -1.0;
        }
        if (aDensityValueItem == null || !aDensityValueItem.getName().equals("Density")) {
            return -1.0;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Replace monomer shortcuts">
        ValueItem tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem;
        if (aMonomerTableValueItem.isActive()) {
            // First clone molecules value item
            tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem.getClone();
            // Second replace in cloned molecules value item
            this.replaceMonomerShortcutsInMolecularStructure(tmpMoleculesWithoutMonomerShortcutsValueItem, aMonomerTableValueItem);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate total particle frequencies">
        int tmpTotalNumberOfParticlesOfSimulation = 0;
        HashMap<String, ParticleFrequency> tmpParticleToTotalFrequencyMap = new HashMap<String, ParticleFrequency>(aParticleTableValueItem.getMatrixRowCount());
        for (int i = 0; i < tmpMoleculesWithoutMonomerShortcutsValueItem.getMatrixRowCount(); i++) {
            // Old code:
            // Molecule tmpMolecule = new Molecule(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1), // Structure
            //      tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0) // Molecule_Name
            // );
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1));
            // NOTE: A row in tmpMoleculesWithoutMonomerShortcutsValueItem corresponds to a row in aQuantityValueItem
            int tmpMoleculeFrequency = aQuantityValueItem.getValueAsInt(i, 1);
            ParticleFrequency[] tmpParticleFrequencies = tmpSpices.getParticleFrequencies();
            for (ParticleFrequency tmpSingleParticleFrequency : tmpParticleFrequencies) {
                if (tmpParticleToTotalFrequencyMap.containsKey(tmpSingleParticleFrequency.getParticle())) {
                    ParticleFrequency tmpTotalFrequency = tmpParticleToTotalFrequencyMap.get(tmpSingleParticleFrequency.getParticle());
                    tmpTotalFrequency.addFrequency(tmpSingleParticleFrequency.getFrequency() * tmpMoleculeFrequency);
                } else {
                    tmpParticleToTotalFrequencyMap.put(tmpSingleParticleFrequency.getParticle(), new ParticleFrequency(tmpSingleParticleFrequency.getParticle(),
                            tmpSingleParticleFrequency.getFrequency() * tmpMoleculeFrequency));
                }
            }
            tmpTotalNumberOfParticlesOfSimulation += tmpSpices.getTotalNumberOfParticles() * tmpMoleculeFrequency;
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine smallest volume and calculate Napparent">
        HashMap<String, Double> tmpParticleToVolumeMap = new HashMap<String, Double>(aParticleTableValueItem.getMatrixRowCount());
        double tmpSmallestVolume = Double.MAX_VALUE;
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            String tmpParticle = aParticleTableValueItem.getValue(i, 0);
            double tmpVolume = aParticleTableValueItem.getValueAsDouble(i, 5);
            tmpParticleToVolumeMap.put(tmpParticle, Double.valueOf(tmpVolume));
            if (tmpVolume < tmpSmallestVolume) {
                tmpSmallestVolume = tmpVolume;
            }
        }
        double tmpTotalWeightedParticleSum = 0.0;
        for (ParticleFrequency tmpSingleTotalParticleFrequency : tmpParticleToTotalFrequencyMap.values()) {
            tmpTotalWeightedParticleSum += (double) tmpSingleTotalParticleFrequency.getFrequency() * tmpParticleToVolumeMap.get(tmpSingleTotalParticleFrequency.getParticle()).doubleValue()
                    / tmpSmallestVolume;
        }
        double tmpNapparent = tmpTotalWeightedParticleSum / tmpTotalNumberOfParticlesOfSimulation;
        // </editor-fold>
        double tmpDensity = aDensityValueItem.getValueAsDouble();
        return Math.cbrt(tmpSmallestVolume * tmpNapparent * tmpDensity);
    }
    
    /**
     * Returns time conversion factor from DPD time to physical time in
     * nanoseconds (ns), usage: time in ns = DPD-time *
     * getTimeConversionFactorFromDpdToPhysicalTime()
     *
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity, Density and
     * Temperature value item (value items are NOT changed)
     * @return Returns time conversion factor from DPD time to physical time in
     * nanoseconds (ns) or -1.0 if value could not be calculated
     */
    public double getTimeConversionFactorFromDpdToPhysicalTime(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1.0;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get necessary value items">
        ValueItem tmpParticleTableValueItem = aJobInputValueItemContainer.getValueItem("ParticleTable");
        ValueItem tmpMonomerTableValueItem = aJobInputValueItemContainer.getValueItem("MonomerTable");
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        ValueItem tmpQuantityValueItem = aJobInputValueItemContainer.getValueItem("Quantity");
        ValueItem tmpDensityValueItem = aJobInputValueItemContainer.getValueItem("Density");
        ValueItem tmpTemperatureValueItem = aJobInputValueItemContainer.getValueItem("Temperature");
        if (tmpParticleTableValueItem == null || tmpMonomerTableValueItem == null || tmpMoleculeTableValueItem == null || tmpQuantityValueItem == null || tmpDensityValueItem == null
                || tmpTemperatureValueItem == null) {
            return -1.0;
        }
        // </editor-fold>
        return this.getTimeConversionFactorFromDpdToPhysicalTime(
            tmpParticleTableValueItem,
            tmpMonomerTableValueItem,
            tmpMoleculeTableValueItem,
            tmpQuantityValueItem,
            tmpDensityValueItem,
            tmpTemperatureValueItem
        );
    }

    /**
     * Returns time conversion factor from DPD time to physical time in
     * nanoseconds (ns), usage: time in ns = DPD-time *
     * getTimeConversionFactorFromDpdToPhysicalTime()
     *
     * @param aParticleTableValueItem ParticleTable value item (is NOT
     * changed)
     * @param aMonomerTableValueItem MonomerTable value item (is NOT changed)
     * @param aMoleculeTableValueItem MoleculeTable value item (is NOT changed)
     * @param aQuantityValueItem Quantity value item (is NOT changed)
     * @param aDensityValueItem Density value item (is NOT changed)
     * @param aTemperatureValueItem Temperature value item (is NOT changed)
     * @return Time conversion factor from DPD time to physical time in
     * nanoseconds (ns) or -1.0 if value could not be calculated
     */
    public double getTimeConversionFactorFromDpdToPhysicalTime(
        ValueItem aParticleTableValueItem,
        ValueItem aMonomerTableValueItem,
        ValueItem aMoleculeTableValueItem,
        ValueItem aQuantityValueItem,
        ValueItem aDensityValueItem,
        ValueItem aTemperatureValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return -1.0;
        }
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return -1.0;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return -1.0;
        }
        if (aQuantityValueItem == null || !aQuantityValueItem.getName().equals("Quantity")) {
            return -1.0;
        }
        if (aDensityValueItem == null || !aDensityValueItem.getName().equals("Density")) {
            return -1.0;
        }
        if (aTemperatureValueItem == null || !aTemperatureValueItem.getName().equals("Temperature")) {
            return -1.0;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Replace monomer shortcuts">
        ValueItem tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem;
        if (aMonomerTableValueItem.isActive()) {
            // First clone molecules value item
            tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem.getClone();
            // Second replace in cloned molecules value item
            this.replaceMonomerShortcutsInMolecularStructure(tmpMoleculesWithoutMonomerShortcutsValueItem, aMonomerTableValueItem);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate total particle frequencies">
        int tmpTotalNumberOfParticlesOfSimulation = 0;
        HashMap<String, ParticleFrequency> tmpParticleToTotalFrequencyMap = new HashMap<String, ParticleFrequency>(aParticleTableValueItem.getMatrixRowCount());
        for (int i = 0; i < tmpMoleculesWithoutMonomerShortcutsValueItem.getMatrixRowCount(); i++) {
            // Old code:
            // Molecule tmpMolecule = new Molecule(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1), // Structure
            //        tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0) // Molecule_Name
            // );
            String tmpMolecularStructureString = tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1);
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
            // NOTE: A row in tmpMoleculesWithoutMonomerShortcutsValueItem corresponds to a row in aQuantityValueItem
            int tmpMoleculeFrequency = aQuantityValueItem.getValueAsInt(i, 1);
            ParticleFrequency[] tmpParticleFrequencies = tmpSpices.getParticleFrequencies();
            for (ParticleFrequency tmpSingleParticleFrequency : tmpParticleFrequencies) {
                if (tmpParticleToTotalFrequencyMap.containsKey(tmpSingleParticleFrequency.getParticle())) {
                    ParticleFrequency tmpTotalFrequency = tmpParticleToTotalFrequencyMap.get(tmpSingleParticleFrequency.getParticle());
                    tmpTotalFrequency.addFrequency(tmpSingleParticleFrequency.getFrequency() * tmpMoleculeFrequency);
                } else {
                    tmpParticleToTotalFrequencyMap.put(tmpSingleParticleFrequency.getParticle(), new ParticleFrequency(tmpSingleParticleFrequency.getParticle(),
                            tmpSingleParticleFrequency.getFrequency() * tmpMoleculeFrequency));
                }
            }
            tmpTotalNumberOfParticlesOfSimulation += tmpSpices.getTotalNumberOfParticles() * tmpMoleculeFrequency;
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine necessary sums">
        HashMap<String, Double> tmpParticleToVolumeMap = new HashMap<String, Double>(aParticleTableValueItem.getMatrixRowCount());
        HashMap<String, Double> tmpParticleToMolWeightMap = new HashMap<String, Double>(aParticleTableValueItem.getMatrixRowCount());
        double tmpSmallestVolume = Double.MAX_VALUE;
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            String tmpParticle = aParticleTableValueItem.getValue(i, 0);
            // NOTE: Molecular weight is in g/mol
            double tmpMolWeigth = aParticleTableValueItem.getValueAsDouble(i, 4);
            tmpParticleToMolWeightMap.put(tmpParticle, Double.valueOf(tmpMolWeigth));
            // NOTE: Volume is in A^3
            double tmpVolume = aParticleTableValueItem.getValueAsDouble(i, 5);
            tmpParticleToVolumeMap.put(tmpParticle, Double.valueOf(tmpVolume));
            if (tmpVolume < tmpSmallestVolume) {
                tmpSmallestVolume = tmpVolume;
            }
        }
        double tmpTotalMolWeightSum = 0.0;
        double tmpTotalWeightedParticleSum = 0.0;
        for (ParticleFrequency tmpSingleTotalParticleFrequency : tmpParticleToTotalFrequencyMap.values()) {
            tmpTotalMolWeightSum += (double) tmpSingleTotalParticleFrequency.getFrequency() * tmpParticleToMolWeightMap.get(tmpSingleTotalParticleFrequency.getParticle()).doubleValue();
            tmpTotalWeightedParticleSum += (double) tmpSingleTotalParticleFrequency.getFrequency() * tmpParticleToVolumeMap.get(tmpSingleTotalParticleFrequency.getParticle()).doubleValue()
                    / tmpSmallestVolume;
        }
        // NOTE: tmpAverageMolWeight is in g/mol
        double tmpAverageMolWeight = tmpTotalMolWeightSum / tmpTotalNumberOfParticlesOfSimulation;
        double tmpNapparent = tmpTotalWeightedParticleSum / tmpTotalNumberOfParticlesOfSimulation;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set length conversion factor">
        double tmpDensity = aDensityValueItem.getValueAsDouble();
        // Note: tmpLengthConversionFactor is in A (since volume is in A^3)
        double tmpLengthConversionFactor = Math.cbrt(tmpSmallestVolume * tmpNapparent * tmpDensity);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set time conversion factor">
        // Note: tmpTemperature is in K
        double tmpTemperature = aTemperatureValueItem.getValueAsDouble();
        // Note: tmpTemperature*ModelDefinitions.GAS_CONSTANT is in J/mol = kg*m^2/(mol*s^2), tmpAverageMolWeight is in g/mol
        //       Factor 0.001 converts g to kg for tmpAverageMolWeight
        //       Result: tmpRootFactor is in s/m
        double tmpRootFactor = Math.sqrt(tmpAverageMolWeight * 0.001 / (tmpTemperature * ModelDefinitions.GAS_CONSTANT));
        // Note: tmpLengthConversionFactor is in A = 10^-10 m, tmpRootFactor is in s/m = (10^12 ps)/(10^10 A) = 100 ps/A
        //       Result: tmpTimeConversionFactor is in ps
        double tmpTimeConversionFactor = tmpLengthConversionFactor * 100.0 * tmpRootFactor;
        // The "true" or "effective" simulation time due to soft cores without caging effects is around 1000 times faster: tmpTimeConversionFactor is in ns
        // </editor-fold>
        return tmpTimeConversionFactor;
    }

    
    /**
     * Returns time conversion factors from DPD length and time to physical 
     * length in Angstrom and time in nanoseconds (ns), 
     * usage:
     * Physical length in Angstrom = DPD-Length * result[0]
     * Physical time in ns = DPD-time * result[1]
     *
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity, Density and
     * Temperature value item (value items are NOT changed)
     * @return Returns time conversion factor from DPD time to physical time in
     * nanoseconds (ns) or -1.0 if value could not be calculated
     */
    public double[] getLengthAndTimeConversionFactorsFromDpdToPhysicalUnits(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return new double[]{-1.0, -1.0};
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get necessary value items">
        ValueItem tmpParticleTableValueItem = aJobInputValueItemContainer.getValueItem("ParticleTable");
        ValueItem tmpMonomerTableValueItem = aJobInputValueItemContainer.getValueItem("MonomerTable");
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        ValueItem tmpQuantityValueItem = aJobInputValueItemContainer.getValueItem("Quantity");
        ValueItem tmpDensityValueItem = aJobInputValueItemContainer.getValueItem("Density");
        ValueItem tmpTemperatureValueItem = aJobInputValueItemContainer.getValueItem("Temperature");
        if (tmpParticleTableValueItem == null || tmpMonomerTableValueItem == null || tmpMoleculeTableValueItem == null || tmpQuantityValueItem == null || tmpDensityValueItem == null
                || tmpTemperatureValueItem == null) {
            return new double[]{-1.0, -1.0};
        }
        // </editor-fold>
        return this.getLengthAndTimeConversionFactorsFromDpdToPhysicalUnits(
            tmpParticleTableValueItem,
            tmpMonomerTableValueItem,
            tmpMoleculeTableValueItem,
            tmpQuantityValueItem,
            tmpDensityValueItem,
            tmpTemperatureValueItem
        );
    }

    /**
     * Returns time conversion factors from DPD length and time to physical 
     * length in Angstrom and time in nanoseconds (ns), 
     * usage:
     * Physical length in Angstrom = DPD-Length * result[0]
     * Physical time in ns = DPD-time * result[1]
     *
     * @param aParticleTableValueItem ParticleTable value item (is NOT
     * changed)
     * @param aMonomerTableValueItem MonomerTable value item (is NOT changed)
     * @param aMoleculeTableValueItem MoleculeTable value item (is NOT changed)
     * @param aQuantityValueItem Quantity value item (is NOT changed)
     * @param aDensityValueItem Density value item (is NOT changed)
     * @param aTemperatureValueItem Temperature value item (is NOT changed)
     * @return Double array of length 2 with: result[0] = length conversion 
     * factor from DPD length to physical length in Angstrom or -1.0 if value 
     * could not be calculated, result[1] = time conversion factor from DPD 
     * time to physical time in nanoseconds (ns) or -1.0 if value could not be
     * calculated
     */
    public double[] getLengthAndTimeConversionFactorsFromDpdToPhysicalUnits(
        ValueItem aParticleTableValueItem,
        ValueItem aMonomerTableValueItem,
        ValueItem aMoleculeTableValueItem,
        ValueItem aQuantityValueItem,
        ValueItem aDensityValueItem,
        ValueItem aTemperatureValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return new double[]{-1.0, -1.0};
        }
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return new double[]{-1.0, -1.0};
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return new double[]{-1.0, -1.0};
        }
        if (aQuantityValueItem == null || !aQuantityValueItem.getName().equals("Quantity")) {
            return new double[]{-1.0, -1.0};
        }
        if (aDensityValueItem == null || !aDensityValueItem.getName().equals("Density")) {
            return new double[]{-1.0, -1.0};
        }
        if (aTemperatureValueItem == null || !aTemperatureValueItem.getName().equals("Temperature")) {
            return new double[]{-1.0, -1.0};
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Replace monomer shortcuts">
        ValueItem tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem;
        if (aMonomerTableValueItem.isActive()) {
            // First clone molecules value item
            tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem.getClone();
            // Second replace in cloned molecules value item
            this.replaceMonomerShortcutsInMolecularStructure(tmpMoleculesWithoutMonomerShortcutsValueItem, aMonomerTableValueItem);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate total particle frequencies">
        int tmpTotalNumberOfParticlesOfSimulation = 0;
        HashMap<String, ParticleFrequency> tmpParticleToTotalFrequencyMap = new HashMap<String, ParticleFrequency>(aParticleTableValueItem.getMatrixRowCount());
        for (int i = 0; i < tmpMoleculesWithoutMonomerShortcutsValueItem.getMatrixRowCount(); i++) {
            // Old code:
            // Molecule tmpMolecule = new Molecule(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1), // Structure
            //        tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0) // Molecule_Name
            // );
            String tmpMolecularStructureString = tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1);
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
            // NOTE: A row in tmpMoleculesWithoutMonomerShortcutsValueItem corresponds to a row in aQuantityValueItem
            int tmpMoleculeFrequency = aQuantityValueItem.getValueAsInt(i, 1);
            ParticleFrequency[] tmpParticleFrequencies = tmpSpices.getParticleFrequencies();
            for (ParticleFrequency tmpSingleParticleFrequency : tmpParticleFrequencies) {
                if (tmpParticleToTotalFrequencyMap.containsKey(tmpSingleParticleFrequency.getParticle())) {
                    ParticleFrequency tmpTotalFrequency = tmpParticleToTotalFrequencyMap.get(tmpSingleParticleFrequency.getParticle());
                    tmpTotalFrequency.addFrequency(tmpSingleParticleFrequency.getFrequency() * tmpMoleculeFrequency);
                } else {
                    tmpParticleToTotalFrequencyMap.put(tmpSingleParticleFrequency.getParticle(), new ParticleFrequency(tmpSingleParticleFrequency.getParticle(),
                            tmpSingleParticleFrequency.getFrequency() * tmpMoleculeFrequency));
                }
            }
            tmpTotalNumberOfParticlesOfSimulation += tmpSpices.getTotalNumberOfParticles() * tmpMoleculeFrequency;
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine necessary sums">
        HashMap<String, Double> tmpParticleToVolumeMap = new HashMap<String, Double>(aParticleTableValueItem.getMatrixRowCount());
        HashMap<String, Double> tmpParticleToMolWeightMap = new HashMap<String, Double>(aParticleTableValueItem.getMatrixRowCount());
        double tmpSmallestVolume = Double.MAX_VALUE;
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            String tmpParticle = aParticleTableValueItem.getValue(i, 0);
            // NOTE: Molecular weight is in g/mol
            double tmpMolWeigth = aParticleTableValueItem.getValueAsDouble(i, 4);
            tmpParticleToMolWeightMap.put(tmpParticle, Double.valueOf(tmpMolWeigth));
            // NOTE: Volume is in A^3
            double tmpVolume = aParticleTableValueItem.getValueAsDouble(i, 5);
            tmpParticleToVolumeMap.put(tmpParticle, Double.valueOf(tmpVolume));
            if (tmpVolume < tmpSmallestVolume) {
                tmpSmallestVolume = tmpVolume;
            }
        }
        double tmpTotalMolWeightSum = 0.0;
        double tmpTotalWeightedParticleSum = 0.0;
        for (ParticleFrequency tmpSingleTotalParticleFrequency : tmpParticleToTotalFrequencyMap.values()) {
            tmpTotalMolWeightSum += (double) tmpSingleTotalParticleFrequency.getFrequency() * tmpParticleToMolWeightMap.get(tmpSingleTotalParticleFrequency.getParticle()).doubleValue();
            tmpTotalWeightedParticleSum += (double) tmpSingleTotalParticleFrequency.getFrequency() * tmpParticleToVolumeMap.get(tmpSingleTotalParticleFrequency.getParticle()).doubleValue()
                    / tmpSmallestVolume;
        }
        // NOTE: tmpAverageMolWeight is in g/mol
        double tmpAverageMolWeight = tmpTotalMolWeightSum / tmpTotalNumberOfParticlesOfSimulation;
        double tmpNapparent = tmpTotalWeightedParticleSum / tmpTotalNumberOfParticlesOfSimulation;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set length conversion factor">
        double tmpDensity = aDensityValueItem.getValueAsDouble();
        // Note: tmpLengthConversionFactor is in A (since volume is in A^3)
        double tmpLengthConversionFactor = Math.cbrt(tmpSmallestVolume * tmpNapparent * tmpDensity);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set time conversion factor">
        // Note: tmpTemperature is in K
        double tmpTemperature = aTemperatureValueItem.getValueAsDouble();
        // Note: tmpTemperature*ModelDefinitions.GAS_CONSTANT is in J/mol = kg*m^2/(mol*s^2), tmpAverageMolWeight is in g/mol
        //       Factor 0.001 converts g to kg for tmpAverageMolWeight
        //       Result: tmpRootFactor is in s/m
        double tmpRootFactor = Math.sqrt(tmpAverageMolWeight * 0.001 / (tmpTemperature * ModelDefinitions.GAS_CONSTANT));
        // Note: tmpLengthConversionFactor is in A = 10^-10 m, tmpRootFactor is in s/m = (10^12 ps)/(10^10 A) = 100 ps/A
        //       Result: tmpTimeConversionFactor is in ps
        double tmpTimeConversionFactor = tmpLengthConversionFactor * 100.0 * tmpRootFactor;
        // The "true" or "effective" simulation time due to soft cores without caging effects is around 1000 times faster: tmpTimeConversionFactor is in ns
        // </editor-fold>
        return new double[]{tmpLengthConversionFactor, tmpTimeConversionFactor};
    }

    /**
     * Returns single step length in nanoseconds.
     *
     * @param aJobInputValueItemContainer JobInput value item container
     * @return Single step length in nanoseconds or "-1.0" if single step length
     * can not be calculated.
     */
    public double getSingleStepLengthInNanoseconds(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1.0;
        }
        // </editor-fold>
        double tmpTimeConversionFactor = this.getTimeConversionFactorFromDpdToPhysicalTime(aJobInputValueItemContainer);
        double tmpTimeStepLength = aJobInputValueItemContainer.getValueItem("TimeStepLength").getValueAsDouble(0, 0);
        double tmpStepInNanoseconds = -1.0;
        if (tmpTimeConversionFactor >= 0.0 && tmpTimeStepLength >= 0.0) {
            tmpStepInNanoseconds = tmpTimeStepLength * tmpTimeConversionFactor;
        }
        return tmpStepInNanoseconds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Electrostatics related methods">
    /**
     * Returns dimensionless electrostatics coupling constant.
     *
     * @param aJobInputValueItemContainer JobInput value item container
     * @return Dimensionless electrostatics coupling constant or "-1.0" if constant 
     * can not be calculated.
     */
    public double getElectrostaticsCouplingConstant(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1.0;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get necessary value items">
        ValueItem tmpParticleTableValueItem = aJobInputValueItemContainer.getValueItem("ParticleTable");
        ValueItem tmpMonomerTableValueItem = aJobInputValueItemContainer.getValueItem("MonomerTable");
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        ValueItem tmpQuantityValueItem = aJobInputValueItemContainer.getValueItem("Quantity");
        ValueItem tmpDensityValueItem = aJobInputValueItemContainer.getValueItem("Density");
        ValueItem tmpTemperatureValueItem = aJobInputValueItemContainer.getValueItem("Temperature");
        if (tmpParticleTableValueItem == null || 
            tmpMonomerTableValueItem == null || 
            tmpMoleculeTableValueItem == null || 
            tmpQuantityValueItem == null || 
            tmpDensityValueItem == null || 
            tmpTemperatureValueItem == null
        ) {
            return -1.0;
        }
        // </editor-fold>
        return this.getElectrostaticsCouplingConstant(
            tmpParticleTableValueItem,
            tmpMonomerTableValueItem,
            tmpMoleculeTableValueItem,
            tmpQuantityValueItem,
            tmpDensityValueItem,
            tmpTemperatureValueItem
        );
    }
    
    /**
     * Returns dimensionless electrostatics coupling constant.
     *
     * @param aParticleTableValueItem ParticleTable value item (is NOT
     * changed)
     * @param aMonomerTableValueItem MonomerTable value item (is NOT changed)
     * @param aMoleculeTableValueItem MoleculeTable value item (is NOT changed)
     * @param aQuantityValueItem Quantity value item (is NOT changed)
     * @param aDensityValueItem Density value item (is NOT changed)
     * @param aTemperatureValueItem Temperature value item (is NOT changed)
     * @return Dimensionless electrostatics coupling constant or "-1.0" if constant 
     * can not be calculated.
     */
    public double getElectrostaticsCouplingConstant(
        ValueItem aParticleTableValueItem,
        ValueItem aMonomerTableValueItem,
        ValueItem aMoleculeTableValueItem,
        ValueItem aQuantityValueItem,
        ValueItem aDensityValueItem,
        ValueItem aTemperatureValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return -1.0;
        }
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return -1.0;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return -1.0;
        }
        if (aQuantityValueItem == null || !aQuantityValueItem.getName().equals("Quantity")) {
            return -1.0;
        }
        if (aDensityValueItem == null || !aDensityValueItem.getName().equals("Density")) {
            return -1.0;
        }
        if (aTemperatureValueItem == null || !aTemperatureValueItem.getName().equals("Temperature")) {
            return -1.0;
        }
        // </editor-fold>
        double tmpRcutoffInAngstrom = 
            this.getLengthConversionFactorFromDpdToPhysicalLength(
                aParticleTableValueItem,
                aMonomerTableValueItem,
                aMoleculeTableValueItem,
                aQuantityValueItem,
                aDensityValueItem
            );
        if (tmpRcutoffInAngstrom == -1.0) {
            return -1.0;
        }
        // Note: aTemperatureValueItem.getValueAsDouble() returns temperature
        // in K
        double tmpTemperatureInK = aTemperatureValueItem.getValueAsDouble();
        return this.getElectrostaticsCouplingConstant(tmpRcutoffInAngstrom, tmpTemperatureInK);
    }

    /**
     * Returns dimensionless electrostatics coupling constant.
     * (No checks are performed)
     *
     * @param aRcutoffInAngstrom Length conversion factor from DPD to physical
     * length unit in Angstrom
     * @param aTemperatureInK Temperature in K
     * @return Dimensionless electrostatics coupling constant or "-1.0" if constant 
     * can not be calculated.
     */
    public double getElectrostaticsCouplingConstant(
        double aRcutoffInAngstrom,
        double aTemperatureInK
    ) {
        // Dimensionless relative permittivity of water:
        // C. G. Malmberg and A. A. Maryott, 
        // Dielectric Constant of Water from 0 to 100 C, 
        // Journal of Research of the National Bureau of Standards 
        // Vol. 56, No. I, 
        // January 1956, Research Paper
        double tmpRelativePermittivityOfWater = 295.87696 + aTemperatureInK * (-1.229097 + aTemperatureInK * (0.0020952245 - 0.00000141 * aTemperatureInK));
        // e^2 / (4 * Pi * k * Epsilon0) = 167100.946898
        return 167100.946898 / (tmpRelativePermittivityOfWater * aRcutoffInAngstrom * aTemperatureInK);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MoleculeTable/MonomerTable value item related methods">
    /**
     * Replaces monomer shortcuts in molecular structures of
     * aMoleculeTableValueItem. NOTE: There is NO test if all monomer shortcuts
     * could be replaced
     *
     * @param aMoleculeTableValueItem Value item forMoleculeTable (may be changed)
     * @param aMonomerTableValueItem Value item for MonomerTable (is not changed)
     */
    public void replaceMonomerShortcutsInMolecularStructure(ValueItem aMoleculeTableValueItem, ValueItem aMonomerTableValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable") || aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return;
        }

        // </editor-fold>
        for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
            for (int k = 0; k < aMonomerTableValueItem.getMatrixRowCount(); k++) {
                // Old code:
                // aMoleculeTableValueItem.setValue(aMoleculeTableValueItem.getValue(i, 1).replaceAll("\\#" + aMonomerTableValueItem.getValue(k, 0), aMonomerTableValueItem.getValue(k, 1)), i, 1);
                aMoleculeTableValueItem.setValue(StringUtils.replace(aMoleculeTableValueItem.getValue(i, 1), "#" + aMonomerTableValueItem.getValue(k, 0), aMonomerTableValueItem.getValue(k, 1)), i, 1);
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Box properties summary related methods">
    /**
     * Returns box properties summary value item container
     *
     * @param aJobInputValueItemContainer ValueItemContainer instance that
     * contains ParticleTable, MonomerTable, MoleculeTable, Quantity and Density value
     * item (NO value item is changed)
     * @return Box properties summary value item container or null if none could
     * be created
     */
    public ValueItemContainer getBoxPropertiesSummaryValueItemContainer(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get necessary value items">
        ValueItem tmpParticleTableValueItem = aJobInputValueItemContainer.getValueItem("ParticleTable");
        ValueItem tmpMonomerTableValueItem = aJobInputValueItemContainer.getValueItem("MonomerTable");
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        ValueItem tmpQuantityValueItem = aJobInputValueItemContainer.getValueItem("Quantity");
        ValueItem tmpDensityValueItem = aJobInputValueItemContainer.getValueItem("Density");
        // NOTE: tmpMoleculeChargeValueItem may be null for legacy reasons
        ValueItem tmpMoleculeChargeValueItem = aJobInputValueItemContainer.getValueItem("MoleculeCharge");
        if (tmpParticleTableValueItem == null
            || tmpMonomerTableValueItem == null
            || tmpMoleculeTableValueItem == null
            || tmpQuantityValueItem == null
            || tmpDensityValueItem == null
        ) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Create box properties summary value item">
        ValueItem tmpBoxSummaryValueItem = new ValueItem();
        tmpBoxSummaryValueItem.setNodeNames(new String[]{ModelMessage.get("BoxPropertiesSummary.Nodename")});
        tmpBoxSummaryValueItem.setName("BOX_PROPERTIES_SUMMARY");
        tmpBoxSummaryValueItem.setDisplayName(ModelMessage.get("BoxPropertiesSummary.DisplayName"));
        tmpBoxSummaryValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpBoxSummaryValueItem.setMatrixColumnNames(
            new String[] {
                ModelMessage.get("BoxPropertiesSummary.ColumnBoxProperty"),
                ModelMessage.get("BoxPropertiesSummary.ColumnBoxPropertyValue")
            }
        );
        tmpBoxSummaryValueItem.setMatrixColumnWidths(
            new String[] {
                ModelDefinitions.CELL_WIDTH_TEXT_400,    // ColumnBoxProperty
                ModelDefinitions.CELL_WIDTH_NUMERIC_120  // ColumnBoxPropertyValue
            }
        );
        tmpBoxSummaryValueItem.setVerticalPosition(1);
        tmpBoxSummaryValueItem.setDescription(ModelMessage.get("BoxPropertiesSummary.Description"));
        this.setBoxSummaryValueItem(
            tmpBoxSummaryValueItem,
            tmpParticleTableValueItem,
            tmpMonomerTableValueItem,
            tmpMoleculeTableValueItem,
            tmpMoleculeChargeValueItem,
            tmpQuantityValueItem,
            tmpDensityValueItem
        );
        // </editor-fold>
        ValueItemContainer tmpBoxPropertiesValueItemContainer = new ValueItemContainer(null);
        tmpBoxPropertiesValueItemContainer.addValueItem(tmpBoxSummaryValueItem);
        return tmpBoxPropertiesValueItemContainer;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Combined value items related methods">
    /**
     * Returns total number of particles of simulation of all molecules with
     * specified quantities
     *
     * @param aMonomerTableValueItem Value item MonomerTable
     * @param aMoleculeTableValueItem Value itemMoleculeTable
     * @param aQuantityValueItem Value item Quantity
     * @return Total number of particles of simulation of all molecules with
     * specified quantities or -1 if number can not be calculated
     */
    public int getTotalNumberOfParticlesOfSimulation(ValueItem aMonomerTableValueItem, ValueItem aMoleculeTableValueItem, ValueItem aQuantityValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return -1;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return -1;
        }
        if (aQuantityValueItem == null || !aQuantityValueItem.getName().equals("Quantity")) {
            return -1;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Replace monomer shortcuts">
        ValueItem tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem;
        if (aMonomerTableValueItem.isActive()) {
            // First clone molecules value item
            tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem.getClone();
            // Second replace in cloned molecules value item
            this.replaceMonomerShortcutsInMolecularStructure(tmpMoleculesWithoutMonomerShortcutsValueItem, aMonomerTableValueItem);
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate total number of particles">
        int tmpTotalNumber = 0;
        for (int i = 0; i < tmpMoleculesWithoutMonomerShortcutsValueItem.getMatrixRowCount(); i++) {
            // Old code:
            // Molecule tmpMolecule = new Molecule(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1), // Structure
            //         tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0) // Name
            // );
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1));
            // NOTE: A row in tmpMoleculesWithoutMonomerShortcutsValueItem corresponds to row in aQuantityValueItem
            tmpTotalNumber += tmpSpices.getTotalNumberOfParticles() * aQuantityValueItem.getValueAsDouble(i, 1);
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        }
        return tmpTotalNumber;

        // </editor-fold>
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Info value items related methods">
    // <editor-fold defaultstate="collapsed" desc="-- Creation methods">
    /**
     * Returns density info value item for compartment container and simulation
     * box
     *
     * @param aValueItemContainer ValueItemContainer instance with value item
     * for Density (is not changed)
     * @return Density info value item for compartment container and simulation
     * box or null if density info value item could not be created
     */
    public ValueItem createDensityInfoValueItem(ValueItemContainer aValueItemContainer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpDensityValueItem = aValueItemContainer.getValueItem("Density");
        if (tmpDensityValueItem == null) {
            return null;
        } else {
            return this.createDensityInfoValueItem(tmpDensityValueItem);
        }
    }

    /**
     * Returns density info value item for compartment container and simulation
     * box
     *
     * @param aDensityValueItem Value item for Density (is not changed)
     * @return Density info value item for compartment container and simulation
     * box or null if density info value item could not be created
     */
    public ValueItem createDensityInfoValueItem(ValueItem aDensityValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aDensityValueItem == null || !aDensityValueItem.getName().equals("Density")) {
            return null;
        }

        // </editor-fold>
        try {

            // <editor-fold defaultstate="collapsed" desc="Set matrix">
            // Parameter false: NOT editable
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][1];
            tmpMatrix[0][0] = new ValueItemMatrixElement(aDensityValueItem.getValue(), new ValueItemDataTypeFormat(ValueItemEnumDataType.NUMERIC, false));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set density info value item">
            ValueItem tmpDensityInfoValueItem = new ValueItem();
            tmpDensityInfoValueItem.setName(ModelDefinitions.DENSITY_INFO_NAME);
            tmpDensityInfoValueItem.setBasicType(ValueItemEnumBasicType.SCALAR);
            tmpDensityInfoValueItem.setMatrix(tmpMatrix);
            // </editor-fold>
            return tmpDensityInfoValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns molecule info value item for compartment container and simulation
     * box
     *
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density and Quantity (are not changed)
     * @return Molecule info value item for compartment container and simulation
     * box or null if molecule info value item could not be created
     */
    public ValueItem createMoleculeInfoValueItem(ValueItemContainer aValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        return this.createMoleculeInfoValueItem(
            aValueItemContainer.getValueItem("MonomerTable"), 
            aValueItemContainer.getValueItem("MoleculeTable"), 
            aValueItemContainer.getValueItem("Density"),
            aValueItemContainer.getValueItem("Quantity")
        );
    }

    /**
     * Returns molecule info value item for compartment container and simulation
     * box
     *
     * @param aMonomerTableValueItem Value item for MonomerTable (is not changed)
     * @param aMoleculeTableValueItem Value item forMoleculeTable (is not changed)
     * @param aDensityValueItem Value item for Density (is not changed)
     * @param aQuantityValueItem Value item for Quantity (is not changed)
     * @return Molecule info value item for compartment container and simulation
     * box or null if molecule info value item could not be created
     */
    public ValueItem createMoleculeInfoValueItem(
        ValueItem aMonomerTableValueItem, 
        ValueItem aMoleculeTableValueItem, 
        ValueItem aDensityValueItem, 
        ValueItem aQuantityValueItem
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return null;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return null;
        }
        if (aDensityValueItem == null || !aDensityValueItem.getName().equals("Density")) {
            return null;
        }
        if (aQuantityValueItem == null || !aQuantityValueItem.getName().equals("Quantity")) {
            return null;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Replace monomer shortcuts in molecules">
            ValueItem tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem;
            if (aMonomerTableValueItem.isActive()) {
                // First clone molecules value item
                tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem.getClone();
                // Second replace in cloned molecules value item
                this.replaceMonomerShortcutsInMolecularStructure(tmpMoleculesWithoutMonomerShortcutsValueItem, aMonomerTableValueItem);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set matrix">
            // Parameter false: NOT editable
            ValueItemDataTypeFormat tmpDataTypeFormatMolecularStructure = new ValueItemDataTypeFormat(ValueItemEnumDataType.MOLECULAR_STRUCTURE, false);
            ValueItemDataTypeFormat tmpDataTypeFormatText = new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT, false);
            ValueItemDataTypeFormat tmpDataTypeFormatInteger = new ValueItemDataTypeFormat(ValueItemEnumDataType.NUMERIC, false);
            // Number of matrix rows are equal to those of aQuantityValueItem
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[aQuantityValueItem.getMatrixRowCount()][];
            for (int i = 0; i < aQuantityValueItem.getMatrixRowCount(); i++) {
                // <editor-fold defaultstate="collapsed" desc="Set Spices">
                // Old code:
                // Molecule tmpMolecule = new Molecule(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1), // Structure
                //         tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0) // Molecule_name
                // );
                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1));
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set row with 5 columns">
                ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[5];

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Column 0 - Molecule name">
                tmpRow[0] = new ValueItemMatrixElement(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0), tmpDataTypeFormatText);

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Column 1 - Molecular structure and possible protein data">
                ValueItemMatrixElement tmpMolecularStructureValueItemMatrixElement = 
                    new ValueItemMatrixElement(tmpSpices.getInputStructure(), tmpDataTypeFormatMolecularStructure);
                // Column 1: Structure with possible protein data
                if (tmpMoleculesWithoutMonomerShortcutsValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                    tmpMolecularStructureValueItemMatrixElement.setProteinData(tmpMoleculesWithoutMonomerShortcutsValueItem.getValueItemMatrixElement(i, 1).getProteinData());
                }
                tmpRow[1] = tmpMolecularStructureValueItemMatrixElement;
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Column 2 - Quantity">
                // Row i of aQuantityValueItem corresponds to row i of
                // tmpMoleculesWithoutMonomerShortcutsValueItem
                tmpRow[2] = new ValueItemMatrixElement(aQuantityValueItem.getValue(i, 1), tmpDataTypeFormatInteger);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Column 3 - Total Number of Particles">
                tmpRow[3] = new ValueItemMatrixElement(String.valueOf(tmpSpices.getTotalNumberOfParticles()), tmpDataTypeFormatInteger);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Column 4 - Molecule graphics color">
                ValueItemDataTypeFormat tmpDataTypeFormatColor = tmpMoleculesWithoutMonomerShortcutsValueItem.getTypeFormat(i, 2).getClone();
                tmpDataTypeFormatColor.setEditable(false);
                tmpRow[4] = new ValueItemMatrixElement(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 2), tmpDataTypeFormatColor);
                // </editor-fold>
                tmpMatrix[i] = tmpRow;
                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set molecule info value item">
            ValueItem tmpMoleculeInfoValueItem = new ValueItem();
            tmpMoleculeInfoValueItem.setName(ModelDefinitions.MOLECULE_INFO_NAME);
            tmpMoleculeInfoValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpMoleculeInfoValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("CompartmentContainer.parameter.moleculeName"),
                ModelMessage.get("CompartmentContainer.parameter.molecularStructure"), ModelMessage.get("CompartmentContainer.parameter.quantity"),
                ModelMessage.get("CompartmentContainer.parameter.totalNumberOfParticles"), ModelMessage.get("CompartmentContainer.parameter.moleculeRenderingColor")});
            tmpMoleculeInfoValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_100, // Molecule_name
                ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecular_structure
                ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Quantity
                ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Total_Number_of_Particles
                ModelDefinitions.CELL_WIDTH_TEXT_100});
            tmpMoleculeInfoValueItem.setMatrix(tmpMatrix);
            // </editor-fold>
            return tmpMoleculeInfoValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns particle info value item for compartment container and simulation
     * box
     *
     * @param aValueItemContainer ValueItemContainer instance with value item
     * for ParticleTable (is not changed)
     * @param aLengthConversionFactor Factor that converts DPD length to
     * physical length (Angstrom since particle volumes are in Angstrom^3)
     * @return Particle info value item for compartment container and simulation
     * box or null if particle info value item could not be created
     */
    public ValueItem createParticleInfoValueItem(ValueItemContainer aValueItemContainer, double aLengthConversionFactor) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpParticleTableValueItem = aValueItemContainer.getValueItem("ParticleTable");
        if (tmpParticleTableValueItem == null) {
            return null;
        } else {
            return this.createParticleInfoValueItem(tmpParticleTableValueItem, aLengthConversionFactor);
        }
    }

    /**
     * Returns particle info value item for compartment container and simulation
     * box
     *
     * @param aParticleTableValueItem Value item for ParticleTable (is not
     * changed)
     * @param aLengthConversionFactor Factor that converts DPD length to
     * physical length (Angstrom since particle volumes are in Angstrom^3)
     * @return Particle info value item for compartment container and simulation
     * box or null if particle info value item could not be created
     */
    private ValueItem createParticleInfoValueItem(ValueItem aParticleTableValueItem, double aLengthConversionFactor) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return null;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Set matrix">
            // Parameter false: NOT editable
            ValueItemDataTypeFormat tmpDataTypeFormatText = new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT, false);
            // Number of matrix rows are equal to those of aParticleTableValueItem
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[aParticleTableValueItem.getMatrixRowCount()][];
            for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
                // <editor-fold defaultstate="collapsed" desc="Set row with 4 columns">
                ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[4];

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Column 0 - Particle">
                tmpRow[0] = new ValueItemMatrixElement(aParticleTableValueItem.getValue(i, 0), tmpDataTypeFormatText);

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Column 1 - Particle name">
                tmpRow[1] = new ValueItemMatrixElement(aParticleTableValueItem.getValue(i, 1), tmpDataTypeFormatText);

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Column 2 - Radius [DPD]">
                // Calculate radius from volume in Angstrom^3 and convert to DPD unit
                double tmpVolumeInAngstrom3 = aParticleTableValueItem.getValueAsDouble(i, 5);
                double tmpRadiusInAngstrom = Math.cbrt(tmpVolumeInAngstrom3 * ModelDefinitions.FACTOR_3_DIV_4_PI);
                double tmpRadiusInDPD = tmpRadiusInAngstrom / aLengthConversionFactor;
                tmpRow[2] = new ValueItemMatrixElement(String.valueOf(tmpRadiusInDPD), tmpDataTypeFormatText);

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Column 3 - Standard-Color">
                tmpRow[3] = new ValueItemMatrixElement(aParticleTableValueItem.getValue(i, 7), tmpDataTypeFormatText);

                // </editor-fold>
                tmpMatrix[i] = tmpRow;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set particle info value item">
            ValueItem tmpParticleInfoValueItem = new ValueItem();
            tmpParticleInfoValueItem.setName(ModelDefinitions.PARTICLE_INFO_NAME);
            tmpParticleInfoValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
            tmpParticleInfoValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("CompartmentContainer.parameter.particle"),
                ModelMessage.get("CompartmentContainer.parameter.particleName"), ModelMessage.get("CompartmentContainer.parameter.graphicsRadius"),
                ModelMessage.get("CompartmentContainer.parameter.standardColor")});
            tmpParticleInfoValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_100, // Particle
                ModelDefinitions.CELL_WIDTH_TEXT_150, // Particle_name
                ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Graphics-Radius
                ModelDefinitions.CELL_WIDTH_TEXT_100});
            tmpParticleInfoValueItem.setMatrix(tmpMatrix);
            // </editor-fold>
            return tmpParticleInfoValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns box info value item for compartment container and simulation box
     *
     * @param aValueItemContainer ValueItemContainer instance with value item
     * for BoxSize (is not changed)
     * @return Box info value item for compartment container and simulation box
     * or null if box info value item could not be created
     */
    public ValueItem createBoxInfoValueItem(ValueItemContainer aValueItemContainer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpBoxSizeValueItem = aValueItemContainer.getValueItem("BoxSize");
        if (tmpBoxSizeValueItem == null) {
            return null;
        } else {
            return this.createBoxInfoValueItem(tmpBoxSizeValueItem);
        }
    }

    /**
     * Returns box info value item for compartment container and simulation box
     *
     * @param aBoxSizeValueItem Value item for BoxSize (is not changed)
     * @return Box info value item for compartment container and simulation box
     * or null if box info value item could not be created
     */
    public ValueItem createBoxInfoValueItem(ValueItem aBoxSizeValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBoxSizeValueItem == null || !aBoxSizeValueItem.getName().equals("BoxSize")) {
            return null;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Set matrix">
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[1][3];
            // X-length of box
            tmpMatrix[0][0] = new ValueItemMatrixElement(aBoxSizeValueItem.getValue(0, 0), aBoxSizeValueItem.getTypeFormat(0, 0).getClone());
            // Y-length of box
            tmpMatrix[0][1] = new ValueItemMatrixElement(aBoxSizeValueItem.getValue(0, 1), aBoxSizeValueItem.getTypeFormat(0, 1).getClone());
            // Z-length of box
            tmpMatrix[0][2] = new ValueItemMatrixElement(aBoxSizeValueItem.getValue(0, 2), aBoxSizeValueItem.getTypeFormat(0, 2).getClone());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set box info value item">
            ValueItem tmpBoxInfoValueItem = new ValueItem();
            tmpBoxInfoValueItem.setName(ModelDefinitions.BOX_INFO_NAME);
            tmpBoxInfoValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
            tmpBoxInfoValueItem.setMatrixColumnNames(new String[]
                    {
                        ModelMessage.get("CompartmentContainer.parameter.xLengthOfDpdBox"),
                        ModelMessage.get("CompartmentContainer.parameter.yLengthOfDpdBox"), 
                        ModelMessage.get("CompartmentContainer.parameter.zLengthOfDpdBox")
                    }
                );
            tmpBoxInfoValueItem.setMatrixColumnWidths(new String[]
                    {
                        ModelDefinitions.CELL_WIDTH_NUMERIC_80, // X-length_of_DPD-Box
                        ModelDefinitions.CELL_WIDTH_NUMERIC_80, // Y-length_of_DPD-Box
                        ModelDefinitions.CELL_WIDTH_NUMERIC_80  // Z-length_of_DPD-Box
                    }
            );
            tmpBoxInfoValueItem.setMatrix(tmpMatrix);
            // </editor-fold>
            return tmpBoxInfoValueItem;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="-- Is-methods">
    /**
     * Checks if passed value item is a density info value item for compartment
     * container and simulation box
     *
     * @param aValueItem Value item to be checked
     * @return True: Value item is density info value item for compartment
     * container and simulation box, false: Otherwise
     */
    public boolean isDensityInfoValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().equals(ModelDefinitions.DENSITY_INFO_NAME);
    }

    /**
     * Checks if passed value item is a molecule info value item for compartment
     * container and simulation box
     *
     * @param aValueItem Value item to be checked
     * @return True: Value item is molecule info value item for compartment
     * container and simulation box, false: Otherwise
     */
    public boolean isMoleculeInfoValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().equals(ModelDefinitions.MOLECULE_INFO_NAME);
    }

    /**
     * Checks if passed value item is a particle info value item for compartment
     * container and simulation box
     *
     * @param aValueItem Value item to be checked
     * @return True: Value item is particle info value item for compartment
     * container and simulation box, false: Otherwise
     */
    public boolean isParticleInfoValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().equals(ModelDefinitions.PARTICLE_INFO_NAME);
    }

    /**
     * Checks if passed value item is a box info value item for compartment
     * container and simulation box
     *
     * @param aValueItem Value item to be checked
     * @return True: Value item is box info value item for compartment container
     * and simulation box, false: Otherwise
     */
    public boolean isBoxInfoValueItem(ValueItem aValueItem) {
        return aValueItem != null && aValueItem.getName().equals(ModelDefinitions.BOX_INFO_NAME);
    }

    /**
     * Returns if molecule definitions of aMoleculeInfoValueItem1 equal those of
     * aMoleculeInfoValueItem2
     *
     * @param aMoleculeInfoValueItem1 Molecule info value item 1
     * @param aMoleculeInfoValueItem2 Molecule info value item 2
     * @return True: Molecule definitions of aMoleculeInfoValueItem1 equal those
     * of aMoleculeInfoValueItem2, false: Otherwise
     */
    public boolean areMoleculesEqual(ValueItem aMoleculeInfoValueItem1, ValueItem aMoleculeInfoValueItem2) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem1) || !this.isMoleculeInfoValueItem(aMoleculeInfoValueItem2)) {
            return false;
        }

        // </editor-fold>
        // Matrix row count must be equal
        if (aMoleculeInfoValueItem1.getMatrixRowCount() != aMoleculeInfoValueItem2.getMatrixRowCount()) {
            return false;
        }
        // Check each molecule
        for (int i = 0; i < aMoleculeInfoValueItem1.getMatrixRowCount(); i++) {
            // Check molecule name (column 0)
            if (!aMoleculeInfoValueItem1.getValue(i, 0).equals(aMoleculeInfoValueItem2.getValue(i, 0))) {
                return false;
            }
            // Check molecular structure (column 1)
            if (!aMoleculeInfoValueItem1.getValue(i, 1).equals(aMoleculeInfoValueItem2.getValue(i, 1))) {
                return false;
            }
        }
        return true;
    }
    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Molecule-to-particles-map related methods">
    /**
     * Return hash table that maps molecule name to its particle hash table that
     * maps a particle to its graphical particle
     *
     * @param aJobInputValueItemContainer ValueItemContainer instance with value
     * items for MonomerTable, MoleculeTable, Density, Quantity and ParticleTable (are
     * not changed)
     * @return HashMap that maps molecule name to its particle hash table that
     * maps a particle to its graphical particle or null if an argument is
     * illegal
     */
    public HashMap<String, HashMap<String, IGraphicalParticle>> getMoleculeToParticlesMap(ValueItemContainer aJobInputValueItemContainer) {
        double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
        if (tmpLengthConversionFactor == -1.0) {
            return null;
        }
        return this.getMoleculeToParticlesMap(aJobInputValueItemContainer, tmpLengthConversionFactor);
    }

    /**
     * Return hashtable that maps molecule name to its particle hashtable that
     * maps a particle to its graphical particle
     *
     * @param aJobInputValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density, Quantity and ParticleTable (are not
     * changed)
     * @param aLengthConversionFactor Factor that converts DPD length to
     * physical length (Angstrom since particle volumes are in Angstrom^3)
     * @return HashMap that maps molecule name to its particle hashtable that
     * maps a particle to its graphical particle or null if an argument is
     * illegal
     */
    public HashMap<String, HashMap<String, IGraphicalParticle>> getMoleculeToParticlesMap(ValueItemContainer aJobInputValueItemContainer, double aLengthConversionFactor) {
        return this.getMoleculeToParticlesMap(this.createDensityInfoValueItem(aJobInputValueItemContainer), this.createMoleculeInfoValueItem(aJobInputValueItemContainer),
                this.createParticleInfoValueItem(aJobInputValueItemContainer, aLengthConversionFactor));
    }

    /**
     * Return hashtable that maps molecule name to its particle hashtable that
     * maps a particle to its graphical particle
     *
     * @param aDensityInfoValueItem Density info value item (is not changed)
     * @param aMoleculeInfoValueItem Molecule info value item (is not changed)
     * @param aParticleInfoValueItem Particle info value item (is not changed)
     * @return HashMap that maps molecule name to its particle hashtable that
     * maps a particle to its graphical particle or null if an argument is
     * illegal
     */
    public HashMap<String, HashMap<String, IGraphicalParticle>> getMoleculeToParticlesMap(ValueItem aDensityInfoValueItem, ValueItem aMoleculeInfoValueItem, ValueItem aParticleInfoValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isDensityInfoValueItem(aDensityInfoValueItem) || !this.isMoleculeInfoValueItem(aMoleculeInfoValueItem) || !this.isParticleInfoValueItem(aParticleInfoValueItem)) {
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpParticleToGraphicalParticleMap">
        HashMap<String, GraphicalParticle> tmpParticleToGraphicalParticleMap = new HashMap<String, GraphicalParticle>(aParticleInfoValueItem.getMatrixRowCount());
        for (int i = 0; i < aParticleInfoValueItem.getMatrixRowCount(); i++) {
            String tmpParticle = aParticleInfoValueItem.getValue(i, 0);
            String tmpParticleName = aParticleInfoValueItem.getValue(i, 1);
            String tmpParticleRadius = aParticleInfoValueItem.getValue(i, 2);
            String tmpParticleColor = aParticleInfoValueItem.getValue(i, 3);
            GraphicalParticle tmpGraphicalParticle = new GraphicalParticle(tmpParticle, tmpParticleName, StandardColorEnum.toStandardColor(tmpParticleColor).toColor(), Double.valueOf(tmpParticleRadius));
            tmpParticleToGraphicalParticleMap.put(tmpParticle, tmpGraphicalParticle);
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpMoleculeToParticlesMap">
        HashMap<String, HashMap<String, IGraphicalParticle>> tmpMoleculeToParticlesMap = new HashMap<>(aMoleculeInfoValueItem.getMatrixRowCount());
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            // Molecule name
            String tmpMoleculeName = aMoleculeInfoValueItem.getValue(i, 0);
            // Molecular structure string
            String tmpMolecularStructureString = aMoleculeInfoValueItem.getValue(i, 1);
            // Color of molecule
            Color tmpMoleculeColor = StandardColorEnum.toStandardColor(aMoleculeInfoValueItem.getValue(i, 4)).toColor();
            // Get particles and molecule-particle frequencies
            // Old code:
            // MolecularStructure tmpMolecularStructure = new MolecularStructure(tmpMolecularStructureString);
            // ParticleFrequency[] tmpParticleFrequencies = tmpMolecularStructure.getParticleFrequencies();
            // New code:
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
            ParticleFrequency[] tmpParticleFrequencies = tmpSpices.getParticleToFrequencyMap().values().toArray(new ParticleFrequency[0]);
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            HashMap<String, IGraphicalParticle> tmpMolecularParticleToGraphicalParticleMap = new HashMap<>(tmpParticleFrequencies.length);
            for (ParticleFrequency tmpParticleFrequency : tmpParticleFrequencies) {
                // IMPORTANT: Clone graphical particle
                GraphicalParticle tmpGraphicalParticle = tmpParticleToGraphicalParticleMap.get(tmpParticleFrequency.getParticle()).getClone();
                // Set molecule name, molecule-particle frequency and molecule color to graphical particle
                tmpGraphicalParticle.setMoleculeName(tmpMoleculeName);
                tmpGraphicalParticle.setMoleculeParticleFrequency(tmpParticleFrequency.getFrequency());
                tmpGraphicalParticle.setMoleculeColor(tmpMoleculeColor);
                tmpMolecularParticleToGraphicalParticleMap.put(tmpParticleFrequency.getParticle(), tmpGraphicalParticle);
            }
            tmpMoleculeToParticlesMap.put(tmpMoleculeName, tmpMolecularParticleToGraphicalParticleMap);
        }

        // </editor-fold>
        return tmpMoleculeToParticlesMap;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Molecule display settings value item related methods">
    /**
     * Fills value item container with value items for molecule display settings
     *
     * @param aJobInputValueItemContainer Job input value item container to be
     * filled with value items for molecule settings. NOTE: ValueItemContainer
     * instance MUST already contain value items for MonomerTable, MoleculeTable,
     * Density, Quantity and ParticleTable as well as Compartments value item
     * (all these are not changed)
     * @param aVerticalPosition Vertical position for first value item to be
     * added
     * @param aRootNodeNames Root node names for value items to be added
     * @return Vertical position for next value item or -1 if molecule display
     * settings value items could not be added
     */
    public int addMoleculeDisplaySettingsValueItems(
            ValueItemContainer aJobInputValueItemContainer,
            int aVerticalPosition,
            String[] aRootNodeNames) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1;
        }
        if (aVerticalPosition < 0) {
            return -1;
        }
        if (aRootNodeNames == null || aRootNodeNames.length == 0) {
            return -1;
        }

        // </editor-fold>
        HashMap<String, HashMap<String, IGraphicalParticle>> tmpMoleculeToParticlesMap = this.getMoleculeToParticlesMap(aJobInputValueItemContainer);
        if (tmpMoleculeToParticlesMap == null) {
            return -1;
        }
        GraphicalParticleInfo tmpGraphicalParticleInfo = new GraphicalParticleInfo(tmpMoleculeToParticlesMap, aJobInputValueItemContainer.hasValueItemWithCompartments());
        return tmpGraphicalParticleInfo.addMoleculeDisplaySettingsValueItems(
            aJobInputValueItemContainer,
            aVerticalPosition,
            aRootNodeNames
        );
    }

    /**
     * Returns new value item container with value items for molecule display
     * settings
     *
     * @param aJobInputValueItemContainer Job input value item container from
     * which value items for molecule settings are created. NOTE:
     * ValueItemContainer instance MUST already contain value items for
     * MonomerTable, MoleculeTable, Density, Quantity and ParticleTable as well as
     * Compartments value item (all these are not changed)
     * @return Value item container with value items for molecule display
     * settings or null if none could be created
     */
    public ValueItemContainer getNewMoleculeDisplaySettingsValueItemContainer(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        HashMap<String, HashMap<String, IGraphicalParticle>> tmpMoleculeToParticlesMap = this.getMoleculeToParticlesMap(aJobInputValueItemContainer);
        if (tmpMoleculeToParticlesMap == null) {
            return null;
        }
        GraphicalParticleInfo tmpGraphicalParticleInfo = new GraphicalParticleInfo(tmpMoleculeToParticlesMap, aJobInputValueItemContainer.hasValueItemWithCompartments());
        return tmpGraphicalParticleInfo.getMoleculeDisplaySettingsValueItemContainer();
    }

    /**
     * Returns value item container with value items for molecule display
     * settings of aJobInputValueItemContainer
     *
     * @param aJobInputValueItemContainer Job input value item container from
     * which molecule display settings value items are taken
     * @return Value item container with value items for molecule display
     * settings or null if none could be created
     */
    public ValueItemContainer getMoleculeDisplaySettingsValueItemContainer(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        ValueItemContainer tmpMoleculeDisplaySettingsValueItemContainer = new ValueItemContainer(null);
        int tmpVerticalPosition = 0;
        String[] tmpRootNodeNames = new String[]{ModelMessage.get("MoleculeDisplaySettings.Root")};
        LinkedList<ValueItem> tmpMoleculeDisplaySettingsValueItemList = aJobInputValueItemContainer.getSortedValueItemsOfContainer(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX);
        for (ValueItem tmpMoleculeDisplaySettingsValueItem : tmpMoleculeDisplaySettingsValueItemList) {
            // IMPORTANT: Clone tmpMoleculeDisplaySettingsValueItem otherwise value items of aJobInputValueItemContainer may be changed!
            ValueItem tmpClonedMoleculeDisplaySettingsValueItem = tmpMoleculeDisplaySettingsValueItem.getClone();
            tmpClonedMoleculeDisplaySettingsValueItem.setNodeNames(
                    this.stringUtilityMethods.getConcatenatedStringArrays(tmpRootNodeNames, new String[]{tmpClonedMoleculeDisplaySettingsValueItem.getLastNodeName()}));
            tmpClonedMoleculeDisplaySettingsValueItem.setVerticalPosition(tmpVerticalPosition++);
            // Update from earlier versions without transparency if necessary
            this.addTransparencyColumnToMoleculeParticlesTableValueItem(tmpClonedMoleculeDisplaySettingsValueItem);
            tmpMoleculeDisplaySettingsValueItemContainer.addValueItem(tmpClonedMoleculeDisplaySettingsValueItem);
        }
        return tmpMoleculeDisplaySettingsValueItemContainer;
    }

    /**
     * Adds transparency to molecule-particles value item if necessary.
     * NOTE: This method is directly related to
 GraphicsUtilityMethods.addMoleculeDisplaySettingsValueItems() and uses
 the same value item definitions!
     *
     * @param aMoleculeParticlesTableValueItem Molecule-particles value item
     * (may be changed due to addition)
     */
    public void addTransparencyColumnToMoleculeParticlesTableValueItem(ValueItem aMoleculeParticlesTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeParticlesTableValueItem == null) {
            return;
        }
        if (!aMoleculeParticlesTableValueItem.getName().equals(ModelDefinitions.MOLECULE_DISPLAY_SETTINGS_VALUE_ITEM_PREFIX + "PARTICLES_IN_MOLECULES")) {
            return;
        }
        // IMPORTANT: If aMoleculeParticlesTableValueItem already contains a transparency column do nothing!
        if (aMoleculeParticlesTableValueItem.getMatrixColumnCount() == 7) {
            return;
        }
        // </editor-fold>
        aMoleculeParticlesTableValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.MoleculeName"),
            ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.Particle"),
            ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.ParticleName"),
            ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.Display"),
            ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.RenderingColor"),
            ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.RadiusScale"),
            ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.Transparency")
        });
        aMoleculeParticlesTableValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_TEXT_150, // Molecule_Name
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Particle
            ModelDefinitions.CELL_WIDTH_TEXT_150, // Particle_Name
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Display
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Rendering_Color
            ModelDefinitions.CELL_WIDTH_TEXT_100, // Radius_Scale
            ModelDefinitions.CELL_WIDTH_TEXT_100}); // Transparency
        aMoleculeParticlesTableValueItem.setDescription(ModelMessage.get("MoleculeDisplaySettings.MoleculeParticles.Description"));
        // Copy molecule-particle information and add transparency
        ValueItemMatrixElement[][] tmpOldMatrix = aMoleculeParticlesTableValueItem.getMatrix();
        ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[tmpOldMatrix.length][];
        for (int i = 0; i < tmpOldMatrix.length; i++) {
            tmpNewMatrix[i] = new ValueItemMatrixElement[7];
            for (int k = 0; k < 6; k++) {
                tmpNewMatrix[i][k] = tmpOldMatrix[i][k];
            }
            // Set transparency (from 0 = opaque to 1 = transparent) to opaque
            tmpNewMatrix[i][6] = new ValueItemMatrixElement("0.00", new ValueItemDataTypeFormat("0.0", 2, 0.0, 1.0));
        }
        aMoleculeParticlesTableValueItem.setMatrix(tmpNewMatrix);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Jdpd (input) file(s) related methods">
    /**
     * Returns pathname of Jdpd input file
     *
     * @param aPath Path of Jdpd input file
     * @return Pathname of Jdpd input file
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJdpdInputFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + ModelDefinitions.JDPD_INPUT_FILENAME;
    }

    /**
     * Returns Jdpd input text and writes Jdpd input file and
     * start-geometry files of molecules if aJdpdInputFilePathname is
     * specified
     *
     * @param aJobInputValueItemContainer Job input value item container
     * @param aJdpdInputFilePathname Full pathname of Jdpd input file
     * (may be null/empty then no files are written)
     * @return Jdpd input text or null if none could be created
     */
    public String getJdpdInputText(ValueItemContainer aJobInputValueItemContainer, String aJdpdInputFilePathname) {
        LinkedList<ValueItem> tmpValueItems;
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            ModelUtils.appendToLogfile(true, "UtilityJobMethods.getJdpdInputText(): aJobInputValueItemContainer == null");
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Local definitions">
        String tmpParameterString;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set string buffer">
        StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initial comment: Header">
        tmpBuffer.append("# Input file for Jdpd created by MFsim");
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initial comment: Job Description (block 0)">
        LinkedList<ValueItem> tmpBlock0ValueItems = aJobInputValueItemContainer.getSortedValueItemsOfBlock(ModelDefinitions.JDPD_INPUT_BLOCK_0);
        if (tmpBlock0ValueItems != null && tmpBlock0ValueItems.size() > 0) {
            for (ValueItem tmpSingleValueItem : tmpBlock0ValueItems) {
                tmpBuffer.append("# ");
                tmpBuffer.append(this.getStringRepresentationOfValueItem(tmpSingleValueItem));
            }
            tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
            tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Section GENERAL">
        tmpBuffer.append(this.getJdpdInputFileBlockStart("GENERAL"));
        tmpBuffer.append("Version 1.0.0.0");
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        tmpBuffer.append(this.getJdpdInputFileBlockEnd("GENERAL"));
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Section PARTICLE_DESCRIPTION (block 1)">
        tmpBuffer.append(this.getJdpdInputFileBlockStart("PARTICLE_DESCRIPTION"));
        // <editor-fold defaultstate="collapsed" desc="- ParticleTable (the only value item of block 1)">
        ValueItem tmpParticleTableValueItem = aJobInputValueItemContainer.getSortedValueItemsOfBlockWithStatus(ModelDefinitions.JDPD_INPUT_BLOCK_1, ValueItemEnumStatus.JDPD_INPUT).getFirst();
        tmpParameterString = this.getStringRepresentationOfValueItem(tmpParticleTableValueItem);
        tmpBuffer.append(tmpParameterString);
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        // </editor-fold>
        tmpBuffer.append(this.getJdpdInputFileBlockEnd("PARTICLE_DESCRIPTION"));
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Section CHEMICAL_SYSTEM_DESCRIPTION (block 2)">
        tmpBuffer.append(this.getJdpdInputFileBlockStart("CHEMICAL_SYSTEM_DESCRIPTION"));
        // <editor-fold defaultstate="collapsed" desc="- Value items">
        tmpValueItems = aJobInputValueItemContainer.getSortedValueItemsOfBlockWithStatus(ModelDefinitions.JDPD_INPUT_BLOCK_2, ValueItemEnumStatus.JDPD_INPUT);
        if (tmpValueItems != null && tmpValueItems.size() > 0) {
            for (ValueItem tmpSingleValueItem : tmpValueItems) {
                if (tmpSingleValueItem.getName().equals("MoleculeTable")) {
                    // <editor-fold defaultstate="collapsed" desc="MoleculeTable - Replacements and start-geometry files">
                    if (aJdpdInputFilePathname == null || aJdpdInputFilePathname.isEmpty()) {
                        tmpSingleValueItem = this.convertMolecularStructureForJdpdInputFile(tmpSingleValueItem, aJobInputValueItemContainer, null);
                    } else {
                        tmpSingleValueItem = this.convertMolecularStructureForJdpdInputFile(tmpSingleValueItem, aJobInputValueItemContainer, (new File(aJdpdInputFilePathname)).getParent());
                    }
                    if (tmpSingleValueItem == null) {
                        ModelUtils.appendToLogfile(true, "UtilityJobMethods.getJdpdInputText(): tmpSingleValueItem == null");
                        return null;
                    }
                    tmpParameterString = this.getStringRepresentationOfValueItem(tmpSingleValueItem);
                    tmpBuffer.append(tmpParameterString);
                    tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
                    // </editor-fold>
                } else {
                    tmpParameterString = this.getStringRepresentationOfValueItem(tmpSingleValueItem);
                    if (tmpParameterString.length() > 0) {
                        tmpBuffer.append(tmpParameterString);
                        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
                    }
                }
            }
        }
        // </editor-fold>
        tmpBuffer.append(this.getJdpdInputFileBlockEnd("CHEMICAL_SYSTEM_DESCRIPTION"));
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Section INTERACTION_DESCRIPTION (block 3)">
        tmpBuffer.append(this.getJdpdInputFileBlockStart("INTERACTION_DESCRIPTION"));
        // <editor-fold defaultstate="collapsed" desc="- Value items">
        tmpValueItems = aJobInputValueItemContainer.getSortedValueItemsOfBlockWithStatus(ModelDefinitions.JDPD_INPUT_BLOCK_3, ValueItemEnumStatus.JDPD_INPUT);
        if (tmpValueItems != null && tmpValueItems.size() > 0) {
            for (ValueItem tmpSingleValueItem : tmpValueItems) {
                // <editor-fold defaultstate="collapsed" desc="Temperature: Convert value to kt unit">
                if (tmpSingleValueItem.getName().equals("Temperature")) {
                    tmpSingleValueItem = tmpSingleValueItem.getClone();
                    // Divide temperature by 300.0 and round to 6 decimals
                    tmpSingleValueItem.setValue(this.stringUtilityMethods.formatDoubleValue(tmpSingleValueItem.getValueAsDouble() / 300.0, 6));
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="InteractionTable: Split particle pair">
                if (tmpSingleValueItem.getName().equals("InteractionTable")) {
                    tmpSingleValueItem = this.getSplitParticlePairValueItem(tmpSingleValueItem);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Bonds12Table: Split particle pair">
                if (tmpSingleValueItem.getName().equals("Bonds12Table")) {
                    tmpSingleValueItem = this.getSplitParticlePairValueItem(tmpSingleValueItem);
                }
                // </editor-fold>
                tmpParameterString = this.getStringRepresentationOfValueItem(tmpSingleValueItem);
                if (tmpParameterString.length() > 0) {
                    tmpBuffer.append(this.getStringRepresentationOfValueItem(tmpSingleValueItem));
                    tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
                }
            }
        }
        // </editor-fold>
        tmpBuffer.append(this.getJdpdInputFileBlockEnd("INTERACTION_DESCRIPTION"));
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Section SIMULATION_DESCRIPTION (block 4)">
        tmpBuffer.append(this.getJdpdInputFileBlockStart("SIMULATION_DESCRIPTION"));
        // <editor-fold defaultstate="collapsed" desc="- Value items">
        tmpValueItems = aJobInputValueItemContainer.getSortedValueItemsOfBlockWithStatus(ModelDefinitions.JDPD_INPUT_BLOCK_4, ValueItemEnumStatus.JDPD_INPUT);
        if (tmpValueItems != null && tmpValueItems.size() > 0) {
            for (ValueItem tmpSingleValueItem : tmpValueItems) {
                tmpParameterString = this.getStringRepresentationOfValueItem(tmpSingleValueItem);
                if (tmpParameterString.length() > 0) {
                    tmpBuffer.append(this.getStringRepresentationOfValueItem(tmpSingleValueItem));
                    tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
                }
            }
        }
        // </editor-fold>
        tmpBuffer.append(this.getJdpdInputFileBlockEnd("SIMULATION_DESCRIPTION"));
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Section SIMULATION_COUNTS (block 5)">
        tmpBuffer.append(this.getJdpdInputFileBlockStart("SIMULATION_COUNTS"));
        // <editor-fold defaultstate="collapsed" desc="- Value items">
        tmpValueItems = aJobInputValueItemContainer.getSortedValueItemsOfBlockWithStatus(ModelDefinitions.JDPD_INPUT_BLOCK_5, ValueItemEnumStatus.JDPD_INPUT);
        if (tmpValueItems != null && tmpValueItems.size() > 0) {
            for (ValueItem tmpSingleValueItem : tmpValueItems) {
                tmpParameterString = this.getStringRepresentationOfValueItem(tmpSingleValueItem);
                if (tmpParameterString.length() > 0) {
                    tmpBuffer.append(this.getStringRepresentationOfValueItem(tmpSingleValueItem));
                    tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
                }
            }
        }
        // </editor-fold>
        tmpBuffer.append(this.getJdpdInputFileBlockEnd("SIMULATION_COUNTS"));
        // </editor-fold>
        String tmpJdpdInputText = tmpBuffer.toString();
        // <editor-fold defaultstate="collapsed" desc="Write Jdpd input file if possible">
        if (aJdpdInputFilePathname != null && !aJdpdInputFilePathname.isEmpty()) {
            if (!this.fileUtilityMethods.deleteSingleFile(aJdpdInputFilePathname)) {
                ModelUtils.appendToLogfile(true, "UtilityJobMethods.getJdpdInputText(): this.fileUtilityMethods.deleteSingleFile(aJdpdInputFilePathname) fails.");
                return null;
            }
            if (!this.fileUtilityMethods.writeSingleStringToTextFile(tmpJdpdInputText, aJdpdInputFilePathname)) {
                ModelUtils.appendToLogfile(true, "UtilityJobMethods.getJdpdInputText(): this.fileUtilityMethods.writeSingleStringToTextFile(tmpJdpdInputText, aJdpdInputFilePathname) fails.");
                return null;
            }
        }
        // </editor-fold>
        return tmpJdpdInputText;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- JobResult file/directory related methods">
    /**
     * Returns pathname of particle positions file for JobResult before
     * minimization
     *
     * @param aPath Path of particle positions file for JobResult
     * @return Pathname of particle positions file for JobResult before
     * minimization
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultParticlePositionsBeforeMinimizationFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        String tmpFileEnding = null;
        // Compressed GZIP file
        tmpFileEnding = Strings.GZIP_FILE_ENDING;
        return aPath + File.separatorChar + FileOutputStrings.PARTICLE_POSITIONS_START_FILENAME_PREFIX + tmpFileEnding;
    }

    /**
     * Returns pathname of particle positions file for JobResult after
     * minimization
     *
     * @param aPath Path of particle positions file for JobResult
     * @return Pathname of particle positions file for JobResult after
     * minimization
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultParticlePositionsAfterMinimizationFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        String tmpFileEnding = null;
        // Compressed GZIP file
        tmpFileEnding = Strings.GZIP_FILE_ENDING;
        return aPath + File.separatorChar + FileOutputStrings.PARTICLE_POSITIONS_MINIMIZED_FILENAME_PREFIX + tmpFileEnding;
    }

    /**
     * Returns pathname of particle positions file for JobResult of specified
     * step
     *
     * @param aJobResultPath Path of JobResult
     * @param aStep Step
     * @return Pathname of particle positions file for JobResult of specified
     * step
     * @throws IllegalArgumentException Thrown if an argument is invalid
     */
    public String getJobResultParticlePositionsStepFilePathname(String aJobResultPath, String aStep) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        if (aStep == null || aStep.isEmpty()) {
            throw new IllegalArgumentException("aStep is null/empty.");
        }

        // </editor-fold>
        String tmpFileEnding = null;
        // Compressed GZIP file
        tmpFileEnding = Strings.GZIP_FILE_ENDING;
        return aJobResultPath + File.separatorChar + ModelDefinitions.JDPD_STEPS_DIRECTORY + File.separatorChar + FileOutputStrings.PARTICLE_POSITIONS_SIMULATION_STEP_FILE_PREFIX + aStep
                + tmpFileEnding;
    }

    /**
     * Returns sorted latest pathnames of particle positions step files for
     * JobResult
     *
     * @param aJobResultPath Path of JobResult
     * @param aNumberOfLatestStepFiles Number of latest step files to be
     * returned
     * @return Sorted latest pathnames of particle positions step files for
     * JobResult or null if none could be found
     * @throws IllegalArgumentException Thrown if an argument is invalid
     */
    public String[] getLatestJobResultParticlePositionsStepFilePathnames(String aJobResultPath, int aNumberOfLatestStepFiles) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        if (aNumberOfLatestStepFiles <= 0) {
            throw new IllegalArgumentException("aNumberOfLatestStepFiles is less/equal zero.");
        }

        // </editor-fold>
        String[] tmpJobResultParticlePositionsStepFilePathnames = this.getJobResultParticlePositionsStepFilePathnames(aJobResultPath);
        if (tmpJobResultParticlePositionsStepFilePathnames != null && tmpJobResultParticlePositionsStepFilePathnames.length > 0) {
            HashMap<String, String> tmpStepToFilePathnameMap = null;
            HashMap<String, String> tmpSortStringToStepMap = null;
            LinkedList<String> tmpStepSortStringList = new LinkedList<String>();
            int tmpMaximumStepNumber = -1;
            tmpStepToFilePathnameMap = new HashMap<String, String>(tmpJobResultParticlePositionsStepFilePathnames.length);
            for (String tmpJobResultParticlePositionsStepFilePathname : tmpJobResultParticlePositionsStepFilePathnames) {
                String tmpStep = this.getStepOfJobResultParticlePositionsStepFilePathname(tmpJobResultParticlePositionsStepFilePathname);
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
            String[] tmpStepSortStringArray = tmpStepSortStringList.toArray(new String[0]);

            LinkedList<String> tmpLatestJobResultParticlePositionsStepFilePathnameList = new LinkedList<String>();
            int tmpCounter = 1;
            for (int i = tmpStepSortStringArray.length - 1; i >= 0; i--) {
                if (tmpCounter <= aNumberOfLatestStepFiles) {
                    tmpLatestJobResultParticlePositionsStepFilePathnameList.add(tmpStepToFilePathnameMap.get(tmpSortStringToStepMap.get(tmpStepSortStringArray[i])));
                } else {
                    break;
                }
                tmpCounter++;
            }
            return tmpLatestJobResultParticlePositionsStepFilePathnameList.toArray(new String[0]);
        } else {
            return null;
        }
    }

    /**
     * Returns pathname of particle positions file for JobResult of step 0
     *
     * @param aJobResultPath Path of particle positions step file for JobResult
     * @return Pathname of particle positions file for JobResult of step 0
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultParticlePositionsStepZeroFilePathname(String aJobResultPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        String tmpFileEnding = null;
        // Compressed GZIP file
        tmpFileEnding = Strings.GZIP_FILE_ENDING;
        return aJobResultPath + File.separatorChar + ModelDefinitions.JDPD_STEPS_DIRECTORY + File.separatorChar + FileOutputStrings.PARTICLE_POSITIONS_SIMULATION_STEP_FILE_PREFIX + "0"
                + tmpFileEnding;
    }

    /**
     * Returns pathname of Ukin progress file for JobResult
     *
     * @param aPath Path of Ukin progress file for JobResult
     * @return Pathname of Ukin progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultUkinProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.U_KIN_FILENAME;
    }

    /**
     * Returns pathname of UpotDpd progress file for JobResult
     *
     * @param aPath Path of UpotDpd progress file for JobResult
     * @return Pathname of UpotDpd progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultUpotDpdProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.U_POT_DPD_FILENAME;
    }

    /**
     * Returns pathname of UpotBond progress file for JobResult
     *
     * @param aPath Path of UpotBond progress file for JobResult
     * @return Pathname of UpotBond progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultUpotBondProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.U_POT_BOND_FILENAME;
    }

    /**
     * Returns pathname of UpotElectrostatics progress file for JobResult
     *
     * @param aPath Path of UpotElectrostatics progress file for JobResult
     * @return Pathname of UpotElectrostatics progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultUpotElectrostaticsProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.U_POT_ELECTROSTATICS_FILENAME;
    }

    /**
     * Returns pathname of UpotTotal progress file for JobResult
     *
     * @param aPath Path of UpotTotal progress file for JobResult
     * @return Pathname of UpotTotal progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultUpotTotalProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.U_POT_TOTAL_FILENAME;
    }

    /**
     * Returns pathname of Utotal progress file for JobResult
     *
     * @param aPath Path of Utotal progress file for JobResult
     * @return Pathname of Utotal progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultUtotalProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.U_TOTAL_FILENAME;
    }

    /**
     * Returns pathname of DPD surface tension along x progress file for JobResult
     *
     * @param aPath Path of DPD surface tension along x progress file for JobResult
     * @return Pathname of DPD surface tension along x progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultDpdSurfaceTensionAlongXProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.DPD_SURFACE_TENSION_X_FILENAME;
    }

    /**
     * Returns pathname of DPD surface tension along y progress file for JobResult
     *
     * @param aPath Path of DPD surface tension along y progress file for JobResult
     * @return Pathname of DPD surface tension along y progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultDpdSurfaceTensionAlongYProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.DPD_SURFACE_TENSION_Y_FILENAME;
    }

    /**
     * Returns pathname of DPD surface tension along z progress file for JobResult
     *
     * @param aPath Path of DPD surface tension along z progress file for JobResult
     * @return Pathname of DPD surface tension along z progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultDpdSurfaceTensionAlongZProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.DPD_SURFACE_TENSION_Z_FILENAME;
    }

    /**
     * Returns pathname of DPD surface tension norm progress file for JobResult
     *
     * @param aPath Path of DPD surface tension norm progress file for JobResult
     * @return Pathname of DPD surface tension norm progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultDpdSurfaceTensionNormProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.DPD_SURFACE_TENSION_NORM_FILENAME;
    }

    /**
     * Returns pathname of surface tension along x progress file for JobResult
     *
     * @param aPath Path of surface tension along x progress file for JobResult
     * @return Pathname of surface tension along x progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultSurfaceTensionAlongXProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.SURFACE_TENSION_X_FILENAME;
    }

    /**
     * Returns pathname of surface tension along y progress file for JobResult
     *
     * @param aPath Path of surface tension along y progress file for JobResult
     * @return Pathname of surface tension along y progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultSurfaceTensionAlongYProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.SURFACE_TENSION_Y_FILENAME;
    }

    /**
     * Returns pathname of surface tension along z progress file for JobResult
     *
     * @param aPath Path of surface tension along z progress file for JobResult
     * @return Pathname of surface tension along z progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultSurfaceTensionAlongZProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.SURFACE_TENSION_Z_FILENAME;
    }

    /**
     * Returns pathname of surface tension norm progress file for JobResult
     *
     * @param aPath Path of surface tension norm progress file for JobResult
     * @return Pathname of surface tension norm progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultSurfaceTensionNormProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }
        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.SURFACE_TENSION_NORM_FILENAME;
    }

    /**
     * Returns pathname of temperature progress file for JobResult
     *
     * @param aPath Path of temperature progress file for JobResult
     * @return Pathname of temperature progress file for JobResult
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultTemperatureProgressFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + FileOutputStrings.TEMPERATURE_FILENAME;
    }

    /**
     * Returns pathname of particle positions file for JobResult after
     * simulation
     *
     * @param aPath Path of particle positions file for JobResult
     * @return Pathname of particle positions file for JobResult after
     * simulation
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultParticlePositionsAfterSimulationFilePathname(String aPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        String tmpFileEnding = null;
        // Compressed GZIP file
        tmpFileEnding = Strings.GZIP_FILE_ENDING;
        return aPath + File.separatorChar + FileOutputStrings.PARTICLE_POSITIONS_FINAL_FILENAME_PREFIX + tmpFileEnding;
    }

    /**
     * Return file pathname of RDF file for Job Result and the specified
     * particles
     *
     * @param aJobResultPath Path of JobResult
     * @param aFirstParticle First particle
     * @param aSecondParticle Second particle
     * @return File pathname of RDF file for Job Result and the specified
     * particles
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultRdfFilePathname_Old(String aJobResultPath, String aFirstParticle, String aSecondParticle) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_RDF_DIRECTORY + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_RDF_FILE_PREFIX + aFirstParticle + "_"
                + aSecondParticle + FileOutputStrings.TEXT_FILE_ENDING;
    }

    /**
     * Return file pathname of particle-pair RDF file for Job Result and the
     * specified particles
     *
     * @param aJobResultPath Path of JobResult
     * @param aFirstParticle First particle
     * @param aSecondParticle Second particle
     * @param aSegmentLength Segment length
     * @return File pathname of particle-pair RDF file for Job Result and the
     * specified particles
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultParticlePairRdfFilePathname(String aJobResultPath, String aFirstParticle, String aSecondParticle, double aSegmentLength) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_RDF_DIRECTORY + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_RDF_FILE_PREFIX
                + aFirstParticle + "_" + aSecondParticle + "_" + String.valueOf(aSegmentLength) + FileOutputStrings.TEXT_FILE_ENDING;
    }

    /**
     * Return file pathname of molecule-particle-pair RDF file for Job Result
     * and the specified molecule particles
     *
     * @param aJobResultPath Path of JobResult
     * @param aFirstMoleculeParticle First molecule particle
     * @param aSecondMoleculeParticle Second molecule particle
     * @param aSegmentLength Segment length
     * @return File pathname of molecule-particle-pair RDF file for Job
     * Result and the specified molecule particles
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultMoleculeParticlePairRdfFilePathname(String aJobResultPath, String aFirstMoleculeParticle, String aSecondMoleculeParticle, double aSegmentLength) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_RDF_DIRECTORY + File.separatorChar
                + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_RDF_FILE_PREFIX + aFirstMoleculeParticle + "_" + aSecondMoleculeParticle + "_" + String.valueOf(aSegmentLength)
                + FileOutputStrings.TEXT_FILE_ENDING;
    }

    /**
     * Return file pathname of molecule-center-pair RDF file for Job Result
     * and the specified molecule particles
     *
     * @param aJobResultPath Path of JobResult
     * @param aFirstMoleculeCenter First molecule center
     * @param aSecondMoleculeCenter Second molecule center
     * @param aSegmentLength Segment length
     * @return File pathname of molecule-center-pair RDF file for Job
     * Result and the specified molecule centers
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultMoleculeCenterPairRdfFilePathname(
        String aJobResultPath, 
        String aFirstMoleculeCenter, 
        String aSecondMoleculeCenter, 
        double aSegmentLength
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath + 
            File.separatorChar + 
            ModelDefinitions.JOB_RESULT_MOLECULE_CENTER_PAIR_RDF_DIRECTORY + 
            File.separatorChar + 
            ModelDefinitions.JOB_RESULT_MOLECULE_CENTER_PAIR_RDF_FILE_PREFIX + 
            aFirstMoleculeCenter + 
            "_" + 
            aSecondMoleculeCenter + 
            "_" + 
            String.valueOf(aSegmentLength) + 
            FileOutputStrings.TEXT_FILE_ENDING;
    }
    
    /**
     * Return file pathname of particle-pair distance file for Job Result and
     * the specified particles
     *
     * @param aJobResultPath Path of JobResult
     * @param aParticlePair Particle pair separated by underscore "_"
     * @return File pathname of particle-pair distance file for Job Result and
     * the specified particle pair
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultParticlePairDistanceFilePathname(String aJobResultPath, String aParticlePair) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_DISTANCE_DIRECTORY + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_DISTANCE_FILE_PREFIX
                + aParticlePair + FileOutputStrings.TEXT_FILE_ENDING;
    }

    /**
     * Return file pathname of molecule-particle-pair distance file for Job
     * Result and the specified particles
     *
     * @param aJobResultPath Path of JobResult
     * @param aMoleculeParticlePair Molecule-particle pair separated by
     * underscore "_"
     * @return File pathname of molecule-particle-pair distance file for Job
     * Result and the specified molecule-particle pair
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultMoleculeParticlePairDistanceFilePathname(String aJobResultPath, String aMoleculeParticlePair) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_DISTANCE_DIRECTORY + File.separatorChar
                + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_DISTANCE_FILE_PREFIX + aMoleculeParticlePair + FileOutputStrings.TEXT_FILE_ENDING;
    }

    /**
     * Return file pathname of particle-pair average distances file for
     * specified step of Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @param aStep Step
     * @return File pathname of particle-pair average distances file for
     * specified step of Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultParticlePairAverageDistancesForStepFilePathname(String aJobResultPath, String aStep) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_DISTANCE_DIRECTORY + File.separatorChar
                + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_AVERAGE_DISTANCE_FOR_STEP_FILE_PREFIX
                + aStep + FileOutputStrings.TEXT_FILE_ENDING;
    }

    /**
     * Return file pathname of molecule-particle-pair average distances file
     * for specified step of Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @param aStep Step
     * @return File pathname of molecule-particle-pair average distances file
     * for specified step of Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultMoleculeParticlePairAverageDistancesForStepFilePathname(String aJobResultPath, String aStep) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_DISTANCE_DIRECTORY + File.separatorChar
                + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_AVERAGE_DISTANCE_FOR_STEP_FILE_PREFIX
                + aStep + FileOutputStrings.TEXT_FILE_ENDING;
    }

    /**
     * Return directory path of particle-pair RDF directory of Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @return Directory path of particle-pair RDF directory of Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultParticlePairRdfPath(String aJobResultPath) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_RDF_DIRECTORY;
    }

    /**
     * Return directory path of molecule-particle-pair RDF directory of Job
     * Result
     *
     * @param aJobResultPath Path of JobResult
     * @return Directory path of molecule-particle-pair RDF directory of Job
     * Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultMoleculeParticlePairRdfPath(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_RDF_DIRECTORY;
    }

    /**
     * Return directory path of molecule-center-pair RDF directory of Job
     * Result
     *
     * @param aJobResultPath Path of JobResult
     * @return Directory path of molecule-center-pair RDF directory of Job
     * Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultMoleculeCenterPairRdfPath(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_MOLECULE_CENTER_PAIR_RDF_DIRECTORY;
    }

    /**
     * Return directory path of particle-pair distance directory of Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @return Directory path of particle-pair distance directory of Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultParticlePairDistancePath(String aJobResultPath) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_DISTANCE_DIRECTORY;
    }

    /**
     * Return directory path of molecule-particle-pair distance directory of
     * Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @return Directory path of molecule-particle-pair distance directory of
     * Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultMoleculeParticlePairDistancePath(String aJobResultPath) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_DISTANCE_DIRECTORY;
    }

    /**
     * Returns array of all Job Result particle-pair radial distribution
     * function file pathnames.
     *
     * @param aJobResultPath Path of Job Result
     * @return Array of all Job Result particle-pair radial distribution
     * function file pathnames
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String[] getJobResultParticlePairRdfFilePathnames(String aJobResultPath) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return this.fileUtilityMethods.getFilePathnamesWithPrefix(aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_RDF_DIRECTORY,
                ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_RDF_FILE_PREFIX);

    }

    /**
     * Returns array of all Job Result molecule-particle-pair radial
     * distribution function file pathnames.
     *
     * @param aJobResultPath Path of Job Result
     * @return Array of all Job Result molecule-particle-pair radial
     * distribution function file pathnames
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String[] getJobResultMoleculeParticlePairRdfFilePathnames(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return this.fileUtilityMethods.getFilePathnamesWithPrefix(aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_RDF_DIRECTORY,
                ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_RDF_FILE_PREFIX);
    }

    /**
     * Returns array of all Job Result molecule-center-pair radial
     * distribution function file pathnames.
     *
     * @param aJobResultPath Path of Job Result
     * @return Array of all Job Result molecule-center-pair radial
     * distribution function file pathnames
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String[] getJobResultMoleculeCenterPairRdfFilePathnames(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return this.fileUtilityMethods.getFilePathnamesWithPrefix(
            aJobResultPath + 
            File.separatorChar + 
            ModelDefinitions.JOB_RESULT_MOLECULE_CENTER_PAIR_RDF_DIRECTORY,
            ModelDefinitions.JOB_RESULT_MOLECULE_CENTER_PAIR_RDF_FILE_PREFIX
        );
    }

    /**
     * Returns array of all Job Result particle-pair distance file pathnames.
     *
     * @param aJobResultPath Path of Job Result
     * @return Array of all Job Result particle-pair distance file pathnames
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String[] getJobResultParticlePairDistanceFilePathnames(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return this.fileUtilityMethods.getFilePathnamesWithPrefix(aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_DISTANCE_DIRECTORY,
                ModelDefinitions.JOB_RESULT_PARTICLE_PAIR_DISTANCE_FILE_PREFIX);

    }

    /**
     * Returns array of all Job Result molecule-particle-pair distance file
     * pathnames.
     *
     * @param aJobResultPath Path of Job Result
     * @return Array of all Job Result molecule-particle-pair distance file
     * pathnames
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String[] getJobResultMoleculeParticlePairDistanceFilePathnames(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return this.fileUtilityMethods.getFilePathnamesWithPrefix(aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_DISTANCE_DIRECTORY,
                ModelDefinitions.JOB_RESULT_PARTICLE_IN_MOLECULE_PAIR_DISTANCE_FILE_PREFIX);

    }

    /**
     * Return directory path of steps directory of Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @return Directory path of steps directory of Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultStepsPath(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JDPD_STEPS_DIRECTORY;
    }

    /**
     * Return directory path of minimization steps directory of Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @return Directory path of minimization steps directory of Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultMinimizationStepsPath(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JDPD_MINIMIZATION_STEPS_DIRECTORY;
    }

    /**
     * Return directory path of radius-of-gyration directory of Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @return Directory path of radius-of-gyration directory of Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultRadiusOfGyrationPath(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JDPD_RADIUS_OF_GYRATION_DIRECTORY;
    }

    /**
     * Return directory path of nearest-neighbor directory of Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @return Directory path of nearest-neighbor directory of Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultNearestNeighborPath(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JDPD_NEAREST_NEIGHBOR_DIRECTORY;
    }

    /**
     * Returns file pathname of radius-of-gyration file of Job Result
     *
     * @param aJobResultPath Path of JobResult
     * @param anIndex Index of radius-of-gyration file
     * @return File pathname of radius-of-gyration file of Job Result
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultRadiusOfGyrationFilePathname(String aJobResultPath, int anIndex) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        if (anIndex < 0) {
            throw new IllegalArgumentException("anIndex is negative.");
        }

        // </editor-fold>
        return this.getJobResultRadiusOfGyrationPath(aJobResultPath) + File.separatorChar + FileOutputStrings.RADIUS_OF_GYRATION_FILENAME_PREFIX + String.valueOf(anIndex).trim()
                + FileOutputStrings.TEXT_FILE_ENDING;
    }

    /**
     * Returns array of all Job Result radius-of-gyration file pathnames.
     *
     * @param aJobResultPath Path of Job Result
     * @return Array of all Job Result particle-pair radial distribution
     * function file pathnames
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String[] getJobResultRadiusOfGyrationFilePathnames(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return this.fileUtilityMethods.getFilePathnamesWithPrefix(aJobResultPath + File.separatorChar + ModelDefinitions.JDPD_RADIUS_OF_GYRATION_DIRECTORY,
            FileOutputStrings.RADIUS_OF_GYRATION_FILENAME_PREFIX
        );
    }

    /**
     * Returns base molecule-particle to nearest-neighbor molecule-particle 
     * step-frequency map file pathname.
     *
     * @param aJobResultPath Path of Job Result
     * @return Base molecule-particle to nearest-neighbor molecule-particle 
     * step-frequency map file pathname
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultBaseMoleculeParticleToNearestNeighborMoleculeParticleStepFrequencyMapFilePathname(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath 
            + File.separatorChar 
            + ModelDefinitions.JDPD_NEAREST_NEIGHBOR_DIRECTORY 
            + File.separatorChar 
            + FileOutputStrings.MP_TO_NN_MP_FILENAME;
    }

    /**
     * Returns base molecule-particle to nearest-neighbor particle 
     * step-frequency map file pathname.
     *
     * @param aJobResultPath Path of Job Result
     * @return Base molecule-particle to nearest-neighbor particle 
     * step-frequency map file pathname
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultBaseMoleculeParticleToNearestNeighborParticleStepFrequencyMapFilePathname(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath 
            + File.separatorChar 
            + ModelDefinitions.JDPD_NEAREST_NEIGHBOR_DIRECTORY 
            + File.separatorChar 
            + FileOutputStrings.MP_TO_NN_P_FILENAME;
    }

    /**
     * Returns base molecule-particle to nearest-neighbor molecule 
     * step-frequency map file pathname.
     *
     * @param aJobResultPath Path of Job Result
     * @return Base molecule-particle to nearest-neighbor molecule 
     * step-frequency map file pathname
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultBaseMoleculeParticleToNearestNeighborMoleculeStepFrequencyMapFilePathname(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath 
            + File.separatorChar 
            + ModelDefinitions.JDPD_NEAREST_NEIGHBOR_DIRECTORY 
            + File.separatorChar 
            + FileOutputStrings.MP_TO_NN_M_FILENAME;
    }

    /**
     * Returns base molecule to nearest-neighbor molecule step-frequency map 
     * file pathname.
     *
     * @param aJobResultPath Path of Job Result
     * @return Base molecule to nearest-neighbor molecule step-frequency map 
     * file pathname
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultBaseMoleculeToNearestNeighborMoleculeStepFrequencyMapFilePathname(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath 
            + File.separatorChar 
            + ModelDefinitions.JDPD_NEAREST_NEIGHBOR_DIRECTORY 
            + File.separatorChar 
            + FileOutputStrings.M_TO_NN_M_FILENAME;
    }

    /**
     * Returns base molecule to nearest-neighbor molecule-tuple step-frequency 
     * map file pathname.
     *
     * @param aJobResultPath Path of Job Result
     * @return Base molecule to nearest-neighbor molecule-tuple step-frequency 
     * map file pathname
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultBaseMoleculeToNearestNeighborMoleculeTupleStepFrequencyMapFilePathname(String aJobResultPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        return aJobResultPath 
            + File.separatorChar 
            + ModelDefinitions.JDPD_NEAREST_NEIGHBOR_DIRECTORY 
            + File.separatorChar 
            + FileOutputStrings.M_TO_NN_M_TUPLE_FILENAME;
    }

    /**
     * Returns all pathnames of Job Result particle position step files
     *
     * @param aJobResultPath Path of Job Result
     * @return Pathnames of all Job Result particle position step files
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String[] getJobResultParticlePositionsStepFilePathnames(String aJobResultPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return this.fileUtilityMethods.getFilePathnamesWithPrefix(aJobResultPath + File.separatorChar + ModelDefinitions.JDPD_STEPS_DIRECTORY,
            FileOutputStrings.PARTICLE_POSITIONS_SIMULATION_STEP_FILE_PREFIX
        );
    }

    /**
     * Returns all pathnames of Job Result particle position minimization step 
     * files
     *
     * @param aJobResultPath Path of Job Result
     * @return Pathnames of all Job Result particle position minimization step files
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String[] getJobResultParticlePositionsMinStepFilePathnames(String aJobResultPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return this.fileUtilityMethods.getFilePathnamesWithPrefix(aJobResultPath + File.separatorChar + ModelDefinitions.JDPD_MINIMIZATION_STEPS_DIRECTORY,
            FileOutputStrings.PARTICLE_POSITIONS_MINIMIZED_FILENAME_PREFIX);
    }

    /**
     * Returns job result history for job inputs path
     *
     * @param aJobResultPath Path of job result
     * @return Job result history path
     * @throws IllegalArgumentException Thrown if aJobResultPath is invalid
     */
    public String getJobResultHistoryPath(String aJobResultPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }

        // </editor-fold>
        return aJobResultPath + File.separatorChar + ModelDefinitions.JOB_RESULT_HISTORY_DIRECTORY;
    }

    /**
     * Returns new job result history job input pathname
     *
     * @param aJobResultHistoryPath Path of job result history directory
     * @return New job result history job input pathname
     * @throws IllegalArgumentException Thrown if aJobResultHistoryPath is
     * invalid
     */
    public String getNewHistoryJobInputPathname(String aJobResultHistoryPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultHistoryPath == null || aJobResultHistoryPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultHistoryPath is null/empty.");
        }
        if (!(new File(aJobResultHistoryPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultHistoryPath is not an existing directory.");
        }

        // </editor-fold>
        String tmpNewHistoryJobInputFilePathname
                = aJobResultHistoryPath
                + File.separatorChar
                + ModelDefinitions.JOB_INPUT_HISTORY_PREFIX
                + this.timeUtilityMethods.convertTimestampInStandardFormatIntoDirectoryEnding(ModelUtils.getUniqueTimestampInStandardFormat())
                + FileOutputStrings.TEXT_FILE_ENDING;
        return tmpNewHistoryJobInputFilePathname;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle positions related methods">
    /**
     * Reads GraphicalParticlePositionInfo instance from graphical particle 
     * positions file with repetitions
     * 
     * @param aJobResultParticlePositionsFilePathname Full pathname of graphical
     * particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @param aNumberOfRepetitions Number of repetitions
     * @param aDelayInMilliseconds Delay in milliseconds
     * @return GraphicalParticlePositionInfo instance with graphical particle 
     * positions or null if GraphicalParticlePositionInfo instance could not be 
     * created
     */
    public GraphicalParticlePositionInfo readGraphicalParticlePositionsWithRepetitions(
        String aJobResultParticlePositionsFilePathname, 
        ValueItemContainer aJobInputValueItemContainer,
        int aNumberOfRepetitions,
        long aDelayInMilliseconds
    ) {
        GraphicalParticlePositionInfo tmpGraphicalParticlePositionInfo = null;
        int tmpCounter = 0;
        while (tmpGraphicalParticlePositionInfo == null && tmpCounter < aNumberOfRepetitions) {
            tmpGraphicalParticlePositionInfo = 
                this.readGraphicalParticlePositions(
                    aJobResultParticlePositionsFilePathname,
                    aJobInputValueItemContainer
                );
            if (tmpGraphicalParticlePositionInfo == null) {
                try {
                    Thread.sleep(aDelayInMilliseconds);
                } catch (InterruptedException anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    // This should never happen!
                    return null;
                }
                tmpCounter++;
            }
        }
        return tmpGraphicalParticlePositionInfo;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Molecule list, particle list, molecule-particle list related methods">
    /**
     * Returns array of linked lists with all molecules, particles and 
     * molecule-particles
     * 
     * @param aJobInputValueItemContainer JobInput value item container
     * @return Array with 3 sorted linked lists (index = 0: Molecules, 
     * index = 1: Particles, index = 2: Molecule-Particles) or null if none 
     * could be created
     */
    public LinkedList<String>[] getAllMoleculeAndParticleLists(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return null;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get necessary value items">
        ValueItem tmpMonomerTableValueItem = aJobInputValueItemContainer.getValueItem("MonomerTable");
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        if (tmpMonomerTableValueItem == null || tmpMoleculeTableValueItem == null) {
            return null;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Replace monomer shortcuts in molecules">
            ValueItem tmpMoleculesWithoutMonomerShortcutsValueItem = tmpMoleculeTableValueItem;
            if (tmpMonomerTableValueItem.isActive()) {
                // First clone molecules value item
                tmpMoleculesWithoutMonomerShortcutsValueItem = tmpMoleculeTableValueItem.getClone();
                // Second replace in cloned molecules value item
                this.replaceMonomerShortcutsInMolecularStructure(tmpMoleculesWithoutMonomerShortcutsValueItem, tmpMonomerTableValueItem);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create molecule-particle list">
            LinkedList<String> tmpMoleculeList = new LinkedList<>();
            LinkedList<String> tmpParticleList = new LinkedList<>();
            // Number of different particles is O(100)
            HashMap<String, String> tmpParticleMap = new HashMap<>(100);
            LinkedList<String> tmpMoleculeParticleList = new LinkedList<>();
            for (int i = 0; i < tmpMoleculesWithoutMonomerShortcutsValueItem.getMatrixRowCount(); i++) {
                String tmpMolecule = tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0);
                tmpMoleculeList.add(tmpMolecule);
                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1));
                String[] tmpParticlesOfMolecule = tmpSpices.getParticles();
                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                for (String tmpSingleParticleOfMolecule : tmpParticlesOfMolecule) {
                    if (!tmpParticleMap.containsKey(tmpSingleParticleOfMolecule)) {
                        tmpParticleMap.put(tmpSingleParticleOfMolecule, tmpSingleParticleOfMolecule);
                        tmpParticleList.add(tmpSingleParticleOfMolecule);
                    }
                    tmpMoleculeParticleList.add(tmpMolecule + SpicesConstants.PARTICLE_SEPARATOR + tmpSingleParticleOfMolecule);
                }
            }
            Collections.sort(tmpMoleculeList);
            Collections.sort(tmpParticleList);
            Collections.sort(tmpMoleculeParticleList);
            // </editor-fold>
            return new LinkedList[] {
                tmpMoleculeList,
                tmpParticleList,
                tmpMoleculeParticleList
            };
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns sorted molecule-particle list
     *
     * @param aMonomerTableValueItem Monomers value item (is NOT changed)
     * @param aMoleculeTableValueItem Molecules value item (is NOT changed)
     * @return Sorted molecule-particle list or null if none could be created
     */
    public LinkedList<String> getSortedMoleculeParticleList(ValueItem aMonomerTableValueItem, ValueItem aMoleculeTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return null;
        }
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return null;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Replace monomer shortcuts in molecules">
            ValueItem tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem;
            if (aMonomerTableValueItem.isActive()) {
                // First clone molecules value item
                tmpMoleculesWithoutMonomerShortcutsValueItem = aMoleculeTableValueItem.getClone();
                // Second replace in cloned molecules value item
                this.replaceMonomerShortcutsInMolecularStructure(tmpMoleculesWithoutMonomerShortcutsValueItem, aMonomerTableValueItem);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create molecule-particle list">
            LinkedList<String> tmpMoleculeParticleList = new LinkedList<String>();
            for (int i = 0; i < tmpMoleculesWithoutMonomerShortcutsValueItem.getMatrixRowCount(); i++) {
                // Old code:
                // Molecule tmpMolecule = new Molecule(
                //     tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1), // Structure without monomer shortcuts
                //     tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0)  // Molecule_name
                // );
                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 1));
                String[] tmpParticlesOfMolecule = tmpSpices.getParticles();
                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                for (String tmpSingleParticleOfMolecule : tmpParticlesOfMolecule) {
                    tmpMoleculeParticleList.add(tmpMoleculesWithoutMonomerShortcutsValueItem.getValue(i, 0) + SpicesConstants.PARTICLE_SEPARATOR + tmpSingleParticleOfMolecule);
                }
            }
            Collections.sort(tmpMoleculeParticleList);
            // </editor-fold>
            return tmpMoleculeParticleList;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns sorted molecule name list
     *
     * @param aMoleculeTableValueItem Molecules value item (is NOT changed)
     * @return Sorted molecule name list or null if none could be created
     */
    public LinkedList<String> getSortedMoleculeNameList(ValueItem aMoleculeTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return null;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Create molecule-particle list">
            LinkedList<String> tmpMoleculeNameList = new LinkedList<String>();
            for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
                tmpMoleculeNameList.add(aMoleculeTableValueItem.getValue(i, 0));
            }
            Collections.sort(tmpMoleculeNameList);
            // </editor-fold>
            return tmpMoleculeNameList;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Radial Distribution Function (RDF) related methods">
    /**
     * Returns if Job Input value item container defines particle-pair RDF
     * calculation
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return True: Job Input value item container defines particle-pair RDF
     * calculation, false: Otherwise
     */
    public boolean isParticlePairRdfCalculation(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return false;
        }

        // </editor-fold>
        ValueItem tmpParticlePairRdfCalculationValueItem = aJobInputValueItemContainer.getValueItem("ParticlePairRdfCalculation");
        if (tmpParticlePairRdfCalculationValueItem == null) {
            return false;
        }
        return tmpParticlePairRdfCalculationValueItem.isActive();
    }

    /**
     * Returns if Job Input value item container defines
     * molecule-particle-pair RDF calculation
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return True: Job Input value item container defines
     * molecule-particle-pair RDF calculation, false: Otherwise
     */
    public boolean isMoleculeParticlePairRdfCalculation(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return false;
        }

        // </editor-fold>
        ValueItem tmpMoleculeParticlePairRdfCalculationValueItem = aJobInputValueItemContainer.getValueItem("MoleculeParticlePairRdfCalculation");
        if (tmpMoleculeParticlePairRdfCalculationValueItem == null) {
            return false;
        }
        return tmpMoleculeParticlePairRdfCalculationValueItem.isActive();
    }

    /**
     * Returns if Job Input value item container defines
     * molecule-center-pair RDF calculation
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return True: Job Input value item container defines
     * molecule-center-pair RDF calculation, false: Otherwise
     */
    public boolean isMoleculeCenterPairRdfCalculation(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return false;
        }
        // </editor-fold>
        ValueItem tmpMoleculeCenterPairRdfCalculationValueItem = aJobInputValueItemContainer.getValueItem("MoleculeCenterPairRdfCalculation");
        if (tmpMoleculeCenterPairRdfCalculationValueItem == null) {
            return false;
        }
        return tmpMoleculeCenterPairRdfCalculationValueItem.isActive();
    }
    
    /**
     * Creates defined particle-pair radial distribution function files by 
     * sequential (!) processing of simulation steps. 
     * NOTE: Necessary RDF directory of JobResult MUST already be created!
     *
     * @param aJobResultParticlePositionsFilePathnames Full pathnames of
     * graphical particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @param aJobResultPath Path of JobResult
     */
    public void createDefinedParticlePairRadialDistributionFunctionFiles_Sequential(
        String[] aJobResultParticlePositionsFilePathnames, 
        ValueItemContainer aJobInputValueItemContainer, 
        String aJobResultPath
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathnames == null || aJobResultParticlePositionsFilePathnames.length == 0) {
            return;
        }
        if (aJobInputValueItemContainer == null) {
            return;
        }
        // Get particle pairs for RDF calculation
        String[][] tmpParticlePairs = this.getParticlePairsForRdfCalculation(aJobInputValueItemContainer);
        if (tmpParticlePairs == null) {
            return;
        }
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            return;
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Prepare calculations">
        // Get length conversion factor for transformation of DPD units to Angstrom
        double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
        // Get size of simulation box
        BoxSizeInfo tmpBoxSizeInfo = this.getBoxSizeInfo(aJobInputValueItemContainer);
        // Transform simulation box volume to Angstrom^3: (xLength * tmpLengthConversionFactor) * (yLength * tmpLengthConversionFactor) * (zLength * tmpLengthConversionFactor)
        double tmpSimulationBoxVolume = tmpBoxSizeInfo.getVolume() * tmpLengthConversionFactor * tmpLengthConversionFactor * tmpLengthConversionFactor;
        double tmpBoxLengthX = tmpBoxSizeInfo.getXLength() * tmpLengthConversionFactor;
        double tmpBoxLengthY = tmpBoxSizeInfo.getYLength() * tmpLengthConversionFactor;
        double tmpBoxLengthZ = tmpBoxSizeInfo.getZLength() * tmpLengthConversionFactor;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Write RDF files for aJobResultParticlePositionsFilePathnames">
        HashMap<String, String[]> tmpParticlePairRdfFilePathnameToStringArrayMap = new HashMap<String, String[]>(aJobResultParticlePositionsFilePathnames.length);
        for (String tmpJobResultParticlePositionsFilePathname : aJobResultParticlePositionsFilePathnames) {
            if (tmpJobResultParticlePositionsFilePathname == null || tmpJobResultParticlePositionsFilePathname.isEmpty() || !(new File(tmpJobResultParticlePositionsFilePathname)).isFile()) {
                return;
            }

            // Get particle positions (NOTE: Particle positions are already in Angstrom)
            HashMap<String, LinkedList<PointInSpace>> tmpParticleToPositionsMap = this.readParticlePositions(tmpJobResultParticlePositionsFilePathname, aJobInputValueItemContainer);
            if (tmpParticleToPositionsMap == null) {
                return;
            }
            // NOTE: For RDF calculation PBC in all directions are necessary
            DistanceDistributionUtils tmpDistanceDistributionUtils = 
                new DistanceDistributionUtils(
                    ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH,
                    tmpBoxLengthX, 
                    tmpBoxLengthY, 
                    tmpBoxLengthZ,
                    this.isPeriodicBoundaryX(aJobInputValueItemContainer),
                    this.isPeriodicBoundaryY(aJobInputValueItemContainer),
                    this.isPeriodicBoundaryZ(aJobInputValueItemContainer)
                );
            for (String[] tmpSingleParticlePair : tmpParticlePairs) {
                // <editor-fold defaultstate="collapsed" desc="Calculate distance bin frequencies">
                // NOTE: tmpParticleDensities[i] corresponds to tmpParticles[i]
                double[] tmpParticleDensities = new double[tmpSingleParticlePair.length];
                for (int i = 0; i < tmpSingleParticlePair.length; i++) {
                    int tmpNumber = this.getTotalNumberOfParticlesOfSpecifiedTypeInSimulation(tmpSingleParticlePair[i], aJobInputValueItemContainer);
                    tmpParticleDensities[i] = (double) tmpNumber / tmpSimulationBoxVolume;
                }
                double[] tmpParticleParticleDistanceBinFrequencies = null;
                if (tmpSingleParticlePair[0].equals(tmpSingleParticlePair[1])) {
                    PointInSpace[] tmpParticlePositions = tmpParticleToPositionsMap.get(tmpSingleParticlePair[0]).toArray(new PointInSpace[0]);
                    tmpParticleParticleDistanceBinFrequencies = tmpDistanceDistributionUtils.getEqualParticlePairDistanceBinFrequencies(tmpParticlePositions);
                } else {
                    PointInSpace[] tmpParticlePositionsA = tmpParticleToPositionsMap.get(tmpSingleParticlePair[0]).toArray(new PointInSpace[0]);
                    PointInSpace[] tmpParticlePositionsB = tmpParticleToPositionsMap.get(tmpSingleParticlePair[1]).toArray(new PointInSpace[0]);
                    tmpParticleParticleDistanceBinFrequencies = tmpDistanceDistributionUtils.getDifferentParticlePairDistanceBinFrequencies(tmpParticlePositionsA, tmpParticlePositionsB);
                }
                if (tmpParticleParticleDistanceBinFrequencies == null) {
                    return;
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Calculate RDF info">
                String tmpVersion = "Version 1.0.0";
                for (double tmpCurrentSegmentLength : ModelDefinitions.RDF_SEGMENT_LENGTHS) {
                    String tmpFirstParticle = tmpSingleParticlePair[0];
                    String tmpSecondParticle = tmpSingleParticlePair[1];

                    LinkedList<String> tmpStringList = new LinkedList<String>();
                    String[] tmpStringArray = null;
                    boolean tmpIsArrayAvailable = tmpParticlePairRdfFilePathnameToStringArrayMap.containsKey(
                        this.getJobResultParticlePairRdfFilePathname(aJobResultPath, tmpFirstParticle, tmpSecondParticle, tmpCurrentSegmentLength));
                    if (tmpIsArrayAvailable) {
                        tmpStringArray = tmpParticlePairRdfFilePathnameToStringArrayMap.get(
                            this.getJobResultParticlePairRdfFilePathname(aJobResultPath, tmpFirstParticle, tmpSecondParticle, tmpCurrentSegmentLength));
                    } else {
                        tmpStringList.add(tmpVersion);
                        tmpStringList.add(tmpFirstParticle);
                        tmpStringList.add(tmpSecondParticle);
                        tmpStringList.add(String.valueOf(tmpCurrentSegmentLength));
                    }

                    double tmpSecondParticleMeanDensity = tmpParticleDensities[1];

                    // Use integer arithmetics to avoid roundoff errors!
                    int tmpBasicMultiple = (int) ModelUtils.roundDoubleValue(tmpCurrentSegmentLength / ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH, 0);
                    int tmpStartMultiple = 0;
                    int tmpEndMultiple = tmpBasicMultiple;
                    int tmpArrayIndex = 5;
                    double tmpCurrentFrequency = 0.0;
                    for (int k = 0; k < tmpParticleParticleDistanceBinFrequencies.length; k++) {
                        if (k < tmpEndMultiple) {
                            tmpCurrentFrequency += tmpParticleParticleDistanceBinFrequencies[k];
                        } else {
                            double tmpStartSegmentLength = (double) tmpStartMultiple * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                            double tmpEndSegmentLength = (double) k * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                            // double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE * (Math.pow(tmpEndSegmentLength, 3.0) - Math.pow(tmpStartSegmentLength, 3.0));
                            double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE * (tmpEndSegmentLength * tmpEndSegmentLength * tmpEndSegmentLength - tmpStartSegmentLength * tmpStartSegmentLength * tmpStartSegmentLength);
                            double tmpRdfValue = (tmpCurrentFrequency / tmpVolumeOfSegment) / tmpSecondParticleMeanDensity;

                            if (tmpIsArrayAvailable) {
                                // NOTE: This if-condition is necessary since number of bins of different simulation boxes may be different which may lead to tmpArrayIndex >= tmpStringArray.length
                                if (tmpArrayIndex < tmpStringArray.length) {
                                    tmpStringArray[tmpArrayIndex] = tmpStringArray[tmpArrayIndex] + ModelDefinitions.GENERAL_SEPARATOR + String.valueOf(tmpRdfValue);
                                    tmpArrayIndex += 2;
                                }
                            } else {
                                tmpStringList.add(String.valueOf(tmpStartSegmentLength + (tmpEndSegmentLength - tmpStartSegmentLength) / 2.0));
                                tmpStringList.add(String.valueOf(tmpRdfValue));
                            }

                            tmpCurrentFrequency = tmpParticleParticleDistanceBinFrequencies[k];
                            tmpStartMultiple = k;
                            tmpEndMultiple += tmpBasicMultiple;
                        }
                    }
                    if (tmpIsArrayAvailable) {
                        tmpParticlePairRdfFilePathnameToStringArrayMap.remove(
                            this.getJobResultParticlePairRdfFilePathname(aJobResultPath, tmpFirstParticle, tmpSecondParticle, tmpCurrentSegmentLength));
                        tmpParticlePairRdfFilePathnameToStringArrayMap.put(
                            this.getJobResultParticlePairRdfFilePathname(aJobResultPath, tmpFirstParticle, tmpSecondParticle, tmpCurrentSegmentLength),
                            tmpStringArray);
                    } else {
                        tmpParticlePairRdfFilePathnameToStringArrayMap.put(
                            this.getJobResultParticlePairRdfFilePathname(aJobResultPath, tmpFirstParticle, tmpSecondParticle, tmpCurrentSegmentLength),
                            tmpStringList.toArray(new String[0]));
                    }
                }
                // </editor-fold>
            }
        }
        for (String tmpParticlePairRdfFilePathname : tmpParticlePairRdfFilePathnameToStringArrayMap.keySet()) {
            this.fileUtilityMethods.writeDefinedStringArrayToFile(
                tmpParticlePairRdfFilePathnameToStringArrayMap.get(tmpParticlePairRdfFilePathname),
                tmpParticlePairRdfFilePathname
            );
        }
        // </editor-fold>
    }

    /**
     * Creates defined particle-pair radial distribution function files by 
     * parallel (!) processing of simulation steps. 
     * NOTE: Necessary RDF directory of JobResult MUST already be created!
     * Note: See 
     * createDefinedParticlePairRadialDistributionFunctionFiles_Sequential()
     * for 1-to-1 comparison with (different) sequential implementation
     *
     * @param aJobResultParticlePositionsFilePathnames Full pathnames of
     * graphical particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @param aJobResultPath Path of JobResult
     */
    public void createDefinedParticlePairRadialDistributionFunctionFiles(
        String[] aJobResultParticlePositionsFilePathnames, 
        ValueItemContainer aJobInputValueItemContainer, 
        String aJobResultPath
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathnames == null || aJobResultParticlePositionsFilePathnames.length == 0) {
            return;
        }
        if (aJobInputValueItemContainer == null) {
            return;
        }
        // Get particle pairs for RDF calculation
        String[][] tmpParticlePairs = this.getParticlePairsForRdfCalculation(aJobInputValueItemContainer);
        if (tmpParticlePairs == null) {
            return;
        }
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            return;
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Prepare calculations">
        // Get length conversion factor for transformation of DPD units to Angstrom
        double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
        // Get size of simulation box
        BoxSizeInfo tmpBoxSizeInfo = this.getBoxSizeInfo(aJobInputValueItemContainer);
        // Transform simulation box volume to Angstrom^3: (xLength * tmpLengthConversionFactor) * (yLength * tmpLengthConversionFactor) * (zLength * tmpLengthConversionFactor)
        double tmpSimulationBoxVolume = tmpBoxSizeInfo.getVolume() * tmpLengthConversionFactor * tmpLengthConversionFactor * tmpLengthConversionFactor;
        double tmpBoxLengthX = tmpBoxSizeInfo.getXLength() * tmpLengthConversionFactor;
        double tmpBoxLengthY = tmpBoxSizeInfo.getYLength() * tmpLengthConversionFactor;
        double tmpBoxLengthZ = tmpBoxSizeInfo.getZLength() * tmpLengthConversionFactor;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Write RDF files for aJobResultParticlePositionsFilePathnames">
        ConcurrentLinkedQueue<IdKeyValue> tmpIdKeyValueList = new ConcurrentLinkedQueue<>();
        Stream<String> tmpJobResultParticlePositionsFilePathnameStream = Arrays.stream(aJobResultParticlePositionsFilePathnames);
        // Calculate particle-particle distances for RDF in parallel
        tmpJobResultParticlePositionsFilePathnameStream.parallel().forEach(tmpJobResultParticlePositionsFilePathname -> {
            if (!(
                    tmpJobResultParticlePositionsFilePathname == null || 
                    tmpJobResultParticlePositionsFilePathname.isEmpty() || 
                    !(new File(tmpJobResultParticlePositionsFilePathname)).isFile()
                )
            ) {
                // Get particle positions (NOTE: Particle positions are already in Angstrom)
                HashMap<String, LinkedList<PointInSpace>> tmpParticleToPositionsMap = 
                    this.readParticlePositions(
                        tmpJobResultParticlePositionsFilePathname, 
                        aJobInputValueItemContainer
                    );
                if (tmpParticleToPositionsMap != null) {
                    // NOTE: For RDF calculation PBC in all directions are necessary
                    DistanceDistributionUtils tmpDistanceDistributionUtils = 
                        new DistanceDistributionUtils(
                            ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH,
                            tmpBoxLengthX, 
                            tmpBoxLengthY, 
                            tmpBoxLengthZ,
                            this.isPeriodicBoundaryX(aJobInputValueItemContainer),
                            this.isPeriodicBoundaryY(aJobInputValueItemContainer),
                            this.isPeriodicBoundaryZ(aJobInputValueItemContainer)
                        );
                    for (String[] tmpSingleParticlePair : tmpParticlePairs) {
                        // <editor-fold defaultstate="collapsed" desc="Calculate distance bin frequencies and RDF info">
                        // NOTE: tmpParticleDensities[i] corresponds to tmpSingleParticlePair[i]
                        double[] tmpParticleDensities = new double[tmpSingleParticlePair.length];
                        for (int i = 0; i < tmpSingleParticlePair.length; i++) {
                            int tmpNumber = 
                                this.getTotalNumberOfParticlesOfSpecifiedTypeInSimulation(
                                    tmpSingleParticlePair[i], 
                                    aJobInputValueItemContainer
                                );
                            tmpParticleDensities[i] = (double) tmpNumber / tmpSimulationBoxVolume;
                        }
                        double[] tmpParticleParticleDistanceBinFrequencies = null;
                        if (tmpSingleParticlePair[0].equals(tmpSingleParticlePair[1])) {
                            PointInSpace[] tmpParticlePositions = tmpParticleToPositionsMap.get(tmpSingleParticlePair[0]).toArray(new PointInSpace[0]);
                            tmpParticleParticleDistanceBinFrequencies = tmpDistanceDistributionUtils.getEqualParticlePairDistanceBinFrequencies(tmpParticlePositions);
                        } else {
                            PointInSpace[] tmpParticlePositionsA = tmpParticleToPositionsMap.get(tmpSingleParticlePair[0]).toArray(new PointInSpace[0]);
                            PointInSpace[] tmpParticlePositionsB = tmpParticleToPositionsMap.get(tmpSingleParticlePair[1]).toArray(new PointInSpace[0]);
                            tmpParticleParticleDistanceBinFrequencies = 
                                tmpDistanceDistributionUtils.getDifferentParticlePairDistanceBinFrequencies(
                                    tmpParticlePositionsA, 
                                    tmpParticlePositionsB
                                );
                        }
                        if (tmpParticleParticleDistanceBinFrequencies != null) {
                            // <editor-fold defaultstate="collapsed" desc="Calculate RDF info">
                            String tmpVersion = "Version 1.0.0";
                            for (double tmpCurrentSegmentLength : ModelDefinitions.RDF_SEGMENT_LENGTHS) {
                                String tmpFirstParticle = tmpSingleParticlePair[0];
                                String tmpSecondParticle = tmpSingleParticlePair[1];

                                IdKeyValue tmpIdKeyValue = 
                                    new IdKeyValue(
                                        new String[] {
                                            this.getJobResultParticlePairRdfFilePathname(
                                                aJobResultPath, 
                                                tmpFirstParticle, 
                                                tmpSecondParticle, 
                                                tmpCurrentSegmentLength
                                            ),
                                            tmpFirstParticle, 
                                            tmpSecondParticle, 
                                            String.valueOf(tmpCurrentSegmentLength)
                                        }
                                    );

                                double tmpSecondParticleMeanDensity = tmpParticleDensities[1];

                                // Use integer arithmetics to avoid roundoff errors!
                                int tmpBasicMultiple = (int) ModelUtils.roundDoubleValue(tmpCurrentSegmentLength / ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH, 0);
                                int tmpStartMultiple = 0;
                                int tmpEndMultiple = tmpBasicMultiple;
                                double tmpCurrentFrequency = 0.0;
                                for (int k = 0; k < tmpParticleParticleDistanceBinFrequencies.length; k++) {
                                    if (k < tmpEndMultiple) {
                                        tmpCurrentFrequency += tmpParticleParticleDistanceBinFrequencies[k];
                                    } else {
                                        double tmpStartSegmentLength = (double) tmpStartMultiple * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                                        double tmpEndSegmentLength = (double) k * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                                        // double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE * (Math.pow(tmpEndSegmentLength, 3.0) - Math.pow(tmpStartSegmentLength, 3.0));
                                        double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE * (tmpEndSegmentLength * tmpEndSegmentLength * tmpEndSegmentLength - tmpStartSegmentLength * tmpStartSegmentLength * tmpStartSegmentLength);
                                        double tmpRdfValue = (tmpCurrentFrequency / tmpVolumeOfSegment) / tmpSecondParticleMeanDensity;

                                        tmpIdKeyValue.add(
                                            tmpStartSegmentLength + (tmpEndSegmentLength - tmpStartSegmentLength) / 2.0,
                                            tmpRdfValue
                                        );

                                        tmpCurrentFrequency = tmpParticleParticleDistanceBinFrequencies[k];
                                        tmpStartMultiple = k;
                                        tmpEndMultiple += tmpBasicMultiple;
                                    }
                                }
                                tmpIdKeyValueList.add(tmpIdKeyValue);
                            }
                            // </editor-fold>
                        }
                        // </editor-fold>
                    }
                }
            }
        });
        // Accumulate IdKeyValue items
        TreeMap<String, IdKeyValueAccumulator> tmpIdToAccumulatorMap = new TreeMap<>();
        for (IdKeyValue tmpIdKeyValue : tmpIdKeyValueList) {
            if (!tmpIdToAccumulatorMap.containsKey(tmpIdKeyValue.getId())) {
                tmpIdToAccumulatorMap.put(tmpIdKeyValue.getId(), new IdKeyValueAccumulator(tmpIdKeyValue.getIdParts()));
            }
            IdKeyValueAccumulator tmpAccumulator = tmpIdToAccumulatorMap.get(tmpIdKeyValue.getId());
            boolean tmpIsKey = false;
            double tmpKey = 0.0;
            double tmpValue;
            for (double tmpItem : tmpIdKeyValue.getKeyValueList()) {
                if (!tmpIsKey) {
                    tmpKey = tmpItem;
                    tmpIsKey = true;
                } else {
                    tmpValue = tmpItem;
                    tmpAccumulator.add(tmpKey, tmpValue);
                    tmpIsKey = false;
                }
            }
        }
        // Write consolidated RDF files
        String tmpVersion = "Version 1.0.0";
        for (IdKeyValueAccumulator tmpAccumulator : tmpIdToAccumulatorMap.values()) {
            String tmpJobResultParticlePairRdfFilePathname = tmpAccumulator.getIdParts()[0];
            LinkedList<String> tmpStringList = new LinkedList<>();
            tmpStringList.add(tmpVersion);
            // tmpFirstParticle
            tmpStringList.add(tmpAccumulator.getIdParts()[1]);
            // tmpSecondParticle, 
            tmpStringList.add(tmpAccumulator.getIdParts()[2]);
            // String.valueOf(tmpCurrentSegmentLength)
            tmpStringList.add(tmpAccumulator.getIdParts()[3]);
            
            TreeMap<Double, LinkedList<Double>> tmpKeyToValuesMap = tmpAccumulator.getKeyToValuesMap();
            for (double tmpkey : tmpKeyToValuesMap.keySet()) {
                tmpStringList.add(String.valueOf(tmpkey));
                LinkedList<Double> tmpValues= tmpKeyToValuesMap.get(tmpkey);
                // Roughly 20 characters for a double value with separator
                StringBuilder tmpBuffer = new StringBuilder(tmpValues.size() * 20);
                for (Double tmpValue : tmpValues) {
                    if (!tmpBuffer.isEmpty()) {
                        tmpBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);
                    }
                    tmpBuffer.append(String.valueOf(tmpValue));
                }
                tmpStringList.add(tmpBuffer.toString());
            }
            this.fileUtilityMethods.writeDefinedStringArrayToFile(
                tmpStringList.toArray(new String[0]),
                tmpJobResultParticlePairRdfFilePathname
            );
        }
        // </editor-fold>
    }

    /**
     * Creates defined molecule-particle-pair radial distribution function files 
     * by sequential (!) processing of simulation steps. 
     * NOTE: Necessary RDF directory of JobResult MUST already be created!
     *
     * @param aJobResultParticlePositionsFilePathnames Full pathnames of
     * graphical particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @param aJobResultPath Path of JobResult
     */
    public void createDefinedMoleculeParticlePairRadialDistributionFunctionFiles_Sequential(
        String[] aJobResultParticlePositionsFilePathnames, 
        ValueItemContainer aJobInputValueItemContainer, 
        String aJobResultPath
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathnames == null || aJobResultParticlePositionsFilePathnames.length == 0) {
            return;
        }
        if (aJobInputValueItemContainer == null) {
            return;
        }
        // Get molecule-particle pairs for RDF calculation
        String[][] tmpMoleculeParticlePairs = this.getMoleculeParticlePairsForRdfCalculation(aJobInputValueItemContainer);
        if (tmpMoleculeParticlePairs == null) {
            return;
        }
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            return;
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Prepare calculations">
        // Get length conversion factor for transformation of DPD units to Angstrom
        double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
        // Get size of simulation box
        BoxSizeInfo tmpBoxSizeInfo = this.getBoxSizeInfo(aJobInputValueItemContainer);
        // Transform simulation box volume to Angstrom^3: (xLength * tmpLengthConversionFactor) * (yLength * tmpLengthConversionFactor) * (zLength * tmpLengthConversionFactor)
        double tmpSimulationBoxVolume = tmpBoxSizeInfo.getVolume() * tmpLengthConversionFactor * tmpLengthConversionFactor * tmpLengthConversionFactor;
        double tmpBoxLengthX = tmpBoxSizeInfo.getXLength() * tmpLengthConversionFactor;
        double tmpBoxLengthY = tmpBoxSizeInfo.getYLength() * tmpLengthConversionFactor;
        double tmpBoxLengthZ = tmpBoxSizeInfo.getZLength() * tmpLengthConversionFactor;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Write RDF files for aJobResultParticlePositionsFilePathnames">
        HashMap<String, String[]> tmpMoleculeParticlePairRdfFilePathnameToStringArrayMap = new HashMap<String, String[]>(aJobResultParticlePositionsFilePathnames.length);
        for (String tmpJobResultParticlePositionsFilePathname : aJobResultParticlePositionsFilePathnames) {
            if (tmpJobResultParticlePositionsFilePathname == null || tmpJobResultParticlePositionsFilePathname.isEmpty() || !(new File(tmpJobResultParticlePositionsFilePathname)).isFile()) {
                return;
            }
            // Get particle positions (NOTE: Particle positions are already in Angstrom)
            HashMap<String, LinkedList<PointInSpace>> tmpMoleculeParticleToPositionsMap = this.readMoleculeParticlePositions(tmpJobResultParticlePositionsFilePathname, aJobInputValueItemContainer);
            if (tmpMoleculeParticleToPositionsMap == null) {
                return;
            }
            // NOTE: For RDF calculation PBC in all directions are necessary
            DistanceDistributionUtils tmpDistanceDistributionUtils = 
                    new DistanceDistributionUtils(
                        ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH,
                        tmpBoxLengthX, 
                        tmpBoxLengthY, 
                        tmpBoxLengthZ,
                        this.isPeriodicBoundaryX(aJobInputValueItemContainer),
                        this.isPeriodicBoundaryY(aJobInputValueItemContainer),
                        this.isPeriodicBoundaryZ(aJobInputValueItemContainer)
                    );
            for (String[] tmpSingleMoleculeParticlePair : tmpMoleculeParticlePairs) {
                // <editor-fold defaultstate="collapsed" desc="Calculate distance bin frequencies">
                // NOTE: tmpParticleDensities[i] corresponds to tmpParticles[i]
                double[] tmpMoleculeParticleDensities = new double[tmpSingleMoleculeParticlePair.length];
                for (int i = 0; i < tmpSingleMoleculeParticlePair.length; i++) {
                    String[] tmpMoleculeNameAndParticle = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(tmpSingleMoleculeParticlePair[i]);
                    String tmpMoleculeName = tmpMoleculeNameAndParticle[0];
                    String tmpParticle = tmpMoleculeNameAndParticle[1];
                    int tmpNumber = this.getTotalNumberOfMoleculeParticlesOfSpecifiedTypeInSimulation(tmpMoleculeName, tmpParticle, aJobInputValueItemContainer);
                    tmpMoleculeParticleDensities[i] = (double) tmpNumber / tmpSimulationBoxVolume;
                }
                double[] tmpParticleParticleDistanceBinFrequencies = null;
                if (tmpSingleMoleculeParticlePair[0].equals(tmpSingleMoleculeParticlePair[1])) {
                    PointInSpace[] tmpMoleculeParticlePositions = tmpMoleculeParticleToPositionsMap.get(tmpSingleMoleculeParticlePair[0]).toArray(new PointInSpace[0]);
                    tmpParticleParticleDistanceBinFrequencies = tmpDistanceDistributionUtils.getEqualParticlePairDistanceBinFrequencies(tmpMoleculeParticlePositions);
                } else {
                    PointInSpace[] tmpMoleculeParticlePositionsA = tmpMoleculeParticleToPositionsMap.get(tmpSingleMoleculeParticlePair[0]).toArray(new PointInSpace[0]);
                    PointInSpace[] tmpMoleculeParticlePositionsB = tmpMoleculeParticleToPositionsMap.get(tmpSingleMoleculeParticlePair[1]).toArray(new PointInSpace[0]);
                    tmpParticleParticleDistanceBinFrequencies = tmpDistanceDistributionUtils.getDifferentParticlePairDistanceBinFrequencies(tmpMoleculeParticlePositionsA, tmpMoleculeParticlePositionsB);
                }
                if (tmpParticleParticleDistanceBinFrequencies == null) {
                    return;
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Calculate RDF info">
                String tmpVersion = "Version 1.0.0";
                for (double tmpCurrentSegmentLength : ModelDefinitions.RDF_SEGMENT_LENGTHS) {
                    String tmpFirstMoleculeParticle = tmpSingleMoleculeParticlePair[0];
                    String tmpSecondMoleculeParticle = tmpSingleMoleculeParticlePair[1];

                    LinkedList<String> tmpStringList = new LinkedList<String>();
                    String[] tmpStringArray = null;
                    boolean tmpIsArrayAvailable = tmpMoleculeParticlePairRdfFilePathnameToStringArrayMap.containsKey(
                        this.getJobResultMoleculeParticlePairRdfFilePathname(aJobResultPath, tmpFirstMoleculeParticle, tmpSecondMoleculeParticle, tmpCurrentSegmentLength));
                    if (tmpIsArrayAvailable) {
                        tmpStringArray = tmpMoleculeParticlePairRdfFilePathnameToStringArrayMap.get(
                            this.getJobResultMoleculeParticlePairRdfFilePathname(aJobResultPath, tmpFirstMoleculeParticle, tmpSecondMoleculeParticle, tmpCurrentSegmentLength));
                    } else {
                        tmpStringList.add(tmpVersion);
                        tmpStringList.add(tmpFirstMoleculeParticle);
                        tmpStringList.add(tmpSecondMoleculeParticle);
                        tmpStringList.add(String.valueOf(tmpCurrentSegmentLength));
                    }

                    double tmpSecondParticleMeanDensity = tmpMoleculeParticleDensities[1];

                    // Use integer arithmetics to avoid roundoff errors!
                    int tmpBasicMultiple = (int) ModelUtils.roundDoubleValue(tmpCurrentSegmentLength / ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH, 0);
                    int tmpStartMultiple = 0;
                    int tmpEndMultiple = tmpBasicMultiple;
                    int tmpArrayIndex = 5;
                    double tmpCurrentFrequency = 0.0;
                    for (int k = 0; k < tmpParticleParticleDistanceBinFrequencies.length; k++) {
                        if (k < tmpEndMultiple) {
                            tmpCurrentFrequency += tmpParticleParticleDistanceBinFrequencies[k];
                        } else {
                            double tmpStartSegmentLength = (double) tmpStartMultiple * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                            double tmpEndSegmentLength = (double) k * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                            // double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE * (Math.pow(tmpEndSegmentLength, 3.0) - Math.pow(tmpStartSegmentLength, 3.0));
                            double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE
                                    * (tmpEndSegmentLength * tmpEndSegmentLength * tmpEndSegmentLength - tmpStartSegmentLength * tmpStartSegmentLength * tmpStartSegmentLength);
                            double tmpRdfValue = (tmpCurrentFrequency / tmpVolumeOfSegment) / tmpSecondParticleMeanDensity;

                            if (tmpIsArrayAvailable) {
                                // NOTE: This if-condition is necessary since number of bins of different simulation boxes may be different which may lead to tmpArrayIndex >= tmpStringArray.length
                                if (tmpArrayIndex < tmpStringArray.length) {
                                    tmpStringArray[tmpArrayIndex] = tmpStringArray[tmpArrayIndex] + ModelDefinitions.GENERAL_SEPARATOR + String.valueOf(tmpRdfValue);
                                    tmpArrayIndex += 2;
                                }
                            } else {
                                tmpStringList.add(String.valueOf(tmpStartSegmentLength + (tmpEndSegmentLength - tmpStartSegmentLength) / 2.0));
                                tmpStringList.add(String.valueOf(tmpRdfValue));
                            }

                            tmpCurrentFrequency = tmpParticleParticleDistanceBinFrequencies[k];
                            tmpStartMultiple = k;
                            tmpEndMultiple += tmpBasicMultiple;
                        }
                    }
                    if (tmpIsArrayAvailable) {
                        tmpMoleculeParticlePairRdfFilePathnameToStringArrayMap.remove(
                            this.getJobResultMoleculeParticlePairRdfFilePathname(aJobResultPath, tmpFirstMoleculeParticle, tmpSecondMoleculeParticle, tmpCurrentSegmentLength));
                        tmpMoleculeParticlePairRdfFilePathnameToStringArrayMap.put(
                            this.getJobResultMoleculeParticlePairRdfFilePathname(aJobResultPath, tmpFirstMoleculeParticle, tmpSecondMoleculeParticle, tmpCurrentSegmentLength),
                            tmpStringArray);
                    } else {
                        tmpMoleculeParticlePairRdfFilePathnameToStringArrayMap.put(
                            this.getJobResultMoleculeParticlePairRdfFilePathname(aJobResultPath, tmpFirstMoleculeParticle, tmpSecondMoleculeParticle, tmpCurrentSegmentLength),
                            tmpStringList.toArray(new String[0]));
                    }
                }

                // </editor-fold>
            }
        }
        for (String tmpMoleculeParticlePairRdfFilePathname : tmpMoleculeParticlePairRdfFilePathnameToStringArrayMap.keySet()) {
            this.fileUtilityMethods.writeDefinedStringArrayToFile(
                tmpMoleculeParticlePairRdfFilePathnameToStringArrayMap.get(tmpMoleculeParticlePairRdfFilePathname),
                tmpMoleculeParticlePairRdfFilePathname
            );
        }
        // </editor-fold>
    }

    /**
     * Creates defined molecule-particle-pair radial distribution function files 
     * by parallel (!) processing of simulation steps. 
     * NOTE: Necessary RDF directory of JobResult MUST already be created!
     * Note: See 
     * createDefinedMoleculeParticlePairRadialDistributionFunctionFiles_Sequential()
     * for 1-to-1 comparison with (different) sequential implementation
     *
     * @param aJobResultParticlePositionsFilePathnames Full pathnames of
     * graphical particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @param aJobResultPath Path of JobResult
     */
    public void createDefinedMoleculeParticlePairRadialDistributionFunctionFiles(
        String[] aJobResultParticlePositionsFilePathnames, 
        ValueItemContainer aJobInputValueItemContainer, 
        String aJobResultPath
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathnames == null || aJobResultParticlePositionsFilePathnames.length == 0) {
            return;
        }
        if (aJobInputValueItemContainer == null) {
            return;
        }
        // Get molecule-particle pairs for RDF calculation
        String[][] tmpMoleculeParticlePairs = this.getMoleculeParticlePairsForRdfCalculation(aJobInputValueItemContainer);
        if (tmpMoleculeParticlePairs == null) {
            return;
        }
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            return;
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Prepare calculations">
        // Get length conversion factor for transformation of DPD units to Angstrom
        double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
        // Get size of simulation box
        BoxSizeInfo tmpBoxSizeInfo = this.getBoxSizeInfo(aJobInputValueItemContainer);
        // Transform simulation box volume to Angstrom^3: (xLength * tmpLengthConversionFactor) * (yLength * tmpLengthConversionFactor) * (zLength * tmpLengthConversionFactor)
        double tmpSimulationBoxVolume = tmpBoxSizeInfo.getVolume() * tmpLengthConversionFactor * tmpLengthConversionFactor * tmpLengthConversionFactor;
        double tmpBoxLengthX = tmpBoxSizeInfo.getXLength() * tmpLengthConversionFactor;
        double tmpBoxLengthY = tmpBoxSizeInfo.getYLength() * tmpLengthConversionFactor;
        double tmpBoxLengthZ = tmpBoxSizeInfo.getZLength() * tmpLengthConversionFactor;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Write RDF files for aJobResultParticlePositionsFilePathnames">
        ConcurrentLinkedQueue<IdKeyValue> tmpIdKeyValueList = new ConcurrentLinkedQueue<>();
        Stream<String> tmpJobResultParticlePositionsFilePathnameStream = Arrays.stream(aJobResultParticlePositionsFilePathnames);
        // Calculate particle-particle distances for RDF in parallel
        tmpJobResultParticlePositionsFilePathnameStream.parallel().forEach(tmpJobResultParticlePositionsFilePathname -> {
            if (!(
                    tmpJobResultParticlePositionsFilePathname == null || 
                    tmpJobResultParticlePositionsFilePathname.isEmpty() || 
                    !(new File(tmpJobResultParticlePositionsFilePathname)).isFile()
                )
            ) {
                // Get particle positions (NOTE: Particle positions are already in Angstrom)
                HashMap<String, LinkedList<PointInSpace>> tmpMoleculeParticleToPositionsMap = 
                    this.readMoleculeParticlePositions(
                        tmpJobResultParticlePositionsFilePathname, 
                        aJobInputValueItemContainer
                    );
                if (tmpMoleculeParticleToPositionsMap != null) {
                    // NOTE: For RDF calculation PBC in all directions are necessary
                    DistanceDistributionUtils tmpDistanceDistributionUtils = 
                        new DistanceDistributionUtils(
                            ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH,
                            tmpBoxLengthX, 
                            tmpBoxLengthY, 
                            tmpBoxLengthZ,
                            this.isPeriodicBoundaryX(aJobInputValueItemContainer),
                            this.isPeriodicBoundaryY(aJobInputValueItemContainer),
                            this.isPeriodicBoundaryZ(aJobInputValueItemContainer)
                        );
                    for (String[] tmpSingleMoleculeParticlePair : tmpMoleculeParticlePairs) {
                        // <editor-fold defaultstate="collapsed" desc="Calculate distance bin frequencies and RDF info">
                        // NOTE: tmpMoleculeParticleDensities[i] corresponds to tmpSingleMoleculeParticlePair[i]
                        double[] tmpMoleculeParticleDensities = new double[tmpSingleMoleculeParticlePair.length];
                        for (int i = 0; i < tmpSingleMoleculeParticlePair.length; i++) {
                            String[] tmpMoleculeNameAndParticle = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(tmpSingleMoleculeParticlePair[i]);
                            String tmpMoleculeName = tmpMoleculeNameAndParticle[0];
                            String tmpParticle = tmpMoleculeNameAndParticle[1];
                            int tmpNumber = this.getTotalNumberOfMoleculeParticlesOfSpecifiedTypeInSimulation(tmpMoleculeName, tmpParticle, aJobInputValueItemContainer);
                            tmpMoleculeParticleDensities[i] = (double) tmpNumber / tmpSimulationBoxVolume;
                        }
                        double[] tmpParticleParticleDistanceBinFrequencies = null;
                        if (tmpSingleMoleculeParticlePair[0].equals(tmpSingleMoleculeParticlePair[1])) {
                            PointInSpace[] tmpMoleculeParticlePositions = tmpMoleculeParticleToPositionsMap.get(tmpSingleMoleculeParticlePair[0]).toArray(new PointInSpace[0]);
                            tmpParticleParticleDistanceBinFrequencies = tmpDistanceDistributionUtils.getEqualParticlePairDistanceBinFrequencies(tmpMoleculeParticlePositions);
                        } else {
                            PointInSpace[] tmpMoleculeParticlePositionsA = tmpMoleculeParticleToPositionsMap.get(tmpSingleMoleculeParticlePair[0]).toArray(new PointInSpace[0]);
                            PointInSpace[] tmpMoleculeParticlePositionsB = tmpMoleculeParticleToPositionsMap.get(tmpSingleMoleculeParticlePair[1]).toArray(new PointInSpace[0]);
                            tmpParticleParticleDistanceBinFrequencies = 
                                tmpDistanceDistributionUtils.getDifferentParticlePairDistanceBinFrequencies(
                                    tmpMoleculeParticlePositionsA, 
                                    tmpMoleculeParticlePositionsB
                                );
                        }
                        if (tmpParticleParticleDistanceBinFrequencies != null) {
                            // <editor-fold defaultstate="collapsed" desc="Calculate RDF info">
                            for (double tmpCurrentSegmentLength : ModelDefinitions.RDF_SEGMENT_LENGTHS) {
                                String tmpFirstMoleculeParticle = tmpSingleMoleculeParticlePair[0];
                                String tmpSecondMoleculeParticle = tmpSingleMoleculeParticlePair[1];

                                IdKeyValue tmpIdKeyValue = 
                                    new IdKeyValue(
                                        new String[] {
                                            this.getJobResultMoleculeParticlePairRdfFilePathname(
                                                aJobResultPath, 
                                                tmpFirstMoleculeParticle, 
                                                tmpSecondMoleculeParticle, 
                                                tmpCurrentSegmentLength
                                            ),
                                            tmpFirstMoleculeParticle, 
                                            tmpSecondMoleculeParticle, 
                                            String.valueOf(tmpCurrentSegmentLength)
                                        }
                                    );

                                double tmpSecondMoleculeParticleMeanDensity = tmpMoleculeParticleDensities[1];

                                // Use integer arithmetics to avoid roundoff errors!
                                int tmpBasicMultiple = (int) ModelUtils.roundDoubleValue(tmpCurrentSegmentLength / ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH, 0);
                                int tmpStartMultiple = 0;
                                int tmpEndMultiple = tmpBasicMultiple;
                                double tmpCurrentFrequency = 0.0;
                                for (int k = 0; k < tmpParticleParticleDistanceBinFrequencies.length; k++) {
                                    if (k < tmpEndMultiple) {
                                        tmpCurrentFrequency += tmpParticleParticleDistanceBinFrequencies[k];
                                    } else {
                                        double tmpStartSegmentLength = (double) tmpStartMultiple * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                                        double tmpEndSegmentLength = (double) k * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                                        // double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE * (Math.pow(tmpEndSegmentLength, 3.0) - Math.pow(tmpStartSegmentLength, 3.0));
                                        double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE
                                                * (tmpEndSegmentLength * tmpEndSegmentLength * tmpEndSegmentLength - tmpStartSegmentLength * tmpStartSegmentLength * tmpStartSegmentLength);
                                        double tmpRdfValue = (tmpCurrentFrequency / tmpVolumeOfSegment) / tmpSecondMoleculeParticleMeanDensity;

                                        tmpIdKeyValue.add(
                                            tmpStartSegmentLength + (tmpEndSegmentLength - tmpStartSegmentLength) / 2.0,
                                            tmpRdfValue
                                        );
                                        
                                        tmpCurrentFrequency = tmpParticleParticleDistanceBinFrequencies[k];
                                        tmpStartMultiple = k;
                                        tmpEndMultiple += tmpBasicMultiple;
                                    }
                                }
                                tmpIdKeyValueList.add(tmpIdKeyValue);
                            }
                            // </editor-fold>
                        }
                        // </editor-fold>
                    }
                }
            }
        });
        // Accumulate IdKeyValue items
        TreeMap<String, IdKeyValueAccumulator> tmpIdToAccumulatorMap = new TreeMap<>();
        for (IdKeyValue tmpIdKeyValue : tmpIdKeyValueList) {
            if (!tmpIdToAccumulatorMap.containsKey(tmpIdKeyValue.getId())) {
                tmpIdToAccumulatorMap.put(tmpIdKeyValue.getId(), new IdKeyValueAccumulator(tmpIdKeyValue.getIdParts()));
            }
            IdKeyValueAccumulator tmpAccumulator = tmpIdToAccumulatorMap.get(tmpIdKeyValue.getId());
            boolean tmpIsKey = false;
            double tmpKey = 0.0;
            double tmpValue;
            for (double tmpItem : tmpIdKeyValue.getKeyValueList()) {
                if (!tmpIsKey) {
                    tmpKey = tmpItem;
                    tmpIsKey = true;
                } else {
                    tmpValue = tmpItem;
                    tmpAccumulator.add(tmpKey, tmpValue);
                    tmpIsKey = false;
                }
            }
        }
        // Write consolidated RDF files
        String tmpVersion = "Version 1.0.0";
        for (IdKeyValueAccumulator tmpAccumulator : tmpIdToAccumulatorMap.values()) {
            String tmpJobResultMoleculeParticlePairRdfFilePathname = tmpAccumulator.getIdParts()[0];
            LinkedList<String> tmpStringList = new LinkedList<>();
            tmpStringList.add(tmpVersion);
            // tmpFirstParticle
            tmpStringList.add(tmpAccumulator.getIdParts()[1]);
            // tmpSecondParticle, 
            tmpStringList.add(tmpAccumulator.getIdParts()[2]);
            // String.valueOf(tmpCurrentSegmentLength)
            tmpStringList.add(tmpAccumulator.getIdParts()[3]);
            
            TreeMap<Double, LinkedList<Double>> tmpKeyToValuesMap = tmpAccumulator.getKeyToValuesMap();
            for (double tmpkey : tmpKeyToValuesMap.keySet()) {
                tmpStringList.add(String.valueOf(tmpkey));
                LinkedList<Double> tmpValues= tmpKeyToValuesMap.get(tmpkey);
                // Roughly 20 characters for a double value with separator
                StringBuilder tmpBuffer = new StringBuilder(tmpValues.size() * 20);
                for (Double tmpValue : tmpValues) {
                    if (!tmpBuffer.isEmpty()) {
                        tmpBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);
                    }
                    tmpBuffer.append(String.valueOf(tmpValue));
                }
                tmpStringList.add(tmpBuffer.toString());
            }
            this.fileUtilityMethods.writeDefinedStringArrayToFile(
                tmpStringList.toArray(new String[0]),
                tmpJobResultMoleculeParticlePairRdfFilePathname
            );
        }
        // </editor-fold>
    }

    /**
     * Creates defined molecule-center-pair radial distribution function files 
     * by parallel (!) processing of simulation steps. 
     * NOTE: Necessary RDF directory of JobResult MUST already be created!
     *
     * @param aJobResultParticlePositionsFilePathnames Full pathnames of
     * graphical particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @param aJobResultPath Path of JobResult
     */
    public void createDefinedMoleculeCenterPairRadialDistributionFunctionFiles(
        String[] aJobResultParticlePositionsFilePathnames, 
        ValueItemContainer aJobInputValueItemContainer, 
        String aJobResultPath
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathnames == null || aJobResultParticlePositionsFilePathnames.length == 0) {
            return;
        }
        if (aJobInputValueItemContainer == null) {
            return;
        }
        // Get molecule-particle pairs for RDF calculation
        String[][] tmpMoleculeCenterPairs = this.getMoleculeCenterPairsForRdfCalculation(aJobInputValueItemContainer);
        if (tmpMoleculeCenterPairs == null) {
            return;
        }
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            return;
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Prepare calculations">
        // Get length conversion factor for transformation of DPD units to Angstrom
        double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
        // Get size of simulation box
        BoxSizeInfo tmpBoxSizeInfo = this.getBoxSizeInfo(aJobInputValueItemContainer);
        // Transform simulation box volume to Angstrom^3: (xLength * tmpLengthConversionFactor) * (yLength * tmpLengthConversionFactor) * (zLength * tmpLengthConversionFactor)
        double tmpSimulationBoxVolume = tmpBoxSizeInfo.getVolume() * tmpLengthConversionFactor * tmpLengthConversionFactor * tmpLengthConversionFactor;
        double tmpBoxLengthX = tmpBoxSizeInfo.getXLength() * tmpLengthConversionFactor;
        double tmpBoxLengthY = tmpBoxSizeInfo.getYLength() * tmpLengthConversionFactor;
        double tmpBoxLengthZ = tmpBoxSizeInfo.getZLength() * tmpLengthConversionFactor;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Write RDF files for aJobResultParticlePositionsFilePathnames">
        ConcurrentLinkedQueue<IdKeyValue> tmpIdKeyValueList = new ConcurrentLinkedQueue<>();
        Stream<String> tmpJobResultParticlePositionsFilePathnameStream = Arrays.stream(aJobResultParticlePositionsFilePathnames);
        // Calculate molecule-center-to-molecule-center distances for RDF in parallel
        tmpJobResultParticlePositionsFilePathnameStream.parallel().forEach(tmpJobResultParticlePositionsFilePathname -> {
            if (!(
                    tmpJobResultParticlePositionsFilePathname == null || 
                    tmpJobResultParticlePositionsFilePathname.isEmpty() || 
                    !(new File(tmpJobResultParticlePositionsFilePathname)).isFile()
                )
            ) {
                // Get molecule-center positions (NOTE: Molecule-center positions are already in Angstrom)
                HashMap<String, LinkedList<PointInSpace>> tmpMoleculeCenterToPositionsMap = 
                    this.readMoleculeCenterPositions(
                        tmpJobResultParticlePositionsFilePathname, 
                        aJobInputValueItemContainer
                    );
                if (tmpMoleculeCenterToPositionsMap != null) {
                    // NOTE: For RDF calculation PBC in all directions are necessary
                    DistanceDistributionUtils tmpDistanceDistributionUtils = 
                        new DistanceDistributionUtils(
                            ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH,
                            tmpBoxLengthX, 
                            tmpBoxLengthY, 
                            tmpBoxLengthZ,
                            this.isPeriodicBoundaryX(aJobInputValueItemContainer),
                            this.isPeriodicBoundaryY(aJobInputValueItemContainer),
                            this.isPeriodicBoundaryZ(aJobInputValueItemContainer)
                        );
                    for (String[] tmpSingleMoleculeCenterPair : tmpMoleculeCenterPairs) {
                        // <editor-fold defaultstate="collapsed" desc="Calculate distance bin frequencies and RDF info">
                        // NOTE: tmpMoleculeCenterDensities[i] corresponds to tmpSingleMoleculeCenterPair[i]
                        double[] tmpMoleculeCenterDensities = new double[tmpSingleMoleculeCenterPair.length];
                        for (int i = 0; i < tmpSingleMoleculeCenterPair.length; i++) {
                            String tmpMoleculeName = tmpSingleMoleculeCenterPair[i];
                            int tmpNumber = 
                                this.getTotalNumberOfMoleculesOfSpecifiedTypeInSimulation(
                                    tmpMoleculeName, 
                                    aJobInputValueItemContainer
                                );
                            tmpMoleculeCenterDensities[i] = (double) tmpNumber / tmpSimulationBoxVolume;
                        }
                        double[] tmpMoleculeCenterMoleculeCenterDistanceBinFrequencies = null;
                        if (tmpSingleMoleculeCenterPair[0].equals(tmpSingleMoleculeCenterPair[1])) {
                            PointInSpace[] tmpMoleculeCenterPositions = 
                                tmpMoleculeCenterToPositionsMap.get(tmpSingleMoleculeCenterPair[0]).toArray(new PointInSpace[0]);
                            tmpMoleculeCenterMoleculeCenterDistanceBinFrequencies = 
                                tmpDistanceDistributionUtils.getEqualParticlePairDistanceBinFrequencies(
                                    tmpMoleculeCenterPositions
                                );
                        } else {
                            PointInSpace[] tmpMoleculeCenterPositionsA = 
                                tmpMoleculeCenterToPositionsMap.get(tmpSingleMoleculeCenterPair[0]).toArray(new PointInSpace[0]);
                            PointInSpace[] tmpMoleculeCenterPositionsB = 
                                tmpMoleculeCenterToPositionsMap.get(tmpSingleMoleculeCenterPair[1]).toArray(new PointInSpace[0]);
                            tmpMoleculeCenterMoleculeCenterDistanceBinFrequencies = 
                                tmpDistanceDistributionUtils.getDifferentParticlePairDistanceBinFrequencies(
                                    tmpMoleculeCenterPositionsA, 
                                    tmpMoleculeCenterPositionsB
                                );
                        }
                        if (tmpMoleculeCenterMoleculeCenterDistanceBinFrequencies != null) {
                            // <editor-fold defaultstate="collapsed" desc="Calculate RDF info">
                            for (double tmpCurrentSegmentLength : ModelDefinitions.RDF_SEGMENT_LENGTHS) {
                                String tmpFirstMoleculeCenter = tmpSingleMoleculeCenterPair[0];
                                String tmpSecondMoleculeCenter = tmpSingleMoleculeCenterPair[1];

                                IdKeyValue tmpIdKeyValue = 
                                    new IdKeyValue(
                                        new String[] {
                                            this.getJobResultMoleculeCenterPairRdfFilePathname(
                                                aJobResultPath, 
                                                tmpFirstMoleculeCenter, 
                                                tmpSecondMoleculeCenter, 
                                                tmpCurrentSegmentLength
                                            ),
                                            tmpFirstMoleculeCenter, 
                                            tmpSecondMoleculeCenter, 
                                            String.valueOf(tmpCurrentSegmentLength)
                                        }
                                    );

                                double tmpSecondMoleculeCenterMeanDensity = tmpMoleculeCenterDensities[1];

                                // Use integer arithmetics to avoid roundoff errors!
                                int tmpBasicMultiple = (int) ModelUtils.roundDoubleValue(tmpCurrentSegmentLength / ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH, 0);
                                int tmpStartMultiple = 0;
                                int tmpEndMultiple = tmpBasicMultiple;
                                double tmpCurrentFrequency = 0.0;
                                for (int k = 0; k < tmpMoleculeCenterMoleculeCenterDistanceBinFrequencies.length; k++) {
                                    if (k < tmpEndMultiple) {
                                        tmpCurrentFrequency += tmpMoleculeCenterMoleculeCenterDistanceBinFrequencies[k];
                                    } else {
                                        double tmpStartSegmentLength = (double) tmpStartMultiple * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                                        double tmpEndSegmentLength = (double) k * ModelDefinitions.RDF_BASIC_SEGMENT_LENGTH;
                                        // double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE * (Math.pow(tmpEndSegmentLength, 3.0) - Math.pow(tmpStartSegmentLength, 3.0));
                                        double tmpVolumeOfSegment = FOUR_PI_DIVIDED_BY_THREE
                                                * (tmpEndSegmentLength * tmpEndSegmentLength * tmpEndSegmentLength - tmpStartSegmentLength * tmpStartSegmentLength * tmpStartSegmentLength);
                                        double tmpRdfValue = (tmpCurrentFrequency / tmpVolumeOfSegment) / tmpSecondMoleculeCenterMeanDensity;

                                        tmpIdKeyValue.add(
                                            tmpStartSegmentLength + (tmpEndSegmentLength - tmpStartSegmentLength) / 2.0,
                                            tmpRdfValue
                                        );
                                        
                                        tmpCurrentFrequency = tmpMoleculeCenterMoleculeCenterDistanceBinFrequencies[k];
                                        tmpStartMultiple = k;
                                        tmpEndMultiple += tmpBasicMultiple;
                                    }
                                }
                                tmpIdKeyValueList.add(tmpIdKeyValue);
                            }
                            // </editor-fold>
                        }
                        // </editor-fold>
                    }
                }
            }
        });
        // Accumulate IdKeyValue items
        TreeMap<String, IdKeyValueAccumulator> tmpIdToAccumulatorMap = new TreeMap<>();
        for (IdKeyValue tmpIdKeyValue : tmpIdKeyValueList) {
            if (!tmpIdToAccumulatorMap.containsKey(tmpIdKeyValue.getId())) {
                tmpIdToAccumulatorMap.put(tmpIdKeyValue.getId(), new IdKeyValueAccumulator(tmpIdKeyValue.getIdParts()));
            }
            IdKeyValueAccumulator tmpAccumulator = tmpIdToAccumulatorMap.get(tmpIdKeyValue.getId());
            boolean tmpIsKey = false;
            double tmpKey = 0.0;
            double tmpValue;
            for (double tmpItem : tmpIdKeyValue.getKeyValueList()) {
                if (!tmpIsKey) {
                    tmpKey = tmpItem;
                    tmpIsKey = true;
                } else {
                    tmpValue = tmpItem;
                    tmpAccumulator.add(tmpKey, tmpValue);
                    tmpIsKey = false;
                }
            }
        }
        // Write consolidated RDF files
        String tmpVersion = "Version 1.0.0";
        for (IdKeyValueAccumulator tmpAccumulator : tmpIdToAccumulatorMap.values()) {
            String tmpJobResultMoleculeCenterPairRdfFilePathname = tmpAccumulator.getIdParts()[0];
            LinkedList<String> tmpStringList = new LinkedList<>();
            tmpStringList.add(tmpVersion);
            // tmpFirstParticle
            tmpStringList.add(tmpAccumulator.getIdParts()[1]);
            // tmpSecondParticle, 
            tmpStringList.add(tmpAccumulator.getIdParts()[2]);
            // String.valueOf(tmpCurrentSegmentLength)
            tmpStringList.add(tmpAccumulator.getIdParts()[3]);
            
            TreeMap<Double, LinkedList<Double>> tmpKeyToValuesMap = tmpAccumulator.getKeyToValuesMap();
            for (double tmpkey : tmpKeyToValuesMap.keySet()) {
                tmpStringList.add(String.valueOf(tmpkey));
                LinkedList<Double> tmpValues= tmpKeyToValuesMap.get(tmpkey);
                // Roughly 20 characters for a double value with separator
                StringBuilder tmpBuffer = new StringBuilder(tmpValues.size() * 20);
                for (Double tmpValue : tmpValues) {
                    if (!tmpBuffer.isEmpty()) {
                        tmpBuffer.append(ModelDefinitions.GENERAL_SEPARATOR);
                    }
                    tmpBuffer.append(String.valueOf(tmpValue));
                }
                tmpStringList.add(tmpBuffer.toString());
            }
            this.fileUtilityMethods.writeDefinedStringArrayToFile(
                tmpStringList.toArray(new String[0]),
                tmpJobResultMoleculeCenterPairRdfFilePathname
            );
        }
        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle-Particle distance related methods">
    /**
     * Returns if Job Input value item container defines particle-pair distance
     * calculation
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return True: Job Input value item container defines particle-pair
     * distance calculation, false: Otherwise
     */
    public boolean isParticlePairDistanceCalculation(ValueItemContainer aJobInputValueItemContainer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return false;
        }

        // </editor-fold>
        ValueItem tmpParticlePairDistanceCalculationValueItem = aJobInputValueItemContainer.getValueItem("ParticlePairDistanceCalculation");
        if (tmpParticlePairDistanceCalculationValueItem == null) {
            return false;
        }
        return tmpParticlePairDistanceCalculationValueItem.isActive();
    }

    /**
     * Returns if Job Input value item container defines
     * molecule-particle-pair distance calculation
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return True: Job Input value item container defines
     * molecule-particle-pair distance calculation, false: Otherwise
     */
    public boolean isMoleculeParticlePairDistanceCalculation(ValueItemContainer aJobInputValueItemContainer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return false;
        }

        // </editor-fold>
        ValueItem tmpMoleculeParticlePairDistanceCalculationValueItem = aJobInputValueItemContainer.getValueItem("MoleculeParticlePairDistanceCalculation");
        if (tmpMoleculeParticlePairDistanceCalculationValueItem == null) {
            return false;
        }
        return tmpMoleculeParticlePairDistanceCalculationValueItem.isActive();
    }

    /**
     * Creates all defined particle-pair average distance files. NOTE: Necessary
     * particle distance directory of JobResult MUST already be created!
     *
     * @param aJobResultPath Full path of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     */
    public void createDefinedParticlePairAverageDistanceFiles(String aJobResultPath, ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            return;
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            return;
        }
        if (aJobInputValueItemContainer == null) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initial settings">
        String[] tmpJobResultParticlePositionsStepFilePathnames = this.getJobResultParticlePositionsStepFilePathnames(aJobResultPath);
        HashMap<String, String> tmpStepToFilePathnameMap = null;
        HashMap<String, String> tmpSortStringToStepMap = null;
        LinkedList<String> tmpStepSortStringList = new LinkedList<String>();
        if (tmpJobResultParticlePositionsStepFilePathnames != null && tmpJobResultParticlePositionsStepFilePathnames.length > 0) {
            int tmpMaximumStepNumber = -1;
            tmpStepToFilePathnameMap = new HashMap<String, String>(tmpJobResultParticlePositionsStepFilePathnames.length);
            for (String tmpJobResultParticlePositionsStepFilePathname : tmpJobResultParticlePositionsStepFilePathnames) {
                String tmpStep = this.getStepOfJobResultParticlePositionsStepFilePathname(tmpJobResultParticlePositionsStepFilePathname);
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
        } else {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate distances">
        HashMap<String, LinkedList<String>> tmpParticlePairToDistancesMap = new HashMap<String, LinkedList<String>>();
        String tmpVersion = "Version 1.0.0";
        for (String tmpSortString : tmpStepSortStringList) {
            String tmpStep = tmpSortStringToStepMap.get(tmpSortString);
            String tmpJobResultParticlePositionsStepFilePathname = tmpStepToFilePathnameMap.get(tmpStep);

            LinkedList<ParticlePairAverageDistance> tmpDefinedParticlePairAverageDistanceList = null;
            String tmpParticlePairAverageDistancesForStepFilePathname = this.getJobResultParticlePairAverageDistancesForStepFilePathname(aJobResultPath, tmpStep);
            if ((new File(tmpParticlePairAverageDistancesForStepFilePathname)).exists()) {
                tmpDefinedParticlePairAverageDistanceList = this.readParticlePairAverageDistances(tmpParticlePairAverageDistancesForStepFilePathname);
            } else {
                tmpDefinedParticlePairAverageDistanceList = this.getDefinedParticlePairAverageDistances(tmpJobResultParticlePositionsStepFilePathname, aJobInputValueItemContainer);
                if (tmpDefinedParticlePairAverageDistanceList == null) {
                    return;
                }
                if (!this.writeParticlePairAverageDistances(tmpParticlePairAverageDistancesForStepFilePathname, tmpDefinedParticlePairAverageDistanceList)) {
                    return;
                }
            }
            if (tmpDefinedParticlePairAverageDistanceList == null) {
                return;
            }

            for (ParticlePairAverageDistance tmpParticlePairAverageDistance : tmpDefinedParticlePairAverageDistanceList) {
                if (!tmpParticlePairToDistancesMap.containsKey(tmpParticlePairAverageDistance.getUnderscoreConcatenatedParticlePair())) {
                    // <editor-fold defaultstate="collapsed" desc="Initialize particle pair">
                    LinkedList<String> tmpNewDistancesList = new LinkedList<String>();
                    tmpNewDistancesList.add(tmpVersion);
                    tmpNewDistancesList.add(tmpParticlePairAverageDistance.getFirstParticle());
                    tmpNewDistancesList.add(tmpParticlePairAverageDistance.getSecondParticle());

                    tmpNewDistancesList.add(tmpStep);
                    tmpNewDistancesList.add(String.valueOf(tmpParticlePairAverageDistance.getAverageDistance()));

                    tmpParticlePairToDistancesMap.put(tmpParticlePairAverageDistance.getUnderscoreConcatenatedParticlePair(), tmpNewDistancesList);
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Append to particle pair">
                    LinkedList<String> tmpDistancesList = tmpParticlePairToDistancesMap.get(tmpParticlePairAverageDistance.getUnderscoreConcatenatedParticlePair());

                    tmpDistancesList.add(tmpStep);
                    tmpDistancesList.add(String.valueOf(tmpParticlePairAverageDistance.getAverageDistance()));
                    // </editor-fold>
                }
            }
        }
        if (tmpParticlePairToDistancesMap.isEmpty()) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Write particle-pair distance files">
        for (String tmpParticlePair : tmpParticlePairToDistancesMap.keySet()) {
            LinkedList<String> tmpDistancesList = tmpParticlePairToDistancesMap.get(tmpParticlePair);
            // IMPORTANT: Delete possible existing file (e.g. for job restart)
            this.fileUtilityMethods.deleteSingleFile(this.getJobResultParticlePairDistanceFilePathname(aJobResultPath, tmpParticlePair));
            this.fileUtilityMethods.writeDefinedStringArrayToFile(
                tmpDistancesList.toArray(new String[0]),
                this.getJobResultParticlePairDistanceFilePathname(aJobResultPath, tmpParticlePair)
            );
        }
        // </editor-fold>
    }

    /**
     * Creates all defined molecule-particle-pair average distance files.
     * NOTE: Necessary molecule-particle distance directory of JobResult MUST
     * already be created!
     *
     * @param aJobResultPath Full path of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     */
    public void createDefinedMoleculeParticlePairAverageDistanceFiles(String aJobResultPath, ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            return;
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            return;
        }
        if (aJobInputValueItemContainer == null) {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initial settings">
        String[] tmpJobResultParticlePositionsStepFilePathnames = this.getJobResultParticlePositionsStepFilePathnames(aJobResultPath);
        HashMap<String, String> tmpStepToFilePathnameMap = null;
        HashMap<String, String> tmpSortStringToStepMap = null;
        LinkedList<String> tmpStepSortStringList = new LinkedList<String>();
        if (tmpJobResultParticlePositionsStepFilePathnames != null && tmpJobResultParticlePositionsStepFilePathnames.length > 0) {
            int tmpMaximumStepNumber = -1;
            tmpStepToFilePathnameMap = new HashMap<String, String>(tmpJobResultParticlePositionsStepFilePathnames.length);
            for (String tmpJobResultParticlePositionsStepFilePathname : tmpJobResultParticlePositionsStepFilePathnames) {
                String tmpStep = this.getStepOfJobResultParticlePositionsStepFilePathname(tmpJobResultParticlePositionsStepFilePathname);
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
        } else {
            return;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate distances">
        HashMap<String, LinkedList<String>> tmpMoleculeParticlePairToDistancesMap = new HashMap<String, LinkedList<String>>();
        String tmpVersion = "Version 1.0.0";
        for (String tmpSortString : tmpStepSortStringList) {
            String tmpStep = tmpSortStringToStepMap.get(tmpSortString);
            String tmpJobResultParticlePositionsStepFilePathname = tmpStepToFilePathnameMap.get(tmpStep);

            LinkedList<ParticlePairAverageDistance> tmpDefinedFragmenInMoleculePairAverageDistanceList = null;
            String tmpMoleculeParticlePairAverageDistancesForStepFilePathname = this.getJobResultMoleculeParticlePairAverageDistancesForStepFilePathname(aJobResultPath, tmpStep);
            if ((new File(tmpMoleculeParticlePairAverageDistancesForStepFilePathname)).exists()) {
                tmpDefinedFragmenInMoleculePairAverageDistanceList = this.readParticlePairAverageDistances(tmpMoleculeParticlePairAverageDistancesForStepFilePathname);
            } else {
                tmpDefinedFragmenInMoleculePairAverageDistanceList = this.getDefinedMoleculeParticlePairAverageDistances(tmpJobResultParticlePositionsStepFilePathname, aJobInputValueItemContainer);
                if (tmpDefinedFragmenInMoleculePairAverageDistanceList == null) {
                    return;
                }
                if (!this.writeParticlePairAverageDistances(tmpMoleculeParticlePairAverageDistancesForStepFilePathname, tmpDefinedFragmenInMoleculePairAverageDistanceList)) {
                    return;
                }
            }
            if (tmpDefinedFragmenInMoleculePairAverageDistanceList == null) {
                return;
            }

            for (ParticlePairAverageDistance tmpMoleculeParticlePairAverageDistance : tmpDefinedFragmenInMoleculePairAverageDistanceList) {
                if (!tmpMoleculeParticlePairToDistancesMap.containsKey(tmpMoleculeParticlePairAverageDistance.getUnderscoreConcatenatedParticlePair())) {
                    // <editor-fold defaultstate="collapsed" desc="Initialize particle pair">
                    LinkedList<String> tmpNewDistancesList = new LinkedList<String>();
                    tmpNewDistancesList.add(tmpVersion);
                    tmpNewDistancesList.add(tmpMoleculeParticlePairAverageDistance.getFirstParticle());
                    tmpNewDistancesList.add(tmpMoleculeParticlePairAverageDistance.getSecondParticle());

                    tmpNewDistancesList.add(tmpStep);
                    tmpNewDistancesList.add(String.valueOf(tmpMoleculeParticlePairAverageDistance.getAverageDistance()));

                    tmpMoleculeParticlePairToDistancesMap.put(tmpMoleculeParticlePairAverageDistance.getUnderscoreConcatenatedParticlePair(), tmpNewDistancesList);

                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Append to particle pair">
                    LinkedList<String> tmpDistancesList = tmpMoleculeParticlePairToDistancesMap.get(tmpMoleculeParticlePairAverageDistance.getUnderscoreConcatenatedParticlePair());

                    tmpDistancesList.add(tmpStep);
                    tmpDistancesList.add(String.valueOf(tmpMoleculeParticlePairAverageDistance.getAverageDistance()));

                    // </editor-fold>
                }
            }
        }
        if (tmpMoleculeParticlePairToDistancesMap.isEmpty()) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Write particle-pair distance files">
        for (String tmpMoleculeParticlePair : tmpMoleculeParticlePairToDistancesMap.keySet()) {
            LinkedList<String> tmpDistancesList = tmpMoleculeParticlePairToDistancesMap.get(tmpMoleculeParticlePair);
            // IMPORTANT: Delete possible existing file (e.g. for job restart)
            this.fileUtilityMethods.deleteSingleFile(this.getJobResultMoleculeParticlePairDistanceFilePathname(aJobResultPath, tmpMoleculeParticlePair));
            this.fileUtilityMethods.writeDefinedStringArrayToFile(
                    tmpDistancesList.toArray(new String[0]),
                    this.getJobResultMoleculeParticlePairDistanceFilePathname(aJobResultPath, tmpMoleculeParticlePair));
        }

        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Job input/result files related methods">

    /**
     * Returns pathname of job input information file
     *
     * @param aPath Path of job input information file
     * @return Pathname of job input information file
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobInputInformationFilePathname(String aPath) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + ModelDefinitions.JOB_INPUT_INFORMATION_FILENAME;
    }

    /**
     * Returns pathname of job result information file
     *
     * @param aPath Path of job result information file
     * @return Pathname of job result information file
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobResultInformationFilePathname(String aPath) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + ModelDefinitions.JOB_RESULT_INFO_FILENAME;
    }

    /**
     * Returns step of particle positions step file for JobResult
     *
     * @param aJobResultParticlePositionsStepFilePathname Particle positions
     * step file pathname for JobResult
     * @return Step of particle positions step file for JobResult
     * @throws IllegalArgumentException Thrown if
     * tmpJobResultParticlePositionsStepFilePathname is invalid
     */
    public String getStepOfJobResultParticlePositionsStepFilePathname(String aJobResultParticlePositionsStepFilePathname) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsStepFilePathname == null || aJobResultParticlePositionsStepFilePathname.isEmpty()) {
            throw new IllegalArgumentException("aJobResultParticlePositionsStepFilePathname is null/empty.");
        }
        // </editor-fold>
        String tmpFileEnding = null;
        // Compressed GZIP file
        tmpFileEnding = Strings.GZIP_FILE_ENDING;
        String tmpJobResultParticlePositionsStepFilename = (new File(aJobResultParticlePositionsStepFilePathname)).getName();
        return tmpJobResultParticlePositionsStepFilename.replace(FileOutputStrings.PARTICLE_POSITIONS_SIMULATION_STEP_FILE_PREFIX, "").replace(tmpFileEnding, "");
    }

    /**
     * Returns step of particle positions minimization step file for JobResult
     *
     * @param aJobResultParticlePositionsMinStepFilePathname Particle positions
     * minimization step file pathname for JobResult
     * @return Step of particle positions minimization step file for JobResult
     * @throws IllegalArgumentException Thrown if
     * tmpJobResultParticlePositionsStepFilePathname is invalid
     */
    public String getStepOfJobResultParticlePositionsMinStepFilePathname(String aJobResultParticlePositionsMinStepFilePathname) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsMinStepFilePathname == null || aJobResultParticlePositionsMinStepFilePathname.isEmpty()) {
            throw new IllegalArgumentException("aJobResultParticlePositionsMinStepFilePathname is null/empty.");
        }
        // </editor-fold>
        String tmpFileEnding = null;
        // Compressed GZIP file
        tmpFileEnding = Strings.GZIP_FILE_ENDING;
        String tmpJobResultParticlePositionsMinStepFilename = (new File(aJobResultParticlePositionsMinStepFilePathname)).getName();
        return tmpJobResultParticlePositionsMinStepFilename.replace(FileOutputStrings.PARTICLE_POSITIONS_MINIMIZED_FILENAME_PREFIX, "").replace(tmpFileEnding, "");
    }

    /**
     * Returns pathname of internal XML job input file
     *
     * @param aPath Path of internal XML job input file
     * @return Pathname of internal XML job input file
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getInternalXmlJobInputFilePathname(String aPath) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPath == null || aPath.isEmpty()) {
            throw new IllegalArgumentException("aPath is null/empty.");
        }
        if (!(new File(aPath)).isDirectory()) {
            throw new IllegalArgumentException("aPath is not an existing directory.");
        }

        // </editor-fold>
        return aPath + File.separatorChar + ModelDefinitions.INTERNAL_XML_JOB_INPUT_VALUE_ITEM_CONTAINER_FILENAME;
    }

    /**
     * Returns path of job input directory in job result path
     *
     * @param aJobResultPath Path of job result
     * @return Path of job input directory in job result path or null if not
     * available
     * @throws IllegalArgumentException Thrown if aPath is invalid
     */
    public String getJobInputPathOfJobResult(String aJobResultPath) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultPath == null || aJobResultPath.isEmpty()) {
            throw new IllegalArgumentException("aJobResultPath is null/empty.");
        }
        if (!(new File(aJobResultPath)).isDirectory()) {
            throw new IllegalArgumentException("aJobResultPath is not an existing directory.");
        }
        // </editor-fold>
        // There is only ONE job input directory in job result directory
        String tmpJobInputPathOfJobResult = null;
        String[] tmpJobInputPathOfJobResultArray = this.fileUtilityMethods.getDirectoryPathsWithPrefix(aJobResultPath, ModelDefinitions.PREFIX_OF_JOB_INPUT_DIRECTORY);
        if (tmpJobInputPathOfJobResultArray != null) {
            tmpJobInputPathOfJobResult = tmpJobInputPathOfJobResultArray[0];
        }
        return tmpJobInputPathOfJobResult;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Molecule backbone and protein backbone/distance forces related methods">
    /**
     * Returns the number of forces of the single molecule (or protein)
     * in simulation which has the maximum number of forces.
     * NOTE: For a protein backbone and distance forces are added.
     *
     * @param aJobInputValueItemContainer Job input value item container instance
     * (value items are NOT changed)
     * @return Number of forces of the single molecule (or protein) in
     * simulation which has the maximum number of forces or -1 if
     * this quantity could not be calculated
     */
    public int getMaximumNumberOfMoleculeAndProteinForces(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1;
        }
        // </editor-fold>
        int tmpMaximumNumberOfMoleculeBackboneForces = this.getMaximumNumberOfMoleculeBackboneForces(aJobInputValueItemContainer);
        int tmpMaximumNumberOfProteinForces = this.getMaximumNumberOfProteinForces(aJobInputValueItemContainer);
        return Math.max(tmpMaximumNumberOfMoleculeBackboneForces, tmpMaximumNumberOfProteinForces);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Number of particles related methods">
    /**
     * Returns number of particles of specified type in simulation
     *
     * @param aParticle Particle
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density and Quantity (are not changed)
     * @return Number of particles of specified type or -1 if number can not be
     * calculated
     */
    public int getTotalNumberOfParticlesOfSpecifiedTypeInSimulation(String aParticle, ValueItemContainer aValueItemContainer) {
        return this.getTotalNumberOfParticlesOfSpecifiedTypeInSimulation(aParticle, this.createMoleculeInfoValueItem(aValueItemContainer));
    }

    /**
     * Returns number of particles of specified type in simulation
     *
     * @param aParticle Particle
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @return Number of particles of specified type or -1 if number can not be
     * calculated
     */
    public int getTotalNumberOfParticlesOfSpecifiedTypeInSimulation(String aParticle, ValueItem aMoleculeInfoValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle == null || aParticle.isEmpty()) {
            return -1;
        }
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return -1;
        }

        // </editor-fold>
        int tmpTotalNumberOfParticlesInSimulation = 0;
        // Loop over all molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aMoleculeInfoValueItem.getValue(i, 1));
            int tmpNumberOfParticles = tmpSpices.getFrequencyOfSpecifiedParticle(aParticle);
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            int tmpNumberOfMolecules = aMoleculeInfoValueItem.getValueAsInt(i, 2);
            tmpTotalNumberOfParticlesInSimulation += tmpNumberOfParticles * tmpNumberOfMolecules;
        }
        return tmpTotalNumberOfParticlesInSimulation;
    }

    /**
     * Returns number of molecule particles of specified type in simulation
     *
     * @param aMoleculeName Name of molecule
     * @param aParticle Particle
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density and Quantity (are not changed)
     * @return Number of molecule particles of specified type or -1 if number
     * can not be calculated
     */
    public int getTotalNumberOfMoleculeParticlesOfSpecifiedTypeInSimulation(String aMoleculeName, String aParticle, ValueItemContainer aValueItemContainer) {
        return this.getTotalNumberOfMoleculeParticlesOfSpecifiedTypeInSimulation(aMoleculeName, aParticle, this.createMoleculeInfoValueItem(aValueItemContainer));
    }

    /**
     * Returns number of molecule particles of specified type in simulation
     *
     * @param aMoleculeName Name of molecule
     * @param aParticle Particle
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @return Number of molecule particles of specified type or -1 if number
     * can not be calculated
     */
    public int getTotalNumberOfMoleculeParticlesOfSpecifiedTypeInSimulation(String aMoleculeName, String aParticle, ValueItem aMoleculeInfoValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeName == null || aMoleculeName.isEmpty()) {
            return -1;
        }
        if (aParticle == null || aParticle.isEmpty()) {
            return -1;
        }
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return -1;
        }

        // </editor-fold>
        int tmpNumberOfMoleculeParticlesInSimulation = -1;
        // Loop over all molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            String tmpMoleculeName = aMoleculeInfoValueItem.getValue(i, 0);
            if (tmpMoleculeName.equals(aMoleculeName)) {
                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aMoleculeInfoValueItem.getValue(i, 1));
                int tmpNumberOfParticles = tmpSpices.getFrequencyOfSpecifiedParticle(aParticle);
                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                int tmpNumberOfMolecules = aMoleculeInfoValueItem.getValueAsInt(i, 2);
                tmpNumberOfMoleculeParticlesInSimulation = tmpNumberOfParticles * tmpNumberOfMolecules;
                break;
            }
        }
        return tmpNumberOfMoleculeParticlesInSimulation;
    }

    /**
     * Returns total number of particles in simulation
     *
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density and Quantity (are not changed)
     * @return Total number of particles or -1 if total number of particles
     * could not be calculated
     */
    public int getTotalNumberOfParticlesInSimulation(ValueItemContainer aValueItemContainer) {
        return this.getTotalNumberOfParticlesInSimulation(this.createMoleculeInfoValueItem(aValueItemContainer));
    }

    /**
     * Returns total number of particles  in simulation
     *
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @return Total number of particles or -1 if total number of particles
     * could not be calculated
     */
    public int getTotalNumberOfParticlesInSimulation(ValueItem aMoleculeInfoValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return -1;
        }

        // </editor-fold>
        int tmpTotalNumberOfParticlesOfSimulation = 0;
        // Loop over all molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aMoleculeInfoValueItem.getValue(i, 1));
            int tmpTotalNumberOfParticlesOfMolecule = tmpSpices.getTotalNumberOfParticles();
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            int tmpNumberOfMolecules = aMoleculeInfoValueItem.getValueAsInt(i, 2);
            tmpTotalNumberOfParticlesOfSimulation += tmpTotalNumberOfParticlesOfMolecule * tmpNumberOfMolecules;
        }
        return tmpTotalNumberOfParticlesOfSimulation;
    }
    
    /**
     * Returns the total number of particles of the single molecule in
     * simulation which has the maximum total number of particles.
     *
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density and Quantity (are not changed)
     * @return Total number of particles of the single molecule in simulation
     * which has the total maximum number of particles or -1 if this quantity
     * could not be calculated
     */
    public int getMaximumNumberOfMoleculeParticles(ValueItemContainer aValueItemContainer) {
        return this.getMaximumNumberOfMoleculeParticles(this.createMoleculeInfoValueItem(aValueItemContainer));
    }

    /**
     * Returns the total number of particles of the single molecule in
     * simulation which has the maximum total number of particles.
     *
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @return Total number of particles of the single molecule in simulation
     * which has the total maximum number of particles or -1 if this quantity
     * could not be calculated
     */
    public int getMaximumNumberOfMoleculeParticles(ValueItem aMoleculeInfoValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return -1;
        }

        // </editor-fold>
        int tmpMaximumNumberOfMoleculeParticles = 0;
        // Loop over all molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aMoleculeInfoValueItem.getValue(i, 1));
            int tmpTotalNumberOfParticlesOfMolecule = tmpSpices.getTotalNumberOfParticles();
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            int tmpNumberOfMolecules = aMoleculeInfoValueItem.getValueAsInt(i, 2);
            tmpMaximumNumberOfMoleculeParticles = Math.max(tmpMaximumNumberOfMoleculeParticles, tmpTotalNumberOfParticlesOfMolecule * tmpNumberOfMolecules);
        }
        return tmpMaximumNumberOfMoleculeParticles;
    }

    /**
     * Returns total number of all particles of a specific molecule in
     * simulation
     *
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @param aMoleculeName Name of molecule
     * @return Total number of particles of molecule or -1 if total number of
     * particles can not be calculated
     */
    public int getTotalNumberOfParticlesOfMoleculeInSimulation(ValueItem aMoleculeInfoValueItem, String aMoleculeName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return -1;
        }
        if (aMoleculeName == null || aMoleculeName.isEmpty()) {
            return -1;
        }

        // </editor-fold>
        // Loop over all molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            // aMoleculeInfoValueItem column 0: Molecule name
            if (aMoleculeInfoValueItem.getValue(i, 0).equals(aMoleculeName)) {
                // aMoleculeInfoValueItem column 1: Molecular structure
                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aMoleculeInfoValueItem.getValue(i, 1));
                int tmpNumberOfParticlesPerMolecule = tmpSpices.getTotalNumberOfParticles();
                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                // aMoleculeInfoValueItem column 2: Quantity
                int tmpNumberOfMolecules = aMoleculeInfoValueItem.getValueAsInt(i, 2);
                return tmpNumberOfParticlesPerMolecule * tmpNumberOfMolecules;
            }
        }
        return -1;
    }

    /**
     * Returns number of particles per molecule of a specific molecule
     *
     * @param aMoleculeName Name of molecule
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density and Quantity (are not changed)
     * @return Number of particles per molecule or -1 if number of particles can
     * not be evaluated
     */
    public int getNumberOfParticlesPerMolecule(String aMoleculeName, ValueItemContainer aValueItemContainer) {
        return this.getNumberOfParticlesPerMolecule(this.createMoleculeInfoValueItem(aValueItemContainer), aMoleculeName);
    }
    
    /**
     * Returns number of particles per molecule of a specific molecule
     *
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @param aMoleculeName Name of molecule
     * @return Number of particles per molecule or -1 if number of particles can
     * not be evaluated
     */
    public int getNumberOfParticlesPerMolecule(ValueItem aMoleculeInfoValueItem, String aMoleculeName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return -1;
        }
        if (aMoleculeName == null || aMoleculeName.isEmpty()) {
            return -1;
        }
        // </editor-fold>
        // Loop over all molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            // aMoleculeInfoValueItem column 0: Molecule name
            if (aMoleculeInfoValueItem.getValue(i, 0).equals(aMoleculeName)) {
                // aMoleculeInfoValueItem column 1: Molecular structure
                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aMoleculeInfoValueItem.getValue(i, 1));
                int tmpNumberOfParticlesPerMolecule = tmpSpices.getTotalNumberOfParticles();
                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                return tmpNumberOfParticlesPerMolecule;
            }
        }
        return -1;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Number of molecules related methods">
    /**
     * Returns number of molecules of specified type in simulation
     *
     * @param aMoleculeName Name of molecule
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density and Quantity (are not changed)
     * @return Number of molecules of specified type or -1 if number can not be 
     * calculated
     */
    public int getTotalNumberOfMoleculesOfSpecifiedTypeInSimulation(String aMoleculeName, ValueItemContainer aValueItemContainer) {
        return this.getTotalNumberOfMoleculesOfSpecifiedTypeInSimulation(aMoleculeName, this.createMoleculeInfoValueItem(aValueItemContainer));
    }

    /**
     * Returns number of molecules of specified type in simulation
     *
     * @param aMoleculeName Name of molecule
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @return Number of molecules of specified type or -1 if number can not be 
     * calculated
     */
    public int getTotalNumberOfMoleculesOfSpecifiedTypeInSimulation(String aMoleculeName, ValueItem aMoleculeInfoValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeName == null || aMoleculeName.isEmpty()) {
            return -1;
        }
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return -1;
        }
        // </editor-fold>
        int tmpNumberOfMoleculesInSimulation = -1;
        // Loop over all molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            String tmpMoleculeName = aMoleculeInfoValueItem.getValue(i, 0);
            if (tmpMoleculeName.equals(aMoleculeName)) {
                tmpNumberOfMoleculesInSimulation = aMoleculeInfoValueItem.getValueAsInt(i, 2);
                break;
            }
        }
        return tmpNumberOfMoleculesInSimulation;
    }

    /**
     * Returns the total number of molecules in simulation
     *
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density and Quantity (are not changed)
     * @return Total number of molecules in simulation or -1 if this quantity
     * could not be calculated
     */
    public int getTotalNumberOfMoleculesInSimulation(ValueItemContainer aValueItemContainer) {
        return this.getTotalNumberOfMoleculesInSimulation(this.createMoleculeInfoValueItem(aValueItemContainer));
    }
    
    /**
     * Returns the total number of molecules in simulation
     *
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @return Total number of molecules in simulation or -1 if this quantity
     * could not be calculated
     */
    public int getTotalNumberOfMoleculesInSimulation(ValueItem aMoleculeInfoValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return -1;
        }
        // </editor-fold>
        int tmpTotalNumberOfMolecules = 0;
        // Loop over all molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            tmpTotalNumberOfMolecules += aMoleculeInfoValueItem.getValueAsInt(i, 2);
        }
        return tmpTotalNumberOfMolecules;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns net charge of whole simulation setup
     *
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @param aMoleculeChargeValueItem Molecule charge value item (is NOT changed)
     * @return Net charge of whole simulation setup or Double.NaN if net charge
     * could not be calculated
     */
    public double getNetChargeOfSimulation(ValueItem aMoleculeInfoValueItem, ValueItem aMoleculeChargeValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return Double.NaN;
        }
        if (aMoleculeChargeValueItem == null || !aMoleculeChargeValueItem.getName().equals("MoleculeCharge")) {
            return Double.NaN;
        }
        // </editor-fold>
        double tmpNetChargeOfSimulation = 0.0;
        // Loop over all different molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            if (!aMoleculeInfoValueItem.getValue(i, 0).equals(aMoleculeChargeValueItem.getValue(i, 0))) {
                return Double.NaN;
            } else {
                int tmpNumberOfMolecules = aMoleculeInfoValueItem.getValueAsInt(i, 2);
                double tmpNetChargeOfMolecule = aMoleculeChargeValueItem.getValueAsDouble(i, 3);
                tmpNetChargeOfSimulation += tmpNetChargeOfMolecule * tmpNumberOfMolecules;
            }
        }
        return tmpNetChargeOfSimulation;
    }
    
    /**
     * Returns if simulation box has periodic boundary in x-direction
     *
     * @param aValueItemContainer Value item container
     * @return True: Simulation box has periodic boundary in x-direction, false:
     * Otherwise
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public boolean isPeriodicBoundaryX(ValueItemContainer aValueItemContainer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null || !aValueItemContainer.hasValueItem("PeriodicBoundaries")) {
            throw new IllegalArgumentException("Argument is illegal");
        }

        // </editor-fold>
        ValueItem tmpPeriodicBoundariesValueItem = aValueItemContainer.getValueItem("PeriodicBoundaries");
        return tmpPeriodicBoundariesValueItem.getValue(0, 0).equals(ModelMessage.get("JdpdInputFile.parameter.true"));
    }

    /**
     * Returns if simulation box has periodic boundary in y-direction
     *
     * @param aValueItemContainer Value item container
     * @return True: Simulation box has periodic boundary in y-direction, false:
     * Otherwise
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public boolean isPeriodicBoundaryY(ValueItemContainer aValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null || !aValueItemContainer.hasValueItem("PeriodicBoundaries")) {
            throw new IllegalArgumentException("Argument is illegal");
        }

        // </editor-fold>
        ValueItem tmpPeriodicBoundariesValueItem = aValueItemContainer.getValueItem("PeriodicBoundaries");
        return tmpPeriodicBoundariesValueItem.getValue(0, 1).equals(ModelMessage.get("JdpdInputFile.parameter.true"));
    }

    /**
     * Returns if simulation box has periodic boundary in z-direction
     *
     * @param aValueItemContainer Value item container
     * @return True: Simulation box has periodic boundary in z-direction, false:
     * Otherwise
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public boolean isPeriodicBoundaryZ(ValueItemContainer aValueItemContainer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItemContainer == null || !aValueItemContainer.hasValueItem("PeriodicBoundaries")) {
            throw new IllegalArgumentException("Argument is illegal");
        }

        // </editor-fold>
        ValueItem tmpPeriodicBoundariesValueItem = aValueItemContainer.getValueItem("PeriodicBoundaries");
        return tmpPeriodicBoundariesValueItem.getValue(0, 2).equals(ModelMessage.get("JdpdInputFile.parameter.true"));
    }

    /**
     * Returns box size info
     *
     * @param aValueItemContainer ValueItemContainer instance with value item
     * for BoxSize (is not changed)
     * @return Box size info or null if argument is illegal
     */
    public BoxSizeInfo getBoxSizeInfo(ValueItemContainer aValueItemContainer) {
        return this.getBoxSizeInfo(this.createBoxInfoValueItem(aValueItemContainer));
    }

    /**
     * Returns box size info
     *
     * @param aBoxInfoValueItem Box info value item (is not changed)
     * @return Box size info or null if argument is illegal
     */
    public BoxSizeInfo getBoxSizeInfo(ValueItem aBoxInfoValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isBoxInfoValueItem(aBoxInfoValueItem)) {
            return null;
        }

        // </editor-fold>
        return new BoxSizeInfo(0.0, aBoxInfoValueItem.getValueAsDouble(0, 0), 0.0, aBoxInfoValueItem.getValueAsDouble(0, 1), 0.0, aBoxInfoValueItem.getValueAsDouble(0, 2));
    }

    /**
     * Returns maximum number of connections of a single particle in simulation
     *
     * @param aValueItemContainer ValueItemContainer instance with value items
     * for MonomerTable, MoleculeTable, Density and Quantity (are not changed)
     * @return Maximum number of connections of a single particle in simulation
     * or -1 if maximum number of connections of a single particle can not be
     * calculated
     */
    public int getMaximumNumberOfConnectionsOfSingleParticleInSimulation(ValueItemContainer aValueItemContainer) {
        return this.getMaximumNumberOfConnectionsOfSingleParticleInSimulation(this.createMoleculeInfoValueItem(aValueItemContainer));
    }

    /**
     * Returns maximum number of connections of a single particle in simulation
     *
     * @param aMoleculeInfoValueItem Molecule info value item (is NOT changed)
     * @return Maximum number of connections of a single particle in simulation
     * or -1 if maximum number of connections of a single particle can not be
     * calculated
     */
    public int getMaximumNumberOfConnectionsOfSingleParticleInSimulation(ValueItem aMoleculeInfoValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isMoleculeInfoValueItem(aMoleculeInfoValueItem)) {
            return -1;
        }
        // </editor-fold>
        int tmpMaximumNumberOfConnections = 0;
        // Loop over all molecules
        for (int i = 0; i < aMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            String tmpMolecularStructureString = aMoleculeInfoValueItem.getValue(i, 1);
            // Old code:
            // tmpSpices.setInputStructure(tmpMolecularStructureString);
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
            if (tmpSpices.getMaximumNumberOfConnectionsOfSingleParticle() > tmpMaximumNumberOfConnections) {
                tmpMaximumNumberOfConnections = tmpSpices.getMaximumNumberOfConnectionsOfSingleParticle();
            }
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        }
        return tmpMaximumNumberOfConnections;
    }

    /**
     * Returns standard particle radius in DPD units
     *
     * @param aDensityInfoValueItem Density info value item (is not changed)
     * @return Standard particle radius in DPD units or -1.0 if argument is
     * illegal
     */
    public double getStandardParticleRadius(ValueItem aDensityInfoValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isDensityInfoValueItem(aDensityInfoValueItem)) {
            return -1.0;
        }

        // </editor-fold>
        return this.getRadiusOfParticlesInDpdBox(1, aDensityInfoValueItem.getValueAsDouble(), ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES);
    }

    /**
     * Calculates the radius of a sphere in DPD units with specified number of
     * particles in the DPD simulation box
     *
     * @param aNumberOfParticles Number of particles
     * @param aDpdDensity DPD density
     * @param aNumberOfDecimalsToCutRadius Number of decimals to cut radius
     * after (i.e. radius is always a little smaller than in "reality": This
     * avoids possible drawing problems). If less/equal zero: No cut of
     * decimals.
     * @return Radius of a sphere with specified number of particles in the DPD
     * simulation box or 0.0 if radius can not be calculated
     */
    public double getRadiusOfParticlesInDpdBox(int aNumberOfParticles, double aDpdDensity, int aNumberOfDecimalsToCutRadius) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfParticles <= 0) {
            return 0.0;
        }
        if (aDpdDensity <= 0.0) {
            return 0.0;
        }

        // </editor-fold>
        try {
            // Volume = Number/Density
            double tmpVolumeInBox = (double) aNumberOfParticles / aDpdDensity;
            // Volume = 4/3*PI*Radius^3
            if (aNumberOfDecimalsToCutRadius <= 0) {
                return Math.cbrt(ModelDefinitions.FACTOR_3_DIV_4_PI * tmpVolumeInBox);
            } else {
                return this.miscUtilityMethods.cutDoubleValue(Math.cbrt(ModelDefinitions.FACTOR_3_DIV_4_PI * tmpVolumeInBox), aNumberOfDecimalsToCutRadius);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return 0.0;
        }
    }

    /**
     * Calculates the z-length of a box with specified number of particles in
     * the DPD simulation box
     *
     * @param aNumberOfParticles Number of particles
     * @param aDpdDensity DPD density
     * @param aXLength X-length of the box
     * @param aYLength Y-length of the box
     * @param aNumberOfDecimalsToCutRadius Number of decimals to cut radius
     * after (i.e. radius is always a little smaller than in "reality": This
     * avoids possible drawing problems). If less than zero: No cut of decimals.
     * @return Z-length of the box with specified number of particles in the DPD
     * simulation box or 0.0 if z-length can not be calculated
     */
    public double getZLengthOfBoxOfParticlesInDpdBox(int aNumberOfParticles, double aDpdDensity, double aXLength, double aYLength, int aNumberOfDecimalsToCutRadius) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aNumberOfParticles <= 0) {
            return 0.0;
        }
        if (aDpdDensity <= 0.0) {
            return 0.0;
        }
        if (aXLength <= 0.0) {
            return 0.0;
        }
        if (aYLength <= 0.0) {
            return 0.0;
        }

        // </editor-fold>
        try {
            // Volume = Number/Density
            double tmpVolumeInBox = (double) aNumberOfParticles / aDpdDensity;
            // Volume = x-length * y-length * z-length
            if (aNumberOfDecimalsToCutRadius <= 0) {
                return tmpVolumeInBox / (aXLength * aYLength);
            } else {
                return this.miscUtilityMethods.cutDoubleValue(tmpVolumeInBox / (aXLength * aYLength), aNumberOfDecimalsToCutRadius);
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return 0.0;
        }

    }

    /**
     * Returns the maximum simulation step from U(total) progress file pathname
     *
     * @param aJobResultPath Job result path
     * @return Maximum simulation step from U(total) progress file pathname or
     * "-1" if value can not be evaluated
     */
    public int getMaximumSimulationStep(String aJobResultPath) {
        try {
            int tmpMaximumSimulationStep = -1;
            if ((new File(this.getJobResultUtotalProgressFilePathname(aJobResultPath))).isFile()) {
                String[] tmpInfoArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(this.getJobResultUtotalProgressFilePathname(aJobResultPath));
                if (tmpInfoArray != null && tmpInfoArray[0].equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpMaximumSimulationStep = Integer.valueOf(tmpInfoArray[tmpInfoArray.length - 2]);
                    // </editor-fold>
                }
            }
            return tmpMaximumSimulationStep;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Read particle positions related methods">
    /**
     * Reads GraphicalParticlePositionInfo instance from graphical particle 
     * positions file
     *
     * @param aJobResultParticlePositionsFilePathname Full pathname of graphical
     * particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @return GraphicalParticlePositionInfo instance with graphical particle 
     * positions or null if GraphicalParticlePositionInfo instance could not be 
     * created
     */
    private GraphicalParticlePositionInfo readGraphicalParticlePositions(
        String aJobResultParticlePositionsFilePathname, 
        ValueItemContainer aJobInputValueItemContainer
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathname == null || aJobResultParticlePositionsFilePathname.isEmpty() || !(new File(aJobResultParticlePositionsFilePathname)).isFile()) {
            return null;
        }
        if (aJobInputValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Local variables">
            String tmpParticle;
            int tmpNumberOfParticlePositions;
            double tmpX;
            double tmpY;
            double tmpZ;
            int tmpParticleIndex;
            int tmpMoleculeIndex;
            int tmpTotalNumberOfParticlesInSimulation;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set necessary variables">
            double tmpBoxLengthX = this.getSimulationBoxLengthX(aJobInputValueItemContainer);
            double tmpBoxLengthY = this.getSimulationBoxLengthY(aJobInputValueItemContainer);
            double tmpBoxLengthZ = this.getSimulationBoxLengthZ(aJobInputValueItemContainer);
            double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
            HashMap<String, HashMap<String, IGraphicalParticle>> tmpMoleculeToParticlesMap = 
                this.getMoleculeToParticlesMap(aJobInputValueItemContainer, tmpLengthConversionFactor);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Read particle positions">
            BufferedReader tmpBufferedReader = null;
            try {
                String tmpLine;
                if (!(new File(aJobResultParticlePositionsFilePathname)).isFile()) {
                    return null;
                }
                tmpBufferedReader = 
                    new BufferedReader(
                        new InputStreamReader(
                            new GZIPInputStream(
                                new FileInputStream(aJobResultParticlePositionsFilePathname), 
                                ModelDefinitions.BUFFER_SIZE
                            )
                        )
                    );
                tmpLine = tmpBufferedReader.readLine();
                if (tmpLine.equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    tmpTotalNumberOfParticlesInSimulation = Integer.valueOf(tmpBufferedReader.readLine());
                    GraphicalParticlePosition[] tmpGraphicalParticlePositions = new GraphicalParticlePosition[tmpTotalNumberOfParticlesInSimulation];
                    int tmpMinMoleculeIndex = Integer.MAX_VALUE;
                    int tmpMaxMoleculeIndex = Integer.MIN_VALUE;
                    while (true) {
                        // <editor-fold defaultstate="collapsed" desc="Molecule">
                        String tmpMoleculeName = tmpBufferedReader.readLine();
                        if (tmpMoleculeName == null) {
                            break;
                        }
                        HashMap<String, IGraphicalParticle> tmpParticleToGraphicalParticleMap = 
                            tmpMoleculeToParticlesMap.get(tmpMoleculeName);
                        if (tmpParticleToGraphicalParticleMap == null) {
                            return null;
                        }
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Particle">
                        tmpParticle = tmpBufferedReader.readLine();
                        GraphicalParticle tmpGraphicalParticle = 
                            (GraphicalParticle) tmpParticleToGraphicalParticleMap.get(tmpParticle);
                        if (tmpGraphicalParticle == null) {
                            return null;
                        }
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Number of positions">
                        tmpNumberOfParticlePositions = Integer.valueOf(tmpBufferedReader.readLine());
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Read particle positions">
                        for (int k = 0; k < tmpNumberOfParticlePositions; k++) {
                            tmpX = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpX < 0.0) {
                                tmpX = 0.0;
                            }
                            if (tmpX > tmpBoxLengthX) {
                                tmpX = tmpBoxLengthX;
                            }
                            tmpY = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpY < 0.0) {
                                tmpY = 0.0;
                            }
                            if (tmpY > tmpBoxLengthY) {
                                tmpY = tmpBoxLengthY;
                            }
                            tmpZ = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpZ < 0.0) {
                                tmpZ = 0.0;
                            }
                            if (tmpZ > tmpBoxLengthZ) {
                                tmpZ = tmpBoxLengthZ;
                            }
                            tmpParticleIndex = Integer.valueOf(tmpBufferedReader.readLine());
                            tmpMoleculeIndex = Integer.valueOf(tmpBufferedReader.readLine());
                            if (tmpMoleculeIndex > tmpMaxMoleculeIndex) {
                                tmpMaxMoleculeIndex = tmpMoleculeIndex;
                            }
                            if (tmpMoleculeIndex < tmpMinMoleculeIndex) {
                                tmpMinMoleculeIndex = tmpMoleculeIndex;
                            }
                            // Write particles to original position in simulation
                            tmpGraphicalParticlePositions[tmpParticleIndex] = 
                                new GraphicalParticlePosition(
                                    tmpGraphicalParticle, 
                                    tmpX, 
                                    tmpY, 
                                    tmpZ,
                                    tmpParticleIndex,
                                    tmpMoleculeIndex    
                                );
                        }
                        // </editor-fold>
                    }
                    // <editor-fold defaultstate="collapsed" desc="Return GraphicalParticlePositionInfo">
                    // Parameter false: Compartments/Bulk do NOT exist
                    return new GraphicalParticlePositionInfo(
                        new GraphicalParticleInfo(tmpMoleculeToParticlesMap, false), 
                        tmpGraphicalParticlePositions, 
                        this.getBoxSizeInfo(aJobInputValueItemContainer),
                        tmpLengthConversionFactor,
                        tmpMinMoleculeIndex,
                        tmpMaxMoleculeIndex
                    );
                    // </editor-fold>
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Unknown version">
                    return null;
                    // </editor-fold>
                }
            } catch (Exception anException) {
                // Note: Exception may be thrown since graphical particle positions 
                // can be still in write process thus
                // ModelUtils.appendToLogfile(true, anException);
                // should not be thrown
                return null;
            } finally {
                if (tmpBufferedReader != null) {
                    try {
                        tmpBufferedReader.close();
                    } catch (IOException anException) {
                        // Note: Exception may be thrown since graphical particle positions 
                        // can be still in write process thus
                        // ModelUtils.appendToLogfile(true, anException);
                        // should not be thrown
                        return null;
                    }
                }
            }
            // </editor-fold>
        } catch (Exception anException) {
            // Note: Exception may be thrown since graphical particle positions 
            // can be still in write process thus
            // ModelUtils.appendToLogfile(true, anException);
            // should not be thrown
            return null;
        }
    }

    /**
     * Returns hash map with particle positions from particle positions file
     * converted to Angstrom. Hash map maps particle to list of all of its
     * positions.
     *
     * @param aJobResultParticlePositionsFilePathname Full pathname of graphical
     * particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @return Hash map with particle positions from particle positions file
     * converted to Angstrom or null if particle positions could not be read
     */
    private HashMap<String, LinkedList<PointInSpace>> readParticlePositions(
        String aJobResultParticlePositionsFilePathname, 
        ValueItemContainer aJobInputValueItemContainer
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathname == null || aJobResultParticlePositionsFilePathname.isEmpty() || !(new File(aJobResultParticlePositionsFilePathname)).isFile()) {
            return null;
        }
        if (aJobInputValueItemContainer == null) {
            return null;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Local variables">
            String tmpParticle;
            int tmpNumberOfParticlePositions;
            double tmpX;
            double tmpY;
            double tmpZ;
            double tmpXinDpd;
            double tmpYinDpd;
            double tmpZinDpd;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set necessary variables">
            double tmpBoxLengthX = this.getSimulationBoxLengthX(aJobInputValueItemContainer);
            double tmpBoxLengthY = this.getSimulationBoxLengthY(aJobInputValueItemContainer);
            double tmpBoxLengthZ = this.getSimulationBoxLengthZ(aJobInputValueItemContainer);
            double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
            // O(100) is reasonable capacity
            HashMap<String, LinkedList<PointInSpace>> tmpParticleToPositionsMap = 
                new HashMap<String, LinkedList<PointInSpace>>(100);
            LinkedList<PointInSpace> tmpPositionsList;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Read particle positions">
            BufferedReader tmpBufferedReader = null;
            try {
                String tmpLine;
                if (!(new File(aJobResultParticlePositionsFilePathname)).isFile()) {
                    return null;
                }
                tmpBufferedReader = 
                    new BufferedReader(
                        new InputStreamReader(
                            new GZIPInputStream(
                                new FileInputStream(aJobResultParticlePositionsFilePathname), 
                                ModelDefinitions.BUFFER_SIZE
                            )
                        )
                    );
                tmpLine = tmpBufferedReader.readLine();
                if (tmpLine.equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    // Ignore total number of particles in simulation
                    tmpBufferedReader.readLine();
                    while (true) {
                        // <editor-fold defaultstate="collapsed" desc="Molecule">
                        String tmpMoleculeName = tmpBufferedReader.readLine();
                        if (tmpMoleculeName == null) {
                            break;
                        }
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Particle">
                        tmpParticle = tmpBufferedReader.readLine();
                        if (tmpParticleToPositionsMap.containsKey(tmpParticle)) {
                            tmpPositionsList = tmpParticleToPositionsMap.get(tmpParticle);
                        } else {
                            tmpPositionsList = new LinkedList<PointInSpace>();
                            tmpParticleToPositionsMap.put(tmpParticle, tmpPositionsList);
                        }
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Number of positions">
                        tmpNumberOfParticlePositions = Integer.valueOf(tmpBufferedReader.readLine());
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Read particle positions">
                        for (int k = 0; k < tmpNumberOfParticlePositions; k++) {
                            tmpXinDpd = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpXinDpd < 0.0) {
                                tmpXinDpd = 0.0;
                            }
                            if (tmpXinDpd > tmpBoxLengthX) {
                                tmpXinDpd = tmpBoxLengthX;
                            }
                            tmpX = tmpXinDpd * tmpLengthConversionFactor;                            

                            tmpYinDpd = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpYinDpd < 0.0) {
                                tmpYinDpd = 0.0;
                            }
                            if (tmpYinDpd > tmpBoxLengthY) {
                                tmpYinDpd = tmpBoxLengthY;
                            }
                            tmpY = tmpYinDpd * tmpLengthConversionFactor;                            

                            tmpZinDpd = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpZinDpd < 0.0) {
                                tmpZinDpd = 0.0;
                            }
                            if (tmpZinDpd > tmpBoxLengthZ) {
                                tmpZinDpd = tmpBoxLengthZ;
                            }
                            tmpZ = tmpZinDpd * tmpLengthConversionFactor;                            

                            tmpPositionsList.add(new PointInSpace(tmpX, tmpY, tmpZ));
                            
                            // Ignore particle index
                            tmpBufferedReader.readLine();
                            // Ignore molecule index
                            tmpBufferedReader.readLine();
                        }
                        // </editor-fold>
                    }
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Unknown version">
                    return null;

                    // </editor-fold>
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                return null;
            } finally {
                if (tmpBufferedReader != null) {
                    try {
                        tmpBufferedReader.close();
                    } catch (IOException anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        return null;
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Return particle positions">
            return tmpParticleToPositionsMap;
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns hash map with molecule-particle positions from particle
     * positions file converted to Angstrom. Hash map maps molecule-particle
     * to list of all of its positions.
     *
     * @param aJobResultParticlePositionsFilePathname Full pathname of graphical
     * particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @return Hash map with molecule-particle positions from particle
     * positions file converted to Angstrom or null if molecule-particle
     * positions could not be read
     */
    private HashMap<String, LinkedList<PointInSpace>> readMoleculeParticlePositions(
        String aJobResultParticlePositionsFilePathname, 
        ValueItemContainer aJobInputValueItemContainer
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathname == null || aJobResultParticlePositionsFilePathname.isEmpty() || !(new File(aJobResultParticlePositionsFilePathname)).isFile()) {
            return null;
        }
        if (aJobInputValueItemContainer == null) {
            return null;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Local variables">
            String tmpParticle;
            int tmpNumberOfParticlePositions;
            double tmpX;
            double tmpY;
            double tmpZ;
            double tmpXinDpd;
            double tmpYinDpd;
            double tmpZinDpd;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set necessary variables">
            double tmpBoxLengthX = this.getSimulationBoxLengthX(aJobInputValueItemContainer);
            double tmpBoxLengthY = this.getSimulationBoxLengthY(aJobInputValueItemContainer);
            double tmpBoxLengthZ = this.getSimulationBoxLengthZ(aJobInputValueItemContainer);
            double tmpLengthConversionFactor = 
                this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
            // O(100) is reasonable capacity
            HashMap<String, LinkedList<PointInSpace>> tmpMoleculeParticleToPositionsMap = 
                new HashMap<String, LinkedList<PointInSpace>>(100);
            LinkedList<PointInSpace> tmpPositionsList;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Read molecule-particle positions">
            BufferedReader tmpBufferedReader = null;
            try {
                String tmpLine;
                if (!(new File(aJobResultParticlePositionsFilePathname)).isFile()) {
                    return null;
                }
                tmpBufferedReader = 
                    new BufferedReader(
                        new InputStreamReader(
                            new GZIPInputStream(
                                new FileInputStream(aJobResultParticlePositionsFilePathname), 
                                ModelDefinitions.BUFFER_SIZE
                            )
                        )
                    );
                tmpLine = tmpBufferedReader.readLine();
                if (tmpLine.equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    // Ignore total number of particles in simulation
                    tmpBufferedReader.readLine();
                    while (true) {
                        // <editor-fold defaultstate="collapsed" desc="Molecule">
                        String tmpMoleculeName = tmpBufferedReader.readLine();
                        if (tmpMoleculeName == null) {
                            break;
                        }
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Particle">
                        tmpParticle = tmpBufferedReader.readLine();
                        String tmpMoleculeParticle = tmpMoleculeName + SpicesConstants.PARTICLE_SEPARATOR + tmpParticle;
                        if (tmpMoleculeParticleToPositionsMap.containsKey(tmpMoleculeParticle)) {
                            tmpPositionsList = tmpMoleculeParticleToPositionsMap.get(tmpMoleculeParticle);
                        } else {
                            tmpPositionsList = new LinkedList<PointInSpace>();
                            tmpMoleculeParticleToPositionsMap.put(tmpMoleculeParticle, tmpPositionsList);
                        }
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Number of positions">
                        tmpNumberOfParticlePositions = Integer.valueOf(tmpBufferedReader.readLine());
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Read particle positions">
                        for (int k = 0; k < tmpNumberOfParticlePositions; k++) {
                            tmpXinDpd = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpXinDpd < 0.0) {
                                tmpXinDpd = 0.0;
                            }
                            if (tmpXinDpd > tmpBoxLengthX) {
                                tmpXinDpd = tmpBoxLengthX;
                            }
                            tmpX = tmpXinDpd * tmpLengthConversionFactor;                            

                            tmpYinDpd = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpYinDpd < 0.0) {
                                tmpYinDpd = 0.0;
                            }
                            if (tmpYinDpd > tmpBoxLengthY) {
                                tmpYinDpd = tmpBoxLengthY;
                            }
                            tmpY = tmpYinDpd * tmpLengthConversionFactor;                            

                            tmpZinDpd = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpZinDpd < 0.0) {
                                tmpZinDpd = 0.0;
                            }
                            if (tmpZinDpd > tmpBoxLengthZ) {
                                tmpZinDpd = tmpBoxLengthZ;
                            }
                            tmpZ = tmpZinDpd * tmpLengthConversionFactor;                            

                            tmpPositionsList.add(new PointInSpace(tmpX, tmpY, tmpZ));
                            
                            // Ignore particle index
                            tmpBufferedReader.readLine();
                            // Ignore molecule index
                            tmpBufferedReader.readLine();
                        }
                        // </editor-fold>
                    }
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Unknown version">
                    return null;
                    // </editor-fold>
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                return null;
            } finally {
                if (tmpBufferedReader != null) {
                    try {
                        tmpBufferedReader.close();
                    } catch (IOException anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        return null;
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Return particle positions">
            return tmpMoleculeParticleToPositionsMap;
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns hash map with molecule center positions (NOT center of mass) 
     * from particle positions file converted to Angstrom. Hash map maps 
     * molecule centers to list of all of its positions.
     *
     * @param aJobResultParticlePositionsFilePathname Full pathname of graphical
     * particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @return Hash map with molecule center positions (NOT center of mass) 
     * from particle positions file converted to Angstrom or null if molecule
     * center positions could not be evaluated
     */
    private HashMap<String, LinkedList<PointInSpace>> readMoleculeCenterPositions(
        String aJobResultParticlePositionsFilePathname, 
        ValueItemContainer aJobInputValueItemContainer
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathname == null || 
            aJobResultParticlePositionsFilePathname.isEmpty() || 
            !(new File(aJobResultParticlePositionsFilePathname)).isFile()
        ) {
            return null;
        }
        if (aJobInputValueItemContainer == null) {
            return null;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Local variables">
            String tmpParticle;
            int tmpNumberOfParticlePositions;
            double tmpX;
            double tmpY;
            double tmpZ;
            double tmpXinDpd;
            double tmpYinDpd;
            double tmpZinDpd;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set necessary variables">
            double tmpBoxLengthX = this.getSimulationBoxLengthX(aJobInputValueItemContainer);
            double tmpBoxLengthY = this.getSimulationBoxLengthY(aJobInputValueItemContainer);
            double tmpBoxLengthZ = this.getSimulationBoxLengthZ(aJobInputValueItemContainer);
            double tmpLengthConversionFactor = 
                this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
            // O(100) is reasonable capacity
            HashMap<String, LinkedList<PointInSpace>> tmpMoleculeCenterToPositionsMap = new HashMap<>(100);
            int tmpTotalNumberOfMoleculesInSimulation = 
                this.getTotalNumberOfMoleculesInSimulation(aJobInputValueItemContainer);
            // tmpMoleculeNames[i] corresponds to tmpMoleculeCenters[i]
            String[] tmpMoleculeNames = new String[tmpTotalNumberOfMoleculesInSimulation];
            PointInSpace[] tmpMoleculeCenters = new PointInSpace[tmpTotalNumberOfMoleculesInSimulation];
            for (int k = 0; k < tmpMoleculeCenters.length; k++) {
                tmpMoleculeCenters[k] = new PointInSpace(0.0, 0.0, 0.0);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Read particle positions">
            BufferedReader tmpBufferedReader = null;
            try {
                String tmpLine;
                if (!(new File(aJobResultParticlePositionsFilePathname)).isFile()) {
                    return null;
                }
                tmpBufferedReader = 
                    new BufferedReader(
                        new InputStreamReader(
                            new GZIPInputStream(
                                new FileInputStream(aJobResultParticlePositionsFilePathname), 
                                ModelDefinitions.BUFFER_SIZE
                            )
                        )
                    );
                tmpLine = tmpBufferedReader.readLine();
                if (tmpLine.equals("Version 1.0.0")) {
                    // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                    // Ignore total number of particles in simulation
                    tmpBufferedReader.readLine();
                    while (true) {
                        // <editor-fold defaultstate="collapsed" desc="Molecule">
                        String tmpMoleculeName = tmpBufferedReader.readLine();
                        if (tmpMoleculeName == null) {
                            break;
                        }
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Particle">
                        tmpParticle = tmpBufferedReader.readLine();
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Number of positions">
                        tmpNumberOfParticlePositions = Integer.valueOf(tmpBufferedReader.readLine());
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Read particle positions">
                        for (int k = 0; k < tmpNumberOfParticlePositions; k++) {
                            tmpXinDpd = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpXinDpd < 0.0) {
                                tmpXinDpd = 0.0;
                            }
                            if (tmpXinDpd > tmpBoxLengthX) {
                                tmpXinDpd = tmpBoxLengthX;
                            }
                            tmpX = tmpXinDpd * tmpLengthConversionFactor;                            

                            tmpYinDpd = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpYinDpd < 0.0) {
                                tmpYinDpd = 0.0;
                            }
                            if (tmpYinDpd > tmpBoxLengthY) {
                                tmpYinDpd = tmpBoxLengthY;
                            }
                            tmpY = tmpYinDpd * tmpLengthConversionFactor;                            

                            tmpZinDpd = Double.valueOf(tmpBufferedReader.readLine());
                            if (tmpZinDpd < 0.0) {
                                tmpZinDpd = 0.0;
                            }
                            if (tmpZinDpd > tmpBoxLengthZ) {
                                tmpZinDpd = tmpBoxLengthZ;
                            }
                            tmpZ = tmpZinDpd * tmpLengthConversionFactor;                            
                            
                            // Ignore particle index
                            tmpBufferedReader.readLine();
                            // Read molecule index
                            int tmpMoleculeIndex = Integer.valueOf(tmpBufferedReader.readLine());

                            tmpMoleculeCenters[tmpMoleculeIndex].add(tmpX, tmpY, tmpZ);
                            tmpMoleculeNames[tmpMoleculeIndex] = tmpMoleculeName;
                        }
                        // </editor-fold>
                    }
                    // O(100) is reasonable capacity
                    HashMap<String, Integer> tmpMoleculeNameToParticleCountMap = new HashMap<>(100);
                    LinkedList<PointInSpace> tmpMoleculeCenterList = null;
                    int tmpNumberOfParticlesPerMolecule = -1;
                    for (int k = 0; k < tmpMoleculeNames.length; k++) {
                        if (tmpMoleculeCenterToPositionsMap.containsKey(tmpMoleculeNames[k])) {
                            tmpMoleculeCenterList = tmpMoleculeCenterToPositionsMap.get(tmpMoleculeNames[k]);
                        } else {
                            tmpMoleculeCenterList = new LinkedList<>();
                            tmpMoleculeCenterToPositionsMap.put(tmpMoleculeNames[k], tmpMoleculeCenterList);
                        }
                        if (tmpMoleculeNameToParticleCountMap.containsKey(tmpMoleculeNames[k])) {
                            tmpNumberOfParticlesPerMolecule = tmpMoleculeNameToParticleCountMap.get(tmpMoleculeNames[k]);
                        } else {
                            tmpNumberOfParticlesPerMolecule = 
                                this.getNumberOfParticlesPerMolecule(
                                    tmpMoleculeNames[k], 
                                    aJobInputValueItemContainer
                                );
                            tmpMoleculeNameToParticleCountMap.put(tmpMoleculeNames[k], tmpNumberOfParticlesPerMolecule);
                        }
                        tmpMoleculeCenters[k].divide((double) tmpNumberOfParticlesPerMolecule);
                        tmpMoleculeCenterList.add(tmpMoleculeCenters[k]);
                    }
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Unknown version">
                    return null;
                    // </editor-fold>
                }
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                return null;
            } finally {
                if (tmpBufferedReader != null) {
                    try {
                        tmpBufferedReader.close();
                    } catch (IOException anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        return null;
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Return molecule center positions">
            return tmpMoleculeCenterToPositionsMap;
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Jdpd input file related methods">
    /**
     * Converts molecular structure for Jdpd input file: Start-geometry
     * files for every molecule are written if aDestinationPath is specified.
     *
     * @param aMoleculeTableValueItem Value item forMoleculeTable (is not changed)
     * @param aJobInputValueItemContainer Job input value item container
     * @param aDestinationPath Full path of Jdpd input file (may be
     * null/empty then no start-geometry files of molecules are written)
     * @return ConvertedMoleculeTable value item for Jdpd input file
     */
    private ValueItem convertMolecularStructureForJdpdInputFile(ValueItem aMoleculeTableValueItem, ValueItemContainer aJobInputValueItemContainer, String aDestinationPath) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeTableValueItem == null) {
            ModelUtils.appendToLogfile(true, "UtilityJobMethods.convertMolecularStructureForJdpdInputFile(): aMoleculeTableValueItem == null");
            return null;
        }
        if (aJobInputValueItemContainer == null) {
            ModelUtils.appendToLogfile(true, "UtilityJobMethods.convertMolecularStructureForJdpdInputFile(): aJobInputValueItemContainer == null");
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="1. Setup converted molecules value item">
        // Clone aMoleculeTableValueItem
        ValueItem tmpConvertedMoleculeTableValueItem = aMoleculeTableValueItem.getClone();
        // IMPORTANT in xy-layer and sphere: Sort so that rows with protein data come first
        tmpConvertedMoleculeTableValueItem.sortMatrixRowsWithProteinDataRowsFirst();
        // IMPORTANT: Suppress all possible notifications of cloned value item
        tmpConvertedMoleculeTableValueItem.setUpdateNotifier(false);
        tmpConvertedMoleculeTableValueItem.removeAllChangeReceivers();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="2. Write molecular start-geometry files">
        for (int i = 0; i < tmpConvertedMoleculeTableValueItem.getMatrixRowCount(); i++) {
            String tmpFileName = ModelDefinitions.JDPD_POSITIONS_BONDS_FILE_PREFIX + String.valueOf(i).trim() + FileOutputStrings.TEXT_FILE_ENDING;
            if (aDestinationPath != null && !aDestinationPath.isEmpty() && (new File(aDestinationPath)).isDirectory()) {
                String tmpFilePathname = aDestinationPath + File.separatorChar + tmpFileName;
                String tmpMoleculeName = tmpConvertedMoleculeTableValueItem.getValue(i, 0);
                if (!this.writeJdpdPositionsBondsFile(aJobInputValueItemContainer, tmpMoleculeName, tmpFilePathname)) {
                    ModelUtils.appendToLogfile(true, "UtilityJobMethods.convertMolecularStructureForJdpdInputFile(): this.writeJdpdPositionsBondsFile(aJobInputValueItemContainer, tmpMoleculeName, tmpFilePathname) fails.");
                    return null;
                }
            }
            tmpConvertedMoleculeTableValueItem.setValue(tmpFileName, i, 1);
        }
        // </editor-fold>
        return tmpConvertedMoleculeTableValueItem;
    }

    /**
     * Writes Jdpd positions and bonds file. A molecule file contains
     * the x,y,z positions of all particles of this molecule type based on
     * bulk/compartments.
     *
     * @param aJobInputValueItemContainer Job input value item container
     * @param aMoleculeName Name of molecule
     * @param aJdpdPositionsBondsFilePathname File pathname of the Jdpd 
     * positions and bonds file (NOTE: Destination directory must
     * already exist)
     * @return True: Operation successful, false: Otherwise
     */
    private boolean writeJdpdPositionsBondsFile(ValueItemContainer aJobInputValueItemContainer, String aMoleculeName, String aJdpdPositionsBondsFilePathname) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): aJobInputValueItemContainer == null");
            return false;
        }
        if (aMoleculeName == null || aMoleculeName.isEmpty()) {
            ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): aMoleculeName == null || aMoleculeName.isEmpty()");
            return false;
        }
        if (aJdpdPositionsBondsFilePathname == null || aJdpdPositionsBondsFilePathname.isEmpty()
                || !(new File((new File(aJdpdPositionsBondsFilePathname)).getParent())).isDirectory()) {
            ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): aJdpdPositionsBondsFilePathname == null ...");
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpCompartmentContainer">
        CompartmentContainer tmpCompartmentContainer = null;
        try {
            tmpCompartmentContainer = aJobInputValueItemContainer.getValueItem("Compartments").getCompartmentContainer();
            if (tmpCompartmentContainer == null) {
                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): tmpCompartmentContainer == null");
                return false;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): 'Set tmpCompartmentContainer' throws exception.");
            ModelUtils.appendToLogfile(true, anException);
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set random number generator and seed">
        int tmpRandomSeed = tmpCompartmentContainer.getGeometryRandomSeed();
        IRandom tmpRandomNumberGenerator = this.miscUtilityMethods.getRandomNumberGenerator(tmpRandomSeed);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set tmpIsMoleculeStartGeometryCompressedToSinglePoint">
        boolean tmpIsMoleculeStartGeometryCompressedToSinglePoint = tmpCompartmentContainer.isMoleculeStartGeometryCompressedToSinglePoint();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Delete aJdpdPositionsBondsFilePathname if necessary">
        if (!this.fileUtilityMethods.deleteSingleFile(aJdpdPositionsBondsFilePathname)) {
            ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): this.fileUtilityMethods.deleteSingleFile(aJdpdPositionsBondsFilePathname) fails.");
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set line visualizers and local variables">
        String tmpLineVisualizerLarge = "#-----------------------------------------------------------------------";
        String tmpLineVisualizerSmall = "#---------";
        StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE_TINY);
        String tmpOneSpace = " ";
        // </editor-fold>
        PrintWriter tmpPrintWriter = null;
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize tmpOutputLineList">
            LinkedList<String> tmpOutputLineList = new LinkedList<>();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set particle radius and bond length">
            ValueItem tmpDensityInfoValueItem = tmpCompartmentContainer.getDensityInfoValueItem();
            if (tmpDensityInfoValueItem == null) {
                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): tmpDensityInfoValueItem == null");
                return false;
            }
            double tmpSingleParticleRadius = this.getRadiusOfParticlesInDpdBox(1, tmpDensityInfoValueItem.getValueAsDouble(), ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES);
            // Bond length = 2 * radius of single particle
            double tmpBondLength = 2.0 * tmpSingleParticleRadius;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize tmpAreProteinBackboneForcesActivated">
            boolean tmpAreProteinBackboneForcesActivated = false;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Write particle information and set tmpLineNumber">
            ValueItem tmpMoleculeInfoValueItem = tmpCompartmentContainer.getMoleculeInfoValueItem();
            if (tmpMoleculeInfoValueItem == null) {
                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): tmpMoleculeInfoValueItem == null");
                return false;
            }
            int tmpTotalNumberOfParticlesOfMoleculeInSimulation = this.getTotalNumberOfParticlesOfMoleculeInSimulation(tmpMoleculeInfoValueItem, aMoleculeName);
            int tmpNumberOfParticlesPerMolecule = this.getNumberOfParticlesPerMolecule(tmpMoleculeInfoValueItem, aMoleculeName);
            tmpOutputLineList.add(tmpLineVisualizerLarge);
            tmpOutputLineList.add("TotalMoleculeParticleNumber " + String.valueOf(tmpTotalNumberOfParticlesOfMoleculeInSimulation));
            tmpOutputLineList.add("SingleMoleculeParticleNumber " + String.valueOf(tmpNumberOfParticlesPerMolecule));
            int tmpLineNumber = 1;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Write coordinates of molecules in compartments">
            // IMPORTANT: Clear possible old lists of excluded spheres of all bodies
            tmpCompartmentContainer.getCompartmentBox().clearExcludedSphereListsOfBodies();
            ArrayList<BodyInterface> tmpBodyList = tmpCompartmentContainer.getCompartmentBox().getBodies();
            if (tmpBodyList.size() > 0) {
                ValueItem tmpGeometryDataValueItem = null;
                ValueItem tmpChemicalCompositionValueItem = null;
                for (BodyInterface tmpBody : tmpBodyList) {
                    switch (tmpBody.getBodyType()) {
                        // <editor-fold defaultstate="collapsed" desc="Spheres">
                        case SPHERE:
                            // <editor-fold defaultstate="collapsed" desc="Set value items">
                            BodySphere tmpSphere = (BodySphere) tmpBody;
                            // Sphere contains geometry data value item
                            tmpGeometryDataValueItem = tmpSphere.getGeometryDataValueItem();
                            if (tmpGeometryDataValueItem == null) {
                                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): SPHERE: tmpGeometryDataValueItem == null");
                                return false;
                            }
                            // Get corresponding chemical composition value item
                            tmpChemicalCompositionValueItem = tmpCompartmentContainer.getSphereChemicalCompositionValueItemOfBlock(tmpGeometryDataValueItem.getBlockName());
                            if (tmpChemicalCompositionValueItem == null) {
                                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): SPHERE: tmpChemicalCompositionValueItem == null");
                                return false;
                            }
                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Write particle information for each molecule">
                            for (int i = 0; i < tmpChemicalCompositionValueItem.getMatrixRowCount(); i++) {
                                // tmpChemicalCompositionValueItem column 0 : Molecule name
                                if (tmpChemicalCompositionValueItem.getValue(i, 0).equals(aMoleculeName)) {
                                    // <editor-fold defaultstate="collapsed" desc="Set molecule information">
                                    // Molecule name
                                    String tmpMoleculeName = tmpChemicalCompositionValueItem.getValue(i, 0);
                                    // Molecular structure and possible protein data
                                    String tmpMolecularStructureString = tmpChemicalCompositionValueItem.getValue(i, 1);
                                    boolean tmpIsSingleParticleMolecule = this.particlePattern.matcher(tmpMolecularStructureString).matches();
                                    String tmpProteinData = "";
                                    if (tmpChemicalCompositionValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                                        tmpProteinData = tmpChemicalCompositionValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                                    }
                                    // Quantity in volume
                                    int tmpQuantityInVolume = tmpChemicalCompositionValueItem.getValueAsInt(i, 5);
                                    // Quantity on surface
                                    int tmpQuantityOnSurface = tmpChemicalCompositionValueItem.getValueAsInt(i, 6);
                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="Write particle information">
                                    if (tmpQuantityInVolume > 0 || tmpQuantityOnSurface > 0) {
                                        if (this.graphicsUtilityMethods.isProtein3dStructureInSphere(tmpChemicalCompositionValueItem, i)) {
                                            // <editor-fold defaultstate="collapsed" desc="Protein data are used for 3D structure">
                                            if (tmpQuantityOnSurface > 0 || tmpProteinData.isEmpty()) {
                                                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): SPHERE: tmpQuantityOnSurface > 0 || tmpProteinData.isEmpty()");
                                                return false;
                                            }
                                            // <editor-fold defaultstate="collapsed" desc="Write comment in file">
                                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                                            tmpOutputLineList.add("# Compartment Sphere with Proteins");
                                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                                            tmpOutputLineList.add("# Compartment Sphere Protein Information:");
                                            tmpOutputLineList.add("# Protein Name              = " + tmpMoleculeName);
                                            tmpOutputLineList.add("# Particles per Protein     = " + String.valueOf(tmpNumberOfParticlesPerMolecule));
                                            tmpOutputLineList.add("# Quantity in Volume        = " + String.valueOf(tmpQuantityInVolume));
                                            tmpOutputLineList.add("# Quantity on Surface       = " + String.valueOf(tmpQuantityOnSurface));
                                            tmpOutputLineList.add(tmpLineVisualizerSmall);
                                            // </editor-fold>
                                            // Amino acids MUST already be initialized
                                            // Initialize tmpPdbToDpd
                                            PdbToDpd tmpPdbToDpd = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                                            tmpAreProteinBackboneForcesActivated = true;
                                            boolean tmpIsProteinRandom3DOrientation = false;
                                            if (this.graphicsUtilityMethods.isRandom3dStructureGeometryInSphere(tmpChemicalCompositionValueItem, i)) {
                                                tmpIsProteinRandom3DOrientation = true;
                                                tmpPdbToDpd.setSeed(tmpRandomSeed);
                                                tmpPdbToDpd.setRandomNumberGenerator(tmpRandomNumberGenerator);
                                            } else {
                                                tmpPdbToDpd.setDefaultRotation();
                                            }
                                            if (tmpQuantityInVolume == 1) {
                                                // <editor-fold defaultstate="collapsed" desc="1 protein in sphere">
                                                tmpPdbToDpd.setCenter(tmpSphere.getBodyCenter().getX(), tmpSphere.getBodyCenter().getY(), tmpSphere.getBodyCenter().getZ());
                                                tmpPdbToDpd.setRadius(tmpSphere.getRadius() * ModelDefinitions.DECREASE_FACTOR);
                                                if (tmpIsProteinRandom3DOrientation) {
                                                    tmpPdbToDpd.setRandomOrientation();
                                                }
                                                // NOTE: First C-alpha index is set to 1.
                                                int tmpFirstCalphaIndex = 1;
                                                String[] tmpProteinParticlesConnectionTableArray = tmpPdbToDpd.getCoordinateConnectionTableArray(tmpLineNumber, tmpFirstCalphaIndex);
                                                for (String tmpLine : tmpProteinParticlesConnectionTableArray) {
                                                    tmpOutputLineList.add(tmpLine);
                                                }
                                                // Write separator after molecule
                                                tmpOutputLineList.add(tmpLineVisualizerSmall);
                                                tmpLineNumber = tmpPdbToDpd.getLastUsedIndex() + 1;
                                                // </editor-fold>
                                            } else {
                                                // <editor-fold defaultstate="collapsed" desc="Multiple proteins in sphere">
                                                // Determine radius of protein
                                                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                                int tmpTotalNumberOfParticlesOfProtein = tmpSpices.getTotalNumberOfParticles();
                                                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                                                double tmpRadiusOfProtein = this.miscUtilityMethods.getRadiusOfParticlesInDpdBox(tmpTotalNumberOfParticlesOfProtein, tmpDensityInfoValueItem.getValueAsDouble());
                                                // Determine non-overlapping spheres for proteins
                                                LinkedList<BodySphere> tmpSphereList = 
                                                    tmpSphere.getNonOverlappingRandomSpheres(
                                                        tmpQuantityInVolume, 
                                                        tmpRadiusOfProtein,
                                                        Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                        tmpRandomNumberGenerator
                                                    );
                                                int tmpFirstCalphaIndex = 1;
                                                for (BodySphere tmpSingleSphere : tmpSphereList) {
                                                    tmpPdbToDpd.setCenter(tmpSingleSphere.getBodyCenter().getX(), tmpSingleSphere.getBodyCenter().getY(), tmpSingleSphere.getBodyCenter().getZ());
                                                    tmpPdbToDpd.setRadius(tmpSingleSphere.getRadius() * ModelDefinitions.DECREASE_FACTOR);
                                                    if (tmpIsProteinRandom3DOrientation) {
                                                        tmpPdbToDpd.setRandomOrientation();
                                                    }
                                                    String[] tmpProteinParticlesConnectionTableArray = tmpPdbToDpd.getCoordinateConnectionTableArray(tmpLineNumber, tmpFirstCalphaIndex);
                                                    for (String tmpLine : tmpProteinParticlesConnectionTableArray) {
                                                        tmpOutputLineList.add(tmpLine);
                                                    }
                                                    // Write separator after molecule
                                                    tmpOutputLineList.add(tmpLineVisualizerSmall);
                                                    tmpLineNumber = tmpPdbToDpd.getLastUsedIndex() + 1;
                                                }
                                                // </editor-fold>
                                            }
                                            PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                            // </editor-fold>
                                        } else {
                                            // <editor-fold defaultstate="collapsed" desc="No protein data are used">
                                            // <editor-fold defaultstate="collapsed" desc="Write comment in file">
                                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                                            tmpOutputLineList.add("# Compartment Sphere");
                                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                                            tmpOutputLineList.add("# Compartment Sphere Molecule Information:");
                                            tmpOutputLineList.add("# Molecule Name             = " + tmpMoleculeName);
                                            // Old code:
                                            // tmpOutputLineList.add("# Molecular Structure       = " + this.whitespacePattern.matcher(tmpMolecularStructureString).replaceAll(""));
                                            tmpOutputLineList.add("# Molecular Structure       = " + StringUtils.deleteWhitespace(tmpMolecularStructureString));
                                            tmpOutputLineList.add("# Particles per Molecule    = " + String.valueOf(tmpNumberOfParticlesPerMolecule));
                                            tmpOutputLineList.add("# Quantity in Volume        = " + String.valueOf(tmpQuantityInVolume));
                                            tmpOutputLineList.add("# Quantity on Surface       = " + String.valueOf(tmpQuantityOnSurface));
                                            tmpOutputLineList.add(tmpLineVisualizerSmall);
                                            // </editor-fold>
                                            PointInSpace[] tmpFirstParticleCoordinates = new PointInSpace[tmpQuantityInVolume + tmpQuantityOnSurface];
                                            PointInSpace[] tmpLastParticleCoordinates = new PointInSpace[tmpQuantityInVolume + tmpQuantityOnSurface];
                                            if (tmpQuantityInVolume > 0) {
                                                if (tmpIsSingleParticleMolecule) {
                                                    tmpSphere.fillRandomPointsInVolumeWithExcludingSpheres(
                                                        tmpFirstParticleCoordinates, 
                                                        0, 
                                                        tmpQuantityInVolume,
                                                        Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                        tmpRandomNumberGenerator
                                                    );
                                                    tmpLastParticleCoordinates = tmpFirstParticleCoordinates;
                                                } else {
                                                    if (tmpIsMoleculeStartGeometryCompressedToSinglePoint) {
                                                        tmpLastParticleCoordinates = tmpFirstParticleCoordinates;
                                                    } else {
                                                        tmpSphere.fillRandomPointsInVolumeWithExcludingSpheres(
                                                            tmpFirstParticleCoordinates, 
                                                            tmpLastParticleCoordinates, 
                                                            0, 
                                                            tmpQuantityInVolume,
                                                            tmpBondLength, 
                                                            Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                            tmpRandomNumberGenerator
                                                        );
                                                    }
                                                }
                                            }
                                            if (tmpQuantityOnSurface > 0) {
                                                if (this.graphicsUtilityMethods.isOutOfSurfaceGeometryInSphere(tmpChemicalCompositionValueItem, i)) {
                                                    tmpSphere.fillRandomPointsOnSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                    this.graphicsUtilityMethods.fillPointsOutsideSphereSurface(
                                                        tmpFirstParticleCoordinates, 
                                                        tmpLastParticleCoordinates, 
                                                        tmpQuantityInVolume, 
                                                        tmpQuantityOnSurface, 
                                                        tmpSphere, 
                                                        tmpCompartmentContainer.getCompartmentBox()
                                                    );
                                                } else {
                                                    if (this.graphicsUtilityMethods.isUpperSurfaceGeometryInSphere(tmpChemicalCompositionValueItem, i)) {
                                                        tmpSphere.fillUpperRandomPointsOnSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                    } else if (this.graphicsUtilityMethods.isMiddleSurfaceGeometryInSphere(tmpChemicalCompositionValueItem, i)) {
                                                        tmpSphere.fillMiddleRandomPointsOnSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                    } else if (this.graphicsUtilityMethods.isWholeSurfaceGeometryInSphere(tmpChemicalCompositionValueItem, i)) {
                                                        tmpSphere.fillRandomPointsOnSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                    }
                                                    // Center of sphere
                                                    PointInSpace tmpCenterOfSphere = tmpSphere.getBodyCenter();
                                                    Arrays.fill(tmpLastParticleCoordinates, tmpQuantityInVolume, tmpQuantityInVolume + tmpQuantityOnSurface, tmpCenterOfSphere);
                                                }
                                            }
                                            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                            tmpSpices.setCoordinates(tmpLineNumber, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
                                            String[][] tmpParticlePositionsAndConnections = tmpSpices.getParticlePositionsAndConnections();
                                            tmpSpices.destroySpicesMatrix();
                                            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                                            int tmpParticleCounter = 0;
                                            for (int j = 0; j < tmpParticlePositionsAndConnections.length; j++) {
                                                tmpBuffer.delete(0, tmpBuffer.length());
                                                tmpBuffer.append(tmpParticlePositionsAndConnections[j][0]); // Line_number
                                                tmpBuffer.append(tmpOneSpace);
                                                tmpBuffer.append(tmpParticlePositionsAndConnections[j][1]); // Particle
                                                // Add particle index for inner molecule potentials: Index 0 (zero) means no index potential
                                                tmpBuffer.append(tmpOneSpace);
                                                tmpBuffer.append(tmpParticlePositionsAndConnections[j][2]); // Particle_Index
                                                for (int k = 3; k < 6; k++) {
                                                    // Coordinates
                                                    tmpBuffer.append(tmpOneSpace);
                                                    tmpBuffer.append(this.stringUtilityMethods.formatDoubleValue(tmpParticlePositionsAndConnections[j][k], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES));
                                                }
                                                for (int k = 6; k < tmpParticlePositionsAndConnections[j].length; k++) {
                                                    // Bonding
                                                    tmpBuffer.append(tmpOneSpace);
                                                    tmpBuffer.append(tmpParticlePositionsAndConnections[j][k]);
                                                }
                                                // Write single particle information
                                                tmpOutputLineList.add(tmpBuffer.toString());
                                                tmpParticleCounter++;
                                                if (tmpParticleCounter == tmpNumberOfParticlesPerMolecule) {
                                                    // Write GENERAL_SEPARATOR after molecule
                                                    tmpOutputLineList.add(tmpLineVisualizerSmall);
                                                    tmpParticleCounter = 0;
                                                }
                                            }
                                            tmpLineNumber += tmpParticlePositionsAndConnections.length;
                                            // </editor-fold>
                                        }
                                    }
                                    // </editor-fold>
                                    break;
                                }
                            }
                            // </editor-fold>
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="xy-layers">
                        case XY_LAYER:
                            // <editor-fold defaultstate="collapsed" desc="Set value items">
                            BodyXyLayer tmpXyLayer = (BodyXyLayer) tmpBody;
                            // xy-Layer contains geometry data value item
                            tmpGeometryDataValueItem = tmpXyLayer.getGeometryDataValueItem();
                            if (tmpGeometryDataValueItem == null) {
                                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): XY_LAYER: tmpGeometryDataValueItem == null");
                                return false;
                            }
                            // Corresponding geometry display value item
                            ValueItem tmpGeometryDisplayValueItem = tmpGeometryDataValueItem.getDisplayValueItem();
                            if (tmpGeometryDisplayValueItem == null) {
                                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): XY_LAYER: tmpGeometryDisplayValueItem == null");
                                return false;
                            }
                            // Get corresponding chemical composition value item
                            tmpChemicalCompositionValueItem = tmpCompartmentContainer.getXyLayerChemicalCompositionValueItemOfBlock(tmpGeometryDataValueItem.getBlockName());
                            if (tmpChemicalCompositionValueItem == null) {
                                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): XY_LAYER: tmpChemicalCompositionValueItem == null");
                                return false;
                            }
                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Write particle information for specified molecule">
                            for (int i = 0; i < tmpChemicalCompositionValueItem.getMatrixRowCount(); i++) {
                                // tmpChemicalCompositionValueItem column 0 : Molecule name
                                if (tmpChemicalCompositionValueItem.getValue(i, 0).equals(aMoleculeName)) {
                                    // <editor-fold defaultstate="collapsed" desc="Set molecule information">
                                    // Molecule name
                                    String tmpMoleculeName = tmpChemicalCompositionValueItem.getValue(i, 0);
                                    // Molecular structure
                                    String tmpMolecularStructureString = tmpChemicalCompositionValueItem.getValue(i, 1);
                                    boolean tmpIsSingleParticleMolecule = this.particlePattern.matcher(tmpMolecularStructureString).matches();
                                    String tmpProteinData = "";
                                    if (tmpChemicalCompositionValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                                        tmpProteinData = tmpChemicalCompositionValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                                    }
                                    // Quantity in volume
                                    int tmpQuantityInVolume = tmpChemicalCompositionValueItem.getValueAsInt(i, 5);
                                    // Quantity on surface
                                    int tmpQuantityOnSurface = tmpChemicalCompositionValueItem.getValueAsInt(i, 6);
                                    // </editor-fold>
                                    // <editor-fold defaultstate="collapsed" desc="Write particle information">
                                    if (this.graphicsUtilityMethods.isLatticeGeometryInXyLayer(tmpChemicalCompositionValueItem)) {
                                        // <editor-fold defaultstate="collapsed" desc="Lattice positions in xy-layer">
                                        if (tmpQuantityInVolume > 0 || tmpQuantityOnSurface > 0) {
                                            // <editor-fold defaultstate="collapsed" desc="Write comment in file">
                                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                                            tmpOutputLineList.add("# Compartment xy-Layer");
                                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                                            tmpOutputLineList.add("# Compartment xy-Layer Molecule Information:");
                                            tmpOutputLineList.add("# Molecule Name             = " + tmpMoleculeName);
                                            // Old code:
                                            // tmpOutputLineList.add("# Molecular Structure       = " + this.whitespacePattern.matcher(tmpMolecularStructureString).replaceAll(""));
                                            tmpOutputLineList.add("# Molecular Structure       = " + StringUtils.deleteWhitespace(tmpMolecularStructureString));
                                            tmpOutputLineList.add("# Particles per Molecule    = " + String.valueOf(tmpNumberOfParticlesPerMolecule));
                                            tmpOutputLineList.add("# Quantity in Volume        = " + String.valueOf(tmpQuantityInVolume));
                                            tmpOutputLineList.add("# Quantity on Surface       = " + String.valueOf(tmpQuantityOnSurface));
                                            tmpOutputLineList.add("# Orientation               = " + tmpChemicalCompositionValueItem.getValue(i, 7));
                                            tmpOutputLineList.add(tmpLineVisualizerSmall);

                                            // </editor-fold>
                                            PointInSpace[] tmpFirstParticleCoordinates = new PointInSpace[tmpQuantityInVolume + tmpQuantityOnSurface];
                                            tmpXyLayer.getSimpleCubicLatticePointsInBuffer(tmpFirstParticleCoordinates, tmpBondLength);

                                            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                            tmpSpices.setCoordinates(tmpLineNumber, tmpFirstParticleCoordinates, tmpFirstParticleCoordinates, tmpBondLength);
                                            String[][] tmpParticlePositionsAndConnections = tmpSpices.getParticlePositionsAndConnections();
                                            tmpSpices.destroySpicesMatrix();
                                            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);

                                            int tmpParticleCounter = 0;
                                            for (int j = 0; j < tmpParticlePositionsAndConnections.length; j++) {
                                                tmpBuffer.delete(0, tmpBuffer.length());
                                                tmpBuffer.append(tmpParticlePositionsAndConnections[j][0]); // Line_number
                                                tmpBuffer.append(tmpOneSpace);
                                                tmpBuffer.append(tmpParticlePositionsAndConnections[j][1]); // Particle
                                                // Add particle index for inner molecule potentials: Index 0 (zero) means no index potential
                                                tmpBuffer.append(tmpOneSpace);
                                                tmpBuffer.append(tmpParticlePositionsAndConnections[j][2]); // Particle_Index
                                                for (int k = 3; k < 6; k++) {
                                                    // Coordinates
                                                    tmpBuffer.append(tmpOneSpace);
                                                    tmpBuffer.append(this.stringUtilityMethods.formatDoubleValue(tmpParticlePositionsAndConnections[j][k], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES));
                                                }
                                                for (int k = 6; k < tmpParticlePositionsAndConnections[j].length; k++) {
                                                    // Bonding
                                                    tmpBuffer.append(tmpOneSpace);
                                                    tmpBuffer.append(tmpParticlePositionsAndConnections[j][k]);
                                                }
                                                // Write single particle information
                                                tmpOutputLineList.add(tmpBuffer.toString());
                                                tmpParticleCounter++;
                                                if (tmpParticleCounter == tmpNumberOfParticlesPerMolecule) {
                                                    // Write GENERAL_SEPARATOR after molecule
                                                    tmpOutputLineList.add(tmpLineVisualizerSmall);
                                                    tmpParticleCounter = 0;
                                                }
                                            }
                                            tmpLineNumber += tmpParticlePositionsAndConnections.length;
                                        }

                                        // </editor-fold>
                                    } else {
                                        // <editor-fold defaultstate="collapsed" desc="Random positions in xy-layer">
                                        if (tmpQuantityInVolume > 0 || tmpQuantityOnSurface > 0) {
                                            if (this.graphicsUtilityMethods.is3dStructureGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                // <editor-fold defaultstate="collapsed" desc="Protein data are used for 3D structure in spheres in xy-layer">
                                                if (tmpQuantityOnSurface > 0 || tmpProteinData.isEmpty()) {
                                                    ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): XY_LAYER: tmpQuantityOnSurface > 0 || tmpProteinData.isEmpty()");
                                                    return false;
                                                }
                                                // <editor-fold defaultstate="collapsed" desc="Write comment in file">
                                                tmpOutputLineList.add(tmpLineVisualizerLarge);
                                                tmpOutputLineList.add("# Compartment xy-Layer with Molecules in excluded Spheres");
                                                tmpOutputLineList.add(tmpLineVisualizerLarge);
                                                tmpOutputLineList.add("# Compartment xy-Layer Molecule Information:");
                                                tmpOutputLineList.add("# Molecule Name             = " + tmpMoleculeName);
                                                // Old code:
                                                // tmpOutputLineList.add("# Molecular Structure       = " + this.whitespacePattern.matcher(tmpMolecularStructureString).replaceAll(""));
                                                tmpOutputLineList.add("# Molecular Structure       = " + StringUtils.deleteWhitespace(tmpMolecularStructureString));
                                                tmpOutputLineList.add("# Particles per Molecule    = " + String.valueOf(tmpNumberOfParticlesPerMolecule));
                                                tmpOutputLineList.add("# Quantity in Volume        = " + String.valueOf(tmpQuantityInVolume));
                                                tmpOutputLineList.add("# Quantity on Surface       = " + String.valueOf(tmpQuantityOnSurface));
                                                tmpOutputLineList.add("# Orientation               = " + tmpChemicalCompositionValueItem.getValue(i, 7));
                                                tmpOutputLineList.add(tmpLineVisualizerSmall);
                                                // </editor-fold>
                                                // Amino acids MUST already be initialized
                                                // Initialize tmpPdbToDpd
                                                PdbToDpd tmpPdbToDpd = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                                                tmpAreProteinBackboneForcesActivated = true;
                                                boolean tmpIsProteinRandom3DOrientation = false;
                                                if (this.graphicsUtilityMethods.isRandom3dStructureGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                    tmpIsProteinRandom3DOrientation = true;
                                                    tmpPdbToDpd.setSeed(tmpRandomSeed);
                                                    tmpPdbToDpd.setRandomNumberGenerator(tmpRandomNumberGenerator);
                                                } else {
                                                    tmpPdbToDpd.setDefaultRotation();
                                                }
                                                // Determine radius of protein
                                                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                                int tmpTotalNumberOfParticlesOfProtein = tmpSpices.getTotalNumberOfParticles();
                                                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                                                double tmpRadiusOfProtein = this.miscUtilityMethods.getRadiusOfParticlesInDpdBox(tmpTotalNumberOfParticlesOfProtein, tmpDensityInfoValueItem.getValueAsDouble());
                                                // Determine non-overlapping spheres for proteins
                                                LinkedList<BodySphere> tmpSphereList = 
                                                    tmpXyLayer.getNonOverlappingRandomSpheres(
                                                        tmpQuantityInVolume, 
                                                        tmpRadiusOfProtein,
                                                        Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                        tmpRandomNumberGenerator
                                                    );
                                                int tmpFirstCalphaIndex = 1;
                                                for (BodySphere tmpSingleSphere : tmpSphereList) {
                                                    tmpPdbToDpd.setCenter(tmpSingleSphere.getBodyCenter().getX(), tmpSingleSphere.getBodyCenter().getY(), tmpSingleSphere.getBodyCenter().getZ());
                                                    tmpPdbToDpd.setRadius(tmpSingleSphere.getRadius() * ModelDefinitions.DECREASE_FACTOR);
                                                    if (tmpIsProteinRandom3DOrientation) {
                                                        tmpPdbToDpd.setRandomOrientation();
                                                    }
                                                    String[] tmpProteinParticlesConnectionTableArray = tmpPdbToDpd.getCoordinateConnectionTableArray(tmpLineNumber, tmpFirstCalphaIndex);
                                                    for (String tmpLine : tmpProteinParticlesConnectionTableArray) {
                                                        tmpOutputLineList.add(tmpLine);
                                                    }
                                                    // Write separator after molecule
                                                    tmpOutputLineList.add(tmpLineVisualizerSmall);
                                                    tmpLineNumber = tmpPdbToDpd.getLastUsedIndex() + 1;
                                                }
                                                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                                                // </editor-fold>
                                            } else {
                                                // <editor-fold defaultstate="collapsed" desc="No protein data">
                                                // <editor-fold defaultstate="collapsed" desc="Write comment in file">
                                                tmpOutputLineList.add(tmpLineVisualizerLarge);
                                                tmpOutputLineList.add("# Compartment xy-Layer");
                                                tmpOutputLineList.add(tmpLineVisualizerLarge);
                                                tmpOutputLineList.add("# Compartment xy-Layer Molecule Information:");
                                                tmpOutputLineList.add("# Molecule Name             = " + tmpMoleculeName);
                                                // Old code:
                                                // tmpOutputLineList.add("# Molecular Structure       = " + this.whitespacePattern.matcher(tmpMolecularStructureString).replaceAll(""));
                                                tmpOutputLineList.add("# Molecular Structure       = " + StringUtils.deleteWhitespace(tmpMolecularStructureString));
                                                tmpOutputLineList.add("# Particles per Molecule    = " + String.valueOf(tmpNumberOfParticlesPerMolecule));
                                                tmpOutputLineList.add("# Quantity in Volume        = " + String.valueOf(tmpQuantityInVolume));
                                                tmpOutputLineList.add("# Quantity on Surface       = " + String.valueOf(tmpQuantityOnSurface));
                                                tmpOutputLineList.add("# Orientation               = " + tmpChemicalCompositionValueItem.getValue(i, 7));
                                                tmpOutputLineList.add(tmpLineVisualizerSmall);
                                                // </editor-fold>
                                                PointInSpace[] tmpFirstParticleCoordinates = new PointInSpace[tmpQuantityInVolume + tmpQuantityOnSurface];
                                                PointInSpace[] tmpLastParticleCoordinates = new PointInSpace[tmpQuantityInVolume + tmpQuantityOnSurface];
                                                // Center of xy-layer
                                                double tmpXyLayerCenterXCoordinate = tmpXyLayer.getBodyCenter().getX();
                                                double tmpXyLayerCenterYCoordinate = tmpXyLayer.getBodyCenter().getY();
                                                double tmpXyLayerCenterZCoordinate = tmpXyLayer.getBodyCenter().getZ();
                                                double tmpHalfXLength = tmpXyLayer.getXLength() / 2.0;
                                                double tmpHalfYLength = tmpXyLayer.getYLength() / 2.0;
                                                double tmpHalfZLength = tmpXyLayer.getZLength() / 2.0;
                                                double tmpOffsetX = tmpXyLayerCenterXCoordinate;
                                                double tmpOffsetY = tmpXyLayerCenterYCoordinate;
                                                double tmpOffsetZ = tmpXyLayerCenterZCoordinate;
                                                if (tmpQuantityInVolume > 0) {
                                                    if (tmpIsSingleParticleMolecule) {
                                                        tmpXyLayer.fillRandomPointsInVolumeWithExcludingSpheres(
                                                            tmpFirstParticleCoordinates, 
                                                            0, 
                                                            tmpQuantityInVolume,
                                                            Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                            tmpRandomNumberGenerator
                                                        );
                                                        tmpLastParticleCoordinates = tmpFirstParticleCoordinates;
                                                    } else {
                                                        if (tmpIsMoleculeStartGeometryCompressedToSinglePoint) {
                                                            tmpLastParticleCoordinates = tmpFirstParticleCoordinates;
                                                        } else {
                                                            tmpXyLayer.fillRandomPointsInVolumeWithExcludingSpheres(
                                                                tmpFirstParticleCoordinates, 
                                                                tmpLastParticleCoordinates, 
                                                                0, 
                                                                tmpQuantityInVolume,
                                                                tmpBondLength, 
                                                                Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                                                tmpRandomNumberGenerator
                                                            );
                                                        }
                                                    }
                                                }
                                                if (tmpQuantityOnSurface > 0) {
                                                    if (this.graphicsUtilityMethods.isAllSurfacesGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                        tmpXyLayer.fillRandomPointsOnAllSurfaces(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                        GraphicalParticlePosition tmpCenterOfXyLayer = new GraphicalParticlePosition(
                                                                tmpXyLayer.getBodyCenter().getX(), tmpXyLayer.getBodyCenter().getY(), tmpXyLayer.getBodyCenter().getZ());
                                                        Arrays.fill(tmpLastParticleCoordinates, tmpQuantityInVolume, tmpQuantityInVolume + tmpQuantityOnSurface, tmpCenterOfXyLayer);
                                                    } else if (this.graphicsUtilityMethods.isSingleSurfaceGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                        // <editor-fold defaultstate="collapsed" desc="Single surface geometry">
                                                        if (this.graphicsUtilityMethods.isSingleSurfaceXyTopGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.XY_TOP;
                                                            tmpOffsetZ = tmpXyLayerCenterZCoordinate - tmpHalfZLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityInVolume + tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new PointInSpace(tmpFirstParticleCoordinates[j].getX(), tmpFirstParticleCoordinates[j].getY(), tmpOffsetZ);
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceXyBottomGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.XY_BOTTOM;
                                                            tmpOffsetZ = tmpXyLayerCenterZCoordinate + tmpHalfZLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityInVolume + tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new PointInSpace(tmpFirstParticleCoordinates[j].getX(), tmpFirstParticleCoordinates[j].getY(), tmpOffsetZ);
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceYzLeftGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.YZ_LEFT;
                                                            tmpOffsetX = tmpXyLayerCenterXCoordinate + tmpHalfXLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityInVolume + tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new PointInSpace(tmpOffsetX, tmpFirstParticleCoordinates[j].getY(), tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceYzRightGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.YZ_RIGHT;
                                                            tmpOffsetX = tmpXyLayerCenterXCoordinate - tmpHalfXLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityInVolume + tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new PointInSpace(tmpOffsetX, tmpFirstParticleCoordinates[j].getY(), tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceXzFrontGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.XZ_FRONT;
                                                            tmpOffsetY = tmpXyLayerCenterYCoordinate + tmpHalfYLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityInVolume + tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new PointInSpace(tmpFirstParticleCoordinates[j].getX(), tmpOffsetY, tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isSingleSurfaceXzBackGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            BodyXyLayerSingleSurfaceEnum tmpSingleSurface = BodyXyLayerSingleSurfaceEnum.XZ_BACK;
                                                            tmpOffsetY = tmpXyLayerCenterYCoordinate - tmpHalfYLength;
                                                            tmpXyLayer.fillRandomPointsOnSingleSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpSingleSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityInVolume + tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new PointInSpace(tmpFirstParticleCoordinates[j].getX(), tmpOffsetY, tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        }
                                                        // </editor-fold>
                                                    } else {
                                                        // <editor-fold defaultstate="collapsed" desc="xy top and bottom surface geometry">
                                                        if (this.graphicsUtilityMethods.isXyTopBottomGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            tmpXyLayer.fillRandomPointsOnTopBottomSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityInVolume + tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new PointInSpace(tmpFirstParticleCoordinates[j].getX(), tmpFirstParticleCoordinates[j].getY(), tmpOffsetZ);
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isYzLeftRightGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            tmpXyLayer.fillRandomPointsOnLeftRightSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityInVolume + tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new PointInSpace(tmpOffsetX, tmpFirstParticleCoordinates[j].getY(), tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        } else if (this.graphicsUtilityMethods.isXzFrontBackGeometryInXyLayer(tmpChemicalCompositionValueItem, i)) {
                                                            tmpXyLayer.fillRandomPointsOnFrontBackSurface(tmpFirstParticleCoordinates, tmpQuantityInVolume, tmpQuantityOnSurface, tmpRandomNumberGenerator);
                                                            for (int j = tmpQuantityInVolume; j < tmpQuantityInVolume + tmpQuantityOnSurface; j++) {
                                                                tmpLastParticleCoordinates[j] = new PointInSpace(tmpFirstParticleCoordinates[j].getX(), tmpOffsetY, tmpFirstParticleCoordinates[j].getZ());
                                                            }
                                                        }
                                                        // </editor-fold>
                                                    }
                                                }
                                                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                                                tmpSpices.setCoordinates(tmpLineNumber, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
                                                String[][] tmpParticlePositionsAndConnections = tmpSpices.getParticlePositionsAndConnections();
                                                tmpSpices.destroySpicesMatrix();
                                                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);

                                                int tmpParticleCounter = 0;
                                                for (int j = 0; j < tmpParticlePositionsAndConnections.length; j++) {
                                                    tmpBuffer.delete(0, tmpBuffer.length());
                                                    tmpBuffer.append(tmpParticlePositionsAndConnections[j][0]); // Line_number
                                                    tmpBuffer.append(tmpOneSpace);
                                                    tmpBuffer.append(tmpParticlePositionsAndConnections[j][1]); // Particle
                                                    // Add particle index for inner molecule potentials: Index 0 (zero) means no index potential
                                                    tmpBuffer.append(tmpOneSpace);
                                                    tmpBuffer.append(tmpParticlePositionsAndConnections[j][2]); // Particle_Index
                                                    for (int k = 3; k < 6; k++) {
                                                        // Coordinates
                                                        tmpBuffer.append(tmpOneSpace);
                                                        tmpBuffer.append(this.stringUtilityMethods.formatDoubleValue(tmpParticlePositionsAndConnections[j][k], ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES));
                                                    }
                                                    for (int k = 6; k < tmpParticlePositionsAndConnections[j].length; k++) {
                                                        // Bonding
                                                        tmpBuffer.append(tmpOneSpace);
                                                        tmpBuffer.append(tmpParticlePositionsAndConnections[j][k]);
                                                    }
                                                    // Write single particle information
                                                    tmpOutputLineList.add(tmpBuffer.toString());
                                                    tmpParticleCounter++;
                                                    if (tmpParticleCounter == tmpNumberOfParticlesPerMolecule) {
                                                        // Write GENERAL_SEPARATOR after molecule
                                                        tmpOutputLineList.add(tmpLineVisualizerSmall);
                                                        tmpParticleCounter = 0;
                                                    }
                                                }
                                                tmpLineNumber += tmpParticlePositionsAndConnections.length;
                                                // </editor-fold>
                                            }
                                        }
                                        // </editor-fold>
                                    }
                                    // </editor-fold>
                                    break;
                                }
                            }
                            // </editor-fold>
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Unknown body">
                        default:
                            // Unknown body type
                            ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): Unknown body type.");
                            return false;
                        // </editor-fold>                        // </editor-fold>
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Write coordinates of molecules in bulk">
            ValueItem tmpBulkInfoValueItem = tmpCompartmentContainer.getBulkInfoValueItem();
            if (tmpBulkInfoValueItem == null) {
                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): tmpBulkInfoValueItem == null.");
                return false;
            }
            // Set NEW list for possible spheres in bulk
            tmpCompartmentContainer.getCompartmentBox().setExcludedSphereList(new LinkedList<BodySphere>());
            for (int i = 0; i < tmpBulkInfoValueItem.getMatrixRowCount(); i++) {
                // tmpBulkInfoValueItem column 0 : Molecule name
                if (tmpBulkInfoValueItem.getValue(i, 0).equals(aMoleculeName)) {
                    // <editor-fold defaultstate="collapsed" desc="Set molecule information">
                    // Molecule name
                    String tmpMoleculeName = tmpBulkInfoValueItem.getValue(i, 0);
                    // Molecular structure
                    String tmpMolecularStructureString = tmpBulkInfoValueItem.getValue(i, 1);
                    boolean tmpIsSingleParticleMolecule = this.particlePattern.matcher(tmpMolecularStructureString).matches();
                    String tmpProteinData = "";
                    if (tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                        tmpProteinData = tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                    }
                    // Quantity
                    int tmpQuantity = tmpBulkInfoValueItem.getValueAsInt(i, 3);
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Write particle information">
                    if (tmpQuantity > 0) {
                        if (this.graphicsUtilityMethods.isProtein3dStructureInBulk(tmpBulkInfoValueItem, i)) {
                            // <editor-fold defaultstate="collapsed" desc="Protein data are used for 3D structure in bulk">
                            if (tmpProteinData.isEmpty()) {
                                ModelUtils.appendToLogfile(true, "UtilityJobMethods.writeJdpdPositionsBondsFile(): BULK: tmpProteinData.isEmpty()");
                                return false;
                            }
                            // <editor-fold defaultstate="collapsed" desc="Write comment in file">
                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                            tmpOutputLineList.add("# Bulk with Molecules in excluded Spheres");
                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                            tmpOutputLineList.add("# Bulk Molecule Information:");
                            tmpOutputLineList.add("# Molecule Name             = " + tmpMoleculeName);
                            // Old code:
                            // tmpOutputLineList.add("# Molecular Structure       = " + this.whitespacePattern.matcher(tmpMolecularStructureString).replaceAll(""));
                            tmpOutputLineList.add("# Molecular Structure       = " + StringUtils.deleteWhitespace(tmpMolecularStructureString));
                            tmpOutputLineList.add("# Particles per Molecule    = " + String.valueOf(tmpNumberOfParticlesPerMolecule));
                            tmpOutputLineList.add("# Quantity in Volume        = " + String.valueOf(tmpQuantity));
                            tmpOutputLineList.add("# Orientation               = " + tmpBulkInfoValueItem.getValue(i, 4));
                            tmpOutputLineList.add(tmpLineVisualizerSmall);
                            // </editor-fold>
                            // Amino acids MUST already be initialized
                            // Initialize tmpPdbToDpd
                            PdbToDpd tmpPdbToDpd = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                            tmpAreProteinBackboneForcesActivated = true;
                            boolean tmpIsProteinRandom3DOrientation = false;
                            if (this.graphicsUtilityMethods.isRandom3dStructureGeometryInBulk(tmpBulkInfoValueItem, i)) {
                                tmpIsProteinRandom3DOrientation = true;
                                tmpPdbToDpd.setSeed(tmpRandomSeed);
                                tmpPdbToDpd.setRandomNumberGenerator(tmpRandomNumberGenerator);
                            } else {
                                tmpPdbToDpd.setDefaultRotation();
                            }
                            // Determine radius of protein
                            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                            int tmpTotalNumberOfParticlesOfProtein = tmpSpices.getTotalNumberOfParticles();
                            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                            double tmpRadiusOfProtein = this.miscUtilityMethods.getRadiusOfParticlesInDpdBox(tmpTotalNumberOfParticlesOfProtein, tmpDensityInfoValueItem.getValueAsDouble());
                            // Determine non-overlapping spheres for proteins
                            LinkedList<BodySphere> tmpNonOverlappingSphereList = 
                                tmpCompartmentContainer.getCompartmentBox().getNonOverlappingRandomSpheres(
                                    tmpQuantity, 
                                    tmpRadiusOfProtein,
                                    Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                    tmpRandomNumberGenerator
                                );
                            int tmpFirstCalphaIndex = 1;
                            for (BodySphere tmpSingleSphere : tmpNonOverlappingSphereList) {
                                tmpPdbToDpd.setCenter(tmpSingleSphere.getBodyCenter().getX(), tmpSingleSphere.getBodyCenter().getY(), tmpSingleSphere.getBodyCenter().getZ());
                                tmpPdbToDpd.setRadius(tmpSingleSphere.getRadius() * ModelDefinitions.DECREASE_FACTOR);
                                if (tmpIsProteinRandom3DOrientation) {
                                    tmpPdbToDpd.setRandomOrientation();
                                }
                                String[] tmpProteinParticlesConnectionTableArray = tmpPdbToDpd.getCoordinateConnectionTableArray(tmpLineNumber, tmpFirstCalphaIndex);
                                for (String tmpLine : tmpProteinParticlesConnectionTableArray) {
                                    tmpOutputLineList.add(tmpLine);
                                }
                                // Write separator after molecule
                                tmpOutputLineList.add(tmpLineVisualizerSmall);
                                tmpLineNumber = tmpPdbToDpd.getLastUsedIndex() + 1;
                            }
                            PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDpd, tmpProteinData);
                            // </editor-fold>
                        } else {
                            // <editor-fold defaultstate="collapsed" desc="No protein data">
                            // <editor-fold defaultstate="collapsed" desc="Write comment in file">
                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                            tmpOutputLineList.add("# Bulk");
                            tmpOutputLineList.add(tmpLineVisualizerLarge);
                            tmpOutputLineList.add("# Bulk Molecule Information:");
                            tmpOutputLineList.add("# Molecule Name             = " + tmpMoleculeName);
                            // Old code:
                            // tmpOutputLineList.add("# Molecular Structure       = " + this.whitespacePattern.matcher(tmpMolecularStructureString).replaceAll(""));
                            tmpOutputLineList.add("# Molecular Structure       = " + StringUtils.deleteWhitespace(tmpMolecularStructureString));
                            tmpOutputLineList.add("# Particles per Molecule    = " + String.valueOf(tmpNumberOfParticlesPerMolecule));
                            tmpOutputLineList.add("# Quantity in Bulk          = " + String.valueOf(tmpQuantity));
                            tmpOutputLineList.add("# Orientation               = " + tmpBulkInfoValueItem.getValue(i, 4));
                            tmpOutputLineList.add(tmpLineVisualizerSmall);
                            // </editor-fold>
                            PointInSpace[] tmpFirstParticleCoordinates = new PointInSpace[tmpQuantity];
                            tmpCompartmentContainer.getCompartmentBox().fillFreeVolumeRandomPoints(
                                tmpFirstParticleCoordinates, 
                                0, 
                                tmpQuantity, 
                                Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                tmpRandomNumberGenerator
                            );
                            PointInSpace[] tmpLastParticleCoordinates;
                            if (tmpIsSingleParticleMolecule) {
                                tmpLastParticleCoordinates = tmpFirstParticleCoordinates;
                            } else {
                                if (tmpIsMoleculeStartGeometryCompressedToSinglePoint) {
                                    tmpLastParticleCoordinates = tmpFirstParticleCoordinates;
                                } else {
                                    tmpLastParticleCoordinates = new PointInSpace[tmpQuantity];
                                    tmpCompartmentContainer.getCompartmentBox().fillFreeVolumeRandomPoints(
                                        tmpLastParticleCoordinates, 
                                        0, 
                                        tmpQuantity, 
                                        Preferences.getInstance().getNumberOfTrialsForCompartment(),
                                        tmpRandomNumberGenerator
                                    );
                                }
                            }
                            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMolecularStructureString);
                            tmpSpices.setCoordinates(tmpLineNumber, tmpFirstParticleCoordinates, tmpLastParticleCoordinates, tmpBondLength);
                            String[][] tmpParticlePositionsAndConnections = tmpSpices.getParticlePositionsAndConnections();
                            String[][] tmpOneMoleculeParticlePositionsAndConnections = new String[tmpNumberOfParticlesPerMolecule][];
                            PointInSpace[] tmpOneMoleculeParticleCoordinates = new PointInSpace[tmpNumberOfParticlesPerMolecule];
                            int tmpParticleCounter = 0;
                            for (int j = 0; j < tmpParticlePositionsAndConnections.length; j++) {
                                tmpParticleCounter++;
                                // <editor-fold defaultstate="collapsed" desc="Accumulate one molecule">
                                if (tmpParticleCounter <= tmpNumberOfParticlesPerMolecule) {
                                    tmpOneMoleculeParticlePositionsAndConnections[tmpParticleCounter - 1] = tmpParticlePositionsAndConnections[j];
                                    tmpOneMoleculeParticleCoordinates[tmpParticleCounter - 1] = new PointInSpace( // Parameters:
                                            Double.valueOf(tmpParticlePositionsAndConnections[j][3]), // x-Coordinate
                                            Double.valueOf(tmpParticlePositionsAndConnections[j][4]), // y-Coordinate
                                            Double.valueOf(tmpParticlePositionsAndConnections[j][5])); // z-Coordinate
                                }

                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="Write one molecule particle information">
                                if (tmpParticleCounter == tmpNumberOfParticlesPerMolecule) {
                                    tmpParticleCounter = 0;
                                    // NOTE: Particle in bulk is not allowed to be in compartment: Correct errors
                                    // <editor-fold defaultstate="collapsed" desc="Correct coordinates">
                                    PointInSpace[] tmpCorrectCoordinates = tmpOneMoleculeParticleCoordinates;
                                    boolean tmpIsCorrect = false;
                                    while (!tmpIsCorrect) {
                                        // <editor-fold defaultstate="collapsed" desc="Check coordinates">
                                        int tmpCorrectIndex = 0;
                                        for (int k = 0; k < tmpCorrectCoordinates.length; k++) {
                                            if (!tmpCompartmentContainer.getCompartmentBox().isInFreeVolume(tmpCorrectCoordinates[k])) {
                                                break;
                                            } else {
                                                tmpCorrectIndex = k;
                                            }
                                        }
                                        tmpIsCorrect = tmpCorrectIndex == tmpCorrectCoordinates.length - 1;
                                        // </editor-fold>
                                        // <editor-fold defaultstate="collapsed" desc="Correct if necessary">
                                        if (!tmpIsCorrect) {
                                            if (tmpCorrectIndex > 0) {
                                                // <editor-fold defaultstate="collapsed" desc="Shrink molecule">
                                                // NOTE: tmpCorrectCoordinates[0] MUST be correct since it is deduced from tmpFirstParticleCoordinates which are all in free volume by definition
                                                PointInSpace[][] tmpNewCoordinates = tmpSpices.getParticleCoordinates(tmpCorrectCoordinates[0], tmpCorrectCoordinates[tmpCorrectIndex],
                                                        tmpBondLength);

                                                tmpCorrectCoordinates = tmpNewCoordinates[0];
                                                tmpIsCorrect = true;
                                                // </editor-fold>
                                            } else {
                                                // <editor-fold defaultstate="collapsed" desc="Get other orientation">
                                                PointInSpace tmpNewLastParticleCoordinate = tmpCompartmentContainer.getCompartmentBox().getRandomPointInFreeVolume(tmpRandomNumberGenerator);
                                                // NOTE: tmpCorrectCoordinates[0] MUST be correct since it is deduced from tmpFirstParticleCoordinates which are all in free volume by definition
                                                PointInSpace[][] tmpNewCoordinates = tmpSpices.getParticleCoordinates(tmpCorrectCoordinates[0], tmpNewLastParticleCoordinate, tmpBondLength);
                                                tmpCorrectCoordinates = tmpNewCoordinates[0];
                                                // </editor-fold>
                                            }
                                        }
                                        // </editor-fold>
                                    }
                                    // </editor-fold>
                                    for (int k = 0; k < tmpOneMoleculeParticlePositionsAndConnections.length; k++) {
                                        tmpBuffer.delete(0, tmpBuffer.length());
                                        tmpBuffer.append(tmpOneMoleculeParticlePositionsAndConnections[k][0]); // Line_number
                                        tmpBuffer.append(tmpOneSpace);
                                        tmpBuffer.append(tmpOneMoleculeParticlePositionsAndConnections[k][1]); // Particle
                                        // Add particle index for inner molecule potentials: Index 0 (zero) means no index potential
                                        tmpBuffer.append(tmpOneSpace);
                                        tmpBuffer.append(tmpOneMoleculeParticlePositionsAndConnections[k][2]); // Particle_Index
                                        tmpBuffer.append(tmpOneSpace);
                                        tmpBuffer.append(this.stringUtilityMethods.formatDoubleValue(tmpCorrectCoordinates[k].getX(), ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES));
                                        tmpBuffer.append(tmpOneSpace);
                                        tmpBuffer.append(this.stringUtilityMethods.formatDoubleValue(tmpCorrectCoordinates[k].getY(), ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES));
                                        tmpBuffer.append(tmpOneSpace);
                                        tmpBuffer.append(this.stringUtilityMethods.formatDoubleValue(tmpCorrectCoordinates[k].getZ(), ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES));
                                        for (int u = 6; u < tmpOneMoleculeParticlePositionsAndConnections[k].length; u++) {
                                            // Bonding
                                            tmpBuffer.append(tmpOneSpace);
                                            tmpBuffer.append(tmpOneMoleculeParticlePositionsAndConnections[k][u]);
                                        }
                                        // Write single particle information
                                        tmpOutputLineList.add(tmpBuffer.toString());
                                    }
                                    // Write GENERAL_SEPARATOR after molecule
                                    tmpOutputLineList.add(tmpLineVisualizerSmall);
                                }
                                // </editor-fold>
                            }
                            tmpSpices.destroySpicesMatrix();
                            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
                            // </editor-fold>
                        }
                    }
                    // </editor-fold>
                    break;
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Write protein distance forces if available">
            for (int i = 0; i < tmpBulkInfoValueItem.getMatrixRowCount(); i++) {
                // tmpBulkInfoValueItem column 0 : Molecule name
                if (tmpBulkInfoValueItem.getValue(i, 0).equals(aMoleculeName)) {
                    // <editor-fold defaultstate="collapsed" desc="Set molecule information">
                    // Molecule name
                    String tmpMoleculeName = tmpBulkInfoValueItem.getValue(i, 0);
                    // Protein data
                    String tmpProteinData = "";
                    if (tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                        tmpProteinData = tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Write distance force information">
                    if (tmpAreProteinBackboneForcesActivated && !tmpProteinData.isEmpty()) {
                        // NOTE: Amino acids are already initialised!
                        // <editor-fold defaultstate="collapsed" desc="Initialize tmpPdbToDPD">
                        PdbToDpd tmpPdbToDPD = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                        double tmpLengthConversionFactorFromPhysicalLengthToDPD = 1.0 / this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
                        // Set number of decimals for coordinates generated by PdbToDpd
                        tmpPdbToDPD.setNumberOfDecimalsForParameters(ModelDefinitions.NUMBER_OF_DECIMALS_FOR_PDBTODPD_PARAMETERS);
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Write protein distance force information">
                        ValueItem tmpProteinDistanceForcesValueItem = aJobInputValueItemContainer.getValueItem("ProteinDistanceForces");
                        if (tmpProteinDistanceForcesValueItem != null && !tmpProteinDistanceForcesValueItem.isLocked() && tmpProteinDistanceForcesValueItem.isActive()) {
                            int tmpNumberOfDistanceForcesOfSpecificProtein = this.getNumberOfSpecificProteinDistanceForces(tmpMoleculeName, tmpProteinData, tmpProteinDistanceForcesValueItem);
                            if (tmpNumberOfDistanceForcesOfSpecificProtein > 0) {
                                tmpOutputLineList.add(tmpLineVisualizerLarge);
                                tmpOutputLineList.add("# Protein distance forces for " + tmpMoleculeName);
                                tmpOutputLineList.add("# Number of protein distance forces = " + String.valueOf(tmpNumberOfDistanceForcesOfSpecificProtein));
                                tmpOutputLineList.add("# Maximum distance type = " + String.valueOf(tmpPdbToDPD.getMaxDistanceTypeOfProteinDistanceForces()));
                                tmpOutputLineList.add(tmpLineVisualizerLarge);
                                tmpOutputLineList.add(ModelDefinitions.JDPD_BACKBONE_BOND_NUMBER + String.valueOf(tmpNumberOfDistanceForcesOfSpecificProtein));
                                // Get column with individual protein information
                                int tmpIndexOfProteinColumn = this.getSpecificProteinBackboneForcesColumnIndex(tmpMoleculeName, tmpProteinDistanceForcesValueItem);
                                for (int k = 0; k < tmpProteinDistanceForcesValueItem.getMatrixRowCount(); k++) {
                                    // NOTE: 1-i distance has i = 2 for distance type = 1 etc.
                                    int tmpDistanceType = tmpProteinDistanceForcesValueItem.getValueAsInt(k, 0) - 1;
                                    if (tmpProteinDistanceForcesValueItem.getValueAsDouble(k, tmpIndexOfProteinColumn) > 0.0) {
                                        String[] tmpBackboneBackboneForceDistanceLineArray = 
                                            tmpPdbToDPD.getProteinDistanceForcesLineArray(
                                                tmpDistanceType, 
                                                tmpLengthConversionFactorFromPhysicalLengthToDPD,
                                                tmpProteinDistanceForcesValueItem.getValueAsDouble(k, tmpIndexOfProteinColumn)
                                            );
                                        if (tmpBackboneBackboneForceDistanceLineArray == null) {
                                            // Distance is not available: Exit for-loop
                                            break;
                                        } else {
                                            tmpOutputLineList.add("# Distance type = " + String.valueOf(tmpDistanceType));
                                            for (String tmpLine : tmpBackboneBackboneForceDistanceLineArray) {
                                                tmpOutputLineList.add(tmpLine);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDPD, tmpProteinData);
                        // </editor-fold>
                    }
                    // </editor-fold>
                    break;
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Transform protein distance force indices if necessary">
            for (int i = 0; i < tmpBulkInfoValueItem.getMatrixRowCount(); i++) {
                // tmpBulkInfoValueItem column 0 : Molecule name
                if (tmpBulkInfoValueItem.getValue(i, 0).equals(aMoleculeName)) {
                    // <editor-fold defaultstate="collapsed" desc="Transform if necessary">
                    String tmpProteinData = "";
                    if (tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                        tmpProteinData = tmpBulkInfoValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                        // NOTE: Amino acids are already initialised!
                        PdbToDpd tmpPdbToDPD = PdbToDpdPool.getInstance().getPdbToDpd(tmpProteinData);
                        int tmpNumberOfBackboneParticles = tmpPdbToDPD.getNumberOfBackboneParticles();
                        PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDPD, tmpProteinData);
                        tmpOutputLineList = this.transformProteinBackboneForceIndices(tmpOutputLineList, tmpNumberOfBackboneParticles);
                    }
                    // </editor-fold>
                    break;
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Write protein backbone forces if available">
            ValueItem tmpProteinBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("ProteinBackboneForces");
            if (tmpProteinBackboneForcesValueItem != null
                    && !tmpProteinBackboneForcesValueItem.isLocked()
                     && tmpProteinBackboneForcesValueItem.isActive()) {
                // Check if aMoleculeName occurs in tmpProteinBackboneForcesValueItem
                int tmpNumberOfProteinBackboneForces = this.getNumberOfSpecificProteinBackboneForces(aMoleculeName, tmpProteinBackboneForcesValueItem);
                if (tmpNumberOfProteinBackboneForces > 0) {
                    tmpOutputLineList.add(tmpLineVisualizerLarge);
                    tmpOutputLineList.add("# Protein backbone forces for " + aMoleculeName);
                    tmpOutputLineList.add("# Number of protein backbone forces = " + String.valueOf(tmpNumberOfProteinBackboneForces));
                    tmpOutputLineList.add(tmpLineVisualizerLarge);
                    tmpOutputLineList = this.correctNumberOfBackboneDistances(tmpOutputLineList, tmpNumberOfProteinBackboneForces);
                    // Protein backbone force value item columns:
                    // Index 0 = Protein name
                    // Index 1 = Amino acid backbone particle 1
                    // Index 2 = Amino acid backbone particle 2
                    // Index 3 = Backbone distance in Angstrom
                    // Index 4 = Backbone distance in DPD unit
                    // Index 5 = Backbone force constant
                    // Index 6 = Backbone behaviour
                    for (int i = 0; i < tmpProteinBackboneForcesValueItem.getMatrixRowCount(); i++) {
                        if (tmpProteinBackboneForcesValueItem.getValue(i, 0).equals(aMoleculeName)
                                && tmpProteinBackboneForcesValueItem.getValueAsDouble(i, 4) > 0.0
                                && tmpProteinBackboneForcesValueItem.getValueAsDouble(i, 5) > 0.0) {
                            if (tmpProteinBackboneForcesValueItem.getMatrixColumnCount() == 6) {
                                // <editor-fold defaultstate="collapsed" desc="Legacy code for old job inputs without definition of backbone behaviour">
                                tmpOutputLineList.add(
                                    this.stringUtilityMethods.getFirstToken(tmpProteinBackboneForcesValueItem.getValue(i, 1)) + 
                                    tmpOneSpace +
                                    this.stringUtilityMethods.getFirstToken(tmpProteinBackboneForcesValueItem.getValue(i, 2)) + 
                                    tmpOneSpace +
                                    tmpProteinBackboneForcesValueItem.getValue(i, 4) + 
                                    tmpOneSpace +
                                    tmpProteinBackboneForcesValueItem.getValue(i, 5)
                                );
                                // </editor-fold>
                            } else {
                                tmpOutputLineList.add(
                                    this.stringUtilityMethods.getFirstToken(tmpProteinBackboneForcesValueItem.getValue(i, 1)) + 
                                    tmpOneSpace +
                                    this.stringUtilityMethods.getFirstToken(tmpProteinBackboneForcesValueItem.getValue(i, 2)) + 
                                    tmpOneSpace +
                                    tmpProteinBackboneForcesValueItem.getValue(i, 4) + 
                                    tmpOneSpace +
                                    tmpProteinBackboneForcesValueItem.getValue(i, 5) + 
                                    tmpOneSpace +
                                    tmpProteinBackboneForcesValueItem.getValue(i, 6)
                                );
                            }
                        }
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Write Molecule backbone forces information if available">
            ValueItem tmpMoleculeBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("MoleculeBackboneForces");
            if (tmpMoleculeBackboneForcesValueItem != null
                    && !tmpMoleculeBackboneForcesValueItem.isLocked()
                     && tmpMoleculeBackboneForcesValueItem.isActive()) {
                // Check if aMoleculeName occurs in tmpMoleculeBackboneForcesValueItem
                int tmpNumberOfMoleculeBackboneForces = this.getNumberOfSpecificMoleculeBackboneForces(aMoleculeName, tmpMoleculeBackboneForcesValueItem);
                if (tmpNumberOfMoleculeBackboneForces > 0) {
                    tmpOutputLineList.add(tmpLineVisualizerLarge);
                    tmpOutputLineList.add("# Molecule backbone forces for " + aMoleculeName);
                    tmpOutputLineList.add(tmpLineVisualizerLarge);
                    tmpOutputLineList.add(ModelDefinitions.JDPD_BACKBONE_BOND_NUMBER + String.valueOf(tmpNumberOfMoleculeBackboneForces));
                    // Molecule backbone force value item columns:
                    // Index 0 = Molecule name
                    // Index 1 = Backbone attribute 1
                    // Index 2 = Backbone attribute 2
                    // Index 3 = Backbone distance in Angstrom
                    // Index 4 = Backbone distance in DPD unit
                    // Index 5 = Backbone force constant
                    // Index 6 = Backbone behaviour
                    for (int i = 0; i < tmpMoleculeBackboneForcesValueItem.getMatrixRowCount(); i++) {
                        if (tmpMoleculeBackboneForcesValueItem.getValue(i, 0).equals(aMoleculeName)
                                && tmpMoleculeBackboneForcesValueItem.getValueAsDouble(i, 4) > 0.0
                                && tmpMoleculeBackboneForcesValueItem.getValueAsDouble(i, 5) > 0.0) {
                            if (tmpMoleculeBackboneForcesValueItem.getMatrixColumnCount() == 6) {
                                // <editor-fold defaultstate="collapsed" desc="Legacy code for old job inputs without definition of backbone behaviour">
                                tmpOutputLineList.add(
                                    tmpMoleculeBackboneForcesValueItem.getValue(i, 1) + 
                                    tmpOneSpace +
                                    tmpMoleculeBackboneForcesValueItem.getValue(i, 2) + 
                                    tmpOneSpace +
                                    tmpMoleculeBackboneForcesValueItem.getValue(i, 4) + 
                                    tmpOneSpace +
                                    tmpMoleculeBackboneForcesValueItem.getValue(i, 5)
                                );
                                // </editor-fold>
                            } else {
                                tmpOutputLineList.add(
                                    tmpMoleculeBackboneForcesValueItem.getValue(i, 1) + 
                                    tmpOneSpace +
                                    tmpMoleculeBackboneForcesValueItem.getValue(i, 2) + 
                                    tmpOneSpace +
                                    tmpMoleculeBackboneForcesValueItem.getValue(i, 4) + 
                                    tmpOneSpace +
                                    tmpMoleculeBackboneForcesValueItem.getValue(i, 5) + 
                                    tmpOneSpace +
                                    tmpMoleculeBackboneForcesValueItem.getValue(i, 6)
                                );
                            }
                        }
                    }
                }
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Write tmpOutputLineList to file">
            FileWriter tmpFileWriter = new FileWriter(aJdpdPositionsBondsFilePathname);
            BufferedWriter tmpBufferedWriter = new BufferedWriter(tmpFileWriter, ModelDefinitions.BUFFER_SIZE);
            tmpPrintWriter = new PrintWriter(tmpBufferedWriter);
            for (String tmpOutputLine : tmpOutputLineList) {
                tmpPrintWriter.println(tmpOutputLine);
            }
            tmpPrintWriter.flush();
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return false;
        } finally {
            if (tmpPrintWriter != null) {
                tmpPrintWriter.close();
            }
        }
        return true;
    }

    /**
     * Corrects number of backbone distances in output line list (without checks, see code)
     * 
     * @param aOutputLineList Output line list to be corrected
     * @param aNumberOfProteinBackboneForces Number of protein backbone forces
     * @return Corrected output line list
     */
    private LinkedList<String> correctNumberOfBackboneDistances(LinkedList<String> aOutputLineList, int aNumberOfProteinBackboneForces) {
        LinkedList<String> tmpCorrectedOutputLineList = new LinkedList<>();
        boolean tmpIsCorrected = false;
        for (String tmpSingleOutputLine : aOutputLineList) {
            if (tmpSingleOutputLine.startsWith(ModelDefinitions.JDPD_BACKBONE_BOND_NUMBER)) {
                int tmpOldNumberOfBackboneForces = Integer.valueOf(this.stringUtilityMethods.getLastToken(tmpSingleOutputLine));
                int tmpNewNumberOfBackboneForces = tmpOldNumberOfBackboneForces + aNumberOfProteinBackboneForces;
                tmpCorrectedOutputLineList.add(ModelDefinitions.JDPD_BACKBONE_BOND_NUMBER + String.valueOf(tmpNewNumberOfBackboneForces));
                tmpIsCorrected = true;
            } else {
                tmpCorrectedOutputLineList.add(tmpSingleOutputLine);
            }
        }
        if (!tmpIsCorrected) {
            tmpCorrectedOutputLineList.add(ModelDefinitions.JDPD_BACKBONE_BOND_NUMBER + String.valueOf(aNumberOfProteinBackboneForces));
        }
        return tmpCorrectedOutputLineList;
    }
    
    /**
     * Transforms protein distance force indices in output line list (see code)
     * 
     * @param anOldOutputLineList Output line list to be transformed
     * @param aNumberOfBackboneParticles Number of backbone particles
     * @return Transformed output line list
     */
    private LinkedList<String> transformProteinBackboneForceIndices(LinkedList<String> anOldOutputLineList, int aNumberOfBackboneParticles) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anOldOutputLineList == null || anOldOutputLineList.isEmpty()) {
            return anOldOutputLineList;
        }
        if (aNumberOfBackboneParticles < 1) {
            return anOldOutputLineList;
        }
        // </editor-fold>
        boolean tmpIsDebug = false;
        LinkedList<String> tmpNewOutputLineList = new LinkedList<>();
        HashMap<String, String> tmpOldToNewIndexMap = new HashMap<>(aNumberOfBackboneParticles);
        int tmpCurrentProteinBackboneParticleIndex = 0;
        boolean tmpIsMappingFinished = false;
        for (String tmpOldOutputLine : anOldOutputLineList) {
            if (tmpOldOutputLine.startsWith("#")) {
                // <editor-fold defaultstate="collapsed" desc="Comment line">
                tmpNewOutputLineList.add(tmpOldOutputLine);
                // </editor-fold>
            } else {
                if (!tmpIsMappingFinished) {
                    // <editor-fold defaultstate="collapsed" desc="Replace and set tmpOldIndexToNewIndexMap">
                    String tmpFirstProteinBackboneForceIndex = this.stringUtilityMethods.getFirstProteinBackboneForceIndex(tmpOldOutputLine);
                    if (tmpFirstProteinBackboneForceIndex == null) {
                        tmpNewOutputLineList.add(tmpOldOutputLine);
                    } else {
                        String tmpNewIndex = String.valueOf(++tmpCurrentProteinBackboneParticleIndex);
                        String tmpNewLine;
                        if (!tmpFirstProteinBackboneForceIndex.equals("0")) {
                            tmpOldToNewIndexMap.put(tmpFirstProteinBackboneForceIndex, tmpNewIndex);
                        }
                        tmpNewLine = this.stringUtilityMethods.replaceFirstProteinBackboneForceIndex(tmpOldOutputLine, tmpNewIndex);
                        if (tmpIsDebug) {
                            tmpNewOutputLineList.add(tmpNewLine + "     FROM     " + tmpOldOutputLine);
                        } else {
                            tmpNewOutputLineList.add(tmpNewLine);
                        }
                    }
                    if (tmpCurrentProteinBackboneParticleIndex == aNumberOfBackboneParticles) {
                        tmpCurrentProteinBackboneParticleIndex = 0;
                        tmpIsMappingFinished = true;
                    }
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Replace only">
                    // There are two (!) protein distance force indices possible per line
                    String tmpNewLine = tmpOldOutputLine;
                    for (int i = 0; i < 2; i++) {
                        String tmpFirstProteinBackboneForceIndex = this.stringUtilityMethods.getFirstProteinBackboneForceIndex(tmpNewLine);
                        if (tmpFirstProteinBackboneForceIndex == null) {
                            break;
                        } else {
                            String tmpNewIndex;
                            if (tmpFirstProteinBackboneForceIndex.equals("0")) {
                                tmpNewIndex = String.valueOf(++tmpCurrentProteinBackboneParticleIndex);
                            } else {
                                tmpNewIndex = tmpOldToNewIndexMap.get(tmpFirstProteinBackboneForceIndex);
                                tmpCurrentProteinBackboneParticleIndex++;
                            }
                            if (tmpCurrentProteinBackboneParticleIndex == aNumberOfBackboneParticles) {
                                tmpCurrentProteinBackboneParticleIndex = 0;
                            }
                            tmpNewLine = this.stringUtilityMethods.replaceFirstProteinBackboneForceIndex(tmpNewLine, tmpNewIndex);
                        }
                    }
                    if (tmpIsDebug && !tmpNewLine.equals(tmpOldOutputLine)) {
                        tmpNewOutputLineList.add(tmpNewLine + "     FROM     " + tmpOldOutputLine);
                    } else {
                        tmpNewOutputLineList.add(tmpNewLine);
                    }
                    // </editor-fold>
                }
            }
        }
        return tmpNewOutputLineList;
    }
    
    /**
     * Formats matrix row
     *
     * @param aColumnElements Column elements
     * @param aColumnWidths Column widths that correspond to texts
     * @return Formatted matrix row
     */
    private String formatMatrixRow(String[] aColumnElements, Integer[] aColumnWidths) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aColumnElements == null || aColumnElements.length == 0 || aColumnWidths == null || aColumnWidths.length == 0) {
            return "";
        }

        // </editor-fold>
        StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE_TINY);
        for (int i = 0; i < aColumnElements.length; i++) {
            if (aColumnElements[i] != null && aColumnElements[i].length() > 0) {
                tmpBuffer.append(aColumnElements[i]);
                if (aColumnElements[i].length() < aColumnWidths[i]) {
                    tmpBuffer.append(this.stringUtilityMethods.createStringWithLength(aColumnWidths[i].intValue() - aColumnElements[i].length(), ' '));
                } else {
                    tmpBuffer.append(" ");
                }
            }
        }
        return tmpBuffer.toString();
    }

    /**
     * Returns block start for Jdpd input file
     *
     * @param aHeaderName Header name
     * @return Block start for Jdpd input file or null if no name was
     * specified
     */
    private String getJdpdInputFileBlockStart(String aHeaderName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aHeaderName == null || aHeaderName.isEmpty()) {
            return "";
        }
        // </editor-fold>
        StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE_SMALL);
        String tmpOpticalSeparatorComment = "# ------------------------------------------------------------------------------";
        tmpBuffer.append(tmpOpticalSeparatorComment);
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        tmpBuffer.append("[");
        tmpBuffer.append(aHeaderName);
        tmpBuffer.append("]");
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        tmpBuffer.append(tmpOpticalSeparatorComment);
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        return tmpBuffer.toString();
    }

    /**
     * Returns block end for Jdpd input file
     *
     * @param aHeaderName Header name
     * @return Block end for Jdpd input file or null if no name was
     * specified
     */
    private String getJdpdInputFileBlockEnd(String aHeaderName) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aHeaderName == null || aHeaderName.isEmpty()) {
            return "";
        }
        // </editor-fold>
        StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE_SMALL);
        String tmpOpticalSeparatorComment = "# ------------------------------------------------------------------------------";
        tmpBuffer.append("[/");
        tmpBuffer.append(aHeaderName);
        tmpBuffer.append("]");
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        tmpBuffer.append(tmpOpticalSeparatorComment);
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
        return tmpBuffer.toString();
    }

    /**
     * Creates a string representation of a matrix for the Jdpd input file
     *
     * @param aMatrix Matrix
     * @param aMatrixOutputOmitColumns Matrix output omit columns (may be null)
     * @return A string representation of a matrix item for Jdpd input
     * file or null if string representation can not be created
     */
    private String getStringRepresentationOfMatrix(ValueItemMatrixElement[][] aMatrix, boolean[] aMatrixOutputOmitColumns) {
        return getStringRepresentationOfMatrix(aMatrix, null, aMatrixOutputOmitColumns);
    }

    /**
     * Creates a string representation of a matrix for the Jdpd input file
     *
     * @param aMatrix Matrix
     * @param aMatrixColumnNames Matrix column names (may be null)
     * @param aMatrixOutputOmitColumns Matrix output omit columns (may be null)
     * @return A string representation of a matrix item for Jdpd input
     * file or null if string representation can not be created
     */
    private String getStringRepresentationOfMatrix(ValueItemMatrixElement[][] aMatrix, String[] aMatrixColumnNames, boolean[] aMatrixOutputOmitColumns) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMatrix == null) {
            return null;
        }
        // </editor-fold>
        try {
            StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE_SMALL);
            tmpBuffer.append("TABLE_START");
            tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
            // <editor-fold defaultstate="collapsed" desc="Create column widths">
            LinkedList<Integer> tmpColumnWidthList = new LinkedList<Integer>();
            for (int i = 0; i < aMatrix[0].length; i++) {
                if (aMatrixOutputOmitColumns == null || !aMatrixOutputOmitColumns[i]) {
                    int tmpColumnWidth = 0;
                    if (aMatrixColumnNames != null) {
                        tmpColumnWidth = aMatrixColumnNames[i].length();
                    }
                    for (int k = 0; k < aMatrix.length; k++) {
                        if (aMatrix[k][i].getValue().length() > tmpColumnWidth) {
                            tmpColumnWidth = aMatrix[k][i].getValue().length();
                        }
                    }
                    tmpColumnWidth += 1;
                    tmpColumnWidthList.addLast(tmpColumnWidth);
                }
            }
            Integer[] tmpColumnWidths = null;
            if (tmpColumnWidthList.size() > 0) {
                tmpColumnWidths = tmpColumnWidthList.toArray(new Integer[0]);
            } else {
                return null;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="First create column description if available">
            if (aMatrixColumnNames != null) {
                LinkedList<String> tmpMatrixColumnNameList = new LinkedList<String>();
                for (int i = 0; i < aMatrixColumnNames.length; i++) {
                    if (aMatrixOutputOmitColumns == null || !aMatrixOutputOmitColumns[i]) {
                        tmpMatrixColumnNameList.addLast(aMatrixColumnNames[i]);
                    }
                }
                // IMPORTANT: Column names MUST be uppercase
                tmpBuffer.append(this.formatMatrixRow(this.stringUtilityMethods.replaceSpaceByUnderscore(tmpMatrixColumnNameList.toArray(new String[0])), tmpColumnWidths).toUpperCase(Locale.ENGLISH));
                tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Then create matrix representation">
            for (int i = 0; i < aMatrix.length; i++) {
                LinkedList<String> tmpColumnElementList = new LinkedList<String>();
                for (int k = 0; k < aMatrix[i].length; k++) {
                    if (aMatrixOutputOmitColumns == null || !aMatrixOutputOmitColumns[k]) {
                        tmpColumnElementList.addLast(aMatrix[i][k].getValue());
                    }
                }
                tmpBuffer.append(this.formatMatrixRow(this.stringUtilityMethods.replaceSpaceByUnderscore(tmpColumnElementList.toArray(new String[0])), tmpColumnWidths));
                tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
            }
            tmpBuffer.append("TABLE_END");
            tmpBuffer.append(ModelDefinitions.LINE_SEPARATOR);
            // </editor-fold>
            return tmpBuffer.toString();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }

    /**
     * Returns string representation of value item for Jdpd input file
     *
     * @param aValueItem Value item
     * @return String representation of value item for Jdpd input file
     */
    private String getStringRepresentationOfValueItem(ValueItem aValueItem) {
        if (aValueItem.isActive()) {
            switch (aValueItem.getBasicType()) {
                case SCALAR:
                    // <editor-fold defaultstate="collapsed" desc="SCALAR">
                    switch (aValueItem.getTypeFormat(0).getDataType()) {
                        case NUMERIC:
                        case NUMERIC_NULL:
                        case TIMESTAMP:
                        case TIMESTAMP_EMPTY:
                        case SELECTION_TEXT:
                        case TEXT:
                            return aValueItem.getName() + " " + aValueItem.getValue(0, 0) + ModelDefinitions.LINE_SEPARATOR;
                        default:
                            return "";
                    }
                // </editor-fold>
                case VECTOR:
                    // <editor-fold defaultstate="collapsed" desc="VECTOR">
                    StringBuilder tmpBuffer = new StringBuilder(ModelDefinitions.BUFFER_SIZE_TINY);
                    for (int i = 0; i < aValueItem.getMatrixColumnCount(); i++) {
                        if (aValueItem.getMatrixOutputOmitColumns() == null || !aValueItem.getMatrixOutputOmitColumns()[i]) {
                            tmpBuffer.append(" ");
                            tmpBuffer.append(aValueItem.getValue(0, i));
                        }
                    }
                    return aValueItem.getName() + tmpBuffer.toString() + ModelDefinitions.LINE_SEPARATOR;
                // </editor-fold>
                case MATRIX:
                case FLEXIBLE_MATRIX:
                    // <editor-fold defaultstate="collapsed" desc="MATRIX, FLEXIBLE_MATRIX">
                    return aValueItem.getName() + ModelDefinitions.LINE_SEPARATOR
                            + this.getStringRepresentationOfMatrix(aValueItem.getMatrix(), aValueItem.getMatrixOutputOmitColumns());
                // </editor-fold>
                default:
                    // <editor-fold defaultstate="collapsed" desc="Default">
                    return "";
                // </editor-fold>
            }
        } else {
            return "";
        }
    }
    
    /**
     * Returns value item with split particle pairs in first column of passed 
     * value item.
     * 
     * @param aValueItem Value item (is NOT changed)
     * @return Value item with split particle pairs in first column of passed 
     * value item or null if split could not be performed.
     */
    private ValueItem getSplitParticlePairValueItem(ValueItem aValueItem) {
        if (aValueItem != null) {
            ValueItem tmpSplittedParticlePairValueItem = new ValueItem();
            tmpSplittedParticlePairValueItem.setName(aValueItem.getName());
            tmpSplittedParticlePairValueItem.setBasicType(aValueItem.getBasicType());
            ValueItemMatrixElement[][] tmpMatrix = aValueItem.getMatrix();
            ValueItemMatrixElement[][] tmpNewMatrix = new ValueItemMatrixElement[tmpMatrix.length][];
            for (int i = 0; i < tmpMatrix.length; i++) {
                ValueItemMatrixElement[] tmpNewElements = new ValueItemMatrixElement[tmpMatrix[i].length + 1];
                for (int k = 0; k < tmpMatrix[i].length; k++) {
                    if (k == 0) {
                        String[] tmpParticles = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(tmpMatrix[i][k].getValue());
                        if (tmpParticles == null || tmpParticles.length != 2) {
                            return null;
                        }
                        tmpNewElements[0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpParticles[0], ValueItemEnumDataType.TEXT, false));
                        tmpNewElements[1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(tmpParticles[1], ValueItemEnumDataType.TEXT, false));
                    } else {
                        tmpNewElements[k + 1] = tmpMatrix[i][k];
                    }
                }
                tmpNewMatrix[i] = tmpNewElements;
            }
            tmpSplittedParticlePairValueItem.setMatrix(tmpNewMatrix);
            return tmpSplittedParticlePairValueItem;
        } else {
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Radial Distribution Function (RDF) related methods">
    /**
     * Returns particle pairs from particle-pair RDF calculation value item of
     * Job Input value item container
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return Particle pairs from particle-pair RDF calculation value item of
     * Job Input value item container or null if particle pairs could not be
     * found
     */
    private String[][] getParticlePairsForRdfCalculation(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpParticlePairRdfCalculationValueItem = aJobInputValueItemContainer.getValueItem("ParticlePairRdfCalculation");
        if (tmpParticlePairRdfCalculationValueItem == null || !tmpParticlePairRdfCalculationValueItem.isActive()) {
            return null;
        }
        LinkedList<String[]> tmpParticlePairList = new LinkedList<String[]>();
        for (int i = 0; i < tmpParticlePairRdfCalculationValueItem.getMatrixRowCount(); i++) {
            tmpParticlePairList.add(new String[]{tmpParticlePairRdfCalculationValueItem.getValue(i, 0), tmpParticlePairRdfCalculationValueItem.getValue(i, 1)});
        }
        if (tmpParticlePairList.size() > 0) {
            return tmpParticlePairList.toArray(new String[0][]);
        } else {
            return null;
        }
    }

    /**
     * Returns molecule-particle pairs from molecule-particle-pair RDF
     * calculation value item of Job Input value item container
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return Molecule-particle pairs from molecule-particle-pair RDF
     * calculation value item of Job Input value item container or null if
     * molecule-particle pairs could not be found
     */
    private String[][] getMoleculeParticlePairsForRdfCalculation(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpMoleculeParticlePairRdfCalculationValueItem = aJobInputValueItemContainer.getValueItem("MoleculeParticlePairRdfCalculation");
        if (tmpMoleculeParticlePairRdfCalculationValueItem == null || !tmpMoleculeParticlePairRdfCalculationValueItem.isActive()) {
            return null;
        }
        LinkedList<String[]> tmpMoleculeParticlePairList = new LinkedList<String[]>();
        for (int i = 0; i < tmpMoleculeParticlePairRdfCalculationValueItem.getMatrixRowCount(); i++) {
            tmpMoleculeParticlePairList.add(new String[]{tmpMoleculeParticlePairRdfCalculationValueItem.getValue(i, 0),
                tmpMoleculeParticlePairRdfCalculationValueItem.getValue(i, 1)});
        }
        if (tmpMoleculeParticlePairList.size() > 0) {
            return tmpMoleculeParticlePairList.toArray(new String[0][]);
        } else {
            return null;
        }
    }

    /**
     * Returns molecule-center pairs from molecule-center-pair RDF
     * calculation value item of Job Input value item container
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return Molecule-center pairs from molecule-center-pair RDF
     * calculation value item of Job Input value item container or null if
     * molecule-center pairs could not be found
     */
    private String[][] getMoleculeCenterPairsForRdfCalculation(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return null;
        }
        // </editor-fold>
        ValueItem tmpMoleculeCenterPairRdfCalculationValueItem = aJobInputValueItemContainer.getValueItem("MoleculeCenterPairRdfCalculation");
        if (tmpMoleculeCenterPairRdfCalculationValueItem == null || !tmpMoleculeCenterPairRdfCalculationValueItem.isActive()) {
            return null;
        }
        LinkedList<String[]> tmpMoleculeCenterPairList = new LinkedList<String[]>();
        for (int i = 0; i < tmpMoleculeCenterPairRdfCalculationValueItem.getMatrixRowCount(); i++) {
            tmpMoleculeCenterPairList.add(
                new String[] {
                    tmpMoleculeCenterPairRdfCalculationValueItem.getValue(i, 0),
                    tmpMoleculeCenterPairRdfCalculationValueItem.getValue(i, 1)
                }
            );
        }
        if (tmpMoleculeCenterPairList.size() > 0) {
            return tmpMoleculeCenterPairList.toArray(new String[0][]);
        } else {
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle-particle distance related methods">
    /**
     * Returns defined particle-pair average distances for simulation box
     * described by aJobResultParticlePositionsFilePathname
     *
     * @param aJobResultParticlePositionsFilePathname Full pathname of graphical
     * particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @return Defined particle-pair average distances or null if average
     * distances could not be calculated
     */
    private LinkedList<ParticlePairAverageDistance> getDefinedParticlePairAverageDistances(String aJobResultParticlePositionsFilePathname, ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathname == null || aJobResultParticlePositionsFilePathname.isEmpty() || !(new File(aJobResultParticlePositionsFilePathname)).isFile()) {
            return null;
        }
        if (aJobInputValueItemContainer == null) {
            return null;
        }
        // Get particle pairs for distance calculation
        String[][] tmpParticlePairs = this.getParticlePairsForDistanceCalculation(aJobInputValueItemContainer);
        if (tmpParticlePairs == null) {
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Prepare calculations">
        // Get particle positions (NOTE: Particle positions are already in Angstrom)
        HashMap<String, LinkedList<PointInSpace>> tmpParticleToPositionsMap = this.readParticlePositions(aJobResultParticlePositionsFilePathname, aJobInputValueItemContainer);
        if (tmpParticleToPositionsMap == null) {
            return null;
        }
        // Get length conversion factor for transformation of DPD units to Angstrom
        double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
        // Get size of simulation box
        BoxSizeInfo tmpBoxSizeInfo = this.getBoxSizeInfo(aJobInputValueItemContainer);
        // Transform simulation box volume to Angstrom^3: (xLength * tmpLengthConversionFactor) * (yLength * tmpLengthConversionFactor) * (zLength * tmpLengthConversionFactor)
        double tmpBoxLengthX = tmpBoxSizeInfo.getXLength() * tmpLengthConversionFactor;
        double tmpBoxLengthY = tmpBoxSizeInfo.getYLength() * tmpLengthConversionFactor;
        double tmpBoxLengthZ = tmpBoxSizeInfo.getZLength() * tmpLengthConversionFactor;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate average distance">
        LinkedList<ParticlePairAverageDistance> tmpParticlePairDistanceList = new LinkedList<ParticlePairAverageDistance>();
        DistanceCalculationUtils tmpDistanceCalculationUtils = 
            new DistanceCalculationUtils(
                tmpBoxLengthX, 
                tmpBoxLengthY, 
                tmpBoxLengthZ,
                this.isPeriodicBoundaryX(aJobInputValueItemContainer),
                this.isPeriodicBoundaryY(aJobInputValueItemContainer),
                this.isPeriodicBoundaryZ(aJobInputValueItemContainer)
            );
        for (String[] tmpSingleParticlePair : tmpParticlePairs) {
            double tmpParticlePairAverageDistance;
            if (tmpSingleParticlePair[0].equals(tmpSingleParticlePair[1])) {
                PointInSpace[] tmpParticlePositions = tmpParticleToPositionsMap.get(tmpSingleParticlePair[0]).toArray(new PointInSpace[0]);
                tmpParticlePairAverageDistance = tmpDistanceCalculationUtils.getEqualParticlePairAverageDistance(tmpParticlePositions);
            } else {
                PointInSpace[] tmpParticlePositionsA = tmpParticleToPositionsMap.get(tmpSingleParticlePair[0]).toArray(new PointInSpace[0]);
                PointInSpace[] tmpParticlePositionsB = tmpParticleToPositionsMap.get(tmpSingleParticlePair[1]).toArray(new PointInSpace[0]);
                tmpParticlePairAverageDistance = tmpDistanceCalculationUtils.getDifferentParticlePairAverageDistance(tmpParticlePositionsA, tmpParticlePositionsB);
            }
            if (tmpParticlePairAverageDistance == -1.0) {
                return null;
            } else {
                tmpParticlePairDistanceList.add(new ParticlePairAverageDistance(tmpSingleParticlePair, tmpParticlePairAverageDistance));
            }
        }

        // </editor-fold>
        if (tmpParticlePairDistanceList.size() > 0) {
            return tmpParticlePairDistanceList;
        } else {
            return null;
        }
    }

    /**
     * Creates defined molecule-particle-pair average distances for
     * simulation box described by aJobResultParticlePositionsFilePathname
     *
     * @param aJobResultParticlePositionsFilePathname Full pathname of graphical
     * particle positions file of Job Result
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @return Defined molecule-particle-pair average distances or null if
     * average distances could not be calculated
     */
    private LinkedList<ParticlePairAverageDistance> getDefinedMoleculeParticlePairAverageDistances(String aJobResultParticlePositionsFilePathname, ValueItemContainer aJobInputValueItemContainer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobResultParticlePositionsFilePathname == null || aJobResultParticlePositionsFilePathname.isEmpty() || !(new File(aJobResultParticlePositionsFilePathname)).isFile()) {
            return null;
        }
        if (aJobInputValueItemContainer == null) {
            return null;
        }
        // Get molecule-particle pairs for RDF calculation
        String[][] tmpMoleculeParticlePairs = this.getMoleculeParticlePairsForDistanceCalculation(aJobInputValueItemContainer);
        if (tmpMoleculeParticlePairs == null) {
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Prepare calculations">
        // Get particle positions (NOTE: Particle positions are already in Angstrom)
        HashMap<String, LinkedList<PointInSpace>> tmpMoleculeParticleToPositionsMap = this.readMoleculeParticlePositions(aJobResultParticlePositionsFilePathname, aJobInputValueItemContainer);
        if (tmpMoleculeParticleToPositionsMap == null) {
            return null;
        }
        // Get length conversion factor for transformation of DPD units to Angstrom
        double tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(aJobInputValueItemContainer);
        // Get size of simulation box
        BoxSizeInfo tmpBoxSizeInfo = this.getBoxSizeInfo(aJobInputValueItemContainer);
        // Transform simulation box volume to Angstrom^3: (xLength * tmpLengthConversionFactor) * (yLength * tmpLengthConversionFactor) * (zLength * tmpLengthConversionFactor)
        double tmpBoxLengthX = tmpBoxSizeInfo.getXLength() * tmpLengthConversionFactor;
        double tmpBoxLengthY = tmpBoxSizeInfo.getYLength() * tmpLengthConversionFactor;
        double tmpBoxLengthZ = tmpBoxSizeInfo.getZLength() * tmpLengthConversionFactor;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate average distance">
        LinkedList<ParticlePairAverageDistance> tmpMoleculeParticlePairDistanceList = new LinkedList<ParticlePairAverageDistance>();
        DistanceCalculationUtils tmpDistanceCalculationUtils = new DistanceCalculationUtils(
                tmpBoxLengthX, tmpBoxLengthY, tmpBoxLengthZ,
                this.isPeriodicBoundaryX(aJobInputValueItemContainer),
                this.isPeriodicBoundaryY(aJobInputValueItemContainer),
                this.isPeriodicBoundaryZ(aJobInputValueItemContainer));
        for (String[] tmpSingleMoleculeParticlePair : tmpMoleculeParticlePairs) {
            double tmpParticlePairAverageDistance;
            if (tmpSingleMoleculeParticlePair[0].equals(tmpSingleMoleculeParticlePair[1])) {
                PointInSpace[] tmpMoleculeParticlePositions = tmpMoleculeParticleToPositionsMap.get(tmpSingleMoleculeParticlePair[0]).toArray(new PointInSpace[0]);
                tmpParticlePairAverageDistance = tmpDistanceCalculationUtils.getEqualParticlePairAverageDistance(tmpMoleculeParticlePositions);
            } else {
                PointInSpace[] tmpMoleculeParticlePositionsA = tmpMoleculeParticleToPositionsMap.get(tmpSingleMoleculeParticlePair[0]).toArray(new PointInSpace[0]);
                PointInSpace[] tmpMoleculeParticlePositionsB = tmpMoleculeParticleToPositionsMap.get(tmpSingleMoleculeParticlePair[1]).toArray(new PointInSpace[0]);
                tmpParticlePairAverageDistance = tmpDistanceCalculationUtils.getDifferentParticlePairAverageDistance(tmpMoleculeParticlePositionsA, tmpMoleculeParticlePositionsB);
            }
            if (tmpParticlePairAverageDistance == -1.0) {
                return null;
            } else {
                tmpMoleculeParticlePairDistanceList.add(new ParticlePairAverageDistance(tmpSingleMoleculeParticlePair, tmpParticlePairAverageDistance));
            }
        }

        // </editor-fold>
        if (tmpMoleculeParticlePairDistanceList.size() > 0) {
            return tmpMoleculeParticlePairDistanceList;
        } else {
            return null;
        }
    }

    /**
     * Returns particle pairs from particle-pair distance calculation value item
     * of Job Input value item container
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return Particle pairs from particle-pair distance calculation value item
     * of Job Input value item container or null if particle pairs could not be
     * found
     */
    private String[][] getParticlePairsForDistanceCalculation(ValueItemContainer aJobInputValueItemContainer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpParticlePairDistanceCalculationValueItem = aJobInputValueItemContainer.getValueItem("ParticlePairDistanceCalculation");
        if (tmpParticlePairDistanceCalculationValueItem == null || !tmpParticlePairDistanceCalculationValueItem.isActive()) {
            return null;
        }
        LinkedList<String[]> tmpParticlePairList = new LinkedList<String[]>();
        for (int i = 0; i < tmpParticlePairDistanceCalculationValueItem.getMatrixRowCount(); i++) {
            tmpParticlePairList.add(new String[]{tmpParticlePairDistanceCalculationValueItem.getValue(i, 0), tmpParticlePairDistanceCalculationValueItem.getValue(i, 1)});
        }
        if (tmpParticlePairList.size() > 0) {
            return tmpParticlePairList.toArray(new String[0][]);
        } else {
            return null;
        }
    }

    /**
     * Returns molecule-particle pairs from molecule-particle-pair
     * distance calculation value item of Job Input value item container
     *
     * @param aJobInputValueItemContainer Value item container of Job Input
     * @return Molecule-particle pairs from molecule-particle-pair
     * distance calculation value item of Job Input value item container or null
     * if molecule-particle pairs could not be found
     */
    private String[][] getMoleculeParticlePairsForDistanceCalculation(ValueItemContainer aJobInputValueItemContainer) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return null;
        }

        // </editor-fold>
        ValueItem tmpMoleculeParticlePairDistanceCalculationValueItem = aJobInputValueItemContainer.getValueItem("MoleculeParticlePairDistanceCalculation");
        if (tmpMoleculeParticlePairDistanceCalculationValueItem == null || !tmpMoleculeParticlePairDistanceCalculationValueItem.isActive()) {
            return null;
        }
        LinkedList<String[]> tmpMoleculeParticlePairList = new LinkedList<String[]>();
        for (int i = 0; i < tmpMoleculeParticlePairDistanceCalculationValueItem.getMatrixRowCount(); i++) {
            tmpMoleculeParticlePairList.add(new String[]{tmpMoleculeParticlePairDistanceCalculationValueItem.getValue(i, 0),
                tmpMoleculeParticlePairDistanceCalculationValueItem.getValue(i, 1)});
        }
        if (tmpMoleculeParticlePairList.size() > 0) {
            return tmpMoleculeParticlePairList.toArray(new String[0][]);
        } else {
            return null;
        }
    }

    /**
     * Writes particle-pair average distances to specified file
     *
     * @param aFilePathname File to write to
     * @param aParticlePairAverageDistanceList List with particle-pair average
     * distances
     * @return True: Operation successful, false: Otherwise (nothing is written)
     */
    private boolean writeParticlePairAverageDistances(String aFilePathname, LinkedList<ParticlePairAverageDistance> aParticlePairAverageDistanceList) {
        String[] tmpParticlePairAverageDistanceArray = new String[aParticlePairAverageDistanceList.size() + 1];

        String tmpVersion = "Version 1.0.0";
        tmpParticlePairAverageDistanceArray[0] = tmpVersion;

        int tmpIndex = 1;
        for (ParticlePairAverageDistance tmpParticlePairAverageDistance : aParticlePairAverageDistanceList) {
            tmpParticlePairAverageDistanceArray[tmpIndex++] = tmpParticlePairAverageDistance.getTokenString();
        }
        return this.fileUtilityMethods.writeDefinedStringArrayToFile(tmpParticlePairAverageDistanceArray, aFilePathname);
    }

    /**
     * Reads particle-pair average distances from specified file
     *
     * @param aFilePathname File to read from
     * @return Particle-pair average distances or null if particle-pair average
     * distances could not be read
     */
    private LinkedList<ParticlePairAverageDistance> readParticlePairAverageDistances(String aFilePathname) {
        String[] tmpParticlePairAverageDistanceArray = this.fileUtilityMethods.readDefinedStringArrayFromFile(aFilePathname);
        if (tmpParticlePairAverageDistanceArray == null) {
            return null;
        }

        try {
            String tmpVersion = tmpParticlePairAverageDistanceArray[0];
            if (tmpVersion.equals("Version 1.0.0")) {

                // <editor-fold defaultstate="collapsed" desc="Version 1.0.0">
                LinkedList<ParticlePairAverageDistance> tmpParticlePairAverageDistanceList = new LinkedList<ParticlePairAverageDistance>();
                for (int i = 1; i < tmpParticlePairAverageDistanceArray.length; i++) {
                    tmpParticlePairAverageDistanceList.add(new ParticlePairAverageDistance(tmpParticlePairAverageDistanceArray[i]));
                }
                return tmpParticlePairAverageDistanceList;
                // </editor-fold>

            } else {
                return null;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Box properties summary related methods">
    /**
     * Set box properties summary in box properties summary value item.
     * (No checks are performed)
     *
     * @param aBoxSummaryValueItem Box properties summary value item (is changed)
     * @param aParticleTableValueItem ParticleTable value item (is NOT changed)
     * @param aMonomerTableValueItem MonomerTable value item (is NOT changed)
     * @param aMoleculeTableValueItem MoleculeTable value item (is NOT changed)
     * @param aMoleculeChargeValueItem MoleculeCharge value item (is NOT changed 
     * and may be null for legacy reasons)
     * @param aQuantityValueItem Quantity value item (is NOT changed)
     * @param aDensityValueItem Density value item (is NOT changed)
     */
    private void setBoxSummaryValueItem(
        ValueItem aBoxSummaryValueItem,
        ValueItem aParticleTableValueItem,
        ValueItem aMonomerTableValueItem,
        ValueItem aMoleculeTableValueItem,
        ValueItem aMoleculeChargeValueItem,
        ValueItem aQuantityValueItem,
        ValueItem aDensityValueItem
    ) {
        // NOTE: No checks are necessary
        // Box properties summary has property-value pairs only
        ValueItemDataTypeFormat tmpDataTypeFormatProperty = new ValueItemDataTypeFormat(ValueItemEnumDataType.TEXT, false);
        ValueItemDataTypeFormat tmpDataTypeFormatDoubleValue = new ValueItemDataTypeFormat(ModelDefinitions.INTERACTION_NUMBER_OF_DECIMALS, false);
        ValueItemDataTypeFormat tmpDataTypeFormatValue = new ValueItemDataTypeFormat(ValueItemEnumDataType.NUMERIC, false);
        LinkedList<ValueItemMatrixElement[]> tmpRowList = new LinkedList<ValueItemMatrixElement[]>();
        // Total number of molecules and particles in simulation box
        ValueItem tmpMoleculeInfoValueItem = this.createMoleculeInfoValueItem(aMonomerTableValueItem, aMoleculeTableValueItem, aDensityValueItem, aQuantityValueItem);
        if (tmpMoleculeInfoValueItem != null) {
            try {
                int tmpTotalNumberOfMolecules = this.getTotalNumberOfMoleculesInSimulation(tmpMoleculeInfoValueItem);
                ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
                tmpRow[0] = new ValueItemMatrixElement(ModelMessage.get("BoxPropertiesSummary.TotalNumberOfMolecules"), tmpDataTypeFormatProperty);
                tmpRow[1] = new ValueItemMatrixElement(String.valueOf(tmpTotalNumberOfMolecules), tmpDataTypeFormatValue);
                tmpRowList.add(tmpRow);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                // Do nothing!
            }
            try {
                int tmpTotalNumberOfParticles = this.getTotalNumberOfParticlesInSimulation(tmpMoleculeInfoValueItem);
                ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
                tmpRow[0] = new ValueItemMatrixElement(ModelMessage.get("BoxPropertiesSummary.TotalNumberOfParticles"), tmpDataTypeFormatProperty);
                tmpRow[1] = new ValueItemMatrixElement(String.valueOf(tmpTotalNumberOfParticles), tmpDataTypeFormatValue);
                tmpRowList.add(tmpRow);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                // Do nothing!
            }
        }
        // Net charge of simulation
        if (tmpMoleculeInfoValueItem != null) {
            try {
                ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
                tmpRow[0] = new ValueItemMatrixElement(ModelMessage.get("BoxPropertiesSummary.NetChargeOfSimulation"), tmpDataTypeFormatProperty);
                if (aMoleculeChargeValueItem == null) {
                    tmpRow[1] = new ValueItemMatrixElement(ModelMessage.get("BoxPropertiesSummary.NotAvailable"), new ValueItemDataTypeFormat(false));
                } else {
                    double tmpNetChargeOfSimulation = this.getNetChargeOfSimulation(tmpMoleculeInfoValueItem, aMoleculeChargeValueItem);
                    tmpRow[1] = new ValueItemMatrixElement(String.valueOf(tmpNetChargeOfSimulation), tmpDataTypeFormatValue);
                }
                tmpRowList.add(tmpRow);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                // Do nothing!
            }
        }
        // Length conversion factor from DPD-length to Angstrom
        double tmpLengthConversionFactor = -1.0;
        try {
            tmpLengthConversionFactor = this.getLengthConversionFactorFromDpdToPhysicalLength(
                    aParticleTableValueItem,
                    aMonomerTableValueItem,
                    aMoleculeTableValueItem,
                    aQuantityValueItem,
                    aDensityValueItem);
            ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
            tmpRow[0] = new ValueItemMatrixElement(ModelMessage.get("BoxPropertiesSummary.LengthConversionDpdToAngstrom"), tmpDataTypeFormatProperty);
            tmpRow[1] = new ValueItemMatrixElement(String.valueOf(tmpLengthConversionFactor), tmpDataTypeFormatDoubleValue);
            tmpRowList.add(tmpRow);
            tmpRow = new ValueItemMatrixElement[2];
            tmpRow[0] = new ValueItemMatrixElement(ModelMessage.get("BoxPropertiesSummary.LengthConversioAngstromToDpd"), tmpDataTypeFormatProperty);
            tmpRow[1] = new ValueItemMatrixElement(String.valueOf(1.0 / tmpLengthConversionFactor), tmpDataTypeFormatDoubleValue);
            tmpRowList.add(tmpRow);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return;
        }
        // Protein information
        for (int i = 0; i < tmpMoleculeInfoValueItem.getMatrixRowCount(); i++) {
            if (tmpMoleculeInfoValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                // Amino acids MUST already be initialized
                PdbToDpd tmpPdbToDPD = null;
                try {
                    tmpPdbToDPD = PdbToDpdPool.getInstance().getPdbToDpd(tmpMoleculeInfoValueItem.getValueItemMatrixElement(i, 1).getProteinData());
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    continue;
                }
                String tmpPdbCode = null;
                try {
                    tmpPdbCode = tmpPdbToDPD.getPdbCode();
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    continue;
                }
                try {
                    double tmpMeanBackboneParticleDistanceInAngstrom = tmpPdbToDPD.getMeanBackboneParticleDistanceInAngstrom();
                    ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
                    tmpRow[0] = new ValueItemMatrixElement(String.format(ModelMessage.get("BoxPropertiesSummary.MeanBackboneParticleDistanceInAngstrom"), tmpMoleculeInfoValueItem.getValue(i, 0),
                            tmpPdbCode), tmpDataTypeFormatProperty);
                    tmpRow[1] = new ValueItemMatrixElement(String.valueOf(tmpMeanBackboneParticleDistanceInAngstrom), tmpDataTypeFormatDoubleValue);
                    tmpRowList.add(tmpRow);
                    double tmpMeanBackboneParticleDistanceInDpd = tmpMeanBackboneParticleDistanceInAngstrom / tmpLengthConversionFactor;
                    tmpRow = new ValueItemMatrixElement[2];
                    tmpRow[0] = new ValueItemMatrixElement(String.format(ModelMessage.get("BoxPropertiesSummary.MeanBackboneParticleDistanceInDpd"), tmpMoleculeInfoValueItem.getValue(i, 0),
                            tmpPdbCode), tmpDataTypeFormatProperty);
                    tmpRow[1] = new ValueItemMatrixElement(String.valueOf(tmpMeanBackboneParticleDistanceInDpd), tmpDataTypeFormatDoubleValue);
                    tmpRowList.add(tmpRow);
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    // Do nothing!
                }
                try {
                    if (tmpPdbToDPD.hasDisulfideBonds()) {
                        double tmpMeanDisulfideBondLengthInAngstrom = tmpPdbToDPD.getMeanDisulfideBondLength();
                        ValueItemMatrixElement[] tmpRow = new ValueItemMatrixElement[2];
                        tmpRow[0] = new ValueItemMatrixElement(String.format(ModelMessage.get("BoxPropertiesSummary.MeanDisulfideBondLengthInAngstrom"), tmpMoleculeInfoValueItem.getValue(i, 0),
                                tmpPdbCode), tmpDataTypeFormatProperty);
                        tmpRow[1] = new ValueItemMatrixElement(String.valueOf(tmpMeanDisulfideBondLengthInAngstrom), tmpDataTypeFormatDoubleValue);
                        tmpRowList.add(tmpRow);
                        double tmpMeanDisulfideBondLengthInDpd = tmpMeanDisulfideBondLengthInAngstrom / tmpLengthConversionFactor;
                        tmpRow = new ValueItemMatrixElement[2];
                        tmpRow[0] = new ValueItemMatrixElement(String.format(ModelMessage.get("BoxPropertiesSummary.MeanDisulfideBondLengthInDpd"), tmpMoleculeInfoValueItem.getValue(i, 0),
                                tmpPdbCode), tmpDataTypeFormatProperty);
                        tmpRow[1] = new ValueItemMatrixElement(String.valueOf(tmpMeanDisulfideBondLengthInDpd), tmpDataTypeFormatDoubleValue);
                        tmpRowList.add(tmpRow);
                    }
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    // Do nothing!
                }
                PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDPD, tmpMoleculeInfoValueItem.getValueItemMatrixElement(i, 1).getProteinData());
            }
        }
        // Set matrix
        if (tmpRowList.size() > 0) {
            aBoxSummaryValueItem.setLocked(false);
            ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[tmpRowList.size()][];
            int tmpIndex = 0;
            for (ValueItemMatrixElement[] tmpSingleRow : tmpRowList) {
                tmpMatrix[tmpIndex++] = tmpSingleRow;
            }
            aBoxSummaryValueItem.setMatrix(tmpMatrix);
        } else {
            aBoxSummaryValueItem.setLocked(true);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Simulation box length related methods">
    /**
     * Returns x-length of simulation box in DPD units.
     * (No checks are performed)
     * 
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @return x-length of simulation box in DPD units.
     */
    private double getSimulationBoxLengthX(ValueItemContainer aJobInputValueItemContainer){
        // (No checks are performed)
        ValueItem tmpBoxSizeValueItem = aJobInputValueItemContainer.getValueItem("BoxSize");
        return tmpBoxSizeValueItem.getValueAsDouble(0, 0);
    }

    /**
     * Returns y-length of simulation box in DPD units.
     * (No checks are performed)
     * 
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @return y-length of simulation box in DPD units.
     */
    private double getSimulationBoxLengthY(ValueItemContainer aJobInputValueItemContainer){
        // (No checks are performed)
        ValueItem tmpBoxSizeValueItem = aJobInputValueItemContainer.getValueItem("BoxSize");
        return tmpBoxSizeValueItem.getValueAsDouble(0, 1);
    }

    /**
     * Returns z-length of simulation box in DPD units.
     * (No checks are performed)
     * 
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @return z-length of simulation box in DPD units.
     */
    private double getSimulationBoxLengthZ(ValueItemContainer aJobInputValueItemContainer){
        // (No checks are performed)
        ValueItem tmpBoxSizeValueItem = aJobInputValueItemContainer.getValueItem("BoxSize");
        return tmpBoxSizeValueItem.getValueAsDouble(0, 2);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Molecule backbone and protein backbone/distance forces related methods">
    /**
     * Returns the number of backbone forces of the single molecule (which is 
     * NOT a protein) in simulation which has the maximum number of backbone 
     * forces.
     *
     * @param aJobInputValueItemContainer Job input value item container instance
     * (value items are NOT changed)
     * @return Number of backbone forces of the single molecule (which is 
     * NOT a protein) in simulation which has the maximum number of backbone 
     * forces or -1 if this quantity could not be calculated
     */
    private int getMaximumNumberOfMoleculeBackboneForces(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1;
        }
        // </editor-fold>
        ValueItem tmpMoleculeBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("MoleculeBackboneForces");
        if (tmpMoleculeBackboneForcesValueItem == null
                || tmpMoleculeBackboneForcesValueItem.isLocked()
                || !tmpMoleculeBackboneForcesValueItem.isActive()) {
            return 0;
        }
        try {
            // Molecule backbone force value item columns:
            // Index 0 = Molecule name
            // Index 1 = Backbone attribute 1
            // Index 2 = Backbone attribute 2
            // Index 3 = Backbone distance in Angstrom
            // Index 4 = Backbone distance in DPD unit
            // Index 5 = Backbone force constant
            int tmpMaximumNumberOfMoleculeBackboneForces = 0;
            HashMap<String, Integer> tmpMoleculeNameToBackboneForcesCountMap = new HashMap<>(tmpMoleculeBackboneForcesValueItem.getMatrixRowCount());
            for (int i = 0; i < tmpMoleculeBackboneForcesValueItem.getMatrixRowCount(); i++) {
                if (tmpMoleculeBackboneForcesValueItem.getValueAsDouble(i, 4) > 0.0
                        && tmpMoleculeBackboneForcesValueItem.getValueAsDouble(i, 5) > 0.0) {
                    String tmpMoleculeName = tmpMoleculeBackboneForcesValueItem.getValue(i, 0);
                    if (!tmpMoleculeNameToBackboneForcesCountMap.containsKey(tmpMoleculeName)) {
                        tmpMoleculeNameToBackboneForcesCountMap.put(tmpMoleculeName, 1);
                        tmpMaximumNumberOfMoleculeBackboneForces = Math.max(tmpMaximumNumberOfMoleculeBackboneForces, 1);
                    } else {
                        int tmpCurrentCount = tmpMoleculeNameToBackboneForcesCountMap.get(tmpMoleculeName);
                        int tmpNewCount = tmpCurrentCount + 1;
                        tmpMaximumNumberOfMoleculeBackboneForces = Math.max(tmpMaximumNumberOfMoleculeBackboneForces, tmpNewCount);
                        tmpMoleculeNameToBackboneForcesCountMap.replace(tmpMoleculeName, tmpCurrentCount, tmpNewCount);
                    }
                }
            }
            return tmpMaximumNumberOfMoleculeBackboneForces;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }

    /**
     * Returns the number of backbone forces of specified single molecule (which 
     * is NOT a protein) according to backbone force constants (backbone force 
     * constant must be > 0.0 to be taken into account)
     *
     * @param aMoleculeName Name of protein
     * @param aMoleculeBackboneForcesValueItem
     * MoleculeBackboneForces value item
     * @return The number of backbone forces of specified single molecule (which 
     * is NOT a protein) according to backbone force constants or -1 if number 
     * could not be calculated
     */
    private int getNumberOfSpecificMoleculeBackboneForces(String aMoleculeName, ValueItem aMoleculeBackboneForcesValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeName == null || aMoleculeName.isEmpty()) {
            return -1;
        }
        if (aMoleculeBackboneForcesValueItem == null
                || !aMoleculeBackboneForcesValueItem.getName().equals("MoleculeBackboneForces")
                || aMoleculeBackboneForcesValueItem.isLocked()
                || !aMoleculeBackboneForcesValueItem.isActive()) {
            return 0;
        }

        // </editor-fold>
        try {
            // Molecule backbone force value item columns:
            // Index 0 = Molecule name
            // Index 1 = Backbone attribute 1
            // Index 2 = Backbone attribute 2
            // Index 3 = Backbone distance in Angstrom
            // Index 4 = Backbone distance in DPD unit
            // Index 5 = Backbone force constant
            int tmpNumberBackboneForces = 0;
            for (int i = 0; i < aMoleculeBackboneForcesValueItem.getMatrixRowCount(); i++) {
                if (aMoleculeBackboneForcesValueItem.getValueAsDouble(i, 4) > 0.0 
                        && aMoleculeBackboneForcesValueItem.getValueAsDouble(i, 5) > 0.0) {
                    String tmpCurrentMoleculeName = aMoleculeBackboneForcesValueItem.getValue(i, 0);
                    if (tmpCurrentMoleculeName.equals(aMoleculeName)) {
                        tmpNumberBackboneForces++;
                    }
                }
            }
            return tmpNumberBackboneForces;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }
    
    /**
     * Returns the number of distance plus backbone forces of the single protein
     * in simulation which has the maximum number of distance plus backbone forces.
     *
     * @param aJobInputValueItemContainer Job input value item container instance
     * (value items are NOT changed)
     * @return Number of distance plus backbone forces of the single protein in
     * simulation which has the maximum number of distance plus backbone forces 
     * or -1 if this quantity could not be calculated
     */
    private int getMaximumNumberOfProteinForces(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1;
        }
        // </editor-fold>
        ValueItem tmpMoleculeTableValueItem = aJobInputValueItemContainer.getValueItem("MoleculeTable");
        if (tmpMoleculeTableValueItem == null) {
            return -1;
        }
        ValueItem tmpProteinBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("ProteinBackboneForces");
        boolean tmpHasProteinBackboneForcesValueItem = tmpProteinBackboneForcesValueItem != null && !tmpProteinBackboneForcesValueItem.isLocked() || tmpProteinBackboneForcesValueItem.isActive();
        ValueItem tmpProteinDistanceForcesValueItem = aJobInputValueItemContainer.getValueItem("ProteinDistanceForces");
        boolean tmpHasProteinDistanceForcesValueItem = tmpProteinDistanceForcesValueItem != null || !tmpProteinDistanceForcesValueItem.isLocked() || tmpProteinDistanceForcesValueItem.isActive();
        if (!tmpHasProteinBackboneForcesValueItem && !tmpHasProteinDistanceForcesValueItem) {
            return 0;
        }
        try {
            int tmpMaximumNumberOfProteinForces = 0;
            // Loop over all molecules
            for (int i = 0; i < tmpMoleculeTableValueItem.getMatrixRowCount(); i++) {
                if (tmpMoleculeTableValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                    //  tmpMoleculeTableValueItem: Column 0 = Molecule name, column 1 = Molecular structure and possible protein data
                    String tmpProteinName = tmpMoleculeTableValueItem.getValue(i, 0);
                    String tmpProteinData = tmpMoleculeTableValueItem.getValueItemMatrixElement(i, 1).getProteinData();
                    int tmpNumberOfBackboneForces = 0;
                    if (tmpHasProteinBackboneForcesValueItem) {
                        tmpNumberOfBackboneForces = this.getNumberOfSpecificProteinBackboneForces(tmpProteinName, tmpProteinBackboneForcesValueItem);
                    }
                    int tmpNumberOfDistanceForces = 0;
                    if (tmpHasProteinDistanceForcesValueItem) {
                        tmpNumberOfDistanceForces = this.getNumberOfSpecificProteinDistanceForces(tmpProteinName, tmpProteinData, tmpProteinDistanceForcesValueItem);
                    }
                    if (tmpNumberOfDistanceForces == -1 || tmpNumberOfBackboneForces == -1) {
                        return -1;
                    }
                    int tmpNumberOfProteinForces = tmpNumberOfBackboneForces + tmpNumberOfDistanceForces;
                    tmpMaximumNumberOfProteinForces = Math.max(tmpMaximumNumberOfProteinForces, tmpNumberOfProteinForces);
                }
            }
            return tmpMaximumNumberOfProteinForces;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }
    
    /**
     * Returns the number of distance forces of the single protein
     * in simulation which has the maximum number of distance forces.
     *
     * @param aJobInputValueItemContainer Job input value item container instance
     * (value items are NOT changed)
     * @return Number of backbone forces of the single protein in
     * simulation which has the maximum number of backbone forces or -1 if
     * this quantity could not be calculated
     */
    private int getMaximumNumberOfProteinDistanceForces(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1;
        }
        // </editor-fold>
        ValueItem tmpMoleculeInfoValueItem = this.createMoleculeInfoValueItem(aJobInputValueItemContainer);
        if (tmpMoleculeInfoValueItem == null) {
            return -1;
        }
        ValueItem tmpProteinDistanceForcesValueItem = aJobInputValueItemContainer.getValueItem("ProteinDistanceForces");
        if (tmpProteinDistanceForcesValueItem == null
                || tmpProteinDistanceForcesValueItem.isLocked()
                || !tmpProteinDistanceForcesValueItem.isActive()) {
            return 0;
        }
        try {
            int tmpMaximumNumberOfProteinDistanceForces = 0;
            // Loop over all molecules
            for (int i = 0; i < tmpMoleculeInfoValueItem.getMatrixRowCount(); i++) {
                if (tmpMoleculeInfoValueItem.getValueItemMatrixElement(i, 1).hasProteinData()) {
                    //  tmpMoleculeInfoValueItem: Column 0 = Molecule name, column 1 = Molecular structure and possible protein data
                    int tmpMaximumNumberOfBackboneForcesOfSpecificProtein = this.getNumberOfSpecificProteinDistanceForces(
                            tmpMoleculeInfoValueItem.getValue(i, 0),
                            tmpMoleculeInfoValueItem.getValueItemMatrixElement(i, 1).getProteinData(),
                            tmpProteinDistanceForcesValueItem);
                    if (tmpMaximumNumberOfBackboneForcesOfSpecificProtein == -1) {
                        return -1;
                    }
                    tmpMaximumNumberOfProteinDistanceForces = Math.max(tmpMaximumNumberOfProteinDistanceForces, tmpMaximumNumberOfBackboneForcesOfSpecificProtein);
                }
            }
            return tmpMaximumNumberOfProteinDistanceForces;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }
    
    /**
     * Returns the number of backbone forces of the single protein in simulation 
     * which has the maximum number of backbone forces.
     *
     * @param aJobInputValueItemContainer Job input value item container 
     * instance (value items are NOT changed)
     * @return Number of backbone forces of the single protein in simulation 
     * which has the maximum number of backbone or -1 if this quantity could not 
     * be calculated
     */
    private int getMaximumNumberOfProteinBackboneForces(ValueItemContainer aJobInputValueItemContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aJobInputValueItemContainer == null) {
            return -1;
        }
        // </editor-fold>
        ValueItem tmpProteinBackboneForcesValueItem = aJobInputValueItemContainer.getValueItem("ProteinBackboneForces");
        if (tmpProteinBackboneForcesValueItem == null
                || tmpProteinBackboneForcesValueItem.isLocked()
                || !tmpProteinBackboneForcesValueItem.isActive()) {
            return 0;
        }
        try {
            // Protein backbone force value item columns:
            // Index 0 = Protein name
            // Index 1 = Amino acid backbone particle 1
            // Index 2 = Amino acid backbone particle 2
            // Index 3 = Backbone distance in Angstrom
            // Index 4 = Backbone distance in DPD unit
            // Index 5 = Backbone force constant
            int tmpMaximumNumberOfProteinBackboneForces = 0;
            HashMap<String, Integer> tmpProteinNameToBackboneForcesCountMap = new HashMap<>(tmpProteinBackboneForcesValueItem.getMatrixRowCount());
            for (int i = 0; i < tmpProteinBackboneForcesValueItem.getMatrixRowCount(); i++) {
                if (tmpProteinBackboneForcesValueItem.getValueAsDouble(i, 4) > 0.0
                        && tmpProteinBackboneForcesValueItem.getValueAsDouble(i, 5) > 0.0) {
                    String tmpProteinName = tmpProteinBackboneForcesValueItem.getValue(i, 0);
                    if (!tmpProteinNameToBackboneForcesCountMap.containsKey(tmpProteinName)) {
                        tmpProteinNameToBackboneForcesCountMap.put(tmpProteinName, 1);
                        tmpMaximumNumberOfProteinBackboneForces = Math.max(tmpMaximumNumberOfProteinBackboneForces, 1);
                    } else {
                        int tmpCurrentCount = tmpProteinNameToBackboneForcesCountMap.get(tmpProteinName);
                        int tmpNewCount = tmpCurrentCount + 1;
                        tmpMaximumNumberOfProteinBackboneForces = Math.max(tmpMaximumNumberOfProteinBackboneForces, tmpNewCount);
                        tmpProteinNameToBackboneForcesCountMap.replace(tmpProteinName, tmpCurrentCount, tmpNewCount);
                    }
                }
            }
            return tmpMaximumNumberOfProteinBackboneForces;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }

    /**
     * Returns the number of distance forces of specified protein according
     * to distance force constants (distance force constant must be > 0.0 to be
     * taken into account)
     *
     * @param aProteinName Name of protein
     * @param aProteinData Protein data for specified protein
     * @param aProteinDistanceForcesValueItem
     * ProteinDistanceForces value item
     * @return The number of distance forces of specified protein according
     * to distance force constants or -1 if number could not be
     * calculated
     */
    private int getNumberOfSpecificProteinDistanceForces(String aProteinName, String aProteinData, ValueItem aProteinDistanceForcesValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aProteinName == null || aProteinName.isEmpty()) {
            return -1;
        }
        if (aProteinData == null || aProteinData.isEmpty()) {
            return -1;
        }
        if (aProteinDistanceForcesValueItem == null
                || !aProteinDistanceForcesValueItem.getName().equals("ProteinDistanceForces")
                || aProteinDistanceForcesValueItem.isLocked()
                || !aProteinDistanceForcesValueItem.isActive()) {
            return 0;
        }

        // </editor-fold>
        try {
            int tmpMaximumNumberOfProteinDistanceForces = 0;
            // Get column with individual protein information: This starts at the 3. column
            int tmpIndexOfProteinColumn = this.getSpecificProteinBackboneForcesColumnIndex(aProteinName, aProteinDistanceForcesValueItem);
            if (tmpIndexOfProteinColumn < 0) {
                return 0;
            }
            // Evaluate individual protein information
            PdbToDpd tmpPdbToDPD = PdbToDpdPool.getInstance().getPdbToDpd(aProteinData);
            for (int i = 0; i < aProteinDistanceForcesValueItem.getMatrixRowCount(); i++) {
                if (aProteinDistanceForcesValueItem.getValueAsDouble(i, tmpIndexOfProteinColumn) > 0.0) {
                    // NOTE: 1-i distance has i = 2 for distance type = 1 etc.
                    int tmpDistanceType = aProteinDistanceForcesValueItem.getValueAsInt(i, 0) - 1;
                    tmpMaximumNumberOfProteinDistanceForces += tmpPdbToDPD.getNumberOfProteinDistanceForces(tmpDistanceType);
                }
            }
            PdbToDpdPool.getInstance().setPdbToDpdForReuse(tmpPdbToDPD, aProteinData);
            return tmpMaximumNumberOfProteinDistanceForces;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }

    /**
     * Returns the number of backbone forces of specified protein according
     * to backbone force constants (backbone force constant must be > 0.0 to be
     * taken into account)
     *
     * @param aProteinName Name of protein
     * @param aProteinBackboneForcesValueItem
     * ProteinBackboneForces value item
     * @return The number of backbone forces of specified protein according
     * to backbone force constants or -1 if number could not be
     * calculated
     */
    private int getNumberOfSpecificProteinBackboneForces(String aProteinName, ValueItem aProteinBackboneForcesValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aProteinName == null || aProteinName.isEmpty()) {
            return -1;
        }
        if (aProteinBackboneForcesValueItem == null
                || !aProteinBackboneForcesValueItem.getName().equals("ProteinBackboneForces")
                || aProteinBackboneForcesValueItem.isLocked()
                || !aProteinBackboneForcesValueItem.isActive()) {
            return 0;
        }

        // </editor-fold>
        try {
            // Protein backbone force value item columns:
            // Index 0 = Protein name
            // Index 1 = Amino acid backbone particle 1
            // Index 2 = Amino acid backbone particle 2
            // Index 3 = Backbone distance in Angstrom
            // Index 4 = Backbone distance in DPD unit
            // Index 5 = Backbone force constant
            int tmpNumberBackboneForces = 0;
            for (int i = 0; i < aProteinBackboneForcesValueItem.getMatrixRowCount(); i++) {
                if (aProteinBackboneForcesValueItem.getValueAsDouble(i, 4) > 0.0 
                        && aProteinBackboneForcesValueItem.getValueAsDouble(i, 5) > 0.0) {
                    String tmpCurrentProteinName = aProteinBackboneForcesValueItem.getValue(i, 0);
                    if (tmpCurrentProteinName.equals(aProteinName)) {
                        tmpNumberBackboneForces++;
                    }
                }
            }
            return tmpNumberBackboneForces;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }

    /**
     * Returns index of column of specified protein in
     * aProteinDistanceForcesValueItem
     *
     * @param aProteinName Name of protein
     * @param aProteinDistanceForcesValueItem
     * ProteinDistanceForces value item
     * @return Index of column of specified protein in
     * aProteinDistanceForcesValueItem or -1 if column could not be
     * found
     */
    private int getSpecificProteinBackboneForcesColumnIndex(String aProteinName, ValueItem aProteinDistanceForcesValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aProteinName == null || aProteinName.isEmpty()) {
            return -1;
        }
        if (aProteinDistanceForcesValueItem == null
                || !aProteinDistanceForcesValueItem.getName().equals("ProteinDistanceForces")) {
            return -1;
        }

        // </editor-fold>
        try {
            int tmpIndexOfProteinColumn = -1;
            String[] tmpColumnNameArray = aProteinDistanceForcesValueItem.getMatrixColumnNames();
            if (tmpColumnNameArray != null) {
                for (int i = 1; i < tmpColumnNameArray.length; i++) {
                    // IMPORTANT: The ".startsWith()" operation has to correspond to the definition in ModelMessage.getString("JdpdInputFile.parameter.proteinDistanceForceConstantFormat").
                    //            Changes in the proteinBackboneForceConstantFormat may lead to other operations for column detection!
                    if (tmpColumnNameArray[i].startsWith(aProteinName)) {
                        tmpIndexOfProteinColumn = i;
                        break;
                    }
                }
            }
            return tmpIndexOfProteinColumn;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return -1;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns center of points in aPointList
     * 
     * @param aPointList List of points
     * @return Center of points in aPointList or null if center can not be 
     * calculated
     */
    private PointInSpace getCenterPoint(LinkedList<PointInSpace> aPointList) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aPointList == null || aPointList.isEmpty()) {
            return null;
        }
        // </editor-fold>
        PointInSpace tmpCenterPoint = new PointInSpace(0.0, 0.0, 0.0);
        for (PointInSpace tmpPoint : aPointList) {
            tmpCenterPoint.add(tmpPoint);
        }
        return tmpCenterPoint;
    }
    // </editor-fold>
    // </editor-fold>

}
