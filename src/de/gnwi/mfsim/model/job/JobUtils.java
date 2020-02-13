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

import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.mfsim.model.particleStructure.SpicesPool;
import de.gnwi.mfsim.model.particleStructure.ParticleInfo;
import de.gnwi.mfsim.model.particleStructure.ParticleInfoContainer;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Utility class with static utility methods for value items of Jdpd input
 * file
 *
 * @author Achim Zielesny
 */
public final class JobUtils {

    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * Pattern for single particle to match
     */
    private static Pattern particlePattern = Pattern.compile(ModelDefinitions.PARTICLE_REGEX_PATTERN_STRING);
    /**
     * Utility string methods
     */
    private static StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="DPD interaction parameter related methods (obsolete)">
    /**
     * NOTE: This method is no longer used and replaced by
     * convertToDpdInteractionParameter() Converts minimum parameter to DPD
     * interaction parameter Source: "Theory for Calculation of Interaction
     * Parameters aij.doc" in documentation
     *
     * @param aParticle_A Particle A
     * @param aParticle_B Particle B
     * @param aMinimumMolWeight Minimum molecular weight in g/mol of all
     * particles used for current job
     * @param aMolWeight_A Molecular weight in g/mol of particle A as a string
     * @param aMolWeight_B Molecular weight in g/mol of particle B as a string
     * @param aVolume_A Volume of particle A as a string (unit is unimportant
     * since this value is only used in a ratio to aVolume_B)
     * @param aVolume_B Volume of particle B as a string (unit is unimportant
     * since this value is only used in a ratio to aVolume_A)
     * @param aTemperatureRepresentation Temperature in K as a representation
     * string
     * @param aDpdDensity DPD density
     *
     * @return DPD interaction parameter a(i,j) for particles or null if
     * parameter could not be calculated
     */
    public static String convertToDpdInteractionParameter_Old1(String aParticle_A, String aParticle_B, String aMinimumMolWeight, String aMolWeight_A, String aMolWeight_B, String aVolume_A,
            String aVolume_B, String aTemperatureRepresentation, String aDpdDensity) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle_A == null || aParticle_A.isEmpty() || !JobUtils.particlePattern.matcher(aParticle_A).matches()) {
            return null;
        }
        if (aParticle_B == null || aParticle_B.isEmpty() || !JobUtils.particlePattern.matcher(aParticle_B).matches()) {
            return null;
        }
        if (aMinimumMolWeight == null || aMinimumMolWeight.isEmpty() || !JobUtils.stringUtilityMethods.isDoubleValue(aMinimumMolWeight)) {
            return null;
        }
        if (aMolWeight_A == null || aMolWeight_A.isEmpty() || !JobUtils.stringUtilityMethods.isDoubleValue(aMolWeight_A)) {
            return null;
        }
        if (aMolWeight_B == null || aMolWeight_B.isEmpty() || !JobUtils.stringUtilityMethods.isDoubleValue(aMolWeight_B)) {
            return null;
        }
        if (aVolume_A == null || aVolume_A.isEmpty() || !JobUtils.stringUtilityMethods.isDoubleValue(aVolume_A)) {
            return null;
        }
        if (aVolume_B == null || aVolume_B.isEmpty() || !JobUtils.stringUtilityMethods.isDoubleValue(aVolume_B)) {
            return null;
        }
        if (aTemperatureRepresentation == null || aTemperatureRepresentation.isEmpty() || !JobUtils.stringUtilityMethods.isDoubleValue(aTemperatureRepresentation)) {
            return null;
        }
        if (aDpdDensity == null || aDpdDensity.isEmpty() || !(aDpdDensity.equals("3") || aDpdDensity.equals("5"))) {
            return null;
        }

        // </editor-fold>
        double tmpA_AB;
        double tmpTemperature = Double.valueOf(aTemperatureRepresentation);
        if (aParticle_A.equals(aParticle_B)) {
            // <editor-fold defaultstate="collapsed" desc="Equal Particles a(i,i)">
            if (aDpdDensity == "3") {
                // DPD density is 3
                tmpA_AB = 75.0 / (3.0 * 298.0) * tmpTemperature;
            } else {
                // DPD density is 5
                tmpA_AB = 75.0 / (5.0 * 298.0) * tmpTemperature;
            }

            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Different Particles a(i,k)">
            // <editor-fold defaultstate="collapsed" desc="- Constants">
            double tmpConversionFactor = 4186.8;
            double tmpR = 8.31451;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Molecular weights">
            double tmpM_A = Double.valueOf(aMolWeight_A);
            double tmpM_B = Double.valueOf(aMolWeight_B);
            double tmpM_Smallest = Double.valueOf(aMinimumMolWeight);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Volumes">
            double tmpVolume_A = Double.valueOf(aVolume_A);
            double tmpVolume_B = Double.valueOf(aVolume_B);
            double tmpVolumeRatioAB = tmpVolume_A / tmpVolume_B;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Minima and interaction energies">
            // <editor-fold defaultstate="collapsed" desc="-- Emin(A,B) : tmpE_Min_AB">
            String tmpE_Min_AB_Representation = StandardParticleInteractionData.getInstance().getInteraction(aParticle_A, aParticle_B, aTemperatureRepresentation);
            if (tmpE_Min_AB_Representation == null) {
                return null;
            }
            double tmpE_Min_AB = Double.valueOf(tmpE_Min_AB_Representation);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Emin(A,A) : tmpE_Min_AA">
            String tmpE_Min_AA_Representation = StandardParticleInteractionData.getInstance().getInteraction(aParticle_A, aParticle_A, aTemperatureRepresentation);
            if (tmpE_Min_AA_Representation == null) {
                return null;
            }
            double tmpE_Min_AA = Double.valueOf(tmpE_Min_AA_Representation);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Emin(B,B) : tmpE_Min_BB">
            String tmpE_Min_BB_Representation = StandardParticleInteractionData.getInstance().getInteraction(aParticle_B, aParticle_B, aTemperatureRepresentation);
            if (tmpE_Min_BB_Representation == null) {
                return null;
            }
            double tmpE_Min_BB = Double.valueOf(tmpE_Min_BB_Representation);

            // </editor-fold>
            double tmpE_AB = tmpE_Min_AB / (tmpM_A + tmpM_B) * tmpConversionFactor;
            double tmpE_AA = tmpE_Min_AA / (tmpM_A + tmpM_A) * tmpConversionFactor;
            double tmpE_BB = tmpE_Min_BB / (tmpM_B + tmpM_B) * tmpConversionFactor;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Coordination numbers">
            double tmpCoordinationNumber_AA = 5.5515;
            double tmpCoordinationNumber_BB = 5.5515;
            double tmpCoordinationNumber_Mean_AB = 3.18669 + 3.22104 * tmpVolumeRatioAB - 1.04266 * Math.pow(tmpVolumeRatioAB, 2) + 0.19588 * Math.pow(tmpVolumeRatioAB, 3) - 0.01753
                    * Math.pow(tmpVolumeRatioAB, 4) + 5.81289E-4 * Math.pow(tmpVolumeRatioAB, 5);

            // </editor-fold>
            double tmpTerm = tmpE_AB * tmpCoordinationNumber_Mean_AB - 0.5 * (tmpE_AA * tmpCoordinationNumber_AA + tmpE_BB * tmpCoordinationNumber_BB);
            double tmpChi_AB = tmpM_Smallest * tmpTerm / (tmpR * 298.0);

            if (aDpdDensity == "3") {
                // DPD density is 3
                tmpA_AB = 75.0 / (3.0 * 298.0) * tmpTemperature + 3.497 * tmpChi_AB;
            } else {
                // DPD density is 5
                tmpA_AB = 75.0 / (5.0 * 298.0) * tmpTemperature + 1.451 * tmpChi_AB;
            }

            // </editor-fold>
        }
        return String.valueOf(tmpA_AB);
    }

    /**
     * Converts minimum parameter to DPD interaction parameter Source:
     * "Interaction parameter calculation/Calculation of interaction parameters
     * aij.ppt" in documentation
     *
     * @param aParticle_A Particle A
     * @param aParticle_B Particle B
     * @param aVolume_A Volume of particle A as a string (unit is unimportant
     * since this value is only used in a ratio to aVolume_B)
     * @param aVolume_B Volume of particle B as a string (unit is unimportant
     * since this value is only used in a ratio to aVolume_A)
     * @param aTemperatureRepresentation Temperature in K as a representation
     * string
     * @param aDpdDensity DPD density
     *
     * @return DPD interaction parameter a(i,j) for particles or null if
     * parameter could not be calculated
     */
    public static String convertToDpdInteractionParameter_Old2(String aParticle_A, String aParticle_B, String aVolume_A, String aVolume_B, String aTemperatureRepresentation, String aDpdDensity) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle_A == null || aParticle_A.isEmpty() || !JobUtils.particlePattern.matcher(aParticle_A).matches()) {
            return null;
        }
        if (aParticle_B == null || aParticle_B.isEmpty() || !JobUtils.particlePattern.matcher(aParticle_B).matches()) {
            return null;
        }
        if (aVolume_A == null || aVolume_A.isEmpty() || !JobUtils.stringUtilityMethods.isDoubleValue(aVolume_A)) {
            return null;
        }
        if (aVolume_B == null || aVolume_B.isEmpty() || !JobUtils.stringUtilityMethods.isDoubleValue(aVolume_B)) {
            return null;
        }
        if (aTemperatureRepresentation == null || aTemperatureRepresentation.isEmpty() || !JobUtils.stringUtilityMethods.isDoubleValue(aTemperatureRepresentation)) {
            return null;
        }
        if (aDpdDensity == null || aDpdDensity.isEmpty() || !(aDpdDensity.equals("3") || aDpdDensity.equals("5"))) {
            return null;
        }

        // </editor-fold>
        double tmpA_AB;
        double tmpTemperature = Double.valueOf(aTemperatureRepresentation);
        if (aParticle_A.equals(aParticle_B)) {
            // <editor-fold defaultstate="collapsed" desc="Equal Particles a(i,i)">
            if (aDpdDensity == "3") {
                // DPD density is 3
                tmpA_AB = 75.0 / (3.0 * 298.0) * tmpTemperature;
            } else {
                // DPD density is 5
                tmpA_AB = 75.0 / (5.0 * 298.0) * tmpTemperature;
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Different Particles a(i,k)">
            // <editor-fold defaultstate="collapsed" desc="- Constants">
            double tmpConversionFactor = 4186.8;
            double tmpR = 8.31451;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Volumes">
            double tmpVolume_A = Double.valueOf(aVolume_A);
            double tmpVolume_B = Double.valueOf(aVolume_B);
            double tmpVolumeRatioAB = tmpVolume_A / tmpVolume_B;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Minima and interaction energies">
            // <editor-fold defaultstate="collapsed" desc="-- Emin(A,B) : tmpE_Min_AB">
            String tmpE_Min_AB_Representation = StandardParticleInteractionData.getInstance().getInteraction(aParticle_A, aParticle_B, aTemperatureRepresentation);
            if (tmpE_Min_AB_Representation == null) {
                return null;
            }
            double tmpE_Min_AB = Double.valueOf(tmpE_Min_AB_Representation);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Emin(A,A) : tmpE_Min_AA">
            String tmpE_Min_AA_Representation = StandardParticleInteractionData.getInstance().getInteraction(aParticle_A, aParticle_A, aTemperatureRepresentation);
            if (tmpE_Min_AA_Representation == null) {
                return null;
            }
            double tmpE_Min_AA = Double.valueOf(tmpE_Min_AA_Representation);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Emin(B,B) : tmpE_Min_BB">
            String tmpE_Min_BB_Representation = StandardParticleInteractionData.getInstance().getInteraction(aParticle_B, aParticle_B, aTemperatureRepresentation);
            if (tmpE_Min_BB_Representation == null) {
                return null;
            }
            double tmpE_Min_BB = Double.valueOf(tmpE_Min_BB_Representation);

            // </editor-fold>
            double tmpE_AB = tmpE_Min_AB * tmpConversionFactor;
            double tmpE_AA = tmpE_Min_AA * tmpConversionFactor;
            double tmpE_BB = tmpE_Min_BB * tmpConversionFactor;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Coordination numbers">
            double tmpCoordinationNumber_AA = 5.5515;
            double tmpCoordinationNumber_BB = 5.5515;
            double tmpCoordinationNumber_Mean_AB = 3.18669 + 3.22104 * tmpVolumeRatioAB - 1.04266 * Math.pow(tmpVolumeRatioAB, 2) + 0.19588 * Math.pow(tmpVolumeRatioAB, 3) - 0.01753
                    * Math.pow(tmpVolumeRatioAB, 4) + 5.81289E-4 * Math.pow(tmpVolumeRatioAB, 5);
            // </editor-fold>
            double tmpTerm = tmpE_AB * tmpCoordinationNumber_Mean_AB - 0.5 * (tmpE_AA * tmpCoordinationNumber_AA + tmpE_BB * tmpCoordinationNumber_BB);
            double tmpChi_AB = tmpTerm / (tmpR * 298.0);
            if (aDpdDensity == "3") {
                // DPD density is 3
                tmpA_AB = 75.0 / (3.0 * 298.0) * tmpTemperature + 3.497 * tmpChi_AB;
            } else {
                // DPD density is 5
                tmpA_AB = 75.0 / (5.0 * 298.0) * tmpTemperature + 1.451 * tmpChi_AB;
            }
            // </editor-fold>
        }
        return String.valueOf(tmpA_AB);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="JobInput value item based methods">
    // <editor-fold defaultstate="collapsed" desc="- ParticleTable value item related methods">
    /**
     * Adds data of value item ParticleTable to particle container
     *
     * @param aParticleInfoContainer Particle container
     * @param aParticleTableValueItem Value item ParticleTable
     * @throws IllegalArgumentException Thrown if an argument is null
     */
    public static void addToParticleInfoContainer(ParticleInfoContainer aParticleInfoContainer, ValueItem aParticleTableValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleInfoContainer == null || aParticleTableValueItem == null) {
            throw new IllegalArgumentException("An argument is null.");
        }

        // </editor-fold>
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            aParticleInfoContainer.addParticleInfo(new ParticleInfo(aParticleTableValueItem.getValue(i, 0), // Particle
                    aParticleTableValueItem.getValueAsDouble(i, 5), // Volume
                    aParticleTableValueItem.getValueAsDouble(i, 4) // Molar_Weight
            ));
        }
    }

    /**
     * Returns HashMap with particles of value item ParticleTable
     *
     * @param aParticleTableValueItem Value item ParticleTable (is NOT
     * changed)
     * @return HashMap with particles of value item ParticleTable or null if
     * HashMap could not be created
     */
    public static HashMap<String, String> getParticleMatrixParticlesHashMap(ValueItem aParticleTableValueItem) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null) {
            return null;
        }

        // </editor-fold>
        HashMap<String, String> tmpParticlesHashMap = new HashMap<String, String>(aParticleTableValueItem.getMatrixRowCount());
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            String tmpParticle = aParticleTableValueItem.getValue(i, 0);
            // No safeguards necessary since particles in particle matrix do NOT have doublets
            tmpParticlesHashMap.put(tmpParticle, tmpParticle);
        }
        return tmpParticlesHashMap;
    }

    /**
     * Returns minimum molecular weight in g/mol of all particles in particle
     * matrix value item
     *
     * @param aParticleTableValueItem Value item ParticleTable (is not
     * changed)
     * @return Minimum molecular weight in g/mol of all particles in particle
     * matrix value item or null if particle matrix value item is illegal
     */
    public static String getMinimumMolWeightInGMol(ValueItem aParticleTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return null;
        }
        // </editor-fold>
        String tmpMinimumMolWeight = aParticleTableValueItem.getValue(0, 4);
        double tmpMinimumMolWeightValue = Double.valueOf(tmpMinimumMolWeight);
        for (int i = 1; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            String tmpMolWeight = aParticleTableValueItem.getValue(i, 4);
            double tmpMolWeightValue = Double.valueOf(tmpMolWeight);
            if (tmpMolWeightValue < tmpMinimumMolWeightValue) {
                tmpMinimumMolWeight = tmpMolWeight;
                tmpMinimumMolWeightValue = Double.valueOf(tmpMinimumMolWeight);
            }
        }
        return tmpMinimumMolWeight;
    }

    /**
     * Returns molecular weight in g/mol of particle from particle matrix value
     * item
     *
     * @param aParticle Particle
     * @param aParticleTableValueItem Value item ParticleTable (is not
     * changed)
     * @return Molecular weight in g/mol of particle from particle matrix value
     * item or null if particle is not found
     */
    public static String getMolWeightInGMolOfParticle(String aParticle, ValueItem aParticleTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle == null || aParticle.isEmpty() || !JobUtils.particlePattern.matcher(aParticle).matches()) {
            return null;
        }
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return null;
        }
        // </editor-fold>
        String tmpMolWeight = null;
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            if (aParticleTableValueItem.getValue(i, 0).equals(aParticle)) {
                tmpMolWeight = aParticleTableValueItem.getValue(i, 4);
                break;
            }
        }
        return tmpMolWeight;
    }

    /**
     * Returns volume of particle from particle matrix value item
     *
     * @param aParticle Particle
     * @param aParticleTableValueItem Value item ParticleTable (is not
     * changed)
     * @return Volume of particle from particle matrix value item or null if
     * particle is not found
     */
    public static String getVolumeOfParticle(String aParticle, ValueItem aParticleTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle == null || aParticle.isEmpty() || !JobUtils.particlePattern.matcher(aParticle).matches()) {
            return null;
        }
        if (aParticleTableValueItem == null || !aParticleTableValueItem.getName().equals("ParticleTable")) {
            return null;
        }
        // </editor-fold>
        String tmpVolume = null;
        for (int i = 0; i < aParticleTableValueItem.getMatrixRowCount(); i++) {
            if (aParticleTableValueItem.getValue(i, 0).equals(aParticle)) {
                tmpVolume = aParticleTableValueItem.getValue(i, 5);
                break;
            }
        }
        return tmpVolume;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MonomerTable value item related methods">
    /**
     * Returns all particles of all molecular structures of value item MonomerTable
     *
     * @param aMonomerTableValueItem Value item MonomerTable
     * @return All particles of all molecular structures or null if none were
     * found
     * @throws IllegalArgumentException Thrown if an argument is null
     */
    public static String[] getAllParticlesOfMonomerTableValueItem(ValueItem aMonomerTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMonomerTableValueItem == null || !aMonomerTableValueItem.getName().equals("MonomerTable")) {
            return null;
        }

        // </editor-fold>
        LinkedList<String> tmpParticleList = new LinkedList<String>();
        for (int i = 0; i < aMonomerTableValueItem.getMatrixRowCount(); i++) {
            // Old Code:
            // Parameter true: Structure is a monomer
            // JobUtils.spices.setInputStructure(aMonomerTableValueItem.getValue(i, 1), true);
            // String[] tmpParticles = JobUtils.spices.getParticles();
            // Parameter true: Structure is a monomer
            SpicesGraphics tmpSpices = new SpicesGraphics(aMonomerTableValueItem.getValue(i, 1), true);
            String[] tmpParticles = tmpSpices.getParticles();
            if (tmpParticles != null) {
                for (String tmpSingleParticle : tmpParticles) {
                    if (!tmpParticleList.contains(tmpSingleParticle)) {
                        tmpParticleList.addLast(tmpSingleParticle);
                    }
                }
            }
        }
        if (tmpParticleList.size() == 0) {
            return null;
        } else {
            return tmpParticleList.toArray(new String[0]);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- MoleculeTable value item related methods">
    /**
     * Returns all particles of all molecular structures of value itemMoleculeTable
     *
     * @param aMoleculeTableValueItem Value itemMoleculeTable
     * @return All particles of all molecular structures or null if none were
     * found
     * @throws IllegalArgumentException Thrown if an argument is null
     */
    public static String[] getAllParticlesOfMoleculeTableValueItem(ValueItem aMoleculeTableValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMoleculeTableValueItem == null || !aMoleculeTableValueItem.getName().equals("MoleculeTable")) {
            return null;
        }
        // </editor-fold>
        LinkedList<String> tmpParticleList = new LinkedList<String>();
        for (int i = 0; i < aMoleculeTableValueItem.getMatrixRowCount(); i++) {
            String tmpInputStructure = aMoleculeTableValueItem.getValue(i, 1);
            // Old code:
            // JobUtils.spices.setInputStructure(tmpInputStructure);
            // String[] tmpParticles = JobUtils.spices.getParticles();
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpInputStructure);
            String[] tmpParticles = tmpSpices.getParticles();
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            if (tmpParticles != null) {
                for (String tmpSingleParticle : tmpParticles) {
                    if (!tmpParticleList.contains(tmpSingleParticle)) {
                        tmpParticleList.addLast(tmpSingleParticle);
                    }
                }
            }
        }
        if (tmpParticleList.size() == 0) {
            return null;
        } else {
            return tmpParticleList.toArray(new String[0]);
        }
    }
    // </editor-fold>
    // </editor-fold>
    
}
