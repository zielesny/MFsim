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

import de.gnwi.spices.SpicesConstants;
import de.gnwi.spices.ParticleFrequency;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import java.util.HashMap;

/**
 * Container for ParticleInfo intances
 *
 * @author Achim Zielesny
 */
public class ParticleInfoContainer {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Particle name to particle information map
     */
    private HashMap<String, ParticleInfo> particleNameToInfoMap;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public ParticleInfoContainer() {
        this.particleNameToInfoMap = new HashMap<String, ParticleInfo>(SpicesConstants.DEFAULT_NUMBER_OF_PARTICLES);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Adds particle information
     *
     * @param aParticleInfo Particle information
     * @return True: Container changed due to add-operation, false: Otherwise
     */
    public boolean addParticleInfo(ParticleInfo aParticleInfo) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleInfo == null || this.particleNameToInfoMap.containsKey(aParticleInfo.getParticle())) {
            return false;
        }

        // </editor-fold>
        this.particleNameToInfoMap.put(aParticleInfo.getParticle(), aParticleInfo);
        return true;
    }

    /**
     * Adds array of particle information
     *
     * @param aParticleInfoArray Array of particle information
     * @return True: Container changed due to add-operation, false: Otherwise
     */
    public boolean addParticleInfo(ParticleInfo[] aParticleInfoArray) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleInfoArray == null || aParticleInfoArray.length == 0) {
            return false;
        }

        // </editor-fold>
        boolean tmpHasChanged = false;
        for (ParticleInfo tmpSingleParticleInfo : aParticleInfoArray) {
            if (this.addParticleInfo(tmpSingleParticleInfo)) {
                tmpHasChanged = true;
            }
        }
        return tmpHasChanged;
    }

    /**
     * Checks if container contains all particles of molecule
     *
     * @param aMolecule Molecule
     * @return True: Container contains all particles of molecule, false:
     * Otherwise
     */
    public boolean checkMolecule(Molecule aMolecule) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMolecule == null) {
            return false;
        }
        // </editor-fold>
        SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aMolecule.getMolecularStructureString());
        ParticleFrequency[] tmpParticleFrequencies = tmpSpices.getParticleFrequencies();
        SpicesPool.getInstance().setSpicesForReuse(tmpSpices);
        if (tmpParticleFrequencies == null) {
            return false;
        }
        for (ParticleFrequency tmpSingleParticleFrequency : tmpParticleFrequencies) {
            if (!this.particleNameToInfoMap.containsKey(tmpSingleParticleFrequency.getParticle())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns particle information for particle with specified name
     *
     * @param aParticleName Particle name
     * @return Particle information for particle with specified name or null if
     * particle was not found
     */
    public ParticleInfo getParticleInfo(String aParticleName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleName == null || aParticleName.isEmpty()) {
            return null;
        }

        // </editor-fold>
        return this.particleNameToInfoMap.get(aParticleName);
    }

    /**
     * Returns if container has particle with specified name
     *
     * @param aParticleName Particle name
     * @return True: Container has particle with specified name, false:
     * Otherwise
     */
    public boolean hasParticle(String aParticleName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleName == null || aParticleName.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.particleNameToInfoMap.containsKey(aParticleName);
    }

    /**
     * Removes particle with specified name from container
     *
     * @param aParticleName Particle name
     * @return True: Particle with specified name was removed, false: Otherwise
     */
    public boolean removeParticle(String aParticleName) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleName == null || aParticleName.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.particleNameToInfoMap.remove(aParticleName) != null;
    }

    /**
     * Sets scaling factor of each particle information of container
     *
     * @return True: Scaling factors could successfully be set, false: Otherwise
     */
    public boolean setScalingFactors() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.particleNameToInfoMap.size() == 0) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine minimum volume">
        double tmpMinimum = Double.MAX_VALUE;
        for (ParticleInfo tmpSingleParticleInfo : this.particleNameToInfoMap.values()) {
            if (tmpMinimum > tmpSingleParticleInfo.getVolume()) {
                tmpMinimum = tmpSingleParticleInfo.getVolume();
            }
        }
        if (tmpMinimum <= 0) {
            return false;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set scaling factors">
        for (ParticleInfo tmpSingleParticleInfo : this.particleNameToInfoMap.values()) {
            tmpSingleParticleInfo.setScalingFactor(tmpSingleParticleInfo.getVolume() / tmpMinimum);
        }

        // </editor-fold>
        return true;
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
        return this.particleNameToInfoMap.size();
    }

	// </editor-fold>
}
