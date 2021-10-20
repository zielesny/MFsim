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
package de.gnwi.mfsim.model.graphics;

import de.gnwi.mfsim.model.util.StandardColorEnum;
import java.util.Arrays;
import java.util.HashMap;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Molecule selection manager
 * 
 * @author Achim Zielesny
 */
public class MoleculeSelectionManager {
    
    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Map of particle index to selected molecule
     */
    private final HashMap<Integer, SelectedMolecule> particleIndexToSelectedMoleculeMap;

    /**
     * Map of particle index to color
     */
    private final HashMap<Integer, StandardColorEnum> particleIndexToColorMap;

    /**
     * Map of particle index to transparency
     */
    private final HashMap<Integer, Float> particleIndexToTransparencyMap;

    /**
     * Map of particle index to color usage
     */
    private final HashMap<Integer, Boolean> particleIndexToDisplayUsageMap;

    /**
     * Map of molecule index name to selected molecule
     */
    private final HashMap<String, SelectedMolecule> moleculeIndexNameToSelectedMoleculeMap;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Entry index
     */
    private long entryIndex;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public MoleculeSelectionManager(
    ) {
        this.moleculeIndexNameToSelectedMoleculeMap = new HashMap<>(ModelDefinitions.DEFEAULT_NUMBER_OF_SELECTED_MOLECULES);
        this.particleIndexToSelectedMoleculeMap = new HashMap<>(ModelDefinitions.DEFEAULT_NUMBER_OF_SELECTED_PARTICLES);
        this.particleIndexToColorMap = new HashMap<>(ModelDefinitions.DEFEAULT_NUMBER_OF_SELECTED_PARTICLES);
        this.particleIndexToTransparencyMap = new HashMap<>(ModelDefinitions.DEFEAULT_NUMBER_OF_SELECTED_PARTICLES);
        this.particleIndexToDisplayUsageMap = new HashMap<>(ModelDefinitions.DEFEAULT_NUMBER_OF_SELECTED_PARTICLES);
        this.entryIndex = 0L;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns if particle with specified index is selected.
     * (No checks are performed)
     * 
     * @param aParticleIndex Particle index
     * @return True: Particle is selected, false: Otherwise
     */
    public boolean isParticleSelected(int aParticleIndex) {
        return this.particleIndexToSelectedMoleculeMap.containsKey(aParticleIndex);
    }

    /**
     * Returns if at least one molecule is selected
     * 
     * @return True: At least one molecule is selected, false: Otherwise
     */
    public boolean isMoleculeSelected() {
        return !this.moleculeIndexNameToSelectedMoleculeMap.isEmpty();
    }

    /**
     * Returns if molecule of particle with specified index is to be displayed
     * with its selection color and transparency.
     * (No checks are performed)
     * 
     * @param aParticleIndex Particle index
     * @return True: Display with selection color and transparency, false: 
     * Otherwise
     */
    public boolean isMoleculeSelectionDisplayUsage(int aParticleIndex) {
        if (this.isParticleSelected(aParticleIndex)) {
            return this.particleIndexToDisplayUsageMap.get(aParticleIndex);
        } else {
            return false;
        }
    }

    /**
     * Returns selected molecule color.
     * (No checks are performed)
     * 
     * @param aParticleIndex Particle index
     * @return Selected molecule color or null if none is specified
     */
    public StandardColorEnum getSelectedMoleculeColor(int aParticleIndex) {
        if (this.isParticleSelected(aParticleIndex)) {
            return this.particleIndexToColorMap.get(aParticleIndex);
        } else {
            return null;
        }
    }

    /**
     * Returns selected molecule transparency.
     * (No checks are performed)
     * 
     * @param aParticleIndex Particle index
     * @return Selected molecule transparency or -1 if none is specified
     */
    public float getSelectedMoleculeTransparency(int aParticleIndex) {
        if (this.isParticleSelected(aParticleIndex)) {
            return this.particleIndexToTransparencyMap.get(aParticleIndex);
        } else {
            return -1.0f;
        }
    }

    /**
     * Returns selected molecule name.
     * (No checks are performed)
     * 
     * @param aParticleIndex Particle index
     * @return Selected molecule name or null if none is specified
     */
    public String getSelectedMoleculeName(int aParticleIndex) {
        if (this.isParticleSelected(aParticleIndex)) {
            SelectedMolecule tmpSelectedMolecule = this.particleIndexToSelectedMoleculeMap.get(aParticleIndex);
            return tmpSelectedMolecule.getMoleculeName();
        } else {
            return null;
        }
    }

    /**
     * Returns selected molecule index.
     * (No checks are performed)
     * 
     * @param aParticleIndex Particle index
     * @return Selected molecule index or -1 if none is specified
     */
    public int getSelectedMoleculeIndex(int aParticleIndex) {
        if (this.isParticleSelected(aParticleIndex)) {
            SelectedMolecule tmpSelectedMolecule = this.particleIndexToSelectedMoleculeMap.get(aParticleIndex);
            return tmpSelectedMolecule.getMoleculeIndex();
        } else {
            return -1;
        }
    }
    
    /**
     * Adds selected molecule.
     * (No checks are performed)
     * 
     * @param aSelectedMolecule Selected molecule
     * @return True: Selected molecule was added, false: Otherwise (nothing changed)
     */
    public boolean addSelectedMolecule(SelectedMolecule aSelectedMolecule) {
        if (this.isParticleSelected(aSelectedMolecule.getMinParticleIndex())) {
            return false;
        } else {
            aSelectedMolecule.setEntryIndex(this.entryIndex++);
            String tmpMoleculeIndexName = this.getMoleculeIndexName(aSelectedMolecule.getMoleculeName(), aSelectedMolecule.getMoleculeIndex());
            this.moleculeIndexNameToSelectedMoleculeMap.put(tmpMoleculeIndexName, aSelectedMolecule);
            for (int i = aSelectedMolecule.getMinParticleIndex(); i < aSelectedMolecule.getExclusiveMaxParticleIndex(); i++) {
                this.particleIndexToSelectedMoleculeMap.put(i, aSelectedMolecule);
                this.particleIndexToDisplayUsageMap.put(i, aSelectedMolecule.isSelectionColorUsage());
                this.particleIndexToColorMap.put(i, aSelectedMolecule.getSelectionColor());
                this.particleIndexToTransparencyMap.put(i, aSelectedMolecule.getSelectionTransparency());
            }
            return true;
        }
    }

    /**
     * Removes selected molecule which contains particle index
     * (No checks are performed)
     * 
     * @param aParticleIndex Particle index
     * @return True: Selected molecule was removed, false: Otherwise (nothing changed)
     */
    public boolean removeSelectedMolecule(int aParticleIndex) {
        if (this.particleIndexToSelectedMoleculeMap.containsKey(aParticleIndex)) {
            SelectedMolecule tmpSelectedMolecule = this.particleIndexToSelectedMoleculeMap.get(aParticleIndex);
            return this.removeSelectedMolecule(tmpSelectedMolecule.getMoleculeName(), tmpSelectedMolecule.getMoleculeIndex());
        } else {
            return false;
        }
    }

    /**
     * Removes selected molecule
     * (No checks are performed)
     * 
     * @param aSelectedMoleculeName Selected molecule name
     * @param aSelectedMoleculeIndex Selected molecule index
     * @return True: Selected molecule was removed, false: Otherwise (nothing changed)
     */
    public boolean removeSelectedMolecule(String aSelectedMoleculeName, int aSelectedMoleculeIndex) {
        String tmpMoleculeIndexName = this.getMoleculeIndexName(aSelectedMoleculeName, aSelectedMoleculeIndex);
        if (this.moleculeIndexNameToSelectedMoleculeMap.containsKey(tmpMoleculeIndexName)) {
            SelectedMolecule tmpSelectedMolecule = this.moleculeIndexNameToSelectedMoleculeMap.remove(tmpMoleculeIndexName);
            for (int i = tmpSelectedMolecule.getMinParticleIndex(); i < tmpSelectedMolecule.getExclusiveMaxParticleIndex(); i++) {
                this.particleIndexToSelectedMoleculeMap.remove(i);
                this.particleIndexToColorMap.remove(i);
                this.particleIndexToTransparencyMap.remove(i);
                this.particleIndexToDisplayUsageMap.remove(i);
            }
            if (this.moleculeIndexNameToSelectedMoleculeMap.isEmpty()) {
                this.entryIndex = 0L;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns selected molecules sorted according to entry index
     * 
     * @return Sorted selected molecules or null if none are available
     */
    public SelectedMolecule[] getEntryIndexSortedSelectedMolecules() {
       if (this.moleculeIndexNameToSelectedMoleculeMap.isEmpty()) {
           return null;
       } else {
           SelectedMolecule[] tmpSelectedMolecules = this.moleculeIndexNameToSelectedMoleculeMap.values().toArray(new SelectedMolecule[0]);
           Arrays.sort(tmpSelectedMolecules);
           return tmpSelectedMolecules;
       }
    }
    
    /**
     * Clears molecule selection
     */
    public void clear() {
        this.moleculeIndexNameToSelectedMoleculeMap.clear();
        this.particleIndexToSelectedMoleculeMap.clear();
        this.particleIndexToColorMap.clear();
        this.particleIndexToTransparencyMap.clear();
        this.particleIndexToDisplayUsageMap.clear();
        this.entryIndex = 0L;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get)">
    /**
     * Number of selected molecules
     * 
     * @return Number of selected molecules
     */
    public int getNumberOfSelectedMolecules() {
        return this.moleculeIndexNameToSelectedMoleculeMap.size();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Return molecule index name
     * (No checks are performed)
     * 
     * @param aMoleculeName Molecule name
     * @param aMoleculeIndex Molecule index
     * @return Molecule index name
     */
    private String getMoleculeIndexName(String aMoleculeName, int aMoleculeIndex) {
        return aMoleculeName + ModelDefinitions.GENERAL_SEPARATOR + String.valueOf(aMoleculeIndex);
    }
    // </editor-fold>
    
}
