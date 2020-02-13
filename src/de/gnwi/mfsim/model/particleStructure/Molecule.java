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
package de.gnwi.mfsim.model.particleStructure;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.spices.ParticleFrequency;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import java.util.Collection;

/**
 * Molecule
 *
 * @author Achim Zielesny
 */
public class Molecule {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Molar weight of molecule
     */
    private double molarWeight;
    /**
     * Name of molecule
     */
    private String moleculeName;
    /**
     * Particle frequencies
     */
    private Collection<ParticleFrequency> particleFrequencies;
    /**
     * Molecular structure string
     */
    private String molecularStructureString;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor. NOTE: Molecular structure MUST be valid (NO checks are
     * performed) and is NOT allowed to contain monomer shortcuts.
     *
     * @param aMolecularStructureString Molecular structure string (NOTE:
     * Molecular structure MUST be valid (NO checks are performed) and is NOT
     * allowed to contain monomer shortcuts)
     * @param aMoleculeName Name of molecule
     * @throws IllegalArgumentException Thrown if aMoleculeName is null/empty
     */
    public Molecule(String aMolecularStructureString, String aMoleculeName) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        // NO CHECKS of molecular structure are performed for performance reasons
        if (aMoleculeName == null || aMoleculeName.isEmpty()) {
            throw new IllegalArgumentException("Name of structure is not defined.");
        }
        // </editor-fold>
        this.molecularStructureString = aMolecularStructureString;
        SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aMolecularStructureString);
        this.particleFrequencies = tmpSpices.getParticleToFrequencyMap().values();
        SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        this.moleculeName = aMoleculeName;
        this.molarWeight = -1.0;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Calculates molar weight with information of supplied particle container
     *
     * @param aParticleInfoContainer Particle info container
     * @return True: Calculation successful, false: Otherwise
     */
    public boolean calculateMolarWeight(ParticleInfoContainer aParticleInfoContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleInfoContainer == null || aParticleInfoContainer.getSize() == 0) {
            return false;
        }
        // </editor-fold>
        try {
            this.molarWeight = 0;
            for (ParticleFrequency tmpSingleParticleFrequency : this.particleFrequencies) {
                double tmpMolarWeightOfParticle = aParticleInfoContainer.getParticleInfo(tmpSingleParticleFrequency.getParticle()).getMolarWeight();
                this.molarWeight += tmpMolarWeightOfParticle * tmpSingleParticleFrequency.getFrequency();
            }
            return true;
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            this.molarWeight = -1.0;
            return false;
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    // <editor-fold defaultstate="collapsed" desc="MolarWeight (get)">
    /**
     * Molar weight
     *
     * @return Molar weight
     * @throws IllegalStateException Thrown if molar weight has not been
     * calculated
     */
    public double getMolarWeight() {
        if (this.molarWeight == -1.0) {
            throw new IllegalStateException("Molar weight has not been calculated.");
        } else {
            return this.molarWeight;
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="MolecularStructureString (get)">
    /**
     * Molecular structure string
     *
     * @return Molecular structure string
     */
    public String getMolecularStructureString() {
        return this.molecularStructureString;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Name (get)">
    /**
     * Name of molecule
     *
     * @return Name of molecule
     */
    public String getName() {
        return this.moleculeName;
    }
    // </editor-fold>
    // </editor-fold>
}
