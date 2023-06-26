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
package de.gnwi.mfsim.model.particleStructure;

import de.gnwi.spices.ParticleFrequency;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import java.util.HashMap;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Container for Molecule instances and calculations
 *
 * @author Achim Zielesny
 */
public class MoleculeContainer {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Box volume
     */
    private double boxVolume;
    /**
     * Complete particle container
     */
    private ParticleInfoContainer completeParticleInfoContainer;
    /**
     * Particle container with all particles of current molecules
     */
    private ParticleInfoContainer currentParticleInfoContainer;
    /**
     * HashMap that maps name of molecule to corresponding MoleculeInfo
     */
    private HashMap<String, MoleculeInfo> currentMoleculeNameToInfoMap;
    /**
     * Concentration type
     */
    private MoleculeConcentrationType concentrationType;
    /**
     * DPD density
     */
    private double dpdDensity;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCompleteParticleInfoContainer Container with complete particles
     * @param aBoxVolume Box volume
     * @param aDpdDensity DPD density
     * @param aConcentrationType Concentration type
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public MoleculeContainer(ParticleInfoContainer aCompleteParticleInfoContainer, double aBoxVolume, double aDpdDensity,
            MoleculeConcentrationType aConcentrationType) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCompleteParticleInfoContainer == null || aCompleteParticleInfoContainer.getSize() == 0 || aBoxVolume <= 0 || aDpdDensity <= 0) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        this.currentMoleculeNameToInfoMap = new HashMap<String, MoleculeInfo>(ModelDefinitions.DEFAULT_NUMBER_OF_MOLECULES);
        this.completeParticleInfoContainer = aCompleteParticleInfoContainer;
        this.boxVolume = aBoxVolume;
        this.dpdDensity = aDpdDensity;
        this.concentrationType = aConcentrationType;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Adds molecule and concentration
     *
     * @param aMolecule Molecule
     * @param aConcentration Concentration of molecule (according to
     * this.concentrationType)
     * @return True: Container changed due to add-operation, false: Otherwise
     */
    public boolean addMolecule(Molecule aMolecule, double aConcentration) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.completeParticleInfoContainer.checkMolecule(aMolecule)) {
            return false;
        }
        // </editor-fold>
        MoleculeInfo tmpMoleculeInfo = new MoleculeInfo(aMolecule);
        switch (this.concentrationType) {
            case MOLAR_PERCENT:
                // NOTE: Transform percent into fraction
                tmpMoleculeInfo.setMolarFraction(aConcentration / 100.0);
                break;
            case MOL:
                tmpMoleculeInfo.setMol(aConcentration);
                break;
            case WEIGHT_PERCENT:
                // NOTE: Transform percent into fraction
                tmpMoleculeInfo.setWeightFraction(aConcentration / 100.0);
                break;
            case GRAM:
                tmpMoleculeInfo.setGram(aConcentration);
                break;
            default:
                return false;
        }
        if (!tmpMoleculeInfo.getMolecule().calculateMolarWeight(this.completeParticleInfoContainer)) {
            return false;
        }
        this.currentMoleculeNameToInfoMap.put(aMolecule.getName(), tmpMoleculeInfo);
        return true;
    }

    /**
     * Calculates concentration properties
     *
     * @return True: Calculation successful, false: Calculation failed
     */
    public boolean calculateConcentrationProperties() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.currentMoleculeNameToInfoMap.size() == 0) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate molar fraction of molecules if necessary">
        switch (this.concentrationType) {
            case MOL:
                this.transformMolToMolarFraction();
                break;
            case WEIGHT_PERCENT:
                this.transformWeightFractionToMolarFraction();
                break;
            case GRAM:
                this.transformGramToMolarFraction();
                break;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Get particles of current molecules">
        this.currentParticleInfoContainer = new ParticleInfoContainer();
        for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpSingleMoleculeInfo.getMolecule().getMolecularStructureString());
            ParticleFrequency[] tmpParticleFrequencies = tmpSpices.getParticleFrequencies();
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            for (ParticleFrequency tmpSingleParticleFrequency : tmpParticleFrequencies) {
                if (!this.completeParticleInfoContainer.hasParticle(tmpSingleParticleFrequency.getParticle())) {
                    return false;
                } else if (!this.currentParticleInfoContainer.hasParticle(tmpSingleParticleFrequency.getParticle())) {
                    this.currentParticleInfoContainer.addParticleInfo(this.completeParticleInfoContainer.getParticleInfo(tmpSingleParticleFrequency.getParticle()));
                }
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate scaling factors of particles">
        if (!this.currentParticleInfoContainer.setScalingFactors()) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate scaling factors of molecules">
        if (Preferences.getInstance().isVolumeScalingForConcentrationCalculation()) {
            for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
                SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpSingleMoleculeInfo.getMolecule().getMolecularStructureString());
                ParticleFrequency[] tmpParticleFrequencies = tmpSpices.getParticleFrequencies();
                double tmpSum = 0;
                for (ParticleFrequency tmpSingleParticleFrequency : tmpParticleFrequencies) {
                    tmpSum += (double) tmpSingleParticleFrequency.getFrequency() * this.currentParticleInfoContainer.getParticleInfo(tmpSingleParticleFrequency.getParticle()).getScalingFactor();
                }
                tmpSingleMoleculeInfo.setScalingFactor(tmpSum / (double) tmpSpices.getTotalNumberOfParticles());
                SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
            }
        } else {
            // No volume scaling, set scaling factors to 1.0
            for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
                tmpSingleMoleculeInfo.setScalingFactor(1.0);
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate scaled molar fractions of molecules">
        // NOTE: Scaling factors are 1.0, scaled molar fraction is the molar fraction
        double tmpSumOfScaledMolarFractions = 0;
        for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
            tmpSumOfScaledMolarFractions += tmpSingleMoleculeInfo.getMolarFraction() * tmpSingleMoleculeInfo.getScalingFactor();
        }
        for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
            tmpSingleMoleculeInfo.setScaledMolarFraction(tmpSingleMoleculeInfo.getMolarFraction() * tmpSingleMoleculeInfo.getScalingFactor()
                    / tmpSumOfScaledMolarFractions);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate discrete number of molecules in the box">
        // NOTE: Scaling factors are 1.0, scaled molar fraction is the molar fraction
        double tmpTotalNumberOfParticles = this.dpdDensity * this.boxVolume;
        MoleculeInfo[] tmpMoleculeInfoArray = this.currentMoleculeNameToInfoMap.values().toArray(new MoleculeInfo[0]);
        double tmpSum = 0;
        for (int i = 1; i < tmpMoleculeInfoArray.length; i++) {
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMoleculeInfoArray[i].getMolecule().getMolecularStructureString());
            tmpSum += (double) tmpSpices.getTotalNumberOfParticles() * tmpMoleculeInfoArray[i].getScaledMolarFraction() / tmpMoleculeInfoArray[0].getScaledMolarFraction();
            SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        }
        SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(tmpMoleculeInfoArray[0].getMolecule().getMolecularStructureString());
        double tmpNumberOfMolecules = tmpTotalNumberOfParticles / ((double) tmpSpices.getTotalNumberOfParticles() + tmpSum);
        SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        tmpMoleculeInfoArray[0].setNumberOfMolecules((int) Math.floor(tmpNumberOfMolecules));
        for (int i = 1; i < tmpMoleculeInfoArray.length; i++) {
            tmpMoleculeInfoArray[i].setNumberOfMolecules((int) Math.floor(tmpNumberOfMolecules * tmpMoleculeInfoArray[i].getScaledMolarFraction() / tmpMoleculeInfoArray[0].getScaledMolarFraction()));
        }
        // </editor-fold>
        return true;
    }

    /**
     * Returns molecule information for molecule with specified name
     *
     * @param aName Name of molecule
     * @return Molecule information for molecule with specified name or null if
     * molecule with specified name was not found
     */
    public MoleculeInfo getMoleculeInfo(String aName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aName == null || aName.isEmpty()) {
            return null;
        }

        // </editor-fold>
        return this.currentMoleculeNameToInfoMap.get(aName);
    }

    /**
     * Returns molecule information
     *
     * @return Molecule information or null if none exists
     */
    public MoleculeInfo[] getCompleteMoleculeInfo() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.currentMoleculeNameToInfoMap.size() == 0) {
            return null;
        }

        // </editor-fold>
        return this.currentMoleculeNameToInfoMap.values().toArray(new MoleculeInfo[0]);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns size of container
     *
     * @return Size of container
     */
    public int getSize() {
        return this.currentMoleculeNameToInfoMap.size();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Transforms gram to molar fraction
     */
    private void transformGramToMolarFraction() {
        double tmpSum = 0;
        for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
            tmpSum += tmpSingleMoleculeInfo.getGram();
        }
        for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
            tmpSingleMoleculeInfo.setWeightFraction(tmpSingleMoleculeInfo.getGram() / tmpSum);
        }
        this.transformWeightFractionToMolarFraction();
    }

    /**
     * Transforms mol per litre to molar fraction
     */
    private void transformMolToMolarFraction() {
        double tmpSum = 0;
        for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
            tmpSum += tmpSingleMoleculeInfo.getMol();
        }
        for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
            tmpSingleMoleculeInfo.setMolarFraction(tmpSingleMoleculeInfo.getMol() / tmpSum);
        }
    }

    /**
     * Transforms weight fraction to molar fraction
     */
    private void transformWeightFractionToMolarFraction() {
        double tmpDenominator = 0;
        for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
            tmpDenominator += tmpSingleMoleculeInfo.getWeightFraction() / tmpSingleMoleculeInfo.getMolecule().getMolarWeight();
        }
        for (MoleculeInfo tmpSingleMoleculeInfo : this.currentMoleculeNameToInfoMap.values()) {
            tmpSingleMoleculeInfo.setMolarFraction(
                (tmpSingleMoleculeInfo.getWeightFraction() / tmpSingleMoleculeInfo.getMolecule().getMolarWeight()) / tmpDenominator
            );
        }
    }
    // </editor-fold>
}
