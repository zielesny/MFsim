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
package de.gnwi.mfsim.model.particleStructure;
/**
 * Information about molecule
 *
 * @author Achim Zielesny
 */
public class MoleculeInfo {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Molecule
     */
    private Molecule molecule;
    /**
     * Gram
     */
    private double gram;
    /**
     * Molar fraction
     */
    private double molarFraction;
    /**
     * Mol
     */
    private double mol;
    /**
     * Number of molecules
     */
    private int numberOfMolecules;
    /**
     * Scaled molar percent
     */
    private double scaledMolarFraction;
    /**
     * Weight fraction
     */
    private double weightFraction;
    /**
     * Scaling factor
     */
    private double scalingFactor;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aMolecule Molecule
     * @throws IllegalArgumentException Thrown if molecule is not defined
     */
    public MoleculeInfo(Molecule aMolecule) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMolecule == null) {
            throw new IllegalArgumentException("Molecule is not defined.");
        }
        // </editor-fold>
        this.molecule = aMolecule;
        this.molarFraction = -1.0;
        this.gram = -1.0;
        this.numberOfMolecules = -1;
        this.scaledMolarFraction = -1.0;
        this.weightFraction = -1.0;
        this.scalingFactor = -1.0;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    // <editor-fold defaultstate="collapsed" desc="- Molecule (get)">
    /**
     * Molecule
     *
     * @return Molecule
     */
    public Molecule getMolecule() {
        return this.molecule;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Gram (get/set)">
    /**
     * Gram
     *
     * @return Gram
     */
    public double getGram() {
        return this.gram;
    }

    /**
     * Gram
     *
     * @param aGram Gram
     */
    public void setGram(double aGram) {
        this.gram = aGram;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- MolarFraction (get/set)">
    /**
     * Molar fraction
     *
     * @return Molar fraction
     */
    public double getMolarFraction() {
        return this.molarFraction;
    }

    /**
     * Molar fraction
     *
     * @param aMolarFraction Molar fraction
     */
    public void setMolarFraction(double aMolarFraction) {
        this.molarFraction = aMolarFraction;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Mol (get/set)">
    /**
     * Mol
     *
     * @return Mol
     */
    public double getMol() {
        return this.mol;
    }

    /**
     * Mol
     *
     * @param aMol Mol
     */
    public void setMol(double aMol) {
        this.mol = aMol;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- NumberOfMolecules (get/set)">
    /**
     * Number of molecules
     *
     * @return Number of molecules
     */
    public int getNumberOfMolecules() {
        return this.numberOfMolecules;
    }

    /**
     * Number of molecules
     *
     * @param aNumberOfMolecules Number of molecules
     */
    public void setNumberOfMolecules(int aNumberOfMolecules) {
        this.numberOfMolecules = aNumberOfMolecules;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- ScaledMolarFraction (get/set)">
    /**
     * Scaled molar percent
     *
     * @return Scaled molar percent
     */
    public double getScaledMolarFraction() {
        return this.scaledMolarFraction;
    }

    /**
     * Scaled molar percent
     *
     * @param aScaledMolarFraction Scaled molar percent
     */
    public void setScaledMolarFraction(double aScaledMolarFraction) {
        this.scaledMolarFraction = aScaledMolarFraction;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- WeightFraction (get/set)">
    /**
     * Weight fraction
     *
     * @return Weight fraction
     */
    public double getWeightFraction() {
        return this.weightFraction;
    }

    /**
     * Weight fraction
     *
     * @param aWeightFraction Weight fraction
     */
    public void setWeightFraction(double aWeightFraction) {
        this.weightFraction = aWeightFraction;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- ScalingFactor (get/set)">
    /**
     * Scaling factor
     *
     * @return Scaling factor
     */
    public double getScalingFactor() {
        return scalingFactor;
    }

    /**
     * Scaling factor
     *
     * @param aScalingFactor Scaling factor
     */
    public void setScalingFactor(double aScalingFactor) {
        this.scalingFactor = aScalingFactor;
    }
    // </editor-fold>
    // </editor-fold>
    
}
